package nc.bs.hrwa.incometax.ace.bp;

import nc.bs.hrwa.incometax.plugin.bpplugin.IncometaxPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;

/**
 * ��׼��������BP
 */
public class AceIncometaxInsertBP {

	public AggIncomeTaxVO[] insert(AggIncomeTaxVO[] bills) {

		InsertBPTemplate<AggIncomeTaxVO> bp = new InsertBPTemplate<AggIncomeTaxVO>(
				IncometaxPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggIncomeTaxVO> processor) {
		// TODO ���������
		IRule<AggIncomeTaxVO> rule = null;
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggIncomeTaxVO> processer) {
		// TODO ����ǰ����
		IRule<AggIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
