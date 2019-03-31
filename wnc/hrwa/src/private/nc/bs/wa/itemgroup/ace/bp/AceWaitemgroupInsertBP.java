package nc.bs.wa.itemgroup.ace.bp;

import nc.bs.wa.itemgroup.plugin.bpplugin.WaitemgroupPluginPoint;
import nc.bs.wa.itemgroup.rule.SetDefaultValueRule;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.wa.itemgroup.AggItemGroupVO;

/**
 * 标准单据新增BP
 */
public class AceWaitemgroupInsertBP {

	public AggItemGroupVO[] insert(AggItemGroupVO[] bills) {

		InsertBPTemplate<AggItemGroupVO> bp = new InsertBPTemplate<AggItemGroupVO>(WaitemgroupPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggItemGroupVO> processor) {
		// TODO 新增后规则
		IRule<AggItemGroupVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggItemGroupVO> processer) {
		// TODO 新增前规则
		IRule<AggItemGroupVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);

		rule = new SetDefaultValueRule();
		processer.addBeforeRule(rule);

	}
}
