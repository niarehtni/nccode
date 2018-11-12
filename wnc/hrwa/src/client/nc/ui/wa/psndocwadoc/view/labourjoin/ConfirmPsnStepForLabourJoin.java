package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.wa.psndocwadoc.validator.circularlyarrange.ConfirmPsnStepValidatorForLabourJoin;

public class ConfirmPsnStepForLabourJoin extends WizardStep {
	
	ConfirmPsnPanelForLabourJoin confirmPsnPanel=null;

	public ConfirmPsnStepForLabourJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getConfirmPsnPanelForLabourJoin());
		getListeners().add(new ConfirmPsnStepListenerForLabourJoin());
		getValidators().add(new ConfirmPsnStepValidatorForLabourJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "确认批改人员"*/);
		setTitle("劳健保批量加保"/*@res "批改"*/);
		setDescription(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "确认批改人员"*/);
	}

	public ConfirmPsnPanelForLabourJoin getConfirmPsnPanelForLabourJoin() {
		if(confirmPsnPanel==null){
			confirmPsnPanel = new ConfirmPsnPanelForLabourJoin();
			confirmPsnPanel.init();
		}
		return confirmPsnPanel;
	}

}
