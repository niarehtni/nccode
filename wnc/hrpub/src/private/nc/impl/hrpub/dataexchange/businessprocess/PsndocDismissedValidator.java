package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnCalendarVO;

import org.apache.commons.lang3.StringUtils;

public class PsndocDismissedValidator {
	private UFDateTime checkTime;
	private BaseDAO baseDAO;

	public PsndocDismissedValidator() {
		checkTime = null;
	}

	public PsndocDismissedValidator(UFDateTime checktime) {
		checkTime = checktime;
	}

	@SuppressWarnings("unchecked")
	public void validate(String pk_psndoc, UFLiteralDate checkDate) throws BusinessException {
		if (StringUtils.isEmpty(pk_psndoc)) {
			throw new BusinessException("離職檢查未指定員工");
		}

		if (this.getCheckTime() != null) {
			checkDate = getShiftRegDateByDateTime(pk_psndoc, this.getCheckTime(), checkDate);
		}

		if (checkDate == null) {
			throw new BusinessException("離職檢查未指定檢查日期。");
		}

		Collection<PsnJobVO> psnjobs = getBaseDAO()
				.retrieveByClause(
						PsnJobVO.class,
						"pk_psndoc = '"
								+ pk_psndoc
								+ "' and pk_psnorg in (select pk_psnorg from hi_psnorg where pk_psndoc = hi_psnjob.pk_psndoc and hi_psnjob.ismainjob='Y' and lastflag='Y')");

		if (psnjobs != null && psnjobs.size() > 0) {
			for (PsnJobVO vo : psnjobs) {
				UFLiteralDate beginDate = vo.getBegindate();
				UFLiteralDate endDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo.getEnddate();

				if ((checkDate.isSameDate(beginDate) || checkDate.after(beginDate))
						&& (checkDate.isSameDate(endDate) || checkDate.before(endDate))) {
					if (vo.getTrnsevent() == 4) {
						throw new BusinessException("該員工於 [" + checkDate.toString() + "] 已處於離職狀態。");
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public UFLiteralDate getShiftRegDateByDateTime(String pk_psndoc, UFDateTime checkTime,
			UFLiteralDate originalCheckDate) throws BusinessException {
		UFLiteralDate rtnDate = originalCheckDate;
		Collection<PsnCalendarVO> psncals = getBaseDAO().retrieveByClause(
				PsnCalendarVO.class,
				"pk_psndoc='" + pk_psndoc + "' and calendar between '"
						+ checkTime.getDate().getDateBefore(3).toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE) + "' and '"
						+ checkTime.getDate().getDateAfter(3).toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE)
						+ "' and calendar >= (select begindate from hi_psnorg where pk_psndoc='" + pk_psndoc
						+ "' and '" + checkTime.getDate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE)
						+ "' between begindate and isnull(enddate, '9999-12-31'))");
		if (psncals != null && psncals.size() > 0) {
			for (PsnCalendarVO psncal : psncals) {
				if (psncal.getPk_shift() != null) {
					ShiftVO shiftvo = (ShiftVO) getBaseDAO().retrieveByPK(ShiftVO.class, psncal.getPk_shift());
					if (shiftvo != null) {
						// mod start tank 2019年8月21日17:04:24 前一日,後一日修復
						UFDateTime startDT = new UFDateTime(psncal.getCalendar()
								.getDateAfter(shiftvo.getTimebeginday()).toString()
								+ " " + shiftvo.getTimebegintime());

						UFDateTime endDT = new UFDateTime(psncal.getCalendar().getDateAfter(shiftvo.getTimeendday())
								.toString()
								+ " " + shiftvo.getTimeendtime());
						// end mod
						if (checkTime.before(endDT) && checkTime.after(startDT)) {
							rtnDate = psncal.getCalendar();
							break;
						}
					}
				}
			}
		}
		return rtnDate;
	}

	public UFDateTime getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(UFDateTime checkTime) {
		this.checkTime = checkTime;
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}
}
