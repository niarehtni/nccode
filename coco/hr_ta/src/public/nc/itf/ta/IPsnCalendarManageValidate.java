package nc.itf.ta;

import java.util.List;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public interface IPsnCalendarManageValidate {

	/**
	 * 校验一例一休(默认排班/循环排班)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<String> validate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException;
	
	/**
	 * 校验一例一休(交换日历天)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<String> updateValidate(String pk_org, String[] pk_psndocs, UFLiteralDate firstDate,
			UFLiteralDate sencodeDate) throws BusinessException;
	/**
	 * 校验一例一休(修改日历天)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<String> updatedayValidate(String pk_org, String[] pk_psndocs, UFLiteralDate firstDate,
			Integer date_daytype) throws BusinessException;
	
	/**
	 * 班组校验一例一休(循环排班)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<String> teamvalidate(String pk_org, String[] pk_teams, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException;
	/**
	 * 班组校验一例一休(交换日历天)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<String> updateteamValidate(String pk_org, String[] pk_teams, UFLiteralDate firstDate,
			UFLiteralDate sencodeDate) throws BusinessException;
	/**
	 * 班组校验一例一休(批量修改)
	 * @throws BusinessException 
	 * 
	 * 
	 */
	List<String> updateteamValidate(String pk_org, String[] pk_teams,
			UFLiteralDate changeDate, Integer getafterChangeDateType);
}
