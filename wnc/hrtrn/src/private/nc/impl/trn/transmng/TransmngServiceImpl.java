package nc.impl.trn.transmng;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.sec.esapi.NCESAPI;
import nc.bs.uif2.VersionConflictException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.BillCodeHelper;
import nc.hr.utils.DataPermissionUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IBlacklistManageService;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceHome;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.hr.pf.HrPfHelper;
import nc.itf.hr.pf.IHrPf;
import nc.itf.trn.IItemSetAdapter;
import nc.itf.trn.TrnDelegator;
import nc.itf.trn.rds.IRdsManageService;
import nc.itf.trn.regmng.IRegmngQueryService;
import nc.itf.trn.transmng.ITransmngManageService;
import nc.itf.trn.transmng.ITransmngQueryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.IBean;
import nc.message.util.IDefaultMsgConst;
import nc.message.util.MessageCenter;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.ui.bd.ref.IRefConst;
import nc.vo.bd.psn.PsnClVO;
import nc.vo.hi.blacklist.BlacklistVO;
import nc.vo.hi.entrymng.HiSendMsgHelper;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.pub.RM2TRNLinkData;
import nc.vo.hi.trnstype.TrnstypeFlowVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hr.pf.PFQueryParams;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.org.DeptVO;
import nc.vo.org.JobVO;
import nc.vo.org.OrgVO;
import nc.vo.org.PostVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.trn.pub.BeanUtil;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.pub.TrnManageService;
import nc.vo.trn.transitem.TrnTransItemVO;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.trn.transmng.StapplyVO;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.uif2.LoginContext;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 调配管理查询、数据处理实现类
 */
public class TransmngServiceImpl extends TrnManageService implements ITransmngManageService, ITransmngQueryService {
	private boolean isqueryctrt = false;

	public TransmngServiceImpl() {
		this("hi_stapply");
	}

	public TransmngServiceImpl(String docName) {
		super(docName);
	}

	public AggregatedValueObject[] doCallBack(AggregatedValueObject[] vos) throws BusinessException {
		for (int i = 0; vos != null && i < vos.length; i++) {
			deleteOldWorknote((AggStapply) vos[i]);
			vos[i] = updateBill(vos[i], false);
		}
		return vos;
	}

	/**
	 * 为了适配提交即通过
	 */
	private void handleCtrtInfotj(StapplyVO billvo) throws BusinessException {
		boolean isCMStart = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_HRCM);
		if (isCMStart)// 为了适配提交即通过-- && IPfRetCheckInfo.PASSING ==
						// billvo.getApprove_state()
		{
			// 如果存在生效的合同，而且没有未生效的结解除或者终止合同，生成一条。
			String cond_hasCtrt = "pk_psnorg = '" + billvo.getPk_psnorg()
					+ "' and lastflag = 'Y' and conttype in (1, 2, 3) and isrefer = 'Y'";
			CtrtVO[] ctrtAllVOs = (CtrtVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, CtrtVO.class, cond_hasCtrt);
			boolean isIsreferCtrt = false;
			if (!ArrayUtils.isEmpty(ctrtAllVOs)) {
				isIsreferCtrt = true;
			}
			if (isIsreferCtrt) {
				String condition = "recordnum = 0 and isrefer = 'N' and pk_psnorg = '" + billvo.getPk_psnorg() + "'";
				CtrtVO[] ctrtVOs = (CtrtVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
						.retrieveByClause(null, CtrtVO.class, condition);
				if (ArrayUtils.isEmpty(ctrtVOs)) {
					// 插入一条新的未生效合同
					CtrtVO newCtrtVO = new CtrtVO();
					try {
						BeanUtils.copyProperties(newCtrtVO, ctrtAllVOs[0]);
					} catch (Exception e) {
						Logger.error(e.getMessage(), e);
					}
					newCtrtVO.setRecordnum(0);
					newCtrtVO.setLastflag(UFBoolean.FALSE);
					newCtrtVO.setIsrefer(UFBoolean.FALSE);
					int conttype = billvo.getIsrelease() == UFBoolean.TRUE ? 4 : 5;
					newCtrtVO.setConttype(conttype);
					newCtrtVO.setSigndate(new UFLiteralDate());
					newCtrtVO.setTermmonth(ctrtAllVOs[0].getTermmonth() == null ? null : ctrtAllVOs[0].getTermmonth());// 合同期限
					newCtrtVO.setPromonth(ctrtAllVOs[0].getPromonth() == null ? null : ctrtAllVOs[0].getPromonth());// 试用期限
					newCtrtVO.setPresenter(1);// 解除提出方---默认为用人单位

					NCLocator.getInstance().lookup(IPersistenceUpdate.class).insertVO(null, newCtrtVO, null);

				} else if (ctrtVOs[0].getConttype() == 1 || ctrtVOs[0].getConttype() == 2
						|| ctrtVOs[0].getConttype() == 3) {
					throw new BusinessException(ResHelper.getString("6009tran", "X6009tran0060")/*
																								 * @
																								 * res
																								 * "存在未生效的续签或变更的合同记录！"
																								 */);
				}
			}
		}
	}

	public AggregatedValueObject commitBill_RequiresNew(AggregatedValueObject aggvo) throws BusinessException {
		StapplyVO billvo = (StapplyVO) ((AggStapply) aggvo).getParentVO();
		// 处理审批流中的合同
		// if (IPfRetCheckInfo.PASSING == billvo.getApprove_state())
		// {
		if (billvo.getIsend() != null && billvo.getIsend().booleanValue() || billvo.getIsrelease() != null
				&& billvo.getIsrelease().booleanValue()) {
			handleCtrtInfotj(billvo);
		}
		// }

		String errMsg = getMsg(billvo);
		if (!StringUtils.isBlank(errMsg)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0153")/*
																						 * @
																						 * res
																						 * "单据中存在未填写的必输项"
																						 */);
		}

		if (!isHasFile(billvo)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0177")/* "单据没有上传附件" */);
		}

		String billmaker = (String) aggvo.getParentVO().getAttributeValue(StapplyVO.BILLMAKER);
		if (billmaker != null && INCSystemUserConst.NC_USER_PK.equals(billmaker)) {
			aggvo.getParentVO().setAttributeValue(StapplyVO.BILLMAKER, PubEnv.getPk_user());
		}
		return getIHrPf().commitBill_RequiresNew(aggvo);
	}

	public PfProcessBatchRetObject batchCommitBill(AggregatedValueObject[] bills) throws BusinessException {

		PFBatchExceptionInfo exInfo = new PFBatchExceptionInfo();
		ArrayList<AggregatedValueObject> successObj = new ArrayList<AggregatedValueObject>();
		for (int i = 0; i < bills.length; i++) {
			try {
				AggregatedValueObject aggVO = NCLocator.getInstance().lookup(ITransmngManageService.class)
						.commitBill_RequiresNew(bills[i]);
				successObj.add(aggVO);
			} catch (Exception e) {
				if (e instanceof VersionConflictException) {
					exInfo.putErrorMessage(i, bills[i], ((VersionConflictException) e).getBusiObject() == null ? ""
							: ((VersionConflictException) e).getBusiObject().toString());
				} else {
					exInfo.putErrorMessage(i, bills[i], StringUtils.isBlank(e.getMessage()) ? "" : e.getMessage());
				}
				Logger.error(e.getMessage(), e);
			}
		}
		return new PfProcessBatchRetObject(successObj.toArray(new AggregatedValueObject[0]), exInfo);
	}

	public AggStapply[] batchSaveBill(AggStapply aggvo, ArrayList<String> pks, Integer transMode, String transType,
			String billType, LoginContext context, String[] billCodes) throws BusinessException {
		boolean isAutoGenerateBillCode = isAutoGenerateBillCode(billType, context.getPk_group(), context.getPk_org());
		ArrayList<AggStapply> al = new ArrayList<AggStapply>();
		if (!isAutoGenerateBillCode) {
			// 如果不自动生成编码,则对编码规则进行加锁
			BillCodeHelper.lockBillCodeRule("hr_auto_billcode" + billType, 100);
		}
		try {
			InSQLCreator isc = new InSQLCreator();
			String insql = isc.getInSQL(pks.toArray(new String[0]));
			String condition = "pk_psnjob in (" + insql + ")";
			Collection<PsnJobVO> cvos = getDao().retrieveByClause(PsnJobVO.class, condition);
			PsnJobVO[] pvos = cvos.toArray(new PsnJobVO[0]);
			HashMap<String, PsnJobVO> psnmap = new HashMap<String, PsnJobVO>();
			for (PsnJobVO pvo : pvos) {
				psnmap.put(pvo.getPk_psnjob(), pvo);
			}
			TrnTransItemVO[] itemvos = TrnDelegator.getIItemSetQueryService().queryItemSetByOrg(
					TRNConst.TRNSITEM_BEANID, context.getPk_group(), context.getPk_org(), transType);
			String prefix = "ZD" + billType + PubEnv.getServerDate().toStdString();
			// 不自动生成单据号 /默认规则生成 "单据类型+yyyy-mm-dd+_流水号"
			String flowCode = SQLHelper.getFlowCode(prefix, StapplyVO.BILL_CODE, StapplyVO.class);
			for (int i = 0; i < pks.size(); i++) {
				AggStapply temp = clone(aggvo);
				StapplyVO head = (StapplyVO) temp.getParentVO();
				if (isAutoGenerateBillCode && billCodes != null && billCodes.length > 0 && billCodes[i] != null) {
					head.setBill_code(billCodes[i]);
				} else {
					head.setBill_code(prefix + "_" + getFlowCode(flowCode, i));
				}
				head.setStapply_mode(transMode);// 调配单时才赋调配方式
				head.setPk_trnstype(transType);
				head.setApprove_state(IPfRetCheckInfo.NOSTATE);
				head.setBillmaker(context.getPk_loginUser());
				// head.setApply_date(PubEnv.getServerLiteralDate());
				head.setPk_billtype(billType);
				head.setPk_org(context.getPk_org());
				head.setPk_group(context.getPk_group());
				head.setFun_code(context.getNodeCode());
				// PsnJobVO oldJobVO = queryByPk(PsnJobVO.class, pks.get(i),
				// true);

				// 20180907, 如果為空值就帶出前一筆的資料
				PsnJobVO oldJobVO = psnmap.get(pks.get(i));
				if (StringUtils.isEmpty(head.getNewpk_dept()) || head.getNewpk_dept() == "~") {
					head.setNewpk_dept(oldJobVO.getPk_dept());
				}
				head.setPk_psnjob(oldJobVO.getPk_psnjob());
				head.setPk_psndoc(oldJobVO.getPk_psndoc());
				head.setPk_psnorg(oldJobVO.getPk_psnorg());

				head.setAssgid(oldJobVO.getAssgid());
				if (TRNConst.BUSITYPE_TRANSITION.equals(billType) && TRNConst.TRANSMODE_CROSS_IN == transMode) {
					// 调入时 每个人的原人事组织是要查询的
					head.setPk_old_hi_org(HiSQLHelper.getHrorg(oldJobVO.getPk_psnorg(), oldJobVO.getAssgid()));
				} else {
					head.setPk_old_hi_org(context.getPk_org());
				}
				// 每个人的原合同管理组织都需要查询
				head.setPk_old_hrcm_org(HiSQLHelper.getEveryHrorg(oldJobVO.getPk_psnorg(), oldJobVO.getAssgid(),
						ManagescopeBusiregionEnum.psnpact));

				// 设置调配前信息
				for (String attr : oldJobVO.getAttributeNames()) {
					head.setAttributeValue("old" + attr, oldJobVO.getAttributeValue(attr));
				}

				// 设置转正后信息
				// 岗位/职务信息特殊处理
				String[] flds = { StapplyVO.NEWPK_POST, StapplyVO.NEWPK_POSTSERIES, StapplyVO.NEWPK_JOB,
						StapplyVO.NEWPK_JOBGRADE, StapplyVO.NEWPK_JOBRANK, StapplyVO.NEWSERIES };

				for (int j = 0; itemvos != null && j < itemvos.length; j++) {
					if (itemvos[j].getItemkey().startsWith("old") || ArrayUtils.contains(flds, itemvos[j].getItemkey())) {
						// 转正前项目或是岗位职务相关项目不处理
						continue;
					}
					if (itemvos[j] != null && itemvos[j].getIsdefault() != null
							&& itemvos[j].getIsdefault().booleanValue()
							&& head.getAttributeValue(itemvos[j].getItemkey()) == null) {
						if ((TRNConst.BUSITYPE_TRANSITION.equals(billType) && TRNConst.TRANSMODE_CROSS_OUT == transMode)
								|| (TRNConst.BUSITYPE_DIMISSION.equals(billType))) {
							// 调配单据-调出、离职单据：这里为批量新增操作，这些情况下部门不需要默认带出
							String[] fldsout = { StapplyVO.NEWPK_DEPT };// 调配/离职后部门
							if (!ArrayUtils.contains(fldsout, itemvos[j].getItemkey())) {
								// 不在上述业务字段范围内的其余字段是要勾了默认就都可以带出
								head.setAttributeValue(itemvos[j].getItemkey(),
										oldJobVO.getAttributeValue(itemvos[j].getItemkey().substring(3)));
							}
						} else {
							// 调配单据-组织内和调入
							head.setAttributeValue(itemvos[j].getItemkey(),
									oldJobVO.getAttributeValue(itemvos[j].getItemkey().substring(3)));
						}
					}
				}

				// 处理岗位职务信息
				// 默认项目中没有职务,则职务为空,那么职务相关的就都为空
				String newPost = head.getNewpk_post();
				String newJob = head.getNewpk_job();

				if (newPost != null && newJob != null) {
					// 岗位职务都有,不操作
				} else if (newPost != null && newJob == null) {
					// 有岗位没职务,不操作
					// 此处只考虑职等的默认情况
					TrnTransItemVO rankItem = getItemByItemkey(itemvos, StapplyVO.NEWPK_JOBRANK);
					if (rankItem != null && rankItem.getIsdefault() != null && rankItem.getIsdefault().booleanValue()
							&& head.getNewpk_jobrank() == null) {
						head.setNewpk_jobrank(head.getOldpk_jobrank());
					}
				} else if (newPost == null && newJob != null) {
					// 没岗位有职务,不操作
				} else {
					// 没岗位没职务考虑默认的情况
					// 只有在前后部门相同的情况下才能处理岗位相关信息,否则岗位不赋值
					if (head.getOldpk_dept().equals(head.getNewpk_dept())) {
						TrnTransItemVO postItem = getItemByItemkey(itemvos, StapplyVO.NEWPK_POST);
						TrnTransItemVO jobItem = getItemByItemkey(itemvos, StapplyVO.NEWPK_JOB);
						if (postItem != null && postItem.getIsdefault() != null
								&& postItem.getIsdefault().booleanValue()) {
							// 如果岗位默认,则岗位职务都按照前项目设置
							head.setNewpk_post(head.getOldpk_post());
							head.setNewpk_postseries(head.getOldpk_postseries());
							head.setNewpk_job(head.getOldpk_job());
							head.setNewpk_jobrank(head.getOldpk_jobrank());
							head.setNewpk_jobgrade(head.getOldpk_jobgrade());
							head.setNewseries(head.getOldseries());
						} else if (jobItem != null && jobItem.getIsdefault() != null
								&& jobItem.getIsdefault().booleanValue()) {
							// 岗位没有默认,职务相关的按照前项目设置
							head.setNewpk_job(head.getOldpk_job());
							head.setNewpk_jobrank(head.getOldpk_jobrank());
							head.setNewpk_jobgrade(head.getOldpk_jobgrade());
							head.setNewseries(head.getOldseries());
						}
					}
				}

				// 20180907, 如果為空值就帶出前一筆的資料
				if (StringUtils.isEmpty(head.getNewseries()) || head.getNewseries() == "~") {
					head.setNewseries(head.getOldseries());
				}
				if (StringUtils.isEmpty(head.getNewpk_jobrank()) || head.getNewpk_jobrank() == "~") {
					head.setNewpk_jobrank(head.getOldpk_jobrank());
				}

				al.add(temp);
			}

			checkBillCodeRepeat(al.toArray(new AggStapply[0]));
			HrBatchService hbs = new HrBatchService("");
			// return saveBatchBill(al.toArray(new AggStapply[0]));
			AggStapply[] ret = hbs.insert(al.toArray(new AggStapply[0]));
			return ret;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (!isAutoGenerateBillCode) {
				// 如果不自动生成编码,则对编码规则进行加锁
				BillCodeHelper.unlockBillCodeRule("hr_auto_billcode" + billType);
			}
		}
	}

	private TrnTransItemVO getItemByItemkey(TrnTransItemVO[] itemvos, String itemKey) {
		for (int i = 0; itemvos != null && i < itemvos.length; i++) {
			if (itemKey.equals(itemvos[i].getItemkey())) {
				return itemvos[i];
			}
		}
		return null;
	}

	/**
	 * 复制一个聚合VO
	 */
	private AggStapply clone(AggStapply src) {

		AggStapply trg = new AggStapply();
		StapplyVO head = new StapplyVO();
		trg.setParentVO(head);
		for (String attrName : src.getParentVO().getAttributeNames()) {
			trg.getParentVO().setAttributeValue(attrName, src.getParentVO().getAttributeValue(attrName));
		}
		return trg;
	}

	public Hashtable<String, String[]> createUserValue(AggregatedValueObject[] aggvos) throws BusinessException {

		String[] fieldCode = TRNConst.FIELDCODE_TRN;
		Hashtable<String, String[]> hm = new Hashtable<String, String[]>();
		for (int i = 0; aggvos != null && i < aggvos.length; i++) {
			StapplyVO bill = (StapplyVO) ((AggStapply) aggvos[i]).getParentVO();
			for (int j = 0; j < fieldCode.length; j++) {
				String value = "";
				if (StapplyVO.BILL_CODE.equals(fieldCode[j])) {
					// 单据编码
					value = bill.getBill_code();
				} else if (StapplyVO.APPROVE_STATE.equals(fieldCode[j])) {
					// 单据状态
					value = TRNConst.getBillStateName(bill.getApprove_state() == null ? 102 : bill.getApprove_state());
				} else if (StapplyVO.PK_PSNJOB.equals(fieldCode[j])) {
					// 人员姓名
					value = VOUtils.getDocName(PsndocVO.class, bill.getPk_psndoc());
				} else if (StapplyVO.EFFECTDATE.equals(fieldCode[j])) {
					value = bill.getEffectdate() == null ? "" : bill.getEffectdate().toStdString();
				} else if (StapplyVO.OLDPK_ORG.equals(fieldCode[j])) {
					// 旧组织
					value = VOUtils.getDocName(OrgVO.class, bill.getOldpk_org());
				} else if (StapplyVO.NEWPK_ORG.equals(fieldCode[j])) {
					// 新组织
					value = VOUtils.getDocName(OrgVO.class, bill.getNewpk_org());
				} else if (StapplyVO.OLDPK_DEPT.equals(fieldCode[j])) {
					// 旧部门
					value = VOUtils.getDocName(DeptVO.class, bill.getOldpk_dept());
				} else if (StapplyVO.NEWPK_DEPT.equals(fieldCode[j])) {
					// 新部门
					value = VOUtils.getDocName(DeptVO.class, bill.getNewpk_dept());
				} else if (StapplyVO.OLDPK_PSNCL.equals(fieldCode[j])) {
					// 旧人员类别
					value = VOUtils.getDocName(PsnClVO.class, bill.getOldpk_psncl());
				} else if (StapplyVO.NEWPK_PSNCL.equals(fieldCode[j])) {
					// 新人员类别
					value = VOUtils.getDocName(PsnClVO.class, bill.getNewpk_psncl());
				} else if (StapplyVO.OLDPK_POST.equals(fieldCode[j])) {
					// 旧岗位
					value = VOUtils.getDocName(PostVO.class, bill.getOldpk_post());
				} else if (StapplyVO.NEWPK_POST.equals(fieldCode[j])) {
					// 新岗位
					value = VOUtils.getDocName(PostVO.class, bill.getNewpk_post());
				} else if (StapplyVO.OLDPK_JOB.equals(fieldCode[j])) {
					// 旧职务
					value = VOUtils.getDocName(JobVO.class, bill.getOldpk_job());
				} else if (StapplyVO.NEWPK_JOB.equals(fieldCode[j])) {
					// 新职务
					value = VOUtils.getDocName(JobVO.class, bill.getNewpk_job());
				} else {
					value = "";
				}
				hm.put(fieldCode[j] + i, new String[] { value });
			}
		}
		return hm;
	}

	// 删除审批意见的操作
	private void deleteOldWorknote(AggStapply vo) throws BusinessException {
		getIHrPf().deleteWorkflowNote(vo);
	}

	public PfProcessBatchRetObject directApprove(Object[] selData, String approveNote, int blPassed)
			throws BusinessException {

		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> bill = new ArrayList<AggStapply>();

		for (int i = 0; i < selData.length; i++) {
			try {
				AggStapply retVO = NCLocator
						.getInstance()
						.lookup(ITransmngManageService.class)
						.singleDirectApprove_RequiresNew((AggStapply) selData[i], PubEnv.getPk_user(),
								PubEnv.getServerTime(), approveNote, blPassed);
				bill.add(retVO);
			} catch (Exception e) {
				if (e instanceof VersionConflictException) {
					errInfo.putErrorMessage(i, selData[i], ((VersionConflictException) e).getBusiObject() == null ? ""
							: ((VersionConflictException) e).getBusiObject().toString());
				} else {
					errInfo.putErrorMessage(i, selData[i], StringUtils.isBlank(e.getMessage()) ? "" : e.getMessage());
				}
				Logger.error(e.getMessage(), e);
			}
		}

		return new PfProcessBatchRetObject(bill.toArray(new AggStapply[0]), errInfo);

	}

	public AggStapply singleDirectApprove_RequiresNew(AggStapply aggvo, String pk_user, UFDateTime approveTime,
			String approveNote, int blPassed) throws BusinessException {

		IFlowBizItf itf = NCObject.newInstance(aggvo).getBizInterface(IFlowBizItf.class);
		StapplyVO billvo = (StapplyVO) aggvo.getParentVO();
		// 校验必输项
		if (blPassed == IPfRetCheckInfo.PASSING) {
			// 离职或组织内调动 提交态
			// 调入或调出 进行中态

			if ((IPfRetCheckInfo.COMMIT == billvo.getApprove_state() && TRNConst.TRANSMODE_INNER == billvo
					.getStapply_mode())
					|| (IPfRetCheckInfo.GOINGON == billvo.getApprove_state() && (TRNConst.TRANSMODE_CROSS_IN == billvo
							.getStapply_mode() || TRNConst.TRANSMODE_CROSS_OUT == billvo.getStapply_mode()))) {
				if (!StringUtils.isBlank(getMsg(billvo))) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0153")/*
																								 * @
																								 * res
																								 * "单据中存在未填写的必输项"
																								 */);
				}
			}
		}

		// 只有批准才发送通知 过滤一下,将需要发送通知的过滤出来
		if (blPassed == IPfRetCheckInfo.PASSING
				&& IPfRetCheckInfo.COMMIT == billvo.getApprove_state()
				&& billvo.getPk_billtype().equals(TRNConst.BUSITYPE_TRANSITION)
				&& billvo.getStapply_mode() != null
				&& (TRNConst.TRANSMODE_CROSS_IN == billvo.getStapply_mode() || TRNConst.TRANSMODE_CROSS_OUT == billvo
						.getStapply_mode())) {
			// 只有跨组织调配,才需要发送二次通知
			String pkOrg = billvo.getStapply_mode() == TRNConst.TRANSMODE_CROSS_IN ? billvo.getPk_old_hi_org() : billvo
					.getPk_hi_org();
			// 为调配单据时
			String tempCode = HICommonValue.msgcode_trns_approve;// 调配直批通知消息模板源编码
			HiSendMsgHelper.sendMessage1(tempCode, new AggStapply[] { aggvo }, pkOrg);
		}

		if (blPassed == IPfRetCheckInfo.PASSING) {
			// 批准
			if (IPfRetCheckInfo.COMMIT == billvo.getApprove_state()
					&& billvo.getStapply_mode() != null
					&& billvo.getPk_billtype().equals(TRNConst.BUSITYPE_TRANSITION)
					&& (TRNConst.TRANSMODE_CROSS_IN == billvo.getStapply_mode() || TRNConst.TRANSMODE_CROSS_OUT == billvo
							.getStapply_mode())) {
				// 跨组织调配,第一审,状态更新为审批中
				writeTransApproveInfo(billvo, pk_user, approveTime, approveNote, IPfRetCheckInfo.GOINGON);
			} else {
				// 其他情况,状态更新为审批通过
				writeTransApproveInfo(billvo, pk_user, approveTime, approveNote, blPassed);
			}
		} else if (blPassed == IPfRetCheckInfo.NOPASS) {
			// 不批准,所有的单据都更新为审批未通过
			writeTransApproveInfo(billvo, pk_user, approveTime, approveNote, blPassed);
		} else {
			// 驳回,所有单据更新为自由,作废之前所有的审批意见,插入一条驳回的审批意见
			writeTransApproveInfo(billvo, null, null, approveNote, blPassed);
			// true-作废全部;fale-作废自己审批的一条
			invalidWorkflowNote(itf, true);
		}
		// 执行审批操作前，将审批信息写到pub_workflownote中
		writeWorknote(billvo, pk_user, approveNote, blPassed, billvo.getPk_billtype());
		// 处理审批流中的合同
		if (billvo.getIsend() != null && billvo.getIsend().booleanValue() || billvo.getIsrelease() != null
				&& billvo.getIsrelease().booleanValue()) {
			handleCtrtInfo(billvo);
		}

		return updateBill(aggvo, false);
	}

	/**
	 * 审批通过时，处理合同信息 <br>
	 * Created on 2014-3-18 19:48:57<br>
	 * 
	 * @param billvo
	 * @throws BusinessException
	 * @author caiqm
	 */
	private void handleCtrtInfo(StapplyVO billvo) throws BusinessException {
		boolean isCMStart = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_HRCM);
		if (isCMStart && IPfRetCheckInfo.PASSING == billvo.getApprove_state())// 为了适配提交即通过--
																				// &&
																				// IPfRetCheckInfo.PASSING
																				// ==
																				// billvo.getApprove_state()
		{
			// 如果存在生效的合同，而且没有未生效的结解除或者终止合同，生成一条。
			String cond_hasCtrt = "pk_psnorg = '" + billvo.getPk_psnorg()
					+ "' and lastflag = 'Y' and conttype in (1, 2, 3) and isrefer = 'Y'";
			CtrtVO[] ctrtAllVOs = (CtrtVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, CtrtVO.class, cond_hasCtrt);
			boolean isIsreferCtrt = false;
			if (!ArrayUtils.isEmpty(ctrtAllVOs)) {
				isIsreferCtrt = true;
			}
			if (isIsreferCtrt) {
				String condition = "recordnum = 0 and isrefer = 'N' and pk_psnorg = '" + billvo.getPk_psnorg() + "'";
				CtrtVO[] ctrtVOs = (CtrtVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
						.retrieveByClause(null, CtrtVO.class, condition);
				if (ArrayUtils.isEmpty(ctrtVOs)) {
					// 插入一条新的未生效合同
					CtrtVO newCtrtVO = new CtrtVO();
					try {
						BeanUtils.copyProperties(newCtrtVO, ctrtAllVOs[0]);
					} catch (Exception e) {
						Logger.error(e.getMessage(), e);
					}
					newCtrtVO.setRecordnum(0);
					newCtrtVO.setLastflag(UFBoolean.FALSE);
					newCtrtVO.setIsrefer(UFBoolean.FALSE);
					int conttype = billvo.getIsrelease() == UFBoolean.TRUE ? 4 : 5;
					newCtrtVO.setConttype(conttype);
					newCtrtVO.setSigndate(new UFLiteralDate());
					newCtrtVO.setTermmonth(ctrtAllVOs[0].getTermmonth() == null ? null : ctrtAllVOs[0].getTermmonth());// 合同期限
					newCtrtVO.setPromonth(ctrtAllVOs[0].getPromonth() == null ? null : ctrtAllVOs[0].getPromonth());// 试用期限
					newCtrtVO.setPresenter(1);// 解除提出方---默认为用人单位

					NCLocator.getInstance().lookup(IPersistenceUpdate.class).insertVO(null, newCtrtVO, null);

				} else if (ctrtVOs[0].getConttype() == 1 || ctrtVOs[0].getConttype() == 2
						|| ctrtVOs[0].getConttype() == 3) {
					throw new BusinessException(ResHelper.getString("6009tran", "X6009tran0060")/*
																								 * @
																								 * res
																								 * "存在未生效的续签或变更的合同记录！"
																								 */);
				}
			}
		}
	}

	public PfProcessBatchRetObject directUnApprove(AggregatedValueObject[] bills) throws BusinessException {

		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> bill = new ArrayList<AggStapply>();

		for (int i = 0; i < bills.length; i++) {
			try {
				AggStapply retVO = NCLocator.getInstance().lookup(ITransmngManageService.class)
						.singleDirectUnApprove_RequiresNew((AggStapply) bills[i]);
				bill.add(retVO);
			} catch (Exception e) {
				if (e instanceof VersionConflictException) {
					errInfo.putErrorMessage(i, bills[i], ((VersionConflictException) e).getBusiObject() == null ? ""
							: ((VersionConflictException) e).getBusiObject().toString());
				} else {
					errInfo.putErrorMessage(i, bills[i], StringUtils.isBlank(e.getMessage()) ? "" : e.getMessage());
				}
				Logger.error(e.getMessage(), e);
			}
		}

		return new PfProcessBatchRetObject(bill.toArray(new AggStapply[0]), errInfo);
	}

	@Override
	public AggStapply singleDirectUnApprove_RequiresNew(AggStapply bill) throws BusinessException {
		IFlowBizItf itf = NCObject.newInstance(bill).getBizInterface(IFlowBizItf.class);
		StapplyVO head = (StapplyVO) bill.getParentVO();
		// 得到有效的审批意见的数量,对于直批,理论上在取消审批时只有1或2条,其他都是作废意见
		int count = getValidNoteCount(itf);
		// 将单据状态前推
		if (count >= 2
				&& head.getPk_billtype().equals(TRNConst.BUSITYPE_TRANSITION)
				&& head.getStapply_mode() != null
				&& (TRNConst.TRANSMODE_CROSS_IN == head.getStapply_mode() || TRNConst.TRANSMODE_CROSS_OUT == head
						.getStapply_mode())) {
			// 跨组织调配,审批通过或两次审批审批不通过,状态更新为审批中
			WorkflownoteVO last = getLastNote(getAllWorkflownoteVO(itf));
			writeTransApproveInfo(head, last.getSenderman(), PubEnv.getServerTime(), "", IPfRetCheckInfo.GOINGON);
		} else {
			// 其他情况,状态更新为自由态
			writeTransApproveInfo(head, null, null, null, IPfRetCheckInfo.COMMIT);
		}
		// 作废自己审批的审批意见
		invalidWorkflowNote(itf, false);
		return updateBill(bill, false);// 更新并处理TS
	}

	/**
	 * 审批流审批
	 */
	public Object doApprove(AggregatedValueObject[] vosin) throws BusinessException {
		// 开启新事物更新单据状态
		AggStapply[] vos = NCLocator.getInstance().lookup(ITransmngManageService.class)
				.batchUpdateBill_RequiresNew(vosin);
		// AggStapply[] vos = batchUpdateBill_RequiresNew(vosin);
		// 为了支持消息模板上的审批和移动审批，将执行单据的操作移至后台处理
		return execBills(vos);
	}

	/**
	 * 开启新事务，更新单据状态
	 * 
	 * @param vosin
	 * @return
	 * @throws BusinessException
	 * @author heqiaoa 2014-11-17
	 */
	public AggStapply[] batchUpdateBill_RequiresNew(AggregatedValueObject[] vosin) throws BusinessException {
		AggStapply[] vos = new AggStapply[vosin.length];
		for (int i = 0; i < vosin.length; i++) {
			vos[i] = (AggStapply) vosin[i];
		}
		validate(vos);
		for (int i = 0; i < vos.length; i++) {
			// 处理审批流中的合同
			StapplyVO billvo = (StapplyVO) vos[i].getParentVO();
			if (IPfRetCheckInfo.PASSING == billvo.getApprove_state()) {
				if (billvo.getIsend() != null && billvo.getIsend().booleanValue() || billvo.getIsrelease() != null
						&& billvo.getIsrelease().booleanValue()) {
					handleCtrtInfo(billvo);
				}
			}
			vos[i] = updateBill(vos[i], false);
		}
		return vos;
	}

	/**
	 * 为了支持消息模板上的审批和移动审批，将执行单据的操作移至后台处理 将原来在前台Model中实现的execBills函数移至后台实现
	 * 
	 * @param datas
	 * @return
	 * @throws BusinessException
	 * @author heqiaoa 2014-11-18
	 */
	public AggStapply[] execBills(AggStapply[] vos) throws BusinessException {
		ArrayList<AggStapply> al = new ArrayList<AggStapply>();
		ArrayList<AggStapply> allvo = new ArrayList<AggStapply>();
		for (int i = 0; i < vos.length; i++) {
			AggStapply agg = vos[i];
			Integer apprState = ((StapplyVO) agg.getParentVO()).getApprove_state();
			UFLiteralDate effectDate = ((StapplyVO) agg.getParentVO()).getEffectdate();
			if (effectDate != null && effectDate.compareTo(PubEnv.getServerLiteralDate()) <= 0 && apprState != null
					&& apprState == IPfRetCheckInfo.PASSING) {
				al.add(agg);
			}

			allvo.add(agg);
		}

		ArrayList<AggStapply> bills = new ArrayList<AggStapply>();
		if (al.size() > 0) {
			// 后台校验时需要查询合同
			isqueryctrt = true;
			// 审批后自动执行情况
			// 为了传入合适的参数进行编制校验，这里用pk_org和pk_group构造一个LoginContext
			LoginContext tempContext = new LoginContext();
			AggStapply aggvo = al.get(0);
			StapplyVO parentVO = (StapplyVO) aggvo.getParentVO();
			tempContext.setPk_group(parentVO.getPk_group());
			tempContext.setPk_org(parentVO.getPk_org());

			// 返回一个map 包括执行成功的单据，及执行不成功的信息
			HashMap<String, Object> result = NCLocator.getInstance().lookup(ITransmngManageService.class)
					.execBills(al.toArray(new AggStapply[0]), tempContext, false);
			bills = (ArrayList<AggStapply>) result.get(TRNConst.RESULT_BILLS);
			String msg = (String) result.get(TRNConst.RESULT_MSG);
			if (!StringUtils.isBlank(msg)) {
				// 构造NCMessage
				NCMessage ncMessage = new NCMessage();
				MessageVO messageVO = new MessageVO();
				messageVO.setMsgsourcetype(IDefaultMsgConst.NOTICE);// 消息源类型
				messageVO.setReceiver(PubEnv.getPk_user());// 设置接收人 多个接收人之间以逗号隔开
				messageVO.setIsdelete(UFBoolean.FALSE);// 接收删除标记
				messageVO.setSender(INCSystemUserConst.NC_USER_PK);
				// 如果发送时间为空则默认为服务器时间
				messageVO.setSendtime(PubEnv.getServerTime());
				messageVO.setDr(0);
				messageVO.setSubject(ResHelper.getString("6007entry", "16007entry0015")/*
																						 * res
																						 * "单据审批失败"
																						 */);
				messageVO.setContent(msg);
				ncMessage.setMessage(messageVO);
				NCMessage[] message = new NCMessage[1];
				message[0] = ncMessage;
				try {
					MessageCenter.sendMessage(message);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}

		// 将执行成功的和不成功的合并
		if (bills != null && bills.size() > 0)// 若没有执行成功的单据，则直接将审批通过的单据返回回去刷新界面
		{
			for (int i = 0; i < allvo.size(); i++) {
				AggStapply aggivo = allvo.get(i);
				for (int j = 0; j < bills.size(); j++) {
					AggStapply aggjvo = allvo.get(j);
					if (aggivo.getParentVO().getPrimaryKey().equals(aggjvo.getParentVO().getPrimaryKey())) {
						allvo.remove(i);
					}
				}
			}
			vos = (AggStapply[]) ArrayUtils.addAll(allvo.toArray(new AggStapply[0]), bills.toArray(new AggStapply[0]));
		}

		return vos;
	}

	/**
	 * 审批流提交
	 */
	public Object doCommit(AggStapply[] vos) throws BusinessException {

		String pk_org = ((StapplyVO) vos[0].getParentVO()).getPk_org();
		String billtype = ((StapplyVO) vos[0].getParentVO()).getPk_billtype();
		List<AggregatedValueObject> ls = new ArrayList<AggregatedValueObject>();
		String functionCode = ((StapplyVO) vos[0].getParentVO()).getFun_code();// 功能节点编码，用于区分是离职还是调配
		for (int i = 0; vos != null && i < vos.length; i++) {
			StapplyVO billvo = (StapplyVO) vos[i].getParentVO();

			if (!isHasFile(billvo)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0177")/* "单据没有上传附件" */);
			}

			// 更新单据状态
			billvo.setApprove_state(IPfRetCheckInfo.COMMIT);
			if (INCSystemUserConst.NC_USER_PK.equals(billvo.getBillmaker())) {
				billvo.setBillmaker(PubEnv.getPk_user());
			}
			vos[i].setParentVO(billvo);
			vos[i] = updateBill(vos[i], false);
			ls.add(vos[i]);
		}
		Integer approvetype = HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW;
		try {
			approvetype = SysInitQuery.getParaInt(pk_org, IHrPf.hashBillTypePara.get(billtype));
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		if (approvetype != null && approvetype == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT) {
			/** modify start:发送通知方式修改 2013-4-10 yunana */
			// 审批方式为直批时发送通知
			// HiSendMsgHelper.sendMessage(TRNConst.BUSITYPE_DIMISSION.equals(billtype)
			// ?
			// "1001Z7APP00000000005" : "1001Z7APP00000000004",
			// PubEnv.getPk_group(), pk_org, createUserValue(vos),
			// TRNConst.FIELDCODE_TRN, vos == null ? 0 :
			// vos.length);
			String tempCode = null;
			if (HICommonValue.NODECODE_DIMISSIONAPPLY.equals(functionCode)) {
				// 为离职单据时
				tempCode = HICommonValue.msgcode_dims_approve;// 离职直批通知消息模板编码
			} else {
				// 为调配单据时
				tempCode = HICommonValue.msgcode_trns_approve;// 调配直批通知消息模板源编码
			}
			HiSendMsgHelper.sendMessage1(tempCode, vos, pk_org);
			/** modify end：yunana */
		}
		return ls.toArray(new AggregatedValueObject[0]);
	}

	/**
	 * 刪除
	 */
	public AggStapply[] doDelete(AggStapply[] vos) throws BusinessException {
		List<String> strBillIdList = new ArrayList<String>();
		String billType = "";
		for (AggStapply vo : vos) {
			billType = (String) vo.getParentVO().getAttributeValue(StapplyVO.PK_BILLTYPE);
			String pk_group = (String) vo.getParentVO().getAttributeValue(StapplyVO.PK_GROUP);
			String pk_org = (String) vo.getParentVO().getAttributeValue(StapplyVO.PK_ORG);
			String bill_code = (String) vo.getParentVO().getAttributeValue(StapplyVO.BILL_CODE);
			strBillIdList.add((String) vo.getParentVO().getAttributeValue(StapplyVO.PK_HI_STAPPLY));
			if (isAutoGenerateBillCode(billType, pk_group, pk_org)) {
				getIBillcodeManage().returnBillCodeOnDelete(billType, pk_group, pk_org, bill_code, null);
			}
			deleteOldWorknote(vo);
			deleteBill(vo);
		}

		if (!strBillIdList.isEmpty()) {
			getIHrPf().deleteOldWorkflowNote(billType, strBillIdList.toArray(new String[0]));
		}
		return vos;
	}

	public Object doPush(AggStapply vo) throws BusinessException {

		return vo;
	}

	/**
	 * 保存流程推出的离职单据
	 */
	public Object doSaveBills(AggStapply[] vos) throws BusinessException {

		try {
			if (vos == null || vos.length == 0) {
				return null;
			}
			String prefix = "ZD" + TRNConst.BUSITYPE_DIMISSION + PubEnv.getServerDate().toStdString();
			// 不自动生成单据号 /默认规则生成 "单据类型+yyyy-mm-dd+_流水号"
			String flowCode = SQLHelper.getFlowCode(prefix, StapplyVO.BILL_CODE, StapplyVO.class);
			for (int i = 0; i < vos.length; i++) {
				StapplyVO billvo = (StapplyVO) vos[i].getParentVO();

				billvo.setPk_billtype(TRNConst.BUSITYPE_DIMISSION);
				// billvo.setTranstype(TRNConst.BUSITYPE_DIMISSION);
				// 单据状态
				billvo.setApprove_state(IPfRetCheckInfo.NOSTATE);
				boolean isAutoGenerateBillCode = isAutoGenerateBillCode(TRNConst.BUSITYPE_DIMISSION,
						billvo.getPk_group(), billvo.getPk_org());

				if (isAutoGenerateBillCode) {
					billvo.setBill_code(getIBillcodeManage().getPreBillCode_RequiresNew(TRNConst.BUSITYPE_DIMISSION,
							billvo.getPk_group(), billvo.getPk_org()));
				} else {
					billvo.setBill_code(prefix + "_" + getFlowCode(flowCode, i));
				}

				PsnJobVO psnJobVO = queryByPk(PsnJobVO.class, billvo.getPk_psnjob(), true);
				billvo.setPk_psndoc(psnJobVO.getPk_psndoc());
				billvo.setPk_psnorg(psnJobVO.getPk_psnorg());
				billvo.setAssgid(psnJobVO.getAssgid());
				// 设置离职前单据信息
				for (String attr : psnJobVO.getAttributeNames()) {
					billvo.setAttributeValue("old" + attr, psnJobVO.getAttributeValue(attr));
				}

				// 离职后就只有组织部门人员类别,不需要查询离职项目
				// SuperVO[] itemvos =
				// TrnDelegator.getIItemSetQueryService().queryItemSetByOrg(TRNConst.TRNSITEM_BEANID,
				// billvo.getPk_group(),
				// billvo.getPk_org(), billvo.getPk_trnstype());
				// IBean ibean =
				// BeanUtil.getBeanEntity(TRNConst.TRNSITEM_BEANID);
				// List<IItemSetAdapter> iitemadpls =
				// BeanUtil.getBizImpObjFromVo(ibean,
				// IItemSetAdapter.class, itemvos);
				// IItemSetAdapter[] items = iitemadpls.toArray(new
				// IItemSetAdapter[0]);
				// for (IItemSetAdapter item : items) {
				// // 离职后默认值
				// if (item.getItemkey().startsWith("new") &&
				// item.getIsdefault().booleanValue()) {
				// billvo.setAttributeValue(item.getItemkey(),
				// psnJobVO.getAttributeValue(item.getItemkey().substring(3)));
				// }
				// }
				billvo.setPk_group(PubEnv.getPk_group());
				billvo.setAttributeValue(StapplyVO.NEWPK_ORG, psnJobVO.getPk_org());
				billvo.setAttributeValue(StapplyVO.NEWPK_DEPT, psnJobVO.getPk_dept());
				billvo.setAttributeValue(StapplyVO.NEWPK_PSNCL, psnJobVO.getPk_psncl());

				if (billvo.getPk_group() == null) {// 集团
					billvo.setPk_group(PubEnv.getPk_group());
				}

				if (billvo.getPk_org() == null) {// 组织
					billvo.setPk_org(psnJobVO.getPk_hrorg());
				}

				if (billvo.getPk_old_hi_org() == null) {// 离职前组织
					billvo.setPk_old_hi_org(psnJobVO.getPk_hrorg());
				}

				if (billvo.getPk_hi_org() == null) {// 离职后组织
					billvo.setPk_hi_org(psnJobVO.getPk_hrorg());
				}

				// 默认不改变HR组织
				billvo.setStapply_mode(TRNConst.TRANSMODE_INNER);
				billvo.setIshrssbill(UFBoolean.FALSE);
				billvo.setFun_code(TRNConst.NODECODE_DIMISSIONAPPLY);
				billvo.setPk_trnstype("1002Z710000000008GSX");// 默认是辞职

				// 申请人和创建人为空，则用NC系统用户
				if (StringUtils.isBlank(billvo.getCreator()))
					billvo.setCreator(INCSystemUserConst.NC_USER_PK);
				if (StringUtils.isBlank(billvo.getBillmaker()))
					billvo.setBillmaker(INCSystemUserConst.NC_USER_PK);
				if (billvo.getCreationtime() == null)
					billvo.setCreationtime(PubEnv.getServerTime());
				if (billvo.getApply_date() == null)
					billvo.setApply_date(PubEnv.getServerLiteralDate());
				insertBill(vos[i]);
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0154")/*
																						 * @
																						 * res
																						 * "业务流配置信息不完整，生成离职申请单出错！"
																						 */);
		}
		return vos;
	}

	/**
	 * 审批流弃审
	 */
	public Object doUnapprove(AggregatedValueObject[] vos) throws BusinessException {

		List<String> stmngPKList = new ArrayList<String>();
		for (int i = 0; i < vos.length; i++) {
			stmngPKList.add(((StapplyVO) vos[i].getParentVO()).getPk_hi_stapply());
		}
		if (!stmngPKList.isEmpty()) {
			InSQLCreator isc = new InSQLCreator();
			String insql = isc.getInSQL(stmngPKList.toArray(new String[0]));
			String strCondition = "pk_hi_stapply in (" + insql + ")";
			StapplyVO[] stApplyVOs = (StapplyVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, StapplyVO.class, strCondition);
			for (int i = 0; i < stApplyVOs.length; i++) {
				int approvestate = stApplyVOs[i].getApprove_state();

				checkPFPassingState(approvestate);

				if (approvestate == HRConstEnum.EXECUTED) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0209")/* "已执行的单据不能取消审批！" */);
				}
			}
		}
		for (int i = 0; i < vos.length; i++) {
			vos[i] = updateBill(vos[i], false);
		}
		return vos;
	}

	public void checkPFPassingState(int pfsate) throws BusinessException {
		// guoqt在审批节点操作单据是走nc.ui.hr.pf.action.PFUnApproveAction的doAction()方法，但是在消息中心的工作任务中是走该方法，所以要同时判断单据审批通过还是弃审，两种情况都不能取消审批
		if (IPfRetCheckInfo.NOPASS == pfsate) {
			throw new BusinessException(ResHelper.getString("6007entry", "16007entry0014")
			/* @res "单据审批未通过,不能取消审批！" */);
		}
	}

	/**
	 * 得到单据的所有审批意见
	 * 
	 * @param head
	 * @return WorkflownoteVO[]
	 * @throws BusinessException
	 */
	private WorkflownoteVO[] getAllWorkflownoteVO(IFlowBizItf itf) throws BusinessException {
		String transtype = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
		return getIPFWorkflowQry().queryWorkitems(itf.getBillId(), transtype,
				WorkflowTypeEnum.Approveflow.getIntValue(), 0);
	}

	@Override
	public String getBillIdSql(int iBillStatus, String billType) throws BusinessException {

		String pks = getIHrPf().getBillIdSql(iBillStatus, billType);
		String strWorkFlowWhere = StapplyVO.PK_HI_STAPPLY + " in (" + (StringUtils.isBlank(pks) ? " '1<>1' " : pks)
				+ ") ";
		return strWorkFlowWhere;
	}

	/**
	 * 获取当前最大的流水号
	 * 
	 * @param prefix
	 * @param i
	 * @return String
	 * @throws BusinessException
	 */
	private String getFlowCode(String code, int i) throws BusinessException {
		Integer value = Integer.valueOf(code);
		return org.apache.commons.lang.StringUtils.leftPad(value + i + "", 5, '0');
	}

	private IBillcodeManage getIBillcodeManage() {

		return NCLocator.getInstance().lookup(IBillcodeManage.class);
	}

	private IHrBillCode getIHrBillCode() {

		return NCLocator.getInstance().lookup(IHrBillCode.class);
	}

	private IHrPf getIHrPf() {

		return NCLocator.getInstance().lookup(IHrPf.class);
	}

	private IPersistenceUpdate getIPersistenceUpdate() {

		return NCLocator.getInstance().lookup(IPersistenceUpdate.class);
	}

	private IPFWorkflowQry getIPFWorkflowQry() {

		return NCLocator.getInstance().lookup(IPFWorkflowQry.class);
	}

	/**
	 * 得到最后一条有效的审批意见
	 * 
	 * @param workflownoteVOs
	 * @return WorkflownoteVO
	 */
	private WorkflownoteVO getLastNote(WorkflownoteVO[] workflownoteVOs) {

		for (int i = workflownoteVOs.length - 1; i >= 0; i--) {
			if ("X".equals(workflownoteVOs[i].getIscheck())) {
				continue;
			}
			return workflownoteVOs[i];
		}
		return null;
	}

	private String getMsg(StapplyVO billvo) throws BusinessException {

		// 对于审批通过的单据,校验所有必输的项目是否都输入完毕
		SuperVO[] itemvos = TrnDelegator.getIItemSetQueryService().queryItemSetByOrg(TRNConst.TRNSITEM_BEANID,
				billvo.getPk_group(), billvo.getPk_org(), billvo.getPk_trnstype());
		IBean ibean = BeanUtil.getBeanEntity(TRNConst.TRNSITEM_BEANID);
		List<IItemSetAdapter> iitemadpls = BeanUtil.getBizImpObjFromVo(ibean, IItemSetAdapter.class, itemvos);
		for (IItemSetAdapter item : iitemadpls) {
			if (item == null || item.getItemkey().startsWith("old")) {
				// 前项目不校验
				continue;
			}
			if (item.getIsnotnull().booleanValue() && isNull(billvo.getAttributeValue(item.getItemkey()))) {
				return '\n' + billvo.getBill_code();
			}
		}
		return "";
	}

	/**
	 * 得到单据有效的审批意见的条数
	 * 
	 * @param itf
	 * @return int
	 * @throws BusinessException
	 */
	private int getValidNoteCount(IFlowBizItf itf) throws BusinessException {
		WorkflownoteVO[] all = getAllWorkflownoteVO(itf);
		if (all == null || all.length == 0) {
			return 0;
		}
		int count = 0;
		for (WorkflownoteVO vo : all) {
			if ("X".equals(vo.getIscheck())) {
				continue;
			}
			count++;
		}
		return count;
	}

	/**
	 * 检查重号
	 * 
	 * @param <T>
	 * @param billvos
	 */
	private <T extends AggregatedValueObject> void checkBillCodeRepeat(T... billvos) throws BusinessException {
		StringBuffer errMsg = new StringBuffer();
		ArrayList<String> repeatCodes = new ArrayList<String>();
		for (T vo : billvos) {
			IFlowBizItf itf = NCObject.newInstance(vo).getBizInterface(IFlowBizItf.class);
			String billCode = itf.getBillNo();
			String pk_entryapply = itf.getBillId();
			String billType = itf.getBilltype();
			String whereSql = StapplyVO.BILL_CODE + " = '" + NCESAPI.sqlEncode(billCode) + "' and pk_group = '"
					+ PubEnv.getPk_group() + "'  and " + StapplyVO.PK_BILLTYPE + " = '" + billType + "'";
			if (!StringUtils.isBlank(pk_entryapply)) {
				whereSql += " and " + StapplyVO.PK_HI_STAPPLY + " <> '" + pk_entryapply + "'";
			}
			int count = NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.getCountByCondition(StapplyVO.getDefaultTableName(), whereSql);
			if (count > 0) {
				errMsg.append('\n' + ResHelper.getString("6007entry", "06007entry0050")/*
																						 * @
																						 * res
																						 * "单据["
																						 */
						+ billCode + ResHelper.getString("6007entry", "06007entry0051")/*
																						 * @
																						 * res
																						 * "]的单据号已存在"
																						 */);
				repeatCodes.add(billCode);
				continue;
			}
		}
		if (errMsg.length() > 0) {
			BillCodeRepeatBusinessException ex = new BillCodeRepeatBusinessException(ResHelper.getString("6007entry",
					"06007entry0052")/*
									 * @ res "保存失败,具体原因如下:"
									 */
					+ errMsg.toString());
			ex.setRepeatCodes(repeatCodes.toArray(new String[0]));
			throw ex;
		}
	}

	private BlacklistVO getBlackListVO(CertVO certVO, PsndocVO psndocVO, String pk_org, String pk_group) {
		BlacklistVO blacklistVO = new BlacklistVO();
		blacklistVO.setId(certVO.getId());
		blacklistVO.setIdtype(certVO.getIdtype());
		blacklistVO.setPsnname(psndocVO.getName());
		blacklistVO.setPsnname2(psndocVO.getName2());
		blacklistVO.setPsnname3(psndocVO.getName3());
		blacklistVO.setPsnname4(psndocVO.getName4());
		blacklistVO.setPsnname5(psndocVO.getName5());
		blacklistVO.setPsnname6(psndocVO.getName6());
		blacklistVO.setPk_org(pk_org);
		blacklistVO.setPk_group(pk_group);
		return blacklistVO;
	}

	// 校验黑名单
	private void validateBlack(StapplyVO head) throws BusinessException {
		String pk_psndoc = head.getPk_psndoc();
		String pk_org = head.getNewpk_org();
		String pk_group = head.getPk_group();
		String condition = " pk_psndoc = '" + pk_psndoc + "'";
		CertVO[] certVOs = (CertVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, CertVO.class, condition);
		PsndocVO[] psndocVOs = (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, PsndocVO.class, condition);
		if (ArrayUtils.isEmpty(certVOs) || ArrayUtils.isEmpty(psndocVOs)) {
			return;
		}
		BlacklistVO blacklistVO = this.getBlackListVO(certVOs[0], psndocVOs[0], pk_org, pk_group);
		boolean blInBlacklist = NCLocator.getInstance().lookup(IBlacklistManageService.class)
				.isInBlacklist(blacklistVO);
		if (blInBlacklist) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0235")/*
																					 * @
																					 * res
																					 * "该人员已经在黑名单中存在！"
																					 */);
		}
	}

	@Override
	public <T extends AggregatedValueObject> T updateBill(T billvo, boolean blChangeAuditInfo) throws BusinessException {
		T agg = null;
		StapplyVO head = (StapplyVO) billvo.getParentVO();
		// if (head != null && HRConstEnum.EXECUTED != head.getApprove_state())
		// {// 执行更新时不需要校验
		// // 校验黑名单
		this.validateBlack(head);
		// }

		try {

			checkBillCodeRepeat(billvo);

			agg = super.updateBill(billvo, blChangeAuditInfo);

		} catch (Exception e) {

			if (e instanceof BillCodeRepeatBusinessException) {
				String[] codes = ((BillCodeRepeatBusinessException) e).getRepeatCodes();
				if (isAutoGenerateBillCode(head.getPk_billtype(), PubEnv.getPk_group(), head.getPk_org())
						&& codes != null) {
					for (int i = 0; i < codes.length; i++) {
						try {
							NCLocator
									.getInstance()
									.lookup(IBillcodeManage.class)
									.AbandonBillCode_RequiresNew(head.getPk_billtype(), PubEnv.getPk_group(),
											head.getPk_org(), codes[i]);
						} catch (Exception e2) {
							Logger.error(e2.getMessage(), e2);
						}
					}
				}
				throw (BillCodeRepeatBusinessException) e;
			}
			throw new BusinessException(e.getMessage());
		}
		return agg;

	}

	@Override
	public <T extends AggregatedValueObject> T insertBill(T billvo) throws BusinessException {
		T agg = null;
		StapplyVO head = (StapplyVO) billvo.getParentVO();
		// 校验黑名单
		this.validateBlack(head);
		try {

			checkBillCodeRepeat(billvo);

			agg = super.insertBill(billvo);
			// 提交单据号
			if (isAutoGenerateBillCode(head.getPk_billtype(), head.getPk_group(), head.getPk_org())) {
				getIHrBillCode().commitPreBillCode(head.getPk_billtype(), head.getPk_group(), head.getPk_org(),
						head.getBill_code());
			}
		} catch (Exception e) {

			if (e instanceof BillCodeRepeatBusinessException) {
				String[] codes = ((BillCodeRepeatBusinessException) e).getRepeatCodes();
				if (isAutoGenerateBillCode(head.getPk_billtype(), PubEnv.getPk_group(), head.getPk_org())
						&& codes != null) {
					for (int i = 0; i < codes.length; i++) {
						try {
							NCLocator
									.getInstance()
									.lookup(IBillcodeManage.class)
									.AbandonBillCode_RequiresNew(head.getPk_billtype(), PubEnv.getPk_group(),
											head.getPk_org(), codes[i]);
						} catch (Exception e2) {
							Logger.error(e2.getMessage(), e2);
						}
					}
				}
				throw (BillCodeRepeatBusinessException) e;
			}

			// 发生异常如果是自动生成单据号,则会滚单据号
			if (isAutoGenerateBillCode(head.getPk_billtype(), PubEnv.getPk_group(), head.getPk_org())) {
				NCLocator
						.getInstance()
						.lookup(IHrBillCode.class)
						.rollbackPreBillCode(head.getPk_billtype(), PubEnv.getPk_group(), head.getPk_org(),
								head.getBill_code());
			}
			throw new BusinessException(e.getMessage());
		}
		return agg;
	}

	/**
	 * 作废审批意见
	 * 
	 * @param itf
	 * @param isDealAll
	 * @throws BusinessException
	 */
	private void invalidWorkflowNote(IFlowBizItf itf, boolean isDealAll) throws BusinessException {

		WorkflownoteVO[] vos = getAllWorkflownoteVO(itf);
		if (isDealAll) {
			for (WorkflownoteVO vo : vos) {
				vo.setIscheck("X");
				vo.setApprovestatus(4);
			}
			getIPersistenceUpdate().updateVOArray(null, vos, new String[] { "ischeck", "approvestatus" }, null);
		} else {
			if (vos == null || vos.length == 0) {
				return;
			}
			WorkflownoteVO last = getLastNote(vos);
			last.setIscheck("X");
			last.setApprovestatus(4);
			last.setMessagenote(ResHelper.getString("6009tran", "06009tran0155")/*
																				 * @
																				 * res
																				 * "审批人取消审批单据"
																				 */);
			getIPersistenceUpdate().updateVO(null, last, new String[] { "ischeck", "approvestatus", "messagenote" },
					null);
		}
	}

	private boolean isAutoGenerateBillCode(String billType, String pk_group, String pk_org) throws BusinessException {
		BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(billType, pk_group, pk_org);
		return billCodeContext != null;
	}

	private boolean isCheckman(IFlowBizItf itf) throws BusinessException {
		String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
		// 如果是需要审批的，需要根据当前的审批人来决定是否可以修改单据
		return getIPFWorkflowQry().isCheckman(itf.getBillId(), strBillType, PubEnv.getPk_user());
	}

	private boolean isDirectApprove(IFlowBizItf itf) throws BusinessException {
		String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
		return !getIPFWorkflowQry().isApproveFlowStartup(itf.getBillId(), strBillType);
	}

	private boolean isNoApprove(IFlowBizItf itf) throws BusinessException {

		WorkflownoteVO[] vo = getAllWorkflownoteVO(itf);
		return vo == null || vo.length == 0;
	}

	private boolean isNull(Object o) {

		if (o == null || o.toString() == null || o.toString().trim().equals("")) {
			return true;
		}
		return false;
	}

	@Override
	public AggStapply[] queryByCondition(LoginContext context, String condition) throws BusinessException {

		return queryByCondition(AggStapply.class, condition);
	}

	@Override
	public AggStapply queryByPk(String pk) throws BusinessException {

		return queryByPk(AggStapply.class, pk);
	}

	/**
	 * 检查操作权限
	 * 
	 * @param operateCode
	 * @param mdOperateCode
	 * @param resourceCode
	 * @param aggVO
	 * @return boolean
	 * @throws BusinessException
	 */
	private boolean checkDataPermission(String operateCode, String mdOperateCode, String resourceCode,
			AggregatedValueObject aggVO) throws BusinessException {
		if (StringUtils.isBlank(operateCode) && StringUtils.isBlank(mdOperateCode) || StringUtils.isBlank(resourceCode)) {
			return true;
		}

		boolean blHasDataPermission = true;

		String resDataId = aggVO.getParentVO().getPrimaryKey();
		if (!StringUtils.isBlank(mdOperateCode)) {
			blHasDataPermission = DataPermissionUtils.isUserhasPermissionByMetaDataOperation(resourceCode, resDataId,
					mdOperateCode);
		} else {
			blHasDataPermission = DataPermissionUtils.isUserhasPermission(resourceCode, resDataId, operateCode);
		}

		return blHasDataPermission;
	}

	@Override
	public PfProcessBatchRetObject transApproveValidation(AggStapply[] aggvos, LoginContext context,
			String operateCode, String mdOperateCode, String resourceCode) throws BusinessException {

		NCLocator.getInstance().lookup(IHrPf.class).validateApproveType(aggvos);

		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> al = new ArrayList<AggStapply>();
		IFlowBizItf itf = null;
		for (int i = 0; i < aggvos.length; i++) {

			AggStapply dbvo = queryByPk(AggStapply.class, aggvos[i].getParentVO().getPrimaryKey(), true);

			if (dbvo == null) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0156")/*
																									 * @
																									 * res
																									 * "单据不存在."
																									 */);
				continue;
			}

			if (!checkDataPermission(operateCode, mdOperateCode, resourceCode, dbvo)) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0157")/*
																									 * @
																									 * res
																									 * "没有权限审批单据."
																									 */);
				continue;
			}

			StapplyVO head = (StapplyVO) dbvo.getParentVO();
			if (!ArrayUtils.contains(new int[] { IPfRetCheckInfo.COMMIT, IPfRetCheckInfo.GOINGON },
					head.getApprove_state())) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0158")/*
																									 * @
																									 * res
																									 * "单据状态不为[提交]或[审批中],不能审批."
																									 */);
				continue;
			}

			itf = NCObject.newInstance(dbvo).getBizInterface(IFlowBizItf.class);

			boolean isDirectApp = isDirectApprove(itf);
			// 审批流检查当前审批人
			if (!isDirectApp && !isCheckman(itf)) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0159")/*
																									 * @
																									 * res
																									 * "不是单据当前审批人"
																									 */);
				continue;
			}
			// 直批检查当前组织是不是要审批的组织
			if (isDirectApp) {
				if (TRNConst.TRANSMODE_CROSS_IN == head.getStapply_mode()) {
					// 调入
					if (IPfRetCheckInfo.COMMIT == head.getApprove_state()
							&& !head.getPk_hi_org().equals(context.getPk_org())) {
						// 调配后组织=当前组织
						errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0159")/*
																											 * @
																											 * res
																											 * "不是单据当前审批人"
																											 */);
						continue;
					}
					if (IPfRetCheckInfo.GOINGON == head.getApprove_state()
							&& !head.getPk_old_hi_org().equals(context.getPk_org())) {
						// 调配前组织=当前组织
						errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0159")/*
																											 * @
																											 * res
																											 * "不是单据当前审批人"
																											 */);
						continue;
					}
				}
				if (TRNConst.TRANSMODE_CROSS_OUT == head.getStapply_mode()) {
					// 调出
					if (IPfRetCheckInfo.COMMIT == head.getApprove_state()
							&& !head.getPk_old_hi_org().equals(context.getPk_org())) {
						// 调配前组织=当前组织
						errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0159")/*
																											 * @
																											 * res
																											 * "不是单据当前审批人"
																											 */);
						continue;
					}
					if (IPfRetCheckInfo.GOINGON == head.getApprove_state()
							&& !head.getPk_hi_org().equals(context.getPk_org())) {
						// 调配后组织=当前组织
						errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0159")/*
																											 * @
																											 * res
																											 * "不是单据当前审批人"
																											 */);
						continue;
					}
				}
			}
			al.add(aggvos[i]);
		}

		return new PfProcessBatchRetObject(al.toArray(new AggStapply[0]), errInfo);
	}

	@Override
	public ValidationFailure transCallBackValidation(AggStapply[] aggvos) throws BusinessException {

		String errName = "";
		for (AggStapply agg : aggvos) {
			NCObject ncObj = NCObject.newInstance(agg);
			IFlowBizItf itf = ncObj.getBizInterface(IFlowBizItf.class);
			String pk = itf.getBillId();
			//
			AggregatedValueObject dbVO = queryByPk(agg.getClass(), pk, true);
			NCObject dbObj = NCObject.newInstance(dbVO);
			IFlowBizItf dbitf = dbObj.getBizInterface(IFlowBizItf.class);
			int iApproveStatus = dbitf.getApproveStatus();
			//
			if (IPfRetCheckInfo.COMMIT != iApproveStatus) {
				if (IPfRetCheckInfo.PASSING == iApproveStatus) {
					// 如果是审批通过并且是直批单据则可以收回
					if (isNoApprove(dbitf)) {
						continue;
					}
				}
				errName += '\n' + itf.getBillNo();
			}
		}
		if (!StringUtils.isBlank(errName)) {
			return new ValidationFailure(ResHelper.getString("6009tran", "06009tran0160")/*
																						 * @
																						 * res
																						 * "收回失败:以下单据的的状态不为[提交]态."
																						 */+ errName);
		}
		return null;
	}

	public PfProcessBatchRetObject transUnApproveValidation(AggStapply[] aggvos, String operateCode,
			String mdOperateCode, String resourceCode) throws BusinessException {

		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggStapply> al = new ArrayList<AggStapply>();
		IFlowBizItf itf = null;
		for (int i = 0; i < aggvos.length; i++) {

			AggStapply dbvo = queryByPk(AggStapply.class, aggvos[i].getParentVO().getPrimaryKey(), true);

			if (dbvo == null) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0156")/*
																									 * @
																									 * res
																									 * "单据不存在."
																									 */);
				continue;
			}

			if (!checkDataPermission(operateCode, mdOperateCode, resourceCode, dbvo)) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0161")/*
																									 * @
																									 * res
																									 * "没有权限弃审单据."
																									 */);
				continue;
			}

			StapplyVO head = (StapplyVO) dbvo.getParentVO();

			int iApproveState = head.getApprove_state();
			if (ArrayUtils.contains(new Integer[] { IPfRetCheckInfo.NOSTATE, IPfRetCheckInfo.COMMIT }, iApproveState)) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0162")/*
																									 * @
																									 * res
																									 * "单据审批尚未开始,不能取消审批."
																									 */);
				continue;
			}
			// 有“审批未通过”状态的单据，也是不能取消审批的
			if (ArrayUtils.contains(new Integer[] { IPfRetCheckInfo.NOPASS }, iApproveState)) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0163")/*
																									 * @
																									 * res
																									 * "单据已经审批未通过,不能取消审批."
																									 */);
				continue;
			}
			// 有“执行”状态的单据，如果已经是“执行中”或者“已执行”的状态，也是不能取消审批的
			if (ArrayUtils.contains(new Integer[] { HRConstEnum.EXECUTING, HRConstEnum.EXECUTED }, iApproveState)) {
				errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0164")/*
																									 * @
																									 * res
																									 * "单据已经已执行,不能取消审批."
																									 */);
				continue;
			}

			itf = NCObject.newInstance(dbvo).getBizInterface(IFlowBizItf.class);

			// 检查是直批情况下的取消审批
			if (isDirectApprove(itf)) {
				WorkflownoteVO workflownoteVOs[] = getAllWorkflownoteVO(itf);
				if (workflownoteVOs == null || workflownoteVOs.length == 0) {
					errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6001pf", "06001pf0058")/*
																									 * @
																									 * res
																									 * "单据审批尚未开始或为提交即通过单据,不能取消审批."
																									 */);
					continue;
				}
				// 是否自己直批单据,要判断最后一条非作废单据的审批人是否是当前用户
				WorkflownoteVO lastVO = getLastNote(workflownoteVOs);
				if (lastVO != null && !PubEnv.getPk_user().equals(lastVO.getCheckman())) {
					errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6009tran", "06009tran0165")/*
																										 * @
																										 * res
																										 * "单据不是您自己直批的单据,不能取消审批."
																										 */);
					continue;
				}
			}
			al.add(aggvos[i]);
		}

		return new PfProcessBatchRetObject(al.toArray(new AggStapply[0]), errInfo);

	}

	private void validate(AggStapply[] vos) throws BusinessException {

		String errMsg = "";
		for (AggStapply vo : vos) {
			StapplyVO billvo = (StapplyVO) vo.getParentVO();
			if (IPfRetCheckInfo.PASSING == billvo.getApprove_state()) {
				errMsg += getMsg(billvo);
			}
		}
		if (!StringUtils.isBlank(errMsg)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0153")/*
																						 * @
																						 * res
																						 * "单据中存在未填写的必输项"
																						 */);
		}
	}

	/**
	 * 回写调配表的审批信息
	 * 
	 * @param headVO
	 * @param strApproveId
	 * @param strApproveDate
	 * @param strCheckNote
	 * @param intAppState
	 * @throws Exception
	 */
	private void writeTransApproveInfo(StapplyVO headVO, String strApproveId, UFDateTime strApproveDate,
			String strCheckNote, Integer intAppState) throws BusinessException {

		if (headVO == null) {
			return;
		}
		headVO.setAttributeValue(StapplyVO.APPROVER, strApproveId);
		headVO.setAttributeValue(StapplyVO.APPROVE_TIME, strApproveDate);
		headVO.setAttributeValue(StapplyVO.APPROVE_NOTE, strCheckNote);
		headVO.setAttributeValue(StapplyVO.APPROVE_STATE, intAppState);
	}

	/**
	 * 插入一条审批意见
	 * 
	 * @param aggVO
	 * @param strApproveId
	 * @param strCheckNote
	 * @param blPassed
	 * @param billtype
	 * @return WorkflownoteVO
	 * @throws BusinessException
	 */
	private void writeWorknote(StapplyVO headVO, String strApproveId, String strCheckNote, int blPassed, String billtype)
			throws BusinessException {

		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid(headVO.getPrimaryKey());// 单据ID
		worknoteVO.setBillVersionPK(headVO.getPrimaryKey());
		worknoteVO.setChecknote(strCheckNote);// 审批意见
		// 发送日期
		worknoteVO.setSenddate(PubEnv.getServerTime());
		worknoteVO.setDealdate(PubEnv.getServerTime());// 审批日期
		// 组织
		worknoteVO.setPk_org(headVO.getPk_org());
		// 单据编号
		worknoteVO.setBillno(headVO.getBill_code());
		// 发送人
		String sendman = headVO.getApprover() == null ? headVO.getBillmaker() : headVO.getApprover();
		worknoteVO.setSenderman(sendman);
		// Y,审批通过，N，审批不通过
		worknoteVO.setApproveresult(IPfRetCheckInfo.NOSTATE == blPassed ? "R"
				: IPfRetCheckInfo.PASSING == blPassed ? "Y" : "N");
		worknoteVO.setApprovestatus(1);// 直批的状态
		worknoteVO.setIscheck(IPfRetCheckInfo.PASSING == blPassed ? "Y" : IPfRetCheckInfo.NOPASS == blPassed ? "N"
				: "X");
		worknoteVO.setActiontype("APPROVE");
		worknoteVO.setCheckman(strApproveId);
		// 单据类型
		worknoteVO.setPk_billtype(billtype);
		worknoteVO.setWorkflow_type(WorkflowTypeEnum.Approveflow.getIntValue());
		getIPersistenceUpdate().insertVO(null, worknoteVO, null);
	}

	@Override
	public HashMap<String, Object> execBills(AggStapply[] bills, LoginContext context, boolean isRunBackgroundTask)
			throws BusinessException {
		HashMap<String, Object> result = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();// 错误信息
		ArrayList<AggStapply> alTrans = new ArrayList<AggStapply>();// 调配单据
		ArrayList<AggStapply> alDimission = new ArrayList<AggStapply>();// 离职单据
		for (int i = 0; bills != null && i < bills.length; i++) {
			if (TRNConst.BUSITYPE_TRANSITION.equals(bills[i].getParentVO().getAttributeValue(StapplyVO.PK_BILLTYPE))) {
				alTrans.add(bills[i]);
			} else {
				alDimission.add(bills[i]);
			}
		}
		AggregatedValueObject[] retVOs = null;
		if (alTrans.size() > 0) {
			retVOs = NCLocator.getInstance().lookup(IRegmngQueryService.class)
					.validateBudget(alTrans.toArray(new AggStapply[0]), context);
		}

		// 添加一段逻辑,将编制校验没通过的单据,也加入到错误提示中
		for (int i = 0; i < alTrans.size(); i++) {
			if (isExit(retVOs, alTrans.get(i))) {
				continue;
			}
			sb.append(ResHelper.getString("6009tran", "06009tran0166"/*
																	 * @res
																	 * "单据{0}由于编制校验未通过不能成功执行"
																	 */, (String) alTrans.get(i).getParentVO()
					.getAttributeValue(StapplyVO.BILL_CODE)));
		}

		if ((retVOs == null || retVOs.length == 0) && alDimission.size() == 0) {
			// 两部分都没有可执行单据,直接返回
			String msg = sb.length() == 0 ? "" : sb.toString();
			result.put(TRNConst.RESULT_MSG, isRunBackgroundTask ? msg : msg.replaceAll("<br>", '\n' + ""));
			result.put(TRNConst.RESULT_BILLS, null);
			return result;
		}
		// 只对调配单据进行编制校验
		ArrayList<AggStapply> transBill = new ArrayList<AggStapply>();// 调配执行成功的单据
		ArrayList<AggStapply> dimisBill = new ArrayList<AggStapply>();// 离职执行成功的单据
		AggregatedValueObject[] aggs = (AggregatedValueObject[]) ArrayUtils.addAll(retVOs,
				alDimission.toArray(new AggStapply[0]));
		for (int i = 0; i < aggs.length; i++) {
			String billtype = (String) aggs[i].getParentVO().getAttributeValue(StapplyVO.PK_BILLTYPE);
			try {

				if (TRNConst.BUSITYPE_TRANSITION.equals(billtype)) {
					// 调配校验&执行
					Object obj = getRdsService().perfromStaff_RequiresNew((AggStapply) aggs[i], isqueryctrt);
					if (null == obj) {
						continue;
					}
					if (obj instanceof String) {
						transBill.add(queryByPk(aggs[i].getParentVO().getPrimaryKey()));
						continue;
					}
					transBill.add(queryByPk(aggs[i].getParentVO().getPrimaryKey()));
				} else {
					// 离职校验&执行
					Object obj = getRdsService().perfromTurnOver_RequiresNew((AggStapply) aggs[i], isqueryctrt);
					if (null == obj) {
						continue;
					}
					if (obj instanceof String) {
						dimisBill.add(queryByPk(aggs[i].getParentVO().getPrimaryKey()));
						continue;
					}
					dimisBill.add(queryByPk(aggs[i].getParentVO().getPrimaryKey()));
				}

			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				String billcode = (String) aggs[i].getParentVO().getAttributeValue(StapplyVO.BILL_CODE);
				if (StringUtils.isBlank(e.getMessage())) {

					sb.append((i + 1) + ":"
							+ ResHelper.getString("6009tran", "06009tran0167"/*
																			 * @res
																			 * "单据{0}由于如下未知异常[{1}]不能成功执行,具体异常信息请查看日志."
																			 */, billcode, e.getMessage()) /*
																											 * +
																											 * " <br>"
																											 */);
				} else {
					if (e.getMessage().indexOf(billcode) < 0) {
						// 如果异常信息中没有出现单据号,则重组异常信息
						sb.append((i + 1) + ":"
								+ ResHelper.getString("6009tran", "06009tran0167"/*
																				 * @
																				 * res
																				 * "单据{0}由于如下未知异常[{1}]不能成功执行,具体异常信息请查看日志."
																				 */, billcode, e.getMessage()) /*
																												 * +
																												 * " <br>"
																												 */);
					} else {
						sb.append((i + 1) + ":" + e.getMessage() /* + "<br>" */);
					}
				}
				continue;
			}
			try {
				AggStapply agg = queryByPk(aggs[i].getParentVO().getPrimaryKey());
				getRdsService().pushWorkflow_RequiresNew(billtype, agg);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}

		if (dimisBill != null && dimisBill.size() > 0) {
			// 回写结束日期到人员信息子集健保信息和劳健退子集 he
			NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class)
					.finishInsurance(dimisBill.toArray(new AggStapply[0]));
		}

		// 执行完成后,发送通知
		// 1001Z7PSN00000000004
		// 1001Z7PSN00000000005
		// 1)发送调配的通知,按照调配后组织发送
		// key-org value-bill_list
		HashMap<String, ArrayList<AggStapply>> hmTrans = new HashMap<String, ArrayList<AggStapply>>();
		for (AggStapply bill : transBill) {
			String pk_org = (String) bill.getParentVO().getAttributeValue(StapplyVO.PK_HI_ORG);
			if (hmTrans.get(pk_org) == null) {
				hmTrans.put(pk_org, new ArrayList<AggStapply>());
			}
			hmTrans.get(pk_org).add(bill);
		}
		for (String key : hmTrans.keySet()) {
			if (hmTrans.get(key) == null || hmTrans.get(key).size() <= 0) {
				continue;
			}

			// 为调配单据时
			String tempCode = HICommonValue.msgcode_trns;// 调配直批通知消息模板源编码
			HiSendMsgHelper.sendMessage1(tempCode, hmTrans.get(key).toArray(new AggStapply[0]), key);
		}
		// 1)发送离职的通知,按照调配后组织发送
		// key-org value-bill_list
		HashMap<String, ArrayList<AggStapply>> hmDimis = new HashMap<String, ArrayList<AggStapply>>();
		for (AggStapply bill : dimisBill) {
			String pk_org = (String) bill.getParentVO().getAttributeValue(StapplyVO.PK_HI_ORG);
			if (hmDimis.get(pk_org) == null) {
				hmDimis.put(pk_org, new ArrayList<AggStapply>());
			}
			hmDimis.get(pk_org).add(bill);
		}
		for (String key : hmDimis.keySet()) {
			if (hmDimis.get(key) == null || hmDimis.get(key).size() <= 0) {
				continue;
			}

			// 为离职单据时
			String tempCode = HICommonValue.msgcode_dimission;// 离职直批通知消息模板编码
			HiSendMsgHelper.sendMessage1(tempCode, hmDimis.get(key).toArray(new AggStapply[0]), key);
		}
		// end
		String msg = sb.length() == 0 ? "" : sb.toString();
		result.put(TRNConst.RESULT_MSG, isRunBackgroundTask ? msg : msg.replaceAll("<br>", '\n' + ""));
		transBill.addAll(dimisBill);
		result.put(TRNConst.RESULT_BILLS, transBill);
		return result;
	}

	private boolean isExit(AggregatedValueObject[] retVOs, AggStapply aggStapply) throws BusinessException {
		for (int i = 0; retVOs != null && i < retVOs.length; i++) {
			if (aggStapply.getParentVO().getPrimaryKey().equals(retVOs[i].getParentVO().getPrimaryKey())) {
				return true;
			}
		}
		return false;
	}

	private IRdsManageService getRdsService() {
		return NCLocator.getInstance().lookup(IRdsManageService.class);
	}

	@Override
	public HashMap validateApproveType(AggregatedValueObject[] aggvos) throws BusinessException {
		HashMap<Integer, ArrayList<AggregatedValueObject>> map = new HashMap<Integer, ArrayList<AggregatedValueObject>>();
		ArrayList<AggregatedValueObject> directBills = new ArrayList<AggregatedValueObject>();
		ArrayList<AggregatedValueObject> approveBills = new ArrayList<AggregatedValueObject>();
		map.put(HRConstEnum.APPROVE_TYPE_FORCE_DIRECT, directBills);
		map.put(HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW, approveBills);
		for (int i = 0; aggvos != null && i < aggvos.length; i++) {
			IFlowBizItf itf = NCObject.newInstance(aggvos[i]).getBizInterface(IFlowBizItf.class);
			if (isDirectApprove(itf)) {
				map.get(HRConstEnum.APPROVE_TYPE_FORCE_DIRECT).add(aggvos[i]);
			} else {
				map.get(HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW).add(aggvos[i]);
			}
		}
		return map;
	}

	@Override
	public PfProcessBatchRetObject dimissionApproveValidation(AggStapply[] aggvos, LoginContext context,
			String operateCode, String mdOperateCode, String resourceCode) throws BusinessException {
		NCLocator.getInstance().lookup(IHrPf.class).validateApproveType(aggvos);
		return getIHrPf().approveValidation(operateCode, mdOperateCode, resourceCode, aggvos);
	}

	@Override
	public AggregatedValueObject[] queryBillData(LoginContext loginContext, String billType, String strWhere,
			PFQueryParams queryParams, String strOrderBySQL) throws BusinessException {

		if (queryParams == null) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0168")/*
																						 * @
																						 * res
																						 * "常规查询参数为空！"
																						 */);
		}

		if (StringUtils.isBlank(strWhere)) {
			strWhere = " 1=1";
		}

		strWhere += " and " + StapplyVO.PK_BILLTYPE + " = '" + billType + "' ";

		String strNormalSQL = getQueryCondition(billType, queryParams.isApproveSite(), loginContext.getPk_org(),
				queryParams.getBillState());

		if (StringUtils.isBlank(strNormalSQL)) {
			strNormalSQL = " 1=1";
		}

		IFlowBizItf itf = HrPfHelper.getFlowBizItf(AggStapply.class);

		String strApproveDatePeriod = HrPfHelper.getApproveDatePeriod(itf, null, queryParams.getApproveDateParam(),
				queryParams.getBillState());

		String strSQL = strWhere + " and " + strNormalSQL;

		if (!StringUtils.isBlank(strApproveDatePeriod)) {
			strSQL += " and " + strApproveDatePeriod;
		}

		if (!StringUtils.isBlank(strOrderBySQL)) {
			// 添加了组织条件，只能看到选择的人力资源组织的数据 add by yanglt 2014-11-30
			strSQL += " and pk_org = '" + loginContext.getPk_org() + "' " + "order by " + strOrderBySQL;
		}

		return queryByCondition(AggStapply.class, strSQL);
	}

	private String getQueryCondition(String billType, boolean blApproveSite, String pk_org, int iBillStatus)
			throws BusinessException {

		String strQueryCondition = "";

		// 如果是申请节点，只能查看自己填写的单据，以及查询对话框中需要查询的单据状态
		if (!blApproveSite) {
			strQueryCondition = MessageFormat.format(" {0} in ( ''{1}'',''{2}'') and {3}=''{4}'' ",
					StapplyVO.BILLMAKER, PubEnv.getPk_user(), INCSystemUserConst.NC_USER_PK, StapplyVO.PK_ORG, pk_org);

			if (HRConstEnum.ALL_INTEGER != iBillStatus) {
				strQueryCondition += MessageFormat.format(" and {0}={1}", StapplyVO.APPROVE_STATE, iBillStatus);
			}

			strQueryCondition = " (" + strQueryCondition + ")";
		} else {
			// 如果是审批节点，调用待办事物查询
			String strWorkCondition = getWorkCondition(billType, pk_org, iBillStatus);

			if (!StringUtils.isBlank(strWorkCondition)) {
				strQueryCondition = strWorkCondition;
			}
		}

		return strQueryCondition;

	}

	private String getWorkCondition(String strBillType, String strPk_org, int iBillStatus) throws BusinessException {

		String strDealWhere = "";// 待处理或者已处理的状态

		if (HRConstEnum.APPROVE_NOT_DEAL == iBillStatus)// 待处理对应“已提交”与“审批中”
		{
			strDealWhere = MessageFormat.format(" and {0} in({1},{2})", StapplyVO.APPROVE_STATE,
					IPfRetCheckInfo.COMMIT, IPfRetCheckInfo.GOINGON);
		} else if (HRConstEnum.APPROVE_WAIT_EXECUTE == iBillStatus) {
			strDealWhere = MessageFormat.format(" and {0} in({1})", StapplyVO.APPROVE_STATE, IPfRetCheckInfo.PASSING);
		} else if (HRConstEnum.APPROVE_DEALED == iBillStatus)// 已处理对应“审批中”、“已批准”、“未批准”、“执行中”、“已执行”
		{
			strDealWhere = MessageFormat.format(" and {0} in({1},{2},{3},{4},{5})", StapplyVO.APPROVE_STATE,
					IPfRetCheckInfo.GOINGON, IPfRetCheckInfo.PASSING, IPfRetCheckInfo.NOPASS, HRConstEnum.EXECUTING,
					HRConstEnum.EXECUTED);
		} else if (HRConstEnum.APPROVE_WAIT_EXECUTE == iBillStatus) {
			strDealWhere = " and ( ( stapply_mode = " + TRNConst.TRANSMODE_INNER + " and pk_hi_org = '" + strPk_org
					+ "' and approve_state in (" + IPfRetCheckInfo.PASSING + ")) or (stapply_mode = "
					+ TRNConst.TRANSMODE_CROSS_OUT + " and pk_hi_org = '" + strPk_org + "'" + " and approve_state in ("
					+ IPfRetCheckInfo.PASSING + ")) or (stapply_mode = " + TRNConst.TRANSMODE_CROSS_IN
					+ " and pk_hi_org = '" + strPk_org + "' and approve_state in (" + IPfRetCheckInfo.PASSING + "))) ";
		} else if (HRConstEnum.ALL_INTEGER == iBillStatus) // 全部
															// 对应“已提交”、“审批中”、“已批准”、“未批准”、“执行中”、“已执行”
		{
			strDealWhere = MessageFormat.format(" and {0} in({1},{2},{3},{4},{5},{6})", StapplyVO.APPROVE_STATE,
					IPfRetCheckInfo.COMMIT, IPfRetCheckInfo.GOINGON, IPfRetCheckInfo.PASSING, IPfRetCheckInfo.NOPASS,
					HRConstEnum.EXECUTING, HRConstEnum.EXECUTED);
		}

		// 走审批流的单据条件
		String strWorkFlowWhere = NCLocator.getInstance().lookup(IHrPf.class).getBillIdSql(iBillStatus, strBillType);

		if (!StringUtils.isBlank(strWorkFlowWhere)) {
			strWorkFlowWhere = StapplyVO.PK_HI_STAPPLY + " in(" + strWorkFlowWhere + ")";
		}

		// 直批条件
		String strDirectWhere = getDirectWhere(strBillType, strPk_org, iBillStatus);

		// 所有条件的组合，直批 or 审批流
		String strAllWhere = "((" + strDirectWhere + ")";
		strAllWhere += StringUtils.isBlank(strWorkFlowWhere) ? ")" : " or (" + strWorkFlowWhere + "))";
		strAllWhere += strDealWhere;

		return strAllWhere;
	}

	private String getDirectWhere(String strBillType, String strPk_org, int iBillStatus) {
		String strDirectWhere = MessageFormat.format(
				" {0} not in (select pub_wf_instance.billid from pub_wf_instance) ", StapplyVO.PK_HI_STAPPLY);
		if (TRNConst.BUSITYPE_TRANSITION.equals(strBillType)) {
			strDirectWhere += addDirectAppExtraWhereSql(strPk_org, iBillStatus);
		} else {
			strDirectWhere += " and pk_org = '" + strPk_org + "' ";
		}
		return strDirectWhere;
	}

	private String addDirectAppExtraWhereSql(String strPk_org, int iBillStatus) {

		StringBuilder extraSql = new StringBuilder(" ");
		if (HRConstEnum.APPROVE_NOT_DEAL == iBillStatus) {
			// --待处理查询
			// --组织内调配,调出：审批状态为 提交
			// --调入：审批状态为 审批中
			extraSql.append(" and ( ( stapply_mode = " + TRNConst.TRANSMODE_INNER + " and pk_hi_org = '" + strPk_org
					+ "' and approve_state = " + IPfRetCheckInfo.COMMIT + " ) or ( stapply_mode = "
					+ TRNConst.TRANSMODE_CROSS_OUT + " and	( ( pk_old_hi_org ='" + strPk_org + "' and approve_state = "
					+ IPfRetCheckInfo.COMMIT + " ) or (pk_hi_org = '" + strPk_org + "' and approve_state = "
					+ IPfRetCheckInfo.GOINGON + " ) ) ) or ( stapply_mode = " + TRNConst.TRANSMODE_CROSS_IN
					+ " and	( ( pk_hi_org = '" + strPk_org + "' and approve_state = " + IPfRetCheckInfo.COMMIT
					+ " ) or ( pk_old_hi_org = '" + strPk_org + "' and approve_state = " + IPfRetCheckInfo.GOINGON
					+ " ) ) ) )");
		} else if (HRConstEnum.APPROVE_DEALED == iBillStatus) {
			// 已处理查询
			// 组织内调配,调入：审批状态为 批准 未批准,执行中,已执行
			extraSql.append(" and ( ( stapply_mode =" + TRNConst.TRANSMODE_INNER + " and	pk_hi_org='" + strPk_org
					+ "' and approve_state in (" + IPfRetCheckInfo.PASSING + "," + IPfRetCheckInfo.NOPASS + ","
					+ HRConstEnum.EXECUTED + "," + HRConstEnum.EXECUTING + ")) or (stapply_mode = "
					+ TRNConst.TRANSMODE_CROSS_OUT + " and	((pk_old_hi_org ='" + strPk_org + "' and approve_state in ("
					+ IPfRetCheckInfo.PASSING + "," + IPfRetCheckInfo.GOINGON + "," + IPfRetCheckInfo.NOPASS + ","
					+ HRConstEnum.EXECUTING + "," + HRConstEnum.EXECUTED + ") ) or (pk_hi_org='" + strPk_org
					+ "' and	approve_state in (" + IPfRetCheckInfo.PASSING + "," + IPfRetCheckInfo.NOPASS + ","
					+ HRConstEnum.EXECUTED + "," + HRConstEnum.EXECUTING + ") ))) or	(stapply_mode ="
					+ TRNConst.TRANSMODE_CROSS_IN + " and	( (pk_hi_org='" + strPk_org + "' and	approve_state in ("
					+ IPfRetCheckInfo.PASSING + "," + IPfRetCheckInfo.GOINGON + "," + IPfRetCheckInfo.NOPASS + ","
					+ HRConstEnum.EXECUTING + "," + HRConstEnum.EXECUTED + ") ) or (pk_old_hi_org ='" + strPk_org
					+ "' and	approve_state in (" + IPfRetCheckInfo.PASSING + "," + IPfRetCheckInfo.NOPASS + ","
					+ HRConstEnum.EXECUTED + "," + HRConstEnum.EXECUTING + " ) ) ) ) ) ");
		} else if (HRConstEnum.ALL_INTEGER == iBillStatus) {
			extraSql.append(" and ( ( pk_old_hi_org = '" + strPk_org + "' or (pk_hi_org = '" + strPk_org
					+ "' and approve_state in ( " + IPfRetCheckInfo.PASSING + "," + IPfRetCheckInfo.NOPASS + ","
					+ HRConstEnum.EXECUTED + "," + HRConstEnum.EXECUTING + "," + IPfRetCheckInfo.GOINGON
					+ "))) and approve_state in (" + IPfRetCheckInfo.PASSING + "," + IPfRetCheckInfo.NOPASS + ","
					+ HRConstEnum.EXECUTED + "," + HRConstEnum.EXECUTING + "," + IPfRetCheckInfo.COMMIT + ","
					+ IPfRetCheckInfo.GOINGON + " ) ) ");
		}
		return extraSql.toString();
	}

	/**
	 * 判断当前单据是否已上传附件
	 * 
	 * @param bill
	 * @return
	 * @throws BusinessException
	 */
	private boolean isHasFile(StapplyVO bill) throws BusinessException {
		if (bill.getIsneedfile() == null || !bill.getIsneedfile().booleanValue()) {
			// 不用必传附件
			return true;
		}

		IFileSystemService service = NCLocator.getInstance().lookup(IFileSystemService.class);
		NCFileNode node = service.getNCFileNodeTreeAndCreateAsNeed(bill.getPk_hi_stapply(), PubEnv.getPk_user());
		return hasFile(node);
	}

	private boolean hasFile(NCFileNode node) {
		if (node.getChildCount() <= 0) {
			return false;
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			if (!((NCFileNode) node.getChildAt(i)).isFolder()) {
				return true;
			} else if (hasFile((NCFileNode) node.getChildAt(i))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public AggStapply createCrossInBill4RM(RM2TRNLinkData linkData) throws BusinessException {
		AggStapply agg = new AggStapply();
		StapplyVO bill = new StapplyVO();
		boolean isAuto = isAutoGenerateBillCode(TRNConst.BUSITYPE_TRANSITION, PubEnv.getPk_group(),
				linkData.getPk_org());
		try {
			if (isAuto) {
				bill.setBill_code(getIHrBillCode().getBillCode(TRNConst.BUSITYPE_TRANSITION, PubEnv.getPk_group(),
						linkData.getPk_org()));
			} else {
				BillCodeHelper.lockBillCodeRule("hr_auto_billcode" + TRNConst.BUSITYPE_TRANSITION, 100);
				String prefix = "ZD" + TRNConst.BUSITYPE_TRANSITION + PubEnv.getServerDate().toStdString();
				// 不自动生成单据号 /默认规则生成 "单据类型+yyyy-mm-dd+_流水号"
				String flowCode = SQLHelper.getFlowCode(prefix, StapplyVO.BILL_CODE, StapplyVO.class);
				bill.setBill_code(prefix + "_" + getFlowCode(flowCode, 0));
			}

			String[] org = ManagescopeFacade.queryHrOrgsByDeptAndBusiregion(linkData.getPk_dept(),
					ManagescopeBusiregionEnum.psndoc);
			String pk_hrorg = org == null || org.length == 0 ? linkData.getPk_org() : org[0];

			bill.setPk_billtype(TRNConst.BUSITYPE_TRANSITION);
			bill.setPk_org(pk_hrorg);
			bill.setPk_group(PubEnv.getPk_group());
			bill.setPk_trnstype(linkData.getPk_trnstype());
			// 根据异动类型设置流程
			TrnstypeFlowVO[] flow = (TrnstypeFlowVO[]) NCLocator
					.getInstance()
					.lookup(IPersistenceRetrieve.class)
					.retrieveByClause(
							null,
							TrnstypeFlowVO.class,
							" pk_group = '" + PubEnv.getPk_group() + "' and pk_trnstype = '"
									+ linkData.getPk_trnstype() + "'");
			if (flow != null && flow.length > 0) {
				bill.setTranstype(flow[0].getPk_transtype());
				if (flow[0].getPk_transtype() != null) {
					BilltypeVO billtype = (BilltypeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
							.retrieveByPk(null, BilltypeVO.class, flow[0].getPk_transtype());
					bill.setTranstype(billtype.getPk_billtypecode());
				}
			}

			bill.setApprove_state(IPfRetCheckInfo.NOSTATE);
			bill.setBillmaker(INCSystemUserConst.NC_USER_PK);
			bill.setApply_date(PubEnv.getServerLiteralDate());
			bill.setFun_code(TRNConst.NODECODE_TRANSAPPLY);

			String pk_psndoc = linkData.getPk_psndoc();
			String pk_psnjob = NCLocator.getInstance().lookup(IPersonRecordService.class).getPsnjobByPsndoc(pk_psndoc);
			PsnJobVO job = queryByPk(PsnJobVO.class, pk_psnjob);
			bill.setPk_psnjob(job.getPk_psnjob());
			bill.setPk_psndoc(job.getPk_psndoc());
			bill.setPk_psnorg(job.getPk_psnorg());
			bill.setAssgid(job.getAssgid());
			bill.setPk_old_hi_org(job.getPk_hrorg());
			bill.setPk_hi_org(pk_hrorg);
			bill.setPk_old_hrcm_org(HiSQLHelper.getEveryHrorg(job.getPk_psnorg(), job.getAssgid(),
					ManagescopeBusiregionEnum.psnpact));// 原合同管理组织
			bill.setPk_hrcm_org(pk_hrorg);// 新合同管理组织
			bill.setStapply_mode(job.getPk_hrorg().equals(pk_hrorg) ? TRNConst.TRANSMODE_INNER
					: TRNConst.TRANSMODE_CROSS_IN);
			bill.setIshrssbill(UFBoolean.FALSE);
			bill.setIsneedfile(UFBoolean.FALSE);
			bill.setIfsynwork(UFBoolean.TRUE);
			bill.setIfendpart(UFBoolean.FALSE);

			// 设置调配前信息
			for (String attr : job.getAttributeNames()) {
				bill.setAttributeValue("old" + attr, job.getAttributeValue(attr));
			}

			// 设置转正后信息
			// 岗位/职务信息特殊处理
			String[] flds = { StapplyVO.NEWPK_POST, StapplyVO.NEWPK_POSTSERIES, StapplyVO.NEWPK_JOB,
					StapplyVO.NEWPK_JOBGRADE, StapplyVO.NEWPK_JOBRANK, StapplyVO.NEWSERIES };

			TrnTransItemVO[] itemvos = TrnDelegator.getIItemSetQueryService().queryItemSetByOrg(
					TRNConst.TRNSITEM_BEANID, PubEnv.getPk_group(), pk_hrorg, linkData.getPk_trnstype());

			for (int j = 0; itemvos != null && j < itemvos.length; j++) {
				if (itemvos[j].getItemkey().startsWith("old") || ArrayUtils.contains(flds, itemvos[j].getItemkey())) {
					// 前项目或是岗位职务相关项目不处理
					continue;
				}
				if (itemvos[j] != null && itemvos[j].getIsdefault() != null && itemvos[j].getIsdefault().booleanValue()
						&& bill.getAttributeValue(itemvos[j].getItemkey()) == null) {
					bill.setAttributeValue(itemvos[j].getItemkey(),
							job.getAttributeValue(itemvos[j].getItemkey().substring(3)));
				}
			}

			// 处理完之后再根据linkData的pk_dept重新赋一遍pk_org和pk_dept
			DeptVO dept = (DeptVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByPk(null, DeptVO.class, linkData.getPk_dept());
			bill.setNewpk_org(dept.getPk_org());
			bill.setNewpk_dept(dept.getPk_dept());

			// 处理岗位职务信息
			// 没岗位没职务考虑默认的情况
			// 只有在前后部门相同的情况下才能处理岗位相关信息,否则岗位不赋值
			if (bill.getOldpk_dept().equals(bill.getNewpk_dept())) {
				TrnTransItemVO postItem = getItemByItemkey(itemvos, StapplyVO.NEWPK_POST);
				TrnTransItemVO jobItem = getItemByItemkey(itemvos, StapplyVO.NEWPK_JOB);
				if (postItem != null && postItem.getIsdefault() != null && postItem.getIsdefault().booleanValue()) {
					// 如果岗位默认,则岗位职务都按照前项目设置
					bill.setNewpk_post(bill.getOldpk_post());
					bill.setNewpk_postseries(bill.getOldpk_postseries());
					bill.setNewpk_job(bill.getOldpk_job());
					bill.setNewpk_jobrank(bill.getOldpk_jobrank());
					bill.setNewpk_jobgrade(bill.getOldpk_jobgrade());
					bill.setNewseries(bill.getOldseries());
				} else if (jobItem != null && jobItem.getIsdefault() != null && jobItem.getIsdefault().booleanValue()) {
					// 岗位没有默认,职务相关的按照前项目设置
					bill.setNewpk_job(bill.getOldpk_job());
					bill.setNewpk_jobrank(bill.getOldpk_jobrank());
					bill.setNewpk_jobgrade(bill.getOldpk_jobgrade());
					bill.setNewseries(bill.getOldseries());
				}
			}

			agg.setParentVO(bill);
			return insertBill(agg);
		} finally {
			if (!isAuto) {
				BillCodeHelper.unlockBillCodeRule("hr_auto_billcode" + TRNConst.BUSITYPE_TRANSITION);
			}
		}
	}

	@Override
	public String validateExistTrail(String[] psnjobPKs) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		try {
			String cond = "  pk_psndoc in ( select pk_psndoc from hi_psndoc_trial where pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psnjob in ( "
					+ isc.getInSQL(psnjobPKs) + " ) ) and endflag <> 'Y' ) ";
			PsndocVO[] vos = (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, PsndocVO.class, cond);
			if (vos == null || vos.length == 0) {
				return null;
			}
			String name = "";
			for (PsndocVO vo : vos) {
				name += "," + MultiLangHelper.getName(vo);
			}
			if (StringUtils.isBlank(name)) {
				return null;
			}
			return name.substring(1);
		} finally {
			isc.clear();
		}
	}

	@Override
	public HashMap<String, String> getPowerItem(String pk_psnjob, boolean isTransOut) throws BusinessException {
		HashMap<String, String> hm = new HashMap<String, String>();
		PsnJobVO job = this.queryByPk(PsnJobVO.class, pk_psnjob);

		String opCode = isTransOut ? HICommonValue.OPERATIONCODE_TRANSDEFAULT : IRefConst.DATAPOWEROPERATION_CODE;

		// 组织
		String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, opCode,
				"org_orgs");
		powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
		int count = NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.getCountByCondition("org_orgs", powerSql + " and org_orgs.pk_org = '" + job.getPk_org() + "' ");
		hm.put(PsnJobVO.PK_ORG, count <= 0 ? null : job.getPk_org());

		// 部门
		powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, opCode, "org_dept");
		powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
		count = NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.getCountByCondition("org_dept", powerSql + " and org_dept.pk_dept = '" + job.getPk_dept() + "' ");
		hm.put(PsnJobVO.PK_DEPT, count <= 0 ? null : job.getPk_dept());

		// 人员类别
		powerSql = HiSQLHelper
				.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL, opCode, "bd_psncl");
		powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
		count = NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.getCountByCondition("bd_psncl", powerSql + " and bd_psncl.pk_psncl = '" + job.getPk_psncl() + "' ");
		hm.put(PsnJobVO.PK_PSNCL, count <= 0 ? null : job.getPk_psncl());

		return hm;
	}

	@Override
	public HashMap<String, Object> manualExecBills(AggStapply[] bills, LoginContext context, UFLiteralDate effectDate)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(bills)) {
			for (int i = 0; i < bills.length; i++) {
				bills[i].getParentVO().setAttributeValue(StapplyVO.EFFECTDATE, effectDate);
			}
		}
		HashMap<String, Object> result = execBills(bills, context, true);
		return result;
	}

	private class MapPsnProcessor extends BaseProcessor {

		private static final long serialVersionUID = 4148546217899305306L;

		@Override
		public Map<String, String> processResultSet(ResultSet rs) throws SQLException {
			Map<String, String> resultMap = new HashMap<String, String>();
			while (rs.next()) {
				String pk_psndoc = rs.getString("pk_psndoc");
				String name = rs.getString("psnname");
				resultMap.put(pk_psndoc, name);
			}
			return resultMap;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryPsnNameByPKPsndoc(List<String> psndocPKLits) throws BusinessException {
		if (psndocPKLits.isEmpty()) {
			return null;
		}
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(psndocPKLits.toArray(new String[0]));
		String sql = "select pk_psndoc, " + SQLHelper.getMultiLangNameColumn("name") + " psnname " + "from bd_psndoc "
				+ "where pk_psndoc in (" + inSql + ")";
		return (Map<String, String>) NCLocator.getInstance().lookup(IPersistenceHome.class)
				.executeQuery(sql, new MapPsnProcessor());

	}
}
