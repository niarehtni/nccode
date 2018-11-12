/*******************************************************************************
 * 工作日历页面班别字段渲染器
 ******************************************************************************/
var PREFIX_COLOR_ = "color_";
var PREFIX_EDITABLE = "edit_";


CalendarRender.settings = {};
CalendarRender.settings["render_editchange"] = true;

function CalendarRender() {

};
CalendarRender.render = function(rowIndex, colIndex, value, header, cell, parentRowIndex) {
	
	var ds = pageUI.getWidget("main").getDataset("dsCalendar");
	var nameFieldId = header.keyName;
	var row = ds.getRow(rowIndex);// 获得当前选中行
	var colorFieldIndex = ds.nameToIndex(PREFIX_COLOR_ + nameFieldId);
	if (colorFieldIndex > -1) {
		// 设置背景色
		cell.style.backgroundColor = row.getCellValue(colorFieldIndex);
	}
	// 重新设置单元格的值
	if (value == null) {
		value = "";
	}
	cell.tip = value;
	if(!ds.editable){
		var dsId = this.model.dataset.id;
		var othis = this;
		var a = $ce("a");
	 	a.href = "javascript:void(0)";
	 	a.innerHTML = value;
	 	a.onclick = function(e){
		  	showDetail(othis,rowIndex,colIndex,dsId);
		 	e = EventUtil.getEvent();
		  	stopAll(e);
	 	}
	 	cell.appendChild(a);
		cell.style.textAlign ="center";
	}else{
		
		cell.innerHTML = "";
		if((parentRowIndex != null || cell.level != null) && colIndex == 0){
		    if(cell.level != null){
		    	for(var i = 0; i < cell.level; i ++){
					cell.innerHTML += '<span style="float:left">&nbsp;&nbsp;&nbsp;&nbsp;</span>';
				}
		    }
		    else{
		    	cell.innerHTML = '<span style="float:left">&nbsp;&nbsp;&nbsp;&nbsp;</span>';
		    }
		}
		else{
			cell.innerHTML = "";
		}
		var grid = this;
		if(grid.model.treeLevel != null && colIndex == 0 && grid.showTree == true) {
			var gridRow = grid.model.getRow(rowIndex);
			var loadImg = $ce("IMG");
			//loadImg.marginTop = "5px";
			loadImg.style[ATTRFLOAT] = "left";
			if(cell.hasChildren && cell.hasChildren != null && cell.hasChildren == true)	
				loadImg.src = DefaultRender.plusImgSrc;
			else		
				loadImg.src = DefaultRender.minusImgSrc;
			loadImg.plus = true;
			loadImg.initialized = false;
			if (gridRow.loadImg){
				loadImg.plus = gridRow.loadImg.plus;
				loadImg.initialized = gridRow.loadImg.initialized;
			}
			loadImg.style.margin = "6px";
			loadImg.style.marginTop = "9px";
			loadImg.style.width = "8px";
			loadImg.style.height = "8px";
			loadImg.owner = grid;
			loadImg.row = gridRow.rowData;
			loadImg.cell = cell;
			if(cell.level == null)
				cell.level = 0;
			loadImg.onclick = expandGridChild;
			cell.appendChild(loadImg);
			gridRow.loadImg = loadImg;
		}
		var userDiv = null;
		if (typeof fillCellContent != "undefined"){
			userDiv = fillCellContent.call(this, grid, rowIndex, colIndex, value, header, parentRowIndex);
		}
		if (userDiv == null){
			var textSpan = $ce("SPAN");
			textSpan.style.display = "block";
			textSpan.style.overflow = "hidden";
			textSpan.style.space = "nowrap";
			textSpan.style.textOverflow = "ellipsis";
			value = value.replaceAll("<","&lt;");	
			value = value.replaceAll(">","&gt;");	
			if (typeof(value) == "string")
				value = value.replaceAll("\n", "<br/>");
			textSpan.innerHTML = value;
			cell.appendChild(textSpan);
		}
		else	
			cell.appendChild(userDiv);
	}
 
};

/**
 * 班别修改前的判断方法
 */
function beforEditClass(cellEvent) {
	var ds = pageUI.getWidget("main").getDataset("dsCalendar");
	// 获得当前选中行
	var row = ds.getRow(cellEvent.rowIndex);
	var GridComp = cellEvent.obj;
	var header = GridComp.basicHeaders[cellEvent.colIndex];
	var nameFieldId = header.keyName;
	// 是否可编辑
	var isEditableFieldIndex = ds.nameToIndex(PREFIX_EDITABLE + nameFieldId);
	if (isEditableFieldIndex > -1) {
		var editableFlag = row.getCellValue(isEditableFieldIndex);
		if (editableFlag == "true") {
			return true;
		}
	}
	return false;
}

/**
 * 批量调班回调函数
 */
function afterBatchChange() {
	window.location.reload();
}

/**
 * 查看班次详细
 */
function showDetail(grid, rowIndex, colIndex) {
	var ds = pageUI.getWidget("main").getDataset("dsCalendar");
	if (ds.editable) {
		return false;
	}
	//var grid = pageUI.getWidget("main").getComponent("gridPsnCalendar");
	var date = grid.basicHeaders[colIndex].parent.showName;
	// 获得当前选中行
	var row = grid.getRow(rowIndex);
	var pk_psndoc = row.getCellValueByFieldName("pk_psnjob_pk_psndoc");
	// 发起后台调用
	var proxy = new ServerProxy(null, null, true);
	proxy.addParam("clc","nc.bs.hrsms.ta.sss.calendar.ctrl.ViewShiftDetailViewMain");
	proxy.addParam("m_n", "showShift4Calendar");
	proxy.addParam("widget_id", "main");
	proxy.addParam("ci_calendar", date);
	proxy.addParam("ci_pk_psndoc", pk_psndoc);
	proxy.execute();
}