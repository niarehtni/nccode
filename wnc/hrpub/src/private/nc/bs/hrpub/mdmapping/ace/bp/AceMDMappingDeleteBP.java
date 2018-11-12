package nc.bs.hrpub.mdmapping.ace.bp;

import nc.bs.hrpub.mdmapping.plugin.bpplugin.MDMappingPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.hrpub.mdmapping.AggMDClassVO;

/**
 * ��׼����ɾ��BP
 */
public class AceMDMappingDeleteBP {

	public void delete(AggMDClassVO[] bills) {

		DeleteBPTemplate<AggMDClassVO> bp = new DeleteBPTemplate<AggMDClassVO>(
				MDMappingPluginPoint.DELETE);
		// ����ִ��ǰ����
		// this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		// this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggMDClassVO> processer) {
		// TODO ǰ����
		IRule<AggMDClassVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggMDClassVO> processer) {
		// TODO �����

	}
}
