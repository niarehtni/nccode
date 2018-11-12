package nc.bs.twhr.twhr_declaration.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.pub.VOStatus;

/**
 * 标准单据弃审的BP
 */
public class AceTwhr_declarationUnApproveBP {

	public AggDeclarationVO[] unApprove(AggDeclarationVO[] clientBills,
			AggDeclarationVO[] originBills) {
		for (AggDeclarationVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggDeclarationVO> update = new BillUpdate<AggDeclarationVO>();
		AggDeclarationVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}
}
