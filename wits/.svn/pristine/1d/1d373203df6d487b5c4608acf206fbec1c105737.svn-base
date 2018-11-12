package nc.bs.hrwa.allocate.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceAllocateUnSendApproveBP {

	public AggAllocateOutVO[] unSend(AggAllocateOutVO[] clientBills, AggAllocateOutVO[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggAllocateOutVO> update = new BillUpdate<AggAllocateOutVO>();
		AggAllocateOutVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggAllocateOutVO[] clientBills) {
		for (AggAllocateOutVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}", BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
