package nc.bs.hrwa.sumincometax.ace.bp;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.hrwa.sumincometax.plugin.bpplugin.SumincometaxPluginPoint;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.SumIncomeTaxVO;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;


/**
 * 标准单据删除BP
 */
public class AceSumincometaxDeleteBP {

	public void delete(AggSumIncomeTaxVO[] bills) throws DAOException {
		DeleteBPTemplate<AggSumIncomeTaxVO> bp = new DeleteBPTemplate<AggSumIncomeTaxVO>(
				SumincometaxPluginPoint.DELETE);
		// 增加执行前规则
//		this.addBeforeRule(bp.getAroundProcesser());
//		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
		
	}

	private void addBeforeRule(AroundProcesser<AggSumIncomeTaxVO> processer) {
		// TODO 前规则
		IRule<AggSumIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * 删除后业务规则
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggSumIncomeTaxVO> processer) {
		// TODO 后规则
		IRule<AggSumIncomeTaxVO> rule = null;
		rule = new nc.bs.hrwa.sumincometax.ace.bp.SumincometaxAfterDeleteRule();
		processer.addAfterRule(rule);

	}
}
