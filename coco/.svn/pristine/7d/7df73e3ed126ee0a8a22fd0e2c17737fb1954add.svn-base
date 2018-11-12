package nc.bs.hrsms.ta.sss.dailyreport;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.monthreport.MonthReportConsts;
import nc.bs.hrss.ta.monthreport.ctrl.MonthReportForEmpViewMain;
import nc.bs.hrss.ta.monthreport.ctrl.MonthReportForMngViewMain;
import nc.bs.hrsms.ta.sss.dailyreport.ctrl.DailyReportForCleViewMain;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.ta.IDayStatQueryMaintain;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.itf.ta.IViewOrderQueryService;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.common.EditorTypeConst;
import nc.uap.lfw.core.common.RenderTypeConst;
import nc.uap.lfw.core.common.StringDataTypeConst;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.comp.LabelComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.daystat.DayStatVO;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.ta.vieworder.ViewOrderVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 考勤月报的工具类
 * 
 * @author qiaoxp
 * 
 */
public class DailyReportUtils {

	/** 表格单元格的id的前缀 */
	private static final String CLOUM_PREFIX = "col_";

	/** 表格单元格的宽度 */
	private static final int GRID_COLUMN_WIDTH = 100;
	
	/**
	 * 根据开始年月和结束年月查询我的考勤月报
	 * 
	 * @param pk_psndoc
	 * @param beginYear
	 * @param beginMonth
	 * @param endYear
	 * @param endMonth
	 * @return
	 */
	public static MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String beginYear, String beginMonth,
			String endYear, String endMonth) {
		MonthStatVO[] monthStatVOs = null;
		try {
			IMonthStatQueryMaintain service = ServiceLocator.lookup(IMonthStatQueryMaintain.class);
			monthStatVOs = service.queryByPsnAndNatualYearMonth(pk_psndoc, beginYear, beginMonth, endYear, endMonth);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

		return monthStatVOs;
	}

	/**
	 * 查询显示项目
	 * 
	 * @param pk_org
	 * @param fun_type
	 * @param report_type
	 * @param containsDisable
	 *            ，是否包含已停用的项目
	 * @return
	 * @throws BusinessException
	 */
	public static ViewOrderVO[] queryViewOrder(String pk_org, int fun_type, int report_type, boolean containsDisable) {
		ViewOrderVO[] viewOrderVOs = null;
		try {
			IViewOrderQueryService viewOrder = ServiceLocator.lookup(IViewOrderQueryService.class);
			viewOrderVOs = viewOrder.queryViewOrder(pk_org, fun_type, report_type, true);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}
		return viewOrderVOs;
	}
	
	/**
	 * 删除表格列
	 * 
	 * @param widget
	 */
	public static void removeCol(LfwView widget){
		// 表格
		GridComp grid = (GridComp) widget.getViewComponents().getComponent(DailyReportForCleViewMain.GRID_DAILY_REPORT);
		List<IGridColumn> groupList = new ArrayList<IGridColumn>();
		IGridColumn col = null;
		
		// 删除原有COLUMNS
		String colStr = null;
		for (Iterator<IGridColumn> it = grid.getColumnList().iterator(); it.hasNext();) {
			col = (GridColumn)it.next();
			colStr = col.getId();
			if(StringUtils.isEmpty(colStr) || colStr.startsWith(CLOUM_PREFIX)){
				groupList.add(col);
			}
		}
		grid.removeColumns(groupList, true);
		// 数据集
		Dataset ds = widget.getViewModels().getDataset(DailyReportForCleViewMain.DATASET_ID);
		// 去掉多余字段
		Field[] fields = ds.getFieldSet().getFields();
		String filedId = null;
		for(Field field : fields){
			filedId = field.getId();
			if(StringUtils.isEmpty(filedId) || filedId.startsWith(CLOUM_PREFIX)){
				ds.getFieldSet().removeField(field);
			}
		}
	}

	/**
	 * 构造员工考勤月报表格
	 * 
	 * @param widget
	 * @param pk_org
	 * @throws LfwRuntimeException
	 */
	public static void buildDsAndGrid(LfwView widget, String pk_org) throws LfwRuntimeException {
		
		// 查询条件开始日期和结束日期没有发生变化,不用重构造DS/Grid！
		UFBoolean dateChangeFlag = (UFBoolean) getApplicationContext().getAppAttribute(MonthReportForMngViewMain.SESSION_DATE_CHANGE);
		// 重置状态
		getApplicationContext().addAppAttribute(MonthReportForMngViewMain.SESSION_DATE_CHANGE, UFBoolean.FALSE);
		if (dateChangeFlag != null && !dateChangeFlag.booleanValue()) {
			return;
		}
		
		// 删除表格列
		removeCol(widget);
		
		// 表格
		GridComp grid = (GridComp) widget.getViewComponents().getComponent(DailyReportForCleViewMain.GRID_DAILY_REPORT);
		// 数据集
		Dataset ds = widget.getViewModels().getDataset(DailyReportForCleViewMain.DATASET_ID);

		// 根据人员考勤档案所在组织, 获得考勤日报项目
		ViewOrderVO[] viewOrderVOs = queryViewOrder(pk_org, ViewOrderVO.FUN_TYPE_DAY, ViewOrderVO.REPORT_TYPE_PSN,
				true);
		if (viewOrderVOs == null || viewOrderVOs.length == 0) {
			return;
		}
		
		// 设置Grid中是否审核列的显示
		needapprove(pk_org, grid);
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		Integer timedecimal = timeRuleVO.getTimedecimal();
//		Integer mreportdecimal = timeRuleVO.getMreportdecimal();
//		String[] mReportFields = new String[]{"ACTUALWORKDAYS", "ACTUALWORKHOURS", "WORKDAYS", "WORKHOURS"};
		for (ViewOrderVO vo : viewOrderVOs) {
			Field field = buildField(vo);
			// 设置精度
			if (field.getDataType().equals(StringDataTypeConst.Decimal)) {
//				if(Arrays.asList(mReportFields).contains(vo.getCode())){
//					field.setPrecision(String.valueOf(mreportdecimal));
//				}else {
					field.setPrecision(String.valueOf(timedecimal));
//				}
			}
			ds.getFieldSet().addField(field);
			GridColumn column = buildColumn(field);
			// 是否合计列
			column.setSumCol(isSumCol(field.getDataType()));
			column.setShowCheckBox(false);
			grid.addColumn(column, true);
		}
		 grid.setShowSumRow(true);
	}

	/**
	 * 查看员工考勤月报详情
	 * 
	 * @param widget
	 * @param pk_org
	 */
	public static void buildDetailGrid(String beginYear, String beginMonth, String endYear, String endMonth) {
		// 人员主键
		String pk_psndoc = SessionUtil.getPk_psndoc();
		MonthStatVO[] monthStatVOs = queryByPsnAndNatualYearMonth(pk_psndoc, beginYear, beginMonth, endYear, endMonth);
		
		if (!ArrayUtils.isEmpty(monthStatVOs)) {

			// 月报JSON
			StringBuffer monthReportJson = new StringBuffer("");
			// 月报表头JSON
			StringBuffer titleJson;
			// 月报项目JSON
			StringBuffer itemsJson;

			String title = "";
			String itemName = "";
			String itemValue = "";
			String pk_hrorg = "";
			DecimalFormat frmt = null;
			
			for (int i = 0; i < monthStatVOs.length; i++) {
				pk_hrorg = monthStatVOs[i].getPk_org();
				
				// 根据人员考勤档案所在组织, 获得考勤月报项目
				ViewOrderVO[] viewOrderVOs = queryViewOrder(pk_hrorg, ViewOrderVO.FUN_TYPE_MONTH, ViewOrderVO.REPORT_TYPE_PSN,true);
				
				// 考勤规则
				TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_hrorg);
				//考勤规则——考勤数据保留小数位数
				Integer timedecimal = timeRuleVO.getTimedecimal();
				String strDcmlFrmt = "#0";
				for (int j = 0; j < timedecimal; j++) {
					if(strDcmlFrmt == "#0"){
						strDcmlFrmt += ".";
					}
					strDcmlFrmt += "0";
				}
				DecimalFormat dcmlFrmt = new DecimalFormat(strDcmlFrmt);
//				// 考勤规则——月报出勤班数保留小数
//				Integer mreportdecimal = timeRuleVO.getMreportdecimal();
//				String mReportDcmlFrmt = "#0";
//				for (int j = 0; j < mreportdecimal; j++) {
//					if(mReportDcmlFrmt == "#0"){
//						mReportDcmlFrmt += ".";
//					}
//					mReportDcmlFrmt += "0";
//				}
//				DecimalFormat reportDcmlFrmt = new DecimalFormat(mReportDcmlFrmt);
//				String[] mReportFields = new String[]{"ACTUALWORKDAYS", "ACTUALWORKHOURS", "WORKDAYS", "WORKHOURS"};
				
				
				titleJson = new StringBuffer("");
				itemsJson = new StringBuffer("");

				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0097")/*
																										 * @
																										 * res
																										 * "考勤期间："
																										 */
						+ monthStatVOs[i].getTbmyear() + "-" + monthStatVOs[i].getTbmmonth() + " "
						+ monthStatVOs[i].getOrgName() + " " + monthStatVOs[i].getDeptName();
				titleJson.append(title);

				itemsJson.append(getItemJsonString(0,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001403")/*
																										 * @
																										 * res
																										 * "姓名"
																										 */,
						monthStatVOs[i].getPsnName()));

				if (!ArrayUtils.isEmpty(viewOrderVOs)) {
					for (int j = 0; j < viewOrderVOs.length; j++) {
						itemName = getDefaultshowname(viewOrderVOs[j]);
						frmt = dcmlFrmt;
						
						Object value = monthStatVOs[i].getAttributeValue(viewOrderVOs[j].getField_name());

						if (value instanceof BigDecimal) {
							itemValue = frmt.format(value);
						}else if(value instanceof UFDouble){
							itemValue = frmt.format(((UFDouble) value).toBigDecimal());
						}else if(value instanceof UFBoolean){
							UFBoolean booleanValue = (UFBoolean)value;
							if(booleanValue.booleanValue()){
								itemValue = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0027")/**@res "是*/;
							}else{
								itemValue = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0028")/**@res "否"*/;
							}
						}else {
							itemValue = String.valueOf(value);
						}
						if(viewOrderVOs[j].getTimeitem_type() != null ){
							if(viewOrderVOs[j].getTimeitem_type() == TimeItemVO.LEAVE_TYPE 
									|| viewOrderVOs[j].getTimeitem_type() == TimeItemVO.OVERTIME_TYPE){
								//在value中加入pk_timeitem
								itemName = itemName + "," +  viewOrderVOs[j].getPk_timeitem() + "-" + viewOrderVOs[j].getTimeitem_type()
								+ "," + monthStatVOs[i].getPk_org() 
								+ "," + monthStatVOs[i].getTbmyear() + "-" + monthStatVOs[i].getTbmmonth();
							}
						}
						itemsJson.append(getItemJsonString(j + 1, itemName, itemValue));
					}
				}
				monthReportJson.append(getMthReportJsonString(i, titleJson.toString(), itemsJson.toString()));

			}
			String direction = "H_8";
			getApplicationContext()
					.addExecScript("layoutSlip('" + monthReportJson.toString()  + "','" + direction + "','"+ "Emp"+"');");
		}else{
			LfwView widget = LfwRuntimeEnvironment.getWebContext().getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
			LabelComp lblContent = (LabelComp) widget.getViewComponents().getComponent(MonthReportForEmpViewMain.LBL_CONTENT);
			lblContent.setVisible(false);
		}

	}

	private static String getItemJsonString(int index, String name, String value) {
		StringBuffer itemStr = new StringBuffer("");
		if (index > 0) {
			itemStr.append(",");
		}
		itemStr.append("item" + index + ": {");
		itemStr.append("name:\"" + name + "\"");
		itemStr.append(",");
		itemStr.append("value:\"" + value + "\"");
		itemStr.append("}");
		return itemStr.toString();
	}

	private static String getMthReportJsonString(int index, String context, String detail) {
		StringBuffer lipJson = new StringBuffer("");
		if (index > 0) {
			lipJson.append(",");
		}
		lipJson.append("{");
		lipJson.append("tital:\"" + context + "\"");
		lipJson.append(", detail:{" + detail + "}");
		lipJson.append("}");
		return lipJson.toString();
	}

	/**
	 * 根据查询条件,显示员工考勤日报列表
	 * 
	 * @param ds
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @param containsSubDepts
	 */
	public static void resetData(Dataset ds, String pk_dept, FromWhereSQL fromWhereSQL, String beginDate, String endDate,
			boolean containsSubDepts, boolean showNoDataRecord) {
		DayStatVO[] dayStatVOs = null;
		LoginContext context = SessionUtil.getLoginContext();
		IDayStatQueryMaintain service  = NCLocator.getInstance().lookup(IDayStatQueryMaintain.class);
		try {
			//service.queryByDeptAndDate(context, pk_dept, date)
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept,containsSubDepts,fromWhereSQL);//拼接pk_dept
			dayStatVOs = service.queryByCondition(context, fromWhereSQL, new UFLiteralDate(beginDate), new UFLiteralDate(endDate), showNoDataRecord);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		SuperVO[] vos = DatasetUtil.paginationMethod(ds, dayStatVOs);
		new SuperVO2DatasetSerializer().serialize(vos, ds, Row.STATE_NORMAL);
	}

	/**
	 * 生成 字段
	 * 
	 * @param curDate
	 * @return
	 */
	public static Field buildField(ViewOrderVO vo) {
		// 班级字段
		Field field = new Field();
		// 设置字段ID
		field.setId(CLOUM_PREFIX + vo.getField_name());
		// 列名
		field.setText(getDefaultshowname(vo));
		// 设置数据类型
		field.setDataType(translatorDataType(vo.getData_type()));
		// 与VO对应字段(属性)
		field.setField(vo.getField_name());
		return field;
	}

	/**
	 * 获得字段显示名称
	 * 
	 * @param viewOrderVO
	 * @return
	 */
	private static String getDefaultshowname(ViewOrderVO viewOrderVO) {
		String name = viewOrderVO.getMultilangName();
		if (name == null) {
			name = viewOrderVO.getName();
		}
		if (viewOrderVO.getItem_type() == ViewOrderVO.ITEM_TYPE_TIMEITEM) {
			name += "("
					+ (viewOrderVO.getUnit() == TimeItemCopyVO.TIMEITEMUNIT_HOUR ? nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0078")/*
																					 * @
																					 * res
																					 * "小时"
																					 */: nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0079")/*
																					 * @
																					 * res
																					 * "天"
																					 */) + ")";
		}
		return name;
	}

	private static String translatorDataType(int dataType) {
		switch (dataType) {
		case ItemVO.DATA_TYPE_INT:
			return StringDataTypeConst.INTEGER;
		case ItemVO.DATA_TYPE_DECIMAL:
			return StringDataTypeConst.Decimal;
		case ItemVO.DATA_TYPE_STRING:
			return StringDataTypeConst.STRING;
		case ItemVO.DATA_TYPE_BOOL:
			return StringDataTypeConst.UFBOOLEAN;
		case ItemVO.DATA_TYPE_DATE:
			return StringDataTypeConst.DATE;
		default:
			return StringDataTypeConst.STRING;
		}
	}

	/**
	 * 根据Field创建Column
	 * 
	 * @param field
	 */
	private static GridColumn buildColumn(Field field) {
		GridColumn column = new GridColumn();
		// 设置id
		column.setId(field.getId());
		// 设置关联Dataset的Field
		column.setField(field.getId());
		// 设置列宽度,按实际宽度
		column.setWidth(GRID_COLUMN_WIDTH);
		// 设置显示标题
		column.setText(field.getText());
		// 设置编辑类型
		column.setEditorType(getEditType(field.getDataType()));
		// 设置渲染器
		column.setRenderType(getRendarType(field.getDataType()));
		// 设置数据类型
		column.setDataType(field.getDataType());
		// 是否合计列
		column.setSumCol(isSumCol(field.getDataType()));
		return column;
	}

	/**
	 * 编辑类型
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getEditType(String dataType) {
		if (StringDataTypeConst.INT.equals(dataType)) {
			return EditorTypeConst.INTEGERTEXT;
		} else if (StringDataTypeConst.Decimal.equals(dataType)) {
			return EditorTypeConst.DECIMALTEXT;
		} else if (StringDataTypeConst.STRING.equals(dataType)) {
			return EditorTypeConst.STRINGTEXT;
		} else if (StringDataTypeConst.UFBOOLEAN.equals(dataType)) {
			return EditorTypeConst.CHECKBOX;
		} else if (StringDataTypeConst.DATE.equals(dataType)) {
			return EditorTypeConst.DATETEXT;
		} else {
			return EditorTypeConst.STRINGTEXT;
		}
	}

	/**
	 * 渲染器
	 * 
	 * @param dataType
	 * @return
	 */
	public static String getRendarType(String dataType) {
		if (StringDataTypeConst.INT.equals(dataType)) {
			return RenderTypeConst.IntegerRender;
		} else if (StringDataTypeConst.Decimal.equals(dataType)) {
			return RenderTypeConst.DecimalRender;
		} else if (StringDataTypeConst.STRING.equals(dataType)) {
			return RenderTypeConst.DefaultRender;
		} else if (StringDataTypeConst.UFBOOLEAN.equals(dataType)) {
			return RenderTypeConst.BooleanRender;
		} else if (StringDataTypeConst.DATE.equals(dataType)) {
			return RenderTypeConst.DateRender;
		} else {
			return RenderTypeConst.DefaultRender;
		}
	}

	/**
	 * 判断是否合计列
	 * 
	 * @param editorType
	 * @return
	 */
	private static boolean isSumCol(String editorType) {
		if (StringDataTypeConst.INTEGER.equals(editorType) || StringDataTypeConst.Decimal.equals(editorType)
				|| StringDataTypeConst.DOUBLE.equals(editorType) || StringDataTypeConst.FLOATE.equals(editorType)
				|| StringDataTypeConst.LONG.equals(editorType) || StringDataTypeConst.UFDOUBLE.equals(editorType)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据考勤档案中是否设置了需审批操作, 设置Grid中是否审核列的显示
	 * 
	 * @param pk_org
	 * @param grid
	 */
	private static void needapprove(String pk_org, GridComp grid) {
		TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		if (timeRuleVO == null || timeRuleVO.getMreportapproveflag() == null
				|| !timeRuleVO.getMreportapproveflag().booleanValue()) {
			GridColumn col = (GridColumn) grid.getColumnById(MonthReportConsts.GRID_COLUMN_ISAPPROVE);
			if (col != null) {
				col.setVisible(false);
			}
		}
	}
	
	public static ApplicationContext getApplicationContext() {
		return AppLifeCycleContext.current().getApplicationContext();
	}
}