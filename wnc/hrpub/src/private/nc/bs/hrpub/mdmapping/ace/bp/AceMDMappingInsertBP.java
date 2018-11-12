package nc.bs.hrpub.mdmapping.ace.bp;

import nc.bs.hrpub.mdmapping.plugin.bpplugin.MDMappingPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrpub.mdmapping.AggMDClassVO;

/**
 * ��׼��������BP
 */
public class AceMDMappingInsertBP {

	public AggMDClassVO[] insert(AggMDClassVO[] bills) {

		InsertBPTemplate<AggMDClassVO> bp = new InsertBPTemplate<AggMDClassVO>(
				MDMappingPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);

	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggMDClassVO> processor) {
		// TODO ���������
		IRule<AggMDClassVO> rule = null;
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggMDClassVO> processer) {
		// TODO ����ǰ����
		IRule<AggMDClassVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
