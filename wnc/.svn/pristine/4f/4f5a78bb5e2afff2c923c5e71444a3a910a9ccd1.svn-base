package com.ufida.report.anareport.model;

import java.awt.Rectangle;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.itf.bd.timezone.TimezoneUtil;
import nc.itf.iufo.freereport.extend.IAreaCondition;
import nc.itf.iufo.freereport.extend.IBusiFormat;
import nc.itf.iufo.freereport.extend.IReportDataAdjustor;
import nc.itf.iufo.freereport.extend.IReportDataAdjustor2;
import nc.pub.iufo.basedoc.IDName;
import nc.pub.smart.context.ISmartContextKey;
import nc.pub.smart.data.Condition;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.metadata.DataTypeConstant;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.pub.smart.model.SmartModel;
import nc.pub.smart.model.descriptor.AggrDescriptor;
import nc.pub.smart.model.descriptor.Descriptor;
import nc.pub.smart.model.descriptor.FilterDescriptor;
import nc.pub.smart.model.descriptor.FilterItem;
import nc.pub.smart.model.descriptor.GroupItem;
import nc.pub.smart.model.descriptor.LimitRowDescriptor;
import nc.pub.smart.model.descriptor.SelectColumnDescriptor;
import nc.pub.smart.model.descriptor.SortDescriptor;
import nc.pub.smart.model.descriptor.SortItem;
import nc.pub.smart.model.preferences.Parameter;
import nc.pub.smart.script.engine.MetaLinkFinder;
import nc.pub.smart.tracedata.TraceDataParam;
import nc.ui.bd.pubinfo.address.AddressFormater;
import nc.ui.format.FormatMetaException;
import nc.ui.format.NCFormatterInstance;
import nc.util.iufo.freereport.FreeReportTimeZoneUtil;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.pub.format.exception.FormatException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.report.subscibe.TaskExecRoom;
import nc.vo.uif2.LoginContext;
import uap.pub.bq.report.complex.calculate.FrCpxResultDataSet;
import uap.pub.bq.report.complex.model.FrCpxDimensionItem;
import uap.pub.bq.report.complex.model.FrCpxDimensionSet;
import uap.pub.bq.report.complex.model.FrCpxModel;
import uap.pub.bq.report.complex.runtime.FrCpxDataModelUtil;
import uap.pub.bq.report.complex.runtime.FrCpxExtAreaCell;
import uap.pub.bq.swchart.util.SWChartRequestUtils;
import uap.pub.bqrt.base.adaptor.IOrgConstProxy;
import uap.vo.bq.pub.analysis.util.AnaContextUtils;
import uap.vo.bq.swchart.model.ISwChartModel;
import uap.vo.bq.swchart.model.runtime.ISWChartRuntimeModel;

import com.borland.dx.dataset.Variant;
import com.ufida.dataset.IContext;
import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.report.extend.IOrgAuthorityExt;
import com.ufida.iufo.table.areafomula.AreaFuncDriver;
import com.ufida.iufo.table.areafomula.BIAreaFormulaHandler;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.iufo.table.exarea.CellsAreaOutput;
import com.ufida.iufo.table.exarea.ExDataDescriptor;
import com.ufida.iufo.table.exarea.ExDataPaginal;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaConstants;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.iufo.table.exarea.ExtendAreaOutput;
import com.ufida.iufo.table.exarea.IAreaOutput;
import com.ufida.iufo.table.exarea.IExData;
import com.ufida.iufo.table.exarea.IExtendAreaModel;
import com.ufida.iufo.table.exarea.SeparateColOutput;
import com.ufida.iufo.table.exarea.link.ExAreaLinkModel;
import com.ufida.iufo.table.exarea.memoryorder.MemoryCrossOrder;
import com.ufida.iufo.table.exarea.memoryorder.MemoryOrder;
import com.ufida.iufo.table.exarea.memoryorder.MemoryOrderMng;
import com.ufida.iufo.table.exarea.memoryorder.MemoryOrderMngUtil;
import com.ufida.iufo.table.exarea.memoryorder.MemoryOrderSet;
import com.ufida.iufo.table.exarea.memoryorder.MemoryOrderTypeConstants;
import com.ufida.iufo.table.model.BaseSmartQueryUtil;
import com.ufida.iufo.table.model.DataRelation;
import com.ufida.iufo.table.model.FreeChartQueryParam;
import com.ufida.iufo.table.model.FreeChartQueryResult;
import com.ufida.iufo.table.model.FreeCpxQueryParam;
import com.ufida.iufo.table.model.FreeCpxQueryResult;
import com.ufida.iufo.table.model.FreeDSStateUtil;
import com.ufida.iufo.table.model.FreeMainOrgCheckUtil;
import com.ufida.iufo.table.model.FreeMainOrgInfoSet;
import com.ufida.iufo.table.model.FreeQueryParam;
import com.ufida.iufo.table.model.FreeQueryResult;
import com.ufida.iufo.table.model.IFreeDSType;
import com.ufida.iufo.table.model.ReportCondition;
import com.ufida.iufo.table.model.SortDescMng;
import com.ufida.iufo.table.pub.DataProcessUtil;
import com.ufida.report.anareport.FreePrivateCommonKey;
import com.ufida.report.anareport.FreePrivateContextKey;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.FreeReportFucletContextKey;
import com.ufida.report.anareport.IFldCountType;
import com.ufida.report.anareport.base.FreePageBreak;
import com.ufida.report.anareport.base.FreePageOutputInfo;
import com.ufida.report.anareport.data.AbsRowData;
import com.ufida.report.anareport.data.AnaDataValue;
import com.ufida.report.anareport.data.DataValue;
import com.ufida.report.anareport.data.DetailRowData;
import com.ufida.report.anareport.data.GroupDataIndex;
import com.ufida.report.anareport.data.GroupRowData;
import com.ufida.report.anareport.data.RowDataArray;
import com.ufida.report.anareport.exec.FreeDataPolicy;
import com.ufida.report.anareport.exec.FreePolicy;
import com.ufida.report.anareport.exec.FreePolicyFactory;
import com.ufida.report.anareport.exec.FreePolicyUtil;
import com.ufida.report.anareport.expand.AreaPrivateMap;
import com.ufida.report.anareport.model.CrossDataModel.CrossRowData;
import com.ufida.report.anareport.util.AreaCrossSetInfoUtil;
import com.ufida.report.anareport.util.AreaDescUtil;
import com.ufida.report.anareport.util.AreaRowLevelUtil;
import com.ufida.report.anareport.util.FreeFieldConverter;
import com.ufida.report.anareport.util.FreeReportModuleTypeUtil;
import com.ufida.report.anareport.util.FreeReportQueryExcutor;
import com.ufida.report.anareport.util.FreeReportUtil;
import com.ufida.report.anareport.util.GridSortUtil;
import com.ufida.report.chart.model.AgrrChartModel;
import com.ufida.report.chart.model.ChartManager;
import com.ufida.report.chart.model.ChartModel;
import com.ufida.report.chart.model.IAgrrChartModel;
import com.ufida.report.chart.model.IBIChartModel;
import com.ufida.report.chart.model.ICombineChartModel;
import com.ufida.report.crosstable.CrossTableHeader;
import com.ufida.report.crosstable.CrossTableModel;
import com.ufida.report.crosstable.CrossTableRelation;
import com.ufida.report.crosstable.CrossTableSet;
import com.ufida.report.crosstable.FixField;
import com.ufida.report.free.field.FieldAttExtModel;
import com.ufida.report.free.field.FieldAttrValue;
import com.ufida.report.free.field.FieldAttribute;
import com.ufida.report.free.plugin.param.IReportVarService;
import com.ufida.report.free.plugin.param.ReportVariableHelper;
import com.ufida.report.free.plugin.param.ReportVariables;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.report.free.plugin.render.CollapsibleInfo;
import com.ufida.report.swchart.model.FrChartAreaModel;
import com.ufida.zior.perfwatch.PerfWatch;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.report.constant.PropertyType;
import com.ufsoft.script.AreaFormulaUtil;
import com.ufsoft.script.IFormularRegionAdjust;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.FormulaVO;
import com.ufsoft.script.base.IParsed;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.Cell;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.CombinedAreaModel;
import com.ufsoft.table.CombinedCell;
import com.ufsoft.table.ExtDataModel;
import com.ufsoft.table.IArea;
import com.ufsoft.table.ICellValue;
import com.ufsoft.table.IExtModel;
import com.ufsoft.table.SeperateLockSet;
import com.ufsoft.table.TableUtilities;
import com.ufsoft.table.area.AreaCompModel;
import com.ufsoft.table.area.AreaCompPos;
import com.ufsoft.table.area.IAreaModel;
import com.ufsoft.table.format.DateFormat;
import com.ufsoft.table.format.IDataFormat;
import com.ufsoft.table.format.IFormat;
import com.ufsoft.table.format.INumberFormat;
import com.ufsoft.table.format.TableConstant;
import com.ufsoft.table.format.UFDoubleFormat;
import com.ufsoft.table.format.condition.AreaCondition;
import com.ufsoft.table.format.condition.ConditionFormatModel;
import com.ufsoft.table.format.condition.ConditionRole;
import com.ufsoft.table.format.condition.IConditionFormat;
import com.ufsoft.table.format.condition.expr.ExprConditionFormat;

/**
 * 分析表中的扩展区域的数据模型
 * 
 * @author ll
 * 
 */

@SuppressWarnings("deprecation")
public class AreaDataModel implements IExtModel, IExData {

	private static final long serialVersionUID = 1L;

	// private static final String KEY_LASTPAGING = "key_lastpaging_state";
	private ExtendAreaCell m_exCell = null;
	private ExDataPaginal m_dataPaginal = null;

	private transient IContext m_areaContext = null;// 区域自己的Context，由报表Context复制而来，但是增加了区域参数等内容
	private transient AreaFields m_flds = null;
	private transient CrossDataModel m_crossData = null;// 对于交叉表的扩展结果进行缓存
	// private transient AnaReportModel m_repModel = null;//
	// 维护一个对分析表模型的引用，以便获得格式模型和其他信息
	private transient AbsAnaReportModel m_repModel = null;
	private transient AreaCombineProcessor m_combProc = null;// 区域内合并单元格的处理
	private transient AreaRowLevel m_rowLvls = null;// 区域中每行的分级状态
	private transient AnaDataSetTool m_dsTool = null;// 封装对数据集取数接口的调用
	private transient ExDataDescriptor m_desc = null;// 扩展信息

	private transient AreaFormulaModel m_fmlModel = null;
	private transient boolean m_hasFormular = false;
	private transient ArrayList<Descriptor> m_descListAggr = null;// 汇总数据集的描述器
	private transient ArrayList<Descriptor> m_descList = null; // 明细数据集的描述器
	private transient Hashtable<CellPosition, IFormat> m_dCellFormat = null;//
	private transient Hashtable<CellPosition, IArea> m_formConbinedArea = null;// 为了提高效率，在填充数据过程中缓存格式单元对应的合并区域

	private transient IAreaCondition m_areaCondition = null;// "查询"交互中附加的区域查询条件
	private transient IBusiFormat m_busiFormat = null;// 业务逻辑附加的格式信息
	private transient IReportDataAdjustor m_areaDataAdjustor = null;// 业务逻辑附加的数据调整器
	private transient FreePolicy m_policy = null;// 当前执行策略

	private transient BIAreaFormulaHandler m_format_formulaHandler = null;
	// NC数据格式处理器
	private transient NCFormatterInstance formatter = null;

	private transient boolean m_bFillData = true;// 是否填充数据（打印场景为了提高效率，计算分页前不填充真实数据）
	boolean isModuleOfBq;
	public boolean getIsModuleOfBq() {
		return isModuleOfBq;
	}
	public void setIsModuleOfBq(boolean isModuleOfBq) {
		this.isModuleOfBq = isModuleOfBq;
	}
	
	public AreaDataModel(ExtendAreaCell exCell) {
		m_exCell = exCell;
	}

	public void setReportModel(AbsAnaReportModel repModel) {
		m_repModel = repModel;
	}

	public AbsAnaReportModel getReportModel() {
		return m_repModel;
	}

	public ExtendAreaCell getExAreaCell() {
		return m_exCell;
	}

	public ExDataDescriptor provideDataDescript(IExtendAreaModel model, IContext context) {
		if (m_exCell instanceof FrCpxExtAreaCell) {
			FrCpxExtAreaCell frCpxAreaCell = (FrCpxExtAreaCell) m_exCell;
			// 清空上次执行临时存储的字段成员信息
			FrCpxModel frCpxModel = frCpxAreaCell.getFrCpxModel();
			if (frCpxModel == null)
				return null;
			FrCpxDimensionSet rowDimensionSet = frCpxModel.getRowDimensionSet();
			clearFieldMembers(rowDimensionSet.getAllDimensionItems());
			FrCpxDimensionSet columnDimensionSet = frCpxModel.getColumnDimensionSet();
			clearFieldMembers(columnDimensionSet.getAllDimensionItems());
			// 计算区域大小
			FrCpxResultDataSet cpxQueryResult = getDSTool().getCpxQueryResult();
			FrCpxDataModelUtil dataModelUtil = new FrCpxDataModelUtil((FrCpxExtAreaCell) m_exCell, cpxQueryResult,
					m_areaContext, this.getReportModel().getFormatModel());

			return dataModelUtil.getFrCpxAreaDesc();
		}

		if (m_exCell == null || (getDSPK() == null && getSmartModel() == null))
			return null;

		return getExtendDesc(context);
	}

	/**
	 * 清空栏目项中暂存的字段成员信息
	 * 
	 * @param allDimensionItems
	 */
	private void clearFieldMembers(FrCpxDimensionItem[] allDimensionItems) {
		if (allDimensionItems == null || allDimensionItems.length == 0)
			return;
		for (FrCpxDimensionItem frCpxDimensionItem : allDimensionItems) {
			frCpxDimensionItem.clearFieldMemberInfo();
			List<FrCpxDimensionItem> subDimesionItems = frCpxDimensionItem.getSubDimesionItems();
			if (subDimesionItems != null && subDimesionItems.size() > 0) {
				clearFieldMembers(subDimesionItems.toArray(new FrCpxDimensionItem[subDimesionItems.size()]));
			}
		}

	}

	public void buildAreaFields(boolean isPreview) {
		// 别名转换器需要一直维持一个，否则有可能会与dataset中的别名不一致 zhongkm
		FreeFieldConverter converter = m_flds == null ? null : m_flds.getFieldConverter();

		if (converter == null) {
			converter = (FreeFieldConverter) m_exCell.getAreaInfoSet().getAreaLevelInfo(
					FreeFieldConverter.class.getName());
			if(converter == null){
				converter = new FreeFieldConverter(getSmartModel());
				m_exCell.getAreaInfoSet().addAreaLevelInfo(FreeFieldConverter.class.getName(), converter);
			}
		}

		m_flds = new AreaFields(m_exCell, m_repModel, isPreview, converter);
		// if (converter != null)// 做为参数传入AreaFields中 modify by yuyangi
		// m_flds.setFieldConverter(converter);
		// 3.计算区域中每行的lvl值
		m_rowLvls = AreaRowLevelUtil.getRowLevel(this, m_exCell);

	}

	public void buildAreaFieldsByStartNode(boolean isPreview, boolean undoQuery) {
		if (!isCross() && undoQuery)// @edit by ll at 2011-9-8,下午07:17:32
			// 为了优化节点打开效率，此时不做数据展开
			return;

		buildAreaFields(isPreview);
	}

	public ExDataDescriptor getExtendDesc(IContext context) {
		if (m_desc == null) {
			ExtendAreaCell exCell = m_exCell;
			if ((getDSPK() == null && getSmartModel() == null)
					|| (m_flds != null && (!m_flds.hasAnyFld && !m_flds.isChart())))// 本区域没有任何字段
				return new ExDataDescriptor(exCell.getArea().getWidth(), exCell.getArea().getHeigth());
			AreaDataRelation dataRelation = DataRelation.getInstance(m_repModel.getExtendAreaModel()).getAreaRelation(
					getAreaPK());
			if (dataRelation == null) {
				this.createMemorySort();// 内存排序
			}
			
			//增加数据格式的预处理（对于地址类型的数据，需要一次计算出所有转换结果，以便减少远程调用次数）add by yanchm，20150602
			preAddresFormatData();
			
			if (isCross()) {
				m_desc = doCrossExpand();
			} else {
				m_desc = doListExpand();
			}

			// @edit by yuyangi at 2012-6-5,上午09:40:35 如果设置了分栏，需要修改扩展描述信息
			changeExtendDesc();
		}
		return m_desc;
	}

	// 收集排序内存排序设置，将格式设计的排序设置转化为内存排序设置
	private void createMemorySort() {
		if (m_flds == null) {
			return;
		}
		MemoryOrderMng mng = MemoryOrderMng.getInstance(m_exCell);
		if (mng.isOrder()) {// 已设置规则，不需要重复设置
			return;
		}
		SortDescriptor sortDesc = AreaDescUtil.getSortDescriptor(m_flds, m_exCell, m_repModel, true);
		if (sortDesc == null) {
			return;
		}
		SortItem[] items = sortDesc.getSorts();
		if (items == null) {
			return;
		}
		ArrayList<AnaRepField> flds = m_flds.getAllAnaFields();
		Map<String, AnaRepField> map = new HashMap<String, AnaRepField>();
		for (Iterator<AnaRepField> iterator = flds.iterator(); iterator.hasNext();) {
			AnaRepField anaRepField = iterator.next();
			map.put(anaRepField.getFieldID(), anaRepField);
		}
		if (SortDescMng.getInstance(m_exCell).isEnabled()) { // 处理排序管理设置的排序
			SortDescriptor sortDesMng = SortDescMng.getInstance(m_exCell).getSortDesc();
			if (sortDesMng != null) {
				SortItem[] items1 = sortDesMng.getSorts();
				for (int i = 0; i < items1.length; i++) {
					AnaRepField repField = map.get(items1[i].getFieldName());
					if (repField != null) {
						continue;
					} else {
						return;
					}
				}
			}
		}
		if (isCross()) {
			for (int i = 0; i < items.length; i++) {
				AnaRepField repField = map.get(items[i].getFieldName());
				if (repField == null) {
					continue;
				}
				if (repField.getFieldType() == AnaRepField.Type_CROSS_COLUMN
						|| repField.getFieldType() == AnaRepField.Type_CROSS_ROW) {
					MemoryOrderSet orderSet = null;
					String fieldName = repField.getUniqueName();
					MemoryCrossOrder memoryOrder = new MemoryCrossOrder(fieldName, fieldName, fieldName,
							repField.getOrderType(), null);
					if (repField.getFieldType() == AnaRepField.Type_CROSS_COLUMN) {// 列维度
						orderSet = new MemoryOrderSet(false, MemoryOrderTypeConstants.FILDTYPE_CROSS_COLDIM);
					} else if (repField.getFieldType() == AnaRepField.Type_CROSS_ROW) {// 行维度
						orderSet = new MemoryOrderSet(true, MemoryOrderTypeConstants.FILDTYPE_CROSS_ROWDIM);
					}
					orderSet.setMemoryOrder(new MemoryOrder[] { memoryOrder });
					mng.addCossMemoryOrderSet(orderSet);
				}
				// 指标和小计暂不支持
			}
		} else {// 列表排序设置
			ArrayList<AnaRepField> calcFlds = new ArrayList<AnaRepField>();
			ArrayList<AnaRepField> detailFlds = new ArrayList<AnaRepField>();
			for (int i = 0; i < items.length; i++) {
				AnaRepField repField = map.get(items[i].getFieldName());
				if (repField != null && !repField.isNoOrder()) {
					if (repField.getFieldType() == AnaRepField.TYPE_GROUP_FIELD) {// 分组字段
						String fieldName = repField.getUniqueName();
						MemoryOrder memoryOrder = new MemoryOrder(fieldName, fieldName, fieldName,
								repField.getOrderType());
						memoryOrder.setDefault(true);
						MemoryOrderSet orderSet = new MemoryOrderSet(true, MemoryOrderTypeConstants.FILDTYPE_LIST_GROUP);
						orderSet.setMemoryOrder(new MemoryOrder[] { memoryOrder });
						mng.addCossMemoryOrderSet(orderSet);
					} else if (repField.getFieldType() == AnaRepField.TYPE_CALC_FIELD) {// 小计字段
						calcFlds.add(repField);
					} else if (repField.getFieldType() == AnaRepField.TYPE_DETAIL_FIELD) {// 明细字段
						detailFlds.add(repField);
					}
				}
			}
			if (!calcFlds.isEmpty()) {// 小计
				MemoryOrderSet orderSet = new MemoryOrderSet(true, MemoryOrderTypeConstants.FILDTYPE_LIST_SUBTOTAL);
				MemoryOrder[] memoryOrders = new MemoryOrder[calcFlds.size()];
				for (int i = 0; i < calcFlds.size(); i++) {
					AnaRepField repField = calcFlds.get(i);
					FieldCountDef field = (FieldCountDef) repField.getField();
					String fieldName = field.getExpression();
					String mainFieldName = field.getMainField() == null ? null : field.getMainField().getExpression();
					String rangeFldName = field.getRangeField() == null ? null : field.getRangeField().getExpression();
					MemoryOrder memoryOrder = new MemoryOrder(fieldName, mainFieldName, rangeFldName,
							repField.getOrderType());
					memoryOrder.setDefault(true);
					memoryOrders[i] = memoryOrder;
				}
				orderSet.setMemoryOrder(memoryOrders);
				mng.addListMemoryOrderSet(orderSet);
			}
			if (!detailFlds.isEmpty()) {// 明细字段
				MemoryOrderSet orderSet = new MemoryOrderSet(true, MemoryOrderTypeConstants.FILDTYPE_LIST_DETAIL);
				MemoryOrder[] memoryOrders = new MemoryOrder[detailFlds.size()];
				for (int i = 0; i < detailFlds.size(); i++) {
					AnaRepField repField = detailFlds.get(i);
					String fieldName = repField.getUniqueName();
					MemoryOrder memoryOrder = new MemoryOrder(fieldName, fieldName, fieldName, repField.getOrderType());
					memoryOrder.setDefault(true);
					memoryOrders[i] = memoryOrder;
				}
				orderSet.setMemoryOrder(memoryOrders);
				mng.addListMemoryOrderSet(orderSet);
			}
		}
	}

	public boolean isChart() {
		if (m_flds == null)
			return false;
		return m_flds.isChart();
	}

	/**
	 * 记录单元位置上定义的公式
	 * 
	 * @create by wanyonga at 2010-7-8,上午09:07:31
	 * 
	 */
	private void recordCellFormulaAttr(IAreaOutput output, CellPosition srcPos, CellPosition destPos,
			Hashtable<IArea, FormulaVO> formulaRef) {
		if (!m_hasFormular || formulaRef == null) {
			return;
		}

		if (m_formConbinedArea == null) {
			m_formConbinedArea = new Hashtable<CellPosition, IArea>();
		}
		IArea srcCombArea = m_formConbinedArea.get(srcPos);
		if (srcCombArea == null) {// 尚未缓存，从格式模型取一次合并单元
			CombinedAreaModel fmtcombModel = CombinedAreaModel.getInstance(m_repModel.getFormatModel());
			CombinedCell[] fmtcombCells = fmtcombModel.getCombineCells();
			CombinedCell fmtcombCell = getBelongToCombinedCell(fmtcombCells, srcPos);
			if (fmtcombCell != null) {
				srcCombArea = fmtcombCell.getArea();
			} else
				srcCombArea = srcPos;
			m_formConbinedArea.put(srcPos, srcCombArea);
		}
		if (srcCombArea == null) {
			srcCombArea = srcPos;
		}
		FormulaVO formulaVO = m_fmlModel.getDirectFml(srcCombArea);
		if (formulaVO == null) {
			formulaVO = m_fmlModel.getDirectFml(srcPos);
		}
		if (formulaVO == null) {
			return;
		}

		IArea destArea = destPos;
		// if (!srcCombArea.equals(srcPos)) {//
		// 如果格式中是合并单元，则数据模型中查找合并区域，再设置公式（有必要么？）
		// if (m_repModel.getDataModel() != null) {
		// CombinedAreaModel datacombModel =
		// CombinedAreaModel.getInstance(m_repModel.getDataModel());
		// CombinedCell[] datacombCells = datacombModel.getCombineCells();
		// CombinedCell datacombCell = getBelongToCombinedCell(datacombCells,
		// destPos);
		//
		// if (datacombCell != null) {
		// destArea = datacombCell.getArea();
		// }
		// }
		// }
		formulaRef.put(destArea, formulaVO);
	}

	/**
	 * 记录单元位置上定义的条件格式
	 * 
	 * @create by wanyonga at 2010-7-8,上午09:07:31
	 * 
	 */
	private void recordCellConditionFormat(IAreaOutput output, CellPosition srcPos, CellPosition destPos,
			Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition> condFormatRef) {
		if (condFormatRef == null) {
			return;
		}
		if (m_formConbinedArea == null) {
			m_formConbinedArea = new Hashtable<CellPosition, IArea>();
		}
		IArea srcCombArea = m_formConbinedArea.get(srcPos);
		if (srcCombArea == null) {// 尚未缓存，从格式模型取一次合并单元
			CombinedAreaModel fmtcombModel = CombinedAreaModel.getInstance(m_repModel.getFormatModel());
			CombinedCell[] fmtcombCells = fmtcombModel.getCombineCells();
			CombinedCell fmtcombCell = getBelongToCombinedCell(fmtcombCells, srcPos);
			if (fmtcombCell != null) {
				srcCombArea = fmtcombCell.getArea();
			} else
				srcCombArea = srcPos;
			m_formConbinedArea.put(srcPos, srcCombArea);
		}
		if (srcCombArea == null) {
			srcCombArea = srcPos;
		}
		// 取的格式态的条件格式模型
		ConditionFormatModel format_condFormatModel = (ConditionFormatModel) ConditionFormatModel.getInstance(
				m_repModel.getFormatModel()).clone();
		com.ufsoft.table.format.condition.IAreaCondition conditionFormat = format_condFormatModel
				.getCondition(srcCombArea);
		if (conditionFormat == null) {
			conditionFormat = format_condFormatModel.getCondition(srcPos);
		}
		if (conditionFormat == null) {
			return;
		}
		try {
			for (IConditionFormat cf : conditionFormat.getConditionFormat()) {
				if (cf.getConditionRole().equals(ConditionRole.EXPR)) {
					ExprConditionFormat exprFormat = (ExprConditionFormat) cf;
					String exprContent = exprFormat.getExprContent();
					IParsed objParsed = exprFormat.getParsedLet();
					if (objParsed == null) {
						objParsed = output.getFormulaHandler().parseExpr(exprContent);
					}
					String newExprContent = AreaFormulaUtil.modifyAbsFormula(srcPos, destPos, exprContent, objParsed,
							m_repModel.getDataModel().getMaxRow(), m_repModel.getDataModel().getMaxCol(), this.getFormularRegionAdjust(output));
					exprFormat.setExprContent(newExprContent);
				}
			}

			IArea destArea = destPos;
			condFormatRef.put(destArea, (com.ufsoft.table.format.condition.IAreaCondition) conditionFormat.clone());
		} catch (Exception e) {
			AppDebug.debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0", "01413006-0855")/*
																											 * @
																											 * res
																											 * "拷贝条件格式到目标位置:"
																											 */
					+ destPos.toString() + ":" + conditionFormat.toString(), e);
		}
	}

	private IFormularRegionAdjust getFormularRegionAdjust(IAreaOutput output) {
		if(output instanceof CellsAreaOutput){
			return ((CellsAreaOutput)output).getFormularRegionAdjust();
		}
		else{
			return null;	
		}
	}

	/**
	 * cell单元是否在合并单元格中，如果是合并单元格则返回改合并单元 在公式记录map中，记录了公式在合并单元的位置。
	 * 
	 * @param combinedCells
	 * @param cell
	 * @return
	 */
	private CombinedCell getBelongToCombinedCell(CombinedCell[] combinedCells, CellPosition cell) {
		if (combinedCells == null || cell == null) {
			return null;
		}
		for (CombinedCell combCell : combinedCells) {
			if (combCell.getArea().contain(cell)) {
				return combCell;
			}
		}
		return null;
	}

	// /**
	// * 记录区域位置上定义的公式
	// *
	// * @create by wanyonga at 2010-7-13,上午11:07:06
	// *
	// * @param formulaRef
	// */
	// private void recordAreaFormulaAttr(Hashtable<IArea, FormulaVO>
	// formulaRef)
	// {
	// FormulaVO formulaVO = null;
	// Hashtable<IArea, FormulaVO> table = m_fmlModel.getMainFmls();
	// for (IArea area : table.keySet())
	// {
	// if (!area.isCell())
	// {
	// formulaVO = m_fmlModel.getDirectFml(area);
	// // formulaRef.put(area, formulaVO);
	// }
	// }
	// }

	public CrossDataModel getCrossDataModel() {
		return m_crossData;
	}

	public void clearData() {
		m_crossData = null;
	}

	private ExDataDescriptor doCrossExpand() {

		if (getSmartModel() == null) {// 如果语义模型为空，则直接返回扩展区大小
			return new ExDataDescriptor(m_exCell.getArea().getWidth(), m_exCell.getArea().getHeigth());
		}

		// @edit by ll at 2011-5-26,上午11:27:41
		// 计算每级分组的数据行数以及统计值
		getDSTool().getGroupDataCounts(isCross(), getAreaFields(),
				DataRelation.getInstance(m_repModel.getExtendAreaModel()).getAreaRelation(getAreaPK()), m_repModel);

		this.m_exCell.getAreaInfoSet().getCrossSetInfo().setFixHeader(null, true);
		this.m_exCell.getAreaInfoSet().getCrossSetInfo().setFixHeader(null, false);
		// cross.setFixHeader(null, true);// 清除固定表头
		// cross.setFixHeader(null, false);

		ExtendAreaCell exCell = m_exCell;
		ExtendAreaModel extendModel = m_repModel.getExtendAreaModel();
		Object query = m_areaContext.getAttribute(FreePrivateContextKey.REPORT_DOQUERY);
		if (query != null && (Boolean) query) {// 只有查询过数据时才去处理数据依赖
			AreaDataRelation dataRelation = DataRelation.getInstance(extendModel).getAreaRelation(getAreaPK());
			if (dataRelation != null && dataRelation.isEnabled()) {// 看看是否需要固定表头
				CrossTableRelation crossRelation = new CrossTableRelation(dataRelation, this, m_exCell.getAreaInfoSet()
						.getCrossSetInfo(), m_repModel);
				CrossTableHeader rowHeader = crossRelation.getRowFixHeader();
				if (rowHeader != null)
					m_exCell.getAreaInfoSet().getCrossSetInfo().setFixHeader(rowHeader, true);
				CrossTableHeader colHeader = crossRelation.getColFixHeader();
				if (colHeader != null)
					m_exCell.getAreaInfoSet().getCrossSetInfo().setFixHeader(colHeader, false);
			}
		}

		m_crossData = new CrossDataModel(exCell, m_busiFormat, getDSTool(), m_repModel);
		m_combProc = new AreaCombineProcessor(m_exCell);

		// 进行交叉表的全部扩展计算
		AreaPosition area = AreaPosition.getInstance(0, 0, exCell.getArea().getWidth(), exCell.getArea().getHeigth());
		fillDataByRowLvl(null, area, area, m_crossData,
				exCell.getBaseInfoSet().getExMode() == ExtendAreaConstants.EX_MODE_X, 0, 0,
				m_rowLvls.getRowCount() - 1, new ArrayList<AbsRowData>(), -2, 0, null, true);
		ExDataDescriptor ex = null;
		boolean isTurn = false;
		Object obj = m_repModel.getContext().getAttribute(FreePrivateContextKey.KEY_CROSS_TURN);
		if (obj instanceof Boolean) {
			isTurn = (Boolean) obj;
		}
		if (isTurn) {
			ex = new ExDataDescriptor(m_crossData.getRows(), m_crossData.getCols());
		} else {
			ex = new ExDataDescriptor(m_crossData.getCols(), m_crossData.getRows());
		}
		return ex;
	}

	/**
	 * @i18n miufo00331=数据级次错误，前面的算法有误？
	 */
	private ExDataDescriptor doListExpand() {
		ExtendAreaCell exCell = m_exCell;
		if (BaseSmartQueryUtil.isPubRepUnDoQuery(getAreaContext()) || getSmartModel() == null) {// 如果语义模型为空，则直接返回扩展区大小
			return new ExDataDescriptor(m_exCell.getArea().getWidth(), m_exCell.getArea().getHeigth());
		}
		Object obj = m_repModel.getContext().getAttribute(FreePrivateContextKey.KEY_ISQUERY_REFURBISH);
		if (obj instanceof Boolean && !((Boolean) obj).booleanValue()) {// 发布后节点小计时
																		// 选作排序在分组
			SortDescriptor sortDes = AreaDescUtil.getSortDescriptor(m_flds, m_exCell, m_repModel);
			if (sortDes != null) {
				getDSTool().sortDateSet(sortDes, getAreaFields().getFieldConverter());
			}
		}
		// 1。每级分组的数据行数
		int[] gLvlCount = getDSTool().getGroupDataCounts(isCross(), m_flds,
				DataRelation.getInstance(m_repModel.getExtendAreaModel()).getAreaRelation(getAreaPK()), m_repModel);

		// @edit by yuyangi at 2012-6-12,上午10:58:11 增加对树的分组数据个数的调整
		// Object info = exCell.getExtFmt(CollapsibleInfo.class.getName());
		if (CollapsibleInfo.isCollapsibaleInfoEnable(exCell, null)) {
			gLvlCount = getDSTool().recalGroupLvlCount(gLvlCount);
		} else {
			// 进行内存排序 发布后节点
			// @edit by yuyangi at 2012-6-5,下午06:42:33
			MemoryOrderMng memoryOrderMng = MemoryOrderMng.getInstance(exCell);
			MemoryOrderMngUtil.sort4list(memoryOrderMng, getDSTool().getGroupDataSet(),
					MemoryOrderMngUtil.getNullSortType(exCell));
		}

		// 2。计算格式行扩展最后的总行数
		int exRow = 0;
		for (int i = 0; i < getAreaRowLevel().getRowCount(); i++) {
			// 逐行添加扩展后的行数
			int row = m_rowLvls.getRowLvl(i).getRowLvl();
			if (row < 0)// 固定文本
				exRow++;
			else {
				if (row >= gLvlCount.length)
					throw new IllegalArgumentException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"1413006_0", "01413006-0911")/*
														 * @res
														 * "数据级次错误，前面的算法有误？"
														 */);
				else{
					exRow += gLvlCount[row];
				}
			}
		}

		// 3.return extend results
		ExDataDescriptor desc = null;
		if (exCell.getBaseInfoSet().getExMode() == ExtendAreaConstants.EX_MODE_X)// 横向扩展
			desc = new ExDataDescriptor(exRow, exCell.getArea().getHeigth());
		else
			desc = new ExDataDescriptor(exCell.getArea().getWidth(), exRow);
		AppDebug.debug("calcAreaSize:" + desc);
		return desc;
	}

	public IContext getAreaContext() {
		if (m_areaContext == null) {
			initAreaContext(getReportModel().getContext());
		}
		return m_areaContext;
	}

	public void setDsTool(AnaDataSetTool m_dsTool) {
		this.m_dsTool = m_dsTool;
	}

	public AnaDataSetTool getDSTool() {
		if (m_dsTool == null) {
			m_dsTool = new AnaDataSetTool(m_repModel.getContext(), this);
		}
		return m_dsTool;
	}

	private void clearDescList() {
		if (m_descListAggr == null)
			m_descListAggr = new ArrayList<Descriptor>();
		else
			m_descListAggr.clear();
		if (m_descList == null)
			m_descList = new ArrayList<Descriptor>();
		else
			m_descList.clear();
		if (m_dCellFormat == null)
			m_dCellFormat = new Hashtable<CellPosition, IFormat>();
		else
			m_dCellFormat.clear();
		if (m_formConbinedArea == null)
			m_formConbinedArea = new Hashtable<CellPosition, IArea>();
		else
			m_formConbinedArea.clear();
		if (getPolicy().isReportQuery() || getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL_ALL) == null) {// 只要不是内存计算所有数据的情况，清除已经加载的数据
			if (getDSTool() != null)
				getDSTool().clearDS();
		}
	}

	ArrayList<Descriptor> getDescList(boolean isAggr) {
		if (isAggr)
			return m_descListAggr;
		else
			return m_descList;
	}

	/**
	 * 根据汇总数据查询对应明细数据，用于汇总数据联查的场合
	 * 
	 * @create by ll at 2011-3-24,下午04:38:52
	 * 
	 * @param withoutAggrFld
	 * @param appendDesc
	 * @param rowFilter
	 * @return
	 */
	DataSet doGetDetailDS(boolean withoutAggrFld, Descriptor[] appendDesc, Hashtable<String, Condition[]> rowFilter) {
		SmartModel model = getSmartModel();
		if (model == null) {
			return null;
		}
		Descriptor[] desc = AnaDataSetTool.mergeDescriptor(model, withoutAggrFld, m_descList, appendDesc, rowFilter);

		// 2.4 数据集open

		if (!m_flds.bPaging) {
			FreeQueryParam pa = createQueryParam(FreeQueryParam.DSTYPE_DETAIL, desc);
			DataSet[] dsArray = FreeReportQueryExcutor.execQueryByPaginalParam(m_repModel.getSmartUtil(),
					new FreeQueryParam[] { pa });
			return dsArray[0];

		}
		return null;
	}

	private ArrayList<Descriptor> addGroupSortToDesc(ArrayList<Descriptor> list) {
		if (m_flds == null)
			return list;
		String[] grpFlds = m_flds.getGroupFldNames();
		if (grpFlds == null)
			return list;

		ArrayList<AnaRepField> flds = m_flds.getAllAnaFields();
		ArrayList<SortItem> al_sorts = new ArrayList<SortItem>();
		AnaRepField fld = null;
		for (int i = 0; i < flds.size(); i++) {
			fld = flds.get(i);
			if (fld != null && !fld.isNoOrder()) {
				al_sorts.add(new SortItem(fld.getFieldID(), fld.getOrderType() == AnaRepField.ORDERTYPE_DESCENDING));
			}
		}
		SortDescriptor sortDes = new SortDescriptor();

		// 将所有分组字段的排序调整到前面
		for (int i = 0; i < grpFlds.length; i++) {
			String name = grpFlds[i];
			int idx = -1;
			for (int j = 0; j < al_sorts.size(); j++) {
				if (name.equals(al_sorts.get(j).getFieldName())) {
					idx = j;
					break;
				}
			}
			SortItem sort = null;
			if (idx >= 0)
				sort = al_sorts.remove(idx);
			else
				sort = AreaDescUtil.getDefaultSortItem(name, getExAreaCell());
			al_sorts.add(i, sort);

			sortDes.addSort(sort);
		}
		if (sortDes.getSortNum() > 0)
			list.add(sortDes);
		return list;
	}

	@SuppressWarnings("unused")
	private void addColumnSortToDesc(SortDescriptor dsDesc, Field[] columnFlds) {
		if (m_flds == null)
			return;
		ArrayList<AnaRepField> flds = m_flds.getAllAnaFields();
		ArrayList<SortItem> al_sorts = new ArrayList<SortItem>();
		AnaRepField fld = null;
		for (int i = 0; i < flds.size(); i++) {
			fld = flds.get(i);
			if (fld != null && !fld.isNoOrder()) {
				for (int j = 0; j < columnFlds.length; j++) {
					if (fld.getField().equals(columnFlds[j])) {
						al_sorts.add(new SortItem(fld.getField().getFldname(),
								fld.getOrderType() == AnaRepField.ORDERTYPE_DESCENDING));

					}
				}
			}
		}
		String[] grpFlds = m_flds.getGroupFldNames();
		if (grpFlds != null) {// 将所有分组字段的排序调整到前面
			for (int i = 0; i < grpFlds.length; i++) {
				String name = grpFlds[i];
				int idx = -1;
				for (int j = 0; j < al_sorts.size(); j++) {
					if (name.equals(al_sorts.get(j).getFieldName())) {
						idx = j;
						break;
					}
				}
				SortItem sort = null;
				if (idx >= 0)
					sort = al_sorts.remove(idx);
				else
					sort = new SortItem(name, false);
				al_sorts.add(i, sort);
				dsDesc.addSort(sort);
			}
		}
		for (SortItem sort : al_sorts) {
			dsDesc.addSort(sort);
		}
	}

	private FilterDescriptor[] addAreaFilterToDesc(FilterDescriptor dsDesc, SmartModel model, IContext context) {
		if (m_flds != null) {
			ArrayList<AnaRepField> flds = m_flds.getAllAnaFields();
			for (int i = 0; i < flds.size(); i++) {
				addFilterFromFixValues(dsDesc, flds.get(i));
			}
		}
		return AreaDescUtil.addFilterToDesc(m_exCell, m_repModel.getExtendAreaModel(), dsDesc, model, context);
	}

	/** 从固定成员中抽取描述器 */
	private void addFilterFromFixValues(FilterDescriptor dsDesc, AnaRepField fld) {
		boolean isCharType = true;
		isCharType = fld.getFieldDataType() == Variant.STRING;
		if (fld != null && fld.getFixValues() != null && fld.getFixValues().size() > 0) {
			ArrayList<FixField> anaFlds = fld.getFixValues();
			ArrayList<String> values = new ArrayList<String>();
			for (int j = 0; j < anaFlds.size(); j++) {
				values.add(anaFlds.get(j).getID().toString());
			}
			String value = AnaDataSetTool.convert2String(values, isCharType);
			FilterItem item = new FilterItem();
			item.setOperation(FilterItem.FILTER_OPERATOR_IN);
			item.setFieldInfo(fld.getField().getExpression(), fld.getField().getDataType());
			item.setValue(value);
			dsDesc.addFilter(item);
		}
	}

	/**
	 * 增加页纬度对应的筛选描述器
	 * 
	 * @param dsDesc
	 */
	private FilterDescriptor getPageDimDescriptor() {
		if (m_repModel == null)
			return null;
		ReportCondition reportCondition = ReportCondition.getInstance(m_repModel.getExtendAreaModel());
		FilterDescriptor pageFilter = AreaDescUtil.getPageDimFilterDesc(reportCondition, getAreaPK());
		if (pageFilter != null) {
			m_areaContext.setAttribute(FreeReportContextKey.KEY_PAGEDIM_CONDITION, m_repModel.getContext()
					.getAttribute(FreeReportContextKey.KEY_PAGEDIM_CONDITION));
		} else
			m_areaContext.removeAttribute(FreeReportContextKey.KEY_PAGEDIM_CONDITION);
		return pageFilter;
	}

	/*
	 * 递归方法，设置某一分组级次的展开内容
	 */
	private int fillDataByRowLvl(IAreaOutput cells, AreaPosition area, AreaPosition realArea,
			CrossDataModel crossModel, boolean isHor, int currCellRow, int sfRow, int efRow,
			ArrayList<AbsRowData> parentGrpData, int currLvl, int currLoop, int[] range, boolean isCross) {
		AbsRowData currGrpData = getGroupData(parentGrpData, currLvl, currLoop);// 当前级次的GroupData
		if (currGrpData != null && !currGrpData.isShowData()) {
			return currCellRow;
		}
		boolean isOut = false;
		// 前半部分
		if (currLoop > 0 && m_flds.isGroupPageBreak(currLvl)) {// 如果当前分组有强制分页，则对后面的组数据添加分页符
			FreePageBreak pageBreak = m_flds.getGroupPageBreak(currLvl);
			FreePageOutputInfo pageBreaks = (FreePageOutputInfo) cells
					.getReportInfo(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK);
			int startRow = isHor ? area.getStart().getColumn() : area.getStart().getRow();
			pageBreaks.addPageBreak(!isHor, startRow + currCellRow, pageBreak);
		}
		int oldCurrRow = currCellRow;
		int nextBegin = sfRow;
		for (int i = sfRow; i <= efRow; i++) {
			int lvl = m_rowLvls.getRowLvl(i).getRowLvl();
			// 判断下级字段是否显示
			boolean isNextLevelShow = true;
			if(currGrpData != null && currGrpData.getSubDatas() != null && currGrpData.getSubDatas().get(0) != null){
				isNextLevelShow = currGrpData.getSubDatas().get(0).isShowData();
			}
			// 判断本级字段是否有小计
			boolean isCount = false;
			Map<Integer, Boolean> groupWithCount = m_flds.getGroupWithCount();
			if(groupWithCount != null && Boolean.TRUE == groupWithCount.get(currLvl)){
				isCount = true;
			}
			// modified by yanchm , 2014.10.13 ,如果某行分组下的明细字段全部不显示，且该分组不存在小计，则用该分组字段填充（折叠显示时，分组与明细在同一行，此时lvl != currLvl, 但仍需执行填充）
			if (lvl == currLvl || (!isNextLevelShow && !isCount)) {// 展开本行
				if ((cells != null) && currCellRow > range[1]) { // 超出目标区域，强行中止
					isOut = true;
					break;
				}
				if (cells == null || currCellRow >= range[0]) {// 实际区域中
					// 实际数据填充
					boolean isFill = FillRowData(cells, area, crossModel, isHor, currCellRow, i, parentGrpData,
							currGrpData, currLoop, isCross);
					if (isFill)
						currCellRow++;
				} else {
					currCellRow++;
				}
				nextBegin++;
			} else {
				break;
			}
		}
		int nextLen = currCellRow;
		// 下一级次
		int nextEnd = efRow;// 下一级次的结尾位置
		int oldCellRow = currCellRow;
		if (!isOut) {
			if (nextBegin <= efRow) {// 存在下一级次
				for (int i = efRow; i >= nextBegin; i--) {
					int lvl = m_rowLvls.getRowLvl(i).getRowLvl();
					if (lvl == currLvl)
						nextEnd--;
					else
						break;
				}
				int size = m_flds.getGroupFldNames().length;
				boolean isCrossLvl = (m_flds.isCross() && (currLvl == size - 1));// 是否到达交叉表的细节填充级次
				if (isCrossLvl) {// 对于交叉的细节区进行填充
					currCellRow = fillSubCrossData(getDSTool().getDS(FreeQueryParam.DSTYPE_AGGR), crossModel, isHor,
							currCellRow, nextBegin, nextEnd, (GroupRowData) currGrpData, currLvl + 1);
					if (currCellRow == -1) {
						isOut = true;
					}
				} else {// 递归调用，继续填充
					if (currGrpData != null)
						parentGrpData.add(currGrpData);
					int dataLoop = getDataCount(currGrpData, currLvl + 1); // 下一级次的数据循环数
					int idx = 0;
					if (!m_flds.isCross()) {
						if (currCellRow < range[0] && currLvl == m_rowLvls.getMaxLvl() - 1) {// 对于明细数据，如果未到达区域，直接计算数据loop索引
							int lvlRows = m_rowLvls.getLvlRows(currLvl + 1)[0];
							idx = (range[0] - currCellRow) / lvlRows;
							currCellRow += Math.min(idx, dataLoop) * lvlRows;
						}
					}
					for (int i = Math.min(idx, dataLoop); i < dataLoop; i++) {
						currCellRow = fillDataByRowLvl(cells, area, realArea, crossModel, isHor, currCellRow,
								nextBegin, nextEnd, parentGrpData, currLvl + 1, i, range, isCross);
						if (currCellRow == -1) {
							isOut = true;
							break;
						}
					}
					if (currGrpData != null)
						parentGrpData.remove(parentGrpData.size() - 1);
				}
			}

			nextLen = currCellRow - nextLen - nextEnd + nextBegin - 1;// 记录下一级次扩展出的行数增量
			oldCellRow = currCellRow;
			// 后半部分
			if (nextEnd < efRow && !isOut) {
				for (int i = nextEnd + 1; i <= efRow; i++) {
					if ((cells != null) && currCellRow > range[1]) {// 超出目标区域，强行中止
						isOut = true;
						break;
					}
					if (cells == null || currCellRow >= range[0]) {// 实际区域中
						// 实际数据填充
						boolean isFill = FillRowData(cells, area, crossModel, isHor, currCellRow, i, parentGrpData,
								currGrpData, currLoop, isCross);
						if (isFill)
							currCellRow++;
					} else {// 未到达填充区域，行数++
						currCellRow++;
					}
				}
			}
			if (m_combProc.m_fmtCombined != null) {// 需要进行合并单元格的处理
				if (nextBegin <= efRow) {// 存在下一级次
					// 处理这一部分格式中设置了的合并单元
					m_combProc.combineCellInOneLvl(isCross, area.getStart(), crossModel, isHor, oldCurrRow, sfRow,
							nextBegin - 1);
					m_combProc.combineCellInOneLvl(isCross, area.getStart(), crossModel, isHor, oldCellRow, nextEnd + 1,
							efRow);

					// 处理本组内的跨级的合并单元格
					if (!isCross)// 交叉表不用处理组内跨级合并
						m_combProc.combineCellBetweenLvls(area.getStart(), isHor, oldCurrRow, sfRow, efRow, nextBegin,
								nextEnd, nextLen, currLvl);
				} else {
					// 处理这一部分格式中设置了的合并单元
					m_combProc.combineCellInOneLvl(isCross, area.getStart(), crossModel, isHor, oldCurrRow, sfRow, efRow);
				}
			}
		}
		if (isOut)
			return -1;
		return currCellRow;
	}

	/**
	 * 计算实际区域的行区间，用于判断超出时及时终止
	 * range[0]：实际区域开始行号（扩展区域内的相对行号）；range[1]：实际区域结束行号（扩展区域内的相对行号）
	 * 
	 * @return
	 */
	private int[] getRealAreaRange(AreaPosition area, AreaPosition realArea, boolean isHor) {
		int rowstart = isHor ? (realArea.getStart().getColumn() - area.getStart().getColumn()) : (realArea.getStart()
				.getRow() - area.getStart().getRow());// 实际区域之前的行数
		int rowend = rowstart + (isHor ? realArea.getWidth() : realArea.getHeigth()) - 1;// 实际区域截止行数
		return new int[] { rowstart, rowend };
	}

	/*
	 * 填充一行数据,同时处理单元格式和行内的合并单元格
	 */
	@SuppressWarnings("unchecked")
	private boolean FillRowData(IAreaOutput cells, AreaPosition area, CrossDataModel crossModel, boolean isHor,
			int currCellRow, int cfRow, ArrayList<AbsRowData> parentGrpData, AbsRowData currData, int currLoop,
			boolean isCross) {

		if (currData != null && (currData.isIgnoreData() || !currData.isShowData()))
			return false;
		// AppDebug.debug("————"+currCellRow+"||"+currData);

		CellPosition pos = area.getStart();
		CellPosition fStart = m_exCell.getArea().getStart();
		int width = isHor ? area.getHeigth() : area.getWidth();

		Hashtable<CellPosition, CellPosition> cpRef = null;// 位置对应信息
		Hashtable<CellPosition, TraceDataParam> traceRef = null;// 数据追踪信息
		Hashtable<CellPosition, Object> dataValueRef = null;// 数据追踪信息
		Hashtable<IArea, FormulaVO> formulaRef = null;// 区域公式信息
		Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition> condFormatRef = null;// 区域条件格式信息
		Hashtable<String, CellPosition> fieldPosMap = null;// 字段和填充位置映射，公式处理中需要
		GroupDataIndex dataIndex = null;
		if (isCross) {
			crossModel.appendOneRow();
			fieldPosMap = new Hashtable<String, CellPosition>();
		} else {
			cpRef = (Hashtable<CellPosition, CellPosition>) cells.getReportInfo(ExtendAreaConstants.CELLPOS_REFER_MAP);
			traceRef = (Hashtable<CellPosition, TraceDataParam>) cells
					.getReportInfo(ExtendAreaConstants.TRACEDATA_REFER_MAP);
			dataValueRef = (Hashtable<CellPosition, Object>) cells
					.getReportInfo(ExtendAreaConstants.DATAVALUE_REFER_MAP);
			condFormatRef = (Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition>) cells
					.getReportInfo(ExtendAreaConstants.CONDITIONFORMAT_REFER_MAP);
			if (m_hasFormular) {
				formulaRef = (Hashtable<IArea, FormulaVO>) cells.getReportInfo(ExtendAreaConstants.FORMULA_REFER_MAP);

				Hashtable<String, AreaPrivateMap> areaMap = (Hashtable<String, AreaPrivateMap>) cells
						.getReportInfo(ExtendAreaConstants.AREA_INFO_MAP);

				if (!areaMap.containsKey(getAreaPK()))
					areaMap.put(getAreaPK(), new AreaPrivateMap());
				Hashtable<Integer, GroupDataIndex> dataIndexRef = (Hashtable<Integer, GroupDataIndex>) areaMap.get(
						getAreaPK()).getDataIndexRef();// 记录每个扩展行对应的数据序号，为公式的数据通道准备的
				dataIndex = new GroupDataIndex(parentGrpData, currLoop);
				dataIndexRef.put(currCellRow, dataIndex);
				Hashtable<GroupDataIndex, Hashtable<String, CellPosition>> dataIndexFieldposRef = (Hashtable<GroupDataIndex, Hashtable<String, CellPosition>>) areaMap
						.get(getAreaPK()).getDataIndexFieldPosRef();// 记录数据序号对应的字段填充位置，为公式的扩展替换准备的
				fieldPosMap = dataIndexFieldposRef.get(dataIndex);
				if (fieldPosMap == null) {
					fieldPosMap = new Hashtable<String, CellPosition>();
					dataIndexFieldposRef.put(dataIndex, fieldPosMap);
				}
			}

		}

		boolean isSample = (m_rowLvls.getRowLvl(cfRow).getRowLvl() <= -1) ? true : false;// 如果是表样和合计行，不记录数据序号对应的字段填充位置
		int rowHeight = -1;
		CellPosition lastAimPos = null;
		SeperateLockSet lockSet = getReportModel().getFormatModel().getTableSetting().getSeperateLockSet();
		boolean isFrozenRow = (lockSet != null && lockSet.isFreezing() && pos.getRow() + 2 + currCellRow <= lockSet
				.getSeperateRow());// 是否是冻结行范围内

		for (int i = 0; i < width; i++) {
			// 填充一个单元格
			CellPosition aimPos = isHor ? pos.getMoveArea(i, currCellRow) : pos.getMoveArea(currCellRow, i);
			CellPosition srcPos = isHor ? fStart.getMoveArea(i, cfRow) : fStart.getMoveArea(cfRow, i);
			if (rowHeight == -1) {// 记录首个有效单元的行高/列宽
				if (isHor) {
					if (cfRow < m_exCell.getAreaColsWidth().length) {
						rowHeight = m_exCell.getAreaColsWidth()[cfRow];
					}
				} else {
					if (cfRow < m_exCell.getAreaRowsHeight().length) {
						rowHeight = m_exCell.getAreaRowsHeight()[cfRow];
					}
				}
			}
			
			// modified by yanchm , 2014.12.3
			// ---begin---
			// 该部分前移，主要考虑到防止因为srcExMap为空导致设置的条件格式不生效，或者单元公式不生效（当单元格中没有数据时，srcExMap为空）
			recordCellFormulaAttr(cells, srcPos, aimPos, formulaRef);// 记录扩展区单元位置上定义的区域公式
			recordCellConditionFormat(cells, srcPos, aimPos, condFormatRef); // 记录扩展区域单元位置上定义的条件格式
			// ---end---
			
			Map<String, Object> srcExMap = m_exCell.getCellInfoSet().getExtInfo(srcPos);

			if (srcExMap == null || srcExMap.size() <= 0) {// 格式单元无信息
				if (!isCross)
					cpRef.put(aimPos, srcPos);// 记录位置映射关系
				continue;
			}
			if (!m_bFillData && !isFrozenRow && !isCross()) {// 当前不需要填充真实数据（对于固定行列、交叉区都会真实填充数据）
				cells.setCellData(aimPos, ExtendAreaConstants.CELL_VALUE, ".", true);
				lastAimPos = aimPos;
				continue;
			}
			AnaRepField fld = (AnaRepField) srcExMap.get(AnaRepField.EXKEY_FIELDINFO);
			// 计算单元取值
			AnaDataValue dataValue = getDSTool()
					.getDataValue(null, srcExMap.get(ExtendAreaConstants.CELL_VALUE), fld, parentGrpData, currData,
							currLoop, getPolicy().isDispOnly(), srcPos, aimPos, m_exCell.getExAreaPK());
			Object realValue = dataValue.getValue();
			// 从分组数据中取出的值，有可能是DataValue，在此处转换出其中的实际值。防止以DataValue填值，渲染器不识别
			if (realValue instanceof DataValue) {
				realValue = ((DataValue) realValue).getValue();
			}
			Object dispValue = dataValue.getDispValue();
			if (dispValue instanceof DataValue) {
				dispValue = ((DataValue) dispValue).getValue();
			}
			// @editby yuyangi 先记录位置关系
			if (!isCross) {
				cpRef.put(aimPos, srcPos);// 记录位置映射关系
			}

			IFormat cellFormat = (IFormat) srcExMap.get(ExtendAreaConstants.CELL_FORMAT);// 为了控制内存占用，直接使用格式
			// edit by yuyangi 将参值传递realValue
			Object[] valueAndFmt = getDataFormatAttr(fld, dispValue, aimPos, cellFormat);

			valueAndFmt = getReportModel().getValueAndDataFormat(m_exCell, aimPos, (IFormat) valueAndFmt[1],
					valueAndFmt[0]);

			dispValue = valueAndFmt[0];
			cellFormat = (IFormat) valueAndFmt[1];

			cellFormat = getDimFormat(srcPos, aimPos, cellFormat, fld, realValue, currData);
			// 如果小数位数为-1还需要根据原始数据获得小数位数
			if (cellFormat != null && cellFormat.getDataFormat() instanceof INumberFormat
					&& ((INumberFormat) cellFormat.getDataFormat()).getDecimalDigits() == -1) {
				int digit = -1;
				Object value = dataValue.getOriginalValue();
				if (value instanceof UFDouble) {
					digit = ((UFDouble) value).getPower();
				} else if (value instanceof BigDecimal) {
					digit = ((BigDecimal) value).scale();
				}
				if (digit < 0)
					digit = -digit;// 确保是正数
				if (digit != TableConstant.UNDEFINED)
					cellFormat = PropertyType.getNewFormatByType(cellFormat, PropertyType.DecimalDigits, digit);
			}
			// 单元格式 不为UFDoubleFormat的，都手工转成double型
			if (dispValue instanceof UFDouble && !(cellFormat.getCellType() == TableConstant.CELLTYPE_BIGNUMBER)) {
				dispValue = ((UFDouble) dispValue).doubleValue();
			}
			if (fld != null) {
				// 增加字段和填充单元的对照
				if (m_hasFormular && !isSample) {
					boolean isAggrFld = fld.isAggrFld();
					String dbName = isAggrFld ? fld.getFieldCountDef().getDbAlias() : fld.getFieldID();
					fieldPosMap.put(dbName, aimPos);
				}
				// 计算数值类型的格式信息（整数时要设置小数位数为0）
				int type = fld.getFieldDataType();
				if (!DataTypeConstant.isNumberType(type) && dispValue != null && !(dispValue instanceof IDName)) {
					boolean isCalcInt = (fld.isAggrFld() && (fld.getFieldCountDef().getCountType() == IFldCountType.TYPE_COUNT || fld
							.getFieldCountDef().getCountType() == IFldCountType.TYPE_COUNT_DISTINCT));
					if (!isCalcInt) {
						if (fld.getRankInfo() != null && fld.getRankInfo().isEnabled())
							isCalcInt = true;
					}
					if (!isCalcInt) {
						// dispValue = dispValue.toString();//保持原值
					} else {// 如果是计数或者是排名函数，取整数
						if (dispValue instanceof BigDecimal) {
							dispValue = ((BigDecimal) dispValue).intValue();
						} else if (dispValue instanceof Double) {
							dispValue = ((Double) dispValue).intValue();
						}
					}
				} else {// 如果是数值类型，有计数或者排名函数也取整数
					boolean isCalcInt = (fld.isAggrFld() && (fld.getFieldCountDef().getCountType() == IFldCountType.TYPE_COUNT || fld
							.getFieldCountDef().getCountType() == IFldCountType.TYPE_COUNT_DISTINCT));
					if (!isCalcInt) {
						if (fld.getRankInfo() != null && fld.getRankInfo().isEnabled())
							isCalcInt = true;
					}
					if (isCalcInt) {
						if (dispValue instanceof BigDecimal) {
							dispValue = ((BigDecimal) dispValue).intValue();
						} else if (dispValue instanceof Double) {
							dispValue = ((Double) dispValue).intValue();
						} else if (dispValue instanceof UFDouble) {
							dispValue = ((UFDouble) dispValue).intValue();
						}
					}
				}
			}
			if (!isCross) {
				lastAimPos = aimPos;

				// 设置目标单元的格式、条件格式和值
				// @edit by ll at 2011-9-2,上午10:04:56 效率优化，单元属性改成批量设置
				BatchCellDatas batchDatas = new BatchCellDatas();
				batchDatas.addData(ExtendAreaConstants.CELL_FORMAT, cellFormat);

				// Object condition =
				// srcExMap.get(ExtendAreaConstants.CELL_CONDITION_FORMAT);
				// if (condition != null) {
				// batchDatas.addData(ExtendAreaConstants.CELL_CONDITION_FORMAT,
				// ((com.ufsoft.table.format.condition.IAreaCondition)
				// condition).clone());
				// }
				batchDatas.addData(ExtendAreaConstants.CELL_VALUE, dispValue);
				if (realValue != null) {// 值的单独记录
					dataValueRef.put(aimPos, realValue);
				}

				if (srcExMap.get(AnaCellCombine.KEY) != null) {// 自动合并属性
					batchDatas.addData(AnaCellCombine.KEY, new AnaCellCombine(dataValue.getCombKey()));
				} else {
					batchDatas.addData(AnaCellCombine.KEY, null);
				}
				TraceDataParam trace = dataValue.getTraceInfo();
				if (trace != null) {// 数据追踪参数
					trace.addParam(FreeReportContextKey.KEY_ICONTEXT, m_areaContext);
					traceRef.put(aimPos, trace);
				}
				int rowNo = dataValue.getDetailRowNo();
				if (rowNo != -1) {// 对应的明细数据集行号
					batchDatas.addData(ExtendAreaConstants.DATASET_ROW_NO, rowNo);
				}
				if (fld != null && fld.getDimInfo().isExtended()) {
					batchDatas.addData(FreePrivateCommonKey.KEY_CELL_WRITABLE, Boolean.FALSE);
				}
				cells.setCellDataBatch(aimPos, batchDatas.getKeys(), batchDatas.getValues());
			} else {
				CrossCell cell = new CrossCell(cellFormat, dispValue);
				crossModel.addRowData(cell , srcPos , null );
				// 交叉表时，需要计算目标位置。pos位置为A1，根据扩展区起始位置与A1的相对位置，计算目标位置。
				int row = fStart.getRow() - pos.getRow();
				int col = fStart.getColumn() - pos.getColumn();
				CellPosition cp = aimPos.getMoveArea(row, col);
				aimPos = cp;
				crossModel.getRefPosMap().put(cp, srcPos);
			}

			// 增加排序筛选的属性
			if (fld != null) {
				// 筛选
				FreeReportUtil.addListFilterAttribute(m_repModel, m_exCell, fld, cpRef, aimPos, isHor);
				// 排序
				FreeReportUtil.addListSortAttribute(m_repModel, m_exCell, fld, cpRef, aimPos, isHor);
			}
		}
		if (lastAimPos != null) {// 设置行高/列宽
			int rr = -1;
			if (isHor) {
				rr = lastAimPos.getColumn();
				cells.setColInfo(rr, ExtendAreaConstants.COLUMN_WIDTH, rowHeight);
			} else {
				rr = lastAimPos.getRow();
				cells.setRowInfo(rr, ExtendAreaConstants.ROW_HEIGHT, rowHeight);
			}
			// AppDebug.debug("————设置行高，row__"+rr+"____height___"+rowHeight);
		}
		return true;
	}

	private Object[] getDataFormatAttr(AnaRepField fld, Object value, CellPosition srcPos, IFormat format) {
		if (fld == null) {
			value = getFmtDateValue(value, format);
			return new Object[] { value, format };
		}
		if (!(value instanceof ICellValue)) {
			if (fld.getDimInfo().getDispField() != null) {
				if (DataTypeConstant.isNumberType(fld.getDimInfo().getDispField().getDataType())) {
					if (!(value instanceof Number) && value != null) {
						try {
							value = new UFDouble(Double.valueOf(value.toString()));
						} catch (Exception e) {

						}
					}
				}
			} else {
				if (DataTypeConstant.isNumberType(fld.getField().getDataType())) {
					if (!(value instanceof Number) && value != null) {
						try {
							value = new UFDouble(Double.valueOf(value.toString()));
						} catch (Exception e) {

						}
					}
				}
			}
		}

		FieldAttExtModel fieldAttExtModel = (FieldAttExtModel) getReportModel().getFormatModel().getExtProp(
				FieldAttExtModel.KEY_FREE_FIELDATTRIBUTE);
		if (fieldAttExtModel == null) {
			value = getFmtDateValue(value, format);
			return new Object[] { value, format };
		}
		String smartId = getExAreaCell().getAreaInfoSet().getSmartModelDefID();
		FieldAttribute[] fldAttrs = fieldAttExtModel.getM_fldAtt(smartId);
		FieldAttribute aimFldAttr = null;
		Field field = fld.getField();
		if (fldAttrs != null) {
			for (FieldAttribute fldAttr : fldAttrs) {
				if (field instanceof FieldCountDef) {
					field = ((FieldCountDef) field).getMainField();
				}
				if (field == null) {// ?
					field = fld.getField();
				}
				if (fldAttr.getM_expression().equals(field.getExpression())
						|| fldAttr.getM_expression().equals(field.getFldname())) {
					aimFldAttr = fldAttr;
				}
			}
		}

		if (aimFldAttr == null) {
			value = getFmtDateValue(value, format);
			return new Object[] { value, format };
		}

		IFormat newFormat = format;
		if (FieldAttribute.TYPE_NUMERIC == aimFldAttr.getFldType()) {
			FieldAttrValue attrvalue = new FieldAttrValue();
			attrvalue.setFieldAttr(aimFldAttr);
			attrvalue.setValue(value);
			value = attrvalue;
		} else if (FieldAttribute.TYPE_ADD == aimFldAttr.getFldType()) {
			// 如果是地址类型，不在此处处理，preAddresFormatData中已经批量处理了
//			try {
//				value = AddressFormater.format(value.toString());
//			} catch (Exception e1) {
//				AppDebug.debug(e1);
//			}
		} else if (value != null && FieldAttribute.TYPE_NCDATE == aimFldAttr.getFldType()) {
			try {
				value = getFmtDateValue(value, format);
				// NCDateFor mat dataFormat = new NCDateFormat();
				value = getNCFormatterInstance().formatDate(value) == null ? value : getNCFormatterInstance().formatDate(value).getValue();
			} catch (Exception e) {
				AppDebug.debug(e);
			}
		} else if (FieldAttribute.TYPE_TIME == aimFldAttr.getFldType()) {
			try {
				value = getNCFormatterInstance().formatTime(value) == null ? value : getNCFormatterInstance().formatTime(value).getValue();
			} catch (Exception e) {
				AppDebug.debug(e);
			}
		} else if (FieldAttribute.TYPE_DATE == aimFldAttr.getFldType()) {
			try {
				value = getFmtDateValue(value, format);
				value = getNCFormatterInstance().formatDateNotShowTime(value) == null ? value : getNCFormatterInstance().formatDateNotShowTime(
						value).getValue();
			} catch (Exception e) {
				AppDebug.debug(e);
			}
		} else if (FieldAttribute.TYPE_DATETIME == aimFldAttr.getFldType()) {
			try {
				value = getNCFormatterInstance().formatDateTime(value) == null ? value : getNCFormatterInstance().formatDateTime(value).getValue();
			} catch (Exception e) {
				AppDebug.debug(e);
			}
		}

		return new Object[] { value, newFormat };
	}

	private Object getFmtDateValue(Object value, IFormat format) {
		// 轻量报表根据时区转换日期格式
		if (getReportModel().getContext().getAttribute("ufofr_portal_query") == null) {
			return value;
		}
		if (format != null && format.getDataFormat() instanceof DateFormat) {
			TimeZone timezone = (TimeZone) getReportModel().getContext().getAttribute(
					FreeReportContextKey.REPORT_EXEC_TIMEZONE);
			if (timezone == null) {
				return value;
			}
			try {
				DateFormat dateformat = (DateFormat) format.getDataFormat();
				UFDate date = UFDate.getDate(value.toString());
				int dateType = dateformat.getDateType();
				String fm = null;
				if (dateType == 0 || dateType == -2) {
					fm = "yyyy-MM-dd";
				} else if (dateType == 1) {
					fm = "yyyy/MM/dd";
				}
				SimpleDateFormat df = new SimpleDateFormat(fm);
				df.setTimeZone(timezone);
				return df.format(date.getMillis());
			} catch (Exception e) {
				AppDebug.debug(e);
			}
		}
		return value;
	}

	private AbsRowData getGroupData(ArrayList<AbsRowData> parentGrpData, int currLvl, int index) {
		if (currLvl < 0)// 固定文本
			return null;
		AbsRowData grpData = parentGrpData.size() == 0 ? null : parentGrpData.get(parentGrpData.size() - 1);
		RowDataArray subGrp = grpData == null ? getDSTool().getGroupDataSet().getTopDatas() : grpData.getSubDatas();
		if (subGrp == null)
			return null;
		if (index >= subGrp.length()) {
			// throw new IllegalArgumentException("组内序号计算错误啦");
			return null;
		}
		return subGrp.get(index);
	}

	/**
	 * 指定级次中某序号对应的明细数据行号
	 * 
	 * @param grpData
	 * @param currLvl
	 * @param index
	 * @return
	 */
	/*
	 * 某一级次的组数据行数 第一个参数是当前的外围分组信息
	 */
	private int getDataCount(AbsRowData grpData, int currLvl) {
		if (currLvl < 0)// 固定文本
			return 1;
		RowDataArray subGrp = grpData == null ? getDSTool().getGroupDataSet().getTopDatas() : grpData.getSubDatas();

		if (subGrp == null) {// 已经到了明细数据
			if (grpData == null) { // 对于没有下级数据的，保留格式状态的行数，以确保文字等信息能出现
				return Math.max(1, getDSTool().getDetailRowCount(getReportModel()));
			} else
				return 0;
		} else {// 继续处理组信息
			return subGrp.length();
		}
	}

	// 清除扩展信息，以便于重新计算
	void clearExtendDesc() {
		m_desc = null;
	}

	// 返回添加的数量
	private int addMetaDataToDataSet(Map<String, String[]> combineFldMap, String[] fs, DataSet ds) {
		int n = 0;
		if (fs == null) {
			return 0;
		}
		for (int i = 0; i < fs.length; i++) {
			if (combineFldMap != null && combineFldMap.containsKey(fs[i])) {
				String[] flds = combineFldMap.get(fs[i]);
				if (flds != null) {
					for (int j = 0; j < flds.length; j++) {
						n++;
						Field f = getDSTool().getAreaFlds().getFieldConverter().getField(flds[j]);
						ds.getMetaData().addField(f);
					}
				}
			}
			Field f = getDSTool().getAreaFlds().getFieldConverter().getField(fs[i]);
			ds.getMetaData().addField(f);
			n++;
		}
		return n;
	}

	private DataSet getEmptyDataSet(CrossTableSet cross) {
		DataSet ds = new DataSet();
		Map<String, String[]> map = cross.getAreaFields().getCombineFldMap();
		String[] cfs = cross.getColExpressions();
		String[] mfs = cross.getMeasureExpressions();
		String[] rfs = cross.getRowExpressions();
		int n = 0;
		n = n + this.addMetaDataToDataSet(map, cfs, ds);
		n = n + this.addMetaDataToDataSet(map, mfs, ds);
		n = n + this.addMetaDataToDataSet(map, rfs, ds);
		ds.setDatas(new Object[1][n]);
		return ds;
	}

	private DataSet getEmptyDataSet(DataSet ds) {
		DataSet temp = new DataSet();
		temp.setMetaData((MetaData) ds.getMetaData().clone());
		int n = ds.getMetaData().getFields().length;
		temp.setDatas(new Object[1][n]);
		return temp;
	}

	/**
	 * 进行交叉表的区域数据计算
	 * 
	 * @return
	 */
	private int fillSubCrossData(DataSet ds, CrossDataModel crossModel, boolean isHor, int currCellRow, int nextBegin,
			int nextEnd, GroupRowData currGrpData, int lvl) {
		// if(ds == null)
		// return currCellRow;
		CrossTableSet cross = AreaCrossSetInfoUtil.getCrossSet(m_repModel, m_exCell.getAreaInfoSet().getCrossSetInfo());
		cross.setAreaFields(getAreaFields());
		// 目前判断是否采集表的方法是：非自由报表模型即是采集表
		if (ds == null) {
			if (!(m_repModel instanceof AnaReportModel)) {
				return currCellRow;
			}
			// 为展现表头,构建一个空DataSet
			ds = getEmptyDataSet(cross);
		} else if (ds.getDatas() == null || ds.getDatas().length == 0) {
			if (!(m_repModel instanceof AnaReportModel)) {
				return currCellRow;
			}
			// 为展现表头,构建一个空DataSet
			ds = getEmptyDataSet(ds);
		}
		Object[][] subRows = null;
		if (currGrpData != null) {
			RowDataArray subData = currGrpData.getSubDatas();
			if (subData != null) {
				subRows = new Object[subData.length()][];
				for (int i = 0; i < subData.length(); i++) {
					subRows[i] = ((DetailRowData) subData.get(i)).getRowData();
				}
			}
		}
		DataSet subDS = subRows == null ? ds : new DataSet(ds.getMetaData(), subRows);
		String infinity = (String) m_repModel.getExtendAreaModel().getRepLevelInfo(FreePrivateCommonKey.KEY_INFINITY);
		String novalue = (String) m_repModel.getExtendAreaModel().getRepLevelInfo(FreePrivateCommonKey.KEY_NO_VALUE);
		CrossTableModel cModel = new CrossTableModel(cross, subDS, m_exCell.getAreaInfoSet().getCrossSetInfo()
				.getFixHeader(true), m_exCell.getAreaInfoSet().getCrossSetInfo().getFixHeader(false), m_exCell);
		cModel.setInfinity(infinity);
		cModel.setNovalue(novalue);

		if (m_areaDataAdjustor != null) {
			CrossTableModel cModel2 = m_areaDataAdjustor.doAdjustCrossHeader(getAreaPK(), m_areaContext, cModel,
					m_repModel);
			if (cModel2 != null)
				cModel = cModel2;
		}
		int cRows = crossModel.addCrossModel(currGrpData, cModel);
		currCellRow += cRows;
		return currCellRow;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<AreaPosition> doCrossFillData(IAreaOutput output, AreaPosition area, boolean isHor) {
		if (m_crossData == null)
			return null;
		boolean isTurn = false;
		Object obj = m_repModel.getContext().getAttribute(FreePrivateContextKey.KEY_CROSS_TURN);
		if (obj instanceof Boolean) {
			isTurn = (Boolean) obj;
		}
		// int aHeight = isHor ? area.getWidth() : area.getHeigth();
		// int aWidth = isHor ? area.getHeigth() : area.getWidth();
		int aHeight = isTurn ? area.getWidth() : area.getHeigth();
		int aWidth = isTurn ? area.getHeigth() : area.getWidth();
		CellPosition fmtPos = null;

		Hashtable<CellPosition, CellPosition> cpRef = (Hashtable<CellPosition, CellPosition>) output
				.getReportInfo(ExtendAreaConstants.CELLPOS_REFER_MAP);
		Hashtable<CellPosition, TraceDataParam> traceRef = (Hashtable<CellPosition, TraceDataParam>) output
				.getReportInfo(ExtendAreaConstants.TRACEDATA_REFER_MAP);
		Hashtable<CellPosition, Object> dataValueRef = (Hashtable<CellPosition, Object>) output
				.getReportInfo(ExtendAreaConstants.DATAVALUE_REFER_MAP);
		Hashtable<IArea, FormulaVO> formulaRef = (Hashtable<IArea, FormulaVO>) output
				.getReportInfo(ExtendAreaConstants.FORMULA_REFER_MAP);
		Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition> condFormatRef = (Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition>) output
				.getReportInfo(ExtendAreaConstants.CONDITIONFORMAT_REFER_MAP);
		HashMap<Integer, Integer> rowhigh = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> colwidth = new HashMap<Integer, Integer>();
		ArrayList<CellPosition> sort = new ArrayList<CellPosition>();
//		// 交叉表模型中的条件格式信息
//		Hashtable<CellPosition, com.ufsoft.table.format.condition.IAreaCondition> conditionRef = m_crossData.getConditionRef();
//		// 交叉表模型中的穿透信息
//		Hashtable<CellPosition, TraceDataParam> cross_traceRef = m_crossData.getTraceRef();
//		traceRef.putAll(cross_traceRef);
		// 缓存单元格对应的标题位置信息
		Hashtable<CellPosition, CellPosition> titleRefCache = new Hashtable<CellPosition, CellPosition>();
		
		for (int i = 0; i < m_crossData.getDataCount(); i++) {
			if (i >= aHeight)
				break;
			CrossRowData rowData = m_crossData.getRowData(i);
			m_crossData.setNullRowData(i);// 用完的数据可以清除避免占用内存// @edit by sunhld
											// at 2012-7-11,上午10:56:29
			if (rowData == null)
				continue;
			FreePageOutputInfo pageBreaks = (FreePageOutputInfo) output
					.getReportInfo(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK);
			for (int j = 0; j < rowData.getDataLen(); j++) {
				if (j >= aWidth)
					break;
				CrossCell cell = rowData.getCellData(j);
				// 插入分页符
				FreeReportUtil.addPageBreak(m_repModel, area, m_crossData, rowData, pageBreaks, m_flds, cell, i, j,
						isTurn);
				if (cell == null)
					continue;

				// CellPosition pos = area.getStart().getMoveArea(isHor ? j : i,
				// isHor ? i : j);
				CellPosition pos = area.getStart().getMoveArea(isTurn ? j : i, isTurn ? i : j);
				// CellPosition pos = CellPosition.getInstance(isTurn ? j : i,
				// isTurn ? i : j);
				// 判断目标单元格上的数据类型是否与值类型匹配（主要判断字符与数字）
				IFormat aimFormat = cell.getFormat();
				Object value = cell.getValue();
				AnaRepField fld = null;
				fmtPos = cell.getFormatPos();
				if (fmtPos != null) {
					Cell c = getReportModel().getFormatModel().getCell(fmtPos);
					if (c != null) {
						fld = (AnaRepField) c.getExtFmt(AnaRepField.EXKEY_FIELDINFO);
					}
				}
				// 处理行高列宽
				if (!rowhigh.containsKey(pos.getRow()) || !colwidth.containsKey(pos.getColumn())) {
					if (fld != null && (fld.getFieldType() == AnaRepField.Type_CROSS_MEASURE)) {
						int rowsize = getReportModel().getFormatModel().getRowHeaderModel().getSize(fmtPos.getRow());
						rowhigh.put(pos.getRow(), rowsize);
						output.setRowInfo(pos.getRow(), ExtendAreaConstants.ROW_HEIGHT, rowsize);
						int colsize = getReportModel().getFormatModel().getColumnHeaderModel()
								.getSize(fmtPos.getColumn());
						colwidth.put(pos.getColumn(), colsize);
						output.setColInfo(pos.getColumn(), ExtendAreaConstants.COLUMN_WIDTH, colsize);
					}
				}
				// 解决在采集表中，指标为数值型，取得的Format为字符型，导致无法编辑
				// getValueAndDataFormat方法根据对应位置取得单元格数据类型和对应的值
				if (getReportModel() != null) {
					Object[] objs = null;
					if (fld != null) {
						objs = this.getDataFormatAttr(fld, value, null, aimFormat);
						if (objs != null && objs.length > 1) {
							value = objs[0];
							aimFormat = (IFormat) objs[1];
						}
					}
					objs = getReportModel().getValueAndDataFormat(m_exCell, pos, aimFormat, value);

					if (objs != null && objs.length > 1) {
						value = objs[0];
						aimFormat = (IFormat) objs[1];
					}
				}
				BatchCellDatas batchDatas = new BatchCellDatas();
				batchDatas.addData(ExtendAreaConstants.CELL_FORMAT, aimFormat);
				batchDatas.addData(ExtendAreaConstants.CELL_VALUE, value);
				Object condFormat = cell.getCondition();
				if (condFormat != null) {
					batchDatas.addData(ExtendAreaConstants.CELL_CONDITION_FORMAT, ((AreaCondition) condFormat).clone());
				}
				if (cell.getShowName() != null) {
					dataValueRef.put(pos, cell.getShowName());
				}
				else{
					if(null != cell.getValue()){
						dataValueRef.put(pos, cell.getValue());
					}
				}
				
				if (cell.getTraceInfo() != null){
					traceRef.put(pos, cell.getTraceInfo());
				}
				if(fmtPos == null){
					fmtPos = getFmtPos(pos);
				}
				if (fmtPos != null) {
					// @edit by ll at 2012-4-26,下午06:18:04
					// 尽可能记录格式位置，条件格式复制公式中会用到
					cpRef.put(pos, fmtPos);
					recordCellFormulaAttr(output, fmtPos, pos, formulaRef);
					recordCellConditionFormat(output, fmtPos, pos, condFormatRef);
				}
				// @edit by ll at 2012-4-26,下午06:18:54
				// setCellDataBatch放到最后，因为这里面会复制条件格式的公式，之前必须记录单元格位置和单元属性类型等信息
				output.setCellDataBatch(pos, batchDatas.getKeys(), batchDatas.getValues());
				if(fmtPos != null){
					// @edit by yuyangi at 2012-8-14,下午04:32:01
					Cell c = getReportModel().getFormatModel().getCell(fmtPos.getRow(), fmtPos.getColumn());
					if (c != null) {
						AnaRepField sortFld = (AnaRepField) getReportModel().getFormatModel()
								.getCell(fmtPos.getRow(), fmtPos.getColumn()).getExtFmt("AnaRepFieldInfo");// 获取字段
						if (sortFld != null) {
							Cell dataCell = new Cell(cell.getFormat(), cell.getValue());
							FreeReportUtil.addCrossFilterAttribute(m_repModel, m_exCell, sortFld, dataCell, pos, cpRef,
									isTurn , titleRefCache);
							FreeReportUtil.addCrossSortAttribute(m_repModel, m_exCell, m_crossData, sortFld, dataCell, pos,
									cpRef, isTurn , titleRefCache);
						}
					}
				}
			}
		}
		getReportModel().getContext().setAttribute(MemoryOrderTypeConstants.KEY_SORT_POSLIST, sort);
		ArrayList<AreaPosition> combList = m_crossData.getAllCombines();
		ArrayList<AreaPosition> returncombList = new ArrayList<AreaPosition>();
		if (isTurn) {
			for (Iterator<AreaPosition> iterator = combList.iterator(); iterator.hasNext();) {
				AreaPosition araPos = iterator.next();
				// CellPosition start =
				// area.getStart().getMoveArea(araPos.getStart().getColumn(),
				// araPos.getStart().getRow()) ;
				CellPosition start = CellPosition
						.getInstance(araPos.getStart().getColumn(), araPos.getStart().getRow());
				// CellPosition end =
				// area.getStart().getMoveArea(araPos.getEnd().getColumn(),
				// araPos.getEnd().getRow()) ;
				CellPosition end = CellPosition.getInstance(araPos.getEnd().getColumn(), araPos.getEnd().getRow());
				returncombList.add(AreaPosition.getInstance(start, end));
			}
		} else {
			returncombList = combList;
		}

		return returncombList;
	}

	private CellPosition getFmtPos(CellPosition pos) {
		CellPosition fmtPos = m_crossData.getRefPosMap().get(pos);
		if (fmtPos == null) {
			return pos;
		}
		return fmtPos;
	}

	public Object clone() {
		AreaDataModel copy = new AreaDataModel(null);
		copy.setReportModel(m_repModel);
		// ZJB+
		return copy;
	}

	public String getUISettingPaneClassName() {
		return "com.ufida.report.rep.applet.exarea.ExAreaAdvDesignWizardTabPn";
	}

	// 获得交叉表的字段所处表头
	public CrossTableHeader getCrossHeader(String fld) {
		if (!getAreaFields().isCross())
			return null;// TODO 暂时还只能处理依赖于交叉表的表头
		getExtendDesc(m_repModel.getContext());// 确保依赖区域已经计算过扩展
		boolean isRow = false;// 先检查列头
		if (AreaCrossSetInfoUtil.isDimFld(fld, isRow, this.m_repModel, m_exCell.getAreaInfoSet().getCrossSetInfo()))
			return getCrossDataModel().getCrossTabel().getCrossHeader(isRow);
		isRow = true;// 然后检查行头
		if (AreaCrossSetInfoUtil.isDimFld(fld, isRow, this.m_repModel, m_exCell.getAreaInfoSet().getCrossSetInfo())) {
			if (getCrossDataModel().getCrossTabel() != null)
				return getCrossDataModel().getCrossTabel().getCrossHeader(isRow);
		}

		return null;
	}

	// isPreview 是否是预览查询。只有在预览查询之前才将关联字段转换成公式
	public AreaFields getAreaFields(boolean isPreview) {
		if (m_flds == null) {
			this.buildAreaFields(isPreview);
		}
		return m_flds;
	}

	private AreaFields getAreaFields() {
		// m_flds = getAreaFields(false);
		if (m_flds == null) {
			this.buildAreaFields(false);
		}
		return m_flds;

	}

	@SuppressWarnings("rawtypes")
	public static IExtModel getModelExProp(CellsModel model, String key) {
		IExtModel extModel = model.getExtProp(key);
		if (extModel == null) {
			extModel = new ExtDataModel(new Hashtable());
			model.putExtProp(key, extModel);
		}
		return extModel;
	}

	@Override
	public ExDataPaginal getDataPaginal() {
		return m_dataPaginal;
	}

	/**
	 * 规则：没有引用语义模型或引用语义模型但没用引用字段的扩展区不扩展 是否扩展
	 */
	@Override
	public boolean hasExtend() {
		if (BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext) && !isCross())
			return false;

		if (this.getSmartModel() == null) {
			return false;
		}
		if (this.getAreaFields() != null && !this.getAreaFields().hasAnyFld) {
			return false;
		}
		if (this.m_exCell.getBaseInfoSet().isFixed()) {
			return false;
		}
		return true;
	}

	public void outputChart(CellsModel dataModel, AreaPosition exArea) {
		IBIChartModel cm = new ChartManager(dataModel).getChildModel(exArea.getStart());
		if (cm == null) {
			return;
		} else if (cm instanceof ChartModel)
			((ChartModel) cm).setRealLoadData(true);

		// @edit by wangyga at 2009-6-24,上午09:48:16
		// 此处传入自由报表定义的描述器，执行时会加上图表自身定义的描述器
		if (cm instanceof ICombineChartModel) {
			/*
			 * if(!BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext))
			 * ((ChartModel) cm).setRealLoadData(true);
			 */
			Descriptor[] descs = m_descListAggr == null ? new Descriptor[0] : m_descListAggr.toArray(new Descriptor[0]);
			((ICombineChartModel) cm).generateChart(descs);
			return;
		}
		DataSet ds = getDSTool().getDS(FreeQueryParam.DSTYPE_AGGR);
		if (ds == null)
			ds = getDSTool().getDS(FreeQueryParam.DSTYPE_DETAIL);
		if (ds == null)
			ds = new DataSet();
		// 将数据集中的nc数据格式转换下
		convertDataFormat4Chart(ds);
		cm.generateChart(ds);
		/*
		 * if (m_descListAggr != null && m_descListAggr.size() > 0) { DataSet ds
		 * = getDSTool().getDS(FreeQueryParam.DSTYPE_AGGR);
		 * cm.generateChart(ds); } else if (getDSTool() != null) {
		 * if(!BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext))
		 * ((ChartModel) cm).setRealLoadData(true);
		 * cm.generateChart(getDSTool().getDS(FreeQueryParam.DSTYPE_DETAIL)); }
		 * else {
		 * AppDebug.debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString
		 * ("1413006_0",null,"01413006-0912",null,new String[]
		 * {exArea.toString()})@res
		 * "ERROR!!!   图表（{0}）对应的语义模型不存在，无法获取数据，请删除该图表！"); cm.generateChart(new
		 * DataSet()); }
		 */

	}

	private void convertDataFormat4Chart(DataSet ds) {

		for (Field field : ds.getMetaData().getFields()) {
			FieldAttExtModel fieldAttExtModel = (FieldAttExtModel) getReportModel().getFormatModel().getExtProp(
					FieldAttExtModel.KEY_FREE_FIELDATTRIBUTE);
			if (fieldAttExtModel == null) {
				return;
			}
			String smartId = getExAreaCell().getAreaInfoSet().getSmartModelDefID();
			FieldAttribute[] fldAttrs = fieldAttExtModel.getM_fldAtt(smartId);
			FieldAttribute aimFldAttr = null;
			if (fldAttrs != null) {
				for (FieldAttribute fldAttr : fldAttrs) {
					if (fldAttr.getM_expression().equals(field.getExpression())) {
						aimFldAttr = fldAttr;
					}
				}
			}
			if (aimFldAttr != null) {
				for (int i = 0; i < ds.getCount(); i++) {
					Object value = ds.getData(i, field.getFldname());
					try {
						if (FieldAttribute.TYPE_NUMERIC == aimFldAttr.getFldType()) {
							value = getNCFormatterInstance().formatNumber(value)== null? value : getNCFormatterInstance().formatNumber(value).getValue();
						} else if (FieldAttribute.TYPE_ADD == aimFldAttr.getFldType()) {
							// 如果是地址类型，不在此处处理，preAddresFormatData中已经批量处理了
//							try {
//								value = AddressFormater.format(value.toString());
//							} catch (Exception e1) {
//								AppDebug.debug(e1);
//							}
						} else if (value != null && FieldAttribute.TYPE_NCDATE == aimFldAttr.getFldType()) {
								value = getNCFormatterInstance().formatDate(value) == null ? value : getNCFormatterInstance().formatDate(value).getValue();
						} else if (FieldAttribute.TYPE_TIME == aimFldAttr.getFldType()) {
								value = getNCFormatterInstance().formatTime(value) == null ? value : getNCFormatterInstance().formatTime(value)
										.getValue();
						} else if (FieldAttribute.TYPE_DATE == aimFldAttr.getFldType()) {
								value = getNCFormatterInstance().formatDateNotShowTime(value) == null ? value : getNCFormatterInstance()
										.formatDateNotShowTime(value).getValue();
						} else if (FieldAttribute.TYPE_DATETIME == aimFldAttr.getFldType()) {
								value = getNCFormatterInstance().formatDateTime(value) == null ? value : getNCFormatterInstance()
										.formatDateTime(value).getValue();
						}
						ds.updateData(i, field.getFldname(), value);
					} catch (Exception e) {
						AppDebug.debug(e);
					} 
				}
			}
		}

	}

	@Override
	public IAreaOutput outputData(AreaPosition exArea, AreaPosition subArea, CellsModel dataModel) {

		if (!BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext) && (this.getSmartModel() == null)) {
			return null;
		}

		ExtendAreaCell exCell = m_exCell;
		if (exCell == null)
			return null;
		ExDataDescriptor decc = getExtendDesc(m_repModel.getContext());// 顺便确保了“若尚未调用过扩展，则有些数据还没有准备好”
		if (decc == null) {
			return null;
		}
		// boolean isEvent = cellsModel.isEnableEvent();
		// cellsModel.setEnableEvent(false);

		boolean isHor = (exCell.getBaseInfoSet().getExMode() == ExtendAreaConstants.EX_MODE_X);// 横向扩展

		this.getDSTool().setConvert(this.getAreaFields().getFieldConverter());
		this.getDSTool().setExCell(this.getExAreaCell());
		Boolean isStaticData = (Boolean) this.m_repModel.getContext().getAttribute(
				FreePrivateContextKey.KYE_SUBSCIBE_STATIC_DATA);
		if (isStaticData != null) {
			this.getDSTool().setCreateTraceInfo(isStaticData);
		}

		IAreaOutput output = initNewExtendAreaOutput(dataModel);
		if (!BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext)) {
			output.setFormulaHandler(this.getDataFormulaHandler(m_repModel.getDataModel()));
		}

		AreaPosition realArea = exArea;

		// @edit by yuyangi at 2012-6-5,上午09:35:46 分栏设置时，要重新计算填充区域大小
		Integer sepCol = (Integer) m_exCell.getAreaInfoSet()
				.getAreaLevelInfo(FreePrivateContextKey.KEY_SEPARATE_COLUMN);
		if (sepCol != null) {
			realArea = AreaPosition.getInstance(realArea.getStart().getRow(), realArea.getStart().getColumn(),
					realArea.getWidth(), realArea.getHeigth() * sepCol.intValue());
			exArea = realArea;
			subArea = realArea;
		}
		// 实际填充区域
		output.setArea(realArea);
		ArrayList<AreaPosition> combList = null;
		if (isCross()) {// 交叉表的扩展数据已经算好了，直接填充
			if (getAreaFields().hasAnyFld)
				combList = doCrossFillData(output, realArea, isHor);
			if (m_crossData != null && m_crossData.getRefPosMap() != null && !m_crossData.getRefPosMap().isEmpty()) {// 调整交叉表
																														// 分组行高
				for (Iterator<CellPosition> iterator = m_crossData.getRefPosMap().keySet().iterator(); iterator
						.hasNext();) {
					CellPosition cellPosition = (CellPosition) iterator.next();
					int size = getReportModel().getFormatModel().getRowHeaderModel()
							.getSize(m_crossData.getRefPosMap().get(cellPosition).getRow());
					getReportModel().getDataModel().getRowHeaderModel().setSize(cellPosition.getRow(), size);
				}
			}
			// this.clearData() ;
		} else {// 列表
			if (BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext)) {// 节点刚打开时，直接显示格式不填充数据
				combList = doFormatFillData(output, realArea, isHor);
			} else {
				m_combProc = new AreaCombineProcessor(m_exCell);
				if (subArea.getWidth() >= decc.getWidth() && subArea.getHeigth() >= decc.getHeight()) {// 不是子区域，就不用考虑分页了
					/*
					 * if (getAreaFields().bPaging) {// 但是此时要执行明细数据的加载 if
					 * (m_repModel.shouldReloadData()) { FreeQueryParam pa = new
					 * FreeQueryParam(getSmartModel().getId(), getSmartModel(),
					 * m_areaContext, m_descList.toArray(new Descriptor[0]));
					 * pa.setType(FreeQueryParam.DSTYPE_DETAIL);
					 * pa.setAreaCondition(m_areaCondition); DataSet[] dsArray =
					 * FreeReportQueryExcutor.execQueryByPageParam
					 * (m_repModel.getSmartUtil(), new FreeQueryParam[] { pa });
					 * if (dsArray == null) { AppDebug.error("语义模型执行错误："); (new
					 * Exception()).printStackTrace(); } else { DataSet dataSet
					 * = dsArray[0]; getDSTool().setDS(new
					 * FreeQueryResult(dataSet, FreeQueryParam.DSTYPE_DETAIL));
					 * getAreaFields().bPaging = false; } } }
					 */
				}
				int[] range = getRealAreaRange(exArea, realArea, isHor);
				PageParameter pageDesc = null;
				// SmartModel model = getSmartModel();
				if (getAreaFields().bPaging) {// 可能需要处理分页
					realArea = subArea;
					// 实际填充区域
					output.setArea(realArea);
					range = getRealAreaRange(exArea, realArea, isHor);

					// // 进行需要加载的明细数据的收集
					pageDesc = AreaRowLevelUtil.calcPageDesc(range, m_rowLvls, getDSTool(), getReportModel());

					// 先设置当前的page信息，以确保能够计算数据的相对行数; 进行数据填充
					pageDesc.setIsFeeler(false);
					getDSTool().setPageParameter(pageDesc);
					boolean unloadData = BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext);
					if (!unloadData && m_repModel.shouldReloadData() && !getDSTool().hasAllDetailData()) {
						LimitRowDescriptor limitDesc = getDSTool().getSubDataLimitDesc(getPolicy().getPageDataRow());
						if (limitDesc != null && m_descList != null) {// 需要重新加载其他分页的数据

							int dSize = m_descList.size() + 1;
							Descriptor[] dds = new Descriptor[dSize];
							for (int i = 0; i < dSize - 1; i++) {
								dds[i] = m_descList.get(i);
							}
							dds[dSize - 1] = limitDesc;

							getPolicy().setDataType(FreePolicy.EXEC_DATA_SUBDATA);
							FreeQueryParam pa = createQueryParam(FreeQueryParam.DSTYPE_DETAIL, dds);
							getPolicy()
									.setclearTempType(com.ufida.report.anareport.exec.IFreeExecTypes.CLEARTEMP_REUSE);
							PerfWatch pw = new PerfWatch(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
									"1413006_0", null, "01413006-0914", null,
									new String[] { String.valueOf(limitDesc) })/*
																				 * @
																				 * res
																				 * "后台执行分页数据加载["
																				 */);
							try {
								DataSet[] dsArray = FreeReportQueryExcutor.execQueryByPaginalParam(
										m_repModel.getSmartUtil(), new FreeQueryParam[] { pa });
								if (dsArray == null) {
									AppDebug.error(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",
											"01413006-0915")/* @res "语义模型执行错误：" */);
									// (new Exception()).printStackTrace();
								} else {
									DataSet dataSet = dsArray[0];
									FreeQueryResult dsReault = new FreeQueryResult(dataSet,
											FreeQueryParam.DSTYPE_DETAIL);
									dsReault.setDSState(IFreeDSType.DATASET_STATE_PAGE);
									getDSTool().setDS(dsReault);
								}
							} catch (RuntimeException e) {
								pw.appendMessage(e);
								throw e;
							} finally {
								pw.stop();
							}
						}
					}
				}

				if (m_rowLvls != null) {
					fillDataByRowLvl(output, exArea, realArea, null, isHor, 0, 0, m_rowLvls.getRowCount() - 1,
							new ArrayList<AbsRowData>(), -2, 0, range, false);
				}
				// 处理单元的自动合并
				combList = m_combProc.processAutoCombine(output, realArea, isHor, m_exCell, m_exCell.getArea());
			}
		}
		ArrayList<AreaPosition> realCombList = new ArrayList<AreaPosition>();
		if (combList != null) {
			// 将所有合并信息设置到表格模型上
			AreaCombineProcessor.doMergeCombines(combList);
			for (AreaPosition c : combList) {
				AreaPosition aim = (!isCross()) ? c : (AreaPosition) c.getMoveArea(exArea.getStart().getRow(), exArea
						.getStart().getColumn());
				if (exArea.contain(aim)) {
					realCombList.add(aim);
				} else {
					AppDebug.debug("out of area" + aim);
				}
			}
		}
		output.setReportInfo(ExtendAreaConstants.COMBINED_AREA_LIST, realCombList);

		// @edit by yuyangi at 2012-6-5,上午09:37:46 增加分栏处理
		separateColFill(output, dataModel);

		// @edit by ll at 2011-7-27,上午09:53:48 将扩展中增加的分页符记录到格式模型中
		FreePageOutputInfo pageBreaks = (FreePageOutputInfo) output
				.getReportInfo(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK);
		if (pageBreaks.hasPageBreak()) {
			ExtendAreaCell exCellFormat = m_repModel.getExtendAreaModel().getExAreaByPK(getAreaPK());
			exCellFormat.getAreaInfoSet().addAreaLevelInfo(FreePageOutputInfo.KEY_FREEPAGE_INFO, pageBreaks);

		}
		// 还原字段设置
		ArrayList<AnaRepField> al_flds = m_flds.getAllAnaFields();
		for (int i = 0; i < al_flds.size(); i++) {
			if (al_flds.get(i) != null) {
				al_flds.get(i).setFilter(false);
			}
		}
		// 还原排序设置
		GridSortUtil.setHasOver(exCell);

		return output;
	}
	
	/**
	 * 预处理数据格式为“地址”类型的数据
	 */
	private void preAddresFormatData() {
		FieldAttExtModel fieldAttExtModel = (FieldAttExtModel) getReportModel().getFormatModel().getExtProp(
				FieldAttExtModel.KEY_FREE_FIELDATTRIBUTE);
		if(fieldAttExtModel == null)
			return;
		String smartId = getExAreaCell().getAreaInfoSet().getSmartModelDefID();
		FieldAttribute[] fldAttrs = fieldAttExtModel.getM_fldAtt(smartId);
		if(fldAttrs == null || fldAttrs.length ==0)
			return ;
		// 获取所有设置了地址型数据格式的字段名
		List<String> addFieldNames = new ArrayList<String>();
		// 获得别名转换器
		FreeFieldConverter converter = m_flds == null ? null : m_flds.getFieldConverter();
		if (converter == null) {
			converter = (FreeFieldConverter) m_exCell.getAreaInfoSet().getAreaLevelInfo(
					FreeFieldConverter.class.getName());
			if(converter == null){
				converter = new FreeFieldConverter(getSmartModel());
				m_exCell.getAreaInfoSet().addAreaLevelInfo(FreeFieldConverter.class.getName(), converter);
			}
		}
		for(FieldAttribute fldAttr : fldAttrs){
			if(fldAttr == null)
				continue;
			// 如果是地址类型，则记录
			if(FieldAttribute.TYPE_ADD == fldAttr.getFldType()){
				addFieldNames.add(converter.getConvertName(fldAttr.getM_expression()));
			}
		}
		// 如果不存在地址类型的字段，则直接返回
		if(addFieldNames.size() <= 0)
			return;
		// 获取明细结果集，或者汇总结果集
		DataSet ds = getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL_ALL);
		if(ds == null){
			ds = getDSTool().getDS(IFreeDSType.DSTYPE_AGGR);
		}
		if(ds == null){
			return;
		}
		MetaData metaData = ds.getMetaData();
		if(metaData == null){
			return;
		}
		String[] fieldNames = metaData.getFieldNames();
		if(fieldNames == null || fieldNames.length == 0){
			return;
		}
		// 结果集中包含的地址类型字段名
		List<String> addrFieldNamesInDs = new ArrayList<String>();
		for (String string : fieldNames) {
			if(addFieldNames.contains(string)){
				addrFieldNamesInDs.add(string);
			}
		}
		if(addrFieldNamesInDs == null || addrFieldNamesInDs.size() <= 0){
			return;
		}
		// 查询结果集中地址类型字段所对应的值
		String[] fieldArray = addrFieldNamesInDs.toArray(new String[addrFieldNamesInDs.size()]);
		Object[][] data2Array = ds.getData2Array(fieldArray);
		if(data2Array == null || data2Array.length == 0)
			return;
		int col = data2Array[0].length;
		// 将二维数组转换为一维数组，便于一次远程调用
		String[] datas = BQArrayUtil.converArray2OneMeas(data2Array);
		if(datas == null)
			return;
		// 将数据转换成正确的地址格式
		String[] formatDatas = AddressFormater.format(datas);
		Object[][] aimData = BQArrayUtil.converArray2DoubleMeas(formatDatas, col);
		if(aimData == null)
			return;
		// 替换结果集中的数据
		Object[][] allOldDatas = ds.getDatas();
		if(allOldDatas == null || allOldDatas.length == 0)
			return;
		int oldLen = allOldDatas.length;
		for (int s = 0; s < addrFieldNamesInDs.size(); s++){
			String fieldName = addrFieldNamesInDs.get(s);
			int index = ds.getMetaData().getIndex(fieldName);
			if(index < 0){
				continue;
			}
			for(int i = 0 ; i < oldLen ; i++){
				allOldDatas[i][index] = aimData[i][s];
			}
		}
	}


	/**
	 * 分栏数据处理
	 * 
	 * @param output
	 * @param dataModel
	 */
	private void separateColFill(IAreaOutput output, CellsModel dataModel) {
		Integer sepCol = (Integer) m_exCell.getAreaInfoSet()
				.getAreaLevelInfo(FreePrivateContextKey.KEY_SEPARATE_COLUMN);
		if (sepCol == null) {
			return;
		}
		AreaPosition titleArea = (AreaPosition) m_exCell.getAreaInfoSet().getAreaLevelInfo(
				FreePrivateContextKey.KEY_UNMOVE_AREA);

		CellsModel srcModel = ((SeparateColOutput) output).getCellsModel();

		setReportInfo(srcModel, dataModel);

		Map<String, AreaPosition> combMap = new HashMap<String, AreaPosition>();

		AreaPosition exArea = m_exCell.getArea();
		CellPosition starttemp = exArea.getStart();
		CellPosition rowTemp = null;
		CellPosition colTemp = null;
		CellPosition srcTemp = starttemp;
		CellPosition srcRowTemp = starttemp;
		if (sepCol != null && sepCol.intValue() >= 2) {
			int height = m_desc.getHeight();
			int width = exArea.getWidth() / sepCol;
			if (m_exCell.getAreaInfoSet().isCrossSet()) {
				width = m_desc.getWidth();
			}
			for (int i = 0; i < sepCol.intValue(); i++) {
				rowTemp = starttemp;
				colTemp = starttemp;
				for (int j = 0; j < height; j++) {
					boolean isNext = true;
					for (int k = 0; k < width; k++) {
						colTemp = rowTemp.getMoveArea(0, k);
						Cell cell = srcModel.getCell(colTemp);
						if (i > 0 && titleArea != null && titleArea.contain(colTemp)) {
							fillCellInfo(dataModel, cell, colTemp);
							dataModel.setCellValue(colTemp, cell.getValue());
							isNext = false;
						} else {
							srcTemp = srcRowTemp.getMoveArea(0, k);
							fillCellInfo(dataModel, cell, colTemp);
							dataModel.setCellValue(colTemp, srcModel.getCellValue(srcTemp));

							AreaPosition combArea = srcModel.getCombinedCellArea(srcTemp);
							if (combArea != null && combArea.isCell() && !combArea.getStart().equals(srcTemp)) {
								String key = combArea.toString() + i;
								AreaPosition tempCombArea = combMap.get(key);
								if (tempCombArea == null) {
									combMap.put(key, AreaPosition.getInstance(colTemp, colTemp));
								} else {
									CellPosition endPos = tempCombArea.getEnd();
									CellPosition startPos = tempCombArea.getStart();
									int rowMove = colTemp.getRow() - endPos.getRow();
									int colMove = colTemp.getColumn() - endPos.getColumn();
									endPos = endPos.getMoveArea(rowMove > 0 ? rowMove : 0, colMove > 0 ? colMove : 0);
									tempCombArea = AreaPosition.getInstance(startPos, endPos);
									combMap.put(key, tempCombArea);
								}
							}
						}
					}
					if (isNext) {
						srcRowTemp = srcRowTemp.getMoveArea(1, 0);
						srcTemp = srcRowTemp;
					}
					rowTemp = rowTemp.getMoveArea(1, 0);
				}
				starttemp = starttemp.getMoveArea(0, exArea.getWidth() / sepCol);
			}

			doExareaComb(dataModel);
			separateCombCells(dataModel, combMap);
		}
	}

	/**
	 * 处理扩展区合并单（标题部分）
	 * 
	 * @param dataModel
	 */
	private void doExareaComb(CellsModel dataModel) {
		List<CombinedCell> combList = m_exCell.getAreaInfoSet().getAllCombinedCell();
		if (combList != null) {
			for (CombinedCell combCell : combList) {
				dataModel.combineCell(combCell.getArea());
			}
		}
	}

	/**
	 * 分割合并单元格
	 * 
	 * @param dataModel
	 * @param combMap
	 */
	private void separateCombCells(CellsModel dataModel, Map<String, AreaPosition> combMap) {
		if (combMap.size() > 0) {
			for (String key : combMap.keySet()) {
				AreaPosition area = combMap.get(key);
				if (area != null) {
					AreaPosition combCell = dataModel.getCombinedCellArea(area.getStart());
					if (combCell != null) {
						dataModel.getCombinedAreaModel().removeCombinedCell(combCell);
					}
					dataModel.combineCell(area);
				}
			}
		}
	}

	/**
	 * 设置单元信息。
	 * 
	 * @param dataModel
	 * @param cell
	 * @param pos
	 */
	private void fillCellInfo(CellsModel dataModel, Cell cell, CellPosition pos) {
		if (cell == null)
			return;

		Object value = cell.getExtFmt(ExtendAreaConstants.CELL_VALUE);
		IFormat format = (IFormat) cell.getExtFmt(ExtendAreaConstants.CELL_FORMAT);
		if (format == null) {
			format = cell.getFormat();
		}
		Cell aimCell = new Cell();
		aimCell.setFormat(format);
		aimCell.setValue(value);
		Object fieldInfo = cell.getExtFmt(ExtendAreaConstants.FIELD_INFO);
		if (fieldInfo != null)
			aimCell.setExtFmt(ExtendAreaConstants.FIELD_INFO, fieldInfo);
		Object rowNum = cell.getExtFmt(ExtendAreaConstants.DATASET_ROW_NO);
		if (rowNum != null)
			aimCell.setExtFmt(ExtendAreaConstants.DATASET_ROW_NO, rowNum);
		Object autoComb = cell.getExtFmt(AnaCellCombine.KEY);
		if (autoComb != null) {
			aimCell.setExtFmt(AnaCellCombine.KEY, autoComb);
		}
		TableUtilities.setCell(dataModel, pos.getRow(), pos.getColumn(), aimCell);
	}

	/**
	 * 设置扩展属性信息
	 * 
	 * @param fromCells
	 * @param toCells
	 */
	public void setReportInfo(CellsModel fromCells, CellsModel toCells) {
		if (fromCells.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP) != null)
			toCells.putExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP,
					fromCells.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP));
		if (fromCells.getExtProp(ExtendAreaConstants.TRACEDATA_REFER_MAP) != null)
			toCells.putExtProp(ExtendAreaConstants.TRACEDATA_REFER_MAP,
					fromCells.getExtProp(ExtendAreaConstants.TRACEDATA_REFER_MAP));
		if (fromCells.getExtProp(ExtendAreaConstants.DATAVALUE_REFER_MAP) != null)
			toCells.putExtProp(ExtendAreaConstants.DATAVALUE_REFER_MAP,
					fromCells.getExtProp(ExtendAreaConstants.DATAVALUE_REFER_MAP));
		if (fromCells.getExtProp(ExtendAreaConstants.FORMULA_REFER_MAP) != null)
			toCells.putExtProp(ExtendAreaConstants.FORMULA_REFER_MAP,
					fromCells.getExtProp(ExtendAreaConstants.FORMULA_REFER_MAP));
		if (fromCells.getExtProp(ExtendAreaConstants.AREA_INFO_MAP) != null)
			toCells.putExtProp(ExtendAreaConstants.AREA_INFO_MAP,
					fromCells.getExtProp(ExtendAreaConstants.AREA_INFO_MAP));
		if (fromCells.getExtProp(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK) != null)
			toCells.putExtProp(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK,
					fromCells.getExtProp(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK));
	}

	private int getIntegerHeight(int height, int sep) {
		AreaPosition titleArea = (AreaPosition) m_exCell.getAreaInfoSet().getAreaLevelInfo(
				FreePrivateContextKey.KEY_UNMOVE_AREA);
		if (titleArea != null) {
			height = height + titleArea.getHeigth();
		}
		double hd = (double) height / sep;
		int hi = height / sep;
		if (hd > hi) {
			return hi + 1;
		} else {
			return hi;
		}
	}

	private ArrayList<AreaPosition> doFormatFillData(IAreaOutput cells, AreaPosition area, boolean isHor) {
		ArrayList<AreaPosition> combList = new ArrayList<AreaPosition>();

		AreaPosition fillArea = area.interArea(m_exCell.getArea());
		if (fillArea == null)
			return combList;
		CellPosition start = fillArea.getStart();
		CellPosition end = fillArea.getEnd();
		for (int row = start.getRow(); row <= end.getRow(); row++) {
			CellPosition lastAimPos = null;
			for (int col = start.getColumn(); col <= end.getColumn(); col++) {
				CellPosition pos = CellPosition.getInstance(row, col);
				CombinedCell combCell = m_exCell.getAreaInfoSet().getCombinedCellByPos(pos);
				if (combCell != null) {
					if (combCell.getArea().getStart().equals(pos)) {
						combList.add(combCell.getArea());
					} else {
						continue;
					}
				}
				lastAimPos = pos;
				Map<String, Object> srcExMap = m_exCell.getCellInfoSet().getExtInfo(pos);
				if (srcExMap == null || srcExMap.size() <= 0) {// 格式单元无信息
					continue;
				}
				AnaRepField fld = (AnaRepField) srcExMap.get(AnaRepField.EXKEY_FIELDINFO);
				Object realValue = srcExMap.get(ExtendAreaConstants.CELL_VALUE);
				IFormat cellFormat = (IFormat) srcExMap.get(ExtendAreaConstants.CELL_FORMAT);// 为了控制内存占用，直接使用格式
				if (fld != null && !fld.getDimInfo().isExtended()) {
					realValue = null;
				}
				// @edit by ll at 2011-9-2,上午10:04:56 效率优化，单元属性改成批量设置
				BatchCellDatas batchDatas = new BatchCellDatas();
				batchDatas.addData(ExtendAreaConstants.CELL_FORMAT, cellFormat);
				batchDatas.addData(ExtendAreaConstants.CELL_VALUE, realValue);
				cells.setCellDataBatch(pos, batchDatas.getKeys(), batchDatas.getValues());
			}
			// 设置行高/列宽
			if (lastAimPos != null) {
				if (isHor) {
					if (row < m_exCell.getAreaColsWidth().length) {
						int rowHeight = m_exCell.getAreaColsWidth()[row];
						cells.setColInfo(lastAimPos.getColumn(), ExtendAreaConstants.COLUMN_WIDTH, rowHeight);
					}
				} else {
					if (row < m_exCell.getAreaRowsHeight().length) {
						int rowHeight = m_exCell.getAreaRowsHeight()[row - start.getRow()];
						cells.setRowInfo(lastAimPos.getRow(), ExtendAreaConstants.ROW_HEIGHT, rowHeight);
					}
				}
			}
		}
		return combList;
		// 返回合并单元
	}

	private BIAreaFormulaHandler getDataFormulaHandler(CellsModel cells) {
		BIAreaFormulaHandler handler = new BIAreaFormulaHandler(cells);
		handler.setReportModel(this.m_repModel);
		handler.registerFuncDriver(new AreaFuncDriver(this.m_repModel));
		handler.registerFuncDriver(new CVSBatchFuncDriver(this.m_repModel));
		handler.registerFuncDriver(new GetFuncDriver(this.m_repModel));
		// add by yuyangi 注册参数公式驱动
		handler.registerFuncDriver(new VarFuncDriver(this.m_repModel));
		// // @edit by yuyangi at 2012-6-25,下午04:27:52 nc数据格式驱动
		handler.registerFuncDriver(new NcDataFormatFuncDriver(this.m_repModel));
		//添加薪资解密函数  add by ward 2018-06-05
		handler.registerFuncDriver(new SalaryDecryptFuncDriver(this.m_repModel));
		handler.registerFuncDriver(new GetAreaFieldFuncDriver(this.m_repModel));

		ExcelStatCalcUtil.registExcelFuncDriver(handler.getCalcEnv());

		return handler;

	}

	public BIAreaFormulaHandler getFormatFormulaHandler() {
		if (m_format_formulaHandler == null) {
			m_format_formulaHandler = new BIAreaFormulaHandler(this.m_repModel.getFormatModel());
			m_format_formulaHandler.setReportModel(this.m_repModel);
			m_format_formulaHandler.registerFuncDriver(new AreaFuncDriver(this.m_repModel));
			m_format_formulaHandler.registerFuncDriver(new CVSBatchFuncDriver(this.m_repModel));
			m_format_formulaHandler.registerFuncDriver(new GetFuncDriver(this.m_repModel));
			// add by yuyangi 注册参数公式驱动
			m_format_formulaHandler.registerFuncDriver(new VarFuncDriver(this.m_repModel));
			// nc数据格式驱动
			m_format_formulaHandler.registerFuncDriver(new NcDataFormatFuncDriver(this.m_repModel));
			//添加薪资解密函数  add by ward 2018-06-05
			m_format_formulaHandler.registerFuncDriver(new SalaryDecryptFuncDriver(this.m_repModel));
			m_format_formulaHandler.registerFuncDriver(new GetAreaFieldFuncDriver(this.m_repModel));
			ExcelStatCalcUtil.registExcelFuncDriver(m_format_formulaHandler.getCalcEnv());
		}
		return m_format_formulaHandler;
	}

	private IAreaOutput initNewExtendAreaOutput(CellsModel dataModel) {

		Integer sepCol = (Integer) m_exCell.getAreaInfoSet()
				.getAreaLevelInfo(FreePrivateContextKey.KEY_SEPARATE_COLUMN);
		if (sepCol != null && sepCol.intValue() >= 2) {
			SeparateColOutput output = new SeparateColOutput();
			CellsModel cellsTemp = output.getCellsModel();
			if (cellsTemp.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP) == null)
				output.setReportInfo(ExtendAreaConstants.CELLPOS_REFER_MAP, new Hashtable<CellPosition, CellPosition>());
			if (cellsTemp.getExtProp(ExtendAreaConstants.TRACEDATA_REFER_MAP) == null)
				output.setReportInfo(ExtendAreaConstants.TRACEDATA_REFER_MAP,
						new Hashtable<CellPosition, TraceDataParam>());
			if (cellsTemp.getExtProp(ExtendAreaConstants.DATAVALUE_REFER_MAP) == null)
				output.setReportInfo(ExtendAreaConstants.DATAVALUE_REFER_MAP, new Hashtable<CellPosition, Object>());
			if (cellsTemp.getExtProp(ExtendAreaConstants.FORMULA_REFER_MAP) == null)
				output.setReportInfo(ExtendAreaConstants.FORMULA_REFER_MAP, new Hashtable<IArea, FormulaVO>());
			if (cellsTemp.getExtProp(ExtendAreaConstants.CONDITIONFORMAT_REFER_MAP) == null)
				output.setReportInfo(ExtendAreaConstants.CONDITIONFORMAT_REFER_MAP,
						new Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition>());
			if (cellsTemp.getExtProp(ExtendAreaConstants.AREA_INFO_MAP) == null)
				output.setReportInfo(ExtendAreaConstants.AREA_INFO_MAP, new Hashtable<String, AreaPrivateMap>());
			if (cellsTemp.getExtProp(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK) == null)
				output.setReportInfo(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK, new FreePageOutputInfo());
			return output;
		} else {

			if (dataModel != null) {
				CellsAreaOutput output = new CellsAreaOutput(dataModel);
				if (dataModel.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP) == null)
					output.setReportInfo(ExtendAreaConstants.CELLPOS_REFER_MAP,
							new Hashtable<CellPosition, CellPosition>());
				if (dataModel.getExtProp(ExtendAreaConstants.TRACEDATA_REFER_MAP) == null)
					output.setReportInfo(ExtendAreaConstants.TRACEDATA_REFER_MAP,
							new Hashtable<CellPosition, TraceDataParam>());
				if (dataModel.getExtProp(ExtendAreaConstants.DATAVALUE_REFER_MAP) == null)
					output.setReportInfo(ExtendAreaConstants.DATAVALUE_REFER_MAP, new Hashtable<CellPosition, Object>());
				if (dataModel.getExtProp(ExtendAreaConstants.FORMULA_REFER_MAP) == null)
					output.setReportInfo(ExtendAreaConstants.FORMULA_REFER_MAP, new Hashtable<IArea, FormulaVO>());
				if (dataModel.getExtProp(ExtendAreaConstants.CONDITIONFORMAT_REFER_MAP) == null)
					output.setReportInfo(ExtendAreaConstants.CONDITIONFORMAT_REFER_MAP,
							new Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition>());
				if (dataModel.getExtProp(ExtendAreaConstants.AREA_INFO_MAP) == null)
					output.setReportInfo(ExtendAreaConstants.AREA_INFO_MAP, new Hashtable<String, AreaPrivateMap>());
				if (dataModel.getExtProp(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK) == null)
					output.setReportInfo(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK, new FreePageOutputInfo());
				return output;
			} else {
				IAreaOutput output = new ExtendAreaOutput();
				output.setReportInfo(ExtendAreaConstants.CELLPOS_REFER_MAP, new Hashtable<CellPosition, CellPosition>());
				output.setReportInfo(ExtendAreaConstants.TRACEDATA_REFER_MAP,
						new Hashtable<CellPosition, TraceDataParam>());
				output.setReportInfo(ExtendAreaConstants.DATAVALUE_REFER_MAP, new Hashtable<CellPosition, Object>());
				output.setReportInfo(ExtendAreaConstants.FORMULA_REFER_MAP, new Hashtable<IArea, FormulaVO>());
				output.setReportInfo(ExtendAreaConstants.CONDITIONFORMAT_REFER_MAP,
						new Hashtable<IArea, com.ufsoft.table.format.condition.IAreaCondition>());
				output.setReportInfo(ExtendAreaConstants.AREA_INFO_MAP, new Hashtable<String, AreaPrivateMap>());
				output.setReportInfo(ExtendAreaConstants.REPORT_PRINT_PAGEBREAK, new FreePageOutputInfo());

				// output.setReportInfo(ExtendAreaConstants.FORMULA_DATAINDEX_MAP,
				// new
				// Hashtable<Integer, GroupDataIndex>());
				// output.setReportInfo(ExtendAreaConstants.FORMULA_DATAINDEX_FIELDPOS_MAP,
				// new Hashtable<GroupDataIndex, Hashtable<String,
				// CellPosition>>());
				return output;
			}
		}

	}

	@Override
	public void setDataPaginal(ExDataPaginal dataPaginal) {
		m_dataPaginal = dataPaginal;

	}

	@Override
	public void setExtendAreaCell(ExtendAreaCell areaCell) {
		m_exCell = areaCell;
	}

	public boolean isCross() {
		return m_exCell.getAreaInfoSet().getCrossSetInfo() != null;
	}

	public String getAreaPK() {
		return m_exCell.getBaseInfoSet().getExAreaPK();
	}

	public String getDSPK() {
		return m_exCell.getAreaInfoSet().getSmartModelDefID();
	}

	public String getSmartDefName() {
		return m_repModel.getSmartUtil().getSmartDefVO(getDSPK()).toString();
	}

	public SmartModel getSmartModel() {
		SmartModel smartModel = m_exCell.getAreaInfoSet().getSmartModel();
		if (smartModel == null) {
			smartModel = m_repModel.getSmartModel(m_exCell.getAreaInfoSet().getSmartModelDefID());
			m_exCell.getAreaInfoSet().setSmartModel(smartModel);
		}
		return smartModel;
	}

	/**
	 * 获得区域的每行的分级状态，采集表再需要分组相关信息时调用
	 * 
	 * @create by wanyonga at 2010-5-28,下午03:42:51
	 * 
	 * @return
	 */
	public AreaRowLevel getAreaRowLevel() {
		if (this.m_rowLvls == null) {
			this.m_rowLvls = AreaRowLevelUtil.getRowLevel(this, m_exCell);
		}
		return this.m_rowLvls;
	}

	private FreeQueryParam[] getCrossQueryParam(IContext context) {

		getDSTool().setPageParameter(null);
		// 1.1 创建汇总数据的描述器
		CrossTableSet cross = AreaCrossSetInfoUtil.getCrossSet(m_repModel, m_exCell.getAreaInfoSet().getCrossSetInfo());
		AggrDescriptor aggrDesc = AreaDescUtil.getAggrDesc(getExAreaCell(), getAreaFields(), true, cross);
		this.AddGroupFieldFromPageDimItem(aggrDesc);
		m_descListAggr.add(aggrDesc);

		ArrayList<Descriptor> list = m_descListAggr;
		// 1.2 创建排序和筛选条件的描述器
		SortDescriptor sortDes = AreaDescUtil.getSortDescriptor(m_flds, m_exCell, m_repModel);
		if (sortDes != null)
			list.add(sortDes);

		SmartModel smart = getSmartModel();

		FilterDescriptor filterDes = new FilterDescriptor();
		FilterDescriptor[] filters = addAreaFilterToDesc(filterDes, smart, context);
		for (FilterDescriptor filter : filters) {
			if (filter.getFilterItemCount() > 0) {
				list.add(filter);
			}
		}
		FilterDescriptor pageDesc = null;
		if (getPolicy().isUsePageDim()) {
			pageDesc = getPageDimDescriptor();// 页纬度也算表级过滤条件
		}
		if (pageDesc != null)
			list.add(pageDesc);

		Descriptor[] policyDescs = getPolicyDescArray();// 内存描述器只能后台执行
		if (policyDescs != null)
			list.addAll(Arrays.asList(policyDescs));

		// 2.1 创建列字段描述器
		Field[] cols = AreaCrossSetInfoUtil.getCrossFlds(AnaRepField.Type_CROSS_COLUMN, m_repModel, m_exCell
				.getAreaInfoSet().getCrossSetInfo());

		ArrayList<Field> convCols = new ArrayList<Field>();
		if (cols != null) {
			for (int i = 0; i < cols.length; i++) {
				convCols.add(m_flds.getFieldConverter().getField(cols[i].getExpression()));
			}
		}

		SelectColumnDescriptor lcDesc = new SelectColumnDescriptor();
		lcDesc.setFields(convCols.toArray(new Field[0]));
		list.add(lcDesc);

		// 1.4 数据集open
		FreeQueryParam pa = createQueryParam(FreeQueryParam.DSTYPE_AGGR, list.toArray(new Descriptor[0]));

		// boolean hasGroupField = getAreaFields().getGroupList().size() == 0;//
		// 交叉表是否有外围分组
		// if (hasGroupField || !AreaCrossSetInfoUtil.isCrossTwoHeader(cross))
		// {// 交叉数据集一次执行
		return new FreeQueryParam[] { pa };
		// } else {// 对于双方向表头的交叉表，并且没有外围分组时，可进行列表头和交叉数据集的批量执行
		// FreeQueryParam[] dsExec = new FreeQueryParam[2];
		// dsExec[0] = pa;
		//
		// // 2.1 创建列字段描述器
		// ArrayList<Descriptor> list2 = new ArrayList<Descriptor>();
		// Field[] cols =
		// AreaCrossSetInfoUtil.getCrossFlds(AnaRepField.Type_CROSS_COLUMN,
		// m_repModel, m_exCell
		// .getAreaInfoSet().getCrossSetInfo());
		//
		// Field[] convCols = new Field[cols.length];
		// for (int i = 0; i < cols.length; i++) {
		// convCols[i] =
		// m_flds.getFieldConverter().getField(cols[i].getExpression());
		// }
		// SelectColumnDescriptor lcDesc = new SelectColumnDescriptor();
		// lcDesc.setFields(convCols);
		// list2.add(lcDesc);
		//
		// // 2.2 创建排序和筛选条件的描述器
		// SortDescriptor sortDes2 = new SortDescriptor();
		// addColumnSortToDesc(sortDes2, convCols);
		// if (sortDes2.getSortNum() > 0)
		// list2.add(sortDes2);
		//
		// FilterDescriptor filterDes2 = new FilterDescriptor();
		// filters = addAreaFilterToDesc(filterDes2, smart, context);
		// for (FilterDescriptor filter : filters) {
		// if (filter.getFilterItemCount() > 0) {
		// list2.add(filter);
		// }
		// }
		// if (pageDesc != null)
		// list2.add(pageDesc);
		//
		// list2.add(new DistinctDescriptor());
		//
		// // 内存描述器只能后台执行
		// if (policyDescs != null)
		// list2.addAll(Arrays.asList(policyDescs));
		//
		// // 2.3返回查询参数
		// FreeQueryParam pa2 =
		// createQueryParam(FreeQueryParam.DSTYPE_AGGR_COLHEAD,
		// list2.toArray(new Descriptor[0]));
		// dsExec[1] = pa2;
		// return dsExec;
		// }
	}

	private void AddGroupFieldFromPageDimItem(AggrDescriptor aggrDesc) {
		if (aggrDesc == null) {
			return;
		}
		// 处理页维度,在汇总描述器上增加分组（页维度分组）
		ArrayList<Field> convCols = new ArrayList<Field>();
		String pageAreaPk = getReportModel().getAreaPKByReportCondition();
		if (pageAreaPk != null && pageAreaPk.equals(getAreaPK())) {
			ReportCondition repCondition = ReportCondition.getInstance(getReportModel().getExtendAreaModel());
			if (repCondition != null) {
				if (repCondition.isExecutedDirectly()) {
					return;
				}
				repCondition.setFieldConverter(m_flds.getFieldConverter());
				String[] pageConds = repCondition.getAllFieldExpressions();

				if (pageConds != null && pageConds.length > 0) {
					for (String pageFldExpress : pageConds) {
						Field pageFld = m_flds.getFieldConverter().getField(pageFldExpress);
						if (!convCols.contains(pageFld)) {
							convCols.add(pageFld);
						}
					}
				}
			}
		}

		boolean changedFlag = false;

		if (convCols.size() > 0) {
			ArrayList<GroupItem> groupItems = new ArrayList<GroupItem>();
			GroupItem[] orgGrpItems = aggrDesc.getGroupFields();

			for (Field field : convCols) {
				boolean isNeedFlag = true;
				if (orgGrpItems != null) {
					for (GroupItem gpItem : orgGrpItems) {
						if (gpItem.getField().equals(field)) {
							isNeedFlag = false;
							break;
						}
					}
				}

				if (isNeedFlag) {
					groupItems.add(new GroupItem(field));
					changedFlag = true;
				}
			}

			if (changedFlag && orgGrpItems != null) {
				for (GroupItem gpItem : orgGrpItems) {
					groupItems.add(gpItem);
				}

				aggrDesc.setGroupFields(groupItems.toArray(new GroupItem[0]));
			}
		}

	}

	private FreeQueryParam[] getDataSetQueryParam(IContext context) {
		// @edit by ll at 2011-6-20,下午02:34:22
		// 已经有所有数据，也得要区分算法是否可以内存处理（排序可以，筛选条件就不行）
		// if (!FreeDSStateUtil.isReportExecAll(m_areaContext)) {// 如果不是整表查询
		// // 已经存在所有数据时，不必再后台查询
		// if (getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL_ALL) != null)
		// return null;
		// }
		ArrayList<FreeQueryParam> list = new ArrayList<FreeQueryParam>();
		// if (!getAreaFields().bPaging)
		getDSTool().setPageParameter(null);
		FilterDescriptor pageDesc = null;
		if (getPolicy().isUsePageDim()) {
			pageDesc = getPageDimDescriptor();
		}
		SmartModel smart = getSmartModel();

		if (getAreaFields().isChart()) {// 图表只查询汇总数据，目前还需要加入固定成员的处理及排序
			ArrayList<IBIChartModel> chartModelList = getAreaFields().getChartModelList();
			if (chartModelList.size() > 0) {
				IBIChartModel cm = chartModelList.get(0);
				if (cm instanceof IAgrrChartModel) {
					AggrDescriptor aggrD = ((IAgrrChartModel) cm).generateAgrrDescripter();
					if (aggrD != null) {
						// 处理页维度,将页维度加入分组区
						this.AddGroupFieldFromPageDimItem(aggrD);
						// 处理隐藏字段
						this.AddGroupFieldFromHiddenField(aggrD);
						m_descListAggr.add(aggrD);
					}
					// 汇总型图表需要加入固定成员处理 还有排序
					if (cm instanceof AgrrChartModel) {
						AgrrChartModel chartModel = (AgrrChartModel) cm;
						// 处理固定成员
						dealFixValuesForChart(chartModel, smart, context);
						// 处理排序
						if (chartModel.getSortType() != AnaRepField.ORDERTYPE_NONE) {
							SortDescriptor sortDes = new SortDescriptor();
							String fieldID = chartModel.getCategoryAxis().getFieldId();
							SortItem item = new SortItem(fieldID,
									chartModel.getSortType() == AnaRepField.ORDERTYPE_DESCENDING);
							sortDes.addSort(item);
							m_descListAggr.add(sortDes);
						}
						// 处理筛选
						FilterDescriptor filterDes = new FilterDescriptor();
						FilterDescriptor[] filters = AreaDescUtil.addFilterToDesc(m_exCell,
								m_repModel.getExtendAreaModel(), filterDes, smart, context);
						for (FilterDescriptor filter : filters) {
							if (filter.getFilterItemCount() > 0) {
								m_descListAggr.add(filter);
							}
						}
					}
				}
				// @edit by yuyangi at 2012-6-28,下午02:01:54 处理图表的字段描述器支持关联字段
				// 以前是查询语义模型一级的所有字段
				else if (cm instanceof ChartModel) {
					ChartModel chartModel = (ChartModel) cm;
					String[] fldIds = chartModel.getAllDataAxisId();
					if (fldIds != null && fldIds.length > 0) {
						List<Field> fldList = new ArrayList<Field>();
						for (String fldId : fldIds) {
							Field field = MetaLinkFinder.findField(smart, fldId);
							if (field == null) {
								field = new Field();
							}
							field.setExpression(fldId);
							field.setFldname(chartModel.getFieldConverter().getConvertName(fldId));
							if (field != null) {
								fldList.add(field);
							}
						}
						SelectColumnDescriptor limitDesc = new SelectColumnDescriptor();
						limitDesc.setFields(fldList.toArray(new Field[0]));
						m_descListAggr.add(limitDesc);
					}

				}
			}
			if (pageDesc != null)// 页维度也算表级过滤条件
				m_descListAggr.add(pageDesc);

			Descriptor[] policyDescs = getPolicyDescArray();// 内存描述器只能后台执行
			if (policyDescs != null)
				m_descListAggr.addAll(Arrays.asList(policyDescs));

			if (chartModelList.size() > 0) {
				IBIChartModel cm = chartModelList.get(0);
				if (cm instanceof ICombineChartModel) {
					return null;
				}
			}

			// 1.4汇总数据查询参数
			FreeQueryParam pa = createQueryParam(FreeQueryParam.DSTYPE_AGGR, m_descListAggr.toArray(new Descriptor[0]));
			return new FreeQueryParam[] { pa };
		} else if (getAreaFields().hasDetailFld) {// 需要记录明细数据的描述符)
			SelectColumnDescriptor limitDesc = null;
			// if (FreePolicyUtil.isQueryAllData(m_areaContext)) {//
			// 查询全部数据，这时要增加所有汇总字段对应的明细字段
			String pageAreaPk = getReportModel().getAreaPKByReportCondition();
			if (pageAreaPk != null && pageAreaPk.equals(getAreaPK())) {
				ReportCondition repCondition = ReportCondition.getInstance(getReportModel().getExtendAreaModel());
				limitDesc = AreaDescUtil.getDetailLimitColDescWithAggrPage(getAreaFields(),
						repCondition.isExecutedDirectly() ? null : repCondition);
			} else
				limitDesc = AreaDescUtil.getDetailLimitColDescWithAggrPage(getAreaFields(), null);
			// } else {
			// limitDesc =
			// AreaDescUtil.getDetailLimitColumnDesc(getAreaFields());
			// }
			if (limitDesc != null) {
				m_descList.add(limitDesc);
			}

			// 1.2 创建排序和筛选条件的描述器
			SortDescriptor sortDes = AreaDescUtil.getSortDescriptor(m_flds, m_exCell, m_repModel);
			if (sortDes != null)
				m_descList.add(sortDes);

			FilterDescriptor filterDes = new FilterDescriptor();
			FilterDescriptor[] filters = addAreaFilterToDesc(filterDes, smart, context);
			for (FilterDescriptor filter : filters) {
				if (filter.getFilterItemCount() > 0)// 2.2.1 创建排序和筛选条件的描述器
				{
					m_descList.add(filter);
				}
			}
			if (!FreePolicyUtil.isQueryAllData(m_areaContext)) {// 查询全部数据时不使用页维度条件，而是用全部数据在内存中再处理
				if (pageDesc != null)// 页维度也算表级过滤条件
					m_descList.add(pageDesc);
			}

			Descriptor[] policyDescs = getPolicyDescArray();// 内存描述器只能后台执行
			if (policyDescs != null)
				m_descList.addAll(Arrays.asList(policyDescs));

			Descriptor[] appendDesc = null;
			if (FreePolicyUtil.isQueryFirtSubData(m_areaContext)) {// 对于超出行数阈值的场景，直接查询第一页数据
				LimitRowDescriptor limit = new LimitRowDescriptor();
				limit.setOffsetStart(0);
				limit.setOffsetEnd(getPolicy().getPageDataRow());
				appendDesc = new LimitRowDescriptor[] { limit };
			}
			Descriptor[] desc2 = AnaDataSetTool.mergeDescriptor(smart, false, m_descList, appendDesc, null);

			// 2.4 明细数据查询参数
			FreeQueryParam detailParam = createQueryParam(FreeQueryParam.DSTYPE_DETAIL, desc2);

			if (FreePolicyUtil.isQueryAllData(m_areaContext) && getPolicy().isDataLimit())// 查询截断场景下的全部数据，则直接返回明细描述器
				return new FreeQueryParam[] { detailParam };
			else
				list.add(detailParam);// 否则添加至列表，继续
		}
		if (getAreaFields().hasGroupFld || getAreaFields().getAggrFlds().length > 0) {
			AggrDescriptor desc = AreaDescUtil.getAggrDesc(getExAreaCell(), getAreaFields(), false, null);
			if (!getAreaFields().hasDetailFld) {
				// 处理页维度,将页维度加入分组区
				this.AddGroupFieldFromPageDimItem(desc);
			}
			m_descListAggr.add(desc);
			// 1.2 创建排序和筛选条件的描述器
			m_descListAggr = addGroupSortToDesc(m_descListAggr);

			FilterDescriptor filterDes = new FilterDescriptor();
			FilterDescriptor[] filters = addAreaFilterToDesc(filterDes, smart, context);
			for (FilterDescriptor filter : filters) {
				if (filter.getFilterItemCount() > 0) {
					m_descListAggr.add(filter);
				}
			}
			if (pageDesc != null)// 页纬度也算表级过滤条件
				m_descListAggr.add(pageDesc);

			Descriptor[] policyDescs = getPolicyDescArray();// 内存描述器只能后台执行
			if (policyDescs != null)
				m_descListAggr.addAll(Arrays.asList(policyDescs));

			Descriptor[] descs = getPolicyDescArray();
			if (descs != null)
				m_descListAggr.addAll(Arrays.asList(descs));

			// 1.4汇总数据查询参数
			FreeQueryParam pa = createQueryParam(FreeQueryParam.DSTYPE_AGGR, m_descListAggr.toArray(new Descriptor[0]));
			// 对于非截断场景，如果同时有明细数据查询参数，则标志为附加参数，首次执行时可忽略
			if (!getPolicy().isDataLimit() && list.size() > 0)
				pa.setIsAppend(true);
			if (list.size() > 0) {// 对于全部导出的列表有明细加 汇总信息的 会 对 汇总
									// 描述器的上下文增加如下标志，保证不清除当前FreExecCache的缓存
				pa.getContext().setAttribute(FreeReportContextKey.KEY_CLEARTEMP_REUSE, Boolean.TRUE);
			}
			list.add(pa);
		}
		return list.toArray(new FreeQueryParam[0]);
	}

	/**
	 * 将隐藏字段增加到chart的分组字段中去
	 * 
	 * @param aggrD
	 */
	private void AddGroupFieldFromHiddenField(AggrDescriptor aggrDesc) {
		if (aggrDesc == null) {
			return;
		}
		// 处理隐藏字段,在汇总描述器上增加分组
		FreeAreaHideFields areaHideFlds = this.getAreaFields().getAreaHideFields();
		if (areaHideFlds == null) {
			return;
		}

		Field[] hideAggrFlds = areaHideFlds.getHideAggrFlds();
		if (hideAggrFlds == null || hideAggrFlds.length == 0) {
			return;
		}

		boolean changedFlag = false;

		ArrayList<GroupItem> groupItems = new ArrayList<GroupItem>();
		GroupItem[] orgGrpItems = aggrDesc.getGroupFields();

		for (Field field : hideAggrFlds) {
			boolean isNeedFlag = true;
			if (orgGrpItems != null) {
				for (GroupItem gpItem : orgGrpItems) {
					if (gpItem.getField().equals(field)) {
						isNeedFlag = false;
						break;
					}
				}
			}

			if (isNeedFlag) {
				groupItems.add(new GroupItem(field));
				changedFlag = true;
			}
		}

		if (changedFlag && orgGrpItems != null) {
			for (GroupItem gpItem : orgGrpItems) {
				groupItems.add(gpItem);
			}

			aggrDesc.setGroupFields(groupItems.toArray(new GroupItem[0]));
		}
	}

	/**
	 * 处理汇总型图表的固定成员
	 * 
	 * @author: zhongkm 2011-8-23 下午04:43:23
	 */
	private void dealFixValuesForChart(AgrrChartModel cm, SmartModel smartModel, IContext context) {
		AnaRepField anaRepField = cm.getFixField();// 主分类轴
		AnaRepField anaSecondRepField = cm.getSecondFixField();// 次分类轴
		setFieldToFilterForChart(anaRepField, smartModel);
		setFieldToFilterForChart(anaSecondRepField, smartModel);
	}

	/**
	 * 为图表将获取的固定成员信息置入描述器中
	 * 
	 * @param anaRepField
	 */
	private void setFieldToFilterForChart(AnaRepField anaRepField, SmartModel smartModel) {
		if (anaRepField != null && anaRepField.getFixValues() != null) {
			FilterDescriptor filterDes = new FilterDescriptor();
			addFilterFromFixValues(filterDes, anaRepField);
			if (filterDes.getFilterItemCount() > 0) {
				ArrayList<FixField> anaFlds = anaRepField.getFixValues();
				for (FixField fix : anaFlds) {
					if (fix.getID() == null || fix.getID().toString().trim().equals("")) {
						FilterItem item = new FilterItem();
						item.setFieldInfo(anaRepField.getField().getFldname(), anaRepField.getField().getDataType());
						// null值筛选做特殊处理
						item.setExpression("isnull(" + anaRepField.getField().getFldname() + ", '~')='~'");
						item.setValueType(FilterItem.TYPE_NULL);
						item.setLink(FilterItem.Filter_Link[1]);
						filterDes.addFilter(item);
					}
				}
			}

			FilterDescriptor[] filters = AreaDescUtil.addFilterToDesc(m_exCell, m_repModel.getExtendAreaModel(),
					filterDes, smartModel);
			for (FilterDescriptor filter : filters) {
				if (filter.getFilterItemCount() > 0) {
					m_descListAggr.add(filter);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ufida.iufo.table.exarea.IExData#getAllSmartQueryParams(com.ufida.
	 * iufo.table.exarea.IExtendAreaModel, com.ufida.dataset.IContext)
	 */
	@Override
	public FreeQueryParam[] getAllSmartQueryParams(IExtendAreaModel model, IContext context) {

		// 这步必须做，否则当把扩展区中的字段全部删掉后直接预览，会报错，m_areaContext为null
		initAreaContext(context);

		if (BaseSmartQueryUtil.isPubRepUnDoQuery(context)) {// 并不是真正执行查询
			return new FreeQueryParam[0];
		}

		// 从区域中收集分组字段和统计字段信息
		if (!getAreaFields().hasAnyFld && !getAreaFields().isChart()
				&& m_exCell.getBaseInfoSet().getExAreaType() != ExtendAreaConstants.EX_TYPE_UNIFYCHART
				&& !(m_exCell instanceof FrCpxExtAreaCell))
			return null;
		if (getSmartModel() == null && !(m_exCell instanceof FrCpxExtAreaCell)
				&& m_exCell.getBaseInfoSet().getExAreaType() != ExtendAreaConstants.EX_TYPE_UNIFYCHART) {
			return new FreeQueryParam[0];
		}

		FreePolicy policy = getPolicy();
		if (policy.getRangeType() == FreePolicy.EXEC_RANGE_AREA) {// 当前执行只涉及部分区域
			if (!policy.isAreaInRange(getAreaPK()))
				return null;
		} else if (policy.getRangeType() == FreePolicy.EXEC_RANGE_ALL) {// 整表执行
			Integer isLoad = (Integer) getExAreaCell().getAreaInfoSet().getAreaLevelInfo(
					FreePrivateContextKey.EX_AREA_LOAD);

			isLoad = dealWithExAreaLinkInfo(model, isLoad);

			if (isLoad != null && isLoad == 1) {// 联查中定义为先不加载数据
				getDSTool().clearDS();// 需要清空原来数据
				getDSTool().clearTempInfo();
				return null;
			}

		} else if (policy.getRangeType() == FreePolicy.EXEC_RANGE_ALLAREA) {// 除页维度外的所有区域执行，表示切换页维度以及部分刷新(例如排序等内存算法)
			// 已经存在所有数据时，不必再后台查询
			if (getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL_ALL) != null)
				return null;
		}

		clearDescList();// 清除旧的数据集描述器

		// 如果是统一统计图或复杂区域，则创建新类型的执行参数
		if (m_exCell.getBaseInfoSet().getExAreaType() == ExtendAreaConstants.EX_TYPE_UNIFYCHART
				|| m_exCell instanceof FrCpxExtAreaCell) {
			// 获得筛选条件列表
			Descriptor[] filterDescs = FrExcContextUtil.getDescriptorList(m_exCell, m_repModel, context);
			
			IContext remoteContext = getRemoteContext(m_areaContext);
			FreeQueryParam pa = null;
			// 如果是统计图，则创建统计图参数
			if (m_exCell.getBaseInfoSet().getExAreaType() == ExtendAreaConstants.EX_TYPE_UNIFYCHART) {
				pa = new FreeChartQueryParam(getDSPK(), getSmartModel(), remoteContext,
						filterDescs);
				ISwChartModel swChartModel = getSwChartModel();
				((FreeChartQueryParam) pa).setSwChartModel(swChartModel);

				putSWChartRtModel2Contex(swChartModel, remoteContext);
			}
			// 如果是复杂区域，则创建复杂区域参数
			else {
				// 因为后台需要调用报表参数和报表变量的解析，所以需要将报表参数和变量定义放入上下文
				// 获取报表变量定义，并将变量定义放入上下文中
				ReportVariables varPool = ReportVariables.getInstance(m_repModel.getFormatModel());
				remoteContext.setAttribute(ReportVariables.class.getName(), varPool);
				// 获取报表变量类名，并放入变量池
				String clsName = (String) m_repModel.getExtendAreaModel().getRepLevelInfo(
						IReportVarService.class.getName());
				remoteContext.setAttribute(IReportVarService.class.getName(), clsName);
				// 获取报表参数定义，并将参数定义放入上下文中
				Parameter[] pars = ReportVariableHelper.getParams(m_repModel.getFormatModel());
				remoteContext.setAttribute(ExtendAreaConstants.REPORT_PARAMS, pars);
				// hr扩展支持，标识本次查询是由扩展区域发出的
				remoteContext.setAttribute(FreePrivateContextKey.KEY_ISEXTENDAREA_QUERY, Boolean.TRUE);

				// 创建复杂区域执行参数
				pa = new FreeCpxQueryParam(getDSPK(), getSmartModel(), remoteContext,
						filterDescs);
				((FreeCpxQueryParam) pa).setFrCpxModel(((FrCpxExtAreaCell) m_exCell).getFrCpxModel());

			}
			pa.setQueryInfo(getAreaPK(), IFreeDSType.DSTYPE_DETAIL_ALL, null);
			return new FreeQueryParam[] { pa };
		}

		// 扩展区域执行参数
		FreeQueryParam[] queryParams = null;
		if (getAreaFields().isCross()) {
			queryParams = getCrossQueryParam(context);
		} else {
			queryParams = getDataSetQueryParam(context);
		}
		return queryParams;
	}

	private void putSWChartRtModel2Contex(ISwChartModel swChartModel, IContext remoteContext) {
		if (swChartModel == null) {
			return;
		}
		if (swChartModel.rtModelIsNull()) {
			return;
		}
		ISWChartRuntimeModel swChartRtModel = swChartModel.getSWChartRuntimeModel();
		SWChartRequestUtils.putSWChartRtModels(swChartModel.getChartId(), swChartRtModel, remoteContext);

	}

	/**
	 * 获得统一统计图模型
	 * 
	 * @return
	 */
	private ISwChartModel getSwChartModel() {
		ISwChartModel swChartModel = null;
		ISWChartRuntimeModel runtimeModel = null;
		// 根据位置获得区域组件模型
		IAreaModel iAreaModel = getAreaCompModel(m_exCell.getArea(), m_repModel.getFormatModel());
		if (m_repModel.getDataModel() != null) {
			IAreaModel dataAreaModel = getAreaCompModel(m_exCell.getArea(), m_repModel.getDataModel());
			if (dataAreaModel instanceof FrChartAreaModel) {
				ISwChartModel dataSWChartModel = ((FrChartAreaModel) dataAreaModel).getSwChartModel();
				if (!dataSWChartModel.rtModelIsNull()) {
					runtimeModel = dataSWChartModel.getSWChartRuntimeModel();
				}
			}
		}
		if (iAreaModel instanceof FrChartAreaModel) {
			swChartModel = (ISwChartModel) ((FrChartAreaModel) iAreaModel).getSwChartModel().clone();
			if (runtimeModel != null) {
				swChartModel.setSWChartRuntimeModel(runtimeModel);
			}
		}
		return swChartModel;
	}

	/**
	 * 从cellsModel中获取区域组件模型
	 * 
	 * @param area
	 * @param cellsModel
	 * @return
	 */
	private IAreaModel getAreaCompModel(AreaPosition area, CellsModel cellsModel) {
		// 获得区域组件模型
		AreaCompModel areaCompModel = AreaCompModel.getInstance(cellsModel);
		// 获得对应的矩形区域
		Rectangle chartArea = cellsModel.getCellRect(area, true);
		// 获得区域组件位置
		AreaCompPos compPos = new AreaCompPos(chartArea);
		// 根据位置获得区域组件模型
		return areaCompModel.get(compPos);
	}

	/**
	 * 处理删除扩展区的有联动区域的情况
	 * 
	 * @param model
	 *            扩展区域模型
	 * @param isLoad
	 *            扩展区是否加载数据
	 * @return isLoad
	 * @since ncv63
	 * @edit by zhujhc at 2012-12-6,下午08:08:08
	 */
	private Integer dealWithExAreaLinkInfo(IExtendAreaModel model, Integer isLoad) {
		// 思路: 先取到联动模型，然后根据目标扩展区的PK取到源扩展区的PK，如果源扩展区的PK对应的扩展区
		// 如果源扩展区的PK对应的扩展区存在在扩展区域模型exAreaModel中，则继续执行否则isLoad置空
		if (isLoad != null && isLoad == 1) {
			Integer oldIsLoad = isLoad;
			isLoad = null;
			ExtendAreaModel exAreaModel = (ExtendAreaModel) model;
			ExAreaLinkModel linkModel = ExAreaLinkModel.getInstance(exAreaModel);
			String sourceExAreaPK = linkModel.getSourceExAreaPK(getExAreaCell().getExAreaPK());
			if (exAreaModel != null) {
				ExtendAreaCell[] exCells = exAreaModel.getExtendAreaCells();
				if (exCells != null && exCells.length > 0) {
					for (ExtendAreaCell cell : exCells) {
						if (cell != null) {
							if (cell.getExAreaPK().equals(sourceExAreaPK)) {
								isLoad = oldIsLoad;
								break;
							}
							// else{
							// isLoad = null ;
							// }
						}
					}
				}
			}
		}
		return isLoad;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ufida.iufo.table.exarea.IExData#getPageSmartQueryParams(com.ufsoft
	 * .table.AreaPosition, com.ufsoft.table.AreaPosition)
	 */
	@Override
	public FreeQueryParam[] getPageSmartQueryParams(AreaPosition exArea, AreaPosition subArea) {
		FreeQueryParam param = getPageOneParam(exArea, subArea);
		if (param == null)
			return null;
		return new FreeQueryParam[] { param };
	}

	private FreeQueryParam getPageOneParam(AreaPosition exArea, AreaPosition subArea) {
		if (this.getSmartModel() == null) {
			return null;
		}
		ExtendAreaCell exCell = m_exCell;
		if (exCell == null)
			return null;
		ExDataDescriptor decc = getExtendDesc(m_repModel.getContext());// 顺便确保了“若尚未调用过扩展，则有些数据还没有准备好”
		if (decc == null)
			return null;

		if (getAreaFields().isChart()) {
			return null;
		}
		boolean isHor = (exCell.getBaseInfoSet().getExMode() == ExtendAreaConstants.EX_MODE_X);// 横向扩展
		AreaPosition realArea = exArea;
		if (isCross()) {// 交叉表不需要加载分页数据
			return null;
		} else {// 列表
			m_combProc = new AreaCombineProcessor(m_exCell);
			if (subArea.getWidth() >= decc.getWidth() && subArea.getHeigth() >= decc.getHeight()) {// 不是子区域
				if (getAreaFields().bPaging) {// 但是此时要执行明细数据的加载
					FreeQueryParam pa = createQueryParam(FreeQueryParam.DSTYPE_DETAIL,
							m_descList.toArray(new Descriptor[0]));
					return pa;
				}
			}
			int[] range = getRealAreaRange(exArea, realArea, isHor);
			PageParameter pageDesc = null;
			if (getAreaFields().bPaging) {// 处理分页
				realArea = subArea;
				range = getRealAreaRange(exArea, realArea, isHor);
				// // 进行需要加载的明细数据的收集
				pageDesc = AreaRowLevelUtil.calcPageDesc(range, m_rowLvls, getDSTool(), getReportModel());

				// 先设置当前的page信息，以确保能够计算数据的相对行数; 进行数据填充
				pageDesc.setIsFeeler(false);
				getDSTool().setPageParameter(pageDesc);

				if (m_repModel.shouldReloadData() && !getDSTool().hasAllDetailData()) {
					LimitRowDescriptor limitDesc = getDSTool().getSubDataLimitDesc(getPolicy().getPageDataRow());
					if (limitDesc == null)
						return null;

					// 需要直接加载分页数据
					getPolicy().setDataType(FreePolicy.EXEC_DATA_SUBDATA);

					int dSize = m_descList.size() + 1;
					Descriptor[] dds = new Descriptor[dSize];
					for (int i = 0; i < dSize - 1; i++) {
						dds[i] = m_descList.get(i);
					}
					dds[dSize - 1] = limitDesc;
					PerfWatch pw = new PerfWatch(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("1413006_0",
							null, "01413006-0916", null, new String[] { String.valueOf(limitDesc) })/*
																									 * @
																									 * res
																									 * "后台数据执行分页加载^^^^^^^Page[{0}]"
																									 */);
					try {
						FreeQueryParam pa = createQueryParam(FreeQueryParam.DSTYPE_DETAIL, dds);
						return pa;
					} catch (RuntimeException e) {
						pw.appendMessage(e);
						throw e;
					} finally {
						pw.stop();
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ufida.iufo.table.exarea.IExData#setAllDataSets(com.ufida.iufo.table
	 * .model.FreeQueryResult[])
	 */
	@Override
	public void setAllDataSets(FreeQueryResult[] results) {
		// 如果是统一统计图
		if (results[0] instanceof FreeChartQueryResult) {
			getDSTool().setSwChartResult(((FreeChartQueryResult) results[0]).getSWChartResult());
			return;
		}
		// 如果是复杂区域
		else if (results[0] instanceof FreeCpxQueryResult) {
			getDSTool().setCpxQueryResult(((FreeCpxQueryResult) results[0]).getCpxResult());
			return;
		}
		// @edit by ll at 2011-8-24,下午02:16:38 如果结果自带别名转换器，则直接使用，以便和DataSet保持一致
		FreeFieldConverter converter = results[0].getFieldConverter();
		if (converter != null)
			getAreaFields().setFieldConverter(converter);

		getDSTool().setAreaFields(getAreaFields());
		DataSet ds0 = results[0].getDs();
		if (ds0 != null && m_areaDataAdjustor != null) {
			if (m_areaDataAdjustor instanceof IReportDataAdjustor2
					&& m_areaContext.getAttribute(FreePrivateContextKey.KEY_ADJUSTOR_PART) == null) {
				DataSet ds2 = ((IReportDataAdjustor2) m_areaDataAdjustor).doAdjustDataSet(getAreaPK(), m_areaContext,
						ds0, getReportModel());
				if (ds2 == null)
					ds2 = ds0;
				results[0].setDs(ds2);
			}
		}
		if (getAreaFields().isChart()) {
			getDSTool().setDS(results[0]);
			getDSTool().setDS(new FreeQueryResult(results[0].getDs(), IFreeDSType.DSTYPE_DETAIL_ALL));
			processPageDSFirst(); // 处理页维度数据，页维度数据来源于交叉表 added by mwh 2012-0605
		} else {
			if (isCross()) {// 交叉表一定有1-2个查询结果
				getDSTool().setDS(results[0]);
				// 设置当前区域完整的明细数据集
				getDSTool().setDS(new FreeQueryResult(results[0].getDs(), IFreeDSType.DSTYPE_DETAIL_ALL));
				if (results.length == 1)
					getDSTool().setDS(new FreeQueryResult(null, FreeQueryParam.DSTYPE_AGGR_COLHEAD));
				else
					getDSTool().setDS(results[1]);
				processPageDSFirst(); // 处理页维度数据，页维度数据来源于交叉表 added by mwh
										// 2012-0605
			} else {// 列表
				getDSTool().setDS(new FreeQueryResult(null, FreeQueryParam.DSTYPE_AGGR_COLHEAD));
				for (FreeQueryResult res : results) {
					getDSTool().setDS(res);
				}
				if (isDetailDataEqAggData()) {
					getDSTool().setDS(
							new FreeQueryResult(getDSTool().getDS(IFreeDSType.DSTYPE_AGGR),
									IFreeDSType.DSTYPE_DETAIL_ALL));
				} else {
					if (getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL_ALL) == null) {
						getDSTool().setDS(
								new FreeQueryResult(getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL),
										IFreeDSType.DSTYPE_DETAIL_ALL));
					}
				}

				// 页维度执行内存处理
				processPageDSFirst();// 内存计算页维度数据集
			}
			// @edit by ll at 2011-5-26,上午11:28:40
			// 这里只坐DataSet设置，级次分组数据计算转移到展开方法中。避免从区域计算页维度和数据依赖的顺序有冲突
			// // 4.计算每级分组的数据行数以及统计值
			// getDSTool().getGroupDataCounts(isCross(), getAreaFields(),
			// DataRelation.getInstance(m_repModel.getExtendAreaModel()).getAreaRelation(getAreaPK()),
			// m_repModel);
		}

	}

	private void processPageDSFirst() {
		DataSet detailDS = null;
		// if(this.isCross() || this.isChart()){
		// detailDS = getDSTool().getDS(IFreeDSType.DSTYPE_AGGR);
		// }
		if (isDetailDataEqAggData()) {// TODO added by wangyxb 2013-05-21
										// UFOB-1802 页维度设置，若页维度字段是分组字段，无法进行数据过滤
			detailDS = getDSTool().getDS(IFreeDSType.DSTYPE_AGGR);
		} else {
			detailDS = getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL);
		}
		// @edit by yuyangi at 2012-7-13,下午01:48:15
		// detailDS==null没有查询，detailDS.getCount()==0查询结果为0
		// 在查询结果为0的时候，也需要走下面代码中的getDSTool().setDS方法，以保证和有查询结果处理一致
		if (detailDS == null /* || detailDS.getCount() == 0 */) {
			return;
		}
		ReportCondition repCondition = ReportCondition.getInstance(getReportModel().getExtendAreaModel());

		String pageArea = getReportModel().getAreaPKByReportCondition();
		// if (pageArea == null)
		// return;
		// 页维度从区域数据而来
		if (pageArea != null && pageArea.equals(getAreaPK())) {// 来自于当前区域
			if (getDSTool().getDataPage() != null)// 当前区域是分页数据，不全，不可以内存计算
				return;

			// if(!(this.isCross() || this.isChart())){
			if (!isDetailDataEqAggData()) {// TODO added by wangyxb 2013-05-21
											// UFOB-1802
											// 页维度设置，若页维度字段是分组字段，无法进行数据过滤
				getDSTool().setDS(new FreeQueryResult(detailDS, IFreeDSType.DSTYPE_DETAIL_ALL));
			}

			if (!repCondition.isExecutedDirectly()) {
				// 根据数据结果计算页维度
				DataSet pageDS = FreeDSStateUtil.getPageDSFromAreaDS(repCondition, detailDS);
				repCondition.setDataSet(pageDS, getAreaPK());// 计算出的页维度数据集
				getReportModel().getContext().setAttribute(FreeReportContextKey.KEY_PAGEDIM_CONDITION,
						AreaDescUtil.getPageDimValueMap(repCondition));
			}

		} else {
			if (getDSTool().getDataPage() == null) {// 当前区域是不是分页数据
				// 设置当前区域完整的明细数据集
				// if(!(this.isCross() || this.isChart()) ){
				if (!isDetailDataEqAggData()) {// TODO added by wangyxb
												// 2013-05-21 UFOB-1802
												// 页维度设置，若页维度字段是分组字段，无法进行数据过滤
					getDSTool().setDS(new FreeQueryResult(detailDS, IFreeDSType.DSTYPE_DETAIL_ALL));
				}
			}
		}
		if (getDSTool().getDataPage() == null) {// 当前区域是不是分页数据
												// 目前不支持分页数据的页维度内存处理，以后可能放开
												// 2013.5.24 mwh
			// 然后根据页维度进行内存过滤
			processByPageDS();
		}
	}

	public void processByPageDS() {
		// 交叉表判断去掉，mwh 2012－0605
		/*
		 * if (isCross())// 交叉表目前不能做内存过滤 return;
		 */
		if (BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext))
			return;
		DataSet allDetailDS = getDSTool().getDS(IFreeDSType.DSTYPE_DETAIL_ALL);
		// if(isDetailDataEqAggData()) {
		// allDetailDS = getDSTool().getDS(IFreeDSType.DSTYPE_AGGR);
		// }
		if (allDetailDS == null) {
			return;
		}
		ReportCondition repCondition = ReportCondition.getInstance(getReportModel().getExtendAreaModel());
		DataSet detailDS2 = allDetailDS;
		if (repCondition.hasPageCondition()) {// 内存处理页维度过滤
			detailDS2 = FreeDSStateUtil.getFiltedDS(repCondition, allDetailDS, getAreaPK());
		}
		if (detailDS2 != null) {// 继续处理排序
			// @edit by ll at 2011-7-28,上午10:29:49 整表执行是从后台拿的数据，已经排好序了，所以不用再处理
			if (!(getPolicy().getRangeType() == FreePolicy.EXEC_RANGE_ALL)) {
				SortDescriptor sortDes = AreaDescUtil.getSortDescriptor(m_flds, m_exCell, m_repModel);
				if (sortDes != null) {
					detailDS2 = DataProcessUtil.getSortedDS(detailDS2, sortDes, getAreaFields().getFieldConverter());
				}
			}
		}
		// if(this.isCross()|| this.isChart()){
		if (isDetailDataEqAggData()) {// TODO added by wangyxb 2013-05-21
										// UFOB-1802 页维度设置，若页维度字段是分组字段，无法进行数据过滤
			getDSTool().setDS(new FreeQueryResult(detailDS2, IFreeDSType.DSTYPE_AGGR));// 页维度过滤后的区域数据集
		} else {
			getDSTool().setDS(new FreeQueryResult(detailDS2, IFreeDSType.DSTYPE_DETAIL));// 页维度过滤后的区域数据集
		}

		getDSTool().setDS(new FreeQueryResult((detailDS2 == null) ? 0 : detailDS2.getCount()));// 区域数据集行数
		getDSTool().clearTempInfo();// 清除级次数据缓存
		clearExtendDesc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ufida.iufo.table.exarea.IExData#setPageDataSets(com.ufida.iufo.table
	 * .model.FreeQueryResult[])
	 */
	@Override
	public void setPageDataSets(FreeQueryResult[] pageDataSets) {
		if (pageDataSets == null || pageDataSets.length == 0)
			return;
		pageDataSets[0].setType(FreeQueryParam.DSTYPE_DETAIL);
		getDSTool().setDS(pageDataSets[0]);
	}

	public void setAreaCondition(IAreaCondition condition) {
		m_areaCondition = condition;
	}

	public void setAreaBusiFormat(IBusiFormat busiFormat) {
		m_busiFormat = busiFormat;
	}

	public IBusiFormat getAreaBusiFormat() {
		return m_busiFormat;
	}

	public void setAreaDataAdjustor(IReportDataAdjustor dataAdjustor) {
		m_areaDataAdjustor = dataAdjustor;
	}

	public void initAreaContext(IContext context) {
		m_policy = null;

		// 初始化公式信息，无论是否真正的查询都要执行。m_hasFormular是不被序列化得
		m_fmlModel = m_repModel.getAreaFormulaModel();
		m_hasFormular = m_fmlModel.getMainFmls().size() > 0;

		if (BaseSmartQueryUtil.isPubRepUnDoQuery(context)) {// 并不真正查询
			m_areaContext = context;
			return;
		}
		// 设置客户端时区偏移量和时间格式
		setExecRoomToContext(context);

		ExtendAreaModel exModel = ExtendAreaModel.getInstance(this.m_repModel.getFormatModel());
		Parameter[] params = this.m_exCell.getAreaInfoSet().getAreaParams(this.m_exCell, exModel);

		// 1.3 为参数等赋值
		m_areaContext = AreaDescUtil.getContextWithParams(context, getSmartModel(), exModel, params);

		if (m_exCell.getAreaInfoSet().getAreaLevelInfo(IFreeDSType.KEY_SMARTMODEL_CHANGED) != null)
			m_areaContext.setAttribute(IFreeDSType.KEY_SMARTMODEL_CHANGED, "true");
		else
			m_areaContext.removeAttribute(IFreeDSType.KEY_SMARTMODEL_CHANGED);

		// add by yanchm,2013.1.8
		// 将报表主组织信息和组织权限扩展类放到context中
		if (exModel != null) {
			FreeMainOrgInfoSet mianOrgInfoSet = (FreeMainOrgInfoSet) exModel
					.getRepLevelInfo(FreeMainOrgInfoSet.MAINORGINFOSET);
			m_areaContext.setAttribute(FreeMainOrgInfoSet.MAINORGINFOSET, mianOrgInfoSet);
			String clsName = (String) exModel.getRepLevelInfo(IOrgAuthorityExt.KEY_IORGAUTHORITYEXT);
			m_areaContext.setAttribute(IOrgAuthorityExt.KEY_IORGAUTHORITYEXT, clsName);

		}

		// 对于组织类型的节点(此处只为自由报表管理节点和发布后的查询节点设置权限组织，发布后的报表节点在SubscibeTaskExecutor中设置)，存放当前权限组织
		if (context.getAttribute(FreePrivateContextKey.KEY_MAINORG_ORG) == null) {
			// 如果原上下文中已经有权限组织信息（目前可能是报表穿透、报表订阅执行、轻量执行时单独放入的），不用重复放
			if (context.getAttribute(FreeReportFucletContextKey.PUBLISH_REP_TYPE_KEY) == null
					&& NODE_TYPE.ORG_NODE.equals(context.getAttribute(FreeMainOrgCheckUtil.KEY_NODE_TYPE))) {
				// 对于自由报表-组织节点中的报表执行数据查询时，放入的是报表主组织
				m_areaContext.setAttribute(FreePrivateContextKey.KEY_MAINORG_ORG,
						new String[] { (String) context.getAttribute(FreeReportContextKey.PK_ORG) });
			} else if (context.getAttribute(FreeReportFucletContextKey.PUBLISH_REP_TYPE_KEY) != null
					&& context.getAttribute(FreeReportFucletContextKey.NODE_TYPE) != null
					&& !(IOrgConstProxy.GLOBEORGTYPE.equals(context.getAttribute(FreeReportFucletContextKey.NODE_TYPE)) || IOrgConstProxy.GROUPORGTYPE
							.equals(context.getAttribute(FreeReportFucletContextKey.NODE_TYPE)))) {
				// 从上下文中获取节点打开时的上下文环境
				LoginContext privateContext = (LoginContext) context
						.getAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
				if (privateContext != null) {
					// 如果当前查询节点是发布后的目录节点，并且是组织级的
					if (privateContext.getEntranceUI().getClass().getName()
							.equals("nc.ui.iufo.freereport.published.PublishedRepFunclet")) {
						// 则应该根据用户当前选择的组织参照进行权限过滤
						m_areaContext.setAttribute(FreePrivateContextKey.KEY_MAINORG_ORG,
								new String[] { (String) context.getAttribute(FreeReportContextKey.PK_ORG) });
					}
					// 否则，对于一般的发布的查询节点执行查询时，如果是组织类型的，放入的是当前功能权限组织集合
					else
						m_areaContext.setAttribute(FreePrivateContextKey.KEY_MAINORG_ORG,privateContext.getPkorgs());
				}
			}
		}
		
		// add by yanchm , 2014.8.11 , 控制BQ数据权限的使用范围
		// 对于报表平台下的自由报表，任何时候都不使用BQ数据权限
		if(!getIsModuleOfBq()){
			AnaContextUtils.setExeModeUseBQPerm(m_areaContext, false);
		}
//		// 如果是BQ平台下的自由报表，只有在发布后节点中启用BQ数据权限
//		else{
//			if(context.getAttribute(FreeReportFucletContextKey.PUBLISH_REP_TYPE_KEY) == null){
//				AnaContextUtils.setExeModeUseBQPerm(m_areaContext, false);
//			}
//		}

		// zmd mod
		// 2013.2.28，上下文中对语义模型执行场景进行标识，区分语义模型预览、自由报表管理中的数据预览和浏览，以及发布后节点的执行。（需求来源HR，要根据场景作数据混淆的处理）
		if (context.getAttribute(FreeReportFucletContextKey.PUBLISH_REP_TYPE_KEY) == null) {
			m_areaContext.setAttribute(FreePrivateContextKey.KEY_EXECUTE_TYPE, FreePrivateContextKey.KEY_FREE_REPORT);
		} else {
			m_areaContext.setAttribute(FreePrivateContextKey.KEY_EXECUTE_TYPE,
					FreePrivateContextKey.KEY_PUBLIC_NODE_EXECUTE);
		}

		// HR扩展属性（将来要增加HR特征判断，否则不走这段代码）
		if (exModel != null) {
			Object hrExtRepLevelInfo = exModel.getRepLevelInfo(FreePrivateContextKey.HR_CPX_EXTENDINFOS);
			m_areaContext.setAttribute(FreePrivateContextKey.HR_CPX_EXTENDINFOS, hrExtRepLevelInfo);
		}

		// 设置语义模型id和区域名称信息
		m_areaContext.setAttribute(FreeReportContextKey.AREA_SMARTID, getDSPK());
		m_areaContext.setAttribute(FreeReportContextKey.AREA_NAME, this.m_exCell.getBaseInfoSet().getExAreaName());

		/**
		 * 用户计算字段处理
		 */
		if (this.getAreaFields() != null) {
			List<Field> calcFields = this.getAreaFields().getAllUserCalcFields();
			if (calcFields != null && calcFields.size() > 0) {
				SmartModel smart = getSmartModel();
				smart.getMetaData().addField(calcFields.toArray(new Field[0]));
				m_areaContext.setAttribute(IFreeDSType.KEY_SMARTMODEL_CHANGED, "true");
			}
		}

		// 将使用的语义模型过滤器信息放到context中
		Object smartFilterDefs = m_exCell.getExtFmt(SmartFilterDefs.class.getName());
		if (smartFilterDefs instanceof SmartFilterDefs) {
			m_areaContext.setAttribute(FreeReportContextKey.KEY_SMART_FILTER_DEFS, smartFilterDefs);
		}

		// 将需要解析为图片的字段信息放入context中
		if (this.getAreaFields() != null) {
			List<String> pictureAnaRepFields = new ArrayList<String>();
			for (AnaRepField anaRepField : this.getAreaFields().getAllAnaFields()) {
				if (anaRepField == null || AnaRepField.FIELD_IS_NOT_PICTURE == anaRepField.getFieldIsPicture()) {
					continue;
				}
				pictureAnaRepFields.add(anaRepField.getExpression());
			}
			if (pictureAnaRepFields.isEmpty()) {
				m_areaContext.removeAttribute(FreeReportContextKey.KEY_FIELD_IS_PICTURE);
			} else {
				m_areaContext.setAttribute(FreeReportContextKey.KEY_FIELD_IS_PICTURE, pictureAnaRepFields);
			}
		}
		
		// 解析图片时，处理blob字段的类信息放入上下文中
		m_areaContext.setAttribute(ISmartContextKey.KEY_RESULTSET_PROCESSOR_CLASS, "uap.pub.bq.report.complex.base.BQSmartResultSetProcessor");
	}

	private void setExecRoomToContext(IContext context) {
		// 设置客户端时区偏移量和时间格式
		try {
			if (context.getAttribute(ISmartContextKey.KEY_TIME_OFFSET) == null) {
				if (context.getAttribute(FreePrivateContextKey.REPORT_EXEC_ROOM) == null)
					return;
				TaskExecRoom room = (TaskExecRoom) context.getAttribute(FreePrivateContextKey.REPORT_EXEC_ROOM);
				context.setAttribute(ISmartContextKey.KEY_TIME_OFFSET,
						FreeReportTimeZoneUtil.getOffset(TimezoneUtil.getTimeZone(room.getExectimezone())));
				context.setAttribute(ISmartContextKey.KEY_TIME_FORMAT,
						FreeReportTimeZoneUtil.getDateFormat(room.getExecuser()));
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	private IFormat getDimFormat(CellPosition srcPos, CellPosition aimPos, IFormat cellFormat, AnaRepField fld,
			Object realValue, AbsRowData currData) {
		return getReportModel().getDimFormat(this, m_exCell, srcPos, aimPos, cellFormat, fld, realValue, currData);
	}

	private void execAreaFormulars(AreaPosition subArea) {
		// 进行所有区域公式的计算
		// @edit by ll at 2011-10-20,上午11:27:36 为了避免重复计算，取消区域内的公式执行
		/*
		 * try { CellsModel dataModel = getReportModel().getDataModel(); if
		 * (dataModel == null) return; boolean isEnableEvent =
		 * dataModel.isEnableEvent(); dataModel.setEnableEvent(false);
		 * formulaCalUtil = new FreeAreaFormulaCalUtil(getReportModel(),
		 * subArea); formulaCalUtil.calcAllFormulaByOrder();
		 * dataModel.setEnableEvent(isEnableEvent); } catch (CmdException e) {
		 * AppDebug.debug(e); }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.ufida.iufo.table.exarea.IExData#afterFillData(com.ufsoft.table.
	 * AreaPosition, com.ufsoft.table.AreaPosition)
	 */
	@Override
	public void afterFillData(AreaPosition exArea, AreaPosition subArea) {
		if (subArea != null && !BaseSmartQueryUtil.isPubRepUnDoQuery(m_areaContext))
			execAreaFormulars(subArea);
	}

	/**
	 * 如果有分栏设置，修改扩展区描述信息
	 */
	public void changeExtendDesc() {
		Integer sepCol = (Integer) m_exCell.getAreaInfoSet()
				.getAreaLevelInfo(FreePrivateContextKey.KEY_SEPARATE_COLUMN);
		if (sepCol != null && m_bFillData) {
			m_desc.setHeight(getIntegerHeight(m_desc.getHeight(), sepCol.intValue()));
			// m_desc.setWidth(m_desc.getWidth() * sepCol.intValue());
		}
	}

	FreeQueryParam createQueryParam(int dsType, Descriptor[] descArray) {
		SmartModel smart = getSmartModel();
		IContext remoteContext = getRemoteContext(m_areaContext);
		String isChangedSmart = (String) m_areaContext.getAttribute(IFreeDSType.KEY_SMARTMODEL_CHANGED);
		if ((getReportModel() instanceof AnaReportModel) && isChangedSmart == null) {// isChangedSmart表示语义模型在内存中有修改，必须传递到后台
			smart = null;
		}

		FreeQueryParam pa = new FreeQueryParam(getDSPK(), smart, remoteContext, descArray);
		pa.setQueryInfo(getAreaPK(), dsType, m_areaCondition);
		pa.setFieldConvertor(getAreaFields().getFieldConverter());
		pa.setIsCross(isCross());
		pa.setIsReportQuery(true);

		// @edit by ll at 2011-8-24,下午02:41:52 根据HR报表特点，对于交叉表的DataSet行数放大到5w
		FreePolicy policy = getPolicy();
		if (isCross() && policy.getPageDataRow() < FreePolicyFactory.PAGINAL_DATAROW_COUNT_CROSS) {
			policy.setPaginalRows(FreePolicyFactory.PAGINAL_DATAROW_COUNT_CROSS, policy.getFillRow());
		}

		pa.setQueryPolicy(policy);

		pa.setNCFlds(getAreaFields().getNCFlds());
		return pa;
	}

	private IContext getRemoteContext(IContext context) {
		IContext cloneContext = (IContext) context.clone();
		// @edit by ll at 2011-7-6,下午05:28:17 为了减小上行流量，不传递节点权限内容，哪里需要的话自己在后台查询
		cloneContext.removeAttribute(FreeReportContextKey.FUNC_NODE_INFO);
		cloneContext.removeAttribute(FreeReportFucletContextKey.PRIVATE_CONTEXT);
		// @edit by ll at 2012-5-23,下午04:50:17 去掉客户端控件，否则远端调用时无法序列化客户端信息报错
		cloneContext.removeAttribute("tabbedListViewAction");
		cloneContext.removeAttribute(FreePrivateContextKey.KEY_FREE_REPORT_MODEL);
		return cloneContext;
	}

	private FreePolicy getPolicy() {
		if (m_policy == null) {
			m_policy = (FreePolicy) m_areaContext.getAttribute(FreePolicy.KEY_EXECPOLICY);
			if (m_policy == null) {
				m_policy = FreePolicyFactory.getDefaultPolicy();
				m_areaContext.setAttribute(FreePolicy.KEY_EXECPOLICY, m_policy);
			}
		}
		return m_policy;
	}

	private Descriptor[] getPolicyDescArray() {
		FreePolicy policy = getPolicy();
		if (policy instanceof FreeDataPolicy) {
			Descriptor[] descs = ((FreeDataPolicy) policy).getDescArrayByArea(getAreaPK());
			return descs;
		}
		return null;
	}

	private class BatchCellDatas {
		private ArrayList<Object[]> list = new ArrayList<Object[]>();

		public BatchCellDatas() {
			super();
		}

		void addData(String key, Object value) {
			Object[] obj = new Object[2];
			obj[0] = key;
			obj[1] = value;
			list.add(obj);
		}

		String[] getKeys() {
			String[] keys = new String[list.size()];
			for (int i = 0; i < keys.length; i++) {
				Object[] obj = list.get(i);
				keys[i] = (String) obj[0];
			}
			return keys;
		}

		Object[] getValues() {
			Object[] values = new Object[list.size()];
			for (int i = 0; i < values.length; i++) {
				Object[] obj = list.get(i);
				values[i] = obj[1];
			}
			return values;
		}
	}

	// 清除非填充区域的扩展信息，避免内存不必要的占用
	@SuppressWarnings("unchecked")
	public void clearCellsInfo(CellsModel dataModel, AreaPosition area) {
		ExtDataModel refer_mapModel = (ExtDataModel) dataModel
				.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP);
		Hashtable<CellPosition, CellPosition> refer_map = refer_mapModel == null ? null
				: (Hashtable<CellPosition, CellPosition>) refer_mapModel
						.getValue();
		ExtDataModel trace_mapModel = (ExtDataModel) dataModel
				.getExtProp(ExtendAreaConstants.TRACEDATA_REFER_MAP);
		Hashtable<CellPosition, TraceDataParam> trace_map = trace_mapModel == null ? null
				: (Hashtable<CellPosition, TraceDataParam>) trace_mapModel
						.getValue();
		ExtDataModel data_mapModel = (ExtDataModel) dataModel
				.getExtProp(ExtendAreaConstants.DATAVALUE_REFER_MAP);
		Hashtable<CellPosition, Object> data_map = data_mapModel == null ? null
				: (Hashtable<CellPosition, Object>) data_mapModel.getValue();

		CellPosition cStart = area.getStart();
		CellPosition cEnd = area.getEnd();
		for (int i = cStart.getRow(); i <= cEnd.getRow(); i++) {
			for (int j = cStart.getColumn(); j <= cEnd.getColumn(); j++) {
				CellPosition pos = CellPosition.getInstance(i, j);
				if (refer_map != null)
					refer_map.remove(pos);
				if (trace_map != null)
					trace_map.remove(pos);
				if (data_map != null)
					data_map.remove(pos);

				Cell cell = dataModel.getCell(pos);
				if (cell != null) {
					AnaCellCombine autoCombine = (AnaCellCombine) cell.getExtFmt(AnaCellCombine.KEY);
					cell.removeExtFmtAll();
					if (autoCombine != null) {
						cell.setExtFmt(AnaCellCombine.KEY, autoCombine);
					}
				}
			}
		}
	}

	public void setFillData(boolean fillData) {
		m_bFillData = fillData;
		if (fillData && !isCross()) {// 交叉表不区分filldata标记，永远会填充数据。所以已经填充的标记不用额外清除。
			if (getDataPaginal() != null)
				getDataPaginal().reset();
		}
	}

	// TODO added by wangyxb 2013-05-21 UFOB-1802 页维度设置，若页维度字段是分组字段，无法进行数据过滤
	/**
	 * 判断模型的detail data 和agg data是否是相同的数据集
	 */
	private boolean isDetailDataEqAggData() {
		boolean result = this.isChart() || this.isCross();// 原有业务逻辑
		if (!result) {// 既不是报表,又不是交叉表的情况
			FreeAreaHideFields hideFlds = this.getAreaFields().getAreaHideFields();
			if (hideFlds != null && hideFlds.getHideFlds() != null && hideFlds.getHideFlds().length > 0) {
				return false;
			}
			/**
			 * 除分组字段以外,所有字段均为聚合字段(无明细字段)
			 */
			result = this.getAreaFields().getAllAnaFields().size() == (this.getAreaFields().getAggrFlds().length + this
					.getAreaFields().getGroupList().size());
		}
		return result;
	}
	
	/**
	 * 获得NC数据格式处理器
	 * @return
	 */
	private NCFormatterInstance getNCFormatterInstance(){
		if(formatter == null){
			if(m_repModel != null){
				formatter = m_repModel.getNCFormatterInstance();
			}else{
				String userId = InvocationInfoProxy.getInstance().getUserId();
				formatter = new NCFormatterInstance(userId);
			}
		}
		return formatter;
	}
}
