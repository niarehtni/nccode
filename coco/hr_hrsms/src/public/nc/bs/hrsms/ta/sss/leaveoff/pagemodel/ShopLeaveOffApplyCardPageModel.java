package nc.bs.hrsms.ta.sss.leaveoff.pagemodel;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.bs.hrsms.ta.sss.leaveoff.ShopLeaveOffConsts;
import nc.bs.hrsms.ta.sss.leaveoff.ShopLeaveOffUtils;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBasePageModel;

public class ShopLeaveOffApplyCardPageModel extends ShopTaApplyBasePageModel{

	@Override
	protected String getBillType() {
		// TODO Auto-generated method stub
		return ShopLeaveOffConsts.BILL_TYPE_CODE;
	}

	@Override
	protected String getFunCode() {
		return ShopLeaveOffConsts.LEAVEOFF_NODE_CODE;
	}
	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		// 交易类型
		 String transTypeRefId = "refnode_dsLeaveOff_transtypeid_billtypename";
		Map<String, String> specialRefMap = new HashMap<String, String>();
		specialRefMap.put(transTypeRefId, TransTypeRefCtrl.class.getName());
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
		return ShopLeaveOffUtils.getPageTimeDataFieldIDs();
	}
}
