package nc.ui.wa.paydata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Paydata_Config extends AbstractJavaBeanDefinition{
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

public nc.ui.wa.paydata.model.PaydataModelService getManageModelService(){
 if(context.get("ManageModelService")!=null)
 return (nc.ui.wa.paydata.model.PaydataModelService)context.get("ManageModelService");
  nc.ui.wa.paydata.model.PaydataModelService bean = new nc.ui.wa.paydata.model.PaydataModelService();
  context.put("ManageModelService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.model.PaydataIBDObjectAdapterFactory getBoadatorfactory(){
 if(context.get("boadatorfactory")!=null)
 return (nc.ui.wa.paydata.model.PaydataIBDObjectAdapterFactory)context.get("boadatorfactory");
  nc.ui.wa.paydata.model.PaydataIBDObjectAdapterFactory bean = new nc.ui.wa.paydata.model.PaydataIBDObjectAdapterFactory();
  context.put("boadatorfactory",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.model.PaydataAppDataModel getManageAppModel(){
 if(context.get("ManageAppModel")!=null)
 return (nc.ui.wa.paydata.model.PaydataAppDataModel)context.get("ManageAppModel");
  nc.ui.wa.paydata.model.PaydataAppDataModel bean = new nc.ui.wa.paydata.model.PaydataAppDataModel();
  context.put("ManageAppModel",bean);
  bean.setService(getManageModelService());
  bean.setOrderCondition(" org_dept_v.code , hi_psnjob.clerkcode");
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.model.PaydataTemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.wa.paydata.model.PaydataTemplateContainer)context.get("templateContainer");
  nc.ui.wa.paydata.model.PaydataTemplateContainer bean = new nc.ui.wa.paydata.model.PaydataTemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setPaydataModel(getManageAppModel());
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

public nc.ui.wa.paydata.model.PaydataModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.wa.paydata.model.PaydataModelDataManager)context.get("modelDataManager");
  nc.ui.wa.paydata.model.PaydataModelDataManager bean = new nc.ui.wa.paydata.model.PaydataModelDataManager();
  context.put("modelDataManager",bean);
  bean.setService(getManageModelService());
  bean.setBilltype("6302");
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
  bean.setPaginationModel(getPaginationModel());
  bean.setPaginationBar(getPaginationBar());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

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

private List getManagedList1(){  List list = new ArrayList();  list.add(getEditAction());  list.add(getNullaction());  list.add(getRefreshAction());  return list;}

private List getManagedList2(){  List list = new ArrayList();  list.add(getFormSaveAction());  list.add(getFormSaveEditAction());  list.add(getNullaction());  list.add(getCancelAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListViewActions(){
 if(context.get("listViewActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("listViewActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getMainListPanel());  context.put("listViewActions",bean);
  bean.setActions(getManagedList3());
  bean.setEditActions(getManagedList4());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getEditAction());  list.add(getReplaceAction());  list.add(getSpecialPsnAction());  list.add(getNullaction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getNullaction());  list.add(getCaculateAction());  list.add(getReTotalAction());  list.add(getWaTimesCollectAction());  list.add(getShowDetailAction());  list.add(getCheckGroupAction());  list.add(getPayGroupAction());  list.add(getNullaction());  list.add(getAssistFunctionAction());  list.add(getNullaction());  list.add(getSortAction());  list.add(getDisplayAction());  list.add(getNullaction());  list.add(getExportXlsAction());  list.add(getPrintGroupAction());  return list;}

private List getManagedList4(){  List list = new ArrayList();  list.add(getListSaveAction());  list.add(getNullaction());  list.add(getCancelAction());  return list;}

public nc.funcnode.ui.action.SeparatorAction getNullaction(){
 if(context.get("nullaction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("nullaction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("nullaction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.EditPaydataAction getEditAction(){
 if(context.get("EditAction")!=null)
 return (nc.ui.wa.paydata.action.EditPaydataAction)context.get("EditAction");
  nc.ui.wa.paydata.action.EditPaydataAction bean = new nc.ui.wa.paydata.action.EditPaydataAction();
  context.put("EditAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.QueryPaydataAction getQueryAction(){
 if(context.get("QueryAction")!=null)
 return (nc.ui.wa.paydata.action.QueryPaydataAction)context.get("QueryAction");
  nc.ui.wa.paydata.action.QueryPaydataAction bean = new nc.ui.wa.paydata.action.QueryPaydataAction();
  context.put("QueryAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOrgpanel(getOrgpanel());
  bean.setDataManager(getModelDataManager());
  bean.setQueryDelegator(getWaPaydataQueryDelegator_41d71f1f());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.wa.paydata.view.WaPaydataQueryDelegator getWaPaydataQueryDelegator_41d71f1f(){
 if(context.get("nc.ui.wa.paydata.view.WaPaydataQueryDelegator#41d71f1f")!=null)
 return (nc.ui.wa.paydata.view.WaPaydataQueryDelegator)context.get("nc.ui.wa.paydata.view.WaPaydataQueryDelegator#41d71f1f");
  nc.ui.wa.paydata.view.WaPaydataQueryDelegator bean = new nc.ui.wa.paydata.view.WaPaydataQueryDelegator();
  context.put("nc.ui.wa.paydata.view.WaPaydataQueryDelegator#41d71f1f",bean);
  bean.setNodeKey("");
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.BillFormSavePaydataAction getFormSaveAction(){
 if(context.get("FormSaveAction")!=null)
 return (nc.ui.wa.paydata.action.BillFormSavePaydataAction)context.get("FormSaveAction");
  nc.ui.wa.paydata.action.BillFormSavePaydataAction bean = new nc.ui.wa.paydata.action.BillFormSavePaydataAction();
  context.put("FormSaveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setEditor(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.BillFormSaveEditPaydataAction getFormSaveEditAction(){
 if(context.get("FormSaveEditAction")!=null)
 return (nc.ui.wa.paydata.action.BillFormSaveEditPaydataAction)context.get("FormSaveEditAction");
  nc.ui.wa.paydata.action.BillFormSaveEditPaydataAction bean = new nc.ui.wa.paydata.action.BillFormSaveEditPaydataAction();
  context.put("FormSaveEditAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setEditor(getBillFormEditor());
  bean.setNextLineAction(getNextLineAction());
  bean.setEditAction(getEditAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.BillListSavePaydataAction getListSaveAction(){
 if(context.get("ListSaveAction")!=null)
 return (nc.ui.wa.paydata.action.BillListSavePaydataAction)context.get("ListSaveAction");
  nc.ui.wa.paydata.action.BillListSavePaydataAction bean = new nc.ui.wa.paydata.action.BillListSavePaydataAction();
  context.put("ListSaveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setEditor(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.PaydataCancelAction getCancelAction(){
 if(context.get("CancelAction")!=null)
 return (nc.ui.wa.paydata.action.PaydataCancelAction)context.get("CancelAction");
  nc.ui.wa.paydata.action.PaydataCancelAction bean = new nc.ui.wa.paydata.action.PaydataCancelAction();
  context.put("CancelAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.SortPaydataAction getSortAction(){
 if(context.get("SortAction")!=null)
 return (nc.ui.wa.paydata.action.SortPaydataAction)context.get("SortAction");
  nc.ui.wa.paydata.action.SortPaydataAction bean = new nc.ui.wa.paydata.action.SortPaydataAction();
  context.put("SortAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.ReplaceAction getReplaceAction(){
 if(context.get("ReplaceAction")!=null)
 return (nc.ui.wa.paydata.action.ReplaceAction)context.get("ReplaceAction");
  nc.ui.wa.paydata.action.ReplaceAction bean = new nc.ui.wa.paydata.action.ReplaceAction();
  context.put("ReplaceAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.DisplayAction getDisplayAction(){
 if(context.get("DisplayAction")!=null)
 return (nc.ui.wa.paydata.action.DisplayAction)context.get("DisplayAction");
  nc.ui.wa.paydata.action.DisplayAction bean = new nc.ui.wa.paydata.action.DisplayAction();
  context.put("DisplayAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.ShowDetailAction getShowDetailAction(){
 if(context.get("ShowDetailAction")!=null)
 return (nc.ui.wa.paydata.action.ShowDetailAction)context.get("ShowDetailAction");
  nc.ui.wa.paydata.action.ShowDetailAction bean = new nc.ui.wa.paydata.action.ShowDetailAction();
  context.put("ShowDetailAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setTemplateContainer(getTemplateContainer());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.PaydataRefreshAction getRefreshAction(){
 if(context.get("RefreshAction")!=null)
 return (nc.ui.wa.paydata.action.PaydataRefreshAction)context.get("RefreshAction");
  nc.ui.wa.paydata.action.PaydataRefreshAction bean = new nc.ui.wa.paydata.action.PaydataRefreshAction();
  context.put("RefreshAction",bean);
  bean.setListView(getListView());
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
  bean.setFormEditor(getBillFormEditor());
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

public nc.ui.wa.paydata.action.CaculateAction getCaculateAction(){
 if(context.get("CaculateAction")!=null)
 return (nc.ui.wa.paydata.action.CaculateAction)context.get("CaculateAction");
  nc.ui.wa.paydata.action.CaculateAction bean = new nc.ui.wa.paydata.action.CaculateAction();
  context.put("CaculateAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.CheckAction getCheckAction(){
 if(context.get("CheckAction")!=null)
 return (nc.ui.wa.paydata.action.CheckAction)context.get("CheckAction");
  nc.ui.wa.paydata.action.CheckAction bean = new nc.ui.wa.paydata.action.CheckAction();
  context.put("CheckAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.UnCheckAction getUnCheckAction(){
 if(context.get("UnCheckAction")!=null)
 return (nc.ui.wa.paydata.action.UnCheckAction)context.get("UnCheckAction");
  nc.ui.wa.paydata.action.UnCheckAction bean = new nc.ui.wa.paydata.action.UnCheckAction();
  context.put("UnCheckAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.ReTotalAction getReTotalAction(){
 if(context.get("ReTotalAction")!=null)
 return (nc.ui.wa.paydata.action.ReTotalAction)context.get("ReTotalAction");
  nc.ui.wa.paydata.action.ReTotalAction bean = new nc.ui.wa.paydata.action.ReTotalAction();
  context.put("ReTotalAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.WaTimesCollectAction getWaTimesCollectAction(){
 if(context.get("WaTimesCollectAction")!=null)
 return (nc.ui.wa.paydata.action.WaTimesCollectAction)context.get("WaTimesCollectAction");
  nc.ui.wa.paydata.action.WaTimesCollectAction bean = new nc.ui.wa.paydata.action.WaTimesCollectAction();
  context.put("WaTimesCollectAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.PayAction getPayAction(){
 if(context.get("PayAction")!=null)
 return (nc.ui.wa.paydata.action.PayAction)context.get("PayAction");
  nc.ui.wa.paydata.action.PayAction bean = new nc.ui.wa.paydata.action.PayAction();
  context.put("PayAction",bean);
  bean.setEditor(getPaydataInfoEditor());
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.UnPayAction getUnPayAction(){
 if(context.get("UnPayAction")!=null)
 return (nc.ui.wa.paydata.action.UnPayAction)context.get("UnPayAction");
  nc.ui.wa.paydata.action.UnPayAction bean = new nc.ui.wa.paydata.action.UnPayAction();
  context.put("UnPayAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setOrgpanel(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.OnTimeCacuAction getOnTimeCaculateAction(){
 if(context.get("OnTimeCaculateAction")!=null)
 return (nc.ui.wa.paydata.action.OnTimeCacuAction)context.get("OnTimeCaculateAction");
  nc.ui.wa.paydata.action.OnTimeCacuAction bean = new nc.ui.wa.paydata.action.OnTimeCacuAction();
  context.put("OnTimeCaculateAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.SpecialPsnAction getSpecialPsnAction(){
 if(context.get("SpecialPsnAction")!=null)
 return (nc.ui.wa.paydata.action.SpecialPsnAction)context.get("SpecialPsnAction");
  nc.ui.wa.paydata.action.SpecialPsnAction bean = new nc.ui.wa.paydata.action.SpecialPsnAction();
  context.put("SpecialPsnAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
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

public nc.ui.wa.paydata.action.ExportXlsAction getExportXlsAction(){
 if(context.get("ExportXlsAction")!=null)
 return (nc.ui.wa.paydata.action.ExportXlsAction)context.get("ExportXlsAction");
  nc.ui.wa.paydata.action.ExportXlsAction bean = new nc.ui.wa.paydata.action.ExportXlsAction();
  context.put("ExportXlsAction",bean);
  bean.setModel(getManageAppModel());
  bean.setView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getCheckGroupAction(){
 if(context.get("CheckGroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("CheckGroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("CheckGroupAction",bean);
  bean.setCode("checkgroup");
  bean.setName(getI18nFB_123c4dfe());
  bean.setActions(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_123c4dfe(){
 if(context.get("nc.ui.uif2.I18nFB#123c4dfe")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#123c4dfe");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#123c4dfe",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("审核操作");
  bean.setResId("X60130024");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#123c4dfe",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList5(){  List list = new ArrayList();  list.add(getCheckAction());  list.add(getUnCheckAction());  return list;}

public nc.funcnode.ui.action.GroupAction getPayGroupAction(){
 if(context.get("PayGroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PayGroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PayGroupAction",bean);
  bean.setCode("paygroup");
  bean.setName(getI18nFB_3524680c());
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_3524680c(){
 if(context.get("nc.ui.uif2.I18nFB#3524680c")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#3524680c");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#3524680c",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("发放操作");
  bean.setResId("X60130025");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#3524680c",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList6(){  List list = new ArrayList();  list.add(getPayAction());  list.add(getUnPayAction());  return list;}

public nc.funcnode.ui.action.GroupAction getPrintGroupAction(){
 if(context.get("PrintGroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PrintGroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PrintGroupAction",bean);
  bean.setCode("print");
  bean.setName(getI18nFB_692b0715());
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_692b0715(){
 if(context.get("nc.ui.uif2.I18nFB#692b0715")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#692b0715");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#692b0715",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("打印");
  bean.setResId("X60130002");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#692b0715",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList7(){  List list = new ArrayList();  list.add(getPrintAction());  list.add(getPreviewAction());  list.add(getOutputAction());  list.add(getNullaction());  list.add(getTemplatePrintAction());  list.add(getTemplatePreviewAction());  return list;}

public nc.funcnode.ui.action.MenuAction getAssistFunctionAction(){
 if(context.get("assistFunctionAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("assistFunctionAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("assistFunctionAction",bean);
  bean.setCode("assistFunction");
  bean.setName(getI18nFB_7de29a4());
  bean.setActions(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_7de29a4(){
 if(context.get("nc.ui.uif2.I18nFB#7de29a4")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#7de29a4");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#7de29a4",bean);  bean.setResDir("xmlcode");
  bean.setDefaultValue("关联功能");
  bean.setResId("X60130026");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#7de29a4",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList8(){  List list = new ArrayList();  list.add(getTransferWaRedataAction());  list.add(getTransferPayleaveAction());  list.add(getNullaction());  list.add(getTransferPayApplyAction());  list.add(getNullaction());  list.add(getTransferAmoAction());  list.add(getTransferDatainterfaceAction());  list.add(getTransferWabankAction());  list.add(getNullaction());  list.add(getTransferMonthEndAction());  return list;}

public nc.ui.wa.paydata.action.TransferPayleaveAction getTransferPayleaveAction(){
 if(context.get("transferPayleaveAction")!=null)
 return (nc.ui.wa.paydata.action.TransferPayleaveAction)context.get("transferPayleaveAction");
  nc.ui.wa.paydata.action.TransferPayleaveAction bean = new nc.ui.wa.paydata.action.TransferPayleaveAction();
  context.put("transferPayleaveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOpeningFunCode("60130payleave");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.TransferWaRedataAction getTransferWaRedataAction(){
 if(context.get("transferWaRedataAction")!=null)
 return (nc.ui.wa.paydata.action.TransferWaRedataAction)context.get("transferWaRedataAction");
  nc.ui.wa.paydata.action.TransferWaRedataAction bean = new nc.ui.wa.paydata.action.TransferWaRedataAction();
  context.put("transferWaRedataAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOpeningFunCode("60130repaydata");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.TransferPayApplyAction getTransferPayApplyAction(){
 if(context.get("transferPayApplyAction")!=null)
 return (nc.ui.wa.paydata.action.TransferPayApplyAction)context.get("transferPayApplyAction");
  nc.ui.wa.paydata.action.TransferPayApplyAction bean = new nc.ui.wa.paydata.action.TransferPayApplyAction();
  context.put("transferPayApplyAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOpeningFunCode("60130payslipaly");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.TransferAmoAction getTransferAmoAction(){
 if(context.get("transferAmoAction")!=null)
 return (nc.ui.wa.paydata.action.TransferAmoAction)context.get("transferAmoAction");
  nc.ui.wa.paydata.action.TransferAmoAction bean = new nc.ui.wa.paydata.action.TransferAmoAction();
  context.put("transferAmoAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOpeningFunCode("60130payamo");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.TransferWabankAction getTransferWabankAction(){
 if(context.get("transferWabankAction")!=null)
 return (nc.ui.wa.paydata.action.TransferWabankAction)context.get("transferWabankAction");
  nc.ui.wa.paydata.action.TransferWabankAction bean = new nc.ui.wa.paydata.action.TransferWabankAction();
  context.put("transferWabankAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOpeningFunCode("60130bankitf");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.TransferDatainterfaceAction getTransferDatainterfaceAction(){
 if(context.get("transferDatainterfaceAction")!=null)
 return (nc.ui.wa.paydata.action.TransferDatainterfaceAction)context.get("transferDatainterfaceAction");
  nc.ui.wa.paydata.action.TransferDatainterfaceAction bean = new nc.ui.wa.paydata.action.TransferDatainterfaceAction();
  context.put("transferDatainterfaceAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOpeningFunCode("60130dataitf");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.TransferMonthEndAction getTransferMonthEndAction(){
 if(context.get("transferMonthEndAction")!=null)
 return (nc.ui.wa.paydata.action.TransferMonthEndAction)context.get("transferMonthEndAction");
  nc.ui.wa.paydata.action.TransferMonthEndAction bean = new nc.ui.wa.paydata.action.TransferMonthEndAction();
  context.put("transferMonthEndAction",bean);
  bean.setModel(getManageAppModel());
  bean.setOpeningFunCode("60130monthend");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.DirectPrintAction getPreviewAction(){
 if(context.get("PreviewAction")!=null)
 return (nc.ui.wa.paydata.action.DirectPrintAction)context.get("PreviewAction");
  nc.ui.wa.paydata.action.DirectPrintAction bean = new nc.ui.wa.paydata.action.DirectPrintAction();
  context.put("PreviewAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDirectPrint(false);
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.action.DirectPrintAction getPrintAction(){
 if(context.get("PrintAction")!=null)
 return (nc.ui.wa.paydata.action.DirectPrintAction)context.get("PrintAction");
  nc.ui.wa.paydata.action.DirectPrintAction bean = new nc.ui.wa.paydata.action.DirectPrintAction();
  context.put("PrintAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDirectPrint(true);
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.pub.WaOutputAction getOutputAction(){
 if(context.get("outputAction")!=null)
 return (nc.ui.wa.pub.WaOutputAction)context.get("outputAction");
  nc.ui.wa.pub.WaOutputAction bean = new nc.ui.wa.pub.WaOutputAction();
  context.put("outputAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.model.PaydataDataSource getDatasource(){
 if(context.get("datasource")!=null)
 return (nc.ui.wa.paydata.model.PaydataDataSource)context.get("datasource");
  nc.ui.wa.paydata.model.PaydataDataSource bean = new nc.ui.wa.paydata.model.PaydataDataSource();
  context.put("datasource",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.pub.action.WaTemplatePreviewAction getTemplatePreviewAction(){
 if(context.get("TemplatePreviewAction")!=null)
 return (nc.ui.wa.pub.action.WaTemplatePreviewAction)context.get("TemplatePreviewAction");
  nc.ui.wa.pub.action.WaTemplatePreviewAction bean = new nc.ui.wa.pub.action.WaTemplatePreviewAction();
  context.put("TemplatePreviewAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDatasource(getDatasource());
  bean.setNodeKey("paydata");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.pub.action.WaTemplatePrintAction getTemplatePrintAction(){
 if(context.get("TemplatePrintAction")!=null)
 return (nc.ui.wa.pub.action.WaTemplatePrintAction)context.get("TemplatePrintAction");
  nc.ui.wa.pub.action.WaTemplatePrintAction bean = new nc.ui.wa.pub.action.WaTemplatePrintAction();
  context.put("TemplatePrintAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDatasource(getDatasource());
  bean.setNodeKey("paydata");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.view.PaydataListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.wa.paydata.view.PaydataListView)context.get("listView");
  nc.ui.wa.paydata.view.PaydataListView bean = new nc.ui.wa.paydata.view.PaydataListView();
  context.put("listView",bean);
  bean.setModel(getManageAppModel());
  bean.setMultiSelectionEnable(false);
  bean.setTemplateContainer(getTemplateContainer());
  bean.setBillListPanelValueSetter(getAppendableBillListPanelSetter_a7fd205());
  bean.setDataManager(getModelDataManager());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hr.append.model.AppendableBillListPanelSetter getAppendableBillListPanelSetter_a7fd205(){
 if(context.get("nc.ui.hr.append.model.AppendableBillListPanelSetter#a7fd205")!=null)
 return (nc.ui.hr.append.model.AppendableBillListPanelSetter)context.get("nc.ui.hr.append.model.AppendableBillListPanelSetter#a7fd205");
  nc.ui.hr.append.model.AppendableBillListPanelSetter bean = new nc.ui.hr.append.model.AppendableBillListPanelSetter();
  context.put("nc.ui.hr.append.model.AppendableBillListPanelSetter#a7fd205",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.value.BillCardPanelHeadVOValueAdapter getComponentValueManager(){
 if(context.get("componentValueManager")!=null)
 return (nc.ui.uif2.editor.value.BillCardPanelHeadVOValueAdapter)context.get("componentValueManager");
  nc.ui.uif2.editor.value.BillCardPanelHeadVOValueAdapter bean = new nc.ui.uif2.editor.value.BillCardPanelHeadVOValueAdapter();
  context.put("componentValueManager",bean);
  bean.setHeadVOName("nc.vo.wa.paydata.DataVO");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.view.PaydataFormEditor getBillFormEditor(){
 if(context.get("billFormEditor")!=null)
 return (nc.ui.wa.paydata.view.PaydataFormEditor)context.get("billFormEditor");
  nc.ui.wa.paydata.view.PaydataFormEditor bean = new nc.ui.wa.paydata.view.PaydataFormEditor();
  context.put("billFormEditor",bean);
  bean.setModel(getManageAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setComponentValueManager(getComponentValueManager());
  bean.setShowOnEditState(false);
  bean.setActions(getManagedList9());
  bean.setDataManager(getModelDataManager());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getManageAppModel());
  bean.setSaveaction(getFormSaveAction());
  bean.setCancelaction(getCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator(){
 if(context.get("billNotNullValidator")!=null)
 return (nc.ui.hr.uif2.validator.BillNotNullValidateService)context.get("billNotNullValidator");
  nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(getBillFormEditor());  context.put("billNotNullValidator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction(){
 if(context.get("editorReturnAction")!=null)
 return (nc.ui.uif2.actions.ShowMeUpAction)context.get("editorReturnAction");
  nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
  context.put("editorReturnAction",bean);
  bean.setGoComponent(getMainListPanel());
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
  bean.setActions(getManagedList10());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList10(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getTBNode_7aef6cc5());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_7aef6cc5(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#7aef6cc5")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#7aef6cc5");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#7aef6cc5",bean);
  bean.setShowMode("CardLayout");
  bean.setTabs(getManagedList11());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList11(){  List list = new ArrayList();  list.add(getVSNode_50f650ac());  list.add(getVSNode_71edbc9());  return list;}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_50f650ac(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#50f650ac")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#50f650ac");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#50f650ac",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_4db54ba5());
  bean.setDown(getCNode_3979657e());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_4db54ba5(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#4db54ba5")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#4db54ba5");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#4db54ba5",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_3979657e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#3979657e")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#3979657e");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#3979657e",bean);
  bean.setComponent(getMainListPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_71edbc9(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#71edbc9")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#71edbc9");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#71edbc9",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_5b2a75ba());
  bean.setDown(getCNode_40973518());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_5b2a75ba(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#5b2a75ba")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#5b2a75ba");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#5b2a75ba",bean);
  bean.setComponent(getEditorToolBarPanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_40973518(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#40973518")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#40973518");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#40973518",bean);
  bean.setComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.wizard.LayoutPanel getMainListPanel(){
 if(context.get("mainListPanel")!=null)
 return (nc.ui.hr.wizard.LayoutPanel)context.get("mainListPanel");
  nc.ui.hr.wizard.LayoutPanel bean = new nc.ui.hr.wizard.LayoutPanel(getBorderLayout_7cce3890());  context.put("mainListPanel",bean);
  bean.setComponentMap(getManagedMap0());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.awt.BorderLayout getBorderLayout_7cce3890(){
 if(context.get("java.awt.BorderLayout#7cce3890")!=null)
 return (java.awt.BorderLayout)context.get("java.awt.BorderLayout#7cce3890");
  java.awt.BorderLayout bean = new java.awt.BorderLayout();
  context.put("java.awt.BorderLayout#7cce3890",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap0(){  Map map = new HashMap();  map.put(getListView(),"Center");  map.put(getPaydataInfoEditor(),"South");  return map;}

public nc.ui.wa.pub.WaOrgHeadPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.wa.pub.WaOrgHeadPanel)context.get("orgpanel");
  nc.ui.wa.pub.WaOrgHeadPanel bean = new nc.ui.wa.pub.WaOrgHeadPanel(getMainListPanel());  context.put("orgpanel",bean);
  bean.setModel(getManageAppModel());
  bean.setContext(getContext());
  bean.setDataManager(getModelDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.paydata.view.PaydataInfoEditor getPaydataInfoEditor(){
 if(context.get("paydataInfoEditor")!=null)
 return (nc.ui.wa.paydata.view.PaydataInfoEditor)context.get("paydataInfoEditor");
  nc.ui.wa.paydata.view.PaydataInfoEditor bean = new nc.ui.wa.paydata.view.PaydataInfoEditor();
  context.put("paydataInfoEditor",bean);
  bean.setModel(getManageAppModel());
  bean.setContext(getContext());
  bean.setPaginationBar(getPaginationBar());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
