package nc.impl.ta.overtime;

import nc.impl.pub.ace.AceSegdetailPubServiceImpl;
import nc.vo.ta.overtime.AggSegDetailVO;
import nc.itf.ta.overtime.ISegdetailMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;

public class SegdetailMaintainImpl extends AceSegdetailPubServiceImpl implements
		ISegdetailMaintain {

	@Override
	public void delete(AggSegDetailVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggSegDetailVO[] insert(AggSegDetailVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggSegDetailVO[] update(AggSegDetailVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggSegDetailVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

}
