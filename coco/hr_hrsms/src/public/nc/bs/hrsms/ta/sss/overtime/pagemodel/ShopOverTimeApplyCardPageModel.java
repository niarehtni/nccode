package nc.bs.hrsms.ta.sss.overtime.pagemodel;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.bs.hrss.ta.overtime.OverTimeConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBasePageModel;
import nc.bs.hrsms.ta.sss.overtime.ShopOverTimeConsts;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;

public class ShopOverTimeApplyCardPageModel extends ShopTaApplyBasePageModel{

	@Override
	protected String getBillType() {
		// TODO Auto-generated method stub
		return ShopOverTimeConsts.BILL_TYPE_CODE;
	}
	
	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 设置子表右肩菜单
		setBodyGridMenu(OverTimeConsts.VIEW_GRID_BODY, OverTimeConsts.DS_MAIN_NAME, OverTimeConsts.DS_SUB_NAME);

	}
	
	/**
	 * 特殊待设置参照集合
	 */
	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		String transTypeRefId = "refnode_hrtaovertimeh_transtypeid_billtypename";
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
		return new String[] { OvertimehVO.SUMHOUR, OvertimebVO.ACTHOUR, OvertimebVO.OVERTIMEHOUR, OvertimebVO.DEDUCT, OvertimebVO.OVERTIMEALREADY };
	}
}
