package nc.bs.hrsms.ta.sss.leaveinfo.ctrl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.TextNavigationUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.hrss.ta.common.ctrl.BaseController;
import nc.bs.hrss.ta.leave.ctrl.LeaveApplyView;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveBalanceQueryMaintain;
import nc.itf.ta.ITimeItemQueryMaintain;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.core.util.AppDynamicCompUtil;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.SettlementPeriodEnum;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 员工假期查询
 * 
 * @author qiaoxp
 */
public class LeaveInfoQryViewMain extends BaseController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	public static final String DATASET_LEAVEINFO = "dsLeavebalance";
	public static final String PARAM_CI_YEAR = "ci_year";
	public static final String PARAM_CI_MONTH = "ci_month";
	public static final String PARAM_CI_PK_LEAVETYPE = "ci_pk_leavetype";
	public static final String PARAM_CI_PK_LEAVETYPEUNIT = "ci_pk_leavetype_unit";
	public static final String PARAM_CI_LEAVESETPERIOD = "ci_leavesetperiod";

	/**
	 * 主数据集ID
	 * 
	 * @return
	 */
	private String getDatasetId() {
		return DATASET_LEAVEINFO;
	}

	/**
	 * 数据集加载事件
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onDataLoad_dsLeavebalance(DataLoadEvent dataLoadEvent) throws BusinessException {
		Dataset ds = dataLoadEvent.getSource();
		String pk_leavetype = (String) getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPE);
		if (!StringUtils.isEmpty(pk_leavetype)) { // 分页操作和休假类型切换事件
			// 获得查询条件
			FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL);
			String year = (String) getApplicationContext().getAppAttribute(PARAM_CI_YEAR);
			String month = (String) getApplicationContext().getAppAttribute(PARAM_CI_MONTH);
			String leavetypeunit = String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPEUNIT));
			String leavesetperiod = String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_LEAVESETPERIOD));
			// 分页数据查询
			loadLeaveInfoData(ds, pk_leavetype, Integer.valueOf(leavetypeunit), Integer.valueOf(leavesetperiod), year, month, fromWhereSQL);
		} else {// 初始化操作
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
		TBMPsndocUtil.checkTimeRuleVO();
		
		if (keys == null || keys.size() == 0) {
			return;
		}
		nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
		// 查询条件-FromWhereSQL
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
		// 获得自定义条件
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
		// 查询条件-年度
		String year = selfDefMap.get(LeaveInfoQryQueryCtrl.FS_TBMYEAR);
		// 查询条件-期间
		// String month = (String)
		// selfDefMap.get(LeaveInfoQryQueryCtrl.FS_TBMMONTH);
		String month = null;
		String name = null;
		name = selfDefMap.get("pk_psndoc_name");
		if(null != name){
			fromWhereSQL.setWhere(" tbm_psndoc.pk_psndoc in ( select bd_psndoc.pk_psndoc from bd_psndoc where name like '%"+ name +"%') ");
		}else{
			fromWhereSQL.setWhere(" 1=1");
		}
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row row = dsSearch.getSelectedRow();
			month = row.getString(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMMONTH));
		}
		// 重置页序号
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		// 管理部门对应的HR组织主键
		String pk_hr_org = SessionUtil.getHROrg();
		if(StringUtils.isEmpty(pk_hr_org)){
			return;
		}
		String pk_leavetype = (String) getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPE);
		Integer leavetypeunit = null;
		Integer leavesetperiod = null;
		if (StringUtils.isEmpty(pk_leavetype)) { // 初始化查询
			// 设置休假类别显示Panel
			TimeItemCopyVO leaveTypesVO = loadLeaveTypeNavigation(pk_hr_org);
			if (leaveTypesVO == null) {
				DatasetUtil.clearData(ds);
				return;
			}
			pk_leavetype = leaveTypesVO.getPk_timeitem();
			leavetypeunit = leaveTypesVO.getTimeitemunit();
			leavesetperiod = leaveTypesVO.getLeavesetperiod();
			getApplicationContext().addAppAttribute(PARAM_CI_PK_LEAVETYPE, pk_leavetype);
			getApplicationContext().addAppAttribute(PARAM_CI_PK_LEAVETYPEUNIT, leavetypeunit);
			getApplicationContext().addAppAttribute(PARAM_CI_LEAVESETPERIOD, leavesetperiod);
		} else {// 休假类型切换事件,点击查询按钮
			leavetypeunit = Integer.valueOf(String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPEUNIT)));
			leavesetperiod = Integer.valueOf(String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_LEAVESETPERIOD)));
		}

		// 保存查询条件
		getApplicationContext().addAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL, fromWhereSQL);
		getApplicationContext().addAppAttribute(PARAM_CI_YEAR, year);
		getApplicationContext().addAppAttribute(PARAM_CI_MONTH, month);

		// 设置Form中期间的可编辑/不可编辑
		ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		DatasetUtil.clearData(ds);
		loadLeaveInfoData(ds, pk_leavetype, leavetypeunit, leavesetperiod, year, month, fromWhereSQL);
	}

	/**
	 * 休假类别的点击事件
	 * 
	 * @param scriptEvent
	 */
	public void onLeaveTypeChanged(ScriptEvent scriptEvent) {
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		// 选中的休假类型主键
		String pk_leavetype = getLifeCycleContext().getParameter(PARAM_CI_PK_LEAVETYPE);
		// 选中的休假类型主键
		String unit = getLifeCycleContext().getParameter(PARAM_CI_PK_LEAVETYPEUNIT);
		// 选中的休假类型结算方式
		String leavesetperiod = getLifeCycleContext().getParameter(PARAM_CI_LEAVESETPERIOD);

		// 保存查询条件
		getApplicationContext().addAppAttribute(PARAM_CI_PK_LEAVETYPE, pk_leavetype);
		getApplicationContext().addAppAttribute(PARAM_CI_PK_LEAVETYPEUNIT, unit);
		getApplicationContext().addAppAttribute(PARAM_CI_LEAVESETPERIOD, leavesetperiod);
		new AppDynamicCompUtil(getLifeCycleContext().getApplicationContext(), getLifeCycleContext().getViewContext()).refreshDataset(ds);
	}

	/**
	 * 拼接休假类别显示JSON
	 * 
	 * @param pk_hr_org
	 * @return
	 */
	private TimeItemCopyVO loadLeaveTypeNavigation(String pk_hr_org) {
		ApplicationContext appCxt = getApplicationContext();
		TimeItemCopyVO leaveTypesVO = null;
		if (StringUtils.isEmpty(pk_hr_org)) {
			appCxt.addExecScript("ceateTextNavigation();");
			return null;
		}
		TimeItemCopyVO[] leaveTypesVOs = getLeaveTypes(pk_hr_org);

		StringBuffer jsonBuf = new StringBuffer("");
		if (leaveTypesVOs != null && leaveTypesVOs.length > 0 ) {
			for (int i = 0; i < leaveTypesVOs.length; i++) {
				String LeaveTypeName = MultiLangHelper.getName(leaveTypesVOs[i].toDefVO());
				jsonBuf.append(TextNavigationUtil.buildTextNavgItemJson(i, LeaveTypeName, leaveTypesVOs[i].getPk_timeitem(), String.valueOf(leaveTypesVOs[i].getLeavesetperiod()), String.valueOf(leaveTypesVOs[i].getTimeItemUnit())));
			}
			leaveTypesVO = leaveTypesVOs[0];
			
		}
		appCxt.addExecScript("ceateTextNavigation('{[" + jsonBuf.toString() + "]}');");
		return leaveTypesVO;
	}

	/**
	 * 获得导航页面,休假类别树 员工假期查询页面, pk_org 为当前管理部门所在组织
	 * 
	 * @param pk_org
	 * @return
	 */
	private TimeItemCopyVO[] getLeaveTypes(String pk_org) {
		TimeItemCopyVO[] leaveTypes = null;
		// 排除哺乳假
		String condition = " pk_timeitem in (select pk_timeitem from tbm_timeitem where islactation='N') ";
		try {
			ITimeItemQueryMaintain service = ServiceLocator.lookup(ITimeItemQueryMaintain.class);
			leaveTypes = service.queryLeaveCopyTypesByOrg(pk_org, condition);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return leaveTypes;
	}

	/**
	 * 查询指定类别的假期计算记录.<br>
	 * 用于经理自助<br>
	 * 
	 * @param ds
	 * @param pk_hr_org
	 * @param pk_leavetype
	 * @param unit
	 * @param year
	 * @param month
	 * @param fromWhereSQL
	 */
	private void loadLeaveInfoData(Dataset ds, String pk_leavetype, Integer unit, Integer leavesetperiod, String year, String month, FromWhereSQL fromWhereSQL) {
		setTimeUnitText(unit, leavesetperiod, pk_leavetype);
		// 设置查询Form中期间的可编辑/不可编辑
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		FormComp frmSearch = (FormComp) simpQryView.getViewComponents().getComponent(HrssConsts.FORM_SIMPLE_QUERY);
		FormElement monthElem = frmSearch.getElementById(LeaveInfoQryQueryCtrl.FS_TBMMONTH);
		if (leavesetperiod != null && SettlementPeriodEnum.MONTH.value().equals(leavesetperiod)) {
			if (StringUtils.isEmpty(month)) {
				month = TaAppContextUtil.getLatestPeriodVO().getTimemonth();
			}
			monthElem.setEnabled(true);
		} else {
			month = null;
			monthElem.setEnabled(false);
		}
		LeaveBalanceVO[] vos = null;
		// 管理部门对应的HR组织主键
		String pk_hr_org = SessionUtil.getHROrg();
		String psnScopeSqlPart = null;
		try {
			psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
		} catch (BusinessException e) {
//			new HrssException(e).deal();
		}
		try {
			ILeaveBalanceQueryMaintain queryServ = ServiceLocator.lookup(ILeaveBalanceQueryMaintain.class);
			if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
				((FromWhereSQLImpl) fromWhereSQL).setWhere(fromWhereSQL.getWhere() + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
			}
			vos = queryServ.queryByCondition(pk_hr_org, pk_leavetype, year, month, fromWhereSQL, SessionUtil.getPk_mng_dept(), SessionUtil.isIncludeSubDept());
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		if (vos != null && vos.length > 0) {
			new SuperVO2DatasetSerializer().serialize(DatasetUtil.paginationMethod(ds, vos), ds, Row.STATE_NORMAL);
			ds.setRowSelectIndex(0);
		}
	}

	/**
	 * 设置考勤单位的相关字段显示
	 * 
	 * @param timeitemunit
	 */
	private void setTimeUnitText(Integer timeitemunit, Integer leavesetperiod, String pk_leavetype) {
		
		// 片段
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		GridComp grid = (GridComp) viewMain.getViewComponents().getComponent("tblLeavebalance");
		// 根据休假类别设置自助是否显示享有字段
		TimeItemCopyVO timeItemCopyVO = LeaveApplyView.getTimeItemCopyVO(SessionUtil.getPsndocVO().getPsnJobVO().getPk_hrorg(), pk_leavetype);
		UFBoolean ishrssshow =  timeItemCopyVO.getIshrssshow();
		GridColumn lastdayorhour = (GridColumn) grid.getColumnById(LeaveBalanceVO.LASTDAYORHOUR);
		GridColumn curdayorhour = (GridColumn) grid.getColumnById(LeaveBalanceVO.CURDAYORHOUR);
		GridColumn realdayorhour = (GridColumn) grid.getColumnById(LeaveBalanceVO.REALDAYORHOUR);
		GridColumn restdayorhour = (GridColumn) grid.getColumnById(LeaveBalanceVO.RESTDAYORHOUR);
		GridColumn usefulrestdayorhour = (GridColumn) grid.getColumnById(LeaveBalanceVO.USEFULRESTDAYORHOUR);
		GridColumn freezedayorhour = (GridColumn) grid.getColumnById(LeaveBalanceVO.FREEZEDAYORHOUR);
		if(ishrssshow != null && ishrssshow.booleanValue()){
			lastdayorhour.setVisible(true);
			curdayorhour.setVisible(true);
			realdayorhour.setVisible(true);
			restdayorhour.setVisible(true);
			usefulrestdayorhour.setVisible(true);
			freezedayorhour.setVisible(true);
		}else{
			lastdayorhour.setVisible(false);
			curdayorhour.setVisible(false);
			realdayorhour.setVisible(false);
			restdayorhour.setVisible(false);
			usefulrestdayorhour.setVisible(false);
			freezedayorhour.setVisible(false);
		}
		
		// 上期结余
		GridColumn column = (GridColumn) grid.getColumnById(LeaveBalanceVO.LASTDAYORHOUR);
		String text = null;
		if (TimeItemCopyVO.LEAVESETPERIOD_YEAR == leavesetperiod) {
			text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0080")/*
																									 * @
																									 * res
																									 * "上年结余"
																									 */;
		} else {
			text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0081")/*
																									 * @
																									 * res
																									 * "上期结余"
																									 */;
		}
		setText(timeitemunit, column, text);
		// 根据结算方式控制上期结余是否显示
		if(timeItemCopyVO.getLeavesettlement() == TimeItemCopyVO.LEAVESETTLEMENT_NEXT){
			column.setVisible(true);
		}else{
			column.setVisible(false);
		}
		
		//调整时长
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0191")/** @ res"调整时长"*/;
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.CHANGELENGTH);
		setText(timeitemunit, column, text);
		
		// 享有
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.CURDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0082")/*
																								 * @
																								 * res
																								 * "享有"
																								 */;
		setText(timeitemunit, column, text);
		// 实际享有
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.REALDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0083")/*
																								 * @
																								 * res
																								 * "实际享有"
																								 */;
		setText(timeitemunit, column, text);
		// 已休
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.YIDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0084")/*
																								 * @
																								 * res
																								 * "已休"
																								 */;
		setText(timeitemunit, column, text);
		// 结余
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.RESTDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0085")/*
																								 * @
																								 * res
																								 * "结余"
																								 */;
		setText(timeitemunit, column, text);
		// 冻结
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.FREEZEDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000030")/*
																								 * @
																								 * res
																								 * "冻结"
																								 */;
		setText(timeitemunit, column, text);
		// 可用
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.USEFULRESTDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0086")/*
																								 * @
																								 * res
																								 * "可用"
																								 */;
		setText(timeitemunit, column, text);

	}

	/**
	 * 设置考勤单位的相关字段显示
	 * 
	 * @param timeitemunit
	 * @param column
	 * @param text
	 */
	private void setText(Integer timeitemunit, GridColumn column, String text) {
		if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {// 天
			column.setText(text + "(" + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0079") + ")"/*
																													 * @
																													 * res
																													 * "天数"
																													 */);
		} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// 小时
			column.setText(text + "(" + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0078")+ ")"/*
																													 * @
																													 * res
																													 * "小时"
																													 */);
		} else {
			column.setText(text);
		}
	}

	/**
	 * 管理部门变更
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
		TaAppContextUtil.addTaAppContext();
		// 当前考勤期间
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
		// 组织下考勤年度和期间
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			String old_year = null;
			String accyear = null;
			if (dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR) > -1) {
				old_year = selRow.getString(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR));
				if (latestPeriodVO != null) {
					selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR), latestPeriodVO.getTimeyear());
				} else {
					if (periodMap != null && periodMap.size() > 0) {
						String[] years = periodMap.keySet().toArray(new String[0]);
						if (!ArrayUtils.isEmpty(years)) {
							accyear = years[0];
							selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR), accyear);
						}
					} else {
						selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR), null);
					}
				}
				accyear = selRow.getString(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMYEAR));
			}
			// 部门发生变更、年度未发生变更
			if (accyear != null && accyear.equals(old_year)) {
				String[] years = null;
				ComboData yearData = simpQryView.getViewModels().getComboData("comb_tbmyear_value");
				if (periodMap != null && periodMap.size() > 0) {
					years = periodMap.keySet().toArray(new String[0]);
					if (years != null && years.length > 1) {
						Arrays.sort(years);
						Collections.reverse(Arrays.asList(years));
					}
				}
				ComboDataUtil.addCombItemsAfterClean(yearData, years);

				ComboData monthData = simpQryView.getViewModels().getComboData("comb_tbmmonth_value");
				String[] months = null;
				if (periodMap != null && periodMap.size() > 0) {
					months = periodMap.get(accyear);
				}
				ComboDataUtil.addCombItemsAfterClean(monthData, months);
				if (ArrayUtils.isEmpty(months)) {
					selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMMONTH), null);
					return;
				}
				if (latestPeriodVO != null && !StringUtils.isEmpty(latestPeriodVO.getTimeyear()) && latestPeriodVO.getAccyear().equals(accyear)) {
					String accmonth = TaAppContextUtil.getLatestPeriodVO().getTimemonth();
					for (String month : months) {
						if (month.equals(accmonth)) {
							selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMMONTH), accmonth);
							break;
						}
					}
				} else {
					selRow.setValue(dsSearch.nameToIndex(LeaveInfoQryQueryCtrl.FS_TBMMONTH), months[0]);
				}
			} else if (accyear == null && old_year == null) {
				String[] years = null;
				ComboData yearData = simpQryView.getViewModels().getComboData("comb_tbmyear_value");
				if (periodMap != null && periodMap.size() > 0) {
					years = periodMap.keySet().toArray(new String[0]);
					if (years != null && years.length > 1) {
						Arrays.sort(years);
						Collections.reverse(Arrays.asList(years));
					}
				}
				ComboDataUtil.addCombItemsAfterClean(yearData, years);
			}
		}

		getApplicationContext().addAppAttribute(PARAM_CI_PK_LEAVETYPE, null);
		// 清除休假类别的导航数据
		ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
		appCxt.addExecScript("destroyTextNavigation();");
		// 清除休假类别的数据
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		DatasetUtil.clearData(ds);

		// 初始化操作
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	
	/**
	 * 浏览休假信息菜单操作
	 * 
	 * @param scriptEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {

		// 主键
		String dsId = getLifeCycleContext().getParameter("dsMain_id");
		if (StringUtils.isEmpty(dsId)) {
			return;
		}
		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset ds = view.getViewModels().getDataset(dsId);
		if (ds == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0166"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0088")/*
																															 * @
																															 * res
																															 * "请先选择一条需要浏览的假期记录！"
																															 */);
		}
		String rowId = getLifeCycleContext().getParameter("dsMain_rowId");
		Row row = ds.getRowById(rowId);
		if (row == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0166"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0088")/*
																															 * @
																															 * res
																															 * "请先选择一条需要浏览的假期记录！"
																															 */);
		}
		SuperVO[] vos = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds, row);
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		appCtx.addAppAttribute(ViewLeaveDetailViewMain.SESSION_SELECTED_DATAS, vos[0]);
		CommonUtil.showWindowDialog("ViewLeaveQryDetail", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0089")/*
																																		 * @
																																		 * res
																																		 * "休假信息"
																																		 */, "802", "500", null, ApplicationContext.TYPE_DIALOG);
	}
}