package nc.bs.hrwa.incometax.ace.bp;

import nc.bs.hrwa.incometax.plugin.bpplugin.IncometaxPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;

/**
 * 修改保存的BP
 * 
 */
public class AceIncometaxUpdateBP {

	public AggIncomeTaxVO[] update(AggIncomeTaxVO[] bills,
			AggIncomeTaxVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggIncomeTaxVO> bp = new UpdateBPTemplate<AggIncomeTaxVO>(
				IncometaxPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggIncomeTaxVO> processer) {
		// TODO 后规则
		IRule<AggIncomeTaxVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggIncomeTaxVO> processer) {
		// TODO 前规则
		IRule<AggIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
