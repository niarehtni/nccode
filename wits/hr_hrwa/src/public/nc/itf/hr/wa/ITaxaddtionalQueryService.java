package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

/**
 * 附加费用扣除
 */
public interface ITaxaddtionalQueryService {

	/**
	 * 查询
	 * 
	 * @param context
	 *            WaLoginContext
	 * @param sqlWhere
	 *            String
	 * @param orderby
	 *            String
	 * @return PsntaxVO[]
	 * @throws nc.vo.pub.BusinessException
	 */
	WASpecialAdditionaDeductionVO[] queryDataByCondition(LoginContext context, String sqlWhere, String orderby)
			throws BusinessException;

	/**
	 * 查询数据主键
	 * 
	 * @param context
	 * @param sqlWhere
	 * @param orderby
	 * @return
	 */
	String[] queryVOsByCondition(LoginContext context, String sqlWhere, String orderby);
	

	/**
	 * 根据主键查询数据
	 * 
	 * @param pks
	 * @return
	 * @throws BusinessException 
	 */
	public WASpecialAdditionaDeductionVO[] queryDataByPKs(String[] pks) throws BusinessException;
	
	/**
	 * 查询数据主键
	 * 
	 * @param pks
	 * @return
	 * @throws BusinessException 
	 */
	public String[] queryDataPksByCondition(LoginContext context, String sqlWhere, String orderby) throws BusinessException;
	
	public WASpecialAdditionaDeductionVO[] exportData(LoginContext context, String sqlWhere, String orderby, String taxyear) throws BusinessException;

}