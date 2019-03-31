package nc.ui.qcco.commission.action;

import java.awt.event.ActionEvent;

import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillListView;

public class CheckAllAction extends NCAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -5254619057531596610L;

	private BillListView billListPanel;

	public CheckAllAction() {
		this.setCode("CHECKALLACTION");
		this.setBtnName("ȫѡ");
	}

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {

	}

	public BillListView getBillListPanel() {
		return billListPanel;
	}

	public void setBillListPanel(BillListView billListPanel) {
		this.billListPanel = billListPanel;
		billListPanel.addBodyMouseClicked(this);
	}
}
