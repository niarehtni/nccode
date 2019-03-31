package nc.impl.wa.paydata.tax;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;

/**
 *
 * @author: zhangg
 * @date: 2010-1-14 上午10:43:32
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxFormulaUtil {
	/**
	 *
	 * @author zhangg on 2010-1-12
	 * @param taxFormula
	 * @return
	 * @throws BusinessException
	 * @throws ParseException
	 */
	public static TaxFormulaVO translate2FormulaVO(FunctionVO taxFunctionVO, String taxFormula) throws BusinessException {

		MessageFormat format = new MessageFormat(taxFunctionVO.getArguments());

		try {
			Object[] parts = format.parse(taxFormula);

			if (parts.length != 2 && parts.length != 4) {
				throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0271")/*@res "中的参数个数或者格式不正确!"*/);
			}

			String base = null;
			String class_type = null;
			String yearAward = null;
			
			String monthpay = null;
			
			String periodTimesField = null;
			if (parts.length == 2) {// 省略第三个参数为tax(base)
				 class_type = parts[0].toString();
				 base = parts[1].toString().trim();
			}
			if (parts.length == 4) {// tax(base, class_type)
				class_type = parts[0].toString().trim();
				 base = parts[1].toString().trim();//本次扣税基数
				 yearAward = parts[1].toString().trim();//年度奖金是一样的
				 monthpay = parts[2].toString().trim();
				 periodTimesField = parts[3].toString().trim();
				 
			}
			// 校验Class_type 参数
			if (class_type != null) {
// {mod:新个税补丁}
// begin
				if (class_type.equals(TaxFormulaVO.CLASS_TYPE_NORMAL) || class_type.equals(TaxFormulaVO.CLASS_TYPE_YEAR) 
						|| class_type.equals(TaxFormulaVO.CLASS_TYPE_TOTAL_YEAR)) {
// end
					Logger.info(taxFormula + "Class_type 参数取值正确!");
				} else {
					throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0272")/*@res "Class_type 参数取值不正确!, 应为0  或者 1"*/);
				}
			}

			TaxFormulaVO taxFormulaVO = new TaxFormulaVO();
			taxFormulaVO.setBase(base);
			taxFormulaVO.setClass_type(class_type);
			taxFormulaVO.setYearAward(yearAward);
			taxFormulaVO.setMonthPay(monthpay);
			taxFormulaVO.setPeirodTimesField(periodTimesField);
			
			return taxFormulaVO;

		} catch (ParseException e) {
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0273")/*@res "扣稅錯誤"*/);
		}

	}



	public static String getCheckTaxFormula(FunctionVO taxFunctionVO,WaClassItemVO itemVO) throws BusinessException {
		String taxFormula = TaxFormulaUtil.getTaxFormula(taxFunctionVO, itemVO.getVformula());
/*		if (taxFormula == null) {
			itemVO.getVformula();
		}*/

		String replaceFormula = translate2FormulaVO(taxFunctionVO, taxFormula).getBase();
		// 校验base 参数，因为base可以是表达式故可以使用过去的 数据库验证方式。返回该值

		String vformula = itemVO.getVformula().replaceAll(taxFormula, replaceFormula);

		return vformula;
	}

	/**
	 * 将公式替换成相应的可以用执行的语句
	 *
	 * @author zhangg on 2010-1-12
	 * @see nc.vo.wa.paydata.IFormula#checkReplace(java.lang.String)
	 */

	public static String getTaxFormula(FunctionVO taxFunctionVO, String itemFormula) throws BusinessException {

		// 第1步：检查是否存在tax（）函数
		// tax函数.x表示括号间有0个以上字符， 真正表达式为最大匹配
		// \\s*表示tax和（间有0个以上空格
		Pattern pattern = Pattern.compile(taxFunctionVO.getPattern());
		Matcher matcher = pattern.matcher(itemFormula);
		if (!matcher.matches()) {
			return null;
		}

		Vector<String> formulaVector = new Vector<String>();

		// 含有tax函数
		while (matcher.find()) {
			String formula = matcher.group();
			formulaVector.add(formula);
		}

		if (formulaVector.size() > 1) {
			throw new BusinessException(itemFormula + ResHelper.getString("6013salarypmt","06013salarypmt0274")/*@res "含有1个以上tax函数, 不支持!"*/);
		}

		String taxFormula = formulaVector.get(0);

		return taxFormula;
	}
}