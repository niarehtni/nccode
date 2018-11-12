package nc.ui.twhr.basedoc.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;

public class BodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {

    @Override
    public void handleAppEvent(CardBodyAfterEditEvent e) {
	if (e.getKey().equals("refvalue")) {

	}
    }

}
