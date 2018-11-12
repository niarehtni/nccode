package nc.impl.ta.leave;

import java.util.Map;
import java.util.TimeZone;

import nc.hr.utils.ArrayHelper;
import nc.itf.ta.algorithm.ITimeScope;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 用于中间计算的类，包装还未被处理的休假时间段，和已经被处理（拆单）的休假时间段
 * 处理的过程，是把toSplitVOs里的休假段一点点地往splitVOs里面挪，直至挪尽
 * 挪的大概过程是，按照往期前置假，往期本类别，晚期前置假，晚期本类别的结算记录依次处理
 * toSplitVOs中拆出来放到splitVOs中，如果把toSplitVOs拆完了就直接返回
 * toSplitVOs在往splitVOs中拆的过程中，有可能会将一个时间段拆成多个时间段，此时要保证拆之后的时间段：
 * 1.class仍然是原来的class
 * 2.pk_org,pk_psnjob,pk_psndoc,pk_leavetype等字段的值一个都不能少
 * 在拆单的过程中，toSplitVOs里面的时间段越来越少，splitVOs里面的时间段越来越多，直至toSplitVOs全部挪到splitVOs里面
 * 并且，要时时刻刻保证两个数组里面的时间段都是按先后顺序排列（但不能保证splitVOs里面的时间段都在toSplitVOs的时间段之前,因为每个
 * 类别的有效期延长等参数规定的不一样，有可能导致前置类别不能支持的时间段在后置类别上能支持）
 * @author zengcheng
 *
 * @param <T>
 */
class TemporarySplitResult<T extends LeaveCommonVO>{
	//原始的未处理的时间段(来源与一张申请单的子表，或者来源于一张登记单。对于批量新增的情形，是一条记录一条记录依次处理的,即每条批增的明细是一个splitresult)
	T[] originalToSplitVOs;
	//未处理的休假时间段，随着拆单的进行，逐渐地被挪到splitVOs中
	T[] toSplitVOs;
	//已被处理（拆单）休假时间段
	T[] splitVOs;
	//针对某一具体的leavebalance记录，未处理的休假时间段，随着拆单的进行，逐渐地被挪到splitVOsForOneBalanceVO中
	T[] toSplitVOsForOneBalanceVO;
	//针对某一具体的leavebalance记录，已被处理（拆单）休假时间段
	T[] splitVOsForOneBalanceVO;
	//用户填写的原始休假类别
	LeaveTypeCopyVO originalLeaveTypeCopyVO;
	TimeRuleVO timeRuleVO;//考勤规则
	Map<String, LeaveTypeCopyVO> leaveTypeVOMap;//所有的休假类别的map，key是Pk_timeitem
	LeaveBalanceVO[] balanceVOs;//此次拆单操作涉及到的所有leavebalance记录
	boolean canOverYear;//休假跨年是否可以保存
	UFLiteralDate applyDate;//如果是申请单的拆单，则要提供申请单的申请日期。不是申请单为null即可
	//balancevo与其有效时间范围的map，包括所有的balancevo，例如结算了的，等等
	Map<LeaveBalanceVO, ITimeScope> balanceVOEffectiveScopeMap;

	public T[] getToSplitVOs() {
		return toSplitVOs;
	}
	public void setToSplitVOs(T[] toSplitVOs) {
		this.toSplitVOs = toSplitVOs;
	}
	public T[] getSplitVOs() {
		return splitVOs;
	}
	public void setSplitVOs(T[] splitVOs) {
		this.splitVOs = splitVOs;
	}
	public T[] getOriginalToSplitVOs() {
		return originalToSplitVOs;
	}
	@SuppressWarnings("unchecked")
	public void setOriginalToSplitVOs(T[] originalToSplitVOs) {
		//originalToSplitVOs里面存储的是原始的未拆单的休假时段。在拆单的过程中，这些时段会被set来set去，拆来拆去，为了保留最原始的信息，因此要clone出来保存
		this.originalToSplitVOs = originalToSplitVOs;
		if(!ArrayUtils.isEmpty(originalToSplitVOs)){
			for(int i=0;i<originalToSplitVOs.length;i++){
				this.originalToSplitVOs[i]=(T) originalToSplitVOs[i].clone();
			}
		}
	}
	
	/**
	 * 是否真正地被拆单了。判断逻辑比较复杂，需要区分结余假是否优先两种场景
	 * @return
	 */
	public boolean isRealSplit(){
		//先简单比较，如果处理之后，单子的个数发生了变化，则肯定拆单了
		if(originalToSplitVOs.length!=ArrayUtils.getLength(splitVOs)){
			return true;
		}
		//若单子的个数没变，则不能肯定是否拆单了，需要综合休假类别以及期间来看
		boolean isPreHolidayFirst = timeRuleVO.isPreHolidayFirst();
		boolean isYearDateHoliday = getOriginalLeaveTypeCopyVO().isSetPeriodYearORDate();//是否是按年/入职日结算
		String pk_originalLeaveType = getOriginalLeaveTypeCopyVO().getPk_timeitem();//用户填写的原始的休假类别
		//如果结余假优先，则如果有了新的类别，或者【没有新类别，但有多个期间】，则被视为拆单了
		//如果结余假不优先，则分两种情况讨论：如果用户没有录入期间，则等同于结余假优先的规则
		//如果用户录入了期间，则如果有了新的类别，或者有了新的期间，则被视为拆单了
		boolean isInputPeriod = isYearDateHoliday?StringUtils.isNotEmpty(originalToSplitVOs[0].getLeaveyear()):(StringUtils.isNotEmpty(originalToSplitVOs[0].getLeaveyear())&&StringUtils.isNotEmpty(originalToSplitVOs[0].getLeavemonth()));
		if(isPreHolidayFirst||(!isPreHolidayFirst&&!isInputPeriod)){
			String tempPeriod = null;//期间
			for(T splitVO:splitVOs){
				if(!splitVO.getPk_leavetype().equals(pk_originalLeaveType))
					return true;


				String period = splitVO.getLeaveyear()+(isYearDateHoliday?"":splitVO.getLeavemonth());
				if(tempPeriod==null){
					tempPeriod = period;
					continue;
				}
				if(!tempPeriod.equals(period))
					return true;
			}
			return false;
		}
		//走到这里，肯定是结余假不优先，且用户录入了期间，这种情况下，有了新的类别，或者有了新的期间，都视为拆单了
		String fixPeriod = originalToSplitVOs[0].getLeaveyear()+(isYearDateHoliday?"":originalToSplitVOs[0].getLeavemonth());
		for(T splitVO:splitVOs){
			if(!splitVO.getPk_leavetype().equals(pk_originalLeaveType))
				return true;


			String period = splitVO.getLeaveyear()+(isYearDateHoliday?"":splitVO.getLeavemonth());
			if(!fixPeriod.equals(period))
				return true;
		}
		return false;
	}

	/**
	 * 是否真正地被拆单了。判断逻辑比较复杂，需要区分结余假是否优先两种场景
	 * @return
	 */
//	public boolean isRealSplit(){
//		boolean isCanOverYear = timeRuleVO.isCanSaveOveryear();// 跨期间单据是否允许保存，如果允许则不算拆单
//		//先简单比较，如果处理之后，单子的个数发生了变化，且跨期间单据不允许保存，则肯定拆单了
//		if(!isCanOverYear && originalToSplitVOs.length!=ArrayUtils.getLength(splitVOs))
//			return true;
//		//若单子的个数没变，则不能肯定是否拆单了，需要综合休假类别以及期间来看
//		boolean isPreHolidayFirst = timeRuleVO.isPreHolidayFirst();
//		boolean isYearDateHoliday = getOriginalLeaveTypeCopyVO().isSetPeriodYearORDate();//是否是按年/入职日结算
//		String pk_originalLeaveType = getOriginalLeaveTypeCopyVO().getPk_timeitem();//用户填写的原始的休假类别
//		//如果结余假优先，则如果有了新的类别，或者【没有新类别，但有多个期间】，则被视为拆单了
//		//如果结余假不优先，则分两种情况讨论：如果用户没有录入期间，则等同于结余假优先的规则
//		//如果用户录入了期间，则如果有了新的类别，或者有了新的期间，则被视为拆单了
//		boolean isInputPeriod = isYearDateHoliday?StringUtils.isNotEmpty(originalToSplitVOs[0].getLeaveyear()):(StringUtils.isNotEmpty(originalToSplitVOs[0].getLeaveyear())&&StringUtils.isNotEmpty(originalToSplitVOs[0].getLeavemonth()));
//		if(isPreHolidayFirst||(!isPreHolidayFirst&&!isInputPeriod)){
//			String tempPeriod = null;//期间
//			for(T splitVO:splitVOs){
//				if(!splitVO.getPk_leavetype().equals(pk_originalLeaveType))
//					return true;
//				if(isCanOverYear) //如果允许跨期间，则不需要校验期间
//					continue;
//				String period = splitVO.getLeaveyear()+(isYearDateHoliday?"":splitVO.getLeavemonth());
//				if(tempPeriod==null){
//					tempPeriod = period;
//					continue;
//				}
//				if(!tempPeriod.equals(period))
//					return true;
//			}
//			return false;
//		}
//		//走到这里，肯定是结余假不优先，且用户录入了期间，这种情况下，有了新的类别，或者有了新的期间，都视为拆单了
//		String fixPeriod = originalToSplitVOs[0].getLeaveyear()+(isYearDateHoliday?"":originalToSplitVOs[0].getLeavemonth());
//		for(T splitVO:splitVOs){
//			if(!splitVO.getPk_leavetype().equals(pk_originalLeaveType))
//				return true;
//			if(isCanOverYear) //如果允许跨期间，则不需要校验期间
//				continue;
//			String period = splitVO.getLeaveyear()+(isYearDateHoliday?"":splitVO.getLeavemonth());
//			if(!fixPeriod.equals(period))
//				return true;
//		}
//		return false;
//	}
	public LeaveTypeCopyVO getOriginalLeaveTypeCopyVO() {
		return originalLeaveTypeCopyVO;
	}
	public void setOriginalLeaveTypeCopyVO(LeaveTypeCopyVO originalLeaveTypeCopyVO) {
		this.originalLeaveTypeCopyVO = originalLeaveTypeCopyVO;
	}
	public TimeRuleVO getTimeRuleVO() {
		return timeRuleVO;
	}
	public void setTimeRuleVO(TimeRuleVO timeRuleVO) {
		this.timeRuleVO = timeRuleVO;
	}
	public boolean isCanOverYear() {
		return canOverYear;
	}
	public void setCanOverYear(boolean canOverYear) {
		this.canOverYear = canOverYear;
	}
	public Map<String, LeaveTypeCopyVO> getLeaveTypeVOMap() {
		return leaveTypeVOMap;
	}
	public void setLeaveTypeVOMap(Map<String, LeaveTypeCopyVO> leaveTypeVOMap) {
		this.leaveTypeVOMap = leaveTypeVOMap;
	}
	public LeaveBalanceVO[] getBalanceVOs() {
		return balanceVOs;
	}
	public void setBalanceVOs(LeaveBalanceVO[] balancedVOs) {
		this.balanceVOs = balancedVOs;
	}
	public UFLiteralDate getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(UFLiteralDate applyDate) {
		this.applyDate = applyDate;
	}
	public T[] getToSplitVOsForOneBalanceVO() {
		return toSplitVOsForOneBalanceVO;
	}
	public void setToSplitVOsForOneBalanceVO(T[] toSplitVOsForOneBalanceVO) {
		this.toSplitVOsForOneBalanceVO = toSplitVOsForOneBalanceVO;
	}
	public T[] getSplitVOsForOneBalanceVO() {
		return splitVOsForOneBalanceVO;
	}
	public void setSplitVOsForOneBalanceVO(T[] splitVOsForOneBalanceVO) {
		this.splitVOsForOneBalanceVO = splitVOsForOneBalanceVO;
	}
	public Map<LeaveBalanceVO, ITimeScope> getBalanceVOEffectiveScopeMap() {
		return balanceVOEffectiveScopeMap;
	}
	public void setBalanceVOEffectiveScopeMap(
			Map<LeaveBalanceVO, ITimeScope> balanceVOEffectiveScopeMap) {
		this.balanceVOEffectiveScopeMap = balanceVOEffectiveScopeMap;
	}
	
	/**
	 * 将休假时段从toSplitVOs里面抠出来，放到splitVOs里面
	 */
	@SuppressWarnings("unchecked")
	public void splitScopesFromToSplitVOs(T[] vos,TimeZone timeZone){
		Class<T> clz = null;
		if(!ArrayUtils.isEmpty(getToSplitVOs()))
			clz = (Class<T>) getToSplitVOs().getClass().getComponentType();
		else
			clz = (Class<T>) getSplitVOs().getClass().getComponentType();
		setSplitVOs((T[]) ArrayHelper.addAll(getSplitVOs(), vos, clz));
		setToSplitVOs(SplitLeaveBillHelper.minus(getToSplitVOs(), vos, timeZone));
	}
}
