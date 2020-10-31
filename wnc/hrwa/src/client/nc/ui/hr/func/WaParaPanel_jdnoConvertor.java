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
	// �����ڼ�н�ʶ�(x���Թ��ʻ�,�ۿ�ϼ�,���ڼ�)

	public static final String sdje = ResHelper.getString("6013salaryctymgt",
			"06013salaryctymgt0206")/* @res "ʱ�㷢�Ž��" */;
	public static final String yffje = ResHelper.getString("6013salaryctymgt",
			"06013salaryctymgt0207")/* @res "ԭ���Ž��" */;
	public static final String xffje = ResHelper.getString("6013salaryctymgt",
			"06013salaryctymgt0208")/* @res "�ַ��Ž��" */;

	private static Map<String, String> map = new HashMap<String, String>();
	// private static Map<String, String> classItemMap = new HashMap<String,
	// String>(); //н�ʷ�����Ŀ
	// private static Map<String, String> classItemMap = new HashMap<String,
	// String>(); //н�ʷ�����Ŀ
	static {
		String[] ml = { "sum", "avg", "max", "min" };
		String[] mlDefault = new String[] {
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0188")/*
																				 * @
																				 * res
																				 * "ȡ��"
																				 */,
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0189")/*
																				 * @
																				 * res
																				 * "ȡƽ��ֵ"
																				 */,
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0190")/*
																				 * @
																				 * res
																				 * "ȡ���ֵ"
																				 */,
				ResHelper
						.getString("6013salaryctymgt", "06013salaryctymgt0191") /*
																				 * @
																				 * res
																				 * "ȡ��Сֵ"
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

					// ����ʱ�㷢�Ž�ԭ���Ž��ַ��Ž��
					if (StringUtils.isEmpty(map.get(params2[0]))
							&& StringUtils.isEmpty(map.get(params[0]))) {
						String className = "";
						// ʱ�㷢�Ž��
						if (params2.length == 1 && params2[0].equals("0")) {
							className = sdje;
						} else if (params2.length > 1 && params2[1].equals("1")) {
							className = yffje;
						} else if (params2.length > 1 && params2[1].equals("2")) {
							className = xffje;
						}
						// ���⴦��ʱ�㷢�Ž�ԭ���Ž��ַ��Ž����ں���Ķ����ʵ�����Ŀ���������
						formula = formula.replaceFirst(params[0], "012");
						params[0] = "012";
						map.put("012", className);
					}
					// ���⴦��ʱ�㷢�Ž���"0"
					if (params2.length == 1 && params2[0].equals("0")) {
						String className = "";
						map.put(params[1], className);
					}
					// ����ԭ���Ž��ַ��Ž��
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
					// �滻ʱ�㷢�Ž������","
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
