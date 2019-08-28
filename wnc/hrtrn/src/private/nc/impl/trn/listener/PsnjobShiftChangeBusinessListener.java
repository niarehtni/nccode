package nc.impl.trn.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.bd.team.team01.ITeamMaintainService;
import nc.itf.hr.wa.IPsndocwadocManageService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.team.team01.entity.AggTeamVO;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.IWaGradeCommonDef;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaPrmlvVO;
import nc.vo.wa.grade.WaPsnhiBVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.grade.WaSeclvVO;

import org.apache.commons.lang.StringUtils;

public class PsnjobShiftChangeBusinessListener implements IBusinessListener {
	private BaseDAO baseDao = null;
	private String refTransType; // ��ͣ�������
	private String refReturnType;// �}�������

	@Override
	public void doAction(IBusinessEvent eventObject) throws BusinessException {
		if (!(eventObject instanceof BusinessEvent)) {
			return;
		}

		BusinessEvent be = (BusinessEvent) eventObject;
		Object eventParams = be.getObject();

		if (eventParams == null) {
			return;
		}

		HiEventValueObject[] hiEventValueObjectArray = handleEventParamters(eventParams);

		dealPsnShift(hiEventValueObjectArray, eventObject);
		dealPsnWaDoc(hiEventValueObjectArray, eventObject);
	}

	private HiEventValueObject[] handleEventParamters(Object eventParams) {

		HiEventValueObject[] hiEventValueObjectArray = null;

		// ���녢��̎��
		if ((eventParams instanceof HiEventValueObject)) {
			hiEventValueObjectArray = new HiEventValueObject[] { (HiEventValueObject) eventParams };
		} else if ((eventParams instanceof HiEventValueObject[])) {
			hiEventValueObjectArray = (HiEventValueObject[]) eventParams;
		} else if ((eventParams instanceof HiBatchEventValueObject)) {
			HiBatchEventValueObject obj = (HiBatchEventValueObject) eventParams;
			HiEventValueObject[] eventArray = new HiEventValueObject[obj.getPk_hrorg().length];
			for (int i = 0; i < eventArray.length; i++) {
				eventArray[i] = new HiEventValueObject();
				eventArray[i].setPsnjob_before(obj.getPsnjobs_before()[i]);
				eventArray[i].setPsnjob_after(obj.getPsnjobs_after()[i]);
				eventArray[i].setPk_hrorg(obj.getPk_hrorg()[i]);
			}

			hiEventValueObjectArray = eventArray;
		} else {
			throw new IllegalArgumentException(eventParams.getClass().getName());
		}
		return hiEventValueObjectArray;
	}

	@SuppressWarnings("unchecked")
	private void dealPsnWaDoc(HiEventValueObject[] eventVOs, IBusinessEvent eventObject) throws BusinessException {
		for (HiEventValueObject vo : eventVOs) {
			PsnJobVO newPsnJob = vo.getPsnjob_after();

			if (newPsnJob == null) {
				continue;
			}

			PsndocVO psnVO = (PsndocVO) getBaseDao().retrieveByPK(PsndocVO.class, newPsnJob.getPk_psndoc());

			// �x��r��̎��
			if (newPsnJob.getTrnsevent() == 4) {
				this.getBaseDao().executeUpdate(
						"update hi_psndoc_wadoc set enddate='" + newPsnJob.getBegindate().getDateBefore(1).toString()
								+ "' where pk_psndoc='" + newPsnJob.getPk_psndoc()
								+ "' and isnull(enddate, '9999-12-31') >= '9999-01-01'");
				return;
			}

			// δ�D���ˆT�n���Ĳ������ɶ��{�Y����
			PsnOrgVO psnorg = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, newPsnJob.getPk_psnorg());
			if (psnorg.getIndocflag() == null || !psnorg.getIndocflag().booleanValue()) {
				continue;
			}

			// ��ͣ�������
			refTransType = SysInitQuery.getParaString(newPsnJob.getPk_hrorg(), "TWHR11").toString();
			// �}�������
			refReturnType = SysInitQuery.getParaString(newPsnJob.getPk_hrorg(), "TWHR12").toString();

			if (refTransType == null || refTransType.equals("~")) {
				throw new BusinessException("ϵ�y���� [TWHR11] δָ�������ͣ�Į�����͡�");
			}

			if (refReturnType == null || refReturnType.equals("~")) {
				throw new BusinessException("ϵ�y���� [TWHR12] δָ�������ͣ�}�Į�����͡�");
			}

			// ̎��ͣн��Ӱ푶��{�Y������ֵ��TRUE�r�������^�m̎�����m���{�Y�Ŀ����߉݋�����ͣн��
			if (!dealWithStopWageRemainPos(newPsnJob)) {

				List<AggWaGradeVO> aggvos = new ArrayList<AggWaGradeVO>();
				Map<String, WaCriterionVO[]> gradeCriterions = new HashMap<String, WaCriterionVO[]>();
				Map<String, Collection<WaPsnhiBVO>> gradePsnhiBs = new HashMap<String, Collection<WaPsnhiBVO>>();

				// ���dн�Y�˜����P����
				loadWaGradeInfo(newPsnJob, aggvos, gradeCriterions, gradePsnhiBs);

				for (AggWaGradeVO aggvo : aggvos) {
					Map<String, String> psnClassValues = new HashMap<String, String>();// �ˆT���eȡֵ
					Map<String, String> psnLevelValues = new HashMap<String, String>();// �ˆT�n�eȡֵ
					for (CircularlyAccessibleValueObject psnhiVO : aggvo.getTableVO(IWaGradeCommonDef.WA_PSNHI)) {
						((WaPsnhiVO) psnhiVO).getVfldcode();
						String value = null;

						if (getSourceMeta(((WaPsnhiVO) psnhiVO).getPk_flddict()).contains("hrhi.bd_psndoc")) {
							value = String.valueOf(psnVO.getAttributeValue(((WaPsnhiVO) psnhiVO).getVfldcode()));
						} else {
							value = String.valueOf(newPsnJob.getAttributeValue(((WaPsnhiVO) psnhiVO).getVfldcode()));
						}
						if (((WaPsnhiVO) psnhiVO).getClasstype() == 1) {
							// ���e
							psnClassValues.put(((WaPsnhiVO) psnhiVO).getPk_wa_psnhi(), value);
						} else {
							// �n�e
							psnLevelValues.put(((WaPsnhiVO) psnhiVO).getPk_wa_psnhi(), value);
						}
					}
					String pk_wa_item = aggvo.getParentVO().getPk_wa_item(); // н�Y���e����н�Y�Ŀ

					// ���Ҍ������H���e���n�eȡֵ�����ļ��ePK���n�ePK
					String pkFoundClass = null;
					String pkFoundLevel = null;
					if (gradePsnhiBs.containsKey((aggvo.getParentVO().getPk_wa_grd()))) {
						for (Entry<String, List<WaPsnhiBVO>> bvo : getClassGroup(
								gradePsnhiBs.get(aggvo.getParentVO().getPk_wa_grd())).entrySet()) {
							// ssx modified on 2019-08-21
							// �ޏ�ƥ��Ҏ�t���}
							String[] matchedValues = groupMatched(bvo.getValue(), psnClassValues);

							if (matchedValues != null) {
								if (psnClassValues.containsKey(matchedValues[0])) {
									pkFoundClass = matchedValues[1];
								}
							}

							matchedValues = groupMatched(bvo.getValue(), psnLevelValues);
							if (matchedValues != null) {
								if (psnLevelValues.containsKey(matchedValues[0])) {
									pkFoundLevel = matchedValues[1];
								}
							}
							// end
						}
					} else {
						continue;
					}

					UFDouble gradeSalary = UFDouble.ZERO_DBL;
					String pk_wa_crt = "";
					// �ҵ�н�Y�������e�ˆT�����O��=���e���Hֵ
					if (gradeCriterions.containsKey(aggvo.getParentVO().getPk_wa_grd())) {
						for (WaCriterionVO vlvo : gradeCriterions.get(aggvo.getParentVO().getPk_wa_grd())) {
							if (vlvo.getPk_wa_prmlv().equals(pkFoundClass)
									&& (vlvo.getPk_wa_seclv() == null || vlvo.getPk_wa_seclv().equals(pkFoundLevel))) {
								pk_wa_crt = vlvo.getPk_wa_crt();
								gradeSalary = new UFDouble(SalaryDecryptUtil.decrypt(vlvo.getCriterionvalue()
										.doubleValue()));
							}
						}
					} else {
						continue;
					}

					PsndocWadocVO existWadoc = getExistsWadoc(newPsnJob.getPk_psnjob(), pk_wa_item); // �Ѵ��ڵĶ��{�Y�Ŀ
					if (existWadoc != null) {
						// ��ǰ����ӛ��Ѵ��ڶ��{�Y�Ŀ������ԭ��ӛ�
						if (!aggvo.getParentVO().getPk_wa_grd().equals(existWadoc.getPk_wa_grd())
								|| !gradeSalary.equals(existWadoc.getNmoney())
								|| !pkFoundClass.equals(existWadoc.getPk_wa_prmlv())
								|| !pkFoundLevel.equals(existWadoc.getPk_wa_seclv())) { // н�Y�˜���������һ헲�ƥ��͸���
							existWadoc.setPk_wa_grd(aggvo.getParentVO().getPk_wa_grd());
							existWadoc.setPk_wa_crt(pk_wa_crt);
							existWadoc.setPk_wa_prmlv(pkFoundClass);
							existWadoc.setPk_wa_seclv(pkFoundLevel);
							existWadoc.setCriterionvalue(gradeSalary);
							existWadoc.setNmoney(gradeSalary);
							existWadoc.setIsrange(UFBoolean.FALSE);
							getPsndocwadocManageService().updatePsndocWadoc(existWadoc);
						}
					} else {
						// ��ǰ����ӛ䛲����ڶ��{�Y�Ŀ����Ҏ�tͣ���f�Ŀ���������Ŀ
						Collection<PsndocWadocVO> wadocs = this.getBaseDao().retrieveByClause(
								PsndocWadocVO.class,
								"pk_psndoc = '" + newPsnJob.getPk_psndoc() + "' and pk_wa_item='" + pk_wa_item
										+ "' and isnull(dr,0)=0");
						for (PsndocWadocVO wadoc : wadocs) {
							if (wadoc.getPk_wa_item().equals(pk_wa_item) // ͬһн�Y�Ŀ
									&& (wadoc.getEnddate() == null || wadoc.getEnddate()
											.after(newPsnJob.getBegindate()))// н�Y�Ŀ�ĽY�������ڹ���ӛ䛵��_ʼ����֮��
									&& UFBoolean.TRUE.equals(wadoc.getWaflag()) // �l����
							) {
								// �Y��ǰһ����Ч��н�Y�Ŀ
								this.getBaseDao().executeUpdate(
										"update hi_psndoc_wadoc set enddate='"
												+ newPsnJob.getBegindate().getDateBefore(1).toString()
												+ "', lastflag='N' where pk_psndoc_sub='" + wadoc.getPk_psndoc_sub()
												+ "'");
								break;
							}
						}

						if (gradeSalary.doubleValue() > 0) {
							this.getBaseDao().executeUpdate(
									"update hi_psndoc_wadoc set recordnum=recordnum+1 where pk_psndoc='"
											+ newPsnJob.getPk_psndoc() + "'");

							PsndocWadocVO newVO = creatNewPsndocWadocVO(newPsnJob, aggvo.getParentVO().getPk_wa_grd(),
									pk_wa_item, pkFoundClass, pkFoundLevel, gradeSalary, pk_wa_crt);
							getPsndocwadocManageService().insertPsndocWadocVO(newVO);
						}
					}
				}
			}
		}
	}

	private String getSourceMeta(String pk_flddict) throws BusinessException {
		String metadata = (String) this.getBaseDao().executeQuery(
				"select meta_data from hr_infoset_item where pk_infoset_item = '" + pk_flddict + "'",
				new ColumnProcessor());
		return metadata;
	}

	private String[] groupMatched(List<WaPsnhiBVO> groupGrade, Map<String, String> psnClassValues) {
		boolean rtn = true;
		boolean found = false;
		String foundKey = "";
		String foundValue = "";
		if (groupGrade != null && psnClassValues != null) {
			for (WaPsnhiBVO bvo : groupGrade) {
				if (psnClassValues.containsKey(bvo.getPk_wa_psnhi())) {
					if ((bvo.getVfldvalue() == null && psnClassValues.get(bvo.getPk_wa_psnhi()) == null)
							|| (bvo.getVfldvalue() != null && bvo.getVfldvalue().equals(
									psnClassValues.get(bvo.getPk_wa_psnhi())))) {
						rtn &= true;
						found = true;
						foundKey = bvo.getPk_wa_psnhi();
						foundValue = bvo.getPk_wa_grdlv();
					} else {
						return null;
					}
				} else {
					found = found | false;
				}
			}
		}
		if (rtn & found) {
			return new String[] { foundKey, foundValue };
		} else {
			return null;
		}
	}

	private Map<String, List<WaPsnhiBVO>> getClassGroup(Collection<WaPsnhiBVO> bvos) {
		Map<String, List<WaPsnhiBVO>> groupMap = new HashMap<String, List<WaPsnhiBVO>>();
		for (WaPsnhiBVO bvo : bvos) {
			if (!groupMap.containsKey(bvo.getSortgroup())) {
				groupMap.put(bvo.getSortgroup(), new ArrayList<WaPsnhiBVO>());
			}

			groupMap.get(bvo.getSortgroup()).add(bvo);
		}
		return groupMap;
	}

	@SuppressWarnings("unchecked")
	private boolean dealWithStopWageRemainPos(PsnJobVO newPsnJob) throws BusinessException {
		Collection existVOs = this.getBaseDao().retrieveByClause(PsndocWadocVO.class,
				"pk_psnjob='" + newPsnJob.getPk_psnjob() + "' and isnull(dr,0)=0");
		if (existVOs != null && existVOs.size() > 0) {
			// ��ǰ����ӛ������ɶ��{�Y���J�����޸Ĳ���
			return false;
		}
		String strWhere = getGradeWhereFilter(newPsnJob);

		if (refTransType.equals(newPsnJob.getTrnstype())) {
			// ͣн��
			Collection<PsndocWadocVO> wadocs = this.getBaseDao().retrieveByClause(
					PsndocWadocVO.class,
					"pk_psndoc = '" + newPsnJob.getPk_psndoc()
							+ "' and waflag='Y' and lastflag='Y' and isnull(dr, 0)=0");
			for (PsndocWadocVO wadoc : wadocs) {
				if (wadoc.getEnddate() == null || wadoc.getEnddate().after(newPsnJob.getBegindate().getDateBefore(1))) // �Y���������ͣн���_ʼ����ǰһ��
				{
					wadoc.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt(wadoc.getNmoney() == null ? 0 : wadoc
							.getNmoney().doubleValue())));
					wadoc.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil.decrypt(wadoc.getCriterionvalue() == null ? 0 : wadoc.getCriterionvalue()
									.doubleValue())));
					wadoc.setChangedate(new UFLiteralDate());
					// wadoc.setWaflag(UFBoolean.FALSE);
					wadoc.setWaflag(UFBoolean.TRUE);
					wadoc.setEnddate(newPsnJob.getBegindate().getDateBefore(1));
					wadoc.setIsrange(wadoc.getIsrange() == null ? UFBoolean.FALSE : wadoc.getIsrange());
					getPsndocwadocManageService().updatePsndocWadoc(wadoc);
				}
			}

			return true;
		} else if (refReturnType.equals(newPsnJob.getTrnstype())) {
			// ��ͣ��
			Collection<PsndocWadocVO> wadocs = this.getBaseDao().retrieveByClause(
					PsndocWadocVO.class,
					"pk_psndoc = '" + newPsnJob.getPk_psndoc()
							+ "' and lastflag='Y' and isnull(dr, 0)=0 and pk_wa_item not in ("
							+ "select pk_wa_item from wa_grade where pk_org='" + newPsnJob.getPk_org()
							+ "' and isnull(dr,0)=0" + strWhere + ")");

			for (PsndocWadocVO wadoc : wadocs) {
				if (wadoc.getEnddate() != null
						&& wadoc.getEnddate().isSameDate(
								getTransTypeEndDate(newPsnJob.getPk_org(), newPsnJob.getBegindate(), refTransType,
										newPsnJob.getPk_psndoc()))) // �Y��������ͣн���_ʼ����ǰһ���
				{
					wadoc.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt(wadoc.getNmoney() == null ? 0 : wadoc
							.getNmoney().doubleValue())));
					wadoc.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil.decrypt(wadoc.getCriterionvalue() == null ? 0 : wadoc.getCriterionvalue()
									.doubleValue())));
					wadoc.setLastflag(UFBoolean.FALSE);
					wadoc.setIsrange(UFBoolean.FALSE);
					getPsndocwadocManageService().updatePsndocWadoc(wadoc);

					PsndocWadocVO newVO = (PsndocWadocVO) wadoc.clone();
					newVO.setBegindate(newPsnJob.getBegindate());
					newVO.setEnddate(newPsnJob.getEnddate());
					newVO.setPk_psnjob(newPsnJob.getPk_psnjob());
					newVO.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt(newVO.getNmoney() == null ? 0 : newVO
							.getNmoney().doubleValue())));
					newVO.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil.decrypt(newVO.getCriterionvalue() == null ? 0 : newVO.getCriterionvalue()
									.doubleValue())));
					newVO.setPk_changecause(null);
					newVO.setTs(null);
					newVO.setChangedate(new UFLiteralDate());
					newVO.setIsrange(wadoc.getIsrange() == null ? UFBoolean.FALSE : wadoc.getIsrange());
					newVO.setPk_psndoc_sub(null);
					newVO.setWaflag(UFBoolean.TRUE);
					newVO.setLastflag(UFBoolean.TRUE);
					getPsndocwadocManageService().insertPsndocWadocVO(newVO);
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private UFLiteralDate getTransTypeEndDate(String pk_org, UFLiteralDate begindate, String refTransType,
			String pk_psndoc) throws BusinessException {
		int minDays = Integer.MAX_VALUE;
		PsnJobVO retvo = null;
		Collection<PsnJobVO> psnjobs = this.getBaseDao().retrieveByClause(
				PsnJobVO.class,
				"trnstype='" + refTransType + "' and pk_psndoc='" + pk_psndoc + "' and pk_org='" + pk_org
						+ "' and isnull(dr,0)=0");
		for (PsnJobVO psnjob : psnjobs) {
			int days = UFLiteralDate.getDaysBetween(psnjob.getBegindate().getDateBefore(1), begindate);
			if (minDays > days) {
				minDays = days;
				retvo = psnjob;
			}
		}
		return retvo == null ? new UFLiteralDate("9999-12-31") : retvo.getBegindate().getDateBefore(1);
	}

	private PsndocWadocVO creatNewPsndocWadocVO(PsnJobVO newPsnJob, String pk_wa_grd, String pk_wa_item,
			String pkFoundClass, String pkFoundLevel, UFDouble gradeSalary, String pk_wa_crt) {
		PsndocWadocVO newVO = new PsndocWadocVO();
		newVO.setPk_group(newPsnJob.getPk_group());
		newVO.setPk_org(newPsnJob.getPk_org());
		newVO.setPk_psndoc(newPsnJob.getPk_psndoc());
		newVO.setPk_psnjob(newPsnJob.getPk_psnjob());
		newVO.setPk_wa_item(pk_wa_item);
		newVO.setPk_wa_grd(pk_wa_grd);
		newVO.setPk_wa_crt(pk_wa_crt);
		newVO.setPk_wa_prmlv(pkFoundClass);
		newVO.setPk_wa_seclv(StringUtil.isEmpty(pkFoundLevel) ? null : pkFoundLevel);
		newVO.setBegindate(newPsnJob.getBegindate());
		newVO.setChangedate(new UFLiteralDate());
		newVO.setCriterionvalue(gradeSalary);
		newVO.setNmoney(gradeSalary);
		newVO.setNegotiation_wage(UFBoolean.FALSE);
		newVO.setWaflag(UFBoolean.TRUE);
		newVO.setLastflag(UFBoolean.TRUE);
		newVO.setPartflag(UFBoolean.FALSE);
		newVO.setIadjustmatter(1);
		newVO.setAssgid(newPsnJob.getAssgid());
		newVO.setDr(0);
		newVO.setRecordnum(0);
		newVO.setWorkflowflag(UFBoolean.FALSE);
		newVO.setIsrange(UFBoolean.FALSE);
		newVO.setIadjustmatter(1);
		return newVO;
	}

	@SuppressWarnings("unchecked")
	private PsndocWadocVO getExistsWadoc(String pk_psnjob, String pk_wa_item) throws BusinessException {
		Collection<PsndocWadocVO> vos = this.getBaseDao().retrieveByClause(PsndocWadocVO.class,
				"pk_psnjob='" + pk_psnjob + "' and pk_wa_item='" + pk_wa_item + "' and isnull(dr,0)=0");
		if (vos != null && vos.size() > 0) {
			return vos.toArray(new PsndocWadocVO[0])[0];
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private void loadWaGradeInfo(PsnJobVO newPsnJob, List<AggWaGradeVO> aggvos,
			Map<String, WaCriterionVO[]> gradeCriterions, Map<String, Collection<WaPsnhiBVO>> gradePsnhiBs)
			throws BusinessException {
		String strWhere = getGradeWhereFilter(newPsnJob);

		Collection<WaGradeVO> gradevos = this.getBaseDao().retrieveByClause(WaGradeVO.class,
				"pk_org='" + newPsnJob.getPk_org() + "' and isnull(dr,0)=0 " + strWhere);

		for (WaGradeVO gradevo : gradevos) {
			Collection<WaPsnhiVO> psnhis = this.getBaseDao().retrieveByClause(WaPsnhiVO.class,
					" pk_wa_grd = '" + gradevo.getPk_wa_grd() + "' and isnull(dr,0)=0");

			// �M��н�Y���e���P����
			if (psnhis != null && psnhis.size() > 0) {

				// н�Y���eAggVO
				AggWaGradeVO aggvo = new AggWaGradeVO();

				// н�Y�˜ʱ�
				aggvo.setParentVO(gradevo);

				// ���e���n�e�O��
				aggvo.setTableVO(IWaGradeCommonDef.WA_PSNHI, psnhis.toArray(new WaPsnhiVO[0]));

				// ���e
				Collection<WaPrmlvVO> prmlvs = this.getBaseDao().retrieveByClause(WaPrmlvVO.class,
						"pk_wa_grd='" + gradevo.getPk_wa_grd() + "'  and isnull(dr,0)=0 ");
				if (prmlvs != null && prmlvs.size() > 0) {
					aggvo.setTableVO(IWaGradeCommonDef.WA_PRMLV, prmlvs.toArray(new WaPrmlvVO[0]));
				}

				// �n�e
				Collection<WaSeclvVO> seclvs = this.getBaseDao().retrieveByClause(WaSeclvVO.class,
						"pk_wa_grd='" + gradevo.getPk_wa_grd() + "' and isnull(dr,0)=0");
				if (seclvs != null && seclvs.size() > 0) {
					aggvo.setTableVO(IWaGradeCommonDef.WA_SECLV, seclvs.toArray(new WaSeclvVO[0]));
				}

				aggvos.add(aggvo);
				//

				// н�Y�˜ʱ�
				Collection<WaCriterionVO> criterionvos = this.getBaseDao().retrieveByClause(WaCriterionVO.class,
						"pk_wa_grd='" + gradevo.getPk_wa_grd() + "' and isnull(dr,0)=0");
				if (criterionvos != null && criterionvos.size() > 0) {
					gradeCriterions.put(gradevo.getPk_wa_grd(), criterionvos.toArray(new WaCriterionVO[0]));
				}
				//

				for (WaPsnhiVO psnhi : psnhis) {
					// ���e�ˆT�����O�ã��n�e�ˆT�����O��
					Collection<WaPsnhiBVO> psnhibvos = this.getBaseDao().retrieveByClause(WaPsnhiBVO.class,
							"pk_wa_psnhi='" + psnhi.getPk_wa_psnhi() + "' and isnull(dr,0)=0");
					if (psnhibvos != null && psnhibvos.size() > 0) {
						if (!gradePsnhiBs.containsKey(gradevo.getPk_wa_grd())) {
							gradePsnhiBs.put(gradevo.getPk_wa_grd(), psnhibvos);
						} else {
							gradePsnhiBs.get(gradevo.getPk_wa_grd()).addAll(psnhibvos);
						}
					}
				}
				//
			}
		}
	}

	private String getGradeWhereFilter(PsnJobVO newPsnJob) throws BusinessException {
		String expCodes = SysInitQuery.getParaString(newPsnJob.getPk_org(), "HRWAWNC01");
		String strWhere = "";
		if (!StringUtils.isEmpty(expCodes)) {
			for (String code : expCodes.replace("��", ",").split(",")) {
				if (!StringUtils.isEmpty(code)) {
					if (StringUtils.isEmpty(strWhere)) {
						strWhere = "'" + code.trim() + "'";
					} else {
						strWhere += ",'" + code.trim() + "'";
					}
				}
			}
			if (!StringUtils.isEmpty(strWhere)) {
				strWhere = "and code not in (" + strWhere + ")";
			}
		}
		return strWhere;
	}

	private void dealPsnShift(HiEventValueObject[] eventVOs, IBusinessEvent eventObject) throws BusinessException {
		for (HiEventValueObject vo : eventVOs) {
			PsnJobVO oldPsnJob = vo.getPsnjob_before();
			PsnJobVO newPsnJob = vo.getPsnjob_after();

			// �x��r��̎��
			if (newPsnJob.getTrnsevent() == 4) {
				return;
			}

			String pk_hrorg = vo.getPk_hrorg();

			String oldShift = getOldShift(eventObject, oldPsnJob);
			String newShift = newPsnJob.getAttributeValue("jobglbdef7") == null ? "" : (String) newPsnJob
					.getAttributeValue("jobglbdef7");

			if (newPsnJob == null) {
				continue;
			}

			// δ�D���ˆT�n���Ĳ������ɶ��{�Y����
			PsnOrgVO psnorg = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, newPsnJob.getPk_psnorg());
			if (psnorg.getIndocflag() == null || !psnorg.getIndocflag().booleanValue()) {
				continue;
			}
			// oldShift��newShift����ͬ������l����M����
			if (!oldShift.equals(newShift)) {
				changeShiftGroup(pk_hrorg, oldPsnJob, newPsnJob, eventObject); // �޸İ���

				if (StringUtils.isEmpty(newShift) && oldPsnJob != null) {
					// ͬ�����
					((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class))
							.sync2TeamCalendar(
									pk_hrorg,
									oldShift,
									new String[] { newPsnJob.getPk_psndoc() },
									oldPsnJob.getBegindate(),
									findEndDate(oldShift, newPsnJob.getBegindate() == null ? "9999-12-31" : newPsnJob
											.getBegindate().getDateBefore(1).toString()));
				} else {
					// ͬ�����
					((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class))
							.sync2TeamCalendar(
									pk_hrorg,
									(StringUtils.isEmpty(newShift) ? null : newShift),
									new String[] { newPsnJob.getPk_psndoc() },
									newPsnJob.getBegindate(),
									findEndDate(newShift, newPsnJob.getEnddate() == null ? "9999-12-31" : newPsnJob
											.getEnddate().toString()));
				}
			}
		}
	}

	public String getOldShift(IBusinessEvent eventObject, PsnJobVO oldPsnJob) {
		return (oldPsnJob == null || oldPsnJob.getAttributeValue("jobglbdef7") == null || (eventObject.getEventType()
				.equals("600702") && eventObject.getSourceID().equals("218971f0-e5dc-408b-9a32-56529dddd4db"))) ? ""
				: (String) oldPsnJob.getAttributeValue("jobglbdef7");
	}

	private UFLiteralDate findEndDate(String cteamid, String psnjobEnddate) throws BusinessException {
		// ȡ�������Ű����һ��
		// ȡ��Ա��ְ�����һ��
		// ȡ���ڽ�С��һ��
		String strSQL = "select calendar from tbm_psncalendar where pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psnjob in (select pk_psnjob from bd_team_b where cteamid = '"
				+ cteamid + "')) and calendar<='" + psnjobEnddate + "' order by calendar desc";
		String maxDate = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		return new UFLiteralDate(StringUtils.isEmpty(maxDate) ? psnjobEnddate : maxDate);
	}

	private void changeShiftGroup(String pk_hrorg, PsnJobVO oldPsnJob, PsnJobVO newPsnJob, IBusinessEvent eventObject)
			throws BusinessException {
		// �Y���f��M
		if (oldPsnJob != null) {
			finishOldShift(oldPsnJob, newPsnJob, eventObject);
		}
		// �_ʼ�°�M
		startNewShift(newPsnJob);
	}

	@SuppressWarnings("unchecked")
	private void finishOldShift(PsnJobVO oldPsnJob, PsnJobVO newPsnJob, IBusinessEvent eventObject)
			throws BusinessException {
		String oldShift = getOldShift(eventObject, oldPsnJob);
		// �f��M��Օr�������Y������
		if (!StringUtils.isEmpty(oldShift)) {
			Collection<TeamItemVO> itemVOs = this.getBaseDao().retrieveByClause(TeamItemVO.class,
					"cteamid='" + oldShift + "' and isnull(dr,0)=0");

			Collection<TeamItemVO> updateItemVOs = new ArrayList<TeamItemVO>();

			for (TeamItemVO vo : itemVOs) {
				// ssx added on 2019-08-18
				// �ڰ�Mͬһ�����ж��lӛ䛕r���������ж��O���Y������
				UFLiteralDate originEnddate = vo.getDenddate() == null ? new UFLiteralDate("9999-12-31") : vo
						.getDenddate();
				if (vo.getPk_psnjob().equals(oldPsnJob.getPk_psnjob())
						&& originEnddate.after(newPsnJob.getBegindate().getDateBefore(1))) {
					vo.setDenddate(newPsnJob.getBegindate().getDateBefore(1));
					vo.setStatus(VOStatus.UPDATED);
					updateItemVOs.add(vo);
				}
			}

			if (updateItemVOs.size() > 0) {
				TeamHeadVO headVO = (TeamHeadVO) this.getBaseDao().retrieveByPK(TeamHeadVO.class, oldShift);
				updateShiftGroup(headVO, updateItemVOs);
			}
		}

	}

	private void updateShiftGroup(TeamHeadVO headVO, Collection<TeamItemVO> itemVOs) throws BusinessException {
		AggTeamVO aggVO = new AggTeamVO();
		aggVO.setParent(headVO);
		aggVO.setChildrenVO(itemVOs.toArray(new TeamItemVO[0]));
		aggVO.getParentVO().setStatus(1);

		NCLocator.getInstance().lookup(ITeamMaintainService.class).update(new AggTeamVO[] { aggVO });
	}

	private void startNewShift(PsnJobVO newPsnJob) throws BusinessException {
		String newShift = ((String) newPsnJob.getAttributeValue("jobglbdef7"));
		// �°�M��Օr�������_ʼ����
		if (!StringUtils.isEmpty(newShift)) {
			TeamHeadVO headVO = (TeamHeadVO) this.getBaseDao().retrieveByPK(TeamHeadVO.class,
					(String) newPsnJob.getAttributeValue("jobglbdef7"));
			Collection<TeamItemVO> insertItemVOs = new ArrayList<TeamItemVO>();

			// �����³�ԱTeamItemVO������itemVOs
			TeamItemVO newMemberVO = new TeamItemVO();
			newMemberVO.setPk_group(headVO.getPk_group());
			newMemberVO.setPk_org(headVO.getPk_org());
			newMemberVO.setPk_org_v(headVO.getPk_org_v());
			newMemberVO.setPk_dept(newPsnJob.getPk_dept());
			newMemberVO.setPk_psncl(newPsnJob.getPk_psncl());
			newMemberVO.setPk_psnjob(newPsnJob.getPk_psnjob());
			newMemberVO.setCworkmanid(newPsnJob.getPk_psndoc());
			newMemberVO.setCteamid(headVO.getCteamid());
			newMemberVO.setBmanager(UFBoolean.FALSE);
			newMemberVO.setDr(0);
			newMemberVO.setDstartdate(newPsnJob.getBegindate());
			newMemberVO.setDenddate(newPsnJob.getEnddate());
			newMemberVO.setStatus(VOStatus.NEW);
			insertItemVOs.add(newMemberVO);

			updateShiftGroup(headVO, insertItemVOs);
		}
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public void setBaseDao(BaseDAO baseDao) {
		this.baseDao = baseDao;
	}

	public static IPsndocwadocManageService getPsndocwadocManageService() {
		return (IPsndocwadocManageService) NCLocator.getInstance().lookup(IPsndocwadocManageService.class);
	}

}
