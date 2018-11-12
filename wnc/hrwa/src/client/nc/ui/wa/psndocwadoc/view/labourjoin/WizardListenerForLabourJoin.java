package nc.ui.wa.psndocwadoc.view.labourjoin;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hr.hi.BatchInsuranceFieldDeclaration;
import nc.itf.hr.hi.InsuranceTypeEnum;
import nc.itf.hr.wa.IPsndocwadocLabourService;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class WizardListenerForLabourJoin implements IWizardDialogListener {

    @Override
    public void wizardCancel(WizardEvent event) throws WizardActionException {
	

    }

    @Override
    public void wizardFinish(WizardEvent event) throws WizardActionException {
	String error = WorkbenchEnvironment.getInstance().getClientCache("labourerror") == null ? ""
		: WorkbenchEnvironment.getInstance().getClientCache("labourerror").toString();
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("labourerror");
	    MessageDialog.showErrorDlg(null, "错误信息", error);
	    throw new WizardActionException(new Exception(error));
	}
	HRWizardModel wizardModel = (HRWizardModel) event.getModel();
	// 取出日期范围
	SelPsnStepForLabourJoin step1 = (SelPsnStepForLabourJoin) wizardModel.getSteps().get(0);
	SelPsnPanelForLabourJoin pane1 = step1.getSelPsnPanelForLabourJoin();
	String pk_org = pane1.getPK_BU();

	// 取出人员主键
	ConfirmPsnStepForLabourJoin step2 = (ConfirmPsnStepForLabourJoin) wizardModel.getSteps().get(1);
	ConfirmPsnPanelForLabourJoin pane2 = step2.getConfirmPsnPanelForLabourJoin();
	Map<String,Set<InsuranceTypeEnum>> psndocsAndInsuranceTypeMap = pane2.getSelPsndocsAndInsuranceType();

	JoinSetStepForLabourJoin step3 = (JoinSetStepForLabourJoin) wizardModel.getSteps().get(2);
	JoinSetPanelForLabourJoin pane3 = step3.getJoinSetPanelForLabourJoin();
	// 时间
	String begindate = pane3.getRefBeginDate().getValueObj() == null ? "" : pane3.getRefBeginDate().getValueObj()
		.toString();
	// 劳保
	Boolean islabourinsurance = pane3.getBaoCheckBox().isSelected();
	// 劳退
	Boolean islabourtreat = pane3.getTuiCheckBox().isSelected();
	// 健保
	Boolean ishealthinsurance = pane3.getJianCheckBox().isSelected();
	// 劳保投保身分
	String labourinsurancestatus = "";
	// 劳保级距
	String labourinsurancegradedistance = "";
	// 劳退提缴身分
	String labourretreatstatus = "";
	// 劳退级距
	String tuiJi = "";
	// 健保投保身份
	String healthinsurancestatus = "";
	// 健保级距
	String healthinsurancegradedistance = "";
	if (islabourinsurance) {
	    labourinsurancestatus = pane3.getLaoShenRefPane().getRefPK().toString();
	    labourinsurancegradedistance = pane3.getLaoJiRefPane().getRefValue("dd.id").toString();
	}
	if (islabourtreat) {
	    labourretreatstatus = pane3.getTuiFenRefPane().getRefPK().toString();
	    tuiJi = pane3.getTuiJiRefPane().getRefValue("dd.id").toString();
	}
	if (ishealthinsurance) {
	    healthinsurancestatus = pane3.getJianShenRefPane().getRefPK().toString();
	    healthinsurancegradedistance = pane3.getJianJiRefPane().getRefValue("dd.id").toString();
	}
	// 劳保补助身分
	String labourallowancestatus = pane3.getLaoBuPk() == null ? "" : pane3.getLaoBuPk();
	// 健保补助身分
	String healthallowancestatus = pane3.getJianbuPk() == null ? "" : pane3.getJianbuPk();
	// 劳保特殊身份别
	String labourinsurancespecialstatus = pane3.getTeShuPk() == null ? "" : pane3.getTeShuPk();
	// 自提比例
	String selfliftingratio = pane3.getRateRefPane().getValueObj() == null ? "" : pane3.getRateRefPane()
		.getValueObj().toString();
	// 异动类型/投保形态
	String insuranceform = String.valueOf(pane3.getYiDongRefPane().getSelectedObjects()[0]);
	if ("加保".equals(insuranceform)) {
	    insuranceform = "1";
	} else {
	    insuranceform = "3";
	}
	Map<String, String> labourMap = new HashMap<>();
	//开始日期
	labourMap.put(BatchInsuranceFieldDeclaration.BEGIN_DATE, begindate);
	
	//是否勾选劳保
	labourMap.put(BatchInsuranceFieldDeclaration.IS_LABOUR_INSURANCE, islabourinsurance == true ? "Y" : "N");
	//是否勾选劳退
	labourMap.put(BatchInsuranceFieldDeclaration.IS_LABOUR_RETREAT, islabourtreat == true ? "Y" : "N");
	//是否勾选健保
	labourMap.put(BatchInsuranceFieldDeclaration.IS_HEALTH_INSURANCE, ishealthinsurance == true ? "Y" : "N");
	
	//劳保投保身份
	labourMap.put(BatchInsuranceFieldDeclaration.LABOUR_INSURANCE_STATUS, labourinsurancestatus);
	//劳保级距
	labourMap.put(BatchInsuranceFieldDeclaration.LABOUR_INSURANCE_GRADEDISTANCE, labourinsurancegradedistance);
	
	//劳退提缴身份
	labourMap.put(BatchInsuranceFieldDeclaration.LABOUR_RETREAT_STATUS, labourretreatstatus);
	//劳退级距
	labourMap.put(BatchInsuranceFieldDeclaration.LABOUR_RETREAT_GRADE_DISTANCE, tuiJi);
	
	//健保投保身份
	labourMap.put(BatchInsuranceFieldDeclaration.HEALTH_INSURANCE_STATUS, healthinsurancestatus);
	//健保级距
	labourMap.put(BatchInsuranceFieldDeclaration.HEALTH_INSURANCE_GRADE_DISTANCE, healthinsurancegradedistance);
	
	
	//劳保补助身份
	labourMap.put(BatchInsuranceFieldDeclaration.LABOUR_ALLOWANCE_STATUS, labourallowancestatus);
	//健保补助身份
	labourMap.put(BatchInsuranceFieldDeclaration.HEALTH_ALLOWANCE_STATUS, healthallowancestatus);
	
	//劳保特殊身份别
	labourMap.put(BatchInsuranceFieldDeclaration.LABOUR_INSURANCE_SPECIAL_STATUS, labourinsurancespecialstatus);
	//自提比例
	labourMap.put(BatchInsuranceFieldDeclaration.SELF_LIFT_INGRATIO, selfliftingratio);
	
	//投保形态（异动类型）
	labourMap.put(BatchInsuranceFieldDeclaration.INSURANCE_FORM, insuranceform);
	IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
	//劳健保批量加保 (人力资源组织PK, 人员列表, 加保信息)
	try {
		service.generatePsnNHI(pk_org, psndocsAndInsuranceTypeMap,labourMap);
	} catch (BusinessException e) {
		throw new WizardActionException(e);
	}
	
	event.getModel().gotoStepForwardNoValidate(0);
	
    }

    @Override
    public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {
	

    }

}
