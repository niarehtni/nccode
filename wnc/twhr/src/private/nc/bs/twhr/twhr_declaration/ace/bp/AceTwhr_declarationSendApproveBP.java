package nc.bs.twhr.twhr_declaration.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * 标准单据送审的BP
 */
public class AceTwhr_declarationSendApproveBP {
	/**
	 * 送审动作
	 * 
	 * @param vos
	 *            单据VO数组
	 * @param script
	 *            单据动作脚本对象
	 * @return 送审后的单据VO数组
	 */

	public AggDeclarationVO[] sendApprove(AggDeclarationVO[] clientBills,
			AggDeclarationVO[] originBills) {
		for (AggDeclarationVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}",
					BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// 数据持久化
		AggDeclarationVO[] returnVos = new BillUpdate<AggDeclarationVO>().update(
				clientBills, originBills);
		return returnVos;
	}
}
