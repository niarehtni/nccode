package nc.impl.ta.formula.parser.var;

import nc.impl.hr.formula.parser.HIVarParser;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveFormulaCalParam;

public class LeaveRulePsnjobVarParser extends HIVarParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LeaveRulePsnjobVarParser() {
		setVarType(VAR_TYPE_PSNJOB);
	}

	@Override
	protected String getWhereSQL(String pk_org, Object... params) {
		LeaveFormulaCalParam param = (LeaveFormulaCalParam) params[0];
		// UFLiteralDate calDate = param.getCalDate();
		UFLiteralDate calDate = param.getCalPsnDate() == null ? param.getCalDate() : param.getCalPsnDate();

		// String where =
		// getAlias()+"."+getPrimaryKeyName()+"=(select pk_psnjob from tbm_psndoc where "+
		// "tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and "+
		// "'"+calDate+"' between tbm_psndoc.begindate and tbm_psndoc.enddate)";
		// �����ն�Ӧ���ڵ�����pk_psnjob
		/*
		 * String calDateJob = " (select pk_psnjob from tbm_psndoc where "+
		 * "tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and "
		 * +
		 * "'"+calDate+"' between tbm_psndoc.begindate and tbm_psndoc.enddate) "
		 * ; //�����ڵ����ڼ�����֮ǰ���� ���Ҳ��������ն�Ӧ�Ŀ��ڵ�����������¼Ҳ�Ҳ����ˣ� //��˲��ü�����֮ǰ�����µĿ��ڵ���
		 * String beforCalDateJob =
		 * " (select pk_psnjob from tbm_psndoc where  tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and "
		 * +
		 * " enddate = ( select max(enddate) from tbm_psndoc where tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and ('"
		 * + calDate +
		 * "' >= tbm_psndoc.enddate or tbm_psndoc.enddate = '9999-12-01' ) ) )"
		 * ; // + //
		 * "'"+calDate+"' >= tbm_psndoc.enddate order by enddate desc ) ";
		 */
		// ssx 2019��9��18��23:01:42 efficiency optimization for std start
		String where = getAlias() + "." + PsnJobVO.PK_PSNORG
				+ " = ( SELECT pk_psnorg FROM hi_psnorg WHERE pk_psndoc = " + getAlias() + ".pk_psndoc " + " AND '"
				+ calDate + "' BETWEEN begindate AND ISNULL(enddate, '9999-12-31')) " + "  AND '" + calDate
				+ "' BETWEEN begindate AND ISNULL(enddate, '9999-12-31') "
				+ "  AND trnsevent IS NOT NULL AND endflag='N'  AND pk_psndoc = tbm_leavebalance.pk_psndoc";

		// end efficiency optimization for std

		return where;
	}
}
