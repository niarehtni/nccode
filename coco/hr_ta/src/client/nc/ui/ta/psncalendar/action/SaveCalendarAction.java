package nc.ui.ta.psncalendar.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.view.Editor;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public class SaveCalendarAction extends HrAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -996093259603621539L;
	
	Editor editor;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		PsnJobCalendarVO[] value = (PsnJobCalendarVO[])editor.getValue();
		PsnCalendarAppModel model = (PsnCalendarAppModel)getModel();
		model.insert(value);
		model.setUiState(UIState.NOT_EDIT);
		putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getSaveSuccessInfo());
	}

	public SaveCalendarAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.SAVE);
	}

	public Editor getEditor() {
		return editor;
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}

}
