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
 * �䶯��Ƭ���������
 */
public class TrnBillFormEditor extends HrBillFormEditor implements
		BillCardBeforeEditListener {

	private static final long serialVersionUID = 1L;

	// Ĭ��ֵ���ýӿ�
	private IExceptionHandler defaultExceptionHandler = null;

	private SimpleDocServiceTemplate service = null;

	private String[] psnjobPKs;

	@Override
	public void afterEdit(BillEditEvent e) {
		try {
			if (StapplyVO.PK_PSNJOB.equals(e.getKey())) {
				// ������ְ��Ա
				setPersonInfo();
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				// ����ʱ��յ������֯
				getRelAndEnd(
						(String) getBillCardPanel().getHeadItem(
								StapplyVO.PK_HRCM_ORG).getValueObject(),
						(Integer) item.getValueObject());// ѡ��ͬ�ֶ�ʱ����������ֹ�ֶ�---������ʵΪ�º�ͬ������֯��Ŀǰ���ڵ����������֯�����濼���Ƿ��޸�
			} else if (StapplyVO.NEWPK_ORG.equals(e.getKey())) {
				// ��ְ��֯,��֯�ĺ����ж�Ҫ���
				setItemValueAndEnable(StapplyVO.NEWPK_DEPT, null, true);// ����
				setItemValueAndEnable(StapplyVO.NEWPK_POST, null, true);// ��λ
				setItemValueAndEnable(StapplyVO.NEWPK_JOB, null, true);// ְ��
				setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, false);// ְ��
				setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// ְ��
				setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null, true);// ��λ����
				setItemValueAndEnable(StapplyVO.NEWSERIES, null, true);// ְ�����
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				if (item != null
						&& (Integer) item.getValueObject() == TRNConst.TRANSMODE_CROSS_OUT)// &&
																							// getModel().isApproveSite()
				{
					// ����ʱ��յ������֯
					setItemValueAndEnable(StapplyVO.PK_HI_ORG, null, true);
					setItemValueAndEnable(StapplyVO.PK_HRCM_ORG, null, true);
				}
			} else if (StapplyVO.NEWPK_DEPT.equals(e.getKey())) {
				setItemValueAndEnable(StapplyVO.NEWPK_POST, null, true);// ��λ
				setItemValueAndEnable(StapplyVO.NEWPK_JOB, null, true);// ְ��
				setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, false);// ְ��
				setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// ְ��
				setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null, true);// ��λ����
				setItemValueAndEnable(StapplyVO.NEWSERIES, null, true);// ְ�����
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				if (item != null
						&& (Integer) item.getValueObject() == TRNConst.TRANSMODE_CROSS_OUT)// &&
																							// !getModel().isApproveSite()
				{
					// ����ʱ��յ������֯
					String pk_dept = (String) getBillCardPanel().getHeadItem(
							StapplyVO.NEWPK_DEPT).getValueObject();
					String pk_org = HiSQLHelper.getHrorgBydept(pk_dept);// �õ�����---�����������֯
					setItemValueAndEnable(StapplyVO.PK_HI_ORG, pk_org, true);

					// ���ݲ���������ѯ�ò��ŵĺ�ͬ�����HR��֯
					String pk_cmorg = HiSQLHelper.getEveryHrorgBydept(pk_dept,
							ManagescopeBusiregionEnum.psnpact);
					// �˴����º�ͬ������֯�Ƿ�ɱ༭����������
					setItemValueAndEnable(StapplyVO.PK_HRCM_ORG, pk_cmorg, true);
					getRelAndEnd(pk_org, (Integer) item.getValueObject());// ѡ��ͬ�ֶ�ʱ����������ֹ�ֶ�---������ʵΪ�º�ͬ������֯��Ŀǰ���ڵ����������֯�����濼���Ƿ��޸�
				}
			} else if (StapplyVO.NEWPK_POST.equals(e.getKey())) {
				// ��λ
				String pk_post = (String) getBillCardPanel().getHeadItem(
						StapplyVO.NEWPK_POST).getValueObject();
				PostVO post = pk_post == null ? null : getService().queryByPk(
						PostVO.class, pk_post, true);
				if (post != null) {
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES,
							post.getPk_postseries(), false);// ��λ����
					setItemValueAndEnable(StapplyVO.NEWPK_JOB,
							post.getPk_job(), true);// ְ��
					JobVO jobVO = post.getPk_job() == null ? null
							: getService().queryByPk(JobVO.class,
									post.getPk_job());
					boolean isJobTypeSeted = false;
					if (jobVO != null) {
						setItemValueAndEnable(StapplyVO.NEWSERIES,
								jobVO.getPk_jobtype(), false);// ְ�����
						isJobTypeSeted = true;
					}
					if (post.getEmployment() != null) {
						setItemValueAndEnable(StapplyVO.NEWOCCUPATION,
								post.getEmployment(), true);// ְҵ
					}
					if (post.getWorktype() != null) {
						setItemValueAndEnable(StapplyVO.NEWWORKTYPE,
								post.getWorktype(), true);// ����
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
								false);// ְ�����
					}
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE,
							defaultlevel, true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// ְ��
				} else {
					setItemValueAndEnable(StapplyVO.NEWSERIES, null, true);// ְ�����
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null,
							true);// ��λ����
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, false);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOB, null, true);// ְ��
				}
				if (post == null) {
					afterEdit(new BillEditEvent(getBillCardPanel().getHeadItem(
							StapplyVO.NEWPK_JOB).getComponent(), null,
							StapplyVO.NEWPK_JOB));
				}
			} else if (StapplyVO.NEWPK_JOB.equals(e.getKey())) {
				// ְ��
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
							job.getPk_jobtype(), true);// ְ�����
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE,
							defaultlevel, true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null,
							false);// ��λ����
				} else {
					setItemValueAndEnable(StapplyVO.NEWSERIES, null,
							(StringUtils.isNotBlank(pk_post) ? false : true));// ְ�����
					setItemValueAndEnable(StapplyVO.NEWPK_POSTSERIES, null,
							true);// ��λ����
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null,
							(StringUtils.isBlank(pk_post) ? false : true));// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// ְ��
				}
			} else if (StapplyVO.NEWPK_JOBGRADE.equals(e.getKey())) {
				// ְ��
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
					// ְ������ְ�Ⱥ�ְ�Ȳ��ܱ༭
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// ְ��
				} else {
					// ְ����պ�,��ְ�������ְ��,����ְ���ϵ�ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// ְ��
				}
			} else if (StapplyVO.NEWSERIES.equals(e.getKey())) {
				// ְ�����
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
							defaultlevel, true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// ְ��
				} else if (StringUtils.isBlank(pk_job)
						&& StringUtils.isBlank(series)) {
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// ְ��
				}
			} else if (StapplyVO.NEWPK_POSTSERIES.equals(e.getKey())) {
				// ��λ����
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
							defaultlevel, true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, defaultrank,
							true);// ְ��
				} else if (StringUtils.isBlank(pk_job)
						&& StringUtils.isBlank(series)
						&& StringUtils.isBlank(pk_post)
						&& StringUtils.isBlank(pk_postseries)) {
					setItemValueAndEnable(StapplyVO.NEWPK_JOBGRADE, null, true);// ְ��
					setItemValueAndEnable(StapplyVO.NEWPK_JOBRANK, null, true);// ְ��
				}
			} else if (StapplyVO.TRANSTYPEID.equals(e.getKey())) {
				// �������ͱ仯 ͬ��ID
				UIRefPane ref = (UIRefPane) getBillCardPanel().getHeadItem(
						StapplyVO.TRANSTYPEID).getComponent();
				setItemValueAndEnable(StapplyVO.TRANSTYPE, ref.getRefCode(),
						false);
			} else if (StapplyVO.PK_HI_ORG.equals(e.getKey())) {// �����������֯
				String pk_hi_org = (String) getBillCardPanel().getHeadItem(
						StapplyVO.PK_HI_ORG).getValueObject();
				setItemValueAndEnable(StapplyVO.PK_HRCM_ORG, pk_hi_org, true);
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				getRelAndEnd(pk_hi_org, (Integer) item.getValueObject());// ѡ��ͬ�ֶ�ʱ����������ֹ�ֶ�---������ʵΪ�º�ͬ������֯��Ŀǰ���ڵ����������֯�����濼���Ƿ��޸�
			} else if (StapplyVO.EFFECTDATE.equals(e.getKey()))// ��Ч����
			{
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				// ����ʱ��յ������֯
				getRelAndEnd(
						(String) getBillCardPanel().getHeadItem(
								StapplyVO.PK_HRCM_ORG).getValueObject(),
						(Integer) item.getValueObject());// ѡ��ͬ�ֶ�ʱ����������ֹ�ֶ�---������ʵΪ�º�ͬ������֯��Ŀǰ���ڵ����������֯�����濼���Ƿ��޸�
			} else if (StapplyVO.PK_HRCM_ORG.equals(e.getKey())) {
				BillItem item = getBillCardPanel().getHeadItem(
						StapplyVO.STAPPLY_MODE);
				String pk_hrcm_org = (String) getBillCardPanel().getHeadItem(
						StapplyVO.PK_HRCM_ORG).getValueObject();
				getRelAndEnd(pk_hrcm_org, (Integer) item.getValueObject());
			}

			String pk_jobType = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWSERIES).getValueObject();// ְ�����
			String pk_job = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_JOB).getValueObject();// ְ��
			String pk_postSeries = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POSTSERIES).getValueObject();// ��λ����
			String pk_post = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_POST).getValueObject();// ��λ
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
		// ִ����ʾ��ʽ
		getBillCardPanel().execHeadTailLoadFormulas();
	}

	// ѡ��ͬ�ֶ�ʱ����������ֹ�ֶ�---������ʵΪ�º�ͬ������֯��Ŀǰ���ڵ����������֯�����濼���Ƿ��޸�
	public void getRelAndEnd(String pk_hrcm_org, Integer inte)
			throws BusinessException {
		CtrtVO ctrtvo = getCtrt();
		if (ctrtvo == null) {
			return;
		}

		if (TRNConst.TRANSMODE_INNER == inte) {
			setItemValueAndEnable(StapplyVO.ISRELEASE, null, true);// ���
			setItemValueAndEnable(StapplyVO.ISEND, null, true);// ��ֹ
			return;
		}

		if (TRNConst.TRANSMODE_CROSS_OUT != inte) {// ���ǵ������
			getRelEffectDate(ctrtvo);
			return;
		}

		// ����ʱ��յ������֯
		String pk_old_hrcm_org = (String) getBillCardPanel().getHeadItem(
				StapplyVO.PK_OLD_HRCM_ORG).getValueObject();
		if (StringUtils.isBlank(pk_old_hrcm_org)
				|| StringUtils.isBlank(pk_hrcm_org)) {
			// ��ͬǰ��֯�϶�����Ϊ�գ���ֻ�к�ͬ����֯Ϊ�յĿ���
			setItemValueAndEnable(StapplyVO.ISRELEASE, null, true);// ���
			setItemValueAndEnable(StapplyVO.ISEND, null, true);// ��ֹ
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
		if (ctrtvo == null) {// ���µĺ�ͬ��¼����ǩ��/���/��ǩ����״̬��δǩ����ͬ���ߺ�ͬ�Ѿ����/��ֹ��������Ҫ��ѡ�ͱ༭
			setItemValueAndEnable(StapplyVO.ISRELEASE, null, false);// ���
			setItemValueAndEnable(StapplyVO.ISEND, null, false);// ��ֹ
			return null;
		}
		return ctrtvo;
	}

	// ������Ч�����ж��Ƿ���/��ֹ���˹��ܸ����������ʹ�õ�
	public void getRelEffectDate(CtrtVO ctrtvo) throws BusinessException {
		UFLiteralDate effectdate = (UFLiteralDate) getBillCardPanel()
				.getHeadItem(StapplyVO.EFFECTDATE).getValueObject();
		if (effectdate != null) {
			String isshow = getRelOrEndShow(ctrtvo, effectdate);
			setItemValueAndEnable(StapplyVO.ISRELEASE,
					StapplyVO.ISRELEASE.equals(isshow) ? UFBoolean.TRUE
							: UFBoolean.FALSE, true);// ���
			setItemValueAndEnable(StapplyVO.ISEND,
					StapplyVO.ISEND.equals(isshow) ? UFBoolean.TRUE
							: UFBoolean.FALSE, true);// ��ֹ
		} else {
			// ����Ч����Ϊ��ʱ���Ͳ���Ҫȥ�ж�������Ƿ��к�ͬ����Ӧ�ý��������ֹ��ͬ
			boolean isrelease = (Boolean) getBillCardPanel().getHeadItem(
					StapplyVO.ISRELEASE).getValueObject();
			setItemValueAndEnable(StapplyVO.ISRELEASE, isrelease ? isrelease
					: null, true);// ���
			boolean isend = (Boolean) getBillCardPanel().getHeadItem(
					StapplyVO.ISEND).getValueObject();
			setItemValueAndEnable(StapplyVO.ISEND, isend ? isend : null, true);// ��ֹ
		}
	}

	// ��ѯ����Ա�Ƿ��к�ͬ���ݣ����жϽ������ֹ��ʹ��
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

	// ��ѯ����Ա�Ƿ��к�ͬ���ݣ����жϽ������ֹ��ʹ��
	public String getRelOrEndShow(CtrtVO ctrtvo, UFLiteralDate effectdate)
			throws BusinessException {
		String isshow = null;
		if (ctrtvo != null) {
			UFLiteralDate enddate = ctrtvo.getEnddate();
			if (effectdate.before(enddate))// ��Ч�����������еĺ�ͬ�Ľ������ڣ���Ҫ�����ͬ
			{
				isshow = StapplyVO.ISRELEASE;
			} else
			// ��Ч���ڵ��ڻ����������еĺ�ͬ�Ľ������ڣ���Ҫ��ֹ��ͬ
			{
				isshow = StapplyVO.ISEND;
			}
		}
		return isshow;
	}

	// ��ѯ��ǰ�𼶹ܿ�ģʽ
	public boolean getQuerySeq() throws BusinessException {
		boolean isset = false;// δ����
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

		// ��ʹ�÷����¼���ʽ��̫����
		if (StapplyVO.TRANSTYPEID.equals(e.getItem().getKey())) {
			// ������
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
			// ҵ������,��ʱ��'������Դ'�µ�,���˵�δ���õ�
			String where = " and busiprop = 6  and ( isnull(pk_org,'~') = '~' or pk_org = '"
					+ getModel().getContext().getPk_org()
					+ "') and validity =1 and primarybilltype like '"
					+ getModel().getBillType() + "%'";
			((UIRefPane) e.getItem().getComponent()).getRefModel()
					.addWherePart(where);
		} else if (StapplyVO.PK_PSNJOB.equals(e.getItem().getKey())) {
			// ����/��ְ��Ա
			// ��ְ����
			String inJobSql = " and hi_psnjob.pk_psnjob in "
					+ HiSQLHelper.getInJobSQL(true);
			UIRefPane psnRef = (UIRefPane) e.getItem().getComponent();
			String condition = "";
			String powerSql = null;
			boolean iscrossin = getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_IN ? true
					: false;// �жϵ��䷽ʽ�Ƿ��ǵ���,����ʱΪtrue,��֯�ں͵���Ϊfalse
			if (iscrossin)// ����
			{
				psnRef.getRefModel().setUseDataPower(Boolean.FALSE);
				// ����ʱ,���ռ���������HR��֯����Ա,ʹ��"������Ա�����ⳡ��"
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
			// ��֯�ں͵���
			{
				// �������,���ձ�HR��֯����Ա,ʹ��ͨ�ó���
				powerSql = HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
						HICommonValue.RESOUCECODE_6007PSNJOB,
						IRefConst.DATAPOWEROPERATION_CODE, "hi_psnjob");
				condition = inJobSql
						+ (StringUtils.isBlank(powerSql) ? ""
								: (" and " + powerSql));
			}

			try {
				// û�����øɲ������HR��֯��ͬʱά���ɲ���Ա����Ϣ��=���񡱣�����ʾ�ɲ���Ϣ
				if (!NCLocator.getInstance().lookup(IPsndocQryService.class)
						.getValueOFParaDeployCadre(getModel().getContext())) {
					condition += " and (bd_psndoc.iscadre = 'N' or bd_psndoc.iscadre is null) ";
				}

				boolean isset = getQuerySeq();
				if (isset)// �������𼶹ܿز���������
				{
					if (!iscrossin)// ��֯�ں͵���
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
					// ����
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
			// ��֯
			UIRefPane orgRef = (UIRefPane) e.getItem().getComponent();
			// ����UAP��֯Ȩ�޿��ƣ���HR��֯Ȩ�޿���
			orgRef.getRefModel().setUseDataPower(Boolean.FALSE);
			String powerSql = "";
			String whereSql = "";
			if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				// ֻ���ڵ������������ڵ㲢�ҵ��䷽ʽΪ����ʱʹ�����ⳡ��
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
					// ֻ���ڵ������������ڵ㲢�ҵ��䷽ʽΪ����ʱʹ�����ⳡ��

				}
			}

			if (!StringUtils.isBlank(powerSql)) {
				whereSql = " and pk_adminorg in ( select pk_org from org_orgs where "
						+ powerSql
						+ ") and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)";
			} else {
				whereSql = " and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)";
			}

			// �𼶹ܿأ�����/��ְ���롪������/��ְ����Ϣ-��֯��
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
			// ����
			UIRefPane deptRef = (UIRefPane) e.getItem().getComponent();
			// ����UAP����Ȩ�޿��ƣ���HR����Ȩ�޿���
			deptRef.getRefModel().setUseDataPower(Boolean.FALSE);
			String pk_org = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_ORG).getValueObject();
			deptRef.setPk_org(pk_org);
			String cond = " and hrcanceled = 'N' and depttype <> 1 ";
			String powerSql = "";
			/*
			 * if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
			 * && TRNConst.BUSITYPE_TRANSITION.equals(getModel().getBillType()))
			 * { // ֻ���ڵ������������ڵ㲢�ҵ��䷽ʽΪ����ʱʹ�����ⳡ�� powerSql =
			 * HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
			 * HICommonValue.RESOUCECODE_DEPT,
			 * HICommonValue.OPERATIONCODE_TRANSDEFAULT, "org_dept");//
			 * ����Ȩ���е�ʹ�ó���Ϊ���䶯����֯/�������á� } else { powerSql =
			 * HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(),
			 * HICommonValue.RESOUCECODE_DEPT,
			 * IRefConst.DATAPOWEROPERATION_CODE, "org_dept");//
			 * ����Ȩ���е�ʹ�ó���Ϊ��ͨ�����á� }
			 */
			if (!StringUtils.isBlank(powerSql)) {
				cond += " and " + powerSql;
			}
			deptRef.getRefModel().addWherePart(cond);
		} else if (StapplyVO.NEWPK_PSNCL.equals(e.getItem().getKey())) {
			// ��Ա���
			// ��Ա���,ת��ʹ��ͨ�õ���Ա��𳡾�
			UIRefPane psnclRef = (UIRefPane) e.getItem().getComponent();
			String powerSql = "";
			if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
					&& TRNConst.BUSITYPE_TRANSITION.equals(getModel()
							.getBillType())) {
				// ֻ���ڵ������������ڵ㲢�ҵ��䷽ʽΪ����ʱʹ�����ⳡ��
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
			// ��λ-������֯�����Ź���
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
				// ֻ���ڵ������������ڵ㲢�ҵ��䷽ʽΪ����ʱʹ�����ⳡ�� ,��λȨ�ޡ��䶯�������á���������
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
			// ְ��
			String pk_org = (String) getBillCardPanel().getHeadItem(
					StapplyVO.NEWPK_ORG).getValueObject();
			UIRefPane jobRef = (UIRefPane) e.getItem().getComponent();
			if (pk_org != null) {
				jobRef.setPk_org(pk_org);
			} else {
				jobRef.setPk_org(getModel().getContext().getPk_group());
			}
		} else if (StapplyVO.NEWPK_JOBGRADE.equals(e.getItem().getKey())) {
			// ְ��
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
			// ְ��
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
			// �������ݵ����������֯���˵Ĳ���
			JComponent ref = e.getItem().getComponent();
			if (e.getItem().getTableCode().equals(TRNConst.TRNS_NEWINFO_TAB)) {
				if (ref instanceof UIRefPane) {
					// ����ڵ����ҵ��ʱ,����ȫ��+��������,�����������ȫ��+����+��֯����
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
	 * �ж��Ƿ�װ��ͬģ�飬���ڿ��Ƶ������뵥����ְ���뵥�Ƿ���ʾ��ͬ������֯���� <br>
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
		// û�����ú�ͬģ�� ����ʾ��ͬҳǩ
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
		if (getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_IN)// ����
		{
			// ����ʱ,���ռ���������HR��֯����Ա
			psnRef.setRefNodeName("��Ա������¼(������)");
		} else
		// ��������֯�ڵ���
		{
			psnRef.setRefNodeName("��Ա������¼(���������¼�HR)");
			psnRef.setPk_org(getModel().getContext().getPk_org());
		}

		psnRef.getRefModel();
		getBillCardPanel().getHeadItem(StapplyVO.NEWPK_POSTSERIES).setEnabled(
				true);
		getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE).setEnabled(
				false);// ������������ʱְ�����ɱ༭
		String trnstype = (String) getBillCardPanel().getHeadItem(
				StapplyVO.PK_TRNSTYPE).getValueObject();

		try {
			boolean isset = getQuerySeq();
			if (isset)// ������
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
			getCtrt();// �޸�ʱ���û�к�ͬ��¼���������ֹ���ɹ�ѡ
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		getBillCardPanel().execHeadTailLoadFormulas();
		Integer stapply_mode = (Integer) getBillCardPanel().getHeadItem(
				StapplyVO.STAPPLY_MODE).getValueObject();
		getModel().setStapply_mode(stapply_mode);
	}

	/**
	 * ����billdata,�ػ����ݽ���
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
			// ����,������ְ��HR��֯
			getBillCardPanel().getHeadItem(StapplyVO.PK_HI_ORG)
					.setEnabled(true);
			getBillCardPanel().setHeadItem(StapplyVO.PK_HI_ORG, null);
		} else {
			// ��֯�ڡ����롢�������ְ����֯���ǵ�ǰHR��֯
			getBillCardPanel().getHeadItem(StapplyVO.PK_HI_ORG).setEnabled(
					false);

			getBillCardPanel().getHeadItem(StapplyVO.PK_HRCM_ORG).setEnabled(
					false);
			getBillCardPanel().getHeadItem(StapplyVO.ISRELEASE).setEnabled(
					false);
			getBillCardPanel().getHeadItem(StapplyVO.ISEND).setEnabled(false);
		}
		// ����ģ�����õ�Ĭ��ֵ�ڽ�����ʾ
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
				// �����ڵ㲻���޸�����������ҵ������
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
			// �����ڵ㲻���޸Ľ������ֹ
			setHeadItemEnable(StapplyVO.ISRELEASE, false);// ���
			setHeadItemEnable(StapplyVO.ISEND, false);// ��ֹ
		}

		// �����ǰ��������ʽ��Ϊ������,���ܱ༭��������
		if (getModel().getApproveType() != HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW) {
			setHeadItemEnable(StapplyVO.TRANSTYPEID, false);
		}
	}

	/**
	 * ��Ϊ�༭̬ʱ,���õ�����Ŀ�Ŀ�����
	 */
	public void setItemEnabledOnEdit() {
		BillItem bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_POST);
		if (bi != null && bi.getValueObject() != null) {
			// ��λ��Ϊ��
			setHeadItemEnable(StapplyVO.NEWPK_JOB, false);
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, false);
		} else {
			setHeadItemEnable(StapplyVO.NEWPK_JOB, true);
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, true);
		}
		setHeadItemEnable(StapplyVO.NEWPK_POSTSERIES, false);
		bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOB);
		if (bi != null && bi.getValueObject() != null) {
			// ְ��Ϊ��
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, false);
			setHeadItemEnable(StapplyVO.NEWPK_JOBGRADE, true);
		} else {
			// setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, true);
			setHeadItemEnable(StapplyVO.NEWPK_JOBGRADE, false);
		}
		setHeadItemEnable(StapplyVO.NEWSERIES, false);
		bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE);
		if (bi != null && bi.getValueObject() != null) {
			// ְ����Ϊ��
			setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, false);
		} else {
			setHeadItemEnable(StapplyVO.NEWPK_JOBRANK, true);
		}
	}

	/**
	 * ������Ŀ��ֵ���Ƿ����
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
			// δѡ����Ա�������Ŀֵ
			for (BillItem i : allItems) {
				// ��ʵʩҪ�󣬲���յ��䣬��ְ����Ա����ֵ heqiaoa 20150520
				if (!("new" + PsnJobVO.PK_PSNCL).equals(i.getKey())) {
					i.setValue(null);
				}
			}
			return;
		}
		// ��պ���Ŀ
		for (BillItem i : newitems) {
			// ��ʵʩҪ�󣬲���յ��䣬��ְ����Ա����ֵ heqiaoa 20150520
			if (!("new" + PsnJobVO.PK_PSNCL).equals(i.getKey())) {
				i.setValue(null);
			}
		}
		PsnJobVO psn = getService().queryByPk(PsnJobVO.class, pk_psnjob, true);
		Object trnstype = getBillData().getHeadItem(StapplyVO.PK_TRNSTYPE)
				.getValueObject();
		// ��ȡ������Ĭ�ϵ���Ŀ
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
		// ѡ����Ա���䶯ǰ��Ŀ��ֵ���䶯����Ŀ��Ĭ��ֵ
		for (BillItem i : allItems) {
			key = i.getKey();
			if (key.startsWith("old") || defaultls.contains(key)) {
				i.setValue(psn.getAttributeValue(i.getKey().substring(3)));
			}
		}

		// ��ѯ��֯�����š���Ա����Ȩ��
		HashMap<String, String> hm = NCLocator
				.getInstance()
				.lookup(ITransmngQueryService.class)
				.getPowerItem(
						pk_psnjob,
						getModel().getStapply_mode() == TRNConst.TRANSMODE_CROSS_OUT
								&& TRNConst.BUSITYPE_TRANSITION
										.equals(getModel().getBillType()));

		if (defaultls.contains(StapplyVO.NEWPK_PSNCL)) {
			// ��Ա���
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_PSNCL).setValue(
					hm.get(PsnJobVO.PK_PSNCL));
		}
		if (defaultls.contains(StapplyVO.NEWPK_DEPT)) {
			// ��֯
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_ORG).setValue(
					hm.get(PsnJobVO.PK_ORG));
		}
		if (defaultls.contains(StapplyVO.NEWPK_DEPT)) {
			// ����,û����֯Ȩ�޾�û�в���Ȩ��
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_DEPT).setValue(
					hm.get(PsnJobVO.PK_ORG) == null ? null : hm
							.get(PsnJobVO.PK_DEPT));
		}

		// Ĭ����Ŀ��û��ְ��,��ְ��Ϊ��,��ôְ����صľͶ�Ϊ��
		BillItem jobItem = getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOB);
		if (jobItem == null || jobItem.getValueObject() == null) {
			// ���ְ��Ϊ��,�����ְ�����/ְ��
			getBillCardPanel().getHeadItem(StapplyVO.NEWSERIES).setValue(null);
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE).setValue(
					null);
		} else {
			// ְ��Ϊ��ʱ
			String pk_job = jobItem.getValueObject().toString();
			setJobInfo(pk_job, defaultls);
		}

		// ���Ĭ�ϵ�û�и�λ,���λ��صĶ�Ϊ��
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

		// �����Ƿ��ڸڵ�ֵ,�������ʾ&��Ĭ��,����Ϊtrue
		BillItem bi = getBillCardPanel().getHeadItem(StapplyVO.NEWPOSTSTAT);
		if (!defaultls.contains(StapplyVO.NEWPOSTSTAT) && bi != null
				&& !bi.isShow()) {
			bi.setValue(UFBoolean.TRUE);
		}

		// ����ѡ�е���Ա��pk_psndoc
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
		// ԭ��ͬ������֯Ӧ����ʲôʱ����Ҫ��ί�й�ϵ
		if (transMode == null || TRNConst.TRANSMODE_INNER == transMode
				|| TRNConst.TRANSMODE_CROSS_OUT == transMode) {
			// �������ְ���ڵ��������� ԭ������֯���ǵ�ǰhr��֯
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
			// ������Ҫ��ί�й�ϵ
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

		// ��������Ա��Ϣ��������Ŀ�Ŀ�����
		// setItemEnabledOnEdit();
	}

	private void setJobInfo(String pk_job, List<String> defaultls)
			throws BusinessException {
		// ���ְ����Ĭ�ϵ�,��Ҫ��ְ����ص���Ŀ���и�ֵ
		JobVO job = pk_job == null ? null : getService().queryByPk(JobVO.class,
				pk_job, true);
		if (job == null) {
			getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBGRADE).setValue(
					null);
			getBillCardPanel().getHeadItem(StapplyVO.NEWSERIES).setValue(null);
		} else {
			// ����ְ�����
			getBillCardPanel().getHeadItem(StapplyVO.NEWSERIES).setValue(
					job.getPk_jobtype());

			String pk_jobgrade = (String) getHeadItemValue(StapplyVO.NEWPK_JOBGRADE);
			if (defaultls.contains(StapplyVO.NEWPK_JOBGRADE)
					&& pk_jobgrade != null) {
				// ְ����Ĭ�ϵ�,������ֵ,��ְ��ְ����ְ������,����ְ���Ƿ�Ĭ��
				JobGradeVO grade = getService().queryByPk(JobGradeVO.class,
						pk_jobgrade, true);

				// FIXME: ��ְ������Ϊ�գ����Ҵ˴���ָ���쳣�ᱻ�̵���heqiaoa 2014-12-15
				// ����ϵͳʹ�õ�ְ��Ϊom_joblevel��������om_jobgrade��������ʱ����ܴ���
				if (null != grade) {
					getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBRANK)
							.setValue(grade.getPk_jobrank());
				}
			} else {
				// ְ������Ĭ�ϵĻ���ְ��û������,��ְ��ʹ��ְ���ϵ�����,����ְ���Ƿ�Ĭ��
				// getBillCardPanel().getHeadItem(StapplyVO.NEWPK_JOBRANK).setValue(job.getPk_jobrank());
			}
		}
	}

	/**
	 * ������Ŀ�����ػ�������Ŀ���÷���
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
		// ҵ�������޶�Ӧ��Ŀ����
		if (ArrayUtils.isEmpty(itemvos)) {
			for (BillItem item : allItems) {
				item.setShow(false);
			}
			oldtab.setMixindex(2);
			newtab.setMixindex(2);
			resetBillData(billdata);
			return;
		}
		// �Ȱ����е���Ŀ������ ����ʾ
		for (BillItem item : allItems) {
			item.setShow(false);
		}
		// ������Ŀ�����ػ�������Ŀ���÷���
		boolean isedit = false;// ����������Ϊ�Ƿ�仯,���Ϊfalse����ʾ
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
					// ǩ��Ŀ����ʾ
					item.setShow(isedit);
				} else {
					// ����Ŀֻ��ʾiseditΪtrue��
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
					// ��֯ ���� ��Ա������ ���ۺ�ʱ
					item.setNull(true);
					item.setEdit(true && editstate);
					item.setEnabled(true && editstate);

					// add by jiazhtb on 2015.7.22
					// �������룬��������Ƿ����Ϊ�޸�Ϊ�Ǳ������Ϊ����
					if (StapplyVO.NEWPK_DEPT.equals(item.getKey())
							&& !getModel().isApproveSite()) {
						item.setNull(false);
					}
					// end
				} else {
					item.setNull(key.startsWith("new") && isedit && notnull);// ֻ������Ŀ�ſ����Ƿ����,���ܷ�༭����,���ܱ༭�ı���Ҳû������
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
