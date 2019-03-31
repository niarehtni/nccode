package nc.ui.wa.taxspecial_statistics.model;

import nc.bs.logging.Logger;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationBar;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.wa.pub.OrderUtil;
import nc.ui.wa.pub.WADelegator;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.payfile.PayfileConstant;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * 税改专项附加扣除计算ModelDataManager
 * 
 * @author: xuhw
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxSpecialStatisticsModelDataManager extends DefaultAppModelDataManager implements IAppModelDataManagerEx,
		IPaginationModelListener, IQueryInfo {
	private String lastWhereSql = "";
	private PaginationModel paginationModel;
	private BillManagePaginationDelegator paginationDelegator;
	int queryResultCount = 0;
	private PaginationBar paginationBar = null;

	public String getLastWhereSql() {
		return lastWhereSql;
	}

	public void setLastWhereSql(String lastWhereSql) {
		this.lastWhereSql = lastWhereSql;
	}

	// 对于一些业务操作后通常需要刷新界面，事实上并不需要再重新查询所有pks，例如批量修改，因此可以用pks来记录最近一次查询pks的结果
	private String[] pks;

	public String[] getPks() {
		return pks;
	}

	public void setPks(String[] pks) {
		this.pks = pks;
	}

	public PaginationBar getPaginationBar() {
		return paginationBar;
	}

	public void setPaginationBar(PaginationBar paginationBar) {
		this.paginationBar = paginationBar;
	}

	public int getQueryResultCount() {
		return queryResultCount;
	}

	public void setQueryResultCount(int queryResultCount) {
		this.queryResultCount = queryResultCount;
	}

	public PaginationModel getPaginationModel() {
		return paginationModel;
	}

	public BillManagePaginationDelegator getPaginationDelegator() {
		if (paginationDelegator == null) {
			paginationDelegator = new BillManagePaginationDelegator((BillManageModel) getModel(), getPaginationModel());
		}
		return paginationDelegator;
	}

	public void setPaginationDelegator(BillManagePaginationDelegator paginationDelegator) {
		this.paginationDelegator = paginationDelegator;
	}

	@Override
	public void initModel() {
		getModel().initModel(null);
		refreshPageSize();
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		this.lastWhereSql = sqlWhere;
		getModel().fireEvent(new AppEvent(PayfileConstant.EVENT_RULE_HINT, this, null));
		refresh();
	}

	@Override
	public void refresh() {
		try {
			WaLoginContext context = (WaLoginContext) getContext();
			if (!context.isContextNotNull() || context.getWaState() == null) {
				return;
			}
			refreshPageSize();
			// 当薪资发放状态发生改变时，刷新时重置状态
			WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(context.getWaLoginVO());
			waLoginVO.setBatch(context.getWaLoginVO().getBatch());
			context.setWaLoginVO(waLoginVO);
			// 获得order by 的sql
			// 已经选了薪资类别才去刷新
			if (!StringUtil.isEmptyWithTrim(context.getPk_wa_class())) {
				String[] pk_vos = null;
				AggPayDataVO datavo = WADelegator.getPaydataQuery().queryAggPayDataVOByCondition(context, lastWhereSql, null);
				try {
					pk_vos = datavo.getDataPKs();
					this.setPks(pk_vos);
					getPaginationModel().setObjectPks(pk_vos);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}

				queryResultCount = pk_vos.length;
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(getContext().getEntranceUI(), null, e.getMessage());
		}
	}

	@Override
	public void onDataReady() {
		getPaginationDelegator().onDataReady();
	}

	@Override
	public void onStructChanged() {

	}

	public void setPaginationModel(PaginationModel paginationModel) {
		this.paginationModel = paginationModel;

		paginationModel.addPaginationModelListener(this);
		paginationModel.setPageSize(HRWACommonConstants.PAGE_SIZE);
		paginationModel.setMaxPageSize(HRWACommonConstants.MAX_ROW_PER_PAGE);
		paginationModel.init();
	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {

	}

	public void refreshPageSize() {
		try {
			Integer pageSize = HRWACommonConstants.PAGE_SIZE;
			if (getContext().getPk_org() != null) {
				pageSize = SysinitAccessor.getInstance().getParaInt(getContext().getPk_org(),
						ParaConstant.DEFAULTPAGENUM);
			}

			if (pageSize == null) {
				pageSize = HRWACommonConstants.PAGE_SIZE;
			}
			paginationModel.setPageSize(pageSize);
			paginationModel.init();
			paginationBar.onStructChanged();
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
	}

	@Override
	public int getQueryDataCount() {
		return queryResultCount;
	}
}
