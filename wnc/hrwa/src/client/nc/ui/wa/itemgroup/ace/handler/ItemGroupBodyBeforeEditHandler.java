package nc.ui.wa.itemgroup.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.wa.ref.WaItemRefModel;

public class ItemGroupBodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		if (e.getKey().equals("pk_waitem")) {
			BillCardPanel billcard = e.getBillCardPanel();
			BillItem bitemWaItem = billcard.getBodyItem(e.getKey());
			UIRefPane pane = (UIRefPane) bitemWaItem.getComponent();
			if (pane.getRefModel() instanceof WaItemRefModel) {
				((WaItemRefModel) pane.getRefModel()).setPkOrg(e.getContext().getPk_org());
			}
			e.setReturnValue(Boolean.TRUE);
			return;
		}

		e.setReturnValue(Boolean.TRUE);
	}
}
