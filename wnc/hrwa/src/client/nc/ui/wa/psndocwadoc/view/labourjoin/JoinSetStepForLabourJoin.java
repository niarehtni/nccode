package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.uif2.model.IAppModel;
import nc.ui.wa.psndocwadoc.validator.circularlyarrange.JoinSetStepValidatorForLabourJoin;
import nc.vo.pub.BusinessException;

/**
 * 设置循环排班的step
 * @author zengcheng
 *
 */
public class JoinSetStepForLabourJoin extends WizardStep {

	private JoinSetPanelForLabourJoin joinSetPanel = null;

	private IAppModel appModel;

	public JoinSetStepForLabourJoin() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setComp(getJoinSetPanelForLabourJoin());
		getListeners().add(new JoinSetStepListenerForLabourJoin());
		getValidators().add(new JoinSetStepValidatorForLabourJoin());
//		setTitle(ResHelper.getString("6017psncalendar","06017psncalendar0104")/*@res "设置修改班次"*/);
		setTitle("劳健保批量加保"/*@res "批改"*/);
		setDescription("设置加保信息"/*@res "设置修改班次"*/);
	}

	public JoinSetPanelForLabourJoin getJoinSetPanelForLabourJoin(){
		if(joinSetPanel==null){
			joinSetPanel = new JoinSetPanelForLabourJoin();
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