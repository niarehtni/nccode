package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psndocwadoc.validator.circularlyarrange.ConfirmPsnStepValidatorForQuitJoin;

public class ConfirmPsnStepForQuitJoin extends WizardStep {
	
	ConfirmPsnPanelForQuitJoin confirmPsnPanel=null;

	public ConfirmPsnStepForQuitJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getConfirmPsnPanelForQuitJoin());
		getListeners().add(new ConfirmPsnStepListenerForQuitJoin());
		getValidators().add(new ConfirmPsnStepValidatorForQuitJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "确认批改人员"*/);
		setTitle("劳健保批量退保"/*@res "批改"*/);
		setDescription(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "确认批改人员"*/);
	}

	public ConfirmPsnPanelForQuitJoin getConfirmPsnPanelForQuitJoin() {
		if(confirmPsnPanel==null){
			confirmPsnPanel = new ConfirmPsnPanelForQuitJoin();
			confirmPsnPanel.init();
		}
		return confirmPsnPanel;
	}

}
