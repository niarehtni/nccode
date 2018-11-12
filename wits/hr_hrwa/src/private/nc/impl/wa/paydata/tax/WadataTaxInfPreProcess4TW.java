package nc.impl.wa.paydata.tax;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.SQLHelper;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

//2017-05-16 upgrade to V65, from V63 code

@SuppressWarnings("restriction")
public class WadataTaxInfPreProcess4TW extends BaseDAOManager implements
		ITaxInfPreProcess {

	@Override
	public void transferTaxCacuData(TaxFormulaVO taxFormulaVO,
			WaLoginContext context) throws DAOException {
		// 更新计税基数，到中间表字段 。wa_cacu_data.tax_base ,然后使用中间表wa_cacu_data.tax_base
		// 作为计税基数。
		// 根据补发类型，以及扣税公式 ，决定扣税基数
		String taxbaseColKey = taxFormulaVO.getBase();
		updateTaxCacuData(context, taxbaseColKey);

		// 不使用减免税的 人员，减税比率是0
		transferDerateptg(context);
	}

	/**
	 * 薪资补发与合并计税影响 扣税基数。
	 * 
	 * 如果扣税公式，是用户自定义已的。则扣税基数不需要处理
	 * 
	 * 如果扣税公式，是系统定义的。则需要考虑 薪资补发与合并计税影响
	 * 
	 * 更新计税数据。如果该薪资方案没有补发，则 (1)薪资补发
	 * 
	 * @author zhangg on 2010-5-13
	 * @param context
	 * @param taxbaseColKey
	 * @throws DAOException
	 */
	protected void updateTaxCacuData(WaLoginContext context,
			String taxbaseColKey) throws DAOException {
		String redataTaxBaseColKey = taxbaseColKey.indexOf("wa_redata") >= 0 ? taxbaseColKey
				: null;
		if (redataTaxBaseColKey != null) {
			taxbaseColKey = taxbaseColKey.replaceAll("wa_redata", "wa_data");
		}

		if (isSqlDbs()) {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_cacu_data "); // 1
			sqlBuffer.append("   set cacu_value  = 0, "); // 2
			sqlBuffer.append("       taxtableid = wa_data.taxtableid, "); // 3
			sqlBuffer.append("       tax_base   = " + taxbaseColKey + ", "); // 4
																				// taxbaseColKey
																				// 一般是
																				// wa_data.f_4
			sqlBuffer.append("       taxtype    = wa_data.taxtype, "); // 5
			sqlBuffer.append("       isndebuct = wa_data.isndebuct, "); // 6
			sqlBuffer.append("       isderate  = wa_data.isderate, "); // 7
			sqlBuffer.append("       derateptg = wa_data.derateptg, "); // 8
			sqlBuffer.append("		   pk_psndoc = wa_data.pk_psndoc, "); // ssx-added

			sqlBuffer.append("       taxable_income = 0, "); // 9 reset
			sqlBuffer.append("       expense_deduction = 0, "); // 10 reset
			sqlBuffer.append("       taxrate = 0, "); // 11 reset
			sqlBuffer.append("       nquickdebuct =0  "); // 12 reset

			sqlBuffer.append("from wa_data ");
			sqlBuffer.append(" where wa_cacu_data.pk_wa_class = '"
					+ context.getPk_wa_class() + "' ");
			sqlBuffer.append("   and wa_cacu_data.creator = '"
					+ context.getPk_loginUser() + "' ");
			sqlBuffer
					.append("   and wa_cacu_data.pk_wa_data = wa_data.pk_wa_data ");

			getBaseDao().executeUpdate(sqlBuffer.toString());
		} else {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_cacu_data  "); // 1
			sqlBuffer
					.append("   set (cacu_value, taxtableid, tax_base, taxtype, isndebuct, isderate, derateptg,pk_psndoc,taxable_income,expense_deduction,taxrate,nquickdebuct)  "); // 2
			sqlBuffer.append("   = (select 0, "); // 3
			sqlBuffer.append("        wa_data.taxtableid, "); // 4
			sqlBuffer.append(taxbaseColKey + " ,"); // 5
			sqlBuffer.append("        wa_data.taxtype, "); // 6
			sqlBuffer.append("        wa_data.isndebuct, "); // 7
			sqlBuffer.append("        wa_data.isderate, "); // 8
			sqlBuffer
					.append("        wa_data.derateptg , wa_data.pk_psndoc,0,0,0,0"); // 9
			sqlBuffer.append("   from wa_data ");
			sqlBuffer.append("  where wa_cacu_data.pk_wa_data = ");
			sqlBuffer.append("        wa_data.pk_wa_data) ");
			sqlBuffer.append(" where wa_cacu_data.pk_wa_class = '"
					+ context.getPk_wa_class() + "' ");
			sqlBuffer.append("   and wa_cacu_data.creator = '"
					+ context.getPk_loginUser() + "' ");

			getBaseDao().executeUpdate(sqlBuffer.toString());
		}

		/**
		 * 补发扣税方式，决定了扣税基数的公式。
		 * 
		 * 如果是补发计税,根据补发扣税的方式，创建不同的策略 (0) 不扣税 (1) 累计到本期扣税 (2) 补发期间扣税（不考虑补发历史） (3)
		 * 补发期间扣税（考虑补发历史）
		 * 
		 * 只有 “ 累计到本期扣税 ” 需要 以下sql语句。
		 * 
		 * 在薪资发放节点，以下sql语句 需要吗？ 薪资补发节点呢？需要吗？
		 */
		if (redataTaxBaseColKey != null) {
			taxbaseColKey = taxbaseColKey.replaceAll("wa_data", "wa_redata");
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("update wa_cacu_data  "); // 1
			sqlBuffer.append("   set tax_base  "); // 2
			sqlBuffer.append("   = (select  "); // 3
			sqlBuffer.append(taxbaseColKey); // 5 。
			sqlBuffer.append("  from wa_redata ");
			sqlBuffer
					.append(" where wa_redata.pk_psndoc = wa_cacu_data.pk_psndoc ");
			sqlBuffer
					.append("   and wa_redata.pk_wa_class = wa_cacu_data.pk_wa_class ");
			sqlBuffer.append("   and wa_redata.pk_wa_class = '"
					+ context.getPk_wa_class() + "' ");
			sqlBuffer.append("   and wa_redata.cyear = '" + context.getWaYear()
					+ "'  ");
			sqlBuffer.append("   and wa_redata.cperiod = '"
					+ context.getWaPeriod() + "' ");
			sqlBuffer.append("   and wa_redata.creyear = '"
					+ context.getWaLoginVO().getReyear() + "' ");
			sqlBuffer.append("   and wa_redata.creperiod = '"
					+ context.getWaLoginVO().getReperiod() + "' )");

			sqlBuffer.append(" where wa_cacu_data.pk_wa_class = '"
					+ context.getPk_wa_class() + "' ");
			sqlBuffer.append("   and wa_cacu_data.creator = '"
					+ context.getPk_loginUser() + "' ");
			getBaseDao().executeUpdate(sqlBuffer.toString());

		}

	}

	/**
	 * 减免税
	 * 
	 * @author zhangg on 2010-6-4
	 * @param context
	 * @throws BusinessException
	 */
	private void transferDerateptg(WaLoginContext context) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update wa_cacu_data "); // 1
		sqlBuffer.append("   set derateptg = 0"); // 2
		sqlBuffer.append("  where wa_cacu_data.pk_wa_class = '"
				+ context.getPk_wa_class() + "' ");
		sqlBuffer.append("   and (isderate = 'N' or  "
				+ SQLHelper.getNumberNullSql("derateptg") + " ) "); // 5

		getBaseDao().executeUpdate(sqlBuffer.toString());
	}

}
