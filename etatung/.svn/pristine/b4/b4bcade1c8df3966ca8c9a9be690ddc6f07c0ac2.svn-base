package nc.impl.ta.leave;

import java.util.List;
import java.util.Map;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ArrayHelper;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.ILeaveQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveServiceImpl implements ILeaveQueryService {
	public LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,
			String pk_psnorg, String pk_leaveType, String year, String month,
			int leaveIndex) throws BusinessException {
		ITimeItemQueryService itemService = (ITimeItemQueryService) NCLocator
				.getInstance().lookup(ITimeItemQueryService.class);
		TimeItemCopyVO typeVO = itemService.queryCopyTypesByDefPK(pk_org,
				pk_leaveType, 0);
		return queryByPsnLeaveTypePeriod(pk_org, pk_psnorg,
				(LeaveTypeCopyVO) typeVO, year, month, leaveIndex);
	}

	public LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,
			String pk_psnorg, LeaveTypeCopyVO leaveTypeVO, String year,
			String month, int leaveIndex) throws BusinessException {
		if (leaveTypeVO == null) {
			return null;
		}
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
//		boolean isYear = (leavesetperiod == 1) || (leavesetperiod == 2);
		
		//BEGIN 张恒    将65年资起算日的整合到63   2018/8/28
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE;
		//END 张恒    将65年资起算日的整合到63   2018/8/28
		
		String where = " pk_org=? and pk_leavetype=? and pk_psnorg=? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(leaveTypeVO.getPk_timeitem());
		para.addParam(pk_psnorg);
		if ((leaveTypeVO.getIslactation() == null)
				|| (!leaveTypeVO.getIslactation().booleanValue())) {
			where = where + " and leaveyear =? ";
			para.addParam(year);
		}
		if (!isYear) {
			where = where + " and leavemonth=? ";
			para.addParam(month);
		}
		where = where + " and leaveindex=? ";
		para.addParam(leaveIndex);
		return (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class,
				new BaseDAO().retrieveByClause(LeaveRegVO.class, where, para));
	}

	public LeaveRegVO[] queryByPsnsLeaveTypePeriod(String pk_org,
			String[] pk_psnorgs, LeaveTypeCopyVO leaveTypeVO, String year,
			String month, int leaveIndex) throws BusinessException {
		if ((leaveTypeVO == null) || (ArrayUtils.isEmpty(pk_psnorgs))) {
			return null;
		}
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
//		boolean isYear = (leavesetperiod == 1) || (leavesetperiod == 2);
		
		//BEGIN 张恒    将65年资起算日的整合到63   2018/8/28
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE;
		//END 张恒    将65年资起算日的整合到63   2018/8/28
		
		InSQLCreator isc = new InSQLCreator();
		try {
			String where = " pk_org=? and pk_leavetype=? and pk_psnorg in ("
					+ isc.getInSQL(pk_psnorgs) + ") ";
			SQLParameter para = new SQLParameter();
			para.addParam(pk_org);
			para.addParam(leaveTypeVO.getPk_timeitem());
			if ((leaveTypeVO.getIslactation() == null)
					|| (!leaveTypeVO.getIslactation().booleanValue())) {
				where = where + " and leaveyear =? ";
				para.addParam(year);
			}
			if (!isYear) {
				where = where + " and leavemonth=? ";
				para.addParam(month);
			}
			where = where + " and leaveindex=? ";
			para.addParam(leaveIndex);
			return (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class,
					new BaseDAO().retrieveByClause(LeaveRegVO.class, where,
							para));
		} finally {
			if (isc != null) {
				isc.clear();
			}
		}
	}

	public String[] queryOverToRestPeriod(String pk_org, UFDate busiDate)
			throws BusinessException {
		TimeItemCopyVO copyvo = ((ITimeItemQueryService) NCLocator
				.getInstance().lookup(ITimeItemQueryService.class))
				.queryCopyTypesByDefPK(pk_org, "1002Z710000000021ZM1", 0);
		PeriodVO period = PeriodServiceFacade.queryByDate(pk_org,
				new UFLiteralDate(busiDate.toDate()));
		if ((period == null) || (period.isSeal())) {
			throw new BusinessException(ResHelper.getString("6017leave",
					"06017leave0224"));
		}
		if (copyvo == null) {
			throw new BusinessException(ResHelper.getString("6017leave",
					"06017leave0248"));
		}
		if (copyvo.getLeavesetperiod().intValue() == 0) {
			return new String[] { period.getTimeyear(), period.getTimemonth() };
		}
		return new String[] { period.getTimeyear() };
	}

	public Map<String, LeaveCommonVO[]> queryAllSuperVOExcNoPassByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return null;
	}

	public LeaveCommonVO[] queryAllSuperVOExcNoPassByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		return queryAllSuperVOExcNoPassByPsnDate(pk_org, pk_psndoc, null, null);
	}

	public LeaveCommonVO[] queryAllSuperVOExcNoPassByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		Map<String, LeaveCommonVO[]> map = queryAllSuperVOExcNoPassByPsndocInSQLDate(
				pk_org, "'" + pk_psndoc + "'", beginDate, endDate);
		if (MapUtils.isEmpty(map)) {
			return null;
		}
		return (LeaveCommonVO[]) map.get(pk_psndoc);
	}

	public Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return null;
	}

	public LeaveRegVO[] queryAllSuperVOIncEffectiveByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		return null;
	}

	public LeaveRegVO[] queryAllSuperVOIncEffectiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		return null;
	}

	public Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsnDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return null;
	}

	public Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocsDate(
			String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if ((StringUtils.isBlank(pk_org)) || (ArrayUtils.isEmpty(pk_psndocs))) {
			return null;
		}
		InSQLCreator isc = new InSQLCreator();
		try {
			return queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org,
					isc.getInSQL(pk_psndocs), beginDate, endDate);
		} finally {
			isc.clear();
		}
	}

	public Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(
				LeaveRegVO.class, pk_org, psndocInSQL, beginDate, endDate,
				SQLHelper.getBoolNullSql("islactation"));
	}

	public Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		String cond = "pk_org=? and islactation='Y' and pk_psndoc in ("
				+ psndocInSQL + ") and " + "leavebegindate" + "<=? and "
				+ "leaveenddate" + ">=?";

		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		LeaveRegVO[] results = (LeaveRegVO[]) CommonUtils.toArray(
				LeaveRegVO.class,
				new BaseDAO().retrieveByClause(LeaveRegVO.class, cond, para));
		return CommonUtils.group2ArrayByField("pk_psndoc", results);
	}

	public LeaveRegVO[] queryAllLactationVOIncEffictiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		String cond = "pk_org=? and islactation='Y' and pk_psndoc =? and leavebegindate<=? and leaveenddate>=?";

		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(pk_psndoc);
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		LeaveRegVO[] results = (LeaveRegVO[]) CommonUtils.toArray(
				LeaveRegVO.class,
				new BaseDAO().retrieveByClause(LeaveRegVO.class, cond, para));
		return results;
	}

	public Map<String, LeaveCommonVO[]> queryAllSuperVOExcNoPassByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		Map<String, LeavebVO[]> approveMap = BillMethods
				.queryApproveBodyVOMapByPsndocInSQLAndDateScopeExcNoPass(
						LeavebVO.class, pk_org, psndocInSQL, beginDate,
						endDate, SQLHelper.getBoolNullSql("islactation"));

		Map<String, LeaveRegVO[]> regMap = BillMethods
				.queryRegVOMapByPsndocInSQLAndDateScope(LeaveRegVO.class,
						pk_org, psndocInSQL, beginDate, endDate, null);

		return CommonMethods.mergeGroupedTimeScopeMap(LeaveCommonVO.class,
				approveMap, regMap);
	}

	public LeaveCommonVO[] queryIntersectionBills(LeaveCommonVO bill)
			throws BusinessException {
		LeavebVO[] approveVOs = (LeavebVO[]) BillMethods
				.queryIntersetionApproveBodyVOsByPsndocAndTimeScopeExcNoPass(
						LeavebVO.class, bill.getPk_org(), bill);

		LeaveRegVO[] regVOs = (LeaveRegVO[]) BillMethods
				.queryIntersetionRegVOsByPsndocAndTimeScope(LeaveRegVO.class,
						bill.getPk_org(), bill);

		return (LeaveCommonVO[]) ArrayHelper.addAll(approveVOs, regVOs,
				LeaveCommonVO.class);
	}

	public Map<String, LeaveCommonVO[]> queryIntersectionBillsMap(
			LeaveCommonVO[] bills) throws BusinessException {
		if (ArrayUtils.isEmpty(bills)) {
			return null;
		}
		String pk_org = bills[0].getPk_org();

		LeavebVO[] approveVOs = (LeavebVO[]) BillMethods
				.queryIntersetionApproveBodyVOsByPsndocAndTimeScopeExcNoPass(
						LeavebVO.class, pk_org, bills);

		LeaveRegVO[] regVOs = (LeaveRegVO[]) BillMethods
				.queryIntersetionRegVOsByPsndocAndTimeScope(LeaveRegVO.class,
						pk_org, bills);

		LeaveCommonVO[] retVOs = (LeaveCommonVO[]) ArrayHelper.addAll(
				approveVOs, regVOs, LeaveCommonVO.class);
		return CommonUtils.group2ArrayByField("pk_psndoc", retVOs);
	}

	public LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(
			String pk_org, String pk_psnorg, LeaveTypeCopyVO leaveTypeVO,
			String year, String month, int leaveIndex) throws BusinessException {
		if (leaveTypeVO == null) {
			return null;
		}
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
//		boolean isYear = (leavesetperiod == 1) || (leavesetperiod == 2);
		
		//BEGIN 张恒    将65年资起算日的整合到63   2018/8/28
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE;
		//END 张恒    将65年资起算日的整合到63   2018/8/28
		
		String where = " h.pk_org=? and h.pk_leavetype=? and h.pk_psnorg=? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(leaveTypeVO.getPk_timeitem());
		para.addParam(pk_psnorg);
		if ((leaveTypeVO.getIslactation() == null)
				|| (!leaveTypeVO.getIslactation().booleanValue())) {
			where = where + " and leaveyear =? ";
			para.addParam(year);
		}
		if (!isYear) {
			where = where + " and leavemonth=? ";
			para.addParam(month);
		}
		where = where + " and leaveindex=? and approve_state in(-1,3,2)";
		para.addParam(leaveIndex);
		String sql = "select b.*,h.pk_psndoc,h.pk_psnorg,h.pk_psnjob,h.pk_leavetype from "
				+ LeavebVO.getDefaultTableName()
				+ " b inner join "
				+ LeavehVO.getDefaultTableName()
				+ " h on b.pk_leaveh=h.pk_leaveh " + " where " + where;

		return (LeavebVO[]) CommonUtils.toArray(LeavebVO.class,
				(List) new BaseDAO().executeQuery(sql, para,
						new BeanListProcessor(LeavebVO.class)));
	}

	public LeavebVO[] queryBeforePassWithoutNoPassByPsnsLeaveTypePeriod(
			String pk_org, String[] pk_psnorgs, LeaveTypeCopyVO leaveTypeVO,
			String year, String month, int leaveIndex) throws BusinessException {
		if (leaveTypeVO == null) {
			return null;
		}
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
//		boolean isYear = (leavesetperiod == 1) || (leavesetperiod == 2);
		
		//BEGIN 张恒    将65年资起算日的整合到63   2018/8/28
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE;
		//END 张恒    将65年资起算日的整合到63   2018/8/28
		
		InSQLCreator isc = new InSQLCreator();
		try {
			String where = " h.pk_org=? and h.pk_leavetype=? and h.pk_psnorg in ("
					+ isc.getInSQL(pk_psnorgs) + ") ";
			SQLParameter para = new SQLParameter();
			para.addParam(pk_org);
			para.addParam(leaveTypeVO.getPk_timeitem());
			if ((leaveTypeVO.getIslactation() == null)
					|| (!leaveTypeVO.getIslactation().booleanValue())) {
				where = where + " and leaveyear =? ";
				para.addParam(year);
			}
			if (!isYear) {
				where = where + " and leavemonth=? ";
				para.addParam(month);
			}
			where = where + " and leaveindex=? and approve_state in(-1,3,2)";
			para.addParam(leaveIndex);
			String sql = "select b.*,h.pk_psndoc,h.pk_psnorg,h.pk_psnjob,h.pk_leavetype from "
					+ LeavebVO.getDefaultTableName()
					+ " b inner join "
					+ LeavehVO.getDefaultTableName()
					+ " h on b.pk_leaveh=h.pk_leaveh " + " where " + where;

			return (LeavebVO[]) CommonUtils.toArray(LeavebVO.class,
					(List) new BaseDAO().executeQuery(sql, para,
							new BeanListProcessor(LeavebVO.class)));
		} finally {
			if (isc != null) {
				isc.clear();
			}
		}
	}

	public LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(
			String pk_org, String pk_psnorg, String pk_leaveType, String year,
			String month, int leaveIndex) throws BusinessException {
		ITimeItemQueryService itemService = (ITimeItemQueryService) NCLocator
				.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) itemService
				.queryCopyTypesByDefPK(pk_org, pk_leaveType, 0);
		return queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(pk_org,
				pk_psnorg, typeVO, year, month, leaveIndex);
	}

	public AggLeaveVO queryByPk(String key) throws BusinessException {
		return new LeaveApplyQueryMaintainImpl().queryByPk(key);
	}

	public LeaveRegVO[] queryLeaveRegByPsnAndDateScope(String pk_org,
			String pk_psndoc, String tbmYear, String tbmMonth,
			String pk_timeitem) throws BusinessException {
		PeriodVO period = ((IPeriodQueryService) NCLocator.getInstance()
				.lookup(IPeriodQueryService.class)).queryByYearMonth(pk_org,
				tbmYear, tbmMonth);
		UFLiteralDate begindate = period.getBegindate();
		UFLiteralDate enddate = period.getEnddate();
		String condition = "pk_psndoc =? and leavebegindate <=? and leaveenddate >=? and pk_leavetype =? and pk_org =? ";

		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(enddate.toString());
		para.addParam(begindate.toString());
		para.addParam(pk_timeitem);
		para.addParam(pk_org);
		LeaveRegVO[] results = (LeaveRegVO[]) CommonUtils.toArray(
				LeaveRegVO.class, new BaseDAO().retrieveByClause(
						LeaveRegVO.class, condition, para));
		return results;
	}
}
