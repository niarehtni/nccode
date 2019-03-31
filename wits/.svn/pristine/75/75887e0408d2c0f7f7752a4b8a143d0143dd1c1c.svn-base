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
 * 累计应纳税所得额
 * 按照一定范围获取某个项目的累计数据(参数：薪资项目(合并计税、多次发薪的情况当期先审核的数据会被累计))
 * 
 * @author xuhw
 */
public class TaxfunTotalTaxableAmtConvertor extends WaConvertor {
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

			String param = formula.substring(formula.indexOf("(") + 1,
					formula.indexOf(")"));
			String[] params = param.split(",");
			String pk_waclass = ((ClassItemContext)super.getContext()).getPk_wa_class();
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
