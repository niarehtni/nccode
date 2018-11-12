package nc.bs.hrpub.mdmapping.ace.bp;

import nc.bs.hrpub.mdmapping.plugin.bpplugin.MDMappingPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.hrpub.mdmapping.AggMDClassVO;

/**
 * 修改保存的BP
 * 
 */
public class AceMDMappingUpdateBP {

	public AggMDClassVO[] update(AggMDClassVO[] bills,
			AggMDClassVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggMDClassVO> bp = new UpdateBPTemplate<AggMDClassVO>(
				MDMappingPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		return bp.update(bills, originBills);
	}

	private void addAfterRule(CompareAroundProcesser<AggMDClassVO> processer) {
		// TODO 后规则
		IRule<AggMDClassVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggMDClassVO> processer) {
		// TODO 前规则
		IRule<AggMDClassVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
