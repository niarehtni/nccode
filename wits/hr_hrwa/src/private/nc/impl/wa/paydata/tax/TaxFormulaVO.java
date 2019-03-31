package nc.impl.wa.paydata.tax;

import java.io.Serializable;

/**
 *
 * @author: zhangg
 * @date: 2010-1-13 ����05:03:06
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class TaxFormulaVO  implements Serializable{
	private static final long serialVersionUID = -2213552954343231821L;
	// Class_type: ȡֵ��Χ��0, 1�� 0Ϊ��ͨ��� 1Ϊ��Ƚ������    2 Ϊн�ʲ�������  
	public final static String CLASS_TYPE_NORMAL = "0";
	public final static String CLASS_TYPE_YEAR = "1";
	
	public final static String CLASS_TYPE_REDATA= "2";
// {MOD:�¸�˰����}
// begin
	// 3 ��Ƚ����ۼƼ�˰
	public final static String CLASS_TYPE_TOTAL_YEAR = "3";
// end
	//base �� tax(��˰������ʽ����˰����) �еġ���˰������ʽ��
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
