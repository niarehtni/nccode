package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;
import nc.ui.uif2.UIFCheckBoxAction;
import nc.ui.uif2.UIState;

import org.apache.commons.lang.StringUtils;

public class ShowLastMonthAction extends UIFCheckBoxAction {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 6200839573555733068L;

	private NhicalcAppModel model = null;
	private BillListView editor = null;

	public NhicalcAppModel getModel() {
		return model;
	}

	public void setModel(NhicalcAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillListView getEditor() {
		return editor;
	}

	public void setEditor(BillListView editor) {
		this.editor = editor;
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	private NhiOrgHeadPanel orgpanel = null;

	public ShowLastMonthAction() {
		setBtnName("Î[≤ÿ…œ‘¬ŸY¡œ");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
				&& !StringUtils.isEmpty(this.getOrgpanel().getWaPeriodRefPane().getRefModel().getRefNameValue())) {
			if (isSelected()) {
				BillListData billdata = this.getEditor().getBillListPanel().getBillListData();
				BillItem[] items = billdata.getHeadItems();
				for (BillItem item : items) {
					if (item.getKey().toUpperCase().contains("LASTMONTH")) {
						item.setShow(false);
					}
				}
				// this.getEditor().getBillListPanel().getBillListData().setHeadItems(items);;
			} else {
				BillListData billdata = this.getEditor().getBillListPanel().getBillListData();
				BillItem[] items = billdata.getHeadItems();
				for (BillItem item : items) {
					if (item.getKey().toUpperCase().contains("LASTMONTH")) {
						item.setShow(true);
					}
				}
				// this.getEditor().getBillListPanel().setBodyModelData(billdata);
			}

			this.getModel().setUiState(UIState.NOT_EDIT);
		}
	}

	public boolean isEnabled() {
		return !StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
				&& !StringUtils.isEmpty(this.getOrgpanel().getWaPeriodRefPane().getRefModel().getRefNameValue());
	}
}
