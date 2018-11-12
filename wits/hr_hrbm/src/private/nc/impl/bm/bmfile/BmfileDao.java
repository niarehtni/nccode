package nc.impl.bm.bmfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.itf.bm.bmfile.IHRBmfileConstant;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.pubitf.resa.costcenter.ICostCenterPubService;
import nc.util.bm.BmPeriodUtil;
import nc.vo.bm.bmclass.AssignclsVO;
import nc.vo.bm.bmclass.BmClassVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.bm.data.BmPayOrgVO;
import nc.vo.bm.pub.BmLoginContext;
import nc.vo.bm.pub.BmPowerSqlHelper;
import nc.vo.bm.pub.IHRBMDataResCode;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class BmfileDao extends AppendBaseDAO {

	/**
	 * 查询所有期间
	 * 
	 * @param pk_org
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllPeriod(String pk_org) throws DAOException {
		String sql = "select min(startyear||startperiod) as startPeriod,max(cyear||cperiod) as newPeriod "
				+ "from bm_bmclass where pk_org = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_org);
		return (Map<String, Object>) getBaseDao().executeQuery(sql, param, new MapProcessor());
	}

	/**
	 * 查询所有期间
	 * 
	 * @param pk_org
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllPeriodForRpt(String whereSql) throws DAOException {

		String sql = "select min(startyear||startperiod) as startPeriod,max(cyear||cperiod) as newPeriod "
				+ "from bm_bmclass ";
		if (!StringUtils.isEmpty(whereSql)) {
			sql += " where " + whereSql;
		}
		return (Map<String, Object>) getBaseDao().executeQuery(sql, new MapProcessor());
	}

	private int getDBType() {
		return getBaseDao().getDBType();
	}

	/**
	 * 根据工作记录PK获取对应的财务组织PK
	 * 
	 * @param pk_psnjob
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] getPkFinanceOrg(BmDataVO[] payfileVOs) throws BusinessException {
		InSQLCreator inC = null;
		if (ArrayUtils.isEmpty(payfileVOs)) {
			return null;
		}
		try {
			inC = new InSQLCreator();

			String sql = "select job.pk_psnjob as pk_psnjob,org.pk_org as pk_org, org.pk_vid as pk_vid,"
					+ " org.orgtype5 as is_financeorg,job.pk_dept as pk_dept,dept.pk_vid as deptvid,"
					+ " org.pk_corp as pk_corp,corp.pk_vid as corpvid  from org_orgs org "
					+ " inner join hi_psnjob job on org.pk_org=job.pk_org and job.pk_psnjob in ("
					+ inC.getInSQL(payfileVOs, BmDataVO.PK_PSNJOB) + ")"
					+ " inner join org_corp corp on corp.pk_corp = org.pk_corp "
					+ " inner join org_dept dept on dept.pk_dept = job.pk_dept ";

			BmPayOrgVO[] payOrgVOs = this.executeQueryVOs(sql, BmPayOrgVO.class);

			for (BmPayOrgVO payOrgVO : payOrgVOs) {
				payOrgVO.setPk_financeorg(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE) ? payOrgVO.getPk_org()
						: payOrgVO.getPk_corp());
				payOrgVO.setPk_financedept(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE) ? payOrgVO.getPk_dept()
						: "");

				payOrgVO.setFiporgvid(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE) ? payOrgVO.getPk_vid()
						: payOrgVO.getCorpvid());
				payOrgVO.setFipdeptvid(payOrgVO.getIs_financeorg().equals(UFBoolean.TRUE) ? payOrgVO.getDeptvid() : "");

				for (BmDataVO payfileVO : payfileVOs) {
					if (payOrgVO.getPk_psnjob().equals(payfileVO.getPk_psnjob())) {
						payfileVO.setPk_financeorg(payOrgVO.getPk_financeorg());
						payfileVO.setPk_financedept(payOrgVO.getPk_financedept());
						payfileVO.setFiporgvid(payOrgVO.getFiporgvid());
						payfileVO.setFipdeptvid(payOrgVO.getFipdeptvid());
						break;
					}
				}

			}
			boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_RESA);

			if (isEnable) {
				ICostCenterPubService orgQuery = NCLocator.getInstance().lookup(ICostCenterPubService.class);
				for (BmPayOrgVO payOrgVO : payOrgVOs) {
					// 加上默认成本中心和成本部门
					String pk_costcenter = orgQuery.queryCostCenterByDept(payOrgVO.getPk_dept(), false);
					for (BmDataVO payfileVO : payfileVOs) {
						if (payOrgVO.getPk_psnjob().equals(payfileVO.getPk_psnjob())) {
							if (pk_costcenter != null) {
								payfileVO.setPk_liabilityorg(pk_costcenter);
								payfileVO.setPk_liabilitydept(payOrgVO.getPk_dept());
								payfileVO.setLibdeptvid(payOrgVO.getDeptvid());
							}
							break;
						}
					}
				}
			}
			return payfileVOs;
		} finally {
			inC.clear();
		}
	}

	/**
	 * 查询期间内所有方案
	 * 
	 * @param loginContext
	 * @param isCheck
	 *            TODO
	 * @return
	 * @throws DAOException
	 */
	public BmClassVO[] queryBmClass(BmLoginContext loginContext, boolean isGroup, boolean isCheck) throws DAOException {
		String sql = "select bm_bmclass.pk_bm_class,bm_bmclass.code,"
				+ "bm_bmclass.name,bm_bmclass.name2,bm_bmclass.name3,"
				+ "bm_bmclass.name4,bm_bmclass.name5,bm_bmclass.name6, "
				+ "bm_bmclass.cyear, bm_bmclass.cperiod,bm_bmclass.pk_org "
				+ "from bm_bmclass,bm_periodstate,bm_period " + "where bm_bmclass.pk_org = ? "
				+ "and bm_bmclass.pk_bm_class = bm_periodstate.pk_bm_class "
				+ "and bm_bmclass.pk_periodscheme = bm_period.pk_periodscheme "
				+ "and bm_periodstate.pk_bm_period = bm_period.pk_bm_period " + "and bm_periodstate.enableflag = 'Y' "
				+ "and bm_period.year = ? " + "and bm_period.period = ? " + "and bm_bmclass.cyear = ? "
				+ "and bm_bmclass.cperiod = ? ";

		if (isCheck) {
			sql += " and bm_periodstate.checkflag = 'N' ";
		}
		if (isGroup) {
			sql = sql
					+ " and exists (select bm_assigncls.classid from bm_assigncls where bm_assigncls.classid = bm_bmclass.pk_bm_class) ";
		}
		String powerSql = BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(), IHRBMDataResCode.BMCLASS,
				IHRBMDataResCode.BMCLASS, "bm_bmclass");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sql += " and " + powerSql;
		}
		sql = sql + "order by bm_bmclass.pk_bm_class";
		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_org());
		param.addParam(loginContext.getCyear());
		param.addParam(loginContext.getCperiod());
		param.addParam(loginContext.getCyear());
		param.addParam(loginContext.getCperiod());
		return executeQueryVOs(sql, param, BmClassVO.class);
	}

	/**
	 * 查询待加险种
	 * 
	 * @param pk_org
	 * @param pk_psnjob
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws DAOException
	 */
	public BmClassVO[] queryBmClassForAdd(String pk_org, String pk_psnjob, String cyear, String cperiod, String pk_group)
			throws DAOException {

		StringBuilder sbd = new StringBuilder();
		sbd.append("  select distinct bm_bmclass.pk_bm_class , bm_bmclass.code , "
				+ SQLHelper.getMultiLangNameColumn("bm_bmclass.name") + " name ");
		sbd.append(" from bm_bmclass inner join bm_periodstate on bm_bmclass.pk_bm_class = bm_periodstate.pk_bm_class ");
		sbd.append("  inner join bm_period on bm_periodstate.pk_bm_period = bm_period.pk_bm_period and bm_period.year = ? and bm_period.period = ? ");
		sbd.append("   where bm_bmclass.pk_org = ? and bm_bmclass.cyear = ? and bm_bmclass.cperiod = ? and bm_bmclass.pk_bm_class not in ( select isnull ( ( select classid from bm_assigncls where pk_sourcecls = ( select pk_sourcecls from bm_assigncls where classid = bm_data.pk_bm_class ) and pk_org = ? ) , bm_data.pk_bm_class ) as pk_bm_class from bm_data where bm_data.pk_psndoc = (select pk_psndoc from hi_psnjob where pk_psnjob = ?) and bm_data.cyear = ? and bm_data.cperiod = ? ) ");
		sbd.append("  and bm_periodstate.checkflag = 'N' ");

		// 增加险种权限
		String powerSql = BmPowerSqlHelper.getRefPowerSql(pk_group, IHRBMDataResCode.BMCLASS, IHRBMDataResCode.BMCLASS,
				"bm_bmclass");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sbd.append(" and " + powerSql);
		}

		SQLParameter param = new SQLParameter();
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_org);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_org);
		param.addParam(pk_psnjob);
		param.addParam(cyear);
		param.addParam(cperiod);
		return executeQueryVOs(sbd.toString(), param, BmClassVO.class);
	}

	/**
	 * 查询人员险种数据
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws DAOException
	 */
	public BmDataVO[] queryBmDataByPsndoc(String pk_group, String pk_org, String pk_psndoc, String cyear,
			String cperiod, String itemFileds) throws DAOException {
		// 20151021 xiejie3 NCdp205514521 查询社保缴交数据，增加险种权限控制。
		// 添加数据使用权
		String powerSql = BmPowerSqlHelper.getRefPowerSql(pk_group, IHRBMDataResCode.BMCLASS, IHRBMDataResCode.BMCLASS,
				"bm_bmclass");
		// end
		String sql = "select bm_data.ts,bm_data.pk_bm_data,bm_data.pk_org,bm_data.pk_psndoc,bm_data.pk_psnjob,"
				+ "bm_data.pk_bm_class,bm_data.cyear,bm_data.cperiod,bm_data.bmaccountno,"
				+ "bm_data.paylocation,bm_data.dbegindate,bm_data.accountstate,"
				+ "bm_data.accounttype,"
				+ itemFileds
				+ "bm_data.workorg,bm_data.workorgvid,bm_data.workdept,bm_data.workdeptvid,"
				+ "bm_data.pk_financeorg,bm_data.fiporgvid,bm_data.pk_financedept,bm_data.fipdeptvid,"
				+ "bm_data.pk_liabilityorg,bm_data.pk_liabilitydept,bm_data.libdeptvid,"
				// MOD {查询时添加20个自定义项} kevin.nie 2017-09-12 start
				+ "bm_data.def1,bm_data.def2,bm_data.def3,bm_data.def4,bm_data.def5,bm_data.def6,bm_data.def7,"
				+ "bm_data.def8,bm_data.def9,bm_data.def10,bm_data.def11,bm_data.def12,bm_data.def13,bm_data.def14,"
				+ "bm_data.def15,bm_data.def16,bm_data.def17,bm_data.def18,bm_data.def19,bm_data.def20,"
				// {查询时添加20个自定义项} kevin.nie 2017-09-12 end
				+ SQLHelper.getMultiLangNameColumn("bm_bmclass.name")
				+ " as classname,bm_data.checkflag,bm_data.vcancelreason "
				+ " from bm_data inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class "
				+ " where bm_data.pk_org = ? and bm_data.pk_psndoc = ? "
				+ " and bm_data.cyear = ? and bm_data.cperiod = ? ";
		if (!StringUtils.isEmpty(powerSql)) {
			sql += " and " + powerSql;
		}
		sql += " order by bm_data.pk_bm_class ";

		SQLParameter param = new SQLParameter();
		param.addParam(pk_org);
		param.addParam(pk_psndoc);
		param.addParam(cyear);
		param.addParam(cperiod);
		return executeQueryVOs(sql, param, BmDataVO.class);
	}

	/**
	 * 查询社保数据
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfile(BmLoginContext loginContext, String condition, String operate)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct hi_psnjob.pk_psndoc, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("       hi_psnjob.recordnum, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  psnname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  deptname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  postname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_job.jobname") + "  jobname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  psnclname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  orgname, ");
		sqlBuffer.append("       bm_data.pk_transferorg ");
		sqlBuffer.append("  from bm_data ");
		sqlBuffer.append(" inner join hi_psnjob on bm_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = bm_data.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnorg on hi_psnorg.pk_psnorg = bm_data.pk_psnorg ");
		sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		sqlBuffer.append("  left outer join org_dept_v on org_dept_v.pk_vid = bm_data.workdeptvid ");
		sqlBuffer.append("  left outer join org_orgs_v on org_orgs_v.pk_vid = bm_data.workorgvid ");
		sqlBuffer.append("  inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class ");
		if (IHRBmfileConstant.ACTION_TRANSFERIN.equals(operate)) {
			sqlBuffer.append(" where bm_data.pk_transferorg = ?");
			sqlBuffer.append(" and bm_data.accountstate = '3'");
			sqlBuffer.append(" and bm_data.cyear || bm_data.cperiod <= ?");
		} else if (IHRBmfileConstant.ACTION_TRNOUTCANCEL.equals(operate)) {
			sqlBuffer.append(" where bm_data.pk_org = ?");
			sqlBuffer.append(" and bm_data.accountstate = '3'");
			sqlBuffer.append(" and bm_data.cyear || bm_data.cperiod <= ?");
		} else if (IHRBmfileConstant.ACTION_UNSEAL.equals(operate)) {
			sqlBuffer.append(" where bm_data.pk_org = ?");
			sqlBuffer.append(" and bm_data.accountstate = '1'");
			sqlBuffer.append(" and bm_data.cyear || bm_data.cperiod = ?");
		} else {
			sqlBuffer.append(" where bm_data.pk_org = ?");
			sqlBuffer.append(" and bm_data.accountstate = '0'");
			sqlBuffer.append(" and bm_data.cyear || bm_data.cperiod = ?");
		}

		if (!StringUtil.isEmptyWithTrim(condition)) {
			sqlBuffer.append(" and bm_data.pk_psnjob in (select pk_psnjob from hi_psnjob where " + condition + ")");
		}
		// 添加数据使用权
		String powerSql = BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(), IHRBMDataResCode.BMCLASS,
				IHRBMDataResCode.BMCLASS, "bm_bmclass");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		sqlBuffer.append(" order by hi_psnjob.pk_psndoc ,hi_psnjob.recordnum    ");
		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_org());
		param.addParam(loginContext.getCyear() + loginContext.getCperiod());
		BmDataVO[] vos = this.executeQueryAppendableVOs(sqlBuffer.toString(), param, BmDataVO.class);
		if (vos == null) {
			return null;
		}
		// return setBmfileClassInfo(loginContext, vos, operate);
		List<BmDataVO> vosList = new ArrayList<BmDataVO>();
		String pk_psndoc = "";
		for (BmDataVO vo : vos) {
			if (!pk_psndoc.equals(vo.getPk_psndoc())) {
				vosList.add(vo);
				pk_psndoc = vo.getPk_psndoc();
			}
		}
		return setBmfileClassInfo(loginContext, (BmDataVO[]) vosList.toArray(new BmDataVO[0]), operate);
	}

	/**
	 * 查询社保数据
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryBmfile4DelAndTransferOut(BmLoginContext loginContext, String condition, String operate,
			String powerSql) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct hi_psnjob.pk_psndoc, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("       hi_psnjob.recordnum, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  psnname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_dept.name") + "  deptname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  postname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_job.jobname") + "  jobname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  psnclname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + "  orgname, ");
		sqlBuffer.append("       bm_data.pk_transferorg ,");
		// 20151107 xiejie3 校验权限需要用到pk_org的值，适配最新的uap权限校验规则。
		sqlBuffer.append("       bm_data.pk_org ");
		// end
		sqlBuffer.append("  from bm_data ");
		sqlBuffer.append(" inner join hi_psnjob on bm_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = bm_data.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnorg on hi_psnorg.pk_psnorg = bm_data.pk_psnorg ");
		sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		sqlBuffer.append("  inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class ");
		sqlBuffer.append("  left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept  ");
		sqlBuffer.append("  left outer join org_orgs on hi_psnjob.pk_org = org_orgs.pk_org  ");

		// sqlBuffer.append("  left outer join org_dept_v on org_dept_v.pk_vid = bm_data.workdeptvid ");
		// sqlBuffer.append("  left outer join org_orgs_v on org_orgs_v.pk_vid = bm_data.workorgvid ");
		if (IHRBmfileConstant.ACTION_TRANSFERIN.equals(operate)) {
			sqlBuffer.append(" where bm_data.pk_transferorg = ?");
			sqlBuffer.append(" and bm_data.accountstate = 3");
		} else if (IHRBmfileConstant.ACTION_TRNOUTCANCEL.equals(operate)) {
			sqlBuffer.append(" where bm_data.pk_org = ?");
			sqlBuffer.append(" and bm_data.accountstate = 3");
		} else if (IHRBmfileConstant.ACTION_UNSEAL.equals(operate)) {
			sqlBuffer.append(" where bm_data.pk_org = ?");
			sqlBuffer.append(" and bm_data.accountstate = 1");
		} else if (IHRBmfileConstant.ACTION_BATCHDEL.equals(operate)) {
			sqlBuffer.append(" where bm_data.pk_org = ?");// 批量删除时，展现“正常”、“封存”、“注销”
			sqlBuffer.append(" and bm_data.accountstate in ( 0,1,2) ");
		} else {
			sqlBuffer.append(" where bm_data.pk_org = ?");
			sqlBuffer.append(" and bm_data.accountstate = 0");
		}
		sqlBuffer.append(" and bm_data.cyear = ?");
		sqlBuffer.append(" and bm_data.cperiod = ?");

		if (!StringUtil.isEmptyWithTrim(condition)) {
			sqlBuffer.append(" and " + condition);
		}

		if (!StringUtils.isBlank(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}

		String powerSql4BmClass = BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(), IHRBMDataResCode.BMCLASS,
				IHRBMDataResCode.BMCLASS, "bm_bmclass");
		if (!StringUtils.isBlank(powerSql4BmClass)) {
			sqlBuffer.append(" and " + powerSql4BmClass);
		}

		sqlBuffer.append(" order by hi_psnjob.pk_psndoc ,hi_psnjob.recordnum    ");
		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_org());
		param.addParam(loginContext.getCyear());
		param.addParam(loginContext.getCperiod());
		BmDataVO[] vos = this.executeQueryAppendableVOs(sqlBuffer.toString(), param, BmDataVO.class);
		if (vos == null) {
			return null;
		}
		List<BmDataVO> vosList = new ArrayList<BmDataVO>();
		String pk_psndoc = "";
		for (BmDataVO vo : vos) {
			if (!pk_psndoc.equals(vo.getPk_psndoc())) {
				vosList.add(vo);
				pk_psndoc = vo.getPk_psndoc();
			}
		}
		return setBmfileClassInfo(loginContext, (BmDataVO[]) vosList.toArray(new BmDataVO[0]), operate);
	}

	public BmDataVO[] queryByCondition(BmLoginContext context, String condition, String orderCondtion)
			throws BusinessException {
		if (context.getPk_org() == null || context.getCyear() == null || context.getCperiod() == null) {
			return null;
		}
		String sql = " bm_data.accountstate = '0' ";
		if (condition != null && condition.indexOf("accountstate") > -1) {
			sql = " bm_data.pk_bm_data in (select pk_bm_data from bm_data where " + condition + ")";
		} else if (condition != null) {
			sql = sql + " and bm_data.pk_bm_data in (select pk_bm_data from bm_data where " + condition + ")";
		}

		sql = sql + " and bm_data.pk_org = '" + context.getPk_org() + "' and bm_data.cyear = '" + context.getCyear()
				+ "' and bm_data.cperiod = '" + context.getCperiod() + "'";
		String powerSql = BmPowerSqlHelper.getRefPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRBMDataResCode.BMDEFAULT, "bm_data");
		// 20150929 xiejie3 NCdp205502023 没有授数据权限控制
		// 原因是，获取权限sql为null，导致sql拼接异常。
		// 海澜集团按照组织部门授权是专项补丁，涉及的地方较多，容易引发问题，撤掉补丁
		// 20150107 shenliangc 海澜集团按照组织部门授权
		//
		// if (!StringUtil.isEmptyWithTrim(powerSql)) {
		// powerSql += " and " +
		// BmPowerSqlHelper.getRefPowerSql(context.getPk_group(),
		// HICommonValue.RESOUCECODE_ORG,"default","bm_data");
		// }else{
		// powerSql = BmPowerSqlHelper.getRefPowerSql(context.getPk_group(),
		// HICommonValue.RESOUCECODE_ORG,"default","bm_data");
		// }
		//
		// if (!StringUtil.isEmptyWithTrim(powerSql)) {
		// //20150113 shenliangc 海澜集团按照组织部门授权，按照任职组织授权 begin
		// powerSql = powerSql.replace("bm_data.pk_org in",
		// "bm_data.workorg in");
		// //end
		// powerSql += " and " +
		// BmPowerSqlHelper.getRefPowerSql(context.getPk_group(),
		// HICommonValue.RESOUCECODE_DEPT,"default","org_dept");
		// }else{
		// powerSql = BmPowerSqlHelper.getRefPowerSql(context.getPk_group(),
		// HICommonValue.RESOUCECODE_DEPT,"default","org_dept");
		// }
		// end
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sql += " and " + powerSql;
		}
		powerSql = BmPowerSqlHelper.getRefPowerSql(context.getPk_group(), IHRBMDataResCode.BMCLASS,
				IHRBMDataResCode.BMCLASS, "bm_bmclass");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sql += " and " + powerSql;
		}

		StringBuffer sqlBuffer = new StringBuffer();
		// 20150819 shenliangc 社保档案查询性能优化，根据排序字段动态确定关联表 begin
		// bd_psndoc.name,org_orgs.name,bd_psncl.name
		sqlBuffer.append(" select pk_bm_data from bm_data ");
		sqlBuffer.append(" inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class ");
		// sqlBuffer.append(" inner join bd_psndoc on bm_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on bm_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		// sqlBuffer.append(" left join org_orgs on hi_psnjob.pk_org = org_orgs.pk_org ");
		// sqlBuffer.append(" left join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept where ");
		sqlBuffer.append(" left join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept  ");
		// sqlBuffer.append(" left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		// sqlBuffer.append(sql);
		// 20151113 xiejie3 NCdp205540416 社保档案按部门编码、任职部门、任职组织排序，同一人员，部门人员的档案分行显示
		// 原因：由于按照部门排序，会出现同一人员的数据分散开，
		// 解决方案：默认加上一个order by bm_data.pk_psndoc，如果前面的order by
		// 可以得到唯一的数据，则最后这一条不会生效，如果不唯一，则按照人员进行分类。
		// 界面加载数据，orderCondtion为空，排序时部位空。
		if (StringUtils.isEmpty(orderCondtion)) {
			orderCondtion += " bm_data.pk_psndoc ";
		} else {
			orderCondtion += " ,bm_data.pk_psndoc ";
		}
		// end

		if (!StringUtil.isEmpty(orderCondtion)) {
			if (orderCondtion.contains("bd_psndoc.name")) {
				sqlBuffer.append(" inner join bd_psndoc on bm_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			}
			if (orderCondtion.contains("org_orgs.name")) {
				sqlBuffer.append(" left join org_orgs on hi_psnjob.pk_org = org_orgs.pk_org ");
			}
			if (orderCondtion.contains("bd_psncl.name")) {
				// 20151120 shenliangc NCdp205545342 社保档案人员类别字段排序不生效。
				sqlBuffer.append(" left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
			}
			// 20150819 shenliangc 社保档案查询性能优化，根据排序字段动态确定关联表 end
			// 20151112 xiejie3 NCdp205540416 按姓名、人员类别排序不生效
			sqlBuffer.append(" where " + sql);
			// end
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			// 20151112 xiejie3 NCdp205540416 按姓名、人员类别排序不生效
			sqlBuffer.append(" where " + sql);
			// end
			sqlBuffer.append(" order by org_dept.code,hi_psnjob.clerkcode,bm_bmclass.code ");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), BmDataVO.class);
	}

	public String[] queryPKSByCondition(BmLoginContext context, String condition, String orderCondtion)
			throws BusinessException {

		BmDataVO[] vos = queryByCondition(context, condition, orderCondtion);

		String[] pks = new String[0];
		if (vos != null) {
			pks = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				pks[i] = vos[i].getPk_bm_data();
			}

		}
		return pks;

	}

	/**
	 * 查询个人信息
	 * 
	 * @param pk_psnjob
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO queryPsnByPsnjob(String pk_psnjob) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select hi_psnjob.pk_psnjob, "); // 1
		sqlBuffer.append("       hi_psnjob.pk_psnorg, "); // 2
		sqlBuffer.append("       hi_psnjob.assgid, "); // 3
		sqlBuffer.append("       hi_psnjob.pk_psndoc, "); // 4
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 5
		sqlBuffer.append("       org_orgs.pk_org as workorg, ");
		sqlBuffer.append("       org_orgs.pk_vid as workorgvid, ");
		sqlBuffer.append("       org_dept.pk_dept as workdept, ");
		sqlBuffer.append("       org_dept.pk_vid as workdeptvid ");
		sqlBuffer.append("  from hi_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		sqlBuffer.append("  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
		sqlBuffer.append(" where hi_psnjob.pk_psnjob =?");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_psnjob);

		BmDataVO[] dataVOs = executeQueryVOs(sqlBuffer.toString(), param, BmDataVO.class);
		// BmDataVO[] dataVOs =
		// getPkFinanceOrg(executeQueryVOs(sqlBuffer.toString(),param,BmDataVO.class));
		if (ArrayUtils.isEmpty(dataVOs)) {
			return null;
		}
		return dataVOs[0];
	}

	/**
	 * 查询人员信息
	 * 
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] queryPsnForAdd(BmLoginContext loginContext, String condition, boolean lastpsnorg)
			throws BusinessException {
		/*
		 * String endDate = loginContext.getCyear() + BmPeriodUtil.interDate +
		 * loginContext.getCperiod() + "-31"; String beginDate
		 * =loginContext.getCyear() + BmPeriodUtil.interDate +
		 * loginContext.getCperiod() + "-01";
		 */

		UFLiteralDate beginDate = new UFLiteralDate(loginContext.getCyear() + BmPeriodUtil.interDate
				+ loginContext.getCperiod() + "-01");
		UFLiteralDate endDate = BmPeriodUtil.getTheLastDate(beginDate);
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct hi_psnjob.pk_psnjob, "); // 1
		sqlBuffer.append("       hi_psnjob.pk_psnorg, "); // 2
		sqlBuffer.append("       hi_psnjob.assgid, "); // 3
		sqlBuffer.append("       hi_psnjob.pk_psndoc, "); // 4
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 5
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  psnname, "); // 6
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_dept.name") + "  deptname, "); // 7
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  postname, "); // 6
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_job.jobname") + "  jobname, "); // 6
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  psnclname, "); // 6
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + "  orgname, "); // 10
		sqlBuffer.append("       org_orgs.pk_org as workorg, ");
		sqlBuffer.append("       org_orgs.pk_vid as workorgvid, ");
		sqlBuffer.append("       org_dept.pk_dept as workdept, ");
		sqlBuffer.append("       org_dept.pk_vid as workdeptvid ");
		sqlBuffer.append("  from hi_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		sqlBuffer.append("  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
		sqlBuffer.append("   left outer join bm_data on bd_psndoc.pk_psndoc = bm_data.pk_psndoc   ");
		sqlBuffer.append("   and bm_data.pk_group = ? and  bm_data.cyear = ? and bm_data.cperiod = ? ");

		sqlBuffer.append(" where hi_psnjob.begindate<=?");
		sqlBuffer.append("   and (hi_psnjob.enddate>=? or hi_psnjob.enddate is null)");
		sqlBuffer.append("   and hi_psnjob.ismainjob = 'Y'  ");
		sqlBuffer.append("   and hi_psnorg.psntype = 0 ");// 员工
		sqlBuffer.append("   and hi_psnorg.indocflag = 'Y' ");
		if (lastpsnorg)
			sqlBuffer.append("   and hi_psnorg.lastflag = 'Y' ");
		sqlBuffer.append("   and hi_psnjob.pk_group = ? ");
		// sqlBuffer.append(" and hi_psnjob.pk_psnjob in ");
		// sqlBuffer.append(" ( select a.pk_psnjob pk_psnjob from hi_psnjob a  where a.recordnum in ");
		// sqlBuffer.append(" (select min(b.recordnum) from  hi_psnjob b  where a.pk_psndoc=b.pk_psndoc  and b.begindate<=? and (b.ENDDATE is null or b.ENDDATE>=?))  ");
		// sqlBuffer.append(" ) ");

		sqlBuffer.append("    and hi_psnjob.begindate = (select max(psnjob.begindate) from hi_psnjob psnjob ");
		sqlBuffer.append(" where hi_psnjob.pk_psndoc = psnjob.pk_psndoc and hi_psnjob.pk_psnorg=psnjob.pk_psnorg");
		sqlBuffer
				.append(" and psnjob.begindate<= ? and (psnjob.enddate>= ? or psnjob.enddate is null) and psnjob.ismainjob='Y' ) ");
		if (!StringUtil.isEmptyWithTrim(condition)) {
			sqlBuffer.append(" and hi_psnjob.pk_psnjob in (select pk_psnjob from hi_psnjob where " + condition + ")");
		}

		// sqlBuffer.append(" order by hi_psnjob.pk_psndoc ");
		sqlBuffer.append(" order by hi_psnjob.clerkcode ");
		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_group());
		param.addParam(loginContext.getCyear());
		param.addParam(loginContext.getCperiod());

		param.addParam(endDate);
		param.addParam(beginDate);
		param.addParam(loginContext.getPk_group());
		param.addParam(endDate);
		param.addParam(beginDate);
		BmDataVO[] vos = executeQueryVOs(sqlBuffer.toString(), param, BmDataVO.class);
		if (vos == null) {
			return new BmDataVO[0];
		}

		return setBmClassInfo(loginContext, vos);
	}

	/**
	 * @param pk_bm_class
	 * @param pk_trnorg
	 * @param year
	 * @param period
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<String> queryTransferClass(String pk_bm_class, String pk_trnorg, String year, String period)
			throws DAOException {
		// String sql = " SELECT classid "
		// + "FROM bm_assigncls "
		// + "WHERE pk_sourcecls = (	SELECT pk_sourcecls "
		// + "						FROM bm_assigncls "
		// + "						WHERE classid = ? "
		// + "	)" + "	AND pk_org = ?";
		String sql = "SELECT distinct classid FROM bm_assigncls inner join bm_bmclass on bm_bmclass.pk_bm_class = bm_assigncls.classid "
				+ " inner join bm_period on bm_bmclass.pk_periodscheme = bm_period.pk_periodscheme and "
				+ "bm_period.year = bm_bmclass.cyear and bm_period.period = bm_bmclass.cperiod "
				+ "inner join bm_periodstate on bm_periodstate.pk_bm_period = bm_period.pk_bm_period and bm_periodstate.pk_bm_class = bm_bmclass.pk_bm_class "

				+ "WHERE pk_sourcecls in ( SELECT pk_sourcecls FROM bm_assigncls WHERE classid in("
				+ pk_bm_class
				+ ") ) " + "AND bm_assigncls.pk_org = ? and bm_bmclass.cyear=? and bm_bmclass.cperiod=?";
		SQLParameter param = new SQLParameter();
		// param.addParam(pk_bm_class);
		param.addParam(pk_trnorg);
		param.addParam(year);
		param.addParam(period);
		return (List<String>) getBaseDao().executeQuery(sql, param, new ColumnListProcessor());
	}

	/***************************************************************************
	 * TODO <br>
	 * Created on 2012-10-31 上午11:18:25<br>
	 * 
	 * @param pk_bm_class
	 * @param pk_trnorg
	 * @return
	 * @throws DAOException
	 * @author daicy
	 ***************************************************************************/
	@SuppressWarnings("unchecked")
	public List<BmClassVO> queryTransferClass(String pk_bm_class, String pk_trnorg) throws DAOException {
		// String sql = " SELECT classid "
		// + "FROM bm_assigncls "
		// + "WHERE pk_sourcecls = (	SELECT pk_sourcecls "
		// + "						FROM bm_assigncls "
		// + "						WHERE classid = ? "
		// + "	)" + "	AND pk_org = ?";
		String sql = "SELECT DISTINCT "
				+ SQLHelper.getMultiLangNameColumn("bm_bmclass.name")
				+ " name,bm_bmclass.pk_bm_class,bm_bmclass.CYEAR,bm_bmclass.CPERIOD FROM bm_assigncls inner join bm_bmclass on bm_bmclass.pk_bm_class = bm_assigncls.classid "
				+ " inner join bm_period on bm_bmclass.pk_periodscheme = bm_period.pk_periodscheme and "
				+ "bm_period.year = bm_bmclass.cyear and bm_period.period = bm_bmclass.cperiod "
				+ "inner join bm_periodstate on bm_periodstate.pk_bm_period = bm_period.pk_bm_period "
				+ "and bm_periodstate.pk_bm_class = bm_bmclass.pk_bm_class  AND bm_periodstate.checkflag = 'N'"
				+ "WHERE pk_sourcecls in ( SELECT pk_sourcecls FROM bm_assigncls WHERE classid in(" + pk_bm_class
				+ ") ) " + "AND bm_assigncls.pk_org = ?";
		SQLParameter param = new SQLParameter();
		// param.addParam(pk_bm_class);
		param.addParam(pk_trnorg);
		// param.addParam(year);
		// param.addParam(period);
		return (List<BmClassVO>) getBaseDao().executeQuery(sql, param, new BeanListProcessor(BmClassVO.class));
	}

	/***************************************************************************
	 * TODO <br>
	 * Created on 2012-10-31 上午11:18:25<br>
	 * 
	 * @param pk_bm_class
	 * @param pk_trnorg
	 * @return
	 * @throws DAOException
	 * @author suihang
	 ***************************************************************************/
	@SuppressWarnings("unchecked")
	public List<BmClassVO> queryAllTransferClass(String pk_bm_class, String pk_trnorg) throws DAOException {

		String sql = "SELECT DISTINCT "
				+ SQLHelper.getMultiLangNameColumn("bm_bmclass.name")
				+ " name,bm_assigncls.pk_sourcecls pk_bm_class,bm_bmclass.CYEAR,bm_bmclass.CPERIOD FROM bm_assigncls inner join bm_bmclass on bm_bmclass.pk_bm_class = bm_assigncls.classid "
				+ " inner join bm_period on bm_bmclass.pk_periodscheme = bm_period.pk_periodscheme and "
				+ "bm_period.year = bm_bmclass.cyear and bm_period.period = bm_bmclass.cperiod "
				+ "inner join bm_periodstate on bm_periodstate.pk_bm_period = bm_period.pk_bm_period "
				+ "and bm_periodstate.pk_bm_class = bm_bmclass.pk_bm_class  "
				+ "WHERE pk_sourcecls in ( SELECT pk_sourcecls FROM bm_assigncls WHERE classid in(" + pk_bm_class
				+ ") ) " + "AND bm_assigncls.pk_org = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_trnorg);

		return (List<BmClassVO>) getBaseDao().executeQuery(sql, param, new BeanListProcessor(BmClassVO.class));
	}

	@SuppressWarnings("unchecked")
	public List<BmClassVO> querySourceClass(String pk_bm_class) throws DAOException {

		String sql = "SELECT DISTINCT "
				+ SQLHelper.getMultiLangNameColumn("bm_bmclass.name")
				+ " name,bm_assigncls.pk_sourcecls pk_bm_class,bm_bmclass.CYEAR,bm_bmclass.CPERIOD FROM   bm_bmclass   "
				+ " inner join bm_assigncls on bm_bmclass.pk_bm_class = bm_assigncls.classid"
				+ " inner join bm_period on bm_bmclass.pk_periodscheme = bm_period.pk_periodscheme and "
				+ "bm_period.year = bm_bmclass.cyear and bm_period.period = bm_bmclass.cperiod "
				+ "inner join bm_periodstate on bm_periodstate.pk_bm_period = bm_period.pk_bm_period "
				+ "and bm_periodstate.pk_bm_class = bm_bmclass.pk_bm_class  " + "WHERE bm_bmclass.pk_bm_class  in("
				+ pk_bm_class + ")  ";
		return (List<BmClassVO>) getBaseDao().executeQuery(sql, new BeanListProcessor(BmClassVO.class));
	}

	/**
	 * 修改效率问题 <br>
	 * Created on 2012-10-9 11:25:08<br>
	 * 
	 * @param pk_bm_class
	 * @param pk_trninorg
	 * @param pk_trnoutorg
	 * @param year
	 * @param period
	 * @return Map<String, String>
	 * @throws DAOException
	 * @author caiqm
	 */
	// public Map<String, String> queryTransferClass(String pk_bm_class, String
	// pk_trninorg, String pk_trnoutorg, String year, String period)
	// throws DAOException
	// {
	// String strSql =
	// "SELECT temptable.sourceclassid as sourceclassid,temptable.destclassid as destclassid FROM  bm_bmclass ,"
	// +
	// "(SELECT a.classid AS destclassid ,b.classid sourceclassid FROM bm_assigncls a ,bm_assigncls b WHERE a.pk_sourcecls = b.pk_sourcecls AND a.pk_org = ? AND b.pk_org = ?"
	// +
	// "b.classid IN ? )temptable WHERE bm_bmclass.pk_bm_class = temptable.destclassid AND bm_bmclass.cyear = ? AND bm_bmclass.cperiod = ?";
	// SQLParameter param = new SQLParameter();
	// param.addParam(pk_trninorg);
	// param.addParam(pk_trnoutorg);
	// param.addParam("(" + pk_bm_class + ")");
	// param.addParam(year);
	// param.addParam(period);
	// AppendableVO[] appendableVOs =
	// (AppendableVO[]) getBaseDao().executeQuery(strSql, param, new
	// AppendBeanArrayProcessor(AppendableVO.class));
	// if (appendableVOs == null || appendableVOs.length == 0)
	// {
	// return null;
	// }
	// Map<String, String> result = new HashMap<String, String>();
	// for (int i = 0; i < appendableVOs.length; i++)
	// {
	// result.put((String) appendableVOs[i].getAttributeValue("sourceclassid"),
	// (String) appendableVOs[i].getAttributeValue("destclassid"));
	// }
	// return result;
	//
	// // return getBaseDao().executeQuery(sql, param, new
	// ColumnProcessor("classid"));
	// }

	public PsnTrnVO[] queryTrnInBmfile(BmLoginContext loginContext) throws DAOException {
		// 添加数据使用权
		String powerSql = BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(), IHRBMDataResCode.BMCLASS,
				IHRBMDataResCode.BMCLASS, "bm_bmclass");
		// 20150929 xiejie3 NCdp205502023 没有授数据权限控制
		// 原因是，获取权限sql为null，导致sql拼接异常。
		// 海澜集团按照组织部门授权是专项补丁，涉及的地方较多，容易引发问题，撤掉补丁
		// 20150204 shenliangc 海澜集团社保档案变动人员转入人员和转出人员页签报错“缺失表达式”，完善权限语句拼装逻辑。
		// begin
		// 20150107 shenliangc 海澜集团按照组织部门授权
		// String orgPowerSql =
		// BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_ORG,"default","bm_data");
		//
		// String deptPowerSql =
		// BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_DEPT,"default","org_dept");
		//
		// if (!StringUtil.isEmptyWithTrim(powerSql) &&
		// !StringUtil.isEmptyWithTrim(orgPowerSql)) {
		// powerSql += " and " + orgPowerSql;
		// }else if(!StringUtil.isEmptyWithTrim(orgPowerSql)){
		// powerSql = orgPowerSql;
		// }
		//
		// if (!StringUtil.isEmptyWithTrim(powerSql) &&
		// !StringUtil.isEmptyWithTrim(deptPowerSql)) {
		// //20150113 shenliangc 海澜集团按照组织部门授权，按照任职组织授权 begin
		// powerSql = powerSql.replace("bm_data.pk_org in",
		// "bm_data.workorg in");
		// //end
		// powerSql += " and " + deptPowerSql;
		// }else if(!StringUtil.isEmptyWithTrim(deptPowerSql)){
		// powerSql = deptPowerSql;
		// }
		// end
		// end
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct hi_psnjob.pk_psndoc,hi_psnjob.pk_psnjob,bd_psndoc.code as psncode,");
		sqlBuffer.append("       hi_psnjob.clerkcode,bm_data.pk_transferorg,bd_psndoc.id as psnid,");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  psnname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  deptname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  postname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_job.jobname") + "  jobname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  psnclassname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  orgname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + "  psntypename ");
		sqlBuffer.append("  from bm_data ");
		sqlBuffer.append(" inner join hi_psnjob on bm_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = bm_data.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnorg on hi_psnorg.pk_psnorg = bm_data.pk_psnorg ");
		sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		sqlBuffer.append("  left outer join org_dept_v on org_dept_v.pk_vid = bm_data.workdeptvid ");
		sqlBuffer.append("  left outer join org_orgs_v on org_orgs_v.pk_vid = bm_data.workorgvid ");
		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = bm_data.pk_transferorg ");
		sqlBuffer.append("  inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class ");
		// 20150929 xiejie3 NCdp205502023 没有授数据权限控制
		// 原因是，获取权限sql为null，导致sql拼接异常。
		// 海澜集团按照组织部门授权是专项补丁，涉及的地方较多，容易引发问题，撤掉补丁
		// 20150107 shenliangc 海澜集团按照组织部门授权
		// sqlBuffer.append(" left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		// end
		sqlBuffer.append(" where bm_data.pk_transferorg = ?");
		sqlBuffer.append(" and bm_data.cyear||bm_data.cperiod <= ?");
		sqlBuffer.append(" and bm_data.accountstate = '3'");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		sqlBuffer.append(" order by hi_psnjob.pk_psndoc ");
		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_org());
		param.addParam(loginContext.getCyear() + loginContext.getCperiod());

		return executeQueryVOs(sqlBuffer.toString(), param, PsnTrnVO.class);
	}

	public PsnTrnVO[] queryTrnOutBmfile(BmLoginContext loginContext) throws DAOException {
		// 添加数据使用权
		String powerSql = BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(), IHRBMDataResCode.BMCLASS,
				IHRBMDataResCode.BMCLASS, "bm_bmclass");
		// 20150929 xiejie3 NCdp205502023 没有授数据权限控制
		// 原因是，获取权限sql为null，导致sql拼接异常。
		// 海澜集团按照组织部门授权是专项补丁，涉及的地方较多，容易引发问题，撤掉补丁
		// 20150204 shenliangc 海澜集团社保档案变动人员转入人员和转出人员页签报错“缺失表达式”，完善权限语句拼装逻辑。
		// begin
		// 20150107 shenliangc 海澜集团按照组织部门授权
		// String orgPowerSql =
		// BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_ORG,"default","bm_data");
		//
		// String deptPowerSql =
		// BmPowerSqlHelper.getRefPowerSql(loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_DEPT,"default","org_dept");
		//
		// if (!StringUtil.isEmptyWithTrim(powerSql) &&
		// !StringUtil.isEmptyWithTrim(orgPowerSql)) {
		// powerSql += " and " + orgPowerSql;
		// }else if(!StringUtil.isEmptyWithTrim(orgPowerSql)){
		// powerSql = orgPowerSql;
		// }
		//
		// if (!StringUtil.isEmptyWithTrim(powerSql) &&
		// !StringUtil.isEmptyWithTrim(deptPowerSql)) {
		// //20150113 shenliangc 海澜集团按照组织部门授权，按照任职组织授权 begin
		// powerSql = powerSql.replace("bm_data.pk_org in",
		// "bm_data.workorg in");
		// //end
		// powerSql += " and " + deptPowerSql;
		// }else if(!StringUtil.isEmptyWithTrim(deptPowerSql)){
		// powerSql = deptPowerSql;
		// }
		// end
		// end
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct hi_psnjob.pk_psndoc,hi_psnjob.pk_psnjob,bd_psndoc.code as psncode,");
		sqlBuffer.append("       hi_psnjob.clerkcode,bm_data.pk_transferorg,bd_psndoc.id as psnid,");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  psnname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  deptname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  postname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("om_job.jobname") + "  jobname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  psnclassname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  orgname, ");
		sqlBuffer.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + "  psntypename ");
		sqlBuffer.append("  from bm_data ");
		sqlBuffer.append(" inner join hi_psnjob on bm_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on bd_psndoc.pk_psndoc = bm_data.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnorg on hi_psnorg.pk_psnorg = bm_data.pk_psnorg ");
		sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		sqlBuffer.append("  left outer join org_dept_v on org_dept_v.pk_vid = bm_data.workdeptvid ");
		sqlBuffer.append("  left outer join org_orgs_v on org_orgs_v.pk_vid = bm_data.workorgvid ");
		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = bm_data.pk_transferorg ");
		// 20150929 xiejie3 NCdp205502023 没有授数据权限控制
		// 原因是，获取权限sql为null，导致sql拼接异常。
		// 海澜集团按照组织部门授权是专项补丁，涉及的地方较多，容易引发问题，撤掉补丁
		// 20150107 shenliangc 海澜集团按照组织部门授权
		// sqlBuffer.append(" left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		// end
		sqlBuffer.append("  inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class ");
		sqlBuffer.append(" where bm_data.pk_org = ?");
		sqlBuffer.append(" and bm_data.cyear || bm_data.cperiod <= ?");
		sqlBuffer.append(" and bm_data.accountstate = '3'");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		sqlBuffer.append(" order by hi_psnjob.pk_psndoc ");
		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_org());
		param.addParam(loginContext.getCyear() + loginContext.getCperiod());
		return executeQueryVOs(sqlBuffer.toString(), param, PsnTrnVO.class);
	}

	/**
	 * 设置险种数据
	 * 
	 * @param loginContext
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] setBmClassInfo(BmLoginContext loginContext, BmDataVO[] vos) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select pk_psndoc, isnull((SELECT classid " + "FROM bm_assigncls "
				+ "WHERE pk_sourcecls = (	SELECT pk_sourcecls " + "						FROM bm_assigncls "
				+ "						WHERE classid = bm_data.pk_bm_class " + "	)"
				+ "	AND pk_org = ?), bm_data.pk_bm_class) as pk_bm_class from bm_data ");
		InSQLCreator inSql = new InSQLCreator();
		sqlBuffer
				.append(" where pk_psndoc in(" + inSql.getInSQL(vos, "pk_psndoc") + ") and cyear = ? and cperiod = ? ");
		sqlBuffer.append(" order by pk_psndoc ");
		SQLParameter param = new SQLParameter();
		param.addParam(loginContext.getPk_org());
		param.addParam(loginContext.getCyear());
		param.addParam(loginContext.getCperiod());
		BmDataVO[] bmDatas = executeQueryVOs(sqlBuffer.toString(), param, BmDataVO.class);
		inSql.clear();
		if (bmDatas == null) {
			return vos;
		}
		for (int i = 0; i < bmDatas.length; i++) {
			for (int j = 0; j < vos.length; j++) {
				if (bmDatas[i].getPk_psndoc().equals(vos[j].getPk_psndoc())) {
					vos[j].setAttributeValue(bmDatas[i].getPk_bm_class(), UFBoolean.TRUE);
				}
			}
		}
		return vos;
	}

	// /**
	// * 设置险种数据
	// * @param loginContext
	// * @param vos
	// * @return
	// * @throws BusinessException
	// */
	// public BmDataVO[] setBmClassInfo(BmLoginContext loginContext, BmDataVO[]
	// vos) throws BusinessException
	// {
	// Map<String, BmDataVO> map = new HashMap<String, BmDataVO>();
	// for (int i = 0; i < vos.length; i++) {
	// BmDataVO currvo = vos[i];
	//
	// if(!StringUtils.isEmpty(currvo.getPk_bm_class())){
	// currvo.setAttributeValue(currvo.getPk_bm_class(), UFBoolean.TRUE);
	// }
	// BmDataVO vo = map.get(currvo.getPk_psndoc());
	// if(vo!=null){
	// String[] fieldNames = vo.getAppendAttributeNames();
	// if(!ArrayUtils.isEmpty(fieldNames)){
	// for (int j = 0; j < fieldNames.length; j++) {
	// currvo.setAttributeValue(fieldNames[j],
	// vo.getAttributeValue(fieldNames[j]));
	// }
	// }
	//
	// }
	// map.put(currvo.getPk_psndoc(), currvo);
	//
	// }
	//
	// return map.values().toArray(new BmDataVO[map.keySet().size()]);
	// }

	/**
	 * 设置险种信息
	 * 
	 * @param loginContext
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	// public BmDataVO[] setBmfileClassInfo(BmLoginContext loginContext,
	// BmDataVO[] vos, String operate) throws BusinessException
	// {
	// StringBuffer sqlBuffer = new StringBuffer();
	// if (IHRBmfileConstant.ACTION_TRANSFERIN.equals(operate))
	// {
	// sqlBuffer.append("select pk_psndoc, (SELECT classid " +
	// "FROM bm_assigncls " + "WHERE pk_sourcecls = (	SELECT pk_sourcecls "
	// + "						FROM bm_assigncls " +
	// "						WHERE classid = bm_data.pk_bm_class " + "	)"
	// +
	// "	AND pk_org = bm_data.pk_transferorg) as pk_bm_class,pk_bm_data from bm_data ");
	// }
	// else
	// {
	// sqlBuffer.append("select pk_psndoc,pk_bm_class,pk_bm_data from bm_data ");
	// }
	// InSQLCreator inSql = new InSQLCreator();
	// sqlBuffer.append(" where pk_psndoc in(" + inSql.getInSQL(vos,
	// "pk_psndoc") + ") and cyear = ? and cperiod = ? ");
	// if (IHRBmfileConstant.ACTION_TRANSFERIN.equals(operate))
	// {
	// sqlBuffer.append(" and accountState = '3' and pk_transferorg = ? ");
	// }
	// else
	// {
	// sqlBuffer.append(" and pk_org = ? and checkflag = 'N' ");
	// }
	// sqlBuffer.append(" order by pk_psndoc ");
	// SQLParameter param = new SQLParameter();
	// param.addParam(loginContext.getCyear());
	// param.addParam(loginContext.getCperiod());
	// param.addParam(loginContext.getPk_org());
	// BmDataVO[] bmDatas = executeQueryVOs(sqlBuffer.toString(), param,
	// BmDataVO.class);
	// inSql.clear();
	// if (bmDatas == null)
	// {
	// return vos;
	// }
	// for (int i = 0; i < bmDatas.length; i++)
	// {
	// for (int j = 0; j < vos.length; j++)
	// {
	// if (bmDatas[i].getPk_psndoc().equals(vos[j].getPk_psndoc()))
	// {
	// vos[j].setAttributeValue(bmDatas[i].getPk_bm_class(), UFBoolean.TRUE);
	// vos[j].setAttributeValue("pk" + bmDatas[i].getPk_bm_class(),
	// bmDatas[i].getPk_bm_data());
	// }
	// }
	// }
	// return vos;
	// }

	/**
	 * 设置险种信息
	 * 
	 * @param loginContext
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] setBmfileClassInfo(BmLoginContext loginContext, BmDataVO[] vos, String operate)
			throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		SQLParameter param = new SQLParameter();

		InSQLCreator inSql = new InSQLCreator();
		if (IHRBmfileConstant.ACTION_TRANSFERIN.equals(operate)) {
			sqlBuffer
					.append("select pk_psndoc, (SELECT classid "
							+ "FROM bm_assigncls "
							+ "inner join bm_periodstate on bm_assigncls.classid = bm_periodstate.pk_bm_class and bm_periodstate.checkflag='N' "
							+ "inner join bm_period on bm_periodstate.pk_bm_period = bm_period.pk_bm_period "
							+ "and bm_period.year || bm_period.period = ? "
							+ "WHERE pk_sourcecls = (	SELECT pk_sourcecls "
							+ "						FROM bm_assigncls "
							+ "						WHERE classid = bm_data.pk_bm_class "
							+ "	)"
							+ "	AND bm_assigncls.pk_org = bm_data.pk_transferorg) as pk_bm_class,pk_bm_data from bm_data ");
			sqlBuffer.append(" where pk_psndoc in(" + inSql.getInSQL(vos, "pk_psndoc") + ") and cyear||cperiod <= ? ");
			param.addParam(loginContext.getCyear() + loginContext.getCperiod());
		} else if (IHRBmfileConstant.ACTION_TRNOUTCANCEL.equals(operate)) {
			sqlBuffer
					.append("select pk_psndoc,bm_data.pk_bm_class,pk_bm_data from bm_data "
							+ "inner join bm_periodstate on bm_data.pk_bm_class = bm_periodstate.pk_bm_class and bm_periodstate.checkflag='N' "
							+ "inner join bm_period on bm_periodstate.pk_bm_period = bm_period.pk_bm_period "
							+ "and bm_period.year || bm_period.period = ?");
			sqlBuffer.append(" where pk_psndoc in(" + inSql.getInSQL(vos, "pk_psndoc") + ") and cyear||cperiod <= ? ");
			param.addParam(loginContext.getCyear() + loginContext.getCperiod());
		} else {
			sqlBuffer.append("select pk_psndoc,pk_bm_class,pk_bm_data from bm_data ");
			sqlBuffer.append(" where pk_psndoc in(" + inSql.getInSQL(vos, "pk_psndoc") + ") and cyear||cperiod = ? ");
		}

		if (IHRBmfileConstant.ACTION_TRANSFERIN.equals(operate)) {
			sqlBuffer.append(" and accountState = 3 and pk_transferorg = ? ");
		} else if (IHRBmfileConstant.ACTION_TRNOUTCANCEL.equals(operate)) {
			sqlBuffer.append(" and bm_data.pk_org = ? and bm_data.checkflag = 'N' ");
			sqlBuffer.append(" and accountState = 3 ");
		} else if (IHRBmfileConstant.ACTION_UNSEAL.equals(operate)) {
			sqlBuffer.append(" and bm_data.pk_org = ? and bm_data.checkflag = 'N' and accountState = 1 ");
			// 20151223 xiejie3 NCdp205564146
			// 已封存/已注销的人员在社保档案下课单个删除，而进行批量删除时，却删除不了
			// 测试和需求要求批量删除可以删除账户状态类型为 “正常”、“封存”、“注销”
		} else if (IHRBmfileConstant.ACTION_BATCHDEL.equals(operate)) {
			sqlBuffer.append(" and bm_data.pk_org = ?   and bm_data.checkflag = 'N' ");// 批量删除时，展现“正常”、“封存”、“注销”
			sqlBuffer.append(" and  accountstate in ( 0,1,2) ");
			// end
		} else {
			sqlBuffer.append(" and bm_data.pk_org = ? and bm_data.checkflag = 'N' and accountState = 0 ");
		}
		sqlBuffer.append(" order by pk_psndoc ");
		param.addParam(loginContext.getCyear() + loginContext.getCperiod());
		param.addParam(loginContext.getPk_org());
		BmDataVO[] bmDatas = executeQueryVOs(sqlBuffer.toString(), param, BmDataVO.class);
		inSql.clear();
		if (bmDatas == null) {
			return vos;
		}
		for (int j = 0; j < vos.length; j++) {
			for (int i = 0; i < bmDatas.length; i++) {
				if (bmDatas[i].getPk_psndoc().equals(vos[j].getPk_psndoc()) && bmDatas[i].getPk_bm_class() != null) {
					vos[j].setAttributeValue(bmDatas[i].getPk_bm_class(), UFBoolean.TRUE);
					vos[j].setAttributeValue("pk" + bmDatas[i].getPk_bm_class(), bmDatas[i].getPk_bm_data());
				}
			}
		}
		return vos;
	}

	/**
	 * 为薪资档案同步财务组织与成本中心
	 * 
	 * @throws BusinessException
	 */
	public void synFiAndCostOrg(HashMap<String, BmDataVO[]> map, String[] pkbmCls) throws BusinessException {
		// 如果数据库是oracle

		InSQLCreator inC = null;
		if (ArrayUtils.isEmpty(pkbmCls)) {
			return;
		}
		boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_RESA);
		String[] sql = null;
		String[] sql2 = null;
		for (String pk_bm_class : pkbmCls) {
			BmDataVO[] addPsn = map.get(pk_bm_class);
			if (ArrayUtils.isEmpty(addPsn)) {
				continue;
			}
			String cyear = addPsn[0].getCyear();
			String cperiod = addPsn[0].getCperiod();

			String pk_hrorg = addPsn[0].getPk_org();

			inC = new InSQLCreator();
			StringBuilder sbd = new StringBuilder();
			StringBuilder sbd2 = new StringBuilder();
			if (getDBType() == DBConsts.ORACLE || getDBType() == DBConsts.DB2) {

				sbd.append("update bm_data set (pk_financeorg,pk_financedept,fiporgvid,fipdeptvid)=( ");
				sbd.append(" select ( case   when  org.orgtype5 = 'Y' then  org.pk_org   else org.pk_corp end )  pk_financeorg,  ");
				sbd.append(" ( case when  org.orgtype5 = 'Y' then  job.pk_dept  else '' end )  pk_financedept,  ");
				sbd.append(" ( case when  org.orgtype5 = 'Y' then  org.pk_vid  else corp.pk_vid  end )  fiporgvid,  ");
				sbd.append(" ( case when  org.orgtype5 = 'Y' then  dept.pk_vid  else ''  end )  fipdeptvid  ");
				sbd.append(" from org_orgs org   ");
				sbd.append(" inner join hi_psnjob job on org.pk_org=job.pk_org   ");
				sbd.append(" inner join org_corp corp on corp.pk_corp = org.pk_corp   ");
				sbd.append(" inner join org_dept dept on dept.pk_dept = job.pk_dept   ");

				sbd.append(" where job.pk_psnjob = bm_data.pk_psnjob ");
				sbd.append(" )  where bm_data.pk_psnjob in (" + inC.getInSQL(addPsn, BmDataVO.PK_PSNJOB)
						+ ")  and  bm_data.pk_org = '" + pk_hrorg + "' and bm_data.cyear = '" + cyear
						+ "' and bm_data.cperiod  = '" + cperiod + "' ");

				sbd2.append("update bm_data set (pk_liabilityorg,pk_liabilitydept,libdeptvid)=( ");
				sbd2.append(" select   resa_costcenter.pk_costcenter AS pk_liabilityorg ,"
						+ " job.pk_dept  AS pk_liabilitydept ," + "   dept.pk_vid  AS libdeptvid ");
				sbd2.append(" from resa_costcenter  "
						+ " inner join resa_ccdepts on  resa_ccdepts.pk_costcenter = resa_costcenter.pk_costcenter "
						+ " inner join hi_psnjob job on  job.pk_dept = resa_ccdepts.pk_dept   ");
				sbd2.append(" inner join org_dept dept on dept.pk_dept = job.pk_dept   ");
				sbd2.append(" where job.pk_psnjob = bm_data.pk_psnjob and  resa_costcenter.enablestate = '2'  ");
				sbd2.append(" )  where bm_data.pk_psnjob in (" + inC.getInSQL(addPsn, BmDataVO.PK_PSNJOB)
						+ ")  and  bm_data.pk_org = '" + pk_hrorg + "'  and bm_data.cyear = '" + cyear
						+ "' and bm_data.cperiod  = '" + cperiod + "' ");

			} else {
				sbd.append("update bm_data set    bm_data.pk_financeorg = ( case   when  org.orgtype5 = 'Y' then  org.pk_org   else org.pk_corp end )  , ");
				sbd.append(" bm_data.pk_financedept=  ( case   when  org.orgtype5 = 'Y' then  job.pk_dept  else '' end )  , ");
				sbd.append(" bm_data.fiporgvid=  ( case   when  org.orgtype5 = 'Y' then  org.pk_vid  else corp.pk_vid  end )  , ");
				sbd.append(" bm_data.fipdeptvid= ( case   when  org.orgtype5 = 'Y' then  dept.pk_vid  else ''  end )   ");
				sbd.append(" from org_orgs org  ");
				sbd.append(" inner join hi_psnjob job on org.pk_org=job.pk_org  ");
				sbd.append(" inner join org_corp corp on corp.pk_corp = org.pk_corp   ");
				sbd.append(" inner join org_dept dept on dept.pk_dept = job.pk_dept ");
				sbd.append(" inner join bm_data  on  job.pk_psnjob = bm_data.pk_psnjob ");
				sbd.append("  where bm_data.pk_psnjob in (" + inC.getInSQL(addPsn, BmDataVO.PK_PSNJOB)
						+ ")  and  bm_data.pk_org = '" + pk_hrorg + "'  and bm_data.cyear = '" + cyear
						+ "' and bm_data.cperiod  = '" + cperiod + "' ");

				sbd2.append("update bm_data set    bm_data.pk_liabilityorg = ( resa_costcenter.pk_costcenter )  , ");
				sbd2.append(" bm_data.pk_liabilitydept=  ( job.pk_dept )  , ");
				sbd2.append(" bm_data.libdeptvid=  ( dept.pk_vid )   ");
				sbd2.append(" from resa_costcenter  "
						+ " inner join resa_ccdepts on  resa_ccdepts.pk_costcenter = resa_costcenter.pk_costcenter "
						+ " inner join hi_psnjob job on  job.pk_dept = resa_ccdepts.pk_dept   ");
				sbd2.append(" inner join org_dept dept on dept.pk_dept = job.pk_dept   ");
				sbd2.append(" inner join bm_data  on  job.pk_psnjob = bm_data.pk_psnjob  ");
				sbd2.append("  where  resa_costcenter.enablestate = '2'  and bm_data.pk_psnjob in ("
						+ inC.getInSQL(addPsn, BmDataVO.PK_PSNJOB) + ")  and  bm_data.pk_org = '" + pk_hrorg
						+ "'  and bm_data.cyear = '" + cyear + "' and bm_data.cperiod  = '" + cperiod + "' ");

			}
			sbd.append(" and bm_data.pk_bm_class='" + pk_bm_class + "'");
			sbd2.append(" and bm_data.pk_bm_class='" + pk_bm_class + "'");
			sql = (String[]) ArrayUtils.add(sql, sbd.toString());
			if (isEnable)
				sql2 = (String[]) ArrayUtils.add(sql2, sbd2.toString());
		}
		PersistenceDAO persistenceDAO = new PersistenceDAO();
		persistenceDAO.executeSQLs(sql);
		if (isEnable) {
			persistenceDAO.executeSQLs(sql2);
		}
	}

	// /**
	// * 为薪资档案同步财务组织与成本中心
	// * @throws BusinessException
	// */
	// public void synFiAndCostOrg(BmDataVO[] addPsn,BmClassVO bmClass) throws
	// BusinessException
	// {
	// // 如果数据库是oracle
	// StringBuilder sbd = new StringBuilder();
	// StringBuilder sbd2 = new StringBuilder();
	//
	// InSQLCreator inC = null;
	// if (ArrayUtils.isEmpty(addPsn))
	// {
	// return;
	// }
	//
	// String cyear = addPsn[0].getCyear();
	// String cperiod = addPsn[0].getCperiod();
	//
	// String pk_hrorg = addPsn[0].getPk_org();
	// try
	// {
	// inC = new InSQLCreator();
	//
	// if(getDBType()==DBConsts.ORACLE || getDBType()==DBConsts.DB2 ){
	//
	//
	// sbd.append("	update bm_data    set (pk_financeorg,pk_financedept,fiporgvid,fipdeptvid)=( ");
	// sbd.append("				select    ( case   when  org.orgtype5 = 'Y' then  org.pk_org   else org.pk_corp end )  pk_financeorg,  ");
	// sbd.append("			  ( case   when  org.orgtype5 = 'Y' then  job.pk_dept  else '' end )  pk_financedept,  ");
	// sbd.append("			 ( case   when  org.orgtype5 = 'Y' then  org.pk_vid  else corp.pk_vid  end )  fiporgvid,  ");
	// sbd.append("			 ( case   when  org.orgtype5 = 'Y' then  dept.pk_vid  else ''  end )  fipdeptvid  ");
	// sbd.append("			  from org_orgs org   ");
	// sbd.append("							 inner join hi_psnjob job on org.pk_org=job.pk_org   ");
	// sbd.append("							 inner join org_corp corp on corp.pk_corp = org.pk_corp   ");
	// sbd.append("			 inner join org_dept dept on dept.pk_dept = job.pk_dept   ");
	//
	// sbd.append("			 where job.pk_psnjob = bm_data.pk_psnjob ");
	// sbd.append("			)  where bm_data.pk_psnjob in (" + inC.getInSQL(addPsn,
	// BmDataVO.PK_PSNJOB) + ")  and  bm_data.pk_org = '"
	// + pk_hrorg + "' and bm_data.cyear = '" + cyear +
	// "' and bm_data.cperiod  = '" + cperiod + "' ");
	//
	// sbd2.append("   update bm_data    set (pk_liabilityorg,pk_liabilitydept,libdeptvid)=( ");
	// sbd2.append("                select   resa_costcenter.pk_costcenter AS pk_liabilityorg ,"
	// + "   job.pk_dept  AS pk_liabilitydept ," +
	// "   dept.pk_vid  AS libdeptvid ");
	// sbd2.append("              from resa_costcenter  "
	// +
	// "              inner join resa_ccdepts on  resa_ccdepts.pk_costcenter = resa_costcenter.pk_costcenter "
	// +
	// "              inner join hi_psnjob job on  job.pk_dept = resa_ccdepts.pk_dept   ");
	// sbd2.append("              inner join org_dept dept on dept.pk_dept = job.pk_dept   ");
	// sbd2.append("             where job.pk_psnjob = bm_data.pk_psnjob and  resa_costcenter.enablestate = '2'  ");
	// sbd2.append("            )  where bm_data.pk_psnjob in (" +
	// inC.getInSQL(addPsn, BmDataVO.PK_PSNJOB)
	// + ")  and  bm_data.pk_org = '" + pk_hrorg + "'  and bm_data.cyear = '" +
	// cyear + "' and bm_data.cperiod  = '" + cperiod
	// + "' ");
	//
	//
	// }
	// else
	// {
	//
	// sbd.append("		update bm_data set    bm_data.pk_financeorg = ( case   when  org.orgtype5 = 'Y' then  org.pk_org   else org.pk_corp end )  , ");
	// sbd.append("			bm_data.pk_financedept=  ( case   when  org.orgtype5 = 'Y' then  job.pk_dept  else '' end )  , ");
	// sbd.append("		bm_data.fiporgvid=  ( case   when  org.orgtype5 = 'Y' then  org.pk_vid  else corp.pk_vid  end )  , ");
	// sbd.append("		 bm_data.fipdeptvid= ( case   when  org.orgtype5 = 'Y' then  dept.pk_vid  else ''  end )   ");
	// sbd.append("			  from org_orgs org  ");
	// sbd.append("							 inner join hi_psnjob job on org.pk_org=job.pk_org  ");
	// sbd.append("							 inner join org_corp corp on corp.pk_corp = org.pk_corp   ");
	// sbd.append("							 inner join org_dept dept on dept.pk_dept = job.pk_dept ");
	// sbd.append("			    inner join bm_data  on  job.pk_psnjob = bm_data.pk_psnjob ");
	// sbd.append("  where bm_data.pk_psnjob in (" + inC.getInSQL(addPsn,
	// BmDataVO.PK_PSNJOB) + ")  and  bm_data.pk_org = '"
	// + pk_hrorg + "'  and bm_data.cyear = '" + cyear +
	// "' and bm_data.cperiod  = '" + cperiod + "' ");
	//
	// sbd2.append("      update bm_data set    bm_data.pk_liabilityorg = ( resa_costcenter.pk_costcenter )  , ");
	// sbd2.append("            bm_data.pk_liabilitydept=  ( job.pk_dept )  , ");
	// sbd2.append("        bm_data.libdeptvid=  ( dept.pk_vid )   ");
	// sbd2.append("        from resa_costcenter  "
	// +
	// "           inner join resa_ccdepts on  resa_ccdepts.pk_costcenter = resa_costcenter.pk_costcenter "
	// +
	// "           inner join hi_psnjob job on  job.pk_dept = resa_ccdepts.pk_dept   ");
	// sbd2.append("        inner join org_dept dept on dept.pk_dept = job.pk_dept   ");
	// sbd2.append("        inner join bm_data  on  job.pk_psnjob = bm_data.pk_psnjob  ");
	// sbd2.append("  where  resa_costcenter.enablestate = '2'  and bm_data.pk_psnjob in ("
	// + inC.getInSQL(addPsn, BmDataVO.PK_PSNJOB) + ")  and  bm_data.pk_org = '"
	// + pk_hrorg + "'  and bm_data.cyear = '"
	// + cyear + "' and bm_data.cperiod  = '" + cperiod + "' ");
	//
	// }
	//
	// if(null!=bmClass && null!=bmClass.getPk_bm_class()){
	// sbd.append(" and bm_data.pk_bm_class='"+bmClass.getPk_bm_class()+"'");
	// sbd2.append(" and bm_data.pk_bm_class='"+bmClass.getPk_bm_class()+"'");
	// }
	//
	//
	// getBaseDao().executeUpdate(sbd.toString());
	//
	// boolean isEnable = PubEnv.isModuleStarted(PubEnv.getPk_group(),
	// PubEnv.MODULE_RESA);
	// if (isEnable)
	// {
	// getBaseDao().executeUpdate(sbd2.toString());
	// }
	//
	// }
	// finally
	// {
	// inC.clear();
	// }
	//
	// }

	/**
	 * 查看是否该期间下，是否在其他组织已经存在档案信息
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] checkRepeatBmClass(BmDataVO[] vos, String tablename) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		BmDataVO[] results = null;
		StringBuffer sbd = new StringBuffer();
		String cyear = vos[0].getCyear();
		String cperiod = vos[0].getCperiod();
		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			executeQueryVOs("select * from " + tablename, BmDataVO.class);
			sbd.append("select " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + " orgname, "
					+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + " psnname, "
					+ SQLHelper.getMultiLangNameColumn("bm_bmclass.name") + " classname ");
			sbd.append(" from bm_data ");
			sbd.append(" inner join org_orgs on bm_data.pk_org = org_orgs.pk_org ");
			sbd.append(" inner join bd_psndoc on bm_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sbd.append(" inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class ");
			sbd.append(" inner join " + tablename + " on bm_data.pk_psndoc = " + tablename + ".pk_psndoc ");
			sbd.append(" where " + "  bm_data.cyear = '" + cyear + "' and bm_data.cperiod = '" + cperiod
					+ "' and bm_data.pk_bm_class in (select classid from bm_assigncls where pk_sourcecls = "
					+ "(select pk_sourcecls from bm_assigncls where classid = " + tablename + ".pk_bm_class))");

			sbd.append(" and accountstate <> 3 ");

			results = executeQueryVOs(sbd.toString(), BmDataVO.class);
			if (ArrayUtils.isEmpty(results)) {
				return null;
			}
		} finally {
			inSQLCreator.clear();
		}

		return results;
	}

	/**
	 * 查看是否该期间下，是否在其他组织已经存在档案信息
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public BmDataVO[] checkTransfOutBmClass(BmDataVO[] vos, String tablename) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		BmDataVO[] results = null;
		StringBuffer sbd = new StringBuffer();

		InSQLCreator inSQLCreator = new InSQLCreator();
		try {

			sbd.append("select " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + " orgname, "
					+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + " psnname, "
					+ SQLHelper.getMultiLangNameColumn("bm_bmclass.name") + " classname,bm_data.cyear,bm_data.cperiod ");
			sbd.append(" from bm_data ");
			sbd.append(" inner join org_orgs on bm_data.pk_org = org_orgs.pk_org ");
			sbd.append(" inner join bd_psndoc on bm_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sbd.append(" inner join bm_bmclass on bm_data.pk_bm_class = bm_bmclass.pk_bm_class ");
			sbd.append(" inner join " + tablename + " on bm_data.pk_psndoc = " + tablename + ".pk_psndoc ");
			sbd.append(" where " + " bm_data.pk_bm_class in (select classid from bm_assigncls where pk_sourcecls = "
					+ "(select pk_sourcecls from bm_assigncls where classid = " + tablename + ".pk_bm_class))");
			sbd.append(" and accountstate = 3");

			results = executeQueryVOs(sbd.toString(), BmDataVO.class);
			if (ArrayUtils.isEmpty(results)) {
				return null;
			}
		} finally {
			inSQLCreator.clear();
		}

		return results;
	}

	/**
	 * 通过classid查询集团分配险种信息
	 * 
	 * @param classInfo
	 * @return
	 * @throws BusinessException
	 */
	public AssignclsVO[] queryAssignclsVOsByClassVOs(BmDataVO[] classInfo) throws BusinessException {
		String[] pks = new String[classInfo.length];
		for (int i = 0; i < classInfo.length; i++) {
			pks[i] = classInfo[i].getPk_bm_class();
		}
		String classPkStr = "";
		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			classPkStr = inSQLCreator.getInSQL(pks);
		} finally {
			inSQLCreator.clear();
		}
		return retrieveByClause(AssignclsVO.class, " classid in (" + classPkStr + ")");
	}

	// 20150831 xiejie3 补丁合并NCdp205306564人员变动社保档案人员变动不能做转出 begin
	// shenliangc
	/**
	 * 根据变动人员面板上选中的人员基本信息主键和变动起止时间 查询变动人员在起止时间段内最新工作记录主键。
	 * 
	 * @param psnDocPks
	 * @param beginLDate
	 * @param endLDate
	 * @return String[]
	 * @throws BusinessException
	 */
	public String[] queryCurrentPkPsnJob(String[] psnDocPks, UFLiteralDate beginLDate, UFLiteralDate endLDate)
			throws BusinessException {
		InSQLCreator inSQLCreator = new InSQLCreator();
		String psnDocPksStr = inSQLCreator.getInSQL(psnDocPks);
		String sql = "select pk_psnjob from hi_psnjob where pk_psndoc in (" + psnDocPksStr + ")"
				+ " and begindate >= '" + beginLDate + "' " + " and ((endflag = 'Y' and enddate <= '" + endLDate
				+ "') or (endflag = 'N'))";
		PsnTrnVO[] psnTrnVOs = this.executeQueryVOs(sql, PsnTrnVO.class);
		ArrayList<String> pkList = new ArrayList<String>();
		if (!ArrayUtils.isEmpty(psnTrnVOs)) {
			for (int i = 0; i < psnTrnVOs.length; i++) {
				pkList.add(psnTrnVOs[i].getPk_psnjob());
			}
		}
		return pkList.toArray(new String[0]);
	}

	// end

	// MOD {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）}
	// kevin.nie
	// 2018-01-17
	// start
	public BmDataVO[] queryBmForEdit(BmLoginContext loginContext, String condition) throws BusinessException {
		BmDataVO[] resultVOs = null;
		if (null != loginContext) {
			StringBuilder sql = new StringBuilder();
			sql.append("select bd_psndoc.code as psncode,bd_psndoc.name as psnname,bd_psnidtype.name as idtype,bd_psndoc.id as id, ");
			sql.append("org_orgs.name as orgname,org_dept.name as deptname,bd_psncl.name as psnclname,cp.name as agentcomp,bm_data.bmaccountno as accno, ");
			sql.append("bm_bmclass.code as classcode,bm_bmclass.name as classname,bm_locationrule.name as paylocation,me1.name as accstate ");
			sql.append(",me2.name as acctype,vcp1.name as vendercomp,vcp2.name as venderpsn ");
			sql.append(",bm_data.pk_bm_data,bm_data.pk_psndoc,bm_data.pk_psnjob,bm_data.pk_group,bm_data.pk_org,bm_data.pk_bm_class ");
			sql.append("from bm_data ");
			sql.append("inner join bd_psndoc on bd_psndoc.pk_psndoc=bm_data.pk_psndoc ");
			sql.append("inner join hi_psnjob on hi_psnjob.pk_psnjob=bm_data.pk_psnjob ");
			sql.append("inner join bm_bmclass on bm_bmclass.pk_bm_class=bm_data.pk_bm_class ");
			sql.append("left join bd_psnidtype on bd_psndoc.idtype=bd_psnidtype.pk_identitype ");
			sql.append("left join org_orgs on org_orgs.pk_org=bd_psndoc.pk_org ");
			sql.append("left join org_dept on org_dept.pk_dept=hi_psnjob.pk_dept ");
			sql.append("left join bd_psncl on bd_psncl.pk_psncl=hi_psnjob.pk_psncl ");
			sql.append("left join bm_locationrule on bm_locationrule.pk_loctrule=bm_data.paylocation ");
			sql.append("left join md_enumvalue me1 on me1.value=bm_data.accountstate and me1.id='47bbe687-4f52-4434-a2a3-4abcb04451a3' ");
			sql.append("left join md_enumvalue me2 on me2.value=bm_data.accounttype and me2.id='641bb399-f57f-4305-aa59-1969a219547c' ");
			sql.append("left join (select bd_defdoc.* from bd_defdoc,bd_defdoclist where bd_defdoc.pk_defdoclist=bd_defdoclist.pk_defdoclist and bd_defdoclist.code='HRAL005') cp on cp.pk_defdoc=bm_data.def1 ");
			sql.append("left join (select bd_defdoc.* from bd_defdoc,bd_defdoclist where bd_defdoc.pk_defdoclist=bd_defdoclist.pk_defdoclist and bd_defdoclist.code='HRAL006') vcp1 on vcp1.pk_defdoc=bm_data.def2 ");
			sql.append("left join (select bd_defdoc.* from bd_defdoc,bd_defdoclist where bd_defdoc.pk_defdoclist=bd_defdoclist.pk_defdoclist and bd_defdoclist.code='HRAL006') vcp2 on vcp2.pk_defdoc=bm_data.def3 ");
			sql.append("where bm_data.pk_group=? and bm_data.pk_org=? and bm_data.cyear=? and bm_data.cperiod=? ");
			if (StringUtils.isNotBlank(condition)) {
				sql.append(" and ").append(condition);
			}
			sql.append(" order by bm_data.pk_psndoc,bm_data.pk_psnjob,bm_data.pk_bm_class ");

			SQLParameter params = new SQLParameter();
			params.addParam(loginContext.getPk_group());
			params.addParam(loginContext.getPk_org());
			params.addParam(loginContext.getCyear());
			params.addParam(loginContext.getCperiod());

			resultVOs = executeQueryVOs(sql.toString(), params, BmDataVO.class);
		}
		return resultVOs;
	}
	// {社保档案批量新增、修改社保代理公司、险种社保供应商代码（公司）、险种社保供应商代码（个人）} kevin.nie
	// 2018-01-17
	// end
}
