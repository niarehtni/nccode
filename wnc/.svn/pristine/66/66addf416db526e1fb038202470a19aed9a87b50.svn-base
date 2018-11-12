package nc.impl.ta.overtime;

import nc.impl.pub.OTLeaveBalancePubServiceImpl;
import nc.itf.ta.overtime.IOTLeaveBalanceMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;

public class OTLeaveBalanceMaintainImpl extends OTLeaveBalancePubServiceImpl implements IOTLeaveBalanceMaintain {

    @Override
    public void delete(AggOTLeaveBalanceVO[] vos) throws BusinessException {
	super.pubdeleteBills(vos);
    }

    @Override
    public AggOTLeaveBalanceVO[] insert(AggOTLeaveBalanceVO[] vos) throws BusinessException {
	return super.pubinsertBills(vos);
    }

    @Override
    public AggOTLeaveBalanceVO[] update(AggOTLeaveBalanceVO[] vos) throws BusinessException {
	return super.pubupdateBills(vos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AggOTLeaveBalanceVO[] query(IQueryScheme queryScheme) throws BusinessException {
	return super.pubquerybills(queryScheme);
    }

}
