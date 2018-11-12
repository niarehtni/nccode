package nc.ui.ta.teamcalendar.view.batchchangedaytype;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.pub.wizard.validator.CompValidator;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.ui.ta.teamcalendar.view.batchchange.SelectTeamPanelForBatchChange;

public class SelectTeamStepForBatchChangeDayType extends WizardStep {

	private TeamCalendarAppModel appModel;
	private SelectTeamPanelForBatchChange teamPanel;

	public void init(){
		setComp(getTeamPanelForBatchChangeDayType());
		getValidators().add(new CompValidator());
//		setTitle(ResHelper.getString("6017teamcalendar","06017teamcalendar0041")/*@res "ѡ����鷶Χ�����ڷ�Χ"*/);
		setTitle(ResHelper.getString("common","UC001-0000085")/*@res "����"*/);
		setDescription(ResHelper.getString("6017teamcalendar","06017teamcalendar0041")/*@res "ѡ����鷶Χ�����ڷ�Χ"*/);
	}

	public SelectTeamPanelForBatchChange getTeamPanelForBatchChangeDayType() {
		if(teamPanel==null){
			teamPanel = new SelectTeamPanelForBatchChange();
			teamPanel.setModel(getAppModel());
			teamPanel.init();
		}
		return teamPanel;
	}

	public TeamCalendarAppModel getAppModel() {
		return appModel;
	}

	public void setAppModel(TeamCalendarAppModel appModel) {
		this.appModel = appModel;
	}


}