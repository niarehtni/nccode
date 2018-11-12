package nc.bs.overtime.segdetail.ace.bp;

import nc.bs.overtime.segdetail.plugin.bpplugin.SegdetailPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.ta.overtime.AggSegDetailVO;

/**
 * �޸ı����BP
 * 
 */
public class AceSegdetailUpdateBP {

	public AggSegDetailVO[] update(AggSegDetailVO[] bills,
			AggSegDetailVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggSegDetailVO> bp = new UpdateBPTemplate<AggSegDetailVO>(
				SegdetailPluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggSegDetailVO> processer) {
		// TODO �����
		IRule<AggSegDetailVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggSegDetailVO> processer) {
		// TODO ǰ����
		IRule<AggSegDetailVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
