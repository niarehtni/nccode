package nc.impl.pub.ace;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.twhr.rangetable.ace.bp.AceRangetableDeleteBP;
import nc.bs.twhr.rangetable.ace.bp.AceRangetableInsertBP;
import nc.bs.twhr.rangetable.ace.bp.AceRangetableUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QueryCondition;
import nc.vo.twhr.rangetable.RangeTableAggVO;


public abstract class AceRangetablePubServiceImpl  {
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
	  	//全局节点,将查询条件的组织设置为全局,如果没有组织,则把组织设为全局
	  
	  queryScheme.put("where", checkOrgSql(queryScheme.getWhereSQLOnly()));
	  FromWhereSQLImpl fws =  (FromWhereSQLImpl)queryScheme.get("tablejoin");
	 
	  fws.setFrom(queryScheme.getTableJoinFromWhereSQL().getFrom());
	  fws.setWhere(checkOrgSql(queryScheme.getTableJoinFromWhereSQL().getWhere()));
	  queryScheme.put("tablejoin", fws);
	  
	  FromWhereSQLImpl fwsList =  (FromWhereSQLImpl)queryScheme.get("tablelist");
	  fwsList.setFrom(queryScheme.getTableListFromWhereSQL().getFrom());
	  fwsList.setWhere(checkOrgSql(queryScheme.getTableListFromWhereSQL().getWhere()));
			  
	  queryScheme.put("tablelist", fwsList);
	  Map<String, QueryCondition> conds = (Map<String, QueryCondition>) queryScheme.get("all_condition");
		for (Entry<String, QueryCondition> cond : conds.entrySet()) {
			String strKey = cond.getKey();
			QueryCondition condition = cond.getValue();
			if (strKey.equals("pk_org")) {
				if (condition.getValues() != null && condition.getValues().length > 0) {
					condition.getValues()[0] = "GLOBLE00000000000000";
				}
			}
		}
	  
	  
	  
	  
	      /* FromWhereSQL fromWhereSQL = queryScheme.getTableJoinFromWhereSQL();
	         StringBuffer sql = new StringBuffer();
	         sql.append(" from ");
	         if (fromWhereSQL.getFrom() != null) {
	           sql.append(fromWhereSQL.getFrom());
	            }
	         sql.append(" ");
	         sql.append(getSelectedFromPart());
	         sql.append(" ");
	         sql.append(getAddedFromPart());
	         sql.append(" where ");
	         if (fromWhereSQL.getWhere() != null) {
	           sql.append(fromWhereSQL.getWhere());
	            }
	         sql.append(" ");
	         sql.append(getSelectedWherePart());
	         sql.append(" ");
	         sql.append(getAddedWherePart());
	         return sql.toString();*/
  }
  /**
   * 将原有的组织数据替换成全局的数据
   * @param sql
   * @return
   * @author Ares.Tank 
   * @date 2018年9月28日 下午8:56:03
   * @description
   */
	private String checkOrgSql(String sql) {
		if(null == sql){
			return sql;
		}

		String result = null;
		
		int localNum = sql.indexOf("pk_org = '");
		if(localNum <0&&0==sql.length()){
			sql += " pk_org = 'GLOBLE00000000000000' ";
			return sql ;
		}else if(localNum <0&&0<sql.length()){
			sql += " and pk_org = 'GLOBLE00000000000000' ";
			return sql ;
		}
		//sql += " pk_org = 'GLOBLE00000000000000' ";
		result = sql.substring(0,localNum+10)  + "GLOBLE00000000000000";
		for(int i = localNum + 10; i <sql.length() ;i++ ){
			if('\'' == sql.charAt(i)){
				result +=sql.substring(i);
				break;
			}
		}
		return result;
	}
	
	
	
	
	
	

}