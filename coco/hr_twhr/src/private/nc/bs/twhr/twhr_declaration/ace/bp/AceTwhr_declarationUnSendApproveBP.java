package nc.bs.twhr.twhr_declaration.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据收回的BP
 */
public class AceTwhr_declarationUnSendApproveBP {

	public AggDeclarationVO[] unSend(AggDeclarationVO[] clientBills,
			AggDeclarationVO[] originBills) {
		// 把VO持久化到数据库中
		this.setHeadVOStatus(clientBills);
		BillUpdate<AggDeclarationVO> update = new BillUpdate<AggDeclarationVO>();
		AggDeclarationVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

	private void setHeadVOStatus(AggDeclarationVO[] clientBills) {
		for (AggDeclarationVO clientBill : clientBills) {
			clientBill.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.FREE.value());
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
	}
}
