package nc.impl.hrsms.ta.leaveoff;

import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hrsms.ta.leaveoff.IShopLeaveOffApplyQueryMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.uif2.LoginContext;

public class ShopLeaveOffApplyQueryMaintainImpl implements IShopLeaveOffApplyQueryMaintain{

	private SimpleDocServiceTemplate serviceTemplate;
	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate("99bb6604-fb63-471f-b598-20a0e430a5a9");
		}
		return serviceTemplate;
	}
	
	@Override
	public AggLeaveoffVO queryByPk(String pk) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByPk(AggLeaveoffVO.class, pk);
	}

	@Override
	public AggLeaveoffVO[] queryByCondition(LoginContext context,
			String condition) throws BusinessException {
		// TODO Auto-generated method stub
		return getServiceTemplate().queryByCondition(context, AggLeaveoffVO.class, condition);
	}

}
