package nc.bs.hrsms.ta.common.dft;

import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;

public class Init_StateManager extends AbstractStateManager {
	@Override
	public IStateManager.State getState(WebComponent target, LfwView widget) {
		Dataset ds = getCtrlDataset(widget);
		Row row = ds.getSelectedRow();
		if (row == null|| row.getContent()==null) {// 没有选中行
			return IStateManager.State.DISABLED;
		}
		return IStateManager.State.ENABLED;
	}

}
