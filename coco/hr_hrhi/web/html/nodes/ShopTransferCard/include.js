function LinkRender() {
};
LinkRender.render = function(rowIndex, colIndex, value, header, cell) {
	var ds = pageUI.getWidget("list").getDataset("hi_stapply");
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
	proxy.addParam("clc", "nc.bs.hrsms.hi.employ.ShopTransfer.TranListViewMain");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("dsMain_primaryKey", primaryKey);
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}