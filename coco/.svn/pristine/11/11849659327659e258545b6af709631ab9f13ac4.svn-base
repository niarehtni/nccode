/**
 * 每次发起服务器调用前，将当前日历定位的日期送到后台备用
 * 
 * @param proxy
 * @param listenerId
 * @param eventName
 * @param eleid
 */
function beforeCallServer(proxy, listenerId, eventName, eleid) {
	if (eleid == "list_save" && eventName == "onclick") {// 保存按钮
		proxy.addParam("widget_id", "main");
		proxy.addParam("ci_year", curYear);
		proxy.addParam("ci_month", curMonth);
		proxy.addParam("ci_pk_psndoc", getSessionAttribute("ci_pk_psndoc"));
	}
}

/**
 * 创建 人员姓名导航
 * 
 * @param jsonString
 */
function ceateTextNavigation(str) {
	if ($("#divTextNavigation").data("textnavigation")) {
		$("#divTextNavigation").textnavigation("destroy");
	}
	$("#divTextNavigation").textnavigation({
		jonsonStr : str,
		valueChangedFunc : psnNameChagned
	});
}

/**
 * 创建 人员姓名导航
 * 
 * @param jsonString
 */
function destroyTextNavigation() {
	if ($("#divTextNavigation").data("textnavigation")) {
		$("#divTextNavigation").empty();
	}
}

/**
 * 人员姓名---切换事件
 * 
 * @param pk_psndoc
 * @param pk_psnName
 */
function psnNameChagned(name, value, type) {
	var oldValue = getSessionAttribute("ci_pk_psndoc");
	if (oldValue == value) {
		return;
	}
	// 日历变化后事件
	afterDateChange(curYear, curMonth, value);
}

/**
 * 加载日历
 * 
 * @param date
 */
function loadCalendar(lYear, lMonth, currentlangcode) {
	curYear = lYear;
	curMonth = lMonth;
	fillBox(currentlangcode);// 根据当前年月填充每日单元格
}

/**
 * 日历变化后事件
 */
function afterDateChange(curYear, curMonth, curPnsdocPk) {
	if (curPnsdocPk == null || curPnsdocPk == "" || curPnsdocPk == undefined) {
		curPnsdocPk = getSessionAttribute("ci_pk_psndoc");// 当前选中人员
	}
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc", "nc.bs.hrsms.ta.sss.calendar.ctrl.WorkCalendarForPsnViewMain");
	proxy.addParam("m_n", "onDateChanged");
	proxy.addParam("widget_id", "main");
	proxy.addParam("ci_year", curYear);
	proxy.addParam("ci_month", curMonth);
	proxy.addParam("ci_pk_psndoc", curPnsdocPk);
	proxy.execute();
}

/**
 * 将选中的调班参照的值,更新到工作日历的指定单元格
 */
function updateShiftDiv() {
	var dsCalendar = pageUI.getWidget("main").getDataset("dsCalendar");
	var selRow = dsCalendar.getSelectedRow();
	if (selRow == null) {
		return;
	}
	var shiftDiv = $("#" + selRow.rowId);// 获得班次Div
	if (shiftDiv != null && shiftDiv != undefined) {
		var colIndex = dsCalendar.nameToIndex("pk_shift_name");
		if (colIndex == -1) {
			return;
		}
		var pk_shift_name = selRow.getCellValue(colIndex); // 标准日期对应的班次名称
		if (pk_shift_name != null && pk_shift_name != undefined
				&& pk_shift_name != "") {
			shiftDiv.text(pk_shift_name);
		} else {
			var tiptext = transs('js_ta-res0000');
			shiftDiv.text(tiptext);
		}
	}

}

// @@@ 弹出班次详细
function popShiftDetail(ds, row) {
	// 人员基本信息
	var pk_psndoc = row.getCellValue(ds.nameToIndex("pk_psndoc"));
	// 日期
	var calendar = row.getCellValue(ds.nameToIndex("calendar"));
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc","nc.bs.hrsms.ta.sss.calendar.ctrl.ViewShiftDetailViewMain");
	proxy.addParam("m_n", "showShift4Calendar");
	proxy.addParam("ci_pk_psndoc", pk_psndoc);
	proxy.addParam("ci_calendar", calendar);
	showDefaultLoadingBar();
	proxy.execute();
}
/**
 * 填充总工时
 * add shaochj 【太平鸟项目】
 */
function setTotalTimes(str){
	var td = $("#totaltimes");
	var content = $("#totaltimes").text();
	//alert(str);
	if(content!=null&&content!=""){
		$("#totaltimes").empty();
		td.append("<span style='float:right;padding-right:15px;font-weight:bold;color:#5599FF'>总工时："+str+"小时</span>");
	}else{
		td.append("<span style='float:right;padding-right:15px;font-weight:bold;color:#5599FF'>总工时："+str+"小时</span>");
	}
}