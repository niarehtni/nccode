package nc.bs.hrsms.hi;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPsndocQryService;
import nc.uap.ad.ref.NcAdapterGridRefModel;
import nc.uap.ad.ref.NcAdapterTreeRefModel;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefConst;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.post.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.transmng.StapplyVO;

import org.apache.commons.lang.StringUtils;

/**
 * 人员变动审批单据的参照的Controller
 * 
 * @author qiaoxp
 * 
 */
public class HiApproveRefController extends AppReferenceController {

	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	/**
	 * 重新设置RefNode的值
	 * 
	 * @param refModel
	 */
	private void resetRefnode(RefNode rfnode, ILfwRefModel refModel) {
		Dataset parentDs = getParentDs(rfnode);
		Row row = parentDs.getSelectedRow();
		if (row == null) {
			return;
		}
		String dsId = rfnode.getWriteDs();

		String pk_dept = null;
		String pk_org = SessionUtil.getPk_org();
		Integer stapply_mode = null;
		String billType = null;
		if (parentDs.nameToIndex(StapplyVO.NEWPK_ORG) > -1) {
			pk_org = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_ORG));
		}
		if (parentDs.nameToIndex(StapplyVO.STAPPLY_MODE) > -1) {
			stapply_mode = (Integer) row.getValue(parentDs.nameToIndex(StapplyVO.STAPPLY_MODE));
		}
		if (parentDs.nameToIndex(StapplyVO.PK_BILLTYPE) > -1) {
			billType = row.getString(parentDs.nameToIndex(StapplyVO.PK_BILLTYPE));
		}
		if (parentDs.nameToIndex(StapplyVO.NEWPK_DEPT) > -1) {
			pk_dept = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_DEPT));
		}

		// 设置参照的Pk_group
		refModel.setPk_group(SessionUtil.getPk_group());
		if (getRefNodeId(dsId, StapplyVO.NEWPK_ORG).equals(rfnode.getId())) {// 组织

			String powerSql = "";
			if (TRNConst.BUSITYPE_TRANSITION.equals(billType) && stapply_mode == TRNConst.TRANSMODE_CROSS_OUT) {
				// 只有在调配申请审批节点并且调配方式为调出时使用特殊场景
				powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG,
						HICommonValue.OPERATIONCODE_TRANSDEFAULT, "org_orgs");
			} else {
				powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG,
						IRefConst.DATAPOWEROPERATION_CODE, "org_orgs");
			}
			if (!StringUtils.isBlank(powerSql)) {
				refModel.addWherePart(" pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ") and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable)");
			}else {
				refModel.addWherePart(" org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable) ");
			}

		} else if (getRefNodeId(dsId, StapplyVO.NEWPK_DEPT).equals(rfnode.getId())) {// 部门
			refModel.setPk_org(pk_org);
			String cond = " hrcanceled = 'N' and depttype <> 1 ";
			String powerSql = "";
			if (TRNConst.BUSITYPE_TRANSITION.equals(billType) && stapply_mode == TRNConst.TRANSMODE_CROSS_OUT) {
				// 只有在调配申请审批节点并且调配方式为调出时使用特殊场景
				powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT,
						HICommonValue.OPERATIONCODE_TRANSDEFAULT, "org_dept");
			} else {
				powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT,
						IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
			}
			if (!StringUtils.isBlank(powerSql)) {
				cond += " and " + powerSql;
			}
			refModel.addWherePart(cond);
		} else if (getRefNodeId(dsId, StapplyVO.NEWPK_PSNCL).equals(rfnode.getId())) {// 人员类别

			String powerSql = "";
			if (TRNConst.BUSITYPE_TRANSITION.equals(billType) && stapply_mode == TRNConst.TRANSMODE_CROSS_OUT) {
				// 只有在调配申请审批节点并且调配方式为调出时使用特殊场景
				refModel.setDataPowerOperation_code(HICommonValue.OPERATIONCODE_TRANSDEFAULT);
				powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL,
						HICommonValue.OPERATIONCODE_TRANSDEFAULT, "bd_psncl");
			} else {
				refModel.setDataPowerOperation_code(IRefConst.DATAPOWEROPERATION_CODE);
				powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL,
						IRefConst.DATAPOWEROPERATION_CODE, "bd_psncl");
			}
			if (!StringUtils.isBlank(powerSql)) {
				refModel.addWherePart(" " + powerSql);
			}
		} else if (getRefNodeId(dsId, StapplyVO.NEWPK_POST).equals(rfnode.getId())) {// 岗位
			// 岗位-根据组织、部门过滤
			refModel.setPk_org(pk_org);
			AbstractRefModel ncModel = ((NcAdapterTreeRefModel) refModel).getNcModel();
			((PostRefModel) ncModel).setPkdept(pk_dept);
			String cond = " and ( " + SQLHelper.getNullSql(PostVO.TABLENAME + ".hrcanceled") + " or "
					+ PostVO.TABLENAME + ".hrcanceled = 'N' ) ";
			String powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT,
					IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
			if (!StringUtils.isBlank(powerSql)) {
				cond += " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
			}
			((PostRefModel) ncModel).addWherePart(cond);

		} else if (getRefNodeId(dsId, StapplyVO.NEWPK_JOB).equals(rfnode.getId())) {// 职务
			if (!StringUtils.isEmpty(pk_org)) {
				refModel.setPk_org(pk_org);
			} else {
				refModel.setPk_org(SessionUtil.getPk_group());
			}
		} else if ("refnode_hi_stapply_newpk_jobgrade_jobgradename".equals(rfnode.getId()) || "refnode_hi_regapply_newpk_jobgrade_jobgradename".equals(rfnode.getId()) ) {// 职级

			String pk_job = null;
			String pk_post = null;
			String pk_postseries = null;
            String pk_jobtype = null;
			if (parentDs.nameToIndex(StapplyVO.NEWPK_JOB) > -1) {
				pk_job = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_JOB));
			}
			if (parentDs.nameToIndex(StapplyVO.NEWPK_POST) > -1) {
				pk_post = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_POST));
			}
			if (parentDs.nameToIndex(StapplyVO.NEWPK_POSTSERIES) > -1) {
				pk_postseries = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_POSTSERIES));
			}
			if (parentDs.nameToIndex(StapplyVO.NEWSERIES) > -1) {
				pk_jobtype = row.getString(parentDs.nameToIndex(StapplyVO.NEWSERIES));
			}
			
			FilterTypeEnum filterType = null;
            String gradeSource = "";
            Map<String, Object> resultMap = null;
            try
            {
                resultMap = NCLocator.getInstance().lookup(IPsndocQryService.class).getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
            }
            catch (BusinessException e)
            {
                Logger.error(e.getMessage(), e);
            }
            
            if (!resultMap.isEmpty())
            {
                filterType = (FilterTypeEnum) resultMap.get("filterType");
                gradeSource = (String) resultMap.get("gradeSource");
            }
			
			AbstractRefModel ncModel = ((NcAdapterGridRefModel) refModel).getNcModel();
			((JobGradeRefModel2) ncModel).setPk_filtertype(gradeSource, filterType);
			
		} else if("refnode_hi_stapply_newpk_jobrank_jobrankname".equals(rfnode.getId()) || "refnode_hi_regapply_newpk_jobrank_jobrankname".equals(rfnode.getId())){// 职等
			AbstractRefModel ncModel = ((NcAdapterGridRefModel) refModel).getNcModel();

            String pk_jobgrade = null;
            
            if (parentDs.nameToIndex(StapplyVO.NEWPK_JOBGRADE) > -1) {
            	pk_jobgrade = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_JOBGRADE));
			}
            
            if (StringUtils.isBlank(pk_jobgrade))
            {
                ((JobRankRefModel) ncModel).setPk_joblevel("");
                return;
            }
            
            String pk_job = null;
			String pk_post = null;
			String pk_postseries = null;
            String pk_jobtype = null;
            
            if (parentDs.nameToIndex(StapplyVO.NEWPK_JOB) > -1) {
				pk_job = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_JOB));
			}
			if (parentDs.nameToIndex(StapplyVO.NEWPK_POST) > -1) {
				pk_post = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_POST));
			}
			if (parentDs.nameToIndex(StapplyVO.NEWPK_POSTSERIES) > -1) {
				pk_postseries = row.getString(parentDs.nameToIndex(StapplyVO.NEWPK_POSTSERIES));
			}
			if (parentDs.nameToIndex(StapplyVO.NEWSERIES) > -1) {
				pk_jobtype = row.getString(parentDs.nameToIndex(StapplyVO.NEWSERIES));
			}
            FilterTypeEnum filterType = null;
            String gradeSource = "";
            Map<String, Object> resultMap = null;
            try
            {
                resultMap = NCLocator.getInstance().lookup(IPsndocQryService.class).getLevelRankCondition(pk_jobtype, pk_job, pk_postseries, pk_post);
            }
            catch (BusinessException e)
            {
                Logger.error(e.getMessage(), e);
            }
            
            if (!resultMap.isEmpty())
            {
                filterType = (FilterTypeEnum) resultMap.get("filterType");
                gradeSource = (String) resultMap.get("gradeSource");
            }
            ((JobRankRefModel) ncModel).setPk_joblevel(pk_jobgrade);
            ((JobRankRefModel) ncModel).setPk_filtertype(gradeSource, filterType);
        
			
		}else if (getRefNodeId(dsId, StapplyVO.PK_HI_ORG).equals(rfnode.getId())) {
			// 调配后人事组织过滤掉当前组织
			refModel.addWherePart(" pk_hrorg <> '" + pk_org + "' and pk_hrorg in (select pk_adminorg from org_admin_enable) ");
		} else {
			// 其他根据调配后人事组织过滤的参照
			if (parentDs.nameToIndex(StapplyVO.PK_HI_ORG) > -1) {
				pk_org = row.getString(parentDs.nameToIndex(StapplyVO.PK_HI_ORG));
			}
			refModel.setPk_org(pk_org);
		}
	}

	/**
	 * 获取参照所在数据集的当前选中行
	 * 
	 * @return
	 */
	private Dataset getParentDs(RefNode rfnode) {
		// 参照所在页面
		String parentPageId = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("otherPageId");
		LfwWindow parentPm = null;
		if (!StringUtils.isEmpty(parentPageId)) {
			parentPm = AppLifeCycleContext.current().getApplicationContext().getWindowContext(parentPageId).getWindow();
		} else {
			parentPm = AppLifeCycleContext.current().getWindowContext().getWindow();
		}
		// 参照所在片段
		LfwView widget = parentPm.getView(rfnode.getView().getId());
		// 参照写入数据集
		String writeDsId = rfnode.getWriteDs();
		return widget.getViewModels().getDataset(writeDsId);
	}

	protected String getRefNodeId(String dsId, String filedId) {
		return "refnode_" + dsId + "_" + filedId;
	}

}
