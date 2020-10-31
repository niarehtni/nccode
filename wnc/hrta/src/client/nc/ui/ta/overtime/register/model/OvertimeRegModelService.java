package nc.ui.ta.overtime.register.model;

import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.FromWhereSQLUtils;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.IOvertimeRegisterQueryMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.pub.TaBillRegQueryParams;
import nc.vo.ta.pub.TaNormalQueryUtils;
import nc.vo.uif2.LoginContext;

public class OvertimeRegModelService implements IAppModelService, IPaginationQueryService {

	private IOvertimeRegisterManageMaintain manageMaintain;
	private IOvertimeRegisterQueryMaintain queryMaintain;

	@Override
	public void delete(Object object) throws BusinessException {
		getManageMaintain().deleteData((OvertimeRegVO) object);
	}

	public void delete(OvertimeRegVO[] vos) throws Exception {
		getManageMaintain().deleteArrayData(vos);
	}

	@Override
	public Object insert(Object object) throws BusinessException {
		return getManageMaintain().insertData((OvertimeRegVO) object);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws BusinessException {
		return getQueryMaintain().queryByCond(context, null, null);
	}

	@Override
	public Object update(Object object) throws BusinessException {
		return getManageMaintain().updateData((OvertimeRegVO) object);
	}

	public Object[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object cond) throws BusinessException {
		return getQueryMaintain().queryByCond(context, fromWhereSQL, cond);
	}

	public IOvertimeRegisterManageMaintain getManageMaintain() {
		if (manageMaintain == null)
			manageMaintain = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class);
		return manageMaintain;
	}

	public IOvertimeRegisterQueryMaintain getQueryMaintain() {
		if (queryMaintain == null)
			queryMaintain = NCLocator.getInstance().lookup(IOvertimeRegisterQueryMaintain.class);
		return queryMaintain;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return getQueryMaintain().queryByPks(pks);
	}

	@SuppressWarnings("unchecked")
	public String[] queryPKsByFromWhereSQL(LoginContext context, FromWhereSQL fromWhereSQL, Object extraConds)
			throws BusinessException {
		String pks[] = null;

		// ssx added on 2020-07-25
		// 啟碁啟用產線權限子集控制領班查詢權限（部門及人員查詢範圍）
		int hasGlbdef8 = -1;
		IUAPQueryBS qry = NCLocator.getInstance().lookup(IUAPQueryBS.class);

		hasGlbdef8 = (int) qry.executeQuery(
				"select count(glbdef1) from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
						+ InvocationInfoProxy.getInstance().getUserId() + "')", new ColumnProcessor());

		if (hasGlbdef8 > 0) {
			String deptWherePart = "#DEPT_PK# in (select glbdef1 from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
					+ InvocationInfoProxy.getInstance().getUserId()
					+ "') and '"
					+ new UFLiteralDate().toString()
					+ "' between BEGINDATE and nvl(ENDDATE, '9999-12-31')) and (select count(pk_dept) from org_dept where pk_dept=#DEPT_PK# and isnull(HRCANCELED, 'N')='N') > 0";

			String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimeRegVO.getDefaultTableName());
			String periodSql = TaNormalQueryUtils.getPeriodSql(context, OvertimeRegVO.class, alias,
					(TaBillRegQueryParams) extraConds);

			List<String> filterPKs = (List<String>) qry.executeQuery(
					"select pk_overtimereg from " + fromWhereSQL.getFrom() + " where " + fromWhereSQL.getWhere()
							+ " and " + deptWherePart.replace("#DEPT_PK#", "T1.pk_dept")
							+ (periodSql == null ? "" : (" and " + periodSql)), new ColumnListProcessor());

			if (filterPKs == null || filterPKs.size() == 0) {
				pks = new String[0];
			} else {
				pks = filterPKs.toArray(new String[0]);
			}
		} else {
			pks = getQueryMaintain().queryPKsByFromWhereSQL(context, fromWhereSQL, extraConds);
		}
		// end

		return pks;
	}
}
