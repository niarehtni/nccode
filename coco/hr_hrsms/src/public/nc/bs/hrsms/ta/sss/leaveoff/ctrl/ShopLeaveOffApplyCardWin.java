package nc.bs.hrsms.ta.sss.leaveoff.ctrl;

import java.io.Serializable;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;

public class ShopLeaveOffApplyCardWin implements WindowController, Serializable {
	private static final long serialVersionUID = -4469483833694796799L;

	public static final String WIN_ID ="ShopLeaveOffApplyCard";
	/**
	 * 窗口的关闭事件
	 * 
	 * @param event
	 */
	public void sysWindowClosed(PageEvent event) {
		// 关闭窗口回滚单据编码
		new ShopLeaveOffApplyCardView().rollBackBillCode();
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}

}
