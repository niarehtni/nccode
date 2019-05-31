package nc.impl.ta.leavebalance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.persistence.TASimpleDocServiceTemplate;
import nc.itf.om.IOrgInfoQueryService;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveBalanceQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.ml.MultiLangUtil;
import nc.vo.om.orginfo.HROrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author zengcheng
 * 
 */
public class LeaveBalanceServiceImpl implements ILeaveBalanceQueryService, ILeaveBalanceManageService {

	private TASimpleDocServiceTemplate serviceTemplate;

	public TASimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null)
			serviceTemplate = new TASimpleDocServiceTemplate(IMetaDataIDConst.LEAVEBALANCE);
		return serviceTemplate;
	}

	@Override
	public Map<String, LeaveBalanceVO> queryAndCalLeaveBalanceVO(String pk_org, Object... leaveCommonVOs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(leaveCommonVOs))
			return null;
		boolean isReg = leaveCommonVOs[0] instanceof LeaveRegVO;
		// 将休假单按休假类别、年度、期间分组
		Map<String, List<Object>> balanceMap = new HashMap<String, List<Object>>();
		for (Object vo : leaveCommonVOs) {
			Object tmp = null;
			String primaryKey = null;
			if (isReg) {
				LeaveRegVO regVO = ((LeaveRegVO) vo);
				primaryKey = regVO.getPk_leavetype() + regVO.getYearmonth();
				tmp = regVO;
			} else {
				LeavehVO hVO = (vo instanceof AggLeaveVO) ? ((AggLeaveVO) vo).getLeavehVO() : (LeavehVO) vo;
				primaryKey = hVO.getPk_leavetype() + hVO.getLeaveyear()
						+ (StringUtils.isEmpty(hVO.getLeavemonth()) ? "" : hVO.getLeavemonth());
				tmp = hVO;
			}
			List<Object> voList = balanceMap.get(primaryKey);
			if (CollectionUtils.isEmpty(voList)) {
				voList = new ArrayList<Object>();
				balanceMap.put(primaryKey, voList);
			}
			voList.add(tmp);
		}
		// 将分组后的数据批量处理
		Map<String, LeaveTypeCopyVO> typeMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class)
				.queryLeaveCopyTypeMapByOrg(pk_org);
		Map<String, LeaveBalanceVO> resultMap = new HashMap<String, LeaveBalanceVO>();
		for (String key : balanceMap.keySet()) {
			SuperVO[] vos = balanceMap.get(key).toArray(new SuperVO[0]);
			LeaveTypeCopyVO typeVO = typeMap.get(isReg ? ((LeaveRegVO) vos[0]).getPk_leavetype() : ((LeavehVO) vos[0])
					.getPk_leavetype());
			String[] pk_psnorgs = StringPiecer.getStrArrayDistinct(vos, LeaveCommonVO.PK_PSNORG);
			String year = isReg ? ((LeaveRegVO) vos[0]).getLeaveyear() : ((LeavehVO) vos[0]).getLeaveyear();
			String month = isReg ? ((LeaveRegVO) vos[0]).getLeavemonth() : ((LeavehVO) vos[0]).getLeavemonth();
			// 不需要处理结余时长
			Map<String, LeaveBalanceVO> calMap = queryAndCalLeaveBalanceVO(pk_org, typeVO, pk_psnorgs, year, month, vos);
			if (MapUtils.isEmpty(calMap))
				continue;
			resultMap.putAll(calMap);
		}
		return resultMap;
	}

	@Override
	public void calLeaveBalanceVO4NewThread(String pk_org, Object... leaveCommonVOs) throws BusinessException {
		final String pk_hrorg = pk_org;
		final Object[] leaveVOs = leaveCommonVOs;
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					queryAndCalLeaveBalanceVO(pk_hrorg, leaveVOs);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	/**
	 * 查询并计算假期记录
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param pk_psnorgs
	 * @param year
	 * @param month
	 * @return Map<pk_psnorg+pk_timeitem+year+month, List<LeaveBalanceVO>>
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, LeaveBalanceVO> queryAndCalLeaveBalanceVO(String pk_org, LeaveTypeCopyVO typeVO,
			String[] pk_psnorgs, String year, String month, Object[] sources) throws BusinessException {
		if (typeVO == null)
			return null;

		int leavesetperiod = typeVO.getLeavesetperiod().intValue();
		UFLiteralDate periodBeginDate = null;
		UFLiteralDate periodEndDate = null;
		boolean isNotHire = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_MONTH
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
				/* ssx added on 2018-12-20 */
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE;

		LeaveBalanceMaintainImpl impl = new LeaveBalanceMaintainImpl();
		// 如果是按入职日期结算，需要知道入职日期
		Map<String, PsnOrgVO> psnOrgMap = new HashMap<String, PsnOrgVO>();
		PsnOrgVO[] psnOrgVOs = CommonUtils.toArray(PsnOrgVO.class, (Collection<PsnOrgVO>) MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByPKs(PsnOrgVO.class, pk_psnorgs, false));
		psnOrgMap = CommonUtils.toMap(PsnOrgVO.PK_PSNORG, psnOrgVOs);

		if (isNotHire) {// 不是按入职日期（年资起算日）结算的
			boolean isYear = leavesetperiod == TimeItemCopyVO.LEAVESETPERIOD_YEAR;
			if (isYear) {
				PeriodVO[] periodVOs = PeriodServiceFacade.queryByYear(pk_org, year);
				if (ArrayUtils.isEmpty(periodVOs))
					return null;
				// MOD 按自然年结算 James
				periodBeginDate = new UFLiteralDate(year + "-01-01");
				periodEndDate = new UFLiteralDate(year + "-12-31");
				// periodBeginDate = periodVOs[0].getBegindate();
				// periodEndDate = periodVOs[periodVOs.length-1].getEnddate();
			} else {
				/* ssx added on 2018-12-20 */
				if (leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
					periodBeginDate = impl.getHireBeginDate(year, impl.getHireStartDate(psnOrgMap.get(pk_psnorgs[0])));
					periodEndDate = impl.getHireEndDate(year, impl.getHireStartDate(psnOrgMap.get(pk_psnorgs[0])));
				}
				/* end */
				else {
					PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
					if (periodVO == null)
						return null;
					periodBeginDate = periodVO.getBegindate();
					periodEndDate = periodVO.getEnddate();
				}
			}
		}

		IDateScope[] scopes = new IDateScope[pk_psnorgs.length];
		for (int i = 0; i < pk_psnorgs.length; i++) {
			// ssx added on 2018-03-16
			// for changes of start date of company age
			UFLiteralDate begindate = null;
			UFLiteralDate enddate = null;
			// MOD James
			if (leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				begindate = isNotHire ? periodBeginDate : impl.getHireBeginDate(year,
						impl.getHireStartDate(psnOrgMap.get(pk_psnorgs[i])));
				enddate = isNotHire ? periodEndDate : impl.getHireEndDate(year,
						impl.getHireStartDate(psnOrgMap.get(pk_psnorgs[i])));
			} else {
				begindate = isNotHire ? periodBeginDate : impl.getHireBeginDate(year,
						impl.getHireDate(psnOrgMap.get(pk_psnorgs[i])));
				enddate = isNotHire ? periodEndDate : impl.getHireEndDate(year,
						impl.getHireDate(psnOrgMap.get(pk_psnorgs[i])));
			}
			//
			scopes[i] = new DefaultDateScope(begindate, enddate);
		}
		// 期间范围内是否有考勤档案信息，如果没有考勤档案，则肯定不会有结算记录
		String[] noDocPsnOrgs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.existsTBMPsndocs(pk_org, pk_psnorgs, scopes);
		if (!ArrayUtils.isEmpty(noDocPsnOrgs))
			return null;
		// 查询假期计算记录
		Map<String, List<LeaveBalanceVO>> balanceMap = new LeaveBalanceDAO().queryLeaveBalanceMapByPsnOrgs(pk_org,
				typeVO, pk_psnorgs, year, month);
		return findNewestBalanceVO(pk_org, typeVO, pk_psnorgs, year, month, periodBeginDate, periodEndDate, balanceMap,
				sources);
	}

	/**
	 * 查询最新的假期记录
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param pk_psnorgs
	 * @param year
	 * @param month
	 * @param periodBeginDate
	 * @param periodEndDate
	 * @param balanceMap
	 *            Map<pk_psnorg+pk_timeitem+year+month,
	 *            List<LeaveBalanceVO>>数据库中已有的假期计算记录
	 * @param pk_excludSelf
	 * @param isApp
	 * @return Map<pk_psnorg+pk_timeitem+year+month, List<LeaveBalanceVO>>
	 * @throws BusinessException
	 */
	private Map<String, LeaveBalanceVO> findNewestBalanceVO(String pk_org, LeaveTypeCopyVO typeVO, String[] pk_psnorgs,
			String year, String month, UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate,
			Map<String, List<LeaveBalanceVO>> balanceMap, Object[] sources) throws BusinessException {
		List<LeaveBalanceVO> newVOList = new ArrayList<LeaveBalanceVO>(); // 最新的假期计算数据
		Map<String, Integer> needNewMap = new HashMap<String, Integer>(); // 需要创建假期计算数据
		List<LeaveBalanceVO> settVOList = new ArrayList<LeaveBalanceVO>(); // 最新的已结算的假期数据
		List<String> psnorgList = new ArrayList<String>(); // settVOList中的人员列表
		List<IDateScope> scopeList = new ArrayList<IDateScope>(); // settVOList中的日期段列表
		int leavesetPeriod = typeVO.getLeavesetperiod().intValue();
		for (String pk_psnorg : pk_psnorgs) {
			List<LeaveBalanceVO> dbVOs = MapUtils.isEmpty(balanceMap) ? null : balanceMap.get(pk_psnorg);
			if (CollectionUtils.isEmpty(dbVOs)) { // 无假期计算数据
				needNewMap.put(pk_psnorg, 1);
				continue;
			}
			LeaveBalanceVO lastSettVO = null; // 最新的已结算假期数据
			for (LeaveBalanceVO dbVO : dbVOs) {
				if (!dbVO.isSettlement()) { // 如果未结算，就是最新的
					// MOD (補充按年結算的假期計算數據中hirebegindate及hireenddate)
					// by ssx on 2018-12-22
					if (leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
							|| leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
						if (dbVO.getHirebegindate() == null) {
							dbVO.setHirebegindate(periodBeginDate);
							dbVO.setHireenddate(periodEndDate);
						}
					}
					// --
					newVOList.add(dbVO);
					lastSettVO = null;
					break;
				}
				if (lastSettVO == null || lastSettVO.getSettlementdate().before(dbVO.getSettlementdate()))
					lastSettVO = dbVO;
			}
			if (lastSettVO == null)
				continue;
			// 看最后的结算数据，结算日期是否在期间结束之后
			// ssx added on 2018-03-16
			// for changes of start date of company age
			UFLiteralDate endDate = (leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE || leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) ? lastSettVO
					.getHireenddate() : periodEndDate;
			if (!lastSettVO.getSettlementdate().before(endDate)) { // 结算日期在期间结束之后
				lastSettVO
						.setPeriodbegindate((leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE || leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) ? lastSettVO
								.getHirebegindate() : periodBeginDate);
				//
				// modify 修改按年结算人员计算出错问题 xw 2018-03-22 start
				// lastSettVO.setPeriodenddate(periodEndDate);
				// lastSettVO.setPeriodextendenddate(periodEndDate.getDateAfter(typeVO.getExtendDaysCount()));
				lastSettVO.setPeriodenddate(leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE ? lastSettVO
						.getHireenddate() : periodEndDate);
				lastSettVO.setPeriodextendenddate((leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE ? lastSettVO
						.getHireenddate() : periodEndDate).getDateAfter(typeVO.getExtendDaysCount()));
				// modify 修改按年结算人员计算出错问题 xw 2018-03-22 end

				newVOList.add(lastSettVO);
				continue;
			}
			// 如果结算日期到期间结束日期还存在考勤档案，就new一条，如果不存在就返回最后的结算记录
			psnorgList.add(pk_psnorg);
			scopeList.add(new DefaultDateScope(lastSettVO.getSettlementdate().getDateAfter(1), endDate));
			settVOList.add(lastSettVO); // 存在最新的已结算假期数据则放入List
		}
		// 如果有最新已结算数据
		if (CollectionUtils.isNotEmpty(settVOList)) {
			String[] noDocPsns = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
					.existsTBMPsndocs(pk_org, psnorgList.toArray(new String[0]), scopeList.toArray(new IDateScope[0]));
			for (LeaveBalanceVO settVO : settVOList) {
				String pk_psnorg = settVO.getPk_psnorg();
				if (ArrayUtils.contains(noDocPsns, pk_psnorg)) {// 如果结算日期到期间结束日期没有考勤档案，就返回最后的结算记录
					newVOList.add(settVO);
					continue;
				}
				// 如果存在考勤档案就需要new一条新的假期记录
				needNewMap.put(pk_psnorg, balanceMap.get(pk_psnorg).size() + 1);
			}
		}

		// 处理需要new假期记录的人员
		if (MapUtils.isNotEmpty(needNewMap)) {
			CollectionUtils.addAll(newVOList, createNewAndCalculate(pk_org, typeVO, needNewMap, year, month));
		}
		// 更新时处理冻结时长
		LeaveBalanceVO[] balanceVOs = newVOList.toArray(new LeaveBalanceVO[0]);
		if (ArrayUtils.isEmpty(balanceVOs))
			return null;
		new LeaveBalanceMaintainImpl().calculate(pk_org, typeVO, year, month, balanceVOs, new UFDateTime());
		execHourInfo(balanceVOs, sources);
		Map<String, LeaveBalanceVO> resultMap = new HashMap<String, LeaveBalanceVO>();
		for (LeaveBalanceVO vo : balanceVOs)
			resultMap.put(
					vo.getPk_psnorg() + vo.getPk_timeitem() + vo.getCuryear()
							+ (StringUtils.isEmpty(vo.getCurmonth()) ? "" : vo.getCurmonth()), vo);
		return resultMap;
	}

	/**
	 * 处理结余时长、冻结时长
	 * 
	 * @param balanceVOs
	 * @param sources
	 * @throws BusinessException
	 */
	private void execHourInfo(LeaveBalanceVO[] balanceVOs, Object[] sources) throws BusinessException {
		if (ArrayUtils.isEmpty(balanceVOs) || ArrayUtils.isEmpty(sources))
			return;
		Object simpleVO = sources[0];
		if (!(simpleVO instanceof AggLeaveVO || simpleVO instanceof LeavehVO || simpleVO instanceof LeaveCommonVO))
			return;
		Set<String> keyList = new HashSet<String>();
		for (Object source : sources) {
			String primaryKey = (source instanceof AggLeaveVO) ? ((AggLeaveVO) source).getLeavehVO().getPk_leaveh()
					: ((SuperVO) source).getPrimaryKey();
			if (source instanceof AggLeaveVO)
				primaryKey = ((AggLeaveVO) source).getLeavehVO().getPk_leaveh();
			else if (source instanceof LeavebVO) // 当为子表时，比较特殊，需要查询主表信息
				primaryKey = ((LeavebVO) source).getPk_leaveh();
			else
				primaryKey = ((SuperVO) source).getPrimaryKey();
			if (StringUtils.isEmpty(primaryKey))
				continue;
			keyList.add(primaryKey);
		}
		// 如果没有修改态的，不需要处理
		if (CollectionUtils.isEmpty(keyList))
			return;
		// 是否申请单
		boolean isApp = !(simpleVO instanceof LeaveRegVO);
		// MOD by Connie.ZH 2019-05-20 start
		// compile error cause by generic type
		Object[] dbVOs = null;
		if (isApp) {
			dbVOs = getServiceTemplate().queryByPks(AggLeaveVO.class, keyList.toArray(new String[0]));
		} else {
			dbVOs = getServiceTemplate().queryByPks(LeaveRegVO.class, keyList.toArray(new String[0]));
		}
		// 2019-05-20 end
		if (ArrayUtils.isEmpty(dbVOs))
			return;
		for (LeaveBalanceVO vo : balanceVOs) {
			for (Object dbVO : dbVOs) {
				if (isApp) {
					LeavehVO hVO = ((AggLeaveVO) dbVO).getLeavehVO();
					if (!vo.getPk_psnorg().equals(hVO.getPk_psnorg())
							|| !vo.getLeaveindex().equals(hVO.getLeaveindex())
							|| !vo.getPk_timeitem().equals(hVO.getPk_timeitem())
							|| !vo.getCuryear().equals(hVO.getLeaveyear())
							|| (!vo.isYearDateSetPeriod() && !vo.getCurmonth().equals(hVO.getLeavemonth())))
						continue;
					// 应该不需要再重新计算一次
					// BillProcessHelperAtServer.calLeaveLength(aggVO);
					// 从冻结中扣除本申请单时长
					vo.setFreezedayorhour(vo.getFreezedayorhour() == null ? new UFDouble(0 - hVO.getSumhour()
							.doubleValue()) : vo.getFreezedayorhour().sub(hVO.getSumhour()));
					continue;
				}
				LeaveRegVO regVO = (LeaveRegVO) dbVO;
				if (!vo.getPk_psnorg().equals(regVO.getPk_psnorg())
						|| !vo.getLeaveindex().equals(regVO.getLeaveindex())
						|| !vo.getPk_timeitem().equals(regVO.getPk_timeitem())
						|| !vo.getCuryear().equals(regVO.getLeaveyear())
						|| (!vo.isYearDateSetPeriod() && !vo.getCurmonth().equals(regVO.getLeavemonth())))
					continue;
				// BillProcessHelperAtServer.calLeaveLength(regVO);
				// 从已休中扣除，结余中加上本登记单的时长
				vo.setYidayorhour(vo.getYidayorhour() == null ? new UFDouble(0 - regVO.getLeaveHourValue()) : vo
						.getYidayorhour().sub(regVO.getLeavehour()));
				vo.setRestdayorhour(vo.getRestdayorhour() == null ? regVO.getLeavehour() : vo.getRestdayorhour().add(
						regVO.getLeavehour()));
			}
		}
	}

	/**
	 * 创建新的假期记录并计算
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param psnorgMap
	 *            Map<pk_psnorg, leaveIndex>
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private LeaveBalanceVO[] createNewAndCalculate(String pk_org, LeaveTypeCopyVO typeVO,
			Map<String, Integer> psnorgMap, String year, String month) throws BusinessException {
		if (MapUtils.isEmpty(psnorgMap))
			return null;
		String[] pk_psnorgs = psnorgMap.keySet().toArray(new String[0]);
		PsnOrgVO[] psnOrgVOs = CommonUtils.toArray(PsnOrgVO.class, (Collection<PsnOrgVO>) MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByPKs(PsnOrgVO.class, pk_psnorgs, false));
		Map<String, PsnOrgVO> psnOrgVOMap = CommonUtils.toMap(PsnOrgVO.PK_PSNORG, psnOrgVOs);
		String pk_group = PubEnv.getPk_group();
		// ssx added on 2018-03-16
		// for changes of start date of company age
		boolean isHireSett = LeaveTypeCopyVO.LEAVESETPERIOD_DATE == typeVO.getLeavesetperiod().intValue()
				|| LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE == typeVO.getLeavesetperiod().intValue();
		LeaveBalanceMaintainImpl impl = new LeaveBalanceMaintainImpl();
		List<LeaveBalanceVO> results = new ArrayList<LeaveBalanceVO>();
		for (String pk_psnorg : pk_psnorgs) {
			PsnOrgVO psnorgVO = psnOrgVOMap.get(pk_psnorg);
			LeaveBalanceVO retVO = new LeaveBalanceVO();
			results.add(retVO);
			retVO.setPk_group(pk_group);
			retVO.setPk_org(pk_org);
			retVO.setPk_psndoc(psnorgVO.getPk_psndoc());
			retVO.setPk_psnorg(pk_psnorg);
			retVO.setPk_timeitem(typeVO.getPk_timeitem());
			retVO.setLeavesetperiod(typeVO.getLeavesetperiod());
			retVO.setCuryear(year);
			retVO.setCurmonth(month);
			retVO.setLeaveindex(psnorgMap.get(pk_psnorg));
			if (!isHireSett) // 如果不是按入职日期结算
				continue;
			// 按入职日期结算时需要处理开始日期和结束日期
			UFLiteralDate hireBeginDate = null;
			UFLiteralDate hireEndDate = null;
			if (LeaveTypeCopyVO.LEAVESETPERIOD_DATE == typeVO.getLeavesetperiod().intValue()) {
				hireBeginDate = impl.getHireBeginDate(year, impl.getHireDate(psnorgVO));
				hireEndDate = impl.getHireEndDate(year, impl.getHireDate(psnorgVO));
			} else if (LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE == typeVO.getLeavesetperiod().intValue()) {
				hireBeginDate = impl.getHireBeginDate(year, impl.getHireStartDate(psnorgVO));
				hireEndDate = impl.getHireEndDate(year, impl.getHireStartDate(psnorgVO));
			}
			//
			retVO.setHirebegindate(hireBeginDate);
			retVO.setPeriodbegindate(hireBeginDate);
			retVO.setHireenddate(hireEndDate);
			retVO.setPeriodenddate(hireEndDate);
			retVO.setPeriodextendenddate(hireEndDate.getDateAfter(typeVO.getExtendDaysCount()));
		}
		return results.toArray(new LeaveBalanceVO[0]);
	}

	@Override
	public String calAndSettlement_RequiresNew(String pk_org, boolean autoSettlement, boolean settlementtocurr)
			throws BusinessException {
		HROrgVO hrorgvo = ((HROrgVO) NCLocator.getInstance().lookup(IOrgInfoQueryService.class).queryByPk(pk_org)
				.getParentVO());
		String orgname = MultiLangUtil.getSuperVONameOfCurrentLang(hrorgvo, "name", null);
		StringBuffer sb = new StringBuffer();
		// 首先查询休假类别
		ITimeItemQueryService timeitemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO[] typeVOs = (LeaveTypeCopyVO[]) timeitemService.queryLeaveCopyTypesByOrg(pk_org);
		if (ArrayUtils.isEmpty(typeVOs))
			return sb.toString();
		// 查询当前时间对应的组织日期
		// 计算日期，需要将计算时间与HR组织时区进行比对
		TimeRuleVO timeRuleVO = null;
		try {
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return e.getMessage() + "<br>";
		}
		TimeZone timeZone = timeRuleVO.getTimeZone();
		UFDateTime calculateTime = new UFDateTime();
		UFLiteralDate calDate = UFLiteralDate.getDate(calculateTime.toStdString(timeZone).substring(0, 10));
		int intYear = calDate.getYear();
		String year = Integer.toString(intYear);// 当前自然年度
		String preYear = Integer.toString(intYear - 1);// 上一自然年度(对于按入职日结算的场景，需要处理上一年度和当前年度)
		// 取出此日期对应的考勤期间
		PeriodVO periodVO = PeriodServiceFacade.queryByDate(pk_org, calDate);
		LeaveBalanceMaintainImpl maintainImpl = new LeaveBalanceMaintainImpl();
		// 可以在calDate结算的类别及其结算的年度、期间
		Map<String, String[]> canSettlementTypeAndYearMonthMap = querySettlementYearMonth(typeVOs, calDate);
		for (LeaveTypeCopyVO typeVO : typeVOs) {
			String typename = typeVO.getMultilangName();
			if (typeVO.getEnablestate().intValue() == IPubEnumConst.ENABLESTATE_DISABLE || typeVO.isLactation())// 如果类别已经停用，或者是哺乳假，则不处理
				continue;
			String salaryYear = null;
			String salaryMonth = null;
			int setPeriod = typeVO.getLeavesetperiod();
			// ssx added on 2018-03-16
			// for changes of start date of company age
			if (setPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE || setPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE) {// 如果是按入职日结算
				//
				LeaveBalanceVO[] balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, preYear, null, null);
				if (!ArrayUtils.isEmpty(balanceVOs)) {
					balanceVOs = maintainImpl.calculate(pk_org, typeVO, preYear, null, balanceVOs, calculateTime, true);
					if (autoSettlement) {
						if (TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typeVO.getLeavesettlement()) {// 转薪资的需要处理余额结算到哪个期间以方便薪资取数
							if (settlementtocurr) {// 结算到当前
								salaryYear = periodVO.getYear();
								salaryMonth = periodVO.getMonth();
							} else {//
								PeriodVO[] periodvos = PeriodServiceFacade.queryByYear(pk_org, preYear);
								PeriodVO dataPeriodVO = periodvos[periodvos.length - 1];
								salaryYear = dataPeriodVO.getYear();
								salaryMonth = dataPeriodVO.getMonth();
							}
						}
						maintainImpl.secondSettlement4HireDate(pk_org, typeVO, preYear, balanceVOs, calDate, false,
								false, false, true, salaryYear, salaryMonth);
					}
				}
				balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, year, null, null);
				if (ArrayUtils.isEmpty(balanceVOs))
					continue;
				balanceVOs = maintainImpl.calculate(pk_org, typeVO, year, null, balanceVOs, calculateTime, true);
				// 对于入职日结算的类型，当年不可能有可以结算的
				continue;
			}
			if (periodVO == null)
				continue;
			String month = setPeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH ? periodVO.getTimemonth() : null;
			LeaveBalanceVO[] balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, periodVO.getTimeyear(), month,
					null);
			if (ArrayUtils.isEmpty(balanceVOs)) {
				sb.append((ResHelper.getString("6017basedoc", "06017basedoc1851"/*
																				 * @
																				 * ress
																				 * "{0}下的{1}类别没有能符合计算结算条件的人员!"
																				 */, orgname, typename)) + "<br>");
				continue;
			}
			balanceVOs = maintainImpl.calculate(pk_org, typeVO, periodVO.getTimeyear(), month, balanceVOs,
					calculateTime, true);
			if (!autoSettlement)
				continue;
			// 看此类别是否可以在calDate进行结算
			String[] settlementYearMonth = canSettlementTypeAndYearMonthMap.get(typeVO.getPk_timeitem());
			if (ArrayUtils.isEmpty(settlementYearMonth)) {
				sb.append((ResHelper.getString("6017basedoc", "06017basedoc1852"/*
																				 * @
																				 * ress
																				 * "{0}下的{1}类别的结算日期不符合结算条件不予结算!"
																				 */, orgname, typename)) + "<br>");
				continue;
			}
			// 如果有某个年度/期间的可以在calDate进行结算了，则结算之
			String settlementYear = settlementYearMonth[0];
			String settlementMonth = settlementYearMonth.length > 1 ? settlementYearMonth[1] : null;
			balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, settlementYear, settlementMonth, null);

			if (TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typeVO.getLeavesettlement()) {// 转薪资的需要处理余额结算到哪个期间以方便薪资取数
				if (settlementtocurr) {// 结算到当前
					salaryYear = periodVO.getYear();
					salaryMonth = periodVO.getMonth();
				} else {
					salaryYear = settlementYear;
					salaryMonth = settlementMonth;
				}
			}

			maintainImpl.secondSettlement4YearMonth(pk_org, typeVO, settlementYear, settlementMonth, balanceVOs,
					calDate, false, false, false, true, salaryYear, salaryMonth);
		}
		return sb.toString();
	}

	/**
	 * 查询可以在date进行结算的所有非入职日类别，key是pk_timeitem,value是可以进行结算的年度/期间
	 * 
	 * @param typeVOs
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, String[]> querySettlementYearMonth(LeaveTypeCopyVO[] typeVOs, UFLiteralDate date)
			throws BusinessException {
		Map<String, String[]> retMap = new HashMap<String, String[]>();
		if (ArrayUtils.isEmpty(typeVOs))
			return retMap;
		for (LeaveTypeCopyVO typeVO : typeVOs) {
			if (typeVO.isLactation())
				continue;
			int setPeriod = typeVO.getLeavesetperiod();
			if (setPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE// 如果是按入职日结算则不用处理，因为入职日结算的类型，每天都有可能有人结算
					// ssx added on 2018-03-16
					// for changes of start date of company age
					|| setPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE)
				//
				continue;
			int effectiveExtendCount = typeVO.getExtendDaysCount();// 有效期延长天数
			// 可以在date日进行结算的年度/期间的最后一天
			UFLiteralDate periodEndDate = date.getDateBefore(effectiveExtendCount + 1);
			PeriodVO periodVO = PeriodServiceFacade.queryByDate(typeVO.getPk_org(), periodEndDate);
			if (periodVO == null)
				continue;
			if (setPeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {
				if (periodVO.getEnddate().equals(periodEndDate)) {// 如果是按期间结算，且此期间的最后一天刚好等于periodEndDate，则表示可以在这一天结算
					retMap.put(typeVO.getPk_timeitem(),
							new String[] { periodVO.getTimeyear(), periodVO.getTimemonth() });
				}
				continue;
			}
			PeriodVO[] yearPeriodVOs = PeriodServiceFacade.queryByYear(typeVO.getPk_org(), periodVO.getTimeyear());
			if (yearPeriodVOs[yearPeriodVOs.length - 1].getEnddate().equals(periodEndDate)) {// 如果是按年结算，且此年的最后一天刚好等于periodEndDate，则表示可以在这一天结算
				retMap.put(typeVO.getPk_timeitem(), new String[] { periodVO.getTimeyear() });
			}
		}
		return retMap;
	}

	@Override
	public void processBeforeRestOvertime(String pk_org, Map<String, UFDouble> hourMap, String year, String month,
			Boolean isToRest) throws BusinessException {
		if (MapUtils.isEmpty(hourMap))
			return;
		String outErrMsg = StringUtils.isEmpty(month) ? ResHelper.getString("6017leave", "06017leave0257")/*
																											* @
																											* res
																											* "转入年度超出考勤档案时间范围！"
																											*/
		: ResHelper.getString("6017leave", "06017leave0235")/*
															* @res
															* "转入期间超出考勤档案时间范围！"
															*/;
		String[] pk_psnorgs = hourMap.keySet().toArray(new String[0]);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) NCLocator.getInstance().lookup(ITimeItemQueryService.class)
				.queryCopyTypesByDefPK(pk_org, LeaveBalanceVO.TIMETOLEAVETYPE, TimeItemCopyVO.LEAVE_TYPE);
		Map<String, LeaveBalanceVO> balanceMap = queryAndCalLeaveBalanceVO(pk_org, typeVO, pk_psnorgs, year, month,
				null);
		if (MapUtils.isEmpty(balanceMap))
			throw new BusinessException(outErrMsg);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		boolean isHourType = TimeItemCopyVO.TIMEITEMUNIT_HOUR == typeVO.getTimeitemunit().intValue();
		List<LeaveBalanceVO> resultList = new ArrayList<LeaveBalanceVO>();
		for (String pk_psnorg : pk_psnorgs) {
			UFDouble torestHour = hourMap.get(pk_psnorg);
			// 时长为0不处理
			if (torestHour == null || torestHour.equals(UFDouble.ZERO_DBL))
				continue;
			torestHour = isHourType ? torestHour : torestHour.div(timeRule.getDaytohour2());
			String key = pk_psnorg + typeVO.getPk_timeitem() + year + (StringUtils.isEmpty(month) ? "" : month);
			LeaveBalanceVO vo = balanceMap.get(key);
			if (vo == null)
				throw new BusinessException(outErrMsg);
			if (vo.isSettlement() || vo.isUse())
				throw new BusinessException(ResHelper.getString("6017leave", "06017leave0236")/*
																								* @
																								* res
																								* "期间的结余数据已结算或已被薪酬使用,不能操作！"
																								*/);
			// 加入到结果集中
			resultList.add(vo);
			// 转调休
			if (isToRest) {
				vo.setCurdayorhour(vo.getCurdayorhour().add(torestHour));
				vo.setRealdayorhour(vo.getRealdayorhour().add(torestHour));
				vo.setRestdayorhour(vo.getRestdayorhour().add(torestHour));
				continue;
			}
			// 反转调休
			if (vo.getRestdayorhour().compareTo(torestHour) < 0)
				throw new BusinessException(ResHelper.getString("6017leave", "06017leave0237")/*
																								* @
																								* res
																								* "转调休期间的结余时长小于反转调休时长！"
																								*/);
			vo.setCurdayorhour(vo.getCurdayorhour().sub(torestHour));
			vo.setRealdayorhour(vo.getRealdayorhour().sub(torestHour));
			vo.setRestdayorhour(vo.getRestdayorhour().sub(torestHour));
		}

		// 更新数据
		getServiceTemplate().batchUpdate(resultList.toArray(new LeaveBalanceVO[0]), true);
	}

	@Override
	public Map<String, List<LeaveBalanceVO>> queryAndCalLeaveBalanceVOBatchForPreHoliday(String pk_org,
			Map<String, LeaveTypeCopyVO[]> dependMap, Map<String, LeaveTypeCopyVO> leaveTypeVOMap,
			TimeRuleVO timeRuleVO, LeaveCommonVO... leaveCommonVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(leaveCommonVOs))
			return null;
		PeriodVO[] periodVOs = CommonUtils.retrieveByClause(PeriodVO.class, " pk_org = '" + timeRuleVO.getPk_org()
				+ "' ");
		// 将休假单按休假类别、年度、期间分组
		Map<String, List<LeaveCommonVO>> keyMap = new HashMap<String, List<LeaveCommonVO>>();
		for (LeaveCommonVO vo : leaveCommonVOs) {
			String primaryKey = vo.getPk_leavetype() + vo.getYearmonth();
			List<LeaveCommonVO> voList = keyMap.get(primaryKey);
			if (CollectionUtils.isEmpty(voList)) {
				voList = new ArrayList<LeaveCommonVO>();
				keyMap.put(primaryKey, voList);
			}
			voList.add(vo);
		}
		// 将分组后的数据批量处理
		Map<String, List<LeaveBalanceVO>> result = new HashMap<String, List<LeaveBalanceVO>>();
		for (String key : keyMap.keySet()) {
			LeaveCommonVO[] vos = keyMap.get(key).toArray(new LeaveCommonVO[0]);
			LeaveTypeCopyVO[] dependVOs = dependMap.get(vos[0].getPk_leavetype());// 用户录入类别的所有前置类别
			LeaveTypeCopyVO typeCopyVO = leaveTypeVOMap.get(vos[0].getPk_leavetype());
			dependVOs = (LeaveTypeCopyVO[]) ArrayUtils.add(dependVOs, typeCopyVO);// 将前置类别和用户录入类别合并到一个数组，方便后面的使用
			int setPeriod = typeCopyVO.getLeavesetperiod().intValue();// 结算周期类型：年/期间/入职日
			Map<String, List<LeaveBalanceVO>> balanceMap = null;
			// 如果是按年/入职日结算，那么比较简单，因为其前置假的结算周期也肯定是年/入职日
			if (setPeriod == TimeItemCopyVO.LEAVESETPERIOD_YEAR || setPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE
			// ssx added on 2018-03-16
			// for changes of start date of company age
					|| setPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE
			//
			)
				balanceMap = queryAndCalLeaveBalanceVOForPreHolidayYearDate(timeRuleVO, vos[0].getLeaveyear(),
						periodVOs, typeCopyVO, dependVOs, vos);
			// 走到这里，表示用户录入的休假类别是按期间结算的，而按期间结算的类别，前置假有可能是按年的，也有可能是按期间的（规定：如有按年的，则必须在按期间的之前）
			else
				balanceMap = queryAndCalLeaveBalanceVOForPreHolidayPeriod(timeRuleVO, vos[0].getLeaveyear(),
						vos[0].getLeavemonth(), periodVOs, typeCopyVO, dependVOs, vos);
			if (MapUtils.isEmpty(balanceMap))
				continue;
			// 将查询结果合并到返回结果中
			for (String pk_psnorg : balanceMap.keySet()) {
				List<LeaveBalanceVO> balanceList = result.get(pk_psnorg);
				if (balanceList == null) {
					balanceList = balanceMap.get(pk_psnorg);
					result.put(pk_psnorg, balanceList);
					continue;
				}
				// balanceList.addAll(balanceMap.get(pk_psnorg));//报错UnsupportedOperationException,改用下面的方案
				List<LeaveBalanceVO> list = balanceMap.get(pk_psnorg);
				if (CollectionUtils.isEmpty(list))
					continue;
				List<LeaveBalanceVO> uniteList = new ArrayList<LeaveBalanceVO>();
				for (LeaveBalanceVO vo : list) {
					uniteList.add(vo);
				}
				for (LeaveBalanceVO vo : balanceList) {
					uniteList.add(vo);
				}
				result.put(pk_psnorg, uniteList);
			}
		}
		return result;
	}

	/**
	 * 查询所需要的所有前置假+用户填写的类别的结算信息。适用于用户填写的类别是按年/入职日结算
	 * 
	 * @param timeRuleVO
	 * @param year
	 * @param typeCopyVO
	 * @param dependVOsAddSelf
	 * @param leaveCommonVOs
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, List<LeaveBalanceVO>> queryAndCalLeaveBalanceVOForPreHolidayYearDate(TimeRuleVO timeRuleVO,
			String year, PeriodVO[] periodVOs, LeaveTypeCopyVO typeCopyVO, LeaveTypeCopyVO[] dependVOsAddSelf,
			LeaveCommonVO[] leaveCommonVOs) throws BusinessException {
		PsnOrgVO[] psnOrgVOs = CommonUtils.toArray(
				PsnOrgVO.class,
				(Collection<PsnOrgVO>) MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(
						PsnOrgVO.class, StringPiecer.getStrArrayDistinct(leaveCommonVOs, LeaveCommonVO.PK_PSNORG),
						false));
		Map<String, PsnOrgVO> psnOrgVOMap = CommonUtils.toMap(PsnOrgVO.PK_PSNORG, psnOrgVOs);
		Map<String, LeaveTypeCopyVO> typeMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, dependVOsAddSelf);
		UFLiteralDate hireDate = null;// 入职日
		// 构造Map<pk_timeitem, Map<year, LeaveCommonVO[]>>
		Map<String, Map<String, List<LeaveCommonVO>>> voMap = new HashMap<String, Map<String, List<LeaveCommonVO>>>();
		for (LeaveCommonVO leaveCommonVO : leaveCommonVOs) {
			// ssx added on 2018-03-16
			// for changes of start date of company age
			if (typeCopyVO.getLeavesetperiod() == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				hireDate = new LeaveBalanceMaintainImpl()
						.getHireStartDate(psnOrgVOMap.get(leaveCommonVO.getPk_psnorg()));
			} else {
				// 设置入职日期（不太影响效率，所以每个都取一下，不用也无妨）
				hireDate = new LeaveBalanceMaintainImpl().getHireDate(psnOrgVOMap.get(leaveCommonVO.getPk_psnorg()));
			}
			//
			// 存储有效期延长天数与相交的年度数组的map，<有效期延长天数，与休假时段有交集的考勤年度/入职年>
			for (LeaveTypeCopyVO typeVO : dependVOsAddSelf) {
				String[] crossYears = null;// 与休假日期范围有交集的所有考勤/入职年度，需要考虑有效期延长
				// 休假类别为按年结算
				if (typeVO.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_YEAR) // typeCopyVO.getLeavesetperiod()==TimeItemCopyVO.LEAVESETPERIOD_YEAR
					crossYears = PreHolidayLeaveBalanceUtils.queryRelatedYearsWithExtendCount(
							typeVO.getExtendDaysCount(), periodVOs, leaveCommonVO.getLeavebegindate(),
							leaveCommonVO.getLeaveenddate());
				else
					crossYears = PreHolidayLeaveBalanceUtils.queryRelatedHireYearsWithExtendCount(
							typeVO.getExtendDaysCount(), leaveCommonVO.getPk_psndoc(), hireDate,
							leaveCommonVO.getLeavebegindate(), leaveCommonVO.getLeaveenddate());
				if (ArrayUtils.isEmpty(crossYears))
					continue;
				Map<String, List<LeaveCommonVO>> yearMap = voMap.get(typeVO.getPk_timeitem());
				if (MapUtils.isEmpty(yearMap)) {
					yearMap = new HashMap<String, List<LeaveCommonVO>>();
					voMap.put(typeVO.getPk_timeitem(), yearMap);
				}
				for (String curYear : crossYears) {
					List<LeaveCommonVO> voList = yearMap.get(curYear);
					if (CollectionUtils.isEmpty(voList)) {
						voList = new ArrayList<LeaveCommonVO>();
						yearMap.put(curYear, voList);
					}
					voList.add(leaveCommonVO);
				}
			}
		}
		if (MapUtils.isEmpty(voMap))
			return null;
		// 按类别和年度批量处理
		Map<String, List<LeaveBalanceVO>> resultMap = new HashMap<String, List<LeaveBalanceVO>>();
		for (String pk_timeitem : voMap.keySet()) {
			Map<String, List<LeaveCommonVO>> yearMap = voMap.get(pk_timeitem);
			if (MapUtils.isEmpty(yearMap))
				continue;
			for (String curYear : yearMap.keySet()) {
				LeaveCommonVO[] commonVOs = yearMap.get(curYear).toArray(new LeaveCommonVO[0]);
				Map<String, LeaveBalanceVO> balanceMap = queryAndCalLeaveBalanceVO(typeCopyVO.getPk_org(),
						typeMap.get(pk_timeitem), StringPiecer.getStrArrayDistinct(commonVOs, LeaveCommonVO.PK_PSNORG),
						curYear, null, commonVOs);
				if (MapUtils.isEmpty(balanceMap))
					continue;
				for (String key : balanceMap.keySet()) {
					LeaveBalanceVO vo = balanceMap.get(key);
					String pk_psnorg = vo.getPk_psnorg();
					List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
					if (CollectionUtils.isEmpty(balanceList)) {
						balanceList = new ArrayList<LeaveBalanceVO>();
						resultMap.put(pk_psnorg, balanceList);
					}
					balanceList.add(vo);
				}
			}
		}
		if (MapUtils.isEmpty(resultMap))
			return null;
		// 下面要对这些LeaveBalanceVO[]进行排序，
		// 排序的规则是：若往期结余优先为Y，则order by 年度，类别。
		// 若往期结余优先为N，但用户未输入年度，则与上面的排序一致
		// 若用户输入了年度，则需要将用户输入的年度、输入的类别的leavebalancevo记录往前提，提到第一个输入的类别的leavebalance记录之前
		for (String pk_psnorg : resultMap.keySet()) {
			List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
			if (CollectionUtils.isEmpty(balanceList))
				continue;
			LeaveBalanceVO[] retArray = balanceList.toArray(new LeaveBalanceVO[0]);
			PreHolidayLeaveBalanceUtils.sortByYearDate(retArray, timeRuleVO.isPreHolidayFirst(),
					typeCopyVO.getPk_timeitem(), year, dependVOsAddSelf);
			resultMap.put(pk_psnorg, Arrays.asList(retArray));
		}
		return resultMap;
	}

	/**
	 * 查询所需要的所有前置假+用户填写的类别的结算信息。适用于用户填写的类别是按期间结算
	 * 按期间结算的类别，其前置假有可能是按年结算的，但必须在前置假的前面，不能在按期间结算的前置假后面
	 * 
	 * @param timeRuleVO
	 * @param year
	 * @param month
	 * @param typeCopyVO
	 * @param dependVOsAddSelf
	 * @param leaveCommonVOs
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, List<LeaveBalanceVO>> queryAndCalLeaveBalanceVOForPreHolidayPeriod(TimeRuleVO timeRuleVO,
			String year, String month, PeriodVO[] periodVOs, LeaveTypeCopyVO typeCopyVO,
			LeaveTypeCopyVO[] dependVOsAddSelf, LeaveCommonVO[] leaveCommonVOs) throws BusinessException {
		Map<String, LeaveTypeCopyVO> typeMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, dependVOsAddSelf);
		// 构造Map<pk_timeitem, Map<year-month, LeaveCommonVO[]>>
		Map<String, Map<String, List<LeaveCommonVO>>> voMap = new HashMap<String, Map<String, List<LeaveCommonVO>>>();
		for (LeaveCommonVO leaveCommonVO : leaveCommonVOs) {
			for (LeaveTypeCopyVO typeVO : dependVOsAddSelf) {
				if (typeVO.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_YEAR) {
					String[] crossYears = PreHolidayLeaveBalanceUtils.queryRelatedYearsWithExtendCount(
							typeVO.getExtendDaysCount(), periodVOs, leaveCommonVO.getLeavebegindate(),
							leaveCommonVO.getLeaveenddate());
					if (ArrayUtils.isEmpty(crossYears))
						continue;
					Map<String, List<LeaveCommonVO>> yearMap = voMap.get(typeVO.getPk_timeitem());
					if (MapUtils.isEmpty(yearMap)) {
						yearMap = new HashMap<String, List<LeaveCommonVO>>();
						voMap.put(typeVO.getPk_timeitem(), yearMap);
					}
					for (String curYear : crossYears) {
						List<LeaveCommonVO> voList = yearMap.get(curYear);
						if (CollectionUtils.isEmpty(voList)) {
							voList = new ArrayList<LeaveCommonVO>();
							yearMap.put(curYear, voList);
						}
						voList.add(leaveCommonVO);
					}
					continue;
				}
				PeriodVO[] crossPeriodVOs = PreHolidayLeaveBalanceUtils.queryRelatedPeriodsWithExtendCount(
						typeVO.getExtendDaysCount(), periodVOs, leaveCommonVO.getLeavebegindate(),
						leaveCommonVO.getLeaveenddate());
				if (ArrayUtils.isEmpty(crossPeriodVOs))
					continue;
				Map<String, List<LeaveCommonVO>> yearMap = voMap.get(typeVO.getPk_timeitem());
				if (MapUtils.isEmpty(yearMap)) {
					yearMap = new HashMap<String, List<LeaveCommonVO>>();
					voMap.put(typeVO.getPk_timeitem(), yearMap);
				}
				for (PeriodVO curPeriod : crossPeriodVOs) {
					String key = curPeriod.getTimeyear() + "-" + curPeriod.getTimemonth();
					List<LeaveCommonVO> voList = yearMap.get(key);
					if (CollectionUtils.isEmpty(voList)) {
						voList = new ArrayList<LeaveCommonVO>();
						yearMap.put(key, voList);
					}
					voList.add(leaveCommonVO);
				}
			}
		}
		if (MapUtils.isEmpty(voMap))
			return null;
		// 按类别和年度批量处理
		Map<String, List<LeaveBalanceVO>> resultMap = new HashMap<String, List<LeaveBalanceVO>>();
		for (String pk_timeitem : voMap.keySet()) {
			Map<String, List<LeaveCommonVO>> yearMap = voMap.get(pk_timeitem);
			if (MapUtils.isEmpty(yearMap))
				continue;
			LeaveTypeCopyVO curType = typeMap.get(pk_timeitem);
			for (String curYear : yearMap.keySet()) {
				LeaveCommonVO[] commonVOs = yearMap.get(curYear).toArray(new LeaveCommonVO[0]);
				String thisYear = null;
				String thisMonth = null;
				if (curType.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_YEAR)
					thisYear = curYear;
				else {
					String[] arr = curYear.split("-");
					thisYear = arr[0];
					thisMonth = arr[1];
				}
				Map<String, LeaveBalanceVO> balanceMap = queryAndCalLeaveBalanceVO(typeCopyVO.getPk_org(),
						typeMap.get(pk_timeitem), StringPiecer.getStrArrayDistinct(commonVOs, LeaveCommonVO.PK_PSNORG),
						thisYear, thisMonth, commonVOs);
				if (MapUtils.isEmpty(balanceMap))
					continue;
				for (String key : balanceMap.keySet()) {
					LeaveBalanceVO vo = balanceMap.get(key);
					String pk_psnorg = vo.getPk_psnorg();
					List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
					if (CollectionUtils.isEmpty(balanceList)) {
						balanceList = new ArrayList<LeaveBalanceVO>();
						resultMap.put(pk_psnorg, balanceList);
					}
					balanceList.add(vo);
				}
			}
		}
		if (MapUtils.isEmpty(resultMap))
			return null;
		// 下面要对这些LeaveBalanceVO[]进行排序，
		// 排序的规则是：若往期结余优先为Y，则order by 年度，类别。
		// 若往期结余优先为N，但用户未输入年度，则与上面的排序一致
		// 若用户输入了年度，则需要将用户输入的年度、输入的类别的leavebalancevo记录往前提，提到第一个输入的类别的leavebalance记录之前
		for (String pk_psnorg : resultMap.keySet()) {
			List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
			if (CollectionUtils.isEmpty(balanceList))
				continue;
			LeaveBalanceVO[] retArray = balanceList.toArray(new LeaveBalanceVO[0]);
			PreHolidayLeaveBalanceUtils.sortByPeriod(retArray, timeRuleVO.isPreHolidayFirst(),
					typeCopyVO.getPk_timeitem(), year, month, dependVOsAddSelf);
			resultMap.put(pk_psnorg, Arrays.asList(retArray));
		}
		return resultMap;
	}
}