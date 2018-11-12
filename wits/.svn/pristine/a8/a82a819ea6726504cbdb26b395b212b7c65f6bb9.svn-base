package nc.bs.hrwa.projsalary.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceProjsalaryUnApproveBP {

	public AggProjSalaryVO[] unApprove(AggProjSalaryVO[] clientBills, AggProjSalaryVO[] originBills) {
		for (AggProjSalaryVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggProjSalaryVO> update = new BillUpdate<AggProjSalaryVO>();
		AggProjSalaryVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
