
/**
 * 查看月报详情
 * 
 * @param rowIndex
 */
function showDetail(grid, rowIndex) {
	if (rowIndex == null || rowIndex == undefined) {
		return;
	}
	var ds = pageUI.getWidget("main").getDataset("dsMonthReport");
	// 获得当前选中行
	var row = grid.getRow(rowIndex);
	// var primaryKeyField = ds.getPrimaryKeyField();
	// var primaryKey = row.getCellValue(ds.nameToIndex(primaryKeyField.key));
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc",
			"nc.bs.hrsms.ta.sss.monthreport.ctrl.MonthReportForCleViewMain");
	proxy.addParam("m_n", "showMthReportDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("dsMain_rowId", row.rowData.rowId);
	proxy.addParam("dsMain_id", "dsMonthReport");
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	var dsr = new DatasetRule('dsMonthReport', 'ds_current_page');
	wdr_main.addDsRule('dsMonthReport', dsr);
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}
