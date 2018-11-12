package nc.bs.hrwa.incometax.ace.bp;

import nc.bs.hrwa.incometax.plugin.bpplugin.IncometaxPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;

/**
 * �޸ı����BP
 * 
 */
public class AceIncometaxUpdateBP {

	public AggIncomeTaxVO[] update(AggIncomeTaxVO[] bills,
			AggIncomeTaxVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggIncomeTaxVO> bp = new UpdateBPTemplate<AggIncomeTaxVO>(
				IncometaxPluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggIncomeTaxVO> processer) {
		// TODO �����
		IRule<AggIncomeTaxVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggIncomeTaxVO> processer) {
		// TODO ǰ����
		IRule<AggIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
