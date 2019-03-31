package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class CommissionTypeRefModel extends AbstractRefModel {
	public CommissionTypeRefModel() {
		super();
		this.setTableName("NC_PROJ_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "PROJ_TYPE_CODE", "NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "委托单类型编码", "委托单类型名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_PROJ_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_PROJ_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "PROJ_TYPE_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择委托单类型";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select PROJ_TYPE_CODE, NAME, PK_PROJ_TYPE from NC_PROJ_TYPE WHERE ISENABLE=1";
	}
}
