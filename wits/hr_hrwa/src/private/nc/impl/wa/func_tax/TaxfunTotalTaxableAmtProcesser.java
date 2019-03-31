package nc.impl.wa.func_tax;

import nc.bs.dao.BaseDAO;
import nc.impl.wa.func.AbstractWAFormulaParse;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.formula.IFormulaAli;
import nc.vo.wa.pub.WaLoginContext;

/**
 * Get Sum(value)
 * 新税改_累计应纳税（新税改_累计应纳税所得额）
 * （新税改_累计应纳税所得额 - (5000 * 发薪月数)） * 适用税率 - 速算扣除数
 * 
 * 汇总薪资方案已经审核
 * @author: xuhw
 * @date:
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class TaxfunTotalTaxableAmtProcesser extends AbstractWAFormulaParse
		implements IFormulaAli {

	/**
	 * @throws BusinessException
	 * @see nc.impl.wa.func.TaxfunTotalTaxAbleIncomeProcesser#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		WaLoginContext context = getContext();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();

		StringBuffer sbsql = new StringBuffer();
		String[] arguments = getArguments(formula);
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String itemkey = arguments[0];
		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();

		
		sqlBuffer.append(" SELECT");
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			sqlBuffer.append(" 	isnull ( ( table2.taxbasicAmt2 * table2.NTAXRATE / 100 - table2.NQUICKDEBUCT  ),  0 ) taxAble  FROM  ");
		} else {
			sqlBuffer.append(" 	NVL ( ( table2.taxbasicAmt2 * table2.NTAXRATE / 100 - table2.NQUICKDEBUCT  ),  0 ) taxAble  FROM  ");
		}
		
		sqlBuffer.append(" 	(    ");
		sqlBuffer.append(" 		SELECT    ");
		sqlBuffer.append(" 			(     ");
		sqlBuffer.append(" 				CASE                 ");
		sqlBuffer.append(" 				WHEN table1.taxbasicAmt < 0 THEN");
		sqlBuffer.append(" 					0                ");
		sqlBuffer.append(" 				ELSE                 ");
		sqlBuffer.append(" 					table1.taxbasicAmt ");
		sqlBuffer.append(" 				END                  ");
		sqlBuffer.append(" 			) taxbasicAmt2,          ");
		sqlBuffer.append(" 			table1.*                 ");
		sqlBuffer.append(" 		FROM      ");
		sqlBuffer.append(" 			(     ");
		sqlBuffer.append(" 				SELECT               ");
		sqlBuffer.append(" 					(                ");
		sqlBuffer.append(" 						data2."+itemkey+"  - (  ");
		sqlBuffer.append(" 							SELECT   ");
		sqlBuffer.append(" 								(    ");
		sqlBuffer.append(" 									COUNT (     ");
		sqlBuffer.append(" 										DISTINCT (wadata.cyear  " + getBDSql() + " wadata.cperiod)       ");
		sqlBuffer.append(" 									) * MAX (TAXBASE.NDEBUCTAMOUNT)");
		sqlBuffer.append(" 								) NDEBUCTAMOUNT ");
		sqlBuffer.append(" 							FROM     ");
		sqlBuffer.append(" 								wa_data wadata  ");
		sqlBuffer.append(" 							INNER JOIN WA_TAXBASE taxbase ON TAXBASE.PK_WA_TAXBASE = WADATA.TAXTABLEID  ");
		sqlBuffer.append(" 							WHERE    ");
		sqlBuffer.append(" 								wadata.pk_psndoc = data2.pk_psndoc  ");
		sqlBuffer.append(" 							AND WADATA.pk_org = data2.pk_org");
		sqlBuffer.append(" 			and wadata.cyear  " + getBDSql() + " wadata.cperiod  <= '" +cyear + cperiod +"' ");
		sqlBuffer.append(" 							AND wadata.cyear  " + getBDSql() + " wadata.cperiod IN (            ");
		sqlBuffer.append(" 								SELECT ");
		sqlBuffer.append(" 									cyear " + getBDSql() + " cperiod               ");
		sqlBuffer.append(" 								FROM ");
		sqlBuffer.append(" 									wa_period   ");
		sqlBuffer.append(" 								WHERE");
		sqlBuffer.append(" 									PK_PERIODSCHEME = (            ");
		sqlBuffer.append(" 										SELECT  ");
		sqlBuffer.append(" 											PK_PERIODSCHEME        ");
		sqlBuffer.append(" 										FROM    ");
		sqlBuffer.append(" 											wa_waclass             ");
		sqlBuffer.append(" 										WHERE   ");
		sqlBuffer.append(" 											PK_WA_CLASS = '" + context.getPk_wa_class() + "'              ");
		sqlBuffer.append(" 									)");
		sqlBuffer.append(" 								AND TAXYEAR = ( ");
		sqlBuffer.append(" 									SELECT      ");
		sqlBuffer.append(" 										period.TAXYEAR             ");
		sqlBuffer.append(" 									FROM        ");
		sqlBuffer.append(" 										wa_waclass waclass         ");
		sqlBuffer.append(" 									INNER JOIN wa_period period ON period.PK_PERIODSCHEME = WACLASS.PK_PERIODSCHEME        ");
		sqlBuffer.append(" 									WHERE       ");
		sqlBuffer.append(" 										WACLASS.PK_WA_CLASS = '" + context.getPk_wa_class() + "'           ");
		sqlBuffer.append(" 									AND PERIOD.CYEAR = WACLASS.CYEAR        ");
		sqlBuffer.append(" 									AND PERIOD.CPERIOD = WACLASS.CPERIOD    ");
		sqlBuffer.append(" 								)    ");
		sqlBuffer.append(" 							)        ");
		sqlBuffer.append(" 						)            ");
		sqlBuffer.append(" 					) taxbasicAmt,   ");
		sqlBuffer.append(" 					taxtable.NMINAMOUNT,        ");
		sqlBuffer.append(" 					taxtable.NMAXAMOUNT,        ");
		sqlBuffer.append(" 					taxtable.NQUICKDEBUCT,      ");
		sqlBuffer.append(" 					taxtable.NTAXRATE , ");
		sqlBuffer.append(" 					data2.pk_wa_data ");
		sqlBuffer.append(" 				FROM                 ");
		sqlBuffer.append(" 					wa_data data2    ");
		sqlBuffer.append(" 				INNER JOIN WA_TAXTABLE taxtable ON data2.TAXTABLEID = taxtable.pk_wa_taxbase   ");
		//sqlBuffer.append(" 				WHERE                ");
		//sqlBuffer.append(" 					data2.pk_wa_data = wa_data.pk_wa_data     ");
		sqlBuffer.append(" 			) table1                 ");
		sqlBuffer.append(" 	) table2      ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" 	table2.pk_wa_data = WA_DATA.PK_WA_DATA and table2.NMINAMOUNT < table2.taxbasicAmt2    ");
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			sqlBuffer.append(" AND isnull (    table2.NMAXAMOUNT, 9999999999  ) >= table2.taxbasicAmt2   ");
		} else {
			sqlBuffer.append(" AND NVL (    table2.NMAXAMOUNT, 9999999999  ) >= table2.taxbasicAmt2    ");
		}

		fvo.setReplaceStr(coalesce(sqlBuffer.toString()));

		return fvo;
	}

	@Override
	public String[] getAliItemKeys(WaClassItemVO itemVO,
			WaLoginContext context, FunctionVO functionVO)
			throws BusinessException {
		if (itemVO == null || context == null || functionVO == null
				|| itemVO.getVformula() == null) {
			return null;
		}

		setFunctionVO(functionVO);
		setContext(context);

		String[] arguments = getArguments(itemVO.getVformula());

		String itemkey = arguments[0];
		return new String[] { itemkey };
	}
	
	private String getBDSql() {
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			return "+";
		} else {
			return "||";
		}
	}
}
