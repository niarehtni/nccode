package nc.bs.hrsms.ta.sss.leaveinfo.ctrl;

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
 * 员工假期查询的查询模板处理Controller
 * 
 * @author qiaoxp
 * 
 */
public class LeaveInfoQryQueryCtrl extends IQueryController {
	// 字段
	public static final String FS_TBMYEAR = "curyear";
	public static final String FS_TBMMONTH = "curmonth";

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
		if (colIndex != ds.nameToIndex(FS_TBMYEAR)) {// 排除除年度发生
			return;
		}
		// 设置月份的默认选中值
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		Row selRow = ds.getSelectedRow();
		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
		Map<String, String[]> periodMap = TaAppContextUtil.getTBMPeriodVOMap();
		String accyear = (String) datasetCellEvent.getNewValue();
		if (StringUtils.isEmpty(accyear)) {
			if (latestPeriodVO != null) {
				selRow.setValue(ds.nameToIndex(FS_TBMYEAR), latestPeriodVO.getTimeyear());
			} else {
				if (periodMap != null && periodMap.size() > 0) {
					String[] years = periodMap.keySet().toArray(new String[0]);
					if (!ArrayUtils.isEmpty(years)) {
						accyear = years[0];
						selRow.setValue(ds.nameToIndex(FS_TBMYEAR), accyear);
					}
				} else {
					selRow.setValue(ds.nameToIndex(FS_TBMYEAR), null);
				}
			}
		}
		String[] years = null;
		ComboData yearData = widget.getViewModels().getComboData("comb_curyear_value");
		if (periodMap != null && periodMap.size() > 0) {
			years = periodMap.keySet().toArray(new String[0]);
			if (years != null && years.length > 1) {
				Arrays.sort(years);
				Collections.reverse(Arrays.asList(years));
			}
		}
		ComboDataUtil.addCombItemsAfterClean(yearData, years);
		ComboData monthData = widget.getViewModels().getComboData("comb_curmonth_value");
		String[] months = null;
		if (periodMap != null && periodMap.size() > 0) {
			months = periodMap.get(accyear);
		}
		ComboDataUtil.addCombItemsAfterClean(monthData, months);

		if (ArrayUtils.isEmpty(months)) {
			selRow.setValue(ds.nameToIndex(FS_TBMMONTH), null);
			return;
		}
		if (latestPeriodVO != null && !StringUtils.isEmpty(latestPeriodVO.getTimeyear()) && latestPeriodVO.getAccyear().equals(accyear)) {
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
