package nc.bs.hrwa.incometax.ace.bp;

import nc.bs.hrwa.incometax.plugin.bpplugin.IncometaxPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;

/**
 * 标准单据新增BP
 */
public class AceIncometaxInsertBP {

	public AggIncomeTaxVO[] insert(AggIncomeTaxVO[] bills) {

		InsertBPTemplate<AggIncomeTaxVO> bp = new InsertBPTemplate<AggIncomeTaxVO>(
				IncometaxPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggIncomeTaxVO> processor) {
		// TODO 新增后规则
		IRule<AggIncomeTaxVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggIncomeTaxVO> processer) {
		// TODO 新增前规则
		IRule<AggIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
