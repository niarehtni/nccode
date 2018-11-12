package nc.bs.hrwa.sumincometax.ace.bp;

import nc.bs.hrwa.sumincometax.plugin.bpplugin.SumincometaxPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;

/**
 * 修改保存的BP
 * 
 */
public class AceSumincometaxUpdateBP {

	public AggSumIncomeTaxVO[] update(AggSumIncomeTaxVO[] bills,
			AggSumIncomeTaxVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggSumIncomeTaxVO> bp = new UpdateBPTemplate<AggSumIncomeTaxVO>(
				SumincometaxPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggSumIncomeTaxVO> processer) {
		// TODO 后规则
		IRule<AggSumIncomeTaxVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggSumIncomeTaxVO> processer) {
		// TODO 前规则
		IRule<AggSumIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
