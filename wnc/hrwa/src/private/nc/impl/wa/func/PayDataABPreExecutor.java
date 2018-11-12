package nc.impl.wa.func;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;

public class PayDataABPreExecutor   extends AbstractFormulaExecutor {

	/** 
	 * @author zhangg on 2010-5-14 
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object, nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object selectSql, WaLoginContext context) throws BusinessException {
		
		
		WaClassItemVO vo = (WaClassItemVO) context.getInitData();
		

		StringBuffer sqlBuffer =  new StringBuffer(" update wa_data set "+vo.getItemkey()+" = (" + selectSql + ")   ") ;
		
		
		sqlBuffer.append(" where wa_data.pk_wa_data in "); 
		sqlBuffer.append("  (select wa_cacu_data.pk_wa_data "); 
		sqlBuffer.append("     from wa_cacu_data "); 
		sqlBuffer.append("    where wa_cacu_data.pk_wa_class = '"+context.getPk_wa_class()+"' and creator = '" + context.getPk_loginUser()+ "' )");

		getBaseDao().executeUpdate(sqlBuffer.toString());		
	}

}
