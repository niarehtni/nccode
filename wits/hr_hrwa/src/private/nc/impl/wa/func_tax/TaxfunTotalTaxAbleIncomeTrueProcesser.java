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
 * 改进后的累计应纳税所得额算法
 * zhaochxs 由原来的对应纳税所得额的合计改为合计（本次扣税基数-已扣税基数）-累计专项扣除，去除中间项的繁琐计算
 * 
 * 
 * 新税改_累计应纳税所得额
sum(itemkey) where cyearperiod in (taxyear（period）） and pk_org and pk_psndoc and pk_waclass in (合并计税 刨除 本方案） and CHECKFLAG = 'Y'
union
sum(itemkey) where cyearperiod in (taxyear(period) and pk_org and pk_psndoc and pk_waclass  = 本方案
union
sum(itemkey) where cyearperiod in (taxyear（period）） and pk_org and pk_psndoc and pk_waclass in (合并计税） and CHECKFLAG = 'Y' and cyearperiod < 当前发薪期间
 * 
 * sum()
 * 1、当前期间当前薪资类别
 * 2、多次发薪当前期间刨除当前薪资类别
 * 3、合并计税当前期间刨除当前薪资了别
 * 4、历史薪资期间
 * 
 * @author: xuhw
 * @date:
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxfunTotalTaxAbleIncomeTrueProcesser extends AbstractWAFormulaParse
		implements IFormulaAli {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2999834882617313191L;


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
		cperiod = context.getCperiod();
		StringBuffer sbsql = new StringBuffer();
		String[] arguments = getArguments(formula);
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String itemkey1 = arguments[0];
		String itemkey2 = arguments[1];
		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();

		// -- 统一员工 同一个人力资源组织 合并计税方案 审核过的数据的合计
		sqlBuffer.append(" (SELECT ");
//		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
//			//zhaochxs 由原来的应纳税所得额改为本次扣税基数-已扣税基数，去除中间项的繁琐计算
//			sqlBuffer.append(" 	isnull(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = wa_data.cyear||wa_data.cperiod then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
//		}else {
//			sqlBuffer.append(" 	nvl(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = wa_data.cyear||wa_data.cperiod then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
//		}
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			//zhaochxs 由原来的应纳税所得额改为本次扣税基数-已扣税基数，去除中间项的繁琐计算
			sqlBuffer.append(" 	isnull(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = '"+ cyear + cperiod +"' then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
		}else {
			sqlBuffer.append(" 	nvl(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = '"+ cyear + cperiod +"' then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
		}
		sqlBuffer.append(" from  wa_data wadata  ");
		//过滤掉汇总薪资类别方案,按月计税方案
		sqlBuffer.append(" 	inner join wa_waclass waclass on wadata.pk_wa_class = waclass.pk_wa_class and waclass.collectflag = 'N'	and coalesce(waclass.yearbonusflag,'N') = 'N' ");			
		sqlBuffer.append(" WHERE          ");
		sqlBuffer.append(" 	WADATA.PK_PSNDOC = wa_data.pk_psndoc           ");
		//zhaocxhs 按照纳税组织，组织关系，累计标志进行累计
		//old	sqlBuffer.append(" AND wadata.pk_org = wa_data.pk_org        ");
		sqlBuffer.append(" AND wadata.taxorg = wa_data.taxorg        ");
		sqlBuffer.append(" AND wadata.taxsumuid = wa_data.taxsumuid        ");
		sqlBuffer.append(" AND wadata.pk_psnorg = wa_data.pk_psnorg        ");
		sqlBuffer.append(" AND (       ");
		//zhaochxs当前期间的合并计税方案已审核数据
		sqlBuffer.append("  ( WADATA.cyear  " + getBDSql() + " WADATA.cperiod = "+cyear+cperiod+" and  ( (  WADATA.PK_WA_CLASS IN ( SELECT PK_WACLASS FROM WA_TAXGRPMEMBER WHERE pk_taxgroup =      ");
		sqlBuffer.append(" 		( SELECT PK_TAXGROUP FROM WA_TAXGRPMEMBER WHERE pk_waclass = '" + context.getPk_prnt_class() + "' or pk_waclass = '" + context.getPk_wa_class() + "'  )  )     "); 
		sqlBuffer.append(" 		AND  WADATA.CHECKFLAG = 'Y'    ) ");
		//当前期间当前方案
		sqlBuffer.append(" 		OR  WADATA.PK_WA_CLASS = '" + context.getPk_wa_class() + "' ");
		//当前期间合并计税下已审核的多次发薪
		sqlBuffer.append(" 		OR (WADATA.pk_wa_class IN ( SELECT PK_CHILDCLASS FROM WA_INLUDECLASS WHERE PK_PARENTCLASS IN      ");
		sqlBuffer.append(" 	( SELECT PK_WACLASS FROM WA_TAXGRPMEMBER WHERE pk_taxgroup =      ");
		sqlBuffer.append(" 		( SELECT PK_TAXGROUP FROM WA_TAXGRPMEMBER WHERE pk_waclass = '" + context.getPk_prnt_class() + "' or pk_waclass = '" + context.getPk_wa_class() + "' ) )  ");
		sqlBuffer.append("   or PK_PARENTCLASS='"+context.getPk_prnt_class()+"' ) ");
		sqlBuffer.append("  and WADATA.CHECKFLAG = 'Y')   ) )    ");
		//已审核的历史期间
		sqlBuffer.append(" 		OR (  WADATA.cyear  " + getBDSql() + " WADATA.cperiod < "+cyear+cperiod+") and  WADATA.CHECKFLAG = 'Y' )     ");
		
		
		
		
		sqlBuffer.append(" AND WADATA.cyear  " + getBDSql() + " WADATA.cperiod IN (      ");
		sqlBuffer.append(" 	SELECT        ");
		//zhaochxs sqlserver2008 不支持CONCAT，不进行判断，底层自动转换
		sqlBuffer.append(" 		cyear || cperiod     ");
		sqlBuffer.append(" 	FROM          ");
		sqlBuffer.append(" 		wa_period ");
		sqlBuffer.append(" 	WHERE         ");
		sqlBuffer.append(" 		PK_PERIODSCHEME = (  ");
		sqlBuffer.append(" 			SELECT");
		sqlBuffer.append(" 				PK_PERIODSCHEME      ");
		sqlBuffer.append(" 			FROM  ");
		sqlBuffer.append(" 				wa_waclass   ");
		sqlBuffer.append(" 			WHERE ");
		sqlBuffer.append(" 				PK_WA_CLASS = '" + context.getPk_wa_class() + "'     ");
		sqlBuffer.append(" 		)         ");
		sqlBuffer.append(" 	AND TAXYEAR = (          ");
		sqlBuffer.append(" 		SELECT    ");
		sqlBuffer.append(" 			period.TAXYEAR   ");
		sqlBuffer.append(" 		FROM      ");
		sqlBuffer.append(" 			wa_waclass waclass       ");
		sqlBuffer.append(" 		INNER JOIN wa_period period ON period.PK_PERIODSCHEME = WACLASS.PK_PERIODSCHEME         ");
		sqlBuffer.append(" 		WHERE     ");
		sqlBuffer.append(" 			WACLASS.PK_WA_CLASS = '" + context.getPk_wa_class() + "' ");
		sqlBuffer.append(" 		AND PERIOD.CYEAR = WACLASS.CYEAR ");
		sqlBuffer.append(" 		AND PERIOD.CPERIOD = WACLASS.CPERIOD ");
		sqlBuffer.append(" 	)             ");
		sqlBuffer.append(" )) ");
		
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

		return arguments;
	}
	

	private String getBDSql() {
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			return "+";
		} else {
			return "||";
		}
	}
}
