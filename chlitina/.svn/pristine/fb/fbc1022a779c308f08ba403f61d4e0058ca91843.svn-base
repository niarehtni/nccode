package nc.ui.ta.overtime.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class overtime_register_config extends AbstractJavaBeanDefinition {
    private Map<String, Object> context = new HashMap();

    public java.lang.Integer getFunc_type() {
	if (context.get("func_type") != null)
	    return (java.lang.Integer) context.get("func_type");
	java.lang.Integer bean = new java.lang.Integer("5");
	context.put("func_type", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.vo.ta.pub.TALoginContext getContext() {
	if (context.get("context") != null)
	    return (nc.vo.ta.pub.TALoginContext) context.get("context");
	nc.vo.ta.pub.TALoginContext bean = new nc.vo.ta.pub.TALoginContext();
	context.put("context", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public java.lang.String getNodekey() {
	if (context.get("nodekey") != null)
	    return (java.lang.String) context.get("nodekey");
	java.lang.String bean = new java.lang.String("overtimereg");
	context.put("nodekey", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.model.OvertimeRegModelService getManageModelService() {
	if (context.get("ManageModelService") != null)
	    return (nc.ui.ta.overtime.register.model.OvertimeRegModelService) context.get("ManageModelService");
	nc.ui.ta.overtime.register.model.OvertimeRegModelService bean = new nc.ui.ta.overtime.register.model.OvertimeRegModelService();
	context.put("ManageModelService", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory() {
	if (context.get("boadatorfactory") != null)
	    return (nc.vo.bd.meta.BDObjectAdpaterFactory) context.get("boadatorfactory");
	nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
	context.put("boadatorfactory", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.FunNodeClosingHandler getClosingListener() {
	if (context.get("ClosingListener") != null)
	    return (nc.ui.uif2.FunNodeClosingHandler) context.get("ClosingListener");
	nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
	context.put("ClosingListener", bean);
	bean.setModel(getManageAppModel());
	bean.setCancelaction(getCancelAction());
	bean.setSaveaction(getSaveAction());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.pub.action.EnableJudge getEnableJudge() {
	if (context.get("enableJudge") != null)
	    return (nc.ui.ta.pub.action.EnableJudge) context.get("enableJudge");
	nc.ui.ta.pub.action.EnableJudge bean = new nc.ui.ta.pub.action.EnableJudge();
	context.put("enableJudge", bean);
	bean.setModel(getManageAppModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.model.OvertimeRegAppModel getManageAppModel() {
	if (context.get("ManageAppModel") != null)
	    return (nc.ui.ta.overtime.register.model.OvertimeRegAppModel) context.get("ManageAppModel");
	nc.ui.ta.overtime.register.model.OvertimeRegAppModel bean = new nc.ui.ta.overtime.register.model.OvertimeRegAppModel();
	context.put("ManageAppModel", bean);
	bean.setService(getManageModelService());
	bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
	bean.setContext(getContext());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.model.OvertimeRegModelDataManager getModelDataManager() {
	if (context.get("modelDataManager") != null)
	    return (nc.ui.ta.overtime.register.model.OvertimeRegModelDataManager) context.get("modelDataManager");
	nc.ui.ta.overtime.register.model.OvertimeRegModelDataManager bean = new nc.ui.ta.overtime.register.model.OvertimeRegModelDataManager();
	context.put("modelDataManager", bean);
	bean.setModel(getManageAppModel());
	bean.setService(getManageModelService());
	bean.setContext(getContext());
	bean.setPaginationModel(getPaginationModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.editor.TemplateContainer getTemplateContainer() {
	if (context.get("templateContainer") != null)
	    return (nc.ui.uif2.editor.TemplateContainer) context.get("templateContainer");
	nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
	context.put("templateContainer", bean);
	bean.setContext(getContext());
	bean.setNodeKeies(getManagedList0());
	bean.load();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList0() {
	List list = new ArrayList();
	list.add("overtimereg");
	return list;
    }

    public nc.ui.ta.pub.QueryEditorListener getQueryEditorListener() {
	if (context.get("queryEditorListener") != null)
	    return (nc.ui.ta.pub.QueryEditorListener) context.get("queryEditorListener");
	nc.ui.ta.pub.QueryEditorListener bean = new nc.ui.ta.pub.QueryEditorListener();
	context.put("queryEditorListener", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.view.OvertimeRegListView getListView() {
	if (context.get("listView") != null)
	    return (nc.ui.ta.overtime.register.view.OvertimeRegListView) context.get("listView");
	nc.ui.ta.overtime.register.view.OvertimeRegListView bean = new nc.ui.ta.overtime.register.view.OvertimeRegListView();
	context.put("listView", bean);
	bean.setModel(getManageAppModel());
	bean.setMultiSelectionEnable(true);
	bean.setMultiSelectionMode(1);
	bean.setNodekey(getNodekey());
	bean.setTemplateContainer(getTemplateContainer());
	bean.setDealHyperlink(true);
	bean.setNorth(getListToolBarPanel());
	bean.setPaginationBar(getPaginationBar());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getListToolBarPanel() {
	if (context.get("listToolBarPanel") != null)
	    return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel) context.get("listToolBarPanel");
	nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
	context.put("listToolBarPanel", bean);
	bean.setModel(getManageAppModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.view.OvertimeRegCardView getBillFormEditor() {
	if (context.get("billFormEditor") != null)
	    return (nc.ui.ta.overtime.register.view.OvertimeRegCardView) context.get("billFormEditor");
	nc.ui.ta.overtime.register.view.OvertimeRegCardView bean = new nc.ui.ta.overtime.register.view.OvertimeRegCardView();
	context.put("billFormEditor", bean);
	bean.setModel(getManageAppModel());
	bean.setNodekey(getNodekey());
	bean.setTemplateContainer(getTemplateContainer());
	bean.setActions(getManagedList1());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList1() {
	List list = new ArrayList();
	list.add(getFirstLineAction());
	list.add(getPreLineAction());
	list.add(getNextLineAction());
	list.add(getLastLineAction());
	return list;
    }

    public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getMouseClickShowPanelMediator() {
	if (context.get("mouseClickShowPanelMediator") != null)
	    return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator) context.get("mouseClickShowPanelMediator");
	nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
	context.put("mouseClickShowPanelMediator", bean);
	bean.setModel(getManageAppModel());
	bean.setShowUpComponent(getBillFormEditor());
	bean.setHyperLinkColumn("pk_psnjob");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
	if (context.get("toftpanelActionContributors") != null)
	    return (nc.ui.uif2.actions.ActionContributors) context.get("toftpanelActionContributors");
	nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
	context.put("toftpanelActionContributors", bean);
	bean.setContributors(getManagedList2());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList2() {
	List list = new ArrayList();
	list.add(getListViewActions());
	list.add(getCardEditorActions());
	return list;
    }

    public nc.ui.uif2.TangramContainer getContainer() {
	if (context.get("container") != null)
	    return (nc.ui.uif2.TangramContainer) context.get("container");
	nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
	context.put("container", bean);
	bean.setTangramLayoutRoot(getTBNode_1881477());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1881477() {
	if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#1881477") != null)
	    return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#1881477");
	nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
	context.put("nc.ui.uif2.tangramlayout.node.TBNode#1881477", bean);
	bean.setShowMode("CardLayout");
	bean.setTabs(getManagedList3());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList3() {
	List list = new ArrayList();
	list.add(getVSNode_bf32f1());
	list.add(getVSNode_1cfc4e6());
	return list;
    }

    private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_bf32f1() {
	if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#bf32f1") != null)
	    return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#bf32f1");
	nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
	context.put("nc.ui.uif2.tangramlayout.node.VSNode#bf32f1", bean);
	bean.setShowMode("NoDivider");
	bean.setUp(getCNode_f39954());
	bean.setDown(getHSNode_12f5ad());
	bean.setDividerLocation(30f);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_f39954() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#f39954") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#f39954");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#f39954", bean);
	bean.setComponent(getOrgpanel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_12f5ad() {
	if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#12f5ad") != null)
	    return (nc.ui.uif2.tangramlayout.node.HSNode) context.get("nc.ui.uif2.tangramlayout.node.HSNode#12f5ad");
	nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
	context.put("nc.ui.uif2.tangramlayout.node.HSNode#12f5ad", bean);
	bean.setLeft(getCNode_d775fb());
	bean.setRight(getCNode_182cbd0());
	bean.setDividerLocation(0.2f);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_d775fb() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#d775fb") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#d775fb");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#d775fb", bean);
	bean.setComponent(getQueryAreaShell());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_182cbd0() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#182cbd0") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#182cbd0");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#182cbd0", bean);
	bean.setComponent(getListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1cfc4e6() {
	if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1cfc4e6") != null)
	    return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#1cfc4e6");
	nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
	context.put("nc.ui.uif2.tangramlayout.node.VSNode#1cfc4e6", bean);
	bean.setShowMode("NoDivider");
	bean.setUp(getCNode_a9aa25());
	bean.setDown(getCNode_e18c82());
	bean.setDividerLocation(26f);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_a9aa25() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#a9aa25") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#a9aa25");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#a9aa25", bean);
	bean.setComponent(getEditorToolBarPanel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_e18c82() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#e18c82") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#e18c82");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#e18c82", bean);
	bean.setComponent(getBillFormEditor());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel() {
	if (context.get("editorToolBarPanel") != null)
	    return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel) context.get("editorToolBarPanel");
	nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
	context.put("editorToolBarPanel", bean);
	bean.setModel(getManageAppModel());
	bean.setTitleAction(getEditorReturnAction());
	bean.setActions(getManagedList4());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList4() {
	List list = new ArrayList();
	list.add(getFirstLineAction());
	list.add(getPreLineAction());
	list.add(getNextLineAction());
	list.add(getLastLineAction());
	return list;
    }

    public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction() {
	if (context.get("editorReturnAction") != null)
	    return (nc.ui.uif2.actions.ShowMeUpAction) context.get("editorReturnAction");
	nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
	context.put("editorReturnAction", bean);
	bean.setGoComponent(getListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.actions.QueryAreaShell getQueryAreaShell() {
	if (context.get("queryAreaShell") != null)
	    return (nc.ui.uif2.actions.QueryAreaShell) context.get("queryAreaShell");
	nc.ui.uif2.actions.QueryAreaShell bean = new nc.ui.uif2.actions.QueryAreaShell();
	context.put("queryAreaShell", bean);
	bean.setQueryArea(getQueryAction_created_f79ec0());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.queryarea.QueryArea getQueryAction_created_f79ec0() {
	if (context.get("QueryAction.created#f79ec0") != null)
	    return (nc.ui.queryarea.QueryArea) context.get("QueryAction.created#f79ec0");
	nc.ui.queryarea.QueryArea bean = getQueryAction().createQueryArea();
	context.put("QueryAction.created#f79ec0", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.pub.view.TAParamOrgPanel getOrgpanel() {
	if (context.get("orgpanel") != null)
	    return (nc.ui.ta.pub.view.TAParamOrgPanel) context.get("orgpanel");
	nc.ui.ta.pub.view.TAParamOrgPanel bean = new nc.ui.ta.pub.view.TAParamOrgPanel();
	context.put("orgpanel", bean);
	bean.setModel(getManageAppModel());
	bean.setDataManager(getModelDataManager());
	bean.setPk_orgtype("HRORGTYPE00000000000");
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator() {
	if (context.get("billNotNullValidator") != null)
	    return (nc.ui.hr.uif2.validator.BillNotNullValidateService) context.get("billNotNullValidator");
	nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(
		getBillFormEditor());
	context.put("billNotNullValidator", bean);
	bean.setNextValidateService(getOvertimeRegValidator());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.vo.ta.overtime.register.validator.SaveOvertimeRegValidatorService getOvertimeRegValidator() {
	if (context.get("OvertimeRegValidator") != null)
	    return (nc.vo.ta.overtime.register.validator.SaveOvertimeRegValidatorService) context
		    .get("OvertimeRegValidator");
	nc.vo.ta.overtime.register.validator.SaveOvertimeRegValidatorService bean = new nc.vo.ta.overtime.register.validator.SaveOvertimeRegValidatorService();
	context.put("OvertimeRegValidator", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardEditorActions() {
	if (context.get("cardEditorActions") != null)
	    return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("cardEditorActions");
	nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
		getBillFormEditor());
	context.put("cardEditorActions", bean);
	bean.setActions(getManagedList5());
	bean.setEditActions(getManagedList6());
	bean.setModel(getManageAppModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList5() {
	List list = new ArrayList();
	list.add(getAddActionGroup());
	list.add(getEditAction());
	list.add(getDeleteAction());
	list.add(getRefreshAction());
	list.add(getSeparatorAction());
	list.add(getViewCardInfoAction());
	list.add(getSeparatorAction());
	list.add(getCardPrintActiongroup());
	return list;
    }

    private List getManagedList6() {
	List list = new ArrayList();
	list.add(getSaveAction());
	list.add(getSaveAddAction());
	list.add(getSeparatorAction());
	list.add(getCancelAction());
	return list;
    }

    public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListViewActions() {
	if (context.get("listViewActions") != null)
	    return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("listViewActions");
	nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
		getListView());
	context.put("listViewActions", bean);
	bean.setActions(getManagedList7());
	bean.setModel(getManageAppModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList7() {
	List list = new ArrayList();
	list.add(getAddActionGroup());
	list.add(getEditAction());
	list.add(getDeleteAction());
	list.add(getSeparatorAction());
	list.add(getQueryAction());
	list.add(getRefreshAction());
	list.add(getViewCardInfoAction());
	list.add(getSeparatorAction());
	list.add(getCheckgroup());
	list.add(getRestgroup());
	list.add(getSeparatorAction());
	list.add(getPrintGroupAction());
	return list;
    }

    public nc.ui.uif2.actions.FirstLineAction getFirstLineAction() {
	if (context.get("FirstLineAction") != null)
	    return (nc.ui.uif2.actions.FirstLineAction) context.get("FirstLineAction");
	nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
	context.put("FirstLineAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.actions.PreLineAction getPreLineAction() {
	if (context.get("PreLineAction") != null)
	    return (nc.ui.uif2.actions.PreLineAction) context.get("PreLineAction");
	nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
	context.put("PreLineAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.actions.NextLineAction getNextLineAction() {
	if (context.get("NextLineAction") != null)
	    return (nc.ui.uif2.actions.NextLineAction) context.get("NextLineAction");
	nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
	context.put("NextLineAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.actions.LastLineAction getLastLineAction() {
	if (context.get("LastLineAction") != null)
	    return (nc.ui.uif2.actions.LastLineAction) context.get("LastLineAction");
	nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
	context.put("LastLineAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.GroupAction getAddActionGroup() {
	if (context.get("addActionGroup") != null)
	    return (nc.funcnode.ui.action.GroupAction) context.get("addActionGroup");
	nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
	context.put("addActionGroup", bean);
	bean.setCode("add");
	bean.setName(getI18nFB_d750c5());
	bean.setActions(getManagedList8());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private java.lang.String getI18nFB_d750c5() {
	if (context.get("nc.ui.uif2.I18nFB#d750c5") != null)
	    return (java.lang.String) context.get("nc.ui.uif2.I18nFB#d750c5");
	nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
	context.put("&nc.ui.uif2.I18nFB#d750c5", bean);
	bean.setResDir("common");
	bean.setDefaultValue("新增");
	bean.setResId("UC001-0000108");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	try {
	    Object product = bean.getObject();
	    context.put("nc.ui.uif2.I18nFB#d750c5", product);
	    return (java.lang.String) product;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private List getManagedList8() {
	List list = new ArrayList();
	list.add(getAddAction());
	list.add(getBatchAddAction());
	return list;
    }

    public nc.ui.ta.overtime.register.action.AddOvertimeRegAction getAddAction() {
	if (context.get("AddAction") != null)
	    return (nc.ui.ta.overtime.register.action.AddOvertimeRegAction) context.get("AddAction");
	nc.ui.ta.overtime.register.action.AddOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.AddOvertimeRegAction();
	context.put("AddAction", bean);
	bean.setModel(getManageAppModel());
	bean.setFormEditor(getBillFormEditor());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.wf.batch.BatchAddAction getBatchAddAction() {
	if (context.get("BatchAddAction") != null)
	    return (nc.ui.ta.wf.batch.BatchAddAction) context.get("BatchAddAction");
	nc.ui.ta.wf.batch.BatchAddAction bean = new nc.ui.ta.wf.batch.BatchAddAction();
	context.put("BatchAddAction", bean);
	bean.setModel(getManageAppModel());
	bean.setCardForm(getBillFormEditor());
	bean.setListView(getListView());
	bean.setFunc_type(getFunc_type());
	bean.setFromApp(false);
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.SaveAddOvertimeRegAction getSaveAddAction() {
	if (context.get("SaveAddAction") != null)
	    return (nc.ui.ta.overtime.register.action.SaveAddOvertimeRegAction) context.get("SaveAddAction");
	nc.ui.ta.overtime.register.action.SaveAddOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.SaveAddOvertimeRegAction();
	context.put("SaveAddAction", bean);
	bean.setModel(getManageAppModel());
	bean.setSaveAction(getSaveAction());
	bean.setAddAction(getAddAction());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.QueryOvertimeRegAction getQueryAction() {
	if (context.get("QueryAction") != null)
	    return (nc.ui.ta.overtime.register.action.QueryOvertimeRegAction) context.get("QueryAction");
	nc.ui.ta.overtime.register.action.QueryOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.QueryOvertimeRegAction();
	context.put("QueryAction", bean);
	bean.setModel(getManageAppModel());
	bean.setQueryDelegator(getQueryDelegator());
	bean.setDataManager(getModelDataManager());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.pub.model.TaRegBillQueryDelegator getQueryDelegator() {
	if (context.get("QueryDelegator") != null)
	    return (nc.ui.ta.pub.model.TaRegBillQueryDelegator) context.get("QueryDelegator");
	nc.ui.ta.pub.model.TaRegBillQueryDelegator bean = new nc.ui.ta.pub.model.TaRegBillQueryDelegator();
	context.put("QueryDelegator", bean);
	bean.setContext(getContext());
	bean.setModel(getManageAppModel());
	bean.setQueryEditorListener(getQueryEditorListener());
	bean.setQueryAreaShell(getQueryAreaShell());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.EditOvertimeRegAction getEditAction() {
	if (context.get("EditAction") != null)
	    return (nc.ui.ta.overtime.register.action.EditOvertimeRegAction) context.get("EditAction");
	nc.ui.ta.overtime.register.action.EditOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.EditOvertimeRegAction();
	context.put("EditAction", bean);
	bean.setModel(getManageAppModel());
	bean.setResourceCode("60170otrgst");
	bean.setMdOperateCode("Edit");
	bean.setFormEditor(getBillFormEditor());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.DeleteOvertimeRegAction getDeleteAction() {
	if (context.get("DeleteAction") != null)
	    return (nc.ui.ta.overtime.register.action.DeleteOvertimeRegAction) context.get("DeleteAction");
	nc.ui.ta.overtime.register.action.DeleteOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.DeleteOvertimeRegAction();
	context.put("DeleteAction", bean);
	bean.setModel(getManageAppModel());
	bean.setListView(getListView());
	bean.setCardView(getBillFormEditor());
	bean.setResourceCode("60170otrgst");
	bean.setMdOperateCode("Delete");
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.SaveOvertimeRegAction getSaveAction() {
	if (context.get("SaveAction") != null)
	    return (nc.ui.ta.overtime.register.action.SaveOvertimeRegAction) context.get("SaveAction");
	nc.ui.ta.overtime.register.action.SaveOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.SaveOvertimeRegAction();
	context.put("SaveAction", bean);
	bean.setModel(getManageAppModel());
	bean.setEditor(getBillFormEditor());
	bean.setExceptionHandler(getSaveExceptionHandler());
	bean.setValidationService(getBillNotNullValidator());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.pub.action.SaveBillExceptionHandler getSaveExceptionHandler() {
	if (context.get("SaveExceptionHandler") != null)
	    return (nc.ui.ta.pub.action.SaveBillExceptionHandler) context.get("SaveExceptionHandler");
	nc.ui.ta.pub.action.SaveBillExceptionHandler bean = new nc.ui.ta.pub.action.SaveBillExceptionHandler();
	context.put("SaveExceptionHandler", bean);
	bean.setContext(getContext());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.CancelOvertimeRegAction getCancelAction() {
	if (context.get("CancelAction") != null)
	    return (nc.ui.ta.overtime.register.action.CancelOvertimeRegAction) context.get("CancelAction");
	nc.ui.ta.overtime.register.action.CancelOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.CancelOvertimeRegAction();
	context.put("CancelAction", bean);
	bean.setModel(getManageAppModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.RefreshOvertimeRegAction getRefreshAction() {
	if (context.get("RefreshAction") != null)
	    return (nc.ui.ta.overtime.register.action.RefreshOvertimeRegAction) context.get("RefreshAction");
	nc.ui.ta.overtime.register.action.RefreshOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.RefreshOvertimeRegAction();
	context.put("RefreshAction", bean);
	bean.setModel(getManageAppModel());
	bean.setFormEditor(getBillFormEditor());
	bean.setDataManager(getModelDataManager());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.GroupAction getCheckgroup() {
	if (context.get("Checkgroup") != null)
	    return (nc.funcnode.ui.action.GroupAction) context.get("Checkgroup");
	nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
	context.put("Checkgroup", bean);
	bean.setCode("check");
	bean.setName(getI18nFB_1f7ddf3());
	bean.setActions(getManagedList9());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private java.lang.String getI18nFB_1f7ddf3() {
	if (context.get("nc.ui.uif2.I18nFB#1f7ddf3") != null)
	    return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1f7ddf3");
	nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
	context.put("&nc.ui.uif2.I18nFB#1f7ddf3", bean);
	bean.setResDir("6017basedoc");
	bean.setDefaultValue("校?");
	bean.setResId("06017basedoc1722");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	try {
	    Object product = bean.getObject();
	    context.put("nc.ui.uif2.I18nFB#1f7ddf3", product);
	    return (java.lang.String) product;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private List getManagedList9() {
	List list = new ArrayList();
	list.add(getCheckAction());
	list.add(getUnCheckAction());
	return list;
    }

    public nc.ui.ta.overtime.register.action.CheckOvertimeRegAction getCheckAction() {
	if (context.get("CheckAction") != null)
	    return (nc.ui.ta.overtime.register.action.CheckOvertimeRegAction) context.get("CheckAction");
	nc.ui.ta.overtime.register.action.CheckOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.CheckOvertimeRegAction();
	context.put("CheckAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.UnCheckOvertimeRegAction getUnCheckAction() {
	if (context.get("UnCheckAction") != null)
	    return (nc.ui.ta.overtime.register.action.UnCheckOvertimeRegAction) context.get("UnCheckAction");
	nc.ui.ta.overtime.register.action.UnCheckOvertimeRegAction bean = new nc.ui.ta.overtime.register.action.UnCheckOvertimeRegAction();
	context.put("UnCheckAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.GroupAction getRestgroup() {
	if (context.get("Restgroup") != null)
	    return (nc.funcnode.ui.action.GroupAction) context.get("Restgroup");
	nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
	context.put("Restgroup", bean);
	bean.setCode("rest");
	bean.setName(getI18nFB_c89d6());
	bean.setActions(getManagedList10());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private java.lang.String getI18nFB_c89d6() {
	if (context.get("nc.ui.uif2.I18nFB#c89d6") != null)
	    return (java.lang.String) context.get("nc.ui.uif2.I18nFB#c89d6");
	nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
	context.put("&nc.ui.uif2.I18nFB#c89d6", bean);
	bean.setResDir("6017basedoc");
	bean.setDefaultValue("??休");
	bean.setResId("06017basedoc1730");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	try {
	    Object product = bean.getObject();
	    context.put("nc.ui.uif2.I18nFB#c89d6", product);
	    return (java.lang.String) product;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private List getManagedList10() {
	List list = new ArrayList();
	list.add(getRestAction());
	list.add(getUnRestAction());
	return list;
    }

    public nc.ui.ta.overtime.register.action.RestOvertimeAction getRestAction() {
	if (context.get("RestAction") != null)
	    return (nc.ui.ta.overtime.register.action.RestOvertimeAction) context.get("RestAction");
	nc.ui.ta.overtime.register.action.RestOvertimeAction bean = new nc.ui.ta.overtime.register.action.RestOvertimeAction();
	context.put("RestAction", bean);
	bean.setModel(getManageAppModel());
	bean.setListView(getListView());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.UnRestOvertimeAction getUnRestAction() {
	if (context.get("UnRestAction") != null)
	    return (nc.ui.ta.overtime.register.action.UnRestOvertimeAction) context.get("UnRestAction");
	nc.ui.ta.overtime.register.action.UnRestOvertimeAction bean = new nc.ui.ta.overtime.register.action.UnRestOvertimeAction();
	context.put("UnRestAction", bean);
	bean.setModel(getManageAppModel());
	bean.setListView(getListView());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.action.ViewCardInfoAction getViewCardInfoAction() {
	if (context.get("ViewCardInfoAction") != null)
	    return (nc.ui.ta.overtime.register.action.ViewCardInfoAction) context.get("ViewCardInfoAction");
	nc.ui.ta.overtime.register.action.ViewCardInfoAction bean = new nc.ui.ta.overtime.register.action.ViewCardInfoAction();
	context.put("ViewCardInfoAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.overtime.register.model.OvertimeRegMetaDataDataSource getDatasource() {
	if (context.get("datasource") != null)
	    return (nc.ui.ta.overtime.register.model.OvertimeRegMetaDataDataSource) context.get("datasource");
	nc.ui.ta.overtime.register.model.OvertimeRegMetaDataDataSource bean = new nc.ui.ta.overtime.register.model.OvertimeRegMetaDataDataSource();
	context.put("datasource", bean);
	bean.setModel(getManageAppModel());
	bean.setSingleData(true);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.GroupAction getPrintGroupAction() {
	if (context.get("PrintGroupAction") != null)
	    return (nc.funcnode.ui.action.GroupAction) context.get("PrintGroupAction");
	nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
	context.put("PrintGroupAction", bean);
	bean.setCode("printgroup");
	bean.setName(getI18nFB_12d71dd());
	bean.setActions(getManagedList11());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private java.lang.String getI18nFB_12d71dd() {
	if (context.get("nc.ui.uif2.I18nFB#12d71dd") != null)
	    return (java.lang.String) context.get("nc.ui.uif2.I18nFB#12d71dd");
	nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
	context.put("&nc.ui.uif2.I18nFB#12d71dd", bean);
	bean.setResDir("common");
	bean.setDefaultValue("打印");
	bean.setResId("UC001-0000007");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	try {
	    Object product = bean.getObject();
	    context.put("nc.ui.uif2.I18nFB#12d71dd", product);
	    return (java.lang.String) product;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private List getManagedList11() {
	List list = new ArrayList();
	list.add(getPrintDirectAction());
	list.add(getPrintPreviewAction());
	list.add(getOutputAction());
	list.add(getSeparatorAction());
	list.add(getTemplatePrintAction());
	list.add(getTemplatePreviewAction());
	return list;
    }

    public nc.ui.hr.uif2.action.print.TemplatePreviewAction getTemplatePreviewAction() {
	if (context.get("TemplatePreviewAction") != null)
	    return (nc.ui.hr.uif2.action.print.TemplatePreviewAction) context.get("TemplatePreviewAction");
	nc.ui.hr.uif2.action.print.TemplatePreviewAction bean = new nc.ui.hr.uif2.action.print.TemplatePreviewAction();
	context.put("TemplatePreviewAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNodeKey(getNodekey());
	bean.setPrintDlgParentConatiner(getBillFormEditor());
	bean.setDatasource(getDatasource());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.hr.uif2.action.print.TemplatePrintAction getTemplatePrintAction() {
	if (context.get("TemplatePrintAction") != null)
	    return (nc.ui.hr.uif2.action.print.TemplatePrintAction) context.get("TemplatePrintAction");
	nc.ui.hr.uif2.action.print.TemplatePrintAction bean = new nc.ui.hr.uif2.action.print.TemplatePrintAction();
	context.put("TemplatePrintAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNodeKey(getNodekey());
	bean.setPrintDlgParentConatiner(getBillFormEditor());
	bean.setDatasource(getDatasource());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.pub.action.TAOutPutAction getOutputAction() {
	if (context.get("OutputAction") != null)
	    return (nc.ui.ta.pub.action.TAOutPutAction) context.get("OutputAction");
	nc.ui.ta.pub.action.TAOutPutAction bean = new nc.ui.ta.pub.action.TAOutPutAction();
	context.put("OutputAction", bean);
	bean.setModel(getManageAppModel());
	bean.setListView(getListView());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.GroupAction getCardPrintActiongroup() {
	if (context.get("CardPrintActiongroup") != null)
	    return (nc.funcnode.ui.action.GroupAction) context.get("CardPrintActiongroup");
	nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
	context.put("CardPrintActiongroup", bean);
	bean.setCode("cardprintgroup");
	bean.setName(getI18nFB_8e90e1());
	bean.setActions(getManagedList12());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private java.lang.String getI18nFB_8e90e1() {
	if (context.get("nc.ui.uif2.I18nFB#8e90e1") != null)
	    return (java.lang.String) context.get("nc.ui.uif2.I18nFB#8e90e1");
	nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
	context.put("&nc.ui.uif2.I18nFB#8e90e1", bean);
	bean.setResDir("common");
	bean.setDefaultValue("打印");
	bean.setResId("UC001-0000007");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	try {
	    Object product = bean.getObject();
	    context.put("nc.ui.uif2.I18nFB#8e90e1", product);
	    return (java.lang.String) product;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private List getManagedList12() {
	List list = new ArrayList();
	list.add(getTemplatePrintAction());
	list.add(getTemplatePreviewAction());
	list.add(getCardOutputAction());
	return list;
    }

    public nc.ui.ta.pub.action.TADirectPreviewAction getPrintPreviewAction() {
	if (context.get("printPreviewAction") != null)
	    return (nc.ui.ta.pub.action.TADirectPreviewAction) context.get("printPreviewAction");
	nc.ui.ta.pub.action.TADirectPreviewAction bean = new nc.ui.ta.pub.action.TADirectPreviewAction();
	context.put("printPreviewAction", bean);
	bean.setModel(getManageAppModel());
	bean.setListView(getListView());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.ta.pub.action.TADirectPrintAction getPrintDirectAction() {
	if (context.get("printDirectAction") != null)
	    return (nc.ui.ta.pub.action.TADirectPrintAction) context.get("printDirectAction");
	nc.ui.ta.pub.action.TADirectPrintAction bean = new nc.ui.ta.pub.action.TADirectPrintAction();
	context.put("printDirectAction", bean);
	bean.setModel(getManageAppModel());
	bean.setListView(getListView());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.hr.uif2.action.print.ExportCardAction getCardOutputAction() {
	if (context.get("cardOutputAction") != null)
	    return (nc.ui.hr.uif2.action.print.ExportCardAction) context.get("cardOutputAction");
	nc.ui.hr.uif2.action.print.ExportCardAction bean = new nc.ui.hr.uif2.action.print.ExportCardAction();
	context.put("cardOutputAction", bean);
	bean.setModel(getManageAppModel());
	bean.setNodeKey(getNodekey());
	bean.setPrintDlgParentConatiner(getBillFormEditor());
	bean.setDatasource(getDatasource());
	bean.setNcActionStatusJudge(getEnableJudge());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.SeparatorAction getSeparatorAction() {
	if (context.get("separatorAction") != null)
	    return (nc.funcnode.ui.action.SeparatorAction) context.get("separatorAction");
	nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
	context.put("separatorAction", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.components.pagination.PaginationBar getPaginationBar() {
	if (context.get("paginationBar") != null)
	    return (nc.ui.uif2.components.pagination.PaginationBar) context.get("paginationBar");
	nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
	context.put("paginationBar", bean);
	bean.setPaginationModel(getPaginationModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.components.pagination.PaginationModel getPaginationModel() {
	if (context.get("paginationModel") != null)
	    return (nc.ui.uif2.components.pagination.PaginationModel) context.get("paginationModel");
	nc.ui.uif2.components.pagination.PaginationModel bean = new nc.ui.uif2.components.pagination.PaginationModel();
	context.put("paginationModel", bean);
	bean.setPaginationQueryService(getManageModelService());
	bean.init();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

}
