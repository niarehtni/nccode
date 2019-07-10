package nc.ui.twhr.twhr_declaration.action;

import nc.hr.utils.ResHelper;

/**
 * 
 * @author wangywt
 * @since 20190620
 */
public class DataDecConst {
	public static final int PREMIUM_NOTPARTJOB_PERSON = 0;//非兼职人员补充保费
	public static final int PREMIUM_PARTJOB_PERSON = 1;//兼职人员补充保费
	public static final int PREMIUM_BUSINESS_INCOME = 2;//执行业务所得补充保费
	public static final int PREMIUM_COMPANY_ADD = 3;//公司补充保费
	public static final String SUFFIX_XLS = ".xls";
	public static final String SUFFIX_XLSX = ".xlsx";
	public static final String DESC_NOTPARTJOB_PERSON = ResHelper.getString("6013dataitf_01", "dataitf-01-0049");//非兼职人员补充保费
	public static final String DESC_PARTJOB_PERSON = ResHelper.getString("6013dataitf_01", "dataitf-01-0050");//兼职人员补充保费
	public static final String DESC_BUSINESS_INCOME = ResHelper.getString("6013dataitf_01", "dataitf-01-0051");//执行业务所得补充保费
	public static final String DESC_COMPANY_ADD = ResHelper.getString("6013dataitf_01", "dataitf-01-0052");//公司补充保费
}
