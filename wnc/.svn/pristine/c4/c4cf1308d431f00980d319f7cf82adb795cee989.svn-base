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

public class WizardListenerForAddJoin implements IWizardDialogListener {

    @Override
    public void wizardCancel(WizardEvent event) throws WizardActionException {

    }

    @Override
    public void wizardFinish(WizardEvent event) throws WizardActionException {
	String error = WorkbenchEnvironment.getInstance().getClientCache("Adderror") == null ? ""
		: WorkbenchEnvironment.getInstance().getClientCache("Adderror").toString();
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("Adderror");
	    MessageDialog.showErrorDlg(null, "错误信息", error);
	    throw new WizardActionException(new Exception(error));
	}
	HRWizardModel wizardModel = (HRWizardModel) event.getModel();
	// 取出日期范围
	SelPsnStepForAddJoin step1 = (SelPsnStepForAddJoin) wizardModel.getSteps().get(0);
	SelPsnPanelForAddJoin pane1 = step1.getSelPsnPanelForAddJoin();
	String pk_org = pane1.getPK_BU();

	// 取出人员主键
	ConfirmPsnStepForAddJoin step2 = (ConfirmPsnStepForAddJoin) wizardModel.getSteps().get(1);
	ConfirmPsnPanelForAddJoin pane2 = step2.getConfirmPsnPanelForAddJoin();
	String[] pk_psndocs = pane2.getSelPkPsndocs();

	JoinSetStepForAddJoin step3 = (JoinSetStepForAddJoin) wizardModel.getSteps().get(2);
	JoinSetPanelForAddJoin pane3 = step3.getJoinSetPanelForAddJoin();
	// 开始日期
	String begindate = pane3.getRefBeginDate().getValueObj().toString();
	// 险种
	String insurancetype = pane3.getxianZhongPk() == null ? "" : pane3.getxianZhongPk();
	// 团保基数
	String regimentinsurancebasenumber = pane3.getRateRefPane().getValueObj() == null ? "" : pane3.getRateRefPane()
		.getValueObj().toString();
	Map<String, String> addMap = new HashMap<String, String>();
	addMap.put(BatchInsuranceFieldDeclaration.BEGIN_DATE, begindate);
	addMap.put(BatchInsuranceFieldDeclaration.INSURANCE_TYPE, insurancetype);
	addMap.put(BatchInsuranceFieldDeclaration.REGIMENT_INSURANCE_BASE_NUMBER, regimentinsurancebasenumber);
	IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
	//生成团保
	try {
		service.generateGroupIns(pk_org, pk_psndocs,addMap);
	} catch (BusinessException e) {
		throw new WizardActionException(e);
	}
	event.getModel().gotoStepForwardNoValidate(0);
    }

    @Override
    public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {

    }

}
