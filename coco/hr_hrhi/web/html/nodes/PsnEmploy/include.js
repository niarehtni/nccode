function NameLinkRender() {
};

NameLinkRender.render = function(rowIndex, colIndex, value, header, cell) {
	// 获得当前选中行
	var row = this.getRow(rowIndex);
	var key = row.getCellValueByFieldName("pk_psndoc");
	var ismainjob = row.getCellValueByFieldName("ismainjob");
	var pk_psnjob = row.getCellValueByFieldName("pk_psnjob");
	cell.style.overflow="hidden";
    cell.style.textOverflow="ellipsis";
    cell.style.cursor="default";
    if (header.textAlign !=null&&header.textAlign !="") 
       cell.style.textAlign=header.textAlign;
    else 
       cell.style.textAlign="left";
    var urlContent="<a href='#' onclick=\"namelink('"+key+"', '"+ismainjob+"', '"+pk_psnjob+"');return false;\">"+value+"</a>";
    cell.tip=value;cell.innerHTML=urlContent;
};

function namelink(pk_psndoc,ismainjob,pk_psnjob) {
	var url = window.globalPath + "/app/AppEmpinfo?pk_psndoc=" + pk_psndoc + "&ismainjob="+ ismainjob +"&pk_psnjob="+ pk_psnjob+"&nodecode=E2060103";
   	showDialog(url, transs('js_hi-res0000'), "990", "700", "DeptPsninfo", null, {isShowLine:false});
}

//add by shaochj Jun 26, 2015 begin
/**
 * 查看单据详细
 * 
 * @param pk_post
 */
function showDetail(grid, rowIndex) {
	if (rowIndex == null || rowIndex == undefined) {
		return;
	}
	var ds = pageUI.getWidget("main").getDataset("DsDeptPsn");
	// 获得当前选中行
	var row = grid.getRow(rowIndex);
	var primaryKeyField = ds.getPrimaryKeyField();
	
	var primaryKey = row.getCellValueByFieldName("pk_psndoc");
	var status = row.getCellValueByFieldName("status");
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc", "nc.bs.hrsms.hi.employ.ctrl.DeptPsnViewContr");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("dsMain_primaryKey", primaryKey);
	proxy.addParam("status", status);
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule("main");
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}
//add by shaochj Jun 26, 2015 end
