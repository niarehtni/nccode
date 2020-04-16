package nc.impl.ta.formula.parser.var;

import nc.impl.hr.formula.parser.HIVarParser;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveFormulaCalParam;

public class LeaveRulePsnorgVarParser extends HIVarParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5706497491745128623L;

	public LeaveRulePsnorgVarParser() {
		setVarType(VAR_TYPE_PSNORG);
	}

	@Override
	protected String getWhereSQL(String pk_org, Object... params) {
		LeaveFormulaCalParam param = (LeaveFormulaCalParam) params[0];
		UFLiteralDate calDate = param.getCalDate();
		// String where =
		// getAlias()+"."+getPrimaryKeyName()+"=(select pk_psnorg from hi_psnjob where pk_psnjob="+
		// "(select pk_psnjob from tbm_psndoc where "+
		// "tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and "+
		// "'"+calDate+"' between tbm_psndoc.begindate and tbm_psndoc.enddate))";
		// "tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and tbm_psndoc.enddate = "+
		// "( select max(enddate) from tbm_psndoc where tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and ('"
		// +
		// calDate +
		// "' >= tbm_psndoc.begindate or tbm_psndoc.enddate = '9999-12-01' ) ) ) )"
		// ;
		String where = getAlias() + "." + getPrimaryKeyName() + "=tbm_leavebalance.pk_psnorg";
		return where;
	}
}
