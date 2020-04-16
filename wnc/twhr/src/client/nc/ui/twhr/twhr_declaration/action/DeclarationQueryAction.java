package nc.ui.twhr.twhr_declaration.action;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction;
import nc.ui.querytemplate.filtereditor.DefaultFilterEditor;
import nc.ui.querytemplate.filtereditor.IFilterEditor;
import nc.ui.querytemplate.value.DefaultFieldValue;
import nc.ui.querytemplate.value.RefValueObject;
import nc.ui.querytemplate.valueeditor.DefaultFieldValueEditor;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.querytemplate.filter.IFilter;
/**
 * @author Administrator
 *
 */
public class DeclarationQueryAction extends DefaultQueryAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2454391567868762831L;
	
	private nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel = null;
		
	
	@Override
	protected boolean isActionEnable() {
		if(null == getPrimaryOrgPanel().getRefPane().getRefPK()){
			return false;
		}
		return true;
	}

	public nc.ui.twhr.glb.view.OrgPanel_Org getPrimaryOrgPanel() {
		return primaryOrgPanel;
	}

	public void setPrimaryOrgPanel(nc.ui.twhr.glb.view.OrgPanel_Org primaryOrgPanel) {
		this.primaryOrgPanel = primaryOrgPanel;
	}
	
	@Override
    public void doAction(ActionEvent e) throws Exception {
		
	DefaultFieldValue orgValue = new DefaultFieldValue();
	RefValueObject vobj = new RefValueObject();
	vobj.setCode(getPrimaryOrgPanel().getRefPane().getRefCode());
	vobj.setName(getPrimaryOrgPanel().getRefPane().getRefName());
	vobj.setPk(getPrimaryOrgPanel().getRefPane().getRefPK());
	vobj.setMultiLang(true);
	vobj.setSubIncluded(false);
	DefaultFieldValueElement ele = new DefaultFieldValueElement(getPrimaryOrgPanel().getRefPane().getRefName(), getPrimaryOrgPanel()
		.getRefPane().getRefPK(), vobj);
	orgValue.add(ele);
	QueryConditionDLGDelegator dlg = getQryDLGDelegator();
	
	//获取选择的人力资源组织
	dlg.getQueryConditionDLG().beforeShowModal();
	for (IFilterEditor edt : dlg.getSimpleEditorFilterEditors()) {
	    IFilter flt = edt.getFilter();
	    if (flt.getFilterMeta().getFieldCode().equals("pk_psndoc_temp")
		    || flt.getFilterMeta().getFieldCode().equals("pk_waperiod_temp")
	    	|| flt.getFilterMeta().getFieldCode().equals("pk_dept_temp")) {
	    
		((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor())
			.getFieldValueElemEditor().getFieldValueElemEditorComponent()).getRefModel().setPk_org(
					getPrimaryOrgPanel().getRefPane().getRefPK());
		
	    }else if(flt.getFilterMeta().getFieldCode().equals("legal_org_temp")){

	    	((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor()).setValue(orgValue);
			flt.setFieldValue(orgValue);
			
			
			
	    	UIRefPane hrPanel = ((UIRefPane) ((DefaultFieldValueEditor) ((DefaultFilterEditor) edt).getFieldValueEditor())
	    			.getFieldValueElemEditor().getFieldValueElemEditorComponent());
	    	hrPanel.setEnabled(false);
	    
	    }
	}
	//dlg.getQueryConditionDLG().beforeShowModal();
	super.doAction(e);
    }



	
}
