package nc.bs.hrwa.allocate.ace.bp;

import nc.bs.hrwa.allocate.plugin.bpplugin.AllocatePluginPoint;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;

/**
 * 标准单据删除BP
 */
public class AceAllocateDeleteBP {

	public void delete(AggAllocateOutVO[] bills) {

		DeleteBPTemplate<AggAllocateOutVO> bp = new DeleteBPTemplate<AggAllocateOutVO>(AllocatePluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	@SuppressWarnings("unchecked")
	private void addBeforeRule(AroundProcesser<AggAllocateOutVO> processer) {
		// TODO 前规则
		IRule<AggAllocateOutVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggAllocateOutVO> processer) {
		// TODO 后规则

	}
}
