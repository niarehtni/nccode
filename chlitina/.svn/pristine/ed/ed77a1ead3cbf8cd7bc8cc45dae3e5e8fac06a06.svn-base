package nc.vo.ta.timeitem;

import nc.hr.utils.ResHelper;
import nc.vo.ta.annotation.IDColumn;
import nc.vo.ta.annotation.Table;
import nc.vo.ta.basedoc.annotation.DefVOClassName;
import nc.vo.ta.basedoc.annotation.DefVOPkFieldName;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
@DefVOClassName(className="nc.vo.ta.timeitem.LeaveTypeVO")
@DefVOPkFieldName(fieldName="pk_timeitem")
@Table(tableName="tbm_timeitemcopy")
@IDColumn(idColumn="pk_timeitemcopy")
public class LeaveTypeCopyVO extends TimeItemCopyVO {

	private TimeItemVO[] dependVOs;

	public static final String DEPENDVOS = "dependVOs";
	@Override
	public TimeItemVO toDefVO() {
		LeaveTypeVO itemVO = new LeaveTypeVO();

		itemVO.setPk_timeitem(getPk_timeitem());
		itemVO.setPk_group(getPk_defgroup());
		itemVO.setPk_org(getPk_deforg());
		itemVO.setTimeitemname(getTimeitemname());
		itemVO.setTimeitemname2(getTimeitemname2());
		itemVO.setTimeitemname3(getTimeitemname3());
		itemVO.setTimeitemcode(getTimeitemcode());
		itemVO.setItemtype(getItemtype());
		itemVO.setIslactation(getIslactation());
		itemVO.setIspredef(getIspredef());
		itemVO.setStatus(getStatus());
		itemVO.setEnablestate(getDefenablestate());
		itemVO.setTs(getDefTS());
//		//add by ward 20180505 添加是否扣款、扣款费率字段 begin
//		itemVO.setIscharge(getIscharge());
//		itemVO.setRate(getRate());
//		//add by ward 20180505 添加是否扣款、扣款费率字段 end
		return itemVO;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null)
		    return false;
		if (!(obj instanceof LeaveTypeCopyVO))
			return false;
		LeaveTypeCopyVO vo = (LeaveTypeCopyVO) obj;
		// 通过主键判断
		if(StringUtils.isEmpty(pk_timeitemcopy)||StringUtils.isEmpty(vo.getPk_timeitemcopy())||!pk_timeitemcopy.equals(vo.getPk_timeitemcopy()))
			return false;
		return true;
	}

	public TimeItemVO[] getDependVOs() {
		return dependVOs;
	}

	public void setDependVOs(TimeItemVO[] dependVOs) {
		this.dependVOs = dependVOs;
	}

	/**
	 * 判断享有是否等于实际享有
	 * 如果是按期间结算，或者计算方式是按年计算，则二者相等
	 * @return
	 */
	public boolean isRealEqualsCur(){
		int settlementPeriod =getLeavesetperiod();
		if(settlementPeriod==LEAVESETPERIOD_MONTH)
			return true;
		int leaveScale = getLeavescale();
		return leaveScale==LEAVESCALE_YEAR;
	}

	public boolean isLactation(){
		return getIslactation()!=null&&getIslactation().booleanValue();
	}

	/**
	 * 得到有效期天数
	 * @return
	 */
	public int getExtendDaysCount(){
		if(getIsleave()==null||!getIsleave().booleanValue())
			return 0;
		return getLeaveextendcount().intValue();
	}

	/**
	 * 判断一个类别是否是按年||入职日结算
	 * 这种类别的期间只有年度，没有月
	 * @return
	 */
	public boolean isSetPeriodYearORDate(){
		int setPeriod = getLeavesetperiod()==null?LEAVESETPERIOD_YEAR:getLeavesetperiod().intValue();
		return setPeriod == LEAVESETPERIOD_YEAR
				|| setPeriod == LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
				|| setPeriod == LEAVESETPERIOD_STARTDATE;
		//
	}

	public static String leaveSetPeriodYearStr(){
		return ResHelper.getString("common","UC000-0001787")
/*@res "年"*/;
	}
	public static String leaveSetPeriodDateStr(){
		return ResHelper.getString("6017basedoc","06017basedoc1859")
/*@res "入职日期"*/;
	}
	public static String leaveSetPeriodMonthStr(){
		return ResHelper.getString("common","UC000-0002560")
/*@res "期间"*/;
	}

	public String getLeaveSetPeriodStr(){
		int settlementPeriod =getLeavesetperiod();
		// ssx added on 2018-03-16
		// for changes of start date of company age
		if (settlementPeriod == LEAVESETPERIOD_DATE
				|| settlementPeriod == LEAVESETPERIOD_STARTDATE)
			//
			return leaveSetPeriodDateStr();
		if(settlementPeriod==LEAVESETPERIOD_MONTH)
			return leaveSetPeriodMonthStr();
		return leaveSetPeriodYearStr();
	}
	
	//add by ward 20180505 添加是否扣款、扣款费率字段 begin
	private nc.vo.pub.lang.UFBoolean ischarge;
	private java.lang.Integer rate;
	public nc.vo.pub.lang.UFBoolean getIscharge() {
		return ischarge;
	}

	public void setIscharge(nc.vo.pub.lang.UFBoolean ischarge) {
		this.ischarge = ischarge;
	}

	public java.lang.Integer getRate() {
		return rate;
	}

	public void setRate(java.lang.Integer rate) {
		this.rate = rate;
	}
	//add by ward 20180505 添加是否扣款、扣款费率字段 end
}