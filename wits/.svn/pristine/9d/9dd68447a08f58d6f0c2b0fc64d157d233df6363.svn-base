package nc.impl.wa.taxspecial_statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.itf.hr.frame.IHRDataPermissionPubService;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.taxspecial_statistics.TaxSpecialStatisticsVO;
import nc.vo.wa_tax.TaxupgradeDef;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: xuhw
 * @date: 2018-12-04
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxSpecialStatisticsDAO extends AppendBaseDAO implements ICommonAlterName {

	public TaxSpecialStatisticsVO[] queryByCondition(String[] pk_wa_datas) throws BusinessException {
		InSQLCreator inSQLCreator = new InSQLCreator();
		String insql = inSQLCreator.getInSQL(pk_wa_datas);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append(" select * from wa_data inner join wa_spe_statis on wa_data.pk_wa_data = wa_spe_statis.pk_wa_data ");
		sqlBuffer.append(" where wa_data.pk_wa_data in ( ").append(insql).append(")");

		TaxSpecialStatisticsVO[] vos = this.executeQueryVOs(sqlBuffer.toString(), TaxSpecialStatisticsVO.class);
		return vos;
	}

	public DataVO[] queryByPKSCondition(String pk_condtion, String orderCondtion) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5

		sqlBuffer.append("       om_job.jobname, "); // 6
		sqlBuffer.append(" spe.parentfeetotal, spe.paretnfeedtotal, spe.parentfee,");
		sqlBuffer.append(" spe.pk_spe_statis, spe.parentfeetotal, spe.paretnfeedtotal,");
		sqlBuffer.append(" spe.parentfee, spe.childfeetotal, spe.childfeedtotal, spe.childfee,");
		sqlBuffer.append(" spe.hoursefeetotal, spe.hoursefeedtotal, spe.hoursefee,");
		sqlBuffer.append(" spe.hoursezufeetotal, spe.hoursezufeedtotal, spe.hoursezufee ,");
		sqlBuffer.append(" spe.educationfeetotal, spe.educationfeedtotal, spe.educationfee,");
		sqlBuffer.append(" spe.healthyfeetotal,spe.healthyfeedtotal,spe.healthyfee ,");
		sqlBuffer.append(" spe.allfeetotal, spe.allfeedtotal, spe.allfee,spe.tax_type, spe.lockflag ,");
		sqlBuffer.append(" (case when wa_data.checkflag = 'Y' then '已审核' else '未审核' end) checkflagname ,");

		sqlBuffer.append(" (case when spe.tax_type = 0 then '未生成' when spe.tax_type = 1 then '已生成' when   spe.tax_type = 2 then '已锁定' end) tax_type_name ,");
		sqlBuffer.append(" spe.tax_type ,");
//		sqlBuffer
//				.append("  (select (select  "
//						+ SQLHelper.getMultiLangNameColumn("name")
//						+ " from wa_waclass where pk_wa_class = lockclass )lockclass from wa_spe_statis where pk_psndoc = wa_data.pk_psndoc and taxyear = spe.taxyear and taxperiod = spe.taxperiod and tax_type = 2) lockclassname, "); // 6
		// guoqt岗位
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlBuffer.append("       wa_data.*,datapower.operateflag "); // 7
		sqlBuffer.append("   , (	SELECT taxyear  FROM wa_period  WHERE pk_periodscheme = wa_waclass.pk_periodscheme  ");
		sqlBuffer.append("    		 AND cyear = wa_data.cyear AND cperiod = wa_data.cperiod  ) taxyear ,");
		sqlBuffer
				.append("    (	SELECT taxperiod  FROM wa_period  WHERE pk_periodscheme = wa_waclass.pk_periodscheme  ");
		sqlBuffer.append("    		 AND cyear = wa_data.cyear AND cperiod = wa_data.cperiod  ) taxperiod  ");

		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");

		sqlBuffer.append(" inner join wa_waclass on wa_data.pk_wa_class = wa_waclass.pk_wa_class ");

		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append(" left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		sqlBuffer.append("  left outer join wa_spe_statis spe on wa_data.pk_wa_data = spe.pk_wa_data ");

		String operateConditon = NCLocator
				.getInstance()
				.lookup(IHRDataPermissionPubService.class)
				.getDataRefSQLWherePartByMDOperationCode(PubEnv.getPk_user(), PubEnv.getPk_group(),
						IHRWADataResCode.WADATA, IHRWAActionCode.EDIT, "wa_data");
		if (StringUtils.isEmpty(operateConditon)) {
			operateConditon = " 1 = 1 ";
		}

		sqlBuffer.append(" left outer join (select 'Y' as operateflag,pk_wa_data from wa_data  where ");
		sqlBuffer.append(operateConditon);
		sqlBuffer.append(") datapower on wa_data.pk_wa_data = datapower.pk_wa_data ");
		if (!StringUtil.isEmpty(pk_condtion)) {

			sqlBuffer.append(" where  wa_data.pk_wa_data in (" + pk_condtion + ")");

		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}

		DataVO[] datavos = executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
		datavos = this.queryLockedData(datavos);
		return datavos;

	}

	public TaxSpecialStatisticsVO[] queryTaxVOByPKSCondition(String[] pk_wa_datas, String orderCondtion)
			throws BusinessException {
		InSQLCreator inSQLCreator = new InSQLCreator();
		String insql = inSQLCreator.getInSQL(pk_wa_datas);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5

		sqlBuffer.append("       om_job.jobname, "); // 6
		// guoqt岗位
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlBuffer.append("       wa_data.*,datapower.operateflag "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append(" left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		sqlBuffer.append("  left outer join wa_spe_statis spe on wa_data.pk_wa_data = spe.pk_wa_data ");

		String operateConditon = NCLocator
				.getInstance()
				.lookup(IHRDataPermissionPubService.class)
				.getDataRefSQLWherePartByMDOperationCode(PubEnv.getPk_user(), PubEnv.getPk_group(),
						IHRWADataResCode.WADATA, IHRWAActionCode.EDIT, "wa_data");
		if (StringUtils.isEmpty(operateConditon)) {
			operateConditon = " 1 = 1 ";
		}

		sqlBuffer.append(" left outer join (select 'Y' as operateflag,pk_wa_data from wa_data  where ");
		sqlBuffer.append(operateConditon);
		sqlBuffer.append(") datapower on wa_data.pk_wa_data = datapower.pk_wa_data ");
		// TODO 完善查询
		if (!StringUtil.isEmpty(insql)) {

			sqlBuffer.append(" where  wa_data.pk_wa_data in (" + insql + ")");

		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}

		return executeQueryVOs(sqlBuffer.toString(), TaxSpecialStatisticsVO.class);

	}

	/**
	 * 查询累计应扣减按照类型 amount，pk_psndoc, type
	 * 
	 * @param pk_wa_datas
	 * @param orderCondtion
	 * @return
	 * @throws BusinessException
	 */
	public DataVO[] queryTotalableAmts(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" SELECT coalesce(SUM( ");
		sqlBuffer.append(" CASE  ");
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER){
			sqlBuffer.append(" WHEN temp.taxyearperiod >= temp.beginyearperiod AND ");
			sqlBuffer.append(" temp.taxyearperiod <= COALESCE(temp.endyearperiod, 999901 )  ");
			
		}else {
			sqlBuffer.append("WHEN to_number(temp.taxyearperiod) >= to_number(temp.beginyearperiod) ");
					sqlBuffer.append("		AND to_number(temp.taxyearperiod) <= COALESCE (to_number(temp.endyearperiod), to_number('999901'))" +
							" ");
					
		}

		sqlBuffer.append(" THEN temp.amount  ");
		sqlBuffer.append(" END),0) amount , ");
		sqlBuffer.append(" MAX(pk_psndoc  " + getBDSql() + " type) pk_psndoc   ");
		sqlBuffer.append(" FROM ");
		sqlBuffer.append(" 	(	SELECT ");
		sqlBuffer.append(" 			DISTINCT (wadata.cyear  " + getBDSql() + " wadata.cperiod) cyearperiod, ");
		sqlBuffer.append(" 			(	SELECT ");
		sqlBuffer.append(" 					taxyear  " + getBDSql() + " taxperiod  ");
		sqlBuffer.append(" 				FROM ");
		sqlBuffer.append(" 					wa_period  ");
		sqlBuffer.append(" 				WHERE ");
		sqlBuffer.append(" 					pk_periodscheme = wa_waclass.pk_periodscheme AND ");
		sqlBuffer.append(" 					taxyear = wadata.cyear AND ");
		sqlBuffer.append(" 					taxperiod = wadata.cperiod  ");
		sqlBuffer.append(" 			) ");
		sqlBuffer.append(" 			taxyearperiod, ");
		sqlBuffer.append(" 			wadata.pk_psndoc  ,  ");
		sqlBuffer.append(" 			(	SELECT ");
		sqlBuffer.append(" 					NAME  ");
		sqlBuffer.append(" 				FROM ");
		sqlBuffer.append(" 					bd_psndoc  ");
		sqlBuffer.append(" 				WHERE ");
		sqlBuffer.append(" 					pk_psndoc = wadata.pk_psndoc  ");
		sqlBuffer.append(" 			) ");
		sqlBuffer.append(" 			panname , ");
		sqlBuffer.append(" 			deduction.begin_year, ");
		sqlBuffer.append(" 			deduction.begin_period, ");
		sqlBuffer.append(" 			(deduction.begin_year " + getBDSql() + " deduction.begin_period) beginyearperiod, ");
		sqlBuffer.append(" 			deduction.end_year, ");
		sqlBuffer.append(" 			deduction.end_period , ");
		sqlBuffer.append(" 			( deduction.end_year " + getBDSql() + " deduction.end_period ) endyearperiod, ");
		sqlBuffer.append(" 			deduction.amount ,deduction.type ");
		sqlBuffer.append(" 		FROM ");
		sqlBuffer.append(" 			wa_data wadata  ");
		sqlBuffer.append(" 			INNER JOIN wa_waclass  ");
		sqlBuffer.append(" 			ON wa_waclass.pk_wa_class = wadata.pk_wa_class  ");
		sqlBuffer.append(" 			INNER JOIN wa_deduction deduction  ");
		sqlBuffer.append(" 			ON deduction.pk_psndoc = wadata.pk_psndoc  ");
		sqlBuffer.append(" 		WHERE ");
		sqlBuffer.append(" 			WADATA.pk_org = ? AND ");
		sqlBuffer.append(" 			wadata.pk_psndoc IN (	SELECT ");
		sqlBuffer.append(" 										pk_psndoc  ");
		sqlBuffer.append(" 									FROM ");
		sqlBuffer.append(" 										wa_data  ");
		sqlBuffer.append(" 									WHERE ");
		sqlBuffer.append(" 										pk_wa_class = ? AND ");
		sqlBuffer.append(" 										cyear = ? AND ");
		sqlBuffer.append(" 										cperiod = ?  ");
		sqlBuffer.append(" 			) ");
		sqlBuffer.append(" 			AND ");
		sqlBuffer.append(" 			wadata.cyear  " + getBDSql() + " wadata.cperiod IN (	SELECT ");
		sqlBuffer.append(" 										cyear " + getBDSql() + " cperiod  ");
		sqlBuffer.append(" 									FROM ");
		sqlBuffer.append(" 										wa_period  ");
		sqlBuffer.append(" 									WHERE ");
		sqlBuffer.append(" 										PK_PERIODSCHEME = (	SELECT ");
		sqlBuffer.append(" 																PK_PERIODSCHEME  ");
		sqlBuffer.append(" 															FROM ");
		sqlBuffer.append(" 																wa_waclass  ");
		sqlBuffer.append(" 															WHERE ");
		sqlBuffer.append(" 																PK_WA_CLASS =  ?  ");
		sqlBuffer.append(" 										) ");
		sqlBuffer.append(" 										AND ");
		sqlBuffer.append(" 										TAXYEAR = (	SELECT ");
		sqlBuffer.append(" 														period.TAXYEAR  ");
		sqlBuffer.append(" 													FROM ");
		sqlBuffer.append(" 														wa_waclass waclass  ");
		sqlBuffer.append(" 														INNER JOIN wa_period  ");
		sqlBuffer.append(" 														period  ");
		sqlBuffer.append(" 														ON period.PK_PERIODSCHEME =  WACLASS. PK_PERIODSCHEME  ");
		sqlBuffer.append(" 													WHERE  ");
		sqlBuffer.append(" 														WACLASS.PK_WA_CLASS = ?  ");
		sqlBuffer.append(" 														AND  ");
		sqlBuffer.append(" 													PERIOD.CYEAR = WACLASS.CYEAR AND ");
		sqlBuffer.append(" 														PERIOD.CPERIOD = WACLASS.CPERIOD  ");
		sqlBuffer.append(" 									) ");
		sqlBuffer.append(" 		) ");
		sqlBuffer.append(" ) ");
		sqlBuffer.append(" temp group by pk_psndoc, type order by pk_psndoc, type ");
		SQLParameter sqlParameter = new SQLParameter();
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String pk_org = context.getPk_org();
		sqlParameter.addParam(pk_org);
		sqlParameter.addParam(pk_wa_class);
		sqlParameter.addParam(cyear);
		sqlParameter.addParam(cperiod);
		sqlParameter.addParam(pk_wa_class);
		sqlParameter.addParam(pk_wa_class);
		DataVO[] datas = this.executeQueryVOs(sqlBuffer.toString(), sqlParameter, DataVO.class);
		return datas;
	}

	/**
	 * 获取map
	 * 
	 * @return MAP <psndoc，datavo>
	 * @throws BusinessException
	 */
	public Map<String, DataVO> convertToMap(DataVO[] datavos, String key) throws BusinessException {
		Map<String, DataVO> map = new HashMap<String, DataVO>();
		if (datavos != null && datavos.length > 0) {
			for (DataVO datavo : datavos) {
				map.put(datavo.getAttributeValue(key) + "", datavo);
			}
		}
		return map;

	}

	/**
	 * 查询累计应扣减 (all) amount，pk_psndoc, type
	 * 
	 * @param pk_wa_datas
	 * @param orderCondtion
	 * @return
	 * @throws BusinessException
	 */
	public DataVO[] queryTotalableAllAmts(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String pk_org = context.getPk_org();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" SELECT coalesce(SUM( ");
		sqlBuffer.append(" CASE  ");
		sqlBuffer.append(" WHEN temp.taxyearperiod >= beginyearperiod AND ");
		sqlBuffer.append(" temp.taxyearperiod <= endyearperiod  ");
		sqlBuffer.append(" THEN amount  ");
		sqlBuffer.append(" END),0) amount , ");
		sqlBuffer.append(" MAX(pk_psndoc) pk_psndoc   ");
		sqlBuffer.append(" FROM ");
		sqlBuffer.append(" 	(	SELECT ");
		sqlBuffer.append(" 			DISTINCT (wadata.cyear  " + getBDSql() + " wadata.cperiod) cyearperiod, ");
		sqlBuffer.append(" 			(	SELECT ");
		sqlBuffer.append(" 					taxyear  " + getBDSql() + " taxperiod  ");
		sqlBuffer.append(" 				FROM ");
		sqlBuffer.append(" 					wa_period  ");
		sqlBuffer.append(" 				WHERE ");
		sqlBuffer.append(" 					pk_periodscheme = wa_waclass.pk_periodscheme AND ");
		sqlBuffer.append(" 					taxyear = wadata.cyear AND ");
		sqlBuffer.append(" 					taxperiod = wadata.cperiod  ");
		sqlBuffer.append(" 			) ");
		sqlBuffer.append(" 			taxyearperiod, ");
		sqlBuffer.append(" 			wadata.pk_psndoc  ,  ");
		sqlBuffer.append(" 			deduction.begin_year, ");
		sqlBuffer.append(" 			deduction.begin_period, ");
		sqlBuffer.append(" 			(deduction.begin_year + deduction.begin_period) beginyearperiod, ");
		sqlBuffer.append(" 			deduction.end_year, ");
		sqlBuffer.append(" 			deduction.end_period , ");
		sqlBuffer.append(" 			( deduction.end_year + deduction.end_period ) endyearperiod, ");
		sqlBuffer.append(" 			deduction.amount  ");
		sqlBuffer.append(" 		FROM ");
		sqlBuffer.append(" 			wa_data wadata  ");
		sqlBuffer.append(" 			INNER JOIN wa_waclass  ");
		sqlBuffer.append(" 			ON wa_waclass.pk_wa_class = wadata.pk_wa_class  ");
		sqlBuffer.append(" 			INNER JOIN wa_deduction deduction  ");
		sqlBuffer.append(" 			ON deduction.pk_psndoc = wadata.pk_psndoc  ");
		sqlBuffer.append(" 		WHERE ");
		sqlBuffer.append(" 			WADATA.pk_org = ? AND ");// 1 param
		sqlBuffer.append(" 			wadata.pk_psndoc IN (	SELECT ");
		sqlBuffer.append(" 										pk_psndoc  ");
		sqlBuffer.append(" 									FROM ");
		sqlBuffer.append(" 										wa_data  ");
		sqlBuffer.append(" 									WHERE ");
		sqlBuffer.append(" 										pk_wa_class = ? AND ");// 2 param
		sqlBuffer.append(" 										cyear = ? AND ");// 3 param
		sqlBuffer.append(" 										cperiod = ?  ");// 4 param
		sqlBuffer.append(" 			) ");
		sqlBuffer.append(" 			AND ");
		sqlBuffer.append(" 			wadata.cyear  " + getBDSql() + " wadata.cperiod IN (	SELECT ");
		sqlBuffer.append(" 										cyear + cperiod  ");
		sqlBuffer.append(" 									FROM ");
		sqlBuffer.append(" 										wa_period  ");
		sqlBuffer.append(" 									WHERE ");
		sqlBuffer.append(" 										PK_PERIODSCHEME = (	SELECT ");
		sqlBuffer.append(" 																PK_PERIODSCHEME  ");
		sqlBuffer.append(" 															FROM ");
		sqlBuffer.append(" 																wa_waclass  ");
		sqlBuffer.append(" 															WHERE ");
		sqlBuffer.append(" 																PK_WA_CLASS =  ?  ");// 5 param
		sqlBuffer.append(" 										) ");
		sqlBuffer.append(" 										AND ");
		sqlBuffer.append(" 										TAXYEAR = (	SELECT ");
		sqlBuffer.append(" 														period.TAXYEAR  ");
		sqlBuffer.append(" 													FROM ");
		sqlBuffer.append(" 														wa_waclass waclass  ");
		sqlBuffer.append(" 														INNER JOIN wa_period  ");
		sqlBuffer.append(" 														period  ");
		sqlBuffer.append(" 														ON period.PK_PERIODSCHEME =  WACLASS. PK_PERIODSCHEME  ");
		sqlBuffer.append(" 													WHERE  ");
		sqlBuffer.append(" 														WACLASS.PK_WA_CLASS = ?  ");// 6 param
		sqlBuffer.append(" 														AND  ");
		sqlBuffer.append(" 													PERIOD.CYEAR = WACLASS.CYEAR AND ");
		sqlBuffer.append(" 														PERIOD.CPERIOD = WACLASS.CPERIOD  ");
		sqlBuffer.append(" 									) ");
		sqlBuffer.append(" 		) ");
		sqlBuffer.append(" ) ");
		sqlBuffer.append(" temp group by pk_psndoc  order by pk_psndoc  ");

		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(pk_org);
		sqlParameter.addParam(pk_wa_class);
		sqlParameter.addParam(cyear);
		sqlParameter.addParam(cperiod);
		sqlParameter.addParam(pk_wa_class);
		sqlParameter.addParam(pk_wa_class);
		DataVO[] datas = this.executeQueryVOs(sqlBuffer.toString(), sqlParameter, DataVO.class);
		return datas;
	}

	/**
	 * 获取所有已经扣除的附加费用扣除额
	 * 
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @return
	 * @throws BusinessException
	 */
	public DataVO[] queryTotaledAllAmts(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String pk_org = context.getPk_org();
		StringBuffer sqlBuffer = new StringBuffer();

		sqlBuffer.append(" SELECT wadata.pk_psndoc, ");
		sqlBuffer.append(" coalesce(SUM(spe.parentfee),0) parentfee, ");
		sqlBuffer.append(" coalesce(SUM(spe.hoursefee),0) hoursefee, ");
		sqlBuffer.append(" coalesce(SUM(spe.hoursezufee),0) hoursezufee, ");
		sqlBuffer.append(" coalesce(SUM(spe.childfee),0) childfee, ");
		sqlBuffer.append(" coalesce(SUM(spe.healthyfee),0) healthyfee, ");
		sqlBuffer.append(" coalesce(SUM(spe.educationfee),0) educationfee, ");
		sqlBuffer.append(" coalesce(SUM(spe.allfee),0) allfee  ");
		sqlBuffer.append(" FROM wa_data wadata  ");
		sqlBuffer.append(" 	LEFT OUTER JOIN wa_spe_statis spe  ");
		sqlBuffer.append(" ON spe.pk_wa_data = wadata.pk_wa_data  ");
		sqlBuffer.append(" WHERE wadata.pk_psndoc IN (	SELECT ");
		sqlBuffer.append(" 	pk_psndoc  ");
		sqlBuffer.append(" FROM ");
		sqlBuffer.append(" 	wa_data  ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" pk_wa_class = ? AND cyear = ? AND cperiod = ?  ");
		sqlBuffer.append(" ) ");
		sqlBuffer.append(" AND ");
		sqlBuffer.append(" wadata.cyear  " + getBDSql() + " wadata.cperiod IN (	SELECT ");
		sqlBuffer.append(" 	cyear " + getBDSql() + " cperiod  ");
		sqlBuffer.append(" FROM wa_period  WHERE ");
		sqlBuffer.append(" PK_PERIODSCHEME = (	SELECT ");
		sqlBuffer.append(" 				PK_PERIODSCHEME ");
		sqlBuffer.append(" 	FROM ");
		sqlBuffer.append(" 	wa_waclass ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" PK_WA_CLASS = ?  ");// 4
		sqlBuffer.append(" ) AND ");
		sqlBuffer.append(" TAXYEAR = (	SELECT ");
		sqlBuffer.append(" period. TAXYEAR  ");
		sqlBuffer.append(" 			FROM ");
		sqlBuffer.append(" 				wa_waclass waclass ");
		sqlBuffer.append(" 				INNER JOIN wa_period period ");
		sqlBuffer.append(" 				ON period. PK_PERIODSCHEME = WACLASS. PK_PERIODSCHEME ");
		sqlBuffer.append(" 			WHERE ");
		sqlBuffer.append(" 				WACLASS. PK_WA_CLASS = ?  AND ");// 5
		sqlBuffer.append(" 				PERIOD.CYEAR = WACLASS.CYEAR AND ");
		sqlBuffer.append(" 				PERIOD. CPERIOD = WACLASS. CPERIOD ");
		sqlBuffer.append(" ) ");
		sqlBuffer.append(" ) ");
		sqlBuffer.append(" AND ");
		sqlBuffer.append(" ( (wadata.pk_wa_class = ?  AND ");// 6
		sqlBuffer.append(" wadata.cyear  " + getBDSql() + " wadata.cperiod < ? ) OR ");
		sqlBuffer.append(" wadata.pk_wa_class <> ?  ) AND ");
		sqlBuffer.append(" spe.tax_type = 2  ");
		sqlBuffer.append(" and wadata.pk_org = ?  ");
		sqlBuffer.append(" GROUP BY ");
		sqlBuffer.append(" wadata.pk_psndoc ");
		SQLParameter sqlParameter = new SQLParameter();
		sqlParameter.addParam(pk_wa_class);// 1
		sqlParameter.addParam(cyear);
		sqlParameter.addParam(cperiod);
		sqlParameter.addParam(pk_wa_class);// 4
		sqlParameter.addParam(pk_wa_class);// 5
		sqlParameter.addParam(pk_wa_class);// 6
		sqlParameter.addParam(cyear + cperiod);
		sqlParameter.addParam(pk_wa_class);// 8
		sqlParameter.addParam(pk_org);// 8
		DataVO[] datas = this.executeQueryVOs(sqlBuffer.toString(), sqlParameter, DataVO.class);
		return datas;
	}

	/**
	 * 删除所有专项扣除数据，方案所有数据都没被审核，生成数据未被锁定
	 * 
	 * @param context
	 * @param condition
	 * @throws DAOException
	 */
	public void deleteByCondition(WaLoginContext context, String condition) throws DAOException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" delete from wa_spe_statis where pk_wa_class = ?  ");
		sqlBuffer.append(" and cyear = ?  ");
		sqlBuffer.append(" and cperiod = ? and tax_type <> 2 ");
		if (!StringUtils.isEmpty(condition)) {
			sqlBuffer.append(" and pk_wa_data in (" + condition + ")  ");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_wa_class);
		sp.addParam(cyear);
		sp.addParam(cperiod);
		this.getBaseDao().executeUpdate(sqlBuffer.toString(), sp);
	}

	/**
	 * 是否存在已经审核的数据
	 * 
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @throws BusinessException
	 */
	public boolean isHasWadataCheckedData(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String sql = " select 1 from wa_data where pk_wa_class = '" + pk_wa_class + "' and cyear = '" + cyear
				+ "' and cperiod = '" + cperiod + "' and checkflag = 'Y'";
		return this.isValueExist(sql);
	}

	/**
	 * 是否已经锁定
	 * 
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @throws BusinessException
	 */
	public boolean isHasWaLockedData(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String sql = " select 1 from wa_spe_statis where pk_wa_class ='" + pk_wa_class + "' and cyear = '" + cyear
				+ "' and cperiod = '" + cperiod + "'  and tax_type = '2'";
		return this.isValueExist(sql);
	}

	/**
	 * 是否已生成
	 * 
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @throws BusinessException
	 */
	public boolean isHasGenData(WaLoginContext context, String condition, String orderSQL) throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String sql = " select 1 from wa_spe_statis where pk_wa_class ='" + pk_wa_class + "' and cyear = '" + cyear
				+ "' and cperiod = '" + cperiod + "'  and tax_type = '0'";
		return this.isValueExist(sql);
	}

	/**
	 * 锁定 将本方案本期间内的更新成已锁定，但是要刨除掉别的方案已经锁定的（相同纳税期间）
	 * 对应薪资数据已经审核的不能锁定
	 * 
	 * @param context
	 * @param condition
	 * @throws DAOException
	 */
	public void lockType(WaLoginContext context, String condition) throws DAOException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" update wa_spe_statis set tax_type = 2  ");
		sqlBuffer.append(" 		WHERE ");
		sqlBuffer.append(" 			pk_wa_class = ? AND ");
		sqlBuffer.append(" cyear = ? AND ");
		sqlBuffer.append(" cperiod = ? AND ");
		if (!StringUtils.isEmpty(condition)) {
			sqlBuffer.append(" pk_wa_data in (" + condition + ") and ");
		}

		sqlBuffer.append(" pk_psndoc IN (	SELECT ");
		sqlBuffer.append(" 					pk_psndoc  ");
		sqlBuffer.append(" 				FROM ");
		sqlBuffer.append(" 					wa_spe_statis spe2 ");
		sqlBuffer.append(" 				WHERE ");
		sqlBuffer.append(" 					spe2.tax_type = 1 AND ");
		sqlBuffer.append(" 					spe2.taxyear = wa_spe_statis.taxyear AND ");
		sqlBuffer.append(" 					spe2.taxperiod = wa_spe_statis.taxperiod ");
		sqlBuffer.append(" ) ");
		
		sqlBuffer.append(" and pk_psndoc IN (	SELECT ");
		sqlBuffer.append(" 					pk_psndoc  ");
		sqlBuffer.append(" 				FROM ");
		sqlBuffer.append(" 					wa_data ");
		sqlBuffer.append(" 				WHERE pk_wa_data in (" + condition + ")  ");
		sqlBuffer.append("  AND checkflag <> 'Y' ) ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_wa_class);
		sp.addParam(cyear);
		sp.addParam(cperiod);
		this.getBaseDao().executeUpdate(sqlBuffer.toString(), sp);

		// 更新relation方案
		sqlBuffer = new StringBuffer();
		sqlBuffer.append(" update wa_spe_statis set lockclass = ? ");
		sqlBuffer.append(" where pk_wa_class = ? ");
		sqlBuffer.append(" and cyear = ? ");
		sqlBuffer.append(" and cperiod = ? ");
		sqlBuffer.append(" and tax_type = 2 ");
		if (!StringUtils.isEmpty(condition)) {
			sqlBuffer.append(" and pk_wa_data in (" + condition + ")  ");
		}
		sp = new SQLParameter();
		sp.addParam(pk_wa_class);
		sp.addParam(pk_wa_class);
		sp.addParam(cyear);
		sp.addParam(cperiod);
		this.getBaseDao().executeUpdate(sqlBuffer.toString(), sp);

	}

	/**
	 * 解锁 将锁定状态2的更新为0 将关联方案更新为 null
	 * 薪资数据状态为已审核则不能解锁
	 * @param context
	 * @param condition
	 * @throws DAOException
	 */
	public void unLockType(WaLoginContext context, String condition) throws DAOException {
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" update wa_spe_statis set tax_type = 0  ");
		sqlBuffer.append(" 		WHERE ");
		sqlBuffer.append(" 			pk_wa_class = ? AND ");
		sqlBuffer.append(" cyear = ? AND ");
		sqlBuffer.append(" cperiod = ? AND tax_type = 2 ");
		if (!StringUtils.isEmpty(condition)) {
			sqlBuffer.append(" and pk_wa_data in (" + condition + ")  ");
		}
		sqlBuffer.append(" and pk_psndoc IN (	SELECT ");
		sqlBuffer.append(" 					pk_psndoc  ");
		sqlBuffer.append(" 				FROM ");
		sqlBuffer.append(" 					wa_data ");
		sqlBuffer.append(" 				WHERE pk_wa_data in (" + condition + ")  ");
		sqlBuffer.append("  AND checkflag <> 'Y' ) ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_wa_class);
		sp.addParam(cyear);
		sp.addParam(cperiod);
		this.getBaseDao().executeUpdate(sqlBuffer.toString(), sp);

		// 更新relation方案
		sqlBuffer = new StringBuffer();
		sqlBuffer.append(" update wa_spe_statis set lockclass = '' ");
		sqlBuffer.append(" where pk_wa_class = ? ");
		sqlBuffer.append(" and cyear = ? ");
		sqlBuffer.append(" and cperiod = ? and tax_type = 2 ");
		if (!StringUtils.isEmpty(condition)) {
			sqlBuffer.append(" and pk_wa_data in (" + condition + ")  ");
		}
		sqlBuffer.append(" and pk_psndoc IN (	SELECT ");
		sqlBuffer.append(" 					pk_psndoc  ");
		sqlBuffer.append(" 				FROM ");
		sqlBuffer.append(" 					wa_data ");
		sqlBuffer.append(" 				WHERE pk_wa_data in (" + condition + ")  ");
		sqlBuffer.append("  AND checkflag <> 'Y' ) ");
		sp = new SQLParameter();
		sp.addParam(pk_wa_class);
		sp.addParam(cyear);
		sp.addParam(cperiod);
		this.getBaseDao().executeUpdate(sqlBuffer.toString(), sp);
	}

	/**
	 * 获取某个纳税期间的已经被锁定的数据
	 * 
	 * @param context
	 * @param condition
	 * @param taxyear
	 * @param taxperiod
	 * @return
	 * @throws BusinessException
	 */
	public DataVO[] queryLockedData(DataVO[] datavos) throws BusinessException {
		String pk_psndocinsql = null;
		String taxyear = null;
		String taxperiod = null;
		String pk_wa_class = null;
		if (datavos != null && datavos.length > 0) {
			pk_psndocinsql = new InSQLCreator().getInSQL(datavos, "pk_psndoc");
			taxyear = datavos[0].getAttributeValue("taxyear") + "";
			taxperiod = datavos[0].getAttributeValue("taxperiod") + "";
			pk_wa_class = datavos[0].getAttributeValue("pk_wa_class") + "";

		} else {
			return null;
		}
		// 查询所有已经锁定的数据
		String str = " select wa_spe_statis.*,(select "+SQLHelper.getMultiLangNameColumn("name")+" from wa_waclass where pk_wa_class = lockclass ) classname from wa_spe_statis where taxyear = ? and taxperiod = ? and tax_type = 2   and pk_psndoc in ("
				+ pk_psndocinsql + ") ";
		SQLParameter param = new SQLParameter();
		param.addParam(taxyear);
		param.addParam(taxperiod);
		DataVO[] lockedDatavos = this.executeQueryVOs(str, param, DataVO.class);

		Map<String, DataVO> lockDataMap = convertToMap(lockedDatavos, "pk_psndoc");
		for (DataVO data : datavos) {
			String pk_psndoc = data.getPk_psndoc();
			DataVO lockData = lockDataMap.get(pk_psndoc);
			if (lockData != null) {
				data.setAttributeValue(TaxSpecialStatisticsVO.ALLFEE,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.ALLFEE));
				data.setAttributeValue(TaxSpecialStatisticsVO.ALLFEEDTOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.ALLFEEDTOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.ALLFEETOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.ALLFEETOTAL));

				data.setAttributeValue(TaxSpecialStatisticsVO.PARENTFEE,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.PARENTFEE));
				data.setAttributeValue(TaxSpecialStatisticsVO.PARENTFEETOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.PARENTFEETOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.PARETNFEEDTOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.PARETNFEEDTOTAL));

				data.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEE,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.CHILDFEE));
				data.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEEDTOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.CHILDFEEDTOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.CHILDFEETOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.CHILDFEETOTAL));

				data.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEE,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEE));
				data.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEEDTOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEEDTOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEETOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HEALTHYFEETOTAL));

				data.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEE,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEE));
				data.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEEDTOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEEDTOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEETOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.EDUCATIONFEETOTAL));

				data.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEE,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HOURSEFEE));
				data.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEEDTOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HOURSEFEEDTOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.HOURSEFEETOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HOURSEFEETOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEE,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEE));
				data.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEEDTOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEEDTOTAL));
				data.setAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEETOTAL,
						lockData.getAttributeValue(TaxSpecialStatisticsVO.HOURSEZUFEETOTAL));

				data.setAttributeValue("tax_type_name", "被锁定");
				data.setAttributeValue("lockclassname", lockData.getAttributeValue("classname"));
			}
		}

		return datavos;
	}

	public void insertVOs(List<TaxSpecialStatisticsVO> vos) {
		// INSERT INTO wa_spe_statis
		// (pk_wa_class,parentfee,pk_spe_statis,hoursefeetotal,educationfee,cperiod,cyear,lockclassname,paretnfeedtotal,healthyfee,healthyfeetotal,taxyear,allfeetotal,pk_psndoc,educationfeedtotal,hoursezufeedtotal,childfee,allfeedtotal,allfee,educationfeetotal,dr,pk_wa_data,hoursefee,childfeedtotal,hoursefeedtotal,tax_type,lockflag,childfeetotal,parentfeetotal,hoursezufee,taxperiod,lockclass,healthyfeedtotal,hoursezufeetotal)
		// VALUES
		// (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
		// 无效的列类型: 1111
		StringBuffer insertSqls = new StringBuffer();
		insertSqls.append(" INSERT INTO wa_spe_statis (pk_wa_class,");
		insertSqls.append(" parentfee,pk_spe_statis,hoursefeetotal,educationfee,");
		insertSqls.append(" cperiod,cyear,lockclassname,paretnfeedtotal,healthyfee,healthyfeetotal,taxyear,allfeetotal,pk_psndoc,");
		insertSqls.append(" educationfeedtotal,hoursezufeedtotal,childfee,allfeedtotal,allfee,");
		insertSqls.append(" educationfeetotal,dr,pk_wa_data,hoursefee,childfeedtotal,hoursefeedtotal,tax_type,");
		insertSqls.append(" lockflag,childfeetotal,parentfeetotal,hoursezufee,taxperiod,lockclass,healthyfeedtotal,hoursezufeetotal");
		insertSqls.append(" ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
			for (TaxSpecialStatisticsVO vo : vos) {
				sqlP = new SQLParameter();
				sqlP.addParam(vo.getPk_wa_class());//pk_wa_class
				sqlP.addParam(vo.getParentfee());//parentfee
				sqlP.addParam(strIDs[i++]);//pk_spe_statis
				sqlP.addParam(vo.getHoursefeetotal());//hoursefeetotal
				sqlP.addParam(vo.getEducationfee());//educationfee
				sqlP.addParam(vo.getCperiod());//cperiod
				sqlP.addParam(vo.getCyear());//cyear
				sqlP.addParam(vo.getLockclassname());//lockclassname
				sqlP.addParam(vo.getParetnfeedtotal());//paretnfeedtotal
				sqlP.addParam(vo.getHealthyfee());//healthyfee
				sqlP.addParam(vo.getHealthyfeetotal());//healthyfeetotal
				sqlP.addParam(vo.getTaxyear());//taxyear
				sqlP.addParam(vo.getAllfeetotal());//allfeetotal
				sqlP.addParam(vo.getPk_psndoc());//
				sqlP.addParam(vo.getEducationfeedtotal());//educationfeedtotal
				sqlP.addParam(vo.getHoursezufeedtotal());//hoursezufeedtotal
				sqlP.addParam(vo.getChildfee());//childfee
				sqlP.addParam(vo.getAllfeedtotal());//allfeedtotal
				sqlP.addParam(vo.getAllfee());//allfee
				sqlP.addParam(vo.getEducationfeetotal());//educationfeetotal
				sqlP.addParam(0);//
				sqlP.addParam(vo.getPk_wa_data());//pk_wa_data
				sqlP.addParam(vo.getHoursefee());//hoursefee
				sqlP.addParam(vo.getChildfeedtotal());//childfeedtotal
				sqlP.addParam(vo.getHoursefeedtotal());//hoursefeedtotal
				
				sqlP.addParam(vo.getTax_type());//
				sqlP.addParam(vo.getLockflag());//
				sqlP.addParam(vo.getChildfeetotal());//
				sqlP.addParam(vo.getParentfeetotal());//
				sqlP.addParam(vo.getHoursezufee());//
				sqlP.addParam(vo.getTaxperiod());//
				sqlP.addParam(vo.getLockclass());//
				sqlP.addParam(vo.getHealthyfeedtotal());//
				sqlP.addParam(vo.getHoursezufeetotal());//
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
