package nc.ui.twhr.twhr_declaration.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.ml.NCLangRes4VoTransl;

public class CancelAction extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 3710735463539771560L;
	private ShowUpableBillForm billForm;
	private BillListView billListView;

	public CancelAction() {
		putValue("Code", "cancelAction");
		setBtnName("取消");

		putValue("ShortDescription", "取消" + "(Ctrl+Z)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		for (int i = 0; i < getBillForm().getBillCardPanel().getBodyTabbedPane().getTabCount(); i++) {
			if (i < 4) {
				getBillForm().getBillCardPanel().getBodyTabbedPane().setEnabledAt(i, true);
				getBillForm().getBillCardPanel().getBodyTabbedPane().setTabVisible(i, true);
			}
		}

		for (int i = 0; i < getBillListView().getBillListPanel().getBodyTabbedPane().getTabCount(); i++) {
			if (i < 4) {
				getBillListView().getBillListPanel().getBodyTabbedPane().setEnabledAt(i, true);
				getBillListView().getBillListPanel().getBodyTabbedPane().setTabVisible(i, true);
			}
		}

		this.getBillForm().getBillOrgPanel().setVisible(true);
		this.getBillForm().getBillCardPanel().getTailScrollPane().setVisible(false);
		this.getBillForm().getBillCardPanel().getBodyPanel().getTableModel().clearBodyData();

		getModel().setUiState(UIState.NOT_EDIT);
		getModel().setOtherUiState(UIState.NOT_EDIT);

		ShowStatusBarMsgUtil.showStatusBarMsg(NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0125"),
				getModel().getContext());
	}

	public BillListView getBillListView() {
		return billListView;
	}

	public void setBillListView(BillListView billListView) {
		this.billListView = billListView;
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

}
