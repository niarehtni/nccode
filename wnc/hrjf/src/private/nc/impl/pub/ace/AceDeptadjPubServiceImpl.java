package nc.impl.pub.ace;

import nc.bs.hrjf.deptadj.ace.bp.AceDeptadjInsertBP;
import nc.bs.hrjf.deptadj.ace.bp.AceDeptadjDeleteBP;
import nc.bs.hrjf.deptadj.ace.bp.AceDeptadjUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceDeptadjPubServiceImpl {
	// 新增
	public AggHRDeptAdjustVO[] pubinsertBills(AggHRDeptAdjustVO[] clientFullVOs
			) throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggHRDeptAdjustVO> transferTool = new BillTransferTool<AggHRDeptAdjustVO>(
					clientFullVOs);
			// 调用BP
			AceDeptadjInsertBP action = new AceDeptadjInsertBP();
			AggHRDeptAdjustVO[] retvos = action.insert(clientFullVOs);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}
	// 删除
		public void pubdeleteBills(AggHRDeptAdjustVO[] vos) throws BusinessException {
			try {
				// 加锁 比较ts
				BillTransferTool<AggHRDeptAdjustVO> transferTool = new BillTransferTool<AggHRDeptAdjustVO>(
						vos);
				AggHRDeptAdjustVO[] fullBills = transferTool.getClientFullInfoBill();
				AceDeptadjDeleteBP deleteBP = new AceDeptadjDeleteBP();
				deleteBP.delete(fullBills);
			} catch (Exception e) {
				ExceptionUtils.marsh(e);
			}
		}

		// 修改
		public AggHRDeptAdjustVO[] pubupdateBills(AggHRDeptAdjustVO[] vos)
				throws BusinessException {
			try {
				// 加锁 + 检查ts
				BillTransferTool<AggHRDeptAdjustVO> transTool = new BillTransferTool<AggHRDeptAdjustVO>(
						vos);
				// 补全前台VO
				AggHRDeptAdjustVO[] fullBills = transTool.getClientFullInfoBill();
				// 获得修改前vo
				AggHRDeptAdjustVO[] originBills = transTool.getOriginBills();
				// 调用BP
				AceDeptadjUpdateBP bp = new AceDeptadjUpdateBP();
				AggHRDeptAdjustVO[] retBills = bp.update(fullBills, originBills);
				// 构造返回数据
				retBills = transTool.getBillForToClient(retBills);
				return retBills;
			} catch (Exception e) {
				ExceptionUtils.marsh(e);
			}
			return null;
		}


	public AggHRDeptAdjustVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggHRDeptAdjustVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggHRDeptAdjustVO> query = new BillLazyQuery<AggHRDeptAdjustVO>(
					AggHRDeptAdjustVO.class);
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