package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psndocwadoc.validator.circularlyarrange.ConfirmPsnStepValidatorForAddJoin;

public class ConfirmPsnStepForAddJoin extends WizardStep {
	
	ConfirmPsnPanelForAddJoin confirmPsnPanel=null;

	public ConfirmPsnStepForAddJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getConfirmPsnPanelForAddJoin());
		getListeners().add(new ConfirmPsnStepListenerForAddJoin());
		getValidators().add(new ConfirmPsnStepValidatorForAddJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "确认批改人员"*/);
		setTitle("团保批量加保"/*@res "批改"*/);
		setDescription(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "确认批改人员"*/);
	}

	public ConfirmPsnPanelForAddJoin getConfirmPsnPanelForAddJoin() {
		if(confirmPsnPanel==null){
			confirmPsnPanel = new ConfirmPsnPanelForAddJoin();
			confirmPsnPanel.init();
		}
		return confirmPsnPanel;
	}

}
