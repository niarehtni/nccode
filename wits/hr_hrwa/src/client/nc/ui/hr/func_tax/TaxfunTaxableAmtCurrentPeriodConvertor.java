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
 * 本期应纳税所得额  根据增加属性 扣税标志进行计算 额外前去 - 附加费用扣除额(参数：本次扣税基数，附加费用扣除额)
 * 
 * @author harvey
 */
public class TaxfunTaxableAmtCurrentPeriodConvertor extends WaConvertor {
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
			if (null != params && params.length == 2) {

				if (StringUtils.isEmpty(map.get(params[0]))) {
					String sql = " itemkey='" + params[0]
							+ "' and pk_wa_class='" + pk_waclass + "'";
					String className = getVoName(sql, WaClassItemVO.class);
					map.put(params[0], className);
				}

				if (StringUtils.isEmpty(map.get(params[1]))) {
					String sql = " itemkey='" + params[1]
							+ "' and pk_wa_class='" + pk_waclass + "'";
					String className = getVoName(sql, WaClassItemVO.class);
					map.put(params[1], className);
				}

				formula = formula.replaceAll(params[1], null == map
						.get(params[1]) ? params[1] : map.get(params[1]));
				formula = formula.replaceAll(params[0], null == map
						.get(params[0]) ? params[0] : map.get(params[0]));
				 
			}

		}
		return formula;
	}
}
