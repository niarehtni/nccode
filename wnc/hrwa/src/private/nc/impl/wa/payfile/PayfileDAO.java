package nc.impl.wa.payfile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.common.WaCommonImpl;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.itf.om.IAOSQueryService;
import nc.itf.om.IAOSQueryService.OrgQueryMode;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.payfile.ItemsVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.WACLASSTYPE;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.YearPeriodSeperatorVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 薪资档案DAO
 * 
 * @author: zhoucx
 * @date: 2009-12-24 下午03:40:00
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("restriction")
public class PayfileDAO extends AppendBaseDAO {

	/**
	 * 查询可以批量增加到档案的人员
	 * 
	 * @author liangxr on 2010-4-22
	 * @param loginContext
	 * @param condition
	 * @param orderCondtion
	 * @param msSql 业务委托sql
	 * @param type 1:正式人员；2:兼职人员；3：相关人员
	 * @return
	 * @throws BusinessException
	 */
	public PayfileVO[] queryBatchAddPayfileVO(WaLoginContext loginContext, String condition,
			String orderCondtion, String msSql,int type) throws BusinessException {

		InSQLCreator inSQLCreator = new InSQLCreator();

		try{
			PeriodVO periodVO = WADelegator.getPeriodQueryService().queryBySchemeYP(loginContext.getWaLoginVO().getPk_periodscheme(), loginContext.getWaYear(), loginContext.getWaPeriod());
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select hi_psnjob.pk_psnjob, "); // 1
			sqlBuffer.append("       hi_psnjob.pk_psnorg, "); // 2
			sqlBuffer.append("       hi_psnjob.assgid, "); // 3
			sqlBuffer.append("       hi_psnjob.pk_psndoc, "); // 4
			sqlBuffer.append("       hi_psnjob.clerkcode, "); // 5
			sqlBuffer.append("       "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  psnname, "); // 6
			sqlBuffer.append("       "+ SQLHelper.getMultiLangNameColumn("org_dept.name")+ "  deptname, "); // 7
			sqlBuffer.append("       "+ SQLHelper.getMultiLangNameColumn("om_post.postname")+ " postname, "); // 8
			sqlBuffer.append("       "+ SQLHelper.getMultiLangNameColumn("om_job.jobname")+ " jobname, "); // 9
			sqlBuffer.append("       "+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")+ " psnclname, "); // 9
			sqlBuffer.append("       "+ SQLHelper.getMultiLangNameColumn("org_orgs.name")+ "  orgname, "); // 10
			sqlBuffer.append("       org_orgs.pk_org as workorg, "); // 9
			sqlBuffer.append("       org_orgs.pk_vid as workorgvid, "); // 9
			sqlBuffer.append("       org_dept.pk_dept as workdept, "); // 9
			sqlBuffer.append("       org_dept.pk_vid as workdeptvid, "); // 9
			//新个税纳税组织
			sqlBuffer.append("       org_orgs.pk_corp as taxorg "); // 9
			sqlBuffer.append("  from hi_psnjob ");
			sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
			sqlBuffer.append("  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
			sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
			sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
			sqlBuffer.append("  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
			sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");

			//所有的人员都有部门　所以不需要　left outer join
			//          sqlBuffer.append("  inner join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
			sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
			//根据期间查询工作记录
			//sqlBuffer.append(" where hi_psnjob.lastflag = 'Y' ");
			sqlBuffer.append(" where  hi_psnorg.indocflag = 'Y' and hi_psnjob.begindate<= ?");
			sqlBuffer
			.append(" and (hi_psnjob.enddate>= ? or hi_psnjob.enddate is null)");

			sqlBuffer.append("   and not exists ");
			sqlBuffer.append("       (select pk_psndoc ");
			sqlBuffer.append("          from wa_data ");
			sqlBuffer.append("         where pk_wa_class = ? ");
			sqlBuffer.append("           and cyear = ? ");
			sqlBuffer.append("           and cperiod = ?  and wa_data.pk_psndoc  = bd_psndoc.pk_psndoc ) ");
			// 判断是否安装了人事模块
			boolean isEnable = true;
			isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(),
					PubEnv.MODULE_HRHI);
			if(!isEnable){
				sqlBuffer.append(" and bd_psndoc.enablestate = 2 ");
			}
			if (type == 1) {
				sqlBuffer.append("   and hi_psnjob.ismainjob = 'Y'  ");
			} else if (type == 2) {
				sqlBuffer.append("   and hi_psnjob.ismainjob = 'N'  ");
				//20151106 shenliangc NCdp205536726  批增兼职人员过滤掉已结束的兼职记录
				//无论兼职记录结束标志如何取值，只要时间上与本期间有交集，就应该可以查询到。
//				sqlBuffer.append("   and hi_psnjob.endflag = 'N' ");
			}
			if (type == 1 || type == 2) {
				sqlBuffer.append("   and hi_psnorg.psntype = 0 ");// 员工
				//                sqlBuffer.append("   and hi_psnorg.indocflag = 'Y' "); //组织关系中是否在档
				sqlBuffer.append("   and hi_psnorg.lastflag = 'Y' ");//最新的组织关系
				if (msSql != null) {
					if (type == 1) {
						// 主职取期间段内最后一条工作记录而非最新工作记录
						msSql = msSql.replaceAll(
								"and hi_psnjob.lastflag = 'Y'", "");
						sqlBuffer
						.append("    and hi_psnjob.begindate = (select max(psnjob.begindate) from hi_psnjob psnjob ");
						sqlBuffer
						.append(" where hi_psnjob.pk_psndoc = psnjob.pk_psndoc and hi_psnjob.pk_psnorg=psnjob.pk_psnorg");
						sqlBuffer
								.append(" and psnjob.begindate<= ? and (psnjob.enddate>= ? or psnjob.enddate is null) and psnjob.ismainjob='Y' ) ");
					}
					String tableName = createTempTable(msSql);
					sqlBuffer.append("   and hi_psnjob.pk_psnjob in " + " (select pk_psnjob from "+tableName+") ");
				}

			} else if (type == 3) {
				sqlBuffer.append("   and hi_psnorg.lastflag = 'Y' ");
				sqlBuffer.append("   and hi_psnorg.psntype = 1 ");// 相关人员
				IAOSQueryService aosQueryService = NCLocator.getInstance().lookup(
						IAOSQueryService.class);
				OrgVO[] orgVOs = aosQueryService.queryOrgByHROrgPK(
						loginContext.getPk_org(), null, null,
						OrgQueryMode.Independent);

				String[] pkOrgArray = new String[orgVOs.length];
				for (int i = 0; i < orgVOs.length; i++) {
					pkOrgArray[i] = orgVOs[i].getPk_org();
				}
				String pkOrgs = inSQLCreator.getInSQL(pkOrgArray);
				sqlBuffer.append("   and hi_psnjob.pk_org IN ( " + pkOrgs + " ) ");
			}
			// 过滤未启用组织职能的人员
			sqlBuffer
			.append(" and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) ");
			if (!StringUtil.isEmptyWithTrim(condition)) {
				sqlBuffer.append(" and hi_psnjob.pk_psnjob in (select pk_psnjob from hi_psnjob where ").append(
						condition).append(")");
			}

			//批量增加时，已经加入到离职结薪的人 不显示

			String pk_prnt_class =  loginContext.getPk_prnt_class();
			String waYear = loginContext.getWaYear();
			String waPeroiod = loginContext.getWaPeriod();
			String leavepsndoc = " select  wa_data.pk_psndoc from  wa_data  where wa_data.pk_psndoc =bd_psndoc.pk_psndoc and pk_wa_class in (  select wa_inludeclass.pk_childclass  from wa_inludeclass where pk_parentclass = '"+pk_prnt_class+"' and cyear = '"+waYear+"' and cperiod = '"+waPeroiod+"' and batch>100) and cyear = '"+waYear+"' and cperiod = '"+waPeroiod+"' ";
			sqlBuffer.append(" and not exists (" + leavepsndoc +")");



			String powerSql = WaPowerSqlHelper.getWaPowerSql(
					loginContext.getPk_group(),
					HICommonValue.RESOUCECODE_6007PSNJOB,
					IHRWADataResCode.WADEFAULT, "hi_psnjob");
			if (!StringUtils.isBlank(powerSql)) {
				sqlBuffer.append(" and " +powerSql);
			}

			if (!StringUtil.isEmptyWithTrim(orderCondtion)) {
				sqlBuffer.append(orderCondtion);
			}

			SQLParameter param = new SQLParameter();
			param.addParam(periodVO.getCenddate());
			param.addParam(periodVO.getCstartdate());
			param.addParam(loginContext.getPk_wa_class());
			param.addParam(loginContext.getWaYear());
			param.addParam(loginContext.getWaPeriod());
			if (type == 1) {
				param.addParam(periodVO.getCenddate());
				param.addParam(periodVO.getCstartdate());
			}

			return executeQueryVOs(sqlBuffer.toString(), param, PayfileVO.class);

		}finally {
			inSQLCreator.clear();
		}


	}



	/***************************************************************************
	 * 创建所有人临时表，里面包含pk_psnjob
	 * <br>Created on 2012-9-28 上午10:27:01<br>
	 * @return
	 * @author daicy
	 * @throws BusinessException
	 * @throws SQLException
	 * @throws NamingException
	 ***************************************************************************/
	private String createTempTable(String sql) throws BusinessException  {

		//创建临时表
		String tableName = HRWACommonConstants.WA_TEMP_PSNJOB;


		//创建临时表
		String  columns= "  pk_psnjob char(20) ";
		InSQLCreator inSQLCreator = new InSQLCreator();

		try
		{

			String pkindex = "pk_psnjob";


			//此表是临时表，事务结束后会自动清空！！

			tableName = inSQLCreator.createTempTable(tableName, columns,
					pkindex);

			String  columns2= "  pk_psnjob  ";


			StringBuilder sbd = new StringBuilder(" insert into  "+tableName+"   ("+columns2+") ");
			sbd.append(sql);

			new BaseDAO().executeUpdate(sbd.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BusinessException(e.getMessage());
		}finally{
			inSQLCreator.clear();
		}


		return tableName;
	}

	/**
	 * 查询档案中已存在的人员,未审核
	 * 
	 * @author liangxr on 2010-1-14
	 * @param loginContext
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws BusinessException
	 */
	public PayfileVO[] queryTransferPayfileVO(WaLoginContext loginContext, String condition,
			String orderCondtion) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer( getPayfileSelectField()[0]+getPayfileSelectField()[1]);

		sqlBuffer.append(" where "+WherePartUtil.getCommonWhereCondtion4Param(loginContext.getWaLoginVO()));

		if (!StringUtil.isEmptyWithTrim(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data "
					+ "where "+WherePartUtil.getCommonWhereCondtion4Param(loginContext.getWaLoginVO())+" and ")
					.append(condition).append(")");
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,IHRWADataResCode.WADEFAULT,"wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmptyWithTrim(orderCondtion)) {
			sqlBuffer.append(orderCondtion);
		}
		return executeQueryVOs(sqlBuffer.toString(), PayfileVO.class);
	}


	public String[] queryPKSByCondition(WaLoginContext context, String condition,
			String orderCondtion) throws BusinessException {
		
//		20150716 xiejie3 由于只需要查询pk_wa_data所以可以只查询这个字段，减少返回数据量，减少查询时间。方法querypkPayfileVO为新增的专为查询pk
		

//		PayfileVO[] vos = queryTransferPayfileVO(context, condition, orderCondtion);
		PayfileVO[] vos = querypkTransferPayfileVO(context, condition, orderCondtion);
		String[] pks = new String[0];
		if(vos!=null){
			pks = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				pks[i] = vos[i].getPk_wa_data();
			}

		}
		return pks;

	}
	
	public PayfileVO[] querypkTransferPayfileVO(WaLoginContext loginContext, String condition,
			String orderCondtion) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer( " select wa_data.pk_wa_data from wa_data "+getPayfileSelectField()[1]);

		sqlBuffer.append(" where "+WherePartUtil.getCommonWhereCondtion4Param(loginContext.getWaLoginVO()));

		if (!StringUtil.isEmptyWithTrim(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data "
					+ "where "+WherePartUtil.getCommonWhereCondtion4Param(loginContext.getWaLoginVO())+" and ")
					.append(condition).append(")");
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,IHRWADataResCode.WADEFAULT,"wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmptyWithTrim(orderCondtion)) {
			sqlBuffer.append(orderCondtion);
		}
		return executeQueryVOs(sqlBuffer.toString(), PayfileVO.class);
	}

	

	/**
	 * 查询档案中已存在的人员,未审核
	 * 
	 * @author liangxr on 2010-1-14
	 * @param loginContext
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws BusinessException
	 */
	public PayfileVO[] queryPayfileVOByPKS(String condition) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer( getPayfileSelectField()[0]+getPayfileSelectField()[1]);
		sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(condition));
		return executeQueryVOs(sqlBuffer.toString(), PayfileVO.class);
	}


	public String[] getPayfileSelectField(){
		String[] sql = new String[2];
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  ");
		sqlBuffer.append("       wa_data.pk_wa_data, ");
		sqlBuffer.append("       wa_data.pk_wa_class, ");
		sqlBuffer.append("       wa_data.cyearperiod, ");
		sqlBuffer.append("       wa_data.cyear, ");
		sqlBuffer.append("       wa_data.cperiod, ");
		sqlBuffer.append("       wa_data.pk_psnorg, ");
		sqlBuffer.append("       wa_data.pk_psnjob, ");
		sqlBuffer.append("       wa_data.pk_psndoc, ");
		sqlBuffer.append("       wa_data.pk_group, ");
		sqlBuffer.append("       wa_data.pk_org, ");
		sqlBuffer.append("       wa_data.taxtableid, ");
		sqlBuffer.append("       wa_data.stopflag, ");
		sqlBuffer.append("       wa_data.caculateflag, ");
		sqlBuffer.append("       wa_data.checkflag, ");
		sqlBuffer.append("       wa_data.partflag, ");
		sqlBuffer.append("       wa_data.pk_bankaccbas1, ");
		sqlBuffer.append("       wa_data.pk_bankaccbas2, ");
		sqlBuffer.append("       wa_data.pk_bankaccbas3, ");
		sqlBuffer.append("       wa_data.pk_banktype1, ");
		sqlBuffer.append("       wa_data.pk_banktype2, ");
		sqlBuffer.append("       wa_data.pk_banktype3, ");
		sqlBuffer.append("       wa_data.isrulehint, ");
		sqlBuffer.append("       wa_data.taxtype, ");
		sqlBuffer.append("       wa_data.isndebuct, ");
		sqlBuffer.append("       wa_data.isderate, ");
		sqlBuffer.append("       wa_data.derateptg, ");
		sqlBuffer.append("       wa_data.workorg, ");
		sqlBuffer.append("       wa_data.workorgvid, ");
		sqlBuffer.append("       wa_data.workdept, ");
		sqlBuffer.append("       wa_data.workdeptvid, ");
		sqlBuffer.append("       wa_data.pk_financeorg, ");
		sqlBuffer.append("       wa_data.pk_financedept, ");
		sqlBuffer.append("       wa_data.pk_liabilityorg, ");
		sqlBuffer.append("       wa_data.pk_liabilitydept, ");
		sqlBuffer.append("       wa_data.fiporgvid, ");
		sqlBuffer.append("       wa_data.fipdeptvid, ");
		sqlBuffer.append("       wa_data.libdeptvid, ");
		// {MOD:个税申报新增字段}
		sqlBuffer.append("       wa_data.biztype, ");// 业务代号
		sqlBuffer.append("       wa_data.feetype, ");// 费用别代号
		sqlBuffer.append("       wa_data.projectcode, ");// 项目代号
		//新个税纳税申报组织
		sqlBuffer.append("       wa_data.taxorg,");
		sqlBuffer.append("       wa_data.taxsumuid,");
		sqlBuffer.append("       wa_data.ts, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("wa_taxbase.name")+ "  taxbasename, ");
		sqlBuffer.append("       bd_psndoc.id psnid, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  psnname, ");
		sqlBuffer.append("       bd_psndoc.code as psncode, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("org_dept.name")+ "  deptname, ");
		sqlBuffer.append("       om_post.postname, ");
		sqlBuffer.append("       om_job.jobname, ");
		sqlBuffer.append("       bd_psncl.name psnclname, ");
		sqlBuffer.append("        "
				+ SQLHelper.getMultiLangNameColumn("org_orgs.name")
				+ "  orgname, ");
		sqlBuffer.append("        wa_data.assgid ");
		sqlBuffer.append("  from wa_data ");
		sql[0] = sqlBuffer.toString();
		sqlBuffer = new StringBuffer();
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		sqlBuffer.append("  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
		sqlBuffer.append("  left outer join wa_taxbase on wa_taxbase.pk_wa_taxbase = wa_data.taxtableid ");
		sql[1] = sqlBuffer.toString();
		return sql;
	}




	/**
	 * 批量修改查询时，同时查询不能修改的记录（在其他子类别中存在）
	 * @param loginContext
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws DAOException
	 */
	public PayfileVO[] queryCannotEditPsn(WaLoginContext loginContext, String condition ) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  ");
		sqlBuffer.append("       wa_data.pk_wa_data, ");
		sqlBuffer.append("       wa_data.pk_wa_class, ");
		sqlBuffer.append("       wa_data.cyear, ");
		sqlBuffer.append("       wa_data.cperiod, ");
		sqlBuffer.append("       wa_data.pk_psnjob, ");
		sqlBuffer.append("       wa_data.pk_psndoc ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.pk_wa_class=? ");
		sqlBuffer.append("           and cyear = ? ");
		sqlBuffer.append("           and cperiod = ? " +
				"and pk_psndoc in (select pk_psndoc from wa_data " +
				"	where pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass=? and cyear = ? and cperiod = ?) and pk_wa_class!=? and cyear= ? and cperiod =?  and stopflag='N')");//guoqt查询不能修改的人员（在其他子方案中且为不停发）

		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));

		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_wa_class());
		param.addParam(loginContext.getWaYear());
		param.addParam(loginContext.getWaPeriod());
		param.addParam(loginContext.getWaLoginVO().getPk_prnt_class());
		param.addParam(loginContext.getWaYear());
		param.addParam(loginContext.getWaPeriod());
		param.addParam(loginContext.getPk_wa_class());
		param.addParam(loginContext.getWaYear());
		param.addParam(loginContext.getWaPeriod());


		return executeQueryVOs(sqlBuffer.toString(), param, PayfileVO.class);
	}
	
	/**
	 * 判别目前范围内是否有未审核的人员，有 return true，没有 return false
	 * 
	 * @author liangxr on 2010-1-14
	 * @param gzlbId
	 * @param waYear
	 * @param waPeriod
	 * @param swhere
	 * @return
	 * @throws SQLException
	 */
	public boolean havePsnNotCheck(String gzlbId, String waYear, String waPeriod) throws DAOException {
		String sql = "select 1 from wa_data where pk_wa_class = ? and cyear=? and cperiod = ? and stopflag = 'N'";
		SQLParameter param = new SQLParameter();
		param.addParam(gzlbId);
		param.addParam(waYear);
		param.addParam(waPeriod);
		// 如果不存在人员（除停发外），不能审核
		if (!isValueExist(sql, param)) {
			return true;
		}

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select 1  from wa_data ");
		sqlB.append(" where pk_wa_class = ?  and cyear=?  and  cperiod=? and stopflag= 'N' ");
		sqlB.append(" and dr=0 and checkflag='N' ");

		return isValueExist(sqlB.toString(), param);

	}

	/**
	 * 判断是否还有未计算的人员
	 * 
	 * @author liangxr on 2010-6-30
	 * @param gzlbId
	 * @param waYear
	 * @param waPeriod
	 * @return
	 * @throws DAOException
	 */
	public boolean isAllCaculated(String gzlbId, String waYear, String waPeriod, String cond)
			throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select 1  from wa_data ");
		sqlB.append(" where pk_wa_class = ?  and cyear=?  and  cperiod=? and stopflag= 'N' ");
		sqlB.append(" and dr=0 and caculateflag='N' ");
		sqlB.append(WherePartUtil.formatAddtionalWhere(cond));

		SQLParameter param = new SQLParameter();
		param.addParam(gzlbId);
		param.addParam(waYear);
		param.addParam(waPeriod);

		return !isValueExist(sqlB.toString(), param);
	}

	/**
	 * 查询当前期间的起始日期
	 * 
	 * @author liangxr on 2010-4-26
	 * @param periodVO
	 * @return
	 * @throws DAOException
	 */
	public PeriodVO queryStartEndDate(PeriodVO periodVO) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB
		.append("select wa_period.pk_wa_period,wa_period.cyear,wa_period.cperiod,wa_period.cstartdate, wa_period.cenddate "); // 1
		sqlB.append("  from wa_periodstate, wa_period ");
		sqlB.append(" where wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
		sqlB.append("   and wa_periodstate.pk_wa_class = ? ");
		sqlB.append("   and wa_period.cyear = ? ");
		sqlB.append("   and wa_period.cperiod = ? ");

		SQLParameter param = new SQLParameter();
		param.addParam(periodVO.getClassid());
		param.addParam(periodVO.getCyear());
		param.addParam(periodVO.getCperiod());

		return executeQueryVO(sqlB.toString(), param, PeriodVO.class);

	}

	/**
	 * 核查转入的薪资类别中是否已经包含 人员psns
	 * 
	 * @author liangxr on 2010-1-18
	 * @param psns
	 * @param gzlbin
	 * @param waYear
	 * @param waPeriod
	 * @return
	 * @throws BusinessException
	 */
	public PayfileVO[] queryExistPsns(PayfileVO[] psns, WaClassVO gzlbin) throws BusinessException {

		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			StringBuffer sqlB = new StringBuffer();
			sqlB.append("select pk_psndoc,pk_psnjob from wa_data where pk_wa_class = ? ");
			sqlB.append(" and cyear = ? and cperiod = ? and pk_psndoc in (");

			sqlB.append(isc.getInSQL(psns, PayfileVO.PK_PSNDOC));

			sqlB.append(")");
			sqlB.append(" and dr=0 ");

			SQLParameter param = new SQLParameter();
			param.addParam(gzlbin.getPk_wa_class());
			param.addParam(gzlbin.getCyear());
			param.addParam(gzlbin.getCperiod());


			return executeQueryVOs(sqlB.toString(), param, PayfileVO.class);
		}finally{
			try {
				isc.clear();
			} catch (Exception e2) {
				Logger.error(e2.getMessage(), e2);
			}
		}
	}

	/**
	 * 查询某薪资类别对应的期间信息
	 * 
	 * @author liangxr on 2010-1-18
	 * @param pk_wa_class
	 * @param waYear
	 * @param waPeriod
	 * @return
	 * @throws DAOException
	 */
	public PeriodVO queryPeriodByWaClass(String pk_wa_class, String waYear, String waPeriod)
			throws DAOException {
		String sql = "select wa_period.pk_wa_period, wa_period.cstartdate, wa_period.cenddate, wa_period.cyear, wa_period.cperiod from wa_period "
				+ "inner join wa_waclass on wa_waclass.pk_periodscheme = wa_period.pk_periodscheme "
				+ " where wa_waclass.pk_wa_class = ? and wa_period.cyear = ? and wa_period.cperiod = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(waYear);
		param.addParam(waPeriod);

		return executeQueryVO(sql, param, PeriodVO.class);
	}

	/**
	 * 转档
	 * 
	 * @author liangxr on 2010-4-22
	 * @param psns
	 * @param itemvos
	 * @param gzlbout
	 * @param classin
	 * @param waYear
	 * @param waPeriod
	 * @return
	 * @throws BusinessException
	 */
	public int transSelData(PayfileVO[] psns, ItemsVO[] itemvos, WaLoginContext context, WaClassVO classin)
			throws BusinessException {

		if (itemvos == null) {
			return 0;
		}
		String classout = context.getPk_wa_class();
		String waYearOut = context.getWaYear();
		String waPeriodOut = context.getWaPeriod();

		String sqlstrSQL = "";
		String sqlstrORA1 = "";
		String sqlstrORA2 = "";
		StringBuffer strSQL = new StringBuffer();
		StringBuffer strORA1 = new StringBuffer();
		StringBuffer strORA2 = new StringBuffer();
		boolean bfind = false;
		for (int i = 0; i < itemvos.length; i++) {
			if (itemvos[i].getNname() != null) {
				bfind = true;
				strSQL.append(" wa_datain.").append(itemvos[i].getNcode());
				strSQL.append(" = wa_dataout.").append(itemvos[i].getCode()).append(" , ");

				strORA1.append(" wa_datain.").append(itemvos[i].getNcode()).append(" , ");
				strORA2.append(" wa_dataout.").append(itemvos[i].getCode()).append(" , ");
				sqlstrSQL += strSQL;
				sqlstrORA1 += strORA1;
				sqlstrORA2 += strORA2;
				strSQL.delete(0, strSQL.length());
				strORA1.delete(0, strORA1.length());
				strORA2.delete(0, strORA2.length());
			}
		}
		if (bfind) {
			sqlstrSQL = sqlstrSQL.substring(0, sqlstrSQL.length() - 2);
			sqlstrORA1 = sqlstrORA1.substring(0, sqlstrORA1.length() - 2);
			sqlstrORA2 = sqlstrORA2.substring(0, sqlstrORA2.length() - 2);
		} else {
			return 0;
		}

		StringBuffer sqlB = new StringBuffer();
		if (getBaseDao().getDBType() == DBUtil.SQLSERVER) {
			sqlB.append("update wa_datain set ");
			sqlB.append(sqlstrSQL);
			sqlB.append(" from wa_data wa_datain,wa_data wa_dataout ");
			sqlB.append("where wa_dataout.pk_wa_class = ? ");
			sqlB.append("and wa_dataout.cyear = ? ");
			sqlB.append("and wa_dataout.cperiod = ? ");
			//sqlB.append("and wa_datain.pk_psnjob = ? ");
			sqlB.append("and wa_datain.cyear = ? ");
			sqlB.append("and wa_datain.cperiod = ? ");
			sqlB.append("and wa_datain.pk_wa_class = ? ");
			sqlB.append("and wa_dataout.pk_psnjob = wa_datain.pk_psnjob ");
			sqlB.append("and wa_datain.dr = 0 and wa_dataout.dr = 0 ");

		} else {
			sqlB.append("update wa_data wa_datain set (");
			sqlB.append(sqlstrORA1);
			sqlB.append(")=(select ");
			sqlB.append(sqlstrORA2);
			sqlB.append(" from wa_data wa_dataout where wa_dataout.pk_wa_class = ? ");
			sqlB.append("and wa_dataout.pk_psnjob = wa_datain.pk_psnjob ");
			sqlB.append("and wa_dataout.cyear = ? ");
			sqlB.append("and wa_dataout.cperiod = ? ");
			sqlB.append("and wa_dataout.dr = 0 )  ");
			sqlB.append("where  wa_datain.cyear = ? ");
			sqlB.append("and wa_datain.cperiod = ? and wa_datain.pk_wa_class = ? ");
			sqlB.append("and wa_datain.dr = 0 ");

		}

		InSQLCreator inSQLCreator = new InSQLCreator();

		try
		{

			sqlB.append(" and wa_datain.pk_psnjob in ("+inSQLCreator.getInSQL(psns, PayfileVO.PK_PSNJOB)+")");

			SQLParameter param = new SQLParameter();
			param.clearParams();
			param.addParam(classout);
			param.addParam(waYearOut);
			param.addParam(waPeriodOut);
			param.addParam(classin.getCyear());
			param.addParam(classin.getCperiod());
			param.addParam(classin.getPk_wa_class());
			getBaseDao().executeUpdate(sqlB.toString(), param);
			return 0;


		}
		finally
		{
			inSQLCreator.clear();
		}


	}

	/**
	 * 判断薪资档案的两个薪资类别中是否有重复人员
	 * 
	 * @author liangxr on 2010-1-25
	 * @param fromWaClass
	 * @param toWaClass
	 * @return
	 * @throws DAOException
	 */
	public boolean hasRepeatPsn(WaLoginVO fromWaClass, WaLoginVO toWaClass) throws DAOException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select 1 ");
		sqlBuffer.append("    from wa_data ");
		sqlBuffer.append("    where wa_data.pk_wa_class = '").append(toWaClass.getPk_wa_class()).append("'");
		sqlBuffer.append("	  and wa_data.cyear = '").append(toWaClass.getCyear()).append("'");
		sqlBuffer.append("    and wa_data.cperiod = '").append(toWaClass.getCperiod()).append("'");
		sqlBuffer.append("    and wa_data.pk_org = '").append(toWaClass.getPk_org()).append("'");
		sqlBuffer.append("    and wa_data.pk_psnjob in ");
		sqlBuffer.append("      (select wa_data.pk_psnjob ");
		sqlBuffer.append("      from wa_data ");
		sqlBuffer.append("      where wa_data.stopflag='N' and wa_data.pk_wa_class = '");
		sqlBuffer.append(fromWaClass.getPk_wa_class()).append("' ");
		sqlBuffer.append("      and wa_data.cyear = '" + fromWaClass.getCyear() + "' ");
		sqlBuffer.append("      and wa_data.cperiod = '" + fromWaClass.getCperiod() + "' ");
		sqlBuffer.append("		and wa_data.pk_org = '" + toWaClass.getPk_org() + "')");

		return isValueExist(sqlBuffer.toString());
	}
	/**
	 * 根据pk_psnjob和薪资类别删除薪资档案
	 * 
	 * @author liangxr on 2010-1-26
	 * @param psns
	 */
	public void delDataByClass(PayfileVO[] psns, String pk_wa_class, String cyear, String cperiod)
			throws DAOException {

		StringBuffer sqlb = new StringBuffer();
		sqlb.append(" delete from wa_data ");
		sqlb.append("where pk_wa_class = ? and cyear = ? and cperiod = ? ");
		sqlb.append("and pk_psndoc in (");

		for (PayfileVO psnvo : psns) {
			sqlb.append("'").append(psnvo.getPk_psndoc()).append("',");
		}
		sqlb.deleteCharAt(sqlb.length() - 1);
		sqlb.append(")");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(cyear);
		param.addParam(cperiod);

		getBaseDao().executeUpdate(sqlb.toString(), param);
	}

	/**
	 * 复制时，删除重复人员
	 * 
	 * @author liangxr on 2010-4-22
	 * @param fromWaClass
	 * @param context
	 * @throws DAOException
	 */
	public void delOverlapPsnForCopy(WaLoginVO fromWaClass, WaLoginVO waLoginVO) throws DAOException {

		StringBuffer sb = new StringBuffer();
		sb.append("delete from wa_data where pk_org = ? ");
		sb.append("and pk_wa_class = ?  and cyear = ? and cperiod = ? ");
		sb.append("and pk_psndoc in ");
		sb.append("(select pk_psndoc from wa_data where pk_wa_class = ? and cyear = ? and cperiod = ? and stopflag='N')");

		SQLParameter param = new SQLParameter();
		param.addParam(waLoginVO.getPk_org());
		param.addParam(waLoginVO.getPk_wa_class());
		param.addParam(waLoginVO.getPeriodVO().getCyear());
		param.addParam(waLoginVO.getPeriodVO().getCperiod());
		param.addParam(fromWaClass.getPk_wa_class());
		param.addParam(fromWaClass.getCyear());
		param.addParam(fromWaClass.getCperiod());

		getBaseDao().executeUpdate(sb.toString(), param);

	}

	/**
	 * 查询薪资档案中不符合计薪规则的人员
	 * 
	 * @author liangxr on 2010-4-22
	 * @param context
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws DAOException
	 */
	public PsnTrnVO[] queryNotInRule(WaLoginContext context, String condition, String orderCondtion)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select hi_psnjob.pk_psnjob,  ");
		sqlBuffer.append("       bd_psndoc.code as psncode, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  as psnname, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")+ "  as psnclassname, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("org_dept.name")+ "  as deptname, ");
		sqlBuffer.append("       om_post.postname, ");
		sqlBuffer.append("       org_dept.pk_dept, ");
		sqlBuffer.append("       bd_psndoc.id as psnid, ");
		sqlBuffer.append("       om_post.pk_post , ");
		sqlBuffer.append("       hi_psnjob.begindate as trndate, ");
		sqlBuffer.append("       hi_psnjob.ismainjob, ");
		sqlBuffer.append("       hi_psnjob.assgid, ");
		sqlBuffer.append("       bd_psncl.pk_psncl, ");
		sqlBuffer.append("       bd_psndoc.pk_psndoc, ");
		sqlBuffer.append("       wa_data.pk_wa_data, ");
		sqlBuffer.append("       hi_psnjob.pk_psnorg, ");
		sqlBuffer.append("       org_dept.pk_dept as workdept, ");
		sqlBuffer.append("       org_dept.pk_vid as workdeptvid, ");
		sqlBuffer.append("       org_orgs.pk_org as workorg, ");
		sqlBuffer.append("       org_orgs.pk_vid as workorgvid ");
		sqlBuffer.append("  from hi_psnjob ");
		sqlBuffer.append(" inner join wa_data on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlBuffer.append("  left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
		sqlBuffer.append(" where wa_data.pk_wa_class=? ");
		sqlBuffer.append("           and cyear = ? ");
		sqlBuffer.append("           and cperiod = ? ");

		if (!StringUtil.isEmptyWithTrim(condition)) {
			sqlBuffer.append(condition);
		}

		if (!StringUtil.isEmptyWithTrim(orderCondtion)) {
			sqlBuffer.append(orderCondtion);
		}

		SQLParameter param = new SQLParameter();
		param.addParam(context.getPk_wa_class());
		param.addParam(context.getWaLoginVO().getCyear());
		param.addParam(context.getWaLoginVO().getCperiod());

		return executeQueryVOs(sqlBuffer.toString(), param, PsnTrnVO.class);
	}

	/**
	 * 更新薪资档案中 不符合规则人员是否提示状态
	 * 
	 * @author liangxr on 2010-4-22
	 * @param context
	 * @param whereSql
	 * @param isHint
	 * @throws DAOException
	 */
	public void updatePsnHintFlag(WaLoginContext context, String whereSql, UFBoolean isHint)
			throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("update wa_data set isrulehint = ? ");
		sqlB.append(" where pk_org = ? ");
		sqlB.append(" and pk_wa_class = ? and cyear = ? and cperiod = ? and ");
		sqlB.append(whereSql);

		SQLParameter param = new SQLParameter();
		param.addParam(isHint);
		param.addParam(context.getPk_org());
		param.addParam(context.getPk_wa_class());
		param.addParam(context.getWaLoginVO().getCyear());
		param.addParam(context.getWaLoginVO().getCperiod());

		getBaseDao().executeUpdate(sqlB.toString(), param);
	}

	/**
	 * 根据结束日期查询期间VO
	 * 
	 * @param enddate
	 * @return
	 * @throws DAOException
	 */
	public PeriodVO getPeriodByendDate(UFDate enddate) throws DAOException {
		if(null!=enddate){
			String sql = "select pk_wa_period,cyear,cperiod,cstartdate,cenddate from wa_period where cenddate = ?";
			SQLParameter param = new SQLParameter();
			param.addParam(enddate.toStdString());
			return executeQueryVO(sql, param, PeriodVO.class);
		}else{
			return null;
		}
	}

	/**
	 * //删除前先删除相关表信息wa_redata,wa_psntax,wa_datas
	 * 
	 * @author liangxr on 2010-7-22
	 * @param vo
	 * @throws DAOException
	 */
	public void deleteRelationTable(PayfileVO vo) throws DAOException {
		String sql1 = "delete from wa_psntax where pk_wa_data = ?";
		String sql2 = "delete from wa_datas where pk_wa_data = ?";
		String sql3 = "delete from wa_redata where pk_psnjob = ? and pk_wa_class = ? and cyear = ? and cperiod = ?";

		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_wa_data());
		getBaseDao().executeUpdate(sql1, param);
		getBaseDao().executeUpdate(sql2, param);

		param = new SQLParameter();
		param.addParam(vo.getPk_psnjob());
		param.addParam(vo.getPk_wa_class());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		getBaseDao().executeUpdate(sql3, param);
	}

	/**
	 * //删除前先删除相关表信息wa_redata,wa_psntax,wa_datas
	 * 
	 * @author liangxr on 2010-7-22
	 * @param vos
	 * @throws DAOException
	 */
	public void deleteRelationTableMClass(PayfileVO[] vos) throws DAOException {
		if(null!=vos&&vos.length>0){
			Map<String,List> map = new HashMap<String,List> ();
			for (int i = 0; i < vos.length; i++) {
				List list = map.get(vos[i].getPk_wa_class()+vos[i].getCyear()+vos[i].getCperiod());
				if(null==list){
					list = new ArrayList();
				}
				list.add(vos[i]);
				map.put(vos[i].getPk_wa_class()+vos[i].getCyear()+vos[i].getCperiod(),list);
			}
			String[] keys =  map.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				deleteRelationTable((PayfileVO[]) map.get(keys[i]).toArray(new PayfileVO[0]));
			}
		}
	}

	/**
	 * //删除前先删除相关表信息wa_redata,wa_psntax,wa_datas
	 * 
	 * @author liangxr on 2010-7-22
	 * @param vos
	 * @throws DAOException
	 */
	private void deleteRelationTable(PayfileVO[] vos) throws DAOException {

		String pkdocs = FormatVO.formatArrayToString(vos, PayfileVO.PK_PSNDOC);

		String sql1 = "delete from wa_psntax where pk_wa_data in(select pk_wa_data from wa_data where pk_psndoc in("
				+ pkdocs + ") and pk_wa_class = ? and cyear = ? and cperiod = ?)";
		String sql2 = "delete from wa_datas where pk_wa_data in(select pk_wa_data from wa_data where pk_psndoc in("
				+ pkdocs + ") and pk_wa_class = ? and cyear = ? and cperiod = ?)";
		String sql3 = "delete from wa_redata where pk_psndoc in("
				+ pkdocs + ") and pk_wa_class = ? and cyear = ? and cperiod = ?";
		String sql4 = "delete from wa_data where pk_psndoc in("
				+ pkdocs + ") and pk_wa_class = ? and cyear = ? and cperiod = ?";
		SQLParameter param = new SQLParameter();

		param = new SQLParameter();
		param.addParam(vos[0].getPk_wa_class());
		param.addParam(vos[0].getCyear());
		param.addParam(vos[0].getCperiod());
		getBaseDao().executeUpdate(sql1,param);
		getBaseDao().executeUpdate(sql2,param);
		getBaseDao().executeUpdate(sql3, param);
		getBaseDao().executeUpdate(sql4, param);
	}



	/**
	 * 批量增加时，更新银行帐号信息
	 * 
	 * @author liangxr on 2010-7-29
	 * @param psnjobs
	 * @throws Exception
	 */
	public void updateAccount(PayfileVO[] addPsn) throws BusinessException {
		if(ArrayUtils.isEmpty(addPsn)){
			return;
		}
		String pk_wa_class  = addPsn[0].getPk_wa_class();
		String cyear = addPsn[0].getCyear();
		String cperiod = addPsn[0].getCperiod();
		StringBuilder sbd = new StringBuilder();
		//创建所有人的银行账号，临时表，
		String tableName = WaCommonImpl.createBankNumTempTable(addPsn);

		if(getDBType()==DBConsts.ORACLE || getDBType()==DBConsts.DB2 ){
			sbd.append(" update wa_data    set (pk_bankaccbas1,pk_banktype1,pk_bankaccbas2,pk_banktype2,pk_bankaccbas3,pk_banktype3)=");
			sbd.append( " (select bankaccbasaccnum1,pk_banktype1,bankaccbasaccnum2,pk_banktype2,bankaccbasaccnum3,pk_banktype3   ");
			sbd.append( " from "+tableName+" a    ");
			sbd.append( " where a.pk_psndoc = wa_data.pk_psndoc) ");

			sbd.append( " where wa_data.checkflag = 'N'     and wa_data.pk_wa_class = '"+ pk_wa_class+"'     and wa_data.cyear = '"+cyear+"'     and wa_data.cperiod = '"+cperiod+"'  ");
			sbd.append( " and exists (select 1   from "+tableName+" a    where a.pk_psndoc = wa_data.pk_psndoc)  ");
		}else{
			sbd.append(" update wa_data    set pk_bankaccbas1 = pk_bankaccbasT1,pk_banktype1=  pk_banktypeT1," +
					"pk_bankaccbas2 = pk_bankaccbasT2 ,pk_banktype2= pk_banktypeT2,pk_bankaccbas3 =pk_bankaccbasT3 ,pk_banktype3 = pk_banktypeT3 from ");
			sbd.append( " ( select bankaccbasaccnum1 pk_bankaccbasT1, ");
			sbd.append( "  pk_banktype1 pk_banktypeT1, ");
			sbd.append( " bankaccbasaccnum2 pk_bankaccbasT2 , ");
			sbd.append( " pk_banktype2 pk_banktypeT2,");
			sbd.append( " bankaccbasaccnum3 pk_bankaccbasT3, ");
			sbd.append( " pk_banktype3 pk_banktypeT3 , pk_psndoc  ");
			sbd.append( "  from "+tableName+"  ");
			sbd.append( " ) temp  where temp.pk_psndoc = wa_data.pk_psndoc ");

			sbd.append( " and  wa_data.checkflag = 'N'     and wa_data.pk_wa_class = '"+ pk_wa_class+"'     and wa_data.cyear = '"+cyear+"'     and wa_data.cperiod = '"+cperiod+"'  ");

		}
		getBaseDao().executeUpdate(sbd.toString());

	}


	/**
	 * 批量修改，其他子方案不存在的人员，修改时同时修改父方案
	 * @param psnjobs
	 * @param pk_prnt_class
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	public void updateTaxset2Total(String psnjobs,String pk_prnt_class,String pk_wa_class,String cyear,String cperiod,UFBoolean stopflag,String[] pk_psndocs) throws BusinessException{
		//guoqt
		updateTaxsetByClassStopflag("", pk_prnt_class, pk_wa_class, cyear, cperiod, stopflag, pk_psndocs);
	}

	/**
	 * 批量增加时，对于父方案中已经存在人员记录设置不变，
	 * @param psnjobs
	 * @param pk_prnt_class
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	public void updateTaxsetByClass(String addSql,String pk_prnt_class,String pk_wa_class,String cyear,String cperiod) throws BusinessException{
		//20151123 shenliangc 喻程:直接在数据库里执行此sql耗时很少，但SPR中耗时则达到5511ms，可能是因为新增档案后，表结构变化导致需要重置执行计划。
		//代码中sql建议调整，因为指定的列不明确(isndebuct等列)，会引发不可预估的执行计划负担。
		String sql = "update a set a.isndebuct=b.isndebuct,a.taxtableid = b.taxtableid,a.isderate = b.isderate," +
				"a.derateptg = b.derateptg,a.taxtype = b.taxtype ,taxorg=b.taxorg,taxsumuid=b.taxsumuid from wa_data b,wa_data a where" +
				" a.pk_wa_class = '"
				+ pk_prnt_class
				+ "' and a.cyear = '"
				+ cyear
				+ "' and a.cperiod = '"
				+ cperiod
				+ "' and b.pk_wa_class ='"+ pk_wa_class +"' and b.cyear = '"+ cyear +"' and b.cperiod = '"+ cperiod +"'  " +
				addSql +
				" and a.pk_psndoc = b.pk_psndoc ";
		

		getBaseDao().executeUpdate(sql);
	}
	
	/**
	 * 批量增加时，对于父方案中已经存在人员记录设置不变，
	 * @param psnjobs
	 * @param pk_prnt_class
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	public void updateTaxsetByClassStopflag(String addSql,String pk_prnt_class,String pk_wa_class,String cyear,String cperiod,UFBoolean stopflag,String[] pk_psndocs) throws BusinessException{
		InSQLCreator inSQLCreator = new InSQLCreator();
		String sql = "update a set stopflag='"+ stopflag +"' ,isndebuct=b.isndebuct,taxtableid = b.taxtableid,isderate = b.isderate," +
				"derateptg = b.derateptg,taxtype = b.taxtype,taxorg=b.taxorg,taxsumuid=b.taxsumuid from wa_data b,wa_data a where" +
				" a.pk_wa_class = '"
				+ pk_prnt_class
				+ "' and a.cyear = '"
				+ cyear
				+ "' and a.cperiod = '"
				+ cperiod
				+ "' and b.pk_wa_class ='"+ pk_wa_class +"' and b.cyear = '"+ cyear +"' and b.cperiod = '"+ cperiod +"'  " +
				addSql +
				" and a.pk_psndoc = b.pk_psndoc "+
				" and a.pk_psndoc in(" + inSQLCreator.getInSQL(pk_psndocs) + ") ";

		getBaseDao().executeUpdate(sql);
	}

	/**
	 * 批量删除时，其他子方案不存在的人员，删除时连汇总方案中一起删除
	 * @param psnjobs
	 * @param pk_prnt_class
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	public void deleteFromTotal(String psndocs, String pk_prnt_class,
			String pk_wa_class, String cyear, String cperiod)
					throws BusinessException {
		String sql = "delete from wa_data where pk_wa_class  = ? and cyear = ? and cperiod = ? " +
				"and pk_psndoc not in (select pk_psndoc from wa_data where cyear = ? and cperiod = ? "
				+
				"         and pk_wa_class!= ? and pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = ?))" +
				" and pk_psndoc in (" + psndocs + ")";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_prnt_class);
		param.addParam(cyear);
		param.addParam(cperiod);

		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_wa_class);
		param.addParam(pk_prnt_class);

		getBaseDao().executeUpdate(sql, param);
	}

	/**
	 * 批量删除时，其他子方案不存在的人员，删除时连汇总方案中一起删除
	 * 
	 * @param WaClassVO
	 * @throws BusinessException
	 */
	public void deleteFromTotal(WaClassVO vo) throws BusinessException {
		String sql = "delete from wa_data where pk_wa_class  = ? and cyear = ? and cperiod = ? "
				+ "and pk_psndoc not in (select pk_psndoc from wa_data where cyear = ? and cperiod = ? "
				+ "         and pk_wa_class!= ? and pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = ?))";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_prnt_class());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());

		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		param.addParam(vo.getPk_wa_class());
		param.addParam(vo.getPk_prnt_class());

		getBaseDao().executeUpdate(sql, param);
	}

	/**
	 * 批量增加时，查询未增加到父方案的人员
	 * @param pk_prnt_class
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws DAOException
	 */
	public PayfileVO[] getPsnNotInPrntClass(String pk_prnt_class,String pk_wa_class,String cyear,String cperiod)throws DAOException{
		String sql = " select pk_wa_class,cyear,cperiod,pk_psnorg,pk_psnjob,pk_psndoc,pk_group,pk_org," +
				"taxtableid,stopflag,caculateflag,checkflag,partflag,pk_bankaccbas1,pk_bankaccbas2,pk_bankaccbas3,pk_banktype1," +
				"pk_banktype2,pk_banktype3,isrulehint,taxtype,isndebuct,isderate,derateptg," +
				"workorg,workorgvid,workdept,workdeptvid,pk_financeorg,pk_financedept,pk_liabilityorg,pk_liabilitydept," +
				"fiporgvid,fipdeptvid,libdeptvid,taxorg,taxsumuid " +
				"from wa_data a " +
				"where a.pk_wa_class = ? and a.cyear = ? and a.cperiod = ? " +
				"and a.pk_psndoc not in(select pk_psndoc from wa_data where pk_wa_class = ? and cyear = ? and cperiod = ?)";

		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_prnt_class);
		param.addParam(cyear);
		param.addParam(cperiod);

		return executeQueryVOs(sql, param, PayfileVO.class);

	}

	/**
	 * 删除方案下所有人
	 * 
	 * @author liangxr on 2010-8-10
	 * @param pk_wa_class
	 * @throws BusinessException
	 */
	public void delPsnbyWaClass(String pk_wa_class) throws BusinessException {

		String[] updateSqls = new String[4];
		updateSqls[0] = "delete from wa_psntax where pk_wa_data in(select pk_wa_data from wa_data where pk_wa_data = '"
				+ pk_wa_class + "')";
		updateSqls[1] = "delete from wa_datas where pk_wa_data in(select pk_wa_data from wa_data where pk_wa_data = '"
				+ pk_wa_class + "')";
		updateSqls[2] = "delete from wa_redata where  pk_wa_class = '" + pk_wa_class + "' ";
		updateSqls[3] = "delete from wa_data where pk_wa_class = '" + pk_wa_class + "'";

		executeSQLs(updateSqls);

	}

	/**
	 * 判断一个人在父方案中是否存在
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public boolean existInPrntClass(PayfileVO vo) throws BusinessException{
		String sql = "select 1 from wa_data where pk_wa_class = ? and pk_psndoc = ? and cyear = ? and cperiod = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_prnt_class());
		param.addParam(vo.getPk_psndoc());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		return isValueExist(sql, param);
	}

	/**
	 * 判断一个人在其他子方案中是否存在
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public boolean existInSubClass(String pk_prnt_class,String pk_wa_class,String cyear,String cperiod,String psndoc) throws BusinessException{
		String sql = "select 1 from wa_data where pk_psndoc = ? and cyear = ? and cperiod = ? " +
				"and pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = ?) and pk_wa_class!=? ";
		SQLParameter param = new SQLParameter();
		param.addParam(psndoc);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_prnt_class);
		param.addParam(pk_wa_class);
		return isValueExist(sql, param);
	}
	
	/**
	 * 判断一个人在当前方案中是否存在
	 * @param vo[]
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, PayfileVO> existInSubClass(String pk_prnt_class,String pk_wa_class,String cyear,String cperiod,PayfileVO[] vos)
			throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String[] fields = new String[]{PayfileVO.PK_PSNDOC};
		String tablename = isc.insertValues(
				HRWACommonConstants.WA_TEMP_DATAPSN,
				fields, fields, vos);
		StringBuffer sql = new StringBuffer();
		sql.append("select wa_data.pk_wa_class,wa_data.pk_psndoc,wa_data.cyear,wa_data.cperiod from wa_data inner join ");
		sql.append(tablename);
		sql.append(" on wa_data.pk_psndoc = ");
		sql.append(tablename);
		sql.append(".pk_psndoc ");
		sql.append(String.format(" where wa_data.pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass ='%s') and wa_data.pk_wa_class!='%s'  ", pk_prnt_class,pk_wa_class));
		sql.append(String.format(" and wa_data.cyear = '%s' ", cyear));
		sql.append(String.format(" and wa_data.cperiod = '%s' ", cperiod));
		PayfileVO[] existvos = executeQueryVOs(sql.toString(), PayfileVO.class);
		HashMap<String, PayfileVO> map = new HashMap<String, PayfileVO>();
		if(existvos!=null&&existvos.length>0){
			for (PayfileVO vo : existvos) {
				map.put(vo.getPk_psndoc(), vo);
			}
		}
		return map;
	}

	
	/**
	 * guoqt
	 * 判断一个人在其他子方案中是否存在（有且不停发）
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public boolean existInSubClassStopFlag(String pk_prnt_class,String pk_wa_class,String cyear,String cperiod,String psndoc) throws BusinessException{
		String sql = "select 1 from wa_data where pk_psndoc = ? and cyear = ? and cperiod = ? " +
				"and pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = ?) and pk_wa_class!=? and stopflag='N'";
		SQLParameter param = new SQLParameter();
		param.addParam(psndoc);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_prnt_class);
		param.addParam(pk_wa_class);
		return isValueExist(sql, param);
	}

	/**
	 * 判断一个方案是否为离职方案
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	public boolean isLeaveClass(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		String sql = "select 1 from wa_inludeclass where pk_childclass = ? and cyear = ? and cperiod = ? and batch>99 ";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(cyear);
		param.addParam(cperiod);
		return isValueExist(sql, param);
	}

	/**
	 * 更新方案离职结薪标示
	 * @param context
	 * @param isPayOff
	 * @throws BusinessException
	 */
	public void updateLeaveFlag(String pk_wa_class, String cyear, String cperiod, boolean isleave) throws BusinessException{
		String sql = " update wa_waclass set leaveflag = '"+(isleave?"Y":"N")+"' where"+
				" pk_wa_class = '"+pk_wa_class+"'";
		getBaseDao().executeUpdate(sql);
		sql = " update wa_periodstate set classtype = "+WACLASSTYPE.PARENTCLASS.getValue()+" where"+
				" pk_wa_class = '"+pk_wa_class+"' and exists " + "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear =  '"+cyear+"' and wa_period.cperiod =  '"+cperiod+"' and  "
				+ "wa_periodstate.pk_wa_class =  '"+pk_wa_class+"')";
		getBaseDao().executeUpdate(sql);
	}
	/**
	 * 判断一个方案是否档案记录
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	public boolean existPsnInClass(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		String sql = "select 1 from wa_data where pk_wa_class = ? and cyear = ? and cperiod = ? ";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(cyear);
		param.addParam(cperiod);
		return isValueExist(sql, param);
	}

	/**
	 * 从父方案中删除某人
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public void delFromPrntClass(PayfileVO vo) throws BusinessException{
		String sql = "delete from wa_data where pk_psndoc = ? and cyear = ? and cperiod = ? and pk_wa_class = ?";

		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPk_psndoc());
		param.addParam(vo.getCyear());
		param.addParam(vo.getCperiod());
		param.addParam(vo.getPk_prnt_class());
		getBaseDao().executeUpdate(sql, param);
	}


	/**
	 * 取当前人在父方案中的记录
	 * @param loginContext
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public PayfileVO getPsnInPrntClass(String classpk,String cyear,String cperiod,String pk_psnjob)throws BusinessException{
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  ");
		sqlBuffer.append("       wa_data.pk_wa_data, ");
		sqlBuffer.append("       wa_data.pk_wa_class, ");
		sqlBuffer.append("       wa_data.cyear, ");
		sqlBuffer.append("       wa_data.cperiod, ");
		sqlBuffer.append("       wa_data.pk_psnorg, ");
		sqlBuffer.append("       wa_data.pk_psnjob, ");
		sqlBuffer.append("       wa_data.pk_psndoc, ");
		sqlBuffer.append("       wa_data.pk_group, ");
		sqlBuffer.append("       wa_data.pk_org, ");
		sqlBuffer.append("       wa_data.taxtableid, ");
		sqlBuffer.append("       wa_data.stopflag, ");
		sqlBuffer.append("       wa_data.partflag, ");
		sqlBuffer.append("       wa_data.pk_bankaccbas1, ");
		sqlBuffer.append("       wa_data.pk_bankaccbas2, ");
		sqlBuffer.append("       wa_data.pk_bankaccbas3, ");
		sqlBuffer.append("       wa_data.pk_banktype1, ");
		sqlBuffer.append("       wa_data.pk_banktype2, ");
		sqlBuffer.append("       wa_data.pk_banktype3, ");
		sqlBuffer.append("       wa_data.taxtype, ");
		sqlBuffer.append("       wa_data.isndebuct, ");
		sqlBuffer.append("       wa_data.isderate, ");
		sqlBuffer.append("       wa_data.derateptg, ");
		sqlBuffer.append("       wa_data.ts ");
		//		sqlBuffer.append("       wa_taxbase.name taxbasename, ");
		//		sqlBuffer.append("       bd_psndoc.id psnid, ");
		//		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		//		sqlBuffer.append("       bd_psndoc.name psnname, ");
		//		sqlBuffer.append("       bd_psndoc.code as psncode, ");
		//		sqlBuffer.append("       org_dept.name deptname, ");
		//		sqlBuffer.append("       om_post.postname, ");
		//		sqlBuffer.append("       om_job.jobname, ");
		//		sqlBuffer.append("       org_orgs.name orgname ");
		sqlBuffer.append("  from wa_data ");
		//		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		//		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		//		sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		//		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		//		sqlBuffer.append("  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
		//		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
		//		sqlBuffer.append("  left outer join wa_taxbase on wa_taxbase.pk_wa_taxbase = wa_data.taxtableid ");
		sqlBuffer.append(" where wa_data.pk_wa_class=? ");
		sqlBuffer.append("           and cyear = ? ");
		sqlBuffer.append("           and cperiod = ? and pk_psnjob = ?");

		SQLParameter param = new SQLParameter();
		param.addParam(classpk);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_psnjob);

		return executeQueryVO(sqlBuffer.toString(), param, PayfileVO.class);
	}
	
	/**
	 * guoqt
	 * 取当前人在父方案中的全部记录
	 * @param loginContext
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public PayfileVO getPsnInPrntClassAll(String classpk,String cyear,String cperiod,String pk_psnjob)throws BusinessException{
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  ");
		sqlBuffer.append("       wa_data.* ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.pk_wa_class=? ");
		sqlBuffer.append("           and cyear = ? ");
		sqlBuffer.append("           and cperiod = ? and pk_psnjob = ?");

		SQLParameter param = new SQLParameter();
		param.addParam(classpk);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_psnjob);

		return executeQueryVO(sqlBuffer.toString(), param, PayfileVO.class);
	}
	//	/**
	//	 * 根据工作记录PK获取对应的财务组织PK
	//	 * @param pk_psnjob
	//	 * @return
	//	 * @throws BusinessException
	//	 */
	//	public PayfileVO[] getPkFinanceOrg(PayfileVO[] payfileVOs) throws BusinessException{
	//		InSQLCreator inC = null;
	//		if(ArrayUtils.isEmpty(payfileVOs)){
	//			return null;
	//		}
	//		try {
	//		inC = new InSQLCreator();
	//
	//		boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_RESA);
	//		String sql = "select job.pk_psnjob as pk_psnjob,org.pk_org as pk_org, org.pk_vid as pk_vid," +
	//				" org.orgtype5 as is_financeorg,job.pk_dept as pk_dept,dept.pk_vid as deptvid," +
	//				" org.pk_corp as pk_corp,corp.pk_vid as corpvid  from org_orgs org " +
	//				" inner join hi_psnjob job on org.pk_org=job.pk_org and job.pk_psnjob in (" +inC.getInSQL(payfileVOs, PayfileVO.PK_PSNJOB)+")" +
	//				" inner join org_corp corp on corp.pk_corp = org.pk_corp " +
	//				" inner join org_dept dept on dept.pk_dept = job.pk_dept " ;
	//
	//		WaBmFileOrgVO[] payOrgVOs = this.executeQueryVOs(sql,  WaBmFileOrgVO.class);
	//
	//
	//		for(WaBmFileOrgVO payOrgVO:payOrgVOs){
	//			payOrgVO.setPk_financeorg(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE)?payOrgVO.getPk_org():payOrgVO.getPk_corp());
	//			payOrgVO.setPk_financedept(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE)?payOrgVO.getPk_dept():"");
	//
	//			payOrgVO.setFiporgvid(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE)?payOrgVO.getPk_vid():payOrgVO.getCorpvid());
	//			payOrgVO.setFipdeptvid(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE)?payOrgVO.getDeptvid():"");
	//
	//
	//
	//			for(PayfileVO payfileVO:payfileVOs){
	//				if(payOrgVO.getPk_psnjob().equals(payfileVO.getPk_psnjob())){
	//					payfileVO.setPk_financeorg(payOrgVO.getPk_financeorg());
	//					payfileVO.setPk_financedept(payOrgVO.getPk_financedept());
	//					payfileVO.setFiporgvid(payOrgVO.getFiporgvid());
	//					payfileVO.setFipdeptvid(payOrgVO.getFipdeptvid());
	//					break;
	//				}
	//			}
	//
	//		}
	//		if(isEnable){
	//			ICostCenterPubService orgQuery = NCLocator.getInstance().lookup(ICostCenterPubService.class);
	//			for(WaBmFileOrgVO payOrgVO:payOrgVOs){
	//				//加上默认成本中心和成本部门
	//				String pk_costcenter = orgQuery.queryCostCenterByDept(payOrgVO.getPk_dept(), false);
	//				for(PayfileVO payfileVO:payfileVOs){
	//					if(payOrgVO.getPk_psnjob().equals(payfileVO.getPk_psnjob())){
	//						if(pk_costcenter!=null){
	//							payfileVO.setPk_liabilityorg(pk_costcenter);
	//							payfileVO.setPk_liabilitydept(payOrgVO.getPk_dept());
	//							payfileVO.setLibdeptvid(payOrgVO.getDeptvid());
	//						}
	//						break;
	//					}
	//				}
	//			}
	//		}
	//		return payfileVOs;
	//		} finally {
	//			inC.clear();
	//		}
	//		}


	/**
	 * 为薪资档案同步财务组织与成本中心
	 * @throws BusinessException
	 */
	public void synFiAndCostOrg(PayfileVO[] addPsn) throws BusinessException{
		//如果数据库是oracle
		StringBuilder sbd = new StringBuilder();
		StringBuilder sbd2 = new StringBuilder();

		InSQLCreator inC = null;
		if(ArrayUtils.isEmpty(addPsn)){
			return ;
		}

		String pk_wa_class = addPsn[0].getPk_wa_class();
		String cyear = addPsn[0].getCyear();
		String cperiod = addPsn[0].getCperiod();
		try {
			inC = new InSQLCreator();

			if(getDBType()==DBConsts.ORACLE || getDBType()==DBConsts.DB2){


				sbd.append("	update wa_data    set (pk_financeorg,pk_financedept,fiporgvid,fipdeptvid)=( ");
				sbd.append("				select    ( case   when  org.orgtype5 = 'Y' then  org.pk_org   else org.pk_corp end )  pk_financeorg,  ");
				sbd.append("			  ( case   when  org.orgtype5 = 'Y' then  job.pk_dept  else '' end )  pk_financedept,  ");
				sbd.append("			 ( case   when  org.orgtype5 = 'Y' then  org.pk_vid  else corp.pk_vid  end )  fiporgvid,  ");
				sbd.append("			 ( case   when  org.orgtype5 = 'Y' then  dept.pk_vid  else ''  end )  fipdeptvid  ");
				sbd.append("			  from org_orgs org   ");
				sbd.append("							 inner join hi_psnjob job on org.pk_org=job.pk_org   ");
				sbd.append("							 inner join org_corp corp on corp.pk_corp = org.pk_corp   ");
				sbd.append("			 inner join org_dept dept on dept.pk_dept = job.pk_dept   ");

				sbd.append("			 where job.pk_psnjob = wa_data.pk_psnjob ");
				sbd.append("			)  where wa_data.pk_psnjob in (" +inC.getInSQL(addPsn, PayfileVO.PK_PSNJOB)+")  and  wa_data.pk_wa_class = '"+pk_wa_class+"' and wa_data.cyear = '"+cyear+"' and wa_data.cperiod  = '"+cperiod+"' ");


				sbd2.append("   update wa_data    set (pk_liabilityorg,pk_liabilitydept,libdeptvid)=( ");
				sbd2.append("                select   resa_costcenter.pk_costcenter AS pk_liabilityorg ," +
						"   job.pk_dept  AS pk_liabilitydept ," +
						"   dept.pk_vid  AS libdeptvid ");
				sbd2.append("              from resa_costcenter  " +
						"              inner join resa_ccdepts on  resa_ccdepts.pk_costcenter = resa_costcenter.pk_costcenter " +
						"              inner join hi_psnjob job on  job.pk_dept = resa_ccdepts.pk_dept   ");
				sbd2.append("              inner join org_dept dept on dept.pk_dept = job.pk_dept   ");
				sbd2.append("             where job.pk_psnjob = wa_data.pk_psnjob and  resa_costcenter.enablestate = '2'  ");
				sbd2.append("            )  where wa_data.pk_psnjob in (" +inC.getInSQL(addPsn, PayfileVO.PK_PSNJOB)+")  and  wa_data.pk_wa_class = '"+pk_wa_class+"' and wa_data.cyear = '"+cyear+"' and wa_data.cperiod  = '"+cperiod+"' ");

			}else{


				sbd.append("		update wa_data set    wa_data.pk_financeorg = ( case   when  org.orgtype5 = 'Y' then  org.pk_org   else org.pk_corp end )  , ");
				sbd.append("			wa_data.pk_financedept=  ( case   when  org.orgtype5 = 'Y' then  job.pk_dept  else '' end )  , ");
				sbd.append("		wa_data.fiporgvid=  ( case   when  org.orgtype5 = 'Y' then  org.pk_vid  else corp.pk_vid  end )  , ");
				sbd.append("		 wa_data.fipdeptvid= ( case   when  org.orgtype5 = 'Y' then  dept.pk_vid  else ''  end )   ");
				sbd.append("			  from org_orgs org  ");
				sbd.append("							 inner join hi_psnjob job on org.pk_org=job.pk_org  ");
				sbd.append("							 inner join org_corp corp on corp.pk_corp = org.pk_corp   ");
				sbd.append("							 inner join org_dept dept on dept.pk_dept = job.pk_dept ");
				sbd.append("			    inner join wa_data  on  job.pk_psnjob = wa_data.pk_psnjob ");
				sbd.append("  where wa_data.pk_psnjob in (" +inC.getInSQL(addPsn, PayfileVO.PK_PSNJOB)+")  and  wa_data.pk_wa_class = '"+pk_wa_class+"' and wa_data.cyear = '"+cyear+"' and wa_data.cperiod  = '"+cperiod+"' ");

				sbd2.append("      update wa_data set    wa_data.pk_liabilityorg = ( resa_costcenter.pk_costcenter )  , ");
				sbd2.append("            wa_data.pk_liabilitydept=  ( job.pk_dept )  , ");
				sbd2.append("        wa_data.libdeptvid=  ( dept.pk_vid )   ");
				sbd2.append("        from resa_costcenter  " +
						"           inner join resa_ccdepts on  resa_ccdepts.pk_costcenter = resa_costcenter.pk_costcenter " +
						"           inner join hi_psnjob job on  job.pk_dept = resa_ccdepts.pk_dept   ");
				sbd2.append("        inner join org_dept dept on dept.pk_dept = job.pk_dept   ");
				sbd2.append("        inner join wa_data  on  job.pk_psnjob = wa_data.pk_psnjob  ");
				sbd2.append("  where  resa_costcenter.enablestate = '2'  and wa_data.pk_psnjob in (" +inC.getInSQL(addPsn, PayfileVO.PK_PSNJOB)+")  and  wa_data.pk_wa_class = '"+pk_wa_class+"' and wa_data.cyear = '"+cyear+"' and wa_data.cperiod  = '"+cperiod+"' ");

			}

			getBaseDao().executeUpdate(sbd.toString());

			boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_RESA);
			if(isEnable){
				getBaseDao().executeUpdate(sbd2.toString());
			}



		}finally{
			inC.clear();
		}

	}


	private int  getDBType(){
		return getBaseDao().getDBType();
	}




	/**
	 * 判断一个人在当前方案中是否存在
	 * @param vo[]
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<String, PayfileVO> existInCurrentClass(PayfileVO[] vos)
			throws BusinessException {
		HashMap<String, PayfileVO> map = new HashMap<String, PayfileVO>();
		InSQLCreator isc = new InSQLCreator();
		String[] fields = HRWACommonConstants.DATAPSN_COLUMN;
		String tablename = isc.insertValues(
				HRWACommonConstants.WA_TEMP_DATAPSN,
				fields, fields, vos);
		StringBuffer sql = new StringBuffer();
		sql.append("select wa_data.pk_wa_class,wa_data.pk_psndoc,wa_data.cyear,wa_data.cperiod from wa_data ,");
		sql.append(tablename);
		sql.append(" where wa_data.pk_wa_class = ");
		sql.append(tablename);
		sql.append(".pk_wa_class ");
		sql.append(" and wa_data.pk_psndoc = ");
		sql.append(tablename);
		sql.append(".pk_psndoc ");
		sql.append(" and wa_data.cyear = ");
		sql.append(tablename);
		sql.append(".cyear ");
		sql.append(" and wa_data.cperiod = ");
		sql.append(tablename);
		sql.append(".cperiod ");
		PayfileVO[] tstr = executeQueryVOs(sql.toString(), PayfileVO.class);
		if(tstr!=null&&tstr.length>0){
			for (PayfileVO str : tstr) {
				map.put(str.getPk_wa_class() + str.getPk_psndoc()
						+ str.getCyear() + str.getCperiod(), str);
			}
		}
		return map;
	}

	/**
	 * 更新父方案的发放状态
	 *
	 * @param parentVO WaClassVO
	 * @param payoffflag String
	 * @throws DAOException
	 */
	public void updateParentClassPayOff(String pk_prnt_class, String cyear, String cperiod,String payoffflag) throws DAOException{
		String sql = "update wa_periodstate set payoffflag = '" + payoffflag
				+ "' where  pk_wa_class = '" + pk_prnt_class
				+ "' and exists "
				+ "(select wa_period.pk_wa_period  from wa_period  "
				+ "  where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ "    and wa_period.cyear =  '" + cyear
				+ "'   and wa_period.cperiod =  '" + cperiod
				+ "'   and  " + "wa_periodstate.pk_wa_class =  '" + pk_prnt_class + "')";
		getBaseDao().executeUpdate(sql);
	}

	public boolean queryIsTaxTableMust(String pk_wa_class) throws DAOException {
		String sql = "select 1 from WA_CLASSITEM where PK_WA_CLASS = '" + pk_wa_class + "' and TAXFLAG = 'Y'";
		return isValueExist(sql);
	}
	
	public void setMultiMarks(PayfileVO [] vos){
		String corpPk = nc.jdbc.framework.util.SQLHelper.getCorpPk();
		String[] pks = new SequenceGenerator().generate(corpPk, vos.length);
		for(int i=0;i<vos.length;i++){
			vos[i].setTaxsumuid(pks[i]);
		}
	}
	
	public void setMultiMark(PayfileVO vo){
		if(StringUtils.isNotBlank(vo.getTaxorg())){
			String corpPk = nc.jdbc.framework.util.SQLHelper.getCorpPk();
			String[] pks = new SequenceGenerator().generate(corpPk, 1);
			vo.setTaxsumuid(pks[0]);
		}else{
			vo.setTaxsumuid(null);
		}
	}



	/**
	 * 同步默认的纳税申报组织，取当前任职组织的法人公司
	 * @param addPsn
	 */
	public void synTaxOrg(PayfileVO[] addPsn) throws BusinessException {

		if(ArrayUtils.isEmpty(addPsn)){
			return ;
		}

		String pk_wa_class = addPsn[0].getPk_wa_class();
		String cyear = addPsn[0].getCyear();
		String cperiod = addPsn[0].getCperiod();
		InSQLCreator inC = null;
		try {
			inC = new InSQLCreator();
			String inPsnjob = inC.getInSQL(addPsn, PayfileVO.PK_PSNJOB);
			synTaxOrg(pk_wa_class,cyear,cperiod,inPsnjob);
		}finally{
			inC.clear();
		}
		
	}
	
	/**
	 * 同步默认的纳税申报组织，取当前任职组织的法人公司
	 * @param addPsn
	 */
	public void synTaxOrg(String pk_wa_class,String cyear,String cperiod,String inPsnjob) throws BusinessException{
		StringBuilder sbd = new StringBuilder();
		
		sbd.append("	update wa_data    set taxorg=( ");
		sbd.append("				select  pk_corp  from org_orgs org   ");
		sbd.append("							 inner join hi_psnjob job on org.pk_org=job.pk_org   ");
		sbd.append("			 where job.pk_psnjob = wa_data.pk_psnjob ) ");
		sbd.append("			  where  (taxorg is null or taxorg='~' )");
		sbd.append(" and  wa_data.pk_wa_class = '"+pk_wa_class+"' and wa_data.cyear = '"+cyear+"' and wa_data.cperiod  = '"+cperiod+"' ");
		if(inPsnjob!=null){
			sbd.append(" and  wa_data.pk_psnjob in (" +inPsnjob+")");
		}
		getBaseDao().executeUpdate(sbd.toString());
		
	}
	
	/**
	 * 根据最小期间同步默认的纳税申报组织
	 * @param addPsn
	 */
	public void synTaxOrgByPeriod(String pk_wa_class,String cyear,String cperiod) throws BusinessException{
		StringBuilder sbd = new StringBuilder();
		
		sbd.append("	update wa_data    set taxorg=( ");
		sbd.append("				select  pk_corp  from org_orgs org   ");
		sbd.append("							 inner join hi_psnjob job on org.pk_org=job.pk_org   ");
		sbd.append("			 where job.pk_psnjob = wa_data.pk_psnjob ) ");
		sbd.append("			  where  (taxorg is null or taxorg='~' )");
		sbd.append(" and  wa_data.pk_wa_class in ("+pk_wa_class+") and wa_data.cyear||wa_data.cperiod >= '"+cyear+cperiod+"' ");
		getBaseDao().executeUpdate(sbd.toString());
		
	}
	
	/**
	 * 清掉薪资档案中的taxsumuid
	 * @param addPsn
	 */
	public void clearTaxsumuid() throws BusinessException{
		StringBuilder sbd = new StringBuilder();
		
		sbd.append("update wa_data set taxsumuid = null where taxsumuid is not null and pk_group='"+PubEnv.getPk_group()+"' ");
		getBaseDao().executeUpdate(sbd.toString());
		
	}
		
		
	/**
	 * 重置纳税累计标志
	 * @param addPsn
	 */
	public void setTaxsumuid4Psn(PayfileVO[] addPsn) throws BusinessException {
		HashMap<String, String> map4lo = queryLastTaxuid(addPsn);
		for(int i=0;i<addPsn.length;i++){
			if(map4lo.get(addPsn[i].getPk_psndoc())!=null){
				addPsn[i].setTaxsumuid(map4lo.get(addPsn[i].getPk_psndoc()));
			}else{
				setMultiMark(addPsn[i]);
			}
		}
	}
	
	/**
	 * 为空的纳税累计标志赋值，升级用
	 * @param addPsn
	 */
	public PayfileVO[] setTaxsumuid4Null(PayfileVO[] addPsn) throws BusinessException {
		List<PayfileVO> list = new ArrayList<PayfileVO>();
		HashMap<String, String> map4lo = queryLastTaxuid(addPsn);
		for(int i=0;i<addPsn.length;i++){
			if(StringUtils.isBlank(addPsn[i].getTaxsumuid())){
				if(map4lo.get(addPsn[i].getPk_psndoc())!=null){
					addPsn[i].setTaxsumuid(map4lo.get(addPsn[i].getPk_psndoc()));
				}else{
					setMultiMark(addPsn[i]);
				}
				list.add(addPsn[i]);
			}
		}
		return list.toArray(new PayfileVO[0]);
	}
	
	/**
	 * 查询当前或上一纳税期间，同一个人，同纳税组织下，同组织关系下，月度累计方案，非汇总方案下的不为空纳税累计标志，如果没有，重新生成一个
	 * @param addPsn
	 */
	public HashMap<String, String> queryLastTaxuid(PayfileVO[] addPsn) throws BusinessException {
		HashMap<String, String> map4lo = new HashMap<String, String>();
		//如果数据库是oracle
		StringBuilder sbd = new StringBuilder();

		InSQLCreator inC = null;
		if(ArrayUtils.isEmpty(addPsn)){
			return map4lo ;
		}
		
		//上一期间与当前期间sql
		String yearperiodSql = " (lastdata.cyear||lastdata.cperiod = '"+addPsn[0].getCyear()+addPsn[0].getCperiod()+"' and lastdata.pk_wa_class<>'"+addPsn[0].getPk_wa_class()+"') ";
		String lastYearperiod = getPrePeriodVO(addPsn[0].getPk_wa_class(),addPsn[0].getCyear(),addPsn[0].getCperiod());
		
		if(StringUtils.isNotEmpty(lastYearperiod)){
			yearperiodSql+=" or lastdata.cyear||lastdata.cperiod = '"+lastYearperiod+"'";
		}
		
		String pk_wa_class = addPsn[0].getPk_wa_class();
		String cyear = addPsn[0].getCyear();
		String cperiod = addPsn[0].getCperiod();
		try {
			inC = new InSQLCreator();
			sbd.append(" select wa_data.pk_psndoc,lastdata.taxsumuid from wa_data ");
			sbd.append(" inner join wa_data lastdata on wa_data.pk_psndoc=lastdata.pk_psndoc ");
			sbd.append("    and wa_data.pk_psnorg=lastdata.pk_psnorg  ");
			sbd.append("    and wa_data.taxorg=lastdata.taxorg  ");
			
			sbd.append("    inner join wa_waclass on lastdata.pk_wa_class=wa_waclass.pk_wa_class   ");
			
			sbd.append(" where coalesce(wa_waclass.yearbonusflag,'N') = 'N' and wa_waclass.collectflag='N'  ");
			sbd.append(" and  wa_data.pk_psnjob in (" +inC.getInSQL(addPsn, PayfileVO.PK_PSNJOB)+")");
			sbd.append(" and  wa_data.pk_wa_class = '"+pk_wa_class+"' and wa_data.cyear = '"+cyear+"' and wa_data.cperiod  = '"+cperiod+"' ");
			sbd.append(" and ( "+yearperiodSql+")");
			sbd.append(" and lastdata.taxsumuid is not null and lastdata.taxsumuid<>'~' ");
			sbd.append(" order by lastdata.cyear||lastdata.cperiod desc ");
			
			map4lo = (HashMap<String, String>)getBaseDao().executeQuery(sbd.toString(), new ResultSetProcessor() {
				public Object handleResultSet(ResultSet rs) throws SQLException
				{
					
					HashMap<String, String> map4lo = new HashMap<String, String>();
					while(rs.next())
					{
						if(!map4lo.containsKey(rs.getString(1))){
							map4lo.put(rs.getString(1), rs.getString(2));
						}
					}
					return map4lo;
				}
			});
			return map4lo;
		}finally{
			inC.clear();
		}
	}
	
	/**
	 * 得到上一期间
	 * 修改新建薪资方案加人时找不到上一期间bug
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	public String getPrePeriodVO(String pk_wa_class,String cyear,String cperiod) throws BusinessException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select max(cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_period ");
		//sqlB.append(" inner join wa_periodscheme on wa_periodscheme.pk_periodscheme = wa_period.pk_periodscheme");
		sqlB.append(" where " );
		//sqlB.append(" wa_periodscheme.pk_periodscheme = " );
		//sqlB.append(" ( select pk_periodscheme from wa_waclass where  pk_wa_class = '"+ pk_wa_class + "' ) and  ");
		sqlB.append("    (cyear || cperiod) < '" + cyear + cperiod + "' ");

		YearPeriodSeperatorVO seperator = executeQueryVO(sqlB.toString(), YearPeriodSeperatorVO.class);

		if (seperator != null && seperator.getYearperiod()!=null) {
			return seperator.getYearperiod();
		} else {
			return null;
		}
	}



	public void setExistsUid(String pkWaClassCond, String yearperiod) throws BusinessException {
		
		String lastYearperiod = getPrePeriodVO("",yearperiod.substring(0,4),yearperiod.substring(4,6));
		StringBuilder sbd = new StringBuilder();

		sbd.append(" update wa_data ");
		sbd.append("   set taxsumuid = ");
		sbd.append("       (select max(taxsumuid) ");
		sbd.append("          from wa_data wadata ");
		sbd.append("         where wadata.pk_psndoc = wa_data.pk_psndoc ");
		sbd.append("           and wadata.taxorg = wa_data.taxorg ");
		sbd.append("           and wadata.pk_psnorg = wa_data.pk_psnorg ");
		sbd.append("           and ((wadata.cyear || wadata.cperiod = ");
		sbd.append("               wa_data.cyear || wa_data.cperiod and ");
		sbd.append("               wadata.pk_wa_class <> wa_data.pk_wa_class) or ");
		sbd.append("               (wadata.cyear || wadata.cperiod = '"+lastYearperiod+"')) ");
		sbd.append("           and coalesce(taxsumuid, '~') <> '~') ");
		sbd.append(" where wa_data.cyear || wa_data.cperiod = '"+yearperiod+"' ");
		sbd.append("   and wa_data.pk_wa_class in ("+pkWaClassCond+") ");
		
		getBaseDao().executeUpdate(sbd.toString());
		
	}
		
}
