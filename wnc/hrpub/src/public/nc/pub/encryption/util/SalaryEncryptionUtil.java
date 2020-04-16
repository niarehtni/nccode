package nc.pub.encryption.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.paydata.DataVO;

/**
 * ���ܹ�����
 * 
 * @author zszyff
 */
public class SalaryEncryptionUtil {
    
	//�������ֵ(����һ����ʼʹ��,�����޸�,Ҫ�ͽ��ܹ������Ӧ)
	public final static int MAX_VALUE= 10000000;
	//������󾫶�(�������ֵ��λ��)(����һ����ʼʹ��,�����޸�,Ҫ�ͽ��ܹ������Ӧ)
	public final static int MAX_DEGREE= 7;
	//(-1,0)����ӳ�����������(��Ҫ���������ֵҪ��)(����һ����ʼʹ��,�����޸�,Ҫ�ͽ��ܹ������Ӧ)
	public final static int POINT_INT_VALUE= 99999999;
    

	/**
	 * �����������ݣ������ݲ��Ϊ������С�����֣��ֱ���
	 * 
	 * @param m
	 * @return
	 */
	public final static double encryption(double m) {
		// ����m���� �Ƿ��ǿ�ѧ������
		int jd = 0;
		String m_s = "";
		if (String.valueOf(m).contains("E")) {
			jd = Integer.valueOf(String.valueOf(m).split("E")[1]) + 1;// û�а취
																		// ֻ������
																		// ȡ�þ���ֵ6
			UFDouble dd = new UFDouble(m, jd);
			m_s = dd.toString();
		} else if (String.valueOf(m).contains("e")) {
			jd = Integer.valueOf(String.valueOf(m).split("e")[1]) + 1;// û�а취
																		// ֻ������
																		// ȡ�þ���ֵ6
			UFDouble dd = new UFDouble(m, jd);
			m_s = dd.toString();
		} else {
			//���ȶ����6λ,��ֹdouble����ʵ��С��λ��������Ч���ֵ���� tank 2020��2��18��23:35:38
			String c_s = String.format("%."+MAX_DEGREE+"f", m);
			m_s = new Double(Double.parseDouble(c_s)).toString();
		}
		// 2016-11-21 zhousze н�ʼ��ܣ�����������ת����string���ж��Ƿ����С��
		String[] split = m_s.split("\\.");
		double p_i = Double.parseDouble(split[0]); // ��������
		double p_d = Double.parseDouble(split[1]); // С������
		if (p_d < 0.000000001) {
			return encryption4part(p_i);
		} else {

			// 2016-12-22 zhousze н�ʼ��ܣ����ﴦ��С�����֣�����������ܴ���
			StringBuffer s1 = new StringBuffer(split[1]);
			p_d = Double.parseDouble(s1.reverse().toString());

			p_i = encryption4part(p_i);
			p_d = encryption4part(p_d);
			StringBuffer s2 = new StringBuffer(String.valueOf(p_d));
			String p_d_s = s2.reverse().toString();
			double rs = p_i + Double.parseDouble(p_d_s);
			//���(-1,0)�����ڵ�ӳ������ tank 2020��2��19��01:04:27
			if(m<0 && rs > 0 && rs < 1){
				rs = -(1-rs+POINT_INT_VALUE);
			}
			return rs;
		}
	}

	/**
	 * m����������Ϣ���ݣ������������������������1��0<k<n����2��k��n����
	 * 
	 * @param m
	 * @param k
	 * @param n
	 * @return
	 */
	private static double encryption4part(double m) {
		// 2016-11-21 zhousze н�ʼ��ܣ�����c������ܺ����ݣ�k������Կ��n����������Ϣ�������ֵ��
		// ͨ������˷�����㷨�������ݡ�
		double k = 6666667;
		double n = MAX_VALUE;
		double c = 0;
		c = (m * k) % n;
		return c;
	}

	/**
	 * ��������н��VO���ݱ���ʱ�ļ��ܴ���
	 * 
	 * @param dataVos
	 * @return
	 */
	public final static DataVO[] encryption4Array(DataVO[] dataVos) {
		for (int i = 0; i < dataVos.length; i++) {
			HashMap<String, Object> map = dataVos[i].appValueHashMap;
			Object[] pks = map.keySet().toArray();
			// 2016-11-22 zhousze н�ʼ��ܣ�������ֵ��н����Ŀ�����м��ܴ���
			ArrayList<String> itemPks = new ArrayList<String>();
			// itemPks.add("expense_deduction"); // ���ÿ۳���׼
			// itemPks.add("taxable_income"); // Ӧ��˰���ö�
			// itemPks.add("taxrate"); // ˰��
			// itemPks.add("nquickdebuct"); // ����۳���
			for (int j = 0; j < pks.length; j++) {
				if (pks[j].toString().startsWith("f_")) {
					itemPks.add(pks[j].toString());
				}
			}
			// 2016-11-22 zhousze н�ʼ��ܣ������滻ԭ��VO�е���ֵ����н����Ŀ���滻�ɼ��ܺ��н����Ŀ
			for (int h = 0; h < itemPks.size(); h++) {
				UFDouble itemValueBefore = (UFDouble) dataVos[i].getAttributeValue(itemPks.get(h));
				if (UFDouble.ZERO_DBL.equals(itemValueBefore)) {
					continue;
				}
				NumberFormat nf = NumberFormat.getInstance();
				// ���ô˸�ʽ�в�ʹ�÷���
				nf.setGroupingUsed(false);
				// ��������С����������������λ����
				nf.setMaximumFractionDigits(8);
				if (itemValueBefore != null) {
					if (itemValueBefore.toDouble() == (5.1E-4)) {
						System.out.println("������Ŀ�������֪����");
					}
					String format = nf.format(itemValueBefore.toDouble());
					Double valueOf = Double.valueOf(format);
					double itemValueAfter = encryption(itemValueBefore.doubleValue());
					dataVos[i].setAttributeValue(itemPks.get(h), new UFDouble(itemValueAfter));
				}
			}
		}
		return dataVos;
	}
}
