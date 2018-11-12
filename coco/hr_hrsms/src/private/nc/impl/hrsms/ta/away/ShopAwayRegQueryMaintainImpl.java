package nc.impl.hrsms.ta.away;

import nc.hr.frame.persistence.HrBatchService;
import nc.itf.hrsms.ta.away.IShopAwayRegQueryMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.uif2.LoginContext;

public class ShopAwayRegQueryMaintainImpl implements IShopAwayRegQueryMaintain{

	private HrBatchService serviceTemplate;

	public static final String DOC_NAME = "01ef452f-94c3-4d9d-9199-2066336bf968";
	private HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(DOC_NAME);
		}
		return serviceTemplate;
	}
	
	@Override
	public AwayRegVO queryByPk(String pk) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByPk(AwayRegVO.class, pk);
	}

	@Override
	public AwayRegVO[] queryByCondition(LoginContext context, String condition)
			throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByCondition(AwayRegVO.class, condition);
	}

}
