package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

import org.apache.commons.lang.StringUtils;

public class SampleInfoRefModel extends AbstractRefModel {
	private String firstClassCode;
	private String secondClassCode;
	private String thirdClassCode;

	public SampleInfoRefModel() {
		super();
		this.setTableName("NC_SAMPLE_INFO");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "SAMPLE_INFO_CODE CODE", "NAME", "ENSTARD", "SAMPLING_POINT", "GRADE",
				"C_ALLOWED_CONTACT", "STAGE" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "产品系列编码", "产品系列名称", "企业标准", "规格号", "结构类型", "触点类型", "温度" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "PK_SAMPLE_INFO", "PK_PROD_TYPE", "PK_BASPROD_NAME", "PK_BASPROD_TYPE",
				"PK_BASPROD_POINT", "PK_BASPROD_STRUCT", "PK_BASPROD_CONTACT", "PK_BASPROD_TEMP", "PK_BASEN_TYPE" };
	}

	public java.lang.String getPkFieldCode() {
		return "PK_SAMPLE_INFO";
	}

	public java.lang.String getOrderPart() {
		return "SAMPLE_INFO_CODE";
	}

	public int getDefaultFieldCount() {
		return 7;
	}

	public java.lang.String getRefTitle() {
		return "请选择产品系列";
	}

	protected String getSql(String strPatch, String[] strFieldCode, String[] hiddenFields, String strTableName,
			String strWherePart, String strGroupField, String strOrderField) {
		String strProdType = "1=1 "
				+ (StringUtils.isEmpty(this.getFirstClassCode()) ? "" : " and PROD_TYPE='" + this.getFirstClassCode()
						+ "' ")
				+ (StringUtils.isEmpty(this.getSecondClassCode()) ? "" : " and C_PROD_TYPE_C1='"
						+ this.getSecondClassCode() + "' ")
				+ (StringUtils.isEmpty(this.getThirdClassCode()) ? "" : " and P_NAME='" + this.getThirdClassCode()
						+ "' ");
		return "select SAMPLE_INFO_CODE, NAME, ENSTARD, SAMPLING_POINT, GRADE, C_ALLOWED_CONTACT, STAGE, PK_SAMPLE_INFO, "
				+ " PK_PROD_TYPE, PK_BASPROD_NAME, PK_BASPROD_TYPE,PK_BASPROD_POINT, PK_BASPROD_STRUCT, PK_BASPROD_CONTACT, PK_BASPROD_TEMP, PK_BASEN_TYPE "
				+ "from NC_SAMPLE_INFO WHERE ISENABLE=1 AND PK_PROD_TYPE IN (SELECT PK_PROD_TYPE FROM NC_PROD_TYPE WHERE "
				+ strProdType + ")";
	}

	public String getFirstClassCode() {
		return firstClassCode;
	}

	public void setFirstClassCode(String firstClassCode) {
		this.firstClassCode = firstClassCode;
	}

	public String getSecondClassCode() {
		return secondClassCode;
	}

	public void setSecondClassCode(String secondClassCode) {
		this.secondClassCode = secondClassCode;
	}

	public String getThirdClassCode() {
		return thirdClassCode;
	}

	public void setThirdClassCode(String thirdClassCode) {
		this.thirdClassCode = thirdClassCode;
	}
}
