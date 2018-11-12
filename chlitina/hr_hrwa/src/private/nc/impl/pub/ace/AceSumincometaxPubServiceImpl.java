package nc.impl.pub.ace;

import nc.bs.hrwa.sumincometax.ace.bp.AceSumincometaxInsertBP;
import nc.bs.hrwa.sumincometax.ace.bp.AceSumincometaxUpdateBP;
import nc.bs.hrwa.sumincometax.ace.bp.AceSumincometaxDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;

public abstract class AceSumincometaxPubServiceImpl {
	// ����
	public AggSumIncomeTaxVO[] pubinsertBills(AggSumIncomeTaxVO[] vos)
			throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggSumIncomeTaxVO> transferTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			AggSumIncomeTaxVO[] mergedVO = transferTool.getClientFullInfoBill();

			// ����BP
			AceSumincometaxInsertBP action = new AceSumincometaxInsertBP();
			AggSumIncomeTaxVO[] retvos = action.insert(mergedVO);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggSumIncomeTaxVO[] vos) throws BusinessException {
		try {
			// ���� �Ƚ�ts
			BillTransferTool<AggSumIncomeTaxVO> transferTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			AggSumIncomeTaxVO[] fullBills = transferTool.getClientFullInfoBill();
			AceSumincometaxDeleteBP deleteBP = new AceSumincometaxDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggSumIncomeTaxVO[] pubupdateBills(AggSumIncomeTaxVO[] vos)
			throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggSumIncomeTaxVO> transTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			// ��ȫǰ̨VO
			AggSumIncomeTaxVO[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			AggSumIncomeTaxVO[] originBills = transTool.getOriginBills();
			// ����BP
			AceSumincometaxUpdateBP bp = new AceSumincometaxUpdateBP();
			AggSumIncomeTaxVO[] retBills = bp.update(fullBills, originBills);
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggSumIncomeTaxVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggSumIncomeTaxVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggSumIncomeTaxVO> query = new BillLazyQuery<AggSumIncomeTaxVO>(
					AggSumIncomeTaxVO.class);
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