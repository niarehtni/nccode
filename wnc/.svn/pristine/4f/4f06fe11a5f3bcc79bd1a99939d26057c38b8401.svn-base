package nc.impl.ta.algorithm;

import nc.vo.pub.BusinessException;
import nc.vo.ta.psndoc.WorkWeekFormEnum;
 
/**
 * <b>台灣一例一休校驗工廠</b><br />
 * <br />
 * 一例一休：每七天有一天是例假日、一天是休息日。<br />
 * 變形工時：分為二週變形、四週變形及八週變形工時 <br />
 * <br/>
 * 
 * 二週變形：<br />
 * - 勞工每7天必須有一天例假<br />
 * - 每兩週內例假及休息日至少共4天 <br />
 * <br />
 * 四週變形：<br />
 * - 勞工每兩周內至少有2天例假<br />
 * - 每四週內例假及休息日至少共8天<br />
 * <br />
 * 八週變形：<br />
 * - 勞工每7天必須有1天例假<br />
 * - 每8週內例假及休息日至少共16天<br />
 * 
 * @author ssx
 * 
 * @since 2018-01-31
 * 
 * @version NC V6.5
 * 
 * @see <a href="https://laws.mol.gov.tw/FLAW/FLAWDAT0201.aspx?lsid=FL014930"
 *      >勞動基準法（中華民國勞動部）</a><br/>
 *      <a href=
 *      "https://zh.wikipedia.org/wiki/%E4%B8%80%E4%BE%8B%E4%B8%80%E4%BC%91"
 *      >一例一休詞條（維基百科）</a> <br />
 */
public class TWHolidayOffdayValidatorFactory {
	public static ITWHolidayOffdayValidate getValidator(WorkWeekFormEnum weekForm) throws BusinessException {
		ITWHolidayOffdayValidate validator = null;
		if (WorkWeekFormEnum.ONEWEEK.equals(weekForm)) {
			validator = new TWOneWeekValidator();
		} else if (WorkWeekFormEnum.TWOWEEKS.equals(weekForm)) {
			validator = new TWTwoWeekValidator();
		} else if (WorkWeekFormEnum.FOURWEEKS.equals(weekForm)) {
			validator = new TWFourWeekValidator();
		} else if (WorkWeekFormEnum.EIGHTWEEKS.equals(weekForm)) {
			validator = new TWEightWeekValidator();
		} else {
			throw new BusinessException("一例一休校驗參數錯誤");
		}

		return validator;
	}
}
