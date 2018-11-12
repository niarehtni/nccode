package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.view.batchchange.SelPsnPanelForBatchChange;
import nc.ui.ta.pub.wizard.validator.CompValidator;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;

public class SelPsnStepForAddJoin extends WizardStep {
	
	private PsndocwadocAppModel appModel;
	private SelPsnPanelForAddJoin selPsnPanel;
	//是否需要选择业务单元参照
	private boolean needBURef=true;

	public SelPsnStepForAddJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getSelPsnPanelForAddJoin());
		getValidators().add(new CompValidator());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0059")/*@res "选择人员范围和日期范围"*/);
//		setTitle(ResHelper.getString("common","UC001-0000085")/*@res "批改"*/);
		setTitle("批量加保");
		setDescription("选择人员范围"/*@res "选择人员范围"*/);
	}


	public SelPsnPanelForAddJoin getSelPsnPanelForAddJoin() {
		if(selPsnPanel==null){
			selPsnPanel = new SelPsnPanelForAddJoin();
			selPsnPanel.setNeedBURef(needBURef);
			selPsnPanel.setModel(getAppModel());
			selPsnPanel.init();
		}
		return selPsnPanel;
	}


	public PsndocwadocAppModel getAppModel() {
		return appModel;
	}

	public void setAppModel(PsndocwadocAppModel model) {
		this.appModel = model;
	}

	public boolean isNeedBURef() {
		return needBURef;
	}

	public void setNeedBURef(boolean needBURef) {
		this.needBURef = needBURef;
	}
	

}
