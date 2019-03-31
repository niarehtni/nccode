package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class UnitTypeRefModel extends AbstractRefModel {
	public UnitTypeRefModel() {
		super();
		this.setTableName("NC_RESULT_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_RESULT_CODE", "NC_RESULT_NAMECN" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "值类型编码", "值类型名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_RESULT_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_RESULT_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_RESULT_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择值类型";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return "select NC_RESULT_CODE,NC_RESULT_NAMECN name, PK_RESULT_TYPE from NC_RESULT_TYPE ";
	}
}
