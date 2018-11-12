package nc.ui.twhr.groupinsurance.view;

import java.awt.Container;

import nc.ui.hr.formula.HRFormulaEditorDialog;
import nc.ui.hr.formula.HRFormulaRefPane;
import nc.ui.hr.formula.itf.IVariableFactory;
import nc.ui.uif2.model.AbstractUIAppModel;

public class GroupInsuranceFormularRefPane extends HRFormulaRefPane {

	public GroupInsuranceFormularRefPane(Container parent,
			AbstractUIAppModel model) {
		super(parent, model);
	}

	private static final long serialVersionUID = 1L;

	protected String getConfigFileName() {
		return "/nc/ui/twhr/groupinsurance/groupins_formulaedit.xml";
	}

	protected IVariableFactory getWaVaribleFactory() {
		GroupInsuranceFormularFactory f = new GroupInsuranceFormularFactory();
		f.setModel(getModel());
		return f;
	}

	public void setEditorDialog() {
		formulaDialog = new HRFormulaEditorDialog(getParent(), getModel(),
				getConfigFileName());
		setParas(formulaDialog);
	}
}
