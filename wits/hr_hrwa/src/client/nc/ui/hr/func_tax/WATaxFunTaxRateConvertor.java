package nc.ui.hr.func_tax;

import java.util.HashMap;
import java.util.Map;

import nc.hr.utils.ResHelper;
import nc.ui.hr.itemsource.view.WaConvertor;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.ClassItemContext;

import org.apache.commons.lang.StringUtils;

/**
 * 适用税率
 * 根据税表获取适用税率(参数：薪资项目)
 * 
 * @author xuhw
 */
public class WATaxFunTaxRateConvertor extends WaConvertor {
	private static Map<String, String> map = new HashMap<String, String>();
	@Override
	public String preConvert(String formula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String postConvert(String formula) {
		// TODO Auto-generated method stub
		if (StringUtils.isNotEmpty(formula)) {
			String pk_waclass = ((ClassItemContext)super.getContext()).getPk_wa_class();
			String param = formula.substring(formula.indexOf("(") + 1,
					formula.indexOf(")"));
			String[] params = param.split(",");
			if (null != params && params.length == 1) {

				if (StringUtils.isEmpty(map.get(params[0]))) {
					String sql = " itemkey='" + params[0]
							+ "' and pk_wa_class='" + pk_waclass + "'";
					String className = getVoName(sql, WaClassItemVO.class);
					map.put(params[0], className);
				}

				for (int i = 0; i < params.length; i++) {
					formula = formula.replaceAll(params[i], null == map
							.get(params[i]) ? params[i] : map.get(params[i]));
				}
			}

		}
		return formula;
	}
}
