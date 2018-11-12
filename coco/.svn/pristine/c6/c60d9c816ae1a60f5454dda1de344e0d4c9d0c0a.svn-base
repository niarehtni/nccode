package nc.ui.ta.teamcalendar.view.batchchangedaytype;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.itf.ta.ITeamCalendarManageMaintain;
import nc.itf.ta.ITeamCalendarQueryMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.ui.ta.teamcalendar.view.batchchange.SelectTeamPanelForBatchChange;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public class TeamCalendarWizardListenerForBatchChangeDayType implements IWizardDialogListener {
	private String pk_org = null;
	private String[] pk_teams = null;
	private WizardEvent event = null;

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {

	}

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		this.event = event;
		HRWizardModel wizardModel = (HRWizardModel) event.getModel();
		// 取出日期范围
		SelectTeamStepForBatchChangeDayType step1 = (SelectTeamStepForBatchChangeDayType) wizardModel.getSteps().get(0);
		SelectTeamPanelForBatchChange panel1 = step1.getTeamPanelForBatchChangeDayType();
		/*UFLiteralDate beginDate = panel1.getBeginDate();
		UFLiteralDate endDate = panel1.getEndDate();*/
		String pk_org = panel1.getPK_BU();
		// 取出班组主键
		ConfirmTeamStepForBatchChangeDayType step2 = (ConfirmTeamStepForBatchChangeDayType) wizardModel.getSteps()
				.get(1);
		ConfirmTeamPanelForBatchChangeDayType panel2 = step2.getTeamPanelForBatchChangeDayType();
		TeamHeadVO[] teamVOs = panel2.getSelTeamVOs();

		String[] pk_teams = StringPiecer.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
		this.pk_teams = pk_teams;
		// 取出是否调换日历天标志
		ShiftSetStepForBatchChangeDayType step3 = (ShiftSetStepForBatchChangeDayType) wizardModel.getSteps().get(2);
		ShiftSetPanelForBatchChangeDayType panel3 = step3.getShiftSetPanelForBatchChangeDayType();

		boolean changeDayTypeCheckBox = panel3.getChangeDayTypeCheckBox().isSelected();
		IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(IPsnCalendarManageValidate.class);
		//获取法人组织
		String[] orgs = { wizardModel.getModel().getContext().getPk_org() };
		String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs)
				.get(wizardModel.getModel().getContext().getPk_org());
		if (changeDayTypeCheckBox) {
			// 调换逻辑
			// 读取两个调换逻辑和两个调换的日历天类型
			UFLiteralDate firstDate = panel3.getFirstDate();
			UFLiteralDate secondDate = panel3.getSecondDate();
			try{
				//IHRHolidayQueryService
				//在页面做一个弹出框
				List<String> strMessage = manageValidate.updateteamValidate(wizardModel.getModel().getContext().getPk_org(),pk_teams, firstDate, secondDate);
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
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				WizardActionException wae = new WizardActionException(e);
				wae.addMsg("1", e.getMessage());
				throw wae;
			}
			if (null == firstDate || null != secondDate) {
				// 首先进行考勤档案的校验
				try {

					//,丢到后台去更改日历天

					ITeamCalendarManageMaintain queryMaintain = NCLocator.getInstance()
							.lookup(ITeamCalendarManageMaintain.class);

					queryMaintain.batchChangeDateType(pk_org, pk_teams, firstDate,
							secondDate);

				} catch (BusinessException e) {
					Debug.error(e.getMessage(), e);
					WizardActionException wae = new WizardActionException(e);
					wae.addMsg("1", e.getMessage());
					throw wae;
				}

			} else {
				WizardActionException wae = new WizardActionException(
						new BusinessException(ResHelper.getString("twhr_psncalendar", "psncalendar-0013")
						/* @res "调换日期不能为空" */));
				Debug.error(wae.getMessage(), wae);
				// throw wae;
				// wae.addMsg("1", e.getMessage());
				throw wae;
			}

			refreshData(firstDate, secondDate);
		} else {
			// 日历变更逻辑
			// 读取一个日历即可
			UFLiteralDate changeDate = panel3.getChangeDate();
			try{
				//IHRHolidayQueryService
				//在页面做一个弹出框
				List<String> strMessage = manageValidate.updateteamValidate(wizardModel.getModel().getContext().getPk_org(),pk_teams, changeDate, panel3.getafterChangeDateType());
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
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				WizardActionException wae = new WizardActionException(e);
				wae.addMsg("1", e.getMessage());
				throw wae;
			}
			if (null != changeDate) {
				try {
					//到后台去更改日历天

					ITeamCalendarManageMaintain queryMaintain = NCLocator.getInstance()
							.lookup(ITeamCalendarManageMaintain.class);

					queryMaintain.batchChangeDateType4OneDay(pk_org, pk_teams,
							changeDate, panel3.getafterChangeDateType());

				} catch (BusinessException e) {
					Debug.debug(e.getMessage(), e);
					e.printStackTrace();
				}
			}

			refreshData(changeDate, changeDate);
		}

		/*
		 * HRWizardModel wizardModel = (HRWizardModel)event.getModel(); //取出日期范围
		 * SelPsnStepForBatchChange step1 =
		 * (SelPsnStepForBatchChange)wizardModel.getSteps().get(0);
		 * SelPsnPanelForBatchChange panel1 =
		 * step1.getSelPsnPanelForBatchChange(); FromWhereSQL fromWhereSQL =
		 * panel1.getQuerySQL(); UFLiteralDate beginDate =
		 * panel1.getBeginDate(); UFLiteralDate endDate = panel1.getEndDate();
		 * String pk_org = panel1.getPK_BU(); //取出人员主键
		 * ConfirmPsnStepForBatchChange step2 =
		 * (ConfirmPsnStepForBatchChange)wizardModel.getSteps().get(1);
		 * ConfirmPsnPanelForBatchChange panel2 =
		 * step2.getConfirmPsnPanelForBatchChange(); String[] pk_psndocs =
		 * panel2.getSelPkPsndocs(); //取出遇假日排班取消标志 ShiftSetStepForBatchChange
		 * step3 = (ShiftSetStepForBatchChange)wizardModel.getSteps().get(2);
		 * ShiftSetPanelForBatchChange panel3 =
		 * step3.getShiftSetPanelForBatchChange(); // boolean isHolidayCancel =
		 * panel3.isHolidayCancel(); //取出班次主键 String old_Pk_shift =
		 * panel3.getOldShiftPk();//老的班次主键 String new_Pk_shift =
		 * panel3.getNewShiftPk();//新的班次主键
		 *
		 * Boolean
		 * withOldShift=panel3.getOldShiftCheckBox().isSelected();//原班次是否被选中
		 * IPsnCalendarManageMaintain manageMaintain =
		 * NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		 * IPsnCalendarQueryMaintain queryMaintain =
		 * NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class);
		 * PsnJobCalendarVO[] calendarVOs = null; try {
		 * manageMaintain.batchChangeShiftNew(pk_org, pk_psndocs, beginDate,
		 * endDate,withOldShift, old_Pk_shift,new_Pk_shift); String pk_hrorg =
		 * NCLocator.getInstance().lookup(IAOSQueryService.class).
		 * queryHROrgByOrgPK(pk_org).getPk_org(); //calendarVOs=
		 * queryMaintain.queryCalendarVOByPsndocs(pk_org, pk_psndocs, beginDate,
		 * endDate); calendarVOs=
		 * queryMaintain.queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs,
		 * beginDate, endDate);
		 *
		 *
		 * } catch (BusinessException e) { Debug.error(e.getMessage(), e);
		 * WizardActionException wae = new WizardActionException(e);
		 * wae.addMsg("1", e.getMessage()); throw wae; } PsnCalendarAppModel
		 * appModel = (PsnCalendarAppModel)wizardModel.getModel();
		 * appModel.setBeginEndDate(beginDate, endDate);
		 * appModel.initModel(calendarVOs);
		 * event.getModel().gotoStepForwardNoValidate(0);
		 */

		System.out.println("确定逻辑开始..............");
	}

	private void refreshData(UFLiteralDate firstDate, UFLiteralDate secondDate) {
		HRWizardModel wizardModel = (HRWizardModel) event.getModel();
		TeamCalendarAppModel appModel = (TeamCalendarAppModel) wizardModel.getModel();
		ITeamCalendarQueryMaintain queryMaintain = NCLocator.getInstance().lookup(ITeamCalendarQueryMaintain.class);

		TeamInfoCalendarVO[] calendarVOs = new TeamInfoCalendarVO[0];
		if (firstDate.before(secondDate)) {

			appModel.setBeginEndDate(firstDate, secondDate);
			try {
				//calendarVOs = queryMaintain.queryCalendarVOByPsndocs(pk_org, pk_psndocs, firstDate, secondDate);
				calendarVOs = queryMaintain.queryCalendarVOByPKTeams(pk_org, pk_teams, firstDate, secondDate);
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				e.printStackTrace();
			}

		} else {
			appModel.setBeginEndDate(secondDate, firstDate);
			try {
				calendarVOs = queryMaintain.queryCalendarVOByPKTeams(pk_org, pk_teams, secondDate, firstDate);
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		appModel.initModel(calendarVOs);

		event.getModel().gotoStepForwardNoValidate(0);
	}

	@Override
	public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {

	}
	/*@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		HRWizardModel wizardModel = (HRWizardModel)event.getModel();
		//取出日期范围
		SelectTeamStepForBatchChangeDayType step1 = (SelectTeamStepForBatchChangeDayType)wizardModel.getSteps().get(0);
		SelectTeamPanelForBatchChangeDayType panel1 = step1.getTeamPanelForBatchChangeDayType();
		UFLiteralDate beginDate = panel1.getBeginDate();
		UFLiteralDate endDate = panel1.getEndDate();
		String pk_org = panel1.getPK_BU();
		//取出班组主键
		ConfirmTeamStepForBatchChangeDayType step2 = (ConfirmTeamStepForBatchChangeDayType)wizardModel.getSteps().get(1);
		ConfirmTeamPanelForBatchChangeDayType panel2 = step2.getTeamPanelForBatchChangeDayType();
		TeamHeadVO[] teamVOs = panel2.getSelTeamVOs();
		String[] pk_teams = StringPiecer.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
		//取出遇假日排班取消标志
		ShiftSetStepForBatchChangeDayType step3 = (ShiftSetStepForBatchChangeDayType)wizardModel.getSteps().get(2);
		ShiftSetPanelForBatchChangeDayType panel3 = step3.getShiftSetPanelForBatchChangeDayType();
		//取出班次主键
		String old_Pk_shift = panel3.getOldShiftPk();//老的班次主键
		String new_Pk_shift = panel3.getNewShiftPk();//新的班次主键
		Boolean withOldShift=panel3.getOldShiftCheckBox().isSelected();//原班次是否被选中
		ITeamCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(ITeamCalendarManageMaintain.class);
		ITeamCalendarQueryMaintain queryMaintain = NCLocator.getInstance().lookup(ITeamCalendarQueryMaintain.class);
		TeamInfoCalendarVO[] calendarVOs = null;
		try {
			//批量更新
			  manageMaintain.batchChangeShiftNew(pk_org, pk_teams, beginDate, endDate,withOldShift, old_Pk_shift,new_Pk_shift);
			//更新后调用查询返回结果
			  String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
			 // calendarVOs = queryMaintain.queryCalendarVOByPKTeams(pk_org, pk_teams, beginDate, endDate);
			  calendarVOs = queryMaintain.queryCalendarVOByPKTeams(pk_hrorg, pk_teams, beginDate, endDate);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			WizardActionException wae = new WizardActionException(e);
			wae.addMsg("1", e.getMessage());
			throw wae;
		}
		TeamCalendarAppModel appModel = (TeamCalendarAppModel)wizardModel.getModel();
		appModel.setBeginEndDate(beginDate, endDate);
		appModel.initModel(calendarVOs);
		event.getModel().gotoStepForwardNoValidate(0);
	}

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {}
	@Override
	public void wizardFinishAndContinue(WizardEvent event)throws WizardActionException {}
*/
}
