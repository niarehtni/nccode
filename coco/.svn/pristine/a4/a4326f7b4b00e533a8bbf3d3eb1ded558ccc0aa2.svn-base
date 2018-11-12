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
	var url = window.globalPath + "/app/AppPsninfo?pk_psndoc=" + pk_psndoc + "&ismainjob="+ ismainjob +"&pk_psnjob="+ pk_psnjob+"&nodecode=E20200101";
   	showDialog(url, transs('js_hi-res0000'), "990", "600", "DeptPsninfo", null, {isShowLine:false});
}
