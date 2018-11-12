package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.UnapproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.itf.hrwa.IProjsalaryMaintain;

public class N_PJSA_UNAPPROVE extends AbstractPfAction<AggProjSalaryVO> {

	@SuppressWarnings("unchecked")
	@Override
	protected CompareAroundProcesser<AggProjSalaryVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggProjSalaryVO> processor = new CompareAroundProcesser<AggProjSalaryVO>(
				ProjsalaryPluginPoint.UNAPPROVE);
		// TODO 在此处添加前后规则
		processor.addBeforeRule(new UnapproveStatusCheckRule());

		return processor;
	}

	@Override
	protected AggProjSalaryVO[] processBP(Object userObj, AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AggProjSalaryVO[] bills = null;
		try {
			IProjsalaryMaintain operator = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
			bills = operator.unapprove(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
