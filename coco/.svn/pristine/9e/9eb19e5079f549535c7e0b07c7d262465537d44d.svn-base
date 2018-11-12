package nc.bs.hrss.ta.timedata.ctrl;

import java.util.List;
import java.util.Map;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.common.ctrl.BaseController;
import nc.bs.hrss.ta.signcard.SignConsts;
import nc.bs.hrss.ta.timedata.TimeDataForEmpPageModel;
import nc.bs.hrss.ta.timedata.lsnr.TimeDataUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILateEarlyQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryMaintain;
import nc.uap.ctrl.tpl.qry.FromWhereSQLImpl;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.TabEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.jsp.uimeta.UILayoutPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.uap.lfw.jsp.uimeta.UITabItem;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.signcard.AggSignVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
/**
 * 我的出勤情况-出勤详细
 * 
 * @author qiaoxp
 * 
 */
public class TimeDataForEmpViewMain extends BaseController {
	//员工信息
	private static final String APP_ATTR_PSNDOC="timedata_emp_pk_psndoc";
	// 开始日期
	private static final String APP_ATTR_BEIGNDATE = "timedata_emp_beigndate";
	// 结束日期
	private static final String APP_ATTR_ENDDATE = "timedata_emp_enddate";
	// 仅查询异常数据																									 
	private static final String APP_ATTR_ONLYSHOWEXCEPTION = "timedata_emp_onlyshowexception";
	private static final int ITEM_INDEX_MACHINE = 0;
	private static final int ITEM_INDEX_MANUAL = 1;
	/**
	 * 机器考勤数据数据集加载操作
	 * 
	 * @param dataLoadEvent
	 */
	public void dsMachineData_onDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset dsMachine = dataLoadEvent.getSource();
		if (isPagination(dsMachine)) { // 分页操作
			String pk_psndoc = (String) getApplicationContext().getAppAttribute(APP_ATTR_PSNDOC);
			UFLiteralDate beginDate = (UFLiteralDate) getApplicationContext().getAppAttribute(APP_ATTR_BEIGNDATE);
			UFLiteralDate endDate = (UFLiteralDate) getApplicationContext().getAppAttribute(APP_ATTR_ENDDATE);
			UFBoolean onlyShowException = (UFBoolean) getApplicationContext().getAppAttribute(APP_ATTR_ONLYSHOWEXCEPTION);
			if (onlyShowException == null) {
				onlyShowException = UFBoolean.FALSE;
			}
			loadMachineDatas(dsMachine, beginDate, endDate, onlyShowException.booleanValue(), true,pk_psndoc);
		} else {// 初始化操作
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}
	
	/**
	 * 手工考勤数据集加载操作
	 * 
	 * @param dataLoadEvent
	 */
	public void dsManualData_onDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset dsManual = dataLoadEvent.getSource();
		
		String pk_psndoc = (String) getApplicationContext().getAppAttribute(APP_ATTR_PSNDOC);
		UFLiteralDate beginDate = (UFLiteralDate) getApplicationContext().getAppAttribute(APP_ATTR_BEIGNDATE);
		UFLiteralDate endDate = (UFLiteralDate) getApplicationContext().getAppAttribute(APP_ATTR_ENDDATE);
		UFBoolean onlyShowException = (UFBoolean) getApplicationContext().getAppAttribute(APP_ATTR_ONLYSHOWEXCEPTION);
		if (onlyShowException == null) {
			onlyShowException = UFBoolean.FALSE;
		}
		loadManualDatas(dsManual, beginDate, endDate, onlyShowException.booleanValue());
	}

	/**
	 * 加载手工考勤数据
	 * 
	 * @param dsManual
	 */
	private void loadManualDatas(Dataset dsManual, UFLiteralDate beginDate, UFLiteralDate endDate, boolean onlyShowException) {
		if (beginDate != null && endDate != null) {
			String pk_psndoc = SessionUtil.getPk_psndoc();
			String pk_org = SessionUtil.getHROrg();
			LateEarlyVO[] manualVOs = null;
			try {
				ILateEarlyQueryMaintain leq = ServiceLocator.lookup(ILateEarlyQueryMaintain.class);
				manualVOs = leq.queryByPsn(pk_org,pk_psndoc, beginDate, endDate, onlyShowException);
			} catch (HrssException e) {
				e.alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			LateEarlyVO[] curPageManualVOs = DatasetUtil.paginationMethod(dsManual, manualVOs);
			new SuperVO2DatasetSerializer().serialize(curPageManualVOs, dsManual, Row.STATE_NORMAL);
			// 设置表格
			GridComp gridManual = (GridComp) getCurrentView().getViewComponents().getComponent(TimeDataForEmpPageModel.COMP_GRID_MANUAL_DATA);
			TimeDataUtil.adjustWorkTimeColumn(manualVOs, gridManual);
			// 为数据填充颜色
			getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_COLOR_MAP, TimeDataForEmpPageModel.getTimeDataColorInJSON(null, curPageManualVOs));
			getApplicationContext().addBeforeExecScript("setColorMap();");
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
		FromWhereSQLImpl whereSql = (FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL); // 获得自定义条件
		if (whereSql == null) {
			return;
		}
		

		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
		// 员工信息
				String pk_psndoc = selfDefMap.get(TimeDataForEmpPageModel.FIELD_MACHINE_PERSON_PK);
	
				if (pk_psndoc == null) { //如果为空的话,则查询自己的出勤数据
					pk_psndoc = SessionUtil.getPk_psndoc();
				}
		// 开始日期
		UFLiteralDate beginDate = null;
		String strBeginDate = selfDefMap.get(TimeDataForEmpPageModel.FIELD_BEGIN_DATE);
		if (!StringUtils.isEmpty(strBeginDate)) {
			beginDate = new UFLiteralDate(strBeginDate);
		}
		if (beginDate == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0168")/* @res "查询失败" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0009")/*
																												 * @
																												 * res
																												 * "开始日期不能为空！"
																												 */);
		}
		// 结束日期
		UFLiteralDate endDate = null;
		String strEndDate = selfDefMap.get(TimeDataForEmpPageModel.FIELD_END_DATE);
		if (!StringUtils.isEmpty(strEndDate)) {
			endDate = new UFLiteralDate(strEndDate);
		}
		if (endDate == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0168")/* @res "查询失败" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0010")/*
																												 * @
																												 * res
																												 * "结束日期不能为空！"
																												 */);
		}
		if (beginDate.after(endDate)) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0168")/* @res "查询失败" */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0019")/*
																												 * @
																												 * res
																												 * "开始日期不能晚于结束日期！"
																												 */);
		}
		// 仅查询异常数据
		UFBoolean onlyShowException = UFBoolean.valueOf(selfDefMap.get(TimeDataForEmpPageModel.FIELD_ONLY_SHOW_EXCEPTION));
		if (onlyShowException == null) {
			onlyShowException = UFBoolean.FALSE;
		}
		
		getApplicationContext().addAppAttribute(APP_ATTR_PSNDOC, pk_psndoc);
		getApplicationContext().addAppAttribute(APP_ATTR_BEIGNDATE, beginDate);
		getApplicationContext().addAppAttribute(APP_ATTR_ENDDATE, endDate);
		getApplicationContext().addAppAttribute(APP_ATTR_ONLYSHOWEXCEPTION, onlyShowException);
		
		Dataset dsMachine = getCurrentView().getViewModels().getDataset(TimeDataForEmpPageModel.DATASET_MACHINE);
		DatasetUtil.clearData(dsMachine);
		getApplicationContext().addBeforeExecScript("window.colorMap = null");
		dsMachine.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		loadMachineDatas(dsMachine, beginDate, endDate, onlyShowException.booleanValue(), false, pk_psndoc);
		ButtonStateManager.updateButtons();
	}

	/**
	 * 加载数据方法
	 * 
	 * @param beginDate
	 * @param endDate
	 */
	/*private void loadMachineDatas(Dataset dsMachine, UFLiteralDate beginDate, UFLiteralDate endDate, boolean onlyShowException, boolean isPagination) {
		if (beginDate != null && endDate != null) {
			String pk_psndoc = SessionUtil.getPk_psndoc();
			String pk_org = SessionUtil.getHROrg();
			// 机器考勤数据
			TimeDataVO[] machineVOs = null;
			try {
				ITimeDataQueryMaintain tdq = ServiceLocator.lookup(ITimeDataQueryMaintain.class);
				machineVOs = tdq.queryByPsn(pk_org,pk_psndoc, beginDate, endDate, onlyShowException);
				ITBMPsndocQueryService tbmPsndocQueryService = ServiceLocator.lookup(ITBMPsndocQueryService.class);
				TBMPsndocVO tbmPsndocVO = tbmPsndocQueryService.queryLatestByPsndocDate(pk_org, pk_psndoc, beginDate, endDate);
				if (!isPagination) { // 初始化/查询操作
					ILateEarlyQueryMaintain leq = ServiceLocator.lookup(ILateEarlyQueryMaintain.class);
					LateEarlyVO[] manualVOs = leq.queryByPsn(pk_org,pk_psndoc, beginDate, endDate, onlyShowException);
					// 设置页签的显示
					int itemIndex = setTabLayoutDisplay(getLifeCycleContext(), machineVOs, manualVOs, tbmPsndocVO == null ? 0 : tbmPsndocVO.getTbm_prop());
					if (ITEM_INDEX_MANUAL == itemIndex) {
						Dataset dsManual = getCurrentView().getViewModels().getDataset(TimeDataForEmpPageModel.DATASET_MANUAL);
						dsManual.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
						DatasetUtil.clearData(dsManual);
						//当前页数据
						LateEarlyVO[] curPageManualVOs = DatasetUtil.paginationMethod(dsManual, manualVOs);
						new SuperVO2DatasetSerializer().serialize(curPageManualVOs, dsManual, Row.STATE_NORMAL);
						// 设置表格
						GridComp gridManual = (GridComp) getCurrentView().getViewComponents().getComponent(TimeDataForEmpPageModel.COMP_GRID_MANUAL_DATA);
						TimeDataUtil.adjustWorkTimeColumn(manualVOs, gridManual);
						// 为数据填充颜色
						getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_COLOR_MAP, TimeDataForEmpPageModel.getTimeDataColorInJSON(null, curPageManualVOs));
						getApplicationContext().addBeforeExecScript("setColorMap();");
					}
				}
			} catch (HrssException e) {
				e.alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			
			TimeDataVO[] curPageMachineVOs = DatasetUtil.paginationMethod(dsMachine, machineVOs);
			new SuperVO2DatasetSerializer().serialize(curPageMachineVOs, dsMachine, Row.STATE_NORMAL);
			// 设置表格
			GridComp gridMachine = (GridComp) getCurrentView().getViewComponents().getComponent(TimeDataForEmpPageModel.COMP_GRID_MACHINE_DATA);
			TimeDataUtil.adjustWorkTimeColumn(machineVOs, gridMachine);
			// 设置数据集
			if (machineVOs == null || machineVOs.length == 0) {
				return;
			}
			// 为数据填充颜色
			getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_COLOR_MAP, TimeDataForEmpPageModel.getTimeDataColorInJSON(curPageMachineVOs, null));
			getApplicationContext().addBeforeExecScript("setColorMap();");
		}
	}*/
	/**
	 * 加载数据方法-带有人员查询
	 * Ares 2018-6-22 20:56:45 
	 * @param beginDate
	 * @param endDate
	 */
	private void loadMachineDatas(Dataset dsMachine, UFLiteralDate beginDate, UFLiteralDate endDate, boolean onlyShowException, boolean isPagination,String pkpsndoc) {
		if (beginDate != null && endDate != null) {
			String pk_psndoc = pkpsndoc;
			String pk_org = SessionUtil.getHROrg();
			// 机器考勤数据
			TimeDataVO[] machineVOs = null;
			try {
				ITimeDataQueryMaintain tdq = ServiceLocator.lookup(ITimeDataQueryMaintain.class);
				machineVOs = tdq.queryByPsn(pk_org,pk_psndoc, beginDate, endDate, onlyShowException);
				ITBMPsndocQueryService tbmPsndocQueryService = ServiceLocator.lookup(ITBMPsndocQueryService.class);
				TBMPsndocVO tbmPsndocVO = tbmPsndocQueryService.queryLatestByPsndocDate(pk_org, pk_psndoc, beginDate, endDate);
				if (!isPagination) { // 初始化/查询操作
					ILateEarlyQueryMaintain leq = ServiceLocator.lookup(ILateEarlyQueryMaintain.class);
					LateEarlyVO[] manualVOs = leq.queryByPsn(pk_org,pk_psndoc, beginDate, endDate, onlyShowException);
					// 设置页签的显示
					int itemIndex = setTabLayoutDisplay(getLifeCycleContext(), machineVOs, manualVOs, tbmPsndocVO == null ? 0 : tbmPsndocVO.getTbm_prop());
					if (ITEM_INDEX_MANUAL == itemIndex) {
						Dataset dsManual = getCurrentView().getViewModels().getDataset(TimeDataForEmpPageModel.DATASET_MANUAL);
						dsManual.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
						DatasetUtil.clearData(dsManual);
						//当前页数据
						LateEarlyVO[] curPageManualVOs = DatasetUtil.paginationMethod(dsManual, manualVOs);
						new SuperVO2DatasetSerializer().serialize(curPageManualVOs, dsManual, Row.STATE_NORMAL);
						// 设置表格
						GridComp gridManual = (GridComp) getCurrentView().getViewComponents().getComponent(TimeDataForEmpPageModel.COMP_GRID_MANUAL_DATA);
						TimeDataUtil.adjustWorkTimeColumn(manualVOs, gridManual);
						// 为数据填充颜色
						getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_COLOR_MAP, TimeDataForEmpPageModel.getTimeDataColorInJSON(null, curPageManualVOs));
						getApplicationContext().addBeforeExecScript("setColorMap();");
					}
				}
			} catch (HrssException e) {
				e.alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			
			TimeDataVO[] curPageMachineVOs = DatasetUtil.paginationMethod(dsMachine, machineVOs);
			new SuperVO2DatasetSerializer().serialize(curPageMachineVOs, dsMachine, Row.STATE_NORMAL);
			// 设置表格
			GridComp gridMachine = (GridComp) getCurrentView().getViewComponents().getComponent(TimeDataForEmpPageModel.COMP_GRID_MACHINE_DATA);
			TimeDataUtil.adjustWorkTimeColumn(machineVOs, gridMachine);
			// 设置数据集
			if (machineVOs == null || machineVOs.length == 0) {
				return;
			}
			// 为数据填充颜色
			getApplicationContext().getClientSession().setAttribute(TimeDataForEmpPageModel.CSES_COLOR_MAP, TimeDataForEmpPageModel.getTimeDataColorInJSON(curPageMachineVOs, null));
			getApplicationContext().addBeforeExecScript("setColorMap();");
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
	 * 设置页签的显示.<br/>
	 * 
	 * @param ctx
	 *            页面上下文
	 * @param viewMain
	 * @param tvos
	 *            机器考勤数据
	 * @param lvos
	 *            手工考勤数据
	 */
	private int setTabLayoutDisplay(AppLifeCycleContext ctx, TimeDataVO[] tvos, LateEarlyVO[] lvos, int tbm_prop) {

		UIMeta um = (UIMeta) ctx.getViewContext().getUIMeta();
		UITabComp tabComp = (UITabComp) um.findChildById(TimeDataUtil.TAB_TIME_DATA);
		List<UILayoutPanel> itemList = tabComp.getPanelList();

		// 机器考勤Item
		UITabItem machinItem = (UITabItem) itemList.get(ITEM_INDEX_MACHINE);
		// 手工考勤Item
		UITabItem manualItem = (UITabItem) itemList.get(ITEM_INDEX_MANUAL);
		if (null != tvos && tvos.length > 0) {
			machinItem.setVisible(true);
		} else {
			if (null != lvos && lvos.length > 0) {// 无机器考勤且有手工考勤，则隐藏机器考勤页签
				machinItem.setVisible(false);
			} else {
				if (tbm_prop == TbmPropEnum.MANUAL_CHECK.toIntValue()) {
					machinItem.setVisible(false); // 无机器考勤、无手工考勤且考勤档案考勤方式为手工考勤，则隐藏机器考勤页签
				} else {
					machinItem.setVisible(true);// 无机器考勤、无手工考勤且考勤档案考勤方式不为手工考勤，则显示机器考勤页签
				}
			}
		}
		if (null != lvos && lvos.length > 0) {
			manualItem.setVisible(true);
		} else {
			if (tbm_prop == TbmPropEnum.MANUAL_CHECK.toIntValue()) {
				manualItem.setVisible(true); // 无机器考勤、无手工考勤且考勤档案考勤方式为手工考勤，则显示手工考勤页签
			} else {
				manualItem.setVisible(false);// 无机器考勤、无手工考勤且考勤档案考勤方式不为手工考勤，则隐藏手工考勤页签
			}
		}
		// 1.无机器考勤且无手工考勤的情况，显示原来的页签
		// 2.无机器考勤且有手工考勤的情况，显示手工考勤页签
		// 3.其他情况，显示机器考勤页签
		if (ArrayUtils.isEmpty(tvos) && ArrayUtils.isEmpty(lvos)) {
			return Integer.valueOf(tabComp.getCurrentItem()).intValue();
		} else if (ArrayUtils.isEmpty(tvos) && !ArrayUtils.isEmpty(lvos)) {
			tabComp.setCurrentItem(String.valueOf(ITEM_INDEX_MANUAL));
			return ITEM_INDEX_MANUAL;
		} else {
			tabComp.setCurrentItem(String.valueOf(ITEM_INDEX_MACHINE));
			return ITEM_INDEX_MACHINE;
		}
	}

	/**
	 * 机器考勤和手工考勤的页签切换事件
	 * 
	 * @param tabEvent
	 */
	public void afterActivedTabItemChange(TabEvent tabEvent) {
		if (String.valueOf(ITEM_INDEX_MANUAL).equals(tabEvent.getSource().getCurrentItem())) {
			Dataset dsManual = getCurrentView().getViewModels().getDataset(TimeDataForEmpPageModel.DATASET_MANUAL);
			dsManual.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
			UFLiteralDate beginDate = (UFLiteralDate) getApplicationContext().getAppAttribute(APP_ATTR_BEIGNDATE);
			UFLiteralDate endDate = (UFLiteralDate) getApplicationContext().getAppAttribute(APP_ATTR_ENDDATE);
			UFBoolean onlyShowException = (UFBoolean) getApplicationContext().getAppAttribute(APP_ATTR_ONLYSHOWEXCEPTION);
			if (onlyShowException == null) {
				onlyShowException = UFBoolean.FALSE;
			}
			loadManualDatas(dsManual, beginDate, endDate, onlyShowException.booleanValue());
		}
		ButtonStateManager.updateButtons();
	}

	/**
	 * 自动生成签卡单据
	 * 
	 * @param mouseEvent
	 */
	public void onSignCard(MouseEvent<MenuItem> mouseEvent) {
								   
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		Dataset dsMachine = viewMain.getViewModels().getDataset(TimeDataForEmpPageModel.DATASET_MACHINE);
		// 当前用户
		//String pk_psndoc = SessionUtil.getPk_psndoc();
		//签卡用户为最近查询过的用户,否则为当前用户(店长代员工签卡)2018-6-23 15:59:26 Ares 
		String pk_psndoc = (String) getApplicationContext().getAppAttribute(APP_ATTR_PSNDOC);
		if(pk_psndoc==null){
			pk_psndoc = SessionUtil.getPk_psndoc();
		}
		//end
		// 最新考勤档案
		TBMPsndocVO tbmPsndocVO = TBMPsndocUtil.getTBMPsndoc(pk_psndoc, new UFDateTime());
		if(tbmPsndocVO == null){
			CommonUtil.showErrorDialog(ResHelper.getString("c_ta-res","0c_ta-res0178")/*@res "签卡失败"*/,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0176")/** @res "你的考勤档案已结束！"*/);
		}
		
		Row[] rows = dsMachine.getSelectedRows();
		if (ArrayUtils.isEmpty(rows)) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0178")/*@res "签卡失败"*/
					, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0113")/*
																															 * @
																															 * res
																															 * "请首先勾选需要签卡的出勤记录！"
																															 */);
		}
		
		// 选定的签卡日期
		UFLiteralDate[] calendars = new UFLiteralDate[rows.length];
		for (int i = 0; i < rows.length; i++) {
			calendars[i] = (UFLiteralDate) rows[i].getValue(dsMachine.nameToIndex(TimeDataForEmpPageModel.FIELD_MACHINE_CALENDAR));
		}
		AggSignVO aggVO = null;
		try {
			// 为员工创建多个日期的默认签卡单据
			aggVO = ServiceLocator.lookup(ITimeDataQueryMaintain.class).createSignCard(pk_psndoc, calendars);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		if (null == aggVO || ArrayUtils.isEmpty(aggVO.getSignbVOs())) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0178")/*@res "签卡失败"*/
					, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0114")/*
																												 * @
																												 * res
																												 * "所选出勤记录无需签卡！"
																												 */);
		} else {
			getApplicationContext().addAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS, HrssConsts.POPVIEW_OPERATE_AUTO_ADD);
			getApplicationContext().addAppAttribute(SignConsts.SESSION_AUTO_GEN_SIGNB, aggVO);
			CommonUtil.showWindowDialog("SignCardApply", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0115")/*																														 */, "802", "700", null, ApplicationContext.TYPE_DIALOG, false, false);
		}
	}
	/**
	 * 选中行事件
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowSelect(DatasetEvent datasetEvent) {
		ButtonStateManager.updateButtons();
	}
	/**
	 * 取消选中行事件
	 * 
	 * @param datasetEvent
	 */
	public void onAfterRowUnSelect(DatasetEvent datasetEvent) {
		ButtonStateManager.updateButtons();
	}

}