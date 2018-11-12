package nc.bs.hrsms.ta.empleavereg4store.win;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;
/**
 * @author renyp
 * @date 2015-4-24
 * @ClassName功能名称：店员休假查询win控制类
 * @Description功能描述：功能是
 * 
 */
public class EmpLeaveReg4StoreWin implements WindowController, Serializable {
	private static final long serialVersionUID = 7532916478964732880L;
/**
 * @author renyp
 * @date 2015-4-24
 * @Description方法功能描述：作用是关闭窗口事件
 * 
 */
	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}
}
