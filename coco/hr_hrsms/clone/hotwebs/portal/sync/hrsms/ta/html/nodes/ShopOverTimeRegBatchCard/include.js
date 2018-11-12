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
	var dataObj = getTBMCalendarService().execute(
			"getDefaultOvertimeBeginEndTime", year, month, day, compId);
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

/**
 * 加班子表,是否校验的Render
 */
function TaBooleanRender() {
}
TaBooleanRender.render = function(rowIndex, colIndex, value, header, cell) {
	cell.style.overflow = "hidden";
	cell.style.textOverflow = "ellipsis";
	cell.style.cursor = "default";
	value = (value == null) ? "" : value.toString();
	var grid = header.owner;
	if (GridComp.ROW_HEIGHT == DefaultRender.HEADERROW_HEIGHT)
		cell.style.paddingTop = "0px";
	else
		cell.style.paddingTop = "0px";

	var checkBox = $ce("INPUT");
	checkBox.type = "checkbox";
	checkBox.style.marginTop = "5px";
	checkBox.rowIndex = rowIndex;
	checkBox.colIndex = colIndex;
	cell.innerHTML = "";
	cell.appendChild(checkBox);
	cell.checkBox = checkBox;

	if (header.valuePair != null) {
		if (value == header.valuePair[0]) {
			cell.trueValue = value;
			checkBox.defaultChecked = true;
			checkBox.checked = true;
		} else if (value == header.valuePair[1]) {
			cell.trueValue = value;
			checkBox.defaultChecked = false;
			checkBox.checked = false;
		}
		if (header.columEditable == false || grid.editable == false) {
			checkBox.disabled = true;
		}
	}

	var ds = this.model.dataset;
	var row = ds.getRow(rowIndex);
	var isEditable = row.getCellValue(ds.nameToIndex("isEditable"));//checkbox是否可编辑
	if(!isEditable || isEditable == "true"){
		checkBox.disabled = false;
	}else{
		checkBox.disabled = true;
	}
	
	checkBox.onclick = function() {
		var pk_org = row.getCellValue(ds.nameToIndex("pk_org"));// 组织
		var pk_psndoc = row.getCellValue(ds.nameToIndex("pk_psndoc"));// 人员主键
		var pk_psnjob = row.getCellValue(ds.nameToIndex("pk_psnjob"));// 人员任职主键
		var beigdate = row.getCellValue(ds.nameToIndex("overtimebegindate"));// 开始日期
		var enddate = row.getCellValue(ds.nameToIndex("overtimeenddate"));// 结束日期
		var beigntime = row.getCellValue(ds.nameToIndex("overtimebegintime"));// 开始时间
		var endtime = row.getCellValue(ds.nameToIndex("overtimeendtime"));// 结束时间
		
		if (beigdate == null || enddate == null || beigntime == null
				|| endtime == null) {
			cell.trueValue = false;
			checkBox.defaultChecked = false;
			checkBox.checked = false;
		} else {
			var dataObj = getTBMCalendarService().execute("isCanCheck",
					"$S_" + pk_org, "$S_" + pk_psndoc, "$S_" + pk_psnjob,
					"$S_" + getUFLiteraldateToString(beigdate),
					"$S_" + getUFLiteraldateToString(enddate),
					"$S_" + getDateTimeToString(beigntime),
					"$S_" + getDateTimeToString(endtime));
			if (!dataObj) {
				cell.trueValue = false;
				checkBox.defaultChecked = false;
				checkBox.checked = false;
			} else {
				rowIndex = cell.rowIndex;// 行索引从单元格中获取
				if (this.checked == true) {
					grid.model.setValueAt(rowIndex, colIndex,
							header.valuePair[0]);
				} else {
					grid.model.setValueAt(rowIndex, colIndex,
							header.valuePair[1]);
				}
			}
		}

	};
}

function getUFLiteraldateToString(temp) {
	if (temp.indexOf("-") > -1)
		temp = temp.replace(/\-/g, "/");
	var date = new Date(temp);
	temp = new DatasetRow().dateFormat(date);
	temp = new DatasetRow().dateToUTCString(temp);
	return temp;
}

function getDateTimeToString(temp) {
	temp = new DatasetRow().dateToUTCString(temp);
	return temp;
}

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