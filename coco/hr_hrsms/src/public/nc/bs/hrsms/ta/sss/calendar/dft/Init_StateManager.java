package nc.bs.hrsms.ta.sss.calendar.dft;

import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;
/**
  * ��ʼ״̬�Ĺ�����<br>
 * (�����ڰ�ʱ�β鿴�ĵ���/�������ఴť�� �������鿴�ĵ��ఴť)
 * @author shaochj
 *
 */
public class Init_StateManager extends AbstractStateManager {

	@Override
	public IStateManager.State getState(WebComponent target, LfwView widget) {
		Dataset ds = getCtrlDataset(widget);
		if (ds == null) {
			return IStateManager.State.DISABLED;
		}
		if (ds.isEnabled()) {
			return IStateManager.State.HIDDEN;
		} else {
			int count = ds.getRowCount();
			if (count > 0) {
				return IStateManager.State.ENABLED_VISIBLE;
			} else {
				return IStateManager.State.DISABLED;
			}
		}

	}
}
