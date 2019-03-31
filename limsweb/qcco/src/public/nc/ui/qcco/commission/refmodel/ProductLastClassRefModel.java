package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductLastClassRefModel extends AbstractRefModel {
	private String pk_second_type;

	public ProductLastClassRefModel() {
		super();
		this.setTableName("NC_THIRD_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_THIRD_NAME Code", "NC_DESCRIPT Name" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "三级分类编码", "三级分类名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_THIRD_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_THIRD_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_THIRD_NAME";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择产品三级分类";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select TRIM(NC_THIRD_NAME) NC_THIRD_NAME, TRIM(NC_DESCRIPT) NC_DESCRIPT, PK_THIRD_TYPE from NC_THIRD_TYPE WHERE IS_ENABLE=1 AND PK_SECOND_TYPE='"
				+ this.getPk_second_type() + "'";
	}

	public String getPk_second_type() {
		return pk_second_type;
	}

	public void setPk_second_type(String pk_second_type) {
		this.pk_second_type = pk_second_type;
	}
}
