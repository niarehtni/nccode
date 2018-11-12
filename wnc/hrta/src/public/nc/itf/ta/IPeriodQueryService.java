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
 * 考勤期间 queryService接口
 * @author yucheng
 *
 */
public interface IPeriodQueryService {
	
	/**
	 * 查找date所属的期间，若date不属于任意一个期间，则返回空
	 * @param pk_org
	 * @param beginDate
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByDate(String pk_org,UFLiteralDate date)throws BusinessException;
	
	/**
	 * 查找date所属的期间，若date不属于任意一个期间，则抛异常
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByDateWithCheck(String pk_org,UFLiteralDate date)throws BusinessException;
	
	/**
	 * 查询HR组织的当期
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryCurPeriod(String pk_org)throws BusinessException;
	
	/**
	 * 查找指定考勤年的所有考勤期间
	 * @param pk_org
	 * @param year
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO[] queryByYear(String pk_org, String year) throws BusinessException;
	
	/**
	 * 查询指定考勤年的所有考勤期间
	 * @param pk_org
	 * @param years
	 * @return
	 * @throws BusinessException
	 */
	Map<String, PeriodVO[]> queryByYear(String pk_org, String[] years) throws BusinessException;
	
	/**
	 * 查询指定考勤年的所有考勤期间
	 * @param pk_org
	 * @param years
	 * @return
	 * @throws BusinessException
	 */
	Map<String, PeriodVO[]> queryByYear(String pk_org, String beginYear,String endYear) throws BusinessException;
	         
	/**
	 * 校验日期段，要求日期段全部落在未封存的期间内，如果有日期不属于任何期间，或者属于已封存的期间，则抛异常
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	void checkDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 数据修改时校验所在的期间是否封存，若是已经封存的期间则不能修改
	 * 如日历、日月报修改时校验使用
	 * @param pk_org
	 * @param dates  -- 日期数组
	 * @throws BusinessException  
	 */
	void checkDateB4Modify(String pk_org, UFLiteralDate[] dates)throws BusinessException;
	
	/**
	 * 校验日期段，要求日期段全部落在未封存的期间内，如果有日期不属于任何期间，或者属于已封存的期间，则返回false
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	boolean checkDateScopeReturnsBoolean(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 类似于 checkDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)
	 * 只不过参数为数组
	 * @param pk_org
	 * @param dateScopes
	 * @throws BusinessException
	 */
	void checkDateScope(String pk_org,IDateScope[] dateScopes) throws BusinessException;
	
	/**
	 * 新增考勤档案前校验。如果考勤档案的开始日期在所有考勤期间的开始日期之前，或者考勤档案与封存的期间有交集，则抛异常
	 * 考勤档案的校验的原则是“要保证组织的第一个考勤期间开始日期之前，不存在考勤档案”，而不需要保证最后一个考勤期间的
	 * 结束日期之后无考勤档案，因为考勤期间是可以向后发展的，如果一个考勤档案的结束日期落在了无考勤期间的日期，那么随着
	 * 时间的流逝，总有一天无考勤期间的日期会变成有考勤期间的日期
	 * @param tbmPsndocVO
	 * @throws BusinessException
	 */
	void checkBeforeInsertTBMPsndoc(SuperVO tbmPsndocVO) throws BusinessException;
	/**
	 * 新增考勤档案前校验。如果考勤档案的开始日期无考勤期间，或者考勤档案与封存的期间有交集，则抛异常
	 * @param pk_org
	 * @param tbmPsndocVOs
	 * @throws BusinessException
	 */
	void checkBeforeInsertTBMPsndoc(String pk_org,SuperVO[] tbmPsndocVOs) throws BusinessException;
	
	/**
	 * 修改考勤档案前进行校验，如果考勤档案变化的日期落在了无考勤期间的范围内，或者已封存的考勤期间的范围内，则抛异常
	 * @param pk_org
	 * @param tbmPsndocVOs
	 * @throws BusinessException
	 */
	void checkBeforeUpdateTBMPsndoc(String pk_org,SuperVO[] tbmPsndocVOs) throws BusinessException;
	/**
	 * 删除考勤档案前进行校验，如果删除的考勤档案的日期落在了无考勤期间的范围内，或者已封存的考勤期间的范围内，则抛异常
	 * @param pk_org
	 * @param tbmPsndocVOs
	 * @throws BusinessException
	 */
	void checkBeforeDeleteTBMPsnodc(String pk_org,String[] pk_tbmpsndocs) throws BusinessException;
	
	/**
	 * 根据年月查询期间
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByYearMonth(String pk_org,String year,String month)throws BusinessException;
	
	/**
	 * 根据基准期间（会计）年月查询考勤期间
	 * @param pk_org
	 * @param accYear
	 * @param accMonth
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryByAccYearMonth(String pk_org,String accYear,String accMonth)throws BusinessException;
	
	
	/**
	 * 根据年月查询下期
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryNextPeriod(String pk_org,String year,String month)throws BusinessException;
	/**
	 * 查询date所在期间的下期
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryNextPeriod(String pk_org,UFLiteralDate date)throws BusinessException;
	/**
	 * 根据年月查询上期
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryPreviousPeriod(String pk_org,String year,String month)throws BusinessException;
	/**
	 * 查询date所在期间的上期
	 * @param pk_org
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryPreviousPeriod(String pk_org,UFLiteralDate date)throws BusinessException;
	
	/**
	 * 查询HR组织的当期的上期
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO queryPreviousPeriod(String pk_org)throws BusinessException;

	/**
	 * 查询指定组织的所有设定过考勤期间的考勤年度
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	String[] queryPeriodYearsByOrg(String pk_org) throws BusinessException;
	
	/**
	 * 查询指定组织、开始日期、结束日期范围内的所有考勤期间
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO[] queryPeriodsByDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 查询指定组织、开始日期、结束日期范围内的所有考勤期间,然后将这些期间的天作为key，天对应的期间作为value返回
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<UFLiteralDate, PeriodVO> queryPeriodMapByDateScope(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 查询指定组织、日期范围内的所有考勤期间,然后将这些期间的天作为key，天对应的期间作为value返回
	 * 日期范围内没有的天不放入map
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<UFLiteralDate, PeriodVO> queryPeriodMapByDateScopes(String pk_org,IDateScope[] dateScopes)throws BusinessException;
	
	/**
	 * 查询指定组织的所有考勤年度和考勤期间
	 * @param pk_org
	 * @return key: 考勤年度 value: 对应考勤年度的所有考勤期间
	 * @throws BusinessException
	 */
	Map<String, String[]> queryPeriodYearAndMonthByOrg(String pk_org) throws BusinessException;
	
	/**
	 * 检查某年某月是否是当期
	 * 如果当期不存在，或者不是当期，则抛异常
	 * 如果无异常，则返回当期的vo
	 * @param pk_org
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	PeriodVO checkCurPeriod(String pk_org,String year,String month) throws BusinessException;
	
	/**
	 * 用于查询员工可见的休假年度
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	String[] queryPsnAllYears(String pk_psndoc)throws BusinessException;
	
	/**
	 * 校验时间段的开始和结束时间是否未封存的期间
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
