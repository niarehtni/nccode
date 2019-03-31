package nc.ui.hr.func_tax;

import java.util.HashMap;
import java.util.Map;

import nc.ui.hr.itemsource.view.WaConvertor;

/**
 * 基本费用扣除额 根据税表获取基本费用扣除额
 * 
 * @author xuhw
 */
public class WATaxFunTaxBasicDeductionConvertor extends WaConvertor {
	private static Map<String, String> map = new HashMap<String, String>();

	@Override
	public String preConvert(String formula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String postConvert(String formula) {
		return formula;
	}
}
