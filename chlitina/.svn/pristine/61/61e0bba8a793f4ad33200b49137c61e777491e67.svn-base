package nc.ui.twhr.nhicalc.handler;

import nc.ref.twhr.refmodel.TWHIFamilyRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent;

public class BodyRowChangedHandler implements IAppEventHandler<CardBodyRowChangedEvent> {

    @Override
    public void handleAppEvent(CardBodyRowChangedEvent e) {
	TWHIFamilyRefModel ref = new TWHIFamilyRefModel();
	ref.setPk_psndoc(null);
	((UIRefPane) e.getBillCardPanel().getBodyItem("pk_psndoc_sub").getComponent()).setRefModel(ref);
    }
}
