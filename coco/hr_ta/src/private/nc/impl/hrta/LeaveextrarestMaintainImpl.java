package nc.impl.hrta;

import nc.impl.pub.ace.AceLeaveextrarestPubServiceImpl;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.itf.hrta.ILeaveextrarestMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;

public class LeaveextrarestMaintainImpl extends AceLeaveextrarestPubServiceImpl implements
		ILeaveextrarestMaintain {

	@Override
	public void delete(AggLeaveExtraRestVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggLeaveExtraRestVO[] insert(AggLeaveExtraRestVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggLeaveExtraRestVO[] update(AggLeaveExtraRestVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggLeaveExtraRestVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

}
