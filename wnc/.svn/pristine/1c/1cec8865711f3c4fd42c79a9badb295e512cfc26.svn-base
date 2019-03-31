package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.YearRestHoursFormulaPreExecutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;

/**
 * 年度补休剩余未休时数
 * 
 * @version 6.5
 * @since 2019-01-25
 * @author sunsx
 * 
 */
public class YearRestHoursFunc extends AbstractWAFormulaParse {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 2308782740772470470L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");
		IFormula excutor = new YearRestHoursFormulaPreExecutor();
		((AbstractWAFormulaParse) excutor).setFunctionVO(getFunctionVO());
		excutor.excute(formula, getContext());
		return fvo;
	}

}
