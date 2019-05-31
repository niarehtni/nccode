/**
 * @Description: TODO
 * @author yejk
 * @date 2018-9-18 
 */
package nc.impl.ta.formula.parser.func;

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
public class PartWHLeavePercentFuncParser implements IFormulaParser {

	private static final long serialVersionUID = -3775904266122916546L;

	/**
	 * 
	 * @Description: 执行解析
	 * @author yejk
	 * @date 2018-9-18
	 * @param pk_org
	 * @param formula
	 * @param params
	 * @throws BusinessException
	 * @return
	 */
	@Override
	public String parse(String pk_org, String formula, Object... params) throws BusinessException {
		FuncParseSplitResult result = FormulaParseHelper.findFirstFunc(formula, "partWHLeavePercent");
		if (result == null) {
			return formula;
		}
		String replaceStr = "isnull((select tbm_partWhLeave_temp.cpercent from tbm_partWhLeave_temp where tbm_partWhLeave_temp.pk_psndoc = tbm_leavebalance.pk_psndoc),0)";
		formula = result.getPrePart() + replaceStr + result.getPostPart();
		return formula;
	}

}
