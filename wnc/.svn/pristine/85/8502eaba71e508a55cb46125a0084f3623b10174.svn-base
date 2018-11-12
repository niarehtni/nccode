package nc.ui.om.ref;

import nc.hr.utils.ResHelper;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.om.hrdept.HRDeptVO;

public class HRDeptRefModel extends AbstractRefTreeModel {

	/** HR组织在行政体系中的内部码 */
	private String innerCode = null;

	/** 人力资源组织主键 */
	private String pk_hrorg = null;

	private boolean isShowAll = false;

	private boolean isShowDisbleOrg = false;

	/**
	 * 默认构造函数，使用主组织作为查询条件<br>
	 */
	public HRDeptRefModel() {
		initModel();
	}

	/**
	 * 带参构造函数，查询HR组织下的所有部门<br>
	 * 
	 * @param pk_hrorg
	 */
	public HRDeptRefModel(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
		initModel();
	}

	private void initModel() {
		setRefNodeName("部门HR"); /* -=notranslate=- */
		setRefTitle(ResHelper.getString("common", "UC000-0004068")/* @res "部门参照" */);
		setPkFieldCode("org_dept.pk_dept");

		// setRefCodeField("code");
		// setRefNameField("name");
		setFieldCode(new String[] { "org_dept.code", "org_dept.name", HiSQLHelper.getLangNameColume("org_orgs.name") });
		setFieldName(new String[] { ResHelper.getString("common", "UC000-0004073")/*
																				 * @
																				 * res
																				 * "部门编码"
																				 */,
				ResHelper.getString("common", "UC000-0004069")/*
															 * @res "部门名称"
															 */, ResHelper.getString("6001ref", "06001ref0009") });

		setHiddenFieldCode(new String[] { "org_dept.pk_dept", "org_dept.pk_fatherorg", "org_dept.pk_group",
				"org_dept.pk_org", "org_dept.hrcanceled", "org_dept.innercode", "org_dept.principal",
				"org_dept.createdate", "org_dept." + HRDeptVO.DISPLAYORDER, "org_orgs.code as org_code",
				"org_orgs.name as org_name" });

		setFatherField("org_dept.pk_fatherorg");
		setChildField("org_dept.pk_dept");
		setOrderPart("org_code," + "org_dept." + HRDeptVO.DISPLAYORDER + ",org_dept." + HRDeptVO.CODE);
		StringBuilder table = new StringBuilder();
		table.append(" org_dept ");
		table.append(" inner join org_orgs on org_orgs.pk_org = org_dept.pk_org ");
		// table.append(" inner join org_adminorg on org_dept.pk_org = org_adminorg.pk_adminorg ");
		table.append(" inner join org_adminorg on org_dept.pk_org = org_adminorg.pk_adminorg and (org_adminorg.enablestate= 2 or org_adminorg.enablestate= 1) ");
		// table.append(" inner join org_adminorg on org_dept.pk_org = org_adminorg.pk_adminorg and org_adminorg.enablestate= IPubEnumConst.ENABLESTATE_ENABLE");
		setTableName(table.toString());
		setDefaultFieldCount(3);
		setFilterRefNodeName(new String[] { "HR组织下属组织" });/* -=notranslate=- */
		setClassMutilLangNameRef(false);
		// setMutilLangNameRef(false);
		setCacheEnabled(false);
	}

	@Override
	public void filterValueChanged(ValueChangedEvent changedValue) {
		super.filterValueChanged(changedValue);
		String[] pk_orgs = (String[]) changedValue.getNewValue();
		if (pk_orgs != null && pk_orgs.length > 0) {
			setPk_org(pk_orgs[0]);
		}
		setShowAll(true);
		setWherePart("org_dept.pk_org = '" + pk_orgs[0] + "'");

	}

	protected String getEnvWherePart() {
		if (isShowAll()) {
			return " org_dept.pk_group = '" + getPk_group() + "' ";
		}

		StringBuilder querySQL = new StringBuilder();
		if (null == pk_hrorg) {
			querySQL.append("org_dept.pk_org = '" + getPk_org() + "' ");
			if (!isShowDisbleOrg()) {
				querySQL.append(" and org_dept.pk_org in (select pk_adminorg from org_admin_enable) ");
			}
			if (!isDisabledDataShow()) {
				querySQL.append(" and (org_dept.enablestate = 2 or org_dept.enablestate = 1) and org_dept.hrcanceled = 'N' ");
			}
		} else {
			// querySQL.append(" org_dept.pk_org in ( ");
			// querySQL.append(" 	select distinct pk_deptorg from hr_relation_dept ");
			// querySQL.append(" 	where pk_hrorg = '" + pk_hrorg + "') ");

			// if (null == innerCode) {
			// pk_hrorg 会发生变化 ,所以innercode也要变
			innerCode = nc.vo.om.pub.SQLHelper.getOrgInnercode(pk_hrorg);
			// }
			querySQL.append(" org_adminorg.innercode like " + "'" + innerCode + "%'");
			if (!isDisabledDataShow()) {
				querySQL.append(" and (org_dept.enablestate = 2 or org_dept.enablestate = 1) ");
			}
			querySQL.append("and (org_orgs.orgtype4 = 'N' or org_adminorg.innercode = '" + innerCode + "') ");
			querySQL.append("and org_adminorg.pk_adminorg not in ( ");
			querySQL.append("select ");
			querySQL.append("aosm.pk_adminorg ");
			querySQL.append("from ");
			querySQL.append("( select ");
			querySQL.append("aos.code, aos.innercode, len(aos.innercode) as innercodelen ");
			querySQL.append("from ");
			querySQL.append("org_orgs org inner join org_adminorg aos on org.pk_org = aos.pk_adminorg ");
			querySQL.append("where ");
			querySQL.append("aos.innercode like '" + innerCode + "%' and aos.innercode <> '" + innerCode
					+ "' and orgtype4 = 'Y' ) ");
			querySQL.append(" sub_hrorg, org_adminorg aosm ");
			querySQL.append("where ");
			querySQL.append("sub_hrorg.innercode = substring(aosm. innercode , 1, sub_hrorg.innercodelen)) ");
			if (!isShowDisbleOrg()) {
				querySQL.append(" and org_adminorg.pk_adminorg in (select pk_adminorg from org_admin_enable) ");
			}
		}

		return querySQL.toString();
	}

	protected String getDisableDataWherePart(boolean isDisableDataShow) {
		if (isDisableDataShow) {
			return "(org_dept.enablestate = 2 or org_dept.enablestate = 3)";
		} else {
			return "org_dept.enablestate = 2";
		}
	}

	public String getPk_hrorg() {
		return pk_hrorg;
	}

	public void setPk_hrorg(String pkHrorg) {
		pk_hrorg = pkHrorg;
	}

	public void setShowAll(boolean isShowAll) {
		this.isShowAll = isShowAll;
	}

	public boolean isShowAll() {
		return isShowAll;
	}

	public void setShowDisbleOrg(boolean isShowDisbleOrg) {
		this.isShowDisbleOrg = isShowDisbleOrg;
		if (isShowDisbleOrg) {
			setFilterRefNodeName(new String[] { "行政组织" });/* -=notranslate=- */
		}
	}

	public boolean isShowDisbleOrg() {
		return isShowDisbleOrg;
	}

}
