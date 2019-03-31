package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductSecondClassRefModel extends AbstractRefModel {
	private String pk_first_type;

	public ProductSecondClassRefModel() {
		super();
		this.setTableName("NC_SECOND_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_SECOND_NAME Code", "NC_DESCRIPT Name" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "二级分类编码", "二级分类名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_SECOND_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_SECOND_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_SECOND_NAME";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择二级产品分类";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select TRIM(NC_SECOND_NAME) NC_SECOND_NAME, TRIM(NC_DESCRIPT) NC_DESCRIPT, PK_SECOND_TYPE from NC_SECOND_TYPE where IS_ENABLE=1 AND PK_FIRST_TYPE='"
				+ this.getPk_first_type() + "'";
	}

	public String getPk_first_type() {
		return pk_first_type;
	}

	public void setPk_first_type(String pk_first_type) {
		this.pk_first_type = pk_first_type;
	}
}
