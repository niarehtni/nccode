package nc.bs.overtime.otleavebalance.bp;

import nc.bs.overtime.otleavebalance.plugin.bpplugin.OTLeaveBalancePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;

/**
 * 标准单据新增BP
 */
public class OTLeaveBalanceInsertBP {

    public AggOTLeaveBalanceVO[] insert(AggOTLeaveBalanceVO[] bills) {

	InsertBPTemplate<AggOTLeaveBalanceVO> bp = new InsertBPTemplate<AggOTLeaveBalanceVO>(
		OTLeaveBalancePluginPoint.INSERT);
	this.addBeforeRule(bp.getAroundProcesser());
	this.addAfterRule(bp.getAroundProcesser());
	return bp.insert(bills);

    }

    /**
     * 新增后规则
     * 
     * @param processor
     */
    private void addAfterRule(AroundProcesser<AggOTLeaveBalanceVO> processor) {
	// TODO 新增后规则
	IRule<AggOTLeaveBalanceVO> rule = null;
    }

    /**
     * 新增前规则
     * 
     * @param processor
     */
    private void addBeforeRule(AroundProcesser<AggOTLeaveBalanceVO> processer) {
	// TODO 新增前规则
	IRule<AggOTLeaveBalanceVO> rule = null;
	rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
	processer.addBeforeRule(rule);
    }
}
