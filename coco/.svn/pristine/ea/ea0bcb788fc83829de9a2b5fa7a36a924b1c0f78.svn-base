package nc.itf.hrsms.ta.away;

import nc.vo.pub.BusinessException;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.uif2.LoginContext;

public interface IShopAwayRegQueryMaintain {

	/**
	 * 根据pk查询
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AwayRegVO queryByPk(String pk)throws BusinessException;
	
	/**
	 * 根据条件查询
	 * @param context
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AwayRegVO[] queryByCondition(LoginContext context,String condition)throws BusinessException;

}
