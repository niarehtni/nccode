package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductPointRefModel extends AbstractRefModel {
	public ProductPointRefModel() {
		super();
		this.setTableName("NC_BASPROD_POINT");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_BASPRODPOINT_CODE", "NC_BASPRODPOINT_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "规格号编码", "规格号名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_BASPROD_POINT" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_BASPROD_POINT";
	}

	public java.lang.String getOrderPart() {
		return "NC_BASPRODPOINT_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择规格号";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return "select TRIM(NC_BASPRODPOINT_CODE) NC_BASPRODPOINT_CODE, NC_BASPRODPOINT_NAME, PK_BASPROD_POINT from NC_BASPROD_POINT";
	}
}
