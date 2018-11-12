package nc.ui.ta.psndocwadoc.validator.circularlyarrange;

import javax.swing.JComponent;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.IWizardStepValidator;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.ta.psndocwadoc.view.labourjoin.ConfirmPsnPanelForOutJoin;
import nc.ui.ta.psndocwadoc.view.labourjoin.ConfirmPsnStepForOutJoin;

import org.apache.commons.lang.ArrayUtils;

public class ConfirmPsnStepValidatorForOutJoin implements IWizardStepValidator {

	@Override
	public void validate(JComponent comp, WizardModel model)
			throws WizardStepValidateException {
		//取出人员主键
		ConfirmPsnStepForOutJoin step2 = (ConfirmPsnStepForOutJoin)model.getSteps().get(1);
		ConfirmPsnPanelForOutJoin panel2 = step2.getConfirmPsnPanelForOutJoin();
		String[] pk_psndocs = panel2.getSelPkPsndocs();
		if(ArrayUtils.isEmpty(pk_psndocs)){
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg("not selected person!", ResHelper.getString("6017psncalendar","06017psncalendar0053")
/*@res "请选择人员!"*/);
			throw e;
		}
	}

}