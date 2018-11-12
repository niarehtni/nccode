package nc.ui.ta.psncalendar.validator.circularlyarrange;


import javax.swing.JComponent;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.IWizardStepValidator;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.ta.psncalendar.view.batchchange.ConfirmPsnPanelForBatchChange;
import nc.ui.ta.psncalendar.view.batchchange.ConfirmPsnStepForBatchChange;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ConfirmPsnPanelForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ConfirmPsnStepForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ShiftSetPanelForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ShiftSetStepForBatchChangeCalendarDay;

import org.apache.commons.lang.ArrayUtils;

public class ConfirmPsnStepValidatorForBatchChangeCalendarDay implements IWizardStepValidator {

	@Override
	public void validate(JComponent comp, WizardModel model)
			throws WizardStepValidateException {
		//取出人员主键
		ConfirmPsnStepForBatchChangeCalendarDay step2 = (ConfirmPsnStepForBatchChangeCalendarDay)model.getSteps().get(1);
		ConfirmPsnPanelForBatchChangeCalendarDay panel2 = step2.getConfirmPsnPanelForBatchChangeCalendarDay();
		String[] pk_psndocs = panel2.getSelPkPsndocs();
		if(ArrayUtils.isEmpty(pk_psndocs)){
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg("not selected person!", ResHelper.getString("6017psncalendar","06017psncalendar0053")
/*@res "请选择人员!"*/);
			throw e;
		}    
		
		
		
		
		
		
		
	}

}