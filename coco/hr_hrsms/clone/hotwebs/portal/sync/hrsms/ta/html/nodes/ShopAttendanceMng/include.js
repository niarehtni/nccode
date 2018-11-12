/**
 * 按人员查看出勤情况
 * 
 * @param {} rowIndex
 */
function showDetail(grid, rowIndex) {
	if (rowIndex == null || rowIndex == undefined) {
		return;
	}
	var ds = pageUI.getWidget("main").getDataset("dsTBMPsndoc");
	// 获得当前选中行
	var row = grid.getRow(rowIndex);
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc","nc.bs.hrsms.ta.sss.ShopAttendance.ctrl.ShopAttendanceMngViewMain");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("dsMain_rowId", row.rowData.rowId);
	proxy.addParam("dsMain_id", "dsTBMPsndoc");
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	var dsr = new DatasetRule('dsTBMPsndoc', 'ds_current_page');
	wdr_main.addDsRule('dsTBMPsndoc', dsr);
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}

/**
 * 按日期查看出勤情况
 * 
 * @param {} headerDivId
 */
function showDateDetail(headerDivTextValue){
	if(headerDivTextValue == null || headerDivTextValue == undefined){
		return;
	}
	var proxy = new ServerProxy(null, null, true);
	var dsId = "dsTBMPsndoc";
	proxy.addParam("clc","nc.bs.hrsms.ta.sss.ShopAttendance.ctrl.ShopAttendanceMngViewMain");
	proxy.addParam("m_n", "showDateDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("headerDivTextValue",headerDivTextValue);
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	var dsr = new DatasetRule(dsId, 'ds_current_page');
	wdr_main.addDsRule(dsId, dsr);
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}