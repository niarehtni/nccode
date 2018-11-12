package nc.ui.twhr.rangetable.action;

import java.awt.event.ActionEvent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction;
import nc.ui.uif2.UIState;

public class RangetableSaveAction extends DifferentVOSaveAction {

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object value = getEditor().getValue();

		//validate(value);

		if (getModel().getUiState() == UIState.ADD) {
			doAddSave(value);
		} else if (getModel().getUiState() == UIState.EDIT) {
			doEditSave(value);
		}

		showSuccessInfo();
	}

}
