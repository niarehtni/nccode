package nc.ui.ta.leave.register.action;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.bill.BillInfoDlg;
import nc.ui.ta.leave.pf.view.SplitResultDialog;
import nc.ui.ta.leave.register.model.LeaveRegAppModel;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.LeaveCheckResult;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timerule.TimeRuleVO;

/**
 * 登记，保存 按钮
 * @author    蔡侑霖
 * @version	   最后修改日期 2010年12月8日20:47:09
 * @see	   SaveAction
 */
@SuppressWarnings("serial")
public class SaveLeaveRegAction extends SaveAction {


	@Override
    public void doAction(ActionEvent evt) throws Exception {

		Object objValue = super.getEditor().getValue();
		LeaveRegVO vo = (LeaveRegVO)objValue;

		super.validate(vo);

		String hint="";

		if(islactation())
		{
			if(vo.getLeavebegindate()==null||vo.getLeaveenddate()==null
					||vo.getLeavebegindate().after(vo.getLeaveenddate()))
			{
				hint = ResHelper.getString("6017leave","06017leave0203")
/*@res "开始日期{0}晚于结束日期{1},请修改!"*/;
				hint = MessageFormat.format(hint, vo.getLeavebegindate(),vo.getLeaveenddate());
			}else {
				if(vo.getLeavebegindate().getYear() != vo.getLeaveenddate().getYear()){
					hint = ResHelper.getString("6017leave","06017leave0265")
							/*@res "休假时间跨年,不能保存!"*/;
				}
			}
		}
		else
		{
			if(vo.getLeavebegintime()==null||vo.getLeaveendtime()==null
					||vo.getLeavebegintime().after(vo.getLeaveendtime()))
			{
				hint = ResHelper.getString("6017leave","06017leave0204")
/*@res "开始时间{0}晚于结束时间{1},请修改!"*/;
				hint = MessageFormat.format(hint, vo.getLeavebegintime(),vo.getLeaveendtime());
			}else if(vo.getLeavebegintime().getYear() != vo.getLeaveendtime().getYear()){
				hint = ResHelper.getString("6017leave","06017leave0265")
						/*@res "休假时间跨年,不能保存!"*/;
			}
		}

		if(!hint.equals(""))
		{
			throw new BusinessException(hint);
		}

		ILeaveRegisterQueryMaintain maintain = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
		try{
			if(!islactation())
			{
				if((vo.getLeavehour()==null||vo.getLeavehour().doubleValue()<=0.00))
				{
					IPersistenceRetrieve ipersistenceRetrieve=NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
					PsndocVO psndocvo=(PsndocVO) ipersistenceRetrieve.retrieveByPk(null, PsndocVO.class, vo.getPk_psndoc());
					String name=psndocvo.getName();
					throw new BusinessException(
							ResHelper.getString("6017leave","06017leave0250"/*@res "{0}的单据时长为0，不能保存!"*/,name));
			}
			}
			LeaveCheckResult<LeaveRegVO> checkResult = maintain.checkWhenSave(vo);
			
			//保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutexResult =checkResult.getMutexCheckResult();
			if(checkMutexResult!=null){
				int result  = BillInfoDlg.showOkCancelDialog(getContext().getEntranceUI(), null, CommonUtils.transferMap(checkMutexResult));
				if(result!=UIDialog.ID_OK){
					putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
					return;
				}
			}
			SplitBillResult<LeaveRegVO> splitResult = checkResult.getSplitResult();
			if(splitResult.needQueryUser()){//如果拆单了,或者超时长了，则要将拆单结果提示给用户
				TimeRuleVO timeRuleVO = ((TALoginContext)getModel().getContext()).getAllParams().getTimeRuleVO();
				int result = SplitResultDialog.showOkCancelDialog(getEntranceUI(), null, splitResult,timeRuleVO);
				if(result!=UIDialog.ID_OK&&result!=UIDialog.ID_YES){
					putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
					return;
				}
			}
//			if(!islactation())
//			{
//				if((vo.getLeavehour()==null||vo.getLeavehour().doubleValue()<=0.00))
//				{
//					throw new BusinessException(ResHelper.getString("6017leave","06017leave0194")
///*@res "休假总时长为0，不能保存!"*/);
//				}
//			}

			if (getModel().getUiState() == UIState.ADD)
				getModel().add(splitResult);
			else if (getModel().getUiState() == UIState.EDIT)
				getModel().update(splitResult);
			getModel().setUiState(UIState.NOT_EDIT);
			ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(), getContext());
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

		super.doAction(evt);
		((LeaveRegAppModel)getModel()).setIslactation(UFBoolean.valueOf(islactation()));
	}

	private boolean islactation()
	{
		boolean islactation = false;
		if(getModel().getUiState()==UIState.ADD)
		{
			islactation = ((LeaveRegAppModel)getModel()).Islactation()==null?false:((LeaveRegAppModel)getModel()).Islactation().booleanValue();
		}
		else
		{
			LeaveRegVO regVO = (LeaveRegVO)getModel().getSelectedData();
			if(regVO!=null)
			{
				islactation = regVO.getIslactation()==null?false:regVO.getIslactation().booleanValue();
			}
		}

		return islactation;
	}
}