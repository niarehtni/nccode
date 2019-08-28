package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.Collection;

import nc.bs.dao.BaseDAO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang3.StringUtils;

public class PsndocDismissedValidator {
	@SuppressWarnings("unchecked")
	public void validate(String pk_psndoc, UFLiteralDate checkDate) throws BusinessException {
		if (StringUtils.isEmpty(pk_psndoc)) {
			throw new BusinessException("離職檢查未指定員工");
		}

		if (checkDate == null) {
			throw new BusinessException("離職檢查未指定檢查日期。");
		}

		BaseDAO basedao = new BaseDAO();
		Collection<PsnJobVO> psnjobs = basedao
				.retrieveByClause(
						PsnJobVO.class,
						"pk_psndoc = '"
								+ pk_psndoc
								+ "' and pk_psnorg in (select pk_psnorg from hi_psnorg where pk_psndoc = hi_psnjob.pk_psndoc and lastflag='Y')");

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
}
