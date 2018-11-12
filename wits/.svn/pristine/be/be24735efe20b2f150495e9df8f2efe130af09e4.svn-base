package nc.itf.ta.algorithm;

import static nc.vo.ta.bill.BillMutexRule.BILL_LEAVE;
import static nc.vo.ta.bill.BillMutexRule.BILL_OVERTIME;
import static nc.vo.ta.bill.BillMutexRule.containsBillType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.itf.bd.shift.ShiftMaintainFacade;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.algorithm.TimeLengthWrapper;
import nc.vo.ta.away.AggAwayVO;
import nc.vo.ta.away.AwaybVO;
import nc.vo.ta.awayoff.AggAwayOffVO;
import nc.vo.ta.awayoff.AwayOffVO;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.bill.IDateScopeBillBodyVO;
import nc.vo.ta.changeshift.AggChangeShiftVO;
import nc.vo.ta.changeshift.ChangeShiftbVO;
import nc.vo.ta.daystat.DayStatbVO;
import nc.vo.ta.daystat.DaystatbNotCurrmonthVO;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author zengcheng
 * 对单据进行处理的帮助类，主要用于计算时长
 * 注意，计算单据时长时，如果碰到了弹性班，要先将弹性班进行固化。此类不负责固化，也就是说，在调用此类的方法之前，必须保证
 * 弹性班已经被固化
 */
public class BillProcessHelper {
	
	
	/**
	 * 处理所有单据的交切
	 * @return
	 */
	public static ITimeScopeWithBillType[] crossAllBills(ITimeScopeWithBillType[]... bills){
		ITimeScopeWithBillType[] bigArray = mergeBillArray(bills);
		if(bigArray==null||bigArray.length==0)
			return bigArray;
		return (ITimeScopeWithBillType[]) TimeScopeUtils.crossTimeScopes(bigArray);
	}
	/**
	 * 处理所有单据的交切
	 * @return
	 */
	public static ITimeScopeWithBillType[] crossAllBills(List<? extends ITimeScopeWithBillType>... bills){
		List<ITimeScopeWithBillType> bigList = mergeBillList(bills);
		if(bigList==null||bigList.size()==0)
			return null;
		return (ITimeScopeWithBillType[]) TimeScopeUtils.crossTimeScopes(bigList.toArray(new ITimeScopeWithBillType[0]));
	}
	
	/**
	 * 将所有单据的时间段并起来
	 * @return
	 */
	public static ITimeScope[] mergeAllBills(ITimeScopeWithBillType[]... bills){
		ITimeScopeWithBillType[] bigArray = mergeBillArray(bills);
		if(bigArray==null||bigArray.length==0)
			return bigArray;
		return TimeScopeUtils.mergeTimeScopes(bigArray);
	}
	
	/**
	 * 将所有单据的数组合并成一个大数组
	 * @return
	 */
	public static ITimeScopeWithBillType[] mergeBillArray(ITimeScopeWithBillType[]... bills){
		int argCount = bills==null?0:bills.length;
		if(argCount==0)
			return null;
		List<ITimeScopeWithBillType> retList = new ArrayList<ITimeScopeWithBillType>();
		for(ITimeScopeWithBillType[] scopes:bills){
			if(scopes==null||scopes.length==0)
				continue;
			retList.addAll(Arrays.asList(scopes));
		}
		return retList.toArray(new ITimeScopeWithBillType[0]);
	}
	
	/**
	 * 将所有单据的数组合并成一个大数组
	 * @param bills
	 * @return
	 */
	public static List<ITimeScopeWithBillType> mergeBillList(List<? extends ITimeScopeWithBillType>... bills){
		int argCount = bills==null?0:bills.length;
		if(argCount==0)
			return null;
		List<ITimeScopeWithBillType> retList = new ArrayList<ITimeScopeWithBillType>();
		for(List<? extends ITimeScopeWithBillType> scopes:bills){
			if(scopes==null||scopes.size()==0)
				continue;
			retList.addAll(scopes);
		}
		return retList;
	}
	
	/**
	 * 计算日报时，处理某日的休假类别的时长。传进来的单据已经是根据冲突规则过滤了的单据
	 * workDayLength是工作日时长，在考勤规则里面定义，一个工作日等于几个小时
	 * @param timeItems
	 * @param billList
	 * @param curCalendar
	 * @param preShift
	 * @param nextCalendar
	 * @param statbVOList
	 */
	public static void processLeaveLength(TimeItemCopyVO[] timeItems,List<ITimeScopeWithBillType> billList,
			AggPsnCalendar curCalendar,
			ShiftVO preShift,ShiftVO curShift,ShiftVO nextShift,
			TimeZone preTimeZone,TimeZone curTimeZone,TimeZone nextTimeZone,
			List<DayStatbVO> statbVOList,Map<String, String[]> datePeriodMap,Map<String, Object> paraMap,
			String pk_daystat,TimeRuleVO timeRuleVO){
		if(timeItems==null){//没有类别 自然也不用计算休假
			return;
		}
		//如果没有休假单，则不用处理（也有一种可能，那就是有休假单，但是和其他单据有时间冲突，且按照冲突规则的定义，这种冲突不算休假）
		if(billList==null||billList.size()==0)
			return;
		if(curCalendar==null)
			return;
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		//如果此天没有工作日历，或者是不考勤的，则休假时间为0
//		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift()))
		//公休日可能会有记为休假的情况 例如产假、婚假等
		if(psncalendarVO==null)
			return;
		//将休假的时间段与工作时间段相交
		ITimeScope[] workScopes = getWorkTimeScopes(psncalendarVO.getCalendar().toString(), 
				curCalendar, preShift, nextShift,
				curTimeZone,preTimeZone,nextTimeZone);
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList.toArray(new ITimeScopeWithBillType[0]), workScopes,new ITimeScopeWithBillType[0]);
		//如果没有相交的部分则不用处理
		if(intersectionScopes==null||intersectionScopes.length==0)
			return;
		//取出参数：单据跨期间时，是记在各个期间，还是第一个期间记在第二个期间,0-分别记，1-第一记在第二
		//由于出差和休假都有可能调用此方法，因此两种情况取不同的参数
		Integer periodPara = null;
		if(timeItems[0] instanceof LeaveTypeCopyVO)
//			periodPara = (Integer)paraMap.get(LeaveConst.OVERPERIOD_PARAM);
			periodPara = timeRuleVO.getLevaeovperprtype();
		else
//			periodPara = (Integer)paraMap.get(AwayConst.PARA_AWAY_OVERPERIOD);
			periodPara = timeRuleVO.getAwayovperprtype();
		if(periodPara==null)//默认分别记
			periodPara=0;
		for(TimeItemCopyVO item:timeItems){
			//这种休假类别这天的休假时长
			TimeLengthWrapper result = calLeaveLength(item,intersectionScopes,curCalendar,curShift,timeRuleVO);
			//处理期间
			String calcPeirod = null;
			if(periodPara==1){//如果是第一期间记在第二期间
				calcPeirod = queryCalcPeriod(curCalendar.getPsnCalendarVO().getCalendar().toString(),intersectionScopes,datePeriodMap,item.getPk_timeitem());
			}
			createDaystatbVO(pk_daystat,item.getPk_timeitem(), result.originalLength,result.processedLength,
					result.originalLengthUseHour,result.processedLengthUseHour,result.toRestHour,
					statbVOList,1,calcPeirod,item.getTimeItemUnit());
		}
	}
	
	
	
	public static void processLeaveLengthApproveCurrentMonth(TimeItemCopyVO[] timeItems,ITimeScopeWithBillType[] billList,
			AggPsnCalendar curCalendar,
			ShiftVO preShift,ShiftVO curShift,ShiftVO nextShift,
			TimeZone preTimeZone,TimeZone curTimeZone,TimeZone nextTimeZone,
			List<DaystatbNotCurrmonthVO> statbVOList,Map<String, String[]> datePeriodMap,Map<String, Object> paraMap,
			String pk_daystat,TimeRuleVO timeRuleVO, LeaveRegVO vo, UFLiteralDate approve_date, UFLiteralDate calendar){
		if(timeItems==null){//没有类别 自然也不用计算休假
			return;
		}
		//如果没有休假单，则不用处理（也有一种可能，那就是有休假单，但是和其他单据有时间冲突，且按照冲突规则的定义，这种冲突不算休假）
		if(billList==null||billList.length==0)
			return;
		if(curCalendar==null)
			return;
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		//如果此天没有工作日历，或者是不考勤的，则休假时间为0
//		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift()))
		//公休日可能会有记为休假的情况 例如产假、婚假等
		if(psncalendarVO==null)
			return;
		//将休假的时间段与工作时间段相交
		ITimeScope[] workScopes = getWorkTimeScopes(psncalendarVO.getCalendar().toString(), 
				curCalendar, preShift, nextShift,
				curTimeZone,preTimeZone,nextTimeZone);
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList, workScopes,new ITimeScopeWithBillType[0]);
		//如果没有相交的部分则不用处理
		if(intersectionScopes==null||intersectionScopes.length==0)
			return;
		//取出参数：单据跨期间时，是记在各个期间，还是第一个期间记在第二个期间,0-分别记，1-第一记在第二
		//由于出差和休假都有可能调用此方法，因此两种情况取不同的参数
		Integer periodPara = null;
		if(timeItems[0] instanceof LeaveTypeCopyVO)
			periodPara = timeRuleVO.getLevaeovperprtype();
		
		if(periodPara==null)//默认分别记
			periodPara=0;
		for(TimeItemCopyVO item:timeItems){
			if(!item.getPk_timeitem().equals(vo.getPk_leavetype()))
			{
				continue;
			}
			//这种休假类别这天的休假时长
			TimeLengthWrapper result = calLeaveLength(item,intersectionScopes,curCalendar,curShift,timeRuleVO);
			//处理期间
			String calcPeirod = null;
			if(periodPara==1){//如果是第一期间记在第二期间
				calcPeirod = queryCalcPeriod(curCalendar.getPsnCalendarVO().getCalendar().toString(),intersectionScopes,datePeriodMap,item.getPk_timeitem());
			}
			if(result.processedLength>0){
				createDaystatbNotCurrentMonthVO(pk_daystat,item.getPk_timeitem(), result.originalLength,result.processedLength,
						result.originalLengthUseHour,result.processedLengthUseHour,result.toRestHour,
						statbVOList,1,calcPeirod,item.getTimeItemUnit(),vo, approve_date, calendar);
			}
		}
	}
	
	private static void createDaystatbNotCurrentMonthVO(String pk_daystat,String pk_timeitem,
			double originalVal,double processedVal,
			double originalValUseHour,double processedValUseHour,
			double toRestHour, List<DaystatbNotCurrmonthVO> statbNotCurrMonthVOList,
			int type,String calPeriod,int timeitemUnit, LeaveRegVO leaveRegVO, UFLiteralDate approve_date, UFLiteralDate calendar){
		if(Math.abs(originalVal-0)>0.0001||Math.abs(processedVal-0)>0.0001){
			DaystatbNotCurrmonthVO vo = new DaystatbNotCurrmonthVO();
			vo.setDr(Integer.valueOf(0));
			vo.setApprove_date(approve_date);
			vo.setHournum(new UFDouble(processedVal));
			vo.setCalendar(calendar);
			vo.setPk_billsource(leaveRegVO.getPk_leavereg());
			vo.setPk_timeitem(pk_timeitem);
			vo.setType(0);
			vo.setTimeitemunit(timeitemUnit);
			vo.setPk_daystat(pk_daystat);
			vo.setToresthour(new UFDouble(toRestHour));
			statbNotCurrMonthVOList.add(vo);
		}
	}
	
	private static String queryCalcPeriod(String curDate,ITimeScopeWithBillType[] intersectionScopes,Map<String, String[]> datePeriodMap,String pk_timeitem){
		String calcPeirod = null;
		for(ITimeScopeWithBillType bill:intersectionScopes){
			IDateScopeBillBodyVO originalBill = (IDateScopeBillBodyVO) bill.getOriginalTimeScopeMap().get(pk_timeitem);
			if(originalBill==null)
				continue;
			UFLiteralDate beginDate = originalBill.getBegindate();
			String[] beginPeriods = datePeriodMap.get(beginDate.toString());
			String beginPeriod = beginPeriods==null?null:beginPeriods[0];
			UFLiteralDate endDate = originalBill.getEnddate();
			String[] endPeriods = datePeriodMap.get(endDate.toString());
			String endPeriod = endPeriods==null?null:endPeriods[0];
			//开始日和结束日无期间，或者开始日和结束日在同一个期间，continue
			if(beginPeriod==null||endPeriod==null||beginPeriod.equals(endPeriod))
				continue;
			//开始日和结束日不在同一个期间，则要处理
			//如果当前日和结束日属于同一个期间，则continue
			String[] curDatePeriods = datePeriodMap.get(curDate);
			if(curDatePeriods==null||curDatePeriods[0]==null||endPeriod.equals(curDatePeriods[0]))
				continue;
			//如果当前日和结束日不属于同一个期间，则返回当前日所属期间的下一个期间
			calcPeirod = curDatePeriods[1];
		}
		return calcPeirod;
	}
	
	/**
	 * 计算某个休假类别的日报时长，curCalendar是当前计算日的班次，lastCalendar是计算日前一日的班次，nextCalendar是计算日后一日的班次
	 * 返回的是小时数或者天数，可以直接写入数据库，不用做另外的转换
	 * @param leaveItem
	 * @param processedBills,所有的加班单、出差单、休假单、停工单经过交切之后的结果,已经与工作时间段交过
	 * @param curCalendar
	 * @param lastCalendar
	 * @param nextCalendar
	 * @return
	 */
	public static TimeLengthWrapper calLeaveLength(TimeItemCopyVO leaveItem,ITimeScopeWithBillType[] intersectionScopes,
			AggPsnCalendar curCalendar,ShiftVO curShift,TimeRuleVO timeRuleVO){
		//added by zengcheng 2009.01.08,对于没有排班的情况，暂时处理为不计休假，返回0即可
		if(curCalendar==null)
			return TimeLengthWrapper.getZeroTimeLength();
		int gxComType = leaveItem.getGxcomtype()==null?0:leaveItem.getGxcomtype().intValue();//0公休不计，1公休计
		//如果是公休且公休不计休假则直接返回0
		boolean isGx = ShiftVO.PK_GX.equals(curCalendar.getPsnCalendarVO().getPk_shift());
		if(isGx&&gxComType==0)
			return TimeLengthWrapper.getZeroTimeLength();
		String itemPk = leaveItem.getPk_timeitem();//项目主键
		//过滤出这种类型（比如事假）的时间段
		ITimeScopeWithBillType[] filteredScopes = TimeScopeUtils.filterByItemPK(itemPk, intersectionScopes);
		if(filteredScopes==null||filteredScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		int timeitemUnit = leaveItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:leaveItem.getTimeitemunit().intValue();//2是按小时计算，1是按天
		//如果是按小时计算，则很简单，直接把秒数除以3600即可（对于公休做了限制，公休的休假时长不能超过考勤规则定义的工作日时长）
		//最终的休假时长等于休假时间段与工作时间段的交集的时长减去休息时长
		long seconds = TimeScopeUtils.getLength(filteredScopes);
		if(seconds==0)
			return TimeLengthWrapper.getZeroTimeLength();
		TimeLengthWrapper length = new TimeLengthWrapper();
		//最小时间单位-对于以小时计的加班类别，单位是分钟；对于以天计的加班类别，单位是天
		double timeUnit = leaveItem.getTimeunit()==null?0:leaveItem.getTimeunit().doubleValue();
		double workDayLength = timeRuleVO.getDaytohour2();
		//取整规则，0向上，1向下，2四舍五入，默认1；
		int roundMode = leaveItem.getRoundmode()==null?1:leaveItem.getRoundmode().intValue();
		//按小时计算
		if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
			double hours =  ((double)seconds)/ITimeScope.SECONDS_PER_HOUR;
			//如果是公休，则时长不能超过考勤规则的一个工作日时长
			length.originalLength = isGx?Math.min(hours, workDayLength):hours;
			hours = CommonMethods.processByDecimalDigitsAndRoundMode(CommonMethods.processByMinLengthAndRoundMode4HourUnit(timeUnit, roundMode, hours),timeRuleVO).doubleValue();
			//如果是公休，则时长不能超过考勤规则的一个工作日时长
			if(isGx)
				hours = Math.min(hours, workDayLength);
			length.processedLength=hours;
			length.originalLengthUseHour=length.originalLength;
			length.processedLengthUseHour=length.processedLength;
			return length;
		}
		//如果是按天计算，则比较复杂
		//看折算方式，如果是按工作日折算，则小时数要除以考勤规则定义的工作日时长。如果是按班次折算，则要除以班次时长
		int convertRule = leaveItem.getConvertrule()==null?TimeItemCopyVO.CONVERTRULE_TIME:leaveItem.getConvertrule().intValue();//折算方式
		double divLength = 0;//用来做除数的时长
		//如果是公休，或者折算方式是按工作日折算
		if(curCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX)||convertRule==TimeItemCopyVO.CONVERTRULE_DAY)
			divLength = workDayLength;
		//如果是按班次时长折算.注意，是除以shift表的时长，而不是除以psncalendar表的时长。比如三八妇女节这天的班次，如果请假了，那么只能算半天假，而不能算是一天假
		else
			divLength = curShift.getGzsj().doubleValue();
		double days = seconds/(ITimeScope.SECONDS_PER_HOUR*divLength);
		//如果是公休，则时长不能超过一天
		if(isGx)
			days = Math.min(days, 1);
		if(roundMode==0||roundMode==1){
			DecimalFormat dcmFmt = new DecimalFormat("0.0000");
			days = new UFDouble(dcmFmt.format(days)).doubleValue();//设置小数位数
		}
		length.originalLength = days;
		length.originalLengthUseHour=length.originalLength*divLength;
		days = CommonMethods.processByDecimalDigitsAndRoundMode(CommonMethods.processByMinLengthAndRoundMode4DayUnit(timeUnit, roundMode, days),timeRuleVO).doubleValue();
		if(isGx)
			days = Math.min(days, 1);
		length.processedLength=days;
		length.processedLengthUseHour=length.processedLength*divLength;
		return length;
	}
	
	
	
	/**
	 * 计算某个停工待料类别的日报时长，curCalendar是当前计算日的班次
	 * 返回的是小时数或者天数，可以直接写入数据库，不用做另外的转换
	 * @param shutdownItem
	 * @param processedBills,所有的加班单、出差单、休假单、停工单经过交切之后的结果
	 * @param curCalendar
	 * @return
	 */
	public static TimeLengthWrapper calShutdownLength(TimeItemCopyVO shutdownItem,ITimeScopeWithBillType[] intersectionScopes,
			AggPsnCalendar curCalendar,TimeRuleVO timeRuleVO){
		//过滤出这种类型的时间段
		ITimeScopeWithBillType[] filteredScopes = TimeScopeUtils.filterByItemPK(shutdownItem.getPk_timeitem(), intersectionScopes);
		if(filteredScopes==null||filteredScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		if(curCalendar==null)
			return TimeLengthWrapper.getZeroTimeLength();
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		PsnWorkTimeVO[] workVOs = curCalendar.getPsnWorkTimeVO();
		//如果此天没有工作日历，或者是公休或者不考勤的，或者没有工作时间段，则停工待料时长为0
		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift())||ArrayUtils.isEmpty(workVOs))
			return TimeLengthWrapper.getZeroTimeLength(); 
		//算出停工单与工作时间段的交集
		intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(intersectionScopes, workVOs,new ITimeScopeWithBillType[0]);
		//如果没有交集时间段，则不用处理
		if(intersectionScopes==null||intersectionScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		double result = (((double)(TimeScopeUtils.getLength(intersectionScopes))))/ITimeScope.SECONDS_PER_HOUR;
		return new TimeLengthWrapper(result,CommonMethods.processByDecimalDigitsAndRoundMode(result,timeRuleVO).doubleValue());//对于停工待料来说，原始时长和最后的时长是一样的
	}
	
//	/**
//	 * 根据工作时间段的信息，和已经与工作时间段进行交集处理的时间段，算出需要从交集时间段的时间长度中扣除的休息时长
//	 * 5.5在工作时间段中引入了休息时长的概念，而休假和停工的时长计算都需要和工作时间段交，所以休假和停工的计算都要
//	 * 考虑休息时长
//	 * zengcheng 2008.09.27增加注释：与需求讨论后，休息时长被去掉，因为会引起很多计算上的错误
//	 * @param curCalendar
//	 * @param intersectionScopes
//	 * @return 需要在后面减去的休息时长，单位为秒
//	 */
//	private static long getRestTimeToMinus(PsncalendarAllVO curCalendar,ITimeScopeWithBillType[] intersectionScopes){
//		long restTimeToMinus = 0;//需要在后面减去的休息时长，单位为秒。tbm_wt表的wtresttime字段存储的也是秒
//		if(curCalendar==null)
//			return restTimeToMinus;
//		Vector<PsncalendarbVO> psncalendarbVOsVec = curCalendar.getPsncalendarbVOs();
//		if(psncalendarbVOsVec!=null&&psncalendarbVOsVec.size()>0){
//			for(PsncalendarbVO calendarbVO:psncalendarbVOsVec){
//				//此工作时段的休息时长,数据库中存储的是分钟，而本方法是以秒计算的，所以要乘以60
//				long wtresttime = calendarbVO.getWtresttime()==null?0:calendarbVO.getWtresttime().intValue()*ITimeScope.SECONDS_PER_MINUTE;
//				//如果休息时长为0，则不用处理
//				if(wtresttime==0)
//					continue;
//				//如果休息时长大于0，则在后面需要减去min(wtresttime,与此时段的交集时长)
//				restTimeToMinus+=Math.min(wtresttime, TimeScopeUtils.getLength(TimeScopeUtils.intersectionTimeScopes(intersectionScopes, new ITimeScope[]{calendarbVO.toTimeScope(curCalendar.getPsncalendarVO().getCalendar().toString())})));
//			}
//		}
//		return restTimeToMinus;
//	}
	/**
	 * 计算某个加班类别的加班时长，belongtoCurDateBillMap是归属于某一天的加班单的map，key是加班单，value是此加班单与其他单据交切后的结果数组
	 * @param overtimeItem
	 * @param belongtoCurDateBillMap
	 * @param isMinusRestLength，是否扣除转调休的时长
	 * @return
	 */
	public static TimeLengthWrapper calOvertimeLength(TimeItemCopyVO overtimeItem,Map<OvertimeCommonVO, 
			List<ITimeScopeWithBillType>> belongtoCurDateBillMap,TimeRuleVO timeRuleVO,boolean isMinusRestLength){
		TimeLengthWrapper result= new TimeLengthWrapper();
		for(OvertimeCommonVO vo:belongtoCurDateBillMap.keySet()){
			if(!overtimeItem.getPk_timeitem().equals(vo.getPk_overtimetype()))
				continue;
			TimeLengthWrapper tempResult=calOvertimeLength(overtimeItem, vo.getDeduct().doubleValue(), belongtoCurDateBillMap.get(vo),timeRuleVO);
			//暂时处理为对于原始时长，不考虑转调休时长。对于处理时长要扣除转调休时长
			result.originalLength+=tempResult.originalLength;
			result.originalLengthUseHour+=tempResult.originalLengthUseHour;
			double toRestHour  = 0;
			if(vo instanceof OvertimeRegVO){
				OvertimeRegVO regVO = (OvertimeRegVO)vo;
				toRestHour = regVO.getToresthour()==null?0:regVO.getToresthour().doubleValue();
			}
			result.toRestHour+=toRestHour;
			if(isMinusRestLength){
				double len = tempResult.processedLength-toRestHour;
				if(len<0)
					len=0;
				result.processedLength+=len;
				if(overtimeItem.getTimeItemUnit()==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
					result.processedLengthUseHour=result.processedLength;
				}
				else{
					result.processedLengthUseHour=result.processedLength*timeRuleVO.getDaytohour2();
				}
			}
			else{
				result.processedLength+=tempResult.processedLength;
				result.processedLengthUseHour=tempResult.processedLengthUseHour;
			}
		}
		return result;
	}
	public static TimeLengthWrapper calOvertimeLength(TimeItemCopyVO overtimeItem,Map<OvertimeCommonVO, 
			List<ITimeScopeWithBillType>> belongtoCurDateBillMap,TimeRuleVO timeRuleVO){
		return calOvertimeLength(overtimeItem, belongtoCurDateBillMap, timeRuleVO, true);
	}
	/**
	 * 计算某个加班单的时长。billList是已经与其他单据交切过，并且减去了工作时间段的单据
	 * 此计算不扣除已转调休的时长，只扣除扣除时间
	 * @param overtimeItem
	 * @param deduct
	 * @param billList
	 * @param workDayLength
	 * @return
	 */
	public static TimeLengthWrapper calOvertimeLength(TimeItemCopyVO overtimeItem,double deduct, 
			List<ITimeScopeWithBillType> billList,TimeRuleVO timeRuleVO){
		if(billList==null||billList.size()==0)
			return TimeLengthWrapper.getZeroTimeLength();
		//1-天，2-小时,默认小时
		int timeitemUnit = overtimeItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:overtimeItem.getTimeitemunit().intValue();
		//最小时间单位-对于以小时计的加班类别，单位是分钟；对于以天计的加班类别，单位是天
		double timeUnit = overtimeItem.getTimeunit()==null?0:overtimeItem.getTimeunit().doubleValue();
		//取整规则，0向上，1向下，2四舍五入，默认1；
		int roundMode = overtimeItem.getRoundmode()==null?TimeItemCopyVO.ROUNDMODE_DOWN:overtimeItem.getRoundmode().intValue();
		long seconds = TimeScopeUtils.getLength(billList.toArray(new ITimeScopeWithBillType[0]));
		if(seconds==0)
			return TimeLengthWrapper.getZeroTimeLength();
		//减去扣除时间
		seconds = seconds-(int)(deduct*ITimeScope.SECONDS_PER_MINUTE);
		//如果减去扣除时间后小于等于0，则直接返回0
		if(seconds<=0)
			return TimeLengthWrapper.getZeroTimeLength();
		//如果是按小时计算:
		if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
			double oriresult = ((double)seconds)/ITimeScope.SECONDS_PER_HOUR;
			double processedResult = CommonMethods.processByDecimalDigitsAndRoundMode(CommonMethods.processByMinLengthAndRoundMode4HourUnit(timeUnit, roundMode, oriresult),timeRuleVO).doubleValue();
			return new TimeLengthWrapper(oriresult,processedResult,oriresult,processedResult);
		}
		//如果是按天计算
		double oriresult = seconds/(ITimeScope.SECONDS_PER_HOUR*timeRuleVO.getDaytohour2());
		double processedResult =CommonMethods.processByDecimalDigitsAndRoundMode(CommonMethods.processByMinLengthAndRoundMode4DayUnit(timeUnit, roundMode, oriresult),timeRuleVO).doubleValue();
		return new TimeLengthWrapper(oriresult,processedResult,oriresult*timeRuleVO.getDaytohour2(),processedResult*timeRuleVO.getDaytohour2());
	}
	/**
	 * 处理某人某天的停工单的时长。如果算得的时长大于0，则new一个日报子表vo，加入到list中
	 * @param timeItems
	 * @param billList，与其他单据交切过，但还没有与工作时间段交过的停工单
	 * @param curCalendar
	 * @param pk_daystat
	 * @param statbVOList
	 */
	public static void processShutdownLength(TimeItemCopyVO[] timeItems,List<ITimeScopeWithBillType> billList,AggPsnCalendar curCalendar,
			List<DayStatbVO> statbVOList,String pk_daystat,TimeRuleVO timeRuleVO){
		//如果没有停工单，则不用处理（也有一种可能，那就是有停工单，但是和其他单据有时间冲突，且按照冲突规则的定义，这种冲突不算停工）
		if(billList==null||billList.size()==0)
			return;
		if(curCalendar==null)
			return;
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		PsnWorkTimeVO[] workVOs = curCalendar.getPsnWorkTimeVO();
		//如果此天没有工作日历，或者是公休或者不考勤的，或者没有工作时间段，则停工待料时长为0
		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift())||ArrayUtils.isEmpty(workVOs))
			return; 
		//算出停工单与工作时间段的交集
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList.toArray(new ITimeScopeWithBillType[0]), workVOs,new ITimeScopeWithBillType[0]);
		//如果没有交集时间段，则不用处理
		if(intersectionScopes==null||intersectionScopes.length==0)
			return;
		for(TimeItemCopyVO item:timeItems){
			TimeLengthWrapper result = calShutdownLength(item,intersectionScopes,curCalendar,timeRuleVO);
			createDaystatbVO(pk_daystat,item.getPk_timeitem(), result.originalLength, result.processedLength,
					result.originalLengthUseHour,result.processedLengthUseHour,result.toRestHour,
					statbVOList,8,null,item.getTimeItemUnit());
		}
	}
	
	
	/**
	 * 处理某天的加班单的时长
	 * 加班单的时长处理与其他单据不一样。其他单据使用切的方式，也就是说，一张单据如果跨了几天的话，时长是分散在各天的
	 * 而加班单是归的方式：一张加班单的时长，是全部算在这张单据所属的工作日上的，不分摊到每天。
	 * 那么该如何确定一张加班单归属于哪一个工作日呢？
	 * 1.首先寻找与此加班单时间段有时间交叉的第一个工作日，如果找到了，则归属于此工作日
	 * 2.如果第一步没找到，则把加班单开始时间往前推一天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则到3
	 * 3.如果第二步也没找到，则把加班单结束时间往后推一天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则到4
	 * 4.把加班单开始时间往前推两天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则到5
	 * 5.把加班单结束时间往后推两天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则不再找了，返回空
	 * 因此计算某日的加班时长的方式是：找出归属于此天的所有加班单，然后对每个加班单单独计算，然后汇总到这天
	 * 注意，日报的加班时长，是要减去转调休的时长的。登记和申请审批节点不减
	 * @param timeItems
	 * @param billList，已经和各种单据交切过，并且已经减去了工作时间段的单据
	 * @param curCalendar
	 * @param lastCalendar
	 * @param nextCalendar
	 * @param statbVOList
	 */
	public static void processOvertiemLength(TimeItemCopyVO[] timeItems,
			List<ITimeScopeWithBillType> billList,
			String curDate,Map<String, UFLiteralDate> belongtoDateMap,
			List<DayStatbVO> statbVOList,String pk_daystat,TimeRuleVO timeRuleVO){
		//如果没有加班单，则不用处理（也有一种可能，那就是有加班单，但是和其他单据有时间冲突，且按照冲突规则的定义，这种冲突不算加班）
		if(billList==null||billList.size()==0 ||timeItems ==null)
			return;
		//归属于本日的加班单，key是加班原始单据，value是此原始单据对应的被交切过的单据
		Map<OvertimeCommonVO, List<ITimeScopeWithBillType>> belongtoCurDateBillMap = new HashMap<OvertimeCommonVO, List<ITimeScopeWithBillType>>();
		//按单据循环处理
		for(ITimeScopeWithBillType bill:billList){
			for(ITimeScopeWithBillType originalBill: bill.getOriginalTimeScopeMap().values()){
				if(!(originalBill instanceof OvertimeCommonVO))
					continue;
				OvertimeCommonVO overbvo = (OvertimeCommonVO)originalBill;
				UFLiteralDate belongdate = belongtoDateMap.get(overbvo.getPrimaryKey());
				if(belongdate==null||!curDate.equals(belongdate.toString()))
					break;
				//此单据属于当前计算的工作日
				List<ITimeScopeWithBillType> belongtoCurDateBillList = null;
				if(belongtoCurDateBillMap.containsKey(overbvo))
					belongtoCurDateBillList = belongtoCurDateBillMap.get(overbvo);
				else{
					belongtoCurDateBillList = new Vector<ITimeScopeWithBillType>();
					belongtoCurDateBillMap.put(overbvo, belongtoCurDateBillList);
				}
				belongtoCurDateBillList.add(bill);
				break;
			}
		}
		for(TimeItemCopyVO item:timeItems){
			TimeLengthWrapper result = calOvertimeLength(item, belongtoCurDateBillMap, timeRuleVO,true);
			createDaystatbVO(pk_daystat,item.getPk_timeitem(), result.originalLength,result.processedLength, 
					result.originalLengthUseHour,result.processedLengthUseHour,result.toRestHour,
					statbVOList,2,null,item.getTimeItemUnit());
		}
	}
	
	/**
	 * 根据工作日历，找出某张加班单所属的工作日
	 * 1.首先寻找与此加班单时间段有时间交叉的第一个工作日，如果找到了，则归属于此工作日
	 * 2.如果第一步没找到，则把加班单开始时间往前推一天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则到3
	 * 3.如果第二步也没找到，则把加班单结束时间往后推一天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则到4
	 * 4.把加班单开始时间往前推两天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则到5
	 * 5.把加班单结束时间往后推两天，看这个时间点是否属于某个工作日，如果找到了，则归属于此工作日；否则不再找了，返回空
	 * 2009.08.21与张旭讨论，将算法改为
	 * 1.与原算法第一步一致
	 * 2.将加班开始时间往前推24小时，在往前推的过程中，如果碰到了某个工作日的结束时间，就以这个工作日作为归属日，否则到3
	 * 3.将加班结束时间往后推24小时，在往后推的过程中，如果碰到了某个工作日的开始时间，就以这个工作日作为归属日，否则到4
	 * 4.以这个单据的开始时间所属的自然日作为归属日并返回
	 * @param overtimebVO
	 * @param calendarMap
	 * @param shiftMap
	 * @return
	 * @throws BusinessException 
	 */
	public static UFLiteralDate findBelongtoDate(OvertimeCommonVO overtimeVO,Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, ShiftVO> shiftMap,UFLiteralDate[] dateArray,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException{
		//先是第一轮寻找：找出与加班单有时间交集的最早的工作日，从单据开始时间前两天开始寻找，找到单据结束时间后两天为止
		UFLiteralDate date = findBelongtoDate0(overtimeVO, calendarMap, shiftMap, dateArray,true,dateTimeZoneMap);
		if(date!=null)
			return date;
		//代码走到这里，表示第一轮没有找到归属日，需要把加班单的时间点做一些改造:开始时间点往前挪一天，结束时间点为原来的开始时间,再次查找,这次是查找有交的最晚的
		UFLiteralDate beginDate = overtimeVO.getOvertimebegindate();
		OvertimeCommonVO cloneVO = (OvertimeCommonVO)overtimeVO.clone();
		cloneVO.setOvertimebegindate(beginDate.getDateBefore(1));
		cloneVO.setOvertimebegintime(DateTimeUtils.getDateTimeBeforeMills(cloneVO.getOvertimebegintime(), ICalendar.MILLIS_PER_DAY));
		cloneVO.setOvertimeenddate(beginDate);
		cloneVO.setOvertimeendtime(overtimeVO.getOvertimebegintime());
		date = findBelongtoDate0(cloneVO, calendarMap, shiftMap, dateArray,false,dateTimeZoneMap);
		if(date!=null)
			return date;
		//走到这里，表示第二轮也没有找到归属日，需要把加班单的时间点再做一些改造:结束时间点往后挪一天，开始时间点为原来的结束时间,再次查找,这次是查找有交的最早的
		UFLiteralDate endDate = overtimeVO.getOvertimeenddate();
		cloneVO = (OvertimeCommonVO)overtimeVO.clone();
		cloneVO.setOvertimeenddate(endDate.getDateAfter(1));
		cloneVO.setOvertimeendtime(DateTimeUtils.getDateTimeAfterMills(overtimeVO.getOvertimeendtime(), ICalendar.MILLIS_PER_DAY));
		cloneVO.setOvertimebegindate(endDate);
		cloneVO.setOvertimebegintime(overtimeVO.getOvertimeendtime());
		date = findBelongtoDate0(cloneVO, calendarMap, shiftMap, dateArray,true,dateTimeZoneMap);
		if(date!=null)
			return date;
		//走到这里不管能不能找到都返回，不再往下找了
		return beginDate;
	}
	
	/**
	 * @param overtimebVO
	 * @param calendarMap
	 * @param shiftMap
	 * @param dateArray
	 * @param isAscSearch true 表示找出最早相交的，false 表示最晚相交的
	 * @return
	 * @throws BusinessException 
	 */
	public static UFLiteralDate findBelongtoDate0(OvertimeCommonVO overtimeVO,
			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, ShiftVO> shiftMap,
			UFLiteralDate[] dateArray,
			boolean isAscSearch,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException{
		UFLiteralDate beginDate = overtimeVO.getOvertimebegindate();
		UFLiteralDate endDate = overtimeVO.getOvertimeenddate();
		int beginIndex = ArrayUtils.indexOf(dateArray,beginDate.getDateBefore(2));
		beginIndex = beginIndex<0?0:beginIndex;
		int endIndex = ArrayUtils.indexOf(dateArray,endDate.getDateAfter(2));
		endIndex = endIndex<0?(dateArray.length-1):endIndex;
		int i = isAscSearch?beginIndex:endIndex;
		for(;;){
			UFLiteralDate curDate = dateArray[i];
			//当天班次的考勤时间段
			ITimeScope curDateAttendTimeScope = getAttendTimeScope4OvertimeCheck(i==0?null:dateArray[i-1],
					curDate, i==dateArray.length-1?null:dateArray[i+1],calendarMap, shiftMap,dateTimeZoneMap);
			//如果当天的考勤时间段与加班单的时间段有交集，则直接返回此天
			if(TimeScopeUtils.isCross(overtimeVO,curDateAttendTimeScope))
				return curDate;
			i=isAscSearch?(i+1):(i-1);
			if(i>endIndex||i<beginIndex)
				break;
		}
		return null;
	}
	
	public static UFLiteralDate findBelongtoDate(OvertimeCommonVO overtimeVO,Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, ShiftVO> shiftMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException{
		UFLiteralDate beginDate = overtimeVO.getOvertimebegindate();
		UFLiteralDate endDate = overtimeVO.getOvertimeenddate();
		return findBelongtoDate(overtimeVO, calendarMap, shiftMap, CommonUtils.createDateArray(beginDate, endDate,2,2),dateTimeZoneMap);
	}
	
	public static void findBelongtoDate(OvertimeCommonVO[] overtimeVOs,
			Map<String,UFLiteralDate> belongDateMap,
			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, ShiftVO> shiftMap,UFLiteralDate[] dateArray,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException{
		if(overtimeVOs == null || overtimeVOs.length==0)
			return;
		for(OvertimeCommonVO vo:overtimeVOs)
			belongDateMap.put(vo.getPrimaryKey(), findBelongtoDate(vo, calendarMap, shiftMap,dateArray,dateTimeZoneMap));
	}
	
	/**
	 * 从各种单据（包括出差休假加班停工）的时间段数组中，找出相应类型的单据。billType表明是加班还是休假还是出差还是停工。详见BillMutexRule类的常量定义
	 * 如果时间段是几种单据相交而成的（比如休假和出差交），那么需要用冲突规则来判断是否应该算成billType类型的时间段
	 * @param bills
	 * @param billType
	 * @return
	 */
	public static List<ITimeScopeWithBillType> filterBills(ITimeScopeWithBillType[] bills,int billType,BillMutexRule billMutexRule){
		if(bills==null||bills.length==0)
			return null;
		List<ITimeScopeWithBillType> retList = new Vector<ITimeScopeWithBillType>(5);
		for(ITimeScopeWithBillType scope:bills){
			//如果此时间段可以计为billType类型（见BillMutexRule类的常量定义）的单据，则加入retList。这个if会过滤掉不是billType类型的时间段，也会过滤掉是billType
			//类型，但是与其他类型的单据有时间冲突且按照冲突规则不计为billType的时间段。冲突规则定义见BillMutexRule.getMutexResult方法
			//比如billType是BillMutexRule.BILL_AWAY出差类型，那么这个if会过滤掉加班休假停工类型的时间段，并且会过滤掉出差与其他类型的单据交且按冲突规则不计为
			//出差的时间段
			if(containsBillType(billMutexRule.getMutexResult(scope.getBillType()), billType))
				retList.add(scope);
		}
		return retList;
	}
	
	/**
	 * 从各种单据（包括出差休假加班停工）的时间段数组中，找出加班类型的单据。过滤出单据后，再减去工作时间段，剩下的才是能够计算加班时长的数据
	 * 南孚电池有需求：如果公休日不计休假，那么加班单应该可以录入，并且可计算时长。这个需求是脱离于考勤项目的冲突规则的，
	 * 因为就算冲突规则定义休假和加班冲突，南孚电池也要求在公休日不计休假的时候，加班单可以录入。
	 * 如果时间段没有和休假相交，则直接使用冲突规则。如果和休假相交，则要考虑
	 * 如果冲突规则规定休假和加班不冲突，并且计为加班，那么不用做任何特殊处理，直接调用filterBills方法即可。可下面的几种情况
	 * 需要考虑
	 * 1.冲突规则规定休假和加班不冲突，但只计休假，不计加班
	 * 2.冲突规则规定休假和加班冲突
	 * 
	 * v63添加，假日排班记为加班，因此减去工作时间段时，工作时间段要先减去假日时间
	 * @param bills
	 * @return
	 * @throws BusinessException 
	 */
	public static List<ITimeScopeWithBillType> filterOvertimeBills(ITimeScopeWithBillType[] bills,
			TimeItemCopyVO[] leaveItemVOs,
			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, AggShiftVO> shiftMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap,
			UFLiteralDate[] dateArray,int beginDateIndexInDateArray,int endDateIndexInDateArray,BillMutexRule billMutexRule,ITimeScope[] holidayScope) throws BusinessException{
		if(bills==null||bills.length==0)
			return null;
		//循环处理
		List<ITimeScopeWithBillType> retList = new ArrayList<ITimeScopeWithBillType>(5);
		for(ITimeScopeWithBillType scope:bills){
			//如果时间段没有加班单属性，则不用考虑，直接continue，因为此方法只是过滤出加班时间段
			if((scope.getBillType()&BILL_OVERTIME)!=BILL_OVERTIME)
				continue;
			//如果时间段没有和休假单相交，则采用冲突规则（与filterBills方法一样），不用特殊处理，采用冲突规则检查即可
			if((scope.getBillType()&BILL_LEAVE)!=BILL_LEAVE){
				if(containsBillType(billMutexRule.getMutexResult(scope.getBillType()), BILL_OVERTIME))
					retList.add(scope);
				continue;
			}
			//代码走到这里，scope肯定至少是休假和加班的相交的单子
			//如果时间段和休假单相交，则看冲突规则规定此种组合是否可以计为加班，如果计为加班，则也不用特殊处理，采用冲突规则检查即可
			if(containsBillType(billMutexRule.getMutexResult(scope.getBillType()), BILL_OVERTIME)){
				retList.add(scope);
				continue;
			}
			//如果加班和休假组合不计为加班，则按道理就应该continue了，不用理这个加班时间段。但公休日是否计休假给这个严格的规定开了个
			//后门：如果公休日计休假，那么不用理。如果公休日不计休假，那么就要处理。这样的话，就要分休假类别类别处理
			for(TimeItemCopyVO leaveItemVO: leaveItemVOs){
				//如果这个时间段不属于这个休假类别，则continue
				if(!scope.belongsToTimeItem(leaveItemVO.getPk_timeitem()))
					continue;
				//0:公休不计休假，1：公休计休假
				int gxComType = leaveItemVO.getGxcomtype()==null?0:leaveItemVO.getGxcomtype().intValue();
				//如果是公休计休假，那么不用理
				if(gxComType==1)
					break;
				//如果公休计休假，那么就要看此加班时间段是否在公休范围内，如果不在公休范围内，则不用理。如果在公休范围内，则要处理
				if(MapUtils.isEmpty(calendarMap))
					break;
				//按日期循环处理
				for(int i =beginDateIndexInDateArray;i<= endDateIndexInDateArray;i++){
					AggPsnCalendar calendar = calendarMap.get(dateArray[i]);
					PsnCalendarVO psncalendarVO =  calendar==null?null:calendar.getPsnCalendarVO();
					if(psncalendarVO!=null&&!ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift()))
						continue;
					TimeZone curTimeZone = CommonUtils.ensureTimeZone(dateTimeZoneMap.get(dateArray[i]));
					TimeZone preTimeZone = CommonUtils.ensureTimeZone(dateTimeZoneMap.get(dateArray[i-1]));
					TimeZone nextTimeZone = CommonUtils.ensureTimeZone(dateTimeZoneMap.get(dateArray[i+1]));
					AggPsnCalendar preCalendar = calendarMap.get(dateArray[i-1]);
					PsnCalendarVO prePsncalendarVO =  preCalendar==null?null:preCalendar.getPsnCalendarVO();
					
					ShiftVO preShiftVO = prePsncalendarVO==null?null:ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, prePsncalendarVO.getPk_shift()).getShiftVO();
					AggPsnCalendar nextCalendar = calendarMap.get(dateArray[i+1]);
					PsnCalendarVO nextPsncalendarVO =  nextCalendar==null?null:nextCalendar.getPsnCalendarVO();
					ShiftVO nextShiftVO = nextPsncalendarVO==null?null:ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, nextPsncalendarVO.getPk_shift()).getShiftVO();
					//如果是公休，则取加班单与公休的交集
					ITimeScopeWithBillType[] intersection = 
						TimeScopeUtils.intersectionTimeScopes(new ITimeScopeWithBillType[]{scope}, 
								getWorkTimeScopes(dateArray[i].toString(), calendar, 
										preShiftVO, 
										nextShiftVO, 
										curTimeZone,preTimeZone,nextTimeZone),
										new ITimeScopeWithBillType[0]);
					if(intersection==null||intersection.length==0)
						continue;
					//把单子加入到返回列表中
					retList.addAll(Arrays.asList(intersection));
				}
			}
		}
		//如果没有工作日历，则下面的减去工作时间段的操作不用进行
		if(calendarMap==null||calendarMap.size()==0)
			return retList;
		//最终的时间段，要减去工作时间段（与休假停工不同，休假停工与工作时间段的运算，是分散在每一天的，而加班单是在刚开始就把所有的加班单减去所有的工作时间段，然后再细算每一天的加班单长度）
		//所有天的工作时间段的list
		List<ITimeScope> worktimeScopeList = new Vector<ITimeScope>();
		for(UFLiteralDate date:calendarMap.keySet()){
			AggPsnCalendar calendarAllVO = calendarMap.get(date);
			if(calendarAllVO==null||ArrayUtils.isEmpty(calendarAllVO.getPsnWorkTimeVO()))
				continue;
			PsnWorkTimeVO[] workTimeVOs = calendarAllVO.getPsnWorkTimeVO();
			for(PsnWorkTimeVO workTimeVO:workTimeVOs){
				worktimeScopeList.add(workTimeVO);
			}
		}
		//如果工作时间段为空，则直接返回原始的单据
		if(worktimeScopeList.size()==0)
			return retList;
		
		ITimeScope[] worktimeScops = worktimeScopeList.toArray(new ITimeScope[0]);
		//否则要减去工作时间段
		ITimeScopeWithBillType[] billScopes = retList.toArray(new ITimeScopeWithBillType[0]);
		ITimeScopeWithBillType[] minusedScopes =  (ITimeScopeWithBillType[]) TimeScopeUtils.minusTimeScopes(billScopes, worktimeScops);
		if(!ArrayUtils.isEmpty(holidayScope)){
//		//工作时间段内是否有有假日，若有假日需要考虑假日排班记为加班的情况
			//V63添加，加上（单据时间、假日时间、工作时间三者重叠记为加班的时间）
			ITimeScope[] holidayWorktimeScope = TimeScopeUtils.intersectionTimeScopes(worktimeScops, holidayScope);
			ITimeScope[] holidayBillWorktimeScope = TimeScopeUtils.intersectionTimeScopes(billScopes, holidayWorktimeScope);
			minusedScopes = (ITimeScopeWithBillType[]) ArrayUtils.addAll(minusedScopes, holidayBillWorktimeScope);
		}
		if(minusedScopes==null||minusedScopes.length==0)
			return null;
		//此时加班时间段还可能含有假日排班中班次的工休时间段，和不记为加班的上下班的延时时间段,这些时长都不能记为加班时长。
		//按日期循环处理
		List<ITimeScope> rtScopeList = new Vector<ITimeScope>();//工休时间段
		List<ITimeScope> notIncludList = new Vector<ITimeScope>();//班次定义的延时不记为加班的时长的时间段
		for(UFLiteralDate date:calendarMap.keySet()){
			AggPsnCalendar calendarAllVO = calendarMap.get(date);
			if(calendarAllVO==null||ArrayUtils.isEmpty(calendarAllVO.getPsnWorkTimeVO())|| calendarAllVO.getPsnCalendarVO() == null||ShiftVO.PK_GX.equals(calendarAllVO.getPsnCalendarVO().getPk_shift()))
				continue;
			PsnWorkTimeVO[] workTimeVOs = calendarAllVO.getPsnWorkTimeVO();
			if(workTimeVOs.length>1){//取工休时间
				TimeScopeUtils.sort(workTimeVOs);
				for(int i=1;i<workTimeVOs.length;i++){
					ITimeScope rtscope = new DefaultTimeScope();
					rtscope.setScope_start_datetime(workTimeVOs[i-1].getScope_end_datetime());
					rtscope.setScope_end_datetime(workTimeVOs[i].getScope_start_datetime());
					rtScopeList.add(rtscope);
				}
			}
			ITimeScope workScope = calendarAllVO.toWorkScope();
			ShiftVO workShift = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, calendarAllVO.getPsnCalendarVO().getPk_shift()).getShiftVO();
			//上班延迟不记为加班的部分
			if(workShift.getUseontmrule()!=null&&workShift.getUseontmrule().booleanValue()){
				ITimeScope notIncludBeforScope = new DefaultTimeScope(workShift.getOntmend().multiply(60).longValue(),workScope.getScope_start_datetime());
//				rtScopeList.add(notIncludBeforScope);
				notIncludList.add(notIncludBeforScope);
			}
			//下班延迟中不记为加班的部分
			if(workShift.getUseovertmrule()!=null&&workShift.getUseovertmrule().booleanValue()){
				ITimeScope notIncludAfterScope = new DefaultTimeScope(workScope.getScope_end_datetime(),workShift.getOvertmbegin().multiply(60).longValue());
//				rtScopeList.add(notIncludAfterScope);
				notIncludList.add(notIncludAfterScope);
			}
			
		}
//		if(rtScopeList==null||rtScopeList.size()==0)
//			return Arrays.asList(minusedScopes);
		if(!CollectionUtils.isEmpty(notIncludList)){
			//扣除延时不记为加班的时长的时间段
			minusedScopes = (ITimeScopeWithBillType[]) TimeScopeUtils.minusTimeScopes(minusedScopes, notIncludList.toArray(new ITimeScope[0]));
		}
		//扣除假日排班的工间休时间段
		if(!ArrayUtils.isEmpty(minusedScopes)&&!ArrayUtils.isEmpty(holidayScope)&&CollectionUtils.isNotEmpty(rtScopeList)){
			ITimeScope[] holidayrtScope = TimeScopeUtils.intersectionTimeScopes(rtScopeList.toArray(new ITimeScope[0]), holidayScope);
			minusedScopes = (ITimeScopeWithBillType[]) TimeScopeUtils.minusTimeScopes(minusedScopes, holidayrtScope);
		}
		if(minusedScopes==null||minusedScopes.length==0)
			return null;
		return Arrays.asList(minusedScopes);
	}
	
	/**
	 * 计算某日的出差时长，filteredVOs已经与加班单休假单停工单交切过，但还没有与工作时间段交切
	 * 要求传进来的工作日历已经是固化了的。即，此类不负责固化工作
	 * 根据出差类别的设置，出差的时长有两种算法：如果出差时长取与工作段的交集，则算法与休假完全一样；如果出差时长不取与工作段的交集，则按自然日算法
	 * @param leaveItem
	 * @param filteredVOs
	 * @param curDate
	 * @param curCalendar
	 * @param preShift
	 * @param curShift
	 * @param nextShift
	 * @param workDayLength
	 * @return
	 */
	public static TimeLengthWrapper calAwayLength(TimeItemCopyVO timeItem,ITimeScopeWithBillType[] filteredVOs,String curDate,
			AggPsnCalendar curCalendar,
			ShiftVO preShift,ShiftVO curShift,ShiftVO nextShift,
			TimeZone preTimeZone,TimeZone curTimeZone,TimeZone nextTimeZone,TimeRuleVO timeRuleVO){
		//如果是取交集，则完全采用休假的算法
		if(timeItem.getIsinterwt()!=null&&timeItem.getIsinterwt().booleanValue()){
			return calLeaveLength(timeItem, filteredVOs, curDate, curCalendar, 
					preShift, curShift, nextShift, 
					preTimeZone,curTimeZone,nextTimeZone,
					timeRuleVO);
		}
		return calAwayLength(timeItem, filteredVOs, curDate, curShift==null?null:curShift.getPrimaryKey(), timeRuleVO);
	}
	
	/**
	 * 处理某人某天的出差单的时长。如果算得的时长大于0，则new一个日报子表vo，加入到list中
	 * 出差有两种算法：取与工作时间段的交集，或者取与自然日的交集
	 * 出差按自然日计算，即某日的出差时长是出差单与这一日0点到23:59:59之间交的结果
	 * @param timeItems
	 * @param billList所有出差单的时间段（已经和其他类型的单据比如加班休假停工交过的，并且已经按冲突规则去掉了和其他单据相冲突且不计为出差的时间部分）
	 * @param curCalendar
	 * @param statbVOList
	 */
	public static void processAwayLength(TimeItemCopyVO[] timeItems,List<ITimeScopeWithBillType> billList,String curDate,
			AggPsnCalendar curCalendar,
			ShiftVO preShift,ShiftVO curShift,ShiftVO nextShift,
			TimeZone preTimeZone,TimeZone curTimeZone,TimeZone nextTimeZone,
			List<DayStatbVO> statbVOList,Map<String, String[]> datePeriodMap,Map<String, Object> paraMap,
			String pk_daystat,TimeRuleVO timeRuleVO){
		if(timeItems==null){//如果没有设置出差类别则直接不用计算
			return;
		}
		//将出差单分为两类：与工作时间段交集的为一类，与自然日交集的为一类
		//与工作时间段交集的，采用与休假相同的算法，与自然日交集的，采用原始的出差算法
		List<TimeItemCopyVO> wtInterTypeList = new ArrayList<TimeItemCopyVO>();
		List<ITimeScopeWithBillType> wtInterBillList = new ArrayList<ITimeScopeWithBillType>();
		List<TimeItemCopyVO> natualDayInterTypeList = new ArrayList<TimeItemCopyVO>();
		List<ITimeScopeWithBillType> natualDayBillList = new ArrayList<ITimeScopeWithBillType>();
		for(TimeItemCopyVO typeVO:timeItems){
			boolean isWtInter = typeVO.getIsinterwt()!=null&&typeVO.getIsinterwt().booleanValue();
			if(isWtInter)
				wtInterTypeList.add(typeVO);
			else
				natualDayInterTypeList.add(typeVO);
			for(ITimeScopeWithBillType bill:billList){
				if(!bill.belongsToTimeItem(typeVO.getPk_timeitem()))
					continue;
				if(isWtInter)
					wtInterBillList.add(bill);
				else
					natualDayBillList.add(bill);
			}
		}
		//计算与工作段相交的类别，算法与休假的计算一模一样
		if(wtInterTypeList.size()>0&&wtInterBillList.size()>0){
			processLeaveLength(wtInterTypeList.toArray(new TimeItemCopyVO[0]), wtInterBillList, curCalendar, 
					preShift, curShift, nextShift, 
					preTimeZone,curTimeZone,nextTimeZone,
					statbVOList, datePeriodMap, paraMap,  pk_daystat, timeRuleVO);
		}
		//计算与自然日相交的类别
		if(natualDayInterTypeList.size()>0&&natualDayBillList.size()>0){
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap = new HashMap<UFLiteralDate, TimeZone>();
			UFLiteralDate curLiteralDate = UFLiteralDate.getDate(curDate);
			dateTimeZoneMap.put(curLiteralDate, curTimeZone);
			dateTimeZoneMap.put(curLiteralDate.getDateBefore(1), preTimeZone);
			dateTimeZoneMap.put(curLiteralDate.getDateAfter(1), nextTimeZone);
			processAwayLength0(natualDayInterTypeList.toArray(new TimeItemCopyVO[0]), natualDayBillList, curDate, curCalendar, 
					statbVOList, datePeriodMap, paraMap,  pk_daystat,dateTimeZoneMap, timeRuleVO);
		}
	}
	
	/**
	 * 计算日报的某一天出差的长度
	 * @param timeItems
	 * @param billList
	 * @param curDate
	 * @param curCalendar
	 * @param statbVOList
	 * @param datePeriodMap
	 * @param paraMap
	 * @param workDayLength
	 * @param pk_daystat
	 * @param curTimeZone
	 */
	private static void  processAwayLength0(TimeItemCopyVO[] timeItems,List<ITimeScopeWithBillType> billList,String curDate,
			AggPsnCalendar curCalendar,List<DayStatbVO> statbVOList,Map<String, String[]> datePeriodMap,
			Map<String, Object> paraMap,String pk_daystat,Map<UFLiteralDate, TimeZone> dateTimeZoneMap,TimeRuleVO timeRuleVO){
		//如果没有出差单，则不用处理（也有一种可能，那就是有出差单，但是和其他单据有时间冲突，且按照冲突规则的定义，这种冲突不算出差）
		if(billList==null||billList.size()==0)
			return;
		//取得班次（没排班按公休算）
		String pk_shift = curCalendar==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO()==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO().getPk_shift();
		ITimeScope dateScope = TimeScopeUtils.toFullDay(curDate,dateTimeZoneMap.get(UFLiteralDate.getDate(curDate)));//自然日的时间范围0点到23:59:59
		//取得出差时间段与自然日的交集时间段
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList.toArray(new ITimeScopeWithBillType[0]), new ITimeScope[]{dateScope},new ITimeScopeWithBillType[0]);
		//如果与自然日交集之后长度为0，则不用处理
		if(intersectionScopes==null||intersectionScopes.length==0)
			return;
		//取出参数：单据跨期间时，是记在各个期间，还是第一个期间记在第二个期间,0-分别记，1-第一记在第二
//		Integer periodPara = (Integer)paraMap.get(AwayConst.PARA_AWAY_OVERPERIOD);
		Integer periodPara =  timeRuleVO.getAwayovperprtype();
		if(periodPara==null)//默认分别记
			periodPara=0;
		//按出差类别循环处理
		for(TimeItemCopyVO item:timeItems){
			TimeLengthWrapper result = calAwayLength(item,intersectionScopes,curDate,pk_shift,timeRuleVO);
			//处理期间
			String calcPeirod = null;
			if(periodPara==1){
				calcPeirod = queryCalcPeriod(curDate,intersectionScopes,datePeriodMap,item.getPk_timeitem());
			}
			createDaystatbVO(pk_daystat,item.getPk_timeitem(), result.originalLength,result.processedLength, 
					result.originalLengthUseHour,result.processedLengthUseHour,result.toRestHour,
					statbVOList,3,calcPeirod,item.getTimeItemUnit());
		}
	}
	
	/**
	 * 根据出差单据和当日的班次计算出出差时长
	 * @param awayItem
	 * @param vo
	 * @param curDate
	 * @param curCalendar，工作日历，只是用来判断当日是否是公休，没有其他作用
	 * @return
	 */
//	public static TimeLengthWrapper calAwayLength(TimeItemCopyVO awayItem,AwayCommonVO vo,String curDate,
//			AggPsnCalendar curCalendar,double workDayLength,TimeZone timeZone){
//		ITimeScope dateScope = TimeScopeUtils.toFullDay(curDate,timeZone);//自然日的时间范围0点到23:59:59
//		//取得出差时间段与自然日的交集时间段
//		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
//			(new AwayCommonVO[]{vo}, new ITimeScope[]{dateScope},new ITimeScopeWithBillType[0]);
//		if(intersectionScopes==null||intersectionScopes.length==0)
//			return TimeLengthWrapper.getZeroTimeLength();
//		String pkClass = curCalendar==null?BclbVO.PK_GX:curCalendar.getPsnCalendarVO()==null?BclbVO.PK_GX:curCalendar.getPsnCalendarVO().getPk_class();
//		return calAwayLength(awayItem,intersectionScopes,curDate,pkClass, workDayLength,timeZone);
//	}
	
//	public static TimeLengthWrapper calLeaveLength(TimeItemCopyVO leaveItem,LeaveCommonVO vo,String curDate,
//			AggPsnCalendar curCalendar,BclbVO preBclb,BclbVO curBclb,BclbVO nextBclb,double workDayLength,TimeZone timeZone){
//		return calLeaveLength(leaveItem, new LeaveCommonVO[]{vo}, curCalendar,curBclb, workDayLength);
//	}
	
	/**
	 * 计算某日的休假时长，filteredVOs已经与加班单出差单停工单交切过，但还没有与工作时间段交切
	 * 要求传进来的工作日历已经是固化了的。即，此类不负责固化工作
	 * @param leaveItem
	 * @param filteredVOs
	 * @param curDate
	 * @param curCalendar
	 * @param preShift
	 * @param curShift
	 * @param nextShift
	 * @param workDayLength
	 * @return
	 */
	public static TimeLengthWrapper calLeaveLength(TimeItemCopyVO leaveItem,ITimeScopeWithBillType[] filteredVOs,String curDate,
			AggPsnCalendar curCalendar,
			ShiftVO preShift,ShiftVO curShift,ShiftVO nextShift,
			TimeZone preTimeZone,TimeZone curTimeZone,TimeZone nextTimeZone,
			TimeRuleVO timeRuleVO){
		//取得休假时间段与工作日的交集时间段
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(filteredVOs, getWorkTimeScopes(curDate, curCalendar, preShift, nextShift,curTimeZone,preTimeZone,nextTimeZone)
					,new ITimeScopeWithBillType[0]);
		if(intersectionScopes==null||intersectionScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		return calLeaveLength(leaveItem, intersectionScopes, curCalendar,curShift, timeRuleVO);
	}
	
//	public static TimeLengthWrapper calShutdownLength(TimeItemCopyVO shutdownItem,ShutdownRegVO vo,
//			AggPsnCalendar curCalendar){
//		if(curCalendar == null)
//			return TimeLengthWrapper.getZeroTimeLength();
//		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
//		PsnWorkTimeVO[] workVOs = curCalendar.getPsnWorkTimeVO();
//		//如果此天没有工作日历，或者是公休或者不考勤的，或者没有工作时间段，则停工待料时长为0
//		if(psncalendarVO==null||BclbVO.PK_GX.equals(psncalendarVO.getPk_class())||ArrayUtils.isEmpty(workVOs))
//			return TimeLengthWrapper.getZeroTimeLength(); 
//		//算出停工单与工作时间段的交集
//		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
//			(new ITimeScope[]{vo}, workVOs,new ITimeScopeWithBillType[0]);
//		//如果没有交集时间段，则不用处理
//		if(intersectionScopes==null||intersectionScopes.length==0)
//			return TimeLengthWrapper.getZeroTimeLength();
//		return calShutdownLength(shutdownItem,intersectionScopes,curCalendar);
//	}
	/**
	 * 计算某个出差类别的时长
	 * intersectionScopes是与其他类型单据交切过，且与自然日交集过后的时间段，已经将不符合冲突规则的时间段挖去
	 * 比如9:00-17:00出差，14:00-17:00休假，如果冲突规则定义出差加休假算休假，那么14:00-17:00这段时间就要从出差单中挖去，只剩下9:00-13:59:59
	 * 5.5的出差算法，与5.02完全一致：没有最小时长和取整规则
	 * 按小时计算时：如果是当日往返，则按实际小时数算
	 *            如果是跨天，则首日：12点前出发按一个工作日时长算，否则0.5个工作日时长
	 *                       尾日：12点后回来按一个工作日时长算，否则0.5个工作日时长
	 *                       中间日：min(实际时长，工作日时长)
	 * 按天计算时：首尾天规则是各计一天：每天都按一天算
	 *           首尾天规则是以12点为界：
	 *               如果是当日往返，则12点前回来是0.5天，12点后出发时0.5天，12点前出发12点后回来是1天
	 *               如果是跨天， 则首日：12点前出发算1天，否则0.5天
	 *                           尾日：12点后回来算1天，否则0.5天
	 *                           中间日：1天
	 *                      
	 * @param awayItem
	 * @param intersectionScopes
	 * @param curDate
	 * @return 出差时长。由于出差时长经过处理后不影响出勤工时的计算，因此出差时长的原始时长和处理时长暂时取相同的值，待以后有需求的时候再改
	 */
	private static TimeLengthWrapper calAwayLength(TimeItemCopyVO awayItem,ITimeScopeWithBillType[] intersectionScopes,
			String curDate,String pk_shift,TimeRuleVO timeRuleVO){
		int gxComType = awayItem.getGxcomtype()==null?0:awayItem.getGxcomtype().intValue();//0公休不计，1公休计
		//如果是公休且此类别公休不计出差，则返回0
		if(ShiftVO.PK_GX.equals(pk_shift)&&gxComType==0)
			return TimeLengthWrapper.getZeroTimeLength();
		//过滤出这种类型（比如本地出差）的时间段
		ITimeScopeWithBillType[] filteredScopes = TimeScopeUtils.filterByItemPK(awayItem.getPk_timeitem(), intersectionScopes);
		if(filteredScopes==null||filteredScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		//1-天，2-小时,默认小时
		int timeitemUnit = awayItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:awayItem.getTimeitemunit().intValue();
		//最小时间单位-对于以小时计的加班类别，单位是分钟；对于以天计的加班类别，单位是天
//		double timeUnit = awayItem.getTimeunit()==null?0:awayItem.getTimeunit().doubleValue();
		//取整规则，0向上，1向下，2四舍五入，默认1；
//		int roundMode = awayItem.getRoundmode()==null?1:awayItem.getRoundmode().intValue();
		//出差时间段的秒数
		//long seconds = TimeScopeUtils.getLengthAutoContainsLastSecond(filteredScopes);
		//如果是按小时计算
		double result = 0;
		double workDayLength = timeRuleVO.getDaytohour2();
		if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
			long seconds = 0;
//			boolean containsBeginEndInSameDay = false;//这些时间段中，是否有当天往返的单据
			for(ITimeScopeWithBillType filteredScope:filteredScopes){
				//当前时间段的绝对时长
				long curBillLength = TimeScopeUtils.getLength(filteredScope);
				int datePos = getDatePos(curDate, awayItem.getPk_timeitem(), filteredScope);
//				if(datePos == TimeScopeUtils.DATE_POS_FIRST_LAST)
//					containsBeginEndInSameDay = true;
				if(curBillLength==0)
					continue;
				//此切割后单据上原始的单据
				IBillInfo originalBill = (IBillInfo) filteredScope.getOriginalTimeScopeMap().get(awayItem.getPk_timeitem());
				switch(datePos){
				//如果是当日往返,则按实际时长计算（2012.5.28与zx讨论后确认，按小时计算时，当日往返用自然时长，不能超过考勤规则的工作日折合时长,改变了之前当日往返可能出差十几个小时的算法）
				case TimeScopeUtils.DATE_POS_FIRST_LAST:
					seconds+=Math.min(TimeScopeUtils.getLength(filteredScope),ITimeScope.SECONDS_PER_HOUR*workDayLength);
					break;
					//中间天,则按实际时长计算
				case TimeScopeUtils.DATE_POS_MIDDLE:
					seconds+= Math.min(curBillLength,ITimeScope.SECONDS_PER_HOUR*workDayLength);
					break;
					//首日，则12点前出发是一个工作日时长，否则0.5个工作日时长
				case TimeScopeUtils.DATE_POS_FIRST:
					if(filteredScope.getScope_start_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")<0){
						seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength;
						break;
					}
					//在12点（包含）之后开始计为半天
					seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength/2;
					break;
					//尾日：12点后回来，计为1个工作日时长，否则0.5个工作日时长
				case TimeScopeUtils.DATE_POS_LAST:
					if(filteredScope.getScope_end_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")>0){
						seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength;
						break;
					}
					//在12点（包含）之前结束计为半天
					seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength/2;
					break;
				}
			}
			result = Math.min(workDayLength, ((double)seconds)/ITimeScope.SECONDS_PER_HOUR);
			double processedLen = Math.min(workDayLength, CommonMethods.processByDecimalDigitsAndRoundMode(((double)seconds)/ITimeScope.SECONDS_PER_HOUR, timeRuleVO).doubleValue());
			return new TimeLengthWrapper(result,processedLen,result,processedLen);
		}
		//如果是按天计算
		int calculatetype = awayItem.getCalculatetype()==null?0:awayItem.getCalculatetype().intValue();//首尾天计算方式0：12点为界计半天，1：计一天
		double days = 0;
		for(ITimeScopeWithBillType filteredScope:filteredScopes){
			//当前时间段的绝对时长
			long curBillLength = TimeScopeUtils.getLength(filteredScope);
			if(curBillLength==0)
				continue;
			int datePos = getDatePos(curDate, awayItem.getPk_timeitem(), filteredScope);
			//如果首尾天规则是各计一天，则每天都按一天计
			if(calculatetype==1){//首尾天分别计为1天
				if(datePos!=TimeScopeUtils.DATE_POS_OUT_OF_RANGE)
					days+=1;
				continue;
			}
			//此切割后单据上原始的单据
			IBillInfo originalBill = (IBillInfo) filteredScope.getOriginalTimeScopeMap().get(awayItem.getPk_timeitem());
			//如果是首尾天规则是以12点为界，则要看是否是首尾天
			switch(datePos){
			//如果是当日往返,则12点前回来是0.5天，12点后出发时0.5天，12点前出发12点后回来是1天
			case TimeScopeUtils.DATE_POS_FIRST_LAST:
				if(filteredScope.getScope_end_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")<=0
						||filteredScope.getScope_start_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")>=0){
					days+=0.5;
					break;
				}
				days+=1;
				break;
				//中间天,算一天
			case TimeScopeUtils.DATE_POS_MIDDLE:
				days+=1;
				break;
				//首日，则12点前出发是一天，否则半天
			case TimeScopeUtils.DATE_POS_FIRST:
				if(filteredScope.getScope_start_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")<=0){
					days+=1;
					break;
				}
				//在12点（包含）之后开始计为半天
				days+=0.5;
				break;
				//尾日：12点后回来，计为1个工作日时长，否则0.5个工作日时长
			case TimeScopeUtils.DATE_POS_LAST:
				if(filteredScope.getScope_end_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")>=0){
					days+=1;
					break;
				}
				//在12点（包含）之前结束计为半天
				days+=0.5;
				break;
			}
		}
		days=Math.min(1, days);
		double processedDays = Math.min(1, CommonMethods.processByDecimalDigitsAndRoundMode(days, timeRuleVO).doubleValue());
		return new TimeLengthWrapper(days,processedDays,days*timeRuleVO.getDaytohour2(),processedDays*timeRuleVO.getDaytohour2());
	}
	
	/**
	 * 根据考勤项目的值和考勤项目主键，生成日报子表vo。如果值等于0，则不生成vo
	 * @param pk_timeitem
	 * @param val
	 * @param statbVOList
	 * @param type:1.休假(请假)  2.加班  3.出差(公出)8:停工待料
	 */
	private static void createDaystatbVO(String pk_daystat,String pk_timeitem,
			double originalVal,double processedVal,
			double originalValUseHour,double processedValUseHour,
			double toRestHour, List<DayStatbVO> statbVOList,
			int type,String calPeriod,int timeitemUnit){
		if(Math.abs(originalVal-0)>0.0001||Math.abs(processedVal-0)>0.0001){
			DayStatbVO vo = new DayStatbVO();
			vo.setDr(Integer.valueOf(0));
			vo.setOrihournum(new UFDouble(originalVal));
			vo.setHournum(new UFDouble(processedVal));
			vo.setOrihournumusehour(new UFDouble(originalValUseHour));
			vo.setHournumusehour(new UFDouble(processedValUseHour));
			vo.setPk_timeitem(pk_timeitem);
			vo.setType(type);
			vo.setTimeitemunit(timeitemUnit);
			vo.setPk_daystat(pk_daystat);
			vo.setCalcperiod(calPeriod);
			vo.setToresthour(new UFDouble(toRestHour));
			statbVOList.add(vo);
		}
	}
	
	/**
	 * 判断curDate表示的日期在scope表示的单据的原始单据中所处的位置，是首天还是尾天还是中间天
	 * @param scope
	 * @return
	 */
	private static int getDatePos(String curDate,String pk_timeitem,ITimeScopeWithBillType scope){
		//原始单据
		ITimeScope originalBill = scope.getOriginalTimeScopeMap().get(pk_timeitem);
		if(originalBill==null)
			return TimeScopeUtils.DATE_POS_OUT_OF_RANGE;
		return TimeScopeUtils.getDatePos(curDate, originalBill,((IBillInfo)originalBill).getTimezone());
	}
	
	/**
	 * 得到curCalendar对应的工作时间段。对于非公休班次，就是班次定义的时间段。
	 * 对于公休班次，则要看前一天的班次和后一天的班次：
	 * 如果前一天是公休，那么curCalendar从00:00:00开始，否则从前一个班次的结束时间开始
	 * 如果后一天是公休，那么curCalendar到23:59:59结束，否则到后一个班次的开始时间结束
	 * @param curCalendar
	 * @param lastCalendar
	 * @param nextCalendar
	 * @return
	 */
	public static ITimeScope[] getWorkTimeScopes(String curDate,AggPsnCalendar curCalendar,ShiftVO preShift,ShiftVO nextShift,
			TimeZone curTimeZone,TimeZone preTimeZone,TimeZone nextTimeZone){
		//班次为空就按公休处理
		String pk_shift = curCalendar==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO()==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO().getPk_shift();
		//如果不是公休，则是工作时间段，不用考虑前后天的班次
		if(!ShiftVO.PK_GX.equals(pk_shift)){
			return curCalendar.getPsnWorkTimeVO();
		}
		return new ITimeScope[]{ShiftVO.toKqScope(null, preShift, nextShift, curDate, curTimeZone,preTimeZone,nextTimeZone)};
	}
	
	/**
	 * 得到某天的考勤时间段。即刷卡开始到刷卡结束时间段
	 * @param curDate
	 * @param preShift
	 * @param curShift
	 * @param nextShift
	 * @return
	 */
	public static ITimeScope getAttendTimeScope(String curDate,ShiftVO preShift,ShiftVO curShift,ShiftVO nextShift,
			TimeZone preTimeZone,TimeZone curTimeZone,TimeZone nextTimeZone){
		return ShiftVO.toKqScope(curShift, preShift, nextShift, curDate, curTimeZone,preTimeZone,nextTimeZone);
	}
	
	
	/**
	 * 得到某天的考勤时间段
	 * @param curDate
	 * @param psnCalendarMap
	 * @param shiftMap
	 * @return
	 * @throws BusinessException 
	 */
	public static ITimeScope getAttendTimeScope(
			UFLiteralDate preDate,UFLiteralDate curDate,UFLiteralDate nextDate,
			Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap,
			Map<String, ShiftVO> shiftMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException{
		return getAttendTimeScope(preDate, curDate, nextDate, psnCalendarMap, shiftMap, dateTimeZoneMap, false);
	}
	
	/**
	 * 得到某天的考勤时间段，专用于加班校验
	 * @param preDate
	 * @param curDate
	 * @param nextDate
	 * @param psnCalendarMap
	 * @param shiftMap
	 * @param dateTimeZoneMap
	 * @return
	 * @throws BusinessException
	 */
	public static ITimeScope getAttendTimeScope4OvertimeCheck(
			UFLiteralDate preDate,UFLiteralDate curDate,UFLiteralDate nextDate,
			Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap,
			Map<String, ShiftVO> shiftMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException{
		return getAttendTimeScope(preDate, curDate, nextDate, psnCalendarMap, shiftMap, dateTimeZoneMap, true);
	}
	
	protected static ITimeScope getAttendTimeScope(
			UFLiteralDate preDate,UFLiteralDate curDate,UFLiteralDate nextDate,
			Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap,
			Map<String, ShiftVO> shiftMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap,
			boolean is4OvertimeCheck) throws BusinessException{
		//当日班次
		AggPsnCalendar curCalendar = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(curDate);
		ShiftVO curShift = curCalendar==null?null:curCalendar.getPsnCalendarVO()==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, curCalendar.getPsnCalendarVO().getPk_shift());
		//前一日班次
		AggPsnCalendar preCalendar = preDate==null?null:(MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(preDate));
		ShiftVO preShift = preCalendar==null?null:preCalendar.getPsnCalendarVO()==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
		//后一日班次
		AggPsnCalendar nextCalendar = nextDate==null?null:(MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(nextDate));
		ShiftVO nextShift = nextCalendar==null?null:nextCalendar.getPsnCalendarVO()==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, nextCalendar.getPsnCalendarVO().getPk_shift());
		TimeZone curTimeZone = CommonUtils.ensureTimeZone(dateTimeZoneMap == null?null:dateTimeZoneMap.get(curDate));
		TimeZone preTimeZone = CommonUtils.ensureTimeZone(dateTimeZoneMap == null?null:dateTimeZoneMap.get(preDate));
		TimeZone nextTimeZone = CommonUtils.ensureTimeZone(dateTimeZoneMap == null?null:dateTimeZoneMap.get(nextDate));
		return is4OvertimeCheck?
				ShiftVO.toKqScope4OvertimeCheckAndGen(curShift, preShift, nextShift, curDate.toString(), curTimeZone,preTimeZone,nextTimeZone)
				:ShiftVO.toKqScope(curShift, preShift, nextShift, curDate.toString(), curTimeZone,preTimeZone,nextTimeZone);
	}
	
//	public static ITimeScope getAttendTimeScope(UFDate curDate,
//			Map<String, PsncalendarAllVO> psnCalendarMap,Map<String, BclbVO> bclbMap){
//		return getAttendTimeScope(curDate.getDateBefore(1).toString(), curDate.toString(), curDate.getDateAfter(1).toString(), psnCalendarMap, bclbMap);
//	}
//	
	/**
	 * @param hvos
	 * @return
	 */
	public static List<AwaybVO> toAwaybVOList(List<AggAwayVO> aggVOList){
		return toAwaybVOList(aggVOList.toArray(new AggAwayVO[0]));
	}
	
	public static AwaybVO[] toAwaybVOs(List<AggAwayVO> aggVOList){
		return toAwaybVOs(aggVOList.toArray(new AggAwayVO[0]));
	}
	
	public static List<AwaybVO> toAwaybVOList(AggAwayVO[] aggVOs){
		List<AwaybVO> scopes = new ArrayList<AwaybVO>();
		for(int i = 0;i<aggVOs.length;i++){
			scopes.addAll(Arrays.asList(aggVOs[i].getBodyVOsExceptDelete()));
		}
		return scopes;
	}
	
	public static AwaybVO[] toAwaybVOs(AggAwayVO[] aggVOs){
		return toAwaybVOList(aggVOs).toArray(new AwaybVO[0]);
	}
	
	public static List<ChangeShiftbVO> toChangeShiftbVOList(List<AggChangeShiftVO> aggVOList){
		return toChangeShiftbVOList(aggVOList.toArray(new AggChangeShiftVO[0]));
	}
	
	public static ChangeShiftbVO[] toChangeShiftbVOs(List<AggChangeShiftVO> aggVOList){
		return toChangeShiftbVOs(aggVOList.toArray(new AggChangeShiftVO[0]));
	}
	
	public static List<ChangeShiftbVO> toChangeShiftbVOList(AggChangeShiftVO[] aggVOs){
		List<ChangeShiftbVO> scopes = new ArrayList<ChangeShiftbVO>();
		for(int i = 0;i<aggVOs.length;i++){
			ChangeShiftbVO[] bvos = aggVOs[i].getChangeShiftbVOs();
			if(ArrayUtils.isEmpty(bvos))
				continue;
			for(ChangeShiftbVO bvo:bvos){
				if(bvo.getStatus()==VOStatus.DELETED)
					continue;
				bvo.setBill_code(aggVOs[i].getChangeShifthVO().getBill_code());//错误提示信息需要编码
				scopes.add(bvo);
			}
		}
		return scopes;
	}
	
	public static ChangeShiftbVO[] toChangeShiftbVOs(AggChangeShiftVO[] aggVOs){
		return toChangeShiftbVOList(aggVOs).toArray(new ChangeShiftbVO[0]);
	}
	
	/**
	 * @param hvos
	 * @return
	 */
	public static List<LeavebVO> toLeavebVOList(List<AggLeaveVO> aggVOList){
		return toLeavebVOList(aggVOList.toArray(new AggLeaveVO[0]));
	}
	
	public static LeavebVO[] toLeavebVOs(List<AggLeaveVO> aggVOList){
		return toLeavebVOs(aggVOList.toArray(new AggLeaveVO[0]));
	}
	
	public static List<LeavebVO> toLeavebVOList(AggLeaveVO[] aggVOs){
		List<LeavebVO> scopes = new ArrayList<LeavebVO>();
		for(int i = 0;i<aggVOs.length;i++){
			scopes.addAll(Arrays.asList(aggVOs[i].getBodyVOsExceptDelete()));
		}
		return scopes;
	}
	
	public static LeavebVO[] toLeavebVOs(AggLeaveVO[] aggVOs){
		return toLeavebVOList(aggVOs).toArray(new LeavebVO[0]);
	}
	
	public static LeaveoffVO[] toLeaveoffVOs(AggLeaveoffVO[] aggvos){
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		LeaveoffVO[] leaveoffvos = new LeaveoffVO[aggvos.length];
		for(int i=0;i<aggvos.length;i++){
			leaveoffvos[i] = aggvos[i].getLeaveoffVO();
		}
		return leaveoffvos;
	}
	public static AwayOffVO[] toAwayOffVOs(AggAwayOffVO[] aggvos){
		if(ArrayUtils.isEmpty(aggvos))
			return null;
		AwayOffVO[] awayoffvos = new AwayOffVO[aggvos.length];
		for(int i=0;i<aggvos.length;i++){
			awayoffvos[i] = aggvos[i].getAwayOffVO();
		}
		return awayoffvos;
	}
	
	/**
	 * @param hvos
	 * @return
	 */
	public static List<OvertimebVO> toOvertimebVOList(List<AggOvertimeVO> aggVOList){
		return toOvertimebVOList(aggVOList.toArray(new AggOvertimeVO[0]));
	}
	
	public static OvertimebVO[] toOvertimebVOs(List<AggOvertimeVO> aggVOList){
		return toOvertimebVOs(aggVOList.toArray(new AggOvertimeVO[0]));
	}
	
	public static List<OvertimebVO> toOvertimebVOList(AggOvertimeVO[] aggVOs){
		List<OvertimebVO> scopes = new ArrayList<OvertimebVO>();
		for(int i = 0;i<aggVOs.length;i++){
			scopes.addAll(Arrays.asList(aggVOs[i].getBodyVOsExceptDelete()));
		}
		return scopes;
	}
	
	public static OvertimebVO[] toOvertimebVOs(AggOvertimeVO[] aggVOs){
		return toOvertimebVOList(aggVOs).toArray(new OvertimebVO[0]);
	}
//	/**
//	 * @param hvos
//	 * @return
//	 */
//	public static List<ShutdownbVO> toShutdownSubVOList(List<ShutdownhVO> hvos){
//		return toShutdownSubVOList(hvos.toArray(new ShutdownhVO[0]));
//	}
//	public static List<ShutdownbVO> toShutdownSubVOList(ShutdownhVO[] hvos){
//		List<ShutdownbVO> scopes = new ArrayList<ShutdownbVO>();
//		for(int i = 0;i<hvos.length;i++){
//			scopes.addAll(Arrays.asList(hvos[i].getShutdownbVOs()));
//		}
//		return scopes;
//	}
//	
//	
//	/**
//	 * 找出某个时间点所属的班次的日期
//	 * @param time
//	 * @param psnCalendarMap，人员的工作日历map，key是日期
//	 * @param bclbMap，班次map，key是班次主键
//	 * @return
//	 */
//	public static UFDate findBelongtoCalendar(UFDateTime time,
//			Map<String, PsncalendarAllVO> psnCalendarMap,Map<String, BclbVO> bclbMap){
//		UFDate date = time.getDate();
//		//从前1天到后1天寻找
//		UFDate searchStartDate = date.getDateBefore(1);
//		UFDate searchEndDate = date.getDateAfter(1);
//		for(UFDate curDate = searchStartDate;!curDate.after(searchEndDate);curDate = curDate.getDateAfter(1)){
//			ITimeScope attendTimeScope = 
//				getAttendTimeScope(curDate, psnCalendarMap, bclbMap);
//			if(attendTimeScope!=null&&TimeScopeUtils.contains(attendTimeScope, time))
//				return curDate;
//		}
//		//走到这里，说明没有找到
//		return null;
//	}
//	
	/**
	 * 将某人的班次按规则在时间轴上连起来，连接的规则是：
	 * 如果某日排了班，前一天也排了班，则此天的班次的考勤开始时间往前推，前一天的班次的考勤结束时间往后推，直到连在一起。
	 * 后一天也排了班，则此天的班次的考勤结束时间往后推，后一天的班次的考勤开始时间往前推，直到连在一起。
	 * 如果前一天没有排班，则开始时间不往前推；如果后一天没有排班，则结束时间不往后推。停止考勤这种班次视为没有排班
	 * 这样做了之后，凡是连续排班的日期段，考勤时间段在时间轴上就会连成一整个时间段。而如果中间有日期没有排班，则会出现空白段
	 * 简单地说，就是如果连续两天都有排班，那么前一天的考勤开始时间到后一天的考勤结束时间要连成一个时间段
	 * 例子：某人的排班情况如下（假设白班的考勤时间段是3:00-23:00）：
	 * 1日白班（之前都没有排班）,2日没有排班，3，4，5日都排白班，6日开始又没有排班
	 * 那么经过上述的连接时间段的操作后，排班的时间段范围就是：1日3:00-23:00，3日3:00-5日23:00
	 * 
	 * 此方法主要用于休假和停工的工作日历是否完整的判断
	 * @param calendarMap，key是日期
	 * @param shiftMap，key是班次主键
	 * @return
	 * @throws DAOException 
	 */
	public static ITimeScope[] mergeToTimeScope(
			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, ShiftVO> shiftMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws DAOException{
		if(calendarMap==null||calendarMap.size()==0)
			return null;
		//将日期排序
		UFLiteralDate[] dates = calendarMap.keySet().toArray(new UFLiteralDate[0]);
		Arrays.sort(dates);
		//将这些日期分成几个组，满足以下几个条件
		//1.组内的日期是连续的
		//2.组之间的日期是不连续的
		//3.组内的日期都有排班（正常班和公休都算排班，停止考勤算没有排班）
		List<List<UFLiteralDate>> groupedDates = new ArrayList<List<UFLiteralDate>>();
		for(int i = 0;i<dates.length;i++){
			UFLiteralDate date = dates[i];
			//首先看这一天是不是排班了（正常班和公休算，停止考勤不算）
			AggPsnCalendar calendar = calendarMap.get(date);
			if(calendar==null||calendar.getPsnCalendarVO()==null||StringUtils.isEmpty(calendar.getPsnCalendarVO().getPk_shift()))
				continue;
			//走到这里，说明这天排了正常班或者公休
			//如果groupedDates中还是空的
			if(groupedDates.size()==0){
				List<UFLiteralDate> group = new ArrayList<UFLiteralDate>();
				group.add(date);
				groupedDates.add(group);
				continue;
			}
			//groupedDates不是空的，看date能不能加到groupedDates的最后一个日期组中去
			List<UFLiteralDate> lastGroup = groupedDates.get(groupedDates.size()-1);
			//如果date和最后一个组是连续的，则加到最后一个组，否则新开一个组
			if(UFLiteralDate.getDaysBetween(lastGroup.get(lastGroup.size()-1), date)==1){
				lastGroup.add(date);
				continue;
			}
			List<UFLiteralDate> group = new ArrayList<UFLiteralDate>();
			group.add(date);
			groupedDates.add(group);
		}
		//上面这个for循环走完后，日期已经按规则被分组了
		if(groupedDates.size()==0)
			return null;
		//按组循环处理，每一个组都生成一个时间段
		List<ITimeScope> retScopeList = new ArrayList<ITimeScope>();
		for(int i = 0;i<groupedDates.size();i++){
			List<UFLiteralDate> group = groupedDates.get(i);
			//如果此日期组只有一天，那么就是这天的考勤时间段的开始时间和结束时间作为一个时间段
			if(group.size()==1){
				PsnCalendarVO calendar = calendarMap.get(group.get(0)).getPsnCalendarVO();
				String pkShift = calendar.getPk_shift();
				//如果是公休
				if(ShiftVO.PK_GX.equals(pkShift)){
					retScopeList.add(TimeScopeUtils.toFullDay(calendar.getCalendar().toString()));
					continue;
				}
				//如果不是公休，则使用班次的考勤开始时间和结束时间
				//容错处理，一般情况下，shiftMap里面会包含所有可能出现的pk_shift，但有可能在一些人事变动点前后产生一些盲区，如果出现盲区，则需要容错
				ShiftVO shiftVO = getShiftVOFromMap(shiftMap, pkShift);
				retScopeList.add(TimeScopeUtils.toTimeScope(shiftVO.toRelativeKqScope(), calendar.getCalendar().toString(),dateTimeZoneMap.get(calendar.getCalendar())));
				continue;			
			}
			//如果不只一天，则取第一天的考勤开始时间和最后一天的考勤结束时间，作为一个时间段
			PsnCalendarVO firstCalendar = calendarMap.get(group.get(0)).getPsnCalendarVO();
			String firstPkShift = firstCalendar.getPk_shift();
			//如果第一天是公休，则开始时间是零点
			UFDateTime beginDT = null;
			if(ShiftVO.PK_GX.equals(firstPkShift)){
				beginDT = new UFDateTime(firstCalendar.getCalendar()+" 00:00:00");
			}
			//如果不是公休，则开始时间是班次的考勤开始时间
			else{
				//容错处理，一般情况下，shiftMap里面会包含所有可能出现的pk_shift，但有可能在一些人事变动点前后产生一些盲区，如果出现盲区，则需要容错
				ShiftVO shiftVO = getShiftVOFromMap(shiftMap, firstPkShift);
				beginDT = TimeScopeUtils.toTimeScope(shiftVO.toRelativeKqScope(), firstCalendar.getCalendar().toString(),dateTimeZoneMap.get(firstCalendar.getCalendar())).getScope_start_datetime();
			}
			PsnCalendarVO preCalendar = calendarMap.get(group.get(group.size()-1)).getPsnCalendarVO();
			String prePkShift = preCalendar.getPk_shift();
			//如果最后一天是公休，则结束时间是23:59:59
			UFDateTime endDT = null;
			if(ShiftVO.PK_GX.equals(prePkShift)){
				endDT = new UFDateTime(preCalendar.getCalendar()+" 23:59:59");
			}
			//如果不是公休，则开始时间是班次的考勤开始时间
			else{
				//容错处理，一般情况下，shiftMap里面会包含所有可能出现的pk_shift，但有可能在一些人事变动点前后产生一些盲区，如果出现盲区，则需要容错
				ShiftVO shiftVO = getShiftVOFromMap(shiftMap, prePkShift);
				endDT = TimeScopeUtils.toTimeScope(shiftVO.toRelativeKqScope(), preCalendar.getCalendar().toString(),dateTimeZoneMap.get(preCalendar.getCalendar())).getScope_end_datetime();
			}
			retScopeList.add(new DefaultTimeScope(beginDT,endDT,ShiftVO.PK_GX.equals(prePkShift)));
		}
		return TimeScopeUtils.mergeTimeScopes(retScopeList.toArray(new ITimeScope[0]));
	}
	
	private static ShiftVO getShiftVOFromMap(Map<String, ShiftVO> shiftMap,String pk_shift) throws DAOException{
		ShiftVO shiftVO = null;
		if(MapUtils.isNotEmpty(shiftMap)&&shiftMap.containsKey(pk_shift))
			shiftVO = shiftMap.get(pk_shift);
		else if(StringUtils.isNotBlank(pk_shift)){
//			shiftVO = (ShiftVO) new BaseDAO().retrieveByPK(ShiftVO.class, pk_shift);
			try {
				shiftVO = ShiftMaintainFacade.queryShiftByPk(pk_shift).getShiftVO();
			} catch (BusinessException e) {
				Logger.error(e.getMessage(),e);
			}
			shiftMap.put(pk_shift, shiftVO);
		}
		return shiftVO;
	} 
}
