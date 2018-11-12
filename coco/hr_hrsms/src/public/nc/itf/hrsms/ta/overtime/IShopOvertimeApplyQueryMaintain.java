package nc.itf.hrsms.ta.overtime;

import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.uif2.LoginContext;

public interface IShopOvertimeApplyQueryMaintain {

	/**
	 * 根据pk查询
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO queryByPk(String pk)throws BusinessException;
	
	/**
	 * 根据条件查询
	 * @param context
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] queryByCondition(LoginContext context,String condition)throws BusinessException;

}
