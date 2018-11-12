package nc.ui.twhr.groupinsurance.view;

import java.awt.Container;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable;
import nc.ui.uif2.AppEvent;
import nc.vo.hr.func.HrFormula;

public class GroupInsuranceBatchBillTable extends ShowUpableBatchBillTable {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 7185543755358186668L;
	private GroupInsuranceFormularRefPane refPaneGroupIns;

	public void handleEvent(AppEvent event) {
		super.handleEvent(event);

		if ("Model_Initialized".equalsIgnoreCase(event.getType())) {
			BillItem item = getBillCardPanel().getBodyItem("cformular");
			item.setComponent(getFormulaRefPane(this));
		}
	}

	protected GroupInsuranceFormularRefPane getFormulaRefPane(
			Container container) {
		try {
			if (refPaneGroupIns == null) {
				refPaneGroupIns = new GroupInsuranceFormularRefPane(container,
						getModel());
				HrFormula formula = new HrFormula();
				refPaneGroupIns.setFormula(formula);
				refPaneGroupIns.setAutoCheck(false);
			} else {
				refPaneGroupIns.setEditorDialog();
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(),
					null,
					ResHelper.getString("60130payrule", "060130payrule0013"));
			return null;
		}
		refPaneGroupIns.getUITextField().setEditable(false);
		return refPaneGroupIns;
	}
}
