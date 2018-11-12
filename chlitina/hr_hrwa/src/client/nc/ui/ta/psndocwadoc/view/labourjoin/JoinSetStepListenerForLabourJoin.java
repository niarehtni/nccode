package nc.ui.ta.psndocwadoc.view.labourjoin;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class JoinSetStepListenerForLabourJoin implements IWizardStepListener {

    @Override
    public void stepActived(WizardStepEvent event) throws WizardStepException {
	// JoinSetStepForLabourJoin currStep =
	// (JoinSetStepForLabourJoin)event.getStep();
	// HRWizardModel model = (HRWizardModel)currStep.getModel();
	// if(!(model.getStepWhenAction() instanceof
	// ConfirmPsnStepForLabourJoin))
	// return;
	// SelPsnStepForLabourJoin selPsnStep =
	// (SelPsnStepForLabourJoin)model.getSteps().get(0);
	// SelPsnPanelForLabourJoin selPsnPanel =
	// selPsnStep.getSelPsnPanelForLabourJoin();
	// String pk_org = selPsnPanel.getPK_BU();
	// JoinSetPanelForLabourJoin panel =
	// currStep.getJoinSetPanelForLabourJoin();
    }

    @Override
    public void stepDisactived(WizardStepEvent event) throws WizardStepException {
	JoinSetStepForLabourJoin currStep = (JoinSetStepForLabourJoin) event.getStep();
	JoinSetPanelForLabourJoin pane = currStep.getJoinSetPanelForLabourJoin();
	Boolean baoSelected = pane.getBaoCheckBox().isSelected();
	Boolean tuiSelected = pane.getTuiCheckBox().isSelected();
	Boolean jianSelected = pane.getJianCheckBox().isSelected();
	String laoShen = "";
	String laoJi = "";
	String tuiFen = "";
	String tuiJi = "";
	String jianShen = "";
	String jianJi = "";
	String yiDong = "";
	String date = "";
	String error = "";
	date = pane.getRefBeginDate().getValueObj() == null ? "" : pane.getRefBeginDate().getValueObj().toString();
	if ("".equals(date)) {
	    error += " ʱ�䲻��Ϊ��;\n ";
	}
	if (baoSelected) {
	    laoShen = pane.getLaoShenRefPane().getRefPK() == null ? "" : pane.getLaoShenRefPane().getRefPK().toString();
	    laoJi = pane.getLaoJiRefPane().getRefValue("dd.id") == null ? "" : pane.getLaoJiRefPane()
		    .getRefValue("dd.id").toString();
	    if ("".equals(laoShen)) {
		error += " �ͱ�Ͷ����ֲ���Ϊ��;\n ";
	    }
	    if ("".equals(laoJi)) {
		error += " �ͱ����಻��Ϊ��;\n ";
	    }
	}
	if (tuiSelected) {
	    tuiFen = pane.getTuiFenRefPane().getRefPK() == null ? "" : pane.getTuiFenRefPane().getRefPK().toString();
	    tuiJi = pane.getTuiJiRefPane().getRefValue("dd.id") == null ? "" : pane.getTuiJiRefPane()
		    .getRefValue("dd.id").toString();
	    if ("".equals(tuiFen)) {
		error += " ���������ֲ���Ϊ��;\n ";
	    }
	    if ("".equals(tuiJi)) {
		error += " ���˼��಻��Ϊ��;\n ";
	    }
	}
	if (jianSelected) {
	    jianShen = pane.getJianShenRefPane().getRefPK() == null ? "" : pane.getJianShenRefPane().getRefPK()
		    .toString();
	    jianJi = pane.getJianJiRefPane().getRefValue("dd.id") == null ? "" : pane.getJianJiRefPane()
		    .getRefValue("dd.id").toString();
	    if ("".equals(tuiFen)) {
		error += " ����Ͷ����ݲ���Ϊ��;\n ";
	    }
	    if ("".equals(tuiJi)) {
		error += " �������಻��Ϊ��;\n ";
	    }
	}
	// laoFu = pane.getLaoBuRefPane().getRefPK() == null ? "" :
	// pane.getLaoBuRefPane().getRefPK().toString();
	// jianFu = pane.getJianBuRefPane().getRefValue("code") == null ? "" :
	// pane.getJianBuRefPane().getRefValue("code").toString();
	// teShu = pane.getTeShuRefPane().getRefPK() == null ? "" :
	// pane.getTeShuRefPane().getRefPK().toString();
	// rate = pane.getRateRefPane().getValueObj() == null ? "" :
	// pane.getRateRefPane().getRefPK().toString();;
	yiDong = pane.getYiDongRefPane().getSelectedObjects() == null ? "" : String.valueOf(pane.getYiDongRefPane()
		.getSelectedObjects()[0]);
	if ("".equals(yiDong)) {
	    error += " �춯���Ͳ���Ϊ��; ";
	}
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("labourerror");
	    WorkbenchEnvironment.getInstance().putClientCache("labourerror", error);
	}
    }

}
