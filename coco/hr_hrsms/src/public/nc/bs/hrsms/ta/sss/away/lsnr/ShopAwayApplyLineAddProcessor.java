package nc.bs.hrsms.ta.sss.away.lsnr;

import nc.bs.hrsms.ta.sss.away.ShopAwayApplyConsts;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.away.AwaybVO;

public class ShopAwayApplyLineAddProcessor implements ILineInsertProcessor {

	/**
	 * 行新增前操作
	 */
	@Override
	public void onBeforeRowInsert(Dataset ds, Row row) {

		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		if (viewMain == null) {
			return;
		}
		Dataset dsMaster = viewMain.getViewModels().getDataset(
				ShopAwayApplyConsts.DS_MAIN_NAME);
		if (dsMaster == null) {
			return;
		}
		// 主表数据
		Row rowMaster = dsMaster.getSelectedRow();
		if (rowMaster == null) {
			return;
		}
		// 所属集团
		row.setValue(ds.nameToIndex("pk_group"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_group")));
		// 所属组织
		row.setValue(ds.nameToIndex("pk_org"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_org")));
		// 人员基本档案主键
		row.setValue(ds.nameToIndex("pk_psndoc"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_psndoc")));
		// 人员任职记录主键
		row.setValue(ds.nameToIndex("pk_psnjob"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_psnjob")));
		// QXP 人员工作记录所在业务单元主键
		// QXP 人员工作记录所在业务单元的时区
		// 出差时长
		row.setValue(ds.nameToIndex(AwaybVO.AWAYHOUR), UFDouble.ZERO_DBL);
		// 预支费用
		row.setValue(ds.nameToIndex(AwaybVO.AHEADFEE), UFDouble.ZERO_DBL);
		// 实际支出
		row.setValue(ds.nameToIndex(AwaybVO.FACTFEE), UFDouble.ZERO_DBL);
	}

	/**
	 * 行新增后操作
	 */
	@Override
	public void onAfterRowInsert(Dataset ds, Row row) {
		new ShopAwayApplyLineDelProcessor().onAfterRowDel();

	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

}
