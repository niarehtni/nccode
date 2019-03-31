package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductFirstClassRefModel extends AbstractRefModel {
	public ProductFirstClassRefModel() {
		super();
		this.setTableName("NC_FIRST_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "PROD_TYPE Code", "NC_FIRST_NAME Name" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "产品大类编码", "产品大类名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_FIRST_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_FIRST_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "PROD_TYPE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择产品大类";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select PROD_TYPE, TRIM(NC_FIRST_NAME) NC_FIRST_NAME, PK_FIRST_TYPE from NC_FIRST_TYPE WHERE IS_ENABLE=1";
	}
}
