package nc.vo.ta.leavebalance;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;

public class LeaveBalanceVO extends SuperVO {
	private String pk_leavebalance;
	private String pk_group;
	private String pk_org;
	private String pk_psndoc;
	private String pk_psnorg;
	private String pk_psnjob;
	private String pk_tbm_psndoc;
	private String pk_timeitem;
	private String pk_timeitemcopy;
	private UFDouble lastdayorhour;
	private UFDouble curdayorhour;
	private UFDouble realdayorhour;
	private UFDouble yidayorhour;
	private UFDouble restdayorhour;
	private UFDouble freezedayorhour;
	private UFDateTime calculatetime;
	private UFLiteralDate settlementdate;
	private String curyear;
	private String curmonth;
	private Integer leaveindex;
	private UFBoolean issettlement;
	private UFBoolean isuse;
	private UFBoolean isabnormalset;
	private UFLiteralDate hirebegindate;
	private UFLiteralDate hireenddate;
	private UFLiteralDate periodbegindate;
	private UFLiteralDate periodenddate;
	private UFLiteralDate periodextendenddate;
	private Integer settlementmethod;
	private Integer dr = Integer.valueOf(0);

	private UFDateTime ts;

	private String pk_org_v;

	private String pk_dept_v;

	private int leavesetperiod;

	private UFDouble translatein;

	private UFDouble translateout;

	private UFBoolean transflag;

	private UFDouble changelength;
	private String salaryyear;
	private String salarymonth;
	public static final String PK_LEAVEBALANCE = "pk_leavebalance";
	public static final String PK_TBM_PSNDOC = "pk_tbm_psndoc";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String PK_DEPT_V = "pk_dept_v";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_PSNORG = "pk_psnorg";
	public static final String PK_PSNJOB = "pk_psnjob";
	public static final String PK_TIMEITEM = "pk_timeitem";
	public static final String PK_TIMEITEMCOPY = "pk_timeitemcopy";
	public static final String LASTDAYORHOUR = "lastdayorhour";
	public static final String CURDAYORHOUR = "curdayorhour";
	public static final String REALDAYORHOUR = "realdayorhour";
	public static final String YIDAYORHOUR = "yidayorhour";
	public static final String RESTDAYORHOUR = "restdayorhour";
	public static final String FREEZEDAYORHOUR = "freezedayorhour";
	public static final String USEFULRESTDAYORHOUR = "usefulrestdayorhour";
	public static final String CALCULATETIME = "calculatetime";
	public static final String SETTLEMENTDATE = "settlementdate";
	public static final String CURYEAR = "curyear";
	public static final String CURMONTH = "curmonth";
	public static final String LEAVEINDEX = "leaveindex";
	public static final String ISSETTLEMENT = "issettlement";
	public static final String ISUSE = "isuse";
	public static final String ISABNORMALSET = "isabnormalset";
	public static final String HIREBEGINDATE = "hirebegindate";
	public static final String SETTLEMENTMETHOD = "settlementmethod";
	public static final String HIREENDDATE = "hireenddate";
	public static final String TRANSLATEIN = "translatein";
	public static final String TRANSLATEOUT = "translateout";
	public static final String TRANSFLAG = "transflag";
	public static final String CHANGELENGTH = "changelength";
	public static final String SALARYYEAR = "salaryyear";
	public static final String SALARYMONTH = "salarymonth";
	public static final String TIMETOLEAVETYPE = "1002Z710000000021ZM1";

	public String getPk_leavebalance() {
		return pk_leavebalance;
	}

	public void setPk_leavebalance(String newPk_leavebalance) {
		pk_leavebalance = newPk_leavebalance;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String newPk_group) {
		pk_group = newPk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String newPk_org) {
		pk_org = newPk_org;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String newPk_psndoc) {
		pk_psndoc = newPk_psndoc;
	}

	public String getPk_timeitem() {
		return pk_timeitem;
	}

	public void setPk_timeitem(String newPk_timeitem) {
		pk_timeitem = newPk_timeitem;
	}

	public UFDouble getLastdayorhour() {
		return lastdayorhour;
	}

	public void setLastdayorhour(UFDouble newLastdayorhour) {
		lastdayorhour = newLastdayorhour;
	}

	public UFDouble getCurdayorhour() {
		return curdayorhour;
	}

	public void setCurdayorhour(UFDouble newCurdayorhour) {
		curdayorhour = newCurdayorhour;
	}

	public UFDouble getRealdayorhour() {
		return realdayorhour;
	}

	public void setRealdayorhour(UFDouble newRealdayorhour) {
		realdayorhour = newRealdayorhour;
	}

	public UFDouble getYidayorhour() {
		return yidayorhour;
	}

	public void setYidayorhour(UFDouble newYidayorhour) {
		yidayorhour = newYidayorhour;
	}

	public UFDouble getRestdayorhour() {
		return restdayorhour;
	}

	public void minusRestdayorhour(double toMinusLen) {
		if (toMinusLen == 0.0D)
			return;
		if (getRestdayorhour() == null) {
			setRestdayorhour(new UFDouble(0.0D - toMinusLen));
			return;
		}
		setRestdayorhour(getRestdayorhour().sub(toMinusLen));
	}

	public void plusFreezedayorhour(double toPlusLen) {
		if (toPlusLen == 0.0D)
			return;
		if (getFreezedayorhour() == null) {
			setFreezedayorhour(new UFDouble(toPlusLen));
			return;
		}
		setFreezedayorhour(getFreezedayorhour().add(toPlusLen));
	}

	public UFDouble getUsefulRestdayorhour() {
		return (getRestdayorhour() == null ? UFDouble.ZERO_DBL : getRestdayorhour())
				.sub(getFreezedayorhour() == null ? UFDouble.ZERO_DBL : getFreezedayorhour());
	}

	public void setRestdayorhour(UFDouble newRestdayorhour) {
		restdayorhour = newRestdayorhour;
	}

	public String getPk_timeitemcopy() {
		return pk_timeitemcopy;
	}

	public void setPk_timeitemcopy(String pk_timeitemcopy) {
		this.pk_timeitemcopy = pk_timeitemcopy;
	}

	public UFDateTime getCalculatetime() {
		return calculatetime;
	}

	public void setCalculatetime(UFDateTime newCalculatetime) {
		calculatetime = newCalculatetime;
	}

	public String getCuryear() {
		return curyear;
	}

	public void setCuryear(String newCuryear) {
		curyear = newCuryear;
	}

	public String getCurmonth() {
		return curmonth;
	}

	public void setCurmonth(String newCurmonth) {
		curmonth = newCurmonth;
	}

	public UFBoolean getIssettlement() {
		return issettlement;
	}

	public void setIssettlement(UFBoolean issettlement) {
		this.issettlement = issettlement;
	}

	public UFBoolean getIsuse() {
		return isuse;
	}

	public void setIsuse(UFBoolean newIsuse) {
		isuse = newIsuse;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer newDr) {
		dr = newDr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime newTs) {
		ts = newTs;
	}

	public String getParentPKFieldName() {
		return null;
	}

	public String getPKFieldName() {
		return "pk_leavebalance";
	}

	public String getTableName() {
		return "tbm_leavebalance";
	}

	public static String getDefaultTableName() {
		return "tbm_leavebalance";
	}

	public UFLiteralDate getSettlementdate() {
		return settlementdate;
	}

	public void setSettlementdate(UFLiteralDate settlementdate) {
		this.settlementdate = settlementdate;
	}

	public String getPk_psnjob() {
		return pk_psnjob;
	}

	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	public UFDouble getFreezedayorhour() {
		return freezedayorhour;
	}

	public void setFreezedayorhour(UFDouble freezedayorhour) {
		this.freezedayorhour = freezedayorhour;
	}

	public void sync(LeaveBalanceVO anotherVO) {
		pk_leavebalance = anotherVO.getPk_leavebalance();
		curdayorhour = anotherVO.getCurdayorhour();
		realdayorhour = anotherVO.getRealdayorhour();
		freezedayorhour = anotherVO.getFreezedayorhour();
		yidayorhour = anotherVO.getYidayorhour();
		restdayorhour = anotherVO.getRestdayorhour();
		lastdayorhour = anotherVO.getLastdayorhour();
		issettlement = anotherVO.getIssettlement();
		isuse = anotherVO.getIsuse();
		isabnormalset = anotherVO.getIsabnormalset();
		settlementdate = anotherVO.getSettlementdate();
		calculatetime = anotherVO.getCalculatetime();
		settlementmethod = anotherVO.getSettlementmethod();
		ts = anotherVO.getTs();
	}

	public UFBoolean getIsabnormalset() {
		return isabnormalset;
	}

	public void setIsabnormalset(UFBoolean isabnormalset) {
		this.isabnormalset = isabnormalset;
	}

	public UFLiteralDate getHirebegindate() {
		return hirebegindate;
	}

	public void setHirebegindate(UFLiteralDate hirebegindate) {
		this.hirebegindate = hirebegindate;
	}

	public UFLiteralDate getHireenddate() {
		return hireenddate;
	}

	public void setHireenddate(UFLiteralDate hireenddate) {
		this.hireenddate = hireenddate;
	}

	public String getPk_psnorg() {
		return pk_psnorg;
	}

	public void setPk_psnorg(String pkPsnorg) {
		pk_psnorg = pkPsnorg;
	}

	public boolean isSettlement() {
		return (issettlement != null) && (issettlement.booleanValue());
	}

	public boolean isUse() {
		return (isuse != null) && (isuse.booleanValue());
	}

	public Integer getLeaveindex() {
		return leaveindex;
	}

	public void setLeaveindex(Integer leaveindex) {
		this.leaveindex = leaveindex;
	}

	public boolean canCalculate() {
		return (!isSettlement()) && (!isUse());
	}

	public Integer getSettlementmethod() {
		return settlementmethod;
	}

	public void setSettlementmethod(Integer settlementmethod) {
		this.settlementmethod = settlementmethod;
	}

	public TBMPsndocVO toTBMPsndocVO() {
		TBMPsndocVO vo = new TBMPsndocVO();
		vo.setPrimaryKey(pk_tbm_psndoc);
		vo.setPk_psndoc(pk_psndoc);
		vo.setPk_psnjob(pk_psnjob);
		vo.setPk_group(pk_group);
		vo.setPk_org(pk_org);
		return vo;
	}

	public String getPk_tbm_psndoc() {
		return pk_tbm_psndoc;
	}

	public void setPk_tbm_psndoc(String pkTbmPsndoc) {
		pk_tbm_psndoc = pkTbmPsndoc;
	}

	public String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(String pkOrgV) {
		pk_org_v = pkOrgV;
	}

	public String getPk_dept_v() {
		return pk_dept_v;
	}

	public void setPk_dept_v(String pkDeptV) {
		pk_dept_v = pkDeptV;
	}

	public UFLiteralDate getPeriodbegindate() {
		return periodbegindate;
	}

	public void setPeriodbegindate(UFLiteralDate periodbegindate) {
		this.periodbegindate = periodbegindate;
	}

	public UFLiteralDate getPeriodenddate() {
		return periodenddate;
	}

	public void setPeriodenddate(UFLiteralDate periodenddate) {
		this.periodenddate = periodenddate;
	}

	public UFLiteralDate getPeriodextendenddate() {
		return periodextendenddate;
	}

	public void setPeriodextendenddate(UFLiteralDate periodextendenddate) {
		this.periodextendenddate = periodextendenddate;
	}

	public int getLeavesetperiod() {
		return leavesetperiod;
	}

	public void setLeavesetperiod(int leavesetperiod) {
		this.leavesetperiod = leavesetperiod;
	}

	public boolean isYearDateSetPeriod() {
		return leavesetperiod != 0;
	}

	public String getPeriodStr() {
		return curyear + curmonth;
	}

	public UFDouble getTranslatein() {
		return translatein;
	}

	public void setTranslatein(UFDouble translatein) {
		this.translatein = translatein;
	}

	public UFDouble getTranslateout() {
		return translateout;
	}

	public void setTranslateout(UFDouble translateout) {
		this.translateout = translateout;
	}

	public UFBoolean getTransflag() {
		return transflag;
	}

	public void setTransflag(UFBoolean transflag) {
		this.transflag = transflag;
	}

	public boolean getTransFlagValue() {
		return (transflag != null) && (transflag.booleanValue());
	}

	public void setChangelength(UFDouble changelength) {
		this.changelength = changelength;
	}

	public UFDouble getChangelength() {
		return changelength;
	}

	public String getSalaryyear() {
		return salaryyear;
	}

	public void setSalaryyear(String salaryyear) {
		this.salaryyear = salaryyear;
	}

	public String getSalarymonth() {
		return salarymonth;
	}

	public void setSalarymonth(String salarymonth) {
		this.salarymonth = salarymonth;
	}
}
