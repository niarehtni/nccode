package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psndocwadoc.validator.circularlyarrange.JoinSetStepValidatorForOutJoin;
import nc.ui.uif2.model.IAppModel;
import nc.vo.pub.BusinessException;

/**
 * 设置循环排班的step
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
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "设置修改班次"*/);
		setTitle("团保批量退保"/*@res "批改"*/);
		setDescription("设置退保信息"/*@res "设置修改班次"*/);
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