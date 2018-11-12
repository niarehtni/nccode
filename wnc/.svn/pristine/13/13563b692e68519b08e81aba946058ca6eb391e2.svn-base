package com.ufida.report.anareport.model;

import java.util.Hashtable;

import nc.pub.smart.metadata.DataTypeConstant;
import nc.vo.pub.lang.UFDouble;

import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.model.BaseSmartQueryUtil;
import com.ufida.iufo.table.model.ISmartQueryUtil;
import com.ufida.report.anareport.FreeReportContextKey;
import com.ufida.report.anareport.IFldCountType;
import com.ufida.report.anareport.data.AbsRowData;
import com.ufida.report.anareport.data.DefaultRowData;
import com.ufida.report.anareport.util.AnaReportFieldUtil;
import com.ufida.report.crosstable.DimInfo.ValueInfo;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.report.IufoFormat;
import com.ufsoft.report.constant.PropertyType;
import com.ufsoft.script.AreaFmlExecutor;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.script.base.CommonExprCalcEnv;
import com.ufsoft.table.CellPosition;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.format.CellAlign;
import com.ufsoft.table.format.CellFont;
import com.ufsoft.table.format.CellLines;
import com.ufsoft.table.format.IFormat;
import com.ufsoft.table.format.INumberFormat;
import com.ufsoft.table.format.NumberFormat;
import com.ufsoft.table.format.TableConstant;
import com.ufsoft.table.format.UFDoubleFormat;

/**
 * 
 * 自由报表分析模型
 * 
 */
public class AnaReportModel extends AbsAnaReportModel {
	private static final long serialVersionUID = 1L;
	private transient BaseSmartQueryUtil m_smartUtil = null;
	private transient Hashtable<CellPosition, IFormat> m_dCellFormat = null;//

	private AnaReportModel(CellsModel cellsModel) {
		super(cellsModel);
	}

	private AnaReportModel() {
	}

	public static AnaReportModel getInstance(CellsModel cellsModel) {
		AnaReportModel model = (AnaReportModel) cellsModel.getExtProp(AnaReportModel.class.getName());
		if (model == null) {
			model = new AnaReportModel(cellsModel);
			cellsModel.putExtProp(AnaReportModel.class.getName(), model);
			model.setFormatCellsModel(cellsModel);
		} else {
			model.setFormatCellsModel(cellsModel);
			if (model.loadFromDB == 0)
				model.readObject0();
		}
		return model;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AnaReportModel newObj = new AnaReportModel();
		newObj.m_dataSource = this.m_dataSource;
		newObj.setFormatCellsModel(getFormatModel());
		return newObj;
	}

	@Override
	public ISmartQueryUtil getSmartUtil() {
		if (m_smartUtil == null)
			m_smartUtil = new BaseSmartQueryUtil();
		return m_smartUtil;
	}

	/**
	 * 
	 * @see com.ufida.report.anareport.model.AbsAnaReportModel#createFormulaModel()
	 */
	@Override
	public AreaFormulaModel createFormulaModel(CellsModel dataModel) {
		AreaFormulaModel formulaMode = AreaFormulaModel.getInstance(dataModel);
		AreaFmlExecutor fmlExe = formulaMode.getAreaFmlExecutor();
		if (fmlExe == null) {
			fmlExe = new AreaFmlExecutor(getContext(),dataModel);
		}
		formulaMode.setAreaFmlExecutor(fmlExe);
		fmlExe.getParserProxy().getEnv().setExEnv(CommonExprCalcEnv.EXPR_EXEC_TIMEZONE,
						getContext().getAttribute(FreeReportContextKey.REPORT_EXEC_TIMEZONE));
		// 参数/变量公式
		VarFuncDriver varFunDriver = (VarFuncDriver) fmlExe.getFuncListInst().getExtDriver(
				VarFuncDriver.class.getName());
		if (varFunDriver == null) {
			varFunDriver = new VarFuncDriver(this);
			fmlExe.registerFuncDriver(varFunDriver);
		}
		// getfield/caption/gettotal公式
		GetFuncDriver getFunDriver = (GetFuncDriver) fmlExe.getFuncListInst().getExtDriver(
				GetFuncDriver.class.getName());
		if (getFunDriver == null) {
			getFunDriver = new GetFuncDriver(this);
			fmlExe.registerFuncDriver(getFunDriver);
		}
		// CVS/CVN公式
		CVSBatchFuncDriver driver2 = (CVSBatchFuncDriver) fmlExe.getFuncListInst().getExtDriver(
				CVSBatchFuncDriver.class.getName());
		if (driver2 == null) {
			driver2 = new CVSBatchFuncDriver(this);
			fmlExe.registerFuncDriver(driver2);
		}
		// nc数据格式公式驱动
		NcDataFormatFuncDriver ncDataFormatFuncDriver = (NcDataFormatFuncDriver) fmlExe.getFuncListInst().getExtDriver(
				NcDataFormatFuncDriver.class.getName());
		if (ncDataFormatFuncDriver == null) {
			ncDataFormatFuncDriver = new NcDataFormatFuncDriver(this);
			fmlExe.registerFuncDriver(ncDataFormatFuncDriver);
		}
		
		//添加薪资解密函数  add by ward 2018-06-05
		SalaryDecryptFuncDriver  salaryDecryptFuncDriver = (SalaryDecryptFuncDriver)fmlExe.getFuncListInst().getExtDriver(
				SalaryDecryptFuncDriver.class.getName());
		if(salaryDecryptFuncDriver == null) {
			salaryDecryptFuncDriver = new SalaryDecryptFuncDriver(this);
			fmlExe.registerFuncDriver(salaryDecryptFuncDriver);
		}

		GetAreaFieldFuncDriver getAreaFunc = (GetAreaFieldFuncDriver) fmlExe.getFuncListInst().getExtDriver(
				GetAreaFieldFuncDriver.class.getName());
		if (getAreaFunc == null) {
			getAreaFunc = new GetAreaFieldFuncDriver(this);
			fmlExe.registerFuncDriver(getAreaFunc);
		}
		ExcelStatCalcUtil.registExcelFuncDriver(fmlExe);
		return formulaMode;
	}

	public IFormat getDimFormat(AreaDataModel dataModel, ExtendAreaCell m_exCell, CellPosition srcPos,
			CellPosition aimPos, IFormat cellFormat, AnaRepField fld, Object realValue, AbsRowData currData) {
		if (fld == null)
			return cellFormat;

		int type = fld.getFieldDataType();
		boolean isCalcInt = (fld.isAggrFld() && (fld.getFieldCountDef().getCountType() == IFldCountType.TYPE_COUNT || fld
				.getFieldCountDef().getCountType() == IFldCountType.TYPE_COUNT_DISTINCT));
		if (fld.getRankInfo() != null && fld.getRankInfo().isEnabled())
			isCalcInt = true;
		if (realValue != null) {
			ValueInfo valueInfo = fld.getDimInfo().getValueInfo(realValue);
			if (valueInfo != null) {
				IFormat dataFormat = valueInfo.getDataFormat();
				if (dataFormat != null)
					cellFormat = dataFormat;
			}
		}

		if (DataTypeConstant.isNumberType(type) || isCalcInt) {
			if (cellFormat == null) {
				if (!getCellFormatCache().containsKey(srcPos)) {
					cellFormat = IufoFormat.getInstance(NumberFormat.getInstance(), CellFont.getInstance(),
							CellAlign.getInstance(), CellLines.getInstance());
					getCellFormatCache().put(srcPos, cellFormat);
				}
				cellFormat = getCellFormatCache().get(srcPos);
			} else {
				if (!((cellFormat.getDataFormat() instanceof NumberFormat) || (cellFormat.getDataFormat() instanceof UFDoubleFormat)))// 需要修改成数值类型
					cellFormat = IufoFormat.getInstance(NumberFormat.getInstance(), cellFormat.getFont(),
							cellFormat.getAlign(), cellFormat.getLines());
			}
			if (AnaDataSetTool.isInteger(type) || isCalcInt) {// 整数设置小数位数
				cellFormat = PropertyType.getNewFormatByType(cellFormat, PropertyType.DecimalDigits, 0);
			} else if (cellFormat.getCellType() == TableConstant.CELLTYPE_NUMBER) {// 普通数据型
				INumberFormat numFormat = (INumberFormat) cellFormat.getDataFormat();
				if (numFormat.getDecimalDigits() == -1) {// 精度为-1，表示按照数据对象的小数位数来显示
					int digit = -1;
					if (realValue instanceof UFDouble) {
						digit = ((UFDouble) realValue).getPower();
						numFormat = (NumberFormat) NumberFormat.getInstance(numFormat.getComma(),
								numFormat.getPercent(), digit, numFormat.getCurrencySymbol(),
								numFormat.getChineseFormat(), numFormat.getMinusFormat());

						cellFormat = IufoFormat.getInstance(numFormat, cellFormat.getFont(), cellFormat.getAlign(),
								cellFormat.getLines());
					}
				}
			} else if (cellFormat.getCellType() == TableConstant.CELLTYPE_BIGNUMBER) {// 大数据类型
				UFDoubleFormat doubleFormat = (UFDoubleFormat) cellFormat.getDataFormat();
				if (doubleFormat.getDecimalDigits() == -1) {// 精度为-1，表示按照数据对象的小数位数来显示
					int digit = -1;
					if (realValue instanceof UFDouble) {
						digit = ((UFDouble) realValue).getPower();
						digit = -digit;
						doubleFormat = (UFDoubleFormat) UFDoubleFormat.getInstance(doubleFormat.getComma(),
								doubleFormat.getPercent(), digit, doubleFormat.getCurrencySymbol(),
								doubleFormat.getChineseFormat(), doubleFormat.getMinusFormat());

						cellFormat = IufoFormat.getInstance(doubleFormat, cellFormat.getFont(), cellFormat.getAlign(),
								cellFormat.getLines());
					}
				}
			}
		}
		String fldName = fld.getBusiFieldName();
		CountField countFld = fld.getBusiCountField();
		DefaultRowData rowData = null;
		if (currData != null) {
			rowData = new DefaultRowData(currData);
			rowData.setFieldConverter(dataModel.getAreaFields(false).getFieldConverter());
		}
		return AnaReportFieldUtil.getCellFormat(dataModel.getAreaBusiFormat(), fldName, countFld, cellFormat, rowData,
				AnaDataSetTool.isInteger(type));
	}

	private Hashtable<CellPosition, IFormat> getCellFormatCache() {
		if (m_dCellFormat == null) {
			m_dCellFormat = new Hashtable<CellPosition, IFormat>();
		}
		return m_dCellFormat;
	}

	public Object[] getValueAndDataFormat(ExtendAreaCell m_exCell, CellPosition pos, IFormat format, Object value) {
		return new Object[] { value, format };
	}

}
