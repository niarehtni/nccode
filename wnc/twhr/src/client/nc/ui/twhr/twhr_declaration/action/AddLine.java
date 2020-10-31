package nc.ui.twhr.twhr_declaration.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.card.BodyRowEditType;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterRowEditEvent;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;

public class AddLine extends HrAction {
	/**
	 * erial no
	 */
	private static final long serialVersionUID = 8820532328109163234L;
	private ShowUpableBillForm billForm;

	public AddLine() {
		putValue("Code", "addLineAction");
		setBtnName("增行");

		putValue("ShortDescription", "增行" + "(Ctrl+I)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK));
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		getBillForm().getBillCardPanel().addLine();
		int index = getBillForm().getBillCardPanel().getBillModel().getRowCount() - 1;
		execLoadFormula(index);

		int[] rows = { getBillForm().getBillCardPanel().getRowCount() - 1 };
		getModel().fireEvent(
				new CardBodyAfterRowEditEvent(getBillForm().getBillCardPanel(), BodyRowEditType.ADDLINE, rows));
	}

	protected void execLoadFormula(int index) {
		BillItem[] billItems = getBillForm().getBillCardPanel().getBodyItems();
		String[] formulas = getExecLoadFormula(billItems);
		if (formulas != null) {
			getBillForm().getBillCardPanel().getBillModel().execFormula(index, formulas);
		}
	}

	protected String[] getExecLoadFormula(BillItem[] items) {
		List<String> vColNames = new ArrayList();
		List<String> vFormulas = new ArrayList();
		for (BillItem item : items) {
			String[] formulas = item.getLoadFormula();
			if (formulas != null) {
				for (String formula2 : formulas) {
					vColNames.add(item.getKey());
					String formula = formula2;
					if (formula.indexOf("->") < 0) {
						formula = item.getKey() + "->" + formula;
					}
					vFormulas.add(formula);
				}
			}
		}

		if (vFormulas.size() == 0) {
			return null;
		}

		String[] formulas = new String[vFormulas.size()];

		for (int i = 0; i < vFormulas.size(); i++) {
			formulas[i] = ((String) vFormulas.get(i));
		}
		return formulas;
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

}
