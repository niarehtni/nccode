package nc.impl.wa.psndocwadoc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import nc.bs.bd.baseservice.busilog.BDBusiLogUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.adjust.BTOBXVOConversionUtility;
import nc.impl.wa.adjust.PsndocWadocAdjustTool;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IPsndocwadocManageService;
import nc.itf.hr.wa.IPsndocwadocQueryService;
import nc.itf.hr.wa.IWaGradeService;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.itf.ta.IPsnCalendarManageService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.comp.sort.UFDoubleCompare;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.WainfoVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;
import nc.vo.util.bizlock.BizlockDataUtil;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.BatchAdjustVO;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.IWaGradeCommonDef;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;
import nc.vo.wa.grade.WaPrmlvVO;
import nc.vo.wa.grade.WaPsnhiBVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.grade.WaSeclvVO;
import nc.vo.wa.pub.HRWAMetadataConstants;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * ��������Ϣά�� impl
 * 
 * @author: xuhw
 * @date: 2009-12-26 ����09:27:04
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:s
 * @�޸�����:
 */
public class PsndocwadocManageServiceImpl implements IPsndocwadocManageService, IPsndocwadocQueryService {

	private String refTransType; // ��ͣ�������
	private String refReturnType;// �}�������

	private PsndocWadocDAO psndocWadocDaO;

	/** ���뵼��ģ������Ŀ��λ�� */
	private final int WA_ITEM = 2;
	private final int PSNNAME = 1;
	private final int PSNCODE = 0;
	private final int WA_GRADE = 3;
	private final int BEGINDATE = 4;

	/**
	 * �����ݿ��в���һ��VO���� �������ڣ�(2004-6-3)
	 * 
	 * @param psndocWadoc
	 *            nc.vo.hi.wadoc.PsndocWadocVO
	 * @return java.lang.String ������VO����������ַ�����
	 */
	@Override
	public String insertPsndocWadocVO(PsndocWadocVO psndocWadoc) throws BusinessException {
		// ��Ч���ڵ�У��
		validateUsedate(psndocWadoc, Boolean.TRUE);
		// У����Ա��н���Ƿ��ںϷ��ķ�Χ�ڡ�
		validateMoneyLimit(psndocWadoc, null);
		// ������Ա������н����Ŀ���������ʵȼ���������ǰ�ļ�¼
		if (psndocWadoc.getRecordnum() == null) {
			psndocWadoc.setRecordnum(Integer.valueOf(0));
		}
		if (psndocWadoc.getLastflag() == null) {
			psndocWadoc.setLastflag(UFBoolean.TRUE);
		}
		psndocWadoc.setWorkflowflag(UFBoolean.FALSE);
		this.getPsndocWadocDao().updatePrePsnWadocFlag(psndocWadoc.getPk_psndoc(), psndocWadoc.getAssgid(),
				psndocWadoc.getPk_wa_item(), psndocWadoc.getPk_wa_grd(), psndocWadoc.getRecordnum(), Boolean.TRUE);
		this.getPsndocWadocDao().updatePreEnddate(psndocWadoc);

		// 2016-12-2 zhousze н�ʼ��ܣ����ﴦ��������Ϣά���ֱ���������ʱ�����ݼ��� begin
		psndocWadoc.setNmoney(psndocWadoc.getNmoney() == null ? null : new UFDouble(SalaryEncryptionUtil
				.encryption(psndocWadoc.getNmoney().toDouble())));
		psndocWadoc.setCriterionvalue(psndocWadoc.getCriterionvalue() == null ? null : new UFDouble(
				SalaryEncryptionUtil.encryption(psndocWadoc.getCriterionvalue().toDouble())));
		// end

		String key = this.getPsndocWadocDao().getBaseDao().insertVO(psndocWadoc);

		// ͬ��hi���ݡ�����
		BTOBXVOConversionUtility.psndocWadocSaveToWainfoVO(psndocWadoc);

		return key;
	}

	/**
	 * �����ݿ��в���һ��VO����
	 */
	@Override
	public String[] insertArray(PsndocWadocVO[] psndocWadocs) throws BusinessException {
		if (psndocWadocs == null || psndocWadocs.length == 0) {
			return null;
		}

		// 2016-12-05 zhousze н�ʼ��ܣ����ﴦ��������Ϣά��н���յ�������������� begin
		for (PsndocWadocVO vo : psndocWadocs) {
			vo.setNmoney(vo.getNmoney() == null ? null : new UFDouble(SalaryEncryptionUtil.encryption(vo.getNmoney()
					.toDouble())));
			vo.setCriterionvalue(vo.getCriterionvalue() == null ? null : new UFDouble(SalaryEncryptionUtil
					.encryption(vo.getCriterionvalue().toDouble())));
		}
		// end

		// ��Ч���ڵ�У�� ��Ϊ�� н���յ��� �������еļ�¼����Ч���ڶ���ͬ��У��һ���Ϳ���
		// validateUsedate(psndocWadocs[0].getBegindate());
		// getPsndocWadocDao().updatePrePsnWadocFlag(psndocWadocs);//�����޸ļ�¼�ţ����Է���Ч��û�б仯���ݲ�����
		Hashtable<String, String> ht = new Hashtable<String, String>();
		PsndocWadocVO[] wadocs = null;
		for (int i = 0; i < psndocWadocs.length; i++) {
			// validateMoneyLimit(psndocWadocs[i], i+1);
			if (psndocWadocs[i].getRecordnum() == null) {
				psndocWadocs[i].setRecordnum(Integer.valueOf(0));
			}
			if (psndocWadocs[i].getLastflag() == null) {
				psndocWadocs[i].setLastflag(UFBoolean.valueOf(true));
			}
			if (!psndocWadocs[i].getPk_wa_item().equals(ht.get(psndocWadocs[i].getPk_psndoc()))) {
				validateUsedate(psndocWadocs[i], new Boolean(true));
				// ��֤ͬһ����������ݣ�ͬһ��ͬһ��Ŀ�ļ�¼��ֻ����һ��
				// this.getPsndocWadocDao().updatePrePsnWadocFlag(psndocWadocs[i].getPk_psndoc(),psndocWadocs[i].getAssgid(),
				// psndocWadocs[i].getPk_wa_item(), "insertarray",
				// psndocWadocs[i].getRecordnum(), new Boolean(true));
				// this.getPsndocWadocDao().updatePreEnddate(psndocWadocs[i]);
				wadocs = (PsndocWadocVO[]) ArrayUtils.add(wadocs, psndocWadocs[i]);
				ht.put(psndocWadocs[i].getPk_psndoc(), psndocWadocs[i].getPk_wa_item());
			}
		}

		// ��ҵ����
		BizlockDataUtil.lockDataByBizlock(psndocWadocs);
		updatePrePsnWadocFlag(wadocs);
		updatePreEnddate(wadocs);
		String[] keys = getPsndocWadocDao().getBaseDao().insertVOArray(psndocWadocs);
		return keys;
	}

	/**
	 * ������������Ϣ��ֹ����Ϊ��,��������Ϣ�Ŀ�ʼ����ǰһ������������������Ϣ��ֹ����
	 * 
	 * @param psnpk
	 * @param itempk
	 * @param grdpk
	 * @throws DAOException
	 */
	private void updatePreEnddate(PsndocWadocVO[] psndocWadocs) throws DAOException {
		String sql = "update  hi_psndoc_wadoc set enddate = ? where pk_psndoc = ? and pk_wa_item=? and  recordnum = 1 and  isnull(enddate,'~')='~' ";
		SQLParameter[] parameters = null;
		String[] sqls = null;
		for (PsndocWadocVO psndocWadocVO : psndocWadocs) {
			sqls = (String[]) ArrayUtils.add(sqls, sql);
			SQLParameter parameter = new SQLParameter();
			UFLiteralDate begindate = psndocWadocVO.getBegindate();
			UFLiteralDate enddate = begindate.getDateBefore(1);
			parameter.addParam(enddate.toString());
			parameter.addParam(psndocWadocVO.getPk_psndoc());
			parameter.addParam(psndocWadocVO.getPk_wa_item());
			parameters = (SQLParameter[]) ArrayUtils.add(parameters, parameter);
		}
		try {
			NCLocator.getInstance().lookup(IPersistenceUpdate.class).executeSQLs(sqls, parameters);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage());
		}
	}

	private void updatePrePsnWadocFlag(PsndocWadocVO[] psndocWadocs) throws DAOException {
		String sql = "update hi_psndoc_wadoc set recordnum = recordnum+1, lastflag='N' where pk_psndoc = ? and pk_wa_item=?  and recordnum >= 0";
		SQLParameter[] parameters = null;
		String[] sqls = null;
		for (PsndocWadocVO psndocWadocVO : psndocWadocs) {
			sqls = (String[]) ArrayUtils.add(sqls, sql);
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(psndocWadocVO.getPk_psndoc());
			parameter.addParam(psndocWadocVO.getPk_wa_item());
			parameters = (SQLParameter[]) ArrayUtils.add(parameters, parameter);
		}
		try {
			NCLocator.getInstance().lookup(IPersistenceUpdate.class).executeSQLs(sqls, parameters);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage());
		}
	}

	/**
	 * �����ݿ��в���һ��VO����
	 */
	public String[] insertArray4BatchImport(PsndocWadocVO[] psndocWadocs) throws BusinessException {
		if (psndocWadocs == null || psndocWadocs.length == 0) {
			return null;
		}

		// ��ҵ����
		BizlockDataUtil.lockDataByBizlock(psndocWadocs);
		return getPsndocWadocDao().getBaseDao().insertVOArray(psndocWadocs);
	}

	/**
	 * �������������ݿ���ɾ��һ��VO����
	 * 
	 * @param key
	 */
	@Override
	public void deleteByPsndocWadocVO(PsndocWadocVO vo) throws BusinessException {
		String pk_psndoc = vo.getPk_psndoc();
		getPsndocWadocDao().delete(vo);
		new BDBusiLogUtil(HRWAMetadataConstants.WA_ADJUST_MDID).writeBusiLog(HRWAMetadataConstants.DELLINE, "", vo);
		PsndocWadocVO temp = new PsndocWadocVO();
		temp.setPk_psndoc(pk_psndoc);

		boolean isNotEmpty = getPsndocWadocDao().isValueExist(
				" select * from hi_psndoc_wadoc where pk_psndoc = '" + pk_psndoc + "'");
		if (!isNotEmpty) {
			getPsndocWadocDao().getBaseDao().deleteByClause(WainfoVO.class, "pk_psndoc = '" + pk_psndoc + "'");
		} else {
			// ͬ��hi���ݡ�����
			BTOBXVOConversionUtility.psndocWadocSaveToWainfoVO(temp);
		}
	}

	/**
	 * �õ���������н����ϵ��н����Ŀ
	 */
	@Override
	public String[][] queryAllItemForWadoc(LoginContext context) throws BusinessException {
		return getPsndocWadocDao().queryAllItemForWadoc(context);
	}

	/**
	 * ��ѯ��Ա������н�ʱ䶯�����
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocVO
	 * @param psnPK
	 */
	@Override
	public PsndocWadocVO queryAllVOsByPsnPK(String psnPK, String pk_wa_grd, String pk_wa_item) throws BusinessException {
		return getPsndocWadocDao().queryAllVOsByPsnPK(psnPK, pk_wa_grd, pk_wa_item);
	}

	/**
	 * ��ѯ��Ա������н�ʱ䶯�����
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocVO[]
	 * @param psnPKs
	 */
	@Override
	public PsndocWadocVO[] queryAllVOsByPsnPKs(String[] psnPKs, String pk_wa_grd, String pk_wa_item)
			throws BusinessException {
		return getPsndocWadocDao().queryAllVOsByPsnPKs(psnPKs, pk_wa_grd, pk_wa_item);
	}

	/**
	 * ��ѯ��Ա������н�ʱ䶯�����
	 */
	@Override
	public PsndocWadocVO[] queryAllVOsByPsnPKForHI(String psnPK, Integer assgid, boolean isFromImport)
			throws BusinessException {
		return getPsndocWadocDao().queryAllVOsByPsnPKForHI(psnPK, assgid, isFromImport);
	}

	/**
	 * 2016-1-14 NCdp205573251 zhousze
	 * Ա���ڶ�������¼�ְʱ�������ܿ���������ְ�����µĶ��������ݡ�����Ĵ��������Ӳ��Ź��˲���
	 */
	@Override
	public PsndocWadocVO[] queryAllVOsByPsnPKForHRSS(String psnPK, boolean ispart, boolean isFromImport, String pk_dept)
			throws BusinessException {
		return getPsndocWadocDao().queryAllVOsByPsnPKForHRSS(psnPK, ispart, isFromImport, pk_dept);
	}

	/**
	 * ��ѯн�ʱ䶯��Ϣ
	 * 
	 * @author xuhw on 2009-12-29
	 * @param pkcorp
	 * @param queryStr
	 * @param tableCodes
	 *            //TODO �������Ŀǰû��ʹ�ã���ѯ�������⡣
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public PsndocWadocMainVO[] queryWadocMainData(LoginContext context, String partFlag, String queryStr,
			List<String> tableCodes, String strItemPK, String strGrdPK) throws BusinessException {
		PsndocWadocMainVO[] vos = null;
		StringBuffer sbSelStr = new StringBuffer();
		sbSelStr.append(" select distinct ");
		sbSelStr.append("    hi_psndoc_wadoc.nmoney, ");
		sbSelStr.append("    bd_psndoc.code as psncode , ");
		sbSelStr.append("     " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  as psnname , ");
		sbSelStr.append("    org_dept.code as deptcode , ");
		sbSelStr.append("     " + SQLHelper.getMultiLangNameColumn("org_dept.name") + "  as deptname , ");
		sbSelStr.append("     " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  as psnclname , ");
		// guoqt��λ��λ����ְ�����
		sbSelStr.append("    " + SQLHelper.getMultiLangNameColumn("om_post.postname") + " as postname , ");
		sbSelStr.append("    " + SQLHelper.getMultiLangNameColumn("om_postseries.postseriesname") + " as postserise , ");
		sbSelStr.append("    " + SQLHelper.getMultiLangNameColumn("om_job.jobname") + " as jobname , ");

		sbSelStr.append("    bd_psndoc.pk_psndoc, ");
		sbSelStr.append("    hi_psnjob.pk_psnjob, ");
		sbSelStr.append("    hi_psnjob.clerkcode, ");
		sbSelStr.append("    (case when hi_psnjob.ismainjob ='N' then 'Y' else 'N' end) as partflag, ");
		sbSelStr.append("    hi_psnjob.assgid, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_wa_item, ");
		sbSelStr.append("    hi_psndoc_wadoc.lastflag, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_wa_grd, ");
		sbSelStr.append("    hi_psndoc_wadoc.waflag, ");
		sbSelStr.append("    hi_psndoc_wadoc.workflowFlag, ");
		sbSelStr.append("    wa_criterion.pk_wa_crt, ");
		// sbSelStr.append("    wa_criterion.max_value as crt_max_value, ");
		// sbSelStr.append("    wa_criterion.min_value as crt_min_value, ");
		// sbSelStr.append("    wa_criterion.pk_wa_prmlv as prmlvpk, ");
		sbSelStr.append("    hi_psndoc_wadoc.criterionvalue, ");
		sbSelStr.append("    hi_psndoc_wadoc.crt_max_value as crt_max_value, ");
		sbSelStr.append("    hi_psndoc_wadoc.crt_min_value as crt_min_value, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_wa_prmlv as prmlvpk, ");

		sbSelStr.append("    hi_psndoc_wadoc.negotiation_wage, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_psndoc_sub, ");
		sbSelStr.append("    wa_grade.prmlv_money_sort,");
		sbSelStr.append("    wa_grade.seclv_money_sort,");
		sbSelStr.append("    wa_grade.ismultsec,");
		sbSelStr.append("    wa_grade.isrange, hi_psndoc_wadoc.recordnum ");
		// -add for V5.7 н���յ� start
		sbSelStr.append(",  " + SQLHelper.getMultiLangNameColumn("wa_grade.name")
				+ "  as pk_wa_grd_showname ,wa_grade.prmlv_money_sort , wa_grade.seclv_money_sort, wa_grade.ismultsec ");
		sbSelStr.append(", wa_prmlv.pk_wa_prmlv , wa_seclv.pk_wa_seclv , wa_criterion.pk_wa_crt,  wa_grade_ver.effect_flag, "
				+ SQLHelper.getMultiLangNameColumn("wa_item.name") + "  as pk_wa_item_showname  ");
		// -add for V5.7 н���յ� end

		// 2015-10-31 zhousze ����������Ϣά�����е���ְ��֯��ְ���鲻������,ʹ�ù����� begin
		sbSelStr.append(" ,  " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + " as  orgName ");
		// sbSelStr.append(" , (select org_orgs.name from org_orgs where org_orgs.pk_org = hi_psnjob.pk_org) as orgName");
		// end

		StringBuffer tablestr = new StringBuffer();
		tablestr.append(" from  hi_psnjob");
		tablestr.append("    inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc   ");
		tablestr.append("    inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		tablestr.append("    left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		tablestr.append("    left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		tablestr.append("    left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		tablestr.append("    left outer join om_postseries on hi_psnjob.pk_postseries = om_postseries.pk_postseries ");
		tablestr.append("    left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");

		tablestr.append("    left outer  join hi_psndoc_wadoc on hi_psndoc_wadoc.pk_psndoc =   bd_psndoc.pk_psndoc and hi_psndoc_wadoc.assgid = hi_psnjob.assgid ");
		tablestr.append("         and hi_psndoc_wadoc.waflag = 'Y' and hi_psndoc_wadoc.LASTFLAG ='Y'");
		// tablestr.append("    left outer join wa_grade on hi_psndoc_wadoc.pk_wa_grd = wa_grade.pk_wa_grd ");
		// tablestr.append("    left outer join wa_criterion on hi_psndoc_wadoc.pk_wa_grd = wa_criterion.pk_wa_grd ");
		// tablestr.append("         and hi_psndoc_wadoc.pk_wa_crt =  wa_criterion.pk_wa_crt ");

		// -add for V5.7 н���յ� start
		tablestr.append("        left outer join wa_grade  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_grd = wa_grade.pk_wa_grd ");

		tablestr.append("        left outer join wa_prmlv  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_prmlv = wa_prmlv.pk_wa_prmlv  ");
		tablestr.append(getJoinTableStr(tableCodes));
		tablestr.append("        left outer join wa_seclv  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_seclv = wa_seclv.pk_wa_seclv  ");

		tablestr.append("        left outer join wa_grade_ver  ");
		tablestr.append("        on wa_grade_ver.pk_wa_grd = hi_psndoc_wadoc.pk_wa_grd   and wa_grade_ver.effect_flag = 'Y'  ");

		tablestr.append("        left outer join wa_criterion  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_grd = wa_criterion.pk_wa_grd and ");
		tablestr.append("        isnull(hi_psndoc_wadoc.pk_wa_seclv, 'not_have_pk_wa_seclv') = isnull(wa_criterion.pk_wa_seclv, 'not_have_pk_wa_seclv') and ");
		tablestr.append("        hi_psndoc_wadoc.pk_wa_prmlv = wa_criterion.pk_wa_prmlv and wa_criterion.pk_wa_gradever = wa_grade_ver.pk_wa_gradever ");

		tablestr.append("        left outer join wa_item  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_item = wa_item.pk_wa_item ");
		// -add for V5.7 н���յ� end
		// 2015-10-31 zhousze ����������Ϣά�����е���ְ��֯��ְ���鲻������,ʹ�ù����� begin
		tablestr.append("        left outer join org_orgs ");
		tablestr.append("        on hi_psnjob.pk_org = org_orgs.pk_org ");
		// end

		StringBuffer sbWhere = new StringBuffer();
		/*
		 * sbWhere.append(" where hi_psnjob.pk_group = '" +
		 * context.getPk_group() + "'");
		 * sbWhere.append("   and hi_psnjob.pk_org = '" + context.getPk_org() +
		 * "'");
		 */
		ManagescopeBusiregionEnum busiregionEnum = ManagescopeBusiregionEnum.salary;
		Integer allowed = WaAdjustParaTool.getWaOrg(context.getPk_group());
		if (allowed.equals(1)) {
			busiregionEnum = ManagescopeBusiregionEnum.psndoc;
		}

		/*
		 * OrgVO[] orgVOs = NCLocator.getInstance().lookup(
		 * IAOSQueryService.class).queryOrgByHROrgPK(context.getPk_org(), null,
		 * null, OrgQueryMode.Independent);
		 * 
		 * String[] pkOrgArray = new String[orgVOs.length]; for (int i = 0; i <
		 * orgVOs.length; i++) { pkOrgArray[i] = orgVOs[i].getPk_org(); } String
		 * pkOrgs = SQLHelper.joinToInSql(pkOrgArray, -1);
		 * 
		 * sbWhere.append(" where ( hi_psnjob.pk_psnjob in "+ManagescopeFacade.
		 * queryPsnjobPksSQLByHrorgAndBusiregion
		 * (context.getPk_org(),busiregionEnum,false));
		 * sbWhere.append("   or ( hi_psnjob.pk_org in ( " + pkOrgs +
		 * " )  and  hi_psnjob.lastflag = 'Y' ))");
		 */
		sbWhere.append(" where hi_psnjob.pk_psnjob in "
				+ ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(context.getPk_org(), busiregionEnum, true));

		sbWhere.append("  and hi_psnorg.indocflag = 'Y' and hi_psnorg.lastflag = 'Y' ");
		// sbWhere.append("and hi_psnorg.psntype in( 0,1) and hi_psnorg.lastflag = 'Y' and (bd_psndoc.enablestate =2)  ");
		// sbWhere.append("   and hi_psnjob.ismainjob = 'Y'  and hi_psnjob.lastflag = 'Y' ");

		// sbWhere.append("  and hi_psnjob.ismainjob = 'Y' ");
		// ��ѯģ����Ԥ������֯��ϵ�Ƿ����������Ĭ��ΪN
		if (partFlag.equals("Y")) {
			sbWhere.append(" and hi_psnjob.ismainjob = 'N'");
		} else if (partFlag.equals("N")) {
			sbWhere.append(" and hi_psnjob.ismainjob = 'Y'");
		}
		if (queryStr != null) {
			queryStr = queryStr.replace("pk_wa_grd", "hi_psndoc_wadoc.pk_wa_grd");
			queryStr = queryStr.replace("pk_wa_prmlv", "hi_psndoc_wadoc.pk_wa_prmlv");
			queryStr = queryStr.replace("pk_wa_seclv", "hi_psndoc_wadoc.pk_wa_seclv");

			sbWhere.append(" and hi_psnjob.pk_psnjob in (select pk_psnjob from hi_psnjob where (" + queryStr
					+ ") and hi_psnjob.lastflag='Y')");
		} /*
		 * else { sbWhere.append("    and hi_psnorg.endflag = 'N' "); }
		 */

		if (!StringUtils.isEmpty(strItemPK)) {
			sbWhere.append("   and hi_psndoc_wadoc.pk_wa_item = '" + strItemPK + "'");
		}

		if (!StringUtils.isEmpty(strGrdPK)) {
			sbWhere.append("   and hi_psndoc_wadoc.pk_wa_grd = '" + strGrdPK + "'");
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "hi_psnjob");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sbWhere.append(" and " + powerSql);
		}
		// ����δ������ְ֯�ܵ���Ա
		sbWhere.append(" and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) ");

		String orderStr = "order by  partflag, org_dept.code,bd_psndoc.code  ,hi_psndoc_wadoc.pk_wa_item ,hi_psndoc_wadoc.recordnum ";

		sbSelStr.append(tablestr.toString());
		sbSelStr.append(sbWhere.toString());
		sbSelStr.append(orderStr);

		WaBusilogUtil.writeAdjustQueryBusiLog(context);

		// �õ����е���VO
		PsndocWadocVO[] subVOs = getPsndocWadocDao().executeQueryVOs(sbSelStr.toString(), PsndocWadocVO.class);
		// subVOs = filterQueryData(subVOs);
		Hashtable<String, PsndocWadocMainVO> htMainVO = new Hashtable<String, PsndocWadocMainVO>();
		Vector<String> vpkpsn = new Vector<String>();
		int intLen = 0;
		if (subVOs != null) {
			intLen = subVOs.length;
		}
		subVOs = NCLocator.getInstance().lookup(IPsndocwadocManageService.class).getCrtName(subVOs);
		for (int i = 0; i < intLen; i++) {
			PsndocWadocMainVO mainvo = htMainVO.get(subVOs[i].getPk_psndoc() + subVOs[i].getAssgid());
			if (mainvo == null) {
				// ���û�и��˵ļ�¼������һ������Hsshtable
				mainvo = new PsndocWadocMainVO();
				mainvo.setPsncode(subVOs[i].getPsnCode());
				mainvo.setPsnname(subVOs[i].getPsnName());
				mainvo.setDeptcode(subVOs[i].getDeptCode());
				mainvo.setDeptname(subVOs[i].getDeptName());
				mainvo.setPostname(subVOs[i].getPostName());
				mainvo.setPostserise(subVOs[i].getPostserise());
				mainvo.setPsnclname(subVOs[i].getPsnclname());
				mainvo.setJobname(subVOs[i].getJobname());
				mainvo.setPk_psndoc(subVOs[i].getPk_psndoc());
				mainvo.setPk_psnjob(subVOs[i].getPk_psnjob());
				mainvo.setClerkcode(subVOs[i].getClerkcode());
				mainvo.setPartflag(subVOs[i].getPartflag());
				mainvo.setAssgid(subVOs[i].getAssgid());

				// 20150806 xiejie3
				// �����ϲ���NCdp205398642��������Ϣά����ͷ����ӡ���ְ��֯���͡�ְ�����ֶΣ����ҿ��Զ����������,begin
				// by wangqim
				mainvo.setOrgName(subVOs[i].getOrgName());
				// end

				// ��¼��Ա������˳��
				vpkpsn.addElement(subVOs[i].getPk_psndoc() + subVOs[i].getAssgid());
			}
			if (subVOs[i].getWaflag() != null && subVOs[i].getWaflag().booleanValue()) {
				/*
				 * subVOs[i].setPk_wa_crt_showname(getPsndocWadocDao().getCrtName
				 * (subVOs[i].getPk_wa_prmlv(), subVOs[i].getPk_wa_seclv(),
				 * subVOs[i].getIsmultsec().booleanValue()));
				 */
				mainvo.getValues().put(subVOs[i].getPk_wa_item(), subVOs[i]);
			}
			htMainVO.put(subVOs[i].getPk_psndoc() + subVOs[i].getAssgid(), mainvo);

		}

		// ������Ա������˳�������ȡ����������
		vos = new PsndocWadocMainVO[vpkpsn.size()];
		for (int k = 0; k < vpkpsn.size(); k++) {
			vos[k] = htMainVO.get(vpkpsn.elementAt(k));
		}
		return vos;
	}

	/**
	 * �Ӳ��Ų�ѯ����
	 */
	@Override
	public PsndocWadocMainVO[] queryWadocMainDataByDept(LoginContext context, String deptInnercode,
			boolean containsSubDepts, String clerkcode, String psnname, String pk_wagrd, String pk_prmlv,
			String pk_seclv, String pk_org, String pk_dept, String psndocArr) throws BusinessException {
		String deptCondition = getDeptConditon(deptInnercode, containsSubDepts);
		if (!StringUtils.isEmpty(clerkcode)) {
			deptCondition = deptCondition + " and hi_psnjob.clerkcode like '%" + clerkcode + "%' ";
		}
		if (!StringUtils.isEmpty(psnname)) {
			deptCondition = deptCondition + " and " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + " like '%"
					+ psnname + "%' ";
		}
		if (!StringUtils.isEmpty(pk_wagrd)) {
			// NCdp205545958 Ա��н�ʹ��ɽڵ㣬����ѯ������н�ʱ�׼��ѡ������׼��ѯʱ����ѯ�������� --lizt
			// deptCondition = deptCondition +
			// " and pk_wa_grd = '"+pk_wagrd+"' ";
			String pk_wagrd_arr = pk_wagrd.replaceAll(",", "','");
			deptCondition = deptCondition + " and pk_wa_grd in ('" + pk_wagrd_arr + "') ";
		}
		if (!StringUtils.isEmpty(pk_prmlv)) {
			// NCdp205545958 Ա��н�ʹ��ɽڵ㣬����ѯ������н�ʱ�׼��ѡ������׼��ѯʱ����ѯ�������� --lizt
			// deptCondition = deptCondition +
			// " and pk_wa_prmlv = '"+pk_prmlv+"' ";
			String pk_prmlv_arr = pk_prmlv.replaceAll(",", "','");
			deptCondition = deptCondition + " and pk_wa_prmlv in ('" + pk_prmlv_arr + "') ";
		}
		if (!StringUtils.isEmpty(pk_seclv)) {
			// NCdp205545958 Ա��н�ʹ��ɽڵ㣬����ѯ������н�ʱ�׼��ѡ������׼��ѯʱ����ѯ�������� --lizt
			// deptCondition = deptCondition +
			// " and pk_wa_seclv = '"+pk_seclv+"' ";
			String pk_seclv_arr = pk_seclv.replaceAll(",", "','");
			deptCondition = deptCondition + " and pk_wa_seclv in ('" + pk_seclv_arr + "') ";
		}
		PsndocWadocMainVO[] vos = null;
		StringBuffer sbSelStr = new StringBuffer();
		sbSelStr.append(" select distinct ");
		sbSelStr.append("    hi_psndoc_wadoc.nmoney, ");
		sbSelStr.append("    bd_psndoc.code as psncode , ");
		sbSelStr.append("     " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  as psnname , ");
		sbSelStr.append("    org_dept.code as deptcode , ");
		sbSelStr.append("     " + SQLHelper.getMultiLangNameColumn("org_dept.name") + "  as deptname , ");
		sbSelStr.append("     " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  as psnclname , ");
		// guoqt��λ����
		sbSelStr.append("    " + SQLHelper.getMultiLangNameColumn("om_post.postname") + " as postname , ");
		sbSelStr.append("    om_postseries.postseriesname as postserise , ");
		sbSelStr.append("    om_job.jobname as jobname , ");

		sbSelStr.append("    bd_psndoc.pk_psndoc, ");
		sbSelStr.append("    hi_psnjob.pk_psnjob, ");
		sbSelStr.append("    hi_psnjob.clerkcode, ");
		sbSelStr.append("	 (CASE WHEN hi_psnjob.ismainjob ='N' THEN 'Y' ELSE 'N' END) AS partflag, ");
		sbSelStr.append("	 hi_psnjob.assgid, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_wa_item, ");
		sbSelStr.append("    hi_psndoc_wadoc.lastflag, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_wa_grd, ");
		sbSelStr.append("    hi_psndoc_wadoc.waflag, ");
		sbSelStr.append("    hi_psndoc_wadoc.workflowFlag, ");
		sbSelStr.append("    wa_criterion.pk_wa_crt, ");
		// sbSelStr.append("    wa_criterion.max_value as crt_max_value, ");
		// sbSelStr.append("    wa_criterion.min_value as crt_min_value, ");
		// sbSelStr.append("    wa_criterion.pk_wa_prmlv as prmlvpk, ");
		sbSelStr.append("    hi_psndoc_wadoc.criterionvalue, ");
		sbSelStr.append("    hi_psndoc_wadoc.crt_max_value as crt_max_value, ");
		sbSelStr.append("    hi_psndoc_wadoc.crt_min_value as crt_min_value, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_wa_prmlv as prmlvpk, ");

		sbSelStr.append("    hi_psndoc_wadoc.negotiation_wage, ");
		sbSelStr.append("    hi_psndoc_wadoc.pk_psndoc_sub, ");
		sbSelStr.append("    wa_grade.prmlv_money_sort,");
		sbSelStr.append("    wa_grade.seclv_money_sort,");
		sbSelStr.append("    wa_grade.ismultsec,");
		sbSelStr.append("    wa_grade.isrange, hi_psndoc_wadoc.recordnum ");
		// -add for V5.7 н���յ� start
		sbSelStr.append(",  " + SQLHelper.getMultiLangNameColumn("wa_grade.name")
				+ "  as pk_wa_grd_showname ,wa_grade.prmlv_money_sort , wa_grade.seclv_money_sort, wa_grade.ismultsec ");
		sbSelStr.append(", wa_prmlv.pk_wa_prmlv , wa_seclv.pk_wa_seclv , wa_criterion.pk_wa_crt,  wa_grade_ver.effect_flag, "
				+ SQLHelper.getMultiLangNameColumn("wa_item.name") + "  as pk_wa_item_showname  ");
		// -add for V5.7 н���յ� end

		StringBuffer tablestr = new StringBuffer();
		tablestr.append(" from  hi_psnjob");
		tablestr.append("    inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc   ");
		tablestr.append("    inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		tablestr.append("    left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		tablestr.append("    left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		tablestr.append("    left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		tablestr.append("    left outer join om_postseries on hi_psnjob.pk_postseries = om_postseries.pk_postseries ");
		tablestr.append("    left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");

		// ���NCdp205534430 ��Ա��ͬһ����������ְ������ְ������н������Ա��н�ʹ��ɲ�ѯ������������ start
		tablestr.append("    left outer  join hi_psndoc_wadoc on hi_psndoc_wadoc.pk_psndoc =   bd_psndoc.pk_psndoc ");
		// 2016-1-26 NCdp205578360 zhousze
		// ����Ա���в��ŵ������Ա��н�ʹ��ɲ鿴ʱ���б����ɿ������ݣ����ǲ鿴��������Ϊ�ա�
		// ���ڵĴ������б������鿴��������ݱ���һ�� begin
		tablestr.append("    and hi_psndoc_wadoc.pk_psnjob = hi_psnjob.pk_psnjob ");
		// end
		tablestr.append("	 and hi_psndoc_wadoc.assgid = hi_psnjob.assgid ");
		// end

		tablestr.append("         and hi_psndoc_wadoc.waflag = 'Y' and hi_psndoc_wadoc.LASTFLAG ='Y'");
		// tablestr.append("         and hi_psndoc_wadoc.pk_psnjob=hi_psnjob.pk_psnjob ");
		// tablestr.append("    left outer join wa_grade on hi_psndoc_wadoc.pk_wa_grd = wa_grade.pk_wa_grd ");
		// tablestr.append("    left outer join wa_criterion on hi_psndoc_wadoc.pk_wa_grd = wa_criterion.pk_wa_grd ");
		// tablestr.append("         and hi_psndoc_wadoc.pk_wa_crt =  wa_criterion.pk_wa_crt ");

		// -add for V5.7 н���յ� start
		tablestr.append("        left outer join wa_grade  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_grd = wa_grade.pk_wa_grd ");

		tablestr.append("        left outer join wa_prmlv  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_prmlv = wa_prmlv.pk_wa_prmlv  ");
		tablestr.append(getJoinTableStr(null));
		tablestr.append("        left outer join wa_seclv  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_seclv = wa_seclv.pk_wa_seclv  ");

		tablestr.append("        left outer join wa_grade_ver  ");
		tablestr.append("        on wa_grade_ver.pk_wa_grd = hi_psndoc_wadoc.pk_wa_grd   and wa_grade_ver.effect_flag = 'Y'  ");

		tablestr.append("        left outer join wa_criterion  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_grd = wa_criterion.pk_wa_grd and ");
		tablestr.append("        isnull(hi_psndoc_wadoc.pk_wa_seclv, 'not_have_pk_wa_seclv') = isnull(wa_criterion.pk_wa_seclv, 'not_have_pk_wa_seclv') and ");
		tablestr.append("        hi_psndoc_wadoc.pk_wa_prmlv = wa_criterion.pk_wa_prmlv and wa_criterion.pk_wa_gradever = wa_grade_ver.pk_wa_gradever ");

		tablestr.append("        left outer join wa_item  ");
		tablestr.append("        on hi_psndoc_wadoc.pk_wa_item = wa_item.pk_wa_item ");
		// -add for V5.7 н���յ� end

		StringBuffer sbWhere = new StringBuffer();

		sbWhere.append(" where ");
		if (!StringUtils.isEmpty(pk_org)) {
			sbWhere.append(" hi_psnjob.pk_org ='" + pk_org + "' and");
		}
		// 2016-1-13 NCdp205572868 zhousze
		// Ա�����ŵ�����ٻ�ԭ���ż�ְʱ��Ա��н�ʹ���ԭ���Ų鿴ʱ�����ܿ�����ְ�Ķ�������Ϣ�����ڵĴ����ǹ��˵��Ѿ���������ְ
		// �Ĺ�����¼������Ҫ��ʾ���� begin
		sbWhere.append(" hi_psnjob.endflag = 'N' and ");
		// end
		// �������Ϊ�������ѯ��ӵ�ǰ��¼�˵Ĳ������� �����Ϊ���������޸� begin ÿ�ſ�2015-11-04 ��У���޸�
		if (!containsSubDepts) {
			// guoqt
			// sbWhere.append(" hi_psnjob.pk_psndoc in ( select pk_psndoc from hi_psnjob where pk_dept ='"+pk_dept+"' ) and");
			// sbWhere.append(" hi_psndoc_wadoc.lastflag='Y' and ");
			sbWhere.append(" (hi_psnjob.pk_dept ='" + pk_dept + "'  )  and");
			// ����ƴ�ӵ�SQL �Ὣͬһ��֯ͬһ������ ͬʱ����ְ�ͼ�ְ���˵Ķ�Ͷ�ʼ�¼���� ����ɾ�� ���ǵ���������Ӱ�� ��˺��������޸�
			// ������2015-11-04
			// sbWhere.append(" (isnull(hi_psndoc_wadoc.partflag, '~')='~' or not exists (select 1 from hi_psnjob where hi_psnjob.ismainjob=isnull(hi_psndoc_wadoc.partflag, '~') and hi_psnjob.lastflag='Y' and hi_psnjob.pk_psndoc=hi_psndoc_wadoc.pk_psndoc and pk_dept = '"+pk_dept+"')) and");
			// sbWhere.append(" (hi_psndoc_wadoc.partflag not in (select ismainjob from hi_psnjob where pk_dept ='"+pk_dept+"' ) )   and");
			// sbWhere.append(" (hi_psndoc_wadoc.partflag is null or (hi_psndoc_wadoc.partflag not in (select ismainjob from hi_psnjob where pk_dept ='"+pk_dept+"' ) ) )and");

		}
		sbWhere.append("  hi_psnjob.pk_dept in ( select pk_dept from org_dept where substring (innercode,1,"
				+ deptInnercode.length() + ") = '" + deptInnercode + "') and ");
		sbWhere.append("  hi_psnorg.indocflag = 'Y' and hi_psnorg.lastflag = 'Y' ");
		// sbWhere.append("  and hi_psnjob.ismainjob = 'Y' ");
		// ��ѯģ����Ԥ������֯��ϵ�Ƿ����������Ĭ��ΪN
		if (deptCondition != null) {
			deptCondition = deptCondition.replace("pk_wa_grd", "hi_psndoc_wadoc.pk_wa_grd");
			deptCondition = deptCondition.replace("pk_wa_prmlv", "hi_psndoc_wadoc.pk_wa_prmlv");
			deptCondition = deptCondition.replace("pk_wa_seclv", "hi_psndoc_wadoc.pk_wa_seclv");
			sbWhere.append(" and hi_psnjob.pk_psndoc in (select pk_psndoc from hi_psnjob where (" + deptCondition
					+ ") and hi_psnjob.lastflag='Y')");
		} /*
		 * else { sbWhere.append("    and hi_psnorg.endflag = 'N' "); }
		 */

		String powerSql = WaPowerSqlHelper.getWaPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "hi_psnjob");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sbWhere.append(" and " + powerSql);
		}

		if (!StringUtils.isEmpty(psndocArr)) {
			sbWhere.append(" and bd_psndoc.pk_psndoc in (" + psndocArr + ")");
		}

		String orderStr = "order by org_dept.code,bd_psndoc.code  ,hi_psndoc_wadoc.pk_wa_item ,hi_psndoc_wadoc.recordnum ";

		sbSelStr.append(tablestr.toString());
		sbSelStr.append(sbWhere.toString());
		sbSelStr.append(orderStr);

		WaBusilogUtil.writeAdjustQueryBusiLog(context);

		// �õ����е���VO
		PsndocWadocVO[] subVOs = getPsndocWadocDao().executeQueryVOs(sbSelStr.toString(), PsndocWadocVO.class);
		// subVOs = filterQueryData(subVOs);
		Hashtable<String, PsndocWadocMainVO> htMainVO = new Hashtable<String, PsndocWadocMainVO>();
		Vector<String> vpkpsn = new Vector<String>();
		int intLen = 0;
		if (subVOs != null) {
			intLen = subVOs.length;
		}
		subVOs = NCLocator.getInstance().lookup(IPsndocwadocManageService.class).getCrtName(subVOs);
		for (int i = 0; i < intLen; i++) {
			PsndocWadocMainVO mainvo = htMainVO.get(subVOs[i].getPk_psndoc() + "partflag"
					+ (subVOs[i].getPartflag() == null ? "" : subVOs[i].getPartflag().toString()));
			if (mainvo == null) {
				// ���û�и��˵ļ�¼������һ������Hsshtable
				mainvo = new PsndocWadocMainVO();
				mainvo.setPsncode(subVOs[i].getPsnCode());
				mainvo.setPsnname(subVOs[i].getPsnName());
				mainvo.setDeptcode(subVOs[i].getDeptCode());
				mainvo.setDeptname(subVOs[i].getDeptName());
				mainvo.setPostname(subVOs[i].getPostName());
				mainvo.setPostserise(subVOs[i].getPostserise());
				mainvo.setPsnclname(subVOs[i].getPsnclname());
				mainvo.setJobname(subVOs[i].getJobname());
				mainvo.setPk_psndoc(subVOs[i].getPk_psndoc());
				mainvo.setPk_psnjob(subVOs[i].getPk_psnjob());
				mainvo.setClerkcode(subVOs[i].getClerkcode());
				mainvo.setPartflag(subVOs[i].getPartflag());

				// ��¼��Ա������˳��
				vpkpsn.addElement(subVOs[i].getPk_psndoc() + "partflag"
						+ (subVOs[i].getPartflag() == null ? "" : subVOs[i].getPartflag().toString()));
			}
			if (subVOs[i].getWaflag() != null && subVOs[i].getWaflag().booleanValue()) {
				/*
				 * subVOs[i].setPk_wa_crt_showname(getPsndocWadocDao().getCrtName
				 * (subVOs[i].getPk_wa_prmlv(), subVOs[i].getPk_wa_seclv(),
				 * subVOs[i].getIsmultsec().booleanValue()));
				 */
				mainvo.getValues().put(subVOs[i].getPk_wa_item(), subVOs[i]);
			}
			htMainVO.put(subVOs[i].getPk_psndoc() + "partflag"
					+ (subVOs[i].getPartflag() == null ? "" : subVOs[i].getPartflag().toString()), mainvo);

		}
		// ������Ա������˳�������ȡ����������
		vos = new PsndocWadocMainVO[vpkpsn.size()];
		for (int k = 0; k < vpkpsn.size(); k++) {
			vos[k] = htMainVO.get(vpkpsn.elementAt(k));
		}
		return vos;

	}

	private String getDeptConditon(String deptInnercode, boolean containsSubDepts) {
		if (containsSubDepts) {
			return " hi_psnjob.pk_dept in ( select pk_dept from org_dept where substring (innercode,1,"
					+ deptInnercode.length() + ") = '" + deptInnercode + "') ";

		} else {
			return " hi_psnjob.pk_dept = (select pk_dept from org_dept where innercode = '" + deptInnercode + "' ) ";
		}

	}

	/**
	 * ���˲�ѯ��������Ա�����˹��򣨷��ű�־�����Ҽ�¼�����С�����£�//TODO Ӧ����Sqlʵ�ֵ�
	 * 
	 * @author xuhw on 2010-8-4
	 * @param subVOs
	 * @return
	 */
	private PsndocWadocVO[] filterQueryData(PsndocWadocVO[] subVOs) {
		List<PsndocWadocVO> lisPsndocVo = new ArrayList<PsndocWadocVO>();

		String strKey = null;
		String strKeyCurrent = null;
		Integer recordNum = null;
		for (PsndocWadocVO subVO : subVOs) {
			recordNum = subVO.getRecordnum();
			if (recordNum == null) {
				lisPsndocVo.add(subVO);
				strKey = strKeyCurrent;
				continue;
			}

			strKeyCurrent = subVO.getPk_psndoc() + subVO.getPk_wa_item();

			if (strKeyCurrent.equals(strKey)) {
				strKey = strKeyCurrent;
				continue;
			}
			lisPsndocVo.add(subVO);

			strKey = strKeyCurrent;
		}

		return lisPsndocVo.toArray(new PsndocWadocVO[0]);
	}

	/**
	 * ��VO���������ֵ�������ݿ⡣
	 * 
	 * @param psndocWadoc
	 */
	@Override
	public void updatePsndocWadoc(PsndocWadocVO psndocWadoc) throws BusinessException {
		try {
			// У����Ч����
			validateUsedate(psndocWadoc, Boolean.FALSE);
			// У����Ա��н���Ƿ��ںϷ��ķ�Χ�ڡ�
			validateMoneyLimit(psndocWadoc, null);

			// 2016-12-2 zhousze н�ʼ��ܣ����ﴦ��������Ϣά���ֱ���������ʱ�����ݼ��� begin
			psndocWadoc.setNmoney(psndocWadoc.getNmoney() == null ? null : new UFDouble(SalaryEncryptionUtil
					.encryption(psndocWadoc.getNmoney().toDouble())));
			psndocWadoc.setCriterionvalue(psndocWadoc.getCriterionvalue() == null ? null : new UFDouble(
					SalaryEncryptionUtil.encryption(psndocWadoc.getCriterionvalue().toDouble())));
			// end

			getPsndocWadocDao().getBaseDao().updateVO(psndocWadoc);
			getPsndocWadocDao().updatePreEnddate(psndocWadoc);

			// ͬ��hi���ݡ�����
			BTOBXVOConversionUtility.psndocWadocSaveToWainfoVO(psndocWadoc);
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			throw new BusinessException(ex.getMessage(), ex);
		}

		// if (psndocWadoc.getLastflag().booleanValue() &&
		// psndocWadoc.getWaflag().booleanValue()) {
		// // ��������Ŀ�����¼�¼���ҷ���
		// getPsndocWadocDao().updateWaFlag(psndocWadoc);
		// }
	}

	// --------------------------------------------------------------------------

	/**
	 * �����ʣ�����Ч���ڡ����ڻ���ڼ�ʱ�䷶Χ������û��У�顣<BR>
	 * �����жԴ˵���Ҫ��:�ڶ����������ύʱ��ϵͳ�Զ�У����Ч���ڵĺϷ��ԣ�<BR>
	 * ������뵥���е���Ч�������ڻ���ڼ��ʱ�䷶Χ��ϵͳҪ������ʾ���Ҳ������ύ��<BR>
	 */
	private void validateUsedate(UFLiteralDate usedate) throws BusinessException {
		if (usedate != null && usedate.toString().length() > 0) {
			// xiejie3 2015-1-7 11:25:01 ����IConfigFileService�ӿ��Ѳ����ڣ�����
			// aIConfigFileService.isValidDate������v633ʵ����ֱ�ӷ��ص�true��������ο���ֱ��ע��
			// IConfigFileService aIConfigFileService = (IConfigFileService)
			// NCLocator.getInstance().lookup(
			// IConfigFileService.class.getName());
			// try {
			// boolean isin = aIConfigFileService.isValidDate(new
			// UFDate(usedate.getMillis()));
			// if (!isin) {
			// Logger.error("��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�.");
			// throw new
			// BusinessException(ResHelper.getString("60130adjmtc","060130adjmtc0218")/*@res
			// "��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�."*/);
			// }
			// } catch (BusinessException ex) {
			// Logger.error("��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�.", ex);
			// throw new
			// BusinessException(ResHelper.getString("60130adjmtc","060130adjmtc0218")/*@res
			// "��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�."*/);
			// }
		} else {
			Logger.debug("��Ч����Ϊ��, ���޸�.");
			throw new BusinessException(ResHelper.getString("60130adjapprove", "060130adjapprove0033")/*
																									 * @
																									 * res
																									 * "��Ч����Ϊ��, ���޸�."
																									 */);
		}
	}

	// --------------------------------------------------------------------------

	/**
	 * �����ʣ�����Ч���ڡ����ڻ���ڼ�ʱ�䷶Χ������û��У�顣<BR>
	 * �����жԴ˵���Ҫ��:�ڶ����������ύʱ��ϵͳ�Զ�У����Ч���ڵĺϷ��ԣ�<BR>
	 * ������뵥���е���Ч�������ڻ���ڼ��ʱ�䷶Χ��ϵͳҪ������ʾ���Ҳ������ύ��<BR>
	 */
	private void validateUsedate(PsndocWadocVO psndocWadocVO, Boolean isAdd) throws BusinessException {
		UFLiteralDate usedate = psndocWadocVO.getBegindate();
		if (usedate != null && usedate.toString().length() > 0) {
			// xiejie3
			// IConfigFileService aIConfigFileService = (IConfigFileService)
			// NCLocator.getInstance().lookup(
			// IConfigFileService.class.getName());
			// try {
			// boolean isin = aIConfigFileService.isValidDate(new
			// UFDate(usedate.getMillis()));
			// if (!isin) {
			// Logger.error("��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�.");
			// throw new
			// BusinessException(ResHelper.getString("60130adjmtc","060130adjmtc0218")/*@res
			// "��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�."*/);
			// }
			// } catch (BusinessException ex) {
			// Logger.error("��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�.", ex);
			// throw new
			// BusinessException(ResHelper.getString("60130adjmtc","060130adjmtc0218")/*@res
			// "��Ч���ڲ���ϵͳ����Ļ���ڼ�֮��, ���޸�."*/);
			// }
		} else {
			Logger.debug("��Ч����Ϊ��, ���޸�.");
			throw new BusinessException(ResHelper.getString("60130adjapprove", "060130adjapprove0033")/*
																									 * @
																									 * res
																									 * "��Ч����Ϊ��, ���޸�."
																									 */);
		}
		if (getPsndocWadocDao().validateUsedate(psndocWadocVO, isAdd)) {
			Integer recordnum = psndocWadocVO.getRecordnum();
			if (isAdd && recordnum.equals(Integer.valueOf(0))) {
				Logger.error("��ʼ���ڲ���������һ����¼�Ľ�ֹ����/��ֹ����, ���޸�.");
				// throw new
				// BusinessException(MessageFormat.format(ResHelper.getString("60130adjmtc","060130adjmtc0235")
				// /*@res
				// "��¼��{0} н����Ŀ��{1} ��ʼ���ڲ���������һ����¼�Ľ�ֹ����/��ʼ����, ���޸�."*/,psndocWadocVO.getPsnName()==null?"":ResHelper.getString("60130adjmtc","060130adjmtc0236")
				// /*@res
				// "��Ա��"*/+psndocWadocVO.getPsnName(),psndocWadocVO.getPk_wa_item_showname()));
				// 2015-11-5 zhousze
				// ����getPk_wa_item_showname,�Ȳ�ѯ��ǰVO�е�PK_WA_ITEMȡ��ѯ��Ӧ��name�����������У�顣begin
				String pk_wa_item_showname = getPsndocWadocDao().queryShownameByPk(psndocWadocVO.getPk_wa_item());
				throw new BusinessException(MessageFormat.format(
						ResHelper.getString("60130adjmtc", "060130adjmtc0235"), pk_wa_item_showname));
			} else {
				Logger.error("��ʼ���ڲ���������һ����¼�Ľ�ֹ����/��ֹ���ڲ���������һ����¼�Ŀ�ʼ����, ���޸�.");
				String pk_wa_item_showname = getPsndocWadocDao().queryShownameByPk(psndocWadocVO.getPk_wa_item());
				throw new BusinessException(MessageFormat.format(
						ResHelper.getString("60130adjmtc", "060130adjmtc0237"), pk_wa_item_showname));
				// end
			}
		}
	}

	/**
	 * У������������Ľ��ĺϷ���<BR>
	 * 1) ��н�ʱ�׼�����н��������0 < =��׼���� <= ����������Ľ�� <= ��׼����<BR>
	 * 1) ��н�ʱ�׼���ǿ��н������������������Ľ�� == н�ʱ�׼ >= 0<BR>
	 * 3) ̸�й��ʣ�����������Ľ�� >= 0 <BR>
	 * 
	 * @author xuhw on 2009-12-29
	 */
	public void validateMoneyLimit(PsndocWadocVO wadocVO, Integer intRow) throws BusinessException {
		if (wadocVO == null) {
			return;
		}
		UFDoubleCompare doubleCompare = new UFDoubleCompare();
		// ������
		UFDouble money = null;
		// �����׼PK
		// String strPkCrt = null;
		String strPKPrmlv = null;
		String strPKSeclv = null;
		// �Ƿ���
		UFBoolean isRange = null;
		// �Ƿ�̸��
		Boolean bnNegotiation = null;
		WaCriterionVO criterionVo = null;
		money = wadocVO.getNmoney();
		// strPkCrt = wadocVO.getPk_wa_crt();
		strPKPrmlv = wadocVO.getPk_wa_prmlv();
		strPKSeclv = wadocVO.getPk_wa_seclv();
		isRange = wadocVO.getIsrange();
		bnNegotiation = wadocVO.getNegotiation_wage().booleanValue();

		if (doubleCompare.lessThan(money, new UFDouble(0))) {
			Logger.debug("����Ϊ����");
			throw new BusinessException(ResHelper.getString("60130adjmtc", "060130adjmtc0219")/*
																							 * @
																							 * res
																							 * "  ����Ϊ����, ���޸�."
																							 */);
		}
		// ��̸��
		if (bnNegotiation) {
			return;
		}
		if (StringUtils.isEmpty(strPKPrmlv)) {
			throw new BusinessException(ResHelper.getString("60130adjapply", "060130adjapply0111"));
		}
		criterionVo = ((NCLocator.getInstance().lookup(IWaGradeService.class))).getCrierionVOByPrmSec(
				wadocVO.getBegindate(), strPKPrmlv, strPKSeclv);

		if (criterionVo != null) {
			// 2016-12-2 zhousze н�ʼ��ܣ����ﴦ��������Ϣά���޸ı���ʱ��У���߼��������� begin
			criterionVo.setCriterionvalue(new UFDouble(
					SalaryDecryptUtil.decrypt((criterionVo.getCriterionvalue() == null ? new UFDouble(0) : criterionVo
							.getCriterionvalue()).toDouble())));
			criterionVo.setMax_value(new UFDouble(
					SalaryDecryptUtil.decrypt((criterionVo.getMax_value() == null ? new UFDouble(0) : criterionVo
							.getMax_value()).toDouble())));
			criterionVo.setMin_value(new UFDouble(
					SalaryDecryptUtil.decrypt((criterionVo.getMin_value() == null ? new UFDouble(0) : criterionVo
							.getMin_value()).toDouble())));
			// end

			// ���ǿ��

			String strErrorMessage = null;
			if (!isRange.booleanValue()) {
				if (!doubleCompare
						.equals(criterionVo.getCriterionvalue().div(new UFDouble(1), money.getPower()), money)) {
					strErrorMessage = ResHelper.getString("60130adjmtc", "060130adjmtc0220")/*
																							 * @
																							 * res
																							 * "�����н�ʱ�׼�����, ���޸�."
																							 */;
				}
			} else {
				if (doubleCompare.lessThan(money, criterionVo.getMin_value().div(new UFDouble(1), money.getPower()))) {

					strErrorMessage = MessageFormat.format(ResHelper.getString("60130adjmtc", "060130adjmtc0221")/*
																												 * @
																												 * res
																												 * "���н�����������[{0}]����С��н�ʱ�׼������[{1}], ���޸�."
																												 */,
							money, criterionVo.getMin_value().div(new UFDouble(1), money.getPower()));
				} else if (doubleCompare.lessThan(criterionVo.getMax_value().div(new UFDouble(1), money.getPower()),
						money)) {
					strErrorMessage = MessageFormat.format(ResHelper.getString("60130adjmtc", "060130adjmtc0222")/*
																												 * @
																												 * res
																												 * "���н�����������[{0}]���ܴ���н�ʱ�׼������[{1}], ���޸�."
																												 */,
							money, criterionVo.getMax_value().div(new UFDouble(1), money.getPower()));
				}
			}

			if (!StringUtils.isEmpty(strErrorMessage)) {
				strErrorMessage = intRow == null ? strErrorMessage : intRow
						+ ResHelper.getString("60130adjmtc", "060130adjmtc0223")/*
																				 * @
																				 * res
																				 * "��:"
																				 */
						+ strErrorMessage;
				Logger.debug(strErrorMessage);
				throw new BusinessException(strErrorMessage);
			}
		}
	}

	// -----------���������ݵ���---------------------
	/**
	 * ���������ݳ־û�
	 * 
	 * @author xuhw on 2010-4-20
	 * @throws BusinessException
	 * @see nc.itf.hr.wa.IPsndocwadocManageService#importData2DB(nc.vo.hi.wadoc.PsndocWadocVO[])
	 */
	@Override
	public PsndocWadocVO[] importData2DB(PsndocWadocVO[] psndocWadocVOs, LoginContext context) throws BusinessException {
		return extendInertVoinfo(psndocWadocVOs, context);
	}

	/**
	 * �Ե�������ݼ����з�������<BR>
	 * 1)�жϵ������ݵĸ���Ŀ�Ƿ�Ϸ�<BR>
	 * �Ϸ����������ݿ�<BR>
	 * �Ƿ����ж�ԭ��洢�����Ը��û�<BR>
	 * 2)���� �������Ϣ<BR>
	 * 
	 * @author xuhw on 2010-4-21
	 * @param psndocWadocVOs
	 * @return
	 * @throws BusinessException
	 */
	private PsndocWadocVO[] extendInertVoinfo(PsndocWadocVO[] psndocWadocVOs, LoginContext context)
			throws BusinessException {
		// ���ݺϷ��ļ�¼����
		List<PsndocWadocVO> successImportList = new ArrayList<PsndocWadocVO>();
		// ���Ϸ��ļ�¼����
		List<PsndocWadocVO> failtureImportList = new ArrayList<PsndocWadocVO>();
		PsndocWadocVO aftProVO = null;
		if (ArrayUtils.isEmpty(psndocWadocVOs)) {
			return psndocWadocVOs;
		}
		int inttt = 0;
		for (PsndocWadocVO psndocWadocVO : psndocWadocVOs) {
			Logger.debug(new Date() + " : " + inttt++);
			try {

				aftProVO = getPsndocWadocDao().expandInertVOInfo(psndocWadocVO, context);
				if (aftProVO == null) {
					// added by zengcheng
					// 2012-07-12��û�в鵽�ˣ�����Ҫ���벻�Ϸ��ļ�¼��һ�㶼����Ϊ��̸��н�꣬Ȼ������ϼ����Ĺ涨
					failtureImportList.add(psndocWadocVO);
					continue;
				} else if (aftProVO.getNegotiation_wage() != null && aftProVO.getNegotiation_wage().booleanValue()) {
					aftProVO.setPk_wa_grd(null);
					aftProVO.setPk_wa_grd_showname(null);
				}
				validateUsedate(aftProVO, Boolean.TRUE);

			} catch (DAOException daoex) {
				// ����ʱ�����쳣����¼���������Ŵ�����һ��
				failtureImportList.add(psndocWadocVO);
				continue;
			} catch (BusinessException busiex) {
				psndocWadocVO.setReason(busiex.getMessage());
				failtureImportList.add(psndocWadocVO);
				continue;
			}

			if (aftProVO == null) {
				failtureImportList.add(psndocWadocVO);
				continue;
			}
			successImportList.add(aftProVO);
		}
		// �洢ϵͳ��ԭ�����µ������ڸı��VO
		List<PsndocWadocVO> sysChangeVOList = new ArrayList<PsndocWadocVO>();
		PsndocWadocVO[] successvos = successImportList.toArray(new PsndocWadocVO[0]);

		NCLocator.getInstance().lookup(IPsndocwadocManageService.class).insertArray4BatchImport(successvos);
		// �Ϸ������ݽ��г־û�����
		// insertArray4BatchImport(successvos);

		// �������±�־�ͼ�¼number
		Map<String, Integer> recortNumberMap = setLastFlagAndRecordNumber(successvos, sysChangeVOList, context);
		Iterator<String> tterator = recortNumberMap.keySet().iterator();
		while (tterator.hasNext()) {
			String strPsncodeItemname = tterator.next();
			String[] psncodeItemname = strPsncodeItemname.split("/");
			// 2015-11-23 zhousze
			// �������һ��������assgid��������ͬһ���˶����ְʱ��ͬһ��н�ʱ�׼ʱ���������֤����ʱ��������ȷ begin
			getPsndocWadocDao().updateRecordNumberAndLastFlag4Import(psncodeItemname[0], psncodeItemname[1],
					psncodeItemname[2]);
			// end
		}

		// ͬ����Ա
		BTOBXVOConversionUtility.psndocWadocSaveToWainfoVO(successvos);

		return queryWaFailedReason(failtureImportList, context);
	}

	/**
	 * ���õ������ݼ������±�־<BR>
	 * <BR>
	 * ��ͬ�˵���ͬн����Ŀ��н�������ļ�¼��ѡ���±�־��<BR>
	 * 
	 * @author xuhw on 2010-4-27
	 * @param psndocwadocvos
	 * @throws BusinessException
	 */
	private Map<String, Integer> setLastFlagAndRecordNumber(PsndocWadocVO[] psndocwadocvos,
			List<PsndocWadocVO> sysChangeVOList, LoginContext context) throws BusinessException {
		// �洢һ��һ����Ŀ���뼸����¼
		Map<String, Integer> mapVO2Number = new HashMap<String, Integer>();
		if (psndocwadocvos == null || psndocwadocvos.length == 0) {
			return mapVO2Number;
		}

		// ȡ��ϵͳ�еı���֯�µ����¼�¼
		Map<String, PsndocWadocVO> sysPsnDocWadocMap = queryLastFlagVOs(context);

		// ĳ��ĳ��Ŀ�˴���������
		Integer intCnt = 0;
		String strLastRecort = null;
		String strCurrentRecort = null;
		PsndocWadocVO sysVO = null;

		// ֻ����һ����¼�ĳ���
		if (psndocwadocvos.length == 1) {
			// ��Ч���ڵ�У��
			// validateUsedate(psndocwadocvos[0],new Boolean(true));
			// 2015-12-3 NCdp205552918 zhousze
			// ��Ա�ֱ���������֯��ְ������ʱ����һ����֯�ĵ�н��ֹ����û�л�д�����������ڸ�MAP�е�KEY
			// ƴ�յĲ����������󣬶���ͬһ��Աͳһ��֯������ż�ְʱ����һ������н����ĿA������һ������н����ĿA��Cʱ����������Ͳ��������ˣ�
			// ����������Ĵ���������һ��assgid��ǿ�ж� begin
			sysVO = sysPsnDocWadocMap.get(psndocwadocvos[0].getPsnCode() + psndocwadocvos[0].getPk_wa_item_showname()
					+ psndocwadocvos[0].getAssgid());
			// end
			setLastFlagValue(sysVO, psndocwadocvos[0], sysChangeVOList);
			psndocwadocvos[0].setRecordnum(0);
			// 2015-11-23 zhousze
			// �������һ��������assgid��������ͬһ���˶����ְʱ��ͬһ��н�ʱ�׼ʱ���������֤����ʱ��������ȷ
			mapVO2Number.put(psndocwadocvos[0].getPk_psndoc() + "/" + psndocwadocvos[0].getPk_wa_item() + "/"
					+ psndocwadocvos[0].getAssgid(), 1);
			// end
			return mapVO2Number;
		}

		// �������ϼ�¼�ĳ���
		PsndocWadocVO nextPsndocwadocvos = null;
		PsndocWadocVO currentPsndocwadocvos = null;
		for (int i = psndocwadocvos.length - 1; i >= 0; i--) {
			// ��ǰ��¼
			currentPsndocwadocvos = psndocwadocvos[i];
			// ��Ч���ڵ�У��
			// validateUsedate(currentPsndocwadocvos,new Boolean(true));
			currentPsndocwadocvos.setRecordnum(intCnt);
			if (currentPsndocwadocvos.getLastflag() == null) {
				currentPsndocwadocvos.setLastflag(UFBoolean.valueOf(false));
			}
			// 2015-12-3 NCdp205552918 zhousze
			// ��Ա�ֱ���������֯��ְ������ʱ����һ����֯�ĵ�н��ֹ����û�л�д�����������ڸ�MAP�е�KEY
			// ƴ�յĲ����������󣬶���ͬһ��Աͳһ��֯������ż�ְʱ����һ������н����ĿA������һ������н����ĿA��Cʱ����������Ͳ��������ˣ�
			// ����������Ĵ���������һ��assgid��ǿ�ж� begin
			strCurrentRecort = currentPsndocwadocvos.getPsnCode() + currentPsndocwadocvos.getPk_wa_item_showname()
					+ currentPsndocwadocvos.getAssgid();
			// end
			// ��һ����¼
			if (i != 0) {
				nextPsndocwadocvos = psndocwadocvos[i - 1];
				// 2015-12-3 NCdp205552918 zhousze
				// ��Ա�ֱ���������֯��ְ������ʱ����һ����֯�ĵ�н��ֹ����û�л�д�����������ڸ�MAP�е�KEY
				// ƴ�յĲ����������󣬶���ͬһ��Աͳһ��֯������ż�ְʱ����һ������н����ĿA������һ������н����ĿA��Cʱ����������Ͳ��������ˣ�
				// ����������Ĵ���������һ��assgid��ǿ�ж� begin
				strLastRecort = nextPsndocwadocvos.getPsnCode() + nextPsndocwadocvos.getPk_wa_item_showname()
						+ nextPsndocwadocvos.getAssgid();
				// end
			}

			// �����¼�����һ�������
			if (i == psndocwadocvos.length - 1) {
				sysVO = sysPsnDocWadocMap.get(strCurrentRecort);
				setLastFlagValue(sysVO, currentPsndocwadocvos, sysChangeVOList);
				// ��ǰһ������ͬ
				if (!strCurrentRecort.equals(strLastRecort)
						|| (nextPsndocwadocvos.getAssgid() != currentPsndocwadocvos.getAssgid())) {
					sysVO = sysPsnDocWadocMap.get(strLastRecort);
					setLastFlagValue(sysVO, nextPsndocwadocvos, sysChangeVOList);
					mapVO2Number.put(
					// 2015-11-23 zhousze
					// �������һ��������assgid��������ͬһ���˶����ְʱ��ͬһ��н�ʱ�׼ʱ���������֤����ʱ��������ȷ
							currentPsndocwadocvos.getPk_psndoc() + "/" + currentPsndocwadocvos.getPk_wa_item() + "/"
									+ currentPsndocwadocvos.getAssgid(), intCnt + 1);
					// end
					intCnt = 0;
				} else {
					intCnt++;
				}
				continue;
			}
			if (i == 0) {
				if (intCnt != 0) {
					mapVO2Number.put(
					// 2015-11-23 zhousze
					// �������һ��������assgid��������ͬһ���˶����ְʱ��ͬһ��н�ʱ�׼ʱ���������֤����ʱ��������ȷ
							currentPsndocwadocvos.getPk_psndoc() + "/" + currentPsndocwadocvos.getPk_wa_item() + "/"
									+ currentPsndocwadocvos.getAssgid(), intCnt + 1);
					// end
				} else {
					sysVO = sysPsnDocWadocMap.get(strCurrentRecort);
					setLastFlagValue(sysVO, currentPsndocwadocvos, sysChangeVOList);
					mapVO2Number.put(
					// 2015-11-23 zhousze
					// �������һ��������assgid��������ͬһ���˶����ְʱ��ͬһ��н�ʱ�׼ʱ���������֤����ʱ��������ȷ
							currentPsndocwadocvos.getPk_psndoc() + "/" + currentPsndocwadocvos.getPk_wa_item() + "/"
									+ currentPsndocwadocvos.getAssgid(), intCnt + 1);
					// end
				}

				return mapVO2Number;
			}

			if (!strCurrentRecort.equals(strLastRecort)) {
				sysVO = sysPsnDocWadocMap.get(strLastRecort);
				setLastFlagValue(sysVO, nextPsndocwadocvos, sysChangeVOList);
				// 2015-11-23 zhousze
				// �������һ��������assgid��������ͬһ���˶����ְʱ��ͬһ��н�ʱ�׼ʱ���������֤����ʱ��������ȷ
				mapVO2Number.put(currentPsndocwadocvos.getPk_psndoc() + "/" + currentPsndocwadocvos.getPk_wa_item()
						+ "/" + currentPsndocwadocvos.getAssgid(), intCnt + 1);
				// end
				intCnt = 0;
				continue;
			}

			intCnt++;
		}
		return mapVO2Number;
	}

	private void setLastFlagValue(PsndocWadocVO sysVO, PsndocWadocVO nextPsndocwadocvos,
			List<PsndocWadocVO> sysChangeVOList) {
		if (sysVO == null) {
			nextPsndocwadocvos.setLastflag(UFBoolean.valueOf(true));
		} else if (sysVO != null && !sysVO.getBegindate().after(nextPsndocwadocvos.getBegindate())) {
			sysVO.setLastflag(UFBoolean.valueOf(false));
			nextPsndocwadocvos.setLastflag(UFBoolean.valueOf(true));
			sysChangeVOList.add(sysVO);
		} else {
			nextPsndocwadocvos.setLastflag(UFBoolean.valueOf(false));
		}
	}

	/**
	 * ��ѯ����<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-4-22
	 * @param failtureImportList
	 * @param content
	 * @return
	 * @throws BusinessException
	 */
	private PsndocWadocVO[] queryWaFailedReason(List<PsndocWadocVO> failtureImportList, LoginContext context)
			throws BusinessException {
		try {
			List<PsndocWadocVO> vNotInBdpsndocByUseDate = removeFailedInUseDate(failtureImportList);
			// Ա����Ϣ���޴�Ա���������Ա
			List<PsndocWadocVO> vNotInBdpsndocByCode = checkFailedInResult(failtureImportList, getPsndocWadocDao()
					.queryInBdpsndocByCode(failtureImportList, context));
			// Ա����Ϣ���޴�Ա���������Ա
			List<PsndocWadocVO> vNotInBdpsndocByItem = checkFailedItemInResult(failtureImportList, getPsndocWadocDao()
					.queryInItemByName(failtureImportList, context));
			// ʣ����Ҫ��һ���жϵ���Ա
			List<PsndocWadocVO> vNotInBdpsndocByGrade = checkFailedGradeInResult(failtureImportList,
					getPsndocWadocDao().queryInGradeByName(failtureImportList, context));
			failtureImportList = new ArrayList<PsndocWadocVO>();
			failtureImportList.addAll(vNotInBdpsndocByUseDate);
			failtureImportList.addAll(vNotInBdpsndocByCode);
			failtureImportList.addAll(vNotInBdpsndocByItem);
			failtureImportList.addAll(vNotInBdpsndocByGrade);
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage(), ex);
		}
		return failtureImportList.toArray(new PsndocWadocVO[0]);
	}

	private List<PsndocWadocVO> removeFailedInUseDate(List<PsndocWadocVO> failtureImportList) {
		List<PsndocWadocVO> itemReasonPersons = new ArrayList<PsndocWadocVO>();
		List<PsndocWadocVO> otherReasonPsn = new ArrayList<PsndocWadocVO>();
		for (int i = 0; i < failtureImportList.size(); i++) {
			Logger.debug(new Date() + " :removeFailedInUseDate ----> + " + i);
			PsndocWadocVO psndocWadocVo = failtureImportList.get(i);
			psndocWadocVo.setError_flag(BEGINDATE);
			itemReasonPersons.add(psndocWadocVo);

			if (psndocWadocVo.getReason() == null) {
				itemReasonPersons.remove(psndocWadocVo);
				otherReasonPsn.add(psndocWadocVo);
			}

		}

		failtureImportList.clear();// removeAll(failtureImportList);
		failtureImportList.addAll(otherReasonPsn);
		return itemReasonPersons;
	}

	/**
	 * �ӽ�����޳��Ѿ��ܹ��жϴ���ԭ�����Ա����Ա
	 * 
	 * @param results
	 *            ��ȷ�ϴ���ԭ�����Ա
	 * @param inQueryResult
	 *            �Ѿ����ڵ���Ա
	 * @return �޳����Ѿ�������Ա�Ľ��
	 */
	private List<PsndocWadocVO> checkFailedInResult(List<PsndocWadocVO> failtureImportList,
			PsndocWadocVO[] inQueryResult) {

		List<PsndocWadocVO> psnReasonPersons = new ArrayList<PsndocWadocVO>();
		List<PsndocWadocVO> otherReasonPsn = new ArrayList<PsndocWadocVO>();

		int length = 0;
		if (!ArrayUtils.isEmpty(inQueryResult)) {
			length = inQueryResult.length;
		}
		for (int i = 0; i < failtureImportList.size(); i++) {
			Logger.debug(new Date() + " : checkFailedInResult ----> + " + i);
			PsndocWadocVO psndocWadocVo = failtureImportList.get(i);
			psndocWadocVo.setReason(ResHelper.getString("60130adjmtc", "060130adjmtc0224", psndocWadocVo.getPsnCode())
			/* @res "Ա����Ϣ�б���Ϊ[{0}]��Ա�������ڣ�\n" */);
			psndocWadocVo.setError_flag(PSNCODE);
			psnReasonPersons.add(psndocWadocVo);
			for (int j = 0; j < length; j++) {
				String psncode = inQueryResult[j].getPsnCode();
				String psnName = inQueryResult[j].getPsnName();
				if (psncode.equals(psndocWadocVo.getPsnCode())) {
					if (!psnName.equals(psndocWadocVo.getPsnName())) {
						psndocWadocVo.setReason(ResHelper.getString("60130adjmtc", "060130adjmtc0226",
								psndocWadocVo.getPsnName())
						/* @res "Ա����Ϣ��������Ϊ[{0}]��Ա�������ڣ�\n" */);
						psndocWadocVo.setError_flag(PSNNAME);
					} else {
						psnReasonPersons.remove(psndocWadocVo);
						psndocWadocVo.setReason(null);
						otherReasonPsn.add(psndocWadocVo);
					}
				}
			}
		}

		failtureImportList.clear();// removeAll(failtureImportList);
		failtureImportList.addAll(otherReasonPsn);
		return psnReasonPersons;
	}

	/**
	 * �ӽ�����޳��Ѿ��ܹ��жϴ���ԭ�����Ա����Ա
	 * 
	 * @param results
	 *            ��ȷ�ϴ���ԭ�����Ա
	 * @param inQueryResult
	 *            �Ѿ����ڵ���Ա
	 * @return �޳����Ѿ�������Ա�Ľ��
	 */
	private List<PsndocWadocVO> checkFailedItemInResult(List<PsndocWadocVO> failtureImportList,
			PsndocWadocVO[] inQueryResult) {

		List<PsndocWadocVO> itemReasonPersons = new ArrayList<PsndocWadocVO>();
		List<PsndocWadocVO> otherReasonPsn = new ArrayList<PsndocWadocVO>();

		int length = 0;
		if (!ArrayUtils.isEmpty(inQueryResult)) {
			length = inQueryResult.length;
		}
		for (int i = 0; i < failtureImportList.size(); i++) {
			Logger.debug(new Date() + " :checkFailedItemInResult ----> + " + i);
			PsndocWadocVO psndocWadocVo = failtureImportList.get(i);
			psndocWadocVo
					.setReason(ResHelper.getString("60130adjmtc", "060130adjmtc0227")/*
																					 * @
																					 * res
																					 * "����н����Ŀ��������Ϊ["
																					 */
							+ psndocWadocVo.getPk_wa_item_showname()
							+ ResHelper.getString("60130adjmtc", "060130adjmtc0228")/*
																					 * @
																					 * res
																					 * "]��н����Ŀ��\n"
																					 */);
			psndocWadocVo.setError_flag(WA_ITEM);
			itemReasonPersons.add(psndocWadocVo);
			for (int j = 0; j < length; j++) {
				String waitem = inQueryResult[j].getPk_wa_item_showname();
				if (waitem.equals(psndocWadocVo.getPk_wa_item_showname())) {
					itemReasonPersons.remove(psndocWadocVo);
					psndocWadocVo.setReason(ResHelper.getString("60130adjmtc", "060130adjmtc0229")/*
																								 * @
																								 * res
																								 * "����ԭ��(н�������ڣ�н�������ڣ�н�ʽ����н�ʱ�׼��һ��)��\n"
																								 */);
					otherReasonPsn.add(psndocWadocVo);
				}
			}
		}

		failtureImportList.clear();// removeAll(failtureImportList);
		failtureImportList.addAll(otherReasonPsn);
		return itemReasonPersons;
	}

	/**
	 * �ӽ�����޳��Ѿ��ܹ��жϴ���ԭ�����Ա����Ա
	 * 
	 * @param results
	 *            ��ȷ�ϴ���ԭ�����Ա
	 * @param inQueryResult
	 *            �Ѿ����ڵ���Ա
	 * @return �޳����Ѿ�������Ա�Ľ��
	 */
	private List<PsndocWadocVO> checkFailedGradeInResult(List<PsndocWadocVO> failtureImportList,
			PsndocWadocVO[] inQueryResult) {
		List<PsndocWadocVO> itemReasonPersons = new ArrayList<PsndocWadocVO>();
		List<PsndocWadocVO> otherReasonPsn = new ArrayList<PsndocWadocVO>();

		int length = 0;
		if (!ArrayUtils.isEmpty(inQueryResult)) {
			length = inQueryResult.length;
		} else {
			for (int i = 0; i < failtureImportList.size(); i++) {
				Logger.debug(new Date() + " :checkFailedGradeInResult ----> + " + i);
				PsndocWadocVO psndocWadocVo = failtureImportList.get(i);
				psndocWadocVo.setReason(MessageFormat.format(ResHelper.getString("60130adjmtc", "060130adjmtc0238"),
						psndocWadocVo.getPk_wa_grd_showname()));/*
																 * @res
																 * "������Ϊ[{0}]��н�ʱ�׼��\n
																 */
				psndocWadocVo.setError_flag(WA_GRADE);
				itemReasonPersons.add(psndocWadocVo);
			}
			return itemReasonPersons;
		}
		for (int i = 0; i < failtureImportList.size(); i++) {
			Logger.debug(new Date() + " :checkFailedGradeInResult ----> + " + i);
			PsndocWadocVO psndocWadocVo = failtureImportList.get(i);
			psndocWadocVo.setReason(MessageFormat.format(ResHelper.getString("60130adjmtc", "060130adjmtc0238"),
					psndocWadocVo.getPk_wa_grd_showname()));/*
															 * @res
															 * "������Ϊ[{0}]��н�ʱ�׼��\n
															 */
			psndocWadocVo.setError_flag(WA_GRADE);
			itemReasonPersons.add(psndocWadocVo);
			for (int j = 0; j < length; j++) {
				String waitem = inQueryResult[j].getPk_wa_grd_showname();
				if (waitem.equals(psndocWadocVo.getPk_wa_grd_showname())) {
					// itemReasonPersons.remove(psndocWadocVo);
					psndocWadocVo.setReason(ResHelper.getString("60130adjmtc", "060130adjmtc0232")/*
																								 * @
																								 * res
																								 * "����ԭ��[н�ʼ���н�ʵ��𣬽��]��н�ʱ�׼��ƥ�䣡"
																								 */);
					otherReasonPsn.add(psndocWadocVo);
				}
			}
		}

		failtureImportList.clear();// removeAll(failtureImportList);
		failtureImportList.addAll(otherReasonPsn);
		return itemReasonPersons;
	}

	/**
	 * �������ݵ�Excel<BR>
	 * ���ݲ�ѯ������ѯ�����з��Ϲ������Ա��������ԱPK�ҳ���Ӧ�����±�־Ϊtrue�Ķ����ʼ�¼<BR>
	 * 
	 * @author xuhw on 2010-5-21
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public PsndocWadocVO[] exportData2Excel(PsndocWadocMainVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		String in = "";
		Vector<String> vpk = new Vector<String>();
		for (PsndocWadocMainVO vo : vos) {
			String pk_psndoc = vo.getPk_psndoc();
			Integer assgid = vo.getAssgid();
			if (pk_psndoc == null) {
				continue;
			} else if (null == assgid) {
				vpk.add(pk_psndoc);
			} else {
				vpk.add(pk_psndoc + assgid);
			}

		}
		if (vpk.size() == 0) {
			return null;
		}
		String[] pks = vpk.toArray(new String[0]);
		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(pks);// getInSQL�Ĳ������������飬Ҳ������vo����
			// �˴���insql����ѯ
			return getPsndocWadocDao().queryAllVOsByPsnPKForHI(inSQL, Integer.valueOf(0), true);
		} finally {
			if (isc != null)
				isc.clear();
		}

	}

	/**
	 * ��ѯ����֯�������¼�¼
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocVO[]
	 */
	@Override
	public Map<String, PsndocWadocVO> queryLastFlagVOs(LoginContext context) throws BusinessException {
		return getPsndocWadocDao().queryLastFlagVOs(context, null);
	}

	private PsndocWadocDAO getPsndocWadocDao() {
		if (psndocWadocDaO == null) {
			psndocWadocDaO = new PsndocWadocDAO();
		}
		return psndocWadocDaO;
	}

	// TODO ----------------������V5X�е��ϴ��룬���ڻ�û��ʹ�ã����ű���----------------------
	// TODO ----------------������V5X�е��ϴ��룬���ڻ�û��ʹ�ã����ű���----------------------
	// TODO ----------------������V5X�е��ϴ��룬���ڻ�û��ʹ�ã����ű���----------------------
	/**
	 * ͨ����λ���뷵��ָ����˾���м�¼VO���顣�����λ����Ϊ�շ������м�¼��
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocVO[] �鵽��VO��������
	 */
	public PsndocWadocVO[] queryAll(String strPkOrg) throws BusinessException {
		return this.getPsndocWadocDao().executeQueryVOs(" pk_org = '" + strPkOrg + " '", PsndocWadocVO.class);
	}

	/**
	 * ͨ���������VO����
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocVO
	 * @param key
	 */
	public PsndocWadocVO findByPrimaryKey(String key) throws BusinessException {
		PsndocWadocVO psndocWadoc = null;
		psndocWadoc = this.getPsndocWadocDao().executeQueryVO(" pk_psndoc_sub = '" + key + "' ", PsndocWadocVO.class);
		return psndocWadoc;
	}

	/**
	 * �˴����뷽��������
	 * 
	 * @return java.lang.String
	 * @param qvos
	 */
	private static String getJoinTableStr(List<String> tableCodes)// TODO
	// û��ʹ�õ���Ӧ���ǲ�ѯģ��ʱ��ʹ�ã��Ժ���
	{
		String joinTableStr = "";
		Vector<String> v = new Vector<String>();
		if (tableCodes != null && tableCodes.size() > 0) {
			for (int i = 0; i < tableCodes.size(); i++) {
				// String fieldCode = tableCodes.get(i);
				// //getTableCodeForMultiTable();
				String tableCode = tableCodes.get(i);
				if (tableCode.equalsIgnoreCase("bd_psncl")) {
					joinTableStr += " inner join bd_psncl on bd_psndoc.pk_psncl = bd_psncl.pk_psncl";
				} else if (!tableCode.equalsIgnoreCase("bd_psndoc") && !tableCode.equalsIgnoreCase("bd_psnbasdoc")
						&& !tableCode.equalsIgnoreCase("bd_deptdoc") && !tableCode.equalsIgnoreCase("hi_psndoc_wadoc")) {
					if (!v.contains(tableCode)) {
						v.addElement(tableCode);
					}
				}
			}
		}
		for (int i = 0; i < v.size(); i++) {
			joinTableStr += " left outer join " + v.elementAt(i) + " on bd_psndoc.pk_psndoc=" + v.elementAt(i)
					+ ".pk_psndoc";
			if ((v.elementAt(i)).startsWith("hi_psndoc") && !(v.elementAt(i)).equalsIgnoreCase("hi_psndoc_flag")) {
				joinTableStr += " and " + v.elementAt(i) + ".recordnum=0";
			}

		}
		return joinTableStr;
	}

	/**
	 * ȡн����ϵ�н�<BR>
	 * 
	 * @return Object[] ��һλ����н����Ŀ���Ŀ�ʼ���ڣ��ڶ�λ������<BR>
	 * @param psnpk
	 *            ��Ա����<BR>
	 * @param waitempk
	 *            н����Ŀ����<BR>
	 * @param date
	 *            �������ڣ�ȡ���¼�¼����ʱ�ò�����Ч<BR>
	 * @param isHis
	 *            �Ƿ�ȡ���¼�¼��Ϊ��ʱȥ��ʷ��¼��Ϊ��ʱȡ���·��ż�¼<BR>
	 */
	public Object[] getPayOffMoney(String psnpk, String waitempk, String date, Boolean isHis) throws BusinessException {
		return getPsndocWadocDao().getPayOffMoney(psnpk, waitempk, date, isHis);
	}

	/**
	 * ��ѯ��Ա������н�ʱ䶯�����
	 * 
	 * @return nc.vo.hi.wadoc.PsndocWadocVO[]
	 * @param psnPK
	 */
	public PsndocWadocVO[] queryAllVOsByPsnPKAndCond(String psnPK, String cond) throws BusinessException {
		return getPsndocWadocDao().queryAllVOsByPsnPKAndCond(psnPK, cond);
	}

	/**
	 * ��ѯ���к��Ӳ��ŵ��� //TODO 5X�еĴ��� ���ű���
	 * 
	 * @author xuhw on 2010-1-7
	 * @see nc.itf.hr.wa.IPsndocwadocQueryService#queryAllchildernDeptdoc(java.lang.String)
	 */
	@Override
	public DeptVO[] queryAllchildernDeptdoc(String pkDeptdoc) throws BusinessException {
		ArrayList<DeptVO> al = new ArrayList<DeptVO>();
		DeptVO vos[] = null;
		vos = query_child(pkDeptdoc);
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				al.add(vos[i]);
				DeptVO cvo[] = queryAllchildernDeptdoc(vos[i].getPrimaryKey());
				for (int j = 0; j < cvo.length; j++) {
					al.add(cvo[j]);
				}
			}
		}
		return al.toArray(new DeptVO[al.size()]);
	}

	private DeptVO[] query_child(String pk) throws BusinessException {
		Collection<DeptVO> c = getPsndocWadocDao().getBaseDao().retrieveByClause(DeptVO.class,
				(new StringBuilder()).append(" pk_fathedept = '").append(pk).append("'").toString());
		return c != null && c.size() != 0 ? (DeptVO[]) c.toArray(new DeptVO[c.size()]) : null;
	}

	/**
	 * 
	 * @author xuhw on 2010-4-27
	 * @param successvos
	 * @return
	 */
	private Map<String, Integer> setRecortNumber(PsndocWadocVO[] successvos) {
		Map<String, Integer> mapVO2Number = new HashMap<String, Integer>();
		if (successvos == null) {
			return mapVO2Number;
		}
		// ĳ��ĳ��Ŀ�˴���������
		Integer intCnt = 0;
		String strNextRecort = null;
		String strCurrentRecort = null;

		if (successvos.length == 1) {
			mapVO2Number.put(successvos[0].getPsnCode() + successvos[0].getPk_wa_item_showname(), 1);
			return mapVO2Number;
		}

		for (int i = 0; i < successvos.length; i++) {
			intCnt++;
			// �������ϼ�¼�ĳ���
			PsndocWadocVO nextPsndocwadocvos = null;
			PsndocWadocVO currentPsndocwadocvos = null;
			// ��ǰ��¼
			currentPsndocwadocvos = successvos[i];
			if (i == (successvos.length - 1)) {
				mapVO2Number.put(strCurrentRecort, intCnt);
			}
			// ��һ����¼
			nextPsndocwadocvos = successvos[i + 1];
			strNextRecort = nextPsndocwadocvos.getPsnCode() + nextPsndocwadocvos.getPk_wa_item_showname();
			strCurrentRecort = currentPsndocwadocvos.getPsnCode() + currentPsndocwadocvos.getPk_wa_item_showname();

			if (!strCurrentRecort.equals(strNextRecort)) {
				mapVO2Number.put(strCurrentRecort, intCnt);
				intCnt = 0;
			}
		}
		return mapVO2Number;
	}

	// ------------------add for ��汾�յ�----------------------------
	/**
	 * н���յ�-��������Ϣά���ĳ���<BR>
	 * 1:��������н�ʱ�׼����н�ʽ�����<BR>
	 * 2:�����𵵱����������<BR>
	 * 3:�û��Լ�����<BR>
	 * <BR>
	 */
	@Override
	public AdjustWadocVO[] batchAdjust(BatchAdjustVO adjustwadocvo, AdjustWadocVO[] adjustWadocPsnInfoVOs)
			throws BusinessException {
		return getAdjustTool().batchAdjust(adjustwadocvo, adjustWadocPsnInfoVOs);
	}

	@Override
	public AdjustWadocVO[] queryWadocMainData4AdjustInfo(LoginContext context, String queryStr,
			List<String> tableCodes, BatchAdjustVO batchadjustVO) throws BusinessException {
		UFBoolean partflagShow = WaAdjustParaTool.getPartjob_Adjmgt(context.getPk_group());
		String partFlag = "All";
		if (!partflagShow.booleanValue()) {
			partFlag = "N";
		}
		PsndocWadocMainVO[] psndocWadocMainVOs = queryWadocMainData(context, partFlag, queryStr, tableCodes,
				batchadjustVO.getPk_wa_item(), batchadjustVO.getPk_wa_grd());
		AdjustWadocVO[] adjustWadocPsnInfoVOs = getAdjustTool().filterPsnInfo(psndocWadocMainVOs, batchadjustVO);
		return getAdjustTool().batchAdjust(batchadjustVO, adjustWadocPsnInfoVOs);
	}

	/**
	 * н���յ���̨������-�����ʵĳ���
	 * 
	 * @param batchadjustVO
	 * @return
	 */
	public AdjustWadocVO[] queryAdjustWadocVOs4Adjust(BatchAdjustVO batchadjustVO, PsnappaproveBVO[] psnappaproveBVOs) {
		return null;
	}

	/**
	 * н���յ���ɣ��־û��յ�����
	 * 
	 * @param adjustWadocPsnInfoVOs
	 * @throws BusinessException
	 */
	@Override
	public void insertArray4Adjust(AdjustWadocVO[] adjustWadocPsnInfoVOs, BatchAdjustVO batchAdjustVO)
			throws BusinessException {
		if (ArrayUtils.isEmpty(adjustWadocPsnInfoVOs)) {
			return;
		}
		insertArray(getAdjustTool().corverPsndocWadocVO(adjustWadocPsnInfoVOs, batchAdjustVO));
	}

	private PsndocWadocAdjustTool adjustTool;

	public PsndocWadocAdjustTool getAdjustTool() {
		if (adjustTool == null) {
			adjustTool = new PsndocWadocAdjustTool();
		}
		return adjustTool;
	}

	public void setAdjustTool(PsndocWadocAdjustTool adjustTool) {
		this.adjustTool = adjustTool;
	}

	/*
	 * @Override public void validateBatchAdjust(AdjustWadocVO[]
	 * adjustWadocPsnInfoVOs, BatchAdjustVO batchAdjustVO) throws
	 * BusinessException { // TODO Auto-generated method stub String
	 * strErrorMessage=""; if (ArrayUtils.isEmpty(adjustWadocPsnInfoVOs)) {
	 * return ; } for (int i = 0; i < adjustWadocPsnInfoVOs.length; i++) {
	 * UFDoubleCompare doubleCompare = new UFDoubleCompare(); // ������ UFDouble
	 * money = null; // �����׼PK String strPKPrmlv = null; String strPKSeclv =
	 * null; // �Ƿ��� UFBoolean isRange = null; // �Ƿ�̸�� Boolean bnNegotiation =
	 * null; WaCriterionVO criterionVo = null; money =
	 * adjustWadocPsnInfoVOs[i].getMoney_adjust(); strPKPrmlv =
	 * adjustWadocPsnInfoVOs[i].getPk_wa_prmlv(); strPKSeclv =
	 * adjustWadocPsnInfoVOs[i].getPk_wa_seclv(); isRange =
	 * adjustWadocPsnInfoVOs[i].getIs_range(); bnNegotiation =
	 * adjustWadocPsnInfoVOs[i].getNegotiation().booleanValue();
	 * 
	 * if (doubleCompare.lessThan(money, new UFDouble(0))) {
	 * Logger.debug("����Ϊ����"); throw new
	 * BusinessException(ResHelper.getString("60130adjmtc"
	 * ,"060130adjmtc0219")@res "  ����Ϊ����, ���޸�."); } // ��̸�� if (bnNegotiation)
	 * { return ; } criterionVo =
	 * ((NCLocator.getInstance().lookup(IWaGradeService
	 * .class))).getCrierionVOByPrmSec(strPKPrmlv, strPKSeclv); // ���ǿ��
	 * 
	 * if (!isRange.booleanValue()) { if
	 * (!doubleCompare.equals(criterionVo.getCriterionvalue().div(new
	 * UFDouble(1), money.getPower()), money)) { strErrorMessage =
	 * ResHelper.getString("60130adjmtc","060130adjmtc0220")@res
	 * "�����н�ʱ�׼�����, ���޸�."; } } else{ if (doubleCompare.lessThan(money,
	 * criterionVo.getMin_value().div(new UFDouble(1), money.getPower()))) {
	 * strErrorMessage +=
	 * (i+1)+ResHelper.getString("60130adjmtc","060130adjmtc0223")@res
	 * "��:"+MessageFormat.format(
	 * ResHelper.getString("60130adjmtc","060130adjmtc0233")@res
	 * "���н�����������[{0}]����С��н�ʱ�׼������[{1}], ϵͳ���Զ�����Ϊ���޽��.\n",
	 * money,criterionVo.getMin_value().div(new UFDouble(1), money.getPower()));
	 * } else if (doubleCompare.lessThan(criterionVo.getMax_value().div(new
	 * UFDouble(1), money.getPower()), money)) { strErrorMessage +=
	 * (i+1)+ResHelper.getString("60130adjmtc","060130adjmtc0223")@res
	 * "��:"+MessageFormat.format(
	 * ResHelper.getString("60130adjmtc","060130adjmtc0234")@res
	 * "���н�����������[{0}]���ܴ���н�ʱ�׼������[{1}], ϵͳ���Զ�����Ϊ���޽��.\n",
	 * money,criterionVo.getMax_value().div(new UFDouble(1), money.getPower()));
	 * } } } if (!StringHelper.isEmpty(strErrorMessage)) {
	 * Logger.debug(strErrorMessage); throw new
	 * BusinessException(strErrorMessage); } }
	 */

	@Override
	public String limitBatchAdjust(AdjustWadocVO[] adjustWadocPsnInfoVOs) throws BusinessException {
		String strErrorMessage = "";
		if (ArrayUtils.isEmpty(adjustWadocPsnInfoVOs)) {
			return strErrorMessage;
		}

		HashMap<String, WaCriterionVO> criterionVoMap = ((NCLocator.getInstance().lookup(IWaGradeService.class)))
				.getCrierionVOMapByPrmSec(adjustWadocPsnInfoVOs);

		for (int i = 0; i < adjustWadocPsnInfoVOs.length; i++) {
			UFDoubleCompare doubleCompare = new UFDoubleCompare();
			// ������
			UFDouble money = null;
			// �����׼PK
			String strPKPrmlv = null;
			String strPKSeclv = null;

			// �Ƿ���
			UFBoolean isRange = null;
			// �Ƿ�̸��
			Boolean bnNegotiation = null;
			WaCriterionVO criterionVo = null;

			money = adjustWadocPsnInfoVOs[i].getMoney_adjust();
			strPKPrmlv = adjustWadocPsnInfoVOs[i].getPk_wa_prmlv();
			strPKSeclv = adjustWadocPsnInfoVOs[i].getPk_wa_seclv();
			criterionVo = criterionVoMap.get(strPKPrmlv + strPKSeclv);

			isRange = adjustWadocPsnInfoVOs[i].getIs_range();
			bnNegotiation = adjustWadocPsnInfoVOs[i].getNegotiation().booleanValue();

			if (doubleCompare.lessThan(money, new UFDouble(0))) {
				strErrorMessage += ResHelper.getString("60130adjmtc", "060130adjmtc0219")/*
																						 * @
																						 * res
																						 * "  ����Ϊ����, ���޸�."
																						 */;
				return strErrorMessage;
			}
			// ��̸��
			if (bnNegotiation) {
				return strErrorMessage;
			}
			if (!isRange.booleanValue()) {
				// ���ǿ��
				if (!doubleCompare
						.equals(criterionVo.getCriterionvalue().div(new UFDouble(1), money.getPower()), money)) {
					strErrorMessage += ResHelper.getString("60130adjmtc", "060130adjmtc0220")/*
																							 * @
																							 * res
																							 * "�����н�ʱ�׼�����, ���޸�."
																							 */;
				}
			} else {
				if (doubleCompare.lessThan(money, criterionVo.getMin_value().div(new UFDouble(1), money.getPower()))) {
					strErrorMessage += (i + 1)
							+ ResHelper.getString("60130adjmtc", "060130adjmtc0223")/*
																					 * @
																					 * res
																					 * "��:"
																					 */
							+ MessageFormat.format(ResHelper.getString("60130adjmtc", "060130adjmtc0233")/*
																										 * @
																										 * res
																										 * "���н�����������[{0}]����С��н�ʱ�׼������[{1}], ϵͳ���Զ�����Ϊ���޽��.\n"
																										 */, money,
									criterionVo.getMin_value().div(new UFDouble(1), money.getPower()));

					adjustWadocPsnInfoVOs[i].setMoney_adjust(criterionVo.getMin_value().div(new UFDouble(1),
							money.getPower()));
					adjustWadocPsnInfoVOs[i].setChange_money(getABSMoney(adjustWadocPsnInfoVOs[i].getMoney_old(),
							adjustWadocPsnInfoVOs[i].getMoney_adjust()));
				} else if (doubleCompare.lessThan(criterionVo.getMax_value().div(new UFDouble(1), money.getPower()),
						money)) {
					strErrorMessage += (i + 1)
							+ ResHelper.getString("60130adjmtc", "060130adjmtc0223")/*
																					 * @
																					 * res
																					 * "��:"
																					 */
							+ MessageFormat.format(ResHelper.getString("60130adjmtc", "060130adjmtc0234")/*
																										 * @
																										 * res
																										 * "���н�����������[{0}]���ܴ���н�ʱ�׼������[{1}], ϵͳ���Զ�����Ϊ���޽��.\n"
																										 */, money,
									criterionVo.getMax_value().div(new UFDouble(1), money.getPower()));
					adjustWadocPsnInfoVOs[i].setMoney_adjust(criterionVo.getMax_value().div(new UFDouble(1),
							money.getPower()));
					adjustWadocPsnInfoVOs[i].setChange_money(getABSMoney(adjustWadocPsnInfoVOs[i].getMoney_old(),
							adjustWadocPsnInfoVOs[i].getMoney_adjust()));
				}
			}
		}
		return strErrorMessage;
	}

	/**
	 * ȡ������UFDouble�ľ���ֵ
	 * 
	 * @param ufOldGradeMoney
	 * @param ufAdjustAftGradeMoney
	 * @return
	 */
	private UFDouble getABSMoney(UFDouble ufOldGradeMoney, UFDouble ufAdjustAftGradeMoney) {

		if (ufOldGradeMoney == null) {
			ufOldGradeMoney = new UFDouble(0);
		}
		if (ufAdjustAftGradeMoney == null) {
			ufAdjustAftGradeMoney = new UFDouble(0);
		}
		return ufAdjustAftGradeMoney.sub(ufOldGradeMoney)/* .abs() */;
	}

	@Override
	public PsndocWadocVO queryLastNMoney(String pk_psndoc, String pk_wa_item) throws BusinessException {
		PsndocWadocVO[] vos = (PsndocWadocVO[]) getPsndocWadocDao()
				.getBaseDao()
				.retrieveByClause(
						PsndocWadocVO.class,
						" LASTFLAG = 'Y' and NEGOTIATION_WAGE = 'Y' and PK_PSNDOC = '" + pk_psndoc
								+ "' and pk_wa_item = '" + pk_wa_item + "'").toArray(new PsndocWadocVO[0]);
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		} else {
			return vos[0];
		}
	}

	/**
	 * ����ȡ��н�ʱ�׼����<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-1-2
	 * @param strPkCrt
	 * @param ismultsec
	 * @return
	 * @throws BusinessException
	 */
	public PsndocWadocVO[] getCrtName(PsndocWadocVO[] PsndocWadocVOs) throws BusinessException {
		return getPsndocWadocDao().getCrtName(PsndocWadocVOs);
	}

	// patch to v636 wangqim NCdp205253327
	@Override
	public PsndocWadocVO[] queryPsndocWadocsByPsncode(String[] psncodes) throws BusinessException {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < psncodes.length; i++) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(" '").append(psncodes[i]).append("' ");
		}
		String sql = "select HI_PSNDOC_WADOC.* from HI_PSNDOC_WADOC,BD_PSNDOC where HI_PSNDOC_WADOC.PK_PSNDOC = BD_PSNDOC.PK_PSNDOC "
				+ "		and BD_PSNDOC.CODE in (" + buffer.toString() + ")";
		PsndocWadocVO[] wadocVOs = new BaseDAOManager().executeQueryVOs(sql, PsndocWadocVO.class);
		return wadocVOs;
	}

	@Override
	public void psndocWadocSaveToWainfoVO(PsndocWadocVO[] psndocWadocs) throws BusinessException {
		BTOBXVOConversionUtility.psndocWadocSaveToWainfoVO(psndocWadocs);
	}

	@Override
	public void generateByPsnJob(PsnJobVO[] newPsnJobs) throws BusinessException {

		// ��ͣ�������

		// ������Ա��Ϣ�͹�����¼���߼��ֶ�
		String loadLogicSql = "select HR_INFOSET.INFOSET_CODE||'::'||HR_INFOSET_ITEM.ITEM_CODE from HR_INFOSET_ITEM "
				+ " left join HR_INFOSET on HR_INFOSET.PK_INFOSET = HR_INFOSET_ITEM.PK_INFOSET  and HR_INFOSET.dr = 0 "
				+ " where HR_INFOSET_ITEM.dr = 0 and HR_INFOSET_ITEM.DATA_TYPE = 4 and HR_INFOSET.INFOSET_CODE in ( 'bd_psndoc','hi_psnjob') ";
		@SuppressWarnings("unchecked")
		Set<String> logicColSet = (Set<String>) getPsndocWadocDao().getBaseDao().executeQuery(loadLogicSql,
				new ResultSetProcessor() {
					private Set<String> rsSet = new HashSet<>();

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						while (rs.next()) {
							rsSet.add(rs.getString(1));
						}
						return rsSet;
					}
				});
		for (PsnJobVO newPsnJob : newPsnJobs) {
			refTransType = SysInitQuery.getParaString(newPsnJob.getPk_hrorg(), "TWHR11").toString();
			// �}�������
			refReturnType = SysInitQuery.getParaString(newPsnJob.getPk_hrorg(), "TWHR12").toString();

			if (newPsnJob == null) {
				continue;
			}
			// У�鿼�ڵ����Ͷ����ʼ�¼
			if (newPsnJob.getEnddate() != null) {
				validateTbmAndWadoc(newPsnJob);
			}

			PsndocVO psnVO = (PsndocVO) getPsndocWadocDao().getBaseDao().retrieveByPK(PsndocVO.class,
					newPsnJob.getPk_psndoc());

			// �x��r��̎��
			if (newPsnJob.getTrnsevent() == 4) {
				this.getPsndocWadocDao()
						.getBaseDao()
						.executeUpdate(
								"update hi_psndoc_wadoc set enddate='"
										+ newPsnJob.getBegindate().getDateBefore(1).toString() + "' where pk_psndoc='"
										+ newPsnJob.getPk_psndoc()
										+ "' and isnull(enddate, '9999-12-31') >= '9999-01-01'");
				return;
			}

			// δ�D���ˆT�n���Ĳ������ɶ��{�Y����
			PsnOrgVO psnorg = (PsnOrgVO) this.getPsndocWadocDao().getBaseDao()
					.retrieveByPK(PsnOrgVO.class, newPsnJob.getPk_psnorg());
			if (psnorg.getIndocflag() == null || !psnorg.getIndocflag().booleanValue()) {
				continue;
			}

			// ssx added on 2020-01-15
			// ���н�Y���r��̎����ǲ�ˆT���ʌ���ǲ�ˆT����̎���{�Y���P�Y�ϣ�����̎���@�����Y�ϕr��Ҫ���_������
			String empForm = (String) newPsnJob.getAttributeValue("jobglbdef8");
			String sendingType = (String) this
					.getPsndocWadocDao()
					.getBaseDao()
					.executeQuery(
							"select pk_defdoc from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'HR006_0xx') and code in ('C')",
							new ColumnProcessor());
			if (sendingType.equals(empForm)) {
				continue;
			}
			// end 2020-01-15

			if (refTransType == null || refTransType.equals("~")) {
				throw new BusinessException("ϵ�y���� [TWHR11] δָ�������ͣ�Į�����͡�");
			}

			if (refReturnType == null || refReturnType.equals("~")) {
				throw new BusinessException("ϵ�y���� [TWHR12] δָ�������ͣ�}�Į�����͡�");
			}

			// ̎��ͣн��Ӱ푶��{�Y������ֵ��TRUE�r�������^�m̎�����m���{�Y�Ŀ����߉݋�����ͣн��
			if (!dealWithStopWageRemainPos(newPsnJob)) {

				List<AggWaGradeVO> aggvos = new ArrayList<AggWaGradeVO>();
				Map<String, WaCriterionVO[]> gradeCriterions = new HashMap<String, WaCriterionVO[]>();
				Map<String, Collection<WaPsnhiBVO>> gradePsnhiBs = new HashMap<String, Collection<WaPsnhiBVO>>();

				// ���dн�Y�˜����P����
				loadWaGradeInfo(newPsnJob, aggvos, gradeCriterions, gradePsnhiBs);

				for (AggWaGradeVO aggvo : aggvos) {
					Map<String, String> psnClassValues = new HashMap<String, String>();// �ˆT���eȡֵ
					Map<String, String> psnLevelValues = new HashMap<String, String>();// �ˆT�n�eȡֵ
					for (CircularlyAccessibleValueObject psnhiVO : aggvo.getTableVO(IWaGradeCommonDef.WA_PSNHI)) {
						((WaPsnhiVO) psnhiVO).getVfldcode();
						String value = null;

						if (getSourceMeta(((WaPsnhiVO) psnhiVO).getPk_flddict()).contains("hrhi.bd_psndoc")) {
							value = String.valueOf(psnVO.getAttributeValue(((WaPsnhiVO) psnhiVO).getVfldcode()));
							// ������߼�����,��ô��Ҫ�ѿ�ֵ��ΪN
							if (value.equals("null")
									&& logicColSet.contains("bd_psndoc::" + ((WaPsnhiVO) psnhiVO).getVfldcode())) {
								value = "N";
							}
						} else {
							value = String.valueOf(newPsnJob.getAttributeValue(((WaPsnhiVO) psnhiVO).getVfldcode()));
							// ������߼�����,��ô��Ҫ�ѿ�ֵ��ΪN
							if (value.equals("null")
									&& logicColSet.contains("hi_psnjob::" + ((WaPsnhiVO) psnhiVO).getVfldcode())) {
								value = "N";
							}
						}

						if (((WaPsnhiVO) psnhiVO).getClasstype() == 1) {
							// ���e
							psnClassValues.put(((WaPsnhiVO) psnhiVO).getPk_wa_psnhi(), value);
						} else {
							// �n�e
							psnLevelValues.put(((WaPsnhiVO) psnhiVO).getPk_wa_psnhi(), value);
						}
					}
					String pk_wa_item = aggvo.getParentVO().getPk_wa_item(); // н�Y���e����н�Y�Ŀ

					// ���Ҍ������H���e���n�eȡֵ�����ļ��ePK���n�ePK
					String pkFoundClass = null;
					String pkFoundLevel = null;
					if (gradePsnhiBs.containsKey((aggvo.getParentVO().getPk_wa_grd()))) {
						for (Entry<String, List<WaPsnhiBVO>> bvo : getClassGroup(
								gradePsnhiBs.get(aggvo.getParentVO().getPk_wa_grd())).entrySet()) {
							// ssx modified on 2019-08-21
							// �ޏ�ƥ��Ҏ�t���}
							String[] matchedValues = groupMatched(bvo.getValue(), psnClassValues);

							if (matchedValues != null) {
								if (psnClassValues.containsKey(matchedValues[0])) {
									pkFoundClass = matchedValues[1];
								}
							}

							matchedValues = groupMatched(bvo.getValue(), psnLevelValues);
							if (matchedValues != null) {
								if (psnLevelValues.containsKey(matchedValues[0])) {
									pkFoundLevel = matchedValues[1];
								}
							}
							// end
						}
					} else {
						continue;
					}
					// mod ���û��ƥ�䵽н�ʱ�׼,��ô�����ж����ʵ��������� tank 2020��2��12��21:19:25
					if (pkFoundClass == null) {
						// ������ͬн����Ŀ,���ڸ���Ķ����ʼ�¼
						this.getBaseDao().executeUpdate(
								"update hi_psndoc_wadoc set enddate='"
										+ newPsnJob.getBegindate().getDateBefore(1).toString() + "' where pk_psndoc='"
										+ newPsnJob.getPk_psndoc()
										+ "' and isnull(enddate, '9999-12-31') >= '9999-01-01'" + " and pk_psnjob='"
										+ newPsnJob.getPk_psnjob() + "' and pk_wa_item='" + pk_wa_item
										+ "' and isnull(dr,0)=0" + " and begindate < '"
										+ newPsnJob.getBegindate().toStdString() + "' ");
						continue;
					}
					// mod end ���û��ƥ�䵽н�ʱ�׼,��ô�����ж����ʵ��������� tank 2020��2��12��21:19:25
					UFDouble gradeSalary = UFDouble.ZERO_DBL;
					String pk_wa_crt = "";
					// �ҵ�н�Y�������e�ˆT�����O��=���e���Hֵ
					if (gradeCriterions.containsKey(aggvo.getParentVO().getPk_wa_grd())) {
						for (WaCriterionVO vlvo : gradeCriterions.get(aggvo.getParentVO().getPk_wa_grd())) {
							if (vlvo.getPk_wa_prmlv().equals(pkFoundClass)
									&& (vlvo.getPk_wa_seclv() == null || vlvo.getPk_wa_seclv().equals(pkFoundLevel))) {
								pk_wa_crt = vlvo.getPk_wa_crt();
								gradeSalary = new UFDouble(SalaryDecryptUtil.decrypt(vlvo.getCriterionvalue()
										.doubleValue()));
							}
						}
					} else {
						continue;
					}

					PsndocWadocVO existWadoc = getExistsWadoc(newPsnJob.getPk_psnjob(), pk_wa_item); // �Ѵ��ڵĶ��{�Y�Ŀ
					if (existWadoc != null) {
						// ��ǰ����ӛ��Ѵ��ڶ��{�Y�Ŀ������ԭ��ӛ�
						if (!aggvo.getParentVO().getPk_wa_grd().equals(existWadoc.getPk_wa_grd())
								|| !gradeSalary.equals(existWadoc.getNmoney())
								|| !pkFoundClass.equals(existWadoc.getPk_wa_prmlv())
								|| !pkFoundLevel.equals(existWadoc.getPk_wa_seclv())) { // н�Y�˜���������һ헲�ƥ��͸���
							existWadoc.setPk_wa_grd(aggvo.getParentVO().getPk_wa_grd());
							existWadoc.setPk_wa_crt(pk_wa_crt);
							existWadoc.setPk_wa_prmlv(pkFoundClass);
							existWadoc.setPk_wa_seclv(pkFoundLevel);
							existWadoc.setCriterionvalue(gradeSalary);
							existWadoc.setNmoney(gradeSalary);
							existWadoc.setIsrange(UFBoolean.FALSE);
							getPsndocwadocManageService().updatePsndocWadoc(existWadoc);
						}
					} else {
						// ��ǰ����ӛ䛲����ڶ��{�Y�Ŀ����Ҏ�tͣ���f�Ŀ���������Ŀ
						Collection<PsndocWadocVO> wadocs = this.getBaseDao().retrieveByClause(
								PsndocWadocVO.class,
								"pk_psndoc = '" + newPsnJob.getPk_psndoc() + "' and pk_wa_item='" + pk_wa_item
										+ "' and isnull(dr,0)=0");
						for (PsndocWadocVO wadoc : wadocs) {
							if (wadoc.getPk_wa_item().equals(pk_wa_item) // ͬһн�Y�Ŀ
									&& (wadoc.getEnddate() == null || wadoc.getEnddate()
											.after(newPsnJob.getBegindate()))// н�Y�Ŀ�ĽY�������ڹ���ӛ䛵��_ʼ����֮��
									&& UFBoolean.TRUE.equals(wadoc.getWaflag()) // �l����
							) {
								// �Y��ǰһ����Ч��н�Y�Ŀ
								this.getPsndocWadocDao()
										.getBaseDao()
										.executeUpdate(
												"update hi_psndoc_wadoc set enddate='"
														+ newPsnJob.getBegindate().getDateBefore(1).toString()
														+ "', lastflag='N' where pk_psndoc_sub='"
														+ wadoc.getPk_psndoc_sub() + "'");
								break;
							}
						}

						if (gradeSalary.doubleValue() > 0) {
							this.getPsndocWadocDao()
									.getBaseDao()
									.executeUpdate(
											"update hi_psndoc_wadoc set recordnum=recordnum+1 where pk_psndoc='"
													+ newPsnJob.getPk_psndoc() + "'");

							PsndocWadocVO newVO = creatNewPsndocWadocVO(newPsnJob, aggvo.getParentVO().getPk_wa_grd(),
									pk_wa_item, pkFoundClass, pkFoundLevel, gradeSalary, pk_wa_crt);
							getPsndocwadocManageService().insertPsndocWadocVO(newVO);
						}
					}
				}
			}
		}
	}

	private PsndocWadocVO creatNewPsndocWadocVO(PsnJobVO newPsnJob, String pk_wa_grd, String pk_wa_item,
			String pkFoundClass, String pkFoundLevel, UFDouble gradeSalary, String pk_wa_crt) {
		PsndocWadocVO newVO = new PsndocWadocVO();
		newVO.setPk_group(newPsnJob.getPk_group());
		newVO.setPk_org(newPsnJob.getPk_org());
		newVO.setPk_psndoc(newPsnJob.getPk_psndoc());
		newVO.setPk_psnjob(newPsnJob.getPk_psnjob());
		newVO.setPk_wa_item(pk_wa_item);
		newVO.setPk_wa_grd(pk_wa_grd);
		newVO.setPk_wa_crt(pk_wa_crt);
		newVO.setPk_wa_prmlv(pkFoundClass);
		newVO.setPk_wa_seclv(StringUtil.isEmpty(pkFoundLevel) ? null : pkFoundLevel);
		newVO.setBegindate(newPsnJob.getBegindate());
		newVO.setChangedate(new UFLiteralDate());
		newVO.setCriterionvalue(gradeSalary);
		newVO.setNmoney(gradeSalary);
		newVO.setNegotiation_wage(UFBoolean.FALSE);
		newVO.setWaflag(UFBoolean.TRUE);
		newVO.setLastflag(UFBoolean.TRUE);
		newVO.setPartflag(UFBoolean.FALSE);
		newVO.setIadjustmatter(1);
		newVO.setAssgid(newPsnJob.getAssgid());
		newVO.setDr(0);
		newVO.setRecordnum(0);
		newVO.setWorkflowflag(UFBoolean.FALSE);
		newVO.setIsrange(UFBoolean.FALSE);
		newVO.setIadjustmatter(1);
		return newVO;
	}

	/**
	 * ����������¼ʱ/�����޸��Ѿ������Ĺ�����¼,�����п�ʼ����Ϊ������¼��������֮��Ŀ��ڵ������߶����ʼ�¼ 2020��2��12��21:48:542
	 * tank by Jessie
	 * 
	 * @param newPsnJob
	 * @throws BusinessException
	 */
	private void validateTbmAndWadoc(PsnJobVO newPsnJob) throws BusinessException {
		String pk_psndoc = newPsnJob.getPk_psndoc();
		UFLiteralDate checkDate = newPsnJob.getEnddate().getDateAfter(1);

		if (pk_psndoc != null) {
			String sql = "select count(*) from tbm_psndoc where pk_psndoc = '" + pk_psndoc
					+ "' and dr = 0 and BEGINDATE >= '" + checkDate.toStdString() + "' ";
			Integer tbmCount = (Integer) getBaseDao().executeQuery(sql, new ColumnProcessor());
			if (tbmCount != null && tbmCount > 0) {
				throw new BusinessException("ԓ�ˆT�Ѵ����_ʼ������[" + checkDate + "]֮��Ŀ���ӛ�,�o���޸�!");
			}
			sql = "select count(*) from hi_psndoc_wadoc where pk_psndoc = '" + pk_psndoc
					+ "'  and dr = 0 and BEGINDATE >= '" + checkDate.toStdString() + "' ";
			Integer waCount = (Integer) getBaseDao().executeQuery(sql, new ColumnProcessor());
			if (waCount != null && waCount > 0) {
				throw new BusinessException("ԓ�ˆT��Ѵ����_ʼ������[" + checkDate + "]֮��Ķ��{�Yӛ�,�o���޸�!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private PsndocWadocVO getExistsWadoc(String pk_psnjob, String pk_wa_item) throws BusinessException {
		Collection<PsndocWadocVO> vos = this.getBaseDao().retrieveByClause(PsndocWadocVO.class,
				"pk_psnjob='" + pk_psnjob + "' and pk_wa_item='" + pk_wa_item + "' and isnull(dr,0)=0");
		if (vos != null && vos.size() > 0) {
			return vos.toArray(new PsndocWadocVO[0])[0];
		} else {
			return null;
		}
	}

	private Map<String, List<WaPsnhiBVO>> getClassGroup(Collection<WaPsnhiBVO> bvos) {
		Map<String, List<WaPsnhiBVO>> groupMap = new HashMap<String, List<WaPsnhiBVO>>();
		for (WaPsnhiBVO bvo : bvos) {
			if (!groupMap.containsKey(bvo.getSortgroup())) {
				groupMap.put(bvo.getSortgroup(), new ArrayList<WaPsnhiBVO>());
			}

			groupMap.get(bvo.getSortgroup()).add(bvo);
		}
		return groupMap;
	}

	@SuppressWarnings("unchecked")
	private boolean dealWithStopWageRemainPos(PsnJobVO newPsnJob) throws BusinessException {
		Collection existVOs = this.getBaseDao().retrieveByClause(PsndocWadocVO.class,
				"pk_psnjob='" + newPsnJob.getPk_psnjob() + "' and isnull(dr,0)=0");
		if (existVOs != null && existVOs.size() > 0) {
			// ��ǰ����ӛ������ɶ��{�Y���J�����޸Ĳ���
			return false;
		}
		String strWhere = getGradeWhereFilter(newPsnJob);

		if (refTransType.equals(newPsnJob.getTrnstype())) {
			// ͣн��
			Collection<PsndocWadocVO> wadocs = this.getBaseDao().retrieveByClause(
					PsndocWadocVO.class,
					"pk_psndoc = '" + newPsnJob.getPk_psndoc()
							+ "' and waflag='Y' and lastflag='Y' and isnull(dr, 0)=0");
			for (PsndocWadocVO wadoc : wadocs) {
				if (wadoc.getEnddate() == null || wadoc.getEnddate().after(newPsnJob.getBegindate().getDateBefore(1))) // �Y���������ͣн���_ʼ����ǰһ��
				{
					wadoc.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt(wadoc.getNmoney() == null ? 0 : wadoc
							.getNmoney().doubleValue())));
					wadoc.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil.decrypt(wadoc.getCriterionvalue() == null ? 0 : wadoc.getCriterionvalue()
									.doubleValue())));
					wadoc.setChangedate(new UFLiteralDate());
					// wadoc.setWaflag(UFBoolean.FALSE);
					wadoc.setWaflag(UFBoolean.TRUE);
					wadoc.setEnddate(newPsnJob.getBegindate().getDateBefore(1));
					wadoc.setIsrange(wadoc.getIsrange() == null ? UFBoolean.FALSE : wadoc.getIsrange());
					getPsndocwadocManageService().updatePsndocWadoc(wadoc);
				}
			}

			return true;
		} else if (refReturnType.equals(newPsnJob.getTrnstype())) {
			// ��ͣ��
			Collection<PsndocWadocVO> wadocs = this.getBaseDao().retrieveByClause(
					PsndocWadocVO.class,
					"pk_psndoc = '" + newPsnJob.getPk_psndoc()
							+ "' and lastflag='Y' and isnull(dr, 0)=0 and pk_wa_item not in ("
							+ "select pk_wa_item from wa_grade where pk_org='" + newPsnJob.getPk_org()
							+ "' and isnull(dr,0)=0" + strWhere + ")");

			for (PsndocWadocVO wadoc : wadocs) {
				if (wadoc.getEnddate() != null
						&& wadoc.getEnddate().isSameDate(
								getTransTypeEndDate(newPsnJob.getPk_org(), newPsnJob.getBegindate(), refTransType,
										newPsnJob.getPk_psndoc()))) // �Y��������ͣн���_ʼ����ǰһ���
				{
					wadoc.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt(wadoc.getNmoney() == null ? 0 : wadoc
							.getNmoney().doubleValue())));
					wadoc.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil.decrypt(wadoc.getCriterionvalue() == null ? 0 : wadoc.getCriterionvalue()
									.doubleValue())));
					wadoc.setLastflag(UFBoolean.FALSE);
					wadoc.setIsrange(UFBoolean.FALSE);
					getPsndocwadocManageService().updatePsndocWadoc(wadoc);

					PsndocWadocVO newVO = (PsndocWadocVO) wadoc.clone();
					newVO.setBegindate(newPsnJob.getBegindate());
					newVO.setEnddate(newPsnJob.getEnddate());
					newVO.setPk_psnjob(newPsnJob.getPk_psnjob());
					newVO.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt(newVO.getNmoney() == null ? 0 : newVO
							.getNmoney().doubleValue())));
					newVO.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil.decrypt(newVO.getCriterionvalue() == null ? 0 : newVO.getCriterionvalue()
									.doubleValue())));
					newVO.setPk_changecause(null);
					newVO.setTs(null);
					newVO.setChangedate(new UFLiteralDate());
					newVO.setIsrange(wadoc.getIsrange() == null ? UFBoolean.FALSE : wadoc.getIsrange());
					newVO.setPk_psndoc_sub(null);
					newVO.setWaflag(UFBoolean.TRUE);
					newVO.setLastflag(UFBoolean.TRUE);
					getPsndocwadocManageService().insertPsndocWadocVO(newVO);
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void loadWaGradeInfo(PsnJobVO newPsnJob, List<AggWaGradeVO> aggvos,
			Map<String, WaCriterionVO[]> gradeCriterions, Map<String, Collection<WaPsnhiBVO>> gradePsnhiBs)
			throws BusinessException {
		String strWhere = getGradeWhereFilter(newPsnJob);

		Collection<WaGradeVO> gradevos = this.getBaseDao().retrieveByClause(WaGradeVO.class,
				"pk_org='" + newPsnJob.getPk_org() + "' and isnull(dr,0)=0 " + strWhere);

		for (WaGradeVO gradevo : gradevos) {
			Collection<WaGradeVerVO> gradeVerVOs = this.getBaseDao().retrieveByClause(WaGradeVerVO.class,
					"pk_wa_grd='" + gradevo.getPk_wa_grd() + "'");

			// ssx added on 2020-01-16
			// ����ȡн�Y�˜ʰ汾߉݋
			String pkVer = "";
			int verTerm = Integer.MAX_VALUE;
			for (WaGradeVerVO verVO : gradeVerVOs) {
				UFLiteralDate verDate = verVO.getVer_create_date().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE);
				if (StringUtils.isEmpty(pkVer)) {
					pkVer = verVO.getPk_wa_gradever();
					verTerm = UFLiteralDate.getDaysBetween(verDate, newPsnJob.getBegindate());
				} else {
					if (UFLiteralDate.getDaysBetween(verDate, newPsnJob.getBegindate()) >= 0
							&& UFLiteralDate.getDaysBetween(verDate, newPsnJob.getBegindate()) < verTerm) {
						verTerm = UFLiteralDate.getDaysBetween(verDate, newPsnJob.getBegindate());
						pkVer = verVO.getPk_wa_gradever();
					}
				}
			}
			// end

			Collection<WaPsnhiVO> psnhis = this.getBaseDao().retrieveByClause(WaPsnhiVO.class,
					" pk_wa_grd = '" + gradevo.getPk_wa_grd() + "' and isnull(dr,0)=0");

			// �M��н�Y���e���P����
			if (psnhis != null && psnhis.size() > 0) {

				// н�Y���eAggVO
				AggWaGradeVO aggvo = new AggWaGradeVO();

				// н�Y�˜ʱ�
				aggvo.setParentVO(gradevo);

				// ���e���n�e�O��
				aggvo.setTableVO(IWaGradeCommonDef.WA_PSNHI, psnhis.toArray(new WaPsnhiVO[0]));

				// ���e
				Collection<WaPrmlvVO> prmlvs = this.getBaseDao().retrieveByClause(WaPrmlvVO.class,
						"pk_wa_grd='" + gradevo.getPk_wa_grd() + "'  and isnull(dr,0)=0 ");
				if (prmlvs != null && prmlvs.size() > 0) {
					aggvo.setTableVO(IWaGradeCommonDef.WA_PRMLV, prmlvs.toArray(new WaPrmlvVO[0]));
				}

				// �n�e
				Collection<WaSeclvVO> seclvs = this.getBaseDao().retrieveByClause(WaSeclvVO.class,
						"pk_wa_grd='" + gradevo.getPk_wa_grd() + "' and isnull(dr,0)=0");
				if (seclvs != null && seclvs.size() > 0) {
					aggvo.setTableVO(IWaGradeCommonDef.WA_SECLV, seclvs.toArray(new WaSeclvVO[0]));
				}

				aggvos.add(aggvo);
				//

				// н�Y�˜ʱ�
				// ssx added on 2020-01-16
				// ����ȡн�Y�˜ʰ汾߉݋
				Collection<WaCriterionVO> criterionvos = this.getBaseDao().retrieveByClause(
						WaCriterionVO.class,
						"pk_wa_grd='" + gradevo.getPk_wa_grd() + "' and isnull(dr,0)=0 and pk_wa_gradever='" + pkVer
								+ "'");
				// end
				if (criterionvos != null && criterionvos.size() > 0) {
					gradeCriterions.put(gradevo.getPk_wa_grd(), criterionvos.toArray(new WaCriterionVO[0]));
				}
				//

				for (WaPsnhiVO psnhi : psnhis) {
					// ���e�ˆT�����O�ã��n�e�ˆT�����O��
					Collection<WaPsnhiBVO> psnhibvos = this.getBaseDao().retrieveByClause(WaPsnhiBVO.class,
							"pk_wa_psnhi='" + psnhi.getPk_wa_psnhi() + "' and isnull(dr,0)=0");
					if (psnhibvos != null && psnhibvos.size() > 0) {
						if (!gradePsnhiBs.containsKey(gradevo.getPk_wa_grd())) {
							gradePsnhiBs.put(gradevo.getPk_wa_grd(), psnhibvos);
						} else {
							gradePsnhiBs.get(gradevo.getPk_wa_grd()).addAll(psnhibvos);
						}
					}
				}
				//
			}
		}
	}

	private String getSourceMeta(String pk_flddict) throws BusinessException {
		String metadata = (String) this.getBaseDao().executeQuery(
				"select meta_data from hr_infoset_item where pk_infoset_item = '" + pk_flddict + "'",
				new ColumnProcessor());
		return metadata;
	}

	private String[] groupMatched(List<WaPsnhiBVO> groupGrade, Map<String, String> psnClassValues) {
		boolean rtn = true;
		boolean found = false;
		String foundKey = "";
		String foundValue = "";
		if (groupGrade != null && psnClassValues != null) {
			for (WaPsnhiBVO bvo : groupGrade) {
				if (psnClassValues.containsKey(bvo.getPk_wa_psnhi())) {
					if ((bvo.getVfldvalue() == null && psnClassValues.get(bvo.getPk_wa_psnhi()) == null)
							|| (bvo.getVfldvalue() != null && bvo.getVfldvalue().equals(
									psnClassValues.get(bvo.getPk_wa_psnhi())))) {
						rtn &= true;
						found = true;
						foundKey = bvo.getPk_wa_psnhi();
						foundValue = bvo.getPk_wa_grdlv();
					} else {
						return null;
					}
				} else {
					found = found | false;
				}
			}
		}
		if (rtn & found) {
			return new String[] { foundKey, foundValue };
		} else {
			return null;
		}
	}

	private String getGradeWhereFilter(PsnJobVO newPsnJob) throws BusinessException {
		String expCodes = SysInitQuery.getParaString(newPsnJob.getPk_org(), "HRWAWNC01");
		String strWhere = "";
		if (!StringUtils.isEmpty(expCodes)) {
			for (String code : expCodes.replace("��", ",").split(",")) {
				if (!StringUtils.isEmpty(code)) {
					if (StringUtils.isEmpty(strWhere)) {
						strWhere = "'" + code.trim() + "'";
					} else {
						strWhere += ",'" + code.trim() + "'";
					}
				}
			}
			if (!StringUtils.isEmpty(strWhere)) {
				strWhere = "and code not in (" + strWhere + ")";
			}
		}
		return strWhere;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UFLiteralDate getTransTypeEndDate(String pk_org, UFLiteralDate begindate, String refTransType,
			String pk_psndoc) throws BusinessException {
		int minDays = Integer.MAX_VALUE;
		PsnJobVO retvo = null;
		Collection<PsnJobVO> psnjobs = this.getBaseDao().retrieveByClause(
				PsnJobVO.class,
				"trnstype='" + refTransType + "' and pk_psndoc='" + pk_psndoc + "' and pk_org='" + pk_org
						+ "' and isnull(dr,0)=0");
		for (PsnJobVO psnjob : psnjobs) {
			int days = UFLiteralDate.getDaysBetween(psnjob.getBegindate().getDateBefore(1), begindate);
			if (minDays > days) {
				minDays = days;
				retvo = psnjob;
			}
		}
		return retvo == null ? new UFLiteralDate("9999-12-31") : retvo.getBegindate().getDateBefore(1);
	}

	private static IPsndocwadocManageService getPsndocwadocManageService() {
		return (IPsndocwadocManageService) NCLocator.getInstance().lookup(IPsndocwadocManageService.class);
	}

	private BaseDAO getBaseDao() {
		return getPsndocWadocDao().getBaseDao();
	}

	@Override
	public void generateTeamItem(PsnJobVO newPsnJob) throws BusinessException {

		String newShift = ((String) newPsnJob.getAttributeValue("jobglbdef7"));
		// �°�M��Օr�������_ʼ����
		if (!StringUtils.isEmpty(newShift)) {
			TeamHeadVO headVO = (TeamHeadVO) this.getBaseDao().retrieveByPK(TeamHeadVO.class,
					(String) newPsnJob.getAttributeValue("jobglbdef7"));
			Collection<TeamItemVO> insertItemVOs = new ArrayList<TeamItemVO>();

			// �����³�ԱTeamItemVO������itemVOs
			TeamItemVO newMemberVO = new TeamItemVO();
			newMemberVO.setPk_group(headVO.getPk_group());
			newMemberVO.setPk_org(headVO.getPk_org());
			newMemberVO.setPk_org_v(headVO.getPk_org_v());
			newMemberVO.setPk_dept(newPsnJob.getPk_dept());
			newMemberVO.setPk_psncl(newPsnJob.getPk_psncl());
			newMemberVO.setPk_psnjob(newPsnJob.getPk_psnjob());
			newMemberVO.setCworkmanid(newPsnJob.getPk_psndoc());
			newMemberVO.setCteamid(headVO.getCteamid());
			newMemberVO.setBmanager(UFBoolean.FALSE);
			newMemberVO.setDr(0);
			newMemberVO.setDstartdate(newPsnJob.getBegindate());
			newMemberVO.setDenddate(newPsnJob.getEnddate());
			newMemberVO.setStatus(VOStatus.NEW);
			insertItemVOs.add(newMemberVO);

			updateShiftGroup(headVO, insertItemVOs);

			String maxTeamDate = (String) this.getBaseDao().executeQuery(
					"select max(calendar) cl from bd_teamcalendar where pk_team = '" + newShift + "'",
					new ColumnProcessor());
			if (!StringUtils.isEmpty(maxTeamDate)) {
				((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class))
						.sync2TeamCalendar(headVO.getPk_org(), newShift, new String[] { newPsnJob.getPk_psndoc() },
								newPsnJob.getBegindate(), new UFLiteralDate(maxTeamDate));
			}
		}

	}
	
	/**
	 * �˷�����춲��빤��ӛ䛵���r,ֻ��ͬ���������Ǘl����ӛ䛕r�g�ε��Ű�
	 * @param insertPsnJob
	 * @throws BusinessException
	 */
	@Override
	public void generateTeamItemForInsertPsn(PsnJobVO insertPsnJob) throws BusinessException {

		String newShift = ((String) insertPsnJob.getAttributeValue("jobglbdef7"));
		// �°�M��Օr�������_ʼ����
		if (!StringUtils.isEmpty(newShift)) {
			TeamHeadVO headVO = (TeamHeadVO) this.getBaseDao().retrieveByPK(TeamHeadVO.class,
					(String) insertPsnJob.getAttributeValue("jobglbdef7"));
			Collection<TeamItemVO> insertItemVOs = new ArrayList<TeamItemVO>();

			// �����³�ԱTeamItemVO������itemVOs
			TeamItemVO newMemberVO = new TeamItemVO();
			newMemberVO.setPk_group(headVO.getPk_group());
			newMemberVO.setPk_org(headVO.getPk_org());
			newMemberVO.setPk_org_v(headVO.getPk_org_v());
			newMemberVO.setPk_dept(insertPsnJob.getPk_dept());
			newMemberVO.setPk_psncl(insertPsnJob.getPk_psncl());
			newMemberVO.setPk_psnjob(insertPsnJob.getPk_psnjob());
			newMemberVO.setCworkmanid(insertPsnJob.getPk_psndoc());
			newMemberVO.setCteamid(headVO.getCteamid());
			newMemberVO.setBmanager(UFBoolean.FALSE);
			newMemberVO.setDr(0);
			newMemberVO.setDstartdate(insertPsnJob.getBegindate());
			newMemberVO.setDenddate(insertPsnJob.getEnddate());
			newMemberVO.setStatus(VOStatus.NEW);
			insertItemVOs.add(newMemberVO);

			updateShiftGroup(headVO, insertItemVOs);
        	    ((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class))
        		    .sync2TeamCalendar(headVO.getPk_org(), newShift, new String[] { insertPsnJob.getPk_psndoc() },
        			    insertPsnJob.getBegindate(), insertPsnJob.getEnddate());
		}

	}

	public void updateShiftGroup(TeamHeadVO headVO, Collection<TeamItemVO> itemVOs) throws BusinessException {
		if (itemVOs != null && itemVOs.size() > 0) {
			for (TeamItemVO vo : itemVOs) {
				if (vo.getStatus() == VOStatus.UPDATED) {
					this.getBaseDao().updateVO(vo);
					vo.setStatus(VOStatus.UNCHANGED);
				} else if (vo.getStatus() == VOStatus.NEW) {
					this.getBaseDao().insertVO(vo);
					vo.setStatus(VOStatus.UNCHANGED);
				} else if (vo.getStatus() == VOStatus.DELETED) {
					this.getBaseDao().deleteVO(vo);
				}
			}
		}
	}

}
