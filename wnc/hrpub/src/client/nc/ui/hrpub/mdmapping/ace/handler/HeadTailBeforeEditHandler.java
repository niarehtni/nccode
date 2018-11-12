package nc.ui.hrpub.mdmapping.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.UIState;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

public class HeadTailBeforeEditHandler implements
		IAppEventHandler<CardHeadTailBeforeEditEvent> {
	private BillForm editor;
	private LoginContext context;

	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		if (e.getKey().equals("pk_class")
				&& this.getEditor().getModel().getUiState() == UIState.EDIT) {
			BillCardPanel card = this.getEditor().getBillCardPanel();
			if (!StringUtils.isEmpty(((UIRefPane) card.getHeadItem("pk_class")
					.getComponent()).getText())) {
				e.setReturnValue(false);
				return;
			}
		}

		e.setReturnValue(true);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

}
