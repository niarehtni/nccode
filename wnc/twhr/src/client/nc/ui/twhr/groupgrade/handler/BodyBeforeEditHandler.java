package nc.ui.twhr.groupgrade.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.twhr.groupinsurance.refmodel.GroupInsuranceRefModel;

public class BodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		boolean rtn = true;

		if (e.getKey().toLowerCase().equals("pk_groupinsurance")) {
			((nc.ui.pub.beans.UIRefPane) e.getBillCardPanel().getBodyItem("pk_groupinsurance").getComponent())
					.setRefNodeName("团保投保设定");
			((nc.ui.pub.beans.UIRefPane) e.getBillCardPanel().getBodyItem("pk_groupinsurance").getComponent())
					.setRefModel(new GroupInsuranceRefModel());
			((nc.ui.pub.beans.UIRefPane) e.getBillCardPanel().getBodyItem("pk_groupinsurance").getComponent())
					.setPk_org(e.getContext().getPk_org());
			rtn = true;
		}

		e.setReturnValue(rtn);
	}
}
