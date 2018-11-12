package nc.bs.overtime.segdetail.ace.bp;

import nc.bs.overtime.segdetail.plugin.bpplugin.SegdetailPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.ta.overtime.AggSegDetailVO;

/**
 * 标准单据删除BP
 */
public class AceSegdetailDeleteBP {

	public void delete(AggSegDetailVO[] bills) {

		DeleteBPTemplate<AggSegDetailVO> bp = new DeleteBPTemplate<AggSegDetailVO>(SegdetailPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggSegDetailVO> processer) {
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggSegDetailVO> processer) {
		// TODO 后规则

	}
}
