package nc.ui.overtime.otleavebalance.model;

import nc.ui.pubapp.uif2app.model.BillManageModel;

public class OTLeaveBalanceModel extends BillManageModel {
    private BillManageModel otListModel;
    private BillManageModel lvListModel;

    @Override
    public void initModel(Object data) {
	super.initModel(data);

	if (data == null) {
	    getOtListModel().initModel(null);
	    getLvListModel().initModel(null);
	}
    }

    @Override
    public void setSelectedRow(int selectedRow) {
	super.setSelectedRow(selectedRow);
    }

    public BillManageModel getOtListModel() {
	return otListModel;
    }

    public void setOtListModel(BillManageModel otListModel) {
	this.otListModel = otListModel;
    }

    public BillManageModel getLvListModel() {
	return lvListModel;
    }

    public void setLvListModel(BillManageModel lvListModel) {
	this.lvListModel = lvListModel;
    }
}
