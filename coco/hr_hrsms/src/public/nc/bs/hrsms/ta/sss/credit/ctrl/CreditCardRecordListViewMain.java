package nc.bs.hrsms.ta.sss.credit.ctrl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import nc.bs.dao.BaseDAO;
import nc.bs.dbcache.intf.IDBCacheBS;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.credit.CreditCardRecordConsts;
import nc.bs.hrsms.ta.sss.credit.common.CreditCardRecordExcelExportUtils;
import nc.bs.hrsms.ta.sss.credit.common.CreditCardUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.TextNavigationUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.ta.ICheckTimeQueryService;
import nc.itf.ta.algorithm.ICheckTime;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.uap.ctrl.tpl.qry.FromWhereSQLImpl;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hrsms.ta.sss.shop.ShopQryConditionVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class CreditCardRecordListViewMain implements IController {

	private static final int MAX_QUERY_DAYS = 60;
	/** 参数--人员主键 */
	public static final String PARAM_CI_PK_PSNDOC = "ci_pk_psndoc";
	/** 参数--年份 */
	public static final String PARAM_CI_YEAR = "ci_year";
	/** 参数--月份 */
	public static final String PARAM_CI_MONTH = "ci_month";
	/** 参数：人员的PsnJobCalendarVO */
	public static final String PARAM_PSNJOB_CALENDARVO = "ci_PsnJobCalendarVO";

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	public void onDataLoad_dsCardInfo(DataLoadEvent dataLoadEvent) throws BusinessException {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 管理部门变更
	 * 
	 * @param keys
	 * @throws BusinessException
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
		LfwView simpQryView = getLifeCycleContext().getWindowContext()
				.getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			// 管理部门所在的HR组织
			String pk_hr_org = SessionUtil.getHROrg();
			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
			if (dsSearch.nameToIndex(CreditCardRecordConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(dsSearch.nameToIndex(CreditCardRecordConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (dsSearch.nameToIndex(CreditCardRecordConsts.FD_ENDDATE) > -1) {
				// 结束日期
				selRow.setValue(dsSearch.nameToIndex(CreditCardRecordConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}

			// 清空保存的查询条件
			SessionUtil.getSessionBean().setExtendAttribute(CreditCardRecordConsts.SESSION_QRY_CONDITIONS, null);
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	/**
	 * 点击搜索后,页面重新加载事件
	 * 
	 * @param keys
	 * @throws BusinessException
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		if (keys == null || keys.size() == 0) {
			return;
		}
		// 是否包含下级部门
		boolean isContainSub = SessionUtil.isIncludeSubDept();
		// 查询条件-部门
		String pk_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_dept)) {
			return;
		}
		ShopQryConditionVO vo = this.getConditions(keys);
		String psnName = vo.getPsnName();
		String pk_psndoc = this.getPk_psndocByName(psnName);
		if (StringUtils.isEmpty(pk_psndoc)) {
			pk_psndoc = loadDeptPsns(pk_dept, isContainSub, vo.getFromWhereSQL(), vo.getBeginDate(), vo.getEndDate());
		}
		// 加载页面人员显示部分
		ApplicationContext appCxt = AppUtil.getCntAppCtx();
		// 设置前台显示的人员主键
		appCxt.getClientSession().setAttribute(PARAM_CI_PK_PSNDOC, pk_psndoc);
		if (StringUtils.isEmpty(pk_psndoc)) {
			// 清除人员的导航数据
			appCxt.addExecScript("destroyTextNavigation();");
			// 清空日历中的班次信息
			LfwView view = ViewUtil.getCurrentView();
			Dataset dsCardInfo = view.getViewModels().getDataset(CreditCardRecordConsts.MAIN_DS_CARDINFO);
			DatasetUtil.clearData(dsCardInfo);
			appCxt.addExecScript("loadCardInfo(" + vo.getBeginDate().getYear() + ","
					+ String.valueOf(vo.getBeginDate().getMonth() - 1) + ");");
		} else {
			appCxt.addExecScript("loadCardInfo(" + vo.getBeginDate().getYear() + ","
					+ String.valueOf(vo.getBeginDate().getMonth() - 1) + ");");
			appCxt.addExecScript("queryByName('" + pk_psndoc + "');");
			loadCardInfo(String.valueOf(vo.getBeginDate().getYear()), String.valueOf(vo.getBeginDate().getMonth() - 1),
					pk_psndoc);
		}
	}

	/**
	 * 加载页面人员显示部分
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String loadDeptPsns(String pk_dept, boolean isContainSub, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) {

		// QueryScopeEnum queryScope =
		// CalendarUtils.getArrangeFlagScope(arrangeflag);
		// String pk_psndoc = "";
		// PsndocVO[] psndocVOs = getPsndocVOsByCond(pk_dept, isContainSub,
		// fromWhereSQL, beginDate, endDate, queryScope);

		String pk_psndoc = "";
		String pk_group = SessionUtil.getPk_group();
		String pk_org = SessionUtil.getPk_org();
		nc.vo.bd.psn.PsndocVO[] psnvos = getPsnDocVOs(pk_dept, isContainSub, pk_group, pk_org);
		/*
		 * if(!isContainSub){ psnvos =
		 * NCLocator.getInstance().lookup(IPsndocQueryService
		 * .class).queryAllPsndocvosByGroupOrOrgOrDept(pk_group, pk_org,
		 * pk_dept); }else{ StringBuffer condition = new StringBuffer();
		 * StringBuffer cond = new StringBuffer(); HRDeptVO deptVO = (HRDeptVO)
		 * new BaseDAO().retrieveByPK(HRDeptVO.class, pk_dept);
		 * cond.append(" 1=1 and ismainjob='Y'"); if(pk_group != null)
		 * cond.append(" and pk_group = '"+pk_group+"' "); if(pk_org != null)
		 * cond.append(" and pk_org = '"+pk_org+"'"); cond.append(
		 * " and pk_dept in(select dept.pk_dept from org_dept dept where dept.innercode like '%"
		 * +deptVO.getInnercode()+"%') ");
		 * condition.append(" pk_psndoc in (select pk_psndoc from bd_psnjob where "
		 * +cond.toString()+") order by code desc"); psnvos =
		 * NCLocator.getInstance
		 * ().lookup(IPsndocQueryService.class).queryPsndocVOsByCondition
		 * (condition.toString()); }
		 */
		if (ArrayUtils.isEmpty(psnvos)) {
			return null;
		}
		// 获取人员姓名信息
		HashMap<String, PsndocVO> map = new HashMap<String, PsndocVO>();
		Collection<PsndocVO> psndocVOs = null;
		try {
			psndocVOs = new BaseDAO().retrieveByClause(PsndocVO.class,
					"pk_psndoc in (" + new InSQLCreator().getInSQL(psnvos, PsnJobVO.PK_PSNDOC) + ")");
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		for (PsndocVO v : psndocVOs) {
			map.put(v.getPk_psndoc(), v);
		}

		if (map.size() == 0) {
			return null;
		}
		// 人员姓名导航
		StringBuffer jsonBuf = new StringBuffer("");
		for (int i = 0; i < psnvos.length; i++) {
			pk_psndoc = psnvos[i].getPk_psndoc();
			jsonBuf.append(TextNavigationUtil.buildTextNavgItemJson(i, map.get(pk_psndoc).getMultiLangName(),
					pk_psndoc, ""));
		}
		pk_psndoc = psnvos[0].getPk_psndoc();
		ApplicationContext appCxt = getLifeCycleContext().getApplicationContext();
		appCxt.addExecScript("ceateTextNavigation('{[" + jsonBuf.toString() + "]}');");
		return pk_psndoc;
	}

	/**
	 * 刷新操作
	 * 
	 * @param mouseEvent
	 */
	public void doRefresh(MouseEvent<MenuItem> mouseEvent) {
		LfwView view = ViewUtil.getCurrentView();
		Dataset ds = view.getViewModels().getDataset(CreditCardRecordConsts.MAIN_DS_CARDINFO);
		// 更新菜单状态
		ds.setEnabled(false);
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @param pk_psndoc
	 */
	public void loadCardInfo(String year, String month, String pk_psndoc) {
		// 所在日历的第一天
		UFLiteralDate firstDateOfMonth = CreditCardUtils.getFirstDateOfMonth(year, month);
		// 所在日历的最后一天
		UFLiteralDate lastDateOfMonth = CreditCardUtils.getLastDateOfMonth(year, month);

		String pk_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_dept)) {
			return;
		}

		LfwView view = ViewUtil.getCurrentView();
		Dataset dsCardInfo = view.getViewModels().getDataset(CreditCardRecordConsts.MAIN_DS_CARDINFO);
		DatasetUtil.clearData(dsCardInfo);
		try {
			ICheckTime[] checkTimes = NCLocator
					.getInstance()
					.lookup(ICheckTimeQueryService.class)
					.queryCheckTimesByPsnAndDateScope(SessionUtil.getPk_org(), pk_psndoc, firstDateOfMonth,
							lastDateOfMonth);

			// tangcht 取同一天前两条刷卡时间作为上下班时间，合成一条显示
			TreeMap<String, GeneralVO> vos = getCocoVos(checkTimes);

			if (!vos.entrySet().isEmpty()) {

				Iterator it = vos.entrySet().iterator();

				while (it.hasNext()) {

					Map.Entry<String, GeneralVO> entry = (Entry) it.next();

					GeneralVO vo = entry.getValue();

					Row row = dsCardInfo.getEmptyRow();

					row.setValue(dsCardInfo.nameToIndex("timecardid"),
							vo.getAttributeValue("timecardid") == null ? null : vo.getAttributeValue("timecardid"));
					row.setValue(dsCardInfo.nameToIndex("date"),
							vo.getAttributeValue("date") == null ? null : vo.getAttributeValue("date"));
					row.setValue(dsCardInfo.nameToIndex("begintime"), vo.getAttributeValue("begintime") == null ? null
							: vo.getAttributeValue("begintime"));
					row.setValue(dsCardInfo.nameToIndex("endtime"),
							vo.getAttributeValue("endtime") == null ? null : vo.getAttributeValue("endtime"));
					row.setValue(dsCardInfo.nameToIndex("timeflag"), vo.getAttributeValue("timeflag") == null ? null
							: vo.getAttributeValue("timeflag"));
					row.setValue(dsCardInfo.nameToIndex("checkflag"), vo.getAttributeValue("checkflag") == null ? null
							: vo.getAttributeValue("checkflag"));
					row.setValue(dsCardInfo.nameToIndex("pk_machine"),
							vo.getAttributeValue("pk_machine") == null ? null : vo.getAttributeValue("pk_machine"));
					row.setValue(dsCardInfo.nameToIndex("pk_place"), vo.getAttributeValue("pk_place") == null ? null
							: vo.getAttributeValue("pk_place"));
					row.setValue(
							dsCardInfo.nameToIndex("placeabnormal"),
							vo.getAttributeValue("placeabnormal") == null ? null : vo
									.getAttributeValue("placeabnormal"));
					row.setValue(dsCardInfo.nameToIndex("signreason"),
							vo.getAttributeValue("signreason") == null ? null : vo.getAttributeValue("signreason"));
					row.setValue(dsCardInfo.nameToIndex("creator"),
							vo.getAttributeValue("creator") == null ? null : vo.getAttributeValue("creator"));
					row.setValue(dsCardInfo.nameToIndex("creationtime"),
							vo.getAttributeValue("creationtime") == null ? null : vo.getAttributeValue("creationtime"));

					dsCardInfo.addRow(row);
				}

				dsCardInfo.setEnabled(false);
			}

			/*
			 * if(checkTimes!=null){ for(int i=0;i<checkTimes.length;i++){ Row
			 * row = dsCardInfo.getEmptyRow();
			 * if(StringUtils.isNotEmpty(checkTimes[i].getTimecardid())){
			 * row.setValue(dsCardInfo.nameToIndex("timecardid"),
			 * checkTimes[i].getTimecardid()); }
			 * row.setValue(dsCardInfo.nameToIndex("datetime"),
			 * checkTimes[i].getDatetime());
			 * row.setValue(dsCardInfo.nameToIndex("timeflag"),
			 * checkTimes[i].getTimeflag());
			 * row.setValue(dsCardInfo.nameToIndex("checkflag"),
			 * checkTimes[i].getCheckflag());
			 * row.setValue(dsCardInfo.nameToIndex("pk_machine"),
			 * checkTimes[i].getPk_machine());
			 * row.setValue(dsCardInfo.nameToIndex("pk_place"),
			 * checkTimes[i].getPk_place());
			 * row.setValue(dsCardInfo.nameToIndex("placeabnormal"),
			 * checkTimes[i].getPlaceabnormal());
			 * row.setValue(dsCardInfo.nameToIndex("placeabnormal"),
			 * checkTimes[i].getPlaceabnormal());
			 * row.setValue(dsCardInfo.nameToIndex("signreason"),
			 * checkTimes[i].getSignreason());
			 * row.setValue(dsCardInfo.nameToIndex("creator"),
			 * checkTimes[i].getCreator());
			 * row.setValue(dsCardInfo.nameToIndex("creationtime"),
			 * checkTimes[i].getCreationtime()); dsCardInfo.addRow(row); }
			 * dsCardInfo.setEnabled(false); }
			 */
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获得查询条件.
	 * 
	 * @param keys
	 * @return
	 */
	public ShopQryConditionVO getConditions(Map<String, Object> keys) {
		ViewContext leftView = AppLifeCycleContext.current().getWindowContext().getViewContext("pubview_simplequery");
		FormComp searchForm = null;
		if (leftView != null && leftView.getView() != null) {
			searchForm = (FormComp) leftView.getView().getViewComponents().getComponent("mainform");
		}
		SessionBean sess = SessionUtil.getSessionBean();
		// ShopQryConditionVO vo = (ShopQryConditionVO)
		// sess.getExtendAttributeValue(CreditCardRecordConsts.SESSION_QRY_CONDITIONS);
		// ApplicationContext appCtx =
		// AppLifeCycleContext.current().getApplicationContext();
		// if (!StringUtils.isEmpty((String)
		// appCtx.getAppAttribute(CreditCardRecordConsts.SESSION_CATAGORY_ACCESS))
		// && vo != null) {// 按时段/日历查看进入
		// // 只使用一次,需删除
		// appCtx.addAppAttribute(CreditCardRecordConsts.SESSION_CATAGORY_ACCESS,
		// null);
		// } else {// 通过菜单进入/通过查询按钮进入
		ShopQryConditionVO vo = new ShopQryConditionVO();
		FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
		nc.ui.querytemplate.querytree.FromWhereSQLImpl fromWhereSQL = (nc.ui.querytemplate.querytree.FromWhereSQLImpl) CommonUtil
				.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL));
		// 获得自定义条件
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();

		// 查询条件-开始日期
		String beginDate = selfDefMap.get(CreditCardRecordConsts.FD_BEGINDATE);
		String endDate = selfDefMap.get(CreditCardRecordConsts.FD_ENDDATE);
		String psnName = selfDefMap.get(CreditCardRecordConsts.FD_PSNNAME);
		if (StringUtils.isEmpty(beginDate)) {
			CommonUtil.showCompErrorDialog(searchForm,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0017")/*
																									 * @
																									 * res
																									 * "开始日期不能为空！"
																									 */);

		}
		// 查询条件-结束日期
		if (StringUtils.isEmpty(endDate)) {
			CommonUtil.showCompErrorDialog(searchForm,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																									 * @
																									 * res
																									 * "结束日期不能为空，请输入！"
																									 */);
		}
		if (new UFLiteralDate(beginDate).afterDate(new UFLiteralDate(endDate))) {
			CommonUtil.showCompErrorDialog(searchForm,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0019")/*
																									 * @
																									 * res
																									 * "开始日期不能晚于结束日期，请重新输入！"
																									 */);

		}
		if (new UFLiteralDate(endDate).after(new UFLiteralDate(beginDate).getDateAfter(MAX_QUERY_DAYS))) {
			CommonUtil.showCompErrorDialog(searchForm,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0139")/*
																									 * @
																									 * res
																									 * 开始日期和结束日期的间隔天数大于60天
																									 * ，
																									 * 请重新输入
																									 * ！
																									 */);

		}
		vo.setBeginDate(new UFLiteralDate(beginDate));
		vo.setEndDate(new UFLiteralDate(endDate));
		vo.setPsnName(psnName);
		String psnScopeSqlPart = null;
		try {
			psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
		} catch (BusinessException e) {
			// new HrssException(e).deal();
		}

		if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
			(fromWhereSQL)
					.setWhere(fromWhereSQL.getWhere() + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
		}
		vo.setFromWhereSQL(fromWhereSQL);
		// 记录本次查询条件
		sess.setExtendAttribute(CreditCardRecordConsts.SESSION_QRY_CONDITIONS, vo);
		// }
		return vo;
	}

	/**
	 * 人员变更事件
	 * 
	 * @param scriptEvent
	 */
	public void onPsnNameChanged(ScriptEvent scriptEvent) {
		String pk_psndoc = getLifeCycleContext().getParameter(PARAM_CI_PK_PSNDOC);
		// 设置前台显示的人员主键
		AppUtil.getCntAppCtx().getClientSession().setAttribute(PARAM_CI_PK_PSNDOC, pk_psndoc);
		// 参数-年
		String customYear = getLifeCycleContext().getParameter(PARAM_CI_YEAR);
		// 参数-月
		String customMonth = getLifeCycleContext().getParameter(PARAM_CI_MONTH);
		// 重新加载数据
		this.loadCardInfo(customYear, customMonth, pk_psndoc);
	}

	/**
	 * 根据条件查询PsndocVO[]
	 * 
	 * @param pk_dept
	 * @param isContainSub
	 * @param pk_group
	 * @param pk_org
	 * @return
	 */
	public nc.vo.bd.psn.PsndocVO[] getPsnDocVOs(String pk_dept, boolean isContainSub, String pk_group, String pk_org) {
		nc.vo.bd.psn.PsndocVO[] psnvos = null;
		try {
			StringBuffer condition = new StringBuffer();
			StringBuffer cond = new StringBuffer();
			cond.append(" 1=1 and ismainjob='Y'");
			if (pk_group != null)
				cond.append(" and pk_group = '" + pk_group + "' ");
			if (pk_dept == null && pk_org != null)// 部门可以是非本组织的，所以去掉tangcht
				cond.append(" and pk_org = '" + pk_org + "'");
			// 是否包含上下级部门
			if (!isContainSub) {
				cond.append(" and pk_dept ='" + pk_dept + "'");
				condition.append(" pk_psndoc in (select pk_psndoc from bd_psnjob where " + cond.toString()
						+ ") order by code");
				psnvos = NCLocator.getInstance().lookup(IPsndocQueryService.class)
						.queryPsndocVOsByCondition(condition.toString());
				// psnvos =
				// NCLocator.getInstance().lookup(IPsndocQueryService.class).queryAllPsndocvosByGroupOrOrgOrDept(pk_group,
				// pk_org, pk_dept);
			} else {
				HRDeptVO deptVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, pk_dept);
				cond.append(" and pk_dept in(select dept.pk_dept from org_dept dept where dept.innercode like '%"
						+ deptVO.getInnercode() + "%') ");
				condition.append(" pk_psndoc in (select pk_psndoc from bd_psnjob where " + cond.toString()
						+ ") order by code");
				psnvos = NCLocator.getInstance().lookup(IPsndocQueryService.class)
						.queryPsndocVOsByCondition(condition.toString());
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return psnvos;
	}

	/**
	 * 根据姓名获取pk_psndoc，暂时不支持模糊查询
	 * 
	 * @param psnname
	 * @return
	 */
	public String getPk_psndocByName(String psnname) {
		String pk_psndoc = null;
		try {
			String sql = "select pk_psndoc  from bd_psndoc " + "where  name = '" + psnname + "' ";
			IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
			ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
			if (result != null && result.size() > 0) {
				Object[] obj = (Object[]) result.get(0);
				if (obj != null && obj[0] != null) {
					pk_psndoc = obj[0].toString();
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return pk_psndoc;

	}

	/**
	 * 导出 add by tangcht on 20171024
	 * 
	 * @param mouseEvent
	 * @throws BusinessException
	 */
	public void doExportExcel(MouseEvent<MenuItem> mouseEvent) throws BusinessException {

		LfwView view = ViewUtil.getCurrentView();
		Dataset ds = view.getViewModels().getDataset(CreditCardRecordConsts.MAIN_DS_CARDINFO);

		LfwView simpQryView = getLifeCycleContext().getWindowContext()
				.getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		// 参数-年
		String customYear = String.valueOf(((UFLiteralDate) dsSearch.getValue(CreditCardRecordConsts.FD_BEGINDATE))
				.getYear());
		// 参数-月
		int a = ((UFLiteralDate) dsSearch.getValue(CreditCardRecordConsts.FD_BEGINDATE)).getMonth() - 1;
		a = a < 0 ? 12 : a;
		String customMonth = String.valueOf(a);//

		UFLiteralDate firstDateOfMonth = CreditCardUtils.getFirstDateOfMonth(customYear, customMonth);
		// 所在日历的最后一天
		UFLiteralDate lastDateOfMonth = CreditCardUtils.getLastDateOfMonth(customYear, customMonth);

		String pk_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_dept)) {
			return;
		}

		nc.vo.bd.psn.PsndocVO[] psnVOs = getPsnDocVOs(pk_dept, false, SessionUtil.getPk_group(),
				SessionUtil.getPk_org());

		if (psnVOs == null || psnVOs.length == 0) {

			CommonUtil.showErrorDialog(ResHelper.getString("coco_ta", "coco_cre_res006"));
		}

		HashMap<String, TreeMap<String, GeneralVO>> psnDateMap = new HashMap<String, TreeMap<String, GeneralVO>>();

		for (int i = 0; i < psnVOs.length; i++) {

			String psnPK = psnVOs[i].getPk_psndoc().toString();

			String psnName = MultiLangHelper.getName(psnVOs[i]);
			// 赶时间，先多次查询，有时间再优化；chekTimes 需要排序
			ICheckTime[] checkTimes = NCLocator
					.getInstance()
					.lookup(ICheckTimeQueryService.class)
					.queryCheckTimesByPsnAndDateScope(SessionUtil.getPk_org(), psnPK, firstDateOfMonth, lastDateOfMonth);

			TreeMap<String, GeneralVO> dateVoMap = new TreeMap<String, GeneralVO>();

			if (checkTimes == null || checkTimes.length == 0) {

				CommonUtil.showMessageDialog(MultiLangHelper.getName(psnVOs[i])
						+ ResHelper.getString("coco_ta", "coco_cre_res001")/* 无数据 */);
			} else {

				dateVoMap = getCocoVos(checkTimes);
			}

			psnDateMap.put(psnPK + "-" + psnName, dateVoMap);

		}

		try {
			/* 写入文件 */
			String filename = "psncardrecord" + System.currentTimeMillis() + ".xls";

			CreditCardRecordExcelExportUtils exportUtils = new CreditCardRecordExcelExportUtils();

			String path = exportUtils.exportCardRecordExcelFile(filename, psnDateMap);

			if (!StringUtils.isEmpty(path)) {
				try {
					AppLifeCycleContext
							.current()
							.getWindowContext()
							.addExecScript(
									"sysDownloadFile('" + LfwRuntimeEnvironment.getRootPath() + "/" + path + "');");
				} catch (Exception e) {
					new HrssException(e).deal();
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
	}

	/**
	 * 取同一天前两条刷卡时间作为上下班时间，合成一条显示
	 * 
	 * @param checkTimes
	 * @return
	 */
	private TreeMap<String, GeneralVO> getCocoVos(ICheckTime[] checkTimes) {

		TreeMap<String, GeneralVO> dateVoMap = new TreeMap<String, GeneralVO>();

		if (checkTimes != null) {

			for (ICheckTime checkTime : checkTimes) {

				GeneralVO vo = new GeneralVO();

				String cardID = checkTime.getTimecardid() == null ? "" : checkTime.getTimecardid().toString();

				String datetime = checkTime.getDatetime().toString();

				String date = datetime.substring(0, 10);

				datetime = datetime.substring(11);

				if (dateVoMap.get(date) == null) {

					vo.setAttributeValue("timecardid", cardID); // 卡号
					vo.setAttributeValue("date", date); // 日期
					vo.setAttributeValue("begintime", datetime); // 上班时间
					dateVoMap.put(date, vo);

				} else if (dateVoMap.get(date).getAttributeValue("endtime") == null) {

					dateVoMap.get(date).setAttributeValue("endtime", datetime);
				}
			}
		}
		return dateVoMap;
	}

}
