package nc.impl.twhr;

import nc.impl.pub.ace.AceAllowancePubServiceImpl;
import nc.bs.twhr.allowance.ace.rule.DataUniqueCheckRule;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.allowance.AllowanceVO;

public class AllowanceMaintainImpl extends AceAllowancePubServiceImpl implements nc.itf.twhr.IAllowanceMaintain {

  @Override
  public AllowanceVO[] query(IQueryScheme queryScheme)
      throws BusinessException {
      return super.pubquerybasedoc(queryScheme);
  }


  @Override
  public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
    //调用编码、名称唯一性校验规则
    new DataUniqueCheckRule().process(new BatchOperateVO[] {
      batchVO    });
	BatchSaveAction<AllowanceVO> saveAction = new BatchSaveAction<AllowanceVO>();
	BatchOperateVO retData = saveAction.batchSave(batchVO);
    return retData;
  }
}
