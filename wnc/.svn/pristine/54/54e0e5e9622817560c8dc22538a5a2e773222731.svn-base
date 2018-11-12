package nc.bs.hrwa.wadaysalary.ace.bp;

import nc.bs.hrwa.wadaysalary.plugin.bpplugin.WadaysalaryPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.wa.paydata.AggDaySalaryVO;

/**
 * 修改保存的BP
 * 
 */
public class AceWadaysalaryUpdateBP {

	public AggDaySalaryVO[] update(AggDaySalaryVO[] bills,
			AggDaySalaryVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggDaySalaryVO> bp = new UpdateBPTemplate<AggDaySalaryVO>(
				WadaysalaryPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggDaySalaryVO> processer) {
		// TODO 后规则
		IRule<AggDaySalaryVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggDaySalaryVO> processer) {
		// TODO 前规则
		IRule<AggDaySalaryVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
