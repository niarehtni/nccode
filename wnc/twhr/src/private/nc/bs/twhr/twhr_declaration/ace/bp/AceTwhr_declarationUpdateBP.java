package nc.bs.twhr.twhr_declaration.ace.bp;

import nc.bs.twhr.twhr_declaration.plugin.bpplugin.Twhr_declarationPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;

/**
 * 修改保存的BP
 * 
 */
public class AceTwhr_declarationUpdateBP {

	public AggDeclarationVO[] update(AggDeclarationVO[] bills, AggDeclarationVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggDeclarationVO> bp = new UpdateBPTemplate<AggDeclarationVO>(
				Twhr_declarationPluginPoint.UPDATE);
		// // 执行前规则
		// this.addBeforeRule(bp.getAroundProcesser());
		// // 执行后规则
		// this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggDeclarationVO> processer) {
		// TODO 后规则
		IRule<AggDeclarationVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillCodeCheckRule();
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCbilltype("NHI1");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setCodeItem("billno");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.BillCodeCheckRule) rule).setOrgItem("pk_org");
		processer.addAfterRule(rule);

	}

	private void addBeforeRule(CompareAroundProcesser<AggDeclarationVO> processer) {
		// TODO 前规则
		IRule<AggDeclarationVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		nc.impl.pubapp.pattern.rule.ICompareRule<AggDeclarationVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCbilltype("NHI1");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setCodeItem("billno");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom).setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
	}

}
