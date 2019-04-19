package nc.impl.wa.payleave;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WACLASSTYPE;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

public class PayLeaveDao extends AppendBaseDAO implements ICommonAlterName{

	/**
	 * 查询当前期间的开始日期和结束日期
	 * 
	 * @param pk_periodscheme 期间方案
	 * @param cyear 年度
	 * @param cperiod 期间
	 * @return 开始日期和结束日期
	 * @throws DAOException
	 */
	public PeriodVO queryPeriodDate(String pk_periodscheme, String cyear,
			String cperiod) throws DAOException {
		String sql = "select cstartdate,cenddate from wa_period where pk_periodscheme = ? and cyear = ? and cperiod = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_periodscheme);
		param.addParam(cyear);
		param.addParam(cperiod);
		PeriodVO vo = executeQueryVO(sql, param, PeriodVO.class);
		return vo;
	}

	/**
	 * 查询离职人员
	 * 
	 * @param pk_waclass 薪资方案
	 * @param cyear 年度
	 * @param cperiod 期间
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return 人员
	 * @throws DAOException
	 */
	public PayfileVO[] queryLeavePsnInfo(String pk_waclass, String cyear,
			String cperiod, String startDate, String endDate)
					throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append("select hi_psnjob.pk_psnjob,wa_data.*,hi_psnjob.begindate leavedate, ");
		sqlBuffer.append("       hi_psnjob.assgid, ");
		//sqlBuffer.append("       hi_psnjob.pk_psndoc, ");
		sqlBuffer.append("       hi_psnjob.clerkcode, ");
		sqlBuffer.append("       "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ " psnname, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("org_dept.name")+ " deptname, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")+ " psnclname, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("om_post.postname")+ " postname, ");
				
		//sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("om_job.jobname")+ " jobname, ");
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("org_orgs.name")+ " orgname ");
		sqlBuffer.append("  from wa_data  inner join bd_psndoc on bd_psndoc.pk_psndoc = wa_data.pk_psndoc " +
				"inner join hi_psnjob on  wa_data.pk_psnorg = hi_psnjob.pk_psnorg  and  bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlBuffer
		.append("    and hi_psnjob.trnsevent = 4 and   hi_psnjob.ismainjob='Y' "
				+ " and hi_psnjob.begindate >= ?  "
				+ " and hi_psnjob.begindate <= ?   ");
		sqlBuffer.append("  left outer join bd_psncl on bd_psncl.pk_psncl = hi_psnjob.pk_psncl ");
		//sqlBuffer.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlBuffer.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		sqlBuffer.append("  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept ");
		sqlBuffer.append("  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org ");
		sqlBuffer.append(" where wa_data.pk_wa_class = ? ");
		sqlBuffer.append("   and wa_data.cyear = ? ");
		sqlBuffer.append("   and wa_data.cperiod = ? ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' ");
		sqlBuffer.append("   and wa_data.partflag='N' ");

		sqlBuffer.append("   and wa_data.pk_psndoc not in ( select wa_data.pk_psndoc from wa_data,wa_inludeclass ");// 过滤掉离职方案中已离职的人员
		sqlBuffer.append(" where wa_data.pk_wa_class = wa_inludeclass.pk_childclass ");
		sqlBuffer.append("   and wa_inludeclass.pk_parentclass = ?  ");
		//guoqt对于进行过离职结薪，然后离职后再入职再离职的人员，再进行离职结薪，需要可以再次添加
		sqlBuffer.append("   and wa_inludeclass.cyear = ? and wa_inludeclass.cperiod = ? ");// 应过滤所有已加入到离职方案中的人员，而不是当前期间的
		sqlBuffer.append("   and wa_inludeclass.batch>100) ");
		//添加 数据权限 mizhl 2015-10-21 begin
		WaLoginContext loginContext = new WaLoginContext();
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), IHRWADataResCode.WADATA, IHRWAActionCode.SpecialPsnAction, "hi_psnjob");
		if (!StringUtils.isBlank(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		/*String powerSql = WaPowerSqlHelper.getWaPowerSql(getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "hi_psnjob");*/
		//添加 数据权限 mizhl 2015-10-21 end		
		SQLParameter param = new SQLParameter();
		param.addParam(startDate);
		param.addParam(endDate);
		param.addParam(pk_waclass);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(pk_waclass);
		//guoqt对于进行过离职结薪，然后离职后再入职再离职的人员，再进行离职结薪，需要可以再次添加
		param.addParam(cyear);
		param.addParam(cperiod);
		PayfileVO[] vos = executeQueryVOs(sqlBuffer.toString(), param, PayfileVO.class);
		return vos;
	}

	/**
	 * 查询薪资发放项目
	 * 
	 * @param context WaLoginContext
	 * @return 薪资发放项目
	 * @throws DAOException
	 */
	public WaClassItemVO[] queryUserItemInfoVO(WaLoginContext context) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag ");
		sqlBuffer.append("from wa_classitem ,wa_item ");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_item.defaultflag = 'N'");
		sqlBuffer.append(" and wa_classitem.pk_wa_item in("+ItemPowerUtil.getItemPower(context)+") ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ? ");
		sqlBuffer.append(" order by wa_classitem.idisplayseq");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(context.getPk_prnt_class());
		parameter.addParam(context.getWaYear());
		parameter.addParam(context.getWaPeriod());
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	public WaClassItemVO[] getAllClassItem(String pk_wa_class) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag,");
		sqlBuffer.append(" itempower.editflag " );
		sqlBuffer.append(" from wa_classitem ,wa_item,wa_inludeclass,wa_waclass ");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_item.defaultflag = 'N'");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = wa_inludeclass.pk_childclass ");
		sqlBuffer.append(" and wa_classitem.cyear = wa_inludeclass.cyear ");
		sqlBuffer.append(" and wa_classitem.cperiod = wa_inludeclass.cperiod ");
		sqlBuffer.append(" and wa_inludeclass.pk_parentclass = wa_waclass.pk_wa_class ");
		sqlBuffer.append(" and wa_inludeclass.cyear = wa_waclass.cyear ");
		sqlBuffer.append(" and wa_inludeclass.cperiod = wa_waclass.cperiod ");
		sqlBuffer.append(" and wa_inludeclass.batch > 100 ");
		sqlBuffer.append(" and wa_waclass.pk_wa_class = ?");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	public Map<String, String> getSelectedItem(WaLoginContext context) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey,max(wa_periodstate.checkflag) as editflag ");
		sqlBuffer.append("from wa_classitem ,wa_item,wa_inludeclass,wa_periodstate,wa_period ");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_item.defaultflag = 'N' ");
		sqlBuffer.append(" and wa_classitem.pk_wa_item in ("+ItemPowerUtil.getItemPower(context)+") ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = wa_inludeclass.pk_childclass ");
		sqlBuffer.append(" and wa_classitem.cyear = wa_inludeclass.cyear ");
		sqlBuffer.append(" and wa_classitem.cperiod = wa_inludeclass.cperiod ");
		sqlBuffer.append(" and wa_periodstate.pk_wa_class = wa_classitem.pk_wa_class ");
		sqlBuffer.append(" and wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
		sqlBuffer.append(" and wa_period.cyear = wa_classitem.cyear ");
		sqlBuffer.append(" and wa_period.cperiod = wa_classitem.cperiod ");
		sqlBuffer.append(" and wa_inludeclass.pk_parentclass = ? and wa_inludeclass.cyear = ? ");
		sqlBuffer.append(" and wa_inludeclass.cperiod = ? and wa_inludeclass.batch > 100");
		sqlBuffer.append(" group by wa_item.itemkey ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(context.getPk_prnt_class());
		parameter.addParam(context.getWaYear());
		parameter.addParam(context.getWaPeriod());
		WaClassItemVO[] vos = executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
		Map<String, String> map = new HashMap<String, String>();
		if(vos!=null&&vos.length>0)
			for(int i = 0;i<vos.length;i++){
				map.put(vos[i].getItemkey(), vos[i].getEditflag().toString());
			}
		return map;
	}

	/**
	 * 获得本期间未审核的离职发薪
	 * @param pk_waclass String
	 * @return WaClassVO
	 * @throws BusinessException
	 */
	public WaClassVO getNotCheckLeaveClass(String pk_waclass) throws BusinessException {
		String sql = "SELECT t1.* "
				+ "FROM wa_waclass t1,wa_periodstate,wa_period "
				+ "WHERE t1.pk_wa_class = wa_periodstate.pk_wa_class "
				+ "	AND wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "	AND wa_period.cyear = t1.cyear "
				+ "	AND wa_period.cperiod = t1.cperiod "
				+ "	AND wa_periodstate.checkflag = 'N' "
				+ "	AND t1.leaveflag = 'Y' "
				+ "	AND t1.pk_wa_class IN(	SELECT wa_inludeclass.pk_childclass "
				+ "							  FROM wa_inludeclass,wa_waclass t2 "
				+ "							 WHERE wa_inludeclass.pk_parentclass = t2.pk_wa_class "
				+ "                            and wa_inludeclass.pk_parentclass = ? "
				+ "                            and wa_inludeclass.cyear = t2.cyear "
				+ "                            and wa_inludeclass.cperiod = t2.cperiod "
				+ "	)";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_waclass);
		return executeQueryVO(sql, parameter, WaClassVO.class);

	}

	/**
	 * 获得本期间未发放的离职发薪
	 * @param pk_waclass String
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean isExistNotPayLeaveClass(String pk_waclass) throws BusinessException {
		String sql = "SELECT t1.pk_wa_class "
				+ "FROM wa_waclass t1,wa_periodstate,wa_period "
				+ "WHERE t1.pk_wa_class = wa_periodstate.pk_wa_class "
				+ "	AND wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "	AND wa_period.cyear = t1.cyear "
				+ "	AND wa_period.cperiod = t1.cperiod "
				+ "	AND wa_periodstate.payoffflag = 'N' "
				+ "	AND t1.leaveflag = 'Y' "
				+ "	AND t1.pk_wa_class IN(	SELECT wa_inludeclass.pk_childclass "
				+ "							  FROM wa_inludeclass,wa_waclass t2 "
				+ "							 WHERE wa_inludeclass.pk_parentclass = t2.pk_wa_class "
				+ "                            and wa_inludeclass.pk_parentclass = ? "
				+ "                            and wa_inludeclass.cyear = t2.cyear "
				+ "                            and wa_inludeclass.cperiod = t2.cperiod "
				+ "	)";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_waclass);
		return isValueExist(sql, parameter);
	}

	/**
	 * 更新状态
	 * @param context
	 * @param isPayOff
	 * @throws BusinessException
	 */
	public void updatePeriodState(WaLoginContext context, boolean isPayOff) throws BusinessException{
		String sql = " update wa_periodstate set payoffflag = '"+(isPayOff?"Y":"N")+"' where " +
				" pk_wa_class = '"+context.getPk_prnt_class()+"' and exists " + "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear =  '"+context.getCyear()+"' and wa_period.cperiod =  '"+context.getCperiod()+"' and  "
				+ "wa_periodstate.pk_wa_class =  '"+context.getPk_prnt_class()+"')";

		getBaseDao().executeUpdate(sql);
	}

	/**
	 * 更新方案离职结薪标示
	 * @param context
	 * @param isPayOff
	 * @throws BusinessException
	 */
	public void updateLeaveFlag(WaLoginContext context, boolean isleave) throws BusinessException{
		String sql = " update wa_waclass set leaveflag = '"+(isleave?"Y":"N")+"',mutipleflag = '"+(isleave?"Y":"N")+"' where"+
				" pk_wa_class = '"+context.getPk_prnt_class()+"'";
		getBaseDao().executeUpdate(sql);
		sql = " update wa_periodstate set classtype = "+WACLASSTYPE.LEAVECLASS.getValue()+" where"+
				" pk_wa_class = '"+context.getPk_prnt_class()+"' and exists " + "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear =  '"+context.getCyear()+"' and wa_period.cperiod =  '"+context.getCperiod()+"' and  "
				+ "wa_periodstate.pk_wa_class =  '"+context.getPk_prnt_class()+"')";
		getBaseDao().executeUpdate(sql);
	}

	/**
	 * 查询离职发薪数据PK
	 * 
	 * @param context WaLoginContext
	 * @param condition String
	 * @param orderCondtion String
	 * @return String[]
	 * @throws BusinessException
	 */
	public String[] queryPKSByCondition(WaLoginContext context, String condition,
			String orderCondtion) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		//guoqt注掉distinct
//		sqlBuffer.append("select 	distinct wa_data.pk_wa_data ,org_dept.code,hi_psnjob.clerkcode ");
		sqlBuffer.append("select  wa_data.pk_wa_data ");
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join wa_inludeclass on wa_data.pk_wa_class = wa_inludeclass.pk_childclass ");
		sqlBuffer.append(" and wa_data.cyear = wa_inludeclass.cyear and wa_data.cperiod = wa_inludeclass.cperiod and wa_inludeclass.batch>100 ");
		sqlBuffer.append(" inner join wa_periodstate on wa_data.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" inner join wa_period on wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
		sqlBuffer.append(" and wa_period.cyear = wa_data.cyear and wa_period.cperiod = wa_data.cperiod");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		//		sqlBuffer.append(" inner join hi_psnjob leavejob on   wa_data.PK_PSNORG = leavejob.PK_PSNORG AND wa_data.pk_psndoc = leavejob.pk_psndoc");
		//		sqlBuffer.append("   and leavejob.lastflag = 'Y'  ");
		//		sqlBuffer.append("   and leavejob.ismainjob = 'Y'  ");
		//		sqlBuffer.append("   and leavejob.trnsevent  in(4,5) ");
		sqlBuffer.append(" left join org_orgs on hi_psnjob.pk_org = org_orgs.pk_org ");
		sqlBuffer.append(" left join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl  ");
		sqlBuffer.append("  where wa_inludeclass.pk_parentclass = '" + context.getWaLoginVO().getPk_prnt_class() + "' "); // 1
		sqlBuffer.append("   and wa_data.cyear = '" + context.getWaLoginVO().getPeriodVO().getCyear() + "' "); // 2
		sqlBuffer.append("   and wa_data.cperiod = '" + context.getWaLoginVO().getPeriodVO().getCperiod() + "' "); // 3
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}
		DataVO[] vos =  executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
		String[] pks = new String[0];
		if(vos!=null){
			pks = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				pks[i] = vos[i].getPk_wa_data();
			}
		}
		return pks;
	}

	/**
	 * 查询离职发薪数据
	 * @param condition String
	 * @param orderCondtion String
	 * @return DataVO[]
	 * @throws BusinessException
	 */
	public DataVO[] queryByPKSCondition(String condition, String orderCondtion) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  "+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")+ "  " + PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("org_dept.name")+ "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("org_orgs.name")+ "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")+ "  " + PLSNAME + ", "); // 5
		
		//zhaochxs税改纳税申报组织
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("taxorg.name") + " taxorgname, "); //
		
		// 2015-09-15 zhosuze NCdp205486278 离职结薪中数据没有带出任职组织等信息 begin
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("financeorg.name")+ "  " + FINANCEORG + ", "); // 3
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("financedept.name")+ "  " + FINANCEDEPT + ", "); // 3
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("liabilityorg.name")+ "  " + LIABILITYORG + ", "); // 3
		sqlBuffer.append("        "+ SQLHelper.getMultiLangNameColumn("liabilitydept.name")+ "  " + LIABILITYDEPT + ", "); // 3
		// end
		sqlBuffer.append("       om_job.jobname, "); // 6
		sqlBuffer.append("       om_post.postname,leavejob.begindate as leavedate,wa_periodstate.payoffflag as payflag, "); // 6
		sqlBuffer.append("       wa_data.* "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join wa_periodstate on wa_data.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" inner join wa_period on wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
		sqlBuffer.append(" and wa_period.cyear = wa_data.cyear and wa_period.cperiod = wa_data.cperiod");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join hi_psnjob leavejob on   wa_data.PK_PSNORG = leavejob.PK_PSNORG AND wa_data.pk_psndoc = leavejob.pk_psndoc");
		//		sqlBuffer.append("   and leavejob.lastflag = 'Y'  ");
		sqlBuffer.append("   and leavejob.ismainjob = 'Y'  ");
		sqlBuffer.append("   and leavejob.trnsevent  = 4 ");
		sqlBuffer.append(" left join org_orgs on hi_psnjob.pk_org = org_orgs.pk_org ");
		sqlBuffer.append(" left join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		// 2015-09-15 zhosuze NCdp205486278 离职结薪中数据没有带出任职组织等信息 begin
		sqlBuffer.append("  left join org_orgs financeorg on wa_data.pk_financeorg = financeorg.pk_org ");
		sqlBuffer.append("  left join org_dept financedept on wa_data.pk_financedept = financedept.pk_dept ");
		sqlBuffer.append("  left join org_orgs liabilityorg on wa_data.pk_liabilityorg = liabilityorg.pk_org ");
		sqlBuffer.append("  left join org_dept liabilitydept on wa_data.pk_liabilitydept = liabilitydept.pk_dept ");
		
		//zhaochxs税改纳税申报组织
		sqlBuffer.append("  left join org_orgs taxorg on wa_data.taxorg = taxorg.pk_org ");
		
		// end
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append("  wa_data.pk_wa_data in ("+ condition +")");
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	/**
	 * 根据父方案查询最新期间所有子方案
	 * @param pk_waclass String
	 * @return WaInludeclassVO
	 * @throws DAOException
	 */
	public WaInludeclassVO queryNewLeaveClasses(String pk_waclass,String cyear,String cperiod) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select t1.* ");
		sqlB.append("  from wa_inludeclass t1,wa_waclass  ");
		sqlB.append(" where wa_waclass.pk_wa_class =? ");
		sqlB.append("   and t1.pk_parentclass = wa_waclass.pk_wa_class ");
		sqlB.append("   and t1.cyear = ? ");
		sqlB.append("	and t1.cperiod = ? ");
		sqlB.append("   and t1.batch>100 ");
		sqlB.append("   and t1.batch = (select max(batch) from wa_inludeclass t2 where t2.pk_parentclass = wa_waclass.pk_wa_class ");
		sqlB.append("   and t2.cyear = ? ");
		sqlB.append("	and t2.cperiod = ? )");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_waclass);
		param.addParam(cyear);
		param.addParam(cperiod);
		param.addParam(cyear);
		param.addParam(cperiod);
		return executeQueryVO(sqlB.toString(), param, WaInludeclassVO.class);
	}

	/**
	 * 从正常发薪中删除未审核的离职人员
	 * @param vo WaClassVO
	 * @throws BusinessException
	 */
	public void deleteFromNormalClass(WaClassVO vo,PayfileVO[] psns) throws BusinessException{

		InSQLCreator inSQLCreator = new InSQLCreator();

		try
		{
			String deleteSQL = "delete from wa_data "
					+ "where pk_psndoc in ("+inSQLCreator.getInSQL(psns,PayfileVO.PK_PSNDOC)+") and "
					+ "pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = ? and cyear = ? and cperiod = ? ) "
					+ "and cyear = ? and cperiod = ? and checkflag = 'N' ";
			SQLParameter param = new SQLParameter();
			param.addParam(vo.getPk_prnt_class());
			param.addParam(vo.getCyear());
			param.addParam(vo.getCperiod());
			param.addParam(vo.getCyear());
			param.addParam(vo.getCperiod());
			getBaseDao().executeUpdate(deleteSQL, param);


		}
		finally
		{
			inSQLCreator.clear();
		}



	}

	/**
	 * 删除薪资项目
	 * @param childVO
	 * @param keys
	 * @throws BusinessException
	 */
	public void deleteClassItemByKey(WaClassVO childVO, Object[] keys)
			throws BusinessException {
		if(keys==null||keys.length==0){
			return;
		}
		String[] keystemp = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			keystemp[i] = keys[i].toString();
		}

		InSQLCreator inSQLCreator = new InSQLCreator();

		try
		{

			String sql = "delete from wa_classitem where pk_wa_class = ? and cyear = ? and cperiod = ? "
					+ "and itemkey in (" + inSQLCreator.getInSQL(keystemp) + ")";
			SQLParameter param = new SQLParameter();
			param.addParam(childVO.getPk_wa_class());
			param.addParam(childVO.getCyear());
			param.addParam(childVO.getCperiod());
			getBaseDao().executeUpdate(sql, param);

		}
		finally
		{
			inSQLCreator.clear();
		}


	}

	/**
	 * 根据父方案查询最新期间所有子方案
	 * @param pk_waclass String
	 * @return WaInludeclassVO
	 * @throws DAOException
	 */
	public boolean hasLeaveClasses(WaLoginVO waLoginVO) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select t1.pk_includeclass  ");
		sqlB.append("  from wa_inludeclass t1,wa_waclass  ");
		sqlB.append(" where wa_waclass.pk_wa_class =? ");
		sqlB.append("   and t1.pk_parentclass = wa_waclass.pk_wa_class ");
		sqlB.append("   and t1.cyear = ? ");
		sqlB.append("	and t1.cperiod = ? ");
		sqlB.append("   and t1.batch>100 ");
		SQLParameter param = new SQLParameter();
		param.addParam(waLoginVO.getPk_wa_class());
		param.addParam(waLoginVO.getPeriodVO().getCyear());
		param.addParam(waLoginVO.getPeriodVO().getCperiod());
		return isValueExist(sqlB.toString(), param);
	}

	public boolean isNormalClassCheck(String sql) throws BusinessException {
		return isValueExist(sql);
	}
}
