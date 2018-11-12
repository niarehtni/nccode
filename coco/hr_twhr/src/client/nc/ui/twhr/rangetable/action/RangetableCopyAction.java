package nc.ui.twhr.rangetable.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.actions.CopyAction;

public class RangetableCopyAction extends CopyAction {
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);

		this.getModel().setAppUiState(AppUiState.REAL_COPY_ADD);
	}
}
