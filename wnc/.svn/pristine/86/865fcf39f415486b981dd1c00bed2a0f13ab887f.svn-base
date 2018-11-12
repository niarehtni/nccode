package nc.bs.hrwa.wadaysalary.ace.bp;

import nc.bs.hrwa.wadaysalary.plugin.bpplugin.WadaysalaryPluginPoint;
import nc.vo.wa.paydata.AggDaySalaryVO;

import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceWadaysalaryDeleteBP {

	public void delete(AggDaySalaryVO[] bills) {

		DeleteBPTemplate<AggDaySalaryVO> bp = new DeleteBPTemplate<AggDaySalaryVO>(
				WadaysalaryPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggDaySalaryVO> processer) {
		// TODO 前规则
		IRule<AggDaySalaryVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggDaySalaryVO> processer) {
		// TODO 后规则

	}
}
