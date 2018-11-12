package nc.ui.overtime.segrule.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.value.DefaultFieldValue;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.ta.pub.view.TAParamOrgPanel;

public class QueryAction extends DefaultQueryAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 1303344321512365087L;

	private TAParamOrgPanel orgpanel;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		DefaultFieldValue orgValue = new DefaultFieldValue();
		RefValueObject vobj = new RefValueObject();
		vobj.setCode(orgpanel.getRefPane().getRefCode());
		vobj.setName(orgpanel.getRefPane().getRefName());
		vobj.setPk(orgpanel.getRefPane().getRefPK());
		vobj.setMultiLang(true);
		vobj.setSubIncluded(false);
		DefaultFieldValueElement ele = new DefaultFieldValueElement(orgpanel.getRefPane().getRefName(), orgpanel
				.getRefPane().getRefPK(), vobj);
		orgValue.add(ele);
		QueryConditionDLGDelegator dlg = getQryDLGDelegator();
		dlg.getQueryConditionDLG().beforeShowModal();
		for (IFilterEditor edt : dlg.getSimpleEditorFilterEditors()) {
			IFilter flt = edt.getFilter();
			if (flt.getFilterMeta().getFieldCode().equals("pk_org")) {
				((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor()).setValue(orgValue);
				flt.setFieldValue(orgValue);
			}
		}
		super.doAction(e);
	}

	public TAParamOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(TAParamOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}
}
