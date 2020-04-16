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
		// ȡ������ڷ�Χ
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
				cond += " and psndoc.pk_org='" + pk_hrorg + "' ";// Ӧ����Ա��������ҵ��Ԫ���ĸ���
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
	 * ��ѯ��Ա�����ڷ�Χ�ڵĹ���������ʹ��ԭʼ��Ԫ���ݲ�ѯ��û�����κδ���
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
	 * 2011.9.22�Ѵ����깤������ҵ��Ԫ��
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
			cond += " and psndoc.pk_org='" + pk_hrorg + "' ";// Ӧ����Ա��������ҵ��Ԫ���ĸ���
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
	 * ������Ա���������ڷ�Χ��ѯ������������ʹ��Ԫ���ݵ���ԭʼ�Ĳ�ѯ
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
	 * 2011.9.22 V6.1��������ҵ��Ԫ�����������
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
	 * �������յĴ���
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param newHolidayVO
	 * @throws BusinessException
	 */
	protected void arrangeByNewHoliday(String pk_group, String pk_org, HolidayVO newHolidayVO) throws BusinessException {
		if (newHolidayVO == null)
			return;
		// ҵ��Ԫ���еİ�Σ������û�а�Σ����ô���������߼�
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		if (MapUtils.isEmpty(shiftMap))
			return;
		// ���ܴ˼��յ���Ա����
		// String[] enjoyPk_psndocs =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryEnjoyPsndocs(pk_org,
		// newHolidayCopyVO);
		String[] enjoyPk_psndocs = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, newHolidayVO);
		// String[] enjoyPk_psndocs = new String[]{"0001N61000000000I88E"};
		if (ArrayUtils.isEmpty(enjoyPk_psndocs))
			return;
		// ���յ�������ڷ�Χ���������պͶԵ��գ���һ��Ԫ���������գ��ڶ�����������.������ȷ����ѯ���й������������ڷ�Χ
		// UFLiteralDate[] maxHolidayRange =
		// newHolidayVO.getEarliestLatestDate();
		// ��������ר�����ʹ�����Χ����Χ�ܴ�����̫�࣬����������̫�󣬵��´��󣬸���ʹ�������������
		// UFLiteralDate[] allDates =
		// newHolidayVO.getAllSwitchAndHolidayDates();
		// UFLiteralDate[] holidayPerhapsAffectDates =
		// CommonUtils.createDateArray(maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		UFLiteralDate[] holidayPerhapsAffectDates = newHolidayVO.getHolidayPerhapsAffectDates();
		// ҵ��Ԫʱ��
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// �Ե�����map
		List<String> switchList = newHolidayVO.getAllSwitch();
		// ��ѯ���������еĹ������������ڷ�ΧΪ����
		// Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs,
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs, holidayPerhapsAffectDates);
		// ��֯�ڵ�Ĭ�ϰ��
		AggShiftVO defaultAggShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// �˼��յ����ڼ���
		// HolidayCopyVO[] neighborHolidayVOs =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryNeighborHolidays(pk_org,
		// newHolidayVO);
		// HolidayCopyVO[] allHolidayVOs =
		// (HolidayCopyVO[])org.apache.commons.lang.ArrayUtils.addAll(new
		// HolidayCopyVO[]{newHolidayVO}, neighborHolidayVOs);
		HolidayVO[] neighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(newHolidayVO);
		HolidayVO[] allHolidayVOs = (HolidayVO[]) org.apache.commons.lang.ArrayUtils.addAll(
				new HolidayVO[] { newHolidayVO }, neighborHolidayVOs);
		// ���ڼ��յ��������
		// Map<String, Map<String, Boolean>> psnEnjoyInfo =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryHolidayEnjoyInfo2(pk_org,
		// enjoyPk_psndocs, neighborHolidayVOs);
		Map<String, Map<String, Boolean>> psnEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				enjoyPk_psndocs, neighborHolidayVOs);
		if (psnEnjoyInfo == null)
			psnEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// ��Ҫ�����ݿ���ɾ���Ĺ�������
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// ��Ҫinsert�Ĺ�������
		String startdate = newHolidayVO.getStarttime().substring(0, 10);
		// wangywt Ա�����������ѹ������޸�Ϊ���յ���ɫ��ʾ���� 20190516 begin
		// �ж��Ƿ������̨�屾�ػ�
		Boolean twFlag = getOrgTWFlag(pk_org);
		BaseDAO basedao = new BaseDAO();
		// ����ѭ���������
		for (String pk_psndoc : enjoyPk_psndocs) {
			Map<String, Boolean> enjoyInfo = psnEnjoyInfo.get(pk_psndoc);
			if (enjoyInfo == null) {
				enjoyInfo = new HashMap<String, Boolean>();
				psnEnjoyInfo.put(pk_psndoc, enjoyInfo);
			}
			enjoyInfo.put(newHolidayVO.getPk_holiday(), true);// �˼��տ϶������е�
			Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null : existsPsnCalendarMap
					.get(pk_psndoc);
			if (MapUtils.isEmpty(existsCalendarMap))
				continue;
			arrangeOnePsnByNewHoliday(pk_group, pk_org, pk_psndoc, holidayPerhapsAffectDates, switchList,
					allHolidayVOs, psnEnjoyInfo.get(pk_psndoc), existsCalendarMap, shiftMap, defaultAggShiftVO,
					timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList);
			// �����ո�Ϊ����ʱ��Ա����������DATE_DAYTYPE�ֶ��޸�Ϊ2��wangywt
			PsnCalendarVO existsPsnCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(startdate
					.toString());
			if (twFlag.booleanValue()
					&& (ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift()) || !existsPsnCalendar.isHolidayCancel())) {
				String sql = "update tbm_psncalendar set DATE_DAYTYPE =" + 2 + " where CALENDAR = '" + startdate
						+ "' and pk_psndoc = '" + pk_psndoc + "'";
				basedao.executeUpdate(sql);
			}
			// wangywt Ա�����������ѹ������޸�Ϊ���յ���ɫ��ʾ���� 20190516 end
		}
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();
			if (toDelPsnCalendarPk.size() > 0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));

			// //����ǰ��ɾ���������ݣ��������ʧ��
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
	 * �ж���֯�ǲ���̨�屾����֯�������Ƿ�����˱��ػ�
	 * 
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 * @Since wangywt 20190515
	 */
	private Boolean getOrgTWFlag(String pk_org) throws BusinessException {
		// �Ƿ�̨�屾�ػ���
		UFBoolean paraTW = SysInitQuery.getParaBoolean(pk_org, "TWHR01");
		if (!paraTW.booleanValue()) {
			return Boolean.FALSE;
		}
		// �ж���֯�ǲ��Ǳ�����֯
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
	 * �������յ�ʱ�򣬵���ĳ����Ա�����а�Ρ�
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param holidayScope
	 * @param existsCalendarMap
	 * @throws BusinessException
	 */
	private void arrangeOnePsnByNewHoliday(String pk_group, String pk_org,
			String pk_psndoc,
			UFLiteralDate[] holidayPerhapsAffectDates,// ���յ�ʱ�ο���Ӱ����Ű������ʱ�Σ�����������죬�ֱ���10.1��10.2��10.3����ô�˲�������9.30��10.1��10.2��10.3��10.4
			List<String> switchList,// ���յĶԵ����
			HolidayVO[] allHolidayVOs,// ���Ǽ����и�����м��գ������������������ڼ���
			Map<String, Boolean> psnEnjoyInfo,// ���յ��������
			Map<String, PsnCalendarVO> existsCalendarMap,// ��Ա�Ѿ��źõİ��
			Map<String, AggShiftVO> shiftMap, AggShiftVO defaultAggShiftVO, TimeZone timeZone,
			List<String> toDelPsnCalendarPk,// ��Ҫɾ���Ĺ����������ڴ˷���������
			List<AggPsnCalendar> toInsertPsnCalendarVOList// ��Ҫ����Ĺ����������ڴ˷���������
	) throws BusinessException {
		Map<String, String> finalCalendarMap = new HashMap<String, String>();// key��date��value�ǰ���������洢���նԵ��Լ������и�Ľ��
		Map<String, String> calendarB4Switch = new HashMap<String, String>();// key��date��value�ǰ����������¼�Ե�ǰ�İ��
		Map<String, AggPsnCalendar> cutCalendarMap = new HashMap<String, AggPsnCalendar>();// key��date�����һ���������ղ������и�²��ܹ���ʱ��α仯�ˣ�������map
		Set<String> processedDateSet = new HashSet<String>();// �Ѿ������������
		if (!CollectionUtils.isEmpty(switchList)) {// ������ڶԵ����������Ե�
			for (int i = 0; i < switchList.size() - 1; i += 2) {
				String date = switchList.get(i);
				String switchDate = switchList.get(i + 1);
				processedDateSet.add(date);// �����춼��Ϊ�Ѵ���
				processedDateSet.add(switchDate);
				// ȡ���������ڵ����а�Σ�ֻ���������������Ҫ���������������������
				// 1.���춼�Ű��ˣ��Ҷ���������ȡ��
				// 2.һ������һ��û�ţ����˵���������ȡ��
				PsnCalendarVO calendarVO1 = existsCalendarMap == null ? null : existsCalendarMap.get(date);
				PsnCalendarVO calendarVO2 = existsCalendarMap == null ? null : existsCalendarMap.get(switchDate);
				if (calendarVO1 == null && calendarVO2 == null)// ������춼û�ţ��򲻶Ե�
					continue;
				if ((calendarVO1 != null && !calendarVO1.isHolidayCancel())
						|| (calendarVO2 != null && !calendarVO2.isHolidayCancel()))// ֻҪ��һ�����վɣ��򲻶Ե�
					continue;
				// �ߵ�����ǿ϶�Ҫ�Ե���
				// ���ȿ����춼�Ű��˵��������������£�����϶�����������ȡ��
				if (calendarVO1 != null && calendarVO2 != null) {
					// �������İ�ζ�һ�����򲻴��������������������������������ݴ���
					if (calendarVO1.getPk_shift().equals(calendarVO2.getPk_shift()))
						continue;
				}
				// �����Ű࣬�����Ű�İ��������������һ����Ĭ�ϰ���ĩ���ݵĹ���
				String pk_shift1 = getPkShiftByCalendarVOAndDate(calendarVO1, date, defaultAggShiftVO);
				String pk_shift2 = getPkShiftByCalendarVOAndDate(calendarVO2, date, defaultAggShiftVO);
				// ��¼�Ե�ǰ���
				calendarB4Switch.put(date, pk_shift1);
				calendarB4Switch.put(switchDate, pk_shift2);
				// ��pk_shift1���õ�switchDate����pk_shift2���õ�date
				processSwitch(pk_psndoc, pk_shift1, switchDate, finalCalendarMap, cutCalendarMap, shiftMap,
						allHolidayVOs, psnEnjoyInfo, timeZone);
				processSwitch(pk_psndoc, pk_shift2, date, finalCalendarMap, cutCalendarMap, shiftMap, allHolidayVOs,
						psnEnjoyInfo, timeZone);
			}
		}

		// ���˶Ե�ȫ��������ϣ����洦����ܱ������ļ���ʱ�θ��ǵ��İ��
		for (UFLiteralDate date : holidayPerhapsAffectDates) {
			if (processedDateSet.contains(date.toString()))// �п�������ĶԵ��Ѿ�����������ˣ������ٴδ���
				continue;
			PsnCalendarVO existsPsnCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(date.toString());
			AggPsnCalendar cutCalendar = null;
			if (existsPsnCalendar == null || ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift())
					|| !existsPsnCalendar.isHolidayCancel())// �������û�Ű�������˹��ݻ����������վɣ���Ҳ���ô���
				continue;
			cutCalendar = PsnCalendarUtils.createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, existsPsnCalendar.getPk_shift()),
					date.toString(), timeZone, allHolidayVOs, psnEnjoyInfo);
			if (cutCalendar != null) {// ��ǿգ���ʾ�������պ󣬴���Ĺ����������и��ˣ���Ҫ��������
				cutCalendar.getPsnCalendarVO().setPk_psndoc(pk_psndoc);
				finalCalendarMap.put(date.toString(), cutCalendar.getPsnCalendarVO().getPk_shift());
				cutCalendarMap.put(date.toString(), cutCalendar);
			}
			// ���Ϊ�գ���ʾ�������պ󣬴���Ĺ���������Ȼû�б��и������������
		}
		// ���ˣ����еĶԵ��������и�Ѵ�����ϣ��Ե����и��������Ҫ���õĹ��������洢��finalCalendarMap�У��Ե�ǰ�İ�δ洢��calendarB4Switch�У�psncalendarvo��Ҫ���⴦��Ĵ洢��cutCalendarMap��
		if (finalCalendarMap.isEmpty())
			return;
		check(pk_org, pk_psndoc, finalCalendarMap);// У��
		createPersistenceRecords(pk_group, pk_org, pk_psndoc, finalCalendarMap, existsCalendarMap, calendarB4Switch,
				cutCalendarMap, toDelPsnCalendarPk, toInsertPsnCalendarVOList, shiftMap);
	}

	private void check(String pk_org, String pk_psndoc, Map<String, String> finalCalendarMap) throws BusinessException {
		Map<String, Map<String, String>> checkMap = new HashMap<String, Map<String, String>>();
		checkMap.put(pk_psndoc, finalCalendarMap);
		new CalendarShiftMutexChecker().checkCalendar(pk_org, checkMap, true, true, false);// У��
	}

	private void processSwitch(String pk_psndoc, String oriPkShift, String switchToDate,
			Map<String, String> finalCalendarMap, Map<String, AggPsnCalendar> cutCalendarMap,
			Map<String, AggShiftVO> shiftMap, HolidayVO[] allHolidayVOs,// ���Ǽ����и�����м��գ������������������ڼ���
			Map<String, Boolean> psnEnjoyInfo,// ���ڼ��յ��������
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
	 * ���calendarvo���գ��򷵻�calendarvo�İ�� ���򷵻�date��Ӧ�����Ĭ�ϰ�Σ��������һ�����壬����Ĭ�ϰ࣬�����ǹ���
	 * 
	 * @param calendarVO
	 * @param date
	 * @param defaultAggShiftVO
	 * @return
	 */
	private String getPkShiftByCalendarVOAndDate(PsnCalendarVO calendarVO, String date, AggShiftVO defaultAggShiftVO) {
		if (calendarVO == null) {// ���û�Ű࣬�����Ĭ�Ϲ������ɰ������
			return TACalendarUtils.getPkShiftByDate(date, defaultAggShiftVO);
		}
		return calendarVO.getPk_shift();
	}

	@Override
	public void arrangeAfterHolidayUpdate(String pk_org, HolidayVO oldHolidayVO, String pk_holiday)
			throws BusinessException {
		// �޸ĺ�ļ���
		HolidayVO holidayVO = HolidayServiceFacade.queryHolidayByPk(pk_holiday);
		arrangeAfterHolidayUpdate((OrgVO) new BaseDAO().retrieveByPK(OrgVO.class, pk_org), oldHolidayVO, holidayVO);
	}

	@Override
	public void arrangeAfterHolidayUpdate(OrgVO orgVO, HolidayVO oldHolidayVO, HolidayVO newHoliday)
			throws BusinessException {

		// ����޸�ǰ�����ʱ�Ρ��Ե���Ϣ������������û�䣬��ʲô��������
		if (oldHolidayVO.isCriticalInfoSame(newHoliday))
			return;
		// ҵ��Ԫ�����еİ�Σ������û�а�Σ����ô���������߼�
		// Map<String, AggBclbDefVO> shiftMap =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
		String pk_org = orgVO.getPk_org();
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		if (MapUtils.isEmpty(shiftMap))
			return;
		// �ҳ��޸�ǰ���е��˺��޸ĺ����е��ˣ��ֳ����ࣺ
		// 1.�޸�ǰ�������У������˰�����ɾ���գ����������մ����������ʱ�κͶԵ����ڶ�û�䣬�򲻴���
		// 2.�޸�ǰ���У��޸ĺ����У���������ɾ�����մ���
		// 3.�޸�ǰ�����У��޸ĺ����У����������������մ���
		// IHolidayQueryService holidayService =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class);
		String[] beforeEnjoyPsns = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, oldHolidayVO);// �޸�ǰ������Ա
		Set<String> beforeEnjoySet = ArrayUtils.isEmpty(beforeEnjoyPsns) ? new HashSet<String>() : new HashSet<String>(
				Arrays.asList(beforeEnjoyPsns));
		String[] afterEnjoyPsns = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, newHoliday);// �޸ĺ�������Ա
		Set<String> afterEnjoySet = ArrayUtils.isEmpty(afterEnjoyPsns) ? new HashSet<String>() : new HashSet<String>(
				Arrays.asList(afterEnjoyPsns));
		// �޸�ǰ��û�����У��򲻴���
		if (beforeEnjoySet.size() == 0 && afterEnjoySet.size() == 0)
			return;
		String pk_group = orgVO.getPk_group();
		// ���յ�������ڷ�Χ���������պͶԵ��գ���һ��Ԫ���������գ��ڶ�����������.������ȷ����ѯ���й������������ڷ�Χ
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
		Set<String> allPersonSet = new HashSet<String>();// �洢�޸�ǰ���к��޸ĺ����е������˵�set����֮�Ǵ˴��޸Ŀ����漰����������
		allPersonSet.addAll(beforeEnjoySet);
		allPersonSet.addAll(afterEnjoySet);
		// ��ѯ���������еĹ������������ڷ�ΧΪ����
		// Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org,
		// allPersonSet.toArray(new String[0]),
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_org, allPersonSet.toArray(new String[0]), allDates);
		// �Ե����ڵ�list
		List<String> oldSwitchList = oldHolidayVO.getAllSwitch();
		List<String> newSwitchList = newHoliday.getAllSwitch();
		// ҵ��Ԫ��ʱ��
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// ҵ��Ԫ�ڵ�Ĭ�ϰ��
		AggShiftVO defaultAggShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// �޸�ǰ���յ����ڼ���
		HolidayVO[] oldNeighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(oldHolidayVO);
		// �޸ĺ���յ����ڼ���
		HolidayVO[] newNeighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(newHoliday);
		// �޸�ǰ���ڼ��յ��������
		Map<String, Map<String, Boolean>> oldEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org, null,
				oldNeighborHolidayVOs);
		if (oldEnjoyInfo == null)// Ϊnull��newһ������ֹ����Ŀ�ָ��
			oldEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		// �޸ĺ����ڼ��յ��������
		Map<String, Map<String, Boolean>> newEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org, null,
				newNeighborHolidayVOs);
		if (newEnjoyInfo == null)// Ϊnull��newһ������ֹ����Ŀ�ָ��
			newEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// ��Ҫ�����ݿ���ɾ���Ĺ�������
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// ��Ҫinsert�Ĺ�������
		// �����ҳ�������
		Set<String> YYSet = new HashSet<String>();// �޸�ǰ�����е���Ա����set
		Set<String> YNSet = new HashSet<String>();// �޸�ǰ���У��޸ĺ�����
		Set<String> NYSet = new HashSet<String>();// �޸�ǰ�����У��޸ĺ�����
		for (String beforePkPsndoc : beforeEnjoySet) {// ���޸�ǰ���е���Աѭ��
			if (afterEnjoySet.contains(beforePkPsndoc)) {// ����޸�ǰ���������
				YYSet.add(beforePkPsndoc);
				continue;
			}
			YNSet.add(beforePkPsndoc);
		}

		for (String afterPkPsndoc : afterEnjoySet) {// ���޸ĺ����Աѭ��
			if (beforeEnjoySet.contains(afterPkPsndoc)) {// ����޸�ǰ��������ˣ��򲻴�����Ϊǰ��һ��ѭ���Ѿ��ӹ���
				continue;
			}
			NYSet.add(afterPkPsndoc);
		}
		// �����޸�ǰ�����޸ĺ����е�
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
		// �����޸�ǰ�������޸ĺ����е�
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
		// ����Ҫɾ���ĺ�Ҫinsert��
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
		// ���洦���޸�ǰҲ�����޸ĺ�Ҳ���е���
		// ��������������ˣ������������ʱ�κͶԵ����ڶ�û�䣬����Щ�˲��ô���
		if (YYSet.isEmpty()
				|| (oldHolidayVO.isSameHolidayScope(newHoliday) && oldHolidayVO.isSameSwithDates(newHoliday)))
			return;
		// ������ɾ������������,������ѭ������ѭ��ɾ������ѭ��insert�����Ч��
		for (String pk_psndoc : YYSet) {// �ȴ���ɾ�����յ�ѭ��
			Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null : existsPsnCalendarMap
					.get(pk_psndoc);
			if (MapUtils.isEmpty(existsCalendarMap))
				continue;
			arrangeOnePsnByDeleteHoliday(pk_group, pk_org, pk_psndoc, oldDates, oldHolidayVO, oldSwitchList,
					oldNeighborHolidayVOs, oldEnjoyInfo.get(pk_psndoc), existsCalendarMap, shiftMap, defaultAggShiftVO,
					timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList, false);
		}

		// ����Ҫɾ���ĺ�Ҫinsert��
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
		// ���²�ѯ���й�����������Ϊ����ĳ־û������ᵼ�¹������������仯
		// existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org,
		// allPersonSet.toArray(new String[0]),
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		existsPsnCalendarMap = new PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org,
				allPersonSet.toArray(new String[0]), allDates);
		HolidayVO[] allHolidayVOs = (HolidayVO[]) org.apache.commons.lang.ArrayUtils.addAll(
				new HolidayVO[] { newHoliday }, newNeighborHolidayVOs);
		for (String pk_psndoc : YYSet) {// �ٴ����������յ�ѭ��
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
		// ����Ҫɾ���ĺ�Ҫinsert��
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
	 * ɾ����������
	 * 
	 * @param pk_org
	 * @param allDates
	 * @throws BusinessException
	 */
	public void deletGdata(String pk_org, UFLiteralDate[] allDates) throws BusinessException {
		// ����ǰ��ɾ���������ݣ��������ʧ��
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
	 * ɾ������ʱ���Ű�
	 * 
	 * @throws BusinessException
	 */
	protected void arrangeByDeleteHoliday(String pk_group, String pk_org, HolidayVO deleteHolidayVO)
			throws BusinessException {
		if (deleteHolidayVO == null)
			return;
		// ҵ��Ԫ�����еİ�Σ������û�а�Σ����ô���������߼�
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		if (MapUtils.isEmpty(shiftMap))
			return;
		// ���ܴ˼��յ���Ա����
		String[] enjoyPk_psndocs = HRHolidayServiceFacade.queryEnjoyPsndocs(pk_org, deleteHolidayVO);
		// String[] enjoyPk_psndocs = new String[]{"0001N61000000000I88E"};
		if (ArrayUtils.isEmpty(enjoyPk_psndocs))
			return;
		// ���յ�������ڷ�Χ���������պͶԵ��գ���һ��Ԫ���������գ��ڶ�����������.������ȷ����ѯ���й������������ڷ�Χ
		// UFLiteralDate[] maxHolidayRange =
		// deleteHolidayVO.getEarliestLatestDate();
		// ��������ר�����ʹ�����Χ����Χ�ܴ�����̫�࣬����������̫�󣬵��´��󣬸���ʹ�������������
		// UFLiteralDate[] allDates =
		// deleteHolidayVO.getAllSwitchAndHolidayDates();
		// UFLiteralDate[] holidayPerhapsAffectDates =
		// CommonUtils.createDateArray(maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		UFLiteralDate[] holidayPerhapsAffectDates = deleteHolidayVO.getHolidayPerhapsAffectDates();
		// ʱ��
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// �Ե�����map
		List<String> switchList = deleteHolidayVO.getAllSwitch();
		// ��ѯ���������еĹ������������ڷ�ΧΪ����
		// Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new
		// PsnCalendarDAO().queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs,
		// maxHolidayRange[0].getDateBefore(2),
		// maxHolidayRange[1].getDateAfter(2));
		Map<String, Map<String, PsnCalendarVO>> existsPsnCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_org, enjoyPk_psndocs, holidayPerhapsAffectDates);
		// ��֯�ڵ�Ĭ�ϰ��
		AggShiftVO defaultAggShiftVO = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// �˼��յ����ڼ���
		HolidayVO[] neighborHolidayVOs = HolidayServiceFacade.queryNeighborHolidays(deleteHolidayVO);
		// ���ڼ��յ��������
		Map<String, Map<String, Boolean>> psnEnjoyInfo = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				enjoyPk_psndocs, neighborHolidayVOs);
		if (psnEnjoyInfo == null)
			psnEnjoyInfo = new HashMap<String, Map<String, Boolean>>();
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// ��Ҫ�����ݿ���ɾ���Ĺ�������
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// ��Ҫinsert�Ĺ�������
		// wangywt Ա�����������ѹ������޸�Ϊ���յ���ɫ��ʾ���� 20190516 begin
		// �ж��Ƿ������̨�屾�ػ�
		String date = deleteHolidayVO.getStarttime().substring(0, 10);// ��ǰ���������
		// ̨�屾�ػ���ʶ
		Boolean twFlag = getOrgTWFlag(pk_org);
		BaseDAO basedao = new BaseDAO();
		// ����ѭ���������
		for (String pk_psndoc : enjoyPk_psndocs) {
			Map<String, PsnCalendarVO> existsCalendarMap = existsPsnCalendarMap == null ? null : existsPsnCalendarMap
					.get(pk_psndoc);
			if (MapUtils.isEmpty(existsCalendarMap))
				continue;
			arrangeOnePsnByDeleteHoliday(pk_group, pk_org, pk_psndoc, holidayPerhapsAffectDates, deleteHolidayVO,
					switchList, neighborHolidayVOs, psnEnjoyInfo.get(pk_psndoc), existsCalendarMap, shiftMap,
					defaultAggShiftVO, timeZone, toDelPsnCalendarPk, toInsertPsnCalendarVOList);
			// �ѹ����������ü���ɾ��ʱ����Ա���������������ɫҲ���Ĺ����ˣ���DATE_DAYTYPE�ĳ�1
			PsnCalendarVO existsPsnCalendar = existsCalendarMap.get(date.toString());
			if ((twFlag.booleanValue())
					&& (ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift())
							&& (StringUtils.isEmpty(existsPsnCalendar.getOriginal_shift_b4cut()) || ShiftVO.PK_GX
									.equals(existsPsnCalendar.getOriginal_shift_b4cut())) || !existsPsnCalendar
								.isHolidayCancel())) {
				String sql = "update tbm_psncalendar set DATE_DAYTYPE =" + 1 + " where CALENDAR = '" + date
						+ "' and pk_psndoc = '" + pk_psndoc + "'";
				basedao.executeUpdate(sql);
				// wangywt Ա�����������ѹ������޸�Ϊ���յ���ɫ��ʾ���� 20190516 end
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
			UFLiteralDate[] holidayPerhapsAffectDates,// ���յ�ʱ�ο���Ӱ����Ű������ʱ�Σ�����������죬�ֱ���10.1��10.2��10.3����ô�˲�������9.30��10.1��10.2��10.3��10.4
			HolidayVO deleteHolidayVO,// ��ɾ���ļ������
			List<String> switchList,// ���յĶԵ����
			HolidayVO[] neighborHolidayVOs,// ���Ǽ����и���������ڼ���
			Map<String, Boolean> psnEnjoyInfo,// ���ڼ��յ��������
			Map<String, PsnCalendarVO> existsCalendarMap,// ��Ա�Ѿ��źõİ��
			Map<String, AggShiftVO> shiftMap, AggShiftVO defaultAggShiftVO, TimeZone timeZone,
			List<String> toDelPsnCalendarPk,// ��Ҫɾ���Ĺ����������ڴ˷���������
			List<AggPsnCalendar> toInsertPsnCalendarVOList// ��Ҫ����Ĺ����������ڴ˷���������
	) throws BusinessException {
		arrangeOnePsnByDeleteHoliday(pk_group, pk_org, pk_psndoc, holidayPerhapsAffectDates, deleteHolidayVO,
				switchList, neighborHolidayVOs, psnEnjoyInfo, existsCalendarMap, shiftMap, defaultAggShiftVO, timeZone,
				toDelPsnCalendarPk, toInsertPsnCalendarVOList, true);
	}

	/**
	 * ɾ�����յ�ʱ�򣬵���ĳ����Ա�����а�Ρ�
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param holidayScope
	 * @param existsCalendarMap
	 * @throws BusinessException
	 */
	private void arrangeOnePsnByDeleteHoliday(String pk_group, String pk_org,
			String pk_psndoc,
			UFLiteralDate[] holidayPerhapsAffectDates,// ���յ�ʱ�ο���Ӱ����Ű������ʱ�Σ�����������죬�ֱ���10.1��10.2��10.3����ô�˲�������9.30��10.1��10.2��10.3��10.4
			HolidayVO deleteHolidayVO,// ��ɾ���ļ������
			List<String> switchList,// ���յĶԵ����
			HolidayVO[] neighborHolidayVOs,// ���Ǽ����и���������ڼ���
			Map<String, Boolean> psnEnjoyInfo,// ���ڼ��յ��������
			Map<String, PsnCalendarVO> existsCalendarMap,// ��Ա�Ѿ��źõİ��
			Map<String, AggShiftVO> shiftMap, AggShiftVO defaultAggShiftVO, TimeZone timeZone,
			List<String> toDelPsnCalendarPk,// ��Ҫɾ���Ĺ����������ڴ˷���������
			List<AggPsnCalendar> toInsertPsnCalendarVOList,// ��Ҫ����Ĺ����������ڴ˷���������
			boolean needCheck) throws BusinessException {
		Map<String, String> finalCalendarMap = new HashMap<String, String>();// key��date��value�ǰ���������洢���նԵ��Լ������и�Ľ��
		Map<String, AggPsnCalendar> cutCalendarMap = new HashMap<String, AggPsnCalendar>();// key��date�����һ���������ղ������и�²��ܹ���ʱ��α仯�ˣ�������map
		Set<String> processedDateSet = new HashSet<String>();// �Ѿ������������
		if (!CollectionUtils.isEmpty(switchList)) {// ������ڶԵ�������򽫶Ե�ǰ�İ�λָ�
			for (int i = 0; i < switchList.size() - 1; i += 2) {
				String date = switchList.get(i);
				String switchDate = switchList.get(i + 1);
				// ȡ���������ڵ����а�Σ�����Ű��ˣ�����������ȡ������ָ����Ե�ǰ�İ�Σ����������������ʱ���ÿ���һ�������������������ݼٲ�ͬ�ĵط���
				PsnCalendarVO calendarVO1 = existsCalendarMap.get(date);
				PsnCalendarVO calendarVO2 = existsCalendarMap.get(switchDate);
				if (calendarVO1 == null && calendarVO2 == null)// ������춼û�ţ��򲻴���
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
		// ���˶Ե�ȫ��������ϣ����洦����ܱ�ɾ���ļ���ʱ�θ��ǵ��İ��
		for (UFLiteralDate date : holidayPerhapsAffectDates) {
			if (processedDateSet.contains(date.toString()))// �п�������ĶԵ��Ѿ�����������ˣ������ٴδ���
				continue;
			PsnCalendarVO existsPsnCalendar = existsCalendarMap.get(date.toString());
			// �������û�Ű�������˹��ݣ��˹���Ҫ���Ǳ������ǹ��ݣ������Ǳ������и�ɵĹ��ݡ��������и�ɵĹ�����ȻҪ��������Ҳ���ô���
			// ԭ���İ�����������վɣ�Ҳ���ô���
			if (existsPsnCalendar == null
					|| (ShiftVO.PK_GX.equals(existsPsnCalendar.getPk_shift()) && (StringUtils.isEmpty(existsPsnCalendar
							.getOriginal_shift_b4cut()) || ShiftVO.PK_GX.equals(existsPsnCalendar
							.getOriginal_shift_b4cut()))) || !existsPsnCalendar.isHolidayCancel())
				continue;
			// �ߵ�����϶�Ҫ���¼����и����������и�ǰ����ֶ���ֵ����ô����϶��Ǳ������и���˹��ݣ�����Ҫ���и�ǰ�İ�������ڼ����ټ���һ�Σ����ûֵ���������ŵİ��ֱ�����¼��㼴��
			String pk_shift = StringUtils.isNotBlank(existsPsnCalendar.getOriginal_shift_b4cut()) ? existsPsnCalendar
					.getOriginal_shift_b4cut() : existsPsnCalendar.getPk_shift();
			AggPsnCalendar cutCalendar = PsnCalendarUtils.createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, pk_shift), date.toString(), timeZone,
					neighborHolidayVOs, psnEnjoyInfo);
			if (cutCalendar != null) {// ����ֵ��Ϊnull����ʾ����ɾ���󣬴�����Ű���Ȼ������������Ӱ��
				cutCalendar.getPsnCalendarVO().setPk_psndoc(pk_psndoc);
				finalCalendarMap.put(date.toString(), cutCalendar.getPsnCalendarVO().getPk_shift());
				cutCalendarMap.put(date.toString(), cutCalendar);
			} else
				// ����ֵΪnull����ʾ����ɾ���󣬴�����Ű಻�ᱻ����������Ӱ��
				finalCalendarMap.put(date.toString(), pk_shift);
		}
		// ���ˣ����еĶԵ��������и�Ѵ�����ϣ��Ե����и��������Ҫ���õĹ��������洢��finalCalendarMap�У��Ե�ǰ�İ�δ洢��calendarB4Switch�У�psncalendarvo��Ҫ���⴦��Ĵ洢��cutCalendarMap��
		if (finalCalendarMap.isEmpty())
			return;
		if (needCheck)
			check(pk_org, pk_psndoc, finalCalendarMap);// У��
		createPersistenceRecords(pk_group, pk_org, pk_psndoc, finalCalendarMap, existsCalendarMap, cutCalendarMap,
				toDelPsnCalendarPk, toInsertPsnCalendarVOList, shiftMap);
	}

	private void createPersistenceRecords(String pk_group, String pk_org, String pk_psndoc,
			Map<String, String> finalCalendarMap, Map<String, PsnCalendarVO> existsCalendarMap,
			Map<String, AggPsnCalendar> cutCalendarMap, List<String> toDelPsnCalendarPk,// ��Ҫɾ���Ĺ����������ڴ˷���������
			List<AggPsnCalendar> toInsertPsnCalendarVOList,// ��Ҫ����Ĺ����������ڴ˷���������
			Map<String, AggShiftVO> shiftMap) throws BusinessException {
		createPersistenceRecords(pk_group, pk_org, pk_psndoc, finalCalendarMap, existsCalendarMap, null,
				cutCalendarMap, toDelPsnCalendarPk, toInsertPsnCalendarVOList, shiftMap);
	}

	/**
	 * ����Ҫ�־û������ݣ�Ҫɾ����pk_psncalendar����toDelPsnCalendarPk��
	 * Ҫinsert�ķ���toInsertPsnCalendarVOList
	 * 
	 * @throws BusinessException
	 */
	private void createPersistenceRecords(String pk_group, String pk_org, String pk_psndoc,
			Map<String, String> finalCalendarMap, Map<String, PsnCalendarVO> existsCalendarMap,
			Map<String, String> calendarB4Switch, Map<String, AggPsnCalendar> cutCalendarMap,
			List<String> toDelPsnCalendarPk,// ��Ҫɾ���Ĺ����������ڴ˷���������
			List<AggPsnCalendar> toInsertPsnCalendarVOList,// ��Ҫ����Ĺ����������ڴ˷���������
			Map<String, AggShiftVO> shiftMap) throws BusinessException {
		// У��ͨ���������еİ��ɾ����Ȼ������insert
		// ѭ������ÿһ��
		String[] dates = finalCalendarMap.keySet().toArray(new String[0]);
		for (String date : dates) {
			if (existsCalendarMap != null && existsCalendarMap.get(date) != null) {// �����Ҫ���õ����Ѿ����˰࣬����һ��İ����Ҫɾ��
				PsnCalendarVO existsCalendar = existsCalendarMap.get(date);
				toDelPsnCalendarPk.add(existsCalendar.getPk_psncalendar());
			}
			// ���湹���������ݿ��vo
			AggPsnCalendar aggVO = cutCalendarMap == null ? null : cutCalendarMap.get(date);
			if (aggVO != null) {
				PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
				toInsertPsnCalendarVOList.add(aggVO);
				continue;
			}
			// �����ߵ�������������������ӱ�϶��ǲ���Ҫ�����ݵģ������������ݼ���
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
	 * ����ɾ������ʱ�Ļָ��Ե����
	 * 
	 * @throws BusinessException
	 */
	private void processResume(PsnCalendarVO calendarVO, Map<String, String> finalCalendarMap,
			Map<String, AggPsnCalendar> cutCalendarMap, HolidayVO[] neighborHolidayVOs,// ���Ǽ����и���������ڼ���
			Map<String, Boolean> psnEnjoyInfo,// ���ڼ��յ��������
			Map<String, AggShiftVO> shiftMap, TimeZone timeZone) throws BusinessException {
		// ������������һ�����������ָ������Ϊ�գ�����������վɣ����߶Ե�ǰ���Ϊ��
		if (calendarVO == null || !calendarVO.isHolidayCancel()
				|| StringUtils.isEmpty(calendarVO.getOriginal_shift_b4exg()))
			return;
		String date = calendarVO.getCalendar().toString();// ����
		String original_shift_b4exg = calendarVO.getOriginal_shift_b4exg();// �Ե�ǰ���
		if (ShiftVO.PK_GX.equals(original_shift_b4exg)) {// ����Ե�ǰ�İ��Ϊ����
			finalCalendarMap.put(date, ShiftVO.PK_GX);
			return;
		}
		// ����Ե�ǰ�İ��Ϊ�ǹ��ݣ���Ҫ��������յĽ���
		AggPsnCalendar cutCalendar = PsnCalendarUtils.createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
				ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, original_shift_b4exg), date, timeZone,
				neighborHolidayVOs, psnEnjoyInfo);
		if (cutCalendar == null) {// ����ͼ���û����
			finalCalendarMap.put(date, original_shift_b4exg);
			return;
		}
		cutCalendar.getPsnCalendarVO().setPk_psndoc(calendarVO.getPk_psndoc());
		finalCalendarMap.put(date, cutCalendar.getPsnCalendarVO().getPk_shift());
		cutCalendarMap.put(date, cutCalendar);
	}

	/*
	 * �˷������ں�̨�Զ��Ű� (non-Javadoc)
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
		// ���ڹ�����Ű������ڵ��ô˷���ǰ�Ѿ�У����˴˴�������ҪУ��
		try {// û���ƶ����ڹ�������쳣�����Ҫcatch
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_hrorg);
		} catch (Exception e) {
			// sb.append(orgname+"û��ָ�����ڹ���");
			return sb.toString();
		}
		Integer autoArrangeMonth = timeRuleVO == null ? null : timeRuleVO.getAutoarrangemonth();
		if (autoArrangeMonth == null || autoArrangeMonth <= 0) {// ���ڹ����Զ��Ű�����Ϊ0����ִ���Զ��Ű�
			// sb.append(orgname+"���ڹ����Զ��Ű�����Ϊ0����ִ���Ű�");
			return sb.toString();
		}
		IAOSQueryService aosService = NCLocator.getInstance().lookup(IAOSQueryService.class);
		// HR��֯�µ�ҵ��Ԫ
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
			// sb.append(orgname+ResHelper.getString("6017basedoc","06017basedoc1849")/*@res"û����Ҫ�Ű����Ա������Ա�Ѿ����źõİ���!"*/+"<br>");
			sb.append(orgname + ResHelper.getString("6017basedoc", "06017basedoc1849")/*
																					 * @
																					 * res
																					 * "û����Ҫ�Ű����Ա������Ա�Ѿ����źõİ���!"
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
	 * �˷��������½����ڵ�����Աʱ���Զ��Ű� �Ű��߼�Ϊ������Ա����Ŀ��ڵ������ڵ��Զ��Ű������������ڵİ�ζ����������������а�Σ�
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
		if (autoArrangeMonth == null || autoArrangeMonth <= 0)// ���ڹ����Զ��Ű�����Ϊ0����ִ���Զ��Ű�
			return;
		// IAOSQueryService aosService =
		// NCLocator.getInstance().lookup(IAOSQueryService.class);
		// HR��֯�µ�ҵ��Ԫ
		// OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false,
		// false);
		OrgVO[] orgVOs = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		// �ӽ��쿪ʼ�������Զ��Ű�����*30��
		UFLiteralDate endDate = UFLiteralDate.getDate(new UFDate().toLocalString().substring(0, 10)).getDateAfter(
				autoArrangeMonth * 30);
		PeriodVO endPeriod = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(pk_hrorg, endDate);
		if (endPeriod == null)
			throw new BusinessException(ResHelper.getString("6017hrta", "06017hrta0065")
			/* @res "�Ű�Ľ�������û�������Ŀ����ڼ�!" */);
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		ITeamCalendarQueryService teamQueryService = NCLocator.getInstance().lookup(ITeamCalendarQueryService.class);
		// TBMPsndocVO[] psndocVOs =
		// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocVOsByPsndocs(pk_hrorg,
		// pk_psndocs,true);
		if (ArrayUtils.isEmpty(psndocVOs))
			return;
		// ����Щ���ڵ�������ְ��֯����
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
			// List<String> teamPsnList = new ArrayList<String>();//������Ա
			// List<String> psnList = new ArrayList<String>();//�ǰ����Ű���Ա
			// UFLiteralDate earliestDate =
			// orgPsndocVOs[0].getBegindate();//��Ҫ�Զ��Ű����������,Ĭ�Ͽ��ڵ����Ŀ�ʼʱ��
			// V65�������Ѿ������Ŀ��ڵ�������Ҫ�����Ű࣬��Ϊ�ضϵ�ԭ���ڵ����Ŀ�ʼʱ�������������Ѿ���棬���°�����Ա�޷�����
			UFLiteralDate earliestDate = orgPsndocVOs[orgPsndocVOs.length - 1].getBegindate();
			String tempPsndoc = null;
			VOUtil.ascSort(orgPsndocVOs, new String[] { TBMPsndocVO.PK_PSNDOC });// ����

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
			// ������鹤������
			for (TBMPsndocVO tbmpsndocVO : orgPsndocVOs) {
				if (StringUtils.isEmpty(tbmpsndocVO.getPk_team())) {
					continue;// �����������Ϊ�գ����ʾ�ǽ����ڵ��������鿪ʼ���ڲ�ֺ��ǰһ�Σ������������Ű�
								// 2015-08-14
				}

				if (earliestDate == null || tbmpsndocVO.getBegindate().before(earliestDate))
					earliestDate = tbmpsndocVO.getBegindate();
				// ������Ĭ���Űദ�� 2011-11-28
				String pk_team = tbmpsndocVO.getPk_team();
				if (StringUtils.isEmpty(pk_team)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// �����ͬһ���ˣ������Ű������ʱ��Ҫ������һ�ε��Ű��û��Ҫ����һ�ΰ��� - modify by -- zhouyuh
				// 2012-06-26
				if (earliestDate != null && earliestDate.before(tbmpsndocVO.getBegindate())
						&& tbmpsndocVO.getPk_psndoc().equals(tempPsndoc)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// ��ѯ��ǰ��Ա�Ű���Ϣ�Ͱ����Ű���Ϣ,������ѯ�ŵ�ѭ������
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
				tempPsndoc = tbmpsndocVO.getPk_psndoc();// �ж�ѭ�����е�������Ƿ����һ�ε�psndoc�Ƿ���ͬһ����
				// ����������Ű��򲻱�
				// �������δ�Ű������ڰ��鵱�����Ű࣬�ð����θ���
				// �������δ�Ű��Ұ���Ҳδ�Ű࣬��ʹ��Ĭ���Ű෽ʽ�Ű�
				// teamCalendars[0].getCalendarMap().putAll(psnCalendars[0].getCalendarMap());
				// psnCalendars[0].getModifiedCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// Ĭ���Ű�ʹ�ð����Σ��������δ����������Ա��Σ������û�У�����Ĭ�ϰ�� *2012-07-23�޸�*
				// psnCalendars[0].getCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// 2013-08-05���
				// ����������û�й������������Ȱ�Ա��������գ�����Ĭ�ϰ���Ű࣬���ҵ��Ԫ���ĺ��λ���ԭ��ҵ��Ԫ�İ�ε�����
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
			// //�����ͬ������Ա�Ű�
			// if(CollectionUtils.isNotEmpty(resultList)){
			// maintainImpl.save(pk_hrorg, resultList.toArray(new
			// PsnJobCalendarVO[0]));
			// maintainImpl.useDefault(orgVO.getPk_org(),
			// teamPsnList.toArray(new String[0]), earliestDate, endDate,
			// false,defaultShiftVO);
			// }
			// //������Ա�Ű�
			// if(CollectionUtils.isNotEmpty(psnList)){
			// String[] psndocs = psnList.toArray(new String[0]);
			// //ҵ��Ԫ�����˱仯����븲���Ű��ˣ������λ���Զҵ��Ԫ��,�˴��ж�������Դͷֱ�����ԭ��������
			// InSQLCreator isc = new InSQLCreator();
			// //��ѯһ��ԭ��ε�ҵ��Ԫ
			// String whereSql =
			// " pk_shift in (select pk_shift from tbm_psncalendar " +
			// " where pk_psndoc in( " + isc.getInSQL(psndocs) +" ) and " +
			// " calendar between '" + earliestDate + "' and '" + endDate +
			// "') " +
			// " and pk_shift <> '0001Z7000000000000GX' and pk_org <> '" +
			// orgVO.getPk_org() + "' ";
			// int count =
			// NCLocator.getInstance().lookup(IPersistenceRetrieve.class).getCountByCondition(ShiftVO.getDefaultTableName(),whereSql);
			// if(count >0){//˵��ҵ��Ԫ�����˸ı䣬���������Ű���
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
	 * �־û������ݿ�֮ǰ,��Ҫ����ĳ�������������
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
		// ����������map��key����Ա������value��key�����ڣ�value�ǹ��������ͣ���holidayvo��
		// <pk_psndoc,<date,����������>>
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
	 * �˷������������½����ڵ�����Աʱ���Զ��Ű� �Ű��߼�Ϊ������Ա����Ŀ��ڵ������ڵ��Զ��Ű������������ڵİ�ζ����������������а�Σ�
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
		if (autoArrangeMonth == null || autoArrangeMonth <= 0)// ���ڹ����Զ��Ű�����Ϊ0����ִ���Զ��Ű�
			return;
		// IAOSQueryService aosService =
		// NCLocator.getInstance().lookup(IAOSQueryService.class);
		// HR��֯�µ�ҵ��Ԫ
		// OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false,
		// false);
		OrgVO[] orgVOs = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		// �ӽ��쿪ʼ�������Զ��Ű�����*30��
		UFLiteralDate endDate = UFLiteralDate.getDate(new UFDate().toLocalString().substring(0, 10)).getDateAfter(
				autoArrangeMonth * 30);
		PeriodVO endPeriod = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(pk_hrorg, endDate);
		if (endPeriod == null)
			throw new BusinessException(ResHelper.getString("6017hrta", "06017hrta0065")
			/* @res "�Ű�Ľ�������û�������Ŀ����ڼ�!" */);
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		ITeamCalendarQueryService teamQueryService = NCLocator.getInstance().lookup(ITeamCalendarQueryService.class);
		TBMPsndocVO[] psndocVOs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocVOsByPsndocs(pk_hrorg, pk_psndocs, true);
		if (ArrayUtils.isEmpty(psndocVOs))
			return;
		// ����Щ���ڵ�������ְ��֯����
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
			// List<String> teamPsnList = new ArrayList<String>();//������Ա
			// List<String> psnList = new ArrayList<String>();//�ǰ����Ű���Ա
			// UFLiteralDate earliestDate =
			// orgPsndocVOs[0].getBegindate();//��Ҫ�Զ��Ű����������,Ĭ�Ͽ��ڵ����Ŀ�ʼʱ��
			// V65�������Ѿ������Ŀ��ڵ�������Ҫ�����Ű࣬��Ϊ�ضϵ�ԭ���ڵ����Ŀ�ʼʱ�������������Ѿ���棬���°�����Ա�޷�����
			UFLiteralDate earliestDate = orgPsndocVOs[orgPsndocVOs.length - 1].getBegindate();
			String tempPsndoc = null;
			VOUtil.ascSort(orgPsndocVOs, new String[] { TBMPsndocVO.PK_PSNDOC });// ����

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
			// ������鹤������
			for (TBMPsndocVO tbmpsndocVO : orgPsndocVOs) {
				if (StringUtils.isEmpty(tbmpsndocVO.getPk_team())) {
					continue;// �����������Ϊ�գ����ʾ�ǽ����ڵ��������鿪ʼ���ڲ�ֺ��ǰһ�Σ������������Ű�
								// 2015-08-14
				}

				if (earliestDate == null || tbmpsndocVO.getBegindate().before(earliestDate))
					earliestDate = tbmpsndocVO.getBegindate();
				// ������Ĭ���Űദ�� 2011-11-28
				String pk_team = tbmpsndocVO.getPk_team();
				if (StringUtils.isEmpty(pk_team)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// �����ͬһ���ˣ������Ű������ʱ��Ҫ������һ�ε��Ű��û��Ҫ����һ�ΰ��� - modify by -- zhouyuh
				// 2012-06-26
				if (earliestDate != null && earliestDate.before(tbmpsndocVO.getBegindate())
						&& tbmpsndocVO.getPk_psndoc().equals(tempPsndoc)) {
					// psnList.add(tbmpsndocVO.getPk_psndoc());
					continue;
				}
				// ��ѯ��ǰ��Ա�Ű���Ϣ�Ͱ����Ű���Ϣ,������ѯ�ŵ�ѭ������
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
				tempPsndoc = tbmpsndocVO.getPk_psndoc();// �ж�ѭ�����е�������Ƿ����һ�ε�psndoc�Ƿ���ͬһ����
				// ����������Ű��򲻱�
				// �������δ�Ű������ڰ��鵱�����Ű࣬�ð����θ���
				// �������δ�Ű��Ұ���Ҳδ�Ű࣬��ʹ��Ĭ���Ű෽ʽ�Ű�
				// teamCalendars[0].getCalendarMap().putAll(psnCalendars[0].getCalendarMap());
				// psnCalendars[0].getModifiedCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// Ĭ���Ű�ʹ�ð����Σ��������δ����������Ա��Σ������û�У�����Ĭ�ϰ�� *2012-07-23�޸�*
				// psnCalendars[0].getCalendarMap().putAll(teamCalendars[0].getCalendarMap());
				// 2013-08-05���
				// ����������û�й������������Ȱ�Ա��������գ�����Ĭ�ϰ���Ű࣬���ҵ��Ԫ���ĺ��λ���ԭ��ҵ��Ԫ�İ�ε�����
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
			// //�����ͬ������Ա�Ű�
			// if(CollectionUtils.isNotEmpty(resultList)){
			// maintainImpl.save(pk_hrorg, resultList.toArray(new
			// PsnJobCalendarVO[0]));
			// maintainImpl.useDefault(orgVO.getPk_org(),
			// teamPsnList.toArray(new String[0]), earliestDate, endDate,
			// false,defaultShiftVO);
			// }
			// //������Ա�Ű�
			// if(CollectionUtils.isNotEmpty(psnList)){
			// String[] psndocs = psnList.toArray(new String[0]);
			// //ҵ��Ԫ�����˱仯����븲���Ű��ˣ������λ���Զҵ��Ԫ��,�˴��ж�������Դͷֱ�����ԭ��������
			// InSQLCreator isc = new InSQLCreator();
			// //��ѯһ��ԭ��ε�ҵ��Ԫ
			// String whereSql =
			// " pk_shift in (select pk_shift from tbm_psncalendar " +
			// " where pk_psndoc in( " + isc.getInSQL(psndocs) +" ) and " +
			// " calendar between '" + earliestDate + "' and '" + endDate +
			// "') " +
			// " and pk_shift <> '0001Z7000000000000GX' and pk_org <> '" +
			// orgVO.getPk_org() + "' ";
			// int count =
			// NCLocator.getInstance().lookup(IPersistenceRetrieve.class).getCountByCondition(ShiftVO.getDefaultTableName(),whereSql);
			// if(count >0){//˵��ҵ��Ԫ�����˸ı䣬���������Ű���
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
	 * �����������ߵǼ�ִ�е���Ĺ��÷���
	 * 
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	protected void arrangeAfterApproveOrRegister(String pk_hrorg, ChangeShiftCommonVO[] vos) throws BusinessException {
		Map<String, PsnJobCalendarVO> psnChangeShiftMap = new HashMap<String, PsnJobCalendarVO>();
		// �����Щ�˵���ְ��֯<pk_psnjob,pk_org>
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
	// //��ѯ������ڵİ��
	// Map<String, Map<String, String>> psnCalendarMap = new
	// PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_hrorg, pk_psndocs,
	// beginDate, endDate);
	// String[] retArray = new String[pk_psndocs.length];
	// //��֯�����а��
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
	// /*@res "����,"*/);
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
		// ����У���map��key����Ա������map��key�����ڣ�value�ǰ������
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
		// ���ڿ�ʼ�������ڿ��������޸ģ����ֻ�ܶԱ��޸�ǰ������ڲ��
		// �����޸�ǰ��Ч���޸ĺ���Ч���죬Ҫɾ�����ڵ���
		// �����޸�ǰ��Ч���޸ĺ���Ч���죬������Զ��Ű�ķ�Χ�ڣ���ִ���Զ��Ű�
		// ���Ƚ�updateVOs����Ա����
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

		// Ҫɾ������������map��key����Ա������value�����ڷ�Χ
		Map<String, IDateScope[]> deleteCalendarMap = new HashMap<String, IDateScope[]>();
		// Ҫִ���Զ��Ű��map��key����Ա������value�����ڷ�Χ
		Map<String, IDateScope[]> autoArrangeMap = new HashMap<String, IDateScope[]>();
		TimeRuleVO timeRuleVO = null;
		try {
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_hrorg);
		} catch (Exception e) {
			return;
		}
		Integer autoArrangeMonth = timeRuleVO == null ? null : timeRuleVO.getAutoarrangemonth();// �Զ��Ű�����
		// AggShiftVO defaultShift =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryDefaultBclbAggVO(pk_hrorg);//Ĭ�ϰ��
		UFLiteralDate now = new UFDate().toUFLiteralDate(TimeZone.getDefault());
		UFLiteralDate endDate = (autoArrangeMonth == null || autoArrangeMonth == 0) ? now : now
				.getDateAfter(autoArrangeMonth * 30);
		IDateScope autoArrangeDateScope = new DefaultDateScope();
		autoArrangeDateScope.setBegindate(now);
		autoArrangeDateScope.setEnddate(endDate);
		// ѭ������ÿһ����Ա
		for (String pk_psndoc : psndocListMap.keySet()) {
			// ���˴˴��޸ĵĿ��ڵ���
			TBMPsndocVO[] uptVOs = psndocListMap.get(pk_psndoc).toArray(new TBMPsndocVO[0]);
			// ȡ�����ݿ��ж�Ӧ��vo����
			TBMPsndocVO[] dbVOs = dbPsndocListMap.get(pk_psndoc);
			// �����޸�ǰ��Ч���޸ĺ���Ч���죬Ҫɾ���Ű�
			IDateScope[] YNDates = DateScopeUtils.minusDateScopes(dbVOs, uptVOs);
			if (!ArrayUtils.isEmpty(YNDates)) {
				deleteCalendarMap.put(pk_psndoc, YNDates);
			}
			// if(autoArrangeMonth==null||autoArrangeMonth<=0||defaultShift==null)
			// continue;
			if (autoArrangeMonth == null || autoArrangeMonth <= 0)
				continue;
			// �����޸�ǰ��Ч���޸ĺ���Ч���죬������Զ��Ű෶Χ�ڣ���ִ���Զ��Ű�
			IDateScope[] NYDates = DateScopeUtils.minusDateScopes(uptVOs, dbVOs);
			if (ArrayUtils.isEmpty(NYDates))
				continue;
			IDateScope[] interDateScopes = DateScopeUtils.intersectionDateScopes(
					new IDateScope[] { autoArrangeDateScope }, NYDates);
			if (ArrayUtils.isEmpty(interDateScopes))
				continue;
			// �ߵ����interDateScopes�������϶����Զ��Ű������������
			autoArrangeMap.put(pk_psndoc, interDateScopes);
		}
		// ��ɾ����ɾ������
		new PsnCalendarDAO().deleteByDateScopeMap(pk_hrorg, deleteCalendarMap);
		// zengcheng
		// 2011.06.28ע�͵�����Ĵ��룬��Ϊ�Զ��Ű����beforeUpdate�л������⣨�Զ��Ű�Ҫ�����ݿ��в�ѯ���ڵ���������ʱ���ڵ�����δupdate��
		// �Զ��Ű�Ĺ��������˿��ڵ���update֮��
		// if(autoArrangeMonth==null||autoArrangeMonth<=0||defaultBclb==null)//����Զ��Ű�����Ϊ0����δ����Ĭ�ϰ�Σ������Զ��Ű�
		// return;
		// //�ٸ���Ҫ�Ű�����Զ���Ĭ�ϰ��
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
		// �����ڵ�����Ч���ڷ�Χ�ڵĹ���������ɾ��
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
	 * 2011.9.22 V6.1��������ҵ��Ԫ��������ɣ���ΰ���ҵ��Ԫʱ������ ����ԭʼ�Ĺ�������vo����
	 * ����tbm_psncalwt���������Ҫ��tbm_wt���ѯ������
	 * 
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	private void processOriginalAggPsncalendarVOs(String pk_hrorg, AggPsnCalendar[] vos, boolean is4DataProcessView)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		// ֻȡ��������ʹ�õ��İ�Σ�2012-08-20�޸ģ�
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
			// ���ݣ�����recreate��־ΪY,��һ���Ǵӹ��������ӱ���ȡ���ݣ�����������������
			if (ShiftVO.PK_GX.equals(pk_shift) || vo.getPsnCalendarVO().isWtRecreate())
				continue;
			// �ߵ�����϶����ǹ��ݣ������Ű�ʱҲû����������wt��
			// �����Ǹ��������ݴ����������ʹ�ã���϶���Ҫ�Ӱ�����ɹ���ʱ���
			// ���Ǹ��������ݴ����������ʹ�ã���Ҫ�����㿼������ʱ�Ĺ̻���־����ΪN����Ҫ�Ӱ�����ɹ����Σ���ΪY,����Ҫ����Ϊ���������ӱ����Ѿ��洢�˹̻����
			// ������߹̻���־ΪN����Ҳ
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

			// ��Щ��Ŀ�ϻ�ȡ����date,û�в鵽ʲôԭ���ڴ��ݴ���һ��
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
				/* @res "��ȡ ��Ա " */+ psnName + ResHelper.getString("6017hrta", "06017hrta0067")
				/* @res " ��������Ϣ���������Ӧ�����������Ű࣡" */);
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
			/* @res "ʹ�ô˰�εĹ����������ڿ����ڼ��ѷ��!" */);
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
		if (oldShiftVO.isAllTimeSameWithAnother(shiftVO))// �����εĹؼ�ʱ����Ϣ��û���޸ģ��������������޸�
			return;
		if (oldShiftVO.isOnlyKQScopeDifferent(shiftVO)) {// ���ֻ��ˢ����ʼ��ֹʱ����ˣ���ǳ��򵥣��޸�psncalendarb�ĵ�һ�������ε�ˢ��ksfrom�����һ�������ε�jsto����

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
	// // ������Ա��ѯfromWhereSql
	// FromWhereSQL fromWhereSql =
	// TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(" pk_team='"+pk_team+"' ",
	// TBMPsndocSqlPiecer.ensureTBMPsndocTable(null));
	// return new PsnCalendarMaintainImpl().queryCalendarVOByCondition(pk_hrorg,
	// fromWhereSql, beginDate, endDate);
	// }

	@Override
	// modify by zhouyuh ����Ĳ�ѯ���������⣬ֻ����������ڷ�Χ��ĳ�������µĿ��ڵ��������һ������10.1--10.3���ڰ���A
	// 10.10-10.20���ڰ���B����ѯ������9.30--12.1 ������Ĳ�ѯ����ֻ�ܲ��������B����Ĺ�������
	public PsnJobCalendarVO[] queryByTeam(String pk_hrorg, String pk_team, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// ������Ա��ѯfromWhereSql
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
		// ������Ա��ѯfromWhereSql
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
		// ��ѯ�����Ű���Ϣ����Ա����������Ϣ
		TeamInfoCalendarVO teamCalendarVO = NCLocator.getInstance().lookup(ITeamCalendarQueryService.class)
				.queryCalendarVOByPKTeams(pk_hrorg, new String[] { pk_team }, beginDate, endDate)[0];
		PsnJobCalendarVO[] psnCalendarVOs = maintainImpl.queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate,
				endDate);
		if (ArrayUtils.isEmpty(psnCalendarVOs))
			return null;
		UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
		// �������Ű���Ϣ��Ϊ��Ա���������޸���Ϣ
		// ֻ��ͬ�����ڷ�Χ����������������Ա����
		// ���ڵ�����map
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_hrorg, pk_psndocs, beginDate, endDate, true);
        	// ���й�������
        	Map<String, Map<String, String>> existsPsnCalendarMap = new PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_hrorg,
        		pk_psndocs, beginDate, endDate);
		for (PsnJobCalendarVO psnCalendarVO : psnCalendarVOs) {
			String pk_psndoc = psnCalendarVO.getPk_psndoc();
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
			;// ��Ա�Ŀ��ڵ���List
			for (UFLiteralDate date : dates) {
				if (!TBMPsndocVO.isIntersect(psndocList, date.toString()))// ������ղ��ڿ��ڵ�����Χ�ڣ���ͬ��
					continue;
				// ������ڵ�����pk_team �����ڴ�������Pk_team��insert
				TBMPsndocVO vo = TBMPsndocVO.getvoForTeam(psndocList, date.toString());
				if (vo == null)
					continue;
				if (vo.getPk_team() == null || !vo.getPk_team().equals(pk_team)) {
					vo.setPk_team((String)psnCalendarVO.getAttributeValue("jobglbdef7"));
					new BaseDAO().updateVO(vo, new String[] { TBMPsndocVO.PK_TEAM });
				}
				//mod tank ���ڵ�������趨�� "��ͬ����M�����Օ�"��ͬ�� TODO �ж��Ƿ��Ű�
				if(vo.getNotsyncal()!=null && vo.getNotsyncal().booleanValue()){

					    if (existsPsnCalendarMap != null && existsPsnCalendarMap.containsKey(pk_psndoc)
							&& existsPsnCalendarMap.get(pk_psndoc).containsKey(date.toString()))
						continue;
				}
				//mod end
				String dateStr = date.toString();
				// Ares.Tank 2018-9-7 22:18:23 ��κ����������Ͷ���ͬ�Ͳ�ͬ�����
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
				// ͬ������������ Ares.Tank 2018-9-8 15:39:26
				psnCalendarVO.getDayTypeMap().put(dateStr, teamCalendarVO.getDayTypeMap().get(dateStr));
				psnCalendarVO.getModifiedCalendarMap().put(dateStr, teamCalendarVO.getCalendarMap().get(dateStr));
			}
		}
		// return maintainImpl.save(pk_hrorg,
		// psnCalendarVOs);���ص��ӱ����ݲ�ȫ�����²��ڰ����ʱ��α���ɫ����ڰ���ı���ɫ
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
		// ������ѭ���Ű��Ľ���Ը��ǵķ�ʽ�԰����µ���Ա����ѭ���Ű�
		PsnCalendarMaintainImpl maintainImpl = new PsnCalendarMaintainImpl();
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		for (String pk_team : modifiedMap.keySet()) {
			Map<String, String> shiftMap = modifiedMap.get(pk_team);

			if (MapUtils.isEmpty(shiftMap))
				continue;
			PsnJobCalendarVO[] psnCalendars = queryByTeam(pk_hrorg, pk_team, beginDate, endDate);
			if (ArrayUtils.isEmpty(psnCalendars))
				continue;
			// ����������
			UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
			List<String> shiftPKs = new ArrayList<String>();
			for (UFLiteralDate date : dates)
				shiftPKs.add(shiftMap.get(date.toString()));
			// maintainImpl.circularArrange(pk_org,
			// SQLHelper.getStrArray(psnCalendars, PsnJobVO.PK_PSNDOC),
			// beginDate, endDate, shiftPKs.toArray(new String[0]),
			// isHolidayCancel, true);
			// ��Щ����beginDate ��endDate��Χ���п��ܴ����ڲ�ͬ�İ��飬���Բ������
			// pk_team����Щ��ȫ����begindate,enddate�Ű�
			// //���������pk_team��ÿһ��������������ϵĿ�ʼ���ںͽ��������п��ܶ���ͬ
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
			// ȡ�����������ڷ�Χ
			String[] dates = teamVO.getModifiedCalendarMap().keySet().toArray(new String[0]);
			if (ArrayUtils.isEmpty(dates))
				continue;
			Arrays.sort(dates);
			PsnJobCalendarVO[] psnCalendars = queryByTeam(pk_hrorg, teamVO.getCteamid(),
					UFLiteralDate.getDate(dates[0]), UFLiteralDate.getDate(dates[dates.length - 1]));
			if (ArrayUtils.isEmpty(psnCalendars))
				continue;
			// �������µ���Ա�������޸ĵ���Ϣ�����޸�
			for (PsnJobCalendarVO psnCalendar : psnCalendars) {
				psnCalendar.getModifiedCalendarMap().putAll(teamVO.getModifiedCalendarMap());
				resultList.add(psnCalendar);
			}
		}
		// ����
		new PsnCalendarMaintainImpl().save(pk_hrorg, resultList.toArray(new PsnJobCalendarVO[0]));
	}

	@Override
	public String getPsnDefaultOnOffDutyTime(String pk_psndoc, UFLiteralDate date, TimeZone timezone, boolean isBegin)
			throws BusinessException {
		// �����߼��ǣ���timezoneʱ������date��0�㵽24���У��Ƿ��зǹ��ݵİ�Ρ����û�У��򷵻�Ĭ�ϰ�εĿ�ʼ/����ʱ�䡣��û��Ĭ�ϰ�Σ��򷵻�8/17��
		// ����зǹ��ݰ�Σ��򷵻ش�0�㿪ʼ����/23:59:59��ʼ��ǰ�����ĵ�һ������ο�ʼ/����ʱ�䡱��
		// ����0��/23:59:59��ʼ���������κ�һ����ο�ʼ/����ʱ�䣬�򷵻ش�0��/23:59:59��ʼ����/ǰ�������ĵ�һ�����ڹ����ε�ʱ���
		// ���ȣ���Ҫ�ҵ���date�յ�24��Сʱ�н�����������Ĺ������������ǵ�ʱ������أ��鹤��������ǰ����������Ƚϱ���һ��
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = toMap(queryCalendarVOsByPsnDates(null, pk_psndoc, beginDate,
				endDate));
		// ���������ķ�Χ�ڶ�û�й�����������ֻ��ʹ��Ĭ��ʱ��
		if (MapUtils.isEmpty(calendarMap))
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);
		// �ҵ����ʱ����н����ĵ�һ���ǹ��ݰ�Σ�������ҿ�ʼʱ�䣬���ǰ�����ң�����Ӻ���ǰ��
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
				// ����˰�εĿ�ʼʱ����0�㣬������0�����ֱ�ӷ��ش˰�εĿ�ʼʱ��
				UFDateTime beginTime = wtVOs[0].getKssj();
				if (!beginTime.before(timeScope.getScope_start_datetime()))
					return beginTime.toStdString(timezone).substring(11, 19);
			} else {
				// ����˰�εĽ���ʱ����23:59:59��������֮ǰ����ֱ�ӷ��ش˰�εĽ���ʱ��
				UFDateTime endTime = wtVOs[wtVOs.length - 1].getJssj();
				if (!endTime.after(timeScope.getScope_end_datetime()))
					return endTime.toStdString(timezone).substring(11, 19);
			}
		}
		// �ߵ����0�㵽24��䣬û���κ�һ����εĿ�ʼʱ��
		if (firstCrossCalendar == null)
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
		ITimeScope[] crossScopes = TimeScopeUtils.intersectionTimeScopes(timeScope,
				firstCrossCalendar.getPsnWorkTimeVO());
		return isBegin ? crossScopes[0].getScope_start_datetime().toStdString(timezone).substring(11, 19)
				: crossScopes[crossScopes.length - 1].getScope_end_datetime().toStdString(timezone).substring(11, 19);
	}

	/**
	 * ���û���ҵ�ĳһ�յķǹ��ݰ�Σ��򷵻�Ĭ�ϰ���ϰ�/�°�ʱ�䡣
	 * 
	 * @param pk_psndoc
	 * @param date
	 * @param isBegin
	 * @return
	 * @throws BusinessException
	 */
	private String getPsnDefaultOnOffDutyTime(String pk_psndoc, UFLiteralDate date, boolean isBegin)
			throws BusinessException {
		// ���Ȳ�ѯ���˴���Ŀ��ڵ�����Ȼ���ѯ�˿��ڵ�������ְ��֯��Ȼ���ѯ����֯��Ĭ�ϰ��
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		Map<UFLiteralDate, String> orgMap = psndocService.queryDateJobOrgMap(pk_psndoc, date, date);
		if (MapUtils.isEmpty(orgMap))
			return getDefaultBeginEndTimeWithoutShift(isBegin);
		String pk_org = orgMap.get(date);
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		return getDefaultOnOffDutyTime(defaultShift, isBegin);

	}

	private String[] getPsnDefaultOnOffDutyTime(String pk_psndoc, UFLiteralDate date) throws BusinessException {
		// ���Ȳ�ѯ���˴���Ŀ��ڵ�����Ȼ���ѯ�˿��ڵ�������ְ��֯��Ȼ���ѯ����֯��Ĭ�ϰ��
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
		// �����㷨�ο����˼�н������
		if (begindate == null || enddate == null || StringUtils.isEmpty(pk_psndoc) || StringUtils.isEmpty(pk_org))
			return null;
		// ��������
		String cond = " where tbm_psncalendar.calendar >= '" + begindate + "' and tbm_psncalendar.calendar <= '"
				+ enddate + "' and tbm_psncalendar.pk_psndoc = '" + pk_psndoc + "' and tbm_psncalendar.pk_org = '"
				+ pk_org + "' ";
		// ��н���� ���նԵ����и�ǰ�ķǹ��ݵĻ���
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
		// �����߼��ǣ���timezoneʱ������date��0�㵽24���У��Ƿ��зǹ��ݵİ�Ρ����û�У��򷵻�Ĭ�ϰ�εĿ�ʼ/����ʱ�䡣��û��Ĭ�ϰ�Σ��򷵻�8/17��
		// ����зǹ��ݰ�Σ��򷵻�23:59:59��ʼ��ǰ�����ĵ�һ������ν���ʱ�䡱��
		// ���ȣ���Ҫ�ҵ���date�յ�24��Сʱ�н�����������Ĺ������������ǵ�ʱ������أ��鹤��������ǰ����������Ƚϱ���һ��
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = toMap(queryCalendarVOsByPsnDates(null, pk_psndoc, beginDate,
				endDate));
		// ���������ķ�Χ�ڶ�û�й�����������ֻ��ʹ��Ĭ��ʱ��
		if (MapUtils.isEmpty(calendarMap))
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);

		// 2013-03-30 ���Ǽ��ռӰ� ��Ĭ�ϵĿ�ʼʱ��Ϊ��εĿ�ʼʱ�䣨���պͰ�ν���������ʱ�䣩
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

			// ����˰�εĽ���ʱ����23:59:59��������֮ǰ����ֱ�ӷ��ش˰�εĽ���ʱ��
			UFDateTime endTime = wtVOs[wtVOs.length - 1].getJssj();

			// 2012-03-30 ����κͼ��ռӰ��н�������ʼʱ��Ϊ�����Ŀ�ʼʱ��
			ITimeScope[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes(holidays, wtVOs);
			if (!ArrayUtils.isEmpty(intersectionScopes) && isBegin) {
				endTime = TimeScopeUtils.getEarliesStartTime(intersectionScopes);
			}

			if (!endTime.after(timeScope.getScope_end_datetime()))
				return endTime.toStdString(timezone).substring(11, 19);
		}
		// �ߵ����0�㵽24��䣬û���κ�һ����εĽ���ʱ��
		return getPsnDefaultOnOffDutyTime(pk_psndoc, date, isBegin);
	}

	protected String[] getPsnDefaultOvertimeBeginEndTime(String pk_psndoc, UFLiteralDate date, TimeZone timezone)
			throws BusinessException {
		// �����߼��ǣ���timezoneʱ������date��0�㵽24���У��Ƿ��зǹ��ݵİ�Ρ����û�У��򷵻�Ĭ�ϰ�εĿ�ʼ/����ʱ�䡣��û��Ĭ�ϰ�Σ��򷵻�8/17��
		// ����зǹ��ݰ�Σ��򷵻�23:59:59��ʼ��ǰ�����ĵ�һ������ν���ʱ�䡱��
		// ���ȣ���Ҫ�ҵ���date�յ�24��Сʱ�н�����������Ĺ������������ǵ�ʱ������أ��鹤��������ǰ����������Ƚϱ���һ��
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = toMap(queryCalendarVOsByPsnDates(null, pk_psndoc, beginDate,
				endDate));
		// ���������ķ�Χ�ڶ�û�й�����������ֻ��ʹ��Ĭ��ʱ��
		if (MapUtils.isEmpty(calendarMap)) {
			// return new
			// String[]{getDefaultBeginEndTimeWithoutShift(true),null};//������Ӧ��û�н���ʱ��,����Ҳ����ɿ�ʼʱ��Ϊ��
			return getPsnDefaultOnOffDutyTime(pk_psndoc, date);
		}

		// 2013-03-22 ���Ǽ��ռӰ� ��Ĭ�ϵĿ�ʼʱ��Ϊ��εĿ�ʼʱ�䣨���պͰ�ν���������ʱ�䣩
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
			// ����˰�εĽ���ʱ����23:59:59��������֮ǰ����ֱ�ӷ��ش˰�εĽ���ʱ��
			UFDateTime endTime = wtVOs[wtVOs.length - 1].getJssj();
			if (!endTime.after(timeScope.getScope_end_datetime())) {
				String time = endTime.toStdString(timezone).substring(11, 19);
				// return new String[]{time,time};
				return new String[] {
						ArrayUtils.isEmpty(intersectionScopes) ? time : TimeScopeUtils
								.getEarliesStartTime(intersectionScopes).getUFTime().toString(), time };
			}
		}
		// �ߵ����0�㵽24��䣬û���κ�һ����εĽ���ʱ��
		return getPsnDefaultOnOffDutyTime(pk_psndoc, date);
	}

	@Override
	public String getOrgDefaultOnOffDutyTime(String pk_org, UFLiteralDate date, TimeZone timezone, boolean isBegin)
			throws BusinessException {
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		if (defaultShift == null)
			return getDefaultBeginEndTimeWithoutShift(isBegin);
		ITimeScope timeScope = DateScopeUtils.toTimeScope(date, timezone);
		// ������ҿ�ʼʱ�䣬�������ǰ�ҵ�������ҵ�timeScope�ڵĵ�һ�������εĿ�ʼʱ��
		// ������ҽ���ʱ�䣬���������ҵ�����ǰ���ҵ�timeScope�ڵĵ�һ�������εĽ���ʱ��
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
		// ��ǰ���������֯��������
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
			// ����Ǽ��ջ�����ĩ������û���Ű࣬���տ�ʼ8����17����
			if (curCalendar == null || curCalendar.getDatetype().intValue() == 1
					|| curCalendar.getDatetype().intValue() == 2)
				return new String[] { getDefaultBeginEndTimeWithoutShift(true),
						getDefaultBeginEndTimeWithoutShift(false) };
			// ������˰࣬�ҷ���ĩ�Ǽ��գ�����17�㷵��
			return new String[] { getDefaultEndTimeWithoutShift(), getDefaultEndTimeWithoutShift() };
		}
		// �ӵ�һ�쿪ʼ�����ң��ҵ�һ�����ڴ��췶Χ�ڵ��°�ʱ�䲢���ء���û�У���Ĭ�ϴ���
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
		// �����ʼʱ��Ϊ�գ���Ĭ��Ϊ�ͻ���ʱ���ĵ���ĵ�һ���ϰ�ʱ�䣨����getPsnDefaultOnOffDutyTime���ɣ�
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
		// ��beginTime�����ң��ҵ���һ����ε��°�ʱ��Ϊֹ
		UFLiteralDate date = beginTime.getDate().toUFLiteralDate(ICalendar.BASE_TIMEZONE);
		// ��ǰ���쵽�����칲5��Ĺ�������
		UFLiteralDate beginDate = date.getDateBefore(2);
		UFLiteralDate endDate = date.getDateAfter(2);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = queryCalendarMapByPsnDates(null, pk_psndoc, beginDate, endDate);
		if (MapUtils.isEmpty(calendarMap)) {
			return bill;
		}
		String pk_joborg = bill.getPk_joborg();
		TimeZone timeZone = bill.getTimezone();// �����ҵ��Ԫ��ʱ�����Ϳͻ���ʱ������һ����
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

		// �ߵ����end�϶�����
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
		// �����ʼʱ��Ϊ�գ���Ĭ��Ϊ�ͻ���ʱ���ĵ���ĵ�һ���ϰ�ʱ�䣨����getOrgDefaultOnOffDutyTime���ɣ�
		if (beginTime == null) {
			UFDateTime now = new UFDateTime();
			UFLiteralDate today = now.getDate().toUFLiteralDate(clientTimeZone);
			String beginTimeStr = getOrgDefaultOnOffDutyTime(pk_org, today, clientTimeZone, true);
			beginTime = new UFDateTime(today + " " + beginTimeStr, clientTimeZone);
		}
		if (endTime != null && !endTime.before(beginTime))
			return new DefaultTimeScope(beginTime, endTime);
		// ��beginTime�����ң��ҵ���һ����ε��°�ʱ��Ϊֹ
		UFLiteralDate date = beginTime.getDate().toUFLiteralDate(ICalendar.BASE_TIMEZONE);
		// ��ǰ���쵽�����칲5��Ĺ�������
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

		// �ߵ����end�϶�����
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