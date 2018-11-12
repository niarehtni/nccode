package nc.bs.hrsms.hi.deptpsn;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;

public class DeptPsnWinContr implements WindowController, Serializable {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final long ID = 5L;

	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

}
