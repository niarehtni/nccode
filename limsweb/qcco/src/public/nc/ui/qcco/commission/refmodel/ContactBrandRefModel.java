package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class ContactBrandRefModel extends AbstractRefModel {
	public ContactBrandRefModel() {
		super();
		this.setTableName("NC_CONTACT_BRAND");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_CONTACT_CODE", "NC_CONTACT_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "¥•µ„≈∆∫≈±‡¬Î", "¥•µ„≈∆∫≈√˚≥∆" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_CONTACT_BRAND" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_CONTACT_BRAND";
	}

	public java.lang.String getOrderPart() {
		return "NC_CONTACT_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "«Î—°‘Ò¥•µ„≈∆∫≈";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		return "select NC_CONTACT_CODE,NC_CONTACT_NAME, PK_CONTACT_BRAND from NC_CONTACT_BRAND WHERE ISENABLE=1";
	}
}
