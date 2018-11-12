package nc.bs.hrsms.ta.sss.away.ctrl;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;
/**
 * 店员出差申请列表界面的Win
 * 
 * @author shaochj
 * @date May 26, 2015
 * 
 */
public class ShopAwayApplyListWin  implements WindowController, Serializable {
	private static final long serialVersionUID = 7532916478964732880L;

	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}
}
