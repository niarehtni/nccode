package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

public class LeaveTypeCurMonSalaryHourParse extends LeaveTypeSalaryHourParse {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -5125290159697583014L;
	private int INTCOMP = 1; // 1: ÔÚÖ°

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] params = this.getArguments(formula);
		this.setPk_leaveitem(params[0]);

		excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String cyear = getContext().getCyear();
		String cperiod = getContext().getCperiod();
		String pk_waclass = getContext().getPk_wa_class();
		String pk_creator = getContext().getPk_loginUser();

		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce((select curmon_hours from wa_cacu_leavehour where intcomp = " + INTCOMP
				+ " and pk_timeitem='" + this.getPk_leaveitem() + "' and cyear='" + cyear + "' and cperiod='" + cperiod
				+ "' and pk_wa_class='" + pk_waclass + "' and creator='" + pk_creator
				+ "' and pk_psndoc = wa_cacu_data.pk_psndoc)" + ", 0)");
		return fvo;
	}
}
