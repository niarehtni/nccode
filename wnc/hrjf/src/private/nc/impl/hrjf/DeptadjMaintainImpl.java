package nc.impl.hrjf;

import nc.impl.pub.ace.AceDeptadjPubServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.itf.hrjf.IDeptadjMaintain;
import nc.vo.pub.BusinessException;


public class DeptadjMaintainImpl extends AceDeptadjPubServiceImpl
		implements IDeptadjMaintain {

	@Override
	public void delete(AggHRDeptAdjustVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggHRDeptAdjustVO[] insert(AggHRDeptAdjustVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggHRDeptAdjustVO[] update(AggHRDeptAdjustVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggHRDeptAdjustVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	

}
