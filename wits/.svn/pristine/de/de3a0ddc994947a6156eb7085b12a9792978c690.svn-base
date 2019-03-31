package nc.vo.wa.taxspecial_statistics;

import nc.vo.wa.payfile.PayfileVO;

/**
 * 
 * @author xuhw
 */
public class TaxSpecialStatisticsVO extends PayfileVO {
	public static final String TABLE_NAME = "wa_spe_statis";
	private String pk_spe_statis;
	private String taxyear;
	private String taxperiod;
	private int tax_type;//0，未生成 1、已生成 2，已审核
	private int lockflag;//是否锁定 
	private String lockclass;//锁定方案
	private String lockclassname;//锁定方案名称
	
	@Override
	public java.lang.String getPKFieldName() {
		return "pk_spe_statis";
	}
	public static String getDefaultTableName()
	{
		return TABLE_NAME;
	}
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2009-11-26 11:30:31
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {
		return "wa_spe_statis";
	}
	public int getTax_type() {
		return tax_type;
	}
	public void setTax_type(int tax_type) {
		this.tax_type = tax_type;
	}
	public String getLockclass() {
		return lockclass;
	}
	public void setLockclass(String lockclass) {
		this.lockclass = lockclass;
	}
	public String getLockclassname() {
		return lockclassname;
	}
	public void setLockclassname(String lockclassname) {
		this.lockclassname = lockclassname;
	}
	public int getLockflag() {
		return lockflag;
	}
	public void setLockflag(int lockflag) {
		this.lockflag = lockflag;
	}
	public String getPk_spe_statis() {
		return pk_spe_statis;
	}
	public void setPk_spe_statis(String pk_spe_statis) {
		this.pk_spe_statis = pk_spe_statis;
	}
	// 赡养老人--
	// 赡养老人应扣
	private Integer parentfeetotal;
	// 赡养老人已扣
	private Integer paretnfeedtotal;
	// 赡养老人本期
	private Integer parentfee;
	// 子女教育--
	// 子女教育应扣
	private Integer childfeetotal;
	// 子女教育已扣
	private Integer childfeedtotal;
	// 子女教育本期
	private Integer childfee;
	// 房屋津贴--
	// 房屋津贴应扣
	private Integer hoursefeetotal;
	// 房屋津贴已扣
	private Integer hoursefeedtotal;
	// 房屋津贴本期
	private Integer hoursefee;
	
	// 房屋津贴-租房
	// 房屋津贴-租房应扣
	private Integer hoursezufeetotal;
	// 房屋津贴-租房已扣
	private Integer hoursezufeedtotal;
	// 房屋津贴-租房本期
	private Integer hoursezufee;
	
	// Healthy医疗
	// 大病医疗应扣
	private Integer healthyfeetotal;
	// 大病医疗已扣
	private Integer healthyfeedtotal;
	// 大病医疗本期
	private Integer healthyfee;
	
	// 继续教育
	// 继续教育应扣
	private Integer educationfeetotal;
	// 继续教育已扣
	private Integer educationfeedtotal;
	// 继续教育本期
	private Integer educationfee;
	
	// 合计
	// 合计应扣
	private Integer allfeetotal;
	// 合计已扣
	private Integer allfeedtotal;
	// 合计本期
	private Integer allfee;
	
	public static String PARENTFEETOTAL = "parentfeetotal";
	public static String PARETNFEEDTOTAL = "paretnfeedtotal";
	public static String PARENTFEE = "parentfee";
	
	public static String CHILDFEETOTAL = "childfeetotal";
	public static String CHILDFEEDTOTAL = "childfeedtotal";
	public static String CHILDFEE = "childfee";
	
	public static String HOURSEFEETOTAL = "hoursefeetotal";
	public static String HOURSEFEEDTOTAL = "hoursefeedtotal";
	public static String HOURSEFEE = "hoursefee";
	
	public static String HOURSEZUFEETOTAL = "hoursezufeetotal";
	public static String HOURSEZUFEEDTOTAL = "hoursezufeedtotal";
	public static String HOURSEZUFEE = "hoursezufee";
	
	public static String HEALTHYFEETOTAL = "healthyfeetotal";
	public static String HEALTHYFEEDTOTAL = "healthyfeedtotal";
	public static String HEALTHYFEE = "healthyfee";
	
	public static String EDUCATIONFEETOTAL = "educationfeetotal";
	public static String EDUCATIONFEEDTOTAL = "educationfeedtotal";
	public static String EDUCATIONFEE = "educationfee";
	
	public static String ALLFEETOTAL = "allfeetotal";
	public static String ALLFEEDTOTAL = "allfeedtotal";
	public static String ALLFEE = "allfee";

	public static String TAXYEAR = "taxyear";
	public static String TAXPERIOD = "taxperiod";
	public static String TAX_STATUS = "tax_status";
	public static String LOCKCLASSNAME = "lockclassname";
	public static String LOCKCLASS = "lockclass";

	public String getTaxyear() {
		return taxyear;
	}
	public void setTaxyear(String taxyear) {
		this.taxyear = taxyear;
	}
	public String getTaxperiod() {
		return taxperiod;
	}
	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}
	public Integer getParentfeetotal() {
		return parentfeetotal;
	}
	public void setParentfeetotal(Integer parentfeetotal) {
		this.parentfeetotal = parentfeetotal;
	}
	public Integer getParetnfeedtotal() {
		return paretnfeedtotal;
	}
	public void setParetnfeedtotal(Integer paretnfeedtotal) {
		this.paretnfeedtotal = paretnfeedtotal;
	}
	public Integer getParentfee() {
		return parentfee;
	}
	public void setParentfee(Integer parentfee) {
		this.parentfee = parentfee;
	}
	public Integer getChildfeetotal() {
		return childfeetotal;
	}
	public void setChildfeetotal(Integer childfeetotal) {
		this.childfeetotal = childfeetotal;
	}
	public Integer getChildfeedtotal() {
		return childfeedtotal;
	}
	public void setChildfeedtotal(Integer childfeedtotal) {
		this.childfeedtotal = childfeedtotal;
	}
	public Integer getChildfee() {
		return childfee;
	}
	public void setChildfee(Integer childfee) {
		this.childfee = childfee;
	}
	public Integer getHoursefeetotal() {
		return hoursefeetotal;
	}
	public void setHoursefeetotal(Integer hoursefeetotal) {
		this.hoursefeetotal = hoursefeetotal;
	}
	public Integer getHoursefeedtotal() {
		return hoursefeedtotal;
	}
	public void setHoursefeedtotal(Integer hoursefeedtotal) {
		this.hoursefeedtotal = hoursefeedtotal;
	}
	public Integer getHoursefee() {
		return hoursefee;
	}
	public void setHoursefee(Integer hoursefee) {
		this.hoursefee = hoursefee;
	}
	public Integer getHoursezufeetotal() {
		return hoursezufeetotal;
	}
	public void setHoursezufeetotal(Integer hoursezufeetotal) {
		this.hoursezufeetotal = hoursezufeetotal;
	}
	public Integer getHoursezufeedtotal() {
		return hoursezufeedtotal;
	}
	public void setHoursezufeedtotal(Integer hoursezufeedtotal) {
		this.hoursezufeedtotal = hoursezufeedtotal;
	}
	public Integer getHoursezufee() {
		return hoursezufee;
	}
	public void setHoursezufee(Integer hoursezufee) {
		this.hoursezufee = hoursezufee;
	}
	public Integer getHealthyfeetotal() {
		return healthyfeetotal;
	}
	public void setHealthyfeetotal(Integer healthyfeetotal) {
		this.healthyfeetotal = healthyfeetotal;
	}
	public Integer getHealthyfeedtotal() {
		return healthyfeedtotal;
	}
	public void setHealthyfeedtotal(Integer healthyfeedtotal) {
		this.healthyfeedtotal = healthyfeedtotal;
	}
	public Integer getHealthyfee() {
		return healthyfee;
	}
	public void setHealthyfee(Integer healthyfee) {
		this.healthyfee = healthyfee;
	}
	public Integer getEducationfeetotal() {
		return educationfeetotal;
	}
	public void setEducationfeetotal(Integer educationfeetotal) {
		this.educationfeetotal = educationfeetotal;
	}
	public Integer getEducationfeedtotal() {
		return educationfeedtotal;
	}
	public void setEducationfeedtotal(Integer educationfeedtotal) {
		this.educationfeedtotal = educationfeedtotal;
	}
	public Integer getEducationfee() {
		return educationfee;
	}
	public void setEducationfee(Integer educationfee) {
		this.educationfee = educationfee;
	}
	public Integer getAllfeetotal() {
		return allfeetotal;
	}
	public void setAllfeetotal(Integer allfeetotal) {
		this.allfeetotal = allfeetotal;
	}
	public Integer getAllfeedtotal() {
		return allfeedtotal;
	}
	public void setAllfeedtotal(Integer allfeedtotal) {
		this.allfeedtotal = allfeedtotal;
	}
	public Integer getAllfee() {
		return allfee;
	}
	public void setAllfee(Integer allfee) {
		this.allfee = allfee;
	}
	 
}
