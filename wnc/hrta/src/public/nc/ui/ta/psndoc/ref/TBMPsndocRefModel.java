package nc.ui.ta.psndoc.ref;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.om.IAOSQueryService;
import nc.itf.om.IAOSQueryService.OrgQueryMode;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.ui.bd.ref.IRefConst;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.logging.Debug;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;

import org.apache.commons.lang.ArrayUtils;

public class TBMPsndocRefModel extends AbstractRefGridTreeModel {
	// 上个期间的开始日期
	protected UFLiteralDate lastPeriodBeginDate = null;
	private String strOldPk_org = getPk_org();

	// 用于在参照中取组织版本和部门版本值的字段。
	public static final String PK_ORG_V = "org_adminorg.PK_VID as pk_org_v";
	public static final String PK_DEPT_V = "org_dept.pk_vid as pk_dept_v";

	public TBMPsndocRefModel() {
		reset();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vector getQueryResultVO() {
		Vector vectData = super.getQueryResultVO();
		if (vectData == null)
			return null;
		int endDateIndex = getFieldIndex("tbm_psndoc.enddate");
		for (int j = 0; j < vectData.size(); j++) {
			Vector vectRow = (Vector) vectData.get(j);
			// 如果结束日期为9999-12-01，那么不显示
			if (TBMPsndocCommonValue.END_DATA.equals(vectRow.get(endDateIndex))) {
				vectRow.set(endDateIndex, null);
			}
		}
		return vectData;
	}

	/*
	 */
	private String getBasicWherePart() {
		lastPeriodBeginDate = retrievePeriodDate();
		// return new StringBuilder(
		// "tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where  tbm_psndoc.pk_org = '")
		// .append(getPk_org()).append("' and (tbm_psndoc.enddate like '9999%' or tbm_psndoc.enddate > '").append(
		// lastPeriodBeginDate).append("'))").toString();

		// modified by zengcheng 20120723,原来显示多条考勤档案记录，改为只显示最新一条
		return new StringBuilder(
		// "tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where  tbm_psndoc.pk_org = '")
		// .append(getPk_org()).append("' and (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2 where psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) and tbm_psndoc.enddate > '").append(
		// lastPeriodBeginDate).append("'))").toString();

				// 上面的语句有缺陷，如果一个人员在a公司有一条结束的考勤档案，该人员被调配到b公司后 在停工待料等节点新增时候
				// 在a公司下查不到该人员的结束的考勤档案 modify by zhouyuh 8-30
				"tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where   (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2"
						+ " where psndoc2.pk_org = '").append(getPk_org())
				.append("' and psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) and tbm_psndoc.enddate > '")
				.append(lastPeriodBeginDate).append("'))").toString();
	}

	/**
	 * 是否包含下级HR组织
	 */
	protected boolean isIncludeHrBranch() {
		return false;
	}

	@Override
	public void reset() {
		setRefTitle(ResHelper.getString("common", "UC000-0000129")
		/* @res "人员" */);
		setRootName(PublicLangRes.ORGDEPT());
		setClassFieldCode(new String[] { "code", "name", "pk_org", "pk_orgdept", "pk_children", "pk_father",
				"display_order", "pk_group" });
		setClassFatherField("pk_father");
		setClassChildField("pk_children");
		setClassJoinField("pk_orgdept");
		// String otherPsn =
		// ResHelper.getString("6017basedoc","06017basedoc1689")
		// /*@res "其他人员"*/;
		// 如果没有选中组织则不处理
		String currOrg = getPk_org();
		String orgInSQL = currOrg.equals(PubEnv.getPk_group()) ? "'noQuery'" : AOSSQLHelper
				.getChildrenBUInSQLByHROrgPK(currOrg);
		StringBuilder classTableName = new StringBuilder();
		classTableName
				.append(" (	select org_adminorg.code, org_adminorg.name,org_adminorg.name2,org_adminorg.name3,org_adminorg.name4,org_adminorg.name5,org_adminorg.name6, org_adminorg.pk_adminorg pk_org, org_adminorg.pk_adminorg pk_orgdept, org_adminorg.pk_group, org_adminorg.pk_adminorg pk_children, ")
				.append(" org_adminorg.pk_fatherorg pk_father, " + /* "org_adminorg.displayorder  display_order" */""
						+ " org_adminorg.code display_order ")
				.append(" from org_adminorg  where pk_adminorg in (" + orgInSQL + ")")
				.append(" union ")
				.append(" select org_dept.code, org_dept.name,org_dept.name2,org_dept.name3,org_dept.name4,org_dept.name5,org_dept.name6, org_dept.pk_org pk_org,org_dept.pk_dept pk_orgdept, org_adminorg.pk_group, org_dept.pk_dept pk_children, ")
				.append(" case when org_dept.pk_fatherorg='~' then org_adminorg.pk_adminorg else org_dept.pk_fatherorg end pk_father,  cast (org_dept.displayorder as char) display_order ")// 此段中有将int型的org_dept.displayorder
																																															// 转换成char类型
				.append(" from org_dept , org_adminorg  where org_dept.pk_org in (" + orgInSQL
						+ ") and org_dept.pk_org = org_adminorg.pk_adminorg and hrcanceled = 'N'")
				// zengcheng 20120421注释掉下面两行，从V61开始，时间管理没有“其他人员”了
				// .append(" union select '', '"+otherPsn+"',pk_group, '" +
				// HICommonValue.PK_OTHRERPSN_DEPT + "', ")
				// .append(" pk_group, pk_group, '',  cast (9999999 as char)  from org_group where pk_group = '"
				// + getPk_group() + "' ")
				.append(") orgdept ");// 次段中有将int型的99999 转换成char类型
		setClassTableName(classTableName.toString());

		setClassDefaultFieldCount(getClassDefaultFieldCount());
		// 主组织可控制组织范围
		// if (isIncludeHrBranch()) {
		// setClassWherePart(" ( pk_org in (" +
		// AOSSQLHelper.getAllBUInSQLByHROrgPK(getPk_org()) + ") or pk_org ='"
		// + getPk_group() + "') ");
		// } else {
		// setClassWherePart(" ( pk_org in (" +
		// AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getPk_org())
		// + ") or pk_org ='" + getPk_group() + "' ) ");
		// }

		setClassOrderPart("display_order,code");

		setFieldCode(new String[] { "bd_psndoc.code", "bd_psndoc.name", "clerkcode", "tbm_psndoc.timecardid",
				"tbm_psndoc.begindate", "tbm_psndoc.enddate", "org_adminorg.name", "org_dept.name" });
		setFieldName(new String[] { ResHelper.getString("common", "UC000-0000147")
		/* @res "人员编码" */, ResHelper.getString("common", "UC000-0001403")
		/* @res "姓名" */, PublicLangRes.EMPNO(), PublicLangRes.TBMCARDNO(),
				ResHelper.getString("6017basedoc", "06017basedoc1690")
				/* @res "考勤开始日期" */, ResHelper.getString("6017basedoc", "06017basedoc1691")
				/* @res "考勤结束日期" */, PublicLangRes.ORG(), PublicLangRes.DEPT() });

		setHiddenFieldCode(new String[] { "bd_psndoc.pk_group", "bd_psndoc.pk_org", "bd_psndoc.pk_psndoc",
				"hi_psnjob.pk_psnorg", "hi_psnjob.pk_psnjob", "hi_psnjob.pk_psncl", "hi_psnjob.pk_org",
				"hi_psnjob.pk_dept", "hi_psnjob.pk_job", "hi_psnjob.pk_post", PK_ORG_V, PK_DEPT_V });

		setTableName("  tbm_psndoc inner join hi_psnjob on tbm_psndoc.pk_psnjob = hi_psnjob.pk_psnjob  "
				+ "inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg "
				+ "inner join bd_psndoc on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc "
				+
				// "left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org "
				// +
				"left outer join org_adminorg on org_adminorg.pk_adminorg = hi_psnjob.pk_org "
				+ "left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept "
				+ "left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		setRefNodeName("考勤档案"); /* -=notranslate=- */
		setPkFieldCode("hi_psnjob.pk_psnjob");
		setRefCodeField("clerkcode");
		setRefNameField("bd_psndoc.name");
		setDocJoinField("hi_psnjob.pk_org||hi_psnjob.pk_dept");
		setExactOn(false);
		setWherePart(getBasicWherePart());
		resetFieldName();
	}

	protected void addJoinCondition(StringBuffer sqlBuffer) {
		// 处理关联---但是不加入WherePart
		if (getClassJoinValue() != null && !getClassJoinValue().equals(IRefConst.QUERY)) {
			if (HICommonValue.PK_OTHRERPSN_DEPT.equals(getClassJoinValue())) {
				// 选择其他人员节点
				sqlBuffer.append(" and " + getOtherSql());
			} else {
				if (isExactOn()) {
					sqlBuffer.append(" and ( " + getDocJoinField() + " = '" + getClassJoinValue() + "' )");
				} else {
					sqlBuffer.append(" and ( " + getDocJoinField() + " like '%" + getClassJoinValue() + "%' ) ");
				}
			}
		}
	}

	protected void addClassAreaCondition(StringBuffer sqlBuffer) {
		String sql = getClassSql(new String[] { getClassJoinField() }, getClassTableName(), getClassWherePart(), null);
		// 搜索全部时，查询全部不能直接用docJoinField关联，而是用or拼接
		if (sql != null) {
			sqlBuffer.append(" and (hi_psnjob.pk_org in (" + sql + ")");
			sqlBuffer.append(" or hi_psnjob.pk_dept in (" + sql + "))");
		}
	}

	private UFLiteralDate retrievePeriodDate() {
		if (lastPeriodBeginDate == null || !strOldPk_org.equals(getPk_org())) {
			// UFDate busDate =
			// WorkbenchEnvironment.getInstance().getBusiDate();
			UFDate busDate = new UFDate();
			UFLiteralDate busLiteralDate = UFLiteralDate.getDate(busDate.toString().substring(0, 10));
			/*
			 * try { // 上一个考勤期间的开始日期 改为60天 PeriodVO periodVO =
			 * NCLocator.getInstance
			 * ().lookup(IPeriodQueryService.class).queryNextPeriod(getPk_org(),
			 * busLiteralDate); lastPeriodBeginDate = periodVO.getBegindate(); }
			 * catch (BusinessException e) { lastPeriodBeginDate =
			 * busLiteralDate; Debug.error(e); }
			 */

			// ssx modified on 2019-10-19
			// 集團的開發大概沒想到離職超過60天了還要生成日報吧，這回就讓你們見識見識
			// 60天太少，先來個一年的
			// lastPeriodBeginDate = busLiteralDate.getDateBefore(60);
			lastPeriodBeginDate = busLiteralDate.getDateBefore(365);
			// end
		}
		return lastPeriodBeginDate;
	}

	private String getOtherSql() {
		try {
			OrgVO[] orgVOs = NCLocator.getInstance().lookup(IAOSQueryService.class)
					.queryOrgByHROrgPK(getPk_org(), null, null, OrgQueryMode.Independent);
			String[] pkOrgArray = new String[orgVOs.length];
			for (int i = 0; i < orgVOs.length; i++) {
				pkOrgArray[i] = orgVOs[i].getPk_org();
			}

			String pkOrgs = StringPiecer.getDefaultPiecesTogether(pkOrgArray);
			StringBuilder deptPkSQL = new StringBuilder();
			// 此处可不处理启用问题
			deptPkSQL.append("(select pk_dept from org_dept where org_dept.pk_org in (" + pkOrgs + ")  ) dept");
			// 归这个hr组织所管理的并且排除掉行政组织是它的
			String whereConditon = " not exists (select 1 from " + deptPkSQL
					+ " where dept.pk_dept = hi_psnjob.pk_dept)";
			return whereConditon;
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			return " ( '1<>1' )";
		}
	}

	@Override
	public int getDefaultFieldCount() {
		return 6;
	}

	/*
	 * 如果有多个匹配的code，则只返回第一个，否则参照的textfield框内会显示code多次 (non-Javadoc)
	 * 
	 * @see nc.ui.bd.ref.AbstractRefModel#getRefCodeValues()
	 */
	@Override
	public String[] getRefCodeValues() {
		String[] codeValues = super.getRefCodeValues();
		// return ArrayUtils.isEmpty(codeValues)?codeValues:new
		// String[]{codeValues[0]};
		return removeSameValue(codeValues);
	}

	@Override
	public String[] getPkValues() {
		return removeSameValue(super.getPkValues());
	}

	@Override
	public String[] getRefNameValues() {
		return removeSameValue(super.getRefNameValues());
	}

	public String[] removeSameValue(String[] array) {
		if (ArrayUtils.isEmpty(array))
			return array;
		String[] oldPks = super.getPkValues();// 删除相同编码或名称前的pks
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			boolean same = false;
			for (int j = i + 1; j < array.length; j++) {
				if (array[i].equals(array[j]) && oldPks[i].equals(oldPks[j])) {
					same = true;
				}
			}
			if (!same)
				list.add(array[i]);
		}
		if (list.isEmpty())
			return array;
		return list.toArray(new String[0]);
	}
}