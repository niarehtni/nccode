/**
 * @Description: TODO
 * @author yejk
 * @date 2018-9-4 
 */
package nc.impl.ta.formula.parser.func;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import nc.impl.hr.formula.parser.IFormulaParser;
import nc.vo.pub.BusinessException;

/**
 * #21266
 * @Description: 加班总时长函数解析器实现类
 * @author yejk
 * @date 2018-9-4
 */
@SuppressWarnings("serial")
public class OvertimeDayFuncParser implements IFormulaParser {

	/**
	 * @TODO:解析方法
	 * @author yejk
	 * @date 2018-9-4
	 * @param pk_org
	 * @param formula
	 * @param params
	 * @return formula
	 */
	@Override
	public String parse(String pk_org, String formula, Object... params) throws BusinessException {
		if (StringUtils.isEmpty(formula)) {
			return null;
		}	
		String tableName = "OVERTIMEDATA";
		String ovSumHour = "_OVERTIME_SUM_HOUR";
		Pattern p = Pattern.compile(tableName + "[.]" + ovSumHour
				+ "([0-9a-zA-Z]{20})");
		Matcher m = p.matcher(formula);
		
		while (m.find()) {//匹配串中的 pk_tiemitem (group)
			int gc = m.groupCount();
			for (int i = 1; i <= gc; i++) {
				String group = m.group(i);
				if (StringUtils.isEmpty(group)) {
					return formula;
				}
					
				String replaceStr = "isnull((select sum(tbm_daystat_cacu.hourcount) from tbm_daystat_cacu where tbm_daystat_cacu.pk_org = '" + pk_org + "' and tbm_daystat_cacu.pk_timeitem = '" + group + "' and tbm_daystat_cacu.pk_psndoc = tbm_daystat.pk_psndoc and tbm_daystat_cacu.calendar = tbm_daystat.calendar and tbm_daystat_cacu.calType = '0'),0)";
				
				formula = formula.replaceAll(tableName + "[.]" + ovSumHour
						+ group, replaceStr);
			}
		}
		return formula;
	}

}
