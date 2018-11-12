package nc.bs.hrsms.ta.sss.monthreport.ctrl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author chouhl
 */
public class MonthReportForCleViewLeft extends IQueryController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	// 字段
	public static final String FS_TBMYEAR = "tbmyear";
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

	}

	@Override
	public void simpleValueChanged(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		int colIndex = datasetCellEvent.getColIndex();
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
