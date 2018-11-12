package nc.bs.hrsms.hi;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.pf.PFApprovePageMode;
import nc.bs.hrss.pub.pf.ctrl.WebBillApproveView;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.bs.hrss.trn.budgetcheck.BudgetCheckView;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hi.IRptQueryService;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.pub.tools.HiSQLHelper;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.repdef.RepDefVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.JobLevelVO;
import nc.vo.om.post.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.trn.transmng.StapplyVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 调配审批的共通View
 *
 * @author qiaoxp
 *
 */
public class HiApproveView extends WebBillApproveView {

	// 变动前项目
	public static String FORM_TRN_OLD = "headTab_card_before_form";
	// 变动后项目
	public static String FORM_TRN_NEW = "headTab_card_after_form";

	private SimpleDocServiceTemplate service = null;

	/**
	 * 根据申请单数据设置异动后项目是否可编辑
	 *
	 * @param itemvos
	 */
	protected void setTrnItemsEnableByApplyData(LfwView widget, Dataset ds, Row row) {
		FormComp frmNewInfo = (FormComp) widget.getViewComponents().getComponent(getNewFormId());
//		String newpk_post = row.getString(ds.nameToIndex(StapplyVO.NEWPK_POST));
//		if (!StringUtils.isEmpty(newpk_post)) {// 岗位不为空,职务不可编辑
//			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOB)).setEnabled(false);
//		} else {
			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOB)).setEnabled(true);
//		}
//		String newpk_job = row.getString(ds.nameToIndex(StapplyVO.NEWPK_JOB));
//		if (!StringUtils.isEmpty(newpk_job)) {// 职务不为空,职级可编辑
//			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBGRADE)).setEnabled(true);
//		} else {
			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBGRADE)).setEnabled(true);
//		}
//		String newpk_jobgrade = row.getString(ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE));
//		if (!StringUtils.isEmpty(newpk_jobgrade)) {// 职级不为空,职等不可编辑
//			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBRANK)).setEnabled(false);
//		} else {
			frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBRANK)).setEnabled(true);
//		}
		// 职务类别不可编辑
		frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWSERIES)).setEnabled(true);
		// 岗位序列不可编辑
		frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_POSTSERIES)).setEnabled(true);

	}

	/**
	 * 异动后项目值变更处理
	 *
	 * @param ds
	 * @param row
	 * @param filedColIndex
	 */
	protected void setTrnItemValueChange(LfwView widget, Dataset ds, Row row, int filedColIndex) {
		try {
			FormComp frmNewInfo = (FormComp) widget.getViewComponents().getComponent(getNewFormId());
			// 转正后组织
			if (filedColIndex == ds.nameToIndex(StapplyVO.NEWPK_ORG)) {// 组织改后
				// 部门
				row.setValue(ds.nameToIndex(StapplyVO.NEWPK_DEPT), null);
				row.setValue(ds.nameToIndex(getRefFieldId(StapplyVO.NEWPK_DEPT)), null);
			} else if (filedColIndex == ds.nameToIndex(StapplyVO.NEWPK_DEPT)) {// 部门改后
				String newpk_dept = row.getString(filedColIndex);
				// 岗位
				row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POST), null);
				row.setValue(ds.nameToIndex(getRefFieldId(StapplyVO.NEWPK_POST)), null);
				// 岗位序列
				row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POSTSERIES), null);
				row.setValue(ds.nameToIndex(getRefFieldId(StapplyVO.NEWPK_POSTSERIES)), null);
				// 职务
				row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB), null);
				row.setValue(ds.nameToIndex(getRefFieldId(StapplyVO.NEWPK_JOB)), null);
				// 职级
				row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBRANK), null);
				row.setValue(ds.nameToIndex(getRefFieldId(StapplyVO.NEWPK_JOBRANK)), null);
				// 职等
				row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE), null);
				row.setValue(ds.nameToIndex(getRefFieldId(StapplyVO.NEWPK_JOBGRADE)), null);
				// 职务类别
				row.setValue(ds.nameToIndex(StapplyVO.NEWSERIES), null);
				row.setValue(ds.nameToIndex(getRefFieldId(StapplyVO.NEWSERIES)), null);

				if(ds.nameToIndex(StapplyVO.STAPPLY_MODE) > -1) {
					// 调配方式为"调出"时, 设置根据部门设置调配后的人事组织
					Integer stapply_mode = (Integer) row.getValue(ds.nameToIndex(StapplyVO.STAPPLY_MODE));
					if (stapply_mode == TRNConst.TRANSMODE_CROSS_OUT) {
						if (StringUtils.isEmpty(newpk_dept)) {
							row.setValue(ds.nameToIndex(StapplyVO.PK_HI_ORG), null);
						} else {
							String pk_hi_org = HiSQLHelper.getHrorgBydept(newpk_dept);
							String pk_old_hi_org = (String)row.getValue(ds.nameToIndex(StapplyVO.PK_OLD_HI_ORG));
							// 如果调配后人事组织和调配前人事组织相同则不设置
							if(!pk_hi_org.equals(pk_old_hi_org))
							    row.setValue(ds.nameToIndex(StapplyVO.PK_HI_ORG), pk_hi_org);
						}
					}
				}

			} else if (filedColIndex == ds.nameToIndex(StapplyVO.NEWPK_POST)) {// 岗位改后
				String newpk_post = row.getString(filedColIndex);
				PostVO postVO = getService().queryByPk(PostVO.class, newpk_post, true);
				if (postVO != null) {
					// 岗位序列
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POSTSERIES), postVO.getPk_postseries());
					// 职务
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB), postVO.getPk_job());
					// 岗位不为空时，岗位序列不可编辑v631加上去的
					frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_POSTSERIES)).setEnabled(false);
					// 职业
					if (!StringUtils.isEmpty(postVO.getEmployment())) {
						row.setValue(ds.nameToIndex(StapplyVO.NEWOCCUPATION), postVO.getEmployment());
					}
					// 工种
					if (!StringUtils.isEmpty(postVO.getWorktype())) {
						row.setValue(ds.nameToIndex(StapplyVO.NEWWORKTYPE), postVO.getWorktype());
					}
					// 适配有岗位无职务
					if (postVO.getPk_job() == null) {
//						// 职等
//						row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBRANK), null);
//						// 职务类别
//						row.setValue(ds.nameToIndex(StapplyVO.NEWSERIES), null);
//						// 职级(如果职务发生了变化,要清空职级)
//						row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE), null);
//						frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBGRADE)).setEnabled(true);
					}
				} else {
					// 设置岗位序列的值
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_POSTSERIES), null);
					// 设置 职务
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOB), null);
					// 岗位、岗位序列、职务、职务类别、职级、职等6个字段都可以编辑
					frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_POSTSERIES)).setEnabled(true);
				}
			} else if (filedColIndex == ds.nameToIndex(StapplyVO.NEWPK_JOB)) {// 职务改后
				String newpk_job = row.getString(filedColIndex);
				JobVO job = newpk_job == null ? null : getService().queryByPk(JobVO.class, newpk_job, true);
				if (job != null) {
					// 职务类别
					row.setValue(ds.nameToIndex(StapplyVO.NEWSERIES), job.getPk_jobtype());
					// 职务不为空时，职务类别不可编辑
					frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWSERIES)).setEnabled(false);
				} else {
					// 职务类别
					row.setValue(ds.nameToIndex(StapplyVO.NEWSERIES), null);
					// 职务为空时,职级不可编辑
					frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBGRADE)).setEnabled(true);
					// 职务为空时,职务类别可编辑
					frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWSERIES)).setEnabled(true);
					// edit by shaochj Oct 12, 2015  begin
					//原代码：
					/*}// 如果职务发生了变化,要清空职级
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE), null);
					// 如果职务发生了变化,要清空职等
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBRANK), null);*/
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE), null);
					// 如果职务发生了变化,要清空职等
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBRANK), null);
				}
				// edit by shaochj Oct 12, 2015 end
				
			} else if (filedColIndex == ds.nameToIndex(StapplyVO.NEWPK_JOBGRADE)) {// 职级改后
				String newpk_jobgrade = row.getString(filedColIndex);
				JobLevelVO jobgrade = newpk_jobgrade == null ? null : getService().queryByPk(JobLevelVO.class, newpk_jobgrade, true);
				if (jobgrade != null) {
					
//					row.setValue(ds.nameToIndex("newpk_jobgrade_jobgradename"), jobgrade.getName());
//					// 岗位、岗位序列、职务、职务类别、职级、职等6个字段都可以编辑
//					frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBRANK)).setEnabled(true);
				} else {
					// 职级清空后,若职务关联了职等,则用职务上的职等
					row.setValue(ds.nameToIndex(StapplyVO.NEWPK_JOBRANK), null);
					frmNewInfo.getElementById(getRefFieldId(StapplyVO.NEWPK_JOBRANK)).setEnabled(true);
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	protected String getNewFormId() {
		return FORM_TRN_NEW;
	}

	protected String getRefFieldId(String id) {
        if(StapplyVO.NEWPK_JOB.equals(id))
        	return "newpk_job_jobname";
        if(StapplyVO.NEWPK_JOBGRADE.equals(id))
    	    return "newpk_jobgrade_jobgradename";
        if(StapplyVO.NEWPK_JOBRANK.equals(id))
    	    return "newpk_jobrank_jobrankname";
        if(StapplyVO.NEWSERIES.equals(id))
    	    return "newseries_jobtypename";
        if(StapplyVO.NEWPK_POSTSERIES.equals(id))
    	    return "newpk_postseries_postseriesname";
        if(StapplyVO.NEWPK_POST.equals(id))
    	    return "newpk_post_postname";
		return id + HrssConsts.REF_SUFFIX;
	}

	private SimpleDocServiceTemplate getService() {
		if (service == null) {
			service = new SimpleDocServiceTemplate("TrnApproveView");
		}
		return service;
	}

	/**
	 * 合同查看
	 *
	 * @param mouseEvent
	 */
	public void contractInfo(MouseEvent<MenuItem> mouseEvent) {
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(PFApprovePageMode.getMasterDsId(widget));
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		String pk_psndoc = row.getString(ds.nameToIndex(RegapplyVO.PK_PSNDOC));
		String url = LfwRuntimeEnvironment.getCorePath() + "/uimeta.ra?pageId=ContractInfo&" + PsnApplyConsts.CONTRACT_PSNDOC_PK + "=" + pk_psndoc;
		getLifeCycleContext().getApplicationContext().showModalDialog(url, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0042")/*@res "合同查看"*/, "750", "400", "Dlg_contractInfo", ApplicationContext.TYPE_DIALOG);
	}

	/**
	 * 编制校验
	 *
	 * @param mouseEvent
	 */
	public void budgetCheck(MouseEvent<MenuItem> mouseEvent) {
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(PFApprovePageMode.getMasterDsId(widget));
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		SuperVO[] vos = new Dataset2SuperVOSerializer<StapplyVO>().serialize(ds, row);
		if(ArrayUtils.isEmpty(vos))
			return;
		String pk_psnjob = row.getString(ds.nameToIndex(RegapplyVO.PK_PSNJOB));
		String newpk_org = row.getString(ds.nameToIndex(RegapplyVO.NEWPK_ORG));
//		String pk_group = row.getString(ds.nameToIndex(RegapplyVO.PK_GROUP));
		PsnJobVO newJobVO = null;
		try {
			newJobVO = BudgetCheckView.buildPsnJobVO(pk_psnjob, vos[0]);
			ServiceLocator.lookup(IOrgBudgetQueryService.class).queryInitBudgetStatusData(SessionUtil.getLoginContext(), new PsnJobVO[]{newJobVO}, null);
		} catch (BusinessException e) {
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_hi-res0017")/*@res "提示信息"*/, e.getMessage());
//			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_hi-res0017")/*@res "提示信息"*/, ResHelper.getString("6003psnbudget", "6003budget0119")/* @res"没有找到相关单位的编制！" */);
//			new HrssException(ResHelper.getString("6003psnbudget", "6003budget0119")/* @res"没有找到相关单位的编制！" */).alert();
		} catch (HrssException e) {
			e.deal();
		}
		SessionUtil.getAppSession().setAttribute(PsnApplyConsts.SID_TRN_NEWPSN_INFO, vos[0]);
		String url = LfwRuntimeEnvironment.getCorePath() + "/uimeta.ra?pageId=BudgetCheck&" + PsnApplyConsts.BUDGET_PSNJOB_PK + "=" + pk_psnjob + "&" + PsnApplyConsts.BUDGET_NEWORG_PK + "="
				+ newpk_org;
		getLifeCycleContext().getApplicationContext().showModalDialog(url, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0043")/*@res "编制校验"*/, "750", "400", "Dlg_budgetCheck", ApplicationContext.TYPE_DIALOG);
	}

	/**
	 * 联查人员卡片
	 *
	 * @param mouseEvent
	 */
	public void psnCard(MouseEvent<MenuItem> mouseEvent) {
		LfwView widget = getLifeCycleContext().getViewContext().getView();
		Dataset ds = widget.getViewModels().getDataset(PFApprovePageMode.getMasterDsId(widget));
		Row row = ds.getSelectedRow();
		if (row == null) {
			return;
		}
		String pk_rpt_def = mouseEvent.getSource().getId();
		RepDefVO repDefVO = null;
		try {
			repDefVO = NCLocator.getInstance().lookup(IRptQueryService.class).queryByPk(pk_rpt_def);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		if(repDefVO==null){
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_hi-res0017")/*@res "提示信息"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0056")/*@res "还未分配卡片，请先分配卡片权限！"*/);
//			new HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0056")/*@res "还未分配卡片，请先分配卡片权限！"*/).alert();
		}
		if (repDefVO.getObj_rpt_def() == null)
		{
			CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_hi-res0017")/*@res "提示信息"*/,nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0012")/*@res "该卡片还没有定义，请先设置卡片内容！"*/);
//			new HrssException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0012")/*@res "该卡片还没有定义，请先设置卡片内容！"*/).alert();
		}
		String pk_psnjob = row.getString(ds.nameToIndex(RegapplyVO.PK_PSNJOB));
		String downurl = LfwRuntimeEnvironment.getRootPath() + "/pt/viewPsnCard/download?pk_rpt_def=" + pk_rpt_def + "&pk_psnjob=" + pk_psnjob;
		getLifeCycleContext().getApplicationContext().addExecScript("window.location='" + downurl + "';");
	}

}