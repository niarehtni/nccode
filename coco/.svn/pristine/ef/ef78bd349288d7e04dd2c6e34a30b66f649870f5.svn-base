package nc.bs.hrsms.ta.sss.overtime.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class ShopOverTimeRegListPageModel extends AdvancePageModel{

	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		
	}

	/**
	 * 设置左侧边栏
	 */
	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	/**
	 * 设置右侧边栏
	 */
	@Override
	protected String getRightPage() {
		return null;
	}

	
	@Override
	protected String getQueryTempletKey() {
		return null;
	}
}
