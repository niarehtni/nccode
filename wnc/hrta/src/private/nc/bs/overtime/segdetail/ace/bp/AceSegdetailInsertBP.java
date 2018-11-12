package nc.bs.overtime.segdetail.ace.bp;

import nc.bs.overtime.segdetail.plugin.bpplugin.SegdetailPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.overtime.AggSegDetailVO;

/**
 * 标准单据新增BP
 */
public class AceSegdetailInsertBP {

	public AggSegDetailVO[] insert(AggSegDetailVO[] bills) {

		InsertBPTemplate<AggSegDetailVO> bp = new InsertBPTemplate<AggSegDetailVO>(
				SegdetailPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggSegDetailVO> processor) {
		// TODO 新增后规则
		IRule<AggSegDetailVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggSegDetailVO> processer) {
		// TODO 新增前规则
		IRule<AggSegDetailVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
