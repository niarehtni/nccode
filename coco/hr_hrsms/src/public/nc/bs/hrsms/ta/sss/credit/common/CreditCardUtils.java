package nc.bs.hrsms.ta.sss.credit.common;

import java.util.Calendar;

import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.period.PeriodVO;

public class CreditCardUtils {

	/**
	 * 获取本月的第一个日期
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
	 * 获取本月的最后一个日期
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
	 * 获取左侧查询条件的开始日期和结束日期<br/>
	 * 1.存在考勤期间,考勤期间的第一天.<br/>
	 * 2.没有考勤期间,系统日期所在月的第一天和最后一天<br/>
	 * 
	 * @param date
	 *            指定日期
	 * @return 开始日期和结束日期
	 * 
	 */
	public static UFLiteralDate[] getDefaultDates4Condition(String pk_org) {
		UFLiteralDate d = new UFLiteralDate();
		PeriodVO periodVO = TBMPeriodUtil.getPeriodVO(pk_org, d);
		if (periodVO == null) {
			// 所属月的第一天
			UFLiteralDate beginDate = getFirstDateOfMonth(d);
			// 所属月的最后一天
			UFLiteralDate endDate = getLastDateOfMonth(d);
			return new UFLiteralDate[] { beginDate, endDate };
		}
		return new UFLiteralDate[] { periodVO.getBegindate(), periodVO.getEnddate() };
	}

	/**
	 * 获取指定日期所属月上的第一天.
	 * 
	 * @param date
	 *            当前日期
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
	 * 获取指定日期所属月上的第一天.
	 * 
	 * @param date
	 *            当前日期
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
