package nc.ui.ta.psncalendar.view.batchchangecalendarday;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.view.batchchange.SelPsnPanelForBatchChange;
import nc.ui.ta.pub.wizard.validator.CompValidator;



/**
 * 用条件选择人员的step
 * @author Ares.Tank
 * @data 2018-8-15 15:07:49
 *
 */
public class SelPsnStepForBatchChangeCalendarDay extends WizardStep {

	private PsnCalendarAppModel appModel;
	private SelPsnPanelForBatchChange selPsnPanel;
	//是否需要选择业务单元参照
	private boolean needBURef=true;

	public SelPsnStepForBatchChangeCalendarDay() {
		
	}

	public void init(){
		setComp(getSelPsnPanelForBatchChangeCalendarDay());
		getValidators().add(new CompValidator());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0059")/*@res "选择人员范围和日期范围"*/);
		setTitle(ResHelper.getString("twhr_psncalendar","psncalendar-0000")/*@res "批改日历天"*/);
		setDescription(ResHelper.getString("twhr_psncalendar","psncalendar-0001")/*@res "选择人员范围"*/);
	}


	public SelPsnPanelForBatchChange getSelPsnPanelForBatchChangeCalendarDay() {
		if(selPsnPanel==null){
			selPsnPanel = new SelPsnPanelForBatchChange();
			selPsnPanel.setNeedBURef(needBURef);
			selPsnPanel.setModel(getAppModel());
			selPsnPanel.init();
		}
		return selPsnPanel;
	}


	public PsnCalendarAppModel getAppModel() {
		return appModel;
	}

	public void setAppModel(PsnCalendarAppModel model) {
		this.appModel = model;
	}

	public boolean isNeedBURef() {
		return needBURef;
	}

	public void setNeedBURef(boolean needBURef) {
		this.needBURef = needBURef;
	}

}