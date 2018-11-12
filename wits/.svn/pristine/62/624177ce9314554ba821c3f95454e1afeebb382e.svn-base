package nc.ui.ta.leave.pf.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveApplyQueryMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.ui.hr.pf.action.PFSaveAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.bill.BillInfoDlg;
import nc.ui.ta.leave.pf.model.LeaveAppModel;
import nc.ui.ta.leave.pf.view.LeaveCardForm;
import nc.ui.ta.leave.pf.view.SplitResultDialog;
import nc.ui.ta.wf.pub.TBMPubBillCardForm;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCheckResult;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timerule.TimeRuleVO;

/**
  *
 * @author wwb
 *
 */
public class PFSaveLeaveAction extends PFSaveAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void validate(Object objValue) {
		super.validate(objValue);
	}

//	@Override
//	public LeaveAppModel getModel() {
//		return (LeaveAppModel) super.getModel();
//	}

	private boolean islactation()
	{
		boolean islactation = false;
		if(getModel().getUiState()==UIState.ADD)
		{
			islactation = ((LeaveAppModel)getModel()).isIslactation()==null?false:((LeaveAppModel)getModel()).isIslactation().booleanValue();
		}
		else
		{
			AggLeaveVO aggVO = (AggLeaveVO)getModel().getSelectedData();
			if(aggVO!=null)
			{
				islactation = aggVO.getLeavehVO().getIslactation()==null?false:aggVO.getLeavehVO().getIslactation().booleanValue();
			}
		}

		return islactation;
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {

		((LeaveCardForm)getEditor()).getBillCardPanel().stopEditing();
		AggLeaveVO aggVO = (AggLeaveVO)((TBMPubBillCardForm)getEditor()).getValue(TBMPubBillCardForm.GET_ALLVALUE_WITHDEL);
		
		LeavehVO headVO = aggVO.getLeavehVO();
		headVO.setIslactation(UFBoolean.valueOf(islactation()));
		super.validate(aggVO);
//		LeaveCardForm form = (LeaveCardForm)getEditor();
//		aggVO = (AggLeaveVO)form.getBillCardPanel().getBillValueChangeVO(AggLeaveVO.class.getName(), LeavehVO.class.getName(), LeavebVO.class.getName());
//
		for(int i=0;aggVO.getLeavebVOs()!=null&&i<aggVO.getLeavebVOs().length;i++)
		{
			aggVO.getLeavebVOs()[i].setIslactation(UFBoolean.valueOf(islactation()));
		}

		ILeaveApplyQueryMaintain maintain = NCLocator.getInstance().lookup(ILeaveApplyQueryMaintain.class);
		try{
			if(!islactation()){
//				if((aggVO.getLeavehVO().getSumhour()==null||aggVO.getLeavehVO().getSumhour().doubleValue()<=0.00))
//					throw new BusinessException(ResHelper.getString("6017leave","06017leave0194")
//					/*@res "休假明细时长为0，不能保存!"*/);
				
				LeavebVO[] leavebvos = aggVO.getLeavebVOs();
				List<LeavebVO> subvos = new ArrayList<LeavebVO>();
				for(int i = 0; i < leavebvos.length; i++){
					if(VOStatus.DELETED == leavebvos[i].getStatus())
						continue;
					subvos.add(leavebvos[i]);
				}
				LeavebVO[] leaveVOs = subvos.toArray(new LeavebVO[0]);
				int year = 0;
				for(int i=0; i<leaveVOs.length; i++){
					if(leaveVOs[i].getLeavehour().doubleValue() <= 0.00){
						throw new ValidationException(
								ResHelper.getString("6017leave","06017leave0249"/*@res "休假明细第{0}行的休假时长为0,不能保存!"*/,i+1+""));
					}
					UFDateTime begintime = leaveVOs[i].getLeavebegintime();
					UFDateTime endtime = leaveVOs[i].getLeaveendtime();
					if(begintime != null && endtime != null){
						if(year != 0 && year != begintime.getYear()){
							throw new ValidationException(
									ResHelper.getString("6017leave","06017leave0266"/*@res "休假明细第{0}行的休假时间不能跨年,不能保存!"*/,i+"、"+(i+1)+""));
						}else if(begintime.getYear() != endtime.getYear()){
							throw new ValidationException(
									ResHelper.getString("6017leave","06017leave0266"/*@res "休假明细第{0}行的休假时间不能跨年,不能保存!"*/,i+1+""));
						}
						year = begintime.getYear();
					}
				}
			}
			LeaveCheckResult<AggLeaveVO> checkResult = maintain.checkWhenSave(aggVO);
			
			
			//保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = checkResult.getMutexCheckResult();
			if(checkMutextResult!=null){
				int result  = BillInfoDlg.showOkCancelDialog(getContext().getEntranceUI(), null, CommonUtils.transferMap(checkMutextResult));
				if(result!=UIDialog.ID_OK){
					putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
					return;
				}
			}
			SplitBillResult<AggLeaveVO> splitResult = checkResult.getSplitResult();
			if(splitResult.needQueryUser()){//如果拆单了,或者时长超限了，则要将拆单结果提示给用户
				TimeRuleVO timeRuleVO = ((TALoginContext)getModel().getContext()).getAllParams().getTimeRuleVO();
				int result = SplitResultDialog.showOkCancelDialog(getContext().getEntranceUI(), null, splitResult,timeRuleVO);
				if(result!=UIDialog.ID_OK&&result!=UIDialog.ID_YES){
					putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
					return;
				}
			}
//			if(!islactation()){
//				if((aggVO.getLeavehVO().getSumhour()==null||aggVO.getLeavehVO().getSumhour().doubleValue()<=0.00))
//					throw new BusinessException(ResHelper.getString("6017leave","06017leave0194")
//					/*@res "休假总时长为0，不能保存!"*/);
//			}
			boolean validateResult = ((BillForm) getEditor()).getBillCardPanel().getBillData().execValidateFormulas();

			if (!validateResult)
				return;
			if (getModel().getUiState() == UIState.ADD){
				getModel().add(splitResult);
				int size = getModel().getRowCount();
				int length = splitResult.getSplitResult().length;
				if(length>1){
					int[] selectedrows = new int[length];
					for(int i=0;i<length;i++){
						selectedrows[i] = size-i-1;
					}
					getModel().setSelectedOperaRowsWithoutEvent(selectedrows);
				}
			}
			else if (getModel().getUiState() == UIState.EDIT)
				getModel().update(splitResult);
			getModel().setUiState(UIState.NOT_EDIT);
			ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(), getContext());
			((LeaveAppModel)getModel()).setIslactation(UFBoolean.valueOf(islactation()));
		}
			
		//如果抛了BillMutexException，则表明存在不被允许的冲突，要显示这些冲突单据，并返回，不执行后面的保存
		catch(BillMutexException bme){
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result = bme.getMutexBillsMap();
			if(result!=null){
				BillInfoDlg.showErrorDialog(getContext().getEntranceUI(), null, CommonUtils.transferMap(result));
				throw new BusinessException(ResHelper.getString("6017leave","06017leave0195")
/*@res "单据存在冲突数据，不能保存!"*/);
//				return;
			}
		}
	}
}
