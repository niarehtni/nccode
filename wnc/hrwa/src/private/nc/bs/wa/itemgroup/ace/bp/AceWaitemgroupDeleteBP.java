package nc.bs.wa.itemgroup.ace.bp;

import nc.bs.wa.itemgroup.plugin.bpplugin.WaitemgroupPluginPoint;
import nc.vo.wa.itemgroup.AggItemGroupVO;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceWaitemgroupDeleteBP {

	public void delete(AggItemGroupVO[] bills) {

		DeleteBPTemplate<AggItemGroupVO> bp = new DeleteBPTemplate<AggItemGroupVO>(
				WaitemgroupPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggItemGroupVO> processer) {
		// TODO 前规则
		IRule<AggItemGroupVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggItemGroupVO> processer) {
		// TODO 后规则

	}
}
