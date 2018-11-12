package nc.impl.pub.ace;

import nc.bs.hrwa.incometax.ace.bp.AceIncometaxInsertBP;
import nc.bs.hrwa.incometax.ace.bp.AceIncometaxUpdateBP;
import nc.bs.hrwa.incometax.ace.bp.AceIncometaxDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceIncometaxPubServiceImpl {
	// 新增
	public AggIncomeTaxVO[] pubinsertBills(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggIncomeTaxVO> transferTool = new BillTransferTool<AggIncomeTaxVO>(
					clientFullVOs);
			// 调用BP
			AceIncometaxInsertBP action = new AceIncometaxInsertBP();
			AggIncomeTaxVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		try {
			// 调用BP
			new AceIncometaxDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggIncomeTaxVO[] pubupdateBills(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggIncomeTaxVO> transferTool = new BillTransferTool<AggIncomeTaxVO>(
					clientFullVOs);
			AceIncometaxUpdateBP bp = new AceIncometaxUpdateBP();
			originBills = transferTool.getOriginBills();
			AggIncomeTaxVO[] retvos = bp.update(clientFullVOs, originBills);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggIncomeTaxVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggIncomeTaxVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggIncomeTaxVO> query = new BillLazyQuery<AggIncomeTaxVO>(
					AggIncomeTaxVO.class);
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