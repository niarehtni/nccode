package nc.bs.hrsms.hi.employ;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;

public class PsnEmployWinController implements WindowController, Serializable {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final long ID = 5L;

	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

}
