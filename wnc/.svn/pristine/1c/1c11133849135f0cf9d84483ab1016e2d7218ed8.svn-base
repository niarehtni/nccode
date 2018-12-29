package nc.ui.hrwa.sumincometax.action;

import java.awt.event.ActionEvent;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;

public class SuminCometaxQueryAction extends DefaultQueryAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		QueryConditionDLGDelegator dlg = getQryDLGDelegator();
		dlg.getQueryConditionDLG().beforeShowModal();
		for (IFilterEditor edt : dlg.getSimpleEditorFilterEditors()) {
		    IFilter flt = edt.getFilter();
		    if (flt.getFilterMeta().getFieldCode().equals("unifiednumber")) {
			((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor())
				.getFieldValueElemEditor().getFieldValueElemEditorComponent()).setMultiSelectedEnabled(false);
			
		    }
		}
		super.doAction(e);
	}
	
	
}
