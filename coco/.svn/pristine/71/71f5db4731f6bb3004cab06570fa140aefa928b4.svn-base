package nc.bs.twhr.twhr_declaration.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.pub.VOStatus;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;

/**
 * 标准单据审核的BP
 */
public class AceTwhr_declarationApproveBP {

	/**
	 * 审核动作
	 * 
	 * @param vos
	 * @param script
	 * @return
	 */
	public AggDeclarationVO[] approve(AggDeclarationVO[] clientBills,
			AggDeclarationVO[] originBills) {
		for (AggDeclarationVO clientBill : clientBills) {
			clientBill.getParentVO().setStatus(VOStatus.UPDATED);
		}
		BillUpdate<AggDeclarationVO> update = new BillUpdate<AggDeclarationVO>();
		AggDeclarationVO[] returnVos = update.update(clientBills, originBills);
		return returnVos;
	}

}
