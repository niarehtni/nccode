package nc.bs.hrsms.hi.employ;

import nc.bs.hrsms.hi.employ.lsnr.JobGradeRefNodeController;
import nc.bs.hrsms.hi.employ.lsnr.ShopPsndeptController;
import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.base.ExtAttribute;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class ShopPsnMainPageModel extends AdvancePageModel {

	protected String getFunCode() {
		return "E2060101";
	}

	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		//得到卡片界面的view，对流程类型参照做控制
		LfwView widget = this.getPageMeta().getView("psn_employ");
		
		IRefNode region = widget.getViewModels().getRefNode("refnode_hi_psnjob_curr_pk_post_postname");
		NCRefNode region1 = (NCRefNode) region;
		region1.setDataListener(ShopPsndeptController.class.getName());
		IRefNode region2 = widget.getViewModels().getRefNode("refnode_hi_psnjob_pk_jobgrade_jobgradename");
		NCRefNode region3 = (NCRefNode) region2;
		region3.setDataListener(JobGradeRefNodeController.class.getName());
		
		NCRefNode placeRefNode = (NCRefNode) widget.getViewModels().getRefNode("refnode_bd_psndoc_nativeplace_name"); 
		placeRefNode.getExtendAttributes();
		/*placeRefNode.setFilterRefNodeNames(true); 
		ExtAttribute attr = new ExtAttribute(); 
		attr.setKey("$LfwRefFilterClass"); 
		attr.setValue(nc.bs.hrsms.hi.psninfo.RefPlaceRefModel.class); 
		placeRefNode.addAttribute(attr);*/
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected String getRightPage() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[]{new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel()};
	}
}
