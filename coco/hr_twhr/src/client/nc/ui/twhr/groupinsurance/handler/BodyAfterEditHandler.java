package nc.ui.twhr.groupinsurance.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.twhr.groupinsurance.view.GroupInsuranceFormularRefPane;

public class BodyAfterEditHandler implements
		IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		if (e.getKey().toLowerCase().equals("cformular")) {
			e.getBillCardPanel()
					.getBillModel()
					.setValueAt(
							((GroupInsuranceFormularRefPane) e
									.getBillCardPanel()
									.getBodyItem("cformular").getComponent())
									.getFormula().getScirptLang(), e.getRow(),
							"cformularstr");
		}
	}
}
