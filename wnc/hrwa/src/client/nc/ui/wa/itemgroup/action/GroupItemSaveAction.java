package nc.ui.wa.itemgroup.action;

import nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction;
import nc.vo.wa.itemgroup.AggItemGroupVO;

public class GroupItemSaveAction extends DifferentVOSaveAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -8838198331928906420L;

	@Override
	protected void beforeDoAction() {
		super.beforeDoAction();
		if (null != getEditor().getValue()) {
			AggItemGroupVO value = (AggItemGroupVO) getEditor().getValue();
			if (value.getParentVO() != null) {
				// 改为全局节点 tank 2019年10月17日15:06:20
				value.getParentVO().setPk_org("GLOBLE00000000000000");
				value.getParentVO().setPk_org_v("GLOBLE00000000000000");
			}
		}
	}
}
