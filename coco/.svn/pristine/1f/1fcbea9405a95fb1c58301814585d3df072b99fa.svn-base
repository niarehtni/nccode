package nc.bs.hrsms.ta.sss.calendar.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarListPanel;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.cata.CatagoryPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class WorkCalendarListPageModel extends AdvancePageModel{

	/**
	 * 个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		// TODO Auto-generated method stub
		CatagoryPanel cp = new CatagoryPanel();
		cp.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0031")/*@res "查看方式"*/);
		cp.setDataProvider(new WorkCalendarListPanel());
		//new MngDeptPanel()
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), cp, new SimpleQueryPanel()};
	}

	@Override
	protected String getFunCode() {
		return WorkCalendarConsts.FUNC_CODE;
	}
	
	@Override
	protected String getQueryTempletKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getRightPage() {
		// TODO Auto-generated method stub
		return null;
	}

}
