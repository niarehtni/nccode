package nc.ui.ta.teamcalendar.view.batchchangedaytype;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.teamcalendar.validator.ShiftSetStepValidatorForBatchChange;
import nc.ui.ta.teamcalendar.validator.ShiftSetStepValidatorForBatchChangeDayType;
import nc.ui.uif2.model.IAppModel;

public class ShiftSetStepForBatchChangeDayType extends WizardStep {

	//将step2注入进来，便于处理      wangywt   20190701
	private WizardStep step;
	
	private ShiftSetPanelForBatchChangeDayType shiftSetPanel = null;

	private IAppModel appModel;
	public void init(){
		setComp(getShiftSetPanelForBatchChangeDayType());
		getListeners().add(new ShiftSetStepListenerForBatchChangeDayType());
		getValidators().add(new ShiftSetStepValidatorForBatchChangeDayType());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "设置修改班次"*/);
		setTitle(ResHelper.getString("common","UC001-0000085")/*@res "批改"*/);
		setDescription(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "设置修改班次"*/);
	}

	public ShiftSetPanelForBatchChangeDayType getShiftSetPanelForBatchChangeDayType() {
		if(shiftSetPanel==null){
			shiftSetPanel = new ShiftSetPanelForBatchChangeDayType();
			shiftSetPanel.setModel(getAppModel());
			shiftSetPanel.setStep(getStep());
			shiftSetPanel.init();
		}
		return shiftSetPanel;
	}
	
	public IAppModel getAppModel() {
		return appModel;
	}

	public void setAppModel(IAppModel appModel) {
		this.appModel = appModel;
	}

	public WizardStep getStep() {
		return step;
	}

	public void setStep(WizardStep step) {
		this.step = step;
	}

	public ShiftSetPanelForBatchChangeDayType getShiftSetPanel() {
		return shiftSetPanel;
	}

	public void setShiftSetPanel(ShiftSetPanelForBatchChangeDayType shiftSetPanel) {
		this.shiftSetPanel = shiftSetPanel;
	}

}