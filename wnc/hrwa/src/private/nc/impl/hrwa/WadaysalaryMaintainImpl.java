package nc.impl.hrwa;

import nc.impl.pub.ace.AceWadaysalaryPubServiceImpl;
import nc.vo.wa.paydata.AggDaySalaryVO;
import nc.vo.wa.pub.WaDayLoginContext;
import nc.itf.hrwa.IWadaysalaryMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;

public class WadaysalaryMaintainImpl extends AceWadaysalaryPubServiceImpl implements
		IWadaysalaryMaintain {

	@Override
	public void delete(AggDaySalaryVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggDaySalaryVO[] insert(AggDaySalaryVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggDaySalaryVO[] update(AggDaySalaryVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggDaySalaryVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggDaySalaryVO[] query(IQueryScheme queryScheme,
			WaDayLoginContext context) throws BusinessException {
		return super.pubquerybills(queryScheme,context);
	}

}
