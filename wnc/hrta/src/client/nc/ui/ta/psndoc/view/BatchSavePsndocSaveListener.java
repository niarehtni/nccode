package nc.ui.ta.psndoc.view;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITBMPsndocManageMaintain;
import nc.itf.ta.ITimeRuleQueryService;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.ta.psndoc.model.TbmPsndocAppModelDataManager;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * ���ڵ����������������¼�
 * @author caiyl
 *
 */
public class BatchSavePsndocSaveListener implements IWizardDialogListener {
	private AbstractUIAppModel model;
	private TbmPsndocAppModelDataManager dataManager;


	public void doSaveAction( WizardEvent event ) throws WizardActionException {
		try {
			HRWizardModel wizardModel = (HRWizardModel) event
					.getModel();
			batchSave(wizardModel);
		} catch (Exception e) {
			WizardActionException ex = new WizardActionException(e);
			if (e instanceof WizardStepValidateException) {
				for (String key : ((WizardStepValidateException) e).getMsgs()
						.keySet()) {
					ex.addMsg(key, ((WizardStepValidateException) e).getMsgs()
							.get(key));
				}
				throw ex;
			} else if (e instanceof BusinessException) {
				ex.addMsg(((BusinessException) e).getErrorCodeString(), e
						.getMessage());
				throw ex;
			}
			ex.addMsg("error", e.getMessage());
			throw ex;
		}
	}


	private void batchSave(HRWizardModel wizardModel) throws BusinessException {
		BatchAddPsnListPanel listPanel = (BatchAddPsnListPanel)wizardModel.getSteps().get(1).getComp();
		//�ڶ�����Ա����б�ҳ��ѡ�����Ա��Ϣ
		TBMPsndocVO[] vos = listPanel.getSelectVos(TBMPsndocVO.class);
		if(ArrayUtils.isEmpty(vos))
			throw new BusinessException(ResHelper.getString("6017psndoc","06017psndoc0084")
/*@res "��������������"*/);
		TBMPsndocBatchCardPanel cardPanel = (TBMPsndocBatchCardPanel)wizardModel.getSteps().get(2).getComp();
		cardPanel.stopEditing();
		//ȡ�ÿ��ڷ�ʽ
		int tbm_prop = (Integer)cardPanel.getTbmPropComboBox().getSelectdItemValue();
		//ȡ�ù�ʱ��̬ by he
		int tbm_weekform = (Integer)(cardPanel.getTbmWeekFormComboBox()).getSelectdItemValue();
		//ȡ�üӰ�ܿ�
		int tbm_otcontrol = (Integer)(cardPanel.getTbmOtControComboBox()).getSelectdItemValue();
		//��ʼ�����Ƿ񰴵�ְ���ڼ���
		boolean isInduty = cardPanel.getPostDateChcBx().isSelected();
		
		// �������Ӳ�ͬ����Θ��R  by George 20200326 ���� #33851
		// ȡ���Ƿ� ��ͬ����M�����Օ�
		boolean isNotsyncal = cardPanel.getNotsyncalDateChcBx().isSelected();
		
		//ȡ�ÿ�ʼ����
		UFLiteralDate beginDate = cardPanel.getBeginDate();
		//ȡ�ÿ��ڵص�
		String pk_place = cardPanel.getPkPlace();
		validate(isInduty, beginDate);
		TimeRuleVO timeRulevo = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(getModel().getContext().getPk_org());
		//�Ƿ����Ա����Ĭ���ɲ���
		boolean isArrangeClass = false;
		if(timeRulevo != null && timeRulevo.getAutoarrangemonth() != 0) {
			int ret = MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(),
					null, ResHelper.getString("6017psndoc","06017psndoc0036")
/*@res "����Ҫ���¼��뿼�ڵ�����Ա����Ĭ�Ϲ�������������?"*/);
			isArrangeClass = (ret==UIDialog.ID_YES?true:false);
		}
		try {
			//��������
			// �������Ӳ�ͬ����Θ��R  by George 20200326 ���� #33851
			// �����������ڵ���������߉݋������ ��ͬ����M�����Օ�(isNotsyncal) 
			vos = NCLocator.getInstance().lookup(ITBMPsndocManageMaintain.class).batchInsert(getModel().getContext(),
					vos, tbm_prop, beginDate,pk_place,tbm_weekform,tbm_otcontrol,isArrangeClass,isNotsyncal);
		} catch(BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw e;
		}
		dataManager.refresh();
	}

	/**
	 * �����ָ�����ڣ���ʼ����Ϊ������
	 * @param isInduty
	 * @param beginDate
	 */
	private void validate(boolean isInduty,UFLiteralDate beginDate) throws BusinessException {
		if(!isInduty && beginDate == null) {
			throw new BusinessException(ResHelper.getString("6017psndoc","06017psndoc0085")
/*@res "��ʼ���ڲ���Ϊ��"*/);
		}
	}

	public void wizardCancel( WizardEvent event ) throws WizardActionException {

	}

	public void wizardFinish( WizardEvent event ) throws WizardActionException {

		doSaveAction( event );
	}

	public void wizardFinishAndContinue( WizardEvent event ) throws WizardActionException {

		doSaveAction( event );
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}


	public TbmPsndocAppModelDataManager getDataManager() {
		return dataManager;
	}


	public void setDataManager(TbmPsndocAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}
}