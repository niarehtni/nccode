package nc.ui.wa.paydata.action;

import java.awt.Color;
import java.awt.event.ActionEvent;

import nc.hr.utils.ResHelper;
import nc.ui.dcm.chnlrplstrct.maintain.action.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;

import org.apache.commons.lang.StringUtils;

public class SearchAction extends NCAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -8303922233620365248L;
	private BillForm formEditor;

	public SearchAction() {
		setBtnName(ResHelper.getString("60050orgchart", "06005chart0159"));
		putValue("Code", "SearchAction");
		putValue("ShortDescription", ResHelper.getString("60050orgchart", "06005chart0159"));
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		String keyWord = (String) MessageDialog.showInputDlg(this.getFormEditor(), "查找薪資項目", "請輸入要查找的薪資項目名稱", "");

		if (!StringUtils.isEmpty(keyWord)) {
			int p = 0;
			for (BillItem item : getFormEditor().getBillCardPanel().getHeadItems()) {
				if (item.getComponent() instanceof UIRefPane) {
					UIRefPane pane = (UIRefPane) item.getComponent();
					if (item.getName() != null && item.getName().contains(keyWord)) {
						if (p == 0) {
							p = item.getComponent().getLocation().y;
						}

						pane.getComponent(0).setForeground(Color.RED);
					} else {
						pane.getComponent(0).setForeground(Color.BLACK);
					}
				}
			}

			if (p != 0) {
				getFormEditor().getBillCardPanel().getSelectedScrollPane(0).getVerticalScrollBar()
						.setValueIsAdjusting(true);
				getFormEditor().getBillCardPanel().getSelectedScrollPane(0).getVerticalScrollBar().setValue(p);
			}
		}
	}

	public BillForm getFormEditor() {
		return formEditor;
	}

	public void setFormEditor(BillForm formEditor) {
		this.formEditor = formEditor;
	}
}
