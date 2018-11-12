package nc.impl.pub.ace;

import nc.bs.twhr.nhicalc.ace.bp.AceNhicalcInsertBP;
import nc.bs.twhr.nhicalc.ace.bp.AceNhicalcUpdateBP;
import nc.bs.twhr.nhicalc.ace.bp.AceNhicalcDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;

public abstract class AceNhicalcPubServiceImpl {
	// ����
	public AggNhiCalcVO[] pubinsertBills(AggNhiCalcVO[] vos)
			throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggNhiCalcVO> transferTool = new BillTransferTool<AggNhiCalcVO>(
					vos);
			AggNhiCalcVO[] mergedVO = transferTool.getClientFullInfoBill();

			// ����BP
			AceNhicalcInsertBP action = new AceNhicalcInsertBP();
			AggNhiCalcVO[] retvos = action.insert(mergedVO);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggNhiCalcVO[] vos) throws BusinessException {
		try {
			// ���� �Ƚ�ts
			BillTransferTool<AggNhiCalcVO> transferTool = new BillTransferTool<AggNhiCalcVO>(
					vos);
			AggNhiCalcVO[] fullBills = transferTool.getClientFullInfoBill();
			AceNhicalcDeleteBP deleteBP = new AceNhicalcDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggNhiCalcVO[] pubupdateBills(AggNhiCalcVO[] vos)
			throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggNhiCalcVO> transTool = new BillTransferTool<AggNhiCalcVO>(
					vos);
			// ��ȫǰ̨VO
			AggNhiCalcVO[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			AggNhiCalcVO[] originBills = transTool.getOriginBills();
			// ����BP
			AceNhicalcUpdateBP bp = new AceNhicalcUpdateBP();
			AggNhiCalcVO[] retBills = bp.update(fullBills, originBills);
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggNhiCalcVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggNhiCalcVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggNhiCalcVO> query = new BillLazyQuery<AggNhiCalcVO>(
					AggNhiCalcVO.class);
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