package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class AnalyseComponentRefModel extends AbstractRefModel {
	public AnalyseComponentRefModel() {
		super();
		this.setTableName("NC_TEST_INIT");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "TEST_INIT_CODE", "TEST_INIT_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "参数项编码", "参数项名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_TEST_INIT" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_TEST_INIT";
	}

	public java.lang.String getOrderPart() {
		return "TEST_INIT_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择参数项";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select TEST_INIT_CODE, TEST_INIT_NAME, PK_TEST_INIT from NC_TEST_INIT";
	}
}
