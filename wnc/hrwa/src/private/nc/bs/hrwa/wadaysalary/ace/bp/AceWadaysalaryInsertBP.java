package nc.bs.hrwa.wadaysalary.ace.bp;

import nc.bs.hrwa.wadaysalary.plugin.bpplugin.WadaysalaryPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.wa.paydata.AggDaySalaryVO;

/**
 * ��׼��������BP
 */
public class AceWadaysalaryInsertBP {

	public AggDaySalaryVO[] insert(AggDaySalaryVO[] bills) {

		InsertBPTemplate<AggDaySalaryVO> bp = new InsertBPTemplate<AggDaySalaryVO>(
				WadaysalaryPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggDaySalaryVO> processor) {
		// TODO ���������
		IRule<AggDaySalaryVO> rule = null;
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggDaySalaryVO> processer) {
		// TODO ����ǰ����
		IRule<AggDaySalaryVO> rule = null;
//		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
//		processer.addBeforeRule(rule);
	}
}
