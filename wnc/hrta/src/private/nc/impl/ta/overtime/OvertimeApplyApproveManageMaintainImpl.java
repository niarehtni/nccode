﻿package nc.impl.ta.overtime;

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
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hr.tools.pub.GeneralVO;
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
 * 加班申请审批 后台管理类
 * 
 * @author yucheng
 * 
 */
public class OvertimeApplyApproveManageMaintainImpl extends
		TaWorkFlowManager<OvertimehVO, OvertimebVO> implements
		IOvertimeApplyApproveManageMaintain {

	private HrBatchService serviceTemplate;

	private HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(
					"a7e298c9-3211-489d-8698-7ae65a922156");
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
		OvertimehVO mainVO = (OvertimehVO) vo.getParentVO();
		returnBillCodeOnDelete(mainVO.getPk_org(), mainVO.getBill_code());
	}

	@Override
	public AggOvertimeVO[] insertArrayData(AggOvertimeVO[] vos)
			throws BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AggOvertimeVO insertData(AggOvertimeVO vo) throws BusinessException {
		OvertimehVO mainVO = (OvertimehVO) vo.getParentVO();
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vo.getBodyVOs());

		// 校验
		DefaultValidationService vService = new DefaultValidationService();
		vService.addValidator(new PFSaveOvertimeValidator());
		vService.validate(vo);
		setValidatorFactory();
		new OvertimeApplyQueryMaintainImpl().check(vo);

		// 校验是否跨业务单元-不放在BillValidatorAtServer中是因为点savebillaction时候就会走check方法这时候会造成校验顺序不对
		List<OvertimebVO> bVOList = BillProcessHelper
				.toOvertimebVOList(new AggOvertimeVO[] { vo });
		OvertimebVO[] bvos = bVOList.toArray(new OvertimebVO[0]);
		BillValidatorAtServer.checkCrossBU(vo.getOvertimehVO().getPk_org(),
				bvos);

		AggOvertimeVO aggVO = getServiceTemplate().insert(vo)[0];
		// 提交单据编码
		commitBillCode(mainVO.getPk_org(), mainVO.getBill_code());
		return aggVO;
	}

	@Override
	public AggOvertimeVO insertDataDirect(AggOvertimeVO vo)
			throws BusinessException {
		OvertimehVO mainVO = (OvertimehVO) vo.getParentVO();
		AggOvertimeVO aggVO = getServiceTemplate().insert(vo)[0];
		// 提交单据编码
		commitBillCode(mainVO.getPk_org(), mainVO.getBill_code());
		return aggVO;
	}

	@Override
	public AggOvertimeVO[] updateArrayData(AggOvertimeVO[] vos)
			throws BusinessException {
		return updateData(vos, true, true);
	}

	protected AggOvertimeVO[] updateData(AggOvertimeVO[] vos,
			boolean isSetAuditInfo, boolean needCheck) throws BusinessException {
		BillMethods.processBeginEndDatePkJobOrgTimeZone(BillProcessHelper
				.toOvertimebVOList(vos).toArray(new OvertimebVO[0]));
		String pk_org = vos[0].getHeadVO().getPk_org();
		for (AggOvertimeVO aggVO : vos) {
			OvertimebVO[] bvos = aggVO.getBodyVOs();
			int len = ArrayUtils.getLength(bvos);
			if (len == 0)
				continue;
			OvertimehVO hvo = aggVO.getOvertimehVO();
			for (OvertimebVO bvo : bvos) {
				bvo.setPk_overtimetype(hvo.getPk_overtimetype());
				bvo.setPk_overtimetypecopy(hvo.getPk_overtimetypecopy());
				bvo.setPk_psndoc(hvo.getPk_psndoc());
				bvo.setPk_psnjob(hvo.getPk_psnjob());
				bvo.setPk_psnorg(hvo.getPk_psnorg());
				// 防止显示的已加班时长保存前和保存后不一样
				if (VOStatus.UNCHANGED == bvo.getStatus())
					bvo.setStatus(VOStatus.UPDATED);
			}
		}
		if (needCheck) {
			OvertimeApplyApproveValidator validator = new OvertimeApplyApproveValidator();
			validator.checkOvertimeAggVOs(vos);
			setValidatorFactory();
			new OvertimeApplyQueryMaintainImpl().check(
					pk_org,
					BillProcessHelper.toOvertimebVOList(vos).toArray(
							new OvertimebVO[0]));
			OvertimebVO[] checkvos = BillProcessHelper.toOvertimebVOList(vos)
					.toArray(new OvertimebVO[0]);
			// 校验是否跨业务单元-不放在BillValidatorAtServer中是因为点savebillaction时候就会走check方法这时候会造成校验顺序不对
			BillValidatorAtServer.checkCrossBU(pk_org, checkvos);
		}
		return getServiceTemplate().update(isSetAuditInfo, vos);
	}

	@Override
	public AggOvertimeVO updateData(AggOvertimeVO vo) throws BusinessException {
		return updateArrayData(new AggOvertimeVO[] { vo })[0];
	}

	/**
	 * 设置校验
	 */
	private void setValidatorFactory() {
		getServiceTemplate().setValidatorFactory(
				new OvertimeApplyValidatorFactory());
	}

	/**
	 * 直批
	 */
	@Override
	public AggOvertimeVO[] directApprove(int directApproveResult,
			String approveNote, AggOvertimeVO[] vos) throws BusinessException {
		String pk_org = vos[0].getOvertimehVO().getPk_org();

		// 所在期间已经封存的单据，是可以驳回和审批不通过的
		if (directApproveResult != IBillStatus.FREE
				&& directApproveResult != IBillStatus.NOPASS)
			BillValidatorAtServer.checkPeriod(pk_org,
					BillProcessHelper.toOvertimebVOs(vos));
		// 审批意见
		WorkflownoteVO[] worknoteVOs = new WorkflownoteVO[vos.length];
		// 存放需要更新字段的调班主表数组，新建的目的是为统一做更新的操作
		OvertimehVO[] updateMainVOs = new OvertimehVO[vos.length];
		// 需要更新的字段
		String[] updateFields = { OvertimehVO.APPROVER,
				OvertimehVO.APPROVE_TIME, OvertimehVO.APPROVE_NOTE,
				OvertimehVO.APPROVE_STATE };
		for (int i = 0; i < vos.length; i++) {
			OvertimehVO mainvo = vos[i].getOvertimehVO();
			// 获取回写信息，即设置需要修改的字段
			updateMainVOs[i] = changeBillData(mainvo, updateFields,
					approveNote, directApproveResult);
			// 执行审批操作前，将审批信息写到pub_workflownote中
			worknoteVOs[i] = buildWorkflownoteVO(directApproveResult,
					approveNote, mainvo);
		}
		// 批量新增审批意见
		getIPersistenceUpdate().insertVOArray(null, worknoteVOs, null);
		// 批量更新调班信息
		getIPersistenceUpdate().updateVOArray(null, updateMainVOs,
				updateFields, null);
		// 如果批准，才可进行务操作
		if (directApproveResult == IPfRetCheckInfo.PASSING)
			exexBills(pk_org, vos);
		return vos;
	}

	@Override
	public AggOvertimeVO directApprove(AggOvertimeVO vo)
			throws BusinessException {
		return null;
	}

	/**
	 * 生成审批意见
	 * 
	 * @param directApproveResult
	 *            ： 操作状态（批准等）
	 * @param approveNote
	 *            : 审批意见
	 * @param mainvo
	 *            : 调班主表
	 * @return
	 * @throws BusinessException
	 */
	public WorkflownoteVO buildWorkflownoteVO(int directApproveResult,
			String approveNote, OvertimehVO mainvo) throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid(mainvo.getPk_overtimeh());// 单据ID
		worknoteVO.setBillVersionPK(mainvo.getPrimaryKey());
		worknoteVO.setChecknote(approveNote);// 审批意见
		worknoteVO.setDealdate(PubEnv.getServerTime());// 审批时间
		worknoteVO.setSenddate(PubEnv.getServerTime());// 发送日期
		worknoteVO.setPk_org(mainvo.getPk_org());// 组织
		worknoteVO.setBillno(mainvo.getBill_code());// 单据号
		worknoteVO.setSenderman(mainvo.getApprover() == null ? mainvo
				.getBillmaker() : mainvo.getApprover());// 发送人
		worknoteVO
				.setApproveresult(IPfRetCheckInfo.NOSTATE == directApproveResult ? "R"
						: IPfRetCheckInfo.PASSING == directApproveResult ? "Y"
								: "N");
		worknoteVO.setApprovestatus(1);// 直批的状态
		worknoteVO
				.setIscheck(IPfRetCheckInfo.PASSING == directApproveResult ? "Y"
						: IPfRetCheckInfo.NOPASS == directApproveResult ? "N"
								: "X");
		worknoteVO.setActiontype("APPROVE");
		worknoteVO.setCheckman(mainvo.getApprover()); // 审批人
		worknoteVO.setWorkflow_type(WorkflowTypeEnum.Approveflow.getIntValue());
		worknoteVO.setPk_billtype(mainvo.getPk_billtype());// 单据类型
		return worknoteVO;
	}

	/**
	 * 根据流程信息录入加班记录的流程回写信息
	 * 
	 * @param updateFields
	 *            ： 需要更新的字段
	 * @param mainvo
	 * @param approveNote
	 *            ：审批意见
	 * @param directApproveResult
	 *            ：操作状态（批准等）
	 * @return
	 * @throws BusinessException
	 */
	private OvertimehVO changeBillData(OvertimehVO mainvo,
			String[] updateFields, String approveNote,
			Integer directApproveResult) throws BusinessException {
		if (mainvo == null)
			return null;
		mainvo.setAttributeValue(updateFields[0], PubEnv.getPk_user()); // 审批人pk_id
		mainvo.setAttributeValue(updateFields[1], PubEnv.getServerTime()); // 审批时间
		mainvo.setAttributeValue(updateFields[2], approveNote); // 审批意见
		mainvo.setAttributeValue(updateFields[3], directApproveResult); // 操作状态
		return mainvo;
	}

	/**
	 * 审批流提交，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doCommit(AggOvertimeVO[] vos)
			throws BusinessException {
		for (AggOvertimeVO vo : vos) {
			OvertimehVO billvo = vo.getOvertimehVO();
			// 更新单据状态
			billvo.setApprove_state(IPfRetCheckInfo.COMMIT);
			vo.setParentVO(billvo);
		}
		// 审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		// 走不到，这是不对的，因此这里主动进行期间封存校验
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(),
				BillProcessHelper.toOvertimebVOs(vos));
		updateData(vos, false, false);
		String pk_org = vos[0].getOvertimehVO().getPk_org();
		Integer approvetype = SysInitQuery.getParaInt(pk_org,
				OvertimeConst.OVERTIMETYPE_PARAM);
		if (approvetype != null
				&& approvetype.intValue() == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT) {
			// 审批方式为直批时发送通知
			// sendMessage( OvertimeConst.PK_NOTICE_SORT , PubEnv.getPk_group()
			// , pk_org , vos );
			sendDirApprMessage(pk_org, TaMessageConst.OVERTIMEMSGCODE, vos);
		}
		return vos;
	}

	/**
	 * 发送通知消息
	 * 
	 * @param pk_sort
	 * @param pkGroup
	 * @param pk_org
	 * @param vos
	 * @throws BusinessException
	 */
	// private void sendMessage(String pk_sort, String pkGroup, String pk_org,
	// AggOvertimeVO[] vos)throws BusinessException {
	// INotice setvice = NCLocator.getInstance().lookup(INotice.class);
	// NoticeTempletVO[] nt = setvice.queryDistributedTemplates(pk_sort,
	// pkGroup, pk_org, true);
	// if (nt != null && nt.length > 0) {
	// nt[0].setTransferValues(createUserValue(vos));
	// nt[0].setContent(TaSendMsgHelper.getNewContent(nt[0].getContent(),
	// vos.length, OvertimeConst.FIELDCODE));
	// if (StringUtils.isBlank( nt[0].getCurrentUserPk() ) ||
	// nt[0].getCurrentUserPk().length() != 20) {
	// // 如果模板的当前用户为空，则附上当前用户，或NC系统用户
	// nt[0].setCurrentUserPk( PubEnv.getPk_user() != null &&
	// PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
	// : INCSystemUserConst.NC_USER_PK );
	// }
	// setvice.sendNotice_RequiresNew(nt[0], pk_org, false);
	// }
	// }

	/**
	 * 审批流审批，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doApprove(AggOvertimeVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		syncHeadInfoToBody(vos);
		// 审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		// 走不到，这是不对的，因此这里主动进行期间封存校验
		// 2012.8.6与需求讨论后确定，对于已封存期间的申请单，可以做驳回和审核不通过。换句话说
		// 对于驳回和审核不通过的申请单，不需要校验期间封存
		OvertimehVO headVO = vos[0].getOvertimehVO();
		int appStatus = headVO.getApprove_state();
		if (appStatus != IBillStatus.FREE && appStatus != IBillStatus.NOPASS)
			PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(),
					BillProcessHelper.toOvertimebVOs(vos));
		vos = updateData(vos, false, false);
		if (appStatus == IBillStatus.CHECKPASS)
			exexBills(headVO.getPk_org(), vos);
		return vos;
	}

	/**
	 * 审批流收回，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doRecall(AggOvertimeVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		syncHeadInfoToBody(vos);
		// 审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		// 走不到，这是不对的，因此这里主动进行期间封存校验
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(),
				BillProcessHelper.toOvertimebVOs(vos));
		vos = updateData(vos, false, false);
		return vos;
	}

	/**
	 * 审批完或者直批完成，在加班登记表记录
	 * 
	 * @param tempvo
	 * @param vos
	 * @throws BusinessException
	 */
	public void exexBills(String pk_org, AggOvertimeVO[] vos)
			throws BusinessException {
		List<OvertimeRegVO> regvos = new ArrayList<OvertimeRegVO>();
		boolean flag = false;
		for (AggOvertimeVO aggvo : vos) {
			OvertimehVO mainvo = aggvo.getOvertimehVO();
			if (mainvo.getIsrerest().booleanValue()) {
				flag = true;
			}
			OvertimebVO[] subvos = aggvo.getOvertimebVOs();
			if (ArrayUtils.isEmpty(subvos))
				continue;
			for (OvertimebVO subvo : subvos) {
				regvos.add(buildOvertimeRegVO(mainvo, subvo));
			}
		}
		if (CollectionUtils.isNotEmpty(regvos)) {
			OvertimeRegVO[] data = new OvertimeRegisterMaintainImpl()
					.insertData(regvos.toArray(new OvertimeRegVO[0]), false);
			// 台湾本地化 由于从申请单过来的单子有可能员工已勾选转调休功能 所以这个地方需要对员工转调休
			if (flag) {
				// 此处需要对每一条记录进行转调休 跟易浪沟通过 -- 默认转调休至该单子所在的考勤期间
				IOvertimeRegisterManageMaintain server = NCLocator
						.getInstance().lookup(
								IOvertimeRegisterManageMaintain.class);
				for (OvertimeRegVO vo : data) {
					OvertimeRegVO[] davo = new OvertimeRegVO[1];
					vo.setToresthour(vo.getActhour());
					vo.setTorestmonth(String.valueOf(vo.getBegindate()
							.getMonth()));
					vo.setTorestyear(String
							.valueOf(vo.getBegindate().getYear()));
					davo[0] = vo;
					GeneralVO gvo = server.over2RestFirst(davo,
							String.valueOf(vo.getBegindate().getYear()),
							String.valueOf(vo.getBegindate().getMonth()));
					server.over2RestSecond(gvo,
							String.valueOf(vo.getBegindate().getYear()),
							String.valueOf(vo.getBegindate().getMonth()));
					vo.setIstorest(UFBoolean.TRUE);
					new OvertimeRegisterMaintainImpl().updateData(vo);
				}
			}
		}
	}

	/**
	 * 构建一条加班登记记录
	 * 
	 * @param mainvo
	 * @param subvo
	 * @return
	 */
	private OvertimeRegVO buildOvertimeRegVO(OvertimehVO mainvo,
			OvertimebVO subvo) {
		OvertimeRegVO regvo = new OvertimeRegVO();
		regvo.setBillsource(ICommonConst.BILL_SOURCE_APPLY);// 单据来源

		regvo.setPk_billsourceh(mainvo.getPk_overtimeh());// 加班申请主表对应记录的PK
		regvo.setPk_billsourceb(subvo.getPk_overtimeb());// 加班明细表对应记录的PK
		regvo.setPk_overtimegen(null);// 加班生成记录的PK
		regvo.setPk_group(mainvo.getPk_group());// 所属集团
		regvo.setPk_org(mainvo.getPk_org());// 所属组织
		regvo.setPk_org_v(mainvo.getPk_org_v());// 组织版本主键
		regvo.setPk_dept_v(mainvo.getPk_dept_v());// 部门版本主键
		regvo.setPk_overtimetype(mainvo.getPk_overtimetype());// 加班类别主键
		regvo.setPk_overtimetypecopy(mainvo.getPk_overtimetypecopy());// 加班类别拷贝主键
		regvo.setPk_psndoc(mainvo.getPk_psndoc());// 人员档案主键
		regvo.setPk_psnjob(mainvo.getPk_psnjob());// 人员任职主键
		regvo.setPk_psnorg(mainvo.getPk_psnorg());// 组织关系主键

		regvo.setActhour(subvo.getActhour());// 实际时长
		regvo.setDeduct(subvo.getDeduct()); // 扣除时长
		regvo.setIscheck(UFBoolean.FALSE); // 是否已校验
		regvo.setIsneedcheck(subvo.getIsneedcheck());// 是否需要校验
		regvo.setIstorest(UFBoolean.FALSE); // 是否转调休
		regvo.setToresthour(UFDouble.ZERO_DBL);
		regvo.setTorestmonth(null);
		regvo.setTorestyear(null);
		regvo.setOvertimealready(subvo.getOvertimealready()); // 所在考勤期间已加班时长
		regvo.setOvertimebegindate(subvo.getOvertimebegindate());// 开始日期
		regvo.setOvertimebegintime(subvo.getOvertimebegintime());// 开始时间
		regvo.setOvertimeenddate(subvo.getOvertimeenddate());// 结束日期
		regvo.setOvertimeendtime(subvo.getOvertimeendtime());// 结束时间
		regvo.setOvertimehour(subvo.getOvertimehour()); // 加班时长
		regvo.setOvertimeremark(subvo.getOvertimeremark());// 加班说明

		regvo.setStatus(VOStatus.NEW);// 新增状态

		return regvo;
	}

	/**
	 * 审批流弃审，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doUnApprove(AggOvertimeVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// 校验单据状态
		OvertimehVO[] mainvos = (OvertimehVO[]) AggVOHelper
				.getParentVOArrayFromAggVOs(vos, OvertimehVO.class);
		AggOvertimeVO[] oldvos = getServiceTemplate().queryByPks(
				AggOvertimeVO.class,
				StringPiecer.getStrArray(mainvos, OvertimehVO.PK_OVERTIMEH));
		for (AggOvertimeVO oldvo : oldvos) {
			checkPFPassingState(oldvo.getOvertimehVO().getApprove_state());
		}
		syncHeadInfoToBody(vos);
		// 审批不用走大部分的校验，因此updateData的第二个参数为false，但这样会导致期间封存校验也
		// 走不到，这是不对的，因此这里主动进行期间封存校验
		PeriodServiceFacade.checkDateScope(vos[0].getHeadVO().getPk_org(),
				BillProcessHelper.toOvertimebVOs(vos));
		vos = updateData(vos, false, false);
		return vos;
	}

	/**
	 * 审批流删除，用于审批流回调
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] doDelete(AggOvertimeVO[] vos)
			throws BusinessException {
		for (AggOvertimeVO vo : vos) {
			OvertimehVO headVO = vo.getOvertimehVO();
			String billType = headVO.getPk_billtype();
			deleteOldWorknote(headVO.getPrimaryKey(), billType);
		}
		deleteArrayData(vos);
		return vos;
	}

	/**
	 * 批量新增
	 */
	@Override
	public AggOvertimeVO[] insertData(OvertimebVO[] vos, boolean mergeBill)
			throws BusinessException {
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		if (ArrayUtils.isEmpty(vos))
			return null;
		String pk_org = vos[0].getPk_org();
		setValidatorFactory();
		// new OvertimeApplyQueryMaintainImpl().check(pk_org,
		// vos);//保存时的提示性校验中已经校验过了，为了效率问题此处不再校验
		BillValidatorAtServer.checkCrossBU(pk_org, vos);

		AggOvertimeVO[] mergedAggVOs = mergeSubvo(vos, mergeBill);

		// 校验
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
			AggOvertimeVO[] aggVOs = getServiceTemplate().insert(mergedAggVOs);
			commitBillCodes(pk_org, billCodes);
			return aggVOs;
		} catch (Exception e) {
			rollbackBillCodes(pk_org, billCodes);// 保存失败则回退单据号
			if (e instanceof BusinessException)
				throw (BusinessException) e;
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * 批量新增合并子VO
	 * 
	 * @param vos
	 * @param mergeBill
	 * @return
	 * @throws BusinessException
	 */
	private AggOvertimeVO[] mergeSubvo(OvertimebVO[] vos, boolean mergeBill)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// 设置已加班时长和校验标识,此处会更改批量新增设置的校验标识
		// new OvertimeDAO().setAlreadyHourAndCheckFlag(vos);
		new OvertimeDAO().setAlreadyHour(vos);
		AggOvertimeVO[] aggvoArray = null;
		if (!mergeBill) {
			aggvoArray = new AggOvertimeVO[vos.length];
			for (int i = 0; i < aggvoArray.length; i++) {
				AggOvertimeVO aggVO = new AggOvertimeVO();
				aggvoArray[i] = aggVO;
				OvertimebVO[] bvos = new OvertimebVO[] { vos[i] };
				aggVO.setParentVO(createMainVO(bvos));
				aggVO.setChildrenVO(bvos);
			}
		} else {
			aggvoArray = mergeSubVOs(AggOvertimeVO.class, vos);
		}
		return aggvoArray;
	}

	/**
	 * 批量新增合并子VO
	 * 
	 * @param vos
	 * @param mergeBill
	 * @return
	 * @throws BusinessException
	 */
	// @Deprecated
	// private AggOvertimeVO[] mergeSubvo1(OvertimebVO[] vos, boolean mergeBill)
	// throws BusinessException {
	// if(ArrayUtils.isEmpty(vos))
	// return null;
	// String pk_org = vos[0].getPk_org();
	//
	// //取当前组织所有班次信息
	// Map<String, AggShiftVO> aggShiftMap =
	// ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
	// Map<String, ShiftVO> shiftMap =
	// CommonUtils.transferAggMap2HeadMap(aggShiftMap);
	// TimeRuleVO timeruleVO =
	// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
	// Map<String, OverTimeTypeCopyVO> timeitemMap =
	// NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
	// IDateScope[] mergedScopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
	// IPeriodQueryService periodService =
	// NCLocator.getInstance().lookup(IPeriodQueryService.class);
	// Map<UFLiteralDate, PeriodVO> periodMap =
	// periodService.queryPeriodMapByDateScopes(pk_org, mergedScopes);
	// OvertimeDAO dao = new OvertimeDAO();
	// InSQLCreator isc = null;
	// try{
	// isc = new InSQLCreator();
	// String psndocInSQL = isc.getInSQL(vos, OvertimebVO.PK_PSNDOC);
	// ITBMPsndocQueryService psndocService =
	// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
	// Map<String, Map<UFLiteralDate, String>> psnDateOrgMap =
	// psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, mergedScopes);
	// IPsnCalendarQueryService calendarService =
	// NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
	// Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnAggCalendarMap =
	// calendarService.queryCalendarVOByPsnInSQL(pk_org, mergedScopes,
	// psndocInSQL);
	// for(OvertimebVO bvo:vos){
	// String pk_psndoc = bvo.getPk_psndoc();
	// Map<UFLiteralDate, String> dateOrgMap =
	// MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
	// Map<UFLiteralDate, AggPsnCalendar> calendarMap =
	// MapUtils.isEmpty(psnAggCalendarMap)?null:psnAggCalendarMap.get(pk_psndoc);
	// // 计算当前考勤期间已加班时长
	// dao.setAlreadyHour(bvo, shiftMap,
	// CommonMethods.createDateTimeZoneMap(dateOrgMap,timeruleVO.getTimeZoneMap()),calendarMap,timeitemMap,periodMap,timeruleVO);
	// //如果选择了是否允许校验但不符合允许校验的规则，要强制转换为不允许校验
	// if(bvo.getIsneedcheck().booleanValue()&&!new
	// OvertimeApplyQueryMaintainImpl().isCanCheck(bvo, timeruleVO, aggShiftMap,
	// shiftMap, calendarMap, dateOrgMap, periodMap))
	// bvo.setIsneedcheck(UFBoolean.FALSE);
	// }
	// }
	// finally{
	// if(isc!=null)
	// isc.clear();
	// }
	//
	// AggOvertimeVO[] aggvoArray = null;
	// if(!mergeBill){
	// aggvoArray = new AggOvertimeVO[vos.length];
	// for(int i=0;i<aggvoArray.length;i++){
	// AggOvertimeVO aggVO = new AggOvertimeVO();
	// aggvoArray[i] = aggVO;
	// OvertimebVO[] bvos = new OvertimebVO[]{vos[i]};
	// aggVO.setParentVO(createMainVO(bvos));
	// aggVO.setChildrenVO(bvos);
	// }
	// }
	// else{
	// aggvoArray = mergeSubVOs(AggOvertimeVO.class, vos);
	// }
	// return aggvoArray;
	// }

	/**
	 * 根据子表VO创建加班主表VO
	 * 
	 * @param vo
	 * @param pk_group
	 * @param pk_org
	 * @return
	 */
	@Override
	protected OvertimehVO createMainVO(OvertimebVO[] vos) {
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
		headVO.setApprove_state(IPfRetCheckInfo.NOSTATE);
		headVO.setBillmaker(PubEnv.getPk_user());
		headVO.setApply_date(PubEnv.getServerLiteralDate());
		headVO.setFun_code(OvertimeConst.FUNCODE_OVERTIMEAPPLE);
		headVO.setTranstypeid(vo.getTranstypeid());
		headVO.setTranstype(vo.getTranstype());
		headVO.setPk_billtype(OvertimeConst.BILLTYPE);
		headVO.setPk_overtimetype(vo.getPk_overtimetype());
		headVO.setPk_overtimetypecopy(vo.getPk_overtimetypecopy());

		if (vos.length > 1) {
			UFDouble sumHour = new UFDouble();
			for (OvertimebVO bvo : vos) {
				if (bvo.getOvertimehour() != null)
					sumHour = sumHour.add(bvo.getOvertimehour());
			}
			headVO.setSumhour(sumHour);
		} else {
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
		if (ArrayUtils.isEmpty(aggVOs))
			return;
		for (AggregatedValueObject aggVO : aggVOs) {
			OvertimebVO[] bvos = (OvertimebVO[]) aggVO.getChildrenVO();
			if (ArrayUtils.isEmpty(bvos))
				continue;
			OvertimehVO hvo = (OvertimehVO) aggVO.getParentVO();
			String pk_psndoc = hvo.getPk_psndoc();
			String pk_psnjob = hvo.getPk_psnjob();
			String pk_psnorg = hvo.getPk_psnorg();
			String pk_timeitem = hvo.getPk_timeitem();
			String pk_timeitemcopy = hvo.getPk_overtimetypecopy();
			for (OvertimebVO bvo : bvos) {
				bvo.setPk_psndoc(pk_psndoc);
				bvo.setPk_psnjob(pk_psnjob);
				bvo.setPk_psnorg(pk_psnorg);
				bvo.setPk_timeitem(pk_timeitem);
				bvo.setPk_overtimetypecopy(pk_timeitemcopy);
			}
		}
	}
}
