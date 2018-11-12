package nc.impl.twhr;

import nc.impl.pub.ace.AceRangetablePubServiceImpl;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.bill.pagination.PaginationTransferObject;
import nc.vo.pubapp.bill.pagination.util.PaginationUtils;
import nc.impl.pubapp.pattern.data.bill.BillQuery;

public class RangetableMaintainImpl extends AceRangetablePubServiceImpl implements nc.itf.twhr.IRangetableMaintain {

      @Override
    public void delete(RangeTableAggVO[] vos) throws BusinessException {
        super.pubdeleteBills(vos);
    }
  
      @Override
    public RangeTableAggVO[] insert(RangeTableAggVO[] vos) throws BusinessException {
        return super.pubinsertBills(vos);
    }
    
      @Override
    public RangeTableAggVO[] update(RangeTableAggVO[] vos) throws BusinessException {
        return super.pubupdateBills(vos);    
    }
  

  @Override
  public RangeTableAggVO[] query(IQueryScheme queryScheme)
      throws BusinessException {
      return super.pubquerybills(queryScheme);
  }







}
