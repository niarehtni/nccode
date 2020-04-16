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
	// �ϸ��ڼ�Ŀ�ʼ����
	protected UFLiteralDate lastPeriodBeginDate = null;
	private String strOldPk_org = getPk_org();

	// �����ڲ�����ȡ��֯�汾�Ͳ��Ű汾ֵ���ֶΡ�
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
			// �����������Ϊ9999-12-01����ô����ʾ
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

		// modified by zengcheng 20120723,ԭ����ʾ�������ڵ�����¼����Ϊֻ��ʾ����һ��
		return new StringBuilder(
		// "tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where  tbm_psndoc.pk_org = '")
		// .append(getPk_org()).append("' and (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2 where psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) and tbm_psndoc.enddate > '").append(
		// lastPeriodBeginDate).append("'))").toString();

				// ����������ȱ�ݣ����һ����Ա��a��˾��һ�������Ŀ��ڵ���������Ա�����䵽b��˾�� ��ͣ�����ϵȽڵ�����ʱ��
				// ��a��˾�²鲻������Ա�Ľ����Ŀ��ڵ��� modify by zhouyuh 8-30
				"tbm_psndoc.pk_tbm_psndoc in (select pk_tbm_psndoc from tbm_psndoc where   (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2"
						+ " where psndoc2.pk_org = '").append(getPk_org())
				.append("' and psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc) and tbm_psndoc.enddate > '")
				.append(lastPeriodBeginDate).append("'))").toString();
	}

	/**
	 * �Ƿ�����¼�HR��֯
	 */
	protected boolean isIncludeHrBranch() {
		return false;
	}

	@Override
	public void reset() {
		setRefTitle(ResHelper.getString("common", "UC000-0000129")
		/* @res "��Ա" */);
		setRootName(PublicLangRes.ORGDEPT());
		setClassFieldCode(new String[] { "code", "name", "pk_org", "pk_orgdept", "pk_children", "pk_father",
				"display_order", "pk_group" });
		setClassFatherField("pk_father");
		setClassChildField("pk_children");
		setClassJoinField("pk_orgdept");
		// String otherPsn =
		// ResHelper.getString("6017basedoc","06017basedoc1689")
		// /*@res "������Ա"*/;
		// ���û��ѡ����֯�򲻴���
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
				.append(" case when org_dept.pk_fatherorg='~' then org_adminorg.pk_adminorg else org_dept.pk_fatherorg end pk_father,  cast (org_dept.displayorder as char) display_order ")// �˶����н�int�͵�org_dept.displayorder
																																															// ת����char����
				.append(" from org_dept , org_adminorg  where org_dept.pk_org in (" + orgInSQL
						+ ") and org_dept.pk_org = org_adminorg.pk_adminorg and hrcanceled = 'N'")
				// zengcheng 20120421ע�͵��������У���V61��ʼ��ʱ�����û�С�������Ա����
				// .append(" union select '', '"+otherPsn+"',pk_group, '" +
				// HICommonValue.PK_OTHRERPSN_DEPT + "', ")
				// .append(" pk_group, pk_group, '',  cast (9999999 as char)  from org_group where pk_group = '"
				// + getPk_group() + "' ")
				.append(") orgdept ");// �ζ����н�int�͵�99999 ת����char����
		setClassTableName(classTableName.toString());

		setClassDefaultFieldCount(getClassDefaultFieldCount());
		// ����֯�ɿ�����֯��Χ
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
		/* @res "��Ա����" */, ResHelper.getString("common", "UC000-0001403")
		/* @res "����" */, PublicLangRes.EMPNO(), PublicLangRes.TBMCARDNO(),
				ResHelper.getString("6017basedoc", "06017basedoc1690")
				/* @res "���ڿ�ʼ����" */, ResHelper.getString("6017basedoc", "06017basedoc1691")
				/* @res "���ڽ�������" */, PublicLangRes.ORG(), PublicLangRes.DEPT() });

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
		setRefNodeName("���ڵ���"); /* -=notranslate=- */
		setPkFieldCode("hi_psnjob.pk_psnjob");
		setRefCodeField("clerkcode");
		setRefNameField("bd_psndoc.name");
		setDocJoinField("hi_psnjob.pk_org||hi_psnjob.pk_dept");
		setExactOn(false);
		setWherePart(getBasicWherePart());
		resetFieldName();
	}

	protected void addJoinCondition(StringBuffer sqlBuffer) {
		// �������---���ǲ�����WherePart
		if (getClassJoinValue() != null && !getClassJoinValue().equals(IRefConst.QUERY)) {
			if (HICommonValue.PK_OTHRERPSN_DEPT.equals(getClassJoinValue())) {
				// ѡ��������Ա�ڵ�
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
		// ����ȫ��ʱ����ѯȫ������ֱ����docJoinField������������orƴ��
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
			 * try { // ��һ�������ڼ�Ŀ�ʼ���� ��Ϊ60�� PeriodVO periodVO =
			 * NCLocator.getInstance
			 * ().lookup(IPeriodQueryService.class).queryNextPeriod(getPk_org(),
			 * busLiteralDate); lastPeriodBeginDate = periodVO.getBegindate(); }
			 * catch (BusinessException e) { lastPeriodBeginDate =
			 * busLiteralDate; Debug.error(e); }
			 */

			// ssx modified on 2019-10-19
			// ���F���_�l��ś]�뵽�x���^60����߀Ҫ�����Ո�ɣ��@�ؾ�׌�ゃҊ�RҊ�R
			// 60��̫�٣��ȁ킀һ���
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
			// �˴��ɲ�������������
			deptPkSQL.append("(select pk_dept from org_dept where org_dept.pk_org in (" + pkOrgs + ")  ) dept");
			// �����hr��֯������Ĳ����ų���������֯������
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
	 * ����ж��ƥ���code����ֻ���ص�һ����������յ�textfield���ڻ���ʾcode��� (non-Javadoc)
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
		String[] oldPks = super.getPkValues();// ɾ����ͬ���������ǰ��pks
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