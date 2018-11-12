package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UncommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hrwa.allocate.plugin.bpplugin.AllocatePluginPoint;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.itf.hrwa.IAllocateMaintain;

public class N_ALLO_UNSAVEBILL extends AbstractPfAction<AggAllocateOutVO> {

	@SuppressWarnings("unchecked")
	@Override
	protected CompareAroundProcesser<AggAllocateOutVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggAllocateOutVO> processor = new CompareAroundProcesser<AggAllocateOutVO>(
				AllocatePluginPoint.UNSEND_APPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UncommitStatusCheckRule());

		return processor;
	}

	@Override
	protected AggAllocateOutVO[] processBP(Object userObj, AggAllocateOutVO[] clientFullVOs,
			AggAllocateOutVO[] originBills) {
		IAllocateMaintain operator = NCLocator.getInstance().lookup(IAllocateMaintain.class);
		AggAllocateOutVO[] bills = null;
		try {
			bills = operator.unsave(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
