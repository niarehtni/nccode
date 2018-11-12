package nc.bs.hrjf.deptadj.ace.bp;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrjf.deptadj.plugin.bpplugin.DeptadjPluginPoint;
import nc.bs.hrta.leaveplan.plugin.bpplugin.LeaveplanPluginPoint;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leaveplan.AggLeavePlanVO;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.data.bill.template.UpdateBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.om.IDeptAdjustService;
import nc.itf.om.IDeptQueryService;


/**
 * 标准单据删除BP
 */
public class AceDeptadjDeleteBP {
	/**
	 * 为新版本部门
	 */
	private String PK_VID_FOR_DEPT_VER = null;
	
	private String getNewDeptVerPK(){
		if(PK_VID_FOR_DEPT_VER == null){
			PK_VID_FOR_DEPT_VER= 
					NCLocator.getInstance().lookup(IDeptAdjustService.class).getNewDeptVerPK();
		}
		return PK_VID_FOR_DEPT_VER;
		
	}
	
	public void delete(AggHRDeptAdjustVO[] bills) {
		

		DeleteBPTemplate<AggHRDeptAdjustVO> bp = new DeleteBPTemplate<AggHRDeptAdjustVO>(
				DeptadjPluginPoint.DELETE);
		//如果是部门新增单据,那么删除时要连部门主档一起删除
		for(AggHRDeptAdjustVO bill : bills){
			if(bill != null && bill.getParentVO() != null 
					&& (bill.getParentVO().getPk_dept_v() == null
					||bill.getParentVO().getPk_dept_v().equals(getNewDeptVerPK()))){
				//而且成立日期要大于当前日期
				if(bill.getParentVO().getCreatedate()==null 
						|| bill.getParentVO().getCreatedate().after(new UFLiteralDate())){
				String sqlStr = "delete from  org_dept where pk_dept = '"+bill.getParentVO().getPk_dept()+"'";
				try {
					new BaseDAO().executeUpdate(sqlStr);
				} catch (DAOException e) {

					e.printStackTrace();
					Debug.debug(e.getMessage());
				}
				}
			}
		}
		// 增加执行前规则
		this.addBeforeRule(bp.getAroundProcesser());
		// 增加执行后业务规则
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	
	/**
	 * 删除后业务规则
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO 后规则
		IRule<AggHRDeptAdjustVO> rule = null;

	}

	private void addBeforeRule(AroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO 前规则
//		IRule<AggLeavePlanVO> rule = null;
//		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
//		processer.addBeforeRule(rule);
	}
}
