package nc.ui.twhr.allowance.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;

public class BodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {

  @Override
  public void handleAppEvent(CardBodyBeforeEditEvent e) {
    // TODO Auto-generated method stub
    e.setReturnValue(Boolean.TRUE);
  }

}
