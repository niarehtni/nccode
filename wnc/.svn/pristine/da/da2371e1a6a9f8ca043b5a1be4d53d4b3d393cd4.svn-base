package nc.ui.wa.othersource.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IOtherSourceManageService;
import nc.ui.hr.uif2.model.HrDefaultAppModelService;
import nc.vo.wa.othersource.OtherSourceVO;

public class OtherSourceModelService extends HrDefaultAppModelService{
	 private IOtherSourceManageService service;
	 private IOtherSourceManageService getService()
	    {
	        if (service == null)
	        {
	            service = NCLocator.getInstance().lookup(IOtherSourceManageService.class);
	        }
	        return service;
	    }
	 @Override
	    public void delete(Object object) throws Exception
	    {
	        getService().delete((OtherSourceVO)object);
	        
	    }
	  @Override
	    public Object insert(Object object) throws Exception
	    {
	        
	        return getService().insert((OtherSourceVO)object);
	    }
	 
	  @Override
	    public Object update(Object object) throws Exception
	    {
	        return getService().update((OtherSourceVO)object);
	    }
}
