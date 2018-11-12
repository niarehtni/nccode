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
	//�Ƿ���Ҫѡ��ҵ��Ԫ����
	private boolean needBURef=true;

	public SelPsnStepForAddJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getSelPsnPanelForAddJoin());
		getValidators().add(new CompValidator());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0059")/*@res "ѡ����Ա��Χ�����ڷ�Χ"*/);
//		setTitle(ResHelper.getString("common","UC001-0000085")/*@res "����"*/);
		setTitle("�����ӱ�");
		setDescription("ѡ����Ա��Χ"/*@res "ѡ����Ա��Χ"*/);
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
