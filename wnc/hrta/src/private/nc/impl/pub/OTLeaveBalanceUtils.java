package nc.impl.pub;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.hr.utils.InSQLCreator;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

import org.apache.commons.lang.StringUtils;

public class OTLeaveBalanceUtils {
	@SuppressWarnings("unchecked")
	public static String[] getPsnListByDateScope(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if (pk_psndocs == null || pk_psndocs.length == 0) {
			List<String> psnList = (List<String>) new BaseDAO()
					.executeQuery(
							"select distinct pk_psndoc from tbm_psndoc psn "
									+ "inner join hi_psnorg og on psn.pk_psnorg = og.pk_psnorg "
									+ (pk_depts == null || pk_depts.length == 0 ? ""
											: " inner join hi_psnjob on og.pk_psnorg = hi_psnjob.pk_psnorg ")
									+ "where psn.pk_org = '"
									+ pk_org
									+ "' and isnull(psn.dr,0)=0 and '"
									+ beginDate.toString()
									+ "' <= psn.enddate and '"
									+ endDate.toString()
									+ "' >= psn.begindate "
									+ (pk_depts == null || pk_depts.length == 0 ? "" : " and hi_psnjob.pk_dept in ("
											+ new InSQLCreator().getInSQL(pk_depts) + ")")
									+ (isTermLeave == null ? ""
											: " and (select count(pk_psnjob) from hi_psnjob where pk_psnorg=psn.pk_psnorg and trnstype=(SELECT value FROM pub_sysinit WHERE pk_org=psn.pk_org AND initcode='TWHR11')) "
													+ (isTermLeave.booleanValue() ? ">" : "=")
													+ " (select count(pk_psnjob) from hi_psnjob where pk_psnorg=psn.pk_psnorg and trnstype=(SELECT value FROM pub_sysinit WHERE pk_org=psn.pk_org AND initcode='TWHR12'))"),
							new ColumnListProcessor());
			if (psnList != null && psnList.size() > 0) {
				pk_psndocs = psnList.toArray(new String[0]);
			}
		}
		return pk_psndocs;
	}

	@SuppressWarnings("unchecked")
	public static String[] getPsnListByQueryYear(String pk_org, String[] pk_psndocs, String[] pk_depts,
			UFBoolean isTermLeave, String queryYear) throws BusinessException {
		if (pk_psndocs == null || pk_psndocs.length == 0) {
			List<String> psnList = (List<String>) new BaseDAO()
					.executeQuery(
							"select distinct psn.pk_psndoc from tbm_psndoc psn "
									+ "inner join hi_psnorg og on psn.pk_psnorg = og.pk_psnorg "
									+ (pk_depts == null || pk_depts.length == 0 ? ""
											: " inner join hi_psnjob on og.pk_psnorg = hi_psnjob.pk_psnorg ")
									+ " where psn.pk_org = '"
									+ pk_org
									+ "' and psn.enddate >= ('"
									+ queryYear
									+ "' || right(og.workagestartdate, 6)) and psn.begindate < ('"
									+ String.valueOf(Integer.valueOf(queryYear) + 1)
									+ "' || right(og.workagestartdate, 6))"
									+ (pk_depts == null || pk_depts.length == 0 ? "" : " and hi_psnjob.pk_dept in ("
											+ new InSQLCreator().getInSQL(pk_depts) + ")")
									+ (isTermLeave == null ? ""
											: " and (select count(pk_psnjob) from hi_psnjob where pk_psnorg=psn.pk_psnorg and trnstype=(SELECT value FROM pub_sysinit WHERE pk_org=psn.pk_org AND initcode='TWHR11')) "
													+ (isTermLeave.booleanValue() ? ">" : "=")
													+ " (select count(pk_psnjob) from hi_psnjob where pk_psnorg=psn.pk_psnorg and trnstype=(SELECT value FROM pub_sysinit WHERE pk_org=psn.pk_org AND initcode='TWHR12'))"),
							new ColumnListProcessor());
			if (psnList != null && psnList.size() > 0) {
				pk_psndocs = psnList.toArray(new String[0]);
			}
		}

		return pk_psndocs;
	}

	public static LeaveRegVO[] getLeaveRegByPsnYear(String pk_org, String pk_psndoc, String queryYear,
			String pk_leavetypecopy) throws BusinessException {
		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnWorkStartDateMap(pk_org,
				new String[] { pk_psndoc }, null, null, queryYear, pk_leavetypecopy);

		return getLeaveRegByPsnDate(pk_org, pk_psndoc,
				getBeginDateByWorkAgeStartDate(queryYear, psnWorkStartDate.get(pk_psndoc)),
				getEndDateByWorkAgeStartDate(queryYear, psnWorkStartDate.get(pk_psndoc)), pk_leavetypecopy);
	}

	public static LeaveRegVO[] getLeaveRegByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_leavetypecopy) throws BusinessException {
		String strSQL = "pk_org='" + pk_org + "' and pk_psndoc='" + pk_psndoc + "' and pk_leavetypecopy='"
				+ pk_leavetypecopy + "' and leavebegindate between '" + beginDate.toString() + "' and '"
				+ endDate.toString() + "' and dr=0";
		Collection<LeaveRegVO> vos = new BaseDAO().retrieveByClause(LeaveRegVO.class, strSQL);

		if (vos != null && vos.size() > 0) {
			return vos.toArray(new LeaveRegVO[0]);
		}

		return new LeaveRegVO[0];
	}

	public static OTLeaveBalanceVO createNewHeadVO(String pk_org, String pk_psndoc, UFDouble totalAmount,
			UFDouble spentAmount, UFDouble remainAmount, UFDouble frozenAmount, UFDouble useableAmount) {
		OTLeaveBalanceVO newHeadVo = new OTLeaveBalanceVO();
		newHeadVo.setPk_psndoc(pk_psndoc);
		newHeadVo.setPk_otleavebalance(pk_psndoc);
		newHeadVo.setPk_org(pk_org);
		newHeadVo.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
		newHeadVo.setTotalhours(totalAmount);
		newHeadVo.setConsumedhours(spentAmount);
		newHeadVo.setRemainhours(remainAmount);
		newHeadVo.setFrozenhours(frozenAmount);
		newHeadVo.setFreehours(useableAmount);
		return newHeadVo;
	}

	public static UFDouble getUFDouble(Object value) {
		if (value == null) {
			return UFDouble.ZERO_DBL;
		} else {
			return new UFDouble(String.valueOf(value));
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, UFLiteralDate> getPsnWorkStartDateMap(String pk_org, String[] pk_psndocs,
			String[] pk_depts, UFBoolean isTermLeave, String queryYear, String pk_leavetypecopy)
			throws BusinessException {
		Map<String, UFLiteralDate> rtn = new HashMap<String, UFLiteralDate>();
		if (!StringUtils.isEmpty(pk_org)) {
			String pk_otleavetype = SysInitQuery.getParaString(pk_org, "TWHRT08");
			String pk_exleavetype = SysInitQuery.getParaString(pk_org, "TWHRT10");

			String effCondition = " ";
			if (pk_leavetypecopy.equals(pk_otleavetype)) {
				effCondition = " and (select count(pk_segdetail) from hrta_segdetail where pk_psndoc = psn.pk_psndoc and expirydate >= '"
						+ queryYear
						+ "' || right(og.workagestartdate, 6) AND expirydate < '"
						+ String.valueOf(Integer.valueOf(queryYear) + 1) + "' || right(og.workagestartdate, 6)) > 0";
			} else if (pk_leavetypecopy.equals(pk_exleavetype)) {
				effCondition = " and (select count(pk_extrarest) from tbm_extrarest where pk_psndoc = psn.pk_psndoc and expiredate >= '"
						+ queryYear
						+ "' || right(og.workagestartdate, 6) AND expiredate < '"
						+ String.valueOf(Integer.valueOf(queryYear) + 1) + "' || right(og.workagestartdate, 6)) > 0";
			}

			List<Map> psnList = (List<Map>) new BaseDAO()
					.executeQuery(
							"select distinct psn.pk_psndoc, og.workagestartdate from tbm_psndoc psn "
									+ "inner join hi_psnorg og on psn.pk_psnorg = og.pk_psnorg "
									+ ((pk_depts == null || pk_depts.length == 0) && isTermLeave == null ? ""
											: " inner join hi_psnjob on og.pk_psnorg = hi_psnjob.pk_psnorg ")
									+ " where psn.pk_org = '"
									+ pk_org
									+ "' and psn.enddate >= ('"
									+ queryYear
									+ "' || right(og.workagestartdate, 6)) and psn.begindate < ('"
									+ String.valueOf(Integer.valueOf(queryYear) + 1)
									+ "' || right(og.workagestartdate, 6)) and og.workagestartdate is not null "
									+ ((pk_psndocs != null && pk_psndocs.length > 0) ? (" and psn.pk_psndoc in ("
											+ (new InSQLCreator()).getInSQL(pk_psndocs, false) + ")") : "")
									+ effCondition
									+ (pk_depts == null || pk_depts.length == 0 ? "" : " and hi_psnjob.pk_dept in ("
											+ new InSQLCreator().getInSQL(pk_depts) + ")")
									+ (isTermLeave == null ? ""
											: " and (select count(pk_psnjob) from hi_psnjob where pk_psnorg=psn.pk_psnorg and trnstype=(SELECT value FROM pub_sysinit WHERE pk_org=psn.pk_org AND initcode='TWHR11')) "
													+ (isTermLeave.booleanValue() ? ">" : "=")
													+ " (select count(pk_psnjob) from hi_psnjob where pk_psnorg=psn.pk_psnorg and trnstype=(SELECT value FROM pub_sysinit WHERE pk_org=psn.pk_org AND initcode='TWHR12'))"),
							new MapListProcessor());

			if (psnList != null && psnList.size() > 0) {
				for (Map psn : psnList) {
					String pk_psndoc = (String) psn.get("pk_psndoc");
					UFLiteralDate workstartdate = new UFLiteralDate((String) psn.get("workagestartdate"));
					if (!rtn.containsKey(pk_psndoc)) {
						rtn.put(pk_psndoc, workstartdate);
					}
				}
			}
		}
		return rtn;
	}

	public static UFLiteralDate getEndDateByWorkAgeStartDate(String queryYear,
			Map<String, UFLiteralDate> psnWorkStartDate, String pk_psndoc) {
		return getEndDateByWorkAgeStartDate(queryYear, psnWorkStartDate.get(pk_psndoc));
	}

	public static UFLiteralDate getEndDateByWorkAgeStartDate(String queryYear, UFLiteralDate psnWorkStartDate) {
		UFLiteralDate endDate;
		if (psnWorkStartDate.toString().substring(5).equals("02-29")) {
			endDate = new UFLiteralDate(String.valueOf(Integer.valueOf(queryYear) + 1) + "-03-01").getDateBefore(1);
		} else {
			endDate = new UFLiteralDate(String.valueOf(Integer.valueOf(queryYear) + 1)
					+ psnWorkStartDate.toString().substring(4)).getDateBefore(1);
		}
		return endDate;
	}

	public static UFLiteralDate getBeginDateByWorkAgeStartDate(String queryYear,
			Map<String, UFLiteralDate> psnWorkStartDate, String pk_psndoc) {
		return getBeginDateByWorkAgeStartDate(queryYear, psnWorkStartDate.get(pk_psndoc));
	}

	public static UFLiteralDate getBeginDateByWorkAgeStartDate(String queryYear, UFLiteralDate psnWorkStartDate) {
		UFLiteralDate beginDate;
		if (psnWorkStartDate.toString().substring(5).equals("02-29")) {
			beginDate = new UFLiteralDate(queryYear + "-03-01");
		} else {
			beginDate = new UFLiteralDate(queryYear + psnWorkStartDate.toString().substring(4));
		}
		return beginDate;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, UFLiteralDate> getPsnOrgMapByPsnBeginDate(String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws DAOException, BusinessException {
		Collection<PsnOrgVO> psnorgs = new BaseDAO().retrieveByClause(PsnOrgVO.class, "pk_psndoc in ("
				+ new InSQLCreator().getInSQL(pk_psndocs) + ") and begindate <= '" + endDate.toString()
				+ "' and isnull(enddate, '9999-12-31') >= '" + beginDate.toString() + "'");
		Map<String, UFLiteralDate> psnWorkStartDate = new HashMap<String, UFLiteralDate>();
		for (PsnOrgVO psnorg : psnorgs) {
			psnWorkStartDate.put(psnorg.getPk_psndoc(), (UFLiteralDate) psnorg.getAttributeValue("workagestartdate"));
		}
		return psnWorkStartDate;
	}

	public static String getLeaveBeginDate(String pk_org, UFLiteralDate settleDate, String pk_psndoc)
			throws BusinessException {
		String strSQL = "SELECT MAX(hi_psnjob.begindate) begindate "
				+ " FROM hi_psnjob "
				+ " inner join hi_psnorg on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg "
				+ " WHERE  hi_psnjob.pk_psndoc='"
				+ pk_psndoc
				+ "' AND '"
				+ settleDate
				+ "' BETWEEN hi_psnorg.begindate AND NVL(hi_psnorg.enddate, '9999-12-31') AND hi_psnjob.ismainjob='Y' AND hi_psnjob.pk_org = '"
				+ pk_org
				+ "' AND ( trnsevent = 4 OR  trnstype = '1001X110000000003O5G')"
				+ " and (select count(pk_psnjob) from hi_psnjob where pk_psnorg = hi_psnorg.pk_psndoc and trnstype = '1001X110000000003O5G') > (select count(pk_psnjob) from hi_psnjob where pk_psnorg = hi_psnorg.pk_psndoc and trnstype = '1001X110000000003O5I')";
		String leaveDate = (String) new BaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return leaveDate;
	}
}
