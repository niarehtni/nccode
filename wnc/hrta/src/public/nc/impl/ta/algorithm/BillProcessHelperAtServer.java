package nc.impl.ta.algorithm;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.AwayServiceFacade;
import nc.itf.ta.CheckTimeServiceFacade;
import nc.itf.ta.HRHolidayServiceFacade;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IHolidayRuleQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.LeaveServiceFacade;
import nc.itf.ta.OverTimeServiceFacade;
import nc.itf.ta.ShutdownServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.DateTimeUtils;
import nc.itf.ta.algorithm.ICheckTime;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.itf.ta.algorithm.ITimeScopeWithBillType;
import nc.itf.ta.algorithm.SolidifyUtils;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.holiday.HolidayInfo;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.algorithm.SolidifyPara;
import nc.vo.ta.away.AggAwayVO;
import nc.vo.ta.away.AwayCommonVO;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.bill.ITimeScopeBillAggVO;
import nc.vo.ta.bill.ITimeScopeBillBodyVO;
import nc.vo.ta.bill.ITimeScopeBillHeadVO;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.holidayrule.HolidayRuleVO;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.shutdown.ShutdownRegVO;
import nc.vo.ta.timeitem.AwayTypeCopyVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.ShutDownTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class BillProcessHelperAtServer {

	/**
	 * 加班出差休假的聚合vo计算时长的 公共方法
	 * 
	 * @param <E>
	 * @param <T>
	 * @param billType
	 * @param aggVO
	 * @throws NegativeArraySizeException
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private static <E extends ITimeScopeBillHeadVO, T extends ITimeScopeBillBodyVO> void calculateAggLength(
			int billType, ITimeScopeBillAggVO<E, T> aggVO) throws NegativeArraySizeException, BusinessException {
		// 表头vo
		E headVO = aggVO.getHeadVO();
		// 人员主键和类别主键
		String pk_psndoc = headVO.getPk_psndoc();
		String pk_timeitem = headVO.getPk_timeitem();
		// 表体vo
		T[] bodyVOs = aggVO.getBodyVOs();
		// 没有设置人员或者类别，都将不能计算，所有时长都设置为0
		if (StringUtils.isBlank(pk_psndoc) || StringUtils.isBlank(pk_timeitem)) {
			headVO.setLength(new UFDouble(0));
			if (!ArrayUtils.isEmpty(bodyVOs))
				for (T bvo : bodyVOs) {
					bvo.setLength(new UFDouble(0));
				}
			return;
		}
		if (ArrayUtils.isEmpty(bodyVOs)) {
			aggVO.getHeadVO().setLength(new UFDouble(0));
			return;
		}
		// 过滤掉被delete的，以及开始时间结束时间为空的（因为aggvo是从客户端传过来的，因此有可能会有被删除的，或者开始结束时间输入不完整的）
		List<T> calList = new ArrayList<T>();
		for (T vo : bodyVOs) {
			if (((SuperVO) vo).getStatus() == VOStatus.DELETED || vo.getScope_start_datetime() == null
					|| vo.getScope_end_datetime() == null
					|| !vo.getScope_start_datetime().before(vo.getScope_end_datetime())) {
				vo.setLength(new UFDouble(0));
				continue;
			}
			// 正常的数据才需要计算
			calList.add(vo);
			// 容错处理：传来的vo中，bodyvo可能没有人员主键、类别主键，需要设置一下
			vo.setPk_psndoc(aggVO.getHeadVO().getPk_psndoc());
			vo.setPk_timeitem(aggVO.getHeadVO().getPk_timeitem());
		}
		if (calList.size() == 0) {
			aggVO.getHeadVO().setLength(new UFDouble(0));
			return;
		}
		calculateLengths(aggVO.getHeadVO().getPk_org(), billType,
				(SuperVO[]) calList.toArray((T[]) Array.newInstance(calList.get(0).getClass(), 0)));
		double sum = 0;
		// 累加所有表体vo的时长，设置到表头vo上
		for (T vo : calList) {
			sum += vo.getLength().doubleValue();
		}
		aggVO.getHeadVO().setLength(new UFDouble(sum));
	}

	/**
	 * 加班出差休假停工的时长计算公共类，vos是申请子表vo数组，或者是登记vo数组，或者是加班单据生成vo数组
	 * 
	 * @param <T>
	 * @param <E>
	 * @param pk_org
	 * @param billType
	 * @param vos
	 * @throws BusinessException
	 */
	private static <T extends SuperVO, E extends TimeItemCopyVO> void calculateLengths(String pk_org, int billType,
			T[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		// 查询出组织内的所有类别
		Map<String, E> timeItemVOMap = queryAllTimeItemVOMap(pk_org, billType);
		// 考勤规则
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		BillMutexRule billMutexRule = BillMutexRule.createBillMutexRule(timeRuleVO.getBillmutexrule());
		// 所有班次
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		// Map<String,AggShiftVO> aggShiftMap = null;
		// if(vos[0] instanceof OvertimeCommonVO){
		// OvertimeCommonVO ocvo = (OvertimeCommonVO)vos[0];
		// aggShiftMap = ocvo.getAggShiftMap();
		// }
		// if(aggShiftMap == null){
		// aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		// }
		// if(vos[0] instanceof OvertimeCommonVO){
		// OvertimeCommonVO ocvo = (OvertimeCommonVO)vos[0];
		// ocvo.setAggShiftMap(aggShiftMap);
		// }
		// 所有的休假类别（如果是计算加班，则除了加班类别之外，还需要休假类别，因为加班的时长计算依赖于休假类别--南孚电池需求）
		LeaveTypeCopyVO[] leaveTypes = null;
		if (billType == BillMutexRule.BILL_OVERTIME) {
			Map<String, TimeItemCopyVO> leaveTypeMap = queryAllTimeItemVOMap(pk_org, BillMutexRule.BILL_LEAVE);
			leaveTypes = MapUtils.isEmpty(leaveTypeMap) ? null : leaveTypeMap.values().toArray(new LeaveTypeCopyVO[0]);
		}
		calculateLengths(pk_org, billType, vos, timeRuleVO, billMutexRule, timeItemVOMap, aggShiftMap, leaveTypes);
	}

	/**
	 * 加班出差休假停工的时长计算公共类，vos是申请子表vo数组，或者是登记vo数组，或者是加班单据生成vo数组
	 * 
	 * @param <T>
	 * @param <E>
	 * @param pk_org
	 * @param billType
	 * @param vos
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private static <T extends SuperVO, E extends TimeItemCopyVO> void calculateLengths(String pk_org, int billType,
			T[] vos, TimeRuleVO timeRuleVO, BillMutexRule billMutexRule, Map<String, E> timeItemVOMap,
			Map<String, AggShiftVO> aggShiftMap, LeaveTypeCopyVO[] leaveTypes) throws BusinessException {
		// 批量初始化计算参数
		Map<String, ShiftVO> shiftMap = CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap);
		Map<String, CalParam4OnePerson> paramMap = initParam(timeRuleVO, billMutexRule, aggShiftMap, shiftMap,
				(ITimeScopeWithBillInfo[]) vos, billType, 1, 1);
		// 按人员分组
		Map<String, T[]> groupedMap = CommonUtils.group2ArrayByField(PsndocVO.PK_PSNDOC, vos);

		// V63添加，若是计算加班时长要考虑假日排班记为加班的情况,此时批量查询出需要的信息
		Map<String, HRHolidayVO[]> psnOverTimeHolidayScope = new HashMap<String, HRHolidayVO[]>();
		if (billType == BillMutexRule.BILL_OVERTIME)
			psnOverTimeHolidayScope = getOverTimeHolidayScope((OvertimeCommonVO[]) vos);

		// 按人员循环计算时长
		for (String pk_psndoc : groupedMap.keySet()) {
			ITimeScopeWithBillInfo[] psnBills = (ITimeScopeWithBillInfo[]) groupedMap.get(pk_psndoc);
			switch (billType) {
			case BillMutexRule.BILL_AWAY:
				calAwayLength4OnePerson(pk_org, pk_psndoc, (AwayCommonVO[]) psnBills,
						(Map<String, AwayTypeCopyVO>) timeItemVOMap, timeRuleVO, billMutexRule, paramMap.get(pk_psndoc));
				continue;
			case BillMutexRule.BILL_LEAVE:
				calLeaveLength4OnePerson(pk_org, pk_psndoc, (LeaveCommonVO[]) psnBills,
						(Map<String, LeaveTypeCopyVO>) timeItemVOMap, timeRuleVO, billMutexRule,
						paramMap.get(pk_psndoc));
				continue;
			case BillMutexRule.BILL_OVERTIME:
				ITimeScope[] holidayScope = psnOverTimeHolidayScope == null ? null : psnOverTimeHolidayScope
						.get(pk_psndoc);
				calOvertimeLength4OnePerson(pk_org, pk_psndoc, (OvertimeCommonVO[]) psnBills,
						(Map<String, OverTimeTypeCopyVO>) timeItemVOMap, leaveTypes, timeRuleVO, billMutexRule,
						paramMap.get(pk_psndoc), holidayScope);
				continue;
			case BillMutexRule.BILL_SHUTDOWN:
				calShutdownLength4OnePerson(pk_org, pk_psndoc, (ShutdownRegVO[]) psnBills,
						(Map<String, ShutDownTypeCopyVO>) timeItemVOMap, timeRuleVO, billMutexRule,
						paramMap.get(pk_psndoc));
				continue;
			}
		}
	}

	/**
	 * V63添加，查询加班单时间段内人员享有的假日且假日加班记为加班的情况
	 * 
	 * @param vos
	 * @return<key-人员主键，value-单据时间段内享有假日且假日排班记为加班的时间段>
	 * @throws BusinessException
	 */
	public static Map<String, HRHolidayVO[]> getOverTimeHolidayScope(OvertimeCommonVO[] vos) throws BusinessException {
		// key-人员主键，value-单据时间段内享有假日且假日排班记为加班的时间段
		Map<String, HRHolidayVO[]> psnEnjoyHolidayScope = new HashMap<String, HRHolidayVO[]>();
		if (ArrayUtils.isEmpty(vos))
			return psnEnjoyHolidayScope;
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		UFLiteralDate beginDate = maxDateScope.getBegindate();
		UFLiteralDate endDate = maxDateScope.getEnddate();
		String pk_hrorg = vos[0].getPk_org();
		// <业务单元，HolidayInfo>,查询hr组织下所有业务单元的假日信息
		Map<String, HolidayInfo<HRHolidayVO>> holidayinfoMap = NCLocator.getInstance()
				.lookup(IHRHolidayQueryService.class).queryHolidayInfoByHROrg(pk_hrorg, beginDate, endDate);
		if (MapUtils.isEmpty(holidayinfoMap))
			return psnEnjoyHolidayScope;
		// 查询所有业务单元的假日加班规则<业务单元，HolidayRuleVO[]>
		Map<String, HolidayRuleVO[]> buHolidayRuleMap = NCLocator.getInstance().lookup(IHolidayRuleQueryService.class)
				.queryByOrgs(pk_hrorg, beginDate, endDate);
		if (MapUtils.isEmpty(buHolidayRuleMap))
			return psnEnjoyHolidayScope;
		// 构造假日Map
		Map<String, HRHolidayVO[]> buHolidayMap = new HashMap<String, HRHolidayVO[]>();
		for (String pk_bu : holidayinfoMap.keySet()) {
			buHolidayMap.put(pk_bu, holidayinfoMap.get(pk_bu).getHolidayVOs());
		}
		String[] pk_psndocs = StringPiecer.getStrArray(vos, PsndocVO.PK_PSNDOC);
		// 在HR组织中，查询人员享受假期情况，返回<人员，<pk_holiday+业务单元主键,是否享有>>
		Map<String, Map<String, Boolean>> psnEnjoyHolidayMap = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_hrorg,
				pk_psndocs, buHolidayMap);
		if (MapUtils.isEmpty(psnEnjoyHolidayMap))
			return psnEnjoyHolidayScope;
		// 单据的组织是hr组织，而与假日相关都是业务单元,而单据中都没有记录人员所属的业务单元，此处先处理一下
		vos = OverTimeServiceFacade.processJobOrg(vos);
		// 按业务单元分组
		Map<String, OvertimeCommonVO[]> orgotmap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_JOBORG, vos);
		for (String pk_org : orgotmap.keySet()) {
			if (StringUtils.isBlank(pk_org))
				continue;
			HolidayRuleVO[] holidayRuleVOs = buHolidayRuleMap == null ? null : buHolidayRuleMap.get(pk_org);
			psnEnjoyHolidayScope.putAll(getOTHolidayScope(pk_org, orgotmap.get(pk_org), holidayinfoMap.get(pk_org),
					holidayRuleVOs, psnEnjoyHolidayMap));
		}
		return psnEnjoyHolidayScope;
	}

	public static HRHolidayVO[] getOverTimeHolidayScope(String pk_psndoc, UFLiteralDate date, TimeZone timezone)
			throws BusinessException {
		TBMPsndocVO tbmpsndoc = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class)
				.queryByPsndocAndDateTime(pk_psndoc, new UFDateTime(date.toDate()));
		if (tbmpsndoc == null)
			return null;
		// String cond = PsnJobVO.PK_PSNJOB + " = '" + tbmpsndoc.getPk_psnjob()
		// + "' ";
		// PsnJobVO[] psnjobs = (PsnJobVO[])
		// NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null,
		// PsnJobVO.class, cond);
		// if(ArrayUtils.isEmpty(psnjobs))
		// return null;
		OvertimebVO vo = new OvertimebVO();
		vo.setPk_org(tbmpsndoc.getPk_org());
		vo.setPk_psnjob(tbmpsndoc.getPk_psnjob());
		vo.setPk_psndoc(pk_psndoc);
		vo.setBegindate(date);
		vo.setEnddate(date);
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);
		vo.setOvertimebegindate(date);
		vo.setOvertimeenddate(date);
		vo.setOvertimebegintime(timeScope.getScope_start_datetime());
		vo.setOvertimeendtime(timeScope.getScope_end_datetime());
		Map<String, HRHolidayVO[]> overTimeHolidayScope = getOverTimeHolidayScope(new OvertimeCommonVO[] { vo });
		if (MapUtils.isEmpty(overTimeHolidayScope))
			return null;
		return overTimeHolidayScope.get(pk_psndoc);

	}

	/**
	 * V63添加，查询加班单时间段内人员享有的假日且假日加班记为加班的情况
	 * 
	 * @param vos
	 * @param pk_org
	 *            -- 业务单元
	 * @param holidayRuleVOs
	 * @param psnEnjoyHolidayMap
	 * @return<key-人员主键，value-单据时间段内享有假日且假日排班记为加班的时间段>
	 * @throws BusinessException
	 */
	public static Map<String, HRHolidayVO[]> getOTHolidayScope(String pk_org, OvertimeCommonVO[] vos,
			HolidayInfo<HRHolidayVO> holidayInfo, HolidayRuleVO[] holidayRuleVOs,
			Map<String, Map<String, Boolean>> psnEnjoyHolidayMap) throws BusinessException {
		// key-人员主键，value-单据时间段内享有假日且假日排班记为加班的时间段
		Map<String, HRHolidayVO[]> psnEnjoyHolidayScope = new HashMap<String, HRHolidayVO[]>();
		if (ArrayUtils.isEmpty(vos) || StringUtils.isBlank(pk_org))
			return psnEnjoyHolidayScope;
		// 含有假日，考虑假日情况
		if (holidayInfo == null)
			return psnEnjoyHolidayScope;
		HRHolidayVO[] hrHolidayVOs = holidayInfo.getHolidayVOs();
		String[] pk_psndocs = StringPiecer.getStrArray(vos, PsndocVO.PK_PSNDOC);
		Map<String, HolidayRuleVO> holidayRuleMap = CommonUtils.toMap(HolidayRuleVO.PK_HOLIDAY, holidayRuleVOs);
		for (String pk_psndoc : pk_psndocs) {
			// <pk_holiday+业务单元主键,是否享有>
			Map<String, Boolean> psnEnjoymap = psnEnjoyHolidayMap.get(pk_psndoc);
			if (psnEnjoymap.isEmpty())
				continue;
			List<HRHolidayVO> holidayScopeList = new ArrayList<HRHolidayVO>();
			for (HRHolidayVO hrHolidayvo : hrHolidayVOs) {
				String pk_holiday = hrHolidayvo.getPk_holiday();
				Boolean isEnjoy = psnEnjoymap.get(pk_holiday + pk_org);
				// 如果不享有
				if (isEnjoy == null || !isEnjoy.booleanValue())
					continue;
				HolidayRuleVO holidayRuleVO = holidayRuleMap == null ? null : holidayRuleMap.get(pk_holiday);
				// 假日排班是否记为加班
				if (holidayRuleVO == null || holidayRuleVO.getIsovertime() == null
						|| holidayRuleMap.get(pk_holiday).getIsovertime().booleanValue())
					holidayScopeList.add((HRHolidayVO) holidayInfo.getTimeScopeMap().get(pk_holiday));
			}
			if (CollectionUtils.isEmpty(holidayScopeList))
				continue;
			psnEnjoyHolidayScope.put(pk_psndoc, holidayScopeList.toArray(new HRHolidayVO[0]));
		}
		return psnEnjoyHolidayScope;
	}

	private static void calculateLength(int billType, ITimeScopeBillBodyVO regVO) throws BusinessException {
		if (regVO.getScope_start_datetime() == null || regVO.getScope_end_datetime() == null
				|| StringUtils.isBlank(regVO.getPk_psndoc()) || StringUtils.isBlank(regVO.getPk_timeitem())
				|| !regVO.getScope_start_datetime().before(regVO.getScope_end_datetime())) {
			regVO.setLength(new UFDouble(0));
			return;
		}
		ITimeScopeWithBillInfo[] array = (ITimeScopeWithBillInfo[]) Array.newInstance(regVO.getClass(), 1);
		array[0] = regVO;
		calculateLengths(regVO.getPk_org(), billType, (SuperVO[]) array);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TimeItemCopyVO> Map<String, T> queryAllTimeItemVOMap(String pk_org, int billType)
			throws BusinessException {
		ITimeItemQueryService service = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		switch (billType) {
		case BillMutexRule.BILL_AWAY:
			return (Map<String, T>) service.queryAwayCopyTypeMapByOrg(pk_org);
		case BillMutexRule.BILL_LEAVE:
			return (Map<String, T>) service.queryLeaveCopyTypeMapByOrg(pk_org);
		case BillMutexRule.BILL_OVERTIME:
			return (Map<String, T>) service.queryOvertimeCopyTypeMapByOrg(pk_org);
		case BillMutexRule.BILL_SHUTDOWN:
			return (Map<String, T>) service.queryShutDownCopyTypeMapByOrg(pk_org);
		}
		return null;
	}

	public static void calAwayLength(AggAwayVO aggVO) throws BusinessException {
		calculateAggLength(BillMutexRule.BILL_AWAY, aggVO);
	}

	public static void calAwayLength(AwayRegVO regVO) throws BusinessException {
		calculateLength(BillMutexRule.BILL_AWAY, regVO);
	}

	public static void calAwayLength(String pk_org, AwayCommonVO[] vos) throws BusinessException {
		calculateLengths(pk_org, BillMutexRule.BILL_AWAY, vos);
	}

	/**
	 * 计算一个人员的一批出差单的时长
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param vos
	 * @param timeItemVOMap
	 * @param aggShiftMap
	 * @param timeRuleVO
	 * @param billMutexRule
	 * @throws BusinessException
	 */
	private static void calAwayLength4OnePerson(String pk_org, String pk_psndoc, AwayCommonVO[] vos,
			Map<String, AwayTypeCopyVO> timeItemVOMap, TimeRuleVO timeRuleVO, BillMutexRule billMutexRule,
			CalParam4OnePerson calParam) throws BusinessException {
		// 按单据循环处理
		for (AwayCommonVO vo : vos) {
			ITimeScopeWithBillType[] filterdArray = null;
			// 将加班单休假单停工单和出差单进行交切处理
			ITimeScopeWithBillType[] crossArray = BillProcessHelper.crossAllBills(new AwayCommonVO[] { vo },
					calParam.overtimeBills, calParam.leaveBills, calParam.shutdownBills);
			// 通过冲突规则来过滤单据
			List<ITimeScopeWithBillType> filteredList = BillProcessHelper.filterBills(crossArray,
					BillMutexRule.BILL_AWAY, billMutexRule);
			// 如果通过冲突规则过滤出的单据为空，则出差的时长肯定为0,不用往下处理，continue即可
			if (filteredList == null || filteredList.size() == 0) {
				vo.setAwayhour(new UFDouble(0));
				continue;
			}
			filterdArray = filteredList.toArray(new ITimeScopeWithBillType[0]);

			// 否则就要处理
			double result = calAwayLength(pk_org, pk_psndoc, timeItemVOMap.get(vo.getPk_awaytype()), calParam,
					filterdArray);
			vo.setAwayhour(CommonMethods.processByDecimalDigitsAndRoundMode(result, timeRuleVO));
		}
	}

	// @Deprecated
	// private static void calAwayLength4OnePerson1(
	// String pk_org,
	// String pk_psndoc,
	// AwayCommonVO[] vos,
	// Map<String, AwayTypeCopyVO> timeItemVOMap,
	// Map<String,AggShiftVO> aggShiftMap,
	// Map<String, ShiftVO> shiftMap,
	// TimeRuleVO timeRuleVO,
	// BillMutexRule billMutexRule) throws BusinessException{
	// //初始化计算参数
	// CalParam4OnePerson calParam = initParam(pk_psndoc, timeRuleVO,
	// billMutexRule,aggShiftMap,shiftMap, vos, BillMutexRule.BILL_AWAY);
	// //按单据循环处理
	// for (AwayCommonVO vo : vos) {
	// ITimeScopeWithBillType[] filterdArray = null;
	// //将加班单休假单停工单和出差单进行交切处理
	// ITimeScopeWithBillType[] crossArray = BillProcessHelper.crossAllBills(new
	// AwayCommonVO[]{vo}, calParam.overtimeBills, calParam.leaveBills,
	// calParam.shutdownBills);
	// //通过冲突规则来过滤单据
	// List<ITimeScopeWithBillType> filteredList =
	// BillProcessHelper.filterBills(crossArray, BillMutexRule.BILL_AWAY,
	// billMutexRule);
	// //如果通过冲突规则过滤出的单据为空，则出差的时长肯定为0,不用往下处理，continue即可
	// if (filteredList == null || filteredList.size() == 0){
	// vo.setAwayhour(new UFDouble(0));
	// continue;
	// }
	// filterdArray = filteredList.toArray(new ITimeScopeWithBillType[0]);
	//
	// //否则就要处理
	// double result = calAwayLength(pk_org, pk_psndoc,
	// timeItemVOMap.get(vo.getPk_awaytype()), calParam, filterdArray);
	// vo.setAwayhour(CommonMethods.processByDecimalDigitsAndRoundMode(result,
	// timeRuleVO));
	// }
	// }
	/**
	 * 计算某人某个休假类别在某个年度的已休时长，适用于按年结算的类别
	 * 
	 * @param pkCorp
	 * @param pk_psndoc
	 * @param pk_timeitem
	 * @param year
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 * @throws BusinessException
	 */
	// public double calConsumedLeaveLength(String pkCorp, String pk_psndoc,
	// TimeitemVO timeitemVO, BillMutexRule billMutexRule, String year) throws
	// NamingException, SQLException, BusinessException {
	// return calConsumedLeaveLength(pkCorp, pk_psndoc, timeitemVO,
	// billMutexRule, year, null);
	// }
	//
	// /**
	// * 计算某人某个休假类别在某个期间的已休时长，适用于按期间结算的类别。
	// * @param pkCorp
	// * @param pk_psndoc
	// * @param pk_timeitem
	// * @param year
	// * @param month
	// * @return
	// * @throws NamingException
	// * @throws SQLException
	// * @throws BusinessException
	// */
	// public double calConsumedLeaveLength(String pkCorp, String pk_psndoc,
	// TimeitemVO timeitemVO, BillMutexRule billMutexRule, String year, String
	// month) throws NamingException, SQLException,
	// BusinessException {
	// //首先查询出此期间内的所有审批通过的单据和登记单据
	// LmholidayDMO dmo = new LmholidayDMO();
	// LeavehbVO[] leavehbVOs = dmo.queryByDateCorpPsnTypeForPeriod(pkCorp,
	// pk_psndoc, timeitemVO.getPrimaryKey(), year, month);
	// if (leavehbVOs == null || leavehbVOs.length == 0)
	// return 0;
	// //转换为标准的休假vo
	// LeavebVO[] leavebVOs = LeavehbVO.toLeavebVOs(leavehbVOs);
	// return calLeaveLength(pkCorp, pk_psndoc, timeitemVO, billMutexRule,
	// leavebVOs, true);
	// }

	/**
	 * 计算某个休假申请单的休假时长，包括每条子表的时长和总时长
	 * 
	 * @param aggVO
	 * @throws BusinessException
	 */
	public static void calLeaveLength(AggLeaveVO aggVO) throws BusinessException {
		calculateAggLength(BillMutexRule.BILL_LEAVE, aggVO);
	}

	/**
	 * 计算一批休假单的休假时长 用于批量新增的场景，可能是多个人员、多种休假类别的混合
	 * 由于单据的时长互相之间可能产生耦合（即一个时间段的休假时长依赖于其他的休假单据），因此计算每条时长时，还要考虑其他单据
	 * 
	 * @param aggVOs
	 * @throws BusinessException
	 * @throws SQLException
	 * @throws NamingException
	 */
	public static void calLeaveLength(String pk_org, LeaveCommonVO[] vos) throws BusinessException {
		calculateLengths(pk_org, BillMutexRule.BILL_LEAVE, vos);
	}

	/**
	 * 计算某个休假登记单的休假时长
	 * 
	 * @param regVO
	 * @throws BusinessException
	 */
	public static void calLeaveLength(LeaveRegVO regVO) throws BusinessException {
		calculateLength(BillMutexRule.BILL_LEAVE, regVO);
	}

	/**
	 * 计算某个人员组织关系某个休假类别在某个年度的已休时长，适用于按年结算或按入职日结算的类别
	 * 因为假期计算时，同时会计算很多人的已休时长，此方法会被循环调用，因此考勤规则、班次这些公共参数都在循环外查好传进来，而不是在循环体中重复查询
	 * 
	 * @param pkCorp
	 * @param pk_psndoc
	 * @param pk_timeitem
	 * @param year
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 * @throws BusinessException
	 */
	public static double calConsumedLeaveLength(String pk_org, String pk_psnorg, LeaveTypeCopyVO timeitemVO,
			String year, int leaveIndex, TimeRuleVO timeRuleVO, BillMutexRule billMutexRule,
			Map<String, AggShiftVO> aggShiftMap) throws BusinessException {
		return calConsumedLeaveLength(pk_org, pk_psnorg, timeitemVO, year, null, leaveIndex, timeRuleVO, billMutexRule,
				aggShiftMap);
	}

	/**
	 * 计算某个人员组织关系某个休假类别在某个期间的已休时长，适用于按期间结算的类别。
	 * 
	 * @param pkCorp
	 * @param pk_psndoc
	 * @param pk_timeitem
	 * @param year
	 * @param month
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 * @throws BusinessException
	 */
	public static double calConsumedLeaveLength(String pk_org, String pk_psnorg, LeaveTypeCopyVO timeitemVO,
			String year, String month, int leaveIndex, TimeRuleVO timeRuleVO, BillMutexRule billMutexRule,
			Map<String, AggShiftVO> aggShiftMap) throws BusinessException {
		// 首先查询出此期间内的所有审批通过的单据和登记单据
		LeaveRegVO[] leaveVOs = LeaveServiceFacade.queryByPsnLeaveTypePeriod(pk_org, pk_psnorg, timeitemVO, year,
				month, leaveIndex);
		return calConsumedLeaveLength(leaveVOs, pk_org, timeitemVO, timeRuleVO, billMutexRule, aggShiftMap);
	}

	/**
	 * 计算某个人员组织关系某个休假类别在某个期间的已休时长，适用于按期间结算的类别。
	 * 
	 * @param leaveVOs
	 * @param pk_org
	 * @param timeitemVO
	 * @param timeRuleVO
	 * @param billMutexRule
	 * @param aggShiftMap
	 * @return
	 * @throws BusinessException
	 */
	public static double calConsumedLeaveLength(LeaveRegVO[] leaveVOs, String pk_org, LeaveTypeCopyVO timeitemVO,
			TimeRuleVO timeRuleVO, BillMutexRule billMutexRule, Map<String, AggShiftVO> aggShiftMap)
			throws BusinessException {
		if (ArrayUtils.isEmpty(leaveVOs))
			return 0;
		Map<String, LeaveTypeCopyVO> typeMap = new HashMap<String, LeaveTypeCopyVO>();
		typeMap.put(timeitemVO.getPk_timeitem(), timeitemVO);
		calculateLengths(pk_org, BillMutexRule.BILL_LEAVE, leaveVOs, timeRuleVO, billMutexRule, typeMap, aggShiftMap,
				null);
		double result = 0;
		for (LeaveRegVO vo : leaveVOs) {
			result += vo.getLeavehour().doubleValue();
		}
		return result;
	}

	/**
	 * 计算某个人员组织关系某个休假类别在某个期间的冻结时长，适用于按期间结算的类别。
	 * 
	 * @param pkCorp
	 * @param pk_psndoc
	 * @param pk_timeitem
	 * @param year
	 * @param month
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 * @throws BusinessException
	 */
	public static double calFreezeLeaveLength(String pk_org, String pk_psnorg, LeaveTypeCopyVO timeitemVO, String year,
			String month, int leaveIndex, TimeRuleVO timeRuleVO, BillMutexRule billMutexRule,
			Map<String, AggShiftVO> aggShiftMap) throws BusinessException {
		// 首先查询出此期间内的自由态，审批中的申请单
		LeavebVO[] leaveVOs = LeaveServiceFacade.queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(pk_org, pk_psnorg,
				timeitemVO, year, month, leaveIndex);
		if (ArrayUtils.isEmpty(leaveVOs))
			return 0;
		Map<String, LeaveTypeCopyVO> typeMap = new HashMap<String, LeaveTypeCopyVO>();
		typeMap.put(timeitemVO.getPk_timeitem(), timeitemVO);
		calculateLengths(pk_org, BillMutexRule.BILL_LEAVE, leaveVOs, timeRuleVO, billMutexRule, typeMap, aggShiftMap,
				null);
		double result = 0;
		for (LeavebVO vo : leaveVOs) {
			result += vo.getLeavehour().doubleValue();
		}
		return result;
	}

	/**
	 * 与上面同名方法相同，在批处理时不用每次调用都重新查询了
	 * 
	 * @param leaveVOs
	 * @param pk_org
	 * @param timeitemVO
	 * @param timeRuleVO
	 * @param billMutexRule
	 * @param aggShiftMap
	 * @return
	 * @throws BusinessException
	 */
	public static double calFreezeLeaveLength(LeavebVO[] leaveVOs, String pk_org, LeaveTypeCopyVO timeitemVO,
			TimeRuleVO timeRuleVO, BillMutexRule billMutexRule, Map<String, AggShiftVO> aggShiftMap)
			throws BusinessException {
		// 首先查询出此期间内的自由态，审批中的申请单
		if (ArrayUtils.isEmpty(leaveVOs))
			return 0;
		Map<String, LeaveTypeCopyVO> typeMap = new HashMap<String, LeaveTypeCopyVO>();
		typeMap.put(timeitemVO.getPk_timeitem(), timeitemVO);
		calculateLengths(pk_org, BillMutexRule.BILL_LEAVE, leaveVOs, timeRuleVO, billMutexRule, typeMap, aggShiftMap,
				null);
		double result = 0;
		for (LeavebVO vo : leaveVOs) {
			result += vo.getLeavehour().doubleValue();
		}
		return result;
	}

	/**
	 * 计算某个人的一批休假单的时长
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param vos
	 * @throws BusinessException
	 * @throws SQLException
	 * @throws NamingException
	 */
	private static void calLeaveLength4OnePerson(String pk_org, String pk_psndoc, LeaveCommonVO[] vos,
			Map<String, LeaveTypeCopyVO> timeItemVOMap, TimeRuleVO timeRuleVO, BillMutexRule billMutexRule,
			CalParam4OnePerson calParam) throws BusinessException {
		// ssx added on 2018-08-21
		String initdate = SysInitQuery.getParaString(pk_org, "TBMEXPDATE");

		// 按单据循环处理
		for (LeaveCommonVO vo : vos) {
			// MOD(系統啟用前的休假單據時數認定以單據上記錄的為準)
			// ssx added on 2018-08-21
			if (!StringUtils.isEmpty(initdate)) {
				UFLiteralDate expDate = new UFLiteralDate(initdate);
				if (vo instanceof LeaveRegVO) {
					LeaveRegVO lrVO = (LeaveRegVO) vo;
					if (lrVO.getLeavebegindate().isSameDate(expDate) || lrVO.getLeavebegindate().before(expDate)) {
						vo.setLeavehour(lrVO.getLeavehour());
						continue;
					}
				}
			}
			// end

			ITimeScopeWithBillType[] filterdArray = null;
			// 将加班单出差单停工单和休假单进行交切处理
			ITimeScopeWithBillType[] crossArray = BillProcessHelper.crossAllBills(new LeaveCommonVO[] { vo },
					calParam.overtimeBills, calParam.awayBills, calParam.shutdownBills);
			// 通过冲突规则来过滤单据
			List<ITimeScopeWithBillType> filteredList = BillProcessHelper.filterBills(crossArray,
					BillMutexRule.BILL_LEAVE, billMutexRule);
			// 如果通过冲突规则过滤出的单据为空，则休假的时长肯定为0,不用往下处理，continue即可
			if (filteredList == null || filteredList.size() == 0) {
				vo.setLeavehour(new UFDouble(0));
				continue;
			}
			filterdArray = filteredList.toArray(new ITimeScopeWithBillType[0]);

			// 否则就要处理
			double result = calLeaveLength(pk_org, pk_psndoc, timeItemVOMap.get(vo.getPk_leavetype()), calParam,
					filterdArray);
			vo.setLeavehour(CommonMethods.processByDecimalDigitsAndRoundMode(result, timeRuleVO));
		}
	}

	// @Deprecated
	// private static void calLeaveLength4OnePerson1(
	// String pk_org,
	// String pk_psndoc,
	// LeaveCommonVO[] vos,
	// Map<String, LeaveTypeCopyVO> timeItemVOMap,
	// Map<String,AggShiftVO> aggShiftMap,
	// Map<String, ShiftVO> shiftMap,
	// TimeRuleVO timeRuleVO,
	// BillMutexRule billMutexRule) throws BusinessException{
	// //初始化计算参数
	// CalParam4OnePerson calParam = initParam(pk_psndoc,
	// timeRuleVO,billMutexRule, aggShiftMap,shiftMap, vos,
	// BillMutexRule.BILL_LEAVE);
	// //按单据循环处理
	// for (LeaveCommonVO vo : vos) {
	// ITimeScopeWithBillType[] filterdArray = null;
	// //将加班单出差单停工单和休假单进行交切处理
	// ITimeScopeWithBillType[] crossArray = BillProcessHelper.crossAllBills(new
	// LeaveCommonVO[]{vo}, calParam.overtimeBills, calParam.awayBills,
	// calParam.shutdownBills);
	// //通过冲突规则来过滤单据
	// List<ITimeScopeWithBillType> filteredList =
	// BillProcessHelper.filterBills(crossArray, BillMutexRule.BILL_LEAVE,
	// billMutexRule);
	// //如果通过冲突规则过滤出的单据为空，则休假的时长肯定为0,不用往下处理，continue即可
	// if (filteredList == null || filteredList.size() == 0){
	// vo.setLeavehour(new UFDouble(0));
	// continue;
	// }
	// filterdArray = filteredList.toArray(new ITimeScopeWithBillType[0]);
	//
	// //否则就要处理
	// double result = calLeaveLength(pk_org, pk_psndoc,
	// timeItemVOMap.get(vo.getPk_leavetype()), calParam, filterdArray);
	// vo.setLeavehour(CommonMethods.processByDecimalDigitsAndRoundMode(result,
	// timeRuleVO));
	// }
	// }

	public static void calLeaveLength4OnePerson(String pk_org, String pk_psndoc, LeaveCommonVO[] vos,
			LeaveTypeCopyVO leaveTypeCopyVO, CalParam4OnePerson calParam) throws BusinessException {
		// 按单据循环处理
		for (LeaveCommonVO vo : vos) {
			ITimeScopeWithBillType[] filterdArray = null;
			// 将加班单出差单停工单和休假单进行交切处理
			ITimeScopeWithBillType[] crossArray = BillProcessHelper.crossAllBills(new LeaveCommonVO[] { vo },
					calParam.overtimeBills, calParam.awayBills, calParam.shutdownBills);
			// 通过冲突规则来过滤单据
			List<ITimeScopeWithBillType> filteredList = BillProcessHelper.filterBills(crossArray,
					BillMutexRule.BILL_LEAVE, calParam.mutexRule);
			// 如果通过冲突规则过滤出的单据为空，则休假的时长肯定为0,不用往下处理，continue即可
			if (filteredList == null || filteredList.size() == 0) {
				vo.setLeavehour(new UFDouble(0));
				continue;
			}
			filterdArray = filteredList.toArray(new ITimeScopeWithBillType[0]);
			// 否则就要处理
			double result = calLeaveLength(pk_org, pk_psndoc, leaveTypeCopyVO, calParam, filterdArray);
			vo.setLeavehour(CommonMethods.processByDecimalDigitsAndRoundMode(result, calParam.timeruleVO));
		}
	}

	private static double calAwayLength(String pk_org, String pk_psndoc, AwayTypeCopyVO timeitemVO,
			CalParam4OnePerson calParam, ITimeScopeWithBillType[] bills) throws BusinessException {
		return calculateAwayLeaveShutdownLength(pk_org, pk_psndoc, timeitemVO, calParam, bills, BillMutexRule.BILL_AWAY);
	}

	/**
	 * 计算休假时长 bills是已经考虑了单据冲突，但还没跟工作时间段交过的单据
	 * 
	 * @param pkCorp
	 * @param pk_timeitem
	 * @param pk_psndoc
	 * @param bills
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 * @throws BusinessException
	 */
	private static double calLeaveLength(String pk_org, String pk_psndoc, LeaveTypeCopyVO timeitemVO,
			CalParam4OnePerson calParam, ITimeScopeWithBillType[] bills) throws BusinessException {
		return calculateAwayLeaveShutdownLength(pk_org, pk_psndoc, timeitemVO, calParam, bills,
				BillMutexRule.BILL_LEAVE);
	}

	private static double calculateAwayLeaveShutdownLength(String pk_org, String pk_psndoc, TimeItemCopyVO timeitemVO,
			CalParam4OnePerson calParam, ITimeScopeWithBillType[] bills, int billType) throws BusinessException {
		if (bills == null || bills.length == 0)
			return 0;

		UFDouble leaveHours = UFDouble.ZERO_DBL;

		// MOD (历史休假单据的处理) ssx added for WNC on 2018-06-01
		// 导入的历史休假单据，由于没有历史排班，导致假期计算的时候无法正确取得休假时长
		// 处理办法：增加系统参数，标识导入单据的时间点，在此时间点之前的单据，不检查排班，直接返回单据上记录的时长
		List<ITimeScopeWithBillType> newBillList = new Vector(5);
		if (BillMutexRule.BILL_LEAVE == billType) {
			String initdate = SysInitQuery.getParaString(pk_org, "TBMEXPDATE");
			if (!StringUtils.isEmpty(initdate)) {
				UFLiteralDate expDate = new UFLiteralDate(initdate);
				for (ITimeScopeWithBillType vo : bills) {
					if (vo instanceof LeaveRegVO) {
						LeaveRegVO lrVO = (LeaveRegVO) vo;
						if (lrVO.getLeavebegindate().isSameDate(expDate) || lrVO.getLeavebegindate().before(expDate)) {
							leaveHours = leaveHours.add(lrVO.getLeavehour());
						} else {
							newBillList.add(lrVO);
						}
					}
				}
			}

			if (newBillList.size() > 0) {
				// 只校验日期点之后有排班的单据
				bills = newBillList.toArray(new ITimeScopeWithBillType[0]);
			}
		}

		if (newBillList.size() == 0) {
			return leaveHours.doubleValue();
		} else {
			leaveHours = UFDouble.ZERO_DBL;
		}
		// end

		// 找出休假单的开始日和结束日
		UFLiteralDate earliestDate = calParam.earliestDate;
		UFLiteralDate latestDate = calParam.latestDate;
		// 此人员的工作日历map,要求往前往后都推2天，也就是说，一般情况下，map里面的天数会比休假天数多4天
		int dayCount = latestDate.getDaysAfter(earliestDate) + 5;// 加5的原因是要往前后都推两天

		// MOD(历史休假单据的处理) ssx modified for WNC on 2018-06-01
		// 此处原为 result = 0，改为leaveHours的原因是因为上面已经计算了参数时间点之前的单据时长
		// 后面计算时间点以后的单据
		double result = leaveHours.doubleValue();
		// end

		// 从前两天开始处理
		for (int i = 0; i < dayCount; i++) {
			UFLiteralDate curDate = earliestDate.getDateAfter(i - 2);
			UFLiteralDate preDate = curDate.getDateBefore(1);
			UFLiteralDate nextDate = curDate.getDateAfter(1);
			AggPsnCalendar calendarVO = calParam.calendarMap.get(curDate);
			AggPsnCalendar preCalendarVO = calParam.calendarMap.get(preDate);
			AggPsnCalendar nextCalendarVO = calParam.calendarMap.get(nextDate);

			ShiftVO preShift = preCalendarVO == null ? null : preCalendarVO.getPsnCalendarVO() == null ? null
					: ShiftServiceFacade.getShiftVOFromMap(calParam.shiftMap, preCalendarVO.getPsnCalendarVO()
							.getPk_shift());
			ShiftVO curShift = calendarVO == null ? null : calendarVO.getPsnCalendarVO() == null ? null
					: ShiftServiceFacade.getShiftVOFromMap(calParam.shiftMap, calendarVO.getPsnCalendarVO()
							.getPk_shift());
			ShiftVO nextShift = nextCalendarVO == null ? null : nextCalendarVO.getPsnCalendarVO() == null ? null
					: ShiftServiceFacade.getShiftVOFromMap(calParam.shiftMap, nextCalendarVO.getPsnCalendarVO()
							.getPk_shift());
			TimeZone curTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(
					calParam.dateOrgMap.get(curDate)));
			TimeZone preTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(
					calParam.dateOrgMap.get(preDate)));
			TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(
					calParam.dateOrgMap.get(nextDate)));
			// 如果是弹性班，则需要先固化，然后才能计算时长
			if (calendarVO != null && calendarVO.getPsnCalendarVO().isFlexibleFinal()) {
				// 考勤时间段
				ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift, nextShift, curDate.toString(), curTimeZone,
						preTimeZone, nextTimeZone);
				// 用来固化的参数
				SolidifyPara solidifyPara = calParam.toSolidifyPara(curDate, kqScope);
				// 进行固化
				calendarVO.setPsnWorkTimeVO(SolidifyUtils.solidify(solidifyPara));
			}
			switch (billType) {
			case BillMutexRule.BILL_AWAY:
				result += BillProcessHelper.calAwayLength(timeitemVO, bills, curDate.toString(), calendarVO, preShift,
						curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone, calParam.timeruleVO).processedLength;
				continue;
			case BillMutexRule.BILL_LEAVE:
				result += BillProcessHelper.calLeaveLength(timeitemVO, bills, curDate.toString(), calendarVO, preShift,
						curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone, calParam.timeruleVO).processedLength;
				continue;
			case BillMutexRule.BILL_SHUTDOWN:
				result += BillProcessHelper.calShutdownLength(timeitemVO, bills, calendarVO, calParam.timeruleVO).processedLength;
				continue;
			}

		}
		return result;
	}

	public static void calShutdownLength(String pk_org, ShutdownRegVO[] regVOs) throws BusinessException {
		calculateLengths(pk_org, BillMutexRule.BILL_SHUTDOWN, regVOs);
	}

	/**
	 * 求一个停工待料单的时长
	 * 
	 * @param pkCorp
	 * @param pk_timeitem
	 * @param vo
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 * @throws BusinessException
	 */
	public static void calShutdownLength(ShutdownRegVO regVO) throws BusinessException {
		calculateLength(BillMutexRule.BILL_SHUTDOWN, regVO);
	}

	/**
	 * 计算某个人的一批停工单的时长
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param vos
	 * @throws BusinessException
	 * @throws SQLException
	 * @throws NamingException
	 */
	private static void calShutdownLength4OnePerson(String pk_org, String pk_psndoc, ShutdownRegVO[] vos,
			Map<String, ShutDownTypeCopyVO> timeItemVOMap, TimeRuleVO timeRuleVO, BillMutexRule billMutexRule,
			CalParam4OnePerson calParam) throws BusinessException {
		// 按单据循环处理
		for (ShutdownRegVO vo : vos) {
			ITimeScopeWithBillType[] filterdArray = null;
			// 将加班单出差单休假单和停工单进行交切处理
			ITimeScopeWithBillType[] crossArray = BillProcessHelper.crossAllBills(new ShutdownRegVO[] { vo },
					calParam.overtimeBills, calParam.awayBills, calParam.leaveBills);
			// 通过冲突规则来过滤单据
			List<ITimeScopeWithBillType> filteredList = BillProcessHelper.filterBills(crossArray,
					BillMutexRule.BILL_SHUTDOWN, billMutexRule);
			// 如果通过冲突规则过滤出的单据为空，则休假的时长肯定为0,不用往下处理，continue即可
			if (filteredList == null || filteredList.size() == 0) {
				vo.setShutdownhour(new UFDouble(0));
				continue;
			}
			filterdArray = filteredList.toArray(new ITimeScopeWithBillType[0]);

			// 否则就要处理
			double result = calShutdownLength(pk_org, pk_psndoc, timeItemVOMap.get(vo.getPk_shutdowntype()), calParam,
					filterdArray);
			vo.setShutdownhour(CommonMethods.processByDecimalDigitsAndRoundMode(result, timeRuleVO));
		}
	}

	private static double calShutdownLength(String pk_org, String pk_psndoc, TimeItemCopyVO timeitemVO,
			CalParam4OnePerson calParam, ITimeScopeWithBillType[] bills) throws BusinessException {
		return calculateAwayLeaveShutdownLength(pk_org, pk_psndoc, timeitemVO, calParam, bills,
				BillMutexRule.BILL_SHUTDOWN);
	}

	public static void calOvertimeLength(AggOvertimeVO aggVO) throws NegativeArraySizeException, BusinessException {
		calculateAggLength(BillMutexRule.BILL_OVERTIME, aggVO);
	}

	public static void calOvertimeLength(OvertimeCommonVO regVO) throws BusinessException {
		calculateLength(BillMutexRule.BILL_OVERTIME, regVO);
	}

	public static void calOvertimeLength(String pk_org, OvertimeCommonVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		calculateLengths(pk_org, BillMutexRule.BILL_OVERTIME, vos);
	}

	private static void calOvertimeLength4OnePerson(String pk_org, String pk_psndoc, OvertimeCommonVO[] vos,
			Map<String, OverTimeTypeCopyVO> timeItemVOMap, LeaveTypeCopyVO[] leaveItemVOs, TimeRuleVO timeRuleVO,
			BillMutexRule billMutexRule, CalParam4OnePerson calParam, ITimeScope[] holidayScope)
			throws BusinessException {
		int daysForward = 2;
		int daysBackward = 2;
		UFLiteralDate[] dates = CommonUtils.createDateArray(calParam.earliestDate, calParam.latestDate, daysForward,
				daysBackward);
		// 首先要将弹性班固化才能继续下面的计算
		if (MapUtils.isNotEmpty(calParam.calendarMap))
			for (int i = 0; i < dates.length; i++) {
				UFLiteralDate date = dates[i];
				AggPsnCalendar calendar = calParam.calendarMap.get(date);
				// 如果没排班，或者是固定班，则不用处理
				if (calendar == null || !calendar.getPsnCalendarVO().isFlexibleFinal())
					continue;
				// 否则要固化
				UFLiteralDate preDate = i == 0 ? null : dates[i - 1];
				UFLiteralDate nextDate = i == (dates.length - 1) ? null : dates[i + 1];
				AggPsnCalendar calendarVO = calParam.calendarMap.get(date);
				AggPsnCalendar preCalendarVO = calParam.calendarMap.get(preDate);
				AggPsnCalendar nextCalendarVO = calParam.calendarMap.get(nextDate);
				ShiftVO preShift = preCalendarVO == null ? null : preCalendarVO.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calParam.shiftMap, preCalendarVO.getPsnCalendarVO()
								.getPk_shift());
				ShiftVO curShift = calendarVO == null ? null : calendarVO.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calParam.shiftMap, calendarVO.getPsnCalendarVO()
								.getPk_shift());
				ShiftVO nextShift = nextCalendarVO == null ? null : nextCalendarVO.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calParam.shiftMap, nextCalendarVO.getPsnCalendarVO()
								.getPk_shift());
				TimeZone curTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(
						calParam.dateOrgMap.get(date)));
				TimeZone preTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(
						calParam.dateOrgMap.get(preDate)));
				TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(
						calParam.dateOrgMap.get(nextDate)));
				// 考勤时间段
				ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift, nextShift, date.toString(), curTimeZone,
						preTimeZone, nextTimeZone);
				// 固化弹性班
				calendar.setPsnWorkTimeVO(SolidifyUtils.solidify(calParam.toSolidifyPara(date, kqScope)));
			}
		Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(calParam.dateOrgMap,
				calParam.timeruleVO.getTimeZoneMap());
		// 按单据循环处理
		for (OvertimeCommonVO vo : vos) {
			// vo的开始时间和结束时间相同时可能导致时间段切割出现错误,从而导致交切结果异常，因此先判断一下
			if (!vo.getScope_end_datetime().after(vo.getScope_start_datetime())) {
				vo.setLength(new UFDouble(0));
				continue;
			}
			// 将加班单出差单停工单和休假单进行交切处理
			LeaveCommonVO[] leaveBills = calParam.leaveBills;

			// 哺乳假期间应该可以加班的，因此从bills中除掉哺乳假单据
			if (!ArrayUtils.isEmpty(leaveBills)) {
				for (LeaveCommonVO bill : leaveBills) {
					if (TimeItemCopyVO.LEAVETYPE_LACTATION.equalsIgnoreCase(bill.getPk_leavetype())) {
						leaveBills = (LeaveCommonVO[]) ArrayUtils.removeElement(leaveBills, bill);
					}
				}
			}

			ITimeScopeWithBillType[] crossArray = BillProcessHelper.crossAllBills(new OvertimeCommonVO[] { vo },
					leaveBills, calParam.awayBills, calParam.shutdownBills);
			// 通过冲突规则来过滤单据
			List<ITimeScopeWithBillType> filteredList = BillProcessHelper.filterOvertimeBills(crossArray, leaveItemVOs,
					calParam.calendarMap, calParam.aggShiftMap, dateTimeZoneMap, dates, daysForward, dates.length - 1
							- daysBackward, billMutexRule, holidayScope);
			// 如果通过冲突规则过滤出的单据为空，则加班的时长肯定为0,不用往下处理，continue即可
			if (filteredList == null || filteredList.size() == 0) {
				vo.setLength(new UFDouble(0));
				continue;
			}
			// 否则就要处理
			double result = BillProcessHelper.calOvertimeLength(timeItemVOMap.get(vo.getPk_overtimetype()),
					vo.getDeduct() == null ? 0 : vo.getDeduct(), filteredList, timeRuleVO).processedLength;
			vo.setLength(CommonMethods.processByDecimalDigitsAndRoundMode(result, timeRuleVO));
		}
	}

	// public static CalParam4OnePerson initParam(String pk_psndoc,TimeRuleVO
	// timeruleVO,BillMutexRule mutexRule,
	// Map<String,AggShiftVO> aggShiftMap,ITimeScopeWithBillType[] bills,int
	// billType) throws BusinessException{
	// Map<String, ShiftVO> shiftMap
	// =CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap);
	// return initParam(pk_psndoc, timeruleVO, mutexRule, aggShiftMap,
	// shiftMap,bills, billType, 1, 1);
	// }
	// public static CalParam4OnePerson initParam(String pk_psndoc,TimeRuleVO
	// timeruleVO,BillMutexRule mutexRule,
	// Map<String,AggShiftVO> aggShiftMap,Map<String, ShiftVO>
	// shiftMap,ITimeScopeWithBillType[] bills,
	// int billType) throws BusinessException{
	// return initParam(pk_psndoc, timeruleVO, mutexRule, aggShiftMap, shiftMap,
	// bills, billType, 1, 1);
	// }

	public static CalParam4OnePerson initParam(String pk_psndoc, TimeRuleVO timeruleVO, BillMutexRule mutexRule,
			Map<String, AggShiftVO> aggShiftMap, Map<String, ShiftVO> shiftMap, ITimeScopeWithBillType[] bills,
			int billType, int forWardDates, int backWardDates) throws BusinessException {
		CalParam4OnePerson calParam = new CalParam4OnePerson();
		calParam.timeruleVO = timeruleVO;
		calParam.mutexRule = mutexRule;
		calParam.aggShiftMap = aggShiftMap;
		calParam.shiftMap = shiftMap;
		UFLiteralDate earliestDate = DateTimeUtils.toLiteralDate(TimeScopeUtils.getEarliesStartTime(bills),
				ICalendar.BASE_TIMEZONE).getDateBefore(forWardDates);
		UFLiteralDate latestDate = DateTimeUtils.toLiteralDate(TimeScopeUtils.getLatestEndTime(bills),
				ICalendar.BASE_TIMEZONE).getDateAfter(backWardDates);
		calParam.earliestDate = earliestDate;
		calParam.latestDate = latestDate;
		String pk_org = timeruleVO.getPk_org();
		// 如果不是计算休假，则只查出所有有效的休假单（即登记表中的所有数据）
		if (billType != BillMutexRule.BILL_LEAVE)
			calParam.leaveBills = LeaveServiceFacade.queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc,
					earliestDate, latestDate);
		// 否则是所有有效的休假单+此次要计算的bills
		else
			calParam.leaveBills = CommonUtils.merge2Array(LeaveServiceFacade.queryAllSuperVOIncEffectiveByPsnDate(
					pk_org, pk_psndoc, earliestDate, latestDate), (LeaveCommonVO[]) bills);
		// 如果不是计算加班，则只查出所有有效的出差单（即登记表中的所有数据）
		if (billType != BillMutexRule.BILL_OVERTIME)
			calParam.overtimeBills = OverTimeServiceFacade.queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc,
					earliestDate, latestDate);
		// 否则是所有有效的加班单+此次要计算的bills
		else
			calParam.overtimeBills = CommonUtils.merge2Array(OverTimeServiceFacade
					.queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc, earliestDate, latestDate),
					(OvertimeCommonVO[]) bills);
		// 如果不是计算出差，则只查出所有有效的加班单（即登记表中的所有数据）
		if (billType != BillMutexRule.BILL_AWAY)
			calParam.awayBills = AwayServiceFacade.queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc,
					earliestDate, latestDate);
		// 否则是所有有效的出差单+此次要计算的bills
		else
			calParam.awayBills = CommonUtils
					.merge2Array(AwayServiceFacade.queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc,
							earliestDate, latestDate), (AwayCommonVO[]) bills);
		// 如果不是计算停工，则只查出所有有效的停工单（即登记表中的所有数据）
		if (billType != BillMutexRule.BILL_SHUTDOWN)
			calParam.shutdownBills = ShutdownServiceFacade.queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc,
					earliestDate, latestDate);
		// 否则是所有有效的休假单+此次要计算的bills
		else
			calParam.shutdownBills = CommonUtils.merge2Array(ShutdownServiceFacade
					.queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc, earliestDate, latestDate),
					(ShutdownRegVO[]) bills);
		// 查出所有有效的哺乳假
		calParam.lactationBills = LeaveServiceFacade.queryAllLactationVOIncEffictiveByPsnDate(pk_org, pk_psndoc,
				earliestDate, latestDate);
		// 查出所有有效的刷签卡
		calParam.checkTimes = CheckTimeServiceFacade.queryCheckTimesByPsnAndDateScope(pk_org, pk_psndoc, earliestDate,
				latestDate);
		// 查询所有天的工作日历
		calParam.calendarMap = NCLocator
				.getInstance()
				.lookup(IPsnCalendarQueryService.class)
				.queryCalendarMapByPsnDates(pk_org, pk_psndoc, earliestDate.getDateBefore(2),
						latestDate.getDateAfter(2), timeruleVO.getTimeZoneMap(), aggShiftMap);
		// V6.1新增，查询所有天的任职所属业务单元
		calParam.dateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryDateJobOrgMap(pk_psndoc, earliestDate.getDateBefore(2), latestDate.getDateAfter(2));
		return calParam;
	}

	/**
	 * 参数批量初始化
	 * 
	 * @param timeruleVO
	 * @param mutexRule
	 * @param aggShiftMap
	 * @param shiftMap
	 * @param bills
	 * @param billType
	 * @param forWardDates
	 * @param backWardDates
	 * @return <pk_psndoc, CalParam4OnePerson>
	 * @throws BusinessException
	 */
	public static Map<String, CalParam4OnePerson> initParam(TimeRuleVO timeruleVO, BillMutexRule mutexRule,
			Map<String, AggShiftVO> aggShiftMap, Map<String, ShiftVO> shiftMap, ITimeScopeWithBillType[] bills,
			int billType, int forWardDates, int backWardDates) throws BusinessException {
		if (ArrayUtils.isEmpty(bills))
			return null;
		/** 批量查询所有参数 */
		UFLiteralDate earliestDate = DateTimeUtils.toLiteralDate(TimeScopeUtils.getEarliesStartTime(bills),
				ICalendar.BASE_TIMEZONE).getDateBefore(forWardDates);
		UFLiteralDate latestDate = DateTimeUtils.toLiteralDate(TimeScopeUtils.getLatestEndTime(bills),
				ICalendar.BASE_TIMEZONE).getDateAfter(backWardDates);
		String pk_org = timeruleVO.getPk_org();
		String[] pk_psndocs = StringPiecer.getStrArrayDistinct((SuperVO[]) bills, "pk_psndoc");
		IDateScope[] maxDateScope = new IDateScope[] { new DefaultDateScope(earliestDate, latestDate) };

		Map<String, LeaveRegVO[]> allLactationBills = BillMethods.queryAllSuperVOIncEffectiveByPsnsDateScope(
				LeaveRegVO.class, pk_org, pk_psndocs, maxDateScope, " and islactation = 'Y' ");
		Map<String, AwayRegVO[]> allAwayBills = BillMethods.queryAllSuperVOIncEffectiveByPsnsDateScope(AwayRegVO.class,
				pk_org, pk_psndocs, maxDateScope, null);
		Map<String, ShutdownRegVO[]> allShutdownBills = BillMethods.queryAllSuperVOIncEffectiveByPsnsDateScope(
				ShutdownRegVO.class, pk_org, pk_psndocs, maxDateScope, null);

		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = null;
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = null;
		InSQLCreator isc = new InSQLCreator();

		Map<String, List<SuperVO>> billMap = CommonUtils.group2ListByField("pk_psndoc", (SuperVO[]) bills);

		List<List<String>> psndocGroups = new ArrayList<List<String>>();
		List<String> psndocs = null;
		int i = 0;
		for (String pk_psndoc : pk_psndocs) {
			i++;
			if (i == 1) {
				psndocs = new ArrayList<String>();
			}

			psndocs.add(pk_psndoc);

			if (i == 50) {
				i = 0;
				psndocGroups.add(psndocs);
			}
		}

		if (psndocs.size() > 0) {
			psndocGroups.add(psndocs);
		}

		/** 构造返回值 */
		Map<String, CalParam4OnePerson> resultMap = new HashMap<String, CalParam4OnePerson>();
		Map<String, ICheckTime[]> allCheckBills = null;
		Map<String, LeaveRegVO[]> allLeaveBills = null;
		Map<String, OvertimeRegVO[]> allOvertimeBills = null;
		int psnindex = 1;
		for (List<String> psndocGroup : psndocGroups) {
			try {
				long startTime = System.currentTimeMillis();
				// allLeaveBills =
				// BillMethods.queryAllSuperVOIncEffectiveByPsnsDateScope(LeaveRegVO.class,
				// pk_org,
				// psndocGroup.toArray(new String[0]), maxDateScope, null);
				long endTime = System.currentTimeMillis();
				// Thread.sleep(endTime - startTime);
				// Logger.error("--------LEAVEBALANCE-SLEEP-TIME-[LEAVE]-" +
				// String.valueOf(endTime - startTime)
				// + "-------");

				// startTime = System.currentTimeMillis();
				// allOvertimeBills =
				// BillMethods.queryAllSuperVOIncEffectiveByPsnsDateScope(OvertimeRegVO.class,
				// pk_org,
				// psndocGroup.toArray(new String[0]), maxDateScope, null);
				// endTime = System.currentTimeMillis();
				// Thread.sleep(endTime - startTime);
				// Logger.error("--------LEAVEBALANCE-SLEEP-TIME-[OVERTIME]-" +
				// String.valueOf(endTime - startTime)
				// + "-------");

				// startTime = System.currentTimeMillis();
				// allCheckBills =
				// BillMethods.queryCheckTimeMapByPsndocInSQLAndDateScope(pk_org,
				// psndocGroup.toArray(new String[0]), maxDateScope);
				// endTime = System.currentTimeMillis();
				// Thread.sleep(endTime - startTime);
				// Logger.error("--------LEAVEBALANCE-SLEEP-TIME-[CHECK]-" +
				// String.valueOf(endTime - startTime)
				// + "-------");

				startTime = System.currentTimeMillis();
				String psndocInSQL = isc.getInSQL(psndocGroup.toArray(new String[0]));
				endTime = System.currentTimeMillis();
				Thread.sleep(endTime - startTime);
				Logger.error("--------LEAVEBALANCE-SLEEP-TIME-[PSNINSQL]-" + String.valueOf(endTime - startTime)
						+ "-------");

				startTime = System.currentTimeMillis();
				psnDateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
						.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, maxDateScope);
				endTime = System.currentTimeMillis();
				Thread.sleep(endTime - startTime);
				Logger.error("--------LEAVEBALANCE-SLEEP-TIME-[PSNDATEORG]-" + String.valueOf(endTime - startTime)
						+ "-------");

				// startTime = System.currentTimeMillis();
				// psnCalendarMap =
				// NCLocator.getInstance().lookup(IPsnCalendarQueryService.class)
				// .queryCalendarVOByPsnInSQL(pk_org, maxDateScope,
				// psndocInSQL);
				// endTime = System.currentTimeMillis();
				// Thread.sleep(endTime - startTime);
				// Logger.error("--------LEAVEBALANCE-SLEEP-TIME-[PSNCALENDAR]-"
				// + String.valueOf(endTime - startTime)
				// + "-------");

				startTime = System.currentTimeMillis();
				for (String pk_psndoc : psndocGroup) {
					Logger.error("--------LEAVEBALANCE-LOAD-PSN-[" + pk_psndoc + "]-[" + String.valueOf(psnindex)
							+ "]-");
					long innerstartTime = System.currentTimeMillis();
					allLeaveBills = BillMethods.queryAllSuperVOIncEffectiveByPsnsDateScope(LeaveRegVO.class, pk_org,
							new String[] { pk_psndoc }, maxDateScope, null);
					long innerendTime = System.currentTimeMillis();
					Logger.error("--------[LEAVE]-" + String.valueOf(innerendTime - innerstartTime) + "-------");
					Thread.sleep((innerendTime - innerstartTime) % 5);

					innerstartTime = System.currentTimeMillis();
					allOvertimeBills = BillMethods.queryAllSuperVOIncEffectiveByPsnsDateScope(OvertimeRegVO.class,
							pk_org, new String[] { pk_psndoc }, maxDateScope, null);
					innerendTime = System.currentTimeMillis();
					Logger.error("--------[OVERTIME]-" + String.valueOf(innerendTime - innerstartTime) + "-------");
					Thread.sleep((innerendTime - innerstartTime) % 5);

					innerstartTime = System.currentTimeMillis();
					psnCalendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class)
							.queryCalendarVOByPsnInSQL(pk_org, maxDateScope, "'+pk_psndoc+'");
					innerendTime = System.currentTimeMillis();
					Logger.error("--------[PSNCALENDAR]-" + String.valueOf(innerendTime - innerstartTime) + "-------");
					Thread.sleep((innerendTime - innerstartTime) % 5);

					innerstartTime = System.currentTimeMillis();
					allCheckBills = BillMethods.queryCheckTimeMapByPsndocInSQLAndDateScope(pk_org,
							new String[] { pk_psndoc }, maxDateScope);
					innerendTime = System.currentTimeMillis();
					Logger.error("--------[CHECK]-" + String.valueOf(innerendTime - innerstartTime) + "-------");
					Thread.sleep((innerendTime - innerstartTime) % 5);

					List<SuperVO> psnBills = billMap.get(pk_psndoc);
					CalParam4OnePerson calParam = new CalParam4OnePerson();
					calParam.timeruleVO = timeruleVO;
					calParam.mutexRule = mutexRule;
					calParam.aggShiftMap = aggShiftMap;
					calParam.shiftMap = shiftMap;
					calParam.earliestDate = earliestDate;
					calParam.latestDate = latestDate;
					calParam.calendarMap = psnCalendarMap == null ? null : psnCalendarMap.get(pk_psndoc); // 工作日历
					calParam.dateOrgMap = psnDateOrgMap == null ? null : psnDateOrgMap.get(pk_psndoc); // 所有天任职所属业务单元
					calParam.lactationBills = allLactationBills == null ? null : allLactationBills.get(pk_psndoc); // 哺乳假
					calParam.checkTimes = allCheckBills == null ? null : allCheckBills.get(pk_psndoc); // 刷签卡
					calParam.leaveBills = allLeaveBills == null ? null : allLeaveBills.get(pk_psndoc); // 休假
					calParam.overtimeBills = allOvertimeBills == null ? null : allOvertimeBills.get(pk_psndoc); // 加班
					calParam.awayBills = allAwayBills == null ? null : allAwayBills.get(pk_psndoc); // 出差
					calParam.shutdownBills = allShutdownBills == null ? null : allShutdownBills.get(pk_psndoc); // 停工待料
					// 根据单据类型，将当前单据合并
					switch (billType) {
					case BillMutexRule.BILL_LEAVE:
						calParam.leaveBills = CommonUtils.merge2Array(calParam.leaveBills,
								psnBills.toArray(new LeaveCommonVO[0]));
						break;
					case BillMutexRule.BILL_OVERTIME:
						calParam.overtimeBills = CommonUtils.merge2Array(calParam.overtimeBills,
								psnBills.toArray(new OvertimeCommonVO[0]));
						break;
					case BillMutexRule.BILL_AWAY:
						calParam.awayBills = CommonUtils.merge2Array(calParam.awayBills,
								psnBills.toArray(new AwayCommonVO[0]));
						break;
					case BillMutexRule.BILL_SHUTDOWN:
						calParam.shutdownBills = CommonUtils.merge2Array(calParam.shutdownBills,
								psnBills.toArray(new ShutdownRegVO[0]));
						break;
					default:
						break;
					}
					resultMap.put(pk_psndoc, calParam);
					allLeaveBills = null;
					allOvertimeBills = null;
					psnCalendarMap = null;
					allCheckBills = null;
					psnindex++;
				}
				endTime = System.currentTimeMillis();
				Thread.sleep(endTime - startTime);
				Logger.error("--------LEAVEBALANCE-SLEEP-TIME-[COMBINE]-" + String.valueOf(endTime - startTime)
						+ "-------");
			} catch (BusinessException | InterruptedException ex) {
				Logger.error(ex.getMessage());
			}

			System.gc();
		}
		return resultMap;
	}
}
