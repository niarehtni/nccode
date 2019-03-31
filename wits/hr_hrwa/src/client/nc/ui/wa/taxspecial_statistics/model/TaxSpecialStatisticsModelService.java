package nc.ui.wa.taxspecial_statistics.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.ITaxSpecialStatisticsManageService;
import nc.itf.hr.wa.ITaxSpecialStatisticsQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 税改专项附加扣除计算ModelService
 * 
 * @author: xuhw 
 * @since: eHR V6.5
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期:
 */
public class TaxSpecialStatisticsModelService implements IAppModelService,IPaginationQueryService {

	private ITaxSpecialStatisticsManageService mainService;
	private ITaxSpecialStatisticsQueryService queryService;
	
	public ITaxSpecialStatisticsManageService getMainService(){
		if(mainService==null){
			mainService = NCLocator.getInstance().lookup(ITaxSpecialStatisticsManageService.class);
		}
		return mainService;
	}
	public ITaxSpecialStatisticsQueryService getQueryService(){
		if(queryService==null){
			queryService = NCLocator.getInstance().lookup(ITaxSpecialStatisticsQueryService.class);
		}
		return queryService;
	}
	@Override
	public void delete(Object object) throws Exception {
	}

	@Override
	public Object insert(Object object) throws Exception {
		return null;
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		return null;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return getQueryService().queryVOByPks(pks);
	}
	
	/**
	 * 生成数据
	 * @param context
	 * @param condition
	 * @param orderSQL
	 * @throws BusinessException 
	 */
	public void genData(WaLoginContext context, String condition,String orderSQL) throws BusinessException{
		getMainService().genData(context, condition, orderSQL);
	}
	
	/**
	 * 锁定
	 * 只有被锁定的薪资数据才能获取
	 * 按照方案
	 * @throws BusinessException 
	 */
	public void lockData(WaLoginContext context, String condition,String orderSQL) throws BusinessException{
		getMainService().lockData(context, condition, orderSQL);
	}
	
	
	/**
	 * 接触锁定
	 * 按照方案
	 * @throws BusinessException 
	 */
	public void unLockData(WaLoginContext context, String condition,String orderSQL) throws BusinessException{
		getMainService().unlockData(context, condition, orderSQL);
	}
}
