package nc.bs.hrwa.allocate.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼���������BP
 */
public class AceAllocateSendApproveBP {
	/**
	 * ������
	 * 
	 * @param vos
	 *            ����VO����
	 * @param script
	 *            ���ݶ����ű�����
	 * @return �����ĵ���VO����
	 */

	public AggAllocateOutVO[] sendApprove(AggAllocateOutVO[] clientBills, AggAllocateOutVO[] originBills) {
		for (AggAllocateOutVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}", BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		AggAllocateOutVO[] returnVos = new BillUpdate<AggAllocateOutVO>().update(clientBills, originBills);
		return returnVos;
	}
}
