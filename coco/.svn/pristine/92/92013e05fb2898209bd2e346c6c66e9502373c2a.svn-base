package nc.bs.hrsms.hi.employ.ShopTransfer;

import org.apache.commons.lang.StringUtils;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.hi.HiApplyBaseViewMain;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.hrss.trn.TrnUtil;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.AppInteractionUtil;
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
import nc.uap.lfw.core.page.LfwView;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.trn.transmng.StapplyVO;
import nc.bs.hrsms.hi.employ.ShopTransfer.lsnr.TransferApplySaveProcessor;
import nc.bs.hrsms.hi.employ.ShopTransfer.lsnr.ShopTransferApplyAddProcessor;

public class TransferCardMainView extends HiApplyBaseViewMain{
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Override
	protected String getBillType() {
		return PsnApplyConsts.TRANSFER_BILLTYPE_CODE;
	}

	@Override
	protected String getDatasetId() {
		return PsnApplyConsts.TRANSFER_DS_ID;
	}

	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return PsnApplyConsts.TRANSFER_AGGVOCLASS;
	}

	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return TransferApplySaveProcessor.class;
	}

	public void onDataLoad(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		Dataset ds = dataLoadEvent.getSource();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		/** 设置试用相关 */
		UFBoolean trial_flag = (UFBoolean) row.getValue(ds
				.nameToIndex(StapplyVO.TRIAL_FLAG));
		setTrialInfo(trial_flag.toString(), ds, row, widget);
		/** 调配后管理组织 */
		Integer stapply_mode = (Integer) row.getValue(ds
				.nameToIndex(StapplyVO.STAPPLY_MODE));
		FormComp frmQrgNew = (FormComp) widget.getViewComponents()
				.getComponent(PsnApplyConsts.TRANSFER_FORM_NEWORGS);
		FormElement orgNewE = frmQrgNew.getElementById(StapplyVO.PK_HI_ORG
				+ HrssConsts.REF_SUFFIX);
		if (stapply_mode == TRNConst.TRANSMODE_CROSS_OUT) {
			// 调配方式为调出时,可编辑调配后HR组织可编辑
			FieldSet fieldSet = ds.getFieldSet();
			Field field = fieldSet.getField(StapplyVO.PK_HI_ORG);
			if(field instanceof UnmodifiableMdField){
				field = ((UnmodifiableMdField) field).getMDField();
			}
			fieldSet.updateField(field.getId(), field);	
			field.setNullAble(false);
			orgNewE.setEnabled(true);
			orgNewE.setRequired(true);
		}
		// 根据情况设置流程类型是否可以编辑，如果true，则是走审批流，则需要进行设置，将流程类型编辑状态放开
		Boolean TRANS_TYPE = false;
		if ((Boolean) AppLifeCycleContext.current().getApplicationContext()
				.getAppAttribute("TRANS_TYPE") != null) {
			TRANS_TYPE = (Boolean) AppLifeCycleContext.current()
					.getApplicationContext().getAppAttribute("TRANS_TYPE");
		}
		if (TRANS_TYPE) {
			FormComp frm = (FormComp) widget.getViewComponents().getComponent(
					PsnApplyConsts.TRANSFER_FORM_BILLINFO);
			FormElement business_type_businame = frm
					.getElementById(TaApplyConsts.TRANS_TYPE);
			business_type_businame.setEnabled(true);
		}
		// 根据情况设置单据申请编号是否可以编辑，如果false，则是自动生成申请编号，不需要特殊设置，否则可编辑状态就要放开
		Boolean AutoGenerateBillCode = false;
		if ((Boolean) AppLifeCycleContext.current().getApplicationContext()
				.getAppAttribute("AutoGenerateBillCode") != null) {
			AutoGenerateBillCode = (Boolean) AppLifeCycleContext.current()
					.getApplicationContext()
					.getAppAttribute("AutoGenerateBillCode");
		}
		if (AutoGenerateBillCode) {
			FormComp frm = (FormComp) widget.getViewComponents().getComponent(
					PsnApplyConsts.TRANSFER_FORM_BILLINFO);
			FormElement bill_code = frm.getElementById(TaApplyConsts.BILL_CODE);
			bill_code.setEnabled(true);

		}
		
		FormComp frmNewInfo = (FormComp) widget.getViewComponents().getComponent(getNewFormId());
		if(!StringUtils.isEmpty(row.getString(ds.nameToIndex(StapplyVO.NEWPK_POST)))){
			// 岗位不为空时，岗位序列不可编辑v631加上去的
			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_POSTSERIES)).setEnabled(false);
		}
		
		if(!StringUtils.isEmpty(row.getString(ds.nameToIndex(StapplyVO.NEWPK_JOB)))){
			// 职务不为空时，职务类别不可编辑
			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWSERIES)).setEnabled(false);
		}
		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		String operateStatus = (String) appCtx.getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
		if (HrssConsts.POPVIEW_OPERATE_ADD.equals(operateStatus)) {
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_POST), null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POST), null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_POSTSERIES),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POSTSERIES),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOB),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOB_TYPE),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB_TYPE),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOBGRADE),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOBRANK),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBRANK),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_PSNCL),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_PSNCL),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDDEPOSEMODE),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWDEPOSEMODE),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDJOBMODE),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWJOBMODE),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDMEMO),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWMEMO),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDOCCUPATION),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWOCCUPATION),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOB_TYPE),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB_TYPE),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDPOSTSTAT),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWPOSTSTAT),null);
			row.setValue(ds.nameToIndex(StapplyVO.OLDWORKTYPE),null);
			row.setValue(ds.nameToIndex(StapplyVO.NEWWORKTYPE),null);
			row.setValue(ds.nameToIndex(StapplyVO.FUN_CODE), "60090transapply");
			row.setValue(ds.nameToIndex(StapplyVO.ASSGID), "1");
		}
	}

	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return ShopTransferApplyAddProcessor.class;
	}

	public void onAfterDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		int filedColIndex = datasetCellEvent.getColIndex();
		Row row = ds.getSelectedRow();
		LfwView widget = AppLifeCycleContext.current().getViewContext()
				.getView();
		UFBoolean trial_flag = row.getUFBoolean(ds
				.nameToIndex(StapplyVO.TRIAL_FLAG));
		SessionBean session = SessionUtil.getSessionBean();
		if (filedColIndex == ds.nameToIndex(StapplyVO.PK_PSNDOC)){
			//调配人变动，信息带出（组织、部门、岗位等等）
			String pk_psndoc = (String) row.getValue(ds.nameToIndex(StapplyVO.PK_PSNDOC));
			String sql = " pk_psndoc='"+pk_psndoc+"' and ismainjob='Y' and lastflag='Y' ";
			try {
				PsnJobVO vo[] = NCLocator.getInstance().lookup(IPsndocQryService.class).queryPsnJobs(sql);
				if(vo.length > 0){
					row.setValue(ds.nameToIndex(StapplyVO.PK_PSNJOB), vo[0].getPk_psnjob());
					row.setValue(ds.nameToIndex(StapplyVO.PK_PSNORG), vo[0].getPk_psnorg());
					//组织oldpk_org_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_ORG),vo[0].getPk_org());
					//部门oldpk_dept_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_DEPT),vo[0].getPk_dept());
					//岗位oldpk_post_postname
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_POST),vo[0].getPk_post());
					//岗位序列oldpk_postseries_postseriesname
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_POSTSERIES),vo[0].getPk_postseries());
					//职务oldpk_job_jobname
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOB),vo[0].getPk_job());
					//职务序列oldseries_jobtypename
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOB_TYPE),vo[0].getPk_job_type());
					//职级oldpk_jobgrade_jobgradename
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOBGRADE),vo[0].getPk_jobgrade());
					//职等oldpk_jobrank_jobrankname
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOBRANK),vo[0].getPk_jobrank());
					//人员类别oldpk_psncl_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_PSNCL),vo[0].getPk_psncl());
					//免职方式olddeposemode_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDDEPOSEMODE),vo[0].getDeposemode());
					//任职方式oldjobmode_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDJOBMODE),vo[0].getJobmode());
					//备注oldmemo
					row.setValue(ds.nameToIndex(StapplyVO.OLDMEMO),vo[0].getMemo());
					//职业oldoccupation_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDOCCUPATION),vo[0].getOccupation());
					//任职类型oldpk_job_type_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDPK_JOB_TYPE),vo[0].getPk_job_type());
					//在岗oldpoststat
					row.setValue(ds.nameToIndex(StapplyVO.OLDPOSTSTAT),vo[0].getPoststat());
					//工种oldworktype_name
					row.setValue(ds.nameToIndex(StapplyVO.OLDWORKTYPE),vo[0].getWorktype());
					
					//组织newpk_org_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_ORG),vo[0].getPk_org());
					//部门newpk_dept_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_DEPT),vo[0].getPk_dept());
					//岗位newpk_post_postname
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POST),vo[0].getPk_post());
					//岗位序列newpk_postseries_postseriesname
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POSTSERIES),vo[0].getPk_postseries());
					//职务newpk_job_jobname
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB),vo[0].getPk_job());
					//职务序列newseries_jobtypename
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB_TYPE),vo[0].getPk_job_type());
					//职级newpk_jobgrade_jobgradename
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE),vo[0].getPk_jobgrade());
					//职等newpk_jobrank_jobrankname
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBRANK),vo[0].getPk_jobrank());
					//人员类别newpk_psncl_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_PSNCL),vo[0].getPk_psncl());
					//免职方式newdeposemode_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWDEPOSEMODE),vo[0].getDeposemode());
					//任职方式newjobmode_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWJOBMODE),vo[0].getJobmode());
					//备注newmemo
					row.setValue(ds.nameToIndex(StapplyVO.NEWMEMO),vo[0].getMemo());
					//职业newoccupation_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWOCCUPATION),vo[0].getOccupation());
					//任职类型newpk_job_type_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB_TYPE),vo[0].getPk_job_type());
					//在岗newpoststat
					row.setValue(ds.nameToIndex(StapplyVO.NEWPOSTSTAT),vo[0].getPoststat());
					//工种newworktype_name
					row.setValue(ds.nameToIndex(StapplyVO.NEWWORKTYPE),vo[0].getWorktype());
				}
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
		if (filedColIndex == ds.nameToIndex(StapplyVO.TRIAL_FLAG)) {
			if (validateExistTrail() > 0 && trial_flag.booleanValue()) {
				row.setValue(ds.nameToIndex(StapplyVO.TRIAL_FLAG),
						UFBoolean.FALSE);
				AppInteractionUtil.showMessageDialog(ResHelper.getString(
						"c_hi-res", "0c_trn-res0051")/**
				 * @res
				 *      "存在未结束的试用记录,不能勾选试用标志."
				 */
				);
				return;
			}
			// 设置试用相关的设置
			setTrialInfo(String.valueOf(datasetCellEvent.getNewValue()), ds,
					row, widget);
		}
		if (filedColIndex == ds.nameToIndex(StapplyVO.EFFECTDATE)) {
			// 生效日期与开始日期相同
			UFLiteralDate effectdate = (UFLiteralDate) row.getValue(ds
					.nameToIndex(StapplyVO.EFFECTDATE));
			UFLiteralDate beginDate = (UFLiteralDate) row.getValue(ds
					.nameToIndex(StapplyVO.TRIALBEGINDATE));
			if (trial_flag.booleanValue() && effectdate != null
					&& beginDate == null)
				row.setValue(ds.nameToIndex(StapplyVO.TRIALBEGINDATE),
						effectdate);
		}
		if (trial_flag.toString().equals("Y")
				&& (filedColIndex == ds.nameToIndex(StapplyVO.TRIALDAYS)
						|| filedColIndex == ds
								.nameToIndex(StapplyVO.TRIALBEGINDATE) || filedColIndex == ds
						.nameToIndex(StapplyVO.TRIAL_UNIT))) {
			// 生效日期与开始日期相同
			UFLiteralDate beginDate = (UFLiteralDate) row.getValue(ds
					.nameToIndex(StapplyVO.TRIALBEGINDATE));
			UFLiteralDate endDate0 = (UFLiteralDate) row.getValue(ds
					.nameToIndex(StapplyVO.TRIALENDDATE));
			// 试用天数或月数
			Object object = row.getValue(ds.nameToIndex(StapplyVO.TRIALDAYS));
			Integer trial_unit = (Integer) row.getValue(ds
					.nameToIndex(StapplyVO.TRIAL_UNIT));
			Integer days = null;
			if (object != null) {
				days = row.getInt(ds.nameToIndex(StapplyVO.TRIALDAYS));
			}
			UFLiteralDate endDate1 = null;
			if (beginDate != null && days != null && trial_unit != null) {
				endDate1 = TrnUtil.getDateAfterMonth(beginDate,
						days.intValue(), trial_unit);
				if (endDate0 == null || endDate0.compareTo(endDate1) != 0)
					row.setValue(ds.nameToIndex(StapplyVO.TRIALENDDATE),
							endDate1);
			}
		}
		// 转正后组织
		if (filedColIndex == ds.nameToIndex(RegapplyVO.NEWPK_ORG)) {
			session.setExtendAttribute(PsnApplyConsts.TRN_NEWPK_ORG,
					(String) datasetCellEvent.getNewValue());
		}
		// 转正后部门
		else if (filedColIndex == ds.nameToIndex(RegapplyVO.NEWPK_DEPT)) {
			session.setExtendAttribute(PsnApplyConsts.TRN_NEWPK_DEPT,
					(String) datasetCellEvent.getNewValue());
		}
		// 调配的异动项目值变更
		if (filedColIndex != ds.nameToIndex(StapplyVO.PK_PSNDOC)){
			// 调配的异动项目值变更
						setTrnItemValueChange(widget, ds, row, filedColIndex);
					}

	}

	private int validateExistTrail() {
		String pk_psnjob = SessionUtil.getPk_psnjob();
		String cond = " pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psnjob ='"
				+ pk_psnjob + "') and endflag <> 'Y'";
		try {
			return ServiceLocator.lookup(IPersistenceRetrieve.class)
					.getCountByCondition(TrialVO.getDefaultTableName(), cond);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.deal();
		}
		return 0;
	}

	/**
	 * 设置试用相关的设置
	 * 
	 * @param trial_flag
	 * @param ds
	 * @param row
	 * @param widget
	 */
	private void setTrialInfo(String trial_flag, Dataset ds, Row row,
			LfwView widget) {
		/** 人员信息 */
		FormComp frmPsnInfo = (FormComp) widget.getViewComponents()
				.getComponent(PsnApplyConsts.REGULAR_FORM_PSNINFO);
		if (UFBoolean.TRUE.toString().equals(trial_flag)) {
			FieldSet fiedset = ds.getFieldSet();
			// 岗位试用期
			Field field_trialdays = fiedset.getField(StapplyVO.TRIALDAYS);
			if (field_trialdays instanceof UnmodifiableMdField) {
				field_trialdays = ((UnmodifiableMdField) field_trialdays).getMDField();
				fiedset.updateField(field_trialdays.getId(), field_trialdays);
			}
			field_trialdays.setNullAble(false);
			frmPsnInfo.getElementById(StapplyVO.TRIALDAYS).setEnabled(true);
			frmPsnInfo.getElementById(StapplyVO.TRIALDAYS).setRequired(true);
			// 岗位试用期限单位
			Field field_trial_unit = fiedset.getField(StapplyVO.TRIAL_UNIT);
			if (field_trial_unit instanceof UnmodifiableMdField) {
				field_trial_unit = ((UnmodifiableMdField) field_trial_unit).getMDField();
				fiedset.updateField(field_trial_unit.getId(), field_trial_unit);
			}
			field_trial_unit.setNullAble(false);
			row.setValue(ds.nameToIndex(StapplyVO.TRIAL_UNIT), 2);
			frmPsnInfo.getElementById(StapplyVO.TRIAL_UNIT).setEnabled(true);
			frmPsnInfo.getElementById(StapplyVO.TRIAL_UNIT).setRequired(true);
			// 试用开始日期
			Field field_trialbegindate = fiedset.getField(StapplyVO.TRIALBEGINDATE);
			if (field_trialbegindate instanceof UnmodifiableMdField) {
				field_trialbegindate = ((UnmodifiableMdField) field_trialbegindate).getMDField();
				fiedset.updateField(field_trialbegindate.getId(), field_trialbegindate);
			}
			field_trialbegindate.setNullAble(false);
			frmPsnInfo.getElementById(StapplyVO.TRIALBEGINDATE)
					.setEnabled(true);
			frmPsnInfo.getElementById(StapplyVO.TRIALBEGINDATE).setRequired(
					true);
			row.setValue(ds.nameToIndex(StapplyVO.TRIALBEGINDATE),
					row.getValue(ds.nameToIndex(StapplyVO.EFFECTDATE)));
			// 试用结束日期
			Field field_trialenddate = fiedset.getField(StapplyVO.TRIALENDDATE);
			if (field_trialenddate instanceof UnmodifiableMdField) {
				field_trialenddate = ((UnmodifiableMdField) field_trialenddate).getMDField();
				fiedset.updateField(field_trialenddate.getId(), field_trialenddate);
			}
			field_trialenddate.setNullAble(false);
			frmPsnInfo.getElementById(StapplyVO.TRIALENDDATE).setEnabled(true);
			frmPsnInfo.getElementById(StapplyVO.TRIALENDDATE).setRequired(true);
		} else {
			if (row != null) {
				row.setValue(ds.nameToIndex(StapplyVO.TRIALDAYS), null);
				row.setValue(ds.nameToIndex(StapplyVO.TRIALBEGINDATE), null);
				row.setValue(ds.nameToIndex(StapplyVO.TRIALENDDATE), null);
				row.setValue(ds.nameToIndex(StapplyVO.TRIAL_UNIT), null);
			}
			FieldSet fiedset = ds.getFieldSet();
			// 岗位试用期
			Field field_trialdays = fiedset.getField(StapplyVO.TRIALDAYS);
			if (field_trialdays instanceof UnmodifiableMdField) {
				field_trialdays = ((UnmodifiableMdField) field_trialdays).getMDField();
				fiedset.updateField(field_trialdays.getId(), field_trialdays);
			}
			field_trialdays.setNullAble(true);
			frmPsnInfo.getElementById(StapplyVO.TRIALDAYS).setEnabled(false);
			frmPsnInfo.getElementById(StapplyVO.TRIALDAYS).setRequired(false);
			// 岗位试用期限单位
			Field field_trial_unit = fiedset.getField(StapplyVO.TRIAL_UNIT);
			if (field_trial_unit instanceof UnmodifiableMdField) {
				field_trial_unit = ((UnmodifiableMdField) field_trial_unit).getMDField();
				fiedset.updateField(field_trial_unit.getId(), field_trial_unit);
			}
			field_trial_unit.setNullAble(true);
			frmPsnInfo.getElementById(StapplyVO.TRIAL_UNIT).setEnabled(false);
			frmPsnInfo.getElementById(StapplyVO.TRIAL_UNIT).setRequired(false);
			// 试用开始日期
			Field field_trialbegindate = fiedset.getField(StapplyVO.TRIALBEGINDATE);
			if (field_trialbegindate instanceof UnmodifiableMdField) {
				field_trialbegindate = ((UnmodifiableMdField) field_trialbegindate).getMDField();
				fiedset.updateField(field_trialbegindate.getId(), field_trialbegindate);
			}
			field_trialbegindate.setNullAble(true);
			frmPsnInfo.getElementById(StapplyVO.TRIALBEGINDATE).setEnabled(
					false);
			frmPsnInfo.getElementById(StapplyVO.TRIALBEGINDATE).setRequired(
					false);
			// 试用结束日期
			Field field_trialenddate = fiedset.getField(StapplyVO.TRIALENDDATE);
			if (field_trialenddate instanceof UnmodifiableMdField) {
				field_trialenddate = ((UnmodifiableMdField) field_trialenddate).getMDField();
				fiedset.updateField(field_trialenddate.getId(), field_trialenddate);
			}
			field_trialenddate.setNullAble(true);
			frmPsnInfo.getElementById(StapplyVO.TRIALENDDATE).setEnabled(false);
			frmPsnInfo.getElementById(StapplyVO.TRIALENDDATE)
					.setRequired(false);
		}
	}
}
