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
	// 新增
	public AggSegRuleVO[] pubinsertBills(AggSegRuleVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggSegRuleVO> transferTool = new BillTransferTool<AggSegRuleVO>(
					vos);
			AggSegRuleVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceSegruleInsertBP action = new AceSegruleInsertBP();
			AggSegRuleVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggSegRuleVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggSegRuleVO> transferTool = new BillTransferTool<AggSegRuleVO>(
					vos);
			AggSegRuleVO[] fullBills = transferTool.getClientFullInfoBill();
			AceSegruleDeleteBP deleteBP = new AceSegruleDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggSegRuleVO[] pubupdateBills(AggSegRuleVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggSegRuleVO> transTool = new BillTransferTool<AggSegRuleVO>(
					vos);
			// 补全前台VO
			AggSegRuleVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggSegRuleVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceSegruleUpdateBP bp = new AceSegruleUpdateBP();
			AggSegRuleVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
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
	 * 由子类实现，查询之前对queryScheme进行加工，加入自己的逻辑
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// 查询之前对queryScheme进行加工，加入自己的逻辑
	}

}