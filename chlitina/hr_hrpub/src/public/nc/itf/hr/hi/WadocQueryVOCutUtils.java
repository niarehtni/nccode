package nc.itf.hr.hi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class WadocQueryVOCutUtils {
	/**
	 * ���~Ӌ�㷽ʽ
	 * 
	 * @author SSX
	 * @since 2014-10-10
	 */
	public enum MoneyCalcTypeEnum {
		SUM, // ���
		NEWER // ȡ��ֵ
	}

	/**
	 * �@ȡ�ρ���VO�б��r�g�SͶӰ�и��
	 * 
	 * @param originVOs
	 *            ԭʼVO�б�
	 * @param cutterVO
	 *            �и�VO��MoneyCalcType��NEWER��ԓVO����~ȡֵ��Դ
	 * @param calcType
	 *            ���~Ӌ�㷽ʽ
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
	 * �@ȡ������ĕr�g�б�
	 * 
	 * @param originVOs
	 *            ԭʼVO�б�
	 * @param cutterVO
	 *            �и�VO
	 * @return ������VO�б�
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
				// ��������ǰ�Ƿ�������
				if (date.before(cutterVO.getBegindate())) {
					beforeDate = date;
				}

				// �������ں�o������
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
			// ��������ǰ��������ڣ����Ӳ�������-1���ֶΘ�־
			// ��t�����Ӳ������ں�o������-1���ֶΘ��I
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
				// �������ں��Ƿ�������
				if (date.after(cutterVO.getEnddate())) {
					afterDate = date;
				}

				// ��������ǰ�o������
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

			// �������ں���������ڣ����Ӳ�������+1���ֶΘ�־
			// ��t�����Ӳ���ǰ���ھo������+1���ֶΘ��I
			if (afterDate != null) {
				dates.add(cutterVO.getEnddate().getDateAfter(1));
			} else {
				dates.add(beforeDate.getDateAfter(1));
			}

			dates.add(cutterVO.getEnddate());
		}

		// ��������
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
