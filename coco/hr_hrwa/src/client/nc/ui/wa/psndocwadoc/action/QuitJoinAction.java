package nc.ui.wa.psndocwadoc.action;

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
import nc.ui.ta.psndocwadoc.view.labourjoin.ConfirmPsnStepForQuitJoin;
import nc.ui.ta.psndocwadoc.view.labourjoin.JoinSetStepForQuitJoin;
import nc.ui.ta.psndocwadoc.view.labourjoin.SelPsnStepForQuitJoin;
import nc.ui.ta.psndocwadoc.view.labourjoin.WizardListenerForQuitJoin;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.ta.PublicLangRes;

import nc.ui.hr.uif2.action.HrAction;


public class QuitJoinAction extends HrAction {
	
	/**
	 * 
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

	public QuitJoinAction() {
		super();
//		String name = ResHelper.getString("common","UC001-0000085")
		String name = "�˱�"
/*@res "����"*/;
		setBtnName(name);
		setCode("Quitjoin");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('I', Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, name+"(Ctrl+I)");
	}

	private WizardDialog getDialog() throws BusinessException{
		if(dialog==null){
			List<WizardStep> stepList = new ArrayList<WizardStep>();

			SelPsnStepForQuitJoin step1 = new SelPsnStepForQuitJoin();
			step1.setAppModel((PsndocwadocAppModel)getModel());
			step1.init();
			stepList.add(step1);

			ConfirmPsnStepForQuitJoin step2 = new ConfirmPsnStepForQuitJoin();
			step2.init();
			stepList.add(step2);

			JoinSetStepForQuitJoin step3 = new JoinSetStepForQuitJoin();
			step3.setAppModel(getModel());
			step3.init();
			stepList.add(step3);

			HRWizardModel wizardModel = new HRWizardModel(stepList);
			wizardModel.setModel(getModel());
			dialog = new WizardDialog(getContext().getEntranceUI(), wizardModel, stepList, null){

				/**
				 *
				 */
				private static final long serialVersionUID = 2834883786608019372L;

				/*
				 * �򿪶Ի�������¼��ص�һ����������ݣ�����ǵ�һ�δ򿪶Ի������Ѿ����ع������ˣ�����Ҫ�ظ����أ�
				 * (non-Javadoc)
				 * @see nc.ui.pub.beans.UIDialog#showModal()
				 */
				@Override
				public int showModal() {
					return super.showModal();
				};
			};
			dialog.setWizardDialogListener(new WizardListenerForQuitJoin());
			dialog.setSize(900, 600);
			dialog.setResizable(false);
			dialog.setWizardExceptionHandler(new ErrorWizardExceptionHandler(dialog));
		}

		return dialog;
	}

	
	
	
	
	
	
	
	/*public QuitJoinDialog getDialog() {
		if(dialog==null){
			dialog = new QuitJoinDialog(getContext().getEntranceUI());
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
