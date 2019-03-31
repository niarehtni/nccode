package nc.impl.pub.ace;

import nc.bs.wa.itemgroup.ace.bp.AceWaitemgroupDeleteBP;
import nc.bs.wa.itemgroup.ace.bp.AceWaitemgroupInsertBP;
import nc.bs.wa.itemgroup.ace.bp.AceWaitemgroupUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.itemgroup.AggItemGroupVO;

public abstract class AceWaitemgroupPubServiceImpl {
	// 新增
	public AggItemGroupVO[] pubinsertBills(AggItemGroupVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggItemGroupVO> transferTool = new BillTransferTool<AggItemGroupVO>(
					vos);
			AggItemGroupVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceWaitemgroupInsertBP action = new AceWaitemgroupInsertBP();
			AggItemGroupVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggItemGroupVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggItemGroupVO> transferTool = new BillTransferTool<AggItemGroupVO>(
					vos);
			AggItemGroupVO[] fullBills = transferTool.getClientFullInfoBill();
			AceWaitemgroupDeleteBP deleteBP = new AceWaitemgroupDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggItemGroupVO[] pubupdateBills(AggItemGroupVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggItemGroupVO> transTool = new BillTransferTool<AggItemGroupVO>(
					vos);
			// 补全前台VO
			AggItemGroupVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggItemGroupVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceWaitemgroupUpdateBP bp = new AceWaitemgroupUpdateBP();
			AggItemGroupVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggItemGroupVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggItemGroupVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggItemGroupVO> query = new BillLazyQuery<AggItemGroupVO>(
					AggItemGroupVO.class);
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