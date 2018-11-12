/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package nc.itf.ta;
import java.util.Map;

import nc.vo.bd.holiday.HolidayInfo;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.holiday.HRHolidayVO;

/**
 * HR自己的假日接口
 * @author zengcheng
 *
 */
public interface IHRHolidayQueryService {

	/**
	 * 查询HR组织内，人员在日期范围内的每一天的工作日情况--假日、工作日还是非工作日
	 * 此信息与排班无关，只与业务单元的假日有关
	 * 
	 * key是pk_psndoc,value的key是date，value是工作日类型，取值见HolidayVO
	 * 
	 * 此方法用于工作日历节点的查询--显示HR组织的考勤档案内的每个人员每天的工作日类型
	 * 因为考勤档案内的人员的任职可能属于不同的业务单元，而不同的业务单元使用不同的假日，因此此方法的逻辑异常复杂
	 * @param pk_org,HR组织
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return <pk_psndoc,<date,工作日类型>>，map内返回所有天
	 * @throws BusinessException
	 */
	Map<String, Map<String, Integer>> queryPsnWorkDayTypeInfo(String pk_hrorg,String[] pk_psndocs,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 查询HR组织内，人员在日期范围内的每一天的工作日情况--假日、工作日还是非工作日
	 * 此信息与排班无关，只与业务单元的假日有关
	 * 
	 * key是pk_psndoc,value的key是date，value是工作日类型，取值见HolidayVO
	 * 
	 * 此方法用于工作日历节点的查询--显示HR组织的考勤档案内的每个人员每天的工作日类型
	 * 因为考勤档案内的人员的任职可能属于不同的业务单元，而不同的业务单元使用不同的假日，因此此方法的逻辑异常复杂
	 * @param pk_org,HR组织
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param onlyAbnormalDayInMap，true，返回的map里面，只包含异常天（即假日和对调日）；否则包含所有天
	 * @return <pk_psndoc,<date,工作日类型>>
	 * @throws BusinessException
	 */
	Map<String, Map<String, Integer>> queryPsnWorkDayTypeInfo(String pk_hrorg,String[] pk_psndocs,
			UFLiteralDate beginDate,UFLiteralDate endDate,boolean onlyAbnormalDayInMap)throws BusinessException;
	
	/**
	 * 业务单元日期范围内的每一天的工作日情况
	 * 此信息与排班无关，与人员无关
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return <日期, 工作日类型>
	 * @throws BusinessException
	 * @description 此方法只会返回组织默认的日历天,不会返回具体到班组的方法,如果各个班组修改了自己的日历天类型,
	 * 				那么这个方法的返回值是不准确的,此方法只可用于班组排班的时候查询默认的排班
	 * 
	 */
	Map<String, Integer> queryTeamWorkDayTypeInfo(String pk_org, UFLiteralDate beginDate, UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 批量优化方法
	 * 业务单元日期范围内的每一天的工作日情况 此信息与排班无关，与人员无关
	 * @param pk_orgs
	 * @param beginDate
	 * @param endDate
	 * @return <业务单元，<日期，工作日类型>>
	 * @throws BusinessException
	 * @description 此方法只会返回组织默认的日历天,不会返回具体到班组的方法,如果各个班组修改了自己的日历天类型,
	 * 				那么这个方法的返回值是不准确的,此方法只可用于班组排班的时候查询默认的排班
	 * @see 
	 */
	Map<String, Map<String, Integer>> queryTeamWorkDayTypeInfos(String[] pkOrgs, UFLiteralDate beginDate, UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 批量优化方法
	 * 查询每一个班组的日历天类型
	 * @param pk_orgs
	 * @param beginDate
	 * @param endDate
	 * @param pk_teams 班组pk合集
	 * @return <班组pk，<日期，工作日类型>>
	 * @author Ares.Tank 2018-9-7 12:21:46
	 * @throws BusinessException
	 * @description 
	 * @see 
	 */
	Map<String, Map<String, Integer>> queryTeamWorkDayTypeInfos4View(String[] pkTeams, String[] pkOrgs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException;
	
	/**
	 * 查询日期范围内的假日，用于循环排班的遇假日取消的情况。
	 * 满足下面两个条件之一的假日都作为结果返回：
	 * 1.假日时间段与排班时间段有交集的。由于排班时间有可能跨自然日，因此查询时，可以将begindate往前推一天到enddate往后推一天的日期范围内有交集的假日都返回
	 * 2.假日定义中，oneswitch-eightswitch，oneswitchto-eightswitchto这16个日期只要有一个出现在日期范围内，也返回
	 * @param pk_org,业务单元，因为循环排班、默认排班、批量调班在V6.1都改为基于业务单元的操作
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	HRHolidayVO[] queryHolidayVOs(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 查询HR组织日期范围内的假日。实质上是查询HR组织下各个业务单元的假日，然后包装成map，key是业务单元的pk_org
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, HRHolidayVO[]> queryHolidayVOsByHROrg(String pk_hrorg,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 对queryHolidayVOsByHROrg方法的包装，key是业务单元的pk_org
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, HolidayInfo<HRHolidayVO>> queryHolidayInfoByHROrg(String pk_hrorg,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 查询日期范围内的假日信息。此方法只是对queryHolidayVOs的一个封装，详见HolidayInfo的定义
	 * @param pk_org,业务单元
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	HolidayInfo<HRHolidayVO> queryHolidayInfo(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 查询人员享受假期的情况，返回的map中，key是假日的定义表主键，value的key是人员主键，value是是否享有标志
	 * 享有某个假日的充分必要条件是：满足假日的人群范围条件（例如全员享有），且业务单元内的考勤档案与假日的最大日期范围（假日+对调日）有交集
	 * 如果一个假日的最大日期范围(假日加对调)与员工的业务单元的考勤档案无交集，则此员工不享有此假日，不管此假日是否是全员假日
	 * 有交集，则根据享有范围，可能享有此假日
	 * 有一种情况：考勤档案只是与假日的最大日期范围有交集，不是完全包含，这种情况下也属于享有此假日（假如此人满足享有条件）
	 * 例如，某业务单元某年的国庆七天假如下：
	 * -------------------------------------------------------------------------------
	 * |9.29周六|9.30周日|10.1周一|10.2周二|10.3周三|10.4周四|10.5周五|10.6周六|10.7周日 |
	 * |------------------------------------------------------------------------------|
	 * |10.4对调|10.5对调|  国庆     |  国庆      |  国庆     |9.29对调|9.30对调|        |        |
	 * |------------------------------------------------------------------------------|
	 * 即9.29，9.30周六日两天上班，10.1-10.7七天放假
	 * 假如一个员工在此业务单元内的考勤档案9.30结束，那么，虽然他的考勤档案不完全包含此假日的日期范围(9.29-10.5)，但我们认为此人还是享有此假日，享有
	 * 的结果就是9.29，9.30两天照常上班。如果10.1开始，此人的考勤档案属于另外一个业务单元，且(另外一个业务单元未定义国庆节，或者此人在另外一个业务单元
	 * 不享有国庆节假期)，则10.1-10.5还得上班（虽然他享有前一个组织的国庆节）
	 * 在这种情况下，可能会对考勤计算产生干扰。例如，此方法表明此人享有此国庆节假日，假如此人在10.1之后属于另外一个业务单元，且不享有假日，则10.1-10.3
	 * 这三天多余假日可能会对计算产生干扰。在代码中，要想办法去除这种干扰。
	 * @param pk_org,业务单元
	 * @param pk_psndocs
	 * @param holidayVOs
	 * @return <pk_holiday,<pk_psndoc,是否享有>>
	 * 注意，如果一个人在假日中途换业务单元，则在两个业务单元中此人可能都享有这个假日
	 * @throws BusinessException
	 */
	Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo(String pk_org,String[] pk_psndocs,HolidayVO[] holidayVOs)throws BusinessException;
	
	/**
	 * 在HR组织中，查询人员享受假期情况，返回<人员，<pk_holiday+业务单元主键,是否享有>>
	 * @param pk_hrorg
	 * @param pk_psndocs
	 * @param holidayMap，<业务单元主键,假日>
	 * @return <人员，<pk_holiday+业务单元主键,是否享有>>
	 * @throws BusinessException
	 */
	Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo2(String pk_hrorg,String[] pk_psndocs,Map<String,HRHolidayVO[]> holidayMap)throws BusinessException;
	
	/**
	 * 在HR组织中，查询人员享受假期情况，返回<pk_holiday+业务单元主键，<人员,是否享有>>
	 * @param pk_hrorg
	 * @param pk_psndocs
	 * @param holidayMap，<业务单元主键,假日>
	 * @return
	 * @throws BusinessException
	 */
	Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo(String pk_hrorg,String[] pk_psndocs,Map<String,HRHolidayVO[]> holidayMap)throws BusinessException;
	
	/**
	 * 查询人员享受假期的情况，返回的map中，key是人员主键，value的key是假日的定义表主键，value是是否享有标志
	 *  享有某个假日的充分必要条件是：满足假日的人群范围条件（例如全员享有），且业务单元内的考勤档案与假日的最大日期范围（假日+对调日）有交集
	 * @param pk_org,业务单元
	 * @param pk_psndocs
	 * @param holidayVOs
	 * @return
	 * @throws BusinessException
	 */
	Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo2(String pk_org,String[] pk_psndocs,HolidayVO[] holidayVOs)throws BusinessException;
	
	
	/**
	 * 根据假日主键返回在组织内享有假日的人员的pk_psndoc数组
	 * @param pk_org,HR组织（HR组织还是业务单元还需斟酌）
	 * @param pk_holiday
	 * @return
	 * @throws BusinessException
	 */
	String[] queryEnjoyPsndocs(String pk_org,String pk_holiday)throws BusinessException;
	
	/**
	 * 根据假日定义的内容返回在组织内享有假日的人员的pk_psndoc数组
	 * @param pk_org,HR组织（HR组织还是业务单元还需斟酌）
	 * @param holidayVO
	 * @return
	 * @throws BusinessException
	 */
	String[] queryEnjoyPsndocs(String pk_org,HolidayVO holidayVO)throws BusinessException;
	
	/**
	 * 查询某个人员在一段日期范围内的工作日类型。此查询是穿透不同的业务单元的。
	 * 返回的key是日期，Integer是工作日类型
	 * @param pk_psndoc
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String,Integer> queryPsnWorkDayTypeInfo(String pk_psndoc,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;

	/**
	 * 查询人员的工作日类型,此方法只在员工工作日历排班时使用,排班是不会涉及原有的工作日类型的影响,只会根据默认规则来生成日历天的类型
	 * 查询HR组织内，人员在日期范围内的每一天的工作日情况--假日、工作日还是非工作日
	 * 此信息与排班无关，只与业务单元的假日有关
	 * 
	 * key是pk_psndoc,value的key是date，value是工作日类型，取值见HolidayVO
	 * 
	 * 此方法用于工作日历节点的查询--显示HR组织的考勤档案内的每个人员每天的工作日类型
	 * 因为考勤档案内的人员的任职可能属于不同的业务单元，而不同的业务单元使用不同的假日，因此此方法的逻辑异常复杂
	 * @param pk_org,HR组织
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param onlyAbnormalDayInMap，true，返回的map里面，只包含异常天（即假日和对调日）；否则包含所有天
	 * @return <pk_psndoc,<date,工作日类型>>
	 * @throws BusinessException
	 * @author Ares.Tank 2018-9-6 15:49:43
	 */
	Map<String, Map<String, Integer>> queryPsnWorkDayTypeInfo4PsnCalenderInsert(String pk_hrorg,String[] pk_psndocs,
			UFLiteralDate beginDate,UFLiteralDate endDate,boolean onlyAbnormalDayInMap)throws BusinessException;
	
	
	}
