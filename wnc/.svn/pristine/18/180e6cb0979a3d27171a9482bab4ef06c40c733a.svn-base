package uap.pub.bq.report.complex.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.pub.smart.model.descriptor.SortDescriptor;
import nc.pub.smart.model.descriptor.SortItem;

import org.apache.commons.lang.StringUtils;

import uap.pub.bq.report.complex.base.FrCpxConsts;
import uap.pub.bq.report.complex.base.FrCpxDimensionSort;
import uap.pub.bq.report.complex.base.FrCpxFieldValue;
import uap.pub.bq.report.complex.base.IFrCpxUniqueValue;
import uap.pub.bq.report.complex.calculate.FieldDimMemberResultInfo;
import uap.pub.bq.report.complex.calculate.FieldDimensionResultInfo;
import uap.pub.bq.report.complex.calculate.FieldsRelationInfo;
import uap.pub.bq.report.complex.calculate.FrCpxResultDataSet;
import uap.pub.bq.report.complex.calculate.FrCpxUserDimExpreInfo;
import uap.pub.bq.report.complex.datasource.FrCpxDimMember;
import uap.pub.bq.report.complex.datasource.FrCpxDimension;
import uap.pub.bq.report.complex.datasource.FrCpxFieldDimMember;
import uap.pub.bq.report.complex.datasource.FrCpxFieldDimension;
import uap.pub.bq.report.complex.datasource.FrCpxUserDimension;
import uap.pub.bq.report.complex.model.FrCpxCountDef;
import uap.pub.bq.report.complex.model.FrCpxCountDefSet;
import uap.pub.bq.report.complex.model.FrCpxDimensionItem;
import uap.pub.bq.report.complex.model.FrCpxDimensionSet;
import uap.pub.bq.report.complex.model.FrCpxMeasureItem;
import uap.pub.bq.report.complex.model.FrCpxModel;
import uap.pub.bq.report.complex.utils.FrCpxExpressionUtils;
import uap.pub.bq.report.complex.utils.FrCpxUtils;

import com.ufida.dataset.IContext;
import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.areafomula.AbsFunc;
import com.ufida.iufo.table.areafomula.AreaFuncDriver;
import com.ufida.iufo.table.areafomula.BIAreaFormulaHandler;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.ColValueCondFunc;
import com.ufida.iufo.table.areafomula.FormatAddressFunc;
import com.ufida.iufo.table.areafomula.GetFunc;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.GetTotalFunc;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.iufo.table.exarea.AreaCrossSetInfo;
import com.ufida.iufo.table.exarea.ExDataDescriptor;
import com.ufida.iufo.table.exarea.ExtendAreaConstants;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.iufo.table.exarea.SimplePinyinComparator;
import com.ufida.iufo.table.exarea.memoryorder.MemoryOrderMngUtil;
import com.ufida.report.anareport.areaset.AreaContentSet;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaReportModel;
import com.ufida.report.anareport.util.FreeFieldConverter;
import com.ufida.report.free.plugin.param.SerialNumberFunc2;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.zior.perfwatch.PerfWatch;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.report.constant.PropertyType;
import com.ufsoft.script.AreaFormulaUtil;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.FormulaVO;
import com.ufsoft.script.base.IParsed;
import com.ufsoft.script.exception.ParseException;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.Cell;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.CombinedAreaModel;
import com.ufsoft.table.CombinedCell;
import com.ufsoft.table.ExtDataModel;
import com.ufsoft.table.IArea;
import com.ufsoft.table.TableUtilities;
import com.ufsoft.table.format.IFormat;
import com.ufsoft.table.format.TableConstant;
import com.ufsoft.table.format.condition.ConditionFormatModel;
import com.ufsoft.table.format.condition.IAreaCondition;

public class FrCpxDataModelUtil {

	private FrCpxExtAreaCell frCpxAreaCell = null;
	private FrCpxResultDataSet cpxQueryResult = null;
	private IContext context = null;
	private CellsModel formatModel = null;
	private FrCpxExtAreaCell frCpxAreaCellFormat = null;
	private Hashtable<CellPosition, CellPosition> m_cpRef = null;
	private Hashtable<String, CellPosition> m_dimMeaPosRefFmt = null;
	private BIAreaFormulaHandler m_BIAreaFormulaHandler = null;

	public FrCpxDataModelUtil(FrCpxExtAreaCell frCpxAreaCell,
			FrCpxResultDataSet cpxQueryResult, IContext context,
			CellsModel formatModel) {
		this.frCpxAreaCell = frCpxAreaCell;
		this.cpxQueryResult = cpxQueryResult;
		this.context = context;
		this.formatModel = formatModel;
		m_cpRef = new Hashtable<CellPosition, CellPosition>();
	}

	private FrCpxExtAreaCell getExtAreaCellFormat() {
		if (frCpxAreaCellFormat != null) {
			return frCpxAreaCellFormat;
		}

		ExtendAreaModel extModel = ExtendAreaModel.getInstance(formatModel);
		frCpxAreaCellFormat = (FrCpxExtAreaCell) extModel
				.getExAreaByPK(frCpxAreaCell.getExAreaPK());

		return frCpxAreaCellFormat;
	}

	private Hashtable<String, CellPosition> getDimMeaPositionRefFmt() {
		if (this.m_dimMeaPosRefFmt == null) {
			m_dimMeaPosRefFmt = this.getExtAreaCellFormat()
					.getDimMeaPositionMap();
		}
		return m_dimMeaPosRefFmt;
	}

	/**
	 * 获取展开后复杂区域
	 * 
	 * @return
	 */
	public ExDataDescriptor getFrCpxAreaDesc() {
		if (frCpxAreaCell == null) {
			return null;
		}

		// 如果复杂区域模型为空，则直接返回
		FrCpxModel frCpxModel = frCpxAreaCell.getFrCpxModel();
		if (frCpxModel == null) {
			return null;
		}

		// 行栏目集合
		FrCpxDimensionSet rowDimensionSet = frCpxModel.getRowDimensionSet();
		// 列栏目集合
		FrCpxDimensionSet columnDimensionSet = frCpxModel
				.getColumnDimensionSet();
		if (rowDimensionSet == null || columnDimensionSet == null) {
			return null;
		}
		// 行栏目数组
		FrCpxDimensionItem[] rowDimensionItems = rowDimensionSet
				.getAllDimensionItems();
		// 列栏目数组
		FrCpxDimensionItem[] colDimensionItems = columnDimensionSet
				.getAllDimensionItems();

		// 行栏目所占区域大小
		int[] rowDimensionSize = getRowDimensionSize(rowDimensionItems, true,
				new ArrayList<FrCpxFieldDimMember>(), "");
		// 列栏目所占区域大小(列上的树形与行上的树形展开方式不一致)
		int[] colDimemsionSize = getRowDimensionSize(colDimensionItems, false,
				new ArrayList<FrCpxFieldDimMember>(), "");

		// 区域宽度 = 行栏目总宽度 + 列栏目宽度（由于列是行的转置，所以列栏目的宽度相当于栏目大小数组中的高度）
		int areaWidth = rowDimensionSize[0] + colDimemsionSize[1];
		// 区域高度 = 行栏目总高度 + 列栏目高度（由于列是行的转置，所以列栏目的高度相当于栏目大小数组中的宽度）
		int areaHeight = rowDimensionSize[1] + colDimemsionSize[0];

		return new ExDataDescriptor(areaWidth, areaHeight);
	}

	/**
	 * 将数据填充到数据态区域中
	 * 
	 * @param frCpxDataAreaCell
	 * @return
	 */
	public boolean setDataFrCpxAreaCell(FrCpxExtAreaCell frCpxDataAreaCell,
			CellsModel dataModel) {

		if (frCpxDataAreaCell == null) {
			return false;
		}

		// 如果复杂区域模型为空，则直接返回
		FrCpxModel frCpxModel = frCpxDataAreaCell.getFrCpxModel();
		if (frCpxModel == null) {
			return false;
		}

		FrCpxExtAreaCell extAreaCellFmt = this.getExtAreaCellFormat();
		if (extAreaCellFmt != null) {
			extAreaCellFmt.rebuildDimMeaPosRef();
		}

		// 行栏目集合
		FrCpxDimensionSet rowDimensionSet = frCpxModel.getRowDimensionSet();
		// 列栏目集合
		FrCpxDimensionSet columnDimensionSet = frCpxModel
				.getColumnDimensionSet();
		if (rowDimensionSet == null || columnDimensionSet == null) {
			return false;
		}

		// 行栏目数组
		FrCpxDimensionItem[] rowDimensionItems = rowDimensionSet
				.getAllDimensionItems();
		// 列栏目数组
		FrCpxDimensionItem[] colDimensionItems = columnDimensionSet
				.getAllDimensionItems();

		// 清除记录在栏目定义信息上的成员信息
		clearFieldMembers(rowDimensionSet.getAllDimensionItems());
		clearFieldMembers(columnDimensionSet.getAllDimensionItems());

		// 行栏目所占区域大小
		int[] rowDimensionSize = getRowDimensionSize(rowDimensionItems, true,
				new ArrayList<FrCpxFieldDimMember>(), "");
		// 列栏目所占区域大小(列上的树形与行上的树形展开方式不一致)
		int[] colDimemsionSize = getRowDimensionSize(colDimensionItems, false,
				new ArrayList<FrCpxFieldDimMember>(), "");

		// 获得区域起始位置
		CellPosition start = frCpxDataAreaCell.getArea().getStart();
		CellPosition startFmt = extAreaCellFmt.getArea().getStart();
		m_cpRef.put(start, startFmt);

		// 交叉点位置
		CellPosition crossPoint = CellPosition.getInstance(start.getRow()
				+ colDimemsionSize[0], start.getColumn() + rowDimensionSize[0]);

		AreaPosition headArea = AreaPosition.getInstance(start, crossPoint);
		ArrayList<Cell> crossHeadCells = this.getHeadCloneCells(dataModel,
				headArea);
		ArrayList<CombinedCell> crossHeadCombinedCells = this
				.getHeadCombinedCells(dataModel, headArea);

		// 清除原先设置的区域信息
		frCpxDataAreaCell.clear();
		// 清除格式模型中的旧信息
		dataModel.clearCells(frCpxDataAreaCell.getArea());
		// 清除合并单元格信息
		CombinedAreaModel.getInstance(dataModel).removeCombinedCell(
				frCpxDataAreaCell.getArea());

		// 设置交叉信息，主要是设置交叉点位置

		frCpxDataAreaCell.getAreaInfoSet().setCrossSetInfo(
				new AreaCrossSetInfo(frCpxDataAreaCell.getArea(), crossPoint));

		// 处理交叉表头数据
		if (crossHeadCells != null) {
			for (Cell cell : crossHeadCells) {
				TableUtilities.setCell(dataModel, cell.getRow(), cell.getCol(),
						cell);
			}
		}

		// 处理交叉表头合并单元格信息
		if (crossHeadCombinedCells != null) {
			CombinedAreaModel combinedAreaModel = CombinedAreaModel
					.getInstance(dataModel);
			combinedAreaModel.addCombinedCell(crossHeadCombinedCells
					.toArray(new CombinedCell[0]));
		}

		// 1. 处理行栏目区域
		// 行栏目起始点
		CellPosition rowAreaStart = CellPosition.getInstance(
				crossPoint.getRow(), start.getColumn());
		// 填充行栏目区域单元格
		setRowDimensionArea(rowDimensionItems, frCpxDataAreaCell, dataModel,
				rowAreaStart, crossPoint, new ArrayList<FrCpxFieldDimMember>(),
				"");

		// 2. 处理列栏目区域
		// 列栏目起始点
		CellPosition colAreaStart = CellPosition.getInstance(start.getRow(),
				crossPoint.getColumn());
		// 填充列栏目区域单元格
		setColDimensionArea(colDimensionItems, frCpxDataAreaCell, dataModel,
				colAreaStart, crossPoint, new ArrayList<FrCpxFieldDimMember>(),
				"");
		return true;
	}

	private ArrayList<CombinedCell> getHeadCombinedCells(CellsModel dataModel,
			AreaPosition headArea) {
		ArrayList<CombinedCell> combinedCells = new ArrayList<CombinedCell>();
		CombinedAreaModel combinedAreaModel = CombinedAreaModel
				.getInstance(dataModel);
		CombinedCell[] cells = combinedAreaModel
				.getContainCombinedCells(headArea);
		for (CombinedCell cell : cells) {
			combinedCells.add((CombinedCell) cell.clone());
		}
		return combinedCells;
	}

	private ArrayList<Cell> getHeadCloneCells(CellsModel dataModel,
			AreaPosition headArea) {
		ArrayList<Cell> cells = new ArrayList<Cell>();
		int startRow = headArea.getStart().getRow();
		int endRow = headArea.getEnd().getRow();
		int startColumn = headArea.getStart().getColumn();
		int endColumn = headArea.getEnd().getColumn();

		for (int row = startRow; row < endRow; row++) {
			for (int col = startColumn; col < endColumn; col++) {
				Cell cell = TableUtilities.getCell(dataModel, row, col);
				if (cell == null) {
					continue;
				}
				cells.add((Cell) cell.clone());
			}
		}

		return cells;
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
			List<FrCpxDimensionItem> subDimesionItems = frCpxDimensionItem
					.getSubDimesionItems();
			if (subDimesionItems != null && subDimesionItems.size() > 0) {
				clearFieldMembers(subDimesionItems
						.toArray(new FrCpxDimensionItem[subDimesionItems.size()]));
			}
		}

	}

	/**
	 * 填充列栏目区域单元格
	 * 
	 * @param colDimensionItems
	 * @param frCpxDataAreaCell
	 * @param dataModel
	 * @param colAreaStart
	 * @param crossPoint
	 */
	private void setColDimensionArea(FrCpxDimensionItem[] colDimensionItems,
			FrCpxExtAreaCell frCpxDataAreaCell, CellsModel dataModel,
			CellPosition colAreaStart, CellPosition crossPoint,
			List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {
		if (colDimensionItems == null)
			return;
		for (FrCpxDimensionItem frCpxDimensionItem : colDimensionItems) {
			// 本层列栏目项的大小
			int[] rowDimensionItemSize = getRowDimensionItemSize(
					frCpxDimensionItem, false, preFrCpxFieldDimMembers,
					preFieldItemKey);
			int height = rowDimensionItemSize[1];
			setColDimensionItem(frCpxDimensionItem, frCpxDataAreaCell,
					dataModel, colAreaStart, height, crossPoint,
					preFrCpxFieldDimMembers, preFieldItemKey);
			// 计算下一层行栏目的起始位置
			colAreaStart = colAreaStart.getMoveArea(0, height);
		}
	}

	/**
	 * 填充行栏目区域单元格
	 * 
	 * @param rowDimensionItems
	 *            ：行栏目项
	 * @param frCpxAreaCell
	 *            ：复杂区域模型
	 * @param formatModel
	 *            ：格式模型
	 * @param rowAreaStart
	 *            : 区域起始位置
	 * @param crossPoint
	 *            : 交叉点
	 */
	private void setRowDimensionArea(FrCpxDimensionItem[] rowDimensionItems,
			FrCpxExtAreaCell frCpxDataAreaCell, CellsModel dataModel,
			CellPosition rowAreaStart, CellPosition crossPoint,
			List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {
		if (rowDimensionItems == null)
			return;
		for (FrCpxDimensionItem frCpxDimensionItem : rowDimensionItems) {
			// 本层行栏目项的大小
			int[] rowDimensionItemSize = getRowDimensionItemSize(
					frCpxDimensionItem, true, preFrCpxFieldDimMembers,
					preFieldItemKey);
			int height = rowDimensionItemSize[1];
			setRowDimensionItem(frCpxDimensionItem, frCpxDataAreaCell,
					dataModel, rowAreaStart, height, crossPoint,
					preFrCpxFieldDimMembers, preFieldItemKey);
			// 计算下一层行栏目的起始位置
			rowAreaStart = rowAreaStart.getMoveArea(height, 0);
		}
	}

	/**
	 * 获得合计显示名称
	 * 
	 * @param frCpxDimensionItem
	 * @return
	 */
	private String getSumDisplayName(FrCpxDimensionItem frCpxDimensionItem) {
		String sumDisplayName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("1413006_0", "01413006-0886")/* @res "合计" */;
		if (frCpxDimensionItem != null
				&& StringUtils.isNotEmpty(frCpxDimensionItem
						.getSumDisplayName())) {
			sumDisplayName = frCpxDimensionItem.getSumDisplayName();
		}
		return sumDisplayName;
	}

	/**
	 * 填充一层列栏目成员（纵向的一层）
	 * 
	 * @param frCpxDimensionItem
	 *            ：列栏目
	 * @param frCpxAreaCell
	 *            ：复杂区域模型
	 * @param formatModel
	 *            ：格式模型
	 * @param colAreaStart
	 *            ：列栏目起始位置
	 * @param height
	 *            ：栏目高度
	 * @param crossPoint
	 *            ：交叉点
	 */
	private void setColDimensionItem(FrCpxDimensionItem frCpxDimensionItem,
			FrCpxExtAreaCell frCpxDataAreaCell, CellsModel dataModel,
			CellPosition colAreaStart, int height, CellPosition crossPoint,
			List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {
		if (frCpxDimensionItem == null)
			return;
		CellPosition startPos = colAreaStart;
		// 如果显示标题
		if (frCpxDimensionItem.isShowLabel()) {
			FrCpxDimMemberInfo titleDimInfo = new FrCpxDimMemberInfo(
					new FrCpxDimMemberItem(frCpxDimensionItem, null));
			titleDimInfo.setTitle(true);
			frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
					FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, titleDimInfo);

			// 设置栏目标题内容
			dataModel.getCellIfNullNew(startPos.getRow(), startPos.getColumn())
					.setValue(frCpxDimensionItem.getDimensionDef().getName());

			// 合并标题单元格
			dataModel.combineCell(AreaPosition.getInstance(startPos,
					startPos.getMoveArea(0, height - 1)));
			startPos = startPos.getMoveArea(1, 0);
		}

		// 如果显示合计
		if (frCpxDimensionItem.isShowSum()) {

			// 合计单元格的位置
			CellPosition countCell = null;

			// 合计在前
			if (frCpxDimensionItem.isPreSum()) {
				countCell = startPos;
				startPos = startPos.getMoveArea(0, 1);
			}
			// 合计在后
			else {
				countCell = CellPosition.getInstance(startPos.getRow(),
						startPos.getColumn() + height - 1);
			}

			// 将字段栏目信息填充到合计单元格上
			FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
					new FrCpxDimMemberItem(frCpxDimensionItem, null), true);
			frCpxDataAreaCell.getCellInfoSet().addExtInfo(countCell,
					FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);

			// 设置栏目合计标题
			dataModel.getCellIfNullNew(countCell.getRow(),
					countCell.getColumn()).setValue(
					getSumDisplayName(frCpxDimensionItem));

			// 计算起始位置到交叉点的行偏移
			int stepRow = crossPoint.getRow() - countCell.getRow();
			// 合并该单元格到交叉点之前的各行
			dataModel.combineCell(AreaPosition.getInstance(countCell,
					countCell.getMoveArea(stepRow - 1, 0)));
		}

		// 获得子栏目项集合
		List<FrCpxDimensionItem> subDimItems = frCpxDimensionItem
				.getSubDimesionItems();

		// 获得本层栏目的栏目成员
		FrCpxDimension dimensionDef = frCpxDimensionItem.getDimensionDef();
		// 获得本层栏目的栏目成员
		List<FrCpxDimMember> dimMembers = new ArrayList<FrCpxDimMember>();
		if (dimensionDef instanceof FrCpxUserDimension) {
			FrCpxDimMember[] userMembers = ((FrCpxUserDimension) dimensionDef)
					.getMembers();
			for (FrCpxDimMember userDimMember : userMembers) {
				dimMembers.add(userDimMember);
			}
		} else if (dimensionDef instanceof FrCpxFieldDimension) {
			List<FrCpxFieldDimMember> allDimMembers = new ArrayList<FrCpxFieldDimMember>();
			List<FrCpxFieldDimMember> fieldMembers = frCpxDimensionItem
					.getFieldDimMember();
			if (fieldMembers != null) {
				// 防止改变frCpxDimensionItem中的字段成员
				for (FrCpxFieldDimMember frCpxDimMember : fieldMembers) {
					allDimMembers.add(frCpxDimMember);
				}
			}
			// 如果前面存在过滤字段
			if (preFieldItemKey != null && !preFieldItemKey.equals("")) {
				// 根据前面的字段成员信息，获得过滤后的字段栏目成员
				FieldsRelationInfo fieldRelationInfo = getFieldRelationInfo(preFieldItemKey
						+ (preFieldItemKey.equals("") ? "" : "##")
						+ frCpxDimensionItem.getDimensionDefPK());
				if (fieldRelationInfo != null) {
					List<String> preFieldMemberKeyList = new ArrayList<String>();
					for (FrCpxFieldDimMember fieldMember : preFrCpxFieldDimMembers) {
						preFieldMemberKeyList.add(fieldMember.getUniqueID());
					}
					List<String> valueByPreList = fieldRelationInfo
							.getValueByPreList(preFieldMemberKeyList);
					dimMembers.addAll(getFilterMembers(allDimMembers,
							valueByPreList));
				}
			} else {
				dimMembers.addAll(allDimMembers);
			}
		}

		// 栏目树的深度
		int dimDeth = FrCpxTableModelUtil.getRowDimItemDeth(dimMembers);
		// 如果下方不存在子栏目项，则直接填充本栏目成员
		if (subDimItems == null || subDimItems.size() <= 0) {
			// 如果没有栏目成员，则直接填充栏目信息
			if (dimMembers == null || dimMembers.size() <= 0) {
				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, null);
				dimInfo.setRow(false);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);
				// 计算起始位置到交叉点的行偏移
				int stepRow = crossPoint.getRow() - startPos.getRow();
				// 合并该单元格到交叉点之前的各列
				dataModel.combineCell(AreaPosition.getInstance(startPos,
						startPos.getMoveArea(stepRow - 1, 0)));
			}
			// 否则填充栏目成员信息
			else {

				// 填充栏目成员信息
				FrCpxTableModelUtil.fillColDimMemberInfo(frCpxDimensionItem,
						dimDeth, dimMembers, frCpxDataAreaCell, dataModel,
						startPos, crossPoint, 0, null, 1); //undo
			}
			return;
		}
		// 如果存在子栏目项，则递归填充
		else {
			FrCpxDimensionItem[] subDimItemArray = subDimItems
					.toArray(new FrCpxDimensionItem[subDimItems.size()]);

			// 如果没有栏目成员，则直接填充栏目信息
			if (dimMembers == null || dimMembers.size() <= 0) {
				// 计算子栏目项的高度
				int[] subDimItemsSize = getRowDimensionSize(subDimItemArray,
						true, preFrCpxFieldDimMembers, preFieldItemKey);
				int subDimItemsHeight = subDimItemsSize[1];

				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, null);
				dimInfo.setRow(false);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);
				// 合并该单元格到子栏目项的高度
				dataModel.combineCell(AreaPosition.getInstance(startPos,
						startPos.getMoveArea(0, subDimItemsHeight - 1)));
				// 填充该组子栏目项
				setColDimensionArea(subDimItemArray, frCpxDataAreaCell,
						dataModel, startPos.getMoveArea(1, 0), crossPoint,
						preFrCpxFieldDimMembers, preFieldItemKey);
			}
			// 否则填充栏目成员信息
			else {
				// 填充栏目成员信息
				fillColDimMemberInfo(frCpxDimensionItem, dimDeth, dimMembers,
						frCpxDataAreaCell, dataModel, startPos, crossPoint, 0,
						subDimItemArray, preFrCpxFieldDimMembers,
						preFieldItemKey);
			}
		}
	}

	/**
	 * 填充列栏目成员信息
	 * 
	 * @param frCpxDimensionItem
	 *            :栏目项
	 * @param dimDeth
	 *            ：栏目树的深度
	 * @param userDimMembers
	 *            ：栏目成员
	 * @param frCpxAreaCell
	 *            ：复杂区域模型
	 * @param formatModel
	 *            ：格式模型
	 * @param colAreaStart
	 *            ：栏目成员区域起始位置
	 * @param crossPoint
	 *            ：复杂区域交叉点
	 * @param level
	 *            : 栏目成员在成员树上的级次
	 * @param subDimItemArray
	 *            ：子栏目项数组
	 */
	public void fillColDimMemberInfo(FrCpxDimensionItem frCpxDimensionItem,
			int dimDeth, List<FrCpxDimMember> userDimMembers,
			FrCpxExtAreaCell frCpxDataAreaCell, CellsModel dataModel,
			CellPosition colAreaStart, CellPosition crossPoint, int level,
			FrCpxDimensionItem[] subDimItemArray,
			List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {
		if (frCpxDimensionItem == null)
			return;
		CellPosition startPos = colAreaStart;

		// 如果是树形展开(列上的树形)
		if (frCpxDimensionItem.isTreeExpand()) {
			for (FrCpxDimMember frCpxDimMember : userDimMembers) {
				// 构建栏目信息
				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, frCpxDimMember);
				dimInfo.setRow(false);
				dimInfo.setLevel(level);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);

				int subDimItemsHeight = 0;
				List<FrCpxFieldDimMember> newPreDimMembers = new ArrayList<FrCpxFieldDimMember>();
				String newPreFieldItemKey = "";
				if (frCpxDimMember instanceof FrCpxFieldDimMember) {
					newPreDimMembers.addAll(preFrCpxFieldDimMembers);
					newPreDimMembers.add((FrCpxFieldDimMember) frCpxDimMember);
					newPreFieldItemKey = preFieldItemKey
							+ (preFieldItemKey.equals("") ? "" : "##")
							+ frCpxDimensionItem.getDimensionDefPK();

				} else {
					newPreDimMembers = preFrCpxFieldDimMembers;
					newPreFieldItemKey = preFieldItemKey;
				}
				if (subDimItemArray == null) {
					subDimItemsHeight = 1;
				} else {
					subDimItemsHeight = getRowDimensionSize(subDimItemArray,
							true, newPreDimMembers, newPreFieldItemKey)[1];
				}

				// 横向合并单元格(合并本栏目成员自身的高度*下级栏目项的高度)
				List<FrCpxDimMember> m_DimMemberItemList = new ArrayList<FrCpxDimMember>();
				m_DimMemberItemList.add(frCpxDimMember);
				// 计算本栏目成员自身的高度
				int m_height = FrCpxTableModelUtil
						.getAllNodeCount(m_DimMemberItemList);
				dataModel.combineCell(AreaPosition.getInstance(
						startPos,
						startPos.getMoveArea(0, m_height * subDimItemsHeight
								- 1)));

				// 获得下级栏目成员
				List<FrCpxDimMember> subMembers = FrCpxTableModelUtil
						.changeArrayToList(frCpxDimMember.getSubMembers());

				// 如果不存在子栏目项
				if (subDimItemArray == null) {
					// 如果非树上的叶子节点
					if (subMembers != null && subMembers.size() > 0) {
						// 纵向合并单元格(合并起始单元格下一个单元格至交叉点前的各行)
						int stepRow = crossPoint.getRow() - startPos.getRow()
								- 1;
						dataModel.combineCell(AreaPosition.getInstance(
								startPos.getMoveArea(1, 0),
								startPos.getMoveArea(stepRow, 0)));
					}
					// 如果是树上的叶子节点，合并该节点至交叉点前的各行
					else {
						int stepRow = crossPoint.getRow() - startPos.getRow();
						dataModel
								.combineCell(AreaPosition.getInstance(startPos,
										startPos.getMoveArea(stepRow - 1, 0)));
					}
				}
				// 如果存在子栏目项，则填充子栏目项
				else {
					// 子栏目项的起始位置偏移至本起始位置下方，偏移行数 = 栏目树的深度 - 级次
					int stepRow = dimDeth - level;
					CellPosition startCombPos = null;
					// 如果非树上的叶子节点
					if (subMembers != null && subMembers.size() > 0) {
						startCombPos = startPos.getMoveArea(1, 0);
					} else {
						startCombPos = startPos.getMoveArea(0, 0);
					}
					CellPosition endCombPos = startPos.getMoveArea(stepRow - 1,
							subDimItemsHeight - 1);
					if (startCombPos.getRow() <= endCombPos.getRow()
							&& startCombPos.getColumn() <= endCombPos
									.getColumn()) {
						// 横向合并单元格，合并起始单元格下一个单元格至子栏目项的高度
						dataModel.combineCell(AreaPosition.getInstance(
								startCombPos, endCombPos));
					}
					// 填充子栏目项数组,起始位置偏移至该栏目下的第一个单元格
					setColDimensionArea(subDimItemArray, frCpxDataAreaCell,
							dataModel, startPos.getMoveArea(stepRow, 0),
							crossPoint, newPreDimMembers, newPreFieldItemKey);
				}

				// 下一个成员的起始位置
				startPos = startPos.getMoveArea(0, 1 * subDimItemsHeight);

				// 如果下级栏目成员不为空，则填充下级栏目成员
				if (subMembers != null && subMembers.size() > 0) {
					fillColDimMemberInfo(frCpxDimensionItem, dimDeth,
							subMembers, frCpxDataAreaCell, dataModel,
							startPos.getMoveArea(1, subDimItemsHeight),
							crossPoint, level + 1, subDimItemArray,
							preFrCpxFieldDimMembers, preFieldItemKey);
					// 下级栏目成员的总填充高度
					int subHeight = 0;
					for (FrCpxDimMember subMember : subMembers) {

						List<FrCpxFieldDimMember> subPreDimMembers = new ArrayList<FrCpxFieldDimMember>();
						String subPreFieldItemKey = "";
						if (subMember instanceof FrCpxFieldDimMember) {
							subPreDimMembers.addAll(preFrCpxFieldDimMembers);
							subPreDimMembers
									.add((FrCpxFieldDimMember) subMember);
							subPreFieldItemKey = preFieldItemKey
									+ (preFieldItemKey.equals("") ? "" : "##")
									+ frCpxDimensionItem.getDimensionDefPK();
						} else {
							subPreDimMembers = preFrCpxFieldDimMembers;
							subPreFieldItemKey = preFieldItemKey;
						}
						subHeight = subHeight
								+ getRowDimensionSize(subDimItemArray, true,
										subPreDimMembers, subPreFieldItemKey)[1];
					}
					startPos = startPos.getMoveArea(0, subHeight);
				}
			}
		}
		// 如果是合并展开（叶子节点有横向合并，中间层的节点有纵向合并）
		else {
			for (FrCpxDimMember frCpxDimMember : userDimMembers) {
				// 构建栏目信息
				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, frCpxDimMember);
				dimInfo.setRow(false);
				dimInfo.setLevel(level);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);
				// 获得下级栏目成员
				List<FrCpxDimMember> subMembers = FrCpxTableModelUtil
						.changeArrayToList(frCpxDimMember.getSubMembers());

				int subDimItemsHeight = 0;

				// 非叶子节点，需要横向合并
				if (subMembers != null && subMembers.size() > 0) {

					// 如果非叶子节点，则默认为是自定义栏目
					// 如果不存在子栏目项
					if (subDimItemArray == null) {
						subDimItemsHeight = 1;
					} else {
						subDimItemsHeight = getRowDimensionSize(
								subDimItemArray, true, preFrCpxFieldDimMembers,
								preFieldItemKey)[1];
					}

					// 高度 = 子成员中的叶子节点的个数
					int height = FrCpxTableModelUtil
							.getLeafNodeCount(subMembers) * subDimItemsHeight;
					// 横向合并
					dataModel.combineCell(AreaPosition.getInstance(startPos,
							startPos.getMoveArea(0, height - 1)));
					// 如果下级栏目成员不为空，则填充下级栏目成员
					fillColDimMemberInfo(frCpxDimensionItem, dimDeth,
							subMembers, frCpxDataAreaCell, dataModel,
							startPos.getMoveArea(1, 0), crossPoint, level + 1,
							subDimItemArray, preFrCpxFieldDimMembers,
							preFieldItemKey);
					startPos = startPos.getMoveArea(0, height);
				}
				// 叶子节点，需要纵向合并
				else {
					// 如果不存在子栏目项
					if (subDimItemArray == null) {
						// 计算起始位置到交叉点的行偏移
						int stepRow = crossPoint.getRow() - startPos.getRow();
						// 纵向合并
						dataModel
								.combineCell(AreaPosition.getInstance(startPos,
										startPos.getMoveArea(stepRow - 1, 0)));
					}
					// 否则横向合并叶子节点
					else {
						List<FrCpxFieldDimMember> newPreDimMembers = new ArrayList<FrCpxFieldDimMember>();
						String newPreFieldItemKey = "";
						if (frCpxDimMember instanceof FrCpxFieldDimMember) {
							newPreDimMembers.addAll(preFrCpxFieldDimMembers);
							newPreDimMembers
									.add((FrCpxFieldDimMember) frCpxDimMember);
							newPreFieldItemKey = preFieldItemKey
									+ (preFieldItemKey.equals("") ? "" : "##")
									+ frCpxDimensionItem.getDimensionDefPK();

						} else {
							newPreDimMembers = preFrCpxFieldDimMembers;
							newPreFieldItemKey = preFieldItemKey;
						}
						subDimItemsHeight = getRowDimensionSize(
								subDimItemArray, true, newPreDimMembers,
								newPreFieldItemKey)[1];

						// 子栏目的起始位置偏移至起始位置的下方 ， 行偏移数 = 栏目树的深度 - 级次
						int stepRow = dimDeth - level;
						CellPosition startCombPos = startPos.getMoveArea(0, 0);
						CellPosition endCombPos = startPos.getMoveArea(
								stepRow - 1, subDimItemsHeight - 1);
						if (startCombPos.getRow() <= endCombPos.getRow()
								&& startCombPos.getColumn() <= endCombPos
										.getColumn()) {
							// 横向合并单元格，合并起始单元格下一个单元格至子栏目项的高度
							dataModel.combineCell(AreaPosition.getInstance(
									startCombPos, endCombPos));
						}
						// 填充子栏目项数组
						setColDimensionArea(subDimItemArray, frCpxDataAreaCell,
								dataModel, startPos.getMoveArea(stepRow, 0),
								crossPoint, newPreDimMembers,
								newPreFieldItemKey);
					}
					// 下一个成员的起始位置
					startPos = startPos.getMoveArea(0, 1 * subDimItemsHeight);
				}
			}
		}
	}

	/**
	 * 填充一层行栏目成员（横向的一层）
	 * 
	 * @param frCpxDimensionItem
	 *            ：行栏目
	 * @param frCpxDataAreaCell
	 *            : 复杂区域存储模型
	 * @param formatModel
	 *            ：格式模型
	 * @param rowAreaStart
	 *            ：行栏目起始位置
	 * @param height
	 *            :栏目高度
	 * @param crossPoint
	 *            ：交叉点
	 */
	private void setRowDimensionItem(FrCpxDimensionItem frCpxDimensionItem,
			FrCpxExtAreaCell frCpxDataAreaCell, CellsModel dataModel,
			CellPosition rowAreaStart, int height, CellPosition crossPoint,
			List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {
		if (frCpxDimensionItem == null)
			return;
		CellPosition startPos = rowAreaStart;
		// 如果显示标题
		if (frCpxDimensionItem.isShowLabel()) {
			FrCpxDimMemberInfo titleDimInfo = new FrCpxDimMemberInfo(
					new FrCpxDimMemberItem(frCpxDimensionItem, null));
			titleDimInfo.setTitle(true);
			frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
					FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, titleDimInfo);

			// 设置栏目标题内容
			dataModel.getCellIfNullNew(startPos.getRow(), startPos.getColumn())
					.setValue(frCpxDimensionItem.getDimensionDef().getName());

			// 合并标题单元格
			dataModel.combineCell(AreaPosition.getInstance(startPos,
					startPos.getMoveArea(height - 1, 0)));
			startPos = startPos.getMoveArea(0, 1);
		}

		// 如果显示合计
		if (frCpxDimensionItem.isShowSum()) {

			// 合计单元格的位置
			CellPosition countCell = null;

			// 合计在前
			if (frCpxDimensionItem.isPreSum()) {
				countCell = startPos;
				startPos = startPos.getMoveArea(1, 0);
			}
			// 合计在后
			else {
				countCell = CellPosition.getInstance(startPos.getRow() + height
						- 1, startPos.getColumn());
			}

			// 将字段栏目信息填充到合计单元格上
			FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
					new FrCpxDimMemberItem(frCpxDimensionItem, null), true);
			frCpxDataAreaCell.getCellInfoSet().addExtInfo(countCell,
					FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);

			// 设置栏目合计标题
			dataModel.getCellIfNullNew(countCell.getRow(),
					countCell.getColumn()).setValue(
					getSumDisplayName(frCpxDimensionItem));

			// 计算起始位置到交叉点的列偏移
			int stepCol = crossPoint.getColumn() - countCell.getColumn();
			// 合并该单元格到交叉点之前的各列
			dataModel.combineCell(AreaPosition.getInstance(countCell,
					countCell.getMoveArea(0, stepCol - 1)));
		}

		// 获得子栏目项集合
		List<FrCpxDimensionItem> subDimItems = frCpxDimensionItem
				.getSubDimesionItems();

		// 获得本层栏目的栏目成员
		FrCpxDimension dimensionDef = frCpxDimensionItem.getDimensionDef();
		List<FrCpxDimMember> dimMembers = new ArrayList<FrCpxDimMember>();
		if (dimensionDef instanceof FrCpxUserDimension) {
			FrCpxDimMember[] userMembers = ((FrCpxUserDimension) dimensionDef)
					.getMembers();
			for (FrCpxDimMember userDimMember : userMembers) {
				dimMembers.add(userDimMember);
			}
		} else if (dimensionDef instanceof FrCpxFieldDimension) {
			List<FrCpxFieldDimMember> allDimMembers = new ArrayList<FrCpxFieldDimMember>();
			List<FrCpxFieldDimMember> fieldMembers = frCpxDimensionItem
					.getFieldDimMember();
			if (fieldMembers != null) {
				// 防止改变frCpxDimensionItem中的字段成员
				for (FrCpxFieldDimMember frCpxDimMember : fieldMembers) {
					allDimMembers.add(frCpxDimMember);
				}
			}

			// 如果前面存在过滤字段
			if (preFieldItemKey != null && !preFieldItemKey.equals("")) {
				// 根据前面的字段成员信息，获得过滤后的字段栏目成员
				FieldsRelationInfo fieldRelationInfo = getFieldRelationInfo(preFieldItemKey
						+ (preFieldItemKey.equals("") ? "" : "##")
						+ frCpxDimensionItem.getDimensionDefPK());
				if (fieldRelationInfo != null) {
					List<String> preFieldMemberKeyList = new ArrayList<String>();
					for (FrCpxFieldDimMember fieldMember : preFrCpxFieldDimMembers) {
						preFieldMemberKeyList.add(fieldMember.getUniqueID());
					}
					List<String> valueByPreList = fieldRelationInfo
							.getValueByPreList(preFieldMemberKeyList);
					dimMembers.addAll(getFilterMembers(allDimMembers,
							valueByPreList));
				}
			} else {
				dimMembers.addAll(allDimMembers);
			}
		}

		// 栏目成员树的深度
		int dimDeth = FrCpxTableModelUtil.getRowDimItemDeth(dimMembers);

		// 如果右侧不存在子栏目项，则直接填充本栏目成员
		if (subDimItems == null || subDimItems.size() <= 0) {
			// 如果没有栏目成员，则直接填充栏目信息
			if (dimMembers == null || dimMembers.size() <= 0) {
				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, null);
				dimInfo.setRow(true);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);
				// 计算起始位置到交叉点的列偏移
				int stepCol = crossPoint.getColumn() - startPos.getColumn();
				// 合并该单元格到交叉点之前的各列
				dataModel.combineCell(AreaPosition.getInstance(startPos,
						startPos.getMoveArea(0, stepCol - 1)));
			}
			// 否则填充栏目成员信息
			else {
				// 填充栏目成员信息
				FrCpxTableModelUtil.fillRowDimMemberInfo(frCpxDimensionItem,
						dimDeth, dimMembers, frCpxDataAreaCell, dataModel,
						startPos, crossPoint, 0, null, 1);
			}
			return;
		}
		// 如果存在子栏目项，则递归填充
		else {
			FrCpxDimensionItem[] subDimItemArray = subDimItems
					.toArray(new FrCpxDimensionItem[subDimItems.size()]);
			// 如果没有栏目成员，则直接填充栏目信息
			if (dimMembers == null || dimMembers.size() <= 0) {
				// 计算子栏目项的高度
				int[] subDimItemsSize = getRowDimensionSize(subDimItemArray,
						true, preFrCpxFieldDimMembers, preFieldItemKey);
				int subDimItemsHeight = subDimItemsSize[1];

				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, null);
				dimInfo.setRow(true);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);
				// 合并该单元格到子栏目项的高度
				dataModel.combineCell(AreaPosition.getInstance(startPos,
						startPos.getMoveArea(subDimItemsHeight - 1, 0)));
				// 填充该组子栏目项
				setRowDimensionArea(subDimItemArray, frCpxDataAreaCell,
						dataModel, startPos.getMoveArea(0, 1), crossPoint,
						preFrCpxFieldDimMembers, preFieldItemKey);
			}
			// 否则填充栏目成员信息
			else {
				// 填充栏目成员信息
				fillRowDimMemberInfo(frCpxDimensionItem, dimDeth, dimMembers,
						frCpxDataAreaCell, dataModel, startPos, crossPoint, 0,
						subDimItemArray, preFrCpxFieldDimMembers,
						preFieldItemKey);
			}
		}
	}

	/**
	 * 填充行栏目成员信息
	 * 
	 * @param frCpxDimensionItem
	 *            :栏目项
	 * @param dimDeth
	 *            :栏目成员树的深度
	 * @param dimMembers
	 *            ：栏目成员
	 * @param frCpxDataAreaCell
	 *            ：复杂区域模型
	 * @param formatModel
	 *            ：格式模型
	 * @param rowAreaStart
	 *            ：栏目成员区域起始位置
	 * @param crossPoint
	 *            ：复杂区域交叉点
	 * @param level
	 *            : 栏目成员在成员树上的级次
	 * @param subDimItemArray
	 *            ：子栏目项数组
	 * @param preFieldItemKey
	 * @param preFrCpxFieldDimMembers
	 */
	public void fillRowDimMemberInfo(FrCpxDimensionItem frCpxDimensionItem,
			int dimDeth, List<FrCpxDimMember> userDimMembers,
			FrCpxExtAreaCell frCpxDataAreaCell, CellsModel dataModel,
			CellPosition rowAreaStart, CellPosition crossPoint, int level,
			FrCpxDimensionItem[] subDimItemArray,
			List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {
		CellPosition startPos = rowAreaStart;
		if (frCpxDimensionItem == null)
			return;
		// 如果是树形展开(树上的每一个节点都有横向合并)
		if (frCpxDimensionItem.isTreeExpand()) {
			for (FrCpxDimMember frCpxDimMember : userDimMembers) {
				// 构建栏目信息
				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, frCpxDimMember);
				dimInfo.setRow(true);
				dimInfo.setLevel(level);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);
				int subDimItemsHeight = 0;
				List<FrCpxFieldDimMember> newPreDimMembers = new ArrayList<FrCpxFieldDimMember>();
				String newPreFieldItemKey = "";
				// 如果不存在子栏目项
				if (subDimItemArray == null) {
					subDimItemsHeight = 1;
					// 计算起始位置到交叉点的列偏移
					int stepCol = crossPoint.getColumn() - startPos.getColumn();
					// 横向合并单元格，合并起始列到交叉点列
					dataModel.combineCell(AreaPosition.getInstance(startPos,
							startPos.getMoveArea(0, stepCol - 1)));
				}
				// 如果存在子栏目项，则填充子栏目项
				else {
					if (frCpxDimMember instanceof FrCpxFieldDimMember) {
						newPreDimMembers.addAll(preFrCpxFieldDimMembers);
						newPreDimMembers
								.add((FrCpxFieldDimMember) frCpxDimMember);
						newPreFieldItemKey = preFieldItemKey
								+ (preFieldItemKey.equals("") ? "" : "##")
								+ frCpxDimensionItem.getDimensionDefPK();

					} else {
						newPreDimMembers = preFrCpxFieldDimMembers;
						newPreFieldItemKey = preFieldItemKey;
					}
					subDimItemsHeight = getRowDimensionSize(subDimItemArray,
							true, newPreDimMembers, newPreFieldItemKey)[1];
					// 纵向合并单元格，合并起始行到子栏目项结束行
					dataModel.combineCell(AreaPosition.getInstance(startPos,
							startPos.getMoveArea(subDimItemsHeight - 1, 0)));
					// 填充子栏目项数组
					setRowDimensionArea(subDimItemArray, frCpxDataAreaCell,
							dataModel, startPos.getMoveArea(0, 1), crossPoint,
							newPreDimMembers, newPreFieldItemKey);

				}

				// 下一个成员的起始位置
				startPos = startPos.getMoveArea(1 * subDimItemsHeight, 0);
				// 获得下级栏目成员
				List<FrCpxDimMember> subMembers = FrCpxTableModelUtil
						.changeArrayToList(frCpxDimMember.getSubMembers());
				if (subMembers != null && subMembers.size() > 0) {
					// 如果下级栏目成员不为空，则填充下级栏目成员
					fillRowDimMemberInfo(frCpxDimensionItem, dimDeth,
							subMembers, frCpxDataAreaCell, dataModel, startPos,
							crossPoint, level + 1, subDimItemArray,
							preFrCpxFieldDimMembers, preFieldItemKey);
					// 下级栏目成员的总填充高度
					int subHeight = 0;
					for (FrCpxDimMember subMember : subMembers) {

						List<FrCpxFieldDimMember> subPreDimMembers = new ArrayList<FrCpxFieldDimMember>();
						String subPreFieldItemKey = "";
						if (subMember instanceof FrCpxFieldDimMember) {
							subPreDimMembers.addAll(preFrCpxFieldDimMembers);
							subPreDimMembers
									.add((FrCpxFieldDimMember) subMember);
							subPreFieldItemKey = preFieldItemKey
									+ (preFieldItemKey.equals("") ? "" : "##")
									+ frCpxDimensionItem.getDimensionDefPK();
						} else {
							subPreDimMembers = preFrCpxFieldDimMembers;
							subPreFieldItemKey = preFieldItemKey;
						}
						subHeight = subHeight
								+ getRowDimensionSize(subDimItemArray, true,
										subPreDimMembers, subPreFieldItemKey)[1];
					}
					startPos = startPos.getMoveArea(subHeight, 0);
				}
			}
		}
		// 如果是合并展开（叶子节点有横向合并，中间层的节点有纵向合并）
		else {
			for (FrCpxDimMember frCpxDimMember : userDimMembers) {
				// 构建栏目信息
				FrCpxDimMemberInfo dimInfo = new FrCpxDimMemberInfo(
						frCpxDimensionItem, frCpxDimMember);
				dimInfo.setRow(true);
				dimInfo.setLevel(level);
				frCpxDataAreaCell.getCellInfoSet().addExtInfo(startPos,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, dimInfo);
				// 获得下级栏目成员
				List<FrCpxDimMember> subMembers = FrCpxTableModelUtil
						.changeArrayToList(frCpxDimMember.getSubMembers());

				int subDimItemsHeight = 0;

				// 非叶子节点，需要纵向合并
				if (subMembers != null && subMembers.size() > 0) {
					// 如果非叶子节点，则默认为是自定义栏目
					// 如果不存在子栏目项
					if (subDimItemArray == null) {
						subDimItemsHeight = 1;
					} else {
						subDimItemsHeight = getRowDimensionSize(
								subDimItemArray, true, preFrCpxFieldDimMembers,
								preFieldItemKey)[1];
					}
					// 高度 = 子成员中的叶子节点的个数
					int height = FrCpxTableModelUtil
							.getLeafNodeCount(subMembers) * subDimItemsHeight;
					// 纵向合并
					dataModel.combineCell(AreaPosition.getInstance(startPos,
							startPos.getMoveArea(height - 1, 0)));
					// 如果下级栏目成员不为空，则填充下级栏目成员
					fillRowDimMemberInfo(frCpxDimensionItem, dimDeth,
							subMembers, frCpxDataAreaCell, dataModel,
							startPos.getMoveArea(0, 1), crossPoint, level + 1,
							subDimItemArray, preFrCpxFieldDimMembers,
							preFieldItemKey);
					startPos = startPos.getMoveArea(height, 0);
				}
				// 叶子节点，需要横向合并
				else {
					// 如果不存在子栏目项
					if (subDimItemArray == null) {
						subDimItemsHeight = 1;
						// 计算起始位置到交叉点的列偏移
						int stepCol = crossPoint.getColumn()
								- startPos.getColumn();
						// 横向合并
						dataModel
								.combineCell(AreaPosition.getInstance(startPos,
										startPos.getMoveArea(0, stepCol - 1)));
					}
					// 否则纵向合并叶子节点
					else {

						List<FrCpxFieldDimMember> newPreDimMembers = new ArrayList<FrCpxFieldDimMember>();
						String newPreFieldItemKey = "";
						if (frCpxDimMember instanceof FrCpxFieldDimMember) {
							newPreDimMembers.addAll(preFrCpxFieldDimMembers);
							newPreDimMembers
									.add((FrCpxFieldDimMember) frCpxDimMember);
							newPreFieldItemKey = preFieldItemKey
									+ (preFieldItemKey.equals("") ? "" : "##")
									+ frCpxDimensionItem.getDimensionDefPK();

						} else {
							newPreDimMembers = preFrCpxFieldDimMembers;
							newPreFieldItemKey = preFieldItemKey;
						}
						subDimItemsHeight = getRowDimensionSize(
								subDimItemArray, true, newPreDimMembers,
								newPreFieldItemKey)[1];
						// 纵向合并单元格，合并起始行到子栏目项结束行
						dataModel
								.combineCell(AreaPosition.getInstance(startPos,
										startPos.getMoveArea(
												subDimItemsHeight - 1, 0)));
						// 子栏目的起始位置 将偏移至叶子节点右侧，偏移列数 = 栏目项树的深度 - 本栏目成员在树上的级次
						int stepCol = dimDeth - level;

						// 填充子栏目项数组
						setRowDimensionArea(subDimItemArray, frCpxDataAreaCell,
								dataModel, startPos.getMoveArea(0, stepCol),
								crossPoint, newPreDimMembers,
								newPreFieldItemKey);
					}
					// 下一个成员的起始位置
					startPos = startPos.getMoveArea(1 * subDimItemsHeight, 0);
				}
			}
		}
	}

	/**
	 * 计算展开后行/列栏目集合的大小
	 * 
	 * @param frCpxAreaCell
	 * @param rowDimensionItems
	 * @param cpxQueryResult
	 * @param context
	 * @param b
	 * @return
	 */
	private int[] getRowDimensionSize(FrCpxDimensionItem[] rowDimensionItems,
			boolean isRow, List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {
		int width = 0;
		int height = 0;
		if (rowDimensionItems != null) {
			for (FrCpxDimensionItem frCpxDimensionItem : rowDimensionItems) {
				// 本层行栏目项的大小
				int[] rowDimensionItemSize = getRowDimensionItemSize(
						frCpxDimensionItem, isRow, preFrCpxFieldDimMembers,
						preFieldItemKey);
				// 宽度取各层中最宽的
				if (rowDimensionItemSize[0] > width) {
					width = rowDimensionItemSize[0];
				}
				// 高度取累加值
				height = height + rowDimensionItemSize[1];
			}
		}
		return new int[] { width, height };
	}

	/**
	 * 计算展开后某一个栏目项的大小
	 * 
	 * @param frCpxAreaCell
	 * @param frCpxDimensionItem
	 * @param cpxQueryResult
	 * @param context
	 * @param isRow
	 * @return
	 */
	private int[] getRowDimensionItemSize(
			FrCpxDimensionItem frCpxDimensionItem, boolean isRow,
			List<FrCpxFieldDimMember> preFrCpxFieldDimMembers,
			String preFieldItemKey) {

		if (frCpxDimensionItem == null)
			return new int[] { 0, 0 };

		// 宽度
		int width = 0;
		// 高度
		int height = 0;

		// --- 计算本层栏目项的大小----
		// 如果是字段型栏目
		if (frCpxDimensionItem.isFieldDimension()) {
			// ------先根据结果集创建字段栏目成员------
			FrCpxFieldDimension dimensionDef = (FrCpxFieldDimension) frCpxDimensionItem
					.getDimensionDef();
			// 如果字段栏目成员为空，则创建（避免多个字段栏目项引用同一个字段栏目定义时，重复创建栏目成员）
			if (dimensionDef.getMembers() == null) {
				// 获得当前字段栏目的结果集
				FieldDimMemberResultInfo[] oneFielDimensionResult = getOneFielDimensionResult(dimensionDef
						.getID());
				// 构建字段栏目成员信息，并放入字段栏目模型中
				createFieldDimMember(dimensionDef, oneFielDimensionResult);
			}

			// 根据字段栏目项的排序设置信息，将排序后的字段栏目成员信息设置到字段栏目项中
			setOrderedMemberToDimItem(frCpxDimensionItem);
			// 获得字段栏目成员
			List<FrCpxFieldDimMember> fieldDimMembers = frCpxDimensionItem
					.getFieldDimMember();
			List<FrCpxFieldDimMember> filterFieldMembers = new ArrayList<FrCpxFieldDimMember>();
			// 如果前面存在过滤字段
			if (preFieldItemKey != null && !preFieldItemKey.equals("")) {
				// 根据前面的字段成员信息，获得过滤后的字段栏目成员
				FieldsRelationInfo fieldRelationInfo = getFieldRelationInfo(preFieldItemKey
						+ (preFieldItemKey.equals("") ? "" : "##")
						+ frCpxDimensionItem.getDimensionDefPK());
				if (fieldRelationInfo != null) {
					List<String> preFieldMemberKeyList = new ArrayList<String>();
					for (FrCpxFieldDimMember fieldMember : preFrCpxFieldDimMembers) {
						preFieldMemberKeyList.add(fieldMember.getUniqueID());
					}
					List<String> valueByPreList = fieldRelationInfo
							.getValueByPreList(preFieldMemberKeyList);
					filterFieldMembers = getFilterMembers(fieldDimMembers,
							valueByPreList);
				}
			} else {
				filterFieldMembers = fieldDimMembers;
			}

			List<FrCpxDimensionItem> subDimesionItems = frCpxDimensionItem
					.getSubDimesionItems();

			// 如果没有下级栏目成员
			if (subDimesionItems == null || subDimesionItems.size() == 0) {
				if (filterFieldMembers == null
						|| filterFieldMembers.size() == 0) {
					height = 1;
					width = 1;
				} else {
					height = filterFieldMembers.size();
					// 暂时不考虑字段型栏目的树形结构，所以认为字段栏目的宽度都是1
					width = 1;
				}
			} else {
				if (filterFieldMembers == null
						|| filterFieldMembers.size() == 0) {
					int[] dimMemberSize = getRowDimensionSize(
							subDimesionItems.toArray(new FrCpxDimensionItem[subDimesionItems
									.size()]), isRow, preFrCpxFieldDimMembers,
							preFieldItemKey);
					height = dimMemberSize[1];
					width = 1 + dimMemberSize[0];
				} else {
					int subWidth = 0;
					for (FrCpxFieldDimMember frCpxFieldDimMember : filterFieldMembers) {

						String newPreFieldItemKey = preFieldItemKey
								+ (preFieldItemKey.equals("") ? "" : "##")
								+ frCpxDimensionItem.getDimensionDefPK();
						List<FrCpxFieldDimMember> newPreFieldMemberKeyList = new ArrayList<FrCpxFieldDimMember>();
						newPreFieldMemberKeyList
								.addAll(preFrCpxFieldDimMembers);
						newPreFieldMemberKeyList.add(frCpxFieldDimMember);
						int[] dimMemberSize = getRowDimensionSize(
								subDimesionItems.toArray(new FrCpxDimensionItem[subDimesionItems
										.size()]), isRow,
								newPreFieldMemberKeyList, newPreFieldItemKey);
						height = height + dimMemberSize[1];
						if (dimMemberSize[0] > subWidth) {
							subWidth = dimMemberSize[0];
						}
					}
					width = 1 + subWidth;
				}
			}

		}
		// 如果自定义栏目
		else {
			List<FrCpxDimMember> dimMembers = FrCpxTableModelUtil
					.changeArrayToList(((FrCpxUserDimension) frCpxDimensionItem
							.getDimensionDef()).getMembers());

			List<FrCpxDimensionItem> subDimesionItems = frCpxDimensionItem
					.getSubDimesionItems();
			int[] subWidth = null;
			// 如果没有下级栏目成员
			if (subDimesionItems == null || subDimesionItems.size() == 0) {
				subWidth = new int[] { 0, 1 };
			} else {
				// 子栏目项的高度
				subWidth = getRowDimensionSize(
						subDimesionItems.toArray(new FrCpxDimensionItem[subDimesionItems
								.size()]), isRow, preFrCpxFieldDimMembers,
						preFieldItemKey);
			}

			// 如果没有栏目成员，则对应的栏目成员位置上存放该栏目的信息
			if (dimMembers == null || dimMembers.size() <= 0) {
				width = 1 + subWidth[0];
				height = subWidth[1];
			}
			// 否则计算栏目成员的大小
			else {

				// 如果是树形方式展开
				if (frCpxDimensionItem.isTreeExpand()) {
					// 行上的树形大小
					if (isRow) {
						width = 1 + subWidth[0];
					} else {
						// 树的深度
						width = FrCpxTableModelUtil
								.getRowDimItemDeth(dimMembers) + subWidth[0];
					}
					// 树上所有节点的个数
					height = FrCpxTableModelUtil.getAllNodeCount(dimMembers)
							* subWidth[1];
				}
				// 如果是合并方式展开
				else {
					// 树的深度
					width = FrCpxTableModelUtil.getRowDimItemDeth(dimMembers)
							+ subWidth[0];
					// 树的叶子节点个数
					height = FrCpxTableModelUtil.getLeafNodeCount(dimMembers)
							* subWidth[1];
				}
			}
		}

		// 如果显示合计值 ，则高度加1
		if (frCpxDimensionItem.isShowSum()) {
			height++;
		}

		// 如果显示栏目标题，则宽度加1
		if (frCpxDimensionItem.isShowLabel()) {
			width++;
		}
		return new int[] { width, height };
	}

	/**
	 * 获取过滤后的字段栏目成员
	 * 
	 * @param fieldDimMembers
	 * @param valueByPreList
	 * @return
	 */
	private List<FrCpxFieldDimMember> getFilterMembers(
			List<FrCpxFieldDimMember> fieldDimMembers,
			List<String> valueByPreList) {
		List<FrCpxFieldDimMember> filterFieldMember = new ArrayList<FrCpxFieldDimMember>();
		if (fieldDimMembers != null) {
			for (FrCpxFieldDimMember fieldMember : fieldDimMembers) {
				// 如果在过滤后的值列表中，或者如果是固定成员，则留下
				if (valueByPreList.contains(fieldMember.getUniqueID())
						|| fieldMember.isSolidMember()) {
					filterFieldMember.add(fieldMember);
				}
			}
		}
		return filterFieldMember;
	}

	private FieldsRelationInfo getFieldRelationInfo(String preFieldItemKey) {
		if (cpxQueryResult == null) {
			return null;
		}
		// 根据字段过滤信息，将字段成员进行过滤
		FieldsRelationInfo[] fieldsRelationInfo = cpxQueryResult
				.getFieldsRelationInfo();
		for (FieldsRelationInfo fieldsRelationInfo2 : fieldsRelationInfo) {
			String key = fieldsRelationInfo2.getKey();
			if (key.indexOf(preFieldItemKey) != -1) {
				return fieldsRelationInfo2;
			}
		}
		return null;
	}

	/**
	 * 将排序后的字段栏目成员设置到字段栏目项中
	 * 
	 * @param frCpxAreaCell
	 * @param frCpxDimensionItem
	 * @param context
	 */
	private void setOrderedMemberToDimItem(FrCpxDimensionItem frCpxDimensionItem) {
		if (frCpxDimensionItem == null)
			return;
		FrCpxFieldDimension dimensionDef = (FrCpxFieldDimension) frCpxDimensionItem
				.getDimensionDef();
		if (dimensionDef == null)
			return;
		List<FrCpxFieldDimMember> members = dimensionDef.getMembers();
		if (members == null || members.size() == 0) {
			frCpxDimensionItem.setFieldDimMember(members);
			return;
		}
		int sortType = frCpxDimensionItem.getSortType();

		// 如果是不排序，则直接将字段栏目成员设置到栏目项中
		if (sortType == FrCpxDimensionSort.SORT_TYPE_UNDEFINED) {
			frCpxDimensionItem.setFieldDimMember(members);
			return;
		}

		FrCpxFieldDimMember[] dimMemberArray = members
				.toArray(new FrCpxFieldDimMember[members.size()]);
		// 升序或降序
		if (sortType == FrCpxDimensionSort.SORT_TYPE_ASCENDING
				|| sortType == FrCpxDimensionSort.SORT_TYPE_DESCENDING) {
			Map<String, SimplePinyinComparator> map = new LinkedHashMap<String, SimplePinyinComparator>();
			String fieldName = FrCpxConsts.FIELDDIM_RELVALUE;
			SimplePinyinComparator comparator = new SimplePinyinComparator(
					sortType == FrCpxDimensionSort.SORT_TYPE_ASCENDING,
					MemoryOrderMngUtil.getNullSortType(frCpxAreaCell));
			map.put(fieldName, comparator);
			FrCpxDataComparator fieldDatacomparator = new FrCpxDataComparator(
					map);
			Arrays.sort(dimMemberArray, fieldDatacomparator);
			List<FrCpxFieldDimMember> sortedMembers = new ArrayList<FrCpxFieldDimMember>();
			for (FrCpxFieldDimMember frCpxFieldDimMember : dimMemberArray) {
				sortedMembers.add(frCpxFieldDimMember);
			}
			frCpxDimensionItem.setFieldDimMember(sortedMembers);
			return;
		}

		// 自定义排序
		if (sortType == FrCpxDimensionSort.SORT_TYPE_USERSORT) {
			FrCpxDimensionSort dimensionSort = frCpxDimensionItem
					.getDimensionSort();
			if (dimensionSort == null) {
				frCpxDimensionItem.setFieldDimMember(members);
				return;
			}
			FreeFieldConverter converter = FrCpxExpressionUtils
					.getFieldConverter(context, dimensionDef.getSmartID());
			Map<String, SimplePinyinComparator> map = new LinkedHashMap<String, SimplePinyinComparator>();
			// 按照排序字段信息，将字段排序
			SortDescriptor sortDescriptor = dimensionSort.getSortDescriptor();
			SortItem[] sorts = sortDescriptor.getSorts();
			if (sorts != null) {
				for (SortItem sortItem : sorts) {
					String fieldName = converter.getConvertName(sortItem
							.getFieldName());
					SimplePinyinComparator comparator = new SimplePinyinComparator(
							!sortItem.isDescending(),
							MemoryOrderMngUtil.getNullSortType(frCpxAreaCell));
					map.put(fieldName, comparator);
				}
			}
			FrCpxDataComparator fieldDatacomparator = new FrCpxDataComparator(
					map);
			Arrays.sort(dimMemberArray, fieldDatacomparator);
			List<FrCpxFieldDimMember> sortedMembers = new ArrayList<FrCpxFieldDimMember>();
			for (FrCpxFieldDimMember frCpxFieldDimMember : dimMemberArray) {
				sortedMembers.add(frCpxFieldDimMember);
			}
			// 如果设置了固定成员，则加入固定成员信息
			List<FrCpxFieldValue> solidMembers = dimensionSort
					.getSolidMembers();
			if (solidMembers == null || solidMembers.size() == 0) {
				frCpxDimensionItem.setFieldDimMember(sortedMembers);
				return;
			}
			// 根据固定成员信息创建字段栏目成员
			List<FrCpxFieldDimMember> fieldSolidDimMembers = new ArrayList<FrCpxFieldDimMember>();
			for (FrCpxFieldValue solidMemberValue : solidMembers) {
				FrCpxFieldDimMember solidDimMember = new FrCpxFieldDimMember();
				solidDimMember.setSolidMember(true);
				solidDimMember.setFieldValue(solidMemberValue);
				FrCpxFieldDimMember sameValueDimMember = getSameValueDimMember(
						sortedMembers, solidDimMember);
				if (sameValueDimMember != null) {
					solidDimMember
							.setUniqueID(sameValueDimMember.getUniqueID());
				}
				fieldSolidDimMembers.add(solidDimMember);
			}
			// 如果仅显示固定成员
			if (dimensionSort.isShowSolidMembersOnly()) {
				frCpxDimensionItem.setFieldDimMember(fieldSolidDimMembers);
				return;
			}
			// 如果是固定成员优先显示，则需要将其他成员信息加入到固定成员列表中
			else {
				addOtherFieldMembers(fieldSolidDimMembers, sortedMembers);
				frCpxDimensionItem.setFieldDimMember(fieldSolidDimMembers);
				return;
			}
		}
	}

	/**
	 * 将其他字段栏目成员信息加入到固定栏目成员中
	 * 
	 * @param fieldSolidDimMember
	 * @param sortedMembers
	 */
	private void addOtherFieldMembers(
			List<FrCpxFieldDimMember> fieldSolidDimMembers,
			List<FrCpxFieldDimMember> sortedMembers) {
		if (fieldSolidDimMembers == null || sortedMembers == null)
			return;
		// 依次加入字段栏目成员
		for (FrCpxFieldDimMember frCpxFieldDimMember : sortedMembers) {
			if (!inSolidMember(fieldSolidDimMembers, frCpxFieldDimMember)) {
				fieldSolidDimMembers.add(frCpxFieldDimMember);
			}
		}
	}

	/**
	 * 判断某一字段栏目成员是否已经在固定成员列表中
	 * 
	 * @param fieldSolidDimMembers
	 * @param frCpxFieldDimMember
	 * @return
	 */
	private boolean inSolidMember(
			List<FrCpxFieldDimMember> fieldSolidDimMembers,
			FrCpxFieldDimMember frCpxFieldDimMember) {
		FrCpxFieldDimMember sameFieldMember = getSameValueDimMember(
				fieldSolidDimMembers, frCpxFieldDimMember);
		if (sameFieldMember != null)
			return true;
		return false;
	}

	/**
	 * 从成员列表中获取与指定成员值相同的成员
	 * 
	 * @param fieldSolidDimMembers
	 * @param frCpxFieldDimMember
	 * @return
	 */
	private FrCpxFieldDimMember getSameValueDimMember(
			List<FrCpxFieldDimMember> fieldDimMembers,
			FrCpxFieldDimMember frCpxFieldDimMember) {
		if (fieldDimMembers == null)
			return null;
		if (frCpxFieldDimMember == null)
			return null;
		;
		for (FrCpxFieldDimMember dimMember : fieldDimMembers) {
			FrCpxFieldValue oneFieldValue = dimMember.getFieldValue();
			FrCpxFieldValue fieldValue = frCpxFieldDimMember.getFieldValue();
			if (oneFieldValue.getValue().equals(fieldValue.getValue()))
				return dimMember;
		}
		return null;
	}

	/**
	 * 根据字段栏目查询结果，构建字段栏目成员，并将成员设置到字段栏目模型中
	 * 
	 * @param dimensionDef
	 *            ：字段栏目模型
	 * @param oneFielDimensionResult
	 *            ：字段栏目查询结果
	 */
	private void createFieldDimMember(FrCpxFieldDimension dimensionDef,
			FieldDimMemberResultInfo[] oneFielDimensionResult) {
		if (dimensionDef == null)
			return;
		if (oneFielDimensionResult == null
				|| oneFielDimensionResult.length == 0)
			return;
		// 根据字段栏目查询结果构建字段栏目成员---暂时不考虑树型结构，所有字段都是平级
		List<FrCpxFieldDimMember> fieldDimMembers = new ArrayList<FrCpxFieldDimMember>();
		for (FieldDimMemberResultInfo fieldDimMemberResultInfo : oneFielDimensionResult) {
			// 构建字段栏目成员
			FrCpxFieldDimMember newFieldDimMember = new FrCpxFieldDimMember();
			// 设置成员唯一编码
			newFieldDimMember.setUniqueID(fieldDimMemberResultInfo
					.getFieldMemberPK());
			// 设置成员值（显示值和实际值 + 所有字段信息）
			FrCpxFieldValue fieldValue = new FrCpxFieldValue();
			fieldValue.setDisValue(fieldDimMemberResultInfo.getDisValue());
			fieldValue.setValue(fieldDimMemberResultInfo.getRealValue());
			fieldValue.setFieldValueMap(fieldDimMemberResultInfo
					.getFieldValueMap());
			newFieldDimMember.setFieldValue(fieldValue);
			fieldDimMembers.add(newFieldDimMember);
		}
		// 将成员设置到字段栏目中
		dimensionDef.setFieldDimMember(fieldDimMembers
				.toArray(new FrCpxFieldDimMember[fieldDimMembers.size()]));
	}

	/**
	 * 从结果集中寻找某一个字段栏目的查询结果
	 * 
	 * @param cpxQueryResult
	 * @param id
	 * @return
	 */
	private FieldDimMemberResultInfo[] getOneFielDimensionResult(
			String dimensionId) {
		if (cpxQueryResult == null) {
			return new FieldDimMemberResultInfo[0];
		}
		// 获得所有字段的查询结果集
		FieldDimensionResultInfo[] fieldDimResInfos = cpxQueryResult
				.getFieldDimResInfos();
		if (fieldDimResInfos == null || fieldDimResInfos.length == 0) {
			return new FieldDimMemberResultInfo[0];
		}
		// 寻找字段栏目Id相匹配的查询结果
		for (FieldDimensionResultInfo fieldDimensionResultInfo : fieldDimResInfos) {
			if (fieldDimensionResultInfo.getDimensionID().equals(dimensionId)) {
				return fieldDimensionResultInfo.getMemberInfos();
			}
		}
		return new FieldDimMemberResultInfo[0];
	}

	/**
	 * 将栏目信息和合计信息同步到单元格中
	 * 
	 * @param frAreaCell
	 * @param dataModel
	 */
	public void sysDimAndCountInfoToDataModel(FrCpxExtAreaCell frAreaCell,
			CellsModel dataModel) {
		if (frAreaCell == null || dataModel == null)
			return;

		CellPosition crossPoint = frAreaCell.getAreaInfoSet().getCrossSetInfo()
				.getCrossPoint();
		// 区域大小
		AreaPosition area = frAreaCell.getArea();
		CellPosition[] split = area.split();
		// 遍历每一个单元格，同步单元格上的字段扩展信息
		for (CellPosition cellPosition : split) {
			Object extInfo = frAreaCell.getCellInfoSet().getExtInfo(
					cellPosition, FrCpxTableModelUtil.FRCPX_DIMINFO_KEY);
			if (extInfo != null) {
				dataModel.setBsFormat(cellPosition,
						FrCpxTableModelUtil.FRCPX_DIMINFO_KEY, extInfo);
				// 替换自定义栏目成员的表达式
				replaceUserDimMemberExpres(((FrCpxDimMemberInfo) extInfo)
						.getDimMemberItem());

				this.setDimensionFormat((FrCpxDimMemberInfo) extInfo,
						cellPosition, dataModel);
			}

			if (dataModel.getCell(cellPosition) == null
					|| dataModel.getCell(cellPosition).getValue() == null) {
				// 合计定义
				FrCpxCountDefSet countDefSet = frAreaCell.getFrCpxModel()
						.getCountDefSet();
				FrCpxDimensionItem[] countRangeDimItem = FrCpxAreaUtil
						.getCountRangeDimItem(cellPosition, frCpxAreaCell);

				// 如果是合计单元格
				if (countRangeDimItem != null && countRangeDimItem.length > 0) {
					// 获取对应合计单元的统计指标的bindingKey
					FrCpxMeasureItem countMeasureItem = FrCpxAreaUtil
							.getCountMeasureItem(cellPosition, frCpxAreaCell);
					if (countMeasureItem != null) {
						String bindingKey = countMeasureItem.getBindingKey();

						String uniqueID0 = countRangeDimItem[0].getUniqueID();
						// 如果不是合计交叉点
						if (countRangeDimItem.length == 1) {
							FrCpxCountDef countDef = countDefSet.getCountDef(
									bindingKey, uniqueID0);
							// 添加到扩展区域模型中
							frAreaCell.getCellInfoSet().addExtInfo(
									cellPosition,
									FrCpxTableModelUtil.FRCPX_COUNTINFO_KEY,
									countDef);
						}
						// 如果是合计交叉点
						else if (countRangeDimItem.length == 2) {
							String uniqueID1 = countRangeDimItem[1]
									.getUniqueID();
							FrCpxCountDef countDefCross = countDefSet
									.getCountDef(bindingKey, uniqueID0
											+ FrCpxConsts.ROW_AND_COL_SPLIT
											+ uniqueID1);
							// 添加到扩展区域模型中
							frAreaCell.getCellInfoSet().addExtInfo(
									cellPosition,
									FrCpxTableModelUtil.FRCPX_COUNTINFO_KEY,
									countDefCross);
						}
					}
					dataModel.setCellFormat(cellPosition.getRow(), cellPosition
							.getColumn(), FrCpxTableModelUtil.getCommonFormat(
							cellPosition, dataModel,
							AreaContentSet.COUNT_FORMAT));
				}

				this.setFormatAndConditionFormat(frAreaCell, dataModel,
						cellPosition);
			}
		}

		dataModel.putExtProp(ExtendAreaConstants.CELLPOS_REFER_MAP,
				new ExtDataModel(this.m_cpRef));
	}

	// 设置栏目相关的格式及数据
	private void setDimensionFormat(FrCpxDimMemberInfo dimInfo,
			CellPosition desPos, CellsModel dataModel) {
		if (dimInfo == null) {
			return;
		}
		// 格式态对应位置
		CellPosition posFmt = this.getExtAreaCellFormat()
				.getDimMeaPositionMap().get(dimInfo.getUniqueID());
		// 格式态对应单元
		Cell cellFmt = null;
		if (posFmt != null) {
			// 格式位置参照
			this.m_cpRef.put(desPos, posFmt);
			cellFmt = this.formatModel.getCell(posFmt.getRow(),
					posFmt.getColumn());
		}

		IFormat format = cellFmt == null ? null : cellFmt.getFormat();
		if (format == null) {
			if (dimInfo.isTitle()) {// 标题默认格式
				format = FrCpxTableModelUtil.getCommonFormat(desPos, dataModel,
						AreaContentSet.TITLE_FORMAT);
			} else if (dimInfo.isCount()) {// 合计默认格式
				format = FrCpxTableModelUtil.getCommonFormat(desPos, dataModel,
						AreaContentSet.COUNT_FORMAT);
				format = PropertyType.getNewFormatByType(format,
						PropertyType.DataType, TableConstant.CELLTYPE_NUMBER);
			} else {
				// 栏目设置的格式
				format = dimInfo.getDimMemberItem().getDimensionItem()
						.getFormat();
			}
		}
		// 设置格式
		dataModel.getCellIfNullNew(desPos.getRow(), desPos.getColumn())
				.setFormat(format);

		// 标题单元及合计单元对应的文本内容可以取对应的格式态单元设置的值
		if (cellFmt != null && (dimInfo.isTitle() || dimInfo.isCount())) {
			dataModel.getCellIfNullNew(desPos.getRow(), desPos.getColumn())
					.setValue(cellFmt.getValue());
		}
	}

	/**
	 * 处理指标区及合计行区的格式与条件格式
	 * 
	 * @param frAreaCell
	 * @param dataModel
	 * @param desPos
	 */
	private void setFormatAndConditionFormat(FrCpxExtAreaCell frAreaCell,
			CellsModel dataModel, CellPosition desPos) {
		// 格式态对应位置
		String uniqueID = FrCpxAreaUtil.getCountMeasureUniqueID(desPos,
				frAreaCell);
		if (uniqueID == null) {
			return;
		}
		CellPosition posFmt = this.getExtAreaCellFormat()
				.getDimMeaPositionMap().get(uniqueID);
		if (posFmt == null) {
			return;
		}

		this.m_cpRef.put(desPos, posFmt);

		Cell fmtCell = this.formatModel.getCell(posFmt);

		// 处理格式
		IFormat format = fmtCell == null ? null : fmtCell.getFormat();
		FrCpxMeasureItem meaInfo = (FrCpxMeasureItem) this
				.getExtAreaCellFormat().getCellInfoSet()
				.getExtInfo(posFmt, FrCpxTableModelUtil.FRCPX_MESINFO_KEY);
		if (format == null) {
			if (meaInfo != null) {
				format = meaInfo.getFormat();
			}
		}
		Cell dataCell = dataModel.getCell(desPos.getRow(), desPos.getColumn());
		if (dataCell == null && fmtCell != null) {
			dataCell = (Cell) fmtCell.clone();
			dataCell.setRow(desPos.getRow());
			dataCell.setCol(desPos.getColumn());
			TableUtilities.setCell(dataModel, desPos.getRow(),
					desPos.getColumn(), dataCell);
		}

		dataModel.setCellFormat(desPos.getRow(), desPos.getColumn(), format);

		// 处理条件格式
		IAreaCondition condition = (IAreaCondition) this.getExtAreaCellFormat()
				.getCellInfoSet()
				.getExtInfo(posFmt, ExtendAreaConstants.CELL_CONDITION_FORMAT);
		if (condition == null) {
			if (meaInfo != null) {
				condition = meaInfo.getAreaCondition();
			}
		}
		if (condition != null) {
			ConditionFormatModel.getInstance(dataModel).addCondition(desPos,
					condition);
		}
		this.fillFormular(desPos, posFmt, dataModel);
	}

	/**
	 * 填充公式
	 * @param desPos
	 * @param posFmt
	 * @param dataModel
	 */
	@SuppressWarnings("deprecation")
	private void fillFormular(CellPosition desPos, CellPosition posFmt,
			CellsModel dataModel) {
		
		AreaFormulaModel newFormula = AreaFormulaModel.getInstance(dataModel);
		AreaFormulaModel oldFormula = AreaFormulaModel.getInstance(formatModel);
		FormulaVO formulaVO = oldFormula.getDirectFml(posFmt);
		if(formulaVO==null){
			return;
		}
		
		BIAreaFormulaHandler handler = this.getDataFormulaHandler(dataModel);
		
		Hashtable<IArea, FormulaVO> mainForms = newFormula.getMainFmls();
		try {			
			IParsed objParsed = formulaVO.getLet();
			if (objParsed == null) {
				objParsed = handler.parseUserDefFormula(formulaVO.getContent());
				formulaVO.setLet(objParsed);
			}
			
			FormulaVO desFmlVo = (FormulaVO)formulaVO.clone();
			resetFormula(desFmlVo, posFmt, handler);
			objParsed = desFmlVo.getLet();
			
			String dtext = AreaFormulaUtil.modifyAbsFormula4BI(posFmt, desPos, desFmlVo.getContent(),
					objParsed, dataModel.getMaxRow(), dataModel.getMaxCol());
			
			//dtext = this.modifyAbsFormula4BI(cpRef,map, dtext, handler.parseUserDefFormula(dtext)) ;
			
			PerfWatch pwData = new PerfWatch(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("1413006_0", null, 
					"01413006-0856", null, new String [] {dtext})/* @res "进行公式解析：[{0}]" */);
			
			desFmlVo = new FormulaVO(desPos.toString(), dtext);
			IParsed newObjParsed = null;
			// System.out.print("替换公式" + dtext);
			handler.getReportModel().getContext().setAttribute(AbsFunc.KEY_FORMULA_CHANGED, true);
			newObjParsed = handler.parseUserDefFormula(desFmlVo.getContent());
			dtext = newObjParsed.toString();
			
			pwData.stop();

			handler.getReportModel().getContext().removeAttribute(AbsFunc.KEY_FORMULA_CHANGED);
			// System.out.println("————————" + dtext);
			PerfWatch pwData2 = new PerfWatch(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
					"1413006_0", null, "01413006-0857", null, new String[] {dtext})/* @res "[进行公式替换：[dtext]" */);
			// liuchun 20110428 修改，只将公式进行填充，不构建公式链
			handler.addDbFormulaForExtendModel(null, desPos, dtext, null, true);			
			mainForms.put(desPos, desFmlVo);
			
		} catch (ParseException e) {
			AppDebug.debug(e);
		}
	}
	
	/**
	 * 将公式中的没有区域参数的公式换成依赖区域的公式，例如：getfield换成getfield2
	 * 
	 * @param newFormula
	 * @param fmtArea
	 * @param formulaHandler
	 */
	private void resetFormula(FormulaVO newFormula, IArea fmtArea, BIAreaFormulaHandler formulaHandler) {
		GetFunc.replaceGetFuncToGetFunc2(newFormula, fmtArea, formulaHandler);
		GetTotalFunc.replaceGetFuncToGetFunc2(newFormula, fmtArea, formulaHandler);
		ColValueCondFunc.replaceFuncToFunc2(newFormula, fmtArea, formulaHandler);
		FormatAddressFunc.replaceFuncToFunc2(newFormula, fmtArea, formulaHandler);
		SerialNumberFunc2.replaceFuncToFunc2(newFormula, fmtArea, formulaHandler);
	}
	
	private BIAreaFormulaHandler getDataFormulaHandler(CellsModel cells) {
		if(this.m_BIAreaFormulaHandler==null){
			this.m_BIAreaFormulaHandler = new BIAreaFormulaHandler(cells);
			AbsAnaReportModel repModel = this.getReportModel();
			this.m_BIAreaFormulaHandler.setReportModel(repModel);
			this.m_BIAreaFormulaHandler.registerFuncDriver(new AreaFuncDriver(repModel));
			this.m_BIAreaFormulaHandler.registerFuncDriver(new CVSBatchFuncDriver(repModel));
			this.m_BIAreaFormulaHandler.registerFuncDriver(new GetFuncDriver(repModel));
			//注册参数公式驱动
			this.m_BIAreaFormulaHandler.registerFuncDriver(new VarFuncDriver(repModel));
			//nc数据格式驱动
			this.m_BIAreaFormulaHandler.registerFuncDriver(new NcDataFormatFuncDriver(repModel));
			//添加薪资解密函数  add by ward 2018-06-05
			this.m_BIAreaFormulaHandler.registerFuncDriver(new SalaryDecryptFuncDriver(repModel));
			this.m_BIAreaFormulaHandler.registerFuncDriver(new GetAreaFieldFuncDriver(repModel));
			ExcelStatCalcUtil.registExcelFuncDriver(this.m_BIAreaFormulaHandler.getCalcEnv());
		}

		return this.m_BIAreaFormulaHandler;

	}

	private AnaReportModel getReportModel() {
		if(this.formatModel==null){
			return null;
		}
		return (AnaReportModel) this.formatModel.getExtProp(AnaReportModel.class.getName());
	}

	/**
	 * 替换自定义栏目成员的表达式
	 * 
	 * @param dimMemberItem
	 * @return
	 */
	private void replaceUserDimMemberExpres(FrCpxDimMemberItem dimMemberItem) {
		FrCpxDimensionItem dimensionItem = dimMemberItem.getDimensionItem();
		if (dimensionItem.isFieldDimension())
			return;
		FrCpxDimMember dimMember = dimMemberItem.getDimMember();
		if (dimMember == null)
			return;
		FrCpxUserDimExpreInfo userDimExpreInfo = cpxQueryResult
				.getUserDimExpreInfo();
		if (userDimExpreInfo == null)
			return;
		Map<String, String> userDimExpres = userDimExpreInfo.getUserDimExpres();
		String dimMemberUniqKey = FrCpxUtils.generateOneUniqKey(dimMemberItem,
				false);

		FrCpxDimMember newDimMember = (FrCpxDimMember) dimMember.clone();
		newDimMember.setExpression(userDimExpres.get(dimMemberUniqKey));
		dimMemberItem.setDimMember(newDimMember);

		return;
	}

	/**
	 * 获得需要统计合计的数值
	 * 
	 * @param mesPos
	 * @param frCpxAreaCell
	 * @return
	 */
	public Object[] getCountValues(CellPosition mesPos,
			FrCpxExtAreaCell frCpxAreaCell) {
		FrCpxDimensionItem[] countRangeDimItem = FrCpxAreaUtil
				.getCountRangeDimItem(mesPos, frCpxAreaCell);
		if (countRangeDimItem == null || countRangeDimItem.length == 0)
			return null;
		List<Object> valueList = new ArrayList<Object>();
		String rowUniqKey = FrCpxAreaUtil.getRowOrColCombineUniqueKey(mesPos,
				frCpxAreaCell, true);
		String colUniqKey = FrCpxAreaUtil.getRowOrColCombineUniqueKey(mesPos,
				frCpxAreaCell, false);
		Map<String, Object> result = cpxQueryResult.getResult();
		for (Iterator<String> keySet = result.keySet().iterator(); keySet
				.hasNext();) {
			String key = (String) keySet.next();
			if (key.indexOf(rowUniqKey.substring(0, rowUniqKey.length() - 1)) != -1
					&& key.indexOf(colUniqKey.substring(0,
							colUniqKey.length() - 1)) != -1) {
				// 找出指标所绑定统计范围内的栏目成员
				List<IFrCpxUniqueValue> rowValues = new ArrayList<IFrCpxUniqueValue>();
				List<IFrCpxUniqueValue> colValues = new ArrayList<IFrCpxUniqueValue>();
				FrCpxUtils.calculateBindingObjects(key,
						frCpxAreaCell.getFrCpxModel(), rowValues, colValues);
				// 如果是单统计范围的合计
				if (countRangeDimItem.length == 1) {
					// 判断该指标对应的统计范围内的栏目成员是否是行栏目一级成员
					boolean[] checkResult = judgeDimMember(rowValues,
							countRangeDimItem[0]);
					// 如果已经从行栏目成员中找到该统计范围,则直接返回
					if (checkResult[0]) {
						if (checkResult[1]) {
							Object value = result.get(key);
							valueList.add(value);
						}
					}
					// 否则再从列栏目成员中寻找
					else {
						// 判断该指标对应的统计范围内的栏目成员是否是列栏目一级成员
						checkResult = judgeDimMember(colValues,
								countRangeDimItem[0]);
						if (checkResult[1]) {
							Object value = result.get(key);
							valueList.add(value);
						}
					}
				}
				// 如果是交叉点处的合计，则两个统计范围内的栏目，均需要筛选出一级成员
				else if (countRangeDimItem.length == 2) {
					// 判断该指标对应的统计范围内的栏目成员是否是行栏目一级成员
					boolean[] checkResultRow = null;
					boolean[] checkResultCol = null;
					checkResultRow = judgeDimMember(rowValues,
							countRangeDimItem[0]);
					if (checkResultRow[0]) {
						// 判断该指标对应的统计范围内的栏目成员是否是列栏目一级成员
						checkResultCol = judgeDimMember(colValues,
								countRangeDimItem[1]);
					} else {
						// 判断该指标对应的统计范围内的栏目成员是否是行栏目一级成员
						checkResultRow = judgeDimMember(rowValues,
								countRangeDimItem[1]);
						if (checkResultRow[0]) {
							// 判断该指标对应的统计范围内的栏目成员是否是列栏目一级成员
							checkResultCol = judgeDimMember(colValues,
									countRangeDimItem[0]);
						}
					}
					if (checkResultRow[1] && checkResultCol[1]) {
						Object value = result.get(key);
						valueList.add(value);
					}

				}
			}
		}

		return valueList.toArray(new Object[valueList.size()]);
	}

	/**
	 * 判断合计范围对应的栏目成员是否属于该栏目的一级成员
	 * 
	 * @param rowValues
	 * @param countRangeDimItem
	 */
	private boolean[] judgeDimMember(List<IFrCpxUniqueValue> rowValues,
			FrCpxDimensionItem countRangeDimItem) {
		// 判断统计范围是否在该栏目序列内
		boolean isRangeInUnqivalues = false;
		// 判断统计范围对应的栏目成员是否是一级成员
		boolean isFirstLevel = false;
		// 处理行栏目成员
		for (IFrCpxUniqueValue rowvalue : rowValues) {
			FrCpxDimMemberItem info = (FrCpxDimMemberItem) rowvalue;
			// 判断统计范围是否在行栏目中
			if (info.getDimensionItem().getUniqueID()
					.equals(countRangeDimItem.getUniqueID())) {
				isRangeInUnqivalues = true;
				// 如果是字段类型，当前默认只有一级，不需呀判断是否是一级成员
				if (countRangeDimItem.isFieldDimension()) {
					isFirstLevel = true;
					return new boolean[] { isRangeInUnqivalues, isFirstLevel };
				} else {
					FrCpxDimMember dimMember = info.getDimMember();
					if (dimMember == null)
						continue;
					// 判断该成员是否是一级成员
					FrCpxDimMember[] firLevelMembers = ((FrCpxUserDimension) countRangeDimItem
							.getDimensionDef()).getMembers();
					for (FrCpxDimMember frCpxDimMember : firLevelMembers) {
						if (frCpxDimMember.getUniqueID().equals(
								dimMember.getUniqueID())) {
							isFirstLevel = true;
							return new boolean[] { isRangeInUnqivalues,
									isFirstLevel };
						}
					}
				}
			}
		}
		return new boolean[] { isRangeInUnqivalues, isFirstLevel };
	}
}
