package nc.bs.hrsms.ta.sss.lateearly;

import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.vo.pub.lang.UFLiteralDate;

public class ShopLateEarlyQueryCtrl extends IQueryController {

	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		}
		// ÈÕÆÚ
		if (ds.nameToIndex(ShopLateEarlyConsts.FD_LATEDATE) > -1) {
			selRow.setValue(ds.nameToIndex(ShopLateEarlyConsts.FD_LATEDATE), new UFLiteralDate());
		}
	}

	@Override
	public void simpleValueChanged(DatasetCellEvent dataLoadEvent) {
	}

	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
	}


}
