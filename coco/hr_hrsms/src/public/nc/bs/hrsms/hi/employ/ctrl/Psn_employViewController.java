package nc.bs.hrsms.hi.employ.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.dbcache.intf.IDBCacheBS;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.hr.utils.PubEnv;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hrsms.hi.entrymng.IEntrymngManageService;
import nc.itf.hrsms.hi.entrymng.IEntrymngQueryService;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hrsms.hi.psndoc.manage.IPsndocManageServicePB;
import nc.itf.hrsms.hi.psndoc.qry.IPsndocQryservice;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwBusinessException;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.core.uif.delegator.DefaultDataValidator;
import nc.uap.lfw.core.uif.delegator.IDataValidator;
import nc.vo.hi.entrymng.AggEntryapplyVO;
import nc.vo.hi.entrymng.EntryapplyVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.LevelRelationVO;
import nc.vo.om.post.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

public class Psn_employViewController implements IController {

	private static final long serialVersionUID = 1L;
	private static final long ID = 5L;

	/**
	 * 人员信息加载
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException
	 * @throws HrssException
	 */
	public void PsndocDSLoad(DataLoadEvent dataLoadEvent) throws BusinessException, HrssException {

		Dataset ds = dataLoadEvent.getSource();
		PsndocVO vo = (PsndocVO) CommonUtil.getCacheValue(AppPsndocConstant.PSN_EMPLOY_INFO);
		if (vo == null) {
			return;
		}
		new SuperVO2DatasetSerializer().serialize(new SuperVO[] { vo }, ds, Row.STATE_NORMAL);
		ds.setEnabled(true);
		ds.setCurrentKey(Dataset.MASTER_KEY);
		ds.setRowSelectIndex(0);
		Row row = ds.getSelectedRow();

		// 获得经理或部门负责人当前管理部门所在集团PK
		String pk_group = SessionUtil.getPk_mng_group();
		// 获得当前登录人员管理的部门的Hr组织
		String pk_HROrg = SessionUtil.getHROrg();
		// 人员信息
		row.setValue(ds.nameToIndex(PsndocVO.PK_PSNDOC), vo.getPk_psndoc());
		// 人员编码
		BillCodeContext bcc = HiCacheUtils.getBillCodeContext(HICommonValue.NBCR_PSNDOC_CODE, pk_group,
				SessionUtil.getPk_org());
		if (bcc != null) {
			String code = BillCoderUtils.getBillCode(pk_group, SessionUtil.getPk_org(), HICommonValue.NBCR_PSNDOC_CODE);
			if (vo == null || StringUtils.isBlank(vo.getCode())) {
				row.setValue(ds.nameToIndex(PsndocVO.CODE), code);
				CommonUtil.setCacheValue(SessionUtil.getPk_user(), code);
			} else {
				CommonUtil.setCacheValue(SessionUtil.getPk_user(), vo.getCode());
			}
		}

		// 姓名
		row.setValue(ds.nameToIndex(PsndocVO.NAME), vo.getName());
		// 证件类型
		row.setValue(ds.nameToIndex(PsndocVO.IDTYPE), vo.getIdtype());
		// 身份证
		row.setValue(ds.nameToIndex(PsndocVO.ID), vo.getId());
		// 单据所属集团
		row.setValue(ds.nameToIndex(PsndocVO.PK_GROUP), pk_group);
		// 单据所属组织
		row.setValue(ds.nameToIndex(PsndocVO.PK_ORG), pk_HROrg);
		// 单据所属hr组织
		row.setValue(ds.nameToIndex(PsndocVO.PK_HRORG), pk_HROrg);
		// 是否是店员
		row.setValue(ds.nameToIndex(PsndocVO.ISSHOPASSIST), UFBoolean.TRUE);
		// 是否drp,
		row.setValue(ds.nameToIndex("glbdef7"), UFBoolean.TRUE);
	}

	public void PsnOrgonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row row = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);

		// 最新进入集团日期
		PsndocVO vo = (PsndocVO) CommonUtil.getCacheValue("psn_employ_info");
		if (vo != null && vo.getCode() != null) {
			String orgglbdef1 = this.getOrgGlbdef1ByPk(vo.getPk_psndoc());
			row.setString(ds.nameToIndex("orgglbdef1"), orgglbdef1);
		}

		// 获得经理或部门负责人当前管理部门所在集团PK
		String pk_group = SessionUtil.getPk_mng_group();
		// 获得当前登录人员管理的部门的Hr组织
		String pk_HROrg = SessionUtil.getHROrg();

		// 单据所属集团
		row.setValue(ds.nameToIndex(PsndocVO.PK_GROUP), pk_group);
		// 单据所属组织
		row.setValue(ds.nameToIndex(PsndocVO.PK_ORG), pk_HROrg);
		// 单据所属组织
		row.setValue(ds.nameToIndex(PsndocVO.PK_HRORG), pk_HROrg);
	}

	public void JobonDataLoad(DataLoadEvent dataLoadEvent) throws BusinessException, HrssException {
		Dataset ds = dataLoadEvent.getSource();
		ds.setEnabled(true);
		PsnJobVO vo = (PsnJobVO) CommonUtil.getCacheValue(AppPsndocConstant.PSN_EMPLOY_JOB);
		if (vo == null) {
			return;
		}
		Row row = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		// 获得经理或部门负责人当前管理部门所在集团PK
		String pk_group = SessionUtil.getPk_mng_group();
		// 获得当前登录人员管理的部门的Hr组织
		String pk_HROrg = SessionUtil.getHROrg();
		// 获得经理或部门负责人当前管理部门PK
		String pk_rmdept = SessionUtil.getPk_mng_dept();
		// 单据所属集团
		row.setValue(ds.nameToIndex(PsnJobVO.PK_GROUP), pk_group);
		// 单据所属组织
		row.setValue(ds.nameToIndex(PsnJobVO.PK_ORG), pk_HROrg);
		// 单据所属组织
		row.setValue(ds.nameToIndex(PsnJobVO.PK_HRORG), pk_HROrg);
		// 单据所属组织
		row.setValue(ds.nameToIndex(PsnJobVO.PK_HRGROUP), pk_group);
		// 所属部门
		row.setValue(ds.nameToIndex(PsnJobVO.PK_DEPT), pk_rmdept);
		// 人员类别
		row.setValue(ds.nameToIndex(PsnJobVO.PK_PSNCL), vo.getPk_psncl());
		// 人员编码
		BillCodeContext bcc = HiCacheUtils.getBillCodeContext(HICommonValue.NBCR_PSNDOC_CODE, pk_group, pk_HROrg);
		if (bcc != null) {
			String code = (String) CommonUtil.getCacheValue(SessionUtil.getPk_user());
			row.setValue(ds.nameToIndex(PsnJobVO.CLERKCODE), code);
		}
		row.setValue(ds.nameToIndex(PsnJobVO.POSTSTAT), UFBoolean.TRUE);
	}

	public void TrialonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		ds.setEnabled(true);
		Row row = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		// 获得经理或部门负责人当前管理部门所在集团PK
		String pk_group = SessionUtil.getPk_mng_group();
		// 获得当前登录人员管理的部门的Hr组织
		String pk_HROrg = SessionUtil.getHROrg();

		// 单据所属集团
		row.setValue(ds.nameToIndex("pk_group"), pk_group);
		// 单据所属组织
		row.setValue(ds.nameToIndex("pk_org"), pk_HROrg);
		// 单据所属组织
		row.setValue(ds.nameToIndex("pk_hrorg"), pk_HROrg);
	}

	public void Saveonclick(MouseEvent<MenuItem> mouseEvent) throws BusinessException {
		PsndocVO vo = (PsndocVO) CommonUtil.getCacheValue(AppPsndocConstant.PSN_EMPLOY_INFO);

		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(viewMain, "bd_psndoc");
		IDataValidator validator = new DefaultDataValidator();

		Dataset hi_psnorg = ViewUtil.getDataset(viewMain, "hi_psnorg");
		Dataset hi_psnjob_curr = ViewUtil.getDataset(viewMain, "hi_psnjob_curr");
		Dataset hi_psndoc_trial = ViewUtil.getDataset(viewMain, "hi_psndoc_trial");
		validator.validate(hi_psnjob_curr, new LfwView());
		validator.validate(hi_psnorg, new LfwView());
		validator.validate(hi_psndoc_trial, new LfwView());
		validator.validate(ds, new LfwView());

		// add begin shaochj 2015-06-23 入职登记修改保存
		String pk_psndoc = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute("scj_pkone");
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute("scj_pkone", null);
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute("scj_pkone");

		// 判断是修改还是新增，新增的逻辑还是用原有的代码
		if (StringUtils.isEmpty(pk_psndoc)) {
			// 保存人员信息、工作记录、组织关系、试用情况
			Dataset[] detailDss = new Dataset[] {};
			PsnJobVO jobvo = new PsnJobVO();
			BeanUtils.copyProperties(new Dataset2SuperVOSerializer<PsnJobVO>().serialize(hi_psnjob_curr,
					hi_psnjob_curr.getSelectedRow())[0], jobvo);
			jobvo = setJob(jobvo);
			PsndocAggVO aggVO = (PsndocAggVO) new Datasets2AggVOSerializer().serialize(ds, detailDss,
					PsndocAggVO.class.getName());

			TrialVO trialVO = new TrialVO();
			BeanUtils.copyProperties(
					new Dataset2SuperVOSerializer<TrialVO>().serialize(hi_psndoc_trial,
							hi_psndoc_trial.getSelectedRow())[0], trialVO);
			trialVO = settrialVO(trialVO);
			PsnOrgVO orgvo = new PsnOrgVO();
			orgvo.setStatus(VOStatus.NEW);
			UFDate orgglbdef1 = (UFDate) hi_psnorg.getSelectedRow().getValue(hi_psnorg.nameToIndex("orgglbdef1"));
			BeanUtils.copyProperties(
					new Dataset2SuperVOSerializer<PsnOrgVO>().serialize(hi_psnorg, hi_psnorg.getSelectedRow())[0],
					orgvo);
			orgvo = setOrg(orgvo);
			String psnpk = aggVO.getParentVO().getPk_psndoc();

			aggVO.getParentVO().setPsnJobVO(jobvo);
			aggVO.getParentVO().setPsnOrgVO(orgvo);
			aggVO.getParentVO().setIdtype(vo.getIdtype());

			aggVO.getParentVO().setEnablestate(2);
			// aggVO.getParentVO().setDataoriginflag(0);

			aggVO.getParentVO().setPreviewphoto(null);
			aggVO.getParentVO().setPhoto(null);
			CertVO[] certvos;

			try {
				certvos = ServiceLocator.lookup(IPsndocManageServicePB.class).queryCertVO(
						aggVO.getParentVO().getIdtype(), aggVO.getParentVO().getId());
			} catch (BusinessException | HrssException e) {
				LfwLogger.error(e.getMessage(), e.getCause());
				throw new LfwRuntimeException(e.getMessage(), e.getCause());
			}
			if (certvos != null && certvos.length == 0) {
				aggVO.setTableVO(CertVO.getDefaultTableName(), new CertVO[] { createCert(aggVO.getParentVO()) });
			}
			aggVO.setTableVO(PsnOrgVO.getDefaultTableName(), new PsnOrgVO[] { orgvo });
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), new PsnJobVO[] { jobvo });
			if (trialVO.getBegindate() != null) {
				aggVO.setTableVO(TrialVO.getDefaultTableName(), new TrialVO[] { trialVO });
			}

			if (StringUtils.isNotEmpty(psnpk)) {
				aggVO.getParentVO().setStatus(VOStatus.UPDATED);
				aggVO.getParentVO().getPsnJobVO().setStatus(VOStatus.NEW);
				aggVO.getParentVO().getPsnOrgVO().setStatus(VOStatus.NEW);
				SuperVO[] childVO = aggVO.getAllChildrenVO();
				for (int i = 0; childVO != null && i < childVO.length; i++) {
					if (childVO[i].getPrimaryKey() != null && VOStatus.NEW == childVO[i].getStatus()) {
						childVO[i].setStatus(VOStatus.UPDATED);
					}
				}
				for (SuperVO superVO : aggVO.getAllChildrenVO()) {

					// 返聘再聘设置pk_psndoc
					if (superVO.getAttributeValue(PsnOrgVO.PK_PSNDOC) == null) {
						superVO.setAttributeValue(PsnOrgVO.PK_PSNDOC, aggVO.getParentVO().getPk_psndoc());
					}

					if (!(superVO instanceof PsnOrgVO) && superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
						superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, aggVO.getParentVO().getPsnOrgVO().getPrimaryKey());
					}
					if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
							&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
						superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, aggVO.getParentVO().getPsnJobVO().getPrimaryKey());
					}
					if (superVO.getAttributeValue(PsnJobVO.ASSGID) == null) {
						superVO.setAttributeValue(PsnJobVO.ASSGID, aggVO.getParentVO().getPsnJobVO().getAssgid());
					}
				}
			} else {
				aggVO.getParentVO().setStatus(VOStatus.NEW);
			}
			IPsndocService service = null;
			String msg = "人员信息保存失败！";
			try {
				service = ServiceLocator.lookup(IPsndocService.class);
				aggVO = service.savePsndoc((PsndocAggVO) aggVO, false);
				if (aggVO != null)
					msg = "人员信息保存成功！";
				PsnOrgVO psnorgVO = getPsnorgVOByPk(aggVO.getParentVO().getPk_psndoc());
				if (psnorgVO != null) {
					psnorgVO.setAttributeValue("orgglbdef1", orgglbdef1);
					updateOrgGlbdef1ByPk(psnorgVO, psnorgVO.getPrimaryKey(), "orgglbdef1");
				}
			} catch (BusinessException e) {
				new HrssException(e).deal();
			} catch (HrssException e) {
				new HrssException(e).alert();
			}
			// 生成待提交的入职申请单
			AggEntryapplyVO applyVo = new AggEntryapplyVO();
			applyVo.setParentVO(createEntryapply(aggVO));
			String[] billCodes = null;

			ArrayList<String> psnjobstr = new ArrayList<String>() {
			};
			psnjobstr.add(aggVO.getParentVO().getPsnJobVO().getPk_psnjob());
			AggEntryapplyVO[] applyVos = NCLocator.getInstance().lookup(IEntrymngManageService.class)
					.batchSaveBill(applyVo, psnjobstr, createCtxNew(aggVO, jobvo), billCodes, false);
			if (applyVos != null) {
				msg = msg + " 入职申请保存成功！";
			} else {
				msg = msg + " 入职申请保存失败！";
			}
			CommonUtil.closeViewDialog("psn_employ");
			CommonUtil.showShortMessage(msg);
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
			// 修改逻辑
		} else {
			Dataset[] detailDss = new Dataset[] {};
			PsnJobVO jobvo = new PsnJobVO();
			BeanUtils.copyProperties(new Dataset2SuperVOSerializer<PsnJobVO>().serialize(hi_psnjob_curr,
					hi_psnjob_curr.getSelectedRow())[0], jobvo);
			jobvo = setJob(jobvo);
			PsndocAggVO aggVO = (PsndocAggVO) new Datasets2AggVOSerializer().serialize(ds, detailDss,
					PsndocAggVO.class.getName());

			// 是否同步到drp
			aggVO.getParentVO().setAttributeValue("glbdef7",
					ds.getAllRow()[0].getUFBoolean(ds.nameToIndex("glbdef7")).toString());
			aggVO.getParentVO().setAttributeValue("glbdef2",
					ds.getAllRow()[0].getUFBoolean(ds.nameToIndex("glbdef7")).toString());
			aggVO.getParentVO().getAttributeValue("glbdef7");
			// 由于新增时，当试用日期为空时，将不再向试用子表中插入数据，所以要判断新增的时候试用子表是否有数据
			TrialVO trialVO = new TrialVO();
			if (hi_psndoc_trial.getSelectedRow() != null) {
				String pk_sub = (String) hi_psndoc_trial.getSelectedRow().getValue(
						hi_psndoc_trial.nameToIndex("pk_psndoc_sub"));
				if (StringUtils.isNotEmpty(pk_sub)) {
					trialVO.setStatus(VOStatus.UPDATED);
					BeanUtils.copyProperties(
							new Dataset2SuperVOSerializer<TrialVO>().serialize(hi_psndoc_trial,
									hi_psndoc_trial.getSelectedRow())[0], trialVO);
				} else {
					BeanUtils.copyProperties(
							new Dataset2SuperVOSerializer<TrialVO>().serialize(hi_psndoc_trial,
									hi_psndoc_trial.getSelectedRow())[0], trialVO);
					trialVO.setStatus(VOStatus.NEW);
					trialVO.setAssgid(1);
					trialVO.setTrial_type(1);
					trialVO.setLastflag(UFBoolean.TRUE);
					trialVO.setEndflag(UFBoolean.FALSE);
					trialVO.setCreator(PubEnv.getPk_user());
					trialVO.setCreationtime(PubEnv.getServerTime());
					trialVO.setPk_group(PubEnv.getPk_group());
					trialVO.setPk_org(SessionUtil.getHROrg());
					trialVO.setPk_hrorg(SessionUtil.getHROrg());
					trialVO.setPk_psndoc(jobvo.getPk_psndoc());
					trialVO.setPk_psnjob(jobvo.getPk_psnjob());
					trialVO.setPk_psnorg(jobvo.getPk_psnorg());
					trialVO.setRecordnum(new Integer(0));
				}
			}
			PsnOrgVO orgvo = new PsnOrgVO();
			orgvo.setStatus(VOStatus.UPDATED);
			UFDate orgglbdef1 = (UFDate) hi_psnorg.getSelectedRow().getValue(hi_psnorg.nameToIndex("orgglbdef1"));
			if (hi_psnorg.getSelectedRow() != null) {
				BeanUtils.copyProperties(new Dataset2SuperVOSerializer<PsnOrgVO>().serialize(hi_psnorg)[0], orgvo);
				orgvo = setOrg(orgvo);
			}
			aggVO.getParentVO().setPsnJobVO(jobvo);
			aggVO.getParentVO().setPsnOrgVO(orgvo);
			aggVO.getParentVO().setStatus(VOStatus.UPDATED);
			aggVO.getParentVO().setEnablestate(2);
			// 身份证件子集
			String idcard = aggVO.getParentVO().getId();
			CertVO certVO = this.getCertVO(idcard, pk_psndoc);
			certVO.setStatus(VOStatus.UPDATED);
			aggVO.setTableVO(CertVO.getDefaultTableName(), new CertVO[] { certVO });
			// edit by shaochj Oct 13, 2015 end

			aggVO.setTableVO(PsnOrgVO.getDefaultTableName(), new PsnOrgVO[] { orgvo });
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), new PsnJobVO[] { jobvo });
			if (trialVO.getBegindate() != null) {
				aggVO.setTableVO(TrialVO.getDefaultTableName(), new TrialVO[] { trialVO });
			}
			IPsndocManageServicePB pbservice = null;
			try {
				pbservice = ServiceLocator.lookup(IPsndocManageServicePB.class);
				pbservice.updateAggVO(aggVO);
				SuperVO orgVO = orgvo;
				if (orgVO != null) {
					orgVO.setAttributeValue("orgglbdef1", orgglbdef1);
					updateOrgGlbdef1ByPk(orgVO, orgVO.getPrimaryKey(), "orgglbdef1");
				}
				// 生成待提交的入职申请单
				AggEntryapplyVO applyVo = new AggEntryapplyVO();
				applyVo.setParentVO(createEntryapply(aggVO));
				EntryapplyVO avo = (EntryapplyVO) applyVo.getParentVO();
				avo.setPk_psndoc(aggVO.getParentVO().getPk_psndoc());
				avo.setPk_psnjob(jobvo.getPk_psnjob());
				avo.setPk_hi_org(jobvo.getPk_psnorg());
				applyVo.setParentVO(avo);
				LoginContext context = new LoginContext();
				context.setPk_group(aggVO.getParentVO().getPk_group());
				context.setPk_org(aggVO.getParentVO().getPk_org());

				String condition = " pk_hi_org ='" + jobvo.getPk_psnorg() + "' " + "and pk_psndoc= '"
						+ aggVO.getParentVO().getPk_psndoc() + "' ";

				AggEntryapplyVO[] vos = NCLocator.getInstance().lookup(IEntrymngQueryService.class)
						.queryByCondition(context, condition);
				if (vos != null && vos.length > 0) {
					AggEntryapplyVO aggEntryVO = vos[0];
					EntryapplyVO entryVO = (EntryapplyVO) aggEntryVO.getParentVO();
					if (-1 == entryVO.getApprove_state().intValue()) {
						NCLocator.getInstance().lookup(IEntrymngManageService.class).deleteBill(aggEntryVO);
						ArrayList<String> psnjobstr = new ArrayList<String>() {
						};
						psnjobstr.add(aggVO.getParentVO().getPsnJobVO().getPk_psnjob());
						NCLocator.getInstance().lookup(IEntrymngManageService.class)
								.batchSaveBill(applyVo, psnjobstr, createCtxNew(aggVO, jobvo), null, false);
					} else {
						throw new LfwBusinessException("入职单据已提交，不能修改！");
					}
				}
				CommonUtil.closeViewDialog("psn_employ");
				CommonUtil.showShortMessage("修改成功！");
				CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
			} catch (HrssException e) {
				new HrssException(e).deal();
			}
		}

	}

	/**
	 * 保存入职申请单，上下文 与createCtx 不同在于pk_org
	 * 
	 * @param aggVO
	 * @param jobvo
	 * @return
	 */
	private LoginContext createCtxNew(PsndocAggVO aggVO, PsnJobVO jobvo) {
		LoginContext ctx = new LoginContext();
		ctx.setPk_group(aggVO.getParentVO().getPk_group());
		// ctx.setPk_org(aggVO.getParentVO().getPk_org());
		ctx.setPk_org(jobvo.getPk_org()); // 再入职人员新增所属组织为最新的工作记录对应的组织。zhangjie
		ctx.setPk_loginUser(PubEnv.getPk_user());
		ctx.setNodeCode("60070entryapply");
		return ctx;
	}

	private LoginContext createCtx(PsndocAggVO aggVO) {
		LoginContext ctx = new LoginContext();
		ctx.setPk_group(aggVO.getParentVO().getPk_group());
		ctx.setPk_org(aggVO.getParentVO().getPk_org());
		ctx.setPk_loginUser(PubEnv.getPk_user());
		ctx.setNodeCode("60070entryapply");
		return ctx;
	}

	private EntryapplyVO createEntryapply(PsndocAggVO aggVO) {
		EntryapplyVO vo = new EntryapplyVO();
		vo.setApply_date(new UFLiteralDate());
		vo.setPk_group(aggVO.getParentVO().getPk_group());
		vo.setPk_org(aggVO.getParentVO().getPk_org());
		vo.setPk_billtype(nc.bs.hrss.trn.PsnApplyConsts.ENTRY_BILLTYPE_CODE);
		vo.setCreator(PubEnv.getPk_user());
		vo.setCreationtime(PubEnv.getServerTime());
		vo.setIssyncwork(UFBoolean.TRUE);
		return vo;
	}

	private CertVO createCert(PsndocVO psndocVO) {
		CertVO cert = new CertVO();
		cert.setIdtype(psndocVO.getIdtype());
		cert.setId(psndocVO.getId());
		cert.setPk_group(psndocVO.getPk_group());
		cert.setPk_org(psndocVO.getPk_org());
		cert.setIseffect(UFBoolean.TRUE);
		cert.setIsstart(UFBoolean.TRUE);
		cert.setCreator(PubEnv.getPk_user());
		cert.setCreationtime(PubEnv.getServerTime());
		cert.setStatus(VOStatus.NEW);
		return cert;
	}

	private PsnJobVO setJob(PsnJobVO jobVO) {
		jobVO.setIsmainjob(UFBoolean.TRUE);
		jobVO.setLastflag(UFBoolean.TRUE);
		jobVO.setEndflag(UFBoolean.FALSE);
		jobVO.setTrnsevent(1);
		jobVO.setCreator(PubEnv.getPk_user());
		jobVO.setCreationtime(PubEnv.getServerTime());

		return jobVO;
	}

	private TrialVO settrialVO(TrialVO trialVO) {
		trialVO.setAssgid(1);
		trialVO.setTrial_type(1);
		trialVO.setLastflag(UFBoolean.TRUE);
		trialVO.setEndflag(UFBoolean.FALSE);
		trialVO.setCreator(PubEnv.getPk_user());
		trialVO.setCreationtime(PubEnv.getServerTime());
		trialVO.setPk_group(PubEnv.getPk_group());
		trialVO.setPk_org(SessionUtil.getHROrg());
		trialVO.setPk_hrorg(SessionUtil.getHROrg());
		trialVO.setStatus(VOStatus.NEW);
		return trialVO;
	}

	private PsnOrgVO setOrg(PsnOrgVO orgVO) {
		orgVO.setIndocflag(UFBoolean.FALSE);
		orgVO.setLastflag(UFBoolean.TRUE);
		orgVO.setPsntype(0);
		orgVO.setCreator(PubEnv.getPk_user());
		orgVO.setCreationtime(PubEnv.getServerTime());
		return orgVO;
	}

	public void TrialonAfterDataChange(DatasetCellEvent dataChangeEvent) {
		Dataset ds = dataChangeEvent.getSource();
		Object object = dataChangeEvent.getNewValue();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		LfwView widget = ViewUtil.getCurrentView();
		FormComp frmPsnInfo = (FormComp) widget.getViewComponents().getComponent("form_trial");
		if (object.equals("1")) {
			frmPsnInfo.getElementById("begindate").setNullAble(false);
		} else if (object.equals("0")) {
			frmPsnInfo.getElementById("begindate").setNullAble(true);
		}
	}

	/**
	 * 在参照中添加WHERE条件
	 */
	public PostVO[] queryPostVOsBySQL(String WhereSQL) throws BusinessException {
		String sql = "select * from om_post om_post where" + WhereSQL;
		LfwLogger.info(sql);
		ArrayList<PostVO> al = (ArrayList<PostVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(PostVO.class));
		return al == null ? null : al.toArray(new PostVO[0]);
	}

	public JobVO[] queryJobVOsBySQL(String WhereSQL) throws BusinessException {
		String sql = "select * from om_job om_job where " + WhereSQL;
		LfwLogger.info(sql);
		ArrayList<JobVO> al = (ArrayList<JobVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(JobVO.class));
		return al == null ? null : al.toArray(new JobVO[0]);
	}

	public LevelRelationVO[] queryLevelRelationVOsBySQL(String WhereSQL) throws BusinessException {
		String sql = "select * from OM_LEVELRELATION OM_LEVELRELATION where DEFAULTLEVEL ='Y' and " + WhereSQL;
		LfwLogger.info(sql);
		ArrayList<LevelRelationVO> al = (ArrayList<LevelRelationVO>) new BaseDAO().executeQuery(sql,
				new BeanListProcessor(LevelRelationVO.class));
		return al == null ? null : al.toArray(new LevelRelationVO[0]);
	}

	public void Closeonclick(MouseEvent mouseEvent) {
		CommonUtil.closeViewDialog("psn_employ");
	}

	public void DoconAfterDataChange(DatasetCellEvent datasetCellEvent) {
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset hi_psnjob_curr = ViewUtil.getDataset(viewMain, "hi_psnjob_curr");
		Row jobrow = hi_psnjob_curr.getSelectedRow();
		Dataset ds = ViewUtil.getDataset(viewMain, "bd_psndoc");
		int filedColIndex = datasetCellEvent.getColIndex();
		Row row = ds.getSelectedRow();
		if (filedColIndex == ds.nameToIndex(PsndocVO.CODE)) {
			// 员工号等于员工编码
			String code = (String) row.getValue(ds.nameToIndex(PsndocVO.CODE));
			jobrow.setValue(hi_psnjob_curr.nameToIndex(PsnJobVO.CLERKCODE), code);
		}
	}

	public void JobonAfterDataChange(DatasetCellEvent datasetCellEvent) throws BusinessException {
		Dataset ds = datasetCellEvent.getSource();
		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset hi_psnorg = ViewUtil.getDataset(viewMain, "hi_psnorg");
		int filedColIndex = datasetCellEvent.getColIndex();
		Row row = ds.getSelectedRow();
		if (filedColIndex == ds.nameToIndex(PsnJobVO.PK_POST)) {// 带出的字段为：岗位系列、职务、职务类别、职级、职业
			String pk_post = (String) row.getValue(ds.nameToIndex(PsnJobVO.PK_POST));
			PostVO[] posts = queryPostVOsBySQL(" pk_post = '" + pk_post + "'");
			if (posts != null) {
				row.setValue(ds.nameToIndex(PsnJobVO.PK_POSTSERIES), posts[0].getPk_postseries());
				row.setValue(ds.nameToIndex(PsnJobVO.PK_JOB), posts[0].getPk_job());
				row.setValue(ds.nameToIndex(PsnJobVO.OCCUPATION), posts[0].getEmployment());
				String pk_job = (String) row.getValue(ds.nameToIndex(PsnJobVO.PK_JOB));
				JobVO[] postJobVOs = queryJobVOsBySQL(" pk_job = '" + pk_job + "'");
				if (postJobVOs != null && postJobVOs.length > 0) {
					row.setValue(ds.nameToIndex(PsnJobVO.SERIES), postJobVOs[0].getPk_jobtype());
				}
				LevelRelationVO[] jobs = queryLevelRelationVOsBySQL(" pk_post = '" + pk_post + "'");
				if (jobs != null && jobs.length > 0) {
					row.setValue(ds.nameToIndex(PsnJobVO.PK_JOBGRADE), jobs[0].getPk_joblevel());
				} else {
					row.setValue(ds.nameToIndex(PsnJobVO.PK_JOBGRADE), null);
				}
			}
		}

		if (filedColIndex == ds.nameToIndex(PsnJobVO.BEGINDATE)) {// 进入事业部日期和进入集团日期默认等于到岗日期
			Row orgrow = hi_psnorg.getSelectedRow();
			UFLiteralDate begindate = (UFLiteralDate) row.getValue(ds.nameToIndex(PsnJobVO.BEGINDATE));
			if (begindate != null) {
				orgrow.setValue(hi_psnorg.nameToIndex(PsnOrgVO.BEGINDATE), begindate);
				orgrow.setValue(hi_psnorg.nameToIndex(PsnOrgVO.JOINSYSDATE), begindate);
				// add by shaochj Jun 26, 2015 begin
				PsndocVO vo = (PsndocVO) CommonUtil.getCacheValue("psn_employ_info");
				if (vo == null || vo.getCode() == null) {
					orgrow.setValue(hi_psnorg.nameToIndex("orgglbdef1"), begindate);
				}
				if (vo == null || vo.getCode() == null) {
					orgrow.setValue(hi_psnorg.nameToIndex("orgglbdef1"), begindate);
				}
				// add by shaochj Jun 26, 2015 end
			}
		}
	}

	public void TtialonAfterDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		Object object = datasetCellEvent.getNewValue();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		LfwView widget = ViewUtil.getCurrentView();
		FormComp frmPsnInfo = (FormComp) widget.getViewComponents().getComponent("form_trial");
		if (object.equals("1")) {
			frmPsnInfo.getElementById("begindate").setNullAble(false);
		} else if (object.equals("0")) {
			frmPsnInfo.getElementById("begindate").setNullAble(true);
		}
	}

	// add by shaochj Jun 24, 2015 begin
	/**
	 * onBeforeShow事件，处理修改操作
	 * 
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		String pk = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute("scj_pk");
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute("scj_pk", null);
		String scj_status = (String) AppLifeCycleContext.current().getApplicationContext()
				.getAppAttribute("scj_status");
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute("scj_status", null);

		LfwView viewMain = ViewUtil.getCurrentView();
		Dataset hi_psnjob_curr = ViewUtil.getDataset(viewMain, "hi_psnjob_curr");

		if (StringUtils.isEmpty(pk)) {
			// 职等默认 1等 主键：1001Q4100000001K5CR9 pk_jobrank
			Row jobrow = hi_psnjob_curr.getSelectedRow();
			if (jobrow != null) {
				jobrow.setValue(hi_psnjob_curr.nameToIndex("pk_jobrank"), "1001Q4100000001K5CR9");
			}
			return;
		}
		boolean enable = true;
		if (!"-1".equals(scj_status)) {
			enable = false;
		}
		MenubarComp menus = viewMain.getViewMenus().getMenuBar("menu_employ");
		MenuItem menuID = menus.getElementById("onSave");
		menuID.setVisible(enable);
		try {
			AppLifeCycleContext.current().getApplicationContext().addAppAttribute("scj_pkone", pk);
			PsndocAggVO aggVO = NCLocator.getInstance().lookup(IPsndocQryService.class).queryPsndocVOByPk(pk);
			// 人员基本信息
			Dataset ds = ViewUtil.getDataset(viewMain, "bd_psndoc");
			PsndocVO psndocVO = aggVO.getParentVO();

			String pk_address = psndocVO.getAddr();
			HashMap<String, String> addrMap = this.getAddress(pk_address);
			String countryname = this.getCountryName(addrMap.get("pk_country"));// 国家
			String provincename = this.getRegionName(addrMap.get("pk_province"));// 省
			String cityname = this.getRegionName(addrMap.get("pk_city"));// 市
			String vsectionname = this.getRegionName(addrMap.get("pk_vsection"));// 县区
			String detailinfo = this.getRegionName(addrMap.get("pk_detailinfo"));// 详细地址
			String postcode = this.getRegionName(addrMap.get("pk_postcode"));// 邮编
			new SuperVO2DatasetSerializer().serialize(new SuperVO[] { psndocVO }, ds, Row.STATE_NORMAL);
			ds.setEnabled(enable);
			ds.setCurrentKey(Dataset.MASTER_KEY);
			ds.setRowSelectIndex(0);

			String addr = postcode + " " + countryname + " " + provincename + " " + cityname + " " + vsectionname + " "
					+ detailinfo;
			Row psn_row = ds.getSelectedRow();
			if (psn_row != null) {
				psn_row.setValue(ds.nameToIndex("addr_pk_address"), addr);
			}
			// FormComp psn_from = (FormComp)
			// viewMain.getViewComponents().getComponent("from_employ");
			// FormElement ele = psn_from.getElementById("censusaddr");
			// ele.setValue(addr);
			// 人员工作记录
			Dataset ds_psnjob = ViewUtil.getDataset(viewMain, "hi_psnjob_curr");
			PsnJobVO psnjobVO = getPsnjobVOByPk(pk);// getPsnjobVOByPk();
			new SuperVO2DatasetSerializer().serialize(new SuperVO[] { psnjobVO }, ds_psnjob, Row.STATE_NORMAL);
			ds_psnjob.setEnabled(enable);
			ds_psnjob.setCurrentKey(Dataset.MASTER_KEY);
			ds_psnjob.setRowSelectIndex(0);
			// 组织关系 hi_psnorg PsnOrgVO
			Dataset ds_psnorg = ViewUtil.getDataset(viewMain, "hi_psnorg");
			PsnOrgVO psnorgVO = getPsnorgVOByPk(pk);
			new SuperVO2DatasetSerializer().serialize(new SuperVO[] { psnorgVO }, ds_psnorg, Row.STATE_NORMAL);
			ds_psnorg.setEnabled(enable);
			ds_psnorg.setCurrentKey(Dataset.MASTER_KEY);
			ds_psnorg.setRowSelectIndex(0);
			// 试用信息 hi_psndoc_trial TrialVO hi_psndoc_trial
			Dataset ds_psntrial = ViewUtil.getDataset(viewMain, "hi_psndoc_trial");
			TrialVO trialVO = getTrialVOByPk(pk);
			// 判断是否有试用信息
			if (trialVO != null) {
				new SuperVO2DatasetSerializer().serialize(new SuperVO[] { trialVO }, ds_psntrial, Row.STATE_NORMAL);
				Row[] trialRow = ds_psntrial.getAllRow();
				if (trialRow != null) {
					for (Row row : trialRow) {
						if (StringUtils.isNotEmpty(row.getValue(ds_psntrial.nameToIndex("begindate")).toString())) {
							row.setValue(ds_psntrial.nameToIndex("status"), 1);
						}
					}
				}
				ds_psntrial.setEnabled(enable);// NC上的逻辑试用信息不允许修改
				ds_psntrial.setCurrentKey(Dataset.MASTER_KEY);
				ds_psntrial.setRowSelectIndex(0);
			} else {
				Row row = ds_psntrial.getEmptyRow();
				ds_psntrial.addRow(row);
				ds_psntrial.setEnabled(enable);// NC上的逻辑试用信息不允许修改
				ds_psntrial.setCurrentKey(Dataset.MASTER_KEY);
				ds_psntrial.setRowSelectIndex(0);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查询PsndocVO，暂时没用
	 * 
	 * @param pk_psndoc
	 * @return
	 */
	private PsndocVO getPsndocVOByPk(String pk_psndoc) {
		PsndocVO vo = null;
		try {
			vo = ServiceLocator.lookup(IPsndocQryservice.class).queryPsndocByPk(pk_psndoc);
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (HrssException e) {
			e.printStackTrace();
		}
		return vo;
	}

	/**
	 * 查询PsnorgVO
	 * 
	 * @param pk_psndoc
	 * @return
	 */
	private PsnOrgVO getPsnorgVOByPk(String pk_psndoc) {
		PsnOrgVO vo = null;
		String sql = "select * from hi_psnorg where pk_psndoc= '" + pk_psndoc + "'";// lastflag='Y'
		// sql=sql+"and hi_psnjob.pk_psndoc in ( select hi_entryapply.pk_psndoc from hi_entryapply where dr=0 and approve_state in (-1,3) ) ";
		LfwLogger.info(sql);
		try {
			ArrayList<PsnOrgVO> list = (ArrayList<PsnOrgVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(
					PsnOrgVO.class));
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					vo = list.get(0);
				}
			}
		} catch (DAOException e) {
			new HrssException(e).deal();
		}
		return vo;
	}

	/**
	 * 查询TrialVO （试用情况）
	 * 
	 * @param pk_psndoc
	 * @return
	 */
	private TrialVO getTrialVOByPk(String pk_psndoc) {
		TrialVO vo = null;
		String sql = "select * from hi_psndoc_trial where pk_psndoc= '" + pk_psndoc + "'";// lastflag='Y'
		LfwLogger.info(sql);
		try {
			ArrayList<TrialVO> list = (ArrayList<TrialVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(
					TrialVO.class));
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					vo = list.get(0);
				}
			}
		} catch (DAOException e) {
			new HrssException(e).deal();
		}
		return vo;
	}

	/**
	 * 获取最新一条任职记录
	 * 
	 * @param pk_psndoc
	 * @return
	 */
	private PsnJobVO getPsnjobVOByPk(String pk_psndoc) {
		PsnJobVO vo = null;
		String sql = "select * from hi_psnjob where pk_psndoc= '" + pk_psndoc + "' and lastflag='Y' and endflag='N' ";
		LfwLogger.info(sql);
		try {
			ArrayList<PsnJobVO> list = (ArrayList<PsnJobVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(
					PsnJobVO.class));
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					vo = list.get(0);
				}
			}
		} catch (DAOException e) {
			new HrssException(e).deal();
		}
		return vo;
	}

	/**
	 * 查询PsnorgVO
	 * 
	 * @param pk_psndoc
	 * @return
	 */
	private String getOrgGlbdef1ByPk(String pk_psndoc) {
		String orgglbdef1 = null;
		String sql = "select orgglbdef1,JOINSYSDATE from hi_psnorg where pk_psndoc= '" + pk_psndoc + "'";
		LfwLogger.info(sql);
		try {
			IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
			ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
			if (result != null && result.size() > 0) {
				Object[] obj = (Object[]) result.get(0);
				if (obj != null && obj[0] != null) {
					orgglbdef1 = obj[0].toString();
				} else {
					orgglbdef1 = obj[1].toString();
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return orgglbdef1;
	}

	/**
	 * 
	 * @param vo
	 * @param pk
	 * @param field
	 */
	private void updateOrgGlbdef1ByPk(SuperVO vo, String pk, String field) {
		IUAPQueryBS uapQry = null;
		try {
			// 需要更新的字段
			ArrayList<String> needUpdateFiledList = new ArrayList<String>();
			uapQry = ServiceLocator.lookup(IUAPQueryBS.class);
			SuperVO oldVO = (SuperVO) uapQry.retrieveByPK(vo.getClass(), pk);
			oldVO.setAttributeValue(field, vo.getAttributeValue(field));
			needUpdateFiledList.add(field);
			ServiceLocator.lookup(IPersistenceUpdate.class).updateVO(null, oldVO,
					needUpdateFiledList.toArray(new String[0]), null);
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (HrssException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查询身份证件 子集
	 * 
	 * @param id
	 * @param pk_psndoc
	 * @return
	 */
	private CertVO getCertVO(String id, String pk_psndoc) {
		CertVO vo = null;
		String sql = "select * from hi_psndoc_cert where pk_psndoc= '" + pk_psndoc + "' and id='" + id
				+ "' and iseffect='Y' and lastflag='Y'";//
		LfwLogger.info(sql);
		try {
			ArrayList<CertVO> list = (ArrayList<CertVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(
					CertVO.class));
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					vo = list.get(0);
				}
			}
		} catch (DAOException e) {
			new HrssException(e).deal();
		}
		return vo;
	}

	/**
	 * 获取地址中的主键
	 * 
	 * @param pk_address
	 * @return
	 */
	private HashMap<String, String> getAddress(String pk_address) {
		HashMap<String, String> addrMap = new HashMap<String, String>();
		String sql = "select country,province,city,vsection,detailinfo,postcode from bd_address where pk_address= '"
				+ pk_address + "'";
		LfwLogger.info(sql);
		try {
			IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
			ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
			if (result != null && result.size() > 0) {
				for (int i = 0; i < result.size(); i++) {
					Object[] obj = (Object[]) result.get(i);
					if (obj != null) {
						if (obj[0] != null && obj[0].toString() != null) {
							addrMap.put("pk_country", obj[0].toString());
						}
						if (obj[1] != null && obj[1].toString() != null) {
							addrMap.put("pk_province", obj[1].toString());
						}
						if (obj[2] != null && obj[2].toString() != null) {
							addrMap.put("pk_city", obj[2].toString());
						}
						if (obj[3] != null && obj[3].toString() != null) {
							addrMap.put("pk_vsection", obj[3].toString());
						}
						if (obj[4] != null && obj[4].toString() != null) {
							addrMap.put("pk_detailinfo", obj[4].toString());
						}
						if (obj[5] != null && obj[5].toString() != null) {
							addrMap.put("pk_postcode", obj[5].toString());
						}
					}

				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return addrMap;

	}

	/**
	 * 获取国家名称
	 * 
	 * @param pk_country
	 * @return
	 */
	private String getCountryName(String pk_country) {
		String countryname = "";
		String sql = "select name from bd_countryzone where pk_country= '" + pk_country + "'";
		LfwLogger.info(sql);
		try {
			IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
			ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
			if (result != null && result.size() > 0) {
				Object[] obj = (Object[]) result.get(0);
				if (obj != null && obj[0] != null) {
					countryname = obj[0].toString();
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return countryname;
	}

	/**
	 * 
	 * @param pk_country
	 * @return
	 */
	private String getRegionName(String pk_country) {
		String regionname = "";
		String sql = "select name from bd_region where pk_region = '" + pk_country + "'";
		LfwLogger.info(sql);
		try {
			IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
			ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
			if (result != null && result.size() > 0) {
				Object[] obj = (Object[]) result.get(0);
				if (obj != null && obj[0] != null) {
					regionname = obj[0].toString();
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return regionname;
	}
}
