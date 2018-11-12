package nc.bs.twhr.nhicalc.ace.bp;

import nc.bs.twhr.nhicalc.plugin.bpplugin.NhicalcPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;

/**
 * ��׼��������BP
 */
public class AceNhicalcInsertBP {

	public AggNhiCalcVO[] insert(AggNhiCalcVO[] bills) {

		InsertBPTemplate<AggNhiCalcVO> bp = new InsertBPTemplate<AggNhiCalcVO>(
				NhicalcPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggNhiCalcVO> processor) {
		// TODO ���������
		IRule<AggNhiCalcVO> rule = null;
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggNhiCalcVO> processer) {
		// TODO ����ǰ����
		IRule<AggNhiCalcVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
