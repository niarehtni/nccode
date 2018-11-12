package nc.bs.overtime.otleavebalance.bp;

import nc.bs.overtime.otleavebalance.plugin.bpplugin.OTLeaveBalancePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;

/**
 * �޸ı����BP
 * 
 */
public class OTLeaveBalanceUpdateBP {

    public AggOTLeaveBalanceVO[] update(AggOTLeaveBalanceVO[] bills, AggOTLeaveBalanceVO[] originBills) {
	// �����޸�ģ��
	UpdateBPTemplate<AggOTLeaveBalanceVO> bp = new UpdateBPTemplate<AggOTLeaveBalanceVO>(
		OTLeaveBalancePluginPoint.UPDATE);
	// ִ��ǰ����
	this.addBeforeRule(bp.getAroundProcesser());
	// ִ�к����
	this.addAfterRule(bp.getAroundProcesser());
	return bp.update(bills, originBills);
    }

    private void addAfterRule(CompareAroundProcesser<AggOTLeaveBalanceVO> processer) {
	// TODO �����
	IRule<AggOTLeaveBalanceVO> rule = null;

    }

    private void addBeforeRule(CompareAroundProcesser<AggOTLeaveBalanceVO> processer) {
	// TODO ǰ����
	IRule<AggOTLeaveBalanceVO> rule = null;
	rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
	processer.addBeforeRule(rule);
    }

}
