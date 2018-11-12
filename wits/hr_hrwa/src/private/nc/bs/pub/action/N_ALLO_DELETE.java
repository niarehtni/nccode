package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.hrwa.allocate.plugin.bpplugin.AllocatePluginPoint;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.itf.hrwa.IAllocateMaintain;

public class N_ALLO_DELETE extends AbstractPfAction<AggAllocateOutVO> {

	@Override
	protected CompareAroundProcesser<AggAllocateOutVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggAllocateOutVO> processor = new CompareAroundProcesser<AggAllocateOutVO>(
				AllocatePluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggAllocateOutVO[] processBP(Object userObj, AggAllocateOutVO[] clientFullVOs,
			AggAllocateOutVO[] originBills) {
		IAllocateMaintain operator = NCLocator.getInstance().lookup(IAllocateMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
