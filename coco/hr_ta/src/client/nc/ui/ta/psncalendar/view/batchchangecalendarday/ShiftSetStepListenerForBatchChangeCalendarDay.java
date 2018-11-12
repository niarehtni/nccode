package nc.ui.ta.psncalendar.view.batchchangecalendarday;

import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.ta.psncalendar.view.batchchange.SelPsnPanelForBatchChange;
import nc.vo.logging.Debug;

public class ShiftSetStepListenerForBatchChangeCalendarDay implements IWizardStepListener {

	@Override
	public void stepActived(WizardStepEvent event) throws WizardStepException {
		Debug.debug("shift set step will be actived");
		ShiftSetStepForBatchChangeCalendarDay currStep = (ShiftSetStepForBatchChangeCalendarDay)event.getStep();
		HRWizardModel model = (HRWizardModel)currStep.getModel();
		if(!(model.getStepWhenAction() instanceof ConfirmPsnStepForBatchChangeCalendarDay))
			return;
		SelPsnStepForBatchChangeCalendarDay selPsnStep = (SelPsnStepForBatchChangeCalendarDay)model.getSteps().get(0);
		SelPsnPanelForBatchChange selPsnPanel = selPsnStep.getSelPsnPanelForBatchChangeCalendarDay();
		String pk_org = selPsnPanel.getPK_BU();
		//���ð�β��յ�pk_org��ʹ��ֻ���ճ���һ��ѡ���ҵ��Ԫ�İ��
		ShiftSetPanelForBatchChangeCalendarDay panel = currStep.getShiftSetPanelForBatchChangeCalendarDay();
		panel.setPK_BU(pk_org);
	}

	@Override
	public void stepDisactived(WizardStepEvent event)
			throws WizardStepException {
	

	}

}
