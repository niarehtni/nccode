package nc.impl.ta.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ArrayHelper;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.AwayServiceFacade;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.LeaveServiceFacade;
import nc.itf.ta.OverTimeServiceFacade;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.ShutdownServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.BillValidator;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.itf.ta.algorithm.ITimeScopeWithBillType;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.annotation.AggVoInfo;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.pf.annotation.HrPfInfo;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.annotation.IDColumn;
import nc.vo.ta.annotation.Table;
import nc.vo.ta.away.AggAwayVO;
import nc.vo.ta.away.AwayCommonVO;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.away.AwaybVO;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.bill.BillPeriodSealedException;
import nc.vo.ta.bill.IAgentPsn;
import nc.vo.ta.bill.IDateScopeBillBodyVO;
import nc.vo.ta.bill.ITimeScopeBillAggVO;
import nc.vo.ta.bill.ITimeScopeBillBodyVO;
import nc.vo.ta.bill.ITimeScopeBillHeadVO;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeGenVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.ITempTableConst;
import nc.vo.ta.shutdown.ShutdownRegVO;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignbVO;
import nc.vo.ta.timebill.annotation.BillCodeFieldName;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class BillValidatorAtServer {

	public BillValidatorAtServer() {
		// TODO 自动生成构造函数存根
	}

	/**
	 *
	 * @param pkCorp
	 * @param psnPkInSql
	 * @param bills
	 * @param billType
	 * @throws BusinessException
	 * 返回一个map，key是人员主键，value是此人员的有时间冲突的，但不影响保存的（即不违反冲突规则）其他单据，
	 * 按单据类型分类，放置在一个子map中，这个子map的key是单据类型，value是此单据类型的有时间重叠的单据。返回这个map的作用是在客户端显示，
	 * 以达到提示用户的作用
	 */
	private static <T extends ITimeScopeBillBodyVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>>
	checkBillsByMutexRule(
			String pk_org, String psndocInSQL, T[] bills, int billType)
			throws BusinessException {
		//找出单据数组的最早开始日期和最晚结束日期
		UFLiteralDate beginDate = DateScopeUtils.findEarliestBeginDate(bills);
		UFLiteralDate endDate = DateScopeUtils.findLatestEndDate(bills);
		//存储所有其他单据类型单据的map，key是在BillMutexRule中定义的单据类型常量，比如休假、加班。value是某个人的map，key是人员主键，value是单据
		//比如本方法如果处理休假单与其他单据的冲突，那么此map就会存储所有人的加班、出差和停工单,而不用查询休假单，因为只是需要和其他类型单据进行交集时间比较
		Map<Integer, Map> billMap = new HashMap<Integer, Map>();
		initBillMap(pk_org, psndocInSQL, beginDate, endDate, billMap, billType);
		//存储所有人这些天班次的数组，后面判断公休日加班时有用
		//所有人员的工作日历map
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, beginDate, endDate, psndocInSQL);
		//本公司所有的班次，后面判断公休日加班时有用
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		//效率优化,因为查询班次需要先查询所有业务单元很耗时
//		Map<String, AggShiftVO> shiftMap = null;
//		if(bills[0] instanceof LeaveCommonVO){
//			shiftMap = ((LeaveCommonVO)bills[0]).getAggShiftMap();
//			if(null == shiftMap){
//				shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//				((LeaveCommonVO)bills[0]).setAggShiftMap(shiftMap);
//			}
//		}else if(bills[0] instanceof OvertimeCommonVO){
//			shiftMap = ((OvertimeCommonVO)bills[0]).getAggShiftMap();
//			if(null == shiftMap){
//				shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//				((OvertimeCommonVO)bills[0]).setAggShiftMap(shiftMap);
//			}
//		}
//		else{
//			shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		}

		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate, 2, 2);
		//所有的休假类别，后面判断公休日加班时有用
		TimeItemCopyVO[] leaveItemVOs = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryLeaveCopyTypesByOrg(pk_org);
		//查找本公司的考勤规则，因为单据的冲突规则存储在考勤规则中
		TimeRuleVO timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		//冲突规则的map
		Map<Integer, Integer> ruleMap = BillMutexRule.parseBillMutexString(timeruleVO.getBillmutexrule());
		//返回的map，记录了与新增单据存在时间冲突，但不影响保存的单据。String是人员主键，Integer是单据类型，参见BillMutexRule的单据类型常量定义
		Map<String, Map<Integer, List<ITimeScopeWithBillInfo>>> retMap = new HashMap<String, Map<Integer, List<ITimeScopeWithBillInfo>>>();
		//冲突的map，记录了与新增单据存在时间冲突，并且导致不能保存的单据。String是人员主键，Integer是单据类型，参见BillMutexRule的单据类型常量定义
		Map<String, Map<Integer, List<ITimeScopeWithBillInfo>>> mutexMap = new HashMap<String, Map<Integer, List<ITimeScopeWithBillInfo>>>();
		//V6.1增加，人员的任职主键的map
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, allDates[0], allDates[allDates.length-1]);
		//用来抛异常的消息
		StringBuilder msg = new StringBuilder();
		//循环处理每一张单据
		for (int i = 0; i < bills.length; i++) {
			ITimeScopeWithBillInfo curBill = bills[i];
			String pk_psndoc = curBill.getPk_psndoc();//人员主键
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
			Map<UFLiteralDate, TimeZone> timeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeruleVO.getTimeZoneMap());
			//			String psnname = psnInfo[1];//人员姓名
			//取出冲突规则中定义的所有单据组合，一共11种：休假+出差，休假+加班....，等等等等
			//循环处理单据组合
			for (Integer billComp:ruleMap.keySet()) {
				//如果当前单据类型组合不包含billType,则不用处理
				if (!BillMutexRule.containsBillType(billComp, billType))
					continue;
				//取出当前新增单据类型以外的单据
				//要找出除billType类型以外的单据的类型,从单据组合billComp中减去billType即可
				//比如billType表示休假，而当前组合是休假+加班+出差，那么从当前单据组合中减去休假，只剩下加班和出差
				int[] otherBillTypes = BillMutexRule.parseCompBillType(billComp - billType);
				ITimeScopeWithBillType[] intersection = new ITimeScopeWithBillType[] { curBill };
				for (int otherBillType : otherBillTypes) {
					//取出此种类型的单据
					ITimeScopeWithBillInfo[] otherBillList = (ITimeScopeWithBillInfo[]) (billMap.get(otherBillType) == null ? null : billMap.get(otherBillType).get(pk_psndoc));
					intersection = TimeScopeUtils.intersectionTimeScopes(intersection, otherBillList,
							new ITimeScopeWithBillType[0]);
					//如果交集为空，则for循环没有必要往下走了，因为往下走的话，空和任何时间段数组交都是空，没有必要
					if (intersection == null || intersection.length == 0)
						break;
				}
				//如果时间段没有交集，则不用处理
				if (intersection == null || intersection.length == 0)
					continue;
				//如果有交集，那么又分两种情况：影响保存，或者不影响保存。如果影响保存，则要把影响保存的单据放入mutexMap，最终包含在异常中抛出；
				//如果不影响保存，则要把单据放入retMap，返回客户端，给客户端显示用
				//影响保存，是因为违反了冲突规则，或者不违反冲突规则，但是会导致加班单的加班时长需要重新计算
				//不影响保存，是因为不违反冲突规则，并且不会导致加班单的加班时长需要重新计算。或者违反了冲突规则，但是根据南孚电池的特殊需求，依然可以保存的情况
				int mutexResult = ruleMap.get(billComp);
				//分违反冲突规则和不违反冲突规则两种情况讨论
				//第一种情况，不违反冲突规则
				//如果当前单据类型的组合违反，按道理也不用处理，但是，有一个特殊的需求：如果加班单已经转调休，
				//且新增的冲突单据会影响加班单的时长，则新增的冲突单据不让保存。什么叫新增的冲突单据会影响加班
				//单时长？比如休假+加班，如果计为加班，或者计为休假+加班，那么新增一个与加班单有交集的单据，不会
				//影响已有的加班单的时长；而如果计为休假，不计为加班，则新增一个与加班单有交集的单据，会影响已有的
				//加班单的时长。对于这种时长的影响，如果加班单还没有转调休，那么是可以接受的；而如果加班单已经转
				//调休，则不让保存
				//5.5的处理方式是简单化：只要新增单据会影响到原有的加班单据的加班时长，就不让保存
				if (mutexResult != 0) {
					//如果当前单据类型组合中没有加班，则不用考虑上面这个复杂的逻辑，直接continue
					//或者新增单据是加班单，也不用考虑上面的这个复杂逻辑，直接continue(如果加班单是已有单据，而新增单据是其他类型的单据，则要考虑影响)
					//或者如果单据类型组合中有加班，但单据冲突的结果中，是计加班的，即新增单据不会影响已有加班单的时长
					//则也不用考虑上面这个复杂的逻辑，直接continue
					if (!BillMutexRule.containsOvertime(billComp) || billType == BillMutexRule.BILL_OVERTIME || BillMutexRule.containsOvertime(mutexResult)) {
						//将有时间冲突的单据信息加入返回map中，在客户端显示
						addIntersectionToMap(pk_psndoc, intersection, otherBillTypes, retMap);
						continue;
					}
					//如果加班单与这个新增单据的冲突结果是不计加班，那么加班单的时长肯定要受这个新增单据的影响
					//我们用如下的方式处理这个影响：
					//如果加班单还没有转调休，则重新计算加班单的时长
					//如果加班单已经转调休，那么不允许这个新增单据的保存，往外抛异常
					//出于算法简便性的考虑，5.5版暂时只用简单的实现：不考虑是否转调休，只要有交集时间段的加班单，就抛异常，对于未转调休的加班单，也不重新计算
					//					//首先看加班单与当前单据有没有交集，如果没有交集，则不用考虑，continue
					//					//取出加班单，然后取交集
					//					List<ITimeScopeWithBillType> overtimeBillList = (List<ITimeScopeWithBillType>)(billMap.get(BillMutexRule.BILL_OVERTIME)==null?null:billMap.get(BillMutexRule.BILL_OVERTIME).get(pk_psndoc));
					//					ITimeScopeWithBillType[] intersection = TimeScopeUtils.
					//					intersectionTimeScopes(new ITimeScopeWithBillType[]{curBill}, overtimeBillList.toArray(new ITimeScopeWithBillType[0]),new ITimeScopeWithBillType[0]);
					//					//如果没有交集，则不可能发生冲突，continue即可
					//					if(intersection==null||intersection.length==0)
					//						continue;
					//					//如果有交集，则要处理这个交集
					//					for(ITimeScopeWithBillType scope:intersection){}
					addIntersectionToMap(pk_psndoc, intersection, new int[] { BillMutexRule.BILL_OVERTIME }, mutexMap);
					continue;
				}
				//第二种情况，违反冲突规则
				//如果当前组合中包含billType类型的单据，并且定义为冲突，则需要从数据库中查找与之冲突的其他几种单据，
				//看有没有时间段交叉，如果有，则需要抛异常
				//代码走到这里，肯定是存在了违反冲突规则的交集时间段
				//如果时间段有交集，那么就表示这张单据违反了单据冲突规则，按道理是要抛出异常的。
				//但是由于南孚电池提了一个需求：如果公休日不计休假，则公休日加班单应该可以录入。
				//也就是说，即使冲突规则定义为休假和加班冲突，但只要休假定义的是公休日不计休假，那么在公休日还能录入加班单
				if (billComp.intValue() == BillMutexRule.BILL_LEAVE + BillMutexRule.BILL_OVERTIME) {
					//本来按照冲突规则的规定，已经冲突的单据，按照南孚电池的需求，有可能又不冲突了。我们把这种现象叫复活
					boolean canRelive = canReliveAccordingToNanfuRequest(pk_psndoc, leaveItemVOs,
							intersection, allDates, calendarMap, shiftMap,timeZoneMap);
					//如果可以复活，则表示加班在公休内，且公休不计休假，没有问题了，可以检查下一个单据组合了
					if (canRelive) {
						addIntersectionToMap(pk_psndoc, intersection, otherBillTypes, retMap);
						continue;
					}
				}
				//代码走到这里，已经肯定是冲突了（按照南孚电池的特殊需求都没有复活），需要提示用户违反了冲突规则
				addIntersectionToMap(pk_psndoc, intersection, otherBillTypes, mutexMap);
			}
		}
		if (mutexMap.size() > 0)
			throw new BillMutexException(msg.toString(), CommonUtils.transferListMap2ArrayMap2(ITimeScopeWithBillInfo.class, mutexMap));
		return CommonUtils.transferListMap2ArrayMap2(ITimeScopeWithBillInfo.class, retMap);
	}

	/**
	 * 将有时间冲突的单据加到map中
	 * @param pk_psndoc
	 * @param intersectionBills
	 * @param billsComp
	 * @param map
	 */
	private static void addIntersectionToMap(String pk_psndoc, ITimeScopeWithBillType[] intersectionBills,
			int[] billsComp, Map<String, Map<Integer, List<ITimeScopeWithBillInfo>>> map) {
		if (intersectionBills == null || intersectionBills.length == 0)
			return;
		Map<Integer, List<ITimeScopeWithBillInfo>> subMap = null;
		if (map.containsKey(pk_psndoc)) {
			subMap = map.get(pk_psndoc);
		} else {
			subMap = new HashMap<Integer, List<ITimeScopeWithBillInfo>>();
			map.put(pk_psndoc, subMap);
		}
		for (int billType : billsComp) {
			for (ITimeScopeWithBillType bill : intersectionBills) {
				for (ITimeScopeWithBillType originalBill:bill.getOriginalTimeScopeMap().values()) {
					if (billType == originalBill.getBillType()) {
						List<ITimeScopeWithBillInfo> billList = null;
						if (subMap.containsKey(billType)) {
							billList = subMap.get(billType);
						} else {
							billList = new Vector<ITimeScopeWithBillInfo>();
							subMap.put(billType, billList);
						}
						if (!billList.contains(originalBill) && originalBill instanceof ITimeScopeWithBillInfo)
							billList.add((ITimeScopeWithBillInfo) originalBill);
					}
				}
			}
		}
	}

	/**
	 * 如果冲突规则定义了休假和加班冲突，并且已经查出来休假和加班有交集时间段，那么按道理属于异常，应该不予通过。但南孚
	 * 电池提了一个需求：如果公休不计休假，那么公休应该能录入加班单。我们把这种按照冲突规则已经算是冲突，但按照南孚电池
	 * 的补充需求又不算冲突的现象叫做复活。下面的这个方法就是判断已经有交集的休假和加班单按照南孚电池的需求是否可以复活
	 * @param pk_psndoc
	 * @param leaveItemVOs
	 * @param intersectionScopes：休假和加班的交集时间段
	 * @param allDates
	 * @param calendarMap
	 * @param shiftMap
	 * @return
	 * @throws BusinessException
	 */
	private static boolean canReliveAccordingToNanfuRequest(String pk_psndoc, TimeItemCopyVO[] leaveItemVOs,
			ITimeScopeWithBillType[] intersectionScopes, UFLiteralDate[] allDates,
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap, Map<String, AggShiftVO> shiftMap,
				Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException {

		//本来按照冲突规则的规定，已经冲突的单据，按照南孚电池的需求，有可能又不冲突了。我们把这种现象叫复活
		//按休假类别循环处理，公休日如果计休假，那么这个时间冲突肯定是不允许的。如果公休日不计休假，那么要
		//看休假与加班相交的时间段是不是在公休，如果不是在公休，那么这个时间冲突肯定也是不允许的
		for (TimeItemCopyVO leaveItemVO : leaveItemVOs) {
			String pk_timeitem = leaveItemVO.getPk_timeitem();
			//公休是否计休假的参数，0为不计，1为计
			int gxComType = leaveItemVO.getGxcomtype() == null ? 0 : leaveItemVO.getGxcomtype().intValue();
			//循环检测休假与加班的相交时间段，如果时间段属于此休假类别，则要进一步判断
			for (ITimeScopeWithBillType scope : intersectionScopes) {
				//如果这个时间段不属于这个休假类别，则处理下一个时间段
				if (!scope.belongsToTimeItem(pk_timeitem))
					continue;
				//如果此时间段属于此种休假类别，并且此种休假类别公休不计为休假，那么要看这个交集时间段是否完全在公休的时间内
				//如果完全在公休的时间内，那么是可以通过的。但只要在工作日范围内，那么就是不能通过的
				//此休假类别公休日计休假，肯定冲突
				if (gxComType == 1)
					return false;
				//此休假类别公休日不计休假，则要看休假加班的交集时间段是否在公休范围内
				//按每一天的班次循环判断
				for (UFLiteralDate date : allDates) {
					//此人此天的班次。如果shift不空，那么这天肯定是排了正常的班次，这种正常的班次的时间段内是不允许加班和休假共存的
					AggShiftVO aggShiftVO = getShiftVOByPsnPKAndDate(pk_psndoc, date, calendarMap, shiftMap);
					if (aggShiftVO != null) {
						ITimeScope[] intersWithShift =
							TimeScopeUtils.intersectionTimeScopes(new ITimeScope[] { scope },
									new ITimeScope[] {aggShiftVO.getShiftVO().toWorkScope(date.toString(), dateTimeZoneMap.get(date)) });
						//如果这个休假加班的交集时间段与正常班次（非公休）的时间段交，那么肯定算是违反冲突了，最后一丝希望破灭
						if (intersWithShift != null && intersWithShift.length > 0) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 校验出差
	 * @param pkCorp
	 * @param hvos
	 * @return
	 * @throws BusinessException
	 * @Calling Method checkAway
	 * 创建时间:(2008-12-18)
	 */
	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkAway(String pk_org, AggAwayVO[] aggVOs) throws BusinessException {

		List<AwaybVO> bVOList = BillProcessHelper.toAwaybVOList(aggVOs);
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap = checkAwayIntersectionByCommonVO(pk_org, bVOList);
		AwaybVO[] bvos = bVOList.toArray(new AwaybVO[0]);
		checkPeriod(pk_org, bvos);
		//checkCrossBU(pk_org, bvos);
		return retMap;
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkAway(AggAwayVO aggVO) throws BusinessException {
		AwaybVO[] bvos = aggVO.getAwaybVOs();
		if(ArrayUtils.isEmpty(bvos))
			return null;
		processAggVO(aggVO);
		return checkAway(aggVO.getAwayhVO().getPk_org(), new AggAwayVO[]{aggVO});
	}

	private static <E extends ITimeScopeBillHeadVO,T extends ITimeScopeBillBodyVO>
	void processAggVO(ITimeScopeBillAggVO<E, T> aggVO){
		T[] bvos = aggVO.getBodyVOs();
		if(ArrayUtils.isEmpty(bvos))
			return;
		E hvo = aggVO.getHeadVO();
		for(T bvo:bvos){
			bvo.setPk_psndoc(hvo.getPk_psndoc());
			bvo.setPk_psnjob(hvo.getPk_psnjob());
			bvo.setPk_timeitem(hvo.getPk_timeitem());
		}
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkAway(AwayRegVO regVO) throws BusinessException {
		AwayRegVO[] bills = new AwayRegVO[]{regVO};
		String pk_org = regVO.getPk_org();
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap =  checkAway(pk_org, bills);
		return retMap;
	}

	public static <T extends AwayCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkAway(String pk_org, T[] vos) throws  BusinessException {
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap =  checkAwayIntersectionByCommonVO(pk_org, vos);
		//checkCrossBU(pk_org, vos);
		return retMap;
	}



	/**
	 * 批量检查出差单的时间段冲突。首先把出差单和出差单进行比较，如果有重复时间的就校验不通过。然后根据冲突规则的定义再来检测与休假、加班、停工单的冲突
	 * 注意，目前的比较规则是：如果是相同类型的单据进行比较，则要比较数据库中的所有单据(不包含nopass)。如果是不同类型的单据比较，则也比较数据库中的
	 * 所有单据(不包含nopass)
	 * @param pkCorp
	 * @param hvos
	 * @throws BusinessException
	 */
	private static <T extends AwayCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkAwayIntersectionByCommonVO(String pk_org, List<T> commonVOs) throws BusinessException {
		return checkAwayIntersectionByCommonVO(pk_org, commonVOs.toArray(new AwayCommonVO[0]));
	}

	@SuppressWarnings("unchecked")
	private static <T extends AwayCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkAwayIntersectionByCommonVO(String pk_org, T[] commonVOs) throws BusinessException {

		//首先检查出差单和出差单的冲突
		// 验证是否有同名的人已申请的未退回纪录有重叠的出差时间段
//		Map<String, ITimeScopeWithBillInfo[]> duplicatedTimescopeMap = CommonMethods.castMap(NCLocator.getInstance().lookup(IAwayQueryService.class).queryIntersectionBillsMap(commonVOs));
		Map duplicatedTimescopeMap = CommonUtils.castMap(AwayServiceFacade.queryIntersectionBillsMap(commonVOs));
		//然后根据单据冲突规则，检查出差单与其他类型的单据的冲突
	//	String psndocInSQL = SQLHelper.joinToString(commonVOs, AwayCommonVO.PK_PSNDOC); 这样如果pk_psndoc超出一定数量会报错，要用临时表-modify by zhouyuh
		try{
			InSQLCreator isc = new InSQLCreator();
			String psndocInSQL = isc.getInSQL(commonVOs, AwayCommonVO.PK_PSNDOC);
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result =
				checkBillsByMutexRule(pk_org, psndocInSQL, commonVOs, BillMutexRule.BILL_AWAY);
			if(MapUtils.isNotEmpty(duplicatedTimescopeMap)){
				Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
				mutexMap.put(BillMutexRule.BILL_AWAY, duplicatedTimescopeMap);
				throw new BillMutexException(null, CommonUtils.transferMap(mutexMap));
			}
			return result;

		}
		catch(BillMutexException bme){
			if(MapUtils.isEmpty(duplicatedTimescopeMap))
				throw bme;
			//如果加班和其他单据都冲突了，则合并两个map，一并显示在客户端
			Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
			mutexMap.put(BillMutexRule.BILL_AWAY, duplicatedTimescopeMap);
			Map uniteMap = CommonUtils.transferMap(mutexMap);
			uniteMap=CommonUtils.putAll(uniteMap,bme.getMutexBillsMap());
			throw new BillMutexException(bme.getMessage(), uniteMap);
		}
	}


	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkLeave(AggLeaveVO aggVO) throws BusinessException {
		if(ArrayUtils.isEmpty(aggVO.getBodyVOs()))
			return null;
		processAggVO(aggVO);
		return checkLeave(aggVO.getLeavehVO().getPk_org(), new AggLeaveVO[]{aggVO});
	}
	/**
	 * 休假校验
	 * @param pkCorp
	 * @param hvos
	 * @return
	 * @throws BusinessException
	 * @Calling Method checkLeave
	 * 创建时间:(2008-12-18)
	 */
	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkLeave(String pk_org, AggLeaveVO[] aggVOs) throws BusinessException {
		List<LeavebVO> bVOList = BillProcessHelper.toLeavebVOList(aggVOs);
		if(bVOList==null||bVOList.size()<1)return null;
		UFBoolean isLactation = aggVOs[0].getLeavehVO().getIslactation();

		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap = checkLeaveIntersectionByCommonVO(pk_org, isLactation!=null&&isLactation.booleanValue(), bVOList);

		//哺乳假也要校验期间是否封存
		checkPeriod(pk_org, bVOList.toArray(new LeavebVO[0]));
		//休假没有校验跨业务单元，因为休假可以自动拆单，拆单的结果肯定不跨业务单元
		return retMap;
	}

	/**
	 * 校验申请日期，参数结余时长，跨年度
	 * @param aggVO
	 * @throws BusinessException
	 */
	public static void checkLeavePara(AggLeaveVO aggVO) throws BusinessException
	{
		ITimeItemQueryService itemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		TimeItemCopyVO typeVO = itemService.queryCopyTypesByDefPK(aggVO.getHeadVO().getPk_org(), aggVO.getHeadVO().getPk_leavetype(), TimeItemCopyVO.LEAVE_TYPE);
		//是否启用休假申请不能晚于休假开始日期(天数)校验
		UFBoolean isApptimeLimit = typeVO.getIsleaveapptimelimit();
		Integer dayNum = typeVO.getLeaveapptimelimit();
		if(isApptimeLimit!=null&&isApptimeLimit.booleanValue()&&dayNum!=null)
		{
			UFLiteralDate applyDate = aggVO.getLeavehVO().getApply_date();
			for(int i=0;aggVO.getLeavebVOs()!=null&&i<aggVO.getLeavebVOs().length;i++)
			{
				UFLiteralDate beginDate = aggVO.getLeavebVOs()[i].getLeavebegindate();
				if(beginDate!=null)
				{
					int sub = applyDate.getDay()-beginDate.getDay();
					if(sub-dayNum.intValue()>0)
					{
						throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1680"
/*@res "申请日期{0}比休假开始日期{1}晚{2}天超过该类别限制的{3}天，请修改!"*/,applyDate.toString(),beginDate.toString(),String.valueOf(sub),dayNum.toString()));
					}
				}
			}
		}
		//是否控制休假时长，是否严格控制
		checkLeaveRestrictPara(typeVO,aggVO.getLeavehVO().getSumhour(),aggVO.getLeavehVO().getRestdayorhour());

	}

	/**
	 *
	 * @param typeVO
	 * @param sumhour
	 * @param restdayorhour
	 * @throws BusinessException
	 */
	public static void checkLeaveRestrictPara(TimeItemCopyVO typeVO,UFDouble sumhour,UFDouble restdayorhour) throws BusinessException
	{
		if(typeVO.getIsLeavelimit()!=null&&typeVO.getIsLeavelimit().booleanValue())
		{
			if(typeVO.getIsRestrictlimit()!=null&&typeVO.getIsRestrictlimit().booleanValue())
			{
				if(sumhour!=null)
				{
					if(restdayorhour==null||(sumhour.sub(restdayorhour)).doubleValue()>0)
					{
						throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1681"
/*@res "休假总时长{0}大于结余时长{1}，请修改!"*/,sumhour.toString(),restdayorhour==null?0+"":restdayorhour.toString()));
					}
				}
			}
		}
	}


	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkLeave(LeaveRegVO regVO) throws BusinessException {
		return checkLeave(regVO.getPk_org(), new LeaveRegVO[]{regVO});
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkLeave(String pk_org, LeaveRegVO[] regVOs) throws BusinessException {
		UFBoolean isLactation = regVOs[0].getIslactation();
		return checkLeaveIntersectionByCommonVO(pk_org, isLactation!=null&&isLactation.booleanValue(),regVOs);
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkLeave(String pk_org, LeavebVO[] bvos) throws BusinessException {
		boolean isLactation = bvos[0].getPk_timeitem().equals(LeaveConst.LEAVETYPE_SUCKLE);
		return checkLeaveIntersectionByCommonVO(pk_org, isLactation,bvos);
	}

	private static <T extends LeaveCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkLeaveIntersectionByCommonVO(String pk_org, boolean isLactation,List<T> commonVOList) throws BusinessException {
		return checkLeaveIntersectionByCommonVO(pk_org,isLactation, commonVOList.toArray(new LeaveCommonVO[0]));
	}

	@SuppressWarnings("unchecked")
	private static <T extends LeaveCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>>
		checkLeaveIntersectionByCommonVO(String pk_org, boolean isLactation, T[] commonVOs) throws BusinessException {

		// 验证是否有同名的人已申请的未退回纪录有重叠的休假时间段（非哺乳假只跟非哺乳假比，哺乳假只跟哺乳假比）
//		Map<String, ITimeScopeWithBillInfo[]> duplicatedTimescopeMap = CommonMethods.castMap(NCLocator.getInstance().lookup(ILeaveQueryService.class).queryIntersectionBillsMap(commonVOs));
		for(int i=0;commonVOs!=null&&i<commonVOs.length;i++)
		{
			commonVOs[i].setAttributeValue(LeavehVO.ISLACTATION, UFBoolean.valueOf(isLactation));
		}
		Map duplicatedTimescopeMap = CommonUtils.castMap(LeaveServiceFacade.queryIntersectionBillsMap(commonVOs));
		//如果是哺乳假，则不用往下比较了，因为哺乳假只用校验与哺乳假的冲突，不用判断与其他休假类别、加班出差停工的冲突
		if(isLactation){
			if(MapUtils.isEmpty(duplicatedTimescopeMap))
				return null;
			Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String,ITimeScopeWithBillInfo[]>>();
			mutexMap.put(BillMutexRule.BILL_LEAVE, duplicatedTimescopeMap);
			throw new BillMutexException(null, CommonUtils.transferMap(mutexMap));
		}
		//然后根据单据冲突规则，检查休假单与其他类型的单据的冲突
	//	String psndocInSQL = SQLHelper.joinToString(commonVOs, LeavebVO.PK_PSNDOC);

		InSQLCreator isc =null;
		String psndocInSQL = null;
		try{
			isc = new InSQLCreator();
			psndocInSQL = isc.getInSQL(commonVOs, LeavebVO.PK_PSNDOC);

		//不考虑哺乳假与其他类型的单据的冲突
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result = checkBillsByMutexRule(pk_org, psndocInSQL, commonVOs, BillMutexRule.BILL_LEAVE);
			if(MapUtils.isNotEmpty(duplicatedTimescopeMap)){
				Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
				mutexMap.put(BillMutexRule.BILL_LEAVE, duplicatedTimescopeMap);
				throw new BillMutexException(null, CommonUtils.transferMap(mutexMap));
			}
			return result;
		}
		catch(BillMutexException bme){
			if(MapUtils.isEmpty(duplicatedTimescopeMap))
				throw bme;
			//如果休假和其他单据都冲突了，则合并两个map，一并显示在客户端
			Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
			mutexMap.put(BillMutexRule.BILL_LEAVE, duplicatedTimescopeMap);
			Map uniteMap = CommonUtils.transferMap(mutexMap);
			uniteMap=CommonUtils.putAll(uniteMap,bme.getMutexBillsMap());
			throw new BillMutexException(bme.getMessage(), uniteMap);
		}
	}

	/**
	 * 根据人员主键和日期返回此人此天的aggshiftvo，如果此日期内没有排班或者是公休这种班，则返回空
	 * @param pk_psndoc
	 * @param date
	 * @param calendarMap
	 * @return
	 * @throws BusinessException
	 */
	private static AggShiftVO getShiftVOByPsnPKAndDate(String pk_psndoc, UFLiteralDate date, Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap,
			Map<String, AggShiftVO> shiftMap) throws BusinessException {
		if (calendarMap == null || calendarMap.size() == 0 || shiftMap == null || shiftMap.size() == 0)
			return null;
		//此人所有天的工作日历
		Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap = calendarMap.get(pk_psndoc);
		if (psnCalendarMap == null || psnCalendarMap.size() == 0)
			return null;
		AggPsnCalendar calendarAllVO = psnCalendarMap.get(date);
		if (calendarAllVO == null || calendarAllVO.getPsnCalendarVO() == null)
			return null;
		return ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, calendarAllVO.getPsnCalendarVO().getPk_shift());
	}

	/**
	 * 初始化单据map，即从数据库中查询出指定人员范围指定日期范围内的单据
	 * @param pkCorp
	 * @param psnPkInSql
	 * @param beginDate
	 * @param endDate
	 * @param map
	 * @param billType
	 * @throws BusinessException
	 */
	private static void initBillMap(String pk_org, String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate, Map<Integer, Map> map, int billType) throws BusinessException {
		if (billType != BillMutexRule.BILL_LEAVE) {//查出这些人这些天的休假单和销假单
			Map leaveCommonVOMap = LeaveServiceFacade.queryAllSuperVOExcNoPassByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);

			//有哺乳假是可以加班的，因此过滤掉哺乳假
			if(BillMutexRule.BILL_OVERTIME == billType&&MapUtils.isNotEmpty(leaveCommonVOMap)){//有哺乳假是应该让加班的
				Map<String, LeaveCommonVO[]> leaveMap = new HashMap<String, LeaveCommonVO[]>();//不含哺乳假的map
				for(Object str:leaveCommonVOMap.keySet()){
					List<LeaveCommonVO> leaveList = new ArrayList<LeaveCommonVO>();
					Object[] objects = (Object[]) leaveCommonVOMap.get(str);
					for(Object obj:objects){
						LeaveCommonVO vo = (LeaveCommonVO) obj;
						if(vo.getIslactation()!=null&&vo.getIslactation().booleanValue()){
							continue;
						}
						leaveList.add(vo);
					}
					if(CollectionUtils.isNotEmpty(leaveList)){
						leaveMap.put((String) str, leaveList.toArray(new LeaveCommonVO[0]));
					}
				}
				leaveCommonVOMap = leaveMap;
//				for(Object str:leaveCommonVOMap.keySet()){
//					Object[] objects = (Object[]) leaveCommonVOMap.get(str);
//					for(Object obj:objects){
//						LeaveCommonVO vo = (LeaveCommonVO) obj;
//						if(vo.getIslactation()!=null&&vo.getIslactation().booleanValue()){
//							objects = ArrayUtils.removeElement(objects, obj);
//						}
//					}
//					leaveCommonVOMap.remove(str);
//					if (!ArrayUtils.isEmpty(objects)) {
//						leaveCommonVOMap.put(str, objects);
//					}
//				}
			}

			map.put(BillMutexRule.BILL_LEAVE, leaveCommonVOMap);
		}

		if (billType != BillMutexRule.BILL_OVERTIME) {//查出这些人这些天的加班单
			Map overtimeCommonVOMap = OverTimeServiceFacade.queryAllSuperVOExcNoPassByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			map.put(BillMutexRule.BILL_OVERTIME, overtimeCommonVOMap);
		}
		if (billType != BillMutexRule.BILL_AWAY) {//查出这些人这些天的出差单
			Map awayCommonVOMap = AwayServiceFacade.queryAllSuperVOExcNoPassByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			map.put(BillMutexRule.BILL_AWAY, awayCommonVOMap);
		}
		if (billType != BillMutexRule.BILL_SHUTDOWN) {//查出这些人这些天的停工单
			Map shutdownMap =  ShutdownServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			map.put(BillMutexRule.BILL_SHUTDOWN, shutdownMap);
		}
	}


	/**
	 * 校验加班单
	 * 目前只提供单据号重复、单据名重复、考勤期间校验、期间已封存、单据时间冲突等校验，至于跨工作日历的校验暂时还没有加进来
	 * @param pkCorp
	 * @param hvos
	 * @return
	 * @throws BusinessException
	 */
	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkOvertime(String pk_org, AggOvertimeVO[] aggVOs) throws  BusinessException {
		List<OvertimebVO> bVOList = BillProcessHelper.toOvertimebVOList(aggVOs);
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap = checkOvertimeIntersectionByCommonVO(pk_org, bVOList);
		OvertimebVO[] bvos = bVOList.toArray(new OvertimebVO[0]);
		NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).checkTBMPsndocDate(pk_org, StringPiecer.getStrArray(bvos, OvertimebVO.PK_PSNDOC), bvos, true);
		checkPeriod(pk_org,bvos);
		//checkCrossBU(pk_org, bvos);
		//校验工作日历的完整性
		BillValidatorAtServer.checkCalendarCompleteForOvertime(pk_org, bvos);
//		OvertimeRegValidator.validateRegVOs(bvos);
		return retMap;
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkOvertime(AggOvertimeVO aggVO) throws BusinessException {
		if(ArrayUtils.isEmpty(aggVO.getBodyVOs()))
			return null;
		processAggVO(aggVO);
		return checkOvertime(aggVO.getOvertimehVO().getPk_org(), new AggOvertimeVO[]{aggVO});
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkOvertime(OvertimeRegVO regVO) throws BusinessException {
		OvertimeRegVO[] bills = new OvertimeRegVO[]{regVO};
		String pk_org = regVO.getPk_org();
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap =  checkOvertime(pk_org, bills);
		return retMap;
	}

	public static <T extends OvertimeCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkOvertime(String pk_org, T[] vos) throws BusinessException {
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap =  checkOvertimeIntersectionByCommonVO(pk_org, vos);
		//checkCrossBU(pk_org, vos);
		return retMap;
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkOvertime(OvertimeGenVO genVO) throws BusinessException {
		return checkOvertime(genVO.getPk_org(), new OvertimeGenVO[]{genVO});
	}

	private static <T extends OvertimeCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkOvertimeIntersectionByCommonVO(String pk_org, List<T> commonVOList) throws BusinessException {
		return checkOvertimeIntersectionByCommonVO(pk_org, commonVOList.toArray(new OvertimeCommonVO[0]));
	}

	@SuppressWarnings("unchecked")
	private static <T extends OvertimeCommonVO> Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkOvertimeIntersectionByCommonVO(String pk_org, T[] commonVOs) throws BusinessException  {
		// 验证是否有同名的人已申请的未退回纪录有重叠的加班时间段
//		Map<String, ITimeScopeWithBillInfo[]> duplicatedTimescopeMap = CommonMethods.castMap(NCLocator.getInstance().lookup(IOvertimeQueryService.class).queryIntersectionBillsMap(commonVOs));
		Map duplicatedTimescopeMap = CommonUtils.castMap(OverTimeServiceFacade.queryIntersectionBillsMap(commonVOs));

		//然后根据单据冲突规则，检查出差单与其他类型的单据的冲突
		//String psndocInSQL = SQLHelper.joinToString(commonVOs, OvertimeCommonVO.PK_PSNDOC);

		InSQLCreator isc =null;
		String psndocInSQL = null;
		try{
			isc = new InSQLCreator();
			psndocInSQL = isc.getInSQL(commonVOs, OvertimeCommonVO.PK_PSNDOC);
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result =
				checkBillsByMutexRule(pk_org, psndocInSQL, commonVOs, BillMutexRule.BILL_OVERTIME);
			if(MapUtils.isNotEmpty(duplicatedTimescopeMap)){
				Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
				mutexMap.put(BillMutexRule.BILL_OVERTIME, duplicatedTimescopeMap);
				throw new BillMutexException(null, CommonUtils.transferMap(mutexMap));
			}
			return result;
		}
		catch(BillMutexException bme){
			if(MapUtils.isEmpty(duplicatedTimescopeMap))
				throw bme;
			//如果加班和其他单据都冲突了，则合并两个map，一并显示在客户端
			Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
			mutexMap.put(BillMutexRule.BILL_OVERTIME, duplicatedTimescopeMap);
			Map uniteMap = CommonUtils.transferMap(mutexMap);
			uniteMap=CommonUtils.putAll(uniteMap, bme.getMutexBillsMap());
			throw new BillMutexException(bme.getMessage(), uniteMap);
		}
	}

	/**
	 * 校验停工待料
	 * @param pkCorp
	 * @param hvos
	 * @return
	 * @throws BusinessException
	 * @Calling Method checkShutdown
	 * 创建时间:(2008-12-18)
	 */
	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkShutdown(String pk_org, ShutdownRegVO[] regVOs) throws BusinessException {
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap =  checkShutdownIntersectionByRegVO(pk_org, regVOs);
	//	checkCrossBU(pk_org, regVOs);
		return retMap;
	}

	public static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkShutdown(ShutdownRegVO regVO) throws BusinessException {
		ShutdownRegVO[] bills =  new ShutdownRegVO[] { regVO };
		String pk_org = regVO.getPk_org();
		Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> retMap = checkShutdown(pk_org, bills);
		return retMap;
	}

//	private Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkShutdownIntersectionByRegVO(String pk_org, List<ShutdownRegVO> regVOList) throws BusinessException, NamingException, SQLException {
//		return checkShutdownIntersectionByRegVO(pk_org, regVOList.toArray(new ShutdownRegVO[0]));
//	}

	@SuppressWarnings("unchecked")
	private static Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkShutdownIntersectionByRegVO(String pk_org, ShutdownRegVO[] regVOs) throws BusinessException {
		//检查工作日历是否完整(2008.10.23zengcheng加入)
		checkCalendarCompleteForLeaveAndShutdown(pk_org, regVOs);
		//验证是否有同名的人已申请的未退回纪录有重叠的停工时间段
//		Map<String, ITimeScopeWithBillInfo[]> duplicatedTimescopeMap = CommonMethods.castMap(NCLocator.getInstance().lookup(IShutdownQueryService.class).queryIntersectionBillsMap(regVOs));
		Map duplicatedTimescopeMap = CommonUtils.castMap(ShutdownServiceFacade.queryIntersectionBillsMap(regVOs));
		//然后根据单据冲突规则，检查出差单与其他类型的单据的冲突
		//String psndocInSQL = SQLHelper.joinToString(regVOs, ShutdownRegVO.PK_PSNDOC);
		InSQLCreator isc =null;
		String psndocInSQL = null;
		try{
			isc = new InSQLCreator();
			psndocInSQL = isc.getInSQL(regVOs, ShutdownRegVO.PK_PSNDOC);

			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result =
				checkBillsByMutexRule(pk_org, psndocInSQL, regVOs, BillMutexRule.BILL_SHUTDOWN);
			if(MapUtils.isNotEmpty(duplicatedTimescopeMap)){
				Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
				mutexMap.put(BillMutexRule.BILL_SHUTDOWN, duplicatedTimescopeMap);
				throw new BillMutexException(null, CommonUtils.transferMap(mutexMap));
			}
			return result;
		}
		catch(BillMutexException bme){
			if(MapUtils.isEmpty(duplicatedTimescopeMap))
				throw bme;
			//如果加班和其他单据都冲突了，则合并两个map，一并显示在客户端
			Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> mutexMap = new HashMap<Integer, Map<String, ITimeScopeWithBillInfo[]>>();
			mutexMap.put(BillMutexRule.BILL_SHUTDOWN, duplicatedTimescopeMap);
			Map uniteMap = CommonUtils.transferMap(mutexMap);
			uniteMap=CommonUtils.putAll(uniteMap,bme.getMutexBillsMap());
			throw new BillMutexException(bme.getMessage(), uniteMap);
		}
	}

	/**
	 * 检查单据，是否有考勤期间已经封存了的
	 * @param pk_corp
	 * @param bills
	 * @throws BusinessException
	 * @throws BusinessException
	 */
	public static void checkPeriod(String pk_org, IDateScopeBillBodyVO[] bills) throws BusinessException {
		if (bills == null || bills.length == 0)
			return;
		Map<UFLiteralDate,PeriodVO> periodMap =PeriodServiceFacade.queryPeriodMapByDateScopes(pk_org, bills);
//		UFLiteralDate firstDate = DateScopeUtils.findEarliestBeginDate(bills);
//		UFLiteralDate lastDate = DateScopeUtils.findLatestEndDate(bills);
		if(MapUtils.isEmpty(periodMap)){
//			throw new BusinessException("the org has not defined tbm period in date scope "+firstDate+"-"+lastDate+" yet!");
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1927")
					/*@res "单据时间超出该组织的考勤期间范围,请先定义考勤期间!"*/);
		}
		List<IDateScopeBillBodyVO> periodSealedBillList = new ArrayList<IDateScopeBillBodyVO>();
//MOD James 客户不需要考勤期间封存的限制
		//		for (IDateScopeBillBodyVO bill : bills) {
//			PeriodVO beginDatePeriod = periodMap.get(bill.getBegindate());
//			if(beginDatePeriod!=null&&beginDatePeriod.getSealflag()!=null&&beginDatePeriod.getSealflag().booleanValue()){
//				periodSealedBillList.add(bill);
//				continue;
//			}
//			PeriodVO endDatePeriod = periodMap.get(bill.getBillEndDate());
//			if(endDatePeriod!=null&&endDatePeriod.getSealflag()!=null&&endDatePeriod.getSealflag().booleanValue()){
//				periodSealedBillList.add(bill);
//				continue;
//			}
//		}
		if (periodSealedBillList.size() == 0)
			return;
		throw new BillPeriodSealedException(null, periodSealedBillList);
	}

	/**
	 * 校验单据是否跨业务单元
	 * 对于休假，加班，出差，停工待料，需要每个时间段都不跨业务单元，因为不同的业务单元的时区有可能不一样，会对处理带来影响
	 * @param pk_org
	 * @param bills
	 * @throws BusinessException
	 * TODO:此方法有待修改。不能简单地用拆分后的数量！=拆分前的数量来判断是否有跨业务单元的记录。有可能
	 * 数量没变，但业务单元的pk变了，这种没有校验出来
	 */
	public static void checkCrossBU(String pk_org,IDateScopeBillBodyVO[] bills) throws BusinessException{
		if(ArrayUtils.isEmpty(bills))
			return;
		IDateScopeBillBodyVO[] cutBills = BillMethods.compareAndCutDate(pk_org, bills);
		if(ArrayUtils.getLength(cutBills)>bills.length){
			throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1825")
					/*@res "存在跨业务单元的记录,请拆分时间段!"*/);
		}if(ArrayUtils.getLength(cutBills)==0){
			throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1836")
					/*@res "单据时间超出该人员的考勤档案范围,请拆分时间段!"*/);
}
	}

	/**
	 * 校验签卡申请的多条签卡记录是否跨了业务单元。与时间段的单据不同，此校验只是简单地将每一条记录打上pk_joborg记录，然后看是否有不同的
	 * @param pk_org
	 * @param aggVO
	 * @throws BusinessException
	 * TODO:此方法有待修改。不能简单地用子表的业务单元有不相同的情况来确认是否跨业务单元了。有可能子表的业务单元
	 * 都一样，但与主表的业务单元不一样，这种就没校验出来
	 */
	public static void checkCrossBU(String pk_org,AggSignVO aggVO) throws BusinessException{
		SignbVO[] bvos = aggVO.getSignbVOs();
		if(ArrayUtils.isEmpty(bvos))
			return;
		List<SignbVO> bList = new ArrayList<SignbVO>();
		for(SignbVO bvo:bvos){
			if(bvo.getStatus()!=VOStatus.DELETED)
				bList.add(bvo);
		}
		if(bList.size()<=1)
			return;
		SignbVO[] notDelVOs = bList.toArray(new SignbVO[0]);
		BillMethods.compareAndCutSign(pk_org, notDelVOs);
		BillMethods.processBeginEndDatePkJobOrgTimeZone(notDelVOs);
		for(int i=1;i<notDelVOs.length;i++){
			String pk_joborg1 = notDelVOs[i-1].getPk_joborg();
			String pk_joborg2 = notDelVOs[i].getPk_joborg();
			if(!pk_joborg1.equals(pk_joborg2))
				throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1827")
			/*@res "存在跨业务单元的签卡记录，请拆分申请单!"*/);
		}
	}

	/**
	 * 在单据数组中找出期间已结算的单据的人名，拼接成字符串返回
	 * @param pk_corp
	 * @param bills
	 * @return
	 */
//	public String getBillPeriodSealedPsnNames(String pk_corp, ITimeScopeWithBillInfo[] bills) throws NamingException, SQLException {
//		if (bills == null || bills.length == 0)
//			return null;
//		PeriodDMO dmo = new PeriodDMO();
//		StringBuilder psnNames = new StringBuilder();
//		for (ITimeScopeWithBillInfo bill : bills) {
//			if (dmo.queryCheckState(pk_corp, bill.getScope_start_datetime().getDate()) == 1 || dmo.queryCheckState(pk_corp, bill.getScope_end_datetime().getDate()) == 1) {
//				psnNames.append(bill.getBillinfo_psnname()).append(",");
//			}
//		}
//		if (psnNames.length() > 0) {
//			psnNames.deleteCharAt(psnNames.length() - 1);
//			return psnNames.toString();
//		}
//		return null;
//	}

//	@SuppressWarnings("unchecked")
//	public <T extends ITimeScopeWithBillInfo> void checkPeriod(String pk_corp, List<T> bills) throws SQLException, NamingException, BillPeriodSealedException {
//		if (bills == null || bills.size() == 0)
//			return;
//		checkPeriod(pk_corp, bills.toArray((T[]) Array.newInstance(bills.get(0).getClass(), 0)));
//	}

	public static void checkCalendarCompleteForLeaveAndShutdown(ITimeScopeBillBodyVO bill) throws BusinessException {
		checkCalendarCompleteForLeaveAndShutdown(bill.getPk_org(), new ITimeScopeBillBodyVO[] { bill });
	}

	/**
	 * 检查休假或者停工单的工作日历是否完整
	 * 检测休假或者停工单的工作日历不完整的的算法比较复杂，描述如下：
	 * 首先把时间轴上的排了班的日期的考勤时间段连起来：
	 * 如果某日排了班，前一天也排了班，则此天的班次的考勤开始时间往前推，前一天的班次的考勤结束时间往后推，直到连在一起。
	 * 后一天也排了班，则此天的班次的考勤结束时间往后推，后一天的班次的考勤开始时间往前推，直到连在一起。
	 * 如果前一天没有排班，则开始时间不往前推；如果后一天没有排班，则结束时间不往后推。停止考勤这种班次视为没有排班
	 * 这样做了之后，凡是连续排班的日期段，考勤时间段在时间轴上就会连成一整个时间段。而如果中间有日期没有排班，则会出现空白段
	 * 简单地说，就是如果连续两天都有排班，那么前一天的考勤开始时间到后一天的考勤结束时间要连成一个时间段
	 * 例子：某人的排班情况如下（假设白班的考勤时间段是3:00-23:00）：
	 * 1日白班（之前都没有排班）,2日没有排班，3，4，5日都排白班，6日开始又没有排班
	 * 那么经过上述的连接时间段的操作后，排班的时间段范围就是：1日3:00-23:00，3日3:00-5日23:00
	 *
	 * 然后，检查休假的时间段是否被上述的连接时间段完全包含，如果不是完全包含，则工作日历完整，否则不完整
	 * @param pkCorp
	 * @param bills
	 * @throws BusinessException
	 */
	public static void checkCalendarCompleteForLeaveAndShutdown(String pk_org, ITimeScopeBillBodyVO[] bills) throws BusinessException {
		if (bills == null || bills.length == 0)
			return;
		Map<ITimeScopeWithBillInfo, Map<UFLiteralDate, AggPsnCalendar>> calendarMap = new HashMap<ITimeScopeWithBillInfo, Map<UFLiteralDate, AggPsnCalendar>>();
		Map<ITimeScopeWithBillInfo,Map<UFLiteralDate, TimeZone>> psnDateTimeZoneMap = new HashMap<ITimeScopeWithBillInfo, Map<UFLiteralDate,TimeZone>>();
		//查出所有单据可能涉及到的工作日历。由于每张单据的时间都可能不一样，因此每张单据单独进行一次查询，而不能按照一个统一的时间查询
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRuleVO.getTimeZoneMap();

//		//效率优化,因为查询班次需要先查询所有业务单元很耗时
//		Map<String, AggShiftVO> allShiftMap = null;
//		if(bills[0] instanceof LeaveCommonVO){
//			allShiftMap = ((LeaveCommonVO)bills[0]).getAggShiftMap();
//			if(null == allShiftMap){
//				allShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//				((LeaveCommonVO)bills[0]).setAggShiftMap(allShiftMap);
//			}
//		}else{
//			allShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		}
		Map<String, AggShiftVO> allShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		StringBuilder psndocInSQL = new StringBuilder();
		List<String> psnList = new ArrayList<String>();
		for (ITimeScopeBillBodyVO bill : bills) {
			//人员的工作日历map,要求往前往后都推2天，也就是说，一般情况下，map里面的天数会比单据天数多4天
//			UFLiteralDate beginDate = bill.getBegindate().getDateBefore(2);
//			UFLiteralDate endDate = bill.getEnddate().getDateAfter(2);
			//下面的pk_org参数为null，表示查询工作日历的结果要跨HR组织，如果不垮HR组织的话，会导致其他
			//HR组织的工作日历查不到，导致误报“工作日历不完整错误”
			//优化下面的工作日历查询
//			Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap = calendarService.queryCalendarMapByPsnDates(null,
//					bill.getPk_psndoc(), beginDate, endDate,timeZoneMap,allShiftMap);
//			calendarMap.put(bill, psnCalendarMap);

			//优化时区查询，使用批量查询方法
//			Map<UFLiteralDate, String> psnDateOrgMap = psndocService.queryDateJobOrgMap(bill.getPk_psndoc(), beginDate, endDate);
//			psnDateTimeZoneMap.put(bill, CommonMethods.createDateTimeZoneMap(psnDateOrgMap, timeRuleVO.getTimeZoneMap()));
//			psndocInSQL.append(" '" + bill.getPk_psndoc() + "',");
			psnList.add(bill.getPk_psndoc());
		}
//		psndocInSQL = psndocInSQL.deleteCharAt(psndocInSQL.length()-1);
		//工作日历批量查询
	    Map<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>> billCalendarVOsMap = calendarService.queryCalendarVOsByPsnsDates(null, bills, timeZoneMap, allShiftMap);
	    for(ITimeScopeBillBodyVO bill:billCalendarVOsMap.keySet()){
			calendarMap.put(bill, billCalendarVOsMap.get(bill));
		}
	    //时区批量查询处理
	    Map<String, Map<UFLiteralDate, String>> psndateorgmap = psndocService.queryDateJobOrgMapByPsndocInSQL(psnList.toArray(new String[0]), bills);
	    for (ITimeScopeBillBodyVO bill : bills) {
	    	psnDateTimeZoneMap.put(bill, CommonMethods.createDateTimeZoneMap(psndateorgmap==null?null:psndateorgmap.get(bill.getPk_psndoc()), timeRuleVO.getTimeZoneMap()));
	    }

		//查找所有的班次类别
//		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);//效率优化，不在直接查询(组织过多查询效率很低） 改用allShiftMap中取值
	    Map<String, ShiftVO> shiftMap = new HashMap<String, ShiftVO>();
	    for(String pk_shift:allShiftMap.keySet()){
	    	shiftMap.put(pk_shift, allShiftMap.get(pk_shift).getShiftVO());
	    }


		BillValidator.checkCalendarCompleteForLeaveAndShutdown(bills, calendarMap, shiftMap,psnDateTimeZoneMap);
	}

	/**
	 * 校验加班单的工作日历是否完整。判断规则是：
	 * 如果找到了加班单的归属日，则完整，否则工作日历不完整
	 * 注意，对加班单的校验，分两种情况：需要校验，和不需要校验。这个方法适用于不需要要校验的情况
	 * @param pkCorp
	 * @param bill
	 * @throws BusinessException
	 */
	public static void checkCalendarCompleteForOvertime(OvertimebVO bill) throws BusinessException {
		checkCalendarCompleteForOvertime(bill.getPk_org(), new OvertimebVO[] { bill });
	}

	public static void checkOvertimeAllowCheck(String pk_org, OvertimebVO[] bills) throws BusinessException {
		checkOvertimeAllowCheck(pk_org, bills, true, true);
	}

	public static void checkOvertimeAllowCheck(String pk_org, List<OvertimebVO> billList) throws BusinessException {
		checkOvertimeAllowCheck(pk_org, billList.toArray(new OvertimebVO[0]), true, true);
	}

	/**
	 * 检查加班单是否可以挑上“是否校验”的勾
	 * 如果filterByCheckFlag为true，则只校验Isneedcheck为Y的单据。如果filterByCheckFlag为false，则校验所有的单据
	 * 如果throwException为true，则对于不能挑勾的，用抛异常的方式；如果throwException为false，则用返回值的方式
	 * 如果有不能挑勾的，则抛异常
	 * @param pkCorp
	 * @param bills
	 * @throws BusinessException
	 */
	public static Boolean[] checkOvertimeAllowCheck(String pk_org, OvertimeCommonVO[] bills, boolean filterByCheckFlag, boolean throwException) throws BusinessException  {
		if (bills == null || bills.length == 0)
			return null;
		Boolean[] retVal = new Boolean[bills.length];
		//得到该公司的所有班次集合
		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		Map<String, TimeZone> timeZoneMap = timeRuleVO.getTimeZoneMap();
		for (int i = 0; i < bills.length; i++) {
			OvertimeCommonVO bill = bills[i];
			//如果单据的isneedcheck标志为否，并且调用者要求只校验isneedcheck为y的单据，则continue
			if ((bill.getIsneedcheck() == null || !bill.getIsneedcheck().booleanValue()) && filterByCheckFlag) {
				continue;
			}

			String pk_psndoc = bill.getPk_psndoc(); // 人员pk
			UFLiteralDate begindDate = bill.getOvertimebegindate(); // 加班单开始日期
			UFLiteralDate endDate = bill.getOvertimeenddate(); // 加班单结束日期
			UFDateTime beginDateTime = bill.getScope_start_datetime(); // 加班单开始时间
			UFDateTime endDateTime = bill.getScope_end_datetime(); // 加班单结束时间

			// 得到前后共五天的工作日历，根据单据的开始时间和日历的考勤结束时间比较确定对应的工作日历
			Map<UFLiteralDate, AggPsnCalendar> calendarMap = calendarService.queryCalendarMapByPsnDates(pk_org, pk_psndoc, begindDate.getDateBefore(2), endDate.getDateAfter(2));
			Map<UFLiteralDate, String> dateOrgMap = psndocService.queryDateJobOrgMap(pk_psndoc, begindDate.getDateBefore(2), endDate.getDateAfter(2));

			UFLiteralDate curDate = null;
			if (calendarMap != null && calendarMap.size() > 0)
				curDate = BillProcessHelper.findBelongtoDate0(bill, calendarMap, shiftMap,
						CommonUtils.createDateArray(begindDate, endDate, 2, 2),true,
						CommonMethods.createDateTimeZoneMap(dateOrgMap, timeRuleVO.getTimeZoneMap()));
			if (curDate == null) {
				if (throwException)
					//throw new ValidationException(bill.getBillinfo_psnname() + "工作日历不完整,不允许校验!");
					//TODO:第x行多语,2008.11.05
					throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1682"
/*@res "{0}工作日历不完整,不允许校验!"*/, getPsnName(pk_psndoc)));
				retVal[i] = Boolean.FALSE;
				continue;
			}

			AggPsnCalendar aggCalendarVO = calendarMap.get(curDate);
			String thePkClass = aggCalendarVO.getPsnCalendarVO().getPk_shift();

			UFLiteralDate preDate = curDate.getDateBefore(1);
			UFLiteralDate nextDate = curDate.getDateAfter(1);
			// 当天班次的考勤时间段（公休和正常班都适用）
			ITimeScope curDateAttendTimeScope = BillProcessHelper.getAttendTimeScope(preDate,curDate, nextDate, calendarMap, shiftMap,
					CommonMethods.createDateTimeZoneMap(dateOrgMap, timeRuleVO.getTimeZoneMap()));

			// 考勤时间段的开始、结束时间点
			UFDateTime kqStart = curDateAttendTimeScope.getScope_start_datetime();
			UFDateTime kqEnd = curDateAttendTimeScope.getScope_end_datetime();

			if (thePkClass.equals(ShiftVO.PK_GX)|| aggCalendarVO.getPsnCalendarVO().getGzsj().doubleValue() == 0) {
				// １、加班开始时间点、结束时间点同时处在同一公休时间段内允许校验（注：处于连续跨公休日加班不允许校验，但在“加班单据生成”节点可生成加班单据）
				if (beginDateTime.compareTo(kqStart) >= 0 && beginDateTime.compareTo(kqEnd) <= 0 && endDateTime.compareTo(kqStart) >= 0 && endDateTime.compareTo(kqEnd) <= 0) {
					retVal[i] = Boolean.TRUE;
					continue;
				}
				if (throwException)
					//throw new ValidationException(bill.getBillinfo_psnname() + "的加班开始时间和加班结束时间不在同一公休时间段内,不允许校验!");
					//TODO:第x行多语,2008.11.05
					throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1683"
/*@res "{0}的加班开始时间和加班结束时间不在同一公休时间段内,不允许校验!"*/, getPsnName(pk_psndoc)));
				retVal[i] = Boolean.FALSE;
				continue;
			}
			// 2、加班开始时间点、结束时间点同时处在同一正常班次的提前加班时段或延时加班时段允许校验

			// 前一日班次
			AggPsnCalendar preCalendar = calendarMap.get(preDate);
			ShiftVO preShift = preCalendar == null ? null : preCalendar.getPsnCalendarVO() == null ? null : ShiftServiceFacade.getShiftVOFromMap(shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
			// 后一日班次
			AggPsnCalendar nextCalendar = calendarMap.get(nextDate);
			ShiftVO nextShift = nextCalendar == null ? null : nextCalendar.getPsnCalendarVO() == null ? null : ShiftServiceFacade.getShiftVOFromMap(shiftMap, nextCalendar.getPsnCalendarVO().getPk_shift());

			TimeZone curTimeZone = CommonUtils.ensureTimeZone(timeZoneMap.get(dateOrgMap.get(curDate)));
			TimeZone preTimeZone = CommonUtils.ensureTimeZone(timeZoneMap.get(dateOrgMap.get(preDate)));
			TimeZone nextTimeZone = CommonUtils.ensureTimeZone(timeZoneMap.get(dateOrgMap.get(nextDate)));
			// 正常工作日的工作时间段
			ITimeScope[] allWorkTimes = BillProcessHelper.getWorkTimeScopes(curDate.toString(), aggCalendarVO, preShift, nextShift,
					curTimeZone,preTimeZone,nextTimeZone);

			// 提前加班考勤开始时间为该日班次考勤开始时间
			UFDateTime jbEKqStartTime = kqStart;
			// 提前加班考勤结束时间为该日班次第一个时段上班时间
			ITimeScope firstWorkTime = allWorkTimes[0];
			UFDateTime jbEKqEndTime = firstWorkTime.getScope_start_datetime();

			if (beginDateTime.compareTo(jbEKqStartTime) >= 0 && beginDateTime.compareTo(jbEKqEndTime) <= 0 && endDateTime.compareTo(jbEKqStartTime) >= 0 && endDateTime.compareTo(jbEKqEndTime) <= 0) {
				retVal[i] = Boolean.TRUE;
				continue;
			}

			// 延时加班考勤开始时间为该日班次最后一个下班时段加上班次定义中延后加班的开始计时时长
			ITimeScope lastWorkTime = allWorkTimes[allWorkTimes.length - 1];
			UFDateTime jbLKqStartTime = lastWorkTime.getScope_end_datetime();
			// 延时加班考勤结束时间为该日班次考勤结束时间
			UFDateTime jbLKqEndTime = kqEnd;

			if (beginDateTime.compareTo(jbLKqStartTime) >= 0 && beginDateTime.compareTo(jbLKqEndTime) <= 0 && endDateTime.compareTo(jbLKqStartTime) >= 0 && endDateTime.compareTo(jbLKqEndTime) <= 0) {
				retVal[i] = Boolean.TRUE;
				continue;
			}
			if (throwException)
				//throw new ValidationException(bill.getBillinfo_psnname() + "的加班时间段包含上班时间段,或者跨越了班次,不允许校验!");
				//TODO:第x行多语,2008.11.05
				throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1684"
/*@res "{0}的加班时间段包含上班时间段,或者跨越了班次,不允许校验!"*/, getPsnName(pk_psndoc)));
			retVal[i] = Boolean.FALSE;
		}
		return retVal;
	}

	/**
	 * 校验加班单的工作日历是否完整。判断规则是：
	 * 如果找到了加班单的归属日，则完整，否则工作日历不完整
	 * 注意，对加班单的校验，分两种情况：需要校验，和不需要校验。这个方法适用于不需要要校验的情况
	 * @param pkCorp
	 * @param bills
	 * @throws BusinessException
	 */
	public static void checkCalendarCompleteForOvertime(String pk_org, OvertimeCommonVO[] bills) throws BusinessException {
		if (bills == null || bills.length == 0)
			return;
		Map<OvertimeCommonVO, Map<UFLiteralDate, AggPsnCalendar>> calendarMap = new HashMap<OvertimeCommonVO, Map<UFLiteralDate, AggPsnCalendar>>();
		//查出所有单据可能涉及到的工作日历。由于每张单据的时间都可能不一样，因此每张单据单独进行一次查询，而不能按照一个统一的时间查询
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);

		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRuleVO.getTimeZoneMap();
		Map<String, AggShiftVO> allShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		Map<String, AggShiftVO> allShiftMap = bills[0].getAggShiftMap();
//		if(allShiftMap == null){
//			allShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//			bills[0].setAggShiftMap(allShiftMap);
//		}
		//优化处理掉
//		for (OvertimeCommonVO bill : bills) {
//			//人员的工作日历map,要求往前往后都推2天，也就是说，一般情况下，map里面的天数会比单据天数多4天
//			Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap = calendarService.
//				queryCalendarMapByPsnDates(null, bill.getPk_psndoc(), bill.getOvertimebegindate().getDateBefore(2),
//						bill.getOvertimeenddate().getDateAfter(2),timeZoneMap,allShiftMap);
//			calendarMap.put(bill, psnCalendarMap);
//		}
		Map<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>> billCalendarVOsMap = calendarService.queryCalendarVOsByPsnsDates(null, bills,timeZoneMap, allShiftMap);
		for(ITimeScopeBillBodyVO bill:billCalendarVOsMap.keySet()){
			calendarMap.put((OvertimeCommonVO) bill, billCalendarVOsMap.get(bill));
		}
		Map<String, ShiftVO> shiftMap = new HashMap<String, ShiftVO>();
		for(String pk_shift:allShiftMap.keySet()){
			shiftMap.put(pk_shift, allShiftMap.get(pk_shift).getShiftVO());
		}
		BillValidator.checkCalendarCompleteForOvertime(bills, calendarMap, shiftMap,
				CommonMethods.createPsnDateTimeZoneMap(queryPsnDateOrgMap(bills),timeZoneMap));
	}

	private static Map<String, Map<UFLiteralDate, String>> queryPsnDateOrgMap(IDateScopeBillBodyVO[] vos) throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return null;
		String[] pk_psndocs = StringPiecer.getStrArrayDistinct((SuperVO[])vos, "pk_psndoc");
		// 取所有VO中的最早开始日期和最晚结束日期（减少了SQL量，牺牲少量内存）
		IDateScope scope = new DefaultDateScope(DateScopeUtils.findEarliestBeginDate(vos).getDateBefore(2), DateScopeUtils.findLatestEndDate(vos).getDateAfter(2));
		return NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(pk_psndocs, new IDateScope[]{scope});
		//按人分组
//		Map<String, SuperVO[]> groupedPsnVOs = CommonUtils.group2ArrayByField("pk_psndoc", (SuperVO[])vos);
//		Map<String, Map<UFLiteralDate, String>> retMap = new HashMap<String, Map<UFLiteralDate,String>>();
//		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
//		for(String pk_psndoc:groupedPsnVOs.keySet()){
//			IDateScopeBillBodyVO[] psnVOs = (IDateScopeBillBodyVO[]) groupedPsnVOs.get(pk_psndoc);
//			Map<UFLiteralDate, String> dateOrgMap = psndocService.queryDateJobOrgMap(pk_psndoc, DateScopeUtils.findEarliestBeginDate(psnVOs).getDateBefore(2), DateScopeUtils.findLatestEndDate(psnVOs).getDateAfter(2));
//			retMap.put(pk_psndoc, dateOrgMap);
//		}
//		return retMap;
	}

	public static void checkCalendarCompleteForAway(ITimeScopeBillBodyVO bill) throws BusinessException {
		checkCalendarCompleteForAway(bill.getPk_org(), new ITimeScopeBillBodyVO[] { bill });
	}

	/**
	 * 检查出差单的工作日历是否完整
	 * @param pkCorp
	 * @param bills
	 * @throws BusinessException
	 */
	public static void checkCalendarCompleteForAway(String pk_org, ITimeScopeBillBodyVO[] bills) throws BusinessException {
		if (bills == null || bills.length == 0)
			return;
		//按出差类别分组
		//SuperVO[] superVOs = ArrayHelper.cast(bills, SuperVO.class);
		//Map<String, SuperVO[]> map = CommonUtils.group2ArrayByField(AwayCommonVO.PK_AWAYTYPE, superVOs);
		AwayCommonVO[] commonVOs = ArrayHelper.cast(bills, AwayCommonVO.class);
		Map<String, AwayCommonVO[]> map = CommonUtils.group2ArrayByField(AwayCommonVO.PK_AWAYTYPE, commonVOs);
		String[] awayTypes = map.keySet().toArray(new String[0]);
		TimeItemCopyVO[] awayTypeCopyVOs = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryCopyTypesByDefPK(pk_org, awayTypes, TimeItemCopyVO.AWAY_TYPE);
		Map<String, TimeItemCopyVO> awayTypeMap = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, awayTypeCopyVOs);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRuleVO.getTimeZoneMap();
		Map<String, AggShiftVO> allShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		for(String pk_type:awayTypes){
//			String pk_type = typeIterator.next();
			TimeItemCopyVO timeitemVO = awayTypeMap.get(pk_type);
			//如果公休日计出差，则不用校验.0-不计，1-计
			int gxComType = timeitemVO.getGxcomtype() == null ? 0 : timeitemVO.getGxcomtype().intValue();
			if (gxComType == 1)
				continue;
			Map<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>> calendarMap = new HashMap<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>>();
			//查出所有单据可能涉及到的工作日历。由于每张单据的时间都可能不一样，因此每张单据单独进行一次查询，而不能按照一个统一的时间查询
			//SuperVO[] vos = (SuperVO[]) map.get(pk_type); 会造成强制类型转换错误  SuperVO 不能转换为ITimeScopeBillBodyVO
			AwayCommonVO[] vos = (AwayCommonVO[]) map.get(pk_type);
			//循环查询优化掉
//			for (SuperVO vo : vos) {
//				ITimeScopeBillBodyVO bill = (ITimeScopeBillBodyVO)vo;
//				//人员的工作日历map,要求往前往后都推2天，也就是说，一般情况下，map里面的天数会比单据天数多4天
//				//下面的pk_org为null，表示要查询跨HR组织的工作日历，以免误报“工作日历不完整错误”
//				Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap = calendarService.queryCalendarMapByPsnDates(null,
//						bill.getPk_psndoc(), bill.getBillBeginDate().getDateBefore(2), bill.getBillEndDate().getDateAfter(2),timeZoneMap,allShiftMap);
//				calendarMap.put(bill, psnCalendarMap);
//			}
			calendarMap = calendarService.queryCalendarVOsByPsnsDates(null, bills,timeZoneMap, allShiftMap);
			BillValidator.checkCalendarCompleteForAway(timeitemVO, (ITimeScopeBillBodyVO[])vos, calendarMap);
		}
	}

	private static String getPsnName(String pk_psndoc) throws DAOException{
		PsndocVO psndocVO = (PsndocVO)new BaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc);
//		return psndocVO.getName();
		return psndocVO.getMultiLangName();

	}

	@SuppressWarnings("unchecked")
	public static void checkAgentPsn(IAgentPsn[] agentPsnVOs) throws BusinessException {
		//首先校验这些人员的工作交接人，工作交接人不能为本人
		List<IAgentPsn> agentPsnList = new ArrayList<IAgentPsn>();
		for(IAgentPsn vo:agentPsnVOs){
			if(StringUtils.isNotEmpty(vo.getPk_agentpsn())){
				//如果工作记录相等，则肯定是同一人，直接抛异常
				if(vo.getPk_agentpsn().equals(vo.getPk_psnjob()))
					throw new BusinessException(PublicLangRes.AGENTPSNCANOTBESELF());
				agentPsnList.add(vo);
			}
		}
		BaseDAO dao = new BaseDAO();
		if(agentPsnList.size()>0){
			IAgentPsn[] hasAgentPsnVOs = agentPsnList.toArray(new IAgentPsn[0]);
			InSQLCreator isc = new InSQLCreator();
				String[] agentPsnjobs = new HashSet<String>(Arrays.asList(StringPiecer.getStrArray(ArrayHelper.cast(hasAgentPsnVOs, SuperVO.class), IAgentPsn.PK_AGENTPSN))).toArray(new String[0]);
				String agentPsnjobInSQL = isc.getInSQL(agentPsnjobs);
				PsnJobVO[] agentPsnjobVOs = (PsnJobVO[]) CommonUtils.toArray(PsnJobVO.class, dao.retrieveByClause(PsnJobVO.class, PsnJobVO.PK_PSNJOB+" in ("+agentPsnjobInSQL+")"));
				Map<String, PsnJobVO> agentPsnjobVOMap = CommonUtils.toMap(PsnJobVO.PK_PSNJOB, agentPsnjobVOs);
				for(IAgentPsn vo:hasAgentPsnVOs){
					if(vo.getPk_psndoc().equals(agentPsnjobVOMap.get(vo.getPk_agentpsn()).getPk_psndoc()))
						throw new BusinessException(PublicLangRes.AGENTPSNCANOTBESELF());
				}
		}
	}

	public static void checkAgentPsn(AggLeaveVO[] aggVOs) throws BusinessException {
		if(ArrayUtils.isEmpty(aggVOs))
			return;
		List<LeavebVO> bvoList = BillProcessHelper.toLeavebVOList(aggVOs);
		checkAgentPsn(bvoList.toArray(new LeavebVO[0]));
	}

	public static  void checkAgentPsn(AggAwayVO[] aggVOs) throws BusinessException {
		if(ArrayUtils.isEmpty(aggVOs))
			return;
		List<AwaybVO> bvoList = BillProcessHelper.toAwaybVOList(aggVOs);
		checkAgentPsn(bvoList.toArray(new AwaybVO[0]));
	}

	public static <T extends AggregatedValueObject> void checkBillCodeUnique(Class<T> approveAggVOClz,
			AggregatedValueObject[] aggVOs) throws BusinessException {
		if(ArrayUtils.isEmpty(aggVOs))
			return;
		String billType = approveAggVOClz.getAnnotation(HrPfInfo.class).billType();
		String headClassName = approveAggVOClz.getAnnotation(AggVoInfo.class).parentVO();
		Class<?> headClass = null;
		try {
			headClass = Class.forName(headClassName);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		//单据表名
		String tableName = headClass.getAnnotation(Table.class).tableName();
		//单据编码字段名
		String billCodeFiedlName = headClass.getAnnotation(BillCodeFieldName.class).fieldName();
		//单据主键字段名
		String billPkFiedlName = headClass.getAnnotation(IDColumn.class).idColumn();
		SuperVO[] vos = new SuperVO[aggVOs.length];
		for(int i = 0;i < aggVOs.length;i++){
			vos[i] = (SuperVO) aggVOs[i].getParentVO();
		}
		String[] fields = new String[]{IBaseServiceConst.PK_ORG_FIELD, billCodeFiedlName, billPkFiedlName};
		String tmpTableName = new InSQLCreator().insertValues(tableName+ITempTableConst.SUFFIX+"u", fields, fields, vos);
		String sql = " select t."+billCodeFiedlName+" from "+tableName+" t where t.pk_billtype='"+billType+"' and exists(select 1 from "+tmpTableName+
			" s where s.pk_org=t.pk_org and s."+billCodeFiedlName+"=t."+billCodeFiedlName+
			" and isnull(s."+billPkFiedlName+", '~')<>t."+billPkFiedlName+")";
		Object c = new BaseDAO().executeQuery(sql, new ColumnProcessor());
		if(c!=null)
			throw new BusinessException(PublicLangRes.BILLCODEEXISTS((String) c));
	}

//	public static <T extends AggregatedValueObject> void checkBillCodeUnique(Class<T> approveAggVOClz,
//			AggregatedValueObject[] aggVOs) throws BusinessException {
//		if(ArrayUtils.isEmpty(aggVOs))
//			return;
//		String billType = approveAggVOClz.getAnnotation(HrPfInfo.class).billType();
//		String headClassName = approveAggVOClz.getAnnotation(AggVoInfo.class).parentVO();
//		Class<?> headClass = null;
//		try {
//			headClass = Class.forName(headClassName);
//		} catch (ClassNotFoundException e) {
//			Logger.error(e.getMessage(), e);
//			throw new BusinessException(e.getMessage(), e);
//		}
//		//单据表名
//		String tableName = headClass.getAnnotation(Table.class).tableName();
//		//单据编码字段名
//		String billCodeFiedlName = headClass.getAnnotation(BillCodeFieldName.class).fieldName();
//		//单据主键字段名
//		String billPkFiedlName = headClass.getAnnotation(IDColumn.class).idColumn();
//		String sql1 ="select top 1 1 from "+tableName+" where "+ billCodeFiedlName + " = ? and pk_org = ? and pk_billtype ='"+billType+"' ";
//		String sql2 = sql1+ " and " + billPkFiedlName + " <> ? ";
//		BaseDAO dao = new BaseDAO();
//		SQLParameter para = new SQLParameter();
//		ColumnProcessor processor = new ColumnProcessor();
//		for(AggregatedValueObject aggVO:aggVOs){
//			SuperVO hvo = (SuperVO) aggVO.getParentVO();
//			String sql = null;
//			para.clearParams();
//			String billCode = (String)hvo.getAttributeValue(billCodeFiedlName);
//			para.addParam(billCode);
//			para.addParam(hvo.getAttributeValue(IBaseServiceConst.PK_ORG_FIELD));
//			if(hvo.getPrimaryKey() != null){
//				sql = sql2;
//				para.addParam(hvo.getPrimaryKey());
//			}
//			else{
//				sql = sql1;
//			}
//			Object c = dao.executeQuery(sql, para, processor);
//			if(c!=null)
//				throw new BusinessException(PublicLangRes.BILLCODEEXISTS(billCode));
//		}
//	}
	 /* 判断单据的日期内是否有出勤正常的数据
	 */
	public static void checkAttend(ITimeScopeBillBodyVO[] vos) throws BusinessException{
		for(ITimeScopeBillBodyVO vo:vos){
			TimeDataVO[] datas=	(TimeDataVO[]) NCLocator.getInstance().lookup(ITimeDataQueryMaintain.class).queryByPsn(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
			if(datas != null){//将是否为空的判断放在外边，避免空指针异常
				for(TimeDataVO data:datas){
					if(	data.getTbmstatus()==null&&!data.getDirty_flag().booleanValue()){
						throw new BusinessException(ResHelper.getString("6017hrta","0hrta0053")
/*@res "单据中存在考勤正常的时间段"*/);
					}
				}
			}


		}
	}
	/**
	 * 判断时长为0的纪录，如果有，则抛异常
	 * @param vos
	 * @throws BusinessException
	 */
	public static void checkZeroLength(ITimeScopeBillBodyVO[] vos) throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return;
		List<String> zeroList = new ArrayList<String>();
		for(ITimeScopeBillBodyVO vo:vos){
			if(((SuperVO)vo).getStatus()==VOStatus.DELETED)
				continue;
			UFDouble length = null;
			if(vo instanceof OvertimeCommonVO){
				length = ((OvertimeCommonVO)vo).getOvertimehour();
			}
			else if(vo instanceof LeaveCommonVO){//哺乳假要取哺乳假时长
				if(((LeaveCommonVO) vo).getIslactation()!=null &&((LeaveCommonVO) vo).getIslactation().booleanValue()){
					length=((LeaveCommonVO)vo).getLactationhour();
				}
				else
					length = vo.getLength();
			}
			else
				length = vo.getLength();
			if(length==null||length.equals(UFDouble.ZERO_DBL))
				zeroList.add(vo.getPk_psndoc());
		}
		if(zeroList.size()==0)
			return;
		String psnNames = CommonUtils.getPsnNames(zeroList.toArray(new String[0]));
		throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1835"
				/*@res "{0}的单据时长为0!"*/, psnNames));
	}
}