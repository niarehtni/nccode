package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @since 2019-05-28
 * @author kk
 * @description 福委會累計代扣福利金函数
 * @description the sum of TWSP0015 of all salary scheme between period
 *
 */
public class CalPaidWelfareParse extends AbstractPreExcutorFormulaParse {

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(coalesce("wa_cacu_data.cacu_value"));
		return fvo;
	}

	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {
		IFormula excutor = new CalPaidWelfareExecutor();
		excutor.excute(null, getContext());
	}

}
