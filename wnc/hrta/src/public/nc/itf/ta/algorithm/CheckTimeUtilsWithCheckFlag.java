package nc.itf.ta.algorithm;

import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.ArrayUtils;

/**
 * 门禁刷签卡计算的工具类
 * 
 * @author zengcheng
 * 
 */
public class CheckTimeUtilsWithCheckFlag {
	/**
	 * 考虑签入签出标志的情况下，简单计算每个刷卡段（注意，是刷卡段，不是工作时间段，即此方法传进来的参数应该是刷卡段）的上下班时间点。
	 * 此计算是最基础的计算，只考虑刷签卡数据以及规定的上下班时间点，不考虑各种单据。 此算法与弹性班无关，即，此计算中涉及的所有时间点都是固定点。
	 * 弹性班的计算可能会调用此方法，例如做试算 要求刷签卡时间已经按班次的刷卡开始时间结束时间过滤
	 * 返回的CheckTimeResultDescriber[]长度与ICompleteCheckTimeScope[]一致
	 */
	public static CheckTimeResultDescriber[] simpleCalculateCheckInOutTime(ICheckTime[] checkTimes,
			ICompleteCheckTimeScope[] checkTimeScopes) {
		if (ArrayUtils.isEmpty(checkTimeScopes))
			return null;
		CheckTimeResultDescriber[] retArray = CheckTimeUtils
				.initCheckTimeResultDescriberByCompleteCheckTimeScopes(checkTimeScopes);
		if (ArrayUtils.isEmpty(checkTimes))
			return retArray;
		// 首先进行第一轮查找
		firstSearch(checkTimes, checkTimeScopes, retArray);
		// 然后进行第二轮查找
		secondSearch(checkTimes, checkTimeScopes, retArray);
		return retArray;
	}

	/**
	 * 第一轮查找 具体算法见viso描述
	 * 
	 * @param checkTimes
	 * @param checkTimsScopes
	 * @param retArray
	 */
	private static void firstSearch(ICheckTime[] checkTimes, ICompleteCheckTimeScope[] checkTimeScopes,
			CheckTimeResultDescriber[] retArray) {
		for (int i = 0; i < checkTimeScopes.length; i++) {
			ICompleteCheckTimeScope checkTimeScope = checkTimeScopes[i];
			CheckTimeResultDescriber result = retArray[i];
			// 查找上班时间，先从上班段开始时间（含）到上班段结束时间（不含）内查找最早的签入记录
			ICheckTime sbTime = findOrderedEarliestYN(checkTimes, checkTimeScope.getKsfromtime(),
					checkTimeScope.getKstotime(), ICheckTime.CHECK_FLAG_IN);
			if (sbTime != null)
				result.setBegintime(sbTime);
			// 然后计算下班时间，按上班时间是否为空分两种情况来计算
			ICheckTime xbTime = null;
			// 如果上班时间非空，则取出later(上班卡,规定上班时间)之后(不含),下班刷卡段结束时间之前(不含,若是最后一个刷卡段，则含)的下班刷签卡数据(含开始时间点,不含结束时间点)中最晚的一条
			if (sbTime != null) {
				xbTime = findOrderedLatest(checkTimes,
						DateTimeUtils.max(sbTime.getDatetime(), checkTimeScope.getKssj()), false,
						checkTimeScope.getJstotime(), i == checkTimeScopes.length - 1, ICheckTime.CHECK_FLAG_OUT);
			}
			// 如果上班时间为空，则取出下班卡刷卡段内的所有签出刷签卡数据(含开始时间点,不含结束时间点,若是最后一个刷卡段，则含),取最晚的一条
			else {
				xbTime = findOrderedLatest(checkTimes, checkTimeScope.getJsfromtime(), false,
						checkTimeScope.getJstotime(), i == checkTimeScopes.length - 1, ICheckTime.CHECK_FLAG_OUT);
			}
			if (xbTime != null)
				result.setEndtime(xbTime);
		}
	}

	/**
	 * 第二轮查找 具体算法见viso描述
	 * 
	 * @param checkTimes
	 * @param checkTimsScopes
	 * @param retArray
	 */
	private static void secondSearch(ICheckTime[] checkTimes, ICompleteCheckTimeScope[] checkTimeScopes,
			CheckTimeResultDescriber[] retArray) {
		for (int i = 0; i < checkTimeScopes.length; i++) {
			CheckTimeResultDescriber result = retArray[i];
			if (result.getBegintime() != null && result.getEndtime() != null)
				continue;
			ICompleteCheckTimeScope checkTimeScope = checkTimeScopes[i];
			if (result.getBegintime() == null) {// 如果上班时间没有计算出来，则需要找前一个下班段借，或者找本下班段借
				UFDateTime // 过滤开始时间
				filterBegintime = i == 0 ? checkTimeScope.getJsfromtime() : DateTimeUtils.max(
						retArray[i - 1].getEndtime() == null ? null : retArray[i - 1].getEndtime().getDatetime(),
						checkTimeScopes[i - 1].getJssj());
				UFDateTime // 过滤结束时间
				filterEndtime = DateTimeUtils.min(result.getEndtime() == null ? null : result.getEndtime()
						.getDatetime(), checkTimeScope.getJssj());
				ICheckTime sbTime = findOrderedEarliestNN(checkTimes, filterBegintime, filterEndtime,
						ICheckTime.CHECK_FLAG_IN);
				if (sbTime != null)
					result.setBegintime(sbTime);
			}
			if (result.getEndtime() != null)
				continue;
			// 如果下班时间没计算出来
			UFDateTime // 过滤开始时间
			filterBegintime = DateTimeUtils.max(result.getBegintime() == null ? null : result.getBegintime()
					.getDatetime(), checkTimeScope.getKssj());
			UFDateTime // 过滤结束时间
			filterEndtime = i == checkTimeScopes.length - 1 ? checkTimeScope.getJsfromtime() : DateTimeUtils.min(
					checkTimeScopes[i + 1].getKssj(), retArray[i + 1].getBegintime() == null ? null : retArray[i + 1]
							.getBegintime().getDatetime());
			ICheckTime xbTime = findOrderedLatestNN(checkTimes, filterBegintime, filterEndtime,
					ICheckTime.CHECK_FLAG_OUT);
			if (xbTime != null)
				result.setEndtime(xbTime);
		}
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最早的一条进，或者出记录 包含时间段的开始时间、结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedEarliestYY(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (ICheckTime checkTime : checkTimes) {
			if (checkTime.getDatetime().before(beginTime))
				continue;
			if (checkTime.getDatetime().after(endTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最晚的一条进，或者出记录 包含时间段的开始时间、结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedLatestYY(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (int i = checkTimes.length - 1; i >= 0; i--) {
			ICheckTime checkTime = checkTimes[i];
			if (checkTime.getDatetime().after(endTime))
				continue;
			if (checkTime.getDatetime().before(beginTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最早的一条进，或者出记录 包含时间段的开始时间，不包含结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedEarliestYN(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (ICheckTime checkTime : checkTimes) {
			if (checkTime.getDatetime().before(beginTime))
				continue;
			if (!checkTime.getDatetime().before(endTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最晚的一条进，或者出记录 包含时间段的开始时间，不包含结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedLatestYN(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (int i = checkTimes.length - 1; i >= 0; i--) {
			ICheckTime checkTime = checkTimes[i];
			if (!checkTime.getDatetime().before(endTime))
				continue;
			if (checkTime.getDatetime().before(beginTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最早的一条进，或者出记录 不包含时间段的开始时间，包含结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedEarliestNY(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (ICheckTime checkTime : checkTimes) {
			if (!checkTime.getDatetime().after(beginTime))
				continue;
			if (checkTime.getDatetime().after(endTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最晚的一条进，或者出记录 不包含时间段的开始时间，包含结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedLatestNY(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (int i = checkTimes.length - 1; i >= 0; i--) {
			ICheckTime checkTime = checkTimes[i];
			if (checkTime.getDatetime().after(endTime))
				continue;
			if (!checkTime.getDatetime().after(beginTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最早的一条进，或者出记录 不包含时间段的开始时间，不包含结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedEarliestNN(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (ICheckTime checkTime : checkTimes) {
			if (!checkTime.getDatetime().after(beginTime))
				continue;
			if (!checkTime.getDatetime().before(endTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 在时间段范围内，查找已排序的刷签卡记录中最晚的一条进，或者出记录 不包含时间段的开始时间，不包含结束时间
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ，见ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
	 * @return
	 */
	public static ICheckTime findOrderedLatestNN(ICheckTime[] checkTimes, UFDateTime beginTime, UFDateTime endTime,
			int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		for (int i = checkTimes.length - 1; i >= 0; i--) {
			ICheckTime checkTime = checkTimes[i];
			if (!checkTime.getDatetime().before(endTime))
				continue;
			if (!checkTime.getDatetime().after(beginTime))
				return null;
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	public static ICheckTime findOrderedLatest(ICheckTime[] checkTimes, UFDateTime beginTime,
			boolean containsBeginTime, UFDateTime endTime, boolean containsEndTime, int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		if (containsBeginTime && containsEndTime)
			return findOrderedLatestYY(checkTimes, beginTime, endTime, checkFlag);
		if (containsBeginTime && !containsEndTime)
			return findOrderedLatestYN(checkTimes, beginTime, endTime, checkFlag);
		if (!containsBeginTime && containsEndTime)
			return findOrderedLatestNY(checkTimes, beginTime, endTime, checkFlag);
		return findOrderedLatestNN(checkTimes, beginTime, endTime, checkFlag);
	}

	public static ICheckTime findOrderedEarliest(ICheckTime[] checkTimes, UFDateTime beginTime,
			boolean containsBeginTime, UFDateTime endTime, boolean containsEndTime, int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		if (null == endTime || endTime.before(beginTime))
			return null;
		if (containsBeginTime && containsEndTime)
			return findOrderedEarliestYY(checkTimes, beginTime, endTime, checkFlag);
		if (containsBeginTime && !containsEndTime)
			return findOrderedEarliestYN(checkTimes, beginTime, endTime, checkFlag);
		if (!containsBeginTime && containsEndTime)
			return findOrderedEarliestNY(checkTimes, beginTime, endTime, checkFlag);
		return findOrderedEarliestNN(checkTimes, beginTime, endTime, checkFlag);
	}

	/**
	 * 找到一批已排序的刷签卡数据中，最早的一个checkin(out)卡
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 * @return
	 */
	public static ICheckTime findOrderedEarliest(ICheckTime[] checkTimes, int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		for (ICheckTime checkTime : checkTimes) {
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}

	/**
	 * 找到一批已排序的刷签卡数据中，最晚的一个checkin(out)卡
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 * @return
	 */
	public static ICheckTime findOrderedLatest(ICheckTime[] checkTimes, int checkFlag) {
		if (ArrayUtils.isEmpty(checkTimes))
			return null;
		for (int i = checkTimes.length - 1; i >= 0; i--) {
			ICheckTime checkTime = checkTimes[i];
			if (checkTime.getCheckflag() == checkFlag)
				return checkTime;
		}
		return null;
	}
}
