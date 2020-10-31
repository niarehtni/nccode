package nc.impl.ta.overtime;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.impl.pub.OTLeaveBalancePubServiceImpl;
import nc.itf.ta.overtime.IOTLeaveBalanceMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QueryCondition;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

import org.apache.commons.lang.StringUtils;

public class OTLeaveBalanceMaintainImpl extends OTLeaveBalancePubServiceImpl implements IOTLeaveBalanceMaintain {
	@SuppressWarnings("unchecked")
	@Override
	public OTLeaveBalanceVO[] query(IQueryScheme queryScheme, String pk_leavetypecopy, String queryYear,
			String beginDate, String endDate) throws BusinessException {
		OTLeaveBalanceVO[] bills = null;
		try {
			Map<String, QueryCondition> conds = (Map<String, QueryCondition>) queryScheme.get("all_condition");
			String pk_org = null;
			UFBoolean isfrozen = null;
			String[] pk_psndocs = null;
			String[] clerkcodes = null;
			String[] pk_depts = null;
			String maxdate = "";
			UFBoolean isTermLeave = null;
			if (conds != null && conds.size() > 0) {
				for (Entry<String, QueryCondition> cond : conds.entrySet()) {
					String strKey = cond.getKey();
					QueryCondition condition = cond.getValue();
					if (strKey.equals("pk_org")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							pk_org = condition.getValues()[0];
						}
					} else if (strKey.equals("isfrozen")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							isfrozen = new UFBoolean(condition.getValues()[0]);
						}
					} else if (strKey.equals("pk_psndoc")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							pk_psndocs = condition.getValues();
						}
					} else if (strKey.equals("pk_dept")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							pk_depts = condition.getValues();
						}
					} else if (strKey.equals("maxdate")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							maxdate = condition.getValues()[0];
						}
					} else if (strKey.equals("istermleave")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							isTermLeave = new UFBoolean(condition.getValues()[0]);
						}
					} else if (strKey.contains("clerkcode")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							clerkcodes = condition.getValues();
						}
					}
				}

				// ssx added on 2020-07-25
				// 啟碁啟用產線權限子集控制領班查詢權限（部門及人員查詢範圍）
				if ((pk_depts == null || pk_depts.length == 0) && (pk_psndocs == null || pk_psndocs.length == 0)) {
					int hasGlbdef8 = -1;
					IUAPQueryBS qry = NCLocator.getInstance().lookup(IUAPQueryBS.class);

					if (clerkcodes != null && clerkcodes.length > 0) {
						String codeInStr = "";
						for (String code : clerkcodes) {
							if (StringUtils.isEmpty(codeInStr)) {
								codeInStr = "'" + code + "'";
							} else {
								codeInStr += ",'" + code + "'";
							}
						}
						List<String> psnPKs = (List<String>) qry.executeQuery(
								"select distinct pk_psndoc from hi_psnjob where clerkcode in (" + codeInStr + ")",
								new ColumnListProcessor());

						if (psnPKs != null && psnPKs.size() > 0) {
							pk_psndocs = psnPKs.toArray(new String[0]);
						}
					}

					hasGlbdef8 = (int) qry.executeQuery(
							"select count(glbdef1) from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
									+ InvocationInfoProxy.getInstance().getUserId() + "')", new ColumnProcessor());

					if (hasGlbdef8 > 0) {
						String deptWherePart = "#DEPT_PK# in (select glbdef1 from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
								+ InvocationInfoProxy.getInstance().getUserId()
								+ "') and '"
								+ new UFLiteralDate().toString()
								+ "' between BEGINDATE and nvl(ENDDATE, '9999-12-31')) and (select count(pk_dept) from org_dept where pk_dept=#DEPT_PK# and isnull(HRCANCELED, 'N')='N') > 0";

						String strSQL = "select pk_dept from org_dept where "
								+ deptWherePart.replace("#DEPT_PK#", "pk_dept");

						List<String> pks = (List<String>) qry.executeQuery(strSQL, new ColumnListProcessor());
						if (pks != null && pks.size() > 0) {
							pk_depts = pks.toArray(new String[0]);
						} else {
							pk_depts = new String[] { "~" };
						}
					}
				}

				bills = queryOTLeaveAggvos(pk_org, pk_psndocs, pk_depts, isTermLeave, pk_leavetypecopy, maxdate,
						queryYear, beginDate, endDate);
			}
		} catch (Exception e) {
			ExceptionUtils.wrappException(e);
		}
		return bills;
	}
}
