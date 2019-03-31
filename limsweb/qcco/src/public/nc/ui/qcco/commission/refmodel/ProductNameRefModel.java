package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductNameRefModel extends AbstractRefModel {
	public ProductNameRefModel() {
		super();
		this.setTableName("NC_BASPROD_NAME");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_BASPROD_CODE", "NC_BASPROD_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "基础产品编码", "基础产品名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_BASPROD_NAME" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_BASPROD_NAME";
	}

	public java.lang.String getOrderPart() {
		return "NC_BASPROD_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择基础产品";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return "select TRIM(NC_BASPROD_CODE) NC_BASPROD_CODE, NC_BASPROD_NAME, PK_BASPROD_NAME from NC_BASPROD_NAME";
	}
}
