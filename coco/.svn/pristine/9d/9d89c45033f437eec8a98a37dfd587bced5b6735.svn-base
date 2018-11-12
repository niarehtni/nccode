package nc.bs.hrsms.ta.sss.calendar.dft;

import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;

/**
 * 修改状态的管理器(保存/取消按钮)
 * 
 * @author shaochj
 * 
 */
public class Edit_StateManager extends AbstractStateManager {

	@Override
	public IStateManager.State getState(WebComponent target, LfwView widget) {
		Dataset ds = getCtrlDataset(widget);
		if (ds == null) {
			return IStateManager.State.HIDDEN;
		}
		int count = ds.getRowCount();
		if (ds.isEnabled() && count > 0) {
			return IStateManager.State.ENABLED_VISIBLE;
		} else {
			return IStateManager.State.HIDDEN;
		}

	}

}
