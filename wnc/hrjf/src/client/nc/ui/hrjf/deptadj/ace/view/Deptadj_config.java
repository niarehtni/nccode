package nc.ui.hrjf.deptadj.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Deptadj_config extends AbstractJavaBeanDefinition{
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

public nc.ui.hrjf.deptadj.ace.serviceproxy.AceDeptadjMaintainProxy getBmModelModelService(){
 if(context.get("bmModelModelService")!=null)
 return (nc.ui.hrjf.deptadj.ace.serviceproxy.AceDeptadjMaintainProxy)context.get("bmModelModelService");
  nc.ui.hrjf.deptadj.ace.serviceproxy.AceDeptadjMaintainProxy bean = new nc.ui.hrjf.deptadj.ace.serviceproxy.AceDeptadjMaintainProxy();
  context.put("bmModelModelService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.GeneralBDObjectAdapterFactory getBOAdapterFactory(){
 if(context.get("BOAdapterFactory")!=null)
 return (nc.vo.bd.meta.GeneralBDObjectAdapterFactory)context.get("BOAdapterFactory");
  nc.vo.bd.meta.GeneralBDObjectAdapterFactory bean = new nc.vo.bd.meta.GeneralBDObjectAdapterFactory();
  context.put("BOAdapterFactory",bean);
  bean.setMode("MD");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.view.DeptBatchOrgRefPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.om.hrdept.view.DeptBatchOrgRefPanel)context.get("orgpanel");
  nc.ui.om.hrdept.view.DeptBatchOrgRefPanel bean = new nc.ui.om.hrdept.view.DeptBatchOrgRefPanel();
  context.put("orgpanel",bean);
  bean.setModel(getBmModel());
  bean.setDataManager(getBmModelModelDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.setControlType(0);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.BillManageModel getBmModel(){
 if(context.get("bmModel")!=null)
 return (nc.ui.pubapp.uif2app.model.BillManageModel)context.get("bmModel");
  nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
  context.put("bmModel",bean);
  bean.setContext(getContext());
  bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.model.ModelDataManager getBmModelModelDataManager(){
 if(context.get("bmModelModelDataManager")!=null)
 return (nc.ui.pubapp.uif2app.query2.model.ModelDataManager)context.get("bmModelModelDataManager");
  nc.ui.pubapp.uif2app.query2.model.ModelDataManager bean = new nc.ui.pubapp.uif2app.query2.model.ModelDataManager();
  context.put("bmModelModelDataManager",bean);
  bean.setModel(getBmModel());
  bean.setService(getBmModelModelService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.pubapp.uif2app.view.TemplateContainer)context.get("templateContainer");
  nc.ui.pubapp.uif2app.view.TemplateContainer bean = new nc.ui.pubapp.uif2app.view.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList0());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add("bt");  return list;}

public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getViewa(){
 if(context.get("viewa")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell)context.get("viewa");
  nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
  context.put("viewa",bean);
  bean.setQueryAreaCreator(getDefaultQueryAction());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.ShowUpableBillListView getBillListView(){
 if(context.get("billListView")!=null)
 return (nc.ui.pubapp.uif2app.view.ShowUpableBillListView)context.get("billListView");
  nc.ui.pubapp.uif2app.view.ShowUpableBillListView bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillListView();
  context.put("billListView",bean);
  bean.setModel(getBmModel());
  bean.setNodekey("bt");
  bean.setMultiSelectionEnable(false);
  bean.setTemplateContainer(getTemplateContainer());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getViewb(){
 if(context.get("viewb")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel)context.get("viewb");
  nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
  context.put("viewb",bean);
  bean.setModel(getBmModel());
  bean.setTitleAction(getReturnAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnAction(){
 if(context.get("returnAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.UEReturnAction)context.get("returnAction");
  nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
  context.put("returnAction",bean);
  bean.setGoComponent(getBillListView());
  bean.setSaveAction(getSaveScriptAction());
  bean.setModel(getBmModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hrjf.deptadj.ace.handler.BillFormEditor getBillForm(){
 if(context.get("billForm")!=null)
 return (nc.ui.hrjf.deptadj.ace.handler.BillFormEditor)context.get("billForm");
  nc.ui.hrjf.deptadj.ace.handler.BillFormEditor bean = new nc.ui.hrjf.deptadj.ace.handler.BillFormEditor();
  context.put("billForm",bean);
  bean.setModel(getBmModel());
  bean.setNodekey("bt");
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
  bean.setTangramLayoutRoot(getTBNode_b08675());
  bean.setActions(getManagedList2());
  bean.setEditActions(getManagedList3());
  bean.setModel(getBmModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_b08675(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#b08675")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#b08675");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#b08675",bean);
  bean.setTabs(getManagedList1());
  bean.setName("cardLayout");
  bean.setShowMode("CardLayout");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getHSNode_a4c96f());  list.add(getVSNode_b3ce5a());  return list;}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_a4c96f(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#a4c96f")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#a4c96f");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#a4c96f",bean);
  bean.setLeft(getCNode_1548a52());
  bean.setRight(getCNode_1453527());
  bean.setDividerLocation(215.0f);
  bean.setName("¡–±Ì");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1548a52(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1548a52")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1548a52");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1548a52",bean);
  bean.setComponent(getViewa());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1453527(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1453527")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1453527");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1453527",bean);
  bean.setComponent(getBillListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_b3ce5a(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#b3ce5a")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#b3ce5a");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#b3ce5a",bean);
  bean.setUp(getCNode_a4b68a());
  bean.setDown(getCNode_1cc3906());
  bean.setDividerLocation(43.0f);
  bean.setName("ø®∆¨");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_a4b68a(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#a4b68a")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#a4b68a");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#a4b68a",bean);
  bean.setComponent(getViewb());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1cc3906(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1cc3906")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1cc3906");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1cc3906",bean);
  bean.setComponent(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getDefaultQueryAction());  list.add(getSeparatorAction());  list.add(getDefaultRefreshAction());  list.add(getSeparatorAction());  list.add(getExecuteAction());  return list;}

private List getManagedList3(){  List list = new ArrayList();  list.add(getSaveScriptAction());  list.add(getCancelAction());  return list;}

public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener(){
 if(context.get("InitDataListener")!=null)
 return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener)context.get("InitDataListener");
  nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
  context.put("InitDataListener",bean);
  bean.setModel(getBmModel());
  bean.setContext(getContext());
  bean.setVoClassName("nc.vo.om.hrdept.AggHRDeptAdjustVO");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.pubapp.common.validateservice.ClosingCheck)context.get("ClosingListener");
  nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
  context.put("ClosingListener",bean);
  bean.setModel(getBmModel());
  bean.setSaveAction(getSaveScriptAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getBmModelEventMediator(){
 if(context.get("bmModelEventMediator")!=null)
 return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator)context.get("bmModelEventMediator");
  nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
  context.put("bmModelEventMediator",bean);
  bean.setModel(getBmModel());
  bean.setHandlerGroup(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getEventHandlerGroup_974b94());  list.add(getEventHandlerGroup_166922d());  return list;}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_974b94(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#974b94")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#974b94");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#974b94",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
  bean.setHandler(getAceOrgChangeHandler_14b8345());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hrjf.deptadj.ace.handler.AceOrgChangeHandler getAceOrgChangeHandler_14b8345(){
 if(context.get("nc.ui.hrjf.deptadj.ace.handler.AceOrgChangeHandler#14b8345")!=null)
 return (nc.ui.hrjf.deptadj.ace.handler.AceOrgChangeHandler)context.get("nc.ui.hrjf.deptadj.ace.handler.AceOrgChangeHandler#14b8345");
  nc.ui.hrjf.deptadj.ace.handler.AceOrgChangeHandler bean = new nc.ui.hrjf.deptadj.ace.handler.AceOrgChangeHandler();
  context.put("nc.ui.hrjf.deptadj.ace.handler.AceOrgChangeHandler#14b8345",bean);
  bean.setBillForm(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_166922d(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#166922d")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#166922d");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#166922d",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent");
  bean.setHandler(getAceDeptChargeHandler_e2f971());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hrjf.deptadj.ace.handler.AceDeptChargeHandler getAceDeptChargeHandler_e2f971(){
 if(context.get("nc.ui.hrjf.deptadj.ace.handler.AceDeptChargeHandler#e2f971")!=null)
 return (nc.ui.hrjf.deptadj.ace.handler.AceDeptChargeHandler)context.get("nc.ui.hrjf.deptadj.ace.handler.AceDeptChargeHandler#e2f971");
  nc.ui.hrjf.deptadj.ace.handler.AceDeptChargeHandler bean = new nc.ui.hrjf.deptadj.ace.handler.AceDeptChargeHandler(getBillForm());  context.put("nc.ui.hrjf.deptadj.ace.handler.AceDeptChargeHandler#e2f971",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator(){
 if(context.get("rowNoMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.RowNoMediator)context.get("rowNoMediator");
  nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
  context.put("rowNoMediator",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator(){
 if(context.get("mouseClickShowPanelMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator)context.get("mouseClickShowPanelMediator");
  nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
  context.put("mouseClickShowPanelMediator",bean);
  bean.setListView(getBillListView());
  bean.setShowUpComponent(getBillForm());
  bean.setHyperLinkColumn("null");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.AddAction getAddAction(){
 if(context.get("addAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.AddAction)context.get("addAction");
  nc.ui.pubapp.uif2app.actions.AddAction bean = new nc.ui.pubapp.uif2app.actions.AddAction();
  context.put("addAction",bean);
  bean.setModel(getBmModel());
  bean.setInterceptor(getCompositeActionInterceptor_15fcae5());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_15fcae5(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#15fcae5")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#15fcae5");
  nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#15fcae5",bean);
  bean.setInterceptors(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  list.add(getShowUpComponentInterceptor_106d45b());  return list;}

private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_106d45b(){
 if(context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#106d45b")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#106d45b");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#106d45b",bean);
  bean.setShowUpComponent(getBillForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hrjf.deptadj.action.DeptadjEditAction getEditAction(){
 if(context.get("editAction")!=null)
 return (nc.ui.hrjf.deptadj.action.DeptadjEditAction)context.get("editAction");
  nc.ui.hrjf.deptadj.action.DeptadjEditAction bean = new nc.ui.hrjf.deptadj.action.DeptadjEditAction();
  context.put("editAction",bean);
  bean.setModel(getBmModel());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hrjf.deptadj.action.DeptadjDeleteAction getDeleteAction(){
 if(context.get("deleteAction")!=null)
 return (nc.ui.hrjf.deptadj.action.DeptadjDeleteAction)context.get("deleteAction");
  nc.ui.hrjf.deptadj.action.DeptadjDeleteAction bean = new nc.ui.hrjf.deptadj.action.DeptadjDeleteAction();
  context.put("deleteAction",bean);
  bean.setModel(getBmModel());
  bean.setSingleBillService(getBmModelModelService());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.QueryTemplateContainer getDefaultQueryActionQueryTemplateContainer(){
 if(context.get("defaultQueryActionQueryTemplateContainer")!=null)
 return (nc.ui.uif2.editor.QueryTemplateContainer)context.get("defaultQueryActionQueryTemplateContainer");
  nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
  context.put("defaultQueryActionQueryTemplateContainer",bean);
  bean.setNodeKey("qt");
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getDefaultQueryAction(){
 if(context.get("defaultQueryAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction)context.get("defaultQueryAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
  context.put("defaultQueryAction",bean);
  bean.setModel(getBmModel());
  bean.setTemplateContainer(getDefaultQueryActionQueryTemplateContainer());
  bean.setNodeKey("qt");
  bean.setDataManager(getBmModelModelDataManager());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.CopyAction getCopyAction(){
 if(context.get("copyAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.CopyAction)context.get("copyAction");
  nc.ui.pubapp.uif2app.actions.CopyAction bean = new nc.ui.pubapp.uif2app.actions.CopyAction();
  context.put("copyAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getDefaultRefreshAction(){
 if(context.get("defaultRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction)context.get("defaultRefreshAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
  context.put("defaultRefreshAction",bean);
  bean.setModel(getBmModel());
  bean.setDataManager(getBmModelModelDataManager());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintAction(){
 if(context.get("metaDataBasedPrintAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction)context.get("metaDataBasedPrintAction");
  nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
  context.put("metaDataBasedPrintAction",bean);
  bean.setModel(getBmModel());
  bean.setActioncode("Preview");
  bean.setActionname("‘§¿¿");
  bean.setPreview(true);
  bean.setNodeKey("ot");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintActiona(){
 if(context.get("metaDataBasedPrintActiona")!=null)
 return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction)context.get("metaDataBasedPrintActiona");
  nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
  context.put("metaDataBasedPrintActiona",bean);
  bean.setModel(getBmModel());
  bean.setActioncode("Print");
  bean.setActionname("¥Ú”°");
  bean.setPreview(false);
  bean.setNodeKey("ot");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction(){
 if(context.get("outputAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.OutputAction)context.get("outputAction");
  nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
  context.put("outputAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setNodeKey("ot");
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hrjf.deptadj.action.DeptadjSaveAction getSaveScriptAction(){
 if(context.get("saveScriptAction")!=null)
 return (nc.ui.hrjf.deptadj.action.DeptadjSaveAction)context.get("saveScriptAction");
  nc.ui.hrjf.deptadj.action.DeptadjSaveAction bean = new nc.ui.hrjf.deptadj.action.DeptadjSaveAction();
  context.put("saveScriptAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setService(getBmModelModelService());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.CancelAction getCancelAction(){
 if(context.get("cancelAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.CancelAction)context.get("cancelAction");
  nc.ui.pubapp.uif2app.actions.CancelAction bean = new nc.ui.pubapp.uif2app.actions.CancelAction();
  context.put("cancelAction",bean);
  bean.setModel(getBmModel());
  bean.setEditor(getBillForm());
  bean.setExceptionHandler(getExceptionHandler());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hrjf.deptadj.action.DeptadjExecuteAction getExecuteAction(){
 if(context.get("executeAction")!=null)
 return (nc.ui.hrjf.deptadj.action.DeptadjExecuteAction)context.get("executeAction");
  nc.ui.hrjf.deptadj.action.DeptadjExecuteAction bean = new nc.ui.hrjf.deptadj.action.DeptadjExecuteAction();
  context.put("executeAction",bean);
  bean.setModel(getBmModel());
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

public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler(){
 if(context.get("exceptionHandler")!=null)
 return (nc.ui.uif2.DefaultExceptionHanler)context.get("exceptionHandler");
  nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler(getContainer());  context.put("exceptionHandler",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
