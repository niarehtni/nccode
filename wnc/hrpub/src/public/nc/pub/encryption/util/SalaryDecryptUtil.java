package nc.pub.encryption.util;

import java.util.ArrayList;
import java.util.HashMap;

import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.paydata.DataVO;

/**
 * ���ܹ�����
 * 
 * @author zszyff
 * 
 */
public class SalaryDecryptUtil {

	/**
	 * �����������ݣ����Ϊ������С�����֣��ֱ�������
	 * 
	 * @param c
	 * @return
	 */
	public final static double decrypt(double c) {
		UFDouble c_uf = new UFDouble(c);
		String c_s = c_uf.toString();
		if (c_s.contains(".")) {
			// 2016-11-21 zhousze н�ʽ��ܣ�����������ת����string���ж��Ƿ����С��
			String[] split = c_s.split("\\.");
			double p_i = Double.parseDouble(split[0]); // ��������
			double p_d = Double.parseDouble(split[1]); // С������
			if (p_d < 0.00000001) {
				return decrypt4part(p_i);
			} else {
				// 2016-12-22 zhousze н�ʽ��ܣ����ﴦ��С�����ݽ��ܣ��������
				StringBuffer s1 = new StringBuffer(split[1]);
				p_d = Double.parseDouble(s1.reverse().toString());

				p_i = decrypt4part(p_i);
				p_d = decrypt4part(p_d);
				StringBuffer s = new StringBuffer(String.valueOf(p_d));
				String p_d_s = s.reverse().toString();
				return p_i + Double.parseDouble(p_d_s);
			}
		} else {
			return decrypt4part(c);
		}
	}

	/**
	 * ���ݴ���������������c���������ģ�������Ԫ��Ȼ�������Ԫ�����������
	 * 
	 * @param c
	 * @return
	 */
	private static double decrypt4part(double c) {
		// 2016-11-21 zhousze н�ʽ��ܣ�����k��n�ó���Ӧ����Ԫk1��Ȼ��õ���������m
		double k = 6666667;
		double n = 10000000;
		double k1 = inverseElement((int) k, (int) n);
		double m = 0;
		m = (c * k1) % n;
		return m;
	}

	/**
	 * ������Կk�Լ�����n��ͨ�������õ���Կ����Ԫ���ڽ���
	 * 
	 * @param k
	 * @param n
	 * @return
	 */
	private static int inverseElement(int k, int n) {
		// 2016-11-21 zhousze н�ʽ��ܣ������Ԫ����������Ҫ��k�����n����Ԫk1������k<n����ʱ����a*k + q ==
		// n����a*k + qͬ��0(mod n)��
		// ����a=[n/k]��q=n%k���ڵ�ʽ����ͬʱ����k' * q'������k'��k����Ԫ��q'��q����Ԫ���õ���a*q' +
		// k'ͬ��0(mod n)������õ���
		// k'=-a * q'��������ʹ����չŷ������㷨�����߷���С������
		if (k == 0) {
			return -1;
		}
		if (k == 1) {
			return 1;
		}
		int ret = inverseElement(n % k, n);
		if (ret == -1) {
			return ret;
		}
		return (-n / k * ret % n + n) % n;
	}

	/**
	 * ��������н�ʲ�ѯ������£�����VO����
	 * 
	 * @param dataVos
	 * @return
	 */
	public final static DataVO[] decrypt4Array(DataVO[] dataVos) {
		for (int i = 0; i < dataVos.length; i++) {
			HashMap<String, Object> map = dataVos[i].appValueHashMap;
			Object[] pks = map.keySet().toArray();
			// 2016-11-22 zhousze н�ʽ��ܣ�������ֵ��н����Ŀ�Լ�˰������ֶΣ����н��ܴ���
			ArrayList<String> itemPks = new ArrayList<String>();
			// itemPks.add("expense_deduction");
			// itemPks.add("taxable_income");
			// itemPks.add("taxrate");
			// itemPks.add("nquickdebuct");
			for (int j = 0; j < pks.length; j++) {
				if (pks[j].toString().startsWith("f_")) {
					itemPks.add(pks[j].toString());
				}
			}
			// 2016-11-22 zhousze н�ʽ��ܣ������滻ԭ��VO�е���ֵ����н����Ŀ���滻�ɽ��ܺ��н����Ŀ
			for (int k = 0; k < itemPks.size(); k++) {
				UFDouble itemValueBefore = (UFDouble) dataVos[i].getAttributeValue(itemPks.get(k));
				if (itemValueBefore != null && itemValueBefore.doubleValue() != 0) {
					double itemValueAfter = decrypt(itemValueBefore.doubleValue());
					dataVos[i].setAttributeValue(itemPks.get(k), new UFDouble(itemValueAfter));
				}
			}
		}
		return dataVos;
	}
}