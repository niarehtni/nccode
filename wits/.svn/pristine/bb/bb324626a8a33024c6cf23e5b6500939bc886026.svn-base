package nc.ui.wa.taxaddtional.action;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nc.bs.uif2.validation.IValidationService;
import nc.hr.utils.ResHelper;
import nc.ui.hr.util.TableColResize;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModel;
import nc.ui.wa.taxaddtional.view.DataImportDia;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 定调资信息数据导入工具事件处理类
 *
 * @author: xuhw
 * @date: 2010-4-8 上午11:30:25
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxExcelImportEHD
{
	private final TaxaddtionalModel appmodel;

	private TaxExcelImporter importer;
	private final DataImportDia ui;
	private IValidationService validationService = null;

	private int errorNum = 0;

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	/**
	 * @param ui
	 *            关联的数据导入UI
	 */
	public TaxExcelImportEHD(DataImportDia ui)
	{
		this.ui = ui;
		this.appmodel = ui.getAppmodel();
	}

	/**
	 * 导出 事件处理
	 */
	@SuppressWarnings("restriction")
	public void doExport() throws Exception
	{
		WASpecialAdditionaDeductionVO[] exprotvos = this.appmodel.exportData2Excel(this.appmodel.getContext(),this.appmodel.getExportQuerySql(),null,this.appmodel.getTaxyear());
		if (exprotvos == null)
		{
			doexportCSVTempate();
		}
		else
		{
			int importAftLen = 0;
			this.ui.setBodyVOs(exprotvos);
			importAftLen = exprotvos.length;
			StringBuffer sbMessage = new StringBuffer();
			sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0175")/*@res "成功导出条数："*/ + importAftLen);
			getImporter().setDetailMessage(sbMessage.toString());
			ui.setResultMessage(sbMessage.toString());
			doexportCSVTempate();
			// ui.showModal();
		}
	}

	/**
	 * 从画面导出
	 *
	 * @author xuhw on 2010-5-21
	 */
	public void doexportCSVTempate() throws Exception
	{
		TaxExcelImporter importer = getImporter();
		importer.exportCSVTempate(getUI().getInputItems());
	}

	/**
	 * 从画面导出
	 *
	 * @author xuhw on 2010-5-21
	 */
	public void doExportFromPanel() throws Exception
	{
		getImporter().setImportFileName(ui.getFileName());
		getImporter().exportCSVFromPanel(getUI().getInputItems());
	}

	/**
	 * 导入数据库事件处理
	 */
	public void doImport2DB() throws Exception
	{
		setErrorNum(0);
		WASpecialAdditionaDeductionVO[] psndocwadocvos = this.ui.getBodyVOs();
		int importBefLen = 0;
		int importAftLen = 0;
		if (ArrayUtils.isEmpty(psndocwadocvos))
		{
			MessageDialog.showErrorDlg(this.ui, null, ResHelper.getString("60130adjmtc","060130adjmtc0176")/*@res "请先组织数据！"*/);
			return;
		}

		importBefLen = psndocwadocvos.length;
		Arrays.sort(psndocwadocvos);
		WASpecialAdditionaDeductionVO[] failtureReports = this.appmodel.importData2DB(psndocwadocvos, this.appmodel.getContext());
		importAftLen = failtureReports.length;
		StringBuffer sbMessage = new StringBuffer();
		sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0177",
				String.valueOf((importBefLen - importAftLen)),String.valueOf(importAftLen))/*@res "成功导入条数：{0}，导入失败条数：{1}。"*/
				);
		sbMessage.append("\n");
		if (importAftLen > 0)
		{
			sbMessage.append(ResHelper.getString("60130adjmtc","060130adjmtc0180")/*@res "失败原因请参考最后的【失败原因】一列。\n 错误数据已经导出，请修改后重试！"*/);
		}
		setErrorNum(importAftLen);
		Arrays.sort(failtureReports);
		this.ui.setBodyVOs(failtureReports);
		TableColResize.reSizeTable(this.ui.getBillCardPanel());
		// MessageDialog.showHintDlg(this.ui, null, sbMessage.toString());
		getImporter().setDetailMessage(sbMessage.toString());
		getUI().setResultMessage(sbMessage.toString());
 
	}

	/**
	 * 导入系统事件处理
	 */
	public void doImport2Panel() throws Exception
	{
		getImporter().importFromCls(getUI().getInputItems());
	}

	public TaxExcelImporter getImporter()
	{
		if (importer == null)
		{
			importer = new TaxExcelImporter(getUI());
		}
		return importer;
	}

	/**
	 * @return 关联的数据导入UI
	 */
	private DataImportDia getUI()
	{
		return ui;
	}

	public IValidationService getValidationService()
	{
		return validationService;
	}

	public void setValidationService(IValidationService validationService)
	{
		this.validationService = validationService;
	}
}