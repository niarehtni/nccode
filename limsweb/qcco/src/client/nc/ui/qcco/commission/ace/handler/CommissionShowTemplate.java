package nc.ui.qcco.commission.ace.handler;

public class CommissionShowTemplate {
	public final static String QUALITY_COMPLAINT_NAME = "质量投诉";

	public final static String ROUTINE_TESTING_NAME = "常规检测";

	public final static String CUSTOMER_REQUIREMENTS_NAME = "客户要求";

	public final static String PERIODIC_TEST_NAME = "周期检验";

	public final static String PRODUCT_DEVELOPMENT_NAME = "产品开发";

	public final static String SAFETY_CERTIFICATION_NAME = "安全认证";

	public final static String PROCESS_TESTING_NAME = "流程测试";

	/**
	 * 以下是某个模板需要显示的字段集合
	 */

	// 质量投诉
	static final String[] QUALITY_COMPLAINT_VALUES = { "testaim", "progressneed" };

	// 常规检测
	static final String[] ROUTINE_TESTING_VALUES = { "testaim", "progressneed", "productproperty" };

	// 客户要求
	static final String[] CUSTOMER_REQUIREMENTS_VALUES = { "progressneed", "customername", "customertype",
			"testrequirement" };

	// 周期检验
	static final String[] PERIODIC_TEST_VALUES = { "progressneed", "checkingproperty", "productline", "batchnumber",
			"productdate", "batchserial" };

	// 产品开发
	static final String[] PRODUCT_DEVELOPMENT_VALUES = { "progressneed", "identificationtype" };

	// 安全认证
	static final String[] SAFETY_CERTIFICATION_VALUES = { "progressneed", "certificationtype" };

	// 流程测试
	static final String[] PROCESS_TESTING_VALUES = { "testaim", "progressneed", "itemnumber" };

	static final String[] ALL_TEMPLATE_FIELD_VALUES = { "testaim", "progressneed", "productproperty", "customername",
			"customertype", "testrequirement", "checkingproperty", "productline", "batchnumber", "productdate",
			"batchserial", "identificationtype", "certificationtype", "itemnumber" };

	/**
	 * 传入模板名:质量投诉,常规检测,客户要求...获取模板
	 */
	public static String[] getTemplateByName(String tempLateName) {
		switch (tempLateName) {
		case QUALITY_COMPLAINT_NAME:
			return QUALITY_COMPLAINT_VALUES;

		case ROUTINE_TESTING_NAME:
			return ROUTINE_TESTING_VALUES;

		case CUSTOMER_REQUIREMENTS_NAME:
			return CUSTOMER_REQUIREMENTS_VALUES;

		case PERIODIC_TEST_NAME:
			return PERIODIC_TEST_VALUES;

		case PRODUCT_DEVELOPMENT_NAME:
			return PRODUCT_DEVELOPMENT_VALUES;

		case SAFETY_CERTIFICATION_NAME:
			return SAFETY_CERTIFICATION_VALUES;

		case PROCESS_TESTING_NAME:
			return PROCESS_TESTING_VALUES;
		default:
			return null;
		}

	}

	public static String[] getTemplateWithAllField() {
		return ALL_TEMPLATE_FIELD_VALUES;

	}

}
