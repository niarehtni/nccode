package nc.itf.ta;

import java.util.Map;

import nc.itf.ta.bill.IApproveBillQueryService;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

public interface ILeaveQueryService extends IApproveBillQueryService<LeaveRegVO, LeaveCommonVO> {

	/**
	 * 根据客户端的业务日期，查找加班转调休的转入年度（期间） 若加班转调休为按年结算，则返回长度为1的数组，元素即业务日期的所属考勤年度
	 * 若按期间结算，则返回长度为2的数组，第一个元素是业务日期所属的考勤年度，第二个元素是业务日期所属考勤期间
	 * 
	 * @param pk_org
	 * @param busiDate
	 * @return
	 * @throws BusinessException
	 */
	String[] queryOverToRestPeriod(String pk_org, UFDate busiDate) throws BusinessException;

	/**
	 * 根据人员、休假类别、年度(期间)查询有效的休假单据 用于假期计算的休假信息浏览
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param pk_leaveType
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,String pk_psnorg,String pk_leaveType,String year,String month,int leaveIndex)throws BusinessException;

	/**
	 * 根据人员、休假类别、年度(期间)查询有效的休假单据,上面方法的批量查询 用于假期计算的休假信息浏览
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param pk_leaveType
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	LeaveRegVO[] queryByPsnsLeaveTypePeriod(String pk_org,String[] pk_psnorgs,LeaveTypeCopyVO leaveTypeVO,String year,String month,int leaveIndex)throws BusinessException;

	/**
	 * 根据人员、休假类别、年度(期间)查询有效的休假单据 用于假期计算的休假信息浏览
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param pk_leaveType
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,String pk_psnorg,LeaveTypeCopyVO leaveTypeVO,String year,String month,int leaveIndex)throws BusinessException;

	/**
	 * 根据人员、休假类别、年度(期间)查询还未审批通过的休假申请单，包括自由态的，以及审批中的，不包括审批不通过的以及审批通过的
	 * 只有这样的申请单才会占用冻结时长
	 * 
	 * @param pk_org
	 * @param pk_psnorg
	 * @param leaveTypeVO
	 * @param year
	 * @param month
	 * @param leaveIndex
	 * @return
	 * @throws BusinessException
	 */
	LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(String pk_org,String pk_psnorg,String pk_leaveType,String year,String month,int leaveIndex)throws BusinessException;

	/**
	 * 根据人员、休假类别、年度(期间)查询还未审批通过的休假申请单，包括自由态的，以及审批中的，不包括审批不通过的以及审批通过的
	 * 只有这样的申请单才会占用冻结时长
	 * 
	 * @param pk_org
	 * @param pk_psnorg
	 * @param leaveTypeVO
	 * @param year
	 * @param month
	 * @param leaveIndex
	 * @return
	 * @throws BusinessException
	 */
	LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(String pk_org,String pk_psnorg,LeaveTypeCopyVO leaveTypeVO,String year,String month,int leaveIndex)throws BusinessException;

	/**
	 * 根据人员、休假类别、年度(期间)查询还未审批通过的休假申请单，包括自由态的，以及审批中的，不包括审批不通过的以及审批通过的
	 * 只有这样的申请单才会占用冻结时长
	 * 
	 * @param pk_org
	 * @param pk_psnorg
	 * @param leaveTypeVO
	 * @param year
	 * @param month
	 * @param leaveIndex
	 * @return
	 * @throws BusinessException
	 */
	LeavebVO[] queryBeforePassWithoutNoPassByPsnsLeaveTypePeriod(String pk_org,String[] pk_psnorgs,LeaveTypeCopyVO leaveTypeVO,String year,String month,int leaveIndex)throws BusinessException;

	/**
	 * 根据人员范围、日期范围查询有效哺乳假
	 * 
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsnDate(String pk_org,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;

	/**
	 * 根据人员主键inSQL、日期范围查询有效哺乳假
	 * 
	 * @param pk_org
	 * @param psndocInSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsndocInSQLDate(String pk_org,String psndocInSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;

	LeaveRegVO[] queryAllLactationVOIncEffictiveByPsnDate(String pk_org,String pk_psndoc,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;

	AggLeaveVO queryByPk(String key) throws BusinessException;

	/**
	 * 查询人员范围内,日期范围内的生效的单据，以登记表VO形式返回（）， Map的key是pk_psndoc，value是单据数组
	 * 
	 * @param pk_org
	 * @param psndocInSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, LeaveRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(String pk_org,String psndocInSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	

	/**
	 * 自助使用 查询考考勤期间的休假记录
	 * 
	 * @param pk_org
	 * @param pk_psndoc
	 * @param tbmYear
	 * @param tbmMonth
	 * @param pk_timeitem
	 * @return
	 * @throws BusinessException
	 */
	LeaveRegVO[] queryLeaveRegByPsnAndDateScope(String pk_org,String pk_psndoc,String tbmYear,String tbmMonth,String pk_timeitem)throws BusinessException;

	LeaveRegVO[] queryByPsnsLeaveTypePeriod(String pk_org, String[] pk_psnorgs,
			LeaveTypeCopyVO typeVO, String year, UFLiteralDate periodBeginDate,
			UFLiteralDate periodEndDate, Integer leaveindex) throws BusinessException;

	LeavebVO[] queryBeforePassWithoutNoPassByPsnsLeaveTypePeriod(String pk_org,
			String[] pk_psnorgs, LeaveTypeCopyVO typeVO, String year,
			UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate,
			Integer leaveindex) throws BusinessException;

	public abstract Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(String paramString1, String paramString2, UFLiteralDate paramUFLiteralDate1, UFLiteralDate paramUFLiteralDate2)
		    throws BusinessException;
}
