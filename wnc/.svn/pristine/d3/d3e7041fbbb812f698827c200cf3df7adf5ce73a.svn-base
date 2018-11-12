package nc.bs.hrjf.deptadj.ace.bp;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrjf.deptadj.plugin.bpplugin.DeptadjPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.om.IDeptAdjustService;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 修改保存的BP
 * 
 */
public class AceDeptadjUpdateBP {

	public AggHRDeptAdjustVO[] update(AggHRDeptAdjustVO[] bills,
			AggHRDeptAdjustVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggHRDeptAdjustVO> bp = new UpdateBPTemplate<AggHRDeptAdjustVO>(
				DeptadjPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		AggHRDeptAdjustVO[] results = bp.update(bills, originBills);
		// 如果生效日期为当前日期,则立即生效
		try {
			NCLocator.getInstance().lookup(IDeptAdjustService.class)
					.executeDeptVersion(new UFLiteralDate());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			e.printStackTrace();
		}
		return results;
	}

	private void addAfterRule(CompareAroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO 后规则
		IRule<AggHRDeptAdjustVO> rule = null;

	}

	private void addBeforeRule(CompareAroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO 前规则
		IRule<AggHRDeptAdjustVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
		processer.addBeforeRule(rule);
	}

}
