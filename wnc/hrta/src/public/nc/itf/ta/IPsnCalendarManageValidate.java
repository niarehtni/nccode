package nc.itf.ta;

import java.util.List;
import java.util.Map;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public interface IPsnCalendarManageValidate {

	/**
	 * У��һ��һ��(Ĭ���Ű�)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<List<String>> validate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException;
	/**
	 * У��һ��һ��(ѭ���Ű�)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<List<String>> curvalidate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String[] pk_shifts) throws BusinessException;
	
	/**
	 * У��һ��һ��(����������)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<List<String>> updateValidate(String pk_org, String[] pk_psndocs, UFLiteralDate firstDate,
			UFLiteralDate sencodeDate) throws BusinessException;
	/**
	 * У��һ��һ��(�޸�������)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<List<String>> updatedayValidate(String pk_org, String[] pk_psndocs, UFLiteralDate changeDate,
			Integer date_daytype) throws BusinessException;
	
	/**
	 * ����У��һ��һ��(ѭ���Ű�)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<List<String>> teamvalidate(String pk_org, String[] pk_teams, UFLiteralDate beginDate,
			UFLiteralDate endDate, String[] pk_shifts) throws BusinessException;
	/**
	 * ����У��һ��һ��(����������)
	 * @throws BusinessException 
	 * 
	 * 
	 */

	List<List<String>> updateteamValidate(String pk_org, String[] pk_teams, UFLiteralDate firstDate,
			UFLiteralDate sencodeDate) throws BusinessException;
	/**
	 * ����У��һ��һ��(�����޸�)
	 * @throws BusinessException 
	 * 
	 * 
	 */
	List<List<String>> updateteamValidate(String pk_org, String[] pk_teams,
			UFLiteralDate changeDate, Integer getafterChangeDateType)throws BusinessException;
}
