package nc.bs.hrwa.allocate.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.wa.allocate.AggAllocateOutVO;

/**
 * 标准单据审核的BP
 */
public class AceAllocateApproveBP {

	/**
	 * 审核动作
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggAllocateOutVO[] approve(AggAllocateOutVO[] clientBills, AggAllocateOutVO[] originBills) {
		for (AggAllocateOutVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggAllocateOutVO> update = new BillUpdate<AggAllocateOutVO>();
		AggAllocateOutVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
