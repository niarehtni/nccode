package nc.impl.pub.ace;

import java.util.Date;

import nc.bs.hrta.leaveplan.ace.bp.AceLeaveplanInsertBP;
import nc.bs.hrta.leaveplan.ace.bp.AceLeaveplanUpdateBP;
import nc.bs.hrta.leaveplan.ace.bp.AceLeaveplanDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leaveplan.AggLeavePlanVO;

public abstract class AceLeaveplanPubServiceImpl {
	// 新增
	public AggLeavePlanVO[] pubinsertBills(AggLeavePlanVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggLeavePlanVO> transferTool = new BillTransferTool<AggLeavePlanVO>(
					vos);
			AggLeavePlanVO[] mergedVO = transferTool.getClientFullInfoBill();
			for(AggLeavePlanVO vo: mergedVO ){
				if(null==vo.getParentVO().getBillcode()){
					Date date = new Date();
					String arg = "plan"+date.getTime();
					mergedVO[0].getParentVO().setAttributeValue("billcode", arg);
				}
			}
			// 调用BP
			AceLeaveplanInsertBP action = new AceLeaveplanInsertBP();
			AggLeavePlanVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggLeavePlanVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggLeavePlanVO> transferTool = new BillTransferTool<AggLeavePlanVO>(
					vos);
			AggLeavePlanVO[] fullBills = transferTool.getClientFullInfoBill();
			AceLeaveplanDeleteBP deleteBP = new AceLeaveplanDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggLeavePlanVO[] pubupdateBills(AggLeavePlanVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggLeavePlanVO> transTool = new BillTransferTool<AggLeavePlanVO>(
					vos);
			// 补全前台VO
			AggLeavePlanVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggLeavePlanVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceLeaveplanUpdateBP bp = new AceLeaveplanUpdateBP();
			AggLeavePlanVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggLeavePlanVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggLeavePlanVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggLeavePlanVO> query = new BillLazyQuery<AggLeavePlanVO>(
					AggLeavePlanVO.class);
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