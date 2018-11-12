package nc.impl.pub.ace;

import nc.bs.hrwa.incometax.ace.bp.AceIncometaxInsertBP;
import nc.bs.hrwa.incometax.ace.bp.AceIncometaxUpdateBP;
import nc.bs.hrwa.incometax.ace.bp.AceIncometaxDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceIncometaxPubServiceImpl {
	// ����
	public AggIncomeTaxVO[] pubinsertBills(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggIncomeTaxVO> transferTool = new BillTransferTool<AggIncomeTaxVO>(
					clientFullVOs);
			// ����BP
			AceIncometaxInsertBP action = new AceIncometaxInsertBP();
			AggIncomeTaxVO[] retvos = action.insert(clientFullVOs);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		try {
			// ����BP
			new AceIncometaxDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggIncomeTaxVO[] pubupdateBills(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggIncomeTaxVO> transferTool = new BillTransferTool<AggIncomeTaxVO>(
					clientFullVOs);
			AceIncometaxUpdateBP bp = new AceIncometaxUpdateBP();
			originBills = transferTool.getOriginBills();
			AggIncomeTaxVO[] retvos = bp.update(clientFullVOs, originBills);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggIncomeTaxVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggIncomeTaxVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggIncomeTaxVO> query = new BillLazyQuery<AggIncomeTaxVO>(
					AggIncomeTaxVO.class);
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