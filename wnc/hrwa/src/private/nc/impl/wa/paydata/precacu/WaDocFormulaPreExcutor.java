/**
 * History Resource:   Modifyer :xuhw  Time :2011年2月28日  Reason :定调资娶数,增加取原薪资额 
 */
package nc.impl.wa.paydata.precacu;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.itf.hr.wa.IWaSalaryctymgtConstant;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * 时点薪资 不需要借用 wa_cacu_data中的cacu_value 直接更新到wa_data中就可以了。这样效率更高
 * 
 * @author: zhangg
 * @date: 2010-5-14 上午09:20:15
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaDocFormulaPreExcutor extends AbstractFormulaExecutor {

	/**
	 * @author zhangg on 2010-5-14
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object,
	 *      nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object inarguments, WaLoginContext context) throws BusinessException {

		String[] arguments = (String[]) inarguments;
		String pk_wa_item = arguments[0];
		int intType = new Integer(arguments[1].toString()).intValue();

		String itemkey = arguments[2].toString();
		// boolean isOnTime = new Integer(arguments[1].toString()).intValue() ==
		// IWaSalaryctymgtConstant.SDJE;

		String enddate = context.getWaLoginVO().getPeriodVO().getCenddate().toString();

		String startdate = context.getWaLoginVO().getPeriodVO().getCstartdate().toString();
		// 2006-12-15
		// 如果[本期间]内有 [最新记录]＆[发放标志]都存在的情况， 取本记录
		// 不符合上面的情况下， 取[本期间]内的[发放标志]＆[RECORDNUM最小]的记录。

		// 原发放金额
		if (IWaSalaryctymgtConstant.YFFJE == intType) {
			// 如果本月内有调薪，则取调薪前金额
			// 如果本月内没有调薪，则取 本月以前的最后的调薪记录
			StringBuffer sqlB = new StringBuffer();
			String sql = null;

			sqlB.append("           (coalesce( ");
			sqlB.append("              (select salary_decryptmax(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// 如果本月内有调薪， 原发放额的日期在本月内，而且记录最小，而且不是最新记录
			// 如果本月内没有调薪， 原发放额的日期不在本月内，在本月以前，而且记录最小。（不能确定是不是最新记录）
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum) ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			sqlB.append("                       and m.lastflag = 'N' and  m.begindate <= '" + enddate
					+ "' and   m.begindate >= '" + startdate + "')  ");

			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                       and hi_psndoc_wadoc.lastflag = 'N' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y'),");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// 如果本月内有调薪， 原发放额的日期在本月内，而且记录最小，而且不是最新记录
			// 如果本月内没有调薪， 原发放额的日期不在本月内，在本月以前，而且记录最小。（不能确定是不是最新记录）
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum) ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			sqlB.append("                      and  m.begindate <= '" + startdate + "' )  ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y'),  0)  ) ");

			sql = "update wa_cacu_data set cacu_value = " + sqlB.toString() + " where  " + "pk_wa_class = '"
					+ context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser() + "' ";
			getBaseDao().executeUpdate(sql);

		}
		// 现发放金额
		else if (IWaSalaryctymgtConstant.XFFJE == intType) {
			StringBuffer sqlB = new StringBuffer();
			String sql = null;
			// 本月已经生效的最近记录
			// 或者 （如果本月内没有生效记录，就去本月的最后一次调薪记录 min(m.recordnum)）
			sqlB.append(" coalesce( "); // 1
			sqlB.append("       (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) "); // 2
			sqlB.append("        from hi_psndoc_wadoc ");
			sqlB.append("        where hi_psndoc_wadoc.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlB.append("        and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("        and hi_psndoc_wadoc.begindate <= '" + enddate + "' ");
			sqlB.append("        and hi_psndoc_wadoc.lastflag = 'Y' ");
			sqlB.append("        and hi_psndoc_wadoc.waflag = 'Y' ");
			sqlB.append("        and hi_psndoc_wadoc.dr = 0), ");
			sqlB.append("              (coalesce( ");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum) ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag ");
			sqlB.append("                      and m.dr = 0 ");
			sqlB.append("                      and m.begindate <= '" + enddate + "') ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.begindate <= '" + enddate + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y' ");
			sqlB.append("                      and hi_psndoc_wadoc.dr = 0), ");
			sqlB.append("                             0) ");
			sqlB.append("              ) ");
			sqlB.append("       ) ");

			sql = "update wa_cacu_data set cacu_value = " + sqlB.toString() + " where  " + "pk_wa_class = '"
					+ context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser() + "' ";
			getBaseDao().executeUpdate(sql);

		} else if (IWaSalaryctymgtConstant.SDJE == intType) {
			// 如果有时点薪资， 就取时点薪资，没有时点薪资，就取先发放金额
			String sql = null;
			StringBuffer sqlBuffer = new StringBuffer();

			sqlBuffer.append(" coalesce((select salary_decrypt(max(hi_psndoc_wa.nmoney)) "); // 1
			sqlBuffer.append("             from hi_psndoc_wa ");
			sqlBuffer.append("            where hi_psndoc_wa.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlBuffer.append("              and hi_psndoc_wa.dr = 0 ");
			sqlBuffer.append("              and hi_psndoc_wa.pk_wa_item = '" + pk_wa_item + "' ");
			sqlBuffer.append("              and hi_psndoc_wa.pk_wa_class = '" + context.getPk_wa_class() + "' ");
			sqlBuffer.append("              and hi_psndoc_wa.cyear = '" + context.getWaLoginVO().getCyear() + "' ");
			sqlBuffer.append("              and hi_psndoc_wa.cperiod = '" + context.getWaLoginVO().getCperiod()
					+ "'), ");
			sqlBuffer.append("            (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) "); // 2
			sqlBuffer.append("        from hi_psndoc_wadoc ");
			sqlBuffer.append("        where hi_psndoc_wadoc.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlBuffer.append("        and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlBuffer.append("        and hi_psndoc_wadoc.begindate <= '" + enddate + "' ");
			sqlBuffer.append("        and hi_psndoc_wadoc.lastflag = 'Y' ");
			sqlBuffer.append("        and hi_psndoc_wadoc.waflag = 'Y' ");
			sqlBuffer.append("        and hi_psndoc_wadoc.dr = 0), ");
			sqlBuffer.append("              (coalesce( ");
			sqlBuffer.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlBuffer.append("               from hi_psndoc_wadoc ");
			sqlBuffer.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlBuffer.append("                     (select min(m.recordnum) ");
			sqlBuffer.append("                      from hi_psndoc_wadoc m ");
			sqlBuffer.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlBuffer.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlBuffer.append("                      and m.waflag = hi_psndoc_wadoc.waflag ");
			sqlBuffer.append("                      and m.dr = 0 ");
			sqlBuffer.append("                      and m.begindate <= '" + enddate + "') ");
			sqlBuffer.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlBuffer.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlBuffer.append("                      and hi_psndoc_wadoc.begindate <= '" + enddate + "' ");
			sqlBuffer.append("                      and hi_psndoc_wadoc.waflag = 'Y' ");
			sqlBuffer.append("                      and hi_psndoc_wadoc.dr = 0), ");
			sqlBuffer.append("                             0) ");
			sqlBuffer.append("              ) ) ");

			sql = "update wa_cacu_data set cacu_value = " + sqlBuffer.toString() + " where  " + "pk_wa_class = '"
					+ context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser() + "' ";

			getBaseDao().executeUpdate(sql);
		}
	}
}
