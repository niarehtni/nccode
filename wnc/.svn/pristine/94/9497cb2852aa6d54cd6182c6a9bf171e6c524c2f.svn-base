package nc.bs.hrta.leaveplan.ace.bp;

import nc.bs.hrta.leaveplan.plugin.bpplugin.LeaveplanPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.leaveplan.AggLeavePlanVO;

/**
 * 修改保存的BP
 * 
 */
public class AceLeaveplanUpdateBP {

	public AggLeavePlanVO[] update(AggLeavePlanVO[] bills,
			AggLeavePlanVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggLeavePlanVO> bp = new UpdateBPTemplate<AggLeavePlanVO>(
				LeaveplanPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggLeavePlanVO> processer) {
		// TODO 后规则
		IRule<AggLeavePlanVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggLeavePlanVO> processer) {
		// TODO 前规则
//		IRule<AggLeavePlanVO> rule = null;
//		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
//		processer.addBeforeRule(rule);
	}

}
