/**
 * WaDocFormulaPreExcutor ��Ч�����⡣
 * ���Բ���this������ֱ�ӽ����ݸ��µ�wa_data�У�����Ҫwa_cacu_data.ca_value.�����м������
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

		// ԭ���Ž��
		if (IWaSalaryctymgtConstant.YFFJE == intType) {

			String preenddate = UFLiteralDate.getDate(startdate).getDateBefore(1).toString();
			StringBuffer sqlB = new StringBuffer();
			String sql = null;

			sqlB.append("           coalesce( ");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// ������ڼ��ж����ʣ���ʼ�����ڱ��ڼ��ڣ���ȡ���ڼ䵹���ڶ��������ʼ�¼�Ľ����Ϊԭ���
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

			// �籾�ڼ䣬ԭн�ʺ���н��һ��
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

			// ������ڼ�û�������ʣ�ȡ���һ�������ʼ�¼�Ľ����Ϊԭ�����ڼ�֮ǰ��
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
			 * //������ڼ��ж����ʣ���ʼ�����ڱ��ڼ��ڣ���ȡ���ڼ䵹���ڶ��������ʼ�¼�Ľ����Ϊԭ���
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
			 * //�����ֹ�����ڱ��ڼ���ֻ��һ����ȡ�ü�¼�Ľ����Ϊԭ��� sqlB.append(
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
			 * //������ڼ�û�������ʣ�ȡ���һ�������ʼ�¼�Ľ����Ϊԭ�����ڼ�֮ǰ��
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
		// �ַ��Ž��
		else if (IWaSalaryctymgtConstant.XFFJE == intType) {
			StringBuffer sqlB = new StringBuffer();
			String sql = null;

			sqlB.append("           coalesce( ");
			sqlB.append("              (select salary_decrypt(max(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// ȡ��ֹ�������ڱ��ڼ�����һ�������ʼ�¼�Ľ����Ϊ�ֽ��
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
			 * //�����ֹ�����ڱ��ڼ���ֻ��һ���ұ��ڼ����޶����ʼ�¼���ֽ��Ϊ0
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
			 * //������ڼ�û�������ʣ�ȡ���һ�������ʼ�¼�Ľ����Ϊ�ֽ����籾�ڼ䣩
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
			// �����ʱ��н�ʣ� ��ȡʱ��н�ʣ�û��ʱ��н�ʣ���ȡ�ַ��Ž��
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

			// ȡ��ֹ�������ڱ��ڼ�����һ�������ʼ�¼�Ľ����Ϊ�ֽ��
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
			 * //������ڼ��ж����ʣ���ʼ�����ڱ��ڼ��ڣ���ȡ���ڼ����һ�������ʼ�¼�Ľ����Ϊ�ֽ��
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
			 * //�����ֹ�����ڱ��ڼ���ֻ��һ���ұ��ڼ����޶����ʼ�¼���ֽ��Ϊ0
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
			 * //������ڼ�û�������ʣ�ȡ���һ�������ʼ�¼�Ľ����Ϊ�ֽ����籾�ڼ䣩
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
