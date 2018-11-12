package nc.bs.hrsms.ta.shift.ctrl;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;

public class StoreShiftCardWin implements WindowController, Serializable {
	private static final long serialVersionUID = 7532916478964732880L;

	/**
	 * 窗口的关闭事件
	 * 
	 * @param event
	 */
	public void sysWindowClosed(PageEvent event) {
		new StoreShiftCardViewCtrl().rollBackBillCode();
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

}
