// 操作按钮
window.operator = null;
// 是否继续标志
window.isContinue = null;
/**
 * 拦截确定按钮请求, 添加附加参数
 */
function beforeCallServer(proxy, listenerId, eventName, eleid) {
	window.operator = null;
	// 拦截查询详细请求, 添加附加参数
	if (eleid == "onBillSave" && eventName == "onclick") {// 保存按钮
		window.operator = "onBillSave";
		if (window.isContinue) {
			window.isContinue = null;
			proxy.addParam("isContinue", "true");
		}
	} else if (eleid == "onSaveCreate" && eventName == "onclick") {// 保存并新增按钮
		window.operator = "onSaveCreate";
		if (window.isContinue) {
			window.isContinue = null;
			proxy.addParam("isContinue", "true");
		}
	}
}

function LinkRender() {
};
LinkRender.render = function(rowIndex, colIndex, value, header, cell) {
	var ds = pageUI.getWidget("list").getDataset("hi_regapply");
	// 获得当前选中行
	var row = ds.getRow(rowIndex);
	var primaryKeyField = ds.getPrimaryKeyField();
	if (primaryKeyField == null || primaryKeyField == "undefined") {
		return;
	}
	cell.style.overflow = "hidden";
	cell.style.textOverflow = "ellipsis";
	cell.style.cursor = "default";
	if (header.textAlign != null && header.textAlign != "") {
		cell.style.textAlign = header.textAlign;
	} else {
		cell.style.textAlign = "left";
	}
	var primaryKey = row.getCellValue(ds.nameToIndex(primaryKeyField.key));
	var urlContent = "<a href='#' onclick=\"showDetail('" + primaryKey
			+ "');return false;\">" + value + "</a>";
	cell.tip = value;
	cell.innerHTML = urlContent;
};

/**
 * 查看单据详细
 * 
 * @param pk_post
 */
function showDetail(primaryKey) {
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc", "nc.bs.hrsms.hi.employ.ShopRegular.MainViewController");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("dsMain_primaryKey", primaryKey);
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}