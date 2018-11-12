package nc.bs.hrsms.ta.common.ctrl;

import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.org.IOrgConst;
import nc.pub.tools.KeyPsnGroupSqlUtils;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hi.ref.PsnjobAOSRefTreeModel2;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.KeyPsnGrpVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

/**
 * 考勤档案人员
 * 参考PsnDeptRefModel类和TBMPsndocRefModel
 * @author shaochj
 * @date Jun 10, 2015
 */
public class TBMPsnDeptRefModel extends PsnjobAOSRefTreeModel2 {
	private boolean isIncludeCancelDept = false; // 是否包含撤消部门

	private boolean includeDummyDept = false; // 包含虚拟部门

	private boolean includeKeyPsnGrp = true; // 包含关键人员组
	
	private static final int DELAY_DAY = 60;
	//用于在参照中取组织版本和部门版本值的字段。
	public static final String PK_ORG_V="org_adminorg.PK_VID as pk_org_v";
	public static final String PK_DEPT_V="org_dept.pk_vid as pk_dept_v";
	public TBMPsnDeptRefModel() {
		reset();
	}

	private HRDeptVO getHRDeptVO(String pk_dept){
		HRDeptVO hrDeptVO = null;
		try {
			// 通过部门主键得到当前部门
			hrDeptVO = (HRDeptVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(null, HRDeptVO.class,
					pk_dept);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			new HrssException(e).alert();
		}
		return hrDeptVO;
	}
	
	@Override
	public void reset() {
		// 将父类的reset方法拿过来后，添加了pk_dpet（部门）,重写了setClassWherePart与setWherePart
		String pk_dept = SessionUtil.getPk_mng_dept();
		HRDeptVO hrDeptVO = getHRDeptVO(pk_dept);
//		try {
//			// 通过部门主键得到当前部门
//			hrDeptVO = (HRDeptVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(null, HRDeptVO.class,
//					pk_dept);
//		} catch (BusinessException e) {
//			new HrssException(e).deal();
//		} catch (HrssException e) {
//			new HrssException(e).alert();
//		}

		setRefTitle(ResHelper.getString("common", "UC000-0000129")/* @res "人员" */);
		setRootName(ResHelper.getString("6007psn", "06007psn0302")/* @res "组织部门" */);
		setClassFieldCode(new String[] { "code", "name", "pk_orgdept", "pk_children", "pk_father", "display_order",
				"pk_group" });
		setClassFatherField("pk_father");
		setClassChildField("pk_children");
		setClassJoinField("pk_orgdept");
		StringBuffer classTableName = new StringBuffer();
		classTableName
				.append(" ( select code, name,name2,name3,name4,name5,name6,"
						+ " org_adminorg.pk_adminorg pk_org, org_adminorg.pk_adminorg pk_orgdept,org_adminorg.pk_group pk_group, ")
				.append(" org_adminorg.pk_adminorg pk_children, org_adminorg.pk_fatherorg pk_father, org_adminorg.displayorder display_order ")
				.append(" from org_adminorg where enablestate=" + IPubEnumConst.ENABLESTATE_ENABLE);
		if (!StringUtils.isBlank(getPk_org()) && !getPk_org().equals(getPk_group())
				&& !getPk_org().equals(IOrgConst.GLOBEORG)) {
			// 如果有HR组织,则显示HR组织在行政树上管理的组织
			classTableName.append("  and pk_adminorg in ( " + AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getPk_org())
					+ " ) ");
		}
		classTableName
				.append(" union ")
				.append(" select org_dept.code,org_dept.name,org_dept.name2,org_dept.name3,org_dept.name4,org_dept.name5,org_dept.name6,"
						+ " org_dept.pk_org pk_org,org_dept.pk_dept pk_orgdept, org_adminorg.pk_group, ")
				.append(" org_dept.pk_dept pk_children, case when " + SQLHelper.getNullSql("org_dept.pk_fatherorg")
						+ " then org_adminorg.pk_adminorg ")
				.append(" else org_dept.pk_fatherorg end pk_father,isnull(org_dept.displayorder,0) + 999999999999 display_order ")
				.append(" from org_dept , org_adminorg  where org_dept.pk_org = org_adminorg.pk_adminorg  ");
		if (!StringUtils.isBlank(getPk_org()) && !getPk_org().equals(getPk_group())
				&& !getPk_org().equals(IOrgConst.GLOBEORG)) {
			// 如果有HR组织,则显示HR组织在行政树上管理的组织
			classTableName.append("  and org_adminorg.pk_adminorg in ( "
					+ AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getPk_org()) + " ) ");
		}
		if (!isIncludeCancelDept()) {// 如果不包含撤消部门
			classTableName.append(" and org_dept.hrcanceled = 'N' ");
		}
		if (!isIncludeDummyDept()) {// 如果不包含虚拟部门
			classTableName.append(" and org_dept.depttype = 0 ");
		}
		if (isIncludeKeyPsnGrp()) {
			String keypsn = ResHelper.getString("6007psn", "06007psn0357")/*
																		 * @res
																		 * "关键人员组"
																		 */;
			classTableName
					.append(" union ")
					.append(" select '','" + keypsn + "','" + keypsn + "','" + keypsn + "','" + keypsn + "','" + keypsn
							+ "','" + keypsn + "' ,pk_group, '" + HICommonValue.PK_KRYPSNGRP + "', ")
					.append(" pk_group, '" + HICommonValue.PK_KRYPSNGRP
							+ "', '~', 9999999 from org_group where pk_group = '" + getPk_group() + "'  ")
					.append(" union ")
					.append(" select group_code code ,group_name name,group_name2 name2,group_name3 name3,group_name4 name4,group_name5 name5,group_name6 name6,pk_org ,'"
							+ HICommonValue.PRE_KRYPSNGRP
							+ "'||pk_keypsn_group pk_orgdept,pk_group,pk_keypsn_group pk_children,'"
							+ HICommonValue.PK_KRYPSNGRP
							+ "' pk_father, 0 display_order  from hi_keypsn_group where enablestate = "
							+ IPubEnumConst.ENABLESTATE_ENABLE
							+ " and "
							+ KeyPsnGroupSqlUtils.getKeyPsnGroupPowerSql(KeyPsnGrpVO.getDefaultTableName()));

			if (getPk_org() != null && !getPk_org().equals(PubEnv.getPk_group())
					&& !getPk_org().equals(IOrgConst.GLOBEORG)) {
				// 所属组织不为空 并且不是全局或集团 ,人员组只显示当前组织的
				classTableName.append(" and pk_org = '" + getPk_org() + "' ");
			}
		}
		classTableName.append(" ) orgdept ");
		setClassTableName(classTableName.toString());
		setClassDefaultFieldCount(getClassDefaultFieldCount());

		if (hrDeptVO != null && hrDeptVO.getInnercode().trim() != null) {
			this.setClassWherePart(" pk_group ='" + getPk_group() + "' and pk_orgdept='"+pk_dept+"'"
					);

		} else {
			setClassWherePart(" pk_group ='" + getPk_group() + "' and pk_orgdept='"+pk_dept+"'");
		}
		
		// setClassWherePart(" pk_group ='" + getPk_group() + "' ");
		setClassOrderPart("display_order");
//		setFieldCode(new String[] {
//				"bd_psndoc.code",
//				"bd_psndoc.name",
//				"clerkcode",
//				"bd_psndoc.pk_psndoc",
//				 
//				"id",
//				HiSQLHelper.getLangNameColume("org_orgs.name"), HiSQLHelper.getLangNameColume("org_dept.name"),
//				HiSQLHelper.getLangNameColume("om_post.postname"),
//				" case ismainjob when  'Y' then '" + ResHelper.getString("6001pub", "06001pub0041")
//				/* @res "是" */+ "' when 'N' then '" + ResHelper.getString("6001pub", "06001pub0042")/*
//																									 * @
//																									 * res
//																									 * "否"
//																									 */+ "' end " });
//		setFieldName(new String[] { ResHelper.getString("common", "UC000-0000147")/*
//																				 * @
//																				 * res
//																				 * "人员编码"
//																				 */,
//				ResHelper.getString("common", "UC000-0001403")/*
//															 * @res "姓名"
//															 */, ResHelper.getString("6007psn", "06007psn0311")/*
//																												 * @
//																												 * res
//																												 * "员工号"
//																												 */,
//				ResHelper.getString("6007psn", "06007psn0229")/* @res "证件类型" */,
//				ResHelper.getString("6007psn", "06007psn0308")/* @res "证件号" */,
//				ResHelper.getString("6007psn", "06007psn0074")/* @res "组织" */,
//				ResHelper.getString("common", "UC000-0004064")/* @res "部门" */,
//				ResHelper.getString("common", "UC000-0001653") /* @res "岗位" */,
//				ResHelper.getString("6007psn", "16007psn0005") /* @res "是否主职" */});
//		setHiddenFieldCode(new String[] { "hi_psnjob.pk_dept", "bd_psndoc.pk_psndoc", "hi_psnjob.pk_org",
//				"hi_psnjob.pk_post", "hi_psnjob.pk_job", "hi_psnjob.pk_psncl", "hi_psnjob.pk_psnorg",
//				"hi_psnjob.pk_psnjob" });
		setFieldCode(new String[]{"bd_psndoc.code", "bd_psndoc.name", 
//				"clerkcode","tbm_psndoc.timecardid", 
				"tbm_psndoc.begindate", "case when tbm_psndoc.enddate ='9999-12-01' then null else tbm_psndoc.enddate end as enddate"
//				, "org_adminorg.name", "org_dept.name"
				});
		setFieldName(new String[]{ResHelper.getString("common","UC000-0000147")
/*@res "人员编码"*/, ResHelper.getString("common","UC000-0001403")
/*@res "姓名"*/, 
//PublicLangRes.EMPNO(), PublicLangRes.TBMCARDNO(),
ResHelper.getString("6017basedoc","06017basedoc1690")
/*@res "考勤开始日期"*/, ResHelper.getString("6017basedoc","06017basedoc1691")
/*@res "考勤结束日期"*/
//, PublicLangRes.ORG(),PublicLangRes.DEPT()
});

		setHiddenFieldCode(new String[]{"bd_psndoc.pk_group", "bd_psndoc.pk_org", "bd_psndoc.pk_psndoc","hi_psnjob.clerkcode",
				"hi_psnjob.pk_psnorg", "hi_psnjob.pk_psnjob", "hi_psnjob.pk_psncl", "hi_psnjob.pk_org","hi_psnjob.pk_dept",
				"hi_psnjob.pk_job", "hi_psnjob.pk_post",PK_ORG_V,PK_DEPT_V});
//		setTableName(" bd_psndoc inner join hi_psnorg on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc   "
//				+ " inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg  "
//				+ " left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org "
//				+ " left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept "
//				+ " left outer join om_post on om_post.pk_post = hi_psnjob.pk_post  ");
		setTableName("  tbm_psndoc inner join hi_psnjob on tbm_psndoc.pk_psnjob = hi_psnjob.pk_psnjob  " +
				"inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg " +
				"inner join bd_psndoc on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc " +
//				"left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org " +
				"left outer join org_adminorg on org_adminorg.pk_adminorg = hi_psnjob.pk_org " +
				"left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept " +
				"left outer join om_post on om_post.pk_post = hi_psnjob.pk_post " );
		
		setPkFieldCode("hi_psnjob.pk_psnjob");
		setDocJoinField("hi_psnjob.pk_dept");
		setRefCodeField("clerkcode");
		setRefNameField("bd_psndoc.name");
		setExactOn(false);
		setWherePart(getBasicWherePart());
		setCacheEnabled(true);
		resetFieldName();
	}

	@Override
	protected void addJoinCondition(StringBuffer sqlBuffer) {
		// 处理关联---但是不加入WherePart
		if (getClassJoinValue() != null && !getClassJoinValue().equals(IRefConst.QUERY)) {
			if (HICommonValue.PK_KRYPSNGRP.equals(getClassJoinValue())) {
				// 选择关键人员组节点
				sqlBuffer.append(" and ( hi_psnjob.pk_psnjob in ('') ) ");
			} else if (getClassJoinValue().startsWith(HICommonValue.PRE_KRYPSNGRP)) {
				// 选择了具体的组
				String pk_keypsngrp = getClassJoinValue().substring(5);
				sqlBuffer
						.append(" and ( hi_psnjob.pk_psnorg in ( select pk_psnorg from hi_psndoc_keypsn where pk_keypsn_grp = '"
								+ pk_keypsngrp + "' and ( endflag <> 'Y' ) ) )");
			} else {
				if (isExactOn()) {
					sqlBuffer.append(" and ( " + getDocJoinField() + " = '" + getClassJoinValue() + "' ) ");
				} else {
					sqlBuffer.append(" and ( " + getDocJoinField() + " = '" + getClassJoinValue()
							+ "' or hi_psnjob.pk_dept = '" + getClassJoinValue() + "')");
				}
			}

			// 人员组的权限最大
			sqlBuffer.append(" and hi_psnjob.pk_psnjob not in ( " + KeyPsnGroupSqlUtils.getKeyPsnPowerSql() + " )");
		}
	}

	@Override
	public boolean isIncludeCancelDept() {
		return isIncludeCancelDept;
	}

	@Override
	public void setIncludeCancelDept(boolean isIncludeCancelDept) {
		this.isIncludeCancelDept = isIncludeCancelDept;
		reset();
	}

	@Override
	public boolean isIncludeDummyDept() {
		return includeDummyDept;
	}

	@Override
	public void setIncludeDummyDept(boolean includeDummyDept) {
		this.includeDummyDept = includeDummyDept;
		reset();
	}

	@Override
	public void setIncludeKeyPsnGrp(boolean includeKeyPsnGrp) {
		this.includeKeyPsnGrp = includeKeyPsnGrp;
		reset();
	}

	@Override
	public boolean isIncludeKeyPsnGrp() {
		return includeKeyPsnGrp;
	}
	// 上个期间的开始日期
	protected UFLiteralDate lastPeriodBeginDate = null;
	private String strOldPk_org = getPk_org();
	private String getBasicWherePart() {
		lastPeriodBeginDate = retrievePeriodDate();
//		return new StringBuilder(
//				"tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where  tbm_psndoc.pk_org = '")
//				.append(getPk_org()).append("' and (tbm_psndoc.enddate like '9999%' or tbm_psndoc.enddate > '").append(
//						lastPeriodBeginDate).append("'))").toString();

		//modified by zengcheng 20120723,原来显示多条考勤档案记录，改为只显示最新一条
		StringBuffer basicWherePart = new StringBuffer();
		basicWherePart.append(" tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where   (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2 ");
		basicWherePart.append(" where psndoc2.pk_org = '"+getPk_org()+"' ");
//		basicWherePart.append(" and psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) and tbm_psndoc.enddate > '");
//		basicWherePart.append(lastPeriodBeginDate);
		basicWherePart.append(" and psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) ");
		basicWherePart.append("))");
		String pk_dept = SessionUtil.getPk_mng_dept();
		HRDeptVO hrDeptVO = getHRDeptVO(pk_dept);
		UFLiteralDate currentDate = lastPeriodBeginDate.getDateAfter(DELAY_DAY);
		basicWherePart.append(" and hi_psnorg.indocflag = 'Y' ");
		basicWherePart.append(" and (( hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N' and hi_psnjob.begindate <= '").append(currentDate).append("')")
		.append(" or ( hi_psnjob.lastflag = 'N' and hi_psnjob.endflag = 'Y'")
		.append("  and ( (hi_psnjob.begindate <= '").append(lastPeriodBeginDate).append("' and hi_psnjob.enddate >= '").append(lastPeriodBeginDate).append("') " +
				"or (hi_psnjob.begindate <= '").append(currentDate).append("' and hi_psnjob.enddate >= '").append(currentDate).append("') " +
						"or (hi_psnjob.begindate >= '").append(lastPeriodBeginDate).append("' and hi_psnjob.enddate <= '").append(currentDate).append("') ))")
						.append(")")
						;
		if (hrDeptVO != null && hrDeptVO.getInnercode().trim() != null) {
//			basicWherePart.append(" and hi_psnorg.indocflag = 'Y' and hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N' and hi_psnjob.ismainjob='Y' and hi_psnjob.poststat='Y' ");
//			.append("and hi_psnjob.ismainjob='Y' and hi_psnjob.poststat='Y' ");
			basicWherePart.append(" and hi_psnjob.pk_dept in (select pk_dept from org_dept where innercode like '"	+hrDeptVO.getInnercode() + "%')");
		}else{
//			basicWherePart.append(" and hi_psnorg.indocflag = 'Y' and hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N' and hi_psnjob.ismainjob='Y' and hi_psnjob.poststat='Y' ");
			basicWherePart.append(" and  hi_psnjob.pk_dept='"+pk_dept+"'");
		}
		return basicWherePart.toString();
//		return new StringBuilder(
//				//"tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where  tbm_psndoc.pk_org = '")
//				//.append(getPk_org()).append("' and (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2 where psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) and tbm_psndoc.enddate > '").append(
//					//	lastPeriodBeginDate).append("'))").toString();
//				
//	// 上面的语句有缺陷，如果一个人员在a公司有一条结束的考勤档案，该人员被调配到b公司后 在停工待料等节点新增时候   在a公司下查不到该人员的结束的考勤档案	modify by  zhouyuh 8-30		
//	"tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where   (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2" +
//	" where psndoc2.pk_org = '")
//	.append(getPk_org()).append("' and psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) and tbm_psndoc.enddate > '").append(
//			lastPeriodBeginDate).append("'))").toString();
	}
	private UFLiteralDate retrievePeriodDate() {
		if (lastPeriodBeginDate == null || !strOldPk_org.equals(getPk_org())) {
//			UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
			UFDate busDate = new UFDate();
			UFLiteralDate busLiteralDate = UFLiteralDate.getDate(busDate.toString().substring(0, 10));
			/*
			try {
				// 上一个考勤期间的开始日期 改为60天
				PeriodVO periodVO = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryNextPeriod(getPk_org(), busLiteralDate);
				lastPeriodBeginDate = periodVO.getBegindate();
			} catch (BusinessException e) {
				lastPeriodBeginDate = busLiteralDate;
				Debug.error(e);
			}
			*/
			lastPeriodBeginDate = busLiteralDate.getDateBefore(DELAY_DAY);
		}
		return lastPeriodBeginDate;
	}

}
