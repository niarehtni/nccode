package nc.ui.wa.psndocwadoc.view.labourjoin;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hr.hi.BatchInsuranceFieldDeclaration;
import nc.itf.hr.hi.InsuranceTypeEnum;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class WizardListenerForQuitJoin implements IWizardDialogListener {

    @Override
    public void wizardCancel(WizardEvent event) throws WizardActionException {
	// TODO Auto-generated method stub

    }

    @Override
    public void wizardFinish(WizardEvent event) throws WizardActionException {
	String error = WorkbenchEnvironment.getInstance().getClientCache("Quiterror") == null ? ""
		: WorkbenchEnvironment.getInstance().getClientCache("Quiterror").toString();
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("Quiterror");
	    MessageDialog.showErrorDlg(null, "������Ϣ", error);
	    throw new WizardActionException(new Exception(error));
	}
	HRWizardModel wizardModel = (HRWizardModel) event.getModel();
	// ȡ�����ڷ�Χ
	SelPsnStepForQuitJoin step1 = (SelPsnStepForQuitJoin) wizardModel.getSteps().get(0);
	SelPsnPanelForQuitJoin pane1 = step1.getSelPsnPanelForQuitJoin();
	String pk_org = pane1.getPK_BU();

	// ȡ����Ա����
	ConfirmPsnStepForQuitJoin step2 = (ConfirmPsnStepForQuitJoin) wizardModel.getSteps().get(1);
	ConfirmPsnPanelForQuitJoin pane2 = step2.getConfirmPsnPanelForQuitJoin();
	Map<String,Set<InsuranceTypeEnum>> psndocsAndInsuranceType = pane2.getSelPsndocsAndInsuranceType();

	// ȥ��������Ϣ
	JoinSetStepForQuitJoin step3 = (JoinSetStepForQuitJoin) wizardModel.getSteps().get(2);
	JoinSetPanelForQuitJoin pane3 = step3.getJoinSetPanelForQuitJoin();
	String enddate = pane3.getRefBeginDate().getValueObj() == null ? "" : pane3.getRefBeginDate().getValueObj()
		.toString();
	// �ͱ�
	Boolean islabourinsurance = pane3.getBaoCheckBox().isSelected();
	// ����
	Boolean islabourtreat = pane3.getTuiCheckBox().isSelected();
	// ����
	Boolean ishealthinsurance = pane3.getJianCheckBox().isSelected();
	// �춯����/Ͷ����̬��Ĭ��Ϊ�˱���
	String insuranceform = "2";
	Map<String, String> quitMap = new HashMap<String, String>();
	quitMap.put(BatchInsuranceFieldDeclaration.IS_LABOUR_INSURANCE, islabourinsurance == true ? "Y" : "N");
	quitMap.put(BatchInsuranceFieldDeclaration.IS_HEALTH_INSURANCE, ishealthinsurance == true ? "Y" : "N");
	quitMap.put(BatchInsuranceFieldDeclaration.IS_LABOUR_RETREAT, islabourtreat == true ? "Y" : "N");
	quitMap.put(BatchInsuranceFieldDeclaration.INSURANCE_FORM, insuranceform);
	quitMap.put(BatchInsuranceFieldDeclaration.END_DATE, enddate);
	IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
	try {
		service.delPsnNHI(pk_org, psndocsAndInsuranceType,quitMap);
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
