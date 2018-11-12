package nc.impl.pub.ace;

import nc.bs.hrjf.deptadj.ace.bp.AceDeptadjInsertBP;
import nc.bs.hrjf.deptadj.ace.bp.AceDeptadjDeleteBP;
import nc.bs.hrjf.deptadj.ace.bp.AceDeptadjUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceDeptadjPubServiceImpl {
	// ����
	public AggHRDeptAdjustVO[] pubinsertBills(AggHRDeptAdjustVO[] clientFullVOs
			) throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggHRDeptAdjustVO> transferTool = new BillTransferTool<AggHRDeptAdjustVO>(
					clientFullVOs);
			// ����BP
			AceDeptadjInsertBP action = new AceDeptadjInsertBP();
			AggHRDeptAdjustVO[] retvos = action.insert(clientFullVOs);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	// ɾ��
		public void pubdeleteBills(AggHRDeptAdjustVO[] vos) throws BusinessException {
			try {
				// ���� �Ƚ�ts
				BillTransferTool<AggHRDeptAdjustVO> transferTool = new BillTransferTool<AggHRDeptAdjustVO>(
						vos);
				AggHRDeptAdjustVO[] fullBills = transferTool.getClientFullInfoBill();
				AceDeptadjDeleteBP deleteBP = new AceDeptadjDeleteBP();
				deleteBP.delete(fullBills);
			} catch (Exception e) {
				ExceptionUtils.marsh(e);
			}
		}

		// �޸�
		public AggHRDeptAdjustVO[] pubupdateBills(AggHRDeptAdjustVO[] vos)
				throws BusinessException {
			try {
				// ���� + ���ts
				BillTransferTool<AggHRDeptAdjustVO> transTool = new BillTransferTool<AggHRDeptAdjustVO>(
						vos);
				// ��ȫǰ̨VO
				AggHRDeptAdjustVO[] fullBills = transTool.getClientFullInfoBill();
				// ����޸�ǰvo
				AggHRDeptAdjustVO[] originBills = transTool.getOriginBills();
				// ����BP
				AceDeptadjUpdateBP bp = new AceDeptadjUpdateBP();
				AggHRDeptAdjustVO[] retBills = bp.update(fullBills, originBills);
				// ���췵������
				retBills = transTool.getBillForToClient(retBills);
				return retBills;
			} catch (Exception e) {
				ExceptionUtils.marsh(e);
			}
			return null;
		}


	public AggHRDeptAdjustVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggHRDeptAdjustVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggHRDeptAdjustVO> query = new BillLazyQuery<AggHRDeptAdjustVO>(
					AggHRDeptAdjustVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	}

	

}