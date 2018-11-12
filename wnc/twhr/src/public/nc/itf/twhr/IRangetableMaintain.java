package nc.itf.twhr;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.pub.BusinessException;

public interface IRangetableMaintain {

    public void delete(RangeTableAggVO[] vos) throws BusinessException ;

    public RangeTableAggVO[] insert(RangeTableAggVO[] vos) throws BusinessException ;
  
    public RangeTableAggVO[] update(RangeTableAggVO[] vos) throws BusinessException ;


    public RangeTableAggVO[] query(IQueryScheme queryScheme)
      throws BusinessException;

}
