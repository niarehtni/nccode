package nc.impl.ta.algorithm;

import java.util.List;
import java.util.Map;

import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;

public interface ITWHolidayOffdayValidate {
	/**
	 * §µ??
	 * @param volists 
	 * @return 
	 * 
	 * @throws BusinessException
	 */
	public Map<Map<String, String>, String> validate(String pk_org, PsndocVO pk_psndoc, UFLiteralDate checkDate, List<AggPsnCalendar> volists) throws BusinessException;

	
	
	
}
