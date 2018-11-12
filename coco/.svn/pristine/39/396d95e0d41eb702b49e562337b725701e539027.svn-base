package nc.bs.hrsms.hi.employ.state;

import nc.vo.hrss.hi.setalter.SetConsts;

/**
 * 提交、还原：不需要审核时隐藏，需要审核，状态为下正常时隐藏
 * @author lihha
 *
 */
public class CommitAndRevert_StateManager extends Psninfo_Base_StateManager{
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		if(SetConsts.NOSUBMIT==data_status)
			return State.VISIBLE ;
		if(!needAudit)
		    return State.HIDDEN;
		if(SetConsts.NOSUBMIT==data_status)
			return State.VISIBLE;
		return State.HIDDEN;
	}
}
