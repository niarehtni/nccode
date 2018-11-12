package nc.bs.hrsms.hi.employ.lsnr;

import org.apache.commons.lang.StringUtils;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.uap.ad.ref.NcAdapterTreeRefModel;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefConst;
import nc.ui.om.ref.PostRefModel;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.om.post.PostVO;

public class ShopPsndeptController extends AppReferenceController{
	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}
	
	private void resetRefnode(RefNode rfnode, ILfwRefModel refModel) {
		// 流程类型参照的控制AbstractRefGridTreeModel

		//if (refModel instanceof NcAdapterTreeRefModel&& ((NcAdapterTreeRefModel) refModel).getNcModel() instanceof PostRefModel) {
			// 流程类型参照的控制AbstractRefGridTreeModel
			String pk_org=SessionUtil.getPk_mng_org();
			String pk_dept=SessionUtil.getPk_mng_dept();
			refModel.setPk_org(pk_org);
			AbstractRefModel ncModel = ((NcAdapterTreeRefModel) refModel).getNcModel();
			((PostRefModel) ncModel).setPkdept(pk_dept);
			String cond = "and om_post.postname!='店长' and ( " + SQLHelper.getNullSql(PostVO.TABLENAME + ".hrcanceled") + " or "
					+ PostVO.TABLENAME + ".hrcanceled = 'N' ) ";
			String powerSql = SQLHelper.getPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT,
					IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
			if (!StringUtils.isBlank(powerSql)) {
				cond += " and om_post.postname!='店长' and  om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
			}
			((PostRefModel) ncModel).addWherePart(cond);
		
		//}
		/*if (refModel instanceof NcAdapterTreeRefModel&& ((NcAdapterTreeRefModel) refModel).getNcModel() instanceof JobGradeRefModel) {
			// 流程类型参照的控制AbstractRefGridTreeModel
			LfwView viewMain = ViewUtil.getCurrentView();
		    Dataset hi_psnjob_curr = ViewUtil.getDataset(viewMain,"hi_psnjob_curr");
			Row row = hi_psnjob_curr.getSelectedRow();
			if (row == null) {
				return;
			}
			String pk_job=row.getString(hi_psnjob_curr.nameToIndex(PsnJobVO.PK_JOB));
			String pk_org=SessionUtil.getPk_mng_org();
			String pk_dept=SessionUtil.getPk_mng_dept();
			refModel.setPk_org(pk_org);
			AbstractRefModel ncModel = ((NcAdapterTreeRefModel) refModel).getNcModel();
			((PostRefModel) ncModel).setPkdept(pk_dept);
			String cond = " and pk_job= '"+pk_job+"'";
			((PostRefModel) ncModel).addWherePart(cond);
		}*/
	}
}
