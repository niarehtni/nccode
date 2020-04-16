package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class LeaveTypeSalaryTermHourParse extends AbstractWAFormulaParse {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 2061110567221193366L;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		String[] arguments = getArguments(formula);
		String pk_timeitem = arguments[0];
		String strStartDate = arguments[1];
		String strEndDate = arguments[2];

		if (StringUtils.isEmpty(strStartDate) || StringUtils.isEmpty(strEndDate)) {
			throw new BusinessException("起迄日期不能為空");
		}

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce((select sum(leavehour) leavehour from tbm_leavereg where dr=0 and pk_leavetype="
				+ pk_timeitem + " and effectivedate between " + strStartDate + " and " + strEndDate
				+ " and pk_psndoc=wa_cacu_data.pk_psndoc),0)");
		return fvo;
	}

}
