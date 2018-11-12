package nc.impl.ta.algorithm;

import java.util.List;
import java.util.Map;

import nc.vo.bd.psn.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;

/**
 * * ���L׃�Σ�<br />
 * - �ڹ�ÿ7������1������<br />
 * - ÿ8�L�����ټ���Ϣ�����ٹ�16��<br />
 * 
 * @author ssx
 * 
 * @since 2018-01-31
 * 
 * @version NC V6.5
 * 
 * @see <a href="https://laws.mol.gov.tw/FLAW/FLAWDAT0201.aspx?lsid=FL014930"
 *      >�ڄӻ��ʷ������A����ڄӲ���</a><br/>
 *      <a href=
 *      "https://zh.wikipedia.org/wiki/%E4%B8%80%E4%BE%8B%E4%B8%80%E4%BC%91"
 *      >һ��һ���~�l���S���ٿƣ�</a> <br />
 */
public class TWEightWeekValidator extends AbstractTWHolidayOffdayValidator {
    @Override
    public int getCheckedWeeks() throws BusinessException {
	return 8;
    }

    @Override
    public int getMinHolidayCheckWeeks() throws BusinessException {
	return 1;
    }

    @Override
    public int getHolidayCountInMinCheckWeeks() throws BusinessException {
	return 1;
    }

    @Override
    public int getTotalHolidaysAndOffdaysCount() throws BusinessException {
	return 16;
    }

    @Override
    public int getMaxDailyWorkHoursInMinWeeks() throws BusinessException {
	return 8;
    }

    @Override
    public int getTotalMaxWorkHoursInMinWeeks() throws BusinessException {
	return 48;
    }

    @Override
    public int getTotalMaxWorkHoursInMaxWeeks() throws BusinessException {
	return 320;
    }


}
