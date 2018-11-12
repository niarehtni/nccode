package nc.bs.twhr.rangetable.ace.bp;

import nc.bs.twhr.rangetable.plugin.bpplugin.RangetablePluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.twhr.rangetable.RangeTableAggVO;

/**
 * ��׼��������BP
 */
public class AceRangetableInsertBP {

	public RangeTableAggVO[] insert(RangeTableAggVO[] bills) {

		InsertBPTemplate<RangeTableAggVO> bp = new InsertBPTemplate<RangeTableAggVO>(
				RangetablePluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<RangeTableAggVO> processer) {
		IRule<RangeTableAggVO> rule = null;
		rule = new nc.bs.twhr.rangetable.rule.SetDefaultAddValueRule();
		processer.addBeforeRule(rule);
		//start ��������Ĭ�ϵ���֯�ͼ�����Ϣ Ares.Tank 2018-7-25 10:36:31
		//rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		//processer.addBeforeRule(rule);
		//end ����������֯�ͼ�����Ϣ Ares 2018-7-25 10:36:28
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
