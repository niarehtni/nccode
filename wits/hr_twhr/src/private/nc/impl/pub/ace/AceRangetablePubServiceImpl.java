package nc.impl.pub.ace;

import nc.bs.twhr.rangetable.ace.bp.AceRangetableDeleteBP;
import nc.bs.twhr.rangetable.ace.bp.AceRangetableInsertBP;
import nc.bs.twhr.rangetable.ace.bp.AceRangetableUpdateBP;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
		import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
		import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.bill.pagination.util.PaginationUtils;
import nc.impl.pubapp.pattern.data.bill.BillQuery;

public abstract class AceRangetablePubServiceImpl {
    //新增
    public RangeTableAggVO[] pubinsertBills(RangeTableAggVO[] vos) throws BusinessException {
        try {
          // 数据库中数据和前台传递过来的差异VO合并后的结果
          BillTransferTool<RangeTableAggVO> transferTool =new BillTransferTool<RangeTableAggVO>(vos);
          RangeTableAggVO[] mergedVO = transferTool.getClientFullInfoBill();

          // 调用BP
          AceRangetableInsertBP action = new AceRangetableInsertBP();
          RangeTableAggVO[] retvos = action.insert(mergedVO);
          // 构造返回数据
          return transferTool.getBillForToClient(retvos);
        }catch (Exception e) {
          ExceptionUtils.marsh(e);
        }
        return null;
    }
    //删除
    public void pubdeleteBills(RangeTableAggVO[] vos) throws BusinessException {
        try {
			          // 加锁 比较ts
          BillTransferTool<RangeTableAggVO> transferTool = new BillTransferTool<RangeTableAggVO>(vos);
          RangeTableAggVO[] fullBills = transferTool.getClientFullInfoBill();
          AceRangetableDeleteBP deleteBP = new AceRangetableDeleteBP();
          deleteBP.delete(fullBills);
        }catch (Exception e) {
          ExceptionUtils.marsh(e);
        }
    }
  	  //修改
    public RangeTableAggVO[] pubupdateBills(RangeTableAggVO[] vos) throws BusinessException {
        try {
    	      //加锁 + 检查ts
		          BillTransferTool<RangeTableAggVO> transTool = new BillTransferTool<RangeTableAggVO>(vos);
		          //补全前台VO
          RangeTableAggVO[] fullBills = transTool.getClientFullInfoBill();
	          //获得修改前vo
          RangeTableAggVO[] originBills = transTool.getOriginBills();
          // 调用BP
          AceRangetableUpdateBP bp = new AceRangetableUpdateBP();
          RangeTableAggVO[] retBills = bp.update(fullBills, originBills);
          // 构造返回数据
          retBills = transTool.getBillForToClient(retBills);
          return retBills;
        }catch (Exception e) {
          ExceptionUtils.marsh(e);
        }
          return null;
     }


  public RangeTableAggVO[] pubquerybills(IQueryScheme queryScheme)
      throws BusinessException {
    RangeTableAggVO[] bills = null;
    try {
      this.preQuery(queryScheme);
      BillLazyQuery<RangeTableAggVO> query =
          new BillLazyQuery<RangeTableAggVO>(RangeTableAggVO.class);
      bills = query.query(queryScheme, null);
    }
    catch (Exception e) {
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