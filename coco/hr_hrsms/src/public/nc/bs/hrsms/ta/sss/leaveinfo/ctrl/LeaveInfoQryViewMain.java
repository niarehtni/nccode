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
 * Ա�����ڲ�ѯ
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
	 * �����ݼ�ID
	 * 
	 * @return
	 */
	private String getDatasetId() {
		return DATASET_LEAVEINFO;
	}

	/**
	 * ���ݼ������¼�
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onDataLoad_dsLeavebalance(DataLoadEvent dataLoadEvent) throws BusinessException {
		Dataset ds = dataLoadEvent.getSource();
		String pk_leavetype = (String) getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPE);
		if (!StringUtils.isEmpty(pk_leavetype)) { // ��ҳ�������ݼ������л��¼�
			// ��ò�ѯ����
			FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) getApplicationContext().getAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL);
			String year = (String) getApplicationContext().getAppAttribute(PARAM_CI_YEAR);
			String month = (String) getApplicationContext().getAppAttribute(PARAM_CI_MONTH);
			String leavetypeunit = String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPEUNIT));
			String leavesetperiod = String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_LEAVESETPERIOD));
			// ��ҳ���ݲ�ѯ
			loadLeaveInfoData(ds, pk_leavetype, Integer.valueOf(leavetypeunit), Integer.valueOf(leavesetperiod), year, month, fromWhereSQL);
		} else {// ��ʼ������
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	/**
	 * ���������,ҳ�����¼����¼�
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
		// ��ѯ����-FromWhereSQL
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
		// ����Զ�������
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
		// ��ѯ����-���
		String year = selfDefMap.get(LeaveInfoQryQueryCtrl.FS_TBMYEAR);
		// ��ѯ����-�ڼ�
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
		// ����ҳ���
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		// �����Ŷ�Ӧ��HR��֯����
		String pk_hr_org = SessionUtil.getHROrg();
		if(StringUtils.isEmpty(pk_hr_org)){
			return;
		}
		String pk_leavetype = (String) getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPE);
		Integer leavetypeunit = null;
		Integer leavesetperiod = null;
		if (StringUtils.isEmpty(pk_leavetype)) { // ��ʼ����ѯ
			// �����ݼ������ʾPanel
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
		} else {// �ݼ������л��¼�,�����ѯ��ť
			leavetypeunit = Integer.valueOf(String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_PK_LEAVETYPEUNIT)));
			leavesetperiod = Integer.valueOf(String.valueOf(getApplicationContext().getAppAttribute(PARAM_CI_LEAVESETPERIOD)));
		}

		// �����ѯ����
		getApplicationContext().addAppAttribute(TaApplyConsts.APP_ATTR_FROMWHERESQL, fromWhereSQL);
		getApplicationContext().addAppAttribute(PARAM_CI_YEAR, year);
		getApplicationContext().addAppAttribute(PARAM_CI_MONTH, month);

		// ����Form���ڼ�Ŀɱ༭/���ɱ༭
		ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		DatasetUtil.clearData(ds);
		loadLeaveInfoData(ds, pk_leavetype, leavetypeunit, leavesetperiod, year, month, fromWhereSQL);
	}

	/**
	 * �ݼ����ĵ���¼�
	 * 
	 * @param scriptEvent
	 */
	public void onLeaveTypeChanged(ScriptEvent scriptEvent) {
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		// ѡ�е��ݼ���������
		String pk_leavetype = getLifeCycleContext().getParameter(PARAM_CI_PK_LEAVETYPE);
		// ѡ�е��ݼ���������
		String unit = getLifeCycleContext().getParameter(PARAM_CI_PK_LEAVETYPEUNIT);
		// ѡ�е��ݼ����ͽ��㷽ʽ
		String leavesetperiod = getLifeCycleContext().getParameter(PARAM_CI_LEAVESETPERIOD);

		// �����ѯ����
		getApplicationContext().addAppAttribute(PARAM_CI_PK_LEAVETYPE, pk_leavetype);
		getApplicationContext().addAppAttribute(PARAM_CI_PK_LEAVETYPEUNIT, unit);
		getApplicationContext().addAppAttribute(PARAM_CI_LEAVESETPERIOD, leavesetperiod);
		new AppDynamicCompUtil(getLifeCycleContext().getApplicationContext(), getLifeCycleContext().getViewContext()).refreshDataset(ds);
	}

	/**
	 * ƴ���ݼ������ʾJSON
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
	 * ��õ���ҳ��,�ݼ������ Ա�����ڲ�ѯҳ��, pk_org Ϊ��ǰ������������֯
	 * 
	 * @param pk_org
	 * @return
	 */
	private TimeItemCopyVO[] getLeaveTypes(String pk_org) {
		TimeItemCopyVO[] leaveTypes = null;
		// �ų������
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
	 * ��ѯָ�����ļ��ڼ����¼.<br>
	 * ���ھ�������<br>
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
		// ���ò�ѯForm���ڼ�Ŀɱ༭/���ɱ༭
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
		// �����Ŷ�Ӧ��HR��֯����
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
	 * ���ÿ��ڵ�λ������ֶ���ʾ
	 * 
	 * @param timeitemunit
	 */
	private void setTimeUnitText(Integer timeitemunit, Integer leavesetperiod, String pk_leavetype) {
		
		// Ƭ��
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		GridComp grid = (GridComp) viewMain.getViewComponents().getComponent("tblLeavebalance");
		// �����ݼ�������������Ƿ���ʾ�����ֶ�
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
		
		// ���ڽ���
		GridColumn column = (GridColumn) grid.getColumnById(LeaveBalanceVO.LASTDAYORHOUR);
		String text = null;
		if (TimeItemCopyVO.LEAVESETPERIOD_YEAR == leavesetperiod) {
			text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0080")/*
																									 * @
																									 * res
																									 * "�������"
																									 */;
		} else {
			text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0081")/*
																									 * @
																									 * res
																									 * "���ڽ���"
																									 */;
		}
		setText(timeitemunit, column, text);
		// ���ݽ��㷽ʽ�������ڽ����Ƿ���ʾ
		if(timeItemCopyVO.getLeavesettlement() == TimeItemCopyVO.LEAVESETTLEMENT_NEXT){
			column.setVisible(true);
		}else{
			column.setVisible(false);
		}
		
		//����ʱ��
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0191")/** @ res"����ʱ��"*/;
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.CHANGELENGTH);
		setText(timeitemunit, column, text);
		
		// ����
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.CURDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0082")/*
																								 * @
																								 * res
																								 * "����"
																								 */;
		setText(timeitemunit, column, text);
		// ʵ������
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.REALDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0083")/*
																								 * @
																								 * res
																								 * "ʵ������"
																								 */;
		setText(timeitemunit, column, text);
		// ����
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.YIDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0084")/*
																								 * @
																								 * res
																								 * "����"
																								 */;
		setText(timeitemunit, column, text);
		// ����
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.RESTDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0085")/*
																								 * @
																								 * res
																								 * "����"
																								 */;
		setText(timeitemunit, column, text);
		// ����
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.FREEZEDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000030")/*
																								 * @
																								 * res
																								 * "����"
																								 */;
		setText(timeitemunit, column, text);
		// ����
		column = (GridColumn) grid.getColumnById(LeaveBalanceVO.USEFULRESTDAYORHOUR);
		text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0086")/*
																								 * @
																								 * res
																								 * "����"
																								 */;
		setText(timeitemunit, column, text);

	}

	/**
	 * ���ÿ��ڵ�λ������ֶ���ʾ
	 * 
	 * @param timeitemunit
	 * @param column
	 * @param text
	 */
	private void setText(Integer timeitemunit, GridColumn column, String text) {
		if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {// ��
			column.setText(text + "(" + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0079") + ")"/*
																													 * @
																													 * res
																													 * "����"
																													 */);
		} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// Сʱ
			column.setText(text + "(" + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0078")+ ")"/*
																													 * @
																													 * res
																													 * "Сʱ"
																													 */);
		} else {
			column.setText(text);
		}
	}

	/**
	 * �����ű��
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
		TaAppContextUtil.addTaAppContext();
		// ��ǰ�����ڼ�
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
		// ��֯�¿�����Ⱥ��ڼ�
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
			// ���ŷ�����������δ�������
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
		// ����ݼ����ĵ�������
		ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
		appCxt.addExecScript("destroyTextNavigation();");
		// ����ݼ���������
		Dataset ds = getCurrentView().getViewModels().getDataset(getDatasetId());
		DatasetUtil.clearData(ds);

		// ��ʼ������
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	
	/**
	 * ����ݼ���Ϣ�˵�����
	 * 
	 * @param scriptEvent
	 */
	public void showDetail(ScriptEvent scriptEvent) {

		// ����
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
																															 * "����ѡ��һ����Ҫ����ļ��ڼ�¼��"
																															 */);
		}
		String rowId = getLifeCycleContext().getParameter("dsMain_rowId");
		Row row = ds.getRowById(rowId);
		if (row == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0166"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0088")/*
																															 * @
																															 * res
																															 * "����ѡ��һ����Ҫ����ļ��ڼ�¼��"
																															 */);
		}
		SuperVO[] vos = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds, row);
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		appCtx.addAppAttribute(ViewLeaveDetailViewMain.SESSION_SELECTED_DATAS, vos[0]);
		CommonUtil.showWindowDialog("ViewLeaveQryDetail", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0089")/*
																																		 * @
																																		 * res
																																		 * "�ݼ���Ϣ"
																																		 */, "802", "500", null, ApplicationContext.TYPE_DIALOG);
	}
}