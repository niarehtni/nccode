package nc.impl.pub.ace;

import nc.bs.overtime.segrule.ace.bp.AceSegruleDeleteBP;
import nc.bs.overtime.segrule.ace.bp.AceSegruleInsertBP;
import nc.bs.overtime.segrule.ace.bp.AceSegruleUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggSegRuleVO;

public abstract class AceSegrulePubServiceImpl {
	// ����
	public AggSegRuleVO[] pubinsertBills(AggSegRuleVO[] vos)
			throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggSegRuleVO> transferTool = new BillTransferTool<AggSegRuleVO>(
					vos);
			AggSegRuleVO[] mergedVO = transferTool.getClientFullInfoBill();

			// ����BP
			AceSegruleInsertBP action = new AceSegruleInsertBP();
			AggSegRuleVO[] retvos = action.insert(mergedVO);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggSegRuleVO[] vos) throws BusinessException {
		try {
			// ���� �Ƚ�ts
			BillTransferTool<AggSegRuleVO> transferTool = new BillTransferTool<AggSegRuleVO>(
					vos);
			AggSegRuleVO[] fullBills = transferTool.getClientFullInfoBill();
			AceSegruleDeleteBP deleteBP = new AceSegruleDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggSegRuleVO[] pubupdateBills(AggSegRuleVO[] vos)
			throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggSegRuleVO> transTool = new BillTransferTool<AggSegRuleVO>(
					vos);
			// ��ȫǰ̨VO
			AggSegRuleVO[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			AggSegRuleVO[] originBills = transTool.getOriginBills();
			// ����BP
			AceSegruleUpdateBP bp = new AceSegruleUpdateBP();
			AggSegRuleVO[] retBills = bp.update(fullBills, originBills);
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggSegRuleVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggSegRuleVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggSegRuleVO> query = new BillLazyQuery<AggSegRuleVO>(
					AggSegRuleVO.class);
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