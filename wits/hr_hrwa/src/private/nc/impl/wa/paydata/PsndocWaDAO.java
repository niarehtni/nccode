package nc.impl.wa.paydata;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * PsndocWa的DMO类。
 * 
 * 创建日期：(2004-6-9)
 * 
 * @author：
 */
public class PsndocWaDAO extends AppendBaseDAO {

	public String waTimesformu = "waAdjustDoc(%,0)";

	public PsndocWaDAO() {
		super();
	}

	/**
	 * 判断该人员是否已经审核
	 * 
	 * @author liangxr on 2010-5-26
	 * @param waLoginVO
	 * @param psnid
	 * @return
	 * @throws DAOException
	 */
	public boolean ischeck(WaLoginVO waLoginVO, String psnid) throws DAOException {
		return new WapubDAO().psnIsChecked(waLoginVO, psnid);
	}

	/**
	 * 时点薪资变动记录查询
	 * 
	 * @author liangxr on 2010-5-26
	 * @param vo
	 * @param deptpower
	 * @param psnclspower
	 * @return
	 * @throws Exception
	 */
	public PsndocWaVO[] queryAllShowResult(WaLoginVO vo, String deptpower, String psnclspower) throws Exception {
		UFBoolean allowed = WaAdjustParaTool.getPartjob_Adjmgt(vo.getPk_group());
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct hi_psndoc_wadoc.pk_psndoc_sub, "); // 1
		sqlBuffer.append("                bd_psndoc.pk_psndoc, "); // 2
		sqlBuffer.append("                hi_psnjob.clerkcode, "); // 2
		sqlBuffer.append("                bd_psndoc.code as psncode, "); // 3
		sqlBuffer.append("                 " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  as psnname, "); // 4
		sqlBuffer.append("                 " + SQLHelper.getMultiLangNameColumn("org_dept.name") + "  as deptname, "); // 5
		sqlBuffer.append("                hi_psndoc_wadoc.pk_wa_item, "); // 6
		sqlBuffer.append("                 " + SQLHelper.getMultiLangNameColumn("wa_classitem.name")
				+ "  as itemname, "); // 7
		sqlBuffer.append("                hi_psndoc_wadoc.begindate, "); // 8
		sqlBuffer.append("                hi_psndoc_wadoc.enddate, "); // 8
		sqlBuffer.append("                wa_seclv.levelname as dbname, "); // 9
		sqlBuffer.append("                wa_prmlv.levelname as jbname, "); // 10
		sqlBuffer.append("                hi_psndoc_wadoc.nmoney as nowmoney, "); // 11
		sqlBuffer.append("                hi_psndoc_wa.basedays, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.cperiod, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.cyear, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.daywage, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.iwatype, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.nafterdays, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.naftermoney, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.nbeforemoney, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.nceforedays, "); // 12
		sqlBuffer.append("                hi_psndoc_wa.nmoney, "); // 12

		sqlBuffer.append("                hi_psndoc_wadoc.recordnum, "); // 26
		sqlBuffer.append("                hi_psndoc_wadoc.lastflag, "); // 27
		sqlBuffer.append("                wa_classitem.iflddecimal, "); // 27
		sqlBuffer.append("                hi_psnjob.pk_psnjob, "); // 28
		sqlBuffer.append("                hi_psndoc_wa.pk_psndoc_wa, "); // 29
		sqlBuffer.append("                hi_psndoc_wadoc.ts as wadocts,"); // 30
		sqlBuffer.append("                wa_data.checkflag, ");
		sqlBuffer.append("                hi_psndoc_wadoc.assgid ");
		sqlBuffer.append("  from hi_psndoc_wadoc ");
		sqlBuffer.append(" inner join wa_data on (hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc  ");
		if (allowed.booleanValue()) {
			sqlBuffer.append("  and hi_psndoc_wadoc.assgid = wa_data.assgid  ");
		} else {
			sqlBuffer.append("  and hi_psndoc_wadoc.assgid = 1  ");
		}
		sqlBuffer.append(" ) ");
		sqlBuffer.append(" inner join hi_psnjob on (hi_psnjob.pk_psnjob = hi_psndoc_wadoc.pk_psnjob) ");
		sqlBuffer.append(" inner join bd_psndoc on (bd_psndoc.pk_psndoc = hi_psndoc_wadoc.pk_psndoc) ");
		sqlBuffer.append(" inner join org_dept on (org_dept.pk_dept = hi_psnjob.pk_dept) ");
		sqlBuffer.append(" inner join wa_classitem on (wa_classitem.pk_wa_item = hi_psndoc_wadoc.pk_wa_item and ");
		sqlBuffer.append("                            wa_classitem.pk_wa_class = '" + vo.getPeriodVO().getPk_wa_class()
				+ "' and ");
		sqlBuffer.append("                            wa_classitem.cyear = '" + vo.getPeriodVO().getCyear() + "' and ");
		sqlBuffer.append("                            wa_classitem.cperiod = '" + vo.getPeriodVO().getCperiod()
				+ "' and ");
		sqlBuffer.append("                            wa_classitem.dr = 0 and  wa_classitem.vformula like '"
				+ waTimesformu + "') ");
		// sqlBuffer
		// .append("  left outer join wa_criterion on (wa_criterion.pk_wa_crt = hi_psndoc_wadoc.pk_wa_crt) ");
		sqlBuffer.append("  left outer join wa_seclv on (wa_seclv.pk_wa_seclv = hi_psndoc_wadoc.pk_wa_seclv) ");
		sqlBuffer.append("  left outer join wa_prmlv on (wa_prmlv.pk_wa_prmlv = hi_psndoc_wadoc.pk_wa_prmlv) ");
		sqlBuffer
				.append("  left outer join hi_psndoc_wa on (((hi_psndoc_wadoc.pk_psndoc_sub = hi_psndoc_wa.pk_psndoc_sub and hi_psndoc_wadoc.ts = hi_psndoc_wa.sub_ts)");
		sqlBuffer
				.append(" or (hi_psndoc_wadoc.pk_psndoc_sub = hi_psndoc_wa.pre_sub_id and hi_psndoc_wadoc.ts = hi_psndoc_wa.pre_sub_ts ) or wa_data.checkflag='Y') and ");
		sqlBuffer.append("                                  hi_psndoc_wadoc.pk_psndoc =  hi_psndoc_wa.pk_psndoc and  ");
		sqlBuffer.append("                              hi_psndoc_wadoc.assgid =  hi_psndoc_wa.assgid and        ");
		sqlBuffer.append("                                  hi_psndoc_wadoc.pk_wa_item = hi_psndoc_wa.pk_wa_item and ");
		sqlBuffer.append("                                  hi_psndoc_wa.pk_wa_class = ? and   ");
		sqlBuffer.append("                                  hi_psndoc_wa.cyear = ? and  ");
		sqlBuffer.append("                                  hi_psndoc_wa.cperiod = ?) ");
		sqlBuffer.append(" where wa_data.pk_wa_class = ? ");
		sqlBuffer.append("   and wa_data.cyear = ? ");
		sqlBuffer.append("   and wa_data.cperiod = ? ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' ");// 没有停发的
		sqlBuffer.append("   and hi_psndoc_wadoc.waflag = 'Y'  ");// 定调资记录已经发布的

		// NCdp205499903 薪资发放/离职结薪的时点薪资的人员权限没有进行控制 mizhl 2015-10-26 begin
		// 数据权限
		WaLoginContext loginContext = new WaLoginContext();
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), IHRWADataResCode.WADATA,
				IHRWAActionCode.SpecialPsnAction, "hi_psnjob");
		if (!StringUtils.isBlank(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		// NCdp205499903 薪资发放/离职结薪的时点薪资的人员权限没有进行控制 mizhl 2015-10-26 end

		// /*期间内生效记录 */ 与 /* 不生效的最小记录 */
		sqlBuffer.append(" AND ");
		sqlBuffer
				.append(" (  (  hi_psndoc_wadoc.RECORDNUM = (  select min(RECORDNUM) from hi_psndoc_wadoc tempdoc where  hi_psndoc_wadoc.pk_psndoc = tempdoc.pk_psndoc and ");
		sqlBuffer.append(" hi_psndoc_wadoc.assgid = tempdoc.assgid  and  ");
		sqlBuffer
				.append(" hi_psndoc_wadoc.PK_WA_ITEM = tempdoc.PK_WA_ITEM   and tempdoc.begindate > ? AND tempdoc.begindate <= ? and ( tempdoc.enddate >= ? or "
						+ SQLHelper.getNullSql("tempdoc.enddate") + ")) )  ");
		sqlBuffer
				.append(" or (  hi_psndoc_wadoc.RECORDNUM = (  select min(RECORDNUM) from hi_psndoc_wadoc tempdoc2 where  hi_psndoc_wadoc.pk_psndoc = tempdoc2.pk_psndoc and ");
		sqlBuffer.append(" hi_psndoc_wadoc.assgid = tempdoc2.assgid and  ");
		sqlBuffer
				.append(" hi_psndoc_wadoc.PK_WA_ITEM = tempdoc2.PK_WA_ITEM   and tempdoc2.enddate >= ? AND tempdoc2.enddate < ?) )");
		// sqlBuffer.append(" or (  hi_psndoc_wadoc.RECORDNUM = (  select min(RECORDNUM)+1 from hi_psndoc_wadoc tempdoc2 where  hi_psndoc_wadoc.pk_psndoc = tempdoc2.pk_psndoc  and  hi_psndoc_wadoc.PK_WA_ITEM = tempdoc2.PK_WA_ITEM   and tempdoc2.begindate = ? and ( tempdoc2.enddate >= ? or "+SQLHelper.getNullSql("tempdoc2.enddate")+" )) and hi_psndoc_wadoc.enddate = ? )");

		sqlBuffer.append(" )  ");
		if (WaAdjustParaTool.getWaOrg(vo.getPk_group()).equals(0)) {
			sqlBuffer.append(" and  hi_psndoc_wadoc.pk_org = wa_data.pk_org ");
		}
		sqlBuffer.append(" order by  ");
		sqlBuffer.append("          bd_psndoc.pk_psndoc, ");
		sqlBuffer.append("          hi_psndoc_wadoc.pk_wa_item, ");
		// sqlBuffer.append("          hi_psndoc_wadoc.lastflag desc, ");
		sqlBuffer.append("          hi_psndoc_wadoc.recordnum, ");
		sqlBuffer.append(SQLHelper.getMultiLangNameColumn("org_dept.name"));

		SQLParameter param = new SQLParameter();
		param.addParam(vo.getPeriodVO().getPk_wa_class());
		param.addParam(vo.getPeriodVO().getCyear());
		param.addParam(vo.getPeriodVO().getCperiod());

		param.addParam(vo.getPeriodVO().getPk_wa_class());
		param.addParam(vo.getPeriodVO().getCyear());
		param.addParam(vo.getPeriodVO().getCperiod());

		param.addParam(vo.getPeriodVO().getCstartdate().toString());
		param.addParam(vo.getPeriodVO().getCenddate().toString());
		param.addParam(vo.getPeriodVO().getCenddate().toString());

		param.addParam(vo.getPeriodVO().getCstartdate().toString());
		param.addParam(vo.getPeriodVO().getCenddate().toString());

		// param.addParam(vo.getPeriodVO().getCstartdate().toString());
		// param.addParam(vo.getPeriodVO().getCenddate().toString());
		// param.addParam(vo.getPeriodVO().getCstartdate().getDateBefore(1).toString());

		// executeQueryVOs(sqlBuffer.toString(), param, AppendableVO.class);
		return executeQueryVOs(sqlBuffer.toString(), param, PsndocWaVO.class);

	}

	/**
	 * 判断时点薪资是否都已计算
	 * 
	 * @author liangxr on 2010-7-15
	 * @param vo
	 * @param deptpower
	 * @param psnclspower
	 * @return
	 * @throws DAOException
	 */
	/*
	 * public boolean isExistUnCaculatePsn(WaLoginVO vo, String deptpower,
	 * String psnclspower) throws DAOException {
	 * 
	 * StringBuffer sqlBuffer = new StringBuffer();
	 * sqlBuffer.append("select hi_psndoc_wadoc.pk_psndoc_sub "); // 1
	 * sqlBuffer.append(
	 * "  from hi_psndoc_wadoc, wa_data, hi_psnjob,bd_psndoc, org_dept, wa_classitem "
	 * );
	 * sqlBuffer.append(" where hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc "
	 * );
	 * sqlBuffer.append("   and hi_psnjob.pk_psnjob = hi_psndoc_wadoc.pk_psnjob "
	 * ); sqlBuffer.append("   and hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc ");
	 * sqlBuffer.append("   and org_dept.pk_dept = org_dept.pk_dept ");
	 * sqlBuffer
	 * .append("   and wa_classitem.pk_wa_item = hi_psndoc_wadoc.pk_wa_item ");
	 * sqlBuffer
	 * .append("   and wa_classitem.pk_wa_class = wa_data.pk_wa_class ");
	 * sqlBuffer.append("   and wa_classitem.cyear = wa_data.cyear ");
	 * sqlBuffer.append("   and wa_classitem.cperiod = wa_data.cperiod ");
	 * sqlBuffer
	 * .append("   and wa_classitem.ifromflag = '"+FromEnumVO.WAORTHER.value
	 * ()+"'  and  wa_classitem.vformula like '"+waTimesformu+"' ");
	 * sqlBuffer.append("   and ((hi_psndoc_wadoc.begindate >= '" +
	 * vo.getPeriodVO().getCstartdate() + "' ");
	 * sqlBuffer.append("   and hi_psndoc_wadoc.begindate <= '" +
	 * vo.getPeriodVO().getCenddate() + "' )");
	 * sqlBuffer.append("   or (hi_psndoc_wadoc.enddate >= '" +
	 * vo.getPeriodVO().getCstartdate() + "' ");
	 * sqlBuffer.append("   and hi_psndoc_wadoc.enddate <= '" +
	 * vo.getPeriodVO().getCenddate() + "' ))");
	 * sqlBuffer.append("   and wa_data.pk_wa_class = '" + vo.getPk_wa_class() +
	 * "' "); sqlBuffer.append("   and wa_data.cyear = '" +
	 * vo.getPeriodVO().getCyear() + "' ");
	 * sqlBuffer.append("   and wa_data.cperiod = '" +
	 * vo.getPeriodVO().getCperiod() + "' ");
	 * sqlBuffer.append("   and wa_data.stopflag = 'N' ");
	 * sqlBuffer.append("   and hi_psndoc_wadoc.waflag = 'Y' ");
	 * sqlBuffer.append("   and hi_psndoc_wadoc.lastflag = 'Y' ");
	 * sqlBuffer.append("   and hi_psndoc_wadoc.pk_psndoc_sub not in ");
	 * sqlBuffer.append("       (select hi_psndoc_wa.pk_psndoc_sub ");
	 * sqlBuffer.append("          from hi_psndoc_wa "); sqlBuffer.append(
	 * "         where hi_psndoc_wadoc.pk_psndoc = hi_psndoc_wa.pk_psndoc ");
	 * sqlBuffer
	 * .append("         and hi_psndoc_wadoc.assgid = hi_psndoc_wa.assgid ");
	 * sqlBuffer.append(
	 * "           and hi_psndoc_wadoc.pk_wa_item = hi_psndoc_wa.pk_wa_item ");
	 * sqlBuffer
	 * .append("           and hi_psndoc_wa.pk_wa_class = wa_data.pk_wa_class "
	 * );
	 * sqlBuffer.append("           and hi_psndoc_wa.cyear = wa_data.cyear ");
	 * sqlBuffer
	 * .append("           and hi_psndoc_wa.cperiod = wa_data.cperiod) ");
	 * 
	 * return isValueExist(sqlBuffer.toString()); }
	 */

	public void deletePsndocWa(PsndocWaVO[] psndocWas) throws DAOException {
		if (psndocWas == null || psndocWas.length == 0) {
			return;
		}

		InSQLCreator inSQl = new InSQLCreator();
		String[] fieldnames = HRWACommonConstants.DATAB_COLUMN;
		String tableName = HRWACommonConstants.WA_TEMP_DATAB;
		try {
			// MOD 防止tableName為空，不回寫tableName
			// ssx modified on 2019-02-23
			tableName = inSQl.insertValues(tableName, fieldnames, fieldnames, psndocWas);
			//
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage());
		}

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" delete from hi_psndoc_wa where exists (select 1 from ");
		sqlBuffer.append(tableName);
		sqlBuffer.append(" where hi_psndoc_wa.pk_psndoc = ");
		sqlBuffer.append(tableName);
		sqlBuffer.append(".pk_psndoc");
		sqlBuffer.append(" and hi_psndoc_wa.pk_wa_item = ");
		sqlBuffer.append(tableName);
		sqlBuffer.append(".pk_wa_item");
		sqlBuffer.append(" and hi_psndoc_wa.pk_wa_class = ");
		sqlBuffer.append(tableName);
		sqlBuffer.append(".pk_wa_class");
		sqlBuffer.append(" and hi_psndoc_wa.cyear =  ");
		sqlBuffer.append(tableName);
		sqlBuffer.append(".cyear");
		sqlBuffer.append(" and hi_psndoc_wa.cperiod =  ");
		sqlBuffer.append(tableName);
		sqlBuffer.append(".cperiod)");
		getBaseDao().executeUpdate(sqlBuffer.toString());
	}

	/*
	 * 删除不在时点薪资计算范围内的时点薪资结果
	 */
	public void deletePsndocWaWithoutSD(WaLoginVO vo, PsndocWaVO[] psndocWas) throws BusinessException {
		InSQLCreator inSQl = new InSQLCreator();
		SQLParameter param = new SQLParameter();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" delete from hi_psndoc_wa  ");
		sqlBuffer.append(" where hi_psndoc_wa.pk_wa_class = ? ");
		sqlBuffer.append(" and hi_psndoc_wa.cyear =  ? ");
		sqlBuffer.append(" and hi_psndoc_wa.cperiod = ? ");
		if (!ArrayUtils.isEmpty(psndocWas)) {
			sqlBuffer.append(" and hi_psndoc_wa.pk_psndoc_wa not in ( "
					+ inSQl.getInSQL(psndocWas, PsndocWaVO.PK_PSNDOC_WA) + ")");
		}
		param.addParam(vo.getPeriodVO().getPk_wa_class());
		param.addParam(vo.getPeriodVO().getCyear());
		param.addParam(vo.getPeriodVO().getCperiod());
		getBaseDao().executeUpdate(sqlBuffer.toString(), param);
	}

	/*
	 * public String getPrePeriodEnddate(WaLoginVO vo) throws BusinessException
	 * {
	 * 
	 * StringBuffer sqlB = new StringBuffer();
	 * sqlB.append("select max(cenddate) "); // 1
	 * sqlB.append("  from wa_periodstate ");
	 * sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
	 * sqlB.append("                         wa_period.pk_wa_period) ");
	 * sqlB.append(" where  pk_wa_class = '" + vo.getPk_wa_class() + "' ");
	 * sqlB.append("   and (cyear || cperiod) < '" + vo.getCyear() +
	 * vo.getCperiod() + "' "); return executeQueryVO(sqlB.toString(),
	 * String.class); }
	 */

}
