package nc.bs.overtime.segdetail.ace.bp;

import nc.bs.overtime.segdetail.plugin.bpplugin.SegdetailPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.overtime.AggSegDetailVO;

/**
 * 修改保存的BP
 * 
 */
public class AceSegdetailUpdateBP {

	public AggSegDetailVO[] update(AggSegDetailVO[] bills,
			AggSegDetailVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggSegDetailVO> bp = new UpdateBPTemplate<AggSegDetailVO>(
				SegdetailPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggSegDetailVO> processer) {
		// TODO 后规则
		IRule<AggSegDetailVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggSegDetailVO> processer) {
		// TODO 前规则
		IRule<AggSegDetailVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
