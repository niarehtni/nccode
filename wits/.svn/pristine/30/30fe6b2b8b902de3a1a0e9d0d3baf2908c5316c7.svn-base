package nc.impl.ta.overtime;

import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.hr.wa.IGlobalCountryQueryService;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IOvertimeAppInfoDisplayer;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.bd.countryzone.CountryZoneVO;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.org.OrgQueryUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 加班申请信息项自动带出
 * 
 * @author yucheng
 * 
 */
public class OvertimeAppInfoDisplayer implements IOvertimeAppInfoDisplayer {

	@Override
	public AggOvertimeVO calculate(AggOvertimeVO vo, TimeZone clientTimeZone) throws BusinessException {
		if (vo == null)
			return null;
		IPsnCalendarQueryService psncalendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		OvertimebVO[] subVOs = vo.getBodyVOs();
		if (ArrayUtils.isEmpty(subVOs))
			return vo;
		OvertimehVO hvo = vo.getHeadVO();
		// MOD {台湾地区人员加班8小时休息一小时，加班12小时休息2小时扣减60.120分钟} kevin.nie 2017-09-25
		// start
		UFLiteralDate startDate = null;
		UFLiteralDate endDate = null;
		String pk_psndoc = vo.getHeadVO().getPk_psndoc();
		for (OvertimebVO subvo : subVOs) {
			if (subvo.getStatus() == VOStatus.DELETED)
				continue;
			subvo.setPk_org(hvo.getPk_org());
			subvo.setPk_psndoc(hvo.getPk_psndoc());
			subvo.setPk_psnjob(hvo.getPk_psnjob());

			// 用户设置了开始时间，可以计算默认的结束时间
			OvertimebVO cloneVO = psncalendarService.calculatePsnDefaultOvertimeBeginEndTime(subvo, clientTimeZone);
			subvo.setOvertimebegintime(cloneVO.getOvertimebegintime());
			subvo.setOvertimeendtime(cloneVO.getOvertimeendtime());
			subvo.setOvertimebegindate(new UFLiteralDate(subvo.getOvertimebegintime().getDay()));
			subvo.setOvertimeenddate(new UFLiteralDate(subvo.getOvertimeendtime().getDay()));

			UFLiteralDate sDate = new UFLiteralDate(subvo.getOvertimebegintime().getMillis());
			UFLiteralDate eDate = new UFLiteralDate(subvo.getOvertimeendtime().getMillis());
			if (null == startDate) {
				startDate = sDate;
			} else if (startDate.compareTo(sDate) > 0) {
				startDate = sDate;
			}
			if (null == endDate) {
				endDate = eDate;
			} else if (endDate.compareTo(eDate) < 0) {
				endDate = eDate;
			}

		}

		// 计算时长
		BillProcessHelperAtServer.calOvertimeLength(vo);

		// StringBuilder sql = new StringBuilder();
		// sql.append(" select count(bd_psndoc.pk_psndoc) num from bd_psndoc");
		// sql.append(" left join bd_countryzone on bd_psndoc.country=bd_countryzone.pk_country");
		// sql.append(" where bd_countryzone.code='TW' and bd_psndoc.pk_psndoc=? ");
		// SQLParameter param = new SQLParameter();
		// param.addParam(pk_psndoc);
		// Integer count = (Integer) new BaseDAO().executeQuery(sql.toString(),
		// param, new ColumnProcessor());
		// if (count.intValue() > 0) {
		String pk_hrorg = vo.getHeadVO().getPk_org();
		if (isTWOrg(pk_hrorg)) {
			Map<String, Map<String, Integer>> psnDateTypeMap = NCLocator
					.getInstance()
					.lookup(IHRHolidayQueryService.class)
					.queryPsnWorkDayTypeInfo(vo.getHeadVO().getPk_org(), new String[] { pk_psndoc }, startDate, endDate);
			Map<String, Integer> dateTypeMap = psnDateTypeMap.get(pk_psndoc);
			UFDouble totalHour = UFDouble.ZERO_DBL;
			for (OvertimebVO subvo : vo.getBodyVOs()) {
				subvo.setDeduct(Integer.valueOf(0));
				Integer type = dateTypeMap.get(subvo.getOvertimebegintime().getDate().toStdString());
				if (null != type && type != HolidayVO.DAY_TYPE_WORKDAY) {
					long diff = Math.abs(subvo.getOvertimebegintime().getMillis()
							- subvo.getOvertimeendtime().getMillis());
					long hourMill = 60 * 60 * 1000L;
					UFDouble otLenComp = new UFDouble((diff * 1.0) / hourMill);
					UFDouble otLen = new UFDouble(diff / hourMill);
					Double dnum = ((diff % hourMill) * 1.0) / hourMill;
					//MOD 调整非工作日加班时数扣减规则 James
//					if (dnum.compareTo(Double.valueOf(0.5)) == 0) {
//						otLen = otLen.add(0.5);
//					} else if (dnum.compareTo(Double.valueOf(0.5)) > 0) {
//						otLen.add(1);
//					}

					UFDouble firHour = new UFDouble(4);
					UFDouble secHour = new UFDouble(8);
					UFDouble thdHour = new UFDouble(12);
					if (otLenComp.compareTo(firHour) <= 0) {
						subvo.setLength(otLen);
						subvo.setDeduct(Integer.valueOf(0));
					} else if (otLenComp.compareTo(secHour) < 0) {
						if (dnum.compareTo(Double.valueOf(0.5)) >= 0) {
							subvo.setLength(otLen);
							subvo.setDeduct(Integer.valueOf(30));
						}
						else{
							subvo.setLength(otLen.sub(1.0));
							subvo.setDeduct(Integer.valueOf(30));
						}
					} else if (otLenComp.compareTo(thdHour) < 0) {
						subvo.setLength(otLen.sub(1.0));
						subvo.setDeduct(Integer.valueOf(60));
					} else {
						if (dnum.compareTo(Double.valueOf(0.5)) >= 0) {
							subvo.setLength(otLen.sub(1.0));
							subvo.setDeduct(Integer.valueOf(90));
						}
						else{
							subvo.setLength(otLen.sub(2.0));
							subvo.setDeduct(Integer.valueOf(90));
						}
					}
//					if (type == HolidayVO.DAY_TYPE_HOLIDAY || type == HolidayVO.DAY_TYPE_HOLIDAY_NOTALL) {
//						// if (isHoliday(pk_psndoc, startDate)) {
//						UFDouble len = new UFDouble(Math.floor(subvo.getLength().doubleValue()));
//						subvo.setLength(len);
//					}

					subvo.setActhour(subvo.getLength());
				}
				totalHour = totalHour.add(subvo.getLength());
			}
			vo.getHeadVO().setSumhour(totalHour);
			vo.getHeadVO().setLength(totalHour);
		}
		// {台湾地区人员加班8小时休息一小时，加班12小时休息2小时扣减60.120分钟} kevin.nie 2017-09-25 end

		// 计算已加班时长和设置校验标识
		String pk_org = vo.getOvertimehVO().getPk_org();
		TimeRuleVO timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vo.getOvertimebVOs(), timeruleVO.getTimeZoneMap());
		if (hvo.getPk_overtimetype() != null) {
			new OvertimeDAO().setAlreadyHourAndCheckFlag(true, subVOs);
		}

		return vo;
	}

	// MOD {台湾地区人员加班8小时休息一小时，加班12小时休息2小时扣减60.120分钟} kevin.nie 2017-09-25
	// start
	public boolean isTWOrg(String pk_org) throws BusinessException {
		OrgVO[] orgVOs = OrgQueryUtil.queryOrgVOByPks(new String[] { pk_org });
		IGlobalCountryQueryService czQry = NCLocator.getInstance().lookup(IGlobalCountryQueryService.class);
		CountryZoneVO czVO = czQry.getCountryZoneByPK(orgVOs[0].getCountryzone());
		return czVO.getCode().equals("TW");
	}

	public boolean isHoliday(String pk_psndoc, UFLiteralDate overtimeBeginDate) throws BusinessException {
		String calendar = overtimeBeginDate.toString() + " 12:00:00";
		String strSQL = "select pk_holiday from bd_holiday holiday " + " inner join ( "
				+ " select dd.pk_defdoc from bd_defdoc dd "
				+ " inner join bd_defdoclist dl on dd.pk_defdoclist = dl.pk_defdoclist "
				+ " where dl.code = 'BDHS01_0xx' and dd.code = (case when 'TW'=(select zn.code from bd_psndoc psn "
				+ " inner join org_orgs org on psn.pk_org = org.pk_org "
				+ " inner join bd_countryzone zn on org.countryzone = zn.pk_country where psn.pk_psndoc='" + pk_psndoc
				+ "') then 'TAIWANHOLIDAY' else 'CHINAHOLIDAY' end)) HOL on holiday.pk_holidaysort = pk_defdoc "
				+ " and holiday.dr=0 and holiday.enablestate=2 and holiday.starttime <= '" + calendar
				+ "' and holiday.endtime >= '" + calendar + "' ";
		String pk_holiday = (String) new BaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return StringUtils.isNotEmpty(pk_holiday);
	}
	// {台湾地区人员加班8小时休息一小时，加班12小时休息2小时扣减60.120分钟} kevin.nie 2017-09-25 end

	// @Override
	// public AggOvertimeVO calculate(AggOvertimeVO vo, TimeZone clientTimeZone)
	// throws BusinessException {
	// if(vo==null)
	// return null;
	// IPsnCalendarQueryService psncalendarService =
	// NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
	// OvertimebVO[] subVOs = vo.getBodyVOs();
	// if(ArrayUtils.isEmpty(subVOs))
	// return vo;
	// OvertimehVO hvo = vo.getHeadVO();
	// for(OvertimebVO subvo : subVOs) {
	// if(subvo.getStatus()==VOStatus.DELETED)
	// continue;
	// subvo.setPk_org(hvo.getPk_org());
	// subvo.setPk_psndoc(hvo.getPk_psndoc());
	// subvo.setPk_psnjob(hvo.getPk_psnjob());
	// //用户设置了开始时间，可以计算默认的结束时间
	// OvertimebVO cloneVO =
	// psncalendarService.calculatePsnDefaultOvertimeBeginEndTime(subvo,
	// clientTimeZone);
	// subvo.setOvertimebegintime(cloneVO.getOvertimebegintime());
	// subvo.setOvertimeendtime(cloneVO.getOvertimeendtime());
	// }
	//
	// //计算时长
	// BillProcessHelperAtServer.calOvertimeLength(vo);
	// String pk_org = vo.getOvertimehVO().getPk_org();
	// //取当前组织所有班次信息
	// Map<String, AggShiftVO> aggShiftMap =
	// ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
	// Map<String, ShiftVO> shiftMap =
	// CommonUtils.transferAggMap2HeadMap(aggShiftMap);
	// TimeRuleVO timeruleVO =
	// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
	// nc.impl.ta.timebill.CommonMethods.processBeginEndDatePkJobOrgTimeZone(vo.getOvertimebVOs(),timeruleVO.getTimeZoneMap());
	// IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(subVOs, 3);
	// String pk_psndoc = vo.getHeadVO().getPk_psndoc();
	// Map<UFLiteralDate, String> dateOrgMap =
	// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
	// queryDateJobOrgMap(vo.getHeadVO().getPk_psndoc(), scopes);
	// Map<UFLiteralDate, TimeZone> dateTimeZoneMap =
	// CommonMethods.createDateTimeZoneMap(dateOrgMap,
	// timeruleVO.getTimeZoneMap());
	// Map<UFLiteralDate,AggPsnCalendar> calendarMap =
	// NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarMapByPsnDates(pk_org,
	// pk_psndoc, scopes);
	// Map<String, OverTimeTypeCopyVO> timeitemMap =
	// NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
	// Map<UFLiteralDate, PeriodVO> periodMap =
	// PeriodServiceFacade.queryPeriodMapByDateScopes(pk_org, scopes);
	// OvertimeDAO dao = new OvertimeDAO();
	// OvertimeApplyQueryMaintainImpl impl = new
	// OvertimeApplyQueryMaintainImpl();
	// for(OvertimebVO subVO:subVOs){
	// dao.setAlreadyHour(subVO, shiftMap, dateTimeZoneMap,
	// calendarMap,timeitemMap, periodMap,timeruleVO);
	// //修改加班信息时，要设置是否需要校验
	// if(subVO.getIsneedcheck().booleanValue()&&!impl.isCanCheck(subVO,
	// timeruleVO, aggShiftMap, shiftMap, calendarMap, dateOrgMap, periodMap))
	// subVO.setIsneedcheck(UFBoolean.FALSE);
	// }
	// return vo;
	// }
}
