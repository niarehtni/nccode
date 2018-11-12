package nc.ui.hrpub.mdmapping.ace.view;

import nc.ui.md.ref.MDMainEntityTreeModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.AppEvent;

public class MDMappingBillForm extends ShowUpableBillForm {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -7601808226800840282L;

	@Override
	public void handleEvent(AppEvent event) {
		if ("Show_Editor".equals(event.getType())) {
			((UIRefPane) this.getBillCardPanel().getHeadItem("pk_class")
					.getComponent()).setRefModel(MDMainEntityTreeModel
					.getModelWithChild());
			((UIRefPane) this.getBillCardPanel().getHeadItem("pk_class")
					.getComponent()).getRefModel().setMutilLangNameRef(false);
		}

		super.handleEvent(event);
	}

}
