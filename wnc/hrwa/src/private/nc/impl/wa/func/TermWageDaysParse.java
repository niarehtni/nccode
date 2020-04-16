package nc.impl.wa.func;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.impl.hr.formula.parser.WaFormulaProcessParser;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

@SuppressWarnings("serial")
public class TermWageDaysParse extends AbstractWAFormulaParse {

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

	public static String valueOfTBMPsn(String beginDate, String endDate, String workorg) {
		beginDate = beginDate.replace("wa_data.", "wd.");
		endDate = endDate.replace("wa_data.", "wd.");

		beginDate = formatRef(beginDate.trim().replace("'", ""));
		endDate = formatRef(endDate.trim().replace("'", ""));

		String sql = "(SELECT SUM(ROUND(TO_NUMBER(TO_DATE(least("
				+ endDate
				+ ",isnull(wadoc.enddate,'9999-12-31'),isnull(po.enddate,'9999-12-31')),'yyyy-MM-dd')-TO_DATE( greatest("
				+ beginDate
				+ ",wadoc.begindate,po.begindate), 'yyyy-MM-dd')))+1) actdays "
				+ "FROM wa_data wd "
				+ " INNER JOIN hi_psnorg po on wd.pk_psndoc=po.pk_psndoc "
				+ "INNER JOIN hi_psndoc_wadoc wadoc ON wadoc.pk_psndoc = wd.pk_psndoc "
				+ "WHERE po.lastflag='Y' and pk_wa_item='1001A110000000000GEF' AND wd.pk_wa_data = wa_cacu_data.pk_wa_data AND least("
				+ endDate + ",isnull(wadoc.enddate,'9999-12-31'),isnull(po.enddate,'9999-12-31')) >= greatest("
				+ beginDate + ",wadoc.begindate,po.begindate))";
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
