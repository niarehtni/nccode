package nc.impl.om.pannavi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.managescope.ManageScopeConst;
import nc.itf.hr.org.HrOrgQueryUtil;
import nc.itf.om.IAOSQueryService;
import nc.itf.om.IDeptAdjustService;
import nc.itf.om.IDeptQueryService;
import nc.itf.om.INaviQueryService;
import nc.itf.org.IOrgUnitQryService;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.org.AdminOrgVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVOUtil;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.uif2.LoginContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @since V6.0 2011-3-25 09:50:21
 */
public class NaviQueryImpl implements INaviQueryService {
	private IOrgUnitQryService orgUnitQryService;

	private IDeptQueryService deptQueryService;

	/**************************************************************
	 * <br>
	 * Created on 2011-12-6 17:26:00<br>
	 * 
	 * @return IDeptQueryService
	 **************************************************************/
	public IDeptQueryService getDeptQueryService() {
		if (deptQueryService == null) {
			deptQueryService = NCLocator.getInstance().lookup(IDeptQueryService.class);
		}

		return deptQueryService;
	}

	/**************************************************************
	 * <br>
	 * Created on 2011-12-6 17:26:02<br>
	 * 
	 * @return IOrgUnitQryService
	 **************************************************************/
	public IOrgUnitQryService getOrgUnitQryService() {
		if (orgUnitQryService == null) {
			orgUnitQryService = NCLocator.getInstance().lookup(IOrgUnitQryService.class);
		}

		return orgUnitQryService;
	}

	/**************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2011-12-6 17:26:06<br>
	 * 
	 * @see nc.itf.om.INaviQueryService#queryNextLayer(nc.vo.uif2.LoginContext,
	 *      java.lang.Object, boolean, boolean,
	 *      nc.vo.hr.managescope.ManagescopeBusiregionEnum)
	 **************************************************************/
	@Override
	public Object[] queryNextLayer(LoginContext context, Object currentValue, boolean includeCancleDept,
			boolean isIncludeDummyDept, ManagescopeBusiregionEnum busiregionEnum) throws BusinessException {
		String pk_hrorg = context.getPk_org();
		String attr = busiregionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;

		if (currentValue instanceof OrgVO) {
			OrgVO orgVO = (OrgVO) currentValue;

			if (StringUtils.isEmpty(orgVO.getInnercode())) {
				// 如果Innercode为空，说明是假树节点！如：“其他人员”
				return null;
			}

			// 人力资源组织为所选人力组织， 上级组织为所选组织
			String orgsql1 = "(pk_hrorg='" + pk_hrorg + "' and pk_fatherorg='" + orgVO.getPk_org() + "'"
					+ " and business_type='" + busiregionEnum.getId() + "')";

			// 人力资源组织为所选人力组织， 上级组织为空 只在根节点使用
			String orgsql2 = "(pk_hrorg='" + pk_hrorg + "' and pk_fatherorg='~'" + " and business_type='"
					+ busiregionEnum.getId() + "')";

			// 人力资源组织为所选人力组织 并且 上级组织不为空 并且 上级组织的人力资源组织不是所选人力组织 （只在根节点使用）
			String orgsql3 = "(pk_hrorg='"
					+ pk_hrorg
					+ "' and pk_fatherorg<>'~' and business_type='"
					+ busiregionEnum.getId()
					+ "'"
					+ " and not exists (select 1 from hr_relation_org oo where oo.pk_org=ro.pk_fatherorg and oo.pk_hrorg='"
					+ pk_hrorg + "' and oo.business_type='" + busiregionEnum.getId() + "'))";

			// 人力资源组织为所选人力组织， 上级组织为所选组织 上级部门为空
			String deptsql1 = "(pk_hrorg='" + pk_hrorg + "' and pk_deptorg='" + orgVO.getPk_org()
					+ "' and pk_fatherdept='~' and " + attr + "='Y')";

			// 人力资源组织为所选人力组织， 上级组织为所选组织 上级部门不为空且上级部门不归该人力组织管理
			String deptsql2 = "(pk_hrorg='"
					+ pk_hrorg
					+ "' and pk_deptorg='"
					+ orgVO.getPk_org()
					+ "' and pk_fatherdept<>'~' and "
					+ attr
					+ "='Y' and not exists (select 1 from hr_relation_dept dd where dd.pk_dept=rd.pk_fatherdept and dd.pk_hrorg='"
					+ pk_hrorg + "' and dd." + attr + "='Y'))";

			String orgRelationClause = null;
			if (pk_hrorg.equals(orgVO.getPk_org())) {
				orgRelationClause = "(" + orgsql1 + " or " + orgsql2 + " or " + orgsql3 + ")";
			} else {
				orgRelationClause = orgsql1;
			}

			// 下层组织不包括当前节点选择的组织和所操作的主组织
			String orgWhereClause = " org_adminorg.pk_adminorg<>'" + orgVO.getPk_org()
					+ "' and org_adminorg.pk_adminorg<>'" + pk_hrorg
					+ "' and exists (select 1 from hr_relation_org ro where org_adminorg.pk_adminorg=ro.pk_org and "
					+ orgRelationClause + ")";

			// OrgVO[] orgVOs =
			// getOrgUnitQryService().queryAllOrgUnitVOSByGroupIDAndClause(context.getPk_group(),
			// orgWhereClause);
			// 增加过滤条件：启用HR业务
			String condition = OrgVO.PK_GROUP + "='" + context.getPk_group() + "'and org_adminorg.enablestate="
					+ IPubEnumConst.ENABLESTATE_ENABLE
					+ " and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable) and "
					+ orgWhereClause;

			Collection<OrgVO> c = new BaseDAO().retrieveByClause(AdminOrgVO.class, condition, AdminOrgVO.CODE);

			AdminOrgVO adminOrgVOs[] = c == null || c.size() == 0 ? new AdminOrgVO[0] : c.toArray(new AdminOrgVO[c
					.size()]);

			OrgVO[] orgVOs = new OrgVO[adminOrgVOs.length];
			for (int i = 0; i < adminOrgVOs.length; i++) {
				orgVOs[i] = new OrgVO();

				try {
					BeanUtils.copyProperties(orgVOs[i], adminOrgVOs[i]);

					orgVOs[i].setPk_org(adminOrgVOs[i].getPk_adminorg());
				} catch (Exception ex) {
					Logger.error(ex.getMessage(), ex);
				}
			}

			HRDeptVO[] deptVOs = new HRDeptVO[0];

			String deptRelationClause = "(" + deptsql1 + " or " + deptsql2 + ")";
			String deptWhereClause = " exists (select 1 from hr_relation_dept rd where org_dept.pk_dept=rd.pk_dept"
					+ " and " + deptRelationClause + ")";

			if (!includeCancleDept) {
				deptWhereClause += " and " + DeptVO.HRCANCELED + "='" + UFBoolean.FALSE.toString() + "'";
			}

			if (!isIncludeDummyDept) {
				deptWhereClause += " and depttype=0 ";
			}

			deptWhereClause += " and " + DeptVO.ENABLESTATE + "<>" + IPubEnumConst.ENABLESTATE_DISABLE;

			AggHRDeptVO[] aggHrDeptVO = getDeptQueryService().queryByCondition(context, deptWhereClause);

			if (aggHrDeptVO != null) {
				deptVOs = new HRDeptVO[aggHrDeptVO.length];
				for (int i = 0; i < aggHrDeptVO.length; i++) {
					deptVOs[i] = (HRDeptVO) aggHrDeptVO[i].getParentVO();
				}
			}
			SuperVOUtil.sortByAttributeName(orgVOs, OrgVO.CODE, true);
			Object[] returnObjects = new Object[orgVOs.length + deptVOs.length];

			System.arraycopy(deptVOs, 0, returnObjects, 0, deptVOs.length);
			System.arraycopy(orgVOs, 0, returnObjects, deptVOs.length, orgVOs.length);

			return returnObjects;
		} else if (currentValue instanceof HRDeptVO) {
			HRDeptVO deptVO = (HRDeptVO) currentValue;

			// 人力资源组织为所选人力组织， 上级部门为其
			String deptsql5 = " pk_hrorg='" + pk_hrorg + "' and pk_fatherdept='" + deptVO.getPk_dept() + "' and "
					+ attr + "='Y'";

			HRDeptVO[] deptVOs = null;

			String deptRelationClause = deptsql5;
			String deptWhereClause = " pk_org<>'" + deptVO.getPk_dept()
					+ "' and exists (select 1 from hr_relation_dept where org_dept.pk_dept=pk_dept and "
					+ deptRelationClause + ")";

			if (!includeCancleDept) {
				deptWhereClause += " and " + DeptVO.HRCANCELED + "='" + UFBoolean.FALSE.toString() + "'";
			}

			deptWhereClause += " and " + DeptVO.ENABLESTATE + "<>" + IPubEnumConst.ENABLESTATE_DISABLE;

			AggHRDeptVO[] aggHrDeptVO = getDeptQueryService().queryByCondition(context, deptWhereClause);

			if (aggHrDeptVO != null) {
				deptVOs = new HRDeptVO[aggHrDeptVO.length];
				for (int i = 0; i < aggHrDeptVO.length; i++) {
					deptVOs[i] = (HRDeptVO) aggHrDeptVO[i].getParentVO();
				}
			}

			return deptVOs;
		}

		return null;
	}

	/**************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2011-12-6 17:26:33<br>
	 * 
	 * @see nc.itf.om.INaviQueryService#queryOrgAndDeptByOrgPK(nc.vo.uif2.LoginContext,
	 *      java.lang.String, boolean, boolean, boolean)
	 **************************************************************/
	@Override
	public Object[] queryOrgAndDeptByOrgPK(boolean isSeqControl, LoginContext context, String pk_org, String topOrg,
			boolean includeChildHR, boolean includeCancleDept, boolean isIncludeDummyDept) throws BusinessException {
		if (pk_org == null) {
			return null;
		}

		List<Object> myList = new ArrayList<Object>();

		StringBuilder querySQL = new StringBuilder();

		if (!includeCancleDept) {
			querySQL.append(" " + DeptVO.HRCANCELED + "='" + UFBoolean.FALSE.toString() + "' and ");
		}

		if (!isIncludeDummyDept) {
			querySQL.append(" depttype=0 and ");
		}

		// modified by zengcheng 2010.08.26,按需求要求，永远不显示停用部门
		querySQL.append(" pk_org='" + pk_org + "' and " + DeptVO.ENABLESTATE + "=" + IPubEnumConst.ENABLESTATE_ENABLE
				+ " and (pk_fatherorg='~' or pk_fatherorg='')");

		AggHRDeptVO[] deptVOs = NCLocator.getInstance().lookup(IDeptQueryService.class)
				.queryByCondition(context, querySQL.toString());

		if (deptVOs != null) {
			for (AggHRDeptVO deptVO : deptVOs) {
				myList.add(deptVO.getParentVO());
			}
		}
		OrgVO[] aosVOs = null;
		if (!isSeqControl) {
			aosVOs = NCLocator.getInstance().lookup(IAOSQueryService.class)
					.getFirstChildHROrgsByOrgPK(pk_org, includeChildHR);
		}
		{
			aosVOs = NCLocator.getInstance().lookup(IAOSQueryService.class)
					.getFirstChildHROrgsByOrgPKSeqControl(topOrg, pk_org, includeChildHR);
		}
		// 增加过滤条件：启用HR业务
		aosVOs = HrOrgQueryUtil.filterOrgPKSet(aosVOs);

		SuperVOUtil.sortByAttributeName(aosVOs, OrgVO.CODE, true);

		if (aosVOs != null) {
			for (OrgVO aos : aosVOs) {
				myList.add(aos);
			}
		}

		if (myList.isEmpty()) {
			return null;
		}

		return myList.toArray(new Object[myList.size()]);
	}

	@Override
	public Object[] queryAOSMembersByHROrgPK(String pk_org, boolean includeChildHR, boolean includeCancleDept,
			boolean includeDummyDept, String orgCondition, String deptCondition, boolean isSeqControl)
			throws BusinessException {

		// 查询行政组织
		String orgInsql = "";

		String gkSql = isSeqControl ? NCLocator.getInstance().lookup(IPsndocService.class)
				.queryControlSql("@@@@Z710000000006M2K", pk_org, true) : "";

		if (includeChildHR && !StringUtils.isEmpty(gkSql) && isSeqControl) {
			orgInsql += gkSql;
		} else {
			orgInsql = AOSSQLHelper.getChildrenBUInSQLByHROrgPK(pk_org);
		}

		// 逐级管控（员工信息查询——左侧机构树 注：将原来是否包含下级HR组织的判断去掉，默认包含）
		// orgInsql = AOSSQLHelper.getAllBUInSQLByHROrgPK(pk_org);

		String adminCondition = " pk_adminorg in (" + orgInsql + ")";
		if (StringUtils.isNotEmpty(orgCondition)) {
			adminCondition += " and " + orgCondition;
		}
		adminCondition += " order by code ";
		AdminOrgVO[] adminOrg = (AdminOrgVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, AdminOrgVO.class, adminCondition);

		List<OrgVO> listOrgVO = new ArrayList<OrgVO>();

		for (int i = 0; adminOrg != null && i < adminOrg.length; i++) {
			OrgVO orgVO = new OrgVO();

			try {
				BeanUtils.copyProperties(orgVO, adminOrg[i]);

				orgVO.setPk_org(adminOrg[i].getPk_adminorg());

				listOrgVO.add(orgVO);
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
			}
		}
		UFDate curdate = new UFDate(new Date());
		// 查询部门
		String deptWhere = " pk_org in (" + orgInsql + ") and enablestate = 2  and createdate < '" + curdate + "'";
		// String deptWhere = " pk_org in (" + orgInsql +
		// ") and enablestate = 2  ";
		if (!includeCancleDept) {
			deptWhere += " and hrcanceled ='N' ";
		}

		if (!includeDummyDept) {
			deptWhere += " and depttype=0  ";
		}
		if (StringUtils.isNotEmpty(deptCondition)) {
			deptWhere += " and " + deptCondition;
		}
		deptWhere += " order by displayorder,code ";
		HRDeptVO[] dept = (HRDeptVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, HRDeptVO.class, deptWhere);

		List<Object> myList = new ArrayList<Object>();

		for (int i = 0; dept != null && i < dept.length; i++) {
			myList.add(dept[i]);
		}

		if (listOrgVO.size() > 0) {
			myList.addAll(listOrgVO);
		}

		if (myList.isEmpty()) {
			return null;
		}

		return myList.toArray(new Object[myList.size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] queryMSMembersByHROrgPK(String pk_org, boolean includeCancleDept, boolean includeDummyDept,
			ManagescopeBusiregionEnum busiRegionEnum) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<Object> myList = new ArrayList<Object>();
		String gkSql = NCLocator.getInstance().lookup(IPsndocService.class)
				.queryControlSql("@@@@Z710000000006M2K", pk_org, true);
		// 管理范围树暂时不受逐级管控影响，以后也许会放开
		gkSql = null;
		if (StringUtils.isEmpty(gkSql)) {
			gkSql = "'" + pk_org + "'";
		}

		// 查询组织
		String orgSqlnew = " select o.code,o.name,o.name2,o.name3,o.name4,o.name5,o.name6,o.pk_fatherorg,o.pk_group,o.pk_org,o.pk_adminorg from hr_relation_org ro inner join org_adminorg o on ro.pk_org = o.pk_adminorg  where ro.pk_hrorg in ("
				+ gkSql
				+ ") and ro.business_type='"
				+ busiRegionEnum.getId()
				+ "' and o.enablestate = "
				+ IPubEnumConst.ENABLESTATE_ENABLE
				+ " and exists (select 1 from org_admin_enable where org_admin_enable.pk_adminorg = o.pk_adminorg) order by o.code ";

		List<Object> listOrgVO = (List<Object>) dao.executeQuery(orgSqlnew, new BaseProcessor() {
			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				List<OrgVO> result = new ArrayList<OrgVO>();
				while (rs.next()) {
					OrgVO vo = new OrgVO();
					result.add(vo);
					vo.setCode(rs.getString("code"));
					vo.setName(rs.getString("name"));
					vo.setName2(rs.getString("name2"));
					vo.setName3(rs.getString("name3"));
					vo.setName4(rs.getString("name4"));
					vo.setName5(rs.getString("name5"));
					vo.setName6(rs.getString("name6"));
					vo.setPk_fatherorg(rs.getString("pk_fatherorg"));
					vo.setPk_group(rs.getString("pk_group"));
					vo.setPk_org(rs.getString("pk_adminorg"));
				}
				return result;
			}
		});

		// 查询部门
		String attr = busiRegionEnum.getCode() + ManageScopeConst.PROPERTY_BUSI;
		String deptSql = "select d.code,d.name,d.name2,d.name3,d.name4,d.name5,d.name6,d.pk_fatherorg,d.pk_group,d.pk_org,d.pk_dept from hr_relation_dept rd  inner join org_dept d on rd.pk_dept = d.pk_dept "
				+ "left join hr_relation_dept ird  on rd.pk_fatherdept = ird.pk_dept and ird.pk_hrorg in ("
				+ gkSql
				+ ") and ird."
				+ attr
				+ " ='Y' "
				+ " where rd.pk_hrorg in ("
				+ gkSql
				+ ") and rd."
				+ attr
				+ " ='Y' and exists (select 1 from org_admin_enable where org_admin_enable.pk_adminorg = d.pk_org) ";
		if (!includeCancleDept) {
			deptSql += " and d.hrcanceled ='N' ";
		}

		if (!includeDummyDept) {
			deptSql += " and d.depttype = 0  ";
		}
		deptSql += " and d.enablestate = 2 order by displayorder,code ";

		List<Object> deptList = (List<Object>) dao.executeQuery(deptSql, new BaseProcessor() {
			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				List<HRDeptVO> result = new ArrayList<HRDeptVO>();
				while (rs.next()) {
					HRDeptVO vo = new HRDeptVO();
					result.add(vo);
					vo.setCode(rs.getString("code"));
					vo.setName(rs.getString("name"));
					vo.setName2(rs.getString("name2"));
					vo.setName3(rs.getString("name3"));
					vo.setName4(rs.getString("name4"));
					vo.setName5(rs.getString("name5"));
					vo.setName6(rs.getString("name6"));
					vo.setPk_fatherorg(rs.getString("pk_fatherorg"));
					vo.setPk_group(rs.getString("pk_group"));
					vo.setPk_org(rs.getString("pk_org"));
					vo.setPk_dept(rs.getString("pk_dept"));
				}
				return result;
			}
		});

		myList.addAll(deptList);

		myList.addAll(listOrgVO);

		if (myList.isEmpty()) {
			return null;
		}

		return myList.toArray(new Object[myList.size()]);
	}

	@Override
	public Object[] queryAOSMembersCascadeByHROrgPK(String pk_org, boolean includeChildHR, boolean includeCancleDept,
			boolean showHRFuturedFlag, boolean includeDummyDept, String orgCondition, String deptCondition)
			throws BusinessException {

		boolean flag = false;

		// 先查询出当前行政组织及其下级组织
		Object[] objs = queryAOSMembersByHROrgPK(pk_org, includeChildHR, includeCancleDept, includeDummyDept,
				orgCondition, deptCondition, false);
		ArrayList<Object> myList = new ArrayList<Object>();
		// 查询出未来部门

		if (showHRFuturedFlag) {
			List<HRDeptVO> deptvolist = NCLocator.getInstance().lookup(IDeptAdjustService.class)
					.queryOFutureDept(pk_org, deptCondition);
			if (deptvolist.size() > 0) {
				for (HRDeptVO deptvo : deptvolist) {
					myList.add(deptvo);
				}
			}
		}

		for (int i = 0; objs != null && i < objs.length; i++) {
			myList.add(objs[i]);
		}

		// 查询部门和其上级部门主键
		ArrayList<String> pkdept = new ArrayList<String>();
		ArrayList<String> pkfatherorg = new ArrayList<String>();

		do {
			for (Object vo : myList) {
				if (vo instanceof HRDeptVO) {
					// MOD by ssx for optimization on 2019-06-14
					if (!pkdept.contains(((HRDeptVO) vo).getPk_dept())) {
						pkdept.add(((HRDeptVO) vo).getPk_dept());
					}

					if (((HRDeptVO) vo).getPk_fatherorg() != null) {
						if (!pkfatherorg.contains(((HRDeptVO) vo).getPk_fatherorg())) {
							pkfatherorg.add(((HRDeptVO) vo).getPk_fatherorg());
						}
					}
					// end
				}
			}

			// 从上级部门集合中除去当前部门，获得未查询的上级部门主键
			pkfatherorg.removeAll(pkdept);

			// 当有未查询的上级部门时，则重新查询上级部门以获得完成的树结构
			if (pkfatherorg.size() > 0) {
				flag = true;
				String insql = new InSQLCreator().getInSQL(pkfatherorg.toArray(new String[0]), true);
				String deptWhere = " pk_dept in ( " + insql + " ) ";

				// 查询部门
				HRDeptVO[] dept = (HRDeptVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
						.retrieveByClause(null, HRDeptVO.class, deptWhere);
				for (int i = 0; dept != null && i < dept.length; i++) {
					myList.add(dept[i]);
				}
			} else
				flag = false;
		} while (flag);

		return myList.toArray(new Object[myList.size()]);
	}
}
