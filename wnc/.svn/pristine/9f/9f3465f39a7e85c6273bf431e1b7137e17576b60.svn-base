package nc.ui.hrta.leaveextrarest.ace.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;

public class HeadTailBeforeEditEventHandler implements IAppEventHandler<CardHeadTailBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		if ("datebeforechange".equals(e.getKey())) {
			if (e.getBillCardPanel().getHeadItem("billdate").getValueObject() == null) {
				e.setReturnValue(false);
				return;
			}
		}
		e.setReturnValue(true);

	}
}
