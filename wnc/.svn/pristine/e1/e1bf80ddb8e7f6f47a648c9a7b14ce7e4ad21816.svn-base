package nc.impl.wa.paydata;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.SQLParameter;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.uap.lfw.core.log.LfwLogger;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsndocWaDAO extends AppendBaseDAO {
	public String waTimesformu = "waAdjustDoc(%,0)";

	public boolean ischeck(WaLoginVO waLoginVO, String psnid)
			throws DAOException {
		return new WapubDAO().psnIsChecked(waLoginVO, psnid);
	}

	public PsndocWaVO[] queryAllShowResult(WaLoginVO vo, String deptpower,
			String psnclspower) throws Exception {
		UFBoolean allowed = WaAdjustParaTool
				.getPartjob_Adjmgt(vo.getPk_group());

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select distinct hi_psndoc_wadoc.pk_psndoc_sub, ");
		sqlBuffer.append("                bd_psndoc.pk_psndoc, ");
		sqlBuffer.append("                hi_psnjob.clerkcode, ");
		sqlBuffer.append("                bd_psndoc.code as psncode, ");
		sqlBuffer.append("                 "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ "  as psnname, ");

		sqlBuffer.append("                 "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ "  as deptname, ");

		sqlBuffer.append("                hi_psndoc_wadoc.pk_wa_item, ");
		sqlBuffer.append("                 "
				+ SQLHelper.getMultiLangNameColumn("wa_classitem.name")
				+ "  as itemname, ");

		sqlBuffer.append("                hi_psndoc_wadoc.begindate, ");
		sqlBuffer.append("                hi_psndoc_wadoc.enddate, ");
		sqlBuffer.append("                wa_seclv.levelname as dbname, ");
		sqlBuffer.append("                wa_prmlv.levelname as jbname, ");
		sqlBuffer
				.append("                hi_psndoc_wadoc.nmoney as nowmoney, ");

		sqlBuffer.append("                hi_psndoc_wa.basedays, ");
		sqlBuffer.append("                hi_psndoc_wa.cperiod, ");
		sqlBuffer.append("                hi_psndoc_wa.cyear, ");
		sqlBuffer.append("                hi_psndoc_wa.daywage, ");
		sqlBuffer.append("                hi_psndoc_wa.iwatype, ");
		sqlBuffer.append("                hi_psndoc_wa.nafterdays, ");
		sqlBuffer.append("                hi_psndoc_wa.naftermoney, ");
		sqlBuffer.append("                hi_psndoc_wa.nbeforemoney, ");
		sqlBuffer.append("                hi_psndoc_wa.nceforedays, ");
		sqlBuffer.append("                hi_psndoc_wa.nmoney, ");

		sqlBuffer.append("                hi_psndoc_wadoc.recordnum, ");
		sqlBuffer.append("                hi_psndoc_wadoc.lastflag, ");
		sqlBuffer.append("                wa_classitem.iflddecimal, ");
		sqlBuffer.append("                hi_psnjob.pk_psnjob, ");
		sqlBuffer.append("                hi_psndoc_wa.pk_psndoc_wa, ");
		sqlBuffer.append("                hi_psndoc_wadoc.ts as wadocts,");
		sqlBuffer.append("                wa_data.checkflag, ");
		sqlBuffer.append("                hi_psndoc_wadoc.assgid ");
		sqlBuffer.append("  from hi_psndoc_wadoc ");
		sqlBuffer
				.append(" inner join wa_data on (hi_psndoc_wadoc.pk_psndoc = wa_data.pk_psndoc  ");

		if (allowed.booleanValue()) {
			sqlBuffer.append("  and hi_psndoc_wadoc.assgid = wa_data.assgid  ");
		} else {
			sqlBuffer.append("  and hi_psndoc_wadoc.assgid = 1  ");
		}
		sqlBuffer.append(" ) ");
		sqlBuffer
				.append(" inner join hi_psnjob on (hi_psnjob.pk_psnjob = hi_psndoc_wadoc.pk_psnjob) ");

		sqlBuffer
				.append(" inner join bd_psndoc on (bd_psndoc.pk_psndoc = hi_psndoc_wadoc.pk_psndoc) ");

		sqlBuffer
				.append(" inner join org_dept on (org_dept.pk_dept = hi_psnjob.pk_dept) ");

		sqlBuffer
				.append(" inner join wa_classitem on (wa_classitem.pk_wa_item = hi_psndoc_wadoc.pk_wa_item and ");

		sqlBuffer
				.append("                            wa_classitem.pk_wa_class = '"
						+ vo.getPeriodVO().getPk_wa_class() + "' and ");

		sqlBuffer.append("                            wa_classitem.cyear = '"
				+ vo.getPeriodVO().getCyear() + "' and ");

		sqlBuffer.append("                            wa_classitem.cperiod = '"
				+ vo.getPeriodVO().getCperiod() + "' and ");

		sqlBuffer
				.append("                            wa_classitem.dr = 0 and  wa_classitem.vformula like '"
						+ waTimesformu + "') ");

		sqlBuffer
				.append("  left outer join wa_seclv on (wa_seclv.pk_wa_seclv = hi_psndoc_wadoc.pk_wa_seclv) ");

		sqlBuffer
				.append("  left outer join wa_prmlv on (wa_prmlv.pk_wa_prmlv = hi_psndoc_wadoc.pk_wa_prmlv) ");

		sqlBuffer
				.append("  left outer join hi_psndoc_wa on (((hi_psndoc_wadoc.pk_psndoc_sub = hi_psndoc_wa.pk_psndoc_sub and hi_psndoc_wadoc.ts = hi_psndoc_wa.sub_ts)");

		sqlBuffer
				.append(" or (hi_psndoc_wadoc.pk_psndoc_sub = hi_psndoc_wa.pre_sub_id and hi_psndoc_wadoc.ts = hi_psndoc_wa.pre_sub_ts ) or wa_data.checkflag='Y') and ");

		sqlBuffer
				.append("                                  hi_psndoc_wadoc.pk_psndoc =  hi_psndoc_wa.pk_psndoc and  ");

		sqlBuffer
				.append("                              hi_psndoc_wadoc.assgid =  hi_psndoc_wa.assgid and        ");

		sqlBuffer
				.append("                                  hi_psndoc_wadoc.pk_wa_item = hi_psndoc_wa.pk_wa_item and ");

		sqlBuffer
				.append("                                  hi_psndoc_wa.pk_wa_class = ? and   ");

		sqlBuffer
				.append("                                  hi_psndoc_wa.cyear = ? and  ");

		sqlBuffer
				.append("                                  hi_psndoc_wa.cperiod = ?) ");

		sqlBuffer.append(" where wa_data.pk_wa_class = ? ");
		sqlBuffer.append("   and wa_data.cyear = ? ");
		sqlBuffer.append("   and wa_data.cperiod = ? ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' ");
		sqlBuffer.append("   and hi_psndoc_wadoc.waflag = 'Y'  ");

		WaLoginContext loginContext = new WaLoginContext();
		String powerSql = WaPowerSqlHelper.getWaPowerSql(
				loginContext.getPk_group(), "wa_data", "SpecialPsnAction",
				"hi_psnjob");
		if (!StringUtils.isBlank(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
				"6007psnjob", "wadefault", "wa_data");

		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}

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

		sqlBuffer.append(" )  ");
		if (WaAdjustParaTool.getWaOrg(vo.getPk_group()).equals(
				Integer.valueOf(0))) {
			sqlBuffer.append(" and  hi_psndoc_wadoc.pk_org = wa_data.pk_org ");
		}
		sqlBuffer.append(" order by  ");
		sqlBuffer.append("          bd_psndoc.pk_psndoc, ");
		sqlBuffer.append("          hi_psndoc_wadoc.pk_wa_item, ");

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

		PsndocWaVO[] psndovVos = (PsndocWaVO[]) executeQueryVOs(
				sqlBuffer.toString(), param, PsndocWaVO.class);
		if(psndovVos==null||psndovVos.length<=0){
			return null;
		}
		for (PsndocWaVO wavo : psndovVos) {
			wavo.setNowmoney(new UFDouble(SalaryDecryptUtil.decrypt(wavo
					.getNowmoney() == null ? new Double(0) : wavo.getNowmoney()
					.doubleValue())));
			wavo.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt(wavo
					.getNmoney() == null ? new Double(0) : wavo.getNmoney()
					.doubleValue())));
			wavo.setNbeforemoney(new UFDouble(SalaryDecryptUtil.decrypt(wavo
					.getNbeforemoney() == null ? new Double(0) : wavo
					.getNbeforemoney().doubleValue())));
		}

		return psndovVos;
	}

	public void deletePsndocWa(PsndocWaVO[] psndocWas) throws DAOException {
		InSQLCreator inSQl = new InSQLCreator();
		String[] fieldnames = HRWACommonConstants.DATAB_COLUMN;
		String tableName = "wa_temp_datab";
		try {
			tableName = inSQl.insertValues(tableName, fieldnames, fieldnames,
					psndocWas);
		} catch (BusinessException e) {
			throw new DAOException(e.getMessage());
		}

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append(" delete from hi_psndoc_wa where exists (select 1 from ");
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

	public void deletePsndocWaWithoutSD(WaLoginVO vo, PsndocWaVO[] psndocWas)
			throws BusinessException {
		InSQLCreator inSQl = new InSQLCreator();
		SQLParameter param = new SQLParameter();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" delete from hi_psndoc_wa  ");
		sqlBuffer.append(" where hi_psndoc_wa.pk_wa_class = ? ");
		sqlBuffer.append(" and hi_psndoc_wa.cyear =  ? ");
		sqlBuffer.append(" and hi_psndoc_wa.cperiod = ? ");
		if (!ArrayUtils.isEmpty(psndocWas)) {
			sqlBuffer.append(" and hi_psndoc_wa.pk_psndoc_wa not in ( "
					+ inSQl.getInSQL(psndocWas, "pk_psndoc_wa") + ")");
		}

		param.addParam(vo.getPeriodVO().getPk_wa_class());
		param.addParam(vo.getPeriodVO().getCyear());
		param.addParam(vo.getPeriodVO().getCperiod());
		getBaseDao().executeUpdate(sqlBuffer.toString(), param);
	}
}