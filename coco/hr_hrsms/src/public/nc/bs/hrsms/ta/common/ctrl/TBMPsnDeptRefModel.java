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
 * ���ڵ�����Ա
 * �ο�PsnDeptRefModel���TBMPsndocRefModel
 * @author shaochj
 * @date Jun 10, 2015
 */
public class TBMPsnDeptRefModel extends PsnjobAOSRefTreeModel2 {
	private boolean isIncludeCancelDept = false; // �Ƿ������������

	private boolean includeDummyDept = false; // �������ⲿ��

	private boolean includeKeyPsnGrp = true; // �����ؼ���Ա��
	
	private static final int DELAY_DAY = 60;
	//�����ڲ�����ȡ��֯�汾�Ͳ��Ű汾ֵ���ֶΡ�
	public static final String PK_ORG_V="org_adminorg.PK_VID as pk_org_v";
	public static final String PK_DEPT_V="org_dept.pk_vid as pk_dept_v";
	public TBMPsnDeptRefModel() {
		reset();
	}

	private HRDeptVO getHRDeptVO(String pk_dept){
		HRDeptVO hrDeptVO = null;
		try {
			// ͨ�����������õ���ǰ����
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
		// �������reset�����ù����������pk_dpet�����ţ�,��д��setClassWherePart��setWherePart
		String pk_dept = SessionUtil.getPk_mng_dept();
		HRDeptVO hrDeptVO = getHRDeptVO(pk_dept);
//		try {
//			// ͨ�����������õ���ǰ����
//			hrDeptVO = (HRDeptVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(null, HRDeptVO.class,
//					pk_dept);
//		} catch (BusinessException e) {
//			new HrssException(e).deal();
//		} catch (HrssException e) {
//			new HrssException(e).alert();
//		}

		setRefTitle(ResHelper.getString("common", "UC000-0000129")/* @res "��Ա" */);
		setRootName(ResHelper.getString("6007psn", "06007psn0302")/* @res "��֯����" */);
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
			// �����HR��֯,����ʾHR��֯���������Ϲ������֯
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
			// �����HR��֯,����ʾHR��֯���������Ϲ������֯
			classTableName.append("  and org_adminorg.pk_adminorg in ( "
					+ AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getPk_org()) + " ) ");
		}
		if (!isIncludeCancelDept()) {// �����������������
			classTableName.append(" and org_dept.hrcanceled = 'N' ");
		}
		if (!isIncludeDummyDept()) {// ������������ⲿ��
			classTableName.append(" and org_dept.depttype = 0 ");
		}
		if (isIncludeKeyPsnGrp()) {
			String keypsn = ResHelper.getString("6007psn", "06007psn0357")/*
																		 * @res
																		 * "�ؼ���Ա��"
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
				// ������֯��Ϊ�� ���Ҳ���ȫ�ֻ��� ,��Ա��ֻ��ʾ��ǰ��֯��
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
//				/* @res "��" */+ "' when 'N' then '" + ResHelper.getString("6001pub", "06001pub0042")/*
//																									 * @
//																									 * res
//																									 * "��"
//																									 */+ "' end " });
//		setFieldName(new String[] { ResHelper.getString("common", "UC000-0000147")/*
//																				 * @
//																				 * res
//																				 * "��Ա����"
//																				 */,
//				ResHelper.getString("common", "UC000-0001403")/*
//															 * @res "����"
//															 */, ResHelper.getString("6007psn", "06007psn0311")/*
//																												 * @
//																												 * res
//																												 * "Ա����"
//																												 */,
//				ResHelper.getString("6007psn", "06007psn0229")/* @res "֤������" */,
//				ResHelper.getString("6007psn", "06007psn0308")/* @res "֤����" */,
//				ResHelper.getString("6007psn", "06007psn0074")/* @res "��֯" */,
//				ResHelper.getString("common", "UC000-0004064")/* @res "����" */,
//				ResHelper.getString("common", "UC000-0001653") /* @res "��λ" */,
//				ResHelper.getString("6007psn", "16007psn0005") /* @res "�Ƿ���ְ" */});
//		setHiddenFieldCode(new String[] { "hi_psnjob.pk_dept", "bd_psndoc.pk_psndoc", "hi_psnjob.pk_org",
//				"hi_psnjob.pk_post", "hi_psnjob.pk_job", "hi_psnjob.pk_psncl", "hi_psnjob.pk_psnorg",
//				"hi_psnjob.pk_psnjob" });
		setFieldCode(new String[]{"bd_psndoc.code", "bd_psndoc.name", 
//				"clerkcode","tbm_psndoc.timecardid", 
				"tbm_psndoc.begindate", "case when tbm_psndoc.enddate ='9999-12-01' then null else tbm_psndoc.enddate end as enddate"
//				, "org_adminorg.name", "org_dept.name"
				});
		setFieldName(new String[]{ResHelper.getString("common","UC000-0000147")
/*@res "��Ա����"*/, ResHelper.getString("common","UC000-0001403")
/*@res "����"*/, 
//PublicLangRes.EMPNO(), PublicLangRes.TBMCARDNO(),
ResHelper.getString("6017basedoc","06017basedoc1690")
/*@res "���ڿ�ʼ����"*/, ResHelper.getString("6017basedoc","06017basedoc1691")
/*@res "���ڽ�������"*/
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
		// �������---���ǲ�����WherePart
		if (getClassJoinValue() != null && !getClassJoinValue().equals(IRefConst.QUERY)) {
			if (HICommonValue.PK_KRYPSNGRP.equals(getClassJoinValue())) {
				// ѡ��ؼ���Ա��ڵ�
				sqlBuffer.append(" and ( hi_psnjob.pk_psnjob in ('') ) ");
			} else if (getClassJoinValue().startsWith(HICommonValue.PRE_KRYPSNGRP)) {
				// ѡ���˾������
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

			// ��Ա���Ȩ�����
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
	// �ϸ��ڼ�Ŀ�ʼ����
	protected UFLiteralDate lastPeriodBeginDate = null;
	private String strOldPk_org = getPk_org();
	private String getBasicWherePart() {
		lastPeriodBeginDate = retrievePeriodDate();
//		return new StringBuilder(
//				"tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where  tbm_psndoc.pk_org = '")
//				.append(getPk_org()).append("' and (tbm_psndoc.enddate like '9999%' or tbm_psndoc.enddate > '").append(
//						lastPeriodBeginDate).append("'))").toString();

		//modified by zengcheng 20120723,ԭ����ʾ�������ڵ�����¼����Ϊֻ��ʾ����һ��
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
//	// ����������ȱ�ݣ����һ����Ա��a��˾��һ�������Ŀ��ڵ���������Ա�����䵽b��˾�� ��ͣ�����ϵȽڵ�����ʱ��   ��a��˾�²鲻������Ա�Ľ����Ŀ��ڵ���	modify by  zhouyuh 8-30		
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
				// ��һ�������ڼ�Ŀ�ʼ���� ��Ϊ60��
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
