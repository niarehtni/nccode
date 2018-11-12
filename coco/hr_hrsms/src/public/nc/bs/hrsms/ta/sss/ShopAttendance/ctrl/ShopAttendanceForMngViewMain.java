package nc.bs.hrsms.ta.sss.ShopAttendance.ctrl;
import java.awt.MenuItem;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.hrsms.ta.sss.ShopAttendance.lsnr.ShopAttendanceUtil;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForEmpPageModel;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForMngPageModel;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttendanceForBatchPageModel;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttendanceMngPageModel;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.bd.shift.IShiftQueryService;
import nc.itf.ta.ILateEarlyManageMaintain;
import nc.itf.ta.ILateEarlyQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryMaintain;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.LabelComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.TabEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
//员工出勤情况
public class ShopAttendanceForMngViewMain implements IController {
	public static final String PLUGIN_PARAM_ID = "conditionRow";
	public static final String PARAM_CI_PSNDOC_OR_DATE = "ci_psndoc_or_date";
	public static final String PARAM_CI_PSNDOC = "ci_psndoc";
	public static final String PARAM_CI_PK_DEPT = "ci_pk_dept";
	public static final String PARAM_CI_BEGINDATE = "ci_begindate";
	public static final String PARAM_CI_ENDDATE = "ci_enddate";
	public static final String PARAM_CI_STATUS = "ci_status";
	public static final String PARAM_CI_ONLY_SHOW_EXCEPTION = "ci_onlyshowexception";
	public static final String PARAM_CI_FROMWHERESQL = "ci_fromwheresql";
	public static final String TABLAYOUT_ID = "tabTimeData";
	public static final String PANEL_LAYOUT_ID = "panellayout6473";
	public static final String SESSION_PARAM_CONDITION = "sess_cond_timedata";
	public static final String PAGE_QUERY_WIDGET = "pubview_simplequery";

	// 出勤情况总览页面
//	public static final String PAGE_TIMEDATAFORMNGAPP = "/app/TimeDataForMngApp?nodecode=E20400907";
	
	public static final String PAGE_SHOPATTFORMNGAPP="/app/ShopAttendanceApp?nodecode=E20600907";
	
	// 显示人名或日期的label
	public static final String LBL_PSNNAME_DATE = "lblShowNameOrDate";

	/**
	 * 数据集加载操作
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsMachineData(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		if (isPagination(ds)) { // 分页操作
			// 查询条件-部门
			String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_PK_DEPT);
			// 查证条件-开始日期
			UFLiteralDate beginDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_BEGINDATE);
			// 查询条件-结束日期
			UFLiteralDate endDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ENDDATE);
			// 查询条件-onlyShowException
			UFBoolean onlyShowException = (UFBoolean) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ONLY_SHOW_EXCEPTION);
			boolean ose = onlyShowException.booleanValue();
			// 查询条件-FromWhereSQL
			FromWhereSQL fromWhereSQL = (FromWhereSQL) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_FROMWHERESQL);
			boolean includeSubDept = SessionUtil.isIncludeSubDept();
			initData(fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose);

		} else {// 初始化操作
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	/**
	 * 点击搜索后,页面重新加载事件
	 * 
	 * @param keys
	 */
	public void pluginSearch(Map<String, Object> keys) {
		TBMPsndocUtil.checkTimeRuleVO();
		if (keys == null || keys.size() == 0) {
			return;
		}
		String pk_org = SessionUtil.getHROrg(ShopAttendanceMngPageModel.TIMEDATAMNG_FUNCODE, true);
		// 管理部门
		String pk_dept = SessionUtil.getPk_mng_dept();
		// 是否包含下级部门
		boolean includeSubDept = SessionUtil.isIncludeSubDept();
		
		SessionBean bean = SessionUtil.getSessionBean();
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.ISBATCHEDIT, false);
		
		nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
		
		getLifeCycleContext().getApplicationContext().addAppAttribute("whereSql_01", whereSql);
		// 查询条件-FromWhereSQL
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
		// 获得自定义条件
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();

		String psnScopeSqlPart;
		try {
			psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
			if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
				(fromWhereSQL).setWhere(fromWhereSQL.getWhere() + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
			}
		} catch (BusinessException e) {
//			new HrssException(e).deal();
		}
		
		// 年度
		String year = selfDefMap.get(ShopAttendanceMngQueryCtrl.FS_TBMYEAR);
		// 期间
		// String month = selfDefMap.get(TimeDataMngQueryCtrl.FS_TBMMONTH);
		String month = null;
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row row = dsSearch.getSelectedRow();
			month = row.getString(dsSearch.nameToIndex(ShopAttendanceMngQueryCtrl.FS_TBMMONTH));
		}
		if (StringUtil.isEmptyWithTrim(year)) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "查询失败"*/,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0143")/*
																														 * @
																														 * res
																														 * "年考勤期间未定义！"
																														 */);
			return;
		}
		if (StringUtil.isEmptyWithTrim(month)) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "查询失败"*/,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0144")/*
																														 * @
																														 * res
																														 * "月考勤期间未定义！"
																														 */);
			return;
		}
		PeriodVO periodVO = ShopAttendanceUtil.queryPeriodVOByYearMonth(pk_org, year, month);

		// 仅查询异常数据
		String onlyShowException = selfDefMap.get(ShopAttForMngPageModel.FIELD_ONLY_SHOW_EXCEPTION);
		UFBoolean ose = UFBoolean.valueOf(onlyShowException);
		UFLiteralDate beginDate = periodVO.getBegindate();
		UFLiteralDate endDate = periodVO.getEnddate();

		// 记录查询条件
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_CI_FROMWHERESQL, fromWhereSQL);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_CI_ONLY_SHOW_EXCEPTION, ose);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_CI_PK_DEPT, pk_dept);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_CI_BEGINDATE, beginDate);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_CI_ENDDATE, endDate);
		Dataset dsMachine = getCurrentView().getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MACHINE);
		Dataset dsMaual = getCurrentView().getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
		DatasetUtil.clearData(dsMachine);
		DatasetUtil.clearData(dsMaual);
		dsMachine.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		dsMaual.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		getLifeCycleContext().getApplicationContext().addBeforeExecScript("window.colorMap = null");
		getLifeCycleContext().getApplicationContext().addBeforeExecScript("window.editList = null");
		initData(fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose.booleanValue());
		
		
	}

	public void initData(FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_dept, boolean includeSubDept, boolean onlyShowException) {
		String pk_org = SessionUtil.getHROrg(ShopAttendanceMngPageModel.TIMEDATAMNG_FUNCODE, true);
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();

		if (viewMain == null) {
			return;
		}
		// 页面状态
		SessionBean bean = SessionUtil.getSessionBean();
		
		String appStatus = String.valueOf(bean.getExtendAttributeValue(ShopAttForMngPageModel.APP_STATUS));
		LabelComp lblPsnNameOrDate = (LabelComp) viewMain.getViewComponents().getComponent(LBL_PSNNAME_DATE);
		if (ShopAttForMngPageModel.STATUS_BYDATE_BROWSE.equals(appStatus)) {// 按日期查看
			// 日期
			String strDay = (String) bean.getExtendAttributeValue("headerDivTextValue");
			UFLiteralDate date = new UFLiteralDate(strDay);
			// 修改手工考勤数据保存时用
			bean.setExtendAttribute(PARAM_CI_PSNDOC, strDay);
			String[] day = ShopAttForMngPageModel.CONST_DAY_DISP.split("~");
			lblPsnNameOrDate.setInnerHTML(date.toString() + day[date.getWeek()]);
			// 拼日期JSON
			loadTimeDataByDate(fromWhereSQL, date, date, pk_dept, includeSubDept, onlyShowException);
		} else {// 按姓名查看
			String pk_psndoc = null;
			if (bean.getExtendAttributeValue(PsndocVO.PK_PSNDOC) != null) {
				pk_psndoc = (String) bean.getExtendAttributeValue(PsndocVO.PK_PSNDOC);
				IMDPersistenceQueryService service = MDPersistenceService.lookupPersistenceQueryService();
				PsndocVO psndocVO = null;
				try {
					psndocVO = service.queryBillOfVOByPK(PsndocVO.class, pk_psndoc, false);
				} catch (MetaDataException e) {
					new HrssException(e).deal();
				}
				String name = MultiLangHelper.getName(psndocVO);
				lblPsnNameOrDate.setInnerHTML(psndocVO.getCode() + MessageFormat.format(
						ResHelper.getString("c_ta-res","0c_ta-res0177")/*@res "【{0}】"*/, 
						name
					));
			} else {
				try {
					pk_psndoc = setPsnNamePanel(fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept);
				} catch (BusinessException e) {
					new HrssException(e).deal();
				} catch (HrssException e) {
					e.alert();
				}
			}
			if (StringUtil.isEmptyWithTrim(pk_psndoc)) {
				return;
			}
			// 修改手工考勤数据保存时用
			bean.setExtendAttribute(PARAM_CI_PSNDOC, pk_psndoc);
			// 根据人员查询出勤数据
			loadTimeDataByName(pk_org, pk_dept, pk_psndoc, beginDate, endDate, onlyShowException, includeSubDept);
		}
	}
	/**
	 * 从卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys){
		SessionBean bean = SessionUtil.getSessionBean();
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.ISBATCHEDIT, true);
		String pk_org = SessionUtil.getHROrg(ShopAttendanceMngPageModel.TIMEDATAMNG_FUNCODE, true);
		// 管理部门
		String pk_dept = SessionUtil.getPk_mng_dept();
		// 是否包含下级部门
		boolean includeSubDept = SessionUtil.isIncludeSubDept();
		// 执行左侧快捷查询
		nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) getLifeCycleContext().getApplicationContext().getAppAttribute("whereSql_01");
		// 查询条件-FromWhereSQL
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
		// 获得自定义条件
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();

		String psnScopeSqlPart;
		try {
			psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
			if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
				(fromWhereSQL).setWhere(fromWhereSQL.getWhere() + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
			}
		} catch (BusinessException e) {
//					new HrssException(e).deal();
		}
		
		// 年度
		String year = selfDefMap.get(ShopAttendanceMngQueryCtrl.FS_TBMYEAR);
		// 期间
		// String month = selfDefMap.get(TimeDataMngQueryCtrl.FS_TBMMONTH);
		String month = null;
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row row = dsSearch.getSelectedRow();
			month = row.getString(dsSearch.nameToIndex(ShopAttendanceMngQueryCtrl.FS_TBMMONTH));
		}
		if (StringUtil.isEmptyWithTrim(year)) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "查询失败"*/,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0143")/*
																														 * @
																														 * res
																														 * "年考勤期间未定义！"
																														 */);
			return;
		}
		if (StringUtil.isEmptyWithTrim(month)) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "查询失败"*/,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0144")/*
																														 * @
																														 * res
																														 * "月考勤期间未定义！"
																														 */);
			return;
		}
		PeriodVO periodVO = ShopAttendanceUtil.queryPeriodVOByYearMonth(pk_org, year, month);

		// 仅查询异常数据
		String onlyShowException = selfDefMap.get(ShopAttForMngPageModel.FIELD_ONLY_SHOW_EXCEPTION);
		UFBoolean ose = UFBoolean.valueOf(onlyShowException);
		UFLiteralDate beginDate = periodVO.getBegindate();
		UFLiteralDate endDate = periodVO.getEnddate();
		initData(fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose.booleanValue());
//		pluginSearch(null);
//		ShopAttendanceMngViewMain.sendRedirect(PAGE_SHOPATTFORMNGAPP);
//		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		
		
		
	}
	/**
	 * 管理部门变更
	 * 
	 * @param keys
	 */
	public void pluginDeptChange(Map<String, Object> keys) {
		ShopAttendanceMngViewMain.sendRedirect(PAGE_SHOPATTFORMNGAPP);
	}
	
	/**
	 * 按人查看考勤数据
	 * 
	 * @param fws
	 * @param begin
	 * @param end
	 * @param pk_dept
	 * @param includeSubDept
	 * @return
	 * @throws BusinessException
	 * @throws HrssException
	 */
	@SuppressWarnings("unchecked")
	private String setPsnNamePanel(FromWhereSQL fws, UFLiteralDate begin, UFLiteralDate end, String pk_dept, boolean includeSubDept) throws BusinessException, HrssException {
		TBMPsndocVO[] tbmPsndocVOs = ShopAttendanceUtil.getTbmPsndoc(fws, begin, end, pk_dept, includeSubDept);
		if (ArrayUtils.isEmpty(tbmPsndocVOs)) {
			return null;
		}
		// 根据查询条件，查出一个人的时候，在按人员查看界面显示
		if (tbmPsndocVOs.length == 1) {

			String pk_psndoc = null;

			// 获取人员姓名信息
			HashMap<String, PsndocVO> map = new HashMap<String, PsndocVO>();
			Collection<PsndocVO> psnScope = null;
			try {
				psnScope = new BaseDAO().retrieveByClause(PsndocVO.class, "pk_psndoc in (" + new InSQLCreator().getInSQL(tbmPsndocVOs, "pk_psndoc") + ")");
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			for (PsndocVO v : psnScope) {
				map.put(v.getPk_psndoc(), v);
			}
			pk_psndoc = tbmPsndocVOs[0].getPk_psndoc();

			String psnCode = map.get(pk_psndoc).getCode();
			LfwView widget = LfwRuntimeEnvironment.getWebContext().getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
			LabelComp lblPsnNameOrDate = (LabelComp) widget.getViewComponents().getComponent(LBL_PSNNAME_DATE);
			lblPsnNameOrDate.setInnerHTML(psnCode + MessageFormat.format(
					ResHelper.getString("c_ta-res","0c_ta-res0177")/*@res "【{0}】"*/, 
					MultiLangHelper.getName(map.get(pk_psndoc))
				));

			return pk_psndoc;
		} else {
			// 根据查询条件，查出多个人或没有人的时候跳转到出勤总览页面
			ShopAttendanceMngViewMain.sendRedirect(PAGE_SHOPATTFORMNGAPP);
			return null;
		}
	}

	/**
	 * 查询员工考勤数据
	 * 
	 * @param pk_dept
	 * @param pk_psndoc
	 * @param begin
	 * @param end
	 */
	private void loadTimeDataByName(String pk_org, String pk_dept, String pk_psndoc, UFLiteralDate begin, UFLiteralDate end, 
			boolean onlyShowException, boolean containsSubDepts) {
		// 机器考勤数据
		TimeDataVO[] machineData = null;
		// 手工考勤数据
		LateEarlyVO[] manualData = null;
		// 考核档案
		TBMPsndocVO tbmPsndocVO = null;
		try {
			ITimeDataQueryMaintain tdq = ServiceLocator.lookup(ITimeDataQueryMaintain.class);
			machineData = tdq.queryByPsnAndDept4Mgr(pk_dept, pk_psndoc, begin, end, onlyShowException,containsSubDepts);
			ILateEarlyQueryMaintain leq = ServiceLocator.lookup(ILateEarlyQueryMaintain.class);
			manualData = leq.queryByPsnAndDept4Mgr(pk_dept, pk_psndoc, begin, end, onlyShowException,containsSubDepts);
			ITBMPsndocQueryService tbmPsndocQueryService = ServiceLocator.lookup(ITBMPsndocQueryService.class);
			tbmPsndocVO = tbmPsndocQueryService.queryLatestByPsndocDate(pk_org, pk_psndoc, begin, end);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
//		AppLifeCycleContext cty=  getLifeCycleContext();
//		LfwView viewMain = cty.getViewContext().getView();
		
		ShopAttendanceUtil.fillData(getLifeCycleContext(), machineData, manualData, tbmPsndocVO == null ? 0 : tbmPsndocVO.getTbm_prop());
	}

	/**
	 * 获得当前活动View
	 * 
	 * @return
	 */
	public LfwView getCurrentView() {
		return getLifeCycleContext().getViewContext().getView();
	}

	/**
	 * 查询员工考勤数据
	 * 
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param pk_dept
	 * @param containsSubDepts
	 */
	public void loadTimeDataByDate(FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_dept, boolean containsSubDepts, boolean onlyShowException) {
		// 机器考勤数据
		TimeDataVO[] machineData = null;
		// 手工考勤数据
		LateEarlyVO[] manualData = null;
		try {
			ITimeDataQueryMaintain tqs = ServiceLocator.lookup(ITimeDataQueryMaintain.class);
			machineData = tqs.queryByCondDateAndDept(fromWhereSQL, beginDate, endDate, onlyShowException, pk_dept, containsSubDepts);
			ILateEarlyQueryMaintain lqs = ServiceLocator.lookup(ILateEarlyQueryMaintain.class);
			manualData = lqs.queryByCondDateAndDept(fromWhereSQL, beginDate, endDate, onlyShowException, pk_dept, containsSubDepts);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}

		ShopAttendanceUtil.fillData(getLifeCycleContext(), machineData, manualData, 0);
	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	/**
	 * 手工数据发生变化，对输入数据校验
	 * 
	 * @param datasetCellEvent
	 */
	public void dataChanged(DatasetCellEvent datasetCellEvent) {
		SessionBean bean = SessionUtil.getSessionBean();
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.ISBATCHEDIT, false);
		
		LfwView main = AppLifeCycleContext.current().getViewContext().getView();
		Dataset dsManual = main.getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
		// 获取行索引
		int rowIndex = datasetCellEvent.getRowIndex();
		// 获取列索引
		int colIndex = datasetCellEvent.getColIndex();
		RowData rowData = dsManual.getCurrentRowData();
		Row[] rows = rowData.getRows();
		Row row = rows[rowIndex];
		String pk_shift = (String) row.getValue(dsManual.nameToIndex(LateEarlyVO.PK_SHIFT));
//		UFLiteralDate calendar=(UFLiteralDate) row.getValue(dsManual.nameToIndex(LateEarlyVO.CALENDAR));
		// 班次AggVO
		AggShiftVO aggShiftVO = null;
		try {
			IShiftQueryService shiftService = ServiceLocator.lookup(IShiftQueryService.class);
			aggShiftVO = shiftService.queryShiftAggVOByPk(pk_shift);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

		// 班次时长
		UFDouble gzsj = aggShiftVO.getShiftVO().getGzsj();
		// // 是否有夜班
		// UFBoolean includenightshift =
		// aggShiftVO.getShiftVO().getIncludenightshift();
		// 夜班时长
		UFDouble nightgzsj = aggShiftVO.getShiftVO().getNightgzsj();

		// 迟到时长
		UFDouble latelength = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH));

		// 早退时长
		UFDouble earlylength = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH));
		// 旷工时长
		UFDouble absenthour = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR));
		// 夜班旷工时长
		UFDouble nightabsenthour = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR));
		if (latelength == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (earlylength == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (absenthour == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		if (nightabsenthour == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		if (latelength.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (earlylength.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (absenthour.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		if (nightabsenthour.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		boolean flag = false;
		if (latelength.doubleValue() + earlylength.doubleValue() + absenthour.doubleValue() * 60 > gzsj.doubleValue() * 60) {
			flag = true;
		}

		// 修改迟到时长
		if (colIndex == dsManual.nameToIndex(LateEarlyVO.LATELENGTH)) {
			if (flag) {
				latelength = new UFDouble(gzsj.doubleValue() * 60 - (absenthour.doubleValue() * 60 + earlylength.doubleValue()));
				row.setValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH), latelength);
			}
			// 早退
		} else if (colIndex == dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH)) {
			if (flag) {
				earlylength = new UFDouble(gzsj.doubleValue() * 60 - (absenthour.doubleValue() * 60 + latelength.doubleValue()));
				row.setValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH), earlylength);
			}

			// 旷工
		} else if (colIndex == dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR)) {
			if (flag) {
				absenthour = new UFDouble((gzsj.doubleValue() * 60 - (earlylength.doubleValue() + latelength.doubleValue())) / 60);
				row.setValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR), absenthour);
			}

			// 修改夜班旷工时长
		} else if (colIndex == dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR)) {
			if (nightabsenthour.doubleValue() > nightgzsj.doubleValue()) {
				row.setValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR), nightgzsj);
			}
		}else if(colIndex == dsManual.nameToIndex(LateEarlyVO.ONEBEGINSTATUS)){
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_BEGIN, calendar);
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_END, calendar);
			Integer status=(Integer) row.getValue(dsManual.nameToIndex(LateEarlyVO.ONEBEGINSTATUS));
			if(status==0){
				bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_DATESTATUS,"正常" );
			}else if(status==1){
				bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_DATESTATUS,"迟到或早退" );
			}else if(status==2){
				bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_DATESTATUS,"未出勤" );
			}
			
		}else if(colIndex == dsManual.nameToIndex(LateEarlyVO.TWOENDSTATUS)){
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_BEGIN, calendar);
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_END, calendar);
			Integer status=(Integer) row.getValue(dsManual.nameToIndex(LateEarlyVO.ONEBEGINSTATUS));
			if(status==0){
				bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_DATESTATUS,"正常" );
			}else if(status==1){
				bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_DATESTATUS,"迟到或早退" );
			}else if(status==2){
				bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_DATESTATUS,"未出勤" );
			}
			
		}

	}

	/**
	 * 保存
	 * 
	 * @param mouseEvent
	 */
	public void onTimeDataSaveclick(MouseEvent<MenuItem> mouseEvent) {
		Dataset dsManual = getLifeCycleContext().getViewContext().getView().getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
		dsManual.setVoMeta(LateEarlyVO.class.getName());
		SuperVO[] vos = DatasetUtil.getUpdatedDataInVO(dsManual);
		
		if (vos == null || vos.length == 0) {
			return;
		}
		List<SuperVO> list = new ArrayList<SuperVO>();
		for(SuperVO vo : vos){
//			if(vo.getStatus() != 0){
//				list.add(vo);
//			}
			
			list.add(vo);
		}
		vos = list.toArray(new SuperVO[0]);
		if (vos == null || vos.length == 0) {
			return;
		}
		LateEarlyVO[] levos = new LateEarlyVO[vos.length];
		String defaultOrg = SessionUtil.getPk_mng_org();
		String defaultGroup = SessionUtil.getPk_mng_group();
		SessionBean sess = SessionUtil.getSessionBean();
		String pk_psndoc = (String) sess.getExtendAttributeValue(PARAM_CI_PSNDOC);
		for (int i = 0; i < vos.length; i++) {
			LateEarlyVO vo = (LateEarlyVO) vos[i];
			if (StringUtil.isEmpty(vo.getPk_org())) {
				/* 为新增记录增加默认组织、集团 */
				vo.setPk_org(defaultOrg);
			}
			if (StringUtil.isEmptyWithTrim(vo.getPk_group())) {
				vo.setPk_group(defaultGroup);
			}
			if (StringUtil.isEmptyWithTrim(vo.getPk_psndoc())) {
				vo.setPk_psndoc(pk_psndoc);
			}
			levos[i] = vo;
		}
		try {
			ILateEarlyManageMaintain lemm = ServiceLocator.lookup(ILateEarlyManageMaintain.class);
			lemm.update(levos);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		onCancel(mouseEvent);
	}

	public Class<? extends SuperVO> getLateEarlyVo() {
		return LateEarlyVO.class;
	}

	/**
	 * 数据集加载操作
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsManualData(DataLoadEvent dataLoadEvent) {
		Dataset dsMaual = getCurrentView().getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
		if (isPagination(dsMaual)) { // 分页操作
			
			getLifeCycleContext().getApplicationContext().addAppAttribute("HrssCurrentItem", "1");
			// 查询条件-部门
			String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_PK_DEPT);
			// 查证条件-开始日期
			UFLiteralDate beginDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_BEGINDATE);
			// 查询条件-结束日期
			UFLiteralDate endDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ENDDATE);
			// 查询条件-onlyShowException
			UFBoolean onlyShowException = (UFBoolean) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_ONLY_SHOW_EXCEPTION);
			boolean ose = onlyShowException.booleanValue();
			// 查询条件-FromWhereSQL
			FromWhereSQL fromWhereSQL = (FromWhereSQL) getLifeCycleContext().getApplicationContext().getAppAttribute(ShopAttendanceForMngViewMain.PARAM_CI_FROMWHERESQL);
			boolean includeSubDept = SessionUtil.isIncludeSubDept();
			
			initData(fromWhereSQL, beginDate, endDate, pk_dept, includeSubDept, ose);

		} else {// 初始化操作
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	/**
	 * 分页操作标志
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}

	/**
	 * 页签切换事件
	 * 
	 * @param tabEvent
	 */
	public void afterActivedTabItemChange(TabEvent tabEvent) {
		UIMeta um = (UIMeta) getLifeCycleContext().getViewContext().getUIMeta();
		UITabComp tabComp = (UITabComp) um.findChildById(ShopAttendanceUtil.TAB_TIME_DATA);
		if(tabComp.getCurrentItem().equals("1")){
			getLifeCycleContext().getApplicationContext().addAppAttribute("HrssCurrentItem", "1");
			onDataLoad_dsManualData(null);
		}
		ButtonStateManager.updateButtons();
		
	}

	/**
	 * 返回
	 * 
	 * @param mouseEvent
	 */
	public void goback(MouseEvent<MenuItem> mouseEvent) {
		SessionBean session = SessionUtil.getSessionBean();
		session.removeExtendAttribute(PsndocVO.PK_PSNDOC);
		session.removeExtendAttribute(ShopAttForMngPageModel.FIELD_TIME_DATA_PSNNAME);
		ShopAttendanceMngViewMain.sendRedirect(PAGE_SHOPATTFORMNGAPP);
	}
	/**
	 * 批量修改
	 * 
	 * @param mouseEvent
	 */
	public void Batchedit(MouseEvent<MenuItem> mouseEvent){
		
		ShopAttendanceForBatchViewMain.doBatchEdit();
		
	}
	
	
	
	/**
	 * 取消
	 * 
	 * @param mouseEvent
	 */
	public void onCancel(MouseEvent<MenuItem> mouseEvent) {
		SessionBean sess = SessionUtil.getSessionBean();
		
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		// 是否包含下级部门
		boolean includeSubDept = SessionUtil.isIncludeSubDept();
		// 组织
		String pk_org = SessionUtil.getHROrg(ShopAttendanceMngPageModel.TIMEDATAMNG_FUNCODE, true);
		// 查询条件-部门
		String pk_dept = (String) appCtx.getAppAttribute(PARAM_CI_PK_DEPT);
		// 选中的日期或人员姓名
		String psndocOrDate = (String) sess.getExtendAttributeValue(PARAM_CI_PSNDOC);
		// 查询条件-onlyShowException
		UFBoolean onlyShowException = (UFBoolean) appCtx.getAppAttribute(PARAM_CI_ONLY_SHOW_EXCEPTION);
		// 查询条件——FrmWhSQL
		FromWhereSQL fromWhereSQL = (FromWhereSQL) appCtx.getAppAttribute(PARAM_CI_FROMWHERESQL);
		// 查证条件-开始日期
		UFLiteralDate beginDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_CI_BEGINDATE);
		// 查询条件-结束日期
		UFLiteralDate endDate = (UFLiteralDate) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_CI_ENDDATE);
		boolean ose = onlyShowException.booleanValue();

		String appStatus = String.valueOf(sess.getExtendAttributeValue(ShopAttForMngPageModel.APP_STATUS));

		if (ShopAttForMngPageModel.STATUS_BYDATE_BROWSE.equals(appStatus)) {// 按日期查看
			UFLiteralDate date = new UFLiteralDate(psndocOrDate);
			loadTimeDataByDate(fromWhereSQL, date, date, pk_dept, includeSubDept, ose);
		} else {// 按姓名查看
			String pk_psndoc = psndocOrDate;

			// 根据人员查询出勤数据
			if (StringUtil.isEmptyWithTrim(pk_psndoc)) {// 人员列表为空
				return;
			}
			loadTimeDataByName(pk_org,pk_dept, pk_psndoc, beginDate, endDate, ose, includeSubDept);
		}

		UIMeta um = getLifeCycleContext().getViewContext().getUIMeta();
		UITabComp tabComp = (UITabComp) um.findChildById(ShopAttendanceUtil.TAB_TIME_DATA);
		tabComp.setCurrentItem("1");
	}
	
	
	
	
}






























