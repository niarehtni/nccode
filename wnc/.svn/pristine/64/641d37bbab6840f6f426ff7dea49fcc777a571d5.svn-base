package nc.ui.ta.dataprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class dataprocess_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.ta.pub.SignCardLoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.ta.pub.SignCardLoginContext)context.get("context");
  nc.vo.ta.pub.SignCardLoginContext bean = new nc.vo.ta.pub.SignCardLoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.pub.UFLiteralDateBDObjectAdapterFactory getUfliteraldateboadatorfactory(){
 if(context.get("ufliteraldateboadatorfactory")!=null)
 return (nc.ui.ta.pub.UFLiteralDateBDObjectAdapterFactory)context.get("ufliteraldateboadatorfactory");
  nc.ui.ta.pub.UFLiteralDateBDObjectAdapterFactory bean = new nc.ui.ta.pub.UFLiteralDateBDObjectAdapterFactory();
  context.put("ufliteraldateboadatorfactory",bean);
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

public nc.ui.ta.pub.QueryEditorListener getQueryEditorListener(){
 if(context.get("queryEditorListener")!=null)
 return (nc.ui.ta.pub.QueryEditorListener)context.get("queryEditorListener");
  nc.ui.ta.pub.QueryEditorListener bean = new nc.ui.ta.pub.QueryEditorListener();
  context.put("queryEditorListener",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.PsnAppModel getPsnModel(){
 if(context.get("psnModel")!=null)
 return (nc.ui.ta.dailydata.model.PsnAppModel)context.get("psnModel");
  nc.ui.ta.dailydata.model.PsnAppModel bean = new nc.ui.ta.dailydata.model.PsnAppModel();
  context.put("psnModel",bean);
  bean.setContext(getContext());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.datamanager.PsnAppModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.ta.dailydata.model.datamanager.PsnAppModelDataManager)context.get("modelDataManager");
  nc.ui.ta.dailydata.model.datamanager.PsnAppModelDataManager bean = new nc.ui.ta.dailydata.model.datamanager.PsnAppModelDataManager();
  context.put("modelDataManager",bean);
  bean.setModel(getPsnModel());
  bean.setService(getPsnModelService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.service.PsnAppModelService getPsnModelService(){
 if(context.get("psnModelService")!=null)
 return (nc.ui.ta.dailydata.model.service.PsnAppModelService)context.get("psnModelService");
  nc.ui.ta.dailydata.model.service.PsnAppModelService bean = new nc.ui.ta.dailydata.model.service.PsnAppModelService();
  context.put("psnModelService",bean);
  bean.setTbmProp(getTbmProp());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.DailyDataAppModel getPsnDailyDataModel(){
 if(context.get("psnDailyDataModel")!=null)
 return (nc.ui.ta.dailydata.model.DailyDataAppModel)context.get("psnDailyDataModel");
  nc.ui.ta.dailydata.model.DailyDataAppModel bean = new nc.ui.ta.dailydata.model.DailyDataAppModel();
  context.put("psnDailyDataModel",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4PsnView getPsnDailyDataModelDataManager(){
 if(context.get("psnDailyDataModelDataManager")!=null)
 return (nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4PsnView)context.get("psnDailyDataModelDataManager");
  nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4PsnView bean = new nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4PsnView();
  context.put("psnDailyDataModelDataManager",bean);
  bean.setModel(getPsnDailyDataModel());
  bean.setService(getDailyDataService());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.mediator.PsnAndDailyDataMediator getPsnMediator(){
 if(context.get("psnMediator")!=null)
 return (nc.ui.ta.dailydata.model.mediator.PsnAndDailyDataMediator)context.get("psnMediator");
  nc.ui.ta.dailydata.model.mediator.PsnAndDailyDataMediator bean = new nc.ui.ta.dailydata.model.mediator.PsnAndDailyDataMediator();
  context.put("psnMediator",bean);
  bean.setPsnAppModel(getPsnModel());
  bean.setDailyDataAppModel(getPsnDailyDataModel());
  bean.setDataManager(getPsnDailyDataModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.DateAppModel getDateModel(){
 if(context.get("dateModel")!=null)
 return (nc.ui.ta.dailydata.model.DateAppModel)context.get("dateModel");
  nc.ui.ta.dailydata.model.DateAppModel bean = new nc.ui.ta.dailydata.model.DateAppModel();
  context.put("dateModel",bean);
  bean.setTreeCreateStrategy(getTreeCreateStrategy());
  bean.setBusinessObjectAdapterFactory(getUfliteraldateboadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.datamanager.DateAppModelDataManager getDateModelDataManager(){
 if(context.get("dateModelDataManager")!=null)
 return (nc.ui.ta.dailydata.model.datamanager.DateAppModelDataManager)context.get("dateModelDataManager");
  nc.ui.ta.dailydata.model.datamanager.DateAppModelDataManager bean = new nc.ui.ta.dailydata.model.datamanager.DateAppModelDataManager();
  context.put("dateModelDataManager",bean);
  bean.setModel(getDateModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.DailyDataAppModel getDateDailyDataModel(){
 if(context.get("dateDailyDataModel")!=null)
 return (nc.ui.ta.dailydata.model.DailyDataAppModel)context.get("dateDailyDataModel");
  nc.ui.ta.dailydata.model.DailyDataAppModel bean = new nc.ui.ta.dailydata.model.DailyDataAppModel();
  context.put("dateDailyDataModel",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4DateView getDateDailyDataModelDataManager(){
 if(context.get("dateDailyDataModelDataManager")!=null)
 return (nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4DateView)context.get("dateDailyDataModelDataManager");
  nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4DateView bean = new nc.ui.ta.dailydata.model.datamanager.DailyDataAppModelDataManager4DateView();
  context.put("dateDailyDataModelDataManager",bean);
  bean.setModel(getDateDailyDataModel());
  bean.setService(getDailyDataService());
  bean.setPaginationModel(getPaginationModel());
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

public nc.ui.ta.dailydata.model.mediator.DateAndDailyDataMediator getDateMediator(){
 if(context.get("dateMediator")!=null)
 return (nc.ui.ta.dailydata.model.mediator.DateAndDailyDataMediator)context.get("dateMediator");
  nc.ui.ta.dailydata.model.mediator.DateAndDailyDataMediator bean = new nc.ui.ta.dailydata.model.mediator.DateAndDailyDataMediator();
  context.put("dateMediator",bean);
  bean.setDateAppModel(getDateModel());
  bean.setDailyDataAppModel(getDateDailyDataModel());
  bean.setDataManager(getDateDailyDataModelDataManager());
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

private List getManagedList0(){  List list = new ArrayList();  list.add(getPsnActions());  list.add(getDateActions());  return list;}

public nc.ui.ta.dailydata.view.PsnPanel getPsnPanel(){
 if(context.get("psnPanel")!=null)
 return (nc.ui.ta.dailydata.view.PsnPanel)context.get("psnPanel");
  nc.ui.ta.dailydata.view.PsnPanel bean = new nc.ui.ta.dailydata.view.PsnPanel();
  context.put("psnPanel",bean);
  bean.setModel(getPsnModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.TreePanel getDatePanel(){
 if(context.get("datePanel")!=null)
 return (nc.ui.uif2.components.TreePanel)context.get("datePanel");
  nc.ui.uif2.components.TreePanel bean = new nc.ui.uif2.components.TreePanel();
  context.put("datePanel",bean);
  bean.setModel(getDateModel());
  bean.setRootvisibleflag(true);
  bean.setTreeCellRenderer(getTreeCellRenderer());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreeCreateStrategy(){
 if(context.get("treeCreateStrategy")!=null)
 return (nc.vo.bd.meta.BDObjectTreeCreateStrategy)context.get("treeCreateStrategy");
  nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
  context.put("treeCreateStrategy",bean);
  bean.setFactory(getUfliteraldateboadatorfactory());
  bean.setRootName(getI18nFB_18fdf83());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_18fdf83(){
 if(context.get("nc.ui.uif2.I18nFB#18fdf83")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#18fdf83");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#18fdf83",bean);  bean.setResDir("common");
  bean.setDefaultValue("日期");
  bean.setResId("UC000-0002313");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#18fdf83",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.ta.dailydata.view.TreeDateCellRenderer getTreeCellRenderer(){
 if(context.get("treeCellRenderer")!=null)
 return (nc.ui.ta.dailydata.view.TreeDateCellRenderer)context.get("treeCellRenderer");
  nc.ui.ta.dailydata.view.TreeDateCellRenderer bean = new nc.ui.ta.dailydata.view.TreeDateCellRenderer();
  context.put("treeCellRenderer",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getFilterAction(){
 if(context.get("FilterAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("FilterAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("FilterAction",bean);
  bean.setCode("filter");
  bean.setName(getI18nFB_9b24b9());
  bean.setActions(getManagedList1());
  bean.setEnabled(true);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_9b24b9(){
 if(context.get("nc.ui.uif2.I18nFB#9b24b9")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#9b24b9");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#9b24b9",bean);  bean.setResDir("common");
  bean.setDefaultValue("过滤");
  bean.setResId("UCH069");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#9b24b9",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList1(){  List list = new ArrayList();  list.add(getOnlyShowExceptionAction());  return list;}

public nc.ui.ta.dailydata.action.OnlyShowExceptionAction getOnlyShowExceptionAction(){
 if(context.get("OnlyShowExceptionAction")!=null)
 return (nc.ui.ta.dailydata.action.OnlyShowExceptionAction)context.get("OnlyShowExceptionAction");
  nc.ui.ta.dailydata.action.OnlyShowExceptionAction bean = new nc.ui.ta.dailydata.action.OnlyShowExceptionAction();
  context.put("OnlyShowExceptionAction",bean);
  bean.setDateViewDataManager(getDateDailyDataModelDataManager());
  bean.setPsnViewDataManager(getPsnDailyDataModelDataManager());
  bean.setModel(getPsnModel());
  bean.setEnabled(false);
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.action.QueryDailyDataAction getQueryAction(){
 if(context.get("QueryAction")!=null)
 return (nc.ui.ta.dailydata.action.QueryDailyDataAction)context.get("QueryAction");
  nc.ui.ta.dailydata.action.QueryDailyDataAction bean = new nc.ui.ta.dailydata.action.QueryDailyDataAction();
  context.put("QueryAction",bean);
  bean.setModel(getPsnModel());
  bean.setFromWhereSQLDataManager(getModelDataManager());
  bean.setDateManager(getDateModelDataManager());
  bean.setDateViewDailyDataManager(getDateDailyDataModelDataManager());
  bean.setEnabled(false);
  bean.setQueryDelegator(getFromWhereSQLDateScopeQueryDelegator_187f219());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator getFromWhereSQLDateScopeQueryDelegator_187f219(){
 if(context.get("nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator#187f219")!=null)
 return (nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator)context.get("nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator#187f219");
  nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator bean = new nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator();
  context.put("nc.ui.ta.pub.quereytemplate.FromWhereSQLDateScopeQueryDelegator#187f219",bean);
  bean.setNodeKey("psnquery");
  bean.setContext(getContext());
  bean.setModel(getPsnModel());
  bean.setQueryEditorListener(getQueryEditorListener());
  bean.getQueryDlg();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.action.RefreshDailyDataAction getRefreshAction(){
 if(context.get("RefreshAction")!=null)
 return (nc.ui.ta.dailydata.action.RefreshDailyDataAction)context.get("RefreshAction");
  nc.ui.ta.dailydata.action.RefreshDailyDataAction bean = new nc.ui.ta.dailydata.action.RefreshDailyDataAction();
  context.put("RefreshAction",bean);
  bean.setPsnManager(getModelDataManager());
  bean.setModel(getPsnModel());
  bean.setDateAppModel(getDateModel());
  bean.setEnabled(false);
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_32e6b6());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_32e6b6(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#32e6b6")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#32e6b6");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#32e6b6",bean);
  bean.setUp(getCNode_274818());
  bean.setDown(getTBNode_1e8ed3());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_274818(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#274818")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#274818");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#274818",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1e8ed3(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#1e8ed3")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#1e8ed3");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#1e8ed3",bean);
  bean.setTabs(getManagedList2());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList2(){  List list = new ArrayList();  list.add(getHSNode_b2a08b());  list.add(getHSNode_cc32da());  return list;}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_b2a08b(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#b2a08b")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#b2a08b");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#b2a08b",bean);
  bean.setName(getI18nFB_ee21c9());
  bean.setLeft(getCNode_1ec2b31());
  bean.setRight(getCNode_7a82e());
  bean.setDividerLocation(0.24f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_ee21c9(){
 if(context.get("nc.ui.uif2.I18nFB#ee21c9")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#ee21c9");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#ee21c9",bean);  bean.setResDir("6017dataprocess");
  bean.setDefaultValue("人员浏览");
  bean.setResId("X6017dataprocess02");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#ee21c9",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1ec2b31(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1ec2b31")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1ec2b31");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1ec2b31",bean);
  bean.setComponent(getPsnPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_7a82e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#7a82e")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#7a82e");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#7a82e",bean);
  bean.setComponent(getPsnDailyDataPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_cc32da(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#cc32da")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#cc32da");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#cc32da",bean);
  bean.setName(getI18nFB_14a3f7a());
  bean.setLeft(getCNode_18d9376());
  bean.setRight(getCNode_1cc29cc());
  bean.setDividerLocation(0.24f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_14a3f7a(){
 if(context.get("nc.ui.uif2.I18nFB#14a3f7a")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#14a3f7a");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#14a3f7a",bean);  bean.setResDir("6017dataprocess");
  bean.setDefaultValue("日期浏览");
  bean.setResId("X6017dataprocess03");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#14a3f7a",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_18d9376(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#18d9376")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#18d9376");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#18d9376",bean);
  bean.setComponent(getDatePanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_1cc29cc(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#1cc29cc")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#1cc29cc");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#1cc29cc",bean);
  bean.setComponent(getDateDailyDataPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dailydata.view.DailyDataOrgPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.ta.dailydata.view.DailyDataOrgPanel)context.get("orgpanel");
  nc.ui.ta.dailydata.view.DailyDataOrgPanel bean = new nc.ui.ta.dailydata.view.DailyDataOrgPanel();
  context.put("orgpanel",bean);
  bean.setModel(getPsnDailyDataModel());
  bean.setDateDailyDataModel(getDateDailyDataModel());
  bean.setDataManager(getModelDataManager());
  bean.setDateManager(getDateModelDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.pub.action.EnableJudge getEnableJudge(){
 if(context.get("enableJudge")!=null)
 return (nc.ui.ta.pub.action.EnableJudge)context.get("enableJudge");
  nc.ui.ta.pub.action.EnableJudge bean = new nc.ui.ta.pub.action.EnableJudge();
  context.put("enableJudge",bean);
  bean.setModel(getPsnDailyDataModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.model.TimeDataModelService getDailyDataService(){
 if(context.get("dailyDataService")!=null)
 return (nc.ui.ta.dataprocess.model.TimeDataModelService)context.get("dailyDataService");
  nc.ui.ta.dataprocess.model.TimeDataModelService bean = new nc.ui.ta.dataprocess.model.TimeDataModelService();
  context.put("dailyDataService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public java.lang.Integer getTbmProp(){
 if(context.get("tbmProp")!=null)
 return (java.lang.Integer)context.get("tbmProp");
  java.lang.Integer bean = Integer.valueOf(2);  context.put("tbmProp",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.view.TimeDataTempletBodyVOCreator getTempletBodyVOCreator(){
 if(context.get("templetBodyVOCreator")!=null)
 return (nc.ui.ta.dataprocess.view.TimeDataTempletBodyVOCreator)context.get("templetBodyVOCreator");
  nc.ui.ta.dataprocess.view.TimeDataTempletBodyVOCreator bean = new nc.ui.ta.dataprocess.view.TimeDataTempletBodyVOCreator();
  context.put("templetBodyVOCreator",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.view.PsnTimeDataPanel getPsnDailyDataPanel(){
 if(context.get("psnDailyDataPanel")!=null)
 return (nc.ui.ta.dataprocess.view.PsnTimeDataPanel)context.get("psnDailyDataPanel");
  nc.ui.ta.dataprocess.view.PsnTimeDataPanel bean = new nc.ui.ta.dataprocess.view.PsnTimeDataPanel();
  context.put("psnDailyDataPanel",bean);
  bean.setModel(getPsnDailyDataModel());
  bean.setTempletBodyVOCreator(getTempletBodyVOCreator());
  bean.setMetaDataName("timedata");
  bean.setCellRenderer(getPsnTimedataCellRenderer());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.view.DateTimeDataPanel getDateDailyDataPanel(){
 if(context.get("dateDailyDataPanel")!=null)
 return (nc.ui.ta.dataprocess.view.DateTimeDataPanel)context.get("dateDailyDataPanel");
  nc.ui.ta.dataprocess.view.DateTimeDataPanel bean = new nc.ui.ta.dataprocess.view.DateTimeDataPanel();
  context.put("dateDailyDataPanel",bean);
  bean.setModel(getDateDailyDataModel());
  bean.setTempletBodyVOCreator(getTempletBodyVOCreator());
  bean.setMetaDataName("timedata");
  bean.setCellRenderer(getDateTimedataCellRenderer());
  bean.setSouth(getPaginationBar());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.components.pagination.PaginationModel getPaginationModel(){
 if(context.get("paginationModel")!=null)
 return (nc.ui.uif2.components.pagination.PaginationModel)context.get("paginationModel");
  nc.ui.uif2.components.pagination.PaginationModel bean = new nc.ui.uif2.components.pagination.PaginationModel();
  context.put("paginationModel",bean);
  bean.setPaginationQueryService(getDailyDataService());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.view.TimeDataCellRenderer getPsnTimedataCellRenderer(){
 if(context.get("psnTimedataCellRenderer")!=null)
 return (nc.ui.ta.dataprocess.view.TimeDataCellRenderer)context.get("psnTimedataCellRenderer");
  nc.ui.ta.dataprocess.view.TimeDataCellRenderer bean = new nc.ui.ta.dataprocess.view.TimeDataCellRenderer();
  context.put("psnTimedataCellRenderer",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.view.TimeDataCellRenderer getDateTimedataCellRenderer(){
 if(context.get("dateTimedataCellRenderer")!=null)
 return (nc.ui.ta.dataprocess.view.TimeDataCellRenderer)context.get("dateTimedataCellRenderer");
  nc.ui.ta.dataprocess.view.TimeDataCellRenderer bean = new nc.ui.ta.dataprocess.view.TimeDataCellRenderer();
  context.put("dateTimedataCellRenderer",bean);
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

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getPsnActions(){
 if(context.get("psnActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("psnActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getPsnPanel());  context.put("psnActions",bean);
  bean.setActions(getManagedList3());
  bean.setModel(getPsnDailyDataModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getFilterAction());  list.add(getSeparatorAction());  list.add(getGenerateAction());  list.add(getClearDataAction());  list.add(getSeparatorAction());  list.add(getUnGenAction());  list.add(getSeparatorAction());  list.add(getViewCardInfoAction());  list.add(getSignCardAction());  list.add(getSeparatorAction());  list.add(getExportToFileAction());  list.add(getPrintActiongroup());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getDateActions(){
 if(context.get("dateActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("dateActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getDateDailyDataPanel());  context.put("dateActions",bean);
  bean.setActions(getManagedList4());
  bean.setModel(getDateDailyDataModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList4(){  List list = new ArrayList();  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getFilterAction());  list.add(getSeparatorAction());  list.add(getGenerateAction());  list.add(getClearDataAction());  list.add(getSeparatorAction());  list.add(getUnGenAction());  list.add(getSeparatorAction());  list.add(getViewCardInfoAction());  list.add(getSeparatorAction());  list.add(getExportToFileAction());  list.add(getPrintActiongroup());  return list;}

public nc.ui.ta.dataprocess.action.GenerateAction getGenerateAction(){
 if(context.get("GenerateAction")!=null)
 return (nc.ui.ta.dataprocess.action.GenerateAction)context.get("GenerateAction");
  nc.ui.ta.dataprocess.action.GenerateAction bean = new nc.ui.ta.dataprocess.action.GenerateAction();
  context.put("GenerateAction",bean);
  bean.setModel(getPsnModel());
  bean.setPsnDataManager(getModelDataManager());
  bean.setDateManager(getDateModelDataManager());
  bean.setDateViewDailyDataManager(getDateDailyDataModelDataManager());
  bean.setEnabled(false);
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.action.UnGenrateDataAction getUnGenAction(){
 if(context.get("UnGenAction")!=null)
 return (nc.ui.ta.dataprocess.action.UnGenrateDataAction)context.get("UnGenAction");
  nc.ui.ta.dataprocess.action.UnGenrateDataAction bean = new nc.ui.ta.dataprocess.action.UnGenrateDataAction();
  context.put("UnGenAction",bean);
  bean.setModel(getPsnModel());
  bean.setEnabled(false);
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.action.ClearDataAction getClearDataAction(){
 if(context.get("ClearDataAction")!=null)
 return (nc.ui.ta.dataprocess.action.ClearDataAction)context.get("ClearDataAction");
  nc.ui.ta.dataprocess.action.ClearDataAction bean = new nc.ui.ta.dataprocess.action.ClearDataAction();
  context.put("ClearDataAction",bean);
  bean.setModel(getPsnModel());
  bean.setEnabled(false);
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.action.ViewCardInfoAction getViewCardInfoAction(){
 if(context.get("ViewCardInfoAction")!=null)
 return (nc.ui.ta.dataprocess.action.ViewCardInfoAction)context.get("ViewCardInfoAction");
  nc.ui.ta.dataprocess.action.ViewCardInfoAction bean = new nc.ui.ta.dataprocess.action.ViewCardInfoAction();
  context.put("ViewCardInfoAction",bean);
  bean.setModelforpsnview(getPsnDailyDataModel());
  bean.setModelfordateview(getDateDailyDataModel());
  bean.setPsnDailyDataPanel(getPsnDailyDataPanel());
  bean.setDateDailyDataPanel(getDateDailyDataPanel());
  bean.setEnabled(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.action.ExportToFileAction getExportToFileAction(){
 if(context.get("ExportToFileAction")!=null)
 return (nc.ui.ta.dataprocess.action.ExportToFileAction)context.get("ExportToFileAction");
  nc.ui.ta.dataprocess.action.ExportToFileAction bean = new nc.ui.ta.dataprocess.action.ExportToFileAction();
  context.put("ExportToFileAction",bean);
  bean.setModelforpsnview(getPsnDailyDataModel());
  bean.setModelfordateview(getDateDailyDataModel());
  bean.setPsnDailyDataPanel(getPsnDailyDataPanel());
  bean.setDateDailyDataPanel(getDateDailyDataPanel());
  bean.setPsnPanel(getPsnPanel());
  bean.setEnabled(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintActiongroup(){
 if(context.get("PrintActiongroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PrintActiongroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PrintActiongroup",bean);
  bean.setCode("printgroup");
  bean.setName(getI18nFB_1c8d38d());
  bean.setActions(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1c8d38d(){
 if(context.get("nc.ui.uif2.I18nFB#1c8d38d")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1c8d38d");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1c8d38d",bean);  bean.setResDir("common");
  bean.setDefaultValue("打印");
  bean.setResId("UC001-0000007");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1c8d38d",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList5(){  List list = new ArrayList();  list.add(getPrintDirectAction());  list.add(getPrintPreviewAction());  list.add(getExportListAction());  return list;}

public nc.ui.ta.dataprocess.action.DataPrintPreviewAction getPrintPreviewAction(){
 if(context.get("printPreviewAction")!=null)
 return (nc.ui.ta.dataprocess.action.DataPrintPreviewAction)context.get("printPreviewAction");
  nc.ui.ta.dataprocess.action.DataPrintPreviewAction bean = new nc.ui.ta.dataprocess.action.DataPrintPreviewAction();
  context.put("printPreviewAction",bean);
  bean.setModelforpsnview(getPsnDailyDataModel());
  bean.setModelfordateview(getDateDailyDataModel());
  bean.setPsnDailyDataPanel(getPsnDailyDataPanel());
  bean.setDateDailyDataPanel(getDateDailyDataPanel());
  bean.setPsnPanel(getPsnPanel());
  bean.setEnabled(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.action.DataPrintDirectAction getPrintDirectAction(){
 if(context.get("printDirectAction")!=null)
 return (nc.ui.ta.dataprocess.action.DataPrintDirectAction)context.get("printDirectAction");
  nc.ui.ta.dataprocess.action.DataPrintDirectAction bean = new nc.ui.ta.dataprocess.action.DataPrintDirectAction();
  context.put("printDirectAction",bean);
  bean.setModelforpsnview(getPsnDailyDataModel());
  bean.setModelfordateview(getDateDailyDataModel());
  bean.setPsnDailyDataPanel(getPsnDailyDataPanel());
  bean.setDateDailyDataPanel(getDateDailyDataPanel());
  bean.setPsnPanel(getPsnPanel());
  bean.setEnabled(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.action.ExportDataPrintAction getExportListAction(){
 if(context.get("exportListAction")!=null)
 return (nc.ui.ta.dataprocess.action.ExportDataPrintAction)context.get("exportListAction");
  nc.ui.ta.dataprocess.action.ExportDataPrintAction bean = new nc.ui.ta.dataprocess.action.ExportDataPrintAction();
  context.put("exportListAction",bean);
  bean.setModelforpsnview(getPsnDailyDataModel());
  bean.setModelfordateview(getDateDailyDataModel());
  bean.setPsnDailyDataPanel(getPsnDailyDataPanel());
  bean.setDateDailyDataPanel(getDateDailyDataPanel());
  bean.setPsnPanel(getPsnPanel());
  bean.setEnabled(false);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.dataprocess.action.FillSignCardAction getSignCardAction(){
 if(context.get("signCardAction")!=null)
 return (nc.ui.ta.dataprocess.action.FillSignCardAction)context.get("signCardAction");
  nc.ui.ta.dataprocess.action.FillSignCardAction bean = new nc.ui.ta.dataprocess.action.FillSignCardAction();
  context.put("signCardAction",bean);
  bean.setPsnModel(getPsnModel());
  bean.setDateModel(getPsnDailyDataModel());
  bean.setPanel(getPsnDailyDataPanel());
  bean.setSignCardEditor(getSignCardcardForm());
  bean.setSaveSignCardAction(getSaveSignCardAction());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.signcard.register.action.SaveSignCardRegAction getSaveSignCardAction(){
 if(context.get("saveSignCardAction")!=null)
 return (nc.ui.ta.signcard.register.action.SaveSignCardRegAction)context.get("saveSignCardAction");
  nc.ui.ta.signcard.register.action.SaveSignCardRegAction bean = new nc.ui.ta.signcard.register.action.SaveSignCardRegAction();
  context.put("saveSignCardAction",bean);
  bean.setEditor(getSignCardcardForm());
  bean.setModel(getPsnModel());
  bean.setValidator(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getBillNotNullValidator());  list.add(getSaveValidator());  return list;}

public nc.ui.ta.signcard.register.view.SignCardRegCardView getSignCardcardForm(){
 if(context.get("signCardcardForm")!=null)
 return (nc.ui.ta.signcard.register.view.SignCardRegCardView)context.get("signCardcardForm");
  nc.ui.ta.signcard.register.view.SignCardRegCardView bean = new nc.ui.ta.signcard.register.view.SignCardRegCardView();
  context.put("signCardcardForm",bean);
  bean.setModel(getPsnModel());
  bean.setNodekey("signreg");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setTabActions(getManagedList7());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList7(){  List list = new ArrayList();  list.add(getAddLineAction());  list.add(getDelLineAction());  list.add(getInsertLineAction());  list.add(getCopyLineAction());  list.add(getPasteLineAction());  return list;}

public nc.ui.uif2.editor.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.uif2.editor.TemplateContainer)context.get("templateContainer");
  nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList8());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add("signreg");  list.add("psnquery");  return list;}

public nc.ui.hr.uif2.validator.BillFormNotNullValidator getBillNotNullValidator(){
 if(context.get("billNotNullValidator")!=null)
 return (nc.ui.hr.uif2.validator.BillFormNotNullValidator)context.get("billNotNullValidator");
  nc.ui.hr.uif2.validator.BillFormNotNullValidator bean = new nc.ui.hr.uif2.validator.BillFormNotNullValidator();
  context.put("billNotNullValidator",bean);
  bean.setBillForm(getSignCardcardForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.ta.signcard.register.validator.SaveSignCardRegValidator getSaveValidator(){
 if(context.get("saveValidator")!=null)
 return (nc.vo.ta.signcard.register.validator.SaveSignCardRegValidator)context.get("saveValidator");
  nc.vo.ta.signcard.register.validator.SaveSignCardRegValidator bean = new nc.vo.ta.signcard.register.validator.SaveSignCardRegValidator();
  context.put("saveValidator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.signcard.register.action.SignCardRegAddLineAction getAddLineAction(){
 if(context.get("AddLineAction")!=null)
 return (nc.ui.ta.signcard.register.action.SignCardRegAddLineAction)context.get("AddLineAction");
  nc.ui.ta.signcard.register.action.SignCardRegAddLineAction bean = new nc.ui.ta.signcard.register.action.SignCardRegAddLineAction();
  context.put("AddLineAction",bean);
  bean.setModel(getPsnModel());
  bean.setCardPanel(getSignCardcardForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.signcard.register.action.SignCardRegDelLineAction getDelLineAction(){
 if(context.get("DelLineAction")!=null)
 return (nc.ui.ta.signcard.register.action.SignCardRegDelLineAction)context.get("DelLineAction");
  nc.ui.ta.signcard.register.action.SignCardRegDelLineAction bean = new nc.ui.ta.signcard.register.action.SignCardRegDelLineAction();
  context.put("DelLineAction",bean);
  bean.setModel(getPsnModel());
  bean.setCardPanel(getSignCardcardForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.signcard.register.action.SignCardRegInsertLineAction getInsertLineAction(){
 if(context.get("InsertLineAction")!=null)
 return (nc.ui.ta.signcard.register.action.SignCardRegInsertLineAction)context.get("InsertLineAction");
  nc.ui.ta.signcard.register.action.SignCardRegInsertLineAction bean = new nc.ui.ta.signcard.register.action.SignCardRegInsertLineAction();
  context.put("InsertLineAction",bean);
  bean.setModel(getPsnModel());
  bean.setCardPanel(getSignCardcardForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.signcard.register.action.SignCardRegCopyLineAction getCopyLineAction(){
 if(context.get("CopyLineAction")!=null)
 return (nc.ui.ta.signcard.register.action.SignCardRegCopyLineAction)context.get("CopyLineAction");
  nc.ui.ta.signcard.register.action.SignCardRegCopyLineAction bean = new nc.ui.ta.signcard.register.action.SignCardRegCopyLineAction();
  context.put("CopyLineAction",bean);
  bean.setModel(getPsnModel());
  bean.setCardPanel(getSignCardcardForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.signcard.register.action.SignCardRegPasteLineAction getPasteLineAction(){
 if(context.get("PasteLineAction")!=null)
 return (nc.ui.ta.signcard.register.action.SignCardRegPasteLineAction)context.get("PasteLineAction");
  nc.ui.ta.signcard.register.action.SignCardRegPasteLineAction bean = new nc.ui.ta.signcard.register.action.SignCardRegPasteLineAction();
  context.put("PasteLineAction",bean);
  bean.setModel(getPsnModel());
  bean.setCardPanel(getSignCardcardForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
