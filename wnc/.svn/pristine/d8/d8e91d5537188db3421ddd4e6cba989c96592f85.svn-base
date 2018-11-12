package nc.impl.hrpub;

import nc.bs.hrpub.mdmapping.rule.DataUniqueCheckRule;
import nc.impl.pub.ace.AceMDMappingPubServiceImpl;
import nc.itf.hrpub.IMDMappingMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.pub.BusinessException;

public class MDMappingMaintainImpl extends AceMDMappingPubServiceImpl implements
		IMDMappingMaintain {

	@Override
	public void delete(AggMDClassVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggMDClassVO[] insert(AggMDClassVO[] vos) throws BusinessException {
		new DataUniqueCheckRule().process(vos);
		return super.pubinsertBills(vos);
	}

	@Override
	public AggMDClassVO[] update(AggMDClassVO[] vos) throws BusinessException {
		new DataUniqueCheckRule().process(vos);
		return super.pubupdateBills(vos);
	}

	@Override
	public AggMDClassVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

}
