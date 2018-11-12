package nc.bs.overtime.otleavebalance.bp;

import nc.bs.overtime.otleavebalance.plugin.bpplugin.OTLeaveBalancePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;

/**
 * 修改保存的BP
 * 
 */
public class OTLeaveBalanceUpdateBP {

    public AggOTLeaveBalanceVO[] update(AggOTLeaveBalanceVO[] bills, AggOTLeaveBalanceVO[] originBills) {
	// 调用修改模板
	UpdateBPTemplate<AggOTLeaveBalanceVO> bp = new UpdateBPTemplate<AggOTLeaveBalanceVO>(
		OTLeaveBalancePluginPoint.UPDATE);
	// 执行前规则
	this.addBeforeRule(bp.getAroundProcesser());
	// 执行后规则
	this.addAfterRule(bp.getAroundProcesser());
	return bp.update(bills, originBills);
    }

    private void addAfterRule(CompareAroundProcesser<AggOTLeaveBalanceVO> processer) {
	// TODO 后规则
	IRule<AggOTLeaveBalanceVO> rule = null;

    }

    private void addBeforeRule(CompareAroundProcesser<AggOTLeaveBalanceVO> processer) {
	// TODO 前规则
	IRule<AggOTLeaveBalanceVO> rule = null;
	rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
	processer.addBeforeRule(rule);
    }

}
