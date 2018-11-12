package nc.bs.hrsms.ta.SignReg.common;

import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;

/**
 * 按钮处理时间
 * @author mayif
 * @date may 5, 2015
 */
public class Init_StateManager extends AbstractStateManager{

	@Override
	public State getState(WebComponent target, LfwView widget) {
		// TODO Auto-generated method stub
		if ("list_add".equals(target.getId())||"batch_add".equals(target.getId())) {// 新增按钮
			return IStateManager.State.ENABLED;
		}
		Dataset ds = getCtrlDataset(widget);
		Row row = ds.getSelectedRow();
		if (row == null|| row.getContent()==null) {// 没有选中行
			return IStateManager.State.DISABLED;
		}
		return IStateManager.State.DISABLED;
		// 其他按钮
		//return IStateManager.State.ENABLED;
	}

}
