package nc.bs.hrsms.ta.sss.overtime.pagemodel;

import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.vo.ta.overtime.OvertimehVO;

public class ShopOverTimeApplyListPageModel extends ShopTaListBasePageModel{

	/**
	 * ��ʼ�����Ի�����
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}

	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 * 
	 * @return
	 */
	@Override
	protected String[] getTimeDataFields() {
		return new String[] { OvertimehVO.SUMHOUR };
	}
}
