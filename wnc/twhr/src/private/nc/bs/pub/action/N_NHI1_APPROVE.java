package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.ApproveStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.twhr.twhr_declaration.plugin.bpplugin.Twhr_declarationPluginPoint;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.itf.twhr.ITwhr_declarationMaintain;

public class N_NHI1_APPROVE extends AbstractPfAction<AggDeclarationVO> {

	public N_NHI1_APPROVE() {
		super();
	}

	@Override
	protected CompareAroundProcesser<AggDeclarationVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggDeclarationVO> processor = new CompareAroundProcesser<AggDeclarationVO>(
				Twhr_declarationPluginPoint.APPROVE);
		processor.addBeforeRule(new ApproveStatusCheckRule());
		return processor;
	}

	@Override
	protected AggDeclarationVO[] processBP(Object userObj,
			AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills) {
		AggDeclarationVO[] bills = null;
		ITwhr_declarationMaintain operator = NCLocator.getInstance().lookup(
				ITwhr_declarationMaintain.class);
		try {
			bills = operator.approve(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
