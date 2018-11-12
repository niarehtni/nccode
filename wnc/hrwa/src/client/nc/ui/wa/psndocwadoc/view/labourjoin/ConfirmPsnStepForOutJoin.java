package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.wa.psndocwadoc.validator.circularlyarrange.ConfirmPsnStepValidatorForOutJoin;

public class ConfirmPsnStepForOutJoin extends WizardStep {
	
	ConfirmPsnPanelForOutJoin confirmPsnPanel=null;

	public ConfirmPsnStepForOutJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getConfirmPsnPanelForOutJoin());
		getListeners().add(new ConfirmPsnStepListenerForOutJoin());
		getValidators().add(new ConfirmPsnStepValidatorForOutJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "ȷ��������Ա"*/);
		setTitle("�ű������˱�"/*@res "����"*/);
		setDescription(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "ȷ��������Ա"*/);
	}

	public ConfirmPsnPanelForOutJoin getConfirmPsnPanelForOutJoin() {
		if(confirmPsnPanel==null){
			confirmPsnPanel = new ConfirmPsnPanelForOutJoin();
			confirmPsnPanel.init();
		}
		return confirmPsnPanel;
	}

}
