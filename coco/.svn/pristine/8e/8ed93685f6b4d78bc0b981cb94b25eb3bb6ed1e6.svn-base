package nc.bs.hrsms.hi.employ.ShopTransfer;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrsms.hi.HiListBasePageModel;
import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.bs.hrss.trn.PsnApplyConsts;

public class TransferListPageModel extends HiListBasePageModel{
	
	@Override
	protected String getFunCode() {
		return PsnApplyConsts.TRANSFER_FUNC_CODE;
	}

	/**
	 * 页面特殊设置
	 */
	@Override
	protected void setPageSepcial() {
		super.setPageSepcial();
	}


	/**
	 * 特殊待设置参照集合
	 */
	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		Map<String, String> specialRefMap = new HashMap<String, String>();
		String transTypeRefId = "refnode_hi_stapply_transtypeid_billtypename";
		specialRefMap.put(transTypeRefId, TransTypeRefCtrl.class.getName());
		return specialRefMap;
	}

	@Override
	protected String getBillType() {
		return PsnApplyConsts.TRANSFER_BILLTYPE_CODE;
	}

	@Override
	protected String getBillInfoForm() {
		return PsnApplyConsts.TRANSFER_FORM_BILLINFO;
	}
}
