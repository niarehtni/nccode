package nc.impl.pub.ace;

import nc.bs.hrwa.allocate.ace.bp.AceAllocateInsertBP;
import nc.bs.hrwa.allocate.ace.bp.AceAllocateUpdateBP;
import nc.bs.hrwa.allocate.ace.bp.AceAllocateDeleteBP;
import nc.bs.hrwa.allocate.ace.bp.AceAllocateSendApproveBP;
import nc.bs.hrwa.allocate.ace.bp.AceAllocateUnSendApproveBP;
import nc.bs.hrwa.allocate.ace.bp.AceAllocateApproveBP;
import nc.bs.hrwa.allocate.ace.bp.AceAllocateUnApproveBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceAllocatePubServiceImpl {
	// 新增
	public AggAllocateOutVO[] pubinsertBills(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggAllocateOutVO> transferTool = new BillTransferTool<AggAllocateOutVO>(clientFullVOs);
			// 调用BP
			AceAllocateInsertBP action = new AceAllocateInsertBP();
			AggAllocateOutVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		try {
			// 调用BP
			new AceAllocateDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggAllocateOutVO[] pubupdateBills(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggAllocateOutVO> transferTool = new BillTransferTool<AggAllocateOutVO>(clientFullVOs);
			AceAllocateUpdateBP bp = new AceAllocateUpdateBP();
			AggAllocateOutVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggAllocateOutVO[] pubquerybills(IQueryScheme queryScheme) throws BusinessException {
		AggAllocateOutVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggAllocateOutVO> query = new BillLazyQuery<AggAllocateOutVO>(AggAllocateOutVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

	// 提交
	public AggAllocateOutVO[] pubsendapprovebills(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		AceAllocateSendApproveBP bp = new AceAllocateSendApproveBP();
		AggAllocateOutVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggAllocateOutVO[] pubunsendapprovebills(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		AceAllocateUnSendApproveBP bp = new AceAllocateUnSendApproveBP();
		AggAllocateOutVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggAllocateOutVO[] pubapprovebills(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceAllocateApproveBP bp = new AceAllocateApproveBP();
		AggAllocateOutVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggAllocateOutVO[] pubunapprovebills(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceAllocateUnApproveBP bp = new AceAllocateUnApproveBP();
		AggAllocateOutVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}