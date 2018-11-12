package nc.impl.hrsms.ta.shift;

import nc.hr.frame.persistence.HrBatchService;
import nc.itf.hrsms.ta.shift.IStoreShiftQueryMaintain;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public class StoreShiftQueryMaintainImpl implements IStoreShiftQueryMaintain{

	private HrBatchService serviceTemplate;

	public static final String DOC_NAME = "8cc504ed-4371-43c6-81a0-8758684b436f";
	private HrBatchService getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new HrBatchService(DOC_NAME);
		}
		return serviceTemplate;
	}
	
	@Override
	public AggShiftVO queryByPk(String pk_shift) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByPk(AggShiftVO.class, pk_shift, false);
	}

	@Override
	public AggShiftVO[] queryByCondition(LoginContext context, String condition)
			throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByCondition(context, AggShiftVO.class, condition);
	}

	@Override
	public AggShiftVO[] queryByCondition(String condition)
			throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByCondition(AggShiftVO.class, condition);
	}

	@Override
	public AggShiftVO[] queryShiftVOByDept(String pk_dept)
			throws BusinessException {
		// TODO Auto-generated method stub
		String condition = " pk_dept = '"+pk_dept+"'";
		return queryByCondition(condition);
	}

}
