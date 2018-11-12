package nc.impl.ta.leave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrta.leaveplan.changetime.LeavePlanChangeTime;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.leave.validator.LeaveApplyApproveValidator;
import nc.impl.ta.leave.validator.LeaveApplyValidatorFactory;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.ILeaveAppInfoDisplayer;
import nc.itf.ta.ILeaveApplyApproveManageMaintain;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.om.pub.AggVOHelper;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.ta.bill.TaMessageConst;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.leave.pf.validator.PFSaveLeaveValidator;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.wf.pub.TaWorkFlowManager;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveAplApvManageMaintainImpl extends TaWorkFlowManager<LeavehVO, LeavebVO> implements ILeaveApplyApproveManageMaintain {
	
	private HrBatchService serviceTemplate;
	 
	public HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService("4c9f5cd7-d8c5-41c3-aa99-b3f12bdb54a8");
			setValidatorFactory();
		}
		return serviceTemplate;
	}
 
	@Override
	public AggLeaveVO directApprove(AggLeaveVO vo)
			throws BusinessException {
		return null;
	}
	
	/**
	 * ֱ��
	 */
	@Override
	public AggLeaveVO[] directApprove(int directApproveResult,
			String approveNote, AggLeaveVO[] vos)
			throws BusinessException {
		LeavehVO tempmainvo = (LeavehVO)vos[0].getParentVO();
		String pk_org = tempmainvo.getPk_org();
		//�����ڼ��Ѿ����ĵ��ݣ��ǿ��Բ��غ�������ͨ����
		if(directApproveResult!=IBillStatus.FREE&&directApproveResult!=IBillStatus.NOPASS)
			BillValidatorAtServer.checkPeriod(pk_org, BillProcessHelper.toLeavebVOs(vos));
		//�������
		WorkflownoteVO[] worknoteVOs = new WorkflownoteVO[vos.length];
		//�����Ҫ�����ֶεĵ����������飬�½���Ŀ����Ϊͳһ�����µĲ���
		LeavehVO[] updateMainVOs = new LeavehVO[vos.length];
		//��Ҫ���µ��ֶ�
		String[] updateFields = {LeavehVO.APPROVER, LeavehVO.APPROVE_TIME, LeavehVO.APPROVE_NOTE,
				LeavehVO.APPROVE_STATE};
		for ( int i = 0 ; i < vos.length ; i++ ) {
			LeavehVO mainvo = ( LeavehVO ) vos[i].getParentVO();
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
	
	
	private LeavehVO changeBillData( LeavehVO mainvo,String[] updateFields , 
			String approveNote, Integer directApproveResult) throws BusinessException {
		if (mainvo == null) {
			return null;
		}
		mainvo.setAttributeValue(updateFields[0], PubEnv.getPk_user());	 //������pk_id
		mainvo.setAttributeValue(updateFields[1], PubEnv.getServerTime());	//����ʱ��
		mainvo.setAttributeValue(updateFields[2], approveNote);	//�������
		mainvo.setAttributeValue(updateFields[3], directApproveResult);	//����״̬
		return mainvo;
	}

	/**
	 * �����������
	 * @param directApproveResult	�� ����״̬����׼�ȣ�
	 * @param approveNote	:	�������
	 * @param mainvo	:	��������
	 * @return
	 * @throws BusinessException
	 */
	public WorkflownoteVO buildWorkflownoteVO( int directApproveResult , String approveNote , LeavehVO mainvo )
		throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid( mainvo.getPk_leaveh() );// ����ID
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


	@Override
	public void deleteArrayData(AggLeaveVO[] vos) throws BusinessException {
		getServiceTemplate().delete(vos);
		String pk_org = vos[0].getHeadVO().getPk_org();
		returnBillCodeOnDelete(pk_org, getBillType(), getBillCodeFormVOs(vos));
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])vos);
	}

	@Override
	public void deleteData(AggLeaveVO vo) throws BusinessException {
		getServiceTemplate().delete(vo);
		String pk_org = (String) vo.getParentVO().getAttributeValue(LeavehVO.PK_ORG);
		String bill_code = (String) vo.getParentVO().getAttributeValue(LeavehVO.BILL_CODE);
		returnBillCodeOnDelete(pk_org, bill_code);
		ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
		balanceService.queryAndCalLeaveBalanceVO(vo.getHeadVO().getPk_org(), vo.getHeadVO());
	}

	
	@Override
	public AggLeaveVO[] insertArrayData(AggLeaveVO[] vos)
			throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		String pk_org = ((LeavehVO)vos[0].getParentVO()).getPk_org();
		AggLeaveVO[] aggVOs = getServiceTemplate().insert(vos);
//		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])vos);
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).calLeaveBalanceVO4NewThread(pk_org, (Object[])vos);
		return aggVOs;
	}

	@Override
	@Deprecated
	public AggLeaveVO insertData(AggLeaveVO vo) throws BusinessException {
		new LeaveApplyQueryMaintainImpl().check(vo);
		//У��
        DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new PFSaveLeaveValidator());
		vService.validate(vo);
		// �ύ���ݱ���
		LeavehVO mainVO = (LeavehVO) vo.getParentVO();
		if(mainVO.getLeaveindex()==null)mainVO.setLeaveindex(1);
		AggLeaveVO aggVO =  getServiceTemplate().insert(vo)[0];
		commitBillCode(mainVO.getPk_org(), mainVO.getBill_code());
		ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
//		balanceService.queryAndCalLeaveBalanceVO(mainVO.getPk_org(), aggVO.getHeadVO());
		balanceService.calLeaveBalanceVO4NewThread(mainVO.getPk_org(), aggVO.getHeadVO());
		return aggVO;
	}
	
	/**
	 * У�����ȱ����ݼٵ���
	 * @param bills
	 * @throws BusinessException
	 */
//	@Deprecated
//	public void checkCrossPeriodBills(String pk_org, LeaveCommonVO[] bills) throws BusinessException {
//		if(ArrayUtils.isEmpty(bills))
//			return;
//		UFBoolean isCanCross = SysInitQuery.getParaBoolean(pk_org, LeaveConst.CROSSPERIOD_PARAM);
//		// ���������ڼ䱣��
//		if(isCanCross.booleanValue())
//			return;
//		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
//		for(LeaveCommonVO bill:bills) {
//			if(bill.getBegindate()==null || bill.getEnddate()==null)
//				continue;
//			PeriodVO[] periods = periodService.queryPeriodsByDateScope(bill.getPk_org(), bill.getBegindate(), bill.getEnddate());
//			if(ArrayUtils.isEmpty(periods))
//				continue;
//			String year = null;
//			for(PeriodVO period:periods){
//				// ��ǰΪ��һ��ѭ�������ڼ����ʼ��year
//				if(year==null){
//					year = period.getTimeyear();
//					continue;
//				}
//				// �����ǰ�ڼ���yearֵ��ͬ���ʾ�����
//				if(!year.equals(period.getTimeyear()))
//					throw new BusinessException("������ݼٵ��ݲ������棡");
//			}
//		}
//	}
	
	private void setValidatorFactory() {
		getServiceTemplate().setValidatorFactory(new LeaveApplyValidatorFactory());
	}

	@Override
	public AggLeaveVO[] updateArrayData(AggLeaveVO[] vos) throws BusinessException {
		return updateData(vos,true,true);
	}
	
	private AggLeaveVO[] updateData(AggLeaveVO[] vos,boolean isSetAuditInfo,boolean needCheck) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		if(needCheck){
			LeaveApplyApproveValidator validator = new  LeaveApplyApproveValidator();
			validator.validateAggVOs(vos);
		}
		return getServiceTemplate().update(isSetAuditInfo, vos);
	}
	
	@Deprecated
	@Override
	public AggLeaveVO updateData(AggLeaveVO vo) throws BusinessException {
		return updateArrayData(new AggLeaveVO[]{vo})[0];
	}
	@Deprecated
	protected AggLeaveVO updateData(AggLeaveVO vo,boolean isSetAuditInfo) throws BusinessException {
		new LeaveApplyQueryMaintainImpl().check(vo);
//		checkCrossPeriodBills(vo.getLeavehVO().getPk_org(), vo.getLeavebVOs());
		return getServiceTemplate().update(isSetAuditInfo, vo)[0];
	}
	
	/**
	 * �������ջأ������������ص�
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doRecall(AggLeaveVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
//		setModifyStatusToHead(vos);
		syncHeadInfoToBody(vos);
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		return updateData(vos, false,false);
	}
	
	
	@Override
	public PfProcessBatchRetObject doCallBack(boolean blCheckPassIsEnd,AggregatedValueObject[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
        PfProcessBatchRetObject retObj = getIHrPf().callbackBill(blCheckPassIsEnd,null,null,LeaveConst.RES_CODE_LEAVEREG, vos);
        
		AggLeaveVO[] aggVOs = new AggLeaveVO[vos.length];
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		PeriodServiceFacade.checkDateScope(aggVOs[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(aggVOs));
		for(int i=0;i<aggVOs.length;i++) {
			aggVOs[i] = (AggLeaveVO)vos[i];
		}
		String pk_org = aggVOs[0].getHeadVO().getPk_org();
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])aggVOs);
        return retObj;
	}

	/**
	 * �������ύ�������������ص�
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggregatedValueObject[] doCommit(AggLeaveVO[] vos) throws BusinessException {
		for ( AggLeaveVO vo : vos ) {
			
			//У�����
//			new BillValidatorAtServer().checkLeavePara(vo);
			
			LeavehVO billvo = ( LeavehVO ) vo.getParentVO();
			// ���µ���״̬
			billvo.setApprove_state( IPfRetCheckInfo.COMMIT );
			vo.setParentVO( billvo );
//			getServiceTemplate().updateBill( vo ,false);
			
//			ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
//			balanceService.processAfterCommit(billvo.getPk_org(), vo);V61��Ϊ����ʱ�͸��¶���ʱ���������ύʱ��
		}
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		updateData(vos, false, false);
		LeavehVO leavehVO =  (LeavehVO)vos[0].getParentVO();
		String pk_org = leavehVO.getPk_org();
		Integer approvetype = SysInitQuery.getParaInt(pk_org, LeaveConst.APPROVE_PARAM);;
		
		if (approvetype != null && approvetype == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT) {
			// ������ʽΪֱ��ʱ����֪ͨ
//			sendMessage( LeaveConst.NOTICE_SORT_APPROVE , PubEnv.getPk_group() , ( ( LeavehVO ) vos[0].getParentVO() ).getPk_org() , vos );
			sendDirApprMessage(pk_org, TaMessageConst.LEAVEMSGCODE, vos);
		}
		
		LeavePlanChangeTime.ChangeTime(vos[0].getHeadVO().getLength(), vos[0].getHeadVO().getLeaveplan());
		
		
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
//	private void sendMessage(String pk_sort, String pkGroup, String pk_org, AggLeaveVO[] vos)
//	throws BusinessException {
//
//		INotice setvice = NCLocator.getInstance().lookup(INotice.class);
//		NoticeTempletVO[] nt = setvice.queryDistributedTemplates(pk_sort, pkGroup, pk_org, true);
//		if (nt != null && nt.length > 0) {
//			nt[0].setTransferValues(createUserValue(ArrayClassConvertUtil.convert(vos, AggLeaveVO.class)));
//			nt[0].setContent(TaSendMsgHelper.getNewContent(nt[0].getContent(), vos.length, LeaveConst.FIELDCODE));
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
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doApprove(AggLeaveVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		syncHeadInfoToBody(vos);
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		//2012.8.6���������ۺ�ȷ���������ѷ���ڼ�����뵥�����������غ���˲�ͨ�������仰˵
		//���ڲ��غ���˲�ͨ�������뵥������ҪУ���ڼ���
		LeavehVO tempvo = (LeavehVO) vos[0].getParentVO();
		int appStatus = tempvo.getApprove_state();
		if(appStatus!=IBillStatus.FREE&&appStatus!=IBillStatus.NOPASS)
			PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		vos = updateData(vos, false,false);
		if (appStatus == IBillStatus.CHECKPASS){
			exexBills(tempvo.getPk_org(), vos);
		}else if (tempvo.getApprove_state() == IBillStatus.FREE||tempvo.getApprove_state() == IBillStatus.NOPASS){
			//����̬����½�����Ϣ
			String pk_org = vos[0].getHeadVO().getPk_org();
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])vos);
		if(tempvo.getApprove_state() == IBillStatus.NOPASS ){
			LeavePlanChangeTime.ChangeTime(tempvo.getLength().multiply(-1), tempvo.getLeaveplan());
		}
		}
		
		return vos;
	}
	/**
	 * ���������ֱ������ͬ�����ݼٵǼǱ��¼
	 * @param tempvo
	 * @param vos
	 * @throws BusinessException
	 */
	public void exexBills(String pk_org,AggLeaveVO[] vos) throws BusinessException{
		genRegAndCalculate(vos);
	}

	
	/**
	 * ���������������������ص�
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doUnApprove(AggLeaveVO[] vos) throws BusinessException {
		//У�鵥��״̬
		LeavehVO[] mainvos = (LeavehVO[]) AggVOHelper.getParentVOArrayFromAggVOs(vos,LeavehVO.class);
		AggLeaveVO[] oldvos = getServiceTemplate().queryByPks(AggLeaveVO.class, StringPiecer.getStrArray(mainvos, LeavehVO.PK_LEAVEH));
		for(AggLeaveVO oldvo:oldvos){
			checkPFPassingState(oldvo.getLeavehVO().getApprove_state());
		}
		
		syncHeadInfoToBody(vos);
		//���������ߴ󲿷ֵ�У�飬���updateData�ĵڶ�������Ϊfalse���������ᵼ���ڼ���У��Ҳ
		//�߲��������ǲ��Եģ�����������������ڼ���У��
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));	
		vos = updateData(vos, false,false);
		String pk_org = vos[0].getLeavehVO().getPk_org();
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])vos);
		return vos;
	}

	/**
	 * ������ɾ���������������ص�
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doDelete(AggLeaveVO[] vos) throws BusinessException {
		for (AggLeaveVO vo : vos) {
			// ���յ��ݺ�
			deleteOldWorknote(vo.getParentVO().getPrimaryKey(), LeaveConst.BillTYPE_LEAVE);
		}
		deleteArrayData(vos);
		return vos;
	}
 
//	@Override
//	public String getBillIdSql(int iBillStatus, String billType) throws BusinessException {
//		String pks = getIHrPf().getBillIdSql(iBillStatus, billType);
//		String strWorkFlowWhere = LeavehVO.PK_LEAVEH + " in (" + (StringUtils.isBlank(pks) ? " '1<>1' " : pks)
//				+ ") ";
//		return strWorkFlowWhere;
//	}
 
	@Override
	protected LeavehVO createMainVO(LeavebVO[] childVOs) {
		LeavehVO mainvo = new LeavehVO();
		LeavebVO childVO=childVOs[0];
		mainvo.setIshrssbill(UFBoolean.FALSE);
		mainvo.setIslactation(UFBoolean.FALSE);
		mainvo.setPk_group(childVO.getPk_group());
		mainvo.setPk_org(childVO.getPk_org());
		mainvo.setPk_psndoc(childVO.getPk_psndoc());
		mainvo.setPk_psnjob(childVO.getPk_psnjob());
		mainvo.setPk_psnorg(childVO.getPk_psnorg());
		mainvo.setPk_org_v(childVO.getPk_org_v());
		mainvo.setPk_dept_v(childVO.getPk_dept_v());
		mainvo.setStatus(VOStatus.NEW);
		mainvo.setApprove_state( IPfRetCheckInfo.NOSTATE );
		mainvo.setBillmaker( PubEnv.getPk_user() );
		mainvo.setApply_date( PubEnv.getServerLiteralDate() );
		mainvo.setLeaveyear(childVO.getLeaveyear());
		mainvo.setLeavemonth(childVO.getLeavemonth());
		if(childVO.getAttributeValue(LeavehVO.TRANSTYPEID)!=null)
		{
			mainvo.setTranstypeid((String)childVO.getAttributeValue(LeavehVO.TRANSTYPEID));			
		}
		if(childVO.getAttributeValue(LeavehVO.TRANSTYPE)!=null)
		{
			mainvo.setTranstype((String)childVO.getAttributeValue(LeavehVO.TRANSTYPE));			
		}
		
		mainvo.setPk_billtype(childVO.getAttributeValue(LeavehVO.PK_BILLTYPE) == null?LeaveConst.BillTYPE_LEAVE:(String)childVO.getAttributeValue(LeavehVO.PK_BILLTYPE));
		mainvo.setPk_leavetype((String)childVO.getAttributeValue(LeavehVO.PK_LEAVETYPE));
		mainvo.setPk_leavetypecopy((String)childVO.getAttributeValue(LeavehVO.PK_LEAVETYPECOPY));
		if(childVOs.length==1)
			mainvo.setSumhour(childVO.getLeavehour());
		else{
			UFDouble sumHour = new UFDouble();
			for(LeavebVO vo:childVOs){
				if(vo.getLeavehour()!=null)
					sumHour = sumHour.add(vo.getLeavehour());
			}
			mainvo.setSumhour(sumHour);
		}
		mainvo.setRealdayorhour(childVO.getRealdayorhour());
		mainvo.setRestdayorhour(childVO.getRestdayorhour());
		mainvo.setResteddayorhour(childVO.getResteddayorhour());
		mainvo.setFreezedayorhour(childVO.getFreezedayorhour());
		mainvo.setUsefuldayorhour(childVO.getUsefuldayorhour());
		
		return mainvo;
	}
	
	private LeavebVO createSubVo(LeavebVO pageVo, String pk_org,String pk_group) {
		LeavebVO subvo = new LeavebVO();
		subvo.setAttributeValue(LeavehVO.PK_LEAVETYPE, pageVo.getAttributeValue(LeavehVO.PK_LEAVETYPE));
		subvo.setAttributeValue(LeavehVO.PK_LEAVETYPECOPY, pageVo.getAttributeValue(LeavehVO.PK_LEAVETYPECOPY));
		subvo.setLeavebegintime(pageVo.getLeavebegintime());
		subvo.setLeaveendtime(pageVo.getLeaveendtime());
		subvo.setLeavebegindate(UFLiteralDate.getDate(pageVo.getLeavebegintime().toString().substring(0, 10)));
		subvo.setLeaveenddate(UFLiteralDate.getDate(pageVo.getLeaveendtime().toString().substring(0, 10)));
		subvo.setLeaveyear(pageVo.getLeaveyear());
		subvo.setLeavemonth(pageVo.getLeavemonth());
		subvo.setLeavehour(pageVo.getLeavehour());
		subvo.setLeaveremark(pageVo.getLeaveremark());
		subvo.setPk_agentpsn(pageVo.getPk_agentpsn());
		subvo.setTranstypeid(pageVo.getTranstypeid());
		subvo.setTranstype(pageVo.getTranstype());
//		subvo.setPk_billtype(pageVo.getPk_billtype() == null?AwayConst.BillTYPE:pageVo.getPk_billtype());
		subvo.setPk_group(pk_group);
		subvo.setPk_org(pk_org);
		subvo.setStatus(VOStatus.NEW);
		subvo.setPk_psndoc(pageVo.getPk_psndoc());
		subvo.setPk_psnjob(pageVo.getPk_psnjob());
		subvo.setPk_psnorg(pageVo.getPk_psnorg());
		subvo.setPk_joborg(pageVo.getPk_joborg());
		subvo.setPk_org_v(pageVo.getPk_org_v());
		subvo.setPk_dept_v(pageVo.getPk_dept_v());
		subvo.setRealdayorhour(pageVo.getRealdayorhour());
		subvo.setRestdayorhour(pageVo.getRestdayorhour());
		subvo.setResteddayorhour(pageVo.getResteddayorhour());
		subvo.setFreezedayorhour(pageVo.getFreezedayorhour());
		subvo.setUsefuldayorhour(pageVo.getUsefuldayorhour());
		subvo.setTimezone(pageVo.getTimezone());
		return subvo;
	}
	
	/**
	 * �ϲ�����
	 * @param vos
	 * @param mergeBill
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] createAggLeaveVO(LeavebVO[] vos,boolean mergeBill) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		String pk_org = vos[0].getPk_org();
		String pk_group = vos[0].getPk_group();
		LeavebVO[] subvos = new LeavebVO[vos.length];
		for(int i =0; i < subvos.length; i++) {
			subvos[i] = createSubVo(vos[i], pk_org, pk_group);
		}
		AggLeaveVO[] retArray = null;
		//������ϲ����ݣ���ôһ��LeavebVO��Ӧһ��AggVO
		if(!mergeBill) {
			List<AggLeaveVO> aggvos = new ArrayList<AggLeaveVO>();
			for(LeavebVO subvo : subvos)
			{
				AggLeaveVO aggVO = new AggLeaveVO();
				aggVO.setParentVO(createMainVO(new LeavebVO[]{subvo}));
				aggVO.setChildrenVO(new LeavebVO[]{subvo});
				aggvos.add(aggVO);				
			}
			retArray = aggvos.toArray( new AggLeaveVO[0]);
		}
		else{
			AggLeaveVO[] tempAggVOs = mergeSubVOs(AggLeaveVO.class, subvos);
			//�����tempAggVOs�ǰ�����Ա/���/��������/ҵ��Ԫ����õ��Ľ��������Ҫ���䰴�ڼ����
			List<AggLeaveVO> resultList = new ArrayList<AggLeaveVO>();
			for(AggLeaveVO aggVO:tempAggVOs){
				LeavebVO[] bvos = aggVO.getBodyVOs();
				if(bvos.length==1){
					resultList.add(aggVO);
					continue;
				}
				Map<String, LeavebVO[]> periodGroupMap = CommonUtils.group2ArrayByField("yearmonth", bvos);
				if(periodGroupMap.size()==1){
					resultList.add(aggVO);
					continue;
				}
				for(LeavebVO[] tempbVOs:periodGroupMap.values()){
					AggLeaveVO a = new AggLeaveVO();
					resultList.add(a);
					a.setParentVO(createMainVO(tempbVOs));
					a.setChildrenVO(tempbVOs);
				}
			}
			retArray = resultList.toArray(new AggLeaveVO[0]);
		}
		Map<String, LeaveBalanceVO> leaveBalanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])retArray);
		for(int i = 0; i < retArray.length; i++) {
			LeavehVO mainvo = (LeavehVO)retArray[i].getParentVO();
			LeaveBalanceVO leaveBalanceVO = MapUtils.isEmpty(leaveBalanceMap)?null:leaveBalanceMap.get(mainvo.getPk_psnorg()+mainvo.getPk_leavetype()+mainvo.getYearmonth());
			mainvo.setLeaveindex(leaveBalanceVO==null?1:leaveBalanceVO.getLeaveindex());
		}
		return retArray;
	}
	
	private ILeaveAppInfoDisplayer autoComputeService;
	public ILeaveAppInfoDisplayer getAutoComputeService() {
		if(this.autoComputeService == null){
			this.autoComputeService = new LeaveAppInfoDisplayer();
		}
		return this.autoComputeService;
	}
	
	@Override
	public AggLeaveVO[] insertData(LeavebVO[] vos, boolean mergeBill)
			throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		String pk_org= vos[0].getPk_org();
//		checkCrossPeriodBills(pk_org, vos);
//		new LeaveApplyQueryMaintainImpl().check(pk_org, vos);//����ǰ����ʾ��У�����Ѿ�У����ˣ�Ϊ��Ч�ʴ˴�����У��
		AggLeaveVO[] aggVOs = createAggLeaveVO(vos, mergeBill);
		String[] billCodes = null;
		try{
			//�Զ����ɱ���
			billCodes = generateBillCodes(pk_org, aggVOs.length);
			for(int i = 0; i < aggVOs.length; i++) {
				LeavehVO mainvo = (LeavehVO)aggVOs[i].getParentVO();
				mainvo.setBill_code(billCodes[i]);
			}
			//���㿪ʼ�����������Լ�ʱ��
//			for(int i=0;i<aggVOs.length;i++){
//				aggVOs[i] = getAutoComputeService().calculate(aggVOs[i]);
//			}
			AggLeaveVO[] retVOs =  insertArrayData(aggVOs);
			commitBillCodes( pk_org,  billCodes);//�ɹ��������ύ���ݺ�
			return retVOs;
		}
		catch(Exception e){
			if(!ArrayUtils.isEmpty(billCodes)){
				rollbackBillCodes(pk_org, billCodes);//����ʧ������˵��ݺ�
			}
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(), e);
		}
	}

	@Override
	public AggLeaveVO[] insertData(SplitBillResult<AggLeaveVO> splitResult)
	throws BusinessException {
//		new LeaveApplyQueryMaintainImpl().checkWhenSave(splitResult.getOriginalBill());//�ڱ���ǰ�Ѿ�У����ˣ�Ϊ��Ч�ʴ˴�����У��
		//У��û�����⣬���������ݿ��в����ˡ��˴�Ҫ����������У����ݱ��룬splitid
		AggLeaveVO[] results = splitResult.getSplitResult();
		//����Ҫ�������ݺ�(���ǵ��п������Լ����ɵ��ݺţ���˱���һ���������еģ�����ѭ������)
		int billCodeCount = 0;
		for(AggLeaveVO aggVO:results){
			//�ݴ�������ǲ���٣�����Ⱥ��ڼ�setΪnull
			LeavehVO headVO = aggVO.getHeadVO();
			if(headVO.getIslactation()!=null&&headVO.getIslactation().booleanValue()){
				headVO.setLeaveyear(null);
				headVO.setLeavemonth(null);
			}
			if(org.apache.commons.lang.StringUtils.isEmpty(headVO.getBill_code()))
				billCodeCount++;
		}
		String pk_org = splitResult.getOriginalBill().getHeadVO().getPk_org();
		String[] billCodes = null;
		try{
			billCodes = generateBillCodes(pk_org, billCodeCount);
			Iterator<String> codeIterator = ArrayUtils.isEmpty(billCodes)?null:Arrays.asList(billCodes).iterator();
			String splitid = new SequenceGenerator().generate();
			for(AggLeaveVO aggVO:results){
				LeavehVO hVO = aggVO.getHeadVO();
				hVO.setSplitid(splitid);
				if(StringUtils.isEmpty(hVO.getBill_code()))//�������еĶ���Ҫ���ɵ��ݺ�
					hVO.setBill_code(codeIterator.next());
			}
			AggLeaveVO[] retVOs =  getServiceTemplate().insert(results);
//			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])retVOs);
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).calLeaveBalanceVO4NewThread(pk_org, (Object[])retVOs);
			billCodes = new String[results.length];//֮ǰ��billCodesֻ�����˴������ɵ�code���������ڿͻ��˾����ɺ��˵�code�������Ҫ��������һ�����ύ
			for(int i=0;i<results.length;i++){ 
				billCodes[i]=results[i].getHeadVO().getBill_code();
			}
			commitBillCodes(pk_org, billCodes);//����ɹ����ύ���ݺ�
			return retVOs;
		}
		catch(Exception e){
//			List<String> billCodeList = new ArrayList<String>();
//			for(AggLeaveVO aggVO:results){
//				if(!StringUtils.isEmpty(aggVO.getHeadVO().getBill_code()))
//					billCodeList.add(aggVO.getHeadVO().getBill_code());
//			}
//			if(billCodeList.size()>0){
//				rollbackBillCodes(pk_org, billCodeList.toArray(new String[0]));//����ʧ������˵��ݺ�
//			}
			rollbackBillCodes(pk_org, billCodes);
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(), e);
		}
	}

	@Override
	public AggLeaveVO[] updateData(SplitBillResult<AggLeaveVO> splitResult) throws BusinessException {
//		new LeaveApplyQueryMaintainImpl().checkWhenSave(splitResult.getOriginalBill());//�ڱ���ǰ�Ѿ�У����ˣ�Ϊ��Ч�ʴ˴�����У��
		LeavebVO[] bodyVO=splitResult.getOriginalBill().getBodyVOs();
		//�ڲ��Լ�У��ʱ����Ϊֻ�ֲ�δ��ɾ���ģ����յõ���splitResult �������ѱ�ɾ�����ֱ��������ɾ���ֱ�����ʱ��ɾ�����ˣ����Դ�ʱ�Ƚ���ɾ���ֱ�ȡ����
		List<LeavebVO> bodyvoList = new ArrayList<LeavebVO>();//ɾ��������
		for(LeavebVO bodyvo : bodyVO){
			if(bodyvo.getStatus()==VOStatus.DELETED){
				bodyvoList.add(bodyvo);
			}
		}
		AggLeaveVO[] results = splitResult.getSplitResult();
		BillMethods.processBeginEndDatePkJobOrgTimeZone(results);
		for(int i=0;i<results.length;i++){
			//ɾ��������
			LeavebVO[] deleteVOS =	(LeavebVO[]) bodyvoList.toArray(new LeavebVO[0]);
			//ҳ���Ͽ��������� 
			LeavebVO[] leaveVOS =	results[i].getBodyVOs();
			results[i].setChildrenVO((CircularlyAccessibleValueObject[])ArrayUtils.addAll(deleteVOS, leaveVOS));
		}
		//����Ҫ�������ݺ�(���ǵ��п������Լ����ɵ��ݺţ���˱���һ���������еģ�����ѭ������)
		int billCodeCount = 0;
		for(AggLeaveVO aggVO:results){
			//�ݴ�������ǲ���٣�����Ⱥ��ڼ�setΪnull
			LeavehVO headVO = aggVO.getHeadVO();
			if(headVO.getIslactation()!=null&&headVO.getIslactation().booleanValue()){
				headVO.setLeaveyear(null);
				headVO.setLeavemonth(null);
			}
			if(StringUtils.isEmpty(headVO.getBill_code()))
				billCodeCount++;
		}
		LeavehVO orihVO = (LeavehVO) new BaseDAO().retrieveByPK(LeavehVO.class, splitResult.getOriginalBill().getHeadVO().getPrimaryKey());
		String splitid = orihVO.getSplitid();
		String pk_org = orihVO.getPk_org();
		String[] billCodes = null;
		try{
			billCodes = generateBillCodes(pk_org, billCodeCount);
			Iterator<String> codeIterator = ArrayUtils.isEmpty(billCodes)?null:Arrays.asList(billCodes).iterator();
			List<AggLeaveVO> resultList = new ArrayList<AggLeaveVO>();
			List<AggLeaveVO> insertList = new ArrayList<AggLeaveVO>();
			List<AggLeaveVO> updateList = new ArrayList<AggLeaveVO>();
			for(AggLeaveVO aggVO:results){
				LeavehVO hVO = aggVO.getHeadVO();
				hVO.setSplitid(splitid);
				if(StringUtils.isEmpty(hVO.getBill_code()))//�������еĶ���Ҫ���ɵ��ݺ�
					hVO.setBill_code(codeIterator.next());
				if(VOStatus.UPDATED==hVO.getStatus())
					updateList.add(aggVO);
				else if(VOStatus.NEW==hVO.getStatus())
					insertList.add(aggVO);
				else if(VOStatus.UNCHANGED == hVO.getStatus()){//�����𵥵��ã������ύ�ջغ��޸��ӱ��治��
					LeavebVO[] bVOs = aggVO.getBodyVOs();
					for(LeavebVO bVo:bVOs){
						if(VOStatus.UNCHANGED == bVo.getStatus()){
							resultList.add(aggVO);
						}else{
							updateList.add(aggVO);
							break;
						}
					}
				}else
					resultList.add(aggVO);
			}
			if(CollectionUtils.isNotEmpty(updateList))
				CommonMethods.mergeArrayToCol(resultList, getServiceTemplate().update(true, updateList.toArray(new AggLeaveVO[0])));
			if(CollectionUtils.isNotEmpty(insertList))
				CommonMethods.mergeArrayToCol(resultList, getServiceTemplate().insert(insertList.toArray(new AggLeaveVO[0])));
			results = resultList.toArray(new AggLeaveVO[0]);
//			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])results);
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).calLeaveBalanceVO4NewThread(pk_org, (Object[])results);
			commitBillCodes(pk_org, billCodes);//����ɹ����ύ���ݺ�
			return results;
		}catch(Exception e){
			rollbackBillCodes(pk_org, billCodes);//����ʧ������˵��ݺ�
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(), e);
		}
	}
	
	private void genRegAndCalculate(AggLeaveVO[] aggVOs) throws BusinessException{
		ILeaveRegisterManageMaintain regService = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class);
		List<LeaveRegVO> insertList = new ArrayList<LeaveRegVO>();
		for(AggLeaveVO aggVO:aggVOs){
			LeavehVO leavehVO = aggVO.getLeavehVO();
			for(LeavebVO leavebVO:aggVO.getBodyVOs())
			{
				LeaveRegVO regVO = new LeaveRegVO();
				regVO.setPk_group(leavehVO.getPk_group());
				regVO.setPk_org(leavehVO.getPk_org());
				regVO.setBillsource(ICommonConst.BILL_SOURCE_APPLY);
				regVO.setPk_billsourceh(leavehVO.getPk_leaveh());
				regVO.setBill_code(leavehVO.getBill_code());
				//����ٵ������ϢҲҪ����
				regVO.setIslactation(leavehVO.getIslactation());
				regVO.setLactationholidaytype(leavebVO.getLactationholidaytype());
				regVO.setLactationhour(leavehVO.getLactationhour());
				
				regVO.setIsleaveoff(UFBoolean.FALSE);
				regVO.setPk_leavetype(leavehVO.getPk_leavetype());
				regVO.setPk_leavetypecopy(leavehVO.getPk_leavetypecopy());
				regVO.setPk_psnjob(leavehVO.getPk_psnjob());
				regVO.setPk_psnorg(leavehVO.getPk_psnorg());
				regVO.setPk_psndoc(leavehVO.getPk_psndoc());
				regVO.setPk_timeitem(leavehVO.getPk_timeitem());
				regVO.setLeaveyear(leavehVO.getLeaveyear());
				regVO.setLeavemonth(leavehVO.getLeavemonth());
				regVO.setLeaveindex(leavehVO.getLeaveindex());

				regVO.setPk_billsourceb(leavebVO.getPk_leaveb());
				regVO.setLeavebegindate(leavebVO.getLeavebegindate());
				regVO.setLeaveenddate(leavebVO.getLeaveenddate());
				regVO.setLeavebegintime(leavebVO.getLeavebegintime());
				regVO.setLeaveendtime(leavebVO.getLeaveendtime());
				
				regVO.setLeaveremark(leavebVO.getLeaveremark());
				regVO.setLeavehour(leavebVO.getLeavehour());
				
				regVO.setRealdayorhour(leavehVO.getRealdayorhour());
				regVO.setResteddayorhour(leavehVO.getResteddayorhour());
				regVO.setRestdayorhour(leavehVO.getRestdayorhour());
				regVO.setFreezedayorhour(leavehVO.getFreezedayorhour());
				regVO.setUsefuldayorhour(leavehVO.getUsefuldayorhour());
				
				regVO.setPk_org_v(leavehVO.getPk_org_v());
				regVO.setPk_dept_v(leavehVO.getPk_dept_v());

				insertList.add(regVO);
			}
		}
		regService.insertData(insertList.toArray(new LeaveRegVO[0]), false);
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(aggVOs[0].getLeavehVO().getPk_org(), (Object[])aggVOs);
	}

	@Override
	protected String getBillCodeFieldName() {
		return LeavehVO.BILL_CODE;
	}

	@Override
	protected String getBillType() {
		return LeaveConst.BillTYPE_LEAVE;
	}

	@Override
	protected Class<?> getHeadVOClass() {
		return LeavehVO.class;
	}

	@Override
	protected UserValueConfig getUserValueConfig() {
		UserValueConfig config = new UserValueConfig();
		config.setBillCodeFieldName(LeavehVO.BILL_CODE);
		config.setApproveStateFieldName(LeavehVO.APPROVE_STATE);
		config.setFieldCodes(LeaveConst.FIELDCODE);
		return config;
	}
	
	@Override
	protected void syncHeadInfoToBody(AggregatedValueObject[] aggVOs) {
		if(ArrayUtils.isEmpty(aggVOs))
			return;
		for(AggregatedValueObject aggVO:aggVOs){
			LeavebVO[] bvos = (LeavebVO[])aggVO.getChildrenVO();
			if(ArrayUtils.isEmpty(bvos))
				continue;
			LeavehVO hvo = (LeavehVO)aggVO.getParentVO();
			String pk_psndoc = hvo.getPk_psndoc();
			String pk_psnjob = hvo.getPk_psnjob();
			String pk_psnorg = hvo.getPk_psnorg();
			String pk_timeitem = hvo.getPk_timeitem();
			String pk_timeitemcopy = hvo.getPk_leavetypecopy();
			for(LeavebVO bvo:bvos){
				bvo.setPk_psndoc(pk_psndoc);
				bvo.setPk_psnjob(pk_psnjob);
				bvo.setPk_psnorg(pk_psnorg);
				bvo.setPk_timeitem(pk_timeitem);
				bvo.setPk_leavetypecopy(pk_timeitemcopy);
			}
		}
	}
}
