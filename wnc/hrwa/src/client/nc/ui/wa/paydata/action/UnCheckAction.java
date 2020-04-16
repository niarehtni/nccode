package nc.ui.wa.paydata.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wabm.view.dialog.CheckTypeChooseDialog;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaState;

/**
 * 取消审核
 *
 * @author: zhangg
 * @date: 2009-12-1 上午09:11:54
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class UnCheckAction extends PayDataBaseAction {

	private static final long serialVersionUID = 1L;

	public UnCheckAction() {
		super();
		putValue(INCAction.CODE,IHRWAActionCode.Unapprove);
		setBtnName(ResHelper.getString("60130paydata","060130paydata0369")/*@res "取消审核"*/);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130paydata","060130paydata0369")/*@res "取消审核"*/+"(Ctrl+Alt+M)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK+Event.ALT_MASK));
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		CheckTypeChooseDialog chooseDialog = new CheckTypeChooseDialog(getParentContainer(),false);
		chooseDialog.showModal();
		if (chooseDialog.getResult() != UIDialog.ID_OK) {
			putValue(this.MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
			return;
		}
		
		boolean rangeAll = chooseDialog.getValue().booleanValue();
		if (!rangeAll) {
			if (getPaydataModel().getData() == null || getPaydataModel().getData().size() < 1) {
				throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0339")/*@res "您没有选择人员！"*/);// "您没有选择人员！"
			}
		}
		//校验离职发薪数据
		PayfileVO[] vos = WADelegator.getPaydataQuery()
				.getInPayLeavePsn(getWaLoginVO(),
						getPaydataModel().getWhereCondition(), rangeAll);
		if (vos != null
				&& MessageDialog.showYesNoDlg(getParentContainer(), ResHelper.getString("60130payfile","060130payfile0245")/*@res "提示"*/,
						getPsnNamesMsg(vos)) == UIDialog.ID_NO) {
			return;
		}
		getPaydataManager().onUnCheck(rangeAll);
		putValue(this.MESSAGE_AFTER_ACTION, ResHelper.getString("60130paydata","060130paydata0523")/*@res "取消审核成功。"*/);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean isActionEnable() {
		if (super.isActionEnable()) {
			//如果界面没有数据或者没有已计算数据的话，审核不可用
			List<Object> datas =getPaydataModel().getData();
			if ( datas== null || datas.size() < 1) {
				return false;
			}
			for(Object obj:datas){
				PayfileVO vo = (PayfileVO)obj;
				if(vo.getCheckflag().equals(UFBoolean.TRUE)){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public String getPsnNamesMsg(PayfileVO[] vos){
		StringBuffer sb = new StringBuffer();
		for(PayfileVO vo:vos){
			sb.append("["+vo.getPsncode()+":"+vo.getPsnname()+"]");
		}
		sb.append(ResHelper.getString("60130paydata","060130paydata0533")/*@res "已被加入离职发薪，取消审核后将从离职发薪删除，是否继续？"*/);
		return sb.toString();
	}

	/**
	 * @author zhangg on 2009-12-1
	 * @see nc.ui.wa.paydata.action.PayDataBaseAction#getEnableStateSet()
	 */
	@Override
	public Set<WaState> getEnableStateSet() {
		waStateSet = new HashSet<WaState>();
		waStateSet.add(WaState.CLASS_CHECKED_WITHOUT_PAY);
		waStateSet.add(WaState.CLASS_PART_CHECKED);
		waStateSet.add(WaState.CLASS_WITHOUT_PAY);
		return waStateSet;

	}

}