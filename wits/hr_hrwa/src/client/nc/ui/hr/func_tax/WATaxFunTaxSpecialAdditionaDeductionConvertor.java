package nc.ui.hr.func_tax;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import nc.ui.hr.itemsource.view.WaConvertor;

/**
 * 专项附加费用扣除
 * 获取专项附件费用扣除额(参数：种类 ，全部/子女教育/继续教育/住房贷款利息/住房租金/赡养老人)
 * 
 * @author xuhw
 */
public class WATaxFunTaxSpecialAdditionaDeductionConvertor extends WaConvertor {
	private static Map<String, String>  map = new HashMap<String, String>(); 
	static{
		String[] ml = {"0","1","2","3","4","5","6"};
		String[] mlDefault = new String[]{"全部","子女教育","继续教育","住房贷款利息","住房租金","赡养老人","大病医疗"};
		for(int i=0;i<ml.length;i++){
			map.put(ml[i], mlDefault[i]);
		}
	}

	@Override
	public String preConvert(String formula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String postConvert(String formula) {
		if(StringUtils.isNotEmpty(formula)){
			String param = formula.substring(formula.indexOf("(")+1, formula.indexOf(")"));
			formula = formula.replaceAll(param, map.get(param));
		}
		return formula;
	}
	
}
