package nc.ui.ta.teamcalendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class teamcalendar_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.ta.pub.TALoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.ta.pub.TALoginContext)context.get("context");
  nc.vo.ta.pub.TALoginContext bean = new nc.vo.ta.pub.TALoginContext();
  context.put("context",bean);
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

public nc.ui.ta.teamcalendar.model.TeamCalendarAppModel getTeamAppModel(){
 if(context.get("TeamAppModel")!=null)
 return (nc.ui.ta.teamcalendar.model.TeamCalendarAppModel)context.get("TeamAppModel");
  nc.ui.ta.teamcalendar.model.TeamCalendarAppModel bean = new nc.ui.ta.teamcalendar.model.TeamCalendarAppModel();
  context.put("TeamAppModel",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.model.TeamCalendarModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.ta.teamcalendar.model.TeamCalendarModelDataManager)context.get("modelDataManager");
  nc.ui.ta.teamcalendar.model.TeamCalendarModelDataManager bean = new nc.ui.ta.teamcalendar.model.TeamCalendarModelDataManager();
  context.put("modelDataManager",bean);
  bean.setModel(getTeamAppModel());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.view.TeamPsnPanel getGridView(){
 if(context.get("GridView")!=null)
 return (nc.ui.ta.teamcalendar.view.TeamPsnPanel)context.get("GridView");
  nc.ui.ta.teamcalendar.view.TeamPsnPanel bean = new nc.ui.ta.teamcalendar.view.TeamPsnPanel();
  context.put("GridView",bean);
  bean.setModel(getTeamAppModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.view.TeamCalendarPanel getCalendarView(){
 if(context.get("CalendarView")!=null)
 return (nc.ui.ta.teamcalendar.view.TeamCalendarPanel)context.get("CalendarView");
  nc.ui.ta.teamcalendar.view.TeamCalendarPanel bean = new nc.ui.ta.teamcalendar.view.TeamCalendarPanel();
  context.put("CalendarView",bean);
  bean.setModel(getTeamAppModel());
  bean.initUI();
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

private List getManagedList0(){  List list = new ArrayList();  list.add(getCalendarActions());  list.add(getGridActions());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCalendarActions(){
 if(context.get("CalendarActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("CalendarActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getCalendarView());  context.put("CalendarActions",bean);
  bean.setActions(getManagedList1());
  bean.setEditActions(getManagedList2());
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getEditAction());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getSeparatorAction());  list.add(getShowDetailAction());  return list;}

private List getManagedList2(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getSeparatorAction());  list.add(getCancelAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getGridActions(){
 if(context.get("GridActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("GridActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getGridView());  context.put("GridActions",bean);
  bean.setActions(getManagedList3());
  bean.setEditActions(getManagedList4());
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getEditActionGroup());  list.add(getSeparatorAction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getSeparatorAction());  list.add(getCircularlyArrangeAction());  list.add(getSyncAction());  list.add(getShowDetailAction());  list.add(getSeparatorAction());  list.add(getExportAction());  list.add(getPrintActiongroup());  return list;}

private List getManagedList4(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getSeparatorAction());  list.add(getCancelAction());  return list;}

public nc.funcnode.ui.action.GroupAction getEditActionGroup(){
 if(context.get("EditActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("EditActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("EditActionGroup",bean);
  bean.setCode("edit");
  bean.setName(getI18nFB_5e9e4309());
  bean.setActions(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_5e9e4309(){
 if(context.get("nc.ui.uif2.I18nFB#5e9e4309")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#5e9e4309");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#5e9e4309",bean);  bean.setResDir("common");
  bean.setDefaultValue("修改");
  bean.setResId("UC001-0000045");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#5e9e4309",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList5(){  List list = new ArrayList();  list.add(getEditAction());  list.add(getBatchEditAction());  list.add(getBatchChangeCalendarDayTypeAction());  return list;}

public nc.ui.ta.teamcalendar.action.EditTeamCalendarAction getEditAction(){
 if(context.get("EditAction")!=null)
 return (nc.ui.ta.teamcalendar.action.EditTeamCalendarAction)context.get("EditAction");
  nc.ui.ta.teamcalendar.action.EditTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.EditTeamCalendarAction();
  context.put("EditAction",bean);
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarAction getBatchEditAction(){
 if(context.get("BatchEditAction")!=null)
 return (nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarAction)context.get("BatchEditAction");
  nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarAction();
  context.put("BatchEditAction",bean);
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarDayTypeAction getBatchChangeCalendarDayTypeAction(){
 if(context.get("BatchChangeCalendarDayTypeAction")!=null)
 return (nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarDayTypeAction)context.get("BatchChangeCalendarDayTypeAction");
  nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarDayTypeAction bean = new nc.ui.ta.teamcalendar.action.BatchEditTeamCalendarDayTypeAction();
  context.put("BatchChangeCalendarDayTypeAction",bean);
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.RefreshTeamCalendarAction getRefreshAction(){
 if(context.get("RefreshAction")!=null)
 return (nc.ui.ta.teamcalendar.action.RefreshTeamCalendarAction)context.get("RefreshAction");
  nc.ui.ta.teamcalendar.action.RefreshTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.RefreshTeamCalendarAction();
  context.put("RefreshAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.view.TeamCalendarEditor getEditor(){
 if(context.get("editor")!=null)
 return (nc.ui.ta.teamcalendar.view.TeamCalendarEditor)context.get("editor");
  nc.ui.ta.teamcalendar.view.TeamCalendarEditor bean = new nc.ui.ta.teamcalendar.view.TeamCalendarEditor();
  context.put("editor",bean);
  bean.setPsnPanel(getGridView());
  bean.setCalendarPanel(getCalendarView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.SaveTeamCalendarAction getSaveAction(){
 if(context.get("SaveAction")!=null)
 return (nc.ui.ta.teamcalendar.action.SaveTeamCalendarAction)context.get("SaveAction");
  nc.ui.ta.teamcalendar.action.SaveTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.SaveTeamCalendarAction();
  context.put("SaveAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setEditor(getEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.pub.action.CancelAction getCancelAction(){
 if(context.get("CancelAction")!=null)
 return (nc.ui.ta.pub.action.CancelAction)context.get("CancelAction");
  nc.ui.ta.pub.action.CancelAction bean = new nc.ui.ta.pub.action.CancelAction();
  context.put("CancelAction",bean);
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.ShowTeamDetailAction getShowDetailAction(){
 if(context.get("ShowDetailAction")!=null)
 return (nc.ui.ta.teamcalendar.action.ShowTeamDetailAction)context.get("ShowDetailAction");
  nc.ui.ta.teamcalendar.action.ShowTeamDetailAction bean = new nc.ui.ta.teamcalendar.action.ShowTeamDetailAction();
  context.put("ShowDetailAction",bean);
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.CircularlyArrangeTeamAction getCircularlyArrangeAction(){
 if(context.get("CircularlyArrangeAction")!=null)
 return (nc.ui.ta.teamcalendar.action.CircularlyArrangeTeamAction)context.get("CircularlyArrangeAction");
  nc.ui.ta.teamcalendar.action.CircularlyArrangeTeamAction bean = new nc.ui.ta.teamcalendar.action.CircularlyArrangeTeamAction();
  context.put("CircularlyArrangeAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.ExportTeamCalendarAction getExportAction(){
 if(context.get("ExportAction")!=null)
 return (nc.ui.ta.teamcalendar.action.ExportTeamCalendarAction)context.get("ExportAction");
  nc.ui.ta.teamcalendar.action.ExportTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.ExportTeamCalendarAction();
  context.put("ExportAction",bean);
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.SyncTeamCalendarAction getSyncAction(){
 if(context.get("SyncAction")!=null)
 return (nc.ui.ta.teamcalendar.action.SyncTeamCalendarAction)context.get("SyncAction");
  nc.ui.ta.teamcalendar.action.SyncTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.SyncTeamCalendarAction();
  context.put("SyncAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setPanel(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.QueryTeamCalendarAction getQueryAction(){
 if(context.get("QueryAction")!=null)
 return (nc.ui.ta.teamcalendar.action.QueryTeamCalendarAction)context.get("QueryAction");
  nc.ui.ta.teamcalendar.action.QueryTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.QueryTeamCalendarAction();
  context.put("QueryAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setQueryDelegator(getTeamCalendarQueryDelegator_68433466());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ta.teamcalendar.action.TeamCalendarQueryDelegator getTeamCalendarQueryDelegator_68433466(){
 if(context.get("nc.ui.ta.teamcalendar.action.TeamCalendarQueryDelegator#68433466")!=null)
 return (nc.ui.ta.teamcalendar.action.TeamCalendarQueryDelegator)context.get("nc.ui.ta.teamcalendar.action.TeamCalendarQueryDelegator#68433466");
  nc.ui.ta.teamcalendar.action.TeamCalendarQueryDelegator bean = new nc.ui.ta.teamcalendar.action.TeamCalendarQueryDelegator();
  context.put("nc.ui.ta.teamcalendar.action.TeamCalendarQueryDelegator#68433466",bean);
  bean.setNodeKey("teamcalendar");
  bean.setContext(getContext());
  bean.setModel(getTeamAppModel());
  bean.getQueryDlg();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintActiongroup(){
 if(context.get("PrintActiongroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PrintActiongroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PrintActiongroup",bean);
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList6(){  List list = new ArrayList();  list.add(getPrintDirectAction());  list.add(getPrintPreviewAction());  list.add(getExportListAction());  return list;}

public nc.ui.ta.teamcalendar.action.PreviewTeamCalendarAction getPrintPreviewAction(){
 if(context.get("printPreviewAction")!=null)
 return (nc.ui.ta.teamcalendar.action.PreviewTeamCalendarAction)context.get("printPreviewAction");
  nc.ui.ta.teamcalendar.action.PreviewTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.PreviewTeamCalendarAction();
  context.put("printPreviewAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setPanel(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.PrintTeamCalendarAction getPrintDirectAction(){
 if(context.get("printDirectAction")!=null)
 return (nc.ui.ta.teamcalendar.action.PrintTeamCalendarAction)context.get("printDirectAction");
  nc.ui.ta.teamcalendar.action.PrintTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.PrintTeamCalendarAction();
  context.put("printDirectAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setPanel(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.action.OutputTeamCalendarAction getExportListAction(){
 if(context.get("exportListAction")!=null)
 return (nc.ui.ta.teamcalendar.action.OutputTeamCalendarAction)context.get("exportListAction");
  nc.ui.ta.teamcalendar.action.OutputTeamCalendarAction bean = new nc.ui.ta.teamcalendar.action.OutputTeamCalendarAction();
  context.put("exportListAction",bean);
  bean.setModel(getTeamAppModel());
  bean.setPanel(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getSeparatorAction(){
 if(context.get("SeparatorAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("SeparatorAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("SeparatorAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getTeamAppModel());
  bean.setSaveaction(getSaveAction());
  bean.setCancelaction(getCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.teamcalendar.view.TeamCalendarShiftDetailHandler getShowDetailPreProcessor(){
 if(context.get("ShowDetailPreProcessor")!=null)
 return (nc.ui.ta.teamcalendar.view.TeamCalendarShiftDetailHandler)context.get("ShowDetailPreProcessor");
  nc.ui.ta.teamcalendar.view.TeamCalendarShiftDetailHandler bean = new nc.ui.ta.teamcalendar.view.TeamCalendarShiftDetailHandler();
  context.put("ShowDetailPreProcessor",bean);
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_2eaa777f());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_2eaa777f(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#2eaa777f")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#2eaa777f");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#2eaa777f",bean);
  bean.setUp(getCNode_14254a04());
  bean.setDown(getTBNode_28581482());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_14254a04(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#14254a04")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#14254a04");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#14254a04",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_28581482(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#28581482")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#28581482");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#28581482",bean);
  bean.setTabs(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList7(){  List list = new ArrayList();  list.add(getCNode_bddf6a3());  list.add(getCNode_53a7b8d6());  return list;}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_bddf6a3(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#bddf6a3")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#bddf6a3");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#bddf6a3",bean);
  bean.setName(getI18nFB_1c1e56f1());
  bean.setComponent(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1c1e56f1(){
 if(context.get("nc.ui.uif2.I18nFB#1c1e56f1")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1c1e56f1");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1c1e56f1",bean);  bean.setResDir("6017psncalendar");
  bean.setDefaultValue("时间段");
  bean.setResId("X6017psncal03");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1c1e56f1",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_53a7b8d6(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#53a7b8d6")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#53a7b8d6");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#53a7b8d6",bean);
  bean.setName(getI18nFB_196b4d6b());
  bean.setComponent(getCalendarView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_196b4d6b(){
 if(context.get("nc.ui.uif2.I18nFB#196b4d6b")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#196b4d6b");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#196b4d6b",bean);  bean.setResDir("6017basedoc");
  bean.setDefaultValue("日历");
  bean.setResId("06017basedoc1768");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#196b4d6b",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.ta.pub.view.TAParamOrgPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.ta.pub.view.TAParamOrgPanel)context.get("orgpanel");
  nc.ui.ta.pub.view.TAParamOrgPanel bean = new nc.ui.ta.pub.view.TAParamOrgPanel();
  context.put("orgpanel",bean);
  bean.setModel(getTeamAppModel());
  bean.setDataManager(getModelDataManager());
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
  bean.setModel(getTeamAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
