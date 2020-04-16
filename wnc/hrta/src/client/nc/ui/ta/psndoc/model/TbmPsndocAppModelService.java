package nc.ui.ta.psndoc.model;

import nc.itf.ta.TBMPsndocDelegator;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.uif2.LoginContext;

public class TbmPsndocAppModelService implements IAppModelService, IPaginationQueryService {

	@Override
	public void delete(Object object) throws Exception {
		if (object == null)
			return;
		if (object.getClass().isArray()) {
			TBMPsndocVO[] vos = new TBMPsndocVO[((Object[]) object).length];
			for (int i = 0; i < ((Object[]) object).length; i++) {
				vos[i] = (TBMPsndocVO) ((Object[]) object)[i];
			}
			TBMPsndocDelegator.getTBMPsndocManageMaintain().delete(vos);
			return;
		}
		TBMPsndocDelegator.getTBMPsndocManageMaintain().delete((TBMPsndocVO) object);
	}

	@Override
	public Object insert(Object object) throws Exception {
		return null;
	}

	public Object insert(Object object, boolean ret, boolean isCreateTeamMember) throws Exception {
		return TBMPsndocDelegator.getTBMPsndocManageMaintain().insert((TBMPsndocVO) object, ret, isCreateTeamMember);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		TBMPsndocVO[] vos = (TBMPsndocVO[]) object;
		return TBMPsndocDelegator.getTBMPsndocManageMaintain().update(vos, false);
	}

	public Object[] queryObjectByPks(String pks[]) throws BusinessException {
		return TBMPsndocDelegator.getTBMPsndocQueryMaintain().queryPsndocVOByPks(pks);
	}
}
