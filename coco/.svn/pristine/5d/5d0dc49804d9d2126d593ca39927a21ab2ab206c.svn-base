package nc.bs.twhr.nhicalc.ace.bp;

import nc.bs.twhr.nhicalc.plugin.bpplugin.NhicalcPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;

/**
 * 修改保存的BP
 * 
 */
public class AceNhicalcUpdateBP {

	public AggNhiCalcVO[] update(AggNhiCalcVO[] bills,
			AggNhiCalcVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggNhiCalcVO> bp = new UpdateBPTemplate<AggNhiCalcVO>(
				NhicalcPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggNhiCalcVO> processer) {
		// TODO 后规则
		IRule<AggNhiCalcVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggNhiCalcVO> processer) {
		// TODO 前规则
		IRule<AggNhiCalcVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
