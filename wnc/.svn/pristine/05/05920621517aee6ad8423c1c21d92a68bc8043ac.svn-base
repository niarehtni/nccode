package nc.ui.wa.psndocwadoc.view.labourjoin;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class JoinSetStepListenerForQuitJoin implements IWizardStepListener {

    @Override
    public void stepActived(WizardStepEvent event) throws WizardStepException {
	// JoinSetStepForQuitJoin currStep =
	// (JoinSetStepForQuitJoin)event.getStep();
	// HRWizardModel model = (HRWizardModel)currStep.getModel();
	// if(!(model.getStepWhenAction() instanceof ConfirmPsnStepForQuitJoin))
	// return;
	// SelPsnStepForQuitJoin selPsnStep =
	// (SelPsnStepForQuitJoin)model.getSteps().get(0);
	// SelPsnPanelForQuitJoin selPsnPanel =
	// selPsnStep.getSelPsnPanelForQuitJoin();
	// String pk_org = selPsnPanel.getPK_BU();
	// JoinSetPanelForQuitJoin panel =
	// currStep.getJoinSetPanelForQuitJoin();
    }

    @Override
    public void stepDisactived(WizardStepEvent event) throws WizardStepException {
	JoinSetStepForQuitJoin currStep = (JoinSetStepForQuitJoin) event.getStep();
	JoinSetPanelForQuitJoin pane = currStep.getJoinSetPanelForQuitJoin();
	String error = "";
	String date = "";
	date = pane.getRefBeginDate().getValueObj() == null ? "" : pane.getRefBeginDate().getValueObj().toString();
	if ("".equals(date)) {
	    error += " 时间不能为空;\n ";
	}
	if (!StringUtils.isEmpty(error)) {
	    WorkbenchEnvironment.getInstance().removeClientCache("Quiterror");
	    WorkbenchEnvironment.getInstance().putClientCache("Quiterror", error);
	}
	// String laoShen = "";
	// String laoJi = "";
	// String tuiFen = "";
	// String tuiJi = "";
	// String jianShen = "";
	// String jianJi = "";
	// String laoFu = "";
	// String jianFu = "";
	// String teShu = "";
	// String rate = "";
	// String yiDong = "";
	// String error = "";
	// if(baoSelected){
	// laoShen = pane.getLaoShenRefPane().getRefPK() == null ? "" :
	// pane.getLaoShenRefPane().getRefPK().toString();
	// laoJi = pane.getLaoJiRefPane().getRefValue("dd.id") == null ? "" :
	// pane.getLaoJiRefPane().getRefValue("dd.id").toString();
	// if("".equals(laoShen)){
	// error += " 劳保投保身分不能为空;\n ";
	// }
	// if("".equals(laoJi)){
	// error += " 劳保级距不能为空;\n ";
	// }
	// }
	// if(tuiSelected){
	// tuiFen = pane.getTuiFenRefPane().getRefPK() == null ? "" :
	// pane.getTuiFenRefPane().getRefPK().toString();
	// tuiJi = pane.getTuiJiRefPane().getRefValue("dd.id") == null ? "" :
	// pane.getTuiJiRefPane().getRefValue("dd.id").toString();
	// if("".equals(tuiFen)){
	// error += " 劳退提缴身分不能为空;\n ";
	// }
	// if("".equals(tuiJi)){
	// error += " 劳退级距不能为空;\n ";
	// }
	// }
	// if(jianSelected){
	// jianShen = pane.getJianShenRefPane().getRefPK() == null ? "" :
	// pane.getJianShenRefPane().getRefPK().toString();
	// jianJi = pane.getJianJiRefPane().getRefValue("dd.id") == null ? "" :
	// pane.getJianJiRefPane().getRefValue("dd.id").toString();
	// if("".equals(tuiFen)){
	// error += " 健保投保身份不能为空;\n ";
	// }
	// if("".equals(tuiJi)){
	// error += " 健保级距不能为空;\n ";
	// }
	// }
	// // laoFu = pane.getLaoBuRefPane().getRefPK() == null ? "" :
	// pane.getLaoBuRefPane().getRefPK().toString();
	// // jianFu = pane.getJianBuRefPane().getRefValue("code") == null ? ""
	// : pane.getJianBuRefPane().getRefValue("code").toString();
	// // teShu = pane.getTeShuRefPane().getRefPK() == null ? "" :
	// pane.getTeShuRefPane().getRefPK().toString();
	// // rate = pane.getRateRefPane().getValueObj() == null ? "" :
	// pane.getRateRefPane().getRefPK().toString();;
	// yiDong = pane.getYiDongRefPane().getSelectedObjects() == null ? "" :
	// String.valueOf(pane.getYiDongRefPane().getSelectedObjects()[0]);
	// if("".equals(yiDong)){
	// error += " 异动类型不能为空; ";
	// }
	// if(!StringUtils.isEmpty(error)){
	// WorkbenchEnvironment.getInstance().removeClientCache("Quiterror");
	// WorkbenchEnvironment.getInstance().putClientCache("Quiterror",
	// error);
	// }
    }

}
