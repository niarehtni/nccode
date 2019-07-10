package nc.bs.hrjf.deptadj.ace.bp;

import nc.bs.hrjf.deptadj.plugin.bpplugin.DeptadjPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;

/**
 * 修改保存的BP
 * 
 */
public class AceDeptadjUpdateBP {

	public AggHRDeptAdjustVO[] update(AggHRDeptAdjustVO[] bills, AggHRDeptAdjustVO[] originBills) {
		// 调用修改模板
		UpdateBPTemplate<AggHRDeptAdjustVO> bp = new UpdateBPTemplate<AggHRDeptAdjustVO>(DeptadjPluginPoint.UPDATE);
		// 执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 执行后规则
		this.addAfterRule(bp.getAroundProcesser());
		AggHRDeptAdjustVO[] results = bp.update(bills, originBills);

		// MOD by ssx for cancel auto execute after save on 2019-07-04
		// HRDeptAdjustVO[] vos = new HRDeptAdjustVO[results.length];
		// for (int i = 0;i<results.length;i++) {
		// if(results[i]==null){
		// continue;
		// }
		// vos[i]= results[i].getParentVO();
		// }
		// // 如果生效日期为当前日期,则立即生效
		// try {
		// NCLocator.getInstance().lookup(IDeptAdjustService.class)
		// .executeDeptVersion(vos,new UFLiteralDate());
		// } catch (BusinessException e) {
		// Debug.debug(e.getMessage());
		// e.printStackTrace();
		// }
		// end
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
