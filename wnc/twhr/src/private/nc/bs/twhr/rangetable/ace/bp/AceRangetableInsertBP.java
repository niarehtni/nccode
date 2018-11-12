package nc.bs.twhr.rangetable.ace.bp;

import nc.bs.twhr.rangetable.plugin.bpplugin.RangetablePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.twhr.rangetable.RangeTableAggVO;

/**
 * 标准单据新增BP
 */
public class AceRangetableInsertBP {

	public RangeTableAggVO[] insert(RangeTableAggVO[] bills) {

		InsertBPTemplate<RangeTableAggVO> bp = new InsertBPTemplate<RangeTableAggVO>(
				RangetablePluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<RangeTableAggVO> processer) {
		IRule<RangeTableAggVO> rule = null;
		rule = new nc.bs.twhr.rangetable.rule.SetDefaultAddValueRule();
		processer.addBeforeRule(rule);
		//start 不再设置默认的组织和集团信息 Ares.Tank 2018-7-25 10:36:31
		//rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		//processer.addBeforeRule(rule);
		//end 不再设置组织和集团信息 Ares 2018-7-25 10:36:28
		rule = new nc.bs.pubapp.pub.rule.CreateBillCodeRule();
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCbilltype("TWRT");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setCodeItem("docno");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setGroupItem("pk_group");
		((nc.bs.pubapp.pub.rule.CreateBillCodeRule) rule).setOrgItem("pk_org");
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.FieldLengthCheckRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.pubapp.pub.rule.CheckNotNullRule();
		processer.addBeforeRule(rule);
		rule = new nc.bs.twhr.rangetable.rule.CheckDateDuplicated();
		processer.addBeforeRule(rule);
	}
}
