package nc.bs.hrsms.ta.sss.monthreport.ctrl;

import java.awt.MenuItem;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import nc.bs.hrsms.ta.sss.leaveinfo.ctrl.LeaveInfoQryQueryCtrl;
import nc.bs.hrsms.ta.sss.monthreport.MonthReportUtils;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.monthreport.MonthReportConsts;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author liuhongd
 */
public class MonthReportForCleViewMain implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	// �����±����ݼ�
	public static final String DATASET_ID = "dsMonthReport";
	// �±�����Ƭ��ID
	public static final String PAGE_MTH_RPT_DTL_WIDGET = "MonthReportDetail";
	// �±�Ƭ��ID
	public static final String PAGE_MTH_RPT_WIDGET = "MonthReport";
	// δ����ͳ��
	public static final String PAGE_UNGENERATE_PSN = "unGeneratePsn";
	// ��ѯ����������ѯ�����ݼ�¼
	public static final String PARAM_ID_NODATA = "showNoDataRecord";
	// ��ѯ�����������
	public static final String PARAM_ID_YEAR = "tbmyear";
	// ��ѯ���������ڼ�
	public static final String PARAM_ID_MONTH = "tbmmonth";
	// ��ѯ����������Ա
	public static final String PARAM_ID_PSNDOC = "psndoc";
	// ��ѯ��������FWSQL
	public static final String PARAM_ID_FWSQL = "fromWhereSql";
	// ��ѯ�����ֶ�.���
	public static final String PARAM_ID_TBMYEAR = "tbmyear";
	// ��ѯ�����ֶ�.�ڼ�
	public static final String PARAM_ID_TBMMONTH = "tbmmonth";
	// ��ѯ�����ֶ�.��Ա
	public static final String PARAM_ID_PK_PSNDOC = "pk_psndoc";
	// ��ѯ�����ֶ�.����
	public static final String PARAM_ID_DEPT = "pk_dept";
	// ����
	public static final String PARAM_ID_DEPTNAME = "deptName";

	public static final String PLUGIN_PARAM_ID = "qryout";
	public static final String PAGE_QUERY_WIDGET = "pubview_simplequery";
	// ����ڼ��Ƿ�仯�Ĳ���
	public static final String SESSION_DATE_CHANGE = "isDateChange";

	/**
	 * �������ݼ�
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onMonthReportDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException {
		Dataset ds = dataLoadEvent.getSource();
		if (isPagination(ds)) { // ��ҳ����
			boolean containsSubDepts = SessionUtil.isIncludeSubDept();
			String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_DEPT);
			String tbmyear = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_TBMYEAR);
			String tbmmonth = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_TBMMONTH);
			UFBoolean showNoDataRecord = (UFBoolean) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_NODATA);
			boolean noDataRecord = showNoDataRecord.booleanValue();
			FromWhereSQLImpl fromWhereSql = (FromWhereSQLImpl) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_FWSQL);
			MonthReportUtils.resetData(ds, pk_dept, fromWhereSql, tbmyear, tbmmonth, containsSubDepts, noDataRecord);
			ButtonStateManager.updateButtons();
		} else {
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
		
	}

	/**
	 * ��ҳ������־
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}

	/**
	 * �����ű��
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
		ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
		appCxt.addAppAttribute(SESSION_DATE_CHANGE, UFBoolean.TRUE);

		TaAppContextUtil.addTaAppContext();
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset(HrssConsts.DS_SIMPLE_QUERY);
		// ��ǰ�����ڼ�
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
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
					String accmonth = latestPeriodVO.getTimemonth();
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

		// ��ʼ������
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * ����
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		TBMPsndocUtil.checkTimeRuleVO();
		
		LfwView viewMain = getCurrentActiveView();
		if (viewMain == null) {
			return;
		}
		Dataset ds = viewMain.getViewModels().getDataset(MonthReportConsts.DATASET_MONTH_REPORT);
		if (ds == null) {
			return;
		}
		// ������ݼ�
		DatasetUtil.clearData(ds);
		ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		if (keys == null || keys.size() == 0) {
			return;
		}

		// ������
		String pk_dept = SessionUtil.getPk_mng_dept();
		// �Ƿ�����¼�����
		boolean containsSubDepts = SessionUtil.isIncludeSubDept();

		nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys
				.get(HrssConsts.PO_SEARCH_WHERESQL);
		// ��ѯ����-FromWhereSQL
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);
		String psnScopeSqlPart;
		try {
			psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
			if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
				(fromWhereSQL).setWhere(" tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
			}
		} catch (BusinessException e) {
//			new HrssException(e).deal();
		}
		// ���
		String year = null;
		// �ڼ�
		String month =  null;
		// ��ѯ�����ݼ�¼
		UFBoolean showNoDataRecord = null;
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset(HrssConsts.DS_SIMPLE_QUERY);
		if (dsSearch != null) {
			Row row = dsSearch.getSelectedRow();
			year = row.getString(dsSearch.nameToIndex(PARAM_ID_TBMYEAR));
			month = row.getString(dsSearch.nameToIndex(PARAM_ID_TBMMONTH));
			showNoDataRecord = (UFBoolean) row.getValue(dsSearch.nameToIndex(PARAM_ID_NODATA));
			if(showNoDataRecord == null){
				showNoDataRecord = UFBoolean.FALSE;
			}
		}
		
		if (StringUtil.isEmptyWithTrim(year)) {
			MonthReportUtils.removeCol(viewMain);
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "��ѯʧ��"*/,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0143")/*@res "�꿼���ڼ�δ���壡"*/);
			return;
		}
		if (StringUtil.isEmptyWithTrim(month)) {
			MonthReportUtils.removeCol(viewMain);
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "��ѯʧ��"*/,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0144")/*@res "�¿����ڼ�δ���壡"*/);
			return;
		}
		
		// ��¼��ѯ����
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_DEPT, pk_dept);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_FWSQL, fromWhereSQL);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_YEAR, year);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_MONTH, month);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_NODATA, showNoDataRecord);
		
		// ���� ���ݼ��ͱ��
		String pk_hrorg = SessionUtil.getHROrg();
		MonthReportUtils.buildDsAndGrid(viewMain, pk_hrorg);		
		//���°�ť״̬
		ButtonStateManager.updateButtons();
		// ��������
		MonthReportUtils.resetData(ds, pk_dept, fromWhereSQL, year, month, containsSubDepts,
				showNoDataRecord.booleanValue());
		
		
		
	}

	/**
	 * �鿴�����±�����
	 * 
	 * @param scriptEvent
	 */
	public void showMthReportDetail(ScriptEvent scriptEvent) {

		String rowId = getLifeCycleContext().getParameter("dsMain_rowId");
		String dsId = getLifeCycleContext().getParameter("dsMain_id");
		Dataset ds = getLifeCycleContext().getViewContext().getView().getViewModels().getDataset(dsId);
		Row selRow = ds.getRowById(rowId);
		if (selRow == null) {
			return;
		}
		String pk_psndoc = (String) selRow.getValue(ds.nameToIndex(PARAM_ID_PK_PSNDOC));
		String tbmyear = (String) selRow.getValue(ds.nameToIndex(PARAM_ID_TBMYEAR));
		String tbmmonth = (String) selRow.getValue(ds.nameToIndex(PARAM_ID_TBMMONTH));
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_PK_PSNDOC, pk_psndoc);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_TBMYEAR, tbmyear);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_TBMMONTH, tbmmonth);
		CommonUtil.showViewDialog(PAGE_MTH_RPT_DTL_WIDGET, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0093")/*
																																			 * @
																																			 * res
																																			 * "�±�����"
																																			 */, DialogSize.TINY);
	}

	/**
	 * δ����ͳ��
	 * 
	 * @param mouseEvent
	 */
	public void showUnGenerate(MouseEvent<MenuItem> mouseEvent) {

		CommonUtil.showViewDialog(PAGE_UNGENERATE_PSN, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0094")/*
																																		 * @
																																		 * res
																																		 * "δ����ͳ��"
																																		 */, 

				DialogSize.LARGE.getWidth(), 610);

	}

	public AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	/**
	 * ��ǰ���Ƭ��
	 * 
	 * @return
	 */
	private LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}

}