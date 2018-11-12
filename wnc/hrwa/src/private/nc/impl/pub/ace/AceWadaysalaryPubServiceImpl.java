package nc.impl.pub.ace;

import nc.bs.hrwa.wadaysalary.ace.bp.AceWadaysalaryInsertBP;
import nc.bs.hrwa.wadaysalary.ace.bp.AceWadaysalaryUpdateBP;
import nc.bs.hrwa.wadaysalary.ace.bp.AceWadaysalaryDeleteBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.paydata.AggDaySalaryVO;
import nc.vo.wa.pub.WaDayLoginContext;

public abstract class AceWadaysalaryPubServiceImpl {
	// 新增
	public AggDaySalaryVO[] pubinsertBills(AggDaySalaryVO[] vos)
			throws BusinessException {
		try {
			// 数据库中数据和前台传递过来的差异VO合并后的结果
			BillTransferTool<AggDaySalaryVO> transferTool = new BillTransferTool<AggDaySalaryVO>(
					vos);
			AggDaySalaryVO[] mergedVO = transferTool.getClientFullInfoBill();

			// 调用BP
			AceWadaysalaryInsertBP action = new AceWadaysalaryInsertBP();
			AggDaySalaryVO[] retvos = action.insert(mergedVO);
			// 构造返回数据
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// 删除
	public void pubdeleteBills(AggDaySalaryVO[] vos) throws BusinessException {
		try {
			// 加锁 比较ts
			BillTransferTool<AggDaySalaryVO> transferTool = new BillTransferTool<AggDaySalaryVO>(
					vos);
			AggDaySalaryVO[] fullBills = transferTool.getClientFullInfoBill();
			AceWadaysalaryDeleteBP deleteBP = new AceWadaysalaryDeleteBP();
			deleteBP.delete(fullBills);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// 修改
	public AggDaySalaryVO[] pubupdateBills(AggDaySalaryVO[] vos)
			throws BusinessException {
		try {
			// 加锁 + 检查ts
			BillTransferTool<AggDaySalaryVO> transTool = new BillTransferTool<AggDaySalaryVO>(
					vos);
			// 补全前台VO
			AggDaySalaryVO[] fullBills = transTool.getClientFullInfoBill();
			// 获得修改前vo
			AggDaySalaryVO[] originBills = transTool.getOriginBills();
			// 调用BP
			AceWadaysalaryUpdateBP bp = new AceWadaysalaryUpdateBP();
			AggDaySalaryVO[] retBills = bp.update(fullBills, originBills);
			// 构造返回数据
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggDaySalaryVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggDaySalaryVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggDaySalaryVO> query = new BillLazyQuery<AggDaySalaryVO>(
					AggDaySalaryVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}
	
	public AggDaySalaryVO[] pubquerybills(IQueryScheme queryScheme,WaDayLoginContext context)
			throws BusinessException {
		AggDaySalaryVO[] bills = null;
		try {
			this.preQuery(queryScheme, context);
			BillLazyQuery<AggDaySalaryVO> query = new BillLazyQuery<AggDaySalaryVO>(
					AggDaySalaryVO.class);
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
	
	protected void preQuery(IQueryScheme queryScheme,WaDayLoginContext context) {
		String pk_hrorg=context.getPk_hrorg();
		UFLiteralDate caiculdate= context.getCalculdate();
		QueryScheme queryScheme2=(QueryScheme)queryScheme;
		FromWhereSQLImpl tablejoin=(FromWhereSQLImpl) queryScheme2.getTableJoinFromWhereSQL();
		String whereSql=tablejoin.getWhere();
		if(null==whereSql||"".equals(whereSql)){
			String condition=" wa_daysalary.pk_hrorg='"+pk_hrorg+"' and wa_daysalary.salarydate='"+caiculdate+"'";
			whereSql=condition;
		}else{
			String condition=" and wa_daysalary.pk_hrorg='"+pk_hrorg+"' and wa_daysalary.salarydate='"+caiculdate+"'";
			whereSql+=condition;
		}
		tablejoin.setWhere(whereSql);
		queryScheme2.putTableJoinFromWhereSQL(tablejoin);
		
	 }
	

}