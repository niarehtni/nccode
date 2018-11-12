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
 * ��׼����ɾ��BP
 */
public class AceDeptadjDeleteBP {
	/**
	 * Ϊ�°汾����
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
		//����ǲ�����������,��ôɾ��ʱҪ����������һ��ɾ��
		for(AggHRDeptAdjustVO bill : bills){
			if(bill != null && bill.getParentVO() != null 
					&& (bill.getParentVO().getPk_dept_v() == null
					||bill.getParentVO().getPk_dept_v().equals(getNewDeptVerPK()))){
				//���ҳ�������Ҫ���ڵ�ǰ����
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
		// ����ִ��ǰ����
		this.addBeforeRule(bp.getAroundProcesser());
		// ����ִ�к�ҵ�����
		this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	
	/**
	 * ɾ����ҵ�����
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO �����
		IRule<AggHRDeptAdjustVO> rule = null;

	}

	private void addBeforeRule(AroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO ǰ����
//		IRule<AggLeavePlanVO> rule = null;
//		rule = new nc.bs.pubapp.pub.rule.FillUpdateDataRule();
//		processer.addBeforeRule(rule);
	}
}
