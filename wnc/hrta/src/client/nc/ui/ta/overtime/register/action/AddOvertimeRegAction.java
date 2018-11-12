package nc.ui.ta.overtime.register.action;

import java.awt.event.ActionEvent;

import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.uif2.action.AddAction;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * 加班登记 新增action
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class AddOvertimeRegAction extends AddAction {
	@Override
	public void doAction(ActionEvent evt) throws Exception {
		super.doAction(evt);

		// MOD(台灣新法令) ssx added on 2018-05-29
		UFBoolean isEnabled;
		try {
			isEnabled = new UFBoolean(SysInitQuery.getParaString(getContext().getPk_org(), "TBMOTSEG"));
			if (isEnabled != null && isEnabled.booleanValue()) {
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEdit(true);
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEnabled(true);
			} else {
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEdit(false);
				this.getFormEditor().getBillCardPanel().getHeadItem("istorest").setEnabled(false);
			}

		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		// end
	}
}
