package nc.bs.hrsms.ta.SignReg.pagemodel;

import java.util.ArrayList;
import java.util.List;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.PageModel;
import nc.bs.hrsms.ta.SignReg.signreg.SignRegConsts;
import nc.bs.hrsms.ta.common.ctrl.ShopTaRegRefController;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.event.conf.DatasetRule;
import nc.uap.lfw.core.event.conf.EventConf;
import nc.uap.lfw.core.event.conf.EventSubmitRule;
import nc.uap.lfw.core.event.conf.ViewRule;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;


public class SignCardPageModel  extends PageModel{

	
	/**
	 * 初始化操作
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 设置子表右肩菜单
		setBodyGridMenu(SignRegConsts.VIEW_GRID_BODY, SignRegConsts.DS_MAIN_NAME, SignRegConsts.DS_SUB_NAME);
		// 设置参照Controller
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		setRefnodesDsListener(viewMain);
	}
	/**
	 * 设置参照Controller
	 */
	private void setRefnodesDsListener(LfwView viewMain) {
		NCRefNode refNode = (NCRefNode) viewMain.getViewModels().getRefNode("refnode_SignReg_DataSet_pk_psnjob_clerkcode");
		refNode.setDataListener(ShopTaRegRefController.class.getName());
		
//		NCRefNode agentpsnrefNode = (NCRefNode) viewMain.getViewModels().getRefNode("refnode_hrtaawayreg_pk_agentpsn_pk_psndoc_name");
//		agentpsnrefNode.setDataListener(ShopTaRegRefController.class.getName());

	}
	
	/**
	 * 设置子表右肩菜单
	 * 
	 * @param gridId
	 * @param dsId
	 * @param dsDetailId
	 */
	protected void setBodyGridMenu(String gridId, String masterDsId, String dsDetailId) {
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// 签卡申请子表Grid
		GridComp bodyGrid = (GridComp) viewMain.getViewComponents().getComponent(gridId);
		/** 设置菜单新增行，插入行，删除行 */
		MenubarComp bodyMenuBar = new MenubarComp();
		bodyMenuBar.setId("gridToolMenubar");
		String[] itemIds = new String[] { "new_row", "delete_row", "insert_row" };
		String[] itemI18nNames = new String[] { "w_ta-001276", "w_ta-001278", "w_ta-001277" };
		String[] eventMethodNames = new String[] { "onLineAdd", "onLineDel", "onLineInsert" };
		for (int i = 0; i < itemIds.length; i++) {
			// 菜单
			MenuItem item = new MenuItem();
			item.setId(itemIds[i]);
			item.setStateManager("");
			item.setModifiers(2);
			item.setShowModel(2);
			// item.setText(itemTexts[i]);
			// item.setI18nName(itemI18nNames[i]);
			item.setLangDir("node_ta-res");
			item.setTipI18nName(itemI18nNames[i]);
			// item.setTip(itemTexts[i]);
			// 事件
			List<EventConf> eventConfList = new ArrayList<EventConf>();
			EventConf itemEvent = new EventConf();
			itemEvent.setOnserver(true);
			itemEvent.setName("onclick");
			itemEvent.setMethodName(eventMethodNames[i]);
			// 提交规则
			EventSubmitRule sr = new EventSubmitRule();
			ViewRule wr = new ViewRule();
			wr.setId(viewMain.getId());
			DatasetRule dsr = new DatasetRule();
			dsr.setId(masterDsId);
			dsr.setType(DatasetRule.TYPE_CURRENT_LINE);
			wr.addDsRule(dsr);
			DatasetRule dsr2 = new DatasetRule();
			dsr2.setId(dsDetailId);
			dsr2.setType(DatasetRule.TYPE_ALL_LINE);
			wr.addDsRule(dsr2);

			sr.addViewRule(wr);
			itemEvent.setSubmitRule(sr);
			eventConfList.add(itemEvent);
			item.setEventConfList(eventConfList);
			bodyMenuBar.addMenuItem(item);
		}
		bodyGrid.setMenuBar(bodyMenuBar);
	}
	
	
}