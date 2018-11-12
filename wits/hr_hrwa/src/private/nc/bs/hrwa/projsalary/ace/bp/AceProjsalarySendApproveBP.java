package nc.bs.hrwa.projsalary.ace.bp;

import nc.impl.pubapp.pattern.data.bill.BillUpdate;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.pf.BillStatusEnum;

/**
 * ��׼���������BP
 */
public class AceProjsalarySendApproveBP {
	/**
	 * ������
	 * 
	 * @param vos
	 *            ����VO����
	 * @param script
	 *            ���ݶ����ű�����
	 * @return �����ĵ���VO����
	 */

	public AggProjSalaryVO[] sendApprove(AggProjSalaryVO[] clientBills, AggProjSalaryVO[] originBills) {
		for (AggProjSalaryVO clientFullVO : clientBills) {
			clientFullVO.getParentVO().setAttributeValue("${vmObject.billstatus}", BillStatusEnum.COMMIT.value());
			clientFullVO.getParentVO().setStatus(VOStatus.UPDATED);
		}
		// ���ݳ־û�
		AggProjSalaryVO[] returnVos = new BillUpdate<AggProjSalaryVO>().update(clientBills, originBills);
		return returnVos;
	}
}
