package nc.bs.hrwa.sumincometax.ace.bp;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.hrwa.sumincometax.plugin.bpplugin.SumincometaxPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.hrwa.sumincometax.SumIncomeTaxVO;

/**
 * 标准单据新增BP
 */
public class AceSumincometaxInsertBP {

	public AggSumIncomeTaxVO[] insert(AggSumIncomeTaxVO[] bills) throws DAOException {

		InsertBPTemplate<AggSumIncomeTaxVO> bp = new InsertBPTemplate<AggSumIncomeTaxVO>(
				SumincometaxPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		return bp.insert(bills);
		

	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggSumIncomeTaxVO> processor) {
		// TODO 新增后规则
		IRule<AggSumIncomeTaxVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggSumIncomeTaxVO> processer) {
		// TODO 新增前规则
		IRule<AggSumIncomeTaxVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
