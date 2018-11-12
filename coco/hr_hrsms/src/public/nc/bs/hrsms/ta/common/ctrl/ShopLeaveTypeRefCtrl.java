package nc.bs.hrsms.ta.common.ctrl;

import nc.bs.hrss.ta.common.ctrl.TaApplyRefController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.reference.ILfwRefModel;

public class ShopLeaveTypeRefCtrl extends TaApplyRefController {

	/**
	 * 在参照中添加WHERE条件
	 */
	@Override
	protected void processSelfWherePart(Dataset ds, RefNode rfnode, String filterValue, ILfwRefModel refModel) {
		super.processSelfWherePart(ds, rfnode, filterValue, refModel);
		// 设置哺乳假
		refModel.addWherePart(" islactation='Y' ");
	}

}
