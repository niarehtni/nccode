package nc.impl.wa.paydata;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class NewTaxPaydataCollectDAO extends PaydataCollectDAO {

	private static final String sum_pre = "sum(";
	private static final String max_pre = "max(";
	private static final String min_pre = "min(";
	private static final String sum_post = ")";
	/** 累计应纳税所得额 */
	private static final String taxfunTotalTaxAbleIncome = "taxfunTotalTaxAbleIncome";
	/** 累计应应纳税 */
	private static final String taxfunTotalTaxAbleAmt = "taxfunTotalTaxAbleAmt";
	/** 累计已扣税 */
	private static final String taxfunTotalTaxedAmt = "taxfunTotalTaxedAmt";
	/** 速算扣除数 */
	private static final String taxfunTaxQuickDeduction = "taxfunTaxQuickDeduction";
	/** 适用税率 */
	private static final String taxfunTaxRate = "taxfunTaxRate";
	/** 基本扣除 */
	private static final String taxfunTaxBasicDeduction = "taxfunTaxBasicDeduction";
	
	/** 专项累计已扣 */
	private static final String tax_deduction_totaled_yz = "taxDeductionTotaled";

	private static List<String> newTaxFunList = Arrays
			.asList(new String[] { taxfunTotalTaxAbleIncome, taxfunTotalTaxAbleAmt, taxfunTotalTaxedAmt,
					taxfunTaxQuickDeduction, taxfunTaxRate, taxfunTaxBasicDeduction });

	public WaItemVO[] getParentClassDigitItem(WaLoginVO waLoginVO) throws DAOException {

		// 得到需要汇总的薪资项目
		// 规则： 在汇总类别中， 并且在被汇总的类别中的数值型项目手工输入的项目
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty ,classitem.VFORMULA ,wa_item.IFROMFLAG "); // 1
		sqlBuffer
				.append("  from wa_item wa_item inner join WA_CLASSITEM classitem on CLASSITEM.PK_WA_ITEM = wa_item.PK_WA_ITEM ");
		sqlBuffer.append("   where  CLASSITEM.PK_WA_CLASS = '"+waLoginVO.getPk_wa_class()+"' and CLASSITEM.cyear = '"+waLoginVO.getCyear()+"' and CLASSITEM.cperiod = '"+waLoginVO.getCperiod()+"' and wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype = 0 ");

		return executeQueryVOs(sqlBuffer.toString(), WherePartUtil.getCommonParameter(waLoginVO), WaItemVO.class);

	}

	/**
	 * 处理新税改函数-多次发薪
	 * 
	 * @param waLoginVO
	 * @throws DAOException
	 */
	public void collectWaTimesNewTaxDigitData(WaLoginVO waLoginVO, WaInludeclassVO[] childClasses)
			throws BusinessException {

		WaItemVO[] itemVOs = getParentClassDigitItem(waLoginVO);
		if (!ArrayUtils.isEmpty(itemVOs)) {
			String sql = getTaxDigitBusinessSql(waLoginVO, itemVOs, childClasses);
			if (!StringUtils.isEmpty(sql)){
				getBaseDao().executeUpdate(sql);
			}
			
		}
	}

	/**
	 * 新税改相关函数多次发薪更新
	 * 
	 * 汇总数据的sql。sqlserver 与 oracle 是不一样的
	 * 
	 * @param itemVOs
	 * @return
	 * @throws BusinessException
	 */
	private String getTaxDigitBusinessSql(WaLoginVO waLoginVO, WaItemVO[] itemVOs, WaInludeclassVO[] childClasses)
			throws BusinessException {

		StringBuilder sbd = new StringBuilder();
		String pk_waclass = waLoginVO.getPk_prnt_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();
		if (isSqlDbs()) {

			sbd.append("update wa_data "); // 1
			sbd.append("  set ");
			StringBuffer updateitemsql = new StringBuffer();;
			for (int i = 0; i < itemVOs.length; i++) {
				// 不是计算新税改项目直接返回
				if (StringUtils.isEmpty(itemVOs[i].getVformula()) || itemVOs[i].getIfromflag() != 6) {
					continue;
				}
				
				if (itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleIncome)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxQuickDeduction)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxRate)
							|| itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleAmt)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxBasicDeduction)
						|| itemVOs[i].getVformula().startsWith(tax_deduction_totaled_yz)
						||itemVOs[i].getVformula().startsWith(taxfunTotalTaxedAmt)) {
						updateitemsql.append(itemVOs[i].getItemkey() + " = data_table." + itemVOs[i].getItemkey());
						updateitemsql.append(",");
				}
//				updateitemsql.append(itemVOs[i].getItemkey() + " = data_table." + itemVOs[i].getItemkey());
//				updateitemsql.append(",");
			}
			
			if (updateitemsql.length() == 0) {
				return null;
			}
			String updateitemsqlStr = updateitemsql.toString().substring(0, updateitemsql.toString().length() - 1);
			sbd.append(updateitemsqlStr);
			sbd.append(" from ");
			sbd.append(" 	( select ");
			for (int i = 0; i < itemVOs.length; i++) {
				if (StringUtils.isEmpty(itemVOs[i].getVformula()) || itemVOs[i].getIfromflag() != 6) {
					continue;
				}
				if (itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleIncome)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxQuickDeduction)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxRate)
							|| itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleAmt)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxBasicDeduction)) {
					sbd.append(max_pre + itemVOs[i].getItemkey() + sum_post + " as " + itemVOs[i].getItemkey());
						sbd.append(",");
						
				} else if (itemVOs[i].getVformula().startsWith(taxfunTotalTaxedAmt) || itemVOs[i].getVformula().startsWith(tax_deduction_totaled_yz)) {
					sbd.append(min_pre + itemVOs[i].getItemkey() + sum_post + " as " + itemVOs[i].getItemkey());
						sbd.append(",");
				}
					
			}
			
			if (updateitemsql.length() == 0) {
				return null;
			}
			sbd.append(" pk_psndoc from wa_data a where   ");
			sbd.append(" 	    a.pk_wa_class in ( "
					+ FormatVO.formatArrayToString(childClasses, WaInludeclassVO.PK_CHILDCLASS) + " ) and a.cyear = '"
					+ cyear + "' and a.cperiod = '" + cperiod
					+ "'  and a.stopflag = 'N'  group by pk_psndoc ) data_table ");
			sbd.append(" 	where data_table.pk_psndoc = wa_data.pk_psndoc  and wa_data.pk_wa_class = '" + pk_waclass
					+ "'  and wa_data.cyear = '" + cyear + "' and wa_data.cperiod = '" + cperiod + "'  ");

			// sbd.append("     and  ");

		} else {
			
			StringBuffer updateItems = new StringBuffer();
			StringBuffer updateValues = new StringBuffer();
			for (int i = 0; i < itemVOs.length; i++) {
				if (StringUtils.isEmpty(itemVOs[i].getVformula()) || itemVOs[i].getIfromflag() != 6 && (
						itemVOs[i].getVformula().indexOf(taxfunTotalTaxAbleIncome)== -1
						&& itemVOs[i].getVformula().indexOf(taxfunTotalTaxAbleAmt) == -1
						&& itemVOs[i].getVformula().indexOf(taxfunTotalTaxedAmt) == -1
						&& itemVOs[i].getVformula().indexOf(taxfunTaxQuickDeduction) == -1
						&& itemVOs[i].getVformula().indexOf(tax_deduction_totaled_yz) == -1
						&& itemVOs[i].getVformula().indexOf(taxfunTaxRate) == -1
						&& itemVOs[i].getVformula().indexOf(taxfunTaxBasicDeduction)== -1) 
						){
					continue;
				}
//				updateItems.append(itemVOs[i].getItemkey());
//				updateItems.append(",");
				if (itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleIncome)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxQuickDeduction)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxRate)
							|| itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleAmt)
						|| itemVOs[i].getVformula().startsWith(taxfunTaxBasicDeduction)) {
					updateItems.append(itemVOs[i].getItemkey());
					updateItems.append(",");
					updateValues.append(max_pre + itemVOs[i].getItemkey() + sum_post + " as " + itemVOs[i].getItemkey());
					updateValues.append(",");
				} else if (itemVOs[i].getVformula().startsWith(taxfunTotalTaxedAmt) || itemVOs[i].getVformula().startsWith(tax_deduction_totaled_yz)) {
					updateItems.append(itemVOs[i].getItemkey());
					updateItems.append(",");
					updateValues.append(min_pre + itemVOs[i].getItemkey() + sum_post + " as " + itemVOs[i].getItemkey());
					updateValues.append(",");
				}
				 
//				if (i < itemVOs.length -1) {
					
					
//				}
				
			}
			
			if (updateItems.length() == 0) {
				return null;
			}
			String updateItemsSql = updateItems.toString().substring(0, updateItems.toString().length() - 1);
			String updateValuesSql = updateValues.toString().substring(0, updateValues.toString().length() - 1);
			sbd.append("update wa_data "); // 1
			sbd.append("  set ");
			sbd.append("( " + updateItemsSql + " )");
			sbd.append(" = ");
			sbd.append(" (select ");
			sbd.append(updateValuesSql.toString());

			sbd.append(" from wa_data   a  ");
			sbd.append(" where  a.pk_wa_class in ("
					+ FormatVO.formatArrayToString(childClasses, WaInludeclassVO.PK_CHILDCLASS) + ") and a.cyear = '"
					+ cyear + "' and a.cperiod = '" + cperiod + "' ");
			sbd.append(" and  a.pk_psndoc = wa_data.pk_psndoc  and a.stopflag = 'N'   group by a.pk_psndoc ) ");
			sbd.append(" where  wa_data.pk_wa_class ='" + pk_waclass + "' and wa_data.cyear = '" + cyear
					+ "' and wa_data.cperiod = '" + cperiod + "' ");

			sbd.append(" and   wa_data.pk_psndoc in ( select a.pk_psndoc	from	wa_data a 	where	a.pk_wa_class in ( "
					+ FormatVO.formatArrayToString(childClasses, WaInludeclassVO.PK_CHILDCLASS) + " )	and a.cyear = '"
					+ cyear + "'	and a.cperiod = '" + cperiod + "'	and a.stopflag = 'N'  )");

		}
		return sbd.toString();
	}
	

	/**
	 * 汇总薪资方案数据更新
	 * 
	 * @param waLoginVO
	 * @param itemVOs
	 * @throws DAOException
	 */
	public void updateDataNewTax(WaLoginVO waLoginVO, WaItemVO[] itemVOs) throws DAOException {
		itemVOs = getParentClassDigitItem(waLoginVO);
		if (itemVOs == null) {
			return;
		}
		
		// 分数据库进行更新
		if (getBaseDao().getDBType() == DBUtil.SQLSERVER) {
			updateDataSQLDbs(itemVOs, waLoginVO);
		} else {
			updateDataOracleDbs(itemVOs, waLoginVO);
		}

	}

	/**
	 * 新税改更新汇总薪资方案（sql server）
	 * 
	 * @param itemVOs
	 * @param waLoginVO
	 * @throws DAOException
	 */
	private void updateDataSQLDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {

		String tableName = " wa_data ";

		// 需要更新的字段
		List<String> list = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
//			list.add(tableName + "." + itemVO.getItemkey() + " = sum_data." + itemVO.getItemkey() + "");
			
			// 不是计算新税改项目直接返回
			if (StringUtils.isEmpty(itemVO.getVformula()) || itemVO.getIfromflag() != 6) {
				continue;
			}
			
			if (itemVO.getVformula().startsWith(taxfunTotalTaxAbleIncome)
					|| itemVO.getVformula().startsWith(taxfunTaxQuickDeduction)
					|| itemVO.getVformula().startsWith(taxfunTaxRate)
						|| itemVO.getVformula().startsWith(taxfunTotalTaxAbleAmt)
					|| itemVO.getVformula().startsWith(taxfunTaxBasicDeduction)
					|| itemVO.getVformula().startsWith(tax_deduction_totaled_yz)
					||itemVO.getVformula().startsWith(taxfunTotalTaxedAmt)) {
					list.add(tableName + "." + itemVO.getItemkey() + " = sum_data." + itemVO.getItemkey() + "");
			}
		}

		String colNames = FormatVO.formatListToString(list, "");

		// SUM的字段
		List<String> sumList = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
//			sumList.add("sum(" + tableName + "." + itemVO.getItemkey() + ") " + itemVO.getItemkey());
			if (StringUtils.isEmpty(itemVO.getVformula()) || itemVO.getIfromflag() != 6) {
				continue;
			}
			if (itemVO.getVformula().startsWith(taxfunTotalTaxAbleIncome)
					|| itemVO.getVformula().startsWith(taxfunTaxQuickDeduction)
					|| itemVO.getVformula().startsWith(taxfunTaxRate)
						|| itemVO.getVformula().startsWith(taxfunTotalTaxAbleAmt)
					|| itemVO.getVformula().startsWith(taxfunTaxBasicDeduction)) {
//				sbd.append(max_pre + itemVO.getItemkey() + sum_post + " as " + itemVO.getItemkey());
				sumList.add("max(" + tableName + "." + itemVO.getItemkey() + ") " + itemVO.getItemkey());
					
			} else if (itemVO.getVformula().startsWith(taxfunTotalTaxedAmt)) {
//				sbd.append(min_pre + itemVO.getItemkey() + sum_post + " as " + itemVO.getItemkey());
				sumList.add("max(" + tableName + "." + itemVO.getItemkey() + ") " + itemVO.getItemkey());
			}
			 else if (itemVO.getVformula().startsWith(tax_deduction_totaled_yz)) {
//					sbd.append(min_pre + itemVO.getItemkey() + sum_post + " as " + itemVO.getItemkey());
					sumList.add("min(" + tableName + "." + itemVO.getItemkey() + ") " + itemVO.getItemkey());
				}
		}

		String sumColNames = FormatVO.formatListToString(sumList, "");
		String extraConditon = "";

		extraConditon = " and wa_data.stopflag = 'N' ";

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " "); // 1
		sqlBuffer.append("   set " + colNames + "  "); // 2
		sqlBuffer.append("   from (select " + sumColNames + ", " + tableName + ".pk_psndoc ");
		sqlBuffer.append("     from " + tableName + " ");
		sqlBuffer.append("    where " + tableName + ".pk_wa_class in ");
		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("      and " + tableName + ".cyear = ? ");
		sqlBuffer.append("      and " + tableName + ".cperiod = ? ");
		sqlBuffer.append(extraConditon);

		//汇总一定是按照pk_psndoc汇总
		sqlBuffer.append("    group by " + tableName + ".pk_psndoc) sum_data ");
		sqlBuffer.append(" where " + tableName + ".pk_wa_class = ? ");
		sqlBuffer.append(" and " + tableName + ".cyear = ?  ");
		sqlBuffer.append(" and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(" and " + tableName + ".pk_psndoc = sum_data.pk_psndoc ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

	}

	/**
	 * 新税改更新汇总薪资方案（oracle）
	 */
	private void updateDataOracleDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {

		String tableName = " wa_data ";

		// 需要更新的字段
		List<String> list = new LinkedList<String>();
//		for (WaItemVO itemVO : itemVOs) {
//			list.add("unit." + itemVO.getItemkey());
//		}

		

		// SUM的字段
		List<String> sumList = new LinkedList<String>();
//		for (WaItemVO itemVO : itemVOs) {
//			sumList.add(" nvl(sum(" + tableName + "." + itemVO.getItemkey() + "),0)");
//		}
//		
		for (int i = 0; i < itemVOs.length; i++) {
			if (StringUtils.isEmpty(itemVOs[i].getVformula()) || itemVOs[i].getIfromflag() != 6 && (
					itemVOs[i].getVformula().indexOf(taxfunTotalTaxAbleIncome)== -1
					&& itemVOs[i].getVformula().indexOf(taxfunTotalTaxAbleAmt) == -1
					&& itemVOs[i].getVformula().indexOf(taxfunTotalTaxedAmt) == -1
					&& itemVOs[i].getVformula().indexOf(taxfunTaxQuickDeduction) == -1
					&& itemVOs[i].getVformula().indexOf(tax_deduction_totaled_yz) == -1
					&& itemVOs[i].getVformula().indexOf(taxfunTaxRate) == -1
					&& itemVOs[i].getVformula().indexOf(taxfunTaxBasicDeduction)== -1) 
					){
				continue;
			}
			if (itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleIncome)
					|| itemVOs[i].getVformula().startsWith(taxfunTaxQuickDeduction)
					|| itemVOs[i].getVformula().startsWith(taxfunTaxRate)
						|| itemVOs[i].getVformula().startsWith(taxfunTotalTaxAbleAmt)
						|| itemVOs[i].getVformula().startsWith(taxfunTotalTaxedAmt)
					|| itemVOs[i].getVformula().startsWith(taxfunTaxBasicDeduction)) {
				list.add("unit." + itemVOs[i].getItemkey());
				sumList.add(" nvl(max(" + tableName + "." + itemVOs[i].getItemkey() + "),0)");
			}else if (itemVOs[i].getVformula().startsWith(tax_deduction_totaled_yz)){
				list.add("unit." + itemVOs[i].getItemkey());
				sumList.add(" nvl(min(" + tableName + "." + itemVOs[i].getItemkey() + "),0)");
			}
		}
		String colNames = FormatVO.formatListToString(list, "");
		String sumColNames = FormatVO.formatListToString(sumList, "");

		String extraConditon = "";
		extraConditon = " and wa_data.stopflag = 'N' ";

		// 组织SQL语句
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " unit "); // 1
		sqlBuffer.append("   set (" + colNames + ") = (select  " + sumColNames + " "); // 2
		sqlBuffer.append("                                  from " + tableName + " where " + tableName
				+ ".pk_wa_class in ");
		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("                                       and " + tableName + ".cyear = ?  ");
		sqlBuffer.append("                                       and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(extraConditon);
		sqlBuffer.append("                                       and " + tableName
				+ ".pk_psndoc = unit.pk_psndoc group by " + tableName + ".pk_psndoc) ");
		sqlBuffer.append(" where unit.pk_wa_class = ? ");
		sqlBuffer.append("   and unit.cyear = ? ");
		sqlBuffer.append("   and unit.cperiod = ? ");

		//仅仅汇总子方案中有的人员
		sqlBuffer.append(" and unit.pk_psndoc in  (select wa_data.pk_psndoc    " +
				"from wa_data  where wa_data.pk_wa_class in " +
				"(select wa_waclass.pk_wa_class from wa_unitctg, wa_waclass  where wa_waclass.pk_wa_class = wa_unitctg.classedid and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag = 'N'  ) and wa_data.cyear = ? and wa_data.cperiod = ? and wa_data.stopflag = 'N') ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
	}

}
