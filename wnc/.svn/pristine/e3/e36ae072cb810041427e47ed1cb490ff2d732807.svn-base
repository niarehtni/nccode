package nc.impl.ta.leave;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.algorithm.CalParam4OnePerson;
import nc.impl.ta.leave.SplitLeaveBillHelper.MergeResultDescriptor;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DateTimeUtils;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.CollectionUtils;

/**
 * 实现拆单算法的类
 * 所谓拆单，就是根据前置假规则以及往期结余假优先参数，将用户填写的申请单/登记单转换为正确的休假类别和期间（
 * 有可能拆单的结果与用户填写的完全一致，例如填写了2011年度的事假，拆单的结果也是2011年度的事假，这种情况下
 * 就是未拆单）
 * 对于单个新增申请单或登记单的情况，若发生了拆单（即最后拆单的结果与用户的潜意识不一致）时，需要用
 * 对话框提示用户，将拆单结果显示在对话框中。
 * 而对于批量新增，直接将拆单结果显示在最后一步的对话框中（此步原来就有，将用户填写的批量信息生成具体的休假记录）
 * 那么，问题是，在单个新增时，什么情况下，才需要用对话框提示用户？
 * 分两种情况讨论。
 * “往期结余假优先”参数为N的时候，用户在界面上需要填写休假类别和具体的期间，那么只要拆单结果与填写的类别、期间
 * 不一致，就可以认为有拆单行为发生。即，如果拆单结果出现了另外的休假类别，或者出现了另外的期间，则可认为是
 * 拆单了。另外，“跨年休假是否允许保存”，“申请日期不超过开始日期__天”的参数也有可能会导致拆单，这种情况下，也需要提示用户
 * “往期结余假优先”参数为Y的时候，用户在界面上只需要填写休假类别，不需要填写期间，即，此时用户不需要关心
 * 期间，系统会根据期间优先算法计算出合适的期间。这种情况下，如果拆单结果中出现了另外的休假类别，或者【没有
 * 出现其他的类别，但本类别被拆成了多个期间】，则可以认为存在拆单了。
 * 例如，2011.1.3日填写了一个事假单，有可能
 * 拆单的结果是：
 * 2010年病假，视为拆单了，需提示用户
 * 2011年病假，2011年事假，视为拆单了，需提示用户
 * 2010年事假，未拆单，不提示用户，生成的2010年度自动带到界面上
 * 2011年事假，未拆单，不提示用户，生成的2011年度自动带到界面上
 * 2010年事假，2011年事假，视为拆单了，需提示用户，此时休假类别虽然没变，但用户的潜意识是自己只填了一张休假单，生成了
 * 两张单子，当然要提示用户
 * 用简练的语言来描述，就是
 * 对于“往期结余假优先”参数为N的情况，只有拆单结果为本类别本期间的时候，才不用提示用户，其他情况都要提示用户
 * 对于“往期结余假优先”参数为Y的情况，只有拆单结果为本类别单期间的时候，才不用提示用户，其他情况都要提示用户
 * @author zengcheng
 *
 */
public class SplitLeaveBillUtils {


	/**
	 * @param aggVO
	 * @param timeRuleVO
	 * @param leaveTypeVOMap 休假类别的主键与其copyvo的对应map，<pk_timeitem,LeaveTypeCopyVO>
	 * @param dependTypeVOMap，休假类别主键与其依赖的copyvo数组的对应，<pk_timeitem,LeaveTypeCopyVO[]>
	 * @return 相同休假类别，相同期间的合并为一张申请单
	 * @throws BusinessException
	 */
	static SplitBillResult<AggLeaveVO> split(
			AggLeaveVO aggVO,
			TimeRuleVO timeRuleVO,
			Map<String, LeaveTypeCopyVO> leaveTypeVOMap,
			Map<String, LeaveTypeCopyVO[]> dependMap) throws BusinessException{
		LeavehVO leavehVO = aggVO.getLeavehVO();
		SplitBillResult<AggLeaveVO> splitResult = new SplitBillResult<AggLeaveVO>();
		splitResult.setOriginalBill(aggVO);
		if(leavehVO.getIslactation()!=null&&leavehVO.getIslactation().booleanValue()){//哺乳假不执行拆单
			splitResult.setSplit(false);
			splitResult.setSplitResult(new AggLeaveVO[]{aggVO});
			return splitResult;
		}
		String pk_leavetype = leavehVO.getPk_leavetype();
		String pk_org = leavehVO.getPk_org();
		String pk_psnorg = leavehVO.getPk_psnorg();
		LeavebVO[] bvos = aggVO.getLeavebVOs();
		LeavebVO[] toSplitVOs = SplitLeaveBillHelper.filterNewAndUpdate(bvos);
		for(LeavebVO bvo:toSplitVOs){
			bvo.setPk_org(leavehVO.getPk_org());
			bvo.setPk_psndoc(leavehVO.getPk_psndoc());
			bvo.setPk_psnorg(leavehVO.getPk_psnorg());
			bvo.setPk_psnjob(leavehVO.getPk_psnjob());
			bvo.setLeaveyear(leavehVO.getLeaveyear());
			bvo.setLeavemonth(leavehVO.getLeavemonth());
		}
		//如果有跨业务单元的休假，则切开
		LeavebVO[] orgSplitVOs = BillMethods.compareAndCutDate(pk_org, toSplitVOs);
		//如果切割的结果为空，表示整个单据与考勤档案没有任何交集，这种情况下应该抛出异常
		if(ArrayUtils.isEmpty(orgSplitVOs)){
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0247")
					/*@res "休假时间范围不在考勤档案范围内!"*/);
		}
		TimeScopeUtils.sort(orgSplitVOs);
		TimeScopeUtils.sort(toSplitVOs);
		TemporarySplitResult<LeavebVO> tempResult = new TemporarySplitResult<LeavebVO>();
		tempResult.setToSplitVOs(orgSplitVOs);
		tempResult.setOriginalToSplitVOs(toSplitVOs);
		tempResult.setApplyDate(leavehVO.getApply_date());
		tempResult.setLeaveTypeVOMap(leaveTypeVOMap);
		tempResult.setOriginalLeaveTypeCopyVO(leaveTypeVOMap.get(pk_leavetype));
//		UFBoolean canOverYear = SysInitQuery.getParaBoolean(pk_org, LeaveConst.OVERYEAR_PARAM);//跨年度单据是否可以保存参数
		UFBoolean canOverYear = timeRuleVO.getIscansaveoveryear();
		tempResult.setCanOverYear(canOverYear!=null&&canOverYear.booleanValue());
		tempResult.setTimeRuleVO(timeRuleVO);
		Map<String, TimeZone> psnjobTimeZoneMap = new HashMap<String, TimeZone>();
		//获取与休假时段有交集的所有可能的结算记录，包含前置假和用户填写的类别，已经按规则排好顺序
//		Map<String, List<LeaveBalanceVO>> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, bvos);
		Map<String, List<LeaveBalanceVO>> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, toSplitVOs);
		LeaveBalanceVO[] balanceVOs = MapUtils.isEmpty(balanceMap)?null:(CollectionUtils.isEmpty(balanceMap.get(pk_psnorg))?null:balanceMap.get(pk_psnorg).toArray(new LeaveBalanceVO[0]));
		//此处的休假数据不应该包含已经结算的数据
		for(LeaveBalanceVO bvo:balanceVOs){
			if(bvo.isSettlement()){
				balanceVOs = (LeaveBalanceVO[]) ArrayUtils.removeElement(balanceVOs, bvo);
			}
		}
		if(ArrayUtils.isEmpty(balanceVOs)){
			balanceVOs = null;
			throw new BusinessException(ResHelper.getString("6017hrta","06017hrta0071")
/*@res "没有可用的休假数据！或休假数据已结算！"*/);
		}
		LeaveBalanceVO[] balanceVOsClone =SplitLeaveBillHelper.clone(balanceVOs);//将结算记录复制一下,因为后面需要用结算记录的结余时长等信息
		tempResult.setBalanceVOs(balanceVOs);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(leavehVO.getPk_org());
//		Map<String, AggShiftVO> aggShiftMap = bvos[0].getAggShiftMap();
//		if(null == aggShiftMap){
//			aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(leavehVO.getPk_org());
//			 bvos[0].setAggShiftMap(aggShiftMap);
//		}

		CalParam4OnePerson leaveLengthCalParam = BillProcessHelperAtServer.initParam(leavehVO.getPk_psndoc(),
				timeRuleVO, BillMutexRule.createBillMutexRule(timeRuleVO.getBillmutexrule()),
				aggShiftMap,CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap),
				aggVO.getBodyVOs(),
				BillMutexRule.BILL_LEAVE,2,2);
		processSplit(tempResult,leaveLengthCalParam,psnjobTimeZoneMap);
		//如果没有任何拆单事件发生，则直接返回
		if(!tempResult.isRealSplit()){
			LeavehVO hVO = aggVO.getHeadVO();
			splitResult.setSplit(false);
			AggLeaveVO[] splitVOs = new AggLeaveVO[]{SplitLeaveBillHelper.syncBodyToHeadAndCreateAggVO(tempResult.getSplitVOs(), hVO)};
			SplitLeaveBillHelper.syncRestLength(splitVOs, balanceVOsClone, leaveTypeVOMap);
			splitResult.setSplitResult(splitVOs);
			return splitResult;
		}
		//有拆单事件发生（拆单不一定是一单变多单，有可能单子的数量没变，但休假类别或者期间变了，也视为拆单），需要将拆完的单子做一下归并
		splitResult.setSplit(true);
		AggLeaveVO[] splitVOs = SplitLeaveBillHelper.groupLeavebVOs(aggVO.getHeadVO(), tempResult.getSplitVOs(), leaveTypeVOMap, canOverYear!=null&&canOverYear.booleanValue());
		SplitLeaveBillHelper.processVOStatus(aggVO, splitVOs);
		SplitLeaveBillHelper.syncRestLength(splitVOs, balanceVOsClone, leaveTypeVOMap);
		splitResult.setSplitResult(splitVOs);
		return splitResult;
	}


	public static SplitBillResult<AggLeaveVO> split(AggLeaveVO aggVO) throws BusinessException{
		LeavehVO leavehVO = aggVO.getLeavehVO();
		if(leavehVO.getIslactation()!=null&&leavehVO.getIslactation().booleanValue()){//哺乳假不执行拆单
			SplitBillResult<AggLeaveVO> splitResult = new SplitBillResult<AggLeaveVO>();
			splitResult.setOriginalBill(aggVO);
			splitResult.setSplit(false);
			splitResult.setSplitResult(new AggLeaveVO[]{aggVO});
			return splitResult;
		}
		String pk_org = aggVO.getHeadVO().getPk_org();
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		ITimeItemQueryService timeItemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		Map<String, LeaveTypeCopyVO> leaveTypeVOMap = timeItemService.queryLeaveCopyTypeMapByOrg(pk_org);
		Map<String, LeaveTypeCopyVO[]> dependMap = timeItemService.queryLeaveTypeDependTypes(pk_org);
		return split(aggVO, timeRuleVO, leaveTypeVOMap, dependMap);
	}

	/**
	 * 将一个登记单执行拆单操作
	 * 如果未执行拆单，则返回null，否则返回拆单结果
	 * @param regVO
	 * @return
	 * @throws BusinessException
	 */
	public static SplitBillResult<LeaveRegVO> split(LeaveRegVO regVO) throws BusinessException{
		SplitBillResult<LeaveRegVO> splitResult = new SplitBillResult<LeaveRegVO>();
		splitResult.setOriginalBill(regVO);
		if(regVO.getIslactation()!=null&&regVO.getIslactation().booleanValue()){//哺乳假不执行拆单
			splitResult.setSplit(false);
			splitResult.setSplitResult(new LeaveRegVO[]{regVO});
			return splitResult;
		}
		String pk_leavetype = regVO.getPk_leavetype();
		String pk_org = regVO.getPk_org();
		String pk_psnorg = regVO.getPk_psnorg();
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		ITimeItemQueryService timeItemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		Map<String, LeaveTypeCopyVO> leaveTypeVOMap = timeItemService.queryLeaveCopyTypeMapByOrg(pk_org);
		Map<String, LeaveTypeCopyVO[]> dependMap = timeItemService.queryLeaveTypeDependTypes(pk_org);
		TemporarySplitResult<LeaveRegVO> tempResult = new TemporarySplitResult<LeaveRegVO>();
		//如果有跨业务单元的休假，则切开
		LeaveRegVO[] oriVOs = new LeaveRegVO[]{regVO};
		LeaveRegVO[] toSplitVOs = BillMethods.compareAndCutDate(pk_org, oriVOs);
		//如果切割的结果为空，表示整个单据与考勤档案没有任何交集，这种情况下应该抛出异常
		if(ArrayUtils.isEmpty(toSplitVOs)){
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0247")
					/*@res "休假时间范围不在考勤档案范围内!"*/);
		}
		tempResult.setToSplitVOs(toSplitVOs);
		tempResult.setOriginalToSplitVOs(oriVOs);
		tempResult.setLeaveTypeVOMap(leaveTypeVOMap);
		tempResult.setOriginalLeaveTypeCopyVO(leaveTypeVOMap.get(pk_leavetype));
		tempResult.setCanOverYear(timeRuleVO.isCanSaveOveryear());
		tempResult.setTimeRuleVO(timeRuleVO);
		Map<String, TimeZone> psnjobTimeZoneMap = new HashMap<String, TimeZone>();
		//获取与休假时段有交集的所有可能的结算记录，包含前置假和用户填写的类别，已经按规则排好顺序
		Map<String, List<LeaveBalanceVO>> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, new LeaveCommonVO[]{regVO});
		LeaveBalanceVO[] balanceVOs = MapUtils.isEmpty(balanceMap)?null:(CollectionUtils.isEmpty(balanceMap.get(pk_psnorg))?null:balanceMap.get(pk_psnorg).toArray(new LeaveBalanceVO[0]));
		LeaveBalanceVO[] balanceVOsClone =SplitLeaveBillHelper.clone(balanceVOs);//将结算记录复制一下,因为后面需要用结算记录的结余时长等信息
		tempResult.setBalanceVOs(balanceVOs);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		CalParam4OnePerson leaveLengthCalParam = BillProcessHelperAtServer.initParam(regVO.getPk_psndoc(),
				timeRuleVO, BillMutexRule.createBillMutexRule(timeRuleVO.getBillmutexrule()),
				aggShiftMap,CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap),
				new LeaveRegVO[]{regVO},
				BillMutexRule.BILL_LEAVE,2,2);
		processSplit(tempResult,leaveLengthCalParam,psnjobTimeZoneMap);
		//如果没有任何拆单事件发生，则直接返回
		if(!tempResult.isRealSplit()){
			splitResult.setSplit(false);
			SplitLeaveBillHelper.syncRestLength(tempResult.getSplitVOs(), balanceVOsClone, leaveTypeVOMap);
			splitResult.setSplitResult(tempResult.getSplitVOs());
			return splitResult;
		}
		//有拆单事件发生（拆单不一定是一单变多单，有可能单子的数量没变，但休假类别或者期间变了，也视为拆单），需要将拆完的单子做一下归并
		splitResult.setSplit(true);
		SplitLeaveBillHelper.processVOStatus(regVO, tempResult.getSplitVOs());
		SplitLeaveBillHelper.syncRestLength(tempResult.getSplitVOs(), balanceVOsClone, leaveTypeVOMap);
		splitResult.setSplitResult(tempResult.getSplitVOs());
		return splitResult;
	}

	/**
	 * 对批量生成的休假申请/登记进行拆单操作
	 * @param <T>
	 * @param pk_org
	 * @param appDate,申请日期，批增申请单时有意义，批增登记单时为null
	 * @param batchVOs
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends LeaveCommonVO> T[] split(String pk_org,UFLiteralDate appDate,T[] batchVOs) throws BusinessException{
		if(ArrayUtils.isEmpty(batchVOs))
			return batchVOs;
		ITimeItemQueryService timeItemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		Map<String, LeaveTypeCopyVO[]> dependMap = timeItemService.queryLeaveTypeDependTypes(pk_org);
		Map<String, LeaveTypeCopyVO> leaveTypeVOMap = timeItemService.queryLeaveCopyTypeMapByOrg(pk_org);
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		BillMutexRule mutexRule = BillMutexRule.createBillMutexRule(timeRuleVO.getBillmutexrule());
		LeaveTypeCopyVO[] sortedTypeVOs = timeItemService.sortLeaveTypeByDependTypes(pk_org);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<String, CalParam4OnePerson> paramMap = BillProcessHelperAtServer.initParam(timeRuleVO, mutexRule, aggShiftMap, CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap), batchVOs, BillMutexRule.BILL_LEAVE, 2, 2);
		//将这些休假记录按人员组织关系分组，再按人员组织关系循环处理
		Map<String, T[]> psnorgGrpMap = CommonUtils.group2ArrayByField(LeaveCommonVO.PK_PSNORG, batchVOs);
		//按自然人分组，因为创建休假计算参数CalParam4OnePerson的时候，需要把此次自然人的所有休假记录都传进去
//		Map<String, T[]> psndocGrpMap = CommonUtils.group2ArrayByField(LeaveCommonVO.PK_PSNDOC, batchVOs);
		LeaveComparator<T> comparator = new LeaveComparator<T>(sortedTypeVOs);
		ILeaveBalanceManageService leaveBalanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
		Map<String, List<LeaveBalanceVO>> balanceMap = leaveBalanceService.queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, batchVOs);
		Map<String, TimeZone> psnjobTimeZoneMap = BillMethods.getPsnjobTimeZoneMap(StringPiecer.getStrArrayDistinct(batchVOs, LeaveCommonVO.PK_PSNJOB), timeRuleVO.getTimeZoneMap());// 组织时区Map
		List<T> retList = new ArrayList<T>();//用于存储返回的数组的list
		for(String pk_psnorg:psnorgGrpMap.keySet()){//按人员组织关系循环处理
			T[] psnBatchVOs = psnorgGrpMap.get(pk_psnorg);//此人此次所有批量新增的记录
			String pk_psndoc = psnBatchVOs[0].getPk_psndoc();
			Arrays.sort(psnBatchVOs, comparator);
			CalParam4OnePerson leaveLengthCalParam = paramMap.get(pk_psndoc);
			//存储某一个人员组织关系，循环处理多个批增时段时，最新的结余情况的map(因为批增的过程中，结余时长不会同步到数据库总的，所以要在内存中同步)
			//<pk_timeitem+年度[+月份]+leaveindex，结算记录>
			Map<String,LeaveBalanceVO> newestBalanceVOMap = new HashMap<String, LeaveBalanceVO>();
			for(T batchVO:psnBatchVOs){
				LeaveTypeCopyVO typeVO = leaveTypeVOMap.get(batchVO.getPk_timeitem());
				LeaveBalanceVO[] balanceVOs = MapUtils.isEmpty(balanceMap)?null:(CollectionUtils.isEmpty(balanceMap.get(batchVO.getPk_psnorg()))?null:balanceMap.get(batchVO.getPk_psnorg()).toArray(new LeaveBalanceVO[0]));
				//由于一个人可能有多条休假时段，因此上面查询的结算记录中的时长可能已经被前面的休假记录消耗掉一些了，需要从之前的结算记录来同步
				SplitLeaveBillHelper.syncFromNewestBalanceVO(newestBalanceVOMap, balanceVOs);
				LeaveBalanceVO[] balanceVOsClone =SplitLeaveBillHelper.clone(balanceVOs);//将结算记录复制一下,因为后面需要用结算记录的结余时长等信息
				TemporarySplitResult<T> tempResult = new TemporarySplitResult<T>();//用于包装此次计算所需数据的tempresult
				tempResult.setApplyDate(appDate);
				tempResult.setBalanceVOs(balanceVOs);
				tempResult.setCanOverYear(timeRuleVO.isCanSaveOveryear());//跨年度单据是否可以保存参数
				tempResult.setLeaveTypeVOMap(leaveTypeVOMap);
				tempResult.setOriginalLeaveTypeCopyVO(typeVO);
				T[] toSplitVOs = SplitLeaveBillHelper.createOneElementArray(batchVO);//需要进行拆单的记录，包装成长度为1的数组，方便调用
				tempResult.setOriginalToSplitVOs(toSplitVOs);
				tempResult.setToSplitVOs(toSplitVOs);
				tempResult.setTimeRuleVO(timeRuleVO);
				processSplit(tempResult, leaveLengthCalParam, psnjobTimeZoneMap);//执行拆单
				SplitLeaveBillHelper.syncRestLengthForBatch(tempResult.getSplitVOs(), balanceVOsClone, leaveTypeVOMap);
				retList.addAll(Arrays.asList(tempResult.getSplitVOs()));
			}
		}
		return retList.toArray((T[]) Array.newInstance(batchVOs.getClass().getComponentType(), 0));
	}

	/**
	 * 进行拆单。此方法是单个处理方法，即，一次要么只处理一张申请单，要么只处理一张登记单，要么只处理批量新增时的一条记录
	 * @param temporarySplitResult
	 * @param leaveTypeVOMap
	 * @param balanceVOs
	 * @param <T>
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> void processSplit(
			TemporarySplitResult<T> temporarySplitResult,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap//pk_psnjob对应的时区的map,<pk_psnjob,TimeZone>
			) throws BusinessException{
		LeaveBalanceVO[] balancedVOs = temporarySplitResult.getBalanceVOs();
		if(ArrayUtils.isEmpty(balancedVOs))
			return;
		//将要拆单的数组先按开始时间排序
		TimeScopeUtils.sort(temporarySplitResult.getToSplitVOs());
		Map<String, LeaveTypeCopyVO> leaveTypeVOMap = temporarySplitResult.getLeaveTypeVOMap();
		//各个leavebalance的有效时间范围
		Map<LeaveBalanceVO, ITimeScope> effectiveScopeMap = SplitLeaveBillHelper.createBalanceVOEffectiveScopeMap(balancedVOs, leaveLengthCalParam);
		temporarySplitResult.setBalanceVOEffectiveScopeMap(effectiveScopeMap);
		//所有前置假+自身类别的结余时长，要轮番地尝试拆单.顺序为：
		//若往期结余假优先为Y，或者（结余假有限问N,但用户没有录入年度/期间），则顺序为往期前置假，往期本类别，当期前置假，当期本类别
		//若结余假优先为N，且用户录入了年度/期间，则需要将用户录入的类别+期间的结余数据往前提，提到第一条用户录入的类别的结余数据之前
		for(int i=0;i<balancedVOs.length;i++){
			if(ArrayUtils.isEmpty(temporarySplitResult.getToSplitVOs()))//toSplitVOs为空，表示已经拆单完毕
				return;
			LeaveBalanceVO balanceVO = balancedVOs[i];
			if(balanceVO.isSettlement())//如果已经结算，则不用处理
				continue;
			//此类别在此年度/期间能支持的休假的时间范围（考虑了有效期延长天数，并且保证了一个完整的班次时段不会被切割到两个期间里，即此时段的开始时间/结束时间不会落在一个完整班次的中间）
			ITimeScope effectivePeriodTimeScope = effectiveScopeMap.get(balanceVO);
			//如果此时间范围与休假时段没有交集，则不用continue。intersectionScopes是此类别在此年度/期间能支持的休假的最大范围
			T[] intersectionScopes = TimeScopeUtils.intersectionTimeScopesRemainsOriType(effectivePeriodTimeScope, temporarySplitResult.getToSplitVOs());
			if(ArrayUtils.isEmpty(intersectionScopes))
				continue;
			temporarySplitResult.setToSplitVOsForOneBalanceVO(intersectionScopes);//此leavebalancevo将要试图支持的休假段
			temporarySplitResult.setSplitVOsForOneBalanceVO(null);
			String pk_leavetype = balanceVO.getPk_timeitem();
			LeaveTypeCopyVO leaveTypeCopyVO = leaveTypeVOMap.get(pk_leavetype);
//			double usefulRestLen = balanceVO.getUsefulRestDayOrHour();//此类别此期间的可用结余时长，由结余时长减去冻结时长
			//2013-07-16 可用是时长是0.33，但是考勤规则中取0位小数，向下取整，会导致拆单的时长为0，先在此根据考勤规则处理可用时长防止这种拆单
			double usefulRestLen = leaveLengthCalParam.timeruleVO.getTimeRuleUFDouble(balanceVO.getUsefulrestdayorhour()).doubleValue();//此类别此期间的可用结余时长，由结余时长减去冻结时长
			int timeitemUnit = leaveTypeCopyVO.getTimeItemUnit();//时长单位，天还是小时。
			boolean isDayUnit = timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_DAY;
			double timeUnit0 = leaveTypeCopyVO.getTimeUnit();//最小时长，例如30分钟，0.5天。如果为0，则说明无最小时长要求，时长算出来是多少就是多少，不用取整
			double timeUnit = isDayUnit?timeUnit0:timeUnit0/60;//将按分钟计的最小时长转换为按小时计
			//只有在结余时长能至少保证一个最小时长的时候，才能拆单
			//结余时长中可以提供的最大时长：若最小时长为0，则可以全部提供；否则只能提供最小时长的整数倍，且不超过结余时长
			double maxAffordLen = timeUnit==0?usefulRestLen:Math.floor(usefulRestLen/timeUnit)*timeUnit;
			//拆单操作
			processSplit(balanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap);
		}
		//第一个循环拆完之后，如果还有时段没被支持，则继续拆
		postProcessSplit(temporarySplitResult, leaveLengthCalParam, psnjobTimeZoneMap);
	}
	/**
	 * 在进行了第一轮的循环处理之后，如果还有未被支持的时段，则要试图将这些时段往用户录入的类别上摁，摁的前提是用户录入的类别不控制，或者不严格控制时长
	 * 摁完之后，用户录入的类别肯定时长超限
	 * @param <T>
	 * @param temporarySplitResult
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> void postProcessSplit(
			TemporarySplitResult<T> temporarySplitResult,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap//pk_psnjob对应的时区的map,<pk_psnjob,TimeZone>
			) throws BusinessException{
		T[] toSplitVOs = temporarySplitResult.getToSplitVOs();
		//循环完了之后所有的休假时长都被拆出，则可以直接返回
		if(ArrayUtils.isEmpty(toSplitVOs))
			return;
		//如果循环完了之后还有未被支持的休假时长，则这些时长要试图往用户录入的类别上靠--前提是用户录入的类别是不严格控制时长的。
		//这些未被支持的时长，形成的原因有多种：可能是休假时段所在的年度/期间的结算记录已经被结算，可能是违反了申请日期超限参数，可能是结余时长不够
		//若用户录入的类别严格控制时长，那么直接抛异常，因为循环的过程可以保证此类别已经竭尽全力提供时长了，上面的循环里不能支持的，现在照样不能支持
		//若用户录入的类别控制时长，但不严格，或者干脆不控制时长，则还有一线希望能容纳这些剩余的时段
		//若抛异常，不要简单地抛出，要分析一下原因，且告知用户，方便用户调整休假时段
		LeaveTypeCopyVO oriType = temporarySplitResult.getOriginalLeaveTypeCopyVO();
		boolean isStrictLimit = oriType.isLeaveLimit()&&oriType.isRestrictLimit();
		String pk_psndoc = toSplitVOs[0].getPk_psndoc();
		Map<LeaveBalanceVO, ITimeScope> effectiveScopeMap = temporarySplitResult.getBalanceVOEffectiveScopeMap();
		//下面，
		//对于不严格控制或者不控制时长的，把这些还没支持的时段轮流往用户录入的类别上寻求支持，能支持就支持，不能支持就到下一个leavebalance记录
		//对于严格控制时长的，只进行异常原因分析，不再寻求往用户录入的类别上寻求支持
		LeaveBalanceVO[] balancedVOs = temporarySplitResult.getBalanceVOs();
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(toSplitVOs[0].getPk_psnjob(), psnjobTimeZoneMap);
		for(int i=0;i<balancedVOs.length;i++){
			if(ArrayUtils.isEmpty(temporarySplitResult.getToSplitVOs()))//toSplitVOs为空，表示已经拆单完毕
				return;
			LeaveBalanceVO balanceVO = balancedVOs[i];
			if(!balanceVO.getPk_timeitem().equals(oriType.getPk_timeitem()))//如果不是用户录入的类别，则不用处理
				continue;
			ITimeScope effectiveScope = effectiveScopeMap.get(balanceVO);
			//如果此时间范围与休假时段没有交集，则不用continue。intersectionScopes是此类别在此年度/期间能支持的休假的最大范围
			T[] intersectionScopes = TimeScopeUtils.intersectionTimeScopesRemainsOriType(effectiveScope, temporarySplitResult.getToSplitVOs());
			if(ArrayUtils.isEmpty(intersectionScopes))
				continue;
			if(balanceVO.isSettlement()){//如果已经结算，则如果交集与独占区相交，则说明相交的部分已经不可能被支持了，只能抛异常
				//如果有交集，且交集与本balancevo的独占区有交集，且本balancevo已经结算，则抛异常
				ITimeScope[] exclusionScopes = SplitLeaveBillHelper.getExclusionScopes(balanceVO, temporarySplitResult, true);
				if(TimeScopeUtils.isCross(exclusionScopes, intersectionScopes)){
					String psnName = getPsnName(pk_psndoc);
					String period = balanceVO.getCuryear()+(oriType.isSetPeriodYearORDate()?"":balanceVO.getCurmonth());
					String periodName = oriType.isSetPeriodYearORDate()?PublicLangRes.YEAR2():PublicLangRes.PERIOD();
					throw new BusinessException(ResHelper.getString("6017leave","06017leave0226"
/*@res "人员{0}在{1}{2}的{3}已经结算!"*/, psnName,period,periodName,oriType.getMultilangName()));
				}
				if(!isStrictLimit)
					continue;//如果交集与独占区不相交，则还有可能被后续的leavebalance支持
			}
			for(T toSplitVO:intersectionScopes){
				toSplitVO.setPk_timeitem(oriType.getPk_timeitem());
				toSplitVO.setPk_leavetypecopy(oriType.getPk_timeitemcopy());
				toSplitVO.setLeaveyear(balanceVO.getCuryear());
				toSplitVO.setLeavemonth(balanceVO.getCurmonth());
				toSplitVO.setLeaveindex(balanceVO.getLeaveindex());
			}
			//如果有交集，则如果没有违反申请日超期的规定的话，应该是可以支持的
			if(temporarySplitResult.applyDate!=null){
				for(T toSplitVO:intersectionScopes){
					checkApplyDatePara(toSplitVO, temporarySplitResult.applyDate, timeZone, oriType, true);//执行申请日超限校验
				}
			}
			if(isStrictLimit){
				//走到这里，如果是严格控制时长，则肯定是时长超限了
				String psnName = getPsnName(pk_psndoc);
				throw new BusinessException(ResHelper.getString("6017leave","06017leave0225"
	/*@res "人员{0}的休假时长超过了{1}的结余时长!"*/, psnName,oriType.getMultilangName()));
			}
			//如果是不严格控制或者不控制，则寻求超限支持
			BillProcessHelperAtServer.calLeaveLength4OnePerson(oriType.getPk_org(), pk_psndoc, intersectionScopes, oriType, leaveLengthCalParam);
			//走到这里，说明不是申请单，或者是申请单，但申请日期没有超限，可以全部支持这些时段。支持的时候，不是简单地把这些时段放到splitVOs里面，
			//而是要先试着与本leavebalance已经被支持的时段合并，这样效果才能更好，否则用户会觉得很奇怪：同一类别同一期间的两条休假记录，时间是接着的
			T[] mergedSplitVOs = SplitLeaveBillHelper.mergeScopesByBalanceVO(temporarySplitResult.getSplitVOs(), intersectionScopes, temporarySplitResult.isCanOverYear(), balanceVO, oriType, leaveLengthCalParam, timeZone);
			temporarySplitResult.setSplitVOs(mergedSplitVOs);
			temporarySplitResult.setToSplitVOs(SplitLeaveBillHelper.minus(temporarySplitResult.getToSplitVOs(), intersectionScopes, timeZone));
		}
		//走到这里，如果还有没有被支持的时段，则直接抛异常即可，因为已经没有可能被支持了。
		if(!ArrayUtils.isEmpty(temporarySplitResult.getToSplitVOs())){
			String psnName = getPsnName(pk_psndoc);
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0227"
/*@res "人员{0}的休假时段不能被支持!"*/, psnName));
		}
	}

	private  static <T extends LeaveCommonVO> void processSplit0(
			LeaveBalanceVO curProcessBalanceVO,//当前处理的结算记录
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			boolean splitBreakAppDateRule,//是否要进行申请日超期拆单校验
			boolean splitBreakOverYearRule//是否要进行休假单跨年拆单
			) throws BusinessException{
		T[] curProecessScopes = temporarySplitResult.getToSplitVOsForOneBalanceVO();
		//如果所有的休假时段都成功地被拆单了，或者已经没有能力提供休假时长了，则递归结束
		if(ArrayUtils.isEmpty(curProecessScopes)||maxAffordLen<0.00001)
			return;
		//当前处理的休假类别
		LeaveTypeCopyVO curProcessLeaveTypeCopyVO = temporarySplitResult.getLeaveTypeVOMap().get(curProcessBalanceVO.getPk_timeitem());
		//对剩下的时段，还要依次进行两项校验：申请日期超过开始日期多少天的校验(只对申请单)和休假时段是否可以跨年度保存的校验
//		T vo = temporarySplitResult.getToSplitVOs()[0];
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(curProecessScopes[0].getPk_psnjob(), psnjobTimeZoneMap);
		if(splitBreakAppDateRule){
			//首先进行申请日期的校验,将时间太早以至于违反了休假类别规定的休假时间段拆出来
			T[][] splitVO = SplitLeaveBillHelper.splitTimeScopeByApplyDatePara(curProecessScopes,temporarySplitResult.getApplyDate(),timeZone,curProcessLeaveTypeCopyVO);
			//若splitVO[0]不为null，表示vo中有违反申请日期超期的时间段，已被放入splitVO[0]中，要将此时间段从toSplitVOs中暂时扣除,对剩下的toSplitVOs进行分单，分完后再加回去
			if(!ArrayUtils.isEmpty(splitVO[0])){
				temporarySplitResult.setToSplitVOsForOneBalanceVO(splitVO[1]);//
				processSplit0(curProcessBalanceVO,  temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap,false,splitBreakOverYearRule);
				temporarySplitResult.setToSplitVOsForOneBalanceVO(SplitLeaveBillHelper.reviveToArray(temporarySplitResult.getToSplitVOsForOneBalanceVO(), splitVO[0]));//暂时扣除的加回去
				return;
			}
			processSplit0(curProcessBalanceVO, temporarySplitResult,  maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, false,splitBreakOverYearRule);
			return;
		}
		if(splitBreakOverYearRule){
			//下面，考虑参数“跨年度休假单是否允许保存”，如果不允许，需要做两件事情：1.跨年度的休假时段拆分开。2.每一个休假时段上记上所属自然年度，将来不同自然年度的休假时段不能属于同一张申请单
			T[] splitVO = SplitLeaveBillHelper.splitTimeScopeByOverYearPara(curProecessScopes, timeZone, leaveLengthCalParam);
			if(splitVO.length==curProecessScopes.length){//如果按自然年度拆分后，长度没变，表示不需要拆分
				processSplit0(curProcessBalanceVO, temporarySplitResult,  maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, splitBreakAppDateRule,false);
				return;
			}
			//拆分后长度大于1，则表示已经按自然年度进行了拆分，需要将拆分后的时段重新设置到tosplitvo中
			temporarySplitResult.setToSplitVOsForOneBalanceVO(splitVO);
			processSplit0(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, splitBreakAppDateRule,false);
			return;
		}
		//走到这里，表示toSplitVOsForOneBalanceVO的所有时间段的申请日期不超限，且不违反是否允许跨年参数，可以放心地进行最核心的业务---拆单了
		//下面看toSplitVOsForOneBalanceVO的时长，是否超过了可以提供的最大时长，若没超过，则可以直接把整个toSplitVOsForOneBalanceVO挪到splitVOsForOneBalanceVO
		//中去；若超过了，则要根据可提供的最大时长反推结束时间
		for(T vo:curProecessScopes){
			vo.setPk_leavetype(curProcessLeaveTypeCopyVO.getPk_timeitem());
			vo.setPk_leavetypecopy(curProcessLeaveTypeCopyVO.getPk_timeitemcopy());
			vo.setLeaveyear(curProcessBalanceVO.getCuryear());
			vo.setLeavemonth(curProcessBalanceVO.getCurmonth());
			vo.setLeaveindex(curProcessBalanceVO.getLeaveindex());
		}
		BillProcessHelperAtServer.calLeaveLength4OnePerson(curProcessBalanceVO.getPk_org(),
				curProcessBalanceVO.getPk_psndoc(),curProecessScopes , curProcessLeaveTypeCopyVO, leaveLengthCalParam);
		//此段时间段的长度，若时间段长度<=本类别本期间可以提供的长度，则直接从toSplitVOs里面挪到splitVOs里面
		double calLength = SplitLeaveBillHelper.sumLength(curProecessScopes);
		if(calLength<=maxAffordLen){
			temporarySplitResult.setToSplitVOsForOneBalanceVO(null);
			temporarySplitResult.setSplitVOsForOneBalanceVO(curProecessScopes);
			curProcessBalanceVO.setRealdayorhour(new UFDouble(curProcessBalanceVO.getRestDayOrHourValue()-calLength));//要将计算的时长从结余中暂时扣除，因为在批量新增的情况下，此BalanceVO可能在后面还会被用到，如果不扣除，将会导致计算错误
			return;
		}
		//若这些休假时段的长度>本类别本期间可以提供的长度，则只能支持这些休假时段中的一部分。具体支持哪一部分，是有讲究的，
		processSplitLengthNotEnough(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap);
	}

	/**
	 * 某个类别期间结余时长不够的情况下，部分地支持休假时间段
	 * 此时处理的休假时段都没有违反申请日超期、跨年度保存规定，可以放心地处理
	 * 由于结余时长不够，只能支持一部分休假时长。具体支持哪一部分，是有讲究的，
	 * 原则是，优先支持“独占区”的休假时段
	 * "独占区"的意思是，本类别本期间能支持，但循环后续的类别期间已经不可能支持的区域。为了不使算法过于复杂，我们约定如下：
	 * 将时段分为三种：完全在独占区里面的，部分在独占区里的，完全在独占区之外的
	 * 优先用从左至右的顺序处理第一种，其次第二种，最后从左至右处理第三种
	 *
	 * 第二种的处理方式最复杂：首先看是否能全额支持，如果能，则好办，全额支持即可。若不能全额支持，则，
	 * 将休假时段与独占区进行交切处理，切割为独占区内的时段scopes1和独占区外的时段scopes2
	 * 若不能全部支持scopes1，则，如果是前置假类别，或者 严格控制时长，抛异常
	 *                           如果是用户录入的类别，且（不控制时长||不严格控制时长），则将scopes1都设为此类别此期间，返回（即scopes2不予支持），并且，如果是不严格控制时长，还要提示用户超限
	 * 若可以全部支持scopes1,则依次将scopes2中的时段合并到scopes1中去，若合并后时长不超限，则表明可以支持此scopes2中的时段。若合并后超限了，需要
	 * 按照某种规则拆单，根据合并的方向，有可能是从左往右拆，也有可能是从右往左拆。具体的方式见代码
	 *
	 * @param <T>
	 * @param curProcessBalanceVO
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> void processSplitLengthNotEnough(
			LeaveBalanceVO curProcessBalanceVO,//当前处理的结算记录
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap
			) throws BusinessException{
		//首先得到此类别期间的"独占区"
		ITimeScope[] exclusionScopes = SplitLeaveBillHelper.getExclusionScopes(curProcessBalanceVO, temporarySplitResult,false);
		processSplitLengthNotEnoughComplexly(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, exclusionScopes);
	}


	/**
	 * 结余时长不够处理所有的休假时段时，复杂的处理方式，要综合判断该先支持哪些时段，以及该从左至右支持，还是从右往左支持
	 * @param <T>
	 * @param curProcessBalanceVO
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private  static <T extends LeaveCommonVO> void processSplitLengthNotEnoughComplexly(
			LeaveBalanceVO curProcessBalanceVO,//当前处理的结算记录
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			ITimeScope[] exclusionScopes//此结算记录的独占区
			) throws BusinessException{
		//首先将待支持的休假时段分为三类：完全在独占区里的，部分在独占区里的，完全在独占区外的，第一类和第三类都可以从左至右支持，第二类需要进一步判断
		T[][] groupVOs = SplitLeaveBillHelper.groupScopesByExclusionScopes(temporarySplitResult.getToSplitVOsForOneBalanceVO(), exclusionScopes);
		List<T> splitVOList = new ArrayList<T>();

		String errMsg = ResHelper.getString("6017leave","06017leave0228")
/*@res "人员{0}从{1}到{2}的休假时段的时长超出了{3}的可用时长!"*/;
		//优先支持完全在独占区里面的。
		T[] inExclusionScopes=groupVOs[0];//完全在独占区里的，即后续的leavebalance记录已经确定无法支持的
		if(!ArrayUtils.isEmpty(inExclusionScopes)){
			maxAffordLen = processSplitLengthNotEnoughInExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, inExclusionScopes, splitVOList, errMsg);
		}
		//然后处理部分在独占区内，部分在独占区外的休假时段
		T[] partInExclusionScopes=groupVOs[1];//部分在独占区里的，后续的leavebalance记录已经可以部分支持的
		if(!ArrayUtils.isEmpty(partInExclusionScopes)){
			maxAffordLen = processSplitLengthNotEnoughPartInExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, exclusionScopes, partInExclusionScopes, splitVOList, errMsg);
		}
		//最后处理完全在独占区之外的部分
		T[] outExclusionScopes=groupVOs[2];//完全在独占区外的，leavebalance记录可以完全支持的
		if(!ArrayUtils.isEmpty(outExclusionScopes)){
			maxAffordLen = processSplitLengthNotEnoughOutExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, outExclusionScopes, splitVOList, errMsg);
		}
		//代码走到这里，可以被支持的时段都放入了splitVOList里面
		if(splitVOList.size()>0){
			TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(splitVOList.get(0).getPk_psnjob(), psnjobTimeZoneMap);
			temporarySplitResult.setSplitVOsForOneBalanceVO(splitVOList.toArray((T[]) Array.newInstance(temporarySplitResult.getToSplitVOsForOneBalanceVO().getClass().getComponentType(), splitVOList.size())));
			temporarySplitResult.setToSplitVOsForOneBalanceVO(SplitLeaveBillHelper.minus(temporarySplitResult.getToSplitVOsForOneBalanceVO(), temporarySplitResult.getSplitVOsForOneBalanceVO(), timeZone));
		}
	}

	/**
	 * 处理完全在独占区之外的休假时段
	 * 基本思想是量力而行，能支持多少支持多少
	 * @param <T>
	 * @param curProcessBalanceVO
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @param outExclusionScopes
	 * @param splitVOList
	 * @param errMsg
	 * @return 支持完毕之后，还剩下的可支持的时长
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> double processSplitLengthNotEnoughOutExclusionScopes(
			LeaveBalanceVO curProcessBalanceVO,//当前处理的结算记录
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			T[] outExclusionScopes,//休假记录中，完全在独占区外的时段
			List<T> splitVOList,//将可以支持的部分放入此list
			String errMsg
	) throws BusinessException{
		LeaveTypeCopyVO typeVO = temporarySplitResult.getLeaveTypeVOMap().get(curProcessBalanceVO.getPk_timeitem());
		for(int i=0;i<outExclusionScopes.length;i++){
			T vo = outExclusionScopes[i];
			double len = vo.getLeaveHourValue();
			if(len<=maxAffordLen){
				maxAffordLen-=len;
				curProcessBalanceVO.minusRestdayorhour(len);
				splitVOList.add(vo);
				continue;
			}
			//代码走到这里，说明当前这个休假时段的时长已经不能全部被支持了。由于是在独占区外，因此还有后续的leavebalance记录可以支持，因此此处
			//竭尽所能支持到哪里算哪里
			if(maxAffordLen==0)
				break;
			//调用休假时长反推算法，算出可以支持的休假时间段（可以支持的在返回数组的第一个元素，不能支持的在第二个元素）
			T[] splitVOs = inverseCalculate(vo, typeVO, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap,true);
			T canAffordScope = splitVOs[0];//可以支持的时段
			if(canAffordScope==null||canAffordScope.getLeavebegintime().equals(canAffordScope.getLeaveendtime()))
				break;
			maxAffordLen-=canAffordScope.getLeaveHourValue();
			curProcessBalanceVO.minusRestdayorhour(canAffordScope.getLeaveHourValue());
			splitVOList.add(canAffordScope);
			break;
		}
		return maxAffordLen;
	}

	/**
	 * 处理完全在独占区之内的休假时段
	 * 处理思想：
	 * 如果能够完全支持，则完全支持
	 * 如果不能完全支持，但当前的leavebalance是用户录入的类别，且（不控制时长||不严格控制时长），也能完全支持（即使用超限时长）
	 * 如果不能完全支持，且用户录入的类别严格控制时长，则抛异常，(因为已经没有leavebalance记录可以支持了)
	 * 如果不能完全支持，且当前的leavebalance不是用户录入的类别，且用户录入的类别（不控制时长||不严格控制时长），则要进行拆单，将能支持的时段拆出来
	 * 不能支持的时段，等到全部leavebalance记录循环完毕之后，再从头循环，寻找用户录入的类别的leavebalance，看它们能不能用超限时长来支持
	 * @param <T>
	 * @param curProcessBalanceVO
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @param inExclusionScopes
	 * @param splitVOList
	 * @param errMsg
	 * @return 支持完毕之后，还剩下的可支持的时长
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> double processSplitLengthNotEnoughInExclusionScopes(
			LeaveBalanceVO curProcessBalanceVO,//当前处理的结算记录
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			T[] inExclusionScopes,//休假记录中，完全在独占区内的时段
			List<T> splitVOList,//将可以支持的部分放入此list
			String errMsg
	) throws BusinessException{

		LeaveTypeCopyVO typeVO = temporarySplitResult.getLeaveTypeVOMap().get(curProcessBalanceVO.getPk_timeitem());
		boolean isLimitLength = typeVO.isLeaveLimit();//当前类别是否控制时长
		boolean isStrictLimit = isLimitLength&&typeVO.isRestrictLimit();//当前类别是否严格控制时长
		//当前处理的是否是用户录入的类别（如果是，则时长超了的时候处理有些不一样）
		boolean isUserInputType = temporarySplitResult.getOriginalLeaveTypeCopyVO().getPk_timeitem().equals(curProcessBalanceVO.getPk_timeitem());
		//用户录入的类别是否严格控制时长
		boolean isUerInputTypeStrictLimit = temporarySplitResult.getOriginalLeaveTypeCopyVO().isLeaveLimit()&&temporarySplitResult.getOriginalLeaveTypeCopyVO().isRestrictLimit();
		double sumLength = SplitLeaveBillHelper.sumLength(inExclusionScopes);
		//若时长未超限，或者(此leavebalance是用户录入类别且(不严格控制||不控制))，则可以支持所有的时段
		if(sumLength<=maxAffordLen||(isUserInputType&&!isStrictLimit)){
			splitVOList.addAll(Arrays.asList(inExclusionScopes));
			maxAffordLen-=sumLength;
			curProcessBalanceVO.minusRestdayorhour(sumLength);
			return maxAffordLen;
		}
		//走到这里，时长肯定超限了，并且(不是用户录入的类别或者严格控制时长)
		//由于当前处于独占区，因此超长的时段已经不能被后续的leavebalance记录支持了。唯一的救命稻草，是前面已经循环过了的用户录入类别的leavebalance记录
		//并且用户录入的类别还要求是不控制时长或者不严格控制。如果用户录入的类别是严格控制，则只能抛异常了
		if(isUerInputTypeStrictLimit){
			String psnName = getPsnName(inExclusionScopes[0].getPk_psndoc());
			TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(inExclusionScopes[0].getPk_psnjob(), psnjobTimeZoneMap);
			throw new BusinessException(MessageFormat.format(errMsg, psnName,inExclusionScopes[0].getLeavebegintime().toStdString(timeZone),inExclusionScopes[inExclusionScopes.length-1].getLeaveendtime().toStdString(timeZone),typeVO.getMultilangName()));
		}
		//走到这里，说明当前的类别不是用户录入的类别，并且用户录入的类别（不控制时长||不严格控制）
		for(int i=0;i<inExclusionScopes.length;i++){
			T vo = inExclusionScopes[i];
			double len = vo.getLeaveHourValue();
			if(len<=maxAffordLen){
				maxAffordLen-=len;
				curProcessBalanceVO.minusRestdayorhour(len);
				splitVOList.add(vo);
				continue;
			}
			//代码走到这里，说明当前这个休假时段的时长已经不能全部被支持了。由于此处是独占区，后续已经没有leavebalance可以支持了，但还有可能被前面
			//的leavebalance记录重新支持(需要是用户录入的类别，且(不控制||不严格控制时长))
			//此时需要拆单，拆剩下的部分等到此次循环完了之后，再回过头去看之前的leavebalance中有没有能用超限时长来支持这些时段的
			if(maxAffordLen==0)
				break;
			//调用休假时长反推算法，算出可以支持的休假时间段（可以支持的在返回数组的第一个元素，不能支持的在第二个元素）
			T[] splitVOs = inverseCalculate(vo, typeVO, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap,true);
			T canAffordScope = splitVOs[0];//可以支持的时段
			if(canAffordScope==null||canAffordScope.getLeavebegintime().equals(canAffordScope.getLeaveendtime()))
				break;
			maxAffordLen-=canAffordScope.getLeaveHourValue();
			curProcessBalanceVO.minusRestdayorhour(canAffordScope.getLeaveHourValue());
			splitVOList.add(canAffordScope);
			break;
		}
		return maxAffordLen;
	}

	/**
	 * 处理部分在独占区里，部分在独占区外的时段
	 * 这些时段是最难处理的
	 * 基本思想是，若能够全部支持，则全部支持即可。
	 * 若不能全部支持，则将这些脚踏两条船的时段，拆成两个部分：独占区之内的部分和独占区之外的部分Pin，优先处理独占区之内的部分Pout
	 * 若Pin能全部支持，则全部支持Pin，Pout部分则量力而行，能支持多少就支持多少
	 * 若Pin不能全部支持，则参考processSplitLengthNotEnoughInExclusionScopes方法的处理方式处理Pin，Pout就不予支持了
	 * @param <T>
	 * @param curProcessBalanceVO
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @param exclusionScopes
	 * @param partInExclusionScopes
	 * @param splitVOList
	 * @param errMsg
	 * @return 支持完毕之后，还剩下的可支持的时长
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> double processSplitLengthNotEnoughPartInExclusionScopes(
			LeaveBalanceVO curProcessBalanceVO,//当前处理的结算记录
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			ITimeScope[] exclusionScopes,//此结算记录的独占区
			T[] partInExclusionScopes,//休假记录中，部分在独占区内，部分在独占区之外的部分(俗称脚踏两条船的时段)
			List<T> splitVOList,//将可以支持的部分放入此list
			String errMsg
	) throws BusinessException{
		double sumLen = SplitLeaveBillHelper.sumLength(partInExclusionScopes);//这些脚踏两条船的时段的总时长
		//若能结余时长够用，则要把这些时段都弄到本类别本期间上
		if(sumLen<=maxAffordLen){
			splitVOList.addAll(Arrays.asList(partInExclusionScopes));
			maxAffordLen-=sumLen;
			curProcessBalanceVO.minusRestdayorhour(sumLen);
			return maxAffordLen;
		}
		//代码走到这里，说明结余时长不能完全支持这些部分在独占区内的时段。那么要优先支持独占区之内的交集时长
		//先算出这些时段与独占区的交集，若交集时长也不能支持，则可能要抛异常（是前置类别，或者严格控制时长）
		LeaveTypeCopyVO typeVO = temporarySplitResult.getLeaveTypeVOMap().get(curProcessBalanceVO.getPk_timeitem());
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(partInExclusionScopes[0].getPk_psnjob(), psnjobTimeZoneMap);
		//交切处理成两部分：独占区之内的部分，和独占区之外的部分
		T[][] splitVOs = SplitLeaveBillHelper.splitScopesByExclusionScopes(partInExclusionScopes, exclusionScopes, timeZone);
		//独占区的交集，计算其总时长
		T[] intersectionScopes = splitVOs[0];
		BillProcessHelperAtServer.calLeaveLength4OnePerson(curProcessBalanceVO.getPk_org(), partInExclusionScopes[0].getPk_psndoc(), intersectionScopes, typeVO, leaveLengthCalParam);
		double intersctionLen = SplitLeaveBillHelper.sumLength(intersectionScopes);//独占区之内部分的总时长
		//如果结余时长不能支持这么长的时段，则参照processSplitLengthNotEnoughInExclusionScopes方法来处理这些独占区内的时段，且独占区之外的不再支持
		if(intersctionLen>maxAffordLen){
			double remainsAffordLen = processSplitLengthNotEnoughInExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, intersectionScopes, splitVOList, errMsg);
			curProcessBalanceVO.minusRestdayorhour(maxAffordLen-remainsAffordLen);//maxAffordLen-remainsAffordLen就是此次消耗的时长
			maxAffordLen=remainsAffordLen;
			return maxAffordLen;
		}
		//代码走到这里，说明结余时长可以支持独占区内的部分,但不够支持独占区之外的部分
		//采取的策略是，先将独占区以内的全支持了，然后用余力支持独占区之外的部分
		//因为一个时段分成两段之后计算的时长一般情况下会大于整个时段计算的时长，因此下面的结算都要将被拆开的时段重新合并后再计算才准确
		T[] toMergeScopes = intersectionScopes;//将要合并的时段
		for(T outScope:splitVOs[1]){//循环尝试着支持独占区之外的部分
			//现将本来已经被拆出来的独占区之外的时段合并回去，再算一下时长，如果能支持，则将这个时段全部加入，否则用时长反推开始时间或者结束时间
			//toMergeScopes永远保存着此次合并之前的记录
			MergeResultDescriptor<T> result = SplitLeaveBillHelper.mergeScopeToScopes(toMergeScopes, outScope, temporarySplitResult.isCanOverYear(),timeZone);
			BillProcessHelperAtServer.calLeaveLength4OnePerson(curProcessBalanceVO.getPk_org(), partInExclusionScopes[0].getPk_psndoc(), result.getVos(), typeVO, leaveLengthCalParam);
			double mergedLength = SplitLeaveBillHelper.sumLength(result.getVos());//合并此时段之后的总时长
			if(mergedLength<=maxAffordLen){//如果加上这个时段后，未超限，则可以继续处理
				toMergeScopes = result.getVos();
				continue;
			}
			//如果加上这个时段后，超限了，则要进行拆单操作
			//此时段outScope合并到mergedScopes里面，有三种可能的方式：
			//1.outScope的开始时间和前面一个时段的结束时间接上了：-----------独占区里的A------*--------独占区外的B-------------
			//2.outScope的结束时间和后面一个时段的开始时间接上了：-----------独占区外的C------*--------独占区里的D-------------
			//3.outScope的开始时间和结束时间和前后各一个时段都接上了：-----独占区里的A------*---独占区外的B----*-----独占区里的C---------
			//对于第一种，从被接上的时段的开始时间开始，从左往右支持休假时段，直到时长用完为止
			//对于第二种，从被接上的时段的结束时间开始，从右往左支持休假时段，直到时长用完为止
			//对于第三种，从前一个时段的开始时间开始，从左往右支持休假时段，直到时长用完为止
			int leftJoinIndex = result.getLeftJoinIndex();//能与outScope的左端接上的时段的index
			int rightJoinIndex = result.getRightJoinIndex();//能与outScope的右端接上的时段的index
			//第一种和第二种接法，从左往右/从右往左拆
			if((leftJoinIndex>=0&&rightJoinIndex<0)||(leftJoinIndex<0&&rightJoinIndex>=0)){
				int keyIndex = leftJoinIndex>=0?leftJoinIndex:rightJoinIndex;//对于第一种情况，是leftJoinIndex，第二种情况是rightJoinIndex
				T toSplitVO = result.getVos()[keyIndex];
				//对于这段，能提供的时长（=maxAffordLen-(mergedScopes里面除去keyIndex位置上的时段的总时长)）.这句话需要仔细体会
				double canAffordLen2SplitVO = maxAffordLen-SplitLeaveBillHelper.sumLengthExclude(toMergeScopes, keyIndex);
				T[] spltVOs = inverseCalculate(toSplitVO, typeVO, canAffordLen2SplitVO, leaveLengthCalParam, psnjobTimeZoneMap, leftJoinIndex>=0);
				for(int n=0;n<toMergeScopes.length;n++){
					if(n==keyIndex){
						splitVOList.add(spltVOs[0]);
						double len = spltVOs[0].getLeaveHourValue();
						maxAffordLen-=len;
						curProcessBalanceVO.minusRestdayorhour(len);
						continue;
					}
					splitVOList.add(toMergeScopes[n]);
					double len = toMergeScopes[n].getLeaveHourValue();
					maxAffordLen-=len;
					curProcessBalanceVO.minusRestdayorhour(len);
				}
				return maxAffordLen;
			}
			//第三种，本来outScope已经一前一后与两个休假时段合并成一个整段了，但由于不能全部支持，只能做如下处理：合并后的整段，从outScope的结束时间再拆成
			//两段，从拆分后的第一个段的开始时间从左往右支持：
			//                          独占区里的A                  独占区外的B                独占区里的C
			//本来已经合并成这个样子：------------------------*-----------------------------*--------------------
			//现在要将C再拆出来                 ------------------------*-----------------------------××-------------------
			//                       ------------------------------------------------------>从左往右支持A+B的合并段(C拆出来后也是被默认支持的)
			T toSplitVO = result.getVos()[leftJoinIndex];//本来这段已经三段合成一段了，下面要再拆开
			toSplitVO.setLeaveendtime(outScope.getLeaveendtime());
			toSplitVO.setLeaveenddate(DateTimeUtils.toLiteralDate(toSplitVO.getLeaveendtime(), timeZone));
			//对于这段，能提供的时长
			double canAffordLen2SplitVO = maxAffordLen-SplitLeaveBillHelper.sumLengthExclude(toMergeScopes, leftJoinIndex);
			T[] spltVOs = inverseCalculate(toSplitVO, typeVO, canAffordLen2SplitVO, leaveLengthCalParam, psnjobTimeZoneMap, true);
			for(int n=0;n<toMergeScopes.length;n++){
				if(n==leftJoinIndex){
					splitVOList.add(spltVOs[0]);
					double len = spltVOs[0].getLeaveHourValue();
					maxAffordLen-=len;
					curProcessBalanceVO.minusRestdayorhour(len);
					continue;
				}
				splitVOList.add(toMergeScopes[n]);
				double len = toMergeScopes[n].getLeaveHourValue();
				maxAffordLen-=len;
				curProcessBalanceVO.minusRestdayorhour(len);
			}
			return maxAffordLen;
		}
		//按道理，是不能走到这里的
		throw new RuntimeException("program calculation error!");
	}
	/**
	 * 进行拆单
	 * 核心算法
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveTypeVOMap
	 * @param unBalancedVOs
	 * @param applyDate 申请日期，对于申请单有意义
	 * @param <T>
	 * @param CalParam4OnePerson,计算个人休假时长需要的参数
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> void processSplit(
			LeaveBalanceVO curProcessBalanceVO,
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap) throws BusinessException{
		processSplit0(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam,
				psnjobTimeZoneMap,  temporarySplitResult.getApplyDate()!=null, !temporarySplitResult.isCanOverYear());
		//走到这里，此leavebalance所有能支持的时段都放入了splitVOsForOneBalanceVO里面，所有不能支持的都放入了toSplitVOsForOneBalanceVO里面
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(temporarySplitResult.getToSplitVOs()[0].getPk_psnjob(), psnjobTimeZoneMap);
		temporarySplitResult.splitScopesFromToSplitVOs(temporarySplitResult.getSplitVOsForOneBalanceVO(), timeZone);
	}

	/**
	 * 反推算法，将一个休假时间段拆成两个部分：第一个部分的长度刚好=maxAffordLen。拆的原因是maxAffordLen不能支持整个vo的长度
	 * isFromLeft2Right决定了拆的方向，为true的话，从左往右支持，即可以支持的部分在vo左部，为false的话，从右往左支持，即可以支持的部分在vo右部
	 * 大致逻辑：
	 * 首先用简单算法算出一个粗略的结束/开始时间T
	 * 然后用T计算休假时长L，定义step=(L<=可提供时长)?正向推10分钟:反向推10分钟（正向是会使休假时长变长的方向，对于从左到右拆，是往after方向，反之是往before方向）
	 * 然后开始下面的循环：
	 * location:
	 * T=T+step，计算休假时长L
	 * 若L<=可提供时长
	 * 		则 如果 step为正向-->step不变，goto location
	 *        如果step=反向推10分钟-->step=正向推1分钟，goto location
	 *        如果step=反向推1分钟，则循环停止，T就是确切的休假结束/开始时间
	 * 若L>可提供时长
	 * 		则 如果 step为反向-->step不变，goto location
	 *        如果 step为正向推10分钟-->step=反向推1分钟，goto location
	 *        如果step为正向推1分钟，则循环停止，T=T-step
	 * 此算法的基本思想是，先算一个大概的时间点，如果此时间点计算的时长还没超限，则用大步子(10分钟)往前冲；如果超限了，则用大步子(10分钟)往后撤
	 * 大步冲了一个step之后，再次计算时长，若仍然没有超，则继续大步(10分钟)冲
	 *                                   若超了，则往回小步(1分钟)退
	 * 大步撤了一个step之后，再次计算时长，若仍然超限，则继续大步(10分钟)撤
	 *                                   若没有超限，则改变方向，小步（1分钟）往前走
	 * 后面就是小步往前走或往后退，直到算出一个比较精细的结束时间为止
	 * @param <T>
	 * @param vo
	 * @param leaveTypeCopyVO
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @param isFromLeft2Right,true表示从开始时间开始往结束时间的方向拆，false表示从结束时间往开始时间的方向拆
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private static <T extends LeaveCommonVO> T[] inverseCalculate(
			T vo,
			LeaveTypeCopyVO leaveTypeCopyVO,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			boolean isFromLeft2Right) throws BusinessException{
		//休假结余时长能提供的休假小时数（maxAffordLen的单位有可能是天，要统一转换为小时，便于计算）
		double leaveHours = leaveTypeCopyVO.getTimeItemUnit()==TimeItemCopyVO.TIMEITEMUNIT_HOUR?maxAffordLen:maxAffordLen*leaveLengthCalParam.timeruleVO.getDaytohour2();
		//初步计算的休假结束/开始时间。这只是一个粗略计算的时间，需要进一步的精确处理
		UFDateTime testEndOrBeginTime = simpleCalculateEndOrBeginTime(isFromLeft2Right?vo.getLeavebegintime():vo.getLeaveendtime(),
				isFromLeft2Right?vo.getLeaveendtime():vo.getLeavebegintime(), leaveHours, leaveTypeCopyVO, leaveLengthCalParam,isFromLeft2Right);
		T tempCanAffordVO = (T)vo.clone();
		tempCanAffordVO.setPk_leavetype(leaveTypeCopyVO.getPk_timeitem());
		tempCanAffordVO.setPk_leavetypecopy(leaveTypeCopyVO.getPk_timeitemcopy());
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(tempCanAffordVO.getPk_psnjob(), psnjobTimeZoneMap);
		setEndOrBeginTime(tempCanAffordVO,testEndOrBeginTime, timeZone, isFromLeft2Right);
		//休假时长计算方法要求传入数组参数，因此构造一个数组
		T[] tempVOs = SplitLeaveBillHelper.createOneElementArray(tempCanAffordVO);
		String pk_org = leaveTypeCopyVO.getPk_org();
		String pk_psndoc = vo.getPk_psndoc();
		BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
		int directionFlag = isFromLeft2Right?1:-1;//从左往右拆和从右往左拆的时候，步长的方向不一样
		long positiveTenMinutesSconds = 10*60*directionFlag;//正向推10分钟时，该加的秒数。对于从左往右拆，正向推加的秒数是正的；对于从右往左拆，正向推的秒数是负的
		long negativeTenMinutesSconds = 0-positiveTenMinutesSconds;//反向推10分钟时，该加的秒数
		long positiveOneMinuteSeconds = 60*directionFlag;//正向推1分钟时，该加的秒数
		long negativeOneMinuteSeconds = 0-positiveOneMinuteSeconds;//反向推1分钟时，该加的秒数
		double calLen = tempCanAffordVO.getLeavehour()==null?0:tempCanAffordVO.getLeavehour().doubleValue();
		long step = calLen<=maxAffordLen?positiveTenMinutesSconds:negativeTenMinutesSconds;
		UFDateTime exactEndOrBeginTime = null;//计算的确切结束/开始时间
		while(true){
			testEndOrBeginTime = DateTimeUtils.getDateTimeAfterMills(testEndOrBeginTime, step*1000);
			setEndOrBeginTime(tempCanAffordVO, testEndOrBeginTime, timeZone, isFromLeft2Right);
			BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
			calLen = tempCanAffordVO.getLeavehour()==null?0:tempCanAffordVO.getLeavehour().doubleValue();
			if(calLen<=maxAffordLen){//如果还未超限
				if(step*directionFlag>0)//step*directionFlag>0表明此时是在正向推时间
					continue;
				if(step==negativeTenMinutesSconds){
					step = positiveOneMinuteSeconds;
					continue;
				}
				exactEndOrBeginTime=testEndOrBeginTime;
				break;
			}
			if(step*directionFlag<0)//step*directionFlag<0表明此时是在反向推时间
				continue;
			if(step==positiveTenMinutesSconds){
				step = negativeOneMinuteSeconds;
				continue;
			}
			exactEndOrBeginTime = DateTimeUtils.getDateTimeBeforeMills(testEndOrBeginTime, step*1000);
			break;
		}
		T leftScope = null;//拆分之后左半部分
		T rightScope = null;//拆分之后右半部分
		if(exactEndOrBeginTime.equals(vo.getLeavebegintime())){//如果切割点刚好是vo的开始时间，则左半部为null，右半部为全部
			leftScope=null;
			rightScope = (T)vo.clone();
		}
		else if(exactEndOrBeginTime.equals(vo.getLeaveendtime())){//如果切割点刚好是vo的结束时间，则右半部为null，左半部为全部
			leftScope= (T)vo.clone();
			rightScope = null;
		}
		else{//如果切割点在中间,则需要分成两段
			leftScope = (T) vo.clone();
			setEndOrBeginTime(leftScope, exactEndOrBeginTime, timeZone, true);
			tempVOs[0]=leftScope;
			BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
			rightScope = (T)vo.clone();
			setEndOrBeginTime(rightScope, exactEndOrBeginTime, timeZone, false);
			tempVOs[0]=rightScope;
			BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
		}
		//用于返回的数组，第一个时段是休假时长可以支持的时段，第二个时段是不能支持的
		T[] retArray = (T[]) Array.newInstance(vo.getClass(), 2);
		if(isFromLeft2Right){//如果是从左往右拆，则左边的元素是能支持的休假时段，右边的是不能支持的
			retArray[0]=leftScope;
			retArray[1]=rightScope;
			return retArray;
		}
		retArray[1]=leftScope;
		retArray[0]=rightScope;
		return retArray;
	}

	/**
	 * 根据休假开始/结束时间fromTime，休假小时数leaveHours，简单快速地计算出一个休假结束/开始时间的近似值
	 * 基本思想是，从fromTime开始往后沿着时间轴往后/前走，走过的工作日历的工作时间段的长度刚好等于leaveHours时，停住，返回停止地的时间点
	 * @param fromTime
	 * @param endTimeLimit,计算的时间点的下限/上限，计算出来的结束/开始时间不能晚于/早于此时间
	 * @param leaveHours
	 * @param leaveTypeCopyVO
	 * @param leaveLengthCalParam
	 * @param fromLeft2Right true，表示从开始时间出发往结束方向找，false相反
	 * @return
	 * @throws BusinessException
	 */
	private static UFDateTime simpleCalculateEndOrBeginTime(UFDateTime fromTime,UFDateTime toTimeLimit,double leaveHours,
			LeaveTypeCopyVO leaveTypeCopyVO,CalParam4OnePerson leaveLengthCalParam,boolean fromLeft2Right) throws BusinessException{
		//取出开始/结束时间的所属日期，然后从所属日期开始循环处理工作日历。保险起见，所属日期往前/后推三天
		UFLiteralDate curDate = fromTime.getDate().toUFLiteralDate(ICalendar.BASE_TIMEZONE).getDateBefore(fromLeft2Right?3:-3);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = leaveLengthCalParam.calendarMap;//人员工作日历
		Map<String, ShiftVO> shiftMap = leaveLengthCalParam.shiftMap;
		Map<UFLiteralDate, String> dateOrgMap = leaveLengthCalParam.dateOrgMap;
		TimeRuleVO timeRuleVO = leaveLengthCalParam.timeruleVO;
		Map<String, TimeZone> orgTimeZoneMap = timeRuleVO.getTimeZoneMap();
		boolean gxIsLeave = leaveTypeCopyVO.getGxcomtype().intValue()==LeaveTypeCopyVO.GXCOMTYPE_TOLEAVE;
		long leaveSeconds = (long)leaveHours*3600;//将小时转换为秒
		long shiftSumSecondsAfterBeforeFromTime = 0;//按日期循环过程中，将每天的班次的工作时间的长度累加起来（要求是休假开始/结束时间fromTime之后/前的部分）
		long workDaySeconds = (long)timeRuleVO.getDaytohour2()*3600;//考勤规则中的工作日时长，转换为秒
//		int dateAfterUnit = fromLeft2Right?1:-1;
		while(true){//若fromLeft2Right为true，则是从前往后推算，否则是从后往前推算
			UFLiteralDate preDate = null;//前一天
			UFLiteralDate nextDate = null;//后一天
			if(fromLeft2Right){
				preDate = curDate;
				curDate = curDate.getDateAfter(1);
				nextDate = curDate.getDateAfter(1);
			}
			else{
				nextDate = curDate;
				curDate = curDate.getDateBefore(1);
				preDate = curDate.getDateBefore(1);
			}
			//当天的工作日历
			AggPsnCalendar curCalendar = calendarMap.get(curDate);
			//当天的时区
			TimeZone curTimeZone = orgTimeZoneMap.get(dateOrgMap.get(curDate));
			curTimeZone = curTimeZone==null?ICalendar.BASE_TIMEZONE:curTimeZone;
			//前一天的工作日历
			AggPsnCalendar preCalendar = calendarMap.get(preDate);
			//前一天的班次
			ShiftVO preShift = preCalendar==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
			//前一天的时区
			TimeZone preTimeZone = orgTimeZoneMap.get(dateOrgMap.get(preDate));
			preTimeZone = preTimeZone==null?ICalendar.BASE_TIMEZONE:preTimeZone;
			//后一天的工作日历
			AggPsnCalendar nextCalendar = calendarMap.get(nextDate);
			//后一天的班次
			ShiftVO nextShift = nextCalendar==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, nextCalendar.getPsnCalendarVO().getPk_shift());
			//后一天的时区
			TimeZone nextTimeZone = orgTimeZoneMap.get(dateOrgMap.get(nextDate));
			nextTimeZone = nextTimeZone==null?ICalendar.BASE_TIMEZONE:nextTimeZone;
			//当天的工作时间段
			ITimeScope[] workScopes = BillProcessHelper.getWorkTimeScopes(curDate.toString(), curCalendar, preShift, nextShift, curTimeZone, preTimeZone, nextTimeZone);
			ITimeScope firstWorkScope = workScopes[0];//第一个工作段
			ITimeScope lastWorkScope = workScopes[workScopes.length-1];//最后一个工作段
			//若班次开始时间已经>=toTimeLimit/班次结束时间已经<=toTimeLimit，表示日期已经往后/前推得太多了，则直接返回toTimeLimit即可
			if((fromLeft2Right&&!firstWorkScope.getScope_start_datetime().before(toTimeLimit))
					||(!fromLeft2Right&&!lastWorkScope.getScope_end_datetime().after(toTimeLimit)))
				return toTimeLimit;
			//如果当天是公休且公休不计休假,则continue
			boolean curIsGX = curCalendar==null||curCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX);
			if(curIsGX&&!gxIsLeave){
				continue;
			}
			//如果班次的工作时间段在fromTime前面/后面,说明while的次数还不够，没有到达正儿八经开始的时间点，continue
			if(fromLeft2Right){
				if((!lastWorkScope.isContainsLastSecond()&&!lastWorkScope.getScope_end_datetime().after(fromTime))||
				    (lastWorkScope.isContainsLastSecond()&&lastWorkScope.getScope_end_datetime().before(fromTime)))
					continue;
			}
			else{
				if(!firstWorkScope.getScope_start_datetime().before(fromTime))
					continue;
			}
			//代码走到这里,说明这一天的班次的时间段全部或者部分在fromTime之后/前,可以被以fromTime为开始时间/结束时间的休假段所覆盖到
			long shiftSecondsAfterBeforeFromTime = 0;//当天的班次在休假开始时间之后/休假结束时间之前的秒数
			//班次的开始/结束时间
			UFDateTime shiftBeginEndTime = fromLeft2Right?firstWorkScope.getScope_start_datetime():lastWorkScope.getScope_end_datetime();
			long shiftSeconds = TimeScopeUtils.getLength(workScopes);//当天班次的完整秒数
			//若休假开始时间<=班次的开始时间，即工作段完全在休假开始时间之后||休假结束时间>=班次的结束时间，即工作段完全在休假结束时间之前
			if((fromLeft2Right&&!fromTime.after(shiftBeginEndTime))||(!fromLeft2Right&&!fromTime.before(shiftBeginEndTime))){
				shiftSecondsAfterBeforeFromTime = curIsGX?Math.min(shiftSeconds, workDaySeconds):shiftSeconds;
			}
			else{//若休假的开始时间>班次的开始时间，即休假开始时间在班次的开始时间与结束时间之间||休假的结束时间<班次的结束时间，即休假结束时间在班次的开始时间与结束时间之间
				//休假开始时间之后的工作段/休假结束时间之前的工作段
				ITimeScope[] workScopesAfterBeforeFromTime = TimeScopeUtils.intersectionTimeScopes(workScopes,
						new ITimeScope[]{fromLeft2Right?
						 new DefaultTimeScope(fromTime, lastWorkScope.getScope_end_datetime(),lastWorkScope.isContainsLastSecond())
						:new DefaultTimeScope(firstWorkScope.getScope_start_datetime(), fromTime, false)});
				shiftSecondsAfterBeforeFromTime = curIsGX?Math.min(workDaySeconds, TimeScopeUtils.getLength(workScopesAfterBeforeFromTime)):
					TimeScopeUtils.getLength(workScopesAfterBeforeFromTime);
			}
			long remainSeconds = leaveSeconds-shiftSumSecondsAfterBeforeFromTime;//此次还能提供的休假秒数
			if(shiftSecondsAfterBeforeFromTime<remainSeconds){//如果remainSeconds还能完全包含住此工作段且还有剩余
				shiftSumSecondsAfterBeforeFromTime+=shiftSecondsAfterBeforeFromTime;
				continue;
			}
			if(shiftSecondsAfterBeforeFromTime==remainSeconds){//如果remainSeconds刚好能包含此工作段，则返回工作段最后一秒/第一秒
				return fromLeft2Right?
						(DateTimeUtils.min(lastWorkScope.isContainsLastSecond()?
								DateTimeUtils.getDateTimeAfterMills(lastWorkScope.getScope_end_datetime(), 1000):
								lastWorkScope.getScope_end_datetime(),
								toTimeLimit)):
						DateTimeUtils.max(firstWorkScope.getScope_start_datetime(), toTimeLimit);
			}
			//如果remainSeconds已经不够了，则试算结束，
			//结束时间为later(休假开始时间fromTime,工作段开始时间)+remainSeconds/开始时间为earlier(休假结束时间fromTime,工作段结束时间)-remainSeconds
			//此试算结束时间不一定准确，还需要下面的微调来精确定位结束时间
			UFDateTime spreadTime = SplitLeaveBillHelper.spread(fromTime, workScopes, remainSeconds,fromLeft2Right);
			return fromLeft2Right?
				DateTimeUtils.min(spreadTime,toTimeLimit):
				DateTimeUtils.max(spreadTime,toTimeLimit);
		}
	}

	private static <T extends LeaveCommonVO> boolean checkApplyDatePara(T vo ,UFLiteralDate applyDate,TimeZone timeZone,LeaveTypeCopyVO leaveTypeCopyVO,boolean throwsException) throws BusinessException{
		if(!(vo instanceof LeavebVO)||!leaveTypeCopyVO.isLeaveAppTimeLimit()){//如果不是申请单，或者休假类别不限制这个，则整个时段都放到第二个元素上
			return false;
		}
		int limitDates = leaveTypeCopyVO.getLeaveAppTimeLimit();
		UFLiteralDate beginDate = DateTimeUtils.toLiteralDate(vo.getLeavebegintime(), timeZone);
		UFLiteralDate properBeginDate = applyDate.getDateBefore(limitDates);
//		int daysAfter = properBeginDate.getDaysAfter(beginDate);
		boolean isBreak = beginDate.before(properBeginDate);//移动端的不好使
		if(isBreak&&throwsException){
//		if(daysAfter>0&&throwsException){
			String psnName = getPsnName(vo.getPk_psndoc());
			String typeName = leaveTypeCopyVO.getMultilangName();
			int lateDates = UFLiteralDate.getDaysBetween(beginDate, applyDate);
			if(limitDates==0)
				throw new BusinessException(MessageFormat.format(ResHelper.getString("6017leave","06017leave0229")
/*@res "人员{0}休假类别{1}的申请日期{2}比休假开始日期{3}晚了{4}天,按规定应该当天申请!"*/, psnName,typeName,applyDate,beginDate,lateDates));
			throw new BusinessException(MessageFormat.format(ResHelper.getString("6017leave","06017leave0230")
/*@res "人员{0}休假类别{1}的申请日期{2}比休假开始日期{3}晚了{4}天,按规定不得超过{5}天!"*/, psnName,typeName,applyDate,beginDate,lateDates,limitDates));
		}
		return isBreak;
	}


	private static String getPsnName(String pk_psndoc) throws DAOException{
		return ((PsndocVO)new BaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc)).getMultiLangName();
	}

	/**
	 * 设置一个休假时段的结束/开始时间，设置时，顺便把结束/开始日期也设置了
	 * @param <T>
	 * @param vo
	 * @param endOrBeginTime
	 * @param timeZone
	 * @param isSetEndTime，true表示设置结束时间，false表示设置开始时间
	 */
	private static <T extends LeaveCommonVO> void setEndOrBeginTime(T vo,UFDateTime endOrBeginTime,TimeZone timeZone,boolean isSetEndTime){
		if(isSetEndTime){
			vo.setLeaveendtime(endOrBeginTime);
			vo.setLeaveenddate(DateTimeUtils.toLiteralDate(endOrBeginTime, timeZone));
			return;
		}
		vo.setLeavebegintime(endOrBeginTime);
		vo.setLeavebegindate(DateTimeUtils.toLiteralDate(endOrBeginTime, timeZone));
	}
}