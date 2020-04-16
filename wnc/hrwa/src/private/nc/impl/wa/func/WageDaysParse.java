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
	// 启碁专用计薪日天数函数实现
	// 不具普遍性
	// /**
	// * 平台工作日历计薪日天数函数:取任职组织计薪日天数（参数：起始日期，终止日期、任职组织关联主键字段）的sql片断
	// *
	// * @param beginDate
	// * 起始日期
	// * @param endDate
	// * 终止日期
	// * @param workorg
	// * 任职组织关联主键字段
	// * @return 假日对调后切割前的非公休的汇总
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
	// //根据企业建模平台中组织选择的工作日历（未选择时使用集团工作日历，集团未设置工作日历时使用全局默认工作日历）进行计算，
	// //包含假日和工作日排除公休日
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

	// 如果是日期帶單引號，非日期不帶引號
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
