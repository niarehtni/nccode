package nc.bs.hrsms.hi.employ.ShopDimission;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.hi.HiApplyBaseViewMain;
import nc.bs.hrsms.hi.employ.ShopDimission.lsnr.ShopDimissionApplyAddProcessor;
import nc.bs.hrsms.hi.employ.ShopDimission.lsnr.ShopDimissionApplySaveProcessor;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.trn.transmng.StapplyVO;
public class DimissionCardMainView extends HiApplyBaseViewMain {
  @SuppressWarnings("unused")
private static final long serialVersionUID=1L;
  @Override protected String getBillType(){
    return PsnApplyConsts.DIMISSION_BILLTYPE_CODE;
  }
  @Override protected String getDatasetId(){
    return PsnApplyConsts.TRANSFER_DS_ID;
  }
  @Override protected Class<? extends AggregatedValueObject> getAggVoClazz(){
    return PsnApplyConsts.TRANSFER_AGGVOCLASS;
  }
  @Override protected Class<? extends ISaveProcessor> getSavePrcss(){
    return ShopDimissionApplySaveProcessor.class;
  }
  public void onDataLoad(  DataLoadEvent dataLoadEvent){
    super.onDataLoad(dataLoadEvent);
		Dataset ds = dataLoadEvent.getSource();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		String pk_old_hi_org = row.getString(ds
				.nameToIndex(StapplyVO.PK_OLD_HI_ORG));
		String pk_hi_org = row.getString(ds.nameToIndex(StapplyVO.PK_HI_ORG));
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		FormComp frmNewOrg = (FormComp) widget.getViewComponents()
				.getComponent(PsnApplyConsts.TRANSFER_FORM_NEWORGS);
		FormElement pk_hi_org_ele = frmNewOrg
				.getElementById(StapplyVO.PK_HI_ORG + PsnApplyConsts.REF_SUFFIX);

		if (pk_old_hi_org != null && pk_old_hi_org.equals(pk_hi_org)) {
			pk_hi_org_ele.setEnabled(false);
		} else {
			pk_hi_org_ele.setEnabled(true);
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
			row.setValue(ds.nameToIndex(StapplyVO.FUN_CODE), "60090dimissionapply");
			row.setValue(ds.nameToIndex(StapplyVO.ASSGID), "1");
		}
  }
  @Override protected Class<? extends IAddProcessor> getAddPrcss(){
    return ShopDimissionApplyAddProcessor.class;
  }
  public void onAfterDataChange(  DatasetCellEvent datasetCellEvent){
    Dataset ds = datasetCellEvent.getSource();
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		int filedColIndex = datasetCellEvent.getColIndex();
		SessionBean session = SessionUtil.getSessionBean();
		if (filedColIndex == ds.nameToIndex(StapplyVO.PK_PSNDOC)){
			//调配人变动，信息带出（组织、部门、岗位等等）
			String pk_psndoc = (String) row.getValue(ds.nameToIndex(StapplyVO.PK_PSNDOC));
			// 2016-04-20 zhangjie 添加条件and poststat = 'Y'
			String sql = " pk_psndoc='"+pk_psndoc+"' and ismainjob='Y' and lastflag='Y' and poststat = 'Y' ";
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
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
//			CommonUtil.showShortMessage("新入人员");
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
		setTrnItemValueChange(widget, ds, row, filedColIndex);
  }
  	// add by shaochj Oct 12, 2015  begin
  	@Override
	public void onBeforeShow(DialogEvent dialogEvent) {
		// TODO Auto-generated method stub
		super.onBeforeShow(dialogEvent);
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		FormComp frmNewOrg = (FormComp) widget.getViewComponents()
				.getComponent("headTab_card_after_form");
		List<FormElement> eleList = frmNewOrg.getElementList();
		if(eleList!=null && eleList.size()>0){
			for(int i=0;i<eleList.size();i++){
				FormElement ele = eleList.get(i);
				ele.setEditable(false);
			}
		}
  	}
	// add by shaochj Oct 12, 2015 end
	
  
  
}
