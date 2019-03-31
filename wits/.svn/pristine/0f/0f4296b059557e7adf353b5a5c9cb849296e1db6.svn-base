package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.DaySalaryFormulaPreExcutor;
import nc.impl.wa.specialleave.precacu.ALPaidHoursFormulaPreExcutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;

public class ALPaidHoursFunc extends AbstractWAFormulaParse{
	private static final long serialVersionUID = 388765426005450247L;
	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");
		IFormula excutor = new ALPaidHoursFormulaPreExcutor();
		((AbstractWAFormulaParse)excutor).setFunctionVO(getFunctionVO());
		excutor.excute(formula, getContext());
		return fvo;
	}

}
