package nc.bs.wa.itemgroup.ace.bp;

import nc.bs.wa.itemgroup.plugin.bpplugin.WaitemgroupPluginPoint;
import nc.bs.wa.itemgroup.rule.SetDefaultValueRule;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.itemgroup.AggItemGroupVO;

/**
 * 修改保存的BP
 * 
 */
public class AceWaitemgroupUpdateBP {

	public AggItemGroupVO[] update(AggItemGroupVO[] bills, AggItemGroupVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggItemGroupVO> bp = new UpdateBPTemplate<AggItemGroupVO>(WaitemgroupPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggItemGroupVO> processer) {
		// TODO 后规则
		IRule<AggItemGroupVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggItemGroupVO> processer) {
		// TODO 前规则
		IRule<AggItemGroupVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);

		rule = new SetDefaultValueRule();
		processer.addBeforeRule(rule);
	}

}
