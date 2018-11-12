package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.psndocwadoc.validator.circularlyarrange.JoinSetStepValidatorForQuitJoin;
import nc.ui.uif2.model.IAppModel;
import nc.vo.pub.BusinessException;

/**
 * 设置循环排班的step
 * @author zengcheng
 *
 */
public class JoinSetStepForQuitJoin extends WizardStep {

	private JoinSetPanelForQuitJoin joinSetPanel = null;

	private IAppModel appModel;

	public JoinSetStepForQuitJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getJoinSetPanelForQuitJoin());
		getListeners().add(new JoinSetStepListenerForQuitJoin());
		getValidators().add(new JoinSetStepValidatorForQuitJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "设置修改班次"*/);
		setTitle("劳健保批量退保"/*@res "批改"*/);
		setDescription("设置退保信息"/*@res "设置修改班次"*/);
	}

	public JoinSetPanelForQuitJoin getJoinSetPanelForQuitJoin(){
		if(joinSetPanel==null){
			joinSetPanel = new JoinSetPanelForQuitJoin();
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