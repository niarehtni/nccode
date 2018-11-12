package nc.itf.hr.hi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class WadocQueryVOCutUtils {
	/**
	 * 金額計算方式
	 * 
	 * @author SSX
	 * @since 2014-10-10
	 */
	public enum MoneyCalcTypeEnum {
		SUM, // 求和
		NEWER // 取新值
	}

	/**
	 * 獲取合併後VO列表（時間軸投影切割法）
	 * 
	 * @param originVOs
	 *            原始VO列表
	 * @param cutterVO
	 *            切割VO，MoneyCalcType為NEWER，該VO為金額取值來源
	 * @param calcType
	 *            金額計算方式
	 * @return
	 */
	public static WadocQueryVO[] getCombinedVOs(WadocQueryVO[] originVOs,
			WadocQueryVO cutterVO, MoneyCalcTypeEnum calcType) {
		List<WadocQueryVO> cacheVOs = new ArrayList<WadocQueryVO>();
		if (originVOs != null && originVOs.length > 0) {
			cacheVOs.addAll(Arrays.asList(originVOs));
		}
		cacheVOs.add(cutterVO);

		List<UFDate> sortedDateList = getSortedDateList(originVOs, cutterVO);

		List<WadocQueryVO> finalVO = new ArrayList<WadocQueryVO>();
		for (int i = 0; i < sortedDateList.size() - 1; i += 2) {
			WadocQueryVO newVO = new WadocQueryVO();
			newVO.setPk_psndoc(cutterVO.getPk_psndoc());
			newVO.setBegindate(sortedDateList.get(i).asBegin());
			newVO.setEnddate(sortedDateList.get(i + 1).asEnd());
			newVO.setNmoney(UFDouble.ZERO_DBL);

			for (WadocQueryVO vo : cacheVOs) {
				if (vo.getEnddate() == null) {
					vo.setEnddate(new UFDate("9999-12-31 23:59:59"));
				}
				if ((newVO.getBegindate().isSameDate(vo.getBegindate()) || newVO
						.getBegindate().after(vo.getBegindate()))
						&& (newVO.getEnddate().isSameDate(vo.getEnddate()) || newVO
								.getEnddate().before(vo.getEnddate()))) {
					if (calcType.equals(MoneyCalcTypeEnum.SUM)) {
						newVO.setNmoney(newVO.getNmoney().add(vo.getNmoney()));
					} else if (calcType.equals(MoneyCalcTypeEnum.NEWER)) {
						if (cutterVO.equals(vo)) {
							newVO.setNmoney(cutterVO.getNmoney());
						} else {
							newVO.setNmoney(vo.getNmoney());
						}
					}
				}
			}

			finalVO.add(newVO);
		}

		return finalVO.toArray(new WadocQueryVO[0]);
	}

	/**
	 * 獲取排序後的時間列表
	 * 
	 * @param originVOs
	 *            原始VO列表
	 * @param cutterVO
	 *            切割VO
	 * @return 排序後VO列表
	 */
	public static List<UFDate> getSortedDateList(WadocQueryVO[] originVOs,
			WadocQueryVO cutterVO) {
		List<UFDate> dates = new ArrayList<UFDate>();
		if (originVOs != null && originVOs.length > 0) {
			for (WadocQueryVO vo : originVOs) {
				if (!isContains(dates, vo.getBegindate())) {
					dates.add(vo.getBegindate());
				}

				if (vo.getEnddate() == null) {
					vo.setEnddate(new UFDate("9999-12-31 23:59:59"));
				}

				if (!isContains(dates, vo.getEnddate())) {
					dates.add(vo.getEnddate());
				}
			}
		} else {
			dates.add(new UFDate("0000-01-01 00:00:00"));
			dates.add(new UFDate("9999-12-31 23:59:59"));
		}

		if (!isContains(dates, cutterVO.getBegindate())) {
			UFDate beforeDate = null;
			UFDate afterDate = null;
			for (UFDate date : dates) {
				// 插入日期前是否有日期
				if (date.before(cutterVO.getBegindate())) {
					beforeDate = date;
				}

				// 插入日期后緊鄰日期
				if (date.after(cutterVO.getBegindate())) {
					if (afterDate != null) {
						if (date.before(afterDate)) {
							afterDate = date;
						}
					} else {
						afterDate = date;
					}
				}
			}
			// 插入日期前如果有日期，增加插入日期-1天為分段標志
			// 否則，增加插入日期后緊鄰日期-1天為分段標誌
			if (beforeDate != null) {
				dates.add(cutterVO.getBegindate().getDateBefore(1));
			} else {
				dates.add(afterDate.getDateBefore(1));
			}

			dates.add(cutterVO.getBegindate());
		}

		if (cutterVO.getEnddate() == null) {
			cutterVO.setEnddate(new UFDate("9999-12-31 23:59:59"));
		}
		if (!isContains(dates, cutterVO.getEnddate())) {
			UFDate beforeDate = null;
			UFDate afterDate = null;
			for (UFDate date : dates) {
				// 插入日期后是否有日期
				if (date.after(cutterVO.getEnddate())) {
					afterDate = date;
				}

				// 插入日期前緊鄰日期
				if (date.before(cutterVO.getEnddate())) {
					if (beforeDate != null) {
						if (date.after(beforeDate)) {
							beforeDate = date;
						}
					} else {
						beforeDate = date;
					}
				}
			}

			// 插入日期后如果有日期，增加插入日期+1天為分段標志
			// 否則，增加插入前日期緊鄰日期+1天為分段標誌
			if (afterDate != null) {
				dates.add(cutterVO.getEnddate().getDateAfter(1));
			} else {
				dates.add(beforeDate.getDateAfter(1));
			}

			dates.add(cutterVO.getEnddate());
		}

		// 日期排序
		for (int i = 0; i < dates.size() - 1; i++) {
			for (int j = i + 1; j < dates.size(); j++) {
				if (dates.get(i).after(dates.get(j))) {
					UFDate tmpDate = dates.get(i);
					dates.set(i, dates.get(j));
					dates.set(j, tmpDate);
				}
			}
		}

		return dates;
	}

	private static boolean isContains(List<UFDate> dates, UFDate comparedDate) {
		for (UFDate date : dates) {
			if (date.isSameDate(comparedDate)) {
				return true;
			}
		}
		return false;
	}
}
