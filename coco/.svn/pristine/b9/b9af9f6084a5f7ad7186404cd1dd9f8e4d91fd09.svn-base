var curYear, curMonth;
var curPnsdocPk;
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
	//alert("name==="+name);
	//alert("value==="+value);
	//alert("type==="+type);
	var oldValue = getSessionAttribute("ci_pk_psndoc");
	if (oldValue == value) {
		return;
	}
	// 人员变化后事件
	afterDateChange(curYear, curMonth, value);
}

/**
 * 加载
 * 
 * @param date
 */
function loadCardInfo(lYear, lMonth) {
	curYear = lYear;
	curMonth = lMonth;
	//alert(lPsndocPk);
	//curPnsdocPk = lPsndocPk;
}
/**
 * 人员变化后事件
 */
function afterDateChange(curYear, curMonth, curPnsdocPk) {
	if (curPnsdocPk == null || curPnsdocPk == "" || curPnsdocPk == undefined) {
		curPnsdocPk = getSessionAttribute("ci_pk_psndoc");// 当前选中人员
	}
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc", "nc.bs.hrsms.ta.sss.credit.ctrl.CreditCardRecordListViewMain");
	proxy.addParam("m_n", "onPsnNameChanged");
	proxy.addParam("widget_id", "main");
	proxy.addParam("ci_year", curYear);
	proxy.addParam("ci_month", curMonth);
	proxy.addParam("ci_pk_psndoc", curPnsdocPk);
	proxy.execute();
}

/**
 * 查询定位姓名
 * @param value
 */
function queryByName(value){
	var item = $("#"+value+"");
	// 删除旧对象的CSS，添加新对象的CSS
	if ($("span").hasClass("item-span-active")) {
		$("span").removeClass("item-span-active");
	}
	item.addClass("item-span-active");
}
