package nc.itf.hr.wa;

import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;


/**
 * 服务接口
 * 
 * @author: xuhw
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public interface ITaxSpecialStatisticsManageService {

	void lockData(WaLoginContext context, String condition,String orderSQL) throws BusinessException;
	void unlockData(WaLoginContext context, String condition,String orderSQL) throws BusinessException;
	/**
	 * 生成数据
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @throws BusinessException 
	 */
	public void genData(WaLoginContext context, String condition,String orderSQL) throws BusinessException;
}
