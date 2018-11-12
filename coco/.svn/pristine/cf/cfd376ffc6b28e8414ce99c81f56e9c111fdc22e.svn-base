package nc.bs.hrsms.ta.sss.monthreport.lsnr;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.combodata.CombItem;
import nc.uap.lfw.core.combodata.DynamicComboData;

@SuppressWarnings("serial")
public class YearComboData extends DynamicComboData {

	@Override
	public CombItem[] getAllCombItems() {
		// 管理部门的HR组织主键
		String pk_hr_org = TaAppContextUtil.getHROrg();
		// 当前考勤期间
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(pk_hr_org);
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		if (periodMap == null || periodMap.size() == 0) {
			return new CombItem[0];
		}
		String[] years = periodMap.keySet().toArray(new String[0]);
		if (years == null || years.length == 0) {
			return new CombItem[0];
		}
		if (years.length > 1) {
			Arrays.sort(years);
			Collections.reverse(Arrays.asList(years));
		}
		CombItem[] items = new CombItem[years.length];
		for (int i = 0; i < years.length; i++) {
			items[i] = new CombItem();
			items[i].setValue(String.valueOf(years[i]));
			items[i].setText(String.valueOf(years[i]));
		}
		return items;
	}

	@Override
	protected CombItem[] createCombItems() {
		return null;
	}

}
