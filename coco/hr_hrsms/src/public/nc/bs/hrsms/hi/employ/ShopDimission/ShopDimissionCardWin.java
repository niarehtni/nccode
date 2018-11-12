package nc.bs.hrsms.hi.employ.ShopDimission;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;

public class ShopDimissionCardWin implements WindowController, Serializable {
	  private static final long serialVersionUID=7532916478964732880L;
	  public void sysWindowClosed(  PageEvent event){  
		new DimissionCardMainView().rollBackBillCode();
	    LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	  }
}