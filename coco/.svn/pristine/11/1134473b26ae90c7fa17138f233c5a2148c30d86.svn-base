package nc.bs.hrsms.ta.SignReg.ctrl;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.event.PageEvent;

public class SignRegCardWinController {
  @SuppressWarnings("unused")
private static final long serialVersionUID=1L;
  @SuppressWarnings("unused")
private static final long ID=5L;
  /**
	 * 窗口的关闭事件
	 * 
	 * @param event
	 */
  public void sysWindowClosed(PageEvent event) {
	  //可能需要回滚数据；
	  
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}
}
