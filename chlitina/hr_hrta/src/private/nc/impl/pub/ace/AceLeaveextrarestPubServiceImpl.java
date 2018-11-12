package nc.impl.pub.ace;

import nc.bs.hrta.leaveextrarest.ace.bp.AceLeaveextrarestInsertBP;
import nc.bs.hrta.leaveextrarest.ace.bp.AceLeaveextrarestUpdateBP;
import nc.bs.hrta.leaveextrarest.ace.bp.AceLeaveextrarestDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;

public abstract class AceLeaveextrarestPubServiceImpl {
	// ����
	public AggLeaveExtraRestVO[] pubinsertBills(AggLeaveExtraRestVO[] vos)
			throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggLeaveExtraRestVO> transferTool = new BillTransferTool<AggLeaveExtraRestVO>(
					vos);
			AggLeaveExtraRestVO[] mergedVO = transferTool.getClientFullInfoBill();

			// ����BP
			AceLeaveextrarestInsertBP action = new AceLeaveextrarestInsertBP();
			AggLeaveExtraRestVO[] retvos = action.insert(mergedVO);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggLeaveExtraRestVO[] vos) throws BusinessException {
		try {
			// ���� �Ƚ�ts
			BillTransferTool<AggLeaveExtraRestVO> transferTool = new BillTransferTool<AggLeaveExtraRestVO>(
					vos);
			AggLeaveExtraRestVO[] fullBills = transferTool.getClientFullInfoBill();
			AceLeaveextrarestDeleteBP deleteBP = new AceLeaveextrarestDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggLeaveExtraRestVO[] pubupdateBills(AggLeaveExtraRestVO[] vos)
			throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggLeaveExtraRestVO> transTool = new BillTransferTool<AggLeaveExtraRestVO>(
					vos);
			// ��ȫǰ̨VO
			AggLeaveExtraRestVO[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			AggLeaveExtraRestVO[] originBills = transTool.getOriginBills();
			// ����BP
			AceLeaveextrarestUpdateBP bp = new AceLeaveextrarestUpdateBP();
			AggLeaveExtraRestVO[] retBills = bp.update(fullBills, originBills);
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggLeaveExtraRestVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggLeaveExtraRestVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggLeaveExtraRestVO> query = new BillLazyQuery<AggLeaveExtraRestVO>(
					AggLeaveExtraRestVO.class);
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