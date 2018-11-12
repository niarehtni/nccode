package nc.bs.hrsms.hi.employ.ShopTransfer;

import java.io.Serializable;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;

public class ShopTransferCardWinController implements WindowController, Serializable {
	  private static final long serialVersionUID=7532916478964732880L;
	  public void sysWindowClosed(  PageEvent event){
		  new TransferCardMainView().rollBackBillCode();
	      LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	  }
}
