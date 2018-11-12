package nc.ui.twhr.rangetable.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Rangetable_config extends AbstractJavaBeanDefinition{
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

public nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableMaintainProxy getMaintainProxy(){
 if(context.get("maintainProxy")!=null)
 return (nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableMaintainProxy)context.get("maintainProxy");
  nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableMaintainProxy bean = new nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableMaintainProxy();
  context.put("maintainProxy",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableDeleteProxy getDeleteProxy(){
 if(context.get("deleteProxy")!=null)
 return (nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableDeleteProxy)context.get("deleteProxy");
  nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableDeleteProxy bean = new nc.ui.twhr.rangetable.ace.serviceproxy.AceRangetableDeleteProxy();
  context.put("deleteProxy",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory getBoadatorfactory(){
 if(context.get("boadatorfactory")!=null)
 return (nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory)context.get("boadatorfactory");
  nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory bean = new nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory();
  context.put("boadatorfactory",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.BillManageModel getManageAppModel(){
 if(context.get("manageAppModel")!=null)
 return (nc.ui.pubapp.uif2app.model.BillManageModel)context.get("manageAppModel");
  nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
  context.put("manageAppModel",bean);
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.model.ModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.pubapp.uif2app.query2.model.ModelDataManager)context.get("modelDataManager");
  nc.ui.pubapp.uif2app.query2.model.ModelDataManager bean = new nc.ui.pubapp.uif2app.query2.model.ModelDataManager();
  context.put("modelDataManager",bean);
  bean.setModel(getManageAppModel());
  bean.setService(getMaintainProxy());
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

public nc.ui.uif2.editor.QueryTemplateContainer getQueryTemplateContainer(){
 if(context.get("queryTemplateContainer")!=null)
 return (nc.ui.uif2.editor.QueryTemplateContainer)context.get("queryTemplateContainer");
  nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
  context.put("queryTemplateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKey("qt");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.ShowUpableBillListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.pubapp.uif2app.view.ShowUpableBillListView)context.get("listView");
  nc.ui.pubapp.uif2app.view.ShowUpableBillListView bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillListView();
  context.put("listView",bean);
  bean.setModel(getManageAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setUserdefitemListPreparator(getCompositeBillListDataPrepare_1ccfa13());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare getCompositeBillListDataPrepare_1ccfa13(){
 if(context.get("nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare#1ccfa13")!=null)
 return (nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare)context.get("nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare#1ccfa13");
  nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare bean = new nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare();
  context.put("nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare#1ccfa13",bean);
  bean.setBillListDataPrepares(getManagedList1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getUserdefitemlistPreparator());  list.add(getMarAsstPreparator());  return list;}

public nc.ui.uif2.editor.UserdefitemContainerListPreparator getUserdefitemlistPreparator(){
 if(context.get("userdefitemlistPreparator")!=null)
 return (nc.ui.uif2.editor.UserdefitemContainerListPreparator)context.get("userdefitemlistPreparator");
  nc.ui.uif2.editor.UserdefitemContainerListPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerListPreparator();
  context.put("userdefitemlistPreparator",bean);
  bean.setContainer(getUserdefitemContainer());
  bean.setParams(getManagedList2());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getUserdefQueryParam_36019a());  list.add(getUserdefQueryParam_1a32ecd());  return list;}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_36019a(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#36019a")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#36019a");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#36019a",bean);
  bean.setMdfullname("twhr.rangetable");
  bean.setPos(0);
  bean.setPrefix("vdef");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_1a32ecd(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#1a32ecd")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#1a32ecd");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#1a32ecd",bean);
  bean.setMdfullname("twhr.rangeline");
  bean.setPos(1);
  bean.setPrefix("vbdef");
  bean.setTabcode("rangeline");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.ShowUpableBillForm getBillFormEditor(){
 if(context.get("billFormEditor")!=null)
 return (nc.ui.pubapp.uif2app.view.ShowUpableBillForm)context.get("billFormEditor");
  nc.ui.pubapp.uif2app.view.ShowUpableBillForm bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillForm();
  context.put("billFormEditor",bean);
  bean.setModel(getManageAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setShowOrgPanel(false);
  bean.setAutoAddLine(true);
  bean.setBodyLineActions(getManagedList3());
  bean.setUserdefitemPreparator(getCompositeBillDataPrepare_1fa93a1());
  bean.setBlankChildrenFilter(getMultiFieldsBlankChildrenFilter_b2bde5());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getBodyAddLineAction_55a0da());  list.add(getBodyInsertLineAction_1b3768b());  list.add(getBodyDelLineAction_12bb617());  list.add(getBodyCopyLineAction_bfa9d6());  list.add(getBodyPasteLineAction_1272e52());  list.add(getBodyPasteToTailAction_1521871());  list.add(getBodyLineEditAction_c2dfea());  return list;}

private nc.ui.pubapp.uif2app.actions.BodyAddLineAction getBodyAddLineAction_55a0da(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#55a0da")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyAddLineAction)context.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#55a0da");
  nc.ui.pubapp.uif2app.actions.BodyAddLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyAddLineAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#55a0da",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.BodyInsertLineAction getBodyInsertLineAction_1b3768b(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#1b3768b")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyInsertLineAction)context.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#1b3768b");
  nc.ui.pubapp.uif2app.actions.BodyInsertLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyInsertLineAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#1b3768b",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.BodyDelLineAction getBodyDelLineAction_12bb617(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#12bb617")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyDelLineAction)context.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#12bb617");
  nc.ui.pubapp.uif2app.actions.BodyDelLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyDelLineAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#12bb617",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.BodyCopyLineAction getBodyCopyLineAction_bfa9d6(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyCopyLineAction#bfa9d6")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyCopyLineAction)context.get("nc.ui.pubapp.uif2app.actions.BodyCopyLineAction#bfa9d6");
  nc.ui.pubapp.uif2app.actions.BodyCopyLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyCopyLineAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyCopyLineAction#bfa9d6",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.BodyPasteLineAction getBodyPasteLineAction_1272e52(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyPasteLineAction#1272e52")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyPasteLineAction)context.get("nc.ui.pubapp.uif2app.actions.BodyPasteLineAction#1272e52");
  nc.ui.pubapp.uif2app.actions.BodyPasteLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyPasteLineAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyPasteLineAction#1272e52",bean);
  bean.setClearItems(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add("pk_rangeline");  list.add("ts");  return list;}

private nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction getBodyPasteToTailAction_1521871(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction#1521871")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction)context.get("nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction#1521871");
  nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction bean = new nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction#1521871",bean);
  bean.setClearItems(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList5(){  List list = new ArrayList();  list.add("pk_rangeline");  list.add("ts");  return list;}

private nc.ui.pubapp.uif2app.actions.BodyLineEditAction getBodyLineEditAction_c2dfea(){
 if(context.get("nc.ui.pubapp.uif2app.actions.BodyLineEditAction#c2dfea")!=null)
 return (nc.ui.pubapp.uif2app.actions.BodyLineEditAction)context.get("nc.ui.pubapp.uif2app.actions.BodyLineEditAction#c2dfea");
  nc.ui.pubapp.uif2app.actions.BodyLineEditAction bean = new nc.ui.pubapp.uif2app.actions.BodyLineEditAction();
  context.put("nc.ui.pubapp.uif2app.actions.BodyLineEditAction#c2dfea",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare getCompositeBillDataPrepare_1fa93a1(){
 if(context.get("nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare#1fa93a1")!=null)
 return (nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare)context.get("nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare#1fa93a1");
  nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare bean = new nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare();
  context.put("nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare#1fa93a1",bean);
  bean.setBillDataPrepares(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getUserdefitemPreparator());  list.add(getMarAsstPreparator());  return list;}

private nc.ui.pubapp.uif2app.view.value.MultiFieldsBlankChildrenFilter getMultiFieldsBlankChildrenFilter_b2bde5(){
 if(context.get("nc.ui.pubapp.uif2app.view.value.MultiFieldsBlankChildrenFilter#b2bde5")!=null)
 return (nc.ui.pubapp.uif2app.view.value.MultiFieldsBlankChildrenFilter)context.get("nc.ui.pubapp.uif2app.view.value.MultiFieldsBlankChildrenFilter#b2bde5");
  nc.ui.pubapp.uif2app.view.value.MultiFieldsBlankChildrenFilter bean = new nc.ui.pubapp.uif2app.view.value.MultiFieldsBlankChildrenFilter();
  context.put("nc.ui.pubapp.uif2app.view.value.MultiFieldsBlankChildrenFilter#b2bde5",bean);
  bean.setNullAssertByOr(false);
  bean.setFilterMap(getManagedMap0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap0(){  Map map = new HashMap();  map.put("pk_rangeline",getManagedList7());  return map;}

private List getManagedList7(){  List list = new ArrayList();  list.add("rangevalue");  list.add("rangeupper");  list.add("rangelower");  return list;}

public nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemPreparator(){
 if(context.get("userdefitemPreparator")!=null)
 return (nc.ui.uif2.editor.UserdefitemContainerPreparator)context.get("userdefitemPreparator");
  nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
  context.put("userdefitemPreparator",bean);
  bean.setContainer(getUserdefitemContainer());
  bean.setParams(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add(getUserdefQueryParam_d10efa());  list.add(getUserdefQueryParam_12e49c3());  return list;}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_d10efa(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#d10efa")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#d10efa");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#d10efa",bean);
  bean.setMdfullname("twhr.rangetable");
  bean.setPos(0);
  bean.setPrefix("vdef");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_12e49c3(){
 if(context.get("nc.ui.uif2.editor.UserdefQueryParam#12e49c3")!=null)
 return (nc.ui.uif2.editor.UserdefQueryParam)context.get("nc.ui.uif2.editor.UserdefQueryParam#12e49c3");
  nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
  context.put("nc.ui.uif2.editor.UserdefQueryParam#12e49c3",bean);
  bean.setMdfullname("twhr.rangeline");
  bean.setPos(1);
  bean.setPrefix("vbdef");
  bean.setTabcode("rangeline");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator getMarAsstPreparator(){
 if(context.get("marAsstPreparator")!=null)
 return (nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator)context.get("marAsstPreparator");
  nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator bean = new nc.ui.pubapp.uif2app.view.material.assistant.MarAsstPreparator();
  context.put("marAsstPreparator",bean);
  bean.setModel(getManageAppModel());
  bean.setContainer(getUserdefitemContainer());
  bean.setPrefix("vfree");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.userdefitem.UserDefItemContainer getUserdefitemContainer(){
 if(context.get("userdefitemContainer")!=null)
 return (nc.ui.uif2.userdefitem.UserDefItemContainer)context.get("userdefitemContainer");
  nc.ui.uif2.userdefitem.UserDefItemContainer bean = new nc.ui.uif2.userdefitem.UserDefItemContainer();
  context.put("userdefitemContainer",bean);
  bean.setContext(getContext());
  bean.setParams(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getQueryParam_1ecb654());  list.add(getQueryParam_1b65425());  list.add(getQueryParam_14e5966());  return list;}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_1ecb654(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#1ecb654")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#1ecb654");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#1ecb654",bean);
  bean.setMdfullname("twhr.rangetable");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_1b65425(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#1b65425")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#1b65425");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#1b65425",bean);
  bean.setMdfullname("twhr.rangeline");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.userdefitem.QueryParam getQueryParam_14e5966(){
 if(context.get("nc.ui.uif2.userdefitem.QueryParam#14e5966")!=null)
 return (nc.ui.uif2.userdefitem.QueryParam)context.get("nc.ui.uif2.userdefitem.QueryParam#14e5966");
  nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
  context.put("nc.ui.uif2.userdefitem.QueryParam#14e5966",bean);
  bean.setRulecode("materialassistant");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getQueryArea(){
 if(context.get("queryArea")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell)context.get("queryArea");
  nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
  context.put("queryArea",bean);
  bean.setQueryAreaCreator(getQueryAction());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getQueryInfo(){
 if(context.get("queryInfo")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("queryInfo");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("queryInfo",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getCardInfoPnl(){
 if(context.get("cardInfoPnl")!=null)
 return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel)context.get("cardInfoPnl");
  nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
  context.put("cardInfoPnl",bean);
  bean.setTitleAction(getReturnaction());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnaction(){
 if(context.get("returnaction")!=null)
 return (nc.ui.pubapp.uif2app.actions.UEReturnAction)context.get("returnaction");
  nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
  context.put("returnaction",bean);
  bean.setGoComponent(getListView());
  bean.setSaveAction(getSaveAction());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setModel(getManageAppModel());
  bean.setTangramLayoutRoot(getTBNode_1a76740());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1a76740(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#1a76740")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#1a76740");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#1a76740",bean);
  bean.setShowMode("CardLayout");
  bean.setTabs(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList10(){  List list = new ArrayList();  list.add(getHSNode_ba71ac());  list.add(getVSNode_8f5352());  return list;}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_ba71ac(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#ba71ac")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#ba71ac");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#ba71ac",bean);
  bean.setLeft(getCNode_cf76c8());
  bean.setRight(getVSNode_1e8d410());
  bean.setDividerLocation(210f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_cf76c8(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#cf76c8")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#cf76c8");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#cf76c8",bean);
  bean.setComponent(getQueryArea());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1e8d410(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#1e8d410")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#1e8d410");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#1e8d410",bean);
  bean.setUp(getCNode_7f6ff8());
  bean.setDown(getCNode_b75bc0());
  bean.setDividerLocation(25f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_7f6ff8(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#7f6ff8")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#7f6ff8");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#7f6ff8",bean);
  bean.setComponent(getQueryInfo());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_b75bc0(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#b75bc0")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#b75bc0");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#b75bc0",bean);
  bean.setName(getI18nFB_900309());
  bean.setComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_900309(){
 if(context.get("nc.ui.uif2.I18nFB#900309")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#900309");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#900309",bean);  bean.setResDir("common");
  bean.setResId("UC001-0000107");
  bean.setDefaultValue("¡–±Ì");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#900309",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_8f5352(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#8f5352")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#8f5352");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#8f5352",bean);
  bean.setUp(getCNode_7d08eb());
  bean.setDown(getCNode_1800808());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_7d08eb(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#7d08eb")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#7d08eb");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#7d08eb",bean);
  bean.setComponent(getCardInfoPnl());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1800808(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1800808")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1800808");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1800808",bean);
  bean.setName(getI18nFB_d49111());
  bean.setComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_d49111(){
 if(context.get("nc.ui.uif2.I18nFB#d49111")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#d49111");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#d49111",bean);  bean.setResDir("common");
  bean.setResId("UC001-0000106");
  bean.setDefaultValue("ø®∆¨");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#d49111",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.pubapp.uif2app.event.ChildrenPicky getChildrenPicky(){
 if(context.get("childrenPicky")!=null)
 return (nc.ui.pubapp.uif2app.event.ChildrenPicky)context.get("childrenPicky");
  nc.ui.pubapp.uif2app.event.ChildrenPicky bean = new nc.ui.pubapp.uif2app.event.ChildrenPicky();
  context.put("childrenPicky",bean);
  bean.setBillform(getBillFormEditor());
  bean.setBodyVoClasses(getManagedList11());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList11(){  List list = new ArrayList();  list.add("nc.vo.twhr.rangetable.RangeLineVO");  return list;}

public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getEventMediator(){
 if(context.get("eventMediator")!=null)
 return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator)context.get("eventMediator");
  nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
  context.put("eventMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setHandlerGroup(getManagedList12());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList12(){  List list = new ArrayList();  list.add(getEventHandlerGroup_1a69a9c());  list.add(getEventHandlerGroup_1129103());  list.add(getEventHandlerGroup_1237a89());  list.add(getEventHandlerGroup_76ced6());  list.add(getEventHandlerGroup_18d3fca());  list.add(getEventHandlerGroup_c87802());  return list;}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1a69a9c(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a69a9c")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a69a9c");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a69a9c",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent");
  bean.setPicky(getChildrenPicky());
  bean.setHandler(getAceBodyBeforeEditHandler_16baafc());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.twhr.rangetable.ace.handler.AceBodyBeforeEditHandler getAceBodyBeforeEditHandler_16baafc(){
 if(context.get("nc.ui.twhr.rangetable.ace.handler.AceBodyBeforeEditHandler#16baafc")!=null)
 return (nc.ui.twhr.rangetable.ace.handler.AceBodyBeforeEditHandler)context.get("nc.ui.twhr.rangetable.ace.handler.AceBodyBeforeEditHandler#16baafc");
  nc.ui.twhr.rangetable.ace.handler.AceBodyBeforeEditHandler bean = new nc.ui.twhr.rangetable.ace.handler.AceBodyBeforeEditHandler();
  context.put("nc.ui.twhr.rangetable.ace.handler.AceBodyBeforeEditHandler#16baafc",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1129103(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1129103")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1129103");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1129103",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent");
  bean.setPicky(getChildrenPicky());
  bean.setHandler(getAceBodyAfterEditHandler_f07ec4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.twhr.rangetable.ace.handler.AceBodyAfterEditHandler getAceBodyAfterEditHandler_f07ec4(){
 if(context.get("nc.ui.twhr.rangetable.ace.handler.AceBodyAfterEditHandler#f07ec4")!=null)
 return (nc.ui.twhr.rangetable.ace.handler.AceBodyAfterEditHandler)context.get("nc.ui.twhr.rangetable.ace.handler.AceBodyAfterEditHandler#f07ec4");
  nc.ui.twhr.rangetable.ace.handler.AceBodyAfterEditHandler bean = new nc.ui.twhr.rangetable.ace.handler.AceBodyAfterEditHandler();
  context.put("nc.ui.twhr.rangetable.ace.handler.AceBodyAfterEditHandler#f07ec4",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1237a89(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1237a89")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1237a89");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1237a89",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent");
  bean.setHandler(getAceHeadTailBeforeEditHandler_1ee68d1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.twhr.rangetable.ace.handler.AceHeadTailBeforeEditHandler getAceHeadTailBeforeEditHandler_1ee68d1(){
 if(context.get("nc.ui.twhr.rangetable.ace.handler.AceHeadTailBeforeEditHandler#1ee68d1")!=null)
 return (nc.ui.twhr.rangetable.ace.handler.AceHeadTailBeforeEditHandler)context.get("nc.ui.twhr.rangetable.ace.handler.AceHeadTailBeforeEditHandler#1ee68d1");
  nc.ui.twhr.rangetable.ace.handler.AceHeadTailBeforeEditHandler bean = new nc.ui.twhr.rangetable.ace.handler.AceHeadTailBeforeEditHandler();
  context.put("nc.ui.twhr.rangetable.ace.handler.AceHeadTailBeforeEditHandler#1ee68d1",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_76ced6(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#76ced6")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#76ced6");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#76ced6",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent");
  bean.setHandler(getAceHeadTailAfterEditHandler_41d6b4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.twhr.rangetable.ace.handler.AceHeadTailAfterEditHandler getAceHeadTailAfterEditHandler_41d6b4(){
 if(context.get("nc.ui.twhr.rangetable.ace.handler.AceHeadTailAfterEditHandler#41d6b4")!=null)
 return (nc.ui.twhr.rangetable.ace.handler.AceHeadTailAfterEditHandler)context.get("nc.ui.twhr.rangetable.ace.handler.AceHeadTailAfterEditHandler#41d6b4");
  nc.ui.twhr.rangetable.ace.handler.AceHeadTailAfterEditHandler bean = new nc.ui.twhr.rangetable.ace.handler.AceHeadTailAfterEditHandler();
  context.put("nc.ui.twhr.rangetable.ace.handler.AceHeadTailAfterEditHandler#41d6b4",bean);
  bean.setGenAction(getGenerateAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_18d3fca(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#18d3fca")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#18d3fca");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#18d3fca",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.billform.AddEvent");
  bean.setHandler(getAceAddHandler_5629d());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.twhr.rangetable.ace.handler.AceAddHandler getAceAddHandler_5629d(){
 if(context.get("nc.ui.twhr.rangetable.ace.handler.AceAddHandler#5629d")!=null)
 return (nc.ui.twhr.rangetable.ace.handler.AceAddHandler)context.get("nc.ui.twhr.rangetable.ace.handler.AceAddHandler#5629d");
  nc.ui.twhr.rangetable.ace.handler.AceAddHandler bean = new nc.ui.twhr.rangetable.ace.handler.AceAddHandler();
  context.put("nc.ui.twhr.rangetable.ace.handler.AceAddHandler#5629d",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_c87802(){
 if(context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#c87802")!=null)
 return (nc.ui.pubapp.uif2app.event.EventHandlerGroup)context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#c87802");
  nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
  context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#c87802",bean);
  bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
  bean.setHandler(getAceOrgChangedHandler_1515d5c());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.twhr.rangetable.ace.handler.AceOrgChangedHandler getAceOrgChangedHandler_1515d5c(){
 if(context.get("nc.ui.twhr.rangetable.ace.handler.AceOrgChangedHandler#1515d5c")!=null)
 return (nc.ui.twhr.rangetable.ace.handler.AceOrgChangedHandler)context.get("nc.ui.twhr.rangetable.ace.handler.AceOrgChangedHandler#1515d5c");
  nc.ui.twhr.rangetable.ace.handler.AceOrgChangedHandler bean = new nc.ui.twhr.rangetable.ace.handler.AceOrgChangedHandler(getBillFormEditor());  context.put("nc.ui.twhr.rangetable.ace.handler.AceOrgChangedHandler#1515d5c",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors(){
 if(context.get("toftpanelActionContributors")!=null)
 return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
  nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
  context.put("toftpanelActionContributors",bean);
  bean.setContributors(getManagedList13());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList13(){  List list = new ArrayList();  list.add(getActionsOfList());  list.add(getActionsOfCard());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfList(){
 if(context.get("actionsOfList")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfList");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListView());  context.put("actionsOfList",bean);
  bean.setModel(getManageAppModel());
  bean.setActions(getManagedList14());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList14(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getCopyAction());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getSeparatorAction());  list.add(getPrintMenuAction());  list.add(getSeparatorAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfCard(){
 if(context.get("actionsOfCard")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("actionsOfCard");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillFormEditor());  context.put("actionsOfCard",bean);
  bean.setModel(getManageAppModel());
  bean.setActions(getManagedList15());
  bean.setEditActions(getManagedList16());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList15(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getCopyAction());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getCardRefreshAction());  list.add(getSeparatorAction());  list.add(getPrintMenuAction());  list.add(getSeparatorAction());  return list;}

private List getManagedList16(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getCancelAction());  list.add(getSeparatorAction());  list.add(getGenerateAction());  return list;}

public nc.funcnode.ui.action.SeparatorAction getSeparatorAction(){
 if(context.get("separatorAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("separatorAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("separatorAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowListInterceptor(){
 if(context.get("showListInterceptor")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("showListInterceptor");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("showListInterceptor",bean);
  bean.setShowUpComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowCardInterceptor(){
 if(context.get("showCardInterceptor")!=null)
 return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor)context.get("showCardInterceptor");
  nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
  context.put("showCardInterceptor",bean);
  bean.setShowUpComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.twhr.rangetable.action.RangetableAddAction getAddAction(){
 if(context.get("addAction")!=null)
 return (nc.ui.twhr.rangetable.action.RangetableAddAction)context.get("addAction");
  nc.ui.twhr.rangetable.action.RangetableAddAction bean = new nc.ui.twhr.rangetable.action.RangetableAddAction(getBillFormEditor());  context.put("addAction",bean);
  bean.setModel(getManageAppModel());
  bean.setInterceptor(getShowCardInterceptor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.twhr.rangetable.action.RangetableEditAction getEditAction(){
 if(context.get("editAction")!=null)
 return (nc.ui.twhr.rangetable.action.RangetableEditAction)context.get("editAction");
  nc.ui.twhr.rangetable.action.RangetableEditAction bean = new nc.ui.twhr.rangetable.action.RangetableEditAction();
  context.put("editAction",bean);
  bean.setModel(getManageAppModel());
  bean.setInterceptor(getShowCardInterceptor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.DeleteAction getDeleteAction(){
 if(context.get("deleteAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.DeleteAction)context.get("deleteAction");
  nc.ui.pubapp.uif2app.actions.DeleteAction bean = new nc.ui.pubapp.uif2app.actions.DeleteAction();
  context.put("deleteAction",bean);
  bean.setModel(getManageAppModel());
  bean.setSingleBillService(getDeleteProxy());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.twhr.rangetable.action.RangetableSaveAction getSaveAction(){
 if(context.get("saveAction")!=null)
 return (nc.ui.twhr.rangetable.action.RangetableSaveAction)context.get("saveAction");
  nc.ui.twhr.rangetable.action.RangetableSaveAction bean = new nc.ui.twhr.rangetable.action.RangetableSaveAction();
  context.put("saveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setService(getMaintainProxy());
  bean.setValidationService(getValidateService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.twhr.rangetable.action.RangetableGenerateAction getGenerateAction(){
 if(context.get("generateAction")!=null)
 return (nc.ui.twhr.rangetable.action.RangetableGenerateAction)context.get("generateAction");
  nc.ui.twhr.rangetable.action.RangetableGenerateAction bean = new nc.ui.twhr.rangetable.action.RangetableGenerateAction();
  context.put("generateAction",bean);
  bean.setEditor(getBillFormEditor());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.validation.CompositeValidation getValidateService(){
 if(context.get("validateService")!=null)
 return (nc.ui.pubapp.uif2app.validation.CompositeValidation)context.get("validateService");
  nc.ui.pubapp.uif2app.validation.CompositeValidation bean = new nc.ui.pubapp.uif2app.validation.CompositeValidation();
  context.put("validateService",bean);
  bean.setValidators(getManagedList17());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList17(){  List list = new ArrayList();  list.add(getTemplateNotNullValidation_13ed2e3());  return list;}

private nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation getTemplateNotNullValidation_13ed2e3(){
 if(context.get("nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation#13ed2e3")!=null)
 return (nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation)context.get("nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation#13ed2e3");
  nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation bean = new nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation();
  context.put("nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation#13ed2e3",bean);
  bean.setBillForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.CancelAction getCancelAction(){
 if(context.get("cancelAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.CancelAction)context.get("cancelAction");
  nc.ui.pubapp.uif2app.actions.CancelAction bean = new nc.ui.pubapp.uif2app.actions.CancelAction();
  context.put("cancelAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.CopyAction getCopyAction(){
 if(context.get("copyAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.CopyAction)context.get("copyAction");
  nc.ui.pubapp.uif2app.actions.CopyAction bean = new nc.ui.pubapp.uif2app.actions.CopyAction();
  context.put("copyAction",bean);
  bean.setModel(getManageAppModel());
  bean.setInterceptor(getShowCardInterceptor());
  bean.setEditor(getBillFormEditor());
  bean.setCopyActionProcessor(getCopyActionProcessor_1873ad9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.twhr.rangetable.action.CopyActionProcessor getCopyActionProcessor_1873ad9(){
 if(context.get("nc.ui.twhr.rangetable.action.CopyActionProcessor#1873ad9")!=null)
 return (nc.ui.twhr.rangetable.action.CopyActionProcessor)context.get("nc.ui.twhr.rangetable.action.CopyActionProcessor#1873ad9");
  nc.ui.twhr.rangetable.action.CopyActionProcessor bean = new nc.ui.twhr.rangetable.action.CopyActionProcessor();
  context.put("nc.ui.twhr.rangetable.action.CopyActionProcessor#1873ad9",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.twhr.rangetable.query.RangetableQueryConditionInitializer getQryCondInitializer(){
 if(context.get("qryCondInitializer")!=null)
 return (nc.ui.twhr.rangetable.query.RangetableQueryConditionInitializer)context.get("qryCondInitializer");
  nc.ui.twhr.rangetable.query.RangetableQueryConditionInitializer bean = new nc.ui.twhr.rangetable.query.RangetableQueryConditionInitializer();
  context.put("qryCondInitializer",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getQueryAction(){
 if(context.get("queryAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction)context.get("queryAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
  context.put("queryAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setQryCondDLGInitializer(getQryCondInitializer());
  bean.setShowUpComponent(getListView());
  bean.setTemplateContainer(getQueryTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction)context.get("refreshAction");
  nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
  context.put("refreshAction",bean);
  bean.setDataManager(getModelDataManager());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.RefreshSingleAction getCardRefreshAction(){
 if(context.get("cardRefreshAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.RefreshSingleAction)context.get("cardRefreshAction");
  nc.ui.pubapp.uif2app.actions.RefreshSingleAction bean = new nc.ui.pubapp.uif2app.actions.RefreshSingleAction();
  context.put("cardRefreshAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getPrintAction(){
 if(context.get("printAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction)context.get("printAction");
  nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
  context.put("printAction",bean);
  bean.setPreview(false);
  bean.setModel(getManageAppModel());
  bean.setNodeKey("ot");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getPreviewAction(){
 if(context.get("previewAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction)context.get("previewAction");
  nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
  context.put("previewAction",bean);
  bean.setPreview(true);
  bean.setModel(getManageAppModel());
  bean.setNodeKey("ot");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction(){
 if(context.get("outputAction")!=null)
 return (nc.ui.pubapp.uif2app.actions.OutputAction)context.get("outputAction");
  nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
  context.put("outputAction",bean);
  bean.setModel(getManageAppModel());
  bean.setParent(getBillFormEditor());
  bean.setNodeKey("ot");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintMenuAction(){
 if(context.get("printMenuAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("printMenuAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("printMenuAction",bean);
  bean.setCode("printMenuAction");
  bean.setName(getI18nFB_128a245());
  bean.setActions(getManagedList18());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_128a245(){
 if(context.get("nc.ui.uif2.I18nFB#128a245")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#128a245");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#128a245",bean);  bean.setResDir("common");
  bean.setResId("UC001-0000007");
  bean.setDefaultValue("¥Ú”°");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#128a245",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList18(){  List list = new ArrayList();  list.add(getPrintAction());  list.add(getPreviewAction());  list.add(getOutputAction());  return list;}

public nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader getBillLazilyLoader(){
 if(context.get("billLazilyLoader")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader)context.get("billLazilyLoader");
  nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader bean = new nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader();
  context.put("billLazilyLoader",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager getLasilyLodadMediator(){
 if(context.get("lasilyLodadMediator")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager)context.get("lasilyLodadMediator");
  nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager bean = new nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager();
  context.put("lasilyLodadMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setLoader(getBillLazilyLoader());
  bean.setLazilyLoadSupporter(getManagedList19());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList19(){  List list = new ArrayList();  list.add(getCardPanelLazilyLoad_b58796());  list.add(getListPanelLazilyLoad_1d89cb4());  return list;}

private nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad getCardPanelLazilyLoad_b58796(){
 if(context.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#b58796")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad)context.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#b58796");
  nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad();
  context.put("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#b58796",bean);
  bean.setBillform(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad getListPanelLazilyLoad_1d89cb4(){
 if(context.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#1d89cb4")!=null)
 return (nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad)context.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#1d89cb4");
  nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad();
  context.put("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#1d89cb4",bean);
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.BillBodySortMediator getBillBodySortMediator(){
 if(context.get("billBodySortMediator")!=null)
 return (nc.ui.pubapp.uif2app.model.BillBodySortMediator)context.get("billBodySortMediator");
  nc.ui.pubapp.uif2app.model.BillBodySortMediator bean = new nc.ui.pubapp.uif2app.model.BillBodySortMediator(getManageAppModel(),getBillFormEditor(),getListView());  context.put("billBodySortMediator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener(){
 if(context.get("InitDataListener")!=null)
 return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener)context.get("InitDataListener");
  nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
  context.put("InitDataListener",bean);
  bean.setModel(getManageAppModel());
  bean.setContext(getContext());
  bean.setVoClassName("nc.vo.twhr.rangetable.RangeTableAggVO");
  bean.setAutoShowUpComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.pubapp.common.validateservice.ClosingCheck)context.get("ClosingListener");
  nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
  context.put("ClosingListener",bean);
  bean.setModel(getManageAppModel());
  bean.setSaveAction(getSaveAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.FractionFixMediator getFractionFixMediator(){
 if(context.get("fractionFixMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.FractionFixMediator)context.get("fractionFixMediator");
  nc.ui.pubapp.uif2app.view.FractionFixMediator bean = new nc.ui.pubapp.uif2app.view.FractionFixMediator(getBillFormEditor());  context.put("fractionFixMediator",bean);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.bill.BillCodeMediator getBillCodeMediator(){
 if(context.get("billCodeMediator")!=null)
 return (nc.ui.pubapp.bill.BillCodeMediator)context.get("billCodeMediator");
  nc.ui.pubapp.bill.BillCodeMediator bean = new nc.ui.pubapp.bill.BillCodeMediator();
  context.put("billCodeMediator",bean);
  bean.setBillForm(getBillFormEditor());
  bean.setBillCodeKey(".{vbillcode}");
  bean.setBillType("TWRT");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator(){
 if(context.get("rowNoMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.RowNoMediator)context.get("rowNoMediator");
  nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
  context.put("rowNoMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller getRemoteCallCombinatorCaller(){
 if(context.get("remoteCallCombinatorCaller")!=null)
 return (nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller)context.get("remoteCallCombinatorCaller");
  nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller bean = new nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller();
  context.put("remoteCallCombinatorCaller",bean);
  bean.setRemoteCallers(getManagedList20());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList20(){  List list = new ArrayList();  list.add(getQueryTemplateContainer());  list.add(getTemplateContainer());  list.add(getUserdefitemContainer());  return list;}

public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getDoubleClickMediator(){
 if(context.get("doubleClickMediator")!=null)
 return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator)context.get("doubleClickMediator");
  nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
  context.put("doubleClickMediator",bean);
  bean.setListView(getListView());
  bean.setShowUpComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
