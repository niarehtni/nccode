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
 * @date: 2010-5-13 ����03:21:26
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
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
	// * ���ö�ջ�İ������� IIf(floor(@����@) >= 10,2000, IIf(floor(@����@) >= 8 ����
	// floor(@����@
	// * ) <10,1500, IIf(floor(@����@) >= 5 ���� floor(@����@) <8,100,500)))
	// *
	// *
	// * ת���� ��� floor(@����@) >= 10 �� 2000 ��� floor(@����@) >= 8 ���� floor(@����@ ) <10
	// ��
	// * 1500 ��� floor(@����@) >= 5 ���� floor(@����@) <8 �� 1000 ���� 500
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
	// // ���Ȳ��ҵ� ��һ�� iif ��ʼλ�ã���� һ��iif���ڵ�λ�á�
	// firsIIfstart = REUtil.REindexOf(pattern, formula);
	//
	// // //���� IIf �����Ľ���λ��
	// int firsIIfend = firsIIfstart;
	// String preFormular = formula.substring(0, firsIIfstart);
	//
	// String bodyString = formula.substring(firsIIfstart);
	// // ���ҵ�һ�� '(' ���ڵ�λ��
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
	// // ���� ���Ų�ƥ��
	// // throw new Exception("���� ���Ų�ƥ�� ");
	// }
	// }
	// pos++;
	// }
	//
	// // pos �� illString �е�λ��
	// if (!bracketStack.isEmpty() && pos >= illString.length()) {
	// // iif ���� ���Ų�ƥ��
	// // throw new Exception("iif ���� ���Ų�ƥ��");
	// } else {
	// firsIIfend = (pos - 1) + (preFormular.length()) + (tempPos + 1);
	// }
	//
	// // ��ȡ iif ����
	// String iiffunc = formula.substring(firsIIfstart, firsIIfend + 1);
	// // iif ����Ĺ�ʽ ��
	//
	// // ���� 'iif('�ĸ��� ,������ȥ��
	// int iifcount = REUtil.REAppersTimes(pattern, iiffunc);
	// iiffunc = REUtil.REReplace(pattern, iiffunc, "");
	//
	// // IWaKeyword thenword =
	// // WaKeywordFactory.getInstance().getKeyword("then");
	// // IWaKeyword elseword =
	// // WaKeywordFactory.getInstance().getKeyword("else");
	//
	// //
	// // ���� ","����ʽ�ֽ�
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
	// // ȥ�������)
	// elseresult = elseresult.substring(0, elseresult.length() - iifcount);
	//
	// SqlFragment fragment = new SqlFragment();
	// fragment.setValue(elseresult);
	// list.add(fragment);
	//
	// String ifresult = varStatck.pop();
	// String condition = varStatck.pop();
	//
	// //ת��condition �еġ� &&�����롡||
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
			throw new BusinessRuntimeException("iif����ֻ�ܵ���ʹ��!"/* -=notranslate=- */);
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
	// * IIf( floor(@����@) >= 10,2000, IIf(floor(@����@) >= 8 ���� floor(@����@ )
	// <10,1500, IIf(floor(@����@) >= 5 ���� floor(@����@) <8,100,500)) )
	// *
	// *
	// * iif( fun(var1,var2)>10,2000,IIf(fun(var1,var2) >= 8 &&
	// fun(var1,var2)<10,1500, IIf(fun(var1,var2) >= 5 && fun(var1,var2)
	// <8,100,500)) )
	// *
	// * �ֽ�ɶԵ���ʽ
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
	// //���������iif ������Ϊ�Ѿ��ֽ⵽ĩ����
	// stack.add(formula);
	// return stack;
	// }else{
	// //����iif �� ����Ѱ�� ƽ���
	//
	// //�����iif��ʼ��ȥ��iif�����ġ�)��----��Ƥ
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
	// //ʵ��ƽ�⣬���зָ�
	// String s = formula.substring(statementbegin,pos);
	//
	// //������S ���еݹ�
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
	// //ʵ��ƽ�⣬���зָ�
	// String s = iifbody.substring(statementbegin,pos);
	//
	// //������S ���еݹ�
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
	// * ��һ���ַ������ж������Ƿ�ƽ��.
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
