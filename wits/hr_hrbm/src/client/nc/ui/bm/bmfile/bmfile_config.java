package nc.ui.bm.bmfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class bmfile_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.bm.pub.BmLoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.bm.pub.BmLoginContext)context.get("context");
  nc.vo.bm.pub.BmLoginContext bean = new nc.vo.bm.pub.BmLoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.model.BmfileModelService getManageModelService(){
 if(context.get("ManageModelService")!=null)
 return (nc.ui.bm.bmfile.model.BmfileModelService)context.get("ManageModelService");
  nc.ui.bm.bmfile.model.BmfileModelService bean = new nc.ui.bm.bmfile.model.BmfileModelService();
  context.put("ManageModelService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory(){
 if(context.get("boadatorfactory")!=null)
 return (nc.vo.bd.meta.BDObjectAdpaterFactory)context.get("boadatorfactory");
  nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
  context.put("boadatorfactory",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.model.BmfileAppModel getManageAppModel(){
 if(context.get("ManageAppModel")!=null)
 return (nc.ui.bm.bmfile.model.BmfileAppModel)context.get("ManageAppModel");
  nc.ui.bm.bmfile.model.BmfileAppModel bean = new nc.ui.bm.bmfile.model.BmfileAppModel();
  context.put("ManageAppModel",bean);
  bean.setService(getManageModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors(){
 if(context.get("toftpanelActionContributors")!=null)
 return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
  nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
  context.put("toftpanelActionContributors",bean);
  bean.setContributors(getManagedList0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getListViewActions());  list.add(getCardEditorActions());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardEditorActions(){
 if(context.get("cardEditorActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("cardEditorActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillFormEditor());  context.put("cardEditorActions",bean);
  bean.setActions(getManagedList1());
  bean.setEditActions(getManagedList2());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getEditAction());  list.add(getDeleteCardAction());  return list;}

private List getManagedList2(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getNullaction());  list.add(getCancelAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListViewActions(){
 if(context.get("listViewActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("listViewActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListView());  context.put("listViewActions",bean);
  bean.setActions(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getAddActionGroup());  list.add(getEditActionGroup());  list.add(getDelActionGroup());  list.add(getNullaction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getNullaction());  list.add(getPsnChangeAction());  list.add(getBmfielManageMenu());  list.add(getNullaction());  list.add(getSortBmdataAction());  list.add(getNullaction());  list.add(getPrintgroupAction());  return list;}

public nc.ui.uif2.editor.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.uif2.editor.TemplateContainer)context.get("templateContainer");
  nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList4());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add("bmfile");  return list;}

public nc.ui.bm.bmfile.model.BmfileModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.bm.bmfile.model.BmfileModelDataManager)context.get("modelDataManager");
  nc.ui.bm.bmfile.model.BmfileModelDataManager bean = new nc.ui.bm.bmfile.model.BmfileModelDataManager();
  context.put("modelDataManager",bean);
  bean.setService(getManageModelService());
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
  bean.setPaginationModel(getPaginationModel());
  bean.setPaginationBar(getPaginationBar());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.pagination.PaginationBar getPaginationBar(){
 if(context.get("paginationBar")!=null)
 return (nc.ui.uif2.components.pagination.PaginationBar)context.get("paginationBar");
  nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
  context.put("paginationBar",bean);
  bean.setPaginationModel(getPaginationModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.pagination.PaginationModel getPaginationModel(){
 if(context.get("paginationModel")!=null)
 return (nc.ui.uif2.components.pagination.PaginationModel)context.get("paginationModel");
  nc.ui.uif2.components.pagination.PaginationModel bean = new nc.ui.uif2.components.pagination.PaginationModel();
  context.put("paginationModel",bean);
  bean.setPaginationQueryService(getManageModelService());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.view.BmfileListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.bm.bmfile.view.BmfileListView)context.get("listView");
  nc.ui.bm.bmfile.view.BmfileListView bean = new nc.ui.bm.bmfile.view.BmfileListView();
  context.put("listView",bean);
  bean.setModel(getManageAppModel());
  bean.setMultiSelectionEnable(false);
  bean.setPos("head");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("bmfile");
  bean.setSouth(getPaginationBar());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.view.BmfileCardForm getBillFormEditor(){
 if(context.get("billFormEditor")!=null)
 return (nc.ui.bm.bmfile.view.BmfileCardForm)context.get("billFormEditor");
  nc.ui.bm.bmfile.view.BmfileCardForm bean = new nc.ui.bm.bmfile.view.BmfileCardForm();
  context.put("billFormEditor",bean);
  bean.setModel(getManageAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("bmfile");
  bean.setActions(getManagedList5());
  bean.setTabActions(getManagedList6());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  return list;}

private List getManagedList6(){  List list = new ArrayList();  return list;}

public nc.funcnode.ui.action.SeparatorAction getNullaction(){
 if(context.get("nullaction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("nullaction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("nullaction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.BmfilePsnChangeAction getPsnChangeAction(){
 if(context.get("PsnChangeAction")!=null)
 return (nc.ui.bm.bmfile.action.BmfilePsnChangeAction)context.get("PsnChangeAction");
  nc.ui.bm.bmfile.action.BmfilePsnChangeAction bean = new nc.ui.bm.bmfile.action.BmfilePsnChangeAction();
  context.put("PsnChangeAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getBmfielManageMenu(){
 if(context.get("BmfielManageMenu")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("BmfielManageMenu");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("BmfielManageMenu",bean);
  bean.setCode("BmfielManageMenu");
  bean.setName(getI18nFB_4db5cb7b());
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_4db5cb7b(){
 if(context.get("nc.ui.uif2.I18nFB#4db5cb7b")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#4db5cb7b");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#4db5cb7b",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("业务处理");
  bean.setResId("X60150010");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#4db5cb7b",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList7(){  List list = new ArrayList();  list.add(getTransferOutAction());  list.add(getTransferInAction());  list.add(getSealBmfileAction());  list.add(getUnSealBmfileAction());  list.add(getUnregBmfileAction());  return list;}

public nc.ui.bm.bmfile.action.TransferOutAction getTransferOutAction(){
 if(context.get("TransferOutAction")!=null)
 return (nc.ui.bm.bmfile.action.TransferOutAction)context.get("TransferOutAction");
  nc.ui.bm.bmfile.action.TransferOutAction bean = new nc.ui.bm.bmfile.action.TransferOutAction();
  context.put("TransferOutAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.TransferInAction getTransferInAction(){
 if(context.get("TransferInAction")!=null)
 return (nc.ui.bm.bmfile.action.TransferInAction)context.get("TransferInAction");
  nc.ui.bm.bmfile.action.TransferInAction bean = new nc.ui.bm.bmfile.action.TransferInAction();
  context.put("TransferInAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.SealBmfileAction getSealBmfileAction(){
 if(context.get("SealBmfileAction")!=null)
 return (nc.ui.bm.bmfile.action.SealBmfileAction)context.get("SealBmfileAction");
  nc.ui.bm.bmfile.action.SealBmfileAction bean = new nc.ui.bm.bmfile.action.SealBmfileAction();
  context.put("SealBmfileAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.UnSealBmfileAction getUnSealBmfileAction(){
 if(context.get("UnSealBmfileAction")!=null)
 return (nc.ui.bm.bmfile.action.UnSealBmfileAction)context.get("UnSealBmfileAction");
  nc.ui.bm.bmfile.action.UnSealBmfileAction bean = new nc.ui.bm.bmfile.action.UnSealBmfileAction();
  context.put("UnSealBmfileAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.UnregBmfileAction getUnregBmfileAction(){
 if(context.get("UnregBmfileAction")!=null)
 return (nc.ui.bm.bmfile.action.UnregBmfileAction)context.get("UnregBmfileAction");
  nc.ui.bm.bmfile.action.UnregBmfileAction bean = new nc.ui.bm.bmfile.action.UnregBmfileAction();
  context.put("UnregBmfileAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getAddActionGroup(){
 if(context.get("AddActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("AddActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("AddActionGroup",bean);
  bean.setActions(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getBatchAddAction());  return list;}

public nc.ui.bm.bmfile.action.AddBmfileAction getAddAction(){
 if(context.get("AddAction")!=null)
 return (nc.ui.bm.bmfile.action.AddBmfileAction)context.get("AddAction");
  nc.ui.bm.bmfile.action.AddBmfileAction bean = new nc.ui.bm.bmfile.action.AddBmfileAction();
  context.put("AddAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.BatchAddBmfileAction getBatchAddAction(){
 if(context.get("BatchAddAction")!=null)
 return (nc.ui.bm.bmfile.action.BatchAddBmfileAction)context.get("BatchAddAction");
  nc.ui.bm.bmfile.action.BatchAddBmfileAction bean = new nc.ui.bm.bmfile.action.BatchAddBmfileAction();
  context.put("BatchAddAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getEditActionGroup(){
 if(context.get("EditActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("EditActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("EditActionGroup",bean);
  bean.setActions(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getEditAction());  list.add(getBatchEditAction());  return list;}

public nc.ui.bm.bmfile.action.BatchEditBmfileAction getBatchEditAction(){
 if(context.get("BatchEditAction")!=null)
 return (nc.ui.bm.bmfile.action.BatchEditBmfileAction)context.get("BatchEditAction");
  nc.ui.bm.bmfile.action.BatchEditBmfileAction bean = new nc.ui.bm.bmfile.action.BatchEditBmfileAction();
  context.put("BatchEditAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.EditBmfileAction getEditAction(){
 if(context.get("EditAction")!=null)
 return (nc.ui.bm.bmfile.action.EditBmfileAction)context.get("EditAction");
  nc.ui.bm.bmfile.action.EditBmfileAction bean = new nc.ui.bm.bmfile.action.EditBmfileAction();
  context.put("EditAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getDelActionGroup(){
 if(context.get("DelActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("DelActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("DelActionGroup",bean);
  bean.setActions(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList10(){  List list = new ArrayList();  list.add(getDeleteAction());  list.add(getBatchDelBmfileAction());  return list;}

public nc.ui.bm.bmfile.action.DeleteBmfileAction getDeleteAction(){
 if(context.get("DeleteAction")!=null)
 return (nc.ui.bm.bmfile.action.DeleteBmfileAction)context.get("DeleteAction");
  nc.ui.bm.bmfile.action.DeleteBmfileAction bean = new nc.ui.bm.bmfile.action.DeleteBmfileAction();
  context.put("DeleteAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.DeleteCardBmfileAction getDeleteCardAction(){
 if(context.get("DeleteCardAction")!=null)
 return (nc.ui.bm.bmfile.action.DeleteCardBmfileAction)context.get("DeleteCardAction");
  nc.ui.bm.bmfile.action.DeleteCardBmfileAction bean = new nc.ui.bm.bmfile.action.DeleteCardBmfileAction();
  context.put("DeleteCardAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.BatchDelBmfileAction getBatchDelBmfileAction(){
 if(context.get("BatchDelBmfileAction")!=null)
 return (nc.ui.bm.bmfile.action.BatchDelBmfileAction)context.get("BatchDelBmfileAction");
  nc.ui.bm.bmfile.action.BatchDelBmfileAction bean = new nc.ui.bm.bmfile.action.BatchDelBmfileAction();
  context.put("BatchDelBmfileAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.QueryBmfileAction getQueryAction(){
 if(context.get("QueryAction")!=null)
 return (nc.ui.bm.bmfile.action.QueryBmfileAction)context.get("QueryAction");
  nc.ui.bm.bmfile.action.QueryBmfileAction bean = new nc.ui.bm.bmfile.action.QueryBmfileAction();
  context.put("QueryAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setQueryDelegator(getQueryEditorListener_5a422018());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.bm.pub.QueryEditorListener getQueryEditorListener_5a422018(){
 if(context.get("nc.ui.bm.pub.QueryEditorListener#5a422018")!=null)
 return (nc.ui.bm.pub.QueryEditorListener)context.get("nc.ui.bm.pub.QueryEditorListener#5a422018");
  nc.ui.bm.pub.QueryEditorListener bean = new nc.ui.bm.pub.QueryEditorListener();
  context.put("nc.ui.bm.pub.QueryEditorListener#5a422018",bean);
  bean.setNodeKey("bmfile");
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.RefreshBmfileAction getRefreshAction(){
 if(context.get("RefreshAction")!=null)
 return (nc.ui.bm.bmfile.action.RefreshBmfileAction)context.get("RefreshAction");
  nc.ui.bm.bmfile.action.RefreshBmfileAction bean = new nc.ui.bm.bmfile.action.RefreshBmfileAction();
  context.put("RefreshAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.SaveBmfileAction getSaveAction(){
 if(context.get("SaveAction")!=null)
 return (nc.ui.bm.bmfile.action.SaveBmfileAction)context.get("SaveAction");
  nc.ui.bm.bmfile.action.SaveBmfileAction bean = new nc.ui.bm.bmfile.action.SaveBmfileAction();
  context.put("SaveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.action.CancelBmfileAction getCancelAction(){
 if(context.get("CancelAction")!=null)
 return (nc.ui.bm.bmfile.action.CancelBmfileAction)context.get("CancelAction");
  nc.ui.bm.bmfile.action.CancelBmfileAction bean = new nc.ui.bm.bmfile.action.CancelBmfileAction();
  context.put("CancelAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintgroupAction(){
 if(context.get("printgroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("printgroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("printgroupAction",bean);
  bean.setCode("print");
  bean.setName(getI18nFB_2b4935f3());
  bean.setActions(getManagedList11());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_2b4935f3(){
 if(context.get("nc.ui.uif2.I18nFB#2b4935f3")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#2b4935f3");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#2b4935f3",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("打印");
  bean.setResId("X60150002");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#2b4935f3",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList11(){  List list = new ArrayList();  list.add(getDirectPrintAction());  list.add(getDirectPreviewAction());  list.add(getExportListAction());  list.add(getNullaction());  return list;}

public nc.ui.bm.bmfile.action.SortBmfileAction getSortBmdataAction(){
 if(context.get("SortBmdataAction")!=null)
 return (nc.ui.bm.bmfile.action.SortBmfileAction)context.get("SortBmdataAction");
  nc.ui.bm.bmfile.action.SortBmfileAction bean = new nc.ui.bm.bmfile.action.SortBmfileAction();
  context.put("SortBmdataAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.ExportListAction getExportListAction(){
 if(context.get("exportListAction")!=null)
 return (nc.ui.hr.uif2.action.print.ExportListAction)context.get("exportListAction");
  nc.ui.hr.uif2.action.print.ExportListAction bean = new nc.ui.hr.uif2.action.print.ExportListAction();
  context.put("exportListAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.DirectPreviewAction getDirectPreviewAction(){
 if(context.get("directPreviewAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPreviewAction)context.get("directPreviewAction");
  nc.ui.hr.uif2.action.print.DirectPreviewAction bean = new nc.ui.hr.uif2.action.print.DirectPreviewAction();
  context.put("directPreviewAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.DirectPrintAction getDirectPrintAction(){
 if(context.get("directPrintAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPrintAction)context.get("directPrintAction");
  nc.ui.hr.uif2.action.print.DirectPrintAction bean = new nc.ui.hr.uif2.action.print.DirectPrintAction();
  context.put("directPrintAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.TemplatePreviewAction getTemplatePreviewAction(){
 if(context.get("TemplatePreviewAction")!=null)
 return (nc.ui.hr.uif2.action.print.TemplatePreviewAction)context.get("TemplatePreviewAction");
  nc.ui.hr.uif2.action.print.TemplatePreviewAction bean = new nc.ui.hr.uif2.action.print.TemplatePreviewAction();
  context.put("TemplatePreviewAction",bean);
  bean.setModel(getManageAppModel());
  bean.setPrintDlgParentConatiner(getBillFormEditor());
  bean.setNodeKey("bmfile");
  bean.setDatasource(getDatasource());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hrcp.cindex.model.HRListMetaDataDataSource getDatasource(){
 if(context.get("datasource")!=null)
 return (nc.ui.hrcp.cindex.model.HRListMetaDataDataSource)context.get("datasource");
  nc.ui.hrcp.cindex.model.HRListMetaDataDataSource bean = new nc.ui.hrcp.cindex.model.HRListMetaDataDataSource();
  context.put("datasource",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.TemplatePrintAction getTemplatePrintAction(){
 if(context.get("TemplatePrintAction")!=null)
 return (nc.ui.hr.uif2.action.print.TemplatePrintAction)context.get("TemplatePrintAction");
  nc.ui.hr.uif2.action.print.TemplatePrintAction bean = new nc.ui.hr.uif2.action.print.TemplatePrintAction();
  context.put("TemplatePrintAction",bean);
  bean.setModel(getManageAppModel());
  bean.setPrintDlgParentConatiner(getBillFormEditor());
  bean.setNodeKey("bmfile");
  bean.setDatasource(getDatasource());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.FirstLineAction getFirstLineAction(){
 if(context.get("FirstLineAction")!=null)
 return (nc.ui.uif2.actions.FirstLineAction)context.get("FirstLineAction");
  nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
  context.put("FirstLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.PreLineAction getPreLineAction(){
 if(context.get("PreLineAction")!=null)
 return (nc.ui.uif2.actions.PreLineAction)context.get("PreLineAction");
  nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
  context.put("PreLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.NextLineAction getNextLineAction(){
 if(context.get("NextLineAction")!=null)
 return (nc.ui.uif2.actions.NextLineAction)context.get("NextLineAction");
  nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
  context.put("NextLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.LastLineAction getLastLineAction(){
 if(context.get("LastLineAction")!=null)
 return (nc.ui.uif2.actions.LastLineAction)context.get("LastLineAction");
  nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
  context.put("LastLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction(){
 if(context.get("editorReturnAction")!=null)
 return (nc.ui.uif2.actions.ShowMeUpAction)context.get("editorReturnAction");
  nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
  context.put("editorReturnAction",bean);
  bean.setGoComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getManageAppModel());
  bean.setSaveaction(getSaveAction());
  bean.setCancelaction(getCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getTBNode_637a9eb9());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_637a9eb9(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#637a9eb9")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#637a9eb9");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#637a9eb9",bean);
  bean.setShowMode("CardLayout");
  bean.setTabs(getManagedList12());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList12(){  List list = new ArrayList();  list.add(getVSNode_32b80490());  list.add(getVSNode_a4f6910());  return list;}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_32b80490(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#32b80490")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#32b80490");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#32b80490",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_5374fab8());
  bean.setDown(getCNode_24f870d6());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_5374fab8(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#5374fab8")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#5374fab8");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#5374fab8",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_24f870d6(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#24f870d6")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#24f870d6");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#24f870d6",bean);
  bean.setComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_a4f6910(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#a4f6910")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#a4f6910");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#a4f6910",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_4f33a3f3());
  bean.setDown(getCNode_4750cf24());
  bean.setDividerLocation(26f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_4f33a3f3(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#4f33a3f3")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#4f33a3f3");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#4f33a3f3",bean);
  bean.setComponent(getEditorToolBarPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_4750cf24(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#4750cf24")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#4750cf24");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#4750cf24",bean);
  bean.setComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.bm.bmfile.view.BmfileOrgHeadPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.bm.bmfile.view.BmfileOrgHeadPanel)context.get("orgpanel");
  nc.ui.bm.bmfile.view.BmfileOrgHeadPanel bean = new nc.ui.bm.bmfile.view.BmfileOrgHeadPanel();
  context.put("orgpanel",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel(){
 if(context.get("editorToolBarPanel")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("editorToolBarPanel");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("editorToolBarPanel",bean);
  bean.setModel(getManageAppModel());
  bean.setTitleAction(getEditorReturnAction());
  bean.setActions(getManagedList13());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList13(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

}
