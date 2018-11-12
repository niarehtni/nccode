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
 * �Ե��ݽ��д���İ����࣬��Ҫ���ڼ���ʱ��
 * ע�⣬���㵥��ʱ��ʱ����������˵��԰࣬Ҫ�Ƚ����԰���й̻������಻����̻���Ҳ����˵���ڵ��ô���ķ���֮ǰ�����뱣֤
 * ���԰��Ѿ����̻�
 */
public class BillProcessHelper {
	
	
	/**
	 * �������е��ݵĽ���
	 * @return
	 */
	public static ITimeScopeWithBillType[] crossAllBills(ITimeScopeWithBillType[]... bills){
		ITimeScopeWithBillType[] bigArray = mergeBillArray(bills);
		if(bigArray==null||bigArray.length==0)
			return bigArray;
		return (ITimeScopeWithBillType[]) TimeScopeUtils.crossTimeScopes(bigArray);
	}
	/**
	 * �������е��ݵĽ���
	 * @return
	 */
	public static ITimeScopeWithBillType[] crossAllBills(List<? extends ITimeScopeWithBillType>... bills){
		List<ITimeScopeWithBillType> bigList = mergeBillList(bills);
		if(bigList==null||bigList.size()==0)
			return null;
		return (ITimeScopeWithBillType[]) TimeScopeUtils.crossTimeScopes(bigList.toArray(new ITimeScopeWithBillType[0]));
	}
	
	/**
	 * �����е��ݵ�ʱ��β�����
	 * @return
	 */
	public static ITimeScope[] mergeAllBills(ITimeScopeWithBillType[]... bills){
		ITimeScopeWithBillType[] bigArray = mergeBillArray(bills);
		if(bigArray==null||bigArray.length==0)
			return bigArray;
		return TimeScopeUtils.mergeTimeScopes(bigArray);
	}
	
	/**
	 * �����е��ݵ�����ϲ���һ��������
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
	 * �����е��ݵ�����ϲ���һ��������
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
	 * �����ձ�ʱ������ĳ�յ��ݼ�����ʱ�����������ĵ����Ѿ��Ǹ��ݳ�ͻ��������˵ĵ���
	 * workDayLength�ǹ�����ʱ�����ڿ��ڹ������涨�壬һ�������յ��ڼ���Сʱ
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
		if(timeItems==null){//û����� ��ȻҲ���ü����ݼ�
			return;
		}
		//���û���ݼٵ������ô���Ҳ��һ�ֿ��ܣ��Ǿ������ݼٵ������Ǻ�����������ʱ���ͻ���Ұ��ճ�ͻ����Ķ��壬���ֳ�ͻ�����ݼ٣�
		if(billList==null||billList.size()==0)
			return;
		if(curCalendar==null)
			return;
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		//�������û�й��������������ǲ����ڵģ����ݼ�ʱ��Ϊ0
//		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift()))
		//�����տ��ܻ��м�Ϊ�ݼٵ���� ������١���ٵ�
		if(psncalendarVO==null)
			return;
		//���ݼٵ�ʱ����빤��ʱ����ཻ
		ITimeScope[] workScopes = getWorkTimeScopes(psncalendarVO.getCalendar().toString(), 
				curCalendar, preShift, nextShift,
				curTimeZone,preTimeZone,nextTimeZone);
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList.toArray(new ITimeScopeWithBillType[0]), workScopes,new ITimeScopeWithBillType[0]);
		//���û���ཻ�Ĳ������ô���
		if(intersectionScopes==null||intersectionScopes.length==0)
			return;
		//ȡ�����������ݿ��ڼ�ʱ���Ǽ��ڸ����ڼ䣬���ǵ�һ���ڼ���ڵڶ����ڼ�,0-�ֱ�ǣ�1-��һ���ڵڶ�
		//���ڳ�����ݼٶ��п��ܵ��ô˷���������������ȡ��ͬ�Ĳ���
		Integer periodPara = null;
		if(timeItems[0] instanceof LeaveTypeCopyVO)
//			periodPara = (Integer)paraMap.get(LeaveConst.OVERPERIOD_PARAM);
			periodPara = timeRuleVO.getLevaeovperprtype();
		else
//			periodPara = (Integer)paraMap.get(AwayConst.PARA_AWAY_OVERPERIOD);
			periodPara = timeRuleVO.getAwayovperprtype();
		if(periodPara==null)//Ĭ�Ϸֱ��
			periodPara=0;
		for(TimeItemCopyVO item:timeItems){
			//�����ݼ����������ݼ�ʱ��
			TimeLengthWrapper result = calLeaveLength(item,intersectionScopes,curCalendar,curShift,timeRuleVO);
			//�����ڼ�
			String calcPeirod = null;
			if(periodPara==1){//����ǵ�һ�ڼ���ڵڶ��ڼ�
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
		if(timeItems==null){//û����� ��ȻҲ���ü����ݼ�
			return;
		}
		//���û���ݼٵ������ô���Ҳ��һ�ֿ��ܣ��Ǿ������ݼٵ������Ǻ�����������ʱ���ͻ���Ұ��ճ�ͻ����Ķ��壬���ֳ�ͻ�����ݼ٣�
		if(billList==null||billList.length==0)
			return;
		if(curCalendar==null)
			return;
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		//�������û�й��������������ǲ����ڵģ����ݼ�ʱ��Ϊ0
//		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift()))
		//�����տ��ܻ��м�Ϊ�ݼٵ���� ������١���ٵ�
		if(psncalendarVO==null)
			return;
		//���ݼٵ�ʱ����빤��ʱ����ཻ
		ITimeScope[] workScopes = getWorkTimeScopes(psncalendarVO.getCalendar().toString(), 
				curCalendar, preShift, nextShift,
				curTimeZone,preTimeZone,nextTimeZone);
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList, workScopes,new ITimeScopeWithBillType[0]);
		//���û���ཻ�Ĳ������ô���
		if(intersectionScopes==null||intersectionScopes.length==0)
			return;
		//ȡ�����������ݿ��ڼ�ʱ���Ǽ��ڸ����ڼ䣬���ǵ�һ���ڼ���ڵڶ����ڼ�,0-�ֱ�ǣ�1-��һ���ڵڶ�
		//���ڳ�����ݼٶ��п��ܵ��ô˷���������������ȡ��ͬ�Ĳ���
		Integer periodPara = null;
		if(timeItems[0] instanceof LeaveTypeCopyVO)
			periodPara = timeRuleVO.getLevaeovperprtype();
		
		if(periodPara==null)//Ĭ�Ϸֱ��
			periodPara=0;
		for(TimeItemCopyVO item:timeItems){
			if(!item.getPk_timeitem().equals(vo.getPk_leavetype()))
			{
				continue;
			}
			//�����ݼ����������ݼ�ʱ��
			TimeLengthWrapper result = calLeaveLength(item,intersectionScopes,curCalendar,curShift,timeRuleVO);
			//�����ڼ�
			String calcPeirod = null;
			if(periodPara==1){//����ǵ�һ�ڼ���ڵڶ��ڼ�
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
			//��ʼ�պͽ��������ڼ䣬���߿�ʼ�պͽ�������ͬһ���ڼ䣬continue
			if(beginPeriod==null||endPeriod==null||beginPeriod.equals(endPeriod))
				continue;
			//��ʼ�պͽ����ղ���ͬһ���ڼ䣬��Ҫ����
			//�����ǰ�պͽ���������ͬһ���ڼ䣬��continue
			String[] curDatePeriods = datePeriodMap.get(curDate);
			if(curDatePeriods==null||curDatePeriods[0]==null||endPeriod.equals(curDatePeriods[0]))
				continue;
			//�����ǰ�պͽ����ղ�����ͬһ���ڼ䣬�򷵻ص�ǰ�������ڼ����һ���ڼ�
			calcPeirod = curDatePeriods[1];
		}
		return calcPeirod;
	}
	
	/**
	 * ����ĳ���ݼ������ձ�ʱ����curCalendar�ǵ�ǰ�����յİ�Σ�lastCalendar�Ǽ�����ǰһ�յİ�Σ�nextCalendar�Ǽ����պ�һ�յİ��
	 * ���ص���Сʱ����������������ֱ��д�����ݿ⣬�����������ת��
	 * @param leaveItem
	 * @param processedBills,���еļӰ൥��������ݼٵ���ͣ������������֮��Ľ��,�Ѿ��빤��ʱ��ν���
	 * @param curCalendar
	 * @param lastCalendar
	 * @param nextCalendar
	 * @return
	 */
	public static TimeLengthWrapper calLeaveLength(TimeItemCopyVO leaveItem,ITimeScopeWithBillType[] intersectionScopes,
			AggPsnCalendar curCalendar,ShiftVO curShift,TimeRuleVO timeRuleVO){
		//added by zengcheng 2009.01.08,����û���Ű���������ʱ����Ϊ�����ݼ٣�����0����
		if(curCalendar==null)
			return TimeLengthWrapper.getZeroTimeLength();
		int gxComType = leaveItem.getGxcomtype()==null?0:leaveItem.getGxcomtype().intValue();//0���ݲ��ƣ�1���ݼ�
		//����ǹ����ҹ��ݲ����ݼ���ֱ�ӷ���0
		boolean isGx = ShiftVO.PK_GX.equals(curCalendar.getPsnCalendarVO().getPk_shift());
		if(isGx&&gxComType==0)
			return TimeLengthWrapper.getZeroTimeLength();
		String itemPk = leaveItem.getPk_timeitem();//��Ŀ����
		//���˳��������ͣ������¼٣���ʱ���
		ITimeScopeWithBillType[] filteredScopes = TimeScopeUtils.filterByItemPK(itemPk, intersectionScopes);
		if(filteredScopes==null||filteredScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		int timeitemUnit = leaveItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:leaveItem.getTimeitemunit().intValue();//2�ǰ�Сʱ���㣬1�ǰ���
		//����ǰ�Сʱ���㣬��ܼ򵥣�ֱ�Ӱ���������3600���ɣ����ڹ����������ƣ����ݵ��ݼ�ʱ�����ܳ������ڹ�����Ĺ�����ʱ����
		//���յ��ݼ�ʱ�������ݼ�ʱ����빤��ʱ��εĽ�����ʱ����ȥ��Ϣʱ��
		long seconds = TimeScopeUtils.getLength(filteredScopes);
		if(seconds==0)
			return TimeLengthWrapper.getZeroTimeLength();
		TimeLengthWrapper length = new TimeLengthWrapper();
		//��Сʱ�䵥λ-������Сʱ�ƵļӰ���𣬵�λ�Ƿ��ӣ���������ƵļӰ���𣬵�λ����
		double timeUnit = leaveItem.getTimeunit()==null?0:leaveItem.getTimeunit().doubleValue();
		double workDayLength = timeRuleVO.getDaytohour2();
		//ȡ������0���ϣ�1���£�2�������룬Ĭ��1��
		int roundMode = leaveItem.getRoundmode()==null?1:leaveItem.getRoundmode().intValue();
		//��Сʱ����
		if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
			double hours =  ((double)seconds)/ITimeScope.SECONDS_PER_HOUR;
			//����ǹ��ݣ���ʱ�����ܳ������ڹ����һ��������ʱ��
			length.originalLength = isGx?Math.min(hours, workDayLength):hours;
			hours = CommonMethods.processByDecimalDigitsAndRoundMode(CommonMethods.processByMinLengthAndRoundMode4HourUnit(timeUnit, roundMode, hours),timeRuleVO).doubleValue();
			//����ǹ��ݣ���ʱ�����ܳ������ڹ����һ��������ʱ��
			if(isGx)
				hours = Math.min(hours, workDayLength);
			length.processedLength=hours;
			length.originalLengthUseHour=length.originalLength;
			length.processedLengthUseHour=length.processedLength;
			return length;
		}
		//����ǰ�����㣬��Ƚϸ���
		//�����㷽ʽ������ǰ����������㣬��Сʱ��Ҫ���Կ��ڹ�����Ĺ�����ʱ��������ǰ�������㣬��Ҫ���԰��ʱ��
		int convertRule = leaveItem.getConvertrule()==null?TimeItemCopyVO.CONVERTRULE_TIME:leaveItem.getConvertrule().intValue();//���㷽ʽ
		double divLength = 0;//������������ʱ��
		//����ǹ��ݣ��������㷽ʽ�ǰ�����������
		if(curCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX)||convertRule==TimeItemCopyVO.CONVERTRULE_DAY)
			divLength = workDayLength;
		//����ǰ����ʱ������.ע�⣬�ǳ���shift���ʱ���������ǳ���psncalendar���ʱ�����������˸�Ů������İ�Σ��������ˣ���ôֻ�������٣�����������һ���
		else
			divLength = curShift.getGzsj().doubleValue();
		double days = seconds/(ITimeScope.SECONDS_PER_HOUR*divLength);
		//����ǹ��ݣ���ʱ�����ܳ���һ��
		if(isGx)
			days = Math.min(days, 1);
		if(roundMode==0||roundMode==1){
			DecimalFormat dcmFmt = new DecimalFormat("0.0000");
			days = new UFDouble(dcmFmt.format(days)).doubleValue();//����С��λ��
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
	 * ����ĳ��ͣ�����������ձ�ʱ����curCalendar�ǵ�ǰ�����յİ��
	 * ���ص���Сʱ����������������ֱ��д�����ݿ⣬�����������ת��
	 * @param shutdownItem
	 * @param processedBills,���еļӰ൥��������ݼٵ���ͣ������������֮��Ľ��
	 * @param curCalendar
	 * @return
	 */
	public static TimeLengthWrapper calShutdownLength(TimeItemCopyVO shutdownItem,ITimeScopeWithBillType[] intersectionScopes,
			AggPsnCalendar curCalendar,TimeRuleVO timeRuleVO){
		//���˳��������͵�ʱ���
		ITimeScopeWithBillType[] filteredScopes = TimeScopeUtils.filterByItemPK(shutdownItem.getPk_timeitem(), intersectionScopes);
		if(filteredScopes==null||filteredScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		if(curCalendar==null)
			return TimeLengthWrapper.getZeroTimeLength();
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		PsnWorkTimeVO[] workVOs = curCalendar.getPsnWorkTimeVO();
		//�������û�й��������������ǹ��ݻ��߲����ڵģ�����û�й���ʱ��Σ���ͣ������ʱ��Ϊ0
		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift())||ArrayUtils.isEmpty(workVOs))
			return TimeLengthWrapper.getZeroTimeLength(); 
		//���ͣ�����빤��ʱ��εĽ���
		intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(intersectionScopes, workVOs,new ITimeScopeWithBillType[0]);
		//���û�н���ʱ��Σ����ô���
		if(intersectionScopes==null||intersectionScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		double result = (((double)(TimeScopeUtils.getLength(intersectionScopes))))/ITimeScope.SECONDS_PER_HOUR;
		return new TimeLengthWrapper(result,CommonMethods.processByDecimalDigitsAndRoundMode(result,timeRuleVO).doubleValue());//����ͣ��������˵��ԭʼʱ��������ʱ����һ����
	}
	
//	/**
//	 * ���ݹ���ʱ��ε���Ϣ�����Ѿ��빤��ʱ��ν��н��������ʱ��Σ������Ҫ�ӽ���ʱ��ε�ʱ�䳤���п۳�����Ϣʱ��
//	 * 5.5�ڹ���ʱ�������������Ϣʱ���ĸ�����ݼٺ�ͣ����ʱ�����㶼��Ҫ�͹���ʱ��ν��������ݼٺ�ͣ���ļ��㶼Ҫ
//	 * ������Ϣʱ��
//	 * zengcheng 2008.09.27����ע�ͣ����������ۺ���Ϣʱ����ȥ������Ϊ������ܶ�����ϵĴ���
//	 * @param curCalendar
//	 * @param intersectionScopes
//	 * @return ��Ҫ�ں����ȥ����Ϣʱ������λΪ��
//	 */
//	private static long getRestTimeToMinus(PsncalendarAllVO curCalendar,ITimeScopeWithBillType[] intersectionScopes){
//		long restTimeToMinus = 0;//��Ҫ�ں����ȥ����Ϣʱ������λΪ�롣tbm_wt���wtresttime�ֶδ洢��Ҳ����
//		if(curCalendar==null)
//			return restTimeToMinus;
//		Vector<PsncalendarbVO> psncalendarbVOsVec = curCalendar.getPsncalendarbVOs();
//		if(psncalendarbVOsVec!=null&&psncalendarbVOsVec.size()>0){
//			for(PsncalendarbVO calendarbVO:psncalendarbVOsVec){
//				//�˹���ʱ�ε���Ϣʱ��,���ݿ��д洢���Ƿ��ӣ������������������ģ�����Ҫ����60
//				long wtresttime = calendarbVO.getWtresttime()==null?0:calendarbVO.getWtresttime().intValue()*ITimeScope.SECONDS_PER_MINUTE;
//				//�����Ϣʱ��Ϊ0�����ô���
//				if(wtresttime==0)
//					continue;
//				//�����Ϣʱ������0�����ں�����Ҫ��ȥmin(wtresttime,���ʱ�εĽ���ʱ��)
//				restTimeToMinus+=Math.min(wtresttime, TimeScopeUtils.getLength(TimeScopeUtils.intersectionTimeScopes(intersectionScopes, new ITimeScope[]{calendarbVO.toTimeScope(curCalendar.getPsncalendarVO().getCalendar().toString())})));
//			}
//		}
//		return restTimeToMinus;
//	}
	/**
	 * ����ĳ���Ӱ����ļӰ�ʱ����belongtoCurDateBillMap�ǹ�����ĳһ��ļӰ൥��map��key�ǼӰ൥��value�Ǵ˼Ӱ൥���������ݽ��к�Ľ������
	 * @param overtimeItem
	 * @param belongtoCurDateBillMap
	 * @param isMinusRestLength���Ƿ�۳�ת���ݵ�ʱ��
	 * @return
	 */
	public static TimeLengthWrapper calOvertimeLength(TimeItemCopyVO overtimeItem,Map<OvertimeCommonVO, 
			List<ITimeScopeWithBillType>> belongtoCurDateBillMap,TimeRuleVO timeRuleVO,boolean isMinusRestLength){
		TimeLengthWrapper result= new TimeLengthWrapper();
		for(OvertimeCommonVO vo:belongtoCurDateBillMap.keySet()){
			if(!overtimeItem.getPk_timeitem().equals(vo.getPk_overtimetype()))
				continue;
			TimeLengthWrapper tempResult=calOvertimeLength(overtimeItem, vo.getDeduct().doubleValue(), belongtoCurDateBillMap.get(vo),timeRuleVO);
			//��ʱ����Ϊ����ԭʼʱ����������ת����ʱ�������ڴ���ʱ��Ҫ�۳�ת����ʱ��
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
	 * ����ĳ���Ӱ൥��ʱ����billList���Ѿ����������ݽ��й������Ҽ�ȥ�˹���ʱ��εĵ���
	 * �˼��㲻�۳���ת���ݵ�ʱ����ֻ�۳��۳�ʱ��
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
		//1-�죬2-Сʱ,Ĭ��Сʱ
		int timeitemUnit = overtimeItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:overtimeItem.getTimeitemunit().intValue();
		//��Сʱ�䵥λ-������Сʱ�ƵļӰ���𣬵�λ�Ƿ��ӣ���������ƵļӰ���𣬵�λ����
		double timeUnit = overtimeItem.getTimeunit()==null?0:overtimeItem.getTimeunit().doubleValue();
		//ȡ������0���ϣ�1���£�2�������룬Ĭ��1��
		int roundMode = overtimeItem.getRoundmode()==null?TimeItemCopyVO.ROUNDMODE_DOWN:overtimeItem.getRoundmode().intValue();
		long seconds = TimeScopeUtils.getLength(billList.toArray(new ITimeScopeWithBillType[0]));
		if(seconds==0)
			return TimeLengthWrapper.getZeroTimeLength();
		//��ȥ�۳�ʱ��
		seconds = seconds-(int)(deduct*ITimeScope.SECONDS_PER_MINUTE);
		//�����ȥ�۳�ʱ���С�ڵ���0����ֱ�ӷ���0
		if(seconds<=0)
			return TimeLengthWrapper.getZeroTimeLength();
		//����ǰ�Сʱ����:
		if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
			double oriresult = ((double)seconds)/ITimeScope.SECONDS_PER_HOUR;
			double processedResult = CommonMethods.processByDecimalDigitsAndRoundMode(CommonMethods.processByMinLengthAndRoundMode4HourUnit(timeUnit, roundMode, oriresult),timeRuleVO).doubleValue();
			return new TimeLengthWrapper(oriresult,processedResult,oriresult,processedResult);
		}
		//����ǰ������
		double oriresult = seconds/(ITimeScope.SECONDS_PER_HOUR*timeRuleVO.getDaytohour2());
		double processedResult =CommonMethods.processByDecimalDigitsAndRoundMode(CommonMethods.processByMinLengthAndRoundMode4DayUnit(timeUnit, roundMode, oriresult),timeRuleVO).doubleValue();
		return new TimeLengthWrapper(oriresult,processedResult,oriresult*timeRuleVO.getDaytohour2(),processedResult*timeRuleVO.getDaytohour2());
	}
	/**
	 * ����ĳ��ĳ���ͣ������ʱ���������õ�ʱ������0����newһ���ձ��ӱ�vo�����뵽list��
	 * @param timeItems
	 * @param billList�����������ݽ��й�������û���빤��ʱ��ν�����ͣ����
	 * @param curCalendar
	 * @param pk_daystat
	 * @param statbVOList
	 */
	public static void processShutdownLength(TimeItemCopyVO[] timeItems,List<ITimeScopeWithBillType> billList,AggPsnCalendar curCalendar,
			List<DayStatbVO> statbVOList,String pk_daystat,TimeRuleVO timeRuleVO){
		//���û��ͣ���������ô���Ҳ��һ�ֿ��ܣ��Ǿ�����ͣ���������Ǻ�����������ʱ���ͻ���Ұ��ճ�ͻ����Ķ��壬���ֳ�ͻ����ͣ����
		if(billList==null||billList.size()==0)
			return;
		if(curCalendar==null)
			return;
		PsnCalendarVO psncalendarVO = curCalendar.getPsnCalendarVO();
		PsnWorkTimeVO[] workVOs = curCalendar.getPsnWorkTimeVO();
		//�������û�й��������������ǹ��ݻ��߲����ڵģ�����û�й���ʱ��Σ���ͣ������ʱ��Ϊ0
		if(psncalendarVO==null||ShiftVO.PK_GX.equals(psncalendarVO.getPk_shift())||ArrayUtils.isEmpty(workVOs))
			return; 
		//���ͣ�����빤��ʱ��εĽ���
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList.toArray(new ITimeScopeWithBillType[0]), workVOs,new ITimeScopeWithBillType[0]);
		//���û�н���ʱ��Σ����ô���
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
	 * ����ĳ��ļӰ൥��ʱ��
	 * �Ӱ൥��ʱ���������������ݲ�һ������������ʹ���еķ�ʽ��Ҳ����˵��һ�ŵ���������˼���Ļ���ʱ���Ƿ�ɢ�ڸ����
	 * ���Ӱ൥�ǹ�ķ�ʽ��һ�żӰ൥��ʱ������ȫ���������ŵ��������Ĺ������ϵģ�����̯��ÿ�졣
	 * ��ô�����ȷ��һ�żӰ൥��������һ���������أ�
	 * 1.����Ѱ����˼Ӱ൥ʱ�����ʱ�佻��ĵ�һ�������գ�����ҵ��ˣ�������ڴ˹�����
	 * 2.�����һ��û�ҵ�����ѼӰ൥��ʼʱ����ǰ��һ�죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ�����3
	 * 3.����ڶ���Ҳû�ҵ�����ѼӰ൥����ʱ��������һ�죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ�����4
	 * 4.�ѼӰ൥��ʼʱ����ǰ�����죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ�����5
	 * 5.�ѼӰ൥����ʱ�����������죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ����������ˣ����ؿ�
	 * ��˼���ĳ�յļӰ�ʱ���ķ�ʽ�ǣ��ҳ������ڴ�������мӰ൥��Ȼ���ÿ���Ӱ൥�������㣬Ȼ����ܵ�����
	 * ע�⣬�ձ��ļӰ�ʱ������Ҫ��ȥת���ݵ�ʱ���ġ��ǼǺ����������ڵ㲻��
	 * @param timeItems
	 * @param billList���Ѿ��͸��ֵ��ݽ��й��������Ѿ���ȥ�˹���ʱ��εĵ���
	 * @param curCalendar
	 * @param lastCalendar
	 * @param nextCalendar
	 * @param statbVOList
	 */
	public static void processOvertiemLength(TimeItemCopyVO[] timeItems,
			List<ITimeScopeWithBillType> billList,
			String curDate,Map<String, UFLiteralDate> belongtoDateMap,
			List<DayStatbVO> statbVOList,String pk_daystat,TimeRuleVO timeRuleVO){
		//���û�мӰ൥�����ô���Ҳ��һ�ֿ��ܣ��Ǿ����мӰ൥�����Ǻ�����������ʱ���ͻ���Ұ��ճ�ͻ����Ķ��壬���ֳ�ͻ����Ӱࣩ
		if(billList==null||billList.size()==0 ||timeItems ==null)
			return;
		//�����ڱ��յļӰ൥��key�ǼӰ�ԭʼ���ݣ�value�Ǵ�ԭʼ���ݶ�Ӧ�ı����й��ĵ���
		Map<OvertimeCommonVO, List<ITimeScopeWithBillType>> belongtoCurDateBillMap = new HashMap<OvertimeCommonVO, List<ITimeScopeWithBillType>>();
		//������ѭ������
		for(ITimeScopeWithBillType bill:billList){
			for(ITimeScopeWithBillType originalBill: bill.getOriginalTimeScopeMap().values()){
				if(!(originalBill instanceof OvertimeCommonVO))
					continue;
				OvertimeCommonVO overbvo = (OvertimeCommonVO)originalBill;
				UFLiteralDate belongdate = belongtoDateMap.get(overbvo.getPrimaryKey());
				if(belongdate==null||!curDate.equals(belongdate.toString()))
					break;
				//�˵������ڵ�ǰ����Ĺ�����
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
	 * ���ݹ����������ҳ�ĳ�żӰ൥�����Ĺ�����
	 * 1.����Ѱ����˼Ӱ൥ʱ�����ʱ�佻��ĵ�һ�������գ�����ҵ��ˣ�������ڴ˹�����
	 * 2.�����һ��û�ҵ�����ѼӰ൥��ʼʱ����ǰ��һ�죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ�����3
	 * 3.����ڶ���Ҳû�ҵ�����ѼӰ൥����ʱ��������һ�죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ�����4
	 * 4.�ѼӰ൥��ʼʱ����ǰ�����죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ�����5
	 * 5.�ѼӰ൥����ʱ�����������죬�����ʱ����Ƿ�����ĳ�������գ�����ҵ��ˣ�������ڴ˹����գ����������ˣ����ؿ�
	 * 2009.08.21���������ۣ����㷨��Ϊ
	 * 1.��ԭ�㷨��һ��һ��
	 * 2.���Ӱ࿪ʼʱ����ǰ��24Сʱ������ǰ�ƵĹ����У����������ĳ�������յĽ���ʱ�䣬���������������Ϊ�����գ�����3
	 * 3.���Ӱ����ʱ��������24Сʱ���������ƵĹ����У����������ĳ�������յĿ�ʼʱ�䣬���������������Ϊ�����գ�����4
	 * 4.��������ݵĿ�ʼʱ����������Ȼ����Ϊ�����ղ�����
	 * @param overtimebVO
	 * @param calendarMap
	 * @param shiftMap
	 * @return
	 * @throws BusinessException 
	 */
	public static UFLiteralDate findBelongtoDate(OvertimeCommonVO overtimeVO,Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, ShiftVO> shiftMap,UFLiteralDate[] dateArray,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException{
		//���ǵ�һ��Ѱ�ң��ҳ���Ӱ൥��ʱ�佻��������Ĺ����գ��ӵ��ݿ�ʼʱ��ǰ���쿪ʼѰ�ң��ҵ����ݽ���ʱ�������Ϊֹ
		UFLiteralDate date = findBelongtoDate0(overtimeVO, calendarMap, shiftMap, dateArray,true,dateTimeZoneMap);
		if(date!=null)
			return date;
		//�����ߵ������ʾ��һ��û���ҵ������գ���Ҫ�ѼӰ൥��ʱ�����һЩ����:��ʼʱ�����ǰŲһ�죬����ʱ���Ϊԭ���Ŀ�ʼʱ��,�ٴβ���,����ǲ����н��������
		UFLiteralDate beginDate = overtimeVO.getOvertimebegindate();
		OvertimeCommonVO cloneVO = (OvertimeCommonVO)overtimeVO.clone();
		cloneVO.setOvertimebegindate(beginDate.getDateBefore(1));
		cloneVO.setOvertimebegintime(DateTimeUtils.getDateTimeBeforeMills(cloneVO.getOvertimebegintime(), ICalendar.MILLIS_PER_DAY));
		cloneVO.setOvertimeenddate(beginDate);
		cloneVO.setOvertimeendtime(overtimeVO.getOvertimebegintime());
		date = findBelongtoDate0(cloneVO, calendarMap, shiftMap, dateArray,false,dateTimeZoneMap);
		if(date!=null)
			return date;
		//�ߵ������ʾ�ڶ���Ҳû���ҵ������գ���Ҫ�ѼӰ൥��ʱ�������һЩ����:����ʱ�������Ųһ�죬��ʼʱ���Ϊԭ���Ľ���ʱ��,�ٴβ���,����ǲ����н��������
		UFLiteralDate endDate = overtimeVO.getOvertimeenddate();
		cloneVO = (OvertimeCommonVO)overtimeVO.clone();
		cloneVO.setOvertimeenddate(endDate.getDateAfter(1));
		cloneVO.setOvertimeendtime(DateTimeUtils.getDateTimeAfterMills(overtimeVO.getOvertimeendtime(), ICalendar.MILLIS_PER_DAY));
		cloneVO.setOvertimebegindate(endDate);
		cloneVO.setOvertimebegintime(overtimeVO.getOvertimeendtime());
		date = findBelongtoDate0(cloneVO, calendarMap, shiftMap, dateArray,true,dateTimeZoneMap);
		if(date!=null)
			return date;
		//�ߵ����ﲻ���ܲ����ҵ������أ�������������
		return beginDate;
	}
	
	/**
	 * @param overtimebVO
	 * @param calendarMap
	 * @param shiftMap
	 * @param dateArray
	 * @param isAscSearch true ��ʾ�ҳ������ཻ�ģ�false ��ʾ�����ཻ��
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
			//�����εĿ���ʱ���
			ITimeScope curDateAttendTimeScope = getAttendTimeScope4OvertimeCheck(i==0?null:dateArray[i-1],
					curDate, i==dateArray.length-1?null:dateArray[i+1],calendarMap, shiftMap,dateTimeZoneMap);
			//�������Ŀ���ʱ�����Ӱ൥��ʱ����н�������ֱ�ӷ��ش���
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
	 * �Ӹ��ֵ��ݣ����������ݼټӰ�ͣ������ʱ��������У��ҳ���Ӧ���͵ĵ��ݡ�billType�����ǼӰ໹���ݼٻ��ǳ����ͣ�������BillMutexRule��ĳ�������
	 * ���ʱ����Ǽ��ֵ����ཻ���ɵģ������ݼٺͳ��������ô��Ҫ�ó�ͻ�������ж��Ƿ�Ӧ�����billType���͵�ʱ���
	 * @param bills
	 * @param billType
	 * @return
	 */
	public static List<ITimeScopeWithBillType> filterBills(ITimeScopeWithBillType[] bills,int billType,BillMutexRule billMutexRule){
		if(bills==null||bills.length==0)
			return null;
		List<ITimeScopeWithBillType> retList = new Vector<ITimeScopeWithBillType>(5);
		for(ITimeScopeWithBillType scope:bills){
			//�����ʱ��ο��Լ�ΪbillType���ͣ���BillMutexRule��ĳ������壩�ĵ��ݣ������retList�����if����˵�����billType���͵�ʱ��Σ�Ҳ����˵���billType
			//���ͣ��������������͵ĵ�����ʱ���ͻ�Ұ��ճ�ͻ���򲻼�ΪbillType��ʱ��Ρ���ͻ�������BillMutexRule.getMutexResult����
			//����billType��BillMutexRule.BILL_AWAY�������ͣ���ô���if����˵��Ӱ��ݼ�ͣ�����͵�ʱ��Σ����һ���˵��������������͵ĵ��ݽ��Ұ���ͻ���򲻼�Ϊ
			//�����ʱ���
			if(containsBillType(billMutexRule.getMutexResult(scope.getBillType()), billType))
				retList.add(scope);
		}
		return retList;
	}
	
	/**
	 * �Ӹ��ֵ��ݣ����������ݼټӰ�ͣ������ʱ��������У��ҳ��Ӱ����͵ĵ��ݡ����˳����ݺ��ټ�ȥ����ʱ��Σ�ʣ�µĲ����ܹ�����Ӱ�ʱ��������
	 * ���ڵ����������������ղ����ݼ٣���ô�Ӱ൥Ӧ�ÿ���¼�룬���ҿɼ���ʱ������������������ڿ�����Ŀ�ĳ�ͻ����ģ�
	 * ��Ϊ�����ͻ�������ݼٺͼӰ��ͻ�����ڵ��ҲҪ���ڹ����ղ����ݼٵ�ʱ�򣬼Ӱ൥����¼�롣
	 * ���ʱ���û�к��ݼ��ཻ����ֱ��ʹ�ó�ͻ����������ݼ��ཻ����Ҫ����
	 * �����ͻ����涨�ݼٺͼӰ಻��ͻ�����Ҽ�Ϊ�Ӱ࣬��ô�������κ����⴦��ֱ�ӵ���filterBills�������ɡ�������ļ������
	 * ��Ҫ����
	 * 1.��ͻ����涨�ݼٺͼӰ಻��ͻ����ֻ���ݼ٣����ƼӰ�
	 * 2.��ͻ����涨�ݼٺͼӰ��ͻ
	 * 
	 * v63��ӣ������Ű��Ϊ�Ӱ࣬��˼�ȥ����ʱ���ʱ������ʱ���Ҫ�ȼ�ȥ����ʱ��
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
		//ѭ������
		List<ITimeScopeWithBillType> retList = new ArrayList<ITimeScopeWithBillType>(5);
		for(ITimeScopeWithBillType scope:bills){
			//���ʱ���û�мӰ൥���ԣ����ÿ��ǣ�ֱ��continue����Ϊ�˷���ֻ�ǹ��˳��Ӱ�ʱ���
			if((scope.getBillType()&BILL_OVERTIME)!=BILL_OVERTIME)
				continue;
			//���ʱ���û�к��ݼٵ��ཻ������ó�ͻ������filterBills����һ�������������⴦�����ó�ͻ�����鼴��
			if((scope.getBillType()&BILL_LEAVE)!=BILL_LEAVE){
				if(containsBillType(billMutexRule.getMutexResult(scope.getBillType()), BILL_OVERTIME))
					retList.add(scope);
				continue;
			}
			//�����ߵ����scope�϶��������ݼٺͼӰ���ཻ�ĵ���
			//���ʱ��κ��ݼٵ��ཻ���򿴳�ͻ����涨��������Ƿ���Լ�Ϊ�Ӱ࣬�����Ϊ�Ӱ࣬��Ҳ�������⴦�����ó�ͻ�����鼴��
			if(containsBillType(billMutexRule.getMutexResult(scope.getBillType()), BILL_OVERTIME)){
				retList.add(scope);
				continue;
			}
			//����Ӱ���ݼ���ϲ���Ϊ�Ӱ࣬�򰴵����Ӧ��continue�ˣ�����������Ӱ�ʱ��Ρ����������Ƿ���ݼٸ�����ϸ�Ĺ涨���˸�
			//���ţ���������ռ��ݼ٣���ô��������������ղ����ݼ٣���ô��Ҫ���������Ļ�����Ҫ���ݼ���������
			for(TimeItemCopyVO leaveItemVO: leaveItemVOs){
				//������ʱ��β���������ݼ������continue
				if(!scope.belongsToTimeItem(leaveItemVO.getPk_timeitem()))
					continue;
				//0:���ݲ����ݼ٣�1�����ݼ��ݼ�
				int gxComType = leaveItemVO.getGxcomtype()==null?0:leaveItemVO.getGxcomtype().intValue();
				//����ǹ��ݼ��ݼ٣���ô������
				if(gxComType==1)
					break;
				//������ݼ��ݼ٣���ô��Ҫ���˼Ӱ�ʱ����Ƿ��ڹ��ݷ�Χ�ڣ�������ڹ��ݷ�Χ�ڣ�����������ڹ��ݷ�Χ�ڣ���Ҫ����
				if(MapUtils.isEmpty(calendarMap))
					break;
				//������ѭ������
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
					//����ǹ��ݣ���ȡ�Ӱ൥�빫�ݵĽ���
					ITimeScopeWithBillType[] intersection = 
						TimeScopeUtils.intersectionTimeScopes(new ITimeScopeWithBillType[]{scope}, 
								getWorkTimeScopes(dateArray[i].toString(), calendar, 
										preShiftVO, 
										nextShiftVO, 
										curTimeZone,preTimeZone,nextTimeZone),
										new ITimeScopeWithBillType[0]);
					if(intersection==null||intersection.length==0)
						continue;
					//�ѵ��Ӽ��뵽�����б���
					retList.addAll(Arrays.asList(intersection));
				}
			}
		}
		//���û�й���������������ļ�ȥ����ʱ��εĲ������ý���
		if(calendarMap==null||calendarMap.size()==0)
			return retList;
		//���յ�ʱ��Σ�Ҫ��ȥ����ʱ��Σ����ݼ�ͣ����ͬ���ݼ�ͣ���빤��ʱ��ε����㣬�Ƿ�ɢ��ÿһ��ģ����Ӱ൥���ڸտ�ʼ�Ͱ����еļӰ൥��ȥ���еĹ���ʱ��Σ�Ȼ����ϸ��ÿһ��ļӰ൥���ȣ�
		//������Ĺ���ʱ��ε�list
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
		//�������ʱ���Ϊ�գ���ֱ�ӷ���ԭʼ�ĵ���
		if(worktimeScopeList.size()==0)
			return retList;
		
		ITimeScope[] worktimeScops = worktimeScopeList.toArray(new ITimeScope[0]);
		//����Ҫ��ȥ����ʱ���
		ITimeScopeWithBillType[] billScopes = retList.toArray(new ITimeScopeWithBillType[0]);
		ITimeScopeWithBillType[] minusedScopes =  (ITimeScopeWithBillType[]) TimeScopeUtils.minusTimeScopes(billScopes, worktimeScops);
		if(!ArrayUtils.isEmpty(holidayScope)){
//		//����ʱ������Ƿ����м��գ����м�����Ҫ���Ǽ����Ű��Ϊ�Ӱ�����
			//V63��ӣ����ϣ�����ʱ�䡢����ʱ�䡢����ʱ�������ص���Ϊ�Ӱ��ʱ�䣩
			ITimeScope[] holidayWorktimeScope = TimeScopeUtils.intersectionTimeScopes(worktimeScops, holidayScope);
			ITimeScope[] holidayBillWorktimeScope = TimeScopeUtils.intersectionTimeScopes(billScopes, holidayWorktimeScope);
			minusedScopes = (ITimeScopeWithBillType[]) ArrayUtils.addAll(minusedScopes, holidayBillWorktimeScope);
		}
		if(minusedScopes==null||minusedScopes.length==0)
			return null;
		//��ʱ�Ӱ�ʱ��λ����ܺ��м����Ű��а�εĹ���ʱ��Σ��Ͳ���Ϊ�Ӱ�����°����ʱʱ���,��Щʱ�������ܼ�Ϊ�Ӱ�ʱ����
		//������ѭ������
		List<ITimeScope> rtScopeList = new Vector<ITimeScope>();//����ʱ���
		List<ITimeScope> notIncludList = new Vector<ITimeScope>();//��ζ������ʱ����Ϊ�Ӱ��ʱ����ʱ���
		for(UFLiteralDate date:calendarMap.keySet()){
			AggPsnCalendar calendarAllVO = calendarMap.get(date);
			if(calendarAllVO==null||ArrayUtils.isEmpty(calendarAllVO.getPsnWorkTimeVO())|| calendarAllVO.getPsnCalendarVO() == null||ShiftVO.PK_GX.equals(calendarAllVO.getPsnCalendarVO().getPk_shift()))
				continue;
			PsnWorkTimeVO[] workTimeVOs = calendarAllVO.getPsnWorkTimeVO();
			if(workTimeVOs.length>1){//ȡ����ʱ��
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
			//�ϰ��ӳٲ���Ϊ�Ӱ�Ĳ���
			if(workShift.getUseontmrule()!=null&&workShift.getUseontmrule().booleanValue()){
				ITimeScope notIncludBeforScope = new DefaultTimeScope(workShift.getOntmend().multiply(60).longValue(),workScope.getScope_start_datetime());
//				rtScopeList.add(notIncludBeforScope);
				notIncludList.add(notIncludBeforScope);
			}
			//�°��ӳ��в���Ϊ�Ӱ�Ĳ���
			if(workShift.getUseovertmrule()!=null&&workShift.getUseovertmrule().booleanValue()){
				ITimeScope notIncludAfterScope = new DefaultTimeScope(workScope.getScope_end_datetime(),workShift.getOvertmbegin().multiply(60).longValue());
//				rtScopeList.add(notIncludAfterScope);
				notIncludList.add(notIncludAfterScope);
			}
			
		}
//		if(rtScopeList==null||rtScopeList.size()==0)
//			return Arrays.asList(minusedScopes);
		if(!CollectionUtils.isEmpty(notIncludList)){
			//�۳���ʱ����Ϊ�Ӱ��ʱ����ʱ���
			minusedScopes = (ITimeScopeWithBillType[]) TimeScopeUtils.minusTimeScopes(minusedScopes, notIncludList.toArray(new ITimeScope[0]));
		}
		//�۳������Ű�Ĺ�����ʱ���
		if(!ArrayUtils.isEmpty(minusedScopes)&&!ArrayUtils.isEmpty(holidayScope)&&CollectionUtils.isNotEmpty(rtScopeList)){
			ITimeScope[] holidayrtScope = TimeScopeUtils.intersectionTimeScopes(rtScopeList.toArray(new ITimeScope[0]), holidayScope);
			minusedScopes = (ITimeScopeWithBillType[]) TimeScopeUtils.minusTimeScopes(minusedScopes, holidayrtScope);
		}
		if(minusedScopes==null||minusedScopes.length==0)
			return null;
		return Arrays.asList(minusedScopes);
	}
	
	/**
	 * ����ĳ�յĳ���ʱ����filteredVOs�Ѿ���Ӱ൥�ݼٵ�ͣ�������й�������û���빤��ʱ��ν���
	 * Ҫ�󴫽����Ĺ��������Ѿ��ǹ̻��˵ġ��������಻����̻�����
	 * ���ݳ����������ã������ʱ���������㷨���������ʱ��ȡ�빤���εĽ��������㷨���ݼ���ȫһ�����������ʱ����ȡ�빤���εĽ���������Ȼ���㷨
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
		//�����ȡ����������ȫ�����ݼٵ��㷨
		if(timeItem.getIsinterwt()!=null&&timeItem.getIsinterwt().booleanValue()){
			return calLeaveLength(timeItem, filteredVOs, curDate, curCalendar, 
					preShift, curShift, nextShift, 
					preTimeZone,curTimeZone,nextTimeZone,
					timeRuleVO);
		}
		return calAwayLength(timeItem, filteredVOs, curDate, curShift==null?null:curShift.getPrimaryKey(), timeRuleVO);
	}
	
	/**
	 * ����ĳ��ĳ��ĳ����ʱ���������õ�ʱ������0����newһ���ձ��ӱ�vo�����뵽list��
	 * �����������㷨��ȡ�빤��ʱ��εĽ���������ȡ����Ȼ�յĽ���
	 * �����Ȼ�ռ��㣬��ĳ�յĳ���ʱ���ǳ������һ��0�㵽23:59:59֮�佻�Ľ��
	 * @param timeItems
	 * @param billList���г����ʱ��Σ��Ѿ����������͵ĵ��ݱ���Ӱ��ݼ�ͣ�������ģ������Ѿ�����ͻ����ȥ���˺������������ͻ�Ҳ���Ϊ�����ʱ�䲿�֣�
	 * @param curCalendar
	 * @param statbVOList
	 */
	public static void processAwayLength(TimeItemCopyVO[] timeItems,List<ITimeScopeWithBillType> billList,String curDate,
			AggPsnCalendar curCalendar,
			ShiftVO preShift,ShiftVO curShift,ShiftVO nextShift,
			TimeZone preTimeZone,TimeZone curTimeZone,TimeZone nextTimeZone,
			List<DayStatbVO> statbVOList,Map<String, String[]> datePeriodMap,Map<String, Object> paraMap,
			String pk_daystat,TimeRuleVO timeRuleVO){
		if(timeItems==null){//���û�����ó��������ֱ�Ӳ��ü���
			return;
		}
		//�������Ϊ���ࣺ�빤��ʱ��ν�����Ϊһ�࣬����Ȼ�ս�����Ϊһ��
		//�빤��ʱ��ν����ģ��������ݼ���ͬ���㷨������Ȼ�ս����ģ�����ԭʼ�ĳ����㷨
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
		//�����빤�����ཻ������㷨���ݼٵļ���һģһ��
		if(wtInterTypeList.size()>0&&wtInterBillList.size()>0){
			processLeaveLength(wtInterTypeList.toArray(new TimeItemCopyVO[0]), wtInterBillList, curCalendar, 
					preShift, curShift, nextShift, 
					preTimeZone,curTimeZone,nextTimeZone,
					statbVOList, datePeriodMap, paraMap,  pk_daystat, timeRuleVO);
		}
		//��������Ȼ���ཻ�����
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
	 * �����ձ���ĳһ�����ĳ���
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
		//���û�г�������ô���Ҳ��һ�ֿ��ܣ��Ǿ����г�������Ǻ�����������ʱ���ͻ���Ұ��ճ�ͻ����Ķ��壬���ֳ�ͻ������
		if(billList==null||billList.size()==0)
			return;
		//ȡ�ð�Σ�û�Űఴ�����㣩
		String pk_shift = curCalendar==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO()==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO().getPk_shift();
		ITimeScope dateScope = TimeScopeUtils.toFullDay(curDate,dateTimeZoneMap.get(UFLiteralDate.getDate(curDate)));//��Ȼ�յ�ʱ�䷶Χ0�㵽23:59:59
		//ȡ�ó���ʱ�������Ȼ�յĽ���ʱ���
		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
			(billList.toArray(new ITimeScopeWithBillType[0]), new ITimeScope[]{dateScope},new ITimeScopeWithBillType[0]);
		//�������Ȼ�ս���֮�󳤶�Ϊ0�����ô���
		if(intersectionScopes==null||intersectionScopes.length==0)
			return;
		//ȡ�����������ݿ��ڼ�ʱ���Ǽ��ڸ����ڼ䣬���ǵ�һ���ڼ���ڵڶ����ڼ�,0-�ֱ�ǣ�1-��һ���ڵڶ�
//		Integer periodPara = (Integer)paraMap.get(AwayConst.PARA_AWAY_OVERPERIOD);
		Integer periodPara =  timeRuleVO.getAwayovperprtype();
		if(periodPara==null)//Ĭ�Ϸֱ��
			periodPara=0;
		//���������ѭ������
		for(TimeItemCopyVO item:timeItems){
			TimeLengthWrapper result = calAwayLength(item,intersectionScopes,curDate,pk_shift,timeRuleVO);
			//�����ڼ�
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
	 * ���ݳ���ݺ͵��յİ�μ��������ʱ��
	 * @param awayItem
	 * @param vo
	 * @param curDate
	 * @param curCalendar������������ֻ�������жϵ����Ƿ��ǹ��ݣ�û����������
	 * @return
	 */
//	public static TimeLengthWrapper calAwayLength(TimeItemCopyVO awayItem,AwayCommonVO vo,String curDate,
//			AggPsnCalendar curCalendar,double workDayLength,TimeZone timeZone){
//		ITimeScope dateScope = TimeScopeUtils.toFullDay(curDate,timeZone);//��Ȼ�յ�ʱ�䷶Χ0�㵽23:59:59
//		//ȡ�ó���ʱ�������Ȼ�յĽ���ʱ���
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
	 * ����ĳ�յ��ݼ�ʱ����filteredVOs�Ѿ���Ӱ൥���ͣ�������й�������û���빤��ʱ��ν���
	 * Ҫ�󴫽����Ĺ��������Ѿ��ǹ̻��˵ġ��������಻����̻�����
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
		//ȡ���ݼ�ʱ����빤���յĽ���ʱ���
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
//		//�������û�й��������������ǹ��ݻ��߲����ڵģ�����û�й���ʱ��Σ���ͣ������ʱ��Ϊ0
//		if(psncalendarVO==null||BclbVO.PK_GX.equals(psncalendarVO.getPk_class())||ArrayUtils.isEmpty(workVOs))
//			return TimeLengthWrapper.getZeroTimeLength(); 
//		//���ͣ�����빤��ʱ��εĽ���
//		ITimeScopeWithBillType[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes
//			(new ITimeScope[]{vo}, workVOs,new ITimeScopeWithBillType[0]);
//		//���û�н���ʱ��Σ����ô���
//		if(intersectionScopes==null||intersectionScopes.length==0)
//			return TimeLengthWrapper.getZeroTimeLength();
//		return calShutdownLength(shutdownItem,intersectionScopes,curCalendar);
//	}
	/**
	 * ����ĳ����������ʱ��
	 * intersectionScopes�����������͵��ݽ��й���������Ȼ�ս��������ʱ��Σ��Ѿ��������ϳ�ͻ�����ʱ�����ȥ
	 * ����9:00-17:00���14:00-17:00�ݼ٣������ͻ�����������ݼ����ݼ٣���ô14:00-17:00���ʱ���Ҫ�ӳ������ȥ��ֻʣ��9:00-13:59:59
	 * 5.5�ĳ����㷨����5.02��ȫһ�£�û����Сʱ����ȡ������
	 * ��Сʱ����ʱ������ǵ�����������ʵ��Сʱ����
	 *            ����ǿ��죬�����գ�12��ǰ������һ��������ʱ���㣬����0.5��������ʱ��
	 *                       β�գ�12��������һ��������ʱ���㣬����0.5��������ʱ��
	 *                       �м��գ�min(ʵ��ʱ����������ʱ��)
	 * �������ʱ����β������Ǹ���һ�죺ÿ�춼��һ����
	 *           ��β���������12��Ϊ�磺
	 *               ����ǵ�����������12��ǰ������0.5�죬12������ʱ0.5�죬12��ǰ����12��������1��
	 *               ����ǿ��죬 �����գ�12��ǰ������1�죬����0.5��
	 *                           β�գ�12��������1�죬����0.5��
	 *                           �м��գ�1��
	 *                      
	 * @param awayItem
	 * @param intersectionScopes
	 * @param curDate
	 * @return ����ʱ�������ڳ���ʱ�����������Ӱ����ڹ�ʱ�ļ��㣬��˳���ʱ����ԭʼʱ���ʹ���ʱ����ʱȡ��ͬ��ֵ�����Ժ��������ʱ���ٸ�
	 */
	private static TimeLengthWrapper calAwayLength(TimeItemCopyVO awayItem,ITimeScopeWithBillType[] intersectionScopes,
			String curDate,String pk_shift,TimeRuleVO timeRuleVO){
		int gxComType = awayItem.getGxcomtype()==null?0:awayItem.getGxcomtype().intValue();//0���ݲ��ƣ�1���ݼ�
		//����ǹ����Ҵ�����ݲ��Ƴ���򷵻�0
		if(ShiftVO.PK_GX.equals(pk_shift)&&gxComType==0)
			return TimeLengthWrapper.getZeroTimeLength();
		//���˳��������ͣ����籾�س����ʱ���
		ITimeScopeWithBillType[] filteredScopes = TimeScopeUtils.filterByItemPK(awayItem.getPk_timeitem(), intersectionScopes);
		if(filteredScopes==null||filteredScopes.length==0)
			return TimeLengthWrapper.getZeroTimeLength();
		//1-�죬2-Сʱ,Ĭ��Сʱ
		int timeitemUnit = awayItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:awayItem.getTimeitemunit().intValue();
		//��Сʱ�䵥λ-������Сʱ�ƵļӰ���𣬵�λ�Ƿ��ӣ���������ƵļӰ���𣬵�λ����
//		double timeUnit = awayItem.getTimeunit()==null?0:awayItem.getTimeunit().doubleValue();
		//ȡ������0���ϣ�1���£�2�������룬Ĭ��1��
//		int roundMode = awayItem.getRoundmode()==null?1:awayItem.getRoundmode().intValue();
		//����ʱ��ε�����
		//long seconds = TimeScopeUtils.getLengthAutoContainsLastSecond(filteredScopes);
		//����ǰ�Сʱ����
		double result = 0;
		double workDayLength = timeRuleVO.getDaytohour2();
		if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
			long seconds = 0;
//			boolean containsBeginEndInSameDay = false;//��Щʱ����У��Ƿ��е��������ĵ���
			for(ITimeScopeWithBillType filteredScope:filteredScopes){
				//��ǰʱ��εľ���ʱ��
				long curBillLength = TimeScopeUtils.getLength(filteredScope);
				int datePos = getDatePos(curDate, awayItem.getPk_timeitem(), filteredScope);
//				if(datePos == TimeScopeUtils.DATE_POS_FIRST_LAST)
//					containsBeginEndInSameDay = true;
				if(curBillLength==0)
					continue;
				//���и�󵥾���ԭʼ�ĵ���
				IBillInfo originalBill = (IBillInfo) filteredScope.getOriginalTimeScopeMap().get(awayItem.getPk_timeitem());
				switch(datePos){
				//����ǵ�������,��ʵ��ʱ�����㣨2012.5.28��zx���ۺ�ȷ�ϣ���Сʱ����ʱ��������������Ȼʱ�������ܳ������ڹ���Ĺ������ۺ�ʱ��,�ı���֮ǰ�����������ܳ���ʮ����Сʱ���㷨��
				case TimeScopeUtils.DATE_POS_FIRST_LAST:
					seconds+=Math.min(TimeScopeUtils.getLength(filteredScope),ITimeScope.SECONDS_PER_HOUR*workDayLength);
					break;
					//�м���,��ʵ��ʱ������
				case TimeScopeUtils.DATE_POS_MIDDLE:
					seconds+= Math.min(curBillLength,ITimeScope.SECONDS_PER_HOUR*workDayLength);
					break;
					//���գ���12��ǰ������һ��������ʱ��������0.5��������ʱ��
				case TimeScopeUtils.DATE_POS_FIRST:
					if(filteredScope.getScope_start_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")<0){
						seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength;
						break;
					}
					//��12�㣨������֮��ʼ��Ϊ����
					seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength/2;
					break;
					//β�գ�12����������Ϊ1��������ʱ��������0.5��������ʱ��
				case TimeScopeUtils.DATE_POS_LAST:
					if(filteredScope.getScope_end_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")>0){
						seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength;
						break;
					}
					//��12�㣨������֮ǰ������Ϊ����
					seconds+=ITimeScope.SECONDS_PER_HOUR*workDayLength/2;
					break;
				}
			}
			result = Math.min(workDayLength, ((double)seconds)/ITimeScope.SECONDS_PER_HOUR);
			double processedLen = Math.min(workDayLength, CommonMethods.processByDecimalDigitsAndRoundMode(((double)seconds)/ITimeScope.SECONDS_PER_HOUR, timeRuleVO).doubleValue());
			return new TimeLengthWrapper(result,processedLen,result,processedLen);
		}
		//����ǰ������
		int calculatetype = awayItem.getCalculatetype()==null?0:awayItem.getCalculatetype().intValue();//��β����㷽ʽ0��12��Ϊ��ư��죬1����һ��
		double days = 0;
		for(ITimeScopeWithBillType filteredScope:filteredScopes){
			//��ǰʱ��εľ���ʱ��
			long curBillLength = TimeScopeUtils.getLength(filteredScope);
			if(curBillLength==0)
				continue;
			int datePos = getDatePos(curDate, awayItem.getPk_timeitem(), filteredScope);
			//�����β������Ǹ���һ�죬��ÿ�춼��һ���
			if(calculatetype==1){//��β��ֱ��Ϊ1��
				if(datePos!=TimeScopeUtils.DATE_POS_OUT_OF_RANGE)
					days+=1;
				continue;
			}
			//���и�󵥾���ԭʼ�ĵ���
			IBillInfo originalBill = (IBillInfo) filteredScope.getOriginalTimeScopeMap().get(awayItem.getPk_timeitem());
			//�������β���������12��Ϊ�磬��Ҫ���Ƿ�����β��
			switch(datePos){
			//����ǵ�������,��12��ǰ������0.5�죬12������ʱ0.5�죬12��ǰ����12��������1��
			case TimeScopeUtils.DATE_POS_FIRST_LAST:
				if(filteredScope.getScope_end_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")<=0
						||filteredScope.getScope_start_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")>=0){
					days+=0.5;
					break;
				}
				days+=1;
				break;
				//�м���,��һ��
			case TimeScopeUtils.DATE_POS_MIDDLE:
				days+=1;
				break;
				//���գ���12��ǰ������һ�죬�������
			case TimeScopeUtils.DATE_POS_FIRST:
				if(filteredScope.getScope_start_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")<=0){
					days+=1;
					break;
				}
				//��12�㣨������֮��ʼ��Ϊ����
				days+=0.5;
				break;
				//β�գ�12����������Ϊ1��������ʱ��������0.5��������ʱ��
			case TimeScopeUtils.DATE_POS_LAST:
				if(filteredScope.getScope_end_datetime().toStdString(originalBill.getTimezone()).substring(11).compareTo("12:00:00")>=0){
					days+=1;
					break;
				}
				//��12�㣨������֮ǰ������Ϊ����
				days+=0.5;
				break;
			}
		}
		days=Math.min(1, days);
		double processedDays = Math.min(1, CommonMethods.processByDecimalDigitsAndRoundMode(days, timeRuleVO).doubleValue());
		return new TimeLengthWrapper(days,processedDays,days*timeRuleVO.getDaytohour2(),processedDays*timeRuleVO.getDaytohour2());
	}
	
	/**
	 * ���ݿ�����Ŀ��ֵ�Ϳ�����Ŀ�����������ձ��ӱ�vo�����ֵ����0��������vo
	 * @param pk_timeitem
	 * @param val
	 * @param statbVOList
	 * @param type:1.�ݼ�(���)  2.�Ӱ�  3.����(����)8:ͣ������
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
	 * �ж�curDate��ʾ��������scope��ʾ�ĵ��ݵ�ԭʼ������������λ�ã������컹��β�컹���м���
	 * @param scope
	 * @return
	 */
	private static int getDatePos(String curDate,String pk_timeitem,ITimeScopeWithBillType scope){
		//ԭʼ����
		ITimeScope originalBill = scope.getOriginalTimeScopeMap().get(pk_timeitem);
		if(originalBill==null)
			return TimeScopeUtils.DATE_POS_OUT_OF_RANGE;
		return TimeScopeUtils.getDatePos(curDate, originalBill,((IBillInfo)originalBill).getTimezone());
	}
	
	/**
	 * �õ�curCalendar��Ӧ�Ĺ���ʱ��Ρ����ڷǹ��ݰ�Σ����ǰ�ζ����ʱ��Ρ�
	 * ���ڹ��ݰ�Σ���Ҫ��ǰһ��İ�κͺ�һ��İ�Σ�
	 * ���ǰһ���ǹ��ݣ���ôcurCalendar��00:00:00��ʼ�������ǰһ����εĽ���ʱ�俪ʼ
	 * �����һ���ǹ��ݣ���ôcurCalendar��23:59:59���������򵽺�һ����εĿ�ʼʱ�����
	 * @param curCalendar
	 * @param lastCalendar
	 * @param nextCalendar
	 * @return
	 */
	public static ITimeScope[] getWorkTimeScopes(String curDate,AggPsnCalendar curCalendar,ShiftVO preShift,ShiftVO nextShift,
			TimeZone curTimeZone,TimeZone preTimeZone,TimeZone nextTimeZone){
		//���Ϊ�վͰ����ݴ���
		String pk_shift = curCalendar==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO()==null?ShiftVO.PK_GX:curCalendar.getPsnCalendarVO().getPk_shift();
		//������ǹ��ݣ����ǹ���ʱ��Σ����ÿ���ǰ����İ��
		if(!ShiftVO.PK_GX.equals(pk_shift)){
			return curCalendar.getPsnWorkTimeVO();
		}
		return new ITimeScope[]{ShiftVO.toKqScope(null, preShift, nextShift, curDate, curTimeZone,preTimeZone,nextTimeZone)};
	}
	
	/**
	 * �õ�ĳ��Ŀ���ʱ��Ρ���ˢ����ʼ��ˢ������ʱ���
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
	 * �õ�ĳ��Ŀ���ʱ���
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
	 * �õ�ĳ��Ŀ���ʱ��Σ�ר���ڼӰ�У��
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
		//���հ��
		AggPsnCalendar curCalendar = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(curDate);
		ShiftVO curShift = curCalendar==null?null:curCalendar.getPsnCalendarVO()==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, curCalendar.getPsnCalendarVO().getPk_shift());
		//ǰһ�հ��
		AggPsnCalendar preCalendar = preDate==null?null:(MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(preDate));
		ShiftVO preShift = preCalendar==null?null:preCalendar.getPsnCalendarVO()==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
		//��һ�հ��
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
				bvo.setBill_code(aggVOs[i].getChangeShifthVO().getBill_code());//������ʾ��Ϣ��Ҫ����
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
//	 * �ҳ�ĳ��ʱ��������İ�ε�����
//	 * @param time
//	 * @param psnCalendarMap����Ա�Ĺ�������map��key������
//	 * @param bclbMap�����map��key�ǰ������
//	 * @return
//	 */
//	public static UFDate findBelongtoCalendar(UFDateTime time,
//			Map<String, PsncalendarAllVO> psnCalendarMap,Map<String, BclbVO> bclbMap){
//		UFDate date = time.getDate();
//		//��ǰ1�쵽��1��Ѱ��
//		UFDate searchStartDate = date.getDateBefore(1);
//		UFDate searchEndDate = date.getDateAfter(1);
//		for(UFDate curDate = searchStartDate;!curDate.after(searchEndDate);curDate = curDate.getDateAfter(1)){
//			ITimeScope attendTimeScope = 
//				getAttendTimeScope(curDate, psnCalendarMap, bclbMap);
//			if(attendTimeScope!=null&&TimeScopeUtils.contains(attendTimeScope, time))
//				return curDate;
//		}
//		//�ߵ����˵��û���ҵ�
//		return null;
//	}
//	
	/**
	 * ��ĳ�˵İ�ΰ�������ʱ�����������������ӵĹ����ǣ�
	 * ���ĳ�����˰࣬ǰһ��Ҳ���˰࣬�����İ�εĿ��ڿ�ʼʱ����ǰ�ƣ�ǰһ��İ�εĿ��ڽ���ʱ�������ƣ�ֱ������һ��
	 * ��һ��Ҳ���˰࣬�����İ�εĿ��ڽ���ʱ�������ƣ���һ��İ�εĿ��ڿ�ʼʱ����ǰ�ƣ�ֱ������һ��
	 * ���ǰһ��û���Ű࣬��ʼʱ�䲻��ǰ�ƣ������һ��û���Ű࣬�����ʱ�䲻�����ơ�ֹͣ�������ְ����Ϊû���Ű�
	 * ��������֮�󣬷��������Ű�����ڶΣ�����ʱ�����ʱ�����Ͼͻ�����һ����ʱ��Ρ�������м�������û���Ű࣬�����ֿհ׶�
	 * �򵥵�˵����������������춼���Ű࣬��ôǰһ��Ŀ��ڿ�ʼʱ�䵽��һ��Ŀ��ڽ���ʱ��Ҫ����һ��ʱ���
	 * ���ӣ�ĳ�˵��Ű�������£�����װ�Ŀ���ʱ�����3:00-23:00����
	 * 1�հװ֮ࣨǰ��û���Űࣩ,2��û���Ű࣬3��4��5�ն��Űװ࣬6�տ�ʼ��û���Ű�
	 * ��ô��������������ʱ��εĲ������Ű��ʱ��η�Χ���ǣ�1��3:00-23:00��3��3:00-5��23:00
	 * 
	 * �˷�����Ҫ�����ݼٺ�ͣ���Ĺ��������Ƿ��������ж�
	 * @param calendarMap��key������
	 * @param shiftMap��key�ǰ������
	 * @return
	 * @throws DAOException 
	 */
	public static ITimeScope[] mergeToTimeScope(
			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<String, ShiftVO> shiftMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws DAOException{
		if(calendarMap==null||calendarMap.size()==0)
			return null;
		//����������
		UFLiteralDate[] dates = calendarMap.keySet().toArray(new UFLiteralDate[0]);
		Arrays.sort(dates);
		//����Щ���ڷֳɼ����飬�������¼�������
		//1.���ڵ�������������
		//2.��֮��������ǲ�������
		//3.���ڵ����ڶ����Űࣨ������͹��ݶ����Űֹ࣬ͣ������û���Űࣩ
		List<List<UFLiteralDate>> groupedDates = new ArrayList<List<UFLiteralDate>>();
		for(int i = 0;i<dates.length;i++){
			UFLiteralDate date = dates[i];
			//���ȿ���һ���ǲ����Ű��ˣ�������͹����㣬ֹͣ���ڲ��㣩
			AggPsnCalendar calendar = calendarMap.get(date);
			if(calendar==null||calendar.getPsnCalendarVO()==null||StringUtils.isEmpty(calendar.getPsnCalendarVO().getPk_shift()))
				continue;
			//�ߵ����˵������������������߹���
			//���groupedDates�л��ǿյ�
			if(groupedDates.size()==0){
				List<UFLiteralDate> group = new ArrayList<UFLiteralDate>();
				group.add(date);
				groupedDates.add(group);
				continue;
			}
			//groupedDates���ǿյģ���date�ܲ��ܼӵ�groupedDates�����һ����������ȥ
			List<UFLiteralDate> lastGroup = groupedDates.get(groupedDates.size()-1);
			//���date�����һ�����������ģ���ӵ����һ���飬�����¿�һ����
			if(UFLiteralDate.getDaysBetween(lastGroup.get(lastGroup.size()-1), date)==1){
				lastGroup.add(date);
				continue;
			}
			List<UFLiteralDate> group = new ArrayList<UFLiteralDate>();
			group.add(date);
			groupedDates.add(group);
		}
		//�������forѭ������������Ѿ������򱻷�����
		if(groupedDates.size()==0)
			return null;
		//����ѭ������ÿһ���鶼����һ��ʱ���
		List<ITimeScope> retScopeList = new ArrayList<ITimeScope>();
		for(int i = 0;i<groupedDates.size();i++){
			List<UFLiteralDate> group = groupedDates.get(i);
			//�����������ֻ��һ�죬��ô��������Ŀ���ʱ��εĿ�ʼʱ��ͽ���ʱ����Ϊһ��ʱ���
			if(group.size()==1){
				PsnCalendarVO calendar = calendarMap.get(group.get(0)).getPsnCalendarVO();
				String pkShift = calendar.getPk_shift();
				//����ǹ���
				if(ShiftVO.PK_GX.equals(pkShift)){
					retScopeList.add(TimeScopeUtils.toFullDay(calendar.getCalendar().toString()));
					continue;
				}
				//������ǹ��ݣ���ʹ�ð�εĿ��ڿ�ʼʱ��ͽ���ʱ��
				//�ݴ���һ������£�shiftMap�����������п��ܳ��ֵ�pk_shift�����п�����һЩ���±䶯��ǰ�����һЩä�����������ä��������Ҫ�ݴ�
				ShiftVO shiftVO = getShiftVOFromMap(shiftMap, pkShift);
				retScopeList.add(TimeScopeUtils.toTimeScope(shiftVO.toRelativeKqScope(), calendar.getCalendar().toString(),dateTimeZoneMap.get(calendar.getCalendar())));
				continue;			
			}
			//�����ֻһ�죬��ȡ��һ��Ŀ��ڿ�ʼʱ������һ��Ŀ��ڽ���ʱ�䣬��Ϊһ��ʱ���
			PsnCalendarVO firstCalendar = calendarMap.get(group.get(0)).getPsnCalendarVO();
			String firstPkShift = firstCalendar.getPk_shift();
			//�����һ���ǹ��ݣ���ʼʱ�������
			UFDateTime beginDT = null;
			if(ShiftVO.PK_GX.equals(firstPkShift)){
				beginDT = new UFDateTime(firstCalendar.getCalendar()+" 00:00:00");
			}
			//������ǹ��ݣ���ʼʱ���ǰ�εĿ��ڿ�ʼʱ��
			else{
				//�ݴ���һ������£�shiftMap�����������п��ܳ��ֵ�pk_shift�����п�����һЩ���±䶯��ǰ�����һЩä�����������ä��������Ҫ�ݴ�
				ShiftVO shiftVO = getShiftVOFromMap(shiftMap, firstPkShift);
				beginDT = TimeScopeUtils.toTimeScope(shiftVO.toRelativeKqScope(), firstCalendar.getCalendar().toString(),dateTimeZoneMap.get(firstCalendar.getCalendar())).getScope_start_datetime();
			}
			PsnCalendarVO preCalendar = calendarMap.get(group.get(group.size()-1)).getPsnCalendarVO();
			String prePkShift = preCalendar.getPk_shift();
			//������һ���ǹ��ݣ������ʱ����23:59:59
			UFDateTime endDT = null;
			if(ShiftVO.PK_GX.equals(prePkShift)){
				endDT = new UFDateTime(preCalendar.getCalendar()+" 23:59:59");
			}
			//������ǹ��ݣ���ʼʱ���ǰ�εĿ��ڿ�ʼʱ��
			else{
				//�ݴ���һ������£�shiftMap�����������п��ܳ��ֵ�pk_shift�����п�����һЩ���±䶯��ǰ�����һЩä�����������ä��������Ҫ�ݴ�
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
