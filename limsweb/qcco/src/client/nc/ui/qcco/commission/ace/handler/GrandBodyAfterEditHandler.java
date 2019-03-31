package nc.ui.qcco.commission.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;

public class GrandBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		Object value = null;
		if (e.getSource() instanceof BillItem) {
			if (((BillItem) e.getSource()).getComponent() instanceof UIRefPane) {
				UIRefPane refPane = (UIRefPane) ((BillItem) e.getSource()).getComponent();
				value = refPane.getRefPK();
			}
		} else {
			BillCellEditor bitem = (BillCellEditor) e.getSource();
			if (bitem.getComponent() instanceof UIRefPane) {
				UIRefPane refPane = (UIRefPane) bitem.getComponent();
				value = refPane.getRefPK();
			}
		}

		if ("samplegroup".equals(e.getKey())) {
			e.getBillCardPanel().setBodyValueAt(value, e.getRow(), "pk_samplegroup");
		} else if ("component".equals(e.getKey())) {
			e.getBillCardPanel().setBodyValueAt(value, e.getRow(), "pk_component");
		}
	}
}
