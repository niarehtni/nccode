package nc.ui.hr.func;

import java.util.HashMap;
import java.util.Map;

import nc.ui.hr.itemsource.view.WaConvertor;
import nc.vo.ta.timeitem.LeaveTypeVO;

import org.apache.commons.lang.StringUtils;

public class TbmValueOfLeaveBalanceHoursParaConvertor extends WaConvertor {
	private static Map<String, String> itemNameMap = null;
	private static String[] ml = { "0", "1", "2", "3", "4", "5", "6" };
	static {
		String[] mlDefault = { "上期結餘", "享有", "當前享有", "已休", "結餘", "凍結", "可用" };
		for (int i = 0; i < ml.length; i++) {
			getItemNameMap().put(ml[i] + ")", mlDefault[i] + ")");
		}
	}

	public String postConvert(String formula) {
		if (StringUtils.isNotEmpty(formula)) {
			String param = formula.substring(formula.indexOf("(") + 1, formula.indexOf(")"));
			String[] params = param.split(",");
			if ((null != params) && (params.length == 2)) {
				if (StringUtils.isEmpty((String) getItemNameMap().get(params[0]))) {
					String sql = "itemtype=0 and timeitemcode='" + params[0] + "'";
					String className = getVoName(sql, LeaveTypeVO.class);
					getItemNameMap().put(params[0], className);
				}

				for (int i = 0; i < params.length; i++) {
					for (int j = 0; j < ml.length; j++) {
						if (params[i].equals(ml[j])) {
							params[i] = (params[i] + ")");
						}
					}
					formula = StringUtils.replaceOnce(
							formula,
							params[i],
							null == getItemNameMap().get(params[i]) ? params[i] : (String) getItemNameMap().get(
									params[i]));
				}
			}
		}

		return formula;
	}

	/**
	 * @return itemNameMap
	 */
	public static Map<String, String> getItemNameMap() {
		if (itemNameMap == null) {
			itemNameMap = new HashMap<String, String>();
		}
		return itemNameMap;
	}

}
