package nc.vo.ta.leave;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import nc.itf.ta.algorithm.ITimeScopeWithBillType;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.bill.IAgentPsn;
import nc.vo.ta.bill.ITimeScopeBillBodyVO;

/**
 * 由于休假申请子表和休假登记的表结构很相似，因此把公共部分抽成一个vo
 * @author zengcheng
 *
 */
public abstract class LeaveCommonVO extends SuperVO implements ITimeScopeBillBodyVO,IAgentPsn{

	/**
	 * 
	 */
	private static final long serialVersionUID = 563060761737217475L;
	

	private java.lang.String pk_org ;
	private java.lang.String pk_group ;
	private nc.vo.pub.lang.UFLiteralDate leavebegindate;
	private nc.vo.pub.lang.UFDateTime leavebegintime;
	private nc.vo.pub.lang.UFLiteralDate leaveenddate;
	private nc.vo.pub.lang.UFDateTime leaveendtime;
	private nc.vo.pub.lang.UFDouble leavehour;
	private nc.vo.pub.lang.UFBoolean islactation;
	private nc.vo.pub.lang.UFDouble lactationhour;
	private java.lang.String leaveremark;
	private java.lang.Integer lactationholidaytype;
	private java.lang.String leaveyear;
	private java.lang.String leavemonth;
	//此休假时段所属的自然年度。拆单计算时需要用到，其他地方都不用到。当参数“是否允许跨年度休假单保存”为N时需要设置此字段。
	//当不允许跨年度休假单保存时，休假的拆单算法需要将跨年度的休假时段拆分，并且，如果是申请单，要归在不同的申请单中，
	//因此需要给每一个休假时段设置一个所属的自然年度，不然无法知道两个休假时段是否属于同一自然年度
	//本来，从休假的开始日期/结束日期的所属自然年就能看出此休假时段所属的自然年，但有时候由于班次的工作时间段跨了0点，导致拆单的时候
	//可能会"稍微地"跨一下年，这种情况下，从开始日期/结束日期的所属自然年就不能准确地判断出其所属的自然年度了
	//例如，用户填写了2011-12-29 08:00:00到2012-01-01 04:00:00的一个休假时段，2011-12-31日的班次一直到2012-01-01 04:00:00才结束，这样的休假单，
	//我们是不会把它拆成2011-12-29 08:00:00-2012-01-01 00:00:00及2012-01-01 00:00:00-2012-01-01 04:00:00两张单子的，因为会导致休假时长计算失真
	private String simpleyear;
	private Integer leaveindex;
	
	//拆单id。在同一次拆单操作中生成的所有单据（包括申请单和登记单），都用同一个id。
	//并且在后续的修改单据时中新拆出来的单子，也使用此id
	//目前此id没有具体的应用
	private String splitid;
	
	//时长是否已经超限，用于拆单后的提示信息：如果此条记录导致时长超限，则为true
	private UFBoolean exceedlimit;

	private java.lang.String pk_agentpsn ;
	private java.lang.String workprocess;
	
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	
	private java.lang.String pk_org_v; //组织多版本
	private java.lang.String pk_dept_v;//部门多版本
	
	//人员主键、工作记录主键、休假类别主键都是冗余字段，便于处理
	private String bill_code;
	private java.lang.String pk_psndoc ;
	private java.lang.String pk_psnjob ;
	private String pk_psnorg;
	private String pk_joborg;
	private TimeZone timezone;
	private java.lang.String pk_leavetype;
	private java.lang.String pk_leavetypecopy ;

	private UFDouble resteddayorhour;//已休时长
	private UFDouble realdayorhour;//享有时长
	private UFDouble restdayorhour;//结余时长
	private UFDouble freezedayorhour;//冻结时长
	private UFDouble usefuldayorhour;//可用时长
	
	private UFDouble changelength;//调整时长
	
	
	
    private Map<String, ITimeScopeWithBillType> originalTimeScopeMap = null;
    
  
//	private Map<String,AggShiftVO> aggShiftMap = null;//所有班次,效率优化，用到的地方太多，且查询耗时，在此存储

	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_PSNORG = "pk_psnorg";
	public static final String PK_PSNJOB = "pk_psnjob";
	public static final String PK_JOBORG = "pk_joborg";
	public static final String TIMEZONE = "timezone";
	public static final String SIMPLEYEAR = "simpleyear";
	public static final String LEAVEYEAR = "leaveyear";
	public static final String LEAVEMONTH = "leavemonth";
	public static final String LEAVEINDEX = "leaveindex";
	public static final String LEAVEBEGINDATE = "leavebegindate";
	public static final String LEAVEBEGINTIME = "leavebegintime";
	public static final String LEAVEENDDATE = "leaveenddate";
	public static final String LEAVEENDTIME = "leaveendtime";
	public static final String LEAVEHOUR = "leavehour";
	public static final String RESTEDDAYORHOUR = "resteddayorhour";//已休时长
	public static final String REALDAYORHOUR = "realdayorhour";//享有时长
	public static final String RESTDAYORHOUR = "restdayorhour";//结余时长
	public static final String FREEZEDAYORHOUR = "freezedayorhour";//冻结时长
	public static final String USEFULDAYORHOUR = "usefuldayorhour";//可用时长
	public static final String ISLACTATION = "islactation";
	public static final String LACTATIONHOUR = "lactationhour";
	public static final String LEAVEREMARK = "leaveremark";
	public static final String LACTATIONHOLIDAYTYPE = "lactationholidaytype";
	public static final String WORKPROCESS = "workprocess";
	public static final String PK_LEAVETYPE = "pk_leavetype";
	public static final String PK_LEAVETYPECOPY = "pk_leavetypecopy";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String PK_DEPT_V= "pk_dept_v";
	public static final String SPLITID= "splitid";
	public static final String EXCEEDLIMIT= "exceedlimit";
	public static final String CHANGELENGTH= "changelength";	

	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	
	public UFDouble getChangelength() {
		return changelength;
	}
	public void setChangelength(UFDouble changelength) {
		this.changelength = changelength;
	}
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	public java.lang.String getPk_group() {
		return pk_group;
	}
	public void setPk_group(java.lang.String pkGroup) {
		pk_group = pkGroup;
	}
	public java.lang.String getPk_org() {
		return pk_org;
	}
	public void setPk_org(java.lang.String pkOrg) {
		pk_org = pkOrg;
	}
	public nc.vo.pub.lang.UFLiteralDate getLeavebegindate() {
		return leavebegindate;
	}
	public void setLeavebegindate(nc.vo.pub.lang.UFLiteralDate leavebegindate) {
		this.leavebegindate = leavebegindate;
	}
	public nc.vo.pub.lang.UFDateTime getLeavebegintime() {
		return leavebegintime;
	}
	public void setLeavebegintime(nc.vo.pub.lang.UFDateTime leavebegintime) {
		this.leavebegintime = leavebegintime;
	}
	public nc.vo.pub.lang.UFLiteralDate getLeaveenddate() {
		return leaveenddate;
	}
	public void setLeaveenddate(nc.vo.pub.lang.UFLiteralDate leaveenddate) {
		this.leaveenddate = leaveenddate;
	}
	public nc.vo.pub.lang.UFDateTime getLeaveendtime() {
		return leaveendtime;
	}
	public void setLeaveendtime(nc.vo.pub.lang.UFDateTime leaveendtime) {
		this.leaveendtime = leaveendtime;
	}
	public nc.vo.pub.lang.UFDouble getLeavehour() {
		return leavehour;
	}
	public double getLeaveHourValue(){
		return leavehour==null?0:leavehour.doubleValue();	
	}
	public void setLeavehour(nc.vo.pub.lang.UFDouble leavehour) {
		this.leavehour = leavehour;
	}
	public java.lang.String getLeaveremark() {
		return leaveremark;
	}
	public void setLeaveremark(java.lang.String leaveremark) {
		this.leaveremark = leaveremark;
	}
	public java.lang.Integer getLactationholidaytype() {
		return lactationholidaytype;
	}
	public void setLactationholidaytype(java.lang.Integer lactationholidaytype) {
		this.lactationholidaytype = lactationholidaytype;
	}
	public java.lang.Integer getDr() {
		return dr;
	}
	public void setDr(java.lang.Integer dr) {
		this.dr = dr;
	}
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}
	public void setTs(nc.vo.pub.lang.UFDateTime ts) {
		this.ts = ts;
	}
	public String getBill_code() {
		return bill_code;
	}
	public void setBill_code(String billCode) {
		bill_code = billCode;
	}
	public String getPk_psndoc() {
		return pk_psndoc;
	}
	public void setPk_psndoc(String pkPsndoc) {
		pk_psndoc = pkPsndoc;
	}
	public String getPk_psnjob() {
		return pk_psnjob;
	}
	public void setPk_psnjob(String pkPsnjob) {
		pk_psnjob = pkPsnjob;
	}
	public String getPk_leavetype() {
		return pk_leavetype;
	}
	public void setPk_leavetype(String pkLeavetype) {
		pk_leavetype = pkLeavetype;
		if(originalTimeScopeMap!=null&&!StringUtils.isEmpty(pk_leavetype))
			originalTimeScopeMap.put(pk_leavetype, this);
	}
	@Override
	public boolean belongsToTimeItem(String timeItemPK) {
		return timeItemPK.equals(pk_leavetype);
	}
	@Override
	public int getBillType() {
		return BillMutexRule.BILL_LEAVE;
	}
	@Override
	public Map<String, ITimeScopeWithBillType> getOriginalTimeScopeMap() {
		if (!StringUtils.isNotBlank(pk_leavetype))
        {
            throw new RuntimeException("the type of the timeitem must be set!plz inform zc when u see this message");
        }
        if (originalTimeScopeMap == null)
        {
            originalTimeScopeMap = new HashMap<String, ITimeScopeWithBillType>(2);
            if (pk_leavetype != null)
            {
                originalTimeScopeMap.put(pk_leavetype, this);
            }
        }
        return originalTimeScopeMap;
	}
	@Override
	public boolean isContainsLastSecond() {
		return false;
	}
	@Override
	public long getMaxLength() {
		return Long.MAX_VALUE;
	}
	@Override
	public UFDateTime getScope_end_datetime() {
		return getLeaveendtime();
	}
	@Override
	public UFDateTime getScope_start_datetime() {
		return getLeavebegintime();
	}
	@Override
	public void setScope_end_datetime(UFDateTime dt) {
		setLeaveendtime(dt);
	}
	@Override
	public void setScope_start_datetime(UFDateTime dt) {
		setLeavebegintime(dt);
	}
	@Override
	public String getPk_timeitem() {
		return getPk_leavetype();
	}
	@Override
	public void setPk_timeitem(String pkTimeitem) {
		setPk_leavetype(pkTimeitem);
	}

	@Override
	public UFLiteralDate getBegindate() {
		return getLeavebegindate();
	}

	@Override
	public UFLiteralDate getEnddate() {
		return getLeaveenddate();
	}

	@Override
	public void setBegindate(UFLiteralDate beginDate) {
		setLeavebegindate(beginDate);
		
	}

	@Override
	public void setEnddate(UFLiteralDate endDate) {
		setLeaveenddate(endDate);
		
	}

	@Override
	public java.lang.String getPk_agentpsn() {
		return pk_agentpsn;
	}
	@Override
	public void setPk_agentpsn(java.lang.String pk_agentpsn) {
		this.pk_agentpsn = pk_agentpsn;
	}

	public java.lang.String getWorkprocess() {
		return workprocess;
	}

	public void setWorkprocess(java.lang.String workprocess) {
		this.workprocess = workprocess;
	}

	@Override
	public UFLiteralDate getBillBeginDate() {
		return leavebegindate;
	}

	@Override
	public void setBillBeginDate(UFLiteralDate beginDate){
		this.leavebegindate = beginDate;
	}
	@Override
	public UFLiteralDate getBillEndDate() {
		return leaveenddate;
	}
	@Override
	public void setBillEndDate(UFLiteralDate endDate){
		this.leaveenddate = endDate;
	}

	@Override
	public UFDouble getLength() {
		return leavehour;
	}

	@Override
	public void setLength(UFDouble len) {
		leavehour=len;
	}

	public java.lang.String getPk_leavetypecopy() {
		return pk_leavetypecopy;
	}

	public void setPk_leavetypecopy(java.lang.String pkLeavetypecopy) {
		pk_leavetypecopy = pkLeavetypecopy;
	}

	public String getPk_psnorg() {
		return pk_psnorg;
	}

	public void setPk_psnorg(String pkPsnorg) {
		pk_psnorg = pkPsnorg;
	}    /**
	 * 属性islactation的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIslactation () {
		return islactation;
	}   
	/**
	 * 属性islactation的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newIslactation nc.vo.pub.lang.UFBoolean
	 */
	public void setIslactation (nc.vo.pub.lang.UFBoolean newIslactation ) {
	 	this.islactation = newIslactation;
	} 	  
	public UFDouble getLactationhour() {
		return lactationhour;
	}
	public void setLactationhour(UFDouble lactationhour) {
		this.lactationhour = lactationhour;
	}

	public String getPk_joborg() {
		return pk_joborg;
	}

	public void setPk_joborg(String pkJoborg) {
		pk_joborg = pkJoborg;
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}
	
	/**
	 * 属性leaveyear的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getLeaveyear () {
		return leaveyear;
	}   
	/**
	 * 属性leaveyear的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newLeaveyear java.lang.String
	 */
	public void setLeaveyear (java.lang.String newLeaveyear ) {
	 	this.leaveyear = newLeaveyear;
	} 	  
	/**
	 * 属性leavemonth的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getLeavemonth () {
		return leavemonth;
	}   
	/**
	 * 属性leavemonth的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newLeavemonth java.lang.String
	 */
	public void setLeavemonth (java.lang.String newLeavemonth ) {
	 	this.leavemonth = newLeavemonth;
	}

	public Integer getLeaveindex() {
		return leaveindex;
	}

	public void setLeaveindex(Integer leaveindex) {
		this.leaveindex = leaveindex;
	}

	public java.lang.String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(java.lang.String pkOrgV) {
		pk_org_v = pkOrgV;
	}

	public java.lang.String getPk_dept_v() {
		return pk_dept_v;
	}

	public void setPk_dept_v(java.lang.String pkDeptV) {
		pk_dept_v = pkDeptV;
	}

	public String getSimpleyear() {
		return simpleyear;
	}

	public void setSimpleyear(String simpleYear) {
		this.simpleyear = simpleYear;
	}

	@Override
	public void setContainsLastSecond(boolean contains) {
		
	}

	public String getSplitid() {
		return splitid;
	}

	public void setSplitid(String splitid) {
		this.splitid = splitid;
	}

	public UFBoolean getExceedlimit() {
		return exceedlimit;
	}

	public void setExceedlimit(UFBoolean exceedlimit) {
		this.exceedlimit = exceedlimit;
	}

	public UFDouble getResteddayorhour() {
		return resteddayorhour;
	}

	public void setResteddayorhour(UFDouble resteddayorhour) {
		this.resteddayorhour = resteddayorhour;
	}

	public UFDouble getRealdayorhour() {
		return realdayorhour;
	}

	public void setRealdayorhour(UFDouble realdayorhour) {
		this.realdayorhour = realdayorhour;
	}

	public UFDouble getRestdayorhour() {
		return restdayorhour;
	}

	public void setRestdayorhour(UFDouble restdayorhour) {
		this.restdayorhour = restdayorhour;
	}

	public UFDouble getFreezedayorhour() {
		return freezedayorhour;
	}

	public void setFreezedayorhour(UFDouble freezedayorhour) {
		this.freezedayorhour = freezedayorhour;
	}

	public UFDouble getUsefuldayorhour() {
		return usefuldayorhour;
	}

	public void setUsefuldayorhour(UFDouble usefuldayorhour) {
		this.usefuldayorhour = usefuldayorhour;
	}
	
	public String getYearmonth(){
		if(StringUtils.isEmpty(getLeavemonth()))
			return getLeaveyear();
		return getLeaveyear()+getLeavemonth();
	}


	@Override
	public Object clone() {
		LeaveCommonVO vo=(LeaveCommonVO) super.clone();
		vo.originalTimeScopeMap=null;
		return vo;
	}

//	public Map<String, AggShiftVO> getAggShiftMap() {
//		return aggShiftMap;
//	}
//
//	public void setAggShiftMap(Map<String, AggShiftVO> aggShiftMap) {
//		this.aggShiftMap = aggShiftMap;
//	}

}
