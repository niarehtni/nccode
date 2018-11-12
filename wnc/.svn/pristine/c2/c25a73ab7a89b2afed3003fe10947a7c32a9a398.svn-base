package nc.impl.pub.ace;

import nc.bs.overtime.segdetail.ace.bp.AceSegdetailDeleteBP;
import nc.bs.overtime.segdetail.ace.bp.AceSegdetailInsertBP;
import nc.bs.overtime.segdetail.ace.bp.AceSegdetailUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggSegDetailVO;

public abstract class AceSegdetailPubServiceImpl {
	// 新增
	public AggSegDetailVO[] pubinsertBills(AggSegDetailVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggSegDetailVO> transferTool = new BillTransferTool<AggSegDetailVO>(
					vos);
			AggSegDetailVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceSegdetailInsertBP action = new AceSegdetailInsertBP();
			AggSegDetailVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggSegDetailVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggSegDetailVO> transferTool = new BillTransferTool<AggSegDetailVO>(
					vos);
			AggSegDetailVO[] fullBills = transferTool.getClientFullInfoBill();
			AceSegdetailDeleteBP deleteBP = new AceSegdetailDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggSegDetailVO[] pubupdateBills(AggSegDetailVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggSegDetailVO> transTool = new BillTransferTool<AggSegDetailVO>(
					vos);
			// 补全前台VO
			AggSegDetailVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggSegDetailVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceSegdetailUpdateBP bp = new AceSegdetailUpdateBP();
			AggSegDetailVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggSegDetailVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggSegDetailVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggSegDetailVO> query = new BillLazyQuery<AggSegDetailVO>(
					AggSegDetailVO.class);
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