package nc.ui.ta.leave.balance.action;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveBalanceManageMaintain;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.leave.balance.model.LeaveBalanceAppModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.leavebalance.UnSettlementResult;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

public class UnLeaveBalanceAction extends HrAction{
	/**
	 *
	 */
	private static final long serialVersionUID = -9078967112171325004L;
	private IAppModelDataManagerEx dataManager = null;
	private AbstractAppModel hierachicalModel;
	public UnLeaveBalanceAction() {
		setCode("UnLeaveBalanceAction");
		setBtnName(ResHelper.getString("6017leave","06017leave0258")
/*@res "反结算"*/);
	}

	@Override
	protected boolean isActionEnable() {
		return getModel().getUiState()==UIState.NOT_EDIT&&getModel().getSelectedData()!=null;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
	   LeaveBalanceAppModel model = (LeaveBalanceAppModel)getModel();
	if (model.getSelectedOperaDatas() == null || model.getSelectedOperaDatas().length <= 0) {
	    //ShowStatusBarMsgUtil.showErrorMsg("無法結算", "請選擇操作資料行!", getContext());
	    ExceptionUtils.wrappBusinessException("請選擇操作資料行!");
	    return;
	}
        if (UIDialog.ID_YES!=MessageDialog.showYesNoDlg(getEntranceUI(), null, ResHelper.getString("6017leave","06017leave0168")
/*@res "您确定要反结算当前所有数据吗？"*/))
        {
        	putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
            return;
        }

        //LeaveBalanceAppModel model = (LeaveBalanceAppModel)getModel();
        Object data = getHierachicalModel().getSelectedData();
        if(data==null)return;
        LeaveTypeCopyVO typeVO = data==null?null:(LeaveTypeCopyVO)data;
        // mod 假期计算单选和多选 tank 2020年3月17日 11:22:59
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
        //mod end 假期计算单选和多选 tank 2020年3月17日 11:22:59
        //LeaveBalanceVO[] vos = (LeaveBalanceVO[]) (CollectionUtils.isEmpty(model.getData())?null:model.getData().toArray(new LeaveBalanceVO[0]));
		UnSettlementResult result = NCLocator.getInstance().lookup(ILeaveBalanceManageMaintain.class).unSettlement(model.getContext().getPk_org(), typeVO.getPk_timeitem(),model.getYear(), model.getMonth(), vos);
		if(StringUtils.isNotEmpty(result.getErrMsg())){

			String msg = ResHelper.getString("6017leave","06017leave0170")
/*@res "下列数据反结算失败:{0}"*/;
			MessageDialog.showHintDlg(getContext().getEntranceUI(), null, MessageFormat.format(msg, "\r\n")+result.getErrMsg());
		}
		model.initModel(result.getUnSettledVOs());
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

}