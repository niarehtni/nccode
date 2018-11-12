package nc.impl.ta.overtime;

import nc.impl.pub.ace.AceSegrulePubServiceImpl;
import nc.vo.ta.overtime.AggSegRuleVO;
import nc.itf.ta.overtime.ISegruleMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;

public class SegruleMaintainImpl extends AceSegrulePubServiceImpl implements
		ISegruleMaintain {

	@Override
	public void delete(AggSegRuleVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggSegRuleVO[] insert(AggSegRuleVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggSegRuleVO[] update(AggSegRuleVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggSegRuleVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

}
