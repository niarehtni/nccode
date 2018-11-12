package nc.bs.hrsms.ta.sss.shopleave.prcss;

import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.leave.LeavebVO;

public class ShopLeaveLineAddProcessor implements ILineInsertProcessor {

	/**
	 * 行新增前操作
	 */
	@Override
	public void onBeforeRowInsert(Dataset ds, Row row) {

		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		if (viewMain == null) {
			return;
		}
		Dataset dsMaster = viewMain.getViewModels().getDataset(ShopLeaveApplyConsts.DS_MAIN_NAME);
		if (dsMaster == null) {
			return;
		}
		// 主表数据
		Row rowMaster = dsMaster.getSelectedRow();
		if (rowMaster == null) {
			return;
		}
		// 单据所属集团
		row.setValue(ds.nameToIndex("pk_group"), rowMaster.getValue(dsMaster.nameToIndex("pk_group")));
		// 单据所属组织
		row.setValue(ds.nameToIndex("pk_org"), rowMaster.getValue(dsMaster.nameToIndex("pk_org")));
		// 人员基本档案主键
		row.setValue(ds.nameToIndex("pk_psndoc"), rowMaster.getValue(dsMaster.nameToIndex("pk_psndoc")));
		// 人员任职记录主键
		row.setValue(ds.nameToIndex("pk_psnjob"), rowMaster.getValue(dsMaster.nameToIndex("pk_psnjob")));
		// 休假时长
		row.setValue(ds.nameToIndex(LeavebVO.LEAVEHOUR), UFDouble.ZERO_DBL);
	}

	/**
	 * 行新增后操作
	 */
	@Override
	public void onAfterRowInsert(Dataset ds, Row row) {
		new ShopLeaveLineDelProcessor().onAfterRowDel();
	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}


}
