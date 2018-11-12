package nc.itf.hrsms.ta.shift;

import nc.vo.bd.shift.AggShiftVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public interface IStoreShiftQueryMaintain {

	/**
	 * 根据班次主键查询班次
	 * @param pk_shift
	 * @return 
	 */
	public AggShiftVO queryByPk(String pk_shift) throws BusinessException;
	
	/**
	 * 在可见范围下根据条件查询
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggShiftVO[] queryByCondition(LoginContext context, String condition) throws BusinessException;
	
	/**
	 * 根据条件查询
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggShiftVO[] queryByCondition(String condition) throws BusinessException;
	
	/**
	 * 查询门店(部门)内的所有班次
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public AggShiftVO[] queryShiftVOByDept(String pk_dept)throws BusinessException;
}
