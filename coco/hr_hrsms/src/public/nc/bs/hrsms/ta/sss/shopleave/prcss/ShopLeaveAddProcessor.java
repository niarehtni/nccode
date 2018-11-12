package nc.bs.hrsms.ta.sss.shopleave.prcss;

import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.leave.LeavehVO;

public class ShopLeaveAddProcessor extends ShopTaBaseAddProcessor{

	/**
	 * ������ǰ�Ĳ���
	 */
	@Override
	public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode) {
		super.onBeforeRowAdd(ds, row, billTypeCode);

		// �ݼ���ʱ��
		row.setValue(ds.nameToIndex(LeavehVO.SUMHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeavehVO.FREEZEDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeavehVO.USEFULDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// ���ղ���ʱ��Сʱ
		row.setValue(ds.nameToIndex(LeavehVO.LACTATIONHOUR), UFDouble.ZERO_DBL);
		// ���ڽ���˳���
		row.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), Integer.valueOf(1));
		// �Ƿ���ٱ�ʶ
		row.setValue(ds.nameToIndex(LeavehVO.ISLACTATION), UFBoolean.FALSE);

	}


	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}
