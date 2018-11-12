package nc.itf.ta;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

public class LeaveServiceFacade{
	
	/**
	 * TODO:此方法判断是否启用了休假管理
	 * @return
	 */
	private static boolean isLeaveInstall(){
		return true;
	}
	
	private static ILeaveQueryService getService(){
		return NCLocator.getInstance().lookup(ILeaveQueryService.class);
	}

	
	public static Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsnDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllLactationVOIncEffictiveByPsnDate(pk_org, fromWhereSQL, beginDate, endDate);
	}

	
	public static LeaveRegVO[] queryAllLactationVOIncEffictiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllLactationVOIncEffictiveByPsnDate(pk_org, pk_psndoc, beginDate, endDate);
	}

	
	public static Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllLactationVOIncEffictiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
	}

	
	public static LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,
			String pk_psnorg, String pk_leaveType, String year, String month,
			int leaveIndex) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryByPsnLeaveTypePeriod(pk_org, pk_psnorg, pk_leaveType, year, month, leaveIndex);
	}

	
	public static LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,
			String pk_psnorg, LeaveTypeCopyVO leaveTypeVO, String year,
			String month, int leaveIndex) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryByPsnLeaveTypePeriod(pk_org, pk_psnorg, leaveTypeVO, year, month, leaveIndex);
	}
	
	public static LeaveRegVO[] queryByPsnsLeaveTypePeriod(String pk_org,
			String[] pk_psnorgs, LeaveTypeCopyVO leaveTypeVO, String year,
			String month, int leaveIndex) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryByPsnsLeaveTypePeriod(pk_org, pk_psnorgs, leaveTypeVO, year, month, leaveIndex);
	}
	
	public static LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(String pk_org,String pk_psnorg,String pk_leaveType,String year,String month,int leaveIndex)throws BusinessException{
		if(!isLeaveInstall())
			return null;
		return getService().queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(pk_org, pk_psnorg, pk_leaveType, year, month, leaveIndex);
	}

	public static LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(String pk_org,String pk_psnorg,LeaveTypeCopyVO leaveTypeVO,String year,String month,int leaveIndex)throws BusinessException{
		if(!isLeaveInstall())
			return null;
		return getService().queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(pk_org, pk_psnorg, leaveTypeVO, year, month, leaveIndex);
	}
	
	public static LeavebVO[] queryBeforePassWithoutNoPassByPsnsLeaveTypePeriod(String pk_org,String[] pk_psnorgs,LeaveTypeCopyVO leaveTypeVO,String year,String month,int leaveIndex)throws BusinessException{
		if(!isLeaveInstall())
			return null;
		return getService().queryBeforePassWithoutNoPassByPsnsLeaveTypePeriod(pk_org, pk_psnorgs, leaveTypeVO, year, month, leaveIndex);
	}
	
	public static String[] queryOverToRestPeriod(String pk_org, UFDate busiDate)
			throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryOverToRestPeriod(pk_org, busiDate);
	}

	
	public static Map<String, LeaveCommonVO[]> queryAllSuperVOExcNoPassByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByCondDate(pk_org, fromWhereSQL, beginDate, endDate);
	}

	
	public static LeaveCommonVO[] queryAllSuperVOExcNoPassByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByPsn(pk_org, pk_psndoc);
	}

	
	public static LeaveCommonVO[] queryAllSuperVOExcNoPassByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByPsnDate(pk_org, pk_psndoc, beginDate, endDate);
	}

	
	public static Map<String, LeaveCommonVO[]> queryAllSuperVOExcNoPassByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOExcNoPassByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
	}

	
	public static Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByCondDate(pk_org, fromWhereSQL, beginDate, endDate);
	}

	
	public static LeaveRegVO[] queryAllSuperVOIncEffectiveByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsn(pk_org, pk_psndoc);
	}

	
	public static LeaveRegVO[] queryAllSuperVOIncEffectiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsnDate(pk_org, pk_psndoc, beginDate, endDate);
	}

	
	public static Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
	}
	
	//MOD James
	public static Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(pk_org, psndocInSQL, beginDate, endDate);
	}

	public static Map<String, LeaveRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOByApproveDateByPsndocInSQLDate(pk_org, psndocInSQL, beginDate, endDate);
	}
	
	public static Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocsDate(
			String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryAllSuperVOIncEffectiveByPsndocsDate(pk_org, pk_psndocs, beginDate, endDate);
	}
	
	public static LeaveCommonVO[] queryIntersectionBills(LeaveCommonVO bill)
			throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryIntersectionBills(bill);
	}

	
	public static Map<String, LeaveCommonVO[]> queryIntersectionBillsMap(
			LeaveCommonVO[] bills) throws BusinessException {
		if(!isLeaveInstall())
			return null;
		return getService().queryIntersectionBillsMap(bills);
	}
}
