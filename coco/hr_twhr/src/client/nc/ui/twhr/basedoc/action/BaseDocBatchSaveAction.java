package nc.ui.twhr.basedoc.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction;
import nc.ui.uif2.actions.batch.BatchRefreshAction;

public class BaseDocBatchSaveAction extends BatchSaveAction {
    /**
     * serial no
     */
    private static final long serialVersionUID = -3171476575266124572L;
    private BatchRefreshAction refreshAction = null;

    public void doAction(ActionEvent e) throws Exception {
	super.doAction(e);

	getRefreshAction().doAction(e);
    }

    public BatchRefreshAction getRefreshAction() {
	return refreshAction;
    }

    public void setRefreshAction(BatchRefreshAction refreshAction) {
	this.refreshAction = refreshAction;
    }
}
