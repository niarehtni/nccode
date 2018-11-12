package nc.bs.hrsms.ta.sss.shopleave.prcss;

import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.leave.LeavehVO;

public class ShopLeaveAddProcessor extends ShopTaBaseAddProcessor{

	/**
	 * 新增行前的操作
	 */
	@Override
	public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode) {
		super.onBeforeRowAdd(ds, row, billTypeCode);

		// 休假总时长
		row.setValue(ds.nameToIndex(LeavehVO.SUMHOUR), UFDouble.ZERO_DBL);
		// 已休时长
		row.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// 享有时长
		row.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// 冻结时长
		row.setValue(ds.nameToIndex(LeavehVO.FREEZEDAYORHOUR), UFDouble.ZERO_DBL);
		// 可用时长
		row.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), UFDouble.ZERO_DBL);
		// 结余时长
		row.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// 单日哺乳时长小时
		row.setValue(ds.nameToIndex(LeavehVO.LACTATIONHOUR), UFDouble.ZERO_DBL);
		// 假期结算顺序号
		row.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), Integer.valueOf(1));
		// 是否哺乳假标识
		row.setValue(ds.nameToIndex(LeavehVO.ISLACTATION), UFBoolean.FALSE);

	}


	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}
