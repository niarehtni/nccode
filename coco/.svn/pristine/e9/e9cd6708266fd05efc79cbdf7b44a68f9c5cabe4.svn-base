package nc.impl.twhr;

import nc.impl.pub.ace.AceBasedocPubServiceImpl;
import nc.bs.twhr.basedoc.ace.rule.DataUniqueCheckRule;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.basedoc.BaseDocVO;

public class BasedocMaintainImpl extends AceBasedocPubServiceImpl implements nc.itf.twhr.IBasedocMaintain {

  @Override
  public BaseDocVO[] query(IQueryScheme queryScheme)
      throws BusinessException {
      return super.pubquerybasedoc(queryScheme);
  }


  @Override
  public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
    //调用编码、名称唯一性校验规则
    new DataUniqueCheckRule().process(new BatchOperateVO[] {
      batchVO    });
	BatchSaveAction<BaseDocVO> saveAction = new BatchSaveAction<BaseDocVO>();
	BatchOperateVO retData = saveAction.batchSave(batchVO);
    return retData;
  }
}
