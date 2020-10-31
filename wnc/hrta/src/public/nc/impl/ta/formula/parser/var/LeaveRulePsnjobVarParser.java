package nc.impl.ta.formula.parser.var;

import nc.impl.hr.formula.parser.HIVarParser;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveFormulaCalParam;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

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

		LeaveTypeCopyVO typevo = param.getTypeVO();
		String where = "";

		// ssx added on 2019-12-27
		// �����������ս���
		if (typevo.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE) {
			int calYear = Integer.valueOf(param.getCalYear());
			int nextYear = calYear + 1;
			where = getAlias()
					+ "."
					+ getPrimaryKeyName()
					+ "="
					+ "(select pk_psnjob from hi_psnjob where pk_psndoc=tbm_leavebalance.pk_psndoc "
					// MOD by ssx on 2020-04-30
					// ͬ����ְ��ְ���˵���ְ��¼
					+ " and trnsevent<>4 "
					// end MOD
					+ " and begindate = (select max(job.begindate) from hi_psnjob job inner join hi_psnorg org on job.pk_psnorg = org.pk_psnorg and nvl(job.enddate, '9999-12-31') >= '"
					+ calYear
					+ "' || '-' || substr(org.workagestartdate, 6) and job.begindate < '"
					+ nextYear
					+ "' || '-' || substr(org.workagestartdate, 6) where job.trnsevent<>4 and job.pk_psndoc = tbm_leavebalance.pk_psndoc)  )";
		}
		// end
		else {
			// String where =
			// getAlias()+"."+getPrimaryKeyName()+"=(select pk_psnjob from tbm_psndoc where "+
			// "tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and "+
			// "'"+calDate+"' between tbm_psndoc.begindate and tbm_psndoc.enddate)";
			// �����ն�Ӧ���ڵ�����pk_psnjob
			String calDateJob = " (select pk_psnjob from tbm_psndoc where "
					+ "tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and "
					+ "'" + calDate + "' between tbm_psndoc.begindate and tbm_psndoc.enddate) ";
			// �����ڵ����ڼ�����֮ǰ���� ���Ҳ��������ն�Ӧ�Ŀ��ڵ�����������¼Ҳ�Ҳ����ˣ�
			// ��˲��ü�����֮ǰ�����µĿ��ڵ���
			String beforCalDateJob = " (select pk_psnjob from tbm_psndoc where  tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and "
					+ " enddate = ( select max(enddate) from tbm_psndoc where tbm_psndoc.pk_psndoc=tbm_leavebalance.pk_psndoc and tbm_psndoc.pk_org=tbm_leavebalance.pk_org and ('"
					+ calDate + "' >= tbm_psndoc.enddate or tbm_psndoc.enddate = '9999-12-01' ) ) )";
			// +
			// "'"+calDate+"' >= tbm_psndoc.enddate order by enddate desc ) ";
			where = getAlias() + "." + getPrimaryKeyName() + "= isnull(" + calDateJob + "," + beforCalDateJob + ")";
		}
		return where;
	}
}
