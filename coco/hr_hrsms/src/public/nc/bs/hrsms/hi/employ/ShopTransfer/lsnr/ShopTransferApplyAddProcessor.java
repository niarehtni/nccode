package nc.bs.hrsms.hi.employ.ShopTransfer.lsnr;

import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;

public class ShopTransferApplyAddProcessor extends TransferApplyAddProcessor{

	
	@Override
	public void onAfterRowAdd(Dataset ds, Row row) {
		// ��Ա������������
		row.setValue(ds.nameToIndex("pk_psndoc"), null);
		super.onAfterRowAdd(ds, row);
		
	}
}
