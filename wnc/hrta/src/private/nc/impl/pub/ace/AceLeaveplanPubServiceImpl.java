package nc.impl.pub.ace;

import java.util.Date;

import nc.bs.hrta.leaveplan.ace.bp.AceLeaveplanInsertBP;
import nc.bs.hrta.leaveplan.ace.bp.AceLeaveplanUpdateBP;
import nc.bs.hrta.leaveplan.ace.bp.AceLeaveplanDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leaveplan.AggLeavePlanVO;

public abstract class AceLeaveplanPubServiceImpl {
	// ����
	public AggLeavePlanVO[] pubinsertBills(AggLeavePlanVO[] vos)
			throws BusinessException {
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggLeavePlanVO> transferTool = new BillTransferTool<AggLeavePlanVO>(
					vos);
			AggLeavePlanVO[] mergedVO = transferTool.getClientFullInfoBill();
			for(AggLeavePlanVO vo: mergedVO ){
				if(null==vo.getParentVO().getBillcode()){
					Date date = new Date();
					String arg = "plan"+date.getTime();
					mergedVO[0].getParentVO().setAttributeValue("billcode", arg);
				}
			}
			// ����BP
			AceLeaveplanInsertBP action = new AceLeaveplanInsertBP();
			AggLeavePlanVO[] retvos = action.insert(mergedVO);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggLeavePlanVO[] vos) throws BusinessException {
		try {
			// ���� �Ƚ�ts
			BillTransferTool<AggLeavePlanVO> transferTool = new BillTransferTool<AggLeavePlanVO>(
					vos);
			AggLeavePlanVO[] fullBills = transferTool.getClientFullInfoBill();
			AceLeaveplanDeleteBP deleteBP = new AceLeaveplanDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggLeavePlanVO[] pubupdateBills(AggLeavePlanVO[] vos)
			throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggLeavePlanVO> transTool = new BillTransferTool<AggLeavePlanVO>(
					vos);
			// ��ȫǰ̨VO
			AggLeavePlanVO[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			AggLeavePlanVO[] originBills = transTool.getOriginBills();
			// ����BP
			AceLeaveplanUpdateBP bp = new AceLeaveplanUpdateBP();
			AggLeavePlanVO[] retBills = bp.update(fullBills, originBills);
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggLeavePlanVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggLeavePlanVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggLeavePlanVO> query = new BillLazyQuery<AggLeavePlanVO>(
					AggLeavePlanVO.class);
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