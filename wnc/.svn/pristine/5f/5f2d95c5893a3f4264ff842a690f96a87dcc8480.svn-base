package com.ufida.report.anareport.areaset;

import java.util.ArrayList;
import java.util.List;

import nc.pub.smart.metadata.Field;

import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.exarea.AreaCrossSetInfo;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaConstants;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaRepField;
import com.ufida.report.anareport.model.CommonStyleFormat;
import com.ufida.report.anareport.model.CommonStyleUtil;
import com.ufida.report.anareport.model.FieldCountDef;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.zior.exception.ForbidedOprException;
import com.ufsoft.script.AreaFmlExecutor;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.CommonExprCalcEnv;
import com.ufsoft.script.exception.ParseException;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.CombinedAreaModel;
import com.ufsoft.table.CombinedCell;
import com.ufsoft.table.IArea;
import com.ufsoft.table.format.IFormat;

/**
 * ExtendAreaSetUtilHelper.java
 * 
 * @author yuyangi
 * @create 2011-4-8
 */
class ExtendAreaSetUtilHelper {

	// 使用表格控件中的常量
	public final static int UNDEFINED = 0;
	public final static int CELL_MOVE_LEFT = 1;
	public final static int CELL_MOVE_UP = 2;
	public final static int DELETE_ROW = 3;
	public final static int DELTE_COLUMN = 4;
	public final static int CELL_MOVE_RIGHT = 5;
	public final static int CELL_MOVE_DOWN = 6;
	public final static int INSERT_ROW = 7;
	public final static int INSERT_COLUMN = 8;

	/**
	 * 移动扩展区域
	 */
	public static boolean moveExtendArea(ExtendAreaModel extendModel, CellsModel cm, ExtendAreaCell exCell,
			int stepRow, int stepCol) {
		AreaPosition area = exCell.getArea();
		ExtendAreaCell realExCell = extendModel.getExAreaByPK(exCell.getExAreaPK());
		area = (AreaPosition) area.getMoveArea(stepRow, stepCol);
		realExCell.setArea(area);
		return true;
	}

	/**
	 * 添加合并单元格
	 * 
	 * @param cm
	 * @param combineList
	 */
	public static void combineArea(CellsModel cm, List<CombinedCell> combineList) {
		CombinedAreaModel cam = CombinedAreaModel.getInstance(cm);
		// for(CombinedCell c : combineList) {
		// // 判断新的合并单元是否与原有的有重叠，如果有，删除原来的合并单元
		// if(cam.getCombineCells(c.getArea())!=null) {
		// cam.removeCombinedCell(cam.getCombineCells(c.getArea()));
		// }
		// cam.addCombinedCell(c);
		// }
		// @edit by ll at 2011-7-24,下午12:29:57 调整写法，减少调用次数
		CombinedCell[] all = cam.getCombineCells();
		ArrayList<CombinedCell> retList = new ArrayList<CombinedCell>();
		for (CombinedCell c : combineList) {
			if (c == null)
				continue;
			AreaPosition area = c.getArea();
			// 判断新的合并单元是否与原有的有重叠，如果有，删除原来的合并单元
			for (CombinedCell cc : all) {
				if (area.intersection(cc.getArea())) {
					retList.add(cc);
				}
			}
		}
		if (retList.size() > 0) {
			cam.removeCombinedCell(retList.toArray(new CombinedCell[0]));
		}
		for (CombinedCell cc : combineList) {
			cam.addCombinedCell(cc);
		}
	}

	/**
	 * 隐藏字段后，新的合并单元格列表
	 * 
	 * @param newexCell
	 * @param cm
	 * @param toMoveArea
	 * @param stepRow
	 * @param stepCol
	 * @return
	 */
	public static List<CombinedCell> getNewCombineArea(ExtendAreaCell newexCell, CellsModel cm,
			AreaPosition toMoveArea, int stepRow, int stepCol) {

		List<CombinedCell> combList = newexCell.getAreaInfoSet().getAllCombinedCell();
		if (combList == null) {
			return null;
		}
		List<CombinedCell> newCombinedCells = new ArrayList<CombinedCell>();
		for (CombinedCell cc : combList.toArray(new CombinedCell[0])) {
			boolean isTitleFormat = false;
			IFormat format = cm.getCellFormat(cc.getArea().getStart());
			if(format instanceof CommonStyleFormat) {
				CommonStyleFormat comFormat = (CommonStyleFormat)format;
//				String titleType1 = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TITLE);
				String titleType2 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERFIELD);
				String titleType3 = CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT;//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0",CommonStyleUtil.COMMON_FORMAT_TABLEHEADERCONTENT);
				if (
//					comFormat.getStyleType().equals(titleType1) ||
				    comFormat.getStyleType().equals(titleType2) ||
				    comFormat.getStyleType().equals(titleType3)) {
					isTitleFormat = true;
				}
			}
			if ((cc.getArea().intersection(toMoveArea) || cc.getArea().equals(toMoveArea))&&!isTitleFormat) {
				AreaPosition oldArea = cc.getArea();
				CombinedCell newCC = (CombinedCell) cc.clone();
				AreaPosition newArea = (AreaPosition) oldArea.getMoveArea(stepRow, stepCol);
				oldArea.getMoveArea(stepRow, stepCol);
				// AreaPosition newArea =
				// AreaPosition.getInstance(oldArea.getStart(),
				// oldArea.getEnd().getMoveArea(stepRow, stepCol));
				if (newArea == null) {
					if (oldArea.getStart().getColumn() == 0 || oldArea.getStart().getRow() == 0) {
						if (oldArea.getEnd().getMoveArea(stepRow, stepCol) != null) {
							newArea = AreaPosition.getInstance(oldArea.getStart(), oldArea.getEnd().getMoveArea(
									stepRow, stepCol));
							newCC.setArea(newArea);
							newCombinedCells.add(newCC);
						}
					}
				} else {
					if ((newArea.getStart().getColumn() - toMoveArea.getStart().getColumn()) < 0
							|| (newArea.getStart().getRow() - toMoveArea.getStart().getRow()) < 0) {
						CellPosition start = newArea.getStart().getMoveArea(-stepRow, -stepCol);
						CellPosition end = newArea.getEnd();
						if (start.getColumn() > end.getColumn() || start.getRow() > end.getRow()) {
							continue;
						}
						newArea = AreaPosition.getInstance(newArea.getStart().getMoveArea(-stepRow, -stepCol), newArea
								.getEnd());
						// newArea = (AreaPosition)newArea.getMoveArea(0,
						// (newArea.getStart().getColumn()-toMoveArea.getStart().getColumn()));
					}
					newCC.setArea(newArea);
					newCombinedCells.add(newCC);
				}
			} else {
				// AreaPosition oldArea = cc.getArea();
				CombinedCell newCC = (CombinedCell) cc.clone();
				// AreaPosition newArea =
				// (AreaPosition)oldArea.getMoveArea(stepRow, stepCol);
				// if(newArea == null) {
				// if(oldArea.getStart().getColumn() == 0 ||
				// oldArea.getStart().getRow() == 0) {
				// if(oldArea.getEnd().getMoveArea(stepRow, stepCol) != null) {
				// newArea = AreaPosition.getInstance(oldArea.getStart(),
				// oldArea.getEnd().getMoveArea(stepRow, stepCol));
				// newCC.setArea(newArea);
				// newCombinedCells.add(newCC);
				// }
				// }
				// } else {
				// newCC.setArea(newArea);
				newCombinedCells.add(newCC);
				// }
			}
		}
		// 清除所有与目标区域有交叠的合并区域
		CombinedAreaModel.getInstance(cm).removeCombinedCell(
				CombinedAreaModel.getInstance(cm).getCombineCells(toMoveArea));
		newexCell.getAreaInfoSet().getAllCombinedCell().clear();
		// 将新的合并区域加入到扩展区中
		for (CombinedCell c : newCombinedCells) {
			newexCell.getAreaInfoSet().addCombinedCell(c);
		}
		return newCombinedCells;
	}

	/**
	 * 删除合并单元
	 * 
	 * @param formatModel
	 * @param toRemove
	 */
	public static void removeCombinedCell(CellsModel formatModel, List<CombinedCell> toRemove) {
		for (CombinedCell c : toRemove) {
			CombinedAreaModel.getInstance(formatModel).removeCombinedCell(c.getArea());
		}
	}

	/**
	 * 删除合并单元
	 * 
	 * @param formatModel
	 * @param toRemove
	 */
	public static void removeCombinedCell(CellsModel formatModel, CombinedCell[] toRemove) {
		for (CombinedCell c : toRemove) {
			CombinedAreaModel.getInstance(formatModel).removeCombinedCell(c.getArea());
		}
	}

	/**
	 * 删除单元格
	 * 
	 * @param extendModel
	 * @param cm
	 * @param ec
	 * @param aimArea
	 * @param isHor
	 * @param delSize
	 */
	public static void deleteCells(ExtendAreaModel extendModel, CellsModel cm, ExtendAreaCell ec, AreaPosition aimArea,
			boolean isHor, int delSize) {
		int deleteType = UNDEFINED;
		if (aimArea == null)
			return;
		if (isHor) {
			deleteType = CELL_MOVE_LEFT;
		} else {
			deleteType = CELL_MOVE_UP;
		}
		if (isSupportDeleteCell(extendModel, cm, deleteType, aimArea, isHor)) {// 检查动态区域,组合单元
			CellPosition newAnchorPos = aimArea.getStart();
			AreaPosition toMoveArea = getToMoveArea(aimArea, deleteType, cm);
			cm.moveCells(toMoveArea, newAnchorPos);
			ArrayList<ExtendAreaCell> conExtAreaList = getContainExtArea(extendModel, toMoveArea, ec, isHor);
			moveExtendArea(extendModel, cm, conExtAreaList, isHor, delSize);// 移动扩展区域
		}
	}

	/**
	 * 插入单元格
	 * 
	 * @param extendModel
	 * @param cm
	 * @param ec
	 * @param aimArea
	 * @param isHor
	 * @param delSize
	 */
	public static void insertCells(ExtendAreaModel extendModel, CellsModel cm, ExtendAreaCell ec, AreaPosition aimArea,
			boolean isHor, int delSize) {

		int insertMethod = UNDEFINED;
		CellPosition newAnchorPos;
		CellPosition startCellPos = aimArea.getStart();
		if (isHor) {
			insertMethod = CELL_MOVE_RIGHT;
			newAnchorPos = (CellPosition) startCellPos.getMoveArea(0, delSize);
		} else {
			insertMethod = CELL_MOVE_DOWN;
			newAnchorPos = (CellPosition) startCellPos.getMoveArea(delSize, 0);
		}

		if (isSupportInsertCell(extendModel, cm, insertMethod, aimArea, isHor)) {// 检查动态区域,组合单元
			AreaPosition toMoveArea = getToMoveAreaIns(aimArea, insertMethod, cm);
			ArrayList<ExtendAreaCell> conExtAreaList = getContainExtArea(extendModel, toMoveArea, ec, isHor);
			moveExtendArea(extendModel, cm, conExtAreaList, isHor, delSize);
			cm.moveCells(toMoveArea, newAnchorPos);
		}
	}

	/**
	 * @i18n miufo00133=删除区域中含有可扩展区，不允许删除。
	 * @i18n miufo00134=移动区域中含有可扩展区，不允许删除。
	 */
	private static boolean isSupportDeleteCell(ExtendAreaModel extendModel, CellsModel cm, int deleteType,
			AreaPosition aimArea, boolean isHor) throws ForbidedOprException {
		ExtendAreaCell[] exCells = null;
		exCells = getIntersectionExCells(extendModel, aimArea, isHor);
		if (exCells != null && exCells.length != 0) {
			// 删除区域中含有可扩展区，不允许删除。
			// throw new ForbidedOprException(MultiLang
			// .getString("ufobmiufo00133"));
			return false;
		}
		AreaPosition toMoveArea = getToMoveArea(aimArea, deleteType, cm);
		exCells = getIntersectionExCells(extendModel, toMoveArea, isHor);
		if (exCells != null && exCells.length != 0) {
			// 移动区域中含有可扩展区，不允许删除。
			// throw new ForbidedOprException(MultiLang
			// .getString("ufobmiufo00134"));
			return false;
		}
		return true;
	}

	/**
	 * @i18n miufo00135=插入方向上不能存在可扩展区。
	 */
	private static boolean isSupportInsertCell(ExtendAreaModel extendModel, CellsModel cm, int insertType,
			AreaPosition aimArea, boolean isHor) throws ForbidedOprException {

		AreaPosition toMoveArea = getToMoveAreaIns(aimArea, insertType, cm);
		ExtendAreaCell[] exCells = getIntersectionExCells(extendModel, toMoveArea, isHor);
		if (exCells != null && exCells.length != 0) {
			// throw new ForbidedOprException(MultiLang
			// .getString("ufobmiufo00135"));
			return false;
		}
		return true;
	}

	/**
	 * 返回与区域area交叠的所有扩展区
	 * 
	 * @param a
	 * @return
	 */
	public static ExtendAreaCell[] getIntersectionExCells(ExtendAreaModel extendModel, IArea area, boolean isHor) {
		if (area == null) {
			return null;
		}
		ArrayList<ExtendAreaCell> list = new ArrayList<ExtendAreaCell>();
		ArrayList<ExtendAreaCell> containList = new ArrayList<ExtendAreaCell>();
		ExtendAreaCell[] cells = extendModel.getExtendAreaCells();
		for (ExtendAreaCell cell : cells) {
			AreaPosition area0 = cell.getArea();

			if (area0.intersection(area)) {
				if (!hasToMoveExtendArea(isHor, area, area0)) {
					list.add(cell);
				} else {
					containList.add(cell);
				}
			}
		}
		return list.toArray(new ExtendAreaCell[0]);
	}

	private static ArrayList<ExtendAreaCell> getContainExtArea(ExtendAreaModel extendModel, IArea area,
			ExtendAreaCell oldExCell, boolean isHor) {
		if (area == null) {
			return null;
		}
		ArrayList<ExtendAreaCell> containList = new ArrayList<ExtendAreaCell>();
		ExtendAreaCell[] cells = extendModel.getExtendAreaCells();
		for (ExtendAreaCell excell : cells) {
			AreaPosition area0 = excell.getArea();
			if (excell.getExAreaPK().equals(oldExCell.getExAreaPK())) {
				continue;
			}
			if (area0.intersection(area)) {
				if (hasToMoveExtendArea(isHor, area, area0)) {
					containList.add(excell);
				}
			}
		}
		return containList;
	}

	public static void moveExtendArea(ExtendAreaModel extendModel, CellsModel cm,
			ArrayList<ExtendAreaCell> containList, boolean isHor, int moveStep) {
		if (containList.size() > 0) {
			for (ExtendAreaCell cell : containList) {
				if (isHor) {
					// 行方向移动
					ExtendAreaCell oldCell = (ExtendAreaCell) cell.clone();
					moveExtendArea(extendModel, cm, cell, 0, moveStep);
					AreaCrossSetInfo areaCrossSetInfo = extendModel.getExAreaByPK(cell.getExAreaPK()).getAreaInfoSet()
							.getCrossSetInfo();
					if (areaCrossSetInfo != null) {
						areaCrossSetInfo.crossMoveArea(areaCrossSetInfo, extendModel.getExAreaByPK(cell.getExAreaPK())
								.getArea(), oldCell.getArea());
					}
				} else {
					// 列方向移动
					ExtendAreaCell oldCell = (ExtendAreaCell) cell.clone();
					moveExtendArea(extendModel, cm, cell, moveStep, 0);
					AreaCrossSetInfo areaCrossSetInfo = extendModel.getExAreaByPK(cell.getExAreaPK()).getAreaInfoSet()
							.getCrossSetInfo();
					if (areaCrossSetInfo != null) {
						areaCrossSetInfo.crossMoveArea(areaCrossSetInfo, extendModel.getExAreaByPK(cell.getExAreaPK())
								.getArea(), oldCell.getArea());
					}
				}
			}
		}
	}

	public static AreaPosition getToMoveArea(AreaPosition aimArea, int deleteType, CellsModel cm,
			ExtendAreaCell exCell, boolean isHor) {
		AreaPosition toMoveArea = null;
		CellPosition startPos = null;
		CellPosition endPos = null;
		if (isHor) {
			startPos = CellPosition.getInstance(exCell.getArea().getStart().getRow(), exCell.getArea().getEnd()
					.getColumn() + 1);
			// exCell.getArea().getEnd().getMoveArea(0, 1);
			endPos = CellPosition.getInstance(aimArea.getEnd().getRow(), cm.getColNum() - 1);
		} else {
			startPos = CellPosition.getInstance(exCell.getArea().getStart().getRow() + 1, exCell.getArea().getEnd()
					.getColumn());
			exCell.getArea().getEnd().getMoveArea(1, 0);
			endPos = CellPosition.getInstance(cm.getRowNum() - 1, aimArea.getEnd().getColumn());
		}
		toMoveArea = AreaPosition.getInstance(startPos, endPos);
		return toMoveArea;
	}

	/**
	 * 如果area0与AimArea在所占行内，则需要移动
	 * 
	 * @param isHor
	 * @param area0
	 * @return
	 */
	private static boolean hasToMoveExtendArea(boolean isHor, IArea aimArea, IArea area0) {
		boolean hasToMoveExtendArea = false;
		CellPosition aimStartPos = aimArea.getStart();
		CellPosition aimEndPos = aimArea.getEnd();
		CellPosition pos0Start = area0.getStart();
		CellPosition pos0End = area0.getEnd();
		if (isHor) {
			if (aimStartPos.getRow() <= pos0Start.getRow() && aimEndPos.getRow() >= pos0End.getRow()) {
				hasToMoveExtendArea = true;
			}
		} else {
			if (aimStartPos.getColumn() <= pos0Start.getColumn() && aimEndPos.getColumn() >= pos0End.getColumn()) {
				hasToMoveExtendArea = true;
			}
		}
		return hasToMoveExtendArea;
	}

	@SuppressWarnings("unused")
	private static boolean doCombineCells(ExtendAreaModel extendModel, CellsModel cm, ExtendAreaCell exCell,
			AreaPosition elseArea) {
		boolean flag = false;
		CombinedAreaModel combineModel = cm.getCombinedAreaModel();
		AreaPosition combineCells = cm.getCombinedCellArea(CellPosition.getInstance(""));
		for (CellPosition cp : elseArea.split()) {
			AreaPosition oldCombinedArea = cm.getCombinedCellArea(cp);

			// if() {
			combineModel.removeCombinedCell(oldCombinedArea);
			// }
		}
		cm.clearCells(elseArea);

		return flag;
	}

	/**
	 * @param aimArea
	 *            要删除的区域
	 * @param deleteType
	 *            移动类型(DeleteDialog.CELL_MOVE_LEFT,CELL_MOVE_UP)
	 * @param cellsModel
	 *            单元格模型
	 * @return AreaPosition 由于删除单元格引起移动的区域
	 */
	public static AreaPosition getToMoveArea(AreaPosition aimArea, int deleteType, CellsModel cellsModel) {
		CellPosition startCellPos;
		CellPosition endCellPos;
		if (deleteType == CELL_MOVE_LEFT) {
			if (aimArea.getStart().getMoveArea(0, aimArea.getWidth()) == null) {
				startCellPos = CellPosition.getInstance(aimArea.getStart().getRow(), 0);
			} else {
				startCellPos = (CellPosition) aimArea.getStart().getMoveArea(0, aimArea.getWidth());
			}
			endCellPos = CellPosition.getInstance(aimArea.getEnd().getRow(), cellsModel.getColNum() - 1);
		} else if (deleteType == CELL_MOVE_UP) {
			if (aimArea.getStart().getMoveArea(aimArea.getHeigth(), 0) == null) {
				startCellPos = CellPosition.getInstance(0, aimArea.getStart().getColumn());
			} else {
				startCellPos = (CellPosition) aimArea.getStart().getMoveArea(aimArea.getHeigth(), 0);
			}
			endCellPos = CellPosition.getInstance(cellsModel.getRowNum() - 1, aimArea.getEnd().getColumn());
		} else {
			throw new IllegalArgumentException();
		}
		return AreaPosition.getInstance(startCellPos, endCellPos);
	}

	/**
	 * 返回选定区域扩展方向上的区域
	 * 
	 * @param aimArea
	 * @param insertType
	 * @param cellsModel
	 * @return
	 */
	public static AreaPosition getToMoveAreaIns(AreaPosition aimArea, int insertType, CellsModel cellsModel) {
		CellPosition startCellPos = aimArea.getStart();
		CellPosition endCellPos;
		if (insertType == CELL_MOVE_DOWN) {
			endCellPos = CellPosition.getInstance(cellsModel.getRowNum() - 1, aimArea.getEnd().getColumn());
		} else if (insertType == CELL_MOVE_RIGHT) {
			endCellPos = CellPosition.getInstance(aimArea.getEnd().getRow(), cellsModel.getColNum() - 1);
		} else {
			throw new IllegalArgumentException();
		}
		return AreaPosition.getInstance(startCellPos, endCellPos);
	}

	/**
	 * 是否包含fld字段
	 * 
	 * @param areaContentSet
	 * @param fld
	 * @return
	 */
	static boolean hasField(AreaContentSet areaContentSet, AnaRepField fld) {
		if (fld.isAggrFld()) {
			FieldCountDef def = fld.getFieldCountDef();
			if (def != null) {
				String fname = def.getMainFldName();
				String rangeName = def.getRangeFldName();
				return hasStrField(areaContentSet, fname) || hasStrField(areaContentSet, rangeName);
			}
		}
		Field field = fld.getField();
		return hasStrField(areaContentSet, field.getExpression());
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
		if (fields == null || fields.length == 0 || fldName == null)
			return false;
		String tempFldName = null;
		fldName = fldName.toLowerCase();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null && fields[i].getField() != null) {
				tempFldName = fields[i].getField().getFldname();
			} else if (fields[i] != null) {
				tempFldName = fields[i].getFieldName();
			}
			if (fldName.startsWith("this.")) {// 如果是关联字段，去掉关联时拼接的this
				fldName = fldName.substring(5, fldName.length());
			}
			if(fldName.contains(".")) {// 如果去掉"this."后还有"."，说明该字段是关联字段，用startWith判断
				// 如果以tempFldName开头，则需要隐藏
				if (fldName.startsWith(tempFldName.toLowerCase())) {
					return true;
				}
			} else {// 如果没有"."，则说明是普通字段。用equals判断
				if (fldName.equals(tempFldName.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}
	
	static void setFormula(String exAreaPk, AbsAnaReportModel anaModel, String fldname, int type, String formula) {
		ExtendAreaCell exCell = anaModel.getExtendAreaModel().getExAreaByPK(exAreaPk);
		if(exCell == null) {
			return ;
		}
		CellPosition fmlPos = null;
		for(CellPosition pos : exCell.getArea().split()) {
			if(exCell.getCellInfoSet().getExtInfo(pos, ExtendAreaConstants.FIELD_INFO) != null) {
				AnaRepField repField = (AnaRepField)exCell.getCellInfoSet().getExtInfo(pos, ExtendAreaConstants.FIELD_INFO);
				if(type == AreaContentSetUtil.DETAIL) {
					if(repField.getFieldCountDef() != null) {
						continue;
					}
					if(repField.getField().getFldname().equals(fldname)) {
						fmlPos = pos;
						break;
					}
				} else if(type == AreaContentSetUtil.SUM) {
					if(repField.getFieldCountDef() != null) {
						Field field = repField.getFieldCountDef().getMainField();
						if(field.getFldname().equals(fldname)) {
							fmlPos = pos;
							break;
						}
					}
				} else if(type == AreaContentSetUtil.ALL) {
					if(repField.getFldname().equals(fldname)) {
						fmlPos = pos;
						break;
					}
				}
			}
		}
		if(fmlPos == null) {
			return ;
		}
		AreaFormulaModel formulaMode = AreaFormulaModel.getInstance(anaModel.getFormatModel());
		AreaFmlExecutor fmlExe = formulaMode.getAreaFmlExecutor();
		if(fmlExe == null) {
			fmlExe = new AreaFmlExecutor(anaModel.getContext(),anaModel.getFormatModel());
		}
		fmlExe.getParserProxy().getEnv().setExEnv(CommonExprCalcEnv.EXPR_EXEC_TIMEZONE,
				anaModel.getContext().getAttribute(FreeReportContextKey.REPORT_EXEC_TIMEZONE));
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
		StringBuffer showMessage = new StringBuffer();
		try {
			fmlExe.addDbDefFormula(showMessage, fmlPos, formula, null, false);
		} catch (ParseException e) {
			AppDebug.debug(e);
		}
	}
}
