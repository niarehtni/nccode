package nc.bs.hrsms.ta.sss.leaveoff.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.bs.hrsms.ta.sss.leaveoff.ShopLeaveOffUtils;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class ShopLeaveOffApplyListPageModel extends ShopTaListBasePageModel{

	@Override
	protected String getFunCode() {
		return "E20600923";
	}
	
	/**
	 * 初始化操作
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}
	
	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return ShopLeaveOffUtils.getPageTimeDataFieldIDs();
	}

	@Override
	protected String getQueryTempletKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		// TODO Auto-generated method stub
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	@Override
	protected String getRightPage() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
