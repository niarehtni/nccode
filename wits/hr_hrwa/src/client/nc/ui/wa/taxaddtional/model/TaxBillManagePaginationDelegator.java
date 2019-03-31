package nc.ui.wa.taxaddtional.model;

import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.BillManageModel;

public class TaxBillManagePaginationDelegator extends BillManagePaginationDelegator{
public TaxBillManagePaginationDelegator(BillManageModel model, PaginationModel paginationModel) {
		// TODO Auto-generated constructor stub
	super(model, paginationModel);
	}

@SuppressWarnings("restriction")
public void onDataReady() {
		Object[] objs = getPaginationModel().getCurrentDatas();
		getBillModel().initModel(objs, getPaginationModel().getCurrentDataDescriptor());
	}
	
}
