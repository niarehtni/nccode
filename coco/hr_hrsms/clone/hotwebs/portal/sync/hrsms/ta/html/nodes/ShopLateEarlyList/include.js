

function TimeDataDecimalRender() {};
TimeDataDecimalRender.prototype = new DecimalRender();
TimeDataDecimalRender.render = function(row, col, value, header, cell) {
	DecimalRender.render.call(this, row, col, value, header, cell);
	if(value>0){
//		var dsTimeData = pageUI.getWidget("main").getDataset("dsManualData");
//		alert("col==="+dsTimeData.nameToIndex("latelength"));
		if(col == 7){//迟到
			cell.style.backgroundColor = "#FFBB77";
		}else if(col == 8){//早退
			cell.style.backgroundColor = "#FFBB77";
		}else if(col == 9||col == 10){
			cell.style.backgroundColor = "#FF7777";//FF7777
		}
//		rendCell(row, col, value, header, cell);
	}
};

/*******************************************************************************
 * 手工考勤出勤情况字段渲染器
 ******************************************************************************/
function ManualDataRender() {
};
ManualDataRender.prototype = new ComboRender();
ManualDataRender.render = function(row, col, value, header, cell) {
	ComboRender.render.call(this, row, col, value, header, cell);
	if(value==1 || value==2 ){
		rendCell(row, col, value, header, cell);
	}
};

/*******************************************************************************
 * 绘制单元格颜色
 ******************************************************************************/
function rendCell(row, col, value, header, cell) {
	var dsTimeData = pageUI.getWidget("main").getDataset("dsManualData");
	var onebeginstatus = dsTimeData.getValueAt(row, dsTimeData.nameToIndex("onebeginstatus"));
	var twoendstatus = dsTimeData.getValueAt(row, dsTimeData.nameToIndex("twoendstatus"));
	var key = header.keyName;
	
	if((value ==1 && onebeginstatus==1) || (value ==1 && twoendstatus==1)){//迟到或早退
		cell.style.backgroundColor = "#FFBB77";//#EE3333 FF6622 //#77DD55 FFBB77
	}else if((value ==2 && onebeginstatus==2) || (value ==2 && twoendstatus==2)){//未出勤
		cell.style.backgroundColor = "#FF7777";//FF7777
	}
}


