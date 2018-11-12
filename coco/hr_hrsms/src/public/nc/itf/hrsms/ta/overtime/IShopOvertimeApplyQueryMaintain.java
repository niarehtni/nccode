package nc.itf.hrsms.ta.overtime;

import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.uif2.LoginContext;

public interface IShopOvertimeApplyQueryMaintain {

	/**
	 * ����pk��ѯ
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO queryByPk(String pk)throws BusinessException;
	
	/**
	 * ����������ѯ
	 * @param context
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggOvertimeVO[] queryByCondition(LoginContext context,String condition)throws BusinessException;

}
