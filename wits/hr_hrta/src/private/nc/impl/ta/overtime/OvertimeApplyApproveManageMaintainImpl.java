package nc.impl.ta.overtime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.overtime.validator.OvertimeApplyApproveValidator;
import nc.impl.ta.overtime.validator.OvertimeApplyValidatorFactory;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.IOvertimeApplyApproveManageMaintain;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.pubitf.para.SysInitQuery;
import nc.uap.lfw.core.data.Row;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.om.pub.AggVOHelper;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.ta.bill.TaMessageConst;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeConst;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.overtime.pf.validator.PFSaveOvertimeValidator;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.ta.wf.pub.TaWorkFlowManager;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 * �Ӱ��������� ��̨������
 * @author yucheng
 *
 */
public class OvertimeApplyApproveManageMaintainImpl extends TaWorkFlowManager<OvertimehVO, OvertimebVO> implements
		IOvertimeApplyApproveManageMaintain {

	private HrBatchService serviceTemplate;
	
	private HrBatchService getServiceTemplate() {
		if(serviceTemplate == null) {
			serviceTemplate = new HrBatchService("a7e298c9-3211-489d-8698-7ae65a922156");
		}
		return serviceTemplate;
	}
	
	@Override
	public void deleteArrayData(AggOvertimeVO[] vos) throws BusinessException {
		getServiceTemplate().delete(vos);
		String pk_org = vos[0].getHeadVO().getPk_org();
		returnBillCodeOnDelete(pk_org, getBillType(), getBillCodeFormVOs(vos));
	}

	@Override
	public void deleteData(AggOvertimeVO vo) throws BusinessException {
		getServiceTemplate().delete(vo);
		OvertimehVO mainVO = (OvertimehVO)vo.getParentVO();
		returnBillCodeOnDelete(mainVO.getPk_org(), mainVO.getBill_code());
	}
	
	@Override
	public AggOvertimeVO[] insertArrayData(AggOvertimeVO[] vos) throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AggOvertimeVO insertData(AggOvertimeVO vo) throws BusinessException {
		OvertimehVO mainVO = (OvertimehVO)vo.getParentVO();
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vo.getBodyVOs());
		
		//У��
        DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new PFSaveOvertimeValidator());
		vService.validate(vo);
		setValidatorFactory();	
		new OvertimeApplyQueryMaintainImpl().check(vo);
		
		//У���Ƿ��ҵ��Ԫ-������BillValidatorAtServer������Ϊ��savebillactionʱ��ͻ���check������ʱ������У��˳�򲻶�
		List<OvertimebVO> bVOList = BillProcessHelper.toOvertimebVOList(new AggOvertimeVO[]{vo});
		OvertimebVO[] bvos = bVOList.toArray(new OvertimebVO[0]);
		BillValidatorAtServer.checkCrossBU(vo.getOvertimehVO().getPk_org(), bvos);
		
		AggOvertimeVO aggVO =  getServiceTemplate().insert(vo)[0];
		//�ύ���ݱ���
		commitBillCode(mainVO.getPk_org(),  mainVO.getBill_code());
		return aggVO;
	}

	@Override
	public AggOvertimeVO insertDataDirect(AggOvertimeVO vo)
			throws BusinessException {
		OvertimehVO mainVO = (OvertimehVO)vo.getParentVO();
		AggOvertimeVO aggVO =  getServiceTemplate().insert(vo)[0];
		//�ύ���ݱ���
		commitBillCode(mainVO.getPk_org(),  mainVO.getBill_code());
		return aggVO;
	}
	@Override
	public AggOvertimeVO[] updateArrayData(AggOvertimeVO[] vos) throws BusinessException {
		return updateData(vos, true, true);
	}
	
	protected AggOvertimeVO[] updateData(AggOvertimeVO[] vos,boolean isSetAuditInfo,boolean needCheck) throws BusinessException {
		BillMethods.processBeginEndDatePkJobOrgTimeZone(BillProcessHelper.toOvertimebVOList(vos).toArray(new OvertimebVO[0]));
		String pk_org = vos[0].getHeadVO().getPk_org();
		for(AggOvertimeVO aggVO:vos){
			OvertimebVO[] bvos = aggVO.getBodyVOs();
			int len = ArrayUtils.getLength(bvos);
			if(len==0)
				continue;
			OvertimehVO hvo = aggVO.getOvertimehVO();
			for(OvertimebVO bvo:bvos){
				bvo.setPk_overtimetype(hvo.getPk_overtimetype());
				bvo.setPk_overtimetypecopy(hvo.getPk_overtimetypecopy());
				bvo.setPk_psndoc(hvo.getPk_psndoc());
				bvo.setPk_psnjob(hvo.getPk_psnjob());
				bvo.setPk_psnorg(hvo.getPk_psnorg());
				//��ֹ��ʾ���ѼӰ�ʱ������ǰ�ͱ����һ��
				if(VOStatus.UNCHANGED==bvo.getStatus())
					bvo.setStatus(VOStatus.UPDATED);
			}
		}
		if(needCheck){
			OvertimeApplyApproveValidator validator = new OvertimeApplyApproveValidator();
			validator.checkOvertimeAggVOs(vos);
			setValidatorFactory();
			new OvertimeApplyQueryMaintainImpl().check(pk_org,BillProcessHelper.toOvertimebVOList(vos).toArray(new OvertimebVO[0]));
			OvertimebVO[] checkvos=BillProcessHelper.toOvertimebVOList(vos).toArray(new OvertimebVO[0]);
			//У���Ƿ��ҵ��Ԫ-������BillValidatorAtServer������Ϊ��savebillactionʱ��ͻ���check������ʱ������У��˳�򲻶�
			BillValidatorAtServer.checkCrossBU(pk_org, checkvos);
		}
		return getServiceTemplate().update(isSetAuditInfo, vos);
	}

	@Override
	public AggOvertimeVO updateData(AggOvertimeVO vo) throws BusinessException {
		return updateArrayData(new AggOvertimeVO[]{vo})[0];
	}

	/**
	 * ����У��
	 */
	private void setValidatorFactory() {
		getServiceTemplate().setValidatorFactory(new OvertimeApplyValidatorFactory());
	}
	
	/**
	 * ֱ��
	 */
	@Override
	public AggOvertimeVO[] directApprove(int directApproveResult, String approveNote, AggOvertimeVO[] vos)
			throws BusinessException {
		String pk_org = vos[0].getOvertimehVO().getPk_org();
		
		//�����ڼ��Ѿ����ĵ��ݣ��ǿ��Բ��غ�������ͨ����
		if(directApproveResult!=IBillStatus.FREE&&directApproveResult!=IBillStatus.NOPASS)
			BillValidatorAtServer.checkPeriod(pk_org, BillProcessHelper.toOvertimebVOs(vos));
		//�������
		WorkflownoteVO[] worknoteVOs = new WorkflownoteVO[vos.length];
		//�����Ҫ�����ֶεĵ����������飬�½���Ŀ����Ϊͳһ�����µĲ���
		OvertimehVO[] updateMainVOs = new OvertimehVO[vos.length];
		//��Ҫ���µ��ֶ�
		String[] updateFields = {OvertimehVO.APPROVER, OvertimehVO.APPROVE_TIME, OvertimehVO.APPROVE_NOTE,
				OvertimehVO.APPROVE_STATE};
		for ( int i = 0 ; i < vos.length ; i++ ) {
			OvertimehVO mainvo = vos[i].getOvertimehVO();
			// ��ȡ��д��Ϣ����������Ҫ�޸ĵ��ֶ�
			updateMainVOs[i] = changeBillData(mainvo, updateFields,approveNote, directApproveResult);
			// ִ����������ǰ����������Ϣд��pub_workflownote��
			worknoteVOs[i] = buildWorkflownoteVO( directApproveResult ,  approveNote ,mainvo);
		}
		//���������������
		getIPersistenceUpdate().insertVOArray( null , worknoteVOs , null );
		//�������µ�����Ϣ
		getIPersistenceUpdate().updateVOArray(null, updateMainVOs, updateFields, null);
		//�����׼���ſɽ��������
		if(directApproveResult == IPfRetCheckInfo.PASSING)
			exexBills(pk_org, vos);
		return vos;
	}

	@Override
	public AggOvertimeVO directApprove(AggOvertimeVO vo)
			throws BusinessException {
		return null;
	}
	
	/**
	 * �����������
	 * @param directApproveResult	�� ����״̬����׼�ȣ�
	 * @param approveNote	:	�������
	 * @param mainvo	:	��������
	 * @return
	 * @throws BusinessException
	 */
	public WorkflownoteVO buildWorkflownoteVO( int directApproveResult , String approveNote , OvertimehVO mainvo )
		throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid( mainvo.getPk_overtimeh() );// ����ID
		worknoteVO.setBillVersionPK(mainvo.getPrimaryKey());
		worknoteVO.setChecknote( approveNote );// �������
		worknoteVO.setDealdate( PubEnv.getServerTime() );// ����ʱ��
		worknoteVO.setSenddate( PubEnv.getServerTime() );// ��������
		worknoteVO.setPk_org( mainvo.getPk_org() );// ��֯
		worknoteVO.setBillno( mainvo.getBill_code() );// ���ݺ�
		worknoteVO.setSenderman( mainvo.getApprover() == null ? mainvo.getBillmaker() : mainvo.getApprover() );// ������
		worknoteVO.setApproveresult(IPfRetCheckInfo.NOSTATE == directApproveResult ? "R" : IPfRetCheckInfo.PASSING == directApproveResult
				? "Y": "N");
		worknoteVO.setApprovestatus( 1 );// ֱ����״̬
		worknoteVO.setIscheck(IPfRetCheckInfo.PASSING == directApproveResult ? "Y" : IPfRetCheckInfo.NOPASS == directApproveResult
				? "N": "X");
		worknoteVO.setActiontype( "APPROVE" );
		worknoteVO.setCheckman( mainvo.getApprover() );	//������
		worknoteVO.setWorkflow_type( WorkflowTypeEnum.Approveflow.getIntValue() );
		worknoteVO.setPk_billtype( mainvo.getPk_billtype() );// ��������
		return worknoteVO;
	}
	
	/**
	 *  ����������Ϣ¼��Ӱ��¼�����̻�д��Ϣ
	 * @param updateFields�� ��Ҫ���µ��ֶ�
	 * @param mainvo
	 * @param approveNote���������
	 * @param directApproveResult������״̬����׼�ȣ�
	 * @return
	 * @throws BusinessException
	 */
	private OvertimehVO changeBillData( OvertimehVO mainvo,String[] updateFields , 
			String approveNote, Integer directApproveResult) throws BusinessException {
		if (mainvo == null) 
			return null;
		mainvo.setAttributeValue(updateFields[0], PubEnv.getPk_user());	 //������pk_id
		mainvo.setAttributeValue(updateFields[1], PubEnv.getServerTime());	//����ʱ��
		mainvo.setAttributeValue(updateFields[2], approveNote);	//�������
		mainvo.setAttributeValue(updateFields[3], directApproveResult);	//����״̬
		return mainvo;
	}
	
	/**
	 * �������ύ�������������ص�
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doCommit(AggOvertimeVO[] vos) throws BusinessException {
		for ( AggOvertimeVO vo : vos ) {
			OvertimehVO billvo = vo.getOvertimehVO();
			// ���µ���״̬
			billvo.setApprove_state( IPfRetCheckInfo.COMMIT );
			vo.setParentVO( billvo );
		}
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		updateData(vos, false, false);
		String pk_org = vos[0].getOvertimehVO().getPk_org();
		Integer approvetype = SysInitQuery.getParaInt(pk_org, OvertimeConst.OVERTIMETYPE_PARAM);
		if (approvetype != null && approvetype.intValue() == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT) {
			// ������ʽΪֱ��ʱ����֪ͨ
//			sendMessage( OvertimeConst.PK_NOTICE_SORT , PubEnv.getPk_group() , pk_org , vos );
			sendDirApprMessage(pk_org, TaMessageConst.OVERTIMEMSGCODE, vos);
		}
		return vos;
	}
	
	/**
	 * ����֪ͨ��Ϣ
	 * @param pk_sort
	 * @param pkGroup
	 * @param pk_org
	 * @param vos
	 * @throws BusinessException
	 */
//	private void sendMessage(String pk_sort, String pkGroup, String pk_org, AggOvertimeVO[] vos)throws BusinessException {
//		INotice setvice = NCLocator.getInstance().lookup(INotice.class);
//		NoticeTempletVO[] nt = setvice.queryDistributedTemplates(pk_sort, pkGroup, pk_org, true);
//		if (nt != null && nt.length > 0) {
//			nt[0].setTransferValues(createUserValue(vos));
//			nt[0].setContent(TaSendMsgHelper.getNewContent(nt[0].getContent(), vos.length, OvertimeConst.FIELDCODE));
//			if (StringUtils.isBlank( nt[0].getCurrentUserPk() ) || nt[0].getCurrentUserPk().length() != 20) {
//				// ���ģ��ĵ�ǰ�û�Ϊ�գ����ϵ�ǰ�û�����NCϵͳ�û�
//				nt[0].setCurrentUserPk( PubEnv.getPk_user() != null && PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
//						: INCSystemUserConst.NC_USER_PK );
//			}
//			setvice.sendNotice_RequiresNew(nt[0], pk_org, false);
//		}
//	}

	/**
	 * �����������������������ص�
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doApprove(AggOvertimeVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		syncHeadInfoToBody(vos);
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		//2012.8.6���������ۺ�ȷ���������ѷ���ڼ�����뵥�����������غ���˲�ͨ�������仰˵
		//���ڲ��غ���˲�ͨ�������뵥������ҪУ���ڼ���
		OvertimehVO headVO = vos[0].getOvertimehVO();
		int appStatus = headVO.getApprove_state();
		if(appStatus!=IBillStatus.FREE&&appStatus!=IBillStatus.NOPASS)
			PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		vos = updateData(vos, false, false);
		if (appStatus == IBillStatus.CHECKPASS) 
			exexBills(headVO.getPk_org(), vos);
		return vos;
	}
	
	/**
	 * �������ջأ������������ص�
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doRecall(AggOvertimeVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		syncHeadInfoToBody(vos);
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		vos = updateData(vos, false, false);
		return vos;
	}
	
	/**
	 * ���������ֱ����ɣ��ڼӰ�ǼǱ���¼
	 * @param tempvo
	 * @param vos
	 * @throws BusinessException
	 */
	public void exexBills(String pk_org,AggOvertimeVO[] vos) throws BusinessException{
		List<OvertimeRegVO> regvos = new ArrayList<OvertimeRegVO>();
		UFLiteralDate beiginTime= new UFLiteralDate();
		boolean isToRest=false; 
		for(AggOvertimeVO aggvo : vos) {
			OvertimehVO mainvo = aggvo.getOvertimehVO();
			OvertimebVO[] subvos = aggvo.getOvertimebVOs();
			isToRest=mainvo.getIstorest().booleanValue();
			if(ArrayUtils.isEmpty(subvos)) 
				continue;
			for(OvertimebVO subvo : subvos) {
				if( beiginTime.after(subvo.getBegindate()))
				{
					beiginTime = subvo.getBegindate();
				}
			 }
			for(OvertimebVO subvo : subvos) {
				regvos.add(buildOvertimeRegVO(mainvo, subvo,String.valueOf(beiginTime.getYear()),String.valueOf(beiginTime.getMonth())));
			}
		}
		if(CollectionUtils.isNotEmpty(regvos))	{
			//MOD James
			OvertimeRegVO[] newRegvos = new OvertimeRegisterMaintainImpl().insertData(regvos.toArray(new OvertimeRegVO[0]),false);
			
			if(isToRest)
			{
				 for(OvertimeRegVO vo : newRegvos){
					if( beiginTime.after(vo.getBegindate()))
					{
						beiginTime = vo.getBegindate();
					}
				 }
				
				try {
					over2Rest(newRegvos,String.valueOf(beiginTime.getYear()));
				} catch (HrssException e) {					
					Logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	
	public OvertimeRegVO[] over2Rest(OvertimeRegVO[] vos, String toRestYear)
			throws BusinessException, HrssException {

		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
//		for(OvertimeRegVO vo:vos){
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		String pk_org = vos[0].getPk_org();
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
		// ȡ�����ڼ��ڿ�ת���ݵ����ʱ��
//		UFDouble maxHour = SysInitQuery.getParaDbl(pk_org, OvertimeConst.OVERTIMETOREST_PARAM);
		UFDouble maxHour = timeRule.getTorestlongest();
		if(maxHour==null) {
			maxHour = UFDouble.ZERO_DBL;
		}
		DecimalFormat dcmFmt = new DecimalFormat("0.00");
		maxHour = new UFDouble(dcmFmt.format(maxHour));
		ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
		TimeItemCopyVO timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, vos[0].getPk_overtimetype(), TimeItemVO.OVERTIME_TYPE);
		
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		Map<UFLiteralDate, PeriodVO> periodVOMap = periodService.queryPeriodMapByDateScopes(pk_org, scopes);
		InSQLCreator isc = new InSQLCreator();
			String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
			Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
			Map<OvertimeCommonVO, OvertimeCommonVO[]> overtimeMap = new OvertimeDAO().getOvertimeVOInPeriod(vos, timeZoneMap, shiftMap, psnDateOrgMap, psnCalendarMap, periodVOMap);

			//����ѭ������
			Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
			List<OvertimeRegVO> returnVOs = new ArrayList<OvertimeRegVO>();
			Map<String, UFDouble> hourMap = new HashMap<String, UFDouble>();
			for(String pk_psndoc:psnRegVOMap.keySet()){
				OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
				Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
				Map<UFLiteralDate, TimeZone> dateTimeZoneMap= CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
				Map<String, UFDouble> periodMap = new HashMap<String, UFDouble>();
				for(OvertimeRegVO vo:regVOs){
//					if(vo.getIstorest().booleanValue()) {
//						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0033")/*@res "������ת���ݵļ�¼��"*/);
//					}
					// �Ӱ൥������ ���ڿ����ڼ��Ƿ��ѷ��
					Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(pk_psndoc);
					UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap, dateTimeZoneMap);
					PeriodVO period = periodVOMap.get(belongDate);
					//MOD James
//					if(period==null||period.isSeal()) {
//						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0034")/*@res "���ݹ��������ڿ����ڼ䲻���ڻ��ѷ�棡"*/);
//					}
					UFDouble sumHour = getSumTorstHour(overtimeMap.get(vo));
					String periodKey = period.getTimeyear()+"-"+period.getTimemonth();
					// ת����ʱ��
					
					UFDouble overtimetorest = timeItemCopyVO.getOvertimetorest();
			        if (overtimetorest == null)
			          overtimetorest = UFDouble.ZERO_DBL;		
					UFDouble torestHour = vo.getLength().multiply(overtimetorest.div(100.0D));
					if(timeItemCopyVO.getRoundmode().equals(0)){						
						torestHour=new UFDouble(Math.floor(torestHour.doubleValue()));
						}
					else if(timeItemCopyVO.getRoundmode().equals(1)){
						torestHour=new UFDouble(Math.ceil(torestHour.doubleValue()));
					}else if(timeItemCopyVO.getRoundmode().equals(2)){
						torestHour=new UFDouble(Math.round(torestHour.doubleValue()));
					}
					
					// ȡ��ת����ʱ��map�б����ʱ����Ϣ
					UFDouble alreadyHour = periodMap.get(periodKey)==null?UFDouble.ZERO_DBL:periodMap.get(periodKey);
					alreadyHour = alreadyHour.add(torestHour);
					if(maxHour.doubleValue()>=0&&maxHour.compareTo(sumHour.add(alreadyHour))<0) {
						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0035"/*@res "ת����ʱ�����������ڼ���ת���ݵ����ʱ���� {0}Сʱ��"*/,  maxHour.toString()));
					}
					periodMap.put(periodKey, alreadyHour);
					// ������Աת����ʱ��
					UFDouble psnOrgHour = hourMap.get(vo.getPk_psnorg());
					hourMap.put(vo.getPk_psnorg(), psnOrgHour==null?torestHour:psnOrgHour.add(torestHour));
					// �޸ĵǼ���Ϣ
					vo.setTorestyear(toRestYear);
					vo.setToresthour(torestHour);
					//vo.setTorestmonth(toRestMonth);
					vo.setIstorest(UFBoolean.TRUE);
					// ȡ�Ӱ����ʵ��, ��ǰ̨�����ڵ�λΪ���ʱ��ת��ΪСʱ������Ҫת������
					OverTimeTypeCopyVO typeVO = timeitemMap.get(vo.getPk_overtimetype());
					if(TimeItemCopyVO.TIMEITEMUNIT_DAY==typeVO.getTimeitemunit().intValue()){
						vo.setActhour(vo.getActhour().div(timeRule.getDaytohour2()));
					}
//					// ���ò���ʱ��
//					vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
					//���¼Ӱ�Ǽǵ���
					returnVOs.add(vo);
				}
			}
			// ���޸ļӰ�ת���ݽ������ݺ͸��²����ᵽѭ���⣬�Ż�sql���� 2012-10-23
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).processBeforeRestOvertime(pk_org, hourMap, toRestYear, null, true);
			//ҵ����־
			TaBusilogUtil.writeOvertimeReg2RestBusiLog(returnVOs.toArray(new OvertimeRegVO[0]));
			return getServiceTemplate().update(true, returnVOs.toArray(new OvertimeRegVO[0]));
	}
	/**
	 * ȡ���������ڿ����ڼ���ת���ݵ��ݵ���ʱ��
	 * @param vo
	 * @param shiftMap
	 * @param timeZone
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getSumTorstHour(OvertimeCommonVO[] belongVOs) throws BusinessException {
		if(ArrayUtils.isEmpty(belongVOs)) {
			return UFDouble.ZERO_DBL;
		}
		UFDouble sumHour = UFDouble.ZERO_DBL;
		for(OvertimeCommonVO belongVO:belongVOs){
			if(!(belongVO instanceof OvertimeRegVO)) {
				continue;
			}
			OvertimeRegVO regVO = (OvertimeRegVO) belongVO;
			if(!regVO.getIstorest().booleanValue()) {
				continue;
			}
			sumHour = sumHour.add(regVO.getToresthour());
		}
		return sumHour;
	}
	/**
	 * ����һ���Ӱ�ǼǼ�¼   
	 * @param mainvo
	 * @param subvo
	 * @return
	 */
	private OvertimeRegVO buildOvertimeRegVO(OvertimehVO mainvo,OvertimebVO subvo, String toRestYear, String toRestMonth) {
		OvertimeRegVO regvo = new OvertimeRegVO();
		regvo.setBillsource(ICommonConst.BILL_SOURCE_APPLY);//������Դ
		
		regvo.setPk_billsourceh(mainvo.getPk_overtimeh());//�Ӱ�����������Ӧ��¼��PK
		regvo.setPk_billsourceb(subvo.getPk_overtimeb());//�Ӱ���ϸ����Ӧ��¼��PK
		regvo.setPk_overtimegen(null);//�Ӱ����ɼ�¼��PK
		regvo.setPk_group(mainvo.getPk_group());//��������
		regvo.setPk_org(mainvo.getPk_org());//������֯		
		regvo.setPk_org_v(mainvo.getPk_org_v());//��֯�汾����
		regvo.setPk_dept_v(mainvo.getPk_dept_v());//���Ű汾����			
		regvo.setPk_overtimetype(mainvo.getPk_overtimetype());//�Ӱ��������
		regvo.setPk_overtimetypecopy(mainvo.getPk_overtimetypecopy());//�Ӱ���𿽱�����
		regvo.setPk_psndoc(mainvo.getPk_psndoc());//��Ա��������
		regvo.setPk_psnjob(mainvo.getPk_psnjob());//��Ա��ְ����
		regvo.setPk_psnorg(mainvo.getPk_psnorg());//��֯��ϵ����
		regvo.setIstorest(mainvo.getIstorest());
		//MOD ����ר������ James
		regvo.setProjectcode(mainvo.getProjectcode());//ר������
		regvo.setActhour(subvo.getActhour());//ʵ��ʱ��
		regvo.setDeduct(subvo.getDeduct()); //�۳�ʱ��
		regvo.setIscheck(UFBoolean.FALSE);	//�Ƿ���У��
		regvo.setIsneedcheck(subvo.getIsneedcheck());//�Ƿ���ҪУ��
		//regvo.setIstorest(UFBoolean.FALSE); //�Ƿ�ת����
		//regvo.setToresthour(UFDouble.ZERO_DBL);
		if(mainvo.getIstorest().booleanValue())			
		{
			regvo.setToresthour(subvo.getOvertimehour());
			regvo.setTorestmonth(toRestMonth);
			regvo.setTorestyear(toRestYear);
		}
		else{
			regvo.setToresthour(UFDouble.ZERO_DBL);
			regvo.setTorestmonth(null);
			regvo.setTorestyear(null);
		}	
		
		regvo.setOvertimealready(subvo.getOvertimealready());	//���ڿ����ڼ��ѼӰ�ʱ��
		regvo.setOvertimebegindate(subvo.getOvertimebegindate());//��ʼ����
		regvo.setOvertimebegintime(subvo.getOvertimebegintime());//��ʼʱ��
		regvo.setOvertimeenddate(subvo.getOvertimeenddate());//��������
		regvo.setOvertimeendtime(subvo.getOvertimeendtime());//����ʱ��
		regvo.setOvertimehour(subvo.getOvertimehour());	//�Ӱ�ʱ��
		regvo.setOvertimeremark(subvo.getOvertimeremark());//�Ӱ�˵��
		
		regvo.setStatus(VOStatus.NEW);//����״̬
		
		return regvo;
	}
	
	/**
	 * ���������������������ص�
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doUnApprove(AggOvertimeVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		//У�鵥��״̬
		OvertimehVO[] mainvos = (OvertimehVO[]) AggVOHelper.getParentVOArrayFromAggVOs(vos,OvertimehVO.class);
		AggOvertimeVO[] oldvos = getServiceTemplate().queryByPks(AggOvertimeVO.class, StringPiecer.getStrArray(mainvos, OvertimehVO.PK_OVERTIMEH));
		for(AggOvertimeVO oldvo:oldvos){
			checkPFPassingState(oldvo.getOvertimehVO().getApprove_state());
		}
		syncHeadInfoToBody(vos);
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toOvertimebVOs(vos));
		vos = updateData(vos, false, false);
		return vos;
	}
	
	/**
	 * ������ɾ���������������ص�
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doDelete(AggOvertimeVO[] vos) throws BusinessException {
		for ( AggOvertimeVO vo : vos ) {
			OvertimehVO headVO = vo.getOvertimehVO();
			String billType = headVO.getPk_billtype();
			deleteOldWorknote( headVO.getPrimaryKey() , billType );
		}
		deleteArrayData(vos);
		return vos;
	}

	/**
	 * ��������
	 */
	@Override
	public AggOvertimeVO[] insertData(OvertimebVO[] vos, boolean mergeBill)
			throws BusinessException {
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		if(ArrayUtils.isEmpty(vos))
			return null;
		String pk_org = vos[0].getPk_org();
		setValidatorFactory();
//		new OvertimeApplyQueryMaintainImpl().check(pk_org, vos);//����ʱ����ʾ��У�����Ѿ�У����ˣ�Ϊ��Ч������˴�����У��
		BillValidatorAtServer.checkCrossBU(pk_org, vos);
		
		AggOvertimeVO[] mergedAggVOs = mergeSubvo(vos, mergeBill);
		
		//У��
		DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new PFSaveOvertimeValidator());
		vService.validate(mergedAggVOs);
		setValidatorFactory();
		BillValidatorAtServer.checkOvertime(vos[0].getPk_org(), mergedAggVOs);
		
		String[] billCodes = null;
		try {
			billCodes = generateBillCodes(pk_org, mergedAggVOs.length);
			for (int i = 0; i < billCodes.length; i++) {
				mergedAggVOs[i].getHeadVO().setBill_code(billCodes[i]);
			}
			AggOvertimeVO[] aggVOs =  getServiceTemplate().insert(mergedAggVOs);
			commitBillCodes(pk_org, billCodes);
			return aggVOs;
		} catch(Exception e){
			rollbackBillCodes(pk_org, billCodes);//����ʧ������˵��ݺ�
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	/**
	 * ���������ϲ���VO
	 * @param vos
	 * @param mergeBill
	 * @return
	 * @throws BusinessException
	 */
	private AggOvertimeVO[] mergeSubvo(OvertimebVO[] vos, boolean mergeBill) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		// �����ѼӰ�ʱ����У���ʶ,�˴�����������������õ�У���ʶ
//		new OvertimeDAO().setAlreadyHourAndCheckFlag(vos);
		new OvertimeDAO().setAlreadyHour(vos);
		AggOvertimeVO[] aggvoArray = null;
		if(!mergeBill){
			aggvoArray = new AggOvertimeVO[vos.length];
			for(int i=0;i<aggvoArray.length;i++){
				AggOvertimeVO aggVO = new AggOvertimeVO();
				aggvoArray[i] = aggVO;
				OvertimebVO[] bvos = new OvertimebVO[]{vos[i]};
				aggVO.setParentVO(createMainVO(bvos));
				aggVO.setChildrenVO(bvos);
			}
		}
		else{
			aggvoArray = mergeSubVOs(AggOvertimeVO.class, vos);
		}
		return aggvoArray;
	}
	
	/**
	 * ���������ϲ���VO
	 * @param vos
	 * @param mergeBill
	 * @return
	 * @throws BusinessException
	 */
//	@Deprecated
//	private AggOvertimeVO[] mergeSubvo1(OvertimebVO[] vos, boolean mergeBill) throws BusinessException {
//		if(ArrayUtils.isEmpty(vos))
//			return null;
//		String pk_org = vos[0].getPk_org();
//		
//		//ȡ��ǰ��֯���а����Ϣ
//		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
//		TimeRuleVO timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
//		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
//		IDateScope[] mergedScopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
//		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
//		Map<UFLiteralDate, PeriodVO> periodMap = periodService.queryPeriodMapByDateScopes(pk_org, mergedScopes);
//		OvertimeDAO dao = new OvertimeDAO();
//		InSQLCreator isc = null;
//		try{
//			isc = new InSQLCreator();
//			String psndocInSQL = isc.getInSQL(vos, OvertimebVO.PK_PSNDOC);
//			ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
//			Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, mergedScopes);
//			IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
//			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnAggCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, mergedScopes, psndocInSQL);
//			for(OvertimebVO bvo:vos){
//				String pk_psndoc = bvo.getPk_psndoc();
//				Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
//				Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnAggCalendarMap)?null:psnAggCalendarMap.get(pk_psndoc);
//				// ���㵱ǰ�����ڼ��ѼӰ�ʱ��
//				dao.setAlreadyHour(bvo, shiftMap, 
//						CommonMethods.createDateTimeZoneMap(dateOrgMap,timeruleVO.getTimeZoneMap()),calendarMap,timeitemMap,periodMap,timeruleVO);
//				//���ѡ�����Ƿ�����У�鵫����������У��Ĺ���Ҫǿ��ת��Ϊ������У��
//				if(bvo.getIsneedcheck().booleanValue()&&!new OvertimeApplyQueryMaintainImpl().isCanCheck(bvo, timeruleVO, aggShiftMap, shiftMap, calendarMap, dateOrgMap, periodMap))
//					bvo.setIsneedcheck(UFBoolean.FALSE);
//			}
//		}
//		finally{
//			if(isc!=null)
//				isc.clear();
//		}
//
//		AggOvertimeVO[] aggvoArray = null;
//		if(!mergeBill){
//			aggvoArray = new AggOvertimeVO[vos.length];
//			for(int i=0;i<aggvoArray.length;i++){
//				AggOvertimeVO aggVO = new AggOvertimeVO();
//				aggvoArray[i] = aggVO;
//				OvertimebVO[] bvos = new OvertimebVO[]{vos[i]};
//				aggVO.setParentVO(createMainVO(bvos));
//				aggVO.setChildrenVO(bvos);
//			}
//		}
//		else{
//			aggvoArray = mergeSubVOs(AggOvertimeVO.class, vos);
//		}
//		return aggvoArray;
//	}
	
	/**
	 * �����ӱ�VO�����Ӱ�����VO
	 * @param vo
	 * @param pk_group
	 * @param pk_org
	 * @return
	 */
	@Override
	protected OvertimehVO createMainVO(OvertimebVO[] vos){
		OvertimebVO vo = vos[0];
		OvertimehVO headVO = new OvertimehVO();
		headVO.setIshrssbill(UFBoolean.FALSE);
		headVO.setPk_group(vo.getPk_group());
		headVO.setPk_org(vo.getPk_org());
		headVO.setPk_psndoc(vo.getPk_psndoc());
		headVO.setPk_psnjob(vo.getPk_psnjob());
		headVO.setPk_psnorg(vo.getPk_psnorg());
		headVO.setPk_org_v(vo.getPk_org_v());
		headVO.setPk_dept_v(vo.getPk_dept_v());
		headVO.setStatus(VOStatus.NEW);
		headVO.setApprove_state( IPfRetCheckInfo.NOSTATE );
		headVO.setBillmaker( PubEnv.getPk_user() );
		headVO.setApply_date( PubEnv.getServerLiteralDate() );
		headVO.setFun_code(OvertimeConst.FUNCODE_OVERTIMEAPPLE);
		headVO.setTranstypeid(vo.getTranstypeid());
		headVO.setTranstype(vo.getTranstype());
		headVO.setPk_billtype(OvertimeConst.BILLTYPE);
		headVO.setPk_overtimetype(vo.getPk_overtimetype());
		headVO.setPk_overtimetypecopy(vo.getPk_overtimetypecopy());
		
		if(vos.length>1){
			UFDouble sumHour = new UFDouble();
			for(OvertimebVO bvo:vos){
				if(bvo.getOvertimehour()!=null)
					sumHour = sumHour.add(bvo.getOvertimehour());
			}
			headVO.setSumhour(sumHour);
		}
		else{
			headVO.setSumhour(vo.getOvertimehour());
		}
		
		return headVO;
	}

	@Override
	protected String getBillCodeFieldName() {
		return OvertimehVO.BILL_CODE;
	}

	@Override
	protected String getBillType() {
		return OvertimeConst.BILLTYPE;
	}

	@Override
	protected Class<?> getHeadVOClass() {
		return OvertimehVO.class;
	}

	@Override
	protected UserValueConfig getUserValueConfig() {
		UserValueConfig config = new UserValueConfig();
		config.setBillCodeFieldName(OvertimehVO.BILL_CODE);
		config.setApproveStateFieldName(OvertimehVO.APPROVE_STATE);
		config.setFieldCodes(OvertimeConst.FIELDCODE);
		return config;
	}
	
	@Override
	protected void syncHeadInfoToBody(AggregatedValueObject[] aggVOs) {
		if(ArrayUtils.isEmpty(aggVOs))
			return;
		for(AggregatedValueObject aggVO:aggVOs){
			OvertimebVO[] bvos = (OvertimebVO[])aggVO.getChildrenVO();
			if(ArrayUtils.isEmpty(bvos))
				continue;
			OvertimehVO hvo = (OvertimehVO)aggVO.getParentVO();
			String pk_psndoc = hvo.getPk_psndoc();
			String pk_psnjob = hvo.getPk_psnjob();
			String pk_psnorg = hvo.getPk_psnorg();
			String pk_timeitem = hvo.getPk_timeitem();
			String pk_timeitemcopy = hvo.getPk_overtimetypecopy();
			for(OvertimebVO bvo:bvos){
				bvo.setPk_psndoc(pk_psndoc);
				bvo.setPk_psnjob(pk_psnjob);
				bvo.setPk_psnorg(pk_psnorg);
				bvo.setPk_timeitem(pk_timeitem);
				bvo.setPk_overtimetypecopy(pk_timeitemcopy);
			}
		}
	}
}