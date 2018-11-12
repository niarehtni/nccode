package nc.bs.hrta.leaveextrarest.ace.bp;

import nc.bs.hrta.leaveextrarest.plugin.bpplugin.LeaveextrarestPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;

/**
 * 标准单据新增BP
 */
public class AceLeaveextrarestInsertBP {

	public AggLeaveExtraRestVO[] insert(AggLeaveExtraRestVO[] bills) {

		InsertBPTemplate<AggLeaveExtraRestVO> bp = new InsertBPTemplate<AggLeaveExtraRestVO>(
				LeaveextrarestPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggLeaveExtraRestVO> processor) {
		// TODO 新增后规则
		IRule<AggLeaveExtraRestVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggLeaveExtraRestVO> processer) {
		// TODO 新增前规则
		IRule<AggLeaveExtraRestVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
