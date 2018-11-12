package nc.ui.hi.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class employee_button_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.ui.hi.psndoc.view.CourtFineValidator getCourtFineValidator(){
 if(context.get("courtFineValidator")!=null)
 return (nc.ui.hi.psndoc.view.CourtFineValidator)context.get("courtFineValidator");
  nc.ui.hi.psndoc.view.CourtFineValidator bean = new nc.ui.hi.psndoc.view.CourtFineValidator();
  context.put("courtFineValidator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getFormEditorActions(){
 if(context.get("formEditorActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("formEditorActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer((nc.ui.uif2.components.ITabbedPaneAwareComponent)findBeanInUIF2BeanFactory("psndocFormEditor"));  context.put("formEditorActions",bean);
  bean.setActions(getManagedList0());
  bean.setEditActions(getManagedList1());
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getEditPsndocAction());  list.add(getDeletePsndocAction());  list.add(getSeparatorAction());  list.add(getRefreshPsndocAction());  list.add(getSeparatorAction());  list.add(getCardAssistGroup());  list.add(getSeparatorAction());  list.add(getCardRelateQueryGroup());  list.add(getSeparatorAction());  list.add(getCardPrintActionGroup());  return list;}

private List getManagedList1(){  List list = new ArrayList();  list.add(getSavePsndocAction());  list.add(getSeparatorAction());  list.add(getCancelPsndocAction());  return list;}

public nc.ui.hi.psndoc.view.PsndocActionContainer getListViewActions(){
 if(context.get("listViewActions")!=null)
 return (nc.ui.hi.psndoc.view.PsndocActionContainer)context.get("listViewActions");
  nc.ui.hi.psndoc.view.PsndocActionContainer bean = new nc.ui.hi.psndoc.view.PsndocActionContainer((nc.ui.uif2.components.ITabbedPaneAwareComponent)findBeanInUIF2BeanFactory("psndocListView"));  context.put("listViewActions",bean);
  bean.setActions(getManagedList2());
  bean.setDataManager((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
  bean.setAdjustSortActions(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getEditActionGroup());  list.add(getDeletePsndocAction());  list.add(getSeparatorAction());  list.add(getQueryPsndocAction());  list.add(getRefreshPsndocAction());  list.add(getSeparatorAction());  list.add(getAssistGroup());  list.add(getSeparatorAction());  list.add(getRelateQueryGroup());  list.add(getSeparatorAction());  list.add(getExportPsnInfoGroup());  list.add(getSeparatorAction());  list.add(getPrintActionGroup());  return list;}

private List getManagedList3(){  List list = new ArrayList();  list.add(getSavePsndocAction());  list.add(getSeparatorAction());  list.add(getCancelPsndocAction());  return list;}

public java.lang.String getResourceCode(){
 if(context.get("resourceCode")!=null)
 return (java.lang.String)context.get("resourceCode");
  java.lang.String bean = new java.lang.String("6007psnjob");  context.put("resourceCode",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBarSeparator(){
 if(context.get("ActionsBarSeparator")!=null)
 return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator)context.get("ActionsBarSeparator");
  nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
  context.put("ActionsBarSeparator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getSeparatorAction(){
 if(context.get("separatorAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("separatorAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("separatorAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.MaxBodyViewAction getBodyMaxAction(){
 if(context.get("bodyMaxAction")!=null)
 return (nc.ui.hr.uif2.action.MaxBodyViewAction)context.get("bodyMaxAction");
  nc.ui.hr.uif2.action.MaxBodyViewAction bean = new nc.ui.hr.uif2.action.MaxBodyViewAction();
  context.put("bodyMaxAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setCardPanel((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(true);
  bean.setEnableWhenEditingOnly(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.MaxHeadViewAction getHeadMaxAction(){
 if(context.get("headMaxAction")!=null)
 return (nc.ui.hr.uif2.action.MaxHeadViewAction)context.get("headMaxAction");
  nc.ui.hr.uif2.action.MaxHeadViewAction bean = new nc.ui.hr.uif2.action.MaxHeadViewAction();
  context.put("headMaxAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setCardPanel((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(true);
  bean.setEnableWhenEditingOnly(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getEditActionGroup(){
 if(context.get("editActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("editActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("editActionGroup",bean);
  bean.setActions(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getEditPsndocAction());  list.add(getBatchEditAction());  return list;}

public nc.ui.hi.psndoc.action.EditPsndocAction getEditPsndocAction(){
 if(context.get("editPsndocAction")!=null)
 return (nc.ui.hi.psndoc.action.EditPsndocAction)context.get("editPsndocAction");
  nc.ui.hi.psndoc.action.EditPsndocAction bean = new nc.ui.hi.psndoc.action.EditPsndocAction();
  context.put("editPsndocAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.DeletePsndocAction getDeletePsndocAction(){
 if(context.get("deletePsndocAction")!=null)
 return (nc.ui.hi.psndoc.action.DeletePsndocAction)context.get("deletePsndocAction");
  nc.ui.hi.psndoc.action.DeletePsndocAction bean = new nc.ui.hi.psndoc.action.DeletePsndocAction();
  context.put("deletePsndocAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hr.uif2.view.HrBillListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.QueryPsndocAction getQueryPsndocAction(){
 if(context.get("queryPsndocAction")!=null)
 return (nc.ui.hi.psndoc.action.QueryPsndocAction)context.get("queryPsndocAction");
  nc.ui.hi.psndoc.action.QueryPsndocAction bean = new nc.ui.hi.psndoc.action.QueryPsndocAction();
  context.put("queryPsndocAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setDataManager((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
  bean.setQueryDelegator(getQueryPsndocDelegator_55a4ae());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hi.psndoc.action.QueryPsndocDelegator getQueryPsndocDelegator_55a4ae(){
 if(context.get("nc.ui.hi.psndoc.action.QueryPsndocDelegator#55a4ae")!=null)
 return (nc.ui.hi.psndoc.action.QueryPsndocDelegator)context.get("nc.ui.hi.psndoc.action.QueryPsndocDelegator#55a4ae");
  nc.ui.hi.psndoc.action.QueryPsndocDelegator bean = new nc.ui.hi.psndoc.action.QueryPsndocDelegator();
  context.put("nc.ui.hi.psndoc.action.QueryPsndocDelegator#55a4ae",bean);
  bean.setContext((nc.vo.uif2.LoginContext)findBeanInUIF2BeanFactory("context"));
  bean.setNodeKey("bd_psndoc");
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.RefreshPsndocAction getRefreshPsndocAction(){
 if(context.get("refreshPsndocAction")!=null)
 return (nc.ui.hi.psndoc.action.RefreshPsndocAction)context.get("refreshPsndocAction");
  nc.ui.hi.psndoc.action.RefreshPsndocAction bean = new nc.ui.hi.psndoc.action.RefreshPsndocAction();
  context.put("refreshPsndocAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setDataManager((nc.ui.uif2.model.IAppModelDataManagerEx)findBeanInUIF2BeanFactory("psndocDataManager"));
  bean.setFormEditor((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.SavePsndocAction getSavePsndocAction(){
 if(context.get("savePsndocAction")!=null)
 return (nc.ui.hi.psndoc.action.SavePsndocAction)context.get("savePsndocAction");
  nc.ui.hi.psndoc.action.SavePsndocAction bean = new nc.ui.hi.psndoc.action.SavePsndocAction();
  context.put("savePsndocAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setDataManager((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
  bean.setSuperValidator((nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil)findBeanInUIF2BeanFactory("SuperValidationConfig"));
  bean.setValidationService((nc.bs.uif2.validation.IValidationService)findBeanInUIF2BeanFactory("billNotNullValidator"));
  bean.setCourtFineValidator(getCourtFineValidator());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.CancelPsndocAction getCancelPsndocAction(){
 if(context.get("cancelPsndocAction")!=null)
 return (nc.ui.hi.psndoc.action.CancelPsndocAction)context.get("cancelPsndocAction");
  nc.ui.hi.psndoc.action.CancelPsndocAction bean = new nc.ui.hi.psndoc.action.CancelPsndocAction();
  context.put("cancelPsndocAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setDataManger((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.AddSubSetAction getAddSubSetAction(){
 if(context.get("addSubSetAction")!=null)
 return (nc.ui.hi.psndoc.action.AddSubSetAction)context.get("addSubSetAction");
  nc.ui.hi.psndoc.action.AddSubSetAction bean = new nc.ui.hi.psndoc.action.AddSubSetAction();
  context.put("addSubSetAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setCardPanel((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(false);
  bean.setDisableTabsSet((java.util.Set)findBeanInUIF2BeanFactory("disableTabSet"));
  bean.setDefaultValueProvider((nc.ui.hr.uif2.model.IDefaultValueProvider)findBeanInUIF2BeanFactory("subDefaultValueProvider"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.DelSubSetAction getDeleteSubSetAction(){
 if(context.get("deleteSubSetAction")!=null)
 return (nc.ui.hi.psndoc.action.DelSubSetAction)context.get("deleteSubSetAction");
  nc.ui.hi.psndoc.action.DelSubSetAction bean = new nc.ui.hi.psndoc.action.DelSubSetAction();
  context.put("deleteSubSetAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setCardPanel((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(false);
  bean.setDisableTabsSet((java.util.Set)findBeanInUIF2BeanFactory("disableTabSet"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.InsertSubSetAction getInsertSubSetAction(){
 if(context.get("insertSubSetAction")!=null)
 return (nc.ui.hi.psndoc.action.InsertSubSetAction)context.get("insertSubSetAction");
  nc.ui.hi.psndoc.action.InsertSubSetAction bean = new nc.ui.hi.psndoc.action.InsertSubSetAction();
  context.put("insertSubSetAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setCardPanel((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(false);
  bean.setDisableTabsSet((java.util.Set)findBeanInUIF2BeanFactory("disableTabSet"));
  bean.setDefaultValueProvider((nc.ui.hr.uif2.model.IDefaultValueProvider)findBeanInUIF2BeanFactory("subDefaultValueProvider"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.CopySubSetAction getCopySubSetAction(){
 if(context.get("copySubSetAction")!=null)
 return (nc.ui.hi.psndoc.action.CopySubSetAction)context.get("copySubSetAction");
  nc.ui.hi.psndoc.action.CopySubSetAction bean = new nc.ui.hi.psndoc.action.CopySubSetAction();
  context.put("copySubSetAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setCardPanel((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(false);
  bean.setDisableTabsSet((java.util.Set)findBeanInUIF2BeanFactory("disableTabSet"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.PasteSubSetAction getPasteSubSetAction(){
 if(context.get("pasteSubSetAction")!=null)
 return (nc.ui.hi.psndoc.action.PasteSubSetAction)context.get("pasteSubSetAction");
  nc.ui.hi.psndoc.action.PasteSubSetAction bean = new nc.ui.hi.psndoc.action.PasteSubSetAction();
  context.put("pasteSubSetAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setCardPanel((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(false);
  bean.setDisableTabsSet((java.util.Set)findBeanInUIF2BeanFactory("disableTabSet"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.AdjustSubReordUpAction getAdjustSubReordUpAction(){
 if(context.get("adjustSubReordUpAction")!=null)
 return (nc.ui.hi.psndoc.action.AdjustSubReordUpAction)context.get("adjustSubReordUpAction");
  nc.ui.hi.psndoc.action.AdjustSubReordUpAction bean = new nc.ui.hi.psndoc.action.AdjustSubReordUpAction();
  context.put("adjustSubReordUpAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setFormEditor((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(false);
  bean.setDisableTabsSet((java.util.Set)findBeanInUIF2BeanFactory("businessInfoSet"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.AdjustSubReordDownAction getAdjustSubReordDownAction(){
 if(context.get("adjustSubReordDownAction")!=null)
 return (nc.ui.hi.psndoc.action.AdjustSubReordDownAction)context.get("adjustSubReordDownAction");
  nc.ui.hi.psndoc.action.AdjustSubReordDownAction bean = new nc.ui.hi.psndoc.action.AdjustSubReordDownAction();
  context.put("adjustSubReordDownAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setFormEditor((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDefaultStatus(true);
  bean.setEnableForAllTabs(false);
  bean.setDisableTabsSet((java.util.Set)findBeanInUIF2BeanFactory("businessInfoSet"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.FirstPsndocAction getFirstLineAction(){
 if(context.get("firstLineAction")!=null)
 return (nc.ui.hi.psndoc.action.FirstPsndocAction)context.get("firstLineAction");
  nc.ui.hi.psndoc.action.FirstPsndocAction bean = new nc.ui.hi.psndoc.action.FirstPsndocAction();
  context.put("firstLineAction",bean);
  bean.setModel((nc.ui.uif2.model.BillManageModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setFormEditor((nc.ui.hi.psndoc.view.PsndocFormEditor)findBeanInUIF2BeanFactory("psndocFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.PrePsndocAction getPreLineAction(){
 if(context.get("preLineAction")!=null)
 return (nc.ui.hi.psndoc.action.PrePsndocAction)context.get("preLineAction");
  nc.ui.hi.psndoc.action.PrePsndocAction bean = new nc.ui.hi.psndoc.action.PrePsndocAction();
  context.put("preLineAction",bean);
  bean.setModel((nc.ui.uif2.model.BillManageModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setFormEditor((nc.ui.hi.psndoc.view.PsndocFormEditor)findBeanInUIF2BeanFactory("psndocFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.NextPsndocAction getNextLineAction(){
 if(context.get("nextLineAction")!=null)
 return (nc.ui.hi.psndoc.action.NextPsndocAction)context.get("nextLineAction");
  nc.ui.hi.psndoc.action.NextPsndocAction bean = new nc.ui.hi.psndoc.action.NextPsndocAction();
  context.put("nextLineAction",bean);
  bean.setModel((nc.ui.uif2.model.BillManageModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setFormEditor((nc.ui.hi.psndoc.view.PsndocFormEditor)findBeanInUIF2BeanFactory("psndocFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.LastPsndocAction getLastLineAction(){
 if(context.get("lastLineAction")!=null)
 return (nc.ui.hi.psndoc.action.LastPsndocAction)context.get("lastLineAction");
  nc.ui.hi.psndoc.action.LastPsndocAction bean = new nc.ui.hi.psndoc.action.LastPsndocAction();
  context.put("lastLineAction",bean);
  bean.setModel((nc.ui.uif2.model.BillManageModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setFormEditor((nc.ui.hi.psndoc.view.PsndocFormEditor)findBeanInUIF2BeanFactory("psndocFormEditor"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.BatchEditPsndocAction getBatchEditAction(){
 if(context.get("batchEditAction")!=null)
 return (nc.ui.hi.psndoc.action.BatchEditPsndocAction)context.get("batchEditAction");
  nc.ui.hi.psndoc.action.BatchEditPsndocAction bean = new nc.ui.hi.psndoc.action.BatchEditPsndocAction();
  context.put("batchEditAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setFormEditor((nc.ui.hi.psndoc.view.PsndocFormEditor)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.BatchAddSubSetAction getBatchAddSubSetAction(){
 if(context.get("batchAddSubSetAction")!=null)
 return (nc.ui.hi.psndoc.action.BatchAddSubSetAction)context.get("batchAddSubSetAction");
  nc.ui.hi.psndoc.action.BatchAddSubSetAction bean = new nc.ui.hi.psndoc.action.BatchAddSubSetAction();
  context.put("batchAddSubSetAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.SortPsndocAction getSortPsndocAction(){
 if(context.get("sortPsndocAction")!=null)
 return (nc.ui.hi.psndoc.action.SortPsndocAction)context.get("sortPsndocAction");
  nc.ui.hi.psndoc.action.SortPsndocAction bean = new nc.ui.hi.psndoc.action.SortPsndocAction();
  context.put("sortPsndocAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setDataManger((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.AdjustPsndocSortAction getAdjustSortAction(){
 if(context.get("adjustSortAction")!=null)
 return (nc.ui.hi.psndoc.action.AdjustPsndocSortAction)context.get("adjustSortAction");
  nc.ui.hi.psndoc.action.AdjustPsndocSortAction bean = new nc.ui.hi.psndoc.action.AdjustPsndocSortAction();
  context.put("adjustSortAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setDataManger((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
  bean.setTreeView((nc.ui.om.psnnavi.view.TreePanelContainer)findBeanInUIF2BeanFactory("treeContainer"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintActionGroup(){
 if(context.get("printActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("printActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("printActionGroup",bean);
  bean.setActions(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  list.add(getPrintDirectAction());  list.add(getPrintPreviewAction());  list.add(getListOutputAction());  list.add(getSeparatorAction());  list.add(getTemplatePrintAction());  list.add(getTemplatePreviewAction());  return list;}

public nc.funcnode.ui.action.GroupAction getCardPrintActionGroup(){
 if(context.get("cardPrintActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("cardPrintActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("cardPrintActionGroup",bean);
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getTemplatePrintAction());  list.add(getTemplatePreviewAction());  list.add(getCardOutputAction());  return list;}

public nc.ui.hr.uif2.action.print.DirectPreviewAction getPrintPreviewAction(){
 if(context.get("printPreviewAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPreviewAction)context.get("printPreviewAction");
  nc.ui.hr.uif2.action.print.DirectPreviewAction bean = new nc.ui.hr.uif2.action.print.DirectPreviewAction();
  context.put("printPreviewAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.uif2.editor.IBillListPanelView)findBeanInUIF2BeanFactory("psndocListView"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.DirectPrintAction getPrintDirectAction(){
 if(context.get("printDirectAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPrintAction)context.get("printDirectAction");
  nc.ui.hr.uif2.action.print.DirectPrintAction bean = new nc.ui.hr.uif2.action.print.DirectPrintAction();
  context.put("printDirectAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.uif2.editor.IBillListPanelView)findBeanInUIF2BeanFactory("psndocListView"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.ExportListPsndocAction getListOutputAction(){
 if(context.get("listOutputAction")!=null)
 return (nc.ui.hi.psndoc.action.ExportListPsndocAction)context.get("listOutputAction");
  nc.ui.hi.psndoc.action.ExportListPsndocAction bean = new nc.ui.hi.psndoc.action.ExportListPsndocAction();
  context.put("listOutputAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.uif2.editor.IBillListPanelView)findBeanInUIF2BeanFactory("psndocListView"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction getTemplatePreviewAction(){
 if(context.get("templatePreviewAction")!=null)
 return (nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction)context.get("templatePreviewAction");
  nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction bean = new nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction();
  context.put("templatePreviewAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setPrintDlgParentConatiner((javax.swing.JComponent)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDatasource(getDatasource());
  bean.setNodeKey("bd_psndoc");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.TemplatePrintPsndocAction getTemplatePrintAction(){
 if(context.get("templatePrintAction")!=null)
 return (nc.ui.hi.psndoc.action.TemplatePrintPsndocAction)context.get("templatePrintAction");
  nc.ui.hi.psndoc.action.TemplatePrintPsndocAction bean = new nc.ui.hi.psndoc.action.TemplatePrintPsndocAction();
  context.put("templatePrintAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setPrintDlgParentConatiner((javax.swing.JComponent)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDatasource(getDatasource());
  bean.setNodeKey("bd_psndoc");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.ExportCardPsndocAction getCardOutputAction(){
 if(context.get("cardOutputAction")!=null)
 return (nc.ui.hi.psndoc.action.ExportCardPsndocAction)context.get("cardOutputAction");
  nc.ui.hi.psndoc.action.ExportCardPsndocAction bean = new nc.ui.hi.psndoc.action.ExportCardPsndocAction();
  context.put("cardOutputAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setPrintDlgParentConatiner((javax.swing.JComponent)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setDatasource(getDatasource());
  bean.setNodeKey("bd_psndoc");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.HIMetaDataDataSource getDatasource(){
 if(context.get("datasource")!=null)
 return (nc.ui.hi.psndoc.action.HIMetaDataDataSource)context.get("datasource");
  nc.ui.hi.psndoc.action.HIMetaDataDataSource bean = new nc.ui.hi.psndoc.action.HIMetaDataDataSource();
  context.put("datasource",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setSingleData(true);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getRelateQueryGroup(){
 if(context.get("relateQueryGroup")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("relateQueryGroup");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("relateQueryGroup",bean);
  bean.setCode("relateQuery");
  bean.setName(getI18nFB_aec1a2());
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_aec1a2(){
 if(context.get("nc.ui.uif2.I18nFB#aec1a2")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#aec1a2");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#aec1a2",bean);  bean.setResDir("6001uif2");
  bean.setResId("x6001uif20002");
  bean.setDefaultValue("联查");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#aec1a2",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList7(){  List list = new ArrayList();  list.add(getQueryCardReptAction());  list.add(getQueryListReptAction());  list.add(getSeparatorAction());  list.add(getQueryJobHistroyAction());  list.add(getSeparatorAction());  list.add(getQueryReptObjectAction());  list.add(getSeparatorAction());  list.add(getQueryBusiBillAction());  return list;}

public nc.funcnode.ui.action.MenuAction getCardRelateQueryGroup(){
 if(context.get("cardRelateQueryGroup")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("cardRelateQueryGroup");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("cardRelateQueryGroup",bean);
  bean.setCode("relateQuery");
  bean.setName(getI18nFB_12a91cd());
  bean.setActions(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_12a91cd(){
 if(context.get("nc.ui.uif2.I18nFB#12a91cd")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#12a91cd");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#12a91cd",bean);  bean.setResDir("6001uif2");
  bean.setResId("x6001uif20002");
  bean.setDefaultValue("联查");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#12a91cd",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList8(){  List list = new ArrayList();  list.add(getQueryCardReptAction());  list.add(getSeparatorAction());  list.add(getQueryJobHistroyAction());  list.add(getSeparatorAction());  list.add(getQueryReptObjectAction());  list.add(getSeparatorAction());  list.add(getQueryBusiBillAction());  return list;}

public nc.ui.hi.psndoc.action.QueryCardReptAction getQueryCardReptAction(){
 if(context.get("queryCardReptAction")!=null)
 return (nc.ui.hi.psndoc.action.QueryCardReptAction)context.get("queryCardReptAction");
  nc.ui.hi.psndoc.action.QueryCardReptAction bean = new nc.ui.hi.psndoc.action.QueryCardReptAction();
  context.put("queryCardReptAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.QueryListReptAction getQueryListReptAction(){
 if(context.get("queryListReptAction")!=null)
 return (nc.ui.hi.psndoc.action.QueryListReptAction)context.get("queryListReptAction");
  nc.ui.hi.psndoc.action.QueryListReptAction bean = new nc.ui.hi.psndoc.action.QueryListReptAction();
  context.put("queryListReptAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setDataManager((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.QueryBusiBillAction getQueryBusiBillAction(){
 if(context.get("queryBusiBillAction")!=null)
 return (nc.ui.hi.psndoc.action.QueryBusiBillAction)context.get("queryBusiBillAction");
  nc.ui.hi.psndoc.action.QueryBusiBillAction bean = new nc.ui.hi.psndoc.action.QueryBusiBillAction();
  context.put("queryBusiBillAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setFormEditor((nc.ui.hi.psndoc.view.PsndocFormEditor)findBeanInUIF2BeanFactory("psndocFormEditor"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction getQueryPsnAbilityMatchAction(){
 if(context.get("queryPsnAbilityMatchAction")!=null)
 return (nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction)context.get("queryPsnAbilityMatchAction");
  nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction bean = new nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction();
  context.put("queryPsnAbilityMatchAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.QueryReptObjectAction getQueryReptObjectAction(){
 if(context.get("queryReptObjectAction")!=null)
 return (nc.ui.hi.psndoc.action.QueryReptObjectAction)context.get("queryReptObjectAction");
  nc.ui.hi.psndoc.action.QueryReptObjectAction bean = new nc.ui.hi.psndoc.action.QueryReptObjectAction();
  context.put("queryReptObjectAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.HIFileManageAction getAttachmentAction(){
 if(context.get("attachmentAction")!=null)
 return (nc.ui.hi.psndoc.action.HIFileManageAction)context.get("attachmentAction");
  nc.ui.hi.psndoc.action.HIFileManageAction bean = new nc.ui.hi.psndoc.action.HIFileManageAction();
  context.put("attachmentAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.HIFileManageAction getFileAction(){
 if(context.get("fileAction")!=null)
 return (nc.ui.hi.psndoc.action.HIFileManageAction)context.get("fileAction");
  nc.ui.hi.psndoc.action.HIFileManageAction bean = new nc.ui.hi.psndoc.action.HIFileManageAction();
  context.put("fileAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setToolBarVisible(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.QueryJobHistroyAction getQueryJobHistroyAction(){
 if(context.get("queryJobHistroyAction")!=null)
 return (nc.ui.hi.psndoc.action.QueryJobHistroyAction)context.get("queryJobHistroyAction");
  nc.ui.hi.psndoc.action.QueryJobHistroyAction bean = new nc.ui.hi.psndoc.action.QueryJobHistroyAction();
  context.put("queryJobHistroyAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.ExportPhotoAction getExportPhotoAction(){
 if(context.get("exportPhotoAction")!=null)
 return (nc.ui.hi.psndoc.action.ExportPhotoAction)context.get("exportPhotoAction");
  nc.ui.hi.psndoc.action.ExportPhotoAction bean = new nc.ui.hi.psndoc.action.ExportPhotoAction();
  context.put("exportPhotoAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.ImportPhotoAction getImportPhotoAction(){
 if(context.get("importPhotoAction")!=null)
 return (nc.ui.hi.psndoc.action.ImportPhotoAction)context.get("importPhotoAction");
  nc.ui.hi.psndoc.action.ImportPhotoAction bean = new nc.ui.hi.psndoc.action.ImportPhotoAction();
  context.put("importPhotoAction",bean);
  bean.setModel((nc.ui.uif2.model.AbstractUIAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.CreateUserAction getCreateUserAction(){
 if(context.get("createUserAction")!=null)
 return (nc.ui.hi.psndoc.action.CreateUserAction)context.get("createUserAction");
  nc.ui.hi.psndoc.action.CreateUserAction bean = new nc.ui.hi.psndoc.action.CreateUserAction();
  context.put("createUserAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.ExportPsnInfoAction getExportPsnInfoAction(){
 if(context.get("exportPsnInfoAction")!=null)
 return (nc.ui.hi.psndoc.action.ExportPsnInfoAction)context.get("exportPsnInfoAction");
  nc.ui.hi.psndoc.action.ExportPsnInfoAction bean = new nc.ui.hi.psndoc.action.ExportPsnInfoAction();
  context.put("exportPsnInfoAction",bean);
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hi.psndoc.action.ExportPsnInfoAllAction getExportPsnInfoAllAction(){
 if(context.get("exportPsnInfoAllAction")!=null)
 return (nc.ui.hi.psndoc.action.ExportPsnInfoAllAction)context.get("exportPsnInfoAllAction");
  nc.ui.hi.psndoc.action.ExportPsnInfoAllAction bean = new nc.ui.hi.psndoc.action.ExportPsnInfoAllAction();
  context.put("exportPsnInfoAllAction",bean);
  bean.setDataManager((nc.ui.hi.psndoc.model.PsndocDataManager)findBeanInUIF2BeanFactory("psndocDataManager"));
  bean.setModel((nc.ui.hi.psndoc.model.PsndocModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setListView((nc.ui.hi.psndoc.view.PsndocListView)findBeanInUIF2BeanFactory("psndocListView"));
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getExportPsnInfoGroup(){
 if(context.get("exportPsnInfoGroup")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("exportPsnInfoGroup");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("exportPsnInfoGroup",bean);
  bean.setCode("exportPsnInfo");
  bean.setName(getI18nFB_1ca65d3());
  bean.setActions(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1ca65d3(){
 if(context.get("nc.ui.uif2.I18nFB#1ca65d3")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1ca65d3");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1ca65d3",bean);  bean.setResDir("6001uif2");
  bean.setResId("x6001uif20004");
  bean.setDefaultValue("导出");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1ca65d3",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList9(){  List list = new ArrayList();  list.add(getExportPsnInfoAction());  list.add(getExportPsnInfoAllAction());  return list;}

public nc.funcnode.ui.action.MenuAction getAssistGroup(){
 if(context.get("assistGroup")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("assistGroup");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("assistGroup",bean);
  bean.setCode("assist");
  bean.setName(getI18nFB_e31f56());
  bean.setActions(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_e31f56(){
 if(context.get("nc.ui.uif2.I18nFB#e31f56")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#e31f56");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#e31f56",bean);  bean.setResDir("6001uif2");
  bean.setResId("x6001uif20001");
  bean.setDefaultValue("辅助功能");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#e31f56",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList10(){  List list = new ArrayList();  list.add(getSortPsndocAction());  list.add(getAdjustSortAction());  list.add(getSeparatorAction());  list.add(getImportPhotoAction());  list.add(getExportPhotoAction());  list.add(getCreateUserAction());  list.add(getSeparatorAction());  list.add(getAttachmentAction());  list.add(getBatchAddSubSetAction());  return list;}

public nc.funcnode.ui.action.MenuAction getCardAssistGroup(){
 if(context.get("cardAssistGroup")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("cardAssistGroup");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("cardAssistGroup",bean);
  bean.setCode("assist");
  bean.setName(getI18nFB_11e6133());
  bean.setActions(getManagedList11());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_11e6133(){
 if(context.get("nc.ui.uif2.I18nFB#11e6133")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#11e6133");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#11e6133",bean);  bean.setResDir("6001uif2");
  bean.setResId("x6001uif20001");
  bean.setDefaultValue("辅助功能");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#11e6133",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList11(){  List list = new ArrayList();  list.add(getExportPhotoAction());  list.add(getCreateUserAction());  list.add(getSeparatorAction());  list.add(getAttachmentAction());  return list;}

}
