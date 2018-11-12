package nc.bs.hrta.leaveextrarest.ace.bp;

import nc.bs.hrta.leaveextrarest.plugin.bpplugin.LeaveextrarestPluginPoint;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceLeaveextrarestDeleteBP {

	public void delete(AggLeaveExtraRestVO[] bills) {

		DeleteBPTemplate<AggLeaveExtraRestVO> bp = new DeleteBPTemplate<AggLeaveExtraRestVO>(
				LeaveextrarestPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggLeaveExtraRestVO> processer) {
		// TODO 前规则
//		IRule<AggLeaveExtraRestVO> rule = null;
//		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
//		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggLeaveExtraRestVO> processer) {
		// TODO 后规则

	}
}
