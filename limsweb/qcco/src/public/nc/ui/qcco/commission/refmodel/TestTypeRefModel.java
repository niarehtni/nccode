package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class TestTypeRefModel extends AbstractRefModel {
	public TestTypeRefModel() {
		super();
		this.setTableName("NC_TEST_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_RATAIN_CODE", "NC_RATAIN_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "检测性质编码", "检测性质名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_RATAIN_HANDLE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_RATAIN_HANDLE";
	}

	public java.lang.String getOrderPart() {
		return "NC_RATAIN_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择检测性质";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select NC_RATAIN_CODE, NC_RATAIN_NAME, PK_RATAIN_HANDLE from NC_TEST_TYPE WHERE ISENABLE=1";
	}
}
