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
	 * ������ǰ����
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
		// ��������
		Row rowMaster = dsMaster.getSelectedRow();
		if (rowMaster == null) {
			return;
		}
		// ��������
		row.setValue(ds.nameToIndex("pk_group"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_group")));
		// ������֯
		row.setValue(ds.nameToIndex("pk_org"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_org")));
		// ��Ա������������
		row.setValue(ds.nameToIndex("pk_psndoc"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_psndoc")));
		// ��Ա��ְ��¼����
		row.setValue(ds.nameToIndex("pk_psnjob"),
				rowMaster.getValue(dsMaster.nameToIndex("pk_psnjob")));
		// QXP ��Ա������¼����ҵ��Ԫ����
		// QXP ��Ա������¼����ҵ��Ԫ��ʱ��
		// ����ʱ��
		row.setValue(ds.nameToIndex(AwaybVO.AWAYHOUR), UFDouble.ZERO_DBL);
		// Ԥ֧����
		row.setValue(ds.nameToIndex(AwaybVO.AHEADFEE), UFDouble.ZERO_DBL);
		// ʵ��֧��
		row.setValue(ds.nameToIndex(AwaybVO.FACTFEE), UFDouble.ZERO_DBL);
	}

	/**
	 * �����������
	 */
	@Override
	public void onAfterRowInsert(Dataset ds, Row row) {
		new ShopAwayApplyLineDelProcessor().onAfterRowDel();

	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

}
