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

public interface ILeaveQueryService extends IApproveBillQueryService<LeaveRegVO,LeaveCommonVO> {
	
	/**
	 * ���ݿͻ��˵�ҵ�����ڣ����ҼӰ�ת���ݵ�ת����ȣ��ڼ䣩
	 * ���Ӱ�ת����Ϊ������㣬�򷵻س���Ϊ1�����飬Ԫ�ؼ�ҵ�����ڵ������������
	 * �����ڼ���㣬�򷵻س���Ϊ2�����飬��һ��Ԫ����ҵ�����������Ŀ�����ȣ��ڶ���Ԫ����ҵ���������������ڼ�
	 * @param pk_org
	 * @param busiDate
	 * @return
	 * @throws BusinessException
	 */
	String[] queryOverToRestPeriod(String pk_org,UFDate busiDate)throws BusinessException;
	
	/**
	 * ������Ա���ݼ�������(�ڼ�)��ѯ��Ч���ݼٵ���
	 * ���ڼ��ڼ�����ݼ���Ϣ���
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
	 * ������Ա���ݼ�������(�ڼ�)��ѯ��Ч���ݼٵ���,���淽����������ѯ
	 * ���ڼ��ڼ�����ݼ���Ϣ���
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
	 * ������Ա���ݼ�������(�ڼ�)��ѯ��Ч���ݼٵ���
	 * ���ڼ��ڼ�����ݼ���Ϣ���
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
	 * ������Ա���ݼ�������(�ڼ�)��ѯ��δ����ͨ�����ݼ����뵥����������̬�ģ��Լ������еģ�������������ͨ�����Լ�����ͨ����
	 * ֻ�����������뵥�Ż�ռ�ö���ʱ��
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
	 * ������Ա���ݼ�������(�ڼ�)��ѯ��δ����ͨ�����ݼ����뵥����������̬�ģ��Լ������еģ�������������ͨ�����Լ�����ͨ����
	 * ֻ�����������뵥�Ż�ռ�ö���ʱ��
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
	 * ������Ա���ݼ�������(�ڼ�)��ѯ��δ����ͨ�����ݼ����뵥����������̬�ģ��Լ������еģ�������������ͨ�����Լ�����ͨ����
	 * ֻ�����������뵥�Ż�ռ�ö���ʱ��
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
	 * ������Ա��Χ�����ڷ�Χ��ѯ��Ч�����
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsnDate(String pk_org,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ������Ա����inSQL�����ڷ�Χ��ѯ��Ч�����
	 * @param pk_org
	 * @param psndocInSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsndocInSQLDate(String pk_org,String psndocInSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	LeaveRegVO[] queryAllLactationVOIncEffictiveByPsnDate(String pk_org,String pk_psndoc,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	AggLeaveVO queryByPk(String key)throws BusinessException;
	
	
	/**
	 * ��ѯ��Ա��Χ��,���ڷ�Χ�ڵ���Ч�ĵ��ݣ��ԵǼǱ�VO��ʽ���أ�����
	 * Map��key��pk_psndoc��value�ǵ�������
	 * @param pk_org
	 * @param psndocInSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, LeaveRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(String pk_org,String psndocInSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	
	/**
	 * ����ʹ��   ��ѯ�������ڼ���ݼټ�¼
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
			Integer leaveindex)throws BusinessException;
	
	public abstract Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(String paramString1, String paramString2, UFLiteralDate paramUFLiteralDate1, UFLiteralDate paramUFLiteralDate2)
		    throws BusinessException;
}
