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
		//�������ɿ����� ������Ȩ�޺Ϳ��ڵ�����ά��Ȩ��һ��
		if(!StringUtils.isEmpty(PubEnv.getPk_user()))//��̨����ȡ�����û�����̨���񲻼�Ȩ��
			fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		//��ѯ������Ҫ�������Ա
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		String[] pk_psndocs = psndocService.queryMachineDocByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if(ArrayUtils.isEmpty(pk_psndocs))//���û�в�ѯ����Ա����ֱ�ӷ���
			return;
//		String[] pk_psndocs = SQLHelper.getStrArray(psndocVOs, TBMPsndocVO.PK_PSNDOC);
//		//��������ֹ�����û�ͬʱ����һ����
//		//����Щ�˵��ձ���ס����ϸӦ���������죬����������������̫�����п��ǣ�ֻ����+��֯��
//		PKLock lock = PKLock.getInstance();
//		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
//		for(int i=0;i<pk_psndocs.length;i++){
//			pk_psndocsLockacble[i]= "timedata"+pk_org+pk_psndocs[i];
//		}
//		try{
//			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
//			if(!acquired)
//				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0055")
//				/*@res "�����������ɿ������ݣ����Ժ�����!"*/);
//			new TimeDataServiceImpl().createTimeDataRecord(pk_org, fromWhereSQL, beginDate, endDate);
//			//Ȼ����м���
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
//		//����Щ�˵��ձ���ס����ϸӦ���������죬����������������̫�����п��ǣ�ֻ����+��֯��
//		PKLock lock = PKLock.getInstance();
//		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
//		for(int i=0;i<pk_psndocs.length;i++){
//			pk_psndocsLockacble[i]= "timedata"+pk_org+pk_psndocs[i];
//		}
//		try{
//			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
//			if(!acquired)
//				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0055")
//						/*@res "�����������ɿ������ݣ����Ժ�����!"*/);
//			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
//			new TimeDataServiceImpl().createTimeDataRecord(pk_org, fromWhereSQL, beginDate, endDate);
//			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
//			//Ȼ����м���
//			generate0(pk_org, pk_psndocs, beginDate, endDate);
//		}
//		finally{
//			lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
//		}
		generateAll(pk_org, pk_psndocs, beginDate, endDate);
	}
	
	/**
	 * �еĿͻ�����Ա�ǳ��࣬��������ʱ���������ܴ󣬵����ڴ�������ߴﵽ�����ݿ��ѯ�����ƣ�������������ʧ��
	 * �ڴ˽��з�������ÿ1000��ִ��һ��
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
		//MOD �����߶��̣߳���ʱ��500�ĳ�3000�����̵߳�������ʧ�� James
		if(length<3000){
			generateOnce(pk_org, pk_psndocs, beginDate, endDate);
			return;
		}
		new TimeDataGenerateThreadPool().doTaskWithThread(pk_org,pk_psndocs, beginDate, endDate);
	}
	
	/**
	 * ����ִ���е�һ�δ�����
	 * @param pk_org
	 * @param pk_psndocs ��Ա������һǧ��
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	@Override
	public void generateOnce(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate)throws BusinessException {
		if(ArrayUtils.isEmpty(pk_psndocs))
			return;
		//����Щ�˵��ձ���ס����ϸӦ���������죬����������������̫�����п��ǣ�ֻ����+��֯��
		PKLock lock = PKLock.getInstance();
		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
		for(int i=0;i<pk_psndocs.length;i++){
			pk_psndocsLockacble[i]= "timedata"+pk_org+pk_psndocs[i];
		}
		try{
			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
			if(!acquired)
				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0055")
						/*@res "�����������ɿ������ݣ����Ժ�����!"*/);
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			new TimeDataServiceImpl().createTimeDataRecord(pk_org, fromWhereSQL, beginDate, endDate);
			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
			//Ȼ����м���
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
			//��Щ��Ա�����ڷ�Χ�ڵ����п��ڵ�����vo��key����Ա��������
			para.allTbmPsndocVOListMap = psndocService.queryTBMPsndocMapByPsndocs(pk_org, para.pk_psndocs, beginDate, endDate, true, true,TbmPropEnum.MACHINE_CHECK);
			//V6.1���ӣ���Щ��Ա��ÿһ�����ְ��֯
			para.allDateOrgMap=TBMPsndocVO.createDateOrgMapByTbmPsndocVOMap(para.allTbmPsndocVOListMap, beginDate, endDate);
			//���е�ˢǩ����¼
			para.allCheckTimes = CheckTimeServiceFacade.queryCheckTimesByPsnsAndDateScope(pk_org, para.pk_psndocs, beginDate, endDate);
			//���е��ݼټ�¼
			para.leaveMap = LeaveServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//���еĲ���ټ�¼
			para.lactationMap = LeaveServiceFacade.queryAllLactationVOIncEffictiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//���еĳ����¼
			para.awayMap = AwayServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//���еļӰ��¼
			para.overMap = OverTimeServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//���е�ͣ�����ϼ�¼
			para.shutMap = ShutdownServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
			//���еĹ�������
			//para.calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1), psndocInSQL);
			para.calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQLForProcess(pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1), pk_psndocs);
		}
		finally{
			isc.clear();
		}
		//���еĹ������ռ�¼
		IHRHolidayQueryService hrholidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		para.orgHolidayVOMap = hrholidayService.queryHolidayVOsByHROrg(pk_org, beginDate, endDate);
		if(MapUtils.isEmpty(para.orgHolidayVOMap)){
			para.holidayVOs = CommonUtils.mapVal2Array(HRHolidayVO.class, para.orgHolidayVOMap);
		}
		//���еļ����������
		if(!MapUtils.isEmpty(para.orgHolidayVOMap))
			para.holidayEnjoyMap = hrholidayService.queryHolidayEnjoyInfo2(pk_org, para.pk_psndocs, para.orgHolidayVOMap);
		//���еİ��
		para.shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		para.timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryAllMap(pk_org);
		AllParams allParams = NCLocator.getInstance().lookup(IAllParamsQueryService.class).queryByOrg(pk_org);
		//���ڹ���
		para.timeRuleVO=allParams.getTimeRuleVO();
		para.paraValueMap = allParams.getParValueMap();
		processSysCard(para);
		//��������ǩ���Ҽ�Ϊȫ�ڵ�����
		processSigninOut(para);
		generateByParams(para);
		//ҵ����־
//		TaBusilogUtil.writeDataProcessGenerate(pk_org, pk_psndocs, beginDate, endDate);
		dataProcessLog(pk_org, pk_psndocs, beginDate, endDate);
	}
	
	/**
	 *��¼��־�����̣߳���Ӱ��������Ӧʱ��
	 * @param psnInSql
	 */
	private void dataProcessLog(final String pk_org, final String[] pk_psndocs, final UFLiteralDate beginDate,final UFLiteralDate endDate){
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// �߳��л�����Ϣ�ᶪʧ������������һ��
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				TaBusilogUtil.writeDataProcessGenerate(pk_org, pk_psndocs, beginDate, endDate);
			}
		}).start();
	}
	
	
	/**
	 * �����ƶ�ǩ������ǩ������
	 * @param para
	 * @throws BusinessException
	 */
	private void processSigninOut(CalParam para) throws BusinessException {
		
		//�ƶ�ǩ������ƿ���ǩ��ǩ����ʶ
		if(para.timeRuleVO.getCheckinflag().booleanValue())
			return;
		BaseDAO baseDAO = new BaseDAO();
//		//�ж��Ƿ����ƶ�ǩ���ı�
//		String sign_sql = " select * from sysobjects where name like 'hr_signin' ";
//		GeneralVO[] cvos = (GeneralVO[]) baseDAO.executeQuery(sign_sql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
//		if(ArrayUtils.isEmpty(cvos)){//û�а�װ�ƶ�ǩ���ı�
//			return;
//		}
		
		String[] pk_psndocs = para.pk_psndocs; 
		UFLiteralDate beginDate = para.beginDate;
		UFLiteralDate endDate = para.endDate;
		
		//��ѯ��Ҫ���������ǩ������
		String outSql = " select pk_psndoc,recordtime from hr_signin where pk_psndoc in (" +new InSQLCreator().getInSQL(pk_psndocs)+") " +
				" and pk_org = '"+para.pk_org+"' and (recordtime between '"+beginDate.toString()+"' and '"+endDate.getDateAfter(1).toString()+"' ) " +
				" and recordtype = 'out' and approvestate = 1 and daynormal = 'Y'";
		GeneralVO[] gvos = null;
		try {
			gvos = (GeneralVO[]) baseDAO.executeQuery(outSql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		} catch (Exception e) {//����û�������ƶ�ǩ������
			Logger.error(e.getMessage(),e);
			return;
		}
		if(ArrayUtils.isEmpty(gvos)){//û����Ҫ���������ǩ������
			return;
		}
		
		//��ǩ������ ������Ա�����ڷ���
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
		for(int i=0;i<pk_psndocs.length;i++) { //����ѭ��
			String pk_psndoc = pk_psndocs[i];
			Map<String, List<UFDateTime>> dateMap = psnSignMap.get(pk_psndoc);
			if(MapUtils.isEmpty(dateMap)){
				continue;
			}
			for(UFLiteralDate date:dates) { //������ѭ��
				List<UFDateTime> signTimeList = dateMap.get(date.toString());
				if(CollectionUtils.isEmpty(signTimeList)){
					continue;
				}
				// û�й�������������
				if(para.calendarMap==null || para.calendarMap.get(pk_psndoc)==null || para.calendarMap.get(pk_psndoc).get(date)==null)
					continue;
				AggPsnCalendar psnCalendar = para.calendarMap.get(pk_psndoc).get(date);
				AggShiftVO aggShift = ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,psnCalendar.getPsnCalendarVO().getPk_shift());
				// û���ҵ������Ϣ������
				if(aggShift==null)
					continue;
				PsnWorkTimeVO[] wtVOs = psnCalendar.getPsnWorkTimeVO();
				if(ArrayUtils.isEmpty(wtVOs)) // û�й���ʱ��β������磺������ȫ���ǣ�
					continue;
				
				List<TBMPsndocVO> psndocVOs = para.allTbmPsndocVOListMap.get(pk_psndoc);
				TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocVOs, date.toStdString());
				if(psndocVO==null) // û���ҵ����ڵ���
					continue;
				
				Map<String, TimeZone> orgTimeZoneMap = para.timeRuleVO.getTimeZoneMap();
				ICheckTime[] yuancheckTimes = para.allCheckTimes[i];
				List<SysCardVO> outSysCardList = new ArrayList<SysCardVO>();
				//������ǩ�����ݼӽ���
				for(UFDateTime time : signTimeList){
					SysCardVO signOut = createSysCardVO(psndocVO, time,ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap);
					cardList.add(signOut);
					outSysCardList.add(signOut);
				}
				//ԭ����ˢ���������������ݺϲ�
				if(!ArrayUtils.isEmpty(yuancheckTimes)){
					CollectionUtils.addAll(outSysCardList, yuancheckTimes);
				}
				ICheckTime[] allCheckTimes = outSysCardList.toArray(new ICheckTime[0]);
				//��������Ч�Ŀ�����
				Arrays.sort(allCheckTimes);
				// ȡˢ��ʱ����ڵ�ˢ������
				ITimeScope kqScope = aggShift.getShiftVO().toKqScope(date.toStdString(), para.timeRuleVO.getTimeZone());
				allCheckTimes = CheckTimeUtils.filterOrderedTimesYY(allCheckTimes, kqScope.getScope_start_datetime(), kqScope.getScope_end_datetime());
				
				//������ˢ��ʱ����ж��Ƿ���Ҫ����
				for(PsnWorkTimeVO wtvo:wtVOs){
					if(wtvo.getCheckinflag().booleanValue()){//�Ƿ���Ҫˢ�ϰ࿨
						//ˢ�����Ƿ����ϰ࿨,û���ϰ࿨�򲹿�
						ICheckTime[] Bchecktimes = CheckTimeUtils.filterOrderedTimesYY(allCheckTimes, wtvo.getKsfromtime(),wtvo.getKssj());
						if(ArrayUtils.isEmpty(Bchecktimes)){
							SysCardVO invo = createSysCardVO(psndocVO, wtvo.getKssj(),ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap);
							cardList.add(invo);
						}
					}
					if(wtvo.getCheckoutflag().booleanValue()){// �Ƿ���ѽˢ�°࿨
						//ˢ�����Ƿ����°࿨
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
		// �������ɵ�ϵͳˢ������ 
		SysCardVO[] sysCardVOs = NCLocator.getInstance().lookup(ISysCardManageService.class).saveSysCardVOs(cardList, psnScope);
		if(ArrayUtils.isEmpty(sysCardVOs))
			return;
		Map<String, List<SysCardVO>> sysCardMap = CommonUtils.group2ListByField(SysCardVO.PK_PSNDOC, sysCardVOs);
		// ��ϵͳˢ�����ݴ���para
		for(int i=0;i<pk_psndocs.length;i++){
			List<SysCardVO> psnSysCards = sysCardMap.get(pk_psndocs[i]);
			if(CollectionUtils.isEmpty(psnSysCards))
				continue;
			ICheckTime[] oldCheckTimes = para.allCheckTimes[i];
			if(!ArrayUtils.isEmpty(oldCheckTimes)){
				CollectionUtils.addAll(psnSysCards, oldCheckTimes);
			}
			ICheckTime[] newCheckTimes = psnSysCards.toArray(new ICheckTime[0]);
			// ����
			Arrays.sort(newCheckTimes);
			para.allCheckTimes[i] = newCheckTimes;
		}
	}
	
	/**
	 * ����ϵͳˢ������ 
	 * @param para
	 * @throws BusinessException
	 */
	private void processSysCard(CalParam para) throws BusinessException {
		String[] pk_psndocs = para.pk_psndocs; 
		UFLiteralDate beginDate = para.beginDate;
		UFLiteralDate endDate = para.endDate;
		UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
		List<SysCardVO> cardList = new ArrayList<SysCardVO>();
		for(int i=0;i<pk_psndocs.length;i++) { //����ѭ��
			String pk_psndoc = pk_psndocs[i];
			for(UFLiteralDate date:dates) { //������ѭ��
				// û�й�������������
				if(para.calendarMap==null || para.calendarMap.get(pk_psndoc)==null || para.calendarMap.get(pk_psndoc).get(date)==null)
					continue;
				AggPsnCalendar psnCalendar = para.calendarMap.get(pk_psndoc).get(date);
				AggShiftVO aggShift = ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,psnCalendar.getPsnCalendarVO().getPk_shift());
				// û���ҵ������Ϣ���β�֧�ֵ���ˢ��������
				if(aggShift==null || !aggShift.getShiftVO().isSingleCard())
					continue;
				PsnWorkTimeVO[] wtVOs = psnCalendar.getPsnWorkTimeVO();
				if(ArrayUtils.isEmpty(wtVOs)) // û�й���ʱ��β������磺������ȫ���ǣ�
					continue;
				UFDateTime earliesStartTime = TimeScopeUtils.getEarliesStartTime(wtVOs);
				UFDateTime latestEndTime = TimeScopeUtils.getLatestEndTime(wtVOs);
				List<TBMPsndocVO> psndocVOs = para.allTbmPsndocVOListMap.get(pk_psndoc);
				TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocVOs, date.toStdString());
				if(psndocVO==null) // û���ҵ����ڵ���
					continue;
				ICheckTime[] checkTimes = para.allCheckTimes[i];
				// ȡˢ��ʱ����ڵ�ˢ������
				ITimeScope kqScope = aggShift.getShiftVO().toKqScope(date.toStdString(), para.timeRuleVO.getTimeZone());
				checkTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes, kqScope.getScope_start_datetime(), kqScope.getScope_end_datetime());
				if(ArrayUtils.isEmpty(checkTimes)) //û��ˢǩ��������
					continue;
				boolean isCheckinout = para.timeRuleVO.getCheckinflag().booleanValue(); //�Ƿ������Ž�
				Map<String, TimeZone> orgTimeZoneMap = para.timeRuleVO.getTimeZoneMap();
//				PsnWorkTimeVO wtVO = wtVOs[0];
				int cardType = aggShift.getShiftVO().getCardtype();
//				if(!(SingleCardTypeEnum.ONLYBEGIN.toIntValue()==cardType)) // ����ֻˢ�ϰ࿨������Ҫ�����ϰ࿨
//					cardList.add(createSysCardVO(psndocVO, wtVO.getScope_start_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
//				if(!(SingleCardTypeEnum.ONLYEND.toIntValue()==cardType))   // ����ֻˢ�°࿨������Ҫ�����°࿨
//					cardList.add(createSysCardVO(psndocVO, wtVO.getScope_end_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
				//��������ˢǩ������������ǩ�������ǣ��
				ICheckTime earliesIn = null;
				ICheckTime latestOut = null;
				if(isCheckinout){
					earliesIn = CheckTimeUtilsWithCheckFlag.findOrderedEarliest(checkTimes, ICheckTime.CHECK_FLAG_IN);
					latestOut = CheckTimeUtilsWithCheckFlag.findOrderedLatest(checkTimes, ICheckTime.CHECK_FLAG_OUT);
				}else{
					earliesIn = checkTimes[0];
					latestOut = checkTimes[checkTimes.length-1];
				}
				//��ֻˢ�ϰ࿨�������Ƿ���Ҫ���°࿨
				if(SingleCardTypeEnum.ONLYBEGIN.toIntValue()==cardType){
					//ȱ���������°࿨
//					if(checkTimes[checkTimes.length-1].getDatetime().before(wtVO.getScope_end_datetime())) {
//						cardList.add(createSysCardVO(psndocVO, wtVO.getScope_end_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					if(latestOut==null||latestOut.getDatetime().before(latestEndTime)){
						cardList.add(createSysCardVO(psndocVO, latestEndTime, isCheckinout?ICheckTime.CHECK_FLAG_OUT:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					}
				}
				//��ֻˢ�°࿨�����Ƿ���Ҫ���ϰ࿨
				if(SingleCardTypeEnum.ONLYEND.toIntValue()==cardType) {
					//ȱ���������ϰ࿨
//					if(checkTimes[0].getDatetime().after(wtVO.getScope_start_datetime())) {
//						cardList.add(createSysCardVO(psndocVO, wtVO.getScope_start_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
//					}
					if(earliesIn == null||earliesIn.getDatetime().after(earliesStartTime)) {
						cardList.add(createSysCardVO(psndocVO, earliesStartTime, isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					}
				}
				//������ʱ���ˢ���������Ƿ��ϡ��°࿨
				if(SingleCardTypeEnum.ANYTIME.toIntValue()==cardType){
					//ȱ���������ϰ࿨
//					if(checkTimes[0].getDatetime().after(wtVO.getScope_start_datetime())) {
//						cardList.add(createSysCardVO(psndocVO, wtVO.getScope_start_datetime(), isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
//					}
					if(earliesIn == null||earliesIn.getDatetime().after(earliesStartTime)) {
						cardList.add(createSysCardVO(psndocVO, earliesStartTime, isCheckinout?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, orgTimeZoneMap));
					}
					//ȱ���������°࿨
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
		// �������ɵ�ϵͳˢ������ 
		SysCardVO[] sysCardVOs = NCLocator.getInstance().lookup(ISysCardManageService.class).saveSysCardVOs(cardList, psnScope);
		if(ArrayUtils.isEmpty(sysCardVOs))
			return;
		Map<String, List<SysCardVO>> sysCardMap = CommonUtils.group2ListByField(SysCardVO.PK_PSNDOC, sysCardVOs);
		// ��ϵͳˢ�����ݴ���para
		for(int i=0;i<pk_psndocs.length;i++){
			List<SysCardVO> psnSysCards = sysCardMap.get(pk_psndocs[i]);
			if(CollectionUtils.isEmpty(psnSysCards))
				continue;
			ICheckTime[] oldCheckTimes = para.allCheckTimes[i];
			CollectionUtils.addAll(psnSysCards, oldCheckTimes);
			ICheckTime[] newCheckTimes = psnSysCards.toArray(new ICheckTime[0]);
			// ����
			Arrays.sort(newCheckTimes);
			para.allCheckTimes[i] = newCheckTimes;
		}
	}
	
	/**
	 * ����ϵͳˢ������ 
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
		//�ĸ�����ӿڣ����°�ʱ��㡢ʱ���post���������ݡ���������post
		ITimePointProcessor timePointProcessor = (ITimePointProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEPOINT_CHECKFLAG:ICommonConst.ITF_CODE_TIMEPOINT_NOCHECKFLAG);
		ITimePointPostProcessor timePointPostProcessor =(ITimePointPostProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEPOINT_POST_CHECKFLAG:ICommonConst.ITF_CODE_TIMEPOINT_POST_NOCHECKFLAG);
		ITimeDataProcessor timeDataProcessor = (ITimeDataProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEDATA_CHECKFLAG:ICommonConst.ITF_CODE_TIMEDATA_NOCHECKFLAG);
		ITimeDataPostProcessor timeDataPostProcessor = (ITimeDataPostProcessor)devItfService.queryByCode(isCheckFlag?ICommonConst.ITF_CODE_TIMEDATA_POST_CHECKFLAG:ICommonConst.ITF_CODE_TIMEDATA_POST_NOCHECKFLAG);
		String[] pk_psndocs = para.pk_psndocs;//������Ҫ�������Ա

		TimeDataCalParam calParam = new TimeDataCalParam();
		calParam.timeruleVO = para.timeRuleVO;//���ڹ���
		calParam.workLenMinusItemMap=TimeDataCalRuleConfig.parseMinusItem(para.timeRuleVO.getWorklenminusitems());
		calParam.paramValues = para.paraValueMap;//���в�����ֵ
		calParam.timeitemMap = para.timeitemMap; // �����ݼټӰ����
		//���е����ڣ���ǰ��һ�죬����Ҳ��һ�죩
		UFLiteralDate[] allDateBefore1AndAfter1 = CommonUtils.createDateArray(para.beginDate, para.endDate, 1, 1);
//		Map<String, ITimeScope> fullDayMap = new HashMap<String, ITimeScope>();//�洢ÿһ���0�㵽23:59:59��scope��map��key�����ڣ���ǰ����ã�ѭ������Ͳ�Ҫ�ظ�������
//		for(UFLiteralDate date:allDateBefore1AndAfter1){
//			fullDayMap.put(date.toString(), TimeScopeUtils.toFullDay(date.toString(), para.timeRuleVO.getTimeZone()));
//		}
		//��Ҫ���µ�timedatavo
		List<TimeDataVO> updateVOList = new ArrayList<TimeDataVO>();
		//����ڼ���Ĺ����У����԰౻�̻��ˣ��򽫹̻��Ľ��д�빤���������ӱ����ڿ������ݴ��������ʾ����ʱ��εĹ涨���°�ʱ��
		List<PsnWorkTimeVO> insertSolidifyWorkVOList = new ArrayList<PsnWorkTimeVO>();
		//��¼��Ҫɾ�����������ӱ�Ĺ�����������������list
		List<String> delWorkVOList = new ArrayList<String>();
		//��Ҫupdate���̻�����ʱ��Ρ��ֶε�����vo
		List<PsnCalendarVO> updatePsnCalendarList = new ArrayList<PsnCalendarVO>();
		for(int i=0;i<pk_psndocs.length;i++){//��������ѭ��
			String pk_psndoc = pk_psndocs[i];//��Ա����
			ICheckTime[] checkTimes = para.allCheckTimes[i];//�������е�ˢǩ����¼
			List<TBMPsndocVO> psndocVOList = para.allTbmPsndocVOListMap==null?null:para.allTbmPsndocVOListMap.get(pk_psndoc);//��Ա�Ŀ��ڵ���
			LeaveRegVO[] leaveRegVOs = para.leaveMap==null?null:para.leaveMap.get(pk_psndoc);//��Ա���ݼټ�¼
			LeaveRegVO[] lactationRegVOs = para.lactationMap==null?null:para.lactationMap.get(pk_psndoc);//��Ա�Ĳ���ټ�¼
			AwayRegVO[] awayRegVOs = para.awayMap==null?null:para.awayMap.get(pk_psndoc);//��Ա�ĳ����¼
			OvertimeRegVO[] overRegVOs = para.overMap==null?null:para.overMap.get(pk_psndoc);//��Ա�ļӰ��¼
			ShutdownRegVO[] shutRegVOs = para.shutMap==null?null:para.shutMap.get(pk_psndoc);//��Ա��ͣ�����ϼ�¼
//			Map<UFLiteralDate, AggPsnCalendar> calendarMap = para.calendarMap==null?null:para.calendarMap.get(pk_psndoc);//��Ա���Ű�
			//�����map���ں�̨��������ʱ�޷�ʹ�ã�ԭ����UFLiteralDate��hashcode��һ��ȡ����ֵ
			Map<String, AggPsnCalendar> calendarMap = getStringCalendarMap(para.calendarMap==null?null:para.calendarMap.get(pk_psndoc));//��Ա���Ű�
			Map<String, TimeDataVO> timeDataMap = para.timeDataMap.get(pk_psndoc);//���˵�����timedatavo
//			Map<UFLiteralDate, String> dateOrgMap = para.allDateOrgMap.get(pk_psndoc);//�������������ְ��֯
			Map<String, String> dateOrgMap = getStringOrgMap(para.allDateOrgMap.get(pk_psndoc));//�������������ְ��֯
			
			//���水�촦��
			for(int dateIndex = 1;dateIndex<allDateBefore1AndAfter1.length-1;dateIndex++){//����������ǰ����๹�������������ǰ�����첻�ô���
				UFLiteralDate curDate = allDateBefore1AndAfter1[dateIndex];
				TBMPsndocVO tbmPsndocVO = TBMPsndocVO.findIntersectionVO(psndocVOList, curDate.toString());
				//��������޿��ڵ��������߲��ǻ������ڣ����ô���
				if(tbmPsndocVO==null||tbmPsndocVO.getTbm_prop().intValue()!=TBMPsndocVO.TBM_PROP_MACHINE)
					continue;
				calParam.psndocVO=tbmPsndocVO;
				//����ǰһ���һ��İ�Σ��Լ���������
				AggPsnCalendar curAggCalendar = calendarMap==null?null:calendarMap.get(curDate.toString());
				AggShiftVO curAggShift = curAggCalendar==null?null:para.shiftMap==null?null:ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,curAggCalendar.getPsnCalendarVO().getPk_shift());
				AggPsnCalendar preAggCalendar = calendarMap==null?null:calendarMap.get(allDateBefore1AndAfter1[dateIndex-1].toString());
				AggShiftVO preAggShift = preAggCalendar==null?null:para.shiftMap==null?null:ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,preAggCalendar.getPsnCalendarVO().getPk_shift());
				AggPsnCalendar nextAggCalendar = calendarMap==null?null:calendarMap.get(allDateBefore1AndAfter1[dateIndex+1].toString());
				AggShiftVO nextAggShift = nextAggCalendar==null?null:para.shiftMap==null?null:ShiftServiceFacade.getAggShiftVOFromMap(para.shiftMap,nextAggCalendar.getPsnCalendarVO().getPk_shift());
				TimeZone curTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(curDate.toString())));
				TimeZone preTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(allDateBefore1AndAfter1[dateIndex-1].toString())));
				TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calParam.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(allDateBefore1AndAfter1[dateIndex+1].toString())));
				//����ʱ���
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
//				calParam.previousNatualDayScope=fullDayMap.get(allDateBefore1AndAfter1[dateIndex-1].toString());//ǰһ����Ȼ�յ�scope��������Ȼ���ڹ���ʱ������
//				calParam.curNatualDayScope=fullDayMap.get(curDate.toString());//������Ȼ�յ�scope��������Ȼ���ڹ���ʱ������
//				calParam.nextNatualDayScope=fullDayMap.get(allDateBefore1AndAfter1[dateIndex+1].toString());//��һ����Ȼ�յ�scope��������Ȼ���ڹ���ʱ������
				calParam.previousNatualDayScope=TimeScopeUtils.toFullDay(allDateBefore1AndAfter1[dateIndex-1].toString(), preTimeZone);//ǰһ����Ȼ�յ�scope��������Ȼ���ڹ���ʱ������
				calParam.curNatualDayScope=TimeScopeUtils.toFullDay(curDate.toString(), curTimeZone);//������Ȼ�յ�scope��������Ȼ���ڹ���ʱ������
				calParam.nextNatualDayScope=TimeScopeUtils.toFullDay(allDateBefore1AndAfter1[dateIndex+1].toString(), nextTimeZone);//��һ����Ȼ�յ�scope��������Ȼ���ڹ���ʱ������
				calParam.preTimeZone=preTimeZone;
				calParam.curTimeZone=curTimeZone;
				calParam.nextTimeZone=nextTimeZone;
				calParam.pk_joborg=dateOrgMap.get(curDate.toDate());
				calParam.checkTimes = DataFilterUtils.filterCheckTimes(kqScope, checkTimes);//���յ�ˢǩ����¼
				calParam.awayBills=DataFilterUtils.filterRegVOs(kqScope, awayRegVOs);
				calParam.leaveBills=DataFilterUtils.filterRegVOs(kqScope, leaveRegVOs);
				calParam.lactationholidayVO=DataFilterUtils.filterDateScopeVO(curDate.toString(), lactationRegVOs);
				calParam.overtimeBills=DataFilterUtils.filterRegVOs(kqScope, overRegVOs);
				calParam.shutdownBills=DataFilterUtils.filterRegVOs(kqScope, shutRegVOs);
				calParam.mergeLASScopes = TimeScopeUtils.mergeTimeScopes(TimeScopeUtils.mergeTimeScopes(calParam.awayBills, calParam.leaveBills),calParam.shutdownBills);
				calParam.holidayVOs = DataFilterUtils.filterHolidayVOs(pk_psndoc,calParam.pk_joborg, kqScope, para.holidayEnjoyMap, para.holidayVOs);
				TimeDataVO timeDataVO = timeDataMap.get(curDate.toString());
				// ����δ���ɱ�ʶΪ������
				timeDataVO.setDirty_flag(UFBoolean.FALSE);
				updateVOList.add(timeDataVO);
				//�����ݽ������Ĵ���
				timePointProcessor.process(calParam, timeDataVO);
				if(timePointPostProcessor!=null)
					timePointPostProcessor.postProcess(calParam, timeDataVO);
				timeDataProcessor.process(calParam, timeDataVO);
				if(timeDataPostProcessor!=null)
					timeDataPostProcessor.postProcess(calParam, timeDataVO);
				//���һ�����ǵ��԰ࣨ������й�֮����Ȼ�ǵ��԰ࣩ���������󣬹���ʱ���ֻ�����ֽ�������̻��ˣ�û���̻���û���̻�������£����������ӱ������ݣ�����������
				if(curAggCalendar!=null&&curAggCalendar.getPsnCalendarVO()!=null&&curAggCalendar.getPsnCalendarVO().isFlexibleFinal()){
					delWorkVOList.add(curAggCalendar.getPsnCalendarVO().getPrimaryKey());
					updatePsnCalendarList.add(curAggCalendar.getPsnCalendarVO());
					curAggCalendar.getPsnCalendarVO().setIssolidifywhencalculation(UFBoolean.valueOf(timeDataVO.isNeedUpdateWT()));
					if(timeDataVO.isNeedUpdateWT())//��ʾ���Զα��̻��ˣ���Ҫ���빤�������ӱ����û���κζԹ̻����õ���Ϣ������Ȼ�̻����ˣ�
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
		//���԰��ڼ�������б��̻�����̻����Ҫ���棨�п��ܲ��ᱻ�̻�������û���κ�ˢǩ����Ҳû���κε��ݣ�
		if(insertSolidifyWorkVOList.size()>0){
			dao.insertVOList(insertSolidifyWorkVOList);
		}
		TimeDataVO[] updateVOs = updateVOList.toArray(new TimeDataVO[0]);
		dao.updateVOArray(updateVOs);
		// ������¼�
//		EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.TIMEDATA, IEventType.TYPE_UPDATE_AFTER, updateVOs));
		fireEvent(new BusinessEvent(IMetaDataIDConst.TIMEDATA, IEventType.TYPE_UPDATE_AFTER, updateVOs));
	}
	
	/**
	 * �����¼������̣߳���Ӱ���ձ��������Ӧʱ��
	 * @param psnInSql
	 */
	private void fireEvent(final BusinessEvent event){
		
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// �߳��л�����Ϣ�ᶪʧ������������һ��
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
		//���÷����������������Ĭ��ֻ����ǰ10w��
		dao.setMaxRows(10000000);
		vos = CommonUtils.toArray(TimeDataVO.class,(Collection<TimeDataVO>)dao.retrieveByClause(TimeDataVO.class, cond,
				new String[]{TimeDataVO.PK_TIMEDATA,TimeDataVO.PK_ORG,TimeDataVO.PK_GROUP,TimeDataVO.PK_PSNDOC,TimeDataVO.CALENDAR,TimeDataVO.CREATIONTIME,TimeDataVO.CREATOR},
				param));
		//��vo�����װ��key����Ա��Сkey�����ڵ�map
		return DailyDataUtils.groupByPsnStrDate(vos);
	}

	private class CalParam{
		public String pk_org;//��֯����
		public String[] pk_psndocs ;//������Ա������
		public Map<String, List<TBMPsndocVO>> allTbmPsndocVOListMap;//������Ա��������ڷ�Χ�ڵ����п��ڵ�����key����Ա����
		public Map<String, Map<UFLiteralDate, String>> allDateOrgMap;//������Ա��������ڷ�Χ�ڵĿ��ڵ�������ְ��֯��key����Ա������value��key�Ǵ��˴�����ְ������ҵ��Ԫ����
		public ICheckTime[][] allCheckTimes;//������Ա�����ڷ�Χ�ڵ�ˢǩ����¼
		public Map<String, LeaveRegVO[]> leaveMap;//������Ա�����ڷ�Χ�ڵ��ݼټ�¼
		public Map<String, LeaveRegVO[]> lactationMap;//������Ա�����ڷ�Χ�ڵĲ���ټ�¼
		public Map<String, OvertimeRegVO[]> overMap;//������Ա�����ڷ�Χ�ڵ��ݼټ�¼
		public Map<String, AwayRegVO[]> awayMap;//������Ա�����ڷ�Χ�ڵĳ����¼
		public Map<String, ShutdownRegVO[]> shutMap;//������Ա�����ڷ�Χ�ڵ�ͣ�����ϼ�¼
		public Map<String, HRHolidayVO[]> orgHolidayVOMap;//��������,key��ҵ��Ԫ������δ��������������
		public HRHolidayVO[] holidayVOs;//��orgHolidayVOMap��value��ƽ������ʹ��
		public Map<String, Map<String, Boolean>> holidayEnjoyMap;//�������յ����������<��Ա����,<pk_holiday+ҵ��Ԫ�������Ƿ�����>>
		public UFLiteralDate beginDate;
		public UFLiteralDate endDate;
		public TimeRuleVO timeRuleVO;
		public Map<String, AggShiftVO> shiftMap ;
		public Map<String, Map<UFLiteralDate, AggPsnCalendar>>  calendarMap;
		public Map<String, Object> paraValueMap;//���еĲ���ֵ
		public Map<String, Map<String, TimeDataVO>> timeDataMap;//�˴�Ҫupdate������timedata��key����Ա��Сkey������
		public Map<String, TimeItemVO> timeitemMap;
	}
	
	// 2012-10-23�޸ģ��Ż�sql��Ч��
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
		//Ҫ����Ȩ��
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
		//��Щ����һ��Ĺ�������
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap =null;
		try{
			String inSQL = isc.getInSQL(psndocVOs, TBMPsndocVO.PK_PSNDOC);
			//Ȼ���ѯ��Щ��Ա����Щ���timedata��¼
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
		// �������п��ڼ�¼Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		//������Щ�˻�û������һ���timedata��¼����Ҫ���ڴ���newһ��(�����������)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		for(TBMPsndocVO psndocVO:psndocVOs){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(psndocVO.getPk_psndoc());
			for(UFLiteralDate date:allDates){
				TimeDataVO vo = dateMap==null?null:dateMap.get(date);
				if(vo==null){
					// û�м�¼ʱ���ڴ���newһ��
					vo = new TimeDataVO();
					vo.setPk_org(psndocVO.getPk_org());
					vo.setPk_psndoc(psndocVO.getPk_psndoc());
					vo.setCalendar(date);
					vo.setDirty_flag(UFBoolean.TRUE);
				}
				// �������Ը�ֵ
				vo.setPk_psnjob(psndocVO.getPk_psnjob());
				vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
				vo.setPk_org_v(psndocVO.getPk_org_v());
				vo.setPk_dept_v(psndocVO.getPk_dept_v());
				
				AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(psndocVO.getPk_psndoc())==null?null:
						calendarMap.get(psndocVO.getPk_psndoc()).get(date);
				if(calendarVO==null&&onlyShowException){//�޹���������Ϊ����
					continue;
				}
				//���ð����Ϣ
				if(calendarVO!=null){
					PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
					vo.setPk_shift(psnCalendarVO.getPk_shift());
					if(onlyShowException&&vo.isNormal())//���ֻ��ʾ�쳣���ҵ�ǰvo�������ģ��򲻼��뷵�ص�list
						continue;
					PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
					vo.setPsnWorkTimeVOs(wtVOs);//���ڽ�����ʾÿ�������εĹ涨���°�ʱ�䣬�Ѿ����ǿ������ݴ���ʱ�̻��İ��
					//�Ƿ���Ҫ��ʾ��;����У������β�������;���������Ҫ��ʾ��;����У�����������ʱ�����У�
					boolean dispMidOut = false;
					if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
						//�ݴ�����ֹshiftMap����û�д˰��
						ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift()).getShiftVO();
						dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
					}
					vo.setMidOut(dispMidOut);
				}
				vo.trimMemory();//�������ܴ�ʱ����������outofmemory����˴˴���UFDouble������һ�£�0ֵ����null
				retList.add(vo);
			}
		}
		return retList.toArray(new TimeDataVO[0]);
	}
	
	/**
	 * Ч���Ż�
	 * ֻ��ѯ vo�е�pk_org+date+pk_psndoc+pk_psnjob+pk_org_v+pk_dept_vƴ���ַ�������
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
		//Ҫ����Ȩ��
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
			//Ȼ���ѯ��Щ��Ա����Щ���timedata��¼
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
		// �������п��ڼ�¼Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		//������Щ�˻�û������һ���timedata��¼����Ҫ���ڴ���newһ��(�����������)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		for(TBMPsndocVO psndocVO:psndocVOs){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(psndocVO.getPk_psndoc());
			for(UFLiteralDate date:allDates){
				TimeDataVO vo = dateMap==null?null:dateMap.get(date);
				if(vo==null){
					// û�м�¼ʱ���ڴ���newһ��
					vo = new TimeDataVO();
					vo.setPk_org(psndocVO.getPk_org());
					vo.setPk_psndoc(psndocVO.getPk_psndoc());
					vo.setCalendar(date);
					vo.setDirty_flag(UFBoolean.TRUE);
				}
				// �������Ը�ֵ
				vo.setPk_psnjob(psndocVO.getPk_psnjob());
				vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
				vo.setPk_org_v(psndocVO.getPk_org_v());
				vo.setPk_dept_v(psndocVO.getPk_dept_v());
				
				vo.trimMemory();//�������ܴ�ʱ����������outofmemory����˴˴���UFDouble������һ�£�0ֵ����null
				retList.add(vo);
			}
		}
		return DailyDataUtils.getPKs(retList.toArray(new TimeDataVO[0]));
	}

	@Override
	public TimeDataVO[] queryByDate(String pk_org, UFLiteralDate date,
			FromWhereSQL fromWhereSQL, boolean onlyShowException)
			throws BusinessException {
		//pk_orgΪnull�Ļ������ѯ��ȫ���ڵ����а��
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
		//��ѯ�������Ա��Ҫ����Ȩ��
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
		//��Щ����һ��Ĺ�������
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap =null;
		try{
			SQLParameter param = new SQLParameter();
			if(hasPkOrg)
				param.addParam(pk_org);
			param.addParam(date.toString());
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(psndocVOs, TBMPsndocVO.PK_PSNDOC);
			//Ȼ���ѯ��Щ��Ա����һ���timedata��¼
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
		Map<String, TimeDataVO> voMap = CommonUtils.toMap(TimeDataVO.PK_PSNDOC, vos);//��timedatavos��װ��map��key����Ա����
		//������Щ�˻�û������һ���timedata��¼����Ҫ���ڴ���newһ��(�����������)
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
			if(calendarVO==null&&onlyShowException){//�޹���������Ϊ����
				continue;
			}
			//���ð����Ϣ
			if(calendarVO!=null){
				PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
				vo.setPk_shift(psnCalendarVO.getPk_shift());
				if(onlyShowException&&vo.isNormal())//���ֻ��ʾ�쳣���ҵ�ǰvo�������ģ��򲻼��뷵�ص�list
					continue;
				PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
				vo.setPsnWorkTimeVOs(wtVOs);//���ڽ�����ʾÿ�������εĹ涨���°�ʱ�䣬�Ѿ����ǿ������ݴ���ʱ�̻��İ��
				//�Ƿ���Ҫ��ʾ��;����У������β�������;���������Ҫ��ʾ��;����У�����������ʱ�����У�
				boolean dispMidOut = false;
				if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
					//�ݴ�����ֹshiftMap����û�д˰��
					AggShiftVO aggShiftVOFromMap = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift());
					if(null!=aggShiftVOFromMap){
						ShiftVO shiftVO = aggShiftVOFromMap.getShiftVO();
						dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
					} 
				}
				vo.setMidOut(dispMidOut);
			}
			vo.trimMemory();//�������ܴ�ʱ����������outofmemory����˴˴���UFDouble������һ�£�0ֵ����null
			retList.add(vo);
		}
//		for(TimeDataVO vo:retList){
//			vo.trimMemory();//�������ܴ�ʱ����������outofmemory����˴˴���UFDouble������һ�£�0ֵ����null
//		}
		return retList.toArray(new TimeDataVO[0]);
	}

	@SuppressWarnings("unchecked")
	protected TimeDataVO[] queryByPsn(String pk_org, String pk_psndoc,
			UFLiteralDate[] allDates,boolean isDatesContinous,
			boolean onlyShowException) throws BusinessException{
		Arrays.sort(allDates);

		//��ѯ���ڵ�����ֻ���ػ������ڵ�
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
		//��vos��װ��map��key������
		Map<UFLiteralDate, TimeDataVO> voMap = new HashMap<UFLiteralDate, TimeDataVO>();
		if(!ArrayUtils.isEmpty(vos))
			for(TimeDataVO vo:vos){
				voMap.put(vo.getCalendar(), vo);
			}
		//��Щ��Ĺ�������
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = isDatesContinous?
				calendarService.queryCalendarMapByPsnDates4DataProcessView(pk_org, pk_psndoc,allDates[0], allDates[allDates.length-1]):
				calendarService.queryCalendarMapByPsnDates(pk_org, pk_psndoc, allDates);


		//��ѯ������vos�����ݿ������еģ����ݿ���û�е���Ҫ�Լ�����
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		for(UFLiteralDate date:allDates){
//			if(!TBMPsndocVO.isIntersect(psndocList, date.toString()))
//				continue;
			// ������¼����Ӧȡ���쿼�ڵ�����Ӧ�Ĺ�����¼
			TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocList, date.toString());
			if (psndocVO == null)
				continue;
			TimeDataVO vo = null;
			if(voMap.containsKey(date)){
				vo = voMap.get(date);
				//�˴���TimeDataVO���ܺ��������ݣ�����������20�Ŵ�A��֯���䵽B��֯������A���ɹ��������ݣ�
				//b��֯û�����ɹ�����������ѯ��ʱ��pk_org=null,��ʱ���20֮������A��֯�Ŀ������ݲ�ѯ����,���ǲ���ȷ��
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
			if(calendarVO==null&&onlyShowException){//�޹���������Ϊ����
				continue;
			}
			//���ð����Ϣ
			if(calendarVO!=null){
				vo.setPk_shift(calendarVO.getPsnCalendarVO().getPk_shift());
				if(onlyShowException&&vo.isNormal())//���ֻ��ʾ�쳣���ҵ�ǰvo�������ģ��򲻼��뷵�ص�list
					continue;
				PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
				vo.setPsnWorkTimeVOs(wtVOs);//���ڽ�����ʾÿ�������εĹ涨���°�ʱ�䣬�Ѿ����ǿ������ݴ���ʱ�̻��İ��
				//�Ƿ���Ҫ��ʾ��;����У������β�������;���������Ҫ��ʾ��;����У�����������ʱ�����У�
				boolean dispMidOut = false;
				if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
					//�ݴ�����ֹshiftMap����û�д˰��
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
	 * ������������ʼ���ڡ��������ں�ѡ���Ҫ������������ݽ�����������
	 * @param param
	 * @param context
	 * @param fromWhereSQL
	 * @param begindate
	 * @param enddate
	 * @throws BusinessException
	 */
	public void cleardata(ClearDataParam param,LoginContext context,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException{
		// ��֤��ʼ�����������Ƿ��ڿ����ڼ䷶Χ�ڻ��Ƿ��ѷ��
		PeriodServiceFacade.checkDateScope(context.getPk_org(), beginDate, endDate);
		//��������Ȩ�޺Ϳ��ڵ�����ά��Ȩ��һ��
//		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", nc.ui.ta.pub.action.IActionCode.CLS, fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		// ���ǩ������
		if (param.isClearqkdata()) {
			NCLocator.getInstance().lookup(ISignCardManageService.class).deleteData(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// ���������������
		if (param.isClearjqkqdata()) {
			deleteTimeData(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// ���������������
		if (param.isCleargzrldata()) {
			NCLocator.getInstance().lookup(IPsnCalendarManageService.class).deleteByCondAndDateScope(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// ���ˢ������
		if (param.isClearskdata()) {
			NCLocator.getInstance().lookup(IImportDataManageMaintain.class).deleteByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate);
			//���ˢ������ʱ���ϵͳˢ������
			NCLocator.getInstance().lookup(ISysCardManageService.class).deleteByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// ����ֹ���������
		if (param.isClearsgkqdata()) {
			NCLocator.getInstance().lookup(ILateEarlyManageService.class).deleteLateEarlyData(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		// ��������ձ�����
		if (param.isClearkqrbdata()) {
			NCLocator.getInstance().lookup(IDayStatManageService.class).deleteByCondAndDateScope(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		}
		//ҵ����־
		String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate);
		TaBusilogUtil.writeDataProcessClear(context.getPk_org(), pk_psndocs, beginDate, endDate);
	}
	
	/**
	 * ɾ��������������
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
			// ������¼�
			EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.TIMEDATA, IEventType.TYPE_DELETE_AFTER, scope));
		} finally {
			isc.clear();
		}
	}

	@Override
	public TBMPsndocVO[] queryPsnByFromWhereSQLAndDates(String pk_org,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		//����Ȩ�޴���
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
		//��ӻ������ڹ�������
		fromWhereSQL = TBMPsndocSqlPiecer.addPropCond(fromWhereSQL, TBMPsndocVO.TBM_PROP_MACHINE);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		//��ѯ�����ڷ�Χ�ڵ����п��ڵ�����¼�����ڷ�Χ�����µ�һ����Ϊ����ƥ�䣬Ȼ���ҳ���Ա�����ڷ�Χ�����еĿ��ڵ�����¼
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.
			queryTBMPsndocMapByConditionForTeam(null, fromWhereSQL, beginDate, endDate, true,true,null);
		if(psndocMap==null||psndocMap.isEmpty())
			return null;
		String pk_hrorg = null;
		for(String psndocPk:psndocMap.keySet()){
			pk_hrorg = psndocMap.get(psndocPk).get(0).getPk_org();
			break;
		}		
//		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(null);//�����ݻ�����ѯ��������̫��
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
		//Ȼ���ѯ��Щ��Ա����Щ���timedata��¼
		String cond = TBMPsndocVO.PK_PSNDOC+" in ("+inSQL+") and "+TimeDataVO.CALENDAR+" between ? and ? ";
		SQLParameter param = new SQLParameter();
		param.addParam(beginDate.toString());
		param.addParam(endDate.toString());
		vos = CommonUtils.retrieveByClause(TimeDataVO.class, cond, param);
		calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
			queryCalendarVOByCondition4DataProcessView(pk_hrorg, beginDate, endDate, inSQL,timeZoneMap,shiftMap);
		// �������п��ڼ�¼Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		//������Щ�˻�û������һ���timedata��¼����Ҫ���ڴ���newһ��(�����������)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		//���˰��칹���¼
		for(String pk_psndoc:psndocMap.keySet()){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(pk_psndoc);
			List<TBMPsndocVO> tbmdocList = psndocMap.get(pk_psndoc);
			if(CollectionUtils.isEmpty(tbmdocList))
				continue;
			for(TBMPsndocVO psndocVO:tbmdocList){
				//ֻ�Կ��ڵ�����Χ����ֹ���ڽ�����ʱ�乹������
				UFLiteralDate begin = psndocVO.getBegindate().before(beginDate)?beginDate:psndocVO.getBegindate();
				UFLiteralDate end = psndocVO.getEnddate().before(endDate)?psndocVO.getEnddate():endDate;
				if(begin.after(end))
					continue;
				UFLiteralDate[] allDates = CommonUtils.createDateArray(begin, end);
				for(UFLiteralDate date:allDates){
					TimeDataVO vo = dateMap==null?null:dateMap.get(date);
					if(vo==null){
						// û�м�¼ʱ���ڴ���newһ��
						vo = new TimeDataVO();
						vo.setPk_org(psndocVO.getPk_org());
						vo.setPk_psndoc(psndocVO.getPk_psndoc());
						vo.setCalendar(date);
						vo.setDirty_flag(UFBoolean.TRUE);
					}
					// �������Ը�ֵ
					vo.setPk_psnjob(psndocVO.getPk_psnjob());
					vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
					vo.setPk_org_v(psndocVO.getPk_org_v());
					vo.setPk_dept_v(psndocVO.getPk_dept_v());
					
					AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(psndocVO.getPk_psndoc())==null?null:
							calendarMap.get(psndocVO.getPk_psndoc()).get(date);
					if(calendarVO==null&&onlyShowException){//�޹���������Ϊ����
						continue;
					}
					//���ð����Ϣ
					if(calendarVO!=null){
						PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
						vo.setPk_shift(psnCalendarVO.getPk_shift());
						if(onlyShowException&&vo.isNormal())//���ֻ��ʾ�쳣���ҵ�ǰvo�������ģ��򲻼��뷵�ص�list
							continue;
						PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
						vo.setPsnWorkTimeVOs(wtVOs);//���ڽ�����ʾÿ�������εĹ涨���°�ʱ�䣬�Ѿ����ǿ������ݴ���ʱ�̻��İ��
						//�Ƿ���Ҫ��ʾ��;����У������β�������;���������Ҫ��ʾ��;����У�����������ʱ�����У�
						boolean dispMidOut = false;
						if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
							//�ݴ�����ֹshiftMap����û�д˰��
//							ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift()).getShiftVO();
//							dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
						}
						vo.setMidOut(dispMidOut);
					}
					vo.trimMemory();//�������ܴ�ʱ����������outofmemory����˴˴���UFDouble������һ�£�0ֵ����null
					retList.add(vo);
				}
			}
		}
		return retList.toArray(new TimeDataVO[0]);
		
		//2013-05-17 ��������һ������ʱ�䷶Χ�ڣ��������������ţ����ѯ���Ϊֻ��ȫ����ʾ�ں���Ĳ�����޷���ѯǰһ�������ڵ���ʷ����
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
		//�����죺ֻ���ڵ�ǰ����HR��֯�Ŀ��ڵ����ڵ��죬������д����
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
				//�����ڿ��ڵ�����Χ�ڣ��Żᴦ����һ��
				if(DateScopeUtils.contains(tbmpsndocVO, date))
					psndocMap.put(date, tbmpsndocVO);
			}
		}
		if(psndocMap.size()==0)
			throw new BusinessException("The dates are not in any TA document!");
		//������ڵ����ж����ְ��֯�������쳣����Ϊһ��ֻ����һ����ְ��֯�ĵ���
		TBMPsndocVO[] filteredPsndocVOs = psndocMap.values().toArray(new TBMPsndocVO[0]);
		Arrays.sort(filteredPsndocVOs);
		String pk_joborg = null;//����pk_psnjob����ְ��֯
		InSQLCreator isc = null;
		try{
			isc = new InSQLCreator();
			String psnjobInSQL = isc.getInSQL(filteredPsndocVOs, TBMPsndocVO.PK_PSNJOB);
			PsnJobVO[] psnjobVOs = CommonUtils.retrieveByClause(PsnJobVO.class, PsnJobVO.PK_PSNJOB+" in("+psnjobInSQL+")");
			String[] pk_joborgs = StringPiecer.getStrArrayDistinct(psnjobVOs, PsnJobVO.PK_ORG);
			if(ArrayUtils.getLength(pk_joborgs)>1)
				throw new BusinessException(ResHelper.getString("6017dataprocess","06017dataprocess0042")
				/*@res "ѡ����������ڲ�ͬҵ��Ԫ����ְ��¼!"*/);
			pk_joborg = pk_joborgs[0];
		}
		finally{
			if(isc!=null)
				isc.clear();
		}
		//�����ߵ����������ѡ������ڶ�������ͬһ�����ڵ�����
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
		//ȡ���һ�����������Ŀ��ڵ�����pk_psnorg��pk_psnjob
		TBMPsndocVO lastPsndocVO = filteredPsndocVOs[filteredPsndocVOs.length-1];
		hVO.setPk_psnorg(lastPsndocVO.getPk_psnorg());
		hVO.setPk_psnjob(lastPsndocVO.getPk_psnjob());
		//���ݱ���
		BillCodeContext billCodeContext = NCLocator.getInstance().lookup(IBillcodeManage.class)
		.getBillCodeContext(SignCardConst.BillTYPE, pk_group, pk_org);
		// ������Զ����ɱ��룬����Ҫ���ɱ���
		String billCode = null;
		try{
			if (billCodeContext != null){
				billCode = NCLocator.getInstance().lookup(IHrBillCode.class).getBillCode(SignCardConst.BillTYPE, pk_group, pk_org);
				hVO.setBill_code(billCode) ;
			}
			//���ˣ�������Ϣ�������,���湹���ӱ���Ϣ
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
		//���Ȳ�ѯ��Щ���timedata����
		Arrays.sort(signCardDates);
		String dateCond = StringPiecer.getDefaultPiecesTogether(signCardDates);
		BaseDAO dao = new BaseDAO();
		String cond = IBaseServiceConst.PK_ORG_FIELD+"='"+pk_org+"' and "+TimeDataVO.PK_PSNDOC+"='"+pk_psndoc+"' and "+TimeDataVO.CALENDAR+" in("+dateCond+")";
		TimeDataVO[] timeDataVOs = (TimeDataVO[]) CommonUtils.toArray(TimeDataVO.class, dao.retrieveByClause(TimeDataVO.class, cond));
		if(ArrayUtils.isEmpty(timeDataVOs))
			return null;
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarMapByPsnDates4DataProcessView(pk_org, pk_psndoc, signCardDates);
		//���û���Ű࣬Ҳ���ô���
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
		//δ���ɿ������ݣ�����û���Ű࣬�����ŵ��ǹ��ݣ���������
		if(timeDataVO == null||aggPsnCalendar==null||ShiftVO.PK_GX.equals(aggPsnCalendar.getPsnCalendarVO().getPk_shift()))
			return null;
		//��������һ��������Ҳ���ô���
		PsnWorkTimeVO[] workVOs = aggPsnCalendar.getPsnWorkTimeVO();
		//��ˢ���δ���
		ICompleteCheckTimeScope[] checkTimeScopes = CompleteCheckTimeScopeUtils.mergeWorkTime(workVOs);
		List<SignbVO> result = new ArrayList<SignbVO>();
		for(ICompleteCheckTimeScope checkTimeScope:checkTimeScopes){
			int checkInID = checkTimeScope.getCheckinScopeTimeID();
			int checkOutID = checkTimeScope.getCheckoutScopeTimeID();
			//�����ˢ���ζ����������ô���
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
			//�����쳣����Ҫ�����߼��ж��Ǻ����쳣��Ȼ����ǩ�����ֲ�����쳣
			//���°�ʱ��
			UFDateTime sbTime = timeDataVO.getBegintime(checkInID);
			if(sbTime==null){
				result.add(createSignbVO(pk_group, pk_org, checkTimeScope.getKssj(),
						checkFlag?ICheckTime.CHECK_FLAG_IN:ICheckTime.CHECK_FLAG_IGNORE, ResHelper.getString("6017dataprocess","06017dataprocess0043")
/*@res "ȱ�ϰ࿨"*/, timeZone));
			}
			else{
				//����ϰ��<=�涨�ϰ��+��С�ٵ�ʱ��������Ϊ�ϰ����������ö���ǩ��
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
/*@res "ȱ�°࿨"*/, timeZone));
			}
			else{
				//����°��>=�涨�°��-��С����ʱ��������Ϊ�ϰ����������ö���ǩ��
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
		//��HR��֯ѭ������Ŀǰ�ĳ����в����п�HR��֯��������֣����˴���orgֻ������һ����
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
				boolean isDatesContinous = UFLiteralDate.getDaysBetween(beginDate, endDate) == dateArray.length-1;//���Ƿ�����
				String[] psnArray = orgPsnDateMap.get(pk_org).keySet().toArray(new String[0]);
				String psnInSQL = isc.getInSQL(psnArray);
				//��ѯ��ô������ô����İ�Ρ����п��ܻ��飬���磬�����pks���棬��һ����100�ˣ��ڶ���ֻ��1���ˡ���Ϊ�����Ч�ʣ����ٲ�ѯ������Ҫһ�β�100�������
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
				
				//��Щ��Ĺ�������
				Map<String,Map<UFLiteralDate, AggPsnCalendar>> calendarMap = 
						calendarService.queryCalendarVOByCondition4DataProcessView(pk_org, beginDate, endDate, psnInSQL);
				Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
				String[] curPks = descriptor.getOrgPkMap().get(pk_org);
				for(String pk:curPks){//�˴���pksΪѭ������Ҫ��Ϊ�˱�֤�����pksÿһ��������һ����Ӧ��vo����
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
					//���ð����Ϣ
					timeDataVO.setPk_shift(calendarVO.getPsnCalendarVO().getPk_shift());
					PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
					timeDataVO.setPsnWorkTimeVOs(wtVOs);//���ڽ�����ʾÿ�������εĹ涨���°�ʱ�䣬�Ѿ����ǿ������ݴ���ʱ�̻��İ��
					//�Ƿ���Ҫ��ʾ��;����У������β�������;���������Ҫ��ʾ��;����У�����������ʱ�����У�
					boolean dispMidOut = false;
					if(!ShiftVO.PK_GX.equals(timeDataVO.getPk_shift())){
						//�ݴ�����ֹshiftMap����û�д˰��
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
//		String[] pKs = DailyDataUtils.getPKs(queryByCondDate(pk_org, fromWhereSQL, beginDate, endDate, onlyShowException));Ч���Ż�ʹ������ķ���
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
		//δ����ͳ��Ȩ�޺Ϳ��ڵ������޸�Ȩ��һ��
//		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_org_v"+FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_dept_v"+FromWhereSQLUtils.getAttPathPostFix());
		String[] otherTableSelFields = new String[]{orgversionAlias+"."+AdminOrgVersionVO.PK_VID+" as "+TBMPsndocVO.PK_ORG_V,
				deptversionAlias+"."+DeptVersionVO.PK_VID+" as "+TBMPsndocVO.PK_DEPT_V};
		//������ڷ�Χ���ձ�����������Ա
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
		//��ӻ������ڹ�������
		fromWhereSQL = TBMPsndocSqlPiecer.addPropCond(fromWhereSQL, TBMPsndocVO.TBM_PROP_MACHINE);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		//��ѯ�����ڷ�Χ�ڵ����п��ڵ�����¼�����ڷ�Χ�����µ�һ����Ϊ����ƥ�䣬Ȼ���ҳ���Ա�����ڷ�Χ�����еĿ��ڵ�����¼
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.
			queryTBMPsndocMapByConditionForTeam(null, fromWhereSQL, beginDate, endDate, true,true,null);
		if(psndocMap==null||psndocMap.isEmpty())
			return null;
		String pk_hrorg = null;
		for(String psndocPk:psndocMap.keySet()){
			pk_hrorg = psndocMap.get(psndocPk).get(0).getPk_org();
			break;
		}		
//		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(null);//�����ݻ�����ѯ��������̫��
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
		//��Щ����һ��Ĺ�������
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap =null;
		//Ȼ���ѯ��Щ��Ա����Щ���timedata��¼
		String cond = TBMPsndocVO.PK_PSNDOC+" in ("+inSQL+") and "+TimeDataVO.CALENDAR+" between ? and ? ";
		SQLParameter param = new SQLParameter();
		param.addParam(beginDate.toString());
		param.addParam(endDate.toString());
		vos = CommonUtils.retrieveByClause(TimeDataVO.class, cond, param);
		calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
			queryCalendarVOByCondition4DataProcessView(pk_hrorg, beginDate, endDate, inSQL,timeZoneMap,shiftMap);
		// �������п��ڼ�¼Map
		Map<String, Map<UFLiteralDate, TimeDataVO>> voMap = new HashMap<String, Map<UFLiteralDate, TimeDataVO>>();
		for(int i = 0,j = ArrayUtils.getLength(vos);i < j;i++){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(vos[i].getPk_psndoc());
			if(MapUtils.isEmpty(dateMap))
				dateMap = new HashMap<UFLiteralDate, TimeDataVO>();
			dateMap.put(vos[i].getCalendar(), vos[i]);
			voMap.put(vos[i].getPk_psndoc(), dateMap);
		}
		//������Щ�˻�û������һ���timedata��¼����Ҫ���ڴ���newһ��(�����������)
		List<TimeDataVO> retList = new ArrayList<TimeDataVO>();
		//���쳣���ݵ���Ա
		Set<String> exceptionPsn = new HashSet<String>();
		//���˰��칹���¼
		for(String pk_psndoc:psndocMap.keySet()){
			Map<UFLiteralDate, TimeDataVO> dateMap = voMap.get(pk_psndoc);
			List<TBMPsndocVO> tbmdocList = psndocMap.get(pk_psndoc);
			if(CollectionUtils.isEmpty(tbmdocList))
				continue;
			for(TBMPsndocVO psndocVO:tbmdocList){
				//ֻ�Կ��ڵ�����Χ����ֹ���ڽ�����ʱ�乹������
				UFLiteralDate begin = psndocVO.getBegindate().before(beginDate)?beginDate:psndocVO.getBegindate();
				UFLiteralDate end = psndocVO.getEnddate().before(endDate)?psndocVO.getEnddate():endDate;
				if(begin.after(end))
					continue;
				UFLiteralDate[] allDates = CommonUtils.createDateArray(begin, end);
				for(UFLiteralDate date:allDates){
					TimeDataVO vo = dateMap==null?null:dateMap.get(date);
					if(vo==null){
						// û�м�¼ʱ���ڴ���newһ��
						vo = new TimeDataVO();
						vo.setPk_org(psndocVO.getPk_org());
						vo.setPk_psndoc(psndocVO.getPk_psndoc());
						vo.setCalendar(date);
						vo.setDirty_flag(UFBoolean.TRUE);
					}
					// �������Ը�ֵ
					vo.setPk_psnjob(psndocVO.getPk_psnjob());
					vo.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
					vo.setPk_org_v(psndocVO.getPk_org_v());
					vo.setPk_dept_v(psndocVO.getPk_dept_v());
					
					AggPsnCalendar calendarVO = calendarMap==null?null:calendarMap.get(psndocVO.getPk_psndoc())==null?null:
							calendarMap.get(psndocVO.getPk_psndoc()).get(date);
					//���ð����Ϣ
					if(calendarVO!=null){
						PsnCalendarVO psnCalendarVO = calendarVO.getPsnCalendarVO();
						vo.setPk_shift(psnCalendarVO.getPk_shift());
						if(!vo.isNormal())
							exceptionPsn.add(pk_psndoc);
						PsnWorkTimeVO[] wtVOs = calendarVO.getPsnWorkTimeVO();
						vo.setPsnWorkTimeVOs(wtVOs);//���ڽ�����ʾÿ�������εĹ涨���°�ʱ�䣬�Ѿ����ǿ������ݴ���ʱ�̻��İ��
						//�Ƿ���Ҫ��ʾ��;����У������β�������;���������Ҫ��ʾ��;����У�����������ʱ�����У�
						boolean dispMidOut = false;
						if(!ShiftVO.PK_GX.equals(vo.getPk_shift())){
							//�ݴ�����ֹshiftMap����û�д˰��
							ShiftVO shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, vo.getPk_shift()).getShiftVO();
							dispMidOut=shiftVO.getIsallowout()==null||!shiftVO.getIsallowout().booleanValue();
						}
						vo.setMidOut(dispMidOut);
					}
					vo.trimMemory();//�������ܴ�ʱ����������outofmemory����˴˴���UFDouble������һ�£�0ֵ����null
					retList.add(vo);
				}
			}
		}
		if(CollectionUtils.isEmpty(exceptionPsn)||CollectionUtils.isEmpty(retList))
			return null;
		//�ҳ������쳣��Ա������
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