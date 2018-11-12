package nc.bs.hrsms.hi.employ.state;

import nc.vo.hrss.hi.setalter.SetConsts;

/**
 * �ύ����ԭ������Ҫ���ʱ���أ���Ҫ��ˣ�״̬Ϊ������ʱ����
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
