package nc.ui.hrjf.deptadj.ace.handler;

import nc.ui.om.ref.DeptPrincipalRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.pubapp.uif2app.view.BillForm;

/**
 * <b> </b>
 * 
 * @author author
 * @version tempProject version
 */
public class AceDeptChargeHandler implements IAppEventHandler<CardHeadTailBeforeEditEvent> {
	private BillForm billForm;

	public AceDeptChargeHandler(BillForm billForm) {
		super();
		this.billForm = billForm;
	}

	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		e.setReturnValue(Boolean.TRUE);
		BillCardPanel panel = e.getBillCardPanel();
		String key = e.getKey();
		if ("dept_charge".equals(key)) {
			String pk_dept = panel.getHeadItem("pk_dept").getValueObject().toString();
			DeptPrincipalRefModel refModel = (DeptPrincipalRefModel) ((UIRefPane) panel.getHeadItem(key).getComponent())
					.getRefModel();
			refModel.setPk_dept(pk_dept);
		}
	}

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

}
