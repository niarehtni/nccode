package nc.bs.hrsms.ta.empleavereg4store.feed;

import nc.bs.hrss.pub.tool.SessionUtil;
import nc.uap.ad.ref.NcAdapterGridRefModel;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.app.AppReferenceController;
import nc.ui.ta.timeitem.ref.LeaveTypeRefModel;

public class ShopLeaveTypeController extends AppReferenceController {
	
	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode,
			String filterValue, ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	@Override
	protected void processTreeSelWherePart(Dataset ds, RefNode rfnode,
			ILfwRefModel refModel) {
		resetRefnode(rfnode, refModel);
	}

	/**
	 * 重新设置RefNode的值
	 * 
	 * @param refModel
	 */
	private void resetRefnode(RefNode rfnode, ILfwRefModel refModel) {
		if (refModel instanceof NcAdapterGridRefModel
				&& ((NcAdapterGridRefModel) refModel).getNcModel() instanceof LeaveTypeRefModel) {
			LeaveTypeRefModel Model = (LeaveTypeRefModel) ((NcAdapterGridRefModel) refModel)
					.getNcModel();
			//涉及跨组织面试，所以设置组织为集团，设置条件为面试中的人员，设置面试人员为当前用户为面试官的范围
			Model.setPk_org(SessionUtil.getPk_org());
			Model.addWherePart(" and islactation='Y'");
		}
	}

}
