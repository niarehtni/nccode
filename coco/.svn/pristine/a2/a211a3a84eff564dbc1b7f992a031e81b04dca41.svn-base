package nc.bs.hrsms.ta.sss.away.pagemodel;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.bs.hrsms.ta.sss.away.ShopAwayApplyConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBasePageModel;
import nc.vo.ta.away.AwaybVO;
import nc.vo.ta.away.AwayhVO;

public class ShopAwayApplyCardPageModel extends ShopTaApplyBasePageModel{

	@Override
	protected String getBillType() {
		// TODO Auto-generated method stub
		return ShopAwayApplyConsts.BILL_TYPE_CODE;
	}

	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 设置子表右肩菜单
		setBodyGridMenu(ShopAwayApplyConsts.VIEW_GRID_BODY, ShopAwayApplyConsts.DS_MAIN_NAME, ShopAwayApplyConsts.DS_SUB_NAME);

	}
	/**
	 * 特殊待设置参照集合
	 */
	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		String transTypeRefId = "refnode_hrtaawayh_transtypeid_billtypename";
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
		return new String[] { AwayhVO.SUMHOUR, AwaybVO.AWAYHOUR };
	}
}
