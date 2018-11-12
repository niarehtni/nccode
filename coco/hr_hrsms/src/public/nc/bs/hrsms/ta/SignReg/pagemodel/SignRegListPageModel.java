package nc.bs.hrsms.ta.SignReg.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class SignRegListPageModel extends AdvancePageModel {

	// 数据集id：签卡登记
	public static final String DATASET_SIGNREG = "SignReg_DataSet";
	
	/**
	 * 个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}

	

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		//return new IPagePanel[] { new CanvasPanel(), new MngDeptPanel(),  new SimpleQueryPanel() };
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(),  new SimpleQueryPanel() };
	}

	@Override
	protected String getFunCode() {
		return "E2060917";
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
