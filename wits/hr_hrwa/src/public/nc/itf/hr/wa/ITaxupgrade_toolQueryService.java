package nc.itf.hr.wa;

import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.item.WaItemVO;
/**
 * 税改快速试试工具
 * @author: xuhw 
 * @date: 2018-12-04  
 * @since: eHR V6.5
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
public interface ITaxupgrade_toolQueryService
{
	/**
	 * 查询所有待复制方案
	 * unInclude_pk_wa_class - 排除方案
	 * @return
	 * @throws BusinessException 
	 */
	GeneralVO[] queryTargetClassInfo(String pk_group) throws BusinessException;
	
	WaItemVO[] queryTaxItems(String pk_group) throws BusinessException;
	
	/**
	 * 查询某个集团的初始化过的税改项目
	 * 
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	public WaItemVO[] queryTaxUpgradeItems(String pk_group) throws BusinessException;
	
	/**
	 * 获取税改项目
	 * 初始5个项目
	 * 
	 */
	WaItemVO[] getInitTaxItems(String pk_group) throws BusinessException;
	
	/**
	 * 查询已经升级完的方案
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	GeneralVO[] queryHasCopyClassInfo(String pk_group) throws BusinessException;
}