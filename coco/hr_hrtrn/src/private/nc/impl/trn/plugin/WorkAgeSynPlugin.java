package nc.impl.trn.plugin;

import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.impl.trn.listener.PsnjobSynWorkAgeBusinessListener;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;

public class WorkAgeSynPlugin implements IBackgroundWorkPlugin {
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
					synWorkAge(pk_psndoc);
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void synWorkAge(String pk_psndoc) throws BusinessException {
		Collection<PsnJobVO> psnjobs = this.getBaseDao().retrieveByClause(PsnJobVO.class,
				"pk_psndoc='" + pk_psndoc + "'", "recordnum desc");

		if (psnjobs != null && psnjobs.size() > 0) {
			for (PsnJobVO vo : psnjobs) {
				// 留停異動類型
				String refTransType = SysInitQuery.getParaString(vo.getPk_org(), "TWHR11").toString();
				// 複職異動類型
				String refReturnType = SysInitQuery.getParaString(vo.getPk_org(), "TWHR12").toString();

				if (refTransType == null || refTransType.equals("~")) {
					throw new BusinessException("系統參數 [TWHR11] 未指定用於留停的異動類型。");
				}

				if (refReturnType == null || refReturnType.equals("~")) {
					throw new BusinessException("系統參數 [TWHR12] 未指定用於留停複職的異動類型。");
				}

				new PsnjobSynWorkAgeBusinessListener().calculateWorkAgeDate(vo, refTransType, refReturnType, true);
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
				"select pk_psndoc from bd_psndoc where pk_org = '" + pk_org + "'", new ColumnListProcessor());

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
