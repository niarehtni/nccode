package nc.impl.wa.func;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.impl.hr.formula.parser.WaFormulaProcessParser;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

@SuppressWarnings("serial")
public class WageDaysParse extends AbstractWAFormulaParse {

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula);
		String replaceArg = "";
		for (String argument : arguments) {
			replaceArg = WaFormulaProcessParser.parse(argument, context);
			if (replaceArg.equals(argument)) {
				continue;
			}
			if (replaceArg.indexOf("wa_cacu_data.") > -1) {
				replaceArg = "( select " + replaceArg + " from wa_cacu_data where wa_cacu_data.pk_wa_class = '"
						+ context.getPk_wa_class() + "' and wa_cacu_data.creator = '" + context.getPk_loginUser()
						+ "' and tbm_psncalendar.pk_psndoc = wa_cacu_data.pk_psndoc )";
			}
			formula = formula.replace(argument, replaceArg);
		}

		arguments = getArguments(formula);
		String begindate = arguments[0];
		String enddate = formula.substring(formula.indexOf(",") + 1, formula.lastIndexOf(")"));
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("(" + valueOfTBMPsn(begindate, enddate, getContext().getPk_org()) + ")");

		return fvo;
	}

	// ssx modified on 2019-12-01
	// ����ר�ü�н����������ʵ��
	// �����ձ���
	// /**
	// * ƽ̨����������н����������:ȡ��ְ��֯��н����������������ʼ���ڣ���ֹ���ڡ���ְ��֯���������ֶΣ���sqlƬ��
	// *
	// * @param beginDate
	// * ��ʼ����
	// * @param endDate
	// * ��ֹ����
	// * @param workorg
	// * ��ְ��֯���������ֶ�
	// * @return ���նԵ����и�ǰ�ķǹ��ݵĻ���
	// */
	// public static String valueOfTBMPsn(String beginDate, String
	// endDate,String workorg){
	// String cond =
	// " where pk_workcalendar in ( coalesce((select workcalendar "+
	// " from org_orgs "+
	// " where pk_org = "+workorg+
	// " and isnull(workcalendar,'~')<>'~') ,coalesce((select workcalendar" +
	// " from org_group "+
	// " where pk_group in(	select pk_group "+
	// " from org_orgs "+
	// " where pk_org = "+workorg+")"+
	// " and isnull(workcalendar,'~')<>'~') ,(select pk_workcalendar "+
	// " from bd_workcalendar "+
	// " where (isdefaultcalendar= 'Y' ) "+
	// " and enablestate=2 ))) )";
	// //������ҵ��ģƽ̨����֯ѡ��Ĺ���������δѡ��ʱʹ�ü��Ź�������������δ���ù�������ʱʹ��ȫ��Ĭ�Ϲ������������м��㣬
	// //�������պ͹������ų�������
	// cond += " and calendardate >= " + beginDate + " and calendardate <= " +
	// endDate +" and datetype<>1";
	// String sql =
	// " select count(pk_workcalendardate) from bd_workcalendardate " + cond;
	// return sql;
	// }

	public static String valueOfTBMPsn(String beginDate, String endDate, String workorg) {
		beginDate = beginDate.replace("wa_data.", "wd.");
		endDate = endDate.replace("wa_data.", "wd.");

		beginDate = formatRef(beginDate.trim().replace("'", ""));
		endDate = formatRef(endDate.trim().replace("'", ""));

		String sql = "(SELECT SUM(ROUND(TO_NUMBER(TO_DATE(least(" + endDate
				+ ",NVL(wadoc.enddate,'9999-12-31')),'yyyy-MM-dd')-TO_DATE( greatest(" + beginDate
				+ ",wadoc.begindate), 'yyyy-MM-dd')))+1) actdays "
				+ "FROM wa_data wd INNER JOIN hi_psndoc_wadoc wadoc ON wadoc.pk_psndoc = wd.pk_psndoc "
				+ "WHERE pk_wa_item='1001A110000000000GEF' AND wd.pk_wa_data = wa_cacu_data.pk_wa_data AND least("
				+ endDate + ",NVL(enddate,'9999-12-31')) >= greatest(" + beginDate + ",begindate))";
		return sql;
	}

	// ��������ڎ�����̖�������ڲ�����̖
	private static String formatRef(String strDate) {
		String regex = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(strDate.replace("'", ""));
		boolean b = m.matches();
		if (b) {
			strDate = "'" + strDate + "'";
		} else {
			strDate = strDate.replace("'", "");
		}

		return strDate;
	}

	// end

}
