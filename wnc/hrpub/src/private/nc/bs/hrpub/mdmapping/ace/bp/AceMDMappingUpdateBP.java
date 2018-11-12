package nc.bs.hrpub.mdmapping.ace.bp;

import nc.bs.hrpub.mdmapping.plugin.bpplugin.MDMappingPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrpub.mdmapping.AggMDClassVO;

/**
 * �޸ı����BP
 * 
 */
public class AceMDMappingUpdateBP {

	public AggMDClassVO[] update(AggMDClassVO[] bills,
			AggMDClassVO[] originBills) {
		// �����޸�ģ��
		UpdateBPTemplate<AggMDClassVO> bp = new UpdateBPTemplate<AggMDClassVO>(
				MDMappingPluginPoint.UPDATE);
		// ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ִ�к����
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggMDClassVO> processer) {
		// TODO �����
		IRule<AggMDClassVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggMDClassVO> processer) {
		// TODO ǰ����
		IRule<AggMDClassVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
