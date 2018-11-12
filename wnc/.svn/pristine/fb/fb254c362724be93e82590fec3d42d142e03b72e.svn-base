package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillItem;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.UIFCheckBoxAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;

import org.apache.commons.lang.StringUtils;

public class ShowLastMonthAction extends UIFCheckBoxAction {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 6200839573555733068L;

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	private BatchBillTableModel model = null;

	private BatchBillTable editor = null;

	private NhiOrgHeadPanel orgpanel = null;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
				&& !StringUtils.isEmpty(this.getOrgpanel().getWaPeriodRefPane()
						.getRefModel().getRefNameValue())) {
			if (isSelected()) {
				BillData billdata = this.getEditor().getBillCardPanel()
						.getBillData();
				BillItem[] items = billdata.getBodyItems();
				for (BillItem item : items) {
					if (item.getKey().toUpperCase().contains("LASTMONTH")) {
						item.setShow(false);
					}
				}
				this.getEditor().getBillCardPanel().setBillData(billdata);
			} else {
				BillData billdata = this.getEditor().getBillCardPanel()
						.getBillData();
				BillItem[] items = billdata.getBodyItems();
				for (BillItem item : items) {
					if (item.getKey().toUpperCase().contains("LASTMONTH")) {
						item.setShow(true);
					}
				}
				this.getEditor().getBillCardPanel().setBillData(billdata);
			}

			this.getModel().setUiState(UIState.NOT_EDIT);
		}
	}

	public boolean isEnabled() {
		return !StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
				&& !StringUtils.isEmpty(this.getOrgpanel().getWaPeriodRefPane()
						.getRefModel().getRefNameValue());
	}
}
