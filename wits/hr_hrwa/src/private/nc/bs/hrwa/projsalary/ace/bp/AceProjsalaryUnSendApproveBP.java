package nc.bs.hrwa.projsalary.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceProjsalaryUnSendApproveBP {

	public AggProjSalaryVO[] unSend(AggProjSalaryVO[] clientBills, AggProjSalaryVO[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggProjSalaryVO> update = new BillUpdate<AggProjSalaryVO>();
		AggProjSalaryVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggProjSalaryVO[] clientBills) {
		for (AggProjSalaryVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}", BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
