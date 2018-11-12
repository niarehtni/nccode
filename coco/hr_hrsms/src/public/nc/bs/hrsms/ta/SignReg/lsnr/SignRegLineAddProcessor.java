package nc.bs.hrsms.ta.SignReg.lsnr;


import nc.bs.hrsms.ta.SignReg.signreg.SignRegConsts;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;


public class SignRegLineAddProcessor implements ILineInsertProcessor {

	/**
	 * ������ǰ����
	 */
	@Override
	public void onBeforeRowInsert(Dataset ds, Row row) {

		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		if (viewMain == null) {
			return;
		}
		Dataset dsMaster = viewMain.getViewModels().getDataset(SignRegConsts.DS_MAIN_NAME);
		if (dsMaster == null) {
			return;
		}
		// ��������
		Row rowMaster = dsMaster.getSelectedRow();
		if (rowMaster == null) {
			return;
		}
		// ������������
		row.setValue(ds.nameToIndex("pk_group"), rowMaster.getValue(dsMaster.nameToIndex("pk_group")));
		// ����������֯
		row.setValue(ds.nameToIndex("pk_org"), rowMaster.getValue(dsMaster.nameToIndex("pk_org")));
		// ��Ա������������
		row.setValue(ds.nameToIndex("pk_psndoc"), rowMaster.getValue(dsMaster.nameToIndex("pk_psndoc")));
		// ��Ա��ְ��¼����
		row.setValue(ds.nameToIndex("pk_psnjob"), rowMaster.getValue(dsMaster.nameToIndex("pk_psnjob")));
		
	}

	/**
	 * �����������
	 */
	@Override
	public void onAfterRowInsert(Dataset ds, Row row) {
//		new ShopLeaveLineDelProcessor().onAfterRowDel();
	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}


}
