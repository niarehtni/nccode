package nc.itf.ta;

import java.util.Map;

import nc.itf.ta.algorithm.IDateScope;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.DateScopeCheckResult;
import nc.vo.ta.period.PeriodVO;

/**
 * �����ڼ� queryService�ӿ�
 * @author yucheng
 *
 */
public interface IPeriodQueryService {
	
	/**
	 * ����date�������ڼ䣬��date����������һ���ڼ䣬�򷵻ؿ�
	 * @param pk_org
	 * @param beginDate
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByDate(String pk_org,UFLiteralDate date)throws BusinessException;
	
	/**
	 * ����date�������ڼ䣬��date����������һ���ڼ䣬�����쳣
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByDateWithCheck(String pk_org,UFLiteralDate date)throws BusinessException;
	
	/**
	 * ��ѯHR��֯�ĵ���
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryCurPeriod(String pk_org)throws BusinessException;
	
	/**
	 * ����ָ������������п����ڼ�
	 * @param pk_org
	 * @param year
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO[] queryByYear(String pk_org, String year) throws BusinessException;
	
	/**
	 * ��ѯָ������������п����ڼ�
	 * @param pk_org
	 * @param years
	 * @return
	 * @throws BusinessException
	 */
	Map<String, PeriodVO[]> queryByYear(String pk_org, String[] years) throws BusinessException;
	
	/**
	 * ��ѯָ������������п����ڼ�
	 * @param pk_org
	 * @param years
	 * @return
	 * @throws BusinessException
	 */
	Map<String, PeriodVO[]> queryByYear(String pk_org, String beginYear,String endYear) throws BusinessException;
	         
	/**
	 * У�����ڶΣ�Ҫ�����ڶ�ȫ������δ�����ڼ��ڣ���������ڲ������κ��ڼ䣬���������ѷ����ڼ䣬�����쳣
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	void checkDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * �����޸�ʱУ�����ڵ��ڼ��Ƿ��棬�����Ѿ������ڼ������޸�
	 * �����������±��޸�ʱУ��ʹ��
	 * @param pk_org
	 * @param dates  -- ��������
	 * @throws BusinessException  
	 */
	void checkDateB4Modify(String pk_org, UFLiteralDate[] dates)throws BusinessException;
	
	/**
	 * У�����ڶΣ�Ҫ�����ڶ�ȫ������δ�����ڼ��ڣ���������ڲ������κ��ڼ䣬���������ѷ����ڼ䣬�򷵻�false
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	boolean checkDateScopeReturnsBoolean(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ������ checkDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)
	 * ֻ��������Ϊ����
	 * @param pk_org
	 * @param dateScopes
	 * @throws BusinessException
	 */
	void checkDateScope(String pk_org,IDateScope[] dateScopes) throws BusinessException;
	
	/**
	 * �������ڵ���ǰУ�顣������ڵ����Ŀ�ʼ���������п����ڼ�Ŀ�ʼ����֮ǰ�����߿��ڵ���������ڼ��н����������쳣
	 * ���ڵ�����У���ԭ���ǡ�Ҫ��֤��֯�ĵ�һ�������ڼ俪ʼ����֮ǰ�������ڿ��ڵ�������������Ҫ��֤���һ�������ڼ��
	 * ��������֮���޿��ڵ�������Ϊ�����ڼ��ǿ������չ�ģ����һ�����ڵ����Ľ��������������޿����ڼ�����ڣ���ô����
	 * ʱ������ţ�����һ���޿����ڼ�����ڻ����п����ڼ������
	 * @param tbmPsndocVO
	 * @throws BusinessException
	 */
	void checkBeforeInsertTBMPsndoc(SuperVO tbmPsndocVO) throws BusinessException;
	/**
	 * �������ڵ���ǰУ�顣������ڵ����Ŀ�ʼ�����޿����ڼ䣬���߿��ڵ���������ڼ��н����������쳣
	 * @param pk_org
	 * @param tbmPsndocVOs
	 * @throws BusinessException
	 */
	void checkBeforeInsertTBMPsndoc(String pk_org,SuperVO[] tbmPsndocVOs) throws BusinessException;
	
	/**
	 * �޸Ŀ��ڵ���ǰ����У�飬������ڵ����仯�������������޿����ڼ�ķ�Χ�ڣ������ѷ��Ŀ����ڼ�ķ�Χ�ڣ������쳣
	 * @param pk_org
	 * @param tbmPsndocVOs
	 * @throws BusinessException
	 */
	void checkBeforeUpdateTBMPsndoc(String pk_org,SuperVO[] tbmPsndocVOs) throws BusinessException;
	/**
	 * ɾ�����ڵ���ǰ����У�飬���ɾ���Ŀ��ڵ����������������޿����ڼ�ķ�Χ�ڣ������ѷ��Ŀ����ڼ�ķ�Χ�ڣ������쳣
	 * @param pk_org
	 * @param tbmPsndocVOs
	 * @throws BusinessException
	 */
	void checkBeforeDeleteTBMPsnodc(String pk_org,String[] pk_tbmpsndocs) throws BusinessException;
	
	/**
	 * �������²�ѯ�ڼ�
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByYearMonth(String pk_org,String year,String month)throws BusinessException;
	
	/**
	 * ���ݻ�׼�ڼ䣨��ƣ����²�ѯ�����ڼ�
	 * @param pk_org
	 * @param accYear
	 * @param accMonth
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByAccYearMonth(String pk_org,String accYear,String accMonth)throws BusinessException;
	
	
	/**
	 * �������²�ѯ����
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryNextPeriod(String pk_org,String year,String month)throws BusinessException;
	/**
	 * ��ѯdate�����ڼ������
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryNextPeriod(String pk_org,UFLiteralDate date)throws BusinessException;
	/**
	 * �������²�ѯ����
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryPreviousPeriod(String pk_org,String year,String month)throws BusinessException;
	/**
	 * ��ѯdate�����ڼ������
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryPreviousPeriod(String pk_org,UFLiteralDate date)throws BusinessException;
	
	/**
	 * ��ѯHR��֯�ĵ��ڵ�����
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryPreviousPeriod(String pk_org)throws BusinessException;

	/**
	 * ��ѯָ����֯�������趨�������ڼ�Ŀ������
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	String[] queryPeriodYearsByOrg(String pk_org) throws BusinessException;
	
	/**
	 * ��ѯָ����֯����ʼ���ڡ��������ڷ�Χ�ڵ����п����ڼ�
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO[] queryPeriodsByDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ��ѯָ����֯����ʼ���ڡ��������ڷ�Χ�ڵ����п����ڼ�,Ȼ����Щ�ڼ������Ϊkey�����Ӧ���ڼ���Ϊvalue����
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<UFLiteralDate, PeriodVO> queryPeriodMapByDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * ��ѯָ����֯�����ڷ�Χ�ڵ����п����ڼ�,Ȼ����Щ�ڼ������Ϊkey�����Ӧ���ڼ���Ϊvalue����
	 * ���ڷ�Χ��û�е��첻����map
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<UFLiteralDate, PeriodVO> queryPeriodMapByDateScopes(String pk_org,IDateScope[] dateScopes)throws BusinessException;
	
	/**
	 * ��ѯָ����֯�����п�����ȺͿ����ڼ�
	 * @param pk_org
	 * @return key: ������� value: ��Ӧ������ȵ����п����ڼ�
	 * @throws BusinessException
	 */
	Map<String, String[]> queryPeriodYearAndMonthByOrg(String pk_org) throws BusinessException;
	
	/**
	 * ���ĳ��ĳ���Ƿ��ǵ���
	 * ������ڲ����ڣ����߲��ǵ��ڣ������쳣
	 * ������쳣���򷵻ص��ڵ�vo
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO checkCurPeriod(String pk_org,String year,String month) throws BusinessException;
	
	/**
	 * ���ڲ�ѯԱ���ɼ����ݼ����
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	String[] queryPsnAllYears(String pk_psndoc)throws BusinessException;
	
	/**
	 * У��ʱ��εĿ�ʼ�ͽ���ʱ���Ƿ�δ�����ڼ�
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	DateScopeCheckResult checkDateScopeEnable(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	void checkBeforeInsertTBMPsndoc(PsndocVO[] psndocvos,SuperVO tbmPsndocVO)
			throws BusinessException ;
}
