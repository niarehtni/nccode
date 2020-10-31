package nc.ui.twhr.twhr_declaration.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import nc.ui.pubapp.uif2app.actions.EditAction;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;

import org.apache.commons.lang3.StringUtils;

public class CompanyAdjustAction extends EditAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -934272259675854585L;
	private nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel = null;
	private ShowUpableBillForm billForm;
	private BillListView billListView;

	public CompanyAdjustAction() {
		putValue("Code", "companyAdjAction");
		setBtnName("公司補充保費調整");

		putValue("ShortDescription", "公司補充保費調整" + "(Ctrl+J)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.CTRL_MASK));
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

	public nc.ui.twhr.glb.view.OrgPanel_Org getPrimaryOrgPanel() {
		return primaryOrgPanel;
	}

	public void setPrimaryOrgPanel(nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel) {
		this.primaryOrgPanel = primaryOrgPanel;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		String pk_org = (String) ((String[]) this.getPrimaryOrgPanel().getRefPane().getValueObj())[0];

		if (StringUtils.isEmpty(pk_org)) {
			return;
		}

		for (int i = 0; i < getBillForm().getBillCardPanel().getBodyTabbedPane().getTabCount(); i++) {
			if (i < 4) {
				getBillForm().getBillCardPanel().getBodyTabbedPane().setEnabledAt(i, false);
				getBillForm().getBillCardPanel().getBodyTabbedPane().setTabVisible(i, false);
			}
		}

		for (int i = 0; i < getBillListView().getBillListPanel().getBodyTabbedPane().getTabCount(); i++) {
			if (i < 4) {
				getBillListView().getBillListPanel().getBodyTabbedPane().setEnabledAt(i, false);
				getBillListView().getBillListPanel().getBodyTabbedPane().setTabVisible(i, false);
			}
		}

		getBillForm().getBillCardPanel().getBodyTabbedPane().setSelectedIndex(4);
		getBillListView().getBillListPanel().getBodyTabbedPane().setSelectedIndex(4);

		this.getBillForm().getBillOrgPanel().setVisible(false);
		this.getBillForm().getBillCardPanel().getTailScrollPane().setVisible(false);

		super.doAction(e);

		this.getBillForm().getModel().getContext().setPk_org(pk_org);
	}

	protected boolean isActionEnable() {
		String tableCode = null;
		String pk_org = null;
		try {
			tableCode = getBillListView().getBillListPanel().getBodyTabbedPane().getSelectedTableCode();
			pk_org = getPrimaryOrgPanel().getRefPane().getRefPK();
		} catch (Exception e) {
			pk_org = null;
			tableCode = null;
		}
		if (pk_org != null && tableCode != null && tableCode.equals("CompanyAdjustVO")) {
			return true;
		} else {
			return false;
		}
	}

	public BillListView getBillListView() {
		return billListView;
	}

	public void setBillListView(BillListView billListView) {
		this.billListView = billListView;
	}
}
