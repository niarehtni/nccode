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
	 * ��������RefNode��ֵ
	 * 
	 * @param refModel
	 */
	private void resetRefnode(RefNode rfnode, ILfwRefModel refModel) {
		if (refModel instanceof NcAdapterGridRefModel
				&& ((NcAdapterGridRefModel) refModel).getNcModel() instanceof LeaveTypeRefModel) {
			LeaveTypeRefModel Model = (LeaveTypeRefModel) ((NcAdapterGridRefModel) refModel)
					.getNcModel();
			//�漰����֯���ԣ�����������֯Ϊ���ţ���������Ϊ�����е���Ա������������ԱΪ��ǰ�û�Ϊ���Թٵķ�Χ
			Model.setPk_org(SessionUtil.getPk_org());
			Model.addWherePart(" and islactation='Y'");
		}
	}

}
