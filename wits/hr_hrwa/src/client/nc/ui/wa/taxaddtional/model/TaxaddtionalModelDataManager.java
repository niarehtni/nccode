package nc.ui.wa.taxaddtional.model;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.funcnode.ui.FuncletInitData;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.querytemplate.querytree.QueryTree;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationBar;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.wa.pub.WADelegator;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.querytemplate.QueryResult;
import nc.vo.querytemplate.QueryTemplateUtils;
import nc.vo.querytemplate.queryscheme.QuerySchemeObject;
import nc.vo.querytemplate.queryscheme.QuerySchemeVO;
import nc.vo.querytemplate.queryscheme.SimpleQuerySchemeVO;
import nc.vo.uif2.LoginContext;
import nc.vo.util.SqlWhereUtil;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.payfile.PayfileConstant;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

import org.apache.commons.lang.StringUtils;

/**
 * 附加费用扣除额-DataManager
 * 
 * @author: xuhw
 * @date: 2018-6-25 下午04:10:21
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxaddtionalModelDataManager extends DefaultAppModelDataManager implements IAppModelDataManagerEx,
		IPaginationModelListener, IQueryInfo {
	/** 上一次查询的条件 */
	private String lastWhereSql;

	private TaxaddtionalAppModelService service;

	private AbstractAppModel model;
	private String strWherePart = null;
	private int queryResultCount = 0;
	private PaginationModel paginationModel;
	private BillManagePaginationDelegator paginationDelegator;
	private PaginationBar paginationBar = null;

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
			paginationDelegator = new TaxBillManagePaginationDelegator((BillManageModel) getModel(), getPaginationModel());
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
			LoginContext context = (LoginContext) getContext();
			refreshPageSize();
			// 当薪资发放状态发生改变时，刷新时重置状态
			String[]  pk_vos = getService().getQueryService().queryDataPksByCondition(context, lastWhereSql, null);
			try {
				this.setPks(pk_vos);
				getPaginationModel().setObjectPks(pk_vos);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}

			queryResultCount = pk_vos.length;
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(getContext().getEntranceUI(), null, e.getMessage());
		}
	}

	public TaxaddtionalAppModelService getService() {
		return service;
	}

	public void setService(TaxaddtionalAppModelService service) {
		this.service = service;
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
