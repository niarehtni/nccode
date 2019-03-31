package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductPropertyRefModel extends AbstractRefModel {
	public ProductPropertyRefModel() {
		super();
		this.setTableName("NC_STATUS_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_STATUS_CODE", "NC_STATUS_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "产品属性编码", "产品属性名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_STATUS_HANDLE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_STATUS_HANDLE";
	}

	public java.lang.String getOrderPart() {
		return "NC_STATUS_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择产品属性";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return "select NC_STATUS_CODE, NC_STATUS_NAME, PK_STATUS_HANDLE from NC_STATUS_TYPE WHERE ISENABLE=1";
	}
}
