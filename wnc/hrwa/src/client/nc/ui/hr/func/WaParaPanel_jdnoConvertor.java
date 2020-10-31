package nc.ui.hr.func;

import java.util.HashMap;
import java.util.Map;
import nc.hr.utils.ResHelper;
import nc.ui.hr.itemsource.view.WaConvertor;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.item.WaItemVO;
import org.apache.commons.lang.StringUtils;

public class WaParaPanel_jdnoConvertor extends WaConvertor {
	// waOtherPeriodData(1001Z71000000000TJAR,f_2,0)
	//
	// 其它期间薪资额(x测试国际化,扣款合计,本期间)

	public static final String sdje = ResHelper.getString("6013salaryctymgt",
			"06013salaryctymgt0206")/* @res "时点发放金额" */;
	public static final String yffje = ResHelper.getString("6013salaryctymgt",
			"06013salaryctymgt0207")/* @res "原发放金额" */;
	public static final String xffje = ResHelper.getString("6013salaryctymgt",
			"06013salaryctymgt0208")/* @res "现发放金额" */;

	private static Map<String, String> map = new HashMap<String, String>();
	// private static Map<String, String> classItemMap = new HashMap<String,
	// String>(); //薪资方案项目
	// private static Map<String, String> classItemMap = new HashMap<String,
	// String>(); //薪资方案项目
	static {
		String[] ml = { "sum", "avg", "max", "min" };
		String[] mlDefault = new String[] {
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0188")/*
																				 * @
																				 * res
																				 * "取和"
																				 */,
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0189")/*
																				 * @
																				 * res
																				 * "取平均值"
																				 */,
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0190")/*
																				 * @
																				 * res
																				 * "取最大值"
																				 */,
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0191") /*
																				 * @
																				 * res
																				 * "取最小值"
																				 */};/*
																					 * -=
																					 * notranslate
																					 * =
																					 * -
																					 */

		for (int i = 0; i < ml.length; i++) {
			map.put(ml[i], mlDefault[i]);
		}

	}

	@Override
	public String preConvert(String formula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String postConvert(String formula) {
		// TODO Auto-generated method stub
		if (StringUtils.isNotEmpty(formula)) {

			String param = formula.substring(formula.indexOf("(") + 1,
					formula.indexOf(")"));
			String[] params = param.split(",");
			if (formula.contains("waAdjustDoc")) {
				String[] params2 = params[1].split("#");
				if (null != params && params.length > 1 && null != params2
						&& params2.length > 0) {

					// 翻译时点发放金额、原发放金额、现发放金额
					if (StringUtils.isEmpty(map.get(params2[0]))
							&& StringUtils.isEmpty(map.get(params[0]))) {
						String className = "";
						// 时点发放金额
						if (params2.length == 1 && params2[0].equals("0")) {
							className = sdje;
						} else if (params2.length > 1 && params2[1].equals("1")) {
							className = yffje;
						} else if (params2.length > 1 && params2[1].equals("2")) {
							className = xffje;
						}
						// 特殊处理时点发放金额、原发放金额、现发放金额等于后面的定调资档案项目主键的情况
						formula = formula.replaceFirst(params[0], "012");
						params[0] = "012";
						map.put("012", className);
					}
					// 特殊处理时点发放金额的"0"
					if (params2.length == 1 && params2[0].equals("0")) {
						String className = "";
						map.put(params[1], className);
					}
					// 处理原发放金额、现发放金额
					if (params2.length > 1
							&& StringUtils.isEmpty(map.get(params2[2]))) {
						String sql = " itemkey='" + params2[2] + "'";
						String className = getVoName(sql, WaItemVO.class);
						map.put(params[1], className);
					}
					for (int i = 0; i < params.length; i++) {
						formula = formula.replaceAll(
								params[i],
								null == map.get(params[i]) ? params[i] : map
										.get(params[i]));
					}
					// 替换时点发放金额后面的","
					if (params2.length == 1 && params2[0].equals("0")) {
						formula = formula.replaceAll(",", "");
					}
				}
			} else {
				if (null != params && params.length > 1) {
					// String[] params2 = params[1].split("#");
					// if(null!=params2&&params2.length>1){
					// if(StringUtils.isEmpty(map.get(params2[2]))){
					// String sql = " itemkey='"+params2[2]+"'";
					// String className = getVoName(sql,WaItemVO.class);
					// map.put(params2[2], className);
					// }
					// }

					if (StringUtils.isEmpty(map.get(params[0]))) {
						String sql = " pk_wa_class='" + params[0] + "'";
						String className = getVoName(sql, WaClassVO.class);
						if (null != className) {
							map.put(params[0], className);
						}
					}

					if (StringUtils.isEmpty(map.get(params[1]))) {
						String sql = " itemkey='" + params[1] + "'";
						String className = getVoName(sql, WaItemVO.class);
						if (null != className) {
							map.put(params[1], className);
						}
					}

					for (int i = 0; i < params.length; i++) {
						formula = formula.replaceAll(
								params[i],
								null == map.get(params[i]) ? params[i] : map
										.get(params[i]));
					}

					// for(int i=0;i<params2.length;i++){
					// formula = formula.replaceFirst(params2[i],
					// null==map.get(params2[i])?params2[i]:map.get(params2[i]));
					// }
				}

			}

			// formula = formula.replaceAll("waOtherClassData",
			// ResHelper.getString("6013commonbasic","06013commonbasic0095"));

		}
		return formula;
	}

}
