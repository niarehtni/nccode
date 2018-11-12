package nc.ui.twhr.rangetable.ace.handler;

import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.ui.twhr.rangetable.action.RangetableGenerateAction;

/**
 * 单据表头表尾字段编辑后事件处理类
 * 
 * @since 6.0
 * @version 2011-7-7 下午02:52:22
 * @author duy
 */
public class AceHeadTailAfterEditHandler implements
		IAppEventHandler<CardHeadTailAfterEditEvent> {
	private RangetableGenerateAction genAction;

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		if (e.getKey().equals("tabletype")) {
			BillItem type = e.getBillCardPanel().getHeadItem("tabletype");
			if (type == null || type.getValueObject() == null // ssx remarked
																// for Open Tax
																// Range Table
																// Generate: ||
																// (Integer)
																// type.getValueObject()
																// == 4
			) {
				genAction.setEnabled(false);
				e.getBillCardPanel().getBodyItem("employeeamount")
						.setEnabled(false);
				e.getBillCardPanel().getBodyItem("employeramount")
						.setEnabled(false);
				e.getBillCardPanel().getBodyItem("rangerate").setEnabled(true);
				e.getBillCardPanel().getBodyItem("rangerevise")
						.setEnabled(true);
			} else {
				genAction.setEnabled(true);
				e.getBillCardPanel().getBodyItem("employeeamount")
						.setEnabled(true);
				e.getBillCardPanel().getBodyItem("employeramount")
						.setEnabled(true);
				e.getBillCardPanel().getBodyItem("rangerate").setEnabled(false);
				e.getBillCardPanel().getBodyItem("rangerevise")
						.setEnabled(false);
			}
		}
	}

	public RangetableGenerateAction getGenAction() {
		return genAction;
	}

	public void setGenAction(RangetableGenerateAction genAction) {
		this.genAction = genAction;
	}

}
