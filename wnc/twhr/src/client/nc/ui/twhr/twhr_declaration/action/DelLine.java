package nc.ui.twhr.twhr_declaration.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterRowEditEvent;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;

public class DelLine extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -8022266589610032247L;
	private ShowUpableBillForm billForm;

	public DelLine() {
		putValue("Code", "delLineAction");
		setBtnName("Ñh––");

		putValue("ShortDescription", "Ñh––" + "(Ctrl+D)");
		putValue("AcceleratorKey", javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		boolean autoAddLine = getBillForm().getBillCardPanel().getBodyPanel().isAutoAddLine();
		getBillForm().getBillCardPanel().getBodyPanel().setAutoAddLine(false);
		try {
			boolean doOperateFlag = getBillForm().getBillCardPanel().doLineAction(
					getBillForm().getBillCardPanel().getCurrentBodyTableCode(), 2);

			if (doOperateFlag) {
				int[] rows = { -1 };
				getModel().fireEvent(
						new CardBodyAfterRowEditEvent(getBillForm().getBillCardPanel(),
								nc.ui.pubapp.uif2app.event.card.BodyRowEditType.DELLINE, rows));
			}
		} catch (Exception e) {
			nc.vo.pubapp.pattern.exception.ExceptionUtils.wrappException(e);
		} finally {
			getBillForm().getBillCardPanel().getBodyPanel().setAutoAddLine(autoAddLine);
		}
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

}
