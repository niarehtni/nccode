package nc.ui.wa.itemgroup.ace.view;

import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
public class ItemGroupBillCardPanel extends ShowUpableBillForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7060835711607143301L;
	
	@Override
	protected void onEdit() {
		getModel().getContext().setPk_org("GLOBLE00000000000000");
		super.onEdit();
	}
	
	@Override
	protected void onAdd() {
		getModel().getContext().setPk_org("GLOBLE00000000000000");
		super.onAdd();
	}
	
}
