package nc.ui.twhr.groupinsurance.handler;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.IGroupinsuranceMaintain;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.twhr.groupinsurance.view.GroupInsuranceFormularRefPane;

public class BodyBeforeEditHandler implements
		IAppEventHandler<CardBodyBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		boolean rtn = true;

		if (e.getKey().toLowerCase().equals("cgrpinsid")) {
			((nc.ui.pub.beans.UIRefPane) e.getBillCardPanel()
					.getBodyItem("cgrpinsid").getComponent())
					.setRefNodeName("团保险种(自定义档案)");
			((nc.ui.pub.beans.UIRefPane) e.getBillCardPanel()
					.getBodyItem("cgrpinsid").getComponent()).setPk_org(e
					.getContext().getPk_org());
			rtn = true;
		} else if (e.getKey().toLowerCase().equals("cgrpinsrelid")) {
			((nc.ui.pub.beans.UIRefPane) e.getBillCardPanel()
					.getBodyItem("cgrpinsrelid").getComponent())
					.setRefNodeName("团保身份(自定义档案)");
			((nc.ui.pub.beans.UIRefPane) e.getBillCardPanel()
					.getBodyItem("cgrpinsrelid").getComponent()).setPk_org(e
					.getContext().getPk_org());
			rtn = true;
		} else if (e.getKey().toLowerCase().equals("cformular")) {
			((GroupInsuranceFormularRefPane) e.getBillCardPanel()
					.getBodyItem("cformular").getComponent()).getFormula()
					.setBusinessLang(
							(String) e.getBillCardPanel().getBillModel()
									.getValueAt(e.getRow(), "cformular"));
			((GroupInsuranceFormularRefPane) e.getBillCardPanel()
					.getBodyItem("cformular").getComponent()).getFormula()
					.setScirptLang(
							(String) e.getBillCardPanel().getBillModel()
									.getValueAt(e.getRow(), "cformularstr"));
		}

		// if (e.getBillCardPanel().getBillModel().getRowState(e.getRow()) !=
		// BillModel.ADD
		// && e.getBillCardPanel().getBillModel().getRowState(e.getRow()) !=
		// BillModel.DELETE) {
		// try {
		// if (this.getCheckService()
		// .isExistsApprovedWaClassByGroupInsurance(
		// (String) e.getBillCardPanel().getBillModel()
		// .getValueAt(e.getRow(), "cgrpinsid"),
		// (String) e.getBillCardPanel().getBillModel()
		// .getValueAt(e.getRow(), "cgrpinsrelid"))) {
		// rtn = false;
		// } else {
		// rtn = true;
		// }
		// } catch (BusinessException be) {
		// ExceptionUtils.wrappBusinessException(be.getMessage());
		// }
		// }

		e.setReturnValue(rtn);
	}

	IGroupinsuranceMaintain service = null;

	private IGroupinsuranceMaintain getCheckService() {
		if (service == null) {
			IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(
					IGroupinsuranceMaintain.class);
		}

		return service;
	}
}
