package nc.impl.ta.psncalendar;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.calendar.CalendarShiftMutexChecker;
import nc.impl.ta.calendar.TACalendarUtils;
import nc.itf.bd.holiday.HolidayServiceFacade;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.om.IAOSQueryService;
import nc.itf.org.IOrgUnitQryService;
import nc.itf.ta.HRHolidayServiceFacade;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITeamCalendarQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.itf.ta.algorithm.RelativeTimeUtils;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.itf.ta.dailydata.IDailyRecordCreator;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.uapbd.IWorkCalendarPubService;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.shift.WTVO;
import nc.vo.bd.workcalendar.WorkCalendarDateVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.ml.MultiLangUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.ITimeScopeBillBodyVO;
import nc.vo.ta.changeshift.AggChangeShiftVO;
import nc.vo.ta.changeshift.ChangeShiftCommonVO;
import nc.vo.ta.changeshift.ChangeShiftRegVO;
import nc.vo.ta.changeshift.ChangeShiftbVO;
import nc.vo.ta.changeshift.ChangeShifthVO;
import nc.vo.ta.dailydata.DailyDataUtils;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.trade.voutils.VOUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsnCalendarServiceImpl implements IPsnCalendarQueryService, IPsnCalendarManageService {

	@Override
	public AggPsnCalendar queryByPsnDate(String pk_psndoc, UFLiteralDate date) throws BusinessException {
		AggPsnCalendar[] vos = queryCalendarVOsByPsnDates(null, pk_psndoc, date, date);
		if (ArrayUtils.isEmpty(vos))
			return null;
		return vos[0];
	}

	@Override
	public AggPsnCalendar[] queryCalendarVOsByPsnDates(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOsByPsnDates0(pk_hrorg, pk_psndoc, beginDate, endDate);
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray);
		return retArray;
	}

	@Override
	public AggPsnCalendar[] queryCalendarVOsByPsnDates(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate, Map<String, TimeZone> timeZoneMap, Map<String, AggShiftVO> allShiftMap)
			throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOsByPsnDates0(pk_hrorg, pk_psndoc, beginDate, endDate);
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray, timeZoneMap, allShiftMap);
		return retArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOsByPsnsDates(String pk_hrorg,
			ITimeScopeBillBodyVO[] bills, Map<String, TimeZone> timeZoneMap, Map<String, AggShiftVO> allShiftMap)
			throws BusinessException {
		if (ArrayUtils.isEmpty(bills))
			return null;
		// 取最大日期范围
		UFLiteralDate begindate = DateScopeUtils.findEarliestBeginDate(bills).getDateBefore(2);
		UFLiteralDate enddate = DateScopeUtils.findLatestEndDate(bills).getDateAfter(2);
		String[] pk_psndocs = new String[bills.length];
		for (int i = 0; i < bills.length; i++) {
			pk_psndocs[i] = bills[i].getPk_psndoc();
		}

		InSQLCreator isc = new InSQLCreator();
		try {
			String insql = isc.getInSQL(pk_psndocs);
			IMDPersistenceQueryService mdService = MDPersistenceService.lookupPersistenceQueryService();
			boolean hasPkOrg = StringUtils.isNotEmpty(pk_hrorg);
			String cond = PsnCalendarVO.PK_PSNDOC
					+ " in ("
					+ insql
					+ ") and "
					+ PsnCalendarVO.CALENDAR
					+ " between '"
					+ begindate
					+ "' and '"
					+ enddate
					+ "' "
					+ " and exists(select top 1 1 from tbm_psndoc psndoc where psndoc.pk_psndoc=tbm_psncalendar.pk_psndoc ";
			if (hasPkOrg)
				cond += " and psndoc.pk_org='" + pk_hrorg + "' ";// 应对人员工作日历业务单元化的改造
			cond += "and calendar between '" + begindate + "' and '" + enddate + "' )";
			AggPsnCalendar[] retArray = CommonUtils.toArray(AggPsnCalendar.class,
					(Collection<AggPsnCalendar>) mdService.queryBillOfVOByCond(AggPsnCalendar.class, cond, false));
			processOriginalAggPsncalendarVOs(pk_hrorg, retArray, timeZoneMap, allShiftMap);

			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psncalendarMap = new HashMap<String, Map<UFLiteralDate, AggPsnCalendar>>();
			Map<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>> billcalendarMap = new HashMap<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>>();
			if (!ArrayUtils.isEmpty(retArray)) {
				for (AggPsnCalendar aggvo : retArray) {
					PsnCalendarVO psnCalendarVO = aggvo.getPsnCalendarVO();
					String pk_psndoc = psnCalendarVO.getPk_psndoc();
					UFLiteralDate calendar = psnCalendarVO.getCalendar();
					if (psncalendarMap.get(pk_psndoc) == null) {
						Map<UFLiteralDate, AggPsnCalendar> datemap = new HashMap<UFLiteralDate, AggPsnCalendar>();
						datemap.put(calendar, aggvo);
						psncalendarMap.put(pk_psndoc, datemap);
					} else {
						psncalendarMap.get(pk_psndoc).put(calendar, aggvo);
					}
				}
			}
			for (ITimeScopeBillBodyVO bill : bills) {
				billcalendarMap.put(bill, psncalendarMap.get(bill.getPk_psndoc()));
			}
			return billcalendarMap;
		} finally {
			isc.clear();
		}
	}

	/**
	 * 查询人员在日期范围内的工作日历，使用原始的元数据查询，没有做任何处理
	 * 
	 * @param pk_hrorg
	 * @param pk_psndoc
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	protected AggPsnCalendar[] queryCalendarVOsByPsnDates0(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return queryCalendarVOsByPsnDates0(pk_hrorg, pk_psndoc, " between '" + beginDate + "' and '" + endDate + "'");
	}

	protected AggPsnCalendar[] queryCalendarVOsByPsnDates0(String pk_hrorg, String pk_psndoc, UFLiteralDate[] allDates)
			throws BusinessException {
		return queryCalendarVOsByPsnDates0(pk_hrorg, pk_psndoc,
				" in(" + StringPiecer.getDefaultPiecesTogether(allDates) + ")");
	}

	/**
	 * 2011.9.22已处理完工作日历业务单元化
	 * 
	 * @param pk_hrorg
	 * @param pk_psndoc
	 * @param calendarCond
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private AggPsnCalendar[] queryCalendarVOsByPsnDates0(String pk_hrorg, String pk_psndoc, String calendarCond)
			throws BusinessException {
		IMDPersistenceQueryService mdService = MDPersistenceService.lookupPersistenceQueryService();
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_hrorg);
		String cond = PsnCalendarVO.PK_PSNDOC + "='" + pk_psndoc + "' and " + PsnCalendarVO.CALENDAR + calendarCond
				+ " and exists(select top 1 1 from tbm_psndoc psndoc where psndoc.pk_psndoc=tbm_psncalendar.pk_psndoc ";
		if (hasPkOrg)
			cond += " and psndoc.pk_org='" + pk_hrorg + "' ";// 应对人员工作日历业务单元化的改造
		cond += "and calendar between begindate and enddate)";
		AggPsnCalendar[] retArray = CommonUtils.toArray(AggPsnCalendar.class,
				(Collection<AggPsnCalendar>) mdService.queryBillOfVOByCond(AggPsnCalendar.class, cond, false));
		return retArray;
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByCondition(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, FromWhereSQL fromWhereSQL) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOByCondition0(pk_hrorg, beginDate, endDate, fromWhereSQL);
		if (ArrayUtils.isEmpty(retArray))
			return null;
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray);
		return DailyDataUtils.groupByPsnDate(retArray);
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByCondition(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, String psndocInSQL) throws BusinessException {
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_hrorg);
		ITimeRuleQueryService timeRuleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
		Map<String, TimeZone> timeZoneMap = hasPkOrg ? timeRuleService.queryTimeZoneMap(pk_hrorg)
				: new HashMap<String, TimeZone>();
		Map<String, AggShiftVO> allShiftMap = hasPkOrg ? ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_hrorg)
				: new HashMap<String, AggShiftVO>();
		return queryCalendarVOByCondition(pk_hrorg, beginDate, endDate, psndocInSQL, timeZoneMap, allShiftMap);
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByCondition(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, String psndocInSQL, Map<String, TimeZone> timeZoneMap,
			Map<String, AggShiftVO> allShiftMap) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOByPsndocInSQL0(pk_hrorg, beginDate, endDate, psndocInSQL);
		if (ArrayUtils.isEmpty(retArray))
			return null;
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray, false, timeZoneMap, allShiftMap);
		return DailyDataUtils.groupByPsnDate(retArray);
	}

	/**
	 * 根据人员条件、日期范围查询工作日历，是使用元数据的最原始的查询
	 * 
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	protected AggPsnCalendar[] queryCalendarVOByCondition0(String pk_org, UFLiteralDate beginDate,
			UFLiteralDate endDate, FromWhereSQL fromWhereSQL) throws BusinessException {
		String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryLatestPsndocsByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(pk_psndocs))
			return null;
		InSQLCreator isc = new InSQLCreator();
		String inSQL = isc.getInSQL(pk_psndocs);
		try {
			return queryCalendarVOByPsndocInSQL0(pk_org, beginDate, endDate, inSQL);
		} finally {
			isc.clear();
		}
	}

	/**
	 * 2011.9.22 V6.1工作日历业务单元化改造已完成
	 * 
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @param inSQL
	 * @return
	 * @throws MetaDataException
	 */
	@SuppressWarnings("unchecked")
	private AggPsnCalendar[] queryCalendarVOByPsndocInSQL0(String pk_hrorg, UFLiteralDate beginDate,
			UFLiteralDate endDate, String inSQL) throws MetaDataException {
		IMDPersistenceQueryService mdService = MDPersistenceService.lookupPersistenceQueryService();
		String cond = PsnCalendarVO.PK_PSNDOC + " in(" + inSQL + ") and " + PsnCalendarVO.CALENDAR + " between '"
				+ beginDate + "' and '" + endDate + "' " + " and exists(select 1 from "
				+ TBMPsndocVO.getDefaultTableName() + " psndoc where  psndoc.pk_psndoc=tbm_psncalendar.pk_psndoc and ";
		if (StringUtils.isNotEmpty(pk_hrorg))
			cond += " psndoc." + TBMPsndocVO.PK_ORG + "='" + pk_hrorg + "' and ";
		cond += " " + PsnCalendarVO.CALENDAR + " between " + TBMPsndocVO.BEGINDATE + " and " + TBMPsndocVO.ENDDATE
				+ ")";
		AggPsnCalendar[] retArray = CommonUtils.toArray(AggPsnCalendar.class,
				(Collection<AggPsnCalendar>) mdService.queryBillOfVOByCond(AggPsnCalendar.class, cond, false));
		return retArray;
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByPsnInSQLForProcess(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, String[] pk_psndocs) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOByPsndocInSQLForDataProcess(pk_hrorg, beginDate, endDate, pk_psndocs);
		if (ArrayUtils.isEmpty(retArray))
			return null;
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray);
		return DailyDataUtils.groupByPsnDate(retArray);
	}

	@SuppressWarnings("unchecked")
	private AggPsnCalendar[] queryCalendarVOByPsndocInSQLForDataProcess(String pk_hrorg, UFLiteralDate beginDate,
			UFLiteralDate endDate, String[] psndocs) throws BusinessException {
		IMDPersistenceQueryService mdService = MDPersistenceService.lookupPersistenceQueryService();
		AggPsnCalendar[] retArray = null;
		InSQLCreator isc = new InSQLCreator();
		try {
			String inSQL = isc.getInSQL(psndocs);
			String cond = PsnCalendarVO.PK_PSNDOC + " in(" + inSQL + ") and " + PsnCalendarVO.CALENDAR + " between '"
					+ beginDate + "' and '" + endDate + "' " + " and exists(select 1 from "
					+ TBMPsndocVO.getDefaultTableName()
					+ " psndoc where  psndoc.pk_psndoc=tbm_psncalendar.pk_psndoc and ";
			if (StringUtils.isNotEmpty(pk_hrorg))
				cond += " psndoc." + TBMPsndocVO.PK_ORG + "='" + pk_hrorg + "' and ";
			cond += " " + PsnCalendarVO.CALENDAR + " between " + TBMPsndocVO.BEGINDATE + " and " + TBMPsndocVO.ENDDATE
					+ ")";
			retArray = CommonUtils.toArray(AggPsnCalendar.class,
					(Collection<AggPsnCalendar>) mdService.queryBillOfVOByCond(AggPsnCalendar.class, cond, false));
		} finally {
			isc.clear();
		}
		return retArray;
	}

	@Override
	public void arrangeAfterHolidayInsert(String pk_org, String pk_holiday) throws BusinessException {
		// HolidayCopyVO holidayVO =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryByPkHoliday(pk_org,
		// pk_holiday);
		HolidayVO holidayVO = HolidayServiceFacade.queryHolidayByPk(pk_holiday);
		OrgVO orgVO = NCLocator.getInstance().lookup(IOrgUnitQryService.class).getOrg(pk_org);
		arrangeByNewHoliday(orgVO.getPk_group(), pk_org, holidayVO);

	}

	@Override
	public void arrangeAfterHolidayInsert(OrgVO orgVO, HolidayVO newHoliday) throws BusinessException {
		arrangeByNewHoliday(orgVO.getPk_group(), orgVO.getPk_org(), newHoliday);

	}

	/**
	 * 新增假日的处理
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param newHolidayVO
	 * @throws BusinessException
	 */
	protected void arrangeByNewHoliday(String pk_group, String pk_org, HolidayVO newHolidayVO) throws BusinessException {
		if (newHolidayVO == null)
			return;
		// 业务单元所有的班次，如果还没有班次，则不用处理下面的逻辑
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		if (MapUtils.isEmpty(shiftMap))
			return;
		// 享受此假日的人员数组
		// String[] enjoyPk_psndocs =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryEnjoyPsndocs(pk_org,
		// newHolidayCopyVO);
		String[] enjoyPk_psndocs = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, newHolidayVO);
		// String[] enjoyPk_psndocs = new String[]{"0001N61000000000I88E"};
		if (ArrayUtils.isEmpty(enjoyPk_psndocs))
			return;
		// 假日的最大日期范围，包括假日和对调日，第一个元素是最早日，第二个是最晚日.作用是确定查询已有工作日历的日期范围
		// UFLiteralDate[] maxHolidayRange =
		// newHolidayVO.getEarliestLatestDate();
		// 东方本田专项，不能使用最大范围，范围很大，人数太多，日历数据量太大，导致错误，改用使用所有相关日期
		// UFLiteralDate[] allDates =
		// newHolidayVO.getAllSwitchAndHolidayDates();
		// UFLiteralDate[] holidayPerhapsAffectDates =
		// CommonUtils.createDateArray(maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		UFLiteralDate[] holidayPerhapsAffectDates = newHolidayVO.getHolidayPerhapsAffectDates();
		// 业务单元时区
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// 对调日期map
		List<String> switchList = newHolidayVO.getAllSwitch();
		// 查询这批人已有的工作日历，日期范围为假日
		// Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs,
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs, holidayPerhapsAffectDates);
		// 组织内的默认班次
		AggShiftVO defaultAggShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// 此假日的相邻假日
		// HolidayCopyVO[] neighborHolidayVOs =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryNeighborHolidays(pk_org,
		// newHolidayVO);
		// HolidayCopyVO[] allHolidayVOs =
		// (HolidayCopyVO[])org.apache.commons.lang.ArrayUtils.addAll(new
		// HolidayCopyVO[]{newHolidayVO}, neighborHolidayVOs);
		HolidayVO[] neighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(newHolidayVO);
		HolidayVO[] allHolidayVOs = (HolidayVO[]) org.apache.commons.lang.ArrayUtils.addAll(
				new HolidayVO[] { newHolidayVO }, neighborHolidayVOs);
		// 相邻假日的享有情况
		// Map<String, Map<String, Boolean>> psnEnjoyInfo =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryHolidayEnjoyInfo2(pk_org,
		// enjoyPk_psndocs, neighborHolidayVOs);
		Map<String, Map<String, Boolean>> psnEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				enjoyPk_psndocs, neighborHolidayVOs);
		if (psnEnjoyInfo == null)
			psnEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// 需要在数据库中删除的工作日历
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// 需要insert的工作日历
		String startdate = newHolidayVO.getStarttime().substring(0, 10);
		// wangywt 员工工作日历把公休日修改为假日的颜色显示问题 20190516 begin
		// 判断是否进行了台湾本地化
		Boolean twFlag = getOrgTWFlag(pk_org);
		BaseDAO basedao = new BaseDAO();
		// 按人循环处理假日
		for (String pk_psndoc : enjoyPk_psndocs) {
			Map<String, Boolean> enjoyInfo = psnEnjoyInfo.get(pk_psndoc);
			if (enjoyInfo == null) {
				enjoyInfo = new HashMap<String, Boolean>();
				psnEnjoyInfo.put(pk_psndoc, enjoyInfo);
			}
			enjoyInfo.put(newHolidayVO.getPk_holiday(), true);// 此假日肯定是享有的
			Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null : existsPsnCalendarMap
					.get(pk_psndoc);
			if (MapUtils.isEmpty(existsCalendarMap))
				continue;
			arrangeOnePsnByNewHoliday(pk_group, pk_org, pk_psndoc, holidayPerhapsAffectDates, switchList,
					allHolidayVOs, psnEnjoyInfo.get(pk_psndoc), existsCalendarMap, shiftMap, defaultAggShiftVO,
					timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList);
			// 公休日改为假日时，员工工作日历DATE_DAYTYPE字段修改为2，wangywt
			PsnCalendarVO existsPsnCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(startdate
					.toString());
			if (twFlag.booleanValue()
					&& (ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift()) || !existsPsnCalendar.isHolidayCancel())) {
				String sql = "update tbm_psncalendar set DATE_DAYTYPE =" + 2 + " where CALENDAR = '" + startdate
						+ "' and pk_psndoc = '" + pk_psndoc + "'";
				basedao.executeUpdate(sql);
			}
			// wangywt 员工工作日历把公休日修改为假日的颜色显示问题 20190516 end
		}
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();
			if (toDelPsnCalendarPk.size() > 0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));

			// //插入前先删除垃圾数据，否则插入失败
			// String[] dates = new String[holidayPerhapsAffectDates.length];
			// for(int i=0;i<holidayPerhapsAffectDates.length;i++){
			// dates[i] = holidayPerhapsAffectDates[i].toString();
			// }
			// InSQLCreator isc2 = new InSQLCreator();
			// String datesInSql = isc2.getInSQL(dates);
			// String deleteSql = " delete from tbm_psncalendar where " +
			// " not exists(select 1 from tbm_psndoc where tbm_psndoc.pk_psndoc = tbm_psncalendar.pk_psndoc  "
			// +
			// " and tbm_psncalendar.calendar between tbm_psndoc.begindate and tbm_psndoc.enddate and tbm_psndoc.pk_org = tbm_psncalendar.pk_org ) "
			// +
			// " and pk_org = '" + pk_org +
			// "' and tbm_psncalendar.calendar in (" + datesInSql + ") ";
			// new BaseDAO().executeUpdate(deleteSql);
			deletGdata(pk_org, holidayPerhapsAffectDates);

			if (toInsertPsnCalendarVOList.size() > 0)
				dao.insert(toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]));
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * 判断组织是不是台湾本地组织，并且是否进行了本地化
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 * @Since wangywt 20190515
	 */
	private Boolean getOrgTWFlag(String pk_org) throws BusinessException {
		// 是否台湾本地化了
		UFBoolean paraTW = SysInitQuery.getParaBoolean(pk_org, "TWHR01");
		if (!paraTW.booleanValue()) {
			return Boolean.FALSE;
		}
		// 判断组织是不是本地组织
		return (Boolean) new BaseDAO()
				.executeQuery(
						"select a.code from org_orgs a left join bd_countryzone b on a.countryzone = b.PK_COUNTRY where b.code = 'TW' and a.pk_org = '"
								+ pk_org + "'", new ResultSetProcessor() {
							@Override
							public Object handleResultSet(ResultSet rs) throws SQLException {
								while (rs.next()) {
									return Boolean.TRUE;
								}
								return Boolean.FALSE;
							}
						});
	}

	/**
	 * 新增假日的时候，调整某个人员的已有班次。
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param holidayScope
	 * @param existsCalendarMap
	 * @throws BusinessException
	 */
	private void arrangeOnePsnByNewHoliday(String pk_group, String pk_org,
			String pk_psndoc,
			UFLiteralDate[] holidayPerhapsAffectDates,// 假日的时段可能影响的排班的日期时段，例如国庆三天，分别是10.1，10.2，10.3，那么此参数就是9.30，10.1，10.2，10.3，10.4
			List<String> switchList,// 假日的对调情况
			HolidayVO[] allHolidayVOs,// 考虑假日切割的所有假日，包括自身与所有相邻假日
			Map<String, Boolean> psnEnjoyInfo,// 假日的享有情况
			Map<String, PsnCalendarVO> existsCalendarMap,// 人员已经排好的班次
			Map<String, AggShiftVO> shiftMap, AggShiftVO defaultAggShiftVO, TimeZone timeZone,
			List<String> toDelPsnCalendarPk,// 需要删除的工作日历，在此方法里设置
			List<AggPsnCalendar> toInsertPsnCalendarVOList// 需要插入的工作日历，在此方法里设置
	) throws BusinessException {
		Map<String, String> finalCalendarMap = new HashMap<String, String>();// key是date，value是班次主键，存储最终对调以及假日切割的结果
		Map<String, String> calendarB4Switch = new HashMap<String, String>();// key是date，value是班次主键，记录对调前的班次
		Map<String, AggPsnCalendar> cutCalendarMap = new HashMap<String, AggPsnCalendar>();// key是date，如果一个班次与假日产生了切割导致不能工作时间段变化了，则存入此map
		Set<String> processedDateSet = new HashSet<String>();// 已经处理过的日期
		if (!CollectionUtils.isEmpty(switchList)) {// 如果存在对调情况，则处理对调
			for (int i = 0; i < switchList.size() - 1; i += 2) {
				String date = switchList.get(i);
				String switchDate = switchList.get(i + 1);
				processedDateSet.add(date);// 这两天都计为已处理
				processedDateSet.add(switchDate);
				// 取出两个日期的已有班次，只有少数几种情况下要调整，其他情况都不调整
				// 1.两天都排班了，且都是遇假日取消
				// 2.一天排了一天没排，排了的是遇假日取消
				PsnCalendarVO calendarVO1 = existsCalendarMap == null ? null : existsCalendarMap.get(date);
				PsnCalendarVO calendarVO2 = existsCalendarMap == null ? null : existsCalendarMap.get(switchDate);
				if (calendarVO1 == null && calendarVO2 == null)// 如果两天都没排，则不对调
					continue;
				if ((calendarVO1 != null && !calendarVO1.isHolidayCancel())
						|| (calendarVO2 != null && !calendarVO2.isHolidayCancel()))// 只要有一天是照旧，则不对调
					continue;
				// 走到这里，是肯定要对调的
				// 首先看两天都排班了的情况，这种情况下，两天肯定都是遇假日取消
				if (calendarVO1 != null && calendarVO2 != null) {
					// 如果两天的班次都一样，则不处理（按道理不会出现这种情况，但这里做容错处理）
					if (calendarVO1.getPk_shift().equals(calendarVO2.getPk_shift()))
						continue;
				}
				// 若有排班，则是排班的班次主键，否则按周一到五默认班周末公休的规则
				String pk_shift1 = getPkShiftByCalendarVOAndDate(calendarVO1, date, defaultAggShiftVO);
				String pk_shift2 = getPkShiftByCalendarVOAndDate(calendarVO2, date, defaultAggShiftVO);
				// 记录对调前班次
				calendarB4Switch.put(date, pk_shift1);
				calendarB4Switch.put(switchDate, pk_shift2);
				// 将pk_shift1设置到switchDate，将pk_shift2设置到date
				processSwitch(pk_psndoc, pk_shift1, switchDate, finalCalendarMap, cutCalendarMap, shiftMap,
						allHolidayVOs, psnEnjoyInfo, timeZone);
				processSwitch(pk_psndoc, pk_shift2, date, finalCalendarMap, cutCalendarMap, shiftMap, allHolidayVOs,
						psnEnjoyInfo, timeZone);
			}
		}

		// 至此对调全部处理完毕，下面处理可能被新增的假日时段覆盖到的班次
		for (UFLiteralDate date : holidayPerhapsAffectDates) {
			if (processedDateSet.contains(date.toString()))// 有可能上面的对调已经处理过此天了，不用再次处理
				continue;
			PsnCalendarVO existsPsnCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(date.toString());
			AggPsnCalendar cutCalendar = null;
			if (existsPsnCalendar == null || ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift())
					|| !existsPsnCalendar.isHolidayCancel())// 如果当天没排班或者排了公休或者遇假日照旧，则也不用处理
				continue;
			cutCalendar = PsnCalendarUtils.createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, existsPsnCalendar.getPk_shift()),
					date.toString(), timeZone, allHolidayVOs, psnEnjoyInfo);
			if (cutCalendar != null) {// 如非空，表示新增假日后，此天的工作日历被切割了，需要特殊设置
				cutCalendar.getPsnCalendarVO().setPk_psndoc(pk_psndoc);
				finalCalendarMap.put(date.toString(), cutCalendar.getPsnCalendarVO().getPk_shift());
				cutCalendarMap.put(date.toString(), cutCalendar);
			}
			// 如果为空，表示新增假日后，此天的工作日历仍然没有被切割，不用特殊设置
		}
		// 至此，所有的对调、假日切割都已处理完毕，对调、切割产生的需要设置的工作日历存储在finalCalendarMap中，对调前的班次存储在calendarB4Switch中，psncalendarvo需要特殊处理的存储在cutCalendarMap中
		if (finalCalendarMap.isEmpty())
			return;
		check(pk_org, pk_psndoc, finalCalendarMap);// 校验
		createPersistenceRecords(pk_group, pk_org, pk_psndoc, finalCalendarMap, existsCalendarMap, calendarB4Switch,
				cutCalendarMap, toDelPsnCalendarPk, toInsertPsnCalendarVOList, shiftMap);
	}

	private void check(String pk_org, String pk_psndoc, Map<String, String> finalCalendarMap) throws BusinessException {
		Map<String, Map<String, String>> checkMap = new HashMap<String, Map<String, String>>();
		checkMap.put(pk_psndoc, finalCalendarMap);
		new CalendarShiftMutexChecker().checkCalendar(pk_org, checkMap, true, true, false);// 校验
	}

	private void processSwitch(String pk_psndoc, String oriPkShift, String switchToDate,
			Map<String, String> finalCalendarMap, Map<String, AggPsnCalendar> cutCalendarMap,
			Map<String, AggShiftVO> shiftMap, HolidayVO[] allHolidayVOs,// 考虑假日切割的所有假日，包括自身与所有相邻假日
			Map<String, Boolean> psnEnjoyInfo,// 相邻假日的享有情况
			TimeZone timeZone) throws BusinessException {
		if (ShiftVO.PK_GX.equals(oriPkShift)) {
			finalCalendarMap.put(switchToDate, oriPkShift);
		} else {
			AggShiftVO aggShiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, oriPkShift);
			AggPsnCalendar cutCalendar = PsnCalendarUtils.createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
					aggShiftVO, switchToDate, timeZone, allHolidayVOs, psnEnjoyInfo);
			if (cutCalendar == null) {
				finalCalendarMap.put(switchToDate, oriPkShift);
			} else {
				cutCalendar.getPsnCalendarVO().setPk_psndoc(pk_psndoc);
				finalCalendarMap.put(switchToDate, cutCalendar.getPsnCalendarVO().getPk_shift());
				cutCalendarMap.put(switchToDate, cutCalendar);
			}
		}
	}

	/**
	 * 如果calendarvo不空，则返回calendarvo的班次 否则返回date对应的天的默认班次：如果是周一到周五，则是默认班，否则是公休
	 * 
	 * @param calendarVO
	 * @param date
	 * @param defaultAggShiftVO
	 * @return
	 */
	private String getPkShiftByCalendarVOAndDate(PsnCalendarVO calendarVO, String date, AggShiftVO defaultAggShiftVO) {
		if (calendarVO == null) {// 如果没排班，则根据默认规则生成班次主键
			return TACalendarUtils.getPkShiftByDate(date, defaultAggShiftVO);
		}
		return calendarVO.getPk_shift();
	}

	@Override
	public void arrangeAfterHolidayUpdate(String pk_org, HolidayVO oldHolidayVO, String pk_holiday)
			throws BusinessException {
		// 修改后的假日
		HolidayVO holidayVO = HolidayServiceFacade.queryHolidayByPk(pk_holiday);
		arrangeAfterHolidayUpdate((OrgVO) new BaseDAO().retrieveByPK(OrgVO.class, pk_org), oldHolidayVO, holidayVO);
	}

	@Override
	public void arrangeAfterHolidayUpdate(OrgVO orgVO, HolidayVO oldHolidayVO, HolidayVO newHoliday)
			throws BusinessException {

		// 如果修改前后假日时段、对调信息、享有条件都没变，则什么都不处理
		if (oldHolidayVO.isCriticalInfoSame(newHoliday))
			return;
		// 业务单元内所有的班次，如果还没有班次，则不用处理下面的逻辑
		// Map<String, AggBclbDefVO> shiftMap =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
		String pk_org = orgVO.getPk_org();
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		if (MapUtils.isEmpty(shiftMap))
			return;
		// 找出修改前享有的人和修改后享有的人，分成三类：
		// 1.修改前、后都享有，这种人按照先删假日，后新增假日处理（如果假日时段和对调日期都没变，则不处理）
		// 2.修改前享有，修改后不享有，这种人做删除假日处理
		// 3.修改前不享有，修改后享有，这种人做新增假日处理
		// IHolidayQueryService holidayService =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class);
		String[] beforeEnjoyPsns = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, oldHolidayVO);// 修改前享有人员
		Set<String> beforeEnjoySet = ArrayUtils.isEmpty(beforeEnjoyPsns) ? new HashSet<String>() : new HashSet<String>(
				Arrays.asList(beforeEnjoyPsns));
		String[] afterEnjoyPsns = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, newHoliday);// 修改后享有人员
		Set<String> afterEnjoySet = ArrayUtils.isEmpty(afterEnjoyPsns) ? new HashSet<String>() : new HashSet<String>(
				Arrays.asList(afterEnjoyPsns));
		// 修改前后都没人享有，则不处理
		if (beforeEnjoySet.size() == 0 && afterEnjoySet.size() == 0)
			return;
		String pk_group = orgVO.getPk_group();
		// 假日的最大日期范围，包括假日和对调日，第一个元素是最早日，第二个是最晚日.作用是确定查询已有工作日历的日期范围
		// UFLiteralDate[] oldMaxHolidayRange =
		// oldHolidayVO.getEarliestLatestDate();
		// UFLiteralDate[] oldHolidayPerhapsAffectDates =
		// CommonUtils.createDateArray(oldMaxHolidayRange[0].getDateBefore(2),
		// oldMaxHolidayRange[1].getDateAfter(2));
		// UFLiteralDate[] newMaxHolidayRange =
		// newHoliday.getEarliestLatestDate();
		// UFLiteralDate[] newHolidayPerhapsAffectDates =
		// CommonUtils.createDateArray(newMaxHolidayRange[0].getDateBefore(2),
		// newMaxHolidayRange[1].getDateAfter(2));
		// UFLiteralDate[] maxHolidayRange = HolidayVO.getEarliestLatestDate(new
		// HolidayVO[]{oldHolidayVO,newHoliday});
		UFLiteralDate[] oldDates = oldHolidayVO.getHolidayPerhapsAffectDates();
		UFLiteralDate[] newDates = newHoliday.getHolidayPerhapsAffectDates();
		UFLiteralDate[] allDates = (UFLiteralDate[]) ArrayUtils.addAll(oldDates, newDates);
		Set<String> allPersonSet = new HashSet<String>();// 存储修改前享有和修改后享有的所有人的set，总之是此次修改可能涉及到的所有人
		allPersonSet.addAll(beforeEnjoySet);
		allPersonSet.addAll(afterEnjoySet);
		// 查询这批人已有的工作日历，日期范围为假日
		// Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org,
		// allPersonSet.toArray(new String[0]),
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_org, allPersonSet.toArray(new String[0]), allDates);
		// 对调日期的list
		List<String> oldSwitchList = oldHolidayVO.getAllSwitch();
		List<String> newSwitchList = newHoliday.getAllSwitch();
		// 业务单元的时区
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// 业务单元内的默认班次
		AggShiftVO defaultAggShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// 修改前假日的相邻假日
		HolidayVO[] oldNeighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(oldHolidayVO);
		// 修改后假日的相邻假日
		HolidayVO[] newNeighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(newHoliday);
		// 修改前相邻假日的享有情况
		Map<String, Map<String, Boolean>> oldEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org, null,
				oldNeighborHolidayVOs);
		if (oldEnjoyInfo == null)// 为null就new一个，防止后面的空指针
			oldEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		// 修改后相邻假日的享有情况
		Map<String, Map<String, Boolean>> newEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org, null,
				newNeighborHolidayVOs);
		if (newEnjoyInfo == null)// 为null就new一个，防止后面的空指针
			newEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// 需要在数据库中删除的工作日历
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// 需要insert的工作日历
		// 否则找出三类人
		Set<String> YYSet = new HashSet<String>();// 修改前后都享有的人员主键set
		Set<String> YNSet = new HashSet<String>();// 修改前享有，修改后不享有
		Set<String> NYSet = new HashSet<String>();// 修改前不享有，修改后享有
		for (String beforePkPsndoc : beforeEnjoySet) {// 按修改前享有的人员循环
			if (afterEnjoySet.contains(beforePkPsndoc)) {// 如果修改前后都有这个人
				YYSet.add(beforePkPsndoc);
				continue;
			}
			YNSet.add(beforePkPsndoc);
		}

		for (String afterPkPsndoc : afterEnjoySet) {// 按修改后的人员循环
			if (beforeEnjoySet.contains(afterPkPsndoc)) {// 如果修改前后都有这个人，则不处理，因为前面一个循环已经加过了
				continue;
			}
			NYSet.add(afterPkPsndoc);
		}
		// 处理修改前享有修改后不享有的
		if (!YNSet.isEmpty()) {
			for (String pk_psndoc : YNSet) {
				Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null
						: existsPsnCalendarMap.get(pk_psndoc);
				if (MapUtils.isEmpty(existsCalendarMap))
					continue;
				arrangeOnePsnByDeleteHoliday(pk_group, pk_org, pk_psndoc, oldDates, oldHolidayVO, oldSwitchList,
						oldNeighborHolidayVOs, oldEnjoyInfo.get(pk_psndoc), existsCalendarMap, shiftMap,
						defaultAggShiftVO, timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList);
			}
		}
		// 处理修改前不享有修改后享有的
		if (!NYSet.isEmpty()) {
			HolidayVO[] allHolidayVOs = (HolidayVO[]) org.apache.commons.lang.ArrayUtils.addAll(
					new HolidayVO[] { newHoliday }, newNeighborHolidayVOs);
			for (String pk_psndoc : NYSet) {
				Map<String, Boolean> enjoyInfo = newEnjoyInfo.get(pk_psndoc);
				if (enjoyInfo == null) {
					enjoyInfo = new HashMap<String, Boolean>();
					newEnjoyInfo.put(pk_psndoc, enjoyInfo);
				}
				enjoyInfo.put(newHoliday.getPk_holiday(), true);
				Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null
						: existsPsnCalendarMap.get(pk_psndoc);
				if (MapUtils.isEmpty(existsCalendarMap))
					continue;
				arrangeOnePsnByNewHoliday(pk_group, pk_org, pk_psndoc, newDates, newSwitchList, allHolidayVOs,
						enjoyInfo, existsCalendarMap, shiftMap, defaultAggShiftVO, timeZone, toDelPsnCalendarPk,
						toInsertPsnCalendarVOList);
			}
		}
		// 处理要删除的和要insert的
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();
			if (toDelPsnCalendarPk.size() > 0) {
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
				toDelPsnCalendarPk.clear();
			}
			if (toInsertPsnCalendarVOList.size() > 0) {
				deletGdata(pk_org, allDates);
				dao.insert(toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]));
				toInsertPsnCalendarVOList.clear();
			}
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		// 下面处理修改前也享有修改后也享有的人
		// 如果不存在这种人，或者如果假日时段和对调日期都没变，则这些人不用处理
		if (YYSet.isEmpty()
				|| (oldHolidayVO.isSameHolidayScope(newHoliday) && oldHolidayVO.isSameSwithDates(newHoliday)))
			return;
		// 否则按先删除后新增处理,分两个循环，先循环删除，再循环insert以提高效率
		for (String pk_psndoc : YYSet) {// 先处理删除假日的循环
			Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null : existsPsnCalendarMap
					.get(pk_psndoc);
			if (MapUtils.isEmpty(existsCalendarMap))
				continue;
			arrangeOnePsnByDeleteHoliday(pk_group, pk_org, pk_psndoc, oldDates, oldHolidayVO, oldSwitchList,
					oldNeighborHolidayVOs, oldEnjoyInfo.get(pk_psndoc), existsCalendarMap, shiftMap, defaultAggShiftVO,
					timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList, false);
		}

		// 处理要删除的和要insert的
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();
			if (toDelPsnCalendarPk.size() > 0) {
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
				toDelPsnCalendarPk.clear();
			}
			if (toInsertPsnCalendarVOList.size() > 0) {
				deletGdata(pk_org, allDates);
				dao.insert(toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]));
				toInsertPsnCalendarVOList.clear();
			}
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		// 重新查询已有工作日历，因为上面的持久化操作会导致工作日历发生变化
		// existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org,
		// allPersonSet.toArray(new String[0]),
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		existsPsnCalendarMap = new PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org,
				allPersonSet.toArray(new String[0]), allDates);
		HolidayVO[] allHolidayVOs = (HolidayVO[]) org.apache.commons.lang.ArrayUtils.addAll(
				new HolidayVO[] { newHoliday }, newNeighborHolidayVOs);
		for (String pk_psndoc : YYSet) {// 再处理新增假日的循环
			Map<String, Boolean> enjoyInfo = newEnjoyInfo.get(pk_psndoc);
			if (enjoyInfo == null) {
				enjoyInfo = new HashMap<String, Boolean>();
				newEnjoyInfo.put(pk_psndoc, enjoyInfo);
			}
			enjoyInfo.put(newHoliday.getPk_holiday(), true);
			arrangeOnePsnByNewHoliday(pk_group, pk_org, pk_psndoc, newDates, newSwitchList, allHolidayVOs, enjoyInfo,
					existsPsnCalendarMap == null ? null : existsPsnCalendarMap.get(pk_psndoc), shiftMap,
					defaultAggShiftVO, timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList);
		}
		// 处理要删除的和要insert的
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();
			if (toDelPsnCalendarPk.size() > 0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
			if (toInsertPsnCalendarVOList.size() > 0) {
				deletGdata(pk_org, allDates);
				dao.insert(toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]));
			}
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}

	}

	/**
	 * 删除垃圾数据
	 * 
	 * @param pk_org
	 * @param allDates
	 * @throws BusinessException
	 */
	public void deletGdata(String pk_org, UFLiteralDate[] allDates) throws BusinessException {
		// 插入前先删除垃圾数据，否则插入失败
		String[] dates = new String[allDates.length];
		for (int i = 0; i < allDates.length; i++) {
			dates[i] = allDates[i].toString();
		}
		InSQLCreator isc2 = new InSQLCreator();
		String datesInSql = isc2.getInSQL(dates);
		String deleteSql = " delete from tbm_psncalendar where "
				+ " not exists(select 1 from tbm_psndoc where tbm_psndoc.pk_psndoc = tbm_psncalendar.pk_psndoc  "
				+ " and tbm_psncalendar.calendar between tbm_psndoc.begindate and tbm_psndoc.enddate and tbm_psndoc.pk_org = tbm_psncalendar.pk_org ) "
				+ " and pk_org = '" + pk_org + "' and tbm_psncalendar.calendar in (" + datesInSql + ") ";
		new BaseDAO().executeUpdate(deleteSql);
	}

	@Override
	public void arrangeBeforeHolidayDelete(String pk_org, String pk_holiday) throws BusinessException {
		// HolidayCopyVO holidayVO =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryByPkHoliday(pk_org,
		// pk_holiday);
		HolidayVO holidayVO = HolidayServiceFacade.queryHolidayByPk(pk_holiday);
		OrgVO orgVO = NCLocator.getInstance().lookup(IOrgUnitQryService.class).getOrg(pk_org);
		arrangeBeforeHolidayDelete(orgVO, holidayVO);

	}

	@Override
	public void arrangeBeforeHolidayDelete(OrgVO orgVO, HolidayVO deleteHoliday) throws BusinessException {
		arrangeByDeleteHoliday(orgVO.getPk_group(), orgVO.getPk_org(), deleteHoliday);
	}

	/**
	 * 删除假日时的排班
	 * 
	 * @throws BusinessException
	 */
	protected void arrangeByDeleteHoliday(String pk_group, String pk_org, HolidayVO deleteHolidayVO)
			throws BusinessException {
		if (deleteHolidayVO == null)
			return;
		// 业务单元内所有的班次，如果还没有班次，则不用处理下面的逻辑
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		if (MapUtils.isEmpty(shiftMap))
			return;
		// 享受此假日的人员数组
		String[] enjoyPk_psndocs = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, deleteHolidayVO);
		// String[] enjoyPk_psndocs = new String[]{"0001N61000000000I88E"};
		if (ArrayUtils.isEmpty(enjoyPk_psndocs))
			return;
		// 假日的最大日期范围，包括假日和对调日，第一个元素是最早日，第二个是最晚日.作用是确定查询已有工作日历的日期范围
		// UFLiteralDate[] maxHolidayRange =
		// deleteHolidayVO.getEarliestLatestDate();
		// 东方本田专项，不能使用最大范围，范围很大，人数太多，日历数据量太大，导致错误，改用使用所有相关日期
		// UFLiteralDate[] allDates =
		// deleteHolidayVO.getAllSwitchAndHolidayDates();
		// UFLiteralDate[] holidayPerhapsAffectDates =
		// CommonUtils.createDateArray(maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		UFLiteralDate[] holidayPerhapsAffectDates = deleteHolidayVO.getHolidayPerhapsAffectDates();
		// 时区
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// 对调日期map
		List<String> switchList = deleteHolidayVO.getAllSwitch();
		// 查询这批人已有的工作日历，日期范围为假日
		// Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs,
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs, holidayPerhapsAffectDates);
		// 组织内的默认班次
		AggShiftVO defaultAggShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// 此假日的相邻假日
		HolidayVO[] neighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(deleteHolidayVO);
		// 相邻假日的享有情况
		Map<String, Map<String, Boolean>> psnEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				enjoyPk_psndocs, neighborHolidayVOs);
		if (psnEnjoyInfo == null)
			psnEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// 需要在数据库中删除的工作日历
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// 需要insert的工作日历
		// wangywt 员工工作日历把公休日修改为假日的颜色显示问题 20190516 begin
		// 判断是否进行了台湾本地化
		String date = deleteHolidayVO.getStarttime().substring(0, 10);// 当前变更的日期
		// 台湾本地化标识
		Boolean twFlag = getOrgTWFlag(pk_org);
		BaseDAO basedao = new BaseDAO();
		// 按人循环处理假日
		for (String pk_psndoc : enjoyPk_psndocs) {
			Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null : existsPsnCalendarMap
					.get(pk_psndoc);
			if (MapUtils.isEmpty(existsCalendarMap))
				continue;
			arrangeOnePsnByDeleteHoliday(pk_group, pk_org, pk_psndoc, holidayPerhapsAffectDates, deleteHolidayVO,
					switchList, neighborHolidayVOs, psnEnjoyInfo.get(pk_psndoc), existsCalendarMap, shiftMap,
					defaultAggShiftVO, timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList);
			// 把公休日上设置假日删除时，把员工工作日历里的颜色也给改过来了，把DATE_DAYTYPE改成1
			PsnCalendarVO existsPsnCalendar = existsCalendarMap.get(date.toString());
			if ((twFlag.booleanValue())
					&& (ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift())
							&& (StringUtils.isEmpty(existsPsnCalendar.getOriginal_shift_b4cut()) || ShiftVO.PK_GX
									.equals(existsPsnCalendar.getOriginal_shift_b4cut())) || !existsPsnCalendar
								.isHolidayCancel())) {
				String sql = "update tbm_psncalendar set DATE_DAYTYPE =" + 1 + " where CALENDAR = '" + date
						+ "' and pk_psndoc = '" + pk_psndoc + "'";
				basedao.executeUpdate(sql);
				// wangywt 员工工作日历把公休日修改为假日的颜色显示问题 20190516 end
			}
		}
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();
			if (toDelPsnCalendarPk.size() > 0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
			if (toInsertPsnCalendarVOList.size() > 0) {
				deletGdata(pk_org, holidayPerhapsAffectDates);
				dao.insert(toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]));
			}
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	private void arrangeOnePsnByDeleteHoliday(String pk_group, String pk_org,
			String pk_psndoc,
			UFLiteralDate[] holidayPerhapsAffectDates,// 假日的时段可能影响的排班的日期时段，例如国庆三天，分别是10.1，10.2，10.3，那么此参数就是9.30，10.1，10.2，10.3，10.4
			HolidayVO deleteHolidayVO,// 被删除的假日情况
			List<String> switchList,// 假日的对调情况
			HolidayVO[] neighborHolidayVOs,// 考虑假日切割的所有相邻假日
			Map<String, Boolean> psnEnjoyInfo,// 相邻假日的享有情况
			Map<String, PsnCalendarVO> existsCalendarMap,// 人员已经排好的班次
			Map<String, AggShiftVO> shiftMap, AggShiftVO defaultAggShiftVO, TimeZone timeZone,
			List<String> toDelPsnCalendarPk,// 需要删除的工作日历，在此方法里设置
			List<AggPsnCalendar> toInsertPsnCalendarVOList// 需要插入的工作日历，在此方法里设置
	) throws BusinessException {
		arrangeOnePsnByDeleteHoliday(pk_group, pk_org, pk_psndoc, holidayPerhapsAffectDates, deleteHolidayVO,
				switchList, neighborHolidayVOs, psnEnjoyInfo, existsCalendarMap, shiftMap, defaultAggShiftVO, timeZone,
				toDelPsnCalendarPk, toInsertPsnCalendarVOList, true);
	}

	/**
	 * 删除假日的时候，调整某个人员的已有班次。
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param holidayScope
	 * @param existsCalendarMap
	 * @throws BusinessException
	 */
	private void arrangeOnePsnByDeleteHoliday(String pk_group, String pk_org,
			String pk_psndoc,
			UFLiteralDate[] holidayPerhapsAffectDates,// 假日的时段可能影响的排班的日期时段，例如国庆三天，分别是10.1，10.2，10.3，那么此参数就是9.30，10.1，10.2，10.3，10.4
			HolidayVO deleteHolidayVO,// 被删除的假日情况
			List<String> switchList,// 假日的对调情况
			HolidayVO[] neighborHolidayVOs,// 考虑假日切割的所有相邻假日
			Map<String, Boolean> psnEnjoyInfo,// 相邻假日的享有情况
			Map<String, PsnCalendarVO> existsCalendarMap,// 人员已经排好的班次
			Map<String, AggShiftVO> shiftMap, AggShiftVO defaultAggShiftVO, TimeZone timeZone,
			List<String> toDelPsnCalendarPk,// 需要删除的工作日历，在此方法里设置
			List<AggPsnCalendar> toInsertPsnCalendarVOList,// 需要插入的工作日历，在此方法里设置
			boolean needCheck) throws BusinessException {
		Map<String, String> finalCalendarMap = new HashMap<String, String>();// key是date，value是班次主键，存储最终对调以及假日切割的结果
		Map<String, AggPsnCalendar> cutCalendarMap = new HashMap<String, AggPsnCalendar>();// key是date，如果一个班次与假日产生了切割导致不能工作时间段变化了，则存入此map
		Set<String> processedDateSet = new HashSet<String>();// 已经处理过的日期
		if (!CollectionUtils.isEmpty(switchList)) {// 如果存在对调情况，则将对调前的班次恢复
			for (int i = 0; i < switchList.size() - 1; i += 2) {
				String date = switchList.get(i);
				String switchDate = switchList.get(i + 1);
				// 取出两个日期的已有班次，如果排班了，且是遇假日取消，则恢复到对调前的班次（两天独立处理，处理时不用看另一天的情况，这是与新增休假不同的地方）
				PsnCalendarVO calendarVO1 = existsCalendarMap.get(date);
				PsnCalendarVO calendarVO2 = existsCalendarMap.get(switchDate);
				if (calendarVO1 == null && calendarVO2 == null)// 如果两天都没排，则不处理
					continue;
				if (calendarVO1 != null && calendarVO1.isHolidayCancel()) {
					processedDateSet.add(date);
					processResume(calendarVO1, finalCalendarMap, cutCalendarMap, neighborHolidayVOs, psnEnjoyInfo,
							shiftMap, timeZone);
				}
				if (calendarVO2 != null && calendarVO2.isHolidayCancel()) {
					processedDateSet.add(switchDate);
					processResume(calendarVO2, finalCalendarMap, cutCalendarMap, neighborHolidayVOs, psnEnjoyInfo,
							shiftMap, timeZone);
				}
			}
		}
		// 至此对调全部处理完毕，下面处理可能被删除的假日时段覆盖到的班次
		for (UFLiteralDate date : holidayPerhapsAffectDates) {
			if (processedDateSet.contains(date.toString()))// 有可能上面的对调已经处理过此天了，不用再次处理
				continue;
			PsnCalendarVO existsPsnCalendar = existsCalendarMap.get(date.toString());
			// 如果当天没排班或者排了公休（此公休要求是本来就是公休，而不是被假日切割成的公休。被假日切割成的公休依然要处理），则也不用处理
			// 原来的班次是遇假日照旧，也不用处理
			if (existsPsnCalendar == null
					|| (ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift()) && (StringUtils.isEmpty(existsPsnCalendar
							.getOriginal_shift_b4cut()) || ShiftVO.PK_GX.equals(existsPsnCalendar
							.getOriginal_shift_b4cut()))) || !existsPsnCalendar.isHolidayCancel())
				continue;
			// 走到这里，肯定要重新计算切割情况：如果切割前班次字段有值，那么此天肯定是被假日切割成了公休，所以要用切割前的班次与相邻假日再计算一次；如果没值，则用所排的班次直接重新计算即可
			String pk_shift = StringUtils.isNotBlank(existsPsnCalendar.getOriginal_shift_b4cut()) ? existsPsnCalendar
					.getOriginal_shift_b4cut() : existsPsnCalendar.getPk_shift();
			AggPsnCalendar cutCalendar = PsnCalendarUtils.createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, pk_shift), date.toString(), timeZone,
					neighborHolidayVOs, psnEnjoyInfo);
			if (cutCalendar != null) {// 返回值不为null，表示假日删除后，此天的排班依然被其他假日所影响
				cutCalendar.getPsnCalendarVO().setPk_psndoc(pk_psndoc);
				finalCalendarMap.put(date.toString(), cutCalendar.getPsnCalendarVO().getPk_shift());
				cutCalendarMap.put(date.toString(), cutCalendar);
			} else
				// 返回值为null，表示假日删除后，此天的排班不会被其他假日所影响
				finalCalendarMap.put(date.toString(), pk_shift);
		}
		// 至此，所有的对调、假日切割都已处理完毕，对调、切割产生的需要设置的工作日历存储在finalCalendarMap中，对调前的班次存储在calendarB4Switch中，psncalendarvo需要特殊处理的存储在cutCalendarMap中
		if (finalCalendarMap.isEmpty())
			return;
		if (needCheck)
			check(pk_org, pk_psndoc, finalCalendarMap);// 校验
		createPersistenceRecords(pk_group, pk_org, pk_psndoc, finalCalendarMap, existsCalendarMap, cutCalendarMap,
				toDelPsnCalendarPk, toInsertPsnCalendarVOList, shiftMap);
	}

	private void createPersistenceRecords(String pk_group, String pk_org, String pk_psndoc,
			Map<String, String> finalCalendarMap, Map<String, PsnCalendarVO> existsCalendarMap,
			Map<String, AggPsnCalendar> cutCalendarMap, List<String> toDelPsnCalendarPk,// 需要删除的工作日历，在此方法里设置
			List<AggPsnCalendar> toInsertPsnCalendarVOList,// 需要插入的工作日历，在此方法里设置
			Map<String, AggShiftVO> shiftMap) throws BusinessException {
		createPersistenceRecords(pk_group, pk_org, pk_psndoc, finalCalendarMap, existsCalendarMap, null,
				cutCalendarMap, toDelPsnCalendarPk, toInsertPsnCalendarVOList, shiftMap);
	}

	/**
	 * 创建要持久化的数据，要删除的pk_psncalendar放入toDelPsnCalendarPk，
	 * 要insert的放入toInsertPsnCalendarVOList
	 * 
	 * @throws BusinessException
	 */
	private void createPersistenceRecords(String pk_group, String pk_org, String pk_psndoc,
			Map<String, String> finalCalendarMap, Map<String, PsnCalendarVO> existsCalendarMap,
			Map<String, String> calendarB4Switch, Map<String, AggPsnCalendar> cutCalendarMap,
			List<String> toDelPsnCalendarPk,// 需要删除的工作日历，在此方法里设置
			List<AggPsnCalendar> toInsertPsnCalendarVOList,// 需要插入的工作日历，在此方法里设置
			Map<String, AggShiftVO> shiftMap) throws BusinessException {
		// 校验通过，将已有的班次删掉，然后重新insert
		// 循环处理每一天
		String[] dates = finalCalendarMap.keySet().toArray(new String[0]);
		for (String date : dates) {
			if (existsCalendarMap != null && existsCalendarMap.get(date) != null) {// 如果需要设置的天已经排了班，则这一天的班次需要删掉
				PsnCalendarVO existsCalendar = existsCalendarMap.get(date);
				toDelPsnCalendarPk.add(existsCalendar.getPk_psncalendar());
			}
			// 下面构建插入数据库的vo
			AggPsnCalendar aggVO = cutCalendarMap == null ? null : cutCalendarMap.get(date);
			if (aggVO != null) {
				PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
				toInsertPsnCalendarVOList.add(aggVO);
				continue;
			}
			// 代码走到这里，工作日历的两个子表肯定是不需要有数据的，构建主表数据即可
			aggVO = new AggPsnCalendar();
			PsnCalendarVO calendarVO = new PsnCalendarVO();
			aggVO.setParentVO(calendarVO);
			calendarVO.setPk_psndoc(pk_psndoc);
			calendarVO.setCalendar(UFLiteralDate.getDate(date));
			calendarVO.setPk_shift(finalCalendarMap.get(date));
			if (calendarB4Switch != null)
				calendarVO.setOriginal_shift_b4exg(calendarB4Switch.get(date));
			if (ShiftVO.PK_GX.equals(calendarVO.getPk_shift())) {
				PsnCalendarUtils.setGX(calendarVO);
			} else {
				PsnCalendarUtils.setNonGX(calendarVO,
						ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, calendarVO.getPk_shift()).getShiftVO());
			}
			PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
			toInsertPsnCalendarVOList.add(aggVO);
		}
	}

	/**
	 * 处理删除假日时的恢复对调班次
	 * 
	 * @throws BusinessException
	 */
	private void processResume(PsnCalendarVO calendarVO, Map<String, String> finalCalendarMap,
			Map<String, AggPsnCalendar> cutCalendarMap, HolidayVO[] neighborHolidayVOs,// 考虑假日切割的所有相邻假日
			Map<String, Boolean> psnEnjoyInfo,// 相邻假日的享有情况
			Map<String, AggShiftVO> shiftMap, TimeZone timeZone) throws BusinessException {
		// 满足下面任意一个条件都不恢复：班次为空，班次遇假日照旧，或者对调前班次为空
		if (calendarVO == null || !calendarVO.isHolidayCancel()
				|| StringUtils.isEmpty(calendarVO.getOriginal_shift_b4exg()))
			return;
		String date = calendarVO.getCalendar().toString();// 日期
		String original_shift_b4exg = calendarVO.getOriginal_shift_b4exg();// 对调前班次
		if (ShiftVO.PK_GX.equals(original_shift_b4exg)) {// 如果对调前的班次为公休
			finalCalendarMap.put(date, ShiftVO.PK_GX);
			return;
		}
		// 如果对调前的班次为非公休，则要处理与假日的交集
		AggPsnCalendar cutCalendar = PsnCalendarUtils.createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
				ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, original_shift_b4exg), date, timeZone,
				neighborHolidayVOs, psnEnjoyInfo);
		if (cutCalendar == null) {// 如果和假日没交集
			finalCalendarMap.put(date, original_shift_b4exg);
			return;
		}
		cutCalendar.getPsnCalendarVO().setPk_psndoc(calendarVO.getPk_psndoc());
		finalCalendarMap.put(date, cutCalendar.getPsnCalendarVO().getPk_shift());
		cutCalendarMap.put(date, cutCalendar);
	}

	/*
	 * 此方法用于后台自动排班 (non-Javadoc)
	 * 
	 * @see nc.itf.ta.IPsnCalendarManageService#autoArrange(java.lang.String)
	 */
	@Override
	public String autoArrange_RequiresNew(String pk_hrorg) throws BusinessException {
		// HROrgVO hrorgvo=((HROrgVO)
		// NCLocator.getInstance().lookup(IOrgInfoQueryService.class).queryByPk(pk_hrorg).getParentVO());
		// String orgname=MultiLangUtil.getSuperVONameOfCurrentLang(hrorgvo,
		// "name", null);
		OrgVO orgvo = NCLocator.getInstance().lookup(IOrgUnitQryService.class).getOrg(pk_hrorg);
		String orgname = MultiLangUtil.getSuperVONameOfCurrentLang(orgvo, "name", null);
		TimeRuleVO timeRuleVO = null;
		StringBuffer sb = new StringBuffer();
		// 考勤规则和排班月数在调用此方法前已经校验过了此处不再需要校验
		try {// 没有制定考勤规则会抛异常，因此要catch
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_hrorg);
		} catch (Exception e) {
			// sb.append(orgname+"没有指定考勤规则");
			return sb.toString();
		}
		Integer autoArrangeMonth = timeRuleVO == null ? null : timeRuleVO.getAutoarrangemonth();
		if (autoArrangeMonth == null || autoArrangeMonth <= 0) {// 考勤规则自动排班月数为0，则不执行自动排班
			// sb.append(orgname+"考勤规则自动排班月数为0，不执行排班");
			return sb.toString();
		}
		IAOSQueryService aosService = NCLocator.getInstance().lookup(IAOSQueryService.class);
		// HR组织下的业务单元
		// OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false,
		// false);
		OrgVO[] orgVOs = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		UFLiteralDate now = new UFDate().toUFLiteralDate(TimeZone.getDefault());
		UFLiteralDate endDate = now.getDateAfter(autoArrangeMonth * 30);
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		PsnJobVO[] psnjobvos = maintainImpl.queryPsnJobVOsByConditionAndOverrideOrg(pk_hrorg, null, now, endDate,
				false, true);
		InSQLCreator inSQLCreator = new InSQLCreator();
		String strCondition = " pk_psnjob in (" + inSQLCreator.getInSQL(psnjobvos, PsnJobVO.PK_PSNJOB) + ") ";
		psnjobvos = (PsnJobVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, PsnJobVO.class, strCondition);

		if (ArrayUtils.isEmpty(psnjobvos)) {
			// sb.append(orgname+ResHelper.getString("6017basedoc","06017basedoc1849")/*@res"没有需要排班的人员或者人员已经有排好的班了!"*/+"<br>");
			sb.append(orgname + ResHelper.getString("6017basedoc", "06017basedoc1849")/*
																					 * @
																					 * res
																					 * "没有需要排班的人员或者人员已经有排好的班了!"
																					 */);
			return sb.toString();
		}
		Map<String, PsnJobVO[]> jobMap = CommonUtils.group2ArrayByField(PsnJobVO.PK_ORG, psnjobvos);
		// Set<String> pkpsndocSet = new HashSet<String>();
		// for(PsnJobVO jobVO:psnjobvos){
		// pkpsndocSet.add(jobVO.getPk_psndoc());

		// }
		for (OrgVO orgVO : orgVOs) {
			String pk_org = orgVO.getPk_org();
			PsnJobVO[] psnJobVOs2 = jobMap.get(pk_org);
			if (ArrayUtils.isEmpty(psnJobVOs2))
				continue;
			AggShiftVO defaultShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
			if (defaultShiftVO == null)
				continue;
			// PsnJobVO[] psnjobvos =
			// maintainImpl.queryPsnJobVOsByConditionAndOverrideOrg(pk_hrorg,
			// null, now, endDate, false,true);
			// if(ArrayUtils.isEmpty(psnjobvos))
			// return;
			// Set<String> pkpsndocSet = new HashSet<String>();
			// for(PsnJobVO jobVO:psnjobvos){
			// pkpsndocSet.add(jobVO.getPk_psndoc());
			// }
			// maintainImpl.useDefault(pk_hrorg, pkpsndocSet.toArray(new
			// String[0]), now, endDate, false,defaultShiftVO);
			maintainImpl.useDefault(orgVO.getPk_group(), pk_hrorg, pk_org,
					StringPiecer.getStrArray(psnJobVOs2, PsnJobVO.PK_PSNDOC), now, endDate, false, defaultShiftVO,
					false);
		}
		return sb.length() > 0 ? sb.toString() : "";
	}

	/*
	 * 此方法用于新进考勤档案人员时的自动排班 排班逻辑为，将人员最早的考勤档案日期到自动排班月数的日期内的班次都填满（不覆盖已有班次）
	 * (non-Javadoc)
	 * 
	 * @see nc.itf.ta.IPsnCalendarManageService#autoArrange(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void autoArrange(String pk_hrorg, String pk_psndoc) throws BusinessException {
		autoArrange(pk_hrorg, new String[] { pk_psndoc });
	}

	public void autoArrange(String pk_hrorg, String pk_psndoc, TBMPsndocVO[] psndocVOs) throws BusinessException {

		TimeRuleVO timeRuleVO = null;
		try {
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_hrorg);
		} catch (Exception e) {
			return;
		}
		Integer autoArrangeMonth = timeRuleVO == null ? null : timeRuleVO.getAutoarrangemonth();
		if (autoArrangeMonth == null || autoArrangeMonth <= 0)// 考勤规则自动排班月数为0，则不执行自动排班
			return;
		// IAOSQueryService aosService =
		// NCLocator.getInstance().lookup(IAOSQueryService.class);
		// HR组织下的业务单元
		// OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false,
		// false);
		OrgVO[] orgVOs = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		// 从今天开始往后数自动排班月数*30天
		UFLiteralDate endDate = UFLiteralDate.getDate(new UFDate().toLocalString().substring(0, 10)).getDateAfter(
				autoArrangeMonth * 30);
		PeriodVO endPeriod = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(pk_hrorg, endDate);
		if (endPeriod == null)
			throw new BusinessException(ResHelper.getString("6017hrta", "06017hrta0065")
			/* @res "排班的结束日期没有所属的考勤期间!" */);
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		ITeamCalendarQueryService teamQueryService = NCLocator.getInstance().lookup(ITeamCalendarQueryService.class);
		// TBMPsndocVO[] psndocVOs =
		// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocVOsByPsndocs(pk_hrorg,
		// pk_psndocs,true);
		if (ArrayUtils.isEmpty(psndocVOs))
			return;
		// 将这些考勤档案用任职组织分组
		Map<String, TBMPsndocVO[]> orgGroupVOs = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_JOBORG, psndocVOs);
		Map<String, AggShiftVO> orgDefal = ShiftServiceFacade.queryDefaultShiftAggVOByHROrg(pk_hrorg);
		for (OrgVO orgVO : orgVOs) {
			String pk_org = orgVO.getPk_org();
			TBMPsndocVO[] orgPsndocVOs = orgGroupVOs.get(pk_org);
			if (ArrayUtils.isEmpty(orgPsndocVOs))
				continue;
			AggShiftVO defaultShiftVO = orgDefal.get(pk_org);
			if (defaultShiftVO == null) {
				defaultShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
			}
			if (defaultShiftVO == null)
				continue;
			List<PsnJobCalendarVO> resultList = new ArrayList<PsnJobCalendarVO>();
			// List<String> teamPsnList = new ArrayList<String>();//班组人员
			// List<String> psnList = new ArrayList<String>();//非班组排班人员
			// UFLiteralDate earliestDate =
			// orgPsndocVOs[0].getBegindate();//需要自动排班的最早日期,默认考勤档案的开始时间
			// V65调整，已经结束的考勤档案不需要重新排班，因为截断的原考勤档案的开始时间很早可能日期已经封存，导致班组人员无法调整
			UFLiteralDate earliestDate = orgPsndocVOs[orgPsndocVOs.length - 1].getBegindate();
			String tempPsndoc = null;
			VOUtil.ascSort(orgPsndocVOs, new String[] { TBMPsndocVO.PK_PSNDOC });// 排序

			UFLiteralDate allEarliestDate = DateScopeUtils.findEarliestBeginDate(orgPsndocVOs);
			TeamInfoCalendarVO[] allTeamCalendars = null;
			String[] pk_teams = StringPiecer.getStrArray(orgPsndocVOs, TBMPsndocVO.PK_TEAM);
			if (ArrayUtils.isEmpty(pk_teams)) {
				allTeamCalendars = teamQueryService.queryCalendarVOByPKTeams(pk_hrorg, pk_teams, allEarliestDate,
						endDate);
			}
			Map<String, TeamInfoCalendarVO> teamMap = CommonUtils.toMap(TeamInfoCalendarVO.CTEAMID, allTeamCalendars);
			PsnJobCalendarVO[] allPpsnCalendars = maintainImpl.queryCalendarVOByPsndocs(pk_hrorg,
					StringPiecer.getStrArray(orgPsndocVOs, TBMPsndocVO.PK_PSNDOC), allEarliestDate, endDate);
			Map<String, PsnJobCalendarVO[]> psnCaMap = CommonUtils.group2ArrayByField(PsnJobCalendarVO.PK_PSNDOC,
					allPpsnCalendars);
			// 处理班组工作日历
			for (TBMPsndocVO tbmpsndocVO : orgPsndocVOs) {
				if (StringUtils.isEmpty(tbmpsndocVO.getPk_team())) {
					continue;// 如果所属班组为空，则表示是将考勤档案按班组开始日期拆分后的前一段，不参与班组的排班
								// 2015-08-14
				}

				if (earliestDate == null || tbmpsndocVO.getBegindate().before(earliestDate))
					earliestDate = tbmpsndocVO.getBegindate();
				// 按班组默认排班处理 2011-11-28
				String pk_team = tbmpsndocVO.getPk_team();
				if (StringUtils.isEmpty(pk_team)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// 如果是同一个人，并且排班的最早时间要晚于上一次的排班就没必要再排一次班了 - modify by -- zhouyuh
				// 2012-06-26
				if (earliestDate != null && earliestDate.before(tbmpsndocVO.getBegindate())
						&& tbmpsndocVO.getPk_psndoc().equals(tempPsndoc)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// 查询当前人员排班信息和班组排班信息,批量查询放到循环外面
				// TeamInfoCalendarVO[] teamCalendars =
				// teamQueryService.queryCalendarVOByPKTeams(pk_hrorg, new
				// String[]{pk_team}, tbmpsndocVO.getBegindate(), endDate);
				// PsnJobCalendarVO[] psnCalendars =
				// maintainImpl.queryCalendarVOByPsndocs(pk_hrorg, new
				// String[]{tbmpsndocVO.getPk_psndoc()},
				// tbmpsndocVO.getBegindate(), endDate);
				PsnJobCalendarVO[] psnCalendars = psnCaMap == null ? null : psnCaMap.get(tbmpsndocVO.getPk_psndoc());
				// if(ArrayUtils.isEmpty(teamCalendars))
				// continue;
				TeamInfoCalendarVO teamCalendar = teamMap == null ? null : teamMap.get(pk_team);
				if (teamCalendar == null) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				tempPsndoc = tbmpsndocVO.getPk_psndoc();// 判断循环体中的这个人是否跟上一次的psndoc是否是同一个人
				// 如果当天已排班则不变
				// 如果当天未排班且所在班组当天已排班，用班组班次覆盖
				// 如果当天未排班且班组也未排班，则使用默认排班方式排班
				// teamCalendars[0].getCalendarMap().putAll(psnCalendars[0].getCalendarMap());
				// psnCalendars[0].getModifiedCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// 默认排班使用班组班次，如果班组未设置则用人员班次，如果都没有，则用默认班次 *2012-07-23修改*
				// psnCalendars[0].getCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// 2013-08-05添加
				// 若所属班组没有工作日历，则先把员工日历清空，后再默认班次排班，解决业务单元更改后班次还是原来业务单元的班次的问题
				UFLiteralDate[] dateScope = CommonUtils.createDateArray(earliestDate, endDate);
				Map<String, String> teamCalendarMap = teamCalendar.getCalendarMap();
				if (teamCalendarMap == null) {
					teamCalendarMap = new HashMap<String, String>();
				}
				for (UFLiteralDate date : dateScope) {
					if (!teamCalendarMap.keySet().contains(date.toString())) {
						teamCalendarMap.put(date.toString(), null);
					}
				}
				psnCalendars[0].getModifiedCalendarMap().putAll(teamCalendarMap);
				resultList.add(psnCalendars[0]);
				// teamPsnList.add(tbmpsndocVO.getPk_psndoc());
			}
			if (endDate.before(earliestDate))
				return;
			// //与班组同步的人员排班
			// if(CollectionUtils.isNotEmpty(resultList)){
			// maintainImpl.save(pk_hrorg, resultList.toArray(new
			// PsnJobCalendarVO[0]));
			// maintainImpl.useDefault(orgVO.getPk_org(),
			// teamPsnList.toArray(new String[0]), earliestDate, endDate,
			// false,defaultShiftVO);
			// }
			// //其他人员排班
			// if(CollectionUtils.isNotEmpty(psnList)){
			// String[] psndocs = psnList.toArray(new String[0]);
			// //业务单元发生了变化则必须覆盖排班了，否则班次还是远业务单元的,此处判断有误在源头直接清空原有日历吧
			// InSQLCreator isc = new InSQLCreator();
			// //查询一下原班次的业务单元
			// String whereSql =
			// " pk_shift in (select pk_shift from tbm_psncalendar " +
			// " where pk_psndoc in( " + isc.getInSQL(psndocs) +" ) and " +
			// " calendar between '" + earliestDate + "' and '" + endDate +
			// "') " +
			// " and pk_shift <> '0001Z7000000000000GX' and pk_org <> '" +
			// orgVO.getPk_org() + "' ";
			// int count =
			// NCLocator.getInstance().lookup(IPersistenceRetrieve.class).getCountByCondition(ShiftVO.getDefaultTableName(),whereSql);
			// if(count >0){//说明业务单元发生了改变，必须重新排班了
			// maintainImpl.useDefault(orgVO.getPk_org(), psndocs, earliestDate,
			// endDate, true,defaultShiftVO);
			// continue;
			// }
			// maintainImpl.useDefault(orgVO.getPk_org(), pk_psndocs,
			// earliestDate, endDate, false,defaultShiftVO);
			// }
			maintainImpl.save(pk_hrorg, resultList.toArray(new PsnJobCalendarVO[0]));
			maintainImpl.useDefault(orgVO.getPk_group(), pk_hrorg, orgVO.getPk_org(),
					StringPiecer.getStrArrayDistinct(orgPsndocVOs, TBMPsndocVO.PK_PSNDOC), earliestDate, endDate,
					false, defaultShiftVO, false);
		}

	}

	/**
	 * 持久化到数据库之前,先要存入某天的日历天类型
	 * 
	 * @param vos
	 * @return
	 */
	private AggPsnCalendar[] dealDateDayType4Insert(AggPsnCalendar[] vos, String pk_Hrorg, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {

		if (null == vos || vos.length <= 0) {
			return vos;
		}
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// 工作日类型map，key是人员主键，value的key是日期，value是工作日类型，见holidayvo。
		// <pk_psndoc,<date,工作日类型>>
		Map<String, Map<String, Integer>> holidayInfo = holidayService.queryPsnWorkDayTypeInfo4PsnCalenderInsert(
				pk_Hrorg, pk_psndocs, beginDate, endDate, false);
		for (AggPsnCalendar aggPsnCalendar : vos) {
			Map<String, Integer> psnDateTypeMap = holidayInfo.get(aggPsnCalendar.getPk_psndoc());
			if (null != psnDateTypeMap) {
				Integer dayType = psnDateTypeMap.get(aggPsnCalendar.getDate().toString());
				if (null != dayType) {
					aggPsnCalendar.getPsnCalendarVO().setDate_daytype(dayType);
				}
			}

		}
		return vos;
	}

	/*
	 * 此方法用于批量新进考勤档案人员时的自动排班 排班逻辑为，将人员最早的考勤档案日期到自动排班月数的日期内的班次都填满（不覆盖已有班次）
	 * (non-Javadoc)
	 * 
	 * @see nc.itf.ta.IPsnCalendarManageService#autoArrange(java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public void autoArrange(String pk_hrorg, String[] pk_psndocs) throws BusinessException {
		TimeRuleVO timeRuleVO = null;
		try {
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_hrorg);
		} catch (Exception e) {
			return;
		}
		Integer autoArrangeMonth = timeRuleVO == null ? null : timeRuleVO.getAutoarrangemonth();
		if (autoArrangeMonth == null || autoArrangeMonth <= 0)// 考勤规则自动排班月数为0，则不执行自动排班
			return;
		// IAOSQueryService aosService =
		// NCLocator.getInstance().lookup(IAOSQueryService.class);
		// HR组织下的业务单元
		// OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false,
		// false);
		OrgVO[] orgVOs = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		// 从今天开始往后数自动排班月数*30天
		UFLiteralDate endDate = UFLiteralDate.getDate(new UFDate().toLocalString().substring(0, 10)).getDateAfter(
				autoArrangeMonth * 30);
		PeriodVO endPeriod = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(pk_hrorg, endDate);
		if (endPeriod == null)
			throw new BusinessException(ResHelper.getString("6017hrta", "06017hrta0065")
			/* @res "排班的结束日期没有所属的考勤期间!" */);
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		ITeamCalendarQueryService teamQueryService = NCLocator.getInstance().lookup(ITeamCalendarQueryService.class);
		TBMPsndocVO[] psndocVOs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocVOsByPsndocs(pk_hrorg, pk_psndocs, true);
		if (ArrayUtils.isEmpty(psndocVOs))
			return;
		// 将这些考勤档案用任职组织分组
		Map<String, TBMPsndocVO[]> orgGroupVOs = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_JOBORG, psndocVOs);
		Map<String, AggShiftVO> orgDefal = ShiftServiceFacade.queryDefaultShiftAggVOByHROrg(pk_hrorg);
		for (OrgVO orgVO : orgVOs) {
			String pk_org = orgVO.getPk_org();
			TBMPsndocVO[] orgPsndocVOs = orgGroupVOs.get(pk_org);
			if (ArrayUtils.isEmpty(orgPsndocVOs))
				continue;
			AggShiftVO defaultShiftVO = orgDefal.get(pk_org);
			if (defaultShiftVO == null) {
				defaultShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
			}
			if (defaultShiftVO == null)
				continue;
			List<PsnJobCalendarVO> resultList = new ArrayList<PsnJobCalendarVO>();
			// List<String> teamPsnList = new ArrayList<String>();//班组人员
			// List<String> psnList = new ArrayList<String>();//非班组排班人员
			// UFLiteralDate earliestDate =
			// orgPsndocVOs[0].getBegindate();//需要自动排班的最早日期,默认考勤档案的开始时间
			// V65调整，已经结束的考勤档案不需要重新排班，因为截断的原考勤档案的开始时间很早可能日期已经封存，导致班组人员无法调整
			UFLiteralDate earliestDate = orgPsndocVOs[orgPsndocVOs.length - 1].getBegindate();
			String tempPsndoc = null;
			VOUtil.ascSort(orgPsndocVOs, new String[] { TBMPsndocVO.PK_PSNDOC });// 排序

			UFLiteralDate allEarliestDate = DateScopeUtils.findEarliestBeginDate(orgPsndocVOs);
			TeamInfoCalendarVO[] allTeamCalendars = null;
			String[] pk_teams = StringPiecer.getStrArray(orgPsndocVOs, TBMPsndocVO.PK_TEAM);
			if (ArrayUtils.isEmpty(pk_teams)) {
				allTeamCalendars = teamQueryService.queryCalendarVOByPKTeams(pk_hrorg, pk_teams, allEarliestDate,
						endDate);
			}
			Map<String, TeamInfoCalendarVO> teamMap = CommonUtils.toMap(TeamInfoCalendarVO.CTEAMID, allTeamCalendars);
			PsnJobCalendarVO[] allPpsnCalendars = maintainImpl.queryCalendarVOByPsndocs(pk_hrorg,
					StringPiecer.getStrArray(orgPsndocVOs, TBMPsndocVO.PK_PSNDOC), allEarliestDate, endDate);
			Map<String, PsnJobCalendarVO[]> psnCaMap = CommonUtils.group2ArrayByField(PsnJobCalendarVO.PK_PSNDOC,
					allPpsnCalendars);
			// 处理班组工作日历
			for (TBMPsndocVO tbmpsndocVO : orgPsndocVOs) {
				if (StringUtils.isEmpty(tbmpsndocVO.getPk_team())) {
					continue;// 如果所属班组为空，则表示是将考勤档案按班组开始日期拆分后的前一段，不参与班组的排班
								// 2015-08-14
				}

				if (earliestDate == null || tbmpsndocVO.getBegindate().before(earliestDate))
					earliestDate = tbmpsndocVO.getBegindate();
				// 按班组默认排班处理 2011-11-28
				String pk_team = tbmpsndocVO.getPk_team();
				if (StringUtils.isEmpty(pk_team)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// 如果是同一个人，并且排班的最早时间要晚于上一次的排班就没必要再排一次班了 - modify by -- zhouyuh
				// 2012-06-26
				if (earliestDate != null && earliestDate.before(tbmpsndocVO.getBegindate())
						&& tbmpsndocVO.getPk_psndoc().equals(tempPsndoc)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// 查询当前人员排班信息和班组排班信息,批量查询放到循环外面
				// TeamInfoCalendarVO[] teamCalendars =
				// teamQueryService.queryCalendarVOByPKTeams(pk_hrorg, new
				// String[]{pk_team}, tbmpsndocVO.getBegindate(), endDate);
				// PsnJobCalendarVO[] psnCalendars =
				// maintainImpl.queryCalendarVOByPsndocs(pk_hrorg, new
				// String[]{tbmpsndocVO.getPk_psndoc()},
				// tbmpsndocVO.getBegindate(), endDate);
				PsnJobCalendarVO[] psnCalendars = psnCaMap == null ? null : psnCaMap.get(tbmpsndocVO.getPk_psndoc());
				// if(ArrayUtils.isEmpty(teamCalendars))
				// continue;
				TeamInfoCalendarVO teamCalendar = teamMap == null ? null : teamMap.get(pk_team);
				if (teamCalendar == null) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				tempPsndoc = tbmpsndocVO.getPk_psndoc();// 判断循环体中的这个人是否跟上一次的psndoc是否是同一个人
				// 如果当天已排班则不变
				// 如果当天未排班且所在班组当天已排班，用班组班次覆盖
				// 如果当天未排班且班组也未排班，则使用默认排班方式排班
				// teamCalendars[0].getCalendarMap().putAll(psnCalendars[0].getCalendarMap());
				// psnCalendars[0].getModifiedCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// 默认排班使用班组班次，如果班组未设置则用人员班次，如果都没有，则用默认班次 *2012-07-23修改*
				// psnCalendars[0].getCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// 2013-08-05添加
				// 若所属班组没有工作日历，则先把员工日历清空，后再默认班次排班，解决业务单元更改后班次还是原来业务单元的班次的问题
				UFLiteralDate[] dateScope = CommonUtils.createDateArray(earliestDate, endDate);
				Map<String, String> teamCalendarMap = teamCalendar.getCalendarMap();
				if (teamCalendarMap == null) {
					teamCalendarMap = new HashMap<String, String>();
				}
				for (UFLiteralDate date : dateScope) {
					if (!teamCalendarMap.keySet().contains(date.toString())) {
						teamCalendarMap.put(date.toString(), null);
					}
				}
				psnCalendars[0].getModifiedCalendarMap().putAll(teamCalendarMap);
				resultList.add(psnCalendars[0]);
				// teamPsnList.add(tbmpsndocVO.getPk_psndoc());
			}
			if (endDate.before(earliestDate))
				return;
			// //与班组同步的人员排班
			// if(CollectionUtils.isNotEmpty(resultList)){
			// maintainImpl.save(pk_hrorg, resultList.toArray(new
			// PsnJobCalendarVO[0]));
			// maintainImpl.useDefault(orgVO.getPk_org(),
			// teamPsnList.toArray(new String[0]), earliestDate, endDate,
			// false,defaultShiftVO);
			// }
			// //其他人员排班
			// if(CollectionUtils.isNotEmpty(psnList)){
			// String[] psndocs = psnList.toArray(new String[0]);
			// //业务单元发生了变化则必须覆盖排班了，否则班次还是远业务单元的,此处判断有误在源头直接清空原有日历吧
			// InSQLCreator isc = new InSQLCreator();
			// //查询一下原班次的业务单元
			// String whereSql =
			// " pk_shift in (select pk_shift from tbm_psncalendar " +
			// " where pk_psndoc in( " + isc.getInSQL(psndocs) +" ) and " +
			// " calendar between '" + earliestDate + "' and '" + endDate +
			// "') " +
			// " and pk_shift <> '0001Z7000000000000GX' and pk_org <> '" +
			// orgVO.getPk_org() + "' ";
			// int count =
			// NCLocator.getInstance().lookup(IPersistenceRetrieve.class).getCountByCondition(ShiftVO.getDefaultTableName(),whereSql);
			// if(count >0){//说明业务单元发生了改变，必须重新排班了
			// maintainImpl.useDefault(orgVO.getPk_org(), psndocs, earliestDate,
			// endDate, true,defaultShiftVO);
			// continue;
			// }
			// maintainImpl.useDefault(orgVO.getPk_org(), pk_psndocs,
			// earliestDate, endDate, false,defaultShiftVO);
			// }
			maintainImpl.save(pk_hrorg, resultList.toArray(new PsnJobCalendarVO[0]));
			maintainImpl.useDefault(orgVO.getPk_group(), pk_hrorg, orgVO.getPk_org(),
					StringPiecer.getStrArrayDistinct(orgPsndocVOs, TBMPsndocVO.PK_PSNDOC), earliestDate, endDate,
					false, defaultShiftVO, false);
		}
	}

	@Override
	public void arrangeAfterApprove(String pk_hrorg, AggregatedValueObject[] aggBills) throws BusinessException {
		if (ArrayUtils.isEmpty(aggBills))
			return;
		if (!(aggBills instanceof AggChangeShiftVO[]))
			throw new IllegalArgumentException("aggBills must be instance of AggChangeClassVO[]!");
		AggChangeShiftVO[] aggVOs = (AggChangeShiftVO[]) aggBills;
		List<ChangeShiftCommonVO> saveList = new ArrayList<ChangeShiftCommonVO>();
		for (AggChangeShiftVO aggVO : aggVOs) {
			String pk_psndoc = aggVO.getChangeShifthVO().getPk_psndoc();
			String pk_psnjob = aggVO.getChangeShifthVO().getPk_psnjob();
			ChangeShiftbVO[] bvos = aggVO.getChangeShiftbVOs();
			for (ChangeShiftbVO bvo : bvos) {
				bvo.setPk_psndoc(pk_psndoc);
				bvo.setPk_psnjob(pk_psnjob);
				saveList.add(bvo);
			}
		}
		arrangeAfterApproveOrRegister(pk_hrorg, saveList.toArray(new ChangeShiftCommonVO[0]));
	}

	/**
	 * 调班审批或者登记执行调班的公用方法
	 * 
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	protected void arrangeAfterApproveOrRegister(String pk_hrorg, ChangeShiftCommonVO[] vos) throws BusinessException {
		Map<String, PsnJobCalendarVO> psnChangeShiftMap = new HashMap<String, PsnJobCalendarVO>();
		// 查出这些人的任职组织<pk_psnjob,pk_org>
		Map<String, String> jobOrgMap = new HashMap<String, String>();
		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(vos, ChangeShiftCommonVO.PK_PSNJOB);
			PsnJobVO[] psnjobvos = (PsnJobVO[]) CommonUtils.toArray(PsnJobVO.class,
					new BaseDAO().retrieveByClause(PsnJobVO.class, PsnJobVO.PK_PSNJOB + " in(" + inSQL + ")"));
			for (PsnJobVO psnjobvo : psnjobvos) {
				jobOrgMap.put(psnjobvo.getPk_psnjob(), psnjobvo.getPk_org());
			}
		} finally {
			if (isc != null)
				isc.clear();
		}
		for (ChangeShiftCommonVO vo : vos) {
			String pk_psndoc = vo.getPk_psndoc();
			PsnJobCalendarVO calendarVO = psnChangeShiftMap.get(pk_psndoc);
			if (calendarVO == null) {
				calendarVO = new PsnJobCalendarVO();
				calendarVO.setPk_psndoc(pk_psndoc);
				psnChangeShiftMap.put(pk_psndoc, calendarVO);
			}
			UFLiteralDate beginDate = vo.getChangeshiftbegindate();
			UFLiteralDate endDate = vo.getChangeshiftenddate();
			UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
			String pk_joborg = jobOrgMap.get(vo.getPk_psnjob());
			for (UFLiteralDate date : allDates) {
				calendarVO.getModifiedCalendarMap().put(date.toString(), vo.getNewshiftpk());
				calendarVO.getOrgMap().put(date.toString(), pk_joborg);
			}
		}
		new PsnCalendarMaintainImpl().save(pk_hrorg, psnChangeShiftMap.values().toArray(new PsnJobCalendarVO[0]),
				false, false);
	}

	// @Override
	// public String queryExistsCalendarName(String pk_hrorg, String pk_psndoc,
	// UFLiteralDate beginDate, UFLiteralDate endDate)
	// throws BusinessException {
	// return queryExistsCalendarName(pk_hrorg, new String[]{pk_psndoc},
	// beginDate, endDate)[0];
	// }
	//
	// @Override
	// public String[] queryExistsCalendarName(String pk_hrorg, String[]
	// pk_psndocs,
	// UFLiteralDate beginDate, UFLiteralDate endDate)
	// throws BusinessException {
	// if(ArrayUtils.isEmpty(pk_psndocs))
	// return null;
	// //查询这段日期的班次
	// Map<String, Map<String, String>> psnCalendarMap = new
	// PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_hrorg, pk_psndocs,
	// beginDate, endDate);
	// String[] retArray = new String[pk_psndocs.length];
	// //组织内所有班次
	// Map<String, AggShiftVO> shiftMap =
	// ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_hrorg);
	// UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate,
	// endDate);
	// for(int i=0;i<pk_psndocs.length;i++){
	// String pk_psndoc = pk_psndocs[i];
	// if(psnCalendarMap==null||!psnCalendarMap.containsKey(pk_psndoc))
	// continue;
	// Map<String, String> calendarMap = psnCalendarMap.get(pk_psndoc);
	// StringBuilder sb = new StringBuilder();
	// for(int j=0;j<allDates.length;j++){
	// String date = allDates[j].toString();
	// String pk_shift = calendarMap.get(date);
	// if(StringUtils.isEmpty(pk_shift))
	// continue;
	// if(pk_shift.equals(ShiftVO.PK_GX)){
	// sb.append(ResHelper.getString("6017psncalendar","06017psncalendar0093")
	// /*@res "公休,"*/);
	// continue;
	// }
	// ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,
	// pk_shift).getShiftVO();
	// sb.append(shiftVO.getMultiLangName() + ",");
	// }
	// retArray[i]=sb.length()==0?null:sb.substring(0,
	// sb.lastIndexOf(",")).toString();
	// }
	// return retArray;
	// }

	@Override
	public void arrangeAfterRegister(String pk_hrorg, SuperVO[] regVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(regVOs))
			return;
		if (!(regVOs instanceof ChangeShiftRegVO[]))
			throw new IllegalArgumentException("regVOs must be instance of ChangeClassRegVO[]!");
		arrangeAfterApproveOrRegister(pk_hrorg, (ChangeShiftRegVO[]) regVOs);
	}

	@Override
	public void arrangeAfterRegister(String pk_hrorg, SuperVO regVO) throws BusinessException {
		if (regVO == null)
			return;
		if (!(regVO instanceof ChangeShiftRegVO))
			throw new IllegalArgumentException("regVO must be instance of ChangeClassRegVO!");
		arrangeAfterRegister(pk_hrorg, new ChangeShiftRegVO[] { (ChangeShiftRegVO) regVO });
	}

	protected void checkCalendarMutex(String pk_org, Object[] bills) throws BusinessException {
		if (ArrayUtils.isEmpty(bills))
			return;
		if (!(bills instanceof AggChangeShiftVO[]) && !(bills instanceof ChangeShiftRegVO[])) {
			throw new IllegalArgumentException("bills must be instance of AggChangeClassVO[] or ChangeClassRegVO[]!");
		}
		// 用来校验的map，key是人员主键，map的key是日期，value是班次主键
		Map<String, Map<String, String>> psnCheckMap = new HashMap<String, Map<String, String>>();
		if (bills instanceof AggChangeShiftVO[]) {
			AggChangeShiftVO[] aggVOs = (AggChangeShiftVO[]) bills;
			for (AggChangeShiftVO aggVO : aggVOs) {
				ChangeShiftbVO[] bvos = aggVO.getChangeShiftbVOs();
				if (ArrayUtils.isEmpty(bvos))
					continue;
				ChangeShifthVO hvo = aggVO.getChangeShifthVO();
				String pk_psndoc = hvo.getPk_psndoc();
				Map<String, String> checkMap = psnCheckMap.get(pk_psndoc);
				if (checkMap == null) {
					checkMap = new HashMap<String, String>();
					psnCheckMap.put(pk_psndoc, checkMap);
				}
				for (ChangeShiftbVO bvo : bvos) {
					UFLiteralDate beginDate = bvo.getChangeshiftbegindate();
					UFLiteralDate endDate = bvo.getChangeshiftenddate();
					UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
					for (UFLiteralDate date : allDates) {
						checkMap.put(date.toString(), bvo.getNewshiftpk());
					}
				}
			}
			new CalendarShiftMutexChecker().checkCalendar(pk_org, psnCheckMap, true, true, false);
			return;
		}
		ChangeShiftRegVO[] regVOs = (ChangeShiftRegVO[]) bills;
		for (ChangeShiftRegVO regVO : regVOs) {
			String pk_psndoc = regVO.getPk_psndoc();
			Map<String, String> checkMap = psnCheckMap.get(pk_psndoc);
			if (checkMap == null) {
				checkMap = new HashMap<String, String>();
				psnCheckMap.put(pk_psndoc, checkMap);
			}
			UFLiteralDate beginDate = regVO.getChangeshiftbegindate();
			UFLiteralDate endDate = regVO.getChangeshiftenddate();
			UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
			for (UFLiteralDate date : allDates) {
				checkMap.put(date.toString(), regVO.getNewshiftpk());
			}
		}
		new CalendarShiftMutexChecker().checkCalendar(pk_org, psnCheckMap, true, true, false);
	}

	@Override
	public void processBeforeUpdateTBMPsndoc(String pk_hrorg, SuperVO[] updateVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(updateVOs))
			return;
		if (!(updateVOs instanceof TBMPsndocVO[]))
			throw new IllegalArgumentException("updateVOs must by type of TBMPsndocVO[]");
		// 由于开始结束日期可以任意修改，因此只能对比修改前后的日期差别
		// 对于修改前有效，修改后无效的天，要删除考勤档案
		// 对于修改前无效，修改后有效的天，如果在自动排班的范围内，则执行自动排班
		// 首先将updateVOs按人员分组
		TBMPsndocVO[] tbmPsndocVOs = (TBMPsndocVO[]) updateVOs;
		TBMPsndocVO[] dbPsndocVOs = null;
		InSQLCreator isc = new InSQLCreator();
		try {
			String cond = TBMPsndocVO.PK_TBM_PSNDOC + " in("
					+ isc.getInSQL(StringPiecer.getStrArray(updateVOs, TBMPsndocVO.PK_TBM_PSNDOC)) + ")";
			dbPsndocVOs = CommonUtils.retrieveByClause(TBMPsndocVO.class, cond);
		} finally {
			isc.clear();
		}
		Map<String, List<TBMPsndocVO>> psndocListMap = CommonUtils.group2ListByField(TBMPsndocVO.PK_PSNDOC,
				tbmPsndocVOs);
		Map<String, TBMPsndocVO[]> dbPsndocListMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNDOC, dbPsndocVOs);

		// 要删除工作日历的map，key是人员主键，value是日期范围
		Map<String, IDateScope[]> deleteCalendarMap = new HashMap<String, IDateScope[]>();
		// 要执行自动排班的map，key是人员主键，value是日期范围
		Map<String, IDateScope[]> autoArrangeMap = new HashMap<String, IDateScope[]>();
		TimeRuleVO timeRuleVO = null;
		try {
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_hrorg);
		} catch (Exception e) {
			return;
		}
		Integer autoArrangeMonth = timeRuleVO == null ? null : timeRuleVO.getAutoarrangemonth();// 自动排班月数
		// AggShiftVO defaultShift =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryDefaultBclbAggVO(pk_hrorg);//默认班次
		UFLiteralDate now = new UFDate().toUFLiteralDate(TimeZone.getDefault());
		UFLiteralDate endDate = (autoArrangeMonth == null || autoArrangeMonth == 0) ? now : now
				.getDateAfter(autoArrangeMonth * 30);
		IDateScope autoArrangeDateScope = new DefaultDateScope();
		autoArrangeDateScope.setBegindate(now);
		autoArrangeDateScope.setEnddate(endDate);
		// 循环处理每一个人员
		for (String pk_psndoc : psndocListMap.keySet()) {
			// 此人此次修改的考勤档案
			TBMPsndocVO[] uptVOs = psndocListMap.get(pk_psndoc).toArray(new TBMPsndocVO[0]);
			// 取出数据库中对应的vo数组
			TBMPsndocVO[] dbVOs = dbPsndocListMap.get(pk_psndoc);
			// 对于修改前有效，修改后无效的天，要删除排班
			IDateScope[] YNDates = DateScopeUtils.minusDateScopes(dbVOs, uptVOs);
			if (!ArrayUtils.isEmpty(YNDates)) {
				deleteCalendarMap.put(pk_psndoc, YNDates);
			}
			// if(autoArrangeMonth==null||autoArrangeMonth<=0||defaultShift==null)
			// continue;
			if (autoArrangeMonth == null || autoArrangeMonth <= 0)
				continue;
			// 对于修改前无效，修改后有效的天，如果在自动排班范围内，则执行自动排班
			IDateScope[] NYDates = DateScopeUtils.minusDateScopes(uptVOs, dbVOs);
			if (ArrayUtils.isEmpty(NYDates))
				continue;
			IDateScope[] interDateScopes = DateScopeUtils.intersectionDateScopes(
					new IDateScope[] { autoArrangeDateScope }, NYDates);
			if (ArrayUtils.isEmpty(interDateScopes))
				continue;
			// 走到这里，interDateScopes里面的天肯定是自动排班天数里面的天
			autoArrangeMap.put(pk_psndoc, interDateScopes);
		}
		// 先删除该删除的天
		new PsnCalendarDAO().deleteByDateScopeMap(pk_hrorg, deleteCalendarMap);
		// zengcheng
		// 2011.06.28注释掉下面的代码，因为自动排班放在beforeUpdate中会有问题（自动排班要从数据库中查询考勤档案，而此时考勤档案还未update）
		// 自动排班的工作放在了考勤档案update之后
		// if(autoArrangeMonth==null||autoArrangeMonth<=0||defaultBclb==null)//如果自动排班月数为0或者未定义默认班次，则不用自动排班
		// return;
		// //再给需要排班的天自动排默认班次
		// PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		// psndocIterator = psndocListMap.keySet().iterator();
		// while(psndocIterator.hasNext()){
		// String pk_psndoc = psndocIterator.next();
		// if(!autoArrangeMap.containsKey(pk_psndoc))
		// continue;
		// IDateScope scope =
		// DateScopeUtils.getMaxRangeDateScope(autoArrangeMap.get(pk_psndoc));
		// maintainImpl.useDefault(pk_org, new String[]{pk_psndoc},
		// scope.getBegindate(), scope.getEnddate(), false,defaultBclb);
		// }

	}

	@SuppressWarnings("unchecked")
	@Override
	public void processBeforeDeleteTBMPsnodc(String pk_hrorg, String[] pk_tbmpsndocs) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_tbmpsndocs))
			return;
		// 将考勤档案有效日期范围内的工作日历都删掉
		InSQLCreator isc = new InSQLCreator();

		try {
			String inSQL = isc.getInSQL(pk_tbmpsndocs);
			Collection<TBMPsndocVO> col = new BaseDAO().retrieveByClause(TBMPsndocVO.class, TBMPsndocVO.PK_TBM_PSNDOC
					+ " in(" + inSQL + ")");
			if (CollectionUtils.isEmpty(col))
				return;
			new PsnCalendarDAO().deleteByTBMPsndocVOs(col.toArray(new TBMPsndocVO[0]));
		} finally {
			isc.clear();
		}
	}

	@Override
	public void checkCalendarForChangeShift(String pk_org, Object[] bills) throws BusinessException {
		checkCalendarMutex(pk_org, bills);

	}

	@Override
	public Map<UFLiteralDate, AggPsnCalendar> queryCalendarMapByPsnDates(String pk_hrorg, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		AggPsnCalendar[] vos = queryCalendarVOsByPsnDates(pk_hrorg, pk_psndoc, beginDate, endDate);
		return toMap(vos);
	}

	@Override
	public Map<UFLiteralDate, AggPsnCalendar> queryCalendarMapByPsnDates(String pk_hrorg, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate, Map<String, TimeZone> timeZoneMap,
			Map<String, AggShiftVO> allShiftMap) throws BusinessException {
		AggPsnCalendar[] vos = queryCalendarVOsByPsnDates(pk_hrorg, pk_psndoc, beginDate, endDate, timeZoneMap,
				allShiftMap);
		return toMap(vos);
	}

	@Override
	public Map<UFLiteralDate, AggPsnCalendar> queryCalendarMapByPsnDates(String pk_hrorg, String pk_psndoc,
			IDateScope[] scopes) throws BusinessException {
		if (ArrayUtils.isEmpty(scopes))
			return null;
		if (scopes.length == 1)
			return queryCalendarMapByPsnDates(pk_hrorg, pk_psndoc, scopes[0].getBegindate(), scopes[0].getEnddate());
		IDateScope[] mergedScopes = DateScopeUtils.mergeDateScopes(scopes);
		AggPsnCalendar[] vos = null;
		for (IDateScope dateScope : mergedScopes) {
			vos = (AggPsnCalendar[]) ArrayUtils.addAll(vos,
					queryCalendarVOsByPsnDates(pk_hrorg, pk_psndoc, dateScope.getBegindate(), dateScope.getEnddate()));
		}
		return toMap(vos);
	}

	private Map<UFLiteralDate, AggPsnCalendar> toMap(AggPsnCalendar[] vos) {
		if (ArrayUtils.isEmpty(vos))
			return null;
		Map<UFLiteralDate, AggPsnCalendar> retMap = new HashMap<UFLiteralDate, AggPsnCalendar>();
		for (AggPsnCalendar vo : vos) {
			retMap.put(vo.getDate(), vo);
		}
		return retMap;
	}

	/**
	 * 2011.9.22 V6.1工作日历业务单元化处理完成，班次按照业务单元时区解释 处理原始的工作日历vo数组
	 * 处理tbm_psncalwt表的数据需要从tbm_wt表查询的情形
	 * 
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	private void processOriginalAggPsncalendarVOs(String pk_hrorg, AggPsnCalendar[] vos, boolean is4DataProcessView)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		// 只取工作日历使用到的班次（2012-08-20修改）
		Set<String> shiftList = new HashSet<String>();
		for (AggPsnCalendar vo : vos) {
			String pk_shift = vo.getPsnCalendarVO().getPk_shift();
			if (StringUtils.isEmpty(pk_shift))
				continue;
			shiftList.add(pk_shift);
		}
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryShiftAggVOByPkArray(CollectionUtils.isEmpty(shiftList) ? null
				: shiftList.toArray(new String[0]));
		Map<String, AggShiftVO> allShiftMap = new HashMap<String, AggShiftVO>();
		for (int i = 0, j = ArrayUtils.getLength(shiftVOs); i < j; i++) {
			allShiftMap.put(shiftVOs[i].getShiftVO().getPk_shift(), shiftVOs[i]);
		}
		// boolean hasPkOrg = StringUtils.isNotEmpty(pk_hrorg);
		// Map<String, AggShiftVO> allShiftMap =
		// hasPkOrg?ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_hrorg):new
		// HashMap<String, AggShiftVO>();
		ITimeRuleQueryService timeRuleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
		Map<String, TimeZone> timeZoneMap = timeRuleService.queryTimeZoneMap(pk_hrorg);
		processOriginalAggPsncalendarVOs(pk_hrorg, vos, is4DataProcessView, timeZoneMap, allShiftMap);
	}

	private void processOriginalAggPsncalendarVOs(String pk_hrorg, AggPsnCalendar[] vos, boolean is4DataProcessView,
			Map<String, TimeZone> timeZoneMap, Map<String, AggShiftVO> allShiftMap) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		ITimeRuleQueryService timeRuleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
		TimeZone timeZone = null;
		for (AggPsnCalendar vo : vos) {
			String pk_shift = vo.getPsnCalendarVO().getPk_shift();
			// 公休，或者recreate标志为Y,则一定是从工作日历子表中取数据，即不用再往下走了
			if (ShiftVO.PK_GX.equals(pk_shift) || vo.getPsnCalendarVO().isWtRecreate())
				continue;
			// 走到这里，肯定不是公休，并且排班时也没有重新生成wt，
			// 若不是给考勤数据处理浏览界面使用，则肯定是要从班次生成工作时间段
			// 若是给考勤数据处理浏览界面使用，则要看计算考勤数据时的固化标志，若为N，则要从班次生成工作段，若为Y,则不需要，因为工作日历子表中已经存储了固化结果
			// 如果或者固化标志为N，则也
			if (!is4DataProcessView) {

			} else if (vo.getPsnCalendarVO().getIssolidifywhencalculation() != null
					&& vo.getPsnCalendarVO().getIssolidifywhencalculation().booleanValue()) {
				continue;
			}
			AggShiftVO aggShiftVO = ShiftServiceFacade.getAggShiftVOFromMap(allShiftMap, pk_shift);
			if (aggShiftVO == null)
				continue;
			WTVO[] wtVOs = aggShiftVO.getWTVOs();
			if (ArrayUtils.isEmpty(wtVOs))
				continue;
			PsnWorkTimeVO[] psnWtVOs = new PsnWorkTimeVO[wtVOs.length];
			String pk_psncalendar = vo.getPsnCalendarVO().getPrimaryKey();

			// 有些项目上获取不到date,没有查到什么原因，在此容错处理一下
			UFLiteralDate date = vo.getPsnCalendarVO().getDate();
			if (null == date) {
				date = vo.getPsnCalendarVO().getCalendar();
			}
			if (null == date) {
				PsnCalendarVO cvo = (PsnCalendarVO) new BaseDAO().retrieveByPK(PsnCalendarVO.class, pk_psncalendar);
				date = cvo.getCalendar();
			}
			if (null == date) {
				String psnName = CommonUtils.getPsnName(vo.getPk_psndoc());
				throw new BusinessException(ResHelper.getString("6017hrta", "06017hrta0066")
				/* @res "获取 人员 " */+ psnName + ResHelper.getString("6017hrta", "06017hrta0067")
				/* @res " 的日历信息错误！请对相应的日期重新排班！" */);
			}

			for (int i = 0; i < wtVOs.length; i++) {
				String pkorg = vo.getPsnCalendarVO().getPk_org();
				if (timeZoneMap.containsKey(pkorg)) {
					timeZone = timeZoneMap.get(pkorg);
				} else {
					timeZone = timeRuleService.queryTimeZone(pkorg);
					timeZoneMap.put(pkorg, timeZone);
				}
				psnWtVOs[i] = new PsnWorkTimeVO(wtVOs[i], date.toString(), timeZone);
				psnWtVOs[i].setPk_psncalendar(pk_psncalendar);
			}
			vo.setPsnWorkTimeVO(psnWtVOs);
		}
	}

	private void processOriginalAggPsncalendarVOs(String pk_org, AggPsnCalendar[] vos) throws BusinessException {
		processOriginalAggPsncalendarVOs(pk_org, vos, false);
	}

	private void processOriginalAggPsncalendarVOs(String pk_hrorg, AggPsnCalendar[] vos,
			Map<String, TimeZone> timeZoneMap, Map<String, AggShiftVO> allShiftMap) throws BusinessException {
		processOriginalAggPsncalendarVOs(pk_hrorg, vos, false, timeZoneMap, allShiftMap);
	}

	@Override
	public boolean checkShiftRef(String pk_shift) throws BusinessException {
		return new PsnCalendarDAO().checkShiftRef(pk_shift);
	}

	@Override
	public void checkPeriodSealed(String pk_shift) throws BusinessException {
		if (new PsnCalendarDAO().checkPeriodSealed(pk_shift))
			throw new ValidationException(ResHelper.getString("6017psncalendar", "06017psncalendar0094")
			/* @res "使用此班次的工作日历所在考勤期间已封存!" */);
	}

	@Override
	public boolean isPeriodSealed(String pk_shift) throws BusinessException {
		return new PsnCalendarDAO().checkPeriodSealed(pk_shift);
	}

	@Override
	public void processAfterUpdateShift(AggShiftVO oldShiftVO, String pk_shift) throws BusinessException {
		// IBclbQueryService bclbService =
		// NCLocator.getInstance().lookup(IBclbQueryService.class);
		AggShiftVO shiftVO = ShiftServiceFacade.queryShiftAggVOByPk(pk_shift);
		if (oldShiftVO.isAllTimeSameWithAnother(shiftVO))// 如果班次的关键时间信息都没有修改，则工作日历不用修改
			return;
		if (oldShiftVO.isOnlyKQScopeDifferent(shiftVO)) {// 如果只是刷卡开始截止时间改了，则非常简单，修改psncalendarb的第一个工作段的刷卡ksfrom和最后一个工作段的jsto即可

		}

	}

	@Override
	public void processBeforeDeleteShift(String pk_shift) throws BusinessException {
		new PsnCalendarDAO().deleteBeforeDeleteShift(pk_shift);

	}

	@Override
	public Map<UFLiteralDate, AggPsnCalendar> queryCalendarMapByPsnDates4DataProcessView(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOsByPsnDates0(pk_org, pk_psndoc, beginDate, endDate);
		processOriginalAggPsncalendarVOs(pk_org, retArray, true);
		return toMap(retArray);
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByCondition4DataProcessView(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, FromWhereSQL fromWhereSQL) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOByCondition0(pk_hrorg, beginDate, endDate, fromWhereSQL);
		if (ArrayUtils.isEmpty(retArray))
			return null;
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray, true);
		return DailyDataUtils.groupByPsnDate(retArray);
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByCondition4DataProcessView(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, String psndocInSQL, Map<String, TimeZone> timeZoneMap,
			Map<String, AggShiftVO> allShiftMap) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOByPsndocInSQL0(pk_hrorg, beginDate, endDate, psndocInSQL);
		if (ArrayUtils.isEmpty(retArray))
			return null;
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray, true, timeZoneMap, allShiftMap);
		return DailyDataUtils.groupByPsnDate(retArray);
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByCondition4DataProcessView(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, String psndocInSQL) throws BusinessException {
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_hrorg);
		ITimeRuleQueryService timeRuleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
		Map<String, TimeZone> timeZoneMap = hasPkOrg ? timeRuleService.queryTimeZoneMap(pk_hrorg)
				: new HashMap<String, TimeZone>();
		Map<String, AggShiftVO> allShiftMap = hasPkOrg ? ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_hrorg)
				: new HashMap<String, AggShiftVO>();
		return queryCalendarVOByCondition4DataProcessView(pk_hrorg, beginDate, endDate, psndocInSQL, timeZoneMap,
				allShiftMap);
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByPsnInSQL(String pk_hrorg,
			UFLiteralDate beginDate, UFLiteralDate endDate, String psndocInSQL) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOByPsndocInSQL0(pk_hrorg, beginDate, endDate, psndocInSQL);
		if (ArrayUtils.isEmpty(retArray))
			return null;
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray);
		return DailyDataUtils.groupByPsnDate(retArray);
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByPsnInSQL(String pk_hrorg,
			IDateScope[] dateScopes, String psndocInSQL) throws BusinessException {
		if (ArrayUtils.isEmpty(dateScopes))
			return null;
		IDateScope[] mergedScopes = DateScopeUtils.mergeDateScopes(dateScopes);
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> map = null;
		for (IDateScope mergedScope : mergedScopes) {
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> tempMap = queryCalendarVOByPsnInSQL(pk_hrorg,
					mergedScope.getBegindate(), mergedScope.getEnddate(), psndocInSQL);
			map = CommonUtils.putAll(map, tempMap);
		}
		return map;
	}

	@Override
	public Map<String, Map<UFLiteralDate, AggPsnCalendar>> queryCalendarVOByPsnInSQL(String pk_hrorg,
			IDateScope[] dateScopes, int extendDates, String psndocInSQL) throws BusinessException {
		return queryCalendarVOByPsnInSQL(pk_hrorg, DateScopeUtils.extendScopes(dateScopes, extendDates), psndocInSQL);
	}

	@Override
	public boolean existsCalendar(String pk_tbmpsndoc) throws BusinessException {
		if (StringUtils.isEmpty(pk_tbmpsndoc))
			return false;
		return new PsnCalendarDAO().existsCalendar(pk_tbmpsndoc);
	}

	@Override
	public Map<String, Boolean> existsCalendar(String[] pk_tbmpsndocs) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_tbmpsndocs))
			return null;
		boolean[] results = new PsnCalendarDAO().existsCalendar(pk_tbmpsndocs);
		Map<String, Boolean> retMap = new HashMap<String, Boolean>();
		for (int i = 0; i < results.length; i++) {
			retMap.put(pk_tbmpsndocs[i], results[i]);
		}
		return retMap;
	}

	@Override
	public void deleteByCondAndDateScope(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		new PsnCalendarRecordCreator(IDailyRecordCreator.CREATOR_TYPE_LASTYDAYMATCH).deleteDailyRecord(pk_hrorg,
				fromWhereSQL, beginDate, endDate);

	}

	@Override
	public Map<UFLiteralDate, AggPsnCalendar> queryCalendarMapByPsnDates(String pk_hrorg, String pk_psndoc,
			UFLiteralDate[] allDates) throws BusinessException {
		AggPsnCalendar[] vos = queryCalendarVOsByPsnDates(pk_hrorg, pk_psndoc, allDates);
		return toMap(vos);
	}

	@Override
	public Map<UFLiteralDate, AggPsnCalendar> queryCalendarMapByPsnDates4DataProcessView(String pk_hrorg,
			String pk_psndoc, UFLiteralDate[] allDates) throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOsByPsnDates0(pk_hrorg, pk_psndoc, allDates);
		processOriginalAggPsncalendarVOs(pk_hrorg, retArray, true);
		return toMap(retArray);
	}

	@Override
	public AggPsnCalendar[] queryCalendarVOsByPsnDates(String pk_org, String pk_psndoc, UFLiteralDate[] allDates)
			throws BusinessException {
		AggPsnCalendar[] retArray = queryCalendarVOsByPsnDates0(pk_org, pk_psndoc, allDates);
		processOriginalAggPsncalendarVOs(pk_org, retArray);
		return retArray;
	}

	// @Override
	// public PsnJobCalendarVO[] queryByTeam(String pk_hrorg, String pk_team,
	// UFLiteralDate beginDate, UFLiteralDate endDate)
	// throws BusinessException {
	// // 构造人员查询fromWhereSql
	// FromWhereSQL fromWhereSql =
	// TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(" pk_team='"+pk_team+"' ",
	// TBMPsndocSqlPiecer.ensureTBMPsndocTable(null));
	// return new PsnCalendarMaintainImpl().queryCalendarVOByCondition(pk_hrorg,
	// fromWhereSql, beginDate, endDate);
	// }

	@Override
	// modify by zhouyuh 上面的查询方法有问题，只查出来的日期范围内某个人最新的考勤档案，如果一个人在10.1--10.3属于班组A
	// 10.10-10.20属于班组B，查询条件是9.30--12.1 则上面的查询方法只能查出此人在B班组的工作日历
	public PsnJobCalendarVO[] queryByTeam(String pk_hrorg, String pk_team, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// 构造人员查询fromWhereSql
		FromWhereSQL fromWhereSql = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(" pk_team='" + pk_team + "' ",
				TBMPsndocSqlPiecer.ensureTBMPsndocTable(null));
		// return new
		// PsnCalendarMaintainImpl().queryCalendarVOByCondition(pk_hrorg,
		// fromWhereSql, beginDate, endDate);
		return new PsnCalendarMaintainImpl().queryCalendarVOByConditionForTeam(pk_hrorg, fromWhereSql, beginDate,
				endDate);
	}

	public PsnJobCalendarVO[] queryByTeamAndPsns(String pk_hrorg, String pk_team, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		// 构造人员查询fromWhereSql
		FromWhereSQL fromWhereSql = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(" pk_team='" + pk_team + "' ",
				TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs));
		// return new
		// PsnCalendarMaintainImpl().queryCalendarVOByCondition(pk_hrorg,
		// fromWhereSql, beginDate, endDate);
		return new PsnCalendarMaintainImpl().queryCalendarVOByConditionForTeam(pk_hrorg, fromWhereSql, beginDate,
				endDate);
	}

	@Override
	public PsnJobCalendarVO[] sync2TeamCalendar(String pk_hrorg, String pk_team, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		// 查询班组排班信息和人员工作日历信息
		TeamInfoCalendarVO teamCalendarVO = NCLocator.getInstance().lookup(ITeamCalendarQueryService.class)
				.queryCalendarVOByPKTeams(pk_hrorg, new String[] { pk_team }, beginDate, endDate)[0];
		PsnJobCalendarVO[] psnCalendarVOs = maintainImpl.queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate,
				endDate);
		if (ArrayUtils.isEmpty(psnCalendarVOs))
			return null;
		UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
		// 将班组排班信息做为人员工作日历修改信息
		// 只能同步日期范围内属于这个班组的人员日历
		// 考勤档案的map
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_hrorg, pk_psndocs, beginDate, endDate, true);
        	// 已有工作日历
        	Map<String, Map<String, String>> existsPsnCalendarMap = new PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_hrorg,
        		pk_psndocs, beginDate, endDate);
		for (PsnJobCalendarVO psnCalendarVO : psnCalendarVOs) {
			String pk_psndoc = psnCalendarVO.getPk_psndoc();
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
			;// 人员的考勤档案List
			for (UFLiteralDate date : dates) {
				if (!TBMPsndocVO.isIntersect(psndocList, date.toString()))// 如果当日不在考勤档案范围内，则不同步
					continue;
				// 如果考勤档案的pk_team 不等于传过来的Pk_team则不insert
				TBMPsndocVO vo = TBMPsndocVO.getvoForTeam(psndocList, date.toString());
				if (vo == null)
					continue;
				if (vo.getPk_team() == null || !vo.getPk_team().equals(pk_team)) {
					vo.setPk_team((String)psnCalendarVO.getAttributeValue("jobglbdef7"));
					new BaseDAO().updateVO(vo, new String[] { TBMPsndocVO.PK_TEAM });
				}
				//mod tank 考勤档案如果设定了 "不同步班組工作日曆"则不同步 TODO 判断是否排班
				if(vo.getNotsyncal()!=null && vo.getNotsyncal().booleanValue()){

					    if (existsPsnCalendarMap != null && existsPsnCalendarMap.containsKey(pk_psndoc)
							&& existsPsnCalendarMap.get(pk_psndoc).containsKey(date.toString()))
						continue;
				}
				//mod end
				String dateStr = date.toString();
				// Ares.Tank 2018-9-7 22:18:23 班次和日历天类型都不同就不同步班次
				if (StringUtils.equals(teamCalendarVO.getCalendarMap().get(dateStr), psnCalendarVO.getCalendarMap()
						.get(dateStr))) {
					if (null == teamCalendarVO.getDayTypeMap().get(dateStr)
							|| null == psnCalendarVO.getDayTypeMap().get(dateStr)) {
						continue;
					}
					if (teamCalendarVO.getDayTypeMap().get(dateStr).intValue() == psnCalendarVO.getDayTypeMap()
							.get(dateStr).intValue()) {
						continue;
					}

				}
				// 同步日历天类型 Ares.Tank 2018-9-8 15:39:26
				psnCalendarVO.getDayTypeMap().put(dateStr, teamCalendarVO.getDayTypeMap().get(dateStr));
				psnCalendarVO.getModifiedCalendarMap().put(dateStr, teamCalendarVO.getCalendarMap().get(dateStr));
			}
		}
		// return maintainImpl.save(pk_hrorg,
		// psnCalendarVOs);返回的子表数据不全，导致不在班组的时间段背景色变成在班组的背景色
		maintainImpl.save(pk_hrorg, psnCalendarVOs);
		return queryByTeamAndPsns(pk_hrorg, pk_team, pk_psndocs, beginDate, endDate);

	}

	@Override
	public PsnJobCalendarVO[] sync2TeamCalendar(String pk_hrorg, String pk_team, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		TBMPsndocVO[] tbmpsndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryLatestByConditionForTeam(pk_hrorg, pk_team, beginDate, endDate);
		if (ArrayUtils.isEmpty(tbmpsndocs))
			return null;
		String[] pk_psndocs = StringPiecer.getStrArray(tbmpsndocs, TBMPsndocVO.PK_PSNDOC);
		return sync2TeamCalendar(pk_hrorg, pk_team, pk_psndocs, beginDate, endDate);
	}

	@Override
	public void sync2TeamCalendarAfterCircularlyArrange(String pk_org, Map<String, Map<String, String>> modifiedMap,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean isHolidayCancel) throws BusinessException {
		if (MapUtils.isEmpty(modifiedMap))
			return;
		// 将班组循环排班后的结果以覆盖的方式对班组下的人员进行循环排班
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		for (String pk_team : modifiedMap.keySet()) {
			Map<String, String> shiftMap = modifiedMap.get(pk_team);

			if (MapUtils.isEmpty(shiftMap))
				continue;
			PsnJobCalendarVO[] psnCalendars = queryByTeam(pk_hrorg, pk_team, beginDate, endDate);
			if (ArrayUtils.isEmpty(psnCalendars))
				continue;
			// 构造班次数组
			UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
			List<String> shiftPKs = new ArrayList<String>();
			for (UFLiteralDate date : dates)
				shiftPKs.add(shiftMap.get(date.toString()));
			// maintainImpl.circularArrange(pk_org,
			// SQLHelper.getStrArray(psnCalendars, PsnJobVO.PK_PSNDOC),
			// beginDate, endDate, shiftPKs.toArray(new String[0]),
			// isHolidayCancel, true);
			// 这些人在beginDate 和endDate范围内有可能从属于不同的班组，所以不能针对
			// pk_team将这些人全都按begindate,enddate排班
			// //从属于这个pk_team的每一个人在这个班组上的开始日期和结束日期有可能都不同
			maintainImpl.circularArrangeForTeam(pk_org, SQLHelper.getStrArray(psnCalendars, PsnJobVO.PK_PSNDOC),
					beginDate, endDate, shiftPKs.toArray(new String[0]), isHolidayCancel, true, pk_team);
		}
	}

	@Override
	public void sync2TeamCalendarAfterSave(String pk_hrorg, TeamInfoCalendarVO[] teamVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(teamVOs))
			return;
		List<PsnJobCalendarVO> resultList = new ArrayList<PsnJobCalendarVO>();
		for (TeamInfoCalendarVO teamVO : teamVOs) {
			// 取工作日历日期范围
			String[] dates = teamVO.getModifiedCalendarMap().keySet().toArray(new String[0]);
			if (ArrayUtils.isEmpty(dates))
				continue;
			Arrays.sort(dates);
			PsnJobCalendarVO[] psnCalendars = queryByTeam(pk_hrorg, teamVO.getCteamid(),
					UFLiteralDate.getDate(dates[0]), UFLiteralDate.getDate(dates[dates.length - 1]));
			if (ArrayUtils.isEmpty(psnCalendars))
				continue;
			// 将班组下的人员按班组修改的信息进行修改
			for (PsnJobCalendarVO psnCalendar : psnCalendars) {
				psnCalendar.getModifiedCalendarMap().putAll(teamVO.getModifiedCalendarMap());
				resultList.add(psnCalendar);
			}
		}
		// 保存
		new PsnCalendarMaintainImpl().save(pk_hrorg, resultList.toArray(new PsnJobCalendarVO[0]));
	}

	@Override
	public String getPsnDefaultOnOffDutyTime(String pk_psndoc, UFLiteralDate date, TimeZone timezone, boolean isBegin)
			throws BusinessException {
		// 查找逻辑是，在timezone时区，从date的0点到24点中，是否有非公休的班次。如果没有，则返回默认班次的开始/结束时间。若没有默认班次，则返回8/17点
		// 如果有非公休班次，则返回从0点开始往后/23:59:59开始往前碰到的第一个“班次开始/结束时间”。
		// 若从0点/23:59:59开始，碰不到任何一个班次开始/结束时间，则返回从0点/23:59:59开始往后/前，碰到的第一个属于工作段的时间点
		// 首先，需要找到与date日的24个小时有交集的所有天的工作日历。考虑到时差的因素，查工作日历往前往后推两天比较保险一点
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = toMap(queryCalendarVOsByPsnDates(null, pk_psndoc, beginDate,
				endDate));
		// 如果这五天的范围内都没有工作日历，则只能使用默认时间
		if (MapUtils.isEmpty(calendarMap))
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);
		// 找到与此时间段有交集的第一个非公休班次，如果是找开始时间，则从前往后找，否则从后往前找
		AggPsnCalendar firstCrossCalendar = null;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (int i = isBegin ? 0 : allDates.length - 1; isBegin ? i < allDates.length : i >= 0; i = i
				+ (isBegin ? 1 : -1)) {
			UFLiteralDate d = allDates[i];
			AggPsnCalendar calendar = calendarMap.get(d);
			if (calendar == null || ShiftVO.PK_GX.equals(calendar.getPsnCalendarVO().getPk_shift()))
				continue;
			PsnWorkTimeVO[] wtVOs = calendar.getPsnWorkTimeVO();
			if (!TimeScopeUtils.isCross(wtVOs, timeScope))
				continue;
			if (firstCrossCalendar == null)
				firstCrossCalendar = calendar;
			if (isBegin) {
				// 如果此班次的开始时间在0点，或者在0点后，则直接返回此班次的开始时间
				UFDateTime beginTime = wtVOs[0].getKssj();
				if (!beginTime.before(timeScope.getScope_start_datetime()))
					return beginTime.toStdString(timezone).substring(11, 19);
			} else {
				// 如果此班次的结束时间在23:59:59，或者在之前，则直接返回此班次的结束时间
				UFDateTime endTime = wtVOs[wtVOs.length - 1].getJssj();
				if (!endTime.after(timeScope.getScope_end_datetime()))
					return endTime.toStdString(timezone).substring(11, 19);
			}
		}
		// 走到这里，0点到24点间，没有任何一个班次的开始时间
		if (firstCrossCalendar == null)
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
		ITimeScope[] crossScopes = TimeScopeUtils.intersectionTimeScopes(timeScope,
				firstCrossCalendar.getPsnWorkTimeVO());
		return isBegin ? crossScopes[0].getScope_start_datetime().toStdString(timezone).substring(11, 19)
				: crossScopes[crossScopes.length - 1].getScope_end_datetime().toStdString(timezone).substring(11, 19);
	}

	/**
	 * 如果没有找到某一日的非公休班次，则返回默认班次上班/下班时间。
	 * 
	 * @param pk_psndoc
	 * @param date
	 * @param isBegin
	 * @return
	 * @throws BusinessException
	 */
	private String getPsnDefaultOnOffDutyTime(String pk_psndoc, UFLiteralDate date, boolean isBegin)
			throws BusinessException {
		// 首先查询此人此天的考勤档案，然后查询此考勤档案的任职组织，然后查询此组织的默认班次
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		Map<UFLiteralDate, String> orgMap = psndocService.queryDateJobOrgMap(pk_psndoc, date, date);
		if (MapUtils.isEmpty(orgMap))
			return getDefaultBeginEndTimeWithoutShift(isBegin);
		String pk_org = orgMap.get(date);
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		return getDefaultOnOffDutyTime(defaultShift, isBegin);

	}

	private String[] getPsnDefaultOnOffDutyTime(String pk_psndoc, UFLiteralDate date) throws BusinessException {
		// 首先查询此人此天的考勤档案，然后查询此考勤档案的任职组织，然后查询此组织的默认班次
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		Map<UFLiteralDate, String> orgMap = psndocService.queryDateJobOrgMap(pk_psndoc, date, date);
		if (MapUtils.isEmpty(orgMap))
			return new String[] { getDefaultBeginEndTimeWithoutShift(false), getDefaultBeginEndTimeWithoutShift(false) };
		String pk_org = orgMap.get(date);
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		return getDefaultOnOffDutyTime(defaultShift);

	}

	private String getDefaultOnOffDutyTime(AggShiftVO defaultShift, boolean isBegin) {
		if (defaultShift == null)
			return getDefaultBeginEndTimeWithoutShift(isBegin);
		ShiftVO shiftVO = defaultShift.getShiftVO();
		return isBegin ? shiftVO.getBegintime() : shiftVO.getEndtime();
	}

	private String[] getDefaultOnOffDutyTime(AggShiftVO defaultShift) {
		return new String[] { getDefaultOnOffDutyTime(defaultShift, true), getDefaultOnOffDutyTime(defaultShift, false) };
	}

	@SuppressWarnings("rawtypes")
	@Override
	public UFDouble taPeriodPsnWageDays(UFLiteralDate begindate, UFLiteralDate enddate, String pk_psndoc, String pk_org)
			throws BusinessException {
		// 具体算法参考个人计薪日天数
		if (begindate == null || enddate == null || StringUtils.isEmpty(pk_psndoc) || StringUtils.isEmpty(pk_org))
			return null;
		// 过滤条件
		String cond = " where tbm_psncalendar.calendar >= '" + begindate + "' and tbm_psncalendar.calendar <= '"
				+ enddate + "' and tbm_psncalendar.pk_psndoc = '" + pk_psndoc + "' and tbm_psncalendar.pk_org = '"
				+ pk_org + "' ";
		// 计薪条件 假日对调后切割前的非公休的汇总
		cond += " and ( pk_shift <> '" + ShiftVO.PK_GX + "' or (pk_shift = '" + ShiftVO.PK_GX
				+ "' and original_shift_b4cut not in ('null','~','" + ShiftVO.PK_GX + "'))) ";
		String sql = " select count(calendar) as days from tbm_psncalendar " + cond;
		BaseDAO dao = new BaseDAO();
		Map remap = (Map) dao.executeQuery(sql, new MapProcessor());
		if (remap.isEmpty())
			return null;
		Object value = remap.get("days");
		if (value instanceof Integer)
			return new UFDouble(((Integer) value).doubleValue());
		else if (value instanceof BigDecimal)
			return new UFDouble(((BigDecimal) value).doubleValue());
		return null;
	}

	@Override
	public ChangeShiftCommonVO[] queryExistsCalendarName(ChangeShiftCommonVO[] vos) throws BusinessException {
		Map<String, Map<UFLiteralDate, String>> calendarMap = new PsnCalendarDAO().queryPsnPkShift(vos);
		Map<String, ShiftVO> shiftNameMap = ShiftServiceFacade.queryAllShiftNameMap();
		for (ChangeShiftCommonVO vo : vos) {
			if (vo == null || vo.getBegindate() == null || vo.getEnddate() == null)
				continue;
			String pk_psndoc = vo.getPk_psndoc();
			Map<UFLiteralDate, String> psnCalendarMap = calendarMap.get(pk_psndoc);
			if (psnCalendarMap == null) {
				continue;
			}
			UFLiteralDate beginDate = vo.getBegindate();
			UFLiteralDate endDate = vo.getEnddate();
			UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
			StringBuilder sb = new StringBuilder();
			for (UFLiteralDate date : allDates) {
				String pk_shift = psnCalendarMap.get(date);
				if (StringUtils.isEmpty(pk_shift))
					continue;
				String shiftName = shiftNameMap.get(pk_shift).getMultiLangName();
				sb.append(shiftName).append(",");
			}
			if (sb.length() > 0)
				sb.deleteCharAt(sb.length() - 1);
			vo.setOldshift(sb.toString());
		}
		return vos;
	}

	@Override
	public String getPsnDefaultOvertimeBeginEndTime(String pk_psndoc, UFLiteralDate date, TimeZone timezone,
			boolean isBegin) throws BusinessException {
		// 查找逻辑是，在timezone时区，从date的0点到24点中，是否有非公休的班次。如果没有，则返回默认班次的开始/结束时间。若没有默认班次，则返回8/17点
		// 如果有非公休班次，则返回23:59:59开始往前碰到的第一个“班次结束时间”。
		// 首先，需要找到与date日的24个小时有交集的所有天的工作日历。考虑到时差的因素，查工作日历往前往后推两天比较保险一点
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = toMap(queryCalendarVOsByPsnDates(null, pk_psndoc, beginDate,
				endDate));
		// 如果这五天的范围内都没有工作日历，则只能使用默认时间
		if (MapUtils.isEmpty(calendarMap))
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);

		// 2013-03-30 若是假日加班 则默认的开始时间为班次的开始时间（假日和班次交集的最早时间）
		HRHolidayVO[] holidays = BillProcessHelperAtServer.getOverTimeHolidayScope(pk_psndoc, date, timezone);

		AggPsnCalendar firstCrossCalendar = null;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (int i = allDates.length - 1; i >= 0; i--) {
			UFLiteralDate d = allDates[i];
			AggPsnCalendar calendar = calendarMap.get(d);
			if (calendar == null || ShiftVO.PK_GX.equals(calendar.getPsnCalendarVO().getPk_shift()))
				continue;
			PsnWorkTimeVO[] wtVOs = calendar.getPsnWorkTimeVO();
			if (!TimeScopeUtils.isCross(wtVOs, timeScope))
				continue;
			if (firstCrossCalendar == null)
				firstCrossCalendar = calendar;

			// 如果此班次的结束时间在23:59:59，或者在之前，则直接返回此班次的结束时间
			UFDateTime endTime = wtVOs[wtVOs.length - 1].getJssj();

			// 2012-03-30 若班次和假日加班有交集，则开始时间为交集的开始时间
			ITimeScope[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes(holidays, wtVOs);
			if (!ArrayUtils.isEmpty(intersectionScopes) && isBegin) {
				endTime = TimeScopeUtils.getEarliesStartTime(intersectionScopes);
			}

			if (!endTime.after(timeScope.getScope_end_datetime()))
				return endTime.toStdString(timezone).substring(11, 19);
		}
		// 走到这里，0点到24点间，没有任何一个班次的结束时间
		return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
	}

	protected String[] getPsnDefaultOvertimeBeginEndTime(String pk_psndoc, UFLiteralDate date, TimeZone timezone)
			throws BusinessException {
		// 查找逻辑是，在timezone时区，从date的0点到24点中，是否有非公休的班次。如果没有，则返回默认班次的开始/结束时间。若没有默认班次，则返回8/17点
		// 如果有非公休班次，则返回23:59:59开始往前碰到的第一个“班次结束时间”。
		// 首先，需要找到与date日的24个小时有交集的所有天的工作日历。考虑到时差的因素，查工作日历往前往后推两天比较保险一点
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = toMap(queryCalendarVOsByPsnDates(null, pk_psndoc, beginDate,
				endDate));
		// 如果这五天的范围内都没有工作日历，则只能使用默认时间
		if (MapUtils.isEmpty(calendarMap)) {
			// return new
			// String[]{getDefaultBeginEndTimeWithoutShift(true),null};//按需求应该没有结束时间,这样也会造成开始时间为空
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date);
		}

		// 2013-03-22 若是假日加班 则默认的开始时间为班次的开始时间（假日和班次交集的最早时间）
		HRHolidayVO[] holidays = BillProcessHelperAtServer.getOverTimeHolidayScope(pk_psndoc, date, timezone);

		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);
		AggPsnCalendar firstCrossCalendar = null;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (int i = allDates.length - 1; i >= 0; i--) {
			UFLiteralDate d = allDates[i];
			AggPsnCalendar calendar = calendarMap.get(d);
			if (calendar == null || ShiftVO.PK_GX.equals(calendar.getPsnCalendarVO().getPk_shift()))
				continue;
			PsnWorkTimeVO[] wtVOs = calendar.getPsnWorkTimeVO();
			if (!TimeScopeUtils.isCross(wtVOs, timeScope))
				continue;
			if (firstCrossCalendar == null)
				firstCrossCalendar = calendar;
			ITimeScope[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes(holidays, wtVOs);
			// 如果此班次的结束时间在23:59:59，或者在之前，则直接返回此班次的结束时间
			UFDateTime endTime = wtVOs[wtVOs.length - 1].getJssj();
			if (!endTime.after(timeScope.getScope_end_datetime())) {
				String time = endTime.toStdString(timezone).substring(11, 19);
				// return new String[]{time,time};
				return new String[] {
						ArrayUtils.isEmpty(intersectionScopes) ? time : TimeScopeUtils
								.getEarliesStartTime(intersectionScopes).getUFTime().toString(), time };
			}
		}
		// 走到这里，0点到24点间，没有任何一个班次的结束时间
		return getPsnDefaultOnOffDutyTime(pk_psndoc, date);
	}

	@Override
	public String getOrgDefaultOnOffDutyTime(String pk_org, UFLiteralDate date, TimeZone timezone, boolean isBegin)
			throws BusinessException {
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		if (defaultShift == null)
			return getDefaultBeginEndTimeWithoutShift(isBegin);
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);
		// 如果是找开始时间，则从两天前找到两天后，找到timeScope内的第一个工作段的开始时间
		// 如果是找结束时间，则从两天后找到两天前，找到timeScope内的第一个工作段的结束时间
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		WTVO[] wtVOs = defaultShift.getWTVOs();
		UFDateTime beginTime = timeScope.getScope_start_datetime();
		UFDateTime endTime = timeScope.getScope_end_datetime();
		TimeZone orgTimeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		for (int i = isBegin ? 0 : allDates.length - 1; isBegin ? i < allDates.length : i >= 0; i = i
				+ (isBegin ? 1 : -1)) {
			if (isBegin) {
				for (int j = 0; j < wtVOs.length; j++) {
					UFDateTime wtBeginTime = wtVOs[j].toTimeScope(allDates[i].toStdString(), orgTimeZone)
							.getScope_start_datetime();
					if (!wtBeginTime.before(beginTime) && wtBeginTime.before(endTime))
						return wtVOs[j].getKssj();
				}
			} else {
				for (int j = wtVOs.length - 1; j >= 0; j--) {
					UFDateTime wtEndTime = wtVOs[j].toTimeScope(allDates[i].toStdString(), orgTimeZone)
							.getScope_end_datetime();
					if (!wtEndTime.before(beginTime) && wtEndTime.before(endTime))
						return wtVOs[j].getJssj();
				}
			}
		}
		return getDefaultBeginEndTimeWithoutShift(isBegin);
	}

	@Override
	public String getOrgDefaultOvertimeBeginEndTime(String pk_org, UFLiteralDate date, TimeZone timezone,
			boolean isBegin) throws BusinessException {
		String[] str = getOrgDefaultOvertimeBeginEndTime(pk_org, date, timezone);
		return isBegin ? str[0] : str[1];
	}

	protected String[] getOrgDefaultOvertimeBeginEndTime(String pk_org, UFLiteralDate date, TimeZone timezone)
			throws BusinessException {
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// 找前后五天的组织工作日历
		IWorkCalendarPubService workCalendarService = NCLocator.getInstance().lookup(IWorkCalendarPubService.class);
		String pk_caleanr = workCalendarService.findBusinessWorkCalenar(pk_org);
		WorkCalendarDateVO[] calendarVOs = workCalendarService.findCalendarDateVOsBetweenDateInterval(pk_caleanr,
				beginDate, endDate);
		if (ArrayUtils.isEmpty(calendarVOs)) {
			return new String[] { getDefaultOnOffDutyTime(defaultShift, true),
					getDefaultOnOffDutyTime(defaultShift, false) };
		}
		Map<UFLiteralDate, WorkCalendarDateVO> calendarMap = CommonUtils.toMap(WorkCalendarDateVO.CALENDARDATE,
				calendarVOs);
		if (defaultShift == null) {
			WorkCalendarDateVO curCalendar = calendarMap.get(date);
			// 如果是假日或者周末，或者没有排班，则按照开始8结束17返回
			if (curCalendar == null || curCalendar.getDatetype().intValue() == 1
					|| curCalendar.getDatetype().intValue() == 2)
				return new String[] { getDefaultBeginEndTimeWithoutShift(true),
						getDefaultBeginEndTimeWithoutShift(false) };
			// 如果排了班，且非周末非假日，则按照17点返回
			return new String[] { getDefaultEndTimeWithoutShift(), getDefaultEndTimeWithoutShift() };
		}
		// 从第一天开始往后找，找第一个落在此天范围内的下班时间并返回。若没有，则按默认处理
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		WTVO[] wtVOs = defaultShift.getWTVOs();
		TimeZone orgTimeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		for (UFLiteralDate curDate : allDates) {
			WorkCalendarDateVO vo = calendarMap.get(curDate);
			if (vo == null || vo.getDatetype().intValue() == 1 || vo.getDatetype().intValue() == 2)
				continue;
			UFDateTime offDutyTime = wtVOs[wtVOs.length - 1].toTimeScope(curDate.toStdString(), orgTimeZone)
					.getScope_end_datetime();
			if (!offDutyTime.before(timeScope.getScope_start_datetime())
					&& offDutyTime.before(timeScope.getScope_end_datetime()))
				return new String[] { wtVOs[wtVOs.length - 1].getJssj(), wtVOs[wtVOs.length - 1].getJssj() };

		}
		return new String[] { getDefaultOnOffDutyTime(defaultShift, true), getDefaultOnOffDutyTime(defaultShift, false) };
	}

	private String getDefaultBeginEndTimeWithoutShift(boolean isBegin) {
		return isBegin ? getDefaultBeginTimeWithoutShift() : getDefaultEndTimeWithoutShift();
	}

	private String getDefaultBeginTimeWithoutShift() {
		return "08:00:00";
	}

	private String getDefaultEndTimeWithoutShift() {
		return "17:00:00";
	}

	@Override
	public <T extends ITimeScopeWithBillInfo> T calculatePsnDefaultOnOffDutyTime(T bill, TimeZone clientTimeZone)
			throws BusinessException {
		String pk_psndoc = bill.getPk_psndoc();
		if (StringUtils.isEmpty(pk_psndoc))
			return bill;
		UFDateTime beginTime = bill.getScope_start_datetime();
		// 如果开始时间为空，则默认为客户端时区的当天的第一个上班时间（调用getPsnDefaultOnOffDutyTime即可）
		if (beginTime == null) {
			UFDateTime now = new UFDateTime();
			UFLiteralDate today = now.getDate().toUFLiteralDate(clientTimeZone);
			String beginTimeStr = getPsnDefaultOnOffDutyTime(pk_psndoc, today, clientTimeZone, true);
			beginTime = new UFDateTime(today + " " + beginTimeStr, clientTimeZone);
			bill.setScope_start_datetime(beginTime);
		}
		UFDateTime endTime = bill.getScope_end_datetime();
		if (endTime != null && !endTime.before(beginTime))
			return bill;
		// 从beginTime往后找，找到第一个班次的下班时间为止
		UFLiteralDate date = beginTime.getDate().toUFLiteralDate(ICalendar.BASE_TIMEZONE);
		// 找前两天到后两天共5天的工作日历
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = queryCalendarMapByPsnDates(null, pk_psndoc, beginDate, endDate);
		if (MapUtils.isEmpty(calendarMap)) {
			return bill;
		}
		String pk_joborg = bill.getPk_joborg();
		TimeZone timeZone = bill.getTimezone();// 这个是业务单元的时区，和客户端时区不是一回事
		boolean existsDefaultShift = true;
		AggShiftVO defaultShift = null;
		for (UFLiteralDate curDate : CommonUtils.createDateArray(beginDate, endDate)) {
			AggPsnCalendar calendar = calendarMap.get(curDate);
			if (calendar == null)
				continue;
			String pk_shift = calendar.getPsnCalendarVO().getPk_shift();
			if (ShiftVO.PK_GX.equals(pk_shift)) {
				if (existsDefaultShift && defaultShift == null) {
					defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_joborg);
					existsDefaultShift = defaultShift != null;
				}
				if (defaultShift == null)
					continue;
				WTVO wtVO = defaultShift.getWTVOs()[defaultShift.getWTVOs().length - 1];
				ITimeScope scope = wtVO.toTimeScope(curDate.toStdString(), timeZone);
				if (scope.getScope_end_datetime().after(beginTime)) {
					bill.setScope_end_datetime(scope.getScope_end_datetime());
					return bill;
				}
				continue;
			}
			PsnWorkTimeVO[] workVOs = calendar.getPsnWorkTimeVO();
			if (ArrayUtils.isEmpty(workVOs))
				return bill;
			PsnWorkTimeVO lastWTVO = workVOs[workVOs.length - 1];
			if (lastWTVO.getScope_end_datetime().after(beginTime)) {
				bill.setScope_end_datetime(lastWTVO.getScope_end_datetime());
				return bill;
			}
		}
		return bill;
	}

	@Override
	public <T extends ITimeScopeWithBillInfo> T calculatePsnDefaultOvertimeBeginEndTime(T bill, TimeZone clientTimeZone)
			throws BusinessException {
		String pk_psndoc = bill.getPk_psndoc();
		if (StringUtils.isEmpty(pk_psndoc))
			return bill;
		UFDateTime beginTime = bill.getScope_start_datetime();
		UFDateTime endTime = bill.getScope_end_datetime();
		UFDateTime now = new UFDateTime();
		UFLiteralDate today = now.getDate().toUFLiteralDate(clientTimeZone);
		if (endTime == null) {
			if (beginTime == null) {
				String[] beginEndTimeStr = getPsnDefaultOvertimeBeginEndTime(pk_psndoc, today, clientTimeZone);
				bill.setScope_start_datetime(new UFDateTime(today + " " + beginEndTimeStr[0], clientTimeZone));
				bill.setScope_end_datetime(new UFDateTime(today + " " + beginEndTimeStr[1], clientTimeZone));
				return bill;
			}
			UFLiteralDate date = bill.getScope_start_datetime().getDate().toUFLiteralDate(clientTimeZone);
			String endTimeStr = getPsnDefaultOvertimeBeginEndTime(pk_psndoc, date, clientTimeZone, false);
			endTime = new UFDateTime(date + " " + endTimeStr, clientTimeZone);
			bill.setScope_end_datetime(endTime.before(beginTime) ? beginTime : endTime);
			return bill;
		}

		// 走到这里，end肯定不空
		if (beginTime == null) {
			String beginTimeStr = getPsnDefaultOvertimeBeginEndTime(pk_psndoc, today, clientTimeZone, true);
			beginTime = new UFDateTime(today + " " + beginTimeStr, clientTimeZone);
			bill.setScope_start_datetime(beginTime);
		}
		if (endTime.before(beginTime)) {
			UFLiteralDate date = bill.getScope_start_datetime().getDate().toUFLiteralDate(clientTimeZone);
			String endTimeStr = getPsnDefaultOvertimeBeginEndTime(pk_psndoc, date, clientTimeZone, false);
			endTime = new UFDateTime(date + " " + endTimeStr, clientTimeZone);
			bill.setScope_end_datetime(endTime);
		}
		return bill;

	}

	@Override
	public ITimeScope calculateOrgDefaultOnOffDutyTime(String pk_org, UFDateTime beginTime, UFDateTime endTime,
			TimeZone clientTimeZone) throws BusinessException {
		if (StringUtils.isEmpty(pk_org))
			return null;
		// 如果开始时间为空，则默认为客户端时区的当天的第一个上班时间（调用getOrgDefaultOnOffDutyTime即可）
		if (beginTime == null) {
			UFDateTime now = new UFDateTime();
			UFLiteralDate today = now.getDate().toUFLiteralDate(clientTimeZone);
			String beginTimeStr = getOrgDefaultOnOffDutyTime(pk_org, today, clientTimeZone, true);
			beginTime = new UFDateTime(today + " " + beginTimeStr, clientTimeZone);
		}
		if (endTime != null && !endTime.before(beginTime))
			return new DefaultTimeScope(beginTime, endTime);
		// 从beginTime往后找，找到第一个班次的下班时间为止
		UFLiteralDate date = beginTime.getDate().toUFLiteralDate(ICalendar.BASE_TIMEZONE);
		// 找前两天到后两天共5天的工作日历
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		TimeZone orgTimeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// int onDutyDay = 0;
		int offDutyDay = 0;
		// String onDutyTime = null;
		String offDutyTime = null;
		if (defaultShift != null) {
			WTVO[] wtVOs = defaultShift.getWTVOs();
			// onDutyDay=wtVOs[0].getWtbeginday().intValue();
			// onDutyTime = wtVOs[0].getKssj();
			offDutyDay = wtVOs[wtVOs.length - 1].getWtendday().intValue();
			offDutyTime = wtVOs[wtVOs.length - 1].getJssj();
		} else {
			// onDutyTime = getDefaultBeginTimeWithoutShift();
			offDutyTime = getDefaultEndTimeWithoutShift();
		}
		for (UFLiteralDate curDate : CommonUtils.createDateArray(beginDate, endDate)) {
			// UFDateTime onDutyDateTime =
			// RelativeTimeUtils.toDateTime(onDutyDay, onDutyTime,
			// curDate.toStdString(), orgTimeZone);
			UFDateTime offDutyDateTime = RelativeTimeUtils.toDateTime(offDutyDay, offDutyTime, curDate.toStdString(),
					orgTimeZone);
			if (offDutyDateTime.before(beginTime))
				continue;
			return new DefaultTimeScope(beginTime, offDutyDateTime);
		}
		return null;
	}

	@Override
	public ITimeScope calculateOrgDefaultOvertimeBeginEndTime(String pk_org, UFDateTime beginTime, UFDateTime endTime,
			TimeZone clientTimeZone) throws BusinessException {
		if (StringUtils.isEmpty(pk_org))
			return null;
		UFDateTime now = new UFDateTime();
		UFLiteralDate today = now.getDate().toUFLiteralDate(clientTimeZone);
		if (endTime == null) {
			if (beginTime == null) {
				String[] beginEndTimeStr = getOrgDefaultOvertimeBeginEndTime(pk_org, today, clientTimeZone);
				return new DefaultTimeScope(new UFDateTime(today + " " + beginEndTimeStr[0], clientTimeZone),
						new UFDateTime(today + " " + beginEndTimeStr[1], clientTimeZone));
			}
			UFLiteralDate date = beginTime.getDate().toUFLiteralDate(clientTimeZone);
			String endTimeStr = getOrgDefaultOvertimeBeginEndTime(pk_org, date, clientTimeZone, false);
			endTime = new UFDateTime(date + " " + endTimeStr, clientTimeZone);
			return new DefaultTimeScope(beginTime, endTime);
		}

		// 走到这里，end肯定不空
		if (beginTime == null) {
			String beginTimeStr = getOrgDefaultOvertimeBeginEndTime(pk_org, today, clientTimeZone, true);
			beginTime = new UFDateTime(today + " " + beginTimeStr, clientTimeZone);
		}
		if (endTime.before(beginTime)) {
			UFLiteralDate date = beginTime.getDate().toUFLiteralDate(clientTimeZone);
			String endTimeStr = getOrgDefaultOvertimeBeginEndTime(pk_org, date, clientTimeZone, false);
			endTime = new UFDateTime(date + " " + endTimeStr, clientTimeZone);
		}
		return new DefaultTimeScope(beginTime, endTime);
	}

}