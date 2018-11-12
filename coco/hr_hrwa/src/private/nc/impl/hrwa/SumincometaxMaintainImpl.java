package nc.impl.hrwa;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.impl.pub.ace.AceSumincometaxPubServiceImpl;
import nc.itf.hrwa.ISumincometaxMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.hrwa.sumincometax.SumIncomeTaxVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class SumincometaxMaintainImpl extends AceSumincometaxPubServiceImpl implements
		ISumincometaxMaintain {

	@Override
	public void delete(AggSumIncomeTaxVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
	}

	@Override
	public AggSumIncomeTaxVO[] insert(AggSumIncomeTaxVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos);
	}

	@Override
	public AggSumIncomeTaxVO[] update(AggSumIncomeTaxVO[] vos) throws BusinessException {
		return super.pubupdateBills(vos);
	}

	@Override
	public AggSumIncomeTaxVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}
	
}
