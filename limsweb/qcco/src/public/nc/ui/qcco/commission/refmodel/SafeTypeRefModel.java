package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class SafeTypeRefModel extends AbstractRefModel {
	public SafeTypeRefModel() {
		super();
		this.setTableName("NC_SAFE_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_SAFE_CODE", "NC_SAFE_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "认证类型编码", "认证类型名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_SAFE_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_SAFE_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_SAFE_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择认证类型";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select NC_SAFE_CODE, NC_SAFE_NAME, PK_SAFE_TYPE from NC_SAFE_TYPE WHERE ISENABLE=1";
	}
}
