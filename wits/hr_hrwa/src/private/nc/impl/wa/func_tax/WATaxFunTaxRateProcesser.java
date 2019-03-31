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
 * Get (value)
 * ��˰��_����˰��
 * ����˰��_�ۼ�Ӧ��˰���ö� - (5000 * ��н����)�� ����н�ʵ�����˰�������˰�ʵ�����۳���
 * 
 * ����н�ʷ����Ѿ����
 * @author: xuhw
 * @date:
 * @since: eHR V6.5
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
@SuppressWarnings("serial")
public class WATaxFunTaxRateProcesser extends AbstractWAFormulaParse
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

		// -- ͳһԱ�� ͬһ��������Դ��֯ �ϲ���˰���� ��˹������ݵĺϼ�
		sqlBuffer.append(" 	SELECT	    ");
		sqlBuffer.append(" 		 table1.NTAXRATE   ");
		sqlBuffer.append(" 	FROM	    ");
		sqlBuffer.append(" 		(    ");
		sqlBuffer.append(" 			SELECT	 (	  ");
		sqlBuffer.append(" 					data2."+itemkey+" - (	    ");
		sqlBuffer.append(" 						SELECT		  ");
		sqlBuffer.append(" 							(	 	 ");
		sqlBuffer.append(" 								COUNT (							 ");
		sqlBuffer.append(" 									DISTINCT (WADATA.cyear  " + getBDSql() + " WADATA.cperiod)						 ");
		sqlBuffer.append(" 								) * MAX (TAXBASE.NDEBUCTAMOUNT)							 ");
		sqlBuffer.append(" 							) NDEBUCTAMOUNT								 ");
		sqlBuffer.append(" 						FROM									 ");
		sqlBuffer.append(" 							wa_data wadata								 ");
		sqlBuffer.append(" 						INNER JOIN WA_TAXBASE taxbase ON TAXBASE.PK_WA_TAXBASE = WADATA.TAXTABLEID	 	 ");
		sqlBuffer.append(" 						WHERE									 ");
		sqlBuffer.append(" 							wadata.pk_psndoc = data2.pk_psndoc							 ");
		sqlBuffer.append(" 						AND WADATA.pk_org = data2.pk_org									 ");
		sqlBuffer.append(" 			and wadata.cyear  " + getBDSql() + " wadata.cperiod  <= '" +cyear + cperiod +"' ");
		sqlBuffer.append(" 						AND WADATA.cyear  " + getBDSql() + " WADATA.cperiod IN (									 ");
		sqlBuffer.append(" 							SELECT								 ");
		sqlBuffer.append(" 								cyear " + getBDSql() + " cperiod							 ");
		sqlBuffer.append(" 							FROM								 ");
		sqlBuffer.append(" 								wa_period							 ");
		sqlBuffer.append(" 							WHERE								 ");
		sqlBuffer.append(" 								PK_PERIODSCHEME = (							 ");
		sqlBuffer.append(" 									SELECT						 ");
		sqlBuffer.append(" 										PK_PERIODSCHEME					 ");
		sqlBuffer.append(" 									FROM						 ");
		sqlBuffer.append(" 										wa_waclass					 ");
		sqlBuffer.append(" 									WHERE						 ");
		sqlBuffer.append(" 										PK_WA_CLASS = '" + context.getPk_wa_class() + "'     ");
		sqlBuffer.append(" 								)		    ");
		sqlBuffer.append(" 							AND TAXYEAR = (			    ");
		sqlBuffer.append(" 								SELECT		    ");
		sqlBuffer.append(" 									period.TAXYEAR	    ");
		sqlBuffer.append(" 								FROM		    ");
		sqlBuffer.append(" 									wa_waclass waclass	    ");
		sqlBuffer.append(" 								INNER JOIN wa_period period ON period.PK_PERIODSCHEME = WACLASS.PK_PERIODSCHEME		    ");
		sqlBuffer.append(" 								WHERE		    ");
		sqlBuffer.append(" 									WACLASS.PK_WA_CLASS = '" + context.getPk_wa_class() + "' 	    ");
		sqlBuffer.append(" 								AND PERIOD.CYEAR = WACLASS.CYEAR		    ");
		sqlBuffer.append(" 								AND PERIOD.CPERIOD = WACLASS.CPERIOD    ");
		sqlBuffer.append(" 							)	    ");
		sqlBuffer.append(" 						)		    ");
		sqlBuffer.append(" 					)			    ");
		sqlBuffer.append(" 				) taxbasicAmt,				    ");
		sqlBuffer.append(" 				taxtable.NMINAMOUNT,				    ");
		sqlBuffer.append(" 				taxtable.NMAXAMOUNT,				    ");
		sqlBuffer.append(" 				taxtable.NQUICKDEBUCT, data2.pk_wa_data,				    ");
		sqlBuffer.append(" 				taxtable.NTAXRATE				    ");
		sqlBuffer.append(" 			FROM					    ");
		sqlBuffer.append(" 				wa_data data2				    ");
		sqlBuffer.append(" 			INNER JOIN WA_TAXTABLE taxtable ON data2.TAXTABLEID = taxtable.pk_wa_taxbase    ");
//		sqlBuffer.append(" 			WHERE    ");
//		sqlBuffer.append(" 				data2.pk_wa_data = wa_data.pk_wa_data		    ");
		sqlBuffer.append(" 		) table1    ");
		sqlBuffer.append(" 	WHERE	    ");
		sqlBuffer.append(" 		table1.pk_wa_data = wa_data.pk_wa_data and table1.NMINAMOUNT < table1.taxbasicAmt    ");
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			sqlBuffer.append(" 	AND isnull (table1.NMAXAMOUNT, 9999999999) >= table1.taxbasicAmt	    ");
		} else {
			sqlBuffer.append(" 	AND NVL (table1.NMAXAMOUNT, 9999999999) >= table1.taxbasicAmt	    ");
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
