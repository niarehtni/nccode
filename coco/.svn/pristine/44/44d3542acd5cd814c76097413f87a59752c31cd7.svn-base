package nc.bs.hrsms.ta.sss.credit.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrsms.ta.sss.credit.CreditCardRecordConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class CreditCardRecordListPageModel extends AdvancePageModel {

	// 数据集id：工作日历
	public static final String DATASET_CALENDAR = "dsCalendar";
	// 数据集字段： 日历.日期
	public static final String FIELD_CALENDAR_DATE = "calendar";

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
		return CreditCardRecordConsts.FUNC_CODE;
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
