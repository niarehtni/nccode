package nc.bs.twhr.nhicalc.ace.bp;

import nc.bs.twhr.nhicalc.plugin.bpplugin.NhicalcPluginPoint;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceNhicalcDeleteBP {

	public void delete(AggNhiCalcVO[] bills) {

		DeleteBPTemplate<AggNhiCalcVO> bp = new DeleteBPTemplate<AggNhiCalcVO>(
				NhicalcPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggNhiCalcVO> processer) {
		// TODO 前规则
		IRule<AggNhiCalcVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggNhiCalcVO> processer) {
		// TODO 后规则

	}
}
