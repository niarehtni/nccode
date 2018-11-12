package nc.itf.hrsms.ta.overtime;

import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.uif2.LoginContext;

public interface IShopOvertimeRegQueryMaintain {
	
	/**
	 * 根据pk查询
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public OvertimeRegVO queryByPk(String pk)throws BusinessException;
	
	/**
	 * 根据条件查询
	 * @param context
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public OvertimeRegVO[] queryByCondition(LoginContext context,String condition)throws BusinessException;

}
