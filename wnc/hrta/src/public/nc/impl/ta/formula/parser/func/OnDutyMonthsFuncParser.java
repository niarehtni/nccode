/**
 * @Description: TODO
 * @author yejk
 * @date 2018-9-18 
 */
package nc.impl.ta.formula.parser.func;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.impl.hr.formula.parser.FormulaParseHelper;
import nc.impl.hr.formula.parser.IFormulaParser;
import nc.vo.hr.formula.FuncParseSplitResult;
import nc.vo.pub.BusinessException;

/**
 * #21390 部分工时年假比例函数解析器
 * 
 * @author yejk
 * @date 2018-9-18
 */
public class OnDutyMonthsFuncParser implements IFormulaParser {

	private static final long serialVersionUID = -3775904266122916546L;

	/**
	 * 
	 * @Description: 执行解析
	 * @author ssx
	 * @date 2020-06-10
	 * @param pk_org
	 * @param formula
	 * @param params
	 * @throws BusinessException
	 * @return
	 */
	@Override
	public String parse(String pk_org, String formula, Object... params) throws BusinessException {
		FuncParseSplitResult result = FormulaParseHelper.findFirstFunc(formula, "onDutyMonths");
		if (result == null) {
			return formula;
		}
		String replaceStr = "isnull((select tbm_cacu_annalleave.ondutymonth from tbm_cacu_annalleave where tbm_cacu_annalleave.pk_leavebalance = tbm_leavebalance.pk_leavebalance and creator='"
				+ InvocationInfoProxy.getInstance().getUserId() + "'),0)";
		formula = result.getPrePart().replace("onDutyMonths()", replaceStr) + replaceStr
				+ result.getPostPart().replace("onDutyMonths()", replaceStr);
		return formula;
	}

}
