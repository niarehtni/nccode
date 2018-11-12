package nc.ui.ta.psncalendar.view.circularlyarrange;

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
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public class CircularlyArrangeWizardListener implements IWizardDialogListener {

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		HRWizardModel wizardModel = (HRWizardModel)event.getModel();
		//ȡ�����ڷ�Χ
		SelPsnStep step1 = (SelPsnStep)wizardModel.getSteps().get(0);
		SelPsnPanel panel1 = step1.getSelPsnPanel();
		UFLiteralDate beginDate = panel1.getBeginDate();
		UFLiteralDate endDate = panel1.getEndDate();
		String pk_org = panel1.getPK_BU();
		//ȡ�����Ƿ񸲸����а�Ρ�
		boolean isOverride = panel1.isOverride();
		//ȡ����Ա����
		ConfirmPsnStep step2 = (ConfirmPsnStep)wizardModel.getSteps().get(1);
		ConfirmPsnPanel panel2 = step2.getConfirmPsnPanel();
		String[] pk_psndocs = panel2.getSelPkPsndocs();
		//ȡ���������Ű�ȡ����־
		ShiftSetStep step3 = (ShiftSetStep)wizardModel.getSteps().get(2);
		ShiftSetPanel panel3 = step3.getShiftSetPanel();
		boolean isHolidayCancel = panel3.isHolidayCancel();
		//ȡ���������
		String[] pk_shifts = panel3.getCircularPks();
		IPsnCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(IPsnCalendarManageValidate.class);
		PsnJobCalendarVO[] calendarVOs = null;
		//��ȡ������֯
		try {
			String[] orgs = { wizardModel.getModel().getContext().getPk_org() };
			String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs)
					.get(wizardModel.getModel().getContext().getPk_org());
			//IHRHolidayQueryService
			//��ҳ����һ��������
			List<List<String>> strMessage = manageValidate.curvalidate(wizardModel.getModel().getContext().getPk_org(),pk_psndocs, beginDate, endDate, pk_shifts);
			UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
			if(null != strMessage ){
				for(List<String> strs : strMessage){
						if(isStrcheck.booleanValue()){
							//�ϸ�У�飨�д������ֹ���棩
							MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "У��", "����Ա��"+strs);
							return;
						} else {
							//���ϸ�У�飨ҳ�浯��һ���������û���Щ�˲�����һ��һ�ݣ�Ȼ�û��Լ�ѡ���Ƿ�������棩
							if( 2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext().getEntranceUI(), "У��", "����Ա��"+strs+"�Ƿ��������?")){
								return;
							}
							
					}
				}


			}
			calendarVOs = manageMaintain.circularArrange(wizardModel.getModel().getContext().getPk_group(),wizardModel.getModel().getContext().getPk_org(),pk_org, pk_psndocs, beginDate, endDate, pk_shifts, isHolidayCancel,isOverride,true);
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
