package nc.impl.ta.formula.parser.func;

import nc.vo.pub.BusinessException;

public class SumCountFuncParser extends AggregateFuncParser {
    private static final long serialVersionUID = -8891628401300003931L;

    public SumCountFuncParser() {
	setFuncName("directmonthsumcount");
    }

    @Override
    protected String translateFunc2SQL(String pk_org, String formula, String funcStr, String[] funcArgs,
	    Object... params) throws BusinessException {

	int dateType = 0;
	int settleType = 0;
	if (formula.toString().contains("NORMAL")) {
	    dateType = 1;
	}
	if (formula.toString().contains("OFFDAY")) {
	    dateType = 2;
	}
	if (formula.toString().contains("HOLIDAYS")) {
	    dateType = 3;
	}
	if (formula.toString().contains("NATIONALDAY")) {
	    dateType = 4;
	}
	if (formula.toString().contains("PERIOD_TOSALARY")) {
	    settleType = 1;
	}
	if (formula.toString().contains("OTHER_TOSALARY")) {
	    settleType = 2;
	}
	if (formula.toString().contains("PERIOD_TOREST")) {
	    settleType = 3;
	}
	if (formula.toString().contains("TOTAL")) {
	    settleType = 4;
	}

	String strsql = "isnull((SELECT otsumhours FROM tbm_monthstat_cacu WHERE "
		+ "tbm_monthstat_cacu.pk_monthstat = tbm_monthstat.pk_monthstat "
		+ "and tbm_monthstat_cacu.datedaytype=" + dateType + " and tbm_monthstat_cacu.otSettletype="
		+ settleType + ") ,0)";
	return strsql;
    }

}
