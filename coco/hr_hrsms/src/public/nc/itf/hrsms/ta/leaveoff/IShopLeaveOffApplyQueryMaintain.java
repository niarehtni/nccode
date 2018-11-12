package nc.itf.hrsms.ta.leaveoff;

import nc.vo.pub.BusinessException;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.uif2.LoginContext;

public interface IShopLeaveOffApplyQueryMaintain {

	/**
	 * 根据pk查询
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveoffVO queryByPk(String pk)throws BusinessException;
	
	/**
	 * 根据条件查询
	 * @param context
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggLeaveoffVO[] queryByCondition(LoginContext context,String condition)throws BusinessException;

}
