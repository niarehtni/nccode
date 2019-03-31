package nc.ui.wa.taxaddtional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class taxadditional_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.uif2.LoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.uif2.LoginContext)context.get("context");
  nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxaddtional.model.TaxaddtionalAppModelService getModelService(){
 if(context.get("modelService")!=null)
 return (nc.ui.wa.taxaddtional.model.TaxaddtionalAppModelService)context.get("modelService");
  nc.ui.wa.taxaddtional.model.TaxaddtionalAppModelService bean = new nc.ui.wa.taxaddtional.model.TaxaddtionalAppModelService();
  context.put("modelService",bean);
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

public nc.ui.wa.taxaddtional.model.TaxaddtionalModel getAppModel(){
 if(context.get("appModel")!=null)
 return (nc.ui.wa.taxaddtional.model.TaxaddtionalModel)context.get("appModel");
  nc.ui.wa.taxaddtional.model.TaxaddtionalModel bean = new nc.ui.wa.taxaddtional.model.TaxaddtionalModel();
  context.put("appModel",bean);
  bean.setService(getModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxaddtional.model.TaxaddtionalModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.wa.taxaddtional.model.TaxaddtionalModelDataManager)context.get("modelDataManager");
  nc.ui.wa.taxaddtional.model.TaxaddtionalModelDataManager bean = new nc.ui.wa.taxaddtional.model.TaxaddtionalModelDataManager();
  context.put("modelDataManager",bean);
  bean.setService(getModelService());
  bean.setModel(getAppModel());
  bean.setContext(getContext());
  bean.setPaginationModel(getPaginationModel());
  bean.setPaginationBar(getPaginationBar());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.uif2.editor.TemplateContainer)context.get("templateContainer");
  nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList0());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add("taxsd");  return list;}

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
  bean.setPaginationQueryService(getModelService());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxaddtional.view.TaxaddtionalListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.wa.taxaddtional.view.TaxaddtionalListView)context.get("listView");
  nc.ui.wa.taxaddtional.view.TaxaddtionalListView bean = new nc.ui.wa.taxaddtional.view.TaxaddtionalListView();
  context.put("listView",bean);
  bean.setModel(getAppModel());
  bean.setMultiSelectionEnable(false);
  bean.setPos("head");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("taxsd");
  bean.setBillListPanelValueSetter(getAppendableBillListPanelSetter_333ed2());
  bean.setSouth(getPaginationBar());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hr.append.model.AppendableBillListPanelSetter getAppendableBillListPanelSetter_333ed2(){
 if(context.get("nc.ui.hr.append.model.AppendableBillListPanelSetter#333ed2")!=null)
 return (nc.ui.hr.append.model.AppendableBillListPanelSetter)context.get("nc.ui.hr.append.model.AppendableBillListPanelSetter#333ed2");
  nc.ui.hr.append.model.AppendableBillListPanelSetter bean = new nc.ui.hr.append.model.AppendableBillListPanelSetter();
  context.put("nc.ui.hr.append.model.AppendableBillListPanelSetter#333ed2",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.RefreshAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.uif2.actions.RefreshAction)context.get("refreshAction");
  nc.ui.uif2.actions.RefreshAction bean = new nc.ui.uif2.actions.RefreshAction();
  context.put("refreshAction",bean);
  bean.setModel(getAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getNullAction(){
 if(context.get("nullAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("nullAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("nullAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxaddtional.action.QueryTaxaddtionalAction getQueryAction(){
 if(context.get("queryAction")!=null)
 return (nc.ui.wa.taxaddtional.action.QueryTaxaddtionalAction)context.get("queryAction");
  nc.ui.wa.taxaddtional.action.QueryTaxaddtionalAction bean = new nc.ui.wa.taxaddtional.action.QueryTaxaddtionalAction();
  context.put("queryAction",bean);
  bean.setModel(getAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setQueryDelegator(getHrQueryDelegator_615d82());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hr.uif2.HrQueryDelegator getHrQueryDelegator_615d82(){
 if(context.get("nc.ui.hr.uif2.HrQueryDelegator#615d82")!=null)
 return (nc.ui.hr.uif2.HrQueryDelegator)context.get("nc.ui.hr.uif2.HrQueryDelegator#615d82");
  nc.ui.hr.uif2.HrQueryDelegator bean = new nc.ui.hr.uif2.HrQueryDelegator();
  context.put("nc.ui.hr.uif2.HrQueryDelegator#615d82",bean);
  bean.setNodeKey("taxsdquery");
  bean.setContext(getContext());
  bean.setModel(getAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxaddtional.action.ImportAction getImportAction(){
 if(context.get("importAction")!=null)
 return (nc.ui.wa.taxaddtional.action.ImportAction)context.get("importAction");
  nc.ui.wa.taxaddtional.action.ImportAction bean = new nc.ui.wa.taxaddtional.action.ImportAction();
  context.put("importAction",bean);
  bean.setModel(getAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
  bean.setEditor(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxaddtional.action.ExportAction getExportAction(){
 if(context.get("exportAction")!=null)
 return (nc.ui.wa.taxaddtional.action.ExportAction)context.get("exportAction");
  nc.ui.wa.taxaddtional.action.ExportAction bean = new nc.ui.wa.taxaddtional.action.ExportAction();
  context.put("exportAction",bean);
  bean.setModel(getAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
  bean.setEditor(getListView());
  bean.setQueryDelegator(getTaxaddtionalQueryDelegator_42040d());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.wa.taxaddtional.model.TaxaddtionalQueryDelegator getTaxaddtionalQueryDelegator_42040d(){
 if(context.get("nc.ui.wa.taxaddtional.model.TaxaddtionalQueryDelegator#42040d")!=null)
 return (nc.ui.wa.taxaddtional.model.TaxaddtionalQueryDelegator)context.get("nc.ui.wa.taxaddtional.model.TaxaddtionalQueryDelegator#42040d");
  nc.ui.wa.taxaddtional.model.TaxaddtionalQueryDelegator bean = new nc.ui.wa.taxaddtional.model.TaxaddtionalQueryDelegator();
  context.put("nc.ui.wa.taxaddtional.model.TaxaddtionalQueryDelegator#42040d",bean);
  bean.setNodeKey("taxsde");
  bean.setContext(getContext());
  bean.setModel(getAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintGroupListAction(){
 if(context.get("PrintGroupListAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PrintGroupListAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PrintGroupListAction",bean);
  bean.setCode("print");
  bean.setName(getI18nFB_17fbde());
  bean.setActions(getManagedList1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_17fbde(){
 if(context.get("nc.ui.uif2.I18nFB#17fbde")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#17fbde");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#17fbde",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("¥Ú”°");
  bean.setResId("X60130002");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#17fbde",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList1(){  List list = new ArrayList();  list.add(getPrintListAction());  list.add(getPreviewListAction());  list.add(getExportListAction());  return list;}

public nc.ui.hr.uif2.action.print.DirectPrintAction getPrintListAction(){
 if(context.get("PrintListAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPrintAction)context.get("PrintListAction");
  nc.ui.hr.uif2.action.print.DirectPrintAction bean = new nc.ui.hr.uif2.action.print.DirectPrintAction();
  context.put("PrintListAction",bean);
  bean.setModel(getAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.DirectPreviewAction getPreviewListAction(){
 if(context.get("PreviewListAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPreviewAction)context.get("PreviewListAction");
  nc.ui.hr.uif2.action.print.DirectPreviewAction bean = new nc.ui.hr.uif2.action.print.DirectPreviewAction();
  context.put("PreviewListAction",bean);
  bean.setModel(getAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.ExportListAction getExportListAction(){
 if(context.get("exportListAction")!=null)
 return (nc.ui.hr.uif2.action.print.ExportListAction)context.get("exportListAction");
  nc.ui.hr.uif2.action.print.ExportListAction bean = new nc.ui.hr.uif2.action.print.ExportListAction();
  context.put("exportListAction",bean);
  bean.setModel(getAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.view.PrimaryOrgPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.hr.uif2.view.PrimaryOrgPanel)context.get("orgpanel");
  nc.ui.hr.uif2.view.PrimaryOrgPanel bean = new nc.ui.hr.uif2.view.PrimaryOrgPanel();
  context.put("orgpanel",bean);
  bean.setModel(getAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setContext(getContext());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_180d73e());
  bean.setActions(getManagedList2());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_180d73e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#180d73e")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#180d73e");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#180d73e",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_98ea71());
  bean.setDown(getCNode_147bfcb());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_98ea71(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#98ea71")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#98ea71");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#98ea71",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_147bfcb(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#147bfcb")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#147bfcb");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#147bfcb",bean);
  bean.setComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getRefreshAction());  list.add(getQueryAction());  list.add(getNullAction());  list.add(getImportAction());  list.add(getExportAction());  list.add(getNullAction());  list.add(getPrintGroupListAction());  return list;}

}
