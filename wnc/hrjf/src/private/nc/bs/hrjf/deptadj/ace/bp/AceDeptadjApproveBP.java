package nc.bs.hrjf.deptadj.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;

/**
 * ��׼������˵�BP
 */
public class AceDeptadjApproveBP {

	/**
	 * ��˶���
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggHRDeptAdjustVO[] approve(AggHRDeptAdjustVO[] clientBills,
			AggHRDeptAdjustVO[] originBills) {
		for (AggHRDeptAdjustVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggHRDeptAdjustVO> update = new BillUpdate<AggHRDeptAdjustVO>();
		AggHRDeptAdjustVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
