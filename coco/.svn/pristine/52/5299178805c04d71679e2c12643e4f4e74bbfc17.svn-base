package nc.bs.hrsms.ta.sss.shopleave.pagemodel;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrsms.ta.sss.common.ShopTaApplyBasePageModel;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.bs.hrss.ta.leave.ctrl.LeaveTypeRefCtrl;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;

public class ShopLeaveApplytCardPageModel extends ShopTaApplyBasePageModel {

	/**
	 * 初始化操作
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 设置子表右肩菜单
		setBodyGridMenu(ShopLeaveApplyConsts.VIEW_GRID_BODY, ShopLeaveApplyConsts.DS_MAIN_NAME, ShopLeaveApplyConsts.DS_SUB_NAME);
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		NCRefNode ref =	(NCRefNode) viewMain.getViewModels().getRefNode("refnode_hrtaleave_pk_psnjob_clerkcode");
		ref.setRefcode("部门人员(考勤档案)");
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent("headTab_card_pk_leaveh_form");
		if(formComp == null){
			return;
		}
		FormElement transtypeidElem = formComp.getElementById("transtypeid");
		transtypeidElem.setNullAble(true);
		FormElement transtypenameElem = formComp.getElementById("transtypeid_billtypename");
		transtypenameElem.setNullAble(true);
		FormElement transtypeElem = formComp.getElementById("transtype");
		transtypeElem.setNullAble(true);
	}

	@Override
	protected String getBillType() {
		return ShopLeaveApplyConsts.BILL_TYPE_CODE;
	}

	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		String leaveTypeRefId = "refnode_hrtaleave_pk_leavetype_timeitemname";
		String transTypeRefId = "refnode_hrtaleave_transtypeid_billtypename";
		Map<String, String> specialRefMap = new HashMap<String, String>();
		specialRefMap.put(transTypeRefId, TransTypeRefCtrl.class.getName());
		specialRefMap.put(leaveTypeRefId, LeaveTypeRefCtrl.class.getName());
		
//		String psnInfoRefId = "refnode_hrtaleave_pk_psnjob_clerkcode";
//		specialRefMap.put(psnInfoRefId, AppReferenceController.class.getName());
		
		return specialRefMap;
	}
	
	

	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	@Override
	protected String[] getTimeDataFields() {
		return new String[] { LeavehVO.SUMHOUR, LeavehVO.REALDAYORHOUR, LeavehVO.RESTEDDAYORHOUR, LeavehVO.RESTDAYORHOUR, LeavehVO.FREEZEDAYORHOUR, LeavehVO.USEFULDAYORHOUR, LeavebVO.LEAVEHOUR,LeavehVO.LACTATIONHOUR };
	}

}
