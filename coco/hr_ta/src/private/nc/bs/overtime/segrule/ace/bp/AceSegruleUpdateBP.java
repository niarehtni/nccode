package nc.bs.overtime.segrule.ace.bp;

import nc.bs.overtime.segrule.plugin.bpplugin.SegrulePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.overtime.AggSegRuleVO;

/**
 * �޸ı����BP
 * 
 */
public class AceSegruleUpdateBP {

	public AggSegRuleVO[] update(AggSegRuleVO[] bills,
			AggSegRuleVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggSegRuleVO> bp = new UpdateBPTemplate<AggSegRuleVO>(
				SegrulePluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggSegRuleVO> processer) {
		// TODO �����
		IRule<AggSegRuleVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggSegRuleVO> processer) {
		// TODO ǰ����
		IRule<AggSegRuleVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
