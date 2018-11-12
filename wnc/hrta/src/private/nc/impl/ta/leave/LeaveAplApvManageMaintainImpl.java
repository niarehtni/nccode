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
	 * 直批
	 */
	@Override
	public AggLeaveVO[] directApprove(int directApproveResult,
			String approveNote, AggLeaveVO[] vos)
			throws BusinessException {
		LeavehVO tempmainvo = (LeavehVO)vos[0].getParentVO();
		String pk_org = tempmainvo.getPk_org();
		//所在期间已经封存的单据，是可以驳回和审批不通过的
		if(directApproveResult!=IBillStatus.FREE&&directApproveResult!=IBillStatus.NOPASS)
			BillValidatorAtServer.checkPeriod(pk_org, BillProcessHelper.toLeavebVOs(vos));
		//审批意见
		WorkflownoteVO[] worknoteVOs = new WorkflownoteVO[vos.length];
		//存放需要更新字段的调班主表数组，新建的目的是为统一做更新的操作
		LeavehVO[] updateMainVOs = new LeavehVO[vos.length];
		//需要更新的字段
		String[] updateFields = {LeavehVO.APPROVER, LeavehVO.APPROVE_TIME, LeavehVO.APPROVE_NOTE,
				LeavehVO.APPROVE_STATE};
		for ( int i = 0 ; i < vos.length ; i++ ) {
			LeavehVO mainvo = ( LeavehVO ) vos[i].getParentVO();
			// 获取回写信息，即设置需要修改的字段
			updateMainVOs[i] = changeBillData(mainvo, updateFields,approveNote, directApproveResult);
			// 执行审批操作前，将审批信息写到pub_workflownote中
			worknoteVOs[i] = buildWorkflownoteVO( directApproveResult ,  approveNote ,mainvo);
		}
		//批量新增审批意见
		getIPersistenceUpdate().insertVOArray( null , worknoteVOs , null );
		//批量更新调班信息
		getIPersistenceUpdate().updateVOArray(null, updateMainVOs, updateFields, null);
		//如果批准，才可进行务操作
		if(directApproveResult == IPfRetCheckInfo.PASSING)
			exexBills(pk_org, vos);
		return vos;
	}
	
	
	private LeavehVO changeBillData( LeavehVO mainvo,String[] updateFields , 
			String approveNote, Integer directApproveResult) throws BusinessException {
		if (mainvo == null) {
			return null;
		}
		mainvo.setAttributeValue(updateFields[0], PubEnv.getPk_user());	 //审批人pk_id
		mainvo.setAttributeValue(updateFields[1], PubEnv.getServerTime());	//审批时间
		mainvo.setAttributeValue(updateFields[2], approveNote);	//审批意见
		mainvo.setAttributeValue(updateFields[3], directApproveResult);	//操作状态
		return mainvo;
	}

	/**
	 * 生成审批意见
	 * @param directApproveResult	： 操作状态（批准等）
	 * @param approveNote	:	审批意见
	 * @param mainvo	:	调班主表
	 * @return
	 * @throws BusinessException
	 */
	public WorkflownoteVO buildWorkflownoteVO( int directApproveResult , String approveNote , LeavehVO mainvo )
		throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid( mainvo.getPk_leaveh() );// 单据ID
		worknoteVO.setBillVersionPK(mainvo.getPrimaryKey());
		worknoteVO.setChecknote( approveNote );// 审批意见
		worknoteVO.setDealdate( PubEnv.getServerTime() );// 审批时间
		worknoteVO.setSenddate( PubEnv.getServerTime() );// 发送日期
		worknoteVO.setPk_org( mainvo.getPk_org() );// 组织
		worknoteVO.setBillno( mainvo.getBill_code() );// 单据号
		worknoteVO.setSenderman( mainvo.getApprover() == null ? mainvo.getBillmaker() : mainvo.getApprover() );// 发送人
		worknoteVO.setApproveresult(IPfRetCheckInfo.NOSTATE == directApproveResult ? "R" : IPfRetCheckInfo.PASSING == directApproveResult
				? "Y": "N");
		worknoteVO.setApprovestatus( 1 );// 直批的状态
		worknoteVO.setIscheck(IPfRetCheckInfo.PASSING == directApproveResult ? "Y" : IPfRetCheckInfo.NOPASS == directApproveResult
				? "N": "X");
		worknoteVO.setActiontype( "APPROVE" );
		worknoteVO.setCheckman( mainvo.getApprover() );	//审批人
		worknoteVO.setWorkflow_type( WorkflowTypeEnum.Approveflow.getIntValue() );
		worknoteVO.setPk_billtype( mainvo.getPk_billtype() );// 单据类型
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
		//校验
        DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new PFSaveLeaveValidator());
		vService.validate(vo);
		// 提交单据编码
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
	 * 校验跨年度保存休假单据
	 * @param bills
	 * @throws BusinessException
	 */
//	@Deprecated
//	public void checkCrossPeriodBills(String pk_org, LeaveCommonVO[] bills) throws BusinessException {
//		if(ArrayUtils.isEmpty(bills))
//			return;
//		UFBoolean isCanCross = SysInitQuery.getParaBoolean(pk_org, LeaveConst.CROSSPERIOD_PARAM);
//		// 如果允许跨期间保存
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
//				// 当前为第一个循环到的期间则初始化year
//				if(year==null){
//					year = period.getTimeyear();
//					continue;
//				}
//				// 如果当前期间与year值不同则表示跨年度
//				if(!year.equals(period.getTimeyear()))
//					throw new BusinessException("跨年度休假单据不允许保存！");
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
	 * 审批流收回，用于审批流回调
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doRecall(AggLeaveVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
//		setModifyStatusToHead(vos);
		syncHeadInfoToBody(vos);
		//审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		//走不到，这是不对的，因此这里主动进行期间封存校验
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		return updateData(vos, false,false);
	}
	
	
	@Override
	public PfProcessBatchRetObject doCallBack(boolean blCheckPassIsEnd,AggregatedValueObject[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
        PfProcessBatchRetObject retObj = getIHrPf().callbackBill(blCheckPassIsEnd,null,null,LeaveConst.RES_CODE_LEAVEREG, vos);
        
		AggLeaveVO[] aggVOs = new AggLeaveVO[vos.length];
		//审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		//走不到，这是不对的，因此这里主动进行期间封存校验
		PeriodServiceFacade.checkDateScope(aggVOs[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(aggVOs));
		for(int i=0;i<aggVOs.length;i++) {
			aggVOs[i] = (AggLeaveVO)vos[i];
		}
		String pk_org = aggVOs[0].getHeadVO().getPk_org();
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])aggVOs);
        return retObj;
	}

	/**
	 * 审批流提交，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggregatedValueObject[] doCommit(AggLeaveVO[] vos) throws BusinessException {
		for ( AggLeaveVO vo : vos ) {
			
			//校验参数
//			new BillValidatorAtServer().checkLeavePara(vo);
			
			LeavehVO billvo = ( LeavehVO ) vo.getParentVO();
			// 更新单据状态
			billvo.setApprove_state( IPfRetCheckInfo.COMMIT );
			vo.setParentVO( billvo );
//			getServiceTemplate().updateBill( vo ,false);
			
//			ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
//			balanceService.processAfterCommit(billvo.getPk_org(), vo);V61改为保存时就更新冻结时长，不在提交时做
		}
		//审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		//走不到，这是不对的，因此这里主动进行期间封存校验
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		updateData(vos, false, false);
		LeavehVO leavehVO =  (LeavehVO)vos[0].getParentVO();
		String pk_org = leavehVO.getPk_org();
		Integer approvetype = SysInitQuery.getParaInt(pk_org, LeaveConst.APPROVE_PARAM);;
		
		if (approvetype != null && approvetype == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT) {
			// 审批方式为直批时发送通知
//			sendMessage( LeaveConst.NOTICE_SORT_APPROVE , PubEnv.getPk_group() , ( ( LeavehVO ) vos[0].getParentVO() ).getPk_org() , vos );
			sendDirApprMessage(pk_org, TaMessageConst.LEAVEMSGCODE, vos);
		}
		
		LeavePlanChangeTime.ChangeTime(vos[0].getHeadVO().getLength(), vos[0].getHeadVO().getLeaveplan());
		
		
		return vos;
	}
	
	/**
	 * 发送通知消息
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
//				// 如果模板的当前用户为空，则附上当前用户，或NC系统用户
//				nt[0].setCurrentUserPk( PubEnv.getPk_user() != null && PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
//						: INCSystemUserConst.NC_USER_PK );
//			}
//			setvice.sendNotice_RequiresNew(nt[0], pk_org, false);
//		}
//	}

	/**
	 * 审批流审批，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doApprove(AggLeaveVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		syncHeadInfoToBody(vos);
		//审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		//走不到，这是不对的，因此这里主动进行期间封存校验
		//2012.8.6与需求讨论后确定，对于已封存期间的申请单，可以做驳回和审核不通过。换句话说
		//对于驳回和审核不通过的申请单，不需要校验期间封存
		LeavehVO tempvo = (LeavehVO) vos[0].getParentVO();
		int appStatus = tempvo.getApprove_state();
		if(appStatus!=IBillStatus.FREE&&appStatus!=IBillStatus.NOPASS)
			PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));
		vos = updateData(vos, false,false);
		if (appStatus == IBillStatus.CHECKPASS){
			exexBills(tempvo.getPk_org(), vos);
		}else if (tempvo.getApprove_state() == IBillStatus.FREE||tempvo.getApprove_state() == IBillStatus.NOPASS){
			//自由态则更新结余信息
			String pk_org = vos[0].getHeadVO().getPk_org();
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])vos);
		if(tempvo.getApprove_state() == IBillStatus.NOPASS ){
			LeavePlanChangeTime.ChangeTime(tempvo.getLength().multiply(-1), tempvo.getLeaveplan());
		}
		}
		
		return vos;
	}
	/**
	 * 审批完或者直批进行同步在休假登记表记录
	 * @param tempvo
	 * @param vos
	 * @throws BusinessException
	 */
	public void exexBills(String pk_org,AggLeaveVO[] vos) throws BusinessException{
		genRegAndCalculate(vos);
	}

	
	/**
	 * 审批流弃审，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doUnApprove(AggLeaveVO[] vos) throws BusinessException {
		//校验单据状态
		LeavehVO[] mainvos = (LeavehVO[]) AggVOHelper.getParentVOArrayFromAggVOs(vos,LeavehVO.class);
		AggLeaveVO[] oldvos = getServiceTemplate().queryByPks(AggLeaveVO.class, StringPiecer.getStrArray(mainvos, LeavehVO.PK_LEAVEH));
		for(AggLeaveVO oldvo:oldvos){
			checkPFPassingState(oldvo.getLeavehVO().getApprove_state());
		}
		
		syncHeadInfoToBody(vos);
		//审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		//走不到，这是不对的，因此这里主动进行期间封存校验
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(), BillProcessHelper.toLeavebVOs(vos));	
		vos = updateData(vos, false,false);
		String pk_org = vos[0].getLeavehVO().getPk_org();
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])vos);
		return vos;
	}

	/**
	 * 审批流删除，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveVO[] doDelete(AggLeaveVO[] vos) throws BusinessException {
		for (AggLeaveVO vo : vos) {
			// 回收单据号
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
	 * 合并单据
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
		//如果不合并单据，那么一个LeavebVO对应一个AggVO
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
			//上面的tempAggVOs是按照人员/类别/交易类型/业务单元分组得到的结果，还需要将其按期间分组
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
//		new LeaveApplyQueryMaintainImpl().check(pk_org, vos);//保存前的提示性校验中已经校验过了，为了效率此处不再校验
		AggLeaveVO[] aggVOs = createAggLeaveVO(vos, mergeBill);
		String[] billCodes = null;
		try{
			//自动生成编码
			billCodes = generateBillCodes(pk_org, aggVOs.length);
			for(int i = 0; i < aggVOs.length; i++) {
				LeavehVO mainvo = (LeavehVO)aggVOs[i].getParentVO();
				mainvo.setBill_code(billCodes[i]);
			}
			//计算开始、结束日期以及时长
//			for(int i=0;i<aggVOs.length;i++){
//				aggVOs[i] = getAutoComputeService().calculate(aggVOs[i]);
//			}
			AggLeaveVO[] retVOs =  insertArrayData(aggVOs);
			commitBillCodes( pk_org,  billCodes);//成功保存则提交单据号
			return retVOs;
		}
		catch(Exception e){
			if(!ArrayUtils.isEmpty(billCodes)){
				rollbackBillCodes(pk_org, billCodes);//保存失败则回退单据号
			}
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(), e);
		}
	}

	@Override
	public AggLeaveVO[] insertData(SplitBillResult<AggLeaveVO> splitResult)
	throws BusinessException {
//		new LeaveApplyQueryMaintainImpl().checkWhenSave(splitResult.getOriginalBill());//在保存前已经校验过了，为了效率此处不再校验
		//校验没有问题，可以往数据库中插入了。此处要处理的事情有：单据编码，splitid
		AggLeaveVO[] results = splitResult.getSplitResult();
		//看需要几个单据号(考虑到有可能是自己生成单据号，因此必须一次生成所有的，不能循环生成)
		int billCodeCount = 0;
		for(AggLeaveVO aggVO:results){
			//容错处理，如果是哺乳假，则年度和期间set为null
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
				if(StringUtils.isEmpty(hVO.getBill_code()))//不是所有的都需要生成单据号
					hVO.setBill_code(codeIterator.next());
			}
			AggLeaveVO[] retVOs =  getServiceTemplate().insert(results);
//			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(pk_org, (Object[])retVOs);
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).calLeaveBalanceVO4NewThread(pk_org, (Object[])retVOs);
			billCodes = new String[results.length];//之前的billCodes只包含此次新生成的code，不包含在客户端就生成好了的code，因此需要重新整理一次再提交
			for(int i=0;i<results.length;i++){ 
				billCodes[i]=results[i].getHeadVO().getBill_code();
			}
			commitBillCodes(pk_org, billCodes);//保存成功则提交单据号
			return retVOs;
		}
		catch(Exception e){
//			List<String> billCodeList = new ArrayList<String>();
//			for(AggLeaveVO aggVO:results){
//				if(!StringUtils.isEmpty(aggVO.getHeadVO().getBill_code()))
//					billCodeList.add(aggVO.getHeadVO().getBill_code());
//			}
//			if(billCodeList.size()>0){
//				rollbackBillCodes(pk_org, billCodeList.toArray(new String[0]));//保存失败则回退单据号
//			}
			rollbackBillCodes(pk_org, billCodes);
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(), e);
		}
	}

	@Override
	public AggLeaveVO[] updateData(SplitBillResult<AggLeaveVO> splitResult) throws BusinessException {
//		new LeaveApplyQueryMaintainImpl().checkWhenSave(splitResult.getOriginalBill());//在保存前已经校验过了，为了效率此处不再校验
		LeavebVO[] bodyVO=splitResult.getOriginalBill().getBodyVOs();
		//在拆单以及校验时候因为只分拆未被删除的，最终得到的splitResult 不包含已被删除的字表这样造成删除字表数据时候删除不了，所以此时先将被删的字表取出来
		List<LeavebVO> bodyvoList = new ArrayList<LeavebVO>();//删除的数据
		for(LeavebVO bodyvo : bodyVO){
			if(bodyvo.getStatus()==VOStatus.DELETED){
				bodyvoList.add(bodyvo);
			}
		}
		AggLeaveVO[] results = splitResult.getSplitResult();
		BillMethods.processBeginEndDatePkJobOrgTimeZone(results);
		for(int i=0;i<results.length;i++){
			//删除的数据
			LeavebVO[] deleteVOS =	(LeavebVO[]) bodyvoList.toArray(new LeavebVO[0]);
			//页面上看到的数据 
			LeavebVO[] leaveVOS =	results[i].getBodyVOs();
			results[i].setChildrenVO((CircularlyAccessibleValueObject[])ArrayUtils.addAll(deleteVOS, leaveVOS));
		}
		//看需要几个单据号(考虑到有可能是自己生成单据号，因此必须一次生成所有的，不能循环生成)
		int billCodeCount = 0;
		for(AggLeaveVO aggVO:results){
			//容错处理，如果是哺乳假，则年度和期间set为null
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
				if(StringUtils.isEmpty(hVO.getBill_code()))//不是所有的都需要生成单据号
					hVO.setBill_code(codeIterator.next());
				if(VOStatus.UPDATED==hVO.getStatus())
					updateList.add(aggVO);
				else if(VOStatus.NEW==hVO.getStatus())
					insertList.add(aggVO);
				else if(VOStatus.UNCHANGED == hVO.getStatus()){//自助拆单调用，单据提交收回后修改子表保存不上
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
			commitBillCodes(pk_org, billCodes);//保存成功则提交单据号
			return results;
		}catch(Exception e){
			rollbackBillCodes(pk_org, billCodes);//保存失败则回退单据号
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
				//哺乳假的相关信息也要设置
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
				
				//add by chenklb@yonyou.com 2018.5.7  休假审批时设置生效时间begin
				UFLiteralDate nowDate=new UFLiteralDate();
				regVO.setEffectivedate(nowDate.before(leavebVO.getLeaveenddate())?leavebVO.getLeaveenddate():nowDate);
				//add by chenklb@yonyou.com 2018.5.7  休假审批时设置生效时间end
				
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
