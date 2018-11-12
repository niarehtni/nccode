package nc.bs.hrsms.ta.sss.leaveinfo.lsnr;

import java.util.Map;

import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.combodata.CombItem;
import nc.uap.lfw.core.combodata.DynamicComboData;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("serial")
public class MonthComboData extends DynamicComboData {

	@Override
	public CombItem[] getAllCombItems() {

		// 管理部门的HR组织主键
		Map<String, String[]> periodMap = TaAppContextUtil.getTBMPeriodVOMap();

		PeriodVO latestPeriodVO = TaAppContextUtil.getLatestPeriodVO();
		if (periodMap == null || periodMap.size() == 0) {
			return new CombItem[0];
		}
		String accyear = null;
		if (latestPeriodVO != null) {
			accyear = latestPeriodVO.getTimeyear();
		} else {
			if (periodMap != null && periodMap.size() > 0) {
				String[] years = periodMap.keySet().toArray(new String[0]);
				if (!ArrayUtils.isEmpty(years)) {
					accyear = years[0];
				}
			}
		}
		// 未包含选中年度
		if (!periodMap.keySet().contains(accyear)) {
			return new CombItem[0];

		}
		String[] months = periodMap.get(accyear);
		if (ArrayUtils.isEmpty(months)) {
			return new CombItem[0];
		}
		CombItem[] items = new CombItem[months.length];
		for (int i = 0; i < months.length; i++) {
			items[i] = new CombItem();
			items[i].setValue(String.valueOf(months[i]));
			items[i].setText(String.valueOf(months[i]));
		}
		return items;
	}

	@Override
	protected CombItem[] createCombItems() {
		return null;
	}

}
