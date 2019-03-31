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
 * Get max(value) 新税改_累计专项已扣 
 * 说明：取本纳税年度 小于 当前纳税期间的的最大的累计应扣的值
 * 
 * @author: xuhw
 * @date:
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class TaxfunTotalSpecialDeductionedAmtProcesser extends AbstractWAFormulaParse implements IFormulaAli {

	/**
	 * @throws BusinessException
	 * @see nc.impl.wa.func.TaxfunTotalTaxAbleIncomeProcesser#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		WaLoginContext context = getContext();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();

		String[] arguments = getArguments(formula);
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String itemkey = arguments[0];
		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();

		// -- 统一员工 同一个人力资源组织 合并计税方案 审核过的数据的合计
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			sqlBuffer
					.append(" SELECT  isnull (max(wadata."+itemkey+"),0) value from wa_data wadata          ");
		} else {
			sqlBuffer
					.append(" SELECT  nvl (max(wadata."+itemkey+"),0) value from wa_data  wadata     ");
		}
		//过滤掉汇总薪资类别方案,按月计税方案
		sqlBuffer.append(" 	inner join wa_waclass waclass on wadata.pk_wa_class = waclass.pk_wa_class and waclass.collectflag = 'N'	and coalesce(waclass.yearbonusflag,'N') = 'N' ");
		sqlBuffer.append(" 		 where wa_data.pk_psndoc = wadata.pk_psndoc and wa_data.pk_org = wadata.pk_org and wadata.CHECKFLAG = 'Y' AND  ( WADATA.cyear  " + getBDSql() + " WADATA.cperiod < "+ cyear + cperiod +" OR   ");
		sqlBuffer.append(" 				(  ( wadata.PK_WA_CLASS IN   ");
		sqlBuffer.append(" 				( SELECT PK_WACLASS FROM WA_TAXGRPMEMBER WHERE pk_taxgroup =   ");
		sqlBuffer
				.append(" 				( SELECT PK_TAXGROUP FROM WA_TAXGRPMEMBER WHERE pk_waclass = '" + context.getPk_wa_class() + "' )   ");
		sqlBuffer.append(" 				AND PK_WACLASS <> '" + context.getPk_wa_class() + "' ) OR wadata.PK_WA_CLASS IN   ");
		sqlBuffer.append(" 				(( SELECT PK_CHILDCLASS FROM WA_INLUDECLASS WHERE PK_PARENTCLASS IN   ");
		sqlBuffer
				.append(" 				( SELECT PK_PARENTCLASS FROM wa_inludeclass WHERE PK_CHILDCLASS = '" + context.getPk_wa_class() + "' )   ");
		sqlBuffer.append(" 				AND PK_CHILDCLASS <> '" + context.getPk_wa_class() + "' )))))  ");
		sqlBuffer.append(" 			and wadata.cyear  " + getBDSql() + " wadata.cperiod  <= '" +cyear + cperiod +"' ");
		sqlBuffer.append(" 							AND WADATA.cyear  " + getBDSql() + " WADATA.cperiod IN (            ");
		sqlBuffer.append(" 								SELECT ");
		sqlBuffer.append(" 									cyear " + getBDSql() + "  cperiod               ");
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
		sqlBuffer
				.append(" 									INNER JOIN wa_period period ON period.PK_PERIODSCHEME = WACLASS.PK_PERIODSCHEME        ");
		sqlBuffer.append(" 									WHERE       ");
		sqlBuffer.append(" 										WACLASS.PK_WA_CLASS = '" + context.getPk_wa_class() + "'           ");
		sqlBuffer.append(" 									AND PERIOD.CYEAR = WACLASS.CYEAR        ");
		sqlBuffer.append(" 									AND PERIOD.CPERIOD = WACLASS.CPERIOD   )) ");

		fvo.setReplaceStr(coalesce(sqlBuffer.toString()));
		return fvo;
	}

	@Override
	public String[] getAliItemKeys(WaClassItemVO itemVO, WaLoginContext context, FunctionVO functionVO)
			throws BusinessException {
		if (itemVO == null || context == null || functionVO == null || itemVO.getVformula() == null) {
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
