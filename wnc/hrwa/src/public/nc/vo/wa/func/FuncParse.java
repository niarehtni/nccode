package nc.vo.wa.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.hr.utils.SQLHelper;
import nc.impl.hr.formula.parser.FormulaParseHelper;
import nc.vo.hr.formula.FuncParseSplitResult;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.pub.HRLogicParse;
import nc.vo.pub.BusinessRuntimeException;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: zhangg
 * @date: 2010-5-13 下午03:21:26
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class FuncParse {
	public final String blank = " ";
	// private static HashMap<String, String> tableWherePare = null;

	private static HashMap<String, String> tableWherePare = new HashMap<String, String>() {
		{
			put("wa_cacu_data", "wa_cacu_data.pk_wa_data = wa_data.pk_wa_data");
			put("bd_psndoc", "bd_psndoc.pk_psndoc = wa_data.pk_psndoc");
			put("bd_psnjob", "bd_psnjob.pk_psnjob = wa_data.pk_psnjob");
			put("hi_psnjob",
					"hi_psnjob.pk_psnjob = (select pk_psnjob from wa_cacu_psnjob where pk_wa_class='#WACLASS#' and creator='#CREATOR#' and pk_psndoc = wa_data.pk_psndoc)");
		}
	};

	/**
	 * @author zhangg on 2010-5-17
	 * @return the tableWherePare
	 */
	public static HashMap<String, String> getTableWherePare() {
		return tableWherePare;
	}

	// /**
	// * 利用堆栈的帮助，将 IIf(floor(@工龄@) >= 10,2000, IIf(floor(@工龄@) >= 8 并且
	// floor(@工龄@
	// * ) <10,1500, IIf(floor(@工龄@) >= 5 并且 floor(@工龄@) <8,100,500)))
	// *
	// *
	// * 转换成 如果 floor(@工龄@) >= 10 则 2000 如果 floor(@工龄@) >= 8 并且 floor(@工龄@ ) <10
	// 则
	// * 1500 如果 floor(@工龄@) >= 5 并且 floor(@工龄@) <8 则 1000 否则 500
	// */
	//
	// public static List<SqlFragment> parse(String formula) {
	// List<SqlFragment> list = new LinkedList<SqlFragment>();
	// String pattern = "\\s*iif\\s*\\(";
	// int firsIIfstart = REUtil.REindexOf(pattern, formula);
	// if (firsIIfstart < 0) {
	// SqlFragment fragment = new SqlFragment();
	// fragment.setValue(formula);
	// list.add(fragment);
	// return list;
	// } else {
	// // 首先查找到 第一个 iif 起始位置，最后 一个iif所在的位置。
	// firsIIfstart = REUtil.REindexOf(pattern, formula);
	//
	// // //查找 IIf 函数的结束位置
	// int firsIIfend = firsIIfstart;
	// String preFormular = formula.substring(0, firsIIfstart);
	//
	// String bodyString = formula.substring(firsIIfstart);
	// // 查找第一个 '(' 所在的位置
	// int tempPos = bodyString.indexOf("(");
	// String illString = bodyString.substring(tempPos + 1);
	// Stack<String> bracketStack = new Stack<String>();
	// bracketStack.push("(");
	// int pos = 0;
	// while (!bracketStack.isEmpty() && pos < illString.length()) {
	// String c = illString.substring(pos, pos + 1);
	// if (c.equals("(")) {
	// bracketStack.push(c);
	// } else if (c.equals(")")) {
	// String leftbracket = bracketStack.pop();
	// if (!leftbracket.equals("(")) {
	// // 左右 括号不匹配
	// // throw new Exception("左右 括号不匹配 ");
	// }
	// }
	// pos++;
	// }
	//
	// // pos 是 illString 中的位置
	// if (!bracketStack.isEmpty() && pos >= illString.length()) {
	// // iif 函数 括号不匹配
	// // throw new Exception("iif 函数 括号不匹配");
	// } else {
	// firsIIfend = (pos - 1) + (preFormular.length()) + (tempPos + 1);
	// }
	//
	// // 提取 iif 函数
	// String iiffunc = formula.substring(firsIIfstart, firsIIfend + 1);
	// // iif 后面的公式 。
	//
	// // 查找 'iif('的个数 ,并将其去掉
	// int iifcount = REUtil.REAppersTimes(pattern, iiffunc);
	// iiffunc = REUtil.REReplace(pattern, iiffunc, "");
	//
	// // IWaKeyword thenword =
	// // WaKeywordFactory.getInstance().getKeyword("then");
	// // IWaKeyword elseword =
	// // WaKeywordFactory.getInstance().getKeyword("else");
	//
	// //
	// // 利用 ","将公式分解
	// String split = "\\,";
	// Stack<String> varStatck = new Stack<String>();
	// String[] ss = iiffunc.split(split);
	// for (int index = 0; index < ss.length; index++) {
	// if (!StringUtils.isBlank(ss[index])) {
	// varStatck.push(ss[index]);
	// }
	//
	// }
	//
	// String elseresult = varStatck.pop();
	// // 去掉多余的)
	// elseresult = elseresult.substring(0, elseresult.length() - iifcount);
	//
	// SqlFragment fragment = new SqlFragment();
	// fragment.setValue(elseresult);
	// list.add(fragment);
	//
	// String ifresult = varStatck.pop();
	// String condition = varStatck.pop();
	//
	// //转换condition 中的　 &&　　与　||
	// fragment = new SqlFragment();
	// fragment.setValue(ifresult);
	// fragment.setCondition(HRLogicParse.parseCondition(condition));
	// list.add(fragment);
	//
	// while (!varStatck.isEmpty()) {
	//
	// ifresult = varStatck.pop();
	// condition = varStatck.pop();
	// fragment = new SqlFragment();
	// fragment.setValue(ifresult);
	// fragment.setCondition(HRLogicParse.parseCondition(condition));
	// list.add(fragment);
	//
	// }
	// }
	// return list;
	// }

	public static List<SqlFragment> parse(String formula) {
		FuncParseSplitResult result = FormulaParseHelper.findFirstFunc(formula, "iif");
		List<SqlFragment> list = new ArrayList<SqlFragment>();
		if (result == null) {
			SqlFragment fragment = new SqlFragment();
			fragment.setValue(formula);
			list.add(fragment);
			return list;
		}
		String prePart = result.getPrePart();
		String postPart = result.getPostPart();
		if (StringUtils.isNotBlank(prePart) || StringUtils.isNotBlank(postPart)) {
			Logger.error("the formula is:" + formula);
			throw new BusinessRuntimeException("iif函数只能单独使用!"/* -=notranslate=- */);
		}
		String[] args = result.getArgs();
		String cond = args[0];
		String ifValue = args[1];
		String elseValue = args[2];
		List<SqlFragment> ifList = parse(ifValue);
		List<SqlFragment> elseList = parse(elseValue);
		for (SqlFragment fragment : ifList) {
			String condition = fragment.getCondition();
			if (StringUtils.isNotEmpty(condition))
				condition = "(" + condition + ")";
			condition = SQLHelper.appendExtraCond(condition, cond);
			fragment.setCondition(HRLogicParse.parseCondition(condition));
		}
		list.addAll(elseList);
		list.addAll(ifList);
		return list;
	}

	// /**
	// *
	// * IIf( floor(@工龄@) >= 10,2000, IIf(floor(@工龄@) >= 8 并且 floor(@工龄@ )
	// <10,1500, IIf(floor(@工龄@) >= 5 并且 floor(@工龄@) <8,100,500)) )
	// *
	// *
	// * iif( fun(var1,var2)>10,2000,IIf(fun(var1,var2) >= 8 &&
	// fun(var1,var2)<10,1500, IIf(fun(var1,var2) >= 5 && fun(var1,var2)
	// <8,100,500)) )
	// *
	// * 分解成对的形式
	// *
	// * @param iifbody
	// * @return
	// */
	// private Stack<String> splitIIfbody(Stack<String> stack ,String formula) {
	//
	//
	// String pattern = "\\s*iif\\s*\\(";
	// int firsIIfstart = REUtil.REindexOf(pattern, formula);
	//
	// if (firsIIfstart < 0) {
	// //如果不包含iif ，则认为已经分解到末级。
	// stack.add(formula);
	// return stack;
	// }else{
	// //包含iif ， 继续寻找 平衡点
	//
	// //如果已iif开始。去掉iif与最后的“)”----扒皮
	//
	//
	//
	//
	//
	// int statementbegin = 0;
	//
	// int pos = 0;
	// Stack<String> bracketStack = new Stack<String>();
	// while (pos < formula.length()) {
	// String c = formula.substring(pos, pos + 1);
	// if (c.equals("(")) {
	// bracketStack.push(c);
	// } else if (c.equals(")")) {
	// String leftbracket = bracketStack.pop();
	// }
	//
	// if (c.equals(",") && bracketStack.isEmpty()) {
	// //实现平衡，进行分割
	// String s = formula.substring(statementbegin,pos);
	//
	// //对下面S 进行递归
	//
	//
	// // varStatck.push(s);
	// statementbegin = pos+1;
	// }
	// pos++;
	//
	// }
	// }
	//
	//
	// List<SqlFragment> list = new LinkedList<SqlFragment>();
	//
	// Stack<String> varStatck = new Stack<String>();
	//
	// int statementbegin = 0;
	//
	// int pos = 0;
	// Stack<String> bracketStack = new Stack<String>();
	// while (pos < iifbody.length()) {
	// String c = iifbody.substring(pos, pos + 1);
	// if (c.equals("(")) {
	// bracketStack.push(c);
	// } else if (c.equals(")")) {
	// String leftbracket = bracketStack.pop();
	// }
	//
	//
	// if (c.equals(",") && bracketStack.isEmpty()) {
	// //实现平衡，进行分割
	// String s = iifbody.substring(statementbegin,pos);
	//
	// //对下面S 进行递归
	//
	//
	// varStatck.push(s);
	// statementbegin = pos+1;
	// }
	// pos++;
	//
	// }
	//
	// //z
	// if(statementbegin<iifbody.length()){
	//
	// String s = iifbody.substring(statementbegin,pos);
	// varStatck.push(s);
	// }
	//
	// return varStatck;
	//
	// }

	// /**
	// * 给一个字符串，判断括号是否平衡.
	// *
	// * @param iifbody
	// * @return
	// */
	// private boolean bracketBalanced(String iifbody) {
	// if (StringUtils.isEmpty(iifbody)) {
	// return true;
	// }
	// if (iifbody.length() == 1) {
	// if (iifbody.equals("(") || iifbody.equals(")")) {
	// return false;
	// } else {
	// return true;
	// }
	// }
	// int pos = 0;
	// Stack<String> bracketStack = new Stack<String>();
	// while (pos < iifbody.length()) {
	// String c = iifbody.substring(pos, pos + 1);
	// if (c.equals("(")) {
	// bracketStack.push(c);
	// } else if (c.equals(")")) {
	// String leftbracket = bracketStack.pop();
	// }
	// }
	// return bracketStack.isEmpty();
	// }

	public static String addSourceTable2Value(String valueFormula) {
		if (valueFormula == null) {
			return valueFormula;
		}
		List<String> tableList = new LinkedList<String>();
		String where = null;

		Iterator<String> iterator = getTableWherePare().keySet().iterator();
		while (iterator.hasNext()) {
			String table = iterator.next();
			if (valueFormula.indexOf(table) >= 0) {
				tableList.add(table);
			}
		}
		if (tableList.size() > 0) {
			String tables = FormatVO.formatListToString(tableList, "");
			for (String key : tableList) {
				if (where == null) {
					where = getTableWherePare().get(key);
				} else {
					where = where + " and " + tableWherePare.get(key);
				}
			}
			valueFormula = "select " + valueFormula + " from " + tables + " where " + where;
		}
		return valueFormula;
	}

	public static String addSourceTable2Conditon(String condtionFormula) {
		if (condtionFormula == null) {
			return condtionFormula;
		}
		List<String> tableList = new LinkedList<String>();
		String where = null;

		Iterator<String> iterator = getTableWherePare().keySet().iterator();
		while (iterator.hasNext()) {
			String table = iterator.next();
			if (condtionFormula.indexOf(table) >= 0) {
				tableList.add(table);
			}
		}
		if (tableList.size() > 0) {
			tableList.add("wa_data");
			String tables = FormatVO.formatListToString(tableList, "");
			for (String key : tableList) {
				if (where == null) {
					where = getTableWherePare().get(key);
				} else {
					if (tableWherePare.get(key) != null) {
						where = where + " and " + tableWherePare.get(key);
					}
				}
			}

			condtionFormula = " and wa_data.pk_wa_data in (select wa_data.pk_wa_data from " + tables + " where "
					+ where + WherePartUtil.formatAddtionalWhere(condtionFormula) + ")";
		}
		return condtionFormula;
	}

}
