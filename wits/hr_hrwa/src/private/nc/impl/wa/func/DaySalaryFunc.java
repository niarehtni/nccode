package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.DaySalaryFormulaPreExcutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
/**
 * 
 * @author ward
 * @date 2018��9��19��22:32:50
 * @desc ��н���ܺ���
 *
 */
public class DaySalaryFunc extends AbstractWAFormulaParse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 388765426005459247L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");
		IFormula excutor = new DaySalaryFormulaPreExcutor();
		((AbstractWAFormulaParse)excutor).setFunctionVO(getFunctionVO());
		excutor.excute(formula, getContext());
		return fvo;
	}

}