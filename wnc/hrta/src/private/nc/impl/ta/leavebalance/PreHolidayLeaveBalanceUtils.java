package nc.impl.ta.leavebalance;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 处理前置假的一些工具方法
 * @author zengcheng
 *
 */
public class PreHolidayLeaveBalanceUtils {
	
	/**
	 * 休假时间段：beginDate-endDate
	 * 取与此时间段有交集的考勤年度(需考虑有效期延长)
	 * 适用于按年结算类别
	 * @param extendCount 有效期延长日期
	 * @param periodVOs 当前组织内所有考勤期间
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	public static String[] queryRelatedYearsWithExtendCount(int extendCount,PeriodVO[] periodVOs,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException{
		if(ArrayUtils.isEmpty(periodVOs))
			return null;
		beginDate = extendCount==0?beginDate:beginDate.getDateBefore(extendCount);
		// 将休假时间段往前移extendCount天，再取与此日期段有交集的考勤期间，即为所需结果
		IDateScope dateScope = new DefaultDateScope(beginDate, endDate);
		Set<String> yearSet = new HashSet<String>();
		for(PeriodVO periodVO:periodVOs){
			// 如果休假段与期间有交集则记录此年
			if(!DateScopeUtils.isCross(periodVO, dateScope))
				continue;
			yearSet.add(periodVO.getTimeyear());
		}
		if(CollectionUtils.isEmpty(yearSet))
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1641"
					/*@res "{0}没有对应的考勤期间!"*/, beginDate.toString()));
		String[] years = yearSet.toArray(new String[0]);
		Arrays.sort(years);
		return years;
	}
	
	/**
	 * 休假时间段：beginDate-endDate
	 * 取与此时间段有交集的入职年度(需考虑有效期延长)
	 * 适用于按入职日期结算类别
	 * @param extendCount 有效期延长日期
	 * @param pk_psndoc 人员基本信息主键
	 * @param hireDate 入职日期
	 * @param leaveBeginDate
	 * @param leaveEndDate
	 * @return
	 * @throws BusinessException
	 */
	public static String[] queryRelatedHireYearsWithExtendCount(int extendCount,String pk_psndoc,UFLiteralDate hireDate,UFLiteralDate leaveBeginDate,UFLiteralDate leaveEndDate) throws BusinessException{
		// 将休假时间段往前移extendCount天，再取与此日期段有交集的入职年，即为所需结果（为了算法的鲁棒性，额外再多移一天）
		UFLiteralDate beginDate = leaveBeginDate.getDateBefore(extendCount+1);
		UFLiteralDate endDate = leaveEndDate.getDateAfter(1);
		IDateScope billScope = new DefaultDateScope(beginDate, endDate);
		LeaveBalanceMaintainImpl impl = new LeaveBalanceMaintainImpl();
		int firstYear = endDate.getYear();
		Set<String> yearSet = new HashSet<String>();
		while(true){
			String year = Integer.toString(firstYear);
			firstYear--;
			UFLiteralDate hireBeginDate = impl.getHireBeginDate(year, hireDate);
			if(hireBeginDate.before(hireDate))//如果往前推的一年已经早于入职日期了，则不再往前推
				break;
			UFLiteralDate hireEndDate = impl.getHireEndDate(year, hireDate);
			//如果加上有效期延长的日期段，与休假范围有交集，则此年也需要
			if(DateScopeUtils.isCross(billScope, new DefaultDateScope(hireBeginDate, hireEndDate))){
				yearSet.add(year);
				continue;
			}
			// 如果无交集，且入职开始日期在单据结束日期之后，则可以继续往前找，否则不用往前找了（因为肯定不会再有交集了）
			if(hireBeginDate.after(endDate)){
				continue;
			}
			break;
		}
		if(CollectionUtils.isEmpty(yearSet))
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0238"/*@res "人员{0}在日期范围{1}到{2}内无法找到入职年!"*/, 
					CommonUtils.getPsnName(pk_psndoc),leaveBeginDate.toString(),leaveEndDate.toString()));
		String[] years = yearSet.toArray(new String[0]);
		Arrays.sort(years);
		return years;
	}
	
	/**
	 * 休假时间段：beginDate-endDate
	 * 取与此时间段有交集的考勤年度(需考虑有效期延长)
	 * 适用于按期间结算类别
	 * @param extendCount 有效期延长日期
	 * @param periodVOs 组织内所有考勤期间
	 * @param leaveBeginDate
	 * @param leaveEndDate
	 * @return
	 * @throws BusinessException
	 */
	public static PeriodVO[] queryRelatedPeriodsWithExtendCount(int extendCount,PeriodVO[] periodVOs,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException{
		if(ArrayUtils.isEmpty(periodVOs))
			return null;
		beginDate = extendCount==0?beginDate:beginDate.getDateBefore(extendCount);
		// 将休假时间段往前移extendCount天，再取与此日期段有交集的考勤期间，即为所需结果
		IDateScope dateScope = new DefaultDateScope(beginDate, endDate);
		Set<PeriodVO> periodSet = new HashSet<PeriodVO>();
		for(PeriodVO periodVO:periodVOs){
			// 如果休假段与期间有交集则记录此年
			if(!DateScopeUtils.isCross(periodVO, dateScope))
				continue;
			periodSet.add(periodVO);
		}
		if(CollectionUtils.isEmpty(periodSet))
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1641"
					/*@res "{0}没有对应的考勤期间!"*/, beginDate.toString()));
		PeriodVO[] periods = periodSet.toArray(new PeriodVO[0]);
		DateScopeUtils.sort(periods);
		return periods;
	}

	/**
	 * 对按年或者按入职日结算的假期结算记录进行排序，规则是：
	 * 若往期结余优先为Y，则order by 年度，类别。
	 * 若往期结余优先为N，但用户未输入年度，则与上面的排序一致
	 * 若用户输入了年度，则需要将用户输入的年度、输入的类别的leavebalancevo记录往前提，提到第一个输入的类别的leavebalance记录之前
	 * @param vos
	 * @param isPreHolFirst
	 * @param dependVOsAddSelf，类别需要遵从的顺序，即前置假在前
	 */
	static void sortByYearDate(LeaveBalanceVO[] vos,boolean isPreHolFirst,String userInputLeaveType,String userInputYear,LeaveTypeCopyVO[] dependVOsAddSelf){
		if(ArrayUtils.isEmpty(vos))
			return;
		Arrays.sort(vos, new YearComparator(dependVOsAddSelf));
		//如果往期结余假优先为Y,或者用户没有录入年度，则直接返回排序结果(有前置假的情况，也直接返回排序结果，因为往期结余假优先为N且有前置假的情况下，不能把当期假往前提，一提就会造成相同期间的记录，本类别在前置类别之前)
		if(isPreHolFirst||StringUtils.isEmpty(userInputYear)||dependVOsAddSelf.length>1)
			return;

		//若往期结余优先为N，且用户录入的年度，则需要把用户录入的类别（下面成为A类别）+期间的记录，往前挪到第一个A类别的结算记录之前
		int firstIndex = -1;//A类别的第一条结算记录的位置
		int userInputTypeYearIndex = -1;//用户录入的类别+期间的记录的位置
		for(int i=0;i<vos.length;i++){
			if(firstIndex>=0&&userInputTypeYearIndex>=0)
				break;
			LeaveBalanceVO vo = vos[i];
			if(firstIndex==-1&&vo.getPk_timeitem().equals(userInputLeaveType)){
				firstIndex = i;
			}
			if(vo.getPk_timeitem().equals(userInputLeaveType)&&vo.getCuryear().equals(userInputYear)){
				userInputTypeYearIndex=i;
			}
		}
		replace(vos, userInputTypeYearIndex, firstIndex);
	}

	static void sortByPeriod(LeaveBalanceVO[] vos,boolean isPreHolFirst,String userInputLeaveType,String userInputYear,String userInputMonth,LeaveTypeCopyVO[] dependVOsAddSelf){
		if(ArrayUtils.isEmpty(vos))
			return;
		Arrays.sort(vos, new PeriodComparator(dependVOsAddSelf));
		//如果往期结余假优先为Y,或者用户没有录入年度期间，则直接返回排序结果(有前置假的情况，也直接返回排序结果，因为往期结余假优先为N且有前置假的情况下，不能把当期假往前提，一提就会造成相同期间的记录，本类别在前置类别之前)
		if(isPreHolFirst||StringUtils.isEmpty(userInputYear)||StringUtils.isEmpty(userInputMonth)||dependVOsAddSelf.length>1)
			return;
		//若往期结余优先为N，且用户录入的年度期间，则需要把用户录入的类别（下面成为A类别）+期间的记录，往前挪到第一个A类别的结算记录之前
		int firstIndex = -1;//A类别的第一条结算记录的位置
		int userInputTypeYearIndex = -1;//用户录入的类别+期间的记录的位置
		for(int i=0;i<vos.length;i++){
			if(firstIndex>=0&&userInputTypeYearIndex>=0)
				break;
			LeaveBalanceVO vo = vos[i];
			if(firstIndex==-1&&vo.getPk_timeitem().equals(userInputLeaveType)){
				firstIndex = i;
			}
			if(vo.getPk_timeitem().equals(userInputLeaveType)&&vo.getCuryear().equals(userInputYear)&&vo.getCurmonth().equals(userInputMonth)){
				userInputTypeYearIndex=i;
			}
		}
		replace(vos, userInputTypeYearIndex, firstIndex);
	}

	/**
	 * 将数组中indexFrom位置的元素往前提到indexTo上,indexFrom大，indexTo小
	 * @param vos
	 * @param indexFrom
	 * @param indexTo
	 */
	private static void replace(Object[] vos,int indexFrom,int indexTo){
		if(indexFrom<0||indexTo<0||indexFrom<=indexTo)
			return;
		Object advanceVO = vos[indexFrom];
		Object temp = vos[indexTo];
		for(int i=indexTo+1;i<=indexFrom;i++){
			Object temp2 = vos[i];
			vos[i]=temp;
			temp = temp2;
		}
		vos[indexTo]=advanceVO;
	}

	/**
	 * 比较的两个结算记录，要么都是按年结算，要么都是按入职日结算，比较单纯
	 * 比较规则是，若年度不一样，则年度早的小
	 * 若年度一样，则按照期间的依赖顺序比较大小
	 * @author zengcheng
	 *
	 */
	private static class YearComparator implements Comparator<LeaveBalanceVO>{

		Map<String, Integer> indexMap = null;//<休假类别主键，休假类别的顺序号>

		public YearComparator(LeaveTypeCopyVO[] dependVOsAddSelf){
			indexMap = new HashMap<String, Integer>();
			for(int i=0;i<dependVOsAddSelf.length;i++){
				indexMap.put(dependVOsAddSelf[i].getPk_timeitem(), i);
			}
		}

		@Override
		public int compare(LeaveBalanceVO o1, LeaveBalanceVO o2) {
			int yearCompare = o1.getCuryear().compareTo(o2.getCuryear());
			if(yearCompare!=0)
				return yearCompare;
			Integer index1 = indexMap.get(o1.getPk_timeitem());
			Integer index2 = indexMap.get(o2.getPk_timeitem());
			return index1.compareTo(index2);
		}

	}

	/**
	 * 比较的两个结算记录，有可能是按年结算的，也有可能是按期间结算的
	 * 比较规则是：若年度不一样，则年度早的为小
	 * 若年度一样，但结算期间一个是年，一个是月，则年度的为小
	 * 若年度和结算周期都一样，则：都是按年结算时，按照休假类别的依赖顺序比较大小；都是按期间结算时，首先比较期间先后，期间早的小；期间一样的，按照休假类别的依赖顺序比较大小
	 * @author zengcheng
	 *
	 */
	private static class PeriodComparator implements Comparator<LeaveBalanceVO>{

		Map<String, Integer> indexMap = null;//<休假类别主键，休假类别的顺序号>
		Map<String, LeaveTypeCopyVO> voMap = null;//<休假类别主键，休假类别>

		public PeriodComparator(LeaveTypeCopyVO[] dependVOsAddSelf){
			indexMap = new HashMap<String, Integer>();
			voMap = new HashMap<String, LeaveTypeCopyVO>();
			for(int i=0;i<dependVOsAddSelf.length;i++){
				indexMap.put(dependVOsAddSelf[i].getPk_timeitem(), i);
				voMap.put(dependVOsAddSelf[i].getPk_timeitem(), dependVOsAddSelf[i]);
			}
		}

		@Override
		public int compare(LeaveBalanceVO o1, LeaveBalanceVO o2) {
			int yearCompare = o1.getCuryear().compareTo(o2.getCuryear());
			//年度不同，那么肯定早的在前面
			if(yearCompare!=0)
				return yearCompare;
			//年度相同，那么肯定按年结算的在按期间结算的前面
			int setPeriod1 = voMap.get(o1.getPk_timeitem()).getLeavesetperiod();
			int setPeriod2 = voMap.get(o2.getPk_timeitem()).getLeavesetperiod();
			if(setPeriod1==TimeItemCopyVO.LEAVESETPERIOD_YEAR&&setPeriod2==TimeItemCopyVO.LEAVESETPERIOD_MONTH)
				return -1;
			if(setPeriod2==TimeItemCopyVO.LEAVESETPERIOD_YEAR&&setPeriod1==TimeItemCopyVO.LEAVESETPERIOD_MONTH)
				return 1;
			//走到这里，两个结算记录是同一年的，并且同为按年结算/期间结算
			//如果同为按期间结算，则比较期间，如果期间不一样，则可以直接比较出大小。若期间还一样，则要比较类别的顺序
			if(setPeriod1==TimeItemCopyVO.LEAVESETPERIOD_MONTH){
				int monthCompare = o1.getCurmonth().compareTo(o2.getCurmonth());
				if(monthCompare!=0)
					return monthCompare;
				Integer index1 = indexMap.get(o1.getPk_timeitem());
				Integer index2 = indexMap.get(o2.getPk_timeitem());
				return index1.compareTo(index2);
			}
			//如果同为按年结算，则直接比较类别顺序即可
			Integer index1 = indexMap.get(o1.getPk_timeitem());
			Integer index2 = indexMap.get(o2.getPk_timeitem());
			return index1.compareTo(index2);
		}

	}

}