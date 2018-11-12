package nc.itf.ta;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;

public class OverTimeServiceFacade{
	/**
	 * TODO:此方法判断是否启用了加班管理
	 * @return
	 */
	private static boolean isOvertimeInstall(){
		return true;
	}
	
	private static IOvertimeQueryService getService(){
		return NCLocator.getInstance().lookup(IOvertimeQueryService.class);
	}

	
	public static UFLiteralDate queryBelongToDate(OvertimeCommonVO vo)
			throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryBelongToDate(vo);
	}

	
	public static Map<String, Map<UFLiteralDate, OvertimeRegVO[]>> queryOvertimeVOsByCondDate(
			String pk_org, String psnCond, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryOvertimeVOsByCondDate(pk_org, psnCond, beginDate, endDate);
	}

	
	public static Map<UFLiteralDate, OvertimeRegVO[]> queryOvertimeVOsByPsnDate(
			String pk_org, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryOvertimeVOsByPsnDate(pk_org, pk_psndoc, beginDate, endDate);
	}

	
	public static Map<String, OvertimeCommonVO[]> queryAllSuperVOExcNoPassByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByCondDate(pk_org, fromWhereSQL, beginDate, endDate);
	}

	
	public static OvertimeCommonVO[] queryAllSuperVOExcNoPassByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByPsn(pk_org, pk_psndoc);
	}

	
	public static OvertimeCommonVO[] queryAllSuperVOExcNoPassByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByPsnDate(pk_org, pk_psndoc, beginDate, endDate);
	}

	
	public static Map<String, OvertimeCommonVO[]> queryAllSuperVOExcNoPassByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
	}

	
	public static Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByCondDate(pk_org, fromWhereSQL, beginDate, endDate);
	}

	
	public static OvertimeRegVO[] queryAllSuperVOIncEffectiveByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsn(pk_org, pk_psndoc);
	}

	
	public static OvertimeRegVO[] queryAllSuperVOIncEffectiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc, beginDate, endDate);
	}

	
	public static Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
	}

	public static Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(pk_org, psndocInSQL, beginDate, endDate);
	}
	
	
	public static Map<String, OvertimeRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOByApproveDateByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
	}
	
	public static Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocsDate(
			String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsndocsDate(pk_org, pk_psndocs, beginDate, endDate);
	}

	
	public static OvertimeCommonVO[] queryIntersectionBills(OvertimeCommonVO bill)
			throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryIntersectionBills(bill);
	}

	
	public static Map<String, OvertimeCommonVO[]> queryIntersectionBillsMap(
			OvertimeCommonVO[] bills) throws BusinessException {
		if(!isOvertimeInstall())
			return null;
		return getService().queryIntersectionBillsMap(bills);
	}

	/**
	 * 处理加班人员的所属业务单元，因为假日加班规则中使用的是业务单元不是hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	public static OvertimeCommonVO[] processJobOrg(OvertimeCommonVO[] vos)throws BusinessException{
		if(!isOvertimeInstall())
			return null;
		return getService().processJobOrg(vos);
	}
}
