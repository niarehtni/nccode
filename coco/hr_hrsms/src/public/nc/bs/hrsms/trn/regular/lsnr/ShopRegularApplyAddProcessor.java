package nc.bs.hrsms.trn.regular.lsnr;

import nc.bs.hrss.trn.regular.lsnr.RegularApplyAddProcessor;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;

public class ShopRegularApplyAddProcessor extends RegularApplyAddProcessor{
	
	@Override
	public void onAfterRowAdd(Dataset ds, Row row) {
		
		// 人员基本档案主键
		row.setValue(ds.nameToIndex("pk_psndoc"), null);
		super.onAfterRowAdd(ds, row);
	}
}
