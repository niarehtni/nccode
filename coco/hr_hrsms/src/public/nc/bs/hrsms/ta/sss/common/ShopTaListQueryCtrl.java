package nc.bs.hrsms.ta.sss.common;

import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 申请单查询模板处理Controller
 * 
 * @author shaochj
 * @date May 8, 2015
 * 
 */
public class ShopTaListQueryCtrl extends IQueryController {

	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		}
//		if (ds.nameToIndex("approve_state") > -1) {
//			selRow.setValue(ds.nameToIndex("approve_state"), IPfRetCheckInfo.NOSTATE);
//		}
		// 审批状态
		if (ds.nameToIndex("apply_date_start") > -1) {
			// 申请日期-开始日期
			selRow.setValue(ds.nameToIndex("apply_date_start"), new UFLiteralDate().getDateBefore(30));
		}
		if (ds.nameToIndex("apply_date_end") > -1) {
			// 申请日期-结束日期
			selRow.setValue(ds.nameToIndex("apply_date_end"), new UFLiteralDate().toPersisted());
		}
	}

	@Override
	public void simpleValueChanged(DatasetCellEvent dataLoadEvent) {
	}

	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
	}

}
