package nc.bs.hrsms.ta.empleavereg4store.feed;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrsms.ta.empleavereg4store.EmpApplyBasePageMode;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.bs.hrss.ta.leave.ctrl.LeaveTypeRefCtrl;
import nc.vo.ta.leave.LeaveRegVO;

/**
 * 休假申请的PageModel
 * 
 * @author renyp
 * 
 */
public class EmpLeaveRegFeedPageModel extends EmpApplyBasePageMode {

	/**
	 * 初始化操作
	 */
	@Override         
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 设置子表右肩菜单
		setBodyGridMenu(LeaveConsts.VIEW_GRID_BODY, LeaveConsts.DS_MAIN_NAME, LeaveConsts.DS_SUB_NAME);

	}

	@Override
	protected String getBillType() {
		return LeaveConsts.BILL_TYPE_CODE;
	}

	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		String leaveTypeRefId = "refnode_hrtaleave_pk_leavetype_timeitemname";
		//String transTypeRefId = "refnode_hrtaleave_transtypeid_billtypename";
		Map<String, String> specialRefMap = new HashMap<String, String>();
		//specialRefMap.put(transTypeRefId, TransTypeRefCtrl.class.getName());
		specialRefMap.put(leaveTypeRefId, LeaveTypeRefCtrl.class.getName());
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
		return new String[] { LeaveRegVO.REALDAYORHOUR, LeaveRegVO.RESTEDDAYORHOUR, LeaveRegVO.RESTDAYORHOUR, LeaveRegVO.FREEZEDAYORHOUR, LeaveRegVO.USEFULDAYORHOUR, LeaveRegVO.LEAVEHOUR,LeaveRegVO.LACTATIONHOUR };
	}
	@Override
	protected String getFunCode() {
		return "E20600909";
	}
}