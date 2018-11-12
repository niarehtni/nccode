package nc.ui.ta.teamcalendar.view.batchchangedaytype;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.teamcalendar.validator.ConfirmTeamStepValidatorForBatchChangeDayType;

/**
 * 将条件展开为具体的班组后供用户确认的step
 *
 */
public class ConfirmTeamStepForBatchChangeDayType extends WizardStep {

	private ConfirmTeamPanelForBatchChangeDayType teamPanel;

	public void init(){
		setComp(getTeamPanelForBatchChangeDayType());
		getListeners().add(new ConfirmTeamStepListenerForBatchChangeDayType());
		getValidators().add(new ConfirmTeamStepValidatorForBatchChangeDayType());
//		setTitle(ResHelper.getString("6017teamcalendar","06017teamcalendar0045")/*@res "确认班组"*/);
		setTitle(ResHelper.getString("common","UC001-0000085")/*@res "批改"*/);
		setDescription(ResHelper.getString("6017teamcalendar","06017teamcalendar0045")/*@res "确认班组"*/);
	}

	public ConfirmTeamPanelForBatchChangeDayType getTeamPanelForBatchChangeDayType() {
		if(teamPanel==null){
			teamPanel = new ConfirmTeamPanelForBatchChangeDayType();
			teamPanel.init();
		}
		return teamPanel;
	}
}