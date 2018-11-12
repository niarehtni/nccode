package nc.bs.hrsms.ta.sss.credit.common;

import java.util.Calendar;

import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.period.PeriodVO;

public class CreditCardUtils {

	/**
	 * ��ȡ���µĵ�һ������
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static UFLiteralDate getFirstDateOfMonth(String year, String month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(year).intValue(), Integer.valueOf(month).intValue(), 1);
		return new UFLiteralDate(cal.getTime());
	}

	/**
	 * ��ȡ���µ����һ������
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static UFLiteralDate getLastDateOfMonth(String year, String month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(year).intValue(), Integer.valueOf(month).intValue() + 1, 1);
		cal.add(Calendar.DATE, -1);
		return new UFLiteralDate(cal.getTime());
	}

	/**
	 * ��ȡ����ѯ�����Ŀ�ʼ���ںͽ�������<br/>
	 * 1.���ڿ����ڼ�,�����ڼ�ĵ�һ��.<br/>
	 * 2.û�п����ڼ�,ϵͳ���������µĵ�һ������һ��<br/>
	 * 
	 * @param date
	 *            ָ������
	 * @return ��ʼ���ںͽ�������
	 * 
	 */
	public static UFLiteralDate[] getDefaultDates4Condition(String pk_org) {
		UFLiteralDate d = new UFLiteralDate();
		PeriodVO periodVO = TBMPeriodUtil.getPeriodVO(pk_org, d);
		if (periodVO == null) {
			// �����µĵ�һ��
			UFLiteralDate beginDate = getFirstDateOfMonth(d);
			// �����µ����һ��
			UFLiteralDate endDate = getLastDateOfMonth(d);
			return new UFLiteralDate[] { beginDate, endDate };
		}
		return new UFLiteralDate[] { periodVO.getBegindate(), periodVO.getEnddate() };
	}

	/**
	 * ��ȡָ�������������ϵĵ�һ��.
	 * 
	 * @param date
	 *            ��ǰ����
	 * @return
	 * @author haoy 2011-7-27
	 */
	public static UFLiteralDate getFirstDateOfMonth(UFLiteralDate d) {
		if (d == null) {
			return null;
		}
		return UFLiteralDate.getDate(d.toString().substring(0, 7) + "-01");
	}

	/**
	 * ��ȡָ�������������ϵĵ�һ��.
	 * 
	 * @param date
	 *            ��ǰ����
	 * @return
	 * @author haoy 2011-7-27
	 */
	public static UFLiteralDate getLastDateOfMonth(UFLiteralDate d) {
		if (d == null) {
			return null;
		}
		return UFLiteralDate.getDate(d.toString().substring(0, 7) + "-" + UFLiteralDate.getDaysMonth(d.getYear(), d.getMonth()));
	}
}
