package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class JoinSetStepListenerForAddJoin implements IWizardStepListener {

    @Override
    public void stepActived(WizardStepEvent event) throws WizardStepException {
	// JoinSetStepForAddJoin currStep =
	// (JoinSetStepForAddJoin)event.getStep();
	// HRWizardModel model = (HRWizardModel)currStep.getModel();
	// if(!(model.getStepWhenAction() instanceof ConfirmPsnStepForAddJoin))
	// return;
	// SelPsnStepForAddJoin selPsnStep =
	// (SelPsnStepForAddJoin)model.getSteps().get(0);
	// SelPsnPanelForAddJoin selPsnPanel =
	// selPsnStep.getSelPsnPanelForAddJoin();
	// String pk_org = selPsnPanel.getPK_BU();
	// JoinSetPanelForAddJoin panel = currStep.getJoinSetPanelForAddJoin();
    }

    @Override
    public void stepDisactived(WizardStepEvent event) throws WizardStepException {
	JoinSetStepForAddJoin currStep = (JoinSetStepForAddJoin) event.getStep();
	JoinSetPanelForAddJoin pane = currStep.getJoinSetPanelForAddJoin();
	String error = "";
	String date = "";
	date = pane.getRefBeginDate().getValueObj() == null ? "" : pane.getRefBeginDate().getValueObj().toString();
	if ("".equals(date)) {
	    error += " 时间不能为空;\n ";
	}
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("Adderror");
	    WorkbenchEnvironment.getInstance().putClientCache("Adderror", error);
	}
    }

}
