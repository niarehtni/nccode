package nc.ui.om.hrdept.action;

import javax.swing.KeyStroke;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrShowFilterDataAction;

public abstract class HrShowDeptAdjAbortAction extends HrShowFilterDataAction {
	public HrShowDeptAdjAbortAction() {
		String name = ResHelper.getString("deptadj", "deptadj_900");

		setBtnName(name);
		setCode("showAbort");

		putValue("AcceleratorKey", KeyStroke.getKeyStroke(87, 2));
		putValue("ShortDescription", name + "(Ctrl+W)");
	}
}
