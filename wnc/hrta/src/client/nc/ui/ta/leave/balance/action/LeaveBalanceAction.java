package nc.ui.ta.leave.balance.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import nc.bs.bank_cvp.compile.registry.BussinessMethods;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveBalanceManageMaintain;
import nc.itf.ta.IPeriodQueryService;
import nc.ui.hr.uif2.action.HrAsynAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.leave.balance.model.LeaveBalanceAppModel;
import nc.ui.ta.leave.balance.view.BalanceQueryDialog;
import nc.ui.ta.leave.balance.view.SettleToPeriodDialog;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.leavebalance.SettlementResult;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

public class LeaveBalanceAction extends HrAsynAction{
	/**
	 *
	 */
	private static final long serialVersionUID = 7689121030896552489L;
	private IAppModelDataManagerEx dataManager = null;
	private AbstractAppModel hierachicalModel;
	private BalanceQueryDialog balanceQueryDialog;//结算提示框
	private SettleToPeriodDialog periodDialog;
	private String pk_org;
	
	public LeaveBalanceAction() {
		setCode("LeaveBalanceAction");
		setBtnName(getSealStr());
	}

	@Override
	protected boolean isActionEnable() {
		return getModel().getUiState()==UIState.NOT_EDIT&&getModel().getSelectedData()!=null;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
//		super.actionPerformed(evt);
		 beforeDoAction();
		try {
			doAction(evt);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		LeaveBalanceAppModel model = (LeaveBalanceAppModel)getModel();
//		LeaveBalanceCalcDialog dlg = new LeaveBalanceCalcDialog(this.getEntranceUI(),getSealStr(),false);
//		dlg.initUI();
//		Object data = getHierachicalModel().getSelectedData();
//		LeaveBalanceVO[] vos = (LeaveBalanceVO[]) (CollectionUtils.isEmpty(model.getData())?null:model.getData().toArray(new LeaveBalanceVO[0]));
//		dlg.setVos(vos);
//		dlg.setTypeVO(data==null?null:(LeaveTypeCopyVO)data);
//		dlg.setYear(model.getYear());
//		dlg.setMonth(model.getMonth());
//		dlg.setContext(model.getContext());
//		dlg.setAppModel(model);
//
//		dlg.showModal();
		//v63修改，不再弹出结算日期选择框，按当前日期结算
		LeaveTypeCopyVO typevo = (LeaveTypeCopyVO) getHierachicalModel().getSelectedData();
		if(model.getSelectedOperaDatas()==null || model.getSelectedOperaDatas().length <= 0){
		    ShowStatusBarMsgUtil.showErrorMsg("無法結算", "請選擇操作資料行!", getContext());
		    return;
		}
		//华衍水务，转工资的类别需要提示结算到哪个期间（薪资取数时使用）
//		if(TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typevo.getLeavesettlement()){
		//MOD 张恒 {21860}  按照年资起算日的也要弹出框选择期间    2018/8/27
		if(TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typevo.getLeavesettlement() || TimeItemCopyVO.LEAVESETPERIOD_STARTDATE == typevo.getLeavesetperiod()){
			SettleToPeriodDialog periodDialog2 = getPeriodDialog();
			int showModal = periodDialog2.showModal();
			if(showModal != UIDialog.ID_OK){
				return;
			}
			String pk_salaryPeriod = periodDialog2.getRefPeriod().getRefPK();
			doSettlement(pk_salaryPeriod, e);
			return;
		}
		
		int show = MessageDialog.showOkCancelDlg(getEntranceUI(), ResHelper.getString("6017leave","06017leave0254")
				/*@res "结算提醒!"*/, "\n      " + ResHelper.getString("6017leave","06017leave0166")/*@res "结算"*/ + "? ");
		if(show != UIDialog.ID_OK){
			return;
		}
		doSettlement(null, e);
	}
	
	private void doSettlement(final String pk_period,final ActionEvent evt) throws BusinessException{
		 SwingWorker<Object, Object> worker = null;
	        
        try
        {
            // 后台任务启动前
            if (interceptor != null && !interceptor.beforeDoAction(this, evt) || !beforeStartDoAction(evt))
            {
                interceptor.afterDoActionFailed(this, evt, null);
                
                return;
            }
            
            worker = new SwingWorker<Object, Object>()
            {
                @Override
                protected Object doInBackground() throws Exception
                {
                	settlement(pk_period);
                    return null;
                }
                
                @Override
                protected void done()
                {
                    try
                    {
                        get();
                        
                        if (interceptor != null)
                        {
                            interceptor.afterDoActionSuccessed(LeaveBalanceAction.this, evt);
                        }
                        
                        doAfterSuccess(evt);
                    }
                    catch (ExecutionException ex)
                    {
                        boolean ret = doAfterFailure(evt, ex);
                        
                        if (ret && (interceptor == null || interceptor.afterDoActionFailed(LeaveBalanceAction.this, evt, ex)))
                        {
                            if (exceptionHandler != null)
                            {
                                if (ex.getCause() instanceof BusinessException)
                                {
                                    processExceptionHandler((BusinessException) ex.getCause());
                                }
                                else if (ex.getCause() instanceof RuntimeException && ex.getCause().getCause() instanceof BusinessException)
                                {
                                    processExceptionHandler((BusinessException) ex.getCause().getCause());
                                }
                                else
                                {
                                    processExceptionHandler(ex);
                                }
                            }
                        }
                    }
                    catch (InterruptedException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                    finally
                    {
                        Logger.debug("Leaving " + LeaveBalanceAction.this.getClass().toString() + ".actionPerformed");
                    }
                }
            };
            
            worker.execute();
        }
        catch (Exception ex)
        {
            boolean blResult = doAfterFailure(evt, ex);
            
            if (blResult && (interceptor == null || interceptor.afterDoActionFailed(LeaveBalanceAction.this, evt, ex)))
            {
                if (exceptionHandler != null)
                {
                    processExceptionHandler(ex);
                }
            }
        }
        finally
        {
            if (worker == null)
            {
                Logger.debug("Leaving " + LeaveBalanceAction.this.getClass().toString() + ".actionPerformed");
            }
        }
	}
	
	public SettleToPeriodDialog getPeriodDialog() {
		if(periodDialog == null){
			periodDialog = new SettleToPeriodDialog(getEntranceUI());
			periodDialog.getRefPeriod().getRefModel().setPk_org(getModel().getContext().getPk_org());
			//过滤掉已经封存的期间
			periodDialog.getRefPeriod().getRefModel().addWherePart(" and sealflag = 'N' ");
		}
		return periodDialog;
	}

	public IAppModelDataManagerEx getDataManager() {
		return dataManager;
	}

	public void setDataManager(IAppModelDataManagerEx dataManager) {
		this.dataManager = dataManager;
	}

	public AbstractAppModel getHierachicalModel() {
		return hierachicalModel;
	}

	public void setHierachicalModel(AbstractAppModel hierachicalModel) {
		this.hierachicalModel = hierachicalModel;
	}

	private String getSealStr(){
		return ResHelper.getString("6017leave","06017leave0166")/*@res "结算"*/;
	}

	private void settlement(String pk_period) throws BusinessException{
		LeaveBalanceAppModel model = (LeaveBalanceAppModel)getModel();
		LeaveTypeCopyVO typevo = (LeaveTypeCopyVO) getHierachicalModel().getSelectedData();
		Object[] selectObjs = model.getSelectedOperaDatas();
		LeaveBalanceVO[] vos = null;
		if(selectObjs !=null && selectObjs.length > 0){
		    vos = new LeaveBalanceVO[selectObjs.length];
		    for(int i = 0 ; i < selectObjs.length ;i++){
			vos[i] =  (LeaveBalanceVO)selectObjs[i];
		    }
		}else{
		    return ;
		}
		
		//LeaveBalanceVO[] vos = (LeaveBalanceVO[]) (CollectionUtils.isEmpty(model.getSelectedOperaDatas())?null:model.getSelectedOperaDatas());
		
		String year = model.getYear();
		String month = model.getMonth();
		
//		UFLiteralDate settlementDate = new UFLiteralDate();
		String pk_org = getContext().getPk_org();
		String pk_timeitem = typevo.getPk_timeitem();
		//进行第一次结算操作，如果没有需要提示用户的内容，则直接返回
		ILeaveBalanceManageMaintain maintain = NCLocator.getInstance().lookup(ILeaveBalanceManageMaintain.class);
		
		SettlementResult result = maintain.
			firstSettlement(pk_org, pk_timeitem, year, month, vos,pk_period);
		if(!result.needQueryUser()){
			if(StringUtils.isNotEmpty(result.getErrMsg())){
				String msg = ResHelper.getString("6017leave","06017leave0179", "\r\n"+result.getErrMsg())/*@res "下列记录结算失败:{0}"*/;
				putValue(MESSAGE_AFTER_ACTION, msg);
//				MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), null, MessageFormat.format(msg, "\r\n")+result.getErrMsg());
			}
			model.initModel(result.getSettledVOs());
			return;
		}
		BalanceQueryDialog queryDialog = getBalanceQueryDialog();
		boolean isSettlementNotToPeriodEndDateNoTbmPsndoc = false;
		if(result.needQueryUserNotToPeriodEndDateNoTbmPsndoc()){
			int queryResult = queryDialog.queryUser(BalanceQueryDialog.QUERY_TYPE_NOTTOPERIODENDDATENOTBMPSNDOC, result);
			if(queryResult==UIDialog.ID_CANCEL){
				putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
				return;
			}
			isSettlementNotToPeriodEndDateNoTbmPsndoc = queryResult==UIDialog.ID_YES;
		}
		boolean isSettlementNotToEffectiveDate4NoTbmPsndoc = false;
		if(result.needQueryUserNotToEffectiveDate4NoTbmPsndoc()){
			int queryResult = queryDialog.queryUser(BalanceQueryDialog.QUERY_TYPE_NOTTOEFFECTIVEDATENOTBMPSNDOC, result);
			if(queryResult==UIDialog.ID_CANCEL){
				putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
				return;
			}
			isSettlementNotToEffectiveDate4NoTbmPsndoc = queryResult==UIDialog.ID_YES;
		}
		boolean isSettlementNotToEffectiveDate4ExistTbmPsndoc = false;
		if(result.needQueryUserNotToEffectiveDate4ExistTbmPsndoc()){
			int queryResult = queryDialog.queryUser(BalanceQueryDialog.QUERY_TYPE_NOTTOEFFECTIVEDATEEXISTTBMPSNDOC, result);
			if(queryResult==UIDialog.ID_CANCEL){
				putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
				return;
			}
			isSettlementNotToEffectiveDate4ExistTbmPsndoc = queryResult==UIDialog.ID_YES;
		}
		result = maintain.secondSettlement(pk_org, pk_timeitem, year, month, vos,
				isSettlementNotToPeriodEndDateNoTbmPsndoc,
				isSettlementNotToEffectiveDate4NoTbmPsndoc,
				isSettlementNotToEffectiveDate4ExistTbmPsndoc,true,pk_period);
		if(StringUtils.isNotEmpty(result.getErrMsg())){
			String msg = ResHelper.getString("6017leave","06017leave0179", "\r\n"+result.getErrMsg())/*@res "下列记录结算失败:{0}"*/;
			putValue(MESSAGE_AFTER_ACTION, msg);
//			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), null, MessageFormat.format(msg, "\r\n")+result.getErrMsg());
		}
		model.initModel(result.getSettledVOs());
	}
	
	public BalanceQueryDialog getBalanceQueryDialog() {
		if(balanceQueryDialog==null){
			balanceQueryDialog = new BalanceQueryDialog(getModel().getContext().getEntranceUI());
			balanceQueryDialog.initUI();
			balanceQueryDialog.setTitle( ResHelper.getString("6017leave","06017leave0254")
			/*@res "结算提醒!"*/);
		}
		return balanceQueryDialog;
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if(AppEventConst.MODEL_INITIALIZED.equals(event.getType())){
			if(StringUtils.isBlank(getModel().getContext().getPk_org()))
				return;
			if(StringUtils.isBlank(pk_org)||!pk_org.equalsIgnoreCase(getModel().getContext().getPk_org())){
				getPeriodDialog().getRefPeriod().getRefModel().setPk_org(getModel().getContext().getPk_org());
				UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
				UFLiteralDate busLiteralDate = UFLiteralDate.getDate(busDate.toString().substring(0, 10));
				PeriodVO period = null;
				try {
					period = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(getModel().getContext().getPk_org(), busLiteralDate);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					return;
				}
				if(period==null)
					return;
				getPeriodDialog().getRefPeriod().setPK(period.getPk_period());
			}
			pk_org = getModel().getContext().getPk_org();
			
		}
	}
}