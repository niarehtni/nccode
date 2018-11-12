package nc.impl.wa.paydata.precacu;

import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.persist.designer.vo.ColumnVO;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hr.itemsource.ItemPropertyConst;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsnInfoPreDefaultExcutor extends AbstractFormulaExecutor {

    /**
     * @author zhangg on 2010-6-21
     * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object,
     *      nc.vo.wa.pub.WaLoginContext)
     */
    @Override
    public void excute(Object argument, WaLoginContext context) throws BusinessException {
	String[] arguments = (String[]) argument;
	String tablename = arguments[0];
	String table_Fld = arguments[1];
	String refcodeORname = arguments[2];
	String date = arguments[3];
	String ref_table = arguments[4];
	String ref_pk_field = arguments[5];
	String conditon = arguments[6];
	// 是否按法人组织汇总 #21106 yejk 2018-09-13
	String isSum = "0";
	if (!"null".equals(arguments[7])) {
	    isSum = arguments[7];
	}

	if ("1".equals(isSum)) {// 按法人组织汇总 tablename table_Fld
	    groupByTableField(context, tablename, table_Fld, nc.vo.hi.psndoc.PsnJobVO.PK_ORG, "0001Z710000000000ZO4",
		    false); // 0001Z710000000000ZO4=法人组织参照
	} else if ("2".equals(isSum)) {// 按人力资源组织汇总
	    groupByTableField(context, tablename, table_Fld, nc.vo.hi.psndoc.PsnJobVO.PK_HRORG, "0001Z01000000001BVPV",
		    false); // 0001Z01000000001BVPV=人力资源组织参照
	} else if ("3".equals(isSum)) {// 全部汇总
	    groupByTableField(context, tablename, table_Fld, nc.vo.hi.psndoc.PsnJobVO.PK_HRORG, "0001Z01000000001BVPV",
		    true);
	} else {

	    Object object = context.getInitData();

	    String coloumn = "char_value";
	    if (object != null && object instanceof WaClassItemVO) {
		WaClassItemVO itemVO = (WaClassItemVO) object;
		coloumn = DataVOUtils.isDigitsAttribute(itemVO.getItemkey()) ? "cacu_value" : "char_value";

	    }

	    // (1) 是取最新记录还是限定日期
	    // (2) 取主职还是兼职
	    String l_datewhere = "";

	    if (date == null || !isUseDate(tablename)) {
		// 能力素质子集单独处理
		// if(CapaVO.getDefaultTableName().equalsIgnoreCase(tablename))
		// l_datewhere = " and lastflag = 'Y'  ";
		// else
		if (!StringUtils.isBlank(conditon) && conditon.equals(ItemPropertyConst.FULLTIME_INFSET_PK)) {
		    // l_datewhere =
		    // " and recordnum = ( select min(a.recordnum) from hi_psnjob a where a.trnsevent<>4 and a.pk_psndoc = hi_psnjob.pk_psndoc and a.ismainjob = hi_psnjob.ismainjob)";
		    // guoqt要么取当前薪资档案里的工作记录，要么取离职那条工作记录的前一条工作记录
		    l_datewhere = " and (pk_psnjob = (select pk_psnjob from wa_data where pk_wa_data = wa_cacu_data.pk_wa_data) or recordnum in ( select (a.recordnum)+1  from hi_psnjob a where a.trnsevent=4  and a.pk_psndoc = hi_psnjob.pk_psndoc and a.ismainjob = hi_psnjob.ismainjob) ) ";
		} else {
		    // 20151026 shenliangc
		    // NCdp205513626社保档案项目为颁证日期=(其他证书.首次颁证日期,@当前日期@)时，缴交计算不了。
		    // begin
		    // 原因是信息集hi_psndoc_traincertfile表中没有下面两个字段，查询直接报错。
		    // 完美的解决方法应该是判断此时的表中是否有这两个字段确定是否拼接这段SQL。
		    if (hasNewestRecord(tablename)) {
			l_datewhere = " and recordnum = 0 and lastflag = 'Y'  "; // 默认取最新记录;
		    }
		    // 20151026 shenliangc
		    // NCdp205513626社保档案项目为颁证日期=(其他证书.首次颁证日期,@当前日期@)时，缴交计算不了。
		    // end
		    // shenliangc 2014-04-18 添加
		    // 为解决杭州萧山机场编号NCdp204987503需求问题：
		    // 发放项目数据来源“其他系统——人员工作信息子集”不取最新工作记录数据，
		    // 而是取加入到薪资档案中的那条工作记录信息。
		    // 只有工作记录才有pk_psnjob，其他子集只有recordnum和lastflag
		    if (tablename.equals("hi_psnjob")) {
			l_datewhere = l_datewhere
				.replaceAll("recordnum = 0 and lastflag = 'Y'",
					"pk_psnjob = (select pk_psnjob from wa_data where pk_wa_data = wa_cacu_data.pk_wa_data)");
		    }
		}
	    } else {
		// 限定日期，不再取最新记录
		l_datewhere = " and  (( " + tablename + ".begindate <= '" + date + "' and " + tablename
			+ ".enddate >= '" + date + "' )" + " or ( " + tablename + ".begindate <= '" + date + "' and ("
			+ tablename + ".enddate='~' or " + tablename + ".enddate is null " + ") ) ) ";
	    }

	    // 兼职记录：conditon 不为空 不是“null”
	    if (!StringUtils.isBlank(conditon) && conditon.equals(ItemPropertyConst.PARTTIME_INFSET_PK)) {
		l_datewhere = l_datewhere + " and  ismainjob = 'N' "; // 取兼职记录

		if (date == null) {
		    // 如果日期为空。 最新的兼职记录有可能是多条，取创建时间晚的
		    l_datewhere = l_datewhere + " and ( " + tablename
			    + ".creationtime =( select	 max(creationtime)	from	" + tablename + " 	where	" + tablename
			    + ".pk_psndoc = wa_cacu_data.pk_psndoc and " + tablename + ".recordnum = 0 and "
			    + tablename + ".lastflag = 'Y' and	" + tablename + ".ismainjob = 'N') )";
		}
	    } else if (tablename.equals("hi_psnjob")) {
		// 主职
		l_datewhere = l_datewhere + " and  ismainjob = 'Y' ";
	    }

	    // guoqt
	    // NCZX：NCdp204970905离职后换组织入职取工作记录信息可能取到前一个组织的信息项，没有加任职组织限制，同时信息项为空的记录不能取
	    if (tablename.equals("hi_psnjob")) {
		l_datewhere = l_datewhere + " and " + tablename
			+ ".pk_psnorg in ( select pk_psnorg from wa_data where pk_wa_data = wa_cacu_data.pk_wa_data ) ";
	    }

	    // 如果参照不为空，则需要取参照中的值
	    // select code_name from ref_table where ref_table.ref_item_code =
	    // ()

	    // guoqt
	    // NCZX：NCdp204970905离职后换组织入职取工作记录信息可能取到前一个组织的信息项，没有加任职组织限制，同时信息项为空的记录不能取
	    String sql = "select max(" + table_Fld
		    + ") from "
		    + tablename
		    // + " where isnull(" + table_Fld + ",'~')<> '~' and " +
		    // tablename
		    // guoqt 取子集数值型字段非空处理
		    + " where (" + table_Fld + ") is not null and (" + table_Fld + ")  <>'~' and " + tablename
		    + ".pk_psndoc = wa_cacu_data.pk_psndoc ";
	    // guoqt金额不能用<>'~'，会报错
	    if (coloumn.equals("cacu_value")) {
		sql = sql.replace("and (" + table_Fld + ")  <>'~'", "");
	    }
	    if (!tablename.equalsIgnoreCase("bd_psndoc")) {
		sql += l_datewhere; // 其他子集，则加入条件
	    }

	    // { table_code, table_Fld, refcodeORname, l_date, ref_table,
	    // ref_pk_field };
	    String sql2 = "";
	    if (!StringUtils.isBlank(ref_table) && !"null".equals(ref_table)) {
		sql2 = " select "
			+ (refcodeORname.equals("name") || refcodeORname.equals("postname") ? SQLHelper
				.getMultiLangNameColumn(ref_table + "." + refcodeORname) : ref_table + "."
				+ refcodeORname) + " from  " + ref_table + " where " + ref_table + "." + ref_pk_field
			+ " = (" + sql + ") ";
	    } else {
		if (tablename.equalsIgnoreCase("bd_psndoc") && table_Fld.equalsIgnoreCase("name")) {
		    sql2 = "select max(" + SQLHelper.getMultiLangNameColumn(table_Fld) + ") from " + tablename
			    + " where " + tablename + ".pk_psndoc = wa_cacu_data.pk_psndoc ";
		} else {
		    sql2 = sql;
		}
	    }

	    sql = " update wa_cacu_data set " + coloumn + " = (" + sql2 + ") where  " + "pk_wa_class = '"
		    + context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser() + "' ";

	    getBaseDao().executeUpdate(sql);
	}

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void groupByTableField(WaLoginContext context, String tablename, String table_Fld, String fieldOnPsnJob,
	    String pk_refinfo, boolean isHRTotal) throws DAOException {
	String code = (String) this.getBaseDao().executeQuery(
		"select item_code from hr_infoset_item where ref_model_name='" + pk_refinfo
			+ "' and pk_infoset = (select pk_infoset from hr_infoset where infoset_code ='" + tablename
			+ "')", new ColumnProcessor());
	if (!StringUtils.isEmpty(code)) {
	    String strSQL = "SELECT wa_cacu_data.pk_psndoc, SUM(" + table_Fld + ") totalamount";
	    strSQL += " FROM " + tablename + "";
	    strSQL += " INNER JOIN wa_cacu_data ON wa_cacu_data.pk_psndoc = " + tablename + ".pk_psndoc";
	    strSQL += " INNER JOIN wa_data ON wa_data.pk_wa_data = wa_cacu_data.pk_wa_data";
	    strSQL += " INNER JOIN hi_psnjob ON hi_psnjob.pk_psnjob = wa_data.pk_psnjob";
	    strSQL += " WHERE " + tablename + ".pk_psndoc=wa_cacu_data.pk_psndoc";
	    strSQL += " AND wa_cacu_data.pk_wa_class = '" + context.getPk_wa_class() + "'";
	    strSQL += " AND wa_cacu_data.creator = '" + context.getPk_loginUser() + "'";
	    strSQL += " AND '" + context.getWaLoginVO().getPeriodVO().getCstartdate().toString() + "' <= " + tablename
		    + ".enddate";
	    strSQL += " AND '" + context.getWaLoginVO().getPeriodVO().getCenddate().toString() + "' >= " + tablename
		    + ".begindate";
	    strSQL += " GROUP BY " + tablename + "." + code + ", wa_cacu_data.pk_psndoc";

	    List<Map> results = (List<Map>) getBaseDao().executeQuery(strSQL, new MapListProcessor());

	    if (results != null && results.size() > 0) {
		for (Map result : results) {
		    getBaseDao().executeUpdate(
			    "update wa_cacu_data set cacu_value="
				    + (result.get("totalamount") == null ? "0" : result.get("totalamount"))
				    + " where pk_psndoc='" + result.get("pk_psndoc") + "' and pk_wa_class='"
				    + context.getPk_wa_class() + "' and creator='" + context.getPk_loginUser() + "'");
		}
	    }
	}
    }

    private boolean isUseDate(String tableid) throws BusinessException {
	IPersistenceRetrieve persistence = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
	String where = " tableid='" + tableid + "' " + " and (name ='begindate' or name = 'enddate')";
	ColumnVO[] vos = (ColumnVO[]) persistence.retrieveByClause(null, ColumnVO.class, where
		+ " order by columnsequence ");
	if (!ArrayUtils.isEmpty(vos) && vos.length == 2) {
	    return true;
	}
	return false;
    }

    // 20151026 shenliangc
    // NCdp205513626社保档案项目为颁证日期=(其他证书.首次颁证日期,@当前日期@)时，缴交计算不了。
    // tableid表中是否存在recordnum、lastflag两个字段。
    private boolean hasNewestRecord(String tableid) throws BusinessException {
	IPersistenceRetrieve persistence = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
	String where = " tableid='" + tableid + "' " + " and (name ='recordnum' or name = 'lastflag')";
	ColumnVO[] vos = (ColumnVO[]) persistence.retrieveByClause(null, ColumnVO.class, where
		+ " order by columnsequence ");
	if (!ArrayUtils.isEmpty(vos) && vos.length == 2) {
	    return true;
	}
	return false;
    }

}
