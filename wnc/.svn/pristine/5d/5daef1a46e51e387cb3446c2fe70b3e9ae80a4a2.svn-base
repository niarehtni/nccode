package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.uif2.model.IAppModel;
import nc.ui.wa.psndocwadoc.validator.circularlyarrange.JoinSetStepValidatorForAddJoin;
import nc.vo.pub.BusinessException;

/**
 * 设置循环排班的step
 * @author zengcheng
 *
 */
public class JoinSetStepForAddJoin extends WizardStep {

	private JoinSetPanelForAddJoin joinSetPanel = null;

	private IAppModel appModel;

	public JoinSetStepForAddJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getJoinSetPanelForAddJoin());
		getListeners().add(new JoinSetStepListenerForAddJoin());
		getValidators().add(new JoinSetStepValidatorForAddJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "设置修改班次"*/);
		setTitle("团保批量加保"/*@res "批改"*/);
		setDescription("设置加保信息"/*@res "设置修改班次"*/);
	}

	public JoinSetPanelForAddJoin getJoinSetPanelForAddJoin(){
		if(joinSetPanel==null){
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