package nc.impl.trn.listener;

import java.util.Collection;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

public class PsnjobSynWorkAgeBusinessListener implements IBusinessListener {

	private String PSNJOB_LEAVEDAYS = "jobglbdef4";
	private String PSNORG_TOTALLEAVEDAYS = "orgglbdef2";
	private String PSNORG_REALSTARTDATE = "orgglbdef3";

	private BaseDAO baseDao = null;

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

		dealPsnWorkAge(hiEventValueObjectArray);
	}

	private HiEventValueObject[] handleEventParamters(Object eventParams) {

		HiEventValueObject[] hiEventValueObjectArray = null;

		if ((eventParams instanceof HiEventValueObject)) {
			hiEventValueObjectArray = new HiEventValueObject[] { (HiEventValueObject) eventParams };
		} else if ((eventParams instanceof HiEventValueObject[])) {
			hiEventValueObjectArray = (HiEventValueObject[]) eventParams;
		} else if ((eventParams instanceof HiBatchEventValueObject)) {
			HiBatchEventValueObject obj = (HiBatchEventValueObject) eventParams;
			HiEventValueObject[] eventArray = new HiEventValueObject[obj
					.getPk_hrorg().length];
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

	private void dealPsnWorkAge(HiEventValueObject[] eventVOs)
			throws BusinessException {
		for (HiEventValueObject vo : eventVOs) {
			PsnJobVO lastPsnJob = vo.getPsnjob_before();
			PsnJobVO newPsnJob = vo.getPsnjob_after();
			String pk_hrorg = vo.getPk_hrorg();

			// 是否啟用年資起算日
			UFBoolean refEnableWorkAgeFunc = SysInitQuery.getParaBoolean(
					pk_hrorg, "TWHR10");

			if (refEnableWorkAgeFunc != null
					&& refEnableWorkAgeFunc.booleanValue()) {
				// 留停異動類型
				String refTransType = SysInitQuery.getParaString(pk_hrorg,
						"TWHR11").toString();

				if (refTransType == null || refTransType.equals("~")) {
					throw new BusinessException("系統參數 [TWHR11] 未指定用於留停異動類型。");
				}

				UFLiteralDate lastEndDate = null;

				lastPsnJob
						.setEnddate(newPsnJob.getBegindate().getDateBefore(1));
				lastPsnJob.setPoststat(UFBoolean.FALSE);
				lastPsnJob.setRecordnum(lastPsnJob.getRecordnum() + 1);
				lastPsnJob.setEndflag(UFBoolean.TRUE);
				lastPsnJob.setLastflag(UFBoolean.FALSE);
				newPsnJob.setRecordnum(0);
				newPsnJob.setLastflag(UFBoolean.TRUE);

				calculateWorkAgeDate(lastPsnJob, refTransType, lastEndDate);
				calculateWorkAgeDate(newPsnJob, refTransType, null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void calculateWorkAgeDate(PsnJobVO psnjob, String refTransType,
			UFLiteralDate updateEndDate) throws BusinessException {
		if (refTransType.equals(psnjob.getTrnstype())) {
			// 留停結束時，計算留停天數，存儲位置：人員工作記錄.留停天數
			if (psnjob.getBegindate() == null || psnjob.getEnddate() == null) {
				psnjob.setAttributeValue(PSNJOB_LEAVEDAYS, null);
			} else {
				psnjob.setAttributeValue(PSNJOB_LEAVEDAYS, UFLiteralDate
						.getDaysBetween(psnjob.getBegindate(),
								psnjob.getEnddate()));
			}
			this.getBaseDao().updateVO(psnjob);

			// 計算纍計留停天數，存儲位置：組織關係.纍計留停天數
			Collection<PsnJobVO> jobs = this.getBaseDao().retrieveByClause(
					PsnJobVO.class,
					"trnstype='" + refTransType + "' and pk_psndoc='"
							+ psnjob.getPk_psndoc() + "' and pk_psnorg='"
							+ psnjob.getPk_psnorg() + "'");
			int days = 0;
			if (jobs != null && jobs.size() > 0) {
				for (PsnJobVO job : jobs) {
					if (job.getAttributeValue(PSNJOB_LEAVEDAYS) != null
							&& !job.getAttributeValue(PSNJOB_LEAVEDAYS).equals(
									"~")) {
						days += (Integer) job
								.getAttributeValue(PSNJOB_LEAVEDAYS);
					}
				}
			}

			// 計算年資起算日
			// 組織關係.年資起算日=(在職員工|留停天數=null).進入集團日期
			// 組織關係.年資起算日=(在職員工|留停天數<>null).進入集團日期+纍計留停天數
			PsnOrgVO psnorgVO = (PsnOrgVO) this.getBaseDao().retrieveByPK(
					PsnOrgVO.class, psnjob.getPk_psnorg());
			psnorgVO.setAttributeValue(PSNORG_TOTALLEAVEDAYS, days);
			psnorgVO.setAttributeValue(PSNORG_REALSTARTDATE, psnorgVO
					.getBegindate().getDateAfter(days));
			this.getBaseDao().updateVO(psnorgVO);
		} else {
			// 非留停類型的異動，將留停日置為0
			psnjob.setAttributeValue(PSNJOB_LEAVEDAYS, null);
			this.getBaseDao().updateVO(psnjob);
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

}
