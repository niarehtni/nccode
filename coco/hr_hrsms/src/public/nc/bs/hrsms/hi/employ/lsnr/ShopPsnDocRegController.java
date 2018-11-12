package nc.bs.hrsms.hi.employ.lsnr;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.uap.ad.ref.NcAdapterGridRefModel;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.ui.hi.ref.PsndocRefModel;

public class ShopPsnDocRegController extends AppReferenceController{
	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel) {
		resetRefnode(ds, rfnode, refModel);
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
		resetRefnode(ds, rfnode, refModel);
	}
	
	private void resetRefnode(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
		// 流程类型参照的控制
		if (refModel instanceof NcAdapterGridRefModel&& ((NcAdapterGridRefModel) refModel).getNcModel() instanceof PsndocRefModel) {
			//String pk_dept = row.getValue(ds.nameToIndex(RegapplyVO.PK_ORG));
			PsndocRefModel Model = (PsndocRefModel) ((NcAdapterGridRefModel) refModel).getNcModel();
			String sql = " bd_psndoc.pk_psndoc in " +
					"(SELECT HI_PSNJOB2.PK_PSNDOC FROM HI_PSNJOB HI_PSNJOB2 WHERE HI_PSNJOB2.TRIAL_FLAG = 'Y' " +
					" AND HI_PSNJOB2.TRIAL_TYPE in ('1','2') and HI_PSNJOB2.lastflag='Y'  " + 
					"AND HI_PSNJOB2.PK_DEPT='"+ SessionUtil.getPk_mng_dept() + "') ";
			Model.setWherePart(sql);
		}
	}
}
