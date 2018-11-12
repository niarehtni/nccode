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