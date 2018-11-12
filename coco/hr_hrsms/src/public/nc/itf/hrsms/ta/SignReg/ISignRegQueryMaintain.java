package nc.itf.hrsms.ta.SignReg;

import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignRegVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public interface ISignRegQueryMaintain {

	/**
	 * 根据签卡登记主键查询签卡
	 * @param pk_signreg
	 * @return 
	 */
	public AggSignVO queryByPk(String pk_signreg) throws BusinessException;
	
	/**
	 * 在可见范围下根据条件查询
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggSignVO[] queryByCondition(LoginContext context, String condition) throws BusinessException;
	
	/**
	 * 根据条件、日期范围、查询人员，
	 * 只要符合条件的人员在日期范围内有满足签卡原因的签卡记录，则此人应返回
	 * @param context
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param signstatus :签卡状态
	 * @return
	 */
	public SignRegVO[] queryVOsByCondition(LoginContext context,String condition) throws BusinessException;
}
