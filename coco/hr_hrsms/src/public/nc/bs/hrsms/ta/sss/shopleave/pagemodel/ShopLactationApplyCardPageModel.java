package nc.bs.hrsms.ta.sss.shopleave.pagemodel;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrsms.ta.common.ctrl.ShopLeaveTypeRefCtrl;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBasePageModel;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.vo.ta.leave.LeavehVO;

public class ShopLactationApplyCardPageModel extends ShopTaApplyBasePageModel {

	/**
	 * 初始化操作
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 设置子表右肩菜单
		setBodyGridMenu(ShopLeaveApplyConsts.VIEW_GRID_BODY, ShopLeaveApplyConsts.DS_MAIN_NAME, ShopLeaveApplyConsts.DS_SUB_NAME);

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
		specialRefMap.put(leaveTypeRefId, ShopLeaveTypeRefCtrl.class.getName());
		
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
		return new String[] { LeavehVO.LACTATIONHOUR };
	}

}
