/**
 * 查看单据详细
 * 
 * @param rowIndex
 */
function showDetail(grid, rowIndex) {
	if (rowIndex == null || rowIndex == "undefined") {
		return;
	}
	var ds = pageUI.getWidget("main").getDataset("hrtaovertimereg");
	// 获得当前选中行
	var row = grid.getRow(rowIndex);
	var primaryKeyField = ds.getPrimaryKeyField();
	var primaryKey = row.getCellValueByFieldName(primaryKeyField.key);
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc", "nc.bs.hrsms.ta.sss.overtime.ctrl.ShopOverTimeRegListView");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("dsMain_primaryKey", primaryKey);
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}