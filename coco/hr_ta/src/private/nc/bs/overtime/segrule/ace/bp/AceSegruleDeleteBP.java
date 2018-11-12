package nc.bs.overtime.segrule.ace.bp;

import nc.bs.overtime.segrule.plugin.bpplugin.SegrulePluginPoint;
import nc.bs.overtime.segrule.rule.RefCheckRule;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.ta.overtime.AggSegRuleVO;

/**
 * 标准单据删除BP
 */
public class AceSegruleDeleteBP {

	public void delete(AggSegRuleVO[] bills) {

		DeleteBPTemplate<AggSegRuleVO> bp = new DeleteBPTemplate<AggSegRuleVO>(SegrulePluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggSegRuleVO> processer) {
		processer.addBeforeRule(new RefCheckRule());
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggSegRuleVO> processer) {
		// TODO 后规则

	}
}
