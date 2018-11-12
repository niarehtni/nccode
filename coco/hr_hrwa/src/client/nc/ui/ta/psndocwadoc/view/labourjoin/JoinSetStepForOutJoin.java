package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psndocwadoc.validator.circularlyarrange.JoinSetStepValidatorForOutJoin;
import nc.ui.uif2.model.IAppModel;
import nc.vo.pub.BusinessException;

/**
 * ����ѭ���Ű��step
 * @author zengcheng
 *
 */
public class JoinSetStepForOutJoin extends WizardStep {

	private JoinSetPanelForOutJoin joinSetPanel = null;

	private IAppModel appModel;

	public JoinSetStepForOutJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getJoinSetPanelForOutJoin());
		getListeners().add(new JoinSetStepListenerForOutJoin());
		getValidators().add(new JoinSetStepValidatorForOutJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "�����޸İ��"*/);
		setTitle("�ű������˱�"/*@res "����"*/);
		setDescription("�����˱���Ϣ"/*@res "�����޸İ��"*/);
	}

	public JoinSetPanelForOutJoin getJoinSetPanelForOutJoin(){
		if(joinSetPanel==null){
			joinSetPanel = new JoinSetPanelForOutJoin();
			joinSetPanel.setModel(getAppModel());
			joinSetPanel.init();
		}
		return joinSetPanel;
	}

	public IAppModel getAppModel() {
		return appModel;
	}

	public void setAppModel(IAppModel appModel) {
		this.appModel = appModel;
	}

}