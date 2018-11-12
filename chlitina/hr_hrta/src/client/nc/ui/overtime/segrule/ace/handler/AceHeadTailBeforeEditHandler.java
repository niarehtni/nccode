package nc.ui.overtime.segrule.ace.handler;

import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.UIState;

/**
 * 单据表头表尾字段编辑前事件处理类
 * 
 * @since 6.0
 * @version 2011-7-7 下午02:51:21
 * @author duy
 */
public class AceHeadTailBeforeEditHandler implements
		IAppEventHandler<CardHeadTailBeforeEditEvent> {

	private ShowUpableBillForm billForm;

	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		if (e.getKey().equals("code")
				&& this.getBillForm().getModel().getUiState()
						.equals(UIState.EDIT)) {
			e.setReturnValue(false);
		}
		e.setReturnValue(true);
	}

	public ShowUpableBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(ShowUpableBillForm billForm) {
		this.billForm = billForm;
	}

}
