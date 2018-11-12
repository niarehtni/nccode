package nc.itf.twhr;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.pub.BusinessException;
import nc.itf.pubapp.pub.smart.ISmartService;

public interface IBasedocMaintain extends ISmartService{
    public BaseDocVO[] query(IQueryScheme queryScheme)
      throws BusinessException, Exception;
}
