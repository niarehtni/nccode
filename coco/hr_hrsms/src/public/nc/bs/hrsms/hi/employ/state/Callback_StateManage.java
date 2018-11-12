package nc.bs.hrsms.hi.employ.state;

import nc.vo.hrss.hi.setalter.SetConsts;

/**
 * 收回按钮，将已提交且尚未审核的单据收回为未提交状态
 * @author lihha
 *
 */
public class Callback_StateManage extends Psninfo_Base_StateManager{
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		if(!needAudit)
		    return State.HIDDEN;
		if(SetConsts.NOAUDIT==data_status)
			return State.VISIBLE;
		return State.HIDDEN;
	}
}
