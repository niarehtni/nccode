package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.itf.hrwa.IProjsalaryMaintain;

public class N_PJSA_SAVEBASE extends AbstractPfAction<AggProjSalaryVO> {

	@Override
	protected CompareAroundProcesser<AggProjSalaryVO> getCompareAroundProcesserWithRules(Object userObj) {
		CompareAroundProcesser<AggProjSalaryVO> processor = null;
		AggProjSalaryVO[] clientFullVOs = (AggProjSalaryVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO().getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggProjSalaryVO>(ProjsalaryPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggProjSalaryVO>(ProjsalaryPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		@SuppressWarnings("unused")
		IRule<AggProjSalaryVO> rule = null;

		return processor;
	}

	@Override
	protected AggProjSalaryVO[] processBP(Object userObj, AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) {

		AggProjSalaryVO[] bills = null;
		try {
			IProjsalaryMaintain operator = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
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
