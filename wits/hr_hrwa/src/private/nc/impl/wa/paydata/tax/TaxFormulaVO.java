package nc.impl.wa.paydata.tax;

import java.io.Serializable;

/**
 *
 * @author: zhangg
 * @date: 2010-1-13 下午05:03:06
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxFormulaVO  implements Serializable{
	private static final long serialVersionUID = -2213552954343231821L;
	// Class_type: 取值范围（0, 1） 0为普通类别， 1为年度奖金类别    2 为薪资补发计算  
	public final static String CLASS_TYPE_NORMAL = "0";
	public final static String CLASS_TYPE_YEAR = "1";
	
	public final static String CLASS_TYPE_REDATA= "2";
// {MOD:新个税补丁}
// begin
	// 3 年度奖金累计计税
	public final static String CLASS_TYPE_TOTAL_YEAR = "3";
// end
	//base 是 tax(扣税基数公式、扣税类型) 中的“扣税基数公式”
	String base;
	String class_type;
	String yearAward;
	
	public String getYearAward() {
		return yearAward;
	}

	public void setYearAward(String yearAward) {
		this.yearAward = yearAward;
	}

	public String getMonthPay() {
		return monthPay;
	}

	public void setMonthPay(String monthPay) {
		this.monthPay = monthPay;
	}

	public String getPeirodTimesField() {
		return peirodTimesField;
	}

	public void setPeirodTimesField(String peirodTimesField) {
		this.peirodTimesField = peirodTimesField;
	}

	String monthPay;
	
	String peirodTimesField;
	
	int  defaultPeriodTimes = 12;

	/**
	 * @author zhangg on 2010-1-12
	 * @return the base
	 */
	public String getBase() {
		return base;
	}

	/**
	 * @author zhangg on 2010-1-12
	 * @param base
	 *            the base to set
	 */
	public void setBase(String base) {
		this.base = base;
	}


	/**
	 * @author zhangg on 2010-1-12
	 * @return the class_type
	 */
	public String getClass_type() {
		return class_type;
	}

	/**
	 * @author zhangg on 2010-1-12
	 * @param class_type
	 *            the class_type to set
	 */
	public void setClass_type(String class_type) {
		this.class_type = class_type;
	}

}
