/**
 *
 */
package com.ufida.report.anareport.areaset;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.pub.smart.metadata.DataTypeConstant;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.model.SmartModel;
import nc.pub.smart.model.descriptor.AggrDescriptor;
import nc.pub.smart.model.descriptor.GroupItem;
import nc.pub.smart.script.engine.MetaLinkFinder;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.areafomula.AreaFuncDriver;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.FormulaChangeHelper;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.exarea.AreaCrossSetInfo;
import com.ufida.iufo.table.exarea.ExCellInfoSet;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaConstants;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.iufo.table.exarea.ExtendAreaModelListener;
import com.ufida.iufo.table.model.DataRelation;
import com.ufida.iufo.table.model.IFreeDSType;
import com.ufida.iufo.table.model.ReportConditionItem;
import com.ufida.report.anareport.FreePrivateContextKey;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.FreeReportFucletContextKey;
import com.ufida.report.anareport.IFldCountType;
import com.ufida.report.anareport.areaset.wizard.ExtendAreaSetWizard;
import com.ufida.report.anareport.base.FreePageBreak;
import com.ufida.report.anareport.individuation.LoadUserIndividuation;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaCellCombine;
import com.ufida.report.anareport.model.AnaRepField;
import com.ufida.report.anareport.model.AnaReportCondition;
import com.ufida.report.anareport.model.AreaDataModel;
import com.ufida.report.anareport.model.AreaDataRelation;
import com.ufida.report.anareport.model.CombineField;
import com.ufida.report.anareport.model.CommonStyleFormat;
import com.ufida.report.anareport.model.CommonStyleUtil;
import com.ufida.report.anareport.model.DataRelaItem;
import com.ufida.report.anareport.model.FieldCountDef;
import com.ufida.report.anareport.model.FreeAreaHideFields;
import com.ufida.report.anareport.util.AnaReportFieldUtil;
import com.ufida.report.anareport.util.FreeFieldConverter;
import com.ufida.report.anareport.util.FreeReportUtil;
import com.ufida.report.anareport.util.GridSortUtil;
import com.ufida.report.chart.model.ChartManager;
import com.ufida.report.crosstable.DimValueSet;
import com.ufida.report.crosstable.FixField;
import com.ufida.report.free.plugin.param.ReportVarsModel;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.report.free.plugin.render.CollapsibleInfo;
import com.ufida.report.sysplugin.cellattr.CellPropertyValues;
import com.ufsoft.report.IufoFormat;
import com.ufsoft.report.constant.PropertyType;
import com.ufsoft.report.edit.EditConstants;
import com.ufsoft.script.AreaFmlExecutor;
import com.ufsoft.script.AreaFormulaHandler;
import com.ufsoft.script.AreaFormulaUtil;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.CommonExprCalcEnv;
import com.ufsoft.script.base.FormulaVO;
import com.ufsoft.script.base.IParsed;
import com.ufsoft.script.exception.ParseException;
import com.ufsoft.table.AbstractArea;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.Cell;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.CombinedAreaModel;
import com.ufsoft.table.CombinedCell;
import com.ufsoft.table.ExtDataModel;
import com.ufsoft.table.IArea;
import com.ufsoft.table.IAreaAtt;
import com.ufsoft.table.ICellAttr;
import com.ufsoft.table.SeperateLockSet;
import com.ufsoft.table.TableStyle;
import com.ufsoft.table.TableUtilities;
import com.ufsoft.table.format.CellLines;
import com.ufsoft.table.format.ICellLine;
import com.ufsoft.table.format.IFormat;
import com.ufsoft.table.format.TableConstant;
import com.ufsoft.table.header.HeaderModel;

/**
 * @author ll
 * @created at 2010-10-21,上午10:08:01
 *
 */

@SuppressWarnings("all")
public class ExtendAreaSetUtil {

	public static final String KEY_ORIALAREAMAP = "orial_formatarea_map";
	private static final String KEY_ORIALFORMULA = "orial_formular_model";
	private static final String KEY_CURR_EXCELLS = "curr_excells_model";
	private static final String KEY_CURR_FORMULA = "KEY_CURR_FORMULA";
	private static final String KEY_ORIAL_HIDECOL = "KEY_ORIAL_HIDECOL";// 调整前得隐藏列（列宽为0）
	private static final String KEY_ORIGIN_COL = "KEY_ORIGIN_COL";// 调整前的所有列宽
	private static final String KEY_ORIAL_WIDTH = "KEY_ORIAL_WIDTH";// 原有的列个数
	private static final String KEY_FORMULA_TITLE = "_FORMULA_";// 公式标题
	public static final String KEY_IS_SHOW_TOTAL = FreePrivateContextKey.KEY_IS_SHOW_TOTAL;
	public static char left = '(';
	public static char right = ')';
	public static String GET = "gettotal";
	public static String NULLVALUE = "Null_Value";
	public static final int COLUMN = 1;  //列
	public static final int ROW = 2;   //行
	
	/**
	 * 根据交叉区内容设置替换报表格式模型中已有的扩展区内容设置
	 * @param crossAreaContentSet
	 * @param anaModel
	 * @return
	 */
	static boolean setCrossArea(CrossAreaContentSet crossAreaContentSet, AbsAnaReportModel anaModel) {
		return setCrossArea(crossAreaContentSet, anaModel, null);
	}

	/**
	 * 根据交叉区内容设置替换报表格式模型中已有的扩展区内容设置(包含栏目设置信息)
	 *
	 * @create by wanyonga at 2010-8-2,上午08:53:33
	 * @param crossAreaContentSet
	 * @param anaModel
	 * @return
	 */
	static boolean setCrossArea(CrossAreaContentSet crossAreaContentSet, AbsAnaReportModel anaModel , AreaContentFieldSet areaContentFieldSet) {
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaCell exCell = getSetExCell(crossAreaContentSet.getAreaPk(), formatModel);
		
		// 将本次设置的所有字段暂存到区域扩展属性中，便于下次初始化面板时调用(在这个位置添加该信息，是由于上面获取的excell是缓存中的，不会获取最新的excel，因此需要实时修改里面的栏目设置信息)
		if(areaContentFieldSet != null)
			exCell.getAreaInfoSet().addAreaLevelInfo(AreaContentFieldSet.KEY_AREA_ALLFIELDSET, areaContentFieldSet);
		else{
			exCell.getAreaInfoSet().removeAreaLevelInfo(AreaContentFieldSet.KEY_AREA_ALLFIELDSET);
		}

		// 当前的扩展区域，有别于缓存中的
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		ExtendAreaCell realExCell = (ExtendAreaCell) exModel.getExAreaByPK(crossAreaContentSet.getAreaPk());
		SmartModel smartModel = null;
		if (crossAreaContentSet.getSmartModelDefID() != null)
			smartModel = anaModel.getSmartModel(crossAreaContentSet.getSmartModelDefID());
		else
			smartModel = exCell.getAreaInfoSet().getSmartModel();
		
		// 校验
		String msg = checkAreaContentSet(null, exCell, smartModel);
		if (msg != null) {
			return false;
		}
		int colFldCount = crossAreaContentSet.getColumnFldNames() == null ? 0
				: crossAreaContentSet.getColumnFldNames().length;
		int rowFldCount = crossAreaContentSet.getRowFldNames() == null ? 0
				: crossAreaContentSet.getRowFldNames().length;
		int measureFldCount = crossAreaContentSet.getMeasureSet() == null ? 0
				: crossAreaContentSet.getMeasureSet().length;
		
		// @edit by yanchm at 2012-10-23,上午11:27:54
		// 为了使交叉小计同时支持行列维度，现将原来的统计字段个数，改为行维度统计字段个数和列为度统计字段个数
		int colCountrowCount = getCountRowCount(crossAreaContentSet,COLUMN); //列为度统计字段个数
		int rowCountrowCount = getCountRowCount(crossAreaContentSet,ROW);   //行维度统计字段个数
		
		if (colFldCount == 0 || rowFldCount == 0 || measureFldCount == 0) {
			// 交叉区设置必须有行列维度和指标
			return false;
		}
		
		AreaCrossSetInfo areaCrossSet = getNewCrossSetInfo(colFldCount, rowFldCount, measureFldCount, colCountrowCount,rowCountrowCount, crossAreaContentSet.isShowTotal(),
				crossAreaContentSet.getMeasureDirection(), exCell);
		// 清除扩展区前面定义的内容
//		clearExAreaContent(exCell, anaModel, true);
		clearExAreaContent(realExCell, anaModel, true);// 清除当前
		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
		doAreaBlock(areaCrossSet.getCrossArea(), anaModel, exCell);

		// 设置报表标题后，移动交叉区域
		int height = addCrossRepTitle(crossAreaContentSet, areaCrossSet.getCrossArea(), formatModel, areaCrossSet.getCrossArea().getWidth());
		if(height > 0 && crossAreaContentSet.getTitle() != null && !"".equals(crossAreaContentSet.getTitle())) {
			AreaPosition newArea = AreaPosition.getInstance(areaCrossSet.getCrossArea().getStart().getMoveArea(height, 0), areaCrossSet.getCrossArea().getEnd().getMoveArea(height, 0));
//			CellPosition newCrossPoint = CellPosition.getInstance(areaCrossSet.getCrossPoint().getMoveArea(height, 0).getRow(), 0);
			areaCrossSet.crossMoveArea(areaCrossSet, newArea, areaCrossSet.getCrossArea());
		}

		// 将非指标行列维度所在单元进行合并 modify by yuyangi exCell.getArea()-->areaCrossSet.getCrossArea()
//		AreaPosition combinedArea = AreaPosition.getInstance(areaCrossSet.getCrossArea().getStart(), areaCrossSet.getCrossPoint()
//				.getMoveArea(-1, -1));
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		newexCell.clear();
		newexCell.setArea(areaCrossSet.getCrossArea());
		newexCell.setExModelListener(new ExtendAreaModelListener(anaModel));
		newexCell.getAreaInfoSet().setMemoryOrderMng(null);
		// newexCell.getBaseInfoSet().setExAreaName(
		// exCell.getBaseInfoSet().getExAreaName());
		// newexCell.getBaseInfoSet().setExAreaPK(
		// exCell.getBaseInfoSet().getExAreaPK());
		if (crossAreaContentSet.getSmartModelDefID() != null)
			newexCell.getAreaInfoSet().setSmartModelDefID(crossAreaContentSet.getSmartModelDefID());
		newexCell.getAreaInfoSet().setSmartModel(smartModel);
		newexCell.getAreaInfoSet().setCrossSetInfo(areaCrossSet);
		
		// @edit by yanchm at 2012-11-15,上午09:25:28,向扩展区中添加交叉小计类型（小计在前、小计在后）
		newexCell.setExtFmt(FreePrivateContextKey.KEY_CROSSCOLUMN_SUBTOTAL_TYPE, crossAreaContentSet.getColumnSubType());
		newexCell.setExtFmt(FreePrivateContextKey.KEY_CROSSROW_SUBTOTAL_TYPE, crossAreaContentSet.getRowSubType());
		
		//add by yanchm , 2013.10.31 , 区域向导中增加了隐藏字段设置后，向扩展区中添加隐藏字段设置信息
		addHideFieldsToLCrossExCell(newexCell , crossAreaContentSet);

		clearArea(anaModel.getFormatModel(), newexCell.getArea());
		exModel.addExArea(newexCell);
//		anaModel.getFormatModel().combineCell(combinedArea);

		// 添加标题
		addCrossFieldTitle(areaCrossSet, crossAreaContentSet, smartModel, anaModel);
		// 添加行列维度
		addCrossField(areaCrossSet, crossAreaContentSet, smartModel, anaModel);
		// 添加指标
		addCrossMeasure(areaCrossSet, crossAreaContentSet, smartModel, anaModel);
		newexCell.getAreaInfoSet().setDataModel(null);// 新区域的数据模型全新自动生成。
//		addDefaultFormat(areaCrossSet.getCrossArea(), anaModel);
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		anaModel.getFormatModel().setDirty(false);
		return true;
	}
	
	/**
	 * 堆叠式交叉报表，按列维度拆分成多个交叉区
	 * 
	 * @param crossAreaContentSet
	 * @param anaModel
	 * @return
	 */
	static boolean setCrossAreaStacked(CrossAreaContentSet crossAreaContentSet, AbsAnaReportModel anaModel) {
		ExtendAreaModel exModel = anaModel.getExtendAreaModel();
		ExtendAreaCell[] exCells = exModel.getExtendAreaCells();
		if(exCells == null || exCells.length == 0) {
			return false;
		}
		ExtendAreaCell exCellTemp = exCells[0];
		String exAreaPk = crossAreaContentSet.getAreaPk();
		SmartModel smartModel = exCellTemp.getAreaInfoSet().getSmartModel();
		String firstExAreaPk = exAreaPk;
		int i = 0;
		for(AreaFieldSet colFieldSet : crossAreaContentSet.getColumnFldNames()) {
			i++;
			CrossAreaContentSet crossAreaContentSetTemp = new CrossAreaContentSet(crossAreaContentSet.getMeasureDirection(), exAreaPk, 
					crossAreaContentSet.getSmartModelDefID(), new AreaFieldSet[] {colFieldSet}, crossAreaContentSet.getRowFldNames(), crossAreaContentSet.getMeasureSet(), 
					crossAreaContentSet.getRowSubType(), crossAreaContentSet.getColumnSubType());
			setCrossArea(crossAreaContentSetTemp, anaModel);
			exCellTemp = exModel.getExAreaByPK(exAreaPk);
			if(i > 1) {
				if(exCellTemp.getAreaInfoSet().getCrossSetInfo() != null) {
					exCellTemp.getAreaInfoSet().getCrossSetInfo().setShowRowHeader(false);
				}
				DataRelation dataRela = DataRelation.getInstance(exModel);
				AreaDataRelation newRela = new AreaDataRelation();
				newRela.setEnabled(true);
				ArrayList<DataRelaItem> list = new ArrayList<DataRelaItem>();
				for (AreaFieldSet item : crossAreaContentSet.getRowFldNames()) {
					DataRelaItem drItem = new DataRelaItem();
					drItem.setRelation(item.getField().getExpression(), false, null, firstExAreaPk, item.getField().getExpression());
					list.add(drItem);
				}
				newRela.setRelations(true, list.toArray(new DataRelaItem[0]));
				dataRela.setAreaRelation(exCellTemp.getExAreaPK(), newRela);
			}
			CellPosition start = CellPosition.getInstance(exCellTemp.getArea().getStart().getRow(), exCellTemp.getArea().getEnd().getColumn()+1);
			if(i < crossAreaContentSet.getColumnFldNames().length) {
				ExtendAreaCell newExCell = new ExtendAreaCell(AreaPosition.getInstance(start, start));
				newExCell.getAreaInfoSet().setSmartModel(smartModel);
				newExCell.getAreaInfoSet().addAreaLevelInfo(IFreeDSType.KEY_SMARTMODEL_CHANGED, "true");
				exModel.addExArea(newExCell);
				exAreaPk = newExCell.getExAreaPK();
			}
		}
		return true;
	}
	
	/**
	 * 向行列表扩展区中添加字段隐藏字段信息
	 * @param exCell
	 * @param areaContentSet
	 */
	private static void addHideFieldsToListExCell(ExtendAreaCell exCell , AreaContentSet areaContentSet){
		FreeAreaHideFields hideFields = new FreeAreaHideFields();
		//如果存在明细字段，则将隐藏字段添加到明细项中
		if(areaContentSet.getDetailFldNames() != null && areaContentSet.getDetailFldNames().length >0 ){
			hideFields.setHideFlds(areaContentSet.getHideFieldSet());
		}
		//如果没有明细字段，但有分组字段，则将隐藏字段添加到分组项中
		else if(areaContentSet.getGroupFldNames() != null && areaContentSet.getGroupFldNames().length >0){
			hideFields.setHideAggrFlds(areaContentSet.getHideFieldSet());
		}
		//如果只有统计字段，则加到分组项中
		else if(areaContentSet.getAreaFieldSet() != null && areaContentSet.getAreaFieldSet().length >0){
			hideFields.setHideAggrFlds(areaContentSet.getHideFieldSet());
		}
		exCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS ,hideFields );
	}
	
	/**
	 * 向交叉表扩展区中添加字段隐藏字段信息
	 * @param exCell
	 * @param areaContentSet
	 */
	private static void addHideFieldsToLCrossExCell(ExtendAreaCell exCell ,CrossAreaContentSet crossAreaContentSet){
		FreeAreaHideFields hideFields = new FreeAreaHideFields();
		//对于交叉表，直接将隐藏字段添加到分组项中
		hideFields.setHideAggrFlds(crossAreaContentSet.getHideFieldSet());
		exCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS ,hideFields );
	}

	// @edit by yanchm at 2012-10-23,上午10:57:34,
	// 分别获取行维度和列为度上的统计字段个数
	private static int getCountRowCount(CrossAreaContentSet crossAreaContentSet , int ColOrRow) {
		int countRowCount = 0;
		AreaFieldSet[] fldSets = null;
		
		
//		if(crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.ROW_DIRECTION) {
//			fldSets = crossAreaContentSet.getColumnFldNames();
//		} else if(crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.COLUMN_DIRECTION) {
//			fldSets = crossAreaContentSet.getRowFldNames();
//		}
		
		
		// @edit by yanchm at 2012-10-30,下午03:02:06
		//改变获取统计字段个数的方法，将原来的根据指标方向确定获取行维度或列维度，去掉指标方向判断的前提，即无论哪个方向均可以进行小计
		if(ColOrRow == COLUMN)
			fldSets = crossAreaContentSet.getColumnFldNames();  //获取列维度
		else if (ColOrRow == ROW)
			fldSets = crossAreaContentSet.getRowFldNames();    //获取行维度
		for(AreaFieldSet colFldSet : fldSets) {
			if(colFldSet instanceof GroupFieldSet) {
				GroupFieldSet groupFldSet = (GroupFieldSet)colFldSet;
				if(groupFldSet.isSum()) {
					countRowCount++;
				}
			}
		}
		return countRowCount;
	}
	
	/**
	 * 清楚区域单元属性和合并单元
	 * @param fmtModel
	 * @param area
	 */
	static void clearArea(CellsModel fmtModel, IArea area) {
		CombinedAreaModel cmobModel = CombinedAreaModel.getInstance(fmtModel);
		CombinedCell[] combs = cmobModel.getCombineCells(area);
		if(combs != null) {
			cmobModel.removeCombinedCell(combs);
		}
		fmtModel.clearArea(EditConstants.CELL_ALL, new IArea[] { area });
	}
	
	/**
	 * 添加交叉指标
	 *
	 * @create by wanyonga at 2010-8-2,下午03:29:11
	 *
	 * @param areaCrossSet
	 * @param crossAreaContentSet
	 * @param smartModel
	 * @param anaModel
	 */
	private static void addCrossMeasure(AreaCrossSetInfo areaCrossSet, CrossAreaContentSet crossAreaContentSet,
			SmartModel smartModel, AbsAnaReportModel anaModel) {
		int measureFldCount = crossAreaContentSet.getMeasureSet().length;
		CellPosition crossPoint = areaCrossSet.getCrossPoint();
		AreaPosition measureArea = null;
		
		// @edit by yanchm at 2012-11-15,下午04:42:17,行列维度交叉小计是否在前
		boolean isRowSubBefore = crossAreaContentSet.getRowSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP ? true : false;
		boolean isColumnBefore = crossAreaContentSet.getColumnSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP ? true : false;
		
		int startRow = crossPoint.getRow();   //交叉指标区域起始位置行数
		int startColumn = crossPoint.getColumn();  //交叉指标区域起始位置列数
		
		CellPosition end = areaCrossSet.getCrossArea().getEnd();  //交叉区域结束单元
		
		if (measureFldCount == 1) {
			// 单个指标
			if(isRowSubBefore)
				startRow = end.getRow();   //如果行维度小计在前，则交叉指标需要排到扩展区的最底行
			if(isColumnBefore)
				startColumn = end.getColumn();  //如果列维度小计在前，则交叉指标需要排到扩展区的最右侧
			measureArea = AreaPosition.getInstance(startRow, startColumn, 1, 1);
		} else if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.ROW_DIRECTION) {
			// 指标在行上显示
			if(isRowSubBefore)
				startRow = end.getRow() - measureFldCount + 1 ;  //如果行维度小计在前，则交叉指标需要排到扩展区的下方
			if(isColumnBefore)
				startColumn = end.getColumn();    //如果列维度小计在前，则交叉指标需要排到扩展区的最右侧
			measureArea = AreaPosition.getInstance(startRow, startColumn, 1, measureFldCount);
		} else if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.COLUMN_DIRECTION) {
			// 指标在列上显示
			if(isRowSubBefore)
				startRow = end.getRow();  //如果行维度小计在前，则交叉指标需要排到扩展区的最底行
			if(isColumnBefore)
				startColumn = end.getColumn() - measureFldCount + 1;    //如果列维度小计在前，则交叉指标需要排到扩展区的右侧
			measureArea = AreaPosition.getInstance(startRow, startColumn, measureFldCount, 1);
		}
		CellPosition[] poss = measureArea.split();
		CellPosition temp = null;
		Field field = null;
		AnaRepField anaField = null;
		FieldCountDef countDef = null;
		IFormat format = null;
		for (int i = 0; i < poss.length; i++) {
			temp = poss[i];
			field = SumByField.getFieldFromAreaSet(smartModel, crossAreaContentSet.getMeasureSet()[i]);
			if (field != null) {
				countDef = new FieldCountDef(field, crossAreaContentSet.getMeasureSet()[i].getCountType());
				anaField = new AnaRepField(countDef, AnaRepField.Type_CROSS_MEASURE, smartModel.getId());
				AnaReportFieldUtil.addFlds(anaModel, temp, anaField, false, true);
				addFormula(crossAreaContentSet.getMeasureSet()[i], anaModel, temp);// 增加公式
				format = crossAreaContentSet.getMeasureSet()[i].getFormat();
				if (format == null) {
					format = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT);
				}
				anaModel.getFormatModel().getCell(temp).setFormat(format);
				
				addCrossDimCount(areaCrossSet, crossAreaContentSet, smartModel, anaModel, temp, field, i);
				
		}
	}

	}
	
	// @edit by yanchm at 2012-10-30,下午04:50:17
	//向扩展区中添加交叉小计字段
	private static void addCrossDimCount (AreaCrossSetInfo areaCrossSet, CrossAreaContentSet crossAreaContentSet,
			SmartModel smartModel, AbsAnaReportModel anaModel,CellPosition temp, Field field, int i){
		
		int measureFldCount = crossAreaContentSet.getMeasureSet().length;
		CellPosition crossPoint = areaCrossSet.getCrossPoint();
		CellPosition end = areaCrossSet.getCrossArea().getEnd();  //交叉区域结束单元

		
		AreaFieldSet[] colFlds = crossAreaContentSet.getColumnFldNames();   //列维度字段
		AreaFieldSet[] rowFlds = crossAreaContentSet.getRowFldNames();   //行维度字段
		
		AreaFieldSet[] colDimCountFlds = getDimCountFields(crossAreaContentSet , COLUMN);   //列维度统计字段
		AreaFieldSet[] rowDimCountFlds = getDimCountFields(crossAreaContentSet , ROW);   //行维度统计字段
		
		boolean isRowSubBefore = crossAreaContentSet.getRowSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP ? true : false;
		boolean isColumnBefore = crossAreaContentSet.getColumnSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP ? true : false;
		
		CellPosition crossCountPoint = crossPoint;  //用于记录交叉小计填充范围的起始位置
		CellPosition aimPos = temp;
		CellPosition dimTitlePos = null;
		
		Boolean isSubTotal = false ; //判断是否为最外层统计指标
	
		// 添加列维度统计字段对应的交叉小计
		if (colDimCountFlds != null && colDimCountFlds.length > 0) {

			for (int j = 0; j < colDimCountFlds.length; j++) {

				FieldCountDef DimCount = new FieldCountDef(field,crossAreaContentSet.getMeasureSet()[i].getCountType(),colDimCountFlds[colDimCountFlds.length - j - 1].getField());
				AnaRepField dimCountFld = new AnaRepField(DimCount,AnaRepField.Type_CROSS_SUBTOTAL, smartModel.getId());
				if(isColumnBefore){
					//如果列维度小计在前
					if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.ROW_DIRECTION) {
						aimPos = temp.getMoveArea(0, -(j+1));    //交叉小计向左移：1（指标区域宽度）+ j(已填充的小计数)
					} else {
						crossCountPoint = CellPosition.getInstance(temp.getRow(), end.getColumn() - (measureFldCount + i)); // 从所有交叉指标前一列开始顺序往前增加交叉小计.
						aimPos = crossCountPoint.getMoveArea(0, - j * measureFldCount);
					}
				}else{
					//列维度小计在后
					if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.ROW_DIRECTION) {
						aimPos = temp.getMoveArea(0, j + 1);
					} else {
						crossCountPoint = CellPosition.getInstance(temp.getRow(), crossPoint.getColumn() + measureFldCount+ i); // 从所有交叉指标下一列开始顺序往后增加交叉小计.
						aimPos = crossCountPoint.getMoveArea(0, j * measureFldCount);
					}
		
				}
				
				AnaReportFieldUtil.addFlds(anaModel, aimPos, dimCountFld, false, true);
				IFormat countFormat = getCommonFormat(aimPos,anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT);
				anaModel.getFormatModel().setCellFormat(aimPos.getRow(),aimPos.getColumn(), countFormat);

				// 添加列维度交叉小计的标题
				// 标题位置
				CellPosition colFieldPos = getFieldTitlePos(anaModel,colDimCountFlds[colDimCountFlds.length - j - 1].getFieldName(), aimPos);
				dimTitlePos = CellPosition.getInstance(colFieldPos.getRow(),aimPos.getColumn());
				// 标题内容
				isSubTotal = colDimCountFlds[colDimCountFlds.length - j - 1] == colFlds[0] ? true: false;
				String DimCountTitle = getDimCountTitle(crossAreaContentSet.getMeasureDirection(), COLUMN,measureFldCount, isSubTotal, DimCount);
				anaModel.getFormatModel().setCellValue(dimTitlePos,DimCountTitle);
				// 标题格式
				IFormat dimTitleFormat = getCommonFormat(dimTitlePos,anaModel.getFormatModel(), AreaContentSet.TITLE_FORMAT);
				anaModel.getFormatModel().setCellFormat(dimTitlePos.getRow(),dimTitlePos.getColumn(), dimTitleFormat);
			}
		}
		
		
		
		// 添加行维度统计字段对应的交叉小计
		if (rowDimCountFlds != null && rowDimCountFlds.length > 0) {
			for (int j = 0; j < rowDimCountFlds.length; j++) {
				FieldCountDef DimCount = new FieldCountDef(field,crossAreaContentSet.getMeasureSet()[i].getCountType(),rowDimCountFlds[rowDimCountFlds.length - j - 1].getField());
				AnaRepField dimCountFld = new AnaRepField(DimCount,AnaRepField.Type_CROSS_SUBTOTAL, smartModel.getId());
				if(isRowSubBefore){
					if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.COLUMN_DIRECTION) {
						aimPos = temp.getMoveArea( -(j + 1), 0);      //交叉小计向上移：1（指标区域宽度）+ j(已填充的小计数)
					} else {
						crossCountPoint =CellPosition.getInstance(end.getRow()-(measureFldCount + i),temp.getColumn()) ; // 从所有交叉指标上一行开始顺序往上增加交叉小计
						aimPos = crossCountPoint.getMoveArea( -j * measureFldCount, 0);
					}
				}else{
					if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.COLUMN_DIRECTION) {
						// 如果指标方向为按列展开，则对应的交叉小计只需在每个指标下方依次填充
						aimPos = temp.getMoveArea(j + 1, 0);
					} else {
						// 如果指标方向按行展开，所有的不同指标的同一小计将在同一列上排列，此时交叉小计需要依次往指标的下方填充
						crossCountPoint = CellPosition.getInstance(crossPoint.getRow()+(measureFldCount + i),temp.getColumn()) ; // 从所有交叉指标下一行开始顺序往下增加交叉小计
						aimPos = crossCountPoint.getMoveArea(j * measureFldCount, 0);
					}
				}
				
				AnaReportFieldUtil.addFlds(anaModel, aimPos, dimCountFld, false, true);
				IFormat countFormat = getCommonFormat(aimPos, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT);
				anaModel.getFormatModel().setCellFormat(aimPos.getRow(), aimPos.getColumn(), countFormat);
				
				// 添加行维度交叉小计的标题
				// 标题位置
				CellPosition colFieldPos = getFieldTitlePos(anaModel,rowDimCountFlds[rowDimCountFlds.length - j - 1].getFieldName(), aimPos);
				dimTitlePos = CellPosition.getInstance(aimPos.getRow() , colFieldPos.getColumn());
				// 标题内容
				isSubTotal = rowDimCountFlds[rowDimCountFlds.length - j - 1] == rowFlds[0] ? true: false;
				String DimCountTitle = getDimCountTitle(crossAreaContentSet.getMeasureDirection(), ROW, measureFldCount, isSubTotal, DimCount);
				anaModel.getFormatModel().setCellValue(dimTitlePos, DimCountTitle);
				// 标题格式
				IFormat dimTitleFormat = getCommonFormat(dimTitlePos, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT);
				anaModel.getFormatModel().setCellFormat(dimTitlePos.getRow(), dimTitlePos.getColumn(), dimTitleFormat);
			}
		}
		
		
}
	
	/**
	 * @edit by yanchm,2012.11.12
	 * 根据不同情况返回小计标题的内容
	 */
	private static String getDimCountTitle(int measureDirection , int ColOrRow , int measureCount ,Boolean isSubTotal, FieldCountDef DimCount){
		//只有两种情况下显示为小计、合计，1.指标显示方向为行方向，填充列维度交叉小计标题，且交叉指标数>1时；2.指标显示方向为列方向，填充行维度交叉小计标题，且交叉指标数>1时
		if(measureCount >1 && ( measureDirection == CrossAreaContentSet.ROW_DIRECTION && ColOrRow == COLUMN || measureDirection == CrossAreaContentSet.COLUMN_DIRECTION && ColOrRow == ROW) ){
			if(isSubTotal)
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",
				"01413006-0886")/* @res "合计" */;
			else
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",
				"01413006-0882")/* @res "小计" */;
		}
		//其他情况下，返回小计自身的标题
		else return DimCount.toString();
	}
	
	
	/**
	 * @edit by yanchm,2012.11.12
	 * 根据维度字段标题，返回维度字段的位置
	 */
	private static CellPosition getFieldTitlePos(AbsAnaReportModel anaModel ,String fieldName, CellPosition aimPos){
		CellsModel fmtModel = anaModel.getFormatModel();
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(fmtModel);
		ExtendAreaCell exCell = exModel.getExArea(aimPos);
		return AnaReportFieldUtil.getCellPositionByFldname(fmtModel , exCell, fieldName);

	}

	private static AreaFieldSet[] getDimCountFields(CrossAreaContentSet crossAreaContentSet, int colOrRow) {
		List<AreaFieldSet> countFlds = new ArrayList<AreaFieldSet>();
		AreaFieldSet[] dimFlds = null;
		// @edit by yanchm at 2012-10-23,下午04:36:41
		//交叉小计不再根据指标方向来判断行小计还是列小计，支持两个方向同时支持交叉小计
//		if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.ROW_DIRECTION) {
//			// 指标在行上显示
//			dimFlds = crossAreaContentSet.getColumnFldNames();
//		} else if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.COLUMN_DIRECTION) {
//			// 指标在列上显示
//			dimFlds = crossAreaContentSet.getRowFldNames();
//		}
		if(colOrRow == COLUMN)
			dimFlds = crossAreaContentSet.getColumnFldNames();
		else if(colOrRow == ROW)
			dimFlds = crossAreaContentSet.getRowFldNames();
		
		if(dimFlds != null) {
			for(AreaFieldSet fld : dimFlds) {
				if(fld instanceof GroupFieldSet && ((GroupFieldSet)fld).isSum()) {
					countFlds.add(fld);
				}
			}
		}
		return countFlds.toArray(new AreaFieldSet[countFlds.size()]);
	}
	
	/**
	 * 添加交叉区行列维度
	 *
	 * @create by wanyonga at 2010-8-2,下午03:06:24
	 *
	 * @param areaCrossSet
	 * @param crossAreaContentSet
	 * @param smartModel
	 * @param anaModel
	 */
	private static void addCrossField(AreaCrossSetInfo areaCrossSet, CrossAreaContentSet crossAreaContentSet,
			SmartModel smartModel, AbsAnaReportModel anaModel) {
		int measureFldCount = crossAreaContentSet.getMeasureSet().length;
		CellPosition crossPoint = areaCrossSet.getCrossPoint();
		CellPosition start = areaCrossSet.getCrossArea().getStart();
		CellPosition end = areaCrossSet.getCrossArea().getEnd();
		AreaPosition rowArea = null;
		AreaPosition colArea = null;
		
		// @edit by yanchm at 2012-11-15,下午04:29:07
		// 增加行列维度小计在前之后，需要对行列维度填充区域进行调整，只有以下四种情况下需要调整
		if(crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.ROW_DIRECTION){
		//指标显示方向为行方向
			if(crossAreaContentSet.getRowSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP)
			//1.行维度小计在前
				rowArea = AreaPosition.getInstance(end.getRow()- measureFldCount + 1, start.getColumn(), crossAreaContentSet
						.getRowFldNames().length, 1);
			if(crossAreaContentSet.getColumnSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP)
			//2.列维度小计在前
				colArea = AreaPosition.getInstance(start.getRow(), end.getColumn(), 1, crossAreaContentSet
						.getColumnFldNames().length);
		}else{
		//指标显示方向为列方向
			if(crossAreaContentSet.getRowSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP)
			//3.行维度小计在前
				rowArea = AreaPosition.getInstance(end.getRow(), start.getColumn(), crossAreaContentSet
						.getRowFldNames().length, 1);
			if(crossAreaContentSet.getColumnSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP)
			//4.列维度小计在前
				colArea = AreaPosition.getInstance(start.getRow(), end.getColumn()- measureFldCount + 1 , 1, crossAreaContentSet
						.getColumnFldNames().length);
		}
		//除了以上四种情况，其他情况下行维度、列维度区域与原来相同
		if(rowArea == null)
			rowArea = AreaPosition.getInstance(crossPoint.getRow(), start.getColumn(), crossAreaContentSet
					.getRowFldNames().length, 1);
		if(colArea == null)
			colArea = AreaPosition.getInstance(start.getRow(), crossPoint.getColumn(), 1, crossAreaContentSet
					.getColumnFldNames().length);
		
		CellPosition[] poss = rowArea.split();
		CellPosition temp = null;
		Field field = null;
		AnaRepField anaField = null;
		IFormat format = null;
		for (int i = 0; i < poss.length; i++) {
			temp = poss[i];
			field = SumByField.getCombineFldFromAreaSet(smartModel, crossAreaContentSet.getRowFldNames()[i]);
//			field = SumByField.getFieldFromAreaSet(smartModel, crossAreaContentSet.getRowFldNames()[i]);
			if (field != null) {
				anaField = new AnaRepField(field, AnaRepField.TYPE_DETAIL_FIELD, crossAreaContentSet
						.getSmartModelDefID());
				AnaReportFieldUtil.addFlds(anaModel, temp, anaField, false, true);
				addFormula(crossAreaContentSet.getRowFldNames()[i], anaModel, temp);// 增加公式
				format = crossAreaContentSet.getRowFldNames()[i].getFormat();
				if (format == null) {
					format = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT);
				}
				anaModel.getFormatModel().getCell(temp).setFormat(format);
			}
		}
		poss = colArea.split();
		for (int i = 0; i < poss.length; i++) {
			temp = poss[i];
			field = SumByField.getCombineFldFromAreaSet(smartModel, crossAreaContentSet.getColumnFldNames()[i]);
//			field = SumByField.getFieldFromAreaSet(smartModel, crossAreaContentSet.getColumnFldNames()[i]);
			if (field != null) {
				anaField = new AnaRepField(field, AnaRepField.TYPE_DETAIL_FIELD, crossAreaContentSet
						.getSmartModelDefID());
				AnaReportFieldUtil.addFlds(anaModel, temp, anaField, false, true);
				addFormula(crossAreaContentSet.getColumnFldNames()[i], anaModel, temp);// 增加公式
				format = crossAreaContentSet.getColumnFldNames()[i].getFormat();
				if (format != null) {
					anaModel.getFormatModel().getCell(temp).setFormat(format);
				} else {
					// 标题样式
					IFormat titleFormat = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.TITLE_FORMAT);
					anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(), titleFormat);
				}
			}
		}
	}

	/**
	 * 添加交叉区指标标题
	 *
	 * @create by wanyonga at 2010-8-2,下午02:40:30
	 *
	 * @param areaCrossSet
	 * @param crossAreaContentSet
	 * @param smartModel
	 */
	private static void addCrossFieldTitle(AreaCrossSetInfo areaCrossSet, CrossAreaContentSet crossAreaContentSet,
			SmartModel smartModel, AbsAnaReportModel anaModel) {
		int measureFldCount = crossAreaContentSet.getMeasureSet().length;
		CellPosition crossPoint = areaCrossSet.getCrossPoint();
		AreaPosition titleArea = null;
		if (measureFldCount == 1) {
			// 单个指标
			titleArea = AreaPosition.getInstance(areaCrossSet.getCrossArea().getStart(), areaCrossSet.getCrossArea()
					.getStart());// start-->end
		} else if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.ROW_DIRECTION) {
			// 指标在行上显示
			if(crossAreaContentSet.getRowSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP){
				//如果行维度小计在前，则指标标题需要向下放
				titleArea = AreaPosition.getInstance(areaCrossSet.getCrossArea().getEnd().getRow() - measureFldCount + 1 , crossPoint.getColumn() - 1, 1, measureFldCount);
			}else
				titleArea = AreaPosition.getInstance(crossPoint.getRow(), crossPoint.getColumn() - 1, 1, measureFldCount);
		} else if (crossAreaContentSet.getMeasureDirection() == CrossAreaContentSet.COLUMN_DIRECTION) {
			// 指标在列上显示
			if(crossAreaContentSet.getColumnSubType() == AreaContentSet.SUBTOTAL_BEFORE_GROUP){
				titleArea = AreaPosition.getInstance(crossPoint.getRow() - 1, areaCrossSet.getCrossArea().getEnd().getColumn() - measureFldCount + 1, measureFldCount, 1);
			}else
				titleArea = AreaPosition.getInstance(crossPoint.getRow() - 1, crossPoint.getColumn(), measureFldCount, 1);
		}
		CellPosition[] poss = titleArea.split();
		CellPosition temp = null;
		AreaFieldSet fieldSet = null;
		Field field = null;
		for (int i = 0; i < poss.length; i++) {
			temp = poss[i];
			if(temp.equals(areaCrossSet.getCrossArea().getStart()))//左上区域不放置标题
				continue;
			fieldSet = crossAreaContentSet.getMeasureSet()[i];
			anaModel.getFormatModel().getCellIfNullNew(temp.getRow(), temp.getColumn());
			anaModel.getFormatModel().setCellProperty(temp, PropertyType.DataType, TableConstant.CELLTYPE_SAMPLE);
			field = SumByField.getFieldFromAreaSet(smartModel, fieldSet);
			if(field instanceof FieldCountDef) {
				field = ((FieldCountDef)field).getMainField();
			}
			if (field != null) {
				anaModel.getFormatModel().setCellValue(temp, field.getCaption());
			}
			// 标题样式
			IFormat titleFormat = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.TITLE_FORMAT);
			anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(), titleFormat);
		}
		// 行维度标题
		AreaPosition rowTitleArea = AreaPosition.getInstance(areaCrossSet.getCrossArea().getStart(), areaCrossSet.getCrossPoint()
				.getMoveArea(-1, -1));
		CellPosition start = rowTitleArea.getStart();
		CellPosition end = rowTitleArea.getStart().getMoveArea(rowTitleArea.getHeigth()-1, 0);
//		crossAreaContentSet;
		for(int i = 0;i < rowTitleArea.getWidth();i++) {
			start = rowTitleArea.getStart().getMoveArea(0, i);
			end = rowTitleArea.getStart().getMoveArea(rowTitleArea.getHeigth()-1, 0).getMoveArea(0, i);
			if(i < crossAreaContentSet.getRowFldNames().length) {
//				Field rowField = crossAreaContentSet.getRowFldNames()[i].getField();
				// 设置行为度标题时，如果是组合字段，根据显示字段设置标题
				Field rowField = SumByField.getCombineFldFromAreaSet(smartModel, crossAreaContentSet.getRowFldNames()[i]);
				if(rowField instanceof CombineField) {
					rowField = ((CombineField) rowField).getShowField();
				}
				
				String title = rowField.getCaption();
				if(crossAreaContentSet.getRowFldNames()[i].getTitles() != null && crossAreaContentSet.getRowFldNames()[i].getTitles().length > 0) {
					title = crossAreaContentSet.getRowFldNames()[i].getTitles()[0];
				}
				anaModel.getFormatModel().setCellValue(start, title);
			}
			if(!start.equals(end)) {
				anaModel.getFormatModel().combineCell(AreaPosition.getInstance(start, end));
			}
			// 标题样式
			IFormat titleFormat = getCommonFormat(start, anaModel.getFormatModel(), AreaContentSet.TITLE_FORMAT);
			anaModel.getFormatModel().setCellFormat(start.getRow(), start.getColumn(), titleFormat);
		}
	}

	/**
	 * 获得新的交叉设置设置
	 *
	 * @create by wanyonga at 2010-8-2,上午11:32:07
	 *
	 * @param colFldCount
	 * @param rowFldCount
	 * @param measureFldCount
	 * @param direction
	 * @param exCell
	 * @return
	 */
	static AreaCrossSetInfo getNewCrossSetInfo(int colFldCount, int rowFldCount, int measureFldCount,int colCountrowCount,int rowCountrowCount,
			boolean isShowTotal, int direction, ExtendAreaCell exCell) {
		int startRow = exCell.getArea().getStart().getRow();
		int startCol = exCell.getArea().getStart().getColumn();
		int width = 0;
		int height = 0;
		CellPosition crossPoint = null;
		if (measureFldCount == 1) {
			// 单个指标
			width = rowFldCount + measureFldCount;
			height = colFldCount + measureFldCount;
			// @edit by yanchm at 2012-10-23,上午11:33:06
			// 加上行、列维度的交叉小计列
			width = width + colCountrowCount ;
			height = height + rowCountrowCount ;
			crossPoint = CellPosition.getInstance(startRow + colFldCount, startCol + rowFldCount);
		} else if (direction == CrossAreaContentSet.ROW_DIRECTION) {
			// 指标在行上显示
			width = rowFldCount + 2;
			height = colFldCount + measureFldCount;
			crossPoint = CellPosition.getInstance(startRow + colFldCount, startCol + rowFldCount + 1);
			width = width + colCountrowCount;
			height = height + rowCountrowCount * measureFldCount;  //交叉小计按照每个指标和统计范围依次展开
			if(isShowTotal) {
				width++;
			}
		} else if (direction == CrossAreaContentSet.COLUMN_DIRECTION) {
			// 指标在列上显示
			width = rowFldCount + measureFldCount;
			height = colFldCount + 2;
			crossPoint = CellPosition.getInstance(startRow + colFldCount + 1, startCol + rowFldCount);
			width = width + colCountrowCount * measureFldCount;
			height = height + rowCountrowCount ;
			if(isShowTotal) {
				height++;
			}
		}
		AreaPosition crossArea = AreaPosition.getInstance(startRow, startCol, width, height);
		AreaCrossSetInfo crossSetInfo = new AreaCrossSetInfo(crossArea, crossPoint);
		return crossSetInfo;
	}

	/**
	 * 根据区域内容设置替换报表格式模型中已有的扩展区内容设置
	 *
	 * @create by wanyonga at 2010-6-21,上午11:21:32
	 *
	 * @param areaContentSet
	 * @param formatModel
	 */
	static boolean set(AreaContentSet areaContentSet, AbsAnaReportModel anaModel, boolean isDoBlock ) {
		return set(areaContentSet, anaModel, isDoBlock, null);
	}
	
	/**
	 * 根据区域内容设置替换报表格式模型中已有的扩展区内容设置
	 *
	 * @create by wanyonga at 2010-6-21,上午11:21:32
	 *
	 * @param areaContentSet
	 * @param formatModel
	 */
	static boolean set(AreaContentSet areaContentSet, AbsAnaReportModel anaModel, boolean isDoBlock , AreaContentFieldSet areaContentFieldSet) {
		
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaCell exCell = getSetExCell(areaContentSet.getAreaPk(), formatModel);
		
		// 将本次设置的所有字段暂存到区域扩展属性中，便于下次初始化面板时调用(在这个位置添加该信息，是由于上面获取的excell是缓存中的，不会获取最新的excel，因此需要实时修改里面的栏目设置信息)
		if(areaContentFieldSet != null)
			exCell.getAreaInfoSet().addAreaLevelInfo(AreaContentFieldSet.KEY_AREA_ALLFIELDSET, areaContentFieldSet);
		else{
			exCell.getAreaInfoSet().removeAreaLevelInfo(AreaContentFieldSet.KEY_AREA_ALLFIELDSET);
		}
		
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
//		exModel.getRepLevelInfo(FreeMainOrgInfoSet.MAINORGINFOSET);
		ExtendAreaCell realExCell = (ExtendAreaCell) exModel.getExAreaByPK(areaContentSet.getAreaPk());
		
		SmartModel smartModel = null;
		if (exCell != null) {
			smartModel = (SmartModel)exCell.getAreaInfoSet().getSmartModel().clone();
		}

		// 校验
		String msg = checkAreaContentSet(areaContentSet, exCell, smartModel);
		if (msg != null) {
			return false;
		}
		// add by yuyangi 将分组字段从明细中去除
		areaContentSet.setDetailFldNames(GridAreaSetUtil.getDetailWithoutGroup(areaContentSet));
		int detailCount = areaContentSet.getDetailFldNames() == null ? 0 : areaContentSet.getDetailFldNames().length;
		// 级次分组数目
		int levelGroupCount = areaContentSet.getGroupFldNames() == null ? 0 : areaContentSet.getGroupFldNames().length;
		int groupWidth = areaContentSet.getGroupColWidth();
		// 分组字段数目
		int totalGroupCount = 0;
		// 表头标题的行数
//		int titleRowCount = 1;
		// 获取原始格式中的字段标题
		AreaPosition unmoveArea = (AreaPosition)exCell.getAreaInfoSet().getAreaLevelInfo(FreePrivateContextKey.KEY_UNMOVE_AREA);
		Map<String, String[]> titles = getFieldTitles(areaContentSet, anaModel, exCell, unmoveArea);
		int titleRowCount = titles.size() > 0 ? titles.values().iterator().next().length : 0;
		// 最大分组级次含分组数目
		int maxGroupLevel = 0;

		if (levelGroupCount > 0) {
			for (AreaFieldSet[] fldNames : areaContentSet.getGroupFldNames()) {
				if (fldNames != null) {
					if (maxGroupLevel < fldNames.length) {
						maxGroupLevel = fldNames.length;
					}
					totalGroupCount += fldNames.length;
					titleRowCount = getMaxTitleRowCount(titleRowCount, fldNames[0]);
				}
			}
		}
		int aggrCount = areaContentSet.getAreaFieldSet() == null ? 0 : areaContentSet.getAreaFieldSet().length;

		// 在扩展区不显示的明细字段上设置了汇总字段的数目
		int otherCount = 0;
		CommonKeyMap<String, AreaFieldSet> aggrMap = null;
		if (aggrCount > 0) {
			aggrMap = new CommonKeyMap<String, AreaFieldSet>();
			for (AreaFieldSet fieldSet : areaContentSet.getAreaFieldSet()) {
				aggrMap.put(fieldSet.getFieldName().toUpperCase(), fieldSet);
				titleRowCount = getMaxTitleRowCount(titleRowCount, fieldSet);
			}
			//统计中含有明细字段的数目
			int hasCount = 0;
			if (areaContentSet.getDetailFldNames() != null) {
				for (AreaFieldSet dtFieldName : areaContentSet.getDetailFldNames()) {
					if (aggrMap.get(dtFieldName.getFieldName().toUpperCase()) != null) {
						hasCount++;
					}
				}
			}
			if(areaContentSet.isShowCount()) {
				otherCount = aggrMap.size() - hasCount;
			}else{//@edit by zhujhc at 2012-11-1,上午11:26:31
				//修改明细字段列表为空，分组列表有字段，并且勾选小计，统计列表里面有字段，但不勾选合计，报表显示异常情况。
				if(hasCount != 0){
					otherCount = aggrMap.size() - hasCount;
				}else{
					otherCount = aggrMap.size() ;
				}
			}
		} else {
			// add by yuyangi 当areaContentSet中isShowCount为true时才设置统计字段
			if(areaContentSet.isShowCount()){
				if (detailCount > 0) {
					List<AreaFieldSet> numFieldList = getNumFiledList(smartModel, areaContentSet.getDetailFldNames(), areaContentSet.getUncountFieldSet());
					if (numFieldList != null && numFieldList.size() > 0) {
						aggrMap = new CommonKeyMap<String, AreaFieldSet>();
						AreaFieldSet fieldSet = null;
						for (AreaFieldSet fldSet : numFieldList) {
							if (fldSet.getCountType() != IFldCountType.NONE)
								fieldSet = fldSet;
							else
								fieldSet = new AreaFieldSet(fldSet.getField(), IFldCountType.TYPE_SUM);
							aggrMap.put(fldSet.getFieldName().toUpperCase(), fieldSet);
						}
						aggrCount = aggrMap.size();
					}
				}
			}
		}
		for (AreaFieldSet fieldSet : areaContentSet.getDetailFldNames()) {
			titleRowCount = getMaxTitleRowCount(titleRowCount, fieldSet);
		}
		// 计算新的区域大小

		int titleHeight = (areaContentSet.getTitle() == null || "".equals(areaContentSet.getTitle())) ? 0 : areaContentSet.getTilteHeight();
//		if(unmoveArea != null) {
//			titleHeight += unmoveArea.getHeigth(); 
//		}
		AreaPosition newArea = getNewArea(detailCount, groupWidth, totalGroupCount, maxGroupLevel, aggrCount,
				otherCount, exCell.getArea(), titleRowCount, areaContentSet.isShowCount(), getGroupNoCountNum(areaContentSet), titleHeight);
		if (newArea == null) {
			return false;
		}
		// 清除扩展区前面定义的内容
//		clearExAreaContent(exCell, anaModel, true);
		clearExAreaContent(realExCell, anaModel, true);
		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
		if(isDoBlock) {
			doAreaBlock(newArea, anaModel, exCell);
		} else {
			int insRows = newArea.getHeigth() - exCell.getArea().getHeigth();
//			HeaderEvent he = new HeaderEvent();
			if(insRows > 0) {
				formatModel.getRowHeaderModel().addHeader(exCell.getArea().getEnd().getRow(), insRows);
			}
		}
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		Map<String, IFormat> oldFormat = getOldFormat(exCell);
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		// 清除交叉设置
		newexCell.getAreaInfoSet().setCrossSetInfo(null);
		newexCell.getAreaInfoSet().setMemoryOrderMng(null) ;
		newexCell.clear();
		newexCell.setArea(newArea);
		if(realExCell.getAreaInfoSet().getDataModel() != null) {
			newexCell.getAreaInfoSet().setDataModel(realExCell.getAreaInfoSet().getDataModel());
		}
		newexCell.setExModelListener(new ExtendAreaModelListener(anaModel));

		// 处理级次设置信息
		dealWithCollapsible(newexCell , areaContentSet , smartModel);
		
		if (areaContentSet.getSmartModelDefID() != null)
			newexCell.getAreaInfoSet().setSmartModelDefID(areaContentSet.getSmartModelDefID());
		if (areaContentSet.getSmartModel() != null)
			newexCell.getAreaInfoSet().setSmartModel(areaContentSet.getSmartModel());
		exModel.addExArea(newexCell);
		
		//add by yanchm , 2013.10.31 , 区域设置向导中增加了隐藏字段设置项后，项新区域中加入隐藏字段信息
		addHideFieldsToListExCell(newexCell, areaContentSet);
		
		// 将字段设置到表格模型相应的表格单元上
		addFieldToCellsModel(detailCount, levelGroupCount, aggrCount, areaContentSet, anaModel, newArea, aggrMap, newexCell, titleRowCount, titleHeight, titles);
		//@edit by zhujhc at 2012-11-7,下午02:04:22 添加判断小计类型的扩展属性
		newexCell.setExtFmt(FreePrivateContextKey.KEY_IS_SUBTOTAL_TYPE, areaContentSet.getShowType());
		//add by madh0,2014.12.26,保存分组管理界面中显示合计选择框的状态
		newexCell.setExtFmt(FreePrivateContextKey.KEY_IS_SHOW_TOTAL,areaContentSet.isShowCount());
		resetOldFormat(formatModel, newexCell, oldFormat);
		resetUnmoveArea(unmoveArea, formatModel, exCell);
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
//		newexCell.getAreaInfoSet().setDataModel(null);// 新区域的数据模型全新自动生成。
		resetAreaDataModel(newexCell, anaModel);
		clearEmptyCols(formatModel);
		clearExCellFromCache(areaContentSet.getAreaPk(), anaModel);// 清除明细/汇总缓存
		// 用户列宽
//		LoadUserIndividuation loadUserIndiv = new LoadUserIndividuation(anaModel.getContext(),anaModel);
//		loadUserIndiv.loadUserIndiv();
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
//		addDefaultFormat(newArea, anaModel); 默认格式
		anaModel.getFormatModel().setDirty(false);
		return true;
	}
	
	private static void dealWithCollapsible(ExtendAreaCell newexCell, AreaContentSet areaContentSet, SmartModel smartModel){
		// 处理级次设置信息
		newexCell.addExtFmt(CollapsibleInfo.class.getName(),
				areaContentSet.getExtInfoByKey(CollapsibleInfo.class.getName()));
		if(areaContentSet instanceof SumByLevelAreaSet){
			if(((SumByLevelAreaSet)areaContentSet).getSmartModel() == null){
				((SumByLevelAreaSet)areaContentSet).setSmartModel(smartModel);
			}
			dealSumByCode( newexCell , (SumByLevelAreaSet)areaContentSet);
		}

		// 往扩展区中同步是否折叠显示的信息
		newexCell.addExtFmt(FreePrivateContextKey.KEY_IS_SHOWFOLD_TYPE,
				areaContentSet.isShowFold());
		if (areaContentSet.isShowFold()) {
			newexCell.addExtFmt(SumByLevelAreaSet.class.getName(),
					SumByLevelAreaSet.class.getName());
		} else {
			// 如果启动了级次设置，则设置上级次设置的信息
			if (areaContentSet instanceof SumByLevelAreaSet) {
				newexCell.addExtFmt(SumByLevelAreaSet.class.getName(),
						areaContentSet);
			}
			// 否则，清空级次设置信息
			else {
				newexCell.removeExtFmt(SumByLevelAreaSet.class.getName());
			}
		}
	}
	
	/**
	 * 处理级次设置信息
	 * @param exCell 
	 * @param areaContentSet
	 */
	private static void dealSumByCode(ExtendAreaCell exCell, SumByLevelAreaSet areaContentSet) {
		// 如果扩展区域中没有设置级次设置信息，则加上（主要用于通过代码添加级次汇总的场景），add by yanchm , 2015.4.8
//		if(exCell.getExtFmt(CollapsibleInfo.class.getName()) == null){
//			SumByLevelCode sumByLevelCode = areaContentSet.getSumByLevelCode();
//			// 增加级次字段的树形信息
//			AreaFieldSet[] sumByFields = sumByLevelCode.getAllFields();
//			List<CollapsibleInfo> collapsibleInfos = new ArrayList<CollapsibleInfo>();
//			for (AreaFieldSet sumByField : sumByFields) {
//				if (sumByField instanceof SumByField) {
//					SumByField sumbyfld = (SumByField) sumByField;
//					CollapsibleInfo info = CollapsibleInfo
//							.createCollapsibleInfo(sumbyfld.getField()
//									.getExpression(), sumbyfld.getLevelCode(),
//									exCell.getExAreaPK());
//					collapsibleInfos.add(info);
//				}
//			}
//			if (collapsibleInfos.size() > 0) {
//				exCell.addExtFmt(CollapsibleInfo.class.getName(),
//						collapsibleInfos);
//			}
//		}
		// 如果存在多级分组，设置小计在前。add by yanchm , 2015.4.8
		AreaFieldSet[][] groupFldNames = areaContentSet.getGroupFldNames();
		if(groupFldNames != null && groupFldNames.length > 0 && groupFldNames[0].length > 1){
			exCell.addExtFmt(FreePrivateContextKey.KEY_IS_SUBTOTAL_TYPE, AreaContentSet.SUBTOTAL_BEFORE_GROUP);
		}
	}

	private static Map<String, IFormat> getOldFormat(ExtendAreaCell exCell) {
		Map<String, IFormat> map = new HashMap<String, IFormat>();
		if(null == exCell || null == exCell.getCellInfoSet() ||
				null == exCell.getCellInfoSet().getM_ExtInfo()){
			return map;
		}
		Iterator<CellPosition> it = exCell.getCellInfoSet().getM_ExtInfo().keySet().iterator();
		for(;it.hasNext();) {
			CellPosition pos = it.next();
			AnaRepField repField = (AnaRepField)exCell.getCellInfoSet().getExtInfo(pos).get(ExtendAreaConstants.FIELD_INFO);
			if(repField != null) {
				IFormat format = (IFormat)exCell.getCellInfoSet().getExtInfo(pos).get(ExtendAreaConstants.CELL_FORMAT);
				String key = repField.getField().getExpression();
				if(repField.getField() instanceof FieldCountDef) {
					FieldCountDef countDef = (FieldCountDef)repField.getField();
					key = countDef.getSubTotalName();
				}
				map.put(key, format);
			}
		}
		return map;
	}
	
	private static void resetOldFormat(CellsModel formatModel, ExtendAreaCell exCell, Map<String, IFormat> oldFormat) {
		for(CellPosition pos : exCell.getArea().split()) {
			Cell cell = formatModel.getCell(pos);
			if(cell != null) {
				AnaRepField repField = (AnaRepField)cell.getExtFmt(ExtendAreaConstants.FIELD_INFO);
				if(repField != null) {
					Field field = repField.getField();
					String fldname = field.getExpression();
					if(field instanceof FieldCountDef) {
						FieldCountDef countDef = (FieldCountDef)field;
						fldname = countDef.getSubTotalName();
					}
					IFormat format = oldFormat.get(fldname);
					if(format != null) {
						cell.setFormat(format);
					}
				}
			}
		}
	}
	
	/**
	 * 清除调整格式后面得空白列
	 * @param cellsModel
	 */
	private static void clearEmptyCols(CellsModel cellsModel) {
		List<IAreaAtt> areaAttrs = cellsModel.getAreaDatas();
		HeaderModel colHeader = cellsModel.getColumnHeaderModel();
		int cols = colHeader.getCount();
		int lastCol = 0;
		for(IAreaAtt areaAttr : areaAttrs) {
			if(areaAttr.getArea().getEnd().getColumn() > lastCol) {
				lastCol = areaAttr.getArea().getEnd().getColumn();
			}
		}
		if(lastCol < cols) {
			for(int i = lastCol + 1;i < cols;i++) {
				colHeader.removeHeader(i, 1);
			}
		}
	}
	
	private static void resetUnmoveArea(AreaPosition unmoveArea,CellsModel cellsModel, ExtendAreaCell exCell) {
//		exCell.getCellInfoSet().getExtInfo(position, exName)
		if(unmoveArea == null) {
			return ;
		}
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(cellsModel);
		cellsModel.clearCells(unmoveArea);
		CombinedAreaModel.getInstance(cellsModel).removeCombinedCell(unmoveArea);
		List<CombinedCell> list = new ArrayList<CombinedCell>();
		for(CellPosition pos : unmoveArea.split() ) {
			exModel.fillCellInfo(exCell, pos, pos);
			cellsModel.removeBsFormat(pos, AnaCellCombine.KEY);
//			setCellInfo(cellInfos, stepRow, stepCol, anaReportModel);
			CombinedCell combCell = exCell.getAreaInfoSet().getCombinedCellByPos(pos);
			if(combCell != null && !list.contains(combCell)) {
				list.add(combCell);
			}
		}
		for(CombinedCell combCell : list) {
			cellsModel.combineCell(combCell.getArea());
		}
		
	}

	private static void resetAreaDataModel(ExtendAreaCell exCell, AbsAnaReportModel reportModel) {
		Object obj = reportModel.getContext().getAttribute(FreePrivateContextKey.KEY_ISQUERY_REFURBISH);
		if(obj != null) {
			AreaDataModel olddataModel = (AreaDataModel)exCell.getAreaInfoSet().getDataModel();
			if(olddataModel != null) {
				FreeFieldConverter converter = olddataModel.getAreaFields(false).getFieldConverter();
				exCell.getAreaInfoSet().addAreaLevelInfo(FreeFieldConverter.class.getName(), converter);
				AreaDataModel newdataModel = new AreaDataModel(exCell);
				newdataModel.setReportModel(reportModel);
				newdataModel.buildAreaFields(false);
				newdataModel.getAreaFields(false).setFieldConverter(converter);
				newdataModel.setAreaBusiFormat(olddataModel.getAreaBusiFormat());
				olddataModel.getDSTool().clearTempInfo();
				newdataModel.setDsTool(olddataModel.getDSTool());
				exCell.getAreaInfoSet().setDataModel(newdataModel);
			}
		} else {
			AreaDataModel olddataModel = (AreaDataModel)exCell.getAreaInfoSet().getDataModel();
			if(olddataModel != null) {
				FreeFieldConverter converter = olddataModel.getAreaFields(false).getFieldConverter();
				exCell.getAreaInfoSet().addAreaLevelInfo(FreeFieldConverter.class.getName(), converter);
				exCell.getAreaInfoSet().setDataModel(null);
			}
		}
	}
	
	private static void clearAreaDataModel(ExtendAreaCell exCell, AbsAnaReportModel reportModel) {
		AreaDataModel olddataModel = (AreaDataModel)exCell.getAreaInfoSet().getDataModel();
		if(olddataModel != null) {
			FreeFieldConverter converter = olddataModel.getAreaFields(false).getFieldConverter();
			exCell.getAreaInfoSet().addAreaLevelInfo(FreeFieldConverter.class.getName(), converter);
			exCell.getAreaInfoSet().setDataModel(null);
		}
	}
	
	/**
	 * 将原始格式中字段上一行的标题保存起来，在重新设置的时候可以保存标题信息
	 * @param formatModel
	 * @param exCell
	 * @return
	 */
	private static Map<String, String[]> getFieldTitles(
			AreaContentSet areaContentSet, AbsAnaReportModel anaModel,
			ExtendAreaCell exCell, AreaPosition unmoveArea) {
		return ExtendAreaSetWizard.getFieldTitles(areaContentSet, anaModel,
				exCell, unmoveArea);
		/*Map<String, String[]> map = new HashMap<String, String[]>();
		// 如果原来是交叉表，不记录标题内容，直接改成分组报表。
		if(exCell.getAreaInfoSet().isCrossSet()) {
			return map;
		}
		String[] titles = null;
		String title = null;
		String expression = "";
		AreaPosition area = exCell.getArea();
		int fldRow = area.getEnd().getRow() + 1;
		for(CellPosition c : area.split()) {
			if(unmoveArea != null && unmoveArea.contain(c)) {
				continue;
			}
			AnaRepField anaFld = null;
			Object obj = exCell.getCellInfoSet().getExtInfo(c, ExtendAreaConstants.FIELD_INFO);
			if(obj != null) {
				if(!exCell.getAreaInfoSet().isCrossSet() && c.getRow() > fldRow) {
					continue;
				}
				
				if(c.getRow() < fldRow) {
					fldRow = c.getRow();
				}
				anaFld = (AnaRepField)obj;
				if(anaFld.getField() == null) {
					expression = anaFld.getFieldID();
				} else {
					expression = anaFld.getField().getExpression();
				}
				
				int len = c.getRow() - area.getStart().getRow();
				if(len == 0) {
					len = 1;
				}
				titles = new String[len];
				for(int i = 0;i < len;i++) {
					title = (String)exCell.getCellInfoSet().getExtInfo(c.getMoveArea(-i - 1, 0), ExtendAreaConstants.CELL_VALUE);
					if(StringUtils.isEmpty(title)) {
						FormulaVO formula = (FormulaVO)exCell.getCellInfoSet().getExtInfo(c.getMoveArea(-i - 1, 0), ExtendAreaConstants.FORMULA);
						if(formula != null) {
							title = KEY_FORMULA_TITLE + formula.getContent();
						}
					}
					titles[i] = title;
				}
				if(areaContentSet instanceof SumByLevelAreaSet) {
					SumByLevelAreaSet sumbylvlset = (SumByLevelAreaSet)areaContentSet;
					if(sumbylvlset.getSumBylvlMap().size() > 0) {
						if(sumbylvlset.getSumBylvlMap().get(expression) != null) {
							String[] codes = sumbylvlset.getSumBylvlMap().get(expression);
							if(codes != null) {
								for(String code : codes) {
									map.put(code, titles);
								}
							}
						}
					}
				}
				if(titles != null && titles.length > 0) {
					map.put(expression, titles);
				}
			}
		}
		return map;*/
	}
	
	/**
	 * <p>获取分组但不统计的个数</p>
	 * @param areaContentSet
	 * @return
	 */
	private static int getGroupNoCountNum(AreaContentSet areaContentSet) {
		int num = 0;
		if(areaContentSet.getGroupFldNames() != null) {
			for(AreaFieldSet[] groups : areaContentSet.getGroupFldNames()) {
				if(groups != null) {
					for(AreaFieldSet groupfield : groups) {

						if(groupfield != null) {
							if(groupfield instanceof GroupFieldSet) {
								if(!((GroupFieldSet)groupfield).isSum()) {
									num ++;
								}
							}
//							else if(groupfield instanceof AreaFieldSet) {
//								num ++;
//							}
						}
					}
				}
			}
		}
		return num;
	}

	/**
	 * <p>分组但不统计的列</p>
	 * @param areaContentSet
	 * @return
	 */
	private static List<Integer> getGroupNoCountList(AreaContentSet areaContentSet) {
		List<Integer> groupNoCountList = new ArrayList<Integer>();
		if(areaContentSet.getGroupFldNames() != null) {
			for(int i = 0;i < areaContentSet.getGroupFldNames().length;i++) {
				AreaFieldSet[] groups = areaContentSet.getGroupFldNames()[i];
				for(int j = 0; j < groups.length;j++) {
					AreaFieldSet groupfield = groups[j];
					if(groupfield != null) {
						if(groupfield instanceof GroupFieldSet) {
							if(!((GroupFieldSet)groupfield).isSum() && !groupNoCountList.contains(Integer.valueOf(i))) {
								groupNoCountList.add(Integer.valueOf(i));
							}
						}
					}
				}
			}
		}
		return groupNoCountList;
	}

	private static int getDelCellNum(int col, AreaContentSet areaContentSet) {
		int groupNum = getGroupNoCountNum(areaContentSet);
		List<Integer> toDelCol = getGroupNoCountList(areaContentSet);

		for(int i = 0; i < groupNum; i++) {
			if(col <= toDelCol.get(i)) {
				return groupNum - i;
			}
		}
		return 0;
	}

	private static int getMaxTitleRowCount(int titleRows, AreaFieldSet fieldSet) {
		String[] titles = fieldSet.getTitles();
		return Math.max(titleRows, titles == null ? 0 : titles.length);
	}

	@SuppressWarnings("unchecked")
	private static ExtendAreaCell getSetExCell(String exAreaPk, CellsModel formatModel) {
		if (exAreaPk == null) {
			return null;
		}
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		// @edit by ll at 2010-10-20,上午10:35:59 增加区域备份处理
		HashMap<String, ExtendAreaCell> orialAreaMap = (HashMap<String, ExtendAreaCell>) exModel
				.getRepLevelInfo(KEY_ORIALAREAMAP);

		if (orialAreaMap == null) {
			orialAreaMap = new HashMap<String, ExtendAreaCell>();
			exModel.addRepLevelInfo(KEY_ORIALAREAMAP, orialAreaMap);
		}
		if (!orialAreaMap.containsKey(exAreaPk)) {
			orialAreaMap.put(exAreaPk, (ExtendAreaCell) exModel.getExAreaByPK(exAreaPk).clone());
		}

		AreaFormulaModel formula = (AreaFormulaModel) exModel.getRepLevelInfo(KEY_ORIALFORMULA);
		if (formula == null)
			exModel.addRepLevelInfo(KEY_ORIALFORMULA, AreaFormulaModel.getInstance(formatModel).clone());

		return (ExtendAreaCell) orialAreaMap.get(exAreaPk).clone();
	}

	/**
	 * 隐藏字段（动态列）
	 * @param areaContentSet
	 * @param anaModel
	 * @return
	 * @throws CloneNotSupportedException
	 */
	static boolean hideFields(AreaContentSet areaContentSet, AbsAnaReportModel anaModel) throws CloneNotSupportedException {
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);// 合并单元模型
		ExtendAreaCell exCell = getSetExCell(areaContentSet.getAreaPk(), formatModel);
		// 交叉区域隐藏字段
		if(exCell.getAreaInfoSet().isCrossSet()) {
			return hideCrossFields(areaContentSet, anaModel, true, false);
		}
		ExtendAreaCell realExCell = (ExtendAreaCell)ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()).clone();
		SmartModel smartModel = null;
		if (exCell != null) {
			smartModel = exCell.getAreaInfoSet().getSmartModel();
		}
		// 校验
		String msg = checkAreaContentSet(areaContentSet, exCell, smartModel);
		if (msg != null) {
			return false;
		}

		// 计算新的区域大小，并记录需要删除字段的位置
		boolean isHor = exCell.isRowDirection();
		AreaPosition formatArea = exCell.getArea();
		int row = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		int col = isHor ? formatArea.getWidth() : formatArea.getHeigth();
		ArrayList<Integer> hideList = new ArrayList<Integer>();
		HashMap<CellPosition, CellPosition> delPosMap = new HashMap<CellPosition, CellPosition>();
		for (int i = 0; i < col; i++) {
			boolean hasHideField = false;
			boolean hasOtherField = false;
			for (int j = 0; j < row; j++) {
				CellPosition pos = formatArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
				AnaRepField fld = (AnaRepField) exCell.getCellInfoSet().getExtInfo(pos, AnaRepField.EXKEY_FIELDINFO);
				if (fld != null) {
					if (hasField(areaContentSet, fld)) {
						delPosMap.put(pos, pos);
						hasHideField = true;
//						break;
						
					}
					// 不是统计字段才算有其他字段
//					else if(fld.getFieldCountDef() == null && !(fld.getField() instanceof FieldCountDef)) {
//						hasOtherField = true;
//					}
				}
			}
			if (hasHideField && !hasOtherField)
				hideList.add(i);
		}

		CellPosition newEndPos = formatArea.getEnd().getMoveArea(isHor ? 0 : -hideList.size(),
				isHor ? -hideList.size() : 0);
		AreaPosition newArea = AreaPosition.getInstance(formatArea.getStart(), newEndPos);
		if (newArea == null) {
			return false;
		}

		// add by yuyangi 判断 设置隐藏字段后的扩展区 与 当前实际扩展区 大小增加还是减少
		CellPosition elseEnd = formatArea.getEnd();
		CellPosition elseStart = elseEnd.getMoveArea(isHor ? -row + 1 : -hideList.size() + 1,
				isHor ? -hideList.size() + 1 : -row + 1);
		AreaPosition elseArea = AreaPosition.getInstance(elseStart, elseEnd);

		int moveRowCount = newArea.getHeigth() - realExCell.getArea().getHeigth();
		int moveColCount = newArea.getWidth() - realExCell.getArea().getWidth();

		AreaPosition aimArea = AreaPosition.getInstance(elseStart, realExCell.getArea().getEnd());
		int moveCount = 0;
		if(isHor) {
			moveCount = moveColCount;
		} else {
			moveCount = moveRowCount;
		}
		// 如果增加了，则增加相应单元个
		if(hideList.size() > 0 && (moveRowCount > 0 || moveColCount > 0)) {
			aimArea = AreaPosition.getInstance(elseStart, newArea.getEnd());
			if(isHor) {
				elseStart =CellPosition.getInstance(realExCell.getArea().getStart().getRow(), realExCell.getArea().getEnd().getColumn() + 1);
				elseEnd = newArea.getEnd();
			} else {
				elseStart = CellPosition.getInstance(realExCell.getArea().getEnd().getRow() + 1, realExCell.getArea().getStart().getColumn());
				elseEnd = newArea.getEnd();
			}

			aimArea = AreaPosition.getInstance(elseStart, elseEnd);
			//
			ExtendAreaSetUtilHelper.insertCells(ExtendAreaModel.getInstance(formatModel), formatModel, realExCell, aimArea, isHor, moveCount);
		}

		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
//		doAreaBlock(newArea, anaModel, exCell);
		// modify by yuyangi
		doAreaBlock(newArea, anaModel, ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()));
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		// 清除交叉设置
		newexCell.getAreaInfoSet().setCrossSetInfo(null);
		newexCell.getAreaInfoSet().setMemoryOrderMng(null);
		newexCell.setArea(newArea);
		// newexCell.setExModelListener(exCell.getExModelListener());
		
		// 处理级次设置信息
		dealWithCollapsible(newexCell , areaContentSet , smartModel);
		
		if(areaContentSet.getSmartModelDefID() != null && !"".equals(areaContentSet.getSmartModelDefID())) {
			newexCell.getAreaInfoSet().setSmartModelDefID(areaContentSet.getSmartModelDefID());
		}
		if(areaContentSet.getSmartModel() != null) {
			newexCell.getAreaInfoSet().setSmartModel(areaContentSet.getSmartModel());
		}
		// dataModel处理
		if(newexCell.getAreaInfoSet().getDataModel() != null) {
			newexCell.getAreaInfoSet().getDataModel().setExtendAreaCell(newexCell);
		}
		exModel.removeExArea(exModel.getExAreaByPK(areaContentSet.getAreaPk()));// 清除之前扩展区域 add by yuyangi
		// 删除原扩展区中的合并单元
		ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(exCell.getArea()));
		exModel.addExArea(newexCell);
		// 隐藏
		hide(formatModel, newexCell, exCell, col, row, hideList, isHor, formatArea, delPosMap, KEY_ORIALFORMULA);

		if(hideList.size() > 0) {// 如果有隐藏列。则按隐藏列的个数清除尾部单元信息
			if(moveRowCount < 0 || moveColCount < 0) {
				anaModel.getFormatModel().clearCells(elseArea);
				ExtendAreaSetUtilHelper.deleteCells(exModel, formatModel, realExCell, aimArea, isHor, moveCount);
//				anaModel.getFormatModel().getColumnHeaderModel().removeHeader(elseArea.getStart().getColumn(), elseArea.getWidth());
			}
			anaModel.getFormatModel().clearCells(elseArea);
		}
//		anaModel.getFormatModel().clearCells(elseArea);
		clearExtFormulars(elseArea, formatModel);// 删除
		// 用户列宽
		LoadUserIndividuation loadUserIndiv = new LoadUserIndividuation(anaModel.getContext(),anaModel);
		loadUserIndiv.loadUserIndiv();
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		anaModel.getFormatModel().setDirty(false);
		return true;
	}

	/**
	 * 隐藏字段（动态列），隐藏时将关联字段一起隐藏
	 * @param areaContentSet
	 * @param anaModel
	 * @return
	 * @throws CloneNotSupportedException
	 */
	static boolean hideRelatedFields(AreaContentSet areaContentSet, AbsAnaReportModel anaModel) throws CloneNotSupportedException {
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);
		ExtendAreaCell exCell = getSetExCell(areaContentSet.getAreaPk(), formatModel);
		ExtendAreaCell realExCell = (ExtendAreaCell)ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()).clone();
		exCell.getAreaInfoSet().setDataModel(realExCell.getAreaInfoSet().getDataModel());  //更换实际扩展区的数据模型 
		SmartModel smartModel = null;
		if (exCell != null) {
			smartModel = exCell.getAreaInfoSet().getSmartModel();
		}
		
		// 校验
		String msg = checkAreaContentSet(areaContentSet, exCell, smartModel);
		if (msg != null) {
			return false;
		}
		// 计算新的区域大小，并记录需要删除字段的位置
		boolean isHor = exCell.isRowDirection();
		AreaPosition formatArea = exCell.getArea();
//		AreaPosition formatArea = realExCell.getArea();
		int row = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		int col = isHor ? formatArea.getWidth() : formatArea.getHeigth();
		ArrayList<Integer> hideList = new ArrayList<Integer>();
		HashMap<CellPosition, CellPosition> delPosMap = new HashMap<CellPosition, CellPosition>();
		for (int i = 0; i < col; i++) {
			boolean hasHideField = false;
			boolean hasOtherField = false;
			for (int j = 0; j < row; j++) {
				CellPosition pos = formatArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
				AnaRepField fld = (AnaRepField) exCell.getCellInfoSet().getExtInfo(pos, AnaRepField.EXKEY_FIELDINFO);
				if (fld != null) {
					if (ExtendAreaSetUtilHelper.hasField(areaContentSet, fld)) {
						delPosMap.put(pos, pos);
						hasHideField = true;
//						break;
					} else {
						hasOtherField = true;
					}
				}
			}
			if (hasHideField && !hasOtherField)
				hideList.add(i);
		}

		CellPosition newEndPos = formatArea.getEnd().getMoveArea(isHor ? 0 : -hideList.size(),
				isHor ? -hideList.size() : 0);
		AreaPosition newArea = AreaPosition.getInstance(formatArea.getStart(), newEndPos);
		if (newArea == null) {
			return false;
		}
		// add by yuyangi 判断 设置隐藏字段后的扩展区 与 当前实际扩展区 大小增加还是减少
		CellPosition elseEnd = formatArea.getEnd();
		CellPosition elseStart = elseEnd.getMoveArea(isHor ? -row + 1 : -hideList.size() + 1,
				isHor ? -hideList.size() + 1 : -row + 1);
		AreaPosition elseArea = AreaPosition.getInstance(elseStart, elseEnd);

		int moveRowCount = newArea.getHeigth() - realExCell.getArea().getHeigth();
		int moveColCount = newArea.getWidth() - realExCell.getArea().getWidth();

		AreaPosition aimArea = AreaPosition.getInstance(elseStart, realExCell.getArea().getEnd());
		int moveCount = 0;
		if(isHor) {
			moveCount = moveColCount;
		} else {
			moveCount = moveRowCount;
		}
		// 如果增加了，则增加相应单元个
		if(hideList.size() > 0 && (moveRowCount > 0 || moveColCount > 0)) {
			aimArea = AreaPosition.getInstance(elseStart, newArea.getEnd());
			if(isHor) {
				elseStart =CellPosition.getInstance(realExCell.getArea().getStart().getRow(), realExCell.getArea().getEnd().getColumn() + 1);
				elseEnd = newArea.getEnd();
			} else {
				elseStart = CellPosition.getInstance(realExCell.getArea().getEnd().getRow() + 1, realExCell.getArea().getStart().getColumn());
				elseEnd = newArea.getEnd();
			}

			aimArea = AreaPosition.getInstance(elseStart, elseEnd);
			// 合并单元处理
			ExtendAreaSetUtilHelper.insertCells(ExtendAreaModel.getInstance(formatModel), formatModel, realExCell, aimArea, isHor, moveCount);
		}
		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
//		doAreaBlock(newArea, anaModel, exCell);
		// modify by yuyangi
		doAreaBlock(newArea, anaModel, ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()));
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		// 清除交叉设置
		newexCell.getAreaInfoSet().setCrossSetInfo(null);
		newexCell.setArea(newArea);
		// newexCell.setExModelListener(exCell.getExModelListener());
		if(areaContentSet.getSmartModelDefID() != null && !"".equals(areaContentSet.getSmartModelDefID()))
			newexCell.getAreaInfoSet().setSmartModelDefID(areaContentSet.getSmartModelDefID());
		if(areaContentSet.getSmartModel() != null)
			newexCell.getAreaInfoSet().setSmartModel(areaContentSet.getSmartModel());

		// dataModel处理
		if(newexCell.getAreaInfoSet().getDataModel() != null) {
			newexCell.getAreaInfoSet().getDataModel().setExtendAreaCell(newexCell);
		}
		exModel.removeExArea(exModel.getExAreaByPK(areaContentSet.getAreaPk()));// 清除之前扩展区域 add by yuyangi
		// 删除原扩展区中的合并单元
		ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(exCell.getArea()));
		exModel.addExArea(newexCell);
		// 隐藏字段
		hide(formatModel, newexCell, exCell, col, row, hideList, isHor, formatArea, delPosMap, KEY_ORIALFORMULA);

		if(hideList.size() > 0) {// 如果有隐藏列。则按隐藏列的个数清除尾部单元信息
			if(moveRowCount < 0 || moveColCount < 0) {
				anaModel.getFormatModel().clearCells(elseArea);
				ExtendAreaSetUtilHelper.deleteCells(exModel, formatModel, realExCell, aimArea, isHor, moveCount);
			}
		}
		clearExtFormulars(elseArea, formatModel);// 删除
		// 用户列宽
		LoadUserIndividuation loadUserIndiv = new LoadUserIndividuation(anaModel.getContext(),anaModel);
		loadUserIndiv.loadUserIndiv();
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		anaModel.getFormatModel().setDirty(false);
		return true;
	}

	/**
	 * 隐藏字段（交叉区域隐藏）
	 * @param areaContentSet
	 * @param anaModel
	 * @param withCache 是否使用缓存原始格式
	 * @return
	 * @throws CloneNotSupportedException
	 */
	static boolean hideCrossFields(AreaContentSet areaContentSet, AbsAnaReportModel anaModel, boolean withCache, boolean isHideRelated) throws CloneNotSupportedException {
		CellsModel formatModel = anaModel.getFormatModel();
//		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
//		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);
		ExtendAreaCell exCell = null;
		if(withCache) {
			exCell = getSetExCell(areaContentSet.getAreaPk(), formatModel);
		} else {
			exCell = (ExtendAreaCell)ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()).clone();
		}
		if(exCell == null || !exCell.getAreaInfoSet().isCrossSet()) {
			return false;
		}
		ExtendAreaCell realExCell = (ExtendAreaCell)ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()).clone();
		SmartModel smartModel = null;
		if (exCell != null) {
			smartModel = exCell.getAreaInfoSet().getSmartModel();
		}
		
		// 校验
		String msg = checkAreaContentSet(areaContentSet, exCell, smartModel);
		if (msg != null) {
			return false;
		}

		AreaPosition colArea = exCell.getAreaInfoSet().getCrossSetInfo().getCrossColArea();
		AreaPosition rowArea = exCell.getAreaInfoSet().getCrossSetInfo().getCrossRowArea();
		AreaPosition measureArea = exCell.getAreaInfoSet().getCrossSetInfo().getCrossMeasArea();
//		exCell.getAreaInfoSet().getCrossSetInfo().isMeasInRow();

		// 计算新的区域大小，并记录需要删除字段的位置
		boolean isHor = exCell.isRowDirection();
		AreaPosition formatArea = exCell.getArea();
		/** 行维度隐藏 begin */
		ExtendAreaCell newexCell = getNewcrossExcell(anaModel, formatModel, areaContentSet, true, formatArea, exCell, exCell, rowArea,isHideRelated, KEY_ORIALFORMULA);
		// 同步扩展区数据和公式
		newexCell.getAreaInfoSet().setDataModel(null);// 新区域的数据模型全新自动生成。
		newexCell.getAreaInfoSet().getCrossSetInfo().setCrossTableSet(null);
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		/** 行维度隐藏 end */

		/** 列维度隐藏 begin */
		formatArea = newexCell.getArea();
		realExCell = (ExtendAreaCell)newexCell.clone();
		colArea = newexCell.getAreaInfoSet().getCrossSetInfo().getCrossColArea();
		newexCell = getNewcrossExcell(anaModel, formatModel, areaContentSet, false, formatArea, newexCell, realExCell, colArea, isHideRelated, KEY_CURR_FORMULA);
		// 同步扩展区数据和公式
		newexCell.getAreaInfoSet().setDataModel(null);// 新区域的数据模型全新自动生成。
		newexCell.getAreaInfoSet().getCrossSetInfo().setCrossTableSet(null);
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		/** 列维度隐藏 end */

		/** 指标维度隐藏 begin */
		measureArea = newexCell.getAreaInfoSet().getCrossSetInfo().getCrossMeasArea();
		realExCell = (ExtendAreaCell)newexCell.clone();
		newexCell = getNewCrossMeasure(anaModel, formatModel, areaContentSet, isHor, formatArea, newexCell, realExCell, measureArea, isHideRelated, KEY_CURR_FORMULA);
		/** 指标维度隐藏 end */
		// 同步扩展区数据和公式
		newexCell.getAreaInfoSet().setDataModel(null);// 新区域的数据模型全新自动生成。
		newexCell.getAreaInfoSet().getCrossSetInfo().setCrossTableSet(null);
		newexCell.getAreaInfoSet().setMemoryOrderMng(null);
		// 用户列宽
		LoadUserIndividuation loadUserIndiv = new LoadUserIndividuation(anaModel.getContext(),anaModel);
		loadUserIndiv.loadUserIndiv();
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		anaModel.getFormatModel().setDirty(false);
		return true;
	}

	static void resetFormulaCache(CellsModel formatModel, String key, AreaFormulaModel formula) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		if(exModel.getRepLevelInfo(key) != null) {
			exModel.removeRepLevelInfo(key);
		}
		exModel.addRepLevelInfo(key, formula.clone());
	}
	
	/**
	 * 隐藏字段（交叉区域隐藏）
	 * @param areaContentSet
	 * @param anaModel
	 * @return
	 * @throws CloneNotSupportedException
	 */
	static boolean hideCrossCellPosition(HideCrossAreaSet areaContentSet, AbsAnaReportModel anaModel) throws CloneNotSupportedException {
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaCell exCell = (ExtendAreaCell)ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()).clone();;
		ExtendAreaCell realExCell = (ExtendAreaCell)ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()).clone();

		AreaPosition colArea = exCell.getAreaInfoSet().getCrossSetInfo().getCrossColArea();// 行维度区域
		AreaPosition rowArea = exCell.getAreaInfoSet().getCrossSetInfo().getCrossRowArea();// 列维度区域

		AreaPosition formatArea = exCell.getArea();
		// 计算新的区域大小，并记录需要删除字段的位置，行维度区域隐藏
		ExtendAreaCell newexCell = getNewCrossDim(anaModel, formatModel, areaContentSet, true, formatArea, exCell, realExCell, rowArea);
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		// 取上次调整后的格式
		formatArea = newexCell.getArea();
		realExCell = newexCell;
		colArea = newexCell.getAreaInfoSet().getCrossSetInfo().getCrossColArea();
		// 列维度区域隐藏
		newexCell = getNewCrossDim(anaModel, formatModel, areaContentSet, false, formatArea, newexCell, realExCell, colArea);
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		anaModel.getFormatModel().setDirty(false);
		return true;
	}

	static ExtendAreaCell getNewCrossMeasure(AbsAnaReportModel anaModel,CellsModel formatModel, AreaContentSet areaContentSet,boolean isHor, AreaPosition formatArea, ExtendAreaCell exCell, ExtendAreaCell realExCell
			,AreaPosition dimArea, boolean isHideRelated, String fmlCacheKey) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);
		int row = isHor ? dimArea.getHeigth() : dimArea.getWidth();
		int col = isHor ? dimArea.getWidth() : dimArea.getHeigth();
		CellPosition crossPoint = exCell.getAreaInfoSet().getCrossSetInfo().getCrossPoint();
		int crossPointMoveRow = 0;

		ArrayList<Integer> hideList = new ArrayList<Integer>();
		HashMap<CellPosition, CellPosition> delPosMap = new HashMap<CellPosition, CellPosition>();
		for (int i = 0; i < col; i++) {
			boolean hasHideField = false;
			boolean hasOtherField = false;
			for (int j = 0; j < row; j++) {
				CellPosition pos = dimArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
				AnaRepField fld = (AnaRepField) realExCell.getCellInfoSet().getExtInfo(pos, AnaRepField.EXKEY_FIELDINFO);
				if (fld != null) {
					boolean hasField = false;
					if(isHideRelated) {
						hasField = ExtendAreaSetUtilHelper.hasField(areaContentSet, fld);
					} else {
						hasField = hasField(areaContentSet, fld);
					}
					if (hasField) {
						delPosMap.put(pos, pos);
						hasHideField = true;
						if(pos.getColumn() < crossPoint.getColumn() || pos.getRow() < crossPoint.getRow()) {
							crossPointMoveRow ++;
						}
//						break;
					} else {
						hasOtherField = true;
					}
				}
			}
			if (hasHideField && !hasOtherField)
				hideList.add(i);
		}

		CellPosition newEndPos = formatArea.getEnd().getMoveArea(isHor ? 0 : -hideList.size(),
				isHor ? -hideList.size() : 0);
		AreaPosition newArea = AreaPosition.getInstance(formatArea.getStart(), newEndPos);
		if (newArea == null) {
			return null;
		}

		// add by yuyangi 判断 设置隐藏字段后的扩展区 与 当前实际扩展区 大小增加还是减少
		int fmtrow = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		CellPosition elseEnd = formatArea.getEnd();
		CellPosition elseStart = elseEnd.getMoveArea(isHor ? -fmtrow + 1 : -hideList.size() + 1,
				isHor ? -hideList.size() + 1 : -fmtrow + 1);
		AreaPosition elseArea = AreaPosition.getInstance(elseStart, elseEnd);

		int moveRowCount = newArea.getHeigth() - realExCell.getArea().getHeigth();
		int moveColCount = newArea.getWidth() - realExCell.getArea().getWidth();

		AreaPosition aimArea = AreaPosition.getInstance(elseStart, realExCell.getArea().getEnd());
		int moveCount = 0;
		if(isHor) {
			moveCount = moveColCount;
		} else {
			moveCount = moveRowCount;
		}
		// 如果增加了，则增加相应单元个
		if(hideList.size() > 0 && (moveRowCount > 0 || moveColCount > 0)) {
			aimArea = AreaPosition.getInstance(elseStart, newArea.getEnd());
			if(isHor) {
				elseStart =CellPosition.getInstance(realExCell.getArea().getStart().getRow(), realExCell.getArea().getEnd().getColumn() + 1);
				elseEnd = newArea.getEnd();
			} else {
				elseStart = CellPosition.getInstance(realExCell.getArea().getEnd().getRow() + 1, realExCell.getArea().getStart().getColumn());
				elseEnd = newArea.getEnd();
			}

			aimArea = AreaPosition.getInstance(elseStart, elseEnd);
			// 合并单元处理
			ExtendAreaSetUtilHelper.insertCells(ExtendAreaModel.getInstance(formatModel), formatModel, realExCell, aimArea, isHor, moveCount);
		}

		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
//		doAreaBlock(newArea, anaModel, exCell);
		// modify by yuyangi
		doAreaBlock(newArea, anaModel, ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()));
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		// 清除交叉设置
//		newexCell.getAreaInfoSet().setCrossSetInfo(null);
		CellPosition newCrossPoint = crossPoint;
		if(isHor) {
			newCrossPoint = crossPoint.getMoveArea(0, -crossPointMoveRow);
		} else {
			newCrossPoint = crossPoint.getMoveArea(-crossPointMoveRow, 0);
		}
		AreaCrossSetInfo crossAreaInfo = new AreaCrossSetInfo(newArea, newCrossPoint);
		crossAreaInfo.setCrossTableSet(exCell.getAreaInfoSet().getCrossSetInfo().getCrossSet());
		crossAreaInfo.setFixHeader(exCell.getAreaInfoSet().getCrossSetInfo().getFixHeader(true), true);
		crossAreaInfo.setFixHeader(exCell.getAreaInfoSet().getCrossSetInfo().getFixHeader(false), false);
		crossAreaInfo.setShowColHeader(exCell.getAreaInfoSet().getCrossSetInfo().isShowColHeader());
		crossAreaInfo.setShowRowHeader(exCell.getAreaInfoSet().getCrossSetInfo().isShowRowHeader());

		newexCell.getAreaInfoSet().setCrossSetInfo(crossAreaInfo);
		newexCell.setArea(newArea);
		// newexCell.setExModelListener(exCell.getExModelListener());
		if(areaContentSet.getSmartModelDefID() != null && !"".equals(areaContentSet.getSmartModelDefID())) {
			newexCell.getAreaInfoSet().setSmartModelDefID(areaContentSet.getSmartModelDefID());
		}
		if(areaContentSet.getSmartModel() != null) {
			newexCell.getAreaInfoSet().setSmartModel(areaContentSet.getSmartModel());
		}
		// dataModel处理
		if(newexCell.getAreaInfoSet().getDataModel() != null) {
			newexCell.getAreaInfoSet().getDataModel().setExtendAreaCell(newexCell);
		}
		exModel.removeExArea(exModel.getExAreaByPK(areaContentSet.getAreaPk()));// 清除之前扩展区域 add by yuyangi
		// 删除原扩展区中的合并单元 ，避免增加扩展区时产生交叉
		ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(exCell.getArea()));
		exModel.addExArea(newexCell);

		row = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		col = isHor ? formatArea.getWidth() : formatArea.getHeigth();

		hideMeasure(formatModel, newexCell, exCell, col, row, hideList, isHor, formatArea, delPosMap, fmlCacheKey);
		if(hideList.size() > 0) {// 如果有隐藏列。则按隐藏列的个数清除尾部单元信息
			if(moveRowCount < 0 || moveColCount < 0) {
				anaModel.getFormatModel().clearCells(elseArea);
				ExtendAreaSetUtilHelper.deleteCells(exModel, formatModel, realExCell, aimArea, isHor, moveCount);
			}
		}
		clearExtFormulars(elseArea, formatModel);// 删除
		return newexCell;
	}


	static ExtendAreaCell getNewcrossExcell(AbsAnaReportModel anaModel,CellsModel formatModel, AreaContentSet areaContentSet,boolean isHor, AreaPosition formatArea, ExtendAreaCell exCell, ExtendAreaCell realExCell
			,AreaPosition dimArea, boolean isHideRelated, String fmlCacheKey) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);
		int row = isHor ? dimArea.getHeigth() : dimArea.getWidth();
		int col = isHor ? dimArea.getWidth() : dimArea.getHeigth();
		CellPosition crossPoint = exCell.getAreaInfoSet().getCrossSetInfo().getCrossPoint();
		int crossPointMoveRow = 0;

		ArrayList<Integer> hideList = new ArrayList<Integer>();
		HashMap<CellPosition, CellPosition> delPosMap = new HashMap<CellPosition, CellPosition>();
		for (int i = 0; i < col; i++) {
			boolean hasHideField = false;
			boolean hasOtherField = false;
			for (int j = 0; j < row; j++) {
				CellPosition pos = dimArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
				AnaRepField fld = (AnaRepField) realExCell.getCellInfoSet().getExtInfo(pos, AnaRepField.EXKEY_FIELDINFO);
				if (fld != null) {
					boolean hasField = false;
					if(isHideRelated) {
						hasField = ExtendAreaSetUtilHelper.hasField(areaContentSet, fld);
					} else {
						hasField = hasField(areaContentSet, fld);
					}
					if (hasField) {
						delPosMap.put(pos, pos);
						hasHideField = true;
						if(pos.getColumn() < crossPoint.getColumn() || pos.getRow() < crossPoint.getRow()) {
							crossPointMoveRow ++;
						}
//						break;
					} else {
						hasOtherField = true;
					}
				}
			}
			if (hasHideField && !hasOtherField)
				hideList.add(i);
		}

		CellPosition newEndPos = formatArea.getEnd().getMoveArea(isHor ? 0 : -hideList.size(),
				isHor ? -hideList.size() : 0);
		AreaPosition newArea = AreaPosition.getInstance(formatArea.getStart(), newEndPos);
		if (newArea == null) {
			return null;
		}

		// add by yuyangi 判断 设置隐藏字段后的扩展区 与 当前实际扩展区 大小增加还是减少
		int fmtrow = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		CellPosition elseEnd = formatArea.getEnd();
		CellPosition elseStart = elseEnd.getMoveArea(isHor ? -fmtrow + 1 : -hideList.size() + 1,
				isHor ? -hideList.size() + 1 : -fmtrow + 1);
		AreaPosition elseArea = AreaPosition.getInstance(elseStart, elseEnd);

		int moveRowCount = newArea.getHeigth() - realExCell.getArea().getHeigth();
		int moveColCount = newArea.getWidth() - realExCell.getArea().getWidth();

		AreaPosition aimArea = AreaPosition.getInstance(elseStart, realExCell.getArea().getEnd());
		int moveCount = 0;
		if(isHor) {
			moveCount = moveColCount;
		} else {
			moveCount = moveRowCount;
		}
		// 如果增加了，则增加相应单元个
		if(hideList.size() > 0 && (moveRowCount > 0 || moveColCount > 0)) {
			aimArea = AreaPosition.getInstance(elseStart, newArea.getEnd());
			if(isHor) {
				elseStart =CellPosition.getInstance(realExCell.getArea().getStart().getRow(), realExCell.getArea().getEnd().getColumn() + 1);
				elseEnd = newArea.getEnd();
			} else {
				elseStart = CellPosition.getInstance(realExCell.getArea().getEnd().getRow() + 1, realExCell.getArea().getStart().getColumn());
				elseEnd = newArea.getEnd();
			}

			aimArea = AreaPosition.getInstance(elseStart, elseEnd);
			// 合并单元处理
			ExtendAreaSetUtilHelper.insertCells(ExtendAreaModel.getInstance(formatModel), formatModel, realExCell, aimArea, isHor, moveCount);
		}

		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
//		doAreaBlock(newArea, anaModel, exCell);
		// modify by yuyangi
		doAreaBlock(newArea, anaModel, ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()));
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		// 清除交叉设置
//		newexCell.getAreaInfoSet().setCrossSetInfo(null);
		CellPosition newCrossPoint = crossPoint;
		if(isHor) {
			newCrossPoint = crossPoint.getMoveArea(0, -crossPointMoveRow);
		} else {
			newCrossPoint = crossPoint.getMoveArea(-crossPointMoveRow, 0);
		}
		AreaCrossSetInfo crossAreaInfo = new AreaCrossSetInfo(newArea, newCrossPoint);
		crossAreaInfo.setCrossTableSet(exCell.getAreaInfoSet().getCrossSetInfo().getCrossSet());
		crossAreaInfo.setFixHeader(exCell.getAreaInfoSet().getCrossSetInfo().getFixHeader(true), true);
		crossAreaInfo.setFixHeader(exCell.getAreaInfoSet().getCrossSetInfo().getFixHeader(false), false);
		crossAreaInfo.setShowColHeader(exCell.getAreaInfoSet().getCrossSetInfo().isShowColHeader());
		crossAreaInfo.setShowRowHeader(exCell.getAreaInfoSet().getCrossSetInfo().isShowRowHeader());

		newexCell.getAreaInfoSet().setCrossSetInfo(crossAreaInfo);
		newexCell.setArea(newArea);
		// newexCell.setExModelListener(exCell.getExModelListener());
		if(areaContentSet.getSmartModelDefID() != null && !"".equals(areaContentSet.getSmartModelDefID())) {
			newexCell.getAreaInfoSet().setSmartModelDefID(areaContentSet.getSmartModelDefID());
		}
		if(areaContentSet.getSmartModel() != null) {
			newexCell.getAreaInfoSet().setSmartModel(areaContentSet.getSmartModel());
		}
		// dataModel处理
		if(newexCell.getAreaInfoSet().getDataModel() != null) {
			newexCell.getAreaInfoSet().getDataModel().setExtendAreaCell(newexCell);
		}
		exModel.removeExArea(exModel.getExAreaByPK(areaContentSet.getAreaPk()));// 清除之前扩展区域 add by yuyangi
		// 删除原扩展区中的合并单元 ，避免增加扩展区时产生交叉
		ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(exCell.getArea()));
		exModel.addExArea(newexCell);

		row = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		col = isHor ? formatArea.getWidth() : formatArea.getHeigth();

		hide(formatModel, newexCell, exCell, col, row, hideList, isHor, formatArea, delPosMap, fmlCacheKey);

		if(hideList.size() > 0) {// 如果有隐藏列。则按隐藏列的个数清除尾部单元信息
			if(moveRowCount < 0 || moveColCount < 0) {
				anaModel.getFormatModel().clearCells(elseArea);
				ExtendAreaSetUtilHelper.deleteCells(exModel, formatModel, realExCell, aimArea, isHor, moveCount);
			}
		}
		clearExtFormulars(elseArea, formatModel);// 删除
		AreaFormulaModel formula = (AreaFormulaModel)AreaFormulaModel.getInstance(formatModel).clone();
		resetFormulaCache(formatModel, KEY_CURR_FORMULA, formula);
		return newexCell;
	}

	static ExtendAreaCell getNewCrossDim(AbsAnaReportModel anaModel,CellsModel formatModel, HideCrossAreaSet areaContentSet,boolean isHor, AreaPosition formatArea, ExtendAreaCell exCell, ExtendAreaCell realExCell
			,AreaPosition dimArea) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);
		int row = isHor ? dimArea.getHeigth() : dimArea.getWidth();
		int col = isHor ? dimArea.getWidth() : dimArea.getHeigth();
		CellPosition crossPoint = exCell.getAreaInfoSet().getCrossSetInfo().getCrossPoint();
		int crossPointMoveRow = 0;

		ArrayList<Integer> hideList = new ArrayList<Integer>();
		HashMap<CellPosition, CellPosition> delPosMap = new HashMap<CellPosition, CellPosition>();
		for (int i = 0; i < col; i++) {
			boolean hasHideField = false;
			for (int j = 0; j < row; j++) {
				CellPosition pos = dimArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
				if (hasPos(areaContentSet.getDelCells(), pos)) {
					delPosMap.put(pos, pos);
					hasHideField = true;
					if(pos.getColumn() < crossPoint.getColumn() || pos.getRow() < crossPoint.getRow()) {
						crossPointMoveRow ++;
					}
				}
			}
			if (hasHideField)
				hideList.add(i);
		}

		CellPosition newEndPos = formatArea.getEnd().getMoveArea(isHor ? 0 : -hideList.size(),
				isHor ? -hideList.size() : 0);
		AreaPosition newArea = AreaPosition.getInstance(formatArea.getStart(), newEndPos);
		if (newArea == null) {
			return null;
		}

		// add by yuyangi 判断 设置隐藏字段后的扩展区 与 当前实际扩展区 大小增加还是减少
		int fmtrow = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		CellPosition elseEnd = formatArea.getEnd();
		CellPosition elseStart = elseEnd.getMoveArea(isHor ? -fmtrow + 1 : -hideList.size() + 1,
				isHor ? -hideList.size() + 1 : -fmtrow + 1);
		AreaPosition elseArea = AreaPosition.getInstance(elseStart, elseEnd);

		int moveRowCount = newArea.getHeigth() - realExCell.getArea().getHeigth();
		int moveColCount = newArea.getWidth() - realExCell.getArea().getWidth();

		AreaPosition aimArea = AreaPosition.getInstance(elseStart, realExCell.getArea().getEnd());
		int moveCount = 0;
		if(isHor) {
			moveCount = moveColCount;
		} else {
			moveCount = moveRowCount;
		}
		// 如果增加了，则增加相应单元个
		if(hideList.size() > 0 && (moveRowCount > 0 || moveColCount > 0)) {
			aimArea = AreaPosition.getInstance(elseStart, newArea.getEnd());
			if(isHor) {
				elseStart =CellPosition.getInstance(realExCell.getArea().getStart().getRow(), realExCell.getArea().getEnd().getColumn() + 1);
				elseEnd = newArea.getEnd();
			} else {
				elseStart = CellPosition.getInstance(realExCell.getArea().getEnd().getRow() + 1, realExCell.getArea().getStart().getColumn());
				elseEnd = newArea.getEnd();
			}

			aimArea = AreaPosition.getInstance(elseStart, elseEnd);
			// 合并单元处理
			ExtendAreaSetUtilHelper.insertCells(ExtendAreaModel.getInstance(formatModel), formatModel, realExCell, aimArea, isHor, moveCount);
		}

		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
//		doAreaBlock(newArea, anaModel, exCell);
		// modify by yuyangi
		doAreaBlock(newArea, anaModel, ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaContentSet.getAreaPk()));
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		// 清除交叉设置
//		newexCell.getAreaInfoSet().setCrossSetInfo(null);
		CellPosition newCrossPoint = crossPoint;
		if(isHor) {
			newCrossPoint = crossPoint.getMoveArea(0, -crossPointMoveRow);
		} else {
			newCrossPoint = crossPoint.getMoveArea(-crossPointMoveRow, 0);
		}
		AreaCrossSetInfo crossAreaInfo = new AreaCrossSetInfo(newArea, newCrossPoint);
		crossAreaInfo.setCrossTableSet(exCell.getAreaInfoSet().getCrossSetInfo().getCrossSet());
		crossAreaInfo.setFixHeader(exCell.getAreaInfoSet().getCrossSetInfo().getFixHeader(true), true);
		crossAreaInfo.setFixHeader(exCell.getAreaInfoSet().getCrossSetInfo().getFixHeader(false), false);
		crossAreaInfo.setShowColHeader(exCell.getAreaInfoSet().getCrossSetInfo().isShowColHeader());
		crossAreaInfo.setShowRowHeader(exCell.getAreaInfoSet().getCrossSetInfo().isShowRowHeader());

		newexCell.getAreaInfoSet().setCrossSetInfo(crossAreaInfo);
		newexCell.setArea(newArea);
		// newexCell.setExModelListener(exCell.getExModelListener());
		if(areaContentSet.getSmartModelDefID() != null && !"".equals(areaContentSet.getSmartModelDefID())) {
			newexCell.getAreaInfoSet().setSmartModelDefID(areaContentSet.getSmartModelDefID());
		}
		// dataModel处理
		if(newexCell.getAreaInfoSet().getDataModel() != null) {
			newexCell.getAreaInfoSet().getDataModel().setExtendAreaCell(newexCell);
		}
		exModel.removeExArea(exModel.getExAreaByPK(areaContentSet.getAreaPk()));// 清除之前扩展区域 add by yuyangi
		// 删除原扩展区中的合并单元 ，避免增加扩展区时产生交叉
		ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(exCell.getArea()));
		exModel.addExArea(newexCell);

		row = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		col = isHor ? formatArea.getWidth() : formatArea.getHeigth();

		hide(formatModel, newexCell, exCell, col, row, hideList, isHor, formatArea, delPosMap, KEY_ORIALFORMULA);
		if(hideList.size() > 0) {// 如果有隐藏列。则按隐藏列的个数清除尾部单元信息
			if(moveRowCount < 0 || moveColCount < 0) {
				anaModel.getFormatModel().clearCells(elseArea);
				ExtendAreaSetUtilHelper.deleteCells(exModel, formatModel, realExCell, aimArea, isHor, moveCount);
			}
		}
		return newexCell;
	}

	static boolean hideSomeFields(String areaPK, AbsAnaReportModel anaModel, boolean showDetail) throws CloneNotSupportedException  {
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);// 合并单元模型
//		clearExCellFromCache(areaPK, formatModel);// 
		cacheCurrExCell(areaPK, formatModel);
		// 获取原始扩展区
		ExtendAreaCell exCell = getExCellFromCache(areaPK, formatModel);
		ExtendAreaCell realExCell = (ExtendAreaCell)ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaPK).clone();
		// 计算新的区域大小，并记录需要删除字段的位置
		boolean isHor = exCell.isRowDirection();
		AreaPosition formatArea = exCell.getArea();
//		AreaPosition formatArea = realExCell.getArea();
		int row = isHor ? formatArea.getHeigth() : formatArea.getWidth();
		int col = isHor ? formatArea.getWidth() : formatArea.getHeigth();
		ArrayList<Integer> hideList = new ArrayList<Integer>();
		HashMap<CellPosition, CellPosition> delPosMap = new HashMap<CellPosition, CellPosition>();
		for (int i = 0; i < col; i++) {
			boolean hasHideField = false;
			boolean hasOtherField = false;
			for (int j = 0; j < row; j++) {
				CellPosition pos = formatArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
				AnaRepField fld = (AnaRepField) exCell.getCellInfoSet().getExtInfo(pos, AnaRepField.EXKEY_FIELDINFO);

				if (fld != null) {
					if (hasField(showDetail, fld)) {
						delPosMap.put(pos, pos);
						hasHideField = true;
//						break;
					} else {
						hasOtherField = true;
					}
				}
			}
			if (hasHideField && !hasOtherField) {
				hideList.add(i);
			}
		}
//		delAggrColFromDelMap(delPosMap, hideList);
		CellPosition newEndPos = formatArea.getEnd().getMoveArea(isHor ? 0 : -hideList.size(),
				isHor ? -hideList.size() : 0);
		AreaPosition newArea = AreaPosition.getInstance(formatArea.getStart(), newEndPos);
		if (newArea == null) {
			return false;
		}
		// add by yuyangi 判断 设置隐藏字段后的扩展区 与 当前实际扩展区 大小增加还是减少
		CellPosition elseEnd = formatArea.getEnd();
		CellPosition elseStart = elseEnd.getMoveArea(isHor ? -row + 1 : -hideList.size() + 1,
				isHor ? -hideList.size() + 1 : -row + 1);
		AreaPosition elseArea = AreaPosition.getInstance(elseStart, elseEnd);

		int moveRowCount = newArea.getHeigth() - realExCell.getArea().getHeigth();
		int moveColCount = newArea.getWidth() - realExCell.getArea().getWidth();

		AreaPosition aimArea = AreaPosition.getInstance(elseStart, realExCell.getArea().getEnd());
		int moveCount = 0;
		if(isHor) {
			moveCount = moveColCount;
		} else {
			moveCount = moveRowCount;
		}
		// 如果增加了，则增加相应单元个
		if(hideList.size() > 0 && (moveRowCount > 0 || moveColCount > 0)) {
			aimArea = AreaPosition.getInstance(elseStart, newArea.getEnd());
			if(isHor) {
				elseStart =CellPosition.getInstance(realExCell.getArea().getStart().getRow(), realExCell.getArea().getEnd().getColumn() + 1);
				elseEnd = newArea.getEnd();
			} else {
				elseStart = CellPosition.getInstance(realExCell.getArea().getEnd().getRow() + 1, realExCell.getArea().getStart().getColumn());
				elseEnd = newArea.getEnd();
			}
			aimArea = AreaPosition.getInstance(elseStart, elseEnd);
			// 增加单元（处理扩展区之外的内容。）
			ExtendAreaSetUtilHelper.insertCells(ExtendAreaModel.getInstance(formatModel), formatModel, realExCell, aimArea, isHor, moveCount);
		}
		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
//		doAreaBlock(newArea, anaModel, exCell);
		// modify by yuyangi
		doAreaBlock(newArea, anaModel, ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaPK));
		// 按新区域大小获得一个新扩展区, 区域的pk和名称必须和原扩展区一致
		ExtendAreaCell newexCell = (ExtendAreaCell) exCell.clone();
		newexCell.getAreaInfoSet().setCrossSetInfo(null);// 清除交叉设置
		newexCell.setArea(newArea);
		newexCell.getAreaInfoSet().setMemoryOrderMng(null);
		newexCell.getAreaInfoSet().setSmartModelDefID(exCell.getAreaInfoSet().getSmartModelDefID());
		newexCell.getAreaInfoSet().setSmartModel(exCell.getAreaInfoSet().getSmartModel());
		// dataModel处理
		if(newexCell.getAreaInfoSet().getDataModel() != null) {
			newexCell.getAreaInfoSet().getDataModel().setExtendAreaCell(newexCell);
		}
		exModel.removeExArea(exModel.getExAreaByPK(areaPK));// 清除之前扩展区域 add by yuyangi
		// 删除原扩展区中的合并单元
		ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(exCell.getArea()));
		exModel.addExArea(newexCell);
//		if(exModel.getRepLevelInfo(KEY_ORIALFORMULA) != null) {// 明细汇总切换时，不使用缓存
//			exModel.removeRepLevelInfo(KEY_ORIALFORMULA);
//		}
//		AreaFormulaModel formula = (AreaFormulaModel)exModel.getRepLevelInfo(KEY_CURR_FORMULA);
//		if(formula != null) {
//			formatModel.putExtProp(AreaFormulaModel.class.getName(), (AreaFormulaModel)formula.clone());
//		}
		// 隐藏  
		hide(formatModel, newexCell, exCell, col, row, hideList, isHor, formatArea, delPosMap, KEY_CURR_FORMULA);
		
		if(delPosMap != null && !showDetail) {
			CellPosition delPos = delPosMap.keySet().iterator().next();
			AreaPosition area = AreaPosition.getInstance(delPos.getRow(), formatArea.getStart().getColumn(), 1, 1);
			hideRow(formatModel, newexCell, col, row, area);
		}
		
		if(hideList.size() > 0) {// 如果有隐藏列。则按隐藏列的个数清除尾部单元信息
			if(moveRowCount < 0 || moveColCount < 0) {
				anaModel.getFormatModel().clearCells(elseArea);
				ExtendAreaSetUtilHelper.deleteCells(exModel, formatModel, realExCell, aimArea, isHor, moveCount);
			}
		}
		// 用户列宽
		LoadUserIndividuation loadUserIndiv = new LoadUserIndividuation(anaModel.getContext(),anaModel);
		loadUserIndiv.loadUserIndiv();
		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(newexCell);
		anaModel.getFormatModel().setDirty(false);
		return true;

	}
	
	/**
	 * 按行和列删除一样单元（该方法只处理了纵向扩展）
	 * @param formatModel
	 * @param newexCell
	 * @param col
	 * @param row
	 * @param formatArea 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void hideRow(CellsModel formatModel, ExtendAreaCell newexCell, int col, int row, AreaPosition formatArea) {
		// 按照区域方向遍历，隐藏字段所在列如果没有其他字段则自动删除
		AreaFormulaModel formulaModel = AreaFormulaModel.getInstance(formatModel);
		Hashtable mainForms = formulaModel.getMainFmls();
		CombinedAreaModel combModel = CombinedAreaModel.getInstance(formatModel);
		for (int i = 0; i < col; i++) {
			CellPosition srcPos = formatArea.getStart().getMoveArea(0, i);
//			AnaRepField srcfld = (AnaRepField) newexCell.getCellInfoSet().getExtInfo(srcPos, AnaRepField.EXKEY_FIELDINFO);
			AnaRepField fld = (AnaRepField) formatModel.getCell(srcPos).getExtFmt(AnaRepField.EXKEY_FIELDINFO);
			if(fld == null) {
				deleteCells(formatModel, srcPos, 2);// DeleteInsertDialog.CELL_MOVE_UP  public final static int CELL_MOVE_UP   = 2;
				moveFormula(formulaModel, srcPos);
			} else {
				CombinedCell comb = getCombCellByPos(formatModel, srcPos);
				if(comb != null) {
					AreaPosition area = comb.getArea();
					AreaPosition newArea = AreaPosition.getInstance(area.getStart(), area.getEnd().getMoveArea(-1, 0));
					CellPosition toDel = area.getStart().getMoveArea(1, 0);
					combModel.removeCombinedCell(comb);
					formatModel.removeArea(comb);
					deleteCells(formatModel, toDel, 2);//DeleteInsertDialog.CELL_MOVE_UP
					moveFormulaVOByPos(formatModel, mainForms, newexCell, formulaModel, toDel, srcPos, false);
					combModel.combineCell(newArea);
				}
			}
		}
		if(formulaModel != null) {
			formulaModel.getPublicFormulaAll().clear();
			Hashtable pubFormulaAll = formulaModel.getPublicFormulaAll();
			Hashtable mainFormulaAll = formulaModel.getMainFmls();
			putAllDynFormulas(mainFormulaAll, pubFormulaAll);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void moveFormula(AreaFormulaModel formulaModel, CellPosition delPos) {
		Hashtable tempmainForms = (Hashtable)formulaModel.getMainFmls().clone();
		Hashtable realFmlMainForms = formulaModel.getMainFmls();
		realFmlMainForms.clear();
		formulaModel.getPublicFormulaAll().clear();
		if(tempmainForms != null) {
			Object[] posObjs =  tempmainForms.keySet().toArray();
			Object formulaVo = null;// 不用强制转换成FormulaVO
			for(Object fmlPosobj : posObjs) {
				AbstractArea fmlPos = (AbstractArea)fmlPosobj;
				if(isUnderDelPos(delPos, fmlPos)) {
					IArea newPos = fmlPos.getMoveArea(-1, 0);
					formulaVo = tempmainForms.get(fmlPos);
//					if(formulaVo != null) {
						realFmlMainForms.put(newPos, formulaVo);
//					}
				} else {
					if(!fmlPos.equals(delPos)) {
						formulaVo = tempmainForms.get(fmlPos);
						realFmlMainForms.put(fmlPos, formulaVo);
					}
				}
			}
		}
	}
	
	/**
	 * 判断给定单元位置pos是否在删除的delPos单元格下
	 * @param delPos
	 * @param pos
	 * @return 
	 */
	static boolean isUnderDelPos(AbstractArea delPos, AbstractArea pos) { 
		boolean flag = false;
		if(pos.getStart().getColumn() != delPos.getStart().getColumn()) {
			return flag;
		}
		if(pos.getStart().getRow() > delPos.getStart().getRow()) {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 删除aimArea单元格
	 * @param cellsModel
	 * @param aimArea
	 * @param deleteType 
	 */
	static void deleteCells(CellsModel cellsModel, CellPosition aimArea, int deleteType) {
		if (aimArea == null)
			return;
		CellPosition newAnchorPos = aimArea.getStart();
		AreaPosition toMoveArea = ExtendAreaSetUtilHelper.getToMoveArea(
				AreaPosition.getInstance(aimArea, aimArea),
				deleteType, cellsModel);
		cellsModel.moveCells(toMoveArea, newAnchorPos);
	}
	
	/**
	 * 判断pos是否在rowno行上
	 * @param rowno
	 * @param pos
	 * @return 
	 */
	static boolean isCellInRow(int rowno,CellPosition pos) {
		boolean flag = false;
		if(pos.getRow() == rowno) {
			flag = true;
		}
		return flag;
	}
	
	static void hideMeasure(CellsModel formatModel, ExtendAreaCell newexCell, ExtendAreaCell exCell, int col, int row, List<Integer> hideList
			, boolean isHor, AreaPosition formatArea, HashMap<CellPosition, CellPosition> delPosMap, String fmlCacheKey) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);// 合并单元模型
		AreaPosition tempArea = null;
		List<CombinedCell> combList = null;
		AreaFormulaModel formula = null;
		Hashtable<IArea, FormulaVO> mainForms = null;
		if(exModel.getRepLevelInfo(fmlCacheKey) != null) {
			formula = (AreaFormulaModel) ((AreaFormulaModel) exModel.getRepLevelInfo(fmlCacheKey))
			.clone();
			mainForms = formula.getMainFmls();
		} else {
			formula = (AreaFormulaModel)AreaFormulaModel.getInstance(formatModel).clone();
			mainForms = formula.getMainFmls();
		}

		int rowEnd = newexCell.getAreaInfoSet().getCrossSetInfo().getCrossColArea().getHeigth();
		int colEnd = newexCell.getAreaInfoSet().getCrossSetInfo().getCrossRowArea().getWidth();

		// 按照区域方向遍历，隐藏字段所在列如果没有其他字段则自动删除
		int hideIndex = 0;
		for (int i = colEnd; i < col; i++) {
			if (hideList.contains(i-colEnd)) {
				if(isHor) {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(0, i-hideIndex), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, 0, -1);
				} else {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(i, 0), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, -1, 0);
				}
				// 处理合并单元
				// 删除原扩展区中的合并单元
				ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(tempArea));
				ExtendAreaSetUtilHelper.combineArea(formatModel, combList);
				hideIndex++;
				continue;
			} else {
 				if(isHor) {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(0, i), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, 0, 0);
				} else {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(i, 0), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, 0, 0);
				}
			}
			CellPosition aimStart = formatArea.getStart().getMoveArea(isHor ? 0 : i - hideIndex,
					isHor ? i - hideIndex : 0);
			for (int j = 0; j < row; j++) {
				// 如果目标位置没有字段。则将单元的值赋值给目标位置
				if(j < rowEnd) {
					CellPosition srcPos = formatArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
					CellPosition aimPos = aimStart.getMoveArea(isHor ? j : 0, isHor ? 0 : j);
					if(exCell.getCellInfoSet().getExtInfo(aimPos, AnaRepField.EXKEY_FIELDINFO) == null) {
						if(exCell.getCellInfoSet().hasExtInfo(srcPos)) {
							exModel.fillCellInfo(exCell, srcPos, aimPos);
						}
						else {// 如果下一个单元上没有字段，则赋值给目标单元 add by yuyangi
//							if(exCell.getCellInfoSet().hasExtInfo(aimPos)) {
								TableUtilities.setCell(formatModel, aimPos.getRow(), aimPos.getColumn(), formatModel.getCell(srcPos));
//							}
						}
						if (delPosMap.containsKey(srcPos)) {
							formatModel.getCell(aimPos).removeExtFmt(AnaRepField.EXKEY_FIELDINFO);
						} else if(delPosMap.containsKey(aimPos)) {
							mainForms.remove(aimPos);
						}
						// 额外处理公式内容
						moveFormulaVOByPos(formatModel, mainForms, exCell, formula, srcPos, aimPos, true);
//						if (formulaVO != null && aimPos != srcPos) {
//							mainForms.put(aimPos, formulaVO);
//							mainForms.remove(srcPos);
//						}
					}
				} else {
					CellPosition srcPos = formatArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
					CellPosition aimPos = aimStart.getMoveArea(isHor ? j : 0, isHor ? 0 : j);
					if(exCell.getCellInfoSet().hasExtInfo(srcPos)) {
						exModel.fillCellInfo(exCell, srcPos, aimPos);
					}
					else {// 如果下一个单元上没有字段，则赋值给目标单元 add by yuyangi
//						if(exCell.getCellInfoSet().hasExtInfo(aimPos)) {
							TableUtilities.setCell(formatModel, aimPos.getRow(), aimPos.getColumn(), formatModel.getCell(srcPos));
//						}
					}
					if (delPosMap.containsKey(srcPos)) {
						formatModel.getCell(aimPos).removeExtFmt(AnaRepField.EXKEY_FIELDINFO);
					} else if(delPosMap.containsKey(aimPos)) {
						mainForms.remove(aimPos);
					}
					// 额外处理公式内容
					moveFormulaVOByPos(formatModel, mainForms, exCell, formula, srcPos, aimPos, true);
//					if (formulaVO != null && aimPos != srcPos) {
//						mainForms.put(aimPos, formulaVO);
//						mainForms.remove(srcPos);
//					}
				}
			}
			// 处理合并单元
			// 删除原扩展区中的合并单元
			ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(tempArea));
			ExtendAreaSetUtilHelper.combineArea(formatModel, combList);
		}

		formatModel.putExtProp(AreaFormulaModel.class.getName(), formula);
	}

	@SuppressWarnings("rawtypes")
	static void hide(CellsModel formatModel, ExtendAreaCell newexCell, ExtendAreaCell exCell, int col, int row, List<Integer> hideList
			, boolean isHor, AreaPosition formatArea, HashMap<CellPosition, CellPosition> delPosMap, String originFmlKey) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		CombinedAreaModel cmodel = CombinedAreaModel.getInstance(formatModel);// 合并单元模型
		AreaPosition tempArea = null;
		List<CombinedCell> combList = null;
		AreaFormulaModel formula = null;
		Hashtable<IArea, FormulaVO> mainForms = null;
//		Hashtable<CellPosition, CellPosition> cellCache = new <>
		if(exModel.getRepLevelInfo(originFmlKey) != null) {
			formula = (AreaFormulaModel) ((AreaFormulaModel) exModel.getRepLevelInfo(originFmlKey)).clone();
			mainForms = formula.getMainFmls();
		} else {
			formula = (AreaFormulaModel)AreaFormulaModel.getInstance(formatModel).clone();
			mainForms = formula.getMainFmls();
		}
		resetHiddenCol(formatModel);// 恢复隐藏列
		// 按照区域方向遍历，隐藏字段所在列如果没有其他字段则自动删除
		int hideIndex = 0;
		for (int i = 0; i < col; i++) {
			if (hideList.contains(i)) {
				if(isHor) {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(0, i-hideIndex), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, 0, -1);
				} else {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(i, 0), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, -1, 0);
				}
				// 处理合并单元
				// 删除原扩展区中的合并单元
				ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(tempArea));
				ExtendAreaSetUtilHelper.combineArea(formatModel, combList);
				hideIndex++;
				continue;
			} else {
 				if(isHor) {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(0, i), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, 0, 0);
				} else {
					tempArea = AreaPosition.getInstance(formatArea.getStart().getMoveArea(i, 0), formatArea.getEnd());
					combList = ExtendAreaSetUtilHelper.getNewCombineArea(newexCell, formatModel, tempArea, 0, 0);
				}
				// 处理合并单元
				// 删除原扩展区中的合并单元
				ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(tempArea));
				ExtendAreaSetUtilHelper.combineArea(formatModel, combList);
			}
			CellPosition aimStart = formatArea.getStart().getMoveArea(isHor ? 0 : i - hideIndex,
					isHor ? i - hideIndex : 0);
			for (int j = 0; j < row; j++) {
				CellPosition srcPos = formatArea.getStart().getMoveArea(isHor ? j : i, isHor ? i : j);
				CellPosition aimPos = aimStart.getMoveArea(isHor ? j : 0, isHor ? 0 : j);

				if(exCell.getCellInfoSet().hasExtInfo(srcPos) || exCell.getCellInfoSet().hasExtInfo(aimPos)) {
					IFormat format = formatModel.getCellFormat(aimPos);
					boolean iscover = true;
					if(format instanceof CommonStyleFormat) {
						CommonStyleFormat comFormat = (CommonStyleFormat)format;
						String titleType1 = CommonStyleUtil.COMMON_FORMAT_TITLE;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TITLE);
						String titleType2 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD);
						String titleType3 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT);
						if (comFormat.getStyleType().equals(titleType1) ||
						    comFormat.getStyleType().equals(titleType2) ||
						    comFormat.getStyleType().equals(titleType3)) {
							if(!srcPos.equals(aimPos)) {
								iscover = false;
							}
						}
					}
					if(iscover) {
						Map<String, Object> srccellInfo = exCell.getCellInfoSet().getExtInfo(srcPos);
						Map<String, Object> aimcellInfo = exCell.getCellInfoSet().getExtInfo(aimPos);
						Object aimValue = null;
						String aimResid = null;
						if(aimcellInfo != null) {
							aimValue = aimcellInfo.get(ExtendAreaConstants.CELL_VALUE);
							aimResid = (String)aimcellInfo.get(FreePrivateContextKey.KEY_MULTILANGID);
						}
						Object srcValue = null;
						if(srccellInfo != null) {
							srcValue = srccellInfo.get(ExtendAreaConstants.CELL_VALUE);
						}
						exModel.fillCellInfo(exCell, srcPos, aimPos);
						// 如原单元格上无值，单目标单元上有值，则保留目标单元的值
						if(aimValue != null && srcValue == null && getHideConstValues().contains(aimValue)) {
							formatModel.setCellValue(aimPos, aimValue);
							formatModel.setBsFormat(aimPos, FreePrivateContextKey.KEY_MULTILANGID, aimResid);
						} else if (srcValue == null){
							formatModel.setCellValue(aimPos, null);
						}
					}
				} else {// 如果下一个单元上没有字段，则赋值给目标单元 add by yuyangi
//					if(formatModel.getCellValue(aimPos) == null || "".equals(formatModel.getCellValue(aimPos))) {
						TableUtilities.setCell(formatModel, aimPos.getRow(), aimPos.getColumn(), formatModel.getCell(srcPos));
//					}
				}
				if (delPosMap.containsKey(srcPos)) {
					formatModel.getCell(aimPos).removeExtFmt(AnaRepField.EXKEY_FIELDINFO);
				} else if(delPosMap.containsKey(aimPos)) {
					mainForms.remove(aimPos);
				}
				// 额外处理公式内容
				if(formula != null) {
					moveFormulaVOByPos(formatModel, mainForms, exCell, formula, srcPos, aimPos, true);
				}
//				if(delPosMap.containsKey(aimPos)) {// 移动隐藏列
//					doColHiddenMove(formatModel, aimPos.getColumn()-hideIndex);
					doColHiddenMove(formatModel, srcPos.getColumn(), aimPos.getColumn());
//				}
			}
			
			// @edit by ll at 2011-7-24,下午12:44:49 下面的两句重复了，应该不需要
			// 处理合并单元
			// 删除原扩展区中的合并单元
//			ExtendAreaSetUtilHelper.removeCombinedCell(formatModel, cmodel.getCombineCells(tempArea));
//			ExtendAreaSetUtilHelper.combineArea(formatModel, combList);
		}
		if(formula != null) {
			formula.getPublicFormulaAll().clear();
			Hashtable pubFormulaAll = formula.getPublicFormulaAll();
			Hashtable mainFormulaAll = formula.getMainFmls();
			putAllDynFormulas(mainFormulaAll, pubFormulaAll);
			formatModel.putExtProp(AreaFormulaModel.class.getName(), formula);
		}
	}
	
	/**
	 * <p>隐藏字段中的不变量。当隐藏列的中文在下列值中，则隐藏列但在原位置保留值</p>
	 * @return 
	 */
	static List<String> getHideConstValues() {
		String total = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0886")/*@res "合计"*/;
		String count = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0882")/*@res "小计"*/;
		List<String> list = new ArrayList<String>();
		list.add(total);
		list.add(count);
		return list;
	}
	
	/**
	 * 处理列头隐藏（列头宽度为0）
	 * @param cellsModel
	 * @param srcCol 
	 */
	private static void doColHiddenMove(CellsModel cellsModel,int srcCol, int aimCol) {
		HeaderModel headerModel = cellsModel.getColumnHeaderModel();
		List<Integer> cols = getCacheCol(cellsModel);
		headerModel.setSize(aimCol, cols.get(srcCol));
//		for(int i = 0;i < headerModel.getCount();i++) {
//			if(headerModel.getSize(i) == 0 && i > srcCol) {
//				headerModel.setSize(i, TableStyle.COLUMN_WIDTH);
//				if(i > 0) {
//					headerModel.setSize(i-1, TableStyle.MINHEADER);
//				}
//			} else if(headerModel.getSize(i) > 0 && i + 1 < headerModel.getCount() && i > srcCol) {
//				headerModel.setSize(i, headerModel.getSize(i + 1));
//			}
//		}
	}
	
	@SuppressWarnings("unchecked")
	private static List<Integer> getCacheCol(CellsModel cellsModel) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(cellsModel);
		List<Integer> hidCols = (List<Integer>)exModel.getRepLevelInfo(KEY_ORIGIN_COL);
		if(hidCols == null) {
			HeaderModel headerModel = cellsModel.getColumnHeaderModel();
			hidCols = new ArrayList<Integer>();
			for(int i = 0;i < headerModel.getCount();i++) {
				hidCols.add(headerModel.getSize(i));
			}
			exModel.addRepLevelInfo(KEY_ORIAL_HIDECOL, hidCols);
		}
		return hidCols;
	}
	
	private static void resetHiddenCol(CellsModel cellsModel) {
		List<Integer> hidCols = getCacheHiddenCol(cellsModel);
		List<Integer> cols = getCacheCol(cellsModel);
		int width = getCacheWidth(cellsModel);
//		if(hidCols != null && !hidCols.isEmpty()) {
			HeaderModel headerModel = cellsModel.getColumnHeaderModel();
			for(int i = 0;i < width;i++) {
				if(hidCols.contains(i)) {
					headerModel.setSize(i, TableStyle.MINHEADER);
				} else {
					if(cols.contains(i)) {
						headerModel.setSize(i, cols.get(i));
					}
				}
			}
			headerModel.resetSizeCache();
//		}
	}
	
	private static int getCacheWidth(CellsModel cellsModel) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(cellsModel);
		Integer hidCols = (Integer)exModel.getRepLevelInfo(KEY_ORIAL_WIDTH);
		if(hidCols == null) {
			HeaderModel headerModel = cellsModel.getColumnHeaderModel();
			hidCols = headerModel.getCount();
			exModel.addRepLevelInfo(KEY_ORIAL_WIDTH,hidCols);
		}
		return hidCols;
	}
	
	/**
	 * <p>缓存中的隐藏列</p>
	 * @param cellsModel
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private static List<Integer> getCacheHiddenCol(CellsModel cellsModel) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(cellsModel);
		List<Integer> hidCols = (List<Integer>)exModel.getRepLevelInfo(KEY_ORIAL_HIDECOL);
		List<Integer> cols = (List<Integer>)exModel.getRepLevelInfo(KEY_ORIGIN_COL);
		HeaderModel headerModel = cellsModel.getColumnHeaderModel();
		if(hidCols == null || cols == null) {
			hidCols = new ArrayList<Integer>();
			cols = new ArrayList<Integer>();
			for(int i = 0;i < headerModel.getCount();i++) {
				if(headerModel.getSize(i) == 0) {
					hidCols.add(i);
				}
				cols.add(headerModel.getSize(i));
			}
			exModel.addRepLevelInfo(KEY_ORIAL_HIDECOL, hidCols);
			exModel.addRepLevelInfo(KEY_ORIGIN_COL, cols);
		}
		return hidCols;
	}
	
	/**
     * 把from中的所有动态区对应的(IArea-FormulaVO),添加到to中.
     * @param from
     * @param to
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static void putAllDynFormulas(Hashtable from, Hashtable to){
    	if(from == null){
    		return;
    	}
    	
    	FormulaVO fml = null;
    	IArea area = null;
    	Hashtable hashFmlSrc = from;
    	Hashtable hashFmlDest = to;
    	for(Enumeration enumFml = hashFmlSrc.keys();enumFml.hasMoreElements();){
    		area = (IArea) enumFml.nextElement();
    		fml = (FormulaVO) hashFmlSrc.get(area);
    		if(fml.getContent()==null || fml.getContent().length()==0){
    			hashFmlDest.remove(area);
    			continue;
    		}
    		hashFmlDest.put(area, fml);
    	}    	
    }   

	@SuppressWarnings("unchecked")
	static FormulaVO moveFormulaVOByPos(CellsModel cellsModel, Hashtable<IArea, FormulaVO> mainForms, ExtendAreaCell exCell, 
			AreaFormulaModel formula, CellPosition srcPos, CellPosition aimPos, boolean isDelAimsFml) {
		FormulaVO formulaVO = null;
		IArea fmlArea = srcPos;
		formulaVO = formula.getDirectFml(srcPos);
		if(formulaVO == null) {
			fmlArea = getCombCellByPos(exCell, srcPos);
			formulaVO = formula.getDirectFml(fmlArea);
		}
		IArea aimArea = getCombAreaCellByPos(cellsModel, aimPos);
		if (formulaVO != null && !aimArea.equals(fmlArea)) {
			boolean isremove = true;
			IFormat format = cellsModel.getCellFormat(aimPos);
			if(format instanceof CommonStyleFormat) {
				CommonStyleFormat comFormat = (CommonStyleFormat)format;
				String titleType1 = CommonStyleUtil.COMMON_FORMAT_TITLE;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TITLE);
				String titleType2 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD);
				String titleType3 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT);
				if (comFormat.getStyleType().equals(titleType1) ||
				    comFormat.getStyleType().equals(titleType2) ||
				    comFormat.getStyleType().equals(titleType3)) {
					isremove = false;
				}
			}
			if(isremove) {
				mainForms.remove(srcPos);
				removeFmls(mainForms, fmlArea);
				mainForms.put(aimArea, formulaVO);
				mainForms.put(aimPos, formulaVO);
			}
		} else if(!aimArea.equals(fmlArea)) {
			boolean isremove = true;
			IFormat format = cellsModel.getCellFormat(aimPos);
			if(format instanceof CommonStyleFormat) {
				CommonStyleFormat comFormat = (CommonStyleFormat)format;
				String titleType1 = CommonStyleUtil.COMMON_FORMAT_TITLE;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TITLE);
				String titleType2 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD);
				String titleType3 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT);
				if (comFormat.getStyleType().equals(titleType1) ||
				    comFormat.getStyleType().equals(titleType2) ||
				    comFormat.getStyleType().equals(titleType3)) {
					isremove = false;
				}
			}
			if(isremove) {
				mainForms.remove(aimPos);
				removeFmls(mainForms, aimArea);
				formula.getMainFmls().remove(aimPos);
				removeFmls(formula.getMainFmls(), aimArea);
				formula.getPublicFormulaAll().remove(aimPos);
				removeFmls(formula.getPublicFormulaAll(), aimArea);
			}
		}
		return formulaVO;
	}
	
	static void removeFmls(Map<IArea, FormulaVO> map, IArea area) {
		for(Iterator<Map.Entry<IArea, FormulaVO>> it = map.entrySet().iterator();it.hasNext();) {
			Map.Entry<IArea, FormulaVO> entry = it.next();
			if(entry.getKey().contain(area)) {
				it.remove();
			}
		}
	}
	
	static IArea getCombCellByPos(ExtendAreaCell exCell, CellPosition pos) {
		IArea ret = pos;
//		CombinedAreaModel combAreaModel = CombinedAreaModel.getInstance(cellsModel);
//		CombinedCell[] allCombCells = combAreaModel.getCombineCells();
		List<CombinedCell> allCombCells = exCell.getAreaInfoSet().getAllCombinedCell();
		for(CombinedCell combCell : allCombCells) {
			IArea combArea = combCell.getArea();
			if(combArea.contain(pos)) {
				ret = combArea;
				break;
			}
		}
		return ret;
	}

	static IArea getCombAreaCellByPos(CellsModel cellsModel, CellPosition pos) {
		IArea ret = pos;
		CombinedAreaModel combAreaModel = CombinedAreaModel.getInstance(cellsModel);
		CombinedCell[] allCombCells = combAreaModel.getCombineCells();
		for(CombinedCell combCell : allCombCells) {
			IArea combArea = combCell.getArea();
			if(combArea.contain(pos)) {
				ret = combArea;
				break;
			}
		}
		return ret;
	}
	
	static CombinedCell getCombCellByPos(CellsModel cellsModel, CellPosition pos) {
		CombinedCell ret = null;
		CombinedAreaModel combAreaModel = CombinedAreaModel.getInstance(cellsModel);
		CombinedCell[] allCombCells = combAreaModel.getCombineCells();
		for(CombinedCell combCell : allCombCells) {
			IArea combArea = combCell.getArea();
			if(combArea.contain(pos)) {
				ret = combCell;
				break;
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	static void clearExtFormulars(IArea area, CellsModel cellsModel) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(cellsModel);
		AreaFormulaModel formula = null;
		Hashtable<IArea, FormulaVO> mainForms = null;
//		Hashtable<CellPosition, CellPosition> cellCache = new <>
		Hashtable<IArea, FormulaVO> publicForms = null;
		if(exModel.getRepLevelInfo(KEY_ORIALFORMULA) != null) {
			formula = (AreaFormulaModel)cellsModel.getExtProp(AreaFormulaModel.class.getName());
			mainForms = formula.getMainFmls();
			publicForms = formula.getPublicFormulaAll();
		} else {
			formula = (AreaFormulaModel)AreaFormulaModel.getInstance(cellsModel);
			mainForms = formula.getMainFmls();
			publicForms = formula.getPublicFormulaAll();
		}
		if(mainForms != null) {
			IArea[] keys = mainForms.keySet().toArray(new IArea[0]);
			for(IArea a : keys) {
				if(area.contain(a)) {
					mainForms.remove(a);
					if(publicForms != null) {
						publicForms.remove(a);
					}
				}
			}
		}
		if(formula != null) {
			cellsModel.putExtProp(AreaFormulaModel.class.getName(), formula);
		}
	}

	static boolean hasPos(CellPosition[] cellsToHide, CellPosition cp) {
		for(CellPosition c : cellsToHide) {
			if(c.equals(cp)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasField(AreaContentSet areaContentSet, AnaRepField fld) {
		if (fld.isAggrFld() || fld.getFieldType() == AnaRepField.Type_CROSS_SUBTOTAL) {
			FieldCountDef def = fld.getFieldCountDef();
			if (def != null) {
				String fname = def.getMainFldName();
				String rangeName = def.getRangeFldName();
				return hasStrField(areaContentSet, fname) && isSameRangeField(areaContentSet, rangeName);
			}
		}
		Field field = fld.getField();
		return hasStrField(areaContentSet, field.getExpression());
	}

	private static boolean isSameRangeField(AreaContentSet areaContentSet, String rangeName) {
		AreaFieldSet[] fields = areaContentSet.getDetailFldNames();

		if (fields == null || fields.length == 0)
			return false;
		for (int i = 0; i < fields.length; i++) {
			if(fields[i] != null && fields[i].getField() != null) {
				Field fld = fields[i].getField();
				if(fld instanceof FieldCountDef) {
					FieldCountDef countDef = ((FieldCountDef)fld);
					if(countDef.getRangeField() != null) {
						if(countDef.getRangeField().getExpression().equalsIgnoreCase(rangeName)) {
							return true;
						}
					}
					if(countDef.getRangeFldName() != null) {
						if(countDef.getRangeFldName().equalsIgnoreCase(rangeName)) {
							return true;
						}
					}
					if(countDef.getRangeField() == null && StringUtils.isEmpty(countDef.getRangeFldName())
							&& StringUtils.isEmpty(rangeName)) {
						return true;
					}
				}
			} //else
		}
		return false;
	}

	private static boolean hasField(boolean showDetail, AnaRepField fld) {
		if (showDetail) {// 细节显示全部字段
//			return fld.isAggrFld();
			return false;
		} else {
			return !fld.isAggrFld() && (fld.getFieldType() != AnaRepField.TYPE_GROUP_FIELD);
		}
	}

	private static boolean hasStrField(AreaContentSet areaContentSet, String fldName) {
		if (areaContentSet == null)
			return false;
		AreaFieldSet[] fields = areaContentSet.getDetailFldNames();
		if (hasStrField(fields, fldName))
			return true;

		fields = areaContentSet.getAreaFieldSet();
		if (hasStrField(fields, fldName))
			return true;
		AreaFieldSet[][] grpFields = areaContentSet.getGroupFldNames();
		if (grpFields != null) {
			for (int i = 0; i < grpFields.length; i++) {
				fields = grpFields[i];
				if (hasStrField(fields, fldName))
					return true;
			}
		}
		return false;
	}

	private static boolean hasStrField(AreaFieldSet[] fields, String fldName) {
		if (fields == null || fields.length == 0)
			return false;
		String tempFldName = null;
		for (int i = 0; i < fields.length; i++) {
			if(fields[i] != null && fields[i].getField() != null) {
				Field fld = fields[i].getField();
				if(fld instanceof FieldCountDef) {
					fld = ((FieldCountDef)fld).getMainField();
				}
				tempFldName = fld.getFldname();
				if (tempFldName != null && tempFldName.equalsIgnoreCase(fldName))
					return true;
			} //else
			if(fields[i] != null) {
				tempFldName = fields[i].getFieldName();
			}
			if (tempFldName != null && tempFldName.equalsIgnoreCase(fldName))
				return true;
		}
		return false;
	}

	/**
	 * 从明细字段中获得数值型字段
	 *
	 * @create by wanyonga at 2010-7-27,下午02:43:44
	 *
	 * @param smartModel
	 * @param fieldNames
	 * @return
	 */
	private static List<AreaFieldSet> getNumFiledList(SmartModel smartModel, AreaFieldSet[] fieldNames, AreaFieldSet[] uncountFields) {
		List<AreaFieldSet> numFieldList = new ArrayList<AreaFieldSet>();
		Field field = null;
		for (AreaFieldSet fldName : fieldNames) {
			AreaFieldSet numFld = null;
			int countType = fldName.getCountType();
			if (countType != IFldCountType.NONE) {// 已经设置了统计类型的
				if (countType == IFldCountType.TYPE_AVAGE || countType == IFldCountType.TYPE_COUNT
						|| countType == IFldCountType.TYPE_COUNT_DISTINCT || countType == IFldCountType.TYPE_SUM)
					numFld = fldName;
			}
			if (numFld == null) {// 未能明确统计类型的，根据原始字段类型进行判断
				field = SumByField.getFieldFromAreaSet(smartModel, fldName);
				if (field != null && DataTypeConstant.isNumberType(field.getDataType())) {
					numFld = fldName;
				}
			}
			if (numFld != null && !isUncountField(uncountFields, fldName))
				numFieldList.add(fldName);
		}
		return numFieldList.size() == 0 ? null : numFieldList;
	}

	/**
	 * 是否非统计字段。
	 * @param uncountFields
	 * @param fldName
	 * @return 
	 */
	private static boolean isUncountField(AreaFieldSet[] uncountFields, AreaFieldSet fldName) {
		if(uncountFields == null) {
			return false;
		} else {
			return ArrayUtils.contains(uncountFields, fldName);
		}
	}
	
	/**
	 * 增加报表标题
	 *
	 * @create by yuyangi 2011-4-18
	 *
	 * @param areaContentSet
	 * @param start
	 * @param formatModel
	 * @param titleColCount
	 * @return
	 */
	private static CellPosition addRepTitle(AreaContentSet areaContentSet, CellPosition start, CellsModel formatModel, int titleColCount) {
		if(areaContentSet.getTilteHeight() > 0 && areaContentSet.getTitle() != null && !"".equals(areaContentSet.getTitle())) {
			CellPosition end = start.getMoveArea(areaContentSet.getTilteHeight() - 1, titleColCount - 1);
			AreaPosition titleArea = AreaPosition.getInstance(start, end);
			formatModel.combineCell(titleArea);
			formatModel.getCombinedAreaModel().setCombinedValue(titleArea, areaContentSet.getTitle());
			formatModel.setCellValue(titleArea.getStart(), areaContentSet.getTitle());
			if(areaContentSet.getTitleFormat() != null) {
				formatModel.setCellFormat(titleArea.getStart().getRow(), titleArea.getStart().getColumn(), areaContentSet.getTitleFormat());
			}
			start = start.getMoveArea(areaContentSet.getTilteHeight(), 0);
		}
		return start;
	}

	/**
	 * 增加报表标题
	 *
	 * @create by yuyangi 2011-4-18
	 *
	 * @param areaContentSet
	 * @param start
	 * @param formatModel
	 * @param titleColCount
	 * @return
	 */
	private static int addCrossRepTitle(CrossAreaContentSet crossAreaContentSet, AreaPosition newArea, CellsModel formatModel, int titleColCount) {
		CellPosition start = newArea.getStart();
		if(crossAreaContentSet.getTilteHeight() > 0 && crossAreaContentSet.getTitle() != null && !"".equals(crossAreaContentSet.getTitle())) {
			CellPosition end = start.getMoveArea(crossAreaContentSet.getTilteHeight() - 1, titleColCount - 1);
			AreaPosition titleArea = AreaPosition.getInstance(start, end);
			formatModel.combineCell(titleArea);
			formatModel.getCombinedAreaModel().setCombinedValue(titleArea, crossAreaContentSet.getTitle());
			formatModel.setCellValue(titleArea.getStart(), crossAreaContentSet.getTitle());
			if(crossAreaContentSet.getTitleFormat() != null) {
				formatModel.setCellFormat(titleArea.getStart().getRow(), titleArea.getStart().getColumn(), crossAreaContentSet.getTitleFormat());
			}
			start = start.getMoveArea(crossAreaContentSet.getTilteHeight(), 0);
		}
		return crossAreaContentSet.getTilteHeight();
	}

	/**
	 * 添加标题，明细字段和分组字段需要显示标题
	 *
	 * @create by wanyonga at 2010-6-22,下午03:06:54
	 *
	 */
	private static int addFieldTitle(AreaContentSet areaContentSet, int col,int moveStep, int detailCount, int levelGroupCount,
			CellPosition start, AbsAnaReportModel anaModel, SmartModel smartModel, int titleRowCount, int titleColCount,
			CommonKeyMap<String, AreaFieldSet> aggrMap, Map<String, String[]> titles) {
		CellsModel formatModel = anaModel.getFormatModel();
		CellPosition temp = null;
		Field field = null;
		int movelen = 0;
//		if (col < detailCount + levelGroupCount) {
		if (col < titleColCount) {
			AreaFieldSet fieldSet = null;
			if (col < levelGroupCount) {
				if(areaContentSet.getGroupFldNames()[col] != null && areaContentSet.getGroupFldNames()[col].length > 0) {
					fieldSet = areaContentSet.getGroupFldNames()[col][0];
				}
			} else if (col - levelGroupCount < detailCount) {
				fieldSet = areaContentSet.getDetailFldNames()[col - levelGroupCount];
			} else {
				if(aggrMap != null) {
					fieldSet = (AreaFieldSet)aggrMap.getNextValue();
				}
			}
			if (fieldSet == null)
				return 0;
			movelen = fieldSet.getColWidth() - 1;
			// 起始位置
			temp = start.getMoveArea(0, col + moveStep);
			
			IFormat titleFormat = getCommonFormat(temp, formatModel, AreaContentSet.TITLE_FORMAT);
			formatModel.getCellIfNullNew(temp.getRow(), temp.getColumn());
			formatModel.setCellProperty(temp, PropertyType.DataType, TableConstant.CELLTYPE_SAMPLE);
			
			// 先看AreaFieldSet中是否设置了标题
			String[] title = fieldSet.getTitles();
			if (title == null) {
				//field = SumByField.getFieldFromAreaSet(smartModel, fieldSet);
				field = SumByField.getCombineFldFromAreaSet(smartModel, fieldSet);
				if (field != null) {
//					String pk_func = (String) anaModel.getContext().getAttribute(
//							FreeReportContextKey.REPORT_FUNCID);
					Object pk_func = anaModel.getContext().getAttribute(FreeReportFucletContextKey.PUBLISH_REP_TYPE_KEY);
					// 如果当前是在发布态节点中，则使用以前的标题。
					if(pk_func != null){
						title = titles.get(field.getExpression());
					}
					// 格式态/发布节点中新增字段,重新生成标题
					if(title == null){
						//新区域设置 -- 增加增加组合字段情况  add by zhujhc
						if(field instanceof CombineField && ((CombineField) field).getShowField()!= null){
							field = ((CombineField) field).getShowField();
						}
						// 如果原先区域标题为空，则新区域也不添加标题
						if(titleRowCount > 0){
							title = new String[titleRowCount];
							title[0] = field.getCaption();
						}
						for(int i = 1;i < titleRowCount;i++) {
							title[i] = NULLVALUE + i;
						}
					}
				}
			}
			if (title == null || title.length == 0)
				return 0;
			if (titleRowCount == 1) {
				if(title[0] != null && title[0].startsWith(KEY_FORMULA_TITLE)) {
//					int index = title[0].indexOf(KEY_FORMULA_TITLE);
					FormulaVO formula = new FormulaVO(temp.toString(), title[0].substring(KEY_FORMULA_TITLE.length()));
					anaModel.getAreaFormulaModel().setMainFmlVO(temp, formula);
				} else {
					formatModel.setCellValue(temp, title[0]);
				}
				if(fieldSet.getColWidth() > 1) {
					formatModel.combineCell(AreaPosition.getInstance(temp, temp.getMoveArea(0, fieldSet.getColWidth()- 1)));
				}
				formatModel.setCellFormat(temp.getRow(), temp.getColumn(), titleFormat);
			} else {
				if(title.length ==1){
					if(title[0] != null && title[0].startsWith(KEY_FORMULA_TITLE)) {
//						int index = title[0].indexOf(KEY_FORMULA_TITLE);
						FormulaVO formula = new FormulaVO(temp.toString(), title[0].substring(KEY_FORMULA_TITLE.length()));
						anaModel.getAreaFormulaModel().setMainFmlVO(temp, formula);
					} else {
						formatModel.setCellValue(temp, title[0]);
					}
					setCombinedArea(AreaPosition.getInstance(temp, temp.getMoveArea(titleRowCount-1, 0)), titleFormat, title[0], formatModel);
				}
				CellPosition colstartPos = temp;
				CellPosition colendPos = temp;
				CellPosition rowstartPos = temp;
				CellPosition rowendPos = temp;
				for (int i = 0; i < Math.max(titleRowCount, title.length); i++) {
					String currValue = i < title.length ? title[title.length - i - 1] : null;
					colstartPos = temp;
					if(i > 0) {
						String colpreValue = title[title.length - i];
						temp = temp.getMoveArea(1, 0);
						if(!isNULLVALUE(currValue) && isNULLVALUE(colpreValue)) {
							title[title.length - i] = currValue;
							colpreValue = currValue;
						} else if(isNULLVALUE(currValue) && isNULLVALUE(colpreValue)) {
//							colpreValue = currValue = "";
						}
						if(isNotNullEquals(colpreValue, currValue)) {
							colendPos = temp;
							formatModel.combineCell(AreaPosition.getInstance(colstartPos, colendPos));
						}
					}
					
					CellPosition rowPrePos = temp.getMoveArea(0, -1);
					Object rowpreValue = null;
					if(rowPrePos != null) {
						rowpreValue = formatModel.getCellValue(rowPrePos);
						rowendPos = temp;
						rowstartPos = rowPrePos;
						if(isNULLVALUE(rowpreValue) && !isNULLVALUE(currValue)) {
//							title[title.length - i - 1] = currValue;
							rowpreValue = currValue;
						}
						if((isNotNullEquals(rowpreValue, currValue)) /*|| isNULLVALUE(rowpreValue)*/) {
							AreaPosition prePosCombArea = formatModel.getCombinedCellArea(rowstartPos);
							AreaPosition currPosCombArea = formatModel.getCombinedCellArea(rowendPos);
							if(prePosCombArea != null && !prePosCombArea.isCell()) {
								rowendPos = CellPosition.getInstance(prePosCombArea.getEnd().getRow(), temp.getColumn());
								rowstartPos = prePosCombArea.getStart();
//								if(combArea.contain(rowendPos) || combArea.contain(rowstartPos)) {
									
//								}
							}
							if(currPosCombArea != null && !currPosCombArea.isCell()) {
								rowendPos = currPosCombArea.getEnd();
								rowstartPos = CellPosition.getInstance(currPosCombArea.getStart().getRow(), rowstartPos.getColumn());
							}
							CombinedAreaModel.getInstance(formatModel).removeCombinedCell(prePosCombArea);
							CombinedAreaModel.getInstance(formatModel).removeCombinedCell(currPosCombArea);
							formatModel.combineCell(AreaPosition.getInstance(rowstartPos, rowendPos));
						}
					}
					if(i < title.length && title[title.length - i - 1] != null && title[title.length - i - 1].startsWith(KEY_FORMULA_TITLE)) {
						FormulaVO formula = new FormulaVO(temp.toString(), title[title.length - i - 1].substring(KEY_FORMULA_TITLE.length()));
						anaModel.getAreaFormulaModel().setMainFmlVO(temp, formula);
					} else {
						CellPosition pos = formatModel.getModifiedStartCell(temp);
						formatModel.setCellValue(pos, i < title.length ? title[title.length - i - 1] : null);
					}
//					formatModel.setBsFormat(temp, AnaCellCombine.KEY, new AnaCellCombine("title"+i));
					IFormat format = formatModel.getCellFormat(temp);
					// 如果原始格式上设置了常用样式，则使用原始格式的常用样式
					ExtendAreaCell exCell = getSetExCell(areaContentSet.getAreaPk(), formatModel);
					format = (IFormat)exCell.getCellInfoSet().getExtInfo(temp, ExtendAreaConstants.CELL_FORMAT);
					if(format instanceof CommonStyleFormat) {
//						titleFormat = format;
						formatModel.setCellFormat(temp.getRow(), temp.getColumn(), format);
					} else {
						formatModel.setCellFormat(temp.getRow(), temp.getColumn(), titleFormat);
					}
					// 如果标题行上有公式，则设置公式信息
					AreaFormulaModel formulaModel =  (AreaFormulaModel) ExtendAreaModel.getInstance(formatModel).getRepLevelInfo(KEY_ORIALFORMULA);
					FormulaVO fomula = formulaModel.getDirectFml(temp);
					if(fomula != null) {
						addFormula(fomula.getContent(), anaModel, temp);
					}
					
					if(fieldSet.getColWidth() > 1) {
						AreaPosition area = formatModel.getCombinedCellArea(temp);
						AreaPosition newArea = AreaPosition.getInstance(temp, area.getEnd().getMoveArea(0, fieldSet.getColWidth() - 1));
						formatModel.combineCell(newArea);
					}
				}
			}
		}
		return movelen;
	}
	
	private static boolean isNotNullEquals(Object obj1, Object obj2) {
		if(obj1 != null && obj2 != null && obj1.equals(obj2)) {
			return true;
		}
		return false;
	}
	
	private static boolean isNULLVALUE(Object value) {
		if(value == null) {
			return false;
		}
		if(value.toString().startsWith(NULLVALUE)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 添加分组和明细字段
	 *
	 * @create by wanyonga at 2010-6-22,下午03:12:42
	 *
	 */
	private static void addGroupAndDetailField(AreaContentSet areaContentSet, int col,int moveStep, int detailCount,
			int levelGroupCount, CellPosition start, AbsAnaReportModel anaModel,
			CommonKeyMap<String, AreaFieldSet> aggrMap, SmartModel smartModel, int totalRow, int titleRowCount, Field detailRange) {
		// 分组细节字段在扩展区的相对行数
		int groupAndDetailRow = 0;
		if (areaContentSet.getShowType() == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
			groupAndDetailRow = 1;
		} else {
			groupAndDetailRow = totalRow - 1;
		}
		Field rangeField = (detailRange == null) ? null : detailRange;
		CellPosition temp = null;
		AnaRepField anaField = null;
		Field field = null;
		IFormat format = null;
		Map<String, IFormat> formatMap = areaContentSet.getFormatMap();

		if (col < detailCount + levelGroupCount) {
			// 分组和细节
			temp = start.getMoveArea(groupAndDetailRow, col + moveStep);
			if (col < levelGroupCount) {
				// 分组
				//@edit by zhujhc at 2012-11-12,下午04:23:24  加上标题行数的参数 
				addGroupFieldByColumn(col, areaContentSet, temp, anaModel, aggrMap, smartModel, levelGroupCount, titleRowCount, detailCount > 0);
			} else if (col - levelGroupCount < detailCount) {
				// 细节
				AreaFieldSet fieldSet = areaContentSet.getDetailFldNames()[col - levelGroupCount];
				field = SumByField.getFieldFromAreaSet(smartModel, fieldSet);
				if (field != null) {
					if (detailRange == null) {// 普通明细字段
						anaField = new AnaRepField(field, AnaRepField.TYPE_DETAIL_FIELD, areaContentSet
								.getSmartModelDefID());
//						if(ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExArea(temp) == null) {
//						}
						AnaReportFieldUtil.addFlds(anaModel, temp, anaField, false, true);
//						addFormula(fieldSet, anaModel, temp);
						if(levelGroupCount == 0) {// 没有分组字段时，需要增加合计格式
							if(aggrMap != null && !aggrMap.isEmpty() && areaContentSet.isShowCount()) {
//								format = areaContentSet.getFormatMap().get(AreaContentSet.COUNT_FORMAT);
								IFormat fmt = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT);
								CellPosition pos = null ;
								//@edit by zhujhc at 2012-11-12,下午04:20:03 补充小计在前的条件
								if(areaContentSet.getShowType() == AreaContentSet.SUBTOTAL_AFTER_GROUP){
									pos = temp.getMoveArea(1, 0);
								}else{
									pos = temp.getMoveArea(-1, 0);
								}
								addFormat(anaModel, pos, fmt);
							}
						}
					} else {// 需要全部统计的明细字段
						if (fieldSet.getCountType() >= 0) {// 统计字段
							addOneCountField(temp, anaModel, fieldSet, aggrMap, rangeField, smartModel, formatMap, areaContentSet.isShowCount());
						} else {// 普通分组字段
							anaField = new AnaRepField(field, AnaRepField.TYPE_GROUP_FIELD, areaContentSet
									.getSmartModelDefID());
							// 分组字段自动默认为升序
							anaField.setOrderType(AnaRepField.ORDERTYPE_ASCENDING);
							AnaReportFieldUtil.addFlds(anaModel, temp, anaField, false, true);
						}
					}
				}
				
				String formula = fieldSet.getFormula();;
				IAreaFieldBaseService areaFieldSrv = fieldSet.getAreaFieldSrv();
				if(areaFieldSrv != null && areaFieldSrv instanceof IAreaFieldSetService) {
					formula =((IAreaFieldSetService)areaFieldSrv).getAreaFieldFormula(field, null);
				} 
				if(formula != null && fieldSet instanceof SumByField) {
					fieldSet.setFormula(formula);
				}
				addFormula(fieldSet, anaModel, temp);
				format = fieldSet.getFormat();
				if (format != null) {
					anaModel.getFormatModel().getCell(temp).setFormat(format);
				}
				// add by yuyangi 增加默认标题格式  1
				else {
//					format = areaContentSet.getFormatMap().get(AreaContentSet.BODY_FORMAT);
					IFormat newFormat = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT);
					anaModel.getFormatModel().getCell(temp).setFormat(newFormat);
				}
			}
		} else {
			// 细节
			temp = start.getMoveArea(groupAndDetailRow, col);
			AreaFieldSet fieldSet = null;
			if(aggrMap != null) {
				fieldSet = (AreaFieldSet)aggrMap.getNextValue();
				field = SumByField.getFieldFromAreaSet(smartModel, fieldSet);
				if (field != null) {
					if (detailRange == null) {// 普通明细字段
						anaField = new AnaRepField(field, AnaRepField.TYPE_DETAIL_FIELD, areaContentSet
								.getSmartModelDefID());
//						if(ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExArea(temp) == null) {
//						}
						AnaReportFieldUtil.addFlds(anaModel, temp, anaField, false, true);
						//@edit by zhujhc at 2012-10-30,下午02:45:00
						//补充条件，没有分组字段时，但是只有统计字段时会多出一行格式的设置，加上存在明细字段的判断。
						if(levelGroupCount == 0 && detailCount != 0) {// 没有分组字段时，需要增加合计格式
							if(aggrMap != null && !aggrMap.isEmpty() && areaContentSet.isShowCount()) {
								IFormat fmt = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.TOTAL_FORMAT);
								CellPosition pos = temp.getMoveArea(1, 0);
								addFormat(anaModel, pos, fmt);
							}
						}
					} else {// 需要全部统计的明细字段
						if (fieldSet.getCountType() >= 0) {// 统计字段
							addOneCountField(temp, anaModel, fieldSet, aggrMap, rangeField, smartModel, formatMap, areaContentSet.isShowCount());
						} else {// 普通分组字段
							anaField = new AnaRepField(field, AnaRepField.TYPE_GROUP_FIELD, areaContentSet
									.getSmartModelDefID());
							// 分组字段自动默认为升序
							anaField.setOrderType(AnaRepField.ORDERTYPE_ASCENDING);
							AnaReportFieldUtil.addFlds(anaModel, temp, anaField, false, true);
						}
					}
				}
				addFormula(fieldSet, anaModel, temp);
				format = fieldSet.getFormat();
				if (format != null) {
					anaModel.getFormatModel().getCell(temp).setFormat(format);
				}
				// add by yuyangi 增加默认标题格式  1
				else {
//					format = areaContentSet.getFormatMap().get(AreaContentSet.BODY_FORMAT);
					IFormat newFormat = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT);
					anaModel.getFormatModel().getCell(temp).setFormat(newFormat);
				}
			} 
		}
	}

	static void addFormat(AbsAnaReportModel anaModel, CellPosition pos, IFormat format) {
		anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn()).setFormat(format);
		if(pos.getColumn() == 0) {
			anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn()).setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0886")/*@res "合计"*/);
		}
	}
	
	static boolean isGroupField(AreaFieldSet[][] groupFieldSets, AreaFieldSet detialField) {
		boolean isGroupField = false;
		if(groupFieldSets == null || groupFieldSets.length == 0) {
			return false;
		}
		for(AreaFieldSet[] groupFields : groupFieldSets) {
			for(AreaFieldSet groupField : groupFields) {
				if(groupField.getField() != null && detialField.getField() != null) {
					if(groupField.getField().equals(detialField.getField())) {
						isGroupField = true;
						break;
					}
				}
			}
		}
		return isGroupField;
	}



	/**
	 * 添加一列对应的分组字段
	 *
	 * @create by wanyonga at 2010-8-3,下午04:39:09
	 *
	 */
	private static void addGroupFieldByColumn(int col, AreaContentSet areaContentSet, CellPosition temp,
			AbsAnaReportModel anaModel, CommonKeyMap<String, AreaFieldSet> aggrMap, SmartModel smartModel,
			int levelGroupCount,int titleRowCount, boolean hasDetail) {
		if(areaContentSet.getGroupFldNames() == null || areaContentSet.getGroupFldNames()[col] == null) {
			return ;
		}
		int groupLevel = areaContentSet.getGroupFldNames()[col].length;
		boolean isShowCount = areaContentSet.isShowCount();
		AnaRepField anaField = null;
		Field field = null;
		IFormat format = null;
		CellPosition pos = null;// add by yuyangi
		if (areaContentSet.getShowType() == AreaContentSet.GROUP_NO_COMBINED && aggrMap != null && aggrMap.size() > 0) {
			int moveRowCount = 1;
			for (int r = col + 1; r < areaContentSet.getGroupFldNames().length; r++) {
				moveRowCount += areaContentSet.getGroupFldNames()[r].length;
			}
			temp = temp.getMoveArea(-moveRowCount, 0);
		}
		pos = temp;
		for (int i = 0; i < groupLevel; i++) {
			field = SumByField.getFieldFromAreaSet(smartModel,
					areaContentSet.getGroupFldNames()[col][groupLevel - 1 - i]);
			if (field != null) {
				format = areaContentSet.getGroupFldNames()[col][groupLevel - 1 - i].getFormat();
				// add by yuyangi 如果没有设置格式信息，则加载默认格式
				if(format == null) {
					//  modify by yuyangi 将IufoFormat改成相应接口IFormat
//					IFormat oldFormat = (IFormat) anaModel.getFormatModel().getCellFormatIfNullNew(pos);
//					IFormat newFormat = areaContentSet.getFormatMap().get(AreaContentSet.BODY_FORMAT);
					format = getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT);
				}
				anaField = new AnaRepField(field, AnaRepField.TYPE_GROUP_FIELD, areaContentSet.getSmartModelDefID());
				// 分组字段自动默认为升序
				anaField.setOrderType(AnaRepField.ORDERTYPE_ASCENDING);
				//计算时没有勾选小计而需要删除的行数
				int noSumNum = getDelCellNum(col, areaContentSet);
				List<Integer> noCountCols = getGroupNoCountList(areaContentSet);
				//@modify by zhujhc at 2012-11-10,下午07:29:33 传入参数 areaContentSet 方便取到里面的设置信息
				doAddOneGroupField(format, anaField, temp, areaContentSet , anaModel, aggrMap,
						levelGroupCount, col, hasDetail, noSumNum, noCountCols, areaContentSet.getGroupFldNames()[col][groupLevel - 1 - i]);
				ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel.getFormatModel());
				ExtendAreaCell exCell = exModel.getExAreaByPK(areaContentSet.getAreaPk());
				if(exCell == null) {
					exCell = exModel.getExtendAreaCells().length > 0 ? exModel.getExtendAreaCells()[0] : null;
				}
				GridSortUtil.updateOrderMng(exCell, anaField,false);// 增加排序信息
				temp = temp.getMoveArea(-1, 0);
				if (col == 0 && i == groupLevel - 1 && areaContentSet.getShowType() == AreaContentSet.GROUP_NO_COMBINED
						&& aggrMap != null && aggrMap.size() > 0) {
					if(isShowCount) {
						anaModel.getFormatModel().getCellIfNullNew(temp.getRow(), temp.getColumn()).setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0886")/*@res "合计"*/);
						// 合计 add by yuyangi
						anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(), getCommonFormat(temp, anaModel.getFormatModel(),
								AreaContentSet.TOTAL_FORMAT));
					}
				}
				//@edit by zhujhc at 2012-11-9,下午08:03:53 整理出一个添加格式的方法
				AreaPosition newArea = exCell.getArea();
				if(newArea != null ){
					addFormatByColumn(newArea, col, areaContentSet, pos, anaModel, aggrMap, levelGroupCount, hasDetail, noSumNum, noCountCols, titleRowCount);
				}
			}
		}
		// 级次汇总时，明细行的第一个单元没有设置格式。先对它单独处理。
		if(areaContentSet.getShowType() == AreaContentSet.GROUP_NO_COMBINED && col < levelGroupCount) {
			CellPosition cpTempDown = pos;
			CellPosition ceTempUp = temp;
			int preGlvl = 0;
			int nextGlvl = 0;
			if(col > 0) {
				for(int i = 0;i < col;i++) {
					preGlvl = preGlvl + areaContentSet.getGroupFldNames()[col - i - 1].length;
				}
			}
			if(col < areaContentSet.getGroupFldNames().length - 1) {
				for(int i = 0;i < areaContentSet.getGroupFldNames().length - 1 - col;i++) {
					nextGlvl = nextGlvl + areaContentSet.getGroupFldNames()[col + i + 1].length;
				}

			}
			int stepRow = 0;
			if(areaContentSet.getShowType() == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
				stepRow = -1;
			} else {
				stepRow = 1;
			}// 向下的格式
			for(int i = 0;i < nextGlvl + 1; i++) {
				cpTempDown = cpTempDown.getMoveArea(stepRow, 0);
//				if(areaContentSet.getShowType() == AreaContentSet.SUBTOTAL_AFTER_GROUP && i == nextGlvl)
//					anaModel.getFormatModel().setCellFormat(cpTempDown.getRow(), cpTempDown.getColumn(), getCommonFormat(AreaContentSet.COUNT_FORMAT, pos, anaModel.getFormatModel(),
//							areaContentSet.getFormatMap().get(AreaContentSet.COUNT_FORMAT)));
//				else
					anaModel.getFormatModel().setCellFormat(cpTempDown.getRow(), cpTempDown.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(),
							AreaContentSet.BODY_FORMAT));
			}
			for(int i = 0;i < preGlvl + 1; i++) {
				if(i == preGlvl)
					anaModel.getFormatModel().setCellFormat(ceTempUp.getRow(), ceTempUp.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(),
							AreaContentSet.TOTAL_FORMAT));
				else
					anaModel.getFormatModel().setCellFormat(ceTempUp.getRow(), ceTempUp.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(),
							AreaContentSet.COUNT_FORMAT));
				ceTempUp = ceTempUp.getMoveArea(-stepRow, 0);
			}
		}
	}

	/**
	 * 行列表，添加完分组字段后，添加单元格格式
	 * 
	 * @param newArea  整理后新的扩展区域
	 * @param col      当前添到的列
	 * @param areaContentSet  分组管理面板的设置内容
	 * @param pos    当前单元格位置
	 * @param anaModel  分析模型
	 * @param aggrMap   统计字段的Map
	 * @param levelGroupCount  分组字段的个数
	 * @param hasDetail   是否有明细字段
	 * @param noSumNum    没有勾选小计而需要删除的行数
	 * @param noCountCols    分组字段没有勾选小计的列列表
	 * @param titleRowCount  标题行数
	 * 
	 * @since ncv63
	 * @edit by zhujhc at 2012-11-10,下午04:18:31
	 */
	private static void addFormatByColumn(AreaPosition newArea, int col, AreaContentSet areaContentSet, CellPosition pos, AbsAnaReportModel anaModel, 
			CommonKeyMap<String, AreaFieldSet> aggrMap, int levelGroupCount, boolean hasDetail, int noSumNum, List<Integer> noCountCols , int titleRowCount){
		int subTotalType = areaContentSet.getShowType();//小计的类型
		boolean isShowCount = areaContentSet.isShowCount();//是否合计
		int groupNoCountNum = getGroupNoCountNum(areaContentSet);
		if(subTotalType != AreaContentSet.GROUP_NO_COMBINED && aggrMap != null && aggrMap.size() > 0) {
			int moveRow = 0;//记录添加分组字段所移动的行数
			if (subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
				if(hasDetail) {
					moveRow = levelGroupCount - col;
				} else {
					moveRow = levelGroupCount - col - 1;
				}
				//noSumNum 该变量是计算没有勾选小计而需要删除的行数
				moveRow = moveRow - noSumNum;
				if(moveRow < 0){//解决分组共有四个字段，头两个字段勾选了小计而出现的异常情况
					moveRow = 0 ;
				}
			} else {
				if(hasDetail) {
					moveRow = -(levelGroupCount - col);
				} else {
					moveRow = -(levelGroupCount - col - 1);
				}
				moveRow = moveRow + noSumNum;
				if(moveRow > 0){//解决分组共有四个字段，头两个字段勾选了小计而出现的异常情况
					moveRow = 0 ;
				}
			}
			//计算合并单元格的行数--- moveRow 变化一个单元格，即为分组字段所占单元格数
			if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
				moveRow = moveRow + 1 ;
			}else{
				moveRow = moveRow - 1 ;
			}
			if(col == 0 && isShowCount) {
				return ; //第一列如果选中了合计则添加合计的格式,添加分组字段时已经把"合计"标题及格式添加，故不作处理
			}else{
				//思路是判断该字段是否添加了合计或小计，然后去掉一行，再用总的高度减去分组字段所占的行数和小计或合计的一行，剩下的即为需要添加格式的行数
				int areaHeigth = newArea.getHeigth();
				areaHeigth = areaHeigth - titleRowCount ;//去掉标题行剩下的高度（可能存在问题标题的高度需要重新计算）
				int fillFormatRowNum = 0;//需要添加小计格式的行数
				if(!isShowCount){//没有勾选合计
					//添加小计的格式
					if(groupNoCountNum != 0){//小计没有全部勾选
						//先判断该列分组字段下面有无小计字段
						if(!noCountCols.contains(col-1)){//存在小计
							if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
								moveRow = moveRow + 1 ;//小计所在单元已经存在格式
							}else{
								moveRow = moveRow - 1 ;//小计所在单元已经存在格式
							}
						}
					}else{//小计全部勾选，第一行除外 
						if(col == 0){
							return ;
						}
						if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
							moveRow = moveRow + 1;//加上"小计"标题所在行数。
						}else{
							moveRow = moveRow - 1;//加上"小计"标题所在行数。
						}
					}
					if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
						fillFormatRowNum = areaHeigth - moveRow;//需要添加小计格式的行数
					}else{
						fillFormatRowNum = areaHeigth + moveRow;//需要添加小计格式的行数
					}
					if(fillFormatRowNum == 0){//为 0 则不需要添加格式，直接返回
						return ;
					}
					if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
						pos = pos.getMoveArea( moveRow - 1 , 0 );//位置移动到分组字段所在位置
					}else{
						pos = pos.getMoveArea( moveRow + 1 , 0 );//位置移动到分组字段所在位置
					}
					for(int i = 0 ;i < fillFormatRowNum ;i ++ ){//添加小计的格式
						if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
							pos = pos.getMoveArea(1, 0);//逐行移动添加小计格式
						}else{
							pos = pos.getMoveArea( - 1, 0);//逐行移动添加小计格式
						}
						anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), 
								getCommonFormat(pos, anaModel.getFormatModel(),AreaContentSet.COUNT_FORMAT));
					}
				}else{//勾选了合计
					if(groupNoCountNum != 0){//小计没有全部勾选
						//先判断该列分组字段下面有无小计字段
						if(!noCountCols.contains(col-1)){//已经存在小计
							if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
								moveRow = moveRow + 1 ;//小计所在单元已经存在格式
							}else{
								moveRow = moveRow - 1 ;//小计所在单元已经存在格式
							}
						}
					}else{//小计全部勾选
						if(col == 0){
							return ;
						}
					}
					if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
						fillFormatRowNum = areaHeigth - moveRow;//需要添加小计格式的行数
					}else{
						fillFormatRowNum = areaHeigth + moveRow;//需要添加小计格式的行数
					}
					if(fillFormatRowNum == 0){//为 0 则不需要添加格式，直接返回
						return ;
					}
					if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
						pos = pos.getMoveArea( moveRow - 1 , 0 );//位置移动到分组字段所在位置
					}else{
						pos = pos.getMoveArea( moveRow + 1 , 0 );//位置移动到分组字段所在位置
					}
					for(int i = 0 ;i < fillFormatRowNum - 1 ;i ++ ){//添加小计的格式
						if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
							pos = pos.getMoveArea(1, 0);//逐行移动添加小计格式
						}else{
							pos = pos.getMoveArea( - 1, 0);//逐行移动添加小计格式
						}
						anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), 
								getCommonFormat(pos, anaModel.getFormatModel(),AreaContentSet.COUNT_FORMAT));
					}
					if(subTotalType == AreaContentSet.SUBTOTAL_AFTER_GROUP){
						pos = pos.getMoveArea(1, 0);//向下移动一个单元格，添加合计的格式
					}else{
						pos = pos.getMoveArea( - 1 , 0);//向上移动一个单元格，添加合计的格式
					}
					anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(),
							AreaContentSet.TOTAL_FORMAT));
				}
			}
		}
	}
	
	/**
	 * 添加一个分组字段到指定单元位置
	 *
	 * @create by wanyonga at 2010-7-30,下午01:49:44
	 *
	 */
	private static void doAddOneGroupField(IFormat format, AnaRepField groupField, CellPosition temp, AreaContentSet areaContentSet,
			AbsAnaReportModel anaModel, CommonKeyMap<String, AreaFieldSet> aggrMap, int levelGroupCount, int col, boolean hasDetial
			, int noSumNum, List<Integer> noCountCols, AreaFieldSet fieldSet) {
		//@modify by zhujhc at 2012-11-10,下午07:29:33 传入参数 areaContentSet 方便取到里面的设置信息
		boolean isShowTotal = areaContentSet.isShowCount();
		int showType = areaContentSet.getShowType() ;
		int moveRow = 0;
		if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP || showType == AreaContentSet.SUBTOTAL_BEFORE_GROUP) {
			if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
				if(hasDetial) {
					moveRow = levelGroupCount - col;
				} else {
					moveRow = levelGroupCount - col - 1;
				}
				moveRow = moveRow - noSumNum;
				//如当有四个分组字段时只勾选前两个就会出现moveRow<0 的情况
				//@edit by zhujhc at 2012-11-3,下午05:48:34 当 moveRow小于0时，说明超出扩展区，故调整为0
				if(moveRow < 0){
					moveRow = 0 ;
				}
			} else {
				if(hasDetial) {
					moveRow = -(levelGroupCount - col);
				} else {
					moveRow = -(levelGroupCount - col - 1);
				}
				moveRow = moveRow + noSumNum;
				if(moveRow > 0){//解决分组共有四个字段，头两个字段勾选了小计而出现的异常情况
					moveRow = 0 ;
				}
			}
			AreaPosition area = null;
			IArea fmlArea = null;
			// @edit by zhujhc at 2012-11-3,下午06:33:08 调整后新区域的最后一行标号
			ExtendAreaCell exCell = anaModel.getExAreaCell(temp);
			int newAreaEndRow = 0 ;
			int newAreaStartRow = 0;//调整区域后的起始行
			if(exCell != null){
				newAreaEndRow = exCell.getArea().getEnd().getRow();
				newAreaStartRow = exCell.getArea().getStart().getRow();
			}
			if (aggrMap != null && aggrMap.size() > 0) {
				// 同时存在分组和汇总字段，分组单元根据分组层次设置合并单元格
				area = AreaPosition.getInstance(temp, temp.getMoveArea(moveRow, 0));
				anaModel.getFormatModel().combineCell(area);
				anaModel.getFormatModel().setCellFormat(area.getStart().getRow(), area.getEnd().getColumn(), getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT));
				// 需要在分组字段下面显示("小计" "合计"字样提示)
				CellPosition pos = null;
				if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
					pos = temp.getMoveArea(moveRow + 1, 0);
				} else {
					pos = temp.getMoveArea(moveRow - 1, 0);
				}
//				anaModel.getFormatModel().setCellProperty(pos, PropertyType.DataType, TableConstant.CELLTYPE_SAMPLE);
				//@edit by zhujhc at 2012-11-6,上午09:56:45 调整格式，三个列表都不为空时，分组字段不全勾选小计时，有的分组字段下面没有小计的格式
				if(pos.getRow() <= newAreaEndRow && pos.getRow() > newAreaStartRow){//这样限制是为了防止因小计无规律勾选而出现的错误
					anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT));
				}
				if (col == 0 && isShowTotal) {
					anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn()).setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0886")/*@res "合计"*/);
					anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(), AreaContentSet.TOTAL_FORMAT));
				} else if(col > 0){
					if(!noCountCols.contains(col -1)) {
						//当勾选合计，无明细字段时，如果小计只勾选一个，则不添加"小计"，添加合计的格式
						if(isShowTotal && !hasDetial && (levelGroupCount - getGroupNoCountNum(areaContentSet)) == 1){
							anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(), AreaContentSet.TOTAL_FORMAT));
						}else{
							// @edit by zhujhc at 2012-11-3,下午06:33:08 添加判断，目标位置超出新区域后的位置则不操作
							if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
								if(pos.getRow() <= newAreaEndRow ){//这样限制是为了防止因小计无规律勾选而出现的错误
									anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn()).setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0882")/*@res "小计"*/);
									anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT));
								}
							}else{
								if(pos.getRow() > newAreaStartRow){//只勾选一个小计
									anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn()).setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0882")/*@res "小计"*/);
									anaModel.getFormatModel().setCellFormat(pos.getRow(), pos.getColumn(), getCommonFormat(pos, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT));
								}
							}
							
						}
					}	

				}
			}
			AnaReportFieldUtil.addFlds(anaModel, temp, groupField, false, true);
			if(area != null) {
				fmlArea = area;
			} else {
				fmlArea = temp;
			}

			String formula = fieldSet.getFormula();;
			IAreaFieldBaseService areaFieldSrv = fieldSet.getAreaFieldSrv();
			if(areaFieldSrv != null && areaFieldSrv instanceof IAreaFieldSetService) {
				formula = ((IAreaFieldSetService)areaFieldSrv).getAreaFieldFormula(groupField.getField(), null);
			} 
			if(formula != null  && fieldSet instanceof SumByField) {
				fieldSet.setFormula(formula);
			}

			addFormula(fieldSet, anaModel, fmlArea);
			AreaPosition combarea = anaModel.getFormatModel().getCombinedCellArea(temp);
			if(combarea == null) {
				combarea = AreaPosition.getInstance(temp, temp);
			}
			anaModel.getFormatModel().combineCell(AreaPosition.getInstance(temp, combarea.getEnd().getMoveArea(0, fieldSet.getColWidth() - 1)));
			anaModel.getFormatModel().getCellIfNullNew(temp.getRow(), temp.getColumn()).addExtFmt(AnaCellCombine.KEY, new AnaCellCombine());
			anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(), getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT));
		} else if (showType == AreaContentSet.GROUP_NO_COMBINED) {
			// 不自动合并
			AnaReportFieldUtil.addFlds(anaModel, temp, groupField, false, true);
			addFormula(fieldSet, anaModel, temp);
			anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(), getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.BODY_FORMAT));
		}
//		if (format != null) {
//			anaModel.getFormatModel().getCell(temp).setFormat(format);
//		}
	}

	/**
	 * 添加汇总字段
	 *
	 * @create by wanyonga at 2010-6-22,下午03:18:50
	 *
	 * @modify by yuyangi 2011-03-21 将addCountAllField和addCountGroupField参数修改成AreaContentSet，方便取格式信息
	 */
	private static void addAggrField(AreaContentSet areaContentSet, int col, int moveStep, int detailCount, int levelGroupCount,
			CellPosition start, AbsAnaReportModel anaModel, CommonKeyMap<String, AreaFieldSet> aggrMap,
			SmartModel smartModel, AreaPosition newArea) {
//		if (aggrMap != null && aggrMap.size() > 0) {
			if (levelGroupCount == 0) {
				// 无分组字段,统计字段都是总计
				addCountAllField(areaContentSet, col, moveStep, detailCount, levelGroupCount, start, anaModel,
						aggrMap, smartModel, areaContentSet.getShowType());
			} else {
				// 有分组字段，统计字段都是按所有分组进行分组统计,最后再总计
				addCountGroupField(areaContentSet, col, moveStep, detailCount, levelGroupCount, start,
						anaModel, aggrMap, smartModel, areaContentSet.getShowType(), newArea.getHeigth());
			}
//		}
	}

	/**
	 * 添加汇总字段(无分组)
	 *
	 * @create by wanyonga at 2010-6-22,下午03:22:42
	 *
	 * @modify by yuyangi 2011-03-21 将addCountAllField参数修改成AreaContentSet，方便取格式信息
	 * 
	 * @modify by zhujhc 2012-11-13,下午02:49:45  将addCountAllField参数修改成AreaContentSet，方便取格式信息
	 */
	private static void addCountAllField(AreaContentSet areaContentSet, int col, int moveStep, int detailCount, int groupCount,
			CellPosition start, AbsAnaReportModel anaModel, CommonKeyMap<String, AreaFieldSet> aggrMap,
			SmartModel smartModel, int showType) {
		CellPosition temp = null;
		AreaFieldSet fieldSet = null;
		AnaRepField detailField = null;
		AreaFieldSet[] fieldSets = areaContentSet.getAreaFieldSet();// add by yuyangi
		Map<String, IFormat> formatMap = areaContentSet.getFormatMap();
		if (detailCount == 0) {
			// 无分组和明细字段
			//@edit by zhujhc at 2012-10-23,下午09:25:50 temp 应该向下移动 1
			temp = start.getMoveArea(1, col + moveStep);
//			temp = start.getMoveArea(0, col + moveStep);
			fieldSet = fieldSets[col];
			addOneCountField(temp, anaModel, fieldSet, aggrMap, null, smartModel, formatMap, areaContentSet.isShowCount());
		} else {
			if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
				temp = start.getMoveArea(1, col + moveStep);
			} else {
				temp = start.getMoveArea(2, col + moveStep);
			}
			Cell cell = anaModel.getFormatModel().getCell(temp);
			if (cell != null && cell.getExtFmt(ExtendAreaConstants.FIELD_INFO) != null) {
				// 上面有显示明细字段
				detailField = (AnaRepField) cell.getExtFmt(ExtendAreaConstants.FIELD_INFO);
				// modify by yuyangi
				String fldName = detailField.getField().getExpression() != null ? detailField.getField().getExpression() : detailField.getField().getFldname();
				if (aggrMap != null && aggrMap.get(fldName) != null) {
					// 上面行的明细字段有汇总字段
					if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
						temp = start.getMoveArea(2, col + moveStep);
					} else {
						if(areaContentSet.isShowCount()){
							temp = start.getMoveArea(1, col + moveStep);
						}else{
							return ;
						}
					}
					fieldSet = aggrMap.get(fldName);
					addOneCountField(temp, anaModel, fieldSet, aggrMap, null, smartModel, formatMap, areaContentSet.isShowCount());
				}
			} 
			//@edit by zhujhc at 2012-11-13,下午05:00:40 去掉该判断，当小计在前，明细和统计列表中有字段时设置出现错误。
//			else {
//				// 上面行无明细字段 
//				if (aggrMap != null && aggrMap.size() > 0) {
//					fieldSet = (AreaFieldSet)aggrMap.getNextValue();
//					if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
//						temp = start.getMoveArea(2, col + moveStep);
//					} else {
//						temp = start.getMoveArea(1, col + moveStep);
//					}
//					addOneCountField(temp, anaModel, fieldSet, aggrMap, null, smartModel, formatMap, areaContentSet.isShowCount());
//				}
//			}
		}
	}

	/**
	 * 添加汇总字段(有分组，按组进行统计)
	 *
	 * @create by wanyonga at 2010-6-22,下午04:06:40
	 *
	 * @modify by yuyangi 2011-03-21 将addCountAllField和addCountGroupField参数修改成AreaContentSet，方便取格式信息
	 */
	private static void addCountGroupField(AreaContentSet areaContentSet, int col, int moveStep, int detailCount,
			int levelGroupCount, CellPosition start, AbsAnaReportModel anaModel,
			CommonKeyMap<String, AreaFieldSet> aggrMap, SmartModel smartModel, int showType, int rowCount) {

		AreaFieldSet[][] groupFldNames = areaContentSet.getGroupFldNames();

		boolean isShowCount = areaContentSet.isShowCount();

		CellPosition temp = null;
		if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
			temp = start.getMoveArea(1, col + moveStep);
		} else {
			temp = start.getMoveArea(rowCount - 1, col + moveStep);
		}
		if (col >= levelGroupCount) {
			Cell cell = anaModel.getFormatModel().getCell(temp);
			AnaRepField detailField = null;
			AreaFieldSet fieldSet = null;
			Map<String, IFormat> formatMap = areaContentSet.getFormatMap();
			if (cell != null && cell.getExtFmt(ExtendAreaConstants.FIELD_INFO) != null) {
				// 上面有明细字段
				detailField = (AnaRepField) cell.getExtFmt(ExtendAreaConstants.FIELD_INFO);
				String dFldName = detailField.isAggrFld() ? detailField.getFieldCountDef().getMainFldName()
						: detailField.getField().getFldname();

				if(detailField.isAggrFld()) {
					dFldName = detailField.getFieldCountDef().getMainField().getExpression() != null ?
							detailField.getFieldCountDef().getMainField().getExpression() : detailField.getFieldCountDef().getMainField().getFldname();
				} else {
					dFldName = detailField.getField().getExpression() != null ? detailField.getField().getExpression() : detailField.getField().getFldname();
				}

				if (aggrMap != null && aggrMap.get(dFldName.toUpperCase()) != null) {
					// 上面行的明细字段有汇总字段
					fieldSet = aggrMap.get(dFldName.toUpperCase());
					addCountFieldByColumn(col, moveStep, fieldSet, smartModel, aggrMap, anaModel, levelGroupCount, showType,
							start, rowCount, groupFldNames, formatMap, isShowCount, detailCount > 0);
				} else {
					// 有分组字段如果第一个明细字段无对应汇总字段，在该明细字段下面加"小计"字样
					if(aggrMap != null) {
						for(int i = 0;i <= levelGroupCount;i++) {
							if (i < levelGroupCount) {
								int len = groupFldNames[levelGroupCount - i - 1].length;
								for (int level = 0; level < len; level++) {
									if(groupFldNames[levelGroupCount - i - 1][len - level - 1] instanceof  GroupFieldSet) {
										if(!((GroupFieldSet)groupFldNames[levelGroupCount - i - 1][len - level - 1]).isSum()) {
											continue;
										}
									}

									if (col == levelGroupCount && showType != AreaContentSet.GROUP_NO_COMBINED && i == 0) {
										if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
											temp = temp.getMoveArea(1, 0);
										} else {
//											temp = temp.getMoveArea(rowCount - 1, 0);
											temp = temp.getMoveArea(-1, 0);
										}
										anaModel.getFormatModel().setCellProperty(temp, PropertyType.DataType,
												TableConstant.CELLTYPE_SAMPLE);
										anaModel.getFormatModel().getCellIfNullNew(temp.getRow(), temp.getColumn()).setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0882")/*@res "小计"*/);
										//  小计 增加小计格式 add by yuyangi
										anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(),
												getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT));
									}
									//  设置格式 第一行小计格式 add by yuyangi
									else {
										if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
											temp = temp.getMoveArea(1, 0);
										} else {
//											temp = temp.getMoveArea(rowCount - 1, 0);
											temp = temp.getMoveArea(-1, 0);
										}
										anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(),
												getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.COUNT_FORMAT));
									}
								}
							} else {
								if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
									temp = temp.getMoveArea(1, 0);
								} else {
//									temp = temp.getMoveArea(rowCount - 1, 0);
									temp = temp.getMoveArea(-1, 0);
								}
								if(areaContentSet.isShowCount()) {
									anaModel.getFormatModel().setCellFormat(temp.getRow(), temp.getColumn(),
											getCommonFormat(temp, anaModel.getFormatModel(), AreaContentSet.TOTAL_FORMAT));
								}
							}
						}
					}

				}
			} else {
				// 上面行无明细字段
				if (aggrMap != null && aggrMap.size() > 0) {
					fieldSet = (AreaFieldSet)aggrMap.getNextValue();
					addCountFieldByColumn(col, moveStep, fieldSet, smartModel, aggrMap, anaModel, levelGroupCount, showType,
							start, rowCount, groupFldNames, formatMap, isShowCount, detailCount > 0);
				}
			}
		}

	}

	/**
	 * 添加一列对应的汇总字段
	 *
	 * @create by wanyonga at 2010-8-3,下午02:44:33
	 *
	 */
	private static void addCountFieldByColumn(int col,int moveStep, AreaFieldSet fieldSet, SmartModel smartModel,
			CommonKeyMap<String, AreaFieldSet> aggrMap, AbsAnaReportModel anaModel, int levelGroupCount, int showType,
			CellPosition start, int rowCount, AreaFieldSet[][] groupFldNames, Map<String, IFormat> formatMap, boolean isShowCount, boolean hasDetail) {
		CellPosition temp = null;
		Field rangeField = null;

		if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
			if(hasDetail) {
				temp = start.getMoveArea(2, col + moveStep);
			} else {
				temp = start.getMoveArea(1, col + moveStep);
			}
		} else {
			if(hasDetail) {
				temp = start.getMoveArea(rowCount - 2, col + moveStep);
			} else {
				temp = start.getMoveArea(rowCount - 1, col + moveStep);
			}
		}
		for (int r = 0; r <= levelGroupCount; r++) {
			if (r < levelGroupCount) {
				int len = groupFldNames[levelGroupCount - r - 1].length;
				for (int level = 0; level < len; level++) {
					rangeField = SumByField.getFieldFromAreaSet(smartModel, groupFldNames[levelGroupCount
							- r - 1][len - level - 1]);
					if(groupFldNames[levelGroupCount - r - 1][len - level - 1] instanceof GroupFieldSet) {
						if(!((GroupFieldSet)groupFldNames[levelGroupCount - r - 1][len - level - 1]).isSum()) {
							continue;
						}
					}

					addOneCountField(temp, anaModel, fieldSet, aggrMap, rangeField, smartModel, formatMap, isShowCount);
					if (showType == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
						temp = temp.getMoveArea(1, 0);
					} else {
						temp = temp.getMoveArea(-1, 0);
					}
				}
			} else {
				// 最后的总计
				if(isShowCount) {
					rangeField = null;
					addOneCountField(temp, anaModel, fieldSet, aggrMap, rangeField, smartModel, formatMap, isShowCount);
				}
			}
		}
	}

	/**
	 * 在指定单元位置上定义一个汇总字段
	 *
	 * @create by wanyonga at 2010-6-22,下午03:42:50
	 *
	 * @update by yuyangi 增加默认格式参数formatMap
	 */
	private static void addOneCountField(CellPosition pos, AbsAnaReportModel anaModel, AreaFieldSet fieldSet,
			CommonKeyMap<String, AreaFieldSet> aggrMap, Field rangeField, SmartModel smartModel, Map<String, IFormat> formatMap
			, boolean isSum) {
		anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn());
		FieldCountDef countDef = null;
		Field field = null;
		if (fieldSet != null) {
			field = SumByField.getFieldFromAreaSet(smartModel, fieldSet);
			String formatType = AreaContentSet.COUNT_FORMAT;
			if (rangeField == null) {
				if (field != null) {
					countDef = new FieldCountDef(field, fieldSet.getCountType());
				}
				formatType = AreaContentSet.TOTAL_FORMAT;
			} else {
				if (field != null) {
					countDef = new FieldCountDef(field, fieldSet.getCountType(), rangeField);
				}
			}
			if (countDef != null) {
				if(field.getDataType() == DataTypeConstant.UNASSIGNED_NULL) {
					if(fieldSet.getCountType() == IFldCountType.TYPE_SUM) {
						countDef.setDataType(DataTypeConstant.BIGDECIMAL);
					} else if(fieldSet.getCountType() == IFldCountType.TYPE_COUNT 
							|| fieldSet.getCountType() == IFldCountType.TYPE_COUNT_DISTINCT
							|| fieldSet.getCountType() == IFldCountType.TYPE_AVAGE
							|| fieldSet.getCountType() == IFldCountType.TYPE_MAX
							|| fieldSet.getCountType() == IFldCountType.TYPE_MIN) {
						countDef.setDataType(DataTypeConstant.INT);
					}
				}
				AnaRepField anaField = new AnaRepField(countDef, AnaRepField.TYPE_CALC_FIELD, smartModel.getId());
				if(ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExArea(pos) != null){
					AnaReportFieldUtil.addFlds(anaModel, pos, anaField, false, true);

					String formula = fieldSet.getFormula();;
					IAreaFieldBaseService areaFieldSrv = fieldSet.getAreaFieldSrv();
					if(areaFieldSrv != null && areaFieldSrv instanceof IAreaFieldSetService) {
						formula = ((IAreaFieldSetService)areaFieldSrv).getAreaFieldFormula(countDef, rangeField);
					} 
					if(formula != null) {
						fieldSet.setFormula(formula);
					}

					addFormula(fieldSet, anaModel, pos);
					if (fieldSet.getFormat() != null) {
						anaModel.getFormatModel().getCell(pos).setFormat(fieldSet.getFormat());
					}
					// add by yuyangi 如果没有设置格式。则加载默认格式
					else {
						anaModel.getFormatModel().getCell(pos).setFormat(getCommonFormat(pos, anaModel.getFormatModel(), formatType));
					}
					AreaPosition combarea = anaModel.getFormatModel().getCombinedCellArea(pos);
					if(combarea == null) {
						combarea = AreaPosition.getInstance(pos, pos);
					}
					anaModel.getFormatModel().combineCell(AreaPosition.getInstance(pos, combarea.getEnd().getMoveArea(0, fieldSet.getColWidth() - 1)));
				}
			}

			// add by yuyangi 如果没有设置格式。则加载默认格式
			else
				if(isSum)
				{
				if(fieldSet.getFormat() != null)
					anaModel.getFormatModel().getCell(pos).setFormat(fieldSet.getFormat());
				else
					anaModel.getFormatModel().getCell(pos).setFormat(getCommonFormat(pos, anaModel.getFormatModel(), formatType));
			}
		}
	}

	/**
	 * @param detailCount 明细字段个数
	 * @param levelGroupCount 分组字段个数
	 * @param aggrCount 汇总字段个数
	 * @param areaContentSet 分组管理参数
	 * @param anaModel 报表模型
	 * @param newArea 新计算出的区域
	 * @param aggrMap 汇总字段集合
	 * @param exCell 扩展区
	 * @param titleRowCount 字段列标题高度
	 * @param titleHeight 大标题高度
	 * @param titles 字段列标题
	 */
	private static void addFieldToCellsModel(int detailCount, int levelGroupCount, int aggrCount,
			AreaContentSet areaContentSet, AbsAnaReportModel anaModel, AreaPosition newArea,
			CommonKeyMap<String, AreaFieldSet> aggrMap, ExtendAreaCell exCell, int titleRowCount, int titleHeight, Map<String, String[]> titles) {
		SmartModel smartModel = exCell.getAreaInfoSet().getSmartModel();
//		Field detailRange = getDetailRange(smartModel.getMetaData(), areaContentSet);
		CellPosition start = newArea.getStart();
		int width = newArea.getWidth();
		// add by yuyangi 增加报表标题
		start = addRepTitle(areaContentSet, start, anaModel.getFormatModel(), newArea.getWidth());
		CellPosition fldStart = start;
		if(areaContentSet.getShowType() == AreaContentSet.SUBTOTAL_AFTER_GROUP) {
			fldStart = start.getMoveArea(titleRowCount - 1, 0);
		}
		
//		CellPosition fldStart = start;
		CommonKeyMap<String, AreaFieldSet> copy1 = null;
		CommonKeyMap<String, AreaFieldSet> copy2 = null;
		CommonKeyMap<String, AreaFieldSet> copy3 = null;
		if(aggrMap != null) {
			copy1 = (CommonKeyMap<String, AreaFieldSet>)aggrMap.clone();
			copy2 = (CommonKeyMap<String, AreaFieldSet>)aggrMap.clone();
			copy3 = (CommonKeyMap<String, AreaFieldSet>)aggrMap.clone();
		}
		int moveStep = 0;
		for (int col = 0; col < width; col++) {
			// 添加字段标题
			int len = 0;
			len = addFieldTitle(areaContentSet, col, moveStep, detailCount, levelGroupCount, start, anaModel,
					smartModel, titleRowCount, newArea.getWidth(), copy1, titles);
			// 添加分组和明细字段
			addGroupAndDetailField(areaContentSet, col, moveStep, detailCount, levelGroupCount, fldStart, anaModel, copy2,
					smartModel, newArea.getHeigth(), titleRowCount, null);
			// 添加汇总字段
			addAggrField(areaContentSet, col, moveStep, detailCount, levelGroupCount, fldStart, anaModel, copy3, smartModel,
					newArea);
			moveStep += len;
		}
	}


	@SuppressWarnings("unused")
	private static Field getDetailRange(SmartModel smartModel, AreaContentSet areaContentSet) {
		// 一种特殊的明细字段设置要求，明细字段中设置了统计类型，则自动将其他字段全部当作分组;
		// 此方法就是计算这种情形下最明细级的分组字段，以便作为明细统计字段的范围
		Field rangeFld = null;
		Field lastGrpFld = null;
		AreaFieldSet[] details = areaContentSet.getDetailFldNames();
		for (AreaFieldSet fld : details) {
			if (fld.getCountType() < 0)
				lastGrpFld = SumByField.getFieldFromAreaSet(smartModel, fld);
			else
				rangeFld = SumByField.getFieldFromAreaSet(smartModel, fld);// 暂时记录有无统计字段
		}
		if (rangeFld == null)
			return null;
		else
			return lastGrpFld;
	}

	/**
	 * 重置区域后区域大小可能和其他扩展区有冲突，需要将这些冲突的扩展区平移
	 *
	 * @create by wanyonga at 2010-7-28,上午08:58:18
	 *
	 * @param newArea
	 * @param formatModel
	 * @param exCell
	 */
	@SuppressWarnings("deprecation")
	private static void doAreaBlock(AreaPosition newArea, AbsAnaReportModel anaModel, ExtendAreaCell exCell) {
		int moveRowCount = newArea.getHeigth() - exCell.getArea().getHeigth();
		int moveColCount = newArea.getWidth() - exCell.getArea().getWidth();
		moveRowCount = moveRowCount < 0 ? 0 : moveRowCount;
		moveColCount = moveColCount < 0 ? 0 : moveColCount;
		if (moveRowCount > 0 || moveColCount > 0) {
			ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel.getFormatModel());
			AreaFormulaHandler formulaHandler = new AreaFormulaHandler(anaModel.getFormatModel());
			formulaHandler.registerFuncDriver(new AreaFuncDriver());
			ExtendAreaCell[] exCells = exModel.getExtendAreaCells();
			for (ExtendAreaCell ec : exCells) {
				if (ec.getExAreaPK().equals(exCell.getExAreaPK()))
					continue;
				if (ec.getArea().intersection(newArea)) {
					if (ec.getArea().getStart().getRow() > exCell.getArea().getEnd().getRow()) {
						// 在下方的向下移动
						doMoveExCell(moveRowCount, 0, ec, exModel, anaModel, formulaHandler);
					} else if (ec.getArea().getStart().getColumn() > exCell.getArea().getEnd().getColumn()) {
						// 右方的右移
						doMoveExCell(0, moveColCount, ec, exModel, anaModel, formulaHandler);
					} else {
						doMoveExCell(moveRowCount, moveColCount, ec, exModel, anaModel, formulaHandler);
					}

				}
			}
			// 清除新区域内存在的表样以及和新区域相交的组合单元
			clearNewArea(newArea, anaModel);
		}
	}

	/**
	 * 处理扩展区的平移
	 *
	 * @create by wanyonga at 2010-7-28,上午09:24:57
	 *
	 * @param moveRowCount
	 * @param moveColCount
	 * @param exCell
	 */
	@SuppressWarnings("deprecation")
	static void doMoveExCell(int stepRow, int stepCol, ExtendAreaCell exCell, ExtendAreaModel exModel,
			AbsAnaReportModel anaModel, AreaFormulaHandler formulaHandler) {
		IArea newArea = exCell.getArea().getMoveArea(stepRow, stepCol);
		ExtendAreaCell[] exCells = exModel.getExtendAreaCells();
		for (ExtendAreaCell ec : exCells) {
			if (!ec.getArea().equals(exCell.getArea())) {
				if (ec.getArea().intersection(newArea)) {
					doMoveExCell(stepRow, stepCol, ec, exModel, anaModel, formulaHandler);
//					ec.getAreaInfoSet().getCrossSetInfo().crossMoveArea(crossset, newArea, oldArea);
				}
			}
		}
		// 先同步扩展区所有内容到模型，再清除旧区域内容，再从模型中取出内容填入新区域相应单元上
		exModel.syncExtendAreaCellData(exCell);
		// 清除原扩展区内容
		HashMap<CellPosition, FormulaVO> formulaMap = clearExCell(exCell, anaModel, formulaHandler);
		setExCell(stepRow, stepCol, exCell, anaModel);
		if (formulaMap != null && formulaMap.size() > 0) {
			// 平移扩展区如果定义公式，需要一并平移
			moveFormula(formulaMap, stepRow, stepCol, anaModel, formulaHandler);
		}
		// 平移后再次同步扩展区数据到扩展区模型
		exModel.syncExtendAreaCellData(exCell);
	}

	/**
	 * 处理扩展区移动时公式的移动
	 *
	 * @create by wanyonga at 2010-7-28,下午09:54:18
	 *
	 * @param formulaMap
	 * @param stepRow
	 * @param stepCol
	 * @param anaModel
	 * @param formulaHandler
	 */
	@SuppressWarnings({ "deprecation" })
	private static void moveFormula(HashMap<CellPosition, FormulaVO> formulaMap, int stepRow, int stepCol,
			AbsAnaReportModel anaModel, AreaFormulaHandler formulaHandler) {
		Set<CellPosition> set = formulaMap.keySet();
		CellPosition newPos = null;
		FormulaVO formulaVO = null;
		String dtext = null;
		for (CellPosition oldPos : set) {
			newPos = oldPos.getMoveArea(stepRow, stepCol);
			formulaVO = formulaMap.get(oldPos);
			try {
				IParsed objParsed = formulaVO.getLet();
				if (objParsed == null) {
					objParsed = formulaHandler.parseUserDefFormula(formulaVO.getContent());
					formulaVO.setLet(objParsed);
				}
				dtext = AreaFormulaUtil.modifyAbsFormula(oldPos, newPos, formulaVO.getContent(), objParsed, anaModel
						.getFormatModel().getMaxRow(), anaModel.getFormatModel().getMaxCol());
				formulaHandler.addDbDefFormula(null, newPos, dtext, null, true);
			} catch (ParseException e) {
				AppDebug.debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0858")/*@res "拷贝区域公式到目标位置:"*/ + e);
			}
		}
	}

	/**
	 * 根据扩展区模型中的同步内容和平移位置重新设置扩展区表格单元内容
	 *
	 * @create by wanyonga at 2010-7-28,上午10:15:17
	 *
	 * @param stepRow
	 * @param stepCol
	 * @param exCell
	 * @param anaModel
	 */
	private static void setExCell(int stepRow, int stepCol, ExtendAreaCell exCell, AbsAnaReportModel anaModel) {
		AreaPosition newArea = (AreaPosition) exCell.getArea().getMoveArea(stepRow, stepCol);
		// 清除新区域内存在的表样以及和新区域相交的组合单元
		clearNewArea(newArea, anaModel);

		// 旧扩展区域
		ExtendAreaCell oldCell = (ExtendAreaCell)exCell.clone();
		// 设置新扩展区域
		exCell.setArea(newArea);
		//@edit by zhujhc at 2012-11-22,下午08:54:35 把原来语义模型areaInfoSet中的SmartModelDefID值添加上打
		SmartModel oldCellSmartModel = oldCell.getAreaInfoSet().getSmartModel();
		if(oldCellSmartModel != null){
			String smartId = oldCellSmartModel.getId() ;
			if(smartId != null && !smartId.trim().equals("")){
				exCell.getAreaInfoSet().setSmartModelDefID(smartId);
			}
		}
		// 将交叉区域平移
		ExtendAreaModel extendModel = ExtendAreaModel.getInstance(anaModel.getFormatModel());
		AreaCrossSetInfo areaCrossSetInfo = extendModel.getExAreaByPK(exCell.getExAreaPK()).getAreaInfoSet().getCrossSetInfo();
		if(areaCrossSetInfo != null) {
			AreaPosition newCrossArea = (AreaPosition)areaCrossSetInfo.getCrossArea().getMoveArea(stepRow, stepCol);
			areaCrossSetInfo.crossMoveArea(areaCrossSetInfo, newCrossArea, oldCell.getArea());
		}

		// 扩展区平移需要将扩展区内定义的组合单元也一并平移
		List<CombinedCell> combinedList = exCell.getAreaInfoSet().getAllCombinedCell();
		AreaPosition newCombinedArea = null;
		for (CombinedCell combinedCell : combinedList) {
			newCombinedArea = (AreaPosition) combinedCell.getArea().getMoveArea(stepRow, stepCol);
			anaModel.getFormatModel().combineCell(newCombinedArea);
		}
		ExCellInfoSet cellInfos = exCell.getCellInfoSet();
		setCellInfo(cellInfos, stepRow, stepCol, anaModel);
	}

	static boolean setCellInfo(ExCellInfoSet cellInfos, int stepRow, int stepCol, AbsAnaReportModel anaModel) {
		for (CellPosition pos : cellInfos.getCellPositionSet()) {
			Set<String> keys = cellInfos.getExtKeys(pos);
			if (keys == null || keys.size() == 0)
				continue;
			CellPosition newPos = pos.getMoveArea(stepRow, stepCol);
			Cell newCell = anaModel.getFormatModel().getCellIfNullNew(newPos.getRow(), newPos.getColumn());
			for (String key : keys) {
				if (key.equals(ExtendAreaConstants.CELL_FORMAT)) {
					Object obj = cellInfos.getExtInfo(pos, key);
					if (obj != null) { // 格式
						newCell.setFormat((IFormat) obj);
					}
				} else if (key.equals(ExtendAreaConstants.CELL_VALUE)) {
					Object obj = cellInfos.getExtInfo(pos, key);
					if (obj != null) { // 值
						newCell.setValue(obj);
					}
				} else if (key.equals(ExtendAreaConstants.FIELD_INFO)) {
					Object obj = cellInfos.getExtInfo(pos, key);
					if (obj != null) {
						AnaRepField anaField = (AnaRepField) obj;
						AnaReportFieldUtil.addFlds(anaModel, newPos, anaField, false, true);
					}
				} else {// 其他的扩展属性
					newCell.addExtFmt(key, cellInfos.getExtInfo(pos, key));
				}
			}
		}
		return true;
	}

	static boolean setReportConditon(String smartID, ReportConditionItem[] condItems, boolean clear,
			AbsAnaReportModel anaModel) {
		SmartModel smart = anaModel.getSmartModel(smartID);
		if (smart == null)
			return false;
		if (clear) {
			anaModel.removeAllAnaReportCondition();
			anaModel.getContext().removeAttribute(FreeReportContextKey.KEY_PAGEDIM_CONDITION);// 在调整前清除上次的页维度信息
		}
		FreeAreaHideFields hideFields = null;
		ExtendAreaCell[] exCells = ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExtendAreaCells();
		Field[] flds = null;
		if(exCells != null) {
			for(ExtendAreaCell exCell : exCells) {
				flds = AnaReportFieldUtil.getFieldnInRealExtendArea(exCell, anaModel.getFormatModel());
				for (ReportConditionItem item : condItems) {
					Field field = null;
					Field disFiled = null;
					if(flds != null) {
						for(Field f : flds) {
							if(f.getExpression().equals(item.getFieldCode())) {
								field = f;
//								break;
							}
							if(f.getExpression().equals(item.getDisField())) {
								disFiled = f;
							}
							if(disFiled != null && field != null) {
								break;
							}
						}
					}
					if(field == null) {// 如果格式设计上没有此字段，则增加一个隐藏字段
						field = smart.getMetaData().getField(item.getFieldCode());
						if(field == null) {
							field = MetaLinkFinder.findField(smart, item.getFieldCode().toLowerCase());
						}
						if(field != null) {
							field.setExpression(item.getFieldCode());
							hideFields = (FreeAreaHideFields)exCell.getAreaInfoSet().getAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
							if(hideFields == null || hideFields.getHideFlds() == null) {
								hideFields = new FreeAreaHideFields();
								hideFields.setHideFlds(new Field[] {field});
							} else {
								Field[] hideFlds = hideFields.getHideFlds();
								Field[] newHideFlds = new Field[hideFlds.length + 1];
								System.arraycopy(hideFlds, 0, newHideFlds, 0, hideFlds.length);
								newHideFlds[hideFlds.length] = field;
								hideFields.setHideFlds(newHideFlds);
							}
							exCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS, hideFields);
						}
					}
					if(disFiled == null) {// 如果格式设计上没有此字段，则增加一个隐藏字段
						disFiled = smart.getMetaData().getField(item.getDisField());
						if(disFiled == null) {
							disFiled = MetaLinkFinder.findField(smart, item.getDisField().toLowerCase());
						}
						if(disFiled != null) {
							disFiled.setExpression(item.getDisField());
							hideFields = (FreeAreaHideFields)exCell.getAreaInfoSet().getAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
							if(hideFields == null) {
								hideFields = new FreeAreaHideFields();
								hideFields.setHideFlds(new Field[] {disFiled});
							} else {
								Field[] hideFlds = hideFields.getHideFlds();
								Field[] newHideFlds = new Field[hideFlds.length + 1];
								System.arraycopy(hideFlds, 0, newHideFlds, 0, hideFlds.length);
								newHideFlds[hideFlds.length] = disFiled;
								hideFields.setHideFlds(newHideFlds);
							}
							exCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS, hideFields);
						}
						if(disFiled==null){
							disFiled = anaModel.getDataSource().getUserDefinedField(item.getDisField(), smartID);
						}
					}
					if (field != null) {
//						field.setExpression(field.getFldname());
						AnaReportCondition cond = new AnaReportCondition((item.getSamrtID() == null) ? smartID : item
								.getSamrtID(), field);
						cond.setDisPlayAll(item.isShowAll());
						cond.setReportModel(anaModel);
						cond.setDirty(true, false);
						cond.setSelectedNull();//清空页维度已选值
						cond.setAllValues(null);//重新加载页维度数据
						cond.ResetReportCondDataSet();
						cond.removeAllChangeListeners();
						if(item.getDisField() != null && disFiled != null) {
							cond.setDispFldName(disFiled);
						}
						anaModel.addAnaReportCondition(cond, false);
					}
				}
			}
		}



		return true;
	}

	static boolean setFreezePos(AbsAnaReportModel anaModel, CellPosition pos) {
		if(pos == null) {
			return false;
		}
		CellsModel formatModel = anaModel.getFormatModel();
		if (formatModel.getTableSetting().getSeperateLockSet().isFreezing()) {
			if (formatModel.getTableSetting().getSeperateLockSet().isFrozenNoSplit()) {
				formatModel.getTableSetting().getSeperateLockSet().setSeperateRow(0);
				formatModel.getTableSetting().getSeperateLockSet().setSeperateCol(0);
			}
			formatModel.getTableSetting().getSeperateLockSet().setFreezing(false);
		}
//		else {
//		if(pos.getRow() == 0&& pos.getColumn() == 0){
//			Rectangle visibleRect = anaModel.getVisibleRect();
//			int row = anaModel.getCellsModel().getRowHeaderModel().getIndexByPosition(visibleRect.height /2);
//			int col = anaModel.getCellsModel().getColumnHeaderModel().getIndexByPosition(visibleRect.width /2);
//			pos = CellPosition.getInstance(row, col);
//		}
		if(formatModel.getTableSetting().getSeperateLockSet().getSeperateRow() == 0 &&
				formatModel.getTableSetting().getSeperateLockSet().getSeperateCol() == 0) {
			formatModel.getTableSetting().getSeperateLockSet().setFrozenNoSplit(true);

			int row  = pos.getRow();
			int col  = pos.getColumn();
			if (row <= 0 && col <= 0) {
				return false;
			}
			if (row >= 0) {
				formatModel.getTableSetting().getSeperateLockSet().setSeperateRow(row);
				formatModel.getTableSetting().getSeperateLockSet().setSeperateY(row);
			}
			if (col >= 0) {
				formatModel.getTableSetting().getSeperateLockSet().setSeperateCol(col);
				formatModel.getTableSetting().getSeperateLockSet().setSeperateX(col);
			}
//			firePropertyChange("seperate2lock", null, null);
		}

		formatModel.getTableSetting().getSeperateLockSet().setSeperateRow(
				formatModel.getRowHeaderModel().getIndexByPosition(
						getSeperateY(formatModel.getTableSetting().getSeperateLockSet(), formatModel)));
		formatModel.getTableSetting().getSeperateLockSet().setSeperateCol(
				formatModel.getColumnHeaderModel().getIndexByPosition(
						getSeperateX(formatModel.getTableSetting().getSeperateLockSet(), formatModel)));
		formatModel.getTableSetting().getSeperateLockSet().setFreezing(true);
		formatModel.setDirty(true);
		return true;
	}


	/**
	 * 窗口分割的水平象素的位置
	 *
	 * @return int
	 */
	static int getSeperateX(SeperateLockSet seperateLockSet, CellsModel formatModel) {
		// 防止拖动行高列宽时，m_nSeperateX不会自动更新。可以在行高列宽变化时重新，减少计算量！
		seperateLockSet.setSeperateX(
				formatModel.getColumnHeaderModel().getPosition(
						seperateLockSet.getSeperateCol()));
		return seperateLockSet.getSeperateX();
	}

	/**
	 * 窗口分割的水平象素的位置
	 *
	 * @return int
	 */
	static int getSeperateY(SeperateLockSet seperateLockSet, CellsModel formatModel) {
		// 防止拖动行高列宽时，m_nSeperateX不会自动更新。
		seperateLockSet.setSeperateY(
				formatModel.getRowHeaderModel().getPosition(
						seperateLockSet.getSeperateRow()));
		return seperateLockSet.getSeperateY();
	}

	/**
	 * 清除扩展区内表格单元上的所有数据
	 *
	 * @create by wanyonga at 2010-7-28,上午09:55:18
	 *
	 * @param exCell
	 * @param formatModel
	 */
	@SuppressWarnings("deprecation")
	private static HashMap<CellPosition, FormulaVO> clearExCell(ExtendAreaCell exCell, AbsAnaReportModel anaModel,
			AreaFormulaHandler formulaHandler) {
		HashMap<CellPosition, FormulaVO> formulaMap = null;
		FormulaVO formulaVO = null;
		CellPosition[] cells = exCell.getArea().split();
		AnaReportFieldUtil.removeFlds(anaModel, cells);
		for (CellPosition pos : cells) {
			Cell cell = anaModel.getFormatModel().getCell(pos);
			if (cell != null) {
				anaModel.getFormatModel().setCellValue(pos, null);
				//@edit by zhujhc at 2012-11-22,下午04:38:55  清除扩展时，把原来扩展区的格式也清除
				IFormat format = anaModel.getFormatModel().getCell(pos).getFormat();
				if(format != null){
					anaModel.getFormatModel().getCell(pos).setFormat(null);
				}
				formulaVO = formulaHandler.getFormulaModel().getDirectFml(pos);
				if(formulaVO == null) {
					IArea fmlArea = getCombCellByPos(exCell, pos);
					formulaVO = formulaHandler.getFormulaModel().getDirectFml(fmlArea);
				}
				if (formulaVO != null) {
					if (formulaMap == null) {
						formulaMap = new HashMap<CellPosition, FormulaVO>();
					}
					formulaMap.put(pos, formulaVO);
					formulaHandler.clearFormula(pos);
				}
			}
		}
		// 扩展区内的组合单元删除
		CombinedAreaModel crm = CombinedAreaModel.getInstance(anaModel.getFormatModel());
		CombinedCell[] combinedCells = crm.getContainCombinedCells(exCell.getArea());
		if (combinedCells.length > 0) {
			crm.removeCombinedCell(combinedCells);
		}
		return formulaMap;
	}

	private static AreaPosition getNewArea(int detailCount, int levelGroupCount, int totalGroupCount,
			int maxGroupLevel, int aggrCount, int otherCount, AreaPosition oldArea, int titleRowCount, boolean isShowCount,
			int noCountNum, int titleHeight) {
		CellPosition start = oldArea.getStart();
		int width = detailCount + levelGroupCount;
		int height = 0;
		if (width == 0) {
			// 无细节和分组字段
			if (aggrCount > 0) {
				// 如果存在汇总字段，按一行显示
//				height = 1;
				//@edit by zhujhc at 2012-10-18,下午07:56:29 两行显示
				height = 2;
				width = aggrCount;
			} else {
				// 无汇总字段，不做设置
				return null;
			}
		} else {
			if (aggrCount > 0) {
				if(isShowCount) {
					if(detailCount > 0) {
						height = 3 + totalGroupCount;
					} else {
						height = 2 + totalGroupCount;
					}
//					height = 3 + totalGroupCount;
				} else if (detailCount > 0){
					height = 2 + totalGroupCount;
				} else {
					height = 1 + totalGroupCount;
				}
				width = width + otherCount;
			} else {
				if (totalGroupCount == 0) {
					height = 2;
				} else {
					height = 1 + maxGroupLevel;
				}
			}
		}
		height += titleRowCount - 1 + titleHeight;
		if(aggrCount > 0) {
			height = height - noCountNum;
		}
		return AreaPosition.getInstance(start.getRow(), start.getColumn(), width, height);
	}

	private static String checkAreaContentSet(AreaContentSet areaContentSet, ExtendAreaCell exCell,
			SmartModel smartModel) {
		if (exCell == null) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0884")/*@res "重新设置内容的扩展区不存在"*/;
		}
		if (smartModel == null) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0885")/*@res "语义模型不存在"*/;
		}
		if (areaContentSet != null) {
			return areaContentSet.checkAreaContentSet(exCell, smartModel);
		}
		return null;
	}

	static boolean setFixValues(String areaPK, String fieldName, FixValueInfo[] fixValues, AbsAnaReportModel anaModel) {
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaCell exCell = ExtendAreaModel.getInstance(formatModel).getExAreaByPK(areaPK);
		if (exCell == null)
			return false;
		AnaRepField fld = findRepField(exCell, fieldName, formatModel);
		if (fld == null)
			return false;
		fld.getDimInfo().setFix(true);
		ArrayList<FixField> list = new ArrayList<FixField>();
		for (FixValueInfo fixInfo : fixValues) {
			FixField fix = new FixField(fixInfo.getValue(), fixInfo.getShowName());
			list.add(fix);
			if (fixInfo.getFormat() != null) {
				DimValueSet dimValue = new DimValueSet(new String[] { fieldName }, new Object[] { fixInfo.getValue() });
				fld.getDimInfo().setDimValueFormt(dimValue, fixInfo.getFormat());
			}
		}
		fld.getDimInfo().setFixValueAndNames(list);
		// 从表格模型再次同步扩展区数据到扩展区模型
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(exCell);
		return true;

	}

	private static AnaRepField findRepField(ExtendAreaCell exCell, String fieldName, CellsModel formatModel) {
		if (exCell == null || fieldName == null)
			return null;
		CellPosition[] posArray = exCell.getArea().split();
		for (CellPosition pos : posArray) {
			Cell cell = formatModel.getCell(pos);
			if (cell == null)
				continue;
			AnaRepField fld = (AnaRepField) cell.getExtFmt(AnaRepField.EXKEY_FIELDINFO);
			if (fld != null && fieldName.equalsIgnoreCase(fld.getField().getFldname())) {
				return fld;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private static void clearExAreaContent(ExtendAreaCell exCell, AbsAnaReportModel anaModel, boolean removeExCell) {
		AreaPosition area = exCell.getArea();
		CellPosition[] cells = area.split();
		AnaReportFieldUtil.removeFlds(anaModel, cells);
		AreaFormulaHandler formulaHandler = new AreaFormulaHandler(anaModel.getFormatModel());
		formulaHandler.registerFuncDriver(new AreaFuncDriver());
		// 清除扩展区内表格单元的值和公式
		for (CellPosition cell : cells) {
			if (anaModel.getFormatModel().getCell(cell) != null) {
				anaModel.getFormatModel().setCellValue(cell, null);
				anaModel.getFormatModel().setCellFormat(cell.getRow(), cell.getColumn(), null);
				formulaHandler.clearFormula(cell);
			}
		}
		// 扩展区内的组合单元删除
		CombinedAreaModel crm = CombinedAreaModel.getInstance(anaModel.getFormatModel());
		CombinedCell[] combinedCells = crm.getContainCombinedCells(area);
		if (combinedCells.length > 0) {
			crm.removeCombinedCell(combinedCells);
		}
		if (removeExCell) {
			ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel.getFormatModel());
//			// 清理单元 add by yuyangi
//			exCell.getAreaInfoSet().setDataModel(null);
			exCell.removeExtFmtAll();
			exModel.removeExArea(exModel.getExAreaByPK(exCell.getExAreaPK()));
			anaModel.getFormatModel().clearCells(exCell.getArea());
//			AnaReportFieldUtil.clearFldsByPos(anaModel, exCell.getArea().split());
		}
	}

	/**
	 * 在给区域设置内容前先清除表样，组合单元、公式等
	 *
	 * @create by wanyonga at 2010-7-28,下午08:36:12
	 *
	 * @param newArea
	 * @param anaModel
	 */
	private static void clearNewArea(AreaPosition newArea, AbsAnaReportModel anaModel) {
		CellPosition[] cells = newArea.split();
		// 清除新区域中已经存在的表样
		for (CellPosition cell : cells) {
			if (anaModel.getFormatModel().getCell(cell) != null) {
				anaModel.getFormatModel().setCellValue(cell, null);
			}
		}
		// 清除和新区域相交的组合单元
		CombinedAreaModel crm = CombinedAreaModel.getInstance(anaModel.getFormatModel());
		CombinedCell[] combinedCells = crm.getCombineCells();
		List<CombinedCell> interList = new ArrayList<CombinedCell>();
		for (CombinedCell combinedCell : combinedCells) {
			if (newArea.intersection(combinedCell.getArea())) {
				interList.add(combinedCell);
			}
		}
		if (interList.size() > 0) {
			crm.removeCombinedCell(interList.toArray(new CombinedCell[0]));
		}
	}

	/**
	 *给新区域添加默认边框
	 *
	 * @create by wanyonga at 2010-8-11,上午09:30:46
	 *
	 * @param newArea
	 * @param anaModel
	 */
	@SuppressWarnings("unused")
	private static void addDefaultFormat(AreaPosition newArea, AbsAnaReportModel anaModel) {
		CellsModel cellsModel = anaModel.getFormatModel();
		CellPropertyValues values = new CellPropertyValues();
		Hashtable<Integer, Integer> linesMap = getDefaultImageValue();
		values.setLines(linesMap);
		Map<Integer, Integer> linesTable = values.getLines();
		for (int iAreaAlign : ICellAttr.AREA_ALIGN) {
			CellPosition[] alignPoses = getPosByAlign(newArea, iAreaAlign, cellsModel);
			ICellLine lines = null;
			for (CellPosition pos : alignPoses) {
				//  modify by yuyangi 将IufoFormat改成相应接口IFormat
				IFormat oldFormat = (IFormat) cellsModel.getCellFormatIfNullNew(pos);
				if (lines == null) {
					lines = getCellLine(pos, newArea, cellsModel, linesTable);
				}
				IFormat newFormat = IufoFormat.getInstance(oldFormat.getDataFormat(), oldFormat.getFont(), oldFormat
						.getAlign(), lines);
				cellsModel.setCellFormat(pos.getRow(), pos.getColumn(), newFormat);
			}
			lines = null;
		}
	}

	private static ICellLine getCellLine(CellPosition pos, AreaPosition area, CellsModel cellsModel,
			Map<Integer, Integer> propertyTable) {
		IFormat format = cellsModel.getCellFormatIfNullNew(pos);
		if (propertyTable == null || propertyTable.size() == 0) {
			return format.getLines();
		}
		ICellLine lines = format.getLines();
		int[] oldLineType = lines.getAllLineType();
		Color[] oldLineColor = lines.getAllColor();
		Iterator<Integer> enPros = propertyTable.keySet().iterator();
		while (enPros.hasNext()) {
			Integer iType = enPros.next();
			switch (iType) {
			case PropertyType.TLType:
				if (pos.getRow() == area.getStart().getRow()) {
					oldLineType[IFormat.TOPLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.TOPLINE] = new Color(propertyTable.get(PropertyType.TLColor));
				}
				break;

			case PropertyType.BLType:
				if (pos.getRow() == area.getEnd().getRow()) {
					oldLineType[IFormat.BOTTOMLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.BOTTOMLINE] = new Color(propertyTable.get(PropertyType.BLColor));
				}
				break;
			case PropertyType.LLType:
				if (pos.getColumn() == area.getStart().getColumn()) {
					oldLineType[IFormat.LEFTLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.LEFTLINE] = new Color(propertyTable.get(PropertyType.LLColor));
				}
				break;
			case PropertyType.RLType:
				if (pos.getColumn() == area.getEnd().getColumn()) {
					oldLineType[IFormat.RIGHTLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.RIGHTLINE] = new Color(propertyTable.get(PropertyType.RLColor));
				}
				break;
			case PropertyType.DLType:
				oldLineType[IFormat.DIAGONAL_LINE] = propertyTable.get(iType);
				oldLineColor[IFormat.DIAGONAL_LINE] = new Color(propertyTable.get(PropertyType.DLColor));
				break;
			case PropertyType.D2LType:
				oldLineType[IFormat.DIAGONAL2_LINE] = propertyTable.get(iType);
				oldLineColor[IFormat.DIAGONAL2_LINE] = new Color(propertyTable.get(PropertyType.D2LColor));
				break;
			case PropertyType.HLType:
				if (pos.getRow() == area.getStart().getRow()) {// 水平线需要添加最上边单元的下边框
					oldLineType[IFormat.BOTTOMLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.BOTTOMLINE] = new Color(propertyTable.get(PropertyType.HLColor));
				}

				if (pos.getRow() == area.getEnd().getRow()) {// 最下边单元的上边框
					oldLineType[IFormat.TOPLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.TOPLINE] = new Color(propertyTable.get(PropertyType.HLColor));
				}

				if (pos.getRow() > area.getStart().getRow() && pos.getRow() < area.getEnd().getRow()
						&& pos.getColumn() >= area.getStart().getColumn()
						&& pos.getColumn() <= area.getEnd().getColumn()) {// 非边缘边框的上下边线

					oldLineType[IFormat.TOPLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.TOPLINE] = new Color(propertyTable.get(PropertyType.HLColor));

					oldLineType[IFormat.BOTTOMLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.BOTTOMLINE] = new Color(propertyTable.get(PropertyType.HLColor));
				}
				break;
			case PropertyType.VLType:
				if (pos.getColumn() == area.getStart().getColumn()) {// 最左侧单元的右边线
					oldLineType[IFormat.RIGHTLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.RIGHTLINE] = new Color(propertyTable.get(PropertyType.VLColor));
				}

				if (pos.getColumn() == area.getEnd().getColumn()) {// 最右侧单元的左边线
					oldLineType[IFormat.LEFTLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.LEFTLINE] = new Color(propertyTable.get(PropertyType.VLColor));
				}

				if (pos.getRow() >= area.getStart().getRow() && pos.getRow() <= area.getEnd().getRow()
						&& pos.getColumn() > area.getStart().getColumn() && pos.getColumn() < area.getEnd().getColumn()) {// 非边缘边框的左右边线

					oldLineType[IFormat.LEFTLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.LEFTLINE] = new Color(propertyTable.get(PropertyType.VLColor));

					oldLineType[IFormat.RIGHTLINE] = propertyTable.get(iType);
					oldLineColor[IFormat.RIGHTLINE] = new Color(propertyTable.get(PropertyType.VLColor));
				}
				break;
			default:
				break;
			}
		}

		return CellLines.getInstance(oldLineType, oldLineColor);
	}

	private static Hashtable<Integer, Integer> getDefaultImageValue() {
		Hashtable<Integer, Integer> image_propertyCache = new Hashtable<Integer, Integer>();
		image_propertyCache.put(PropertyType.TLType, ICellAttr.DFType);
		image_propertyCache.put(PropertyType.TLColor, ICellAttr.DFColor);
		image_propertyCache.put(PropertyType.BLType, ICellAttr.DFType);
		image_propertyCache.put(PropertyType.BLColor, ICellAttr.DFColor);
		image_propertyCache.put(PropertyType.LLType, ICellAttr.DFType);
		image_propertyCache.put(PropertyType.LLColor, ICellAttr.DFColor);
		image_propertyCache.put(PropertyType.RLType, ICellAttr.DFType);
		image_propertyCache.put(PropertyType.RLColor, ICellAttr.DFColor);
		image_propertyCache.put(PropertyType.HLType, ICellAttr.DFType);
		image_propertyCache.put(PropertyType.HLColor, ICellAttr.DFColor);
		image_propertyCache.put(PropertyType.VLType, ICellAttr.DFType);
		image_propertyCache.put(PropertyType.VLColor, ICellAttr.DFColor);
		return image_propertyCache;
	}

	private static CellPosition[] getPosByAlign(AreaPosition area, int align, CellsModel cellsModel) {
		if (area == null) {
			return null;
		}
		List<CellPosition> areaPoses = cellsModel.getSeperateCellPos(area);
		ArrayList<CellPosition> poses = new ArrayList<CellPosition>();
		switch (align) {
		case ICellAttr.TOP_CENTER:
			for (CellPosition pos : areaPoses) {
				if (pos.getRow() == area.getStart().getRow() && pos.getColumn() > area.getStart().getColumn()
						&& pos.getColumn() < area.getEnd().getColumn()) {
					poses.add(pos);
				}
			}
			break;
		case ICellAttr.BOTTOM_CENTER:
			for (CellPosition pos : areaPoses) {
				if (pos.getRow() == area.getEnd().getRow() && pos.getColumn() > area.getStart().getColumn()
						&& pos.getColumn() < area.getEnd().getColumn()) {
					poses.add(pos);
				}
			}
			break;
		case ICellAttr.CENTER_CENTER:
			for (CellPosition pos : areaPoses) {
				if (pos.getRow() > area.getStart().getRow() && pos.getRow() < area.getEnd().getRow()
						&& pos.getColumn() > area.getStart().getColumn() && pos.getColumn() < area.getEnd().getColumn()) {
					poses.add(pos);
				}
			}
			break;
		case ICellAttr.LEFT_CENTER:
			for (CellPosition pos : areaPoses) {
				if (pos.getColumn() == area.getStart().getColumn() && pos.getRow() > area.getStart().getRow()
						&& pos.getRow() < area.getEnd().getRow()) {
					poses.add(pos);
				}
			}
			break;
		case ICellAttr.RIGHT_CENTER:
			for (CellPosition pos : areaPoses) {
				if (pos.getColumn() == area.getEnd().getColumn() && pos.getRow() > area.getStart().getRow()
						&& pos.getRow() < area.getEnd().getRow()) {
					poses.add(pos);
				}
			}
			break;
		case ICellAttr.TOP_LEFT:
			poses.add(area.getStart());
			break;
		case ICellAttr.TOP_RIGHT:
			poses.add(CellPosition.getInstance(area.getStart().getRow(), area.getEnd().getColumn()));
			break;
		case ICellAttr.BOTTOM_LEFT:
			poses.add(CellPosition.getInstance(area.getEnd().getRow(), area.getStart().getColumn()));
			break;
		case ICellAttr.BOTTOM_RIGHT:
			poses.add(area.getEnd());
			break;
		default:
			throw new IllegalArgumentException();
		}
		return poses.toArray(new CellPosition[0]);
	}

	static void clearTempCacheBeforeSave(AbsAnaReportModel anaModel) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel.getFormatModel());
		// 报表格式更改时，清除区域备份
		exModel.removeRepLevelInfo(KEY_ORIALAREAMAP);
		exModel.removeRepLevelInfo(KEY_ORIALFORMULA);
		exModel.removeRepLevelInfo(KEY_CURR_EXCELLS);
	}

	static void clearTempCacheBeforeSave(CellsModel cellsModel) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(cellsModel);
		// 报表格式更改时，清除区域备份
		exModel.removeRepLevelInfo(KEY_ORIALAREAMAP);
		exModel.removeRepLevelInfo(KEY_ORIALFORMULA);
		exModel.removeRepLevelInfo(KEY_CURR_EXCELLS);
	}
	
	static boolean showOriginalFields(String areaPK, AbsAnaReportModel anaModel) {
		CellsModel formatModel = anaModel.getFormatModel();
		// 获取原始扩展区
		ExtendAreaCell exCell = getExCellFromCache(areaPK, formatModel);

		AreaPosition newArea = exCell.getArea();
		if (newArea == null) {
			return false;
		}
		// 处理冲突(新区域如果已经定义了扩展区，需要将这些扩展区进行平移)
		doAreaBlock(newArea, anaModel, exCell);

		setExCell(0, 0, exCell, anaModel);

		// 同步扩展区数据
		ExtendAreaModel.getInstance(formatModel).syncExtendAreaCellData(exCell);
		anaModel.getFormatModel().setDirty(false);
		return true;
	}

	@SuppressWarnings("unchecked")
	private static void cacheCurrExCell(String exAreaPk, CellsModel formatModel) {
		if (exAreaPk == null) {
			return;
		}
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		// @edit by ll at 2010-10-20,上午10:35:59 增加区域备份处理
		HashMap<String, ExtendAreaCell> orialAreaMap = (HashMap<String, ExtendAreaCell>) exModel
				.getRepLevelInfo(KEY_CURR_EXCELLS);
		if (orialAreaMap == null) {
			orialAreaMap = new HashMap<String, ExtendAreaCell>();
			exModel.addRepLevelInfo(KEY_CURR_EXCELLS, orialAreaMap);
		}
		if (!orialAreaMap.containsKey(exAreaPk)) {
			orialAreaMap.put(exAreaPk, (ExtendAreaCell)exModel.getExAreaByPK(exAreaPk).clone());
		}
		AreaFormulaModel formula = (AreaFormulaModel) exModel.getRepLevelInfo(KEY_CURR_FORMULA);//
		if (formula == null)
			exModel.addRepLevelInfo(KEY_CURR_FORMULA, AreaFormulaModel.getInstance(formatModel).clone());
	}
	
	@SuppressWarnings("unchecked")
	static void clearExCellFromCache(String exAreaPk, AbsAnaReportModel reportmodel) {
		if (exAreaPk == null) {
			return;
		}
		CellsModel formatModel = reportmodel.getFormatModel();
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		HashMap<String, ExtendAreaCell> orialAreaMap = (HashMap<String, ExtendAreaCell>) exModel.getRepLevelInfo(KEY_CURR_EXCELLS);
		if (orialAreaMap == null) {
			orialAreaMap = new HashMap<String, ExtendAreaCell>();
			exModel.addRepLevelInfo(KEY_CURR_EXCELLS, orialAreaMap);
		} else {
			orialAreaMap.remove(exAreaPk);
		}
		exModel.removeRepLevelInfo(KEY_CURR_FORMULA);
	}

	@SuppressWarnings("unchecked")
	private static ExtendAreaCell getExCellFromCache(String exAreaPk, CellsModel formatModel) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);

		HashMap<String, ExtendAreaCell> orialAreaMap = (HashMap<String, ExtendAreaCell>) exModel
				.getRepLevelInfo(KEY_CURR_EXCELLS);
		if (orialAreaMap.containsKey(exAreaPk)) {
			return (ExtendAreaCell) orialAreaMap.get(exAreaPk).clone();
		}
		return null;
	}
	static boolean setCombinedArea(AreaPosition area, IFormat format, Object value, AbsAnaReportModel anaModel) {
		return setCombinedArea( area,  format,  value,  anaModel.getFormatModel());
	}

	/**
	 * 设置数据依赖
	 * add by yuyangi
	 * @param dataRelation
	 * @param anaModel
	 * @return
	 */
	static boolean setDataRelation(AreaDataRelation dataRelation, AbsAnaReportModel anaModel) {

		boolean setRelation = true;// 设置数据依赖
		CellPosition pos = anaModel.getCellsModel()
				.getSelectModel().getAnchorCell();
		CellPosition[] dataStateCells = getFormatPoses(new AreaPosition[] { AreaPosition
				.getInstance(pos, pos) }, anaModel);
		if (dataStateCells != null && dataStateCells.length > 0)
		{
			pos = dataStateCells[0];
		}
		// 当前选择点必须是一个数据区域
		ExtendAreaCell exCell = getFormatAnchorExCell(anaModel);
		if (exCell == null
				|| exCell.getAreaInfoSet().getSmartModelDefID() == null)
			return false;
		String areaPK = exCell.getBaseInfoSet().getExAreaPK();

		// 从报表格式中获取其他数据区域
		ArrayList<ExtendAreaCell> al_areas = new ArrayList<ExtendAreaCell>();
		ExtendAreaCell[] cells = getAllCanSetExCells(anaModel);
		if (cells != null && cells.length > 1)
		{
			for (ExtendAreaCell exArea : cells)
			{
				if (exArea.getBaseInfoSet().getExAreaPK().equals(areaPK))
					continue;
				if (exArea.getAreaInfoSet().getSmartModelDefID() == null)
					continue;
				al_areas.add(exArea);
			}
		}
		if (al_areas.size() == 0)
		{
			return false;
		}

		DataRelation dataRela = DataRelation.getInstance(ExtendAreaModel
				.getInstance(anaModel.getFormatModel()));
		AreaDataRelation oldRela = dataRela.getAreaRelation(areaPK);
		if (setRelation)
		{
			AreaDataRelation relation = dataRelation;
			dataRela.setAreaRelation(areaPK, relation);
//			anaModel.getFormatModel().setDirty(false);
		} else
		{
			if (oldRela == null){
				return false;
			}
			dataRela.setAreaRelation(areaPK, null);
//			anaModel.getFormatModel().setDirty(false);
		}

		return true;
	}

	/**
	 * 获得所用能设置依赖关系的扩展区域
	 *
	 * @create by wanyonga at 2010-8-25,上午10:26:13
	 *
	 * @param des
	 * @return
	 */
	private static ExtendAreaCell[] getAllCanSetExCells(AbsAnaReportModel anaModel)
	{
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel.getFormatModel());
		return exModel.getExtendAreaCells();
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private static CellPosition[] getFormatPoses(AreaPosition[] areas, AbsAnaReportModel anaModel) {
		ArrayList<CellPosition> allCells = new ArrayList<CellPosition>();
		boolean isFormat = anaModel.isFormatState();
		if (isFormat) {
			for (AreaPosition area : areas) {
				List<CellPosition> cells = anaModel.getCellsModel().getSeperateCellPos(area);
				allCells.addAll(cells);
			}
			if (allCells.size() > 0)
				return allCells.toArray(new CellPosition[0]);
			return null;
		}

		ExtDataModel cpRefModel = (ExtDataModel) anaModel.getCellsModel().getExtProp(
				ExtendAreaConstants.CELLPOS_REFER_MAP);
		if (cpRefModel == null)
			return null;
		Hashtable<CellPosition, CellPosition> cpRef = (Hashtable<CellPosition, CellPosition>) cpRefModel.getValue();
		if (cpRef == null)
			return null;
		for (AreaPosition area : areas) {
			List<CellPosition> cells = anaModel.getCellsModel().getSeperateCellPos(area);
			for (CellPosition pos : cells) {
				if (cpRef.containsKey(pos))
					allCells.add(cpRef.get(pos));
			}
		}
		if (allCells.size() > 0)
			return allCells.toArray(new CellPosition[0]);
		return null;
	}

	private static ExtendAreaCell getFormatAnchorExCell(AbsAnaReportModel anaModel)
	{
		CellPosition fmtPos = getFormatAnchorCellPosition(anaModel);
		if (fmtPos != null)
		{
			ExtendAreaCell[] exCells = getAllCanAnaExCells(anaModel);
			for (ExtendAreaCell cell : exCells)
			{
				if (cell.getArea().contain(fmtPos))
				{
					return cell;
				}
			}
		}
		return null;
	}

	private static CellPosition getFormatAnchorCellPosition(AbsAnaReportModel anaModel)
	{
		CellPosition formatPos = null;
		if (anaModel != null)
		{
			CellsModel fmtModel = anaModel.getFormatModel();
			if (anaModel.isFormatState())
			{
				formatPos = fmtModel.getSelectModel().getAnchorCell();
			} else
			{
				formatPos = getFormatPos(anaModel
						.getDataModel(), anaModel
						.getDataModel().getSelectModel().getAnchorCell(), anaModel);
			}
		}
		return formatPos;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private static CellPosition getFormatPos(CellsModel model, CellPosition selPos, AbsAnaReportModel anaModel) {
		if (anaModel.isFormatState()) {
			return selPos;
		} else {
			ExtDataModel cpRefModel = (ExtDataModel) model.getExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP);
			if (cpRefModel == null) {
				return null;
			}
			Hashtable<CellPosition, CellPosition> cpRef = (Hashtable<CellPosition, CellPosition>) cpRefModel.getValue();
			if (cpRef != null && cpRef.containsKey(selPos)) {
				CellPosition pos = cpRef.get(selPos);

				return pos;
			}
		}
		return null;
	}

	private static ExtendAreaCell[] getAllCanAnaExCells(AbsAnaReportModel anaModel) {
		return ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExtendAreaCells();
	}

	/**
	 * 合并单元
	 * @param area
	 * @param format
	 * @param value
	 * @param formatModel
	 * @return
	 */
	private static boolean setCombinedArea(AreaPosition area, IFormat format, Object value, CellsModel formatModel) {
		CombinedAreaModel combModel = CombinedAreaModel.getInstance(formatModel);
		combModel.addCombinedCell(new CombinedCell(area, format, value));
		return true;
	}

	/**
	 * 常用格式
	 * @param styleType
	 * @param pos
	 * @param cellsModel
	 * @param newFormat
	 * @return
	 */
	private static IFormat getCommonFormat(CellPosition pos, CellsModel cellsModel, String styleType) {
		IFormat format = null;
		IFormat oldFormat = cellsModel.getCellFormatIfNullNew(pos);
		// 格式
		format = CommonStyleUtil.getFormat(oldFormat.getDataFormat(), styleType);
		return format;
	}

	/**
	 * 获取扩展区域上的Field
	 * @param anaModel
	 * @param exAreaPk
	 * @return
	 */
	static Field[] getExtendAreaFields(AbsAnaReportModel anaModel, String exAreaPk) {
		ExtendAreaCell exCell = getExCellFromCache(exAreaPk, anaModel.getFormatModel());
		Field[] flds = getFieldArrByType(anaModel.getFormatModel(), exCell);
		return flds;
	}

	private static AnaRepField getAnaFieldByPos(CellsModel formatModel, ExtendAreaCell exCell, CellPosition fmtPos) {
		Cell fmtCell = formatModel.getCell(fmtPos);
		CombinedCell[] combinedCells = CombinedAreaModel.getInstance(formatModel).getContainCombinedCells(
				exCell.getArea());
		if (combinedCells != null && combinedCells.length > 0) {
			for (CombinedCell cc : combinedCells) {
				if (cc.getArea().contain(fmtPos)) {
					// 合并单元只考虑首单元
					if (!cc.getArea().getStart().equals(fmtPos)) {
						return null;
					}
				}
			}
		}
		if (fmtCell != null && fmtCell.getExtFmt(ExtendAreaConstants.FIELD_INFO) != null) {
			return (AnaRepField) fmtCell.getExtFmt(ExtendAreaConstants.FIELD_INFO);
		}
		return null;
	}

	private static Field[] getFieldArrByType(CellsModel formatModel, ExtendAreaCell exCell) {
		CellPosition[] posArr = exCell.getArea().split();
		AnaRepField anaField = null;
		Field field = null;
		List<Field> fieldList = new ArrayList<Field>();
		for (CellPosition fmtPos : posArr) {
			anaField = getAnaFieldByPos(formatModel, exCell, fmtPos);
			if(anaField != null) {
				field = anaField.getField();
				if(field instanceof FieldCountDef) {
					field = ((FieldCountDef)field).getMainField();
				}
				if(!fieldList.contains(field)) {
					fieldList.add(field);
				}
			}
		}
		return fieldList.toArray(new Field[0]);
	}

	private static StringBuffer addFormula(AreaFieldSet fieldSet, AbsAnaReportModel anaModel, IArea temp) {
		AreaFormulaModel formulaMode = AreaFormulaModel.getInstance(anaModel.getFormatModel());
		AreaFmlExecutor fmlExe = formulaMode.getAreaFmlExecutor();
		if(fmlExe == null) {
			fmlExe = new AreaFmlExecutor(anaModel.getContext(),anaModel.getFormatModel());
		}
		fmlExe.getParserProxy().getEnv()
				.setExEnv(CommonExprCalcEnv.EXPR_EXEC_TIMEZONE,
						anaModel.getContext().getAttribute(FreeReportContextKey.REPORT_EXEC_TIMEZONE));
		String formula = fieldSet.getFormula();
		StringBuffer showMessage = new StringBuffer();
		if(formula != null) {
			try {
				// CVS/CVN公式
				CVSBatchFuncDriver driver2 = (CVSBatchFuncDriver) fmlExe.getFuncListInst().getExtDriver(
						CVSBatchFuncDriver.class.getName());
				if (driver2 == null) {
					driver2 = new CVSBatchFuncDriver(anaModel);
					fmlExe.registerFuncDriver(driver2);
				}
				// 按字段取值公式
				GetFuncDriver getDriver = (GetFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						GetFuncDriver.class.getName());
				if(getDriver == null) {
					getDriver = new GetFuncDriver(anaModel);
					fmlExe.registerFuncDriver(getDriver);
				}
				// 变量公式驱动
				VarFuncDriver varFuncDriver = (VarFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						VarFuncDriver.class.getName());
				if(varFuncDriver == null) {
					varFuncDriver = new VarFuncDriver(anaModel);
					fmlExe.registerFuncDriver(varFuncDriver);
				}
				
				// nc数据格式公式驱动
				NcDataFormatFuncDriver ncDataFormatFuncDriver = (NcDataFormatFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						NcDataFormatFuncDriver.class.getName());
				if(ncDataFormatFuncDriver == null) {
					ncDataFormatFuncDriver = new NcDataFormatFuncDriver(anaModel);
					fmlExe.registerFuncDriver(ncDataFormatFuncDriver);
				}
				
				// nc数据格式公式驱动
				SalaryDecryptFuncDriver salaryDecryptFuncDriver = (SalaryDecryptFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						SalaryDecryptFuncDriver.class.getName());
				if(salaryDecryptFuncDriver == null) {
					salaryDecryptFuncDriver = new SalaryDecryptFuncDriver(anaModel);
					fmlExe.registerFuncDriver(salaryDecryptFuncDriver);
				}
				fmlExe.addDbDefFormula(showMessage, temp, formula, null, false);
//				formulaHandler.addDbDefFormula(showMessage, temp, formula, null, false);
			} catch (ParseException e) {
				AppDebug.debug(e);
			}
		}
		return showMessage;
	}


	static StringBuffer addFormula(String formula, AbsAnaReportModel anaModel, IArea temp) {
		AreaFormulaModel formulaMode = AreaFormulaModel.getInstance(anaModel.getFormatModel());
		AreaFmlExecutor fmlExe = formulaMode.getAreaFmlExecutor();
		if(fmlExe == null) {
			fmlExe = new AreaFmlExecutor(anaModel.getContext(),anaModel.getFormatModel());
		}
		fmlExe.getParserProxy().getEnv().setExEnv(CommonExprCalcEnv.EXPR_EXEC_TIMEZONE,
				anaModel.getContext().getAttribute(FreeReportContextKey.REPORT_EXEC_TIMEZONE));
		StringBuffer showMessage = new StringBuffer();
		if(formula != null) {
			try {
				// CVS/CVN公式
				CVSBatchFuncDriver driver2 = (CVSBatchFuncDriver) fmlExe.getFuncListInst().getExtDriver(
						CVSBatchFuncDriver.class.getName());
				if (driver2 == null) {
					driver2 = new CVSBatchFuncDriver(anaModel);
					fmlExe.registerFuncDriver(driver2);
				}
				// 按字段取值公式
				GetFuncDriver getDriver = (GetFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						GetFuncDriver.class.getName());
				if(getDriver == null) {
					getDriver = new GetFuncDriver(anaModel);
					fmlExe.registerFuncDriver(getDriver);
				}
				// 变量公式驱动
				VarFuncDriver varFuncDriver = (VarFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						VarFuncDriver.class.getName());
				if(varFuncDriver == null) {
					varFuncDriver = new VarFuncDriver(anaModel);
					fmlExe.registerFuncDriver(varFuncDriver);
				}
				// nc数据格式公式驱动
				NcDataFormatFuncDriver ncDataFormatFuncDriver = (NcDataFormatFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						NcDataFormatFuncDriver.class.getName());
				if(ncDataFormatFuncDriver == null) {
					ncDataFormatFuncDriver = new NcDataFormatFuncDriver(anaModel);
					fmlExe.registerFuncDriver(ncDataFormatFuncDriver);
				}
				
				//添加薪资解密函数  add by ward 2018-06-05
				SalaryDecryptFuncDriver salaryDecryptFuncDriver = (SalaryDecryptFuncDriver)fmlExe.getFuncListInst().getExtDriver(
						SalaryDecryptFuncDriver.class.getName());
				if(salaryDecryptFuncDriver == null) {
					salaryDecryptFuncDriver = new SalaryDecryptFuncDriver(anaModel);
					fmlExe.registerFuncDriver(salaryDecryptFuncDriver);
				}
				
				fmlExe.addDbDefFormula(showMessage, temp, formula, null, false);
			} catch (ParseException e) {
				AppDebug.debug(e);
			}
		}
		return showMessage;
	}


	/**
	 * 获取上一级次字段的编码，code_l3-->code_l2
	 * @param rangeField
	 * @return
	 */
	static String getPreRangeField(Field rangeField) {
		String preRangeField = null;
		String rangeString = null;
		String lvl = "";
		if(rangeField == null) {
			return preRangeField;
		}
		rangeString = rangeField.getFldname();
		if(rangeString.indexOf("_") > 0) {
			lvl = rangeString.substring(rangeString.indexOf("_") + 2, rangeString.length());
			if(lvl == null || "".equals(lvl)) {
				rangeString = rangeString + "1";
			} else {
				int intLvl = Integer.parseInt(lvl);
				int preLvl = intLvl;
				if(intLvl > 1) {
					preLvl = intLvl - 1;
				}

				preRangeField = rangeString.substring(0, rangeString.indexOf("_")) + "_l" + preLvl;
			}
		}
		return preRangeField;
	}

	/**
	 * 替换GetTotal('field',countType,'rangeField')公式中的第三个参数
	 * 级次汇总时，需要将code 换成 code_l1形式的字段
	 * @param s
	 * @param oraCode
	 * @param newCode
	 * @return
	 */
	static String replaceGetTotalFormula(String s, String oldCode, String newCode) {
//		if(s.toLowerCase().contains("gettotal")) {
//			s = FormulaChangeHelper.replaceParam2(s, "GetTotal", 2, oldCode, newCode);
//		}

		return s;
	}

	static String replaceGetFormula(String s, String oldCode, String newCode) {
		if(s.toLowerCase().contains("getfield")) {
			s = FormulaChangeHelper.replaceParam2(s, "GetField", 0, oldCode, newCode);
		}
		return s;
	}

	static void delRow(int row, AbsAnaReportModel anaModel) {
		if(anaModel == null) {
			return;
		}
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel.getFormatModel());
		ExtendAreaCell[] exCells = exModel.getExtendAreaCells();
		if(exCells != null) {
			for(ExtendAreaCell exCell : exCells) {
				if(exCell.getAreaInfoSet().isCrossSet()) {
					AreaCrossSetInfo areaCrossSetInfo = exCell.getAreaInfoSet().getCrossSetInfo();
					if(row < exCell.getArea().getStart().getRow()) {
						if(areaCrossSetInfo != null) {
							AreaPosition newCrossArea = (AreaPosition)areaCrossSetInfo.getCrossArea().getMoveArea(-1, 0);
							areaCrossSetInfo.crossMoveArea(areaCrossSetInfo, newCrossArea, exCell.getArea());
						}
					} else if(row < exCell.getAreaInfoSet().getCrossSetInfo().getCrossPoint().getRow()) {
						if(areaCrossSetInfo != null) {
							areaCrossSetInfo.moveAnaCrossSet(false, true, row, 1);
						}
					}
				}
			}
		}
		CellsModel cModel = anaModel.getFormatModel();
		//目前不支持多选区域。
		ReportVarsModel repVarModel = ReportVarsModel.getInstance(cModel);
		AreaFormulaModel formulaMode = AreaFormulaModel.getInstance(anaModel.getFormatModel());
		AreaFmlExecutor fmlExe = formulaMode.getAreaFmlExecutor();
		if(fmlExe == null) {
			fmlExe = new AreaFmlExecutor(anaModel.getFormatModel());
		}
		// CVS/CVN公式
		CVSBatchFuncDriver driver2 = (CVSBatchFuncDriver) fmlExe.getFuncListInst().getExtDriver(
				CVSBatchFuncDriver.class.getName());
		if (driver2 == null) {
			driver2 = new CVSBatchFuncDriver(anaModel);
			fmlExe.registerFuncDriver(driver2);
		}
		// 按字段取值公式
		GetFuncDriver getDriver = (GetFuncDriver)fmlExe.getFuncListInst().getExtDriver(
				GetFuncDriver.class.getName());
		if(getDriver == null) {
			getDriver = new GetFuncDriver(anaModel);
			fmlExe.registerFuncDriver(getDriver);
		}
		// 变量公式驱动
		VarFuncDriver varFuncDriver = (VarFuncDriver)fmlExe.getFuncListInst().getExtDriver(
				VarFuncDriver.class.getName());
		if(varFuncDriver == null) {
			varFuncDriver = new VarFuncDriver(anaModel);
			fmlExe.registerFuncDriver(varFuncDriver);
		}
		// nc数据格式公式驱动
		NcDataFormatFuncDriver ncDataFormatFuncDriver = (NcDataFormatFuncDriver)fmlExe.getFuncListInst().getExtDriver(
				NcDataFormatFuncDriver.class.getName());
		if(ncDataFormatFuncDriver == null) {
			ncDataFormatFuncDriver = new NcDataFormatFuncDriver(anaModel);
			fmlExe.registerFuncDriver(ncDataFormatFuncDriver);
		}
		formulaMode.setAreaFmlExecutor(fmlExe);
		
		CellPosition[] cps = repVarModel.getCellsWithVar();
		if(cps != null) {
			for(CellPosition cell : cps) {
				if(cell.getRow() == row) {
					repVarModel.removeVarFromCell(cell);
					fmlExe.clearFormula(cell);
				}
			}
		}
		cModel.getRowHeaderModel().removeHeader(row, 1);
		//	清除选中区域
		cModel.getSelectModel().clear();
		for(ExtendAreaCell exCell : exCells) {
			clearAreaDataModel(exCell, anaModel);
			exModel.syncExtendAreaCellData(exCell);
		}
	}

	/**
	 * 移除隐藏字段
	 * @param anaModel
	 * @param exAreaPk
	 * @param flds
	 */
	static void removeAreaHideFields(AbsAnaReportModel anaModel, String exAreaPk, Field[] flds) {
		if(flds == null) {
			return ;
		}
		ExtendAreaCell selectExCell = ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExAreaByPK(exAreaPk);
		FreeAreaHideFields hideFields = (FreeAreaHideFields)
		selectExCell.getAreaInfoSet().getAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
		if(hideFields == null) {
			hideFields = new FreeAreaHideFields();
			selectExCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS, hideFields);
		}
		Field[] details = hideFields.getHideFlds();
		Field[] aggrs = hideFields.getHideAggrFlds();
		int len = flds.length;
		for(int i = 0;i < len;i++) {
			if(flds[i] instanceof FieldCountDef) {
				details = (Field[])ArrayUtils.removeElement(details, flds[i]);
			} else {
				aggrs = (Field[])ArrayUtils.removeElement(aggrs, flds[i]);
			}
		}
		hideFields.setHideAggrFlds(aggrs);
		hideFields.setHideFlds(details);
	}
	
	/**
	 * 删除所有的隐藏字段
	 * @param anaModel
	 * @param exAreaPk
	 */
	static void removeAllAreaHideFields(AbsAnaReportModel anaModel, String exAreaPk) {
//		ExtendAreaCell selectExCell = getSetExCell(exAreaPk, anaModel.getFormatModel());
		ExtendAreaCell selectExCell = ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExAreaByPK(exAreaPk);
		if(selectExCell == null) {
			return ;
		}
		selectExCell.getAreaInfoSet().removeAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
	}
	
	/**
	 * 增加格式隐藏字段，通过此方法设置的隐藏字段在格式上不展示，但是会在查询时此部分数据
	 * @param anaModel
	 * @param exAreaPk
	 * @param flds 增加明显隐藏字段
	 * @param aggrFlds 增加汇总的隐藏字段
	 */
	static void addAreaHideFields(AbsAnaReportModel anaModel, String exAreaPk, Field[] flds, Field[] aggrFlds) {
		if(flds == null && aggrFlds == null) {
			return ;
		}
//		ExtendAreaCell selectExCell = getSetExCell(exAreaPk, anaModel.getFormatModel());
		ExtendAreaCell selectExCell = ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExAreaByPK(exAreaPk);
		FreeAreaHideFields hideFields = (FreeAreaHideFields)
		selectExCell.getAreaInfoSet().getAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
		if(hideFields == null) {
			hideFields = new FreeAreaHideFields();
			selectExCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS, hideFields);
		}
		Field[] details = hideFields.getHideFlds();
		Field[] aggrs = hideFields.getHideAggrFlds();
		if(flds != null && flds.length > 0) {
			if(details == null) {
				details = new Field[0];
			}
			details = (Field[])ArrayUtils.addAll(details, flds);
		}
		if(aggrFlds != null && aggrFlds.length != 0) {
			if(aggrs == null) {
				aggrs = new Field[0];
			}
			aggrs = (Field[])ArrayUtils.addAll(aggrs, aggrFlds);
		}
		hideFields.setHideAggrFlds(aggrs);
		hideFields.setHideFlds(details);
	}
	
	static Field[] getHiddenAggrFields(AbsAnaReportModel anaModel, String exAreaPk) {
		ExtendAreaCell selectExCell = ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExAreaByPK(exAreaPk);
		FreeAreaHideFields hideFields = (FreeAreaHideFields)
		selectExCell.getAreaInfoSet().getAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
		if(hideFields == null) {
			hideFields = new FreeAreaHideFields();
			selectExCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS, hideFields);
		}
		Field[] aggrs = hideFields.getHideAggrFlds();
		return aggrs;
	}
	
	static Field[] getHiddenFields(AbsAnaReportModel anaModel, String exAreaPk) {
		ExtendAreaCell selectExCell = ExtendAreaModel.getInstance(anaModel.getFormatModel()).getExAreaByPK(exAreaPk);
		FreeAreaHideFields hideFields = (FreeAreaHideFields)
		selectExCell.getAreaInfoSet().getAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
		if(hideFields == null) {
			hideFields = new FreeAreaHideFields();
			selectExCell.getAreaInfoSet().addAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS, hideFields);
		}
		Field[] flds = hideFields.getHideFlds();
		return flds;
	}
	
	/**
	 * <p>按页维度打印工具</p>
	 * @param anaModel
	 * @param areaPK
	 * @param addGroupFlds
	 * @param removeGroupFlds
	 * @param pageBreaks
	 * @param fromOriginalFormat
	 * @return 
	 */
	static boolean setGroupPageBreakers(AbsAnaReportModel anaModel,String areaPK, Field[] addGroupFlds, 
			   Field[] removeGroupFlds, Field[] pageBreaks, boolean fromOriginalFormat) {
		CellsModel formatModel = anaModel.getFormatModel();
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		ExtendAreaCell exCell = null;
		if(fromOriginalFormat)
			exCell = getSetExCell(areaPK, formatModel);
		else
			exModel.getExAreaByPK(areaPK);
		CellPosition[] posArr = exCell.getArea().split();
		AnaRepField anaField = null;
		Field field = null;
		for (CellPosition fmtPos : posArr) {
			anaField = getAnaFieldByPos(formatModel, exCell, fmtPos);
			if(anaField != null) {
				field = anaField.getField();
				if(field != null) {
					if(ArrayUtils.contains(addGroupFlds, field)) {
						if(anaField.getFieldType() != AnaRepField.TYPE_GROUP_FIELD) {
							anaField.setFieldType(AnaRepField.TYPE_GROUP_FIELD);
						}
					}
					if(ArrayUtils.contains(removeGroupFlds, field)) {
						if(anaField.getFieldType() == AnaRepField.TYPE_GROUP_FIELD) {
							anaField.setFieldType(AnaRepField.TYPE_DETAIL_FIELD);
						}
					}
					if(ArrayUtils.contains(pageBreaks, field)) {
						anaField.getDimInfo().setPageBreak(new FreePageBreak());
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * <p>将扩展区恢复到原始格式</p>
	 * @param reportModel
	 * @param exAreaPk 
	 */
	static void resetExArea(AbsAnaReportModel reportModel, String exAreaPk) {
		ExtendAreaCell oldexCell= getSetExCell(exAreaPk, reportModel.getFormatModel());
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(reportModel.getFormatModel());
		AreaPosition area = exModel.getExAreaByPK(oldexCell.getExAreaPK()).getArea();
		exModel.removeExArea(exModel.getExAreaByPK(oldexCell.getExAreaPK()));
		clearAll(area, reportModel);
		setExAreaByExCell(reportModel, oldexCell);
		exModel.addExArea(oldexCell);
		
		// 同步扩展区数据
		exModel.syncExtendAreaCellData(oldexCell);
		oldexCell.getAreaInfoSet().setDataModel(null);// 新区域的数据模型全新自动生成。
		reportModel.getFormatModel().setDirty(false);
	}
	
	/**清除所有，包括单元格背景色啥的
	 * 此方法与上面的clear()基本一致，可考虑删除掉一个zhongkm
	 */
	static void clearAll(AreaPosition area, AbsAnaReportModel anaModel){
		CellsModel formatModel = anaModel.getFormatModel();
		List<CellPosition> cells = anaModel.getFormatModel().getSeperateCellPos(area);

		AnaReportFieldUtil.removeFlds(anaModel, cells
				.toArray(new CellPosition[0]));
		for (CellPosition pos : cells)
		{
			Cell cell = formatModel.getCell(pos);
			if (cell != null)
			{
				formatModel.setCell(pos.getRow(), pos.getColumn(), null);
				//formatModel.setCellValue(pos, null);
				AreaFormulaModel.getInstance(anaModel.getFormatModel()).removeMainRelatedFml(pos);

			}
		}
		ChartManager.clearAreaChart(formatModel, area);
		CombinedAreaModel.getInstance(formatModel).removeCombinedCell(area);
	}
	
	static void setExAreaByExCell(AbsAnaReportModel reportModel, ExtendAreaCell oldexCell) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(reportModel.getFormatModel());
		for(CellPosition pos : oldexCell.getArea().split()) {
			exModel.fillCellInfo(oldexCell, pos, pos);
		}
		List<CombinedCell> combList = oldexCell.getAreaInfoSet().getAllCombinedCell();
		for(CombinedCell combCell : combList) {
			reportModel.getFormatModel().combineCell(combCell.getArea());
		}
	}

	static Field[] getHiddenGroupField(AbsAnaReportModel reportModel, ExtendAreaCell exCell) {
		FreeAreaHideFields hideFields = (FreeAreaHideFields)
		exCell.getAreaInfoSet().getAreaLevelInfo(FreeAreaHideFields.KEY_HIDE_FIELDS);
		if(hideFields == null) {
			return null;
		}
		return hideFields.getHideAggrFlds();
	}
	
	/**
	 * 设置分组顺序
	 * 
	 * @param groupFlds
	 * @param reportModel
	 * @param exAreaPk
	 */
	static void setGroupOrder(Field[] groupFlds, AbsAnaReportModel reportModel, String exAreaPk) {
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(reportModel.getFormatModel());
		ExtendAreaCell exCell = exModel.getExAreaByPK(exAreaPk);
		AreaDataModel dataModel = (AreaDataModel)exCell.getAreaInfoSet().getDataModel();
		FreeFieldConverter converter = null;
		if(dataModel != null) {
			converter = dataModel.getAreaFields(false).getFieldConverter();
		}
		if(converter == null) {
			converter = (FreeFieldConverter)exCell.getAreaInfoSet().getAreaLevelInfo(FreeFieldConverter.class.getName());
		}
		if(converter == null) {
			converter = new FreeFieldConverter();
			exCell.getAreaInfoSet().addAreaLevelInfo(FreeFieldConverter.class.getName(), converter);
		}
		ArrayList<GroupItem> grpList = new ArrayList<GroupItem>();
		for(Field fld : groupFlds) {
			GroupItem item = new GroupItem();
			item.setField(converter.getField(fld.getFldname()));
			grpList.add(item);
		}
		if(grpList.size() > 0) {
			exCell.getAreaInfoSet().addAreaLevelInfo(FreePrivateContextKey.USER_OR_DEFAULT, true);
			AggrDescriptor desc = new AggrDescriptor();
			desc.setGroupFields(grpList.toArray(new GroupItem[0]));
			exCell.getAreaInfoSet().addAreaLevelInfo(FreePrivateContextKey.USER_DEFINE_GROUP_ORDER, desc);
		}
	}
	
}