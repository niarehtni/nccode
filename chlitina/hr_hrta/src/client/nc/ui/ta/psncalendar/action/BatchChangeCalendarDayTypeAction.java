package nc.ui.ta.psncalendar.action;

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
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.view.batchchange.ConfirmPsnStepForBatchChange;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ConfirmPsnStepForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.SelPsnStepForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ShiftSetStepForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.WizardListenerForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchange.ShiftSetStepForBatchChange;
import nc.ui.ta.psncalendar.view.batchchange.WizardListenerForBatchChange;
import nc.vo.ta.PublicLangRes;

public class BatchChangeCalendarDayTypeAction extends HrAction {

	/**
	 *
	 */
	private static final long serialVersionUID = -6671656249456854266L;

	WizardDialog dialog = null;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if(UIDialog.ID_OK!=getDialog().showModal()){
			putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
			return;
		}
	}

	public BatchChangeCalendarDayTypeAction() {
		super();
		String name = ResHelper.getString("twhr_psncalendar","psncalendar-0000")
/*@res "批改日历天"*/;
		setBtnName(name);
		setCode("batchchangecalendarday");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, name+"(Ctrl+R)");
	}

	private WizardDialog getDialog(){
		if(dialog==null){
			List<WizardStep> stepList = new ArrayList<WizardStep>();

			SelPsnStepForBatchChangeCalendarDay selPsnStep = new SelPsnStepForBatchChangeCalendarDay();
			selPsnStep.setAppModel((PsnCalendarAppModel)getModel());
			selPsnStep.init();
			stepList.add(selPsnStep);

			ConfirmPsnStepForBatchChangeCalendarDay confirmPsnStep = new ConfirmPsnStepForBatchChangeCalendarDay();
			confirmPsnStep.init();
			stepList.add(confirmPsnStep);

			ShiftSetStepForBatchChangeCalendarDay shiftSetStep = new ShiftSetStepForBatchChangeCalendarDay();
			shiftSetStep.setAppModel(getModel());
			shiftSetStep.init();
			stepList.add(shiftSetStep);

			HRWizardModel wizardModel = new HRWizardModel(stepList);
			wizardModel.setModel(getModel());
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
			dialog.setWizardDialogListener(new WizardListenerForBatchChangeCalendarDay());
			dialog.setSize(900, 600);
			dialog.setResizable(false);
			dialog.setWizardExceptionHandler(new ErrorWizardExceptionHandler(dialog));
		}

		return dialog;
	}

	
	
	
	
	
	
	
	/*public BatchChangeDialog getDialog() {
		if(dialog==null){
			dialog = new BatchChangeDialog(getContext().getEntranceUI());
			dialog.setModel((PsnCalendarAppModel)getModel());
			dialog.initUI();
		}
		return dialog;
	}
*/
	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
//		UIState state = getModel().getUiState();
//		return (state==UIState.INIT||state==UIState.NOT_EDIT)&&!StringUtils.isEmpty(getModel().getContext().getPk_org());
	}

}