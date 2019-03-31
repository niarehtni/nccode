package nc.bs.wa.itemgroup.ace.bp;

import nc.bs.wa.itemgroup.plugin.bpplugin.WaitemgroupPluginPoint;
import nc.bs.wa.itemgroup.rule.SetDefaultValueRule;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.wa.itemgroup.AggItemGroupVO;

/**
 * ��׼��������BP
 */
public class AceWaitemgroupInsertBP {

	public AggItemGroupVO[] insert(AggItemGroupVO[] bills) {

		InsertBPTemplate<AggItemGroupVO> bp = new InsertBPTemplate<AggItemGroupVO>(WaitemgroupPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggItemGroupVO> processor) {
		// TODO ���������
		IRule<AggItemGroupVO> rule = null;
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggItemGroupVO> processer) {
		// TODO ����ǰ����
		IRule<AggItemGroupVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);

		rule = new SetDefaultValueRule();
		processer.addBeforeRule(rule);

	}
}
