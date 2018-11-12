package nc.ui.wa.psndocwadoc.view.labourjoin;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hr.hi.BatchInsuranceFieldDeclaration;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class WizardListenerForOutJoin implements IWizardDialogListener {

    @Override
    public void wizardCancel(WizardEvent event) throws WizardActionException {
	// TODO Auto-generated method stub

    }

    @Override
    public void wizardFinish(WizardEvent event) throws WizardActionException {
	String error = WorkbenchEnvironment.getInstance().getClientCache("Outerror") == null ? ""
		: WorkbenchEnvironment.getInstance().getClientCache("Outerror").toString();
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("Outerror");
	    MessageDialog.showErrorDlg(null, "错误信息", error);
	    throw new WizardActionException(new Exception(error));
	}
	HRWizardModel wizardModel = (HRWizardModel) event.getModel();
	// 取出日期范围
	SelPsnStepForOutJoin step1 = (SelPsnStepForOutJoin) wizardModel.getSteps().get(0);
	SelPsnPanelForOutJoin pane1 = step1.getSelPsnPanelForOutJoin();
	String pk_org = pane1.getPK_BU();

	// 取出人员主键
	ConfirmPsnStepForOutJoin step2 = (ConfirmPsnStepForOutJoin) wizardModel.getSteps().get(1);
	ConfirmPsnPanelForOutJoin pane2 = step2.getConfirmPsnPanelForOutJoin();
	String[] pk_psndocs = pane2.getSelPkPsndocs();

	JoinSetStepForOutJoin step3 = (JoinSetStepForOutJoin) wizardModel.getSteps().get(2);
	JoinSetPanelForOutJoin pane3 = step3.getJoinSetPanelForOutJoin();
	// 结束日期
	String enddate = pane3.getRefBeginDate().getValueObj() == null ? "" : pane3.getRefBeginDate().getValueObj()
		.toString();
	// 险种
	String insurancetype = pane3.getxianZhongPk() == null ? "" : pane3.getxianZhongPk();
	Map<String, String> outMap = new HashMap<String, String>();
	outMap.put(BatchInsuranceFieldDeclaration.END_DATE, enddate);
	outMap.put(BatchInsuranceFieldDeclaration.INSURANCE_TYPE, insurancetype);
	IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
	try {
		service.delGroupIns(pk_org, pk_psndocs,outMap);
	} catch (BusinessException e) {
		throw new WizardActionException(e);
	}
	event.getModel().gotoStepForwardNoValidate(0);
    }

    @Override
    public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {
	// TODO Auto-generated method stub

    }

}
