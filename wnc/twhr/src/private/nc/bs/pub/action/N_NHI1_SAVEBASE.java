package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.twhr.twhr_declaration.plugin.bpplugin.Twhr_declarationPluginPoint;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.itf.twhr.ITwhr_declarationMaintain;

public class N_NHI1_SAVEBASE extends AbstractPfAction<AggDeclarationVO> {

	@Override
	protected CompareAroundProcesser<AggDeclarationVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggDeclarationVO> processor = null;
		AggDeclarationVO[] clientFullVOs = (AggDeclarationVO[]) this.getVos();
		if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
				.getPrimaryKey())) {
			processor = new CompareAroundProcesser<AggDeclarationVO>(
					Twhr_declarationPluginPoint.SCRIPT_UPDATE);
		} else {
			processor = new CompareAroundProcesser<AggDeclarationVO>(
					Twhr_declarationPluginPoint.SCRIPT_INSERT);
		}
		// TODO 在此处添加前后规则
		IRule<AggDeclarationVO> rule = null;

		return processor;
	}

	@Override
	protected AggDeclarationVO[] processBP(Object userObj,
			AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills) {

		AggDeclarationVO[] bills = null;
		try {
			ITwhr_declarationMaintain operator = NCLocator.getInstance()
					.lookup(ITwhr_declarationMaintain.class);
			if (!StringUtil.isEmptyWithTrim(clientFullVOs[0].getParentVO()
					.getPrimaryKey())) {
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
