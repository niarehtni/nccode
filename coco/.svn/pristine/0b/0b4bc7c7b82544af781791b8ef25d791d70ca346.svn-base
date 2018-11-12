package nc.impl.hrsms.ta.overtime;

import nc.hr.frame.persistence.HrBatchService;
import nc.itf.hrsms.ta.overtime.IShopOvertimeApplyQueryMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.uif2.LoginContext;

public class ShopOvertimeApplyQueryMaintainImpl implements IShopOvertimeApplyQueryMaintain{

	
	private HrBatchService serviceTemplate;

	public static final String DOC_NAME = IMetaDataIDConst.OVERTIME;
	private HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(DOC_NAME);
		}
		return serviceTemplate;
	}
	
	@Override
	public AggOvertimeVO queryByPk(String pk) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByPk(AggOvertimeVO.class, pk);
	}

	@Override
	public AggOvertimeVO[] queryByCondition(LoginContext context,
			String condition) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByCondition(context, AggOvertimeVO.class, condition);
	}

}
