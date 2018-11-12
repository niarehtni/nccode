package nc.ui.om.hrdept.action;

import nc.ui.om.hrdept.model.OrgTreeDataManager;
import nc.ui.om.pub.model.IDeptAdjFeaturedAppModelDataManagerEx;

public class ShowHRFuturedDataAction extends HrShowDeptAdjAbortAction {
	private OrgTreeDataManager treeDataManager;

	public OrgTreeDataManager getTreeDataManager() {
		return treeDataManager;
	}

	public void setTreeDataManager(OrgTreeDataManager treeDataManager) {
		this.treeDataManager = treeDataManager;
	}

	public ShowHRFuturedDataAction() {
		super();
	}

	@Override
	public void setShowFilterDataFlag(boolean isShow) {
		// ((IDeptAdjFeaturedAppModelDataManagerEx)
		// getDataManager()).setShowHRFuturedFlag(isSelected());
		((IDeptAdjFeaturedAppModelDataManagerEx) getDataManager()).setShowHRFuturedFlag(isSelected());
		((IDeptAdjFeaturedAppModelDataManagerEx) getTreeDataManager()).setShowHRFuturedFlag(isSelected());

	}
}
