/**
 * 切换日期时监听方法
 * 
 * @param calendar
 */
function calendarChange(calendar) {
	var compId = calendar.compId;
	// 对应日期控件ID
	var year = calendar.year;
	var month = calendar.month;
	var day = calendar.day;
	var dataObj = getTBMCalendarService().execute("getDefaultBeginEndTime",
			year, month, day, compId);
	if (dataObj.split("@").length == 3) {
		// 改变小时
		calendar.hourInput.value = dataObj.split("@")[0];
		// 改变分钟
		calendar.minInput.value = dataObj.split("@")[1];
		// 改变秒
		calendar.secInput.value = dataObj.split("@")[2];
	}

}

/**
 * 根据开始日期以及日期所在时区,自动计算休假/出差/停工待料开始时间/结束时间
 * 
 */
function getTBMCalendarService() {
	return getService("nc.itf.hrss.ta.timeapply.IQueryCalendarService");
};

/** 操作按钮(保存/保存提交) */
window.oprateBtnName = "";
/** 是否继续标志 */
window.isContinue = "";
/**
 * 拦截确定按钮请求, 添加附加参数
 */
function beforeCallServer(proxy, listenerId, eventName, eleid) {
	if ((eleid == "btnSave" && eventName == "onclick")
			|| (eleid == "btnSaveAdd" && eventName == "onclick")
			|| (eleid == "btnSaveCommit" && eventName == "onclick")) {// 保存按钮
		proxy.addParam("isContinue", window.isContinue);
		window.oprateBtnName = eleid;
	}
}