package nc.bs.hrsms.ta.sss.overtime.ctrl;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;
/**
 * 店员加班登记列表的Window
 * 
 * @author shaochj
 * @date May 20, 2015
 * 
 */
public class ShopOverTimeRegListWin implements WindowController, Serializable {
	private static final long serialVersionUID = 7532916478964732880L;

	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

}
