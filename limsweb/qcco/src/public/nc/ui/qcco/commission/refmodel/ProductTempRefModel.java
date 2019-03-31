package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ProductTempRefModel extends AbstractRefModel {
	public ProductTempRefModel() {
		super();
		this.setTableName("NC_BASPROD_TEMP");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_BASPRODTEMP_CODE", "NC_BASPRODTEMP_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "温度编码", "温度名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_BASPROD_TEMP" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_BASPROD_TEMP";
	}

	public java.lang.String getOrderPart() {
		return "NC_BASPRODTEMP_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择温度";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return "select TRIM(NC_BASPRODTEMP_CODE) NC_BASPRODTEMP_CODE, NC_BASPRODTEMP_NAME, PK_BASPROD_TEMP from NC_BASPROD_TEMP";
	}
}
