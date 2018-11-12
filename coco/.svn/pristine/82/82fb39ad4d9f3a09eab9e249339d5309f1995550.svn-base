
/**
 * 查看月报详情
 * 
 * @param rowIndex
 */
function showDetail(grid, rowIndex) {
	if (rowIndex == null || rowIndex == undefined) {
		return;
	}
	var ds = pageUI.getWidget("main").getDataset("dsDailyReport");
	// 获得当前选中行
	var row = grid.getRow(rowIndex);
	// var primaryKeyField = ds.getPrimaryKeyField();
	// var primaryKey = row.getCellValue(ds.nameToIndex(primaryKeyField.key));
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc",
			"nc.bs.hrsms.ta.sss.dailyreport.ctrl.DailyReportForCleViewMain");
	proxy.addParam("m_n", "showDayReportDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("dsMain_rowId", row.rowData.rowId);
	proxy.addParam("dsMain_id", "dsDailyReport");
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	var dsr = new DatasetRule('dsDailyReport', 'ds_current_page');
	wdr_main.addDsRule('dsDailyReport', dsr);
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}
