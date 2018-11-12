package nc.bs.hrsms.hi.employ.lsnr;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.uap.ad.ref.NcAdapterGridRefModel;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.ui.hi.ref.PsndocRefModel;

public class ShopPsnDocController extends AppReferenceController{
	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}
	
	private void resetRefnode(RefNode rfnode, ILfwRefModel refModel) {
		// 流程类型参照的控制
		if (refModel instanceof NcAdapterGridRefModel&& ((NcAdapterGridRefModel) refModel).getNcModel() instanceof PsndocRefModel) {
			
			PsndocRefModel Model = (PsndocRefModel) ((NcAdapterGridRefModel) refModel).getNcModel();
			String sql = " hi_psnjob.PK_DEPT = '"+ SessionUtil.getPk_mng_dept() +"' and  bd_psndoc.pk_psndoc in (select PK_PSNDOC from BD_PSNJOB where BD_PSNJOB.PK_DEPT='"+ SessionUtil.getPk_mng_dept() + 
					"' and BD_PSNJOB.pk_psndoc in (select PK_PSNDOC from HI_PSNJOB where HI_PSNJOB.LASTFLAG='Y' and HI_PSNJOB.ENDFLAG='N')) ";
			Model.setWherePart(sql);
		}
	}
}
