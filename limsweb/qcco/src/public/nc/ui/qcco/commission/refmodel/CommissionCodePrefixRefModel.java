package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class CommissionCodePrefixRefModel extends AbstractRefModel {
	public CommissionCodePrefixRefModel() {
		super();
		this.setTableName("NC_PROJ_PREFIX");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_SAFE_CODE", "NC_SAFE_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "前缀编码", "前缀名称" };
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
		return "请选择委托单编码前缀";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select NC_SAFE_CODE, NC_SAFE_NAME, PK_SAFE_TYPE from NC_PROJ_PREFIX WHERE ISENABLE=1";
	}
}
