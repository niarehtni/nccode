package nc.bs.hrwa.projsalary.ace.bp;

import nc.bs.hrwa.projsalary.ace.rule.ClassitemUnionPjCheckRule;
import nc.bs.hrwa.projsalary.ace.rule.UpdatePaydataStateRule;
import nc.bs.hrwa.projsalary.plugin.bpplugin.ProjsalaryPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.wa.projsalary.AggProjSalaryVO;

/**
 * 标准单据新增BP
 */
public class AceProjsalaryInsertBP {

	public AggProjSalaryVO[] insert(AggProjSalaryVO[] bills) {

		InsertBPTemplate<AggProjSalaryVO> bp = new InsertBPTemplate<AggProjSalaryVO>(ProjsalaryPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	@SuppressWarnings("unchecked")
	private void addAfterRule(AroundProcesser<AggProjSalaryVO> processor) {
		// TODO 新增后规则
		IRule<AggProjSalaryVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("PJSA");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
		rule = new UpdatePaydataStateRule();
		processor.addAfterRule(rule);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	@SuppressWarnings("unchecked")
	private void addBeforeRule(AroundProcesser<AggProjSalaryVO> processer) {
		// TODO 新增前规则
		IRule<AggProjSalaryVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("PJSA");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
		rule = new ClassitemUnionPjCheckRule();
		processer.addBeforeRule(rule);
	}
}
