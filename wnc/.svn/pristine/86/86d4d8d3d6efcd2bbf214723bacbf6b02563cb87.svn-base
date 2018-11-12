package nc.ui.ta.psncalendar.view.batchchangecalendarday;


import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psncalendar.validator.circularlyarrange.ConfirmPsnStepValidatorForBatchChangeCalendarDay;

/**
 * 将条件展开为具体的人员后供用户确认的step
 * @author Ares.Tank 
 * @date 2018-8-15 15:55:31
 *
 */
public class ConfirmPsnStepForBatchChangeCalendarDay extends WizardStep {

	ConfirmPsnPanelForBatchChangeCalendarDay confirmPsnPanel=null;

	public ConfirmPsnStepForBatchChangeCalendarDay() {
	
	}

	public void init(){
		setComp(getConfirmPsnPanelForBatchChangeCalendarDay());
		getListeners().add(new ConfirmPsnStepListenerForBatchChangeCalendarDay());
		getValidators().add(new ConfirmPsnStepValidatorForBatchChangeCalendarDay());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0103")/*@res "确认批改人员"*/);
		setTitle(ResHelper.getString("twhr_psncalendar","psncalendar-0000")/*@res "批改日历天"*/);
		setDescription(ResHelper.getString("twhr_psncalendar","psncalendar-0002")/*@res "确认批改日历天人员"*/);
	}

	public ConfirmPsnPanelForBatchChangeCalendarDay getConfirmPsnPanelForBatchChangeCalendarDay() {
		if(confirmPsnPanel==null){
			confirmPsnPanel = new ConfirmPsnPanelForBatchChangeCalendarDay();
			confirmPsnPanel.init();
		}
		return confirmPsnPanel;
	}

}
