package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ReportLanguageRefModel extends AbstractRefModel {
	public ReportLanguageRefModel() {
		super();
		this.setTableName("NC_REPORT_LANG");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "RP_REPORT_CODE", "RP_REPORT_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "报告语言编码", "报告语言名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_REPORT_LANG" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_REPORT_LANG";
	}

	public java.lang.String getOrderPart() {
		return "RP_REPORT_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择报告语言";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select RP_REPORT_CODE, RP_REPORT_NAME, PK_REPORT_LANG from NC_REPORT_LANG WHERE ISENABLE=1";
	}
}
