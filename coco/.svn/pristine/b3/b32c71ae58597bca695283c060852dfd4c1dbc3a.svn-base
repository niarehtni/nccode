package nc.bs.hrsms.ta.sss.calendar.ctrl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dbcache.intf.IDBCacheBS;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrsms.ta.sss.calendar.dft.BatchChangeShift_StateManager;
import nc.bs.hrsms.ta.sss.calendar.pagemodel.BatchChangeShiftPageModel;
import nc.bs.hrsms.ta.sss.calendar.pagemodel.WorkCalendarForPsnPageModel;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.TextNavigationUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.calendar.CalendarUtils;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.StringPiecer;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrss.ta.calendar.QryConditionVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class WorkCalendarForPsnViewMain implements IController {

	// 参数--人员主键
	public static final String PARAM_CI_PK_PSNDOC = "ci_pk_psndoc";
	// 参数--年份
	public static final String PARAM_CI_YEAR = "ci_year";
	// 参数--月份
	public static final String PARAM_CI_MONTH = "ci_month";
	// 参数：人员的PsnJobCalendarVO
	public static final String PARAM_PSNJOB_CALENDARVO = "ci_PsnJobCalendarVO";

	/**
	 * 日历数据集的加载事件,触发查询操作<br/>
	 * 页面初始化加载操作.<br/>
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsCalendar(DataLoadEvent dataLoadEvent) {
		CalendarUtils.setCalendarColorToClientSession();
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 循环排班  关闭刷新
	 * @param keys
	 */
	public void pluginCircleArrangeShift_inId(Map<String, Object> keys) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 批量排班 关闭窗口刷新
	 * @param keys
	 */
	public void plugininid_soci(Map<String, Object> keys) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	

	/**
	 * 管理部门变更
	 * 
	 * @param keys
	 */
	public void pluginDeptChange(Map<String, Object> keys) {
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			// 管理部门所在的HR组织
			String pk_hr_org = SessionUtil.getHROrg();
			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
			if (dsSearch.nameToIndex(WorkCalendarConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(dsSearch.nameToIndex(WorkCalendarConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (dsSearch.nameToIndex(WorkCalendarConsts.FD_ENDDATE) > -1) {
				// 结束日期
				selRow.setValue(dsSearch.nameToIndex(WorkCalendarConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}
			if (dsSearch.nameToIndex(WorkCalendarConsts.FD_ENDDATE) > -1) {
				// 排班条件
				selRow.setValue(dsSearch.nameToIndex(WorkCalendarConsts.FD_ARRANGEFLAG), String.valueOf(WorkCalendarConsts.QUERYSCOPE_ALL));
			}

			// 清空保存的查询条件
			SessionUtil.getSessionBean().setExtendAttribute(WorkCalendarConsts.SESSION_QRY_CONDITIONS, null);
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	

	/**
	 * 分类切换事件
	 * 
	 * @param keys
	 */
	public void pluginCatagory(Map<String, Object> keys) {
		WorkCalendarListViewMain.pluginCatagory(keys);
	}

	/**
	 * 点击搜索后,页面重新加载事件
	 * 
	 * @param keys
	 */
	public void pluginSearch(Map<String, Object> keys) {
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
		QryConditionVO vo = WorkCalendarListViewMain.getConditions(keys);
		// 加载页面人员显示部分
		String pk_psndoc = loadDeptPsns(pk_dept, isContainSub, vo.getFromWhereSQL(), vo.getBeginDate(), vo.getEndDate(), vo.getArrangeflag());
		ApplicationContext appCxt = AppUtil.getCntAppCtx();
		// 设置前台显示的人员主键
		appCxt.getClientSession().setAttribute(PARAM_CI_PK_PSNDOC, pk_psndoc);
		if (StringUtils.isEmpty(pk_psndoc)) {
			// 清除人员的导航数据
			
			appCxt.addExecScript("destroyTextNavigation();");
			// 清空日历中的班次信息
			LfwView view = ViewUtil.getCurrentView();
			Dataset dsCalendar = view.getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
			DatasetUtil.clearData(dsCalendar);
			// 获取当前多语编号
			String currentLanguageCode = String.valueOf(MultiLangContext.getInstance().getCurrentLangVO().getLangcode());
			/* 前台重绘日历组件 */
			appCxt.addExecScript("loadCalendar(" + vo.getBeginDate().getYear() + "," + String.valueOf(vo.getBeginDate().getMonth() - 1) + ",'" + currentLanguageCode + "');");
		}else {
			// 加载人员的日历组件
			loadCalendar(String.valueOf(vo.getBeginDate().getYear()), String.valueOf(vo.getBeginDate().getMonth() - 1), pk_psndoc);
		}		
	}

	/**
	 * 加载页面人员显示部分
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String loadDeptPsns(String pk_dept, boolean isContainSub, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, String arrangeflag) {

//		QueryScopeEnum queryScope = CalendarUtils.getArrangeFlagScope(arrangeflag);
//		String pk_psndoc = "";
//		PsndocVO[] psndocVOs = getPsndocVOsByCond(pk_dept, isContainSub, fromWhereSQL, beginDate, endDate, queryScope);

		String pk_psndoc = "";
		PsnJobCalendarVO[] psnJobCalendarVOs = CalendarUtils.getDeptPsnCalendar(pk_dept, isContainSub, beginDate, endDate,
						arrangeflag, fromWhereSQL);
		if (ArrayUtils.isEmpty(psnJobCalendarVOs)) {
			return null;
		}
		// 获取人员姓名信息
		HashMap<String, PsndocVO> map = new HashMap<String, PsndocVO>();
		Collection<PsndocVO> psndocVOs = null;
		try {
			psndocVOs = new BaseDAO().retrieveByClause(PsndocVO.class,
					"pk_psndoc in ("+ new InSQLCreator().getInSQL(psnJobCalendarVOs,PsnJobVO.PK_PSNDOC) + ")");
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
		for (int i = 0; i < psnJobCalendarVOs.length; i++) {
			pk_psndoc = psnJobCalendarVOs[i].getPk_psndoc();
			jsonBuf.append(TextNavigationUtil.buildTextNavgItemJson(i,map.get(pk_psndoc).getMultiLangName(), pk_psndoc, ""));
		}
		pk_psndoc = psnJobCalendarVOs[0].getPk_psndoc();
		ApplicationContext appCxt = getLifeCycleContext().getApplicationContext();
		appCxt.addExecScript("ceateTextNavigation('{[" + jsonBuf.toString()+ "]}');");
		appCxt.addAppAttribute(BatchChangeShift_StateManager.APP_PSN_COUNT,
				String.valueOf(ArrayUtils.isEmpty(psnJobCalendarVOs) ? 0: psnJobCalendarVOs.length));
		
		// 人员姓名导航
//		StringBuffer jsonBuf = new StringBuffer("");
//		if (psndocVOs != null && psndocVOs.length > 0) {
//			for (int i = 0; i < psndocVOs.length; i++) {
//				jsonBuf.append(TextNavigationUtil.buildTextNavgItemJson(i, MultiLangHelper.getName(psndocVOs[i]), psndocVOs[i].getPk_psndoc(), ""));
//			}
//			pk_psndoc = psndocVOs[0].getPk_psndoc();
//		}
//		ApplicationContext appCxt = getLifeCycleContext().getApplicationContext();
//		appCxt.addExecScript("ceateTextNavigation('{[" + jsonBuf.toString() + "]}');");
//		appCxt.addAppAttribute(BatchChangeShift_StateManager.APP_PSN_COUNT, String.valueOf(ArrayUtils.isEmpty(psndocVOs) ? 0 : psndocVOs.length));

		// 批量调班时使用参数 */
		String[] psnjobPks = StringPiecer.getStrArray(psnJobCalendarVOs, "pk_psnjob");
		appCxt.addAppAttribute(BatchChangeShiftPageModel.WSES_PSN_KEYS, psnjobPks);
		appCxt.addAppAttribute(BatchChangeShiftPageModel.FLD_BEGIN, beginDate);
		appCxt.addAppAttribute(BatchChangeShiftPageModel.FLD_END, endDate);
		ButtonStateManager.updateButtons();
		return pk_psndoc;
	}

	/**
	 * 查询指定部门、条件、日期范围内的人员
	 * 
	 * @param pk_dept
	 * @param containsSubDepts
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param queryScope
	 * @return
	 */
//	private PsndocVO[] getPsndocVOsByCond(String pk_dept, boolean containsSubDepts, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, QueryScopeEnum queryScope) {
//		PsndocVO[] psndocVOs = null;
//		try {
//			// String psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
//			// if (fromWhereSQL != null &&
//			// !StringUtils.isEmpty(psnScopeSqlPart)) {
//			// ((FromWhereSQLImpl)
//			// fromWhereSQL).setWhere(fromWhereSQL.getWhere() +
//			// " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
//			// }
//			IPsnCalendarQueryMaintain service = ServiceLocator.lookup(IPsnCalendarQueryMaintain.class);
//			psndocVOs = service.queryPsndocVOsByConditionAndDept(pk_dept, containsSubDepts, fromWhereSQL, beginDate, endDate, queryScope);
//		} catch (HrssException e) {
//			e.alert();
//		} catch (BusinessException e) {
//			new HrssException(e).deal();
//		}
//		return psndocVOs;
//	}

	/**
	 * 日期变更操作
	 * 
	 * @param mouseEvent
	 */
	public void onDateChanged(ScriptEvent scriptEvent) {
		String pk_psndoc = getLifeCycleContext().getParameter(PARAM_CI_PK_PSNDOC);
		// 设置前台显示的人员主键
		AppUtil.getCntAppCtx().getClientSession().setAttribute(PARAM_CI_PK_PSNDOC, pk_psndoc);
		// 参数-年
		String customYear = getLifeCycleContext().getParameter(PARAM_CI_YEAR);
		// 参数-月
		String customMonth = getLifeCycleContext().getParameter(PARAM_CI_MONTH);
		// 重新加载数据
		loadCalendar(customYear, customMonth, pk_psndoc);
	}

	/**
	 * 加载工作日历
	 * 
	 * @param ctx
	 * @param beginDate
	 * @param endDate
	 * @param arrangeflag
	 */
	private void loadCalendar(String year, String month, String pk_psndoc) {

		// 所在日历的第一天
		UFLiteralDate firstDateOfMonth = CalendarUtils.getFirstDateOfMonth(year, month);
		// 所在日历的最后一天
		UFLiteralDate lastDateOfMonth = CalendarUtils.getLastDateOfMonth(year, month);

		String pk_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_dept)) {
			return;
		}
		// 默认显示第一个员工的工作日历
		PsnJobCalendarVO calVO = getPsnCalendar(pk_dept, pk_psndoc, firstDateOfMonth, lastDateOfMonth);
		ApplicationContext appCxt = getLifeCycleContext().getApplicationContext();
		appCxt.addAppAttribute(PARAM_PSNJOB_CALENDARVO, calVO);

		/* 填充Dataset */
		LfwView view = ViewUtil.getCurrentView();
		Dataset dsCalendar = view.getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		/* 将人员工作日历VO转换为Dataset */
		CalendarUtils.fillDataset(dsCalendar, calVO, true);
		/**获取日期范围内的总工时数  begin add shaochj 2015-04-29 【太平鸟项目】*/
		Row[] row = dsCalendar.getAllRow();
		if(row!=null){
			double gzsj = 0;
			for(int i=0;i<row.length;i++){
				String pk_shift = row[i].getString(dsCalendar.nameToIndex("pk_shift"));
				gzsj += this.getGzsjByPk_shift(pk_shift);
			}
			appCxt.addExecScript("setTotalTimes("+gzsj+""+")");
		}
		//end  shaochj 2015-04-29 【太平鸟项目】
		// 获取当前多语编号
		String currentLanguageCode = String.valueOf(MultiLangContext.getInstance().getCurrentLangVO().getLangcode());
		/* 前台重绘日历组件 */
		appCxt.addExecScript("loadCalendar(" + year + "," + month + ",'" + currentLanguageCode + "');");
		
		dsCalendar.setEnabled(false);
		ButtonStateManager.updateButtons();
	}

	/**
	 * 获取指定员工某时间段内的工作日历
	 * 
	 * @param pk_psndoc
	 *            员工基本信息主键
	 * @param begin
	 *            起始日期
	 * @param end
	 *            终止日期
	 * @return
	 * @author haoy 2011-7-31
	 */
	public PsnJobCalendarVO getPsnCalendar(String pk_dept, String pk_psndoc, UFLiteralDate begin, UFLiteralDate end) {
		PsnJobCalendarVO data = null;
		try {
			data = ServiceLocator.lookup(IPsnCalendarQueryMaintain.class).queryCalendarVOByConditionAndDept(pk_dept, pk_psndoc, begin, end);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return data;
	}

	/**
	 * 保存操作
	 * 
	 * @param mouseEvent
	 */

	@SuppressWarnings("rawtypes")
	public void doSave(MouseEvent mouseEvent) {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset dsCalendar = viewMain.getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		PsnJobCalendarVO[] psnvos = getChangeCalendars(dsCalendar); // 获得更改的班次数据
		ApplicationContext appCxt = getLifeCycleContext().getApplicationContext();
		if (psnvos != null && psnvos.length > 0) {
			try {
				IPsnCalendarManageMaintain service = ServiceLocator.lookup(IPsnCalendarManageMaintain.class);
				psnvos = service.save4Mgr(SessionUtil.getPk_mng_dept(), psnvos);
			} catch (HrssException e) {
				new HrssException(e).alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			/* 将人员工作日历VO转换为Dataset */
			CalendarUtils.fillDataset(dsCalendar, psnvos[0], true);

			/* 前台重绘日历组件 */
			String pk_psndoc = getLifeCycleContext().getParameter(PARAM_CI_PK_PSNDOC);
			// 设置前台显示的人员主键
			AppUtil.getCntAppCtx().getClientSession().setAttribute(PARAM_CI_PK_PSNDOC, pk_psndoc);
			// 参数-年
			String customYear = getLifeCycleContext().getParameter(PARAM_CI_YEAR);
			// 参数-月
			String customMonth = getLifeCycleContext().getParameter(PARAM_CI_MONTH);
			// 获取当前多语编号
			String currentLanguageCode = String.valueOf(MultiLangContext.getInstance().getCurrentLangVO().getLangcode());
			appCxt.addExecScript("loadCalendar(" + customYear + "," + customMonth + ",'" + currentLanguageCode + "');");
		}
		// 更新菜单状态
		dsCalendar.setEnabled(false);
		ButtonStateManager.updateButtons();

	}

	/**
	 * 根据日历信息集获取需要保存的VO。
	 * 
	 * @param dsCalendar
	 * @return
	 * @author haoy 2011-8-3
	 */
	private PsnJobCalendarVO[] getChangeCalendars(Dataset dsCalendar) {
		Row[] changedRows = DatasetUtil.getUpdatedRows(dsCalendar);
		if (ArrayUtils.isEmpty(changedRows)) {
			return null;
		}
		FieldSet fs = dsCalendar.getFieldSet();
		int idxPsndoc = fs.nameToIndex("pk_psndoc");
		int idxPsnjob = fs.nameToIndex("pk_psnjob");
		int idxCalendar = fs.nameToIndex("calendar");
		int idxClass = fs.nameToIndex("pk_shift");
		// 创建HashMap保存每个人的日历VO
		HashMap<String, PsnJobCalendarVO> map = new HashMap<String, PsnJobCalendarVO>();
		ArrayList<PsnJobCalendarVO> list = new ArrayList<PsnJobCalendarVO>();
		PsnJobCalendarVO calVO = (PsnJobCalendarVO) AppUtil.getCntAppCtx().getAppAttribute(PARAM_PSNJOB_CALENDARVO);
		//double gzsj = 0;
	//	HashMap<String,String> gzsjMap = new HashMap<String, String>();
		for (Row row : changedRows) {
			String pk_psnjob = row.getString(idxPsnjob);
			if (!map.containsKey(pk_psnjob)) {
				PsnJobCalendarVO pjvo = null;
				if (calVO != null && !StringUtils.isEmpty(pk_psnjob) && pk_psnjob.equals(calVO.getPk_psnjob())) {
					pjvo = calVO;
				} else {
					pjvo = new PsnJobCalendarVO();
				}
				pjvo.setPk_psnjob(row.getString(idxPsnjob));
				pjvo.setPk_psndoc(row.getString(idxPsndoc));
				map.put(pk_psnjob, pjvo);
				list.add(pjvo);
			}
			PsnJobCalendarVO vo = map.get(pk_psnjob);
			UFLiteralDate calendar = (UFLiteralDate) row.getValue(idxCalendar);
			String pk_shift = row.getString(idxClass);
			vo.getModifiedCalendarMap().put(calendar.toString(), pk_shift);
			//gzsj += this.getGzsjByPk_shift(pk_shift);
		}
		//gzsjMap.put("totaltimes", gzsj+"");
	//	AppUtil.getCntAppCtx().addAppAttribute("totaltimes", gzsj+"");
		return list.toArray(new PsnJobCalendarVO[0]);
	}

	/**
	 * 排班
	 * 
	 * @param mouseEvent
	 */
	public void doChangeClasses(MouseEvent<MenuItem> mouseEvent) {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset dsCalendar = viewMain.getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		// 更新菜单状态
		dsCalendar.setEnabled(true);
		ButtonStateManager.updateButtons();
	}

	/**
	 * 刷新操作
	 * 
	 * @param mouseEvent
	 */
	public void doRefresh(MouseEvent<MenuItem> mouseEvent) {
		LfwView view = ViewUtil.getCurrentView();
		Dataset dsCalendar = view.getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		// 更新菜单状态
		dsCalendar.setEnabled(false);
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 批量调班操作
	 * 
	 * @param mouseEvent
	 */
	public void doBatchChange(MouseEvent<MenuItem> mouseEvent) {
		BatchChangeShiftViewMain.doBatchChange();
	}

	/**
	 * 循环排班 操作
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCircleArrangeShift(MouseEvent mouseEvent) {
		BatchArrangeShiftViewMain.doCircleArrangeShift(WorkCalendarConsts.FUNC_CODE);
	}
	/**
	 * 修改时班次后,同时修改了dsCalendar的值，但无法改日历组件中的对应内容.<br/>
	 * 所以添加了dsCalendar的onDataChange事件,调用此方法执行的日历更新显示
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange_dsCalendar(DatasetCellEvent datasetCellEvent) {
		int colIndex = datasetCellEvent.getColIndex();
		Dataset dsCalendar = datasetCellEvent.getSource();
		if (colIndex == dsCalendar.nameToIndex("pk_shift")) {
			getLifeCycleContext().getApplicationContext().addExecScript("updateShiftDiv()");
		}

	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
	
	/**
	 * 根据班次PK获取工作时长
	 * @param pk_shift
	 * @return
	 */
	public double getGzsjByPk_shift(String pk_shift){
		double gzsj = 0;
		/*try {
			AggShiftVO aggVO = NCLocator.getInstance().lookup(IStoreShiftQueryMaintain.class).queryByPk(pk_shift);
			ShiftVO vo = (ShiftVO) aggVO.getParentVO();
			UFDouble gzsc = vo.getGzsj();
			gzsj = gzsc.doubleValue();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		try {
			String sql = "select gzsj  from bd_shift " +
					"where  pk_shift = '"+pk_shift+"' ";
			IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(
					IDBCacheBS.class.getName());
			ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql,
					new ArrayListProcessor());
			if (result != null && result.size() > 0) {
				Object[] obj = (Object[]) result.get(0);
				if (obj != null && obj[0] != null){
					gzsj = Double.parseDouble(obj[0].toString());
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return gzsj;
	}
}
