package nc.impl.pub.ace;

import nc.bs.hrwa.projsalary.ace.bp.AceProjsalaryInsertBP;
import nc.bs.hrwa.projsalary.ace.bp.AceProjsalaryUpdateBP;
import nc.bs.hrwa.projsalary.ace.bp.AceProjsalaryDeleteBP;
import nc.bs.hrwa.projsalary.ace.bp.AceProjsalarySendApproveBP;
import nc.bs.hrwa.projsalary.ace.bp.AceProjsalaryUnSendApproveBP;
import nc.bs.hrwa.projsalary.ace.bp.AceProjsalaryApproveBP;
import nc.bs.hrwa.projsalary.ace.bp.AceProjsalaryUnApproveBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceProjsalaryPubServiceImpl {
	// 新增
	public AggProjSalaryVO[] pubinsertBills(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggProjSalaryVO> transferTool = new BillTransferTool<AggProjSalaryVO>(clientFullVOs);
			// 调用BP
			AceProjsalaryInsertBP action = new AceProjsalaryInsertBP();
			AggProjSalaryVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceProjsalaryDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggProjSalaryVO[] pubupdateBills(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggProjSalaryVO> transferTool = new BillTransferTool<AggProjSalaryVO>(clientFullVOs);
			AceProjsalaryUpdateBP bp = new AceProjsalaryUpdateBP();
			AggProjSalaryVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggProjSalaryVO[] pubquerybills(IQueryScheme queryScheme) throws BusinessException {
		AggProjSalaryVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggProjSalaryVO> query = new BillLazyQuery<AggProjSalaryVO>(AggProjSalaryVO.class);
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
	public AggProjSalaryVO[] pubsendapprovebills(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		AceProjsalarySendApproveBP bp = new AceProjsalarySendApproveBP();
		AggProjSalaryVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// 收回
	public AggProjSalaryVO[] pubunsendapprovebills(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		AceProjsalaryUnSendApproveBP bp = new AceProjsalaryUnSendApproveBP();
		AggProjSalaryVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// 审批
	public AggProjSalaryVO[] pubapprovebills(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceProjsalaryApproveBP bp = new AceProjsalaryApproveBP();
		AggProjSalaryVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// 弃审

	public AggProjSalaryVO[] pubunapprovebills(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceProjsalaryUnApproveBP bp = new AceProjsalaryUnApproveBP();
		AggProjSalaryVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

}