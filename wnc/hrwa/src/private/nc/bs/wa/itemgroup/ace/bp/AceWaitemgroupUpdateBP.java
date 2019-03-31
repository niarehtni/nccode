package nc.bs.wa.itemgroup.ace.bp;

import nc.bs.wa.itemgroup.plugin.bpplugin.WaitemgroupPluginPoint;
import nc.bs.wa.itemgroup.rule.SetDefaultValueRule;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.itemgroup.AggItemGroupVO;

/**
 * �޸ı����BP
 * 
 */
public class AceWaitemgroupUpdateBP {

	public AggItemGroupVO[] update(AggItemGroupVO[] bills, AggItemGroupVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggItemGroupVO> bp = new UpdateBPTemplate<AggItemGroupVO>(WaitemgroupPluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggItemGroupVO> processer) {
		// TODO �����
		IRule<AggItemGroupVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggItemGroupVO> processer) {
		// TODO ǰ����
		IRule<AggItemGroupVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);

		rule = new SetDefaultValueRule();
		processer.addBeforeRule(rule);
	}

}
