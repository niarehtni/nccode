package nc.ui.hrpub.iopermit.handler;

import nc.ui.hrpub.mdclass.refmodel.MDClassRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable;
import nc.vo.uif2.LoginContext;

public class CardBodyBeforeEditHandler implements
		IAppEventHandler<CardBodyBeforeEditEvent> {
	private ShowUpableBatchBillTable editor;
	private LoginContext context;

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		if (e.getKey().equals("pk_mdclass")) {
			BillCardPanel card = this.getEditor().getBillCardPanel();
			String pk_ioschema = (String) card.getBodyValueAt(e.getRow(),
					"pk_ioschema");
			UIRefPane refpane = ((UIRefPane) card.getBodyItem("pk_mdclass")
					.getComponent());
			refpane.setRefModel(new MDClassRefModel());
			((MDClassRefModel) refpane.getRefModel())
					.setPk_ioschema(pk_ioschema);
		}
		e.setReturnValue(true);

	}

	public ShowUpableBatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(ShowUpableBatchBillTable editor) {
		this.editor = editor;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}
}
