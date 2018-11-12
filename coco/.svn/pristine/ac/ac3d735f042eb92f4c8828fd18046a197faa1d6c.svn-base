package nc.impl.ta.algorithm;

import nc.vo.pub.BusinessException;

/**
 * * 四週變形：<br />
 * - 勞工每兩周內至少有2天例假<br />
 * - 每四週內例假及休息日至少共8天<br />
 * <br />
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
public class TWFourWeekValidator extends AbstractTWHolidayOffdayValidator {
    @Override
    public int getCheckedWeeks() throws BusinessException {
	return 4;
    }

    @Override
    public int getMinHolidayCheckWeeks() throws BusinessException {
	return 2;
    }

    @Override
    public int getHolidayCountInMinCheckWeeks() throws BusinessException {
	return 2;
    }

    @Override
    public int getTotalHolidaysAndOffdaysCount() throws BusinessException {
	return 8;
    }

    @Override
    public int getMaxDailyWorkHoursInMinWeeks() throws BusinessException {
	return 10;
    }

    @Override
    public int getTotalMaxWorkHoursInMinWeeks() throws BusinessException {
	return -1;
    }

    @Override
    public int getTotalMaxWorkHoursInMaxWeeks() throws BusinessException {
	return 160;
    }

}
