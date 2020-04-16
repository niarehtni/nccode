package nc.impl.ta.overtime;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
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
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * �Ӱ�������Ϣ���Զ�����
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
		// MOD {̨�������Ա�Ӱ�8Сʱ��ϢһСʱ���Ӱ�12Сʱ��Ϣ2Сʱ�ۼ�60.120����} kevin.nie 2017-09-25
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

			// �û������˿�ʼʱ�䣬���Լ���Ĭ�ϵĽ���ʱ��
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

		// ����ʱ��
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
//			Map<String, Map<String, Integer>> psnDateTypeMap = NCLocator
//					.getInstance()
//					.lookup(IHRHolidayQueryService.class)
//					.queryPsnWorkDayTypeInfo(vo.getHeadVO().getPk_org(), new String[] { pk_psndoc }, startDate, endDate);
//			Map<String, Integer> dateTypeMap = psnDateTypeMap.get(pk_psndoc);
			UFDouble totalHour = UFDouble.ZERO_DBL;
			Integer type = GetDateType(startDate,pk_psndoc,pk_hrorg);
			for (OvertimebVO subvo : vo.getBodyVOs()) {
				subvo.setDeduct(Integer.valueOf(0));
				
				if (null != type && type != HolidayVO.DAY_TYPE_WORKDAY) {
					long diff = Math.abs(subvo.getOvertimebegintime().getMillis()
							- subvo.getOvertimeendtime().getMillis());
					long hourMill = 60 * 60 * 1000L;
					UFDouble otLenComp = new UFDouble((diff * 1.0) / hourMill);
					UFDouble otLen = new UFDouble(diff / hourMill);
					Double dnum = ((diff % hourMill) * 1.0) / hourMill;
					//MOD �����ǹ����ռӰ�ʱ���ۼ����� James
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
				
				// �����ռӰ�Ҏ�t�����������Ű�����Ո�Ӱ��ΟoЧ  by George  20190712  ȱ��Bug #27379
				// portal�Ӱ���Ո�۳��r�g����{��  by George  20190620  ȱ��Bug #27571
				// �����ƽ�ռӰ�
				if (vo.getHeadVO().getPk_overtimetype().equals("1001A1100000000009PC")) {
					// ƽ�ռӰ�:90������� �۳��r�g���:30���
					if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 90) {
						subvo.setDeduct(Integer.valueOf(30));
					// ƽ�ռӰ�:90���֮�� �Ӱ��r�L:0С�r	
					} else {
						subvo.setLength(new UFDouble(0.00));
					}	
				// ��ƽ�ռӰ�
				} else {
					// ��ƽ�ռӰ�:240������� & 270���֮�� �۳��r�g���:0���	 
					if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 240 &&
							UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) < 270) {
						subvo.setDeduct(Integer.valueOf(0));
					// ��ƽ�ռӰ�:480������� & 511���֮�� �۳��r�g���:30���		
					} else if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 480 &&
							UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) < 511) {
						subvo.setDeduct(Integer.valueOf(30));
					// ��ƽ�ռӰ�:511������� �۳��r�g���:60���
					} else if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 511) {	
						subvo.setDeduct(Integer.valueOf(60));
					}
					
					// ��ʽ: (�Y���r�g - �_ʼ�r�g - �۳��r�g(���))/60 ���o�l����ȥС���cλ�� ����ȡ2λС���c����
					subvo.setLength(new UFDouble(UFDateTime
							.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()))
					        .sub(subvo.getDeduct()).div(60).setScale(0, UFDouble.ROUND_DOWN)
					        .setScale(2, UFDouble.ROUND_DOWN));
                    
					// ����r�g: ��ƽ�ռӰ�:510������� & 511���֮�� �Ӱ��r�L:8С�r	
					if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 511 &&
							UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) < 540) {	
						subvo.setLength(new UFDouble(8.00));
					}
					
					// ����Ǉ������ռӰ�
					if (vo.getHeadVO().getPk_overtimetype().equals("1001A1100000000009PE")) {
						try {
							// ��ԃ�T�������Օ��У��@λ�T�����M�����@һ�����ڵİ��PK
						    String strSQL = "select pk_shift from tbm_psncalendar where pk_psndoc = '" + vo.getHeadVO().getPk_psndoc() + "'"
							        + " and tbm_psncalendar.pk_org = '" + vo.getHeadVO().getPk_org() + "'"
								    + " and tbm_psncalendar.calendar = '" + subvo.getOvertimebegintime().toString().substring(0, 10) + "'";
						
						    String ispublicholiday = (String) new BaseDAO().executeQuery(strSQL, new ColumnProcessor());
							// ���鹫��(���Ű�) �� �����ֵ
							if(!"0001Z7000000000000GX".equals(ispublicholiday) && !"".equals(ispublicholiday)) {
								// �Ǉ������ռӰ��ҷǹ���(���Ű�): 270������� & 690���֮�� �۳��r�g���:60���
								if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 270 &&
										UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) < 690) {
									subvo.setDeduct(Integer.valueOf(60));
								// �Ǉ������ռӰ��ҷǹ���(���Ű�): 690������� �۳��r�g���:90���  	
								} else if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 690) {
									subvo.setDeduct(Integer.valueOf(90));
								}
								
								// ��ʽ: (�Y���r�g - �_ʼ�r�g - �۳��r�g(���))/60 ���o�l����ȥС���cλ�� ����ȡ2λС���c����
								subvo.setLength(new UFDouble(UFDateTime
										.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()))
								        .sub(subvo.getDeduct()).div(60).setScale(0, UFDouble.ROUND_DOWN)
								        .setScale(2, UFDouble.ROUND_DOWN));
								
								// ����r�g: �Ǉ������ռӰ��ҷǹ���(���Ű�): 240������� & 270���֮��  �������ռӰ��r�L:3С�r
								if (UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) >= 240 &&
										UFDateTime.getMinutesBetween(subvo.getOvertimebegintime(), subvo.getOvertimeendtime()) < 270) {
									subvo.setLength(new UFDouble(3.00));
								}
							}	
						} catch (DAOException e) {
							// TODO �Զ����ɵ� catch ��
							e.printStackTrace();
						}
					}	
				}
				totalHour = totalHour.add(subvo.getLength());
			}
			vo.getHeadVO().setSumhour(totalHour);
			vo.getHeadVO().setLength(totalHour);
		}
		// {̨�������Ա�Ӱ�8Сʱ��ϢһСʱ���Ӱ�12Сʱ��Ϣ2Сʱ�ۼ�60.120����} kevin.nie 2017-09-25 end

		// �����ѼӰ�ʱ��������У���ʶ
		String pk_org = vo.getOvertimehVO().getPk_org();
		TimeRuleVO timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vo.getOvertimebVOs(), timeruleVO.getTimeZoneMap());
		if (hvo.getPk_overtimetype() != null) {
			new OvertimeDAO().setAlreadyHourAndCheckFlag(true, subVOs);
		}

		return vo;
	}

	// MOD {̨�������Ա�Ӱ�8Сʱ��ϢһСʱ���Ӱ�12Сʱ��Ϣ2Сʱ�ۼ�60.120����} kevin.nie 2017-09-25
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
	// {̨�������Ա�Ӱ�8Сʱ��ϢһСʱ���Ӱ�12Сʱ��Ϣ2Сʱ�ۼ�60.120����} kevin.nie 2017-09-25 end

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
	// //�û������˿�ʼʱ�䣬���Լ���Ĭ�ϵĽ���ʱ��
	// OvertimebVO cloneVO =
	// psncalendarService.calculatePsnDefaultOvertimeBeginEndTime(subvo,
	// clientTimeZone);
	// subvo.setOvertimebegintime(cloneVO.getOvertimebegintime());
	// subvo.setOvertimeendtime(cloneVO.getOvertimeendtime());
	// }
	//
	// //����ʱ��
	// BillProcessHelperAtServer.calOvertimeLength(vo);
	// String pk_org = vo.getOvertimehVO().getPk_org();
	// //ȡ��ǰ��֯���а����Ϣ
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
	// //�޸ļӰ���Ϣʱ��Ҫ�����Ƿ���ҪУ��
	// if(subVO.getIsneedcheck().booleanValue()&&!impl.isCanCheck(subVO,
	// timeruleVO, aggShiftMap, shiftMap, calendarMap, dateOrgMap, periodMap))
	// subVO.setIsneedcheck(UFBoolean.FALSE);
	// }
	// return vo;
	// }
	

	public Integer GetDateType(UFLiteralDate beiginTime, String pkPsndoc, String pkOrg ) throws BusinessException
	{
		if(beiginTime ==null)
		{
			return 0;
		}
		StringBuilder sql = new StringBuilder();
	 	sql.append(" select case when tbm_psncalendar.if_rest='Y' or"
	 			+ " exists (select pk_holiday from org_orgs ");
		sql.append(" inner join bd_workcalendar on org_orgs.workcalendar=bd_workcalendar.pk_workcalendar ");
		sql.append(" inner join bd_holiday on bd_workcalendar.pk_holidayrule=bd_holiday.pk_holidaysort ");
		sql.append(" where org_orgs.pk_org=tbm_psncalendar.pk_org ");
		sql.append(" and bd_holiday.starttime<='"+beiginTime.toString()+"' and endtime>='"+beiginTime.toString()+"' ");
		sql.append(" ) then 1 ");
		sql.append(" else  0 end as datetype from tbm_psncalendar"); 
		sql.append(" left join tbm_psncalholiday on tbm_psncalendar.pk_psncalendar=tbm_psncalholiday.pk_psncalendar");
		sql.append("  where  pk_psndoc='"+pkPsndoc+"'");
		sql.append(" and tbm_psncalendar.pk_org='"+pkOrg+"'");
		sql.append(" and tbm_psncalendar.calendar='"+beiginTime.toString().subSequence(0, 10)+"'");
		
		Integer dateType = (Integer) new BaseDAO().executeQuery(sql.toString(), new ColumnProcessor());
		
		return dateType;
		
	}
}
