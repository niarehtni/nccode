package nc.impl.hrta;

import nc.impl.pub.ace.AceLeaveplanPubServiceImpl;
import nc.vo.ta.leaveplan.AggLeavePlanVO;
import nc.itf.hrta.ILeaveplanMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;

public class LeaveplanMaintainImpl extends AceLeaveplanPubServiceImpl implements
		ILeaveplanMaintain {

	@Override
	public void delete(AggLeavePlanVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggLeavePlanVO[] insert(AggLeavePlanVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggLeavePlanVO[] update(AggLeavePlanVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggLeavePlanVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

}
