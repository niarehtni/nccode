package nc.bs.hrsms.ta.sss.calendar.pagemodel;

import java.util.Arrays;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.PageModel;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrsms.ta.common.ctrl.BURefController;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.jsp.uimeta.UIFlowvLayout;
import nc.uap.lfw.jsp.uimeta.UIFlowvPanel;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class BatchArrangeShiftPageModel extends PageModel {

	@Override
	protected String getFunCode() {
		return (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(WorkCalendarConsts.FUNCODE_CIRCLEARRANGESHIFT);
	}
	
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		setShiftRefNode();
	}
	
	/**
	 * 设置班次参照，隐藏班组表格或人员表格
	 * 
	 */
	public void setShiftRefNode(){
		LfwView widget = LfwRuntimeEnvironment.getWebContext().getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		Dataset dsWorkPeriod = widget.getViewModels().getDataset(WorkCalendarConsts.DS_WORKPERIOD);
		dsWorkPeriod.setEnabled(Boolean.TRUE);
		// 班次参照参照控制
		NCRefNode shiftRfnodeGrade = (NCRefNode) widget.getViewModels().getRefNode("refShift");
		shiftRfnodeGrade.setDataListener(BURefController.class.getName());
		
		UIMeta um = (UIMeta)getUIMeta();
		UIFlowvLayout flowvLayout = (UIFlowvLayout)um.findChildById("flowvlayout5674");
		
		
		/* 为表格添加toolbar */
		LfwView wdtMain = LfwRuntimeEnvironment.getWebContext().getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		GridComp tblWorkPeriod = (GridComp) wdtMain.getViewComponents().getComponent("tblWorkPeriod");
		MenuItem[] mItems = ViewUtil.getMenuItemsOfBar(wdtMain, "menuWorkPeriod");
		tblWorkPeriod.getMenuBar().setMenuList(Arrays.asList(mItems));
		// 如果是班组工作日历节点，则隐藏人员表格
		if(getFunCode().equals(WorkCalendarConsts.TEAMCALENDAR_FUN_CODE)){
			flowvLayout.removePanel((UIFlowvPanel)um.findChildById("panelv65674"));
		}
		// 如果是员工工作日历节点，则隐藏班组表格
		else{
			flowvLayout.removePanel((UIFlowvPanel)um.findChildById("panelv45674"));
		}
	}
}
