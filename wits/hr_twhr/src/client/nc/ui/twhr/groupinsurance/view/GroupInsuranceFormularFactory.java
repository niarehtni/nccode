package nc.ui.twhr.groupinsurance.view;

import java.util.ArrayList;
import java.util.List;

import nc.ui.hr.formula.HRFormulaItem;
import nc.ui.hr.formula.itf.IFormulaTableCreator;
import nc.ui.wa.item.view.HrWaDefaultVariableFactory;
import nc.vo.pub.formulaedit.FormulaItem;

public class GroupInsuranceFormularFactory extends HrWaDefaultVariableFactory {
	private IFormulaTableCreator tableCreator;

	protected List<FormulaItem> createAllFieldItems(String tablename) {
		List<FormulaItem> fieldItems = new ArrayList();

		if (GroupInsuranceFormularTableCreator.GROUPINS_INFOSET_NAME
				.equals(tablename)) {
			HRFormulaItem item = new HRFormulaItem(
					GroupInsuranceFormularTableCreator.GROUPINS_BASECALC_CODE,
					GroupInsuranceFormularTableCreator.GROUPINS_BASECALC_NAME,
					"["
							+ GroupInsuranceFormularTableCreator.GROUPINS_INFOSET_NAME
							+ "."
							+ GroupInsuranceFormularTableCreator.GROUPINS_BASECALC_NAME
							+ "]",
					"["
							+ GroupInsuranceFormularTableCreator.GROUPINS_INFOSET_NAME
							+ "."
							+ GroupInsuranceFormularTableCreator.GROUPINS_BASECALC_NAME
							+ "]");
			fieldItems.add(item);
		}

		return fieldItems;
	}

	public IFormulaTableCreator getTableCreator() {
		if (tableCreator == null) {
			tableCreator = new GroupInsuranceFormularTableCreator();
		}
		return tableCreator;
	}
}
