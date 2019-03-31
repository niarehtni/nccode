package nc.ui.wa.taxspecial_statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class taxspecial_statistics extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.wa.pub.WaLoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.wa.pub.WaLoginContext)context.get("context");
  nc.vo.wa.pub.WaLoginContext bean = new nc.vo.wa.pub.WaLoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelService getManageModelService(){
 if(context.get("ManageModelService")!=null)
 return (nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelService)context.get("ManageModelService");
  nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelService bean = new nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelService();
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

public nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsAppModel getManageAppModel(){
 if(context.get("ManageAppModel")!=null)
 return (nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsAppModel)context.get("ManageAppModel");
  nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsAppModel bean = new nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsAppModel();
  context.put("ManageAppModel",bean);
  bean.setService(getManageModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
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

private List getManagedList0(){  List list = new ArrayList();  list.add("taxsspcial");  return list;}

public nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager)context.get("modelDataManager");
  nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager bean = new nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager();
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

public nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsListView)context.get("listView");
  nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsListView bean = new nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsListView();
  context.put("listView",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setMultiSelectionEnable(false);
  bean.setPos("head");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("taxsspcial");
  bean.setBillListPanelValueSetter(getAppendableBillListPanelSetter_fc4119());
  bean.setSouth(getPaginationBar());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hr.append.model.AppendableBillListPanelSetter getAppendableBillListPanelSetter_fc4119(){
 if(context.get("nc.ui.hr.append.model.AppendableBillListPanelSetter#fc4119")!=null)
 return (nc.ui.hr.append.model.AppendableBillListPanelSetter)context.get("nc.ui.hr.append.model.AppendableBillListPanelSetter#fc4119");
  nc.ui.hr.append.model.AppendableBillListPanelSetter bean = new nc.ui.hr.append.model.AppendableBillListPanelSetter();
  context.put("nc.ui.hr.append.model.AppendableBillListPanelSetter#fc4119",bean);
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

public nc.funcnode.ui.action.SeparatorAction getNullaction(){
 if(context.get("nullaction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("nullaction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("nullaction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxspecial_statistics.action.QueryTaxSpecialStatisticsAction getQueryAction(){
 if(context.get("QueryAction")!=null)
 return (nc.ui.wa.taxspecial_statistics.action.QueryTaxSpecialStatisticsAction)context.get("QueryAction");
  nc.ui.wa.taxspecial_statistics.action.QueryTaxSpecialStatisticsAction bean = new nc.ui.wa.taxspecial_statistics.action.QueryTaxSpecialStatisticsAction();
  context.put("QueryAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOrgpanel(getOrgpanel());
  bean.setDataManager(getModelDataManager());
  bean.setQueryDelegator(getTaxSpecialStatisticsQueryDelegator_115e96f());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsQueryDelegator getTaxSpecialStatisticsQueryDelegator_115e96f(){
 if(context.get("nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsQueryDelegator#115e96f")!=null)
 return (nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsQueryDelegator)context.get("nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsQueryDelegator#115e96f");
  nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsQueryDelegator bean = new nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsQueryDelegator();
  context.put("nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsQueryDelegator#115e96f",bean);
  bean.setNodeKey("taxsspcial");
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsRefreshAction getRefreshAction(){
 if(context.get("RefreshAction")!=null)
 return (nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsRefreshAction)context.get("RefreshAction");
  nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsRefreshAction bean = new nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsRefreshAction();
  context.put("RefreshAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsGenericAction getGenericAction(){
 if(context.get("genericAction")!=null)
 return (nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsGenericAction)context.get("genericAction");
  nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsGenericAction bean = new nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsGenericAction();
  context.put("genericAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
  bean.setEditor(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsLockAction getLockAction(){
 if(context.get("lockAction")!=null)
 return (nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsLockAction)context.get("lockAction");
  nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsLockAction bean = new nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsLockAction();
  context.put("lockAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
  bean.setEditor(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsUnLockAction getUnlockAction(){
 if(context.get("unlockAction")!=null)
 return (nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsUnLockAction)context.get("unlockAction");
  nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsUnLockAction bean = new nc.ui.wa.taxspecial_statistics.action.TaxSpecialStatisticsUnLockAction();
  context.put("unlockAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
  bean.setEditor(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_19470f5());
  bean.setActions(getManagedList1());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_19470f5(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#19470f5")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#19470f5");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#19470f5",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_19076f7());
  bean.setDown(getCNode_17275cf());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_19076f7(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#19076f7")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#19076f7");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#19076f7",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_17275cf(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#17275cf")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#17275cf");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#17275cf",bean);
  bean.setComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getRefreshAction());  list.add(getQueryAction());  list.add(getNullaction());  list.add(getGenericAction());  list.add(getNullaction());  list.add(getLockAction());  list.add(getUnlockAction());  return list;}

public nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsHeadPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsHeadPanel)context.get("orgpanel");
  nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsHeadPanel bean = new nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsHeadPanel(getListView());  context.put("orgpanel",bean);
  bean.setModel(getManageAppModel());
  bean.setContext(getContext());
  bean.setDataManager(getModelDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
