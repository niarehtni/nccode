package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class JoinSetStepListenerForOutJoin implements IWizardStepListener {

    @Override
    public void stepActived(WizardStepEvent event) throws WizardStepException {
	// JoinSetStepForOutJoin currStep =
	// (JoinSetStepForOutJoin)event.getStep();
	// HRWizardModel model = (HRWizardModel)currStep.getModel();
	// if(!(model.getStepWhenAction() instanceof ConfirmPsnStepForOutJoin))
	// return;
	// SelPsnStepForOutJoin selPsnStep =
	// (SelPsnStepForOutJoin)model.getSteps().get(0);
	// SelPsnPanelForOutJoin selPsnPanel =
	// selPsnStep.getSelPsnPanelForOutJoin();
	// String pk_org = selPsnPanel.getPK_BU();
	// JoinSetPanelForOutJoin panel = currStep.getJoinSetPanelForOutJoin();
    }

    @Override
    public void stepDisactived(WizardStepEvent event) throws WizardStepException {
	JoinSetStepForOutJoin currStep = (JoinSetStepForOutJoin) event.getStep();
	JoinSetPanelForOutJoin pane = currStep.getJoinSetPanelForOutJoin();
	String error = "";
	String date = "";
	date = pane.getRefBeginDate().getValueObj() == null ? "" : pane.getRefBeginDate().getValueObj().toString();
	if ("".equals(date)) {
	    error += " 时间不能为空;\n ";
	}
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("Outerror");
	    WorkbenchEnvironment.getInstance().putClientCache("Outerror", error);
	}
    }

}
