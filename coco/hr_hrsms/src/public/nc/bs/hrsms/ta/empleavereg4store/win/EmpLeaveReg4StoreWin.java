package nc.bs.hrsms.ta.empleavereg4store.win;

import java.io.Serializable;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.ctrl.WindowController;
import nc.uap.lfw.core.event.PageEvent;
/**
 * @author renyp
 * @date 2015-4-24
 * @ClassName�������ƣ���Ա�ݼٲ�ѯwin������
 * @Description����������������
 * 
 */
public class EmpLeaveReg4StoreWin implements WindowController, Serializable {
	private static final long serialVersionUID = 7532916478964732880L;
/**
 * @author renyp
 * @date 2015-4-24
 * @Description�������������������ǹرմ����¼�
 * 
 */
	public void sysWindowClosed(PageEvent event) {
		LfwRuntimeEnvironment.getWebContext().destroyWebSession();
	}
}
