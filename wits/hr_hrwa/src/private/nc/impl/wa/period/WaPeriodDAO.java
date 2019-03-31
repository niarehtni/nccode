package nc.impl.wa.period;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import nc.bd.accperiod.AccperiodParamAccessor;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.category.WaClassDAO;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.accperiod.AccountCalendar;
import nc.vo.bd.period.AccperiodVO;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.period.WaClassViewVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 薪资期间的DAO类
 * 
 * @author: liangxr
 * @date: 2009-11-11 上午10:21:38
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaPeriodDAO extends BaseDAOManager {
	
	

	private static IMDPersistenceQueryService getMDQueryService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}
	



	/**
	 * 根据方案ID查询薪资期间
	 * 
	 * @author liangxr on 2009-11-12
	 * @param periodschemePK
	 * @return
	 */
	public PeriodVO[] queryBySchemePK(String periodschemePK) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select pk_wa_period, pk_periodscheme, cyear, cperiod, cstartdate, cenddate,");
// {MOD:新个税补丁}
// begin
// sqlB.append("caccyear, caccperiod from wa_period where pk_periodscheme = ? order by cyear, cperiod");
		sqlB.append("caccyear, caccperiod , taxyear, taxperiod from wa_period where pk_periodscheme = ? order by cyear, cperiod");
// end
		SQLParameter param = new SQLParameter();
		param.addParam(periodschemePK);

		return executeQueryVOs(sqlB.toString(), param, PeriodVO.class);

	}

	/**
	 * 根据 薪资方案与薪资期间B 查询B以后的薪资期间
	 * 
	 * @param periodschemePK
	 * @param curPeriod
	 * @return
	 * @throws SQLException
	 * @see
	 */
	public PeriodVO[] queryByChemePKAndStartDate(String periodschemePK, String curPeriod) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select pk_wa_period, pk_periodscheme, cyear, cperiod, cstartdate, cenddate,");
// {MOD：新个税补丁}
// begin
// sqlB.append("caccyear, caccperiod");
		sqlB.append("caccyear, caccperiod , taxyear, taxperiod  ");
// end
		sqlB
				.append(" from wa_period where cyear||cperiod>=? and pk_periodscheme = ? order by cyear, cperiod");
//		sqlB.append("order by cyear||cperiod");
		SQLParameter param = new SQLParameter();
		param.addParam(curPeriod);
		param.addParam(periodschemePK);

		return executeQueryVOs(sqlB.toString(), param, PeriodVO.class);

	}

	
	/**
	 * 根据 薪资方案与薪资期间B 查询B以后的薪资期间
	 * 
	 * @param periodschemePK
	 * @param curPeriod
	 * @return
	 * @throws SQLException
	 * @see
	 */
	public PeriodVO[] queryByChemePKAndDate(String periodschemePK, String cyear ,String cperiod) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select pk_wa_period, pk_periodscheme, cyear, cperiod, cstartdate, cenddate,");
// {MOD:新个税补丁}
// begin
// sqlB.append("caccyear, caccperiod");
		sqlB.append("caccyear, caccperiod , taxyear, taxperiod  ");
// end
		sqlB
				.append(" from wa_period where cyear=? and cperiod= ?  and pk_periodscheme = ?");

		SQLParameter param = new SQLParameter();
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(periodschemePK);

		return executeQueryVOs(sqlB.toString(), param, PeriodVO.class);

	}
	
	
	/**
	 * 根据期间方案 ,薪资年度,薪资期间 查询 薪资期间的开始日期与结束日期 21<Strong>
	 * 
	 * @param scheme
	 * @param waYear
	 * @param waPeriod
	 * @return
	 * @throws SQLException
	 * @see
	 */
	public PeriodVO queryBySchemeYP(String scheme, String waYear, String waPeriod) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
// {MOD:新个税补丁}
// begin
//sqlB.append("select pk_wa_period ,cstartdate,cenddate,cyear,cperiod");
		sqlB.append("select pk_wa_period ,cstartdate,cenddate,cyear,cperiod , taxyear, taxperiod  ");
// end
		sqlB
				.append(" from wa_period where pk_periodscheme = ? and cyear = ?  and cperiod = ?  order by cyear , cperiod");

		SQLParameter param = new SQLParameter();
		param.addParam(scheme);
		param.addParam(waYear);
		param.addParam(waPeriod);

		return executeQueryVO(sqlB.toString(), param, PeriodVO.class);

	}

	/**
	 * 查询某薪资方案的所有薪资期间
	 * 
	 * @author liangxr on 2010-1-18
	 * @param pk_wa_class
	 * @param waYear
	 * @param waPeriod
	 * @return
	 * @throws DAOException
	 */
	public PeriodVO[] queryByWaClass(String pk_wa_class) throws DAOException {
// {MOD：新个税补丁}
// begin
//String sql = "select wa_period.pk_wa_period, wa_period.cstartdate, wa_period.cenddate, wa_period.cyear, wa_period.cperiod from wa_period "
		String sql = "select wa_period.pk_wa_period, wa_period.cstartdate, wa_period.cenddate, wa_period.cyear, wa_period.cperiod,wa_period.taxyear, wa_period.taxperiod  from wa_period "
// end
				+ "inner join wa_waclass on wa_waclass.pk_periodscheme = wa_period.pk_periodscheme "
				+ " where wa_waclass.pk_wa_class = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);

		return executeQueryVOs(sql, param, PeriodVO.class);
	}

	/**
	 * 查询某薪资方案的所有薪资期间
	 * 
	 * @author xuanlt on 2010-1-18
	 * @param pk_wa_class
	 * @param waYear
	 * @param waPeriod
	 * @return
	 * @throws DAOException
	 */
	public PeriodVO[] queryByWaClass(String pk_wa_class, String condition) throws DAOException {
// {新个税补丁}
// begin
//	String sql = "select wa_period.pk_wa_period, wa_period.cstartdate, wa_period.cenddate, wa_period.cyear, wa_period.cperiod from wa_period "
		String sql = "select wa_period.pk_wa_period, wa_period.cstartdate, wa_period.cenddate, wa_period.cyear, wa_period.cperiod, wa_period.cperiod,wa_period.taxyear from wa_period "
// end
				+ "inner join wa_waclass on wa_waclass.pk_periodscheme = wa_period.pk_periodscheme "
				+ "inner join wa_periodstate   on wa_periodstate.pk_wa_period = wa_period.pk_wa_period and wa_periodstate.pk_wa_class = wa_waclass.pk_wa_class"
				+ " where wa_waclass.pk_wa_class = ? ";
		if (!StringUtils.isBlank(condition)) {
			sql = sql + " and " + condition;
		}

		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);

		return executeQueryVOs(sql, param, PeriodVO.class);
	}

	/**
	 * 查询关联该期间的所有薪资方案
	 * 
	 * @author liangxr on 2009-12-3
	 * @param periodChemmePK
	 * @param sqlWhere
	 * @return
	 */
	public WaClassViewVO[] queryWaClassByCheme(String periodChemmePK, String sqlWhere) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB
				.append("select wa_waclass.pk_wa_class, "+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")+ " as name , wa_waclass.pk_org,wa_waclass.pk_group,   "+ SQLHelper.getMultiLangNameColumn("org_orgs.name")+ "   as orgname,");
		sqlB.append(" wa_waclass.cyear, wa_waclass.cperiod, wa_waclass.startyear, wa_waclass.startperiod");
		sqlB.append(" from wa_waclass left outer join org_orgs on org_orgs.pk_org = wa_waclass.pk_org ");
		sqlB.append("where wa_waclass.pk_periodscheme = ? ");

		if (sqlWhere != null) {
			sqlB.append("and ").append(sqlWhere);
		}

		SQLParameter param = new SQLParameter();
		param.addParam(periodChemmePK);

		return executeQueryVOs(sqlB.toString(), param, WaClassViewVO.class);

	}

	/**
	 * 查询薪资期间是否被引用
	 * 
	 * @author liangxr on 2010-5-27
	 * @param pks
	 * @return
	 * @throws DAOException
	 */
	public boolean isPeriodRefs(String pks) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		// 2015-08-13 zhousze 解决期间方案中的没有被引用的期间应该是可以修改的 begin
		sqlB.append(" select 1 from wa_periodstate where enableflag = 'Y' and pk_wa_period in(");
		// end
		if (!pks.startsWith("'")) {
			sqlB.append("'");
		}
		sqlB.append(pks);
		if (!pks.endsWith("'")) {
			sqlB.append("'");
		}
		sqlB.append(")");

		return isValueExist(sqlB.toString());
	}

	/**
	 * 查询薪资期间方案是否被引用（已审核的薪资方案）
	 * 
	 * @author liangxr on 2010-5-27
	 * @param pks
	 * @return
	 * @throws DAOException
	 */
	public boolean isPeriodSchemeRef(String pks) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append(" select 1 from wa_periodstate ps" );
		sqlB.append(" inner join wa_period p on ps.pk_wa_period =p.pk_wa_period" );
		sqlB.append(" inner join wa_periodscheme pc on p.pk_periodscheme = pc.pk_periodscheme " );
		sqlB.append(" where pc.pk_periodscheme = '"+pks+"' and ps.checkflag='Y'");

		return isValueExist(sqlB.toString());
	}
	/**
	 * 按条件查询薪资年度
	 * 
	 * @param conditon
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public String[] queryCyears(String conditon) throws DAOException {
		String sql = " select distinct cyear from wa_period where pk_periodscheme in ("
				+ " select distinct pk_periodscheme from wa_waclass where cyear is not null "
				+ ((conditon == null || conditon.equals("")) ? "" : (" and " + conditon)) + ") ";
		sql += " order by cyear desc ";
		BaseDAO dao = new BaseDAO();
		ArrayList<String> result = (ArrayList<String>) dao.executeQuery(sql, new ColumnListProcessor());
		return result.toArray(new String[0]);
	}

	/**
	 * 按条件查询薪资期间
	 * 
	 * @param conditon
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public String[] queryCperiods(String conditon) throws DAOException {
		String sql = " select distinct cperiod from wa_period where pk_periodscheme in ("
				+ " select distinct pk_periodscheme from wa_waclass where cperiod is not null "
				+ ((conditon == null || conditon.equals("")) ? "" : (" and " + conditon)) + ") ";
		sql += " order by cperiod ";
		BaseDAO dao = new BaseDAO();
		ArrayList<String> result = (ArrayList<String>) dao.executeQuery(sql, new ColumnListProcessor());
		return result.toArray(new String[0]);
	}

	/**
	 * 查询期间方案中哪些期间未在薪资方案期间状态中
	 * @author liangxr on 2010-7-27 
	 * @param pk_periodscheme
	 * @param pk_wa_class
	 * @return
	 * @throws DAOException
	 */
	public PeriodVO[] queryPeriodNotInClass(String periodChemmePK,String pk_wa_class) throws DAOException {
		String sql = "select cyear,cperiod,pk_wa_period from wa_period where pk_periodscheme = ? and pk_wa_period not in (select pk_wa_period from wa_periodstate where pk_wa_class= ?)";
		SQLParameter param = new SQLParameter();
		param.addParam(periodChemmePK);
		param.addParam(pk_wa_class);

		return executeQueryVOs(sql, param, PeriodVO.class);
	}
	
	
	/**
	 * @param 获取所有会计期间
	 * @return
	 * @throws BusinessException
	 */
	public AccperiodVO[] getAllAccPeriod() throws BusinessException {
		
		//AccperiodVO[] accArrs = AccountCalendar.getInstance().getYearVOsOfCurrentScheme();
		String pk_accperiodscheme = AccperiodParamAccessor.getInstance().getDefaultSchemePk();
		Collection<AccperiodVO> result = getMDQueryService().queryBillOfVOByCond(
				AccperiodVO.class, AccperiodVO.PK_ACCPERIODSCHEME+"='"+pk_accperiodscheme+"' order by begindate", false);
		AccperiodVO[] accArrs = (result.toArray(new AccperiodVO[0]));
		
		if (ArrayUtils.isEmpty(accArrs)) {
			accArrs = AccountCalendar.getInstance().getYearVOsOfCurrentScheme();
		}

		return accArrs;
	}
	
	/**
	 * @param 获取所有会计期间月度
	 * @return
	 * @throws BusinessException
	 */
	public AccperiodmonthVO[] getAccperiodmonthVO(String pk_accperiod) throws BusinessException {

		
		Collection<AccperiodVO> result2 = getMDQueryService().queryBillOfVOByCond(
				AccperiodmonthVO.class, AccperiodmonthVO.PK_ACCPERIOD+"='"+pk_accperiod+"' and "+AccperiodmonthVO.ISADJ+"='N'", false);
		AccperiodmonthVO[] accperiodmonths = (result2.toArray(new AccperiodmonthVO[0]));
		if (ArrayUtils.isEmpty(accperiodmonths)) {
			accperiodmonths = AccountCalendar.getInstanceByAccperiod(pk_accperiod).getMonthVOsOfCurrentYear();
		}
		
		return accperiodmonths;
	}
	

	/**
	 * 根据薪资类别。查找最新的薪资期间
	 * @param pk_waclass
	 * @return
	 * @throws DAOException
	 * @return PeriodVO
	 */
	public PeriodVO queryNewWaPeriod(String pk_waclass) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select wa_period.*");
		sqlB.append(" from wa_period ,wa_periodstate,wa_waclass " );
		sqlB.append(" where  wa_waclass.pk_wa_class = ?" );
		sqlB.append(" and wa_period.pk_wa_period = wa_periodstate.pk_wa_period " );
		sqlB.append(" and wa_periodstate.pk_wa_class = wa_waclass.pk_wa_class " );
		sqlB.append(" and wa_period.cyear=wa_waclass.cyear" );
		sqlB.append(" and wa_period.cperiod=wa_waclass.cperiod");
		SQLParameter para = new SQLParameter();
		para.addParam(pk_waclass);


		return executeQueryVO(sqlB.toString(), para, PeriodVO.class);
 
	}
	
	
	public PeriodVO queryNewWaPeriod4TaxGroup(String pk_taxgroup) throws DAOException{
		WaClassVO vo = new WaClassDAO().queryWaClassByPkTaxgroup(pk_taxgroup);
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select wa_period.*");
		sqlB.append(" from wa_period ,wa_periodstate,wa_waclass " );
		sqlB.append(" where  wa_waclass.pk_wa_class = '" + vo.getPk_wa_class() + "' ");
		sqlB.append(" and wa_period.pk_wa_period = wa_periodstate.pk_wa_period " );
		sqlB.append(" and wa_periodstate.pk_wa_class = wa_waclass.pk_wa_class " );
		sqlB.append(" and wa_period.cyear=wa_waclass.cyear" );
		sqlB.append(" and wa_period.cperiod=wa_waclass.cperiod");

		return executeQueryVO(sqlB.toString(), PeriodVO.class);
	}
	
	/**
	 *
	 * @author xuanlt on 2010-4-30
	 * @param pk_waclass
	 * @param date
	 * @return
	 * @throws DAOException
	 * @return PeriodVO
	 */
	public PeriodVO queryWaPeriod(String pk_waclass, UFDate date) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB
				.append("select wa_period.* from wa_period ,wa_periodstate where wa_period.pk_wa_period = wa_periodstate.pk_wa_period and wa_periodstate.pk_wa_class = ? and  wa_period.cstartdate<=? and wa_period.cenddate>=?");
		SQLParameter para = new SQLParameter();
		para.addParam(pk_waclass);
		para.addParam(date.toStdString());
		para.addParam(date.toStdString());

		return executeQueryVO(sqlB.toString(), para, PeriodVO.class);

	}
	
	

}
