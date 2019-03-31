package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

import org.apache.commons.lang.StringUtils;

public class TestInitRefModel extends AbstractRefModel {
	private String enterpriseStandard;
	private String productSpec;
	private String productGrade;
	private String productStage;

	public TestInitRefModel() {
		super();
		this.setTableName("PROD_GRADE_STAGE");
		this.setMutilLangNameRef(false);
	}

	public java.lang.String[] getFieldCode() {
		return new String[] { "ORDER_NUMBER", "ANALYSIS" };
	}

	public java.lang.String[] getFieldName() {
		return new String[] { "实验前参数编码", "实验前参数名称" };
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] {};
	}

	public java.lang.String getPkFieldCode() {
		return "ANALYSIS";
	}

	public java.lang.String getOrderPart() {
		return "ORDER_NUMBER";
	}

	public int getDefaultFieldCount() {
		return 2;
	}

	public java.lang.String getRefTitle() {
		return "请选择实验前参数";
	}

	protected String getSql(String strPatch, String[] strFieldCode,
			String[] hiddenFields, String strTableName, String strWherePart,
			String strGroupField, String strOrderField) {
		String strCond = "";
		if (!StringUtils.isEmpty(this.getEnterpriseStandard())) {
			strCond += " and product = '" + this.getEnterpriseStandard() + "' ";
		}

		if (!StringUtils.isEmpty(this.getProductSpec())) {
			strCond += " and sampling_point='" + this.getProductSpec() + "' ";
		}

		if (!StringUtils.isEmpty(this.getProductGrade())) {
			strCond += " and grade='" + this.getProductGrade() + "'";
		}

		if (!StringUtils.isEmpty(this.getProductStage())) {
			strCond += " and stage='" + this.getProductStage() + "'";
		}
		return "select ORDER_NUMBER, ANALYSIS from PROD_GRADE_STAGE where 1=1 "
				+ strCond;
	}

	public String getEnterpriseStandard() {
		return enterpriseStandard;
	}

	public void setEnterpriseStandard(String enterpriseStandard) {
		this.enterpriseStandard = enterpriseStandard;
	}

	public String getProductSpec() {
		return productSpec;
	}

	public void setProductSpec(String productSpec) {
		this.productSpec = productSpec;
	}

	public String getProductGrade() {
		return productGrade;
	}

	public void setProductGrade(String productGrade) {
		this.productGrade = productGrade;
	}

	public String getProductStage() {
		return productStage;
	}

	public void setProductStage(String productStage) {
		this.productStage = productStage;
	}
}
