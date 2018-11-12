package nc.impl.hi;

import java.util.ArrayList;
import java.util.List;

import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hi.IPsndocWadocSynNHIService;
import nc.itf.hi.PsndocDefUtil;
import nc.itf.hr.hi.WadocQueryVO;
import nc.itf.hr.hi.WadocQueryVOCutUtils;
import nc.itf.hr.hi.WadocQueryVOCutUtils.MoneyCalcTypeEnum;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class PsndocWadocSynNHIServiceImpl implements IPsndocWadocSynNHIService {

	@Override
	public void synNHIByWadocQueryVO(WadocQueryVO[] wadocQueryVOs)
			throws Exception {
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
				"TWHRGLBDEF");
		service.setLazyLoad(true);

		// 支持多人批處理
		String[] psndocPKs = getPsndocPKs(wadocQueryVOs);

		for (String pk_psndoc : psndocPKs) {
			String strCondition = "pk_psndoc='" + pk_psndoc + "'";
			// 切分VOs
			WadocQueryVO[] cutterVOs = getVOsByPsndoc(wadocQueryVOs, pk_psndoc);
			if (cutterVOs.length > 0) {
				PsndocDefVO[] nhiVOs = service.queryByCondition(PsndocDefUtil
						.getPsnLaborVO().getClass(), strCondition);
				// 原始（切分後）VOs
				WadocQueryVO[] finalQueryVOs = getOriginNHIVOs(nhiVOs);
				if (wadocQueryVOs.length > 0) {
					for (int i = 0; i < cutterVOs.length; i++) {
						finalQueryVOs = WadocQueryVOCutUtils.getCombinedVOs(
								finalQueryVOs, cutterVOs[i],
								MoneyCalcTypeEnum.NEWER);
					}
				}
				PsndocDefVO[] nhiSaveVOs = getSaveVOsByCombinedVOs(
						finalQueryVOs, nhiVOs);
				List<String> updatedList = new ArrayList<String>();
				if (nhiSaveVOs != null && nhiSaveVOs.length > 0) {
					for (PsndocDefVO vo : nhiSaveVOs) {
						if (StringUtils.isEmpty(vo.getPk_psndoc_sub())) {
							service.insert(vo);
						} else {
							service.update(vo, true);
							updatedList.add(vo.getPk_psndoc_sub());
						}
					}

					UFLiteralDate startDate = new UFLiteralDate("9999-12-31");
					UFLiteralDate endDate = new UFLiteralDate("0000-01-01");
					for (PsndocDefVO nhivo : nhiSaveVOs) {
						if (nhivo.getBegindate().before(startDate)) {
							startDate = nhivo.getBegindate();
						}

						if (nhivo.getEnddate().after(endDate)) {
							endDate = nhivo.getEnddate();
						}
					}
					if (nhiVOs != null && nhiVOs.length > 0) {
						for (PsndocDefVO vo : nhiVOs) {
							if ((vo.getBegindate().isSameDate(startDate) || vo
									.getBegindate().after(startDate))
									&& (vo.getEnddate().isSameDate(endDate) || vo
											.getEnddate().before(endDate))) {
								if (!updatedList
										.contains(vo.getPk_psndoc_sub())) {
									service.delete(vo);
								}
							}
						}
					}
				}
			}
		}
	}

	private PsndocDefVO[] getSaveVOsByCombinedVOs(WadocQueryVO[] finalQueryVOs,
			PsndocDefVO[] nhiVOs) throws Exception {
		List<PsndocDefVO> finalVOList = new ArrayList<PsndocDefVO>();
		if (finalQueryVOs != null && finalQueryVOs.length > 0) {
			for (WadocQueryVO vo : finalQueryVOs) {
				PsndocDefVO newVO = PsndocDefUtil.getPsnLaborVO();
				newVO.setPk_psndoc(vo.getPk_psndoc());
				newVO.setBegindate(UFLiteralDate.getDate(vo.getBegindate()
						.toDate()));
				newVO.setEnddate(UFLiteralDate
						.getDate(vo.getEnddate().toDate()));
				newVO.setAttributeValue("glbdef2", vo.getNmoney());
				PsndocDefVO nhiVO = getPeriodContainedNHIVO(nhiVOs, vo);
				newVO = filledVOs(newVO, nhiVO);
				finalVOList.add(newVO);
			}
		}
		return finalVOList.toArray(new PsndocDefVO[0]);
	}

	private PsndocDefVO filledVOs(PsndocDefVO newVO, PsndocDefVO comVO) {
		PsndocDefVO returnedVO = null;
		if (comVO == null) {
			newVO.setLastflag(UFBoolean.TRUE);
			returnedVO = newVO;
		} else {
			// 起始時間均相同，更新源VO金額（Update）
			if (newVO.getBegindate().isSameDate(comVO.getBegindate())
					&& newVO.getEnddate().isSameDate(comVO.getEnddate())) {
				comVO.setAttributeValue("glbdef2",
						newVO.getAttributeValue("glbdef2"));
				returnedVO = comVO;
			}
			// 起始時間在源VO區間內，從源VO取除人員PK、時間、金額以外屬性值（Insert）
			else {
				for (int i = 1; i < 10; i++) {
					if (i != 2) {
						newVO.setAttributeValue(
								"glbdef" + String.valueOf(i),
								comVO.getAttributeValue("glbdef"
										+ String.valueOf(i)));
					}
					newVO.setLastflag(UFBoolean.TRUE);
				}
				returnedVO = newVO;
			}
		}
		return returnedVO;
	}

	private PsndocDefVO getPeriodContainedNHIVO(PsndocDefVO[] nhiVOs,
			WadocQueryVO vo) {
		if (nhiVOs != null && nhiVOs.length > 0) {
			for (PsndocDefVO nhivo : nhiVOs) {
				if ((vo.getBegindate().isSameDate(
						UFDate.getDate(nhivo.getBegindate().toDate())) || vo
						.getBegindate().after(
								UFDate.getDate(nhivo.getBegindate().toDate())))
						&& (vo.getEnddate().isSameDate(
								UFDate.getDate(nhivo.getEnddate().toDate())) || vo
								.getEnddate().before(
										UFDate.getDate(nhivo.getEnddate()
												.toDate())))) {
					return nhivo;
				}
			}
		}
		return null;
	}

	private WadocQueryVO[] getOriginNHIVOs(PsndocDefVO[] nhiVOs) {
		List<WadocQueryVO> vos = new ArrayList<WadocQueryVO>();
		if (nhiVOs != null) {
			for (PsndocDefVO vo : nhiVOs) {
				WadocQueryVO newVO = new WadocQueryVO();
				newVO.setPk_psndoc(vo.getPk_psndoc());
				newVO.setBegindate(UFDate.getDate(vo.getBegindate().toDate()));
				newVO.setEnddate(UFDate.getDate(vo.getEnddate().toDate()));
				newVO.setNmoney((vo.getAttributeValue("glbdef2") == null || vo
						.getAttributeValue("glbdef2").equals("~")) ? UFDouble.ZERO_DBL
						: (UFDouble) vo.getAttributeValue("glbdef2"));
			}
		}
		return null;
	}

	private WadocQueryVO[] getVOsByPsndoc(WadocQueryVO[] wadocQueryVOs,
			String pk_psndoc) {
		List<WadocQueryVO> vos = new ArrayList<WadocQueryVO>();
		if (wadocQueryVOs != null) {
			for (WadocQueryVO vo : wadocQueryVOs) {
				vos.add(vo);
			}
		}
		return vos.toArray(new WadocQueryVO[0]);
	}

	private String[] getPsndocPKs(WadocQueryVO[] wadocQueryVOs) {
		List<String> pks = new ArrayList<String>();
		if (wadocQueryVOs != null) {
			for (WadocQueryVO vo : wadocQueryVOs) {
				if (!pks.contains(vo.getPk_psndoc())) {
					pks.add(vo.getPk_psndoc());
				}
			}
		}
		return pks.toArray(new String[0]);
	}

}
