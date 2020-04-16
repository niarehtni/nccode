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
 * 考勤档案批量新增保存事件
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
		//第二步人员结果列表页面选择的人员信息
		TBMPsndocVO[] vos = listPanel.getSelectVos(TBMPsndocVO.class);
		if(ArrayUtils.isEmpty(vos))
			throw new BusinessException(ResHelper.getString("6017psndoc","06017psndoc0084")
/*@res "无批量新增数据"*/);
		TBMPsndocBatchCardPanel cardPanel = (TBMPsndocBatchCardPanel)wizardModel.getSteps().get(2).getComp();
		cardPanel.stopEditing();
		//取得考勤方式
		int tbm_prop = (Integer)cardPanel.getTbmPropComboBox().getSelectdItemValue();
		//取得工时形态 by he
		int tbm_weekform = (Integer)(cardPanel.getTbmWeekFormComboBox()).getSelectdItemValue();
		//取得加班管控
		int tbm_otcontrol = (Integer)(cardPanel.getTbmOtControComboBox()).getSelectdItemValue();
		//开始日期是否按到职日期计算
		boolean isInduty = cardPanel.getPostDateChcBx().isSelected();
		
		// 考勤增加不同步班次標識  by George 20200326 特性 #33851
		// 取得是否 不同步班組工作日曆
		boolean isNotsyncal = cardPanel.getNotsyncalDateChcBx().isSelected();
		
		//取得开始日期
		UFLiteralDate beginDate = cardPanel.getBeginDate();
		//取得考勤地点
		String pk_place = cardPanel.getPkPlace();
		validate(isInduty, beginDate);
		TimeRuleVO timeRulevo = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(getModel().getContext().getPk_org());
		//是否对人员进行默认派不能
		boolean isArrangeClass = false;
		if(timeRulevo != null && timeRulevo.getAutoarrangemonth() != 0) {
			int ret = MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(),
					null, ResHelper.getString("6017psndoc","06017psndoc0036")
/*@res "您需要对新加入考勤档案人员进行默认工作日历设置吗?"*/);
			isArrangeClass = (ret==UIDialog.ID_YES?true:false);
		}
		try {
			//批量新增
			// 考勤增加不同步班次標識  by George 20200326 特性 #33851
			// 批量新增考勤档案，新增邏輯參數為 不同步班組工作日曆(isNotsyncal) 
			vos = NCLocator.getInstance().lookup(ITBMPsndocManageMaintain.class).batchInsert(getModel().getContext(),
					vos, tbm_prop, beginDate,pk_place,tbm_weekform,tbm_otcontrol,isArrangeClass,isNotsyncal);
		} catch(BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw e;
		}
		dataManager.refresh();
	}

	/**
	 * 如果是指定日期，则开始日期为必输项
	 * @param isInduty
	 * @param beginDate
	 */
	private void validate(boolean isInduty,UFLiteralDate beginDate) throws BusinessException {
		if(!isInduty && beginDate == null) {
			throw new BusinessException(ResHelper.getString("6017psndoc","06017psndoc0085")
/*@res "开始日期不能为空"*/);
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