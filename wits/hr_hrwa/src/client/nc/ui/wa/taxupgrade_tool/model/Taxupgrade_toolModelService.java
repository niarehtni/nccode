package nc.ui.wa.taxupgrade_tool.model;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.ITaxupgrade_toolModelService;
import nc.itf.hr.wa.ITaxupgrade_toolQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;

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
public class Taxupgrade_toolModelService implements IAppModelService {

	public Taxupgrade_toolModelService() {
	}

	private ITaxupgrade_toolModelService getService(){
		return (ITaxupgrade_toolModelService)NCLocator.getInstance().lookup(ITaxupgrade_toolModelService.class.getName());
	}

	private ITaxupgrade_toolQueryService getQueryService(){
		return (ITaxupgrade_toolQueryService)NCLocator.getInstance().lookup(ITaxupgrade_toolQueryService.class.getName());
	}
	public void initTaxItems4Group(String pk_group) throws BusinessException {
		// TODO Auto-generated method stub
	}

	public void initTaxItems4ClassItems(String pk_group, String pk_wa_class, String year, String month) throws BusinessException {
		// TODO Auto-generated method stub
		getService().initTaxItems4ClassItems(pk_group, pk_wa_class, year, month);
	}

	public void copyTaxItemsToAnotherWaClasss(WaLoginContext sourceContext, List<WaLoginContext> targetContextList) throws BusinessException {
		// TODO Auto-generated method stub
		getService().copyTaxItemsToAnotherWaClasss(sourceContext.getPk_group() , sourceContext, targetContextList);
	}
	public void taxUpgradeToSelectClass(String pk_group, WaItemVO[] waitemvos, GeneralVO[] selectClassVOs, String opType) throws BusinessException {
		// TODO Auto-generated method stub
		getService().taxUpgradeToSelectClass(pk_group, waitemvos, selectClassVOs, opType);
	}
	@Override
	public Object insert(Object object) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Object object) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public WaItemVO[] queryTaxItemVOs(String pk_group) throws BusinessException{
		return getQueryService().queryTaxItems(pk_group);
	}
	
	public WaItemVO[] queryTaxUpgradeItems(String pk_group) throws BusinessException{
		return getQueryService().queryTaxUpgradeItems(pk_group);
	}
	
	public GeneralVO[] queryHasInitClassVOs(String pk_group) throws BusinessException{
		return getQueryService().queryHasCopyClassInfo(pk_group);
	}
	
	public GeneralVO[] queryUnInitClassVOs(String pk_group) throws BusinessException{
		return getQueryService().queryTargetClassInfo(pk_group);
	}
}
