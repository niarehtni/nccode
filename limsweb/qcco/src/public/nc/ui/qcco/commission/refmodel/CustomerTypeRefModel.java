package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class CustomerTypeRefModel extends AbstractRefModel {
	public CustomerTypeRefModel() {
		super();
		this.setTableName("NC_CUSTOMER_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_CUSTOMER_CODE", "NC_CUSTOMER_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "客户类型编码", "客户类型名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_CUSTOMER_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_CUSTOMER_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_CUSTOMER_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择客户类型";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select NC_CUSTOMER_CODE, NC_CUSTOMER_NAME, PK_CUSTOMER_TYPE from NC_CUSTOMER_TYPE WHERE ISENABLE=1";
	}
}
