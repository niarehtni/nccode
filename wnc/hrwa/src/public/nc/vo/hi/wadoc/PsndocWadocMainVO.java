package nc.vo.hi.wadoc;

import java.util.Hashtable;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

/**
 * 此处插入类型描述。 创建日期：(2004-6-5 13:28:00)
 * 
 * @author：Administrator
 */
public class PsndocWadocMainVO extends SuperVO {
	private static final long serialVersionUID = -8569452394178203946L;
	private java.lang.String deptcode = null;
	private java.lang.String deptname = null;
	private java.lang.String postname = null;
	private java.lang.String psnclname = null;
	private java.lang.String jobname = null;
	private java.lang.String postserise = null;
	private java.lang.String psncode = null;
	private java.lang.String clerkcode = null;
	private java.lang.String psnname = null;
	private java.lang.String pk_psndoc_sub = null;

	private Hashtable<String, PsndocWadocVO> values = new Hashtable<String, PsndocWadocVO>();
	private nc.vo.hi.wadoc.PsndocWadocVO[] subVOs = null;
	private java.lang.String pk_psndoc = null;
	private java.lang.String pk_psnjob = null;
	/* 兼职定调薪 */
	private UFBoolean partflag;
	private Integer assgid;

	// 20150806 xiejie3
	// 补丁合并，NCdp205398642定调资信息维护表头能添加【任职组织】和【职级】字段，并且可以对其进行排序，begin
	// BY WANGQIM
	private String orgName;
	// end

	public static final String DEPT_CODE = "deptcode";
	public static final String DEPTNAME = "deptname";
	public static final String POSTNAME = "postname";

	public static final String PSNCLNAME = "psnclname";
	public static final String JOBNAME = "jobname";
	public static final String POSTSERISE = "postserise";

	public static final String PSN_CODE = "psncode";
	public static final String CLERKCODE = "clerkcode";
	public static final String PSN_NAME = "psnname";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_PSNJOB = "pk_psnjob";

	public static final String PK_PSNDOC_SUB = "pk_psndoc_sub";
	public static final String PK_WA_CRT_SHOWNAME = "pk_wa_crt_showname";
	public static final String PK_WA_CRT = "pk_wa_crt";
	public static final String CRITERIONVALUE = "criterionvalue";
	public static final String NMONEY = "nmoney";
	public static final String PARTFLAG = "partflag";
	public static final String ASSGID = "assgid";

	// ssx added on 2019-11-13
	// 新增表頭薪資合計
	public static final String TOTALSALARY = "totalsalary";
	private UFDouble totalsalary;

	public UFDouble getTotalsalary() {
		return totalsalary;
	}

	public void setTotalsalary(UFDouble totalsalary) {
		this.totalsalary = totalsalary;
	}

	// end

	// 20150806 xiejie3
	// 补丁合并，NCdp205398642定调资信息维护表头能添加【任职组织】和【职级】字段，并且可以对其进行排序，begin
	public static final String ORGNAME = "orgName";

	// end

	public String getClerkcode() {
		return clerkcode;
	}

	public void setClerkcode(String clerkcode) {
		this.clerkcode = clerkcode;
	}

	public java.lang.String getPk_psnjob() {
		return pk_psnjob;
	}

	public void setPk_psnjob(java.lang.String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * PsndocWadocMainVO 构造子注解。
	 */
	public PsndocWadocMainVO() {
		super();
	}

	public java.lang.String getPk_psndoc_sub() {
		return pk_psndoc_sub;
	}

	public void setPk_psndoc_sub(java.lang.String pk_psndoc_sub) {
		this.pk_psndoc_sub = pk_psndoc_sub;
	}

	/**
	 * 返回数值对象的显示名称。
	 * 
	 * 创建日期：(2001-2-15 14:18:08)
	 * 
	 * @return java.lang.String 返回数值对象的显示名称。
	 */
	@Override
	public String getEntityName() {
		return null;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-9 20:06:05)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc() {
		return pk_psndoc;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-5 14:37:31)
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocVO[]
	 */
	public nc.vo.hi.wadoc.PsndocWadocVO[] getSubVOs() {
		return subVOs;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-5 13:42:45)
	 * 
	 * @return java.util.Hashtable
	 */
	public Hashtable<String, PsndocWadocVO> getValues() {
		return values;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-9 20:06:05)
	 * 
	 * @param newPk_psndoc
	 *            java.lang.String
	 */
	public void setPk_psndoc(java.lang.String newPk_psndoc) {
		pk_psndoc = newPk_psndoc;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-5 14:37:31)
	 * 
	 * @param newSubVOs
	 *            nc.vo.hi.wadoc.PsndocWadocVO[]
	 */
	public void setSubVOs(nc.vo.hi.wadoc.PsndocWadocVO[] newSubVOs) {
		subVOs = newSubVOs;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2004-6-5 13:42:45)
	 * 
	 * @param newValues
	 *            java.util.Hashtable
	 */
	public void setValues(Hashtable<String, PsndocWadocVO> newValues) {
		values = newValues;
	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性。
	 * 
	 * 创建日期：(2001-2-15 11:47:35)
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败，抛出 ValidationException，对错误进行解释。
	 */
	@Override
	public void validate() throws nc.vo.pub.ValidationException {
	}

	public java.lang.String getDeptcode() {
		return deptcode;
	}

	public void setDeptcode(java.lang.String deptcode) {
		this.deptcode = deptcode;
	}

	public java.lang.String getDeptname() {
		return deptname;
	}

	public void setDeptname(java.lang.String deptname) {
		this.deptname = deptname;
	}

	public java.lang.String getPostname() {
		return postname;
	}

	public void setPostname(java.lang.String postname) {
		this.postname = postname;
	}

	public java.lang.String getPsncode() {
		return psncode;
	}

	public void setPsncode(java.lang.String psncode) {
		this.psncode = psncode;
	}

	public java.lang.String getPsnname() {
		return psnname;
	}

	public void setPsnname(java.lang.String psnname) {
		this.psnname = psnname;
	}

	/**
	 * 此处插入方法说明。 创建日期：(01-3-20 17:24:29)
	 * 
	 * @param key
	 *            java.lang.String
	 */
	@Override
	public Object getAttributeValue(String attributeName) {
		int index = attributeName.indexOf(".");
		if (index > 0) {
			String pkItem = attributeName.substring(0, index);
			String itemname = attributeName.substring(index + 1);
			PsndocWadocVO value = values.get(pkItem);
			if (value == null) {
				return null;
			}
			return value.getAttributeValue(itemname);
		}

		return super.getAttributeValue(attributeName);
	}

	public void setPsnclname(java.lang.String psnclname) {
		this.psnclname = psnclname;
	}

	public java.lang.String getPsnclname() {
		return psnclname;
	}

	public void setJobname(java.lang.String jobname) {
		this.jobname = jobname;
	}

	public java.lang.String getJobname() {
		return jobname;
	}

	public void setPostserise(java.lang.String postserise) {
		this.postserise = postserise;
	}

	public java.lang.String getPostserise() {
		return postserise;
	}

	public UFBoolean getPartflag() {
		return partflag;
	}

	public void setPartflag(UFBoolean partflag) {
		this.partflag = partflag;
	}

	public Integer getAssgid() {
		return assgid;
	}

	public void setAssgid(Integer assgid) {
		this.assgid = assgid;
	}

	// 20150806 xiejie3
	// 补丁合并，NCdp205398642定调资信息维护表头能添加【任职组织】和【职级】字段，并且可以对其进行排序,begin
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	// end
}
