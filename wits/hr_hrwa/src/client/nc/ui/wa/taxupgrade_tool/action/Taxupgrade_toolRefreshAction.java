package nc.ui.wa.taxupgrade_tool.action;

import java.awt.event.ActionEvent;

import nc.ui.uif2.AppEvent;
import nc.ui.uif2.actions.RefreshAction;
import nc.ui.uif2.model.RowOperationInfo;
import nc.ui.wa.taxupgrade_tool.view.Taxupgrade_toolForm;

/**
 * Ë¢ÐÂAction
 * @author xuhw
 */
public class Taxupgrade_toolRefreshAction extends RefreshAction {
	private Taxupgrade_toolForm editor;

	public Taxupgrade_toolForm getEditor() {
		return editor;
	}

	public void setEditor(Taxupgrade_toolForm editor) {
		this.editor = editor;
	}
	private static final long serialVersionUID = 6499266015495519599L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		editor.initUI();
	}

}
