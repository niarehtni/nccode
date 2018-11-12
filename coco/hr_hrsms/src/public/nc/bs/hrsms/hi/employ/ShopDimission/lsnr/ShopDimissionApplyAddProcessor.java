package nc.bs.hrsms.hi.employ.ShopDimission.lsnr;

import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;

public class ShopDimissionApplyAddProcessor extends DimissionApplyAddProcessor{
	
	
	@Override
	public void onAfterRowAdd(Dataset ds, Row row) {
		// 人员基本档案主键
		row.setValue(ds.nameToIndex("pk_psndoc"), null);
		super.onAfterRowAdd(ds, row);
	}
}
