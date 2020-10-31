package nc.ui.twhr.twhr_declaration.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Twhr_declaration_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.vo.uif2.LoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.uif2.LoginContext) context.get("context");
		nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.ace.serviceproxy.AceTwhr_declarationMaintainProxy getBmModelModelService() {
		if (context.get("bmModelModelService") != null)
			return (nc.ui.twhr.twhr_declaration.ace.serviceproxy.AceTwhr_declarationMaintainProxy) context
					.get("bmModelModelService");
		nc.ui.twhr.twhr_declaration.ace.serviceproxy.AceTwhr_declarationMaintainProxy bean = new nc.ui.twhr.twhr_declaration.ace.serviceproxy.AceTwhr_declarationMaintainProxy();
		context.put("bmModelModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.glb.view.OrgPanel_Org getPrimaryOrgPanel() {
		if (context.get("primaryOrgPanel") != null)
			return (nc.ui.twhr.glb.view.OrgPanel_Org) context.get("primaryOrgPanel");
		nc.ui.twhr.glb.view.OrgPanel_Org bean = new nc.ui.twhr.glb.view.OrgPanel_Org();
		context.put("primaryOrgPanel", bean);
		bean.setModel(getBmModel());
		bean.setDataManager(getBmModelModelDataManager());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.GeneralBDObjectAdapterFactory getBOAdapterFactory() {
		if (context.get("BOAdapterFactory") != null)
			return (nc.vo.bd.meta.GeneralBDObjectAdapterFactory) context.get("BOAdapterFactory");
		nc.vo.bd.meta.GeneralBDObjectAdapterFactory bean = new nc.vo.bd.meta.GeneralBDObjectAdapterFactory();
		context.put("BOAdapterFactory", bean);
		bean.setMode("MD");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BillManageModel getBmModel() {
		if (context.get("bmModel") != null)
			return (nc.ui.pubapp.uif2app.model.BillManageModel) context.get("bmModel");
		nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
		context.put("bmModel", bean);
		bean.setContext(getContext());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.model.ModelDataManager getBmModelModelDataManager() {
		if (context.get("bmModelModelDataManager") != null)
			return (nc.ui.pubapp.uif2app.query2.model.ModelDataManager) context.get("bmModelModelDataManager");
		nc.ui.pubapp.uif2app.query2.model.ModelDataManager bean = new nc.ui.pubapp.uif2app.query2.model.ModelDataManager();
		context.put("bmModelModelDataManager", bean);
		bean.setModel(getBmModel());
		bean.setService(getBmModelModelService());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.TemplateContainer getTemplateContainer() {
		if (context.get("templateContainer") != null)
			return (nc.ui.pubapp.uif2app.view.TemplateContainer) context.get("templateContainer");
		nc.ui.pubapp.uif2app.view.TemplateContainer bean = new nc.ui.pubapp.uif2app.view.TemplateContainer();
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
		list.add("bt");
		return list;
	}

	public nc.ui.twhr.twhr_declaration.ace.view.DeclarationListView getBillListView() {
		if (context.get("billListView") != null)
			return (nc.ui.twhr.twhr_declaration.ace.view.DeclarationListView) context.get("billListView");
		nc.ui.twhr.twhr_declaration.ace.view.DeclarationListView bean = new nc.ui.twhr.twhr_declaration.ace.view.DeclarationListView();
		context.put("billListView", bean);
		bean.setModel(getBmModel());
		bean.setNodekey("bt");
		bean.setMultiSelectionEnable(false);
		bean.setTemplateContainer(getTemplateContainer());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getViewb() {
		if (context.get("viewb") != null)
			return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel) context.get("viewb");
		nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
		context.put("viewb", bean);
		bean.setModel(getBmModel());
		bean.setTitleAction(getReturnAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnAction() {
		if (context.get("returnAction") != null)
			return (nc.ui.pubapp.uif2app.actions.UEReturnAction) context.get("returnAction");
		nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
		context.put("returnAction", bean);
		bean.setGoComponent(getBillListView());
		bean.setSaveAction(getSaveAction());
		bean.setModel(getBmModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.ace.view.DecBillForm getBillForm() {
		if (context.get("billForm") != null)
			return (nc.ui.twhr.twhr_declaration.ace.view.DecBillForm) context.get("billForm");
		nc.ui.twhr.twhr_declaration.ace.view.DecBillForm bean = new nc.ui.twhr.twhr_declaration.ace.view.DecBillForm();
		context.put("billForm", bean);
		bean.setModel(getBmModel());
		bean.setNodekey("bt");
		bean.setShowOrgPanel(true);
		bean.setBodyLineActions(getManagedList1());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getBodyAddLineAction_1d55f70());
		list.add(getBodyInsertLineAction_16cee80());
		list.add(getBodyDelLineAction_9e1b5c());
		return list;
	}

	private nc.ui.pubapp.uif2app.actions.BodyAddLineAction getBodyAddLineAction_1d55f70() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#1d55f70") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyAddLineAction) context
					.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#1d55f70");
		nc.ui.pubapp.uif2app.actions.BodyAddLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyAddLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#1d55f70", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyInsertLineAction getBodyInsertLineAction_16cee80() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#16cee80") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyInsertLineAction) context
					.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#16cee80");
		nc.ui.pubapp.uif2app.actions.BodyInsertLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyInsertLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#16cee80", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyDelLineAction getBodyDelLineAction_9e1b5c() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#9e1b5c") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyDelLineAction) context
					.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#9e1b5c");
		nc.ui.pubapp.uif2app.actions.BodyDelLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyDelLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#9e1b5c", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getTBNode_b8c505());
		bean.setActions(getManagedList3());
		bean.setEditActions(getManagedList4());
		bean.setModel(getBmModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_b8c505() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#b8c505") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#b8c505");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#b8c505", bean);
		bean.setName("cardLayout");
		bean.setShowMode("CardLayout");
		bean.setTabs(getManagedList2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getVSNode_caa631());
		list.add(getVSNode_13dd62f());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_caa631() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#caa631") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#caa631");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#caa631", bean);
		bean.setUp(getCNode_1643b1());
		bean.setDown(getCNode_1b6428a());
		bean.setDividerLocation(30.0f);
		bean.setName("列表");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1643b1() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1643b1") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1643b1");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1643b1", bean);
		bean.setComponent(getPrimaryOrgPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1b6428a() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1b6428a") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1b6428a");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1b6428a", bean);
		bean.setComponent(getBillListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_13dd62f() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#13dd62f") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#13dd62f");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#13dd62f", bean);
		bean.setUp(getCNode_64ebc8());
		bean.setDown(getCNode_1b3c4ce());
		bean.setDividerLocation(30.0f);
		bean.setName("卡片");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_64ebc8() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#64ebc8") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#64ebc8");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#64ebc8", bean);
		bean.setComponent(getViewb());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1b3c4ce() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1b3c4ce") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1b3c4ce");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1b3c4ce", bean);
		bean.setComponent(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getDefaultQueryAction());
		list.add(getSeparatorAction());
		list.add(getGeneratAction());
		list.add(getSeparatorAction());
		list.add(getDefaultExportAction());
		list.add(getDefaultRefreshAction());
		list.add(getSeparatorAction());
		list.add(getDefaultImportAction());
		list.add(getSeparatorAction());
		list.add(getCompanyAdjAction());
		return list;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getSaveAction());
		list.add(getCancelAction());
		list.add(getSeparatorAction());
		list.add(getImportCompanyAdjAction());
		return list;
	}

	public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener) context.get("InitDataListener");
		nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
		context.put("InitDataListener", bean);
		bean.setModel(getBmModel());
		bean.setContext(getContext());
		bean.setVoClassName("nc.vo.twhr.twhr_declaration.AggDeclarationVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.pubapp.common.validateservice.ClosingCheck) context.get("ClosingListener");
		nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
		context.put("ClosingListener", bean);
		bean.setModel(getBmModel());
		bean.setSaveAction(getSaveAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getBmModelEventMediator() {
		if (context.get("bmModelEventMediator") != null)
			return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context.get("bmModelEventMediator");
		nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
		context.put("bmModelEventMediator", bean);
		bean.setModel(getBmModel());
		bean.setHandlerGroup(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getEventHandlerGroup_2ba62e());
		list.add(getEventHandlerGroup_1007263());
		return list;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_2ba62e() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#2ba62e") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#2ba62e");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#2ba62e", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
		bean.setHandler(getAceOrgChangeHandler_8ed0c2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.twhr.twhr_declaration.ace.handler.AceOrgChangeHandler getAceOrgChangeHandler_8ed0c2() {
		if (context.get("nc.ui.twhr.twhr_declaration.ace.handler.AceOrgChangeHandler#8ed0c2") != null)
			return (nc.ui.twhr.twhr_declaration.ace.handler.AceOrgChangeHandler) context
					.get("nc.ui.twhr.twhr_declaration.ace.handler.AceOrgChangeHandler#8ed0c2");
		nc.ui.twhr.twhr_declaration.ace.handler.AceOrgChangeHandler bean = new nc.ui.twhr.twhr_declaration.ace.handler.AceOrgChangeHandler();
		context.put("nc.ui.twhr.twhr_declaration.ace.handler.AceOrgChangeHandler#8ed0c2", bean);
		bean.setBillForm(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1007263() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1007263") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1007263");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1007263", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.billform.AddEvent");
		bean.setHandler(getAceAddHandler_1ab53a4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.twhr.twhr_declaration.ace.handler.AceAddHandler getAceAddHandler_1ab53a4() {
		if (context.get("nc.ui.twhr.twhr_declaration.ace.handler.AceAddHandler#1ab53a4") != null)
			return (nc.ui.twhr.twhr_declaration.ace.handler.AceAddHandler) context
					.get("nc.ui.twhr.twhr_declaration.ace.handler.AceAddHandler#1ab53a4");
		nc.ui.twhr.twhr_declaration.ace.handler.AceAddHandler bean = new nc.ui.twhr.twhr_declaration.ace.handler.AceAddHandler();
		context.put("nc.ui.twhr.twhr_declaration.ace.handler.AceAddHandler#1ab53a4", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader getBillLazilyLoader() {
		if (context.get("billLazilyLoader") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader) context.get("billLazilyLoader");
		nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader bean = new nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader();
		context.put("billLazilyLoader", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager getBmModelLasilyLodadMediator() {
		if (context.get("bmModelLasilyLodadMediator") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager) context.get("bmModelLasilyLodadMediator");
		nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager bean = new nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager();
		context.put("bmModelLasilyLodadMediator", bean);
		bean.setModel(getBmModel());
		bean.setLoader(getBillLazilyLoader());
		bean.setLazilyLoadSupporter(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getCardPanelLazilyLoad_55f24f());
		list.add(getListPanelLazilyLoad_972bfd());
		return list;
	}

	private nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad getCardPanelLazilyLoad_55f24f() {
		if (context.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#55f24f") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad) context
					.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#55f24f");
		nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad();
		context.put("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#55f24f", bean);
		bean.setBillform(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad getListPanelLazilyLoad_972bfd() {
		if (context.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#972bfd") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad) context
					.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#972bfd");
		nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad();
		context.put("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#972bfd", bean);
		bean.setListView(getBillListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator() {
		if (context.get("rowNoMediator") != null)
			return (nc.ui.pubapp.uif2app.view.RowNoMediator) context.get("rowNoMediator");
		nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
		context.put("rowNoMediator", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator) context.get("mouseClickShowPanelMediator");
		nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setListView(getBillListView());
		bean.setShowUpComponent(getBillForm());
		bean.setHyperLinkColumn("billno");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.bill.BillCodeMediator getBillCodeMediator() {
		if (context.get("billCodeMediator") != null)
			return (nc.ui.pubapp.bill.BillCodeMediator) context.get("billCodeMediator");
		nc.ui.pubapp.bill.BillCodeMediator bean = new nc.ui.pubapp.bill.BillCodeMediator();
		context.put("billCodeMediator", bean);
		bean.setBillForm(getBillForm());
		bean.setBillCodeKey("billno");
		bean.setBillType("NHI1");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.AddAction getAddAction() {
		if (context.get("addAction") != null)
			return (nc.ui.pubapp.uif2app.actions.AddAction) context.get("addAction");
		nc.ui.pubapp.uif2app.actions.AddAction bean = new nc.ui.pubapp.uif2app.actions.AddAction();
		context.put("addAction", bean);
		bean.setModel(getBmModel());
		bean.setInterceptor(getCompositeActionInterceptor_100fbbc());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_100fbbc() {
		if (context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#100fbbc") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#100fbbc");
		nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
		context.put("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#100fbbc", bean);
		bean.setInterceptors(getManagedList7());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getShowUpComponentInterceptor_c922a3());
		return list;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_c922a3() {
		if (context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#c922a3") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#c922a3");
		nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
		context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#c922a3", bean);
		bean.setShowUpComponent(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.EditAction getEditAction() {
		if (context.get("editAction") != null)
			return (nc.ui.pubapp.uif2app.actions.EditAction) context.get("editAction");
		nc.ui.pubapp.uif2app.actions.EditAction bean = new nc.ui.pubapp.uif2app.actions.EditAction();
		context.put("editAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.ace.action.GeneratAction getGeneratAction() {
		if (context.get("generatAction") != null)
			return (nc.ui.twhr.twhr_declaration.ace.action.GeneratAction) context.get("generatAction");
		nc.ui.twhr.twhr_declaration.ace.action.GeneratAction bean = new nc.ui.twhr.twhr_declaration.ace.action.GeneratAction();
		context.put("generatAction", bean);
		bean.setModel(getBmModel());
		bean.setPrimaryOrgPanel(getPrimaryOrgPanel());
		bean.setBtnName("生成");
		bean.setBillForm(getBillForm());
		bean.setBillListView(getBillListView());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.CompanyAdjustAction getCompanyAdjAction() {
		if (context.get("companyAdjAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.CompanyAdjustAction) context.get("companyAdjAction");
		nc.ui.twhr.twhr_declaration.action.CompanyAdjustAction bean = new nc.ui.twhr.twhr_declaration.action.CompanyAdjustAction();
		context.put("companyAdjAction", bean);
		bean.setModel(getBmModel());
		bean.setPrimaryOrgPanel(getPrimaryOrgPanel());
		bean.setBtnName("公司補充保費調整");
		bean.setBillForm(getBillForm());
		bean.setBillListView(getBillListView());
		bean.setInterceptor(getCompositeActionInterceptor_e0dac4());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_e0dac4() {
		if (context.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#e0dac4") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#e0dac4");
		nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
		context.put("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#e0dac4", bean);
		bean.setInterceptors(getManagedList8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList8() {
		List list = new ArrayList();
		list.add(getShowUpComponentInterceptor_18efe69());
		return list;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_18efe69() {
		if (context.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#18efe69") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#18efe69");
		nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
		context.put("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#18efe69", bean);
		bean.setShowUpComponent(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.ImportCompanyAdjustAction getImportCompanyAdjAction() {
		if (context.get("importCompanyAdjAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.ImportCompanyAdjustAction) context.get("importCompanyAdjAction");
		nc.ui.twhr.twhr_declaration.action.ImportCompanyAdjustAction bean = new nc.ui.twhr.twhr_declaration.action.ImportCompanyAdjustAction();
		context.put("importCompanyAdjAction", bean);
		bean.setModel(getBmModel());
		bean.setPrimaryOrgPanel(getPrimaryOrgPanel());
		bean.setBtnName("導入調整數據");
		bean.setBillForm(getBillForm());
		bean.setBillListView(getBillListView());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction getDeleteScriptAction() {
		if (context.get("deleteScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction) context.get("deleteScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.DeleteScriptAction();
		context.put("deleteScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setBillType("NHI1");
		bean.setFilledUpInFlow(true);
		bean.setActionName("DELETE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.QueryTemplateContainer getDefaultQueryActionQueryTemplateContainer() {
		if (context.get("defaultQueryActionQueryTemplateContainer") != null)
			return (nc.ui.uif2.editor.QueryTemplateContainer) context.get("defaultQueryActionQueryTemplateContainer");
		nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
		context.put("defaultQueryActionQueryTemplateContainer", bean);
		bean.setNodeKey("qt");
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.DeclarationQueryAction getDefaultQueryAction() {
		if (context.get("defaultQueryAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.DeclarationQueryAction) context.get("defaultQueryAction");
		nc.ui.twhr.twhr_declaration.action.DeclarationQueryAction bean = new nc.ui.twhr.twhr_declaration.action.DeclarationQueryAction();
		context.put("defaultQueryAction", bean);
		bean.setModel(getBmModel());
		bean.setPrimaryOrgPanel(getPrimaryOrgPanel());
		bean.setTemplateContainer(getDefaultQueryActionQueryTemplateContainer());
		bean.setNodeKey("qt");
		bean.setDataManager(getBmModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		bean.setBtnName("查询");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.DefaultExportAction getDefaultExportAction() {
		if (context.get("defaultExportAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.DefaultExportAction) context.get("defaultExportAction");
		nc.ui.twhr.twhr_declaration.action.DefaultExportAction bean = new nc.ui.twhr.twhr_declaration.action.DefaultExportAction();
		context.put("defaultExportAction", bean);
		bean.setModel(getBmModel());
		bean.setDataManager(getBmModelModelDataManager());
		bean.setPrimaryOrgPanel(getPrimaryOrgPanel());
		bean.setBillForm(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		bean.setBtnName("二代健保申报");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.DecImportAction getDefaultImportAction() {
		if (context.get("defaultImportAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.DecImportAction) context.get("defaultImportAction");
		nc.ui.twhr.twhr_declaration.action.DecImportAction bean = new nc.ui.twhr.twhr_declaration.action.DecImportAction();
		context.put("defaultImportAction", bean);
		bean.setModel(getBmModel());
		bean.setPrimaryOrgPanel(getPrimaryOrgPanel());
		bean.setBillListView(getBillListView());
		bean.setBtnName("導入");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.CopyAction getCopyAction() {
		if (context.get("copyAction") != null)
			return (nc.ui.pubapp.uif2app.actions.CopyAction) context.get("copyAction");
		nc.ui.pubapp.uif2app.actions.CopyAction bean = new nc.ui.pubapp.uif2app.actions.CopyAction();
		context.put("copyAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getDefaultRefreshAction() {
		if (context.get("defaultRefreshAction") != null)
			return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction) context.get("defaultRefreshAction");
		nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
		context.put("defaultRefreshAction", bean);
		bean.setModel(getBmModel());
		bean.setDataManager(getBmModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction getCommitScriptAction() {
		if (context.get("commitScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction) context.get("commitScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.CommitScriptAction();
		context.put("commitScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("NHI1");
		bean.setFilledUpInFlow(true);
		bean.setActionName("SAVE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction getUnCommitScriptAction() {
		if (context.get("unCommitScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction) context.get("unCommitScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.UnCommitScriptAction();
		context.put("unCommitScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("NHI1");
		bean.setFilledUpInFlow(true);
		bean.setActionName("UNSAVEBILL");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction getApproveScriptAction() {
		if (context.get("approveScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction) context.get("approveScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.ApproveScriptAction();
		context.put("approveScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("NHI1");
		bean.setFilledUpInFlow(true);
		bean.setActionName("APPROVE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction getUNApproveScriptAction() {
		if (context.get("uNApproveScriptAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction) context.get("uNApproveScriptAction");
		nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction bean = new nc.ui.pubapp.uif2app.actions.pflow.UNApproveScriptAction();
		context.put("uNApproveScriptAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setBillType("NHI1");
		bean.setFilledUpInFlow(true);
		bean.setActionName("UNAPPROVE");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.LinkQueryAction getLinkQueryAction() {
		if (context.get("linkQueryAction") != null)
			return (nc.ui.pubapp.uif2app.actions.LinkQueryAction) context.get("linkQueryAction");
		nc.ui.pubapp.uif2app.actions.LinkQueryAction bean = new nc.ui.pubapp.uif2app.actions.LinkQueryAction();
		context.put("linkQueryAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintAction() {
		if (context.get("metaDataBasedPrintAction") != null)
			return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction) context.get("metaDataBasedPrintAction");
		nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
		context.put("metaDataBasedPrintAction", bean);
		bean.setModel(getBmModel());
		bean.setActioncode("Preview");
		bean.setActionname("预览");
		bean.setPreview(true);
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		bean.setBtnName("预览");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintActiona() {
		if (context.get("metaDataBasedPrintActiona") != null)
			return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction) context.get("metaDataBasedPrintActiona");
		nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
		context.put("metaDataBasedPrintActiona", bean);
		bean.setModel(getBmModel());
		bean.setActioncode("Print");
		bean.setActionname("打印");
		bean.setPreview(false);
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		bean.setBtnName("打印");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.pubapp.uif2app.actions.OutputAction) context.get("outputAction");
		nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
		context.put("outputAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction getPFApproveStatusInfoAction() {
		if (context.get("pFApproveStatusInfoAction") != null)
			return (nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction) context
					.get("pFApproveStatusInfoAction");
		nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction bean = new nc.ui.pubapp.uif2app.actions.pflow.PFApproveStatusInfoAction();
		context.put("pFApproveStatusInfoAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.SaveAction getSaveAction() {
		if (context.get("saveAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.SaveAction) context.get("saveAction");
		nc.ui.twhr.twhr_declaration.action.SaveAction bean = new nc.ui.twhr.twhr_declaration.action.SaveAction();
		context.put("saveAction", bean);
		bean.setModel(getBmModel());
		bean.setBillForm(getBillForm());
		bean.setBillListView(getBillListView());
		bean.setDataManager(getBmModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.CancelAction getCancelAction() {
		if (context.get("cancelAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.CancelAction) context.get("cancelAction");
		nc.ui.twhr.twhr_declaration.action.CancelAction bean = new nc.ui.twhr.twhr_declaration.action.CancelAction();
		context.put("cancelAction", bean);
		bean.setModel(getBmModel());
		bean.setBillForm(getBillForm());
		bean.setBillListView(getBillListView());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.AddLine getAddLineAction() {
		if (context.get("addLineAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.AddLine) context.get("addLineAction");
		nc.ui.twhr.twhr_declaration.action.AddLine bean = new nc.ui.twhr.twhr_declaration.action.AddLine();
		context.put("addLineAction", bean);
		bean.setModel(getBmModel());
		bean.setBillForm(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.twhr_declaration.action.DelLine getDelLineAction() {
		if (context.get("delLineAction") != null)
			return (nc.ui.twhr.twhr_declaration.action.DelLine) context.get("delLineAction");
		nc.ui.twhr.twhr_declaration.action.DelLine bean = new nc.ui.twhr.twhr_declaration.action.DelLine();
		context.put("delLineAction", bean);
		bean.setModel(getBmModel());
		bean.setBillForm(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
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

	public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler() {
		if (context.get("exceptionHandler") != null)
			return (nc.ui.uif2.DefaultExceptionHanler) context.get("exceptionHandler");
		nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler(getContainer());
		context.put("exceptionHandler", bean);
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
