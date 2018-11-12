package nc.impl.ta.holiday;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.StringPiecer;
import nc.itf.bd.holiday.IHolidayQueryMaintain;
import nc.itf.bd.holiday.IHolidayQueryService;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.bd.timezone.TimezoneUtil;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.IHRHolidayManageService;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.pubitf.uapbd.IWorkCalendarPubService;
import nc.vo.bd.holiday.HolidayInfo;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;

public class HRHolidayServiceImpl implements IHRHolidayManageService, IHRHolidayQueryService {

	// Ares.Tank 2018-9-3 16:12:47 无奈之下写个饿汉单例,不是我水平不行,我是怕代码抽查说我没初始化
	private BaseDAO baseDAO = new BaseDAO();

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	@Override
	public void clearEnjoyDetail() throws BusinessException {
		BaseDAO dao = new BaseDAO();
		// dao.executeUpdate("truncate table tbm_holidayenjoyb");
		// dao.executeUpdate("truncate table tbm_holidayenjoyh");
		// 不用truncate的理由是，truncate调用后，其他事务的数据也没了
		dao.executeUpdate("delete from tbm_holidayenjoyb");
		dao.executeUpdate("delete from tbm_holidayenjoyh where pk_user='" + PubEnv.getPk_user() + "'");

	}

	@Override
	public void createEnjoyDetail(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		if (beginDate == null && endDate == null) {
			UFLiteralDate today = new UFLiteralDate();
			beginDate = today.getDateBefore(365);
			endDate = today.getDateAfter(365);
		} else if (beginDate == null && endDate != null) {
			beginDate = endDate.getDateBefore(730);
		} else if (beginDate != null && endDate == null) {
			endDate = beginDate.getDateAfter(730);
		}
		createEnjoyDetail0(pk_hrorg, pk_psndocs, beginDate, endDate);
	}

	/**
	 * 当一个HR组织下的不同业务单元之间的假日分类不一样的时候,橱窗里人员的工作日类型
	 * 按每人每年有30天异常日(即假日+对调日)计算,一人一年有30条数据,按极限情况一次计算5000人两年，需要30万条数据
	 *
	 * @param pk_hrorg
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param insertbSQL
	 * @throws BusinessException
	 */
	private void createEnjoyDetailForMultiHolidayType(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String insertbSQL) throws BusinessException {
		// 首先查询所有人在所有天的工作日类型。考虑到人数和天数太多，会导致消耗的内存太大，因此需要做一下控制，一次最多计算1000人，100天
		JdbcSession session = null;
		try {
			session = new JdbcSession();
			SequenceGenerator sg = new SequenceGenerator();
			int psnCountOnce = 1000;
			int dayCountOnce = 100;
			for (int i = 0; true; i++) {
				if (i * psnCountOnce > pk_psndocs.length)
					break;
				int beginIndex = i * psnCountOnce;
				int endIndex = Math.min((i + 1) * psnCountOnce - 1, pk_psndocs.length - 1);
				String[] pk_psndocs0 = new String[endIndex - beginIndex + 1];
				System.arraycopy(pk_psndocs, beginIndex, pk_psndocs0, 0, pk_psndocs0.length);
				UFLiteralDate tempBeginDate = beginDate;
				while (true) {
					int dayCount = UFLiteralDate.getDaysBetween(tempBeginDate, endDate) + 1;
					if (dayCount > dayCountOnce) {// 如果超过了100天，则只计算100天
						createEnjoyDetailForMultiHolidayType0(pk_hrorg, pk_psndocs0, tempBeginDate,
								tempBeginDate.getDateAfter(dayCountOnce - 1), sg, session, insertbSQL);
						tempBeginDate = tempBeginDate.getDateAfter(dayCountOnce);
						continue;
					}
					// 如果没有超过100天，则计算所有的天
					createEnjoyDetailForMultiHolidayType0(pk_hrorg, pk_psndocs0, tempBeginDate, endDate, sg, session,
							insertbSQL);
					break;
				}
			}
			session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			session.closeAll();
		}
	}

	private void createEnjoyDetailForMultiHolidayType0(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, SequenceGenerator sg, JdbcSession session, String insertbSQL)
			throws BusinessException, DbException {
		// 这个方法里面，pk_psndocs不会超过1000，begindate和enddate之间不会超过100天，因此能有效地控制map的大小，防止内存溢出
		Map<String, Map<String, Integer>> psnWorkdayTypeMap = queryPsnWorkDayTypeInfo(pk_hrorg, pk_psndocs, beginDate,
				endDate, true);
		int count = CommonUtils.getValueCount(psnWorkdayTypeMap);
		if (count == 0)
			return;
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		String[] pks = sg.generate(count);
		int i = 0;
		for (String pk_psndoc : psnWorkdayTypeMap.keySet()) {
			Map<String, Integer> workDayTypeMap = psnWorkdayTypeMap.get(pk_psndoc);
			if (MapUtils.isEmpty(workDayTypeMap))
				continue;
			for (String date : workDayTypeMap.keySet()) {
				Integer workdaytype = workDayTypeMap.get(date);
				SQLParameter para = new SQLParameter();
				para.addParam(pks[i]);
				i++;
				para.addParam(pk_group);
				para.addParam(pk_hrorg);
				para.addNullParam(Types.CHAR);// 本方法里面提供不了假日主键
				para.addParam(date);
				para.addParam(workdaytype);
				para.addParam(pk_psndoc);
				session.addBatch(insertbSQL, para);
			}
		}
	}

	/**
	 * 如果一个hrorg下所有的业务单元都使用同一套假日，则用此方法处理tbm_holidayenjoyh和tbm_holidayenjoyb记录
	 *
	 * @param pk_hrorg
	 * @param holidayVOs
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param inserthSQL
	 * @param insertbSQL
	 * @throws BusinessException
	 */
	private void createEnjoyDetailForSingleHolidayType(String pk_hrorg, OrgVO[] orgVOs, HolidayVO[] holidayVOs,
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, String inserthSQL, String insertbSQL)
			throws BusinessException {
		JdbcSession session = null;
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		try {
			session = new JdbcSession();
			SequenceGenerator sg = new SequenceGenerator();
			HolidayDAO dao = new HolidayDAO();
			Set<String> holidaySet = new HashSet<String>();// 存储假日的set，元素是日期。如果一天是假日，那么肯定不可能是工作日和非工作日
			// 按假日循环处理
			String pk_user = PubEnv.getPk_user();
			for (HolidayVO holidayVO : holidayVOs) {
				String pk_holiday = holidayVO.getPk_holiday();
				String holBeginDate = holidayVO.getStarttime().substring(0, 10);// 假日开始日期
				String holEndDate = holidayVO.getEndtime().substring(0, 10);// 假日结束日期
				UFLiteralDate[] allHolidays = CommonUtils.createDateArray(holBeginDate, holEndDate);
				String isAllEnjoy = UFBoolean.valueOf(holidayVO.isAllEnjoy()).toString();// 是否全员享有
				// 如果不是全员享有，则需要查询出享有人员(按业务单元依次查询)
				String[] enjoyPsndocs = null;
				if (!holidayVO.isAllEnjoy()) {
					Set<String> enjoySet = new HashSet<String>();
					for (OrgVO orgVO : orgVOs) {// 按业务单元循环
						Set<String> tempEnyjoySet = dao.queryEnjoyHolidayPsn(orgVO.getPk_org(), pk_psndocs, holidayVO);
						if (CollectionUtils.isNotEmpty(tempEnyjoySet))
							enjoySet.addAll(tempEnyjoySet);
					}
					if (enjoySet.size() > 0)
						enjoyPsndocs = enjoySet.toArray(new String[0]);
				}
				// 将此假日的每一天假日都插入主表
				for (UFLiteralDate holiday : allHolidays) {// 先将假日插入主表
					if (holidaySet.contains(holiday.toString()))
						continue;
					SQLParameter para = new SQLParameter();
					para.addParam(sg.generate());
					para.addParam(pk_group);
					para.addParam(pk_hrorg);
					para.addParam(pk_holiday);
					para.addParam(holiday.toString());
					para.addParam(2);
					para.addParam(isAllEnjoy);
					para.addParam(pk_user);
					session.addBatch(inserthSQL, para);
					holidaySet.add(holiday.toString());
					// 如果不是全员享有，则需要将享有人员插入子表
					if (!ArrayUtils.isEmpty(enjoyPsndocs)) {
						String[] pks = sg.generate(enjoyPsndocs.length);
						for (int i = 0; i < enjoyPsndocs.length; i++) {
							para = new SQLParameter();
							para.addParam(pks[i]);
							para.addParam(pk_group);
							para.addParam(pk_hrorg);
							para.addParam(pk_holiday);
							para.addParam(holiday.toString());
							para.addParam(2);
							para.addParam(enjoyPsndocs[i]);
							session.addBatch(insertbSQL, para);
						}
					}
				}
				// 然后将工作日和非工作日插入主表
				List<UFLiteralDate> switchDateList = holidayVO.getAllSwitchDate();
				if (!org.springframework.util.CollectionUtils.isEmpty(switchDateList)) {
					for (UFLiteralDate switchDate : switchDateList) {
						if (holidaySet.contains(switchDate.toString()))
							continue;
						int weekDay = switchDate.getWeek();
						int type = (weekDay == 0 || weekDay == 6) ? 0 : 1;// 如果此天为周六周日，则肯定是工作日，否则是非工作日（因为一个日期如果对调了，且不是假日的话，那么肯定会从工作日变为非工作日，或者由非工作日变为工作日）
						SQLParameter para = new SQLParameter();
						para.addParam(sg.generate());
						para.addParam(pk_group);
						para.addParam(pk_hrorg);
						para.addParam(pk_holiday);
						para.addParam(switchDate.toString());
						para.addParam(type);
						para.addParam(isAllEnjoy);
						para.addParam(pk_user);
						session.addBatch(inserthSQL, para);
						holidaySet.add(switchDate.toString());
						// 如果不是全员享有，则需要将享有人员插入子表
						if (!ArrayUtils.isEmpty(enjoyPsndocs)) {
							String[] pks = sg.generate(enjoyPsndocs.length);
							for (int i = 0; i < enjoyPsndocs.length; i++) {
								para = new SQLParameter();
								para.addParam(pks[i]);
								para.addParam(pk_group);
								para.addParam(pk_hrorg);
								para.addParam(pk_holiday);
								para.addParam(switchDate.toString());
								para.addParam(type);
								para.addParam(enjoyPsndocs[i]);
								session.addBatch(insertbSQL, para);
							}
						}
					}
				}
			}
			session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			session.closeAll();
		}
	}

	/**
	 * 分两种情况讨论
	 * 如果hrorg下所有的业务单元都使用同一套假日分类（这是绝大多数情况），则采用简单模式：pk_holidayenjoyh中存储所有假日，
	 * 以及对调过的工作日和非工作日，tbm_holidayenjoyb中存储非全员享有时，享有此假日（对调的工作日非工作日）的人员
	 * 如果hrorg下的多个业务单元使用的假日还不一样，则采用复杂模式：不存pk_holidayenjoyh，只存tbm_holidayenjoyb，
	 * 逻辑是：如果一个人某一天的工作日 类型与默认不符（默认是周一到周五是工作日，周六日是非工作日），则存储此人此天的记录
	 *
	 * @param pk_hrorg
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	private void createEnjoyDetail0(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		clearEnjoyDetail();
		// 首先查询HR组织下所有的业务单元，看是否使用同一套假日
		NCLocator.getInstance().lookup(IAOSQueryService.class);
		// OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false,
		// false);
		OrgVO[] orgVOs = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		IWorkCalendarPubService workcalendarService = NCLocator.getInstance().lookup(IWorkCalendarPubService.class);
		String holidayType = null;
		String inserthSQL = "insert into tbm_holidayenjoyh(pk_holidayenjoyh," + IBaseServiceConst.PK_GROUP + ","
				+ IBaseServiceConst.PK_ORG + ",pk_holiday,calendar,calendar_type,isallenjoy,pk_user) "
				+ "values(?,?,?,?,?,?,?,?)";
		String insertbSQL = "insert into tbm_holidayenjoyb(pk_holidayenjoyb," + IBaseServiceConst.PK_GROUP + ","
				+ IBaseServiceConst.PK_ORG + ",pk_holiday,calendar,calendar_type,pk_psndoc) " + "values(?,?,?,?,?,?,?)";
		Map<String, String> HolidayRules = workcalendarService
				.queryCalendarHolidayRule(StringPiecer.getStrArray(orgVOs, OrgVO.PK_ORG));
		for (OrgVO orgVO : orgVOs) {
			// 优化查询放循环外
			// String curHolidayType =
			// workcalendarService.getCalendarHolidayRule(orgVO.getPk_org());
			String curHolidayType = HolidayRules == null ? null : HolidayRules.get(orgVO.getPk_org());
			if (curHolidayType == null)
				curHolidayType = HolidayVO.HOLIDAYSORT_CHINAMAINLAND;
			if (holidayType == null) {
				holidayType = curHolidayType;
				continue;
			}
			if (!holidayType.equals(curHolidayType)) {// 如果两个假日类别不相等,说明要使用第二种方法
				createEnjoyDetailForMultiHolidayType(pk_hrorg, pk_psndocs, beginDate, endDate, insertbSQL);
				return;
			}
		}
		// 走到这里,说明首先查询出范围内的所有假日
		HolidayVO[] holidayVOs = NCLocator.getInstance().lookup(IHolidayQueryService.class).queryHolidayVOs(holidayType,
				beginDate, endDate);
		if (ArrayUtils.isEmpty(holidayVOs))
			return;
		createEnjoyDetailForSingleHolidayType(pk_hrorg, orgVOs, holidayVOs, pk_psndocs, beginDate, endDate, inserthSQL,
				insertbSQL);

	}

	@Override
	public String[] queryEnjoyPsndocs(String pk_org, String pk_holiday) throws BusinessException {
		return queryEnjoyPsndocs(pk_org, (HolidayVO) new BaseDAO().retrieveByPK(HolidayVO.class, pk_holiday));
	}

	@Override
	public String[] queryEnjoyPsndocs(String pk_org, HolidayVO holidayVO) throws BusinessException {
		Set<String> enjoySet = new HolidayDAO().queryEnjoyHolidayPsn(pk_org, (String[]) null, holidayVO);
		if (CollectionUtils.isEmpty(enjoySet))
			return null;
		return enjoySet.toArray(new String[0]);
	}

	@Override
	public Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo(String pk_org, String[] pk_psndocs,
			HolidayVO[] holidayVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(holidayVOs))
			return null;
		UFLiteralDate[] earliestLatestDates = HolidayVO.getEarliestLatestDate(holidayVOs);
		UFLiteralDate firstDate = earliestLatestDates[0];// 假日信息的最早天
		UFLiteralDate latestDate = earliestLatestDates[1];// 假日信息的最晚天
		// 下面的查询，会查询出假日范围内有考勤档案的人员。有个问题可能会发生，那就是pk_psndocs里面有此人，但此人的考勤档案范围刚好和假日的范围无交集，此时，此人就会被漏掉
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, firstDate, latestDate, true);
		return queryHolidayEnjoyInfo(pk_org, pk_psndocs, psndocMap, holidayVOs);
	}

	/**
	 * 查询假日享有信息
	 *
	 * @param pk_org
	 * @param pk_psndocs
	 * @param psndocMap
	 * @param holidayVOs
	 * @return <pk_holiday, <pk_psndoc, isEnjoy>>
	 * @throws BusinessException
	 */
	private Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo(String pk_org, String[] pk_psndocs,
			Map<String, List<TBMPsndocVO>> psndocMap, HolidayVO[] holidayVOs) throws BusinessException {
		// 如果假日范围内无考勤档案, 返回空
		if (ArrayUtils.isEmpty(holidayVOs) || MapUtils.isEmpty(psndocMap))
			return null;
		HolidayDAO dao = new HolidayDAO();
		if (ArrayUtils.isEmpty(pk_psndocs))// 如果pk_psndocs为空，表示用户希望查询组织内所有人员的享有情况
			pk_psndocs = psndocMap.keySet().toArray(new String[0]);
		Map<String, Map<String, Boolean>> returnMap = new HashMap<String, Map<String, Boolean>>();
		for (HolidayVO holidayVO : holidayVOs) {
			Set<String> enjoyPsndocs = dao.queryEnjoyHolidayPsn(pk_org, psndocMap, holidayVO);
			Map<String, Boolean> enjoyMap = new HashMap<String, Boolean>();
			returnMap.put(holidayVO.getPk_holiday(), enjoyMap);
			for (String pk_psndoc : pk_psndocs) {
				Boolean isEnjoy = enjoyPsndocs != null && enjoyPsndocs.contains(pk_psndoc);
				enjoyMap.put(pk_psndoc, isEnjoy);
			}
		}
		return returnMap;
		// 变量：这些假日是否所有人都享有，有一个假不是所有人享有，此变量就为false
		// boolean allPersonHoliday = true;
		// for(HolidayVO holidayVO:holidayVOs){
		// if(!holidayVO.isAllEnjoy()){
		// allPersonHoliday = false;
		// break;
		// }
		// }
		// //如果psndocMap为空，表示传入的所有人，在假日范围内都没有考勤档案，此时这些人都不享有这些假
		// if(MapUtils.isEmpty(psndocMap)){
		// if(ArrayUtils.isEmpty(pk_psndocs)){
		// return null;
		// }
		// Map<String, Boolean> enjoyMap = new HashMap<String, Boolean>();
		// for(String pk_psndoc:pk_psndocs){
		// enjoyMap.put(pk_psndoc, Boolean.FALSE);
		// }
		// for(HolidayVO holidayVO:holidayVOs){
		// returnMap.put(holidayVO.getPk_holiday(), enjoyMap);
		// }
		// return returnMap;
		// }
		// //如果pk_psndocs为空，表示用户希望查询组织内所有人员的享有情况
		// if(ArrayUtils.isEmpty(pk_psndocs)){
		// pk_psndocs = psndocMap.keySet().toArray(new String[0]);
		// }
		//
		// ////////////////////////////////////////////////////////如果假日都是全员享有，则很简单，直接判断假日的最大范围是否和考勤档案有交集，如果有则享有，否则不享有/////////////
		// if(allPersonHoliday){
		// for(HolidayVO holidayVO:holidayVOs){
		// //<pk_psndoc,是否享有>
		// Map<String, Boolean> enjoyMap = new HashMap<String, Boolean>();
		// UFLiteralDate[] thisEarliestLatestDates =
		// holidayVO.getEarliestLatestDate();
		// IDateScope holidayScope = new
		// DefaultDateScope(thisEarliestLatestDates[0],thisEarliestLatestDates[1]);
		// for(String pk_psndoc:pk_psndocs){
		// if(!psndocMap.containsKey(pk_psndoc)){
		// enjoyMap.put(pk_psndoc, Boolean.FALSE);
		// continue;
		// }
		// List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
		// enjoyMap.put(pk_psndoc, DateScopeUtils.isCross(psndocList,
		// holidayScope));
		// }
		// returnMap.put(holidayVO.getPk_holiday(), enjoyMap);
		// }
		// return returnMap;
		// }
		// ///////////////////////////////////////////////////////如果不是每个假期都是全员享有，则比较复杂，对于不是所有人都享有的假日，需要根据享有条件来判断一个人是否应该享有//////
		// for(HolidayVO holidayVO:holidayVOs){
		// //<pk_psndoc,是否享有>
		// Map<String, Boolean> enjoyMap = new HashMap<String, Boolean>();
		// UFLiteralDate[] thisEarliestLatestDates =
		// holidayVO.getEarliestLatestDate();
		// //如果当前这个假所有人都享有,则比较简单
		// if(holidayVO.isAllEnjoy()){
		// IDateScope holidayScope = new
		// DefaultDateScope(thisEarliestLatestDates[0],thisEarliestLatestDates[1]);
		// for(String pk_psndoc:pk_psndocs){
		// if(!psndocMap.containsKey(pk_psndoc)){
		// enjoyMap.put(pk_psndoc, Boolean.FALSE);
		// continue;
		// }
		// List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
		// enjoyMap.put(pk_psndoc, DateScopeUtils.isCross(psndocList,
		// holidayScope));
		// }
		// returnMap.put(holidayVO.getPk_holiday(), enjoyMap);
		// continue;
		// }
		// //如果当前这个假不是每个人都享有，则需要根据享有条件来判断
		// boolean[] enjoyResult = queryHolidayEnjoyInfo(pk_org, pk_psndocs,
		// psndocMap, holidayVO);
		// for(int i=0;i<pk_psndocs.length;i++){
		// enjoyMap.put(pk_psndocs[i], enjoyResult[i]);
		// }
		// returnMap.put(holidayVO.getPk_holiday(), enjoyMap);
		// }
		// return returnMap;
	}

	@Override
	public Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo2(String pk_org, String[] pk_psndocs,
			HolidayVO[] holidayVOs) throws BusinessException {
		return CommonUtils.transferMap(queryHolidayEnjoyInfo(pk_org, pk_psndocs, holidayVOs));
	}

	@Override
	public HolidayInfo<HRHolidayVO> queryHolidayInfo(String pk_org, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		// 查询业务单元在日期范围内的假日，然后分析其假日/对调信息，包装成HolidayInfo
		return createHolidayInfo(pk_org, queryHolidayVOs(pk_org, beginDate, endDate));
	}

	/**
	 * 优化方法，批量方法
	 *
	 * @param pk_orgs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, HolidayInfo<HRHolidayVO>> queryHolidayInfos(String[] pk_orgs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		Map<String, HRHolidayVO[]> HolidayVOs = queryHolidayVOsByOrgs(pk_orgs);
		Map<String, HolidayInfo<HRHolidayVO>> retMap = new HashMap<String, HolidayInfo<HRHolidayVO>>();
		if (MapUtils.isEmpty(HolidayVOs))
			return retMap;
		for (String pk_org : pk_orgs) {
			// 查询业务单元在日期范围内的假日，然后分析其假日/对调信息，包装成HolidayInfo
			retMap.put(pk_org, createHolidayInfo(pk_org, HolidayVOs.get(pk_org)));
		}
		return retMap;
	}

	@Override
	public HRHolidayVO[] queryHolidayVOs(String pk_org, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		OrgVO orgVO = (OrgVO) new BaseDAO().retrieveByPK(OrgVO.class, pk_org);
		// 从业务单元上取假日分类的主键
		String pk_holidaysort = NCLocator.getInstance().lookup(IWorkCalendarPubService.class)
				.getCalendarHolidayRule(orgVO.getPk_org());
		if (pk_holidaysort == null)
			pk_holidaysort = HolidayVO.HOLIDAYSORT_CHINAMAINLAND;
		// 查询指定假日分类(在业务单元中使用的假日分类)并且在指定的日期范围内的假日,然后要set其pk_org以及时区
		IHolidayQueryService service = NCLocator.getInstance().lookup(IHolidayQueryService.class);
		// HolidayVO[] holidayVOs =
		// service.queryBySort(pk_holidaysort);//之前没有限制日期范围
		HolidayVO[] holidayVOs = service.queryHolidayVOs(pk_holidaysort, beginDate, endDate);// 增加对日期范围的限制
		if (ArrayUtils.isEmpty(holidayVOs))
			return null;

		// 性能优化，减少查询数据库次数
		// TimeZone timeZone =
		// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		TimeZone zone = null;
		if (StringUtils.isEmpty(orgVO.getPk_timezone())) {
			zone = ICalendar.BASE_TIMEZONE;
		} else {
			zone = TimezoneUtil.getTimeZone(orgVO.getPk_timezone());
		}

		HRHolidayVO[] retVOs = new HRHolidayVO[holidayVOs.length];
		for (int i = 0; i < retVOs.length; i++) {
			HRHolidayVO hrHolidayVO = new HRHolidayVO(holidayVOs[i]);
			hrHolidayVO.setPk_bu(pk_org);
			hrHolidayVO.setTimeZone(zone);
			retVOs[i] = hrHolidayVO;
		}
		return retVOs;
	}

	/**
	 * 性能优化添加方法，批量处理
	 *
	 * @param pk_orgs
	 * @return<组织，假日>
	 * @throws BusinessException
	 */
	private Map<String, HRHolidayVO[]> queryHolidayVOsByOrgs(String[] pk_orgs) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_orgs))
			return null;
		InSQLCreator isc = new InSQLCreator();
		try {
			String orgCondition = OrgVO.PK_ORG + " in ( " + isc.getInSQL(pk_orgs) + ") ";
			OrgVO[] orgVOs = (OrgVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null,
					OrgVO.class, orgCondition);
			// 从业务单元上取假日分类的主键
			Map<String, String> holidaySortsMap = NCLocator.getInstance().lookup(IWorkCalendarPubService.class)
					.queryCalendarHolidayRule(pk_orgs);
			// 处理假日类别,若业务单元没有查询到假日类别则默认为中国大陆节假日
			for (String pk_org : pk_orgs) {
				if (holidaySortsMap.get(pk_org) == null)
					holidaySortsMap.put(pk_org, HolidayVO.HOLIDAYSORT_CHINAMAINLAND);
			}
			String[] holidaysorts = holidaySortsMap.values().toArray(new String[0]);
			String holidayCondition = HolidayVO.PK_HOLIDAYSORT + " in ( " + isc.getInSQL(holidaysorts) + ") ";
			HolidayVO[] holidayVOs = NCLocator.getInstance().lookup(IHolidayQueryMaintain.class)
					.queryByCondition(holidayCondition);
			if (ArrayUtils.isEmpty(holidayVOs))
				return null;
			// Map<String, HolidayVO[]> holidayMap = new HashMap<String,
			// HolidayVO[]>() ;
			// for(HolidayVO holidayvo:holidayVOs){
			// if(holidayMap.get(holidayvo.getPk_holidaysort())==null){
			// HolidayVO[] vos = new HolidayVO[]{holidayvo};
			// holidayMap.put(holidayvo.getPk_holidaysort(), vos);
			// }else{
			// holidayMap.put(holidayvo.getPk_holidaysort(), (HolidayVO[])
			// ArrayUtils.add(holidayMap.get(holidayvo.getPk_holidaysort()),
			// holidayvo));
			//
			// }
			// }
			Map<String, HolidayVO[]> holidayMap = CommonUtils.group2ArrayByField(HolidayVO.PK_HOLIDAYSORT, holidayVOs);
			Map<String, HRHolidayVO[]> retMap = new HashMap<String, HRHolidayVO[]>();
			for (String pk_org : pk_orgs) {
				String pk_holidaySort = holidaySortsMap.get(pk_org);
				if (StringUtils.isBlank(pk_holidaySort))
					continue;
				HolidayVO[] hvos = holidayMap.get(pk_holidaySort);
				if (ArrayUtils.isEmpty(hvos))
					continue;
				HRHolidayVO[] hrholidayvos = new HRHolidayVO[hvos.length];
				for (int i = 0; i < hrholidayvos.length; i++) {
					HRHolidayVO hrHolidayVO = new HRHolidayVO(hvos[i]);
					hrHolidayVO.setPk_bu(pk_org);
					hrHolidayVO.setTimeZone(getTimeZone(pk_org, orgVOs));
					hrholidayvos[i] = hrHolidayVO;
				}
				retMap.put(pk_org, hrholidayvos);
			}
			return retMap;
		} finally {
			isc.clear();
		}
	}

	/**
	 * 获取组织时区
	 *
	 * @param pk_org
	 * @param orgVOs
	 * @return
	 * @throws BusinessException
	 */
	private TimeZone getTimeZone(String pk_org, OrgVO[] orgVOs) throws BusinessException {
		if (StringUtils.isBlank(pk_org) || ArrayUtils.isEmpty(orgVOs))
			return null;
		for (OrgVO orgVO : orgVOs) {
			if (!pk_org.equals(orgVO.getPk_org()))
				continue;
			return StringUtils.isEmpty(orgVO.getPk_timezone()) ? ICalendar.BASE_TIMEZONE
					: TimezoneUtil.getTimeZone(orgVO.getPk_timezone());
		}
		return null;
	}

	/**
	 * 查询人员的工作日类型
	 *
	 * @param pk_hrorg
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param onlyAbnormalDayInMap,true,则map里面只会出现有异常的天(即假日和对调日),否则出现所有的天
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Map<String, Integer>> queryPsnWorkDayTypeInfo(String pk_hrorg, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean onlyAbnormalDayInMap) throws BusinessException {
		UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
		// HR组织下所有业务单元的假日
		Map<String, HolidayInfo<HRHolidayVO>> holidayInfoMap = queryHolidayInfoByHROrg(pk_hrorg, beginDate, endDate);
		String[] pk_orgs = holidayInfoMap.keySet().toArray(new String[0]);// HR组织下所有业务单元
		Map<String, Map<String, Integer>> returnMap = new HashMap<String, Map<String, Integer>>();
		// HR组织下有至少一个业务单元，采用分业务单元依次处理的策略：按业务单元循环，依次判断这批人员在业务单元内的假日享有情况，然后再将map合并
		// 每个组织的人员享有情况，<pk_org,<pk_psndoc,<日期,工作日类型>>>
		Map<String, Map<String, Map<String, Integer>>> orgPsnEnjoyMap = new HashMap<String, Map<String, Map<String, Integer>>>();
		for (String pk_org : pk_orgs) {
			orgPsnEnjoyMap.put(pk_org, queryPsnWorkDayTypeInfo4SingleOrg(pk_org, pk_psndocs, dates,
					holidayInfoMap.get(pk_org), onlyAbnormalDayInMap));
		}
		// 将map的结构转换一下，改为以人员主键为外层key，存储人员在不同组织内的工作日类型
		Map<String, Map<String, Map<String, Integer>>> psnOrgEnjoyMap = CommonUtils.transferMap(orgPsnEnjoyMap);
		// 所有人员在日期范围内的考勤档案，key是人员主键
		Map<String, List<TBMPsndocVO>> tbmpsndocVOMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate, true, true, null);
		if (MapUtils.isEmpty(tbmpsndocVOMap))
			return null;
		// 按人员依次处理其在多个任职组织内的假日享有情况
		for (String pk_psndoc : pk_psndocs) {
			// 此人员在多个业务单元的工作日类型，key是业务单元,内层key是日期
			Map<String, Map<String, Integer>> orgEnjoyMap = psnOrgEnjoyMap == null
					? new HashMap<String, Map<String, Integer>>() : psnOrgEnjoyMap.get(pk_psndoc);
			// 此人员在日期范围内在pk_hrorg内的所有考勤档案记录，记录上set了任职业务单元主键字段
			List<TBMPsndocVO> psndocVOList = tbmpsndocVOMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(psndocVOList))
				continue;
			// 将考勤档案按任职所属业务单元分组
			Map<String, TBMPsndocVO[]> orgGroupPsndocVOMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_JOBORG,
					psndocVOList.toArray(new TBMPsndocVO[0]));
			// 按正常的业务逻辑，考勤档案的任职所属业务单元应该不会超过行政体系中HR组织下业务单元的范围（因为加考勤档案时做了限制，只能加HR组织下所属业务单元的任职）
			// 但是，由于可以在人事模块直接修改任职记录的组织，因此，有可能造成有些考勤档案的任职所属组织超出了HR组织下业务单元的范围
			// 如果在日期范围内，人员只在同一个组织任职（这是占绝大多数的情况），则直接使用此人员在此组织内的工作日类型即可
			if (orgGroupPsndocVOMap.size() == 1) {
				String pk_org = orgGroupPsndocVOMap.keySet().toArray(new String[0])[0];
				if (orgEnjoyMap.keySet().contains(pk_org)) {
					returnMap.put(pk_psndoc, orgEnjoyMap.get(pk_org));
					continue;
				}
				// 如果此人员的任职组织不在HR组织的管辖范围内，则比较麻烦，因为上面的工作日类型处理逻辑都只是考虑了HR组织范围内的业务
				// 单元，因此此处要单独处理此人在此任职组织的工作日类型
				returnMap.put(pk_psndoc, queryPsnWorkDayTypeInOrg(pk_psndoc, pk_org,
						queryHolidayInfo(pk_org, holidayInfoMap, beginDate, endDate), dates, onlyAbnormalDayInMap));
				continue;
			}
			// 如果人员任职在多个组织，则按组织循环处理
			String[] psnpk_orgs = orgGroupPsndocVOMap.keySet().toArray(new String[0]);
			Map<String, Integer> psnWorkDayTypeMap = new HashMap<String, Integer>();// 此人的工作日类型map，用来作为返回值
			returnMap.put(pk_psndoc, psnWorkDayTypeMap);
			for (String psnpk_org : psnpk_orgs) {// 按组织循环
				// 人员在此组织内的工作日类型
				Map<String, Integer> psnWorkDayTypeInOrgMap = orgEnjoyMap.containsKey(psnpk_org)
						? orgEnjoyMap.get(psnpk_org)
						: queryPsnWorkDayTypeInOrg(pk_psndoc, psnpk_org,
								queryHolidayInfo(psnpk_org, holidayInfoMap, beginDate, endDate), dates,
								onlyAbnormalDayInMap);
				// 此人在此业务单元的考勤档案
				TBMPsndocVO[] psndocVOsInOrg = orgGroupPsndocVOMap.get(psnpk_org);
				// 将此业务单元内的考勤档案的有效天的工作日类型放到返回值的map中
				for (TBMPsndocVO psndovVO : psndocVOsInOrg) {
					UFLiteralDate[] validPsndocDates = CommonUtils.createDateArray(psndovVO.getBegindate(),
							psndovVO.getEnddate());
					for (UFLiteralDate validDate : validPsndocDates) {
						if (!onlyAbnormalDayInMap) {// 如果返回的map存储所有天，则考勤档案有效的每一天都要put进去
							psnWorkDayTypeMap.put(validDate.toString(),
									psnWorkDayTypeInOrgMap.get(validDate.toString()));
							continue;
						}
						// 否则，返回的map只put进入异常天（假日，对调日）
						if (psnWorkDayTypeInOrgMap.containsKey(validDate.toString()))
							psnWorkDayTypeMap.put(validDate.toString(),
									psnWorkDayTypeInOrgMap.get(validDate.toString()));
					}
				}
			}
		}
		return checkPsnCalendarDay(pk_hrorg, beginDate, endDate, returnMap);
	}

	/**
	 * 员工工作日历会进行修改日历天处理(精确到每人),如果有修改则以这部分修改的日历天为准
	 *
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @param returnMap
	 * @return
	 * @des Ares.Tank 2018-8-31 15:19:50
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Integer>> checkPsnCalendarDay(String pk_hrorg, UFLiteralDate beginDate,
			UFLiteralDate endDate, Map<String, Map<String, Integer>> returnMap) {
		// Map<String, Map<String, Integer>> Map<pk_psndoc,Map<日期,工作日类型>>

		// 查找某些人员和某些日期相对应的datetype
		String sql = "select tbm_psncalendar.pk_psndoc,tbm_psncalendar.calendar,tbm_psncalendar.date_daytype "
				+ "FROM tbm_psncalendar " + "where tbm_psncalendar.calendar >= '" + beginDate + "' "
				+ "and tbm_psncalendar.calendar <= '" + endDate + "' " + "and tbm_psncalendar.pk_org = '" + pk_hrorg
				+ "'" + "and dr=0";
		List<HashMap<String, Object>> result = new ArrayList<>();// 数据库返回结果
		// 封装后的结果 Map<date,map<pk_psndoc,datetype>>,不让循环调用,就只能全丢内存了,
		// 为什么这么封装呢?因为日期比较少,而HashMap的读取效率为O(1),而且人员基本都是完全存在的
		Map<String, HashMap<String, Integer>> resultPackage = new HashMap<>();
		try {
			result = (ArrayList<HashMap<String, Object>>) getBaseDAO().executeQuery(sql, new MapListProcessor());
			for (HashMap<String, Object> colMap : result) {
				if (null != colMap.get("calendar") && null != colMap.get("pk_psndoc")
						&& null != colMap.get("date_daytype")) {
					if (resultPackage.containsKey(colMap.get("calendar"))) {
						resultPackage.get(colMap.get("calendar")).put(colMap.get("pk_psndoc").toString(),
								(Integer) colMap.get("date_daytype"));
					} else {
						HashMap<String, Integer> tempMap = new HashMap<>();
						tempMap.put(colMap.get("pk_psndoc").toString(), (Integer) colMap.get("date_daytype"));
						resultPackage.put(colMap.get("calendar").toString(), tempMap);
					}
				}
			}

		} catch (BusinessException e) {
			result = new ArrayList<>();
			e.printStackTrace();
		}
		// 对返回值进行过滤,优先采用修改过的日历天
		// resultPackage<date,map<pk_psndoc,datetype>>
		// returnMap<pk_psndoc,Map<日期,工作日类型>>
		for (Map.Entry<String, Map<String, Integer>> entry : returnMap.entrySet()) {
			if (null != entry.getValue()) {
				for (Map.Entry<String, Integer> entryInside : entry.getValue().entrySet()) {
					if (null != resultPackage.get(entryInside.getKey())
							&& null != resultPackage.get(entryInside.getKey()).get(entry.getKey())) {// 该员工在该天有被修改过日历天
						/*
						 * 先修复一个bug,各位观众老爷有没有发现标准产品的逻辑,全部人员的日期类型Map都指向了同一个内存空间?
						 * 是的,这是个操蛋的bug,因为这个bug,员工工作日历的粒度从员工降级到了组织,
						 * 一定要吐槽一下,因为这个bug还是浪费了我两天时间!!! (/＞皿<)/ ～ ┴┴ 翻小型桌子
						 * (／‵口′)／~ ╧╧ 翻中等大小桌子 ╯-_-)╯~═╩════╩═~ 翻大型桌子 (╯' -
						 * ')╯(┻━┻ 把全部桌子都掀了
						 */
						Map<String, Integer> copyMap = new HashMap<String, Integer>();
						copyMap.putAll(returnMap.get(entry.getKey()));
						// 将员工工作日历节点修改过的日期类型存入
						returnMap.put(entry.getKey(), copyMap);
						returnMap.get(entry.getKey()).put(entryInside.getKey(),
								resultPackage.get(entryInside.getKey()).get(entry.getKey()));
					}
				}
			}
			// System.out.println("Key: " + entry.getKey() + " Value: " +
			// entry.getValue());
		}

		return returnMap;
	}

	/*
	 * 2011.9.23已完成假日业务单元化处理 (non-Javadoc)
	 *
	 * @see nc.itf.ta.IHRHolidayQueryService#queryPsnWorkDayTypeInfo(java.lang.
	 * String, java.lang.String[], nc.vo.pub.lang.UFLiteralDate,
	 * nc.vo.pub.lang.UFLiteralDate)
	 */
	@Override
	public Map<String, Map<String, Integer>> queryPsnWorkDayTypeInfo(String pk_hrorg, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {

		return queryPsnWorkDayTypeInfo(pk_hrorg, pk_psndocs, beginDate, endDate, false);
	}

	@Override
	public Map<String, Integer> queryTeamWorkDayTypeInfo(String pk_org, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		// Map<String, Integer> returnMap = new HashMap<String, Integer>();
		HolidayInfo<HRHolidayVO> holidayInfo = queryHolidayInfo(pk_org, beginDate, endDate);
		// UFLiteralDate[] dateScope = CommonUtils.createDateArray(beginDate,
		// endDate);
		// // 如果没有假日,则所有人都情况都一样：周1到周5工作日，周末非工作日
		// if(holidayInfo==null||ArrayUtils.isEmpty(holidayInfo.getHolidayVOs())){
		// for(UFLiteralDate date:dateScope)
		// returnMap.put(date.toString(), HolidayVO.getSimpleWorkDayType(date));
		// return returnMap;
		// }
		// //存储假日的map，key是日期，value是holidaycopyvo，例如国庆，此map中存储的数据就是10.1---国庆，10.2---国庆，10.3---国庆，对调日期不存进去
		// Map<String, HRHolidayVO> holidayMap = holidayInfo.getHolidayMap();
		// //存储班次对调的map，key、value都是日期，所有对调和被对调的日期都作为key存进去了
		// Map<String, String> switchMap = holidayInfo.getSwitchMap();
		// // 按天循环判断工作日类型
		// for(UFLiteralDate date : dateScope) {
		// String strDate = date.toStdString();
		// // 如果不是假日,要看当天是否与另外一天对调了
		// if(holidayMap.get(strDate)==null) {
		// UFLiteralDate realDate =
		// switchMap.containsKey(strDate)?UFLiteralDate.getDate(switchMap.get(strDate)):date;
		// returnMap.put(strDate, HolidayVO.getSimpleWorkDayType(realDate));
		// continue;
		// }
		// // 是假日则要看是否全员享有
		// returnMap.put(strDate,
		// holidayMap.get(strDate).isAllEnjoy()?HolidayVO.DAY_TYPE_HOLIDAY:HolidayVO.DAY_TYPE_HOLIDAY_NOTALL);
		// }
		// return returnMap;
		return queryTeamWorkDayTypeInfo(pk_org, beginDate, endDate, holidayInfo);
	}

	/**
	 * 抽取上面的方法，共用
	 *
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @param holidayInfo
	 * @return
	 * @description 此方法只会返回组织默认的日历天,不会返回具体到班组的方法,如果各个班组修改了自己的日历天类型,
	 *              那么这个方法的返回值是不准确的,此方法只可用于班组排班的时候查询默认的排班
	 * @throws BusinessException
	 */
	private Map<String, Integer> queryTeamWorkDayTypeInfo(String pk_org, UFLiteralDate beginDate, UFLiteralDate endDate,
			HolidayInfo<HRHolidayVO> holidayInfo) throws BusinessException {
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		UFLiteralDate[] dateScope = CommonUtils.createDateArray(beginDate, endDate);
		// 如果没有假日,则所有人都情况都一样：周1到周5工作日，周末非工作日
		if (holidayInfo == null || ArrayUtils.isEmpty(holidayInfo.getHolidayVOs())) {
			for (UFLiteralDate date : dateScope)
				returnMap.put(date.toString(), HolidayVO.getSimpleWorkDayType(date));
			return returnMap;
		}
		// 存储假日的map，key是日期，value是holidaycopyvo，例如国庆，此map中存储的数据就是10.1---国庆，10.2---国庆，10.3---国庆，对调日期不存进去
		Map<String, HRHolidayVO> holidayMap = holidayInfo.getHolidayMap();
		// 存储班次对调的map，key、value都是日期，所有对调和被对调的日期都作为key存进去了
		Map<String, String> switchMap = holidayInfo.getSwitchMap();
		// 按天循环判断工作日类型
		for (UFLiteralDate date : dateScope) {
			String strDate = date.toStdString();
			// 如果不是假日,要看当天是否与另外一天对调了
			if (holidayMap.get(strDate) == null) {
				UFLiteralDate realDate = switchMap.containsKey(strDate) ? UFLiteralDate.getDate(switchMap.get(strDate))
						: date;
				returnMap.put(strDate, HolidayVO.getSimpleWorkDayType(realDate));
				continue;
			}
			// 是假日则要看是否全员享有
			returnMap.put(strDate, holidayMap.get(strDate).isAllEnjoy() ? HolidayVO.DAY_TYPE_HOLIDAY
					: HolidayVO.DAY_TYPE_HOLIDAY_NOTALL);
		}
		return returnMap;
	}

	/**
	 * 批量优化方法 业务单元日期范围内的每一天的工作日情况 此信息与排班无关，与人员无关
	 *
	 * @param pk_orgs
	 * @param beginDate
	 * @param endDate
	 * @return <业务单元，<日期，工作日类型>>
	 * @description 此方法只会返回组织默认的日历天,不会返回具体到班组的方法,如果各个班组修改了自己的日历天类型,
	 *              那么这个方法的返回值是不准确的,此方法只可用于班组排班的时候查询默认的排班
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Map<String, Integer>> queryTeamWorkDayTypeInfos(String[] pk_orgs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_orgs))
			return null;
		Map<String, HolidayInfo<HRHolidayVO>> HolidayInfos = queryHolidayInfos(pk_orgs, beginDate, endDate);
		Map<String, Map<String, Integer>> retMap = new HashMap<String, Map<String, Integer>>();
		UFBoolean isLocation = null;
		// 本地化日曆天
		Map<String, Map<String, Integer>> orgDateType = findWorkDayType(pk_orgs, beginDate, endDate);
		for (String pk_org : pk_orgs) {
			isLocation = SysInitQuery.getParaBoolean(pk_org, "TWHR01");
			if (null == isLocation) {
				throw new BusinessException("未找到本地化參數:TWHR01");
			}
			if (isLocation.booleanValue()) {
				// 啟用本地化的參數,直接從組織關聯的--工作日曆-全局讀取,
				retMap.put(pk_org, orgDateType.get(pk_org));
			} else {
				retMap.put(pk_org, queryTeamWorkDayTypeInfo(pk_org, beginDate, endDate, HolidayInfos.get(pk_org)));
			}
		}
		return retMap;

	}

	/**
	 * 批量优化方法 查询每一个班组的日历天类型
	 *
	 * @param pk_orgs
	 * @param beginDate
	 * @param endDate
	 * @param pk_teams
	 *            班组pk合集
	 * @return <班组pk，<日期，工作日类型>>
	 * @author Ares.Tank 2018-9-7 12:21:46
	 * @throws BusinessException
	 * @description
	 * @see
	 */
	@Override
	public Map<String, Map<String, Integer>> queryTeamWorkDayTypeInfos4View(String[] pkTeams, String[] pkOrgs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {

		// 首先查询一下整个组织默认的工作日历
		// Map<业务单元，<日期，工作日类型>>
		Map<String, Map<String, Integer>> defualtOrgMap = queryTeamWorkDayTypeInfos(pkOrgs, beginDate, endDate);
		if (null == defualtOrgMap || defualtOrgMap.size() <= 0) {// 为空返回
			return new HashMap<String, Map<String, Integer>>();
		} else {
			return checkTeamCalendarDay(defualtOrgMap, pkTeams, pkOrgs, beginDate, endDate);

		}

	}

	/**
	 * 班组工作日历会进行修改日历天处理(精确到班组),如果有修改则以这部分修改的日历天为准
	 *
	 * @param pk_hrorgs
	 *            Map<业务单元，<日期，工作日类型>>
	 * @param beginDate
	 * @param endDate
	 * @param returnMap
	 * @return @return <班组pk，<日期，工作日类型>>
	 * @des Ares.Tank 2018-8-31 15:19:50
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Integer>> checkTeamCalendarDay(Map<String, Map<String, Integer>> defaultOrgMap,
			String[] pkTeams, String[] pk_hrorgs, UFLiteralDate beginDate, UFLiteralDate endDate) {
		Map<String, Map<String, Integer>> mapForReturn = new HashMap<>();
		for (String pk_hrorg : pk_hrorgs) {
			// Map<String, Map<String, Integer>> Map<pk_psndoc,Map<日期,工作日类型>>

			// 查找某些人员和某些日期相对应的datetype
			String sql = "select bd_teamcalendar.pk_team,bd_teamcalendar.calendar,bd_teamcalendar.date_daytype "
					+ "from bd_teamcalendar  " + "where bd_teamcalendar.calendar >='" + beginDate + "'  "
					+ "and bd_teamcalendar.calendar <='" + endDate + "'  " + "and  bd_teamcalendar.pk_org ='" + pk_hrorg
					+ "' " + "and dr = 0 ";
			List<HashMap<String, Object>> result = new ArrayList<>();// 数据库返回结果
			// 封装后的结果 Map<date,map<pkTeam,datetype>>,不让循环调用,就只能全丢内存了,
			Map<String, HashMap<String, Integer>> resultPackage = new HashMap<>();
			try {
				result = (ArrayList<HashMap<String, Object>>) getBaseDAO().executeQuery(sql, new MapListProcessor());
				for (HashMap<String, Object> colMap : result) {
					if (null != colMap.get("calendar") && null != colMap.get("pk_team")
							&& null != colMap.get("date_daytype")) {
						if (resultPackage.containsKey(colMap.get("calendar"))) {
							resultPackage.get(colMap.get("calendar")).put(colMap.get("pk_team").toString(),
									(Integer) colMap.get("date_daytype"));
						} else {
							HashMap<String, Integer> tempMap = new HashMap<>();
							tempMap.put(colMap.get("pk_team").toString(), (Integer) colMap.get("date_daytype"));
							resultPackage.put(colMap.get("calendar").toString(), tempMap);
						}
					}
				}

			} catch (BusinessException e) {
				result = new ArrayList<>();
				e.printStackTrace();
			}

			// 对返回值进行改造,优先采用修改过的日历天
			// 原: defaultMap <业务单元，<日期，工作日类型>>
			// 改造后: mapForReturn <pk_team,Map<日期,工作日类型>>
			// resultPackage<date,map<pk_team,datetype>>
			for (String pk_team : pkTeams) {
				Map<String, Integer> tempDateTypeMap = new HashMap<>();
				if (null == defaultOrgMap.get(pk_hrorg)) {
					continue;
				}
				tempDateTypeMap.putAll(defaultOrgMap.get(pk_hrorg));
				mapForReturn.put(pk_team, tempDateTypeMap);// 加入默认的日历天类型
				// 如果班组工作日历节点有修改,则原先按照修改过的数据
				for (Map.Entry<String, Map<String, Integer>> entry : mapForReturn.entrySet()) {
					if (null != entry.getValue()) {
						for (Map.Entry<String, Integer> entryInside : entry.getValue().entrySet()) {
							if (null != resultPackage.get(entryInside.getKey())
									&& null != resultPackage.get(entryInside.getKey()).get(entry.getKey())) {
								// Map<String, Integer> copyMap = new
								// HashMap<String, Integer>();
								// copyMap.putAll(mapForReturn.get(entry.getKey()));
								// 将班组工作日历节点修改过的日期类型存入
								// mapForReturn.put(entry.getKey(), copyMap);
								mapForReturn.get(entry.getKey()).put(entryInside.getKey(),
										resultPackage.get(entryInside.getKey()).get(entry.getKey()));
							}
						}
					}
				}
			}
		}
		return mapForReturn;
	}

	/**
	 * 查询某个业务单元的HolidayInfo。若缓存infoMap里面已有，则直接返回。若没有，则查询后放入map再返回
	 *
	 * @param pk_org
	 * @param infoMap
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	private HolidayInfo<HRHolidayVO> queryHolidayInfo(String pk_org, Map<String, HolidayInfo<HRHolidayVO>> infoMap,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if (infoMap.containsKey(pk_org))
			return infoMap.get(pk_org);
		HolidayInfo<HRHolidayVO> info = queryHolidayInfo(pk_org, beginDate, endDate);
		infoMap.put(pk_org, info);
		return info;
	}

	/**
	 * 处理一批人员在某业务单元内的假日享有情况。处理时，假设人员在日期内的任职都属于此业务单元。如果人员任职有不属于此业务单元的情况，此
	 * 方法不负责处理，在调用此方法的地方想办法容错 这种场景最简单
	 *
	 * @param pk_org
	 * @param pk_psndocs
	 * @param dates
	 * @param holidayInfo，此业务单元的假日信息
	 * @param onlyAbnormalDayInMap，true，返回的map里面，只包含异常天（即假日和对调日）；否则包含所有天
	 * @return <pk_psndoc,<date,工作日类型>>
	 * @throws BusinessException
	 */
	protected <T extends HolidayVO> Map<String, Map<String, Integer>> queryPsnWorkDayTypeInfo4SingleOrg(String pk_org,
			String[] pk_psndocs, UFLiteralDate[] continuousDates, HolidayInfo<T> holidayInfo,
			boolean onlyAbnormalDayInMap) throws BusinessException {

		Map<String, Map<String, Integer>> returnMap = new HashMap<String, Map<String, Integer>>();
		///////////////////////////////////////// 如果日期范围内没有假日，则很简单，所有人都情况都一样：周1到周5工作日，周末非工作日///////////////////////////////
		if (holidayInfo == null || ArrayUtils.isEmpty(holidayInfo.getHolidayVOs())) {
			Map<String, Integer> dayTypeMap = new HashMap<String, Integer>();
			if (!onlyAbnormalDayInMap)
				for (UFLiteralDate date : continuousDates) {
					dayTypeMap.put(date.toString(), HolidayVO.getSimpleWorkDayType(date));
				}
			for (String pk_psndoc : pk_psndocs) {
				returnMap.put(pk_psndoc, dayTypeMap);
			}
			if (0 == continuousDates.length) {
				return returnMap;
			} else if (1 == continuousDates.length) {
				return checkPsnCalendarDay(pk_org, continuousDates[0], continuousDates[0], returnMap);
			} else {
				return checkPsnCalendarDay(pk_org, continuousDates[0], continuousDates[1], returnMap);
			}

		}
		// 存储假日的map，key是日期，value是holidaycopyvo，例如国庆，此map中存储的数据就是10.1---国庆，10.2---国庆，10.3---国庆，对调日期不存进去
		Map<String, T> holidayMap = holidayInfo.getHolidayMap();
		// 存储班次对调的map，key、value都是日期，所有对调和被对调的日期都作为key存进去了
		Map<String, String> switchMap = holidayInfo.getSwitchMap();
		// 这些假日是否所有人都享有
		boolean allPersonHoliday = true;
		for (T holidayVO : holidayInfo.getHolidayVOs()) {
			if (!holidayVO.isAllEnjoy()) {
				allPersonHoliday = false;
				break;
			}
		}
		/////////////////////////////////////////////////////// 如果假日对所有人都享有，则处理也很简单，直接处理假日，以及对调的日期即可，所有的人都一样：
		if (allPersonHoliday) {
			Map<String, Integer> dayTypeMap = new HashMap<String, Integer>();
			for (UFLiteralDate date : continuousDates) {
				// 假日
				if (holidayMap.containsKey(date.toString())) {
					dayTypeMap.put(date.toString(), HolidayVO.DAY_TYPE_HOLIDAY);
					continue;
				}
				// 当天对应的真正的天，无对调的话就是当天，有对调的话，应该是对调天
				boolean isSwitch = switchMap.containsKey(date.toString());// 是否有对调
				if (!isSwitch && onlyAbnormalDayInMap)// 如果无对调，且不返回正常天，则不用处理此天
					continue;
				// 如果对调了，或者返回所有天，则要处理此天
				UFLiteralDate realDate = isSwitch ? UFLiteralDate.getDate(switchMap.get(date.toString())) : date;
				dayTypeMap.put(date.toString(), HolidayVO.getSimpleWorkDayType(realDate));
			}
			for (String pk_psndoc : pk_psndocs) {
				returnMap.put(pk_psndoc, dayTypeMap);
			}
			if (0 == continuousDates.length) {
				return returnMap;
			} else if (1 == continuousDates.length) {
				return checkPsnCalendarDay(pk_org, continuousDates[0], continuousDates[0], returnMap);
			} else {
				return checkPsnCalendarDay(pk_org, continuousDates[0], continuousDates[1], returnMap);
			}
		}
		//////////////////////////////////////////////////////// 如果假日不是所有人都享有，则要按人进行处理，因为不同的人假日可能不一样
		// 假日享有情况，key是人员主键，value的key是假日的定义主键，value是是否享有
		Map<String, Map<String, Boolean>> enjoyMap = CommonUtils
				.transferMap(queryHolidayEnjoyInfo(pk_org, pk_psndocs, holidayInfo.getHolidayVOs()));
		for (String pk_psndoc : pk_psndocs) {
			// 当前人员的假日享有情况，key是假日的定义主键
			Map<String, Boolean> holidayEnjoyMap = enjoyMap == null ? null : enjoyMap.get(pk_psndoc);
			Map<String, Integer> dayTypeMap = new HashMap<String, Integer>();
			for (UFLiteralDate date : continuousDates) {
				// 当前日期对应的假日
				T holiday = holidayMap.get(date.toString());
				// 如果是假日，且当前人员享有
				if (holiday != null && holidayEnjoyMap != null && holidayEnjoyMap.get(holiday.getPk_holiday())) {
					dayTypeMap.put(date.toString(), HolidayVO.DAY_TYPE_HOLIDAY);
					continue;
				}
				// 若不是假日，或者是假日但不享有，则要看当天是否与另外一天对调了
				boolean isSwitch = switchMap.containsKey(date.toString());// 是否有对调
				if (!isSwitch && onlyAbnormalDayInMap)// 如果无对调，且不返回正常天，则不用处理此天
					continue;
				// 如果对调了，或者返回所有天，则要处理此天
				UFLiteralDate realDate = switchMap.containsKey(date.toString())
						? UFLiteralDate.getDate(switchMap.get(date.toString())) : date;
				dayTypeMap.put(date.toString(), HolidayVO.getSimpleWorkDayType(realDate));
			}
			returnMap.put(pk_psndoc, dayTypeMap);
		}
		if (0 == continuousDates.length) {
			return returnMap;
		} else if (1 == continuousDates.length) {
			return checkPsnCalendarDay(pk_org, continuousDates[0], continuousDates[0], returnMap);
		} else {
			return checkPsnCalendarDay(pk_org, continuousDates[0], continuousDates[1], returnMap);
		}
	}

	@Override
	public Map<String, HRHolidayVO[]> queryHolidayVOsByHROrg(String pk_hrorg, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// 查询HR组织下所有业务单元的pk_org(还包括考勤档案中的例外组织，即不是HR组织管理的业务单元，但也出现在考勤档案中的)
		String[] orgPks = psndocService.queryAllBUPksByHROrg(pk_hrorg, beginDate, endDate, true);
		return queryHolidayVOsByOrgs(orgPks);
	}

	@Override
	public Map<String, HolidayInfo<HRHolidayVO>> queryHolidayInfoByHROrg(String pk_hrorg, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		Map<String, HRHolidayVO[]> holidayVOMap = queryHolidayVOsByHROrg(pk_hrorg, beginDate, endDate);
		Map<String, HolidayInfo<HRHolidayVO>> retMap = new HashMap<String, HolidayInfo<HRHolidayVO>>();
		if (MapUtils.isEmpty(holidayVOMap))
			return retMap;
		for (String pk_org : holidayVOMap.keySet()) {
			retMap.put(pk_org, createHolidayInfo(pk_org, holidayVOMap.get(pk_org)));
		}
		return retMap;
	}

	private <T extends HolidayVO> Map<String, Integer> queryPsnWorkDayTypeInOrg(String pk_psndoc, String pk_org,
			HolidayInfo<T> holidayInfo, UFLiteralDate[] continuousDates, boolean onlyAbnormalDayInMap)
			throws BusinessException {
		return queryPsnWorkDayTypeInfo4SingleOrg(pk_org, new String[] { pk_psndoc }, continuousDates, holidayInfo,
				onlyAbnormalDayInMap).get(pk_psndoc);
	}

	/**
	 * 将某个业务单元的假日VO数组的假日信息、对调信息包装一下，使之更便于使用
	 *
	 * @param <T>
	 * @param pk_org
	 * @param holidayVOs
	 * @return
	 * @throws BusinessException
	 */
	private <T extends HolidayVO> HolidayInfo<T> createHolidayInfo(String pk_org, T[] holidayVOs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(holidayVOs))
			return null;
		// 存储假日的map，key是日期，value是holidaycopyvo，例如国庆，此map中存储的数据就是10.1---国庆，10.2---国庆，10.3---国庆，对调日期不存进去
		Map<String, T> holidayMap = new HashMap<String, T>();
		// 存储班次对调的map，key、value都是日期，所有对调和被对调的日期都作为key存进去了
		Map<String, String> switchMap = new HashMap<String, String>();
		// 存储对调日所属假日的map，key是所有对调和被对调的日期，value是此对调日期所属的假日
		Map<String, T> switchHolidayMap = new HashMap<String, T>();
		// 存储假日时间段的map
		Map<String, ITimeScope> scopeMap = new HashMap<String, ITimeScope>();
		// 业务单元的时区,修改说明减少查询数据库次数
		TimeZone timeZone = null;
		if (holidayVOs instanceof HRHolidayVO[]) {
			timeZone = ((HRHolidayVO) holidayVOs[0]).getTimeZone();
		}
		if (timeZone == null)
			timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		for (T holidayVO : holidayVOs) {
			// 获取假日的开始、结束日期
			UFLiteralDate holidayBeginDate = UFLiteralDate.getDate(holidayVO.getStarttime().substring(0, 10));
			UFLiteralDate holidayEndDate = UFLiteralDate.getDate(holidayVO.getEndtime().substring(0, 10));
			// 此假日的时间段，要考虑组织时区
			ITimeScope scope = null;
			if (holidayVO instanceof ITimeScope)
				scope = (ITimeScope) holidayVO;
			else
				scope = holidayVO.toTimeScope(timeZone);
			scopeMap.put(holidayVO.getPk_holiday(), scope);
			UFLiteralDate[] holidayDates = CommonUtils.createDateArray(holidayBeginDate, holidayEndDate);
			for (UFLiteralDate holidayDate : holidayDates) {
				holidayMap.put(holidayDate.toString(), holidayVO);
			}
			// 处理对调的日期
			for (int i = 1; i <= HolidayVO.SWITCH_COUNT(); i++) {
				UFLiteralDate switchDate = holidayVO.getSwitchDate(i);
				UFLiteralDate switchToDate = holidayVO.getSwitchToDate(i);
				// 不对调不处理
				if (switchDate == null || switchToDate == null)
					continue;
				switchMap.put(switchDate.toString(), switchToDate.toString());
				switchMap.put(switchToDate.toString(), switchDate.toString());
				switchHolidayMap.put(switchDate.toString(), holidayVO);
				switchHolidayMap.put(switchToDate.toString(), holidayVO);
			}
		}
		HolidayInfo<T> info = new HolidayInfo<T>(holidayMap, switchMap, scopeMap);
		info.setSwitchHolidayMap(switchHolidayMap);
		info.setHolidayVOs(holidayVOs);
		return info;
	}

	@Override
	public Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo(String pk_hrorg, String[] pk_psndocs,
			Map<String, HRHolidayVO[]> holidayMap) throws BusinessException {
		if (MapUtils.isEmpty(holidayMap))
			return null;
		List<HRHolidayVO> holidayList = new ArrayList<HRHolidayVO>();
		for (String pk_org : holidayMap.keySet())
			CollectionUtils.addAll(holidayList, holidayMap.get(pk_org));
		UFLiteralDate[] earliestLatestDates = HolidayVO.getEarliestLatestDate(
				CollectionUtils.isEmpty(holidayList) ? null : holidayList.toArray(new HRHolidayVO[0]));
		UFLiteralDate firstDate = earliestLatestDates[0];// 假日信息的最早天
		UFLiteralDate latestDate = earliestLatestDates[1];// 假日信息的最晚天
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocs(null, pk_psndocs, firstDate, latestDate, true, true, null);
		// <pk_holiday+业务单元主键，<人员,是否享有>>
		Map<String, Map<String, Boolean>> retMap = new HashMap<String, Map<String, Boolean>>();
		// 按业务单元循环处理享有情况
		for (String pk_org : holidayMap.keySet()) {
			HRHolidayVO[] holidayVOs = holidayMap.get(pk_org);
			if (ArrayUtils.isEmpty(holidayVOs))
				continue;
			// <pk_holiday,<pk_psndoc,是否享有>>
			Map<String, Map<String, Boolean>> enjoyMap = queryHolidayEnjoyInfo(pk_org, pk_psndocs,
					CommonMethods.filterTBMPsndocByOrg(pk_org, psndocMap), holidayVOs);
			if (MapUtils.isEmpty(enjoyMap))
				continue;
			for (HRHolidayVO holidayVO : holidayVOs) {
				String pk_holiday = holidayVO.getPk_holiday();
				Map<String, Boolean> psnEnjoyMap = enjoyMap.get(pk_holiday);
				retMap.put(pk_holiday + pk_org, psnEnjoyMap);
			}
		}

		return retMap;
	}

	@Override
	public Map<String, Map<String, Boolean>> queryHolidayEnjoyInfo2(String pk_hrorg, String[] pk_psndocs,
			Map<String, HRHolidayVO[]> holidayMap) throws BusinessException {
		return CommonUtils.transferMap(queryHolidayEnjoyInfo(pk_hrorg, pk_psndocs, holidayMap));
	}

	/**
	 * 查询人员的假日享有情况，boolean数组的顺序与人员主键数组的顺序一样
	 *
	 * @param pk_org，业务单元主键
	 * @param pk_psndocs
	 * @param holidayVO
	 * @return
	 * @throws BusinessException
	 */
	// protected boolean[] queryHolidayEnjoyInfo(String pk_org, String[]
	// pk_psndocs, HolidayVO holidayVO) throws BusinessException{
	// UFLiteralDate[] earliestLatestDates = holidayVO.getEarliestLatestDate();
	// UFLiteralDate firstDate = earliestLatestDates[0];
	// UFLiteralDate latestDate = earliestLatestDates[1];
	// Map<String, List<TBMPsndocVO>> psndocMap =
	// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocMapByPsndocsBU(pk_org,
	// pk_psndocs, firstDate, latestDate, true);
	// return queryHolidayEnjoyInfo(pk_org, pk_psndocs, psndocMap, holidayVO);
	// }
	/**
	 * 查询人员的假日享有情况，boolean数组的顺序与人员主键数组的顺序一样
	 *
	 * @param pk_org，业务单元主键
	 * @param pk_psndocs
	 * @param holidayVO
	 * @return
	 * @throws BusinessException
	 */
	// protected boolean[] queryHolidayEnjoyInfo(String pk_org, String[]
	// pk_psndocs,Map<String, List<TBMPsndocVO>> psndocMap, HolidayVO holidayVO)
	// throws BusinessException{
	// boolean[] returnArray = new boolean[pk_psndocs.length];
	// Set<String> enjoySet = new HolidayDAO().queryEnjoyHolidayPsn(pk_org,
	// psndocMap, holidayVO);
	// if(org.springframework.util.CollectionUtils.isEmpty(enjoySet)){
	// for(int i=0;i<pk_psndocs.length;i++){
	// returnArray[i]=false;
	// }
	// return returnArray;
	// }
	//
	// for(int i=0;i<pk_psndocs.length;i++){
	// //set中有此人员则为true，否则为false
	// returnArray[i]=enjoySet.contains(pk_psndocs[i]);
	// }
	// return returnArray;
	// }

	@Override
	public Map<String, Integer> queryPsnWorkDayTypeInfo(String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// 首先查询此人在日期范围内所有的考勤档案
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		TBMPsndocVO[] psndocVOs = psndocService.queryTBMPsndocVOsByPsndocDate(pk_psndoc, beginDate, endDate);
		if (ArrayUtils.isEmpty(psndocVOs))
			return null;
		Map<String, Integer> retMap = new HashMap<String, Integer>();
		IDateScope dateScope = new DefaultDateScope(beginDate, endDate);
		for (TBMPsndocVO vo : psndocVOs) {
			// 考勤档案与用户查询日期范围的交集
			IDateScope interDateScope = DateScopeUtils.intersectionDateScope(dateScope, vo);
			if (interDateScope == null)
				continue;
			// 查询此人在此考勤档案内的工作日类型
			UFLiteralDate[] continuousDates = CommonUtils.createDateArray(interDateScope.getBegindate(),
					interDateScope.getEnddate());
			Map<String, Map<String, Integer>> workDayTypeMap = queryPsnWorkDayTypeInfo4SingleOrg(vo.getPk_joborg(),
					new String[] { pk_psndoc }, continuousDates,
					queryHolidayInfo(vo.getPk_joborg(), vo.getBegindate(), vo.getEnddate()), false);
			if (MapUtils.isEmpty(workDayTypeMap))
				continue;
			Map<String, Integer> thisWorkDayTypeMap = workDayTypeMap.get(pk_psndoc);
			// 将工作日类型的map的内容同步到retMap中，注意不能用Map.putAll方法，因为thisWorkDayTypeMap中可能有考勤档案无效的日期（见queryHolidayInfo方法的注释，如果有这种日期，需要自己处理）
			for (UFLiteralDate date : continuousDates) {
				retMap.put(date.toString(), thisWorkDayTypeMap.get(date.toString()));
			}
		}

		return retMap;
	}

	/**
	 * 查询人员的工作日类型,此方法只在员工工作日历排班时使用,排班是不会涉及原有的工作日类型的影响,只会根据默认规则来生成日历天的类型
	 *
	 * @param pk_hrorg
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param onlyAbnormalDayInMap,true,则map里面只会出现有异常的天(即假日和对调日),否则出现所有的天
	 * @return
	 * @author Ares.Tank 2018-9-6 16:00:58
	 * @throws BusinessException
	 */
	@Override
	public Map<String, Map<String, Integer>> queryPsnWorkDayTypeInfo4PsnCalenderInsert(String pk_hrorg,
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, boolean onlyAbnormalDayInMap)
			throws BusinessException {
		Map<String, Map<String, Integer>> returnMap = new HashMap<String, Map<String, Integer>>();
		if (null == pk_psndocs) {
			return returnMap;
		}
		// 获取启用参数
		//先获取法人组织
		String[] orgs = { pk_hrorg };
		String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs)
				.get(pk_hrorg);
				
		UFBoolean isLocation = SysInitQuery.getParaBoolean(legal_pk_org, "TWHR01");
		if (null == isLocation) {
			throw new BusinessException("未找到本地化參數:TWHR01");
		}
		if (isLocation.booleanValue()) {
			String[] pk_hrorgs = { pk_hrorg };
			Map<String, Map<String, Integer>> orgDateType = findWorkDayType(pk_hrorgs, beginDate, endDate);
			Map<String, Integer> typeMap = orgDateType.get(pk_hrorg);
			if (null == typeMap) {
				throw new BusinessException("未找到組織關聯的工作日曆!");
			}
			// 所有人员都一样,将组织换成人员
			for (String pk_psndoc : pk_psndocs) {
				returnMap.put(pk_psndoc, typeMap);
			}

			return returnMap;
		}

		UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
		// HR组织下所有业务单元的假日
		Map<String, HolidayInfo<HRHolidayVO>> holidayInfoMap = queryHolidayInfoByHROrg(pk_hrorg, beginDate, endDate);
		String[] pk_orgs = holidayInfoMap.keySet().toArray(new String[0]);// HR组织下所有业务单元

		// HR组织下有至少一个业务单元，采用分业务单元依次处理的策略：按业务单元循环，依次判断这批人员在业务单元内的假日享有情况，然后再将map合并
		// 每个组织的人员享有情况，<pk_org,<pk_psndoc,<日期,工作日类型>>>
		Map<String, Map<String, Map<String, Integer>>> orgPsnEnjoyMap = new HashMap<String, Map<String, Map<String, Integer>>>();
		for (String pk_org : pk_orgs) {
			orgPsnEnjoyMap.put(pk_org, queryPsnWorkDayTypeInfo4SingleOrg(pk_org, pk_psndocs, dates,
					holidayInfoMap.get(pk_org), onlyAbnormalDayInMap));
		}
		// 将map的结构转换一下，改为以人员主键为外层key，存储人员在不同组织内的工作日类型
		Map<String, Map<String, Map<String, Integer>>> psnOrgEnjoyMap = CommonUtils.transferMap(orgPsnEnjoyMap);
		// 所有人员在日期范围内的考勤档案，key是人员主键
		Map<String, List<TBMPsndocVO>> tbmpsndocVOMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate, true, true, null);
		if (MapUtils.isEmpty(tbmpsndocVOMap))
			return null;
		// 按人员依次处理其在多个任职组织内的假日享有情况
		for (String pk_psndoc : pk_psndocs) {
			// 此人员在多个业务单元的工作日类型，key是业务单元,内层key是日期
			Map<String, Map<String, Integer>> orgEnjoyMap = psnOrgEnjoyMap == null
					? new HashMap<String, Map<String, Integer>>() : psnOrgEnjoyMap.get(pk_psndoc);
			// 此人员在日期范围内在pk_hrorg内的所有考勤档案记录，记录上set了任职业务单元主键字段
			List<TBMPsndocVO> psndocVOList = tbmpsndocVOMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(psndocVOList))
				continue;
			// 将考勤档案按任职所属业务单元分组
			Map<String, TBMPsndocVO[]> orgGroupPsndocVOMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_JOBORG,
					psndocVOList.toArray(new TBMPsndocVO[0]));
			// 按正常的业务逻辑，考勤档案的任职所属业务单元应该不会超过行政体系中HR组织下业务单元的范围（因为加考勤档案时做了限制，只能加HR组织下所属业务单元的任职）
			// 但是，由于可以在人事模块直接修改任职记录的组织，因此，有可能造成有些考勤档案的任职所属组织超出了HR组织下业务单元的范围
			// 如果在日期范围内，人员只在同一个组织任职（这是占绝大多数的情况），则直接使用此人员在此组织内的工作日类型即可
			if (orgGroupPsndocVOMap.size() == 1) {
				String pk_org = orgGroupPsndocVOMap.keySet().toArray(new String[0])[0];
				if (orgEnjoyMap.keySet().contains(pk_org)) {
					returnMap.put(pk_psndoc, orgEnjoyMap.get(pk_org));
					continue;
				}
				// 如果此人员的任职组织不在HR组织的管辖范围内，则比较麻烦，因为上面的工作日类型处理逻辑都只是考虑了HR组织范围内的业务
				// 单元，因此此处要单独处理此人在此任职组织的工作日类型
				returnMap.put(pk_psndoc, queryPsnWorkDayTypeInOrg(pk_psndoc, pk_org,
						queryHolidayInfo(pk_org, holidayInfoMap, beginDate, endDate), dates, onlyAbnormalDayInMap));
				continue;
			}
			// 如果人员任职在多个组织，则按组织循环处理
			String[] psnpk_orgs = orgGroupPsndocVOMap.keySet().toArray(new String[0]);
			Map<String, Integer> psnWorkDayTypeMap = new HashMap<String, Integer>();// 此人的工作日类型map，用来作为返回值
			returnMap.put(pk_psndoc, psnWorkDayTypeMap);
			for (String psnpk_org : psnpk_orgs) {// 按组织循环
				// 人员在此组织内的工作日类型
				Map<String, Integer> psnWorkDayTypeInOrgMap = orgEnjoyMap.containsKey(psnpk_org)
						? orgEnjoyMap.get(psnpk_org)
						: queryPsnWorkDayTypeInOrg(pk_psndoc, psnpk_org,
								queryHolidayInfo(psnpk_org, holidayInfoMap, beginDate, endDate), dates,
								onlyAbnormalDayInMap);
				// 此人在此业务单元的考勤档案
				TBMPsndocVO[] psndocVOsInOrg = orgGroupPsndocVOMap.get(psnpk_org);
				// 将此业务单元内的考勤档案的有效天的工作日类型放到返回值的map中
				for (TBMPsndocVO psndovVO : psndocVOsInOrg) {
					UFLiteralDate[] validPsndocDates = CommonUtils.createDateArray(psndovVO.getBegindate(),
							psndovVO.getEnddate());
					for (UFLiteralDate validDate : validPsndocDates) {
						if (!onlyAbnormalDayInMap) {// 如果返回的map存储所有天，则考勤档案有效的每一天都要put进去
							psnWorkDayTypeMap.put(validDate.toString(),
									psnWorkDayTypeInOrgMap.get(validDate.toString()));
							continue;
						}
						// 否则，返回的map只put进入异常天（假日，对调日）
						if (psnWorkDayTypeInOrgMap.containsKey(validDate.toString()))
							psnWorkDayTypeMap.put(validDate.toString(),
									psnWorkDayTypeInOrgMap.get(validDate.toString()));
					}
				}
			}
		}

		return returnMap;
	}

	/**
	 * 从组织关联的工作日历中找到日历天类型
	 *
	 * @return <pk_org,<calendar,Integer>>
	 * @param pk_org
	 *            组织
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @author Ares.Tank
	 * @throws BusinessException
	 * @date 2018年10月6日 上午11:12:19
	 *
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Integer>> findWorkDayType(String[] pk_orgs, UFLiteralDate startDate,
			UFLiteralDate endDate) throws BusinessException {
		Map<String, Map<String, Integer>> result = new HashMap<>();
		if (null == pk_orgs) {
			return result;
		}
		// 查詢組織關聯工作日曆,再查出工作日曆的日曆天類型
		for (String pk_org : pk_orgs) {
			if (null == pk_org) {
				continue;
			}
			String sqlStr = " SELECT workcld.calendardate,workcld.datetype " + " FROM bd_workcalendardate workcld "
					+ " WHERE workcld.pk_workcalendar = (select org.workcalendar "
					+ " from org_orgs org where org.pk_org = '" + pk_org + "') " + " and workcld.calendardate <='"
					+ endDate.getYear() + "-" + endDate.getStrMonth() + "-" + endDate.getStrDay() + "' "
					+ " and workcld.calendardate >='" + startDate.getYear() + "-" + startDate.getStrMonth() + "-"
					+ startDate.getStrDay() + "' " + " AND workcld.dr = 0 ";
			List<Map<String, Object>> qresult = (List<Map<String, Object>>) getBaseDAO().executeQuery(sqlStr,
					new MapListProcessor());
			if (null == qresult) {
				continue;
			}
			Map<String, Integer> org2DayType = new HashMap<>();
			for (Map<String, Object> columMap : qresult) {
				if (null == columMap) {
					continue;
				}
				org2DayType.put((String) columMap.get("calendardate"),
						dayTypeAdapter((Integer) columMap.get("datetype")));
			}
			result.put(pk_org, org2DayType);
		}

		return result;
	}

	/**
	 * 在標準平台中,工作日历有三个类型:工作日0,休息日4,节假日2,例假日3(新加)
	 * 而在员工工作日历中则有四个类型:工作日0,公休(班)1,节假日2,非全员假日,例假日(新加)4,休息日(新加,占用非工作日枚举)1
	 * 这两个地方的日历天类型是用的不同的枚举值(mmp 要命啊,谁设计的) 所以需要进行适配 规则: 工作日历--员工工作日历 工作日0 - 工作日0
	 * 休息日4 - 公休,休息日1 节假日2 - 节假日2 例假日3 - 例假日4
	 *
	 * @param dayType
	 * @return
	 * @author Ares.Tank
	 * @date 2018年10月6日 下午4:21:03
	 * @description
	 */
	public static Integer dayTypeAdapter(Integer dayType) {
		if (null == dayType) {
			return null;
		}
		switch (dayType) {
		case 0:
			return 0;
		case 2:
			return 2;
		case 3:
			return 4;
		case 4:
			return 1;
		}
		return dayType;
	}

}
