package nc.itf.twhr;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.twhr.allowance.AllowanceVO;
import nc.vo.pub.BusinessException;
import nc.itf.pubapp.pub.smart.ISmartService;

public interface IAllowanceMaintain extends ISmartService{
    public AllowanceVO[] query(IQueryScheme queryScheme)
      throws BusinessException, Exception;
}
