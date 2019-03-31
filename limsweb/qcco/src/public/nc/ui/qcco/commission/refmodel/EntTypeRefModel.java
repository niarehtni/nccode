package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

import org.apache.commons.lang.StringUtils;

public class EntTypeRefModel extends AbstractRefModel {
	private String pk_basprod_type;

	public EntTypeRefModel() {
		super();
		this.setTableName("NC_BASEN_TYPE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "NC_BASEN_CODE", "NC_BBASEN_NAME" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "企业标准编码", "企业标准名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_BASEN_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_BASEN_TYPE";
	}

	public java.lang.String getOrderPart() {
		return "NC_BASEN_CODE";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择企业标准";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		return "select TRIM(NC_BASEN_CODE) NC_BASEN_CODE, NC_BBASEN_NAME, PK_BASEN_TYPE from NC_BASEN_TYPE "
				+ (StringUtils.isEmpty(getPk_basprod_type()) ? "1=1"
						: (" WHERE PK_BASPROD_TYPE=(SELECT PK_BASPROD_TYPE FROM NC_SAMPLE_INFO WHERE PK_SAMPLE_INFO='"
								+ this.getPk_basprod_type() + "')"));
	}

	public String getPk_basprod_type() {
		return pk_basprod_type;
	}

	public void setPk_basprod_type(String pk_basprod_type) {
		this.pk_basprod_type = pk_basprod_type;
	}
}
