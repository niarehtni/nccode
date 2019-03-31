package nc.ui.wa.taxaddtional.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.ITaxaddtionalManageService;
import nc.itf.hr.wa.ITaxaddtionalQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

/**
 * 
 * @author: xuhw
 * @date: 2010-6-25 下午04:07:23
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxaddtionalAppModelService implements IAppModelService, IPaginationQueryService {
	private ITaxaddtionalManageService manageService;
	private ITaxaddtionalQueryService queryService;

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext arg0) throws Exception {
		return null;
	}

	public WASpecialAdditionaDeductionVO[] queryDataByCondition(LoginContext context, String sqlWhere, String orderby)
			throws BusinessException {
		return getQueryService().queryDataByCondition(context, sqlWhere, orderby);

	}

	public ITaxaddtionalQueryService getQueryService() {
		if (queryService == null) {
			queryService = NCLocator.getInstance().lookup(ITaxaddtionalQueryService.class);
		}
		return queryService;
	}

	public ITaxaddtionalManageService getManageService() {
		if (manageService == null) {
			manageService = NCLocator.getInstance().lookup(ITaxaddtionalManageService.class);
		}
		return manageService;
	}

	@Override
	public Object insert(Object object) throws Exception {
		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		return null;
	}

	@Override
	public void delete(Object object) throws Exception {
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return getQueryService().queryDataByPKs(pks);
	}

}
