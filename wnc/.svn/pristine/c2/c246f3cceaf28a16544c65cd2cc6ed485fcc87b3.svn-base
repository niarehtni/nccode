package nc.bs.overtime.segrule.ace.bp;

import nc.bs.overtime.segrule.plugin.bpplugin.SegrulePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.overtime.AggSegRuleVO;

/**
 * 标准单据新增BP
 */
public class AceSegruleInsertBP {

	public AggSegRuleVO[] insert(AggSegRuleVO[] bills) {

		InsertBPTemplate<AggSegRuleVO> bp = new InsertBPTemplate<AggSegRuleVO>(
				SegrulePluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggSegRuleVO> processor) {
		// TODO 新增后规则
		IRule<AggSegRuleVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggSegRuleVO> processer) {
		// TODO 新增前规则
		IRule<AggSegRuleVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
