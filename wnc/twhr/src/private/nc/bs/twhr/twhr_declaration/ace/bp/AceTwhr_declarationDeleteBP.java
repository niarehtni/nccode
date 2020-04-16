package nc.bs.twhr.twhr_declaration.ace.bp;

import nc.bs.twhr.twhr_declaration.plugin.bpplugin.Twhr_declarationPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;

/**
 * 标准单据删除BP
 */
public class AceTwhr_declarationDeleteBP {

	public void delete(AggDeclarationVO[] bills) {

		DeleteBPTemplate<AggDeclarationVO> bp = new DeleteBPTemplate<AggDeclarationVO>(
				Twhr_declarationPluginPoint.DELETE);
		// // 增加执行前规则
		// this.addBeforeRule(bp.getAroundProcesser());
		// // 增加执行后业务规则
		// this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggDeclarationVO> processer) {
		// TODO 前规则
		IRule<AggDeclarationVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggDeclarationVO> processer) {
		// TODO 后规则

	}
}
