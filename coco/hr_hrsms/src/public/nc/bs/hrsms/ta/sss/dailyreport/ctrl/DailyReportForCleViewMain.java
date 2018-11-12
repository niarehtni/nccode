package nc.bs.hrsms.ta.sss.dailyreport.ctrl;

import java.awt.MenuItem;
import java.util.Map;

import nc.bs.hrsms.ta.sss.dailyreport.DailyReportUtils;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
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

import org.apache.commons.lang.StringUtils;

/**
 * @author liuhongd  
 */
public class DailyReportForCleViewMain implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	//grid
	public static final String GRID_DAILY_REPORT = "gridDailyReport";
	
	// 考勤月报数据集
	public static final String DATASET_ID = "dsDailyReport";
	// 月报详情片断ID
	public static final String PAGE_MTH_RPT_DTL_WIDGET = "DailyReportDetail";
	// 月报片断ID
	public static final String PAGE_MTH_RPT_WIDGET = "DailyReport";
	// 未生成统计
	public static final String PAGE_UNGENERATE_PSN = "unGeneratePsn";
	// 查询条件——查询无数据记录
	public static final String PARAM_ID_NODATA = "showNoDataRecord";
	// 查询条件——年度
	public static final String PARAM_ID_YEAR = "tbmyear";
	// 查询条件——期间
	public static final String PARAM_ID_MONTH = "tbmmonth";
	// 查询条件——人员
	public static final String PARAM_ID_PSNDOC = "psndoc";
	// 查询条件——年度
	public static final String PARAM_ID_BEGIN = "begindate";
	// 查询条件——年度
	public static final String PARAM_ID_END = "enddate";
	// 查询条件——FWSQL
	public static final String PARAM_ID_FWSQL = "fromWhereSql";
	// 查询条件字段.年度
	public static final String PARAM_ID_TBMYEAR = "tbmyear";
	// 查询条件字段.期间
	public static final String PARAM_ID_TBMMONTH = "tbmmonth";
	// 查询条件字段.人员
	public static final String PARAM_ID_PK_PSNDOC = "pk_psndoc";
	// 查询条件字段.部门
	public static final String PARAM_ID_DEPT = "pk_dept";
	// 部门
	public static final String PARAM_ID_DEPTNAME = "deptName";

	public static final String PLUGIN_PARAM_ID = "qryout";
	public static final String PAGE_QUERY_WIDGET = "pubview_simplequery";
	// 年度期间是否变化的参数
	public static final String SESSION_DATE_CHANGE = "isDateChange";

	/**
	 * 加载数据集
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onMonthReportDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException {
		Dataset ds = dataLoadEvent.getSource();
		if (isPagination(ds)) { // 分页操作 
			boolean containsSubDepts = SessionUtil.isIncludeSubDept();
			String pk_dept = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_DEPT);
			String begindate = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_BEGIN);
			String enddate = (String) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_END);
			UFBoolean showNoDataRecord = (UFBoolean) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_NODATA);
			boolean noDataRecord = showNoDataRecord.booleanValue();
			FromWhereSQLImpl fromWhereSql = (FromWhereSQLImpl) getLifeCycleContext().getApplicationContext().getAppAttribute(PARAM_ID_FWSQL);
			DailyReportUtils.resetData(ds, pk_dept, fromWhereSql, begindate, enddate, containsSubDepts, noDataRecord);
			ButtonStateManager.updateButtons();
		} else {
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
	 * 管理部门变更
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
		// 初始化操作
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 搜索
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
		Dataset ds = viewMain.getViewModels().getDataset(DATASET_ID);
		if (ds == null) {
			return;
		}
		// 清空数据集
		DatasetUtil.clearData(ds);
		ds.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		if (keys == null || keys.size() == 0) {
			return;
		}

		// 管理部门
		String pk_dept = SessionUtil.getPk_mng_dept();
		// 是否包含下级部门
		boolean containsSubDepts = SessionUtil.isIncludeSubDept();

		nc.uap.ctrl.tpl.qry.FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys
				.get(HrssConsts.PO_SEARCH_WHERESQL);
		// 查询条件-FromWhereSQL
		FromWhereSQLImpl fromWhereSQL = (FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL(whereSql);

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
		String beginDate = null;
		// 期间
		String endDate =  null;
		// 查询无数据记录
		UFBoolean showNoDataRecord = null;
		LfwView simpQryView = getLifeCycleContext().getWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset(HrssConsts.DS_SIMPLE_QUERY);
		if (dsSearch != null) {
			Row row = dsSearch.getSelectedRow();
			beginDate = row.getValue(dsSearch.nameToIndex(PARAM_ID_BEGIN)).toString();
			endDate = row.getValue(dsSearch.nameToIndex(PARAM_ID_END)).toString();
			showNoDataRecord = (UFBoolean) row.getValue(dsSearch.nameToIndex(PARAM_ID_NODATA));
			if(showNoDataRecord == null){
				showNoDataRecord = UFBoolean.FALSE;
			}
		}
		
		if (StringUtil.isEmptyWithTrim(beginDate)) {
			DailyReportUtils.removeCol(viewMain);
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "查询失败"*/,
					"开始日期未定义"/*@res "年考勤期间未定义！"*/);
			return;
		}
		if (StringUtil.isEmptyWithTrim(endDate)) {
			DailyReportUtils.removeCol(viewMain);
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res","0c_pub-res0168")/*@res "查询失败"*/,
					"结束日期未定义"/*@res "月考勤期间未定义！"*/);
			return;
		}
		
		// 记录查询条件
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_DEPT, pk_dept);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_FWSQL, fromWhereSQL);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_BEGIN, beginDate);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_END, endDate);
		getLifeCycleContext().getApplicationContext().addAppAttribute(PARAM_ID_NODATA, showNoDataRecord);
		
		// 生成 数据集和表格
		String pk_hrorg = SessionUtil.getHROrg();
		DailyReportUtils.buildDsAndGrid(viewMain, pk_hrorg);		
		//更新按钮状态
		ButtonStateManager.updateButtons();
		// 加载数据
		DailyReportUtils.resetData(ds, pk_dept, fromWhereSQL, beginDate, endDate, containsSubDepts,
				showNoDataRecord.booleanValue());
		
		
		
	}

	/**
	 * 查看考勤月报详情
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
																																			 * "月报详情"
																																			 */, DialogSize.TINY);
	}

	/**
	 * 未生成统计
	 * 
	 * @param mouseEvent
	 */
	public void showUnGenerate(MouseEvent<MenuItem> mouseEvent) {

		CommonUtil.showViewDialog(PAGE_UNGENERATE_PSN, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0094")/*
																																		 * @
																																		 * res
																																		 * "未生成统计"
																																		 */, 

				DialogSize.LARGE.getWidth(), 610);

	}

	public AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	/**
	 * 当前获得片段
	 * 
	 * @return
	 */
	private LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}

}