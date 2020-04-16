/**
 * History Resource:   Modifyer :xuhw  Time :2011��2��28��  Reason :������Ȣ��,����ȡԭн�ʶ� 
 */
package nc.impl.wa.paydata.precacu;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.itf.hr.wa.IWaSalaryctymgtConstant;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * ʱ��н�� ����Ҫ���� wa_cacu_data�е�cacu_value ֱ�Ӹ��µ�wa_data�оͿ����ˡ�����Ч�ʸ���
 * 
 * @author: zhangg
 * @date: 2010-5-14 ����09:20:15
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
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
		// ���[���ڼ�]���� [���¼�¼]��[���ű�־]�����ڵ������ ȡ����¼
		// ���������������£� ȡ[���ڼ�]�ڵ�[���ű�־]��[RECORDNUM��С]�ļ�¼��

		// ԭ���Ž��
		if (IWaSalaryctymgtConstant.YFFJE == intType) {
			// ����������е�н����ȡ��нǰ���
			// ���������û�е�н����ȡ ������ǰ�����ĵ�н��¼
			StringBuffer sqlB = new StringBuffer();
			String sql = null;

			sqlB.append("           (coalesce( ");
			sqlB.append("              (select salary_decryptmax(hi_psndoc_wadoc.nmoney)) ");
			sqlB.append("               from hi_psndoc_wadoc ");

			// ����������е�н�� ԭ���Ŷ�������ڱ����ڣ����Ҽ�¼��С�����Ҳ������¼�¼
			// ���������û�е�н�� ԭ���Ŷ�����ڲ��ڱ����ڣ��ڱ�����ǰ�����Ҽ�¼��С��������ȷ���ǲ������¼�¼��
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

			// ����������е�н�� ԭ���Ŷ�������ڱ����ڣ����Ҽ�¼��С�����Ҳ������¼�¼
			// ���������û�е�н�� ԭ���Ŷ�����ڲ��ڱ����ڣ��ڱ�����ǰ�����Ҽ�¼��С��������ȷ���ǲ������¼�¼��
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
		// �ַ��Ž��
		else if (IWaSalaryctymgtConstant.XFFJE == intType) {
			StringBuffer sqlB = new StringBuffer();
			String sql = null;
			// �����Ѿ���Ч�������¼
			// ���� �����������û����Ч��¼����ȥ���µ����һ�ε�н��¼ min(m.recordnum)��
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
			// �����ʱ��н�ʣ� ��ȡʱ��н�ʣ�û��ʱ��н�ʣ���ȡ�ȷ��Ž��
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
