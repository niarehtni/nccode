package nc.bs.overtime.otleavebalance.bp;

import nc.bs.overtime.otleavebalance.plugin.bpplugin.OTLeaveBalancePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;

/**
 * 标准单据删除BP
 */
public class OTLeaveBalanceDeleteBP {

    public void delete(AggOTLeaveBalanceVO[] bills) {

	DeleteBPTemplate<AggOTLeaveBalanceVO> bp = new DeleteBPTemplate<AggOTLeaveBalanceVO>(
		OTLeaveBalancePluginPoint.DELETE);
	// 增加执行前规则
	this.addBeforeRule(bp.getAroundProcesser());
	// 增加执行后业务规则
	this.addAfterRule(bp.getAroundProcesser());
	bp.delete(bills);
    }

    private void addBeforeRule(AroundProcesser<AggOTLeaveBalanceVO> processer) {
    }

    /**
     * 删除后业务规则
     * 
     * @param processer
     */
    private void addAfterRule(AroundProcesser<AggOTLeaveBalanceVO> processer) {
	// TODO 后规则

    }
}
