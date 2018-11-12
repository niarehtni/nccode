package nc.bs.hrwa.allocate.ace.bp;

import nc.bs.hrwa.allocate.plugin.bpplugin.AllocatePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.wa.allocate.AggAllocateOutVO;

/**
 * �޸ı����BP
 * 
 */
public class AceAllocateUpdateBP {

	public AggAllocateOutVO[] update(AggAllocateOutVO[] bills,
			AggAllocateOutVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggAllocateOutVO> bp = new UpdateBPTemplate<AggAllocateOutVO>(
				AllocatePluginPoint.UPDATE);
		// // ִ��ǰ����
		// this.addBeforeRule(bp.getAroundProcesser());
		// // ִ�к����
		// this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	@SuppressWarnings("unchecked")
	private void addAfterRule(CompareAroundProcesser<AggAllocateOutVO> processer) {
		// TODO �����
		IRule<AggAllocateOutVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("ALLO");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);

	}

	@SuppressWarnings("unchecked")
	private void addBeforeRule(
			CompareAroundProcesser<AggAllocateOutVO> processer) {
		// TODO ǰ����
		IRule<AggAllocateOutVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		nc.impl.pubapp.pattern.rule.ICompareRule<AggAllocateOutVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCbilltype("ALLO");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCodeItem("vbillcode");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
	}

}