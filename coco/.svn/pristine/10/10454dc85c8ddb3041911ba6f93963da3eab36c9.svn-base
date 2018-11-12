package nc.ui.twhr.basedoc.handler;

import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;

public class BodyBeforeEditHandler implements
		IAppEventHandler<CardBodyBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		if (e.getKey().equals("numbervalue")) {
			BillItem type = e.getBillCardPanel().getBodyItem("doctype");
			if (type == null || type.getValueObject() == null
					|| (Integer) type.getValueObject() == 3) {
				e.setReturnValue(Boolean.FALSE);
			}
		} else if (e.getKey().equals("waitemvalue")) {
			BillItem type = e.getBillCardPanel().getBodyItem("doctype");
			if (type == null || type.getValueObject() == null
					|| (Integer) type.getValueObject() != 3) {
				e.setReturnValue(Boolean.FALSE);
			}
		}

		e.setReturnValue(Boolean.TRUE);
	}

}
