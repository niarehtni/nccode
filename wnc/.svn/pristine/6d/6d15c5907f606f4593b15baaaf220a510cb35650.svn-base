package nc.ui.ta.teamcalendar.view.circularlyarrange;

import java.util.List;

import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.itf.ta.ITeamCalendarManageMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public class CircularlyArrangeWizardListener implements IWizardDialogListener {

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		final HRWizardModel wizardModel = (HRWizardModel) event.getModel();
		// 取出日期范围
		SelectTeamStep step1 = (SelectTeamStep) wizardModel.getSteps().get(0);
		SelectTeamPanel panel1 = step1.getTeamPanel();
		final UFLiteralDate beginDate = panel1.getBeginDate();
		final UFLiteralDate endDate = panel1.getEndDate();
		String pk_org = panel1.getPK_BU();
		// 取出“是否覆盖已有班次”
		boolean isOverride = panel1.isOverride();
		// 取出班组主键
		ConfirmTeamStep step2 = (ConfirmTeamStep) wizardModel.getSteps().get(1);
		ConfirmTeamPanel panel2 = step2.getTeamPanel();
		TeamHeadVO[] teamVOs = panel2.getSelTeamVOs();
		final String[] pk_teams = StringPiecer.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
		// 取出遇假日排班取消标志
		ShiftSetStep step3 = (ShiftSetStep) wizardModel.getSteps().get(2);
		ShiftSetPanel panel3 = step3.getShiftSetPanel();
		boolean isHolidayCancel = panel3.isHolidayCancel();
		// 取出班次主键
		final String[] pk_shifts = panel3.getCircularPks();
		ITeamCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(ITeamCalendarManageMaintain.class);
		final IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(
				IPsnCalendarManageValidate.class);
		TeamInfoCalendarVO[] calendarVOs = null;
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
																										 * "数据导入中，请稍等..."
																										 */);
						dialog.start();
						strMessage = manageValidate.teamvalidate(wizardModel.getModel().getContext().getPk_org(),
								pk_teams, beginDate, endDate, pk_shifts);
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
			// 在页面做一个弹出框
			// List<List<String>> strMessage = manageValidate.teamvalidate(
			// wizardModel.getModel().getContext().getPk_org(), pk_teams,
			// beginDate, endDate, pk_shifts);

			calendarVOs = manageMaintain.circularArrange(pk_org, pk_teams, beginDate, endDate, pk_shifts,
					isHolidayCancel, isOverride, true);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			WizardActionException wae = new WizardActionException(e);
			wae.addMsg("1", e.getMessage());
			throw wae;
		}
		TeamCalendarAppModel appModel = (TeamCalendarAppModel) wizardModel.getModel();
		appModel.setBeginEndDate(beginDate, endDate);
		appModel.initModel(calendarVOs);
		event.getModel().gotoStepForwardNoValidate(0);
	}

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {
	}

	@Override
	public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {
	}

}
