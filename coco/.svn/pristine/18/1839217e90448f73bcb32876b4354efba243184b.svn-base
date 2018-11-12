package nc.bs.hrsms.ta.shift.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class StoreShiftPageModel extends AdvancePageModel{

	/**
	 * 个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}

	/**
	 * 左侧Panel
	 */
	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		//new MngDeptPanel()
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	@Override
	protected String getFunCode() {
		return "E2060501";
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected String getRightPage() {
		return null;
	}
}
