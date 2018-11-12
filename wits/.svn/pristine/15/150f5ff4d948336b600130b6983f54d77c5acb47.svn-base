package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.itf.hrwa.IProjsalaryMaintain;

public class N_PJSA_SAVE extends AbstractPfAction<AggProjSalaryVO> {

	protected CompareAroundProcesser<AggProjSalaryVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggProjSalaryVO> processor = new CompareAroundProcesser<AggProjSalaryVO>(
				ProjsalaryPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		@SuppressWarnings("unchecked")
		IRule<AggProjSalaryVO> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggProjSalaryVO[] processBP(Object userObj, AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) {
		IProjsalaryMaintain operator = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
		AggProjSalaryVO[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
