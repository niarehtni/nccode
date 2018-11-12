package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.StatisticLeaveChargePreExcutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;

public class StatisticLeaveCharge extends AbstractWAFormulaParse{

	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");

		IFormula excutor = new StatisticLeaveChargePreExcutor();
		excutor.excute(formula, getContext());
		return fvo;
	}

}
