package nc.bs.hrsms.ta.sss.shopleave.pagemodel;

import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.bs.hrsms.ta.sss.shopleave.common.ShopQueryCondLeaveTypeRefCtrl;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.vo.ta.leave.LeavehVO;

public class ShopLeaveApplyListPageModel extends ShopTaListBasePageModel{

	/**
	 * 初始化操作
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		LfwView pubview_simplequery = getPageMeta().getView(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET);
		// 获得单据页面的所有参照
		IRefNode refnode = pubview_simplequery.getViewModels().getRefNode("refnode_mainds_pk_leavetype");
		if (refnode != null) {
			((NCRefNode) refnode).setDataListener(ShopQueryCondLeaveTypeRefCtrl.class.getName());
		}
	}

	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	@Override
	protected String[] getTimeDataFields() {
		return new String[] { LeavehVO.SUMHOUR, LeavehVO.RESTEDDAYORHOUR, LeavehVO.RESTDAYORHOUR,LeavehVO.LACTATIONHOUR };
	}
}
