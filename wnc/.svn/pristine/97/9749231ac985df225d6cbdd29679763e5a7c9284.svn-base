package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.ui.hr.pf.action.PFCancelAction;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;
import nc.ui.uif2.UIState;

public class CancelAction extends PFCancelAction {
	private static final long serialVersionUID = -5577499794281985886L;

	private NhicalcAppModel model = null;
	
	private ShowUpableBillForm editor = null;
	
	public NhicalcAppModel getModel() {
		return model;
	}

	public void setModel(NhicalcAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
	
	public void doAction(ActionEvent evt) {
		super.doAction(evt);
	}
	
	public ShowUpableBillForm getEditor() {
		return editor;
	}

	public void setEditor(ShowUpableBillForm editor) {
		this.editor = editor;
	}

	 protected boolean isActionEnable()
	  {
	    return ((this.getModel().getUiState() == UIState.ADD) || (this.getModel().getUiState() == UIState.EDIT));
	  }
}
