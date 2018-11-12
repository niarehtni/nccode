/**
 * 设置单元格可编辑
 * @param {} cellEvent
 * @return {Boolean}
 */
function beforEditClass(cellEvent) {
	var rowIndex = parseInt(cellEvent.rowIndex);
	var colIndex = parseInt(cellEvent.colIndex);
	var dsTimeData = pageUI.getWidget("main").getDataset("dsManualData");
	var row = dsTimeData.getRow(rowIndex);
	
	var grid = cellEvent.obj;
	var fieldId = grid.basicHeaders[colIndex].keyName;
	
//	if(window.editList == null){
//		window.editList = eval("(" + getSessionAttribute("editList") + ")");
//	}else {
//		var curPageEditList = eval("(" + getSessionAttribute("editList") + ")");
//		window.editList = window.editList.concat(curPageEditList);
//	}
	var key =  row.getCellValue(dsTimeData.nameToIndex("pk_psndoc"))
			+ row.getCellValue(dsTimeData.nameToIndex("calendar"))
			+ fieldId;
	var isEdit = window.editList[key];
	if(isEdit){
		return true;
	}
	return false;
}

function setEditList(){
	if (window.editList == null) {
		window.editList = eval("(" + getSessionAttribute("editList") + ")");
	} else {
		var oldPageEditList = window.editList;
		var curPageEditList = eval("(" + getSessionAttribute("editList") + ")");
		window.editList = mergeJsonObject(oldPageEditList, curPageEditList);
	}
}

/**
 * 查看单据详细
 * 
 * @param pk_post
 */
function showDetail(grid, rowIndex) {
	if (rowIndex == null || rowIndex == undefined) {
		return;
	}
	
	var dsId = "dsMachineData";
	var ds = pageUI.getWidget("main").getDataset(dsId);
	// 获得当前选中行
	//var row = ds.getRow(rowIndex);
	var row = grid.getRow(rowIndex);
	var primaryKeyField = ds.getPrimaryKeyField();
	var primaryKey = row.getCellValue(ds.nameToIndex(primaryKeyField.key));
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc","nc.bs.hrsms.ta.sss.ShopAttendance.ctrl.ViewShopAttDetailViewMain");
	proxy.addParam("m_n", "showDetail");
	proxy.addParam("widget_id", "main");
	proxy.addParam("dsMain_primaryKey", primaryKey);
	proxy.addParam("dsMain_id", dsId);
	proxy.addParam("dsMain_rowId", row.rowData.rowId);
	var sbr = new SubmitRule();
	var wdr_main = new WidgetRule('main');
	var dsMain = new DatasetRule(dsId, 'ds_current_page');
	wdr_main.addDsRule(dsId, dsMain);
	sbr.addWidgetRule("main", wdr_main);
	proxy.setSubmitRule(sbr);
	proxy.execute();
}

/**
 * 查看详细
 */
function DetailRender() {
};
DetailRender.render = function(rowIndex, colIndex, value, header, cell) {
	var dsId = this.model.dataset.id;
	var showName = transs('js_ta-res0020');
	var othis = this;
	
	var a = $ce("a");
	a.innerHTML = showName;
 	a.href = "javascript:void(0)";
 	
 	a.onclick = function(e){
	  	showDetail(othis,rowIndex,colIndex,dsId);
	 	e = EventUtil.getEvent();
	  	stopAll(e);
 	}
 	cell.appendChild(a);
 	
	cell.style.textAlign = "center"; 
};

