package com.ufida.report.anareport.areaset.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.pub.smart.metadata.DataTypeConstant;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.model.SmartModel;

import org.apache.commons.lang.StringUtils;

import com.ufida.iufo.pub.tools.AppDebug;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaConstants;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.report.anareport.FreePrivateContextKey;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.IFldCountType;
import com.ufida.report.anareport.areaset.AreaContentSet;
import com.ufida.report.anareport.areaset.AreaFieldSet;
import com.ufida.report.anareport.areaset.CommonKeyMap;
import com.ufida.report.anareport.areaset.GridAreaSetUtil;
import com.ufida.report.anareport.areaset.GroupFieldSet;
import com.ufida.report.anareport.areaset.IAreaFieldBaseService;
import com.ufida.report.anareport.areaset.IAreaFieldSetService;
import com.ufida.report.anareport.areaset.SumByField;
import com.ufida.report.anareport.areaset.SumByLevelAreaSet;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaRepField;
import com.ufida.report.anareport.model.CombineField;
import com.ufida.report.anareport.model.CommonStyleUtil;
import com.ufida.report.anareport.model.FieldCountDef;
import com.ufida.report.anareport.model.IReportField;
import com.ufida.report.anareport.util.AnaReportFieldUtil;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.report.free.plugin.render.CollapsibleInfo;
import com.ufsoft.report.IufoFormat;
import com.ufsoft.script.AreaFmlExecutor;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.CommonExprCalcEnv;
import com.ufsoft.script.base.FormulaVO;
import com.ufsoft.script.exception.ParseException;
import com.ufsoft.table.AreaPosition;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.CombinedAreaModel;
import com.ufsoft.table.CombinedCell;
import com.ufsoft.table.IArea;
import com.ufsoft.table.IAreaAtt;
import com.ufsoft.table.format.IFormat;
import com.ufsoft.table.header.HeaderModel;

/**
 * 区域设置向导---扩展区设置工具方法
 * @created by zhujhc at 2013-11-27,下午7:29:41
 * @version BQ8 UAP
 * @since JDK1.6
 */ 
public class ExtendAreaSetWizard {
	
	public static final String KEY_ORIALAREAMAP = "orial_formatarea_map";
	private static final String KEY_CURR_EXCELLS = "curr_excells_model";
	private static final String KEY_CURR_FORMULA = "KEY_CURR_FORMULA";
	public static final String KEY_ORIALFORMULA = "orial_formular_model";
	public static final String KEY_FORMULA_TITLE = "_FORMULA_";// 公式标题
	
	public static void addFormat(AbsAnaReportModel anaModel, CellPosition pos, IFormat format) {
		anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn()).setFormat(format);
		if(pos.getColumn() == 0) {
			anaModel.getFormatModel().getCellIfNullNew(pos.getRow(), pos.getColumn()).setValue(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0886")/*@res "合计"*/);
		}
	}
	
	/**
	 * 常用格式
	 * @param styleType
	 * @param pos
	 * @param cellsModel
	 * @param newFormat
	 * @return
	 */
	public static IFormat getCommonFormat(CellPosition pos, CellsModel cellsModel, String styleType) {
		IFormat format = null;
		IFormat oldFormat = cellsModel.getCellFormatIfNullNew(pos);
		// 格式
		format = CommonStyleUtil.getFormat(oldFormat.getDataFormat(), styleType);
		return format;
	}
	
	/**
	 * 合并单元
	 * @param area
	 * @param format
	 * @param value
	 * @param formatModel
	 * @return true || false
	 */
	public static boolean setCombinedArea(AreaPosition area, IFormat format, Object value, AbsAnaReportModel anaModel) {
		CombinedAreaModel combModel = CombinedAreaModel.getInstance(anaModel.getFormatModel());
		combModel.addCombinedCell(new CombinedCell(area, format, value));
		return true;
	}
	
	public static StringBuffer addFormula(String formula, AbsAnaReportModel anaModel, IArea temp) {
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
	
	public static StringBuffer addFormula(AreaFieldSet fieldSet, AbsAnaReportModel anaModel, IArea temp) {
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
				//添加薪资解密函数  add by ward 2018-06-05
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
	
	/**
	 * 在指定单元位置上定义一个汇总字段
	 *
	 * @create by wanyonga at 2010-6-22,下午03:42:50
	 *
	 * @update by yuyangi 增加默认格式参数formatMap
	 */
	public static void addOneCountField(CellPosition pos, AbsAnaReportModel anaModel, AreaFieldSet fieldSet,
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
					if(areaFieldSrv != null&& areaFieldSrv instanceof IAreaFieldSetService) {
						formula = ((IAreaFieldSetService)areaFieldSrv).getAreaFieldFormula(countDef, rangeField);
					} 
					if(formula != null) {
						fieldSet.setFormula(formula);
					}

					addFormula(fieldSet, anaModel, pos);
					if (fieldSet.getFormat() != null) {
						IFormat defaultFormat = getCommonFormat(pos, anaModel.getFormatModel(), formatType);
						IFormat fieldFormat = fieldSet.getFormat();
						defaultFormat = IufoFormat.getInstance(fieldFormat.getDataFormat(), defaultFormat.getFont(), defaultFormat.getAlign(), defaultFormat.getLines());
						anaModel.getFormatModel().getCell(pos).setFormat(defaultFormat);
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
	 * 清除调整格式后面得空白列
	 * @param cellsModel
	 */
	public static void clearEmptyCols(CellsModel cellsModel) {
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
	
	/**
	 * 设置扩展区
	 * @param exAreaPk
	 * @param formatModel
	 * @return ExtendAreaCell
	 */
	public static ExtendAreaCell getSetExCell(String exAreaPk, CellsModel formatModel) {
		if (exAreaPk == null) {
			return null;
		}
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		// @edit by ll at 2010-10-20,上午10:35:59 增加区域备份处理
		@SuppressWarnings("unchecked")
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
	
	public static void clearExCellFromCache(String exAreaPk, AbsAnaReportModel reportmodel) {
		if (exAreaPk == null) {
			return;
		}
		CellsModel formatModel = reportmodel.getFormatModel();
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(formatModel);
		@SuppressWarnings("unchecked")
		HashMap<String, ExtendAreaCell> orialAreaMap = (HashMap<String, ExtendAreaCell>) exModel.getRepLevelInfo(KEY_CURR_EXCELLS);
		if (orialAreaMap == null) {
			orialAreaMap = new HashMap<String, ExtendAreaCell>();
			exModel.addRepLevelInfo(KEY_CURR_EXCELLS, orialAreaMap);
		} else {
			orialAreaMap.remove(exAreaPk);
		}
		exModel.removeRepLevelInfo(KEY_CURR_FORMULA);
	}
	
	/**
	 * 将原始格式中字段上一行的标题保存起来，在重新设置的时候可以保存标题信息
	 * @param formatModel
	 * @param exCell
	 * @return
	 */
	public static Map<String, String[]> getFieldTitles(
			AreaContentSet areaContentSet, AbsAnaReportModel anaModel,
			ExtendAreaCell exCell, AreaPosition unmoveArea) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		// 如果原来是交叉表，不记录标题内容，直接改成分组报表。
		if(exCell.getAreaInfoSet().isCrossSet()) {
			return map;
		}
		String[] titles = null;
		String title = null;
		String expression = "";
		AreaPosition area = exCell.getArea();
		/*int fldRow = 0;*/
		int eachTitleLen = getEachTitleLen(exCell, area);
		for(CellPosition c : area.split()) {
			if(unmoveArea != null && unmoveArea.contain(c)) {
				continue;
			}
			AnaRepField anaFld = null;
			Object obj = exCell.getCellInfoSet().getExtInfo(c, ExtendAreaConstants.FIELD_INFO);
//			if(obj == null){
//				//从CellInfoSet中取anaFieldInfo不对，要从Cell中取
//				obj = exCell.getCellsModel().getBsFormat(c, ExtendAreaConstants.FIELD_INFO);
//			}
			if(obj != null) {
				//if(/*!exCell.getAreaInfoSet().isCrossSet() &&*/ c.getRow() > area.getEnd().getRow()) {
				//	continue;
				//}
				
				/*if(c.getRow() < fldRow) {
					fldRow = c.getRow();	
				}*/
				anaFld = (AnaRepField)obj;
				if(anaFld.getField() == null) {
					expression = anaFld.getFieldID();
				} else {
					Field fld = anaFld.getField();
					if(fld instanceof FieldCountDef){
						fld = ((FieldCountDef) fld).getMainField();
					}
					expression = fld.getExpression();
				}
				
				/*int len = c.getRow() - area.getStart().getRow();
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
				}*/
				titles = new String[eachTitleLen];
				int FstTitlecolumn = c.getColumn();
				int FstTitlerow = area.getStart().getRow();
				for(int i = 0; i < eachTitleLen; i++){
					CellPosition pos =  CellPosition.getInstance(FstTitlerow + i, FstTitlecolumn);
					title = (String)exCell.getCellInfoSet().getExtInfo(pos, ExtendAreaConstants.CELL_VALUE);
					if(StringUtils.isEmpty(title)) {
						FormulaVO formula = (FormulaVO)exCell.getCellInfoSet().getExtInfo(pos, ExtendAreaConstants.FORMULA);
						if(formula != null) {
							title = KEY_FORMULA_TITLE + formula.getContent();
						}
					}
					titles[eachTitleLen-1-i] = title;
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
		return map;
	}
	
	/**
	 * 获取扩展区中标题所占行数
	 * @param area
	 * @return
	 */
	private static int getEachTitleLen(ExtendAreaCell exCell, AreaPosition area) {
		int titleLen = 0;
		int areaHeight = area.getHeigth();
		int areaWidth = area.getWidth();
		int firstRow = area.getStart().getRow();
		int firstColumn = area.getStart().getColumn();

		// 按列向右遍历，其中每一列向下遍历，碰到的第一个Field决定标题所占的行数
		outer: for(int j = 0; j < areaWidth; j++){
			for(int i = 0; i < areaHeight; i++){
				CellPosition  c = CellPosition.getInstance(firstRow + i, firstColumn + j);
				Object obj = exCell.getCellInfoSet().getExtInfo(c, ExtendAreaConstants.FIELD_INFO);
//				if(obj == null){
//					//从CellInfoSet中取anaFieldInfo不对，要从Cell中取
//					obj = exCell.getCellsModel().getBsFormat(c, ExtendAreaConstants.FIELD_INFO);
//				}
				if(obj != null){
					titleLen = c.getRow() - area.getStart().getRow();
					break outer;
				}
			}
		}
		
		Object collaps = exCell.getExtFmt(CollapsibleInfo.class.getName());
		Object sumByLvl = exCell.getExtFmt(SumByLevelAreaSet.class.getName());
		if (collaps != null && sumByLvl != null && !(sumByLvl instanceof String)) {//级次汇总
			//没有显示在前的合计
			titleLen--;
		}else{// 没有级次，或者是级次但是不是级次汇总
			// 如果选中小计在前，并且显示合计，那么标题行下一行是合计行，所以要将title的长度减1
			if (exCell.getExtFmt(FreePrivateContextKey.KEY_IS_SUBTOTAL_TYPE) != null
					&& exCell.getExtFmt(FreePrivateContextKey.KEY_IS_SHOW_TOTAL) != null) {
				int subtotalType = (Integer) exCell
						.getExtFmt(FreePrivateContextKey.KEY_IS_SUBTOTAL_TYPE);
				boolean showTotal = (Boolean) exCell
						.getExtFmt(FreePrivateContextKey.KEY_IS_SHOW_TOTAL);
				if (subtotalType == AreaContentSet.SUBTOTAL_BEFORE_GROUP && showTotal) {
					titleLen--;
				}
				// 上次设置了级次汇总时，显示合计且小计在前，subtotalType值为GROUP_NO_COMBINED
				else if(subtotalType == AreaContentSet.GROUP_NO_COMBINED){
					titleLen--;
				}
			}
		}

		return titleLen;
	}
	
	public static AreaFieldSet[] getDetailWithoutGroup(AreaContentSet areaContentSet) {
		List<AreaFieldSet> newDetail = new ArrayList<AreaFieldSet>();
		AreaFieldSet[][] groupFieldSets = areaContentSet.getGroupFldNames();
		AreaFieldSet[] detailFields = areaContentSet.getDetailFldNames();
		if(detailFields == null || detailFields.length == 0) {
			return new AreaFieldSet[0];
		}
		AreaFieldSet[] aggrFeilds = areaContentSet.getAreaFieldSet();
		//@edit by zhujhc at 2012-10-25,下午08:44:47 
		//这种情况的处理会造成下面的情况无效：分组列表无字段，明细和统计列表有字段，再次应用时，分组列表无字段，明细和统计列表有字段  
		//向统计字段列表中添加新字段不能添加到明细字段列表中，造成后面添加重复字段的错误。
		if(groupFieldSets == null || groupFieldSets.length == 0) {
			//@@edit by zhujhc at 2012-10-25,下午09:30:52
			//将原来明细列表中的字段添加到新的newDetail中，为后面添加统计字段isInNewDetails()方法做判断。
			for(AreaFieldSet detailField : detailFields){
				newDetail.add(detailField);
			}
			//@edit by zhujhc at 2012-10-25,下午08:52:29
			//分组列表为空的时候，也需要遍历一下统计列表，把新增的字段添加到明细列表中，提出公共代码为一个新的方法
			getNewDetail(newDetail,aggrFeilds);
//			return detailFields;
			return newDetail.toArray(new AreaFieldSet[0]);
		}
		boolean isGroupField = false;
		if(detailFields != null) {
			for(AreaFieldSet detialField : detailFields) {
				isGroupField = false;
				for(AreaFieldSet[] groupFields : groupFieldSets) {
					if(isGroupField == true) {
						break;
					}
					if(groupFields != null) {
						for(AreaFieldSet groupField : groupFields) {
							if(groupField.getField() != null && detialField.getField() != null) {
								if(areaContentSet instanceof SumByLevelAreaSet) {
									if(groupField.getField() != null && detialField.getField() != null) {
										if(groupField.getField().getExpression().startsWith(detialField.getField().getExpression())
												|| groupField.getField().getExpression().startsWith(detialField.getField().getFldname())) {
											isGroupField = true;
											break;
										}
									}
								} else {
									if(groupField.getField().getExpression().equals(detialField.getField().getExpression())) {
										isGroupField = true;
										break;
									}
								}
							}
						}
					}
				}
				if(!isGroupField) {
					newDetail.add(detialField);
				}
//				else {
//					if(isAggrField(aggrFeilds, detialField)) {
//						newDetail.add(detialField);
//					}
//				}
			}
		}
		//@edit by zhujhc at 2012-10-29,下午04:16:57 提出公共代码为一个新的方法
		getNewDetail(newDetail,aggrFeilds);
		return newDetail.toArray(new AreaFieldSet[0]);
	}
	
	/**
	 * 将不在明细列表的统计字段添加明细列表中
	 * 
	 * @param newDetail
	 * @param aggrFeilds
	 * @return newDetail
	 * @since nc6.3
	 * @author zhujhc at 2012-10-29,下午04:14:15
	 */
	private static List<AreaFieldSet> getNewDetail(List<AreaFieldSet> newDetail,
			AreaFieldSet[] aggrFeilds){
		for(AreaFieldSet a : aggrFeilds) {
			if(a != null) {
				AreaFieldSet newField = new AreaFieldSet(a.getField());
				if(a.getFieldName() != null) {
					newField.setFieldName(a.getFieldName());
					if(!isInNewDetails(newDetail, newField)) {
						newDetail.add(newField);
					}
				}
			}
		}
		return newDetail;
	}
	
	/**
	 * <p>分组但不统计的列</p>
	 * @param areaContentSet
	 * @return
	 */
	public static List<Integer> getGroupNoCountList(AreaContentSet areaContentSet) {
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
	
	/**
	 * 判断字段是否在明细列表中
	 * 
	 * @param newDetail 新的明细列表
	 * @param detialField 明细字段
	 * @return false || true
	 * @since ncv63
	 * @modify zhujhc 
	 */
	private static boolean isInNewDetails(List<AreaFieldSet> newDetail, AreaFieldSet detialField) {
		for(AreaFieldSet aggrFeild : newDetail) {
			if(aggrFeild.getField() != null && detialField.getField() != null) {
				if(aggrFeild.getField().getExpression().equals(detialField.getField().getExpression())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static String checkAreaContentSet(AreaContentSet areaContentSet, ExtendAreaCell exCell, SmartModel smartModel) {
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
	
	/**
	 * 获得的组装后的数据级字段信息
	 * @param areaFieldSet
	 * @created by zhujhc at 2013-11-29,下午4:23:48
	 * @version BQ8 UAP
	 * @since JDK1.6
	 */ 
	public static AnaRepField getAnaRepFieldByFldType(AreaFieldSet areaFieldSet, SmartModel smartModel,int anaRepFieldType){
		AnaRepField anaRepField = null ;
		if(areaFieldSet == null || !(areaFieldSet instanceof GroupFieldSet)){
			return anaRepField;
		}
		GroupFieldSet groupFieldSet =(GroupFieldSet) areaFieldSet;
		if(groupFieldSet.getShowField() == null){
			anaRepField = new AnaRepField(groupFieldSet.getField(), anaRepFieldType, smartModel.getId());
		}else{
			CombineField combineField = new CombineField(groupFieldSet.getField(), groupFieldSet.getShowField(), new Field[]{groupFieldSet.getShowField()});
			anaRepField = new AnaRepField(combineField, anaRepFieldType, smartModel.getId());
		}
		return anaRepField;
	}
	


	
	/**
	 * 根据字段类型获得扩展区中所有字段（目前仅包含分节字段）
	 * @created by zhujhc at 2013-12-3,下午2:20:23
	 * @version BQ8 UAP
	 * @since JDK1.6
	 */ 
	public static Field[] getFieldArrByType(CellsModel formatModel, ExtendAreaCell exCell) {
		CellPosition[] posArr = exCell.getArea().split();
		AnaRepField anaField = null;
		Field field = null;
		List<Field> fieldList = new ArrayList<Field>();
		List<String> expList = new ArrayList<String>();
		for (CellPosition fmtPos : posArr) {
			anaField = GridAreaSetUtil.getAnaFieldByPos(formatModel, exCell, fmtPos);
			if (anaField != null) {
				field = anaField.getField();
				if (field != null
						&& anaField.getFieldType() == AnaRepField.TYPE_GROUP_FIELD) {
					if (!expList.contains(field.getExpression())) {
						expList.add(field.getExpression());
						fieldList.add(field);
					}
				}
			}
		}
		return fieldList.toArray(new Field[0]);
	}
	
	/**
	 * 获得扩展区中分组统计字段
	 * @created by zhujhc at 2013-12-3,下午4:15:52
	 * @version BQ8 UAP
	 * @since JDK1.6
	 */ 
	public static List<String> getGroupCountList(CellsModel formatModel, ExtendAreaCell exCell){
		List<String> groupCountlist = new ArrayList<String>();
		Field[] calcFlds = getAllCountFields(formatModel, exCell);
		Field field = null;
		for (Field fld : calcFlds) {
			// 字段的名称中有表别名信息，table.column截取column的值
			field = fld;
			if (field != null) {
				if (fld != null) {
					if (fld instanceof IReportField) {
						if (((IReportField) fld).getRangeFldName() != null) {
							groupCountlist.add(((IReportField) fld)
									.getRangeFldName());
						}
					}
				}
			}
		}
		return groupCountlist;
	}
	
	/**
	 * 获得扩展区中所有统计的字段
	 * @created by zhujhc at 2013-12-3,下午4:00:46
	 * @version BQ8 UAP
	 * @since JDK1.6
	 */ 
	public static Field[] getAllCountFields(CellsModel formatModel, ExtendAreaCell exCell) {
		CellPosition[] posArr = exCell.getArea().split();
		AnaRepField anaField = null;
		Field field = null;
		List<Field> fieldList = new ArrayList<Field>();
		List<String> expList = new ArrayList<String>();
		for (CellPosition fmtPos : posArr) {
			anaField = GridAreaSetUtil.getAnaFieldByPos(formatModel, exCell, fmtPos);
			if (anaField != null
					&& !(anaField.getFieldType() == AnaRepField.TYPE_DETAIL_FIELD)) {
				field = anaField.getField();
				if (field instanceof FieldCountDef) {
					FieldCountDef countDef = (FieldCountDef) field;
					if (!expList.contains(countDef.getUniqueName())) {
						expList.add(countDef.getUniqueName());
						fieldList.add(countDef);
					}
				}
			}
		}
		return fieldList.toArray(new Field[0]);
	}
	

}
