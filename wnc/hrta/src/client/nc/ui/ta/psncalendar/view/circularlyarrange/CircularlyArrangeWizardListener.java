package nc.ui.ta.psncalendar.view.circularlyarrange;

import java.util.List;

import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.caculate.view.BannerTimerDialog;
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
		final HRWizardModel wizardModel = (HRWizardModel) event.getModel();
		// 取出日期范围
		SelPsnStep step1 = (SelPsnStep) wizardModel.getSteps().get(0);
		SelPsnPanel panel1 = step1.getSelPsnPanel();
		final UFLiteralDate beginDate = panel1.getBeginDate();
		final UFLiteralDate endDate = panel1.getEndDate();
		String pk_org = panel1.getPK_BU();
		// 取出“是否覆盖已有班次”
		boolean isOverride = panel1.isOverride();
		// 取出人员主键
		ConfirmPsnStep step2 = (ConfirmPsnStep) wizardModel.getSteps().get(1);
		ConfirmPsnPanel panel2 = step2.getConfirmPsnPanel();
		final String[] pk_psndocs = panel2.getSelPkPsndocs();
		// 取出遇假日排班取消标志
		ShiftSetStep step3 = (ShiftSetStep) wizardModel.getSteps().get(2);
		ShiftSetPanel panel3 = step3.getShiftSetPanel();
		boolean isHolidayCancel = panel3.isHolidayCancel();
		// 取出班次主键
		final String[] pk_shifts = panel3.getCircularPks();
		IPsnCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		final IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(
				IPsnCalendarManageValidate.class);
		PsnJobCalendarVO[] calendarVOs = null;
		// 获取法人组织
		try {

			new SwingWorker() {
				String[] orgs = { wizardModel.getModel().getContext().getPk_org() };
				String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs).get(
						wizardModel.getModel().getContext().getPk_org());
				// IHRHolidayQueryService
				UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
				BannerTimerDialog dialog = new BannerTimerDialog(wizardModel.getModel().getContext().getEntranceUI());
				String error = null;
				List<List<String>> strMessage = null;

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText(ResHelper.getString("twhr_psncalendar", "psncalendar-0022")/*
																										 * @
																										 * res
																										 * "排班校验中，请稍等..."
																										 */);
						dialog.start();
						strMessage = manageValidate.curvalidate(wizardModel.getModel().getContext().getPk_org(),
								pk_psndocs, beginDate, endDate, pk_shifts);
					} catch (LockFailedException le) {
						error = ResHelper.getString("60130paydata", "060130paydata0334")/*
																						 * @
																						 * res
																						 * "你操作的数据正被他人修改！"
																						 */;
					} catch (VersionConflictException le) {
						throw new BusinessException(le.getBusiObject().toString(), le);
					} catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}

				@Override
				protected void done() {
					if (null != strMessage) {
						for (List<String> strs : strMessage) {
							if (isStrcheck.booleanValue()) {
								// 严格校验（有错误则取消保存）
								MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "校验",
										"以下员工" + strs);
								return;
							} else {
								// 非严格校验（页面弹出一个框提醒用户哪些人不符合一例一休，然用户自己选择是否继续保存）
								if (2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext()
										.getEntranceUI(), "校验", "以下员工" + strs + "是否继续保存?")) {
									return;
								}

							}
						}

					}
				}
			}.execute();
			calendarVOs = manageMaintain.circularArrange(wizardModel.getModel().getContext().getPk_group(), wizardModel
					.getModel().getContext().getPk_org(), pk_org, pk_psndocs, beginDate, endDate, pk_shifts,
					isHolidayCancel, isOverride, true);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			WizardActionException wae = new WizardActionException(e);
			wae.addMsg("1", e.getMessage());
			throw wae;
		}
		PsnCalendarAppModel appModel = (PsnCalendarAppModel) wizardModel.getModel();
		appModel.setBeginEndDate(beginDate, endDate);
		appModel.initModel(calendarVOs);
		event.getModel().gotoStepForwardNoValidate(0);
	}

	@Override
	public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {
		// TODO Auto-generated method stub

	}

}
