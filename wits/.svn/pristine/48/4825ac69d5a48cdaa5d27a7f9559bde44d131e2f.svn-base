package nc.impl.wa.paydata.tax;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 年度累计税率表 年度累计方式本次扣税 = （累计应纳税 - 累计已纳税） * (100 - 减免税比例)
 * 
 * @author xuhw
 */
public class YearTaxTotalInfPreProcess extends AbstractFormulaExecutor {
	@Override
	public void excute(Object arguments, WaLoginContext context) throws BusinessException {
		// TODO Auto-generated method stub
		WaClassItemVO vo = (WaClassItemVO)	context.getInitData();
		String updateItemkey =  vo.getItemkey();
		if (arguments instanceof TaxFormulaVO) {
			TaxFormulaVO taxFormulaVO = (TaxFormulaVO) arguments;
			String taxableAmtIteKey = taxFormulaVO.getBase();//累计应扣税
			String taxedAmtIteKey = taxFormulaVO.getMonthPay();//累计已扣税
			String pk_wa_class = context.getPk_wa_class();
			String cyear = context.getCyear();
			String cperiod = context.getCperiod();
			
			if (isSqlDbs()) {
				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("update wa_cacu_data  "); // 1
				sqlBuffer.append("   set cacu_value =   "); // 2
				StringBuffer sbValue = new StringBuffer();
				sbValue.append(" coalesce(( "+ taxableAmtIteKey +" ) *  ");
				sbValue.append(" 		 (100 -  (case when  wa_data.isderate = 'N' then 0 else wa_data.derateptg end)) / 100 - "+ taxedAmtIteKey +" ,0 )    ");
				String roundsql = this.getRoundTaxSQL(vo, sbValue.toString());
				roundsql = " (case when "+roundsql+" < 0 then 0 else "+roundsql + " end)" ;
				roundsql = " (case when "+ taxableAmtIteKey +" - "+ taxedAmtIteKey +" < 0 then 0 else " + roundsql +" end) ";
				sqlBuffer.append(roundsql);
				sqlBuffer.append("   from wa_data ");
				sqlBuffer.append(" where wa_cacu_data.pk_wa_class = '" + context.getPk_wa_class() + "' ");
				sqlBuffer.append("   and wa_cacu_data.creator = '" + context.getPk_loginUser() + "' ");
				sqlBuffer.append("   and wa_cacu_data.pk_wa_data = wa_data.pk_wa_data ");
				getBaseDao().executeUpdate(sqlBuffer.toString());
			} else {
				StringBuffer sqlBuffer = new StringBuffer();
				sqlBuffer.append("update wa_cacu_data  "); // 1
				sqlBuffer.append("   set cacu_value = "); // 2
				
				StringBuffer sbValue = new StringBuffer();
				sbValue.append(" coalesce(( "+ taxableAmtIteKey +" ) *  ");
				sbValue.append(" 		 (100 -  (case when  wa_data.isderate = 'N' then 0 else wa_data.derateptg end)) / 100 - "+ taxedAmtIteKey +" ,0 )    ");
				String roundsql = this.getRoundTaxSQL(vo, sbValue.toString());
				roundsql = " (case when "+roundsql+" < 0 then 0 else "+roundsql +" end) ";
				roundsql = " (case when "+ taxableAmtIteKey +" - "+ taxedAmtIteKey +" < 0 then 0 else " + roundsql +" end) ";
				sqlBuffer.append("    (select ").append(roundsql); // 3
				sqlBuffer.append("   from wa_data ");
				sqlBuffer.append("  where wa_cacu_data.pk_wa_data = ");
				sqlBuffer.append("        wa_data.pk_wa_data) ");
				sqlBuffer.append(" where wa_cacu_data.pk_wa_class = '" + context.getPk_wa_class() + "' ");
				sqlBuffer.append("   and wa_cacu_data.creator = '" + context.getPk_loginUser() + "' ");

				getBaseDao().executeUpdate(sqlBuffer.toString());
			}
		}
	}
	
	private String getRoundTaxSQL(WaClassItemVO itemVO, String valueSql) throws BusinessException {
		   String sql = "";

			int digits = itemVO.getIflddecimal();
			/**
			 * 四舍五入， 进位， 舍位 薪资提供的进行方式同普通意义上的进位不同， 需求如下：
			 *
			 * 在小数位数后增加进位方式属性,下拉选择,系统提供进位 舍位和四舍五入三种舍位方式
			 * 进位，根据用户设定的小数位数,当计算结果中小数位数的的后一位不等于0时小数位数的最后一位加1
			 * 舍位，根据用户设定的小数位数,不论计算结果中小数位数的后一位是否等于0,均直接舍弃
			 * 四舍五入,根据用户设定的小数位数,计算结果中小数位数的后一位按照四舍五入的规则进行进位或舍位计算
			 */

			if (itemVO.getRound_type() == null || itemVO.getRound_type().intValue() == 0) {// 四舍五入
				 sql = "  ( round("+ valueSql + ", " + digits + " ))  " ;
			} else {
				//本次扣税肯定会>=0
				UFDouble f = UFDouble.ZERO_DBL;
				if (itemVO.getRound_type().intValue() == 1) {// 进位
					f = new UFDouble(0.4f);
				} else if (itemVO.getRound_type().intValue() == 2) {// 舍位操作
					f = new UFDouble(-0.5f);
				} else {// 默认四舍五入
					f = UFDouble.ZERO_DBL;
				}

				f = f.multiply(Math.pow(0.1, digits));

				 sql = "  ( round("+ valueSql +"+("+ f + "), " + digits + " ))   " ;
			}

			return sql;

	}
}
