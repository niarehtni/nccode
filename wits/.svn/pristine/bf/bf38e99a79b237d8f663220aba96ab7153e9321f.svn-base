package nc.bs.hrwa.projsalary.ace.bp;

import nc.bs.hrwa.projsalary.ace.rule.ClassitemUnionPjCheckRule;
import nc.bs.hrwa.projsalary.ace.rule.UpdatePaydataStateRule;
import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.wa.projsalary.AggProjSalaryVO;

/**
 * 修改保存的BP
 * 
 */
public class AceProjsalaryUpdateBP {

	public AggProjSalaryVO[] update(AggProjSalaryVO[] bills, AggProjSalaryVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggProjSalaryVO> bp = new UpdateBPTemplate<AggProjSalaryVO>(ProjsalaryPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	@SuppressWarnings("unchecked")
	private void addAfterRule(CompareAroundProcesser<AggProjSalaryVO> processer) {
		// TODO 后规则
		IRule<AggProjSalaryVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("PJSA");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);
		rule = new UpdatePaydataStateRule();
		processer.addAfterRule(rule);

	}

	@SuppressWarnings("unchecked")
	private void addBeforeRule(CompareAroundProcesser<AggProjSalaryVO> processer) {
		// TODO 前规则
		IRule<AggProjSalaryVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		nc.impl.pubapp.pattern.rule.ICompareRule<AggProjSalaryVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCbilltype("PJSA");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
		rule = new ClassitemUnionPjCheckRule();
		processer.addBeforeRule(rule);
	}

}
