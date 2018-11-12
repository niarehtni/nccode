package nc.impl.trn.listener;

import java.util.Collection;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

public class PsnjobSynWorkAgeBusinessListener implements IBusinessListener {
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

	private void dealPsnWorkAge(HiEventValueObject[] eventVOs) throws BusinessException {
		for (HiEventValueObject vo : eventVOs) {
			PsnJobVO lastPsnJob = vo.getPsnjob_before();
			PsnJobVO newPsnJob = vo.getPsnjob_after();
			String pk_hrorg = vo.getPk_hrorg();

			// 是否啟用年資起算日
			UFBoolean refEnableWorkAgeFunc = SysInitQuery.getParaBoolean(pk_hrorg, "TWHR10");

			if (refEnableWorkAgeFunc != null && refEnableWorkAgeFunc.booleanValue()) {
				// 留停異動類型
				String refTransType = SysInitQuery.getParaString(pk_hrorg, "TWHR11").toString();
				// 複職異動類型
				String refReturnType = SysInitQuery.getParaString(pk_hrorg, "TWHR12").toString();

				if (refTransType == null || refTransType.equals("~")) {
					throw new BusinessException("系統參數 [TWHR11] 未指定用於留停的異動類型。");
				}

				if (refReturnType == null || refReturnType.equals("~")) {
					throw new BusinessException("系統參數 [TWHR12] 未指定用於留停複職的異動類型。");
				}

				lastPsnJob.setEnddate(newPsnJob.getBegindate().getDateBefore(1));
				lastPsnJob.setPoststat(UFBoolean.FALSE);
				lastPsnJob.setRecordnum(lastPsnJob.getRecordnum() + 1);
				lastPsnJob.setEndflag(UFBoolean.TRUE);
				lastPsnJob.setLastflag(UFBoolean.FALSE);
				newPsnJob.setRecordnum(0);
				newPsnJob.setLastflag(UFBoolean.TRUE);

				calculateWorkAgeDate(lastPsnJob, refTransType, refReturnType, false);
				calculateWorkAgeDate(newPsnJob, refTransType, refReturnType, false);
			}
		}
	}

	public void calculateWorkAgeDate(PsnJobVO psnjob, String refTransType, String refReturnType, boolean isSyn)
			throws BusinessException {
		// 是否計算纍計留停
		boolean isCalculateLeaveDays = false;

		if (refTransType.equals(psnjob.getTrnstype())) {
			// 異動類型為留停的，計算纍計留停
			isCalculateLeaveDays = true;
		} else if (!refTransType.equals(psnjob.getTrnstype())) {
			// 非留停但是在留停期間的，計算纍計留停
			if (transInLeavePeriod(psnjob, refTransType, refReturnType)) {
				isCalculateLeaveDays = true;
			}
		}

		if (isCalculateLeaveDays || isSyn) {
			// 計算纍計留停
			int days = calculateLeaveDaysOnPsnJob(psnjob, isCalculateLeaveDays);
			calculateWorkAgeStartDateOnPsnOrg(psnjob, days);
		} else {
			// 留停日置為0
			psnjob.setAttributeValue("leavedays", null);
			this.getBaseDao().updateVO(psnjob);
		}

	}

	@SuppressWarnings("unchecked")
	private boolean transInLeavePeriod(PsnJobVO psnjob, String refTransType, String refReturnType)
			throws BusinessException {
		Collection<PsnJobVO> jobs = this.getBaseDao().retrieveByClause(
				PsnJobVO.class,
				"pk_psndoc='" + psnjob.getPk_psndoc() + "' and pk_psnorg='" + psnjob.getPk_psnorg()
						+ "' and isnull(dr,0)=0", "begindate");

		if (jobs == null || jobs.size() == 0) {
			return false;
		} else {
			boolean isLeave = false;
			for (PsnJobVO vo : jobs) {
				if (psnjob.getPk_psnjob().equals(vo.getPk_psnjob())) {
					// 遇到當前檢查記錄就跳出
					if (refReturnType.equals(vo.getTrnstype())) {
						isLeave = false;
					}

					break;
				} else {
					if (refTransType.equals(vo.getTrnstype())) {
						isLeave = true;
					} else if (refReturnType.equals(vo.getTrnstype())) {
						isLeave = false;
					}
				}
			}
			return isLeave;
		}
	}

	@SuppressWarnings("unchecked")
	private int calculateLeaveDaysOnPsnJob(PsnJobVO psnjob, boolean isCalculateLeaveDays) throws DAOException {
		// 留停結束時，計算留停天數，存儲位置：人員工作記錄.留停天數
		if (psnjob.getBegindate() == null || psnjob.getEnddate() == null || !isCalculateLeaveDays) {
			psnjob.setAttributeValue("leavedays", null);
		} else {
			psnjob.setAttributeValue("leavedays",
					UFLiteralDate.getDaysBetween(psnjob.getBegindate(), psnjob.getEnddate()) + 1);
		}
		this.getBaseDao().updateVO(psnjob);

		// 計算纍計留停天數，存儲位置：組織關係.纍計留停天數
		Collection<PsnJobVO> jobs = this.getBaseDao().retrieveByClause(
				PsnJobVO.class,
				"pk_psndoc='" + psnjob.getPk_psndoc() + "' and pk_psnorg='" + psnjob.getPk_psnorg()
						+ "' and isnull(dr,0)=0");
		int days = 0;
		if (jobs != null && jobs.size() > 0) {
			for (PsnJobVO job : jobs) {
				if (job.getAttributeValue("leavedays") != null && !job.getAttributeValue("leavedays").equals("~")) {
					days += (Integer) job.getAttributeValue("leavedays");
				}
			}
		}
		return days;
	}

	private void calculateWorkAgeStartDateOnPsnOrg(PsnJobVO psnjob, int days) throws DAOException {
		// 計算年資起算日
		// 組織關係.年資起算日=(在職員工|留停天數=null).進入集團日期
		// 組織關係.年資起算日=(在職員工|留停天數<>null).進入集團日期+纍計留停天數-年資保留天數
		PsnOrgVO psnorgVO = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, psnjob.getPk_psnorg());
//		int remaindays = (int) (psnorgVO.getAttributeValue("workageremaindays") == null ? 0 : psnorgVO
//				.getAttributeValue("workageremaindays"));
		psnorgVO.setAttributeValue("totalleavedays", days);
//		psnorgVO.setAttributeValue("workagestartdate",
//				psnorgVO.getBegindate().getDateAfter(days).getDateBefore(remaindays));
		//如果TWHR13为Y，TWHR10为Y，则开始日期+累计留停天数-年资保留天数=年资起算日
		// 承认年资是否影响年资起算日TWHR13 , by he
		try {
			UFBoolean refEnableWorkAge = SysInitQuery.getParaBoolean(psnorgVO.getPk_hrorg(), "TWHR13");
			if(null != refEnableWorkAge && refEnableWorkAge.booleanValue()){
				if(null == psnorgVO.getAttributeValue("workageremaindays")){
					psnorgVO.setAttributeValue("workagestartdate",psnorgVO.getBegindate().getDateAfter(days).getDateBefore(0));
				}else{
					psnorgVO.setAttributeValue("workagestartdate",psnorgVO.getBegindate().getDateAfter(days).getDateBefore((int)psnorgVO.getAttributeValue("workageremaindays")));
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		this.getBaseDao().updateVO(psnorgVO);
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
