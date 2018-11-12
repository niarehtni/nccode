package nc.bs.hrta.leaveextrarest.ace.bp;

import nc.bs.hrta.leaveextrarest.plugin.bpplugin.LeaveextrarestPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;

/**
 * 修改保存的BP
 * 
 */
public class AceLeaveextrarestUpdateBP {

	public AggLeaveExtraRestVO[] update(AggLeaveExtraRestVO[] bills,
			AggLeaveExtraRestVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggLeaveExtraRestVO> bp = new UpdateBPTemplate<AggLeaveExtraRestVO>(
				LeaveextrarestPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggLeaveExtraRestVO> processer) {
		// TODO 后规则
		IRule<AggLeaveExtraRestVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggLeaveExtraRestVO> processer) {
		// TODO 前规则
		IRule<AggLeaveExtraRestVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
