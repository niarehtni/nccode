package nc.impl.wa.taxaddtional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa_tax.TaxupgradeDef;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

import org.apache.commons.lang.StringUtils;

public class TaxAddtionalDAO extends BaseDAOManager {
	public WASpecialAdditionaDeductionVO[] queryAllTaxInfo(LoginContext context, String sqlWhere, String orderby)
			throws DAOException {
		StringBuffer sbSql = new StringBuffer();

		return null;
	}

	/**
	 * 批量增加 第二种方式 人员 查询
	 * 
	 * @param conditions
	 * @return
	 * @throws BusinessException
	 */
	public WASpecialAdditionaDeductionVO[] queryExportDataByCondition(LoginContext context, String sqlWhere,
			String orderby, String taxyear) throws BusinessException {
		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append(queryPsnAppSql(context,taxyear));
		if (!StringUtils.isEmpty(sqlWhere)) {
			sqlquery.append("and bd_psndoc."+sqlWhere);
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "hi_psnjob");
		if (!StringUtils.isEmpty(powerSql)) {
			sqlquery.append(" and " + powerSql);
		}
		// 过滤未启用组织职能的人员
		sqlquery.append(" and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable)  ");
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER){
			sqlquery.append(" and (hi_psnjob.endflag = 'N' or (hi_psnjob.endflag='Y' and year(hi_psnjob.begindate) >= "+taxyear+"))");
		}else {
			String taxyearBegindate = taxyear+"-01-01";
			sqlquery.append(" and (hi_psnjob.endflag = 'N' or (hi_psnjob.endflag='Y' and hi_psnjob.begindate >= '"+taxyearBegindate+"'))");
		}
		sqlquery.append(" order by  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ " ,bd_psndoc.code");
		return executeQueryVOs(sqlquery.toString(), WASpecialAdditionaDeductionVO.class);
	}
	
	/**
	 * 导入条件限定
	 * 
	 * @return
	 */
	private String queryPsnAppSqlExtForImport(LoginContext context) throws DAOException {
		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append("select distinct bd_psndoc.pk_psndoc,  ");
		sqlquery.append(" bd_psndoc.code psncode,  ");
		sqlquery.append(" "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ " psnname,  ");
		sqlquery.append(" bd_psndoc.id idcard,  ");
		sqlquery.append("  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ "  deptname,  ");
		sqlquery.append("   from hi_psnjob  ");
		sqlquery.append("  inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlquery.append("  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		sqlquery.append("  left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		sqlquery.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl  ");
		sqlquery.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlquery.append("  left outer join om_postseries on om_postseries.pk_postseries = hi_psnjob.pk_postseries ");
		sqlquery.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlquery.append("  left outer join hi_psndoc_wadoc  on HI_PSNDOC_WADOC.pk_psndoc = hi_psnjob.pk_psndoc ");
		try {
			ManagescopeBusiregionEnum busiregionEnum = ManagescopeBusiregionEnum.salary;
			Integer allowed = WaAdjustParaTool.getWaOrg(context.getPk_group());
			if (allowed.equals(1)) {
				busiregionEnum = ManagescopeBusiregionEnum.psndoc;
			}
			sqlquery.append(" where ( hi_psnjob.pk_psnjob in "
					+ ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(
							context.getPk_org(), busiregionEnum, true));
		} catch (BusinessException e) {
			throw new DAOException(e);
		}
		sqlquery.append("    and hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc ");
		// 人员组织关系是否限定
		sqlquery.append("    and hi_psnorg.indocflag = 'Y' ");
		sqlquery.append("    and hi_psnorg.lastflag = 'Y' ");

		// 兼职记录也要查出来
		UFBoolean partflagShow = WaAdjustParaTool.getPartjob_Adjmgt(context
				.getPk_group());
		if (!partflagShow.booleanValue()) {
			sqlquery.append("    and hi_psnjob.ismainjob = 'Y' ");
		}
		sqlquery.append(" )");
		return sqlquery.toString();
	}
	
	/**
	 * 批量增加人员
	 * 
	 * @return
	 */
	private String queryPsnAppSql(LoginContext context,
			 String taxyear) throws DAOException {
		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append("select distinct bd_psndoc.pk_psndoc,  ");
		sqlquery.append(" bd_psndoc.code psncode,  ");
		sqlquery.append(" "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ " psnname,  ");
		sqlquery.append(" bd_psndoc.id idcard,  ");
		sqlquery.append("  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ "  deptname,  ");
		sqlquery.append(" wa_deduction.TYPE,  ");
		sqlquery.append(" (  ");
		sqlquery.append(" CASE   ");
		sqlquery.append(" 	WHEN wa_deduction.TYPE = 1   ");
		sqlquery.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_CHILD_NAME + "'   ");
		sqlquery.append(" 	WHEN wa_deduction.TYPE = 2   ");
		sqlquery.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_EDUCATION_NAME + "'   ");
		sqlquery.append(" 	WHEN wa_deduction.TYPE = 3   ");
		sqlquery.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_ZU_NAME + "'   ");
		sqlquery.append(" 	WHEN wa_deduction.TYPE = 4   ");
		sqlquery.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_NAME + "'   ");
		sqlquery.append(" WHEN wa_deduction.TYPE = 5   ");
		sqlquery.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_PARENT_NAME + "'   ");
		sqlquery.append(" 	WHEN wa_deduction.TYPE = 6   ");
		sqlquery.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HEALTH_NAME + "'   ");
		sqlquery.append(" END) typename,  ");
		sqlquery.append(" wa_deduction.begin_year,  ");
		sqlquery.append(" wa_deduction.begin_period,  ");
		sqlquery.append(" wa_deduction.end_year,  ");
		sqlquery.append(" wa_deduction.end_period,  ");
		sqlquery.append(" wa_deduction.amount ,  ");
		sqlquery.append(" wa_deduction.amount amountimp   ");
		sqlquery.append("   from hi_psnjob  ");
		sqlquery.append("  inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlquery.append("  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		sqlquery.append(" 			LEFT OUTER JOIN wa_deduction   ");
		sqlquery.append(" 		ON wa_deduction.pk_psndoc = bd_psndoc.pk_psndoc AND  ");
		sqlquery.append(" 	wa_deduction.begin_year <= "+taxyear+" AND  ");
		sqlquery.append(" 		(wa_deduction.end_year IS NULL OR  ");
		sqlquery.append(" 		wa_deduction.end_year >= "+taxyear+" )   ");
		sqlquery.append("  left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		sqlquery.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl  ");
		sqlquery.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlquery.append("  left outer join om_postseries on om_postseries.pk_postseries = hi_psnjob.pk_postseries ");
		sqlquery.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlquery.append("  left outer join hi_psndoc_wadoc  on HI_PSNDOC_WADOC.pk_psndoc = hi_psnjob.pk_psndoc ");
		try {
			ManagescopeBusiregionEnum busiregionEnum = ManagescopeBusiregionEnum.salary;
			Integer allowed = WaAdjustParaTool.getWaOrg(context.getPk_group());
			if (allowed.equals(1)) {
				busiregionEnum = ManagescopeBusiregionEnum.psndoc;
			}
			sqlquery.append(" where ( hi_psnjob.pk_psnjob in "
					+ ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(
							context.getPk_org(), busiregionEnum, true));
		} catch (BusinessException e) {
			throw new DAOException(e);
		}
		sqlquery.append("    and hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc ");
		// 人员组织关系是否限定
		sqlquery.append("    and hi_psnorg.indocflag = 'Y' ");
		sqlquery.append("    and hi_psnorg.lastflag = 'Y' ");

		// 兼职记录也要查出来
		UFBoolean partflagShow = WaAdjustParaTool.getPartjob_Adjmgt(context
				.getPk_group());
		if (!partflagShow.booleanValue()) {
			sqlquery.append("    and hi_psnjob.ismainjob = 'Y' ");
		}
		sqlquery.append(" )");
		return sqlquery.toString();
	}
	
	public WASpecialAdditionaDeductionVO[] queryDataByCondition(LoginContext context, String sqlWhere, String orderby)
			throws BusinessException {

		StringBuffer sbsql = new StringBuffer();

		sbsql.append(" SELECT  ");
		sbsql.append(" bd_psndoc.pk_psndoc,  ");
		sbsql.append(" bd_psndoc.code psncode,  ");
		sbsql.append(" bd_psndoc.NAME psnname,  ");
		sbsql.append(" bd_psndoc.id idcard,  ");
//		sbsql.append(" org_dept.NAME deptname,  ");
		sbsql.append(" wa_deduction.TYPE,  ");
		sbsql.append(" (  ");
		sbsql.append(" CASE   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 1   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_CHILD_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 2   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_EDUCATION_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 3   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_ZU_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 4   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_NAME + "'   ");
		sbsql.append(" WHEN wa_deduction.TYPE = 5   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_PARENT_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 6   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HEALTH_NAME + "'   ");
		sbsql.append(" END) typename,  ");
		sbsql.append(" wa_deduction.begin_year,  ");
		sbsql.append(" wa_deduction.begin_period,  ");
		sbsql.append(" wa_deduction.end_year,  ");
		sbsql.append(" wa_deduction.end_period,  ");
		sbsql.append(" wa_deduction.amount ,  ");
		sbsql.append(" wa_deduction.amount amountimp, wa_deduction.pk_deduction   ");
		sbsql.append(" FROM  ");
		sbsql.append(" 	wa_deduction inner join bd_psndoc ON wa_deduction.pk_psndoc = bd_psndoc.pk_psndoc   ");
		sbsql.append(" 		left outer JOIN hi_psnjob   ");
		sbsql.append(" 	ON bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc  and hi_psnjob.ismainjob = 'Y' and lastflag = 'Y'");
		sbsql.append(" 			WHERE   ");
//		sbsql.append(" 			bd_psndoc.pk_org = ?  ");
		sbsql.append(" 			wa_deduction.def2 = ?  ");
//		sbsql.append(" 				 AND   ");
//		sbsql.append(" 				    ");
		if (!StringUtil.isEmpty(sqlWhere)) {
			sbsql.append(" and " + sqlWhere);
		}
		sbsql.append(" 			ORDER BY   ");
		sbsql.append(" 				bd_psndoc.code,   ");
		sbsql.append(" 				wa_deduction.TYPE   ");

		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(context.getPk_org());

		WASpecialAdditionaDeductionVO[] vos = this.executeQueryVOs(sbsql.toString(), sqlp,
				WASpecialAdditionaDeductionVO.class);
		return vos;
	}

	/**
	 * 导入校验使用
	 * 
	 * @param context
	 * @param idcards
	 * @return
	 * @throws BusinessException
	 */
	public WASpecialAdditionaDeductionVO[] queryPsndocExt(LoginContext context, String idcards)
			throws BusinessException {

		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append("select distinct bd_psndoc.pk_psndoc,  ");
		sqlquery.append(" bd_psndoc.code psncode,  ");
		sqlquery.append(" "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ " psnname,  ");
		sqlquery.append(" bd_psndoc.id idcard,  ");
		sqlquery.append("  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ "  deptname   ");
		sqlquery.append("   from hi_psnjob  ");
		sqlquery.append("  inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlquery.append("  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		sqlquery.append("  left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		sqlquery.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl  ");
		sqlquery.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlquery.append("  left outer join om_postseries on om_postseries.pk_postseries = hi_psnjob.pk_postseries ");
		sqlquery.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlquery.append("  left outer join hi_psndoc_wadoc  on HI_PSNDOC_WADOC.pk_psndoc = hi_psnjob.pk_psndoc ");
		try {
			ManagescopeBusiregionEnum busiregionEnum = ManagescopeBusiregionEnum.salary;
			Integer allowed = WaAdjustParaTool.getWaOrg(context.getPk_group());
			if (allowed.equals(1)) {
				busiregionEnum = ManagescopeBusiregionEnum.psndoc;
			}
			sqlquery.append(" where ( hi_psnjob.pk_psnjob in "
					+ ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(
							context.getPk_org(), busiregionEnum, true));
		} catch (BusinessException e) {
			throw new DAOException(e);
		}
		sqlquery.append("    and hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc ");
		// 人员组织关系是否限定
		sqlquery.append("    and hi_psnorg.indocflag = 'Y' ");
		sqlquery.append("    and hi_psnorg.lastflag = 'Y' ");

		// 兼职记录也要查出来
		UFBoolean partflagShow = WaAdjustParaTool.getPartjob_Adjmgt(context
				.getPk_group());
		if (!partflagShow.booleanValue()) {
			sqlquery.append("    and hi_psnjob.ismainjob = 'Y' ");
		}
		sqlquery.append(" )");
//		sqlquery.append(this.queryPsnAppSqlExtForImport(context));
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "hi_psnjob");
		if (!StringUtils.isEmpty(powerSql)) {
			sqlquery.append(" and " + powerSql);
		}
		sqlquery.append(" 			and	bd_psndoc.id in (" + idcards + ")   ");
		// 过滤未启用组织职能的人员
		sqlquery.append(" and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable)  ");
		sqlquery.append(" order by  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ " ,bd_psndoc.code");
		return executeQueryVOs(sqlquery.toString(), WASpecialAdditionaDeductionVO.class);
	}

	/**
	 * 根据主键查询
	 * 
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public WASpecialAdditionaDeductionVO[] queryDataByPks(String[] pks) throws BusinessException {

		InSQLCreator ins = new InSQLCreator();
		String insql = ins.getInSQL(pks, true);
		StringBuffer sbsql = new StringBuffer();
		sbsql.append(" SELECT  ");
		sbsql.append(" bd_psndoc.pk_psndoc,  ");
		sbsql.append(" bd_psndoc.code psncode,  ");
		sbsql.append(" bd_psndoc.NAME psnname,  ");
		sbsql.append(" bd_psndoc.id idcard,  ");
		sbsql.append(" org_dept.NAME deptname,  ");
		sbsql.append(" (select user_name from sm_user where cuserid = wa_deduction.creator) creator, ");
		sbsql.append(" wa_deduction.creationtime, ");
		sbsql.append(" wa_deduction.TYPE,  ");
		sbsql.append(" wa_deduction.source,  ");
		sbsql.append(" (  ");
		sbsql.append(" CASE   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 1   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_CHILD_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 2   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_EDUCATION_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 3   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_ZU_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 4   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HOURSE_NAME + "'   ");
		sbsql.append(" WHEN wa_deduction.TYPE = 5   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_PARENT_NAME + "'   ");
		sbsql.append(" 	WHEN wa_deduction.TYPE = 6   ");
		sbsql.append(" 	THEN '" + TaxupgradeDef.ADDDEDUCTION_KEY_HEALTH_NAME + "'   ");
		sbsql.append(" END) typename,  ");
		sbsql.append(" wa_deduction.pk_deduction,  ");
		sbsql.append(" wa_deduction.begin_year,  ");
		sbsql.append(" wa_deduction.begin_period,  ");
		sbsql.append(" wa_deduction.end_year,  ");
		sbsql.append(" wa_deduction.end_period,  ");
		sbsql.append(" wa_deduction.amount ,  ");
		sbsql.append(" wa_deduction.amount amountimp   ");
		sbsql.append(" FROM  ");
		sbsql.append(" 	bd_psndoc   ");
		sbsql.append(" 		INNER JOIN hi_psnjob   ");
		sbsql.append(" 	ON bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc   ");
		sbsql.append(" 		INNER JOIN org_dept   ");
		sbsql.append(" 		ON hi_psnjob.pk_dept = org_dept.pk_dept   ");
		sbsql.append(" 			inner JOIN wa_deduction   ");
		sbsql.append(" 		ON wa_deduction.pk_psndoc = bd_psndoc.pk_psndoc  ");
		sbsql.append(" 			WHERE   ");
		sbsql.append(" 				hi_psnjob.ismainjob = 'Y' AND   ");
		sbsql.append(" 				lastflag = 'Y'    ");
		if (!StringUtil.isEmpty(insql)) {
			sbsql.append(" and wa_deduction.pk_deduction in (" + insql + ")");
		}
		sbsql.append(" 			ORDER BY   ");
		sbsql.append(" 				bd_psndoc.code,   ");
		sbsql.append(" 				wa_deduction.TYPE   ");

		SQLParameter sqlp = new SQLParameter();

		WASpecialAdditionaDeductionVO[] vos = this.executeQueryVOs(sbsql.toString(), sqlp,
				WASpecialAdditionaDeductionVO.class);
		return vos;
	}

	/**
	 * 获取map
	 * 
	 * @return MAP <psndoc，datavo>
	 * @throws BusinessException
	 */
	public Map<String, WASpecialAdditionaDeductionVO> convertToMap(WASpecialAdditionaDeductionVO[] datavos, String key)
			throws BusinessException {
		Map<String, WASpecialAdditionaDeductionVO> map = new HashMap<String, WASpecialAdditionaDeductionVO>();
		if (datavos != null && datavos.length > 0) {
			for (WASpecialAdditionaDeductionVO datavo : datavos) {
				map.put(datavo.getAttributeValue(key) + "", datavo);
			}
		}
		return map;
	}

	public void insertVOs(List<WASpecialAdditionaDeductionVO> vos) throws BusinessException {
		StringBuffer insertSqls = new StringBuffer();
		insertSqls.append(" INSERT INTO wa_deduction (");
		insertSqls.append(" begin_year,");
		insertSqls.append(" pk_psndoc,");
		insertSqls.append(" circle_period,");
		insertSqls.append(" TYPE,");
		insertSqls.append(" creator,");
		insertSqls.append(" amount,");
		insertSqls.append(" apply_date,");
		insertSqls.append(" end_year,");
		insertSqls.append(" def5,");
		insertSqls.append(" end_period,");
		insertSqls.append(" pk_source,");
		insertSqls.append(" idcard,");
		insertSqls.append(" creationtime,");
		insertSqls.append(" SOURCE,");
		insertSqls.append(" def4,");
		insertSqls.append(" def3,");
		insertSqls.append(" begin_period,");
		insertSqls.append(" def2,");
		insertSqls.append(" def1,");
		insertSqls.append(" pk_deduction ");
		insertSqls.append(" ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		SQLParameter sqlP = new SQLParameter();
		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JdbcSession session = sessionManager.getJdbcSession();
		String[] strIDs = DBAUtil.getIdGenerator().generate(vos.size());
		try {
			int i = 0;
			for (WASpecialAdditionaDeductionVO vo : vos) {
				sqlP = new SQLParameter();
				sqlP.addParam(vo.getBegin_year());
				sqlP.addParam(vo.getPk_psndoc());
				sqlP.addParam(vo.getCircle_period());
				sqlP.addParam(vo.getType());
				sqlP.addParam(vo.getCreator());
				sqlP.addParam(vo.getAmount());
				sqlP.addParam(vo.getApply_date());
				sqlP.addParam(vo.getEnd_year());
				sqlP.addParam(vo.getDef5());
				sqlP.addParam(vo.getEnd_period());
				sqlP.addParam(vo.getPk_source());
				sqlP.addParam(vo.getIdcard());
				sqlP.addParam(vo.getCreationtime());
				sqlP.addParam(vo.getSource());
				sqlP.addParam(vo.getDef4());
				sqlP.addParam(vo.getDef3());
				sqlP.addParam(vo.getBegin_period());
				sqlP.addParam(vo.getDef2());
				sqlP.addParam(vo.getDef1());
				sqlP.addParam(strIDs[i++]);
				session.addBatch(insertSqls.toString(), sqlP);

			}
			session.executeBatch();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private String getBDSql() {
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			return "+";
		} else {
			return "||";
		}
	}
}