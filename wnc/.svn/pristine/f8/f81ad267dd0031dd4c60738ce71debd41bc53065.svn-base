package nc.vo.ta.leave;

import java.util.Map;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.common.ConvertVOUtil;

import org.apache.commons.lang.StringUtils;
/**
 * 休假登记记录表，供日薪计算使用
 * 每天只允许存放一张休假登记记录表，当已经扣款过后，不允许当天继续修改
 * @author ward
 * @date 20180504
 *
 */
public class LeaveRegHistoryVO extends LeaveRegVO{

	private static final long serialVersionUID = 22433804426521408L;
	
	private String pk_leavereg_history;// 主键

	private UFBoolean ischarge = UFBoolean.FALSE;// 是否扣款

	private UFDouble charge;// 扣款金额

	private UFDouble actualcharge;// 实际扣款

	private java.lang.String pk_leavereg;
	private java.lang.String relatetel;
	private nc.vo.pub.lang.UFBoolean isleaveoff;
	private nc.vo.pub.lang.UFDateTime backtime;
	private java.lang.Integer billsource;
	private java.lang.String pk_billsourceh;
	private java.lang.String pk_billsourceb;
	private java.lang.String pk_adminorg;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private nc.vo.pub.lang.UFLiteralDate effectivedate;

	private String pk_org;
	private String pk_group;
	private UFLiteralDate leavebegindate;
	private UFDateTime leavebegintime;
	private UFLiteralDate leaveenddate;
	private UFDateTime leaveendtime;
	private UFDouble leavehour;
	private UFBoolean islactation;
	private UFDouble lactationhour;
	private String leaveremark;
	private Integer lactationholidaytype;
	private String leaveyear;
	private String leavemonth;
	private String simpleyear;
	private Integer leaveindex;
	private String splitid;
	private UFBoolean exceedlimit;
	private String pk_agentpsn;
	private String workprocess;
	private Integer dr = Integer.valueOf(0);
	private String pk_org_v;
	private String pk_dept_v;
	private String bill_code;
	private String pk_psndoc;
	private String pk_psnjob;
	private String pk_psnorg;
	private String pk_joborg;
	private String pk_leavetype;
	private String pk_leavetypecopy;
	private UFDouble resteddayorhour;
	private UFDouble realdayorhour;
	private UFDouble restdayorhour;
	private UFDouble freezedayorhour;
	private UFDouble usefuldayorhour;
	
	

	public UFDouble getActualcharge() {
		return actualcharge;
	}

	public void setActualcharge(UFDouble actualcharge) {
		this.actualcharge = actualcharge;
	}

	public java.lang.String getPk_leavereg() {
		return pk_leavereg;
	}

	public void setPk_leavereg(java.lang.String pk_leavereg) {
		this.pk_leavereg = pk_leavereg;
	}

	public java.lang.String getRelatetel() {
		return relatetel;
	}

	public void setRelatetel(java.lang.String relatetel) {
		this.relatetel = relatetel;
	}

	public nc.vo.pub.lang.UFBoolean getIsleaveoff() {
		return isleaveoff;
	}

	public void setIsleaveoff(nc.vo.pub.lang.UFBoolean isleaveoff) {
		this.isleaveoff = isleaveoff;
	}

	public nc.vo.pub.lang.UFDateTime getBacktime() {
		return backtime;
	}

	public void setBacktime(nc.vo.pub.lang.UFDateTime backtime) {
		this.backtime = backtime;
	}

	public java.lang.Integer getBillsource() {
		return billsource;
	}

	public void setBillsource(java.lang.Integer billsource) {
		this.billsource = billsource;
	}

	public java.lang.String getPk_billsourceh() {
		return pk_billsourceh;
	}

	public void setPk_billsourceh(java.lang.String pk_billsourceh) {
		this.pk_billsourceh = pk_billsourceh;
	}

	public java.lang.String getPk_billsourceb() {
		return pk_billsourceb;
	}

	public void setPk_billsourceb(java.lang.String pk_billsourceb) {
		this.pk_billsourceb = pk_billsourceb;
	}

	public java.lang.String getPk_adminorg() {
		return pk_adminorg;
	}

	public void setPk_adminorg(java.lang.String pk_adminorg) {
		this.pk_adminorg = pk_adminorg;
	}

	public java.lang.String getCreator() {
		return creator;
	}

	public void setCreator(java.lang.String creator) {
		this.creator = creator;
	}

	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(nc.vo.pub.lang.UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public java.lang.String getModifier() {
		return modifier;
	}

	public void setModifier(java.lang.String modifier) {
		this.modifier = modifier;
	}

	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(nc.vo.pub.lang.UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	public nc.vo.pub.lang.UFLiteralDate getEffectivedate() {
		return effectivedate;
	}

	public void setEffectivedate(nc.vo.pub.lang.UFLiteralDate effectivedate) {
		this.effectivedate = effectivedate;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public UFLiteralDate getLeavebegindate() {
		return leavebegindate;
	}

	public void setLeavebegindate(UFLiteralDate leavebegindate) {
		this.leavebegindate = leavebegindate;
	}

	public UFDateTime getLeavebegintime() {
		return leavebegintime;
	}

	public void setLeavebegintime(UFDateTime leavebegintime) {
		this.leavebegintime = leavebegintime;
	}

	public UFLiteralDate getLeaveenddate() {
		return leaveenddate;
	}

	public void setLeaveenddate(UFLiteralDate leaveenddate) {
		this.leaveenddate = leaveenddate;
	}

	public UFDateTime getLeaveendtime() {
		return leaveendtime;
	}

	public void setLeaveendtime(UFDateTime leaveendtime) {
		this.leaveendtime = leaveendtime;
	}

	public UFDouble getLeavehour() {
		return leavehour;
	}

	public void setLeavehour(UFDouble leavehour) {
		this.leavehour = leavehour;
	}

	public UFBoolean getIslactation() {
		return islactation;
	}

	public void setIslactation(UFBoolean islactation) {
		this.islactation = islactation;
	}

	public UFDouble getLactationhour() {
		return lactationhour;
	}

	public void setLactationhour(UFDouble lactationhour) {
		this.lactationhour = lactationhour;
	}

	public String getLeaveremark() {
		return leaveremark;
	}

	public void setLeaveremark(String leaveremark) {
		this.leaveremark = leaveremark;
	}

	public Integer getLactationholidaytype() {
		return lactationholidaytype;
	}

	public void setLactationholidaytype(Integer lactationholidaytype) {
		this.lactationholidaytype = lactationholidaytype;
	}

	public String getLeaveyear() {
		return leaveyear;
	}

	public void setLeaveyear(String leaveyear) {
		this.leaveyear = leaveyear;
	}

	public String getLeavemonth() {
		return leavemonth;
	}

	public void setLeavemonth(String leavemonth) {
		this.leavemonth = leavemonth;
	}

	public String getSimpleyear() {
		return simpleyear;
	}

	public void setSimpleyear(String simpleyear) {
		this.simpleyear = simpleyear;
	}

	public Integer getLeaveindex() {
		return leaveindex;
	}

	public void setLeaveindex(Integer leaveindex) {
		this.leaveindex = leaveindex;
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

	public String getPk_agentpsn() {
		return pk_agentpsn;
	}

	public void setPk_agentpsn(String pk_agentpsn) {
		this.pk_agentpsn = pk_agentpsn;
	}

	public String getWorkprocess() {
		return workprocess;
	}

	public void setWorkprocess(String workprocess) {
		this.workprocess = workprocess;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	public String getPk_dept_v() {
		return pk_dept_v;
	}

	public void setPk_dept_v(String pk_dept_v) {
		this.pk_dept_v = pk_dept_v;
	}

	public String getBill_code() {
		return bill_code;
	}

	public void setBill_code(String bill_code) {
		this.bill_code = bill_code;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String getPk_psnjob() {
		return pk_psnjob;
	}

	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	public String getPk_psnorg() {
		return pk_psnorg;
	}

	public void setPk_psnorg(String pk_psnorg) {
		this.pk_psnorg = pk_psnorg;
	}

	public String getPk_joborg() {
		return pk_joborg;
	}

	public void setPk_joborg(String pk_joborg) {
		this.pk_joborg = pk_joborg;
	}

	public String getPk_leavetype() {
		return pk_leavetype;
	}

	public void setPk_leavetype(String pk_leavetype) {
		this.pk_leavetype = pk_leavetype;
	}

	public String getPk_leavetypecopy() {
		return pk_leavetypecopy;
	}

	public void setPk_leavetypecopy(String pk_leavetypecopy) {
		this.pk_leavetypecopy = pk_leavetypecopy;
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

	public String getPk_leavereg_history() {
		return pk_leavereg_history;
	}

	public void setPk_leavereg_history(String pk_leavereg_history) {
		this.pk_leavereg_history = pk_leavereg_history;
	}

	public UFBoolean getIscharge() {
		return ischarge;
	}

	public void setIscharge(UFBoolean ischarge) {
		this.ischarge = ischarge;
	}

	public UFDouble getCharge() {
		return charge;
	}

	public void setCharge(UFDouble charge) {
		this.charge = charge;
	}

	public UFDouble getActualCharge() {
		return actualcharge;
	}

	public void setActualCharge(UFDouble actualCharge) {
		this.actualcharge = actualCharge;
	}
	
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_leavereg_history";
	}
   
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_leavereg_history";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_leavereg_history";
	}

	public static LeaveRegHistoryVO toHistoryVO(LeaveRegVO vo) {
		LeaveRegHistoryVO historyVO = new LeaveRegHistoryVO();
		Map<String, String> voAttrTypeMap = ConvertVOUtil
				.getVOFieldType(LeaveRegHistoryVO.class);
		for (String voAttr : voAttrTypeMap.keySet()) {
			String value = ConvertVOUtil.getStringValue(vo
					.getAttributeValue(voAttr));
			if (StringUtils.isNotBlank(voAttr)) {
				String attrType = voAttrTypeMap.get(voAttr);
				if (StringUtils.isNotBlank(attrType)) {
					ConvertVOUtil.setVoFieldValueByType(historyVO, attrType,
							voAttr, value);
				}
			}
		}
		return historyVO;
	}
   
}
