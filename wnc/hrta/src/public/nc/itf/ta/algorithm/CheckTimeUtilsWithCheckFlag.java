package nc.itf.ta.algorithm;

import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.ArrayUtils;

/**
 * �Ž�ˢǩ������Ĺ�����
 * 
 * @author zengcheng
 * 
 */
public class CheckTimeUtilsWithCheckFlag {
	/**
	 * ����ǩ��ǩ����־������£��򵥼���ÿ��ˢ���Σ�ע�⣬��ˢ���Σ����ǹ���ʱ��Σ����˷����������Ĳ���Ӧ����ˢ���Σ������°�ʱ��㡣
	 * �˼�����������ļ��㣬ֻ����ˢǩ�������Լ��涨�����°�ʱ��㣬�����Ǹ��ֵ��ݡ� ���㷨�뵯�԰��޹أ������˼������漰������ʱ��㶼�ǹ̶��㡣
	 * ���԰�ļ�����ܻ���ô˷��������������� Ҫ��ˢǩ��ʱ���Ѿ�����ε�ˢ����ʼʱ�����ʱ�����
	 * ���ص�CheckTimeResultDescriber[]������ICompleteCheckTimeScope[]һ��
	 */
	public static CheckTimeResultDescriber[] simpleCalculateCheckInOutTime(ICheckTime[] checkTimes,
			ICompleteCheckTimeScope[] checkTimeScopes) {
		if (ArrayUtils.isEmpty(checkTimeScopes))
			return null;
		CheckTimeResultDescriber[] retArray = CheckTimeUtils
				.initCheckTimeResultDescriberByCompleteCheckTimeScopes(checkTimeScopes);
		if (ArrayUtils.isEmpty(checkTimes))
			return retArray;
		// ���Ƚ��е�һ�ֲ���
		firstSearch(checkTimes, checkTimeScopes, retArray);
		// Ȼ����еڶ��ֲ���
		secondSearch(checkTimes, checkTimeScopes, retArray);
		return retArray;
	}

	/**
	 * ��һ�ֲ��� �����㷨��viso����
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
			// �����ϰ�ʱ�䣬�ȴ��ϰ�ο�ʼʱ�䣨�������ϰ�ν���ʱ�䣨�������ڲ��������ǩ���¼
			ICheckTime sbTime = findOrderedEarliestYN(checkTimes, checkTimeScope.getKsfromtime(),
					checkTimeScope.getKstotime(), ICheckTime.CHECK_FLAG_IN);
			if (sbTime != null)
				result.setBegintime(sbTime);
			// Ȼ������°�ʱ�䣬���ϰ�ʱ���Ƿ�Ϊ�շ��������������
			ICheckTime xbTime = null;
			// ����ϰ�ʱ��ǿգ���ȡ��later(�ϰ࿨,�涨�ϰ�ʱ��)֮��(����),�°�ˢ���ν���ʱ��֮ǰ(����,�������һ��ˢ���Σ���)���°�ˢǩ������(����ʼʱ���,��������ʱ���)�������һ��
			if (sbTime != null) {
				xbTime = findOrderedLatest(checkTimes,
						DateTimeUtils.max(sbTime.getDatetime(), checkTimeScope.getKssj()), false,
						checkTimeScope.getJstotime(), i == checkTimeScopes.length - 1, ICheckTime.CHECK_FLAG_OUT);
			}
			// ����ϰ�ʱ��Ϊ�գ���ȡ���°࿨ˢ�����ڵ�����ǩ��ˢǩ������(����ʼʱ���,��������ʱ���,�������һ��ˢ���Σ���),ȡ�����һ��
			else {
				xbTime = findOrderedLatest(checkTimes, checkTimeScope.getJsfromtime(), false,
						checkTimeScope.getJstotime(), i == checkTimeScopes.length - 1, ICheckTime.CHECK_FLAG_OUT);
			}
			if (xbTime != null)
				result.setEndtime(xbTime);
		}
	}

	/**
	 * �ڶ��ֲ��� �����㷨��viso����
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
			if (result.getBegintime() == null) {// ����ϰ�ʱ��û�м������������Ҫ��ǰһ���°�ν裬�����ұ��°�ν�
				UFDateTime // ���˿�ʼʱ��
				filterBegintime = i == 0 ? checkTimeScope.getJsfromtime() : DateTimeUtils.max(
						retArray[i - 1].getEndtime() == null ? null : retArray[i - 1].getEndtime().getDatetime(),
						checkTimeScopes[i - 1].getJssj());
				UFDateTime // ���˽���ʱ��
				filterEndtime = DateTimeUtils.min(result.getEndtime() == null ? null : result.getEndtime()
						.getDatetime(), checkTimeScope.getJssj());
				ICheckTime sbTime = findOrderedEarliestNN(checkTimes, filterBegintime, filterEndtime,
						ICheckTime.CHECK_FLAG_IN);
				if (sbTime != null)
					result.setBegintime(sbTime);
			}
			if (result.getEndtime() != null)
				continue;
			// ����°�ʱ��û�������
			UFDateTime // ���˿�ʼʱ��
			filterBegintime = DateTimeUtils.max(result.getBegintime() == null ? null : result.getBegintime()
					.getDatetime(), checkTimeScope.getKssj());
			UFDateTime // ���˽���ʱ��
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ����ʱ��εĿ�ʼʱ�䡢����ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ����ʱ��εĿ�ʼʱ�䡢����ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ����ʱ��εĿ�ʼʱ�䣬����������ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ����ʱ��εĿ�ʼʱ�䣬����������ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ������ʱ��εĿ�ʼʱ�䣬��������ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ������ʱ��εĿ�ʼʱ�䣬��������ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ������ʱ��εĿ�ʼʱ�䣬����������ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * ��ʱ��η�Χ�ڣ������������ˢǩ����¼�������һ���������߳���¼ ������ʱ��εĿ�ʼʱ�䣬����������ʱ��
	 * 
	 * @param checkTimes
	 * @param checkFlag
	 *            ����ICheckTime.CHECK_FLAG_IN,ICheckTime.CHECK_FLAG_OUT
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
	 * �ҵ�һ���������ˢǩ�������У������һ��checkin(out)��
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
	 * �ҵ�һ���������ˢǩ�������У������һ��checkin(out)��
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
