package nc.impl.wa.category;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.bs.uap.oid.OidGenerator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.frame.persistence.AppendBeanArrayProcessor;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.sm.UserVO;
import nc.vo.uif2.LoginContext;
import nc.vo.util.VisibleUtil;
import nc.vo.wa.category.AssignclsVO;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaFiorgVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.category.WaRangeConditon;
import nc.vo.wa.classpower.ClassPowerUtil;
import nc.vo.wa.grade.WaPsnhiBVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.periodsate.WaPeriodstateVO;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.WACLASSTYPE;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author: xuanlt
 * @date: 2009-11-19 上午11:28:55
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaClassDAO extends BaseDAOManager {

	/**
	 * 查询薪资方案 ，以及计薪规则
	 *
	 * @author xuanlt on 2010-4-12
	 * @param context
	 * @return
	 * @throws DAOException
	 * @return WaClassVO[]
	 */
	public WaClassVO[] queryWaClassByOrg(LoginContext context) throws DAOException {
		return queryWaClassByOrg(context, false);
	}

	public WaClassVO[] queryWaClassByOrg(LoginContext context, boolean withRangeRule) throws DAOException {
		return queryWaClassByOrg(context, withRangeRule, false);
	}

	public WaClassVO[] queryWaClassByOrgWtihStopclass(LoginContext context) throws DAOException {
		return queryWaClassByOrg(context, false, true);
	}

	public WaClassVO[] queryWaClassByOrg(LoginContext context, boolean withRangeRule, boolean withStopClass)
			throws DAOException {
		return queryWaClassByOrg(context, withRangeRule, withStopClass, "  wa_waclass.code ");
	}

	public WaClassVO[] queryWaClassByOrg(LoginContext context, boolean withRangeRule, boolean withStopClass,
			String orderby) throws DAOException {

		return queryWaClassByOrg(context, withRangeRule, withStopClass, true,"  wa_waclass.code ");

	}

	public WaClassVO[] queryWaClassByOrg(LoginContext context, boolean withRangeRule, boolean withStopClass,boolean onlyUserDef,
			String orderby) throws DAOException {

		if (context == null) {
			return new WaClassVO[0];
		}
		// get pay slip
		StringBuilder sqlB = new StringBuilder();
		//这个"*"先不要改了 . 因为就是所有的字段
		sqlB.append("select  wa_waclass.*,(case   when "+ SQLHelper.getNullSql("wa_assigncls.pk_assigncls")+"  then  wa_waclass.pk_org  else  wa_waclass.pk_group end)   as  pk_org   from wa_waclass   left outer join wa_assigncls   on wa_waclass.pk_wa_class = wa_assigncls.classid "); // 1

		sqlB.append(" where ");

		/**
		 * 薪资方案的可见性与UAP提供额可见性有区别 组织级节点只能看到本组织的，以及集团分配给本组织的。不能看到集团的
		 */
		String visibleConditon = " wa_waclass.pk_org =  '" + context.getPk_org() + "'"; // getVisibleCondition(context,
		// WaClassVO.class);
		if (!StringUtils.isBlank(visibleConditon)) {
			sqlB.append(visibleConditon);
		}

		if (!withStopClass) {
			if (sqlB.toString().trim().endsWith("where")) {
				sqlB.append(" ("+ SQLHelper.getEqualsWaveSql("wa_waclass.stopflag")+"  or wa_waclass.stopflag ='N')");
			} else {
				sqlB.append("  and  ( "+  SQLHelper.getEqualsWaveSql("wa_waclass.stopflag") +" or wa_waclass.stopflag ='N')");
			}
		}

		if(onlyUserDef){
			if (sqlB.toString().trim().endsWith("where")) {
				sqlB.append(" ( showflag = 'Y' )");
			} else {
				sqlB.append("  and  ( showflag = 'Y' )");
			}
		}

		if (!StringUtils.isBlank(orderby)) {
			sqlB.append("  order by " + orderby);
		}

		SQLParameter param = new SQLParameter();

		WaClassVO[] vos = executeQueryVOs(sqlB.toString(), param, WaClassVO.class);

		/**
		 * 为每一个薪资方案查询计薪规则
		 *
		 */
		if (withRangeRule && !ArrayUtils.isEmpty(vos)) {
			completeRange(vos);
		}

		return vos;
	}

	public WaClassVO queryGroupClassByOrgClass(String pk_waclass) throws BusinessException{
		String  selec = 	" select wa_waclass.* from wa_waclass ,wa_assigncls where  wa_waclass.pk_wa_class = wa_assigncls.pk_sourcecls and  wa_assigncls.classid = '"+ pk_waclass +"'";
		return executeQueryVO(selec, WaClassVO.class);
	}


	public WaClassVO queryWaclassBypk(String pk_waclass) throws DAOException{
		StringBuilder sqlB = new StringBuilder();
		//这个"*"先不要改了 . 因为就是所有的字段
		sqlB.append("select  wa_waclass.*,(case   when "+ SQLHelper.getNullSql("wa_assigncls.pk_assigncls")+"  then  wa_waclass.pk_org  else  wa_waclass.pk_group end)   as  pk_org   from wa_waclass   left outer join wa_assigncls   on wa_waclass.pk_wa_class = wa_assigncls.classid "); // 1
		sqlB.append(" where pk_wa_class = ?");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_waclass);

		WaClassVO[] vos = executeQueryVOs(sqlB.toString(), param, WaClassVO.class);
		if(vos==null||vos.length==0){
			return null;
		}
		completeRange(vos);
		return vos[0];
	}

	public Integer getBatchTimes(WaClassVO waclassvo) throws DAOException{
		String sql = " select isnull(max(batch)+1,1) from wa_inludeclass where pk_parentclass = '"+waclassvo.getPk_wa_class()+"' and cyear = '"+ waclassvo.getCyear() +"' and cperiod = '"+ waclassvo.getCperiod() +"' and batch < 100 ";

		Object o = 	 getBaseDao().executeQuery(sql, new ResultSetProcessor(){

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {

				rs.next();
				return rs.getInt(1);
			}

		});

		return (Integer)o;

	}

	/**
	 * 补充计薪规则与计薪范围
	 *
	 * @author xuanlt on 2010-4-15
	 * @param vo
	 * @return
	 * @return WaClassVO
	 * @throws DAOException
	 */
	public WaClassVO completeRange(WaClassVO vo) throws DAOException {
		WaPsnhiVO[] tempvos = queryRangRuleByClass(vo.getPk_wa_class());
		WaPsnhiBVO[] bvos = queryWaRangeByClass(vo.getPk_wa_class());
		WaFiorgVO[] fiorgvos = queryWaFiorgByClass(vo.getPk_wa_class());
		vo.setWaPsnhiVOs(tempvos);
		vo.setWaPsnhiBVOs(bvos);
		vo.setWaclassFiorgvo(fiorgvos);
		return vo;

	}
	/**
	 * 补充计薪规则与计薪范围(批量)
	 *
	 * @author xuanlt on 2010-4-15
	 * @param vos
	 * @return
	 * @return WaClassVO[]
	 * @throws DAOException
	 */

	public WaClassVO[] completeRange(WaClassVO[] vos) throws DAOException {
		if(vos==null||vos.length==0){
			return vos;
		}
		HashMap<String, WaPsnhiVO[]> maphivo = queryRangRuleByClass(vos);
		HashMap<String, WaPsnhiBVO[]> maphibvos = queryWaRangeByClass(vos);
		HashMap<String,WaFiorgVO[]> mapfiorgvos = queryWaFiorgByClass(vos);
		for(WaClassVO vo:vos){
			vo.setWaPsnhiVOs(maphivo.get(vo.getPk_wa_class()));
			vo.setWaPsnhiBVOs(maphibvos.get(vo.getPk_wa_class()));
			vo.setWaclassFiorgvo(mapfiorgvos.get(vo.getPk_wa_class()));
		}
		return vos;

	}

	public HashMap<String, WaPsnhiVO[]> queryRangRuleByClass(WaClassVO[] vos) throws DAOException {
		HashMap<String, WaPsnhiVO[]> map = new HashMap<String, WaPsnhiVO[]>();
		String[] clspks = null;
		String[] subclspks = null;
		for(WaClassVO vo:vos){
			if(vo.getShowflag().booleanValue()){
				clspks = (String[]) ArrayUtils.add(clspks, vo.getPk_wa_class());
			}else{
				subclspks = (String[]) ArrayUtils.add(subclspks, vo.getPk_wa_class());
			}
		}
		String clssql = null;
		String subclssql = null;
		InSQLCreator isc = new InSQLCreator();
		if(clspks!=null&&clspks.length>0){
			String insql = null;
			try {
				insql = isc.getInSQL(clspks);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				throw new DAOException(e.getMessage());
			}
			//guoqt计薪规则显示顺序
//			clssql = "select pk_wa_grd,pk_wa_psnhi , pk_flddict, code  as  vfldcode, name as vfldname , wa_psnhi.showorder  , isref , refmodel from wa_psnhi, wa_rangetable where wa_psnhi.pk_flddict = pk_rangetable  and  pk_wa_grd in ("+insql+") order by pk_wa_grd,wa_psnhi.showorder ";
			// 2015-11-16 zhousze name需要根据当前语种取选择，所以这里处理一下 begin
			clssql = "select pk_wa_grd,pk_wa_psnhi , pk_flddict, code  as  vfldcode, " + SQLHelper.getMultiLangNameColumn("name")
					+ " as vfldname , wa_psnhi.showorder  , isref , refmodel from wa_psnhi, wa_rangetable where wa_psnhi.pk_flddict = pk_rangetable  and  pk_wa_grd in ("+insql+") order by pk_wa_grd ";
			// end

		}
		if(subclspks!=null&&subclspks.length>0){
			String insql = null;
			try {
				insql = isc.getInSQL(subclspks);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				throw new DAOException(e.getMessage());
			}
			// 2015-11-16 zhousze name需要根据当前语种取选择，所以这里处理一下 begin
			subclssql = "select pk_wa_grd,pk_wa_psnhi , pk_flddict, code  as  vfldcode, " + SQLHelper.getMultiLangNameColumn("name")
					+ " as vfldname , wa_psnhi.showorder  , isref , refmodel from wa_psnhi, wa_rangetable where wa_psnhi.pk_flddict = pk_rangetable  and  (pk_wa_grd in (select distinct pk_parentclass from wa_inludeclass where pk_childclass in ("+insql+"))) order by pk_wa_grd,wa_psnhi.showorder ";
			// end
		}
		if(clssql!=null){
			WaPsnhiVO[] tempvos = executeQueryVOs(clssql, WaPsnhiVO.class);
			if(tempvos!=null&&tempvos.length>0){
				for(WaPsnhiVO vo:tempvos){
					String pk_wa_class = vo.getPk_wa_grd();
					WaPsnhiVO[] vos2 = map.get(pk_wa_class);
					vos2 = (WaPsnhiVO[]) ArrayUtils.add(vos2, vo);
					map.put(pk_wa_class, vos2);
				}
			}
		}
		if(subclssql!=null){
			WaPsnhiVO[] tempsubvos = executeQueryVOs(subclssql, WaPsnhiVO.class);
			if(tempsubvos!=null&&tempsubvos.length>0){
				for(WaPsnhiVO vo:tempsubvos){
					String pk_wa_class = vo.getPk_wa_grd();
					WaPsnhiVO[] vos2 = map.get(pk_wa_class);
					vos2 = (WaPsnhiVO[]) ArrayUtils.add(vos2, vo);
					map.put(pk_wa_class, vos2);
				}
			}
		}

		return map;

	}

	public WaPsnhiVO[] queryRangRuleByClass(String classpk) throws DAOException {

		// 2015-11-16 zhousze name需要根据当前语种取选择，所以这里处理一下 begin
		String sql = "  select pk_wa_grd,pk_wa_psnhi , pk_flddict, code  as  vfldcode, " + SQLHelper.getMultiLangNameColumn("name")
				+ " as vfldname , wa_psnhi.showorder  , isref , refmodel from wa_psnhi, wa_rangetable where wa_psnhi.pk_flddict = pk_rangetable  and  (pk_wa_grd = ? or pk_wa_grd = (select distinct pk_parentclass from wa_inludeclass where pk_childclass = ?)) order by wa_psnhi.showorder ";
		// end
		SQLParameter param2 = new SQLParameter();
		param2.addParam(classpk);
		param2.addParam(classpk);
		WaPsnhiVO[] tempvos = executeQueryVOs(sql, param2, WaPsnhiVO.class);

		return tempvos;

	}

	public WaPsnhiBVO[] queryWaRangeByClass(String classpk) throws DAOException {

		String sql = "  select *  from  wa_psnhi_b  where pk_wa_grdlv = ? ";
		SQLParameter param2 = new SQLParameter();
		param2.addParam(classpk);
		WaPsnhiBVO[] tempvos = executeQueryVOs(sql, param2, WaPsnhiBVO.class);

		return tempvos;

	}
	public HashMap<String, WaPsnhiBVO[]> queryWaRangeByClass(WaClassVO[] vos) throws DAOException {

		InSQLCreator isc = new InSQLCreator();
		String insql;
		try {
			insql = isc.getInSQL(vos,WaClassVO.PK_WA_CLASS);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage());
		}
		String sql = "  select *  from  wa_psnhi_b  where pk_wa_grdlv in ("+insql+") order by pk_wa_grdlv";

		WaPsnhiBVO[] tempvos = executeQueryVOs(sql, WaPsnhiBVO.class);
		HashMap<String, WaPsnhiBVO[]> map = new HashMap<String, WaPsnhiBVO[]>();
		/////////////////////////////////////
		if(tempvos!=null&&tempvos.length>0){
			for(WaPsnhiBVO vo:tempvos){
				String pk_wa_class = vo.getPk_wa_grdlv();
				WaPsnhiBVO[] fvos = map.get(pk_wa_class);
				fvos = (WaPsnhiBVO[]) ArrayUtils.add(fvos, vo);
				map.put(pk_wa_class, fvos);
			}
		}
		return map;

	}


	public HashMap<String, WaFiorgVO[]> queryWaFiorgByClass(WaClassVO[] vos) throws DAOException {
		InSQLCreator isc = new InSQLCreator();
		String insql;
		try {
			insql = isc.getInSQL(vos,WaClassVO.PK_WA_CLASS);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage());
		}
		String sql = "  select pk_class_fiorg,pk_wa_class,pk_financeorg from wa_class_fiorg where  pk_wa_class in ( "+insql+" ) order by pk_wa_class";
		WaFiorgVO[] tempvos = executeQueryVOs(sql, WaFiorgVO.class);
		HashMap<String, WaFiorgVO[]> map = new HashMap<String, WaFiorgVO[]>();
		/////////////////////////////////////
		if(tempvos!=null&&tempvos.length>0){
			for(WaFiorgVO vo:tempvos){
				String pk_wa_class = vo.getPk_wa_class();
				WaFiorgVO[] fvos = map.get(pk_wa_class);
				fvos = (WaFiorgVO[]) ArrayUtils.add(fvos, vo);
				map.put(pk_wa_class, fvos);
			}
		}
		/////////////////////////////////////
		return map;

	}

	public WaFiorgVO[] queryWaFiorgByClass(String classpk) throws DAOException {

		String sql = "  select pk_class_fiorg,pk_wa_class,pk_financeorg from wa_class_fiorg where  pk_wa_class = ? order by pk_wa_class";
		SQLParameter param2 = new SQLParameter();
		param2.addParam(classpk);

		WaFiorgVO[] tempvos = executeQueryVOs(sql, param2, WaFiorgVO.class);

		return tempvos;

	}

	public WaFiorgVO[] queryCheckedWaFiorgByClass(String classpk)
			throws DAOException {

		String sql = " select pk_class_fiorg,pk_wa_class,pk_financeorg,value as accountBookId from wa_class_fiorg  inner join pub_sysinit on wa_class_fiorg.pk_financeorg = pub_sysinit.pk_org where  pk_wa_class=? and initcode = '"
				+ ParaConstant.GL034
				+ "' and  ( pub_sysinit.value is not null and pub_sysinit.value <> '~') ";
		SQLParameter param2 = new SQLParameter();
		param2.addParam(classpk);

		WaFiorgVO[] tempvos = executeQueryVOs(sql, param2, WaFiorgVO.class);

		return tempvos;

	}



	public WaClassVO[] queryWaClassByCondition(String condition) throws DAOException {
		return queryWaClassByCondition(condition,UFBoolean.TRUE);
	}


	public WaClassVO[] queryWaClassByCondition(String condition,UFBoolean onlyUserShow) throws DAOException {
		String showConditon = "";
		if( onlyUserShow.booleanValue()){
			showConditon = " showflag = 'Y' ";
		}

		if (!StringUtil.isEmpty(condition)) {
			if(!StringUtil.isEmpty(showConditon)){
				condition =  condition + " and " + showConditon;
			}

		}else{
			condition = showConditon;
		}

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select * "); // 1
		sqlB.append("  from wa_waclass  ");

		if (!StringUtil.isEmpty(condition)) {
			sqlB.append(" where ");
			if (condition.trim().startsWith("and")) {
				condition = condition.trim().substring(3);
			}
			sqlB.append(condition);
		}

		return executeQueryVOs(sqlB.toString(), WaClassVO.class);
	}

	public WaClassVO queryWaClassByParentClass(String parentClassPK) throws DAOException {

		StringBuilder sbd = new StringBuilder();
		sbd.append(" pk_wa_class=  (");
		sbd.append("	select  pk_childclass    from  wa_inludeclass  where batch =( select max(batch)  from wa_inludeclass  where pk_parentclass = '"+parentClassPK+"'" );
		sbd.append("   and wa_inludeclass.cyear = (select cyear from wa_waclass where pk_wa_class = '"+parentClassPK+"') ");
		sbd.append("   and wa_inludeclass.cperiod = (select cperiod from wa_waclass where pk_wa_class = '"+parentClassPK+"') ");
		sbd.append("   and wa_inludeclass.batch < 100 ");
		sbd.append(		") and pk_parentclass = '"+parentClassPK+"'");
		sbd.append("   and wa_inludeclass.cyear = (select cyear from wa_waclass where pk_wa_class = '"+parentClassPK+"') ");
		sbd.append("   and wa_inludeclass.cperiod = (select cperiod from wa_waclass where pk_wa_class = '"+parentClassPK+"') ");
		sbd.append(")");


		WaClassVO[] vos =  	queryWaClassByCondition(sbd.toString(),UFBoolean.FALSE);

		if(vos.length==1){
			return vos[0];
		}else if(ArrayUtils.isEmpty(vos)){
			throw new DAOException(ResHelper.getString("60130waclass","060130waclass0063")/*@res "没有查询到对应的子方案"*/);
		}


		else{
			throw new DAOException(ResHelper.getString("60130waclass","060130waclass0064")/*@res "子方案不唯一"*/);
		}
	}



	public AssignclsVO[] queryWaClsAssignedOrg(WaClassVO vo) throws DAOException {

		StringBuilder sbd = new StringBuilder();
		sbd.append(" select wa_assigncls.pk_assigncls,");
		sbd.append(" wa_assigncls.pk_sourcecls,");
		sbd.append(" wa_assigncls.classid,");
		sbd.append("  "+SQLHelper.getMultiLangNameColumn("org_orgs.name")+ " as name,");
		sbd.append(" org_orgs.code,");
		sbd.append(" org_orgs.pk_org,");
		sbd.append(" wa_assigncls.startyear,");
		sbd.append(" wa_assigncls.startperiod,");
		sbd.append(" wa_waclass.cyear,");
		sbd.append(" wa_waclass.cperiod,");
//      BY:xiejie3  查询薪资档案，薪资方案是否被审核，集团级方案目前没有实现多次发放，并且直接查询相应档案下薪资档案checkflag。
//		sbd.append(" (select count(pk_wa_data) from wa_data where pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass =wa_assigncls.classid and cyear = wa_waclass.cyear and cperiod = wa_waclass.cperiod) and checkflag = 'Y' and stopflag = 'N' and cyear = wa_waclass.cyear and cperiod = wa_waclass.cperiod) as approvecount ");
		sbd.append(" (select count(pk_wa_data) from wa_data where pk_wa_class =wa_assigncls.classid  and checkflag = 'Y' and stopflag = 'N' ) as approvecount ");
//      end:xiejie3

		sbd.append(" from wa_waclass ");
		sbd.append(" inner join org_orgs on wa_waclass.pk_org = org_orgs.pk_org");
		sbd
		.append(" inner join wa_assigncls on wa_waclass.pk_wa_class = wa_assigncls.classid and org_orgs.pk_org = wa_assigncls.pk_org");
		sbd
		.append(" inner join wa_period on wa_waclass.pk_periodscheme = wa_period.pk_periodscheme and wa_period.cyear = wa_waclass.cyear and wa_period.cperiod = wa_waclass.cperiod ");
		sbd
		.append("  inner join wa_periodstate on wa_periodstate.pk_wa_period = wa_period.pk_wa_period and wa_periodstate.pk_wa_class = wa_waclass.pk_wa_class");
		sbd.append(" where (wa_assigncls.moduleflag = 0) and (wa_assigncls.pk_sourcecls =  '"
				+ vo.getPk_wa_class() + "')");
		sbd.append(" order by org_orgs.code");

		return executeQueryVOs(sbd.toString(), AssignclsVO.class);

	}

	/**
	 * 查询薪资转档可以转入的方案： 最新期间未审核，未停用，同一组织下不等于当前方案
	 *
	 * @author liangxr on 2010-1-13
	 * @param context
	 * @return
	 * @throws DAOException
	 */
	public WaClassVO[] queryAllClassidForRollIn(WaLoginContext context) throws DAOException {
		String sql ="SELECT wa_waclass.code,  "+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")+"  as name, wa_inludeclass.pk_childclass AS pk_wa_class, wa_waclass.cyear, wa_waclass.cperiod,wa_inludeclass.batch,wa_inludeclass.pk_parentclass as pk_prnt_class"
				+ " FROM wa_waclass "
				+ "		INNER JOIN wa_inludeclass "
				+ "		ON wa_waclass.pk_wa_class = wa_inludeclass.pk_parentclass "
				+ "		INNER JOIN wa_periodstate "
				+ "		ON wa_inludeclass.pk_childclass = wa_periodstate.pk_wa_class "
				+ "		INNER JOIN wa_period "
				+ "		ON wa_period.pk_wa_period = wa_periodstate.pk_wa_period "
				+ "		AND wa_period.cyear = wa_inludeclass.cyear "
				+ "		AND wa_period.cperiod = wa_inludeclass.cperiod "
				+ "WHERE wa_waclass.pk_org = ? "
				+ "	AND wa_inludeclass.pk_childclass <> ? "
				+ "	AND wa_periodstate.checkflag = 'N' "
				+ "	AND wa_waclass.stopflag = 'N' "
				+ "	AND wa_waclass.showflag='Y' "
				+ "	and wa_waclass.MUTIPLEFLAG = 'Y' "
				+ " and wa_inludeclass.batch<100"
				+"  and wa_inludeclass.pk_parentclass in ("+ClassPowerUtil.getClassower(context)+") and wa_waclass.cyear=? and wa_waclass.cperiod=? ";


		SQLParameter param = new SQLParameter();
		param.addParam(context.getPk_org());
		param.addParam(context.getPk_wa_class());
		param.addParam(context.getCyear());
		param.addParam(context.getCperiod());
		WaClassVO[] mutipleClass= executeQueryVOs(sql, param, WaClassVO.class);
		sql ="SELECT wa_waclass.code,  "+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")+"  as name, " +
				"wa_waclass.pk_wa_class,wa_waclass.cyear,wa_waclass.cperiod,'1' as batch,wa_waclass.pk_wa_class FROM wa_waclass " +
				"INNER JOIN wa_periodstate ON wa_waclass.pk_wa_class  = wa_periodstate.pk_wa_class " +
				"INNER JOIN wa_period ON wa_period.pk_wa_period = wa_periodstate.pk_wa_period " +
				"AND wa_period.cyear = wa_waclass.cyear AND wa_period.cperiod = wa_waclass.cperiod " +
				"WHERE wa_waclass.pk_org = ? AND wa_waclass.pk_wa_class  <> ? " +
				"AND wa_periodstate.checkflag = 'N' AND wa_waclass.stopflag = 'N' AND wa_waclass.showflag = 'Y' " +
				"AND wa_waclass.MUTIPLEFLAG = 'N' and wa_waclass.pk_wa_class in ("+ClassPowerUtil.getClassower(context)+")" +
				" and wa_waclass.cyear=? and wa_waclass.cperiod=? ";



		param = new SQLParameter();
		param.addParam(context.getPk_org());
		param.addParam(context.getPk_wa_class());
		param.addParam(context.getCyear());
		param.addParam(context.getCperiod());
		WaClassVO[] singleClass= executeQueryVOs(sql, param, WaClassVO.class);

		int mutipleCount = mutipleClass == null ? 0 : mutipleClass.length;
		int singleCount = singleClass == null ? 0 : singleClass.length;
		if (mutipleCount + singleCount == 0)
		{
			return null;
		}
		WaClassVO[] waClassVOs = new WaClassVO[mutipleCount + singleCount];
		if (mutipleCount > 0)
		{
			System.arraycopy(mutipleClass, 0, waClassVOs, 0, mutipleCount);
		}
		if (singleCount > 0)
		{
			System.arraycopy(singleClass, 0, waClassVOs, mutipleCount, singleCount);
		}
		return waClassVOs;
	}

	public WaClassVO[] queryWaClassByUnitClass(String uinitClasspk) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB
		.append("select	wa_waclass.pk_wa_class ,  "+ SQLHelper.getMultiLangNameColumn("wa_waclass.name")+ "  , wa_waclass.code , wa_waclass.voucherflag  ");
		sqlB.append("from wa_waclass ");
		sqlB
		.append("where	wa_waclass.pk_wa_class in (	select	classedid 	from wa_unitctg where wa_unitctg.pk_wa_class = ?)");

		SQLParameter param = new SQLParameter();
		param.addParam(uinitClasspk);

		return executeQueryVOs(sqlB.toString(), param, WaClassVO.class);
	}

	/**
	 * 集团类别要封存，判断所有分配出去的薪资类别，在最新业务期间是否有审核的数据 如果没有，则认为允许封存
	 *
	 * @author xuanlt on 2010-1-26
	 * @param vo
	 * @return
	 * @throws DAOException
	 * @return boolean
	 */
	public boolean groupClassSealable(WaClassVO vo) throws DAOException {

		StringBuilder sbd = new StringBuilder();
		sbd.append(" select ");
		sbd.append(" data.pk_wa_data ");
		sbd.append(" from ");
		sbd.append(" wa_data data , " );

		sbd.append(" (	select  " );
		sbd.append(" 		(case when isnull(wa_inludeclass.pk_childclass,'~')='~' then wa_waclass.PK_WA_CLASS else wa_inludeclass.pk_childclass end) as classid,  " );
		sbd.append(" 		wa_waclass.cyear AS cyear, wa_waclass.cperiod AS cperiod  " );
		sbd.append(" 				from " );
		sbd.append(" 						wa_assigncls  " );
		sbd.append(" 				inner join wa_waclass on wa_assigncls.classid = wa_waclass.pk_wa_class  " );
		sbd.append(" 				left join wa_inludeclass on wa_waclass.pk_wa_class = wa_inludeclass.pk_parentclass  " );
		sbd.append(" 					and wa_waclass.cyear = wa_inludeclass.cyear " );
		sbd.append(" 					and wa_waclass.cperiod = wa_inludeclass.cperiod  " );
		sbd.append(" 	WHERE wa_assigncls.pk_sourcecls = ? 		) " );

		sbd.append(" assginvos ");
		sbd.append(" where  ");
		sbd.append(" data.pk_wa_class = assginvos.classid and data.cyear = assginvos.cyear and data.cperiod = assginvos.cperiod and data.checkflag = 'Y'");

		SQLParameter para = new SQLParameter();
		para.addParam(vo.getPk_wa_class());

		if (isValueExist(sbd.toString(), para)) {
			return false;
		} else {
			return true;
		}

	}

	public void sealAssignedWaClass(WaClassVO vo) throws DAOException {

		//更新薪资方案本身的标示
		StringBuilder sbd = new StringBuilder();
		sbd.append(" update wa_waclass  ");
		sbd.append(" set stopflag = 'Y' ");
		sbd
		.append(" where pk_wa_class in (select wa_assigncls.classid from wa_assigncls where wa_assigncls.pk_sourcecls = ? ) ");

		SQLParameter para = new SQLParameter();
		para.addParam(vo.getPk_wa_class());

		getBaseDao().executeUpdate(sbd.toString(), para);


		//封存子方案最新的业务期间
		sbd = new StringBuilder();

		sbd.append(" update  wa_periodstate set enableflag = 'N' where pk_wa_class  in (select wa_assigncls.classid from wa_assigncls where wa_assigncls.pk_sourcecls = ? )  and wa_periodstate.pk_wa_period = ( ");
		sbd.append("		select wa_period.pk_wa_period from wa_waclass, wa_period where  ");
		sbd.append("		wa_period.pk_periodscheme = wa_waclass.pk_periodscheme and  wa_period.cyear = wa_waclass.cyear and wa_period.cperiod = wa_waclass.cperiod  ");
		sbd.append("	    and wa_waclass.pk_wa_class = wa_periodstate.pk_wa_class ");
		sbd.append("		and wa_period.pk_wa_period = wa_periodstate.pk_wa_period  ");
		sbd.append("		) ");

		getBaseDao().executeUpdate(sbd.toString(), para);


	}

	public WaClassVO[] queryGroupAssignedWaclass(WaClassVO vo) throws DAOException {

		String sql = " select wa_waclass.*  from  wa_waclass,wa_assigncls where wa_waclass.pk_wa_class = wa_assigncls.classid and  wa_assigncls.pk_sourcecls =? ";

		SQLParameter para = new SQLParameter();
		para.addParam(vo.getPk_wa_class());

		return executeQueryVOs(sql, para, WaClassVO.class);
	}

	public OrgVO[] queryWaClsUnassignedOrg(WaClassVO vo, OrgVO[] orgvos) throws DAOException {

		if (ArrayUtils.isEmpty(orgvos)) {
			return new OrgVO[0];
		}

		// StringBuffer sqlB = new StringBuffer();
		// sqlB.append("select pk_org , code,name ");
		// sqlB.append("from org_orgs ");
		// sqlB
		// .append("where not exists (select  wa_assigncls.pk_org from wa_assigncls where wa_assigncls.pk_org  = org_orgs.pk_org");
		// sqlB.append(" and wa_assigncls.pk_sourcecls = '" +
		// vo.getPk_wa_class()
		// + "') and org_orgs.pk_group = '" + vo.getPk_group() +
		// "'  and org_orgs.pk_org <>'"+ vo.getPk_group() +"'");
		// 查询已经分配的
		AssignclsVO[] assvos = queryWaClsAssignedOrg(vo);
		// orgvos 中出去已经分配的
		ArrayList<OrgVO> list = new ArrayList<OrgVO>();

		for (int i = 0; i < orgvos.length; i++) {
			OrgVO orgvo = orgvos[i];
			boolean find = false;
			for (int j = 0; j < assvos.length; j++) {
				if (assvos[j].getPk_org().equals(orgvo.getPk_org())) {
					find = true;
				}
			}

			if (!find) {
				list.add(orgvo);
			}

		}

		return list.toArray(new OrgVO[list.size()]);

	}

	private <T> String getVisibleCondition(LoginContext context, Class<T> class1) {
		String visibleWhere = "";

		try {
			visibleWhere = VisibleUtil.getVisibleCondition(context, class1);
			Method m = class1.getMethod("getTableName", new Class[] {});
			String sTablename = (String) m.invoke(class1.newInstance(), new Object[] {});
			visibleWhere = visibleWhere.replaceAll("pk_org", sTablename + ".pk_org").replaceAll("pk_group",
					sTablename + ".pk_group");

		} catch (Exception e) {
			Logger.error(e);
		}

		return visibleWhere;

	}

	public String queryWaRangeRule(String pk_waclass) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB
		.append(" select wa_rangetable.code as field,wa_psnhi_b.vfldvalue  as value ,wa_psnhi_b.sortgroup  as sortgroup "
				+ " from wa_psnhi,wa_psnhi_b,wa_rangetable "
				+ " where wa_psnhi_b.pk_wa_psnhi = wa_psnhi.pk_flddict and wa_psnhi_b.pk_wa_grdlv = wa_psnhi.pk_wa_grd  and wa_psnhi.pk_flddict = pk_rangetable "
				+ " and (wa_psnhi.pk_wa_grd = ? or wa_psnhi.pk_wa_grd =(select distinct pk_parentclass from WA_INLUDECLASS where pk_childclass = ?))"
				+ " order by sortgroup");
		SQLParameter para = new SQLParameter();
		para.addParam(pk_waclass);
		para.addParam(pk_waclass);
		WaRangeConditon[] conditons = executeQueryVOs(sqlB.toString(), para, WaRangeConditon.class);
		if (ArrayUtils.isEmpty(conditons)) {
			return "";
		} else {
			StringBuffer sbf = new StringBuffer();

			for (int temp = 0; temp < conditons.length; temp++) {
				if (temp == 0) {
					sbf.append("((");
					sbf.append(conditons[temp].getConditon());

				}
				if (temp == conditons.length - 1) {
					if (temp != 0) {

						if (conditons[temp - 1].getSortgroup().equals(conditons[temp].getSortgroup())) {
							sbf.append(" and " + conditons[temp].getConditon());
						} else {
							sbf.append(" ) or ( " + conditons[temp].getConditon());
						}

					}
					sbf.append("))");

				}
				if (temp > 0 && temp < conditons.length - 1) {
					if (conditons[temp - 1].getSortgroup().equals(conditons[temp].getSortgroup())) {
						sbf.append(" and " + conditons[temp].getConditon());
					} else {
						sbf.append(" ) or ( " + conditons[temp].getConditon());
					}
				}
			}

			return sbf.toString();
		}

	}



	/**
	 * 判断启用期间是否大于停用期间(停用期间为该方案的最新期间)
	 *
	 * @author liangxr on 2010-8-3
	 * @param pk_waclass
	 * @param periodvo
	 * @return
	 * @throws DAOException
	 */
	public boolean isEnablePeriod(String pk_waclass, PeriodVO periodvo) throws DAOException {
		String sql = "select 1 from wa_waclass " + "where wa_waclass.pk_wa_class = ? "
				+ "and wa_waclass.cyear||wa_waclass.cperiod>? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_waclass);
		para.addParam(periodvo.getCyear() + periodvo.getCperiod());
		return !isValueExist(sql, para);
	}

	/**
	 * 启用时添加人员
	 *
	 * @author liangxr on 2010-8-5
	 * @param vo
	 * @throws DAOException
	 */
	public void updatePayfileForUnseal(WaClassVO vo) throws DAOException {

		// 查询有人员的最后一个期间
		String sql = "select max(cyearperiod)  from wa_data "
				+ "where pk_wa_class = ? and cyearperiod<=? having count(pk_wa_data)>0";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear() + vo.getCperiod());
		String oldperiod = (String) this.getBaseDao().executeQuery(sql, parameter, new ColumnProcessor());

		if (oldperiod == null)
			return;

		DataVO dataVOCon = new DataVO();
		dataVOCon.setPk_wa_class(vo.getPk_wa_class());
		dataVOCon.setCyear(oldperiod.substring(0, 4));
		dataVOCon.setCperiod(oldperiod.substring(4, 6));
		dataVOCon.setCyearperiod(oldperiod);
		DataVO[] dataVOs = new AppendBaseDAO().retrieveAppendableVOs(dataVOCon, DataVO.PK_WA_CLASS,
				DataVO.CYEAR, DataVO.CPERIOD);

		if (dataVOs == null) {
			return;
		}

		// 预形成主键， 为多个表引用
		String strOids[] = OidGenerator.getInstance().nextOid(OidGenerator.GROUP_PK_CORP, dataVOs.length);

		for (int i = 0; i < dataVOs.length; i++) {
			DataVO dataVO = dataVOs[i];
			dataVO.setPk_wa_data(strOids[i]);
			// 更新期间
			dataVO.setCyear(vo.getCyear());
			dataVO.setCperiod(vo.getCperiod());
			// 更新状态
			dataVO.setCheckflag(UFBoolean.FALSE);
			dataVO.setCaculateflag(UFBoolean.FALSE);
		}
		// 形成下个月的数据
		getBaseDao().insertVOArrayWithPK(dataVOs);

	}

	/**
	 * 判断方案的最新期间是否有人
	 *
	 * @author liangxr on 2010-8-5
	 * @param vo
	 * @return
	 * @throws DAOException
	 */
	public boolean isHavePsn(WaClassVO vo) throws DAOException {
		String sql = "select 1 from wa_data where pk_wa_class = ? and cyear  = ? and cperiod = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear());
		parameter.addParam(vo.getCperiod());
		return isValueExist(sql, parameter);
	}

	/**
	 * 判断方案下是否有已审核人员
	 * @author liangxr on 2010-8-5
	 * @param vo
	 * @return
	 * @throws DAOException
	 */
	public boolean isHaveCheckPsn(WaClassVO vo) throws DAOException {
		String sql = "select 1 from wa_data where pk_wa_class = ? and checkflag='Y'";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_wa_class());
		return isValueExist(sql, parameter);
	}
	/**
	 * 根据父方案期间查询所有子方案
	 * @param pk_waclass
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws DAOException
	 */
	public WaInludeclassVO[] querySubClasses(String pk_waclass,String cyear,String cperiod,boolean withoutLeaveClass) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select wa_inludeclass.* ");
		sqlB.append("  from wa_inludeclass  ");
		sqlB.append(" where wa_inludeclass.pk_parentclass =? ");
		sqlB.append("   and wa_inludeclass.cyear = ? ");
		sqlB.append("	and wa_inludeclass.cperiod = ? ");

		if(withoutLeaveClass){
			sqlB.append("	and wa_inludeclass.batch  < 100 ");
		}
		sqlB.append(" order by wa_inludeclass.batch ");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_waclass);
		param.addParam(cyear);
		param.addParam(cperiod);

		return executeQueryVOs(sqlB.toString(), param, WaInludeclassVO.class);
	}


	public WaInludeclassVO[] queryAllPayOffChildClasses(String pk_waclass,
			String cyear, String cperiod) throws DAOException {
		StringBuilder sbd = new StringBuilder();
		sbd.append("   select wa_inludeclass.* from wa_inludeclass ,wa_periodstate ,wa_period where  ");
		sbd.append("  wa_periodstate.pk_wa_period = wa_period.pk_wa_period  and   ");
		sbd.append("   wa_inludeclass.pk_childclass = wa_periodstate.pk_wa_class  ");
		sbd.append("  and wa_inludeclass.cyear = wa_period.cyear and wa_inludeclass.cperiod = wa_period.cperiod  ");
		sbd.append("  and wa_inludeclass.pk_parentclass = ? and wa_inludeclass.cyear  = ? and wa_inludeclass.cperiod =?   and wa_periodstate.payoffflag = 'Y'");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_waclass);
		param.addParam(cyear);
		param.addParam(cperiod);

		return executeQueryVOs(sbd.toString(), param, WaInludeclassVO.class);

	}

	public WaInludeclassVO[] queryAllCheckedChildClasses(String pk_waclass,
			String cyear, String cperiod) throws DAOException {
		StringBuilder sbd = new StringBuilder();
		sbd.append("   select wa_inludeclass.* from wa_inludeclass ,wa_periodstate ,wa_period where  ");
		sbd.append("  wa_periodstate.pk_wa_period = wa_period.pk_wa_period  and   ");
		sbd.append("   wa_inludeclass.pk_childclass = wa_periodstate.pk_wa_class  ");
		sbd.append("  and wa_inludeclass.cyear = wa_period.cyear and wa_inludeclass.cperiod = wa_period.cperiod  ");
		sbd.append("  and wa_inludeclass.pk_parentclass = ? and wa_inludeclass.cyear  = ? and wa_inludeclass.cperiod =?   and wa_periodstate.checkflag = 'Y'");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_waclass);
		param.addParam(cyear);
		param.addParam(cperiod);

		return executeQueryVOs(sbd.toString(), param, WaInludeclassVO.class);

	}

	/**
	 * 根据子方案pk，薪资期间，查询WaInludeclassVO
	 * @param pk_childclass
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws DAOException
	 */
	public WaInludeclassVO queryInludeclassvo(String pk_childclass,
			String cyear, String cperiod) throws DAOException {

		StringBuilder sbd = new StringBuilder();
		sbd.append("   select wa_inludeclass.* from wa_inludeclass where pk_childclass = ? and wa_inludeclass.cyear  = ? and wa_inludeclass.cperiod =?   ");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_childclass);
		param.addParam(cyear);
		param.addParam(cperiod);

		WaInludeclassVO[]	vos= executeQueryVOs(sbd.toString(), param, WaInludeclassVO.class);
		if(ArrayUtils.isEmpty(vos)){
			return null;
		}
		return vos[0];
	}




	public WaClassVO queryParentClass(String pk_waclass,String cyear,String cperiod) throws DAOException {

		String condition = " pk_wa_class =(select  pk_parentclass from wa_inludeclass where pk_childclass  = '"+ pk_waclass + "' and cyear = '" + cyear +"' and cperiod = '"+ cperiod +"')";
		WaClassVO[] vos 	= queryWaClassByCondition(condition);

		if(ArrayUtils.isEmpty(vos)){
			return null;
		}else if(vos.length == 1) {
			return vos[0];
		}else{
			throw new DAOException(ResHelper.getString("60130waclass","060130waclass0065")/*@res "父方案不唯一"*/);
		}

	}

	/**
	 * 更新父方案的发放状态
	 *
	 * @param parentVO WaClassVO
	 * @param payoffflag String
	 * @throws DAOException
	 */
	public void updateParentClassPayOff(WaClassVO parentVO,String payoffflag) throws DAOException{
		String sql = "update wa_periodstate set payoffflag = '" + payoffflag
				+ "' where  pk_wa_class = '" + parentVO.getPk_wa_class()
				+ "' and exists "
				+ "(select wa_period.pk_wa_period  from wa_period  "
				+ "  where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ "    and wa_period.cyear =  '" + parentVO.getCyear()
				+ "'   and wa_period.cperiod =  '" + parentVO.getCperiod()
				+ "'   and  " + "wa_periodstate.pk_wa_class =  '" + parentVO.getPk_wa_class() + "')";
		getBaseDao().executeUpdate(sql);
	}
	/**
	 * 更新父方案的多次发放标示
	 *
	 * @param parentVO WaClassVO
	 * @param mutipleflag String
	 * @throws DAOException
	 */
	public void updateMutipleFlag(WaClassVO parentVO,boolean mutipleflag) throws DAOException{
		String sql = " update wa_waclass set mutipleflag = '"+(mutipleflag?"Y":"N")+"' where"+
				" pk_wa_class = '"+parentVO.getPk_wa_class()+"'";
		getBaseDao().executeUpdate(sql);
		parentVO.setMutipleflag(UFBoolean.TRUE);
		sql = " update wa_periodstate set classtype = "
				+ WaLoginVOHelper.getClassType(parentVO).getValue() + " where"
				+
				" pk_wa_class = '"+parentVO.getPk_wa_class()+"' and exists " + "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear =  '"+parentVO.getCyear()+"' and wa_period.cperiod =  '"+parentVO.getCperiod()+"' and  "
				+ "wa_periodstate.pk_wa_class =  '"+parentVO.getPk_wa_class()+"')";
		getBaseDao().executeUpdate(sql);
	}
	/**
	 * 是否所有子方案全部发放
	 * @param loginContext
	 * @return
	 * @throws DAOException
	 */
	public boolean isChildPayoff(WaClassVO parentVO) throws DAOException {
		String sql = "SELECT wa_inludeclass.pk_childclass "
				+ "FROM wa_inludeclass,wa_periodstate,wa_period "
				+ "WHERE wa_inludeclass.pk_childclass = wa_periodstate.pk_wa_class "
				+ "	AND wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "	AND wa_inludeclass.cyear = wa_period.cyear "
				+ "	AND wa_inludeclass.cperiod = wa_period.cperiod "
				+ "	AND wa_inludeclass.pk_parentclass = ? "
				+ "	AND wa_inludeclass.cyear = ? "
				+ "	AND wa_inludeclass.cperiod = ? "
				+ "	AND wa_periodstate.payoffflag = 'N' ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(parentVO.getPk_wa_class());
		parameter.addParam(parentVO.getCyear());
		parameter.addParam(parentVO.getCperiod());
		return !isValueExist(sql, parameter);
	}

	/**
	 * 根据父方案查询最新期间所有子方案
	 * @param pk_waclass
	 * @return
	 * @throws DAOException
	 */
	public WaInludeclassVO[] queryNewPeriodSubClasses(String pk_waclass) throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select wa_inludeclass.* ");
		sqlB.append("  from wa_inludeclass,wa_waclass  ");
		sqlB.append(" where wa_waclass.pk_wa_class =? ");
		sqlB.append("   and wa_inludeclass.pk_parentclass = wa_waclass.pk_wa_class ");
		sqlB.append("   and wa_inludeclass.cyear = wa_waclass.cyear ");
		sqlB.append("	and wa_inludeclass.cperiod = wa_waclass.cperiod ");
		sqlB.append(" order by wa_inludeclass.batch ");

		SQLParameter param = new SQLParameter();
		param.addParam(pk_waclass);

		return executeQueryVOs(sqlB.toString(), param, WaInludeclassVO.class);
	}


	/**
	 * 正常方案转成离职结薪方案时，增加长兄子方案对应期间状态
	 * @param parentVO
	 * @param childVO
	 * @return
	 * @throws DAOException
	 */
	public void insertPeriodState4Leave(WaClassVO parentVO, WaClassVO childVO) throws DAOException {
		// 查询出符合条件的薪资期间

		String sql = " select * from wa_periodstate  where " +
				" pk_wa_class = '"+parentVO.getPk_wa_class()+"' and exists " + "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear =  '"+parentVO.getCyear()+"' and wa_period.cperiod =  '"+parentVO.getCperiod()+"') ";

		WaPeriodstateVO periodStateVO = (WaPeriodstateVO)getBaseDao().executeQuery(sql, new BeanProcessor(WaPeriodstateVO.class));
		periodStateVO.setPk_wa_class(childVO.getPk_wa_class());
		periodStateVO.setClasstype(WACLASSTYPE.CHILDCLASS.getValue());
		periodStateVO.setPk_periodstate(null);
		periodStateVO.setStatus(VOStatus.NEW);
		getBaseDao().insertVO(periodStateVO);
	}
	/**
	 * 正常方案转成离职结薪方案时，增加长兄子方案薪资发放数据
	 * @param parentVO
	 * @param childVO
	 * @return
	 * @throws DAOException
	 */
	public void insertFirstChildPsn4Leave(WaClassVO parentVO, WaClassVO childVO) throws DAOException {


		String sql = " select * from wa_data  where " +
				" pk_wa_class = '"+parentVO.getPk_wa_class()+"' and cyear =  '"+parentVO.getCyear()+"' and cperiod =  '"+parentVO.getCperiod()+"' ";
		DataVO[] dataVO = (DataVO[])getBaseDao().executeQuery(sql, new AppendBeanArrayProcessor(DataVO.class));
		if(ArrayUtils.isEmpty(dataVO)){
			return;
		}
		for(DataVO vo:dataVO){
			vo.setPk_wa_class(childVO.getPk_wa_class());
			vo.setPk_wa_data(null);
			vo.setStatus(VOStatus.NEW);
		}
		getBaseDao().insertVOArray(dataVO);
	}


	public void updataPrewadata(WaClassVO parentVO, WaClassVO childVO) throws DAOException{
		StringBuilder sbd = new StringBuilder();
		String pk_parent_class = parentVO.getPk_wa_class();
		String pk_child_class = childVO.getPk_wa_class();
		String cyear = parentVO.getCyear();
		String cperiod = parentVO.getCperiod();

		sbd.append("	update wa_data set prewadata =   (  ");
		sbd.append("		select child.pk_wa_data  from wa_data parent inner join  wa_data  child on  ");
		sbd.append("	child.pk_wa_class = ?  and child.cyear =parent.cyear and child.cperiod = parent.cperiod and  child.pk_psndoc = parent.pk_psndoc  ");
		sbd.append("	where  parent.pk_wa_class = ?  and parent.cyear = ?  and parent.cperiod = ? and parent.pk_wa_data = wa_data.prewadata) ");
		sbd.append("		 where  wa_data.prewadata  in (  select pk_wa_data from wa_data where pk_wa_class = ? and cyear = ?  and cperiod = ? ) ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_child_class);

		parameter.addParam(pk_parent_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);

		parameter.addParam(pk_parent_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);


		getBaseDao().executeUpdate(sbd.toString(), parameter);

	}
	/**
	 * 正常方案转成离职结薪方案时，更新父方案状态（包括期间状态和发放数据状态）
	 * @param parentVO
	 * @param childVO
	 * @return
	 * @throws DAOException
	 */
	public void updatePeriodState4Leave(WaClassVO vo) throws DAOException{
		String sql = " update wa_periodstate set caculateflag='N',checkflag='N',isapproved='N' where " +
				" pk_wa_class = '"+vo.getPk_wa_class()+"' and exists " + "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear =  '"+vo.getCyear()+"' and wa_period.cperiod =  '"+vo.getCperiod()+"')";

		getBaseDao().executeUpdate(sql);

		String sql2 = "update wa_data set caculateflag='N' ,checkflag='N' where " +
				" pk_wa_class = '"+vo.getPk_wa_class()+"' and cyear =  '"+vo.getCyear()+"' and cperiod =  '"+vo.getCperiod()+"' ";
		getBaseDao().executeUpdate(sql2);
	}



	public WaClassVO queryWaClassByPkTaxgroup(String pk_taxgroup) throws DAOException{
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select wa_waclass.pk_wa_class from wa_taxgroup");
		sqlB.append(" inner join wa_taxgrpmember on wa_taxgroup.pk_taxgroup = wa_taxgrpmember.pk_taxgroup " );
		sqlB.append(" inner join wa_waclass on wa_taxgrpmember.pk_waclass = wa_waclass.pk_wa_class " );
		sqlB.append(" where wa_taxgroup.pk_taxgroup = '" + pk_taxgroup + "'");
		sqlB.append(" order by wa_waclass.cyear,wa_waclass.cperiod " );
		WaClassVO[] vos = executeQueryVOs(sqlB.toString(), WaClassVO.class);
		if (vos != null && vos.length > 0) {
			return vos[0];
		}else{
			return null;
		}
	}

	/**
	 * 更新个人所得税数据
	 * @param parentVO
	 * @param childVO
	 * @throws DAOException
	 */
	public void updatePsnTaxdata(WaClassVO parentVO, WaClassVO childVO) throws BusinessException{
		StringBuffer sbd = new StringBuffer();
		sbd.append(" insert into WA_PSNTAX (pk_wa_class,pk_wa_data,displaytaxitem,income_period,income,duty_free_income,");
		sbd.append(" deduction_tax,expense_deduction,deduction_donations,tax_payable,been_deducted_tax,vmemo,taxable_income,");
		sbd.append(" tax_rate,nquickdebuct,pk_psndoc) ");
		sbd.append(" (select  wa_data.PK_WA_CLASS pk_wa_class,wa_data.PK_WA_DATA pk_wa_data,  wa_psntax.displaytaxitem displaytaxitem,wa_psntax.income_period income_period,");
		sbd.append(" wa_psntax.income income,wa_psntax.duty_free_income duty_free_income,wa_psntax.deduction_tax deduction_tax,wa_psntax.expense_deduction expense_deduction,");
		sbd.append(" wa_psntax.deduction_donations deduction_donations,wa_psntax.tax_payable tax_payable,wa_psntax.been_deducted_tax been_deducted_tax,");
		sbd.append(" wa_psntax.vmemo vmemo,wa_psntax.taxable_income taxable_income,wa_psntax.tax_rate tax_rate,wa_psntax.nquickdebuct nquickdebuct,");
		sbd.append(" wa_psntax.pk_psndoc pk_psndoc ");
		sbd.append(" from WA_PSNTAX inner join wa_data on wa_data.PK_PSNDOC =WA_PSNTAX.PK_PSNDOC and  wa_data.CYEAR = '"+childVO.getCyear()+"' and wa_data.CPERIOD = '"+childVO.getCperiod()+"' and wa_data.PK_WA_CLASS = '"+childVO.getPk_wa_class()+"'  ");
		sbd.append(" where wa_psntax.PK_WA_CLASS = '"+parentVO.getPk_wa_class()+"' and wa_psntax.INCOME_PERIOD = '"+childVO.getCyear()+childVO.getCperiod()+"' )");

		getBaseDao().executeUpdate(sbd.toString());
	}

	/**
	 * 更新个别调整项目
	 * @param parentVO
	 * @param childVO
	 * @throws BusinessException
	 */
	public void updateWaDatas(WaClassVO parentVO, WaClassVO childVO) throws BusinessException{
		StringBuffer sbd = new StringBuffer();
		//查询所有的父方案个别调整数据
		sbd.append("select * from wa_datas inner join wa_data on wa_datas.PK_WA_DATA = wa_data.PK_WA_DATA ");
		sbd.append("where wa_data.pk_wa_class = '"+parentVO.getPk_wa_class()+"' and wa_data.cyear = '"+parentVO.getCyear()+"' AND wa_data.cperiod = '"+parentVO.getCperiod()+"' AND wa_data.stopflag = 'N'");
		DataSVO[] vos = ((ArrayList<DataSVO>)getBaseDao().executeQuery(sbd.toString(), new BeanListProcessor(DataSVO.class))).toArray(new DataSVO[0]);
		if (vos!= null && vos.length > 0) {
			sbd = new StringBuffer();
			//找到薪资档案中子方案和父方案的对应关系
			sbd.append("select wd1.pk_wa_data parentPk, wd2.pk_wa_data childPk from wa_data wd1");
			sbd.append(" inner join wa_data wd2 on wd1.pk_psndoc = wd2.pk_psndoc inner join wa_datas on wd1.PK_WA_DATA = wa_datas.PK_WA_DATA ");
			sbd.append(" where wd1.pk_wa_class = '"+parentVO.getPk_wa_class()+"' and wd1.cyear = '"+parentVO.getCyear()+"' AND wd1.cperiod = '"+parentVO.getCperiod()+"' AND wd1.stopflag = 'N'");
			sbd.append(" and wd2.pk_wa_class = '"+childVO.getPk_wa_class()+"' and wd2.cyear = '"+childVO.getCyear()+"' AND wd2.cperiod = '"+childVO.getCperiod()+"' AND wd2.stopflag = 'N'");
			ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>)getBaseDao().executeQuery(sbd.toString(), new MapListProcessor());
			//执行子方案个别调整数据的保存
			Map<String, String> map = new HashMap<String, String>();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					map.put(list.get(i).get("parentpk"), list.get(i).get("childpk"));
				}
			}
			for (int i = 0; i < vos.length; i++) {
				vos[i].setPk_wa_datas(null);
				vos[i].setPk_wa_data(map.get(vos[i].getPk_wa_data()));
				vos[i].setStatus(VOStatus.NEW);
			}
			getBaseDao().insertVOArray(vos);
			sbd = new StringBuffer();
			//删除父方案的个别调整
			sbd.append("delete from wa_datas where pk_wa_datas in (");
			sbd.append("select pk_wa_datas from wa_datas inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data " +
					"where wa_data.pk_wa_class = '"+parentVO.getPk_wa_class()+"' and wa_data.cyear = '"+parentVO.getCyear()+"'" +
					" and wa_data.cperiod = '"+parentVO.getCperiod()+"' and wa_data.stopflag = 'N'");
			sbd.append(")");
			getBaseDao().executeUpdate(sbd.toString());
		}
	}

	public WaClassVO queryPayrollClassbyPK(String pk) throws BusinessException {
		StringBuffer sbd = new StringBuffer();
		// 查询所有的父方案个别调整数据
		sbd.append("select  code,name,cyear,cperiod,showbatch,class_id,pk_org,batch ");
		// sbd.append("from  (select wa_waclass.collectflag,wa_waclass.code,wa_waclass.name,wa_waclass.name2,wa_waclass.name3,"		
		// {MOD:新个税补丁}
		// begin
		sbd.append("from  (select wa_waclass.collectflag,wa_waclass.code,wa_waclass.name,wa_waclass.name2,wa_waclass.name3,wa_waclass.yearbonusflag,"
		// end
				+ "wa_waclass.name4,wa_waclass.name5,wa_waclass.name6,wa_waclass.cyear,wa_waclass.cperiod,"
				+ "(case when batch >100 then '离职发薪' when isnull(wa_inludeclass.pk_childclass,'~')='~'  then '' else replace(convert(char,batch),' ','') end) as showbatch,"
				+ "(case when isnull(wa_inludeclass.pk_childclass,'~')='~' then wa_waclass.PK_WA_CLASS else wa_inludeclass.pk_childclass end) as class_id,"
				+ "wa_waclass.pk_org,batch,showflag,stopflag,pk_childclass,pk_wa_class,PK_COUNTRY "
				+ " from wa_waclass "
				+ "left outer join wa_inludeclass on wa_inludeclass.pk_childclass  = wa_waclass.pk_wa_class and wa_inludeclass.cyear = wa_waclass.cyear and wa_inludeclass.cperiod = wa_waclass.cperiod ) wa_waclass"
				+ "  where  stopflag = 'N'  and  wa_waclass.pk_wa_class = ? ");
		SQLParameter param = new SQLParameter();
		param.addParam(pk);
		return executeQueryVO(sbd.toString(), param, WaClassVO.class);
	}
	
	public void updatePeriodState4IsApporve(WaClassVO vo) throws BusinessException {

		//只修改当前期间的是否需要审批标志
		StringBuffer sql = new StringBuffer();
		sql.append(" update wa_periodstate  set isapporve =? where  ");
		sql.append(" pk_periodstate in (select periodstate.pk_periodstate from wa_periodstate periodstate inner join" );
		sql.append(" wa_period period on  periodstate.pk_wa_period = period.pk_wa_period  where ( periodstate.pk_wa_class");
		sql.append(" in ( select pk_childclass from wa_inludeclass where pk_parentclass= ? ) or periodstate.pk_wa_class=? )");
		if(getDBType()==DBConsts.ORACLE || getDBType()==DBConsts.DB2 ){
			sql.append(" and period.cyear||period.cperiod >= ?)");
		}else{
			sql.append(" and period.cyear+period.cperiod >= ?)");
		}
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getIsapporve());
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear()+vo.getCperiod());
		getBaseDao().executeUpdate(sql.toString(), parameter);	
	}
	/**
	 * 修改（子方案）离职结薪的是否需要审批标志
	 * @param guoqt
	 * @param IsApporve
	 * @throws BusinessException
	 */
	public void updateWaClass4IsApporve(WaClassVO vo) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		sql.append(" update wa_waclass  set isapporve =? where  ");
		sql.append(" pk_wa_class in ( select pk_childclass from wa_inludeclass where pk_parentclass= ? )");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getIsapporve());
		parameter.addParam(vo.getPk_wa_class());
		getBaseDao().executeUpdate(sql.toString(), parameter);	
	}
	private int  getDBType(){
		return getBaseDao().getDBType();
	}
	public void deleteData4StartPeriod(WaClassVO vo) throws BusinessException {

		String sql=" pk_wa_class= ? and cyear = ? and cperiod = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getStartyear());
		parameter.addParam(vo.getStartperiod());
		getBaseDao().deleteByClause(DataVO.class, sql, parameter);
	}
	

	// 20150728 xiejie3 补丁合并，NCdp205367162  厦门港务控股集团有限公司  多次发放银行报盘第二次报盘无法成功报错 begin
	//20150512 shenliangc 修改父方案银企直连数据制单状态
	public void updateFipendflag(WaClassVO vo)throws DAOException{
		String sql = "update wa_data set fipendflag = 'N' where "
				+ "  pk_wa_class= ? and cyear = ? and cperiod = ? ";

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear());
		parameter.addParam(vo.getCperiod());
		
		getBaseDao().executeUpdate(sql, parameter);
	}
	
// {MOD:新个税补丁}
// begin
	/**
	 * 用于薪资方案未增加次数时，已经银企直联传递数据到工资清单，然后再增加发放次数时，这里在wa_cashcard中存放的pk_wa_class是对应的
	 *汇总方案的pk_wa_class，所以再去取消第一次的传递时无法取消。这里的处理是更新wa_cashcard中的pk_wa_class为子方案的pk，即对应数据
	 *的方案
	 * @author zhousze
	 * @param vo
	 * @param childvo
	 * @throws DAOException
	 * @since 2016-1-11
	 */
	public void updateWaCashcardPkwaclass(WaClassVO vo, WaClassVO childvo) throws DAOException{
		String sql = "update wa_cashcard set pk_wa_class = ? where pk_wa_class = ? "
				+ " and cyear = ? and cperiod = ? ";
		
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(childvo.getPk_wa_class());
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(childvo.getCyear());
		parameter.addParam(childvo.getCperiod());
		
		getBaseDao().executeUpdate(sql, parameter);
	}
	
//	end
	// 20150806 xiejie3 补丁合并，NCdp205398612薪资方案权限分配时还能选到已离职并且停用的用户begin
	public UserVO[] queryUserVO(String pkorg) throws DAOException {
		// 20150806 xiejie3  NCdp205463245 获取当前的时间，用于拼接sql。
		Date date=new Date();
		String time=null;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		time=df.format(date);
		
//		String sql = " select s.* from  sm_user s, hi_psnorg h where s.pk_psndoc = h.pk_psndoc and h.lastflag = 'Y' and h.indocflag = 'Y' and h.endflag = 'N' "
//				+ " and s.pk_org = ? and s.enablestate = 2 ";
		SQLParameter param2 = new SQLParameter();
		param2.addParam(time);
		param2.addParam(time);
		param2.addParam(pkorg);
		// 20150820 xiejie3 NCdp205463245  补丁的sql有问题，现改为下面所示，sm_user（用户）是有可能不绑定人员的，也就是pk_psndoc为空，所以当pk_psndoc为null时保留，当不为null时，判断绑定的人员是否离职。
		String sql2=" SELECT sm_user.*  FROM   sm_user LEFT JOIN hi_psnorg ON sm_user.pk_psndoc=hi_psnorg.pk_psndoc  " +
						"WHERE cuserid IN (SELECT cuserid  FROM sm_user_role WHERE enabledate<=? AND  " +
						"(isnull(CAST(disabledate AS CHAR),'~')='~' OR disabledate>?) AND pk_role IN  " +
						"(SELECT subjectid  FROM sm_subject_org WHERE pk_org = ? )) AND  sm_user.enablestate = 2 AND" +
						" ((hi_psnorg.lastflag = 'Y' AND hi_psnorg.indocflag = 'Y' AND hi_psnorg.endflag = 'N') OR( hi_psnorg.pk_psndoc is null))";
		UserVO[] userVOs = executeQueryVOs(sql2, param2, UserVO.class);

		return userVOs;

	}
	//end
	
}
