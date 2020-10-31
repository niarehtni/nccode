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
				// ��Ϊȫ�ֽڵ� tank 2019��10��17��15:06:20
				value.getParentVO().setPk_org("GLOBLE00000000000000");
				value.getParentVO().setPk_org_v("GLOBLE00000000000000");
			}
		}
	}
}
