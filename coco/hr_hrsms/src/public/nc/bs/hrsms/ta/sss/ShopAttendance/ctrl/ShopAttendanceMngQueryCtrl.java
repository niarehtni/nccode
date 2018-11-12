package nc.bs.hrsms.ta.sss.ShopAttendance.ctrl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
//import nc.bs.hrss.ta.timedata.TimeDataForMngPageModel

import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForMngPageModel;
//import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;

import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class ShopAttendanceMngQueryCtrl extends IQueryController {

	// 字段
	//年度
	public static final String FS_TBMYEAR = "tbmyear";
	//期间
	public static final String FS_TBMMONTH = "tbmmonth";

	/**
	 * 设置默认值
	 */
	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		}
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
		Map<String, String[]> periodMap = TaAppContextUtil.getTBMPeriodVOMap();
		if (latestPeriodVO == null) {
			if (periodMap != null && periodMap.size() > 0) {
				String[] years = periodMap.keySet().toArray(new String[0]);
				if (!ArrayUtils.isEmpty(years)) {
					selRow.setValue(ds.nameToIndex(FS_TBMYEAR), years[0]);
				}
			} else {
				selRow.setValue(ds.nameToIndex(FS_TBMYEAR), null);
			}
		} else {
			selRow.setValue(ds.nameToIndex(FS_TBMYEAR), latestPeriodVO.getTimeyear());
		}

		SessionBean session = SessionUtil.getSessionBean();
		String psnname = (String) session.getExtendAttributeValue(ShopAttForMngPageModel.FIELD_TIME_DATA_PSNNAME);
		if (!StringUtil.isEmptyWithTrim(psnname)) {
			selRow.setValue(ds.nameToIndex(ShopAttForMngPageModel.FIELD_PK_PSNDOC_NAME), psnname);
			session.removeExtendAttribute(ShopAttForMngPageModel.FIELD_TIME_DATA_PSNNAME);
		}

	}

	@Override
	public void simpleValueChanged(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		String oldValue = (String) datasetCellEvent.getOldValue();
		String newValue = (String) datasetCellEvent.getNewValue();
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex == ds.nameToIndex(FS_TBMYEAR) || colIndex == ds.nameToIndex(FS_TBMMONTH)) {
			ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
			if (oldValue.equals(newValue)) {
				appCxt.addAppAttribute(ShopAttendanceMngViewMain.SESSION_DATE_CHANGE, UFBoolean.FALSE);
			} else {
				// 开始日期/结束日期发生了变化
				appCxt.addAppAttribute(ShopAttendanceMngViewMain.SESSION_DATE_CHANGE, UFBoolean.TRUE);
			}
		} else if (colIndex == ds.nameToIndex(ShopAttForMngPageModel.FIELD_PK_PSNDOC_NAME)) {
			if (!StringUtils.isEmpty(oldValue) && !oldValue.equals(newValue)) {
				SessionBean session = SessionUtil.getSessionBean();
				session.removeExtendAttribute(PsndocVO.PK_PSNDOC);
			}
		}
		if (colIndex != ds.nameToIndex(FS_TBMYEAR)) {// 年度发生变更
			return;
		}
		// 设置月份的默认选中值
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Row selRow = ds.getSelectedRow();
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();

		Map<String, String[]> periodMap = TaAppContextUtil.getTBMPeriodVOMap();
		String accyear = (String) datasetCellEvent.getNewValue();
		if (latestPeriodVO == null) {
			if (periodMap != null && periodMap.size() > 0) {
				String[] years = periodMap.keySet().toArray(new String[0]);
				if (!ArrayUtils.isEmpty(years)) {
					accyear = years[0];
					selRow.setValue(ds.nameToIndex(FS_TBMYEAR), accyear);
				}
			} else {
				selRow.setValue(ds.nameToIndex(FS_TBMYEAR), null);
			}
			// selRow.setValue(ds.nameToIndex(FS_TBMMONTH), null);
		} else {
			// edit by shaochj Jan 28, 2016  begin
			//原代码：/* */
//			selRow.setValue(ds.nameToIndex(FS_TBMYEAR), latestPeriodVO.getTimeyear());
			if(!StringUtils.isEmpty(accyear)){
				selRow.setValue(ds.nameToIndex(FS_TBMYEAR),accyear);
			}else{
				selRow.setValue(ds.nameToIndex(FS_TBMYEAR), latestPeriodVO.getTimeyear());
			}
			// edit by shaochj Jan 28, 2016 end
		}

		ComboData yearData = widget.getViewModels().getComboData("comb_tbmyear_value");
		String[] years = null;
		if (periodMap != null && periodMap.size() > 0) {
			years = periodMap.keySet().toArray(new String[0]);
			if (years != null && years.length > 1) {
				Arrays.sort(years);
				Collections.reverse(Arrays.asList(years));
			}
		}
		ComboDataUtil.addCombItemsAfterClean(yearData, years);
		ComboData monthData = widget.getViewModels().getComboData("comb_tbmmonth_value");
		String[] months = null;
		if (periodMap != null && periodMap.size() > 0) {
			months = periodMap.get(accyear);
		}
		ComboDataUtil.addCombItemsAfterClean(monthData, months);

		if (ArrayUtils.isEmpty(months)) {
			selRow.setValue(ds.nameToIndex(FS_TBMMONTH), null);
			return;
		}
		if (latestPeriodVO != null && !StringUtils.isEmpty(latestPeriodVO.getTimeyear())
				&& latestPeriodVO.getTimeyear().equals(accyear)) {
			String accmonth = TaAppContextUtil.getLatestPeriodVO().getTimemonth();
			for (String month : months) {
				if (month.equals(accmonth)) {
					selRow.setValue(ds.nameToIndex(FS_TBMMONTH), accmonth);
					break;
				}
			}
		} else {
			selRow.setValue(ds.nameToIndex(FS_TBMMONTH), months[0]);
		}

	}

	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
	}

}