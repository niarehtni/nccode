package nc.bs.hrpub.mdmapping.ace.bp;

import nc.bs.hrpub.mdmapping.plugin.bpplugin.MDMappingPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.hrpub.mdmapping.AggMDClassVO;

/**
 * 标准单据删除BP
 */
public class AceMDMappingDeleteBP {

	public void delete(AggMDClassVO[] bills) {

		DeleteBPTemplate<AggMDClassVO> bp = new DeleteBPTemplate<AggMDClassVO>(
				MDMappingPluginPoint.DELETE);
		// 增加执行前规则
		// this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		// this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggMDClassVO> processer) {
		// TODO 前规则
		IRule<AggMDClassVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggMDClassVO> processer) {
		// TODO 后规则

	}
}
