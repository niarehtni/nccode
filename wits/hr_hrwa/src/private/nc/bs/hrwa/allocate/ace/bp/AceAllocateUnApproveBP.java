package nc.bs.hrwa.allocate.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceAllocateUnApproveBP {

	public AggAllocateOutVO[] unApprove(AggAllocateOutVO[] clientBills, AggAllocateOutVO[] originBills) {
		for (AggAllocateOutVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggAllocateOutVO> update = new BillUpdate<AggAllocateOutVO>();
		AggAllocateOutVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
