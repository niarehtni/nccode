package nc.impl.ta.overtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hr.wa.IGlobalCountryQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DataFilterUtils;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.ICheckTime;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.SolidifyUtils;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.jdbc.framework.SQLParameter;
import nc.vo.bd.countryzone.CountryZoneVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.org.OrgQueryUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.algorithm.SolidifyPara;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.importdata.ImportDataVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeGenVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.shutdown.ShutdownRegVO;
import nc.vo.ta.signcard.SignRegVO;
import nc.vo.ta.timebill.annotation.BillBeginDateFieldName;
import nc.vo.ta.timebill.annotation.BillEndDateFieldName;
import nc.vo.ta.timebill.annotation.PkPsndocFieldName;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class OvertimeDAO {
	
	/**
	 * 设置考勤期间已加班时长和校验标识
	 * @param setCheckFlag 是否需要重新设置校验标志
	 * @param vos
	 * @throws BusinessException
	 */
	public void setAlreadyHourAndCheckFlag(boolean setCheckFlag,OvertimeCommonVO... vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return;
		String pk_org = vos[0].getPk_org();
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		Map<String, AggShiftVO> aggShiftMap = vos[0].getAggShiftMap();
//		if(aggShiftMap == null){
//			aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//			vos[0].setAggShiftMap(aggShiftMap);
//		}
		Map<UFLiteralDate, PeriodVO> periodMap = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScopes(pk_org, scopes);
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = null;
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = null;
		InSQLCreator isc = new InSQLCreator();
		try{
			String psndocInSQL = isc.getInSQL(vos, OvertimeRegVO.PK_PSNDOC);
			psnDateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
			psnCalendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
		} finally {
			isc.clear();
		}
		// 先计算考勤期间已加班时长
		setAlreadyHour(vos, timeRule, aggShiftMap, periodMap, timeitemMap, psnDateOrgMap, psnCalendarMap);
		if(!setCheckFlag)
			return;
		// 再设置校验标识
		Map<OvertimeCommonVO, Boolean> checkMap = getOvertimeCheckMap(vos, timeRule, aggShiftMap, periodMap, timeitemMap, psnDateOrgMap, psnCalendarMap);
		for(OvertimeCommonVO vo:vos){
			//由加班生成或申请过来登记单的不允许校验
			if(vo instanceof OvertimeRegVO){
				OvertimeRegVO regvo = (OvertimeRegVO) vo;
				if(regvo.getBillsource()!=null && ICommonConst.BILL_SOURCE_REG!=regvo.getBillsource())
					continue;
			}
			// 如果本来是不允许校验，不需要处理  v63修改，若是可以校验的， 默认设置为需要校验
//			if(!vo.getIsneedcheck().booleanValue())
//				continue;
			if(checkMap.get(vo)!=null && checkMap.get(vo)){
				vo.setIsneedcheck(UFBoolean.TRUE);
				continue;
			}
			vo.setIsneedcheck(UFBoolean.FALSE);
		}
	}
	
	/**
	 * 计算所在考勤期间已加班时长
	 * @param vos
	 * @throws BusinessException
	 */
	public void setAlreadyHour(OvertimeCommonVO... vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return;
		String pk_org = vos[0].getPk_org();
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<UFLiteralDate, PeriodVO> periodMap = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScopes(pk_org, scopes);
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = null;
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = null;
		InSQLCreator isc = new InSQLCreator();
		try{
			String psndocInSQL = isc.getInSQL(vos, OvertimeRegVO.PK_PSNDOC);
			psnDateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
			psnCalendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
		} finally {
			isc.clear();
		}
		setAlreadyHour(vos, timeRule, aggShiftMap, periodMap, timeitemMap, psnDateOrgMap, psnCalendarMap);
	}
	
	/**
	 * 计算所在考勤期间已加班时长
	 * @param vos
	 * @param timeRule
	 * @param shiftMap
	 * @param periodMap
	 * @param timeitemMap
	 * @param psnDateOrgMap
	 * @param psnCalendarMap
	 * @throws BusinessException
	 */
	public void setAlreadyHour(OvertimeCommonVO[] vos, TimeRuleVO timeRule,
			Map<String, AggShiftVO> aggShiftMap, Map<UFLiteralDate, PeriodVO> periodMap, 
			Map<String, OverTimeTypeCopyVO> timeitemMap, Map<String, Map<UFLiteralDate, String>> psnDateOrgMap, 
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return;
		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
		Map<OvertimeCommonVO, OvertimeCommonVO[]> belongMap = getOvertimeVOInPeriod(vos, timeRule.getTimeZoneMap(), shiftMap, psnDateOrgMap, psnCalendarMap, periodMap);
		for(OvertimeCommonVO vo:vos){
			UFDouble alreadyHour = UFDouble.ZERO_DBL;
			OvertimeCommonVO[] belongVOs = belongMap.get(vo);
			if(ArrayUtils.isEmpty(belongVOs)){
				vo.setOvertimealready(alreadyHour);
				continue;
			}
			for(OvertimeCommonVO belongVO:belongVOs){
				if(TimeItemCopyVO.TIMEITEMUNIT_DAY==timeitemMap.get(belongVO.getPk_overtimetype()).getTimeitemunit().intValue()){
					alreadyHour = alreadyHour.add(belongVO.getActhour().multiply(timeRule.getDaytohour2()));
					continue;
				}
				alreadyHour = alreadyHour.add(belongVO.getActhour());
			}
			vo.setOvertimealready(alreadyHour);
		}
	}
	
	private boolean isTWOrg(String pk_org) throws BusinessException {
		OrgVO[] orgVOs = OrgQueryUtil.queryOrgVOByPks(new String[] { pk_org });
		IGlobalCountryQueryService czQry = NCLocator.getInstance().lookup(IGlobalCountryQueryService.class);
		CountryZoneVO czVO = czQry.getCountryZoneByPK(orgVOs[0].getCountryzone());
		return czVO.getCode().equals("TW");
	}
	/**
	 * 取归属日所在考勤期间内所有有效单据
	 * 包括申请单（自由态、提交态和进行中态）+ 生成单（未生效）+ 登记单
	 * @param vos
	 * @param shiftMap
	 * @param dateTimeZoneMap
	 * @param calendarMap
	 * @param periodMap
	 * @return Map<人员主键+考勤期间, 有效单据>
	 * @throws BusinessException
	 */
	public Map<OvertimeCommonVO, OvertimeCommonVO[]> getOvertimeVOInPeriod(
			OvertimeCommonVO[] vos,
			Map<String, TimeZone> timeZoneMap,
			Map<String, ShiftVO> shiftMap, 
			Map<String, Map<UFLiteralDate, String>> psnDateOrgMap,
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap,
			Map<UFLiteralDate, PeriodVO> periodMap) throws BusinessException {
		Map<OvertimeCommonVO, OvertimeCommonVO[]> resultMap = new HashMap<OvertimeCommonVO, OvertimeCommonVO[]>();
		// 取归属日 Map直接用vo作key
		Map<OvertimeCommonVO, PeriodVO> belongDateMap = new HashMap<OvertimeCommonVO, PeriodVO>();
		if(ArrayUtils.isEmpty(vos))
			return resultMap;
		
		boolean isTWOrg=isTWOrg(vos[0].getPk_org());
		//MOD 更新期间为自然月 James
		if (isTWOrg) {
			periodMap.clear();
			for(OvertimeCommonVO vo:vos){
					PeriodVO pVO=new PeriodVO();
					pVO.setPk_org(vo.getPk_org());
					pVO.setTimeyear(Integer.toString(vo.getBegindate().getYear()));
					pVO.setTimemonth(Integer.toString(vo.getBegindate().getMonth()));
					pVO.setBegindate(vo.getBegindate().getDateBefore(vo.getBegindate().getDay()-1));
					pVO.setEnddate(vo.getBegindate().getDateAfter(vo.getBegindate().getDaysMonth()-vo.getBegindate().getDay()));
					if(!periodMap.containsKey(vo.getBegindate().getDateBefore(vo.getBegindate().getDay()-1))){
						periodMap.put(vo.getBegindate().getDateBefore(vo.getBegindate().getDay()-1), pVO);
				}
			}	
		}		
		
		// 主键(查询时排除原单据)
		List<String> pkList = new ArrayList<String>();
		Set<PeriodVO> periods = new HashSet<PeriodVO>();
		for(OvertimeCommonVO vo:vos){
			// 有主键时记录主键
			if(StringUtils.isNotEmpty(vo.getPrimaryKey()))
				pkList.add(vo.getPrimaryKey());
			Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(vo.getPk_psndoc());
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(vo.getPk_psndoc());
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap= CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
			//MOD 更新期间为自然月 James
			if(isTWOrg){
				UFLiteralDate belongDate = vo.getBegindate().getDateBefore(vo.getBegindate().getDay()-1);
				if(belongDate==null)
					continue;
				PeriodVO period = periodMap==null?null:periodMap.get(belongDate);
				if(period==null)
					continue;
				periods.add(period);
				belongDateMap.put(vo, period);
			}
			else{
				UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap, dateTimeZoneMap);
				if(belongDate==null)
					continue;
				PeriodVO period = periodMap==null?null:periodMap.get(belongDate);
				if(period==null)
					continue;
				periods.add(period);
				belongDateMap.put(vo, period);
			}
		}
		// 如果都没有归属日
		if(MapUtils.isEmpty(belongDateMap))
			return resultMap;
		OvertimeCommonVO[] belongVOs = queryRalationOvertimeVO(vos[0].getPk_org(), StringPiecer.getStrArrayDistinct(vos, OvertimeCommonVO.PK_PSNDOC), CollectionUtils.isEmpty(pkList)?null:pkList.toArray(new String[0]), periods);
		if(ArrayUtils.isEmpty(belongVOs))
			return resultMap;
		// 将查询到的单据按期间构造Map
		Map<PeriodVO, List<OvertimeCommonVO>> belongMap = new HashMap<PeriodVO, List<OvertimeCommonVO>>();
		for(OvertimeCommonVO belongVO:belongVOs){
			Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(belongVO.getPk_psndoc());
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(belongVO.getPk_psndoc());
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap= CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
			
			//MOD 更新期间为自然月 James
			if(isTWOrg){		
				
				UFLiteralDate belongDate = belongVO.getBegindate().getDateBefore(belongVO.getBegindate().getDay()-1);
				if(belongDate==null)
					continue;
				PeriodVO period = periodMap==null?null:periodMap.get(belongDate);
				if(period==null)
					continue;
				List<OvertimeCommonVO> belongList = belongMap.get(period);
				if(belongList==null)
					belongList = new ArrayList<OvertimeCommonVO>();
				belongList.add(belongVO);
				belongMap.put(period, belongList);
			}else{
			
				UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(belongVO, calendarMap, shiftMap, dateTimeZoneMap);
				if(belongDate==null)
					continue;
				PeriodVO period = periodMap==null?null:periodMap.get(belongDate);
				if(period==null)
					continue;
				List<OvertimeCommonVO> belongList = belongMap.get(period);
				if(belongList==null)
					belongList = new ArrayList<OvertimeCommonVO>();
				belongList.add(belongVO);
				belongMap.put(period, belongList);
			}
		}
		// 处理结果
		for(OvertimeCommonVO vo:vos){
			PeriodVO period = belongDateMap.get(vo);
			if(period==null)
				continue;
			List<OvertimeCommonVO> belongList = belongMap.get(period);
			if(CollectionUtils.isEmpty(belongList))
				continue;
			// 过滤掉不是本人的单据
			List<OvertimeCommonVO> resultList = new ArrayList<OvertimeCommonVO>();
			for(OvertimeCommonVO belongVO:belongList){
				if(!StringUtils.equals(vo.getPk_psndoc(), belongVO.getPk_psndoc()))
					continue;
				resultList.add(belongVO);
			}
			if(CollectionUtils.isEmpty(resultList))
				continue;
			resultMap.put(vo, resultList.toArray(new OvertimeCommonVO[0]));
		}
		return resultMap;
	}
	
	/**
	 * 查询归属日所在考勤期间内所有有效单据
	 * 包括申请单（自由态、提交态和进行中态）+ 生成单（未生效）+ 登记单
	 * @param pk_org
	 * @param pk_psndocs
	 * @param notIncludePks 不考虑的单据主键
	 * @param periods
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private OvertimeCommonVO[] queryRalationOvertimeVO(String pk_org, String[] pk_psndocs, String[] notIncludePks, Set<PeriodVO> periods) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		try {
			String psndocInSql = isc.getInSQL(pk_psndocs);
			String notInPkSql  = isc.getInSQL(notIncludePks);
			// 申请单条件
			String appCond = IBaseServiceConst.PK_ORG_FIELD+"=? "+(StringUtils.isEmpty(notInPkSql)?"":("and pk_overtimeb not in ("+notInPkSql+") "))+" and pk_overtimeh in (select pk_overtimeh from " + 
				OvertimehVO.getDefaultTableName() + " where pk_psndoc in ("+psndocInSql+") and "+OvertimehVO.APPROVE_STATE+" in (?,?,?))";
			SQLParameter appParam = new SQLParameter();
			appParam.addParam(pk_org);
			appParam.addParam(IPfRetCheckInfo.NOSTATE);
			appParam.addParam(IPfRetCheckInfo.COMMIT);
			appParam.addParam(IPfRetCheckInfo.GOINGON);
			// 登记单条件
			String regCond = IBaseServiceConst.PK_ORG_FIELD+"=? "+(StringUtils.isEmpty(notInPkSql)?"":("and pk_overtimereg not in ("+notInPkSql+") "))+" and " + OvertimeCommonVO.PK_PSNDOC + " in ("+psndocInSql+")";
			SQLParameter regParam = new SQLParameter();
			regParam.addParam(pk_org);
			// 生成单条件
			String genCond = IBaseServiceConst.PK_ORG_FIELD+"=? "+(StringUtils.isEmpty(notInPkSql)?"":("and pk_overtimegen not in ("+notInPkSql+") "))+" and " + OvertimeCommonVO.PK_PSNDOC + " in ("+psndocInSql+") and " + OvertimeGenVO.ISEFFECT + "=? ";
			SQLParameter genParam = new SQLParameter();
			genParam.addParam(pk_org);
			genParam.addParam(UFBoolean.FALSE);
			// 日期条件
			StringBuffer dateCond = new StringBuffer(" and ( 1=2 ");
			for(PeriodVO period:periods){
				dateCond.append(" or (" + OvertimeCommonVO.OVERTIMEENDDATE + ">=? and " + OvertimeCommonVO.OVERTIMEBEGINDATE + "<=?) ");
				// 取出考勤期间3天前至3天后的所有单据
				UFLiteralDate beginDate = period.getBegindate().getDateBefore(3);
				UFLiteralDate endDate = period.getEnddate().getDateAfter(3);
				appParam.addParam(beginDate);
				appParam.addParam(endDate);
				regParam.addParam(beginDate);
				regParam.addParam(endDate);
				genParam.addParam(beginDate);
				genParam.addParam(endDate);
			}
			dateCond.append(")");
			// 查询申请单，生成单和登记单
			BaseDAO dao = new BaseDAO();
			Collection<OvertimeRegVO> reglist = dao.retrieveByClause(OvertimeRegVO.class, regCond + dateCond.toString(), regParam);
			Collection<OvertimebVO> applist = dao.retrieveByClause(OvertimebVO.class, appCond + dateCond.toString(), appParam);
			Collection<OvertimeRegVO> genlist = dao.retrieveByClause(OvertimeGenVO.class, genCond + dateCond.toString(), genParam);
			List<OvertimeCommonVO> vlist = new ArrayList<OvertimeCommonVO>();
			if(CollectionUtils.isNotEmpty(reglist))
				vlist.addAll(reglist);
			if(CollectionUtils.isNotEmpty(applist)){
				//需要查询主表上的pk_timeitem
				String inSQL = isc.getInSQL(applist.toArray(new OvertimebVO[0]), "pk_overtimeh");
				OvertimehVO[] hvos = CommonUtils.retrieveByClause(OvertimehVO.class, OvertimehVO.PK_OVERTIMEH+" in("+inSQL+")");
				Map<String, OvertimehVO> hMap = CommonUtils.toMap(OvertimehVO.PK_OVERTIMEH, hvos);
				for(OvertimebVO bvo:applist){
					OvertimehVO hvo = hMap.get(bvo.getPk_overtimeh());
					bvo.setPk_overtimetype(hvo.getPk_overtimetype());
					bvo.setPk_overtimetypecopy(hvo.getPk_overtimetypecopy());
					bvo.setPk_psndoc(hvo.getPk_psndoc());
					bvo.setPk_psnjob(hvo.getPk_psnjob());
					bvo.setPk_psnorg(hvo.getPk_psnorg());
				}
				vlist.addAll(applist);
			}
			if(CollectionUtils.isNotEmpty(genlist))
				vlist.addAll(genlist);
			return CollectionUtils.isEmpty(vlist)?null:vlist.toArray(new OvertimeCommonVO[0]);
		} finally {
			isc.clear();
		}
	}
	
	/**
	 * 加班单据是否允许校验
	 * @param vos
	 * @throws BusinessException
	 */
	public void getOvertimeCheckMap(OvertimeCommonVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return;
		String pk_org = vos[0].getPk_org();
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<UFLiteralDate, PeriodVO> periodMap = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScopes(pk_org, scopes);
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = null;
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = null;
		InSQLCreator isc = new InSQLCreator();
		try{
			String psndocInSQL = isc.getInSQL(vos, OvertimeRegVO.PK_PSNDOC);
			psnDateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
			psnCalendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
		} finally {
			isc.clear();
		}
		getOvertimeCheckMap(vos, timeRule, aggShiftMap, periodMap, timeitemMap, psnDateOrgMap, psnCalendarMap);
	}
	
	public Map<OvertimeCommonVO, Boolean> getOvertimeCheckMap(OvertimeCommonVO[] vos, TimeRuleVO timeRule,
			Map<String, AggShiftVO> aggShiftMap, Map<UFLiteralDate, PeriodVO> periodMap, 
			Map<String, OverTimeTypeCopyVO> timeitemMap, Map<String, Map<UFLiteralDate, String>> psnDateOrgMap, 
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap) throws BusinessException {
		Map<OvertimeCommonVO, Boolean> resultMap = new HashMap<OvertimeCommonVO, Boolean>();
		List<SolidifyParamVO> solidifyList = new ArrayList<SolidifyParamVO>();
		Set<String> psndocList = new HashSet<String>();
		List<IDateScope> dateList = new ArrayList<IDateScope>();
		if(ArrayUtils.isEmpty(vos))
			return resultMap;
		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
		
		//V63修改，假日排班的工作时间与加班单有交集是可以校验的，因此此时的工作时间应该扣除假日时间
		//获取单据时间段内享有的假日且假日排班记为加班的信息
		Map<String, HRHolidayVO[]> overTimeHolidayScope = BillProcessHelperAtServer.getOverTimeHolidayScope(vos);
		
		//查询考勤档案
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocMapByPsndocs(null, StringPiecer.getStrArrayDistinct(vos, OvertimeCommonVO.PK_PSNDOC),
				maxDateScope.getBegindate(), maxDateScope.getEnddate(), true, true, null);
		for(OvertimeCommonVO vo:vos){
			//获取开始时间
			UFDateTime beginTime = vo.getOvertimebegintime();
			//获取结束时间
			UFDateTime endTime = vo.getOvertimeendtime();
			if(beginTime==null||endTime==null||beginTime.after(endTime)){
				resultMap.put(vo, false);
				continue;
			}
			// 先通过结束日期找考勤档案
			Map<String, List<TBMPsndocVO>> tmpPsndocMap = CommonMethods.filterTBMPsndocByOrg(vo.getPk_org(), psndocMap);
			List<TBMPsndocVO> psnList = tmpPsndocMap==null?null:tmpPsndocMap.get(vo.getPk_psndoc());
			TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psnList, vo.getEnddate().toStdString());
			if(psndocVO == null){
				// 如果结束日期没找到归属的考勤档案，则通过开始日期再找一次
				psndocVO = TBMPsndocVO.findIntersectionVO(psnList, vo.getBegindate().toStdString());
			}
			if(psndocVO == null || TBMPsndocVO.TBM_PROP_MANUAL == psndocVO.getTbm_prop()){
				resultMap.put(vo, false);
				continue;
			}
			//计算加班单据归属日
			Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(vo.getPk_psndoc());
			if(MapUtils.isEmpty(calendarMap)){
				resultMap.put(vo, false);
				continue;
			}
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(vo.getPk_psndoc());
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap= CommonMethods.createDateTimeZoneMap(dateOrgMap, timeRule.getTimeZoneMap());
			UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap,dateTimeZoneMap);
			// 查出归属日所在考勤期间
			PeriodVO period = periodMap==null?null:periodMap.get(belongDate);
			// 如果考勤期间已封存，则不允许校验
			if(period==null || period.isSeal()){
				resultMap.put(vo, false);
				continue;
			}
			// 没有设置工作日历，不允许校验
			AggPsnCalendar calendarVO = calendarMap.get(belongDate);
			if(calendarVO==null){
				resultMap.put(vo, false);
				continue;
			}
			
			//考勤时间段--如果是工作日，就是刷卡开始到：
			//后一日是工作日，则后一日的考勤开始
			//后一日是公休日，则看当日刷卡结束是否到了第二天，如果到了，就到刷卡结束，否则到当日24点
			//           如果是公休日，那么考勤时间段为连续公休的时间段
			ITimeScope kqScope = CommonMethods.getKQScope4OvertimeCheckAndGen(belongDate, 5, shiftMap, calendarMap, dateTimeZoneMap);
			// 加班绝对时间段
			ITimeScope voScope = new DefaultTimeScope(beginTime,endTime);
			//如果加班时间段超出了考勤时间段，则不允许校验
			if(!TimeScopeUtils.contains(kqScope, voScope)){
				resultMap.put(vo, false);
				continue;
			}
			// 如果是弹性班，则需要固化
			if(calendarVO.getPsnCalendarVO().isFlexibleFinal()){
				psndocList.add(vo.getPk_psndoc());
				dateList.add(new DefaultDateScope(belongDate.getDateBefore(2), belongDate.getDateAfter(2)));
				SolidifyParamVO param = new SolidifyParamVO();
				param.vo = vo;
				param.belongDate = belongDate;
				param.calendarMap = calendarMap;
				param.dateTimeZoneMap = dateTimeZoneMap;
				solidifyList.add(param);
				continue;
			}
			// 与工作时间段有交集则不允许校验
			PsnWorkTimeVO[] workTimevos = calendarVO.getPsnWorkTimeVO();
			//根据港华和深圳万华的需求，平常工间休加班要记加班时长,且不需要校验，假日排班的工间休不计加班时长
			List<ITimeScope> rtScopeList = new ArrayList<ITimeScope>();
			if(!ArrayUtils.isEmpty(workTimevos)){
				TimeScopeUtils.sort(workTimevos);
				for(int i=1;i<workTimevos.length;i++){
					ITimeScope rtscope = new DefaultTimeScope();
					rtscope.setScope_start_datetime(workTimevos[i-1].getScope_end_datetime());
					rtscope.setScope_end_datetime(workTimevos[i].getScope_start_datetime());
					rtScopeList.add(rtscope);
				}
			}
			//V63修改，假日排班的工作时间与加班单有交集是可以校验的，因此此时的工作时间应该扣除假日时间
			if(!overTimeHolidayScope.isEmpty()&&TimeScopeUtils.isCross(overTimeHolidayScope.get(vo.getPk_psndoc()), voScope)){
				HRHolidayVO[] hrHolidayVOs = overTimeHolidayScope.get(vo.getPk_psndoc());
				if(!ArrayUtils.isEmpty(hrHolidayVOs)){
					ITimeScope[] workScopes = TimeScopeUtils.minusTimeScopes(workTimevos, hrHolidayVOs);
					if(!CollectionUtils.isEmpty(rtScopeList)){
						ITimeScope[] rtScopes = TimeScopeUtils.minusTimeScopes(rtScopeList.toArray(new ITimeScope[0]), hrHolidayVOs);
						resultMap.put(vo, !TimeScopeUtils.isCross(workScopes, voScope)&&!TimeScopeUtils.isCross(rtScopes, voScope));
						continue;
					}
					resultMap.put(vo, !TimeScopeUtils.isCross(workScopes, voScope));
					continue;
				}
			}
			//65修改，若与工间休时间段有冲入也不能校验
			if(CollectionUtils.isNotEmpty(rtScopeList)&&TimeScopeUtils.isCross(rtScopeList.toArray(new ITimeScope[0]), voScope)){
				resultMap.put(vo, false);
				continue;
			}
			resultMap.put(vo, !TimeScopeUtils.isCross(workTimevos, voScope));
		}
		// 单独处理弹性班
		if(CollectionUtils.isEmpty(solidifyList))
			return resultMap;
		String pk_org = vos[0].getPk_org();
		String[] pk_psndocs = psndocList.toArray(new String[0]);
		IDateScope[] dateScopes = DateScopeUtils.mergeDateScopes(dateList.toArray(new IDateScope[0]));
		// 查询所有相关单据
		Map<String, LeaveRegVO[]> allLeaveBills = queryAllSuperVOIncEffectiveByPsnsDateScope(LeaveRegVO.class, pk_org, pk_psndocs, dateScopes, null);
		Map<String, AwayRegVO[]> allAwayBills = queryAllSuperVOIncEffectiveByPsnsDateScope(AwayRegVO.class, pk_org, pk_psndocs, dateScopes, null);
		Map<String, ShutdownRegVO[]> allShutdownBills = queryAllSuperVOIncEffectiveByPsnsDateScope(ShutdownRegVO.class, pk_org, pk_psndocs, dateScopes, null);
		Map<String, LeaveRegVO[]> allLactationBills = queryAllSuperVOIncEffectiveByPsnsDateScope(LeaveRegVO.class, pk_org, pk_psndocs, dateScopes, " and islactation = 'Y' ");
		Map<String, ICheckTime[]> allCheckBills = queryCheckTimeMapByPsndocInSQLAndDateScope(pk_org, pk_psndocs, dateScopes);
		// 处理需要固化的
		for(SolidifyParamVO param:solidifyList) {
			// 因为要实现批量查询，所以不调用SolidifyParaUtils构造，改为自己构造
			SolidifyPara solidifyPara = new SolidifyPara();
			//构造考勤时间段
			UFLiteralDate preDate=param.belongDate.getDateBefore(1);
			UFLiteralDate nextDate=param.belongDate.getDateAfter(1);
			AggPsnCalendar calendarVO = param.calendarMap.get(param.belongDate);
			AggPsnCalendar preCalendarVO = param.calendarMap.get(preDate);
			AggPsnCalendar nextCalendarVO = param.calendarMap.get(nextDate);
			
			ShiftVO preShift = preCalendarVO == null ? null : preCalendarVO.getPsnCalendarVO() == null ? null : shiftMap.get(preCalendarVO.getPsnCalendarVO().getPk_shift());
			ShiftVO curShift = calendarVO == null ? null : calendarVO.getPsnCalendarVO() == null ? null : shiftMap.get(calendarVO.getPsnCalendarVO().getPk_shift());
			ShiftVO nextShift = nextCalendarVO == null ? null : nextCalendarVO.getPsnCalendarVO() == null ? null : shiftMap.get(nextCalendarVO.getPsnCalendarVO().getPk_shift());
			TimeZone curTimeZone = CommonUtils.ensureTimeZone(param.dateTimeZoneMap.get(param.belongDate));
			TimeZone preTimeZone = CommonUtils.ensureTimeZone(param.dateTimeZoneMap.get(preDate));
			TimeZone nextTimeZone = CommonUtils.ensureTimeZone(param.dateTimeZoneMap.get(nextDate));
			//考勤时间段
			ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift, nextShift, param.belongDate.toString(), curTimeZone,preTimeZone,nextTimeZone);
			solidifyPara.timeruleVO = timeRule;
			solidifyPara.date = param.belongDate;
			solidifyPara.calendarVO = calendarVO;
			solidifyPara.shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(aggShiftMap, calendarVO.getPsnCalendarVO().getPk_shift());
			solidifyPara.timeZone=curTimeZone;
			solidifyPara.leaveBills = allLeaveBills==null?null:DataFilterUtils.filterRegVOs(kqScope, allLeaveBills.get(param.vo.getPk_psndoc()));
			solidifyPara.awayBills = allAwayBills==null?null:DataFilterUtils.filterRegVOs(kqScope, allAwayBills.get(param.vo.getPk_psndoc()));
			solidifyPara.shutdownBills = allShutdownBills==null?null:DataFilterUtils.filterRegVOs(kqScope, allShutdownBills.get(param.vo.getPk_psndoc()));
			solidifyPara.mergeLASScopes = TimeScopeUtils.mergeTimeScopes(solidifyPara.awayBills, TimeScopeUtils.mergeTimeScopes(solidifyPara.shutdownBills,solidifyPara.leaveBills));
			solidifyPara.lactationholidayVO = allLactationBills==null?null:DataFilterUtils.filterDateScopeVO(param.belongDate.toString(), allLactationBills.get(param.vo.getPk_psndoc()));
			solidifyPara.checkTimes = allCheckBills==null?null:DataFilterUtils.filterCheckTimes(kqScope, allCheckBills.get(param.vo.getPk_psndoc()));
			// 固化
			PsnWorkTimeVO[] workTimeVOs = SolidifyUtils.solidify(solidifyPara);
			
			//根据港华和深圳万华的需求，平常工间休加班要记加班时长,且不需要校验，假日排班的工间休不计加班时长
			List<ITimeScope> rtScopeList = new ArrayList<ITimeScope>();
			if(!ArrayUtils.isEmpty(workTimeVOs)){
				TimeScopeUtils.sort(workTimeVOs);
				for(int i=1;i<workTimeVOs.length;i++){
					ITimeScope rtscope = new DefaultTimeScope();
					rtscope.setScope_start_datetime(workTimeVOs[i-1].getScope_end_datetime());
					rtscope.setScope_end_datetime(workTimeVOs[i].getScope_start_datetime());
					rtScopeList.add(rtscope);
				}
			}
			
			//V63修改，假日排班的工作时间与加班单有交集是可以校验的，因此此时的工作时间应该扣除假日时间
			if(!overTimeHolidayScope.isEmpty()){
				HRHolidayVO[] hrHolidayVOs = overTimeHolidayScope.get(param.vo.getPk_psndoc());
				if(!ArrayUtils.isEmpty(hrHolidayVOs)){
					ITimeScope[] workScopes = TimeScopeUtils.minusTimeScopes(workTimeVOs, hrHolidayVOs);
					if(!CollectionUtils.isEmpty(rtScopeList)){
						ITimeScope[] rtScopes = TimeScopeUtils.minusTimeScopes(rtScopeList.toArray(new ITimeScope[0]), hrHolidayVOs);
						resultMap.put(param.vo, !TimeScopeUtils.isCross(workScopes, param.vo)&&!TimeScopeUtils.isCross(rtScopes, param.vo));
						continue;
					}
					resultMap.put(param.vo, !TimeScopeUtils.isCross(workScopes, param.vo));
					continue;
				}
			}
			//65修改，若与工间休时间段有冲入也不能校验
			if(CollectionUtils.isNotEmpty(rtScopeList)&&TimeScopeUtils.isCross(rtScopeList.toArray(new ITimeScope[0]), param.vo)){
				resultMap.put(param.vo, false);
				continue;
			}
			// 与工作时间段有交集则不允许校验
			resultMap.put(param.vo, !TimeScopeUtils.isCross(workTimeVOs, param.vo));
		}
		return resultMap;
	}
	
	/**
	 * 弹性参数
	 * @author yucheng
	 *
	 */
	private class SolidifyParamVO{
		public OvertimeCommonVO vo;
		public UFLiteralDate belongDate;
		public Map<UFLiteralDate, AggPsnCalendar> calendarMap;
		public Map<UFLiteralDate, TimeZone> dateTimeZoneMap;
	}
	
//	private Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsnsDateScope(String pk_org,
//			String[] pk_psndocs, IDateScope[] dateScopes) throws BusinessException {
//		InSQLCreator isc = new InSQLCreator();
//		try{
//			String cond = LeaveRegVO.PK_ORG+"=? and "+LeaveRegVO.PK_PSNDOC+" in ("+isc.getInSQL(pk_psndocs)+") ";
//			SQLParameter param = new SQLParameter();
//			param.addParam(pk_org);
//			StringBuffer dateCond = new StringBuffer(" and ( 1=2 ");
//			for(IDateScope dateScope:dateScopes){
//				dateCond.append(" or (" + LeaveRegVO.LEAVEBEGINDATE + "<=? and " + LeaveRegVO.LEAVEENDDATE + ">=?) ");
//				param.addParam(dateScope.getEnddate());
//				param.addParam(dateScope.getBegindate());
//			}
//			LeaveRegVO[] vos = TimeScopeUtils.removeZeroTimeScope(CommonUtils.retrieveByClause(LeaveRegVO.class, cond+dateCond.toString(), param));
//			return CommonUtils.group2ArrayByField(LeaveRegVO.PK_PSNDOC, vos);
//		} finally {
//			isc.clear();
//		}
//	}
	
	// TODO 暂时先放在这个类里，后续需要转移到各自模块中or CommonMethods里
	/**
	 * 查询与日期段有交集的单据
	 * @param pk_org
	 * @param pk_psndocs
	 * @param dateScopes
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private <T extends SuperVO> Map<String, T[]> queryAllSuperVOIncEffectiveByPsnsDateScope(Class<T> clz, String pk_org,
			String[] pk_psndocs, IDateScope[] dateScopes, String extraCond) throws BusinessException {
		//开始日期的字段名
		String beginDateFieldName = clz.getAnnotation(BillBeginDateFieldName.class).fieldName();
		//结束日期的字段名
		String endDateFieldName = clz.getAnnotation(BillEndDateFieldName.class).fieldName();
		//人员主键的字段名
		String pk_psndocFieldName = clz.getAnnotation(PkPsndocFieldName.class).fieldName();
		InSQLCreator isc = new InSQLCreator();
		try{
			String cond = IBaseServiceConst.PK_ORG_FIELD+"=? and "+pk_psndocFieldName+" in ("+isc.getInSQL(pk_psndocs)+")";
			SQLParameter param = new SQLParameter();
			param.addParam(pk_org);
			StringBuffer dateCond = new StringBuffer(" and ( 1=2 ");
			for(IDateScope dateScope:dateScopes){
				dateCond.append(" or (" + beginDateFieldName + "<=? and " + endDateFieldName + ">=?) ");
				param.addParam(dateScope.getEnddate());
				param.addParam(dateScope.getBegindate());
			}
			cond = cond + dateCond.toString() + ") " + (StringUtils.isEmpty(extraCond)?"":extraCond);
			T[] vos = (T[]) TimeScopeUtils.removeZeroTimeScope((ITimeScope[])CommonUtils.retrieveByClause(clz, cond, param));
			return CommonUtils.group2ArrayByField(pk_psndocFieldName, vos);
		} finally {
			isc.clear();
		}
	}
	
	/**
	 * 查询刷签卡信息
	 * @param pk_org
	 * @param pk_psndocs
	 * @param dateScopes
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, ICheckTime[]> queryCheckTimeMapByPsndocInSQLAndDateScope(
			String pk_org, String[] pk_psndocs, IDateScope[] dateScopes) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		try{
			String psndocInSql = isc.getInSQL(pk_psndocs);
			String importCond = ImportDataVO.PK_ORG+" = ? and "+ImportDataVO.PK_PSNDOC+" in("+psndocInSql+")";
			SQLParameter importParam = new SQLParameter();
			importParam.addParam(pk_org);
			String signCond   = SignRegVO.PK_ORG+" = ? and "+SignRegVO.PK_PSNDOC+" in("+psndocInSql+")";
			SQLParameter signParam = new SQLParameter();
			signParam.addParam(pk_org);
			StringBuffer importDateCond = new StringBuffer(" and ( 1=2 ");
			StringBuffer signDateCond = new StringBuffer(" and ( 1=2 ");
			for(IDateScope dateScope:dateScopes){
				importDateCond.append(" or (" + ImportDataVO.CALENDARDATE + " between ? and ?) ");
				importParam.addParam(dateScope.getBegindate());
				importParam.addParam(dateScope.getEnddate());
				signDateCond.append(" or (" + SignRegVO.SIGNTIME + " between ? and ?) ");
				signParam.addParam(new UFDateTime(dateScope.getBegindate().toStdString()+" 00:00:00",ICalendar.BASE_TIMEZONE));
				signParam.addParam(new UFDateTime(dateScope.getEnddate().toStdString()+" 23:59:59",ICalendar.BASE_TIMEZONE));
			}
			importCond = importCond + importDateCond.toString() + ") ";
			signCond = signCond + signDateCond.toString() + ") ";
			ImportDataVO[] importVOs = CommonUtils.retrieveByClause(ImportDataVO.class, importCond, importParam);
			SignRegVO[] signVOs = CommonUtils.retrieveByClause(SignRegVO.class, signCond, signParam);
			Map<String, ImportDataVO[]> importMap = CommonUtils.group2ArrayByField(ImportDataVO.PK_PSNDOC, importVOs);
			Map<String, SignRegVO[]> signMap = CommonUtils.group2ArrayByField(SignRegVO.PK_PSNDOC, signVOs);
			return CommonUtils.mergeGroupedMap(ICheckTime.class, importMap, signMap);
		} finally {
			isc.clear();
		}
	}
	
	/**
	 * 计算加班单据归属日
	 * @param vo
	 * @param shiftMap
	 * @param timeZone
	 * @return
	 * @throws BusinessException
	 */
	public UFLiteralDate getBelongDate(
			OvertimeCommonVO vo,
			Map<String, ShiftVO> shiftMap, 
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException {
		//获取开始时间
		UFDateTime beginTime = vo.getOvertimebegintime();
		//获取结束时间
		UFDateTime endTime = vo.getOvertimeendtime();
		if(beginTime==null||endTime==null||beginTime.after(endTime))
			return null;
		if(MapUtils.isEmpty(shiftMap))
			return null;
		//计算加班单据归属日 
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = 
			NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarMapByPsnDates(vo.getPk_org(), vo.getPk_psndoc(), vo.getOvertimebegindate().getDateBefore(2), vo.getOvertimeenddate().getDateAfter(2));
		String name="";
		if(MapUtils.isEmpty(calendarMap)){
			name = CommonUtils.getPsnName(vo.getPk_psndoc());
			throw new BusinessException(name+(ResHelper.getString("6017overtime","06017overtime0046")
					/*@res "在加班单据所在日期内没有考勤档案或者没有排班!"*/));
		}
		return BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap, dateTimeZoneMap);
	}
	
	/**
	 * 计算加班单据归属日
	 * @param vo
	 * @param shiftMap
	 * @param timeZone
	 * @return
	 * @throws BusinessException
	 */
	public UFLiteralDate getBelongDate(
			OvertimeCommonVO vo,
			Map<String, ShiftVO> shiftMap, 
			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap) throws BusinessException {
		//获取开始时间
		UFDateTime beginTime = vo.getOvertimebegintime();
		//获取结束时间
		UFDateTime endTime = vo.getOvertimeendtime();
		if(beginTime==null||endTime==null||beginTime.after(endTime))
			return null;
		if(MapUtils.isEmpty(shiftMap))
			return null;
		//计算加班单据归属日 
		if(MapUtils.isEmpty(calendarMap)){
			calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarMapByPsnDates(vo.getPk_org(), vo.getPk_psndoc(), vo.getOvertimebegindate().getDateBefore(2), vo.getOvertimeenddate().getDateAfter(2));
		}
		if(MapUtils.isEmpty(calendarMap)){
			return null;
		}
		return BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap, dateTimeZoneMap);
	}
}
