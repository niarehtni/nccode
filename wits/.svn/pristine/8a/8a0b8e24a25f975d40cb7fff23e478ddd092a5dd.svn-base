package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.itf.hrwa.IProjsalaryMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.projsalary.AggProjSalaryVO;

public class N_PJSA_APPROVE extends AbstractPfAction<AggProjSalaryVO> {

	public N_PJSA_APPROVE() {
		super();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected CompareAroundProcesser<AggProjSalaryVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggProjSalaryVO> processor = new CompareAroundProcesser<AggProjSalaryVO>(
				ProjsalaryPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggProjSalaryVO[] processBP(Object userObj, AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) {
		AggProjSalaryVO[] bills = null;
		IProjsalaryMaintain operator = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
