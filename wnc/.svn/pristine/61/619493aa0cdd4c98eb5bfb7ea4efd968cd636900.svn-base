package nc.itf.ta.algorithm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.ta.algorithm.impl.DefaultCompleteCheckTimeScope;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;

/**
 * 刷卡段的工具类
 * 
 * @author zengcheng
 * 
 */
public class CompleteCheckTimeScopeUtils {

	/**
	 * 从所有的工作时间段中，找出刷卡段对应的工作时间段数组 例如刷卡段为第一段和第二段组成，则此方法返回长度为2的数组，内容是第一个工作段和第二工作段
	 * 
	 * @param checkTimeScope
	 * @param allWorkScopes
	 * @return
	 */
	public static IWorkTimeScope[] filterWorkScopesByCompleteCheckTimeScope(ICompleteCheckTime checkTimeScope,
			IWorkTimeScope[] allWorkScopes) {
		IWorkTimeScope[] returnArray = (IWorkTimeScope[]) Array.newInstance(allWorkScopes[0].getClass(),
				checkTimeScope.getCheckoutScopeTimeID() - checkTimeScope.getCheckinScopeTimeID() + 1);
		for (int i = checkTimeScope.getCheckinScopeTimeID(); i <= checkTimeScope.getCheckoutScopeTimeID(); i++) {
			returnArray[i - checkTimeScope.getCheckinScopeTimeID()] = allWorkScopes[i];
		}
		return returnArray;
	}

	/**
	 * 将工作时间段根据刷卡开始时间、刷卡结束时间合并成刷卡时间段 中间不刷卡的工作时间段都会合并成一整个刷卡时间段
	 * 第一个刷卡时间段的刷卡开始时间就是班次的考勤刷卡开始时间，最后一个刷卡时间段的刷卡结束时间就是班次的考勤刷卡结束时间
	 * 
	 * @param beginTime
	 * @param endTime
	 * @param workScopes
	 * @return
	 */
	public static ICompleteCheckTimeScope[] mergeWorkTime(UFDateTime beginTime, UFDateTime endTime,
			IWorkTimeScope[] workScopes) {
		// 存储timeid-工作时间段 keyvalue对的map
		Map<Integer, IWorkTimeScope> workScopeMap = new HashMap<Integer, IWorkTimeScope>();
		for (IWorkTimeScope workScope : workScopes) {
			workScopeMap.put(workScope.getTimeid(), workScope);
		}
		List<ICompleteCheckTimeScope> retList = new ArrayList<ICompleteCheckTimeScope>();
		for (int i = 0; i < workScopes.length; i++) {
			IWorkTimeScope workScope = workScopes[i];
			// 如果这个workscope上班刷卡，说明要新new一个刷卡段了
			if (workScope.getCheckinflag() != null && workScope.getCheckinflag().booleanValue()) {
				ICompleteCheckTimeScope checkTimeScope = new DefaultCompleteCheckTimeScope();
				retList.add(checkTimeScope);
				checkTimeScope.setCheckinScopeTimeID(workScope.getTimeid());
				checkTimeScope.setKssj(workScope.getScope_start_datetime());// 此刷卡段的规定上报时间
				if (workScope.getCheckoutflag() != null && workScope.getCheckoutflag().booleanValue()) {
					checkTimeScope.setCheckoutScopeTimeID(workScope.getTimeid());
					checkTimeScope.setJssj(workScope.getScope_end_datetime());// 此刷卡段的规定下班时间
				}
				continue;
			}
			// 如果这个workscope上班不刷卡，那么看这个workscope下班是否要刷卡
			// 如果下班不刷卡，则很简单，循环继续
			if (workScope.getCheckoutflag() == null || !workScope.getCheckoutflag().booleanValue())
				continue;
			// 如果下班刷卡，则要取出当前的刷卡段，将其下班卡所属段设置进去
			retList.get(retList.size() - 1).setCheckoutScopeTimeID(workScope.getTimeid());
			retList.get(retList.size() - 1).setJssj(workScope.getScope_end_datetime());// 此刷卡段的规定下班时间
		}
		// 至此所有刷卡段生成完毕，且上班卡所属段、下班卡所属段也生成完毕
		// 下面开始生成刷卡段的上班刷卡段开始时间、截止时间等信息
		for (int i = 0; i < retList.size(); i++) {
			// 计算上班刷卡段的开始时间
			ICompleteCheckTimeScope checkTimeScope = retList.get(i);
			if (i == 0)// 如果是第一个刷卡段，则ksfrom就是考勤开始时间
				checkTimeScope.setKsfromtime(beginTime);
			else {// 如果不是第一个刷卡段，则ksfrom就是上一个刷卡段的下班刷卡段的截止时间
				checkTimeScope.setKsfromtime(retList.get(i - 1).getJstotime());
			}
			// 计算上班刷卡段的结束时间，比较简单，就是本刷卡段的中点
			Integer checkinID = checkTimeScope.getCheckinScopeTimeID();
			Integer checkoutID = checkTimeScope.getCheckoutScopeTimeID();
			UFDateTime thisBeginTime = workScopeMap.get(checkinID).getScope_start_datetime();
			UFDateTime thisEndTime = workScopeMap.get(checkoutID).getScope_end_datetime();
			checkTimeScope.setKstotime(new UFDateTime((thisBeginTime.getMillis() + thisEndTime.getMillis()) / 2));
			// 计算下班刷卡段的开始时间，更简单，就是上班刷卡段的结束时间
			checkTimeScope.setJsfromtime(checkTimeScope.getKstotime());
			// 计算下班刷卡段的结束时间
			if (i == retList.size() - 1) {// 如果是最后一个刷卡段，则截止时间就是考勤结束时间
				checkTimeScope.setJstotime(endTime);
				continue;
			}
			// 如果不是最后一个刷卡段，则下班刷卡段的结束时间就是本段的下班时间与下一段的上班时间的中点
			ICompleteCheckTimeScope nextScope = retList.get(i + 1);
			Integer nextCheckinID = nextScope.getCheckinScopeTimeID();
			UFDateTime nextBeginTime = workScopeMap.get(nextCheckinID).getScope_start_datetime();
			checkTimeScope.setJstotime(new UFDateTime((nextBeginTime.getMillis() + thisEndTime.getMillis()) / 2));
		}
		return retList.toArray(new ICompleteCheckTimeScope[0]);
	}

	/**
	 * 将n个工作时间段合并为m个刷卡段，m<=n。 工作时间段中的刷卡段开始时间、刷卡段结束时间已经在排班时计算好了，在这里直接set到刷卡段中去就行了
	 * 
	 * @param workTimeVOs
	 * @return
	 */
	public static ICompleteCheckTimeScope[] mergeWorkTime(PsnWorkTimeVO[] workTimeVOs) {
		List<ICompleteCheckTimeScope> retList = new ArrayList<ICompleteCheckTimeScope>();
		if (null == workTimeVOs) {
			return null;
		}
		for (int i = 0; i < workTimeVOs.length; i++) {
			PsnWorkTimeVO workTime = workTimeVOs[i];
			// 如果这个workTime上班刷卡，说明要新new一个刷卡段了
			if (workTime.getCheckinflag() != null && workTime.getCheckinflag().booleanValue()) {
				ICompleteCheckTimeScope checkTimeScope = new DefaultCompleteCheckTimeScope();
				retList.add(checkTimeScope);
				checkTimeScope.setCheckinScopeTimeID(workTime.getTimeid());
				checkTimeScope.setKssj(workTime.getScope_start_datetime());// 此刷卡段的规定上报时间
				checkTimeScope.setKsfromtime(workTime.getKsfromtime());
				checkTimeScope.setKstotime(workTime.getKstotime());
				if (workTime.getCheckoutflag() != null && workTime.getCheckoutflag().booleanValue()) {
					checkTimeScope.setCheckoutScopeTimeID(workTime.getTimeid());
					checkTimeScope.setJssj(workTime.getScope_end_datetime());// 此刷卡段的规定下班时间
					checkTimeScope.setJsfromtime(workTime.getJsfromtime());
					checkTimeScope.setJstotime(workTime.getJstotime());
				}
				continue;
			}
			// 如果这个workscope上班不刷卡，那么看这个workscope下班是否要刷卡
			// 如果下班不刷卡，则很简单，循环继续
			if (workTime.getCheckoutflag() == null || !workTime.getCheckoutflag().booleanValue())
				continue;
			if (retList.size() < 1)
				continue;
			// 如果下班刷卡，则要取出当前的刷卡段，将其下班卡所属段设置进去
			ICompleteCheckTimeScope lastScope = retList.get(retList.size() - 1);
			lastScope.setCheckoutScopeTimeID(workTime.getTimeid());
			lastScope.setJssj(workTime.getScope_end_datetime());// 此刷卡段的规定下班时间
			lastScope.setJsfromtime(workTime.getJsfromtime());
			lastScope.setJstotime(workTime.getJstotime());
		}
		return retList.toArray(new ICompleteCheckTimeScope[0]);
	}
}
