package nc.impl.hr.pub;

import java.text.DecimalFormat;

import nc.vo.pub.lang.UFDouble;

public class LogicalCalculator {
	private static String[] keyWords;

	public static String eval(String expression) {
		if (expression.substring(0, expression.indexOf("(")).toLowerCase()
				.equals("iif")) {
			return evalFun_iif(expression);
		} else if (expression.substring(0, expression.indexOf("("))
				.toLowerCase().equals("round")) {
			return evalFun_round(expression);
		}
		return expression;
	}

	private static String evalFun_round(String expression) {
		if (!expression.contains("round(")) {
			return expression;
		} else {
			String[] roundexp = expression.substring(
					expression.indexOf("(") + 1, expression.indexOf(")"))
					.split(",");

			UFDouble value = new UFDouble(SimpleCalculator.evalExp(roundexp[0]));
			double pow = Math.pow(10, Double.valueOf(roundexp[1]));

			DecimalFormat decimalFormat = new DecimalFormat("#0.00000000");
			return decimalFormat.format(Math.round(value.toDouble() * pow)
					/ pow);
		}
	}

	public static String evalFun_iif(String expression) {
		if (!expression.contains("iif(")) {
			return expression;
		} else {
			String[] iifexp = expression.substring(expression.indexOf("(") + 1,
					expression.indexOf(")")).split(",");

			boolean bCond = false;
			String strCond = iifexp[0];

			if (strCond.contains("==")) {
				Double left = SimpleCalculator.evalExp(strCond.split("==")[0]);
				Double right = SimpleCalculator.evalExp(strCond.split("==")[1]);
				bCond = left == right;
			} else if (strCond.contains("<>")) {
				Double left = SimpleCalculator.evalExp(strCond.split("<>")[0]);
				Double right = SimpleCalculator.evalExp(strCond.split("<>")[1]);
				bCond = left != right;
			} else if (strCond.contains(">=")) {
				Double left = SimpleCalculator.evalExp(strCond.split(">=")[0]);
				Double right = SimpleCalculator.evalExp(strCond.split(">=")[1]);
				bCond = left >= right;
			} else if (strCond.contains("<=")) {
				Double left = SimpleCalculator.evalExp(strCond.split("<=")[0]);
				Double right = SimpleCalculator.evalExp(strCond.split("<=")[1]);
				bCond = left <= right;
			} else if (strCond.contains(">")) {
				Double left = SimpleCalculator.evalExp(strCond.split(">")[0]);
				Double right = SimpleCalculator.evalExp(strCond.split(">")[1]);
				bCond = left > right;
			} else if (strCond.contains("<")) {
				Double left = SimpleCalculator.evalExp(strCond.split("<")[0]);
				Double right = SimpleCalculator.evalExp(strCond.split("<")[1]);
				bCond = left < right;
			} else if (strCond.contains("=")) {
				Double left = SimpleCalculator.evalExp(strCond.split("=")[0]);
				Double right = SimpleCalculator.evalExp(strCond.split("=")[1]);
				bCond = left == right;
			}

			if (bCond) {
				return iifexp[1];
			} else {
				return iifexp[2];
			}
		}
	}

	public static String[] getKeyWords() {
		if (keyWords == null || keyWords.length == 0) {
			keyWords = new String[] { "iif", "round" };
		}

		return keyWords;
	}
}
