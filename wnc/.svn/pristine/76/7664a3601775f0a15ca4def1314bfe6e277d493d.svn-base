package nc.impl.hrpub;

import nc.impl.pub.ace.AceIopermitPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.vo.bd.meta.BatchOperateVO;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.hrpub.mdmapping.IOPermitVO;
import nc.itf.hrpub.IIopermitMaintain;

public class IopermitMaintainImpl extends AceIopermitPubServiceImpl
		implements IIopermitMaintain {

	@Override
	public IOPermitVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<IOPermitVO> saveAction = new BatchSaveAction<IOPermitVO>();
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}
}
