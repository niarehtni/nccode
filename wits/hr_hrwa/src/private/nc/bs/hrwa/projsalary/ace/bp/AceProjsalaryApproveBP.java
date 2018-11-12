package nc.bs.hrwa.projsalary.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.wa.projsalary.AggProjSalaryVO;

/**
 * 标准单据审核的BP
 */
public class AceProjsalaryApproveBP {

	/**
	 * 审核动作
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggProjSalaryVO[] approve(AggProjSalaryVO[] clientBills, AggProjSalaryVO[] originBills) {
		for (AggProjSalaryVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggProjSalaryVO> update = new BillUpdate<AggProjSalaryVO>();
		AggProjSalaryVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
