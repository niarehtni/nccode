package nc.bs.hrsms.ta.sss.leaveoff.prcss;

import nc.bs.hrsms.ta.sss.leaveoff.ctrl.ShopLeaveRegListView;
import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopTaBaseAddProcessor;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import uap.web.bd.pub.AppUtil;

public class ShopLeaveOffAddProcessor extends ShopTaBaseAddProcessor{

	@Override
	public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode) {
		super.onBeforeRowAdd(ds, row, billTypeCode);
		
		String pk_leavereg = (String)AppUtil.getAppAttr(ShopLeaveRegListView.APP_ID_PK_LEAVEREG);
		row.setString(ds.nameToIndex(LeaveoffVO.PK_LEAVEREG), pk_leavereg);
		
		String pk_psndoc = (String) AppUtil.getAppAttr(ShopLeaveRegListView.APP_ID_PK_PSNDOC);
		row.setString(ds.nameToIndex(LeaveoffVO.PK_PSNDOC), pk_psndoc);
//		row.setString(ds.nameToIndex("pk_psnjob_pk_psndoc"), pk_psndoc);
		String pk_psnjob = (String) AppUtil.getAppAttr(ShopLeaveRegListView.APP_ID_PK_PSNJOB);
		row.setString(ds.nameToIndex(LeaveoffVO.PK_PSNJOB), pk_psnjob);
		
		String pk_leavetype = (String)AppUtil.getAppAttr(ShopLeaveRegListView.APP_ID_PK_LEAVETYPE);
		row.setString(ds.nameToIndex(LeaveoffVO.PK_LEAVETYPE), pk_leavetype);
		
		String pk_leavetypecopy = (String)AppUtil.getAppAttr(ShopLeaveRegListView.APP_ID_PK_LEAVETYPECOPY);
		row.setString(ds.nameToIndex(LeaveoffVO.PK_LEAVETYPECOPY), pk_leavetypecopy);
		
		UFDouble leavehour = (UFDouble) AppUtil.getAppAttr(LeaveRegVO.LEAVEHOUR);
		row.setValue(ds.nameToIndex(LeaveoffVO.REGLEAVEHOURCOPY), leavehour);
		
		UFLiteralDate leavebegindate = (UFLiteralDate) AppUtil.getAppAttr(LeaveRegVO.LEAVEBEGINDATE);
		UFLiteralDate leaveenddate = (UFLiteralDate) AppUtil.getAppAttr(LeaveRegVO.LEAVEENDDATE);
		UFDateTime  leavebegintime = (UFDateTime) AppUtil.getAppAttr(LeaveRegVO.LEAVEBEGINTIME);
		UFDateTime  leaveendtime = (UFDateTime) AppUtil.getAppAttr(LeaveRegVO.LEAVEENDTIME);
		
		// 设置销假默认数据
		if(leavebegindate == null && leavebegintime != null){
			leavebegindate = new UFLiteralDate(leavebegintime.getDate().toDate());
		}
		if(leaveenddate == null && leaveendtime != null){
			leaveenddate = new UFLiteralDate(leaveendtime.getDate().toDate());
		}
		if(leavebegindate != null && leavebegintime == null){
			leavebegintime = new UFDateTime(leavebegindate.toDate());
		}
		if(leaveenddate != null && leaveendtime == null){
			leaveendtime = new UFDateTime(leaveenddate.toDate());
		}
		
		row.setValue(ds.nameToIndex(LeaveoffVO.REGBEGINDATECOPY), leavebegindate);
		row.setValue(ds.nameToIndex(LeaveoffVO.REGENDDATECOPY), leaveenddate);
		row.setValue(ds.nameToIndex(LeaveoffVO.REGBEGINTIMECOPY), leavebegintime);
		row.setValue(ds.nameToIndex(LeaveoffVO.REGENDTIMECOPY), leaveendtime);
		
		row.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINDATE), leavebegindate);
		row.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDDATE), leaveenddate);
		row.setValue(ds.nameToIndex(LeaveoffVO.LEAVEBEGINTIME), leavebegintime);
		row.setValue(ds.nameToIndex(LeaveoffVO.LEAVEENDTIME), leaveendtime);
		row.setValue(ds.nameToIndex(LeaveoffVO.REALLYLEAVEHOUR), leavehour);
		row.setValue(ds.nameToIndex(LeaveoffVO.DIFFERENCEHOUR), 0);
		
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute(LeaveRegVO.LEAVEBEGINDATE);
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute(LeaveRegVO.LEAVEENDDATE);
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute(LeaveRegVO.LEAVEBEGINTIME);
		AppLifeCycleContext.current().getApplicationContext().removeAppAttribute(LeaveRegVO.LEAVEENDTIME);
	}

	@Override
	public void onAfterRowAdd(Dataset ds, Row row) {		
	}
}
