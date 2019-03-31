package nc.vo.wa_tax;

import nc.vo.wa.taxspecial_statistics.TaxSpecialStatisticsVO;

/**
 * 税改升级工具类
 * @author xuhw
 *
 */
public class TaxUpgradeHelper {
	
	/**
	 * 专项费用扣除转换
	 * 
	 * @param strKey
	 * @return
	 */
	public static int convertAdddeductionStrKey2IntKey(String strKey){
		int intKey = 99;
		if (TaxSpecialStatisticsVO.PARENTFEE.endsWith(strKey)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_PARENT;
		} else if (TaxSpecialStatisticsVO.CHILDFEE.endsWith(strKey)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_CHILD;
		} else if (TaxSpecialStatisticsVO.EDUCATIONFEE.endsWith(strKey)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_EDUCATION;
		} else if (TaxSpecialStatisticsVO.HEALTHYFEE.endsWith(strKey)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_HEALTH;
		} else if (TaxSpecialStatisticsVO.HOURSEFEE.endsWith(strKey)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE;
		} else if (TaxSpecialStatisticsVO.HOURSEZUFEE.endsWith(strKey)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_ZU;
		}else if (TaxSpecialStatisticsVO.ALLFEE.endsWith(strKey)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_ALL;
		}
		//nothing
		return intKey;
	}
	
	/**
	 * 专项费用扣除转换
	 * 
	 * @param strKey
	 * @return
	 */
	public static String convertAdddeductionIntKey2StrKey(int intKey){
		String strKey = TaxSpecialStatisticsVO.ALLFEE;
		if (intKey == TaxupgradeDef.ADDDEDUCTION_KEY_PARENT) {
			strKey = TaxSpecialStatisticsVO.PARENTFEE;
		} else if (intKey == TaxupgradeDef.ADDDEDUCTION_KEY_CHILD) {
			strKey = TaxSpecialStatisticsVO.CHILDFEE;
		} else if (intKey == TaxupgradeDef.ADDDEDUCTION_KEY_EDUCATION) {
			strKey = TaxSpecialStatisticsVO.EDUCATIONFEE;
		} else if (intKey == TaxupgradeDef.ADDDEDUCTION_KEY_HEALTH) {
			strKey = TaxSpecialStatisticsVO.HEALTHYFEE;
		} else if (intKey == TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE) {
			strKey = TaxSpecialStatisticsVO.HOURSEFEE;
		} else if (intKey == TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_ZU) {
			strKey = TaxSpecialStatisticsVO.HOURSEZUFEE;
		}else if (intKey == TaxupgradeDef.ADDDEDUCTION_KEY_ALL) {
			strKey = TaxSpecialStatisticsVO.ALLFEE;
		}
		//nothing
		return strKey;
	}

	/**
	 * 专项费用扣除转换 名称转Int
	 * 
	 * @param strKey
	 * @return
	 */
	public static int convertAdddeductionIntName2Int(String name){
		int intKey = 99;
		if (TaxupgradeDef.ADDDEDUCTION_KEY_CHILD_NAME.endsWith(name)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_CHILD;
		} else if (TaxupgradeDef.ADDDEDUCTION_KEY_EDUCATION_NAME.endsWith(name)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_EDUCATION;
		} else if (TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_ZU_NAME.endsWith(name)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_ZU;
		} else if (TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_NAME.endsWith(name)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE;
		} else if (TaxupgradeDef.ADDDEDUCTION_KEY_PARENT_NAME.endsWith(name)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_PARENT;
		} else if (TaxupgradeDef.ADDDEDUCTION_KEY_HEALTH_NAME.endsWith(name)) {
			intKey = TaxupgradeDef.ADDDEDUCTION_KEY_HEALTH;
		} 
		//nothing
		return intKey;
	}
}
