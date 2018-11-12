package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.allocate.plugin.bpplugin.AllocatePluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hrwa.IAllocateMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.allocate.AggAllocateOutVO;

public class N_ALLO_APPROVE extends AbstractPfAction<AggAllocateOutVO> {

	public N_ALLO_APPROVE() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected CompareAroundProcesser<AggAllocateOutVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggAllocateOutVO> processor = new CompareAroundProcesser<AggAllocateOutVO>(
				AllocatePluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggAllocateOutVO[] processBP(Object userObj, AggAllocateOutVO[] clientFullVOs,
			AggAllocateOutVO[] originBills) {
		AggAllocateOutVO[] bills = null;
		IAllocateMaintain operator = NCLocator.getInstance().lookup(IAllocateMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
