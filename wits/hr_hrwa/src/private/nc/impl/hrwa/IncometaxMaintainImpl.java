package nc.impl.hrwa;

import nc.impl.pub.ace.AceIncometaxPubServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.itf.hrwa.IIncometaxMaintain;
import nc.vo.pub.BusinessException;

public class IncometaxMaintainImpl extends AceIncometaxPubServiceImpl
		implements IIncometaxMaintain {

	@Override
	public void delete(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggIncomeTaxVO[] insert(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggIncomeTaxVO[] update(AggIncomeTaxVO[] clientFullVOs,
			AggIncomeTaxVO[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggIncomeTaxVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

}
