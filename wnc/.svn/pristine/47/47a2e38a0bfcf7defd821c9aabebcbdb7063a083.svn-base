package nc.impl.hrpub;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrpub.ioschema.rule.DataUniqueCheckRule;
import nc.bs.hrpub.ioschema.rule.DeleteRefCheckRule;
import nc.bs.hrpub.ioschema.rule.SetDefaultValueRule;
import nc.impl.pub.ace.AceIOSchemaPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.hrpub.IIOSchemaMaintain;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hrpub.mdmapping.IOSchemaVO;
import nc.vo.pub.BusinessException;

public class IOSchemaMaintainImpl extends AceIOSchemaPubServiceImpl implements
		IIOSchemaMaintain {

	@Override
	public IOSchemaVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO)
			throws BusinessException {
		new SetDefaultValueRule().process(new BatchOperateVO[] { batchVO });
		new DataUniqueCheckRule().process(new BatchOperateVO[] { batchVO });
		new DeleteRefCheckRule().process(new BatchOperateVO[] { batchVO });
		BatchSaveAction<IOSchemaVO> saveAction = new BatchSaveAction<IOSchemaVO>();
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}

	@Override
	public IOSchemaVO[] queryAll() throws BusinessException {
		IMDPersistenceQueryService service = ((IMDPersistenceQueryService) NCLocator
				.getInstance().lookup(IMDPersistenceQueryService.class));
		ArrayList<IOSchemaVO> ioschemaVOList = (ArrayList) service
				.queryBillOfVOByCond(IOSchemaVO.class, "1=1", true);
		return ioschemaVOList.toArray(new IOSchemaVO[0]);
	}
}
