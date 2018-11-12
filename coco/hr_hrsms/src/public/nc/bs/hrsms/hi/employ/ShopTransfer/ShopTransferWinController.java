package nc.bs.hrsms.hi.employ.ShopTransfer;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import java.io.Serializable;
public class ShopTransferWinController implements WindowController, Serializable {
  private static final long serialVersionUID=7532916478964732880L;
  public void sysWindowClosed(  PageEvent event){
    LfwRuntimeEnvironment.getWebContext().destroyWebSession();
  }
}
