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
	 * ��ʼ�����Ի�����
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// �����ӱ��Ҽ�˵�
		setBodyGridMenu(ShopAwayApplyConsts.VIEW_GRID_BODY, ShopAwayApplyConsts.DS_MAIN_NAME, ShopAwayApplyConsts.DS_SUB_NAME);

	}
	/**
	 * ��������ò��ռ���
	 */
	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		String transTypeRefId = "refnode_hrtaawayh_transtypeid_billtypename";
		Map<String, String> specialRefMap = new HashMap<String, String>();
		specialRefMap.put(transTypeRefId, TransTypeRefCtrl.class.getName());
		return specialRefMap;
	}

	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 * 
	 * @return
	 */
	@Override
	protected String[] getTimeDataFields() {
		return new String[] { AwayhVO.SUMHOUR, AwaybVO.AWAYHOUR };
	}
}
