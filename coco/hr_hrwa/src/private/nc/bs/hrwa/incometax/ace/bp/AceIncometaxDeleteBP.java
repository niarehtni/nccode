package nc.bs.hrwa.incometax.ace.bp;

import nc.bs.hrwa.incometax.plugin.bpplugin.IncometaxPluginPoint;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceIncometaxDeleteBP {

	public void delete(AggIncomeTaxVO[] bills) {

		DeleteBPTemplate<AggIncomeTaxVO> bp = new DeleteBPTemplate<AggIncomeTaxVO>(
				IncometaxPluginPoint.DELETE);
		// 增加执行前规则
//		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
//		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggIncomeTaxVO> processer) {
		// TODO 前规则
		IRule<AggIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggIncomeTaxVO> processer) {
		// TODO 后规则

	}
}
