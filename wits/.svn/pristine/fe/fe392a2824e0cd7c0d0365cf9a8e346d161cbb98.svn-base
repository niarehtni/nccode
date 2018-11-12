package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hrwa.allocate.plugin.bpplugin.AllocatePluginPoint;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.itf.hrwa.IAllocateMaintain;

public class N_ALLO_SAVEBASE extends AbstractPfAction<AggAllocateOutVO> {

	@Override
	protected CompareAroundProcesser<AggAllocateOutVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggAllocateOutVO> processor = null;
		AggAllocateOutVO[] clientFullVOs = (AggAllocateOutVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO().getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggAllocateOutVO>(AllocatePluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggAllocateOutVO>(AllocatePluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		@SuppressWarnings("unused")
		IRule<AggAllocateOutVO> rule = null;

		return processor;
	}

	@Override
	protected AggAllocateOutVO[] processBP(Object userObj, AggAllocateOutVO[] clientFullVOs,
			AggAllocateOutVO[] originBills) {

		AggAllocateOutVO[] bills = null;
		try {
			IAllocateMaintain operator = NCLocator.getInstance().lookup(IAllocateMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO().getPrimaryKey())) {
				bills = operator.update(clientFullVOs, originBills);
			} else {
				bills = operator.insert(clientFullVOs, originBills);
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}
}
