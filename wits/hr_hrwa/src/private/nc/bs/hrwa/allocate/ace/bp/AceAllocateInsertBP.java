package nc.bs.hrwa.allocate.ace.bp;

import nc.bs.hrwa.allocate.plugin.bpplugin.AllocatePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.wa.allocate.AggAllocateOutVO;

/**
 * 标准单据新增BP
 */
public class AceAllocateInsertBP {

	public AggAllocateOutVO[] insert(AggAllocateOutVO[] bills) {

		InsertBPTemplate<AggAllocateOutVO> bp = new InsertBPTemplate<AggAllocateOutVO>(
				AllocatePluginPoint.INSERT);
		// this.addBeforeRule(bp.getAroundProcesser());
		// this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	@SuppressWarnings("unchecked")
	private void addAfterRule(AroundProcesser<AggAllocateOutVO> processor) {
		// TODO 新增后规则
		IRule<AggAllocateOutVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("ALLO");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processor.addAfterRule(rule);
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	@SuppressWarnings("unchecked")
	private void addBeforeRule(AroundProcesser<AggAllocateOutVO> processer) {
		// TODO 新增前规则
		IRule<AggAllocateOutVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("ALLO");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
	}
}
