package nc.impl.pub.ace;

import nc.bs.hrpub.mdmapping.ace.bp.AceMDMappingInsertBP;
import nc.bs.hrpub.mdmapping.ace.bp.AceMDMappingUpdateBP;
import nc.bs.hrpub.mdmapping.ace.bp.AceMDMappingDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.hrpub.mdmapping.AggMDClassVO;

public abstract class AceMDMappingPubServiceImpl {
	// 新增
	public AggMDClassVO[] pubinsertBills(AggMDClassVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggMDClassVO> transferTool = new BillTransferTool<AggMDClassVO>(
					vos);
			AggMDClassVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceMDMappingInsertBP action = new AceMDMappingInsertBP();
			AggMDClassVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggMDClassVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggMDClassVO> transferTool = new BillTransferTool<AggMDClassVO>(
					vos);
			AggMDClassVO[] fullBills = transferTool.getClientFullInfoBill();
			AceMDMappingDeleteBP deleteBP = new AceMDMappingDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggMDClassVO[] pubupdateBills(AggMDClassVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggMDClassVO> transTool = new BillTransferTool<AggMDClassVO>(
					vos);
			// 补全前台VO
			AggMDClassVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggMDClassVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceMDMappingUpdateBP bp = new AceMDMappingUpdateBP();
			AggMDClassVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggMDClassVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggMDClassVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggMDClassVO> query = new BillLazyQuery<AggMDClassVO>(
					AggMDClassVO.class);
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