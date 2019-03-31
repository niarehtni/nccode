package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductAuthTypeRefModel extends AbstractRefModel {
	public ProductAuthTypeRefModel() {
		super();
		this.setTableName("NC_PRODAUTH_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_PRODAUTH_CODE", "NC_PRODAUTH_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "产品鉴定类型编码", "产品鉴定类型名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_PRODAUTH_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_PRODAUTH_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_PRODAUTH_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择产品鉴定类型";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select NC_PRODAUTH_CODE, NC_PRODAUTH_NAME, PK_PRODAUTH_TYPE from NC_PRODAUTH_TYPE WHERE ISENABLE=1";
	}
}
