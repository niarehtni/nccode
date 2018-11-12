package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.TaxableOvertimePaysPreExcutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;

/**
 * 
 * @author: zhangg
 * @date: 2010-5-12 下午02:40:32
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class TaxableOvertimePays extends AbstractWAFormulaParse {
	private static final long serialVersionUID = 1L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		
		IFormula excutor = new TaxableOvertimePaysPreExcutor();
		excutor.excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");

		return fvo;
	}

}
