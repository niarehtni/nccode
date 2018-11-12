package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.formula.IFormulaAli;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 薪资统计数:绝对时间段
 * @author: zhangg 
 * @date: 2010-5-11 上午09:06:14
 * @since: eHR V6.0
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
@SuppressWarnings("serial")
public class PayDataOP extends AbstractWAFormulaParse implements IFormulaAli {

	/** 
	 * （参数：薪资方案,薪资项目,统计方式,起始期间,终止期间）
	 * @author zhangg on 2010-5-11 
	 * @see nc.impl.wa.func.AbstractWAFormulaParse#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula);

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String pk_wa_class = arguments[0];
		pk_wa_class = trans2OrgPk(pk_wa_class);
		
		String itemid = arguments[1];
		String type = arguments[2];
		String s_period = arguments[3];
		String e_period = arguments[4];
		
		
		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select " + type + "(data_source." + itemid + " )"); // 1
		sqlBuffer.append("  from wa_data data_source ");
		sqlBuffer.append(" where data_source.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("   and (data_source.cyear || data_source.cperiod) >= '" + s_period + "' ");
		sqlBuffer.append("   and (data_source.cyear || data_source.cperiod) <= '" + e_period + "' ");
		sqlBuffer.append("   and data_source.pk_psndoc = wa_data.pk_psndoc and  data_source.stopflag = 'N' ");
		fvo.setReplaceStr(coalesce( sqlBuffer.toString()));

		
		return fvo;
	}

	/** 
	 * @author zhangg on 2010-6-10 
	 * @throws BusinessException 
	 * @see nc.vo.wa.formula.IFormulaAli#getAliItemKeys(nc.vo.wa.classitem.WaClassItemVO, nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public String[] getAliItemKeys(WaClassItemVO itemVO, WaLoginContext context,  FunctionVO functionVO) throws BusinessException {
		if(itemVO == null || context == null || functionVO == null || itemVO.getVformula() == null){
			return null;
		}
		
		setFunctionVO(functionVO);
		setContext(context);
		
		String[] arguments = getArguments(itemVO.getVformula());

		String pk_wa_class = arguments[0];
		String itemid = arguments[1];
		//String type = arguments[2];
		String s_period = arguments[3];
		String e_period = arguments[4];
		
		//看薪资类别是否相同
		if(!pk_wa_class.equalsIgnoreCase(context.getPk_wa_class())){
			return null;
		}
		//看薪资期间是否包括了本期间
		if(e_period.compareTo(context.getWaYear() + context.getWaPeriod()) < 0){
			return null;
		}
		if(s_period.compareTo(context.getWaYear() + context.getWaPeriod()) > 0){
			return null;
		}

		return new String[]{itemid};
	}

}
