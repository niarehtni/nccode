package nc.ui.overtime.otleavebalance.action;

import java.awt.event.ActionEvent;

import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.value.DefaultFieldValue;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;

public class QueryAction extends DefaultQueryAction {

    /**
     * serial no
     */
    private static final long serialVersionUID = 1303344321512365087L;

    private OTLeaveBalanceOrgPanel orgpanel;

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
	    } else if (flt.getFilterMeta().getFieldCode().equals("pk_psndoc")
		    || flt.getFilterMeta().getFieldCode().equals("pk_dept")) {
		((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor())
			.getFieldValueElemEditor().getFieldValueElemEditorComponent()).getRefModel().setPk_org(
			orgpanel.getRefPane().getRefPK());
	    }
	}
	super.doAction(e);
    }

    public OTLeaveBalanceOrgPanel getOrgpanel() {
	return orgpanel;
    }

    public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
	this.orgpanel = orgpanel;
    }

    @Override
    protected boolean isActionEnable() {
	return getOrgpanel().getRefPane().getValueObj() != null
		&& getOrgpanel().getRefBeginDate().getValueObj() != null
		&& getOrgpanel().getRefEndDate().getValueObj() != null;
    }
}
