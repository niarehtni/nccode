package nc.impl.ta.leave;

import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;

public class EffectiveDateSynPlugin implements IBackgroundWorkPlugin {
	private BaseDAO baseDao = null;

	@Override
	public PreAlertObject executeTask(BgWorkingContext bgwc) throws BusinessException {
		String[] pk_orgs = getPkOrgs(bgwc);
		String[] pk_psndocs = null;

		for (String pk_org : pk_orgs) {
			if (pk_psndocs == null) {
				pk_psndocs = getPsndocsByOrg(pk_org);
			}

			if (pk_psndocs != null && pk_psndocs.length > 0) {
				for (String pk_psndoc : pk_psndocs) {
					synEffectiveDate(pk_psndoc);
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void synEffectiveDate(String pk_psndoc) throws BusinessException {
		Collection<LeaveRegVO> leaveRegs = this.getBaseDao().retrieveByClause(LeaveRegVO.class,
				"pk_psndoc='" + pk_psndoc + "' and effectivedate is null");

		if (leaveRegs != null && leaveRegs.size() > 0) {
			for (LeaveRegVO vo : leaveRegs) {
				UFLiteralDate realDate = BillProcessHelper.getShiftRegDateByLeave(vo);

				try {
					this.getBaseDao().executeUpdate(
							"update tbm_leavereg set effectivedate='" + realDate.toString() + "' where pk_leavereg = '"
									+ vo.getPk_leavereg() + "'");
				} catch (BusinessException e) {
					nc.bs.logging.Logger.error(e.getMessage());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String[] getPkOrgs(BgWorkingContext bgwc) throws BusinessException {
		String[] ret = bgwc.getPk_orgs();

		if (ret == null || ret.length == 0) {
			List<String> orgs = (List<String>) this.getBaseDao().executeQuery(
					"select pk_hrorg from org_hrorg where dr=0 and enablestate=2", new ColumnListProcessor());

			if (orgs != null && orgs.size() > 0) {
				ret = orgs.toArray(new String[0]);
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private String[] getPsndocsByOrg(String pk_org) throws BusinessException {
		List<String> psndocs = (List<String>) this.getBaseDao().executeQuery(
				"select distinct pk_psndoc from hi_psnorg where pk_org = '" + pk_org + "'", new ColumnListProcessor());

		if (psndocs == null) {
			return null;
		}
		return psndocs.toArray(new String[0]);
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

}
