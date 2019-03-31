package nc.ui.twhr.rangetable.action;

import java.awt.event.ActionEvent;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.pubapp.uif2app.actions.CopyAction;

public class RangetableCopyAction extends CopyAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 798057105032502881L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// MOD 补充校验用默认组织by Andy on 2019-01-15
		String pk_org = WorkbenchEnvironment.getInstance().getLoginUser().getPk_org();
		getModel().getContext().setPk_org(pk_org);

		super.doAction(e);

	}

}
