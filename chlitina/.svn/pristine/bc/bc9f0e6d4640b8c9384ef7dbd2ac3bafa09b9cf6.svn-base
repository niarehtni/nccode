package nc.ui.overtime.otleavebalance.handler;

import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.IAppModelDataManager;

public class OTLeaveBalanceMediator implements AppEventListener {
    private AbstractAppModel hierachicalModel;
    private IAppModelDataManager dataManager;

    public void handleEvent(AppEvent event) {
	if (event.getSource() == hierachicalModel) {
	    if ("Selection_Changed".equals(event.getType())) {

		((OTLeaveBalanceModeDataManager) getDataManager()).refresh();
	    }
	}
    }

    public AbstractAppModel getHierachicalModel() {
	return hierachicalModel;
    }

    public void setHierachicalModel(AbstractAppModel hierachicalModel) {
	this.hierachicalModel = hierachicalModel;
	hierachicalModel.addAppEventListener(this);
    }

    public IAppModelDataManager getDataManager() {
	return dataManager;
    }

    public void setDataManager(IAppModelDataManager dataManager) {
	this.dataManager = dataManager;
    }
}
