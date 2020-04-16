/**
 * WaDocFormulaPreExcutor 有效率问题。
 * 所以才有this。该类直接将数据更新到wa_data中，不需要wa_cacu_data.ca_value.进行中间接力了
 */
package nc.impl.wa.paydata.precacu;

import nc.hr.utils.SQLHelper;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.itf.hr.wa.IWaSalaryctymgtConstant;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;

public class WaDocFormulaPreExcutor2 extends AbstractFormulaExecutor {

	/**
	 * @author zhangg on 2010-5-14
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object,
	 *      nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object inarguments, WaLoginContext context) throws BusinessException {

		WaClassItemVO vo = (WaClassItemVO) context.getInitData();
		String updateItemkey = vo.getItemkey();
		String[] arguments = (String[]) inarguments;
		String pk_wa_item = arguments[0];
		int intType = new Integer(arguments[1].toString()).intValue();
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getWaYear();
		String cperiod = context.getWaPeriod();

		String enddate = context.getWaLoginVO().getPeriodVO().getCenddate().toString();

		String startdate = context.getWaLoginVO().getPeriodVO().getCstartdate().toString();

		UFBoolean allowed = WaAdjustParaTool.getPartjob_Adjmgt(vo.getPk_group());
		boolean isAdjustByWaOrg = isAdjustByWaOrg(vo.getPk_group());

		// 原发放金额
		if (IWaSalaryctymgtConstant.YFFJE == intType) {

			String preenddate = UFLiteralDate.getDate(startdate).getDateBefore(1).toString();
			StringBuffer sqlB = new StringBuffer();
			String sql = null;

			sqlB.append("           coalesce( ");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// 如果本期间有定调资（开始日期在本期间内），取本期间倒数第二条定调资记录的金额作为原金额
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum) ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.assgid = hi_psndoc_wadoc.assgid ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			sqlB.append("                      and m.enddate < '" + enddate + "' and m.enddate >= '" + startdate
					+ "'  )  ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc ");
			if (allowed.booleanValue()) {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = wa_data.assgid ");
			} else {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = 1 ");
			}
			if (isAdjustByWaOrg) {
				sqlB.append("                      and hi_psndoc_wadoc.pk_org = wa_data.pk_org ");
			}
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y'),");

			sqlB.append("           coalesce( ");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// 跨本期间，原薪资和现薪资一样
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum) ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.assgid = hi_psndoc_wadoc.assgid ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			sqlB.append("                      and  m.begindate < '" + startdate + "'   ");
			sqlB.append("                      and ( m.enddate>= '" + enddate + "' or "
					+ SQLHelper.getNullSql("m.enddate") + " ))  ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc ");
			if (allowed.booleanValue()) {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = wa_data.assgid ");
			} else {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = 1 ");
			}
			if (isAdjustByWaOrg) {
				sqlB.append("                      and hi_psndoc_wadoc.pk_org = wa_data.pk_org ");
			}
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y'),");

			sqlB.append("             coalesce( (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// 如果本期间没做定调资，取最后一条定调资记录的金额作为原金额（本期间之前）
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum)+1 ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.assgid = hi_psndoc_wadoc.assgid ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			sqlB.append("                      and  m.begindate = '" + startdate + "'   ");
			sqlB.append(" 		               and ( m.enddate>= '" + enddate + "' or " + SQLHelper.getNullSql("m.enddate")
					+ " )   )  ");
			sqlB.append("                      and hi_psndoc_wadoc.enddate = '" + preenddate + "'");
			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc ");
			if (allowed.booleanValue()) {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = wa_data.assgid ");
			} else {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = 1 ");
			}
			if (isAdjustByWaOrg) {
				sqlB.append("                      and hi_psndoc_wadoc.pk_org = wa_data.pk_org ");
			}
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y'),  0) )  ) ");
			/*
			 * sqlB.append("           coalesce( ");
			 * sqlB.append("              (select max(hi_psndoc_wadoc.nmoney) "
			 * ); sqlB.append("               from hi_psndoc_wadoc ");
			 * 
			 * //如果本期间有定调资（开始日期在本期间内），取本期间倒数第二条定调资记录的金额作为原金额
			 * sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			 * sqlB.append("                     (select min(m.recordnum)+1 ");
			 * sqlB.append("                      from hi_psndoc_wadoc m ");
			 * sqlB.append(
			 * "                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item "
			 * ); sqlB.append(
			 * "                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			 * sqlB.append("                      and m.begindate <= '" +
			 * enddate + "' and   m.begindate > '" + startdate + "'" );
			 * sqlB.append(" 		               and ( m.enddate>= '" + enddate +
			 * "' or nvl(m.enddate,'~')='~' )   )  ");
			 * sqlB.append(" 		               and hi_psndoc_wadoc.enddate < '"
			 * +enddate+"' and hi_psndoc_wadoc.enddate >= '"+startdate+"'   ");
			 * sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_wa_item = '" +
			 * pk_wa_item + "' "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.waflag = 'Y'),");
			 * 
			 * //如果截止日期在本期间内只有一条，取该记录的金额作为原金额 sqlB.append(
			 * " coalesce( (select max(hi_psndoc_wadoc.nmoney) from hi_psndoc_wadoc  "
			 * ); sqlB.append(
			 * " where hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc and  hi_psndoc_wadoc.pk_wa_item = '"
			 * + pk_wa_item+ "' ");
			 * sqlB.append("   and  hi_psndoc_wadoc.waflag = 'Y'  ");
			 * sqlB.append(
			 * "  and hi_psndoc_wadoc.recordnum = (select max(m.recordnum) from 	hi_psndoc_wadoc m where   m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item"
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.enddate < '"
			 * +enddate+"' and m.enddate >= '"+startdate+"' ) " ); sqlB.append(
			 * "  and hi_psndoc_wadoc.recordnum = (select min(m.recordnum) from 	hi_psndoc_wadoc m where   m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item"
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.enddate < '"
			 * +enddate+"' and m.enddate >= '"+startdate+"' ) " );
			 * sqlB.append(" ),");
			 * 
			 * sqlB.append(
			 * "             coalesce( (select max(hi_psndoc_wadoc.nmoney) ");
			 * sqlB.append("               from hi_psndoc_wadoc ");
			 * 
			 * //如果本期间没做定调资，取最后一条定调资记录的金额作为原金额（本期间之前）
			 * sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			 * sqlB.append("                     (select min(m.recordnum) ");
			 * sqlB.append("                      from hi_psndoc_wadoc m ");
			 * sqlB.append(
			 * "                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item "
			 * ); sqlB.append(
			 * "                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			 * sqlB.append("                      and  m.enddate < '" +
			 * startdate + "' )  "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_wa_item = '" +
			 * pk_wa_item + "' "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.waflag = 'Y'),  0) )   "
			 * ); sqlB.append("       ) ");
			 */

			sql = "update wa_data set " + updateItemkey + " = " + sqlB.toString()
					+ " where  wa_data.pk_wa_data in ( select  pk_wa_data from wa_cacu_data where pk_wa_class = '"
					+ pk_wa_class + "' and creator = '" + context.getPk_loginUser()
					+ "' )   and wa_data.pk_wa_class = '" + pk_wa_class + "' and wa_data.cyear = '" + cyear
					+ "' and wa_data.cperiod = '" + cperiod + "' ";
			getBaseDao().executeUpdate(sql);

		}
		// 现发放金额
		else if (IWaSalaryctymgtConstant.XFFJE == intType) {
			StringBuffer sqlB = new StringBuffer();
			String sql = null;

			sqlB.append("           coalesce( ");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// 取截止日期晚于本期间的最后一条定调资记录的金额作为现金额
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum) ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.assgid = hi_psndoc_wadoc.assgid ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			sqlB.append("                      and m.begindate <= '" + enddate + "' ");
			sqlB.append(" 		               and ( m.enddate >= '" + enddate + "' or "
					+ SQLHelper.getNullSql("m.enddate") + " )  )    ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc ");
			if (allowed.booleanValue()) {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = wa_data.assgid ");
			} else {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = 1 ");
			}
			if (isAdjustByWaOrg) {
				sqlB.append("                      and hi_psndoc_wadoc.pk_org = wa_data.pk_org ");
			}
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y'),0");
			sqlB.append("       ) ");
			/*
			 * //如果截止日期在本期间内只有一条且本期间内无定调资记录，现金额为0
			 * sqlB.append(" coalesce( (select 0 from hi_psndoc_wadoc  ");
			 * sqlB.append(
			 * " where hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc and  hi_psndoc_wadoc.pk_wa_item = '"
			 * + pk_wa_item+ "' ");
			 * sqlB.append("   and  hi_psndoc_wadoc.waflag = 'Y'  ");
			 * sqlB.append(
			 * "  and hi_psndoc_wadoc.recordnum = (select max(m.recordnum) from 	hi_psndoc_wadoc m where   m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item"
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.enddate < '"
			 * +enddate+"' and m.enddate >= '"+startdate+"'  ) " ); sqlB.append(
			 * "  and hi_psndoc_wadoc.recordnum = (select min(m.recordnum) from 	hi_psndoc_wadoc m where   m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item"
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.enddate < '"
			 * +enddate+"' and m.enddate >= '"+startdate+"'  ) " ); sqlB.append(
			 * " and not exists(select 1 from hi_psndoc_wadoc m where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item "
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.begindate <= '"
			 * +enddate+"' and m.begindate >= '"+startdate+
			 * "' and m.recordnum = hi_psndoc_wadoc.recordnum-1)");
			 * sqlB.append(" ),");
			 * 
			 * sqlB.append(
			 * "             coalesce( (select max(hi_psndoc_wadoc.nmoney) ");
			 * sqlB.append("               from hi_psndoc_wadoc ");
			 * 
			 * //如果该期间没做定调资，取最后一条定调资记录的金额作为现金额（需横跨本期间）
			 * sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			 * sqlB.append("                     (select min(m.recordnum) ");
			 * sqlB.append("                      from hi_psndoc_wadoc m ");
			 * sqlB.append(
			 * "                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item "
			 * ); sqlB.append(
			 * "                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			 * sqlB.append("                      and m.begindate <= '" +
			 * startdate + "' ");
			 * sqlB.append("                      and (m.enddate>='"
			 * +enddate+"' or nvl(m.enddate,'~')='~' ))  "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_wa_item = '" +
			 * pk_wa_item + "' "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.waflag = 'Y'),  0) )   "
			 * ); sqlB.append("       ) ");
			 */

			sql = "update wa_data set " + updateItemkey + " = " + sqlB.toString()
					+ " where  wa_data.pk_wa_data in ( select  pk_wa_data from wa_cacu_data where pk_wa_class = '"
					+ pk_wa_class + "' and creator = '" + context.getPk_loginUser()
					+ "' )   and wa_data.pk_wa_class = '" + pk_wa_class + "' and wa_data.cyear = '" + cyear
					+ "' and wa_data.cperiod = '" + cperiod + "' ";
			getBaseDao().executeUpdate(sql);

		} else if (IWaSalaryctymgtConstant.SDJE == intType) {

			String sql = null;
			StringBuffer sqlB = new StringBuffer();
			// 如果有时点薪资， 就取时点薪资，没有时点薪资，就取现发放金额
			sqlB.append(" coalesce((select salary_decrypt(max(hi_psndoc_wa.nmoney)) "); // 1
			sqlB.append("             from hi_psndoc_wa ");
			sqlB.append("            where hi_psndoc_wa.pk_psndoc = wa_data.pk_psndoc ");
			if (allowed.booleanValue()) {
				sqlB.append("                     and hi_psndoc_wa.assgid = wa_data.assgid ");
			} else {
				sqlB.append("                     and hi_psndoc_wa.assgid = 1 ");
			}
			if (isAdjustByWaOrg) {
				sqlB.append("                      and hi_psndoc_wa.pk_wa_class = wa_data.pk_wa_class ");
			}
			sqlB.append("              and hi_psndoc_wa.dr = 0 ");
			sqlB.append("              and hi_psndoc_wa.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("              and hi_psndoc_wa.pk_wa_class = '" + pk_wa_class + "' ");
			sqlB.append("              and hi_psndoc_wa.cyear = '" + cyear + "' ");
			sqlB.append("              and hi_psndoc_wa.cperiod = '" + cperiod + "'), ");

			sqlB.append("           coalesce( ");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// 取截止日期晚于本期间的最后一条定调资记录的金额作为现金额
			sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			sqlB.append("                     (select min(m.recordnum) ");
			sqlB.append("                      from hi_psndoc_wadoc m ");
			sqlB.append("                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc ");
			sqlB.append("                      and m.assgid = hi_psndoc_wadoc.assgid ");
			sqlB.append("                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
			sqlB.append("                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			sqlB.append("                      and m.begindate <= '" + enddate + "' ");
			sqlB.append(" 		               and ( m.enddate >= '" + enddate + "' or "
					+ SQLHelper.getNullSql("m.enddate") + " )  )    ");
			sqlB.append("                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc ");
			if (allowed.booleanValue()) {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = wa_data.assgid ");
			} else {
				sqlB.append("                      and hi_psndoc_wadoc.assgid = 1 ");
			}
			if (isAdjustByWaOrg) {
				sqlB.append("                      and hi_psndoc_wadoc.pk_org = wa_data.pk_org ");
			}
			sqlB.append("                      and hi_psndoc_wadoc.pk_wa_item = '" + pk_wa_item + "' ");
			sqlB.append("                      and hi_psndoc_wadoc.waflag = 'Y'),0");
			sqlB.append("       )) ");

			/*
			 * sqlB.append("           coalesce( ");
			 * sqlB.append("              (select max(hi_psndoc_wadoc.nmoney) "
			 * ); sqlB.append("               from hi_psndoc_wadoc ");
			 * //如果本期间有定调资（开始日期在本期间内），取本期间最后一条定调资记录的金额作为现金额
			 * sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			 * sqlB.append("                     (select min(m.recordnum) ");
			 * sqlB.append("                      from hi_psndoc_wadoc m ");
			 * sqlB.append(
			 * "                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item "
			 * ); sqlB.append(
			 * "                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			 * sqlB.append("                      and m.begindate <= '" +
			 * enddate + "' and   m.begindate > '" + startdate + "')  ");
			 * sqlB.append(" 		               and ( m.enddate >= '" + enddate +
			 * "' or nvl(m.enddate,'~')='~' )   )  ");
			 * 
			 * sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_wa_item = '" +
			 * pk_wa_item + "' "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.waflag = 'Y'),");
			 * 
			 * //如果截止日期在本期间内只有一条且本期间内无定调资记录，现金额为0
			 * sqlB.append(" coalesce( (select 0 from hi_psndoc_wadoc  ");
			 * sqlB.append(
			 * " where hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc and  hi_psndoc_wadoc.pk_wa_item = '"
			 * + pk_wa_item+ "' ");
			 * sqlB.append("   and  hi_psndoc_wadoc.waflag = 'Y'  ");
			 * sqlB.append(
			 * "  and hi_psndoc_wadoc.recordnum = (select max(m.recordnum) from 	hi_psndoc_wadoc m where   m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item"
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.enddate < '"
			 * +enddate+"' and m.enddate >= '"+startdate+"'  ) " ); sqlB.append(
			 * "  and hi_psndoc_wadoc.recordnum = (select min(m.recordnum) from 	hi_psndoc_wadoc m where   m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item"
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.enddate < '"
			 * +enddate+"' and m.enddate >= '"+startdate+"'  ) " ); sqlB.append(
			 * " and not exists(select 1 from hi_psndoc_wadoc m where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc and  m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item "
			 * ); sqlB.append(
			 * " and m.waflag = hi_psndoc_wadoc.waflag and m.begindate <= '"
			 * +enddate+"' and m.begindate >= '"+startdate+
			 * "' and m.recordnum = hi_psndoc_wadoc.recordnum-1)");
			 * sqlB.append(" ),");
			 * 
			 * sqlB.append(
			 * "             coalesce( (select max(hi_psndoc_wadoc.nmoney) ");
			 * sqlB.append("               from hi_psndoc_wadoc ");
			 * 
			 * //如果该期间没做定调资，取最后一条定调资记录的金额作为现金额（需横跨本期间）
			 * sqlB.append("               where hi_psndoc_wadoc.recordnum = ");
			 * sqlB.append("                     (select min(m.recordnum) ");
			 * sqlB.append("                      from hi_psndoc_wadoc m ");
			 * sqlB.append(
			 * "                      where m.pk_psndoc = hi_psndoc_wadoc.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and m.pk_wa_item = hi_psndoc_wadoc.pk_wa_item "
			 * ); sqlB.append(
			 * "                      and m.waflag = hi_psndoc_wadoc.waflag  ");
			 * sqlB.append("                      and m.begindate <= '" +
			 * startdate + "' ");
			 * sqlB.append("                      and (m.enddate>='"
			 * +enddate+"' or nvl(m.enddate,'~')='~' ))  "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc "
			 * ); sqlB.append(
			 * "                      and hi_psndoc_wadoc.pk_wa_item = '" +
			 * pk_wa_item + "' "); sqlB.append(
			 * "                      and hi_psndoc_wadoc.waflag = 'Y'),  0) )   "
			 * ); sqlB.append("       )) ");
			 */

			sql = "update wa_data set " + updateItemkey + " = " + sqlB.toString()
					+ " where  wa_data.pk_wa_data in ( select  pk_wa_data from wa_cacu_data where pk_wa_class = '"
					+ pk_wa_class + "' and creator = '" + context.getPk_loginUser()
					+ "' )   and wa_data.pk_wa_class = '" + pk_wa_class + "' and wa_data.cyear = '" + cyear
					+ "' and wa_data.cperiod = '" + cperiod + "' ";

			getBaseDao().executeUpdate(sql);
		}
	}

	private boolean isAdjustByWaOrg(String pk_group) {
		return WaAdjustParaTool.getWaOrg(pk_group).equals(0);
	}

}
