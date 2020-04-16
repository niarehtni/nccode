package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.YearRestHoursFormulaPreExecutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;

/**
 * ��Ȳ���ʣ��δ��ʱ��
 * 
 * @version 6.5
 * @since 2019-01-25
 * @author sunsx
 * 
 */
public class LeaveYearRestHoursFunc extends AbstractWAFormulaParse {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 2308782740772470470L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		IFormula excutor = new YearRestHoursFormulaPreExecutor(true);
		((AbstractWAFormulaParse) excutor).setFunctionVO(getFunctionVO());
		excutor.excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce((select hours from wa_cacu_yearresthour where dr=0 and isleave='Y' and pk_wa_class='"
				+ context.getPk_wa_class()
				+ "' and creator='"
				+ context.getPk_loginUser()
				+ "' and pk_psndoc=wa_cacu_data.pk_psndoc),0)");
		return fvo;
	}

}
