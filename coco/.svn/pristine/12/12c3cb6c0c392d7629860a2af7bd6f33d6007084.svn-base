package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psndocwadoc.validator.circularlyarrange.JoinSetStepValidatorForAddJoin;
import nc.ui.uif2.model.IAppModel;

/**
 * 设置循环排班的step
 * 
 * @author zengcheng
 * 
 */
public class JoinSetStepForAddJoin extends WizardStep {

	private JoinSetPanelForAddJoin joinSetPanel = null;

	private IAppModel appModel;

	public JoinSetStepForAddJoin() {

	}

	public void init() {
		setComp(getJoinSetPanelForAddJoin());
		getListeners().add(new JoinSetStepListenerForAddJoin());
		getValidators().add(new JoinSetStepValidatorForAddJoin());
		// setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res
		// "设置修改班次"*/);
		setTitle("团保批量加保"/* @res "批改" */);
		setDescription("设置加保信息"/* @res "设置修改班次" */);
	}

	public JoinSetPanelForAddJoin getJoinSetPanelForAddJoin() {
		if (joinSetPanel == null) {
			joinSetPanel = new JoinSetPanelForAddJoin();
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