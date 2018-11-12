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
	// 新增
	public AggLeaveExtraRestVO[] pubinsertBills(AggLeaveExtraRestVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggLeaveExtraRestVO> transferTool = new BillTransferTool<AggLeaveExtraRestVO>(
					vos);
			AggLeaveExtraRestVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceLeaveextrarestInsertBP action = new AceLeaveextrarestInsertBP();
			AggLeaveExtraRestVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggLeaveExtraRestVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggLeaveExtraRestVO> transferTool = new BillTransferTool<AggLeaveExtraRestVO>(
					vos);
			AggLeaveExtraRestVO[] fullBills = transferTool.getClientFullInfoBill();
			AceLeaveextrarestDeleteBP deleteBP = new AceLeaveextrarestDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggLeaveExtraRestVO[] pubupdateBills(AggLeaveExtraRestVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggLeaveExtraRestVO> transTool = new BillTransferTool<AggLeaveExtraRestVO>(
					vos);
			// 补全前台VO
			AggLeaveExtraRestVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggLeaveExtraRestVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceLeaveextrarestUpdateBP bp = new AceLeaveextrarestUpdateBP();
			AggLeaveExtraRestVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
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
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

}