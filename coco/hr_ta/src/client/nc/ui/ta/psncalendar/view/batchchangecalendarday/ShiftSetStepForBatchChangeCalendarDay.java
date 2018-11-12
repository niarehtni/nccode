package nc.ui.ta.psncalendar.view.batchchangecalendarday;


import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psncalendar.validator.circularlyarrange.ShiftSetStepValidatorForBatchChange;
import nc.ui.ta.psncalendar.validator.circularlyarrange.ShiftSetStepValidatorForBatchChangeCalendarDay;
import nc.ui.uif2.model.IAppModel;

/**
 * ����Ҫ���ĵ��������step
 * @author Ares.tank
 * @date 2018-8-15 18:35:47
 *
 */
public class ShiftSetStepForBatchChangeCalendarDay extends WizardStep {

	private ShiftSetPanelForBatchChangeCalendarDay shiftSetPanel = null;

	private IAppModel appModel;

	public ShiftSetStepForBatchChangeCalendarDay() {
	
	}

	public void init(){
		setComp(getShiftSetPanelForBatchChangeCalendarDay());
		getListeners().add(new ShiftSetStepListenerForBatchChangeCalendarDay());
		getValidators().add(new ShiftSetStepValidatorForBatchChangeCalendarDay());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "�����޸İ��"*/);
		setTitle(ResHelper.getString("twhr_psncalendar","psncalendar-0000")/*@res "����������"*/);
		setDescription(ResHelper.getString("twhr_psncalendar","psncalendar-0003")/*@res "�����޸�������"*/);
	}

	public ShiftSetPanelForBatchChangeCalendarDay getShiftSetPanelForBatchChangeCalendarDay() {
		if(shiftSetPanel==null){
			shiftSetPanel = new ShiftSetPanelForBatchChangeCalendarDay();
			shiftSetPanel.setModel(getAppModel());
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

}