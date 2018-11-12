package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.itf.hrwa.IProjsalaryMaintain;

public class N_PJSA_DELETE extends AbstractPfAction<AggProjSalaryVO> {

	@Override
	protected CompareAroundProcesser<AggProjSalaryVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggProjSalaryVO> processor = new CompareAroundProcesser<AggProjSalaryVO>(
				ProjsalaryPluginPoint.SCRIPT_DELETE);
		// TODO 在此处添加前后规则
		return processor;
	}

	@Override
	protected AggProjSalaryVO[] processBP(Object userObj, AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) {
		IProjsalaryMaintain operator = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
		try {
			operator.delete(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return clientFullVOs;
	}

}
