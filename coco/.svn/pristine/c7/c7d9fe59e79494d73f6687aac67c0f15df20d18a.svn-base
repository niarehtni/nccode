package nc.bs.hrsms.hi.employ.state;

/**
 * 审核意见：不需要审核时隐藏
 * @author lihha
 *
 */
public class Auditopi_StateManager extends Psninfo_Base_StateManager{
	@Override
	protected State getStateByItem(String infoset, boolean needAudit, int data_status) {
		if(!needAudit)
			return State.HIDDEN;
		return State.VISIBLE;
	}
}
