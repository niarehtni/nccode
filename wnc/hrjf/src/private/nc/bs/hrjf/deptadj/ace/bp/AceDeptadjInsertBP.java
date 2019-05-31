package nc.bs.hrjf.deptadj.ace.bp;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrjf.deptadj.plugin.bpplugin.DeptadjPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.om.IDeptAdjustService;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 标准单据新增BP
 */
public class AceDeptadjInsertBP {

	public AggHRDeptAdjustVO[] insert(AggHRDeptAdjustVO[] bills) {

		InsertBPTemplate<AggHRDeptAdjustVO> bp = new InsertBPTemplate<AggHRDeptAdjustVO>(
				DeptadjPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		AggHRDeptAdjustVO[] results = bp.insert(bills);
		HRDeptAdjustVO[] vos = new HRDeptAdjustVO[results.length];
		for (int i = 0;i<results.length;i++) {
			if(results[i]==null){
				continue;
			}
			vos[i]= results[i].getParentVO();
		}
		//如果生效日期为当前日期,则立即生效
		try {
			NCLocator.getInstance().lookup(IDeptAdjustService.class).executeDeptVersion(vos,new UFLiteralDate());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * 新增后规则
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggHRDeptAdjustVO> processor) {
		// TODO 新增后规则
		IRule<AggHRDeptAdjustVO> rule = null;
	}

	/**
	 * 新增前规则
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO 新增前规则
		IRule<AggHRDeptAdjustVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}
