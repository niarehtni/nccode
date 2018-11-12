package nc.bs.hrsms.hi.employ;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.event.PageEvent;

public class EmployeeInfoWinWinController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final long ID = 5L;

	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

}
