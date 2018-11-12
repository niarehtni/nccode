package nc.impl.ta.dataprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.framework.execute.RunnableItem;
import nc.bs.logging.Logger;
import nc.bs.uap.lock.PKLock;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hr.devitf.IDevItfQueryService;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.ta.AwayServiceFacade;
import nc.itf.ta.CheckTimeServiceFacade;
import nc.itf.ta.IAllParamsQueryService;
import nc.itf.ta.IDayStatManageService;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IImportDataManageMaintain;
import nc.itf.ta.ILateEarlyManageService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ISignCardManageService;
import nc.itf.ta.ISysCardManageService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataManageMaintain;
import nc.itf.ta.ITimeDataQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.LeaveServiceFacade;
import nc.itf.ta.OverTimeServiceFacade;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.ShutdownServiceFacade;
import nc.itf.ta.algorithm.CheckTimeUtils;
import nc.itf.ta.algorithm.CheckTimeUtilsWithCheckFlag;
import nc.itf.ta.algorithm.CompleteCheckTimeScopeUtils;
import nc.itf.ta.algorithm.DataFilterUtils;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.DateTimeUtils;
import nc.itf.ta.algorithm.ICheckTime;
import nc.itf.ta.algorithm.ICompleteCheckTimeScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.customization.ITimeDataPostProcessor;
import nc.itf.ta.customization.ITimeDataProcessor;
import nc.itf.ta.customization.ITimePointPostProcessor;
import nc.itf.ta.customization.ITimePointProcessor;
import nc.itf.ta.dailydata.IDailyRecordCreator;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.shift.SingleCardTypeEnum;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.customization.TimeDataCalParam;
import nc.vo.ta.dailydata.DailyDataUtils;
import nc.vo.ta.dailydata.DailyDataUtils.PKArrayDescriptor;
import nc.vo.ta.dataprocess.ClearDataParam;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.pub.AllParams;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.PsnInSQLDateScope;
import nc.vo.ta.pub.SQLParamWrapper;
import nc.vo.ta.shutdown.ShutdownRegVO;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignCardConst;
import nc.vo.ta.signcard.SignbVO;
import nc.vo.ta.signcard.SignhVO;
import nc.vo.ta.syscard.SysCardVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.ta.timerule.TimeDataCalRuleConfig;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uif2.LoginContext;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TimeDataMaintainImpl implements ITimeDataManageMaintain,
		ITimeDataQueryMaintain {

	@Override
	public void generate_RequiresNew(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		generate(pk_org,fromWhereSQL,beginDate,endDate);
	}
	@Override
	public void generate(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		//首先生成空数据 ，生成权限和考勤档案的维护权限一致
		if(!StringUtils.isEmpty(PubEnv.getPk_user()))//后台任务取不到用户，后台任务不加权限
			fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		//查询所有需要计算的人员
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		String[] pk_psndocs = psndocService.queryMachineDocByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if(ArrayUtils.isEmpty(pk_psndocs))//如果没有查询出人员，则直接返回
			return;
//		String[] pk_psndocs = SQLHelper.getStrArray(psndocVOs, TBMPsndocVO.PK_PSNDOC);
//		//加锁，防止两个用户同时计算一批人
//		//将这些人的日报锁住（最细应该锁到人天，但这样锁的数据量太大，折中考虑，只锁人+组织）
//		PKLock lock = PKLock.getInstance();
//		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
//		for(int i=0;i<pk_psndocs.length;i++){
//			pk_psndocsLockacble[i]= "timedata"+pk_org+pk_psndocs[i];
//		}
//		try{
//			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
//			if(!acquired)
//				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0055")
//				/*@res "他人正在生成考勤数据，请稍候再试!"*/);
//			new TimeDataServiceImpl().createTimeDataRecord(pk_org, fromWhereSQL, beginDate, endDate);
//			//然后进行计算
//			generate0(pk_org, pk_psndocs, beginDate, endDate); 
//		}
//		finally{
//			lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
//		}
		generateAll(pk_org, pk_psndocs, beginDate, endDate);
	}
	
	@Override
	public void generate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
//		if(ArrayUtils.isEmpty(pk_psndocs))
//			return;
//		//将这些人的日报锁住（最细应该锁到人天，但这样锁的数据量太大，折中考虑，只锁人+组织）
//		PKLock lock = PKLock.getInstance();
//		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
//		for(int i=0;i<pk_psndocs.length;i++){
//			pk_psndocsLockacble[i]= "timedata"+pk_org+pk_psndocs[i];
//		}
//		try{
//			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
//			if(!acquired)
//				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0055")
//						/*@res "他人正在生成考勤数据，请稍候再试!"*/);
//			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
//			new TimeDataServiceImpl().createTimeDataRecord(pk_org, fromWhereSQL, beginDate, endDate);
//			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
//			//然后进行计算
//			generate0(pk_org, pk_psndocs, beginDate, endDate);
//		}
//		finally{
//			lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
//		}
		generateAll(pk_org, pk_psndocs, beginDate, endDate);
	}
	
	/**
	 * 有的客户的人员非常多，数据生成时可能数量很大，导致内存溢出或者达到了数据库查询的限制，导致数据生成失败
	 * 在此进行分批处理，每1000人执行一次
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	private void generateAll(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate)throws BusinessException {
		if(ArrayUtils.isEmpty(pk_psndocs)||StringUtils.isBlank(pk_org)||beginDate.afterDate(endDate))
			return;
		int length = pk_psndocs.length;
		//MOD 避免走多线程，临时将500改成3000，多线程导致生成失败 James
		if(length<3000){
			generateOnce(pk_org, pk_psndocs, beginDate, endDate);
			return;
		}
		new TimeDataGenerateThreadPool().doTaskWithThread(pk_org,pk_psndocs, beginDate, endDate);
	}
	
	/**
	 * 分批执行中的一次处理方法
	 * @param pk_org
	 * @param pk_psndocs 人员不超过一千个
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	@Override
	public void generateOnce(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate)throws BusinessException {
		if(ArrayUtils.isEmpty(pk_psndocs))
			return;
		//将这些人的日报锁住（最细应该锁到人天，但这样锁的数据量太大，折中考虑，只锁人+组织）
		PKLock lock = PKLock.getInstance();
		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
		for(int i=0;i<pk_psndocs.length;i++){
			pk_psndocsLockacble[i]= "timedata"+pk_org+pk_psndocs[i];
		}
		try{
			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
			if(!acquired)
				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0055")
						/*@res "他人正在生成考勤数据，请稍候再试!"*/);
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			new TimeDataServiceImpl().createTimeDataRecord(pk_org, fromWhereSQL, beginDate, endDate);
			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
			//然后进行计算
			generate0(pk_org, pk_psndocs, beginDate, endDate);
		}
		finally{
			lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
		}
	}

	private void generate0(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		CalParam para = new CalParam();
		para.pk_org=pk_org;
		para.beginDate=beginDate;
		para.endDate=endDate;
		para.pk_psndocs = pk_psndocs;
		InSQLCreator isc = new InSQLCreator();
		try{
			String psndocInSQL = isc.getInSQL(para.pk_psndocs);
			para.timeDataMap = queryMapByPkPsndocsDate(pk_org, psndocInSQL, beginDate, endDate);
			ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
			//这些人员在日期范围内的所有考勤档案的vo，key是人员基本主键
			para.allTbmPsndocVOListMap = psndocService.queryTBMPsndocMapByPsndocs(pk_org, para.pk_psndocs, beginDate, endDate, true, true,TbmPropEnum.MACHINE_CHECK);
			//V6.1增加，这些人员的每一天的任职组织
			para.allDateOrgMap=TBMPsndocVO.createDateOrgMapByTbmPsndocVOMap(para.allTbmPsndocVOListMap, beginDate, endDate);
			//所有的刷签卡记录
			para.allCheckTimes = CheckTimeServiceFacade.queryCheckTimesByPsnsAndDateScope(pk_org, para.pk_psndocs, beginDate, endDate);
			//所有的休假记录
			para.leaveMap = LeaveServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//所有的哺乳假记录
			para.lactationMap = LeaveServiceFacade.queryAllLactationVOIncEffictiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//所有的出差记录
			para.awayMap = AwayServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//所有的加班记录
			para.overMap = OverTimeServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//所有的停工待料记录
			para.shutMap = ShutdownServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//所有的工作日历
			//para.calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1), psndocInSQL);
			para.calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQLForProcess(pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1), pk_psndocs);
		}
		finally{
			isc.clear();
		}
		//所有的公共假日记录
		IHRHolidayQueryService hrholidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		para.orgHolidayVOMap = hrholidayService.queryHolidayVOsByHROrg(pk_org, beginDate, endDate);
		if(MapUtils.isEmpty(para.orgHolidayVOMap)){
			para.holidayVOs = CommonUtils.mapVal2Array(HRHolidayVO.class, para.orgHolidayVOMap);
		}
		//所有的假日享有情况
		if(!MapUtils.isEmpty(para.orgHolidayVOMap))
			para.holidayEnjoyMap = hrholidayService.queryHolidayEnjoyInfo2(pk_org, para.pk_psndocs, para.orgHolidayVOMap);
		//所有的班次
		para.shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		para.timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryAllMap(pk_org);
		AllParams allParams = NCLocator.getInstance().lookup(IAllParamsQueryService.class).queryByOrg(pk_org);
		//考勤规则
		para.timeRuleVO=allParams.getTimeRuleVO();
		para.paraValueMap = allParams.getParValueMap();
		processSysCard(para);
		//处理外勤签到且记为全勤的数据
		processSigninOut(para);
		generateByParams(para);
		//业务日志
//		TaBusilogUtil.writeDataProcessGenerate(pk_org, pk_psndocs, beginDate, endDate);
		dataProcessLog(pk_org, pk_psndocs, beginDate, endDate);
	}
	
	/**
	 *记录日志另起线程，不影响计算的相应时间
	 * @param psnInSql
	 */
	private void dataProcessLog(final String pk_org, final String[] pk_psndocs, final UFLiteralDate beginDate,final UFLiteralDate endDate){
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				TaBusilogUtil.writeDataProcessGenerate(pk_org, pk_psndocs, beginDate, endDate);
			}
		}).start();
	}
	
	
	/**
	 * 处理移动签到外勤签到数据
	 * @param para
	 * @throws BusinessException
	 */
	private void processSigninOut(CalParam para) throws BusinessException {
		
		//移动签到不设计考勤签入签出标识
		if(para.timeRuleVO.getCheckinflag().booleanValue())
			return;
		BaseDAO baseDAO = new BaseDAO();
//		//判断是否有移动签到的表
//		String sign_sql = " select * from sysobjects where name like 'hr_signin' ";
//		GeneralVO[] cvos = (GeneralVO[]) baseDAO.executeQuery(sign_sql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
//		if(ArrayUtils.isEmpty(cvos)){//没有安装移动签到的表
//			return;
//		}
		
		String[] pk_psndocs = para.pk_psndocs; 
		UFLiteralDate beginDate = para.beginDate;
		UFLiteralDate endDate = para.endDate;
		
		//查询需要处理的外勤签到数据
		String outSql = " select pk_psndoc,recordtime from hr_signin where pk_psndoc in (" +new InSQLCreator().getInSQL(pk_psndocs)+") " +
				" and pk_org = '"+para.pk_org+"' and (recordtime between '"+beginDate.toString()+"' and '"+endDate.getDateAfter(1).toString()+"' ) " +
				" and recordtype = 'out' and approvestate = 1 and daynormal = 'Y'";
		GeneralVO[] gvos = null;
		try {
			gvos = (GeneralVO[]) baseDAO.executeQuery(outSql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		} catch (Exception e) {//可能没有启用移动签到数据
			Logger.error(e.getMessage(),e);
			return;
		}
		if(ArrayUtils.isEmpty(gvos)){//没有需要处理的外勤签到数据
			return;
		}
		
		//对签到数据 根据人员和日期分组
		Map<String,Map<String,List<UFDateTime>>> psnSignMap = new HashMap<String,Map<String,List<UFDateTime>>> ();
		for(GeneralVO gvo:gvos){
			String pk_psndoc = (String) gvo.getAttributeValue("pk_psndoc");
			String signTime = (String) gvo.getAttributeValue("recordtime");
			String sigdate = new UFLiteralDate(signTime.substring(0, 10)).toString();
			UFDateTime ufTime = new UFDateTime(signTime);
			Map<String, List<UFDateTime>> dateMap = psnSignMap.get(pk_psndoc);
			if(dateMap==null){
				dateMap = new HashMap<String, List<UFDateTime>>();
				List<UFDateTime> timeList = new ArrayList<UFDateTime>();
				timeList.add(ufTime);
				dateMap.put(sigdate, timeList);
				psnSignMap.put(pk_psndoc, dateMap);
				continue;
			}
			List<UFDateTime> timeList = dateMap.get(sigdate);
			if(timeList == null){
				timeList = new ArrayList<UFDateTime>();
				timeList.add(ufTime);
				dateMap.put(sigdate, timeList);
				continue;
			}
			timeList.add(ufTime);
		}
		
		UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
		List<SysCardVO> cardList = new ArrayList<SysCardVO>();
		for(int i=0;i<pk_psndocs.length;i++) { //按人循环
			String pk_psndoc = pk_psndocs[i];
			Map<String, List<UFDateTime>> dateMap = psnSignMap.get(pk_psndoc);
			if(MapUtils.isEmpty(dateMap)){
				continue;
			}
			for(UFLiteralDate date:dates) { //按日期循环
				List<UFDateTime> signTimeList = dateMap.get(date.toString());
				if(CollectionUtils.isEmpty(signTimeList)){
					continue;
				}
				// 没有工作日历不处理
				if(para.calendarMap==null || para.calendarMap.get(pk_psndoc)==null || para.calendarMap.get(pk_psndoc).get(date)==null)
					continue;
				AggPsnCalendar psnCalendar = para.calendarMap.get(pk_psndoc).get(date);
				AggShiftVO aggShift = ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,psnCalendar.getPsnCalendarVO().getPk_shift());
				// 没有找到班次信息不处理
				if(aggShift==null)
					continue;
				PsnWorkTimeVO[] wtVOs = psnCalendar.getPsnWorkTimeVO();
				if(ArrayUtils.isEmpty(wtVOs)) // 没有工作时间段不处理（如：假日完全覆盖）
					continue;
				
				List<TBMPsndocVO> psndocVOs = para.allTbmPsndocVOListMap.get(pk_psndoc);
				TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocVOs, date.toStdString());
				if(psndocVO==null) // 没有找到考勤档案
					continue;
				
				Map<String, TimeZone> orgTimeZoneMap = para.timeRuleVO.getTimeZoneMap();
				ICheckTime[] yuancheckTimes = para.allCheckTimes[i];
				List<SysCardVO> outSysCardList = new ArrayList<SysCardVO>();
				//把外勤签到数据加进来
				for(UFDateTime time : signTimeList){
					SysCardVO signOut = createSysCardVO(psndocVO, time,ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap);
					cardList.add(signOut);
					outSysCardList.add(signOut);
				}
				//原来的刷卡数据与外勤数据合并
				if(!ArrayUtils.isEmpty(yuancheckTimes)){
					CollectionUtils.addAll(outSysCardList, yuancheckTimes);
				}
				ICheckTime[] allCheckTimes = outSysCardList.toArray(new ICheckTime[0]);
				//对所以有效的卡排序
				Arrays.sort(allCheckTimes);
				// 取刷卡时间段内的刷卡数据
				ITimeScope kqScope = aggShift.getShiftVO().toKqScope(date.toStdString(), para.timeRuleVO.getTimeZone());
				allCheckTimes = CheckTimeUtils.filterOrderedTimesYY(allCheckTimes, kqScope.getScope_start_datetime(), kqScope.getScope_end_datetime());
				
				//对所有刷卡时间段判断是否需要补卡
				for(PsnWorkTimeVO wtvo:wtVOs){
					if(wtvo.getCheckinflag().booleanValue()){//是否需要刷上班卡
						//刷卡段是否有上班卡,没有上班卡则补卡
						ICheckTime[] Bchecktimes = CheckTimeUtils.filterOrderedTimesYY(allCheckTimes, wtvo.getKsfromtime(),wtvo.getKssj());
						if(ArrayUtils.isEmpty(Bchecktimes)){
							SysCardVO invo = createSysCardVO(psndocVO, wtvo.getKssj(),ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap);
							cardList.add(invo);
						}
					}
					if(wtvo.getCheckoutflag().booleanValue()){// 是否需呀刷下班卡
						//刷卡段是否有下班卡
						ICheckTime[] endchecktimes = CheckTimeUtils.filterOrderedTimesYY(allCheckTimes, wtvo.getJssj(),wtvo.getJstotime());
						if(ArrayUtils.isEmpty(endchecktimes)){
							SysCardVO outvo = createSysCardVO(psndocVO, wtvo.getJssj(),ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap);
							cardList.add(outvo);
						}
					}
				}
			}
		}
		InSQLCreator isc = new InSQLCreator();
		PsnInSQLDateScope psnScope = new PsnInSQLDateScope();
		psnScope.setPk_org(para.pk_org);
		psnScope.setPsndocInSQL(isc.getInSQL(pk_psndocs));
		psnScope.setBeginDate(beginDate);
		psnScope.setEndDate(endDate);
		// 保存生成的系统刷卡数据 
		SysCardVO[] sysCardVOs = NCLocator.getInstance().lookup(ISysCardManageService.class).saveSysCardVOs(cardList, psnScope);
		if(ArrayUtils.isEmpty(sysCardVOs))
			return;
		Map<String, List<SysCardVO>> sysCardMap = CommonUtils.group2ListByField(SysCardVO.PK_PSNDOC, sysCardVOs);
		// 将系统刷卡数据存入para
		for(int i=0;i<pk_psndocs.length;i++){
			List<SysCardVO> psnSysCards = sysCardMap.get(pk_psndocs[i]);
			if(CollectionUtils.isEmpty(psnSysCards))
				continue;
			ICheckTime[] oldCheckTimes = para.allCheckTimes[i];
			if(!ArrayUtils.isEmpty(oldCheckTimes)){
				CollectionUtils.addAll(psnSysCards, oldCheckTimes);
			}
			ICheckTime[] newCheckTimes = psnSysCards.toArray(new ICheckTime[0]);
			// 排序
			Arrays.sort(newCheckTimes);
			para.allCheckTimes[i] = newCheckTimes;
		}
	}
	
	/**
	 * 处理系统刷卡数据 
	 * @param para
	 * @throws BusinessException
	 */
	private void processSysCard(CalParam para) throws BusinessException {
		String[] pk_psndocs = para.pk_psndocs; 
		UFLiteralDate beginDate = para.beginDate;
		UFLiteralDate endDate = para.endDate;
		UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
		List<SysCardVO> cardList = new ArrayList<SysCardVO>();
		for(int i=0;i<pk_psndocs.length;i++) { //按人循环
			String pk_psndoc = pk_psndocs[i];
			for(UFLiteralDate date:dates) { //按日期循环
				// 没有工作日历不处理
				if(para.calendarMap==null || para.calendarMap.get(pk_psndoc)==null || para.calendarMap.get(pk_psndoc).get(date)==null)
					continue;
				AggPsnCalendar psnCalendar = para.calendarMap.get(pk_psndoc).get(date);
				AggShiftVO aggShift = ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,psnCalendar.getPsnCalendarVO().getPk_shift());
				// 没有找到班次信息或班次不支持单次刷卡不处理
				if(aggShift==null || !aggShift.getShiftVO().isSingleCard())
					continue;
				PsnWorkTimeVO[] wtVOs = psnCalendar.getPsnWorkTimeVO();
				if(ArrayUtils.isEmpty(wtVOs)) // 没有工作时间段不处理（如：假日完全覆盖）
					continue;
				UFDateTime earliesStartTime = TimeScopeUtils.getEarliesStartTime(wtVOs);
				UFDateTime latestEndTime = TimeScopeUtils.getLatestEndTime(wtVOs);
				List<TBMPsndocVO> psndocVOs = para.allTbmPsndocVOListMap.get(pk_psndoc);
				TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocVOs, date.toStdString());
				if(psndocVO==null) // 没有找到考勤档案
					continue;
				ICheckTime[] checkTimes = para.allCheckTimes[i];
				// 取刷卡时间段内的刷卡数据
				ITimeScope kqScope = aggShift.getShiftVO().toKqScope(date.toStdString(), para.timeRuleVO.getTimeZone());
				checkTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes, kqScope.getScope_start_datetime(), kqScope.getScope_end_datetime());
				if(ArrayUtils.isEmpty(checkTimes)) //没有刷签卡不处理
					continue;
				boolean isCheckinout = para.timeRuleVO.getCheckinflag().booleanValue(); //是否启用门禁
				Map<String, TimeZone> orgTimeZoneMap = para.timeRuleVO.getTimeZoneMap();
//				PsnWorkTimeVO wtVO = wtVOs[0];
				int cardType = aggShift.getShiftVO().getCardtype();
//				if(!(SingleCardTypeEnum.ONLYBEGIN.toIntValue()==cardType)) // 不是只刷上班卡，则需要生成上班卡
//					cardList.add(createSysCardVO(psndocVO, wtVO.getScope_start_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
//				if(!(SingleCardTypeEnum.ONLYEND.toIntValue()==cardType))   // 不是只刷下班卡，则需要生成下班卡
//					cardList.add(createSysCardVO(psndocVO, wtVO.getScope_end_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
				//查找所有刷签卡数据中最早签入和最晚牵扯
				ICheckTime earliesIn = null;
				ICheckTime latestOut = null;
				if(isCheckinout){
					earliesIn = CheckTimeUtilsWithCheckFlag.findOrderedEarliest(checkTimes, ICheckTime.CHECK_FLAG_IN);
					latestOut = CheckTimeUtilsWithCheckFlag.findOrderedLatest(checkTimes, ICheckTime.CHECK_FLAG_OUT);
				}else{
					earliesIn = checkTimes[0];
					latestOut = checkTimes[checkTimes.length-1];
				}
				//若只刷上班卡，则检查是否需要补下班卡
				if(SingleCardTypeEnum.ONLYBEGIN.toIntValue()==cardType){
					//缺少正常的下班卡
//					if(checkTimes[checkTimes.length-1].getDatetime().before(wtVO.getScope_end_datetime())) {
//						cardList.add(createSysCardVO(psndocVO, wtVO.getScope_end_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					if(latestOut==null||latestOut.getDatetime().before(latestEndTime)){
						cardList.add(createSysCardVO(psndocVO, latestEndTime, isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					}
				}
				//若只刷下班卡则检查是否需要补上班卡
				if(SingleCardTypeEnum.ONLYEND.toIntValue()==cardType) {
					//缺少正常的上班卡
//					if(checkTimes[0].getDatetime().after(wtVO.getScope_start_datetime())) {
//						cardList.add(createSysCardVO(psndocVO, wtVO.getScope_start_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
//					}
					if(earliesIn == null||earliesIn.getDatetime().after(earliesStartTime)) {
						cardList.add(createSysCardVO(psndocVO, earliesStartTime, isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					}
				}
				//若任意时间段刷卡，则检查是否补上、下班卡
				if(SingleCardTypeEnum.ANYTIME.toIntValue()==cardType){
					//缺少正常的上班卡
//					if(checkTimes[0].getDatetime().after(wtVO.getScope_start_datetime())) {
//						cardList.add(createSysCardVO(psndocVO, wtVO.getScope_start_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
//					}
					if(earliesIn == null||earliesIn.getDatetime().after(earliesStartTime)) {
						cardList.add(createSysCardVO(psndocVO, earliesStartTime, isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					}
					//缺少正常的下班卡
//					if(checkTimes[checkTimes.length-1].getDatetime().before(wtVO.getScope_end_datetime())) {
//						cardList.add(createSysCardVO(psndocVO, wtVO.getScope_end_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
//					}
					if(latestOut==null||latestOut.getDatetime().before(latestEndTime)){
						cardList.add(createSysCardVO(psndocVO, latestEndTime, isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					}
				}
			}
		}
		InSQLCreator isc = new InSQLCreator();
		PsnInSQLDateScope psnScope = new PsnInSQLDateScope();
		psnScope.setPk_org(para.pk_org);
		psnScope.setPsndocInSQL(isc.getInSQL(pk_psndocs));
		psnScope.setBeginDate(beginDate);
		psnScope.setEndDate(endDate);
		// 保存生成的系统刷卡数据 
		SysCardVO[] sysCardVOs = NCLocator.getInstance().lookup(ISysCardManageService.class).saveSysCardVOs(cardList, psnScope);
		if(ArrayUtils.isEmpty(sysCardVOs))
			return;
		Map<String, List<SysCardVO>> sysCardMap = CommonUtils.group2ListByField(SysCardVO.PK_PSNDOC, sysCardVOs);
		// 将系统刷卡数据存入para
		for(int i=0;i<pk_psndocs.length;i++){
			List<SysCardVO> psnSysCards = sysCardMap.get(pk_psndocs[i]);
			if(CollectionUtils.isEmpty(psnSysCards))
				continue;
			ICheckTime[] oldCheckTimes = para.allCheckTimes[i];
			CollectionUtils.addAll(psnSysCards, oldCheckTimes);
			ICheckTime[] newCheckTimes = psnSysCards.toArray(new ICheckTime[0]);
			// 排序
			Arrays.sort(newCheckTimes);
			para.allCheckTimes[i] = newCheckTimes;
		}
	}
	
	/**
	 * 生成系统刷卡数据 
	 * @param psndocVO
	 * @param time
	 * @return
	 * @throws BusinessException 
	 */
	private SysCardVO createSysCardVO(TBMPsndocVO psndocVO, UFDateTime time, int checkFlag, Map<String, TimeZone> orgTimeZoneMap) throws BusinessException{
		SysCardVO vo = new SysCardVO();
		vo.setPk_psndoc(psndocVO.getPk_psndoc());
		vo.setPk_psnjob(psndocVO.getPk_psnjob());
		vo.setPk_psnorg(psndocVO.getPk_psnorg());
		vo.setPk_group(psndocVO.getPk_group());
		vo.setPk_org(psndocVO.getPk_org());
		vo.setSigntime(time);
		vo.setSigndate(DateTimeUtils.toLiteralDate(time, orgTimeZoneMap.get(psndocVO.getPk_joborg())));
		vo.setSignstatus(checkFlag);
		vo.setPk_org_v(psndocVO.getPk_org_v());
		vo.setPk_dept_v(psndocVO.getPk_dept_v());
		return vo;
	}

	private void generateByParams(CalParam para) throws BusinessException{
		TimeRuleVO timeRuleVO = para.timeRuleVO;
		boolean isCheckFlag = timeRuleVO.isCheckinflag();
		IDevItfQueryService devItfService = NCLocator.getInstance().lookup(IDevItfQueryService.class);
		//四个计算接口：上下班时间点、时间点post、考勤数据、考勤数据post
		ITimePointProcessor timePointProcessor = (ITimePointProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEPOINT_CHECKFLAG:ICommonConst.ITF_CODE_TIMEPOINT_NOCHECKFLAG);
		ITimePointPostProcessor timePointPostProcessor =(ITimePointPostProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEPOINT_POST_CHECKFLAG:ICommonConst.ITF_CODE_TIMEPOINT_POST_NOCHECKFLAG);
		ITimeDataProcessor timeDataProcessor = (ITimeDataProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEDATA_CHECKFLAG:ICommonConst.ITF_CODE_TIMEDATA_NOCHECKFLAG);
		ITimeDataPostProcessor timeDataPostProcessor = (ITimeDataPostProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEDATA_POST_CHECKFLAG:ICommonConst.ITF_CODE_TIMEDATA_POST_NOCHECKFLAG);
		String[] pk_psndocs = para.pk_psndocs;//所有需要处理的人员

		TimeDataCalParam calParam = new TimeDataCalParam();
		calParam.timeruleVO = para.timeRuleVO;//考勤规则
		calParam.workLenMinusItemMap=TimeDataCalRuleConfig.parseMinusItem(para.timeRuleVO.getWorklenminusitems());
		calParam.paramValues = para.paraValueMap;//所有参数的值
		calParam.timeitemMap = para.timeitemMap; // 出差休假加班类别
		//所有的日期（往前推一天，往后也推一天）
		UFLiteralDate[] allDateBefore1AndAfter1 = CommonUtils.createDateArray(para.beginDate, para.endDate, 1, 1);
//		Map<String, ITimeScope> fullDayMap = new HashMap<String, ITimeScope>();//存储每一天的0点到23:59:59的scope的map，key是日期，提前构造好，循环里面就不要重复构造了
//		for(UFLiteralDate date:allDateBefore1AndAfter1){
//			fullDayMap.put(date.toString(), TimeScopeUtils.toFullDay(date.toString(), para.timeRuleVO.getTimeZone()));
//		}
		//需要更新的timedatavo
		List<TimeDataVO> updateVOList = new ArrayList<TimeDataVO>();
		//如果在计算的过程中，弹性班被固化了，则将固化的结果写入工作日历的子表，用于考勤数据处理界面显示各个时间段的规定上下班时间
		List<PsnWorkTimeVO> insertSolidifyWorkVOList = new ArrayList<PsnWorkTimeVO>();
		//记录需要删除工作日历子表的工作日历主表主键的list
		List<String> delWorkVOList = new ArrayList<String>();
		//需要update“固化工作时间段”字段的日历vo
		List<PsnCalendarVO> updatePsnCalendarList = new ArrayList<PsnCalendarVO>();
		for(int i=0;i<pk_psndocs.length;i++){//按人依次循环
			String pk_psndoc = pk_psndocs[i];//人员主键
			ICheckTime[] checkTimes = para.allCheckTimes[i];//此人所有的刷签卡记录
			List<TBMPsndocVO> psndocVOList = para.allTbmPsndocVOListMap==null?null:para.allTbmPsndocVOListMap.get(pk_psndoc);//人员的考勤档案
			LeaveRegVO[] leaveRegVOs = para.leaveMap==null?null:para.leaveMap.get(pk_psndoc);//人员的休假记录
			LeaveRegVO[] lactationRegVOs = para.lactationMap==null?null:para.lactationMap.get(pk_psndoc);//人员的哺乳假记录
			AwayRegVO[] awayRegVOs = para.awayMap==null?null:para.awayMap.get(pk_psndoc);//人员的出差记录
			OvertimeRegVO[] overRegVOs = para.overMap==null?null:para.overMap.get(pk_psndoc);//人员的加班记录
			ShutdownRegVO[] shutRegVOs = para.shutMap==null?null:para.shutMap.get(pk_psndoc);//人员的停工待料记录
//			Map<UFLiteralDate, AggPsnCalendar> calendarMap = para.calendarMap==null?null:para.calendarMap.get(pk_psndoc);//人员的排班
			//上面的map中在后台任务允许时无法使用，原因是UFLiteralDate的hashcode不一样取不到值
			Map<String, AggPsnCalendar> calendarMap = getStringCalendarMap(para.calendarMap==null?null:para.calendarMap.get(pk_psndoc));//人员的排班
			Map<String, TimeDataVO> timeDataMap = para.timeDataMap.get(pk_psndoc);//此人的所有timedatavo
//			Map<UFLiteralDate, String> dateOrgMap = para.allDateOrgMap.get(pk_psndoc);//此人所有天的任职组织
			Map<String, String> dateOrgMap = getStringOrgMap(para.allDateOrgMap.get(pk_psndoc));//此人所有天的任职组织
			
			//下面按天处理
			for(int dateIndex = 1;dateIndex<allDateBefore1AndAfter1.length-1;dateIndex++){//日期数组往前往后多构造了两个，因此前后两天不用处理
				UFLiteralDate curDate = allDateBefore1AndAfter1[dateIndex];
				TBMPsndocVO tbmPsndocVO = TBMPsndocVO.findIntersectionVO(psndocVOList, curDate.toString());
				//如果当日无考勤档案，或者不是机器考勤，则不用处理
				if(tbmPsndocVO==null||tbmPsndocVO.getTbm_prop().intValue()!=TBMPsndocVO.TBM_PROP_MACHINE)
					continue;
				calParam.psndocVO=tbmPsndocVO;
				//当天前一天后一天的班次，以及工作日历
				AggPsnCalendar curAggCalendar = calendarMap==null?null:calendarMap.get(curDate.toString());
				AggShiftVO curAggShift = curAggCalendar==null?null:para.shiftMap==null?null:ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,curAggCalendar.getPsnCalendarVO().getPk_shift());
				AggPsnCalendar preAggCalendar = calendarMap==null?null:calendarMap.get(allDateBefore1AndAfter1[dateIndex-1].toString());
				AggShiftVO preAggShift = preAggCalendar==null?null:para.shiftMap==null?null:ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,preAggCalendar.getPsnCalendarVO().getPk_shift());
				AggPsnCalendar nextAggCalendar = calendarMap==null?null:calendarMap.get(allDateBefore1AndAfter1[dateIndex+1].toString());
				AggShiftVO nextAggShift = nextAggCalendar==null?null:para.shiftMap==null?null:ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,nextAggCalendar.getPsnCalendarVO().getPk_shift());
				TimeZone curTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(curDate.toString())));
				TimeZone preTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(allDateBefore1AndAfter1[dateIndex-1].toString())));
				TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(allDateBefore1AndAfter1[dateIndex+1].toString())));
				//考勤时间段
				ITimeScope kqScope = ShiftVO.toKqScope(
						curAggShift==null?null:curAggShift.getShiftVO(),
						preAggShift==null?null:preAggShift.getShiftVO(),
						nextAggShift==null?null:nextAggShift.getShiftVO(),
						curDate.toString(),
						curTimeZone,preTimeZone,nextTimeZone);
				calParam.date=curDate;
				calParam.preShiftVO=preAggShift;
				calParam.curShiftVO=curAggShift;
				calParam.nextShiftVO=nextAggShift;
				calParam.preCalendarVO=preAggCalendar;
				calParam.curCalendarVO=curAggCalendar;
				calParam.nextCalendarVO=nextAggCalendar;
//				calParam.previousNatualDayScope=fullDayMap.get(allDateBefore1AndAfter1[dateIndex-1].toString());//前一个自然日的scope，计算自然日内工作时长有用
//				calParam.curNatualDayScope=fullDayMap.get(curDate.toString());//当日自然日的scope，计算自然日内工作时长有用
//				calParam.nextNatualDayScope=fullDayMap.get(allDateBefore1AndAfter1[dateIndex+1].toString());//后一个自然日的scope，计算自然日内工作时长有用
				calParam.previousNatualDayScope=TimeScopeUtils.toFullDay(allDateBefore1AndAfter1[dateIndex-1].toString(), preTimeZone);//前一个自然日的scope，计算自然日内工作时长有用
				calParam.curNatualDayScope=TimeScopeUtils.toFullDay(curDate.toString(), curTimeZone);//当日自然日的scope，计算自然日内工作时长有用
				calParam.nextNatualDayScope=TimeScopeUtils.toFullDay(allDateBefore1AndAfter1[dateIndex+1].toString(), nextTimeZone);//后一个自然日的scope，计算自然日内工作时长有用
				calParam.preTimeZone=preTimeZone;
				calParam.curTimeZone=curTimeZone;
				calParam.nextTimeZone=nextTimeZone;
				calParam.pk_joborg=dateOrgMap.get(curDate.toDate());
				calParam.checkTimes = DataFilterUtils.filterCheckTimes(kqScope, checkTimes);//当日的刷签卡记录
				calParam.awayBills=DataFilterUtils.filterRegVOs(kqScope, awayRegVOs);
				calParam.leaveBills=DataFilterUtils.filterRegVOs(kqScope, leaveRegVOs);
				calParam.lactationholidayVO=DataFilterUtils.filterDateScopeVO(curDate.toString(), lactationRegVOs);
				calParam.overtimeBills=DataFilterUtils.filterRegVOs(kqScope, overRegVOs);
				calParam.shutdownBills=DataFilterUtils.filterRegVOs(kqScope, shutRegVOs);
				calParam.mergeLASScopes = TimeScopeUtils.mergeTimeScopes(TimeScopeUtils.mergeTimeScopes(calParam.awayBills, calParam.leaveBills),calParam.shutdownBills);
				calParam.holidayVOs = DataFilterUtils.filterHolidayVOs(pk_psndoc,calParam.pk_joborg, kqScope, para.holidayEnjoyMap, para.holidayVOs);
				TimeDataVO timeDataVO = timeDataMap.get(curDate.toString());
				// 设置未生成标识为已生成
				timeDataVO.setDirty_flag(UFBoolean.FALSE);
				updateVOList.add(timeDataVO);
				//对数据进行最后的处理
				timePointProcessor.process(calParam, timeDataVO);
				if(timePointPostProcessor!=null)
					timePointPostProcessor.postProcess(calParam, timeDataVO);
				timeDataProcessor.process(calParam, timeDataVO);
				if(timeDataPostProcessor!=null)
					timeDataPostProcessor.postProcess(calParam, timeDataVO);
				//如果一个班是弹性班（与假日切过之后仍然是弹性班），则计算过后，工作时间段只有两种结果：被固化了，没被固化，没被固化的情况下，工作日历子表无数据，否则有数据
				if(curAggCalendar!=null&&curAggCalendar.getPsnCalendarVO()!=null&&curAggCalendar.getPsnCalendarVO().isFlexibleFinal()){
					delWorkVOList.add(curAggCalendar.getPsnCalendarVO().getPrimaryKey());
					updatePsnCalendarList.add(curAggCalendar.getPsnCalendarVO());
					curAggCalendar.getPsnCalendarVO().setIssolidifywhencalculation(UFBoolean.valueOf(timeDataVO.isNeedUpdateWT()));
					if(timeDataVO.isNeedUpdateWT())//表示弹性段被固化了，需要插入工作日历子表（如果没有任何对固化有用的信息，则依然固化不了）
						insertSolidifyWorkVOList.addAll(Arrays.asList(timeDataVO.getPsnWorkTimeVOs()));
				}
			}
		}
		BaseDAO dao = new BaseDAO();
		dao.setMaxRows(10000000);
		if(delWorkVOList.size()>0){
			InSQLCreator isc = new InSQLCreator();
			try{
				String inSQL = isc.getInSQL(delWorkVOList.toArray(new String[0]));
				dao.deleteByClause(PsnWorkTimeVO.class, PsnWorkTimeVO.PK_PSNCALENDAR+" in ("+inSQL+")");
			}
			finally{
				isc.clear();
			}
		}
		if(updatePsnCalendarList.size()>0)
			dao.updateVOArray(updatePsnCalendarList.toArray(new PsnCalendarVO[0]), new String[]{PsnCalendarVO.ISSOLIDIFYWHENCALCULATION});
		//弹性班在计算过程中被固化，则固化结果要保存（有可能不会被固化，例如没有任何刷签卡，也没有任何单据）
		if(insertSolidifyWorkVOList.size()>0){
			dao.insertVOList(insertSolidifyWorkVOList);
		}
		TimeDataVO[] updateVOs = updateVOList.toArray(new TimeDataVO[0]);
		dao.updateVOArray(updateVOs);
		// 保存后事件
//		EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.TIMEDATA, IEventType.TYPE_UPDATE_AFTER, updateVOs));
		fireEvent(new BusinessEvent(IMetaDataIDConst.TIMEDATA, IEventType.TYPE_UPDATE_AFTER, updateVOs));
	}
	
	/**
	 * 发送事件另起线程，不影响日报计算的相应时间
	 * @param psnInSql
	 */
	private void fireEvent(final BusinessEvent event){
		
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					EventDispatcher.fireEvent(event);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}).start();
	}

	/*
	 *  (non-Javadoc)
	 * @see nc.itf.ta.IAttendDataQueryMaintain#queryByCondDate(java.lang.String, nc.ui.querytemplate.querytree.FromWhereSQL, nc.vo.pub.lang.UFLiteralDate, nc.vo.pub.lang.UFLiteralDate)
	 */
	@Override
	public TimeDataVO[] queryByCondDate(String pk_org, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		return queryByCondDate(pk_org, fromWhereSQL, beginDate, endDate, false);
	}

	@Override
	public TimeDataVO[] queryByDate(String pk_org, UFLiteralDate date,
			FromWhereSQL fromWhereSQL) throws BusinessException {
		return queryByDate(pk_org, date, fromWhereSQL, false);
	}

	@Override
	public TimeDataVO[] queryByPsn(String pk_org, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		return queryByPsn(pk_org, pk_psndoc, beginDate, endDate, false);
	}

	@Override
	public TimeDataVO[] queryByCondDate(String pk_org, FromWhereSQL fromWhereSQL,
			String year, String month) throws BusinessException {
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if(periodVO==null)
			return null;
		return queryByCondDate(pk_org, fromWhereSQL, periodVO.getBegindate(), periodVO.getEnddate());
	}

	@Override
	public TimeDataVO[] queryByPsn(String pk_org, String pk_psndoc,
			String year, String month) throws BusinessException {
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if(periodVO==null)
			return null;
		return queryByPsn(pk_org, pk_psndoc, periodVO.getBegindate(), periodVO.getEnddate());
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, TimeDataVO>> queryMapByPkPsndocsDate(String pk_org, String psndocInSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		TimeDataVO[] vos = null;
		String cond = IBaseServiceConst.PK_ORG_FIELD+"=? and "+TBMPsndocVO.PK_PSNDOC+" in ("+psndocInSQL+") and "+TimeDataVO.CALENDAR+" between ? and ? ";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_org);
		param.addParam(beginDate.toString());
		param.addParam(endDate.toString());
		BaseDAO dao = new BaseDAO();
		//设置返回最大行数，否则默认只返回前10w行
		dao.setMaxRows(10000000);
		vos = CommonUtils.toArray(TimeDataVO.class,(Collection<TimeDataVO>)dao.retrieveByClause(TimeDataVO.class, cond,
				new String[]{TimeDataVO.PK_TIMEDATA,TimeDataVO.PK_ORG,TimeDataVO.PK_GROUP,TimeDataVO.PK_PSNDOC,TimeDataVO.CALENDAR,TimeDataVO.CREATIONTIME,TimeDataVO.CREATOR},
				param));
		//将vo数组包装成key是人员，小key是日期的map
		return DailyDataUtils.groupByPsnStrDate(vos);
	}

	private class CalParam{
		public String pk_org;//组织主键
		public String[] pk_psndocs ;//所有人员的主键
		public Map<String, List<TBMPsndocVO>> allTbmPsndocVOListMap;//所有人员在这段日期范围内的所有考勤档案，key是人员主键
		public Map<String, Map<UFLiteralDate, String>> allDateOrgMap;//所有人员在这段日期范围内的考勤档案的任职组织，key是人员主键，value的key是此人此天任职所属的业务单元主键
		public ICheckTime[][] allCheckTimes;//所有人员在日期范围内的刷签卡记录
		public Map<String, LeaveRegVO[]> leaveMap;//所有人员在日期范围内的休假记录
		public Map<String, LeaveRegVO[]> lactationMap;//所有人员在日期范围内的哺乳假记录
		public Map<String, OvertimeRegVO[]> overMap;//所有人员在日期范围内的休假记录
		public Map<String, AwayRegVO[]> awayMap;//所有人员在日期范围内的出差记录
		public Map<String, ShutdownRegVO[]> shutMap;//所有人员在日期范围内的停工待料记录
		public Map<String, HRHolidayVO[]> orgHolidayVOMap;//公共假日,key是业务单元主键（未考虑享有条件）
		public HRHolidayVO[] holidayVOs;//将orgHolidayVOMap的value拉平，方便使用
		public Map<String, Map<String, Boolean>> holidayEnjoyMap;//公共假日的享有情况，<人员主键,<pk_holiday+业务单元主键，是否享有>>
		public UFLiteralDate beginDate;
		public UFLiteralDate endDate;
		public TimeRuleVO timeRuleVO;
		public Map<String, AggShiftVO> shiftMap ;
		public Map<String, Map<UFLiteralDate, AggPsnCalendar>>  calendarMap;
		public Map<String, Object> paraValueMap;//所有的参数值
		public Map<String, Map<String, TimeDataVO>> timeDataMap;//此次要update的所有timedata，key是人员，小key是日期
		public Map<String, TimeItemVO> timeitemMap;
	}
	
	// 2012-10-23修改，优化sql及效率
//	@Override
//	public TimeDataVO[] queryByCondDate(String pk_org,
//			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
//			UFLiteralDate endDate, boolean onlyShowException)
//			throws BusinessException {
//		if(beginDate.equals(endDate))
//			return queryByDate(pk_org, beginDate, fromWhereSQL,onlyShowException);
//		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
//		TimeDataVO[] retArray = null;
//		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
//		boolean hasPkOrg = StringUtils.isNotEmpty(pk_org);
//		ITimeRuleQueryService timeRuleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
//		Map<String, TimeZone> timeZoneMap = hasPkOrg?timeRuleService.queryTimeZoneMap(pk_org):new HashMap<String, TimeZone>();
//		for(UFLiteralDate date:allDates){
//			retArray = (TimeDataVO[])org.apache.commons.lang.ArrayUtils.addAll(retArray,
//					queryByDate(pk_org, date, fromWhereSQL,onlyShowException,true,shiftMap,timeZoneMap));
//		}
//		return retArray;
//	}
	
	@Override
	public TimeDataVO[] queryByCondDate(String pk_org,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate,boolean onlyShowException)throws BusinessException {
		//要考虑权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		TBMPsndocVO[] psndocVOs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
			queryLatestMachineDocByCondition(pk_org,fromWhereSQL, beginDate, endDate);
		if(ArrayUtils.isEmpty(psndocVOs))
			return null;
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_org);
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		ITimeRuleQueryService timeRuleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
		Map<String, TimeZone> timeZoneMap = hasPkOrg?timeRuleService.queryTimeZoneMap(pk_org):new HashMap<String, TimeZone>();
		TimeDataVO[] vos = null;
		InSQLCreator isc = new InSQLCreator();
		//这些人这一天的工作日历
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap =null;
		try{
			String inSQL = isc.getInSQL(psndocVOs, TBMPsndocVO.PK_PSNDOC);
			//然后查询这些人员在这些天的timedata记录
			String cond = "";
			if(hasPkOrg)
				cond+=IBaseServiceConst.PK_ORG_FIELD+"=? and ";
			cond +=TBMPsndocVO.PK_PSNDOC+" in ("+inSQL+") and "+TimeDataVO.CALENDAR+" between ? and ? ";
			SQLParameter param = new SQLParameter();
			if(hasPkOrg)
				param.addParam(pk_org);
			param.addParam(beginDate.toString());
			param.addParam(endDate.toString());
			vos = CommonUtils.retrieveByClause(TimeDataVO.class, cond, param);
			calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
				queryCalendarVOByCondition4DataProcessView(pk_org, beginDate, endDate, inSQL,timeZoneMap,shiftMap);
		}
		finally{
			isc.clear();
		}
		// 构造已有考勤记录Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		//可能有些人还没生成这一天的timedata记录，需要在内存中new一条(不是真的生成)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		for(TBMPsndocVO psndocVO:psndocVOs){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(psndocVO.getPk_psndoc());
			for(UFLiteralDate date:allDates){
				TimeDataVO vo = dateMap==null?null:dateMap.get(date);
				if(vo==null){
					// 没有记录时在内存中new一条
					vo = new TimeDataVO();
					vo.setPk_org(psndocVO.getPk_org());
					vo.setPk_psndoc(psndocVO.getPk_psndoc());
					vo.setCalendar(date);
					vo.setDirty_flag(UFBoolean.TRUE);
				}
				// 计算属性赋值
				vo.setPk_psnjob(psndocVO.getPk_psnjob());
				vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
				vo.setPk_org_v(psndocVO.getPk_org_v());
				vo.setPk_dept_v(psndocVO.getPk_dept_v());
				
				AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(psndocVO.getPk_psndoc())==null?null:
						calendarMap.get(psndocVO.getPk_psndoc()).get(date);
				if(calendarVO==null&&onlyShowException){//无工作日历视为正常
					continue;
				}
				//设置班次信息
				if(calendarVO!=null){
					PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
					vo.setPk_shift(psnCalendarVO.getPk_shift());
					if(onlyShowException&&vo.isNormal())//如果只显示异常，且当前vo是正常的，则不加入返回的list
						continue;
					PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
					vo.setPsnWorkTimeVOs(wtVOs);//用于界面显示每个工作段的规定上下班时间，已经考虑考勤数据处理时固化的班次
					//是否需要显示中途外出列（如果班次不允许中途外出，则需要显示中途外出列，包含次数和时长子列）
					boolean dispMidOut = false;
					if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
						//容错处理，防止shiftMap里面没有此班次
						ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift()).getShiftVO();
						dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
					}
					vo.setMidOut(dispMidOut);
				}
				vo.trimMemory();//数据量很大时，服务器会outofmemory，因此此处将UFDouble处理了一下，0值都用null
				retList.add(vo);
			}
		}
		return retList.toArray(new TimeDataVO[0]);
	}
	
	/**
	 * 效率优化
	 * 只查询 vo中的pk_org+date+pk_psndoc+pk_psnjob+pk_org_v+pk_dept_v拼成字符串返回
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param onlyShowException
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryOnlyPkByCondDate(String pk_org,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate,boolean onlyShowException)throws BusinessException {
		//要考虑权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		TBMPsndocVO[] psndocVOs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
			queryLatestMachineDocByCondition(pk_org,fromWhereSQL, beginDate, endDate);
		if(ArrayUtils.isEmpty(psndocVOs))
			return null;
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_org);
		TimeDataVO[] vos = null;
		InSQLCreator isc = new InSQLCreator();
		try{
			String inSQL = isc.getInSQL(psndocVOs, TBMPsndocVO.PK_PSNDOC);
			//然后查询这些人员在这些天的timedata记录
			String cond = "";
			if(hasPkOrg)
				cond+=IBaseServiceConst.PK_ORG_FIELD+"=? and ";
			cond +=TBMPsndocVO.PK_PSNDOC+" in ("+inSQL+") and "+TimeDataVO.CALENDAR+" between ? and ? ";
			SQLParameter param = new SQLParameter();
			if(hasPkOrg)
				param.addParam(pk_org);
			param.addParam(beginDate.toString());
			param.addParam(endDate.toString());
			BaseDAO dao = new BaseDAO();
			Collection c = dao.retrieveByClause(TimeDataVO.class, cond, null, new String[]{TimeDataVO.PK_ORG,TimeDataVO.CALENDAR,TimeDataVO.PK_PSNDOC}, param);
			if(!CollectionUtils.isEmpty(c)){
				vos = (TimeDataVO[]) c.toArray(new TimeDataVO[0]);
			}
//			vos = CommonUtils.retrieveByClause(TimeDataVO.class, cond, param);
		}
		finally{
			isc.clear();
		}
		// 构造已有考勤记录Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		//可能有些人还没生成这一天的timedata记录，需要在内存中new一条(不是真的生成)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		for(TBMPsndocVO psndocVO:psndocVOs){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(psndocVO.getPk_psndoc());
			for(UFLiteralDate date:allDates){
				TimeDataVO vo = dateMap==null?null:dateMap.get(date);
				if(vo==null){
					// 没有记录时在内存中new一条
					vo = new TimeDataVO();
					vo.setPk_org(psndocVO.getPk_org());
					vo.setPk_psndoc(psndocVO.getPk_psndoc());
					vo.setCalendar(date);
					vo.setDirty_flag(UFBoolean.TRUE);
				}
				// 计算属性赋值
				vo.setPk_psnjob(psndocVO.getPk_psnjob());
				vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
				vo.setPk_org_v(psndocVO.getPk_org_v());
				vo.setPk_dept_v(psndocVO.getPk_dept_v());
				
				vo.trimMemory();//数据量很大时，服务器会outofmemory，因此此处将UFDouble处理了一下，0值都用null
				retList.add(vo);
			}
		}
		return DailyDataUtils.getPKs(retList.toArray(new TimeDataVO[0]));
	}

	@Override
	public TimeDataVO[] queryByDate(String pk_org, UFLiteralDate date,
			FromWhereSQL fromWhereSQL, boolean onlyShowException)
			throws BusinessException {
		//pk_org为null的话，会查询出全局内的所有班次
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		return queryByDate(pk_org, date, fromWhereSQL, onlyShowException, shiftMap);
	}

	private TimeDataVO[] queryByDate(String pk_org, UFLiteralDate date,
			FromWhereSQL fromWhereSQL, boolean onlyShowException,Map<String, AggShiftVO> shiftMap)
			throws BusinessException {
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_org);
		ITimeRuleQueryService timeRuleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
		Map<String, TimeZone> timeZoneMap = hasPkOrg?timeRuleService.queryTimeZoneMap(pk_org):new HashMap<String, TimeZone>();
		return queryByDate(pk_org, date, fromWhereSQL, onlyShowException, false, shiftMap,timeZoneMap);
	}

	@SuppressWarnings("unchecked")
	private TimeDataVO[] queryByDate(String pk_org, UFLiteralDate date,
			FromWhereSQL fromWhereSQL, 
			boolean onlyShowException,
			boolean hasProcessedPowerSQL,Map<String, AggShiftVO> shiftMap,
			Map<String, TimeZone> timeZoneMap)
			throws BusinessException {
		//查询此天的人员，要考虑权限
		if(!hasProcessedPowerSQL)
			fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		TBMPsndocVO[] psndocVOs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
		queryLatestMachineDocByCondition(pk_org,
				fromWhereSQL, date, date);
		if(ArrayUtils.isEmpty(psndocVOs))
			return null;
		TimeDataVO[] vos = null;
		InSQLCreator isc = null;
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_org);
		//这些人这一天的工作日历
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap =null;
		try{
			SQLParameter param = new SQLParameter();
			if(hasPkOrg)
				param.addParam(pk_org);
			param.addParam(date.toString());
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(psndocVOs, TBMPsndocVO.PK_PSNDOC);
			//然后查询这些人员在这一天的timedata记录
			String cond = "";
			if(hasPkOrg)
				cond+=IBaseServiceConst.PK_ORG_FIELD+"=? and ";
			cond +=TBMPsndocVO.PK_PSNDOC+" in ("+inSQL+") and "+TimeDataVO.CALENDAR+" = ? ";
			vos = CommonUtils.toArray(TimeDataVO.class,(Collection<TimeDataVO>)new BaseDAO().retrieveByClause(TimeDataVO.class, cond, param));
			calendarMap = 
				NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
				queryCalendarVOByCondition4DataProcessView(pk_org, date, date, inSQL,timeZoneMap,shiftMap);
		}
		finally{
			isc.clear();
		}
		Map<String, TimeDataVO> voMap = CommonUtils.toMap(TimeDataVO.PK_PSNDOC, vos);//将timedatavos包装成map，key是人员主键
		//可能有些人还没生成这一天的timedata记录，需要在内存中new一条(不是真的生成)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		for(TBMPsndocVO psndocVO:psndocVOs){
			TimeDataVO vo = MapUtils.isEmpty(voMap)?null:voMap.get(psndocVO.getPk_psndoc());
			if(vo==null){
				vo = new TimeDataVO();
				vo.setPk_org(psndocVO.getPk_org());
				vo.setPk_psndoc(psndocVO.getPk_psndoc());
				vo.setCalendar(date);
				vo.setDirty_flag(UFBoolean.TRUE);
			}
			vo.setPk_psnjob(psndocVO.getPk_psnjob());
			vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
			vo.setPk_org_v(psndocVO.getPk_org_v());
			vo.setPk_dept_v(psndocVO.getPk_dept_v());


			AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(psndocVO.getPk_psndoc())==null?null:calendarMap.get(psndocVO.getPk_psndoc()).get(date);
			if(calendarVO==null&&onlyShowException){//无工作日历视为正常
				continue;
			}
			//设置班次信息
			if(calendarVO!=null){
				PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
				vo.setPk_shift(psnCalendarVO.getPk_shift());
				if(onlyShowException&&vo.isNormal())//如果只显示异常，且当前vo是正常的，则不加入返回的list
					continue;
				PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
				vo.setPsnWorkTimeVOs(wtVOs);//用于界面显示每个工作段的规定上下班时间，已经考虑考勤数据处理时固化的班次
				//是否需要显示中途外出列（如果班次不允许中途外出，则需要显示中途外出列，包含次数和时长子列）
				boolean dispMidOut = false;
				if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
					//容错处理，防止shiftMap里面没有此班次
					AggShiftVO aggShiftVOFromMap = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift());
					if(null!=aggShiftVOFromMap){
						ShiftVO shiftVO = aggShiftVOFromMap.getShiftVO();
						dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
					} 
				}
				vo.setMidOut(dispMidOut);
			}
			vo.trimMemory();//数据量很大时，服务器会outofmemory，因此此处将UFDouble处理了一下，0值都用null
			retList.add(vo);
		}
//		for(TimeDataVO vo:retList){
//			vo.trimMemory();//数据量很大时，服务器会outofmemory，因此此处将UFDouble处理了一下，0值都用null
//		}
		return retList.toArray(new TimeDataVO[0]);
	}

	@SuppressWarnings("unchecked")
	protected TimeDataVO[] queryByPsn(String pk_org, String pk_psndoc,
			UFLiteralDate[] allDates,boolean isDatesContinous,
			boolean onlyShowException) throws BusinessException{
		Arrays.sort(allDates);

		//查询考勤档案，只返回机器考勤的
		List<TBMPsndocVO> psndocList = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
			queryTBMPsndocListByPsndoc(pk_org, pk_psndoc, allDates[0], allDates[allDates.length-1], true, TbmPropEnum.MACHINE_CHECK);
		if(CollectionUtils.isEmpty(psndocList))
			return null;
		boolean hasPkOrg = StringUtils.isNotEmpty(pk_org);
		String cond = TimeDataVO.PK_PSNDOC+"=? and ";
		if(hasPkOrg)
			cond+=IBaseServiceConst.PK_ORG_FIELD+"=? and ";
		cond += TimeDataVO.CALENDAR;
		if(isDatesContinous)
			cond+=" between ? and ? ";
		else
			cond+=" in ("+StringPiecer.getDefaultPiecesTogether(allDates)+")";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_psndoc);
		if(hasPkOrg)
			param.addParam(pk_org);
		if(isDatesContinous){
			param.addParam(allDates[0].toString());
			param.addParam(allDates[allDates.length-1].toString());
		}
		TimeDataVO[] vos = CommonUtils.toArray(TimeDataVO.class,(Collection<TimeDataVO>)new BaseDAO().retrieveByClause(TimeDataVO.class, cond, param));
		//将vos包装成map，key是日期
		Map<UFLiteralDate, TimeDataVO> voMap = new HashMap<UFLiteralDate, TimeDataVO>();
		if(!ArrayUtils.isEmpty(vos))
			for(TimeDataVO vo:vos){
				voMap.put(vo.getCalendar(), vo);
			}
		//这些天的工作日历
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = isDatesContinous?
				calendarService.queryCalendarMapByPsnDates4DataProcessView(pk_org, pk_psndoc,allDates[0], allDates[allDates.length-1]):
				calendarService.queryCalendarMapByPsnDates(pk_org, pk_psndoc, allDates);


		//查询出来的vos是数据库中已有的，数据库中没有的需要自己构造
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		for(UFLiteralDate date:allDates){
//			if(!TBMPsndocVO.isIntersect(psndocList, date.toString()))
//				continue;
			// 工作记录主键应取当天考勤档案对应的工作记录
			TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocList, date.toString());
			if (psndocVO == null)
				continue;
			TimeDataVO vo = null;
			if(voMap.containsKey(date)){
				vo = voMap.get(date);
				//此处的TimeDataVO可能含有脏数据，比如张三在20号从A组织调配到B组织，并在A生成过考勤数据，
				//b组织没有生成过，则自助查询的时候pk_org=null,此时会把20之后属于A组织的考勤数据查询出来,这是不正确的
				if(!vo.getPk_org().equals(psndocVO.getPk_org()))
					vo = null;
			}
			if(vo == null){
				vo = new TimeDataVO();
				vo.setPk_psndoc(pk_psndoc);
				vo.setCalendar(date);
				vo.setDirty_flag(UFBoolean.TRUE);
			}
			vo.setPk_psnjob(psndocVO.getPk_psnjob());

			AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(date);
			if(calendarVO==null&&onlyShowException){//无工作日历视为正常
				continue;
			}
			//设置班次信息
			if(calendarVO!=null){
				vo.setPk_shift(calendarVO.getPsnCalendarVO().getPk_shift());
				if(onlyShowException&&vo.isNormal())//如果只显示异常，且当前vo是正常的，则不加入返回的list
					continue;
				PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
				vo.setPsnWorkTimeVOs(wtVOs);//用于界面显示每个工作段的规定上下班时间，已经考虑考勤数据处理时固化的班次
				//是否需要显示中途外出列（如果班次不允许中途外出，则需要显示中途外出列，包含次数和时长子列）
				boolean dispMidOut = false;
				if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
					//容错处理，防止shiftMap里面没有此班次
					AggShiftVO aggShiftVOFromMap = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift());
					if(null!=aggShiftVOFromMap){
						ShiftVO shiftVO = aggShiftVOFromMap.getShiftVO();
						dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
					}
				}
				vo.setMidOut(dispMidOut);
			}
			retList.add(vo);
		}
		return retList.toArray(new TimeDataVO[0]);

	}

	@Override
	public TimeDataVO[] queryByPsn(String pk_org, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean onlyShowException) throws BusinessException {
		return queryByPsn(pk_org, pk_psndoc, CommonUtils.createDateArray(beginDate, endDate), true, onlyShowException);
	}

	/**
	 * 根据条件、开始日期、结束日期和选择的要清理的数据内容进行数据清理
	 * @param param
	 * @param context
	 * @param fromWhereSQL
	 * @param begindate
	 * @param enddate
	 * @throws BusinessException
	 */
	public void cleardata(ClearDataParam param,LoginContext context,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException{
		// 验证开始、结束日期是否在考勤期间范围内或是否已封存
		PeriodServiceFacade.checkDateScope(context.getPk_org(), beginDate, endDate);
		//数据清理权限和考勤档案的维护权限一致
//		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", nc.ui.ta.pub.action.IActionCode.CLS, fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		// 清除签卡数据
		if (param.isClearqkdata()) {
			NCLocator.getInstance().lookup(ISignCardManageService.class).deleteData(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// 清除机器考勤数据
		if (param.isClearjqkqdata()) {
			deleteTimeData(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// 清除工作日历数据
		if (param.isCleargzrldata()) {
			NCLocator.getInstance().lookup(IPsnCalendarManageService.class).deleteByCondAndDateScope(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// 清除刷卡数据
		if (param.isClearskdata()) {
			NCLocator.getInstance().lookup(IImportDataManageMaintain.class).deleteByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate);
			//清除刷卡数据时清除系统刷卡数据
			NCLocator.getInstance().lookup(ISysCardManageService.class).deleteByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// 清除手工考勤数据
		if (param.isClearsgkqdata()) {
			NCLocator.getInstance().lookup(ILateEarlyManageService.class).deleteLateEarlyData(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// 清除考勤日报数据
		if (param.isClearkqrbdata()) {
			NCLocator.getInstance().lookup(IDayStatManageService.class).deleteByCondAndDateScope(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		//业务日志
		String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		TaBusilogUtil.writeDataProcessClear(context.getPk_org(), pk_psndocs, beginDate, endDate);
	}
	
	/**
	 * 删除机器考勤数据
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	private void deleteTimeData(String pk_org, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		new TimeDataRecordCreator(IDailyRecordCreator.CREATOR_TYPE_LASTYDAYMATCH).deleteDailyRecord(pk_org, fromWhereSQL, beginDate, endDate);
		TBMPsndocVO[] psndocVOs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestManualDocByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if(ArrayUtils.isEmpty(psndocVOs))
			return;
		InSQLCreator isc = new InSQLCreator();
		try {
			PsnInSQLDateScope scope = new PsnInSQLDateScope();
			scope.setPk_org(pk_org);
			scope.setPsndocInSQL(isc.getInSQL(StringPiecer.getStrArray(psndocVOs, TBMPsndocVO.PK_PSNDOC)));
			scope.setBeginDate(beginDate);
			scope.setEndDate(endDate);
			// 保存后事件
			EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.TIMEDATA, IEventType.TYPE_DELETE_AFTER, scope));
		} finally {
			isc.clear();
		}
	}

	@Override
	public TBMPsndocVO[] queryPsnByFromWhereSQLAndDates(String pk_org,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		//增加权限处理
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		return NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
		queryLatestMachineDocByCondition(pk_org, fromWhereSQL, beginDate, endDate);
	}

	@Override
	public TimeDataVO[] queryByCondDateAndDept(
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_dept, boolean containsSubDepts)
			throws BusinessException {
		return queryByCondDate(null, TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL),
				beginDate, endDate);
	}

	@Override
	public TimeDataVO[] queryByCondDateAndDept(
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean onlyShowException, String pk_dept,
			boolean containsSubDepts) throws BusinessException {
		fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		//添加机器考勤过滤条件
		fromWhereSQL = TBMPsndocSqlPiecer.addPropCond(fromWhereSQL, TBMPsndocVO.TBM_PROP_MACHINE);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		//查询出日期范围内的所有考勤档案记录，日期范围内最新的一条作为条件匹配，然后找出人员在日期范围内所有的考勤档案记录
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.
			queryTBMPsndocMapByConditionForTeam(null, fromWhereSQL, beginDate, endDate, true,true,null);
		if(psndocMap==null||psndocMap.isEmpty())
			return null;
		String pk_hrorg = null;
		for(String psndocPk:psndocMap.keySet()){
			pk_hrorg = psndocMap.get(psndocPk).get(0).getPk_org();
			break;
		}		
//		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(null);//大数据环境查询的数据量太大
		InSQLCreator isc = new InSQLCreator();
		String inSQL = isc.getInSQL(psndocMap.keySet().toArray(new String[0]));		
		String shiftPkQrySql = "select distinct(pk_shift) from tbm_psncalendar where pk_psndoc in (" +inSQL+ ") and calendar between '" + beginDate+"' and '" +endDate+"'";
		GeneralVO[] shiftPk = (GeneralVO[]) new BaseDAO().executeQuery(shiftPkQrySql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		String[] pk_shifts = null;
		if(shiftPk != null){
			pk_shifts = new String[shiftPk.length];
			for(int i=0;i<shiftPk.length;i++){
				pk_shifts[i] = shiftPk[i].toString();
			}
		}	
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryShiftAggVOByPkArray(pk_shifts);
		Map<String, AggShiftVO> shiftMap = new HashMap<String,AggShiftVO>();
		if(shiftVOs != null){
			for(AggShiftVO shiftVO:shiftVOs){
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(), shiftVO);
			}
		}				
		Map<String, TimeZone> timeZoneMap = new HashMap<String, TimeZone>();
		TimeDataVO[] vos = null;
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap = null;
		//然后查询这些人员在这些天的timedata记录
		String cond = TBMPsndocVO.PK_PSNDOC+" in ("+inSQL+") and "+TimeDataVO.CALENDAR+" between ? and ? ";
		SQLParameter param = new SQLParameter();
		param.addParam(beginDate.toString());
		param.addParam(endDate.toString());
		vos = CommonUtils.retrieveByClause(TimeDataVO.class, cond, param);
		calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
			queryCalendarVOByCondition4DataProcessView(pk_hrorg, beginDate, endDate, inSQL,timeZoneMap,shiftMap);
		// 构造已有考勤记录Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		//可能有些人还没生成这一天的timedata记录，需要在内存中new一条(不是真的生成)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		//按人按天构造记录
		for(String pk_psndoc:psndocMap.keySet()){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(pk_psndoc);
			List<TBMPsndocVO> tbmdocList = psndocMap.get(pk_psndoc);
			if(CollectionUtils.isEmpty(tbmdocList))
				continue;
			for(TBMPsndocVO psndocVO:tbmdocList){
				//只对考勤档案范围和起止日期交集的时间构造数据
				UFLiteralDate begin = psndocVO.getBegindate().before(beginDate)?beginDate:psndocVO.getBegindate();
				UFLiteralDate end = psndocVO.getEnddate().before(endDate)?psndocVO.getEnddate():endDate;
				if(begin.after(end))
					continue;
				UFLiteralDate[] allDates = CommonUtils.createDateArray(begin, end);
				for(UFLiteralDate date:allDates){
					TimeDataVO vo = dateMap==null?null:dateMap.get(date);
					if(vo==null){
						// 没有记录时在内存中new一条
						vo = new TimeDataVO();
						vo.setPk_org(psndocVO.getPk_org());
						vo.setPk_psndoc(psndocVO.getPk_psndoc());
						vo.setCalendar(date);
						vo.setDirty_flag(UFBoolean.TRUE);
					}
					// 计算属性赋值
					vo.setPk_psnjob(psndocVO.getPk_psnjob());
					vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
					vo.setPk_org_v(psndocVO.getPk_org_v());
					vo.setPk_dept_v(psndocVO.getPk_dept_v());
					
					AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(psndocVO.getPk_psndoc())==null?null:
							calendarMap.get(psndocVO.getPk_psndoc()).get(date);
					if(calendarVO==null&&onlyShowException){//无工作日历视为正常
						continue;
					}
					//设置班次信息
					if(calendarVO!=null){
						PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
						vo.setPk_shift(psnCalendarVO.getPk_shift());
						if(onlyShowException&&vo.isNormal())//如果只显示异常，且当前vo是正常的，则不加入返回的list
							continue;
						PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
						vo.setPsnWorkTimeVOs(wtVOs);//用于界面显示每个工作段的规定上下班时间，已经考虑考勤数据处理时固化的班次
						//是否需要显示中途外出列（如果班次不允许中途外出，则需要显示中途外出列，包含次数和时长子列）
						boolean dispMidOut = false;
						if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
							//容错处理，防止shiftMap里面没有此班次
//							ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift()).getShiftVO();
//							dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
						}
						vo.setMidOut(dispMidOut);
					}
					vo.trimMemory();//数据量很大时，服务器会outofmemory，因此此处将UFDouble处理了一下，0值都用null
					retList.add(vo);
				}
			}
		}
		return retList.toArray(new TimeDataVO[0]);
		
		//2013-05-17 问题是若一个人在时间范围内，出现在两个部门，则查询结果为只能全部显示在后面的部门里，无法查询前一个部门内的历史数据
//		return queryByCond Date(null, TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL),
//				beginDate, endDate, onlyShowException);
	}

	@Override
	public TimeDataVO[] queryByDateAndDept(UFLiteralDate date,
			FromWhereSQL fromWhereSQL, String pk_dept, boolean containsSubDepts)
			throws BusinessException {
		return queryByDate(null, date, TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL));
	}

	@Override
	public TimeDataVO[] queryByDateAndDept(UFLiteralDate date,
			FromWhereSQL fromWhereSQL, boolean onlyShowException,
			String pk_dept, boolean containsSubDepts) throws BusinessException {
		return queryByDate(null, date,
				TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL),
				onlyShowException);
	}

	@Override
	public TBMPsndocVO[] queryPsnByFromWhereSQLAndDatesAndDept(
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_dept, boolean containsSubDepts)
			throws BusinessException {
		return queryPsnByFromWhereSQLAndDates(null,
				TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL),
				beginDate, endDate);
	}

	@Override
	public TimeDataVO[] queryByPsn4Mgr(String pk_org, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		return queryByPsn4Mgr(pk_org, pk_psndoc, beginDate, endDate, false);
	}

	@Override
	public TimeDataVO[] queryByPsn4Mgr(String pk_org, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean onlyShowException) throws BusinessException {
		return queryByPsn(null, pk_psndoc, beginDate, endDate, onlyShowException);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggSignVO createSignCard(String pk_psndoc,
			UFLiteralDate[] signCardDates) throws BusinessException {
		if(org.apache.commons.lang.StringUtils.isBlank(pk_psndoc)||ArrayUtils.isEmpty(signCardDates))
			throw new IllegalArgumentException("the pk_psndoc and signcarddates can not be null!");
		//过滤天：只有在当前最新HR组织的考勤档案内的天，才能填写单据
		String cond = TBMPsndocVO.PK_PSNDOC+"=? and "+TBMPsndocVO.TBM_PROP+"="+TBMPsndocVO.TBM_PROP_MACHINE;
		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		TBMPsndocVO[] psndocVOs = (TBMPsndocVO[]) CommonUtils.toArray(TBMPsndocVO.class,
				dao.retrieveByClause(TBMPsndocVO.class, cond, TBMPsndocVO.BEGINDATE, para));
		if(ArrayUtils.isEmpty(psndocVOs))
			throw new BusinessException("This employee has not TA document!");
		Map<UFLiteralDate, TBMPsndocVO> psndocMap = new HashMap<UFLiteralDate, TBMPsndocVO>();
		for(UFLiteralDate date : signCardDates){
			for(TBMPsndocVO tbmpsndocVO:psndocVOs){
				//日期在考勤档案范围内，才会处理这一天
				if(DateScopeUtils.contains(tbmpsndocVO, date))
					psndocMap.put(date, tbmpsndocVO);
			}
		}
		if(psndocMap.size()==0)
			throw new BusinessException("The dates are not in any TA document!");
		//如果考勤档案有多个任职组织，则抛异常，因为一次只能填一个任职组织的单据
		TBMPsndocVO[] filteredPsndocVOs = psndocMap.values().toArray(new TBMPsndocVO[0]);
		Arrays.sort(filteredPsndocVOs);
		String pk_joborg = null;//最终pk_psnjob的任职组织
		InSQLCreator isc = null;
		try{
			isc = new InSQLCreator();
			String psnjobInSQL = isc.getInSQL(filteredPsndocVOs, TBMPsndocVO.PK_PSNJOB);
			PsnJobVO[] psnjobVOs = CommonUtils.retrieveByClause(PsnJobVO.class, PsnJobVO.PK_PSNJOB+" in("+psnjobInSQL+")");
			String[] pk_joborgs = StringPiecer.getStrArrayDistinct(psnjobVOs, PsnJobVO.PK_ORG);
			if(ArrayUtils.getLength(pk_joborgs)>1)
				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0042")
				/*@res "选择的日期属于不同业务单元的任职记录!"*/);
			pk_joborg = pk_joborgs[0];
		}
		finally{
			if(isc!=null)
				isc.clear();
		}
		//代码走到这里，表明所选择的日期都是属于同一个考勤档案了
		String pk_group = filteredPsndocVOs[0].getPk_group();
		String pk_org = filteredPsndocVOs[0].getPk_org();
		AggSignVO aggVO = new AggSignVO();
		SignhVO hVO = new SignhVO();
		aggVO.setParentVO(hVO);
		hVO.setStatus(VOStatus.NEW);
		hVO.setPk_group(pk_group);
		hVO.setPk_org(pk_org);
		hVO.setPk_psndoc(pk_psndoc);
		hVO.setIshrssbill(UFBoolean.TRUE);
		hVO.setApply_date(new UFLiteralDate());
		hVO.setBillmaker(InvocationInfoProxy.getInstance().getUserId());
		hVO.setPk_billtype(SignCardConst.BillTYPE);
		hVO.setApprove_state(IBillStatus.FREE);
		//取最后一个日期所属的考勤档案的pk_psnorg和pk_psnjob
		TBMPsndocVO lastPsndocVO = filteredPsndocVOs[filteredPsndocVOs.length-1];
		hVO.setPk_psnorg(lastPsndocVO.getPk_psnorg());
		hVO.setPk_psnjob(lastPsndocVO.getPk_psnjob());
		//单据编码
		BillCodeContext billCodeContext = NCLocator.getInstance().lookup(IBillcodeManage.class)
		.getBillCodeContext(SignCardConst.BillTYPE, pk_group, pk_org);
		// 如果是自动生成编码，则需要生成编码
		String billCode = null;
		try{
			if (billCodeContext != null){
				billCode = NCLocator.getInstance().lookup(IHrBillCode.class).getBillCode(SignCardConst.BillTYPE, pk_group, pk_org);
				hVO.setBill_code(billCode) ;
			}
			//至此，主表信息构造完毕,下面构造子表信息
			UFLiteralDate[] filteredDates = psndocMap.size()==signCardDates.length?signCardDates:psndocMap.keySet().toArray(new UFLiteralDate[0]);
			Arrays.sort(filteredDates);
			TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_joborg);
			aggVO.setChildrenVO(createSignBodyVOs(pk_group,pk_org, pk_psndoc, filteredDates,timeZone));
			return aggVO;
		}
		catch(Exception e){
			if(billCode!=null)
				NCLocator.getInstance().lookup(IHrBillCode.class).rollbackPreBillCode(SignCardConst.BillTYPE, pk_group, pk_org, billCode);
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(),e);
		}
	}

	@SuppressWarnings("unchecked")
	private SignbVO[] createSignBodyVOs(String pk_group,String pk_org,String pk_psndoc,UFLiteralDate[] signCardDates,TimeZone timeZone) throws BusinessException{
		//首先查询这些天的timedata数据
		Arrays.sort(signCardDates);
		String dateCond = StringPiecer.getDefaultPiecesTogether(signCardDates);
		BaseDAO dao = new BaseDAO();
		String cond = IBaseServiceConst.PK_ORG_FIELD+"='"+pk_org+"' and "+TimeDataVO.PK_PSNDOC+"='"+pk_psndoc+"' and "+TimeDataVO.CALENDAR+" in("+dateCond+")";
		TimeDataVO[] timeDataVOs = (TimeDataVO[]) CommonUtils.toArray(TimeDataVO.class, dao.retrieveByClause(TimeDataVO.class, cond));
		if(ArrayUtils.isEmpty(timeDataVOs))
			return null;
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarMapByPsnDates4DataProcessView(pk_org, pk_psndoc, signCardDates);
		//如果没有排班，也不用处理
		if(MapUtils.isEmpty(calendarMap))
			return null;
		Map<UFLiteralDate, TimeDataVO> timeDataVOMap = CommonUtils.toMap(TimeDataVO.CALENDAR, timeDataVOs);
		List<SignbVO> signbVOList = new ArrayList<SignbVO>();
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		boolean checkFlag = timeRuleVO.isCheckinflag();
		for(UFLiteralDate signCardDate:signCardDates){
			TimeDataVO timeDataVO = MapUtils.isEmpty(timeDataVOMap)?null:timeDataVOMap.get(signCardDate);
			AggPsnCalendar aggPsnCalendar = calendarMap.get(signCardDate);
			String pk_shift = aggPsnCalendar==null?null:aggPsnCalendar.getPsnCalendarVO().getPk_shift();
			List<SignbVO> signbVOs = createSignBodyVOs(pk_group, pk_org, pk_psndoc, timeDataVO,
					aggPsnCalendar,checkFlag,timeZone,ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,pk_shift).getShiftVO());
			if(!CollectionUtils.isEmpty(signbVOs))
				signbVOList.addAll(signbVOs);

		}
		return signbVOList.size()==0?null:signbVOList.toArray(new SignbVO[0]);
	}

	private List<SignbVO> createSignBodyVOs(String pk_group,String pk_org,String pk_psndoc,
			TimeDataVO timeDataVO,AggPsnCalendar aggPsnCalendar,boolean checkFlag,
			TimeZone timeZone,ShiftVO shiftVO) throws BusinessException{
		//未生成考勤数据，或者没有排班，或者排的是公休，都不处理
		if(timeDataVO == null||aggPsnCalendar==null||ShiftVO.PK_GX.equals(aggPsnCalendar.getPsnCalendarVO().getPk_shift()))
			return null;
		//考勤数据一切正常，也不用处理
		PsnWorkTimeVO[] workVOs = aggPsnCalendar.getPsnWorkTimeVO();
		//按刷卡段处理
		ICompleteCheckTimeScope[] checkTimeScopes = CompleteCheckTimeScopeUtils.mergeWorkTime(workVOs);
		List<SignbVO> result = new ArrayList<SignbVO>();
		for(ICompleteCheckTimeScope checkTimeScope:checkTimeScopes){
			int checkInID = checkTimeScope.getCheckinScopeTimeID();
			int checkOutID = checkTimeScope.getCheckoutScopeTimeID();
			//如果此刷卡段都正常，则不用处理
			boolean isNormal = true;
			for(int i=checkInID;i<=checkOutID;i++){
				if(timeDataVO.getIslate(i)>0||timeDataVO.getIsearly(i)>0||
						timeDataVO.getIsabsent(i)>0||timeDataVO.getIsearlyabsent(i)>0||timeDataVO.getIslateabsent(i)>0){
					isNormal = false;
					break;
				}
			}
			if(isNormal)
				continue;
			//若有异常，则要根据逻辑判断是何种异常，然后用签卡来弥补这个异常
			//上下班时间
			UFDateTime sbTime = timeDataVO.getBegintime(checkInID);
			if(sbTime==null){
				result.add(createSignbVO(pk_group, pk_org, checkTimeScope.getKssj(),
						checkFlag?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, ResHelper.getString("6017dataprocess","06017dataprocess0043")
/*@res "缺上班卡"*/, timeZone));
			}
			else{
				//如果上班点<=规定上班点+最小迟到时长，则视为上班正常，不用额外签卡
				int allowLate = (int) shiftVO.getAllowlateseconds();
				if(sbTime.getMillis()>checkTimeScope.getKssj().getMillis()+allowLate*1000){
					result.add(createSignbVO(pk_group, pk_org, checkTimeScope.getKssj(),
							checkFlag?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, PublicLangRes.LATEIN(), timeZone));
				}
			}
			UFDateTime xbTime = timeDataVO.getEndtime(checkOutID);
			if(xbTime==null){
				result.add(createSignbVO(pk_group, pk_org, checkTimeScope.getJssj(),
						checkFlag?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, ResHelper.getString("6017dataprocess","06017dataprocess0044")
/*@res "缺下班卡"*/, timeZone));
			}
			else{
				//如果下班点>=规定下班点-最小早退时长，则视为上班正常，不用额外签卡
				int allowEarly = (int)shiftVO.getAllowearlyseconds();
				if(xbTime.getMillis()<checkTimeScope.getJssj().getMillis()-allowEarly*1000){
					result.add(createSignbVO(pk_group, pk_org, checkTimeScope.getJssj(),
							checkFlag?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, PublicLangRes.EARLYOUT(), timeZone));
				}
			}
		}
		return result;
	}

	private SignbVO createSignbVO(String pk_group,String pk_org,UFDateTime signTime,int signStatus,String remark,
			TimeZone timeZone){
		SignbVO bvo = new SignbVO();
		bvo.setStatus(VOStatus.NEW);
		bvo.setPk_group(pk_group);
		bvo.setPk_org(pk_org);
		bvo.setSigntime(signTime);
		bvo.setSigndate(DateTimeUtils.toLiteralDate(signTime, timeZone));
		bvo.setSignstatus(signStatus);
		bvo.setSignremark(remark);
		return bvo;
	}

	@Override
	public TimeDataVO[] queryByPsn4PsnSelf(String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean onlyShowException) throws BusinessException {
		return queryByPsn(null, pk_psndoc, beginDate, endDate, onlyShowException);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TimeDataVO[] queryByPks(String[] pks) throws BusinessException {
		if(ArrayUtils.isEmpty(pks))
			return null;
		PKArrayDescriptor descriptor = DailyDataUtils.parsePKs(pks);
		Map<String, Map<UFLiteralDate, String[]>> orgDatePsnMap = descriptor.getOrgDatePsnMap();
		Map<String, Map<String, UFLiteralDate[]>> orgPsnDateMap = descriptor.getOrgPsnDateMap();
		Set<String> orgSet = orgDatePsnMap.keySet();
		//按HR组织循环处理（目前的场景中不会有跨HR组织的情况出现，即此处的org只可能有一个）
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		InSQLCreator isc = null;
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		try{
			isc = new InSQLCreator();
			for(String pk_org:orgSet){
				UFLiteralDate[] dateArray = orgDatePsnMap.get(pk_org).keySet().toArray(new UFLiteralDate[0]);
				Arrays.sort(dateArray);
				UFLiteralDate beginDate = dateArray[0];
				UFLiteralDate endDate = dateArray[dateArray.length-1];
				boolean isDatesContinous = UFLiteralDate.getDaysBetween(beginDate, endDate) == dateArray.length-1;//天是否连续
				String[] psnArray = orgPsnDateMap.get(pk_org).keySet().toArray(new String[0]);
				String psnInSQL = isc.getInSQL(psnArray);
				//查询这么多人这么多天的班次。很有可能会多查，例如，传入的pks里面，第一天有100人，第二天只有1个人。但为了提高效率，减少查询次数，要一次查100人两天的
				String cond=TimeDataVO.PK_PSNDOC+" in("+psnInSQL+") and "+IBaseServiceConst.PK_ORG_FIELD+"=? and ";
				cond += TimeDataVO.CALENDAR;
				if(isDatesContinous)
					cond+=" between ? and ? ";
				else
					cond+=" in ("+StringPiecer.getDefaultPiecesTogether(dateArray)+") ";
				SQLParameter param = new SQLParameter();
				param.addParam(pk_org);
				if(isDatesContinous){
					param.addParam(beginDate);
					param.addParam(endDate);
				}
				TimeDataVO[] vos = CommonUtils.toArray(TimeDataVO.class,(Collection<TimeDataVO>)new BaseDAO().retrieveByClause(TimeDataVO.class, cond, param));
				Map<String, Map<UFLiteralDate, TimeDataVO>> timeDataVOMap = CommonUtils.toMap(TimeDataVO.PK_PSNDOC, TimeDataVO.CALENDAR, vos);
				
				//这些天的工作日历
				Map<String,Map<UFLiteralDate, AggPsnCalendar>> calendarMap = 
						calendarService.queryCalendarVOByCondition4DataProcessView(pk_org, beginDate, endDate, psnInSQL);
				Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
				String[] curPks = descriptor.getOrgPkMap().get(pk_org);
				for(String pk:curPks){//此处以pks为循环，主要是为了保证传入的pks每一条都会有一条对应的vo返回
					String[] parsedPk = DailyDataUtils.parsePK(pk);
					UFLiteralDate date = UFLiteralDate.getDate(parsedPk[1]);
					String pk_psndoc = parsedPk[2];
					String pk_psnjob = parsedPk[3];
					String pk_org_v = parsedPk[4];
					String pk_dept_v = parsedPk[5];
					TimeDataVO timeDataVO = MapUtils.isEmpty(timeDataVOMap)?null:MapUtils.isEmpty(timeDataVOMap.get(pk_psndoc))?null:timeDataVOMap.get(pk_psndoc).get(date);
					if(timeDataVO==null){
						timeDataVO = new TimeDataVO();
						timeDataVO.setPk_org(pk_org);
						timeDataVO.setPk_psndoc(pk_psndoc);
						timeDataVO.setCalendar(date);
						timeDataVO.setDirty_flag(UFBoolean.TRUE);
					}
					retList.add(timeDataVO);
					timeDataVO.setPk_psnjob(pk_psnjob);
					timeDataVO.setPk_dept_v(pk_dept_v);
					timeDataVO.setPk_org_v(pk_org_v);
					AggPsnCalendar calendarVO = MapUtils.isEmpty(calendarMap)?null:MapUtils.isEmpty(calendarMap.get(pk_psndoc))?null:calendarMap.get(pk_psndoc).get(date);
					if(calendarVO==null){
						continue;
					}
					//设置班次信息
					timeDataVO.setPk_shift(calendarVO.getPsnCalendarVO().getPk_shift());
					PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
					timeDataVO.setPsnWorkTimeVOs(wtVOs);//用于界面显示每个工作段的规定上下班时间，已经考虑考勤数据处理时固化的班次
					//是否需要显示中途外出列（如果班次不允许中途外出，则需要显示中途外出列，包含次数和时长子列）
					boolean dispMidOut = false;
					if(!ShiftVO.PK_GX.equals(timeDataVO.getPk_shift())){
						//容错处理，防止shiftMap里面没有此班次
						ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, timeDataVO.getPk_shift()).getShiftVO();
						dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
					}
					timeDataVO.setMidOut(dispMidOut);
				}
			}
		}
		finally{
			if(isc!=null)
				isc.clear();
		}
		TimeDataVO[] retArray = retList.toArray(new TimeDataVO[0]);
		DailyDataUtils.sortByPk(retArray, pks);
		return retArray;
	}

	@Override
	public String[] queryPksByCondDate(String pk_org, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate,boolean onlyShowException)
			throws BusinessException {
		if(beginDate.equals(endDate))
			return queryPksByDate(pk_org, beginDate, fromWhereSQL,onlyShowException);
		String[] opks = queryOnlyPkByCondDate(pk_org, fromWhereSQL, beginDate, endDate, onlyShowException);
//		String[] pKs = DailyDataUtils.getPKs(queryByCondDate(pk_org, fromWhereSQL, beginDate, endDate, onlyShowException));效率优化使用上面的方法
		return opks;
	}

	@Override
	public String[] queryPksByDate(String pk_org, UFLiteralDate date,
			FromWhereSQL fromWhereSQL, boolean onlyShowException)
			throws BusinessException {
		TimeDataVO[] retVOs = queryByDate(pk_org, date, fromWhereSQL, onlyShowException);
		return DailyDataUtils.getPKs(retVOs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TBMPsndocVO[] queryUnGenerateByCondition(LoginContext context,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		//未生成统计权限和考勤档案的修改权限一致
//		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_org_v"+FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_dept_v"+FromWhereSQLUtils.getAttPathPostFix());
		String[] otherTableSelFields = new String[]{orgversionAlias+"."+AdminOrgVersionVO.PK_VID+" as "+TBMPsndocVO.PK_ORG_V,
				deptversionAlias+"."+DeptVersionVO.PK_VID+" as "+TBMPsndocVO.PK_DEPT_V};
		//查出日期范围内日报不完整的人员
		SQLParamWrapper wrapper = TBMPsndocSqlPiecer.selectUnCompleteDailyDataByPsndocFieldAndDateFieldAndDateArea
		(context.getPk_org(), new String[]{TBMPsndocVO.PK_PSNDOC,TBMPsndocVO.PK_PSNJOB},
				otherTableSelFields, "tbm_timedata timedata", "timedata.pk_org", "timedata.pk_psndoc",
		"timedata.calendar", beginDate.toString(), endDate.toString(), "filtertbmpsndoc", "timedata.dirty_flag='N'", fromWhereSQL); 
		String sql = wrapper.getSql();
		SQLParameter para = wrapper.getParam();
		return CommonUtils.toArray(TBMPsndocVO.class, (List<TBMPsndocVO>)new BaseDAO().executeQuery(sql, para, new BeanListProcessor(TBMPsndocVO.class)));
	}
	
	private Map<String, AggPsnCalendar> getStringCalendarMap(Map<UFLiteralDate, AggPsnCalendar> map){
		Map<String, AggPsnCalendar> stringMap = new HashMap<String, AggPsnCalendar>();
		if(MapUtils.isNotEmpty(map)){
			for(UFLiteralDate date:map.keySet()){
				stringMap.put(date.toString(),map.get(date));
			}
		}
		return stringMap;
	}
	private Map<String, String> getStringOrgMap(Map<UFLiteralDate, String> map){
		Map<String, String> stringMap = new HashMap<String, String>();
		if(MapUtils.isNotEmpty(map)){
			for(UFLiteralDate date:map.keySet()){
				stringMap.put(date.toString(),map.get(date));
			}
		}
		return stringMap;
	}

	@Override
	public TimeDataVO[] queryByPsnAndDept4Mgr(String pk_dept, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean onlyShowException,boolean containsSubDepts) throws BusinessException {
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocQuerySQL(pk_psndoc);
		return queryByCondDateAndDept(fromWhereSQL, beginDate, endDate, onlyShowException, pk_dept, containsSubDepts);
	}

	@Override
	public TimeDataVO[] queryByCondDateAndDeptShowExceptionAll(
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_dept, boolean containsSubDepts)
			throws BusinessException {
		fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		//添加机器考勤过滤条件
		fromWhereSQL = TBMPsndocSqlPiecer.addPropCond(fromWhereSQL, TBMPsndocVO.TBM_PROP_MACHINE);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		//查询出日期范围内的所有考勤档案记录，日期范围内最新的一条作为条件匹配，然后找出人员在日期范围内所有的考勤档案记录
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.
			queryTBMPsndocMapByConditionForTeam(null, fromWhereSQL, beginDate, endDate, true,true,null);
		if(psndocMap==null||psndocMap.isEmpty())
			return null;
		String pk_hrorg = null;
		for(String psndocPk:psndocMap.keySet()){
			pk_hrorg = psndocMap.get(psndocPk).get(0).getPk_org();
			break;
		}		
//		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(null);//大数据环境查询的数据量太大
		InSQLCreator isc = new InSQLCreator();
		String inSQL = isc.getInSQL(psndocMap.keySet().toArray(new String[0]));		
		String shiftPkQrySql = "select distinct(pk_shift) from tbm_psncalendar where pk_psndoc in (" +inSQL+ ") and calendar between '" + beginDate+"' and '" +endDate+"'";
		GeneralVO[] shiftPk = (GeneralVO[]) new BaseDAO().executeQuery(shiftPkQrySql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		String[] pk_shifts = null;
		if(shiftPk != null){
			pk_shifts = new String[shiftPk.length];
			for(int i=0;i<shiftPk.length;i++){
				pk_shifts[i] = shiftPk[i].toString();
			}
		}	
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryShiftAggVOByPkArray(pk_shifts);
		Map<String, AggShiftVO> shiftMap = new HashMap<String,AggShiftVO>();
		if(shiftVOs != null){
			for(AggShiftVO shiftVO:shiftVOs){
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(), shiftVO);
			}
		}
		Map<String, TimeZone> timeZoneMap = new HashMap<String, TimeZone>();
		TimeDataVO[] vos = null;
		//这些人这一天的工作日历
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap =null;
		//然后查询这些人员在这些天的timedata记录
		String cond = TBMPsndocVO.PK_PSNDOC+" in ("+inSQL+") and "+TimeDataVO.CALENDAR+" between ? and ? ";
		SQLParameter param = new SQLParameter();
		param.addParam(beginDate.toString());
		param.addParam(endDate.toString());
		vos = CommonUtils.retrieveByClause(TimeDataVO.class, cond, param);
		calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
			queryCalendarVOByCondition4DataProcessView(pk_hrorg, beginDate, endDate, inSQL,timeZoneMap,shiftMap);
		// 构造已有考勤记录Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		//可能有些人还没生成这一天的timedata记录，需要在内存中new一条(不是真的生成)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		//有异常数据的人员
		Set<String> exceptionPsn = new HashSet<String>();
		//按人按天构造记录
		for(String pk_psndoc:psndocMap.keySet()){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(pk_psndoc);
			List<TBMPsndocVO> tbmdocList = psndocMap.get(pk_psndoc);
			if(CollectionUtils.isEmpty(tbmdocList))
				continue;
			for(TBMPsndocVO psndocVO:tbmdocList){
				//只对考勤档案范围和起止日期交集的时间构造数据
				UFLiteralDate begin = psndocVO.getBegindate().before(beginDate)?beginDate:psndocVO.getBegindate();
				UFLiteralDate end = psndocVO.getEnddate().before(endDate)?psndocVO.getEnddate():endDate;
				if(begin.after(end))
					continue;
				UFLiteralDate[] allDates = CommonUtils.createDateArray(begin, end);
				for(UFLiteralDate date:allDates){
					TimeDataVO vo = dateMap==null?null:dateMap.get(date);
					if(vo==null){
						// 没有记录时在内存中new一条
						vo = new TimeDataVO();
						vo.setPk_org(psndocVO.getPk_org());
						vo.setPk_psndoc(psndocVO.getPk_psndoc());
						vo.setCalendar(date);
						vo.setDirty_flag(UFBoolean.TRUE);
					}
					// 计算属性赋值
					vo.setPk_psnjob(psndocVO.getPk_psnjob());
					vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
					vo.setPk_org_v(psndocVO.getPk_org_v());
					vo.setPk_dept_v(psndocVO.getPk_dept_v());
					
					AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(psndocVO.getPk_psndoc())==null?null:
							calendarMap.get(psndocVO.getPk_psndoc()).get(date);
					//设置班次信息
					if(calendarVO!=null){
						PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
						vo.setPk_shift(psnCalendarVO.getPk_shift());
						if(!vo.isNormal())
							exceptionPsn.add(pk_psndoc);
						PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
						vo.setPsnWorkTimeVOs(wtVOs);//用于界面显示每个工作段的规定上下班时间，已经考虑考勤数据处理时固化的班次
						//是否需要显示中途外出列（如果班次不允许中途外出，则需要显示中途外出列，包含次数和时长子列）
						boolean dispMidOut = false;
						if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
							//容错处理，防止shiftMap里面没有此班次
							ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift()).getShiftVO();
							dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
						}
						vo.setMidOut(dispMidOut);
					}
					vo.trimMemory();//数据量很大时，服务器会outofmemory，因此此处将UFDouble处理了一下，0值都用null
					retList.add(vo);
				}
			}
		}
		if(CollectionUtils.isEmpty(exceptionPsn)||CollectionUtils.isEmpty(retList))
			return null;
		//找出含有异常人员的数据
		List<TimeDataVO> returnList = new ArrayList<TimeDataVO>();
		for(TimeDataVO vo:retList){
			if(exceptionPsn.contains(vo.getPk_psndoc()))
				returnList.add(vo);
		}
		if(CollectionUtils.isEmpty(returnList))
			return null;
		return returnList.toArray(new TimeDataVO[0]);
		
	}
}