package nc.bs.hrsms.ta.sss.leaveoff.ctrl;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;
/**
 * 店员销假申请列表界面的Win
 * 
 * @author shaochj
 * @date May 14, 2015
 * 
 */
public class ShopLeaveOffApplyListWin implements WindowController, Serializable {

	private static final long serialVersionUID = -1334123853735802499L;


	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

}
