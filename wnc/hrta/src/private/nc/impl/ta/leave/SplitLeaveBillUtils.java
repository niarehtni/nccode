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
 * ʵ�ֲ��㷨����
 * ��ν�𵥣����Ǹ���ǰ�üٹ����Լ����ڽ�������Ȳ��������û���д�����뵥/�Ǽǵ�ת��Ϊ��ȷ���ݼ������ڼ䣨
 * �п��ܲ𵥵Ľ�����û���д����ȫһ�£�������д��2011��ȵ��¼٣��𵥵Ľ��Ҳ��2011��ȵ��¼٣����������
 * ����δ�𵥣�
 * ���ڵ����������뵥��Ǽǵ���������������˲𵥣������𵥵Ľ�����û���Ǳ��ʶ��һ�£�ʱ����Ҫ��
 * �Ի�����ʾ�û������𵥽����ʾ�ڶԻ����С�
 * ����������������ֱ�ӽ��𵥽����ʾ�����һ���ĶԻ����У��˲�ԭ�����У����û���д��������Ϣ���ɾ�����ݼټ�¼��
 * ��ô�������ǣ��ڵ�������ʱ��ʲô����£�����Ҫ�öԻ�����ʾ�û���
 * ������������ۡ�
 * �����ڽ�������ȡ�����ΪN��ʱ���û��ڽ�������Ҫ��д�ݼ����;�����ڼ䣬��ôֻҪ�𵥽������д������ڼ�
 * ��һ�£��Ϳ�����Ϊ�в���Ϊ��������������𵥽��������������ݼ���𣬻��߳�����������ڼ䣬�����Ϊ��
 * ���ˡ����⣬�������ݼ��Ƿ������桱�����������ڲ�������ʼ����__�족�Ĳ���Ҳ�п��ܻᵼ�²𵥣���������£�Ҳ��Ҫ��ʾ�û�
 * �����ڽ�������ȡ�����ΪY��ʱ���û��ڽ�����ֻ��Ҫ��д�ݼ���𣬲���Ҫ��д�ڼ䣬������ʱ�û�����Ҫ����
 * �ڼ䣬ϵͳ������ڼ������㷨��������ʵ��ڼ䡣��������£�����𵥽���г�����������ݼ���𣬻��ߡ�û��
 * ������������𣬵�����𱻲���˶���ڼ䡿���������Ϊ���ڲ��ˡ�
 * ���磬2011.1.3����д��һ���¼ٵ����п���
 * �𵥵Ľ���ǣ�
 * 2010�겡�٣���Ϊ���ˣ�����ʾ�û�
 * 2011�겡�٣�2011���¼٣���Ϊ���ˣ�����ʾ�û�
 * 2010���¼٣�δ�𵥣�����ʾ�û������ɵ�2010����Զ�����������
 * 2011���¼٣�δ�𵥣�����ʾ�û������ɵ�2011����Զ�����������
 * 2010���¼٣�2011���¼٣���Ϊ���ˣ�����ʾ�û�����ʱ�ݼ������Ȼû�䣬���û���Ǳ��ʶ���Լ�ֻ����һ���ݼٵ���������
 * ���ŵ��ӣ���ȻҪ��ʾ�û�
 * �ü���������������������
 * ���ڡ����ڽ�������ȡ�����ΪN�������ֻ�в𵥽��Ϊ������ڼ��ʱ�򣬲Ų�����ʾ�û������������Ҫ��ʾ�û�
 * ���ڡ����ڽ�������ȡ�����ΪY�������ֻ�в𵥽��Ϊ������ڼ��ʱ�򣬲Ų�����ʾ�û������������Ҫ��ʾ�û�
 * @author zengcheng
 *
 */
public class SplitLeaveBillUtils {


	/**
	 * @param aggVO
	 * @param timeRuleVO
	 * @param leaveTypeVOMap �ݼ�������������copyvo�Ķ�Ӧmap��<pk_timeitem,LeaveTypeCopyVO>
	 * @param dependTypeVOMap���ݼ������������������copyvo����Ķ�Ӧ��<pk_timeitem,LeaveTypeCopyVO[]>
	 * @return ��ͬ�ݼ������ͬ�ڼ�ĺϲ�Ϊһ�����뵥
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
		if(leavehVO.getIslactation()!=null&&leavehVO.getIslactation().booleanValue()){//����ٲ�ִ�в�
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
		//����п�ҵ��Ԫ���ݼ٣����п�
		LeavebVO[] orgSplitVOs = BillMethods.compareAndCutDate(pk_org, toSplitVOs);
		//����и�Ľ��Ϊ�գ���ʾ���������뿼�ڵ���û���κν��������������Ӧ���׳��쳣
		if(ArrayUtils.isEmpty(orgSplitVOs)){
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0247")
					/*@res "�ݼ�ʱ�䷶Χ���ڿ��ڵ�����Χ��!"*/);
		}
		TimeScopeUtils.sort(orgSplitVOs);
		TimeScopeUtils.sort(toSplitVOs);
		TemporarySplitResult<LeavebVO> tempResult = new TemporarySplitResult<LeavebVO>();
		tempResult.setToSplitVOs(orgSplitVOs);
		tempResult.setOriginalToSplitVOs(toSplitVOs);
		tempResult.setApplyDate(leavehVO.getApply_date());
		tempResult.setLeaveTypeVOMap(leaveTypeVOMap);
		tempResult.setOriginalLeaveTypeCopyVO(leaveTypeVOMap.get(pk_leavetype));
//		UFBoolean canOverYear = SysInitQuery.getParaBoolean(pk_org, LeaveConst.OVERYEAR_PARAM);//����ȵ����Ƿ���Ա������
		UFBoolean canOverYear = timeRuleVO.getIscansaveoveryear();
		tempResult.setCanOverYear(canOverYear!=null&&canOverYear.booleanValue());
		tempResult.setTimeRuleVO(timeRuleVO);
		Map<String, TimeZone> psnjobTimeZoneMap = new HashMap<String, TimeZone>();
		//��ȡ���ݼ�ʱ���н��������п��ܵĽ����¼������ǰ�üٺ��û���д������Ѿ��������ź�˳��
//		Map<String, List<LeaveBalanceVO>> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, bvos);
		Map<String, List<LeaveBalanceVO>> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, toSplitVOs);
		LeaveBalanceVO[] balanceVOs = MapUtils.isEmpty(balanceMap)?null:(CollectionUtils.isEmpty(balanceMap.get(pk_psnorg))?null:balanceMap.get(pk_psnorg).toArray(new LeaveBalanceVO[0]));
		//�˴����ݼ����ݲ�Ӧ�ð����Ѿ����������
		for(LeaveBalanceVO bvo:balanceVOs){
			if(bvo.isSettlement()){
				balanceVOs = (LeaveBalanceVO[]) ArrayUtils.removeElement(balanceVOs, bvo);
			}
		}
		if(ArrayUtils.isEmpty(balanceVOs)){
			balanceVOs = null;
			throw new BusinessException(ResHelper.getString("6017hrta","06017hrta0071")
/*@res "û�п��õ��ݼ����ݣ����ݼ������ѽ��㣡"*/);
		}
		LeaveBalanceVO[] balanceVOsClone =SplitLeaveBillHelper.clone(balanceVOs);//�������¼����һ��,��Ϊ������Ҫ�ý����¼�Ľ���ʱ������Ϣ
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
		//���û���κβ��¼���������ֱ�ӷ���
		if(!tempResult.isRealSplit()){
			LeavehVO hVO = aggVO.getHeadVO();
			splitResult.setSplit(false);
			AggLeaveVO[] splitVOs = new AggLeaveVO[]{SplitLeaveBillHelper.syncBodyToHeadAndCreateAggVO(tempResult.getSplitVOs(), hVO)};
			SplitLeaveBillHelper.syncRestLength(splitVOs, balanceVOsClone, leaveTypeVOMap);
			splitResult.setSplitResult(splitVOs);
			return splitResult;
		}
		//�в��¼��������𵥲�һ����һ����൥���п��ܵ��ӵ�����û�䣬���ݼ��������ڼ���ˣ�Ҳ��Ϊ�𵥣�����Ҫ������ĵ�����һ�¹鲢
		splitResult.setSplit(true);
		AggLeaveVO[] splitVOs = SplitLeaveBillHelper.groupLeavebVOs(aggVO.getHeadVO(), tempResult.getSplitVOs(), leaveTypeVOMap, canOverYear!=null&&canOverYear.booleanValue());
		SplitLeaveBillHelper.processVOStatus(aggVO, splitVOs);
		SplitLeaveBillHelper.syncRestLength(splitVOs, balanceVOsClone, leaveTypeVOMap);
		splitResult.setSplitResult(splitVOs);
		return splitResult;
	}


	public static SplitBillResult<AggLeaveVO> split(AggLeaveVO aggVO) throws BusinessException{
		LeavehVO leavehVO = aggVO.getLeavehVO();
		if(leavehVO.getIslactation()!=null&&leavehVO.getIslactation().booleanValue()){//����ٲ�ִ�в�
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
	 * ��һ���Ǽǵ�ִ�в𵥲���
	 * ���δִ�в𵥣��򷵻�null�����򷵻ز𵥽��
	 * @param regVO
	 * @return
	 * @throws BusinessException
	 */
	public static SplitBillResult<LeaveRegVO> split(LeaveRegVO regVO) throws BusinessException{
		SplitBillResult<LeaveRegVO> splitResult = new SplitBillResult<LeaveRegVO>();
		splitResult.setOriginalBill(regVO);
		if(regVO.getIslactation()!=null&&regVO.getIslactation().booleanValue()){//����ٲ�ִ�в�
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
		//����п�ҵ��Ԫ���ݼ٣����п�
		LeaveRegVO[] oriVOs = new LeaveRegVO[]{regVO};
		LeaveRegVO[] toSplitVOs = BillMethods.compareAndCutDate(pk_org, oriVOs);
		//����и�Ľ��Ϊ�գ���ʾ���������뿼�ڵ���û���κν��������������Ӧ���׳��쳣
		if(ArrayUtils.isEmpty(toSplitVOs)){
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0247")
					/*@res "�ݼ�ʱ�䷶Χ���ڿ��ڵ�����Χ��!"*/);
		}
		tempResult.setToSplitVOs(toSplitVOs);
		tempResult.setOriginalToSplitVOs(oriVOs);
		tempResult.setLeaveTypeVOMap(leaveTypeVOMap);
		tempResult.setOriginalLeaveTypeCopyVO(leaveTypeVOMap.get(pk_leavetype));
		tempResult.setCanOverYear(timeRuleVO.isCanSaveOveryear());
		tempResult.setTimeRuleVO(timeRuleVO);
		Map<String, TimeZone> psnjobTimeZoneMap = new HashMap<String, TimeZone>();
		//��ȡ���ݼ�ʱ���н��������п��ܵĽ����¼������ǰ�üٺ��û���д������Ѿ��������ź�˳��
		Map<String, List<LeaveBalanceVO>> balanceMap = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, new LeaveCommonVO[]{regVO});
		LeaveBalanceVO[] balanceVOs = MapUtils.isEmpty(balanceMap)?null:(CollectionUtils.isEmpty(balanceMap.get(pk_psnorg))?null:balanceMap.get(pk_psnorg).toArray(new LeaveBalanceVO[0]));
		LeaveBalanceVO[] balanceVOsClone =SplitLeaveBillHelper.clone(balanceVOs);//�������¼����һ��,��Ϊ������Ҫ�ý����¼�Ľ���ʱ������Ϣ
		tempResult.setBalanceVOs(balanceVOs);
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		CalParam4OnePerson leaveLengthCalParam = BillProcessHelperAtServer.initParam(regVO.getPk_psndoc(),
				timeRuleVO, BillMutexRule.createBillMutexRule(timeRuleVO.getBillmutexrule()),
				aggShiftMap,CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap),
				new LeaveRegVO[]{regVO},
				BillMutexRule.BILL_LEAVE,2,2);
		processSplit(tempResult,leaveLengthCalParam,psnjobTimeZoneMap);
		//���û���κβ��¼���������ֱ�ӷ���
		if(!tempResult.isRealSplit()){
			splitResult.setSplit(false);
			SplitLeaveBillHelper.syncRestLength(tempResult.getSplitVOs(), balanceVOsClone, leaveTypeVOMap);
			splitResult.setSplitResult(tempResult.getSplitVOs());
			return splitResult;
		}
		//�в��¼��������𵥲�һ����һ����൥���п��ܵ��ӵ�����û�䣬���ݼ��������ڼ���ˣ�Ҳ��Ϊ�𵥣�����Ҫ������ĵ�����һ�¹鲢
		splitResult.setSplit(true);
		SplitLeaveBillHelper.processVOStatus(regVO, tempResult.getSplitVOs());
		SplitLeaveBillHelper.syncRestLength(tempResult.getSplitVOs(), balanceVOsClone, leaveTypeVOMap);
		splitResult.setSplitResult(tempResult.getSplitVOs());
		return splitResult;
	}

	/**
	 * ���������ɵ��ݼ�����/�Ǽǽ��в𵥲���
	 * @param <T>
	 * @param pk_org
	 * @param appDate,�������ڣ��������뵥ʱ�����壬�����Ǽǵ�ʱΪnull
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
		//����Щ�ݼټ�¼����Ա��֯��ϵ���飬�ٰ���Ա��֯��ϵѭ������
		Map<String, T[]> psnorgGrpMap = CommonUtils.group2ArrayByField(LeaveCommonVO.PK_PSNORG, batchVOs);
		//����Ȼ�˷��飬��Ϊ�����ݼټ������CalParam4OnePerson��ʱ����Ҫ�Ѵ˴���Ȼ�˵������ݼټ�¼������ȥ
//		Map<String, T[]> psndocGrpMap = CommonUtils.group2ArrayByField(LeaveCommonVO.PK_PSNDOC, batchVOs);
		LeaveComparator<T> comparator = new LeaveComparator<T>(sortedTypeVOs);
		ILeaveBalanceManageService leaveBalanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
		Map<String, List<LeaveBalanceVO>> balanceMap = leaveBalanceService.queryAndCalLeaveBalanceVOBatchForPreHoliday(pk_org, dependMap, leaveTypeVOMap, timeRuleVO, batchVOs);
		Map<String, TimeZone> psnjobTimeZoneMap = BillMethods.getPsnjobTimeZoneMap(StringPiecer.getStrArrayDistinct(batchVOs, LeaveCommonVO.PK_PSNJOB), timeRuleVO.getTimeZoneMap());// ��֯ʱ��Map
		List<T> retList = new ArrayList<T>();//���ڴ洢���ص������list
		for(String pk_psnorg:psnorgGrpMap.keySet()){//����Ա��֯��ϵѭ������
			T[] psnBatchVOs = psnorgGrpMap.get(pk_psnorg);//���˴˴��������������ļ�¼
			String pk_psndoc = psnBatchVOs[0].getPk_psndoc();
			Arrays.sort(psnBatchVOs, comparator);
			CalParam4OnePerson leaveLengthCalParam = paramMap.get(pk_psndoc);
			//�洢ĳһ����Ա��֯��ϵ��ѭ������������ʱ��ʱ�����µĽ��������map(��Ϊ�����Ĺ����У�����ʱ������ͬ�������ݿ��ܵģ�����Ҫ���ڴ���ͬ��)
			//<pk_timeitem+���[+�·�]+leaveindex�������¼>
			Map<String,LeaveBalanceVO> newestBalanceVOMap = new HashMap<String, LeaveBalanceVO>();
			for(T batchVO:psnBatchVOs){
				LeaveTypeCopyVO typeVO = leaveTypeVOMap.get(batchVO.getPk_timeitem());
				LeaveBalanceVO[] balanceVOs = MapUtils.isEmpty(balanceMap)?null:(CollectionUtils.isEmpty(balanceMap.get(batchVO.getPk_psnorg()))?null:balanceMap.get(batchVO.getPk_psnorg()).toArray(new LeaveBalanceVO[0]));
				//����һ���˿����ж����ݼ�ʱ�Σ���������ѯ�Ľ����¼�е�ʱ�������Ѿ���ǰ����ݼټ�¼���ĵ�һЩ�ˣ���Ҫ��֮ǰ�Ľ����¼��ͬ��
				SplitLeaveBillHelper.syncFromNewestBalanceVO(newestBalanceVOMap, balanceVOs);
				LeaveBalanceVO[] balanceVOsClone =SplitLeaveBillHelper.clone(balanceVOs);//�������¼����һ��,��Ϊ������Ҫ�ý����¼�Ľ���ʱ������Ϣ
				TemporarySplitResult<T> tempResult = new TemporarySplitResult<T>();//���ڰ�װ�˴μ����������ݵ�tempresult
				tempResult.setApplyDate(appDate);
				tempResult.setBalanceVOs(balanceVOs);
				tempResult.setCanOverYear(timeRuleVO.isCanSaveOveryear());//����ȵ����Ƿ���Ա������
				tempResult.setLeaveTypeVOMap(leaveTypeVOMap);
				tempResult.setOriginalLeaveTypeCopyVO(typeVO);
				T[] toSplitVOs = SplitLeaveBillHelper.createOneElementArray(batchVO);//��Ҫ���в𵥵ļ�¼����װ�ɳ���Ϊ1�����飬�������
				tempResult.setOriginalToSplitVOs(toSplitVOs);
				tempResult.setToSplitVOs(toSplitVOs);
				tempResult.setTimeRuleVO(timeRuleVO);
				processSplit(tempResult, leaveLengthCalParam, psnjobTimeZoneMap);//ִ�в�
				SplitLeaveBillHelper.syncRestLengthForBatch(tempResult.getSplitVOs(), balanceVOsClone, leaveTypeVOMap);
				retList.addAll(Arrays.asList(tempResult.getSplitVOs()));
			}
		}
		return retList.toArray((T[]) Array.newInstance(batchVOs.getClass().getComponentType(), 0));
	}

	/**
	 * ���в𵥡��˷����ǵ���������������һ��Ҫôֻ����һ�����뵥��Ҫôֻ����һ�ŵǼǵ���Ҫôֻ������������ʱ��һ����¼
	 * @param temporarySplitResult
	 * @param leaveTypeVOMap
	 * @param balanceVOs
	 * @param <T>
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> void processSplit(
			TemporarySplitResult<T> temporarySplitResult,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap//pk_psnjob��Ӧ��ʱ����map,<pk_psnjob,TimeZone>
			) throws BusinessException{
		LeaveBalanceVO[] balancedVOs = temporarySplitResult.getBalanceVOs();
		if(ArrayUtils.isEmpty(balancedVOs))
			return;
		//��Ҫ�𵥵������Ȱ���ʼʱ������
		TimeScopeUtils.sort(temporarySplitResult.getToSplitVOs());
		Map<String, LeaveTypeCopyVO> leaveTypeVOMap = temporarySplitResult.getLeaveTypeVOMap();
		//����leavebalance����Чʱ�䷶Χ
		Map<LeaveBalanceVO, ITimeScope> effectiveScopeMap = SplitLeaveBillHelper.createBalanceVOEffectiveScopeMap(balancedVOs, leaveLengthCalParam);
		temporarySplitResult.setBalanceVOEffectiveScopeMap(effectiveScopeMap);
		//����ǰ�ü�+�������Ľ���ʱ����Ҫ�ַ��س��Բ�.˳��Ϊ��
		//�����ڽ��������ΪY�����ߣ������������N,���û�û��¼�����/�ڼ䣩����˳��Ϊ����ǰ�ü٣����ڱ���𣬵���ǰ�ü٣����ڱ����
		//�����������ΪN�����û�¼�������/�ڼ䣬����Ҫ���û�¼������+�ڼ�Ľ���������ǰ�ᣬ�ᵽ��һ���û�¼������Ľ�������֮ǰ
		for(int i=0;i<balancedVOs.length;i++){
			if(ArrayUtils.isEmpty(temporarySplitResult.getToSplitVOs()))//toSplitVOsΪ�գ���ʾ�Ѿ������
				return;
			LeaveBalanceVO balanceVO = balancedVOs[i];
			if(balanceVO.isSettlement())//����Ѿ����㣬���ô���
				continue;
			//������ڴ����/�ڼ���֧�ֵ��ݼٵ�ʱ�䷶Χ����������Ч���ӳ����������ұ�֤��һ�������İ��ʱ�β��ᱻ�и�����ڼ������ʱ�εĿ�ʼʱ��/����ʱ�䲻������һ��������ε��м䣩
			ITimeScope effectivePeriodTimeScope = effectiveScopeMap.get(balanceVO);
			//�����ʱ�䷶Χ���ݼ�ʱ��û�н���������continue��intersectionScopes�Ǵ�����ڴ����/�ڼ���֧�ֵ��ݼٵ����Χ
			T[] intersectionScopes = TimeScopeUtils.intersectionTimeScopesRemainsOriType(effectivePeriodTimeScope, temporarySplitResult.getToSplitVOs());
			if(ArrayUtils.isEmpty(intersectionScopes))
				continue;
			temporarySplitResult.setToSplitVOsForOneBalanceVO(intersectionScopes);//��leavebalancevo��Ҫ��ͼ֧�ֵ��ݼٶ�
			temporarySplitResult.setSplitVOsForOneBalanceVO(null);
			String pk_leavetype = balanceVO.getPk_timeitem();
			LeaveTypeCopyVO leaveTypeCopyVO = leaveTypeVOMap.get(pk_leavetype);
//			double usefulRestLen = balanceVO.getUsefulRestDayOrHour();//�������ڼ�Ŀ��ý���ʱ�����ɽ���ʱ����ȥ����ʱ��
			//2013-07-16 ������ʱ����0.33�����ǿ��ڹ�����ȡ0λС��������ȡ�����ᵼ�²𵥵�ʱ��Ϊ0�����ڴ˸��ݿ��ڹ��������ʱ����ֹ���ֲ�
			double usefulRestLen = leaveLengthCalParam.timeruleVO.getTimeRuleUFDouble(balanceVO.getUsefulrestdayorhour()).doubleValue();//�������ڼ�Ŀ��ý���ʱ�����ɽ���ʱ����ȥ����ʱ��
			int timeitemUnit = leaveTypeCopyVO.getTimeItemUnit();//ʱ����λ���컹��Сʱ��
			boolean isDayUnit = timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_DAY;
			double timeUnit0 = leaveTypeCopyVO.getTimeUnit();//��Сʱ��������30���ӣ�0.5�졣���Ϊ0����˵������Сʱ��Ҫ��ʱ��������Ƕ��پ��Ƕ��٣�����ȡ��
			double timeUnit = isDayUnit?timeUnit0:timeUnit0/60;//�������ӼƵ���Сʱ��ת��Ϊ��Сʱ��
			//ֻ���ڽ���ʱ�������ٱ�֤һ����Сʱ����ʱ�򣬲��ܲ�
			//����ʱ���п����ṩ�����ʱ��������Сʱ��Ϊ0�������ȫ���ṩ������ֻ���ṩ��Сʱ�������������Ҳ���������ʱ��
			double maxAffordLen = timeUnit==0?usefulRestLen:Math.floor(usefulRestLen/timeUnit)*timeUnit;
			//�𵥲���
			processSplit(balanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap);
		}
		//��һ��ѭ������֮���������ʱ��û��֧�֣��������
		postProcessSplit(temporarySplitResult, leaveLengthCalParam, psnjobTimeZoneMap);
	}
	/**
	 * �ڽ����˵�һ�ֵ�ѭ������֮���������δ��֧�ֵ�ʱ�Σ���Ҫ��ͼ����Щʱ�����û�¼����������������ǰ�����û�¼�����𲻿��ƣ����߲��ϸ����ʱ��
	 * ����֮���û�¼������϶�ʱ������
	 * @param <T>
	 * @param temporarySplitResult
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> void postProcessSplit(
			TemporarySplitResult<T> temporarySplitResult,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap//pk_psnjob��Ӧ��ʱ����map,<pk_psnjob,TimeZone>
			) throws BusinessException{
		T[] toSplitVOs = temporarySplitResult.getToSplitVOs();
		//ѭ������֮�����е��ݼ�ʱ����������������ֱ�ӷ���
		if(ArrayUtils.isEmpty(toSplitVOs))
			return;
		//���ѭ������֮����δ��֧�ֵ��ݼ�ʱ��������Щʱ��Ҫ��ͼ���û�¼�������Ͽ�--ǰ�����û�¼�������ǲ��ϸ����ʱ���ġ�
		//��Щδ��֧�ֵ�ʱ�����γɵ�ԭ���ж��֣��������ݼ�ʱ�����ڵ����/�ڼ�Ľ����¼�Ѿ������㣬������Υ�����������ڳ��޲����������ǽ���ʱ������
		//���û�¼�������ϸ����ʱ������ôֱ�����쳣����Ϊѭ���Ĺ��̿��Ա�֤������Ѿ��߾�ȫ���ṩʱ���ˣ������ѭ���ﲻ��֧�ֵģ�������������֧��
		//���û�¼���������ʱ���������ϸ񣬻��߸ɴ಻����ʱ��������һ��ϣ����������Щʣ���ʱ��
		//�����쳣����Ҫ�򵥵��׳���Ҫ����һ��ԭ���Ҹ�֪�û��������û������ݼ�ʱ��
		LeaveTypeCopyVO oriType = temporarySplitResult.getOriginalLeaveTypeCopyVO();
		boolean isStrictLimit = oriType.isLeaveLimit()&&oriType.isRestrictLimit();
		String pk_psndoc = toSplitVOs[0].getPk_psndoc();
		Map<LeaveBalanceVO, ITimeScope> effectiveScopeMap = temporarySplitResult.getBalanceVOEffectiveScopeMap();
		//���棬
		//���ڲ��ϸ���ƻ��߲�����ʱ���ģ�����Щ��û֧�ֵ�ʱ���������û�¼��������Ѱ��֧�֣���֧�־�֧�֣�����֧�־͵���һ��leavebalance��¼
		//�����ϸ����ʱ���ģ�ֻ�����쳣ԭ�����������Ѱ�����û�¼��������Ѱ��֧��
		LeaveBalanceVO[] balancedVOs = temporarySplitResult.getBalanceVOs();
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(toSplitVOs[0].getPk_psnjob(), psnjobTimeZoneMap);
		for(int i=0;i<balancedVOs.length;i++){
			if(ArrayUtils.isEmpty(temporarySplitResult.getToSplitVOs()))//toSplitVOsΪ�գ���ʾ�Ѿ������
				return;
			LeaveBalanceVO balanceVO = balancedVOs[i];
			if(!balanceVO.getPk_timeitem().equals(oriType.getPk_timeitem()))//��������û�¼���������ô���
				continue;
			ITimeScope effectiveScope = effectiveScopeMap.get(balanceVO);
			//�����ʱ�䷶Χ���ݼ�ʱ��û�н���������continue��intersectionScopes�Ǵ�����ڴ����/�ڼ���֧�ֵ��ݼٵ����Χ
			T[] intersectionScopes = TimeScopeUtils.intersectionTimeScopesRemainsOriType(effectiveScope, temporarySplitResult.getToSplitVOs());
			if(ArrayUtils.isEmpty(intersectionScopes))
				continue;
			if(balanceVO.isSettlement()){//����Ѿ����㣬������������ռ���ཻ����˵���ཻ�Ĳ����Ѿ������ܱ�֧���ˣ�ֻ�����쳣
				//����н������ҽ����뱾balancevo�Ķ�ռ���н������ұ�balancevo�Ѿ����㣬�����쳣
				ITimeScope[] exclusionScopes = SplitLeaveBillHelper.getExclusionScopes(balanceVO, temporarySplitResult, true);
				if(TimeScopeUtils.isCross(exclusionScopes, intersectionScopes)){
					String psnName = getPsnName(pk_psndoc);
					String period = balanceVO.getCuryear()+(oriType.isSetPeriodYearORDate()?"":balanceVO.getCurmonth());
					String periodName = oriType.isSetPeriodYearORDate()?PublicLangRes.YEAR2():PublicLangRes.PERIOD();
					throw new BusinessException(ResHelper.getString("6017leave","06017leave0226"
/*@res "��Ա{0}��{1}{2}��{3}�Ѿ�����!"*/, psnName,period,periodName,oriType.getMultilangName()));
				}
				if(!isStrictLimit)
					continue;//����������ռ�����ཻ�����п��ܱ�������leavebalance֧��
			}
			for(T toSplitVO:intersectionScopes){
				toSplitVO.setPk_timeitem(oriType.getPk_timeitem());
				toSplitVO.setPk_leavetypecopy(oriType.getPk_timeitemcopy());
				toSplitVO.setLeaveyear(balanceVO.getCuryear());
				toSplitVO.setLeavemonth(balanceVO.getCurmonth());
				toSplitVO.setLeaveindex(balanceVO.getLeaveindex());
			}
			//����н����������û��Υ�������ճ��ڵĹ涨�Ļ���Ӧ���ǿ���֧�ֵ�
			if(temporarySplitResult.applyDate!=null){
				for(T toSplitVO:intersectionScopes){
					checkApplyDatePara(toSplitVO, temporarySplitResult.applyDate, timeZone, oriType, true);//ִ�������ճ���У��
				}
			}
			if(isStrictLimit){
				//�ߵ����������ϸ����ʱ������϶���ʱ��������
				String psnName = getPsnName(pk_psndoc);
				throw new BusinessException(ResHelper.getString("6017leave","06017leave0225"
	/*@res "��Ա{0}���ݼ�ʱ��������{1}�Ľ���ʱ��!"*/, psnName,oriType.getMultilangName()));
			}
			//����ǲ��ϸ���ƻ��߲����ƣ���Ѱ����֧��
			BillProcessHelperAtServer.calLeaveLength4OnePerson(oriType.getPk_org(), pk_psndoc, intersectionScopes, oriType, leaveLengthCalParam);
			//�ߵ����˵���������뵥�����������뵥������������û�г��ޣ�����ȫ��֧����Щʱ�Ρ�֧�ֵ�ʱ�򣬲��Ǽ򵥵ذ���Щʱ�ηŵ�splitVOs���棬
			//����Ҫ�������뱾leavebalance�Ѿ���֧�ֵ�ʱ�κϲ�������Ч�����ܸ��ã������û�����ú���֣�ͬһ���ͬһ�ڼ�������ݼټ�¼��ʱ���ǽ��ŵ�
			T[] mergedSplitVOs = SplitLeaveBillHelper.mergeScopesByBalanceVO(temporarySplitResult.getSplitVOs(), intersectionScopes, temporarySplitResult.isCanOverYear(), balanceVO, oriType, leaveLengthCalParam, timeZone);
			temporarySplitResult.setSplitVOs(mergedSplitVOs);
			temporarySplitResult.setToSplitVOs(SplitLeaveBillHelper.minus(temporarySplitResult.getToSplitVOs(), intersectionScopes, timeZone));
		}
		//�ߵ�����������û�б�֧�ֵ�ʱ�Σ���ֱ�����쳣���ɣ���Ϊ�Ѿ�û�п��ܱ�֧���ˡ�
		if(!ArrayUtils.isEmpty(temporarySplitResult.getToSplitVOs())){
			String psnName = getPsnName(pk_psndoc);
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0227"
/*@res "��Ա{0}���ݼ�ʱ�β��ܱ�֧��!"*/, psnName));
		}
	}

	private  static <T extends LeaveCommonVO> void processSplit0(
			LeaveBalanceVO curProcessBalanceVO,//��ǰ����Ľ����¼
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			boolean splitBreakAppDateRule,//�Ƿ�Ҫ���������ճ��ڲ�У��
			boolean splitBreakOverYearRule//�Ƿ�Ҫ�����ݼٵ������
			) throws BusinessException{
		T[] curProecessScopes = temporarySplitResult.getToSplitVOsForOneBalanceVO();
		//������е��ݼ�ʱ�ζ��ɹ��ر����ˣ������Ѿ�û�������ṩ�ݼ�ʱ���ˣ���ݹ����
		if(ArrayUtils.isEmpty(curProecessScopes)||maxAffordLen<0.00001)
			return;
		//��ǰ������ݼ����
		LeaveTypeCopyVO curProcessLeaveTypeCopyVO = temporarySplitResult.getLeaveTypeVOMap().get(curProcessBalanceVO.getPk_timeitem());
		//��ʣ�µ�ʱ�Σ���Ҫ���ν�������У�飺�������ڳ�����ʼ���ڶ������У��(ֻ�����뵥)���ݼ�ʱ���Ƿ���Կ���ȱ����У��
//		T vo = temporarySplitResult.getToSplitVOs()[0];
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(curProecessScopes[0].getPk_psnjob(), psnjobTimeZoneMap);
		if(splitBreakAppDateRule){
			//���Ƚ����������ڵ�У��,��ʱ��̫��������Υ�����ݼ����涨���ݼ�ʱ��β����
			T[][] splitVO = SplitLeaveBillHelper.splitTimeScopeByApplyDatePara(curProecessScopes,temporarySplitResult.getApplyDate(),timeZone,curProcessLeaveTypeCopyVO);
			//��splitVO[0]��Ϊnull����ʾvo����Υ���������ڳ��ڵ�ʱ��Σ��ѱ�����splitVO[0]�У�Ҫ����ʱ��δ�toSplitVOs����ʱ�۳�,��ʣ�µ�toSplitVOs���зֵ���������ټӻ�ȥ
			if(!ArrayUtils.isEmpty(splitVO[0])){
				temporarySplitResult.setToSplitVOsForOneBalanceVO(splitVO[1]);//
				processSplit0(curProcessBalanceVO,  temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap,false,splitBreakOverYearRule);
				temporarySplitResult.setToSplitVOsForOneBalanceVO(SplitLeaveBillHelper.reviveToArray(temporarySplitResult.getToSplitVOsForOneBalanceVO(), splitVO[0]));//��ʱ�۳��ļӻ�ȥ
				return;
			}
			processSplit0(curProcessBalanceVO, temporarySplitResult,  maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, false,splitBreakOverYearRule);
			return;
		}
		if(splitBreakOverYearRule){
			//���棬���ǲ�����������ݼٵ��Ƿ������桱�������������Ҫ���������飺1.����ȵ��ݼ�ʱ�β�ֿ���2.ÿһ���ݼ�ʱ���ϼ���������Ȼ��ȣ�������ͬ��Ȼ��ȵ��ݼ�ʱ�β�������ͬһ�����뵥
			T[] splitVO = SplitLeaveBillHelper.splitTimeScopeByOverYearPara(curProecessScopes, timeZone, leaveLengthCalParam);
			if(splitVO.length==curProecessScopes.length){//�������Ȼ��Ȳ�ֺ󣬳���û�䣬��ʾ����Ҫ���
				processSplit0(curProcessBalanceVO, temporarySplitResult,  maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, splitBreakAppDateRule,false);
				return;
			}
			//��ֺ󳤶ȴ���1�����ʾ�Ѿ�����Ȼ��Ƚ����˲�֣���Ҫ����ֺ��ʱ���������õ�tosplitvo��
			temporarySplitResult.setToSplitVOsForOneBalanceVO(splitVO);
			processSplit0(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, splitBreakAppDateRule,false);
			return;
		}
		//�ߵ������ʾtoSplitVOsForOneBalanceVO������ʱ��ε��������ڲ����ޣ��Ҳ�Υ���Ƿ����������������Է��ĵؽ�������ĵ�ҵ��---����
		//���濴toSplitVOsForOneBalanceVO��ʱ�����Ƿ񳬹��˿����ṩ�����ʱ������û�����������ֱ�Ӱ�����toSplitVOsForOneBalanceVOŲ��splitVOsForOneBalanceVO
		//��ȥ���������ˣ���Ҫ���ݿ��ṩ�����ʱ�����ƽ���ʱ��
		for(T vo:curProecessScopes){
			vo.setPk_leavetype(curProcessLeaveTypeCopyVO.getPk_timeitem());
			vo.setPk_leavetypecopy(curProcessLeaveTypeCopyVO.getPk_timeitemcopy());
			vo.setLeaveyear(curProcessBalanceVO.getCuryear());
			vo.setLeavemonth(curProcessBalanceVO.getCurmonth());
			vo.setLeaveindex(curProcessBalanceVO.getLeaveindex());
		}
		BillProcessHelperAtServer.calLeaveLength4OnePerson(curProcessBalanceVO.getPk_org(),
				curProcessBalanceVO.getPk_psndoc(),curProecessScopes , curProcessLeaveTypeCopyVO, leaveLengthCalParam);
		//�˶�ʱ��εĳ��ȣ���ʱ��γ���<=������ڼ�����ṩ�ĳ��ȣ���ֱ�Ӵ�toSplitVOs����Ų��splitVOs����
		double calLength = SplitLeaveBillHelper.sumLength(curProecessScopes);
		if(calLength<=maxAffordLen){
			temporarySplitResult.setToSplitVOsForOneBalanceVO(null);
			temporarySplitResult.setSplitVOsForOneBalanceVO(curProecessScopes);
			curProcessBalanceVO.setRealdayorhour(new UFDouble(curProcessBalanceVO.getRestDayOrHourValue()-calLength));//Ҫ�������ʱ���ӽ�������ʱ�۳�����Ϊ����������������£���BalanceVO�����ں��滹�ᱻ�õ���������۳������ᵼ�¼������
			return;
		}
		//����Щ�ݼ�ʱ�εĳ���>������ڼ�����ṩ�ĳ��ȣ���ֻ��֧����Щ�ݼ�ʱ���е�һ���֡�����֧����һ���֣����н����ģ�
		processSplitLengthNotEnough(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap);
	}

	/**
	 * ĳ������ڼ����ʱ������������£����ֵ�֧���ݼ�ʱ���
	 * ��ʱ������ݼ�ʱ�ζ�û��Υ�������ճ��ڡ�����ȱ���涨�����Է��ĵش���
	 * ���ڽ���ʱ��������ֻ��֧��һ�����ݼ�ʱ��������֧����һ���֣����н����ģ�
	 * ԭ���ǣ�����֧�֡���ռ�������ݼ�ʱ��
	 * "��ռ��"����˼�ǣ�������ڼ���֧�֣���ѭ������������ڼ��Ѿ�������֧�ֵ�����Ϊ�˲�ʹ�㷨���ڸ��ӣ�����Լ�����£�
	 * ��ʱ�η�Ϊ���֣���ȫ�ڶ�ռ������ģ������ڶ�ռ����ģ���ȫ�ڶ�ռ��֮���
	 * �����ô������ҵ�˳�����һ�֣���εڶ��֣����������Ҵ��������
	 *
	 * �ڶ��ֵĴ���ʽ��ӣ����ȿ��Ƿ���ȫ��֧�֣�����ܣ���ð죬ȫ��֧�ּ��ɡ�������ȫ��֧�֣���
	 * ���ݼ�ʱ�����ռ�����н��д����и�Ϊ��ռ���ڵ�ʱ��scopes1�Ͷ�ռ�����ʱ��scopes2
	 * ������ȫ��֧��scopes1���������ǰ�ü���𣬻��� �ϸ����ʱ�������쳣
	 *                           ������û�¼�������ң�������ʱ��||���ϸ����ʱ��������scopes1����Ϊ�������ڼ䣬���أ���scopes2����֧�֣������ң�����ǲ��ϸ����ʱ������Ҫ��ʾ�û�����
	 * ������ȫ��֧��scopes1,�����ν�scopes2�е�ʱ�κϲ���scopes1��ȥ�����ϲ���ʱ�������ޣ����������֧�ִ�scopes2�е�ʱ�Ρ����ϲ������ˣ���Ҫ
	 * ����ĳ�ֹ���𵥣����ݺϲ��ķ����п����Ǵ������Ҳ�Ҳ�п����Ǵ�������𡣾���ķ�ʽ������
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
			LeaveBalanceVO curProcessBalanceVO,//��ǰ����Ľ����¼
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap
			) throws BusinessException{
		//���ȵõ�������ڼ��"��ռ��"
		ITimeScope[] exclusionScopes = SplitLeaveBillHelper.getExclusionScopes(curProcessBalanceVO, temporarySplitResult,false);
		processSplitLengthNotEnoughComplexly(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, exclusionScopes);
	}


	/**
	 * ����ʱ�������������е��ݼ�ʱ��ʱ�����ӵĴ���ʽ��Ҫ�ۺ��жϸ���֧����Щʱ�Σ��Լ��ô�������֧�֣����Ǵ�������֧��
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
			LeaveBalanceVO curProcessBalanceVO,//��ǰ����Ľ����¼
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			ITimeScope[] exclusionScopes//�˽����¼�Ķ�ռ��
			) throws BusinessException{
		//���Ƚ���֧�ֵ��ݼ�ʱ�η�Ϊ���ࣺ��ȫ�ڶ�ռ����ģ������ڶ�ռ����ģ���ȫ�ڶ�ռ����ģ���һ��͵����඼���Դ�������֧�֣��ڶ�����Ҫ��һ���ж�
		T[][] groupVOs = SplitLeaveBillHelper.groupScopesByExclusionScopes(temporarySplitResult.getToSplitVOsForOneBalanceVO(), exclusionScopes);
		List<T> splitVOList = new ArrayList<T>();

		String errMsg = ResHelper.getString("6017leave","06017leave0228")
/*@res "��Ա{0}��{1}��{2}���ݼ�ʱ�ε�ʱ��������{3}�Ŀ���ʱ��!"*/;
		//����֧����ȫ�ڶ�ռ������ġ�
		T[] inExclusionScopes=groupVOs[0];//��ȫ�ڶ�ռ����ģ���������leavebalance��¼�Ѿ�ȷ���޷�֧�ֵ�
		if(!ArrayUtils.isEmpty(inExclusionScopes)){
			maxAffordLen = processSplitLengthNotEnoughInExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, inExclusionScopes, splitVOList, errMsg);
		}
		//Ȼ�������ڶ�ռ���ڣ������ڶ�ռ������ݼ�ʱ��
		T[] partInExclusionScopes=groupVOs[1];//�����ڶ�ռ����ģ�������leavebalance��¼�Ѿ����Բ���֧�ֵ�
		if(!ArrayUtils.isEmpty(partInExclusionScopes)){
			maxAffordLen = processSplitLengthNotEnoughPartInExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, exclusionScopes, partInExclusionScopes, splitVOList, errMsg);
		}
		//�������ȫ�ڶ�ռ��֮��Ĳ���
		T[] outExclusionScopes=groupVOs[2];//��ȫ�ڶ�ռ����ģ�leavebalance��¼������ȫ֧�ֵ�
		if(!ArrayUtils.isEmpty(outExclusionScopes)){
			maxAffordLen = processSplitLengthNotEnoughOutExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, outExclusionScopes, splitVOList, errMsg);
		}
		//�����ߵ�������Ա�֧�ֵ�ʱ�ζ�������splitVOList����
		if(splitVOList.size()>0){
			TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(splitVOList.get(0).getPk_psnjob(), psnjobTimeZoneMap);
			temporarySplitResult.setSplitVOsForOneBalanceVO(splitVOList.toArray((T[]) Array.newInstance(temporarySplitResult.getToSplitVOsForOneBalanceVO().getClass().getComponentType(), splitVOList.size())));
			temporarySplitResult.setToSplitVOsForOneBalanceVO(SplitLeaveBillHelper.minus(temporarySplitResult.getToSplitVOsForOneBalanceVO(), temporarySplitResult.getSplitVOsForOneBalanceVO(), timeZone));
		}
	}

	/**
	 * ������ȫ�ڶ�ռ��֮����ݼ�ʱ��
	 * ����˼�����������У���֧�ֶ���֧�ֶ���
	 * @param <T>
	 * @param curProcessBalanceVO
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @param outExclusionScopes
	 * @param splitVOList
	 * @param errMsg
	 * @return ֧�����֮�󣬻�ʣ�µĿ�֧�ֵ�ʱ��
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> double processSplitLengthNotEnoughOutExclusionScopes(
			LeaveBalanceVO curProcessBalanceVO,//��ǰ����Ľ����¼
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			T[] outExclusionScopes,//�ݼټ�¼�У���ȫ�ڶ�ռ�����ʱ��
			List<T> splitVOList,//������֧�ֵĲ��ַ����list
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
			//�����ߵ����˵����ǰ����ݼ�ʱ�ε�ʱ���Ѿ�����ȫ����֧���ˡ��������ڶ�ռ���⣬��˻��к�����leavebalance��¼����֧�֣���˴˴�
			//�߾�����֧�ֵ�����������
			if(maxAffordLen==0)
				break;
			//�����ݼ�ʱ�������㷨���������֧�ֵ��ݼ�ʱ��Σ�����֧�ֵ��ڷ�������ĵ�һ��Ԫ�أ�����֧�ֵ��ڵڶ���Ԫ�أ�
			T[] splitVOs = inverseCalculate(vo, typeVO, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap,true);
			T canAffordScope = splitVOs[0];//����֧�ֵ�ʱ��
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
	 * ������ȫ�ڶ�ռ��֮�ڵ��ݼ�ʱ��
	 * ����˼�룺
	 * ����ܹ���ȫ֧�֣�����ȫ֧��
	 * ���������ȫ֧�֣�����ǰ��leavebalance���û�¼�������ң�������ʱ��||���ϸ����ʱ������Ҳ����ȫ֧�֣���ʹ�ó���ʱ����
	 * ���������ȫ֧�֣����û�¼�������ϸ����ʱ���������쳣��(��Ϊ�Ѿ�û��leavebalance��¼����֧����)
	 * ���������ȫ֧�֣��ҵ�ǰ��leavebalance�����û�¼���������û�¼�����𣨲�����ʱ��||���ϸ����ʱ��������Ҫ���в𵥣�����֧�ֵ�ʱ�β����
	 * ����֧�ֵ�ʱ�Σ��ȵ�ȫ��leavebalance��¼ѭ�����֮���ٴ�ͷѭ����Ѱ���û�¼�������leavebalance���������ܲ����ó���ʱ����֧��
	 * @param <T>
	 * @param curProcessBalanceVO
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @param inExclusionScopes
	 * @param splitVOList
	 * @param errMsg
	 * @return ֧�����֮�󣬻�ʣ�µĿ�֧�ֵ�ʱ��
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> double processSplitLengthNotEnoughInExclusionScopes(
			LeaveBalanceVO curProcessBalanceVO,//��ǰ����Ľ����¼
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			T[] inExclusionScopes,//�ݼټ�¼�У���ȫ�ڶ�ռ���ڵ�ʱ��
			List<T> splitVOList,//������֧�ֵĲ��ַ����list
			String errMsg
	) throws BusinessException{

		LeaveTypeCopyVO typeVO = temporarySplitResult.getLeaveTypeVOMap().get(curProcessBalanceVO.getPk_timeitem());
		boolean isLimitLength = typeVO.isLeaveLimit();//��ǰ����Ƿ����ʱ��
		boolean isStrictLimit = isLimitLength&&typeVO.isRestrictLimit();//��ǰ����Ƿ��ϸ����ʱ��
		//��ǰ������Ƿ����û�¼����������ǣ���ʱ�����˵�ʱ������Щ��һ����
		boolean isUserInputType = temporarySplitResult.getOriginalLeaveTypeCopyVO().getPk_timeitem().equals(curProcessBalanceVO.getPk_timeitem());
		//�û�¼�������Ƿ��ϸ����ʱ��
		boolean isUerInputTypeStrictLimit = temporarySplitResult.getOriginalLeaveTypeCopyVO().isLeaveLimit()&&temporarySplitResult.getOriginalLeaveTypeCopyVO().isRestrictLimit();
		double sumLength = SplitLeaveBillHelper.sumLength(inExclusionScopes);
		//��ʱ��δ���ޣ�����(��leavebalance���û�¼�������(���ϸ����||������))�������֧�����е�ʱ��
		if(sumLength<=maxAffordLen||(isUserInputType&&!isStrictLimit)){
			splitVOList.addAll(Arrays.asList(inExclusionScopes));
			maxAffordLen-=sumLength;
			curProcessBalanceVO.minusRestdayorhour(sumLength);
			return maxAffordLen;
		}
		//�ߵ����ʱ���϶������ˣ�����(�����û�¼����������ϸ����ʱ��)
		//���ڵ�ǰ���ڶ�ռ������˳�����ʱ���Ѿ����ܱ�������leavebalance��¼֧���ˡ�Ψһ�ľ������ݣ���ǰ���Ѿ�ѭ�����˵��û�¼������leavebalance��¼
		//�����û�¼������Ҫ���ǲ�����ʱ�����߲��ϸ���ơ�����û�¼���������ϸ���ƣ���ֻ�����쳣��
		if(isUerInputTypeStrictLimit){
			String psnName = getPsnName(inExclusionScopes[0].getPk_psndoc());
			TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(inExclusionScopes[0].getPk_psnjob(), psnjobTimeZoneMap);
			throw new BusinessException(MessageFormat.format(errMsg, psnName,inExclusionScopes[0].getLeavebegintime().toStdString(timeZone),inExclusionScopes[inExclusionScopes.length-1].getLeaveendtime().toStdString(timeZone),typeVO.getMultilangName()));
		}
		//�ߵ����˵����ǰ��������û�¼�����𣬲����û�¼�����𣨲�����ʱ��||���ϸ���ƣ�
		for(int i=0;i<inExclusionScopes.length;i++){
			T vo = inExclusionScopes[i];
			double len = vo.getLeaveHourValue();
			if(len<=maxAffordLen){
				maxAffordLen-=len;
				curProcessBalanceVO.minusRestdayorhour(len);
				splitVOList.add(vo);
				continue;
			}
			//�����ߵ����˵����ǰ����ݼ�ʱ�ε�ʱ���Ѿ�����ȫ����֧���ˡ����ڴ˴��Ƕ�ռ���������Ѿ�û��leavebalance����֧���ˣ������п��ܱ�ǰ��
			//��leavebalance��¼����֧��(��Ҫ���û�¼��������(������||���ϸ����ʱ��))
			//��ʱ��Ҫ�𵥣���ʣ�µĲ��ֵȵ��˴�ѭ������֮���ٻع�ͷȥ��֮ǰ��leavebalance����û�����ó���ʱ����֧����Щʱ�ε�
			if(maxAffordLen==0)
				break;
			//�����ݼ�ʱ�������㷨���������֧�ֵ��ݼ�ʱ��Σ�����֧�ֵ��ڷ�������ĵ�һ��Ԫ�أ�����֧�ֵ��ڵڶ���Ԫ�أ�
			T[] splitVOs = inverseCalculate(vo, typeVO, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap,true);
			T canAffordScope = splitVOs[0];//����֧�ֵ�ʱ��
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
	 * �������ڶ�ռ��������ڶ�ռ�����ʱ��
	 * ��Щʱ�������Ѵ����
	 * ����˼���ǣ����ܹ�ȫ��֧�֣���ȫ��֧�ּ��ɡ�
	 * ������ȫ��֧�֣�����Щ��̤��������ʱ�Σ�����������֣���ռ��֮�ڵĲ��ֺͶ�ռ��֮��Ĳ���Pin�����ȴ����ռ��֮�ڵĲ���Pout
	 * ��Pin��ȫ��֧�֣���ȫ��֧��Pin��Pout�������������У���֧�ֶ��پ�֧�ֶ���
	 * ��Pin����ȫ��֧�֣���ο�processSplitLengthNotEnoughInExclusionScopes�����Ĵ���ʽ����Pin��Pout�Ͳ���֧����
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
	 * @return ֧�����֮�󣬻�ʣ�µĿ�֧�ֵ�ʱ��
	 * @throws BusinessException
	 */
	private  static <T extends LeaveCommonVO> double processSplitLengthNotEnoughPartInExclusionScopes(
			LeaveBalanceVO curProcessBalanceVO,//��ǰ����Ľ����¼
			TemporarySplitResult<T> temporarySplitResult,
			double maxAffordLen,
			CalParam4OnePerson leaveLengthCalParam,
			Map<String, TimeZone> psnjobTimeZoneMap,
			ITimeScope[] exclusionScopes,//�˽����¼�Ķ�ռ��
			T[] partInExclusionScopes,//�ݼټ�¼�У������ڶ�ռ���ڣ������ڶ�ռ��֮��Ĳ���(�׳ƽ�̤��������ʱ��)
			List<T> splitVOList,//������֧�ֵĲ��ַ����list
			String errMsg
	) throws BusinessException{
		double sumLen = SplitLeaveBillHelper.sumLength(partInExclusionScopes);//��Щ��̤��������ʱ�ε���ʱ��
		//���ܽ���ʱ�����ã���Ҫ����Щʱ�ζ�Ū��������ڼ���
		if(sumLen<=maxAffordLen){
			splitVOList.addAll(Arrays.asList(partInExclusionScopes));
			maxAffordLen-=sumLen;
			curProcessBalanceVO.minusRestdayorhour(sumLen);
			return maxAffordLen;
		}
		//�����ߵ����˵������ʱ��������ȫ֧����Щ�����ڶ�ռ���ڵ�ʱ�Ρ���ôҪ����֧�ֶ�ռ��֮�ڵĽ���ʱ��
		//�������Щʱ�����ռ���Ľ�����������ʱ��Ҳ����֧�֣������Ҫ���쳣����ǰ����𣬻����ϸ����ʱ����
		LeaveTypeCopyVO typeVO = temporarySplitResult.getLeaveTypeVOMap().get(curProcessBalanceVO.getPk_timeitem());
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(partInExclusionScopes[0].getPk_psnjob(), psnjobTimeZoneMap);
		//���д���������֣���ռ��֮�ڵĲ��֣��Ͷ�ռ��֮��Ĳ���
		T[][] splitVOs = SplitLeaveBillHelper.splitScopesByExclusionScopes(partInExclusionScopes, exclusionScopes, timeZone);
		//��ռ���Ľ�������������ʱ��
		T[] intersectionScopes = splitVOs[0];
		BillProcessHelperAtServer.calLeaveLength4OnePerson(curProcessBalanceVO.getPk_org(), partInExclusionScopes[0].getPk_psndoc(), intersectionScopes, typeVO, leaveLengthCalParam);
		double intersctionLen = SplitLeaveBillHelper.sumLength(intersectionScopes);//��ռ��֮�ڲ��ֵ���ʱ��
		//�������ʱ������֧����ô����ʱ�Σ������processSplitLengthNotEnoughInExclusionScopes������������Щ��ռ���ڵ�ʱ�Σ��Ҷ�ռ��֮��Ĳ���֧��
		if(intersctionLen>maxAffordLen){
			double remainsAffordLen = processSplitLengthNotEnoughInExclusionScopes(curProcessBalanceVO, temporarySplitResult, maxAffordLen, leaveLengthCalParam, psnjobTimeZoneMap, intersectionScopes, splitVOList, errMsg);
			curProcessBalanceVO.minusRestdayorhour(maxAffordLen-remainsAffordLen);//maxAffordLen-remainsAffordLen���Ǵ˴����ĵ�ʱ��
			maxAffordLen=remainsAffordLen;
			return maxAffordLen;
		}
		//�����ߵ����˵������ʱ������֧�ֶ�ռ���ڵĲ���,������֧�ֶ�ռ��֮��Ĳ���
		//��ȡ�Ĳ����ǣ��Ƚ���ռ�����ڵ�ȫ֧���ˣ�Ȼ��������֧�ֶ�ռ��֮��Ĳ���
		//��Ϊһ��ʱ�ηֳ�����֮������ʱ��һ������»��������ʱ�μ����ʱ�����������Ľ��㶼Ҫ�����𿪵�ʱ�����ºϲ����ټ����׼ȷ
		T[] toMergeScopes = intersectionScopes;//��Ҫ�ϲ���ʱ��
		for(T outScope:splitVOs[1]){//ѭ��������֧�ֶ�ռ��֮��Ĳ���
			//�ֽ������Ѿ���������Ķ�ռ��֮���ʱ�κϲ���ȥ������һ��ʱ���������֧�֣������ʱ��ȫ�����룬������ʱ�����ƿ�ʼʱ����߽���ʱ��
			//toMergeScopes��Զ�����Ŵ˴κϲ�֮ǰ�ļ�¼
			MergeResultDescriptor<T> result = SplitLeaveBillHelper.mergeScopeToScopes(toMergeScopes, outScope, temporarySplitResult.isCanOverYear(),timeZone);
			BillProcessHelperAtServer.calLeaveLength4OnePerson(curProcessBalanceVO.getPk_org(), partInExclusionScopes[0].getPk_psndoc(), result.getVos(), typeVO, leaveLengthCalParam);
			double mergedLength = SplitLeaveBillHelper.sumLength(result.getVos());//�ϲ���ʱ��֮�����ʱ��
			if(mergedLength<=maxAffordLen){//����������ʱ�κ�δ���ޣ�����Լ�������
				toMergeScopes = result.getVos();
				continue;
			}
			//����������ʱ�κ󣬳����ˣ���Ҫ���в𵥲���
			//��ʱ��outScope�ϲ���mergedScopes���棬�����ֿ��ܵķ�ʽ��
			//1.outScope�Ŀ�ʼʱ���ǰ��һ��ʱ�εĽ���ʱ������ˣ�-----------��ռ�����A------*--------��ռ�����B-------------
			//2.outScope�Ľ���ʱ��ͺ���һ��ʱ�εĿ�ʼʱ������ˣ�-----------��ռ�����C------*--------��ռ�����D-------------
			//3.outScope�Ŀ�ʼʱ��ͽ���ʱ���ǰ���һ��ʱ�ζ������ˣ�-----��ռ�����A------*---��ռ�����B----*-----��ռ�����C---------
			//���ڵ�һ�֣��ӱ����ϵ�ʱ�εĿ�ʼʱ�俪ʼ����������֧���ݼ�ʱ�Σ�ֱ��ʱ������Ϊֹ
			//���ڵڶ��֣��ӱ����ϵ�ʱ�εĽ���ʱ�俪ʼ����������֧���ݼ�ʱ�Σ�ֱ��ʱ������Ϊֹ
			//���ڵ����֣���ǰһ��ʱ�εĿ�ʼʱ�俪ʼ����������֧���ݼ�ʱ�Σ�ֱ��ʱ������Ϊֹ
			int leftJoinIndex = result.getLeftJoinIndex();//����outScope����˽��ϵ�ʱ�ε�index
			int rightJoinIndex = result.getRightJoinIndex();//����outScope���Ҷ˽��ϵ�ʱ�ε�index
			//��һ�ֺ͵ڶ��ֽӷ�����������/���������
			if((leftJoinIndex>=0&&rightJoinIndex<0)||(leftJoinIndex<0&&rightJoinIndex>=0)){
				int keyIndex = leftJoinIndex>=0?leftJoinIndex:rightJoinIndex;//���ڵ�һ���������leftJoinIndex���ڶ��������rightJoinIndex
				T toSplitVO = result.getVos()[keyIndex];
				//������Σ����ṩ��ʱ����=maxAffordLen-(mergedScopes�����ȥkeyIndexλ���ϵ�ʱ�ε���ʱ��)��.��仰��Ҫ��ϸ���
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
			//�����֣�����outScope�Ѿ�һǰһ���������ݼ�ʱ�κϲ���һ�������ˣ������ڲ���ȫ��֧�֣�ֻ�������´����ϲ�������Σ���outScope�Ľ���ʱ���ٲ��
			//���Σ��Ӳ�ֺ�ĵ�һ���εĿ�ʼʱ���������֧�֣�
			//                          ��ռ�����A                  ��ռ�����B                ��ռ�����C
			//�����Ѿ��ϲ���������ӣ�------------------------*-----------------------------*--------------------
			//����Ҫ��C�ٲ����                 ------------------------*-----------------------------����-------------------
			//                       ------------------------------------------------------>��������֧��A+B�ĺϲ���(C�������Ҳ�Ǳ�Ĭ��֧�ֵ�)
			T toSplitVO = result.getVos()[leftJoinIndex];//��������Ѿ����κϳ�һ���ˣ�����Ҫ�ٲ�
			toSplitVO.setLeaveendtime(outScope.getLeaveendtime());
			toSplitVO.setLeaveenddate(DateTimeUtils.toLiteralDate(toSplitVO.getLeaveendtime(), timeZone));
			//������Σ����ṩ��ʱ��
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
		//�������ǲ����ߵ������
		throw new RuntimeException("program calculation error!");
	}
	/**
	 * ���в�
	 * �����㷨
	 * @param temporarySplitResult
	 * @param maxAffordLen
	 * @param leaveTypeVOMap
	 * @param unBalancedVOs
	 * @param applyDate �������ڣ��������뵥������
	 * @param <T>
	 * @param CalParam4OnePerson,��������ݼ�ʱ����Ҫ�Ĳ���
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
		//�ߵ������leavebalance������֧�ֵ�ʱ�ζ�������splitVOsForOneBalanceVO���棬���в���֧�ֵĶ�������toSplitVOsForOneBalanceVO����
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(temporarySplitResult.getToSplitVOs()[0].getPk_psnjob(), psnjobTimeZoneMap);
		temporarySplitResult.splitScopesFromToSplitVOs(temporarySplitResult.getSplitVOsForOneBalanceVO(), timeZone);
	}

	/**
	 * �����㷨����һ���ݼ�ʱ��β���������֣���һ�����ֵĳ��ȸպ�=maxAffordLen�����ԭ����maxAffordLen����֧������vo�ĳ���
	 * isFromLeft2Right�����˲�ķ���Ϊtrue�Ļ�����������֧�֣�������֧�ֵĲ�����vo�󲿣�Ϊfalse�Ļ�����������֧�֣�������֧�ֵĲ�����vo�Ҳ�
	 * �����߼���
	 * �����ü��㷨���һ�����ԵĽ���/��ʼʱ��T
	 * Ȼ����T�����ݼ�ʱ��L������step=(L<=���ṩʱ��)?������10����:������10���ӣ������ǻ�ʹ�ݼ�ʱ���䳤�ķ��򣬶��ڴ����Ҳ�����after���򣬷�֮����before����
	 * Ȼ��ʼ�����ѭ����
	 * location:
	 * T=T+step�������ݼ�ʱ��L
	 * ��L<=���ṩʱ��
	 * 		�� ��� stepΪ����-->step���䣬goto location
	 *        ���step=������10����-->step=������1���ӣ�goto location
	 *        ���step=������1���ӣ���ѭ��ֹͣ��T����ȷ�е��ݼٽ���/��ʼʱ��
	 * ��L>���ṩʱ��
	 * 		�� ��� stepΪ����-->step���䣬goto location
	 *        ��� stepΪ������10����-->step=������1���ӣ�goto location
	 *        ���stepΪ������1���ӣ���ѭ��ֹͣ��T=T-step
	 * ���㷨�Ļ���˼���ǣ�����һ����ŵ�ʱ��㣬�����ʱ�������ʱ����û���ޣ����ô���(10����)��ǰ�壻��������ˣ����ô���(10����)����
	 * �󲽳���һ��step֮���ٴμ���ʱ��������Ȼû�г����������(10����)��
	 *                                   �����ˣ�������С��(1����)��
	 * �󲽳���һ��step֮���ٴμ���ʱ��������Ȼ���ޣ��������(10����)��
	 *                                   ��û�г��ޣ���ı䷽��С����1���ӣ���ǰ��
	 * �������С����ǰ�߻������ˣ�ֱ�����һ���ȽϾ�ϸ�Ľ���ʱ��Ϊֹ
	 * @param <T>
	 * @param vo
	 * @param leaveTypeCopyVO
	 * @param maxAffordLen
	 * @param leaveLengthCalParam
	 * @param psnjobTimeZoneMap
	 * @param isFromLeft2Right,true��ʾ�ӿ�ʼʱ�俪ʼ������ʱ��ķ����false��ʾ�ӽ���ʱ������ʼʱ��ķ����
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
		//�ݼٽ���ʱ�����ṩ���ݼ�Сʱ����maxAffordLen�ĵ�λ�п������죬Ҫͳһת��ΪСʱ�����ڼ��㣩
		double leaveHours = leaveTypeCopyVO.getTimeItemUnit()==TimeItemCopyVO.TIMEITEMUNIT_HOUR?maxAffordLen:maxAffordLen*leaveLengthCalParam.timeruleVO.getDaytohour2();
		//����������ݼٽ���/��ʼʱ�䡣��ֻ��һ�����Լ����ʱ�䣬��Ҫ��һ���ľ�ȷ����
		UFDateTime testEndOrBeginTime = simpleCalculateEndOrBeginTime(isFromLeft2Right?vo.getLeavebegintime():vo.getLeaveendtime(),
				isFromLeft2Right?vo.getLeaveendtime():vo.getLeavebegintime(), leaveHours, leaveTypeCopyVO, leaveLengthCalParam,isFromLeft2Right);
		T tempCanAffordVO = (T)vo.clone();
		tempCanAffordVO.setPk_leavetype(leaveTypeCopyVO.getPk_timeitem());
		tempCanAffordVO.setPk_leavetypecopy(leaveTypeCopyVO.getPk_timeitemcopy());
		TimeZone timeZone = SplitLeaveBillHelper.queryTimeZoneByPkPsnjob(tempCanAffordVO.getPk_psnjob(), psnjobTimeZoneMap);
		setEndOrBeginTime(tempCanAffordVO,testEndOrBeginTime, timeZone, isFromLeft2Right);
		//�ݼ�ʱ�����㷽��Ҫ���������������˹���һ������
		T[] tempVOs = SplitLeaveBillHelper.createOneElementArray(tempCanAffordVO);
		String pk_org = leaveTypeCopyVO.getPk_org();
		String pk_psndoc = vo.getPk_psndoc();
		BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
		int directionFlag = isFromLeft2Right?1:-1;//�������Ҳ�ʹ���������ʱ�򣬲����ķ���һ��
		long positiveTenMinutesSconds = 10*60*directionFlag;//������10����ʱ���üӵ����������ڴ������Ҳ������Ƽӵ����������ģ����ڴ�������������Ƶ������Ǹ���
		long negativeTenMinutesSconds = 0-positiveTenMinutesSconds;//������10����ʱ���üӵ�����
		long positiveOneMinuteSeconds = 60*directionFlag;//������1����ʱ���üӵ�����
		long negativeOneMinuteSeconds = 0-positiveOneMinuteSeconds;//������1����ʱ���üӵ�����
		double calLen = tempCanAffordVO.getLeavehour()==null?0:tempCanAffordVO.getLeavehour().doubleValue();
		long step = calLen<=maxAffordLen?positiveTenMinutesSconds:negativeTenMinutesSconds;
		UFDateTime exactEndOrBeginTime = null;//�����ȷ�н���/��ʼʱ��
		while(true){
			testEndOrBeginTime = DateTimeUtils.getDateTimeAfterMills(testEndOrBeginTime, step*1000);
			setEndOrBeginTime(tempCanAffordVO, testEndOrBeginTime, timeZone, isFromLeft2Right);
			BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
			calLen = tempCanAffordVO.getLeavehour()==null?0:tempCanAffordVO.getLeavehour().doubleValue();
			if(calLen<=maxAffordLen){//�����δ����
				if(step*directionFlag>0)//step*directionFlag>0������ʱ����������ʱ��
					continue;
				if(step==negativeTenMinutesSconds){
					step = positiveOneMinuteSeconds;
					continue;
				}
				exactEndOrBeginTime=testEndOrBeginTime;
				break;
			}
			if(step*directionFlag<0)//step*directionFlag<0������ʱ���ڷ�����ʱ��
				continue;
			if(step==positiveTenMinutesSconds){
				step = negativeOneMinuteSeconds;
				continue;
			}
			exactEndOrBeginTime = DateTimeUtils.getDateTimeBeforeMills(testEndOrBeginTime, step*1000);
			break;
		}
		T leftScope = null;//���֮����벿��
		T rightScope = null;//���֮���Ұ벿��
		if(exactEndOrBeginTime.equals(vo.getLeavebegintime())){//����и��պ���vo�Ŀ�ʼʱ�䣬����벿Ϊnull���Ұ벿Ϊȫ��
			leftScope=null;
			rightScope = (T)vo.clone();
		}
		else if(exactEndOrBeginTime.equals(vo.getLeaveendtime())){//����и��պ���vo�Ľ���ʱ�䣬���Ұ벿Ϊnull����벿Ϊȫ��
			leftScope= (T)vo.clone();
			rightScope = null;
		}
		else{//����и�����м�,����Ҫ�ֳ�����
			leftScope = (T) vo.clone();
			setEndOrBeginTime(leftScope, exactEndOrBeginTime, timeZone, true);
			tempVOs[0]=leftScope;
			BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
			rightScope = (T)vo.clone();
			setEndOrBeginTime(rightScope, exactEndOrBeginTime, timeZone, false);
			tempVOs[0]=rightScope;
			BillProcessHelperAtServer.calLeaveLength4OnePerson(pk_org, pk_psndoc, tempVOs, leaveTypeCopyVO, leaveLengthCalParam);
		}
		//���ڷ��ص����飬��һ��ʱ�����ݼ�ʱ������֧�ֵ�ʱ�Σ��ڶ���ʱ���ǲ���֧�ֵ�
		T[] retArray = (T[]) Array.newInstance(vo.getClass(), 2);
		if(isFromLeft2Right){//����Ǵ������Ҳ�����ߵ�Ԫ������֧�ֵ��ݼ�ʱ�Σ��ұߵ��ǲ���֧�ֵ�
			retArray[0]=leftScope;
			retArray[1]=rightScope;
			return retArray;
		}
		retArray[1]=leftScope;
		retArray[0]=rightScope;
		return retArray;
	}

	/**
	 * �����ݼٿ�ʼ/����ʱ��fromTime���ݼ�Сʱ��leaveHours���򵥿��ٵؼ����һ���ݼٽ���/��ʼʱ��Ľ���ֵ
	 * ����˼���ǣ���fromTime��ʼ��������ʱ��������/ǰ�ߣ��߹��Ĺ��������Ĺ���ʱ��εĳ��ȸպõ���leaveHoursʱ��ͣס������ֹͣ�ص�ʱ���
	 * @param fromTime
	 * @param endTimeLimit,�����ʱ��������/���ޣ���������Ľ���/��ʼʱ�䲻������/���ڴ�ʱ��
	 * @param leaveHours
	 * @param leaveTypeCopyVO
	 * @param leaveLengthCalParam
	 * @param fromLeft2Right true����ʾ�ӿ�ʼʱ����������������ң�false�෴
	 * @return
	 * @throws BusinessException
	 */
	private static UFDateTime simpleCalculateEndOrBeginTime(UFDateTime fromTime,UFDateTime toTimeLimit,double leaveHours,
			LeaveTypeCopyVO leaveTypeCopyVO,CalParam4OnePerson leaveLengthCalParam,boolean fromLeft2Right) throws BusinessException{
		//ȡ����ʼ/����ʱ����������ڣ�Ȼ����������ڿ�ʼѭ���������������������������������ǰ/��������
		UFLiteralDate curDate = fromTime.getDate().toUFLiteralDate(ICalendar.BASE_TIMEZONE).getDateBefore(fromLeft2Right?3:-3);
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = leaveLengthCalParam.calendarMap;//��Ա��������
		Map<String, ShiftVO> shiftMap = leaveLengthCalParam.shiftMap;
		Map<UFLiteralDate, String> dateOrgMap = leaveLengthCalParam.dateOrgMap;
		TimeRuleVO timeRuleVO = leaveLengthCalParam.timeruleVO;
		Map<String, TimeZone> orgTimeZoneMap = timeRuleVO.getTimeZoneMap();
		boolean gxIsLeave = leaveTypeCopyVO.getGxcomtype().intValue()==LeaveTypeCopyVO.GXCOMTYPE_TOLEAVE;
		long leaveSeconds = (long)leaveHours*3600;//��Сʱת��Ϊ��
		long shiftSumSecondsAfterBeforeFromTime = 0;//������ѭ�������У���ÿ��İ�εĹ���ʱ��ĳ����ۼ�������Ҫ�����ݼٿ�ʼ/����ʱ��fromTime֮��/ǰ�Ĳ��֣�
		long workDaySeconds = (long)timeRuleVO.getDaytohour2()*3600;//���ڹ����еĹ�����ʱ����ת��Ϊ��
//		int dateAfterUnit = fromLeft2Right?1:-1;
		while(true){//��fromLeft2RightΪtrue�����Ǵ�ǰ�������㣬�����ǴӺ���ǰ����
			UFLiteralDate preDate = null;//ǰһ��
			UFLiteralDate nextDate = null;//��һ��
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
			//����Ĺ�������
			AggPsnCalendar curCalendar = calendarMap.get(curDate);
			//�����ʱ��
			TimeZone curTimeZone = orgTimeZoneMap.get(dateOrgMap.get(curDate));
			curTimeZone = curTimeZone==null?ICalendar.BASE_TIMEZONE:curTimeZone;
			//ǰһ��Ĺ�������
			AggPsnCalendar preCalendar = calendarMap.get(preDate);
			//ǰһ��İ��
			ShiftVO preShift = preCalendar==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
			//ǰһ���ʱ��
			TimeZone preTimeZone = orgTimeZoneMap.get(dateOrgMap.get(preDate));
			preTimeZone = preTimeZone==null?ICalendar.BASE_TIMEZONE:preTimeZone;
			//��һ��Ĺ�������
			AggPsnCalendar nextCalendar = calendarMap.get(nextDate);
			//��һ��İ��
			ShiftVO nextShift = nextCalendar==null?null:ShiftServiceFacade.getShiftVOFromMap(shiftMap, nextCalendar.getPsnCalendarVO().getPk_shift());
			//��һ���ʱ��
			TimeZone nextTimeZone = orgTimeZoneMap.get(dateOrgMap.get(nextDate));
			nextTimeZone = nextTimeZone==null?ICalendar.BASE_TIMEZONE:nextTimeZone;
			//����Ĺ���ʱ���
			ITimeScope[] workScopes = BillProcessHelper.getWorkTimeScopes(curDate.toString(), curCalendar, preShift, nextShift, curTimeZone, preTimeZone, nextTimeZone);
			ITimeScope firstWorkScope = workScopes[0];//��һ��������
			ITimeScope lastWorkScope = workScopes[workScopes.length-1];//���һ��������
			//����ο�ʼʱ���Ѿ�>=toTimeLimit/��ν���ʱ���Ѿ�<=toTimeLimit����ʾ�����Ѿ�����/ǰ�Ƶ�̫���ˣ���ֱ�ӷ���toTimeLimit����
			if((fromLeft2Right&&!firstWorkScope.getScope_start_datetime().before(toTimeLimit))
					||(!fromLeft2Right&&!lastWorkScope.getScope_end_datetime().after(toTimeLimit)))
				return toTimeLimit;
			//��������ǹ����ҹ��ݲ����ݼ�,��continue
			boolean curIsGX = curCalendar==null||curCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX);
			if(curIsGX&&!gxIsLeave){
				continue;
			}
			//�����εĹ���ʱ�����fromTimeǰ��/����,˵��while�Ĵ�����������û�е��������˾���ʼ��ʱ��㣬continue
			if(fromLeft2Right){
				if((!lastWorkScope.isContainsLastSecond()&&!lastWorkScope.getScope_end_datetime().after(fromTime))||
				    (lastWorkScope.isContainsLastSecond()&&lastWorkScope.getScope_end_datetime().before(fromTime)))
					continue;
			}
			else{
				if(!firstWorkScope.getScope_start_datetime().before(fromTime))
					continue;
			}
			//�����ߵ�����,˵����һ��İ�ε�ʱ���ȫ�����߲�����fromTime֮��/ǰ,���Ա���fromTimeΪ��ʼʱ��/����ʱ����ݼٶ������ǵ�
			long shiftSecondsAfterBeforeFromTime = 0;//����İ�����ݼٿ�ʼʱ��֮��/�ݼٽ���ʱ��֮ǰ������
			//��εĿ�ʼ/����ʱ��
			UFDateTime shiftBeginEndTime = fromLeft2Right?firstWorkScope.getScope_start_datetime():lastWorkScope.getScope_end_datetime();
			long shiftSeconds = TimeScopeUtils.getLength(workScopes);//�����ε���������
			//���ݼٿ�ʼʱ��<=��εĿ�ʼʱ�䣬����������ȫ���ݼٿ�ʼʱ��֮��||�ݼٽ���ʱ��>=��εĽ���ʱ�䣬����������ȫ���ݼٽ���ʱ��֮ǰ
			if((fromLeft2Right&&!fromTime.after(shiftBeginEndTime))||(!fromLeft2Right&&!fromTime.before(shiftBeginEndTime))){
				shiftSecondsAfterBeforeFromTime = curIsGX?Math.min(shiftSeconds, workDaySeconds):shiftSeconds;
			}
			else{//���ݼٵĿ�ʼʱ��>��εĿ�ʼʱ�䣬���ݼٿ�ʼʱ���ڰ�εĿ�ʼʱ�������ʱ��֮��||�ݼٵĽ���ʱ��<��εĽ���ʱ�䣬���ݼٽ���ʱ���ڰ�εĿ�ʼʱ�������ʱ��֮��
				//�ݼٿ�ʼʱ��֮��Ĺ�����/�ݼٽ���ʱ��֮ǰ�Ĺ�����
				ITimeScope[] workScopesAfterBeforeFromTime = TimeScopeUtils.intersectionTimeScopes(workScopes,
						new ITimeScope[]{fromLeft2Right?
						 new DefaultTimeScope(fromTime, lastWorkScope.getScope_end_datetime(),lastWorkScope.isContainsLastSecond())
						:new DefaultTimeScope(firstWorkScope.getScope_start_datetime(), fromTime, false)});
				shiftSecondsAfterBeforeFromTime = curIsGX?Math.min(workDaySeconds, TimeScopeUtils.getLength(workScopesAfterBeforeFromTime)):
					TimeScopeUtils.getLength(workScopesAfterBeforeFromTime);
			}
			long remainSeconds = leaveSeconds-shiftSumSecondsAfterBeforeFromTime;//�˴λ����ṩ���ݼ�����
			if(shiftSecondsAfterBeforeFromTime<remainSeconds){//���remainSeconds������ȫ����ס�˹������һ���ʣ��
				shiftSumSecondsAfterBeforeFromTime+=shiftSecondsAfterBeforeFromTime;
				continue;
			}
			if(shiftSecondsAfterBeforeFromTime==remainSeconds){//���remainSeconds�պ��ܰ����˹����Σ��򷵻ع��������һ��/��һ��
				return fromLeft2Right?
						(DateTimeUtils.min(lastWorkScope.isContainsLastSecond()?
								DateTimeUtils.getDateTimeAfterMills(lastWorkScope.getScope_end_datetime(), 1000):
								lastWorkScope.getScope_end_datetime(),
								toTimeLimit)):
						DateTimeUtils.max(firstWorkScope.getScope_start_datetime(), toTimeLimit);
			}
			//���remainSeconds�Ѿ������ˣ������������
			//����ʱ��Ϊlater(�ݼٿ�ʼʱ��fromTime,�����ο�ʼʱ��)+remainSeconds/��ʼʱ��Ϊearlier(�ݼٽ���ʱ��fromTime,�����ν���ʱ��)-remainSeconds
			//���������ʱ�䲻һ��׼ȷ������Ҫ�����΢������ȷ��λ����ʱ��
			UFDateTime spreadTime = SplitLeaveBillHelper.spread(fromTime, workScopes, remainSeconds,fromLeft2Right);
			return fromLeft2Right?
				DateTimeUtils.min(spreadTime,toTimeLimit):
				DateTimeUtils.max(spreadTime,toTimeLimit);
		}
	}

	private static <T extends LeaveCommonVO> boolean checkApplyDatePara(T vo ,UFLiteralDate applyDate,TimeZone timeZone,LeaveTypeCopyVO leaveTypeCopyVO,boolean throwsException) throws BusinessException{
		if(!(vo instanceof LeavebVO)||!leaveTypeCopyVO.isLeaveAppTimeLimit()){//����������뵥�������ݼ�������������������ʱ�ζ��ŵ��ڶ���Ԫ����
			return false;
		}
		int limitDates = leaveTypeCopyVO.getLeaveAppTimeLimit();
		UFLiteralDate beginDate = DateTimeUtils.toLiteralDate(vo.getLeavebegintime(), timeZone);
		UFLiteralDate properBeginDate = applyDate.getDateBefore(limitDates);
//		int daysAfter = properBeginDate.getDaysAfter(beginDate);
		boolean isBreak = beginDate.before(properBeginDate);//�ƶ��˵Ĳ���ʹ
		if(isBreak&&throwsException){
//		if(daysAfter>0&&throwsException){
			String psnName = getPsnName(vo.getPk_psndoc());
			String typeName = leaveTypeCopyVO.getMultilangName();
			int lateDates = UFLiteralDate.getDaysBetween(beginDate, applyDate);
			if(limitDates==0)
				throw new BusinessException(MessageFormat.format(ResHelper.getString("6017leave","06017leave0229")
/*@res "��Ա{0}�ݼ����{1}����������{2}���ݼٿ�ʼ����{3}����{4}��,���涨Ӧ�õ�������!"*/, psnName,typeName,applyDate,beginDate,lateDates));
			throw new BusinessException(MessageFormat.format(ResHelper.getString("6017leave","06017leave0230")
/*@res "��Ա{0}�ݼ����{1}����������{2}���ݼٿ�ʼ����{3}����{4}��,���涨���ó���{5}��!"*/, psnName,typeName,applyDate,beginDate,lateDates,limitDates));
		}
		return isBreak;
	}


	private static String getPsnName(String pk_psndoc) throws DAOException{
		return ((PsndocVO)new BaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc)).getMultiLangName();
	}

	/**
	 * ����һ���ݼ�ʱ�εĽ���/��ʼʱ�䣬����ʱ��˳��ѽ���/��ʼ����Ҳ������
	 * @param <T>
	 * @param vo
	 * @param endOrBeginTime
	 * @param timeZone
	 * @param isSetEndTime��true��ʾ���ý���ʱ�䣬false��ʾ���ÿ�ʼʱ��
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