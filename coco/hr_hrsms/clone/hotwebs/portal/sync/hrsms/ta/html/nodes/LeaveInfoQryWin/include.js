// 创建文本导航的显示方法
function ceateTextNavigation(str) {
	if ($("#divTextNavigation").data("textnavigation")) {
		$("#divTextNavigation").textnavigation("destroy");
	}
	$("#divTextNavigation").textnavigation({
		jonsonStr : str,
		valueChangedFunc : leaveTypeChanged
	});
}

/**
 * 创建休假类别导航
 * 
 * @param jsonString
 */
function destroyTextNavigation() {
	if ($("#divTextNavigation").data("textnavigation")) {
		$("#divTextNavigation").empty();
	}
}

/**
 * 休假类别切换事件
 * 
 * @param pk_psndoc
 * @param pk_psnName
 */
function leaveTypeChanged(pk_leavetype_name, pk_leavetype, type, unit) {
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc",
			"nc.bs.hrsms.ta.sss.leaveinfo.ctrl.LeaveInfoQryViewMain");
	proxy.addParam("m_n", "onLeaveTypeChanged");
	proxy.addParam("widget_id", "main");
	proxy.addParam("ci_pk_leavetype", pk_leavetype);
	proxy.addParam("ci_leavesetperiod", type);
	proxy.addParam("ci_pk_leavetype_unit", unit);
	showDefaultLoadingBar();
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}

/**
 * 查看单据详细
 * 
 * @param pk_post
 */
function showDetail(grid, rowIndex) {
	if (rowIndex == null || rowIndex == "undefined") {
		return;
	}
	var datasetId = "dsLeavebalance";
	var ds = pageUI.getWidget("main").getDataset(datasetId);
	// 获得当前选中行
	var row = grid.getRow(rowIndex);
	var rowId = row.rowData.rowId;
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc",
			"nc.bs.hrsms.ta.sss.leaveinfo.ctrl.LeaveInfoQryViewMain");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("dsMain_rowId", rowId);
	proxy.addParam("dsMain_id", datasetId);

	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule('main');
	var dsLeavebalance = new DatasetRule(datasetId, 'ds_current_page');
	wdr_main.addDsRule(datasetId, dsLeavebalance);
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);

	proxy.execute();
}
