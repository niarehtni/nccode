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
	// 新增
	public AggSumIncomeTaxVO[] pubinsertBills(AggSumIncomeTaxVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggSumIncomeTaxVO> transferTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			AggSumIncomeTaxVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceSumincometaxInsertBP action = new AceSumincometaxInsertBP();
			AggSumIncomeTaxVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggSumIncomeTaxVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggSumIncomeTaxVO> transferTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			AggSumIncomeTaxVO[] fullBills = transferTool.getClientFullInfoBill();
			AceSumincometaxDeleteBP deleteBP = new AceSumincometaxDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggSumIncomeTaxVO[] pubupdateBills(AggSumIncomeTaxVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggSumIncomeTaxVO> transTool = new BillTransferTool<AggSumIncomeTaxVO>(
					vos);
			// 补全前台VO
			AggSumIncomeTaxVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggSumIncomeTaxVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceSumincometaxUpdateBP bp = new AceSumincometaxUpdateBP();
			AggSumIncomeTaxVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
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
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

}