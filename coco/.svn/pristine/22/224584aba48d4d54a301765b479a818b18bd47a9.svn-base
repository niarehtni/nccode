package nc.bs.hrta.leaveplan.ace.bp;

import nc.bs.hrta.leaveplan.plugin.bpplugin.LeaveplanPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.leaveplan.AggLeavePlanVO;

/**
 * 标准单据新增BP
 */
public class AceLeaveplanInsertBP {

	public AggLeavePlanVO[] insert(AggLeavePlanVO[] bills) {

		InsertBPTemplate<AggLeavePlanVO> bp = new InsertBPTemplate<AggLeavePlanVO>(
				LeaveplanPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggLeavePlanVO> processor) {
		// TODO 新增后规则
		IRule<AggLeavePlanVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggLeavePlanVO> processer) {
		// TODO 新增前规则
//		IRule<AggLeavePlanVO> rule = null;
//		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
//		processer.addBeforeRule(rule);
	}
}
