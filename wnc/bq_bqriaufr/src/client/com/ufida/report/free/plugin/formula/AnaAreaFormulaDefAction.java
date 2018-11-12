package com.ufida.report.free.plugin.formula;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import nc.itf.iufo.constants.ModuleConstants;
import nc.pub.smart.model.SmartModel;

import com.ufida.iufo.pub.tools.DeepCopyUtilities;
import com.ufida.iufo.table.areafomula.AreaFuncDriver;
import com.ufida.iufo.table.areafomula.CVSBatchFuncDriver;
import com.ufida.iufo.table.areafomula.GetFuncDriver;
import com.ufida.iufo.table.areafomula.NcDataFormatFuncDriver;
import com.ufida.iufo.table.areafomula.SalaryDecryptFuncDriver;
import com.ufida.iufo.table.areafomula.cal.GetAreaFieldFuncDriver;
import com.ufida.iufo.table.exarea.ExtendAreaCell;
import com.ufida.iufo.table.exarea.ExtendAreaModel;
import com.ufida.iufo.table.model.ReportCondition;
import com.ufida.report.anadesigner.AbsAnaReportDesigner;
import com.ufida.report.anareport.FreePrivateContextKey;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.anareport.model.AnaReportCondition;
import com.ufida.report.anareport.model.AnaReportModel;
import com.ufida.report.free.action.AbstractFreeRepPluginAction;
import com.ufida.report.free.plugin.param.VarFuncDriver;
import com.ufida.report.frquery.model.FrQueryModel;
import com.ufida.zior.plugin.IPluginActionDescriptor;
import com.ufida.zior.plugin.PluginActionDescriptor;
import com.ufida.zior.plugin.PluginKeys.XPOINT;
import com.ufida.zior.view.Mainboard;
import com.ufsoft.iufo.func.excel.stat.ExcelStatCalcUtil;
import com.ufsoft.script.AreaFmlExecutor;
import com.ufsoft.script.base.AreaFormulaModel;
import com.ufsoft.table.CellsModel;
import com.ufsoft.table.CellsPane;
import com.ufsoft.table.IArea;

/**
 * 区域公式定义
 *
 * @author wanyonga
 * @created at 2010-7-7,下午12:07:08
 *
 */
public class AnaAreaFormulaDefAction extends AbstractFreeRepPluginAction {

	public static String PATH = ModuleConstants.MODULE_PATH+"bi/formula/ufob_bi_formula.xml";
	
	private void init(CellsPane cellsPane) {
		CellsModel fmtModel = null;
		if (cellsPane == null) {
			fmtModel = getAnaModel(cellsPane).getFormatModel();
		} else {
			fmtModel = cellsPane.getDataModel();
		}
		AreaFormulaModel areaFormulaModel = AreaFormulaModel.getInstance(fmtModel);
		AreaFmlExecutor fmtExecutor = areaFormulaModel.getAreaFmlExecutor();
		if (areaFormulaModel.getAreaFmlExecutor() == null) {
			fmtExecutor = new AreaFmlExecutor(getAnaModel(cellsPane).getContext(), fmtModel);
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
		SalaryDecryptFuncDriver  salaryDecryptFuncDriver = (SalaryDecryptFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				SalaryDecryptFuncDriver.class.getName());
		if(salaryDecryptFuncDriver == null) {
			salaryDecryptFuncDriver = new SalaryDecryptFuncDriver(AnaReportModel.getInstance(fmtModel));
			fmtExecutor.registerFuncDriver(salaryDecryptFuncDriver);
		}
		
		GetAreaFieldFuncDriver getAreaFunc = (GetAreaFieldFuncDriver)fmtExecutor.getFuncListInst().getExtDriver(
				GetAreaFieldFuncDriver.class.getName());
		if(getAreaFunc == null) {
			getAreaFunc = new GetAreaFieldFuncDriver(getAnaModel(cellsPane));
			fmtExecutor.registerFuncDriver(getAreaFunc);
		}
		
		ExcelStatCalcUtil.registExcelFuncDriver(fmtExecutor);
	}

	protected void execute(CellsPane cellsPane) {
//		init(cellsPane);
//		new AreaFormulaActionHandler(cellsPane).execute(null);
		doexecute(cellsPane);
		
		// @edit by yanchm at 2012-11-2,上午11:24:57
		//关闭函数定义向导后，去掉context中的语义模型和页维度
		getAnaModel(cellsPane).getContext().removeAttribute(FreePrivateContextKey.KEY_EXCELL_SMART_MODEL);
		getAnaModel(cellsPane).getContext().removeAttribute(FreePrivateContextKey.REPORT_PAGEDIM);
	
	}

	@Override
	public void execute(ActionEvent e) {
//		init(null);
//		new AreaFormulaActionHandler(getCellsPane()).execute(null);
		CellsPane cellsPane = getCellsPane();
		doexecute(cellsPane);
		
		//关闭函数定义向导后，去掉context中的临时属性
		removeAttFromContext(cellsPane);
	}
	
	/**
	 * 获取当前报表中的所有页维度
	 * @return
	 */
	private AnaReportCondition[] getAllPageDim(CellsPane cellsPane) {
		AnaReportCondition[] pageDims = null;
		AbsAnaReportModel anaModel = getAnaModel(cellsPane);
		ExtendAreaModel exModel = ExtendAreaModel.getInstance(anaModel
				.getFormatModel());

		pageDims = ReportCondition.getInstance(exModel).getAllCondition();
		pageDims = (AnaReportCondition[]) DeepCopyUtilities
				.getDeepCopy(pageDims);
		return pageDims;
	}

	public void doexecute(CellsPane cellsPane)
	{
		if(cellsPane == null) {
			return;
		}
		

		AreaFormulaModel areaFormulaModel = AreaFormulaModel
				.getInstance(cellsPane.getDataModel());
		if (areaFormulaModel.getAreaFmlExecutor() == null)
		{

			// 初始化公式执行器
			AreaFormulaModel formulaModel = AreaFormulaModel
					.getInstance(cellsPane.getDataModel());
			AreaFmlExecutor fmlExe = new AreaFmlExecutor(getAnaModel(cellsPane).getContext(), cellsPane
					.getDataModel());
			formulaModel.setAreaFmlExecutor(fmlExe);
			//注册函数驱动
			FormulaUtil.regFormula(cellsPane.getDataModel());
//			formulaModel.setAreaFmlExecutor(new AreaFmlExecutor(cellsPane
//					.getDataModel()));
		}
//		 获得选定单元定义的单元公式公式内容
//		IArea anchorCell = cellsPane.getDataModel().getSelectModel()
//				.getSelectedArea();
		// 获得选定单元定义的单元公式公式内容
		IArea anchorCell = cellsPane.getDataModel().getSelectModel()
				.getRealAnchorCell();
		if (anchorCell == null)
			return;
//		AreaFmlEditDlg fmlEditDlg = new AreaFmlEditDlg(getCellsPane(),
//				areaFormulaModel.getAreaFmlExecutor());
		AnaFormulaDefDlg anaFmlDefDlg = new AnaFormulaDefDlg(getMainboard(cellsPane), areaFormulaModel.getAreaFmlExecutor(),
				anchorCell, getFmlConfigUrl(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0365")/*@res "单元公式"*/);
		//在打开公式编辑器之前，将属性加入到context中，供processor中使用
		addAttToContext(cellsPane, anchorCell);
		
//		anaFmlDefDlg.showNoModal();
		anaFmlDefDlg.showModal();
//		fmlEditDlg.setArea(new IArea[] { anchorCell });
//		fmlEditDlg.loadFormula(anchorCell);
//		fmlEditDlg.show();
	}
	
	/**
	 * 在打开公式编辑器之前，将属性加入到context中，供processor中使用
	 * 
	 * @param cellsPane
	 * @param anchorCell
	 */
	private void addAttToContext(CellsPane cellsPane, IArea anchorCell){
		
		// 加入语义模型
		ExtendAreaCell excell = ExtendAreaModel.getInstance(
				getAnaModel(cellsPane).getFormatModel()).getExArea(anchorCell);
		if (excell != null) {
			SmartModel smartModel = excell.getAreaInfoSet().getSmartModel();
			getAnaModel(cellsPane).getContext().setAttribute(
					FreePrivateContextKey.KEY_EXCELL_SMART_MODEL, smartModel);
		} else {
			getAnaModel(cellsPane).getContext().removeAttribute(
					FreePrivateContextKey.KEY_EXCELL_SMART_MODEL);
		}

		// @edit by yanchm at 2012-11-2,上午10:47:54
		// 加入页维度项
		if (getAllPageDim(cellsPane) != null) {
			getAnaModel(cellsPane).getContext().setAttribute(
					FreePrivateContextKey.REPORT_PAGEDIM,
					getAllPageDim(cellsPane));
		}
		
		// 加入查询模型
		FrQueryModel frQueryModel = FrQueryModel.getInstance(getAnaModel(
				cellsPane).getFormatModel());
		if (frQueryModel != null) {
			getAnaModel(cellsPane).getContext().setAttribute(
					FreePrivateContextKey.REPORT_FRQUERYMODEL, frQueryModel);
		}
	}
	
	/**
	 * 关闭函数定义向导后，去掉context中的临时属性
	 * 
	 * @param cellsPane
	 */
	private void removeAttFromContext(CellsPane cellsPane){
		// 去掉语义模型
		getAnaModel(cellsPane).getContext().removeAttribute(FreePrivateContextKey.KEY_EXCELL_SMART_MODEL);
		
		// @edit by yanchm at 2012-11-2,上午11:24:57
		// 去掉页维度
		getAnaModel(cellsPane).getContext().removeAttribute(FreePrivateContextKey.REPORT_PAGEDIM);
	
		// 去掉查询模型
		getAnaModel(cellsPane).getContext().removeAttribute(FreePrivateContextKey.REPORT_FRQUERYMODEL);
	}
	
	private Mainboard getMainboard(Component comp) {
		if(comp.getParent() instanceof Mainboard) {
			return (Mainboard)comp.getParent();
		} else {
			return getMainboard(comp.getParent());
		}
	}
	
	public AbsAnaReportModel getAnaModel(CellsPane cellsPane) {
		if(getPlugin() != null) {
			return getAnaModel();
		}
		Mainboard mainboard = getMainboard(cellsPane);
		
		AbsAnaReportDesigner designer = null;
		if (mainboard.getCurrentView() instanceof AbsAnaReportDesigner){
			designer = (AbsAnaReportDesigner) mainboard.getCurrentView();
			return designer.getAnaReportModel();
		}
		return null;
	}
	
	protected String getFmlConfigUrl() {
		return ModuleConstants.MODULE_PATH+"bi/formula/ufob_bi_formula.xml";
	}
	
	@Override
	public IPluginActionDescriptor getPluginActionDescriptor() {
		PluginActionDescriptor descriptor = new PluginActionDescriptor();
		descriptor.setGroupPaths(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413003_0","01413003-0050")/*@res "格式"*/, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413003_0","01413003-0343")/*@res "单元公式"*/);
		descriptor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0));
		descriptor.setName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("1413006_0","01413006-0365")/*@res "单元公式"*/);// "单元公式"
		descriptor.setExtensionPoints(new XPOINT[] { XPOINT.MENU, XPOINT.POPUPMENU });
		return descriptor;
	}

	@Override
	public boolean isEnabled() {
		if(getAnaDesigner() == null)
			return false;
		if(!isNoStaticInstance()){
			return false;
		}
//		ExtendAreaCell exCell = getFormatAnchorExCell();
		// @edit by ll at 2011-5-3,上午10:07:24 放开必须在扩展区定义公式的限制，普通单元的公式执行继续完善
//		if (exCell != null && exCell.getAreaInfoSet().isCrossSet()) {
//			return false;
//		}
		return true;
	}

}

