package nc.ui.ta.psncalendar.view.usedefault;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.view.circularlyarrange.ConfirmPsnPanel;
import nc.ui.ta.psncalendar.view.circularlyarrange.ConfirmPsnStep;
import nc.ui.ta.psncalendar.view.circularlyarrange.SelPsnPanel;
import nc.ui.ta.psncalendar.view.circularlyarrange.SelPsnStep;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public class UseDefaultWizardListener implements IWizardDialogListener {

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		HRWizardModel wizardModel = (HRWizardModel)event.getModel();
		//取出日期范围
		SelPsnStep step1 = (SelPsnStep)wizardModel.getSteps().get(0);
		SelPsnPanel panel1 = step1.getSelPsnPanel();
		UFLiteralDate beginDate = panel1.getBeginDate();
		UFLiteralDate endDate = panel1.getEndDate();
		//取出“是否覆盖已有班次”
		boolean isOverride = panel1.isOverride();
		//取出人员主键
		ConfirmPsnStep step2 = (ConfirmPsnStep)wizardModel.getSteps().get(1);
		ConfirmPsnPanel panel2 = step2.getConfirmPsnPanel();
		String[] pk_psndocs = panel2.getSelPkPsndocs();
		IPsnCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(IPsnCalendarManageValidate.class);
		PsnJobCalendarVO[] calendarVOs = null;
		//获取法人组织
		try {
			String[] orgs = { wizardModel.getModel().getContext().getPk_org() };
			String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs)
					.get(wizardModel.getModel().getContext().getPk_org());
			//IHRHolidayQueryService
			//在页面做一个弹出框
			List<String> strMessage = manageValidate.validate(wizardModel.getModel().getContext().getPk_org(),pk_psndocs, beginDate, endDate);
			UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
			if(null != strMessage ){
				for(String strs : strMessage){
					if(isStrcheck.booleanValue()){
						//严格校验（有错误则取消保存）
						MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "校验", "以下员工"+strs);
						return;
					} else {
						//非严格校验（页面弹出一个框提醒用户哪些人不符合一例一休，然用户自己选择是否继续保存）
						if( 2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext().getEntranceUI(), "校验", "以下员工"+strs+"是否继续保存?")){
							return;
						}

					}
				}


			}
			calendarVOs = manageMaintain.useDefault(wizardModel.getModel().getContext().getPk_org(), pk_psndocs, beginDate, endDate,isOverride);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			WizardActionException wae = new WizardActionException(e);
			wae.addMsg("1", e.getMessage());
			throw wae;
		}
		PsnCalendarAppModel appModel = (PsnCalendarAppModel)wizardModel.getModel();
		appModel.setBeginEndDate(beginDate, endDate);
		appModel.initModel(calendarVOs);
		event.getModel().gotoStepForwardNoValidate(0);
	}

	@Override
	public void wizardFinishAndContinue(WizardEvent event)
			throws WizardActionException {
		// TODO Auto-generated method stub

	}

}
