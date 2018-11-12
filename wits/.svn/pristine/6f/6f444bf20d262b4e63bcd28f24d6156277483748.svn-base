package nc.bs.twhr.rangetable.ace.bp;

import nc.bs.twhr.rangetable.plugin.bpplugin.RangetablePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.ICompareRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.twhr.rangetable.RangeTableAggVO;

/**
 * 修改保存的BP
 * 
 */
public class AceRangetableUpdateBP {

	public RangeTableAggVO[] update(RangeTableAggVO[] bills,
			RangeTableAggVO[] originBills) {

		// 调用修改模板
		UpdateBPTemplate<RangeTableAggVO> bp = new UpdateBPTemplate<RangeTableAggVO>(
				RangetablePluginPoint.UPDATE);

		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addBeforeRule(CompareAroundProcesser<RangeTableAggVO> processer) {
		IRule<RangeTableAggVO> rule = null;
		rule = new nc.bs.twhr.rangetable.rule.SetDefaultValueRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
		ICompareRule<RangeTableAggVO> ruleCom = new nc.bs.pubapp.pub.rule.UpdateBillCodeRule();
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCbilltype("TWRT");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setCodeItem("docno");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.UpdateBillCodeRule) ruleCom)
				.setOrgItem("pk_org");
		processer.addBeforeRule(ruleCom);
		rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.twhr.rangetable.rule.CheckDateDuplicated();
		processer.addBeforeRule(rule);
	}

}
