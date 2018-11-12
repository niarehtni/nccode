package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.view.batchchange.SelPsnPanelForBatchChange;
import nc.ui.ta.pub.wizard.validator.CompValidator;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.ui.wa.psndocwadoc.view.labourjoin.SelPsnPanelForLabourJoin;

public class SelPsnStepForLabourJoin extends WizardStep {
	
	private PsndocwadocAppModel appModel;
	private SelPsnPanelForLabourJoin selPsnPanel;
	//�Ƿ���Ҫѡ��ҵ��Ԫ����
	private boolean needBURef=true;

	public SelPsnStepForLabourJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getSelPsnPanelForLabourJoin());
		getValidators().add(new CompValidator());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0059")/*@res "ѡ����Ա��Χ�����ڷ�Χ"*/);
//		setTitle(ResHelper.getString("common","UC001-0000085")/*@res "����"*/);
		setTitle("�����ӱ�");
		setDescription("ѡ����Ա��Χ"/*@res "ѡ����Ա��Χ"*/);
	}


	public SelPsnPanelForLabourJoin getSelPsnPanelForLabourJoin() {
		if(selPsnPanel==null){
			selPsnPanel = new SelPsnPanelForLabourJoin();
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
