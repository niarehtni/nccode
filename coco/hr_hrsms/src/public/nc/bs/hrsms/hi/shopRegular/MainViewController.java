package nc.bs.hrsms.hi.shopRegular;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.hrss.trn.TrnApplyBaseViewMain;
import nc.bs.hrss.trn.TrnUtil;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.trn.transmng.StapplyVO;
import nc.bs.hrsms.hi.HiApplyBaseViewMain;
import nc.bs.hrsms.trn.regular.lsnr.ShopRegularApplyAddProcessor;
import nc.bs.hrsms.trn.regular.lsnr.ShopRegularApplySaveProcessor;

public class MainViewController extends HiApplyBaseViewMain {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Override
	protected String getBillType() {
		return PsnApplyConsts.REGULAR_BILLTYPE_CODE;
	}

	@Override
	protected String getDatasetId() {
		return PsnApplyConsts.REGULAR_DS_ID;
	}

	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return PsnApplyConsts.REGULAR_AGGVOCLASS;
	}

	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return ShopRegularApplySaveProcessor.class;
	}

	public void onDataLoad(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		Dataset ds = dataLoadEvent.getSource();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		/** 人员信息 */
		FormComp frmPsnInfo = (FormComp) widget.getViewComponents().getComponent("headTab_card_psninfo_form");
		// 延期转正日期可编辑(和数据相关)
		Integer trialresult = (Integer) row.getValue(ds.nameToIndex(RegapplyVO.TRIALRESULT));
		if (trialresult != null && TRNConst.TRIALRESULT_DELAY == trialresult) {// 试用结果－延长试用期限
			frmPsnInfo.getElementById(RegapplyVO.TRIALDELAYDATE).setEnabled(true);
		} else {
			frmPsnInfo.getElementById(RegapplyVO.TRIALDELAYDATE).setEnabled(false);
		}
		// 根据情况设置流程类型是否可以编辑，如果true，则是走审批流，则需要进行设置，将流程类型编辑状态放开
		Boolean TRANS_TYPE = false;
		if ((Boolean) AppLifeCycleContext.current().getApplicationContext().getAppAttribute("TRANS_TYPE") != null) {
			TRANS_TYPE = (Boolean) AppLifeCycleContext.current().getApplicationContext().getAppAttribute("TRANS_TYPE");
		}
		if (TRANS_TYPE) {
			FormComp frm = (FormComp) widget.getViewComponents().getComponent(PsnApplyConsts.REGULAR_FORM_BILLINFO);
			FormElement business_type_businame = frm.getElementById(TaApplyConsts.TRANS_TYPE);
			business_type_businame.setEnabled(true);
		}
		// 根据情况设置单据申请编号是否可以编辑，如果false，则是自动生成申请编号，不需要特殊设置，否则可编辑状态就要放开
		Boolean AutoGenerateBillCode = false;
		if ((Boolean) AppLifeCycleContext.current().getApplicationContext().getAppAttribute("AutoGenerateBillCode") != null) {
			AutoGenerateBillCode = (Boolean) AppLifeCycleContext.current().getApplicationContext()
					.getAppAttribute("AutoGenerateBillCode");
		}
		if (AutoGenerateBillCode) {
			FormComp frm = (FormComp) widget.getViewComponents().getComponent(PsnApplyConsts.REGULAR_FORM_BILLINFO);
			FormElement bill_code = frm.getElementById(TaApplyConsts.BILL_CODE);
			bill_code.setEnabled(true);

		}

		FormComp frmNewInfo = (FormComp) widget.getViewComponents().getComponent(getNewFormId());
		if (!StringUtils.isEmpty(row.getString(ds.nameToIndex(StapplyVO.NEWPK_POST)))) {
			// 岗位不为空时，岗位序列不可编辑v631加上去的
			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_POSTSERIES)).setEnabled(false);
		}

		if (!StringUtils.isEmpty(row.getString(ds.nameToIndex(StapplyVO.NEWPK_JOB)))) {
			// 职务不为空时，职务类别不可编辑
			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWSERIES)).setEnabled(false);
		}
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		String operateStatus = (String) appCtx.getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateStatus)) {
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_POST), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_POST), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_POSTSERIES), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_POSTSERIES), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOB), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOB), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOB_TYPE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOB_TYPE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOBGRADE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOBGRADE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOBRANK), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOBRANK), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_PSNCL), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_PSNCL), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDDEPOSEMODE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWDEPOSEMODE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDJOBMODE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWJOBMODE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDMEMO), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWMEMO), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDOCCUPATION), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWOCCUPATION), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOB_TYPE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOB_TYPE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDPOSTSTAT), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWPOSTSTAT), null);
			row.setValue(ds.nameToIndex(RegapplyVO.OLDWORKTYPE), null);
			row.setValue(ds.nameToIndex(RegapplyVO.NEWWORKTYPE), null);
			row.setValue(ds.nameToIndex("newjobglbdef5"), null);
			row.setValue(ds.nameToIndex("oldjobglbdef5"), null);
			row.setValue(ds.nameToIndex(RegapplyVO.ASSGID), "1");
		}
	}

	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return ShopRegularApplyAddProcessor.class;
	}

	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		int filedColIndex = datasetCellEvent.getColIndex();
		SessionBean session = SessionUtil.getSessionBean();
		if (filedColIndex == ds.nameToIndex(RegapplyVO.PK_PSNDOC)) {
			String pk_psndoc = (String) row.getValue(ds.nameToIndex(RegapplyVO.PK_PSNDOC));
			// 设置人员是否有试用信息，没有不能赋值。有的话带出试用信息
			try {
				validate(pk_psndoc);
				String sysql = "select * from HI_PSNDOC_TRIAL where pk_psndoc='" + pk_psndoc + "'";
				ArrayList<TrialVO> al = (ArrayList<TrialVO>) new BaseDAO().executeQuery(sysql, new BeanListProcessor(
						TrialVO.class));
				if (al.size() >= 1) {
					row.setInt(ds.nameToIndex(RegapplyVO.PROBATION_TYPE), al.get(0).getTrial_type());
					row.setValue(ds.nameToIndex(RegapplyVO.BEGIN_DATE), al.get(0).getBegindate());
					row.setValue(ds.nameToIndex(RegapplyVO.END_DATE), al.get(0).getEnddate());
					row.setValue(ds.nameToIndex(RegapplyVO.MEMO), al.get(0).getMemo());
				}
				// 转正人变动，信息带出（组织、部门、岗位等等）
				String sql = " pk_psndoc='" + pk_psndoc + "' and ismainjob='Y' and lastflag='Y' ";
				PsnJobVO vo[] = NCLocator.getInstance().lookup(IPsndocQryService.class).queryPsnJobs(sql);
				if (vo.length > 0) {
					row.setValue(ds.nameToIndex(RegapplyVO.PK_PSNJOB), vo[0].getPk_psnjob());
					row.setValue(ds.nameToIndex(RegapplyVO.PK_PSNORG), vo[0].getPk_psnorg());
					// 组织oldpk_org_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_ORG), vo[0].getPk_org());
					// 部门oldpk_dept_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_DEPT), vo[0].getPk_dept());
					// 岗位oldpk_post_postname
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_POST), vo[0].getPk_post());
					// 岗位序列oldpk_postseries_postseriesname
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_POSTSERIES), vo[0].getPk_postseries());
					// 职务oldpk_job_jobname
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOB), vo[0].getPk_job());
					// 职务序列oldseries_jobtypename
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOB_TYPE), vo[0].getPk_job_type());
					// 职级oldpk_jobgrade_jobgradename
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOBGRADE), vo[0].getPk_jobgrade());
					// 职等oldpk_jobrank_jobrankname
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOBRANK), vo[0].getPk_jobrank());
					// 人员类别oldpk_psncl_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_PSNCL), vo[0].getPk_psncl());
					// 免职方式olddeposemode_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDDEPOSEMODE), vo[0].getDeposemode());
					// 任职方式oldjobmode_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDJOBMODE), vo[0].getJobmode());
					// 备注oldmemo
					row.setValue(ds.nameToIndex(RegapplyVO.OLDMEMO), vo[0].getMemo());
					// 职业oldoccupation_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDOCCUPATION), vo[0].getOccupation());
					// 任职类型oldpk_job_type_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPK_JOB_TYPE), vo[0].getPk_job_type());
					// 在岗oldpoststat
					row.setValue(ds.nameToIndex(RegapplyVO.OLDPOSTSTAT), vo[0].getPoststat());
					// 工种oldworktype_name
					row.setValue(ds.nameToIndex(RegapplyVO.OLDWORKTYPE), vo[0].getWorktype());

					// 组织newpk_org_name
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_ORG), vo[0].getPk_org());
					// 部门newpk_dept_name
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_DEPT), vo[0].getPk_dept());
					// 岗位newpk_post_postname
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_POST), vo[0].getPk_post());
					// 岗位序列newpk_postseries_postseriesname
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_POSTSERIES), vo[0].getPk_postseries());
					// 职务newpk_job_jobname
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOB), vo[0].getPk_job());
					// 职务序列newseries_jobtypename
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOB_TYPE), vo[0].getPk_job_type());
					// 职级newpk_jobgrade_jobgradename
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOBGRADE), vo[0].getPk_jobgrade());
					// 职等newpk_jobrank_jobrankname
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOBRANK), vo[0].getPk_jobrank());
					// 人员类别newpk_psncl_name
					// row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_PSNCL),vo[0].getPk_psncl());

					// 免职方式newdeposemode_name
					row.setValue(ds.nameToIndex(RegapplyVO.NEWDEPOSEMODE), vo[0].getDeposemode());
					// 任职方式newjobmode_name
					row.setValue(ds.nameToIndex(RegapplyVO.NEWJOBMODE), vo[0].getJobmode());
					// 备注newmemo
					row.setValue(ds.nameToIndex(RegapplyVO.NEWMEMO), vo[0].getMemo());
					// 职业newoccupation_name
					row.setValue(ds.nameToIndex(RegapplyVO.NEWOCCUPATION), vo[0].getOccupation());
					// 任职类型newpk_job_type_name
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPK_JOB_TYPE), vo[0].getPk_job_type());
					// 在岗newpoststat
					row.setValue(ds.nameToIndex(RegapplyVO.NEWPOSTSTAT), vo[0].getPoststat());
					// 工种newworktype_name
					row.setValue(ds.nameToIndex(RegapplyVO.NEWWORKTYPE), vo[0].getWorktype());
				}

			} catch (DAOException e1) {
				LfwLogger.error(e1.getMessage());
				throw new LfwRuntimeException(e1.getMessage(), e1.getCause());
			} catch (BusinessException e) {
				LfwLogger.error(e.getMessage());
				throw new LfwRuntimeException(e.getMessage(), e.getCause());
			}
		}

		// 转正后组织
		if (filedColIndex == ds.nameToIndex(RegapplyVO.NEWPK_ORG)) {
			session.setExtendAttribute(PsnApplyConsts.TRN_NEWPK_ORG, (String) datasetCellEvent.getNewValue());
		}
		// 转正后部门
		else if (filedColIndex == ds.nameToIndex(RegapplyVO.NEWPK_DEPT)) {
			session.setExtendAttribute(PsnApplyConsts.TRN_NEWPK_DEPT, (String) datasetCellEvent.getNewValue());
		}
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		if (filedColIndex == ds.nameToIndex(RegapplyVO.PROBATION_TYPE)) {
			Integer probation_type = (Integer) row.getInt(ds.nameToIndex(RegapplyVO.PROBATION_TYPE));
		}
		// 试用结果
		if (filedColIndex == ds.nameToIndex(RegapplyVO.TRIALRESULT)) {
			FormComp frmPsnInfo = (FormComp) widget.getViewComponents().getComponent(
					PsnApplyConsts.REGULAR_FORM_PSNINFO);
			Integer trialresult = row.getInt(ds.nameToIndex(RegapplyVO.TRIALRESULT));
			if (TRNConst.TRIALRESULT_DELAY == trialresult) {
				// 延长试用期限
				frmPsnInfo.getElementById(RegapplyVO.TRIALDELAYDATE).setEnabled(true);
				frmPsnInfo.getElementById(RegapplyVO.TRIALDELAYDATE).setRequired(true);
				FieldSet fieldSet = ds.getFieldSet();
				Field field = fieldSet.getField(RegapplyVO.TRIALDELAYDATE);
				if (field instanceof UnmodifiableMdField) {
					field = ((UnmodifiableMdField) field).getMDField();
				}
				fieldSet.updateField(field.getId(), field);
				field.setNullAble(false);
			} else {
				frmPsnInfo.getElementById(RegapplyVO.TRIALDELAYDATE).setEnabled(false);
				row.setValue(ds.nameToIndex(RegapplyVO.TRIALDELAYDATE), null);
				frmPsnInfo.getElementById(RegapplyVO.TRIALDELAYDATE).setRequired(false);
				FieldSet fieldSet = ds.getFieldSet();
				Field field = fieldSet.getField(RegapplyVO.TRIALDELAYDATE);
				if (field instanceof UnmodifiableMdField) {
					field = ((UnmodifiableMdField) field).getMDField();
				}
				fieldSet.updateField(field.getId(), field);
				field.setNullAble(true);
			}
		}
		// 调配的异动项目值变更
		if (filedColIndex != ds.nameToIndex(RegapplyVO.PK_PSNDOC)) {
			// 调配的异动项目值变更
			setTrnItemValueChange(widget, ds, row, filedColIndex);
		}

	}

	/**
	 * 效验
	 * 
	 * @param row
	 */
	private void validate(String pk_psndoc) {
		try {
			PsndocAggVO aggVO = ServiceLocator.lookup(IPsndocQryService.class).queryPsndocVOByPk(pk_psndoc);
			// 员工主职VO
			PsnJobVO psnJobVO = aggVO.getParentVO().getPsnJobVO();
			// SessionUtil.getPsndocVO().setPsnJobVO(psnJobVO);
			String where = " pk_psnorg = '" + psnJobVO.getPk_psnorg() + "' and lastflag = 'Y' ";
			TrialVO[] trialVOs = (TrialVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, TrialVO.class, where);
			if (!ArrayUtils.isEmpty(trialVOs)) {
				if (trialVOs[0].getTrialresult() != null && TRNConst.TRIALRESULT_FALL == trialVOs[0].getTrialresult()) {
					CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("c_hi-res", "提示信息")/* @res "提示信息" */, nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_hi-res", "已有转正未通过记录，不允许增加转正单据！")/*
																						 * @
																						 * res
																						 * "已有转正未通过记录，不允许增加转正单据！0c_trn-res0054"
																						 */);
					// throw new
					// LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0054")/*@res
					// "已有转正未通过记录，不允许增加转正单据！"*/);
				}
			}
			UFBoolean trial_flag = psnJobVO.getTrial_flag();
			Integer trial_type = psnJobVO.getTrial_type();

			if (trial_type == null || trial_flag == null || !trial_flag.booleanValue()) {
				CommonUtil.showErrorDialog(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "提示信息")/*
																								 * @
																								 * res
																								 * "提示信息"
																								 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "没有有效的试用信息!")/*
																										 * @
																										 * res
																										 * "0c_trn-res0015"
																										 */);
				// throw new
				// LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0015")/*@res
				// "没有有效的试用信息!"*/);
			}

			// SuperVO[] itemvos = TrnUtil.getTrnItems(TRNConst.REGITEM_BEANID,
			// trial_type, SessionUtil.getPk_group(), SessionUtil.getPk_org());
			// if(ArrayUtils.isEmpty(itemvos))
			// {
			// CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","提示信息")/*@res
			// "提示信息"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0016")/*@res
			// "未设置转正项目，不能填写转正申请单！"*/);
			// // throw new
			// LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0016")/*@res
			// "未设置转正项目，不能填写转正申请单！"*/);
			// }
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
	}

	@Override
	protected SuperVO[] getTrnItems(Dataset dsMaster, Row row) throws BusinessException, HrssException {
		Integer probation_type = (Integer) 1;// row.getValue(dsMaster
		// .nameToIndex(RegapplyVO.PROBATION_TYPE));
		String pk_group = row.getString(dsMaster.nameToIndex(RegapplyVO.PK_GROUP));// 单据所属集团
		String pk_org = row.getString(dsMaster.nameToIndex(RegapplyVO.PK_ORG));// 单据所属组织
		SuperVO[] itemvos = TrnUtil.getTrnItems(TRNConst.REGITEM_BEANID, probation_type, pk_group, pk_org);
		return itemvos;
	}

}
