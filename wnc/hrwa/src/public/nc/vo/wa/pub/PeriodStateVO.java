package nc.vo.wa.pub;

import nc.hr.utils.ResHelper;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;

@SuppressWarnings("serial")
public class PeriodStateVO extends SuperVO {
	public static final String CPAYDATE = "cpaydate";
	public static final String VPAYCOMMENT = "vpaycomment";
	public static final String CACULATEFLAG = "caculateflag";
	public static final String CHECKFLAG = "checkflag";
	public static final String PAYOFFFLAG = "payoffflag";
	public static final String CSTARTDATE = "cstartdate";
	public static final String CENDDATE = "cenddate";
	private UFBoolean accountmark;
	private UFBoolean approvetype;
	private String caccperiod;
	private String caccyear;
	private UFBoolean caculateflag;
	private UFLiteralDate cenddate;
	private UFBoolean checkflag;
	private UFDate cpaydate;
	private String cperiod;
	private String cpreclassid;
	private UFLiteralDate cstartdate;
	private String cyear;
	private UFDate daccdate;
	private UFBoolean enableflag;
	private UFBoolean isapproved;
	private UFBoolean isapporve;
	private Integer classtype;
	private String operatorid;
	private UFBoolean payoffflag;
	private String pk_group;
	private String pk_org;
	private String pk_periodscheme;
	private String pk_periodstate;
	private String pk_wa_class;
	private String pk_wa_period;
	private UFDateTime ts;
	private String vcalmonth;
	private String vcalyear;
	private String vpaycomment;
	private Integer batchnum;

	public Integer getBatchnum() {
		return this.batchnum;
	}

	public void setBatchnum(Integer batchnum) {
		this.batchnum = batchnum;
	}

	public String toString() {
		return getCyear() + ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0246") + getCperiod()
				+ ResHelper.getString("common", "UC000-0002560");
	}

	public UFBoolean getAccountmark() {
		return this.accountmark;
	}

	public UFBoolean getApprovetype() {
		return this.approvetype;
	}

	public String getCaccperiod() {
		return this.caccperiod;
	}

	public String getCaccyear() {
		return this.caccyear;
	}

	public UFBoolean getCaculateflag() {
		return this.caculateflag;
	}

	public UFLiteralDate getCenddate() {
		return this.cenddate;
	}

	public UFBoolean getCheckflag() {
		return this.checkflag;
	}

	public UFDate getCpaydate() {
		return this.cpaydate;
	}

	public String getCperiod() {
		return this.cperiod;
	}

	public String getCpreclassid() {
		return this.cpreclassid;
	}

	public UFLiteralDate getCstartdate() {
		return this.cstartdate;
	}

	public String getCyear() {
		return this.cyear;
	}

	public UFDate getDaccdate() {
		return this.daccdate;
	}

	public UFBoolean getEnableflag() {
		return this.enableflag;
	}

	public UFBoolean getIsapproved() {
		return this.isapproved;
	}

	public UFBoolean getIsapporve() {
		return this.isapporve;
	}

	public String getOperatorid() {
		return this.operatorid;
	}

	public UFBoolean getPayoffflag() {
		return this.payoffflag;
	}

	public String getPk_group() {
		return this.pk_group;
	}

	public String getPk_org() {
		return this.pk_org;
	}

	public String getPk_periodscheme() {
		return this.pk_periodscheme;
	}

	public String getPk_periodstate() {
		return this.pk_periodstate;
	}

	public String getPk_wa_class() {
		return this.pk_wa_class;
	}

	public String getPk_wa_period() {
		return this.pk_wa_period;
	}

	public UFDateTime getTs() {
		return this.ts;
	}

	public String getVcalmonth() {
		return this.vcalmonth;
	}

	public String getVcalyear() {
		return this.vcalyear;
	}

	public String getVpaycomment() {
		return this.vpaycomment;
	}

	public void setAccountmark(UFBoolean accountmark) {
		this.accountmark = accountmark;
	}

	public void setApprovetype(UFBoolean approvetype) {
		this.approvetype = approvetype;
	}

	public void setCaccperiod(String caccperiod) {
		this.caccperiod = caccperiod;
	}

	public void setCaccyear(String caccyear) {
		this.caccyear = caccyear;
	}

	public void setCaculateflag(UFBoolean caculateflag) {
		this.caculateflag = caculateflag;
	}

	public void setCenddate(UFLiteralDate cenddate) {
		this.cenddate = cenddate;
	}

	public void setCheckflag(UFBoolean checkflag) {
		this.checkflag = checkflag;
	}

	public void setCpaydate(UFDate cpaydate) {
		this.cpaydate = cpaydate;
	}

	public void setCperiod(String cperiod) {
		this.cperiod = cperiod;
	}

	public void setCpreclassid(String cpreclassid) {
		this.cpreclassid = cpreclassid;
	}

	public void setCstartdate(UFLiteralDate cstartdate) {
		this.cstartdate = cstartdate;
	}

	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	public void setDaccdate(UFDate daccdate) {
		this.daccdate = daccdate;
	}

	public void setEnableflag(UFBoolean enableflag) {
		this.enableflag = enableflag;
	}

	public void setIsapproved(UFBoolean isapproved) {
		this.isapproved = isapproved;
	}

	public void setIsapporve(UFBoolean isapporve) {
		this.isapporve = isapporve;
	}

	public void setOperatorid(String operatorid) {
		this.operatorid = operatorid;
	}

	public void setPayoffflag(UFBoolean payoffflag) {
		this.payoffflag = payoffflag;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public void setPk_periodscheme(String pk_periodscheme) {
		this.pk_periodscheme = pk_periodscheme;
	}

	public void setPk_periodstate(String pk_periodstate) {
		this.pk_periodstate = pk_periodstate;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public void setPk_wa_period(String pk_wa_period) {
		this.pk_wa_period = pk_wa_period;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public void setVcalmonth(String vcalmonth) {
		this.vcalmonth = vcalmonth;
	}

	public void setVcalyear(String vcalyear) {
		this.vcalyear = vcalyear;
	}

	public void setVpaycomment(String vpaycomment) {
		this.vpaycomment = vpaycomment;
	}

	public Integer getClasstype() {
		return this.classtype;
	}

	public void setClasstype(Integer classtype) {
		this.classtype = classtype;
	}

	public void setAttributeValue(String name, Object value) {
		if ((name.equals("cstartdate")) && (value != null))
			this.cstartdate = new UFLiteralDate(value.toString());
		else if ((name.equals("cenddate")) && (value != null))
			this.cenddate = new UFLiteralDate(value.toString());
		else
			super.setAttributeValue(name, value);
	}

	public Object getAttributeValue(String name) {
		if (name.equals("cstartdate"))
			return this.cstartdate;
		if (name.equals("cenddate")) {
			return this.cenddate;
		}
		return super.getAttributeValue(name);
	}

	// MOD {paydata move} kevin.nie 2017-03-01 start
	public static final String PK_PERIODSTATE = "pk_periodstate";
	public static final String TABLENAME = "wa_periodstate";

	@Override
	public String getPKFieldName() {
		return PK_PERIODSTATE;
	}

	@Override
	public String getTableName() {
		return TABLENAME;
	}
	// {paydata move} kevin.nie 2017-03-01 end
}