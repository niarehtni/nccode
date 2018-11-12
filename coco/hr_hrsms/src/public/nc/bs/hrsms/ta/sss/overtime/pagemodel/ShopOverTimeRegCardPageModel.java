package nc.bs.hrsms.ta.sss.overtime.pagemodel;

import nc.bs.hrsms.ta.common.ctrl.ShopTaRegRefController;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.PageModel;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;

public class ShopOverTimeRegCardPageModel extends PageModel{
	
	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
//		// 设置参照Controller
		setRefnodesDsListener(viewMain);
//		// 页面特殊设置
//		setPageSepcial(viewMain);
	}

	/**
	 * 设置参照Controller
	 */
	private void setRefnodesDsListener(LfwView viewMain) {
		NCRefNode refNode = (NCRefNode) viewMain.getViewModels().getRefNode("refnode_hrtaovertimereg_pk_overtimetype_timeitemname");
		refNode.setDataListener(ShopTaRegRefController.class.getName());
		
		NCRefNode refNode1 = (NCRefNode) viewMain.getViewModels().getRefNode("refnode_hrtaovertimereg_pk_psnjob_clerkcode");
		refNode1.setDataListener(ShopTaRegRefController.class.getName());
	}
}
