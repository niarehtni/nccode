package nc.ui.twhr.nhicalc.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.vo.pub.lang.UFBoolean;

public class BodyAfterEditHandler implements
		IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		if (!e.getKey().equals("iscalculated")) {
			e.getBillCardPanel().getBillModel()
					.setValueAt(UFBoolean.FALSE, e.getRow(), "iscalculated");
		}
	}

}
