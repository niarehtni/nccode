package nc.ui.ta.leave.batch;


import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveApplyApproveManageMaintain;
import nc.itf.ta.ILeaveApplyQueryMaintain;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.ta.wf.batch.BatchAddBillSaveStep;
import nc.ui.ta.wf.batch.BatchAddSaveDataListener;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

public class LeaveSaveDataActionListener extends BatchAddSaveDataListener {

	@SuppressWarnings("unchecked")
	private void batchSave(HRWizardModel wizardModel)
			throws BusinessException {
		BatchAddBillSaveStep saveStep = (BatchAddBillSaveStep)wizardModel.getSteps().get(1);
		Class className = isFromApp()?LeavebVO.class:LeaveRegVO.class;
		LeaveCommonVO[] vos = (LeaveCommonVO[])saveStep.getFinalPanel().getBillSavePanel().getSelectVos(className);
		if(ArrayUtils.isEmpty(vos))
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0191")
/*@res "无批量新增数据"*/);
		boolean isFix = false;	//是否自动合并,调班登记页面无自动合并checkbox
		//如果来自申请页面，则判断是否合并单据
		if(isFromApp())
			isFix = saveStep.getFinalPanel().getBillSavePanel().getAutoFixCbx().isSelected();
		finalSave(vos, isFix);
	}

	/**
	 * 根据页面条件创建vo信息
	 * @param vos：批量新增第三步列表页面的数据：这里
	 * @param wizardModel
	 */
	@SuppressWarnings("unchecked")
	private void finalSave(LeaveCommonVO[] vos,boolean isFix) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return;
		String pk_group = getModel().getContext().getPk_group();
		String pk_org = getModel().getContext().getPk_org();
		if(isFromApp()) {
			for(LeavebVO vo : (LeavebVO[])vos) {
				vo.setPk_org(pk_org);
				vo.setPk_group(pk_group);
			}
			//保存申请
			AggLeaveVO[] newVos = NCLocator.getInstance().lookup(ILeaveApplyApproveManageMaintain.class).insertData((LeavebVO[])vos,isFix);
			List<Object> datas = ((BillManageModel)getModel()).getData();
			if(!CollectionUtils.isEmpty(datas)) {
				newVos = (AggLeaveVO[])org.apache.commons.lang.ArrayUtils.addAll(datas.toArray(new AggLeaveVO[0]), newVos);
			}
			getModel().initModel(newVos);
			return;
		}
		//保存登记
		saveReg(pk_group, pk_org, vos);

	}

	/**
	 * 保存登记
	 * @param pk_group
	 * @param pk_org
	 * @param isFix
	 * @param vos
	 */
	@SuppressWarnings("unchecked")
	private void saveReg(String pk_group,String pk_org, LeaveCommonVO[] vos)
				throws BusinessException {
		LeaveRegVO[] regvos = (LeaveRegVO[])vos;
		if(ArrayUtils.isEmpty(regvos))
			return;
		for(LeaveRegVO regvo: regvos) {
			//add by chenklb@yonyou.com 2018.5.7  批量保存时同时修改生效时间begin
			nc.vo.pub.lang.UFLiteralDate nowdate=new nc.vo.pub.lang.UFLiteralDate();
			regvo.setEffectivedate(nowdate.before(regvo.getLeaveenddate())?regvo.getLeaveenddate():nowdate);
			//add by chenklb@yonyou.com 2018.5.8  批量保存时同时修改生效时间begin
			
			regvo.setStatus(VOStatus.NEW);
			regvo.setPk_org(pk_org);
			regvo.setPk_group(pk_group);
			regvo.setIslactation(UFBoolean.valueOf(LeaveTypeCopyVO.LEAVETYPE_LACTATION.equalsIgnoreCase(regvo.getPk_timeitem())));
			regvo.setBillsource(ICommonConst.BILL_SOURCE_REG);
		}
		regvos = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class).insertArrayData(regvos);
		List<Object> datas = ((BillManageModel)getModel()).getData();
		if(!CollectionUtils.isEmpty(datas)) {
			regvos = (LeaveRegVO[])org.apache.commons.lang.ArrayUtils.addAll(datas.toArray(new LeaveRegVO[0]), regvos);
		}
		getModel().initModel(regvos);
	}


	@Override
	protected void doSaveAction(WizardEvent event) throws WizardActionException {

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


	public void wizardCancel(WizardEvent event) throws WizardActionException {
		super.wizardCancel(event);
	}

	/**
	 * 
	 */
	@Override
	protected Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			String pk_org, CircularlyAccessibleValueObject[] vos)
			throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return null;
		boolean isApp = vos[0] instanceof LeavebVO;
		return isApp? NCLocator.getInstance().lookup(ILeaveApplyQueryMaintain.class).check(pk_org, (LeavebVO[])vos):
			          NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class).check(pk_org, (LeaveRegVO[])vos);
	}

}