package nc.ui.ta.teamcalendar.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.hr.uif2.validator.ErrorWizardExceptionHandler;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.ConfirmTeamStepForBatchChangeDayType;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.SelectTeamStepForBatchChangeDayType;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.ShiftSetStepForBatchChangeDayType;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.TeamCalendarWizardListenerForBatchChangeDayType;
import nc.ui.uif2.UIState;
import nc.vo.ta.PublicLangRes;

import org.apache.commons.lang.StringUtils;

/**
 * 批改 action
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class BatchEditTeamCalendarDayTypeAction extends HrAction {

	private WizardDialog dialog;

	public BatchEditTeamCalendarDayTypeAction() {
		super();
		String name = ResHelper.getString("twhr_psncalendar","psncalendar-0000")
				/*@res "批改日历天"*/;
		setBtnName(name);
		setCode("batcheditdaytype");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, name+"(Ctrl+R)");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if(UIDialog.ID_OK!=getDialog().showModal()){
			putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
			return;
		}
	}
/*
	public BatchEditDialog getDlg() {
		if(dlg==null){
			dlg = new BatchEditDialog(getModel().getContext().getEntranceUI());
			dlg.setModel((TeamCalendarAppModel) getModel());
			dlg.initUI();
		}
		return dlg;
	}
	*/
	
	private WizardDialog getDialog(){
		if(dialog==null){
			List<WizardStep> stepList = new ArrayList<WizardStep>();

			SelectTeamStepForBatchChangeDayType selPsnStep = new SelectTeamStepForBatchChangeDayType();
			selPsnStep.setAppModel((TeamCalendarAppModel)getModel());
			selPsnStep.init();
			stepList.add(selPsnStep);

			ConfirmTeamStepForBatchChangeDayType confirmTeamStep = new ConfirmTeamStepForBatchChangeDayType();
			confirmTeamStep.init();
			stepList.add(confirmTeamStep);

			ShiftSetStepForBatchChangeDayType shiftSetStep = new ShiftSetStepForBatchChangeDayType();
			shiftSetStep.setAppModel(getModel());
			shiftSetStep.init();
			stepList.add(shiftSetStep);

			HRWizardModel wizardModel = new HRWizardModel(stepList);
			wizardModel.setModel((TeamCalendarAppModel)getModel());
			dialog = new WizardDialog(getContext().getEntranceUI(), wizardModel, stepList, null){

				/**
				 *
				 */
				private static final long serialVersionUID = 2834883786608019372L;

				/*
				 * 打开对话框后，重新加载第一个界面的数据（如果是第一次打开对话框，则已经加载过数据了，不需要重复加载）
				 * (non-Javadoc)
				 * @see nc.ui.pub.beans.UIDialog#showModal()
				 */
				@Override
				public int showModal() {
					return super.showModal();
				};
			};
			dialog.setWizardDialogListener(new TeamCalendarWizardListenerForBatchChangeDayType());
			dialog.setSize(900, 600);
			dialog.setResizable(false);
			dialog.setWizardExceptionHandler(new ErrorWizardExceptionHandler(dialog));
		}

		return dialog;
	}

	@Override
	protected boolean isActionEnable() {
		UIState state = getModel().getUiState();
		return (state==UIState.INIT||state==UIState.NOT_EDIT)&&!StringUtils.isEmpty(getModel().getContext().getPk_org());
	}
}