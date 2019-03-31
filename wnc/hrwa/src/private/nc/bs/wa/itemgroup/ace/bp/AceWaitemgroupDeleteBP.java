package nc.bs.wa.itemgroup.ace.bp;

import nc.bs.wa.itemgroup.plugin.bpplugin.WaitemgroupPluginPoint;
import nc.vo.wa.itemgroup.AggItemGroupVO;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * ��׼����ɾ��BP
 */
public class AceWaitemgroupDeleteBP {

	public void delete(AggItemGroupVO[] bills) {

		DeleteBPTemplate<AggItemGroupVO> bp = new DeleteBPTemplate<AggItemGroupVO>(
				WaitemgroupPluginPoint.DELETE);
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggItemGroupVO> processer) {
		// TODO ǰ����
		IRule<AggItemGroupVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggItemGroupVO> processer) {
		// TODO �����

	}
}
