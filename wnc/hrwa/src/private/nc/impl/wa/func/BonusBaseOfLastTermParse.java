package nc.impl.wa.func;

import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class BonusBaseOfLastTermParse extends AbstractWAFormulaParse {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -6180538078543408996L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula.toString());
		String specificDate = arguments[0].replaceAll("\'", "");

		StringBuffer strSQL = new StringBuffer();
		strSQL.append("select wa_period.cenddate ");
		strSQL.append("  from wa_periodstate ");
		strSQL.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		strSQL.append("                         wa_period.pk_wa_period) ");
		strSQL.append(" where pk_wa_class = '" + getContext().getPk_wa_class() + "' ");
		strSQL.append("   and (cyear || cperiod) < '" + getContext().getWaYear() + getContext().getWaPeriod() + "' ");
		strSQL.append(" ORDER BY cyear || cperiod DESC");
		String lastTermEndDate = (String) this.getDaoManager().getBaseDao()
				.executeQuery(strSQL.toString(), new ColumnProcessor());

		String pk_back_trnstype = SysInitQuery.getParaString(this.getContext().getPk_org(), "TWHR12");
		if (StringUtils.isEmpty(pk_back_trnstype)) {
			throw new BusinessException("組織參數中未定義留停複職的異動類型，請檢查組織參數 [TWHR12]");
		}

		// 上期期末獎金基數：本薪+夥食津貼+職務加給+崗位津貼+全勤獎金
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce("
				+ "(select SUM(SALARY_DECRYPT(isnull(nmoney,0))) from hi_psndoc_wadoc where  "
				+ " EXISTS(SELECT PK_PSNDOC FROM hi_psndoc_wadoc WHERE ( SELECT d_1 FROM wa_data WHERE pk_wa_data = wa_cacu_data.pk_wa_data) BETWEEN begindate AND NVL (enddate , '9999-12-31') AND pk_psndoc = hi_psndoc_wadoc.pk_psndoc) and"
				+ "(case when (select count(pk_psnjob) from hi_psnjob where (trnsevent  in (1,2) or trnstype='"
				+ pk_back_trnstype
				+ "') and (select "
				// 指定日期在职
				+ specificDate
				+ " from wa_data where pk_wa_data = wa_cacu_data.pk_cacu_data) between begindate and isnull(enddate, '9999-12-31') and pk_psndoc = hi_psndoc_wadoc.pk_psndoc) = 0 "
				+ " then '"
				+ lastTermEndDate
				+ "' else (select GREATEST(MAX(begindate), '2019-08-31') from hi_psnjob where (trnsevent  in (1,2) or trnstype='"
				+ pk_back_trnstype
				+ "') and  (select "
				// 指定日期在职
				+ specificDate
				+ " from wa_data where pk_wa_data = wa_cacu_data.pk_cacu_data)  between begindate and isnull(enddate, '9999-12-31') and pk_psndoc = hi_psndoc_wadoc.pk_psndoc) end)"
				+ " between begindate and isnull(enddate,'9999-12-31')  and "
				+ " pk_wa_item in (select pk_wa_item from wa_item where itemkey in ('f_34','f_35','f_36','f_37','f_40','f_38')) and " // 12/13:Lisa增加班別津貼
				+ " pk_psndoc = wa_cacu_data.pk_psndoc)" + ", 0)");
		return fvo;
	}

}
