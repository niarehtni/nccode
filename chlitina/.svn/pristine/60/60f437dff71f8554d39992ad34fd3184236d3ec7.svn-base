package nc.ui.twhr.nhicalc.handler;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;

public class BodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {
    @Override
    public void handleAppEvent(CardBodyBeforeEditEvent e) {
	if (e.getKey().equals("pk_psndoc_sub")) {
	    BillCardPanel card = e.getBillCardPanel();
	    if (card.getBodyItem("pk_psndoc").getValueObject() == null) {
		e.setReturnValue(Boolean.FALSE);
	    } else {
		e.getSource();
	    }
	} else {
	    e.setReturnValue(Boolean.TRUE);
	}
    }
}
