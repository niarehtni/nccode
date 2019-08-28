package nc.ui.trn.transmgt.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hi.seqcontrol.ISeqcontrolManageService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.trn.IItemSetAdapter;
import nc.itf.trn.transmng.ITransmngQueryService;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hi.ref.PsnjobSeqcontrolTreeModel;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.IBillItem;
import nc.ui.trn.utils.RefUtils;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.seqcontrol.SeqcontrolSetVO;
import nc.vo.hi.trnstype.TrnstypeFlowVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.om.job.JobGradeVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.joblevelsys.JobLevelVO;
import nc.vo.om.post.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.transmng.StapplyVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 变动卡片界面基础类
 */
public class TrnBillFormEditor extends HrBillFormEditor implements
		BillCardBeforeEditListener {

	private static final long serialVersionUID = 1L;

	// 默认值设置接口
	private IExceptionHandler defaultExceptionHandler = null;

	private SimpleDocServiceTemplate service = null;

	private String[] psnjobPKs;

	@Override
	public void afterEdit(BillEditEvent e) {
		try {
			if (StapplyVO.PK_PSNJOB.equals(e.getKey())) {
				// 调配离职人员
				setPersonInfo();
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				// 调出时清空调配后组织
				getRelAndEnd(
						(String) getBillCardPanel().getHeadItem(
								StapplyVO.PK_HRCM_ORG).getValueObject(),
						(Integer) item.getValueObject());// 选择不同字段时处理解除和终止字段---参数其实为新合同管理组织，目前等于调配后人事组织，后面考虑是否修改
			} else if (StapplyVO.NEWPK_ORG.equals(e.getKey())) {
				// 任职组织,组织改后所有都要清空
				setItemValueAndEnable(StapplyVO.NEWPK_DEPT, null, true);// 部门
				setItemValueAndEnable(StapplyVO.NEWPK_POST, null, true);// 岗位
				setItemValueAndEnable(StapplyVO.NEWPK_JOB, null, true);// 职务
				setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, false);// 职级
				setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// 职等
				setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null, true);// 岗位序列
				setItemValueAndEnable(StapplyVO.NEWSERIES, null, true);// 职务类别
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				if (item != null
						&& (Integer) item.getValueObject() == TRNConst.TRANSMODE_CROSS_OUT)// &&
																							// getModel().isApproveSite()
				{
					// 调出时清空调配后组织
					setItemValueAndEnable(StapplyVO.PK_HI_ORG, null, true);
					setItemValueAndEnable(StapplyVO.PK_HRCM_ORG, null, true);
				}
			} else if (StapplyVO.NEWPK_DEPT.equals(e.getKey())) {
				setItemValueAndEnable(StapplyVO.NEWPK_POST, null, true);// 岗位
				setItemValueAndEnable(StapplyVO.NEWPK_JOB, null, true);// 职务
				setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, false);// 职级
				setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// 职等
				setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null, true);// 岗位序列
				setItemValueAndEnable(StapplyVO.NEWSERIES, null, true);// 职务类别
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				if (item != null
						&& (Integer) item.getValueObject() == TRNConst.TRANSMODE_CROSS_OUT)// &&
																							// !getModel().isApproveSite()
				{
					// 调出时清空调配后组织
					String pk_dept = (String) getBillCardPanel().getHeadItem(
							StapplyVO.NEWPK_DEPT).getValueObject();
					String pk_org = HiSQLHelper.getHrorgBydept(pk_dept);// 得到的是---调配后人事组织
					setItemValueAndEnable(StapplyVO.PK_HI_ORG, pk_org, true);

					// 根据部门主键查询该部门的合同管理的HR组织
					String pk_cmorg = HiSQLHelper.getEveryHrorgBydept(pk_dept,
							ManagescopeBusiregionEnum.psnpact);
					// 此处的新合同管理组织是否可编辑待定？？？
					setItemValueAndEnable(StapplyVO.PK_HRCM_ORG, pk_cmorg, true);
					getRelAndEnd(pk_org, (Integer) item.getValueObject());// 选择不同字段时处理解除和终止字段---参数其实为新合同管理组织，目前等于调配后人事组织，后面考虑是否修改
				}
			} else if (StapplyVO.NEWPK_POST.equals(e.getKey())) {
				// 岗位
				String pk_post = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_POST).getValueObject();
				PostVO post = pk_post == null ? null : getService().queryByPk(
						PostVO.class, pk_post, true);
				if (post != null) {
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES,
							post.getPk_postseries(), false);// 岗位序列
					setItemValueAndEnable(StapplyVO.NEWPK_JOB,
							post.getPk_job(), true);// 职务
					JobVO jobVO = post.getPk_job() == null ? null
							: getService().queryByPk(JobVO.class,
									post.getPk_job());
					boolean isJobTypeSeted = false;
					if (jobVO != null) {
						setItemValueAndEnable(StapplyVO.NEWSERIES,
								jobVO.getPk_jobtype(), false);// 职务类别
						isJobTypeSeted = true;
					}
					if (post.getEmployment() != null) {
						setItemValueAndEnable(StapplyVO.NEWOCCUPATION,
								post.getEmployment(), true);// 职业
					}
					if (post.getWorktype() != null) {
						setItemValueAndEnable(StapplyVO.NEWWORKTYPE,
								post.getWorktype(), true);// 工种
					}

					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = NCLocator
							.getInstance()
							.lookup(IPsndocQryService.class)
							.getDefaultLevelRank(null, null, null, pk_post,
									null);
					if (!resultMap.isEmpty()) {
						defaultlevel = resultMap.get("defaultlevel");
						defaultrank = resultMap.get("defaultrank");
					}
					if (!isJobTypeSeted) {
						String newseries = (String) getBillCardPanel()
								.getHeadItem(StapplyVO.NEWSERIES)
								.getValueObject();
						setItemValueAndEnable(StapplyVO.NEWSERIES, newseries,
								false);// 职务类别
					}
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE,
							defaultlevel, true);// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// 职等
				} else {
					setItemValueAndEnable(StapplyVO.NEWSERIES, null, true);// 职务类别
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null,
							true);// 岗位序列
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, false);// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// 职等
					setItemValueAndEnable(StapplyVO.NEWPK_JOB, null, true);// 职务
				}
				if (post == null) {
					afterEdit(new BillEditEvent(getBillCardPanel().getHeadItem(
							StapplyVO.NEWPK_JOB).getComponent(), null,
							StapplyVO.NEWPK_JOB));
				}
			} else if (StapplyVO.NEWPK_JOB.equals(e.getKey())) {
				// 职务
				String pk_job = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_JOB).getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_POST).getValueObject();
				JobVO job = pk_job == null ? null : getService().queryByPk(
						JobVO.class, pk_job, true);
				if (job != null) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = NCLocator
							.getInstance()
							.lookup(IPsndocQryService.class)
							.getDefaultLevelRank(null, pk_job, null, pk_post,
									null);
					if (!resultMap.isEmpty()) {
						defaultlevel = resultMap.get("defaultlevel");
						defaultrank = resultMap.get("defaultrank");
					}

					setItemValueAndEnable(StapplyVO.NEWSERIES,
							job.getPk_jobtype(), true);// 职务类别
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE,
							defaultlevel, true);// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// 职等
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null,
							false);// 岗位序列
				} else {
					setItemValueAndEnable(StapplyVO.NEWSERIES, null,
							(StringUtils.isNotBlank(pk_post) ? false : true));// 职务类别
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null,
							true);// 岗位序列
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null,
							(StringUtils.isBlank(pk_post) ? false : true));// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// 职等
				}
			} else if (StapplyVO.NEWPK_JOBGRADE.equals(e.getKey())) {
				// 职级
				String pk_jobgrage = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_JOBGRADE).getValueObject();
				JobLevelVO jobgrade = pk_jobgrage == null ? null : getService()
						.queryByPk(JobLevelVO.class, pk_jobgrage, true);
				if (jobgrade != null) {
					String pk_postseries = (String) getBillCardPanel()
							.getHeadItem(StapplyVO.NEWPK_POSTSERIES)
							.getValueObject();
					String pk_job = (String) getBillCardPanel().getHeadItem(
							StapplyVO.NEWPK_JOB).getValueObject();
					String pk_post = (String) getBillCardPanel().getHeadItem(
							StapplyVO.NEWPK_POST).getValueObject();
					String series = (String) getBillCardPanel().getHeadItem(
							StapplyVO.NEWSERIES).getValueObject();
					String defaultrank = "";
					Map<String, String> resultMap = NCLocator
							.getInstance()
							.lookup(IPsndocQryService.class)
							.getDefaultLevelRank(series, pk_job, pk_postseries,
									pk_post, pk_jobgrage);
					if (!resultMap.isEmpty()) {
						defaultrank = resultMap.get("defaultrank");
					}
					// 职级带出职等后职等不能编辑
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// 职等
				} else {
					// 职级清空后,若职务关联了职等,则用职务上的职等
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// 职等
				}
			} else if (StapplyVO.NEWSERIES.equals(e.getKey())) {
				// 职务类别
				String series = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWSERIES).getValueObject();
				String pk_job = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_JOB).getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_POST).getValueObject();
				if (StringUtils.isBlank(pk_job)
						&& StringUtils.isNotBlank(series)) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = NCLocator
							.getInstance()
							.lookup(IPsndocQryService.class)
							.getDefaultLevelRank(series, pk_job, null, pk_post,
									null);
					if (!resultMap.isEmpty()) {
						defaultlevel = resultMap.get("defaultlevel");
						defaultrank = resultMap.get("defaultrank");
					}

					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE,
							defaultlevel, true);// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// 职等
				} else if (StringUtils.isBlank(pk_job)
						&& StringUtils.isBlank(series)) {
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, true);// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// 职等
				}
			} else if (StapplyVO.NEWPK_POSTSERIES.equals(e.getKey())) {
				// 岗位序列
				String pk_postseries = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_POSTSERIES).getValueObject();
				String pk_job = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_JOB).getValueObject();
				String pk_post = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_POST).getValueObject();
				String series = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWSERIES).getValueObject();
				if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series)
						&& StringUtils.isBlank(pk_post)
						&& StringUtils.isNotBlank(pk_postseries)) {
					String defaultlevel = "";
					String defaultrank = "";
					Map<String, String> resultMap = NCLocator
							.getInstance()
							.lookup(IPsndocQryService.class)
							.getDefaultLevelRank(series, pk_job, pk_postseries,
									pk_post, null);
					if (!resultMap.isEmpty()) {
						defaultlevel = resultMap.get("defaultlevel");
						defaultrank = resultMap.get("defaultrank");
					}

					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE,
							defaultlevel, true);// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// 职等
				} else if (StringUtils.isBlank(pk_job)
						&& StringUtils.isBlank(series)
						&& StringUtils.isBlank(pk_post)
						&& StringUtils.isBlank(pk_postseries)) {
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, true);// 职级
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// 职等
				}
			} else if (StapplyVO.TRANSTYPEID.equals(e.getKey())) {
				// 单据类型变化 同步ID
				UIRefPane ref = (UIRefPane) getBillCardPanel().getHeadItem(
						StapplyVO.TRANSTYPEID).getComponent();
				setItemValueAndEnable(StapplyVO.TRANSTYPE, ref.getRefCode(),
						false);
			} else if (StapplyVO.PK_HI_ORG.equals(e.getKey())) {// 调配后人事组织
				String pk_hi_org = (String) getBillCardPanel().getHeadItem(
						StapplyVO.PK_HI_ORG).getValueObject();
				setItemValueAndEnable(StapplyVO.PK_HRCM_ORG, pk_hi_org, true);
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				getRelAndEnd(pk_hi_org, (Integer) item.getValueObject());// 选择不同字段时处理解除和终止字段---参数其实为新合同管理组织，目前等于调配后人事组织，后面考虑是否修改
			} else if (StapplyVO.EFFECTDATE.equals(e.getKey()))// 生效日期
			{
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				// 调出时清空调配后组织
				getRelAndEnd(
						(String) getBillCardPanel().getHeadItem(
								StapplyVO.PK_HRCM_ORG).getValueObject(),
						(Integer) item.getValueObject());// 选择不同字段时处理解除和终止字段---参数其实为新合同管理组织，目前等于调配后人事组织，后面考虑是否修改
			} else if (StapplyVO.PK_HRCM_ORG.equals(e.getKey())) {
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				String pk_hrcm_org = (String) getBillCardPanel().getHeadItem(
						StapplyVO.PK_HRCM_ORG).getValueObject();
				getRelAndEnd(pk_hrcm_org, (Integer) item.getValueObject());
			}

			String pk_jobType = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWSERIES).getValueObject();// 职务类别
			String pk_job = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_JOB).getValueObject();// 职务
			String pk_postSeries = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POSTSERIES).getValueObject();// 岗位序列
			String pk_post = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POST).getValueObject();// 岗位
			if (StringUtils.isBlank(pk_jobType)
					&& StringUtils.isBlank(pk_postSeries)
					&& StringUtils.isBlank(pk_job)
					&& StringUtils.isBlank(pk_post)) {
				setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, false);
			} else {
				getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE)
						.setEnabled(true);
			}

		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
		}
		// 执行显示公式
		getBillCardPanel().execHeadTailLoadFormulas();
	}

	// 选择不同字段时处理解除和终止字段---参数其实为新合同管理组织，目前等于调配后人事组织，后面考虑是否修改
	public void getRelAndEnd(String pk_hrcm_org, Integer inte)
			throws BusinessException {
		CtrtVO ctrtvo = getCtrt();
		if (ctrtvo == null) {
			return;
		}

		if (TRNConst.TRANSMODE_INNER == inte) {
			setItemValueAndEnable(StapplyVO.ISRELEASE, null, true);// 解除
			setItemValueAndEnable(StapplyVO.ISEND, null, true);// 终止
			return;
		}

		if (TRNConst.TRANSMODE_CROSS_OUT != inte) {// 不是调出情况
			getRelEffectDate(ctrtvo);
			return;
		}

		// 调出时清空调配后组织
		String pk_old_hrcm_org = (String) getBillCardPanel().getHeadItem(
				StapplyVO.PK_OLD_HRCM_ORG).getValueObject();
		if (StringUtils.isBlank(pk_old_hrcm_org)
				|| StringUtils.isBlank(pk_hrcm_org)) {
			// 合同前组织肯定不会为空，那只有合同后组织为空的可能
			setItemValueAndEnable(StapplyVO.ISRELEASE, null, true);// 解除
			setItemValueAndEnable(StapplyVO.ISEND, null, true);// 终止
			return;
		}
		getRelEffectDate(ctrtvo);
	}

	public CtrtVO getCtrt() throws BusinessException {
		String pk_psnorg = (String) getBillCardPanel().getHeadItem(
				StapplyVO.PK_PSNORG).getValueObject();
		if (StringUtils.isBlank(pk_psnorg)) {
			return null;
		}
		CtrtVO ctrtvo = getQueryCtrtVO(pk_psnorg);
		if (ctrtvo == null) {// 最新的合同记录不是签订/变更/续签三个状态（未签订合同或者合同已经解除/终止），不需要勾选和编辑
			setItemValueAndEnable(StapplyVO.ISRELEASE, null, false);// 解除
			setItemValueAndEnable(StapplyVO.ISEND, null, false);// 终止
			return null;
		}
		return ctrtvo;
	}

	// 根据生效日期判断是否解除/终止，此功能各种情况都得使用到
	public void getRelEffectDate(CtrtVO ctrtvo) throws BusinessException {
		UFLiteralDate effectdate = (UFLiteralDate) getBillCardPanel()
				.getHeadItem(StapplyVO.EFFECTDATE).getValueObject();
		if (effectdate != null) {
			String isshow = getRelOrEndShow(ctrtvo, effectdate);
			setItemValueAndEnable(StapplyVO.ISRELEASE,
					StapplyVO.ISRELEASE.equals(isshow) ? UFBoolean.TRUE
							: UFBoolean.FALSE, true);// 解除
			setItemValueAndEnable(StapplyVO.ISEND,
					StapplyVO.ISEND.equals(isshow) ? UFBoolean.TRUE
							: UFBoolean.FALSE, true);// 终止
		} else {
			// 当生效日期为空时，就不必要去判断这个人是否有合同或者应该解除或者终止合同
			boolean isrelease = (Boolean) getBillCardPanel().getHeadItem(
					StapplyVO.ISRELEASE).getValueObject();
			setItemValueAndEnable(StapplyVO.ISRELEASE, isrelease ? isrelease
					: null, true);// 解除
			boolean isend = (Boolean) getBillCardPanel().getHeadItem(
					StapplyVO.ISEND).getValueObject();
			setItemValueAndEnable(StapplyVO.ISEND, isend ? isend : null, true);// 终止
		}
	}

	// 查询出人员是否有合同数据，以判断解除和终止的使用
	public CtrtVO getQueryCtrtVO(String pk_psnorg) throws BusinessException {
		CtrtVO ctrtvo = null;
		String condition = " pk_psnorg = '"
				+ pk_psnorg
				+ "' and lastflag = 'Y' and isrefer = 'Y' and conttype in (1,2,3) ";
		CtrtVO[] ctrtvos = (CtrtVO[]) NCLocator.getInstance()
				.lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, CtrtVO.class, condition);
		if (!ArrayUtils.isEmpty(ctrtvos)) {
			ctrtvo = ctrtvos[0];
		}
		return ctrtvo;
	}

	// 查询出人员是否有合同数据，以判断解除和终止的使用
	public String getRelOrEndShow(CtrtVO ctrtvo, UFLiteralDate effectdate)
			throws BusinessException {
		String isshow = null;
		if (ctrtvo != null) {
			UFLiteralDate enddate = ctrtvo.getEnddate();
			if (effectdate.before(enddate))// 生效日期早于现有的合同的结束日期，需要解除合同
			{
				isshow = StapplyVO.ISRELEASE;
			} else
			// 生效日期等于或者晚于现有的合同的结束日期，需要终止合同
			{
				isshow = StapplyVO.ISEND;
			}
		}
		return isshow;
	}

	// 查询当前逐级管控模式
	public boolean getQuerySeq() throws BusinessException {
		boolean isset = false;// 未设置
		SeqcontrolSetVO seqvo = NCLocator.getInstance()
				.lookup(ISeqcontrolManageService.class).getQuerySet();
		if (seqvo != null) {
			String code = seqvo.getCode();
			if (StringUtils.isNotBlank(code)) {
				isset = "0".equals(code) ? false : true;
			}
		} else {
			return isset;
		}
		return isset;
	}

	public boolean beforeEdit(BillItemEvent e) {

		// 不使用发送事件方式，太复杂
		if (StapplyVO.TRANSTYPEID.equals(e.getItem().getKey())) {
			// 审批流
			((UIRefPane) e.getItem().getComponent()).getRefModel()
					.setUseDataPower(false);
			String where = " and ( parentbilltype = '"
					+ getModel().getBillType() + "' and pk_group = '"
					+ getModel().getContext().getPk_group() + "' )";
			String powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
					HICommonValue.RESOUCECODE_TRANSTYPE,
					IRefConst.DATAPOWEROPERATION_CODE, "bd_billtype");
			if (!StringUtils.isBlank(powerSql)) {
				where += " and " + powerSql;
			}
			((UIRefPane) e.getItem().getComponent()).getRefModel()
					.addWherePart(where);

		} else if (StapplyVO.BUSINESS_TYPE.equals(e.getItem().getKey())) {
			// 业务流程,暂时用'人力资源'下的,过滤掉未启用的
			String where = " and busiprop = 6  and ( isnull(pk_org,'~') = '~' or pk_org = '"
					+ getModel().getContext().getPk_org()
					+ "') and validity =1 and primarybilltype like '"
					+ getModel().getBillType() + "%'";
			((UIRefPane) e.getItem().getComponent()).getRefModel()
					.addWherePart(where);
		} else if (StapplyVO.PK_PSNJOB.equals(e.getItem().getKey())) {
			// 调配/离职人员
			// 在职条件
			String inJobSql = " and hi_psnjob.pk_psnjob in "
					+ HiSQLHelper.getInJobSQL(true);
			UIRefPane psnRef = (UIRefPane) e.getItem().getComponent();
			String condition = "";
			String powerSql = null;
			boolean iscrossin = getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_IN ? true
					: false;// 判断调配方式是否是调入,调入时为true,组织内和调出为false
			if (iscrossin)// 调入
			{
				psnRef.getRefModel().setUseDataPower(Boolean.FALSE);
				// 调入时,参照集团内其他HR组织的人员,使用"调入人员的特殊场景"
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_6007PSNJOB,
						HICommonValue.OPERATIONCODE_TRANSDEFAULT, "hi_psnjob");
				condition = " and hi_psnjob.pk_group = '"
						+ getModel().getContext().getPk_group()
						+ "' and hi_psnjob.pk_hrorg <> '"
						+ getModel().getContext().getPk_org()
						+ "' and hi_psnjob.pk_org <> '"
						+ getModel().getContext().getPk_org()
						+ "' and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y' "
						+ inJobSql
						+ (StringUtils.isBlank(powerSql) ? ""
								: (" and " + powerSql));
			} else
			// 组织内和调出
			{
				// 其他情况,参照本HR组织的人员,使用通用场景
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_6007PSNJOB,
						IRefConst.DATAPOWEROPERATION_CODE, "hi_psnjob");
				condition = inJobSql
						+ (StringUtils.isBlank(powerSql) ? ""
								: (" and " + powerSql));
			}

			try {
				// 没有启用干部管理或“HR组织可同时维护干部人员的信息”=“否”，不显示干部信息
				if (!NCLocator.getInstance().lookup(IPsndocQryService.class)
						.getValueOFParaDeployCadre(getModel().getContext())) {
					condition += " and (bd_psndoc.iscadre = 'N' or bd_psndoc.iscadre is null) ";
				}

				boolean isset = getQuerySeq();
				if (isset)// 设置了逐级管控才走这里面
				{
					if (!iscrossin)// 组织内和调出
					{
						((PsnjobSeqcontrolTreeModel) psnRef.getRefModel())
								.setOrgSeqcontrol("'"
										+ getModel().getContext().getPk_org()
										+ "'");
						String pkSql = NCLocator
								.getInstance()
								.lookup(IPsndocService.class)
								.queryControlSql("@@@@Z710000000006M7F",
										getModel().getContext().getPk_org(),
										false);
						if (!StringUtils.isEmpty(pkSql)) {
							((PsnjobSeqcontrolTreeModel) psnRef.getRefModel())
									.setOrgSeqcontrol(pkSql);
						}
					} else
					// 调入
					{
						((PsnjobSeqcontrolTreeModel) psnRef.getRefModel())
								.setOrgSeqcontrol("'"
										+ getModel().getContext().getPk_org()
										+ "'");
						String pkSql = NCLocator
								.getInstance()
								.lookup(IPsndocService.class)
								.queryControlSql("@@@@Z710000000006M4C",
										getModel().getContext().getPk_org(),
										false);
						if (!StringUtils.isEmpty(pkSql)) {
							((PsnjobSeqcontrolTreeModel) psnRef.getRefModel())
									.setOrgSeqcontrol(pkSql);
						}
					}
				}
			} catch (BusinessException ex) {
				Logger.error(ex.getMessage(), ex);
			}

			psnRef.getRefModel().addWherePart(condition);
			psnRef.getRefModel().reset();
		} else if (StapplyVO.NEWPK_ORG.equals(e.getItem().getKey())) {
			// 组织
			UIRefPane orgRef = (UIRefPane) e.getItem().getComponent();
			// 不受UAP组织权限控制，受HR组织权限控制
			orgRef.getRefModel().setUseDataPower(Boolean.FALSE);
			String powerSql = "";
			String whereSql = "";
			if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				// 只有在调配申请审批节点并且调配方式为调出时使用特殊场景
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_ORG,
						HICommonValue.OPERATIONCODE_TRANSDEFAULT, "org_orgs");
			} else {
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_ORG,
						IRefConst.DATAPOWEROPERATION_CODE, "org_orgs");
				if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_IN
						&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
								.getBillType())) {
					// 只有在调配申请审批节点并且调配方式为调入时使用特殊场景

				}
			}

			if (!StringUtils.isBlank(powerSql)) {
				whereSql = " and pk_adminorg in ( select pk_org from org_orgs where "
						+ powerSql
						+ ") and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)";
			} else {
				whereSql = " and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)";
			}

			// 逐级管控（调配/离职申请——调配/离职后信息-组织）
			String funcode = "";
			if (getModel().getStapply_mode() == TRNConst.TRANSMODE_INNER
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				funcode = "@@@@Z710000000006M2A";
			} else if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				funcode = "@@@@Z710000000006M3B";
			} else if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_IN
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				funcode = "@@@@Z710000000006M4C";
			} else if (TRNConst.BUSITYPE_DIMISSION.equals(getModel()
					.getBillType())) {
				funcode = "@@@@Z710000000006M7F";
			}

			try {
				String gkSql = NCLocator
						.getInstance()
						.lookup(IPsndocService.class)
						.queryControlSql(funcode,
								getModel().getContext().getPk_org(), true);
				if (!StringUtils.isEmpty(gkSql)) {
					whereSql += " and org_adminorg.pk_adminorg in ( " + gkSql
							+ ")";
				}
				orgRef.getRefModel().addWherePart(whereSql);
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
			}
		} else if (StapplyVO.NEWPK_DEPT.equals(e.getItem().getKey())) {
			// 部门
			UIRefPane deptRef = (UIRefPane) e.getItem().getComponent();
			// 不受UAP部门权限控制，受HR部门权限控制
			deptRef.getRefModel().setUseDataPower(Boolean.FALSE);
			String pk_org = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_ORG).getValueObject();
			deptRef.setPk_org(pk_org);
			String cond = " and hrcanceled = 'N' and depttype <> 1 ";
			String powerSql = "";
			/*
			 * if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
			 * && TRNConst.BUSITYPE_TRANSITION.equals(getModel().getBillType()))
			 * { // 只有在调配申请审批节点并且调配方式为调出时使用特殊场景 powerSql =
			 * HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
			 * HICommonValue.RESOUCECODE_DEPT,
			 * HICommonValue.OPERATIONCODE_TRANSDEFAULT, "org_dept");//
			 * 数据权限中的使用场景为“变动后组织/部门引用” } else { powerSql =
			 * HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
			 * HICommonValue.RESOUCECODE_DEPT,
			 * IRefConst.DATAPOWEROPERATION_CODE, "org_dept");//
			 * 数据权限中的使用场景为“通用引用” }
			 */
			if (!StringUtils.isBlank(powerSql)) {
				cond += " and " + powerSql;
			}
			deptRef.getRefModel().addWherePart(cond);
		} else if (StapplyVO.NEWPK_PSNCL.equals(e.getItem().getKey())) {
			// 人员类别
			// 人员类别,转正使用通用的人员类别场景
			UIRefPane psnclRef = (UIRefPane) e.getItem().getComponent();
			String powerSql = "";
			if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				// 只有在调配申请审批节点并且调配方式为调出时使用特殊场景
				psnclRef.getRefModel().setDataPowerOperation_code(
						HICommonValue.OPERATIONCODE_TRANSDEFAULT);
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_PSNCL,
						HICommonValue.OPERATIONCODE_TRANSDEFAULT, "bd_psncl");
			} else {
				psnclRef.getRefModel().setDataPowerOperation_code(
						IRefConst.DATAPOWEROPERATION_CODE);
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_PSNCL,
						IRefConst.DATAPOWEROPERATION_CODE, "bd_psncl");
			}
			if (!StringUtils.isBlank(powerSql)) {
				psnclRef.getRefModel().addWherePart(" and " + powerSql);
			}
		} else if (StapplyVO.NEWPK_POST.equals(e.getItem().getKey())) {
			// 岗位-根据组织、部门过滤
			UIRefPane postRef = (UIRefPane) e.getItem().getComponent();
			String pk_org = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_ORG).getValueObject();
			String pk_dept = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_DEPT).getValueObject();
			PostRefModel postModel = (PostRefModel) postRef.getRefModel();
			postModel.setPk_org(pk_org);
			postModel.setPkdept(pk_dept);
			String cond = " and ( "
					+ SQLHelper.getNullSql(PostVO.TABLENAME + ".hrcanceled")
					+ " or " + PostVO.TABLENAME + ".hrcanceled = 'N' ) ";

			String powerSql = "";
			if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				// 只有在调配申请审批节点并且调配方式为调出时使用特殊场景 ,岗位权限“变动后部门引用”场景控制
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_DEPT,
						HICommonValue.OPERATIONCODE_TRANSDEFAULT, "org_dept");
			} else {
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_DEPT,
						IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
			}

			if (!StringUtils.isBlank(powerSql)) {
				cond += " and om_post.pk_dept in ( select pk_dept from org_dept where  "
						+ powerSql + " ) ";
			}
			postModel.addWherePart(cond);
		} else if (StapplyVO.NEWPK_JOB.equals(e.getItem().getKey())) {
			// 职务
			String pk_org = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_ORG).getValueObject();
			UIRefPane jobRef = (UIRefPane) e.getItem().getComponent();
			if (pk_org != null) {
				jobRef.setPk_org(pk_org);
			} else {
				jobRef.setPk_org(getModel().getContext().getPk_group());
			}
		} else if (StapplyVO.NEWPK_JOBGRADE.equals(e.getItem().getKey())) {
			// 职级
			String pk_postseries = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POSTSERIES).getValueObject();
			String pk_job = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_JOB).getValueObject();
			String pk_post = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POST).getValueObject();
			String series = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWSERIES).getValueObject();
			BillItem item = (BillItem) e.getSource();
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map<String, Object> resultMap = null;
				try {
					resultMap = NCLocator
							.getInstance()
							.lookup(IPsndocQryService.class)
							.getLevelRankCondition(series, pk_job,
									pk_postseries, pk_post);
				} catch (BusinessException e1) {
					Logger.error(e1.getMessage(), e1);
				}

				if (!resultMap.isEmpty()) {
					filterType = (FilterTypeEnum) resultMap.get("filterType");
					gradeSource = (String) resultMap.get("gradeSource");
				}

				((JobGradeRefModel2) ((UIRefPane) item.getComponent())
						.getRefModel()).setPk_filtertype(gradeSource,
						filterType);
			}
		} else if (StapplyVO.NEWPK_JOBRANK.equals(e.getItem().getKey())) {
			// 职等
			BillItem item = e.getItem();
			String pk_jobrank = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_JOBRANK).getValueObject();
			String pk_jobgrade = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_JOBGRADE).getValueObject();
			if (StringUtils.isBlank(pk_jobrank)) {
				((JobRankRefModel) ((UIRefPane) item.getComponent())
						.getRefModel()).setPk_filtertype(null, null);
				// ((JobRankRefModel) ((UIRefPane)
				// item.getComponent()).getRefModel()).setPk_joblevel("");
				return true;
			}

			String pk_postseries = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POSTSERIES).getValueObject();
			String pk_job = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_JOB).getValueObject();
			String pk_post = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POST).getValueObject();
			String series = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWSERIES).getValueObject();
			if (item != null) {
				FilterTypeEnum filterType = null;
				String gradeSource = "";
				Map<String, Object> resultMap = null;
				try {
					resultMap = NCLocator
							.getInstance()
							.lookup(IPsndocQryService.class)
							.getLevelRankCondition(series, pk_job,
									pk_postseries, pk_post);
				} catch (BusinessException e1) {
					Logger.error(e1.getMessage(), e1);
				}

				if (!resultMap.isEmpty()) {
					filterType = (FilterTypeEnum) resultMap.get("filterType");
					gradeSource = (String) resultMap.get("gradeSource");
				}
				((JobRankRefModel) ((UIRefPane) item.getComponent())
						.getRefModel()).setPk_joblevel(pk_jobgrade);
				((JobRankRefModel) ((UIRefPane) item.getComponent())
						.getRefModel()).setPk_filtertype(gradeSource,
						filterType);
			}
		} else {
			// 其他根据调配后人事组织过滤的参照
			JComponent ref = e.getItem().getComponent();
			if (e.getItem().getTableCode().equals(TRNConst.TRNS_NEWINFO_TAB)) {
				if (ref instanceof UIRefPane) {
					// 申请节点调出业务时,参照全局+集团数据,其他情况参照全局+集团+组织数据
					int transMode = -1;
					BillItem item = getBillCardPanel().getHeadItem(
							StapplyVO.STAPPLY_MODE);
					if (item != null) {
						transMode = (Integer) item.getValueObject();
					}
					String pk_org = "";
					item = getBillCardPanel().getHeadItem(StapplyVO.PK_HI_ORG);
					pk_org = item.getValueObject() == null ? getModel()
							.getContext().getPk_org() : (String) item
							.getValueObject();
					if (!getModel().isApproveSite()
							&& TRNConst.TRANSMODE_CROSS_OUT == transMode) {
						if (((UIRefPane) ref).getRefModel() != null) {
							((UIRefPane) ref).setPk_org(getModel().getContext()
									.getPk_group());
						}
					} else {
						if (((UIRefPane) ref).getRefModel() != null) {
							((UIRefPane) ref).setPk_org(pk_org);
						}
					}
				}
			}
		}
		return true;
	}

	public void bodyRowChange(BillEditEvent e) {

	}

	public boolean canBeHidden() {

		if (this.getModel().getUiState() == UIState.ADD
				|| this.getModel().getUiState() == UIState.EDIT) {
			return false;
		} else {
			return super.canBeHidden();
		}
	}

	private BillData getBillData() {

		return getBillCardPanel().getBillData();
	}

	public IExceptionHandler getDefaultExceptionHandler() {

		if (defaultExceptionHandler == null) {
			defaultExceptionHandler = new DefaultExceptionHanler(getModel()
					.getContext().getEntranceUI());
		}
		return defaultExceptionHandler;
	}

	private SimpleDocServiceTemplate getService() {
		if (service == null) {
			service = new SimpleDocServiceTemplate("TrnBillFormEditor");
		}
		return service;
	}

	public AbstractTrnPFAppModel getModel() {

		return (AbstractTrnPFAppModel) super.getModel();
	}

	public void initUI() {
		super.initUI();
		hideAgreementSet();
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
	}

	/**
	 * 判断是否安装合同模块，用于控制调配申请单、离职申请单是否显示合同管理组织界面 <br>
	 * Created on 2014-2-21 11:04:33<br>
	 * 
	 * @author caiqm
	 */
	private void hideAgreementSet() {
		boolean isCMStart = false;
		isCMStart = PubEnv.isModuleStarted(PubEnv.getPk_group(),
				PubEnv.MODULE_HRCM);
		if (isCMStart) {
			return;
		}
		// 没有启用合同模块 则不显示合同页签
		getBillCardPanel().getHeadItem("pk_old_hrcm_org").setShow(false);
		getBillCardPanel().getHeadItem("pk_hrcm_org").setShow(false);
		getBillCardPanel().getHeadItem("isrelease").setShow(false);
		getBillCardPanel().getHeadItem("isend").setShow(false);
	}

	protected void onAdd() {

		super.onAdd();
		showAllSetItems(true);
		UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem(
				StapplyVO.PK_PSNJOB).getComponent();
		if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_IN)// 调入
		{
			// 调入时,参照集团内其他HR组织的人员
			psnRef.setRefNodeName("人员工作记录(行政树)");
		} else
		// 调出和组织内调配
		{
			psnRef.setRefNodeName("人员工作记录(左树不含下级HR)");
			psnRef.setPk_org(getModel().getContext().getPk_org());
		}

		psnRef.getRefModel();
		getBillCardPanel().getHeadItem(StapplyVO.NEWPK_POSTSERIES).setEnabled(
				true);
		getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE).setEnabled(
				false);// 调配申请新增时职级不可编辑
		String trnstype = (String) getBillCardPanel().getHeadItem(
				StapplyVO.PK_TRNSTYPE).getValueObject();

		try {
			boolean isset = getQuerySeq();
			if (isset)// 设置了
			{
				PsnjobSeqcontrolTreeModel refmodel = new PsnjobSeqcontrolTreeModel();
				refmodel.setPk_org(getModel().getContext().getPk_org());
				psnRef.setRefModel(refmodel);
			}

			TrnstypeFlowVO[] flow = (TrnstypeFlowVO[]) NCLocator
					.getInstance()
					.lookup(IPersistenceRetrieve.class)
					.retrieveByClause(
							null,
							TrnstypeFlowVO.class,
							" pk_group = '" + PubEnv.getPk_group()
									+ "' and pk_trnstype = '" + trnstype + "'");
			if (flow != null && flow.length > 0) {
				BillItem transtype = getBillCardPanel().getHeadItem(
						StapplyVO.TRANSTYPEID);
				BillItem transtypecode = getBillCardPanel().getHeadItem(
						StapplyVO.TRANSTYPE);
				if (transtype != null
						&& transtypecode != null
						&& getModel().getApproveType() == HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW) {
					transtype.setValue(flow[0].getPk_transtype());
					transtypecode.setValue(null);
					if (flow[0].getPk_transtype() != null) {
						BilltypeVO billtype = (BilltypeVO) NCLocator
								.getInstance()
								.lookup(IPersistenceRetrieve.class)
								.retrieveByPk(null, BilltypeVO.class,
										flow[0].getPk_transtype());
						transtypecode.setValue(billtype.getPk_billtypecode());
					}

				}

				BillItem busitype = getBillCardPanel().getHeadItem(
						StapplyVO.BUSINESS_TYPE);
				if (busitype != null && busitype.isShow()) {
					busitype.setValue(flow[0].getPk_businesstype());
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

		getBillCardPanel().execHeadTailLoadFormulas();
	}

	protected void onEdit() {
		super.onEdit();
		showAllSetItems(true);

		try {
			boolean isset = getQuerySeq();
			// if (isset)
			// {
			// UIRefPane psnRef = (UIRefPane)
			// getBillCardPanel().getHeadItem(StapplyVO.PK_PSNJOB).getComponent();
			// psnRef.setPk_org(getModel().getContext().getPk_org());
			// PsnjobSeqcontrolTreeModel refmodel = new
			// PsnjobSeqcontrolTreeModel();
			// refmodel.setPk_org(getModel().getContext().getPk_org());
			// psnRef.setRefModel(refmodel);
			// }
			getCtrt();// 修改时如果没有合同记录，解除和终止不可勾选
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		getBillCardPanel().execHeadTailLoadFormulas();
		Integer stapply_mode = (Integer) getBillCardPanel().getHeadItem(
				StapplyVO.STAPPLY_MODE).getValueObject();
		getModel().setStapply_mode(stapply_mode);
	}

	/**
	 * 重置billdata,重画单据界面
	 * 
	 * @param billdata
	 */
	private void resetBillData(BillData billdata) {

		getBillCardPanel().setBillData(billdata);
		getBillCardPanel().updateUI();
	}

	protected void setDefaultValue() {

		super.setDefaultValue();
		this.getBillCardPanel().setHeadItem(StapplyVO.PK_TRNSTYPE,
				getModel().getTrnstype());
		Integer tranMode = getModel().getStapply_mode();
		if (tranMode != null && TRNConst.TRANSMODE_CROSS_OUT == tranMode) {
			// 调出,或变更离职后HR组织
			getBillCardPanel().getHeadItem(StapplyVO.PK_HI_ORG)
					.setEnabled(true);
			getBillCardPanel().setHeadItem(StapplyVO.PK_HI_ORG, null);
		} else {
			// 组织内、调入、不变更离职后组织都是当前HR组织
			getBillCardPanel().getHeadItem(StapplyVO.PK_HI_ORG).setEnabled(
					false);

			getBillCardPanel().getHeadItem(StapplyVO.PK_HRCM_ORG).setEnabled(
					false);
			getBillCardPanel().getHeadItem(StapplyVO.ISRELEASE).setEnabled(
					false);
			getBillCardPanel().getHeadItem(StapplyVO.ISEND).setEnabled(false);
		}
		// 单据模板设置的默认值在界面显示
		BillItem[] items = getBillCardPanel().getBillData().getHeadTailItems();
		if (items != null) {
			for (BillItem item : items) {
				Object value2 = item.getDefaultValueObject();
				if (value2 != null
						&& (item.getValueObject() == null || IBillItem.TEXTAREA == item
								.getDataType()
								&& "".equals(item.getValueObject()))) {
					item.setValue(value2);
				}
			}
		}
	}

	public void setEditable(boolean editable) {

		super.setEditable(editable);
		if (getModel().getUiState() == UIState.ADD) {
			setHeadItemEnable(StapplyVO.BILL_CODE, getModel()
					.isBillCodeEditable());
		}
		if (getModel().getUiState() == UIState.EDIT) {
			setHeadItemEnable(StapplyVO.PK_TRNSTYPE, false);
			setHeadItemEnable(StapplyVO.PK_PSNJOB, false);
			setHeadItemEnable(StapplyVO.PK_HI_ORG, false);
			setHeadItemEnable(StapplyVO.PK_HRCM_ORG, false);
			if (getModel().isApproveSite()) {
				// 审批节点不能修改流程类型与业务类型
				setHeadItemEnable(StapplyVO.TRANSTYPEID, false);
				setHeadItemEnable(StapplyVO.BUSINESS_TYPE, false);
			} else {
				setHeadItemEnable(StapplyVO.BILL_CODE, getModel()
						.isBillCodeEditable());
			}
		}

		if (getModel().isApproveSite()) {
			setHeadItemEnable(StapplyVO.BILL_CODE, false);
			setHeadItemEnable(StapplyVO.BUSINESS_TYPE, false);
			setHeadItemEnable(StapplyVO.TRANSTYPEID, false);
			setHeadItemEnable(StapplyVO.APPLY_DATE, false);
			// 审批节点不能修改解除和终止
			setHeadItemEnable(StapplyVO.ISRELEASE, false);// 解除
			setHeadItemEnable(StapplyVO.ISEND, false);// 终止
		}

		// 如果当前的审批方式不为审批流,则不能编辑交易类型
		if (getModel().getApproveType() != HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW) {
			setHeadItemEnable(StapplyVO.TRANSTYPEID, false);
		}
	}

	/**
	 * 当为编辑态时,设置调配项目的可用性
	 */
	public void setItemEnabledOnEdit() {
		BillItem bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_POST);
		if (bi != null && bi.getValueObject() != null) {
			// 岗位不为空
			setHeadItemEnable(StapplyVO.NEWPK_JOB, false);
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, false);
		} else {
			setHeadItemEnable(StapplyVO.NEWPK_JOB, true);
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, true);
		}
		setHeadItemEnable(StapplyVO.NEWPK_POSTSERIES, false);
		bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOB);
		if (bi != null && bi.getValueObject() != null) {
			// 职务不为空
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, false);
			setHeadItemEnable(StapplyVO.NEWPK_JOBGRADE, true);
		} else {
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, true);
			setHeadItemEnable(StapplyVO.NEWPK_JOBGRADE, false);
		}
		setHeadItemEnable(StapplyVO.NEWSERIES, false);
		bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE);
		if (bi != null && bi.getValueObject() != null) {
			// 职级不为空
			setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, false);
		} else {
			setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, true);
		}
	}

	/**
	 * 设置项目的值与是否可用
	 * 
	 * @param itemKey
	 * @param value
	 * @param isEnable
	 */
	private void setItemValueAndEnable(String itemKey, Object value,
			boolean isEnable) {

		BillItem item = getBillCardPanel().getHeadItem(itemKey);
		if (item != null) {
			getBillCardPanel().setHeadItem(itemKey, value);
			item.setEnabled(isEnable);
		}
	}

	public void setModel(AbstractTrnPFAppModel model) {

		super.setModel(model);
	}

	public void setPersonInfo() throws BusinessException {

		BillItem[] olditems = getBillCardPanel().getBillData().getHeadItems(
				TRNConst.TRNS_OLDINFO_TAB);
		BillItem[] newitems = getBillCardPanel().getBillData().getHeadItems(
				TRNConst.TRNS_NEWINFO_TAB);
		BillItem[] allItems = (BillItem[]) ArrayUtils
				.addAll(olditems, newitems);
		UIRefPane psnref = RefUtils.getRefPaneOfItem(getBillCardPanel()
				.getHeadItem(StapplyVO.PK_PSNJOB));
		String pk_psnjob = psnref.getRefPK();
		if (pk_psnjob == null) {
			// 未选中人员，清空项目值
			for (BillItem i : allItems) {
				// 据实施要求，不清空调配，离职后人员类别的值 heqiaoa 20150520
				if (!("new" + PsnJobVO.PK_PSNCL).equals(i.getKey())) {
					i.setValue(null);
				}
			}
			return;
		}
		// 清空后项目
		for (BillItem i : newitems) {
			// 据实施要求，不清空调配，离职后人员类别的值 heqiaoa 20150520
			if (!("new" + PsnJobVO.PK_PSNCL).equals(i.getKey())) {
				i.setValue(null);
			}
		}
		PsnJobVO psn = getService().queryByPk(PsnJobVO.class, pk_psnjob, true);
		Object trnstype = getBillData().getHeadItem(StapplyVO.PK_TRNSTYPE)
				.getValueObject();
		// 获取设置了默认的项目
		IItemSetAdapter[] itemvos = getModel().getHashItemSets().get(trnstype);
		if (ArrayUtils.isEmpty(itemvos)) {
			return;
		}
		List<String> defaultls = new ArrayList<String>();
		UFBoolean isde = null;
		String key = null;
		for (IItemSetAdapter vo : itemvos) {
			key = vo.getItemkey();
			if (key.startsWith("old")) {
				continue;
			}
			isde = vo.getIsdefault();
			if (isde != null && isde.booleanValue()) {
				defaultls.add(key);
			}
		}
		// 选中人员，变动前项目赋值，变动后项目设默认值
		for (BillItem i : allItems) {
			key = i.getKey();
			if (key.startsWith("old") || defaultls.contains(key)) {
				i.setValue(psn.getAttributeValue(i.getKey().substring(3)));
			}
		}

		// 查询组织、部门、人员类别的权限
		HashMap<String, String> hm = NCLocator
				.getInstance()
				.lookup(ITransmngQueryService.class)
				.getPowerItem(
						pk_psnjob,
						getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
								&& TRNConst.BUSITYPE_TRANSITION
										.equals(getModel().getBillType()));

		if (defaultls.contains(StapplyVO.NEWPK_PSNCL)) {
			// 人员类别
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_PSNCL).setValue(
					hm.get(PsnJobVO.PK_PSNCL));
		}
		if (defaultls.contains(StapplyVO.NEWPK_DEPT)) {
			// 组织
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_ORG).setValue(
					hm.get(PsnJobVO.PK_ORG));
		}
		if (defaultls.contains(StapplyVO.NEWPK_DEPT)) {
			// 部门,没有组织权限就没有部门权限
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_DEPT).setValue(
					hm.get(PsnJobVO.PK_ORG) == null ? null : hm
							.get(PsnJobVO.PK_DEPT));
		}

		// 默认项目中没有职务,则职务为空,那么职务相关的就都为空
		BillItem jobItem = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOB);
		if (jobItem == null || jobItem.getValueObject() == null) {
			// 如果职务为空,则清除职务类别/职级
			getBillCardPanel().getHeadItem(StapplyVO.NEWSERIES).setValue(null);
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE).setValue(
					null);
		} else {
			// 职务不为空时
			String pk_job = jobItem.getValueObject().toString();
			setJobInfo(pk_job, defaultls);
		}

		// 如果默认的没有岗位,则岗位相关的都为空
		BillItem postItem = getBillCardPanel()
				.getHeadItem(StapplyVO.NEWPK_POST);
		if (!defaultls.contains(StapplyVO.NEWPK_POST) || postItem == null
				|| postItem.getValueObject() == null) {
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_POSTSERIES)
					.setValue(null);
		} else {
			String pk_post = postItem.getValueObject().toString();
			PostVO post = pk_post == null ? null : getService().queryByPk(
					PostVO.class, pk_post, true);
			if (post == null) {
				getBillCardPanel().getHeadItem(StapplyVO.NEWPK_POSTSERIES)
						.setValue(null);
			} else {
				getBillCardPanel().getHeadItem(StapplyVO.NEWPK_POSTSERIES)
						.setValue(post.getPk_postseries());
				if (!defaultls.contains(StapplyVO.NEWPK_JOB)) {
					setJobInfo(post.getPk_job(), defaultls);
				}
			}
		}

		// 处理是否在岗的值,如果不显示&不默认,则设为true
		BillItem bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPOSTSTAT);
		if (!defaultls.contains(StapplyVO.NEWPOSTSTAT) && bi != null
				&& !bi.isShow()) {
			bi.setValue(UFBoolean.TRUE);
		}

		// 设置选中的人员的pk_psndoc
		getBillCardPanel().getHeadItem(StapplyVO.PK_PSNDOC).setValue(
				psn.getPk_psndoc());
		getBillCardPanel().getHeadItem(StapplyVO.PK_PSNORG).setValue(
				psn.getPk_psnorg());
		getBillCardPanel().getHeadItem(StapplyVO.ASSGID).setValue(
				psn.getAssgid());
		Integer transMode = null;
		if (getBillCardPanel().getHeadItem(StapplyVO.STAPPLY_MODE) != null) {
			transMode = (Integer) getBillCardPanel().getHeadItem(
					StapplyVO.STAPPLY_MODE).getValueObject();
		}
		// 原合同管理组织应该是什么时候都需要查委托关系
		if (transMode == null || TRNConst.TRANSMODE_INNER == transMode
				|| TRNConst.TRANSMODE_CROSS_OUT == transMode) {
			// 如果是离职、内调、调出， 原人事组织都是当前hr组织
			// setItemValueAndEnable(StapplyVO.PK_HI_ORG,
			// getModel().getContext().getPk_org(), false);
			// setItemValueAndEnable(StapplyVO.PK_HRCM_ORG,
			// getModel().getContext().getPk_org(), false);

			// getBillCardPanel().getHeadItem(StapplyVO.PK_OLD_HI_ORG).setValue(getModel().getContext().getPk_org());
			getBillCardPanel().getHeadItem(StapplyVO.PK_OLD_HI_ORG).setValue(
					HiSQLHelper.getEveryHrorg(psn.getPk_psnorg(),
							psn.getAssgid(), ManagescopeBusiregionEnum.psndoc));
			getBillCardPanel().getHeadItem(StapplyVO.PK_OLD_HRCM_ORG)
					.setValue(
							HiSQLHelper.getEveryHrorg(psn.getPk_psnorg(),
									psn.getAssgid(),
									ManagescopeBusiregionEnum.psnpact));
		} else {
			// 调入是要查委托关系
			getBillCardPanel().getHeadItem(StapplyVO.PK_OLD_HI_ORG).setValue(
					HiSQLHelper.getHrorg(psn.getPk_psnorg(), psn.getAssgid()));
			getBillCardPanel().getHeadItem(StapplyVO.PK_OLD_HRCM_ORG)
					.setValue(
							HiSQLHelper.getEveryHrorg(psn.getPk_psnorg(),
									psn.getAssgid(),
									ManagescopeBusiregionEnum.psnpact));
		}

		if (transMode == null || TRNConst.TRANSMODE_INNER == transMode
				|| TRNConst.TRANSMODE_CROSS_IN == transMode) {
			getBillCardPanel().getHeadItem(StapplyVO.PK_HI_ORG).setValue(
					getModel().getContext().getPk_org());
			getBillCardPanel().getHeadItem(StapplyVO.PK_HRCM_ORG).setValue(
					getModel().getContext().getPk_org());
		}

		// 设置完人员信息后设置项目的可用性
		// setItemEnabledOnEdit();
	}

	private void setJobInfo(String pk_job, List<String> defaultls)
			throws BusinessException {
		// 如果职务是默认的,则要对职务相关的项目进行赋值
		JobVO job = pk_job == null ? null : getService().queryByPk(JobVO.class,
				pk_job, true);
		if (job == null) {
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE).setValue(
					null);
			getBillCardPanel().getHeadItem(StapplyVO.NEWSERIES).setValue(null);
		} else {
			// 设置职务类别
			getBillCardPanel().getHeadItem(StapplyVO.NEWSERIES).setValue(
					job.getPk_jobtype());

			String pk_jobgrade = (String) getHeadItemValue(StapplyVO.NEWPK_JOBGRADE);
			if (defaultls.contains(StapplyVO.NEWPK_JOBGRADE)
					&& pk_jobgrade != null) {
				// 职级是默认的,并且有值,则职级职等用职级数据,无论职等是否默认
				JobGradeVO grade = getService().queryByPk(JobGradeVO.class,
						pk_jobgrade, true);

				// FIXME: 该职级可能为空，而且此处空指针异常会被吞掉，heqiaoa 2014-12-15
				// 现在系统使用的职级为om_joblevel，而不是om_jobgrade，这里暂时做规避处理
				if (null != grade) {
					getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBRANK)
							.setValue(grade.getPk_jobrank());
				}
			} else {
				// 职级不是默认的或者职级没有数据,那职等使用职务上的数据,无论职等是否默认
				// getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBRANK).setValue(job.getPk_jobrank());
			}
		}
	}

	/**
	 * 根据项目设置重画单据项目设置分组
	 */
	public void showAllSetItems(boolean editstate) {

		BillData billdata = getBillCardPanel().getBillData();
		BillTabVO oldtab = billdata.getTabVO(IBillItem.HEAD,
				TRNConst.TRNS_OLDINFO_TAB);
		BillTabVO newtab = billdata.getTabVO(IBillItem.HEAD,
				TRNConst.TRNS_NEWINFO_TAB);
		if (oldtab == null || newtab == null) {
			return;
		}
		BillItem[] olditems = billdata.getHeadItems(TRNConst.TRNS_OLDINFO_TAB);
		BillItem[] newitems = billdata.getHeadItems(TRNConst.TRNS_NEWINFO_TAB);
		BillItem[] allItems = (BillItem[]) ArrayUtils
				.addAll(olditems, newitems);
		String trnstype = (String) getBillCardPanel().getHeadItem(
				StapplyVO.PK_TRNSTYPE).getValueObject();
		IItemSetAdapter[] itemvos = null;
		if (!StringUtils.isBlank(trnstype)) {
			itemvos = getModel().getHashItemSets().get(trnstype);
			if (itemvos == null || itemvos.length == 0) {
				getModel().queryTrnsItems(trnstype);
				itemvos = getModel().getHashItemSets().get(trnstype);
			}
		} else {
			itemvos = new IItemSetAdapter[0];
		}
		// 业务类型无对应项目设置
		if (ArrayUtils.isEmpty(itemvos)) {
			for (BillItem item : allItems) {
				item.setShow(false);
			}
			oldtab.setMixindex(2);
			newtab.setMixindex(2);
			resetBillData(billdata);
			return;
		}
		// 先把所有的项目都隐藏 再显示
		for (BillItem item : allItems) {
			item.setShow(false);
		}
		// 根据项目设置重画单据项目设置分组
		boolean isedit = false;// 此属性现在为是否变化,如果为false则不显示
		boolean notnull = false;
		String key = null;
		BillItem item = null;
		for (IItemSetAdapter itemvo : itemvos) {
			key = itemvo.getItemkey();
			isedit = itemvo.getIsedit().booleanValue();
			notnull = itemvo.getIsnotnull().booleanValue();
			item = billdata.getHeadItem(key);
			if (item != null) {
				if (key.startsWith("old")) {
					// 签项目都显示
					item.setShow(isedit);
				} else {
					// 后项目只显示isedit为true的
					item.setShow(isedit);
				}
				if (StapplyVO.OLDPK_POST.equals(item.getKey())
						|| StapplyVO.OLDPK_JOBGRADE.equals(item.getKey())
						|| StapplyVO.OLDPK_JOBRANK.equals(item.getKey())
						|| StapplyVO.NEWPK_POST.equals(item.getKey())
						|| StapplyVO.NEWPK_JOBGRADE.equals(item.getKey())
						|| StapplyVO.NEWPK_JOBRANK.equals(item.getKey())) {
					item.setShow(true);
                }
				if (StapplyVO.NEWPK_ORG.equals(item.getKey())
						|| StapplyVO.NEWPK_DEPT.equals(item.getKey())
						|| StapplyVO.NEWPK_PSNCL.equals(item.getKey())) {
					// 组织 部门 人员类别必输 无论何时
					item.setNull(true);
					item.setEdit(true && editstate);
					item.setEnabled(true && editstate);

					// add by jiazhtb on 2015.7.22
					// 调配申请，调配后部门是否必填为修改为非必填，审批为必填
					if (StapplyVO.NEWPK_DEPT.equals(item.getKey())
							&& !getModel().isApproveSite()) {
						item.setNull(false);
					}
					// end
				} else {
					item.setNull(key.startsWith("new") && isedit && notnull);// 只有新项目才考虑是否必输,与能否编辑关联,不能编辑的必输也没有意义
					item.setEdit(key.startsWith("new") && isedit && editstate);
					item.setEnabled(key.startsWith("new") && isedit
							&& editstate);
				}

			}
		}

		oldtab.setMixindex(1);
		newtab.setMixindex(1);
		resetBillData(billdata);
	}

	protected void synchronizeDataFromModel() {

		super.synchronizeDataFromModel();
		if (getModel().getSelectedData() != null) {
			showAllSetItems(false);
		}
	}

	public void setPsnjobPKs(String[] psnjobPKs) {
		this.psnjobPKs = psnjobPKs;
	}

	public String[] getPsnjobPKs() {
		return psnjobPKs;
	}
}
