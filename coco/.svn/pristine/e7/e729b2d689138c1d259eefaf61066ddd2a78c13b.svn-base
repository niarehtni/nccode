package nc.bs.hrsms.ta.sss.calendar.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrsms.ta.common.ctrl.BURefController;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarListPanel;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.cata.CatagoryPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class WorkCalendarForPsnPageModel extends AdvancePageModel {

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
		// 设置班次(业务单元)参照的Controller
		setRefnodesDsListener();
	}

	/**
	 * 设置班次(业务单元)参照的Controller
	 */
	private void setRefnodesDsListener() {
		LfwView widget = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// 班次(业务单元)
		String shiftpkRefId = "refClass";
		IRefNode refnode = widget.getViewModels().getRefNode(shiftpkRefId);
		if (refnode != null) {
			((NCRefNode) refnode).setDataListener(BURefController.class.getName());
		}
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		CatagoryPanel cp = new CatagoryPanel();
		cp.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0031")/*
																									 * @
																									 * res
																									 * "查看方式"
																									 */);
		cp.setDataProvider(new WorkCalendarListPanel());
		//new MngDeptPanel()
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), cp, new SimpleQueryPanel() };
	}

	@Override
	protected String getFunCode() {
		return WorkCalendarConsts.FUNC_CODE;
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
