package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductSerialRefModel extends AbstractRefModel {
	public ProductSerialRefModel() {
		super();
		this.setTableName("NC_BASPROD_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_BASPRODTYPE_CODE", "NC_BASPRODTYPE_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "产品系列编码", "产品系列名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_BASPROD_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_BASPROD_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_BASPRODTYPE_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择产品系列";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return "select TRIM(NC_BASPRODTYPE_CODE) NC_BASPRODTYPE_CODE, NC_BASPRODTYPE_NAME, PK_BASPROD_TYPE from NC_BASPROD_TYPE";
	}
}
