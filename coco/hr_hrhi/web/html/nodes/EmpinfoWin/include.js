function NameLinkRender() {
};

NameLinkRender.render = function(rowIndex, colIndex, value, header, cell) {
	var ds = this.model.dataset;
	// 获得当前选中行
	var row = ds.getRow(rowIndex);
	var primaryKeyField = ds.getPrimaryKeyField();
	if (primaryKeyField == null || primaryKeyField == "undefined") {
		return;
	}
	var primaryKey = row.getCellValue(ds.nameToIndex(primaryKeyField.key));
	cell.style.overflow="hidden";
    cell.style.textOverflow="ellipsis";
    cell.style.cursor="default";
    if (header.textAlign !=null&&header.textAlign !="") 
       cell.style.textAlign=header.textAlign;
    else 
       cell.style.textAlign="left";
    var urlContent="<a href='#' onclick=\"namelink('"+primaryKey+"', '" + rowIndex + "', '" + ds.id + "');return false;\">"+transs('js_pub-res0000')+"</a>";
    cell.tip=value;cell.innerHTML=urlContent;
};

function namelink(primaryKey, rowIndex, dsid) {
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc", "nc.bs.hrsms.hi.employ.EmpinfoMainViewController");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("primaryKey", primaryKey);
	proxy.addParam("rowIndex", rowIndex);
	var ds = pageUI.getWidget("main").getDataset(dsid);
	ds.setRowSelected(rowIndex);
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}

function afterFileUpload(){
	window.location.reload();
}