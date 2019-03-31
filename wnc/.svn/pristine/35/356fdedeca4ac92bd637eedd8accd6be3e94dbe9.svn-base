package nc.impl.hrwa;

import nc.impl.pub.ace.AceWaitemgroupPubServiceImpl;
import nc.itf.hrwa.IWaitemgroupMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.wa.itemgroup.AggItemGroupVO;

public class WaitemgroupMaintainImpl extends AceWaitemgroupPubServiceImpl implements IWaitemgroupMaintain {

	@Override
	public void delete(AggItemGroupVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggItemGroupVO[] insert(AggItemGroupVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggItemGroupVO[] update(AggItemGroupVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggItemGroupVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

}
