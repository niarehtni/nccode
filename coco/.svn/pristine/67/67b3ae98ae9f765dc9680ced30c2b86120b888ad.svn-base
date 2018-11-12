package nc.impl.hrsms.ta.overtime;

import nc.hr.frame.persistence.HrBatchService;
import nc.itf.hrsms.ta.overtime.IShopOvertimeRegQueryMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.uif2.LoginContext;

public class ShopOvertimeRegQueryMaintainImpl implements IShopOvertimeRegQueryMaintain{

	private HrBatchService serviceTemplate;

	public static final String DOC_NAME = "d83e3326-c2db-44e5-9cd6-a12bdb786a2a";
	private HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(DOC_NAME);
		}
		return serviceTemplate;
	}
	
	@Override
	public OvertimeRegVO queryByPk(String pk) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByPk(OvertimeRegVO.class, pk);
	}

	@Override
	public OvertimeRegVO[] queryByCondition(LoginContext context,
			String condition) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByCondition(OvertimeRegVO.class, condition);
	}

}
