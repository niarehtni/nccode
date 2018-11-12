package com.ufida.report.free.plugin.formula;

import com.ufida.iufo.table.areafomula.AreaFuncDriver;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.report.anareport.model.AnaReportModel;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.script.AreaFmlExecutor;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.table.CellsModel;

public class FormulaUtil {

	/**
	 * 公式驱动注册
	 * @param fmtModel
	 */
	public static void regFormula(CellsModel fmtModel) {
		AreaFormulaModel areaFormulaModel = AreaFormulaModel.getInstance(fmtModel);
		AreaFmlExecutor fmtExecutor = areaFormulaModel.getAreaFmlExecutor();
		if (areaFormulaModel.getAreaFmlExecutor() == null) {
			fmtExecutor = new AreaFmlExecutor(AnaReportModel.getInstance(fmtModel).getContext(), fmtModel);
			areaFormulaModel.setAreaFmlExecutor(fmtExecutor);
		}
		AreaFuncDriver driver = (AreaFuncDriver) fmtExecutor.getFuncListInst().getExtDriver(
				AreaFuncDriver.class.getName());
		if (driver == null) {
			driver = new AreaFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(driver);
		}
		CVSBatchFuncDriver driver2 = (CVSBatchFuncDriver) fmtExecutor.getFuncListInst().getExtDriver(
				CVSBatchFuncDriver.class.getName());
		if (driver2 == null) {
			driver2 = new CVSBatchFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(driver2);
		}
		// add by yuyangi 增加参数公式注册
		VarFuncDriver varFuncDriver = (VarFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				VarFuncDriver.class.getName());
		if(varFuncDriver == null) {
			varFuncDriver = new VarFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(varFuncDriver);
		}
		// 按字段取值函数
		GetFuncDriver getDriver = (GetFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				GetFuncDriver.class.getName());
		if(getDriver == null) {
			getDriver = new GetFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(getDriver);
		}
		// nc数据格式公式驱动
		NcDataFormatFuncDriver ncDataFormatFuncDriver = (NcDataFormatFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				NcDataFormatFuncDriver.class.getName());
		if(ncDataFormatFuncDriver == null) {
			ncDataFormatFuncDriver = new NcDataFormatFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(ncDataFormatFuncDriver);
		}
		
		//添加薪资解密函数  add by ward 2018-06-05
		SalaryDecryptFuncDriver salaryDecryptFuncDriver = (SalaryDecryptFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				SalaryDecryptFuncDriver.class.getName());
		if(salaryDecryptFuncDriver == null) {
			salaryDecryptFuncDriver = new SalaryDecryptFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(salaryDecryptFuncDriver);
		}
		
		GetAreaFieldFuncDriver getAreaFunc = (GetAreaFieldFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				GetAreaFieldFuncDriver.class.getName());
		if(getAreaFunc == null) {
			getAreaFunc = new GetAreaFieldFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(getAreaFunc);
		}
		
		ExcelStatCalcUtil.registExcelFuncDriver(fmtExecutor);
	}
}
