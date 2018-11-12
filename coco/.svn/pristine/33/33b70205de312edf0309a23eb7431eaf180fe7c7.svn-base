package nc.bs.hrsms.hi.employ.state;

import nc.vo.hrss.hi.setalter.SetConsts;
/**
 * ¼ÌÐøÐÞ¸Ä
 * @author lihha
 *
 */
public class ContinueUpd_StateManager extends Psninfo_Base_StateManager{
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		if(!needAudit)
		    return State.HIDDEN;
		if(SetConsts.AUDITNOPASS==data_status)
			return State.VISIBLE;
		return State.HIDDEN;
	}
}
