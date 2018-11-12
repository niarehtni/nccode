package nc.impl.ta.overtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.ArrayHelper;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.algorithm.SolidifyParaUtils;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hr.pf.HrPfHelper;
import nc.itf.hr.wa.IGlobalCountryQueryService;
import nc.itf.ta.IHolidayRuleQueryService;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.itf.ta.algorithm.SolidifyUtils;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.mddb.baseutil.MDDAOUtil;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.countryzone.CountryZoneVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hr.pf.PFQueryParams;
import nc.vo.org.OrgQueryUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.algorithm.SolidifyPara;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.holidayrule.HolidayRuleVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimeCheckResult;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.TAPFBillQueryParams;
import nc.vo.ta.pub.TaNormalQueryUtils;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


/**
 * �Ӱ������ѯ�������
 * @author yucheng
 *
 */
public class OvertimeApplyQueryMaintainImpl implements IOvertimeApplyQueryMaintain {

	private SimpleDocServiceTemplate serviceTemplate;
	@Override
	public AggOvertimeVO[] defaultQuery(LoginContext context) throws BusinessException {
		return null;
	}

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(IMetaDataIDConst.OVERTIME);
		}
		return serviceTemplate;
	}

	@Override
	public AggOvertimeVO queryByPk(String pk) throws BusinessException {
		return getServiceTemplate().queryByPk(AggOvertimeVO.class, pk);
	}

	@Override
	public AggOvertimeVO[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
			throws BusinessException {
		return queryByCond(context, fromWhereSQL, etraConds, false);
	}

	protected AggOvertimeVO[] queryByCond(LoginContext context,
			FromWhereSQL fromWhereSQL, Object etraConds,boolean blApproveSite)
			throws BusinessException {
		return queryByCond(context.getPk_org(), null, fromWhereSQL, etraConds, blApproveSite);
	}

	 private AggOvertimeVO[] queryByCond(String pk_org, String pk_psndoc,
				FromWhereSQL fromWhereSQL, Object etraConds,boolean blApproveSite)
				throws BusinessException {
//			if(etraConds == null)
//				etraConds = TAPFBillQueryParams.getDefaultParams(blApproveSite);
//			if(!(etraConds instanceof PFQueryParams))
//				return null;
//			String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimehVO.getDefaultTableName());
//			String strNormalSQL =
//	            HrPfHelper.getQueryCondition(AggOvertimeVO.class, alias, blApproveSite, pk_org, blApproveSite?((PFQueryParams)etraConds).getBillState():((TAPFBillQueryParams)etraConds).getStateCode());
//
//			if(!StringUtils.isEmpty(pk_psndoc))//��Ա�������գ���ʾ��������Ա������
//				strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, alias+"."+OvertimehVO.PK_PSNDOC+"='"+pk_psndoc+"'");
//
//
//	        String dateFilter = blApproveSite?TaNormalQueryUtils.getApproveDatePeriod(HrPfHelper.getFlowBizItf(AggOvertimeVO.class),alias, ((PFQueryParams)etraConds).getApproveDateParam(),((PFQueryParams)etraConds).getBillState())
//	        			: TaNormalQueryUtils.getDateScopeSql(alias,(TAPFBillQueryParams)etraConds);
//
//	        strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, dateFilter);
//
//	      //����pk_org Ҫ��Ȼ���л���֯ʱ��Ҳ�Ὣ�����ڱ���֯�ĵ��ݲ�ѯ����
//	        String othercond="";
//	        if(StringUtils.isNotEmpty(pk_org))
//	        	othercond=" and "+alias+".pk_org='"+ pk_org+"' " ;
//	        
//			String order = " order by " + OvertimehVO.APPLY_DATE + " desc, " + OvertimehVO.BILL_CODE;
//			if (fromWhereSQL == null || fromWhereSQL.getWhere() == null)
//				return getServiceTemplate().queryByCondition(AggOvertimeVO.class, strNormalSQL+othercond+ order);
//			// ���FromWhereSQL��������
//			String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, OvertimehVO.getDefaultTableName(),
//					new String[]{OvertimehVO.PK_OVERTIMEH}, null, null, null, null);
//			strNormalSQL +=  " and " + OvertimehVO.PK_OVERTIMEH + " in ( " + sql + " )" + othercond+order;
		 	String strNormalSQL=getSQLCondByFromWhereSQL(pk_org, pk_psndoc,fromWhereSQL, etraConds,blApproveSite);
			return getServiceTemplate().queryByCondition(AggOvertimeVO.class, strNormalSQL);
		}

	@Override
	public OvertimeCommonVO[] queryTBMPsndocs(OvertimeCommonVO[] infoVOs,
			FromWhereSQL fromWhereSQL, String[] pk_psndocs, LoginContext context)
			throws BusinessException {
		if (ArrayUtils.isEmpty(infoVOs))
			return null;
		String pk_org = context.getPk_org();
		// ���÷���ʱ��fromWhereSQL��pk_psndocsֻ��һ����ֵ
		if(fromWhereSQL==null)
			fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		//����Ȩ�޴���
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		Map<String, TimeZone> timeZoneMap = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZoneMap(pk_org);
		CommonMethods.processBeginEndDate(infoVOs, timeZoneMap);
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(infoVOs);
		//��ѯ���ڵ���
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocMapByCondition(pk_org, fromWhereSQL, maxDateScope.getBegindate(), maxDateScope.getEnddate(), false);
		if(MapUtils.isEmpty(psndocMap))
			return null;
		List<OvertimeCommonVO> voList = new ArrayList<OvertimeCommonVO>();
		for(OvertimeCommonVO infoVO:infoVOs){
			// ����ѭ��
			for(String pk_psndoc:psndocMap.keySet()) {
				// ��ͨ�����������ҿ��ڵ���
				TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocMap.get(pk_psndoc), infoVO.getEnddate().toStdString());
				if(psndocVO == null){
					// �����������û�ҵ������Ŀ��ڵ�������ͨ����ʼ��������һ��
					psndocVO = TBMPsndocVO.findIntersectionVO(psndocMap.get(pk_psndoc), infoVO.getBegindate().toStdString());
					if(psndocVO == null)
						continue;
				}
				// �н��������ɼӰ൥��
				OvertimeCommonVO vo = (OvertimeCommonVO)infoVO.clone();
				vo.setPk_psndoc(psndocVO.getPk_psndoc());
				vo.setPk_psnjob(psndocVO.getPk_psnjob());
				vo.setPk_psnorg(psndocVO.getPk_psnorg());
				vo.setPk_org_v(psndocVO.getPk_org_v());
				vo.setPk_dept_v(psndocVO.getPk_dept_v());
				vo.setPk_group(context.getPk_group());
				vo.setPk_org(pk_org);
				voList.add(vo);
			}
		}
		if(CollectionUtils.isEmpty(voList))
			return null;
		OvertimeCommonVO[] returnVOs = voList.toArray(new OvertimeCommonVO[0]);
		// ���ÿ����ڼ��ѼӰ�ʱ��
		if(infoVOs[0].getIsneedcheck().booleanValue()){
			new OvertimeDAO().setAlreadyHourAndCheckFlag(true,returnVOs);
		}else{
			new OvertimeDAO().setAlreadyHour(returnVOs);
		}
		// ���ݿ��ڵ����и��ʱ��
		returnVOs = BillMethods.compareAndCutDate(context.getPk_org(),returnVOs);
		BillProcessHelperAtServer.calOvertimeLength(context.getPk_org(), returnVOs);
		return returnVOs;
	}
	
//	@Deprecated
//	public OvertimeCommonVO[] queryTBMPsndocs1(OvertimeCommonVO[] infoVOs,
//			FromWhereSQL fromWhereSQL, String[] pk_psndocs, LoginContext context)
//			throws BusinessException {
//		if(ArrayUtils.isEmpty(infoVOs))
//			return null;
//		String pk_org = context.getPk_org();
//		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
//		//ȡ��ǰ��֯���а����Ϣ
//		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
//		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
//		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
//		List<OvertimeCommonVO> vos = new ArrayList<OvertimeCommonVO>();
//		//����Ȩ�޴���
//		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
//		OvertimeDAO dao = new OvertimeDAO();
//
//		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
//		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
//		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
//		for(OvertimeCommonVO infoVO:infoVOs){
//			UFLiteralDate tempBeginDate = infoVO.getOvertimebegintime().getDate().getDateBefore(2).toUFLiteralDate(ICalendar.BASE_TIMEZONE);
//			UFLiteralDate tempEndDate = infoVO.getOvertimeendtime().getDate().getDateAfter(2).toUFLiteralDate(ICalendar.BASE_TIMEZONE);
//			CommonMethods.processBeginEndDate(infoVO, timeRuleVO.getTimeZoneMap());
//			TBMPsndocVO[] psndocvos = psndocService.queryLatestByCondition(context.getPk_org(),fromWhereSQL,pk_psndocs,infoVO.getBegindate(),infoVO.getEnddate());
//			if(ArrayUtils.isEmpty(psndocvos))
//				continue;
//			InSQLCreator isc = null;
//			Map<String,Map<UFLiteralDate,String>> psnDateOrgMap = null;
//			Map<String,Map<UFLiteralDate,AggPsnCalendar>> psnCalendarMap = null;
//			Map<UFLiteralDate,PeriodVO> periodMap = periodService.queryPeriodMapByDateScope(pk_org, tempBeginDate, tempEndDate);
//			try{
//				isc = new InSQLCreator();
//				String psndocInSQL = isc.getInSQL(psndocvos, TBMPsndocVO.PK_PSNDOC);
//				psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, tempBeginDate, tempEndDate);
//				psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, tempBeginDate, tempEndDate, psndocInSQL);
//			}
//			finally{
//				if(isc!=null)
//					isc.clear();
//			}
//			for(TBMPsndocVO psndocvo : psndocvos) {
//				OvertimeCommonVO vo = (OvertimeCommonVO)infoVO.clone();
//				String pk_psndoc = psndocvo.getPk_psndoc();
//				vo.setPk_psndoc(pk_psndoc);
//				vo.setPk_psnjob(psndocvo.getPk_psnjob());
//				vo.setPk_psnorg(psndocvo.getPk_psnorg());
//				vo.setPk_org_v(psndocvo.getPk_org_v());
//				vo.setPk_dept_v(psndocvo.getPk_dept_v());
//				vo.setPk_group(context.getPk_group());
//				vo.setPk_org(pk_org);
//				Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
//				Map<UFLiteralDate,AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(pk_psndoc);
//				dao.setAlreadyHour(vo, shiftMap,
//						CommonMethods.createDateTimeZoneMap(dateOrgMap,timeRuleVO.getTimeZoneMap()),
//						calendarMap,timeitemMap,periodMap,timeRule);
////				Boolean isCanCheck = isCanCheck(vo);//�Ż�ʱ�޸ģ�����ѭ�������²�ѯTimeRuleVO
//				Boolean isCanCheck = isCanCheck(vo,timeRule);
//				if(!isCanCheck)
//					vo.setIsneedcheck(UFBoolean.FALSE);
//				vos.add(vo);
//			}
//		}
//		OvertimeCommonVO[] returnVOs = vos.toArray(new OvertimeCommonVO[0]);
//		if(ArrayUtils.isEmpty(returnVOs))
//			return null;
//		// ���ݿ��ڵ����и��ʱ��
//		returnVOs = nc.impl.ta.timebill.CommonMethods.compareAndCutDate(context.getPk_org(),returnVOs);
//		BillProcessHelperAtServer.calOvertimeLength(context.getPk_org(), returnVOs);
//		return returnVOs;
//	}

//	@Override
//	public String getBillIdSql(int billStatus, String billType)
//			throws BusinessException {
//		String pks = NCLocator.getInstance().lookup(IHrPf.class).getBillIdSql(billStatus, billType);
//		String strWorkFlowWhere = OvertimehVO.PK_OVERTIMEH + " in (" + (StringUtils.isBlank(pks) ? " '1<>1' " : pks) + ") ";
//		return strWorkFlowWhere;
//	}
	
	public boolean isCanCheck(
			OvertimeCommonVO vo,
			TimeRuleVO timeRuleVO,
			Map<String, AggShiftVO> aggShiftMap,
			Map<String, ShiftVO> shiftMap,
			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
			Map<UFLiteralDate, String> dateOrgMap,
			Map<UFLiteralDate, PeriodVO> periodMap) throws BusinessException{
		//��ȡ��ʼʱ��
		UFDateTime beginTime = vo.getOvertimebegintime();
		//��ȡ����ʱ��
		UFDateTime endTime = vo.getOvertimeendtime();
		if(beginTime==null||endTime==null||beginTime.after(endTime))
			return false;
		String pk_org = vo.getPk_org();
		//û�а����Ϣ��û�����ù�������������У��
		if(calendarMap==null||aggShiftMap==null)
			return false;
		Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeRuleVO.getTimeZoneMap());
		//����Ӱ൥�ݹ�����
		UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap,dateTimeZoneMap);
		// ������������ڿ����ڼ�
		PeriodVO period = periodMap.get(belongDate);
		if(period == null){
			period = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(pk_org, belongDate);
		}
		// ��������ڼ��ѷ�棬������У��
		if(period==null||period.isSeal())
			return false;

		//���쿼��ʱ���
		AggPsnCalendar calendarVO = calendarMap.get(belongDate);
		// û�����ù���������������У��
		if(calendarVO==null)
			return false;
		
		//����ʱ���--����ǹ����գ�����ˢ����ʼ����
		//��һ���ǹ����գ����һ�յĿ��ڿ�ʼ
		//��һ���ǹ����գ��򿴵���ˢ�������Ƿ��˵ڶ��죬������ˣ��͵�ˢ�����������򵽵���24��
		//           ����ǹ����գ���ô����ʱ���Ϊ�������ݵ�ʱ���
		ITimeScope kqScope = CommonMethods.getKQScope4OvertimeCheckAndGen(belongDate, 5, shiftMap, calendarMap, dateTimeZoneMap);
		// �Ӱ����ʱ���
		ITimeScope voScope = new DefaultTimeScope(beginTime,endTime);
		//����Ӱ�ʱ��γ����˿���ʱ��Σ�������У��
		if(!TimeScopeUtils.contains(kqScope, voScope))
			return false;

		// ����ǵ��԰࣬����Ҫ�ȹ̻�
		if(calendarVO.getPsnCalendarVO().isFlexibleFinal()){
			//�����̻��Ĳ���
			SolidifyPara solidifyPara = SolidifyParaUtils.initPara(pk_org, calendarVO.getPk_psndoc(), belongDate, timeRuleVO, aggShiftMap, calendarMap, dateOrgMap);
			//���й̻�
			calendarVO.setPsnWorkTimeVO(SolidifyUtils.solidify(solidifyPara));
		}
		//�ж��Ƿ��빤��ʱ����н���
		ITimeScope[] workScopes = calendarVO.getPsnWorkTimeVO();
		
		//V63�޸ģ������Ű�Ĺ���ʱ����Ӱ൥�н����ǿ���У��ģ���˴�ʱ�Ĺ���ʱ��Ӧ�ÿ۳�����ʱ��
		//��ȡ����ʱ��������еļ����Ҽ����Ű��Ϊ�Ӱ����Ϣ
		Map<String, HRHolidayVO[]> overTimeHolidayScope = BillProcessHelperAtServer.getOverTimeHolidayScope(new OvertimeCommonVO[]{vo});
		if(!overTimeHolidayScope.isEmpty()){
			HRHolidayVO[] hrHolidayVOs = overTimeHolidayScope.get(vo.getPk_psndoc());
			if(!ArrayUtils.isEmpty(hrHolidayVOs)){
				workScopes = TimeScopeUtils.minusTimeScopes(workScopes, hrHolidayVOs);
			}
		}
		
//		��ȡ����ʱ���
		List<ITimeScope> rtScopeList = new ArrayList<ITimeScope>();
		if(!ArrayUtils.isEmpty(workScopes)){
			TimeScopeUtils.sort(workScopes);
			for(int i=1;i<workScopes.length;i++){
				ITimeScope rtscope = new DefaultTimeScope();
				rtscope.setScope_start_datetime(workScopes[i-1].getScope_end_datetime());
				rtscope.setScope_end_datetime(workScopes[i].getScope_start_datetime());
				rtScopeList.add(rtscope);
			}
		}
		//65�޸ģ����빤����ʱ����г���Ҳ����У��
		if(CollectionUtils.isNotEmpty(rtScopeList)&&TimeScopeUtils.isCross(rtScopeList.toArray(new ITimeScope[0]), voScope)){
			return false;
		}
		//����Ӱ�ʱ����빤��ʱ����н�����������У��
		return !TimeScopeUtils.isCross(workScopes, voScope);
	}

	/**
	 * �Ƿ�����У��
	 * 1. ��ǰ��֯û�а����Ϣ�򵥾ݿ�ʼ����ǰ3�����������ں�3��û�����ù�������������У��
	 * 2. ���������ڿ����ڼ��ѷ�治����У��
	 * 3. ������û�����ù�������������У��
	 * 4. �Ӱ�ʱ��γ������ڰ�εĿ���ʱ��β�����У��
	 * 5. �Ӱ�ʱ��������ڰ�ι���ʱ����ཻ������У��
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public boolean isCanCheck(OvertimeCommonVO vo) throws BusinessException {
		//��ȡ��ʼʱ��
		UFDateTime beginTime = vo.getOvertimebegintime();
		//��ȡ����ʱ��
		UFDateTime endTime = vo.getOvertimeendtime();
		if(beginTime==null||endTime==null||beginTime.after(endTime))
			return false;
		String pk_org = vo.getPk_org();
		// ������ڷ�Χ�����¿��ڵ���Ϊ�ֹ����ڣ�������У��
		TBMPsndocVO psndocVO = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestByPsndocDate(pk_org, vo.getPk_psndoc(), vo.getOvertimebegindate(), vo.getOvertimeenddate());
		if(psndocVO == null || TBMPsndocVO.TBM_PROP_MANUAL == psndocVO.getTbm_prop())
			return false;
		TimeRuleVO timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
		//ȡ��ʼ����ǰ���쵽�������ں�����Ĺ�������
		UFLiteralDate beginDateBefore3 = vo.getOvertimebegindate().getDateBefore(3);
		UFLiteralDate endDateAfter3 = vo.getOvertimeenddate().getDateAfter(3);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarMapByPsnDates(pk_org, vo.getPk_psndoc(), beginDateBefore3, endDateAfter3);
		//û�а����Ϣ��û�����ù�������������У��
		if(calendarMap==null||shiftMap==null)
			return false;
		Map<UFLiteralDate, String> dateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMap(vo.getPk_psndoc(), beginDateBefore3, endDateAfter3);
		// ������������ڿ����ڼ�
		Map<UFLiteralDate, PeriodVO> periodMap= NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScope(pk_org, vo.getBegindate(), vo.getEnddate());
		if(periodMap == null || periodMap.isEmpty()){
			   return false;
			  }
		
		return isCanCheck(vo, timeruleVO, aggShiftMap, shiftMap, calendarMap, dateOrgMap, periodMap);
	}
	
	/**
	 * �Ż���ӷ��������ٲ�ѯTimeRuleVO
	 * @param vo
	 * @param timeRule
	 * @return
	 * @throws BusinessException
	 */
//	private boolean isCanCheck(OvertimeCommonVO vo,TimeRuleVO timeruleVO) throws BusinessException {
//		//��ȡ��ʼʱ��
//		UFDateTime beginTime = vo.getOvertimebegintime();
//		//��ȡ����ʱ��
//		UFDateTime endTime = vo.getOvertimeendtime();
//		if(beginTime==null||endTime==null||beginTime.after(endTime))
//			return false;
//		String pk_org = vo.getPk_org();
//		// ������ڷ�Χ�����¿��ڵ���Ϊ�ֹ����ڣ�������У��
//		TBMPsndocVO psndocVO = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestByPsndocDate(pk_org, vo.getPk_psndoc(), vo.getOvertimebegindate(), vo.getOvertimeenddate());
//		if(psndocVO == null || TBMPsndocVO.TBM_PROP_MANUAL == psndocVO.getTbm_prop())
//			return false;
//		if(timeruleVO == null)
//			timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
//		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
//		//ȡ��ʼ����ǰ���쵽�������ں�����Ĺ�������
//		UFLiteralDate beginDateBefore3 = vo.getOvertimebegindate().getDateBefore(3);
//		UFLiteralDate endDateAfter3 = vo.getOvertimeenddate().getDateAfter(3);
//		Map<UFLiteralDate, AggPsnCalendar> calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarMapByPsnDates(pk_org, vo.getPk_psndoc(), beginDateBefore3, endDateAfter3);
//		//û�а����Ϣ��û�����ù�������������У��
//		if(calendarMap==null||shiftMap==null)
//			return false;
//		Map<UFLiteralDate, String> dateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMap(vo.getPk_psndoc(), beginDateBefore3, endDateAfter3);
//		// ������������ڿ����ڼ�
//		Map<UFLiteralDate, PeriodVO> periodMap= NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScope(pk_org, vo.getBegindate(), vo.getEnddate());
//		return isCanCheck(vo, timeruleVO, aggShiftMap, shiftMap, calendarMap, dateOrgMap, periodMap);
//	}

	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> isCanCheckInBatch(OvertimeCommonVO[] vos) throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return null;
		boolean needWarning = false; //�Ƿ���Ҫ���ѣ�����У�鵫û��ѡ��У���ʶ��
		//����Ƿ���ҪУ���ʶ
		for(OvertimeCommonVO vo:vos){
			if(!vo.getIsneedcheck().booleanValue())
				needWarning = true;
		}
		// ���δ��ѡ�Ƿ���ҪУ�飬����Ҫ����
		if(!needWarning)
			return null;
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		InSQLCreator isc = null;
		String pk_org = vos[0].getPk_org();
		isc = new InSQLCreator();
		String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
		Map<UFLiteralDate, PeriodVO> periodMap = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScopes(pk_org, scopes);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
		TimeRuleVO timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocMapByPsndocs(null, StringPiecer.getStrArrayDistinct(vos, OvertimeCommonVO.PK_PSNDOC),
				maxDateScope.getBegindate(), maxDateScope.getEnddate(), true, true, null);
		List<OvertimeCommonVO> checkvos = new ArrayList<OvertimeCommonVO>();
		for(OvertimeCommonVO vo:vos){
			if(vo.getIsneedcheck().booleanValue())
				continue;
			String pk_psndoc = vo.getPk_psndoc();
			Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(pk_psndoc);
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
			// ��ͨ�����������ҿ��ڵ���
			Map<String, List<TBMPsndocVO>> tmpPsndocMap = CommonMethods.filterTBMPsndocByOrg(vo.getPk_org(), psndocMap);
			List<TBMPsndocVO> psnList = tmpPsndocMap==null?null:tmpPsndocMap.get(vo.getPk_psndoc());
			TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psnList, vo.getEnddate().toStdString());
			if(psndocVO == null){
				// �����������û�ҵ������Ŀ��ڵ�������ͨ����ʼ��������һ��
				psndocVO = TBMPsndocVO.findIntersectionVO(psnList, vo.getBegindate().toStdString());
			}
			if(psndocVO == null || TBMPsndocVO.TBM_PROP_MANUAL == psndocVO.getTbm_prop()){
				continue;//�ֹ����ڲ���ҪУ��
			}
			if(isCanCheck(vo, timeruleVO, aggShiftMap, shiftMap, calendarMap, dateOrgMap, periodMap))
				checkvos.add(vo); //����У�鵫��û��ѡ���  ��������
		}
		if(CollectionUtils.isEmpty(checkvos))
			return null;
		Map<String, ITimeScopeWithBillInfo[]> unCheckMap = new HashMap<String, ITimeScopeWithBillInfo[]>();
		for(OvertimeCommonVO vo:checkvos){
			String psndoc = vo.getPk_psndoc();
			// ���map�ﲻ���ڴ���Ա��¼�ĵ���
			if(unCheckMap.get(psndoc)==null){
				unCheckMap.put(psndoc, new OvertimeCommonVO[]{vo});
				continue;
			}
			// ���map���Ѵ��ڴ���Ա��¼�ĵ���
			unCheckMap.put(psndoc, (OvertimeCommonVO[]) ArrayHelper.addAll(unCheckMap.get(psndoc), new OvertimeCommonVO[]{vo}, OvertimeCommonVO.class));
		}
		Map<Integer, Map<String, ITimeScopeWithBillInfo[]>> returnMap = new HashMap<Integer, Map<String,ITimeScopeWithBillInfo[]>>();
		returnMap.put(BillMutexRule.BILL_OVERTIME, unCheckMap);
		return CommonUtils.transferMap(returnMap);
	}

	/**
	 * У�鿼�ڹ������ڼ��ڼӰ�ʱ������
	 * @param vos
	 * @param shiftMap
	 * @param timeZone
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public String checkOvertimeLength(String pk_org, OvertimeCommonVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		Map<String, String[]> map = checkOvertimeLength4PF(pk_org, vos);
		if(map == null)
			return null;
		String psnNames = map.keySet().toArray(new String[0])[0];
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		if(timeRule==null||timeRule.getCtrlothours()==null||timeRule.getCtrlothours().doubleValue() <0)
			return null;
		UFDouble maxLength = timeRule.getDecimalFormatDouble(timeRule.getCtrlothours());
		String lengthStr = maxLength.toString();
//		int index = lengthStr.indexOf('.');
//		lengthStr = index<=0 ? lengthStr:lengthStr.substring(0, index+timeRule.getTimedecimal().intValue()+1);
		String errMsg = psnNames + ResHelper.getString("6017overtime","06017overtime0031"/*@res "�Ӱ�ʱ���ѳ���һ�������ڼ��ڵ����Ӱ�ʱ��{0}Сʱ"*/, lengthStr);
		return errMsg;
	}
	
	@Override
	public Map<String,String[]>  checkOverTimeHoliday(String pk_org, OvertimeCommonVO[] vos)throws BusinessException {
		if(ArrayUtils.isEmpty(vos)||StringUtils.isBlank(pk_org))
			return null;
		List<OvertimeCommonVO> checkOvertimeTypeHoliday = checkOvertimeTypeHoliday(pk_org, vos);
		if(CollectionUtils.isEmpty(checkOvertimeTypeHoliday))
			return null;
		List<String> warningPsn = new ArrayList<String>();
		List<String> errPsnTime = new ArrayList<String>();
		for(OvertimeCommonVO vo:checkOvertimeTypeHoliday){
			warningPsn.add(vo.getPk_psndoc());
			errPsnTime.add(vo.getPk_psndoc()+vo.getOvertimebegintime().toString());
		}
		String[] pk_psndocs = warningPsn.toArray(new String[0]);
		String psnNames = CommonUtils.getPsnNames(pk_psndocs);
		Map<String,String[]> holidayMap = new HashMap<String,String[]>();
		holidayMap.put(psnNames, errPsnTime.toArray(new String[0]));
		return holidayMap;
	}
	
	private List<OvertimeCommonVO>  checkOvertimeTypeHoliday(String pk_org, OvertimeCommonVO[] vos)throws BusinessException {
		if(ArrayUtils.isEmpty(vos)||StringUtils.isBlank(pk_org))
			return null;
		//��ȡ����ʱ��������еļ����Ҽ����Ű��Ϊ�Ӱ����Ϣ
		Map<String, HRHolidayVO[]> overTimeHolidayScope = BillProcessHelperAtServer.getOverTimeHolidayScope( vos);
		if(MapUtils.isEmpty(overTimeHolidayScope))
			return null;
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		//��ѯ����ҵ��Ԫ�ļ��ռӰ����<ҵ��Ԫ��HolidayRuleVO[]>
		Map<String, HolidayRuleVO[]> buHolidayRuleMap = NCLocator.getInstance().lookup(IHolidayRuleQueryService.class).queryByOrgs(pk_org, maxDateScope.getBegindate(), maxDateScope.getEnddate());
		List<OvertimeCommonVO> holruleBillsList = new ArrayList<OvertimeCommonVO>();//�����յļӰ����ͼ��ռӰ�����岻һ�µĵ���
		for(OvertimeCommonVO vo:vos){
			HRHolidayVO[] hrholidays = (HRHolidayVO[]) overTimeHolidayScope.get(vo.getPk_psndoc());
			if(ArrayUtils.isEmpty(hrholidays))
				continue;
			//��ѯ���ռӰ����
			HolidayRuleVO[] holidayRules = buHolidayRuleMap.get(vo.getPk_joborg());
			Map<String, HolidayRuleVO> holidayRuleMap = CommonUtils.toMap(HolidayRuleVO.PK_HOLIDAY, holidayRules);
			for(HRHolidayVO hrholiday:hrholidays){
				//�ж��Ƿ�ͼӰ൥�н���
				if(!TimeScopeUtils.isCross(hrholiday, vo))
					continue;
				//���жϼӰ൥�ļӰ����ͼ��չ���Ӱ�����Ƿ�һ��
				HolidayRuleVO holidayRuleVO = holidayRuleMap==null?null:holidayRuleMap.get(hrholiday.getPk_holiday());
				if(holidayRuleVO == null)
					continue;
				if(holidayRuleVO.getPk_ottype().equals(vo.getPk_overtimetype()))
					continue;
				//���еļ��յļӰ൥�ļӰ����ͼ��չ����ж���ļӰ����һ��ʱ��Ҫ������ʾ
				holruleBillsList.add(vo);
			}
		}
		return holruleBillsList;
	}

	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			AggOvertimeVO aggVO) throws BusinessException, BillMutexException {
		return BillValidatorAtServer.checkOvertime(aggVO);
	}

	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			String pk_org, OvertimebVO[] vos) throws BusinessException,
			BillMutexException {
		return BillValidatorAtServer.checkOvertime(pk_org, vos);
	}

	@Override
	public AggOvertimeVO[] queryByCond(String cond) throws BusinessException {
		return getServiceTemplate().queryByCondition(AggOvertimeVO.class, cond);
	}

	@Override
	public AggOvertimeVO[] queryByPsndoc(String pk_psndoc,
			FromWhereSQL fromWhereSQL, Object extraConds)
			throws BusinessException {
		return queryByCond(null, pk_psndoc, fromWhereSQL, extraConds, false);
	}

	@Override
	public Map<String,String[]> checkIsNeed4PF(String pkOrg, OvertimeCommonVO[] vos)
			throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		List<String> psnList = new ArrayList<String>();
		List<String> psnTimeList = new ArrayList<String>();
		for(OvertimeCommonVO vo:vos){
			//�Ѿ�ѡ�еĲ�������
			if(vo.getIsneedcheck().booleanValue())
				continue;
			//����ҪУ���Ҳ��������
			if(!isCanCheck(vo))
				continue;
			psnList.add(vo.getPk_psndoc());
			psnTimeList.add(vo.getPk_psndoc() + vo.getOvertimebegintime().toString());
		}
		if(psnList.size()>0){
			String[] pk_psndocs = psnList.toArray(new String[0]);
			String psnNames = CommonUtils.getPsnNames(pk_psndocs);
//			if(StringUtils.isNotBlank(psnNames)){
//				String warMsg =  psnNames  + "�ļӰ൥û��ѡ�мӰ�У���ʶ��ϵͳ����У��õ��ݵļӰ�ʱ��";
//				return warMsg;
//			}
			Map<String,String[]> checkMap = new HashMap<String,String[]>();
			checkMap.put(psnNames, psnTimeList.toArray(new String[0]));
			return checkMap;
		}
		return null;
	}
	
	@Override
	public String checkIsNeed(String pk_org, OvertimeCommonVO[] vos)
			throws BusinessException {
		Map<String, String[]> needMap = checkIsNeed4PF(pk_org, vos);
		if(needMap == null)
			return null;
		String psnNames = needMap.keySet().toArray(new String[0])[0];
		if(StringUtils.isNotBlank(psnNames)){
			String warMsg = ResHelper.getString("6017overtime","06017overtime0057"/*@res "{0}�ļӰ൥û��ѡ�мӰ�У���ʶ��ϵͳ����У��õ��ݵļӰ�ʱ��"*/, psnNames);
			return warMsg;
		}
		return null;
	}
	private boolean isTWOrg(String pk_org) throws BusinessException {
		OrgVO[] orgVOs = OrgQueryUtil.queryOrgVOByPks(new String[] { pk_org });
		IGlobalCountryQueryService czQry = NCLocator.getInstance().lookup(IGlobalCountryQueryService.class);
		CountryZoneVO czVO = czQry.getCountryZoneByPK(orgVOs[0].getCountryzone());
		return czVO.getCode().equals("TW");
	}
	@Override
	public Map<String,String[]> checkOvertimeLength4PF(String pk_org, OvertimeCommonVO[] vos)
			throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		if(timeRule==null||timeRule.getCtrlothours()==null||timeRule.getCtrlothours().doubleValue() <0)
			return null;
		UFDouble maxLength = timeRule.getCtrlothours();
		//ȡ��ǰ��֯���а����Ϣ
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
		//ʱ��Map key: ��Ա����  value��key: �����ڼ� value��ָ����Ա��ָ�������ڼ��ڵļӰ�ʱ��
		Map<String, Map<String, UFDouble>> lengthMap = new HashMap<String, Map<String, UFDouble>>();
		//���������Ա����
		List<String> errPsndocList = new ArrayList<String>();
		//������ĵ��ݣ�����û���������˴�ʹ��pk_psndoc + overtimebegintime ��¼
		List<String> errPsnTimeList = new ArrayList<String>();
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		Map<UFLiteralDate, PeriodVO> periodVOMap = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScopes(pk_org,scopes);
		boolean isTWOrg=isTWOrg(pk_org);
		//MOD �����ڼ�Ϊ��Ȼ�� James
		if (isTWOrg) {
			periodVOMap.clear();
			for(OvertimeCommonVO vo:vos){
					PeriodVO pVO=new PeriodVO();
					pVO.setPk_org(pk_org);
					pVO.setTimeyear(Integer.toString(vo.getBegindate().getYear()));
					pVO.setTimemonth(Integer.toString(vo.getBegindate().getMonth()));
					pVO.setBegindate(vo.getBegindate().getDateBefore(vo.getBegindate().getDay()-1));
					pVO.setEnddate(vo.getBegindate().getDateAfter(vo.getBegindate().getDaysMonth()-vo.getBegindate().getDay()));
					if(!periodVOMap.containsKey(vo.getBegindate().getDateBefore(vo.getBegindate().getDay()-1))){
					periodVOMap.put(vo.getBegindate().getDateBefore(vo.getBegindate().getDay()-1), pVO);
				}
			}	
		}
		
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		InSQLCreator isc = new InSQLCreator();
		String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
		// �����ѼӰ�ʱ��
		new OvertimeDAO().setAlreadyHour(vos, timeRule, aggShiftMap, periodVOMap, timeitemMap, psnDateOrgMap, psnCalendarMap);
		
		for(OvertimeCommonVO vo:vos){
			OvertimeCommonVO thisVO = (OvertimeCommonVO) vo.clone();
			//��Ա����ְ��֯map
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(thisVO.getPk_psndoc());
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeRule.getTimeZoneMap());
			Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(thisVO.getPk_psndoc());
			UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap,shiftMap, dateTimeZoneMap);
			//MOD James
			if(isTWOrg){
				 belongDate = vo.getBegindate().getDateBefore(thisVO.getBegindate().getDay()-1);
			}
			if(belongDate==null)
				continue;
			PeriodVO period = periodVOMap==null?null:periodVOMap.get(belongDate);
			if(period==null)
				continue;
			String periodStr = period.getTimeyear() + "-" + period.getTimemonth();
			Map<String, UFDouble> periodMap = lengthMap.get(thisVO.getPk_psndoc())==null?new HashMap<String, UFDouble>():lengthMap.get(thisVO.getPk_psndoc());
			// ��ǰ�Ӱ�����Ƿ�Ϊ�������
			boolean isDayUnit = TimeItemCopyVO.TIMEITEMUNIT_DAY==timeitemMap.get(thisVO.getPk_overtimetype()).getTimeitemunit().intValue();
			UFDouble timeLength = null;
			UFDouble thisLength = isDayUnit ? thisVO.getActhour().multiply(timeRule.getDaytohour2()) : thisVO.getActhour();
			if(periodMap.get(periodStr)==null){
				timeLength = thisVO.getOvertimealready();
				timeLength = timeLength.add(thisLength);
			}
			else {
				timeLength = periodMap.get(periodStr).add(thisLength);
			}
			// ����������ڹ���ļӰ�ʱ������
			if(maxLength.compareTo(timeLength)<0){
				errPsndocList.add(thisVO.getPk_psndoc());
				errPsnTimeList.add(thisVO.getPk_psndoc()+thisVO.getOvertimebegintime().toString());
			}
			periodMap.put(periodStr, timeLength);
			lengthMap.put(thisVO.getPk_psndoc(), periodMap);
		}
		if(CollectionUtils.isEmpty(errPsndocList))
			return null;
		String[] pk_psndocs = errPsndocList.toArray(new String[0]);
		String psnNames = CommonUtils.getPsnNames(pk_psndocs);
		//�ϸ����ֱ�����쳣
		if(timeRule.getIsrestrictctrlot().booleanValue()){
			String lengthStr = maxLength.toString();
			int index = lengthStr.indexOf('.');
			lengthStr = index<=0 ? lengthStr:lengthStr.substring(0, index+timeRule.getTimedecimal().intValue()+1);
			String errMsg = psnNames + ResHelper.getString("6017overtime","06017overtime0031"/*@res "�Ӱ�ʱ���ѳ���һ�������ڼ��ڵ����Ӱ�ʱ��{0}Сʱ"*/, lengthStr);
			throw new BusinessException(errMsg);
		}
		Map<String,String[]> retMap = new HashMap<String,String[]>();
		retMap.put(psnNames, errPsnTimeList.toArray(new String[0]));
		return retMap ;
	}

	@Override
	public String checkOverTimeHolidayMsg(String pkOrg, OvertimeCommonVO[] vos)
			throws BusinessException {
		Map<String, String[]> checkOverTimeHoliday = checkOverTimeHoliday(pkOrg, vos);
		if(checkOverTimeHoliday == null)
			return null;
		String psnNames = checkOverTimeHoliday.keySet().toArray(new String[0])[0];
		return ResHelper.getString("6017overtime","06017overtime0047"/*@res "{0}�ļӰ൥�к��м����ҼӰ����ͼ��ռӰ�����ж���Ĳ�һ��"*/, psnNames);
	}

	@Override
	public boolean isCanCheck(String pk_org, String pk_psndoc, String pk_psnjob, String beginDate, String enddate, String begintime, String endtime)
			throws BusinessException {
		  OvertimebVO  vo = new OvertimebVO ();
		  vo.setPk_org(pk_org);
		  vo.setPk_psnjob(pk_psnjob);
		  vo.setPk_psndoc(pk_psndoc);
		  vo.setBegindate(new UFLiteralDate(Long.parseLong(beginDate)));
		  vo.setEnddate(new UFLiteralDate(Long.parseLong(enddate)));
		  vo.setOvertimebegintime(new UFDateTime(Long.parseLong(begintime)));
		  vo.setOvertimeendtime(new UFDateTime(Long.parseLong(endtime)));
		return isCanCheck(vo);
	}

	@Override
	public OvertimeCheckResult overtimeChecks(String pk_org,OvertimeCommonVO[] vos) throws BusinessException {
		
		if(ArrayUtils.isEmpty(vos)||StringUtils.isBlank(pk_org))
			return null;
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		if(timeRule==null)
			return null;
//		if(timeRule==null||timeRule.getCtrlothours()==null||timeRule.getCtrlothours().doubleValue() <0)
//			return null;
		List<OvertimeCommonVO> overLengthBillsList = new ArrayList<OvertimeCommonVO>();//��������
		List<OvertimeCommonVO> noCheckBillsList = new ArrayList<OvertimeCommonVO>();//û��ѡ��У���ʶ����
//		UFDouble maxLength = timeRule.getCtrlothours();
		//ȡ��ǰ��֯���а����Ϣ
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		Map<String, AggShiftVO> aggShiftMap = vos[0].getAggShiftMap();
//		if(aggShiftMap == null){
//			aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//			 vos[0].setAggShiftMap(aggShiftMap);
//		}
		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
		//ʱ��Map key: ��Ա����  value��key: �����ڼ� value��ָ����Ա��ָ�������ڼ��ڵļӰ�ʱ��
		Map<String, Map<String, UFDouble>> lengthMap = new HashMap<String, Map<String, UFDouble>>();
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		Map<UFLiteralDate, PeriodVO> periodVOMap = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodMapByDateScopes(pk_org,scopes);
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		InSQLCreator isc = new InSQLCreator();
		String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
		
		
		// �����ѼӰ�ʱ��
		new OvertimeDAO().setAlreadyHour(vos, timeRule, aggShiftMap, periodVOMap, timeitemMap, psnDateOrgMap, psnCalendarMap);
		
		for(OvertimeCommonVO vo:vos){
			
			//ʱ��У��
			if(timeRule.getCtrlothours()!=null&&timeRule.getCtrlothours().doubleValue()>0)
				checkLength(vo, psnDateOrgMap, timeRule, psnCalendarMap, shiftMap, periodVOMap, lengthMap, timeitemMap, timeRule.getCtrlothours(), overLengthBillsList);
//			if(psnCalendarMap==null){
//				String name = CommonUtils.getPsnName(vo.getPk_psndoc());
//				throw new BusinessException(name+(ResHelper.getString("6017overtime","06017overtime0046")
//						/*@res "�ڼӰ൥������������û�п��ڵ�������û���Ű�!"*/));
//			}
			
			//��ʶУ��
			checkNoCheck(vo, noCheckBillsList, aggShiftMap, timeRule, psnCalendarMap, psnDateOrgMap, periodVOMap);
		}
		//�ϸ����ֱ�����쳣
		if(!CollectionUtils.isEmpty(overLengthBillsList)&&timeRule.getCtrlothours()!=null&&timeRule.getIsrestrictctrlot().booleanValue()){
			OvertimeCommonVO[] overLength = overLengthBillsList.toArray(new OvertimeCommonVO[0]);
			String[] pk_psndocs = StringPiecer.getStrArray(overLength, OvertimeCommonVO.PK_PSNDOC);
			String psnNames = CommonUtils.getPsnNames(pk_psndocs);
			String lengthStr = timeRule.getCtrlothours().toString();
			int index = lengthStr.indexOf('.');
			lengthStr = index<=0 ? lengthStr:lengthStr.substring(0, index+timeRule.getTimedecimal().intValue()+1);
			String errMsg = psnNames + ResHelper.getString("6017overtime","06017overtime0031"/*@res "�Ӱ�ʱ���ѳ���һ�������ڼ��ڵ����Ӱ�ʱ��{0}Сʱ"*/, lengthStr);
			throw new BusinessException(errMsg);
			
		}
		//�������У��
		List<OvertimeCommonVO> holruleBillsList =checkOvertimeTypeHoliday(pk_org, vos);//�����յļӰ����ͼ��ռӰ�����岻һ�µĵ���
		OvertimeCheckResult result = new OvertimeCheckResult();
		if(CollectionUtils.isEmpty(overLengthBillsList)
				&&CollectionUtils.isEmpty(noCheckBillsList)
				&&CollectionUtils.isEmpty(holruleBillsList))
			return result;
		String[] pk_psndocs = StringPiecer.getStrArray(vos, OvertimeCommonVO.PK_PSNDOC);
		Map<String, String> psnNamesMap = CommonUtils.getPsnNamesMap(pk_psndocs);
		result.setOverLengthBillsList(overLengthBillsList);
		result.setNoCheckBillsList(noCheckBillsList);
		result.setHolruleBillsList(holruleBillsList);
		result.setPsnNamesMap(psnNamesMap);
		return result;
	}
	
	private void checkNoCheck(OvertimeCommonVO vo,List<OvertimeCommonVO> noCheckBillsList,
			Map<String, AggShiftVO> aggShiftMap,TimeRuleVO timeRule,
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap,
			Map<String, Map<UFLiteralDate, String>> psnDateOrgMap,
			Map<UFLiteralDate, PeriodVO> periodVOMap) throws BusinessException{
		
		//�Ѿ�ѡ�еĲ�������
		if(vo.getIsneedcheck().booleanValue())
			return;
		//��ȡ��ʼʱ��
		UFDateTime beginTime = vo.getOvertimebegintime();
		//��ȡ����ʱ��
		UFDateTime endTime = vo.getOvertimeendtime();
		if(beginTime==null||endTime==null||beginTime.after(endTime))
			return;
		String pk_org = vo.getPk_org();
		// ������ڷ�Χ�����¿��ڵ���Ϊ�ֹ����ڣ�������У��
		TBMPsndocVO psndocVO = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestByPsndocDate(pk_org, vo.getPk_psndoc(), vo.getOvertimebegindate(), vo.getOvertimeenddate());
		if(psndocVO == null || TBMPsndocVO.TBM_PROP_MANUAL == psndocVO.getTbm_prop())
			return ;
		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(vo.getPk_psndoc());
		//û�а����Ϣ��û�����ù�������������У��
		if(calendarMap==null||shiftMap==null)
			return ;
		Map<UFLiteralDate, String> dateOrgMap = psnDateOrgMap.get(vo.getPk_psndoc());
		//����ҪУ���Ҳ��������
		if(!isCanCheck(vo, timeRule, aggShiftMap, shiftMap, calendarMap, dateOrgMap, periodVOMap))
			return;
		noCheckBillsList.add(vo);
		
	}
	
	private void checkLength(OvertimeCommonVO vo,Map<String, Map<UFLiteralDate, String>> psnDateOrgMap,
			TimeRuleVO timeRule,Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap,
			Map<String, ShiftVO> shiftMap,Map<UFLiteralDate, PeriodVO> periodVOMap,
			Map<String, Map<String, UFDouble>> lengthMap,Map<String, OverTimeTypeCopyVO> timeitemMap,
			UFDouble maxLength,List<OvertimeCommonVO> overLengthBillsList) throws BusinessException{
		OvertimeCommonVO thisVO = (OvertimeCommonVO) vo.clone();
		//��Ա����ְ��֯map
		Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(thisVO.getPk_psndoc());
		Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeRule.getTimeZoneMap());
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(thisVO.getPk_psndoc());
		UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap,shiftMap, dateTimeZoneMap);
		if(belongDate==null)
			return;
		PeriodVO period = periodVOMap==null?null:periodVOMap.get(belongDate);
		if(period==null)
			return;
		String periodStr = period.getTimeyear() + "-" + period.getTimemonth();
		Map<String, UFDouble> periodMap = lengthMap.get(thisVO.getPk_psndoc())==null?new HashMap<String, UFDouble>():lengthMap.get(thisVO.getPk_psndoc());
		// ��ǰ�Ӱ�����Ƿ�Ϊ�������
		boolean isDayUnit = TimeItemCopyVO.TIMEITEMUNIT_DAY==timeitemMap.get(thisVO.getPk_overtimetype()).getTimeitemunit().intValue();
		UFDouble timeLength = null;
		UFDouble thisLength = isDayUnit ? thisVO.getActhour().multiply(timeRule.getDaytohour2()) : thisVO.getActhour();
		if(periodMap.get(periodStr)==null){
			timeLength = thisVO.getOvertimealready();
			timeLength = timeLength.add(thisLength);
		}
		else {
			timeLength = periodMap.get(periodStr).add(thisLength);
		}
		// ����������ڹ���ļӰ�ʱ������
		if(maxLength.compareTo(timeLength)<0){
			overLengthBillsList.add(vo);
		}
		periodMap.put(periodStr, timeLength);
		lengthMap.put(thisVO.getPk_psndoc(), periodMap);
	}

	@Override
	public String[] queryPKsByFromWhereSQL(LoginContext context,
			FromWhereSQL fromWhereSQL, Object etraConds,
			boolean approveSite) throws BusinessException {
		String cond = getSQLCondByFromWhereSQL(context.getPk_org(),null, fromWhereSQL, etraConds, approveSite);
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL,OvertimehVO.getDefaultTableName());
		List<String> result = excuteQueryPksBycond(cond, alias);
		return CollectionUtils.isEmpty(result) ? null : (String[])result.toArray(new String[0]);
	}

	@Override
	public AggOvertimeVO[] queryByPks(String[] pks)
			throws BusinessException {
		if(ArrayUtils.isEmpty(pks))
			return null;
		String order = " order by " + OvertimehVO.APPLY_DATE + " desc, " + OvertimehVO.BILL_CODE;
		String insql=OvertimehVO.PK_OVERTIMEH+" in "+MDDAOUtil.getInSql(pks);
		return getServiceTemplate().queryByCondition(AggOvertimeVO.class, insql+order);
//		return getServiceTemplate().queryByPks(AggOvertimeVO.class, paramArrayOfString);
	}
	
	private String getSQLCondByFromWhereSQL(String pk_org, String pk_psndoc,
			FromWhereSQL fromWhereSQL, Object etraConds,boolean blApproveSite)
			throws BusinessException{
		if(etraConds == null)
			etraConds = TAPFBillQueryParams.getDefaultParams(blApproveSite);
		if(!(etraConds instanceof PFQueryParams))
			return null;
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimehVO.getDefaultTableName());
		String strNormalSQL =
            HrPfHelper.getQueryCondition(AggOvertimeVO.class, alias, blApproveSite, pk_org, blApproveSite?((PFQueryParams)etraConds).getBillState():((TAPFBillQueryParams)etraConds).getStateCode());

		if(!StringUtils.isEmpty(pk_psndoc))//��Ա�������գ���ʾ��������Ա������
			strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, alias+"."+OvertimehVO.PK_PSNDOC+"='"+pk_psndoc+"'");


        String dateFilter = blApproveSite?TaNormalQueryUtils.getApproveDatePeriod(HrPfHelper.getFlowBizItf(AggOvertimeVO.class),alias, ((PFQueryParams)etraConds).getApproveDateParam(),((PFQueryParams)etraConds).getBillState())
        			: TaNormalQueryUtils.getDateScopeSql(alias,(TAPFBillQueryParams)etraConds);

        strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, dateFilter);

      //����pk_org Ҫ��Ȼ���л���֯ʱ��Ҳ�Ὣ�����ڱ���֯�ĵ��ݲ�ѯ����
        String othercond="";
        if(StringUtils.isNotEmpty(pk_org))
        	othercond=" and "+alias+".pk_org='"+ pk_org+"' " ;
        
		String order = " order by " + OvertimehVO.APPLY_DATE + " desc, " + OvertimehVO.BILL_CODE;
		if (fromWhereSQL == null || fromWhereSQL.getWhere() == null)
			return strNormalSQL+othercond+ order;
		// ���FromWhereSQL��������
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, OvertimehVO.getDefaultTableName(),
				new String[]{OvertimehVO.PK_OVERTIMEH}, null, null, null, null);
		strNormalSQL +=  " and " + OvertimehVO.PK_OVERTIMEH + " in ( " + sql + " )" + othercond+order;
		return strNormalSQL;
	}
	
	private List<String> excuteQueryPksBycond(String cond, String alias)
			throws BusinessException{
		String sql = "select "+(StringUtils.isEmpty(alias)?"":alias+".")+OvertimehVO.PK_OVERTIMEH+
				" from "+OvertimehVO.getDefaultTableName()+" "+(StringUtils.isEmpty(alias)?"":alias);
		if(!StringUtils.isEmpty(cond))
			sql = sql +" where "+ cond;        
		List<String> result = (List<String>) new BaseDAO().executeQuery(sql, new ColumnListProcessor());//��������
		return result;
	}

	@Override
	public void overtimeTypeChecks(String pk_org,
			String pk_timeitme) throws BusinessException {
		OverTimeTypeCopyVO[] vo=NCLocator.getInstance().lookup(ITimeItemQueryService.class).
				queryOvertimeCopyTypesByOrg(pk_org,OverTimeTypeCopyVO.PK_TIMEITEM+"  = '"+pk_timeitme+"' "/*+" and "+OverTimeTypeCopyVO.ENABLESTATE+" = "+IPubEnumConst.ENABLESTATE_ENABLE*/);
		if(vo==null){
			throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0067"/*@res"�Ӱ���𲻴��ڣ�"*/));
		}else{
			if(vo[0].getEnablestate().intValue()==IPubEnumConst.ENABLESTATE_DISABLE)
				throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0066"/*@res"�Ӱ������ͣ�ã�"*/));
		}		
	}
}
