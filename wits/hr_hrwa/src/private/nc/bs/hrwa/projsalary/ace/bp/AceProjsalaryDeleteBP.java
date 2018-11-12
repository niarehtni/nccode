package nc.bs.hrwa.projsalary.ace.bp;

import nc.bs.hrwa.projsalary.ace.rule.UpdatePaydataStateRule;
import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;

/**
 * 标准单据删除BP
 */
public class AceProjsalaryDeleteBP {

	public void delete(AggProjSalaryVO[] bills) {

		DeleteBPTemplate<AggProjSalaryVO> bp = new DeleteBPTemplate<AggProjSalaryVO>(ProjsalaryPluginPoint.DELETE);
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	@SuppressWarnings("unchecked")
	private void addBeforeRule(AroundProcesser<AggProjSalaryVO> processer) {
		// TODO 前规则
		IRule<AggProjSalaryVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggProjSalaryVO> processer) {
		// TODO 后规则
		IRule<AggProjSalaryVO> rule = null;
		rule = new UpdatePaydataStateRule();
		processer.addAfterRule(rule);
	}
}
